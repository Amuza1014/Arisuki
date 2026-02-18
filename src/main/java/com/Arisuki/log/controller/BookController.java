package com.Arisuki.log.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.Arisuki.log.entity.BookEntity;
import com.Arisuki.log.repository.BookRepository;

@Controller
public class BookController {

    @Autowired
    private BookRepository repository; // 追加

	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}
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
    
    @GetMapping("/mypage")
    public String mypage(Model model) {
        // DBからすべてのデータを取得してリストに入れる
        List<BookEntity> bookList = repository.findAll();
        // HTML（Thymeleaf）に "bookList" という名前でリストを渡す
        model.addAttribute("bookList", bookList);
        // mypage.html を呼び出す
        return "mypage";
    }
    
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        // IDを元にデータを1件取得（なければマイページへリダイレクト）
        BookEntity book = repository.findById(id).orElse(null);
        if (book == null) {
            return "redirect:/mypage";
        }
        model.addAttribute("book", book);
        return "detail"; // detail.htmlを表示
    }

}