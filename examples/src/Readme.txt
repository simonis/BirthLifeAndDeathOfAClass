Howto build:

from examples:
/share/output-jdk9-dev-opt/images/jdk/bin/javac -cp lib/asm-6.0.jar -d bin/ src/simonis/MethodInstAgent.java
cd bin
/share/output-jdk9-dev-opt/images/jdk/bin/jar cvfm MethodInstAgent.jar ../src/manifest.mf simonis/MethodInstAgent*


g++ -fPIC -shared -I /share/output-jdk9-hs-comp-dbg/images/jdk/include/ -I /share/output-jdk9-hs-comp-dbg/images/jdk/include/linux/ -o ../bin/traceMethodAgent.so jvmti/traceMethodAgent.cpp
