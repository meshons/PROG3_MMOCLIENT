package game;

public class Tile {
    static short Type(int x,int y){
        if (((x+(y*66536))%87)==0)return 3;
        if (x * x + y * y > 30000 * 30000)return 2;
        return 1;
    }
}
