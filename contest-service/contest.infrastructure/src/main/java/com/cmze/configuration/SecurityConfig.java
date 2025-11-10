package com.cmze.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMethodSecurity // Włącza @PreAuthorize (np. aby tylko Admin mógł tworzyć szablony)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Ten serwis NIE POTRZEBUJE 'PasswordEncoder' ani 'AuthenticationManager',
    // ponieważ sam nie loguje - tylko waliduje tokeny.

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // TODO: Ustaw swoje publiczne endpointy dla konkursów
                        // np. .requestMatchers(HttpMethod.GET, "/contests/public").permitAll()

                        // Zezwól na dostęp do Swaggera (jeśli go tu dodasz)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Reszta endpointów (np. POST /contests) wymaga autoryzacji
                        .anyRequest().authenticated()
                );

        // Dodajemy nasz filtr, który odczyta token i ustawi użytkownika w kontekście
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
