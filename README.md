# WebHook_RSA 🔐

Sistema completo de troca de mensagens seguras utilizando WebHooks com criptografia RSA. O projeto implementa uma arquitetura full-stack com três componentes integrados:  API em Rust, frontend Angular e serviço de mensagens Spring Boot.

## 📋 Descrição Detalhada

Este projeto demonstra a implementação de um sistema de mensageria segura com as seguintes características:

- **Criptografia RSA**:  Implementação manual do algoritmo SHA-256 e criptografia assimétrica
- **WebHooks**: Sistema de comunicação assíncrona entre serviços
- **Arquitetura Microserviços**: Três aplicações independentes trabalhando em conjunto
- **API RESTful**: Backend robusto em Rust com Actix-web
- **Frontend Moderno**: Interface responsiva com Angular 19. 2 e SSR (Server-Side Rendering)
- **Spring Boot**: Serviço de mensagens com segurança e retry automático

## 🏗️ Arquitetura do Projeto

```
WebHook_RSA/
├── rust_api/          # API backend em Rust (Actix-web)
│   ├── src/
│   │   └── main.rs    # Implementação SHA-256 manual + endpoints
│   ├── Cargo.toml     # Gerenciador de dependências Rust
│   └── target/        # Compilados
│
├── pagina/            # Frontend Angular 19.2
│   ├── src/
│   │   ├── app/       # Componentes e lógica
│   │   ├── main.ts    # Bootstrap da aplicação
│   │   ├── server.ts  # Servidor SSR com Express
│   │   └── index.html # Página principal
│   ├── package.json   # Dependências Node.js
│   └── angular.json   # Configuração Angular CLI
│
└── Mensagem/          # Serviço de mensagens Spring Boot
    ├── src/
    │   └── main/java/ # Código fonte Java
    ├── build.gradle   # Gerenciador de dependências
    └── gradlew        # Gradle Wrapper
```

## 🔧 Tecnologias Utilizadas

### Backend - Rust API
- **Rust Edition 2024**
- **Actix-web 4.x** - Framework web assíncrono de alta performance
- **Serde 1.0** - Serialização/deserialização JSON
- **Rand 0.8** - Geração de números aleatórios para criptografia

### Frontend - Angular
- **Angular 19.2.0** - Framework principal
- **Angular SSR** - Server-Side Rendering
- **Express 4.18.2** - Servidor Node.js para SSR
- **RxJS 7.8.0** - Programação reativa
- **TypeScript 5.7.2** - Tipagem estática
- **Zone.js 0.15.0** - Detecção de mudanças

### Backend - Serviço de Mensagens
- **Java 21** - Linguagem base
- **Spring Boot 3.4.5** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Retry** - Retry automático em falhas
- **Spring AOP** - Programação orientada a aspectos
- **Lombok** - Redução de boilerplate
- **Spring Boot DevTools** - Hot reload em desenvolvimento

## 📦 Dependências Completas

### Rust API (`rust_api/Cargo.toml`)

```toml
[dependencies]
actix-web = "4"                              # Framework web
serde = { version = "1.0", features = ["derive"] }  # JSON serialization
rand = "0.8"                                 # Random number generation
```

### Frontend Angular (`pagina/package.json`)

**Dependências de Produção:**
```json
"@angular/common": "^19.2.0"
"@angular/compiler":  "^19.2.0"
"@angular/core": "^19.2.0"
"@angular/forms": "^19.2.0"
"@angular/platform-browser": "^19.2.0"
"@angular/platform-browser-dynamic": "^19.2.0"
"@angular/platform-server": "^19.2.0"
"@angular/router": "^19.2.0"
"@angular/ssr": "^19.2.12"
"express": "^4.18.2"
"rxjs": "~7.8.0"
"tslib": "^2.3.0"
"zone.js":  "~0.15.0"
```

**Dependências de Desenvolvimento:**
```json
"@angular-devkit/build-angular": "^19.2.12"
"@angular/cli": "^19.2.12"
"@angular/compiler-cli": "^19.2.0"
"@types/express": "^4.17.17"
"@types/jasmine": "~5.1.0"
"@types/node": "^18.18.0"
"jasmine-core": "~5.6.0"
"karma": "~6.4.0"
"karma-chrome-launcher": "~3.2.0"
"karma-coverage": "~2.2.0"
"karma-jasmine":  "~5.1.0"
"karma-jasmine-html-reporter": "~2.1.0"
"typescript": "~5.7.2"
```

### Serviço de Mensagens (`Mensagem/build.gradle`)

```gradle
// Spring Boot
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-aop'
implementation 'org. springframework.retry:spring-retry'

// Utilities
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'

// Development
developmentOnly 'org. springframework.boot:spring-boot-devtools'

// Testing
testImplementation 'org. springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
```

## 🚀 Instalação e Configuração

### Pré-requisitos

Certifique-se de ter instalado: 

