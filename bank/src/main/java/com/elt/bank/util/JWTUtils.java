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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class JWTUtils {

    private static final Logger log = LoggerFactory.getLogger(JWTUtils.class);


    private static Set<String> blackSet = new HashSet<>();

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

    private static Claims parseJwtAndGetClaims(final String jwtToken) {
        log.info("parsing jwt token for claims."+jwtToken);
        return Jwts.parser().setSigningKey(Constants.SECRET).parseClaimsJws(jwtToken).getBody();
    }

    public static boolean validateJwtToken(String token) {
        //TODO
        return false;
    }

    public static Set<String> getBlackSet() {
        return blackSet;
    }

    public static void logout(String token) {
        Thread blackSetCleaner;
        try{
            String[] arr = token.split("\\s");
            final String tokenOnly = arr[1];
            Claims claims = parseJwtAndGetClaims(tokenOnly);
            Date exp = claims.getExpiration();
            long timeLeft = exp.getTime()-System.currentTimeMillis();
            getBlackSet().add(tokenOnly);
            log.info("Time left to expire "+timeLeft+" MS. ");
            blackSetCleaner = new Thread() {
                public void run() {
                    try {
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

        blackSetCleaner.setDaemon(true);
        blackSetCleaner.setName("blackSetCleanerThread");
        blackSetCleaner.start();
        log.info("Logout successful. token will be in black set Black list size now: "
                +getBlackSet().size());
    }
}
