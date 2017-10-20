package simonis;

public class Inner {
  class InnerClass {
    public InnerClass() {}
  }
  public static void main(String... args) {
    InnerClass ic = new Inner() . new InnerClass();
    TopLevel.printClassAttrs(ic.getClass());
  }
}
