package com.minecraftsolutions.freecrate.model.reward.service;

import com.minecraftsolutions.freecrate.model.reward.Reward;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RewardService implements RewardFoundationService {

    private final Map<String, Reward> cache;

    public RewardService(){
        this.cache = new HashMap<>();
    }

    @Override
    public Set<Reward> getAll() {
        return this.cache
                .keySet()
                .stream()
                .map(this::get)
                .collect(Collectors.toSet());
    }

    @Override
    public Reward get(String identifier) {
        return this.cache.get(identifier);
    }

    @Override
    public void put(Reward reward) {
        this.cache.put(reward.getIdentifier(), reward);
    }

}
