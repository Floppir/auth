package com.sigma.auth.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import com.sigma.auth.exception.TokenRefreshException;
import com.sigma.auth.models.ERole;
import com.sigma.auth.models.RefreshToken;
import com.sigma.auth.models.Role;
import com.sigma.auth.models.User;
import com.sigma.auth.payload.request.LogOutRequest;
import com.sigma.auth.payload.request.LoginRequest;
import com.sigma.auth.payload.request.SignupRequest;
import com.sigma.auth.payload.request.TokenRefreshRequest;
import com.sigma.auth.payload.response.JwtResponse;
import com.sigma.auth.payload.response.MessageResponse;
import com.sigma.auth.payload.response.SignUpResponse;
import com.sigma.auth.payload.response.TokenRefreshResponse;
import com.sigma.auth.repository.RefreshTokenRepository;
import com.sigma.auth.repository.RoleRepository;
import com.sigma.auth.repository.UserRepository;
import com.sigma.auth.security.jwt.JwtUtils;
import com.sigma.auth.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateAccessToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        String refreshToken = jwtUtils.createRefreshToken(userDetails.getUsername());
        RefreshToken refreshToken1 =  new RefreshToken();
        refreshToken1.setToken(refreshToken);
        refreshTokenRepository.save(refreshToken1);

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken, userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);


        user.setRoles(roles);
        userRepository.save(user);

        String accessToken = jwtUtils.generateAccessTokenFromUsername(signUpRequest.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(signUpRequest.getUsername());
        RefreshToken refreshToken1 = new RefreshToken();
        refreshToken1.setToken(refreshToken);
        refreshTokenRepository.save(refreshToken1);

        return ResponseEntity.ok(new SignUpResponse(accessToken, refreshToken));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken (@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        String username = jwtUtils.getUserNameFromRefreshToken(requestRefreshToken);

        if (jwtUtils.validateRefreshExpiration(requestRefreshToken)) {

            if (refreshTokenRepository.existsByToken(requestRefreshToken)){

                String token = jwtUtils.generateAccessTokenFromUsername(username);

                jwtUtils.deleteByRefreshToken(requestRefreshToken);
                String refreshToken = jwtUtils.createRefreshToken(username);
                RefreshToken refreshToken1 = new RefreshToken();
                refreshToken1.setToken(refreshToken);
                refreshTokenRepository.save(refreshToken1);

                return ResponseEntity.ok(new TokenRefreshResponse(token, refreshToken));
            }
        }
        return ResponseEntity.ok(new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
        jwtUtils.deleteByRefreshToken(logOutRequest.getToken());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

}
