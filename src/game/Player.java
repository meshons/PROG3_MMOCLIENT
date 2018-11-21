package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    private String name;
    private short x,y;
    private double dx, dy;
    private short id;
    private Direction  dir = Direction.SOUTH;
    private Action act = Action.Stand;//todo improve it for later use
    private short actpercent = 100;
    private boolean changed = true;

    public Player(String name_,short id_,short x_,short y_){
        name = name_;
        x=x_;
        y=y_;
        id=id_;
        dx=x;
        dy=y;
    }
    public String getName(){
        return name;
    }

    public short getId() {
        return id;
    }

    public void update(String[] data){
        x=Short.parseShort(data[2]);
        y=Short.parseShort(data[3]);
        actpercent=Short.parseShort(data[5]);
        switch (data[6]){
            case "north":
                dir=Direction.NORTH;
                break;
            case "south":
                dir=Direction.SOUTH;
                break;
            case "east":
                dir=Direction.EAST;
                break;
            case "west":
                dir=Direction.WEST;
                break;
        }

        switch (data[4]){
            case "stand":
                act = Action.Stand;
                dx=x;
                dy=y;
                break;
            case "attack":
                act = Action.Attack;
                dx=x;
                dy=y;
                break;
            case "move":
                act=Action.Move;
                //todo calc dx, dy
                changed=true;
                switch (dir){
                    case NORTH:
                        dy=y-((double)1/(101-actpercent));
                        break;
                    case SOUTH:
                        dy=y+((double)1/(101-actpercent));
                        break;
                    case EAST:
                        dx=x+((double)1/(101-actpercent));
                        break;
                    case WEST:
                        dx=x-((double)1/(101-actpercent));
                        break;
                }
                break;
            case "movea":
                act=Action.Move_animation;
                dx=x;
                dy=y;
        }
    }

    public void draw(double x, double y, short w, short h, GraphicsContext gc){
        gc.setFill(Color.ORANGE);
        gc.fillRect(x, y, w, h);
    }

    public void change(){
        changed=false;
    }
    public boolean getChanged(){
        return changed;
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
}
