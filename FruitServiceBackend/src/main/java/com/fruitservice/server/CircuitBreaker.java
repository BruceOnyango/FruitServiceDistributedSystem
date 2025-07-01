import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CircuitBreaker {
    
    public enum State {
        CLOSED,    // Normal operation
        OPEN,      // Failing fast
        HALF_OPEN  // Testing if service recovered
    }
    
    private final int failureThreshold;
    private final long timeoutMillis;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private volatile State state = State.CLOSED;
    
    public CircuitBreaker(int failureThreshold, long timeoutMillis) {
        this.failureThreshold = failureThreshold;
        this.timeoutMillis = timeoutMillis;
    }
    
    public <T> T execute(CircuitBreakerTask<T> task) throws Exception {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime.get() > timeoutMillis) {
                state = State.HALF_OPEN;
                System.out.println("Circuit breaker moving to HALF_OPEN state");
            } else {
                throw new RuntimeException("Circuit breaker is OPEN - failing fast");
            }
        }
        
        try {
            T result = task.execute();
            onSuccess();
            return result;
            
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
    
    private void onSuccess() {
        failureCount.set(0);
        state = State.CLOSED;
        System.out.println("Circuit breaker: Success - state is CLOSED");
    }
    
    private void onFailure() {
        int failures = failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        
        if (failures >= failureThreshold) {
            state = State.OPEN;
            System.out.println("Circuit breaker: Too many failures (" + failures + ") - state is OPEN");
        }
    }
    
    public State getState() {
        return state;
    }
    
    public int getFailureCount() {
        return failureCount.get();
    }
    
    @FunctionalInterface
    public interface CircuitBreakerTask<T> {
        T execute() throws Exception;
    }
}
