package es.upm.dit.isst.splitit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        // Crea un usuario con username "alvarocolin" y password "123" y rol USER
        UserDetails user = User.withUsername("alvarocolin")
                .password("123")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        // No se recomienda en producción, pero para pruebas podemos usar NoOpPasswordEncoder
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")               // Página de login personalizada
                .defaultSuccessUrl("/dashboard", true) // URL de redirección tras login
                .permitAll()
            )
            .logout(logout -> logout.permitAll());
        return http.build();
    }
}
