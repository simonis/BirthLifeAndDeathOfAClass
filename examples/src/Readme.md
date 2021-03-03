### Howto build:

from examples:
```
/share/output-jdk9-dev-opt/images/jdk/bin/javac -cp lib/asm-6.0.jar -d bin/ src/simonis/InstAgent.java
cd bin
/share/output-jdk9-dev-opt/images/jdk/bin/jar cvfm InstAgent.jar ../src/manifest.mf simonis/InstAgent*
```

```
/share/software/Java/corretto-11/bin/javac src/simonis/HotChocolate.java
/priv/simonisv/output/jdk-opt/images/jdk/bin/javac --patch-module java.base=src src/jdk/internal/loader/ClassLoaders.java
```

```
g++ -fPIC -shared -I /share/output-jdk9-hs-comp-dbg/images/jdk/include/ -I /share/output-jdk9-hs-comp-dbg/images/jdk/include/linux/ -o ../bin/traceMethodAgent.so jvmti/traceMethodAgent.cpp
```

### Howto run:

from examples:
```
/priv/simonisv/output/jdk-opt/images/jdk/bin/java -Dsun.misc.URLClassPath.debug=true -Djava.system.class.loader=simonis.HotChocolate -DguestJdkBootPath=/share/software/Java/corretto-8/lib/tools.jar:/tmp -Xlog:class+load --patch-module java.base=src/jdk/internal/loader/ -cp src HelloWorld

/priv/simonisv/output/jdk-opt/images/jdk/bin/javac -J-Dsun.misc.URLClassPath.debug=true -J-Djava.system.class.loader=simonis.HotChocolate -J-DguestJdkBootPath=/share/software/Java/corretto-8/lib/tools.jar -J-classpath -Jsrc -J-Xlog:class+load -J--patch-module -Jjava.base=src/ -J-Djdk.module.showModuleResolution -d /tmp/ ~/Java/HelloWorld.java 2>&1 | less
```

### Understanding how class loading works for standard launchers like `javac`

