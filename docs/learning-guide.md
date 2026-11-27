# Learning guide: understand every part of this project

This project was built first and is learned second. Go through the lessons **in order**. Each lesson says:
the **problem** the part solves, the **file** to open, what to **look at**, and a small **try it** exercise.
Tick a lesson only when you can explain it out loud without reading.

| # | Lesson | Time |
|---|---|---|
| 1 | Feature list and test plan | 1 h |
| 2 | Test design techniques and the test case sheet | 2 h |
| 3 | Bug reports | 1 h |
| 4 | Postman: requests, variables, tests | 3 h |
| 5 | Newman (Postman from the command line) | 1 h |
| 6 | Maven and pom.xml | 1 h |
| 7 | First look at Selenium + TestNG | 2 h |
| 8 | Page Object Model | 3 h |
| 9 | Waits (why tests are flaky and how to fix it) | 2 h |
| 10 | Config, DriverFactory, BaseTest | 2 h |
| 11 | Test data: API user creation, CSV data-driven tests | 2 h |
| 12 | Listeners: screenshots and retries | 1 h |
| 13 | testng.xml, groups, smoke vs regression | 1 h |
| 14 | Allure report | 1 h |
| 15 | GitHub Actions (CI/CD) | 2 h |
| 16 | Interview practice | ongoing |

---

## Lesson 1: Feature list and test plan
**Problem:** "Test the app" has no end. You need to know what exists and decide what matters.
**Open:** `feature-list.md`, `test-plan.md`.
**Look at:** the *Questions or odd things* column; the *Out of scope* reasons; the *Risks* table.
**Try it:** explain in 2 sentences why Checkout is in scope and Rentals is not (answer: risk = usage × impact).

## Lesson 2: Test design techniques
**Problem:** you cannot test every possible input.
**Open:** the Excel file → *Test Cases*. Filter the *Technique* column.
**Look at:** TC-REG-003/004/005 (BVA: 7, 8, 9 characters), TC-PROD-004/005 (quantity 0/1, 99/100), TC-REG-006/007/008 (decision table: one rule broken at a time), TC-CART-002 (error guessing).
**Try it:** write 3 new test cases for the Contact form message field using EP and BVA.

## Lesson 3: Bug reports
**Problem:** a developer must be able to reproduce the bug without asking you anything.
**Open:** Excel → *Bug Reports*.
**Look at:** BUG-007: title says *where + what*, steps are numbered, expected vs actual are concrete, severity vs priority.
**Try it:** open the with-bugs site, reproduce BUG-003 yourself and add a screenshot.

## Lesson 4: Postman
**Problem:** the UI is slow to test; the API is where the real logic is. Testing it directly is faster and finds bugs earlier.
**Open:** Postman → *Import* → `postman/Toolshop-API.postman_collection.json` and both environment files.
**Look at, in order:**
1. *Register a new customer* → **Pre-request Script** (creates a unique email) → **Body** → **Tests** tab.
2. *Login with valid credentials* → the test saves `token` into a **collection variable**.
3. *Get my profile (with token)* → **Headers**: `Authorization: Bearer {{token}}`.
4. Collection → *Variables* tab: see how values flow from one request to the next.
5. Collection → *Scripts/Tests* at collection level: one check that runs after every request.
**Try it:** run the collection with the *toolshop* environment (all green), then with *toolshop-with-bugs* and find which tests fail. Each failure is a possible bug.

## Lesson 5: Newman
**Problem:** Postman needs a person clicking. CI needs a command.
**Try it:**
```
npm install -g newman newman-reporter-htmlextra
newman run postman/Toolshop-API.postman_collection.json -e postman/toolshop.postman_environment.json -r cli,htmlextra
```
Open the HTML file in `newman/`.

## Lesson 6: Maven and pom.xml
**Problem:** a Java project needs libraries (Selenium, TestNG…) in the right versions, and one command to build and test.
**Open:** `pom.xml`.
**Look at:** `<dependencies>` (what we use), `maven.compiler.release=21`, the surefire plugin (runs TestNG with `testng.xml`), the `-Dsuite` property.
**Try it:** run `mvn test -Dsuite=testng-smoke.xml` and read the console.

