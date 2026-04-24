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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests E2E Selenium pour les flux d'authentification.
 * Vérifie le login, l'inscription et les redirections de sécurité dans un vrai navigateur.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthSeleniumTest extends BaseSeleniumTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriverWait wait;

    @BeforeAll
    void initTestUsers() {
        // Créer un utilisateur CLIENT avec un mot de passe bcrypt valide
        // pour permettre un vrai login via le formulaire
        if (userRepository.findByEmail("selenium-client@test.com").isEmpty()) {
            User client = new User();
            client.setNom("Selenium Client");
            client.setEmail("selenium-client@test.com");
            client.setPassword(passwordEncoder.encode("Password123!"));
            client.setRole(Role.CLIENT);
            userRepository.save(client);
        }
    }

    @BeforeEach
    void initWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @Order(1)
    @DisplayName("Admin login redirects to admin dashboard")
    void adminLoginSuccess() {
        navigateTo("/login");

        driver.findElement(By.id("username")).sendKeys("admin@admin.com");
        driver.findElement(By.id("password")).sendKeys("testadmin");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/homeAdmin"));

        assertTrue(driver.getCurrentUrl().contains("/admin/homeAdmin"));
        assertTrue(driver.getPageSource().contains("Bienvenue, Admin"));
    }

    @Test
    @Order(2)
    @DisplayName("Client login redirects to patisserie home")
    void clientLoginSuccess() {
        navigateTo("/login");

        driver.findElement(By.id("username")).sendKeys("selenium-client@test.com");
        driver.findElement(By.id("password")).sendKeys("Password123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/client/homePatisserie"));

        assertTrue(driver.getCurrentUrl().contains("/client/homePatisserie"));
        assertTrue(driver.getPageSource().contains("Lalla Délices"));
    }

    @Test
    @Order(3)
    @DisplayName("Invalid login shows error message")
    void invalidLoginShowsError() {
        navigateTo("/login");

        driver.findElement(By.id("username")).sendKeys("wrong@user.com");
        driver.findElement(By.id("password")).sendKeys("wrongpassword");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("alert-error")));

        WebElement errorAlert = driver.findElement(By.className("alert-error"));
        assertTrue(errorAlert.getText().contains("incorrect"));
    }

    @Test
    @Order(4)
    @DisplayName("User registration redirects to login with success message")
    void userRegistrationSuccess() {
        navigateTo("/register");

        String uniqueEmail = "selenium-new-" + System.currentTimeMillis() + "@test.com";

        driver.findElement(By.id("nom")).sendKeys("New Selenium User");
        driver.findElement(By.id("email")).sendKeys(uniqueEmail);

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("Password123!");

        // Attendre que la validation JS active le bouton
        WebElement submitBtn = driver.findElement(By.id("submit-btn"));
        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
        submitBtn.click();

        wait.until(ExpectedConditions.urlContains("/login?registered=true"));

        assertTrue(driver.getCurrentUrl().contains("/login?registered=true"));
        assertTrue(driver.getPageSource().contains("succès"));
    }

    @Test
    @Order(5)
    @DisplayName("Anonymous access to admin page redirects to login")
    void anonymousAccessToAdminRedirectsToLogin() {
        navigateTo("/admin/homeAdmin");

        wait.until(ExpectedConditions.urlContains("/login"));

        assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}

