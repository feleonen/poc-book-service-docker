# Agent Orchestration Guide
# Feature: GET /entities endpoint + React Dropdown Component

---

## The Mental Model

Claude Code is **not** a background process. Each agent is a Claude Code session
you open in a terminal inside a specific repo. You are the **orchestrator** —
you decide when to start each agent, what to hand off, and when to unblock parallelism.

```
YOU (Human Orchestrator)
      │
      ├──▶ Terminal 1: cd api-contracts  && claude  (Architect)
      ├──▶ Terminal 2: cd api-users      && claude  (Backend)
      ├──▶ Terminal 3: cd mfe-dashboard  && claude  (Frontend)
      ├──▶ Terminal 4: cd e2e-tests      && claude  (QA)
      └──▶ Terminal 5: cd platform-infra && claude  (Platform)
```

You write the epic/story once, then give each agent a scoped prompt.
Each agent reads its own `CLAUDE.md` automatically — so it already knows
its role, standards, and constraints before you say a word.

---

## Step 0 — Define the Work Item (Your Input)

Write this once. You will paste it (or parts of it) into each agent session.

```
EPIC: Entity Selection
STORY: US-042
As a user, I want to select an entity from a dropdown in the dashboard form
so that I can associate my submission with the correct entity.

Acceptance Criteria:
  - GET /entities returns a paginated list of entities (id, name, code)
  - Dropdown component is reusable (accepts any {id, label} list as props)
  - Dropdown is pre-populated on page load via the new endpoint
  - Loading and error states are handled in the UI
  - Accessible: keyboard navigable, aria-label support
  - Auth required: Azure AD bearer token

Out of scope for this story:
  - Create/edit entities (separate story)
  - Filtering or search within dropdown
```

---

## Phase 1 — Architect Agent (Blocking — others wait for this)

### Open terminal 1
```bash
cd api-contracts
claude
```

### Prompt to give the Architect Agent
```
US-042: I need a new endpoint for the Entity Selection epic.

Create a new OpenAPI 3.x spec entry in specs/entities-api.yaml for:

  GET /entities
  - Returns a paginated list of entities
  - Each entity has: id (UUID), name (string), code (string, 3-char uppercase)
  - Supports cursor-based pagination (pageSize, pageToken)
  - Requires Azure AD authentication (use existing AzureAD security scheme)
  - All standard error responses (400, 401, 403, 500)
  - Use $ref for shared schemas and responses — no inline duplication

After creating the spec:
1. Validate it with Spectral
2. Show me the generated spec for review
3. List any shared schemas you created or reused
```

### What to expect back
The agent will create `specs/entities-api.yaml`, run Spectral, and show you
the spec. Review it, request any changes, then:

```
Looks good. Commit this as:
  feat(entities): add GET /entities OpenAPI spec for US-042

Then tell me the exact schema names I should share with Frontend and Backend.
```

### ✅ Gate: Spec is merged to main. Phase 2 can begin.
**Copy the spec output** — you'll paste relevant parts into Backend and Frontend prompts.

---

## Phase 2 — Backend + Frontend in Parallel

Once the spec is merged, open two terminals and run both simultaneously.
They do not depend on each other — only on the spec.

---

### Terminal 2 — Backend Agent

```bash
cd api-users   # or api-entities depending on your domain split
claude
```

#### Prompt to give the Backend Agent
```
US-042: Implement GET /entities endpoint.

The OpenAPI spec is now merged in api-contracts/specs/entities-api.yaml.

Follow TDD and API-first:

1. Pull the latest spec from api-contracts/ (it's symlinked at openapi/entities-api.yaml)
2. Run the OpenAPI generator to regenerate the controller interface
3. Write the FAILING unit test first for EntityApplicationService.listEntities()
   - Should return Page<EntityResponse>
   - Should accept Pageable parameter
4. Write the FAILING @WebMvcTest for GET /entities
   - 200 with valid token
   - 401 without token
   - 400 with invalid pageSize
5. Implement the minimum code to make tests GREEN:
   - EntityController implements the generated EntitiesApi interface
   - EntityApplicationService.listEntities()
   - EntityRepository (Spring Data JPA)
   - Entity JPA entity with Flyway migration V{next}__create_entities_table.sql
   - EntityMapper (MapStruct)
6. Write Testcontainers integration test against Azure SQL Server container
7. Write Pact provider test stub (state: "entities exist")

Do NOT implement pagination beyond what the spec requires for this story.
Flag anything ambiguous before implementing.
```

