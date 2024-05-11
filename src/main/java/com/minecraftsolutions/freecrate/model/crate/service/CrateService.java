package com.minecraftsolutions.freecrate.model.crate.service;

import com.minecraftsolutions.freecrate.model.crate.Crate;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CrateService implements CrateFoundationService {

    private final Map<String, Crate> cache;

    public CrateService(){
        this.cache = new HashMap<>();
    }

    @Override
    public List<Crate> getAll() {
        return this.cache
                .keySet()
                .stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public Crate get(String identifier) {
        return this.cache.get(identifier);
    }

    @Override
    public Optional<Crate> get(ItemStack item) {
        return this.cache
                .values()
                .stream()
                .filter(crate -> crate.getItem().isSimilar(item))
                .findAny();
    }

    @Override
    public void put(Crate crate) {
        this.cache.put(crate.getIdentifier(), crate);
    }

}
