package game;

import java.util.ArrayList;

public class Command {
    private String message = "";
    public Command add(String value){
        message+=value;
        message+=' ';
        return this;
    }

    public Command add(char value){
        message+=value;
        message+=' ';
        return this;
    }

    public Command add(short value){
        message+=Short.toString(value);
        message+=' ';
        return this;
    }


    public String getMessage(){
        message+='\n';
        return message;
    }

    public static Command create(){
        return new Command();
    }

    public static String[] get(){
        return new String[12];
    }
}