---

### Terminal 3 — Frontend Agent (MFE)

```bash
cd mfe-dashboard
claude
```

#### Prompt to give the Frontend Agent
```
US-042: Implement entity dropdown in the dashboard form.

The OpenAPI spec is now merged at: ../api-contracts/specs/entities-api.yaml

Work in this order:

1. Regenerate the TypeScript API client:
   npm run generate:api -- --spec ../api-contracts/specs/entities-api.yaml --output src/api/entities

2. Write the Pact consumer contract test FIRST (TDD):
   - Consumer: mfe-dashboard
   - Provider: entities-api
   - Interaction: GET /entities returns list with at least one entity
   - Entity shape: { id: uuid, name: string, code: string }

3. Build a reusable EntityDropdown component:
   Path: src/components/shared/EntityDropdown/
   Files: EntityDropdown.tsx, EntityDropdown.test.tsx, index.ts

   Props interface:
     - value: string | null
     - onChange: (id: string) => void
     - disabled?: boolean
     - label?: string  (default: "Entity")
     - aria-label for accessibility

   Internal behavior:
     - Fetches entities via useEntities() hook on mount
     - Shows skeleton/spinner while loading
     - Shows error alert with retry button on failure
     - Renders native <select> or headless UI — your call, justify it

4. Write RTL tests:
   - Renders skeleton while loading
   - Renders options when data loads
   - Calls onChange with correct id on selection
   - Renders error state with retry button
   - Is keyboard navigable

5. Integrate EntityDropdown into DashboardForm.tsx
   - Pre-populate on form load
   - Wire value to form state

Do not build search/filter — out of scope for this story.
```

---

## Phase 3 — QA Agent (Starts when Phase 2 is nearly done)

Start this when Backend and Frontend are both in review/ready state.
QA needs both sides working together.

```bash
cd e2e-tests
claude
```

### Prompt to give the QA Agent
```
US-042: Write E2E and contract verification for the entity dropdown.

Context:
- New endpoint: GET /entities (paginated, auth required)
- New UI: EntityDropdown component in dashboard form
- Backend Pact provider state: "entities exist"
- Frontend Pact consumer: mfe-dashboard → entities-api

Tasks:

1. Verify Pact contracts can-i-deploy:
   npx pact-broker can-i-deploy \
     --pacticipant mfe-dashboard \
     --pacticipant entities-api \
     --to-environment staging

2. Write Playwright E2E test:
   File: tests/dashboard/entity-dropdown.spec.ts

   Journey 1 — Happy path:
     - Authenticated user navigates to /dashboard/new
     - Dropdown is visible and pre-populated with entity options
     - User selects an entity
     - Entity id is reflected in form state (check aria-selected or hidden input)

   Journey 2 — Error state:
     - Use page.route() to mock GET /entities returning 500
     - Dropdown shows error state with retry button
     - User clicks retry — mock returns 200 — dropdown populates

3. Add EntityDropdown to the accessibility audit:
   Use axe-playwright to check the dropdown has no a11y violations

4. Do NOT write performance tests for this story (GET /entities is low-risk read).
   Flag if you think volume load-testing is needed.

Use authenticatedPage fixture for all tests.
Seed any required entity data via the API fixture before each test.
```

---

## Phase 4 — Platform Agent (Triggered by QA green)

```bash
cd platform-infra
claude
```

