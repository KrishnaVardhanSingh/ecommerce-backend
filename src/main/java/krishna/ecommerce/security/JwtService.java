package krishna.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// create, read and validate JWT
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;


    // for your configured secret. For the learning implementation, this demonstrates the mechanics,
    // but for the final production-oriented implementation I'd prefer a properly generated/encoded
    // cryptographic secret rather than treating an arbitrary environment string as a raw key.
    // JJWT specifically recommends appropriate encoded key material and provides key-generation utilities.
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateAccessToken(UserDetails userDetails){
        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + accessTokenExpiration
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                // this is reasonable just because we have only one role assigned to each one
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }


    // The nice part is that JJWT's parsing process will reject an invalid
    // signature or expired token by throwing a JWT-related exception.
    public boolean validateToken(
            String token,
            UserDetails userDetails
    ){
        try{
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject().equals(userDetails.getUsername());
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

}
