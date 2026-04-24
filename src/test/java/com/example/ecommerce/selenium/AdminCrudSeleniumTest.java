package com.example.ecommerce.selenium;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests E2E Selenium pour les opérations Admin CRUD :
 * produits et catégories (création, modification, suppression).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminCrudSeleniumTest extends BaseSeleniumTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriverWait wait;

    private static String createdProductName;
    private static String createdCategoryName;

    @BeforeAll
    void initTestAdmin() {
        // S'assurer que l'admin existe avec un mot de passe connu
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            User admin = new User();
            admin.setNom("Administrator");
            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("testadmin"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }

    @BeforeEach
    void initWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void loginAsAdmin() {
        navigateTo("/login");
        driver.findElement(By.id("username")).sendKeys("admin@admin.com");
        driver.findElement(By.id("password")).sendKeys("testadmin");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin/homeAdmin"));
    }

    // ─── PRODUITS ───────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Admin creates a new product")
    void adminCreateProduct() {
        loginAsAdmin();
        navigateTo("/admin/products/new");

        createdProductName = "Selenium-Produit-" + System.currentTimeMillis();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nom")));

        driver.findElement(By.id("nom")).sendKeys(createdProductName);
        driver.findElement(By.id("description")).sendKeys("Produit créé par Selenium E2E");
        driver.findElement(By.id("prix")).sendKeys("99.99");
        driver.findElement(By.id("stock")).sendKeys("50");

        // Sélectionner la première catégorie disponible
        Select categorySelect = new Select(driver.findElement(By.id("categoryId")));
        categorySelect.selectByIndex(1); // index 0 = "Choisir", index 1 = première catégorie

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/products"));

        assertTrue(driver.getPageSource().contains(createdProductName));
    }

    @Test
    @Order(2)
    @DisplayName("Admin edits the created product")
    void adminEditProduct() {
        loginAsAdmin();
        navigateTo("/admin/products");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));

        // Trouver le lien d'édition du produit créé et naviguer directement
        WebElement editLink = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + createdProductName + "')]//a[contains(@href, '/admin/products/edit/')]")
            )
        );
        driver.get(editLink.getAttribute("href"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nom")));

        WebElement nomField = driver.findElement(By.id("nom"));
        nomField.clear();
        nomField.sendKeys(createdProductName + "-MODIFIE");

        driver.findElement(By.id("prix")).clear();
        driver.findElement(By.id("prix")).sendKeys("149.99");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/products"));

        assertTrue(driver.getPageSource().contains(createdProductName + "-MODIFIE"));
        assertTrue(driver.getPageSource().contains("149.99"));
    }

    @Test
    @Order(3)
    @DisplayName("Admin deletes the created product")
    void adminDeleteProduct() {
        loginAsAdmin();
        navigateTo("/admin/products");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));

        // Extraire l'ID du produit depuis le lien d'édition
        WebElement editLink = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + createdProductName + "-MODIFIE')]//a[contains(@href, '/admin/products/edit/')]")
            )
        );
        String href = editLink.getAttribute("href");
        String productId = href.substring(href.lastIndexOf('/') + 1);

        // Supprimer directement via l'URL (évite le confirm() JS)
        navigateTo("/admin/products/delete/" + productId);

        wait.until(ExpectedConditions.urlContains("/admin/products"));

        assertFalse(driver.getPageSource().contains(createdProductName + "-MODIFIE"));
    }

    // ─── CATÉGORIES ─────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Admin creates a new category")
    void adminCreateCategory() {
        loginAsAdmin();
        navigateTo("/admin/categories/new");

        createdCategoryName = "Selenium-Categorie-" + System.currentTimeMillis();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nom")));

        driver.findElement(By.id("nom")).sendKeys(createdCategoryName);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/categories"));

        assertTrue(driver.getPageSource().contains(createdCategoryName));
    }

    @Test
    @Order(5)
    @DisplayName("Admin edits the created category")
    void adminEditCategory() {
        loginAsAdmin();
        navigateTo("/admin/categories");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));

        // Trouver le lien d'édition et naviguer directement (évite les problèmes de clic intercepté)
        WebElement editLink = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + createdCategoryName + "')]//a[contains(@href, '/admin/categories/edit/')]")
            )
        );
        driver.get(editLink.getAttribute("href"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nom")));

        WebElement nomField = driver.findElement(By.id("nom"));
        nomField.clear();
        nomField.sendKeys(createdCategoryName + "-MODIFIE");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Attendre explicitement la redirection ET la présence du texte modifié dans le DOM
        wait.until(ExpectedConditions.urlContains("/admin/categories"));
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[contains(text(), '" + createdCategoryName + "-MODIFIE')]")
        ));

        assertTrue(driver.getPageSource().contains(createdCategoryName + "-MODIFIE"));
    }

    @Test
    @Order(6)
    @DisplayName("Admin deletes the created category")
    void adminDeleteCategory() {
        loginAsAdmin();
        navigateTo("/admin/categories");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));

        WebElement editLink = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + createdCategoryName + "-MODIFIE')]//a[contains(@href, '/admin/categories/edit/')]")
            )
        );
        String href = editLink.getAttribute("href");
        String categoryId = href.substring(href.lastIndexOf('/') + 1);

        navigateTo("/admin/categories/delete/" + categoryId);

        wait.until(ExpectedConditions.urlContains("/admin/categories"));

        assertFalse(driver.getPageSource().contains(createdCategoryName + "-MODIFIE"));
    }
}

