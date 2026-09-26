One thing I want us to preserve throughout

At the end of each major block, we'll capture four things:

1. What we built

Actual classes, endpoints, configuration and architecture.

2. What you learned

For example, from this block:

Authentication vs Authorization
JWT vs UserDetails vs Authentication
SecurityContext
OncePerRequestFilter
Spring Security filter chain
401 vs 403
AuthenticationEntryPoint
Lambda/type inference in Spring Security DSL
3. Mistakes and debugging lessons

For example:

validateToken(username, userDetails)
↓
wrong argument

return inside JWT catch
↓
stopped filter chain

403 vs 401 confusion
↓
missing AuthenticationEntryPoint

Role changed manually in DB
↓
revealed missing admin-provisioning mechanism

These are valuable because they demonstrate engineering understanding, not just that the final code works.

4. Production considerations

For example:

Development admin provisioning
↓
needs deliberate production strategy

JWT secret
↓
externalized configuration

Refresh token
↓
needs revocation/rotation strategy

That accumulated document will eventually become a very useful project-development/architecture study note and can also feed directly into the technical explanation of the project on your resume/interviews.