package com.example;

import java.io.File;
import java.lang.reflect.Method;
//import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务 2：自定义测试运行器
 * 使用反射机制自动扫描并运行所有的测试用例。
 */
public class MyTestRunner {
    public static void main(String[] args) {
        System.out.println("=== 启动自定义测试引擎 ===");

        // 1. 自动定位扫描目录
        // 兼容 src/main 和 src/test 的编译输出路径
        List<File> scanDirs = new ArrayList<>();
        scanDirs.add(new File("target/test-classes/com/example"));
        scanDirs.add(new File("target/classes/com/example"));

        boolean foundAny = false;
        for (File testDir : scanDirs) {
            if (testDir.exists()) {
                File[] testFiles = testDir.listFiles((dir, name) -> name.endsWith(".class"));
                if (testFiles != null && testFiles.length > 0) {
                    processFiles(testFiles);
                    foundAny = true;
                }
            }
        }

        if (!foundAny) {
            System.out.println("❌ 未找到编译后的类文件，请先执行: mvn clean compile test-compile");
        }
    }

    private static void processFiles(File[] files) {
        for (File file : files) {
            try {
                // 根据文件名还原完整类名
                String className = "com.example." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);

                // 排除当前运行器类，只处理测试类 (通常以 Test 结尾)
                if (clazz.getName().endsWith("Test")) {
                    runTestClass(clazz);
                }
            } catch (Exception e) {
                System.err.println("加载类失败: " + file.getName());
            }
        }
    }

    /**
     * 任务 2 核心：使用反射动态执行测试方法
     */
    private static void runTestClass(Class<?> clazz) {
        System.out.println("\n--- 正在扫描测试类: " + clazz.getSimpleName() + " ---");
        try {
            // 实例化测试类
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method[] methods = clazz.getDeclaredMethods();

            for (Method m : methods) {
                // 亮点：通过注解名称或方法名判定是否为测试用例，实现低耦合
                boolean isTestMethod = false;
                
                // 方式 A: 检查是否有 @Test 注解
                for (java.lang.annotation.Annotation ann : m.getAnnotations()) {
                    if (ann.annotationType().getSimpleName().equals("Test")) {
                        isTestMethod = true;
                        break;
                    }
                }
                
                // 方式 B: 检查方法名是否以 test 开头
                if (isTestMethod || m.getName().startsWith("test")) {
                    try {
                        m.setAccessible(true);
                        m.invoke(instance);
                        System.out.println("✅ " + m.getName() + " 执行成功");
                    } catch (Exception e) {
                        System.out.println("❌ " + m.getName() + " 执行失败: " + e.getCause());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("无法运行测试类: " + clazz.getName());
            e.printStackTrace();
        }
    }
}