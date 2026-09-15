# Oil Commerce Platform - Enterprise Backend (Spring Boot 3.5)

Production-ready Enterprise Spring Boot backend for an **Oil Commerce ERP + E-Commerce Platform**.
Serves both the **Customer Web Store** (`oil-commerce-frontend-web`) and the **ERP Admin Panel** (`oil-commerce-admin-panel`).

---

## 🌟 Business Domain

### Brands
- **Nisha Pure Oils** (Cold-pressed / Wood-pressed pure oils)
- **Varshini Gold** (Refined & traditional cooking oils)

### Product Categories
- Groundnut Oil (Kadala Ennai)
- Coconut Oil (Thengai Ennai)
- Sesame Oil / Gingelly Oil (Nallennai)
- Castor Oil (Vilakkennai)
- Lamp Oil / Puja Oil (Deepam Ennai)
- Neem Oil (Veppennai)
- Mahua Oil (Iluppai Ennai)
- Palm Oil
- Burfi (Groundnut Chikki / Sweet confectionery)
- Oil Cake (Kadala Pinnakku / Cattle feed byproduct)

### Supported Package Variants
- Liquid: `100ml`, `200ml`, `500ml`, `1L`, `2L`, `5L`, `15L` (Tin/Can)
- Weight: `5Kg`, `15Kg` (Bulk/Commercial)

---

## 🏗️ Architecture & Technology Stack

| Layer | Technology |
|---|---|
| **Language & Runtime** | Java 21 LTS |
| **Framework** | Spring Boot 3.5 (Spring Security, Spring Data JPA, Spring Web) |
| **Security & Auth** | Stateless JWT (HMAC-SHA256), Role-Based Access Control (RBAC) |
| **Database** | PostgreSQL 16+ with Flyway schema versioning (V1 – V15) |
| **API Documentation** | Swagger / OpenAPI 3.0 (`/swagger-ui/index.html`) |
| **Auditing & Logging** | Hibernate Envers / Spring Data JPA Auditing + AOP Audit Interceptors |
| **Multi-Tenancy** | Schema / Tenant-ID column segregation with automatic request context filtering |
| **DevOps & Container** | Multi-stage Alpine Dockerfile, Docker Compose |

---

## 📂 Project Structure

```
oil-commerce-backend/
├── src/main/java/com/oilcommerce/
│   ├── address/          # Customer delivery & billing addresses
│   ├── audit/            # Audit logging entity, repository, AOP interceptor
│   ├── auth/             # JWT auth, login, register, password reset, refresh
│   ├── brand/            # Brands (Nisha Pure Oils, Varshini Gold)
│   ├── cart/             # Shopping cart & session management
│   ├── category/         # Oil categories & subcategories
│   ├── common/           # BaseEntity, ApiResponse, PaginatedResponse, GlobalExceptionHandler
│   ├── config/           # SecurityConfig, CorsConfig, OpenApiConfig, AuditorAwareConfig
│   ├── coupon/           # Promotional discounts & coupons
│   ├── dashboard/        # Admin ERP revenue, sales, top products, low stock metrics
│   ├── exception/        # ResourceNotFoundException, BusinessException, UnauthorizedException
│   ├── fileupload/       # Product images and attachment storage
│   ├── inventory/        # Stock management, low-stock alerts, stock movement logs
│   ├── notification/     # User notifications (orders, payments, inventory alerts)
│   ├── order/            # Order placement, status tracking (PENDING -> DELIVERED)
│   ├── payment/          # Razorpay / Stripe payment gateway integration & webhook handlers
│   ├── product/          # Product catalog, multi-volume packaging variants, search filters
│   ├── report/           # Sales reports, tax reports, PDF/Excel export endpoints
│   ├── review/           # Customer ratings & reviews
│   ├── security/         # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl
│   ├── sheetsync/        # Google Sheets bidirectional catalog & inventory sync
│   ├── shipment/         # Shipping carriers, tracking numbers, dispatch
│   ├── tenant/           # Multi-tenant tenant configuration & resolution
│   ├── user/             # User profiles, passwords, RBAC management
│   └── wishlist/         # Customer wishlists
├── src/main/resources/
│   ├── db/migration/     # Flyway migrations V1 to V15 (DDL + initial seeds)
│   └── application.yml   # Configuration profiles (dev, prod)
├── Dockerfile            # Alpine Java 21 multi-stage container build
├── docker-compose.yml    # Postgres 16 + Backend container composition
└── pom.xml
```

---

## 🚀 Quick Start

### 1. Run with Docker Compose (Recommended)
```bash
docker-compose up -d
```
This automatically boots:
- PostgreSQL on port `5432` with pre-created `oil_commerce` database
- Flyway migrations applying `V1` to `V15` schema and seed data
- Backend API listening on `http://localhost:8080`

### 2. Run Locally with Maven
```bash
# Build the project
mvn clean package -DskipTests

# Or using the Maven Wrapper:
./mvnw clean package -DskipTests     # Linux / Mac
mvnw.cmd clean package -DskipTests  # Windows

# Run application
java -jar target/oil-commerce-backend-1.0.0.jar
```

