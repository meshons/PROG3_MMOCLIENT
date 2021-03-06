package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Monster {
    private int id;
    private short x,y;
    private int health;
    private int basehealth;
    private Direction direction;
    private static Image monster = new Image(Monster.class.getResourceAsStream("/monster.png"));

    public Monster(int id_,int hp_,short x_,short y_){
        id=id_;
        x=x_;
        y=y_;
        health=hp_;
        basehealth=hp_;
    }

    public void update(String[] data){
        health=Integer.parseInt(data[2]);
        switch (data[4]){
            case "north":
                direction=Direction.NORTH;
                break;
            case "south":
                direction=Direction.SOUTH;
                break;
            case "east":
                direction=Direction.EAST;
                break;
            case "west":
                direction=Direction.WEST;
                break;
        }
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public void draw(double x, double y, short w, short h, GraphicsContext gc){
        gc.drawImage(monster,x+5,y,w*2-10,h*2);
        //healthbar
        gc.setFill(Color.BLACK);
        gc.fillRect(x+5, y-18,w*2-10, 14);
        gc.setFill(Color.RED);
        gc.fillRect(x+6, y-17,(w*2-12)*((double)health/basehealth), 12);
    }
}
