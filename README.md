# APRESENTAÇÃO PESSOAL #

Ola! Seja muito bem vindo, meu nome é Anderson Littig, sou aluno de Análise e desenvolvimento de sistemas do Centro Universitário Internacional UNINTER. Este projeto foi desenvolvido como uma atividade multidisciplinar promovida pelo centro de ensino, a ideia é desenvolver e simular o funcionamento de um sistema de uma rede de lanchonetes nordestinas, é um dos meus maiores projetos até o momento, mas sempre busco aprender mais.
Nas linhas seguintes detalhei de forma mais didática, ficou bem longo, mas a ideia é que até mesmo um leigo possa ler, entender e com paciência e vontade de fazer, colocar em funcionamento esse sistema. Após as apresentações, desejo uma boa leitura.

**A interface visual do restaurante (frontend) não está incluída no projeto.**
**A interação com a API é feita através do Swagger/OpenAPI.**

#####################################################################

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

###############################################################################################################

# SUMÁRIO #

1. [Tecnologias](#tecnologias)
2. [Executando o Sistema](#executando_o_sistema)
3. [Acessando Swagger](#swagger)
4. [Fluxo Principal](#fluxo-principal)
5. [Endpoints](#endpoints)
6. [LGPD e Segurança](#lgpd-e-segurança)
7. [Conclusão](#conclusão)
8. [Passo a Passo da Instalação do Maven](#passo-a-passo)

#################################################################################################################

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

####################################################################################################################

# EXECUTANDO O SISTEMA #

## Programas Necessários ##

- Java 21 instalado ([download](https://www.oracle.com/br/java/technologies/downloads/))
- Maven 3.9.9 instalado ([download](https://maven.apache.org/download.cgi))

> O banco de dados H2 já está embutido na aplicação, portanto **não é necessário instalar MySQL ou PostgreSQL.**

> Obs: **As funcionalidades seguintes foram feitas no sistema operacional Windows, então, onde estiver escrito PowerShell (prompt de comando do windows), leia-se bash, caso utilize outro sistema operacional.**

**Caso tenha dúvidas na instalação do Maven, deixei uma breve explicação no final desta pagina.**

### Clonar o repositório ###

```PowerShell
git clone https://github.com/AndersonLNeves/Projeto-Multidisciplinar---Raizes-do-Nordeste
cd raizes-do-nordeste
```
> Obs: Se no terminal de comando estiver escrito:
> PS C:\Windows\System32>

indica que você esta dentro da pasta protegida do windows e pode dar erro na execução. Neste caso você vai digitar o seguinte comando para entrar na pasta de usuário:
> C:\Users\User

### Executando a API e Compilando Programa ###

```PowerShell
mvn clean install -DskipTests
```
Resultado esperado após o comando:
> BUILD SUCCESS

```PowerShell
mvn spring-boot:run
```
Saída esperada no terminal:
> === Seed concluído! ===
```
Usuários criados:
  ADMIN:     admin@raizes.com / admin123
  GERENTE:   gerente@raizes.com / gerente123
  COZINHA:   cozinha@raizes.com / cozinha123
  ATENDENTE: atendente@raizes.com / atendente123
  CLIENTE:   cliente@raizes.com / cliente123 (500 pontos)
Swagger: http://localhost:8080/swagger-ui.html
```

#############################################################################################################

### ACESSANDO O SWAGGER ###

Abra no Navegador:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
> O Swagger é a interface visual da API.

- **OpenAPI JSON:** http://localhost:8080/api-docs
> Documentação

- **H2 Console:** http://localhost:8080/h2-console
> Banco de dados
  
No console H2, nos campos da esquerda, digite o código que aparece na direita:
- JDBC URL: `jdbc:h2:mem:raizesnordeste`
- User Name: `sa`
- Password: (em branco)
> Agora tera acesso ao banco de dados para gerenciamento geral

### Fazendo login na API ###

#### Usuários de teste ####

Criados automaticamente pelo `DataSeeder` ao iniciar a aplicação:

| Role | Email | Senha | Pontos |
|------|-------|-------|--------|
| ADMIN | admin@raizes.com | admin123 | 0 |
| GERENTE | gerente@raizes.com | gerente123 | 0 |
| COZINHA | cozinha@raizes.com | cozinha123 | 0 |
| ATENDENTE | atendente@raizes.com | atendente123 | 0 |
| CLIENTE | cliente@raizes.com | cliente123 | 500 |

#### 2. No Swagger: ####

Para testar endpoints de login:
1. Procure `POST /auth/login`
2. Clique em 'Try it out'
3. Envie no terminal que aparecer abaixo o código a seguir e Execute:

{
  "email": "cliente@raizes.com",
  "senha": "cliente123"
}

> Resposta esperada no terminal:

{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "email": "cliente@raizes.com",
  "role": "CLIENTE"
}

4. Copie o campo `token` da resposta
3. Clique em **Authorize** (cadeado no topo)
4. Cole: `Bearer {seu_token}`
> Agora os Endpoints protegidos funcionarão normalmente.

##################################################################################################

## FLUXO PRINCIPAL ##

### Fluxo A — Pedido → Pagamento → Status ###

```
POST /auth/login
     → obtém JWT token
GET  /unidades
     → lista unidades
GET  /produtos/unidade/1/cardapio
     → ver cardápio disponível
POST /pedidos
     → criar pedido (informar canalPedido!)
POST /pagamentos
     → processar pagamento mock
PATCH /pedidos/{id}/status?novoStatus=EM_PREPARO
     → cozinha inicia
PATCH /pedidos/{id}/status?novoStatus=PRONTO
     → cozinha finaliza
PATCH /pedidos/{id}/status?novoStatus=ENTREGUE
     → atendente entrega
GET  /fidelidade/saldo
     → ver pontos acumulados
```
#####################################################################################################

## ENDPOINTS ##

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
Requer: JWT
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

##############################################################################################################

## LGPD E SEGURANÇA ##

### Dados pessoais coletados ###

Dado
    Nome, Email, CPF, Telefone e Histórico de Pedidos
Finalidade
    Identificação, autenticação, Programa de fidelidade, Comunicação e Auditoria
Base Legal
    Execução de contrato, Consentimento e Obrigação Legal
Retenção
    Enquanto conta ativa, 5 anos pelo código civil

### Controles implementados ###

- **Senhas:** BCrypt hash (fator 10) — nunca armazenadas em texto plano
- **Autenticação:** JWT Bearer token com expiração de 24h
- **Autorização:** RBAC com roles (CLIENTE, ATENDENTE, COZINHA, GERENTE, ADMIN)
- **Consentimento LGPD:** campo obrigatório no cadastro com timestamp registrado
- **Consentimento marketing:** opt-in separado, atualizável a qualquer momento
- **Auditoria:** `AuditLog` registra ações sensíveis (LOGIN, CRIAR_PEDIDO, CANCELAR_PEDIDO, PROCESSAR_PAGAMENTO)
- **LGPD no response:** CPF não é retornado nas respostas padrão da API
- **Dados de menores:** não coletados (sem campo de data de nascimento sem validação de idade)

#################################################################################################################

## CONCLUSÃO ##

Durante a implementação do sistema foram concluídas as principais funcionalidades previstas para o gerenciamento operacional do restaurante, contemplando autenticação, controle de pedidos, estoque, pagamentos e fidelização de clientes.

### As funcionalidades implementadas incluem: ###

- **Fluxo completo de atendimento** abrangendo criação do pedido, processamento do pagamento e atualização do status até sua conclusão;
- **Sistema de autenticação baseado em JWT (JSON Web Token)** com controle de permissões para cinco perfis distintos de usuários;
- **Suporte a múltiplos canais de atendimento** exigindo a identificação da origem do pedido e permitindo consultas filtradas por canal;
- **Gerenciamento de estoque por unidade** incluindo movimentações de entrada e saída e validação automática durante a realização de pedidos;
- **Programa de fidelidade** com acúmulo e utilização de pontos, respeitando as regras de consentimento estabelecidas;
- **Simulação de gateway de pagamento** com regras de aprovação e reprovação de transações e retorno detalhado das operações;
- **Registro de auditoria** para operações consideradas sensíveis, contribuindo para rastreabilidade e conformidade com a LGPD;
- **Documentação automática** da API por meio do Swagger/OpenAPI;
- **Tratamento padronizado** de erros e exceções em todos os endpoints;
- **Carga inicial de dados para testes e validação** contendo usuários, unidades, produtos e estoque previamente configurados.

###################################################################################################################

## PASSO A PASSO PARA INSTALAÇÃO DO MAVEN NO WINDOWS ##

1. Baixe o Apache Maven no link https://maven.apache.org/
2. Escolha Binary zip archive
3. Extraia para C:\Program Files\Apache\Maven
    Exemplo C:\Program Files\Apache\Maven\apache-maven-3.9.9

> Agora vamos configurar o PATH

1. Pesquise no Windows: 'Variáveis de Ambiente'
2. Abra: 'Editar variáveis do de ambiente do sistema'
3. Depois: 'Variáveis do ambiente'
4. Na seção variáveis do sistema
5. Edite: PATH
6. Adicione: C:\Program Files\Apache\Maven\apache-maven-3.9.9\bin

> Verificação do Maven

``` PowerShell
mvn -version
```

Resultado Esperado:

- Apache Maven 3.9.9

...