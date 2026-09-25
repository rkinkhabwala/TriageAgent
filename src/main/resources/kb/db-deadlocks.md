# Database Deadlocks

## Symptoms
- Deadlock detected
- Transaction rolled back
- SQLState: 40001
- Increased transaction retries
- Sporadic failures under concurrency

## Common Root Causes
- Long-running transactions
- Inconsistent row locking order
- Missing or inefficient indexes
- High write contention on shared tables

## Indicators
- Deadlock graphs in database logs
- Increased rollback counts
- Spikes during peak traffic

## Recommended Actions
- Identify conflicting transactions
- Reduce transaction scope and duration
- Enforce consistent locking order
- Add missing indexes
- Implement retry logic with jitter

## Prevention
- Keep transactions short
- Avoid unnecessary locks
- Use optimistic locking where possible
- Load test concurrency scenarios
