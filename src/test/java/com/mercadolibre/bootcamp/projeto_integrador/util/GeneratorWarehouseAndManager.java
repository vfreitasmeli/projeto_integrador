package com.mercadolibre.bootcamp.projeto_integrador.util;

import com.mercadolibre.bootcamp.projeto_integrador.model.Manager;
import com.mercadolibre.bootcamp.projeto_integrador.model.Warehouse;

public class GeneratorWarehouseAndManager {
    public static Warehouse newWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("New York");
        return warehouse;
    }

    public static Manager newManager() {
        Manager manager = new Manager();
        manager.setName("John Doe");
        manager.setUsername("john");
        manager.setEmail("john@example.com");
        return manager;
    }

    public static Manager getManagerWithId() {
        Manager manager = new Manager();
        manager.setName("John Doe");
        manager.setUsername("john");
        manager.setEmail("john@example.com");
        manager.setManagerId(1l);
        return manager;
    }
}
