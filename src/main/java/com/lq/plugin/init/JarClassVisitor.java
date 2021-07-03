package com.lq.plugin.init;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JarClassVisitor extends ClassVisitor implements Opcodes {

    private boolean bThread;

    private String mClass;

    public JarClassVisitor(ClassVisitor cv, String clazz) {
        super(ASM5, cv);
        mClass = clazz;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        bThread = "java/lang/Thread".equals(superName);
//        String modifierSuperName = superName;
//        if (bThread && !name.contains("ShadowThread")) {
//            modifierSuperName = Constants.SHADOW_THREAD;
//        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//        if (
//                "KwShadowThread.class".equals(mClass) ||
//                        "ShadowThread.class".equals(mClass) ||
//                        "BuildConfig.class".equals(mClass) ||
//                        mClass.startsWith("R$") ||
//                        "R.class".equals(mClass)
//            /* || whiteList() */) {
//            return mv;
//        }
//        return new JarMethodVisitor(mv, mClass);

        if ("<clinit>".equals(name)) {
            Log.e("class visitor:" + name);
//            return new JarMethodVisitor(ASM5, mv, access, name, desc);
            return new InitJarMethodVisitor(ASM5, mv, access, name, desc);
        } else {
            return mv;
        }
    }
}

