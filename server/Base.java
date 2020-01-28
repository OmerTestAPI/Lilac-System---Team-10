package server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;


import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Base  implements Initializable {
    @FXML
    Button login;
    @FXML
    TabPane pane;
    @FXML
    Tab home;
    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
    public void login(ActionEvent event) throws IOException {
        Stage primaryStage =Main.getStage();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        // primaryStage.setTitle("Hello World");
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        primaryStage.setScene(new Scene(root, width, height));
    }

}
