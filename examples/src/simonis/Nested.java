package simonis;

public class Nested {
  static class NestedClass {
    public NestedClass() {}
  }
  static class PrivateNestedClass {
    public int i;
    private PrivateNestedClass() { i = 1;} // Not accessible!
    private PrivateNestedClass(PrivateNestedClass pnc) { i = 2; } // Not accessible!
    //PrivateNestedClass(PrivateNestedClass pnc, PrivateNestedClass pnc2) { i = 3; } // Not accessible!
  }
  public static void main(String... args) {
    NestedClass nc = new NestedClass();
    TopLevel.printClassAttrs(nc.getClass());
    PrivateNestedClass pnc = new PrivateNestedClass();
    try {
      TopLevel.printClassAttrs(Class.forName("simonis.Nested$1"));
    }
    catch (Exception e) {
      System.out.println("Compiled by ECJ");
    }
    System.out.println(pnc.i);
    pnc = new PrivateNestedClass(pnc);
    System.out.println(pnc.i);
  }
}
