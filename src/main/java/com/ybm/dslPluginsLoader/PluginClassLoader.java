/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslPluginsLoader;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader implements ImportBeanDefinitionRegistrar {

    @Value( "${dsl.plugins.path:/Users/yogeshwar_mahalle/RuleEngine/DSLPlugins/}" )
    private final String pluginJarsUrl; // = "file:///Users/yogeshwar_mahalle/RuleEngine/DSLPlugins/interface-plugin-impl-0.0.1-SNAPSHOT.jar";
    @Value( "${dsl.plugins.package.name:com.ybm.dslResolverImpl}"  )
    private final String pluginPackage; // = "com.ybm.dslResolverImpl";

    public PluginClassLoader(String pluginJarsUrl, String pluginPackage) {
        this.pluginJarsUrl = pluginJarsUrl;
        this.pluginPackage = pluginPackage;
    }

    public ClassLoader getSystemClassLoader() {
        try {
            File pluginDir = new File(pluginJarsUrl);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());

            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if (!method.canAccess(classLoader)) {
                method.trySetAccessible();
            }

            method.invoke(classLoader, pluginDir.toURI().toURL());
            return classLoader;
        } catch (Exception e) {
            System.out.println("ClassLoader-error : " + e);
            return null;
        }
    }


    /*
     * This method loads the existing plugin classes from given path at Spring Boot startup time.
     */
    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassLoader classLoader = getSystemClassLoader();
        Class<?> clazz = classLoader.loadClass(pluginPackage);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(clazz.getName(), beanDefinition);
    }

}
