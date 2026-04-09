# Demo Project Repository Map and Access Boundaries

This project is a Kotlin Multiplatform monorepo organized as app entrypoints, shared core modules, and feature modules.

The goal of this README is to define:
- what each module/repository is responsible for,
- what it can depend on,
- and what it must not connect to or access.

## High-Level Dependency Direction

Use this as the default architectural rule:

`composeApp` -> `feature:*` and `core:*`  
`feature:*` -> `core:ui`, `core:domain`  
`core:data` -> `core:domain`, `core:databaseMeta`, `core:databaseWorld`  
`core:ui` -> `core:domain`  
`core:domain` -> no project modules  
`core:database*` -> no feature modules

If a module dependency is not listed above, it should be considered disallowed.

## Modules (Repositories), Purpose, and Forbidden Access

### `composeApp`
- **Purpose:** Application shell, top-level DI wiring, root navigation, and platform app startup.
- **Should connect to:** `core:data`, `core:ui`, `core:databaseMeta`, `core:databaseWorld`, and selected features.
- **Must not connect/access:**
  - Must not contain business rules that belong in `core:domain`.
  - Must not implement persistence logic that belongs in `core:data` or `core:database*`.
  - Must not bypass repository interfaces to perform direct SQL logic in feature flows.

### `iosApp`
- **Purpose:** Native iOS entrypoint and SwiftUI integration layer.
- **Should connect to:** Shared framework output from `composeApp`.
- **Must not connect/access:**
  - Must not duplicate shared business logic from Kotlin modules.
  - Must not access shared database files directly outside shared abstractions.

### `core:domain`
- **Purpose:** Pure domain layer (models, repository contracts, use cases, domain utilities).
- **Should connect to:** Kotlin/stdlib and cross-platform utility libraries only.
- **Must not connect/access:**
  - Must not depend on `feature:*`, `core:data`, `core:ui`, or `core:database*`.
  - Must not import SQLDelight schema/query classes.
  - Must not include Compose UI code or Android/iOS framework specifics.

### `core:data`
- **Purpose:** Repository implementations and data orchestration between domain contracts and storage drivers.
- **Should connect to:** `core:domain`, `core:databaseMeta`, `core:databaseWorld`.
- **Must not connect/access:**
  - Must not depend on `feature:*`.
  - Must not host Compose UI or navigation logic.
  - Must not expose database-specific entities directly to UI layers; map to domain models.

### `core:ui`
- **Purpose:** Shared design system/theming and reusable UI utilities.
- **Should connect to:** `core:domain` (for theme/domain-facing contracts only).
- **Must not connect/access:**
  - Must not depend on `core:data` or `core:database*`.
  - Must not query persistence directly.
  - Must not contain feature-specific business workflows.

### `core:databaseMeta`
- **Purpose:** SQLDelight schema and drivers for metadata (guilds, save slot metadata, etc.).
- **Should connect to:** SQLDelight runtime/drivers and DI setup.
- **Must not connect/access:**
  - Must not depend on feature modules.
  - Must not depend on `core:data` or `core:domain` business logic.
  - Must not contain UI logic.

### `core:databaseWorld`
- **Purpose:** SQLDelight schema and file/driver management for world-save content.
- **Should connect to:** SQLDelight runtime/drivers and world database file management.
- **Must not connect/access:**
  - Must not depend on feature modules.
  - Must not contain use-case/business orchestration (belongs to domain/data layers).
  - Must not contain UI logic.

### `feature:account`
- **Purpose:** Account/guild picker flows and related UI/view-model state.
- **Should connect to:** `core:domain`, `core:ui`.
- **Must not connect/access:**
  - Must not depend directly on `core:data` or `core:database*`.
  - Must not define persistence schema or SQL.
  - Must not own cross-feature app shell concerns (root DI/app startup).

### `feature:game`
- **Purpose:** Game screens, game flow, and in-game save interactions at feature level.
- **Should connect to:** `core:domain`, `core:ui`.
- **Must not connect/access:**
  - Must not depend directly on `core:data` or `core:database*`.
  - Must not perform direct DB driver/file operations.
  - Must not redefine shared theme primitives from `core:ui`.

### `feature:save`
- **Purpose:** Reserved for save-focused feature UI/workflows (currently scaffolded with no `commonMain` implementation).
- **Should connect to:** `core:domain`, `core:ui` when implemented.
- **Must not connect/access:**
  - Must not bypass domain contracts to talk directly to DB modules.
  - Must not duplicate save-domain behavior that should remain in use cases/repositories.

## Domain Repository Contracts and Intended Ownership

These repository interfaces live under `core:domain` and define data access boundaries:

### `GuildRepository`
- **Intended purpose:** Guild/account metadata CRUD (`getAll`, `insert`, `delete`).
- **Must not have access to:** world-save file internals or UI state.

### `SaveSlotRepository`
- **Intended purpose:** Save-slot metadata lifecycle (`create`, `list`, `get`, `markSaved`, `delete`).
- **Must not have access to:** presentation/view-model concerns or navigation.

### `WorldSaveRepository`
- **Intended purpose:** Open/close/load/save world state and world file lifecycle operations.
- **Must not have access to:** feature navigation logic or screen/UI state.

### `ThemePreferenceRepository`
- **Intended purpose:** Theme preference read/write via a stable domain contract.
- **Must not have access to:** feature-specific business data or SQL schema concerns.

### `CloudSaveSyncService`
- **Intended purpose:** Contract for cloud sync operations (upload/download/list remote saves).
- **Must not have access to:** UI orchestration, direct screen state, or hard-coded platform UI dependencies.

## Boundary Guardrails for Contributors

When adding new code:
- add interfaces and use-cases in `core:domain`,
- add implementations in `core:data`,
- keep SQL/schema/driver details in `core:database*`,
- keep presentation logic in `feature:*`,
- keep app composition and startup in `composeApp`.

Any new dependency that violates these boundaries should be treated as an architectural issue and reviewed before merge.