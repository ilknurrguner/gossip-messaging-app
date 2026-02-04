package com.example.mesajlasmauygulamasi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private boolean deletedForSender = false;
    private boolean deletedForReceiver = false;
    private boolean deletedForEveryone = false; // Yeni alan

    public boolean isDeletedForSender() {
        return deletedForSender;
    }

    public void setDeletedForSender(boolean deletedForSender) {
        this.deletedForSender = deletedForSender;
    }

    public boolean isDeletedForReceiver() {
        return deletedForReceiver;
    }

    public void setDeletedForReceiver(boolean deletedForReceiver) {
        this.deletedForReceiver = deletedForReceiver;
    }

    public boolean isDeletedForEveryone() {
        return deletedForEveryone;
    }

    public void setDeletedForEveryone(boolean deletedForEveryone) {
        this.deletedForEveryone = deletedForEveryone;
    }

    private String sender;
    private String receiver;
    private String content;
    private String timestamp;
    private boolean read;

    public Message() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.read = false;
    }

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.read = false;
        this.deletedForSender = false;
        this.deletedForReceiver = false;
        this.deletedForEveryone = false;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    //kolay timestamp formatı için yardımcı metod
    public String getFormattedTime() {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestamp,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return "??:??";
        }
    }
}