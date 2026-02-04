package com.example.mesajlasmauygulamasi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String USERS_FILE = "users.json";

    public static List<User> loadUsers() { //dosyadan kullanıcı bilgisini okuyor
        File usersFile = new File(USERS_FILE);
        try {
            if (usersFile.exists()) {
                try (FileReader reader = new FileReader(usersFile)) {
                    Type userListType = new TypeToken<List<User>>() {}.getType();
                    List<User> users = gson.fromJson(reader, userListType);
                    return users != null ? users : new ArrayList<>();
                }
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean saveUsers(List<User> users) { //kullanıcıları dosyaya yazar
        try {
            File usersFile = new File(USERS_FILE);
            try (FileWriter writer = new FileWriter(usersFile)) {
                gson.toJson(users, writer);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addUser(String username, String password) { //yeni kullanıcıyı listeye ekliyor
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }

        User newUser = new User(username, password);
        users.add(newUser);

        return saveUsers(users);
    }

    public static boolean authenticateUser(String username, String password) { //girilen kullanıcı bilgilerini kontrol ediyor
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getAllUsernames() {
        List<User> users = loadUsers();
        List<String> usernames = new ArrayList<>();

        for (User user : users) {
            usernames.add(user.getUsername());
        }

        return usernames;
    }

    public static User getUser(String username) {
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public static boolean updateUsername(String oldUsername, String newUsername) { //kullanıcı adı güncelle
        List<User> users = loadUsers();
        boolean userFound = false;

        for (User user : users) {
            if (user.getUsername().equals(oldUsername)) {
                user.setUsername(newUsername);
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            return false;
        }

        updateUsernameInMessages(oldUsername, newUsername); //mesajlardaki kullanıcı adını günceller

        return saveUsers(users);
    }

    private static void updateUsernameInMessages(String oldUsername, String newUsername) {
        List<Message> messages = MessageManager.loadMessages();
        boolean changed = false;

        for (Message message : messages) {
            if (message.getSender().equals(oldUsername)) {
                message.setSender(newUsername);
                changed = true;
            }
            if (message.getReceiver().equals(oldUsername)) {
                message.setReceiver(newUsername);
                changed = true;
            }
        }
        if (changed) {
            MessageManager.saveMessages(messages);
        }
    }
}