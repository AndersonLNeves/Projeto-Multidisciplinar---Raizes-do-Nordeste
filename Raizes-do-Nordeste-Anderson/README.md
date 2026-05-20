# Apresentação Pessoal #

Ola! Seja muito bem vindo, meu nome é Anderson Littig, sou aluno de Análise e desenvolvimento de sistemas do Centro Universitário Internacional UNINTER. Este projeto foi desenvolvido como uma atividade multidisciplinar promovida pelo centro de ensino, a ideia é desenvolver e simular o funcionamento de um sistema de uma rede de lanchonetes nordestinas, é um dos meus maiores projetos até o momento, mas sempre busco aprender mais.
Nas linhas seguintes detalhei de forma mais didática, ficou bem longo, mas a ideia é que até mesmo um leigo possa ler, entender e com paciência e vontade de fazer, colocar em funcionamento esse sistema. Após as apresentações, desejo uma boa leitura.

###############################################################

# Rede Raízes do Nordeste — API Backend #

Sistema backend desenvolvido em Java e Spring Boot para gerenciamento de uma rede de lanchonetes nordestinas, contendo autenticação JWT, pedidos, pagamentos, estoque, fidelidade e controle de perfís.
O projeto simula o funcionamento real de um sistema moderno de restaurante, permitindo:
- Autenticação de Usuário;
- Gerenciamento de pedidos;
- Controle de estoque;
- Pagamentos simulados;
- Programa de fidelidade;
- Multiplos canais de atendimento;
- Atualização de status de pedido.

###############################################################

# Sumário #

