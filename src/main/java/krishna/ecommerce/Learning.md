### JetFilterChain
                 HTTP REQUEST
                      │
                      ▼
             Authorization header
                      │
                      ▼
                Bearer token
                      │
                      ▼
            ┌─────────────────┐
            │    JwtService   │
            │                 │
            │ Validate token  │
            │ Extract subject │
            └────────┬────────┘
                     │
                     ▼
                  username
                     │
                     ▼
             UserDetailsService
                     │
                     ▼
                UserDetails
                     │
                     ▼
              validateToken()
                     │
                  VALID?
                     │
                     ▼
          UsernamePasswordAuthenticationToken
                     │
                     ▼
              SecurityContext
                     │
                     ▼
             Spring Security
                     │
                     ▼
                Controller

