package com.example.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class SpringSecurityConfig {
	
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user=User.withDefaultPasswordEncoder()
							.username("admin")
							.password("root")
							.roles("USER")
							.build();
		return new InMemoryUserDetailsManager(user);
	}
	
	@Bean	
	public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
		http.authorizeHttpRequests((authz)->authz
									.antMatchers("/jira/test")
									//.anyRequest()
									.authenticated()
									)
									.httpBasic();
		return http.build();	
	}
	@Bean
    public PasswordEncoder passwordEncoder() {
        // This is just for demonstration purposes; in a real application, use a secure password encoder.
        return NoOpPasswordEncoder.getInstance();
    }
	

}
