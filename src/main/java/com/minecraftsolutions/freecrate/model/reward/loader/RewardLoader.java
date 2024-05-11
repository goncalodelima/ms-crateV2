package com.minecraftsolutions.freecrate.model.reward.loader;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.reward.Reward;
import com.minecraftsolutions.freecrate.model.reward.adapter.RewardAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class RewardLoader {

    private final RewardAdapter adapter;

    public RewardLoader(){
        this.adapter = new RewardAdapter();
    }

    public List<Reward> setup(){
        return CratePlugin.getInstance().getReward().getConfig().getConfigurationSection("").getKeys(false)
                .stream()
                .map(key -> this.adapter.adapt(CratePlugin.getInstance().getReward().getConfig().getConfigurationSection(key)))
                .collect(Collectors.toList());
    }

}
