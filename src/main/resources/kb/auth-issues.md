# Authentication & Authorization Issues

## Symptoms
- HTTP 401 Unauthorized
- HTTP 403 Forbidden
- JWT validation errors
- Token expired exceptions
- Users intermittently logged out

## Common Root Causes
- Expired or invalid JWT tokens
- Clock skew between services
- Incorrect signing keys
- Missing roles/authorities
- Misconfigured gateway filters

## Indicators
- Auth failures after deployments
- Errors clustered around token expiration
- Increased login attempts

## Recommended Actions
- Verify token expiration and refresh logic
- Check clock synchronization (NTP)
- Validate signing keys and rotation
- Inspect gateway and security filters
- Review role mappings

## Prevention
- Use short-lived access tokens with refresh tokens
- Centralize authentication logic
- Monitor auth failure rates
