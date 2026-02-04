package com.example.mesajlasmauygulamasi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupManager {
    private static final String GROUPS_FILE = "groups.json";
    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Grupları dosyadan yükle
    public static List<Group> loadGroups() {
        try {
            File file = new File(GROUPS_FILE);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            String json = new String(Files.readAllBytes(Paths.get(GROUPS_FILE)));
            Type groupListType = new TypeToken<List<Group>>() {}.getType();
            List<Group> groups = gson.fromJson(json, groupListType);
            return groups != null ? groups : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Grupları dosyaya kaydet
    public static void saveGroups(List<Group> groups) {
        try (FileWriter writer = new FileWriter(GROUPS_FILE)) {
            gson.toJson(groups, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Yeni grup oluştur
    public static boolean createGroup(String groupName, String creator, List<String> members) {
        List<Group> groups = loadGroups();
        
        // Aynı isimde grup var mı kontrol et
        for (Group g : groups) {
            if (g.getGroupName().equalsIgnoreCase(groupName)) {
                return false; // Grup adı zaten var
            }
        }

        // Oluşturan kişiyi de üyelere ekle (eğer listede yoksa)
        if (!members.contains(creator)) {
            members.add(creator);
        }

        Group newGroup = new Group(groupName, creator, members);
        groups.add(newGroup);
        saveGroups(groups);
        return true;
    }

    // Gruba üye ekle
    public static boolean addMember(String groupName, String newMember) {
        List<Group> groups = loadGroups();
        for (Group g : groups) {
            if (g.getGroupName().equals(groupName)) {
                if (!g.getMembers().contains(newMember)) {
                    g.getMembers().add(newMember);
                    saveGroups(groups);
                    return true;
                }
                return false; // Zaten üye
            }
        }
        return false; // Grup bulunamadı
    }

    // Gruptan üye çıkar
    public static boolean removeMember(String groupName, String memberToRemove) {
        List<Group> groups = loadGroups();
        for (Group g : groups) {
            if (g.getGroupName().equals(groupName)) {
                if (g.getMembers().contains(memberToRemove)) {
                    // Kurucuyu çıkarmaya izin verme (isteğe bağlı, ama mantıklı olan bu)
                    if (g.getCreator().equals(memberToRemove)) {
                        return false; 
                    }
                    g.getMembers().remove(memberToRemove);
                    saveGroups(groups);
                    return true;
                }
                return false; // Üye değil
            }
        }
        return false; // Grup bulunamadı
    }

    // Kullanıcının üye olduğu grupları getir
    public static List<String> getUserGroups(String username) {
        List<Group> allGroups = loadGroups();
        return allGroups.stream()
                .filter(g -> g.getMembers().contains(username))
                .map(Group::getGroupName)
                .collect(Collectors.toList());
    }

    // Grup üyelerini getir
    public static List<String> getGroupMembers(String groupName) {
        List<Group> allGroups = loadGroups();
        for (Group g : allGroups) {
            if (g.getGroupName().equals(groupName)) {
                return g.getMembers();
            }
        }
        return new ArrayList<>();
    }

    // Grup nesnesini getir
    public static Group getGroup(String groupName) {
        List<Group> allGroups = loadGroups();
        for (Group g : allGroups) {
            if (g.getGroupName().equals(groupName)) {
                return g;
            }
        }
        return null;
    }
}