import java.io.*;
import java.net.*;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
/*
// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class MyServer
{

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    // counter for clients
    static int i = 0;

    public static void main(String[] args) throws IOException
    {
        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        // running infinite loop for getting
        // client request
        while (true)
        {
            // Accept the incoming request
            s = ss.accept();

            System.out.println("New client request received : " + s);

            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);

            System.out.println("Adding this client to active client list");

            // add this client to active clients list
            ar.add(mtch);

            // start the thread.
            t.start();

            // increment i for new client.
            // i is used for naming only, and can be replaced
            // by any naming scheme
            i++;

        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();

                System.out.println(received);

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }

                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : MyServer.ar)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(recipient) && mc.isloggedin==true)
                    {
                        mc.dos.writeUTF(this.name+" : "+MsgToSend);
                        break;
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
*/



public class MyServer {
    static int i = 0;
    static volatile boolean ThereIsWinner = false;
    public static HashMap<ClientHandler, DataBase> ar = new HashMap<ClientHandler, DataBase>();
    public static void main(String[] args){
        try {

            ServerSocket ss = new ServerSocket(6666);
            String secretCode = SecretCodeGenerator.getInstance().getNewSecretCode();
            System.out.println("Secret Code: " + secretCode);

            while (!ThereIsWinner) {
                Socket s = ss.accept();//establishes connection
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                System.out.println("Player " + i + " has connected successfully");
                ClientHandler match = new ClientHandler(s, "client " + i, dis, dos);
                Thread t = new Thread(match);
                ar.put(match, new DataBase(secretCode));
                t.start();
                i++;
            }
            System.out.println("Process finished with exit code 0");

            ThereIsWinner = true;

            for (ClientHandler curr : ar.keySet()) {
                curr.stopThread();
            }

            ss.close();
            System.exit(0);
        } catch (IOException e){
            System.out.println(e);
        }
    }
}

class ClientHandler implements Runnable {
    public String name;
    public DataInputStream dis;
    public DataOutputStream dos;
    public Socket s;
    public boolean Iwon;
    public boolean SomeOneElseWon;
    public volatile boolean stopThread = false;


