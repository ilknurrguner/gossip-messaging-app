package com.example.mesajlasmauygulamasi;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    @FXML
    private Button girisBtn;

    @FXML
    private TextField kullaniciID;

    @FXML
    private PasswordField kullaniciSifre;

    @FXML
    private Text mesaj;

    private static String loggedInUser;

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static void clearLoggedInUser() {
        loggedInUser = null;
        MessageManager.setCurrentUser(null);
    }

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
                girisButon();
            }
        });
        girisBtn.setDefaultButton(true);
    }

    @FXML
    private void mesaj_sayfasiGec() throws IOException {
        Stage mevcutStage = (Stage) girisBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("mesaj_sayfasi.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        mevcutStage.setTitle("Gossip - " + loggedInUser);
        mevcutStage.setScene(scene);
        mevcutStage.show();
    }

    @FXML
    public void girisButon() {
        String userName = kullaniciID.getText();
        String password = kullaniciSifre.getText();
        kullaniciKontrol(userName, password);
    }

    private void kullaniciKontrol(String enteredUsername, String enteredPassword) {
        try {
            //UserManager ile girilen bilgiler kontrol ediliyor
            if (UserManager.authenticateUser(enteredUsername, enteredPassword)) {
                loggedInUser = enteredUsername;
                MessageManager.setCurrentUser(enteredUsername);
                mesaj_sayfasiGec();
            } else {
                mesaj.setText("Kullanıcı adı veya şifre hatalı!");
                kullaniciID.requestFocus();
                kullaniciID.selectAll();
            }
        } catch (Exception e) {
            mesaj.setText("Giriş sırasında hata oluştu!");
            e.printStackTrace();
        }
    }

    @FXML
    public void Kayit_sayfasiGec() throws IOException {
        Stage mevcutStage = (Stage) girisBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("kayit_ekrani.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mevcutStage.setTitle("Kayıt Olma Ekranı");
        mevcutStage.setScene(scene);
        mevcutStage.show();
    }
}