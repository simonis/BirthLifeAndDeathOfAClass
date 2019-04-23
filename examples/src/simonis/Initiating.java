package simonis;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class Initiating {

  static class MyClassLoader extends URLClassLoader {
    public MyClassLoader(String name, URL[] urls, ClassLoader parent) {
      super(name, urls, parent);
    }
    public Class<?> myLoadClass(String name, boolean resolve) throws ClassNotFoundException {
      return loadClass(name, resolve);
    }
    public boolean isInitiating(String name) {
      return findLoadedClass(name) != null;
    }
  }

  public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
    MyClassLoader cl1 = new MyClassLoader("cl1", new URL[] { Initiating.class.getResource("..") }, null);
    MyClassLoader cl2 = new MyClassLoader("cl2", new URL[] { Initiating.class.getResource("..") }, cl1) {
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
    MyClassLoader cl3 = new MyClassLoader("cl3", new URL[] { Initiating.class.getResource("..") }, cl2);
    MyClassLoader cl4 = new MyClassLoader("cl4", new URL[] { Initiating.class.getResource("..") }, cl3);

    // Class<?> cA = cl4.myLoadClass("simonis.Initiating$A", true);  // Won't make 'cl4' an "initiating" class loader of "A"
    Class<?> cA = Class.forName("simonis.Initiating$A", true, cl4);  // 'cl4' will become an "initiating" class loader of "A"
/*

// Class.forName will ultimately (through JVM_FindClassFromCaller) call the method below with 'class_loader' == 'cl3'

Klass* SystemDictionary::resolve_instance_class_or_null(Symbol* name,
                                                        Handle class_loader,
                                                        Handle protection_domain,
                                                        TRAPS) {

      // Do actual loading
      k = load_instance_class(name, class_loader, THREAD);
// -> This calls back to Java by calling ClassLoader::loadClass() on 'class_loader'

// At this place 'Initiating$A' was loaded, but by 'cl2' so 'k->class_loader()' == 'cl2' != 'class_loader()' == 'cl3'

      // If everything was OK (no exceptions, no null return value), and
      // class_loader is NOT the defining loader, do a little more bookkeeping.
      if (!HAS_PENDING_EXCEPTION && k != NULL &&
        k->class_loader() != class_loader()) {

        check_constraints(d_index, d_hash, k, class_loader, false, THREAD);

// This actually adds 'class_loader()' == 'cl3' as INITIATING loader of 'k' to 'cl3's dictionary

              update_dictionary(d_index, d_hash, p_index, p_hash,
                              k, class_loader, THREAD);

// i.e.

void SystemDictionary::update_dictionary(int d_index, unsigned int d_hash,
                                         int p_index, unsigned int p_hash,
                                         InstanceKlass* k,
                                         Handle class_loader,

  ClassLoaderData *loader_data = class_loader_data(class_loader);

    // Make a new dictionary entry.
    Dictionary* dictionary = loader_data->dictionary();
    InstanceKlass* sd_check = find_class(d_index, d_hash, name, dictionary);
    if (sd_check == NULL) {
      dictionary->add_klass(d_index, d_hash, name, k);


// also see Kris Mok's comments @ https://gist.github.com/rednaxelafx/1284325/4301cf6a3431a3b1fcca28e1ef80f81a86619ae0

JVMLS (https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-5.html)

5.3. Creation and Loading

A class loader L may create C by defining it directly or by delegating to another class loader.
If L creates C directly, we say that L defines C or, equivalently, that L is the defining loader of C.

When one class loader delegates to another class loader, the loader that initiates the loading is not
necessarily the same loader that completes the loading and defines the class.
If L creates C, either by defining it directly or by delegation, we say that L initiates loading of C or,
equivalently, that L is an initiating loader of C.

// Initial problem description: "Java is not type-safe", by Vijay Saraswat, AT&T Research,180 Park Avenue, Florham Park NJ 07932

https://www.cis.upenn.edu/~bcpierce/courses/629/papers/Saraswat-javabug.html

 */
    //java.awt.Frame frame = new java.awt.Frame();
    //System.out.println("Defining = " + frame.getClass().getClassLoader());

    cA.newInstance(); // Will make 'c2' (the defining loader of "A") an initiating loader of "java.awt.Frame"
    System.out.println("Defining = " + cA.getClassLoader().getName());
    System.out.println("cl1 is initiating loader for A: " + cl1.isInitiating("simonis.Initiating$A"));
    System.out.println("cl2 is initiating loader for A: " + cl2.isInitiating("simonis.Initiating$A"));
    System.out.println("cl3 is initiating loader for A: " + cl3.isInitiating("simonis.Initiating$A"));
    System.out.println("cl4 is initiating loader for A: " + cl4.isInitiating("simonis.Initiating$A"));

    Instrumentation inst = InstAgent.inst;
    System.out.println(cl1.getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(cl1)).forEach(System.out::println);
    System.out.println(cl2.getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(cl2)).forEach(System.out::println);
    System.out.println(cl3.getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(cl3)).forEach(System.out::println);
    System.out.println(cl4.getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(cl4)).forEach(System.out::println);
    System.out.println(Initiating.class.getClassLoader().getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(Initiating.class.getClassLoader())).stream().filter(c -> c.getName().startsWith("simonis") || c.getName().startsWith("java.awt.Frame")).forEach(System.out::println);
    System.out.println(Initiating.class.getClassLoader().getParent().getName() + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(Initiating.class.getClassLoader().getParent())).stream().filter(c -> c.getName().startsWith("simonis") || c.getName().startsWith("java.awt.Frame")).forEach(System.out::println);
    System.out.println("Bootstrap" + " initiating loder for:");
    Arrays.asList(inst.getInitiatedClasses(null)).stream().filter(c -> c.getName().startsWith("simonis") || c.getName().startsWith("java.awt.Frame")).forEach(System.out::println);
  }

  public static class A {
    static { java.awt.Frame frame = new java.awt.Frame(); }
  }
}
