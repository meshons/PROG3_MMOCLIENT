package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Hit {
    private short x;
    private short y;
    private int value;
    private long end;
    private static Font font = new Font("Bitstream Vera Sans Mono Bold", 36);

    public Hit(short x_,short y_, int value_){
        x=x_;
        y=y_;
        value=value_;
        end=System.currentTimeMillis()+200;
    }
    public void draw(double x,double y,GraphicsContext gc){
        gc.setFill(Color.BLUE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(font);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.fillText("-"+String.valueOf(value/1000)+"K", x + 25, y + 40);
        gc.strokeText("-"+String.valueOf(value/1000)+"K", x + 25, y + 40);
    }

    public long getEnd() {
        return end;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }
}
