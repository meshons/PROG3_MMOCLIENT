package game;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private String serveraddr;
    private int port;
    private LoginScreen ls;

    @Override
    public void init(){
         serveraddr = "217.112.128.80";
         port = 60000;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.initStyle(StageStyle.UNDECORATED);

            primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon.png")));
            primaryStage.setTitle("super duper minimal MMO homework");
            primaryStage.setResizable(false);
            primaryStage.setHeight(900);
            primaryStage.setWidth(1600);

            Font.loadFont(Main.class.getResource("/sho.ttf").toExternalForm(), 10);
            Font.loadFont(Main.class.getResource("/lt.ttf").toExternalForm(), 10);

            Group root = new Group();
            Canvas canvas = new Canvas(1600, 900);
            root.resize(1600,900);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(new Image(this.getClass().getResourceAsStream("/back.jpg")),0,0,1600,900);
            root.getChildren().add(canvas);

            FXMLLoader loader = new FXMLLoader();
            AnchorPane NotAnAnchorPane =loader.load(this.getClass().getResourceAsStream("/login_screen.fxml"));
            ls = loader.getController();
            root.getChildren().add(NotAnAnchorPane);

            Scene sc = new Scene(root,1600,900);
            primaryStage.setScene(sc);
            primaryStage.show();
            ls.setServerInfo(serveraddr,port,NotAnAnchorPane,canvas,primaryStage);
            ls.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    @Override
    public void stop() throws  InterruptedException {
        ls.close();
        ls.join();
    }

}
