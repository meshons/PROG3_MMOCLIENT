package game;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

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
            primaryStage.setMinHeight(720);
            primaryStage.setMinWidth(1280);
            primaryStage.setMaximized(true);
            primaryStage.show();

            Font.loadFont(Main.class.getResource("/sho.ttf").toExternalForm(), 40);
            Font.loadFont(Main.class.getResource("/lt.ttf").toExternalForm(), 40);
            Font.loadFont(Main.class.getResource("/vmb.ttf").toExternalForm(), 40);

            Group root = new Group();

            Canvas canvas = new Canvas(primaryStage.getWidth(), primaryStage.getHeight());
            GraphicsContext gc = canvas.getGraphicsContext2D();


            gc.drawImage(new Image(this.getClass().getResourceAsStream("/back.jpg")),0,0,primaryStage.getWidth(),primaryStage.getHeight());
            root.getChildren().add(canvas);

            FXMLLoader loader = new FXMLLoader();
            AnchorPane NotAnAnchorPane =loader.load(this.getClass().getResourceAsStream("/login_screen.fxml"));
            NotAnAnchorPane.setPrefSize(primaryStage.getWidth(),primaryStage.getHeight());
            ls = loader.getController();
            root.getChildren().add(NotAnAnchorPane);

            Scene sc = new Scene(root,primaryStage.getWidth(),primaryStage.getHeight());
            primaryStage.setScene(sc);
            ls.setServerInfo(serveraddr,port,NotAnAnchorPane,canvas,primaryStage);
            ls.setDaemon(true);
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
