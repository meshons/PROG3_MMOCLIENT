package game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;


import java.io.IOException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class LoginScreen extends Thread {
    private String serveraddr;
    private int port;

    private boolean run;

    private User user;
    private Tcp tcp;
    private Udp udp;
    private Map<Short, Player> players = Collections.synchronizedMap(new HashMap<>());
    private Map<Integer, Monster> monsters = Collections.synchronizedMap(new HashMap<>());
    private CopyOnWriteArrayList<Hit> hitQueue = new CopyOnWriteArrayList<>();
    private AnchorPane parent;
    private Canvas canvas;
    private Game g;
    private Stage stage;

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
    private void initialize() {
    }

    public void setServerInfo(String addr, int port_, AnchorPane parent_, Canvas canvas_,Stage s) throws IOException {
        serveraddr = addr;
        port = port_;
        setCon();
        parent = parent_;
        canvas = canvas_;
        stage=s;
    }

    public LoginScreen() {
        run = true;
    }


    public void close() {
        run = false;
        if (g != null) g.stopit();
    }

    public void enterPressedLogin(KeyEvent event) throws InterruptedException, NoSuchAlgorithmException, IOException {
        if(event.getCode() == KeyCode.ENTER) {
            login();
        }
    }
    public void enterPressedReg(KeyEvent event) throws InterruptedException, NoSuchAlgorithmException, IOException {
        if(event.getCode() == KeyCode.ENTER) {
            login();
        }
    }

    public void login() throws IOException, NoSuchAlgorithmException, InterruptedException {
        //make login
        if(login_id.getText().contains(" ") || login_id.getText().contains("\n"))
        {
            message_box.setVisible(true);
            message.setText("No space and enter please!");
            message.setOnAction((e) -> {
                message_box.setVisible(false);
            });
            login_pw.setText("");
        }else {
            user = tcp.Login(login_id.getText(), login_pw.getText());

            //start game
            if (user == null) {
                message_box.setVisible(true);
                message.setText("Wrong authentication! Make it better please.");
                message.setOnAction((e) -> {
                    message_box.setVisible(false);
                });
                login_pw.setText("");
                return;
            }
            parent.setVisible(false);
        }
    }

    private void setCon() throws IOException {
        try {
            tcp = new Tcp(serveraddr, port, players, monsters);
            udp = new Udp(serveraddr, port, tcp, hitQueue, players, monsters);
        } catch (ConnectException e) {
            message_box.setVisible(true);
            message.setText("No server connection! Try again later please :D");
            message.setOnAction((ef) -> {
                Platform.exit();
            });
        }
    }

    public void register() throws IOException, NoSuchAlgorithmException {
        if (reg_pw.getText().equals(reg_pw_re.getText()))
            if(reg_id.getText().contains(" ") || reg_id.getText().contains("\n"))
            {
                message_box.setVisible(true);
                message.setText("No space and enter please!");
                message.setOnAction((e) -> {
                    message_box.setVisible(false);
                });
                reg_pw.setText("");
                reg_pw_re.setText("");
            }
            else
            if (tcp.Registration(reg_id.getText(), reg_pw.getText())) {
                message_box.setVisible(true);
                reg_pw.setText("");
                reg_pw_re.setText("");
                message.setText("Registration was successful sir " + reg_id.getText() + "!");
                message.setOnAction((e) -> {
                    message_box.setVisible(false);
                });
            } else {
                message_box.setVisible(true);
                reg_pw.setText("");
                reg_pw_re.setText("");
                message.setText("It seems to be a problem with your data sir!");
                message.setOnAction((e) -> {
                    message_box.setVisible(false);
                });
            }
        else {
            message_box.setVisible(true);
            reg_pw.setText("");
            reg_pw_re.setText("");
            message.setText("The given passwords does not match.");
            message.setOnAction((e) -> {
                message_box.setVisible(false);
            });
        }
    }

    public void run() {
        while (run) {
            try {
                while (user == null && run)
                    Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!run)
                break;

            tcp.start();
            udp.start();
            while (players.get(user.getId()) == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            g = new Game(canvas,stage,udp,user, run, players, monsters, hitQueue, players.get(user.getId()));
           //g.setDaemon(true);
            g.start();

            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
