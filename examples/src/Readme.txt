Howto build:

from examples:
/share/output-jdk9-dev-opt/images/jdk/bin/javac -cp lib/asm-6.0.jar -d bin/ src/simonis/InstAgent.java
cd bin
/share/output-jdk9-dev-opt/images/jdk/bin/jar cvfm InstAgent.jar ../src/manifest.mf simonis/InstAgent*


g++ -fPIC -shared -I /share/output-jdk9-hs-comp-dbg/images/jdk/include/ -I /share/output-jdk9-hs-comp-dbg/images/jdk/include/linux/ -o ../bin/traceMethodAgent.so jvmti/traceMethodAgent.cpp
