package com.Arisuki.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.Arisuki.log.entity.BookEntity;
import com.Arisuki.log.repository.BookRepository;

@Controller
public class BookController {

    @Autowired
    private BookRepository repository; // 追加

    // 1. 入力画面を表示する
    @GetMapping("/") 
    public String input() {
        return "form"; 
    }

    // 2. データを保存して完了画面を表示する
    @PostMapping("/complete")
    public String result(BookEntity book, Model model) {
        repository.save(book); // H2 DBへ保存
        model.addAttribute("book", book);
        return "complete"; // complete.htmlを表示
    }
}