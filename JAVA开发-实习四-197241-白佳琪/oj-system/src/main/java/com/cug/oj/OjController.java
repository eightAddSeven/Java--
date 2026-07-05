package com.cug.oj;

import com.cug.oj.service.JudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OjController {

    @Autowired
    private JudgeService judgeService;

    // 访问主页
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 接收前端表单提交
    @PostMapping("/submit")
    public String submitCode(
            @RequestParam("language") String language, // 🌟 新增：接收语言选项
            @RequestParam("code") String code, 
            @RequestParam(value = "input", required = false) String input, 
            Model model) {
        
        // 把 language 也传给判题引擎
        String realResult = judgeService.judge(language, code, input); 
        
        // 传回给前端，保持页面的输入状态
        model.addAttribute("result", realResult);
        model.addAttribute("code", code);
        model.addAttribute("input", input);
        model.addAttribute("language", language); // 🌟 新增：保持下拉框的选择状态
        
        return "index";
    }
}