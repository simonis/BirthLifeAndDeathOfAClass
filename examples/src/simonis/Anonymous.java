package simonis;

public class Anonymous {
  
  public static void main(String... args) {
    Runnable r = new Runnable() {
      public void run() {
        new Exception().printStackTrace();
      }
    };
    r.run();
    Nested.printClassAttrs(r.getClass());
  }
}
