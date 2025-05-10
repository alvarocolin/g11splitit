package es.upm.dit.isst.splitit.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/crear-cuenta", "/crear-cuenta/confirmar").permitAll()
            .requestMatchers("/css/**", "/js/**", "/img/**", "/fonts/**", "/uploads/**").permitAll()
            .requestMatchers("/h2/**").permitAll() // permitir acceso a consola H2
            .requestMatchers("/grupos", "/pagos", "/usuarios").hasRole("ADMIN")
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/iniciar-sesion")
            .defaultSuccessUrl("/mi-espacio", true)
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/iniciar-sesion?logout")
            .permitAll());

    // Solo para H2: desactivar CSRF y frameOptions
    http.csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2/**") // solo desactivar CSRF en /h2/**
    );
    http.headers(headers -> headers
            .frameOptions(frame -> frame
                .disable()) // permitir frames (necesario para consola H2)
    );

    return http.build();
}


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("SELECT email, password, true as enabled FROM usuarios WHERE email = ?");
        users.setAuthoritiesByUsernameQuery("SELECT email, auth FROM usuarios WHERE email = ?");
        return users;
    }
}
