# Toolshop QA Testing Project

End-to-end QA project for **[Practice Software Testing – Toolshop](https://practicesoftwaretesting.com)**, a demo online shop for tools.
It covers the full testing life cycle: exploration, test planning, manual testing, bug reporting, API testing, UI automation and CI/CD.

**Author:** Mahmoud Sameeh · ISTQB CTFL v4.0

---

## Results at a glance

| Area | What was done | Numbers |
|---|---|---|
| Exploration | Feature list for 3 roles (guest, customer, admin) | ~40 features |
| Planning | Risk-based test plan: scope, out of scope, risks, exit criteria | 8 features in scope |
| Manual testing | Test cases designed with EP, BVA, decision tables, state transition, error guessing | **55 test cases**, 26 executed, 26 passed |
| Bug reporting | Bugs on the main site and on the planted-bugs site | **12 bugs** (1 High, 8 Medium, 3 Low) |
| API testing | Postman collection with environments, variables, token handling | **23 requests, 77 assertions** |
| UI automation | Java + Selenium + TestNG, Page Object Model | **32 test methods → 43 test runs**, 9 page objects · **43/43 passed** (local run, 27 Nov 2026) |
| CI/CD | GitHub Actions runs API and UI tests on every push | 2 parallel jobs, reports as artifacts |

## Tech stack

Java 21 · Selenium WebDriver 4 · TestNG · Maven · Page Object Model · RestAssured (test data setup) · Allure Reports · Postman · Newman · Git & GitHub · GitHub Actions

## What is in this repository

```
├── feature-list.md                      # Phase 1: what the app can do, with questions and odd things
├── test-plan.md                         # Phase 1: scope, approach, risks, exit criteria
├── toolshop-test-cases-and-bugs.xlsx    # Phase 2: 55 test cases, 11 bug reports, automatic summary
├── postman/                             # Phase 3: API collection + environments (main site and with-bugs site)
├── pom.xml                              # Phase 4: Maven build (dependencies, test runner, Allure)
├── src/test/java/com/toolshop/
│   ├── config/      ConfigReader        # reads config.properties, can be overridden with -D or env vars
│   ├── driver/      DriverFactory       # creates Chrome/Firefox/Edge, headless option, one browser per thread
│   ├── base/        BaseTest            # opens a fresh browser before each test, closes it after
│   ├── pages/       9 page objects      # locators + actions for each page (Page Object Model)
│   ├── api/         ApiClient           # creates fresh test users through the API
│   ├── data/        test data factory, CSV reader
│   ├── listeners/   screenshot on failure, retry of flaky tests
│   └── tests/       8 test classes
├── src/test/resources/
│   ├── config.properties                # base URL, browser, headless, timeouts
│   ├── testng.xml / testng-smoke.xml    # full regression suite / smoke suite
│   └── testdata/*.csv                   # data for data-driven tests
├── .github/workflows/tests.yml          # Phase 6: CI pipeline
└── docs/                                # learning guide, interview Q&A, CV bullets
```

## How to run

**Requirements:** Java 21+ and Maven 3.9+ (UI tests), Node.js 18+ (API tests), Google Chrome.

### UI tests (Selenium)
```bash
mvn test                                   # full regression suite, Chrome with a visible window
mvn test -Dheadless=true                   # without a window (as in CI)
mvn test -Dsuite=testng-smoke.xml          # smoke suite only (~11 tests)
mvn test -Dbrowser=firefox                 # another browser
mvn allure:serve                           # open the Allure report after a run
```

### API tests (Postman / Newman)
```bash
npm install -g newman newman-reporter-htmlextra
newman run postman/Toolshop-API.postman_collection.json -e postman/toolshop.postman_environment.json -r cli,htmlextra
```
To see the planted bugs, run the same collection against the buggy API:
`-e postman/toolshop-with-bugs.postman_environment.json`

## Test approach (short)

* **Risk-based:** features are prioritised by *how often they are used × how much damage a failure causes*. Checkout, cart and login are P1.
* **Manual first, then automate:** the manual test cases became the list of what to automate (37 of 55 marked "Automate = Yes").
* **Independent tests:** every UI test opens its own browser and creates its **own fresh user through the API**, so tests never depend on shared demo accounts (which other people lock or change) or on each other.
* **No hard-coded IDs:** product IDs change whenever the public demo site is reset, so tests find products **by name**.
* **Explicit waits only:** no implicit waits and no `Thread.sleep` for synchronisation (one documented exception when checking that something does *not* appear).
* **Retry once:** the public site is sometimes slow or briefly down; a test is re-run once before it counts as failed.

Full details: [test-plan.md](test-plan.md)

## Bugs found (highlights)

| ID | Severity | Summary |
|---|---|---|
| BUG-007 | High | Cart line total shows `$00.00` for a $14.15 product (with-bugs site) |
| BUG-002 | Medium | Credit Card with empty fields shows only "Unknown error" (main site) |
| BUG-003 | Medium | "Price (Low – High)" sorts from high to low (with-bugs site) |
| BUG-005 | Medium | Quantity "+" button does nothing (with-bugs site) |
| BUG-006 | Medium | "Add to cart" shows an error message although the item is added (with-bugs site) |
| BUG-012 | Medium | API rejects real cities (Amsterdam, Rotterdam) as "not belonging to the country" (main site, found by the CI pipeline) |
| BUG-001 | Low | Password error says "minimal 6 characters" but the rule list says 8 (main site) |

All 12 bug reports, with steps, expected and actual results: `toolshop-test-cases-and-bugs.xlsx` → *Bug Reports*.

## CI/CD

`.github/workflows/tests.yml` runs on every push to `main`, on every pull request and on demand:

1. **API tests:** installs Newman, runs the collection, uploads an HTML report.
2. **UI tests:** installs Java 21, runs the TestNG suite in headless Chrome, builds the Allure report, uploads it, and uploads screenshots of failed tests.

Reports are under **Actions → latest run → Artifacts**.

## Known limitations

* The application is a **shared public demo**. Other users change data, and the site is sometimes reset or briefly unavailable. Some failures can be caused by the environment, not by the product (see *Risks* in the test plan).
* Performance, security, "Sign in with Google", mobile browsers and languages other than English are **out of scope** (reasons in the test plan).
