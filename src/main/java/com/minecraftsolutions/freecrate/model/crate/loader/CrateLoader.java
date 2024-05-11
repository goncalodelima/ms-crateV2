package com.minecraftsolutions.freecrate.model.crate.loader;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.crate.Crate;
import com.minecraftsolutions.freecrate.model.crate.adapter.CrateAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class CrateLoader {

    private final CrateAdapter adapter;

    public CrateLoader(){
        this.adapter = new CrateAdapter();
    }

    public List<Crate> setup(){
        return CratePlugin.getInstance().getCrate().getConfig().getConfigurationSection("").getKeys(false)
                .stream()
                .map(key -> this.adapter.adapt(CratePlugin.getInstance().getCrate().getConfig().getConfigurationSection(key)))
                .collect(Collectors.toList());
    }

}
