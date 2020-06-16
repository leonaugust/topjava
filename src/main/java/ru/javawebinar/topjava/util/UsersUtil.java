package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Arrays;
import java.util.List;

public class UsersUtil {
    public static final List<User> USERS = Arrays.asList(
            new User(null, "Joey", "joey_tribbiani@mail.ru", "root", Role.USER),
            new User(null, "Chandler", "chandler_bing@mail.ru", "root", Role.ADMIN)
    );
}
