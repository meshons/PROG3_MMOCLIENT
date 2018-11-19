package game;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Game extends Thread {

    private Map<Short, Player> players = new ConcurrentHashMap<>();
    private Map<Integer, Monster> monsters = new ConcurrentHashMap<>();
    private BlockingQueue<Hit> hitQueue = new LinkedBlockingQueue<>();
    private Canvas canvas;
    private boolean run;

    public Game(Canvas canvas_,boolean run_,Map<Short, Player> players_,Map<Integer, Monster> monsters_, BlockingQueue<Hit> hitQueue_){
        canvas=canvas_;
        players=players_;
        monsters=monsters_;
        hitQueue=hitQueue_;
        run=run_;
    }

    public void run(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,1600,900);
        while (run){

        }
    }
}
