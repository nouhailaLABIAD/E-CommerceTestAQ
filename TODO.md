# Fix Failing Tests - TODO

## Errors to Fix (7 total)
- [x] 1. Fix `AdminOrderControllerTest` — malformed `createOrderWithUser` helper
- [x] 2. Fix `LoginControllerTest` — missing security context for Thymeleaf `#authorization`
- [x] 3. Fix `PatisserieControllerTest` — missing security context for Thymeleaf `#authorization`
- [x] 4. Fix `ProductControllerTest` — unhandled RuntimeException in @WebMvcTest context
- [x] 5. Fix `OrderIntegrationTest` — isolate from MySQL, use H2 for tests
- [x] 6. Run `mvnw.cmd clean test` to verify all 106 tests pass

## Result: BUILD SUCCESS — 106 tests run, 0 failures, 0 errors



