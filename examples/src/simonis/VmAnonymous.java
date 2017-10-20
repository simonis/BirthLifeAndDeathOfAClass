package simonis;

import java.io.IOException;

public class VmAnonymous {
  
  public static void main(String... args) throws IOException {
    Runnable r = () -> {
      new Exception("Hello World!") . printStackTrace();
    };
    TopLevel.printClassAttrs(r.getClass());
    r.run();
    System.out.println("<RETURN>");
    System.in.read();
  }
}
