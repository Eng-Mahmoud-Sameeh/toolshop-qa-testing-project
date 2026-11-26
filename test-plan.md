# Test Plan – Toolshop (Practice Software Testing)

**Author:** Mahmoud Sameeh  **Date:** 26 Nov 2026  **Status:** Draft v1

## 1. Introduction
Toolshop is a demo online shop for tools. Guests can browse and buy, registered customers also have an account area, and admins manage products, orders and users.
This plan describes what will be tested, how, and when testing is finished.

- App: https://practicesoftwaretesting.com
- API: https://api.practicesoftwaretesting.com (Swagger: /api/documentation)
- Version with planted bugs (for bug reports): https://with-bugs.practicesoftwaretesting.com

## 2. Scope (what we will test)
Features are chosen by risk: how often they are used × how much damage a failure causes.

| # | Feature | Why |
|---|---|---|
| 1 | Login and logout | Without login, customers cannot buy or see orders |
| 2 | Registration | New customers bring new money |
| 3 | Search, filters, sort | Customers cannot buy what they cannot find |
| 4 | Product details | Price, stock and "out of stock" must be correct |
| 5 | Cart | Wrong quantity or total means wrong money |
| 6 | Checkout (4 steps, incl. guest) | This is where the shop earns money |
| 7 | Customer invoices | Proof of payment; wrong totals are a legal problem |
| 8 | Admin: products and orders | Without them the shop cannot be run |

## 3. Out of scope (what we will NOT test)

| # | Item | Why |
|---|---|---|
| 1 | Performance / load testing | Needs special tools; would overload a shared public site |
| 2 | Security testing | Needs special skills and permission |
| 3 | "Sign in with Google" | External system we do not control |
| 4 | Chat assistant, live shop activity, rentals | Do not sell anything; low risk |
| 5 | Languages other than English, mobile browsers | Multiplies the work; English on desktop Chrome only |

## 4. Approach (how we will test)
1. **Manual testing first.** Write test cases for the in-scope features using test design techniques (equivalence partitioning, boundary values, decision tables). Run them on the main site. Report bugs found on the with-bugs site.
2. **API testing.** Postman collection with environments, variables and token handling; run from the command line with Newman.
3. **UI automation.** Java + Selenium WebDriver + TestNG + Maven, Page Object Model, Allure reports.
4. **CI/CD.** GitHub Actions runs the API and UI tests on every push.

The manual test cases become the list of what to automate.

## 5. Test environment and data
- Windows 11, Google Chrome (latest), Java 21, Maven
- Test accounts: own registered account(s) with **fake data only**; published shared accounts only as backup
- Product IDs change when the site is reset, so tests must **find products by name, never by ID**

## 6. Risks

| Risk | Impact | What we do about it |
|---|---|---|
| Public site or API is down (happened on 26 Nov 2026) | Tests fail for reasons that are not bugs | Check the site works before reporting a bug; re-run later |
| Shared test accounts are changed or locked by other users | Login tests fail | Use our own accounts; create test users through the API |
| Other testers add or delete data (brands, users, products) | Expected results change | Do not depend on data we did not create; search by name |
| Site is reset and IDs change | Hard-coded links break | Never hard-code IDs |
| Limited time and experience (one junior tester) | Not everything can be tested | Test by risk: highest-risk features first |

## 7. What to automate
Automate tests that are **repeated often, stable, and high risk**:
- Login (valid, invalid, empty fields)
- Search and filter
- Add to cart and change quantity
- Full checkout as a registered customer and as a guest
- API: login and token, products list, create cart, add item

Not automated: one-time exploratory checks and visual layout.

## 8. Entry and exit criteria
**Start testing when:** the site and API are reachable, and test accounts work.

**Testing is finished when:**
- All planned test cases for in-scope features have been run
- No open **critical** or **high** bugs in checkout, cart or login
- Automated suites run green in GitHub Actions
- A short test summary (results, bugs found, open risks) is written in the README

## 9. Deliverables
- Feature list (`feature-list.md`)
- This test plan (`test-plan.md`)
- Test case sheet and bug report sheet (Excel)
- Postman collection and Newman report
- Selenium automation project with Allure report
- GitHub Actions pipeline