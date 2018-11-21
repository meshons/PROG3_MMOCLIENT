package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.FontWeight;

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
    private boolean changed = true;
    private static Font font = new Font("Bitstream Vera Sans Mono Bold", 16);


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
            actionstart=System.currentTimeMillis();
            actpercent = Short.parseShort(data[5]);
            animationpercent=0.0;
        }
        actionNumber = Integer.parseInt(data[7]);
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
                //todo calc dx, dy
                /*changed=true;
                */
                break;
            case "movea":
                act = Action.Move_animation;
                dx = x;
                dy = y;
        }
    }

    public void draw(double x, double y, short w, short h, GraphicsContext gc) {
        gc.setFill(Color.ORANGE);
        gc.fillRect(x, y, w, h);
        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(font);
        gc.fillText(name,x+(double)w/2,y-2);
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
        dx=x;
    }

    public void setDy(double y) {
        dy=y;
    }
    public Action getAct(){
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
}
