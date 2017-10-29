package simonis;

public class VmAnonymous3 {
  
  public static void main(String... args) {
    Runnable r = () -> {
      new Exception("Hello World!") . printStackTrace();
      System.console().readLine();
    };
    r.run();
  }
}
