package com.minecraftsolutions.freecrate.model.user.repository;

import com.minecraftsolutions.freecrate.model.user.User;
import com.minecraftsolutions.freecrate.model.user.adapter.RankingAdapter;
import com.minecraftsolutions.freecrate.model.user.adapter.UserAdapter;
import com.minecraftsolutions.database.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class UserRepository implements UserFoundationRepository {

    private final Database database;
    private final UserAdapter userAdapter;
    private final RankingAdapter rankingAdapter;

    public UserRepository(Database database){
        this.database = database;
        this.userAdapter = new UserAdapter();
        this.rankingAdapter = new RankingAdapter();
    }

    @Override
    public void setup() {
        database
                .execute("CREATE TABLE IF NOT EXISTS crates_user (name VARCHAR(255), crates TEXT, filter TEXT, rewards TEXT, cratesopen INTEGER)")
                .write();
    }

    @Override
    public User findOne(String name) {
        return database
                .execute("SELECT * FROM crates_user WHERE name = ?")
                .readOneWithAdapter(statement -> statement.set(1, name), this.userAdapter)
                .join();
    }

    @Override
    public void insert(User user) {

        JSONObject crates = new JSONObject();
        user.getCrates().forEach((crate, amount) -> crates.put(crate.getIdentifier(), amount));
        JSONObject rewards = new JSONObject();
        user.getCrates().forEach((reward, amount) -> rewards.put(reward.getIdentifier(), amount));
        JSONArray filter = new JSONArray();
        user.getFilter().forEach(reward -> filter.add(reward.getIdentifier()));

        database
                .execute("INSERT INTO crates_user (name, crates, filter, rewards, cratesopen) VALUES(?,?,?,?,?)")
                .write(statement -> {
                    statement.set(1, user.getName());
                    statement.set(2, crates.isEmpty() ? null : crates.toJSONString());
                    statement.set(3, filter.isEmpty() ? null : filter.toJSONString());
                    statement.set(4, rewards.isEmpty() ? null : rewards.toJSONString());
                    statement.set(5, user.getCratesOpen());
                });
    }

    @Override
    public void update(User user) {

        JSONObject crates = new JSONObject();
        user.getCrates().forEach((crate, amount) -> crates.put(crate.getIdentifier(), amount));
        JSONObject rewards = new JSONObject();
        user.getRewards().forEach((reward, amount) -> rewards.put(reward.getIdentifier(), amount));
        JSONArray filter = new JSONArray();
        user.getFilter().forEach(reward -> filter.add(reward.getIdentifier()));

        database
                .execute("UPDATE crates_user SET crates = ?, filter = ?, rewards = ?, cratesopen = ? WHERE name = ?")
                .write(statement -> {
                    statement.set(1, crates.isEmpty() ? null : crates.toJSONString());
                    statement.set(2, filter.isEmpty() ? null : filter.toJSONString());
                    statement.set(3, rewards.isEmpty() ? null : rewards.toJSONString());
                    statement.set(4, user.getCratesOpen());
                    statement.set(5, user.getName());
                });

    }

    @Override
    public List<User> findTop() {
        return database
                .execute("SELECT * FROM crates_user ORDER BY cratesopen DESC")
                .readOneWithAdapter(statement -> {}, this.rankingAdapter)
                .join();
    }

}
