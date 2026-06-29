# Database Design: auth-service and user-service

Tai lieu nay ve lai thiet ke database cho `auth-service` va `user-service` theo Liquibase changelog hien tai.

## Tong quan kien truc du lieu

- `auth-service` su dung PostgreSQL database rieng: `auth_db`.
- `user-service` su dung PostgreSQL database rieng: `user_db`.
- Hai service khong co foreign key vat ly giua database. Lien ket nghiep vu duoc thuc hien bang `account_id` va Kafka event.
- `auth-service.accounts.id` la identity goc cua tai khoan dang nhap.
- `user-service.users.account_id` la ban sao tham chieu logic den `auth-service.accounts.id`.

```mermaid
flowchart LR
    subgraph AUTH_DB["auth_db - auth-service"]
        A["accounts<br/>id UUID"]
        R["roles<br/>id BIGINT"]
        AR["account_roles<br/>account_id UUID<br/>role_id BIGINT"]
        T["account_tokens<br/>user_id UUID"]
        D["device_log<br/>user_id UUID"]
    end

    subgraph USER_DB["user_db - user-service"]
        U["users<br/>id UUID<br/>account_id UUID"]
    end

    A -->|"1-n physical FK"| AR
    R -->|"1-n physical FK"| AR
    A -->|"1-n physical FK"| T
    A -->|"1-n physical FK"| D
    A -. "Kafka create-user event / logical 1-1" .-> U
```

## auth-service ERD

```mermaid
erDiagram
    ACCOUNTS ||--o{ ACCOUNT_ROLES : has
    ROLES ||--o{ ACCOUNT_ROLES : assigned_by
    ACCOUNTS ||--o{ ACCOUNT_TOKENS : owns
    ACCOUNTS ||--o{ DEVICE_LOG : records

    ACCOUNTS {
        UUID id PK
        VARCHAR_255 email UK
        VARCHAR_255 username UK
        VARCHAR_255 password
        INT status
        TIMESTAMP inserted_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        UUID inserted_by
        UUID updated_by
        UUID deleted_by
    }

    ROLES {
        BIGINT id PK
        VARCHAR_255 name UK
        TIMESTAMP inserted_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        UUID inserted_by
        UUID updated_by
        UUID deleted_by
    }

    ACCOUNT_ROLES {
        UUID id PK
        UUID account_id FK
        BIGINT role_id FK
        TIMESTAMP inserted_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        UUID inserted_by
        UUID updated_by
        UUID deleted_by
    }

    ACCOUNT_TOKENS {
        UUID id PK
        UUID user_id FK
        VARCHAR_512 refresh_token UK
        TIMESTAMP issued_at
        TIMESTAMP expires_at
        BOOLEAN is_revoked
        VARCHAR_255 provider
        TIMESTAMP inserted_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        UUID inserted_by
        UUID updated_by
        UUID deleted_by
    }

    DEVICE_LOG {
        UUID id PK
        UUID device_id
        UUID user_id FK
        TIMESTAMP first_seen_at
        TIMESTAMP last_seen_at
        INT status
        TIMESTAMP inserted_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        UUID inserted_by
        UUID updated_by
        UUID deleted_by
    }
```

### auth-service quan he

| Bang nguon | Cot | Bang dich | Cot | Loai |
| --- | --- | --- | --- | --- |
| `account_roles` | `account_id` | `accounts` | `id` | Physical FK |
| `account_roles` | `role_id` | `roles` | `id` | Physical FK |
| `account_tokens` | `user_id` | `accounts` | `id` | Physical FK |
| `device_log` | `user_id` | `accounts` | `id` | Physical FK |

## user-service ERD

```mermaid
erDiagram
    USERS {
        UUID id PK
        UUID account_id
        VARCHAR_255 email UK
        VARCHAR_255 username UK
        VARCHAR_255 first_name
        VARCHAR_255 last_name
        VARCHAR_255 address
        VARCHAR_20 phone_number
        INT status
        TIMESTAMP inserted_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        UUID inserted_by
        UUID updated_by
        UUID deleted_by
    }
```

## Quan he logic giua auth-service va user-service

```mermaid
erDiagram
    AUTH_ACCOUNTS ||--|| USER_USERS : "logical account_id"

    AUTH_ACCOUNTS {
        UUID id PK
        VARCHAR_255 email UK
        VARCHAR_255 username UK
        VARCHAR_255 password
        INT status
    }

    USER_USERS {
        UUID id PK
        UUID account_id "logical ref to AUTH_ACCOUNTS.id"
        VARCHAR_255 email UK
        VARCHAR_255 username UK
        VARCHAR_255 first_name
        VARCHAR_255 last_name
        VARCHAR_255 address
        VARCHAR_20 phone_number
        INT status
    }
```

Luồng tao user hien tai:

1. `auth-service` tao record trong `accounts`.
2. `auth-service` publish event/command tao user qua Kafka, gom `accountId`, `email`, `username`.
3. `user-service` consume event va tao record trong `users` voi `users.account_id = accounts.id`.
4. Neu tao user that bai, co co che rollback/failed event de xu ly buoc bu tru.

## Ghi chu can kiem tra

- Trong entity `User`, `account_id` co `unique = true`, nhung Liquibase changelog cua `user-service` chua khai bao unique constraint cho cot nay. Neu nghiep vu la moi account chi co mot user profile, nen them unique constraint vao migration.
- Trong `account_tokens` va `device_log`, cot `user_id` thuc te dang foreign key den `accounts.id`. Ten `account_id` se ro nghia hon neu sau nay refactor schema.
- Entity `AccountToken` khai bao `expires_at` va `is_revoked` la `nullable = false`, nhung Liquibase changelog hien tai chua dat `nullable="false"` cho hai cot nay.
