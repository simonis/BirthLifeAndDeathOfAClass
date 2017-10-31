package simonis;

import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Instrument2 {
  static CountDownLatch stop = new CountDownLatch(1);
  
  public static class A {
    public static Runnable getRunnable() {
      return () -> {
        try { stop.await(); } catch (Exception e) {};
      };      
    }
  }
  public static void main(String[] args) throws UnmodifiableClassException, InterruptedException, IOException, NoSuchFieldException, SecurityException {
    Runnable r = A.getRunnable();
    for (int i = 0; i < 10; i++) {
      InstAgent.getInst().retransformClasses(Instrument2.A.class);
      new Thread(r).start();
    }
    System.out.println(
        Arrays.asList(InstAgent.getInst().getAllLoadedClasses())
          .stream()
          .map(c -> c.getName())
          .filter(n -> n.equals("simonis.Instrument2$A"))
          .count());
    System.in.read();
    stop.countDown();
    System.gc();
  }
}
