package com.Arisuki.log.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Arisuki.log.entity.InformationEntity;
import com.Arisuki.log.repository.ItemRepository;

@Controller
public class ItemController {

	@Autowired
	private ItemRepository repository;

	// --- ログイン関連の処理 ---

	// ログイン画面を表示する
	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}
	
	// 初期アクセス（/）もログイン画面へ
	@GetMapping("/")
	public String input() {
		return "redirect:/login";
	}

	// ログイン実行処理
	@PostMapping("/login")
	public String loginSuccess(
			@RequestParam String username, 
			@RequestParam String password, 
			HttpSession session, 
			Model model) {
		
		// 【暫定認証】後ほどDB連携に書き換え可能です
		if ("admin".equals(username) && "password".equals(password)) {
			// セッションにユーザー情報を保存
			session.setAttribute("user", username);
			return "redirect:/mypage";
		} else {
			// 失敗時はメッセージを添えてログイン画面に戻る
			model.addAttribute("error", "ユーザー名またはパスワードが正しくありません");
			return "login";
		}
	}

	// ログアウト処理
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // セッション破棄
		return "redirect:/login";
	}

	// --- マイページ・データ操作関連（要ログインチェック） ---

	@GetMapping("/mypage")
	public String mypage(HttpSession session, Model model) {
		// 【門番】ログインチェック
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		List<InformationEntity> itemList = repository.findAll();
		model.addAttribute("itemList", itemList);
		return "mypage";
	}
	
	@GetMapping("/form")
	public String form(HttpSession session) {
		if (session.getAttribute("user") == null) return "redirect:/login";
		return "form";
	}

	@GetMapping("/timeline")
	public String timeline(HttpSession session, Model model) {
		if (session.getAttribute("user") == null) return "redirect:/login";
		
		List<InformationEntity> list = repository.findAll();
		model.addAttribute("sukiList", list);
		return "timeline";
	}

	@PostMapping("/complete")
	public String result(InformationEntity item, HttpSession session, Model model) {
		if (session.getAttribute("user") == null) return "redirect:/login";
		
		item.setCreator(cleanComma(item.getCreator()));
		item.setCategory(cleanComma(item.getCategory()));
		item.setPublisher(cleanComma(item.getPublisher()));
		item.setSubAttribute(cleanComma(item.getSubAttribute()));

		repository.save(item);
		model.addAttribute("item", item);
		return "complete";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Integer id, HttpSession session, Model model) {
		if (session.getAttribute("user") == null) return "redirect:/login";
		
		InformationEntity item = repository.findById(id).orElse(null);
		if (item == null) {
			return "redirect:/mypage";
		}
		model.addAttribute("item", item);
		return "detail";
	}

	@PostMapping("/delete/{id}")
	public String deleteItem(@PathVariable("id") Integer id, HttpSession session) {
		if (session.getAttribute("user") == null) return "redirect:/login";
		
		repository.deleteById(id);
		return "redirect:/mypage";
	}
	
	@GetMapping("/edit/{id}")
	public String editItem(@PathVariable("id") Integer id, HttpSession session, Model model) {
		if (session.getAttribute("user") == null) return "redirect:/login";
		
		InformationEntity item = repository.findById(id).orElseThrow();
		model.addAttribute("item", item);
		return "edit";
	}

	// --- 共通補助メソッド ---

	private String cleanComma(String str) {
		if (str == null || str.isEmpty()) {
			return "";
		}
		String[] parts = str.split(",");
		for (String part : parts) {
			String trimmed = part.trim();
			if (!trimmed.isEmpty()) {
				return trimmed;
			}
		}
		return "";
	}
	
	// 登録画面の表示
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    // 登録実行（今はまだDB保存処理がないため、ログ出力だけ行いログイン画面へ飛ばします）
    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password) {
        // TODO: ここで UserEntity を作成し、UserRepository で保存する処理を後ほど追加します
        System.out.println("新規登録リクエスト: " + username);
        
        return "redirect:/login"; 
    }
}