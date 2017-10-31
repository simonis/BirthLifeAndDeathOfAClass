package simonis;

public class Instrument {
  
  public static void main(String[] args) {
    Runnable r = () -> {
      new Exception("Hello World!") . printStackTrace();
    };      
    r.run();
  }
}
