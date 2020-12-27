package com.elt.bank.config.filter;

import com.elt.bank.modal.Role;
import com.elt.bank.modal.User;
import com.elt.bank.repo.UserRepo;
import com.elt.bank.util.Constants;
import com.elt.bank.util.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.info("Filtering");
        String header = request.getHeader(Constants.HEADER_STRING);
        Map<String, Object> tokenDetails = null;
        if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
            String authToken = header.substring(Constants.TOKEN_PREFIX.length());
            try {
                tokenDetails = JWTUtils.getTokenMap(authToken);
                logger.trace("Token details parsed.");
            } catch (IllegalArgumentException e) {
                logger.error("An error occured while reading token details", e);
            } catch (ExpiredJwtException e) {
                logger.warn("The access token is expired.");
            } catch (SignatureException e) {
                logger.error("Token verification failed!", e);
            } catch (MalformedJwtException e) {
                logger.error("Invalid access token.", e);
            } catch (Exception e) {
                logger.error("Something wrong with token ", e);
            }
        } else {
            logger.trace("couldn't find bearer string, will ignore the header");
        }

        if (tokenDetails != null && JWTUtils.validateJwtToken(header)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            logger.info("Token Validated.");

            List<Role> roles = Arrays.stream(tokenDetails.get(Constants.JWT_TOKEN_ROLES)
                    .toString().split(","))
                    .map(Role::new).collect(Collectors.toList());
            List<SimpleGrantedAuthority> authorities = Arrays.stream(tokenDetails.get(Constants.JWT_TOKEN_ROLES)
                    .toString().split(","))
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            User user = new User();
            user.setUserName(tokenDetails.get(Constants.JWT_USER_NAME).toString());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "", authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            logger.debug("authenticated user, setting security context: " + tokenDetails.get(Constants.JWT_USER_NAME).toString());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // set request parameters to be used in "/assets" end-point
            request.setAttribute("user", user);
            request.setAttribute("roles", roles);
            logger.info("request params filled");
        }
        chain.doFilter(request, response);
        // reset the authentication context after filter chain is processed
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
