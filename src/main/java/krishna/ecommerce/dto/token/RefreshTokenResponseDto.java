package krishna.ecommerce.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
//Because we'll implement rotation:
//
//Old Refresh Token
//       ↓
//     used
//       ↓
//    revoked
//       ↓
//New Refresh Token