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
public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/iniciar-sesion", "/crear-cuenta").permitAll()
            .requestMatchers("/css/**", "/js/**", "/img/**", "/fonts/**", "/uploads/**").permitAll()
            .requestMatchers("/grupos", "/pagos", "/usuarios").hasRole("ADMIN")
            .anyRequest().authenticated())
        .oauth2Login(oauth -> oauth
            .loginPage("/iniciar-sesion")
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService)
            )
            .defaultSuccessUrl("/mi-espacio", true)
        )
        .formLogin(form -> form
            .loginPage("/iniciar-sesion")
            .defaultSuccessUrl("/mi-espacio", true)
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .logoutSuccessUrl("/iniciar-sesion")
            .permitAll());

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
