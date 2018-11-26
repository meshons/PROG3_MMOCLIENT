package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;

public class Player {
    private String name;
    private short x, y;
    private double dx, dy;
    private short id;
    private Direction dir = Direction.SOUTH;
    private Action act = Action.Stand;
    private short actpercent = 100;
    private double animationpercent = 0.0;
    private int actionNumber = 0;
    private long actionstart = 0;
    private double actionDuration = 0;
    private boolean changed = true;
    private static Font font = new Font("Bitstream Vera Sans Mono Bold", 16);

    private static Image[] player =
            {
                    new Image(Monster.class.getResourceAsStream("/playerN.png")),
                    new Image(Monster.class.getResourceAsStream("/playerE.png")),
                    new Image(Monster.class.getResourceAsStream("/playerS.png")),
                    new Image(Monster.class.getResourceAsStream("/playerW.png"))
            };
    private static Image[] left =
            {
                    new Image(Monster.class.getResourceAsStream("/assets/leg/leftN.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/leg/leftE.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/leg/leftS.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/leg/leftW.png"))
            };
    private static Image[] right =
            {
                    new Image(Monster.class.getResourceAsStream("/assets/leg/rightN.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/leg/rightE.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/leg/rightS.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/leg/rightW.png"))
            };
    private static Image[] pizza =
            {
                    new Image(Monster.class.getResourceAsStream("/assets/pizzaN.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/pizzaE.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/pizzaS.png")),
                    new Image(Monster.class.getResourceAsStream("/assets/pizzaW.png"))
            };

    public Player(String name_, short id_, short x_, short y_) {
        name = name_;
        x = x_;
        y = y_;
        id = id_;
        dx = x;
        dy = y;
    }

    public String getName() {
        return name;
    }

    public short getId() {
        return id;
    }

    public void update(String[] data) {
        x = Short.parseShort(data[2]);
        y = Short.parseShort(data[3]);

        if (actionNumber != Integer.parseInt(data[7])) {
            switch (data[4]) {
                case "stand":
                    act = Action.Stand;
                    dx = x;
                    dy = y;
                    break;
                case "attack":
                    act = Action.Attack;
                    dx = x;
                    dy = y;
                    break;
                case "move":
                    act = Action.Move;
                    break;
                case "movea":
                    act = Action.Move_animation;
                    dx = x;
                    dy = y;
            }
            actpercent = Short.parseShort(data[5]);
            actionstart = System.currentTimeMillis();

            if (act == Action.Attack)
                actionDuration = ((double) (100-actpercent) / 100 * 290) + 10;
            else if (act == Action.Move)
                actionDuration = ((double) (100-actpercent) / 100 * 240) + 10;

            animationpercent = 0.0;
            actionNumber = Integer.parseInt(data[7]);
        }

        switch (data[6]) {
            case "north":
                dir = Direction.NORTH;
                break;
            case "south":
                dir = Direction.SOUTH;
                break;
            case "east":
                dir = Direction.EAST;
                break;
            case "west":
                dir = Direction.WEST;
                break;
        }


    }

