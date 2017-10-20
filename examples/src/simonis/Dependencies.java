package simonis;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/*
 * /share/output-jdk9-dev-dbg/images/jdk/bin/java -cp /tmp/javac/ -agentpath:bin/traceClassAgent.so=simonis -Xlog:class*=trace,load*=trace,load*=trace,loader*=trace simonis.Dependencies file:///tmp/b/ file:///tmp/a/ | grep class,loader,constraints
 * 
 */
public class Dependencies {
  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException {
    ClassLoader cl1 = new URLClassLoader(new URL[] { new URL(args[0]) }, null);
    ClassLoader cl2 = new URLClassLoader(new URL[] { new URL(args[1]) }, cl1) {
      protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if ("simonis.Dependencies$X".equals(name)) {
          Class<?> c = findClass(name);
          if (resolve) {
            resolveClass(c);
          }
          return c;
        }
        else {
          return super.loadClass(name, resolve);
        }
      }
    };
    Class<?> a_class = cl2.loadClass("simonis.Dependencies$A");
    Object a = a_class.newInstance();
  }

  static public class A {
    public A() {
      X x = B.getX();
      System.out.println(this.getClass().getClassLoader());
      System.out.println(B.class.getClassLoader());
      System.out.println(x.getClass().getClassLoader());
      new X();
    }
  }

  static public class B {
    public static X getX() { return new X(); }
  }

  public static class X {}
}
