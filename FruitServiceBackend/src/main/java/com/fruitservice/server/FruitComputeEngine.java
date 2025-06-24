import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DistributedFruitComputeEngine extends UnicastRemoteObject implements Compute, HealthCheck {
    
    private static final Map<String, FruitPrice> fruitPriceTable = new ConcurrentHashMap<>();
    private final String serverId;
    private final int serverPort;
    private final List<String> replicaServers;
    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong lastHeartbeat = new AtomicLong(System.currentTimeMillis());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private volatile boolean isHealthy = true;
    
    public DistributedFruitComputeEngine(String serverId, int serverPort, List<String> replicas) throws RemoteException {
        super();
        this.serverId = serverId;
        this.serverPort = serverPort;
        this.replicaServers = replicas != null ? replicas : new ArrayList<>();
        
        // Initialize with sample data
        initializeSampleData();
        
        // Start health monitoring
        startHealthMonitoring();
        
        System.out.println("Server " + serverId + " started on port " + serverPort);
        System.out.println("Replicas: " + replicaServers);
    }
    
    private void initializeSampleData() {
        fruitPriceTable.put("apple", new FruitPrice("apple", 2.50));
        fruitPriceTable.put("banana", new FruitPrice("banana", 1.20));
        fruitPriceTable.put("orange", new FruitPrice("orange", 3.00));
        fruitPriceTable.put("grape", new FruitPrice("grape", 4.00));
    }
    
    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        requestCount.incrementAndGet();
        lastHeartbeat.set(System.currentTimeMillis());
        
        if (!isHealthy) {
            throw new RemoteException("Server " + serverId + " is not healthy");
        }
        
        System.out.println("Server " + serverId + " executing task: " + t.getClass().getSimpleName());
        
        try {
            T result = t.execute();
            
            // Replicate write operations to backup servers
            if (isWriteOperation(t)) {
                replicateToBackups(t);
            }
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Task execution failed on server " + serverId + ": " + e.getMessage());
            throw new RemoteException("Task execution failed", e);
        }
    }
    
    @Override
    public boolean isHealthy() throws RemoteException {
        return isHealthy && (System.currentTimeMillis() - lastHeartbeat.get()) < 30000; // 30 seconds
    }
    
    @Override
    public ServerStats getServerStats() throws RemoteException {
        return new ServerStats(
            serverId,
            requestCount.get(),
            System.currentTimeMillis() - lastHeartbeat.get(),
            fruitPriceTable.size(),
            isHealthy
        );
    }
    
    @Override
    public String getServerId() throws RemoteException {
        return serverId;
    }
    
    public static Map<String, FruitPrice> getFruitPriceTable() {
        return fruitPriceTable;
    }
    
    private boolean isWriteOperation(Task<?> task) {
        return task instanceof AddFruitPrice || 
               task instanceof UpdateFruitPrice || 
               task instanceof DeleteFruitPrice;
    }
    
    private void replicateToBackups(Task<?> task) {
        // Asynchronous replication to avoid blocking main request
        scheduler.execute(() -> {
            for (String replicaServer : replicaServers) {
                try {
                    String[] parts = replicaServer.split(":");
                    String host = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    
                    Registry replicaRegistry = LocateRegistry.getRegistry(host, port);
                    Compute replicaComp = (Compute) replicaRegistry.lookup("FruitComputeEngine");
                    
                    // Execute same task on replica (eventual consistency)
                    replicaComp.executeTask(task);
                    System.out.println("Replicated to " + replicaServer);
                    
                } catch (Exception e) {
                    System.err.println("Failed to replicate to " + replicaServer + ": " + e.getMessage());
                    // Could implement retry logic or mark replica as down
                }
            }
        });
    }
    
    private void startHealthMonitoring() {
        // Periodic health check
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Simulate health check logic
                // In real system: check database connections, memory usage, etc.
                double memoryUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) 
                                   / (double) Runtime.getRuntime().maxMemory();
                
                if (memoryUsage > 0.9) { // 90% memory usage
                    isHealthy = false;
                    System.err.println("Server " + serverId + " marked unhealthy: High memory usage");
                } else {
                    isHealthy = true;
                }
                
                lastHeartbeat.set(System.currentTimeMillis());
                
            } catch (Exception e) {
                isHealthy = false;
                System.err.println("Health check failed for server " + serverId + ": " + e.getMessage());
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java DistributedFruitComputeEngine <serverId> <registryPort> [replica1:port] [replica2:port]...");
            System.exit(1);
        }
        
        try {
            String serverId = args[0];
            int registryPort = Integer.parseInt(args[1]);
            
            List<String> replicas = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                replicas.add(args[i]);
            }
            
            DistributedFruitComputeEngine engine = new DistributedFruitComputeEngine(serverId, registryPort, replicas);
            
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(registryPort);
                System.out.println("Created RMI registry on port " + registryPort);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(registryPort);
                System.out.println("Connected to existing registry on port " + registryPort);
            }
            
            registry.rebind("FruitComputeEngine", engine);
            
            System.out.println("DistributedFruitComputeEngine (" + serverId + ") bound and ready on port " + registryPort);
            
            // Keep server running
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
            
        } catch (Exception e) {
            System.err.println("DistributedFruitComputeEngine startup failed:");
            e.printStackTrace();
        }
    }
}
