package com.meiyou.compiler;

import com.google.auto.service.AutoService;
import com.meiyou.annotation.Test;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@Deprecated
@AutoService(Processor.class)
@SupportedAnnotationTypes({ "com.meiyou.annotation.Test" })
public class TestProcessor extends AbstractProcessor {
    private Filer filer;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(Test.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("------ process -----");
        //MethodSpec这个类是要引入'com.squareup:javapoet:1.8.0'包，方便通过代码创建java文件
        MethodSpec main = MethodSpec.methodBuilder("main")
                                    //.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                    .returns(void.class)
                                    .addParameter(String[].class, "args")
                                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                                    .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloChina")
                                      .addMethod(main)
                                      .build();

        JavaFile javaFile = JavaFile.builder("com.francis.helloworld", helloWorld)
                                    .build();

        try {
            //这里的输出要在Gradle Console中看
            javaFile.writeTo(System.out);
//            javaFile.writeTo(new File());
            javaFile.writeTo(filer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}