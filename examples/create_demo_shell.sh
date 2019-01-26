
export PS1="\[\e]0;\w\a\]\n\[\e[32m\]\u@\h:\[\033[01;34m\]\w\[\033[00m\]\n\$ "

CWD=`pwd`

export PATH=/share/output-jdk-dbg/images/jdk/bin:$PATH

rm -rf /tmp/Demo_$1
mkdir -p /tmp/Demo_$1
cd /tmp/Demo_$1

if [ "$1" == "sa" ]; then
  export _JAVA_OPTIONS="--patch-module=java.desktop=$CWD/lib/java.desktop"
else
if [ "$1" == "instrument2" ]; then
  cp $CWD/bin/InstAgent.jar .
else
if [ "$1" == "loop" ]; then
  export _JAVA_OPTIONS='-Xbatch -XX:-UseCompressedOops -XX:+UseSerialGC -XX:-TieredCompilation -XX:-UseOnStackReplacement -XX:+UnlockDiagnosticVMOptions -XX:-LogVMOutput -XX:CICompilerCount=1'
else
if [ "$1" == "loop_print" ]; then
  export _JAVA_OPTIONS='-Xbatch -XX:-UseCompressedOops -XX:+UseSerialGC -XX:-TieredCompilation -XX:-UseOnStackReplacement -XX:+UnlockDiagnosticVMOptions -XX:-LogVMOutput -XX:CICompilerCount=1'
else
if [ "$1" == "loop_with_gc" ]; then
  export _JAVA_OPTIONS='-Xbatch -XX:-UseCompressedOops -XX:+UseSerialGC -XX:-TieredCompilation -XX:-UseOnStackReplacement -XX:+UnlockDiagnosticVMOptions -XX:-LogVMOutput -XX:CICompilerCount=1'
else
if [ "$1" == "snippets" ]; then
export PATH=/share/output-panama-dbg/images/jdk/bin:$PATH
  export _JAVA_OPTIONS='-Xbatch -XX:-UseCompressedOops -XX:+UseSerialGC -XX:-TieredCompilation -XX:-UseOnStackReplacement -XX:+UnlockDiagnosticVMOptions -XX:-LogVMOutput -XX:CICompilerCount=1'
fi
fi
fi
fi
fi
fi

alias la='ls -la'

export CLASSPATH=$CWD/bin:$CWD/lib/asm-6.0.jar

set -o history
unset HISTFILE
history -c
history -r $CWD/.history_$1
