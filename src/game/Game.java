package game;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.input.KeyEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
    private long lastAction = 0;

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

        try {
            switch (event.getCode()) {
                case W:
                    udp.send(Command.create().add('M').add(user.getId()).
                            add(user.getSecret()).add("N").getMessage());
                    lastAction = System.currentTimeMillis();
                    break;
                case S:
                    udp.send(Command.create().add('M').add(user.getId()).
                            add(user.getSecret()).add("S").getMessage());
                    lastAction = System.currentTimeMillis();
                    break;
                case D:
                    udp.send(Command.create().add('M').add(user.getId()).
                            add(user.getSecret()).add("E").getMessage());
                    lastAction = System.currentTimeMillis();
                    break;
                case A:
                    udp.send(Command.create().add('M').add(user.getId()).
                            add(user.getSecret()).add("W").getMessage());
                    lastAction = System.currentTimeMillis();
                    break;
                case SPACE:
                    udp.send(Command.create().add('A').add(user.getId()).
                            add(user.getSecret()).getMessage());
                    lastAction = System.currentTimeMillis();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        event.consume();
    };

    private EventHandler<KeyEvent> keyRelease = event -> {
        try {
            udp.send(Command.create().add('S').add(user.getId()).
                    add(user.getSecret()).getMessage());
            lastAction = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.consume();
    };

    public void run() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image grass = new Image(this.getClass().getResourceAsStream("/fu.png"));
        Image grass2 = new Image(this.getClass().getResourceAsStream("/fu2.png"));
        Image rock = new Image(this.getClass().getResourceAsStream("/ko.png"));
        Image water = new Image(this.getClass().getResourceAsStream("/viz.png"));

        Runnable updater = new Runnable() {
            @Override
            public void run() {
                gc.clearRect(0, 0, 1600, 900);

                final short w = 50;
                final short h = 50;
                Font font1 = new Font("Bitstream Vera Sans Mono Bold", 16);

                stage.addEventFilter(KeyEvent.KEY_PRESSED, keyPress);
                stage.addEventFilter(KeyEvent.KEY_RELEASED, keyRelease);
                //while (run) {
                //32+3 * 18+3
                if (System.currentTimeMillis() - lastAction > 30000) {
                    try {
                        udp.send(Command.create().add('S').add(user.getId()).
                                add(user.getSecret()).getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lastAction = System.currentTimeMillis();
                }

                for (Map.Entry<Short, Player> entry : players.entrySet()) {
                    Player p = entry.getValue();
                    if (p.getAct() == Action.Move) {
                        p.setAnimationpercent((double) (System.currentTimeMillis() - p.getActionstart()) / p.getActionDuration());
                        if (p.getAnimationpercent() > 1.0)
                            p.setAnimationpercent(1.0);
                        switch (p.getDir()) {
                            case NORTH:
                                p.setDy((double) p.getY() - p.getAnimationpercent());
                                break;
                            case SOUTH:
                                p.setDy((double) p.getY() + p.getAnimationpercent());
                                break;
                            case EAST:
                                p.setDx((double) p.getX() + p.getAnimationpercent());
                                break;
                            case WEST:
                                p.setDx((double) p.getX() - p.getAnimationpercent());
                                break;
                        }

                    } else if (p.getAct() == Action.Attack)
                        p.setAnimationpercent((double) (System.currentTimeMillis() - p.getActionstart()) / p.getActionDuration());
                    else if (p.getAct() == Action.Move_animation) {
                        p.setAnimationpercent((double) (System.currentTimeMillis() - p.getActionstart()) / 300);
                    }
                    while (p.getAnimationpercent() > 1.0)
                        p.setAnimationpercent(p.getAnimationpercent() - 1.0);
                }


                //make game here please
                double x_offset = -75 + (((double) player.getX() - player.getDx()) * w);
                double y_offset = -75 + (((double) player.getY() - player.getDy()) * h);
                int starting_x = player.getX() - 17;
                int starting_y = player.getY() - 10;


                for (int y = 0; y < 22; ++y)
                    for (int x = 0; x < 36; ++x)
                        switch (Tile.Type(starting_x + x, starting_y + y)) {
                            case 1:
                                gc.drawImage(grass2, (double) x * w + x_offset, y * h + y_offset, w, h);
                                break;
                            case 4:
                                gc.drawImage(grass, (double) x * w + x_offset, y * h + y_offset, w, h);
                                break;
                            case 2:
                                gc.drawImage(water, (double) x * w + x_offset, y * h + y_offset, w, h);
                                break;
                            case 3:
                                gc.drawImage(rock, (double) x * w + x_offset, y * h + y_offset, w, h);
                                break;
                        }

                for (Map.Entry<Short, Player> entry : players.entrySet()) {
                    Player p = entry.getValue();
                    if (p != player && p.getX() >= starting_x && p.getX() <= starting_x + 35 && p.getY() >= starting_y && p.getY() <= starting_y + 21)
                        p.draw((p.getDx() - (double) starting_x) * w + x_offset //math need
                                , (p.getDy() - (double) starting_y) * h + y_offset //math need
                                , w, h, gc);
                }
                player.draw(775, 425, w, h, gc);
                for (Map.Entry<Integer, Monster> entry : monsters.entrySet()) {
                    Monster m = entry.getValue();
                    if (m.getX() >= starting_x - 1 && m.getX() <= starting_x + 35 && m.getY() >= starting_y - 1 && m.getY() <= starting_y + 21)
                        m.draw(((double) m.getX() - (double) starting_x) * w + x_offset,
                                ((double) m.getY() - (double) starting_y) * h + y_offset//maths
                                , w, h, gc);
                }
                gc.setFill(Color.BLUE);
                gc.setFont(font1);
                gc.setTextAlign(TextAlignment.LEFT);
                gc.fillText("x: " + String.valueOf(player.getX()) + ", y: " + String.valueOf(player.getY()), 5, 21);
            }
        };

        while (run) {
            try {
                Platform.runLater(updater);
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        gc.drawImage(new Image(this.getClass().getResourceAsStream("/back.jpg")), 0, 0, 1600, 900);

    }
}
