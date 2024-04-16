package com.G2T5203.wingit.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.key}")
    private String jwtKey;

    @Bean
    @Order(1)
    SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll();
                })
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
                .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable())
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth
                            // new AntPathRequestMatcher() allows you to set additional properties on 'AntPathRequestMatcher' instance during instantiation
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/error")).permitAll()
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/token")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/adminToken")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/checkJwt")).hasRole("ADMIN")

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/adminUtils/forceCancelNonInitBookings")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/adminUtils/resetBookingsAndUsers")).hasRole("ADMIN")

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/routes/new")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/seats/*")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/routeListings/new")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/seatListings/new")).hasRole("ADMIN")

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/users/new")).permitAll()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/users/newAdmin")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/users")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/users/adminAuthTest")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/users/authTest")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/users/*")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/users/update/*")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/users/updatePass/*")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/users/delete/*")).authenticated()

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/planes")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/planes/new")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/planes/newWithSeats")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/planes/*")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/planes/delete/*")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/planes/update/*")).hasRole("ADMIN")

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/routes")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/routes/*")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/routes/departureDest/*")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/routes/new")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/routes/delete/*")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/routes/update/*")).hasRole("ADMIN")


                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/routeListings/fullSearch/**")).permitAll()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/routeListings")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/routeListings/new")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/routeListings/delete/*")).hasRole("ADMIN")

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/bookings")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/bookings/**")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/bookings/**")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/bookings/**")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/bookings/**")).authenticated()

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/seatListings")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/seatListings/**")).authenticated()
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/seatListings/**")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/seatListings/**")).hasRole("ADMIN")
                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/seatListings/**")).authenticated()

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/rest/api/generate-calendar/*")).authenticated()

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/*")).permitAll()

                            .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/users/authTest/*")).authenticated()
                            .anyRequest().hasAuthority("SCOPE_READ");
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter())
                        )
                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey.getBytes()));
    }

    @Bean
    public JwtAuthenticationConverter customJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] bytes = jwtKey.getBytes();
        SecretKeySpec originalKey = new SecretKeySpec(bytes, 0, bytes.length,"RSA");
        return NimbusJwtDecoder.withSecretKey(originalKey).macAlgorithm(MacAlgorithm.HS512).build();
    }

    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());

        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder encoder() { return new BCryptPasswordEncoder(); }
}
