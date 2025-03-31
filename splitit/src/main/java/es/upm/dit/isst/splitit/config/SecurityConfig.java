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
    UserDetails user1 = User.withUsername("alvaro@example.com")
            .password("123")
            .roles("USER")
            .build();
    UserDetails user2 = User.withUsername("roberto@example.com")
            .password("123")
            .roles("USER")
            .build();
    UserDetails user3 = User.withUsername("alejo@example.com")
            .password("123")
            .roles("USER")
            .build();
    UserDetails user4 = User.withUsername("luis@example.com")
            .password("123")
            .roles("USER")
            .build();
    return new InMemoryUserDetailsManager(user1, user2, user3, user4);
}


    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        //  para pruebas podemos usar NoOpPasswordEncoder
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                                .requestMatchers("/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
                                .requestMatchers("/dashboard").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                                .loginPage("/login")               // Página de login personalizada
                                .defaultSuccessUrl("/dashboard", true) // URL de redirección tras login
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }
}
