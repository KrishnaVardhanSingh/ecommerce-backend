package krishna.ecommerce.configuration;

import krishna.ecommerce.security.JwtAuthenticationFilter;
import krishna.ecommerce.security.RestAccessDeniedHandler;
import krishna.ecommerce.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        httpSecurity
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // securing the ADMIN access
                                .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/inventory/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/inventory/**").hasRole("ADMIN")

                        // any other can be accessed only by the authenticated user

                                   // no need of these 4 because it will be covered in the .anyRequest()
//                                .requestMatchers(HttpMethod.GET, "/api/cart/**").authenticated()
//                                .requestMatchers(HttpMethod.POST, "/api/cart/**").authenticated()
//                                .requestMatchers(HttpMethod.PUT, "/api/cart/**").authenticated()
//                                .requestMatchers(HttpMethod.DELETE, "/api/cart/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/order/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/order/**").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/order/**").authenticated()

                                .anyRequest().authenticated()
                        )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                                .accessDeniedHandler(new RestAccessDeniedHandler())
                        );

                // no need of these two because we are building jwt login with email + pass
                //                .httpBasic(httpBasic -> {})
                //                .formLogin(formLogin -> {});



        return httpSecurity.build();
    }
}

//Products → public GET, ADMIN mutation
//Inventory → ADMIN
//Cart → authenticated
//Orders → authenticated
//Everything else → authenticated


