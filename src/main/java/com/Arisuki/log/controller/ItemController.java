package com.Arisuki.log.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.Arisuki.log.entity.InformationEntity;
import com.Arisuki.log.repository.ItemRepository;

@Controller
public class ItemController {

	@Autowired
	private ItemRepository repository; // 追加

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
	public String result(InformationEntity item, Model model) {
		
		// --- 追加: カンマ連結を防ぐクレンジング処理 ---
		item.setCreator(cleanComma(item.getCreator()));
		item.setCategory(cleanComma(item.getCategory()));
		item.setPublisher(cleanComma(item.getPublisher()));
		item.setSubAttribute(cleanComma(item.getSubAttribute()));
		// ------------------------------------------

		repository.save(item); // H2 DBへ保存
		model.addAttribute("item", item);
		return "complete"; // complete.htmlを表示
	}

	@GetMapping("/mypage")
	public String mypage(Model model) {
		// DBからすべてのデータを取得してリストに入れる
		List<InformationEntity> itemList = repository.findAll();
		// HTML（Thymeleaf）に "itemList" という名前でリストを渡す
		model.addAttribute("itemList", itemList);
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
		InformationEntity item = repository.findById(id).orElse(null);
		if (item == null) {
			return "redirect:/mypage";
		}
		model.addAttribute("item", item);
		return "detail"; // detail.htmlを表示
	}

	@PostMapping("/delete/{id}")
	public String deleteItem(@PathVariable("id") Integer id) {
		// 1. サービスを呼び出して削除を実行
		repository.deleteById(id);
		// 2. 削除後は一覧画面などにリダイレクト
		return "redirect:/mypage";
	}
	
	@GetMapping("/edit/{id}")
	public String editItem(@PathVariable("id") Integer id,Model model) {
		// 1. URLのIDを使って、データベースから1件だけ作品(InformationEntity)を取り出す
		// .orElseThrow() は「もしデータがなかったらエラーにするよ」という指示です
		InformationEntity item = repository.findById(id).orElseThrow();
		
		// 2. 取り出したデータを、HTML（Thymeleaf）に「item」という名前で渡す
		model.addAttribute("item", item);
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