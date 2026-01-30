# Skyla Advance POS Mobile

A modern, modular Android Point of Sale (POS) application built with Kotlin, Jetpack Compose, and Clean Architecture. Designed for retail businesses to manage sales transactions, products, customers, inventory, and reporting from a mobile device.

## Features

- **Point of Sale** - Full transaction flow: product search, cart management, quantity controls, discounts, payment processing, and receipt generation
- **Sales Management** - Browse, filter, and manage sales by status (Draft, Completed, Voided)
- **Product Catalog** - CRUD operations with search, pagination, barcode/SKU lookup, and stock tracking
- **Customer Management** - Customer directory with contact info, loyalty points, and spending history
- **Category Management** - Organize products into categories
- **Inventory Control** - Stock level tracking with adjustment history
- **Payment Processing** - Multi-method support (Cash, Card, E-Wallet) with split payments and change calculation
- **Receipt & Void** - Digital receipts with print/share placeholders, and sale voiding with reason tracking
- **Authentication** - Token-based login with automatic refresh and password management
- **User Management** - Admin user operations (scaffold ready)
- **Dashboard & Reports** - Business analytics (scaffold ready)

## Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| Language | Kotlin | 2.1.0 |
| UI | Jetpack Compose (Material 3) | BOM 2024.12.01 |
| DI | Hilt (Dagger) | 2.53.1 |
| Networking | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| Serialization | Kotlinx Serialization | 1.7.3 |
| Navigation | Navigation Compose | 2.8.5 |
| Async | Kotlin Coroutines + Flow | 1.9.0 |
| State | StateFlow / SharedFlow | - |
| Storage | DataStore Preferences | 1.1.1 |
| Images | Coil | 2.7.0 |
| Logging | Timber | 5.0.1 |
| Build | Gradle (Kotlin DSL) | 8.11.1 |
| AGP | Android Gradle Plugin | 8.7.3 |
| SDK | Compile 35 / Min 24 / Target 35 | - |

## Architecture

The project follows **modular Clean Architecture** with MVVM pattern:

```
┌─────────────────────────────────────────────────────────┐
│                        :app                             │
│  Navigation (NavHost, Screen, BottomNavBar)             │
│  DI (AppModule), Application, MainActivity              │
├─────────────────────────────────────────────────────────┤
│                   Feature Modules                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │  :auth   │ │ :sales   │ │:payments │ │:products │   │
│  ├──────────┤ ├──────────┤ ├──────────┤ ├──────────┤   │
│  │  :custo- │ │:categor- │ │:invento- │ │ :users   │   │
│  │  mers    │ │  ies     │ │  ry      │ │          │   │
│  ├──────────┤ ├──────────┤ ├──────────┤ ├──────────┤   │
│  │:dashboard│ │ :reports │ │          │ │          │   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
├─────────────────────────────────────────────────────────┤
│                    Core Modules                         │
│  ┌─────────────┐ ┌──────────┐ ┌─────────┐ ┌────────┐   │
│  │ :core-common│ │:core-model│ │:core-net│ │:core-ui│   │
│  │ Resource,   │ │ Sale,     │ │ Retrofit│ │ Skyla* │   │
│  │ UiState,    │ │ Product,  │ │ OkHttp, │ │ compo- │   │
│  │ Formatters, │ │ Customer, │ │ SafeApi │ │ nents, │   │
│  │ Constants   │ │ Payment   │ │ Auth    │ │ Theme  │   │
│  └─────────────┘ └──────────┘ └─────────┘ └────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Each Feature Module Follows:

```
feature-{name}/
├── data/
│   ├── api/          # Retrofit API service interface
│   ├── dto/          # Request/Response DTOs (@Serializable)
│   └── repository/   # Repository implementation
├── di/               # Hilt module (@Module, @InstallIn)
├── domain/
│   └── repository/   # Repository interface (abstract contract)
└── presentation/
    ├── Screen.kt     # Jetpack Compose UI
    └── ViewModel.kt  # StateFlow + SharedFlow state management
```

### Data Flow

```
Screen (Compose) → ViewModel (StateFlow) → Repository (interface)
                                                  ↓
                                          RepositoryImpl → ApiService → Retrofit → API
                                                  ↓
                                          safeApiCall() → Resource<T> (Success|Error|Loading)