1. [Tecnologias](#tecnologias)
2. [Executando o Sistema](#executando_o_sistema)
3. 
4. [Usuários de teste (seed)](#usuários-de-teste)
5. [Swagger / Documentação](#swagger)
6. [Fluxo Principal](#fluxo-principal)
7. [Endpoints](#endpoints)
8. [Plano de Testes](#plano-de-testes)
9. [LGPD e Segurança](#lgpd-e-segurança)
10. [Conclusão](#conclusão)

###############################################################

# TECNOLOGIAS #

| Backend | ------ | Java 21 | --- | Spring Boot |
| Segurança | ----- | JWT | --- | Spring Security | --- |	BCrypt |
| Persistência | --- | JPA | --- | Hibernate | --- | H2 |
|	Documentação e Testes | --- |	Swagger | --- |	Postman |
| Versionamento | ---- | Git/GitHub |

| Java 21 | --- Foi utilizado como linguagem principal de aplicação por possuir ampla utilização no mercado e forte integração com o ecossistema spring.

| Spring Boot | --- Foi utilizado para simplificar a configuração da aplicação backend, permitindo criação rápida da API REST e integração com diversos módulos de framework spring.

| Spring Security + JWT | --- Foram fundamentais para realizar a autenticação, com base em JSON Web Token, o que garantia mais proteção dos endpoints e controle de acesso para cada perfil de usuário.

| H2 Database | --- Foi nosso banco de dados em memória, para facilitar a execução local do projeto sem a necessidade de instalação adicional de servidores de banco de dados.

| Swagger e OpenAPI | --- Serviram de documentação automática de API, permitindo testes diretos dos endpoints e visualização das rotas disponíveis.

#################################################################

# EXECUTANDO O SISTEMA #

## Programas Necessários ##

- Java 21 instalado ([download](https://www.oracle.com/br/java/technologies/downloads/))
- Maven 3.9.9 instalado ([download](https://maven.apache.org/download.cgi))

> **Não precisa instalar banco de dados externo como MySQL ou PostgreSQL.** O H2 é o banco de dados, ele é criado automaticamente na memória.

**Obs**: As funcionalidades seguintes foram feitas no sistema operacional Windows, então, onde estiver escrito PowerShell (prompt de comando do windows), leia-se bash, caso utilize outro sistema operacional.

### 1. Clonar o repositório

```PowerShell
git clone https://github.com/AndersonLNeves/Projeto-Multidisciplinar---Raizes-do-Nordeste
cd raizes-do-nordeste
```

### 2. Variáveis de ambiente (opcional)

O projeto já possui valores padrão configurados em `application.properties`. Para personalizar, crie um `.env` ou sobrescreva as propriedades:

```properties
# JWT secret (mínimo 32 chars)
jwt.secret=raizesnordeste-secret-key-super-segura-2024-backend-atividade

# Expiração do token em ms (padrão: 24h)
jwt.expiration=86400000

# Porta do servidor (padrão: 8080)
server.port=8080
```

### 3. Instalar dependências e compilar

```bash
mvn clean install -DskipTests
```

### 4. Iniciar a API

```bash
mvn spring-boot:run
```

Saída esperada no terminal:
```
=== Seed concluído! ===
Usuários criados:
  ADMIN:     admin@raizes.com / admin123
  GERENTE:   gerente@raizes.com / gerente123
  COZINHA:   cozinha@raizes.com / cozinha123
  ATENDENTE: atendente@raizes.com / atendente123
  CLIENTE:   cliente@raizes.com / cliente123 (500 pontos)
Swagger: http://localhost:8080/swagger-ui.html
```

### 5. Acessar a documentação

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:raizesnordeste`
  - User: `sa` | Password: (em branco)

### 6. Rodar os testes

```bash
mvn test
```

---

## Usuários de teste

Criados automaticamente pelo `DataSeeder` ao iniciar a aplicação:

| Role | Email | Senha | Pontos |
|------|-------|-------|--------|
| ADMIN | admin@raizes.com | admin123 | 0 |
| GERENTE | gerente@raizes.com | gerente123 | 0 |
| COZINHA | cozinha@raizes.com | cozinha123 | 0 |
| ATENDENTE | atendente@raizes.com | atendente123 | 0 |
| CLIENTE | cliente@raizes.com | cliente123 | 500 |

---

## Swagger

URL local: **http://localhost:8080/swagger-ui.html**

Para testar endpoints autenticados no Swagger:
1. Execute `POST /auth/login` com as credenciais
2. Copie o campo `token` da resposta
3. Clique em **Authorize** (cadeado no topo)
4. Cole: `Bearer {seu_token}`

---

## Fluxo Principal

### Fluxo A — Pedido → Pagamento → Status (MVP obrigatório)

```
POST /auth/login                          → obtém JWT token
GET  /unidades                            → lista unidades
GET  /produtos/unidade/1/cardapio         → ver cardápio disponível
POST /pedidos                             → criar pedido (informar canalPedido!)
POST /pagamentos                          → processar pagamento mock
PATCH /pedidos/{id}/status?novoStatus=EM_PREPARO   → cozinha inicia
PATCH /pedidos/{id}/status?novoStatus=PRONTO        → cozinha finaliza
PATCH /pedidos/{id}/status?novoStatus=ENTREGUE      → atendente entrega
GET  /fidelidade/saldo                    → ver pontos acumulados
```

---

## Endpoints

### /auth

#### POST /auth/login
Autentica usuário e retorna JWT Bearer token.

**Request:**
```json
{
  "email": "cliente@raizes.com",
  "senha": "cliente123"
}
```
**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "email": "cliente@raizes.com",
  "nome": "Pedro Cliente",
  "role": "CLIENTE",
  "expiracaoMs": 86400000
}
```
**Status:** 200 OK | 401 (credenciais inválidas)

---

#### POST /auth/cadastro
Cria novo usuário. Requer `consentimentoLgpd: true`.

**Request:**
```json
{
  "nome": "João da Silva",
  "email": "joao@email.com",
  "senha": "minhasenha123",
  "cpf": "12345678901",
  "telefone": "(85) 99999-9999",
  "consentimentoLgpd": true,
  "consentimentoMarketing": false
}
```
**Response 201:**
```json
{
  "id": 6,
  "nome": "João da Silva",
  "email": "joao@email.com",
  "role": "CLIENTE",
  "pontosFidelidade": 0,
  "consentimentoLgpd": true
}
```
**Status:** 201 Created | 400 (email duplicado) | 422 (validação)

---

### /unidades

#### GET /unidades
Lista todas as unidades ativas. Público.

**Response 200:**
```json
[
  { "id": 1, "nome": "Raízes do Nordeste - Centro", "cidade": "Fortaleza", "ativa": true },
  { "id": 2, "nome": "Raízes do Nordeste - Shopping", "cidade": "Fortaleza", "ativa": true }
]
```

#### POST /unidades
Requer: GERENTE ou ADMIN

---

### /produtos

#### GET /produtos/unidade/{unidadeId}/cardapio
Retorna somente produtos disponíveis. Público.

#### GET /produtos/unidade/{unidadeId}?categoria=LANCHE&page=0&limit=10
Listagem paginada com filtro por categoria.

**Status:** 200 OK | 404 (unidade não encontrada)

---

### /pedidos

#### POST /pedidos
Requer: JWT (qualquer role).
**`canalPedido` é obrigatório.**

**Request:**
```json
{
  "unidadeId": 1,
  "canalPedido": "APP",
  "itens": [
    { "produtoId": 1, "quantidade": 2, "observacoes": "sem sal" },
    { "produtoId": 4, "quantidade": 1 }
  ],
  "observacoes": "Mesa 5",
  "pontosParaResgatar": 0
}
```
**Response 201:**
```json
{
  "id": 1,
  "canalPedido": "APP",
  "status": "PENDENTE",
  "valorTotal": 47.70,
  "itens": [...],
  "criadoEm": "2024-01-15T14:30:00"
}
```
**Status:** 201 | 400 (canalPedido inválido) | 404 (produto/unidade) | 409 (estoque insuficiente) | 422 (validação)

**Erro padrão:**
```json
{
  "status": 409,
  "erro": "Conflito de regra de negócio",
  "mensagem": "Estoque insuficiente para 'Tapioca de Queijo Coalho'. Disponível: 50, Solicitado: 99999",
  "timestamp": "2024-01-15T14:30:00"
}
```

#### GET /pedidos?canalPedido=TOTEM&status=PENDENTE&unidadeId=1&page=0&limit=10
Filtro por canal de pedido (multicanalidade).

#### PATCH /pedidos/{id}/status?novoStatus=EM_PREPARO
Fluxo: PENDENTE → CONFIRMADO → EM_PREPARO → PRONTO → ENTREGUE | CANCELADO

---

### /pagamentos

#### POST /pagamentos
Envia pedido para o gateway mock.

**Request:**
```json
{ "pedidoId": 1, "metodo": "PIX" }
```
**Response 200:**
```json
{
  "id": 1,
  "status": "APROVADO",
  "gatewayTransactionId": "TXN-A1B2C3D4",
  "gatewayMensagem": "Pagamento autorizado pelo gateway",
  "gatewayPayload": "{ \"gateway\": \"RaizesPayMock\", \"status\": \"APROVADO\", ... }"
}
```

**Regras do mock:**
- PIX e DINHEIRO: sempre APROVADO
- CARTÃO: APROVADO se valor < R$500, RECUSADO se >= R$500
- Idempotência: pagamento aprovado não pode ser reprocessado (409)

---

### /estoque

#### POST /estoque/movimentar
Requer: GERENTE ou ADMIN.

```json
{ "produtoId": 1, "unidadeId": 1, "tipo": "ENTRADA", "quantidade": 50, "motivo": "REPOSICAO" }
```

#### GET /estoque/unidade/{unidadeId}
Lista estoque completo da unidade, com flag `abaixoMinimo`.

---

### /fidelidade

#### GET /fidelidade/saldo
Retorna saldo de pontos do usuário logado.
- 1 ponto por R$1,00 gasto
- 100 pontos = R$1,00 de desconto
- Pontos creditados ao status ENTREGUE

#### PATCH /fidelidade/consentimento?consentimentoMarketing=true
Atualiza preferência de marketing (LGPD).

---

## Padrão de Erro

Todos os erros seguem o mesmo formato:
```json
{
  "status": 422,
  "erro": "Erro de validação",
  "mensagem": "Um ou mais campos são inválidos",
  "timestamp": "2024-01-15T14:30:00",
  "campos": {
    "canalPedido": "Canal do pedido é obrigatório (APP, TOTEM, BALCAO, PICKUP, WEB)"
  }
}
```

---

## Plano de Testes

> Coleção completa em: `postman/raizes-do-nordeste.postman_collection.json`
>
> **Ordem de execução:** T01 (login admin) → T02 (login cliente) → T05 (cadastro) → T07-T08 (unidades/cardápio) → T09-T10 (estoque) → T11 (criar pedido) → T18 (pagamento) → T21-T23 (status) → T24 (fidelidade)

### Cenários Positivos

| ID | Endpoint | Pré-condição | Entrada | Status esperado |
|----|----------|--------------|---------|-----------------|
| T01 | POST /auth/login | seed aplicado | admin@raizes.com / admin123 | 200 + token |
| T02 | POST /auth/login | seed aplicado | cliente@raizes.com / cliente123 | 200 + role=CLIENTE |
| T05 | POST /auth/cadastro | email inédito | consentimentoLgpd=true | 201 |
| T07 | GET /unidades | seed aplicado | - | 200 + lista |
| T08 | GET /produtos/unidade/1/cardapio | seed aplicado | - | 200 + disponivel=true |
| T11 | POST /pedidos | logado, estoque OK | canalPedido=APP | 201 + status=PENDENTE |
| T18 | POST /pagamentos | pedido pendente | metodo=PIX | 200 + status=APROVADO |
| T23 | PATCH /pedidos/{id}/status | pagamento aprovado | novoStatus=ENTREGUE | 200 + pontosGerados>0 |

### Cenários Negativos

| ID | Endpoint | Entrada | Status esperado | Mensagem |
|----|----------|---------|-----------------|---------|
| T03 | POST /auth/login | senha errada | 401 | Email ou senha inválidos |
| T04 | GET /pedidos | sem token | 401 | - |
| T06 | POST /auth/cadastro | consentimentoLgpd=false | 422 | campos.consentimentoLgpd |
| T14 | POST /pedidos | sem canalPedido | 422 | campos.canalPedido |
| T15 | POST /pedidos | produtoId=9999 | 404 | Produto não encontrado |
| T16 | POST /pedidos | quantidade=99999 | 409 | Estoque insuficiente |
| T17 | POST /unidades | token de CLIENTE | 403 | Acesso negado |
| T19 | POST /pagamentos | pedido já pago | 409 | Pagamento já aprovado |

---

## LGPD e Segurança

### Dados pessoais coletados

| Dado | Finalidade | Base legal | Retenção |
|------|-----------|------------|---------|
| Nome, Email | Identificação, autenticação | Execução de contrato | Enquanto conta ativa |
| CPF | Programa de fidelidade | Consentimento | Enquanto conta ativa |
| Telefone | Comunicação | Consentimento (marketing) | Enquanto conta ativa |
| Histórico de pedidos | Programa de fidelidade, auditoria | Obrigação legal | 5 anos (Código Civil) |

### Controles implementados

- **Senhas:** BCrypt hash (fator 10) — nunca armazenadas em texto plano
- **Autenticação:** JWT Bearer token com expiração de 24h
- **Autorização:** RBAC com roles (CLIENTE, ATENDENTE, COZINHA, GERENTE, ADMIN)
- **Consentimento LGPD:** campo obrigatório no cadastro com timestamp registrado
- **Consentimento marketing:** opt-in separado, atualizável a qualquer momento
- **Auditoria:** `AuditLog` registra ações sensíveis (LOGIN, CRIAR_PEDIDO, CANCELAR_PEDIDO, PROCESSAR_PAGAMENTO)
- **LGPD no response:** CPF não é retornado nas respostas padrão da API
- **Dados de menores:** não coletados (sem campo de data de nascimento sem validação de idade)

### Promoções/Campanhas (conceitual)

Para implementar promoções, o modelo sugere:
```
Entidade Campanha { id, nome, desconto, dataInicio, dataFim, canalAplicavel, produtosAlvo }
```
Aplicada no PedidoService antes do cálculo do valorTotal, verificando `canalPedido` e `dataAtual`.

---

## Conclusão

### O que foi implementado

- **Fluxo A completo:** Pedido → Pagamento Mock → Atualização de Status ✅
- **Autenticação JWT** com 5 perfis e controle de acesso ✅
- **Multicanalidade:** campo `canalPedido` obrigatório em pedidos, filtro por canal ✅
- **Estoque por unidade** com controle de entrada/saída e validação em pedidos ✅
- **Programa de fidelidade** com acúmulo de pontos e resgate com consentimento ✅
- **Gateway mock** com lógica de aprovação/recusa e payload de retorno ✅
- **AuditLog** para ações sensíveis (LGPD) ✅
- **Swagger/OpenAPI** gerado automaticamente ✅
- **Padrão de erro** consistente em toda a API ✅
- **Seed** com 5 usuários, 2 unidades, 9 produtos e estoque configurado ✅

### O que ficou como proposta

- **Deploy em nuvem:** Dockerfile e deploy em Railway/Render (migração para PostgreSQL é automática via `spring.jpa.hibernate.ddl-auto`)
- **Campanhas/Promoções:** modelagem conceitual documentada acima
- **Testes de integração** completos com MockMvc
- **Rate limiting** por IP para endpoints públicos
- **Anonimização** automática de dados após inatividade (cron job)

### Como o DER, Classes e Casos de Uso se conectam

O DER reflete exatamente as entidades JPA: `pedidos` possui FK para `usuarios`, `unidades` e referência 1:1 para `pagamentos`. Os casos de uso (criar pedido, processar pagamento) são os Application Services, que orquestram as regras do Domain (verificação de estoque, transição de status) e delegam persistência para os Repositories da camada Infrastructure.

---

## Links

- **Swagger local:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
- **Coleção Postman:** `postman/raizes-do-nordeste.postman_collection.json`
