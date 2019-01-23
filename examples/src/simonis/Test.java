package simonis;

import java.util.Arrays;

public class Test extends Nested {
  public static void foo$foo() {}
  public static void main(String[] args) {
    String.class.getDeclaredMethods();
    System.out.println(Test.class.getName());
    System.out.println(Test.class.getClass().getName());
    System.out.println(Test.class.getClass().getClass().getName());
    System.out.println(Test.class);
    System.out.println(Test.class.getClass());
    System.out.println(Test.class.getClass().getClass());
    Nested.PrivateNestedClass n = null;
    foo$foo();
    Arrays.stream(PrivateNestedClass.class.getDeclaredConstructors()).forEach(c -> System.out.println(c));
    //Nested.PrivateNestedClass pnc = new Nested.PrivateNestedClass(n);
    //System.out.println(pnc.i);
  }
}
