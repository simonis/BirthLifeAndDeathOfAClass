package simonis;

public class Test {
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
    //Nested.PrivateNestedClass pnc = new Nested.PrivateNestedClass(n);
  }
}