```

## Module Dependency Graph

```
:app
 ├── :core:core-common
 ├── :core:core-model
 ├── :core:core-network
 ├── :core:core-ui
 ├── :feature:feature-auth
 ├── :feature:feature-sales ──→ :feature:feature-products
 ├── :feature:feature-payments
 ├── :feature:feature-products
 ├── :feature:feature-categories
 ├── :feature:feature-customers
 ├── :feature:feature-inventory
 ├── :feature:feature-dashboard
 ├── :feature:feature-users
 └── :feature:feature-reports

:feature:feature-* (each)
 ├── :core:core-common
 ├── :core:core-model
 ├── :core:core-network
 └── :core:core-ui
```

## POS Transaction Flow

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Sales List  │────→│  POS Screen  │────→│   Payment    │────→│   Receipt    │
│              │     │              │     │   Screen     │     │   Screen     │
│ - Filter by  │     │ - Search     │     │ - Cash/Card/ │     │ - Sale info  │
│   status     │     │   products   │     │   E-Wallet   │     │ - Items list │
│ - Pagination │     │ - Add to cart│     │ - Split pay  │     │ - Totals     │
│ - FAB: new   │     │ - Qty +/-    │     │ - Change     │     │ - Payments   │
│   sale       │     │ - Discount   │     │   calc       │     │ - Void sale  │
│              │     │ - Pay button │     │ - Complete   │     │ - Print/Share│
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
       ↑                                                              │
       └──────────────────────────────────────────────────────────────┘
                              Back to Sales List
```

## Navigation

### Bottom Navigation Tabs

| Tab | Icon | Destination |
|-----|------|-------------|
| POS | Shopping Cart | Sales List |
| Products | Inventory | Product List |
| Customers | People | Customer List |
| More | Menu | More Menu (Dashboard, Categories, Inventory, Users, Reports, Settings) |

### Route Definitions

| Screen | Route | Parameters |
|--------|-------|------------|
| Login | `login` | - |
| Sales List | `sales_list` | - |
| POS | `pos?saleId={saleId}` | `saleId` (optional) |
| Payment | `payment/{saleId}` | `saleId` (required) |
| Receipt | `receipt/{saleId}` | `saleId` (required) |
| Products | `products` | - |
| Product Detail | `product/{productId}` | `productId` |
| Customers | `customers` | - |
| Dashboard | `dashboard` | - |
| Reports | `reports` | - |

## API Configuration

The app connects to a REST API backend:

- **Base URL**: `http://10.0.2.2:3000/api/v1/` (Android emulator localhost)
- **Auth**: Bearer token with automatic refresh on 401
- **Serialization**: JSON with `snake_case` field names via `@SerialName`
- **Currency**: All monetary values stored as `Long` in cents (e.g., `1599` = $15.99)
- **Pagination**: Page-based with `page`, `per_page` query params and `PaginationMeta` response

### API Endpoints

#### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Login with email/password |
| POST | `/auth/logout` | Logout (invalidate token) |
| POST | `/auth/refresh` | Refresh access token |
| PUT | `/auth/change-password` | Change password |

#### Sales
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/sales` | Create new sale |
| GET | `/sales` | List sales (paginated, filterable) |
| GET | `/sales/{id}` | Get sale details |
| POST | `/sales/{id}/items` | Add item to sale |
| PUT | `/sales/{id}/items/{itemId}` | Update sale item |
| DELETE | `/sales/{id}/items/{itemId}` | Remove sale item |
| PUT | `/sales/{id}/discount` | Apply discount |
| POST | `/sales/{id}/complete` | Complete sale |
| POST | `/sales/{id}/void` | Void sale |
| GET | `/sales/{id}/receipt` | Get receipt |

#### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/sales/{saleId}/payments` | Add payment |
| GET | `/sales/{saleId}/payments` | List payments |
| GET | `/sales/{saleId}/payments/summary` | Payment summary |

#### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/products` | List products (search, paginated) |
| GET | `/products/{id}` | Get product |
| POST | `/products` | Create product |
| PUT | `/products/{id}` | Update product |
| DELETE | `/products/{id}` | Delete product |
| GET | `/products/barcode/{code}` | Lookup by barcode |
| GET | `/products/sku/{sku}` | Lookup by SKU |

