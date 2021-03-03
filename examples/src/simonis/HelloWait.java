package simonis;

public class HelloWait {

  public static void main(String args[]) throws Exception {
    System.out.println("HelloWorld");
    (new Object()).wait(-42, 0);
  }

}
