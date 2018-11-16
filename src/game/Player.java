package game;

public class Player {
    private String name;
    private short x,y;
    private short id;
    private Direction  dir = Direction.SOUTH;
    private Action act = Action.Stand;//todo improve it for later use
    private short actpercent = 100;

    public Player(String name_,short id_,short x_,short y_){
        name = name_;
        x=x_;
        y=y_;
        id=id_;
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
        switch (data[4]){
            case "stand":
                act = Action.Stand;
                break;
            case "attack":
                act = Action.Attack;
                break;
            // no need for more
        }
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
    }
}
