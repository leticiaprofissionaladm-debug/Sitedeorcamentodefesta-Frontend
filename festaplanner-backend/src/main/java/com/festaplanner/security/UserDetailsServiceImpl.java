package com.festaplanner.security;

import com.festaplanner.model.Admin;
import com.festaplanner.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carrega o Admin do banco de dados (por e-mail) para o Spring Security
 * usar durante a autenticacao e a validacao do JWT.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador nao encontrado: " + email));

        return User.builder()
                .username(admin.getEmail())
                .password(admin.getSenhaHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + admin.getPerfil()))
                .disabled(!admin.isAtivo())
                .build();
    }
}
