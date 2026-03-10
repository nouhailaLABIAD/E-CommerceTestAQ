package com.example.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service de gestion du stockage des images uploadées.
 * Les fichiers sont sauvegardés dans : src/main/resources/static/uploads/{type}/
 * et accessibles via l'URL : /uploads/{type}/fichier.jpg
 */
@Service
public class FileStorageService {

    // Répertoire racine des uploads (dans static pour être servi par Spring Boot)
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

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
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Générer un nom unique pour éviter les collisions
        String newFilename = UUID.randomUUID().toString() + extension;

        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR + subDir);
        Files.createDirectories(uploadPath);

        // Copier le fichier
        Path filePath = uploadPath.resolve(newFilename);
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
            String filePath = "src/main/resources/static" + imageUrl;
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log silencieux — ne pas bloquer l'opération principale
            System.err.println("Impossible de supprimer l'image : " + imageUrl);
        }
    }
}