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
            primaryStage.getIcons().add(new Image("file:icon.png"));
            primaryStage.setTitle("super duper minimal MMO homework");
            primaryStage.setResizable(false);

            Group root = new Group();
            Canvas canvas = new Canvas(1600, 900);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            drawShapes(gc);
            root.getChildren().add(canvas);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File("src/game/login_screen.fxml").toURI().toURL());
            AnchorPane NotAnAnchorPane = loader.<AnchorPane>load();
            ls = loader.getController();
            root.getChildren().add(NotAnAnchorPane);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            ls.setServerInfo(serveraddr,port,NotAnAnchorPane,canvas);
            //System.out.println("teszt");
            //tcp.Registration("testuser","tesadaszt");
            //user = tcp.Login("testuser", "tesadaszt");
            //tcp.start();
            //System.out.println(user.getName());
            //System.out.println(user.getSecret());
            //udp.send(Command.create().add('S').add(user.getId()).add(user.getSecret()).getMessage());
            //while(players.isEmpty())
            //    Thread.sleep(15);
            //udp.start();

            //infinity loop here please

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        ls.close();
    }

    private void drawShapes(GraphicsContext gc) throws IOException {
        gc.drawImage(new Image(new FileInputStream("back.jpg")),0,0,1600,900);
    }

}
