package com.exam.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "VikashBansal1509vvvvvvvvfhdsfaodvndaojboadbgdavbdomnvknvofbfeijqfoqfbofbawsedefrffjrufdudifjfdkudjedjdidjddjkdj";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
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

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

