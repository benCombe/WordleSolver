import BasicIO.*;

public class Solver {

    private static final int LENGTH = 5;

    BasicForm form;
    ASCIIDataFile file;
    ASCIIOutputFile log;

    char[] elim, mustCont, fixed;
    Node List, alph;

    int guessCount = 1;

    String startWord = "adieu";
    String result, currWord;

    boolean wordFound = false;
    boolean reset = true;



    public Solver(){

        log = new ASCIIOutputFile("debugLog.txt");

        file = new ASCIIDataFile("words.txt");
          
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

    }

    private void Solve(){
        currWord = startWord;       

        while (true){
            if (!wordFound)                
                form.writeString("out", guessCount +": " + currWord);               
            
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
                        guessCount++;  
                    }

                break;

                case 1: //Reset
                    guessCount = 1;
                    wordFound = false;
                    form.clearAll();
                    load();
                    return;
                

                case 2: //Exit
                    reset = false;
                    return;
                

            }
            
            
            
        
        }
        
    }

    private Node Filter(Node p){ //This needs to be fixed
        Node result = null;      

        while (p!=null){
            if (!containsAny(p.item, elim)){
                addLog(p.item + " !Contain " + new String(elim));
                if (containsAll(p.item, mustCont)){
                    addLog(p.item + " Contains " + new String(mustCont));
                    if (mismatch(p.item, mustCont)){
                        addLog(p.item + " does not match place of " + new String(mustCont));
                        if(matchPlace(p.item, fixed)) {
                            addLog(p.item + " Has Matched Char " + new String(fixed));
                            result = new Node(p.item, result); 
                            addLog(p.item + " added to result!");                       
                        }
                    }
                }
            }
            p = p.next;
        }
        return result;

    }

    private void resetFilters(){
        for (int i = 0; i < LENGTH; i++){
            elim[i] = '0';
            mustCont[i] = '0';
            fixed[i] = '0';
        }
    }

    private void setFilters(String in){
        resetFilters();
        char[] let = in.toCharArray();
        char R;
       
        for (int i = 0; i < in.length(); i++){
            R = let[i];
            if (R == '2') fixed[i] = currWord.charAt(i);
            else if (R == '1') mustCont[i] = currWord.charAt(i);
            else elim[i] = currWord.charAt(i);
        }
    }

    private boolean mismatch(String wrd, char[] c){
        for (int i = 0; i < c.length; i++){
            if (c[i] != '0'){
                if (c[i] == wrd.charAt(i)) return false;
            }
        }
        return true;
    }

    private boolean matchPlace(String wrd, char[] c){
        
        for (int i = 0; i < c.length; i++){
            addLog(wrd + ": " + c[i]);
            if (c[i] != '0'){
                if (c[i] != wrd.charAt(i)) return false;
            }
        }
        return true;
    }

    private boolean containsAll(String wrd, char[] c){
        int count = 0;
        int n = 0;
        for (int i = 0; i < c.length; i++){
            addLog(wrd + ": " + c[i]);
            if (c[i] != '0'){
                n++;  

                if (wrd.contains(""+c[i])) 
                    count++;  
            }       
        }
        return (count == n);
    }

    private boolean containsAny(String wrd, char[] c){
        for (int i = 0; i < c.length; i++){
            if (c[i] != '0'){
                if (wrd.contains(""+c[i])) return true;
            }
        }
        return false;
    }

    private void print(Node p){
        if (p!=null){
            System.out.println(p.item);
            print(p.next);            
        }
    }

    private void load(){
        String word;
        while (true){
            word = file.readLine();
            if (file.isEOF()) break;
            List = new Node(word,List);
        }
    }

    private void buildForm(){
        form = new BasicForm("Enter", "Reset", "Exit");        
        form.addTextArea("out");
        form.setEditable("out", false);
        form.addTextField("in", "Enter: ", 15);
        form.show();
    }

    private void addLog(String s){
        log.writeLine(s);
    }

    public static void main(String[] args) {
        new Solver();
    }
}