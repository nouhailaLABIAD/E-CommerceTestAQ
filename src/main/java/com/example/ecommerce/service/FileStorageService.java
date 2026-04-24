package com.example.ecommerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Service de gestion du stockage des images uploadées.
 * Les fichiers sont sauvegardés dans : src/main/resources/static/uploads/{type}/
 * et accessibles via l'URL : /uploads/{type}/fichier.jpg
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    // REVIEW: Extensions d'image autorisées (whitelist) pour sécurité
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    @Value("${upload.dir:src/main/resources/static/uploads/}")
    private String uploadDir;

    /**
     * Sauvegarde un fichier image et retourne son URL publique.
     *
     * @param file     Le fichier uploadé
     * @param subDir   Sous-dossier : "products" ou "categories"
     * @return L'URL publique accessible depuis le navigateur (ex: /uploads/products/uuid.jpg)
     */
    public String saveImage(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Valider le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image.");
        }

        // Extraire l'extension d'origine
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // REVIEW: Validation stricte de l'extension (whitelist)
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Extension d'image non autorisée : " + extension);
        }

        // Générer un nom unique pour éviter les collisions
        String newFilename = UUID.randomUUID().toString() + extension;

        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir + subDir).normalize();
        Files.createDirectories(uploadPath);

        // REVIEW: Vérification anti-traversal — le fichier résolu doit être sous uploadPath
        Path filePath = uploadPath.resolve(newFilename).normalize();
        if (!filePath.startsWith(uploadPath)) {
            throw new IllegalArgumentException("Chemin de fichier invalide détecté (path traversal)");
        }

        // Copier le fichier
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retourner l'URL publique
        return "/uploads/" + subDir + "/" + newFilename;
    }

    /**
     * Supprime un fichier image du disque.
     *
     * @param imageUrl L'URL publique (ex: /uploads/products/uuid.jpg)
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;
        try {
            // Convertir l'URL en chemin fichier
            String filePath = uploadDir.replaceAll("/$", "") + imageUrl;
            Path path = Paths.get(filePath).normalize();
            Path basePath = Paths.get(uploadDir).normalize();

            // REVIEW: Vérification anti-traversal lors de la suppression
            if (!path.startsWith(basePath)) {
                logger.warn("Tentative de suppression en dehors du répertoire autorisé : {}", imageUrl);
                return;
            }

            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.warn("Impossible de supprimer l'image : {}", imageUrl, e);
        }
    }
}
