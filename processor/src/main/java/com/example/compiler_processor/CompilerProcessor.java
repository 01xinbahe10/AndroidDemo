package com.example.compiler_processor;


import com.google.auto.service.AutoService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


@AutoService(Processor.class)
@SuppressWarnings("NullAway")
public class CompilerProcessor extends AbstractProcessor {
    private final String PACKAGE_NAME = "permission";
    private final String CLASS_NAME = "PermissionUtil";
    private Messager mMessager;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        //初始化我们需要的基础工具
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(PermissionCheck.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 遍历所有被注解了@PermissionCheck元素
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(PermissionCheck.class)) {
            System.out.println("---------------->>>>>>>>  字段名：" + annotatedElement.getSimpleName());

            //这个log提示可能会导致编译不通过，是因为如果其中发生不得已抛错，该类型log会提示编译器有错，终止编译；需谨慎使用
//            mMessager.printMessage(Diagnostic.Kind.ERROR,"---------------->>>>>>>>  " + annotatedElement.getSimpleName());
        }

        System.out.println("---------------->>>>>>>>  set:" + (set == null)+"     roundEnvironment:"+(roundEnvironment == null));
        createPermissionUtilClass();



        return true;
    }


    private void createPermissionUtilClass(){
        boolean isPresence = true;
        try {
            Class.forName((PACKAGE_NAME+"."+CLASS_NAME));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block    
            isPresence = false;
        }

        if (isPresence){
            return;
        }

        System.out.println("------>>>>该类不存在");
        BufferedWriter writer = null;

        try {
            JavaFileObject source = mFiler.createSourceFile((PACKAGE_NAME+"."+CLASS_NAME));
            writer = new BufferedWriter(source.openWriter());
            writer.write("package "+PACKAGE_NAME+";\n\n");
            writer.write("import android.Manifest;\n");
            writer.write("import android.app.Activity;\n");
            writer.write("import androidx.core.content.ContextCompat;\n");
            writer.write("import androidx.core.app.ActivityCompat;\n");
            writer.write("import android.content.pm.PackageManager;\n");
            writer.write("/**Permission checker*/\n");
            writer.write("public final class "+CLASS_NAME+" {\n");
            writer.write("    /*Privilege application status return code.*/\n");
            writer.write("    //success return 0\n");
            writer.write("    public static final int SUCCESS = 0;\n");
            writer.write("    //fail return -1\n");
            writer.write("    public static final int FAIL = -1;\n");
            writer.write("    //Applied but the user refused return 1\n");
            writer.write("    public static final int APPLIED_AND_REJECTED = 1;\n\n");
            writer.write("    public static int checkPermission(Activity activity,String[] permission){\n");
            writer.write("        for(String per : permission){\n");
            writer.write("            if (ContextCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {\n");
            writer.write("                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, per)) {\n");
            writer.write("                    return APPLIED_AND_REJECTED;\n");
            writer.write("                }\n");
            writer.write("                return FAIL;\n");
            writer.write("            }\n");
            writer.write("        }\n");
            writer.write("        return SUCCESS;\n");
            writer.write("    }\n");
            writer.write("}\n");

            writer.flush();
        } catch (IOException e) {
            //当异常不属于 重新创建文件 的错误就不使用mMessager打印 "源码无法撰写" 的提示
            if (!(e instanceof FilerException)){
                e.printStackTrace();
                mMessager.printMessage(Diagnostic.Kind.ERROR,"Could not write source for");
            }

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //Silent
                }
            }
        }

    }


}
