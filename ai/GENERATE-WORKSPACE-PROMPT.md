# Prompt — Generate Multi-Agent Agentic Workspace (Claude Code)

---

Please generate a complete multi-agent workspace configuration for Claude Code (CLI).
Produce one `CLAUDE.md` file per repo type, plus shared supporting files.
All files should be ready to drop into their respective repositories with no edits needed.

---

## Project Context

**Stack:**
- Frontend: React + Webpack Module Federation (MFE architecture)
- Backend: Java 21 + Spring Boot 3.x
- Database: Azure SQL Server
- Auth: Azure AD / Entra ID (MSAL)
- Cloud: Microsoft Azure (App Services, Static Web Apps, API Management, App Insights, Key Vault)
- CI/CD: Azure DevOps Pipelines
- API Spec: OpenAPI 3.x (YAML) — API-first, spec is always written before implementation
- Agent Tooling: Claude Code (CLI)

**Architecture principles:**
- TDD (Red → Green → Refactor) enforced in every repo
- API-first: OpenAPI spec in `api-contracts/` is the single source of truth
- Vertical slicing: every feature delivered end-to-end across all layers
- Polyrepo: one Git repository per service or MFE

**Team:** Small (3–6 developers)

**Testing layers (all must be covered):**
- Unit tests: JUnit 5 (backend) / Jest + React Testing Library (frontend)
- Integration tests: Testcontainers with Azure SQL Server container
- Contract tests: Pact (consumer on MFE side, provider on backend side)
- E2E tests: Playwright
- Performance tests: k6

**No event-driven messaging** — REST only between services.

---

## Polyrepo Structure

```
org/
├── api-contracts/        → OpenAPI specs (source of truth for all APIs)
├── api-[domain]/         → One repo per Java/Spring Boot API (e.g. api-users, api-entities)
├── shell-app/            → Webpack Module Federation host (React)
├── mfe-[feature]/        → One repo per MFE remote (React)
├── e2e-tests/            → Playwright E2E suite (cross-MFE journeys)
├── performance-tests/    → k6 performance test suite
└── platform-infra/       → Azure infrastructure (Bicep) + pipeline templates
```

---

## Agent Roster — One CLAUDE.md per Repo Type

| Agent | Repo | Responsibility |
|---|---|---|
| Architect | `api-contracts/` | OpenAPI specs, shared schemas, ADRs, Spectral validation |
| Backend | `api-[domain]/` | Java 21, Spring Boot, JPA, Flyway, Pact provider, Testcontainers |
| Frontend Shell | `shell-app/` | MFE host, Module Federation config, MSAL auth, routing |
| Frontend MFE | `mfe-[feature]/` | MFE remote, generated API clients, Pact consumer, RTL tests |
| QA | `e2e-tests/` | Playwright journeys, Pact can-i-deploy, accessibility (axe) |
| QA Performance | `performance-tests/` | k6 load/stress/spike tests, SLO thresholds |
| Platform | `platform-infra/` | Bicep modules, reusable pipeline templates, Key Vault, App Insights |

---

## Files to Generate

Please generate ALL of the following files with complete, production-ready content:

### 1. `WORKSPACE-OVERVIEW.md` (root level)
Include:
- Full polyrepo directory tree
- Agent roster table
- Agent collaboration flow diagram (ASCII) showing: Architect → Backend + Frontend (parallel) → QA → Platform
- Handoff protocol between phases
- How to activate each agent (`cd [repo] && claude`)

### 2. `SHARED-CONVENTIONS.md` (root level, read by all agents)
Include:
- Team context summary
- API-first rule (non-negotiable)
- TDD rule (non-negotiable)
- Vertical slicing definition
- Definition of Done checklist
- Git branch naming convention
- Conventional Commits format with examples
- PR rules (size limit, tags, merge strategy)
- Security baseline (Key Vault, HTTPS, no PII in logs)
- Azure DevOps pipeline trigger template

### 3. `AGENT-ORCHESTRATION-GUIDE.md` (root level)
Include:
- Mental model: human as orchestrator, Claude Code as agent per terminal
- How to write a story/epic as input (template)
- Phase-by-phase orchestration for a full feature (example: GET /entities + React dropdown)
  - Phase 1: Architect (blocking gate)
  - Phase 2: Backend + Frontend in parallel (exact prompts to give each agent)
  - Phase 3: QA (Pact can-i-deploy + Playwright)
  - Phase 4: Platform (APIM sync, pipeline check, slot swap)
- Parallel execution diagram showing what can run simultaneously
- 7 principles for writing good agent prompts
- How to handle cross-agent blockers (spec change mid-flight, Pact mismatch)
- Release checklist (human-owned)

### 4. `api-contracts/CLAUDE.md` — Architect Agent
Include:
- Role identity and authority statement
- Repo directory structure
- Responsibilities: create spec, modify spec, review spec
- OpenAPI file header template
- Azure AD / Entra ID security scheme (oauth2 authorizationCode)
- Naming conventions table (paths, operationId, schemas, params, headers)
- Cursor-based pagination standard (schemas + query params)
- ProblemDetail error schema (RFC 7807) with traceId field for App Insights
- Required responses on every endpoint ($ref to shared components)
- Spectral validation command
- Spectral rules to enforce (list)
- ADR storage convention
- Anti-patterns list

