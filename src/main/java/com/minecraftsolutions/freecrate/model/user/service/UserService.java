package com.minecraftsolutions.freecrate.model.user.service;

import com.minecraftsolutions.freecrate.model.user.User;
import com.minecraftsolutions.freecrate.model.user.repository.UserFoundationRepository;
import com.minecraftsolutions.freecrate.model.user.repository.UserRepository;
import com.minecraftsolutions.database.Database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserService implements UserFoundationService {

    private final Map<String, User> cache;
    private final UserFoundationRepository userRepository;

    public UserService(Database database){
        this.cache = new HashMap<>();
        this.userRepository = new UserRepository(database);
        this.userRepository.setup();
    }

    @Override
    public List<User> getAll() {
        return this.cache
                .keySet()
                .stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public User get(String name) {

        if (this.cache.containsKey(name))
            return this.cache.get(name);

        User user = this.userRepository.findOne(name);

        if (user != null)
            this.cache.put(user.getName(), user);

        return user;
    }

    @Override
    public void put(User user) {
        this.cache.put(user.getName(), user);
        this.userRepository.insert(user);
    }

    @Override
    public void remove(User user) {
        this.cache.remove(user.getName());
    }

    @Override
    public void update(User user) {
        this.userRepository.update(user);
    }

    @Override
    public List<User> getTop() {
        return this.userRepository.findTop();
    }

}
