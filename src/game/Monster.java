package game;

public class Monster {
    //todo
    private int id;
    private short x,y;
    private int health;
    private Direction direction;

    public Monster(int id_,int hp_,short x_,short y_){
        id=id_;
        x=x_;
        y=y_;
        health=hp_;
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
}