    public void draw(double x, double y, short w, short h, GraphicsContext gc) {
        switch (act) {
            case Stand:
                switch (dir) {
                    case NORTH:
                        gc.drawImage(left[0], x + 4, y + 14, w - 10, h);
                        gc.drawImage(right[0], x + 6, y + 14, w - 10, h);
                        gc.drawImage(player[0], x + 6, y, w - 12, h);
                        break;
                    case SOUTH:
                        gc.drawImage(left[2], x + 6, y - 14, w - 10, h);
                        gc.drawImage(right[2], x + 4, y - 14, w - 10, h);
                        gc.drawImage(player[2], x + 6, y, w - 12, h);
                        break;
                    case EAST:
                        gc.drawImage(left[1], x - 14, y + 4, w, h - 10);
                        gc.drawImage(right[1], x - 14, y + 6, w, h - 10);
                        gc.drawImage(player[1], x, y + 6, w, h - 12);
                        break;
                    case WEST:
                        gc.drawImage(left[3], x + 14, y + 6, w, h - 10);
                        gc.drawImage(right[3], x + 14, y + 4, w, h - 10);
                        gc.drawImage(player[3], x, y + 6, w, h - 12);
                        break;
                }
                break;
            case Move:
            case Move_animation:
                switch (dir) {
                    case NORTH:
                        if (animationpercent <= 0.5) {
                            gc.drawImage(left[0], x + 4, y + 20 - ((animationpercent) * w / 2), w - 10, h);
                            gc.drawImage(right[0], x + 6, y + 20 - (double) w / 4 + ((animationpercent) * w / 2), w - 10, h);
                        } else {
                            gc.drawImage(left[0], x + 4, y + 20 - ((1 - animationpercent) * w / 2), w - 10, h);
                            gc.drawImage(right[0], x + 6, y + 20 - (double) w / 4 + ((1 - animationpercent) * w / 2), w - 10, h);
                        }
                        gc.drawImage(player[0], x + 6, y, w - 12, h);
                        break;
                    case SOUTH:
                        if (animationpercent <= 0.5) {
                            gc.drawImage(left[2], x + 6, y -8 - ((animationpercent) * w / 2), w - 10, h);
                            gc.drawImage(right[2], x + 4, y -8 - (double) w / 4 + ((animationpercent) * w / 2), w - 10, h);
                        } else {
                            gc.drawImage(left[2], x + 6, y -8 - ((1 - animationpercent) * w / 2), w - 10, h);
                            gc.drawImage(right[2], x + 4, y -8 - (double) w / 4 + ((1 - animationpercent) * w / 2), w - 10, h);
                        }
                        gc.drawImage(player[2], x + 6, y, w - 12, h);
                        break;
                    case EAST:
                        if (animationpercent <= 0.5) {
                            gc.drawImage(left[1], x -8 - ((animationpercent) * w / 2), y + 4, w , h-10);
                            gc.drawImage(right[1], x -8 - (double) w / 4 + ((animationpercent) * w / 2), y + 6, w, h - 10);
                        } else {
                            gc.drawImage(left[1], x -8 - ((1 - animationpercent) * w / 2), y + 4, w, h-10);
                            gc.drawImage(right[1], x -8 - (double) w / 4 + ((1 - animationpercent) * w / 2), y + 6, w, h - 10);
                        }
                        gc.drawImage(player[1], x, y + 6, w, h - 12);
                        break;
                    case WEST:
                        if (animationpercent <= 0.5) {
                            gc.drawImage(left[3], x + 20 - ((animationpercent) * w / 2), y + 6, w , h-10);
                            gc.drawImage(right[3], x + 20 - (double) w / 4 + ((animationpercent) * w / 2), y + 4, w, h - 10);
                        } else {
                            gc.drawImage(left[3], x + 20 - ((1 - animationpercent) * w / 2), y + 6, w, h-10);
                            gc.drawImage(right[3], x + 20 - (double) w / 4 + ((1 - animationpercent) * w / 2), y + 4, w, h - 10);
                        }
                        gc.drawImage(player[3], x, y + 6, w, h - 12);
                        break;
                }
                break;
            case Attack:
                gc.setFill(Color.BLUE);
                switch (dir) {
                    case NORTH:
                        gc.drawImage(left[0], x + 4, y + 14, w - 10, h);
                        gc.drawImage(right[0], x + 6, y + 14, w - 10, h);
                        gc.drawImage(pizza[0],x+(double)w/2-15,y-30-5-((animationpercent) * h * 0.8),30,30);
                        gc.drawImage(player[0], x + 6, y, w - 12, h);
                        break;
                    case SOUTH:
                        gc.drawImage(left[2], x + 6, y - 14, w - 10, h);
                        gc.drawImage(right[2], x + 4, y - 14, w - 10, h);
                        gc.drawImage(pizza[2],x+(double)w/2-15,y+w+5+((animationpercent) * h * 0.8),30,30);
                        gc.drawImage(player[2], x + 6, y, w - 12, h);
                        break;
                    case EAST:
                        gc.drawImage(left[1], x - 14, y + 4, w, h - 10);
                        gc.drawImage(right[1], x - 14, y + 6, w, h - 10);
                        gc.drawImage(pizza[3],x+h+5+((animationpercent) * w * 0.8),y+(double)h/2-15, 30,30);
                        gc.drawImage(player[1], x, y + 6, w, h - 12);
                        break;
                    case WEST:
                        gc.drawImage(left[3], x + 14, y + 6, w, h - 10);
                        gc.drawImage(right[3], x + 14, y + 4, w, h - 10);
                        gc.drawImage(pizza[1],x-30-5-((animationpercent) * w * 0.8),y+(double)h/2-15,30,30);
                        gc.drawImage(player[3], x, y + 6, w, h - 12);
                        break;
                }
                break;

        }


        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(font);
        gc.fillText(name, x + (double) w / 2, y - 2);
    }

    public void change() {
        changed = false;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDx(double x) {
        dx = x;
    }

    public void setDy(double y) {
        dy = y;
    }

    public Action getAct() {
        return act;
    }

    public double getAnimationpercent() {
        return animationpercent;
    }

    public void setAnimationpercent(double animationpercent) {
        this.animationpercent = animationpercent;
    }

    public long getActionstart() {
        return actionstart;
    }

    public Direction getDir() {
        return dir;
    }

    public double getActionDuration() {
        return actionDuration;
    }
}
