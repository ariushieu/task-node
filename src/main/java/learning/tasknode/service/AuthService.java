package learning.tasknode.service;

import learning.tasknode.config.JwtTokenProvider;
import learning.tasknode.dto.LoginRequest;
import learning.tasknode.dto.LoginResponse;
import learning.tasknode.dto.RefreshTokenRequest;
import learning.tasknode.dto.RefreshTokenResponse;
import learning.tasknode.entity.RefreshToken;
import learning.tasknode.entity.User;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                User user = userRepository.findByUsername(userDetails.getUsername())
                        .orElse(null);
                if (user != null) {
                    refreshTokenService.deleteByUser(user);
                }
            }
        }
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        refreshTokenService.deleteByUser(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        // Save user reference before deleting tokens to avoid lazy proxy issues
        User user = refreshToken.getUser();

        String accessToken = jwtTokenProvider.generateAccessTokenFromUsername(user.getUsername());

        refreshTokenService.deleteByUser(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }
}
