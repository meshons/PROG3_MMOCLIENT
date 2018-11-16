package game;

import javafx.scene.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.FontWeight;

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

import javafx.scene.layout.BorderPane;

import javax.swing.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private User user;
    private Tcp tcp;
    private Udp udp;
    private Map<Short, Player> players = new ConcurrentHashMap<>();
    private Map<Integer, Monster> monsters = new ConcurrentHashMap<>();
    private BlockingQueue<Hit> hitQueue = new LinkedBlockingQueue<>();

    @Override
    public void init() throws IOException{
        String serveraddr = "217.112.128.80";
        int port = 60000;
        tcp = new Tcp(serveraddr, port,players,monsters);
        udp = new Udp(serveraddr,port,tcp, hitQueue);
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
            BorderPane loginscreen = new BorderPane();
            Text scenetitle = new Text("super duper minimal MMO homework");
            //todo fancy font please in jar
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 150));
            scenetitle.setTextAlignment(TextAlignment.CENTER);
            loginscreen.setTop(scenetitle);

            GridPane logingrid = new GridPane();

            Label userName = new Label("User Name:");
            logingrid.add(userName, 0, 1);

            TextField userTextField = new TextField();
            logingrid.add(userTextField, 1, 1);

            Label pw = new Label("Password:");
            logingrid.add(pw, 0, 2);

            PasswordField pwBox = new PasswordField();
            logingrid.add(pwBox, 1, 2);
            loginscreen.setCenter(logingrid);
            root.getChildren().add(loginscreen);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            System.out.println("teszt");
            //tcp.Registration("testuser","tesadaszt");
            user = tcp.Login("testuser", "tesadaszt");
            tcp.start();
            udp.start();
            System.out.println(user.getName());
            System.out.println(user.getSecret());
            udp.send("teszt");
            udp.send("tesztasdasd");
            udp.send("12");
            //infinity loop here please

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        if(user!=null)
            tcp.Logout(user);
        tcp.stopit();
        udp.stopit();
        tcp.join();
        udp.join();
    }

    private void drawShapes(GraphicsContext gc) throws IOException {
        gc.drawImage(new Image(new FileInputStream("back.jpg")),0,0,1600,900);
    }

}
