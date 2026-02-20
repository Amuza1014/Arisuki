package com.Arisuki.log.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.Arisuki.log.entity.InformationEntity;
import com.Arisuki.log.repository.BookRepository;

@Controller
public class BookController {

	@Autowired
	private BookRepository repository; // 追加

	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}
	
	@PostMapping("login")
	public String loginSuccess() {
		return "form";
	}

	// 1. 入力画面を表示する
	@GetMapping("/")
	public String input() {
		return "login";
	}
	
//	// ログイン画面からマイページへ遷移
//	@PostMapping("/mypage")
//	public String loginToMypage() {
//		return "mypage";
//	}

	// 2. データを保存して完了画面を表示する
	@PostMapping("/complete")
	public String result(InformationEntity book, Model model) {
		
		// --- 追加: カンマ連結を防ぐクレンジング処理 ---
		book.setCreator(cleanComma(book.getCreator()));
		book.setCategory(cleanComma(book.getCategory()));
		book.setPublisher(cleanComma(book.getPublisher()));
		book.setSubAttribute(cleanComma(book.getSubAttribute()));
		// ------------------------------------------

		repository.save(book); // H2 DBへ保存
		model.addAttribute("book", book);
		return "complete"; // complete.htmlを表示
	}

	@GetMapping("/mypage")
	public String mypage(Model model) {
		// DBからすべてのデータを取得してリストに入れる
		List<InformationEntity> bookList = repository.findAll();
		// HTML（Thymeleaf）に "bookList" という名前でリストを渡す
		model.addAttribute("bookList", bookList);
		// mypage.html を呼び出す
		return "mypage";
	}
	
	@GetMapping("/form")
	public String form() {
		return "form";
	}
	

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Integer id, Model model) {
		// IDを元にデータを1件取得（なければマイページへリダイレクト）
		InformationEntity book = repository.findById(id).orElse(null);
		if (book == null) {
			return "redirect:/mypage";
		}
		model.addAttribute("book", book);
		return "detail"; // detail.htmlを表示
	}

	@PostMapping("/delete/{id}")
	public String deleteBook(@PathVariable("id") Integer id) {
		// 1. サービスを呼び出して削除を実行
		repository.deleteById(id);
		// 2. 削除後は一覧画面などにリダイレクト
		return "redirect:/mypage";
	}
	
	@GetMapping("/edit/{id}")
	public String editBook(@PathVariable("id") Integer id,Model model) {
		// 1. URLのIDを使って、データベースから1件だけ作品(BookEntity)を取り出す
		// .orElseThrow() は「もしデータがなかったらエラーにするよ」という指示です
		InformationEntity book = repository.findById(id).orElseThrow();
		
		// 2. 取り出したデータを、HTML（Thymeleaf）に「book」という名前で渡す
		model.addAttribute("book", book);
		//ダミーコメント
		// 3. 編集用のHTMLファイルを表示する
		return "edit";
	}

	/**
	 * Spring MVCの仕様で同じ名前の入力項目が複数送られてきた際、
	 * カンマ区切りで連結されてしまうのを防ぐための補助メソッドです。
	 */
	private String cleanComma(String str) {
		if (str == null || str.isEmpty()) {
			return "";
		}
		// カンマで分割し、最初に見つかった「空文字でない値」を返します
		String[] parts = str.split(",");
		for (String part : parts) {
			String trimmed = part.trim();
			if (!trimmed.isEmpty()) {
				return trimmed;
			}
		}
		return "";
	}

}