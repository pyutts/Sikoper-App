/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author pyuts
 */

 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Parent;
 import javafx.scene.Scene;
 import javafx.scene.image.Image;
 import javafx.stage.Stage;
 
 public class SikoperApp extends Application {
     @Override
     public void start(Stage primaryStage) throws Exception {
         Parent root = FXMLLoader.load(getClass().getResource("/Views/Login.fxml"));
         primaryStage.setTitle("Sikoper - Login");
         primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/icon.png"))); 
         primaryStage.setScene(new Scene(root));
         primaryStage.show();
     }
 
     public static void main(String[] args) {
         launch(args);
     }
 }
