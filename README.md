
# ⚙️ TinyRoute - API REST (Backend)

O **TinyRoute API** é o motor de processamento e persistência de dados da plataforma de encurtamento de URLs. Desenvolvido sob uma arquitetura RESTful robusta, este Backend é responsável por garantir a geração eficiente de links únicos, o redirecionamento de alta performance e a manutenção da integridade das regras de negócio (como expiração de links e controle de cliques).

Este repositório contém o código-fonte exclusivo da camada de servidor (Server-side).

---

## ✨ Funcionalidades Principais

- 🔗 **Algoritmo de Encurtamento:** Geração automática de códigos curtos (hashes alfanuméricos de 6 caracteres) com verificação de colisão no banco de dados, além de suporte para *aliases* personalizados fornecidos pelo usuário.
- 🔀 **Redirecionamento Rápido:** Endpoint otimizado na raiz da aplicação para capturar o código curto, validar as regras e redirecionar o usuário para a URL original (HTTP 302).
- 🛡️ **Segurança e Autenticação:** Sistema de login e registro protegido por tokens JWT (JSON Web Token), garantindo que apenas usuários autenticados possam gerenciar, editar ou excluir seus próprios links.
- 📏 **Validação de Regras de Negócio:** - **Limite de Cliques:** Bloqueio automático do redirecionamento caso a URL atinja o número máximo de acessos configurado.
  - **Data de Expiração:** Invalidação de links que ultrapassaram a data limite estipulada pelo criador.
  - **Controle de Status:** Capacidade de ativar ou desativar links manualmente.
- 📊 **Rastreamento em Tempo Real:** Incremento dinâmico e seguro da contagem de cliques a cada redirecionamento bem-sucedido.

---

## 🛠️ Tecnologias Utilizadas

A stack tecnológica foi selecionada com base em padrões de mercado para aplicações corporativas escaláveis:

- **Java 17+**: Linguagem principal, utilizando recursos modernos de tipagem e performance.
- **Spring Boot 3.x**: Framework base para injeção de dependências, roteamento e configuração simplificada.
- **Spring Security & JWT**: Implementação de filtros de segurança e proteção de rotas via tokens *stateless*.
- **Spring Data JPA / Hibernate**: Mapeamento Objeto-Relacional (ORM) para abstração das consultas ao banco de dados.
- **Banco de Dados**: PostgreSQL / MySQL *(configurável via `application.properties`)*.
- **Springdoc OpenAPI (Swagger)**: Geração automatizada da documentação interativa da API.
- **Lombok**: Redução de código boilerplate (Getters, Setters, Construtores).

---

## 📚 Documentação da API (Swagger)

A API possui uma documentação interativa e visual gerada pelo Swagger (OpenAPI), facilitando o teste e a integração por parte dos desenvolvedores Frontend.

Para acessar a documentação, rode a aplicação localmente e acesse em seu navegador:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### Resumo dos Principais Endpoints:

* **Autenticação (`/auth`)**
    * `POST /auth/login` - Autentica o usuário e retorna o token JWT.
    * `POST /auth/register` - Cadastra um novo usuário no sistema.
* **Gestão de URLs (`/api/urls`)**
    * `POST /api/urls` - Cria uma nova URL encurtada (requer JWT).
    * `GET /api/urls` - Lista todas as URLs do usuário logado (requer JWT).
* **Redirecionamento (`/`)**
    * `GET /{shortCode}` - Endpoint público que recebe o código e redireciona para o link original.

---

## ⚙️ Instruções de Execução

### Pré-requisitos
* **Java Development Kit (JDK) 17** ou superior instalado.
* **Maven** instalado (ou uso do Maven Wrapper incluso no projeto).
* Banco de dados relacional em execução (ou H2 em memória configurado).

### Variáveis de Ambiente e Configuração
Antes de rodar, verifique o arquivo `src/main/resources/application.properties` (ou `.env`). Você precisará configurar as credenciais do banco e a chave secreta do JWT:

```properties
spring.application.name=TinyRoute
server.port=8080

# Segurança e Autenticação
jwt.secret=MINHA_CHAVE_SUPER_SECRETA_COM_MAIS_DE_32_CARACTERES
cors.allowed-origins=http://localhost:4200

# Configurações de Redirecionamento
app.base-url=[http://tiny.route:8080/api/urls/r](http://tiny.route:8080/api/urls/r)

# Configurações do Banco de Dados (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/Tinyroute_DB
spring.datasource.username=postgres
spring.datasource.password=123

# Configurações do Hibernate / JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Encoding
spring.servlet.encoding.charset=UTF-8
spring.servlet.encoding.force=true
```

### Comandos para Iniciar

1. Clone o repositório:

```bash
git clone [https://github.com/SEU_USUARIO/tiny-route-backend.git](https://github.com/SEU_USUARIO/tiny-route-backend.git)

```

2. Acesse o diretório do projeto:

```bash
cd tiny-route-backend

```

3. Compile e baixe as dependências:

```bash
./mvnw clean install

```

4. Inicie o servidor:

```bash
./mvnw spring-boot:run

```

A API iniciará na porta padrão `8080`.

---

## 📂 Estrutura do Projeto

A arquitetura de pacotes segue o padrão **MVC / Layered Architecture** (Arquitetura em Camadas) do Spring Boot, promovendo baixo acoplamento e alta coesão:

```text
src/main/java/com/url/tinyroute/
├── config/           # Configurações globais (CorsConfig, Swagger, Beans)
├── controllers/      # Endpoints REST (Auth, UrlController, RedirectController)
├── dtos/             # Data Transfer Objects (Requests e Responses formatados)
├── models/           # Entidades de Banco de Dados (User, ShortUrl)
├── repositories/     # Interfaces do Spring Data JPA
├── security/         # Filtros JWT, UserDetailsService e configurações de Auth
└── services/         # Lógica de negócio e regras de encurtamento

```

---

## 🏛️ Integração e Arquitetura (Visão do Desenvolvedor)

Este projeto foi desenvolvido num contexto de equipe onde a **separação de responsabilidades (Separation of Concerns)** foi rigorosamente aplicada. Atuei como o engenheiro responsável pela totalidade da construção do **Backend**, desenhando a API para ser agnóstica em relação ao cliente e altamente escalável.

### Diretrizes Arquiteturais:

* **Stateless & JWT:** A API não guarda sessão (estado) do cliente. Toda requisição autenticada é validada pelo token JWT recebido no cabeçalho `Authorization`, facilitando a escalabilidade horizontal.
* **Padrão DTO (Data Transfer Object):** Nenhuma entidade de banco de dados (`@Entity`) é exposta diretamente aos *Controllers*. Utilizei classes de resposta específicas (ex: `ShortUrlResponse`) para garantir que dados sensíveis não vazem para o Frontend.
* **Tratamento Global de Erros:** Exceções de negócio (URL não encontrada, link expirado) são capturadas por um `@ControllerAdvice`, garantindo respostas JSON padronizadas com os códigos HTTP corretos (ex: 404 Not Found, 403 Forbidden).
* **CORS Configurado:** O arquivo `CorsConfig.java` foi devidamente configurado para permitir a comunicação fluida com o Frontend em Angular (rodando em `http://localhost:4200`), evitando bloqueios de segurança do navegador durante a fase de integração.

### [👉 Projeto FullStack:](https://github.com/PablineSantos/TinyRoute))
