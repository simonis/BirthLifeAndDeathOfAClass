package simonis;

public class Local {
  
  public static void main(String... args) {
    class LocalClass {
      LocalClass() {}
    }
    LocalClass l = new LocalClass();
    TopLevel.printClassAttrs(l.getClass());
  }
}
