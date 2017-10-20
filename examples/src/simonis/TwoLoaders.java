package simonis;

import java.net.URL;
import java.net.URLClassLoader;

public class TwoLoaders {
  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    System.out.println(TwoLoaders.class.getResource(".."));
    ClassLoader cl = new URLClassLoader(new URL[] { TwoLoaders.class.getResource("..") }, null);
    TwoLoaders tl1 = new TwoLoaders();
    Object o = cl.loadClass("simonis.TwoLoaders").newInstance();
    System.out.println(o.getClass().getName().equals(tl1.getClass().getName())); // true / false ?
    System.out.println(o instanceof TwoLoaders);
    tl1 = (TwoLoaders)o;
  }
}
