package simonis;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

public class HotChocolate extends URLClassLoader {

    public HotChocolate(ClassLoader parent) {
        //super("HotChocolate", new URL[0], parent);
        super(new URL[0], parent);
        System.out.println("--> HotChocolate: initializing");
        for (final String path : System.getProperty("guestJdkBootPath", "").split(":")) {
            try {
                if (!"".equals(path)) {
                    this.addURL(Paths.get(path).toUri().toURL());
                    System.out.println("--> HotChocolate: adding \"" + path + "\" to class path");
                }
            }
            catch (MalformedURLException mue) {
                System.err.println("--> HotChocolate: couldn't add " + path + " to class path");
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class cl;
        try {
            System.out.println("--> HotChocolate: findClass " + name);
            cl = super.findClass(name);
            System.out.println("<-- HotChocolate: findClass " + name + " OK");
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("<-- HotChocolate: findClass " + name + " ClassNotFoundException");
            throw cnfe;
        }
        return cl;
    }

    @Override
    protected Class<?> findClass(String mn, String cn) {
        Class cl;
        System.out.printf("--> HotChocolate: findClass %s:%s%n", mn, cn);
        cl = super.findClass(mn, cn);
        System.out.printf("<-- HotChocolate: findClass %s:%s %s%n", mn, cn, (cl == null ? "NULL" : "OK"));
        return cl;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        System.out.println("--> HotChocolate: loadClass " + name);
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                // Reverse the default search order (see ClassLoader.loadClass())
                // First try in this URLClassLoader, then delegate if not found
                try {
                    c = findClass(name);
                    System.out.println("<-- HotChocolate: loadClass " + name + " OK");
                } catch (ClassNotFoundException cnfe) {
                    System.out.println("<-- HotChocolate: loadClass " + name + " ClassNotFoundException");
                }
                if (c == null) {
                    c = super.loadClass(name, resolve);
                    System.out.println("<-- HotChocolate: loadClass " + name + " parent");
                }
            }
            else {
                System.out.println("<-- HotChocolate: loadClass " + name + " found");
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

}
