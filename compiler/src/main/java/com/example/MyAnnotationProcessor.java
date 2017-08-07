package com.example;

import com.example.anno.BindView;
import com.example.anno.ContentView;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by fenglei on 17/8/7.
 */

public class MyAnnotationProcessor extends AbstractProcessor {

    /**
     * 日志打印类
     * */
    private Messager messager;
    /**
     * 元素工具类
     * */
    private Elements elementsUtils;

    /**
     * 保存所有的要生成的注解文件信息
     * */
    private Map<String, ProxyInfo> mProxyMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementsUtils = processingEnv.getElementUtils();
        Map<String, String> map = processingEnv.getOptions();
        for (String key : map.keySet()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "key" + "：" + map.get(key));
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        supportTypes.add(ContentView.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // 获取全部的带有BindView注解的Element
        Set<? extends Element> elesWidthBind = roundEnv.getElementsAnnotatedWith(BindView.class);
        // 对BindView进行循环，构建ProxyInfo信息
        for (Element element : elesWidthBind) {
            // 强转成属性元素
            VariableElement variableElement = (VariableElement) element;
            // 我们知道属性元素的外层一定是类元素
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            // 获取类元素的类名
            String fqClassName = typeElement.getQualifiedName().toString();
            // 以class名称为key，保存到mProxyMap中
            ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(elementsUtils, typeElement);
                mProxyMap.put(fqClassName, proxyInfo);
            }
            // 获取BindView注解，把信息放入proxyInfo中
            BindView bindAnnotation = element.getAnnotation(BindView.class);
            int id = bindAnnotation.value();
            proxyInfo.injectVariables.put(id, variableElement);
        }

        // 获取所有的ContentView注解，操作原理和上面的BindView一样
        Set<? extends Element> contentAnnotations = roundEnv.getElementsAnnotatedWith(ContentView.class);
        for (Element element : contentAnnotations) {
            TypeElement typeElement = (TypeElement) element;
            String fqClassName = typeElement.getQualifiedName().toString();
            ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(elementsUtils, typeElement);
                mProxyMap.put(fqClassName, proxyInfo);
            }
            ContentView contentViewAnnotation = element.getAnnotation(ContentView.class);
            proxyInfo.contentViewId =contentViewAnnotation.value();
        }

        // 循环生成源文件
        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(), proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

}
