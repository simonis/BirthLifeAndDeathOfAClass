package simonis;

public class CHA {

  static class A {
    void f() {}
  }

  static class B extends A {
    void f() {}
  }

  static void g(A a) {
    a.f();
  }

  public static void main (String[] args) {
    A a = new A();
    for (int i = 0; i < 20_000; i++) {
      g(a);
    }
    B b = new B();
    for (int i = 0; i < 20_000; i++) {
      g(a);
    }
  }
}
