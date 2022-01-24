import java.util.Scanner;

/**
 * The class Gameboard is to print control the flow of the game and
 * contains a set of players and characters.
 * Name: Cheung King Hung ID: 21237379 Session: 000002
 */
public class Gameboard {

    /**
     * The maximum number of characters can be placed in the same position.
     */
    public static final int FULL = 4;
    /**
     * The total number of characters
     */
    public static final int NO_OF_CHARACTER = 13;
    /**
     * The total number of player
     */
    public static final int NO_OF_PLAYERS = 4;
    /**
     * The position of Throne
     */
    public static final int THRONE = 6;
    /**
     * The scores calculation formula
     */
    public static final int[] SCORES = {0, 1, 2, 3, 4, 5, 10};
    /**
     * The name of the characters
     */
    public static final String[] CHARACTER_NAMES = {
            "Aligliero", "Beatrice", "Clemence", "Dario",
            "Ernesto", "Forello", "Gavino", "Irima",
            "Leonardo", "Merlino", "Natale", "Odessa", "Piero"
    };
    /**
     * The name of the players
     */
    public static final String[] PLAYER_NAMES = {
        "You", "Computer 1", "Computer 2", "Computer 3"
    };
    /**
     * Determine if the players are human player or not.
     */
    public static final boolean[] HUMAN_PLAYERS = {
        true, false, false, false
    };
    /**
     * A list of character
     */
    private Character[] characters;
    /**
     * A list of player
     */
    private Player[] players;


    public static void main(String[] argv) {
        new Gameboard().runOnce();
    }

