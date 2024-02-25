package com.example.myplugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ModuleVersionIdentifier;

import java.util.ArrayList;
import java.util.List;

class DependenciesPlugin implements Plugin<Project> {
    private static final String TAG = "DependenciesPlugin ===> ";

    @Override
    public void apply(Project project) {
        System.out.println(TAG + this.getClass().getName());
        DependenciesPluginExtension extension = project.getExtensions().create("printDependencies", DependenciesPluginExtension.class);
        /*
         * 扩展的配置要在 project.afterEvaluate 之后获取哦
         * 因为配置阶段完成，才能读取参数
         * 且配置完成，才能拿到所有的依赖
         */
        project.afterEvaluate(project1 -> {
            // 默认开启打印
            extension.getEnable().convention(false);
            if (extension.getEnable().get()) {
                System.out.println(TAG + "已开启依赖打印");
                AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
                appExtension.getApplicationVariants().all(applicationVariant -> {
                    System.out.println(TAG + "applicationVariant.getName() = " + applicationVariant.getName());
                    // 方式一：build.gradle 文件中添加的依赖
                    Configuration configuration = project.getConfigurations().getByName(applicationVariant.getName() + "CompileClasspath");
                    DependencySet allDependencies = configuration.getAllDependencies();
//                    for (Dependency dependency : allDependencies) {
//                        System.out.println(TAG + "dependency ===>" + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion());
//                    }
                    List<String> androidLibs = new ArrayList<>();
                    List<String> otherLibs = new ArrayList<>();
                    // 方式二：所有的依赖，包括依赖中的依赖
                    configuration.getResolvedConfiguration().getLenientConfiguration().getAllModuleDependencies().forEach(resolvedDependency -> {
                        ModuleVersionIdentifier identifier = resolvedDependency.getModule().getId();
                        //System.out.println(TAG + "identifier === " + identifier.getGroup() + ":" + identifier.getName() + ":" + identifier.getVersion());
                        if (identifier.getGroup().contains("androidx") || identifier.getGroup().contains("com.google") || identifier.getGroup().contains("org.jetbrains")) {
                            androidLibs.add(identifier.getGroup() + ":" + identifier.getName() + ":" + identifier.getVersion());
                        } else {
                            otherLibs.add(identifier.getGroup() + ":" + identifier.getName() + ":" + identifier.getVersion());
                        }
                    });
                    System.out.println("--------------官方库 start--------------");
                    androidLibs.forEach(System.out::println);
                    System.out.println("--------------官方库 end--------------");

                    System.out.println("--------------三方库 start--------------");
                    otherLibs.forEach(System.out::println);
                    System.out.println("--------------三方库 end--------------");
                });
            } else {
                System.out.println(TAG + "已关闭依赖打印");
            }
        });
    }
}