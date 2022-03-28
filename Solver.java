import BasicIO.*;

/**
 * @author      Ben Combe
 * @version     a1.3.3
 * @date        March 24th, 2022
 */

public class Solver {

    private static final int LENGTH = 5; //word length

    BasicForm form;
    ASCIIDisplayer reportDisplay;
    ASCIIPrompter prompt;
    ASCIIDataFile file;
    
    char[] elim, mustCont, fixed;
    Node List, alph;

    int guessCount = 0;

    //testing var
    String[] testWords;
    int[] numsOfGuess;
    int successCount = 0;

    String longestGuessWord;
    int longestGuess;

    String startWord = "adieu";
    String result, currWord;

    boolean wordFound = false;
    boolean reset = true;



    public Solver(){        
            
        prompt = new ASCIIPrompter();

        elim = new char[LENGTH];
        mustCont = new char[LENGTH];
        fixed = new char[LENGTH];

        buildForm();
        load();
        print(List);
        while (reset){
            Solve();
        }        
        form.close();
        prompt.close();

    }

    //program run method
    private void Solve(){
        currWord = startWord;       

        while (true){
            if (!wordFound) {
                guessCount++;               
                form.writeString("out", guessCount +": " + currWord); 
            }              
            
            form.clear("in");
            int button = form.accept();
            switch(button){
                case 0: //Enter
                    result = form.readString("in");
                    form.writeString("out","  " + result);

                    if (result.equals("22222")){
                        form.newLine("out");
                        form.writeString("out","WORD FOUND! '" + currWord + "' in " + guessCount + " guesses!");
                        wordFound = true;
                    }
                    else{                        
                        form.newLine("out");

                        setFilters(result);
                        List = Filter(List);
                        currWord = List.item;
                        print(List);
                        System.out.println("-------------------");
                          
                    }

                break;

                case 1: //Reset
                    form.writeString("status", "Solving");
                    reset();
                    return;
                
                case 2: //set start
                    String input = prompt.readString();
                    System.out.println(inList(input, List)); //BUG: to be inplemented later 'inList' always returns false..
                    if (input.length() == 5){
                        startWord = input;
                        reset(); 
                    }
                    else{
                        form.clear("out");
                    }
                    
                            
                                      
                return;


                case 3: //Test                
                    
                    Test(prompt.readInt());
            
                break;
                

                case 4: //Exit
                    reset = false;
                    return;               

            }           
  
        }
        
    }

    private void Test(int n){         
        
        String fC;
        testWords = new String[n];
        numsOfGuess = new int[n];

        currWord = startWord;
        form.clear("out");
        
        //new word, test
        for (int i = 0; i < n; i++){ 
            form.writeString("status", "Testing " + (i+1) + "/" + n);
            testWords[i] = searchList(List,  randomInt(1, listLength(List))); //new word
            form.writeLine("Testing... '" + testWords[i] + "'");
            form.writeString("out", guessCount +": " + currWord); 
            
            test:
            while(true){

                fC = filterCode(testWords[i], currWord);
                form.writeString("out",fC);
                guessCount++; 

                if (currWord == testWords[i]) {
                    numsOfGuess[i] = guessCount;
                    if (numsOfGuess[i] <= 6) successCount++;
                    if (i == 0){
                        longestGuessWord = testWords[i];
                        longestGuess = numsOfGuess[i];
                    }
                    else if (i != 0 && numsOfGuess[i] > longestGuess){
                        longestGuessWord = testWords[i];
                        longestGuess = numsOfGuess[i];
                    }
                    form.newLine("out");
                    form.writeString("out","WORD FOUND! '" + currWord + "' in " + guessCount + " guesses!");
                    break test;
                }
                else{
                    setFilters(fC);
                    System.out.println(fC);
                    List = Filter(List);

                    currWord = List.item;
                    

                    form.newLine("out");
                
                    print(List);
                    System.out.println("-------------------");
                }

            }              
            reset();
            currWord = startWord;
        }

        printReport(n);

        form.writeString("status", "Solving");
        successCount = 0;
        reset();
                
    }

