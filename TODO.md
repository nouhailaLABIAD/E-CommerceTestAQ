# Fix Failing Tests - TODO

## Errors to Fix (7 total)
- [x] 1. Fix `AdminOrderControllerTest` — malformed `createOrderWithUser` helper
- [x] 2. Fix `LoginControllerTest` — missing security context for Thymeleaf `#authorization`
- [x] 3. Fix `PatisserieControllerTest` — missing security context for Thymeleaf `#authorization`
- [x] 4. Fix `ProductControllerTest` — unhandled RuntimeException in @WebMvcTest context
- [x] 5. Fix `OrderIntegrationTest` — isolate from MySQL, use H2 for tests
- [x] 6. Run `mvnw.cmd clean test` to verify all 106 tests pass

## Result: BUILD SUCCESS — 106 tests run, 0 failures, 0 errors

---

# Align JaCoCo & SonarQube Coverage - TODO

## Root Causes
1. **Exclusions out of sync**: JaCoCo excludes `entity`, `config`, `dto`, and `EcommerceApplication`, but SonarQube still includes them (~600 untested LOC).
2. **Missing XML report path**: SonarQube is not configured to read `target/site/jacoco/jacoco.xml`.

## Steps
- [x] 1. Add `sonar.coverage.jacoco.xmlReportPaths` to `pom.xml`
- [x] 2. Add `sonar.exclusions` to `pom.xml` (matching JaCoCo exclusions)
- [x] 3. Run `mvnw.cmd clean test` — BUILD SUCCESS (106 tests, 0 failures)
- [x] 4. Run `mvnw.cmd clean verify sonar:sonar` — ANALYSIS SUCCESS (JaCoCo report imported)



