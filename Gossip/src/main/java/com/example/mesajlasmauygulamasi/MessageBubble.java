package com.example.mesajlasmauygulamasi;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageBubble extends HBox {

    public MessageBubble(String message, boolean isFromMe, String timestamp, boolean isRead, String senderName) {
        Label messageLabel = new Label(message);
        messageLabel.setFont(javafx.scene.text.Font.font("Segoe UI", 14));
        
        // Varsayılan ayarlar
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setMinWidth(50); 

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(3.0);
        dropShadow.setOffsetX(1.0);
        dropShadow.setOffsetY(1.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));

        // Sistem mesajı kontrolü (Gruba ekleme ve çıkarma)
        if (message.contains("gruba ekledi") || message.contains("gruptan çıkardı")) {
            messageLabel.setStyle(
                "-fx-background-color: #e1f5fe;" +
                "-fx-text-fill: #0277bd;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 5 10 5 10;"
            );
            this.setAlignment(Pos.CENTER); // Ortala
            this.getChildren().add(messageLabel);
            return; // Diğer işlemleri yapma
        }

        // Mesaj silinmişse stilini değiştir
        if (message.equals("🚫 Bu mesaj silindi") || message.equals("🗑️ Bu mesaj sizin tarafınızdan silindi")) {
            // --- DÜZELTME: Titremeyi önlemek için wrapText KAPATILIYOR ---
            messageLabel.setWrapText(false); // Tek satır olsun, hesaplama karışmasın
            
            // Sabit genişlik ayarları
            double fixedWidth = 240.0;
            messageLabel.setMinWidth(fixedWidth);
            messageLabel.setPrefWidth(fixedWidth);
            messageLabel.setMaxWidth(fixedWidth);
            
            messageLabel.setStyle(
                "-fx-background-color: #e0e0e0;" +
                "-fx-text-fill: #888888;" +
                "-fx-font-style: italic;" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 10 14 10 14;"
            );
        } else if (isFromMe) {
            messageLabel.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #a886d3, #8e44ad);" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 18 18 2 18;" +
                            "-fx-padding: 10 14 10 14;"
            );
            messageLabel.setEffect(dropShadow);
        } else {
            messageLabel.setStyle(
                    "-fx-background-color: #ffffff;" +
                            "-fx-text-fill: #333333;" +
                            "-fx-background-radius: 18 18 18 2;" +
                            "-fx-padding: 10 14 10 14;" +
                            "-fx-border-color: #f0f0f0;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 18 18 18 2;"
            );
            messageLabel.setEffect(dropShadow);
        }

        String timeText = formatTime(timestamp);
        Label timeLabel = new Label(timeText);
        timeLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 10px; -fx-padding: 2 0 0 0;");

        Label statusLabel = new Label();
        if (isFromMe) {
            String statusText = isRead ? "✓✓" : "✓";
            statusLabel.setText(statusText);
            if (isRead) {
                statusLabel.setStyle("-fx-text-fill: #8e44ad; -fx-font-size: 11px; -fx-font-weight: bold;");
            } else {
                statusLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 11px;");
            }
        }

        this.setAlignment(isFromMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        this.setPadding(new Insets(4, 8, 4, 8));

        HBox bottomInfo = new HBox(4);
        bottomInfo.setAlignment(Pos.CENTER_RIGHT);

        if (isFromMe) {
            bottomInfo.getChildren().addAll(timeLabel, statusLabel);
        } else {
            bottomInfo.getChildren().add(timeLabel);
        }

        VBox container = new VBox(2);
        container.setAlignment(isFromMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        // Silinen mesajlarda container genişliğini de sabitle
        if (message.equals("🚫 Bu mesaj silindi") || message.equals("🗑️ Bu mesaj sizin tarafınızdan silindi")) {
             container.setMinWidth(240);
        }

        // Eğer mesaj benden değilse ve gönderen ismi varsa (grup sohbeti için)
        if (!isFromMe && senderName != null && !senderName.isEmpty()) {
            Label senderLabel = new Label(senderName);
            senderLabel.setStyle("-fx-text-fill: #a886d3; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 0 0 0 5;");
            container.getChildren().add(senderLabel);
        }
        
        container.getChildren().addAll(messageLabel, bottomInfo);

        this.getChildren().add(container);
    }
    
    // Eski constructor'ı koruyalım (geriye dönük uyumluluk için)
    public MessageBubble(String message, boolean isFromMe, String timestamp, boolean isRead) {
        this(message, isFromMe, timestamp, isRead, null);
    }

    private String formatTime(String timestamp) {
        try {
            if (timestamp == null || timestamp.isEmpty()) {
                return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            LocalDateTime dateTime = LocalDateTime.parse(timestamp,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return "??:??";
        }
    }
}