package com.example.ecommerce.selenium;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests E2E Selenium pour le flux client complet :
 * navigation produits, panier, passage de commande.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientFlowSeleniumTest extends BaseSeleniumTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriverWait wait;

    @BeforeAll
    void initTestUser() {
        if (userRepository.findByEmail("selenium-client-flow@test.com").isEmpty()) {
            User client = new User();
            client.setNom("Selenium Client Flow");
            client.setEmail("selenium-client-flow@test.com");
            client.setPassword(passwordEncoder.encode("Password123!"));
            client.setRole(Role.CLIENT);
            userRepository.save(client);
        }
    }

    @BeforeEach
    void initWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void loginAsClient() {
        navigateTo("/login");
        driver.findElement(By.id("username")).sendKeys("selenium-client-flow@test.com");
        driver.findElement(By.id("password")).sendKeys("Password123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/client/homePatisserie"));
    }

    @Test
    @Order(1)
    @DisplayName("Browse products page and verify content")
    void browseProductsPage() {
        loginAsClient();
        navigateTo("/client/products");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("page-title")));

        assertTrue(driver.getPageSource().contains("Notre Collection"));
        assertTrue(driver.getPageSource().contains("Ajouter au panier") || driver.getPageSource().contains("Indisponible"));
    }

    @Test
    @Order(2)
    @DisplayName("Add first available product to cart")
    void addProductToCart() {
        loginAsClient();
        navigateTo("/client/products");

        // Trouver le premier bouton "Ajouter au panier" disponible
        List<WebElement> addButtons = driver.findElements(By.cssSelector("form[action^='/cart/add/'] button[type='submit']"));
        assertFalse(addButtons.isEmpty(), "Aucun produit disponible à l'ajout");

        WebElement firstAddBtn = addButtons.get(0);
        firstAddBtn.click();

        // Attendre la redirection vers le panier
        wait.until(ExpectedConditions.urlContains("/cart"));

        assertTrue(driver.getPageSource().contains("Mon Panier"));
        assertFalse(driver.getPageSource().contains("Votre panier est vide"));
    }

    @Test
    @Order(3)
    @DisplayName("Create order from cart and verify confirmation page")
    void createOrderFromCart() {
        loginAsClient();

        // Ajouter un produit au panier d'abord
        navigateTo("/client/products");
        List<WebElement> addButtons = driver.findElements(By.cssSelector("form[action^='/cart/add/'] button[type='submit']"));
        if (!addButtons.isEmpty()) {
            addButtons.get(0).click();
            wait.until(ExpectedConditions.urlContains("/cart"));
        }

        // Cliquer sur "Passer la commande"
        WebElement checkoutBtn = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("form[action='/orders/create'] button[type='submit']"))
        );
        checkoutBtn.click();

        // Attendre la page de confirmation
        wait.until(ExpectedConditions.urlContains("/orders/"));
        wait.until(ExpectedConditions.urlContains("/confirmation"));

        assertTrue(driver.getPageSource().contains("Commande créée avec succès"));
        assertTrue(driver.getPageSource().contains("Récapitulatif de la commande"));
    }

    @Test
    @Order(4)
    @DisplayName("View order history after placing an order")
    void viewOrderHistory() {
        loginAsClient();
        navigateTo("/orders/history");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertTrue(driver.getPageSource().contains("Historique") || driver.getPageSource().contains("Commande"));
    }

    @Test
    @Order(5)
    @DisplayName("Search products by keyword")
    void searchProducts() {
        loginAsClient();
        navigateTo("/client/products");

        WebElement searchInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("form[action='/client/products/search'] input[name='keyword']"))
        );
        searchInput.sendKeys("Croissant");

        WebElement searchBtn = driver.findElement(By.cssSelector("form[action='/client/products/search'] button[type='submit']"));
        searchBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("page-title")));

        assertTrue(driver.getPageSource().contains("Résultats de recherche") || driver.getPageSource().contains("Croissant"));
    }
}

