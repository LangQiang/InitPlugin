package com.lq.plugin.init;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class InitJarMethodVisitor extends AdviceAdapter implements Opcodes {


    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param api           the ASM API version implemented by this visitor. Must be one of {@link
     *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param name          the method's name.
     * @param descriptor    the method's descriptor.
     */
    protected InitJarMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        Log.e("visitCode");

        ArrayList<String> initClasses = getInitClasses();


        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(19, l2);
        mv.visitFieldInsn(GETSTATIC, "com/lazylite/bridge/init/ComponentInit", "classNames", "Ljava/util/List;");
        mv.visitLdcInsn("com.lazy.lite.auto.InitImpl$modulea");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(POP);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(20, l3);
        mv.visitFieldInsn(GETSTATIC, "com/lazylite/bridge/init/ComponentInit", "classNames", "Ljava/util/List;");
        mv.visitLdcInsn("com.lazy.lite.auto.InitImpl$moduleb");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(POP);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(21, l4);
        mv.visitFieldInsn(GETSTATIC, "com/lazylite/bridge/init/ComponentInit", "classNames", "Ljava/util/List;");
        mv.visitLdcInsn("com.lazy.lite.auto.InitImpl$basemodule");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(POP);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitLineNumber(22, l5);
        mv.visitFieldInsn(GETSTATIC, "com/lazylite/bridge/init/ComponentInit", "classNames", "Ljava/util/List;");
        mv.visitLdcInsn("com.lazy.lite.auto.InitImpl$media");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(POP);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(23, l6);
        mv.visitFieldInsn(GETSTATIC, "com/lazylite/bridge/init/ComponentInit", "classNames", "Ljava/util/List;");
        mv.visitLdcInsn("com.lazy.lite.auto.InitImpl$barrage");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(POP);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitLineNumber(24, l7);
        mv.visitFieldInsn(GETSTATIC, "com/lazylite/bridge/init/ComponentInit", "classNames", "Ljava/util/List;");
        mv.visitLdcInsn("com.lazy.lite.auto.InitImpl$tripartite");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(POP);

    }

    private ArrayList<String> getInitClasses() {
        File file = new File("init.config");
        if (!file.exists()) {
            return null;
        }

        BufferedReader bufferedReader = null;
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            bufferedReader = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                arrayList.add(s.replace("\r", "").replace("\n", ""));
            }
            for (String s1 : arrayList) {
                Log.e(s1);
            }
            return arrayList;
        } catch (FileNotFoundException fnfe) {
            Log.e(fnfe.getMessage());
        } catch (IOException e) {
            Log.e(e.getMessage());
        }finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ioe) {
                Log.e(ioe.getMessage());
            }
            file.delete();
        }
        return null;
    }
}