---

## 🔑 Default Credentials (Seed Data)

Upon initial Flyway migration `V15`:
- **Admin Account**: `admin@oilcommerce.com` / `Admin@123` (Role: `SUPER_ADMIN`)
- **Default Tenant**: `Main Branch` (`SLUG: default`)

---

## 📡 REST API Directory

Base URL: `http://localhost:8080/api/v1`

### 1. Authentication (`/auth`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/auth/register` | Register new customer | Public |
| `POST` | `/auth/login` | Authenticate and receive JWT token | Public |
| `POST` | `/auth/refresh` | Refresh expired access token | Public |
| `POST` | `/auth/forgot-password` | Request password reset email | Public |
| `POST` | `/auth/reset-password` | Reset password using reset token | Public |
| `GET` | `/auth/me` | Retrieve authenticated user profile | Authenticated |

### 2. Products & Variants (`/products`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/products` | List products with pagination, search, category/brand filters | Public |
| `GET` | `/products/{id}` | Get product details with all volume variants | Public |
| `POST` | `/products` | Create new product | `ADMIN`, `STORE_MANAGER` |
| `PUT` | `/products/{id}` | Update product information | `ADMIN`, `STORE_MANAGER` |
| `DELETE` | `/products/{id}` | Delete product | `SUPER_ADMIN`, `ADMIN` |
| `POST` | `/products/{id}/variants` | Add packaging variant (e.g. 1L, 15L Tin) | `ADMIN` |

### 3. Categories & Brands (`/categories`, `/brands`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/categories` | List active categories | Public |
| `POST` | `/categories` | Create category | `ADMIN` |
| `GET` | `/brands` | List brands (Nisha Pure Oils, Varshini Gold) | Public |
| `POST` | `/brands` | Create brand | `ADMIN` |

### 4. Shopping Cart (`/cart`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/cart` | Get current customer's cart | Authenticated |
| `POST` | `/cart/items` | Add variant to cart | Authenticated |
| `PUT` | `/cart/items/{itemId}` | Update item quantity | Authenticated |
| `DELETE` | `/cart/items/{itemId}` | Remove item from cart | Authenticated |
| `DELETE` | `/cart` | Clear entire cart | Authenticated |

### 5. Orders & Checkout (`/orders`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/orders` | Place order from cart | Authenticated |
| `GET` | `/orders/my-orders` | Get logged-in user order history | Authenticated |
| `GET` | `/orders/{orderNumber}` | Get detailed order status & tracking | Authenticated |
| `GET` | `/orders` | Admin: List all orders with status filter | `ADMIN`, `STORE_MANAGER` |
| `PUT` | `/orders/{id}/status` | Update status (`CONFIRMED`, `SHIPPED`, etc.) | `ADMIN` |
| `POST` | `/orders/{id}/cancel` | Cancel order & restore inventory | Authenticated |

### 6. Payments (`/payments`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/payments/create-order` | Initialize Razorpay / gateway payment | Authenticated |
| `POST` | `/payments/verify` | Verify payment signature and mark order paid | Authenticated |
| `POST` | `/payments/webhook` | Webhook receiver for async gateway status | Public |

### 7. Inventory & Stock Movements (`/inventory`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/inventory` | List current inventory levels | `ADMIN`, `STORE_MANAGER` |
| `GET` | `/inventory/low-stock` | View products below threshold | `ADMIN`, `STORE_MANAGER` |
| `POST` | `/inventory/adjust` | Record stock adjustment / movement | `ADMIN`, `STORE_MANAGER` |

### 8. Admin Dashboard (`/admin/dashboard`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/admin/dashboard/stats` | KPI metrics (Gross Revenue, Orders, Low Stock) | `ADMIN` |
| `GET` | `/admin/dashboard/sales-chart` | Monthly / Weekly revenue chart data | `ADMIN` |
| `GET` | `/admin/dashboard/top-products` | Top 5 best-selling oils | `ADMIN` |

### 9. File Uploads & Google Sheets Sync
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/files/upload` | Upload product images/assets | `ADMIN` |
| `POST` | `/sync/sheets/export` | Export inventory/orders to Google Sheets | `ADMIN` |
| `POST` | `/sync/sheets/import` | Import catalog updates from Google Sheets | `ADMIN` |

---

## 🔒 Security & CORS

The backend is configured to accept requests from both frontend applications:
- **Customer Store**: `http://localhost:4200`
- **Admin Panel**: `http://localhost:4201`

JWT authentication uses standard `Bearer <TOKEN>` in the `Authorization` header.
Tokens are signed using a secure 256-bit key configurable via `JWT_SECRET` environment variable.

---

## 📄 Swagger / OpenAPI UI

Once running, access interactive OpenAPI documentation at:
```
http://localhost:8080/swagger-ui/index.html
```
Raw OpenAPI JSON spec is at:
```
http://localhost:8080/v3/api-docs
```
