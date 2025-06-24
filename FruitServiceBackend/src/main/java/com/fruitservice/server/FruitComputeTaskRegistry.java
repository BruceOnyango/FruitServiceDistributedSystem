import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadBalancedFruitTaskRegistry {
    
    private static final List<ServerNode> serverNodes = new ArrayList<>();
    private static final AtomicInteger currentIndex = new AtomicInteger(0);
    private static final Map<String, Long> serverFailureTime = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService healthChecker = Executors.newScheduledThreadPool(2);
    private static final long FAILURE_TIMEOUT = 30000; // 30 seconds
    
    static {
        // Initialize server nodes
        initializeServers();
        
        // Start health checking
        startHealthChecking();
    }
    
    private static void initializeServers() {
        // Add multiple server nodes for load balancing
        serverNodes.add(new ServerNode("server1", "localhost", 1099, 1.0));
        serverNodes.add(new ServerNode("server2", "localhost", 1100, 1.0));
        serverNodes.add(new ServerNode("server3", "localhost", 1101, 1.0));
        
        System.out.println("Initialized " + serverNodes.size() + " server nodes");
    }
    
    public static <T> T executeTask(Task<T> task) {
        return executeTaskWithRetry(task, 3); // Retry up to 3 times
    }
    
    public static <T> T executeTaskWithRetry(Task<T> task, int maxRetries) {
        Exception lastException = null;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            ServerNode server = selectHealthyServer();
            
            if (server == null) {
                throw new RuntimeException("No healthy servers available");
            }
            
            try {
                System.out.println("Attempt " + (attempt + 1) + ": Executing task on " + server.getId());
                
                Registry registry = LocateRegistry.getRegistry(server.getHost(), server.getPort());
                Compute comp = (Compute) registry.lookup("FruitComputeEngine");
                
                // Execute task
                T result = comp.executeTask(task);
                
                // Mark server as healthy on successful execution
                markServerHealthy(server);
                
                return result;
                
            } catch (Exception e) {
                lastException = e;
                System.err.println("Server " + server.getId() + " failed: " + e.getMessage());
                
                // Mark server as unhealthy
                markServerUnhealthy(server);
                
                // If this was the last attempt, don't wait
                if (attempt < maxRetries - 1) {
                    try {
                        Thread.sleep(1000 * (attempt + 1)); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        throw new RuntimeException("All retry attempts failed. Last error: " + 
                                 (lastException != null ? lastException.getMessage() : "Unknown"));
    }
    
    private static ServerNode selectHealthyServer() {
        List<ServerNode> healthyServers = getHealthyServers();
        
        if (healthyServers.isEmpty()) {
            return null;
        }
        
        // Weighted round-robin load balancing
        return selectServerByWeight(healthyServers);
    }
    
    private static List<ServerNode> getHealthyServers() {
        List<ServerNode> healthy = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        
        for (ServerNode server : serverNodes) {
            Long failureTime = serverFailureTime.get(server.getId());
            
            // Server is healthy if:
            // 1. Never failed, OR
            // 2. Failed but timeout period has passed
            if (failureTime == null || (currentTime - failureTime) > FAILURE_TIMEOUT) {
                healthy.add(server);
            }
        }
        
        return healthy;
    }
    
    private static ServerNode selectServerByWeight(List<ServerNode> servers) {
        // Calculate total weight
        double totalWeight = servers.stream().mapToDouble(ServerNode::getWeight).sum();
        
        // Simple round-robin for now (can be enhanced with actual weights)
        int index = currentIndex.getAndIncrement() % servers.size();
        return servers.get(index);
    }
    
    private static void markServerHealthy(ServerNode server) {
        serverFailureTime.remove(server.getId());
        System.out.println("Server " + server.getId() + " marked as healthy");
    }
    
    private static void markServerUnhealthy(ServerNode server) {
        serverFailureTime.put(server.getId(), System.currentTimeMillis());
        System.out.println("Server " + server.getId() + " marked as unhealthy");
    }
    
    private static void startHealthChecking() {
        healthChecker.scheduleAtFixedRate(() -> {
            System.out.println("=== Health Check Report ===");
            
            for (ServerNode server : serverNodes) {
                try {
                    Registry registry = LocateRegistry.getRegistry(server.getHost(), server.getPort());
                    HealthCheck healthCheck = (HealthCheck) registry.lookup("FruitComputeEngine");
                    
                    if (healthCheck.isHealthy()) {
                        markServerHealthy(server);
                        ServerStats stats = healthCheck.getServerStats();
                        System.out.println("✓ " + server.getId() + ": " + stats);
                    } else {
                        markServerUnhealthy(server);
                        System.out.println("✗ " + server.getId() + ": Unhealthy");
                    }
                    
                } catch (Exception e) {
                    markServerUnhealthy(server);
                    System.out.println("✗ " + server.getId() + ": " + e.getMessage());
                }
            }
            
            System.out.println("Healthy servers: " + getHealthyServers().size() + "/" + serverNodes.size());
            System.out.println("========================");
            
        }, 15, 15, TimeUnit.SECONDS); // Check every 15 seconds
    }
    
    public static void printServerStatus() {
        System.out.println("\n=== Current Server Status ===");
        List<ServerNode> healthy = getHealthyServers();
        
        for (ServerNode server : serverNodes) {
            String status = healthy.contains(server) ? "HEALTHY" : "UNHEALTHY";
            Long failureTime = serverFailureTime.get(server.getId());
            String failureInfo = failureTime != null ? 
                " (failed " + (System.currentTimeMillis() - failureTime) + "ms ago)" : "";
            
            System.out.println(server.getId() + " (" + server.getHost() + ":" + server.getPort() + "): " + 
                             status + failureInfo);
        }
        System.out.println("============================\n");
    }
    
    // Static class for server node information
    private static class ServerNode {
        private final String id;
        private final String host;
        private final int port;
        private final double weight;
        
        public ServerNode(String id, String host, int port, double weight) {
            this.id = id;
            this.host = host;
            this.port = port;
            this.weight = weight;
        }
        
        public String getId() { return id; }
        public String getHost() { return host; }
        public int getPort() { return port; }
        public double getWeight() { return weight; }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ServerNode that = (ServerNode) obj;
            return Objects.equals(id, that.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
