package com.example.mesajlasmauygulamasi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AyarlarController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button ayarlardan_geri;

    private String currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = Controller.getLoggedInUser();

        //kullanıcı verilerini al
        User userObj = UserManager.getUser(currentUser);

        //alanları doldur
        usernameField.setText(currentUser);
        if (userObj != null && userObj.getEmail() != null) {
            emailField.setText(userObj.getEmail());
        }
    }

    @FXML
    public void bilgileriGuncelle(ActionEvent event) {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPassword = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (newUsername.isEmpty()) {
            showAlert("Hata", "Kullanıcı adı boş olamaz!");
            return;
        }

        // E-posta format kontrolü
        if (!newEmail.isEmpty()) {
            // Basit bir e-posta regex kontrolü: @ ve . içermeli
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            if (!newEmail.matches(emailRegex)) {
                showAlert("Hata", "Lütfen geçerli bir e-posta adresi giriniz! (Örn: ornek@gmail.com)");
                return;
            }
        }

        if (!newPassword.isEmpty() && !newPassword.equals(confirmPass)) {
            showAlert("Hata", "Şifreler uyuşmuyor!");
            return;
        }

        //kullanıcı adı değiştiyse kontrol
        if (!newUsername.equals(currentUser)) {
            List<User> users = UserManager.loadUsers();
            for (User u : users) {
                if (u.getUsername().equals(newUsername)) {
                    showAlert("Hata", "Bu kullanıcı adı zaten alınmış!");
                    return;
                }
            }

            //kullanıcı adını güncelle
            boolean usernameUpdated = UserManager.updateUsername(currentUser, newUsername);
            if (!usernameUpdated) {
                showAlert("Hata", "Kullanıcı adı güncellenirken bir hata oluştu!");
                return;
            }

            //şifreyi güncelle (eğer değiştiyse)
            if (!newPassword.isEmpty()) {
                updateUserPassword(newUsername, newPassword);
            }
            //email'i güncelle
            updateUserEmail(newUsername, newEmail);

        } else {
            //sadece şifre veya email güncellenecek
            if (!newPassword.isEmpty()) {
                updateUserPassword(currentUser, newPassword);
            }
            updateUserEmail(currentUser, newEmail);
        }

        //güncellemeden sonra tekrar giriş yaptırıyor
        showAlert("Başarılı", "Bilgiler güncellendi! Lütfen tekrar giriş yapın.");

        Controller.clearLoggedInUser();
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("giris_ekrani.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Giriş Ekranı");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean updateUserPassword(String username, String newPassword) {
        List<User> users = UserManager.loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setPassword(newPassword);
                return UserManager.saveUsers(users);
            }
        }
        return false;
    }

    private boolean updateUserEmail(String username, String newEmail) {
        List<User> users = UserManager.loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setEmail(newEmail);
                return UserManager.saveUsers(users);
            }
        }
        return false;
    }

    @FXML
    void ayarlardan_geri_gel(ActionEvent event) throws IOException {
        Stage stage = (Stage) ayarlardan_geri.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mesaj_sayfasi.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // DialogPane'e CSS sınıfını ekle
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("ayarlar.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        alert.showAndWait();
    }

    @FXML
    void tumSohbetleriTemizle(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Tüm Sohbetleri Temizle");
        alert.setHeaderText("DİKKAT: Tüm mesaj geçmişin silinecek!");
        alert.setContentText("Bu işlem geri alınamaz. Devam etmek istiyor musun?");
        
        // DialogPane'e CSS sınıfını ekle
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("ayarlar.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                MessageManager.clearAllChatsForUser(currentUser);
                showAlert("Başarılı", "Tüm sohbet geçmişin temizlendi.");
            }
        });
    }
}