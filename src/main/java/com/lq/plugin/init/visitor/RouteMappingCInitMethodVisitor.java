package com.lq.plugin.init.visitor;

import com.lq.plugin.init.DeepLinkClassInfo;
import com.lq.plugin.init.utils.ConfigFileMgr;
import com.lq.plugin.init.utils.Log;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;

public class RouteMappingCInitMethodVisitor extends AdviceAdapter implements Opcodes {
    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param api           the ASM API version implemented by this visitor. Must be one of {@link
     *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param name          the method's name.
     * @param descriptor    the method's descriptor
     */
    protected RouteMappingCInitMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);

        ArrayList<DeepLinkClassInfo> deepLinkClassInfos = ConfigFileMgr.getInstance().readDeepLinkConfig();

        if (deepLinkClassInfos == null) {
            return;
        }

        Log.listToString(deepLinkClassInfos);

        int lineNumber = 19;
        for (DeepLinkClassInfo deepLinkClassInfo : deepLinkClassInfos) {
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(lineNumber++ , l2);
            mv.visitFieldInsn(GETSTATIC, "com/lazylite/bridge/router/deeplink/route/RouteMapping", "ROUTE_MAP", "Ljava/util/Map;");
            mv.visitLdcInsn(deepLinkClassInfo.getPath());
            mv.visitLdcInsn(deepLinkClassInfo.getFullName());
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(POP);
        }

    }
}
