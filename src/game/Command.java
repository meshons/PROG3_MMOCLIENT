package game;

/**
 * Egy parancsot előállító segédosztály UDP-hez és TCP-hez
 */
public class Command {
    /**
     * Az üzenet
     */
    private String message = "";

    /** Hozzáadja az üzenethez a value értékét és egy szóközt
     * @param value a hozzáadandó String
     * @return visszatér saját magával
     */
    public Command add(String value){
        message+=value;
        message+=' ';
        return this;
    }

    /** Hozzáadja az üzenethez a value karaktert és egy szóközt
     * @param value a hozzáadandó karakter
     * @return visszatér saját magával
     */
    public Command add(char value){
        message+=value;
        message+=' ';
        return this;
    }

    /**
     * @param value
     * @return
     */
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
}
