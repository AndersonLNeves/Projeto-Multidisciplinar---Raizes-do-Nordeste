package com.raizesnordeste.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@OpenAPIDefinition(
    info = @Info(
        title = "Raízes do Nordeste API",
        version = "1.0.0",
        description = """
            API Backend para rede de lanchonetes Raízes do Nordeste.
            
            ## Fluxo Principal (Pedido → Pagamento → Status)
            1. `POST /auth/cadastro` — cadastrar usuário (com consentimento LGPD)
            2. `POST /auth/login` — obter JWT token
            3. `GET /unidades` — listar unidades
            4. `GET /produtos/unidade/{id}/cardapio` — ver cardápio
            5. `POST /pedidos` — criar pedido (informar **canalPedido**)
            6. `POST /pagamentos` — processar pagamento mock
            7. `PATCH /pedidos/{id}/status` — atualizar status (cozinha/atendente)
            
            ## Canais aceitos: APP, TOTEM, BALCAO, PICKUP, WEB
            """,
        contact = @Contact(name = "Raízes do Nordeste", email = "tech@raizesnordeste.com.br")
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Informe o token JWT obtido no endpoint /auth/login"
)
public class OpenApiConfig {
}
