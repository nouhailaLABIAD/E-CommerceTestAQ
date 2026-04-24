# Plan Augmentation Couverture de Tests

## Objectif : Passer de 83% à >95% (instructions) et 56% à >80% (branches)

## État actuel (baseline)
- **Instructions** : 83% (394 missed / 2329 total)
- **Branches** : 56% (94 missed / 218 total)
- **Tests** : 137 passent, 0 échecs

## Étapes à réaliser

- [x] **Étape 1** : FileStorageServiceTest — ajouter tests chemin succès, extensions, path traversal, IO exception
- [x] **Étape 2** : CartServiceImplTest — ajouter tests cas positifs (création panier, new item, update qty positive, remove, clear, totaux)
- [x] **Étape 3** : ProductServiceImplTest — ajouter getAllAvailableProducts, searchProducts, softDelete, updateStock, update complet
- [x] **Étape 4** : AdminProductControllerTest — catégorie null (création + update), update avec nouvelle image
- [x] **Étape 5** : CartControllerTest — exceptions dans add/update/remove/clear, update quantité positive
- [x] **Étape 6** : OrderControllerTest — commande d'un autre utilisateur, exceptions
- [x] **Étape 7** : CategoryControllerTest — création sans image, update avec nouvelle image
- [x] **Étape 8** : ClientProductControllerTest — branches manquantes
- [x] **Étape 9** : GlobalExceptionHandlerTest — test des deux handlers
- [x] **Étape 10** : BusinessExceptionTest — constructeur avec cause
- [ ] **Vérification finale** : mvnw clean test jacoco:report + vérification rapport

