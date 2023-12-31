/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.ybm.dslPluginsLoader.PluginClassLoader;
import com.ybm.dslPluginsLoader.SpringBeansContextAware;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DSLPluginsRestController {
    private static final Logger LOG = LoggerFactory.getLogger(DSLPluginsRestController.class);
    @Autowired
    PluginClassLoader pluginClassLoader;

    @Autowired
    SpringBeansContextAware springBeansContextAware;

    @GetMapping("/dsl-plugins-reload/{pluginClass}")
    public Object reload(@PathVariable("pluginClass") String pluginClass) throws ClassNotFoundException {
        ClassLoader classLoader = pluginClassLoader.getSystemClassLoader();
        Class<?> clazz = classLoader.loadClass(pluginClass);
        springBeansContextAware.registerBean(clazz.getName(), clazz);
        return clazz.getName() + " bean loaded into context";
    }

}
