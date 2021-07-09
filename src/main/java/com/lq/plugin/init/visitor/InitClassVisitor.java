package com.lq.plugin.init.visitor;

import com.lq.plugin.init.utils.Log;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InitClassVisitor extends ClassVisitor implements Opcodes {

    private boolean bThread;

    private String mClass;

    public InitClassVisitor(ClassVisitor cv, String clazz) {
        super(ASM5, cv);
        mClass = clazz;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (mClass != null && mClass.contains("com/lazylite/bridge/init/ComponentInit.class")) {

            if ("<clinit>".equals(name)) {

                Log.e("mClass: " + mClass + "   visitMethod <clinit>");
                return new InitMethodVisitor(ASM5, mv, access, name, desc);

            } else if ("findInitClassFullNames".equals(name)) {

                Log.e("mClass: " + mClass + "   visitMethod findInitClassFullNames");
                return new GetClassMethodVisitor(ASM5, mv, access, name, desc);

            }
        }

        if (mClass != null && mClass.contains("TestInit.class")) {

            if ("<clinit>".equals(name)) {

                Log.e("mClass: " + mClass + "   visitMethod <clinit>");
                return new InitMethodVisitor(ASM5, mv, access, name, desc);

            } else if ("findInitClassFullNames".equals(name)) {

                Log.e("mClass: " + mClass + "   visitMethod findInitClassFullNames");
                return new GetClassMethodVisitor(ASM5, mv, access, name, desc);

            }
        }

        return mv;

    }
}

