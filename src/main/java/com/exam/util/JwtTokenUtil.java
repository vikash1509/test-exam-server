package com.exam.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "VikashBansal1509vvvvvvvvfhdsfaodvndaojboadbgdavbdomnvknvofbfeijqfoqfbofbawsedefrffjrufdudifjfdkudjedjdidjddjkdj";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    public String generateToken(String username,String userType,String userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userType", userType) // Include userType in the token
                .claim("userId", userId) // Include userId as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getClaimFromToken(String token, String claimKey) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(claimKey, String.class);
    }

    public String getUserIdFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY) // Use the same secret key used to sign the token
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", String.class); // Extract userId claim
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

