package com.plazza.ridehailing.repository;

import com.plazza.ridehailing.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> store = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId("USR-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000));
        }
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        if (phone == null) return Optional.empty();
        return store.values().stream()
                .filter(u -> phone.equalsIgnoreCase(u.getPhone()))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsById(String id) {
        return id != null && store.containsKey(id);
    }

    @Override
    public void clear() {
        store.clear();
    }
}
