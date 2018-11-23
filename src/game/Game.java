package game;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;


/**
 * A játék osztály, ami a pályát rajzolja ki.
 */
public class Game extends Thread {

    /**
     * A játékosok tárolója
     */
    private Map<Short, Player> players;
    /**
     * A szörnyek tárolója
     */
    private Map<Integer, Monster> monsters;
    /**
     * A találatok tárolója
     */
    private CopyOnWriteArrayList<Hit> hitArrayList;
    /**
     * A játék vászna
     */
    private Canvas canvas;
    /**
     * A szál futását szabályozó bool
     */
    private boolean run;
    /**
     * A felhasználó játékosa
     */
    private Player player;
    /**
     * A javafx primary stage-e
     */
    private Stage stage;
    /**
     * Az udp kapcsolat
     */
    private Udp udp;
    /**
     * A felhasználó, ami tartalmazza a kulcsot az udp-hez
     */
    private User user;
    /**
     * A legutoljára végrehajtott utasítás ideje
     */
    private long lastAction = 0;

    /** A játék konstruktora, beállítja az értékeket
     * @param canvas_ A játék vászna
     * @param s A javafx primary stage-e
     * @param udp_ Az udp kapcsolat
     * @param user_ A felhasználó
     * @param run_ A futást jelző bool
     * @param players_ A játékosok tárolója
     * @param monsters_ A szörnyek tárolója
     * @param hitQueue_ A találatok tárolója
     * @param p A felhasználó játékosa
     */
    public Game(Canvas canvas_, Stage s, Udp udp_, User user_, boolean run_, Map<Short, Player> players_, Map<Integer, Monster> monsters_, CopyOnWriteArrayList<Hit> hitQueue_, Player p) {
        canvas = canvas_;
        players = players_;
        monsters = monsters_;
        hitArrayList = hitQueue_;
        player = p;
        run = run_;
        stage = s;
        udp = udp_;
        user = user_;
    }

    /**
     * A szál leállítása, a run bool-t billenti false-ra
     */
    public void stopit() {
        run = false;
    }

    /**
     * A billentyű leütést kezelő EventHandler
     * Udp-t küld, ha mozgás van vagy támadás (WASD, space)
     * Z betűvel kiléptet
     * P betűvel printscreen-t csinál
     */
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
                case Z:
                    run =false;
                    break;
                case P:
                    WritableImage image = stage.getScene().snapshot(null);
                    File file = new File("chart.png");
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        event.consume();
    };

    /**
     * A billentyű elengedést kezelő Eventhandler
     * elküld, egy stand jelet udp-n
     */
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

    /**
     * A játék thread-jét indítja el
     * folyamatosan kirajzolja a pályát a javaFX threadjére
     */
    public void run() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image grass = new Image(this.getClass().getResourceAsStream("/fu.png"));
        Image grass2 = new Image(this.getClass().getResourceAsStream("/fu2.png"));
        Image rock = new Image(this.getClass().getResourceAsStream("/ko.png"));
        Image water = new Image(this.getClass().getResourceAsStream("/viz.png"));

        Runnable updater = new Runnable() {
            @Override
            public void run() {
                gc.clearRect(0, 0, stage.getWidth(), stage.getHeight());

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

                //todo responsive!!
                int row = (int)(stage.getHeight()/w+3);
                if(row%2==0)row++;
                int column = (int)(stage.getWidth()/h+3);
                if(column%2==0)column++;
                int starting_x = player.getX() - column/2;
                int starting_y = player.getY() - row/2;
                double x_offset = -1*(column*w-stage.getWidth())/2 + (((double) player.getX() - player.getDx()) * w);
                double y_offset = -1*(row*h-stage.getHeight())/2 + (((double) player.getY() - player.getDy()) * h);

                for (int y = 0; y < row; ++y)
                    for (int x = 0; x < column; ++x)
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


                for (Map.Entry<Integer, Monster> entry : monsters.entrySet()) {
                    Monster m = entry.getValue();
                    if (m.getX() >= starting_x - 1 && m.getX() <= starting_x + column+1 && m.getY() >= starting_y - 1 && m.getY() <= starting_y + row+1)
                        m.draw(((double) m.getX() - (double) starting_x) * w + x_offset,
                                ((double) m.getY() - (double) starting_y) * h + y_offset//maths
                                , w, h, gc);
                }
                player.draw(stage.getWidth()/2-25, stage.getHeight()/2-25, w, h, gc);
                for (Map.Entry<Short, Player> entry : players.entrySet()) {
                    Player p = entry.getValue();
                    if (p != player && p.getX() >= starting_x && p.getX() <= starting_x + column+1 && p.getY() >= starting_y && p.getY() <= starting_y + row+1)
                        p.draw((p.getDx() - (double) starting_x) * w + x_offset //math need
                                , (p.getDy() - (double) starting_y) * h + y_offset //math need
                                , w, h, gc);
                }
                for(Hit hit:hitArrayList){
                    if(hit.getEnd()<System.currentTimeMillis())
                        hitArrayList.remove(hit);
                    else
                        hit.draw(((double) hit.getX() - (double) starting_x) * w + x_offset,
                                ((double) hit.getY() - (double) starting_y) * h + y_offset,gc);
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
