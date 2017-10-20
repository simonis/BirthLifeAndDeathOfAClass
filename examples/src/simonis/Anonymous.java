package simonis;

public class Anonymous {
  
  public static void main(String... args) {
    Runnable r = new Runnable() {
      public void run() {}
    };
    TopLevel.printClassAttrs(r.getClass());
  }
}