#### Customers
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/customers` | List customers |
| GET | `/customers/{id}` | Get customer |
| POST | `/customers` | Create customer |
| PUT | `/customers/{id}` | Update customer |

#### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/categories` | List categories |
| POST | `/categories` | Create category |
| PUT | `/categories/{id}` | Update category |
| DELETE | `/categories/{id}` | Delete category |

#### Inventory
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/inventory` | List inventory |
| POST | `/inventory/adjustments` | Create adjustment |
| GET | `/inventory/adjustments` | List adjustments |

## Reusable UI Components

All components are in `:core:core-ui` with the `Skyla` prefix:

| Component | Description |
|-----------|-------------|
| `SkylaButton` | Primary action button with loading state |
| `SkylaOutlinedButton` | Secondary outlined button |
| `SkylaTextField` | Form input with label, error, and keyboard support |
| `SkylaSearchBar` | Search input with debounce and clear button |
| `SkylaCard` | Elevated card container with optional click |
| `SkylaTopBar` | Top app bar with back button and actions |
| `SkylaMoneyText` | Currency display (converts cents to formatted string) |
| `SkylaStatusChip` | Colored status badge (Sale/Active status) |
| `SkylaPaginatedList` | Lazy list with infinite scroll and empty state |
| `SkylaLoadingScreen` | Full-screen loading indicator |
| `SkylaLoadingOverlay` | Semi-transparent loading overlay |
| `SkylaErrorView` | Error display with retry button |
| `SkylaEmptyView` | Empty state with icon and message |
| `SkylaDialog` | Confirm/Alert dialog variants |
| `SkylaBottomSheet` | Modal bottom sheet |

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or later
- JDK 17
- Android SDK 35
- A running backend API at `http://localhost:3000/api/v1/`

### Build & Run

```bash
# Clone the repository
git clone git@github.com:maulanasdqn/skyla-advance-pos-mobile.git
cd skyla-advance-pos-mobile

# Build the project
./gradlew build

# Run on connected device/emulator
./gradlew installDebug
```

### Configuration

To change the API base URL, edit `core/core-common/src/main/kotlin/com/skyla/pos/common/Constants.kt`:

```kotlin
const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/api/v1/"
```

> `10.0.2.2` is the Android emulator alias for the host machine's `localhost`.

## Project Structure

```
skyla-advance-pos-mobile/
├── app/                          # Application module
│   └── src/main/kotlin/.../
│       ├── navigation/           # NavHost, Screen routes, BottomNavBar
│       ├── di/                   # AppModule (TokenManager binding)
│       ├── data/                 # TokenManagerImpl (DataStore)
│       ├── ui/                   # SettingsScreen, MoreMenuScreen
│       ├── MainActivity.kt
│       └── SkylaApplication.kt
├── core/
│   ├── core-common/              # Resource, UiState, formatters, constants
│   ├── core-model/               # Domain models (Sale, Product, Customer, etc.)
│   ├── core-network/             # Retrofit, OkHttp, auth interceptor, safeApiCall
│   └── core-ui/                  # Reusable Compose components, Material 3 theme
├── feature/
│   ├── feature-auth/             # Login, Change Password
│   ├── feature-sales/            # Sales List, POS, Receipt
│   ├── feature-payments/         # Payment processing
│   ├── feature-products/         # Product CRUD
│   ├── feature-categories/       # Category CRUD
│   ├── feature-customers/        # Customer CRUD
│   ├── feature-inventory/        # Inventory management
│   ├── feature-dashboard/        # Dashboard (scaffold)
│   ├── feature-users/            # User management (scaffold)
│   └── feature-reports/          # Reports (scaffold)
├── gradle/
│   ├── libs.versions.toml        # Version catalog
│   └── wrapper/
├── build.gradle.kts              # Root build config
├── settings.gradle.kts           # Module includes
└── gradlew                       # Gradle wrapper
```

## Implementation Status

| Feature | Data Layer | UI | Navigation |
|---------|-----------|-----|------------|
| Authentication | Done | Done | Done |
| Sales List | Done | Done | Done |
| POS Screen | Done | Done | Done |
| Payment | Done | Done | Done |
| Receipt | Done | Done | Done |
| Products | Done | Done | Placeholder |
| Categories | Done | Done | Placeholder |
| Customers | Done | Done | Placeholder |
| Inventory | Done | Done | Placeholder |
| Dashboard | - | - | Placeholder |
| Users | - | - | Placeholder |
| Reports | - | - | Placeholder |

## License

This project is proprietary. All rights reserved.