### 5. `api-[domain]/CLAUDE.md` — Backend Agent
Include:
- Role identity and TDD rule
- Repo directory structure (layered: api, application, domain, infrastructure)
- Full tech stack with versions
- Code standards with examples:
  - Thin controller (implements generated interface from OpenAPI Generator)
  - Application service pattern
  - Pure domain model (records)
  - Global exception handler returning ProblemDetail
  - Azure AD resource server security config
  - CorrelationId MDC filter
- TDD workflow: order of test writing (unit → slice → integration → contract)
- Unit test example (Mockito)
- @WebMvcTest controller slice test example (with JWT mock)
- Testcontainers integration test example (Azure SQL Server container)
- Pact provider test example
- Database rules (Flyway only, Azure SQL types, readOnly transactions, no ddl-auto)
- Observability setup (App Insights starter, structured logging, Actuator endpoints)
- Azure DevOps pipeline YAML (build → test → contract → deploy stages)
- Anti-patterns list

### 6. `shell-app/CLAUDE.md` — Frontend Shell Agent
Include:
- Role identity and Module Federation host rules
- Repo directory structure
- Webpack Module Federation host config (with shared singletons: React, ReactDOM, react-router-dom, MSAL)
- Lazy remote import pattern with ErrorBoundary and Suspense
- MSAL React auth setup (PublicClientApplication config)
- ProtectedRoute component pattern with role checking
- API client generation command (openapi-generator-cli, typescript-fetch)
- Authenticated fetch wrapper (token acquisition + X-Correlation-Id header injection)
- Jest + RTL test example (ProtectedRoute)
- Playwright auth fixture (for sharing with e2e-tests)
- Environment variable list (all VITE_ prefixed)
- Azure DevOps pipeline YAML
- Anti-patterns list

### 7. `mfe-[feature]/CLAUDE.md` — Frontend MFE Agent
Include:
- Role identity and MFE remote rules
- Repo directory structure
- Webpack Module Federation remote config (exposes + shared singletons)
- Standalone dev mode bootstrap pattern (with MockAuthProvider)
- API client generation step + hook wrapping pattern (React Query)
- Pact consumer contract test example (PactV3, full interaction definition)
- Pact publish command
- RTL component test pattern (loading / data / error states)
- Environment variable list
- Azure DevOps pipeline YAML (build → test → pact publish → deploy Static Web App)
- Anti-patterns list

### 8. `e2e-tests/CLAUDE.md` — QA Agent
Include:
- Role identity and testing scope statement
- Repo directory structure
- Playwright config (parallelism, retries, reporters including JUnit for Azure DevOps, projects for chromium + mobile)
- Page Object Model pattern (full example class)
- Auth fixture using MSAL test accounts
- User journey test structure (happy path + error path using page.route() to mock API)
- Accessibility audit with axe-playwright
- Pact can-i-deploy verification step
- Test data strategy (API seeding, cleanup, dedicated test tenant)
- API fixture helper for seeding data
- Azure DevOps pipeline YAML
- Anti-patterns list

### 9. `performance-tests/CLAUDE.md` — QA Performance Agent
Include:
- Role identity and scope (staging only, never production)
- Repo directory structure
- SLO thresholds definition (shared thresholds.js with p95, p99, error rate)
- Azure AD client credentials token acquisition helper
- Load test pattern (k6 stages: ramp up, sustain, ramp down)
- Stress test pattern
- Multi-step scenario pattern
- Azure DevOps pipeline YAML (scheduled nightly, not on every PR)
- Anti-patterns list

### 10. `platform-infra/CLAUDE.md` — Platform Agent
Include:
- Role identity and infrastructure-as-code rule
- Repo directory structure
- Azure resource naming convention table
- Bicep module examples:
  - App Service (Java API) with Managed Identity + Key Vault reference secrets + App Insights
  - Static Web App (React MFE)
  - API Management with OpenAPI spec import from api-contracts
  - App Insights + Log Analytics workspace
- Reusable Azure DevOps pipeline templates:
  - `java-api-pipeline.yaml` (parameterized by appName + environment)
  - `mfe-pipeline.yaml` (parameterized, includes Pact publish step)
- Observability: App Insights alert definitions (response time, error rate, availability)
- Sample KQL queries (error rate by endpoint, slow requests)
- Key Vault secret naming conventions
- Anti-patterns list

---

## Quality Requirements for All Files

- Every `CLAUDE.md` must open with a clear **Role Identity** section that states what the agent is, what it owns, and its top non-negotiable rules (max 3, bolded)
- All code examples must be complete and runnable — no placeholder-only snippets
- All Azure DevOps pipeline YAMLs must be syntactically valid
- All Bicep must use current API versions (2023+)
- Use Java 21 features where relevant (records, sealed classes, pattern matching, virtual threads)
- Use `jakarta.*` namespace (not `javax.*`) throughout all Java examples
- Anti-patterns sections must use concrete examples, not just labels
- No file should require editing before use — placeholders like `[domain]` in directory names are acceptable, but config values should use environment variable references

---

## Output Format

Generate each file in a separate code block clearly labeled with the file path.
After all files, provide a brief summary table of what was generated and where each file goes.
