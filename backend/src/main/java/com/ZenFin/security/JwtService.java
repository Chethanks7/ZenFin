package com.ZenFin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class JwtService {


    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    private final EncryptionKey encryptionKey;


    public String generateToken(UserDetails userDetails) throws IOException {
        return generateToken(new HashMap<String, Object>(), userDetails); // Generates a token with no additional claims.
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) throws IOException {
        return buildToken(claims, userDetails, jwtExpiration); // Builds and returns the jwt token with the specified claims and expiration.
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) throws IOException {
        // Maps user authorities to a list for inclusion in the token.
        var authorities = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts
                .builder() // Begins the building process for a JWT.
                .setClaims(extraClaims) // Sets any additional claims.
                .setSubject(userDetails.getUsername()) // Sets the subject of the token (username).
                .setIssuedAt(new Date(System.currentTimeMillis())) // Sets the issued date.
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Sets the expiration date.
                .claim("authorities", authorities) // Adds user authorities to the claims.
                .signWith(getSignInKey()) // Signs the token using the signing key.
                .compact(); // Builds the final JWT string.
    }



    public String extractUsername(String token) throws IOException {
        return extractClaim(token, (Claims::getSubject));
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) throws IOException {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractClaims(String token) throws IOException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) throws IOException {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws IOException {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws IOException {
        return extractClaim(token, Claims::getExpiration);
    }

    public Key getSignInKey() throws IOException {
        // Decodes the base64 encoded secret key and returns it as a Key object.
        byte[] bytes = Decoders.BASE64.decode(encryptionKey.getEncryptionKey("secretmanager.googleapis.com","driven-origin-443911-r0"));
        return Keys.hmacShaKeyFor(bytes); // Creates an HMAC signing key for signing the JWT.
    }
}
