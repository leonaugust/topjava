package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.UsersUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private Map<Integer, User> usersById;
    private AtomicInteger counter;

    private static final Comparator<User> USER_COMPARATOR = (first, second) -> {
        int nameCompare = first.getName().compareTo(second.getName());
        int emailCompare = first.getEmail().compareTo(second.getEmail());

        if (nameCompare == 0) {
            return emailCompare;
        } else {
            return nameCompare;
        }
    };

    public InMemoryUserRepository() {
        usersById = new ConcurrentHashMap<>();
        counter = new AtomicInteger(0);
        UsersUtil.USERS.forEach(this::save);
    }

    @Override
    public User save(User user) {
        log.info("save {}", user);
        if (user.isNew()) {
            user.setId(counter.incrementAndGet());
            usersById.put(user.getId(), user);
            return user;
        }
        return usersById.computeIfPresent(user.getId(), (id, oldUser) -> user);
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return usersById.remove(id) != null;
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return usersById.get(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        List<User> result = new ArrayList<>(usersById.values());
        result.sort(USER_COMPARATOR);
        return result;
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        List<User> users = new ArrayList<>(usersById.values());
        Optional<User> matchingUser = users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
        return matchingUser.orElse(null);
    }
}

