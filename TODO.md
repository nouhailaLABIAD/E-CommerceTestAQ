<<<<<<< Updated upstream
- [x] Ajouter les dépendances Selenium dans `pom.xml`
- [x] Créer la classe de base `BaseSeleniumTest.java`
- [x] Créer les tests E2E `AuthSeleniumTest.java`
- [x] Exécuter les tests pour validation
- [x] Créer les tests flux client `ClientFlowSeleniumTest.java`
- [x] Créer les tests admin CRUD `AdminCrudSeleniumTest.java`
- [x] Corriger `adminEditCategory` (ElementClickInterceptedException -> driver.get() + WebDriverWait)
- [x] Tous les tests Selenium passent (16/16)
=======
# Plan : Correction du bouton de déconnexion Admin

## Problème identifié
Dans les templates **admin**, le bouton de déconnexion utilisait un simple lien GET :
```html
<div class="logout"><a href="/logout">🚪 Déconnexion</a></div>
```
Spring Security exige par défaut une requête **POST** pour le logout (protection CSRF). C'est pourquoi le lien ne fonctionnait pas pour l'admin, alors que les pages **user** utilisaient correctement un formulaire POST.

## Fichiers modifiés
- [x] `src/main/resources/templates/homeAdmin.html`
- [x] `src/main/resources/templates/admin-products.html`
- [x] `src/main/resources/templates/admin-categories.html`
- [x] `src/main/resources/templates/admin-orders.html`
- [x] `src/main/resources/templates/product-form.html`
- [x] `src/main/resources/templates/category-form.html`
- [x] `src/main/resources/templates/order-detail.html`
- [x] `src/main/resources/templates/order-status-form.html`

## Modification appliquée dans chaque fichier
Remplacement de :
```html
<div class="logout"><a href="/logout">🚪 Déconnexion</a></div>
```
Par :
```html
<div class="logout">
    <form th:action="@{/logout}" method="post" style="display:inline; margin:0;">
        <button type="submit" style="background:none; border:none; color:inherit; font:inherit; cursor:pointer; padding:0;">🚪 Déconnexion</button>
    </form>
</div>
```

## Statut
✅ Terminé — Le bouton de déconnexion admin envoie désormais une requête POST conforme à la configuration Spring Security.

>>>>>>> Stashed changes
