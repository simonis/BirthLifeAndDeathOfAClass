package simonis;

import java.net.URL;
import java.net.URLClassLoader;

public class TwoLoaders {
  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    TwoLoaders tl1 = new TwoLoaders();
    System.out.println(TwoLoaders.class.getResource(".."));
    ClassLoader cl = new URLClassLoader(new URL[] { TwoLoaders.class.getResource("..") }, null);
    Object tl2 = cl.loadClass("simonis.TwoLoaders").newInstance();
    System.out.println(tl2.getClass().getName().equals(tl1.getClass().getName())); // true / false ?
    System.out.println(tl2 instanceof simonis.TwoLoaders);
    tl1 = (TwoLoaders)tl2;
  }
}
