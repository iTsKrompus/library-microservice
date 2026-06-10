# 📚 Library Microservices

Aplicación de gestión de biblioteca desarrollada con arquitectura de microservicios.

## 🏗️ Arquitectura

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │   :8080         │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┐
            ▼                ▼                ▼
    ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
    │ book-service │ │ user-service │ │ loan-service │
    │    :8081     │ │    :8082     │ │    :8083     │
    │              │ │              │ │              │
    │   H2 DB      │ │   H2 DB      │ │   H2 DB      │
    └──────────────┘ └──────────────┘ └──────────────┘
```

## 🛠️ Stack Tecnológico

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud Gateway** (API Gateway)
- **Spring Data JPA** + H2 (base de datos en memoria)
- **Lombok** (reducción de boilerplate)
- **Docker** + **Docker Compose**

## 🚀 Cómo ejecutar

### Opción 1: Docker Compose (recomendado)

```bash
# Construir y levantar todos los servicios
docker-compose up --build

# En background
docker-compose up --build -d

# Ver logs
docker-compose logs -f

# Parar
docker-compose down
```

### Opción 2: Ejecución local

Requiere Java 17 y Maven instalados.

```bash
# Terminal 1 - Book Service
cd book-service && mvn spring-boot:run

# Terminal 2 - User Service
cd user-service && mvn spring-boot:run

# Terminal 3 - Loan Service
cd loan-service && mvn spring-boot:run

# Terminal 4 - API Gateway
cd api-gateway && mvn spring-boot:run
```

## 📡 Endpoints

Todos los endpoints son accesibles a través del **API Gateway** en `http://localhost:8080`.

### 📖 Books (`/api/books`)

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/books` | Listar todos los libros |
| GET | `/api/books/{id}` | Obtener libro por ID |
| GET | `/api/books/isbn/{isbn}` | Buscar por ISBN |
| GET | `/api/books/search?title=` | Buscar por título |
| GET | `/api/books/available` | Libros disponibles |
| POST | `/api/books` | Crear libro |
| PUT | `/api/books/{id}` | Actualizar libro |
| DELETE | `/api/books/{id}` | Eliminar libro |

### 👤 Users (`/api/users`)

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| GET | `/api/users/email/{email}` | Buscar por email |
| GET | `/api/users/search?name=` | Buscar por nombre |
| GET | `/api/users/active` | Usuarios activos |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario |

### 📋 Loans (`/api/loans`)

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/loans` | Listar todos los préstamos |
| GET | `/api/loans/{id}` | Obtener préstamo por ID |
| GET | `/api/loans/user/{userId}` | Préstamos de un usuario |
| GET | `/api/loans/active` | Préstamos activos |
| GET | `/api/loans/overdue` | Préstamos vencidos |
| POST | `/api/loans` | Crear préstamo |
| PATCH | `/api/loans/{id}/return` | Devolver libro |

## 🧪 Ejemplos de uso con cURL

### Crear un libro
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Microservices Patterns",
    "author": "Chris Richardson",
    "isbn": "9781617294549",
    "publishedYear": 2018,
    "totalCopies": 3
  }'
```

### Crear un usuario
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "María Pérez",
    "email": "maria.perez@email.com",
    "phone": "600555666"
  }'
```

### Crear un préstamo
```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "bookId": 1
  }'
```

### Devolver un libro
```bash
curl -X PATCH http://localhost:8080/api/loans/1/return
```

## 🗃️ Consolas H2 (modo local)

| Servicio | URL |
|---------|-----|
| book-service | http://localhost:8081/h2-console |
| user-service | http://localhost:8082/h2-console |
| loan-service | http://localhost:8083/h2-console |

**Credenciales:** usuario `sa`, contraseña `password`

## 💡 Conceptos aplicados

- **Microservicios**: cada servicio tiene su propio dominio, BD y despliegue independiente
- **API Gateway**: punto único de entrada con enrutamiento y circuit breaker
- **REST**: comunicación síncrona entre servicios mediante RestTemplate
- **DTO Pattern**: separación entre entidades y objetos de transferencia
- **Manejo de excepciones**: GlobalExceptionHandler con respuestas HTTP apropiadas
- **Validación**: Bean Validation con @Valid
- **Datos de prueba**: CommandLineRunner para seed data
- **Containerización**: Dockerfile multi-stage + Docker Compose
