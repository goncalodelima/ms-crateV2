package com.minecraftsolutions.freecrate.model.crate.service;

import com.minecraftsolutions.freecrate.model.crate.Crate;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface CrateFoundationService {

    List<Crate> getAll();

    Crate get(String identifier);

    Optional<Crate> get(ItemStack item);

    void put(Crate crate);

}
