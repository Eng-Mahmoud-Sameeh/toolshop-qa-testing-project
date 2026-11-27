# Interview questions about this project (with model answers)

Say each answer in your own words. Short, concrete, with an example from the project.

## About the project

**1. Tell me about your project.**
I tested Toolshop, a demo online shop, from start to finish. I explored it and wrote a feature list, made a risk-based test plan, designed 55 manual test cases with techniques like boundary values and equivalence partitioning, and reported 11 bugs. Then I tested the REST API with Postman and Newman, built a Selenium + TestNG framework with the Page Object Model (32 test methods, 43 runs), and set up GitHub Actions to run everything on every push.

**2. Why did you choose this application?**
It is a realistic shop: login, search, cart, checkout with several payment methods, an admin area and a documented REST API. It also has a version with planted bugs, so I could practise bug reporting on real defects.

**3. What was the hardest part?**
The environment. The site is public and shared: on the first day the API went down, the shared test accounts stopped working, and product IDs changed after resets. I solved it by creating a fresh user through the API for every test and finding products by name instead of ID.

## Planning and manual testing

**4. How did you decide what to test?**
By risk: how often a feature is used multiplied by how bad a failure is. Checkout and cart affect money and every buyer, so they are P1. Rentals and the chat assistant are rarely used, so they are out of scope.

**5. What is equivalence partitioning? Give an example.**
Splitting inputs into groups the system treats the same and testing one value per group. Passwords are either shorter than 8 characters (invalid) or 8 or more (valid), so I test one of each.

**6. What is boundary value analysis? Give an example.**
Testing on and next to the edges, because off-by-one mistakes happen there. The quantity field allows 1–99, so I tested 0, 1, 2 and 99, 100. The app corrects 0 to 1 and 100 to 99.

**7. What is a decision table and where did you use it?**
A table of conditions and the expected result for each combination. For the password rules (length, number, symbol, upper/lower case) I broke one rule at a time and expected a rejection each time.

**8. What is error guessing?**
Using experience to guess where bugs hide. Example: adding the same product twice often creates two cart lines instead of quantity 2, so I tested it on purpose.

**9. What makes a good bug report?**
A clear title (where + what), environment, preconditions, numbered steps, expected vs actual result, severity, priority and evidence. A developer must reproduce it without asking me anything.

**10. Severity vs priority?**
Severity is how much damage the bug does (decided by the tester). Priority is how soon it must be fixed (decided with the team). A typo in the logo is low severity but can be high priority.

**11. Tell me about a bug you found.**
On the with-bugs site, the cart line total showed $00.00 for a $14.15 product while the cart total was correct. I rated it High because it shows wrong money to the customer. On the main site, the password error says "minimal 6 characters" while the rules list says 8: a low-severity but real inconsistency.

**12. What are entry and exit criteria?**
Entry: conditions to start testing (environment up, accounts work). Exit: when we are done (all planned tests run, no open critical bugs in checkout, automated suites green).

## API testing

**13. Why test the API if you test the UI?**
The business logic lives in the API. API tests are faster, more stable, find bugs earlier, and test things the UI hides, like a missing token returning 401.

**14. Which status codes did you check?**
200 OK, 201 Created (register, create cart), 401 Unauthorized (no token, wrong password), 404 Not Found (unknown product), 409/422 for a duplicate email or invalid product id.

**15. How did you handle authentication in Postman?**
The login request saves `access_token` into a collection variable with a test script. Protected requests send the header `Authorization: Bearer {{token}}`.

**16. How do you pass data between requests?**
With collection variables: register saves the email, login saves the token, the product list saves a product id, create cart saves the cart id, and later requests use `{{cartId}}`.

**17. What is Newman?**
The command-line runner for Postman collections. It lets CI run the collection and produce HTML and JUnit reports.

## Automation

**18. Explain the Page Object Model.**
One class per page holds its locators and actions. Tests call methods like `cart.changeQuantity("Hammer", 3)` and contain no locators. If the UI changes, I update one class instead of many tests.

**19. Which locators did you use and why?**
Mostly the app's `data-test` attributes. They exist for testing, so they don't change when the design changes. They are more stable than XPath based on layout or CSS classes.

**20. Implicit vs explicit wait?**
An implicit wait applies to every element search. An explicit wait waits for a specific condition (visible, clickable, URL contains…). I use only explicit waits, because mixing both causes unpredictable timing.

**21. Why not Thread.sleep?**
It always waits the full time (slow) and is still not enough when the site is slower (flaky). Explicit waits continue as soon as the condition is true.

**22. How do you make tests independent?**
Each test opens a new browser and creates its own user through the API. No test depends on another test or on shared data.

**23. How do you do data-driven testing?**
With TestNG `@DataProvider`. Invalid logins and weak passwords come from CSV files, so one test method runs once per row.

**24. What happens when a test fails?**
A TestNG listener takes a screenshot, attaches it and the URL to the Allure report, and saves it in `target/screenshots`. CI uploads these as artifacts.

**25. Why do you retry failed tests? Isn't that hiding problems?**
The demo site is public and sometimes slow, so I retry once and log every retry. A test that fails twice is a real failure. In a real company I would track retried tests and fix the root cause.

**26. How would you run tests in parallel?**
The driver is stored in a `ThreadLocal`, so each thread has its own browser. In `testng.xml` I would set `parallel="classes"` and a thread count. I kept it sequential to be fair to the shared demo site.

**27. How do you run different suites?**
Groups: `smoke` (11 most important tests) and `regression` (all). `mvn test -Dsuite=testng-smoke.xml` runs the smoke suite.

## CI/CD and Git

**28. What does your pipeline do?**
On every push and pull request, GitHub Actions starts two jobs in parallel: Newman runs the API collection; Maven runs the Selenium suite in headless Chrome. Reports (HTML, Allure, screenshots) are uploaded as artifacts.

**29. What is headless mode and why use it in CI?**
The browser runs without a window. CI servers have no screen, and headless is faster.

**30. What is a merge conflict and how did you resolve one?**
Two changes to the same lines that Git cannot combine. It happened when I edited a file on GitHub and locally. I opened the file, kept the correct content, removed the markers, then `git add` and `git commit`.

**31. What would you improve next?**
Publish the Allure report to GitHub Pages, run tests in parallel, add cross-browser runs in CI, add API contract checks against the Swagger schema, and cover the admin area.
