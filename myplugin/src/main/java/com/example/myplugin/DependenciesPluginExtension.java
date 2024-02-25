package com.example.myplugin;

import org.gradle.api.provider.Property;

/**
 * desc: DependenciesPluginExtension
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/9/20 17:39
 */
public interface DependenciesPluginExtension {
    Property<Boolean> getEnable();
}
