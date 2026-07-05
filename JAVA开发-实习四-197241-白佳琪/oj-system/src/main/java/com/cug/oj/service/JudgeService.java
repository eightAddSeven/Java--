package com.cug.oj.service;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class JudgeService {

    public String judge(String language, String code, String testInput) {
        // 1. 创建沙箱隔离目录
        String workDir = System.getProperty("user.dir") + "/temp/" + UUID.randomUUID().toString();
        File dir = new File(workDir);
        dir.mkdirs();

        // 获取当前操作系统类型，兼容 Windows 物理机开发环境
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        try {
            // 2. 根据语言动态决定源文件名
            boolean isCpp = "cpp".equals(language);
            String sourceFileName = isCpp ? "Main.cpp" : "Main.java";
            File sourceFile = new File(dir, sourceFileName);
            Files.writeString(sourceFile.toPath(), code);

            // 3. 动态构建编译命令
            ProcessBuilder compilePb;
            if (isCpp) {
                compilePb = new ProcessBuilder("g++", "-O2", "-std=c++14", "Main.cpp", "-o", "Main");
            } else {
                compilePb = new ProcessBuilder("javac", "Main.java");
            }
            
            compilePb.directory(dir);
            Process compileProcess = compilePb.start();

            boolean compiled = compileProcess.waitFor(5, TimeUnit.SECONDS);
            if (!compiled || compileProcess.exitValue() != 0) {
                return "Compile Error (编译错误):\n" + new String(compileProcess.getErrorStream().readAllBytes());
            }

            // 4. 动态构建运行命令
            ProcessBuilder runPb;
            if (isCpp) {
                // 🌟 修复点 1：使用绝对路径，彻底解决 Windows 底层 CreateProcess 找不到文件的问题
                String exeName = isWindows ? "Main.exe" : "Main";
                String absolutePath = new File(dir, exeName).getAbsolutePath();
                runPb = new ProcessBuilder(absolutePath);
            } else {
                runPb = new ProcessBuilder("java", "Main");
            }
            
            runPb.directory(dir);
            Process runProcess = runPb.start();

            // 5. 🌟 修复点 2：将 try-with-resources 提至最外层，强制发送 EOF 信号
            // 无论用户是否填写了测试用例，os 都会在代码块结束时自动 close()
            // 这相当于给 C/C++ 进程发送了一个 Ctrl+Z/Ctrl+D，直接打断 cin 的挂起死等！
            try (OutputStream os = runProcess.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                
                if (testInput != null && !testInput.isEmpty()) {
                    writer.write(testInput);
                    writer.write("\n"); // 强行补一个回车，防止缓冲区未刷新
                    writer.flush();
                }
            } // <--- 就是在这里，水管被无情切断，子进程知道“没数据了”

            // 6. 超时与结果捕获
            boolean finished = runProcess.waitFor(3, TimeUnit.SECONDS);
            if (!finished) {
                runProcess.destroy();
                return "Time Limit Exceeded (运行超时：代码中可能存在死循环或死等输入)";
            }

            if (runProcess.exitValue() != 0) {
                return "Runtime Error (运行出错):\n" + new String(runProcess.getErrorStream().readAllBytes());
            }

            String output = new String(runProcess.getInputStream().readAllBytes());
            return output.isEmpty() ? "运行成功，但程序没有输出任何内容。" : output;

        } catch (Exception e) {
            return "System Error (判题机内部错误): " + e.getMessage();
        } finally {
            // 在生产环境中，这里应该加上删除 dir 文件夹的代码以防磁盘撑爆
            // 目前为了方便你查看生成的文件，暂不清理
        }
    }
}