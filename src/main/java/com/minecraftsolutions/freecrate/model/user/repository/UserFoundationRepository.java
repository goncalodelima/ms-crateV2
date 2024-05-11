package com.minecraftsolutions.freecrate.model.user.repository;

import com.minecraftsolutions.freecrate.model.user.User;

import java.util.List;

public interface UserFoundationRepository {

    void setup();

    User findOne(String name);

    void insert(User user);

    void update(User user);

    List<User> findTop();

}
