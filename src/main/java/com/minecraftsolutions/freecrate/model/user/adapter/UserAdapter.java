package com.minecraftsolutions.freecrate.model.user.adapter;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.crate.Crate;
import com.minecraftsolutions.freecrate.model.reward.Reward;
import com.minecraftsolutions.freecrate.model.user.User;
import com.minecraftsolutions.database.adapter.DatabaseAdapter;
import com.minecraftsolutions.database.executor.DatabaseQuery;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter implements DatabaseAdapter<User> {

    @SneakyThrows
    @Override
    public User adapt(DatabaseQuery query) {

        Map<Crate, Integer> crates = new HashMap<>();
        if (query.get("crates") != null) {

            JSONObject object = (JSONObject) new JSONParser().parse((String) query.get("crates"));
            for (Object identifier : object.keySet()) {
                Crate crate = CratePlugin.getInstance().getCrateService().get((String) identifier);
                long amount = (long) object.get(identifier);
                crates.put(crate, (int) amount);
            }

        }

        Map<Reward, Integer> rewards = new HashMap<>();
        if (query.get("rewards") != null) {

            JSONObject object = (JSONObject) new JSONParser().parse((String) query.get("rewards"));
            for (Object identifier : object.keySet()) {
                Reward reward = CratePlugin.getInstance().getRewardService().get((String) identifier);
                long amount = (long) object.get(identifier);
                rewards.put(reward, (int) amount);
            }

        }

        List<Reward> filter = new ArrayList<>();
        if (query.get("filter") != null) {

            JSONArray array = (JSONArray) new JSONParser().parse((String) query.get("filter"));
            array.forEach(obj -> filter.add(CratePlugin.getInstance().getRewardService().get((String) obj)));

        }

        return User.builder()
                .name((String) query.get("name"))
                .crates(crates)
                .filter(filter)
                .rewards(rewards)
                .cratesOpen((Integer) query.get("cratesopen"))
                .build();
    }

}