### Prompt to give the Platform Agent
```
US-042: Release readiness check and deployment for entity dropdown feature.

Checklist — verify each item and report status:

1. APIM spec sync:
   - Confirm entities-api.yaml is imported into Azure APIM for staging
   - If not: update bicep/modules/api-management.bicep to add the entities API resource
   - Show me the what-if diff before applying

2. Pipeline review:
   - Check azure-pipelines.yml in api-users/ covers: build → unit test → contract test → deploy staging
   - Check azure-pipelines.yml in mfe-dashboard/ covers: build → pact publish → deploy staging

3. Environment variables:
   - Confirm staging App Service has all required env vars for the entities endpoint
   - Confirm MFE static web app has VITE_API_BASE_URL pointing to staging APIM

4. Smoke test gate:
   - After staging deploy, trigger the e2e-tests pipeline against staging URL
   - Only promote to production after e2e green

5. If all green — prepare production deployment:
   - Use slot swap strategy (staging slot → production) for zero downtime
   - Show me the swap command before executing

Report any gaps as blocking or non-blocking.
```

---

## How to Write Good Agent Prompts — Principles

### 1. Always anchor to the story ID
Every prompt starts with `US-042:` — this keeps the agent focused and
makes it easy to search conversation history later.

### 2. Tell the agent the ORDER to do things
Don't say "implement the endpoint." Say:
"1. Write the failing test. 2. Show me. 3. Then implement."
Claude Code will follow the sequence and pause at review points.

### 3. Explicitly state out-of-scope items
If you don't say it's out of scope, the agent may build it.
> "Do NOT implement filtering — out of scope for this story."

### 4. Use "Flag anything ambiguous before implementing"
This prevents the agent from making silent assumptions on hard decisions.
It will ask you instead of guessing.

### 5. Reference the spec as the source of truth
> "The OpenAPI spec at openapi/entities-api.yaml is the source of truth.
>  If your implementation contradicts it, fix the implementation — not the spec."

### 6. Ask for a plan first on complex tasks
```
Before writing any code, give me a brief implementation plan:
- What files will be created/modified?
- Any risks or questions?
I'll approve before you start.
```

### 7. Use checkpoints for long tasks
```
Complete steps 1-3, then pause and show me the test output before continuing.
```

---

## Parallel Execution — What Can Run Simultaneously

```
Phase 1    [Architect] ──────────────────┐
                                         │ spec merged
Phase 2                    [Backend] ────┤──────────────────┐
                           [Frontend] ───┘                  │
                                                            │ PRs ready
Phase 3                                       [QA] ─────────┤
                                                            │ all green
Phase 4                                            [Platform]┘
```

Rules:
- **Never** start Backend or Frontend before the spec is merged
- Backend and Frontend **always** run in parallel — they share nothing except the spec
- QA **can start writing** E2E test structure during Phase 2, but can't run them until both are deployed to a shared env
- Platform **always** runs last — it validates readiness, not development

---

## Handling Cross-Agent Blockers

### Scenario: Frontend finds the spec is missing a field
```
# In Frontend agent terminal:
"The spec doesn't include `code` in the EntityResponse schema,
 but the UI needs it for display. Don't implement a workaround —
 I'll update the spec first."

# Switch to Architect terminal:
"US-042 spec update: Add `code: string` (3-char uppercase) to EntityResponse
 in entities-api.yaml. This is additive — minor version bump.
 Validate and commit."

# Then return to Frontend terminal:
"Spec is updated and merged. Re-pull and regenerate the TypeScript client,
 then continue."
```

### Scenario: Backend Pact test fails because Frontend consumer changed the contract
```
# In QA terminal:
"can-i-deploy is failing — mfe-dashboard consumer expects `entityCode`
 but entities-api provider returns `code`. 

 Backend: update EntityResponse to include `entityCode` as an alias OR
 Frontend: update consumer pact to use `code`.
 
 Check the spec — spec wins."
```

---

## Release Checklist (Human-Owned)

Before promoting to production, verify:
- [ ] Spec merged and validated in `api-contracts/`
- [ ] Backend unit + integration + Pact provider tests green
- [ ] Frontend component tests + Pact consumer tests green
- [ ] `can-i-deploy` passes for both pacticipants
- [ ] E2E happy path and error path green on staging
- [ ] APIM updated with new spec
- [ ] No secrets in code or pipeline vars
- [ ] Azure DevOps pipeline fully green (no manual approvals skipped)
- [ ] Slot swap approved by at least 1 team member