Using `_JAVA_LAUNCHER_DEBUG=1 /priv/simonisv/output/jdk-opt/images/jdk/bin/javac -version` reveals that the `javac` launcher for jdk >=9 executes the compiler by invoking the JVM with `-m jdk.compiler/com.sun.tools.javac.Main`, i.e. it executes the class `com.sun.tools.javac.Main` from the `jdk.compiler` module. It  turns out that setting `java.system.class.loader` to a custom class loader will not load the applications class (i.e. `com.sun.tools.javac.Main` from the `jdk.compiler` module in this case) with the custom application class loader as expected. Instead. the application class will be loaded by the default system (i.e. default application) class loader [jdk.internal.loader.ClassLoaders$AppClassLoader](https://github.com/openjdk/jdk/blob/4c9adce20d114a3df629fa8c72d88795039ae69a/src/java.base/share/classes/jdk/internal/loader/ClassLoaders.java#L158) which is the default parent for the user defined application class loader.

This is because when using `-m <module>/class` the VM will [add `<module>` to the boot layer](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/jdk/internal/module/ModuleBootstrap.java#L308) and [associate it with the default system class loader](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/jdk/internal/module/ModuleLoaderMap.java#L71). Later, all these modules are [loaded and registered with the associated built-in class loaders](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/jdk/internal/module/ModuleBootstrap.java#L499). This means that [the types contained in the respective modules become visibel in their associated class loaders](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/jdk/internal/loader/BuiltinClassLoader.java#L230).

If we start the launcher with `-cp <class-path> <class>` [sun.launcher.LauncherHelper.checkAndLoadMain](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/sun/launcher/LauncherHelper.java#L664) will call [sun.launcher.LauncherHelper.loadMainClass](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/sun/launcher/LauncherHelper.java#L759) which loads the main application class by calling [`Class.forName(...)`](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/sun/launcher/LauncherHelper.java#L780) with `Classloader.getSystemClassLoader()` as class loader. If we start with `--module-path <module-path> -m <module>/<class>` [sun.launcher.LauncherHelper.checkAndLoadMain](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/sun/launcher/LauncherHelper.java#L664) will call [sun.launcher.LauncherHelper.loadModuleMainClass](https://github.com/openjdk/jdk/blob/96c43210d34f6a0982f7f577353177ecf1ca6975/src/java.base/share/classes/sun/launcher/LauncherHelper.java#L704) which will load the main module from the boot layer (notice that the main module must be in the boot layer!) and call `Class.forName(...)` with the defining loader of the current class (i.e. `LauncherHelper`) as class loader to load the main application class. See [Question about java.system.class.loader and the module system](https://mail.openjdk.java.net/pipermail/core-libs-dev/2021-March/074851.html) on the core-libs-dev mailing list.

In order to use the jdk8 `javac` classes we have to replace the `javac` launcher in jdk >= 9 with the original launcher from jdk8 (in order to make it run we have to set `LD_LIBRARY_PATH=/priv/simonisv/output/jdk-opt/images/jdk/lib` to the path where the `libjli.so` veersion of the new jdk >= 9 is found and also set `sun.boot.class.path` to point to the `rt.jar` of the old jdk8 from where we copied the launcher). This will now  executes the compiler by invoking the JVM with `com.sun.tools.javac.Main` as main class. By default this is still the new jdk >= 9 `javac` implementation. But we can now define our own application class loader `-Djava.system.class.loader=simonis.HotChocolate` and let it prefer the jdk8 compiler classes (`-DguestJdkBootPath=/share/software/Java/corretto-8/lib/tools.jar`) to execute the jdk8 `javac` implementation in the context of a jdk >= 9. `simonis.HotChocolate` is a simple class loader derived from `java.net.URLClassLoader` which loads classes from the URL passed to it via the `guestJdkBootPath` system property.

With the following command line we can compile an old version of [Spring PetClinic](https://github.com/spring-projects/spring-petclinic/commit/e38a9feebe1814ada460dea50ba45f11389fdc9f) with jdk-17-ea and the jdk8 version of `javac` producing Java 8 class files (at least up to the packaging step):
```
JAVA_HOME=/priv/simonisv/output/jdk-opt/images/jdk \
LD_LIBRARY_PATH=/priv/simonisv/output/jdk-opt/images/jdk/lib \
_JAVA_OPTIONS='--illegal-access=permit \
               -Djava.system.class.loader=simonis.HotChocolate \
	       -DguestJdkBootPath=/share/software/Java/corretto-8/lib/tools.jar \
	       -Dsun.boot.class.path=/share/software/Java/corretto-8/jre/lib/rt.jar \
	       -Xbootclasspath/a:/priv/simonisv/Git/BirthLifeAndDeathOfAClass/examples/src:/share/software/Java/corretto-8/jre/lib/rt.jar' \
mvn -Dmaven.compiler.verbose=true -Dmaven.compiler.fork=true -X package
```

`JAVA_HOME` points to a new jdk-17-ea where we've replaced the `javac` launcher with the one from jdk8. It will be picked up by Maven as defaul jdk. The old launcher needs `LD_LIBRARY_PATH=/priv/simonisv/output/jdk-opt/images/jdk/lib` in order to find `libjli.so` (because it only searches the library relatively to its own location which is different in jdk-17-ea. Because Maven is doing illegal reflective accesses we need `--illegal-access=permit` and because new jdk are missing the `javax.xml.bind` package we need `-Xbootclasspath/a:/share/software/Java/corretto-8/jre/lib/rt.jar`. We also set the location of our custom application class loader `-Djava.system.class.loader=simonis.HotChocolate` by appending `-Xbootclasspath/a:/priv/simonisv/Git/BirthLifeAndDeathOfAClass/examples/src` to the boot classe path (it would be actually enough to append this location to the normal class path, but that's not possible with a `_JAVA_OPTIONS` option.

See:
- [Setting BOOT_RTJAR: rt.jar vs. 'sun.boot.class.path'](http://mail.openjdk.java.net/pipermail/core-libs-dev/2013-November/thread.html#23229)
- [JDK-8026964: Building with an IBM J9 boot jdk requires special settings for BOOT_RTJAR](https://bugs.openjdk.java.net/browse/JDK-8026964)
- [JDK-8026964 Review thread](http://mail.openjdk.java.net/pipermail/build-dev/2013-November/thread.html#11108)
