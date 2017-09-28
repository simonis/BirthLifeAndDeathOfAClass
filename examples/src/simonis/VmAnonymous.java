package simonis;

public class VmAnonymous {
  
  public void foo() {
    Runnable r = () -> {
      throw new Error("Hello " + this.getClass().getName());
    };
    r.run();
  }
  public static void main(String... args) {
    Runnable r = () -> {
      throw new Error("Hello World!");
    };
    Class<?> anonymous = r.getClass();
    System.out.println("Name:             " + anonymous.getName());
    System.out.println("Package name:     " + anonymous.getPackageName());
    System.out.println("Local:            " + anonymous.isLocalClass());
    System.out.println("Anonymous:        " + anonymous.isAnonymousClass());
    System.out.println("Member:           " + anonymous.isMemberClass());
    System.out.println("Synthetic:        " + anonymous.isSynthetic());
    System.out.println("Declaring class:  " + anonymous.getDeclaringClass());
    System.out.println("Enclosing class:  " + anonymous.getEnclosingClass());
    System.out.println("Enclosing method: " + anonymous.getEnclosingMethod());
    new VmAnonymous() . foo();
    r.run();
  }
}
