# Memory Leaks

## Symptoms
- Increasing heap usage over time
- Frequent Full GC
- OutOfMemoryError
- Pod restarts in Kubernetes
- Application slows before crash

## Common Root Causes
- Static references to large objects
- Unbounded caches
- Listener or thread leaks
- Improper use of ThreadLocal
- Missing resource cleanup

## Indicators
- Heap dump showing retained objects
- GC logs showing long pauses
- Steady RSS growth

## Recommended Actions
- Capture heap dump and analyze with MAT
- Review caches for eviction policies
- Remove unused listeners
- Ensure proper resource cleanup
- Limit ThreadLocal usage

## Prevention
- Enable heap monitoring
- Enforce cache limits
- Run memory profiling in non-prod
