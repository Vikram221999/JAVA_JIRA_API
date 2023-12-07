package com.example.demo.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.cors().and().csrf().disable();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    

}





/*
 * package com.example.demo.config;
 * 
 * import org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.security.config.annotation.web.builders.HttpSecurity;
 * import org.springframework.security.config.annotation.web.configuration.
 * EnableWebSecurity; import org.springframework.security.core.userdetails.User;
 * import org.springframework.security.core.userdetails.UserDetails; import
 * org.springframework.security.core.userdetails.UserDetailsService; import
 * org.springframework.security.crypto.password.NoOpPasswordEncoder; import
 * org.springframework.security.crypto.password.PasswordEncoder; import
 * org.springframework.security.provisioning.InMemoryUserDetailsManager; import
 * org.springframework.security.web.SecurityFilterChain;
 * 
 * @Configuration
 * 
 * @EnableWebSecurity
 * 
 * public class SpringSecurityConfig {
 * 
 * @Bean public UserDetailsService userDetailsService() { UserDetails
 * user=User.withDefaultPasswordEncoder() .username("admin") .password("root")
 * .roles("USER") .build(); return new InMemoryUserDetailsManager(user); }
 * 
 * @Bean public SecurityFilterChain filterChain(HttpSecurity http)throws
 * Exception{ http.authorizeHttpRequests((authz)->authz .antMatchers("/jira")
 * //.anyRequest() .authenticated() ) .httpBasic(); return http.build(); }
 * 
 * @Bean public PasswordEncoder passwordEncoder() { // This is just for
 * demonstration purposes; in a real application, use a secure password encoder.
 * return NoOpPasswordEncoder.getInstance(); }
 * 
 * 
 * }
 */