## Lesson 7: Selenium + TestNG basics
**Problem:** clicking the same flows by hand every release is slow and error-prone.
**Open:** `tests/SearchTests.java`.
**Look at:** `@Test`, `groups`, `description`, `@DataProvider` (three spellings of "pliers"), `assertEquals` / `assertTrue`.
**Try it:** add `{"Pliers"}` to the data provider and run only this class from IntelliJ (green triangle).

## Lesson 8: Page Object Model
**Problem:** if 20 tests contain the login locator and the developer renames it, you fix 20 places.
**Solution:** one class per page holds the locators and actions; tests only call methods like `login(email, password)`.
**Open:** `pages/LoginPage.java`, then `tests/LoginTests.java`.
**Look at:** locators are `private static final By`; tests contain no locators; methods return the next page (`loginSuccessfully` returns `AccountPage`).
**Try it:** find where `data-test='login-submit'` is used. It exists in exactly one place.

## Lesson 9: Waits
**Problem:** the page loads data in the background; Selenium is faster than the page, so it clicks before the element exists → random failures ("flaky tests").
**Open:** `pages/BasePage.java`.
**Look at:** `visible()`, `click()` (waits until clickable, falls back to a JavaScript click if something covers the element), `waitForListToSettle()` (waits until the product list stops changing after a search/sort).
**Rule:** explicit waits (`WebDriverWait` + a condition), never `Thread.sleep`, no implicit wait.

## Lesson 10: Config, DriverFactory, BaseTest
**Problem:** URLs, browser and timeouts must be changeable without editing code (local vs CI).
**Open:** `config.properties`, `config/ConfigReader.java`, `driver/DriverFactory.java`, `base/BaseTest.java`.
**Look at:** priority `-Dkey` > environment variable > file; `ThreadLocal<WebDriver>` (safe for parallel runs); `@BeforeMethod` opens a browser, `@AfterMethod` closes it.
**Try it:** `mvn test -Dsuite=testng-smoke.xml -Dheadless=true`.

## Lesson 11: Test data
**Problem 1:** the shared demo accounts are often broken by other people (you saw it on day 1).
**Solution:** `api/ApiClient.registerNewCustomer()` creates a fresh user through the API in under a second.
**Problem 2:** the same test with many inputs.
**Solution:** CSV + `@DataProvider` → `testdata/invalid-logins.csv`, `testdata/weak-passwords.csv`.
**Try it:** add a row `no lowercase letter,ABCDEF#1` to `weak-passwords.csv` and run `RegisterTests`.

## Lesson 12: Listeners
**Open:** `listeners/TestListener.java`, `RetryAnalyzer.java`, `RetryTransformer.java`.
**Look at:** `onTestFailure` takes a screenshot and attaches it to Allure; the retry runs a failed test once more (environment is unstable). Be ready to defend this: a retry can hide real flakiness, so the console prints every retry.

## Lesson 13: Suites and groups
**Open:** `testng.xml`, `testng-smoke.xml`.
**Look at:** smoke = the ~11 most important tests for a fast check; regression = everything.

## Lesson 14: Allure
**Try it:** after a run, `mvn allure:serve`. Find: the *Features* view (from `@Feature`), the steps inside a test (from `@Step`), severity, and a screenshot on a failed test.

## Lesson 15: GitHub Actions
**Problem:** tests only help if they run automatically, every time code changes.
**Open:** `.github/workflows/tests.yml`.
**Look at:** `on:` (triggers), two `jobs` (API and UI run in parallel on separate machines), `steps`, `if: always()` (upload reports even when tests fail), artifacts.
**Try it:** on GitHub → *Actions* → *Tests* → *Run workflow*. Download the artifacts.

## Lesson 16: Interview practice
Use `docs/interview-qa.md`. Read an answer, close the file, say it out loud in your own words.
