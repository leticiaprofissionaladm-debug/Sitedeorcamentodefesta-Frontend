package com.festaplanner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste basico que garante que o contexto Spring sobe corretamente
 * (todos os beans, security config, etc. sao validos).
 *
 * Requer um MySQL acessivel conforme application.properties, ou configure
 * um application-test.properties com H2 para rodar sem banco externo.
 */
@SpringBootTest
class FestaPlannerApplicationTests {

    @Test
    void contextLoads() {
    }
}
