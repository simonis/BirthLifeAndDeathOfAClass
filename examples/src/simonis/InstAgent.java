package simonis;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InstAgent {

  static String pattern;
  public static Instrumentation inst;
  static int count = -1; // Will be increased before first usage!
  
  public static void premain(String args, Instrumentation inst) {
    InstAgent.inst = inst;
    pattern = args;
    inst.addTransformer(new MethodInstrumentorTransformer(), true);
  }

  public static void agentmain(String args, Instrumentation inst) {
    premain(args, inst);
  }

  public static Instrumentation getInst() {
    count++;
    return inst;
  }
  static class MethodInstrumentorTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, 
                            String className, 
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
      
      if (!className.startsWith(pattern)) return classfileBuffer;
      
      System.out.println("=> " + (classBeingRedefined == null ? "transformimg " : "re-transforming ") + className);

      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
      MethodInstrumentorClassVisitor cv = new MethodInstrumentorClassVisitor(cw, classBeingRedefined == null);
      ClassReader cr = new ClassReader(classfileBuffer);
      cr.accept(cv, 0);
      return cw.toByteArray();
    }

  }

  static class MethodInstrumentorClassVisitor extends ClassVisitor {
    private String className;
    private boolean transform;

    public MethodInstrumentorClassVisitor(ClassVisitor cv, boolean transform) {
      super(Opcodes.ASM5, cv);
      this.transform = transform;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      cv.visit(version, access, name, signature, superName, interfaces);
      className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
      if (className.startsWith(pattern)) {
        mv = new MethodInstrumentorMethodVisitor(mv, className.replace('/', '.') + "::" + name + desc, transform);
      }
      return mv;
    }
  }

  static class MethodInstrumentorMethodVisitor extends MethodVisitor implements Opcodes {
    private String methodName;
    private boolean transform;

    public MethodInstrumentorMethodVisitor(MethodVisitor mv, String name, boolean transform) {
      super(Opcodes.ASM5, mv);
      methodName = name;
      this.transform = transform;
    }

    @Override
    public void visitCode() {
      mv.visitCode();
      mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
      mv.visitLdcInsn((transform ? "-> " : count + "> ") + methodName);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitInsn(int opcode) {
      if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn((transform ? "<- " : "<" + count + " ") + methodName);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
      }
      mv.visitInsn(opcode);
    }
  }
}
