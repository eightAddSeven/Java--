module com.example {
    requires javafx.controls;
    requires javafx.graphics; // 必须添加这个，因为 App 继承自 Application
    
    // requires org.fxmisc.richtext;
    // requires reactfx; // 关键：尝试去掉 org. 前缀，很多自动模块识别为这个名字

    opens com.example to javafx.graphics, javafx.fxml;
    exports com.example;
}