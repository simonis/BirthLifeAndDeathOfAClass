package simonis;

import java.io.IOException;
import java.util.function.Consumer;

public class VmAnonymous4 {
  String name;
  
  public VmAnonymous4(String name) {
    this.name = name;
  }
  
  public Consumer<String> foo(String test) {
    return (s) -> {
      System.out.println(name + " " + test + s);
    };
  }
  
  public static void main(String... args) throws IOException {
    VmAnonymous4 a4 = new VmAnonymous4("Hello");
    System.out.println("<RETURN>");
    System.in.read();
    Consumer<String> con = a4.foo("world");
    con.accept("!");
    con = a4.foo("Joker");
    System.out.println("<RETURN>");
    System.in.read();
  }
}
