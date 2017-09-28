package simonis;

public class Nested {
  static class NestedClass {
    private NestedClass() {
      new Exception().printStackTrace();
    }
  }
  public static void main(String... args) throws ClassNotFoundException {
    NestedClass nc = new NestedClass();
    printClassAttrs(nc.getClass());
  }

  public static void printClassAttrs(Class<?> anonymous) {
    System.out.println("Name:             " + anonymous.getName());
    System.out.println("Package name:     " + anonymous.getPackageName());
    System.out.println("Local:            " + anonymous.isLocalClass());
    System.out.println("Anonymous:        " + anonymous.isAnonymousClass());
    System.out.println("Member:           " + anonymous.isMemberClass());
    System.out.println("Synthetic:        " + anonymous.isSynthetic());
    System.out.println("Declaring class:  " + anonymous.getDeclaringClass());
    System.out.println("Enclosing class:  " + anonymous.getEnclosingClass());
    System.out.println("Enclosing method: " + anonymous.getEnclosingMethod());
  }
}
