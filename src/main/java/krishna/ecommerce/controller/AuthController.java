package krishna.ecommerce.controller;

import jakarta.validation.Valid;
import krishna.ecommerce.dto.login.LoginRequestDto;
import krishna.ecommerce.dto.login.LoginResponseDto;
import krishna.ecommerce.dto.token.RefreshTokenRequestDto;
import krishna.ecommerce.dto.token.RefreshTokenResponseDto;
import krishna.ecommerce.entity.RefreshToken;
import krishna.ecommerce.entity.User;
import krishna.ecommerce.exception.cart.UserNotFound;
import krishna.ecommerce.repository.UserRepository;
import krishna.ecommerce.security.JwtService;
import krishna.ecommerce.services.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService,
                          UserRepository userRepository){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(token);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userDetails);

        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() ->
                new UserNotFound("User not found"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        LoginResponseDto response = new LoginResponseDto(
                accessToken,
                refreshToken.getToken()
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto>  refresh(
            @Valid @RequestBody RefreshTokenRequestDto request
            ){
        RefreshToken oldRefreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        User user = oldRefreshToken.getUser();

        refreshTokenService.revokeRefreshToken(oldRefreshToken);

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                )
        );

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        RefreshTokenResponseDto response = new RefreshTokenResponseDto(newAccessToken, newRefreshToken.getToken());

        return ResponseEntity.ok(response);
    }
}


//    ACCESS TOKEN
//    │
//    ├── short-lived
//    ├── JWT
//    ├── sent with normal API requests
//    └── validated by JwtAuthenticationFilter
//
//
//    REFRESH TOKEN
//    │
//    ├── long-lived
//    ├── opaque random value
//    ├── stored server-side
//    ├── used only at /refresh
//    ├── revocable
//    └── rotated after use