package simonis;

public class Nested {
  static class NestedClass {
    public NestedClass() {}
  }
  static class PrivateNestedClass {
    private PrivateNestedClass() {} // Not accessible!
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
  }
}
