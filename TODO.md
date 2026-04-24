# TODO - Atteindre 88% de couverture de test

## Phase 1 : Corriger les tests existants qui échouent
- [ ] 1.1 AdminProductControllerTest — ajouter `@MockBean CategoryRepository`
- [ ] 1.2 CategoryControllerTest — corriger `isA()` et `model().attribute("category.nom")`
- [ ] 1.3 ProductControllerTest — corriger gestion d'exception
- [ ] 1.4 CategoryRepositoryTest — isoler la base (assertion adaptable)
- [ ] 1.5 OrderIntegrationTest — ajouter `deleted = false` dans SQL script

## Phase 2 : Créer les tests pour les controllers manquants
- [ ] 2.1 AdminOrderControllerTest
- [ ] 2.2 ClientProductControllerTest
- [ ] 2.3 HomeControllerTest
- [ ] 2.4 LoginControllerTest
- [ ] 2.5 PatisserieControllerTest
- [ ] 2.6 RegisterControllerTest

## Phase 3 : Créer les tests services manquants
- [ ] 3.1 FileStorageServiceTest

## Phase 4 : Créer les tests repositories manquants
- [ ] 4.1 ProductRepositoryTest
- [ ] 4.2 UserRepositoryTest
- [ ] 4.3 CartRepositoryTest
- [ ] 4.4 OrderRepositoryTest

## Phase 5 : Configurer JaCoCo
- [ ] 5.1 Exclure `config/`, `entity/`, `dto/`, `EcommerceApplication`

## Phase 6 : Vérification finale
- [ ] 6.1 `mvn clean test jacoco:report`
- [ ] 6.2 Vérifier que le build est VERT
- [ ] 6.3 Vérifier que la couverture >= 88%

