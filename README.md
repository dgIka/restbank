# Bank Cards Backend

Учебный проект: backend-сервис для управления банковскими картами.  
Стек: **Java 17, Spring Boot 3, Spring Data JPA, PostgreSQL, Liquibase, Spring Security (JWT), Swagger**.

## Функционал
- Регистрация и логин пользователей (JWT-аутентификация).
- Роли: `USER`, `ADMIN`.
- Админ может:
    - выпускать карты пользователям,
    - блокировать/активировать/удалять карты,
    - видеть все карты.
- Пользователь может:
    - смотреть свои карты (пагинация, фильтр по статусу),
    - запрашивать блокировку карты,
    - делать переводы между своими картами (с идемпотентностью),
    - смотреть баланс.

## Архитектура
- `entity/` — JPA-сущности (User, Role, Card, Transfer).
- `repository/` — Spring Data JPA репозитории.
- `service/` — бизнес-логика (UserService, CardService, TransferService).
- `controller/` — REST-контроллеры (Auth, Cards, Transfers).
- `security/` — JWT, фильтр, конфиг.
- `util/` — вспомогалки (например, PanCrypto для работы с PAN).
- `resources/db/migration` — миграции Liquibase.

## Запуск

### 1. Локально
- Установить **Java 17+** и **PostgreSQL**.
- Создать базу данных, например:

```sql
CREATE DATABASE bankcards;
```

- Настроить доступ в `application.yaml` (`spring.datasource.url`, `username`, `password`).
- Запустить:

```bash
./mvnw spring-boot:run
```

или

```bash
mvn clean package
java -jar target/bank-cards-backend-0.0.1-SNAPSHOT.jar
```

При старте выполняются миграции Liquibase (создаются таблицы, добавляются роли `USER` и `ADMIN`).

### 2. Docker Compose (опционально)
*(TODO: добавить docker-compose.yml с Postgres + backend)*

## API
Документация доступна после запуска:
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Основные эндпоинты
- `POST /api/auth/register` — регистрация.
- `POST /api/auth/login` — логин (возвращает JWT).
- `GET /api/cards` — список карт текущего пользователя.
- `POST /api/cards/issue/{userId}` — выпуск карты (ADMIN).
- `POST /api/cards/{cardId}/block` — блокировка своей карты (USER).
- `POST /api/cards/{cardId}/activate` — активация карты (ADMIN).
- `DELETE /api/cards/{cardId}` — удалить карту (ADMIN).
- `POST /api/transfers` — перевод между своими картами.

## Авторизация
- Все защищённые эндпоинты требуют заголовок:
  ```
  Authorization: Bearer <JWT>
  ```
- JWT получается при логине.

## Примечания
- PAN хранится в базе в виде SHA-256 + pepper (см. `PanCrypto`).
- В ответах номер карты маскируется (`**** **** **** 1234`).
- Переводы сделаны идемпотентными (ключ `Idempotency-Key` в заголовке).

---

### Статус
MVP готов: регистрация, логин, выпуск/просмотр карт, переводы, блокировка.  
Дальше можно допилить:
- Docker Compose,
- обработчик ошибок (`@RestControllerAdvice`),
- тесты.
