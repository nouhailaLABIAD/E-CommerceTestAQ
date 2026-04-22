-- FIX: suppression de la colonne "prenom" qui n'existe pas dans la table users
-- La table users contient : id, email, password, nom, role
INSERT INTO users (email, password, nom, role)
VALUES ('client@test.com', '$2a$10$demo', 'Test Client', 'CLIENT');