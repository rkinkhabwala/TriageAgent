# Timeouts

## Symptoms
- java.net.SocketTimeoutException
- Read timed out
- Connection timed out
- HTTP 504 Gateway Timeout
- Slow response times under load

## Common Root Causes
- Downstream service latency spike
- Database connection pool exhaustion (HikariCP)
- Thread pool starvation (Tomcat / WebClient)
- Network instability between services
- Missing circuit breakers or retries

## Indicators
- Increased latency in APM dashboards
- High DB connection utilization
- Thread pools at or near max
- Retry storms amplifying load

## Recommended Actions
- Check downstream service latency dashboards
- Inspect HikariCP pool metrics:
    - `hikaricp.connections.active`
    - `hikaricp.connections.pending`
- Temporarily increase pool size if safe
- Enable circuit breaker or retry with backoff
- Add alerting on connection pool saturation

## Prevention
- Enforce timeouts on all outbound calls
- Use bulkheads and circuit breakers
- Load test connection pools
- Monitor saturation metrics proactively
