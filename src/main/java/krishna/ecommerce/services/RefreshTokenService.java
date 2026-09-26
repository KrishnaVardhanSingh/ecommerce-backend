package krishna.ecommerce.services;

import krishna.ecommerce.entity.RefreshToken;
import krishna.ecommerce.entity.User;
import krishna.ecommerce.exception.InvalidRefreshToken;
import krishna.ecommerce.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class RefreshTokenService {
    @Value("${jwt.refresh-token-expiration}")
    private long duration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom random = new SecureRandom();

    private RefreshTokenService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user){

        byte[] bytes = new byte[32];

        random.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);
        refreshToken.setToken(token);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiresAt(Instant.now().plusMillis(duration));

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(()->
                new InvalidRefreshToken("Invalid refresh token"));

        if(refreshToken.isRevoked()){
            throw new InvalidRefreshToken("Invalid refresh token");
        }

        if(!refreshToken.getExpiresAt().isAfter(Instant.now())){
            throw new InvalidRefreshToken("Invalid refresh token");
        }

        return refreshToken;
    }


    public void revokeRefreshToken(RefreshToken refreshToken){
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}


//createRefreshToken()
//        │
//        ├── generate unpredictable token
//        ├── associate User
//        ├── set creation time
//        ├── set expiration
//        ├── revoked = false
//        └── save
//
//
//verifyRefreshToken()
//        │
//        ├── doesn't exist → InvalidRefreshToken
//        ├── revoked       → InvalidRefreshToken
//        ├── expired       → InvalidRefreshToken
//        └── valid         → return RefreshToken


// multiple refresh tokens per user is the deliberate design. MULTIPLE DEVICE Diff logout
