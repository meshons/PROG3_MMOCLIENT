package game;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.input.KeyEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.scene.input.KeyCode;

import javafx.scene.paint.*;
import javafx.stage.Stage;


public class Game extends Thread {

    private Map<Short, Player> players;
    private Map<Integer, Monster> monsters;
    private BlockingQueue<Hit> hitQueue;
    private Canvas canvas;
    private boolean run;
    private Player player;
    private Stage stage;
    private Udp udp;
    private User user;

    public Game(Canvas canvas_, Stage s, Udp udp_, User user_, boolean run_, Map<Short, Player> players_, Map<Integer, Monster> monsters_, BlockingQueue<Hit> hitQueue_, Player p) {
        canvas = canvas_;
        players = players_;
        monsters = monsters_;
        hitQueue = hitQueue_;
        player = p;
        run = run_;
        stage = s;
        udp = udp_;
        user = user_;
    }

    public void stopit() {
        run = false;
    }

    private EventHandler<KeyEvent> keyPress = event -> {
        //todo make it better
        System.out.println("Filtering out event " + event.getEventType());
        try {
            switch (event.getCode()) {
                case W:
                    udp.send(Command.create().add('M').add(user.getId()).
                            add(user.getSecret()).add("N").getMessage());
                    break;
                case S:
                    udp.send(Command.create().add('M').add(user.getId()).
                            add(user.getSecret()).add("S").getMessage());
                    break;
                case D:
                    udp.send(Command.create().add('M').add(user.getId()).
                            add(user.getSecret()).add("E").getMessage());
                    break;
                case A:
                    udp.send(Command.create().add('M').add(user.getId()).
                            add(user.getSecret()).add("W").getMessage());
                    break;
                case SPACE:
                    udp.send(Command.create().add('A').add(user.getId()).
                            add(user.getSecret()).getMessage());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.consume();
    };

    private EventHandler<KeyEvent> keyRelease = event -> {
        System.out.println("Filtering out event " + event.getEventType());
        try {
            udp.send(Command.create().add('S').add(user.getId()).
                    add(user.getSecret()).getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.consume();
    };

    public void run() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 1600, 900);

        final short w = 50;
        final short h = 50;

        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyPress);
        stage.addEventFilter(KeyEvent.KEY_RELEASED, keyRelease);


        int counter = 0;
        while (run) {
            //32+3 * 18+3

            //make game here please
            double x_offset = -75 + (((double) player.getX() - player.getDx()) * w);
            double y_offset = -75 + (((double) player.getY() - player.getDy()) * h);
            int starting_x = player.getX() - 17;
            int starting_y = player.getY() - 10;

            for (int y = 0; y < 22; ++y)
                for (int x = 0; x < 36; ++x)
                    switch (Tile.Type(starting_x + x, starting_y + y)) {
                        case 1:
                            gc.setFill(Color.YELLOW);
                            gc.fillRect((double) x * w + x_offset, y * h + y_offset, w, h);
                            break;
                        case 2:
                            gc.setFill(Color.BLUE);
                            gc.fillRect((double) x * w + x_offset, y * h + y_offset, w, h);
                            break;
                        case 3:
                            gc.setFill(Color.VIOLET);
                            gc.fillRect((double) x * w + x_offset, y * h + y_offset, w, h);
                            break;
                    }
            for (Map.Entry<Integer, Monster> entry : monsters.entrySet()) {
                Monster m = entry.getValue();
                if (m.getX() >= starting_x - 1 && m.getX() <= starting_x + 35 && m.getY() >= starting_y - 1 && m.getY() <= starting_y + 21)
                    m.draw(((double) m.getX() - (double) starting_x) * w + x_offset,
                            ((double) m.getY() - (double) starting_y) * h + y_offset//maths
                            , w, h, gc);
            }

            for (Map.Entry<Short, Player> entry : players.entrySet()) {
                Player p = entry.getValue();
                if ((p.getChanged() || player.getChanged()) && p != player && p.getX() >= starting_x && p.getX() <= starting_x + 35 && p.getY() >= starting_y && p.getY() <= starting_y + 21)
                    p.draw((p.getDx() - (double) starting_x) * w + x_offset //math need
                            , (p.getDy() - (double) starting_y) * h + y_offset //math need
                            , w, h, gc);
            }

            player.draw(775, 425, w, h, gc);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        gc.drawImage(new Image(this.getClass().getResourceAsStream("/back.jpg")), 0, 0, 1600, 900);

    }
}
