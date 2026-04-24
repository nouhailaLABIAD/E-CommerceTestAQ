# Rapport d'Analyse des Hotspots de Sécurité — SonarQube

**Projet :** E-Commerce Spring Boot  
**Date :** 2025-04-24  
**Auteur :** Expert Qualité Logicielle  
**Objectif :** Passage du Quality Gate — Revue des hotspots de sécurité (0% → 100% reviewed)

---

## Résumé Exécutif

| Règle SonarQube | Fichier(s) concernés | Statut | Action |
|---|---|---|---|
| S4502 — CSRF désactivé | `SecurityConfig.java` | **Corrigé** | Suppression de `.csrf(csrf -> csrf.disable())` — CSRF actif par défaut |
| S5122 — FrameOptions permissif | `SecurityConfig.java` | **Corrigé** | `disable()` → `sameOrigin()` |
| S2068 — Mot de passe en dur | `AdminSeeder.java`, `application.properties` | **Corrigé** | `@Value("${admin.password:}")` + garde `isBlank()` + variable d'env |
| Path Traversal (upload) | `FileStorageService.java` | **Corrigé** | `Path.normalize()` + whitelist extensions |
| S106 — `System.err.println` | `FileStorageService.java` | **Corrigé** | Remplacé par SLF4J `Logger` |
| S112 — `RuntimeException` générique | Services métier | **Corrigé** | Exceptions métiers typées |
| DOM XSS potentiel (`innerHTML`) | `register.html` | **Corrigé** | Remplacé par `textContent` |

---

## 1. S4502 — Désactivation de la protection CSRF (`SecurityConfig.java`)

### Localisation (avant correction)
```java
.csrf(csrf -> csrf.disable())
```

### Correction appliquée
Suppression complète de la ligne. Spring Security active CSRF par défaut.

### Justification
- L'application utilise des formulaires Thymeleaf avec `th:action` qui injecte automatiquement le token CSRF.
- Tous les endpoints POST sensibles (`/cart/**`, `/orders/**`, `/admin/**`) sont protégés.
- Les tests MockMvc utilisent déjà `.with(csrf())` là où nécessaire.
- **Avantage :** Suppression du hotspot SonarQube sans revue manuelle UI.

---

## 2. S5122 — Frame Options permissif (`SecurityConfig.java`)

### Localisation (avant correction)
```java
.frameOptions(frame -> frame.disable())
```

### Correction appliquée
```java
.frameOptions(frame -> frame.sameOrigin())
```

### Justification
`disable()` supprimait entièrement la protection contre le clickjacking.  
`sameOrigin()` permet l'affichage en iframe uniquement depuis le même domaine (nécessaire pour la console H2 en développement), tout en bloquant les embeddings tiers malveillants.

---

## 3. S2068 — Mot de passe administrateur en dur (`AdminSeeder.java` + `application.properties`)

### Localisation (avant correction)
```java
// AdminSeeder.java
@Value("${admin.password:admin123}")
private String adminPassword;

// application.properties
admin.password=admin123
```

### Correction appliquée
```java
// AdminSeeder.java
@Value("${admin.password:}")
private String adminPassword;

// dans run()
if (adminPassword == null || adminPassword.isBlank()) {
    return;
}

// application.properties
admin.password=${ADMIN_PASSWORD:}
```

### Justification
- Plus aucun mot de passe codé en dur dans le code source ou les fichiers de configuration commités.
- Le compte admin n'est créé que si `ADMIN_PASSWORD` est définie via variable d'environnement.
- Les tests utilisent `admin.password=testadmin` dans `src/test/resources/application.properties`.

---

## 4. Path Traversal (Upload de fichiers — `FileStorageService.java`)

### Localisation (avant correction)
```java
Path uploadPath = Paths.get(UPLOAD_DIR + subDir);
// Pas de validation du chemin résolu
```

### Correction appliquée
```java
Path uploadPath = Paths.get(uploadDir + subDir).normalize();
Path filePath = uploadPath.resolve(newFilename).normalize();
if (!filePath.startsWith(uploadPath)) {
    throw new IllegalArgumentException("Chemin de fichier invalide détecté (path traversal)");
}
```

Avec une **whitelist d'extensions** :
```java
private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp", ".gif");
```

### Justification
Un attaquant pouvait exploiter `subDir` ou `originalFilename` pour écrire en dehors du répertoire d'upload.  
La normalisation + la vérification `startsWith()` empêche toute écriture hors du répertoire de destination. La whitelist d'extensions empêche l'upload de fichiers exécutables.

---

## 5. S106 — Utilisation de `System.err.println` (`FileStorageService.java`)

### Localisation (avant correction)
```java
System.err.println("Impossible de supprimer l'image : " + imageUrl);
```

### Correction appliquée
```java
private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
// ...
logger.warn("Impossible de supprimer l'image : {}", imageUrl, e);
```

### Justification
`System.err.println` est non configurable, non filtrable et peut fuir des informations sensibles dans les logs de production. SLF4J offre des niveaux de log, une centralisation et une gestion des exceptions structurée.

---

## 6. S112 — `RuntimeException` générique (Services métier)

### Fichiers concernés
- `OrderServiceImpl.java`
- `CartServiceImpl.java`
- `ProductServiceImpl.java`
- `CategoryServiceImpl.java`

### Correction appliquée
Création d'une hiérarchie d'exceptions métiers :
- `BusinessException` (classe de base)
- `OrderNotFoundException`
- `EmptyCartException`
- `InsufficientStockException`
- `ProductNotFoundException`
- `ProductUnavailableException`
- `CategoryNotFoundException`

### Justification
`RuntimeException` générique masque les causes réelles, empêche une gestion d'erreur fine par le client et pollue les logs avec des messages non structurés. Les exceptions typées permettent au `GlobalExceptionHandler` de retourner des codes HTTP appropriés (404 vs 400 vs 500).

---

## 7. DOM XSS potentiel — `innerHTML` (`register.html`)

### Localisation (avant correction)
```javascript
feedback.innerHTML = '❌ Min 6 caractères';
```

### Correction appliquée
```javascript
feedback.textContent = '❌ Min 6 caractères';
```

### Justification
`innerHTML` interprète le contenu comme du HTML, ce qui peut constituer un vecteur XSS si des données utilisateur sont injectées. `textContent` traite le contenu comme du texte brut, éliminant tout risque d'injection de script.

---

## Vérification finale

| Critère Quality Gate | Avant | Après | Statut |
|---|---|---|---|
| Hotspots revus | 0% (E) | 100% | ✅ |
| Bugs / Vulnérabilités | 0 | 0 | ✅ |
| Duplications | 0% | 0% | ✅ |
| Tests | 137 passés | 137 passés | ✅ |

**Conclusion :** Tous les hotspots de sécurité ont été corrigés par modification du code source. Aucune revue manuelle dans l'interface SonarQube n'est nécessaire car les déclencheurs de règles ont été éliminés. Le critère "Security Hotspots Reviewed" du Quality Gate est satisfait.

