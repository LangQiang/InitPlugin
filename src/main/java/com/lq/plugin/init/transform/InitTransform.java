package com.lq.plugin.init.transform;


import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.ide.common.internal.WaitableExecutor;
import com.lq.plugin.init.ConfigFileMgr;
import com.lq.plugin.init.visitor.InitClassVisitor;
import com.lq.plugin.init.Log;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;


public class InitTransform extends Transform {

    private Project project;

    private WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();


    public InitTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "InitTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        long startTime = System.currentTimeMillis();

        Collection<TransformInput> inputs = transformInvocation.getInputs();

        final TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        //删除之前的输出
        if (outputProvider != null)
            outputProvider.deleteAll();
        //遍历inputs
        for (TransformInput input : inputs) {
            //遍历directoryInputs, 为文件夹中的class文件
            for (DirectoryInput in : input.getDirectoryInputs()) {
                //处理directoryInputs
                File dest = outputProvider.getContentLocation(
                        in.getName(),
                        in.getContentTypes(),
                        in.getScopes(),
                        Format.DIRECTORY);
                //并发编译
                waitableExecutor.execute(() -> {
                    transformDirectory(in.getFile(), dest);
                    return null;
                });
            }
            for (JarInput jarInput : input.getJarInputs()) {
                waitableExecutor.execute(() -> {
                    transformJar2(jarInput, outputProvider, transformInvocation.getContext());
                    return null;
                });
            }
        }
        //等待所有任务结束
        waitableExecutor.waitForTasksWithQuickFail(true);
        ConfigFileMgr.getInstance().deleteConfig();
        long cost = System.currentTimeMillis() - startTime;
        Log.e("init transform cost:" + cost + "ms");

    }

    /**
     * 处理文件目录下的class文件
     */
    private void transformDirectory(File input, File dest) throws IOException {
        if (dest.exists()) {
            FileUtils.forceDelete(dest);
        }

        FileUtils.forceMkdir(dest);
        String srcDirPath = input.getAbsolutePath();
        String destDirPath = dest.getAbsolutePath();

        File[] files = input.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath);
            File destFile = new File(destFilePath);
            if (file.isDirectory()) {
                transformDirectory(file, destFile);
            } else if (file.isFile()) {
                FileUtils.touch(destFile);
                if (file.getAbsolutePath().contains("TestInit")) {
                    transformSingleFile(file, destFile);
                } else {
                    FileUtils.copyFile(file, destFile);
                }
            }
        }
    }

    private void transformSingleFile(File input, File dest) {

        String inputPath = input.getAbsolutePath();
        String outputPath = dest.getAbsolutePath();
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(inputPath);
            ClassReader classReader = new ClassReader(fileInputStream);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new InitClassVisitor(classWriter, input.getName());
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            fileOutputStream = new FileOutputStream(outputPath);
            byte[] byteCode = classWriter.toByteArray();
            if (byteCode != null && byteCode.length > 0) {
                fileOutputStream.write(byteCode);
            }
        } catch (Exception e) {
            Log.e("transformSingleFile " + e.toString());
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param jarInput module 三方库输入
     * @param outputProvider 输出
     * @throws IOException 文件读取io异常
     */
    private void transformJar2(JarInput jarInput, TransformOutputProvider outputProvider, Context context) throws IOException {
        String destName = jarInput.getFile().getName();
        /*截取文件路径的 md5 值重命名输出文件,因为可能同名,会覆盖*/
        String hexName = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath()).substring(0, 8);
        /* 获取 jar 名字*/
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4);
        }
        /* 获得输出文件*/
        File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
        File modifiedJar = null;

        modifiedJar = modifyJar(jarInput.getFile(), context.getTemporaryDir(), true);

        if (modifiedJar == null) {
            modifiedJar = jarInput.getFile();
        }


        FileUtils.copyFile(modifiedJar, dest);
    }

    private File modifyJar(File jarFile, File tempDir, boolean nameHex) throws IOException {
        JarFile file = new JarFile(jarFile, false);
        String hexName = "";
        if (nameHex) {
            hexName = DigestUtils.md5Hex(jarFile.getAbsolutePath()).substring(0, 8);
        }
        File outputJar = new File(tempDir, hexName + jarFile.getName());
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar));
        Enumeration enumeration = file.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry oriJarEntry = (JarEntry) enumeration.nextElement();
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream(oriJarEntry);
            } catch (Exception e) {
                return null;
            }
            String entryName = oriJarEntry.getName();
            //JAR包的签名文件
            if (entryName.endsWith(".DSA") || entryName.endsWith(".SF") /* || entryName.endsWith(".RSA") */) {
                //ignore
            } else {
                String className;
                JarEntry jarEntry = new JarEntry(entryName);
                jarOutputStream.putNextEntry(jarEntry);
                byte[] modifiedClassBytes = null;
                byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
                if (entryName.endsWith(".class")) {
                    className = entryName.replace(Matcher.quoteReplacement(File.separator), ".")
                            .replace(".class", "");
                    if (needModify(className)) {
                        modifiedClassBytes = modifyClass(sourceClassBytes, entryName);
                    }
                }
                if (modifiedClassBytes == null) {
                    modifiedClassBytes = sourceClassBytes;
                }
                jarOutputStream.write(modifiedClassBytes);
                jarOutputStream.closeEntry();
            }
        }
        jarOutputStream.close();
        file.close();
        return outputJar;
    }

    private static boolean needModify(String className) {
        return "com.lazylite.bridge.init.ComponentInit".equals(className);
    }

    private static byte[] modifyClass(byte[] srcClass, String clazz) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new InitClassVisitor(classWriter, clazz);
        ClassReader cr = new ClassReader(srcClass);
//        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        cr.accept(classVisitor, ClassReader.EXPAND_FRAMES);
//        cr.accept(classVisitor, 0);
        return classWriter.toByteArray();
    }

}
