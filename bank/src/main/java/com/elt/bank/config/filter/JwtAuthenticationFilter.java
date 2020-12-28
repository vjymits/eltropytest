package com.elt.bank.config.filter;

import com.elt.bank.modal.Role;
import com.elt.bank.pojo.UserPojo;
import com.elt.bank.repo.UserRepo;
import com.elt.bank.util.Constants;
import com.elt.bank.util.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * Filter every request to make sure authentication
     * and authorization is valid.
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(Constants.HEADER_STRING);
        boolean auth=false;
        Map<String, Object> tokenDetails = null;
        if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
            // get rid of Bearer String
            String authToken = header.substring(Constants.TOKEN_PREFIX.length());
            try {
                tokenDetails = JWTUtils.getTokenMap(authToken);
                auth = authorize(request.getRequestURI(), (ArrayList<String>) tokenDetails.get(Constants.JWT_TOKEN_ROLES));
                log.info("auth: "+auth);
                if (!auth)
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                log.trace("Token details parsed.");
            } catch (IllegalArgumentException e) {
                log.error("An error occurred while reading token details", e);
            } catch (ExpiredJwtException e) {
                log.warn("The access token is expired.");
                response.setStatus(HttpStatus.FORBIDDEN.value());
            } catch (SignatureException e) {
                log.error("Token verification failed!", e);
            } catch (MalformedJwtException e) {
                log.error("Invalid access token.", e);
            } catch (Exception e) {
                log.error("Something wrong with token ", e);
            }
        } else {
            log.trace("couldn't find bearer string, will ignore the header");
        }

        if (tokenDetails != null && JWTUtils.validateJwtToken(header)
                && SecurityContextHolder.getContext().getAuthentication() == null
                && auth) {

            log.info("Token Validated.");

            List<SimpleGrantedAuthority> authorities = Arrays.stream(tokenDetails.get(Constants.JWT_TOKEN_ROLES)
                    .toString().split(","))
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            UserPojo userPojo = new UserPojo();
            userPojo.setUsername(tokenDetails.get(Constants.JWT_USER_NAME).toString());

            User user= new User(userPojo.getUsername(), "", authorities);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "", authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // set request parameters to be used in "/assets" end-point
            request.setAttribute("user", userPojo);

            log.info("request params filled");
        }
        chain.doFilter(request, response);
        // reset the authentication context after filter chain is processed
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * authorize on the basis of URI and Role.
     * @param uri
     * @param roles
     * @return
     */
    private boolean authorize(String uri, List<String> roles) {
        String r = roles.get(0);
        if(uri.contains("employee") && r.equals(Constants.ROLE_ADMIN))
            return true;
        else if(uri.contains("customer") && r.equals(Constants.ROLE_EMP))
            return  true;
        else if(uri.contains("account") && r.equals(Constants.ROLE_EMP))
            return  true;
        else if(uri.contains("transaction") && r.equals(Constants.ROLE_EMP))
            return  true;
        return false;
    }
}
