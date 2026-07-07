package com.festaplanner.service;

import com.festaplanner.dto.AuthResponseDto;
import com.festaplanner.dto.LoginDto;
import com.festaplanner.model.Admin;
import com.festaplanner.repository.AdminRepository;
import com.festaplanner.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementa POST /api/auth/login, espelhando o AuthService/LoginService
 * do Angular (core/services/auth-service.ts e login-service.ts).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;
    private final JwtService jwtService;

    public AuthResponseDto login(LoginDto dto) {
        // Lanca BadCredentialsException (-> HTTP 401) se e-mail/senha invalidos,
        // tratado pelo GlobalExceptionHandler.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        Admin admin = adminRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalStateException("Administrador nao encontrado apos autenticacao"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("nome", admin.getNome());
        claims.put("perfil", admin.getPerfil());

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(admin.getEmail())
                .password(admin.getSenhaHash())
                .authorities("ROLE_" + admin.getPerfil())
                .build();

        String token = jwtService.gerarToken(userDetails, claims);

        return AuthResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .nome(admin.getNome())
                .email(admin.getEmail())
                .perfil(admin.getPerfil())
                .build();
    }
}
