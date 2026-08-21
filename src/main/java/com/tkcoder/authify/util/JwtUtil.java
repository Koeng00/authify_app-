package com.tkcoder.authify.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String SECRET_TOKEN;

    @Value("${JWT_ACCESS_EXPIRATION}")
    private Long ACCESS_TOKEN_EXP;

    @Value("${JWT_REFRESH_EXPIRATION}")
    private Long REFRESH_TOKEN_EXP;

    private Key getSignKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_TOKEN);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails userDetails)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        return createToken(
                claims,
                userDetails.getUsername(),
                ACCESS_TOKEN_EXP);
    }

    public String generateRefreshToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(
                claims,
                userDetails.getUsername(),
                REFRESH_TOKEN_EXP);
    }


    private String createToken(
            Map<String, Object> claims,
            String email,
            Long expiration)
    {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }


    private Claims extractAllClaims(String token)
    {
        return Jwts.parserBuilder().
                setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Create a functional interface
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractByType(String token) { return extractClaim(token, claims -> claims.get("type", String.class));};

    public String extractByEmail(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractByExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token)
    {
        return extractByExpiration(token).before(new Date());
    }

    public Boolean validateAccessToken(String token, UserDetails userDetails)
    {
        String email = extractByEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateRefreshToken(String token, UserDetails userDetails){
        String email = extractByEmail(token);
        String type = extractByType(token);

        return email.equals(userDetails.getUsername())
                && type.equals("refresh")
                && !isTokenExpired(token);
    }
}
