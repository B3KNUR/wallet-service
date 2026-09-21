# Wallet Service

Backend service for Spring course.

## Practice 1
- Spring Boot project generated.
- Healthcheck endpoint: `GET /api/v1/status`

## Practice 2
- Added `WalletProperties` with `@ConfigurationProperties`.
- Configured profiles: `dev` and `test` in `application.yml`.
- Config endpoint: `GET /api/v1/wallet/config`

## Practice 3
- Service layer refactored to use constructor injection (`WalletService`).
- No `@Autowired` on private fields; dependencies are `final`.
- Added conditional beans for commission calculation:
    - `StandardCommissionCalculator` (when `commission-enabled: true`)
    - `ZeroCommissionCalculator` (when `commission-enabled: false` or missing)

### How to toggle conditional bean:
Open `src/main/resources/application.yml` and switch active profile:
- `spring.profiles.active: dev` -> sets `commission-enabled: true` and calculates commission based on `commission-fee`.
- `spring.profiles.active: test` -> sets `commission-enabled: false` and uses zero commission.

The application starts successfully in both cases.