package com.elt.bank.util;

import com.elt.bank.modal.Role;
import com.elt.bank.modal.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * All operation related to JWT token, such as creating, blacklisting and
 * validating etc.
 */

@Component
public class JWTUtils {

    private static final Logger log = LoggerFactory.getLogger(JWTUtils.class);
    private static Set<String> blackSet = new HashSet<>();

    /**
     * Create a jwt token for the user, and add important data in.
     * @param user
     * @return
     */
    public static String genrateJwtToken(User user){
        log.info("Genrating jwt token for user: "+user.getUserName());
        long t = System.currentTimeMillis();
        Claims claims = Jwts.claims().setSubject(user.getUserName());
        claims.put(Constants.JWT_TOKEN_TIMESTAMP, t);
        claims.put(Constants.JWT_TOKEN_ROLES, user.getRoles().stream()
                .map(Role::getName).collect(Collectors.toList()));
        claims.put(Constants.JWT_TOKEN_EMAIL, user.getEmail());
        claims.setIssuedAt(new Date(t));
        claims.setExpiration(new Date(t+Constants.EXPIRATION_TIME));
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512,
                Constants.SECRET).compact();

        return "Bearer "+token;
    }

    /**
     * get JWT token claims.
     * @param jwtToken
     * @return
     */
    private static Claims parseJwtAndGetClaims(final String jwtToken) {
        log.info("parsing jwt token for claims. "+jwtToken);
        return Jwts.parser().setSigningKey(Constants.SECRET).parseClaimsJws(jwtToken).getBody();
    }

    /**
     * Get the token data in the form of map, which was encapsulated in token.
     * @param token
     * @return
     */
    public static Map<String, Object> getTokenMap(final String token) {
        Claims claims = parseJwtAndGetClaims(token);
        Map<String, Object> m = new TreeMap<>();
        m.put(Constants.JWT_EXPIRY_TIME, claims.getExpiration().getTime());
        m.put(Constants.JWT_TOKEN_EMAIL, claims.get(Constants.JWT_TOKEN_EMAIL));
        m.put(Constants.JWT_TOKEN_ROLES, claims.get(Constants.JWT_TOKEN_ROLES));
        m.put(Constants.JWT_ISSUE_TIME, claims.getIssuedAt().getTime());
        m.put(Constants.JWT_USER_NAME, claims.getSubject());
        return m;
    }

    /**
     * Validate the token, by checking sign, expiry and blacklisted or not.
     * @param token
     * @return
     */
    public static boolean validateJwtToken(String token) {
        boolean f = false;
        try {
            if (token != null && token.startsWith(Constants.TOKEN_PREFIX))
                token = token.substring(Constants.TOKEN_PREFIX.length());
            Claims c = parseJwtAndGetClaims(token);
            f = c.getExpiration() != null && !getBlackSet()
                    .contains(token);
        }
        catch (Exception e) {
            log.trace("An error while validation token.", e);
            f=false;
        }
        log.info("returning "+f);
        return f;
    }

    /**
     * Get blacklist which is a set.
     * @return
     */
    public static Set<String> getBlackSet() {
        return blackSet;
    }

    /**
     * Get rid of Bearer word in token.
     * @param token
     * @return
     */
    private static String onlyToken(String token) {
        String[] arr = token.split("\\s");
        final String tokenOnly = arr[1];
        return tokenOnly;
    }

    /**
     * Stateless log out by putting token in black list
     * later remove from black list if it expire.
     * @param token
     */
    public static void logout(String token) {
        Thread blackSetCleaner;
        try{
            final String tokenOnly = onlyToken(token);
            Claims claims = parseJwtAndGetClaims(tokenOnly);
            Date exp = claims.getExpiration();
            long timeLeft = exp.getTime()-System.currentTimeMillis();
            getBlackSet().add(tokenOnly);
            log.info("Time left to expire "+timeLeft+" MS. ");
            blackSetCleaner = new Thread() {
                public void run() {
                    try {
                        // Sleep till token expire +1 sec.
                        Thread.sleep(timeLeft+1000);
                        getBlackSet().remove(tokenOnly);
                        log.info("Black-set updated by cleanup, size now : "
                                +getBlackSet().size());
                    }
                    catch (Exception e){
                        log.error("Black-set cleaner got a error,"+e);
                    }
                }
            };
        }
        catch (ExpiredJwtException e){
            log.info("Token already expired");
            return;
        }
        // start cleaner thread for the token.
        blackSetCleaner.setDaemon(true);
        blackSetCleaner.setName("blackSetCleanerThread");
        blackSetCleaner.start();
        log.info("Logout successful. token will be in black set Black list size now: "
                +getBlackSet().size());
    }
}
