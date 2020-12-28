package com.elt.bank.config;


import javax.servlet.http.HttpServletResponse;

import com.elt.bank.config.filter.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;
import java.util.Set;

/**
 * Spring security configuration for REST end-points.
 * A state-less system configuration.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Security configurations
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/**/auth/**")
                .permitAll()
                .and().exceptionHandling().authenticationEntryPoint((rq, rs, e) -> rs.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Bean configuration for CORS
     *
     * @return WebMvcConfigurer instance
     */
    @Bean
    public WebMvcConfigurer configureCors() {
        // this needs to be changed to a filter to return request origin dynamically
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Global Cross Origins
                registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").maxAge(3600);
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }


}
