# CPU Spikes

## Symptoms
- High CPU utilization
- Slow response times
- Increased latency
- Pod throttling in Kubernetes

## Common Root Causes
- Infinite loops or busy-waiting
- Excessive JSON serialization
- Inefficient algorithms
- Thread contention
- Misconfigured autoscaling

## Indicators
- CPU usage charts show sudden spikes
- Thread dumps with runnable threads
- High GC CPU time

## Recommended Actions
- Capture thread dump during spike
- Identify hot methods
- Optimize CPU-intensive code paths
- Adjust autoscaling thresholds
- Add caching where appropriate

## Prevention
- Performance testing
- CPU profiling
- Capacity planning