- **Rust** (última versão estável) - [rustup.rs](https://rustup.rs/)
- **Node.js** 18+ e npm - [nodejs.org](https://nodejs.org/)
- **Java JDK 21** - [adoptium.net](https://adoptium.net/)
- **Gradle** (ou use o wrapper incluído)

### 1️⃣ Configurar e Executar a API Rust

```bash
# Navegue até o diretório
cd rust_api

# Compile o projeto
cargo build --release

# Execute o servidor
cargo run

# A API estará rodando em http://localhost:8080 (verificar porta no código)
```

**Recursos da API:**
- Implementação manual de SHA-256
- Endpoints para criptografia RSA
- Sistema de webhooks assíncrono

### 2️⃣ Configurar e Executar o Frontend Angular

```bash
# Navegue até o diretório
cd pagina

# Instale as dependências
npm install

# Execute em modo de desenvolvimento
npm start
# ou
ng serve

# Para build de produção
npm run build

# Para executar com SSR
npm run serve: ssr: pagina
```

**Acesso:**
- Desenvolvimento: `http://localhost:4200/`
- Produção (SSR): `http://localhost:4000/`

**Scripts disponíveis:**
- `npm start` - Servidor de desenvolvimento
- `npm run build` - Build de produção
- `npm run watch` - Build com watch mode
- `npm test` - Executar testes
- `npm run serve: ssr: pagina` - Servidor SSR

### 3️⃣ Configurar e Executar o Serviço de Mensagens

```bash
# Navegue até o diretório
cd Mensagem

# Build do projeto
./gradlew build

# Execute a aplicação
./gradlew bootRun

# Ou no Windows
gradlew.bat bootRun

# A aplicação Spring Boot estará rodando em http://localhost:8080
```

**Características do Serviço:**
- Spring Security configurado
- Retry automático em falhas de comunicação
- Hot reload com DevTools
- Endpoints RESTful protegidos

## 🔐 Funcionalidades Principais

### Criptografia RSA
- ✅ Implementação manual do algoritmo SHA-256
- ✅ Geração de chaves pública/privada
- ✅ Criptografia e descriptografia de mensagens
- ✅ Padding seguro para mensagens

### Sistema de WebHooks
- ✅ Registro de webhooks
- ✅ Envio assíncrono de notificações
- ✅ Retry automático em falhas
- ✅ Validação de assinaturas

### Interface Web
- ✅ Interface responsiva e moderna
- ✅ Server-Side Rendering (SSR) para melhor SEO
- ✅ Tratamento global de erros
- ✅ Feedback visual em tempo real

### Segurança
- ✅ Spring Security integrado
- ✅ Autenticação de requisições
- ✅ Validação de tokens
- ✅ CORS configurado

## 🧪 Testes

### Frontend Angular
```bash
cd pagina
npm test              # Testes unitários com Karma
ng test              # Modo alternativo
```

### Serviço de Mensagens
```bash
cd Mensagem
./gradlew test       # Executa todos os testes
```

### API Rust
```bash
cd rust_api
cargo test           # Executa testes unitários
```

## 🌐 Endpoints da API

### Rust API
```
POST /webhook        - Receber webhook
GET  /status        - Status da API
POST /encrypt       - Criptografar mensagem
POST /decrypt       - Descriptografar mensagem
```

### Spring Boot Service
```
POST /api/messages      - Enviar mensagem
GET  /api/messages/{id} - Buscar mensagem
GET  /api/health       - Health check
```

## 📝 Configuração de Ambiente

Crie arquivos de configuração para cada serviço:

### rust_api/.env
```env
PORT=8080
RUST_LOG=info
```

### pagina/.env
```env
PORT=4000
API_URL=http://localhost:8080
```

### Mensagem/application.properties
```properties
server.port=8081
spring.security.user.name=admin
spring.security.user.password=admin123
```

## 🐛 Debugging

O projeto inclui tratamento avançado de erros:

- **Frontend**: `GlobalErrorHandler` captura erros e exibe no console
- **Rust API**:  Logs detalhados com diferentes níveis
- **Spring Boot**: DevTools permite hot reload e debugging

## 📊 Performance

- **Rust API**: Alta performance com Actix-web assíncrono
- **Angular SSR**: Carregamento inicial otimizado
- **Spring Boot**:  Gerenciamento eficiente de threads

## 🤝 Contribuindo

Contribuições são muito bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto é open source e está disponível sob licença permissiva. 

## 👤 Autor

**KiyotaYuji**
- GitHub: [@KiyotaYuji](https://github.com/KiyotaYuji)
- Projeto: [WebHook_RSA](https://github.com/KiyotaYuji/WebHook_RSA)

## 🙏 Agradecimentos

- Comunidade Rust
- Time do Angular
- Spring Framework
- Todos os contribuidores open source

## 📚 Recursos Adicionais

- [Documentação Actix-web](https://actix.rs/)
- [Documentação Angular](https://angular.dev/)
- [Documentação Spring Boot](https://spring.io/projects/spring-boot)
- [RSA Algorithm](https://en.wikipedia.org/wiki/RSA_(cryptosystem))

---

**Nota**:  Este é um projeto educacional demonstrando conceitos de criptografia, webhooks e arquitetura microserviços.  
