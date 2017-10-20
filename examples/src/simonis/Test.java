package simonis;

public class Test {
  public static void foo$foo() {}
  public static void main(String[] args) {
    Nested.PrivateNestedClass n = null;
    foo$foo();
    //Nested.PrivateNestedClass pnc = new Nested.PrivateNestedClass(n);
  }
}
