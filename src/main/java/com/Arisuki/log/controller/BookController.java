package com.Arisuki.log.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Arisuki.log.entity.BookEntity;



@Controller
@RequestMapping("book")
public class BookController {
	
	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}
	
	// 1. 入力画面を表示する
	@GetMapping("/")
	public String input() {
		return "form"; // templates/insert-input.html を探す
	}

	// 2. 送信されたデータを受け取って結果画面を表示する
	@PostMapping("complete")
	public String result(BookEntity book, Model model) {	    
	    // まるごと画面に渡す
	    model.addAttribute("book", book);
	    return "HTML";
	}
}
