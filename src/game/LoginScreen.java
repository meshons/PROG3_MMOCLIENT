package game;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public class LoginScreen {
    private String serveraddr;
    private int port;

    private boolean run;

    private User user;
    private Tcp tcp;
    private Udp udp;
    private Map<Short, Player> players = new ConcurrentHashMap<>();
    private Map<Integer, Monster> monsters = new ConcurrentHashMap<>();
    private BlockingQueue<Hit> hitQueue = new LinkedBlockingQueue<>();
    private AnchorPane parent;
    private Canvas canvas;

    @FXML
    private TextField login_id;
    @FXML
    private TextField reg_id;
    @FXML
    private PasswordField login_pw;
    @FXML
    private PasswordField reg_pw;
    @FXML
    private PasswordField reg_pw_re;
    @FXML
    private VBox message_box;
    @FXML
    private Button message;
    @FXML
    private Button login_button;
    @FXML
    private Button reg_button;
    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

    @FXML
    private void initialize() {
    }

    public void setServerInfo(String addr, int port_, AnchorPane parent_, Canvas canvas_) throws IOException {
        serveraddr=addr;
        port=port_;
        setCon();
        parent=parent_;
        canvas=canvas_;
    }

    public LoginScreen() {
        run=true;
    }


    public void close() {
        //todo
        run=false;
    }

    public void login() throws IOException, NoSuchAlgorithmException, InterruptedException {
        //make login
        user = tcp.Login(login_id.getText(), login_pw.getText());

        //start game
        if (user == null) {
            message_box.setVisible(true);
            message.setText("Wrong authentication! Make it better please.");
            message.setOnAction((e) -> {
                message_box.setVisible(false);
            });
            return;
        }
        parent.setVisible(false);
        tcp.start();
        udp.start();
        Game g = new Game(canvas,run,players,monsters,hitQueue);
        g.start();

        g.join();
        udp.stopit();
        udp.join();
        tcp.stopit();
        tcp.join();
        tcp.Logout(user);
        players.clear();
        monsters.clear();
        hitQueue.clear();

        parent.setVisible(true);
        setCon();
    }

    private void setCon() throws IOException {
        try {
            tcp = new Tcp(serveraddr, port, players, monsters);
            udp = new Udp(serveraddr, port, tcp, hitQueue, players, monsters);
        } catch (ConnectException e) {
            message_box.setVisible(true);
            message.setText("No server connection! Try again later please :D");
        }
    }

    public void register() throws IOException, NoSuchAlgorithmException {
        if (reg_pw.getText().equals(reg_pw_re.getText()))
            if (tcp.Registration(reg_id.getText(), reg_pw.getText())) {
                message_box.setVisible(true);
                message.setText("Registration was successful sir "+reg_id.getText()+ "!");
                message.setOnAction((e) -> {
                    message_box.setVisible(false);
                });
            } else {
                message_box.setVisible(true);
                message.setText("It seems to be a problem with your data sir!");
                message.setOnAction((e) -> {
                    message_box.setVisible(false);
                });
            }
        else {
            message_box.setVisible(true);
            message.setText("The given passwords does not match.");
            message.setOnAction((e) -> {
                message_box.setVisible(false);
            });
        }
    }

}
