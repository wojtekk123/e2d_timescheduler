package pl.codeconscept.e2d.timescheduler.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Component
public class JwtAuthFilter extends BasicAuthenticationFilter {

    private static final String jwtSecret = "1234";

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    private String token;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwt(httpServletRequest);

            if (jwt != null) {
                setToken(jwt);
                String username = getClaims(jwt).getSubject();
                String role = getClaims(jwt).get("role").toString();
                Long authId = Long.parseLong(getClaims(jwt).get("id").toString());
                Set<SimpleGrantedAuthority> grantedAuthority = Collections.singleton(new SimpleGrantedAuthority(role));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, authId, grantedAuthority);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else throw new NullPointerException();

            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (NullPointerException e) {
            logger.error("lack of token");
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (MalformedJwtException e) {
            logger.error("Invalid token");
        } catch (ExpiredJwtException e) {
            logger.error("Token expired ");
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (UsernameNotFoundException e) {
            logger.error("User not found");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Some other exception in JWT parsing");
        }
    }

    private String getJwt(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "");
        }
        return null;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
