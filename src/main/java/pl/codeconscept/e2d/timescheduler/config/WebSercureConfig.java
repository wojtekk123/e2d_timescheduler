package pl.codeconscept.e2d.timescheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import pl.codeconscept.e2d.timescheduler.service.jwt.JwtAuthFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
@Configuration
public class WebSercureConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter (AuthenticationManager authenticationManager){
        return new JwtAuthFilter(authenticationManager);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/ride/add").hasAnyRole("INSTRUCTOR","ADMIN")
                .antMatchers("/ride/delete/**").hasAnyRole("INSTRUCTOR","SCHOOL","ADMIN")
                .antMatchers("/reservation/add").hasAnyRole("STUDENT","ADMIN")
                .antMatchers("/reservation/delete/**").hasAnyRole("INSTRUCTOR","SCHOOL","STUDENT","ADMIN")
                .antMatchers("/reservation/all").hasAnyRole("INSTRUCTOR","SCHOOL","STUDENT","ADMIN")
                .antMatchers("/reservation/approve/**").hasAnyRole("INSTRUCTOR","SCHOOL","ADMIN")
                .antMatchers("/reservation/decline/**").hasAnyRole("INSTRUCTOR","SCHOOL","ADMIN")
                .anyRequest().denyAll()
                .and()
                .addFilter(jwtAuthFilter(authenticationManagerBean()))
                .csrf().disable();

    }
}
