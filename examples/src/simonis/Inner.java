package simonis;

public class Inner {
  class InnerClass {
    private InnerClass() {
      new Exception().printStackTrace();
    }
  }
  class PrivateInnerClass {
    private /* Not accessible! */ PrivateInnerClass() {
      new Exception().printStackTrace();
    }
  }
  public static void main(String... args) throws ClassNotFoundException {
    InnerClass ic = new Inner() . new InnerClass();
    Nested.printClassAttrs(ic.getClass());
    PrivateInnerClass pic = new Inner() . new PrivateInnerClass();
    Nested.printClassAttrs(pic.getClass());
    Nested.printClassAttrs(Class.forName("simonis.Inner$1")); // Doesn't work with ECJ
  }
}
