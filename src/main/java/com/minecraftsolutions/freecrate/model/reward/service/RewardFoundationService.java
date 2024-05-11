package com.minecraftsolutions.freecrate.model.reward.service;

import com.minecraftsolutions.freecrate.model.reward.Reward;

import java.util.Set;

public interface RewardFoundationService {

    Set<Reward> getAll();

    Reward get(String identifier);

    void put(Reward reward);

}