    /**
     * Initialize all data attributes. You will need to initialize and create
     * the array players and characters. You should initialize them using the
     * String array PLAYER_NAMES and CHARACTER_NAMES respectively.
     */
    public Gameboard() {
        //TODO
        characters = new Character[NO_OF_CHARACTER];
        for (int i = 0; i < NO_OF_CHARACTER; i++) {
            characters[i] = new Character(CHARACTER_NAMES[i]);
            characters[i].setPosition(Character.OUT_OF_GAME);

        }
        players = new Player[NO_OF_PLAYERS];
        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            players[i] = new Player(PLAYER_NAMES[i],characters);
            players[i].initVetoCard(3);
        }
    }

    /**
     * The main logic of the game. This part has been done for you.
     */
    public void runOnce() {

        print();
        System.out.println("======= Placing stage ======= \n"
                + "Each player will take turns to place three characters on the board.\n"
                + "No character can be placed in the position 0 or 5 or 6 (Throne) at this stage.\n"
                + "A position is FULL when there are four characters placed there already.\n"
                + "The remaining character will be placed at the position 0.\n");

        placingStage();

        print();
        System.out.println("======= Playing stage ======= \n"
                + "Each player will take turn to move a character UP the board.\n"
                + "You cannot move a character that is been killed or its immediate upper position is full.\n"
                + "A voting will be trigger immediately when a character is moved to the Throne (position 6).");

        playingStage();

        print();
        System.out.println("======= Scoring stage ======= \n"
                + "This will trigger if and only if the voting result is ALL positive, i.e., no player play the veto (reject) card. \n"
                + "The score of each player is computed by the secret list of characters owned by each player.");

        scoringStage();
    }


    /**
     * Print the scores of all players correctly. This part has been done
     * for you.
     */
    private void scoringStage() {
        for (Player p : players) {
            System.out.println(p);
            System.out.println("Score: " + p.getScore());
        }
    }

    /**
     * Perform the placing stage. You have to be careful that human player will need to chosen on what to place
     * Non-human player will need to place it using the method placeRandomly (see Player.java)
     */
    private void placingStage() {
        //TODO
        //loop until 12 of the characters have been placed 
        //and place the last character at position 0
        Scanner sc = new Scanner(System.in);
        int[] countFloor = new int[THRONE +1];
        for (int i = 0; i < NO_OF_CHARACTER-1; i++) {
            int j = i % NO_OF_PLAYERS;
            print();
            System.out.println(players[j].toString() + ", this is your turn to place a character");
            if (HUMAN_PLAYERS[j]){
                Character selectedCharacter = null;
                boolean isOK = false;
                pickingCharacter:
                do {
                    System.out.println("Please pick a character");
                    String chara = sc.next();
                    for (Character c: characters) {
                        if (c.getName().equals(chara)){
                            selectedCharacter = c;
                            if (selectedCharacter.getPosition()!=Character.OUT_OF_GAME){
                                continue pickingCharacter;
                            }else {
                                isOK = true;
                            }
                        }
                    }
                }while (!isOK);
                isOK = false;
                do {
                    System.out.println("Please enter the floor you want to place " + selectedCharacter.getName());
                    int selectedFloor = sc.nextInt();
                    if (selectedFloor >= 1 && selectedFloor <= 4 && countFloor[selectedFloor] < FULL){
                        boolean isSet = selectedCharacter.setPosition(selectedFloor);
                        if (isSet){
                            countFloor[selectedFloor]++;
                            isOK = true;;
                        }
                    }
                }while (!isOK);
            }else {//computer
                Character randomCharacter = players[j].placeRandomly(characters);
                countFloor[randomCharacter.getPosition()]++;
            }
        }
        for (Character c: characters) {
            if (c.getPosition() == Character.OUT_OF_GAME) {
                c.setPosition(0);
            }
        }
        System.out.println("End placing");
    }

    /**
     * Perform playing stage. Be careful that human player will need to pick the character to move.
     * You should detect any invalid input and stop human player from doing something nonsense or illegal.
     * Computer players will need to run the code pickCharToMoveRandomly or pickCharToMoveSmartly to pick which character to move.
     */
    private void playingStage() {
        //TODO
        //loop until a character has been voted for the new King.
        Scanner sc = new Scanner(System.in);
        int i = 0;
        while (true){
            int j = i % NO_OF_PLAYERS;
            print();
            System.out.println(players[j].toString() + "\nThis is your turn to move a character up");
            Character selectedCharacter = null;
            if (HUMAN_PLAYERS[j]){
                do {
                    System.out.println("Please type the character that you want to move.");
                    selectedCharacter = players[j].pickCharToMove(characters, sc.next());
                }while (selectedCharacter == null);
            }else {
                selectedCharacter = players[j].pickCharToMoveSmartly(characters);
            }selectedCharacter.setPosition(selectedCharacter.getPosition()+1);

            if (selectedCharacter.getPosition()==THRONE){//vote
                print();
                int voteCount = 0;
                for (int k = 0; k < players.length; k++) {
                    if (HUMAN_PLAYERS[k]){
                        System.out.println("Please vote. Type V for veto. Other for accept");
                        boolean b = sc.next().equalsIgnoreCase("V");
                        voteCount += !players[k].vote(!b)?1:0;
                    }else {
                        voteCount += !players[k].voteSmartly(selectedCharacter)?1:0;
                    }
                }
                if (voteCount == 0){
                    break;
                }else {
                    selectedCharacter.setPosition(Character.OUT_OF_GAME);
                }
            }
            i++;
        }
    }

    /**
     * Print the gameboard. Please see the assignment webpage or the demo program for
     * the format. You should call this method after a character has been moved or placed or killed.
     */
    private void print() {
        //TODO
        String str = "";
        for (int i = Gameboard.THRONE; i >= 0; i--) {
            str += String.format("Level %d:\t", i);
            for (Character c:characters) {
                if (c.getPosition() == i){
                    str += c.getName()+"\t";
                }
            }
            str +="\n";
        }
        str +="Unplaced/Killed Characters\n";
        for (Character c:characters) {
            if (c.getPosition()==Character.OUT_OF_GAME){
                str += c.toString()+"\t";
            }
        }
        str +="\n";
        System.out.println(str);
    }
}
