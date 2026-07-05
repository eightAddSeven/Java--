package com.example;

// 基础 Application 类
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
// 布局相关 (Layout)
import javafx.scene.layout.BorderPane;
// 控件相关 (Controls)
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.SplitPane;
// 文件读取相关
import javafx.stage.FileChooser; 
import java.io.File;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.File;



/**
 * JavaFX App
 */
public class App extends Application {
    //采用递归获取当前目录下的文件
    // 将 TreeItem 的泛型从 <String> 改为 <File>
    private void createTree(File file, TreeItem<File> parent) {
        if (file.isDirectory()) {
            TreeItem<File> treeItem = new TreeItem<>(file); // 直接存 file 对象
            parent.getChildren().add(treeItem);
        
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    createTree(subFile, treeItem);
                }
            }
        } else {
            parent.getChildren().add(new TreeItem<>(file)); // 直接存 file 对象
        }
    }

    // 实现编译功能
    private void compileJavaFile(File file) {
        if (file == null) return;
    
        try {
            // 创建进程：javac <文件路径>
            ProcessBuilder sb = new ProcessBuilder("javac", file.getAbsolutePath());
            Process process = sb.start();
        
            // 等待编译结束并获取状态码 (0 表示成功)
            int exitCode = process.waitFor();
        
            if (exitCode == 0) {
                System.out.println("编译成功！");
            } else {
                // 如果失败，读取 javac 的错误输出
                String error = new String(process.getErrorStream().readAllBytes());
                System.err.println("编译失败：\n" + error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 实现运行功能
    private void runJavaFile(File file) {
        if (file == null) return;

        try {
            // 运行 Java 需要在文件所在的文件夹下执行
            // 命令：java -cp <目录> <类名>
            String className = file.getName().replace(".java", "");
            ProcessBuilder pb = new ProcessBuilder("java", className);
            pb.directory(file.getParentFile()); // 设置工作目录

            Process process = pb.start();

            // 读取程序的标准输出并打印到控制台（后续可以显示在界面的底部面板）
            String output = new String(process.getInputStream().readAllBytes());
            System.out.println("程序输出：\n" + output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 实现保存功能
    private void saveJavaFile(File file, String content) {
        if (file == null) {
            System.out.println("没有打开的文件，无法保存");
            return;
        }

        try {
            // 使用 Files.writeString 覆盖写入文件
            // StandardCharsets.UTF_8 确保中文不会乱码
            Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
            System.out.println("文件已成功保存: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("保存文件出错: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) {
        final File[] currentFile = {null};

        //创建组件

        //1、创建编辑区
        TextArea textArea=new TextArea();
        textArea.setPromptText("在此编写代码");
        textArea.setStyle(
            "-fx-control-inner-background: #1e1e1e; " +
            "-fx-text-fill: #d4d4d4; " +
            "-fx-font-family: 'Consolas', 'Monaco', 'monospace'; " +
            "-fx-font-size: 14px; " +
            "-fx-highlight-fill: #264f78; " + // 选中文字的背景色
            "-fx-border-color: transparent;"
        );

        //2、创建菜单栏
        MenuBar menuBar=new MenuBar();
        Menu fileMenu = new Menu("文件");
        MenuItem openItem =new MenuItem("打开");
        openItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择要打开的文件");
    
            // 设置文件过滤器（可选，这样用户只能选 .java 或 .txt）
            fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("文本文件", "*.txt", "*.java", "*.md")
            );

            // 弹出窗口，注意要传入当前的 stage
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                try {
                    // Java 11 的极简读取语法：直接读取文件所有内容
                    String content = Files.readString(selectedFile.toPath(), StandardCharsets.UTF_8);
            
                    // 把内容丢进你的编辑区！
                    textArea.setText(content);
            
                    // 顺便改一下窗口标题，显示当前文件名
                    stage.setTitle("My Java Editor - " + selectedFile.getName());
            
                } catch (IOException e) {
                    // 如果读取失败（比如文件被占用），弹出一个简单的警告框
                    System.err.println("读取文件出错: " + e.getMessage());
                }
            }
        });

        MenuItem saveItem = new MenuItem("保存");
        saveItem.setOnAction(event->{
            if (currentFile[0] != null) {
                // 传入当前选中的文件，以及此时编辑区最新的文本
                saveJavaFile(currentFile[0], textArea.getText());
            } else {
                System.out.println("请先打开一个文件再尝试保存");
            }
        });

        fileMenu.getItems().add(openItem);
        fileMenu.getItems().add(saveItem);

        Menu runMenu = new Menu("调试");
        MenuItem compile = new MenuItem("编译");
        MenuItem run =new MenuItem("运行");
        runMenu.getItems().add(compile);
        runMenu.getItems().add(run);
        compile.setOnAction(e -> {
            if (currentFile[0] != null) {
                compileJavaFile(currentFile[0]);
            }
            else{
                System.out.println("请至少打开一个Java文件");
            }
        });
        run.setOnAction(e->{
            if(currentFile[0]!=null){
                runJavaFile(currentFile[0]);
            }
        });

        Menu comp =new Menu("对比");
        comp.getItems().add(new MenuItem("跳转"));

        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(runMenu);
        menuBar.getMenus().add(comp);

        //3、创建左侧侧边栏
        File projectDir =new File("D:/A-java开发/JAVA开发-实习一-197241-白佳琪");  //创建文件
        TreeItem<File> rootItem =new TreeItem<>(projectDir);
        File[] subs = projectDir.listFiles();
        if (subs != null) {
            for (File sub : subs) {
                createTree(sub, rootItem); // 从子文件/子文件夹开始递归
            }
        }

        rootItem.setExpanded((true));  //默认展开
        TreeView<File> treeView=new TreeView(rootItem);
        treeView.setPrefWidth(200);
        // 监听 TreeView 的选中项变化
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getChildren().isEmpty()) { 
                // 1. 获取节点中存的 File 对象
                File fileToOpen = newValue.getValue();
                // 2、更新当前文件
                currentFile[0] = fileToOpen;
        
                try {
                    // 2. 直接使用 fileToOpen 的路径读取
                    String content = Files.readString(fileToOpen.toPath(), StandardCharsets.UTF_8);
                    textArea.setText(content);
                    stage.setTitle("My Java Editor - " + fileToOpen.getName());
                } catch (IOException e) {
                    System.err.println("点击读取失败: " + e.getMessage());
                }
            }
        });
        treeView.setCellFactory(tv -> new TreeCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // 这一行非常重要：它保证了界面只显示文件名，而不是全路径
                    setText(item.getName()); 
                }
            }
        });
        // 优化侧边栏
        treeView.setStyle(
            "-fx-background-color: #252526; " +
            "-fx-control-inner-background: #252526; " +
            "-fx-text-fill: #cccccc; " +
            "-fx-font-size: 13px; " +
            "-fx-border-color: #333333; " +
            "-fx-border-width: 0 1 0 0;" // 仅在右侧保留一条细细的分割线
        );
        

        //4、放入容器
        SplitPane splitPane =new SplitPane();     //实现可拉动的侧边栏
        treeView.setMinWidth(100); // 防止侧边栏被缩得完全看不见
        textArea.setMinWidth(400); // 确保编辑区始终保留足够空间
        splitPane.getItems().addAll(treeView,textArea);
        splitPane.setDividerPositions(0.2);
        BorderPane rootLayout =new BorderPane();
        rootLayout.setTop(menuBar);
        rootLayout.setCenter(splitPane);
        rootLayout.setStyle("-fx-background-color: #1e1e1e;");

        //5、配置舞台
        Scene scene =new Scene(rootLayout,1500,1000);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("My Java Editor");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}