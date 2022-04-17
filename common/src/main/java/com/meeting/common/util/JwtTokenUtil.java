package com.meeting.common.util;

import com.meeting.common.entity.User;
import com.meeting.common.exception.UnAuthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {

    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_EMAIL = "email";
    private static final String CLAIM_KEY_ID = "id";
    private static final String CLAIM_KEY_ROLES = "roles";
    private static final String CLAIM_KEY_PROFILE = "profile";
    private static final String SECRET = "secret";
    private static final String ISSUER = "sdu-meeting";
    private static final int EXPIRATION = 604800;

    public Long getUserIdFromToken(String token) {
        Long uid;
        try {
            final Claims claims = getClaimsFromToken(token);
            uid = ((Integer) claims.get(CLAIM_KEY_ID)).longValue();
        } catch (Exception e) {
            uid = null;
        }
        return uid;
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            username = getClaimsFromToken(token).getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = claims.getIssuedAt();
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public String getEmailFromToken(String token) {
        String email;
        try {
            final Claims claims = getClaimsFromToken(token);
            email = (String) claims.get(CLAIM_KEY_EMAIL);
        } catch (Exception e) {
            email = null;
        }
        return email;
    }

    public Boolean getProfileFromToken(String token) {
        Boolean profile;
        try {
            final Claims claims = getClaimsFromToken(token);
            profile = (Boolean) claims.get(CLAIM_KEY_PROFILE);
        } catch (Exception e) {
            profile = null;
        }
        return profile;
    }

    private Claims getClaimsFromToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new UnAuthorizedException();
        }
        Claims claims;
        try {
            claims = Jwts
                    .parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, user.getUsername());
        claims.put(CLAIM_KEY_ID, user.getId());
        claims.put(CLAIM_KEY_EMAIL, user.getEmail());
        claims.put(CLAIM_KEY_ROLES, user.getAuthorities());
        claims.put(CLAIM_KEY_PROFILE, user.getProfile() == 0);
        return generateToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        final Date created = new Date(now);
        final Date expired = new Date(now + EXPIRATION * 1000L);
        return Jwts.builder()
                // 头部
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                // 载荷，setClaims必须写在前面，避免覆盖标准申明
                .setClaims(claims)
                .setSubject("user")
                .setIssuer(ISSUER)
                .setIssuedAt(created)
                .setExpiration(expired)
                // 签名
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    public String refreshToken(String token, Map<String, Object> info) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            info.forEach((key, value) -> claims.put(key, value));
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public boolean validateToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .requireIssuer(ISSUER)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ignored) {
            return false;
        }
        return !isTokenExpired(token);
    }

}
