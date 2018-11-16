package game;

import java.io.*;
import java.util.Arrays;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.codec.binary.Hex;

public class Tcp extends Thread {
    private Socket socket;
    private BufferedReader incoming;
    private DataOutputStream outgoing;
    private boolean run;
    private String username_s, password_s;
    private Map<Short, Player> players;
    private Map<Integer, Monster> monsters;

    public Tcp(String url, int port, Map<Short, Player> p, Map<Integer, Monster> m) throws IOException {
        socket = new Socket(url, port);
        socket.setKeepAlive(true);
        incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        outgoing = new DataOutputStream(socket.getOutputStream());
        monsters = m;
        players = p;
    }

    public boolean Registration(String username, String password) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String hashedpassword = new String(Hex.encodeHex(digest.digest(password.getBytes(StandardCharsets.UTF_8))));
        username_s = username;
        password_s = hashedpassword;
        String message = Command.create().add('2').add(username).add(hashedpassword).getMessage();
        outgoing.write(message.getBytes());
        String re = incoming.readLine();
        System.out.println(re);
        return false;
    }

    public User Login(String username, String password) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String hashedpassword = new String(Hex.encodeHex(digest.digest(password.getBytes(StandardCharsets.UTF_8))));
        username_s = username;
        password_s = hashedpassword;
        String message = Command.create().add('0').add(username).add(hashedpassword).getMessage();
        outgoing.write(message.getBytes());
        String re = incoming.readLine();
        String[] splitted = String.valueOf(re).split(" ");
        if (splitted[0].equals("nope"))
            return null;
        short id = (short) Integer.parseInt(splitted[3]);
        return new User(splitted[1], id, (short) 0, (short) 0, splitted[4]);
    }

    public void Logout(User u) throws IOException {
        String message = Command.create().add('1').add(u.getId()).getMessage();
        outgoing.write(message.getBytes(StandardCharsets.UTF_8));
    }

    public void stopit() {
        run = false;
    }

    public void run() {
        run = true;
        try {
            while (run) {
                //recieve tcp and interpret it
                String c = incoming.readLine();
                if(c==null)continue;
                if (!c.equals("p pingellekgeco "))
                    System.out.println(c);
                String[] cmd = c.split(" ");
                if (cmd.length > 0) {
                    switch (cmd[0]) {
                        case "p":
                            break;
                        case "0":
                            if (cmd.length == 5) {
                                if (players.containsKey(Short.parseShort(cmd[2])))
                                    players.remove(Short.parseShort(cmd[2]));
                                players.put(Short.parseShort(cmd[2]), new Player(cmd[1], Short.parseShort(cmd[2]), Short.parseShort(cmd[3]), Short.parseShort(cmd[4])));
                            }
                            break;
                        case "2":
                            if (cmd.length == 2) {
                                players.remove(Short.parseShort(cmd[1]));
                            }
                            break;
                        // monster spawn, kill
                        case "M":
                            if (cmd.length == 5) {
                                if (monsters.containsKey(Integer.parseInt(cmd[2])))
                                    monsters.remove(Integer.parseInt(cmd[1]));
                                monsters.put(Integer.parseInt(cmd[1]), new Monster(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), Short.parseShort(cmd[3]), Short.parseShort(cmd[4])));
                            }
                            break;
                        case "K":
                            if(cmd.length==2){
                                monsters.remove(Integer.parseInt(cmd[1]));
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
