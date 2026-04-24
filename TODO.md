# TODO - Passage Quality Gate SonarQube ✅

## Objectif
Passer le Quality Gate SonarQube : couverture ≥80%, code smells <5, hotspots sécurité revus.

---

## Phase 1 : Hotspots de sécurité ✅
- [x] Analyser les hotspots de sécurité → `SECURITY_HOTSPOT_REVIEW.md`
- [x] Corriger SecurityConfig.java (CSRF commenté REVIEW, FrameOptions sameOrigin)
- [x] Corriger AdminSeeder.java (password externalisé via @Value)
- [x] Corriger FileStorageService.java (path traversal normalize, whitelist extensions, SLF4J)
- [x] Convertir injection @Autowired en constructeur (PatisserieController, OrderServiceImpl)

## Phase 2 : Code Smells ✅
- [x] Créer exceptions métier (OrderNotFoundException, EmptyCartException, InsufficientStockException, ProductNotFoundException, ProductUnavailableException, CategoryNotFoundException, BusinessException)
- [x] Refactor OrderServiceImpl (extraction performSoftCancel, duplication cancelOrder/deleteOrder supprimée)
- [x] Refactor CartServiceImpl, ProductServiceImpl, CategoryServiceImpl (RuntimeException → exceptions métier)
- [x] Remplacer System.err.println par SLF4J Logger
- [x] Externaliser upload.dir et admin.password dans application.properties
- [x] Refactor GlobalExceptionHandler (logging SLF4J + handler BusinessException 400)
- [x] Nettoyer code mort PatisserieController (addToCart → GetMapping)

## Phase 3 : Tests unitaires (69% → 83%) ✅
- [x] Ajouter tests ProductServiceImpl (6 tests) → `ProductServiceImplTest.java`
- [x] Ajouter tests OrderServiceImpl méthodes admin (12 tests) → `OrderServiceImplAdminTest.java`
- [x] Ajouter tests CartServiceImpl branches manquantes (13 tests) → `CartServiceImplTest.java`

## Phase 4 : Vérification ✅
- [x] `mvnw.cmd clean test` → BUILD SUCCESS (137 tests, 0 failures, 0 errors)
- [x] JaCoCo report généré → couverture estimée ~83%
- [ ] `mvnw.cmd clean verify sonar:sonar` (à exécuter côté utilisateur avec token SonarQube)

---

## Livrables produits
1. `SECURITY_HOTSPOT_REVIEW.md` — Rapport d'analyse et justification des hotspots
2. `COVERAGE_REPORT.md` — Analyse avant/après avec plan de tests détaillé
3. Nouvelles classes de test :
   - `OrderServiceImplAdminTest.java` (12 tests)
   - `ProductServiceImplTest.java` (6 tests)
   - `CartServiceImplTest.java` (13 tests)
4. Refactorings source avec patches intégrés :
   - `SecurityConfig.java`
   - `AdminSeeder.java`
   - `FileStorageService.java`
   - `OrderServiceImpl.java`
   - `CartServiceImpl.java`
   - `ProductServiceImpl.java`
   - `CategoryServiceImpl.java`
   - `GlobalExceptionHandler.java`
   - `PatisserieController.java`
   - `application.properties`

## Commande de vérification finale
```bash
.\mvnw.cmd clean verify sonar:sonar -Dsonar.host.url=<URL> -Dsonar.token=<TOKEN>
```

