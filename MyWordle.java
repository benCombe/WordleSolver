import BasicIO.*;

//NOT COMPLETE
//WORDLE GAME

public class MyWordle {
    BasicForm form;
    ASCIIDataFile file;

    String theWord, guess, fC;
    int guessCount = 0;

    static final String[] defaultLetters = {"qwertyuiop","asdfghjkl","zxcvbnm"};
    String[] currLetters;

    boolean resetting = true;


    Node List;

    public MyWordle(){
        load();
        buildForm();
        while (resetting){
            playGame();
        }
        
        form.close();

    }

    private void playGame(){
        theWord = searchList(List,  randomInt(1, listLength(List)));
        currLetters = defaultLetters;

        while (true){
            int button = form.accept();
            switch(button){
                case 0: //Enter
                    guessCount++;
                    guess = form.readString("in");
                    form.writeString("out", guess);
                    form.newLine("out");
                    fC = filterCode(theWord, guess);
                    form.writeString("out", fC);
                    form.newLine("out");
                    form.clear("in");

                    if (fC.equals("22222")){
                        form.newLine("out");
                        form.writeString("CORRECT! You found the word in " + guessCount + " guesses!");
                    }                       
                    currLetters = removeLetters(fC, guess, currLetters);
                    fillLetters(currLetters);

                break;

                case 1: //New Game
                    reset();
                    currLetters = defaultLetters;
                return;

                case 2: //Exit
                resetting = false;
                return;
            }
            

        }
    }

    //resets list to default
    //resets program start points
    private void reset(){
        guessCount = 0;        
        form.clear("out");
        form.clear("in");
        fillLetters(defaultLetters);
        List = null;
        load();       
    }

    private String filterCode(String wrd, String guess){
        char[] result = new char[guess.length()];

        for (int i = 0; i < guess.length(); i++){
            if (wrd.charAt(i) == guess.charAt(i)){
                result[i] = '2';
            }
            else if (wrd.contains(""+guess.charAt(i))){
                result[i] = '1';
            }
            else{
                result[i] = '0';
            }
        }
        
        return new String(result);
    }

    public int listLength(Node p){
        if (p!=null){
            return 1 + listLength(p.next);
        }
        else return 0;
    }  

    //returns String of item in list by input key
    private String searchList(Node p, int key){
        while (p != null && key > 0){
            p = p.next;
            key--;
        }
        if ( key > 0){
            return null;
        }
        else
         return p.item;
 
     }

     private String[] removeLetters(String fC, String guess, String[] letters){
         
       for (int i = 0; i < fC.length(); i++){
           if (fC.charAt(i) == '0'){
               for (int j = 0; j < 3; j++){
                   
                   for (int k = 0; k < letters[j].length(); k++){
                       if (letters[j].charAt(k) == guess.charAt(i)){
                            char[] temp = letters[j].toCharArray();
                            temp[k] = ' ';
                            letters[j] = new String(temp);
                        }
                    }
                }
            }
        }
       return letters;
     }
     private void fillLetters(String[] letters){
         form.clear("letR");
         for (String s : letters){
             System.out.println(s);
         }
         for (int i = 0; i < 3; i++){
             for (int j = 0; j < letters[i].length(); j++){
                 form.writeChar("letR", letters[i].charAt(j));
             }
             form.newLine("letR");
         }

     }

    //This method loads the words into a linked-list 'List' from words.txt
    private void load(){
        file = new ASCIIDataFile("words.txt");
        String word;
        while (true){
            word = file.readLine();
            if (file.isEOF()) break;
            List = new Node(word,List);
        }
        file.close();
    }

    private void buildForm(){
        form = new BasicForm("Enter", "New Game", "Exit");  
        form.setTitle("My Wordle");      
        form.addTextArea("out", 15, 25);
        form.setEditable("out", false);
        form.addTextField("in", "Guess", 15);
        form.addTextArea("letR", 5, 25);
        fillLetters(defaultLetters);
        form.show();
    }

    private int randomInt(int min, int max){
        return (int)((max-min)*Math.random())+min;
    }

    public static void main(String[] args) {
        new MyWordle();
    }
    
}
