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

public class MessageManager {
    private static final String MESSAGES_FILE = "messages.json";
    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static String currentUser;

    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    //mesajları dosyadan yükle
    public static List<Message> loadMessages() {
        try {
            File file = new File(MESSAGES_FILE);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            String json = new String(Files.readAllBytes(Paths.get(MESSAGES_FILE)));
            Type messageListType = new TypeToken<List<Message>>() {}.getType();
            List<Message> messages = gson.fromJson(json, messageListType);
            return messages != null ? messages : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //mesajları dosyaya kaydet
    public static void saveMessages(List<Message> messages) {
        try (FileWriter writer = new FileWriter(MESSAGES_FILE)) {
            gson.toJson(messages, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //yeni mesaj ekle
    public static void addMessage(String sender, String receiver, String content) {
        List<Message> messages = loadMessages();
        Message newMessage = new Message(sender, receiver, content);
        messages.add(newMessage);
        saveMessages(messages);
    }

    //iki kullanıcı arasındaki mesajları getir (Grup desteği eklendi)
    public static List<Message> getMessagesBetween(String user1, String user2) {
        List<Message> allMessages = loadMessages();
        
        // user2 bir grup mu kontrol et
        List<String> groupMembers = GroupManager.getGroupMembers(user2);
        boolean isGroup = groupMembers != null && !groupMembers.isEmpty();

        return allMessages.stream()
                .filter(msg -> {
                    if (isGroup) {
                        // Grup mesajı ise: Alıcı grup adı olmalı
                        return msg.getReceiver().equals(user2);
                    } else {
                        // Bireysel mesajlaşma
                        boolean isSenderUser1 = msg.getSender().equals(user1);
                        if (isSenderUser1 && msg.getReceiver().equals(user2)) {
                            return !msg.isDeletedForSender();
                        }
                        else if (msg.getSender().equals(user2) && msg.getReceiver().equals(user1)) {
                            return !msg.isDeletedForReceiver();
                        }
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    //tek bir mesajı sil (ID olmadığı için timestamp ve içerik ile buluyoruz)
    public static void deleteSingleMessage(String sender, String receiver, String timestamp, String content, boolean deleteForEveryone) {
        List<Message> allMessages = loadMessages();
        boolean changed = false;

        for (Message msg : allMessages) {
            if (msg.getSender().equals(sender) && msg.getReceiver().equals(receiver) &&
                msg.getTimestamp().equals(timestamp) && msg.getContent().equals(content)) {
                
                if (deleteForEveryone) {
                    // Herkesten sil: Mesaj içeriğini değiştir ve bayrağı set et
                    msg.setDeletedForEveryone(true);
                    msg.setContent("🚫 Bu mesaj silindi");
                } else {
                    // Sadece benden sil: Gönderen için silindi işaretle
                    msg.setContent("🗑️ Bu mesaj sizin tarafınızdan silindi");
                }
                changed = true;
                break;
            }
        }

        if (changed) {
            saveMessages(allMessages);
        }
    }

    //tek bir kişiyle olan sohbeti sil
    public static void clearChat(String me, String partner, boolean deleteForEveryone) {
        List<Message> allMessages = loadMessages();
        boolean changed = false;
        for (Message msg : allMessages) {
            // Eğer mesaj benim gönderdiğim ve partnerin aldığı bir mesajsa
            if (msg.getSender().equals(me) && msg.getReceiver().equals(partner)) {
                msg.setDeletedForSender(true);
                if (deleteForEveryone) {
                    msg.setDeletedForReceiver(true);
                }
                changed = true;
            }
            // Eğer mesaj partnerin gönderdiği ve benim aldığım bir mesajsa
            else if (msg.getSender().equals(partner) && msg.getReceiver().equals(me)) {
                msg.setDeletedForReceiver(true);
                if (deleteForEveryone) {
                    msg.setDeletedForSender(true);
                }
                changed = true;
            }
        }
        if (changed) {
            saveMessages(allMessages);
        }
    }

    //tüm sohbetleri sil
    public static void clearAllChatsForUser(String username) {
        List<Message> allMessages = loadMessages();
        boolean changed = false;

        for (Message msg : allMessages) {
            //eğer gönderen bensem benden silindi yap
            if (msg.getSender().equals(username)) {
                if (!msg.isDeletedForSender()) {
                    msg.setDeletedForSender(true);
                    changed = true;
                }
            }
            //eğer alıcı bensem benden silindi yap
            if (msg.getReceiver().equals(username)) {
                if (!msg.isDeletedForReceiver()) {
                    msg.setDeletedForReceiver(true);
                    changed = true;
                }
            }
        }

        if (changed) {
            saveMessages(allMessages);
        }
    }

    //sohbet edilen kişileri getir (Grupları da ekle)
    public static List<String> getChatPartners(String username) {
        List<Message> allMessages = loadMessages();
        List<String> partners = new ArrayList<>();
        
        // Bireysel sohbetler
        for (Message msg : allMessages) {
            if (msg.getSender().equals(username) && !msg.isDeletedForSender()) {
                // Eğer alıcı bir grup değilse ekle
                if (GroupManager.getGroupMembers(msg.getReceiver()).isEmpty()) {
                    if (!partners.contains(msg.getReceiver())) partners.add(msg.getReceiver());
                }
            }
            else if (msg.getReceiver().equals(username) && !msg.isDeletedForReceiver()) {
                // Eğer gönderen bir grup değilse ekle
                if (GroupManager.getGroupMembers(msg.getSender()).isEmpty()) {
                    if (!partners.contains(msg.getSender())) partners.add(msg.getSender());
                }
            }
        }
        
        // Grupları ekle
        List<String> userGroups = GroupManager.getUserGroups(username);
        for (String group : userGroups) {
            if (!partners.contains(group)) {
                partners.add(group);
            }
        }

        return partners;
    }

    //okundu işaretle (Grup desteği eklendi)
    public static void markMessagesAsRead(String chatPartner, String currentUser) {
        List<Message> allMessages = loadMessages();
        boolean changed = false;
        
        // chatPartner bir grup mu?
        List<String> groupMembers = GroupManager.getGroupMembers(chatPartner);
        boolean isGroup = groupMembers != null && !groupMembers.isEmpty();

        for (Message msg : allMessages) {
            if (isGroup) {
                // Grup mesajı: Alıcı grup adı ise ve gönderen ben değilsem okundu yap
                if (msg.getReceiver().equals(chatPartner) && !msg.getSender().equals(currentUser) && !msg.isRead()) {
                    msg.setRead(true);
                    changed = true;
                }
            } else {
                // Bireysel mesaj: Gönderen partner ise ve alıcı bensem okundu yap
                if (msg.getSender().equals(chatPartner) && msg.getReceiver().equals(currentUser) && !msg.isRead()) {
                    msg.setRead(true);
                    changed = true;
                }
            }
        }
        if (changed) saveMessages(allMessages);
    }
}