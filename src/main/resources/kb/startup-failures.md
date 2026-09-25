# Application Startup Failures

## Symptoms
- Application fails to start
- BeanCreationException
- Port already in use
- Configuration binding errors

## Common Root Causes
- Missing or invalid environment variables
- Port conflicts
- Incorrect Spring profiles
- Database connectivity issues at startup

## Indicators
- Errors in startup logs
- CrashLoopBackOff in Kubernetes
- Failing health checks

## Recommended Actions
- Validate environment variables
- Check active Spring profiles
- Verify DB connectivity
- Review startup logs carefully

## Prevention
- Validate configs at build time
- Use config validation
- Add startup health checks
