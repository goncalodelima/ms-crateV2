package com.minecraftsolutions.freecrate.model.user.service;

import com.minecraftsolutions.freecrate.model.user.User;

import java.util.List;

public interface UserFoundationService {

    List<User> getAll();

    User get(String name);

    void put(User user);

    void remove(User user);

    void update(User user);

    List<User> getTop();

}
