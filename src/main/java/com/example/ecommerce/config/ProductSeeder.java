package com.example.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;

@Component
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductSeeder(ProductRepository productRepository,
                         CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {

        if (productRepository.count() > 0) return;

        // ─── CATÉGORIES ────────────────────────────────────────────
        Category patisseries = new Category();
        patisseries.setNom("Pâtisseries");
        categoryRepository.save(patisseries);

        Category viennoiseries = new Category();
        viennoiseries.setNom("Viennoiseries");
        categoryRepository.save(viennoiseries);

        Category desserts = new Category();
        desserts.setNom("Desserts");
        categoryRepository.save(desserts);

        Category boissons = new Category();
        boissons.setNom("Boissons");
        categoryRepository.save(boissons);

        Category briwat = new Category();
        briwat.setNom("Briwat");
        categoryRepository.save(briwat);

        Category bastila = new Category();
        bastila.setNom("Bastila");
        categoryRepository.save(bastila);

        // ─── PRODUITS EXISTANTS ────────────────────────────────────
        Product p1 = new Product();
        p1.setNom("Croissant Beurre");
        p1.setDescription("Croissant artisanal au beurre frais");
        p1.setPrix(4.50);
        p1.setStock(50);
        p1.setCategory(viennoiseries);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setNom("Tarte aux Fraises");
        p2.setDescription("Tarte garnie de fraises fraîches et crème pâtissière");
        p2.setPrix(12.00);
        p2.setStock(20);
        p2.setCategory(patisseries);
        productRepository.save(p2);

        Product p3 = new Product();
        p3.setNom("Macaron Assortis");
        p3.setDescription("Boîte de 12 macarons assortis (chocolat, pistache, fraise, citron)");
        p3.setPrix(18.00);
        p3.setStock(30);
        p3.setCategory(patisseries);
        productRepository.save(p3);

        Product p4 = new Product();
        p4.setNom("Éclair au Chocolat");
        p4.setDescription("Éclair garni de crème au chocolat et glaçage fondant");
        p4.setPrix(5.50);
        p4.setStock(25);
        p4.setCategory(patisseries);
        productRepository.save(p4);

        Product p5 = new Product();
        p5.setNom("Mille-feuille");
        p5.setDescription("Pâte feuilletée dorée, crème pâtissière à la vanille, glaçage");
        p5.setPrix(6.00);
        p5.setStock(15);
        p5.setCategory(patisseries);
        productRepository.save(p5);

        Product p6 = new Product();
        p6.setNom("Pain au Chocolat");
        p6.setDescription("Pain au chocolat artisanal, feuilletage croustillant");
        p6.setPrix(3.50);
        p6.setStock(40);
        p6.setCategory(viennoiseries);
        productRepository.save(p6);

        Product p7 = new Product();
        p7.setNom("Chocolat Chaud");
        p7.setDescription("Chocolat belge chaud avec chantilly maison");
        p7.setPrix(5.00);
        p7.setStock(100);
        p7.setCategory(boissons);
        productRepository.save(p7);

        Product p8 = new Product();
        p8.setNom("Crème Brûlée");
        p8.setDescription("Crème brûlée à la vanille de Madagascar, sucre caramélisé");
        p8.setPrix(7.50);
        p8.setStock(18);
        p8.setCategory(desserts);
        productRepository.save(p8);

        // ─── BRIWAT ────────────────────────────────────────────────

        // Briwat Poulet
        Product bw1 = new Product();
        bw1.setNom("Briwat Poulet (pièce)");
        bw1.setDescription("Briwat croustillant farci au poulet épicé, oignon et persil, frit à la commande");
        bw1.setPrix(3.50);
        bw1.setStock(60);
        bw1.setCategory(briwat);
        productRepository.save(bw1);

        Product bw2 = new Product();
        bw2.setNom("Briwat Poulet (plateau 12 pcs)");
        bw2.setDescription("Plateau de 12 briwats au poulet, idéal pour les fêtes et réceptions");
        bw2.setPrix(38.00);
        bw2.setStock(20);
        bw2.setCategory(briwat);
        productRepository.save(bw2);

        // Briwat Viande Hachée
        Product bw3 = new Product();
        bw3.setNom("Briwat Viande Hachée (pièce)");
        bw3.setDescription("Briwat doré à la viande hachée assaisonnée aux épices marocaines");
        bw3.setPrix(3.50);
        bw3.setStock(60);
        bw3.setCategory(briwat);
        productRepository.save(bw3);

        Product bw4 = new Product();
        bw4.setNom("Briwat Viande Hachée (plateau 12 pcs)");
        bw4.setDescription("Plateau de 12 briwats à la viande hachée, parfait pour vos tables");
        bw4.setPrix(38.00);
        bw4.setStock(20);
        bw4.setCategory(briwat);
        productRepository.save(bw4);

        // Briwat Amande
        Product bw5 = new Product();
        bw5.setNom("Briwat Amande & Miel (pièce)");
        bw5.setDescription("Briwat sucré à la pâte d'amande parfumée à la fleur d'oranger, nappé de miel");
        bw5.setPrix(4.00);
        bw5.setStock(50);
        bw5.setCategory(briwat);
        productRepository.save(bw5);

        Product bw6 = new Product();
        bw6.setNom("Briwat Amande & Miel (plateau 12 pcs)");
        bw6.setDescription("Plateau de 12 briwats amande-miel, spécialité marocaine incontournable");
        bw6.setPrix(44.00);
        bw6.setStock(15);
        bw6.setCategory(briwat);
        productRepository.save(bw6);

        // Briwat Fromage
        Product bw7 = new Product();
        bw7.setNom("Briwat Fromage (pièce)");
        bw7.setDescription("Briwat croustillant au fromage fondu et fines herbes fraîches");
        bw7.setPrix(3.50);
        bw7.setStock(50);
        bw7.setCategory(briwat);
        productRepository.save(bw7);

        Product bw8 = new Product();
        bw8.setNom("Briwat Fromage (plateau 12 pcs)");
        bw8.setDescription("Plateau de 12 briwats fromage-herbes, fondants à cœur");
        bw8.setPrix(38.00);
        bw8.setStock(15);
        bw8.setCategory(briwat);
        productRepository.save(bw8);

        // Briwat Crevettes
        Product bw9 = new Product();
        bw9.setNom("Briwat Crevettes (pièce)");
        bw9.setDescription("Briwat aux crevettes marinées, citron et coriandre fraîche");
        bw9.setPrix(5.00);
        bw9.setStock(40);
        bw9.setCategory(briwat);
        productRepository.save(bw9);

        // Briwat Kefta
        Product bw10 = new Product();
        bw10.setNom("Briwat Kefta (pièce)");
        bw10.setDescription("Briwat à la kefta épicée, cumin, paprika et herbes marocaines");
        bw10.setPrix(4.00);
        bw10.setStock(40);
        bw10.setCategory(briwat);
        productRepository.save(bw10);

        // ─── BASTILA ───────────────────────────────────────────────

        // Mini Bastila Poulet
        Product ba1 = new Product();
        ba1.setNom("Mini Bastila Poulet (pièce)");
        ba1.setDescription("Mini bastila individuelle au poulet effiloché, amandes grillées, œufs et cannelle, enveloppée de feuille de ouarka");
        ba1.setPrix(8.00);
        ba1.setStock(30);
        ba1.setCategory(bastila);
        productRepository.save(ba1);

        Product ba2 = new Product();
        ba2.setNom("Mini Bastila Poulet (plateau 6 pcs)");
        ba2.setDescription("Plateau de 6 mini bastila au poulet, dorées au four, saupoudrées de sucre glace et cannelle");
        ba2.setPrix(44.00);
        ba2.setStock(15);
        ba2.setCategory(bastila);
        productRepository.save(ba2);

        // Mini Bastila Poisson
        Product ba3 = new Product();
        ba3.setNom("Mini Bastila Poisson (pièce)");
        ba3.setDescription("Mini bastila au poisson blanc, vermicelles, épices chermoula et citron confit");
        ba3.setPrix(9.00);
        ba3.setStock(25);
        ba3.setCategory(bastila);
        productRepository.save(ba3);

        Product ba4 = new Product();
        ba4.setNom("Mini Bastila Poisson (plateau 6 pcs)");
        ba4.setDescription("Plateau de 6 mini bastila poisson-chermoula, croustillantes et parfumées");
        ba4.setPrix(50.00);
        ba4.setStock(12);
        ba4.setCategory(bastila);
        productRepository.save(ba4);

        // Bastila Poulet — tailles familles
        Product ba5 = new Product();
        ba5.setNom("Bastila Poulet — 4 personnes");
        ba5.setDescription("Bastila traditionnelle au poulet pour 4 personnes, amandes, œufs, cannelle — livrée prête à réchauffer");
        ba5.setPrix(65.00);
        ba5.setStock(10);
        ba5.setCategory(bastila);
        productRepository.save(ba5);

        Product ba6 = new Product();
        ba6.setNom("Bastila Poulet — 6 personnes");
        ba6.setDescription("Bastila généreuse au poulet pour 6 personnes, dorée au four, sucre glace et cannelle");
        ba6.setPrix(90.00);
        ba6.setStock(8);
        ba6.setCategory(bastila);
        productRepository.save(ba6);

        Product ba7 = new Product();
        ba7.setNom("Bastila Poulet — 10 personnes");
        ba7.setDescription("Grande bastila au poulet pour 10 personnes, idéale pour événements et cérémonies");
        ba7.setPrix(140.00);
        ba7.setStock(5);
        ba7.setCategory(bastila);
        productRepository.save(ba7);

        // Bastila Poisson — tailles familles
        Product ba8 = new Product();
        ba8.setNom("Bastila Poisson — 4 personnes");
        ba8.setDescription("Bastila au poisson blanc et fruits de mer pour 4 personnes, épices douces et citron confit");
        ba8.setPrix(75.00);
        ba8.setStock(8);
        ba8.setCategory(bastila);
        productRepository.save(ba8);

        Product ba9 = new Product();
        ba9.setNom("Bastila Poisson — 6 personnes");
        ba9.setDescription("Bastila au poisson pour 6 personnes, garniture généreuse aux saveurs de la mer");
        ba9.setPrix(105.00);
        ba9.setStock(6);
        ba9.setCategory(bastila);
        productRepository.save(ba9);

        Product ba10 = new Product();
        ba10.setNom("Bastila Poisson — 10 personnes");
        ba10.setDescription("Grande bastila poisson pour 10 personnes, parfaite pour mariages et grandes tablées");
        ba10.setPrix(165.00);
        ba10.setStock(4);
        ba10.setCategory(bastila);
        productRepository.save(ba10);

        // Bastila Viande — tailles familles
        Product ba11 = new Product();
        ba11.setNom("Bastila Viande — 4 personnes");
        ba11.setDescription("Bastila à la viande d'agneau pour 4 personnes, amandes grillées, raisins et épices ras el hanout");
        ba11.setPrix(80.00);
        ba11.setStock(8);
        ba11.setCategory(bastila);
        productRepository.save(ba11);

        Product ba12 = new Product();
        ba12.setNom("Bastila Viande — 6 personnes");
        ba12.setDescription("Bastila agneau pour 6 personnes, feuilletage croustillant, farce généreuse aux épices marocaines");
        ba12.setPrix(115.00);
        ba12.setStock(5);
        ba12.setCategory(bastila);
        productRepository.save(ba12);

        Product ba13 = new Product();
        ba13.setNom("Bastila Viande — 10 personnes");
        ba13.setDescription("Grande bastila agneau pour 10 personnes, commande événementielle, livraison possible");
        ba13.setPrix(180.00);
        ba13.setStock(3);
        ba13.setCategory(bastila);
        productRepository.save(ba13);
    }
}