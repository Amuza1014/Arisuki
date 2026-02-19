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
    private BookRepository repository;

<<<<<<< HEAD
    // ログイン画面を表示する
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
=======
	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}
	
	@PostMapping("login")
	public String loginSuccess() {
		return "form";
	}
>>>>>>> branch 'master' of https://github.com/Amuza1014/Arisuki.git

<<<<<<< HEAD
    // ログイン成功時、または直接フォームを開く
    @PostMapping("/login")
    public String loginSuccess() {
        return "form";
    }

    // ルートパスにアクセスした時はログイン画面へ飛ばす
    @GetMapping("/")
    public String input() {
        return "login";
    }

    // ★追加：新しく登録する画面（form.html）を直接開くための設定
    @GetMapping("/form")
    public String showForm() {
        return "form";
    }

	//	// ログイン画面からマイページへ遷移
	//	@PostMapping("/mypage")
	//	public String loginToMypage() {
	//		return "mypage";
	//	}
=======
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
>>>>>>> branch 'master' of https://github.com/Amuza1014/Arisuki.git

	// 2. データを保存して完了画面を表示する
	@PostMapping("/complete")
	public String result(InformationEntity book, Model model) {
<<<<<<< HEAD
		repository.save(book);
=======
		repository.save(book); // H2 DBへ保存
>>>>>>> branch 'master' of https://github.com/Amuza1014/Arisuki.git
		model.addAttribute("book", book);

		// 遷移先の判定ロジックを復活
		String cat = book.getCategory();
		if ("1".equals(cat))
			return "bookform";
		if ("2".equals(cat))
			return "movieform";
		if ("3".equals(cat))
			return "gameform";

		return "complete";
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

	@PostMapping("edit/{id}")
	public String editBook(@PathVariable("id") Integer id, Model model) {
		// 1. URLのIDを使って、データベースから1件だけ作品(BookEntity)を取り出す
<<<<<<< HEAD
		// .orElseThrow() は「もしデータがなかったらエラーにするよ」という指示です
		InformationEntity book = repository.findById(id).orElseThrow();

		// 2. 取り出したデータを、HTML（Thymeleaf）に「book」という名前で渡す
		model.addAttribute("book", book);
		//ダミーコメント
		// 3. 編集用のHTMLファイルを表示する
		return "edit";
=======
	    // .orElseThrow() は「もしデータがなかったらエラーにするよ」という指示です
	    InformationEntity book = repository.findById(id).orElseThrow();
	    
	    // 2. 取り出したデータを、HTML（Thymeleaf）に「book」という名前で渡す
	    model.addAttribute("book", book);
	    //ダミーコメント
	    // 3. 編集用のHTMLファイルを表示する
	    return "edit";
>>>>>>> branch 'master' of https://github.com/Amuza1014/Arisuki.git
	}

}
