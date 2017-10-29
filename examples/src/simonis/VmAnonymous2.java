package simonis;

import java.io.IOException;
import java.lang.StackWalker.Option;

public class VmAnonymous2 {
  
  public static void main(String... args) throws IOException {
    Runnable r = () -> {
      StackWalker sw = StackWalker.getInstance(Option.SHOW_HIDDEN_FRAMES);
      sw.forEach(System.out::println);
    };
    r.run();
    System.out.println("<RETURN>");
    System.in.read();
  }
}
