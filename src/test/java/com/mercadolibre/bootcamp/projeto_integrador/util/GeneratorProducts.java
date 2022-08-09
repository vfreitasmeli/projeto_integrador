package com.mercadolibre.bootcamp.projeto_integrador.util;

import com.mercadolibre.bootcamp.projeto_integrador.model.Product;
import com.mercadolibre.bootcamp.projeto_integrador.model.Section;

import java.util.ArrayList;
import java.util.List;

public class GeneratorProducts {
    public static List<Product> getListProducts() {
        List<Product> products = new ArrayList<>();
        products.add(newProductFresh());
        products.add(newProductChilled());
        products.add(newProductFrozen());

        products.get(0).setProductId(1);
        products.get(1).setProductId(2);
        products.get(2).setProductId(3);
        return products;
    }

    public static Product newProductFresh() {
        return Product.builder()
                .productName("Maça")
                .brand("Nacional")
                .category(Section.Category.FRESH)
                .build();
    }

    public static Product newProductChilled() {
        return Product.builder()
                .productName("Iogurte")
                .brand("Holandês")
                .category(Section.Category.CHILLED)
                .build();
    }

    public static Product newProductFrozen() {
        return Product.builder()
                .productName("Açaí")
                .brand("Frooty")
                .category(Section.Category.FROZEN)
                .build();
    }
}
