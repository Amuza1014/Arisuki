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
import com.Arisuki.log.entity.UserEntity;
import com.Arisuki.log.repository.ItemRepository;
import com.Arisuki.log.repository.UserRepository;

@Controller
public class ItemController {

	@Autowired
	private ItemRepository repository;

	@Autowired
	private UserRepository userRepository;

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

		// 1. DBからユーザー名をキーに検索
		return userRepository.findByUsername(username)
				.map(user -> {
					// 2. パスワードの照合
					if (user.getPassword().equals(password)) {
						// 3. 成功：セッションにユーザー「Entityまるごと」を保存
						session.setAttribute("user", user);
						return "redirect:/mypage";
					} else {
						model.addAttribute("error", "パスワードが違います");
						return "login";
					}
				})
				.orElseGet(() -> {
					// ユーザーが見つからない場合
					model.addAttribute("error", "ユーザーが見つかりません");
					return "login";
				});
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
		// 1. セッションからログインユーザー(UserEntity)を取得
		UserEntity loginUser = (UserEntity) session.getAttribute("user");

		// 【門番】ログインチェック
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 2. ログインユーザーのIDに紐づく投稿だけをリポジトリから取得
		// ※ repository は ItemRepository を指していると想定しています
		List<InformationEntity> itemList = repository.findByUserId(loginUser.getId());

		model.addAttribute("itemList", itemList);
		return "mypage";
	}

	@GetMapping("/form")
	public String form(HttpSession session) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";
		return "form";
	}

	@GetMapping("/timeline")
	public String timeline(HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		// タイムラインは「全員の投稿」が見える場所なので findAll() のままでOK！
		List<InformationEntity> list = repository.findAll();
		model.addAttribute("sukiList", list);
		return "timeline";
	}

	@PostMapping("/complete")
	public String result(InformationEntity item, HttpSession session, Model model) {
		// セッションからログイン中のユーザーを取得
		UserEntity loginUser = (UserEntity) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/login";

		// 投稿データにユーザーをセット（ここがリレーションの肝！）
		item.setUser(loginUser);

		// カンマ除去などの既存処理
		item.setCreator(cleanComma(item.getCreator()));
		// ... 他の項目も同様 ...

		repository.save(item);
		model.addAttribute("item", item);
		return "complete";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Integer id, HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		InformationEntity item = repository.findById(id).orElse(null);
		if (item == null) {
			return "redirect:/mypage";
		}
		model.addAttribute("item", item);
		return "detail";
	}

	@PostMapping("/delete/{id}")
	public String deleteItem(@PathVariable("id") Integer id, HttpSession session) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		repository.deleteById(id);
		return "redirect:/mypage";
	}

	@GetMapping("/edit/{id}")
	public String editItem(@PathVariable("id") Integer id, HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

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
		UserEntity newUser = new UserEntity();
		newUser.setUsername(username);
		newUser.setPassword(password); // ※現在は生パスワード。後で暗号化します。
		newUser.setRole("ROLE_USER");
		newUser.setDisplayName(username); // Usernameを初期値としてセット

		// 2. DBへ保存
		userRepository.save(newUser);

		// 3. ログイン画面へリダイレクト
		return "redirect:/login";
	}

}