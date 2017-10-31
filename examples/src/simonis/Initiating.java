package simonis;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class Initiating {

  public static void main(String[] args) throws ClassNotFoundException, IOException {
    ClassLoader cl1 = new URLClassLoader("cl1", new URL[] { TwoLoaders.class.getResource("..") }, null);
    ClassLoader cl2 = new URLClassLoader("cl2", new URL[] { TwoLoaders.class.getResource("..") }, cl1) {
      protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if ("simonis.Initiating$A".equals(name)) {
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
    ClassLoader cl3 = new URLClassLoader("cl3", new URL[] { TwoLoaders.class.getResource("..") }, cl2);

    Class<?> cA = cl3.loadClass("simonis.Initiating$A");
    System.out.println("Defining = " + cA.getClassLoader().getName());

    Instrumentation inst = InstAgent.inst;
    System.out.println(cl1.getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(cl1)).forEach(System.out::println);
    System.out.println(cl2.getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(cl2)).forEach(System.out::println);
    System.out.println(cl3.getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(cl3)).forEach(System.out::println);
  }
  
  public static class A {}
}