    // constructor
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.Iwon=false;
        this.SomeOneElseWon = false;

    }
    @Override
    public void run() {
        String received;
        try {
            dos.writeUTF(
                    "\nWelcome to Mastermind. Here are the rules.\n"+
                            "\nThis is a text version of the classic board game Mastermind.\n"+
                            "\nThe computer will think of a secret code. The code consists of 4\n"+
                            "colored pegs. The pegs MUST be one of six colors: blue, green,\n"+
                            "orange, purple, red, or yellow. A color may appear more than once in\n"+
                            "the code. You try to guess what colored pegs are in the code and\n"+
                            "what order they are in. After you make a valid guess the result\n"+
                            "(feedback) will be displayed.\n"+
                            "\nThe result consists of a black peg for each peg you have guessed\n"+
                            "exactly correct (color and position) in your guess. For each peg in\n"+
                            "the guess that is the correct color, but is out of position, you get\n"+
                            "a white peg. For each peg, which is fully incorrect, you get no\n"+
                            "feedback.\n"+
                            "\nOnly the first letter of the color is displayed. B for Blue, R for\n"+
                            "Red, and so forth. When entering guesses you only need to enter the\n"+
                            "first character of each color as a capital letter.\n"+
                            "You have 12 guesses to figure out the secret code or you lose the\n"+
                            "game. Are you ready to play? (Y/N): ");

            received = dis.readUTF();

            if (!(received.equals("y") || received.equals("Y"))) {
                try {
                    this.dis.close();
                    this.dos.close();
                    this.s.close();
                    dos.writeUTF("Process finished with exit code 0");

                }catch(IOException e) {
                    e.printStackTrace();
                }
            }

            dos.writeUTF("\nGenerating secret code ...");
            while (!MyServer.ThereIsWinner && MyServer.ar.get(this).guess_left > 0 && !stopThread) {
                dos.writeUTF("\nYou have " + MyServer.ar.get(this).guess_left + " guesses left." +
                        "\nWhat is your next guess?" +
                        "\nType in the characters for your guess and press enter." +
                        "\nEnter guess: "
                );

                received = dis.readUTF();
                while (!this.Check(received)) {
                    dos.writeUTF("\n" + received + " -> INVALID GUESS");
                    dos.writeUTF(
                            "\nWhat is your next guess?" +
                                    "\nType in the characters for your guess and press enter." +
                                    "\nEnter guess: "
                    );
                    received = dis.readUTF();
                }

                if (received.equals("HISTORY")) {
                    dos.writeUTF("\n");
                    for (int i = 0; i < MyServer.ar.get(this).color_set.size(); i++) {
                        dos.writeUTF(MyServer.ar.get(this).color_set.get(i) + "\t\t" + MyServer.ar.get(this).result_set.get(i));
                    }
                    continue;
                }

                // Valid input => check if match with guess
                String black_white = this.GetResult(received);
                if (MyServer.ar.get(this).answer.equals(received)) {
                    if (MyServer.ThereIsWinner) { break; }
                    this.Iwon = true;
                    MyServer.ThereIsWinner = true;
                    // Matches with random answer
                    dos.writeUTF("\n" + MyServer.ar.get(this).answer + " -> Result: " + black_white + " - You win !!");
                    break;

                } else {
                    // Does not match with random generated answer
                    if (MyServer.ar.get(this).guess_left == 1)
                        break;
                    dos.writeUTF("\n" + received + " -> Result: " + black_white);
                    MyServer.ar.get(this).guess_left--;
                    MyServer.ar.get(this).color_set.add(received);
                    MyServer.ar.get(this).result_set.add(black_white);
                }
            }
        } catch (IOException e) {
            e.getMessage();
        }

        if (MyServer.ThereIsWinner && !this.Iwon) { this.SomeOneElseWon = true;}

        if (MyServer.ThereIsWinner && this.SomeOneElseWon || !this.Iwon) {
            try {
                dos.writeUTF("Disconnecting from game because you lost.");
                dos.writeUTF("Process finished with exit code 0");
            } catch (IOException e) {
            }
        }else {
            try {
                dos.writeUTF("Congrats you won the game.");
            } catch (IOException e) {
            }
        }

        try
        {
            //closing resources
            this.dis.close();
            this.dos.close();
            this.s.close();

        }catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println(this.name + " has been disconnected.");
    }
    public String GetResult(String in) {
        int black = 0, white = 0;
        ArrayList<Character> tmp = new ArrayList<Character>();
        ArrayList<Character> ans = new ArrayList<Character>();

        // Copy characters to temporary strings
        for (int i = 0; i < in.length(); i++) {
            tmp.add(in.charAt(i));
            ans.add(MyServer.ar.get(this).answer.charAt(i));
        }

        // Check for black pegs
        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i) == MyServer.ar.get(this).answer.charAt(i)) {
                black++;
                tmp.set(i, '-');
                ans.set(i, '-');
            }
        }

        // Check for white pegs
        for (int i = 0; i < tmp.size(); i++) {
            for (int j = 0; j < ans.size(); j++) {
                if (tmp.get(i) == '-' && ans.get(j) == '-') {
                    break;
                } else if (tmp.get(i) == ans.get(j)) {
                    white++;
                    tmp.set(i, '-');
                    ans.set(j, '-');
                    //Once you remove you have to reset
                    break;
                }
            }
        }
        return black + "B_" + white + "W";
    }
    public boolean Check(String input) {
        if (input.equals("HISTORY")) {
            return true;
        }

        //Check if length of input is the same
        if (input.length() != MyServer.ar.get(this).pegNum)
            return false;

        String tmp = input.toUpperCase();

        //Check if string is all capital
        for (int i = 0; i < input.length(); i++)
            if (Character.isDigit(input.charAt(i)) || tmp.charAt(i) != input.charAt(i))
                return false;

        //Check if each character is in colors string
        int flag;

        for (int i = 0; i < input.length(); i++) {
            flag = 0;
            for (String color : MyServer.ar.get(this).colors) {
                if (color.equals(Character.toString(input.charAt(i)))) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                return false;
            }
        }
        return true;
    }

    public void stopThread() {
        stopThread = true;
    }
}

