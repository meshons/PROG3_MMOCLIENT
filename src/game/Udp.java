package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Udp extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private boolean run;
    private Map<Short, Player> players;
    private Map<Integer, Monster> monsters;
    private Tcp tcp;
    private BlockingQueue<Hit> hitQueue;

    public Udp(String url, int port_, Tcp tcp_, BlockingQueue<Hit> hitQueue_,Map<Short, Player> p, Map<Integer, Monster> m) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(0);
        port = port_;
        tcp = tcp_;
        hitQueue = hitQueue_;
        address = InetAddress.getByName(url);
        players=p;
        monsters=m;
        socket.setSoTimeout(1000);
    }

    public void send(String message) throws IOException {
        byte[] buf = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }

    public void stopit() {
        run = false;
    }

    public void run() {
        byte[] buf = new byte[256];
        run = true;
        while (run) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            }catch (SocketTimeoutException e) {
                continue;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            //System.out.print(received);
            //interpret please
            String[] cmd = received.split(" ");
            switch (cmd[0]) {
                case "X":
                    if( players.get(Short.parseShort(cmd[1]))!=null)
                    players.get(Short.parseShort(cmd[1])).update(cmd);
                    else {
                        try {
                            tcp.send(Command.create().add('P').add(cmd[1]).getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "Y":
                    if( monsters.get(Integer.parseInt(cmd[1]))!=null)
                    monsters.get(Integer.parseInt(cmd[1])).update(cmd);
                    else {
                        try {
                            tcp.send(Command.create().add('M').add(cmd[1]).getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "H":
                    //todo hit
                    try {
                        hitQueue.put(new Hit(Short.parseShort(cmd[1]),
                                Short.parseShort(cmd[2]), Integer.parseInt(cmd[3])));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }
}