    private void printReport(int n){
        reportDisplay = new ASCIIDisplayer();
        reportDisplay.setLabel("TEST REPORT");
        reportDisplay.writeString("START WORD: " + startWord);
        reportDisplay.newLine();
        reportDisplay.writeString("   WORD");
        reportDisplay.writeString("# of Guesses");
        reportDisplay.newLine();
        
        for (int i = 0; i < n; i++){
            //spacing
            if (i <= 8){
                reportDisplay.writeString(" "+(i+1)+": " + testWords[i] + " - ");
            }
            else{
                reportDisplay.writeString((i+1)+": " + testWords[i] + " - ");
            }
            
            reportDisplay.writeInt(numsOfGuess[i]);
            if (numsOfGuess[i] > 6){
                reportDisplay.writeChar('X');
            }
            reportDisplay.newLine();
        }
        reportDisplay.writeString("Longest Guess: " + longestGuessWord);
        reportDisplay.writeString("("+longestGuess+")");
        reportDisplay.newLine();
        reportDisplay.writeString("Average: ");
        reportDisplay.writeDouble((double)sumOfInt(numsOfGuess)/n);
        reportDisplay.newLine();
        reportDisplay.writeString("Success Rate: ");
        reportDisplay.writeDouble((double)successCount/n);
        reportDisplay.close();
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

    private int sumOfInt(int[] n){
        int result = 0;
        for (int i: n){
            result = result + i;
        }
        return result;
    }

    //resets list to default
    //resets program start points
    private void reset(){
        guessCount = 0;
        wordFound = false;
        form.clear("out");
        form.clear("in");
        List = null;
        load();
        print(List);
    }

    //goes through each word in passed list, adds words that pass through filters to 'result' list
    //returns new list with words that went through filters 
    private Node Filter(Node p){ //This needs to be fixed
        Node result = null;      

        while (p!=null){
            if (!containsAny(p.item, elim)){
                
                if (containsAll(p.item, mustCont)){
                    
                    if (mismatch(p.item, mustCont)){
                        
                        if(matchPlace(p.item, fixed)) {
                            
                            result = new Node(p.item, result); 
                                                   
                        }
                    }
                }
            }
            p = p.next;
        }
        return result;

    }

    //fills all char arrays with '0'
    private void resetFilters(){
        for (int i = 0; i < LENGTH; i++){
            elim[i] = '0';
            mustCont[i] = '0';
            fixed[i] = '0';
        }
    }

    //sets the char arrays based on String input of 2's, 1's, & 0's
    //if in[i] = '2', added to "fixed"
    //else if = '1', added to "mustCont"
    //else added to "elim"
    private void setFilters(String in){
        resetFilters();
        char[] let = in.toCharArray();       
       
        for (int i = 0; i < in.length(); i++){            
            if (let[i] == '2') fixed[i] = currWord.charAt(i);
            else if (let[i] == '1') mustCont[i] = currWord.charAt(i);
            else elim[i] = currWord.charAt(i);
        }
    }

    //checks contents of String 'wrd'
    //returns true if none of the chars in 'wrd' match the same index of same chars in 'c'
    private boolean mismatch(String wrd, char[] c){
        for (int i = 0; i < c.length; i++){
            if (c[i] != '0'){
                if (c[i] == wrd.charAt(i)) return false;
            }
        }
        return true;
    }

    //checks contents of String 'wrd'
    //returns true if all chars in 'wrd' match the same index of same chars in'c'
    private boolean matchPlace(String wrd, char[] c){        
        for (int i = 0; i < c.length; i++){
            
            if (c[i] != '0'){
                if (c[i] != wrd.charAt(i)) return false;
            }
        }
        return true;
    }

    //checks contents of String 'wrd'
    //returns true if all chars in 'c' are in 'wrd' 
    private boolean containsAll(String wrd, char[] c){
        int count = 0;
        int n = 0;
        for (int i = 0; i < c.length; i++){
            
            if (c[i] != '0'){
                n++;  

                if (wrd.contains(""+c[i])) 
                    count++;  
            }       
        }
        return (count == n);
    }

    //checks contents of String 'wrd'
    //returns true if any chars in 'c' are in 'wrd'
    private boolean containsAny(String wrd, char[] c){
        for (int i = 0; i < c.length; i++){
            if (c[i] != '0' /*&& !inCharArray(c[i], mustCont)*/){ //TODO: test to make sure it doesn't break the program
                if (wrd.contains(""+c[i])) return true;
            }
        }
        return false;
    }

    //returns true if array contains char c
    private boolean inCharArray(char c, char[] array){
        for (char ch : array) {
            if (ch == c) return true;
        }
        return false;
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

     //returns true if String 's' is in list 'p'
     private boolean inList(String s, Node p){

        while (p!=null){
            if (p.item == s) {return true;}            
            p = p.next;
        }
        return false;
     }
 
     public int listLength(Node p){
         if (p!=null){
             return 1 + listLength(p.next);
         }
         else return 0;
     }  

    //prints list from passed Node to console
    private void print(Node p){
        if (p!=null){
            System.out.println(p.item);
            print(p.next);            
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

    //sets up IO form
    private void buildForm(){
        form = new BasicForm("Enter", "Reset", "Set Start", "Test", "Exit");  
        form.setTitle("WordleSolver");      
        form.addTextField("status");
        form.writeString("status", "Solving");
        form.setEditable("status", false);
        form.addTextArea("out", 10, 40);
        form.setEditable("out", false);
        form.addTextField("in", "Enter: ", 15);
        form.show();
    }

    private int randomInt(int min, int max){
        return (int)((max-min)*Math.random())+min;
    }

    public static void main(String[] args) {
        new Solver();
    }
}