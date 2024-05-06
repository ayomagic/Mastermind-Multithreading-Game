import java.util.ArrayList;

public class DataBase {
    public int guess_left;
    public String[] colors;
    public int pegNum;
    public ArrayList<String> color_set;
    public ArrayList<String> result_set;

    // Check if this line is correct
    public String answer;
    DataBase(String Code) {
        this.guess_left = GameConfiguration.guessNumber;
        this.colors = GameConfiguration.colors;
        this.pegNum = GameConfiguration.pegNumber;
        this.color_set = new ArrayList<>();
        this.result_set = new ArrayList<>();
        this.answer = Code;
    }
}
