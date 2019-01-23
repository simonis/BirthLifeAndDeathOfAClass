package simonis;

public class Inner {
  class InnerClass {
    private InnerClass() {}
  }
  public static void main(String... args) {
    InnerClass ic = new Inner() . new InnerClass();
    TopLevel.printClassAttrs(ic.getClass());
    class InnerClass {
      public InnerClass() {}
    }
    InnerClass ic2 = new InnerClass();
    TopLevel.printClassAttrs(ic2.getClass());
  }
}
