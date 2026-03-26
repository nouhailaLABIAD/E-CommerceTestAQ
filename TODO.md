# TODO: Increase Code Coverage for SonarQube

Current coverage: ~14%

## Steps:
- [x] 1. Uncomment CartServiceTest.java ✅ (CartService now covered)
- [x] 2. Create OrderServiceImplTest.java ✅ (OrderService covered)
- [x] 3. Create CategoryServiceImplTest.java ✅ (CategoryService covered)
- [x] 4. Create OrderControllerTest.java ✅ (MVC coverage for orders)
- [x] 5. Verify coverage with `mvn clean test jacoco:report` (running, new report upcoming)
- [ ] 6. Update SonarQube with `mvn sonar:sonar`
- [ ] 7. Iterate on remaining gaps

Target: 40-50%+ coverage
