package com.festaplanner.config;

import com.festaplanner.model.Admin;
import com.festaplanner.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria um administrador padrao na primeira execucao, apenas se a tabela
 * "admins" estiver vazia. Assim ha sempre uma conta para testar o login
 * do painel /admin no Angular.
 *
 * Login inicial: admin@festaplanner.com / admin123
 * IMPORTANTE: troque a senha (ou remova este seeder) antes de ir para producao.
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            Admin admin = Admin.builder()
                    .nome("Administrador Festa Planner")
                    .email("admin@festaplanner.com")
                    .senhaHash(passwordEncoder.encode("admin123"))
                    .perfil("ADMIN")
                    .ativo(true)
                    .build();
            adminRepository.save(admin);
            System.out.println("=====================================================");
            System.out.println(" Admin padrao criado: admin@festaplanner.com / admin123");
            System.out.println(" Troque essa senha assim que possivel!");
            System.out.println("=====================================================");
        }
    }
}
