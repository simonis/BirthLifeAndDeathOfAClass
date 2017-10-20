package simonis;

import java.lang.reflect.InvocationTargetException;

/*
import simonis.Local$1Runnable;

src/simonis/Local.java:3: error: cannot access Local$1Runnable
import simonis.Local$1Runnable;
              ^
  bad class file: /tmp/yyy/simonis/Local$1Runnable.class
    bad enclosing method attribute for class Local$1Runnable
    Please remove or make sure it appears in the correct subdirectory of the classpath.
1 error

*/

public class LocalTest {
  
  void baz() {
    Runnable r = () -> {
      class Runnable {
        Runnable() {
          new Exception().printStackTrace();
        }
      }
      Runnable rr = new Runnable();
      TopLevel.printClassAttrs(rr.getClass());    
      new Exception().printStackTrace();
    };
    r.run();
  }

  void bar() {
    class Runnable {
      Runnable() {
        new Exception().printStackTrace();
      }
    }
    Runnable r = new Runnable();
    TopLevel.printClassAttrs(r.getClass());    
  }
  
  void foo() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException {
    System.out.println(Class.forName("simonis.Local$3Runnable").getDeclaredConstructors()[0].newInstance());    
    System.out.println(Class.forName("simonis.Local$2Runnable").getDeclaredConstructors()[0].newInstance(this));    
  }
  
  public static void main(String... args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
    class Runnable {
      Runnable() {
        new Exception().printStackTrace();
      }
    }
    Runnable r = new Runnable();
    TopLevel.printClassAttrs(r.getClass());
    new LocalTest() . baz();
    new LocalTest() . bar();
    new LocalTest() . foo();
  }
}
