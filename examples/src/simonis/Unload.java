package simonis;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

/*

/share/output-jdk9-dev-dbg/images/jdk/bin/java -cp bin/ -Xlog:class*=trace,load*=trace,loader*=trace simonis.Unload | grep simonis

 */
public class Unload {
  static Object keepAlive;
  public static class X {
  }
  public static class Y {
    @Override
    protected void finalize() throws Throwable {
      keepAlive = this;
    }
  }
  public static class Z {
    public static void run() throws InterruptedException {
      Thread.sleep(3_000);
      System.out.println("simonis: exiting Z::run()");
    }
  }
  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    ClassLoader cl = new URLClassLoader(new URL[] { Unload.class.getResource("..") }, null);
    Object o = cl.loadClass("simonis.Unload$X").newInstance();
    o = null;
    cl = null;
    systemGC();
    
    cl = new URLClassLoader(new URL[] { Unload.class.getResource("..") }, null);
    o = cl.loadClass("simonis.Unload$Y").newInstance();
    o = null;
    cl = null;
    systemGC();
    // System.out.println(keepAlive);
    // keepAlive = null;
    systemGC();  

    cl = new URLClassLoader(new URL[] { Unload.class.getResource("..") }, null);
    Class<?> c = cl.loadClass("simonis.Unload$Z");
    new MyThread(c).start();
    c = null;
    cl = null;
    systemGC();
    systemGC();  

  }
  static void systemGC() throws IOException {
    System.out.println("simonis: -> Calling System.gc() ...");
    System.gc();
    System.out.println("simonis: <- System.gc() done");
    System.in.read();
  }
  static class MyThread extends Thread {
    Class<?> receiver;
    public MyThread(Class<?> c) {
      receiver = c;
    }
    public void run() {
      try {
        receiver.getDeclaredMethod("run").invoke(null);
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
    }    
  }
}
