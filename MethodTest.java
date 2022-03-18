public class MethodTest {
    String[] array = new String[] {"hello", "faint", "steal"};
  public MethodTest(){

  }  
  private boolean contains(String wrd, char[] c){
        
    for (int i = 0; i < c.length; i++){
       
        if (c[i] != '0'){          
         if (!wrd.contains(""+c[i])) return false;  
        }       
    }
    return true;
}
  public static void main(String[] args) {
      new MethodTest();
  }
}
