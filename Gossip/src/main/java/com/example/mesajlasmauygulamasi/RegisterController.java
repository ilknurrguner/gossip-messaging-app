package com.example.mesajlasmauygulamasi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML
    private Button kayitBtn;

    @FXML
    private TextField kullaniciID;

    @FXML
    private PasswordField kullaniciSifre;

    @FXML
    private PasswordField kullaniciSifre1;

    @FXML
    private Text registerUyari;

    @FXML
    private Hyperlink vazgecBtn;

    @FXML
    public void initialize() {
        setupEnterKeyActions();
    }

    private void setupEnterKeyActions() {
        kullaniciID.setOnAction(event -> {
            if (!kullaniciID.getText().isEmpty()) {
                kullaniciSifre.requestFocus();
            }
        });

        kullaniciSifre.setOnAction(event -> {
            if (!kullaniciSifre.getText().isEmpty()) {
                kullaniciSifre1.requestFocus();
            }
        });

        kullaniciSifre1.setOnAction(event -> {
            if (!kullaniciSifre1.getText().isEmpty()) {
                kayitOl();
            }
        });
        kayitBtn.setDefaultButton(true);
    }

    public void kayitOl() {
        String userName = kullaniciID.getText().trim();
        String password = kullaniciSifre.getText();
        String password1 = kullaniciSifre1.getText();

        if (userName.isEmpty()) {
            showError("Kullanıcı adı boş olamaz!");
            kullaniciID.requestFocus();
            return;
        }

        boolean success = UserManager.addUser(userName, password);

        if (!success) {
            showError("Kullanıcı adı kullanılıyor.");
            kullaniciID.requestFocus();
            kullaniciID.selectAll();
            return;
        }

        if (password.isEmpty()) {
            showError("Şifre boş olamaz!");
            kullaniciSifre.requestFocus();
            return;
        }

        if (!password.equals(password1)) {
            showError("Şifreler uyuşmuyor!");
            kullaniciSifre.clear();
            kullaniciSifre1.clear();
            kullaniciSifre.requestFocus();
            return;
        }

        showSuccess("Kayıt başarılı! Giriş ekranına yönlendiriliyorsunuz.");

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(() -> {
                    try {
                        goToLoginPage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showError(String message) {
        registerUyari.setText(message);
        registerUyari.setStyle("-fx-fill: red;");
    }

    private void showSuccess(String message) {
        registerUyari.setText(message);
        registerUyari.setStyle("-fx-fill: green;");

        kullaniciID.clear();
        kullaniciSifre.clear();
        kullaniciSifre1.clear();
    }

    @FXML
    private void goToLoginPage() throws IOException {
        Stage mevcutStage = (Stage) kayitBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("giris_ekrani.fxml"));
        mevcutStage.setScene(new Scene(fxmlLoader.load()));
        mevcutStage.setTitle("Giriş Yap");
    }
}