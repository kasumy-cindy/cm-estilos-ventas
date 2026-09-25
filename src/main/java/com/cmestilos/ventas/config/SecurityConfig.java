package com.cmestilos.ventas.config;

import com.cmestilos.ventas.security.JwtAuthFilter;
import com.cmestilos.ventas.security.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            UsuarioDetailsService usuarioDetailsService) {

        this.jwtAuthFilter = jwtAuthFilter;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .userDetailsService(usuarioDetailsService)

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/",
                        "/*.html",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/api/auth/**",
                        "/error",
                        "/favicon.ico"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/catalogo/**"
                ).permitAll()

                .requestMatchers(
                        "/api/tienda/**"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/ventas/online"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/categorias/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "CAJERO"
                )

                .requestMatchers(
                        "/api/usuarios/**"
                ).hasRole("ADMINISTRADOR")

                .requestMatchers(
                        "/api/reportes/**",
                        "/api/proveedores/**",
                        "/api/pedidos-proveedor/**"
                ).hasRole("ADMINISTRADOR")

                .requestMatchers(
                        "/api/metodos-pago/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "CAJERO"
                )

                .requestMatchers(
                        "/api/productos/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "CAJERO"
                )

                .requestMatchers(
                        "/api/inventario/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "CAJERO"
                )

                .requestMatchers(
                        "/api/clientes/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "CAJERO"
                )

                .requestMatchers(
                        "/api/ventas/**"
                ).hasAnyRole(
                        "ADMINISTRADOR",
                        "CAJERO"
                )

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                    jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}