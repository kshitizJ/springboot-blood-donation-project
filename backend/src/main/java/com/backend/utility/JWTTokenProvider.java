package com.backend.utility;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.backend.constant.SecurityConstant.AUTHORITIES;
import static com.backend.constant.SecurityConstant.BLOOD_DONATION_ADMINISTRATION;
import static com.backend.constant.SecurityConstant.BLOOD_DONATION_LLC;
import static com.backend.constant.SecurityConstant.EXPIRATION_TIME;
import static com.backend.constant.SecurityConstant.TOKEN_CANNOT_BE_VERIFIED;

// import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.backend.domain.AdminPrincipal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(AdminPrincipal principal) {
        log.info("\nGenerating the Token\n");
        return JWT.create()
                .withIssuer(BLOOD_DONATION_LLC)
                .withAudience(BLOOD_DONATION_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(principal.getUsername())
                .withClaim(AUTHORITIES, principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC256(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        JWTVerifier verifier = getJWTVerifier();
        log.info("Roles are:\n\t\n", verifier.verify(token).getClaim(AUTHORITIES).asList(String.class).stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        return verifier.verify(token).getClaim(AUTHORITIES).asList(String.class).stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities,
            HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                username, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        log.info("Getting username and authorities.",
                usernamePasswordAuthenticationToken.getAuthorities().stream().toArray().toString());
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        log.info("Validating the token.");
        return StringUtils.isNotEmpty(username) && isTokenExpired(verifier, token);
    }

    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        log.info("Getting the subject from the token.");
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        log.info("\nChecking if the token is expired or not.");
        return expiration.after(new Date());
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = HMAC256(secret);
            verifier = JWT.require(algorithm).withIssuer(BLOOD_DONATION_LLC).build();
        } catch (Exception e) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    // private List<String> getClaimsFromAdmin(AdminPrincipal principal) {
    // List<String> authorities = new ArrayList<>();
    // for (GrantedAuthority authority : principal.getAuthorities()) {
    // authorities.add(authority.getAuthority());
    // }
    // return authorities;
    // }

}
