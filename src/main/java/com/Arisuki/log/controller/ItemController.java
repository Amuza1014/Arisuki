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
	
	   @GetMapping("/view/{id}")
	public String view(@PathVariable Integer id, Model model) {

	    InformationEntity item = repository.findById(id)
	            .orElseThrow();

	    model.addAttribute("item", item);

	    return "view";
	}
    @PostMapping("/rate/{id}")
	public String rate(@PathVariable Integer id,
	                   @RequestParam("score") Integer score) {

	    InformationEntity item =
	       repository.findById(id).orElseThrow();

	    item.setScore(score);

	    repository.save(item);

	    return "redirect:/view/" + id;
	}
	
	@PostMapping("/ratesuccess/{id}")
	public String ratesuccess(@PathVariable Integer id,
            @RequestParam("score") Integer score) {
		

	    InformationEntity item =
	       repository.findById(id).orElseThrow();
	    
	    if(item.getScoreSum() == null) {
	    	item.setScoreSum(0);
	    }
	    if(item.getScoreCount() == null) {
	    	item.setScoreCount(0);
	    }
	    item.setScoreSum(item.getScoreSum() + score);
	    item.setScoreCount(item.getScoreCount() + 1);
	    
	   // item.setScore(score);

	    repository.save(item);
		
		return "ratesuccess";
	}
	
	@PostMapping("/ratesuccess")
	public String ratesuccessfull() {
		return "ratesuccess";
	}
	
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

//	@Autowired
//	private ItemRepository repository; // 追加
//
//	@GetMapping("/login")
//	public String loginForm() {
//		return "login";
//	}
//	
//	@PostMapping("login")
//	public String loginSuccess() {
//		return "form";
//	}
//
//	// 1. 入力画面を表示する
//	@GetMapping("/")
//	public String input() {
//		return "login";
//	}
//	
////	// ログイン画面からマイページへ遷移
////	@PostMapping("/mypage")
////	public String loginToMypage() {
////		return "mypage";
////	}
//	@GetMapping("/timeline")
//	public String timeline(Model model) {
//	    List<InformationEntity> list = repository.findAll();
//	    model.addAttribute("sukiList", list);
//	    return "timeline";
//	}
//	// 2. データを保存して完了画面を表示する
//	@PostMapping("/complete")
//	public String result(InformationEntity item, Model model) {
//		
//		// --- 追加: カンマ連結を防ぐクレンジング処理 ---
//		item.setCreator(cleanComma(item.getCreator()));
//		item.setCategory(cleanComma(item.getCategory()));
//		item.setPublisher(cleanComma(item.getPublisher()));
//		item.setSubAttribute(cleanComma(item.getSubAttribute()));
//		// ------------------------------------------
//
//		repository.save(item); // H2 DBへ保存
//		model.addAttribute("item", item);
//		return "complete"; // complete.htmlを表示
//	}
//
//	@GetMapping("/mypage")
//	public String mypage(Model model) {
//		// DBからすべてのデータを取得してリストに入れる
//		List<InformationEntity> itemList = repository.findAll();
//		// HTML（Thymeleaf）に "itemList" という名前でリストを渡す
//		model.addAttribute("itemList", itemList);
//		// mypage.html を呼び出す
//		return "mypage";
//	}
//	
//	@GetMapping("/view/{id}")
//	public String view(@PathVariable Integer id, Model model) {
//
//	    InformationEntity item = repository.findById(id)
//	            .orElseThrow();
//
//	    model.addAttribute("item", item);
//
//	    return "view";
//	}
//	
//	@GetMapping("/form")
//	public String form() {
//		return "form";
//	}
//	
//
//	@GetMapping("/detail/{id}")
//	public String detail(@PathVariable("id") Integer id, Model model) {
//		// IDを元にデータを1件取得（なければマイページへリダイレクト）
//		InformationEntity item = repository.findById(id).orElse(null);
//		if (item == null) {
//			return "redirect:/mypage";
//		}
//		model.addAttribute("item", item);
//		return "detail"; // detail.htmlを表示
//	}
//
//	@PostMapping("/delete/{id}")
//	public String deleteItem(@PathVariable("id") Integer id) {
//		// 1. サービスを呼び出して削除を実行
//		repository.deleteById(id);
//		// 2. 削除後は一覧画面などにリダイレクト
//		return "redirect:/mypage";
//	}
//	
//	@GetMapping("/edit/{id}")
//	public String editItem(@PathVariable("id") Integer id,Model model) {
//		// 1. URLのIDを使って、データベースから1件だけ作品(InformationEntity)を取り出す
//		// .orElseThrow() は「もしデータがなかったらエラーにするよ」という指示です
//		InformationEntity item = repository.findById(id).orElseThrow();
//		
//		// 2. 取り出したデータを、HTML（Thymeleaf）に「item」という名前で渡す
//		model.addAttribute("item", item);
//		//ダミーコメント
//		// 3. 編集用のHTMLファイルを表示する
//		return "edit";
//	}
//	
//	@PostMapping("/rate/{id}")
//	public String rate(@PathVariable Integer id,
//	                   @RequestParam("score") Integer score) {
//
//	    InformationEntity item =
//	       repository.findById(id).orElseThrow();
//
//	    item.setScore(score);
//
//	    repository.save(item);
//
//	    return "redirect:/view/" + id;
//	}
//	
//	@PostMapping("/ratesuccess/{id}")
//	public String ratesuccess(@PathVariable Integer id,
//            @RequestParam("score") Integer score) {
//		
//
//	    InformationEntity item =
//	       repository.findById(id).orElseThrow();
//
//	    item.setScore(score);
//
//	    repository.save(item);
//		
//		return "ratesuccess";
//	}
//	
//	@PostMapping("/ratesuccess")
//	public String ratesuccessfull() {
//		return "ratesuccess";
//	}
//	
//	/**
//	 * Spring MVCの仕様で同じ名前の入力項目が複数送られてきた際、
//	 * カンマ区切りで連結されてしまうのを防ぐための補助メソッドです。
//	 */
//	private String cleanComma(String str) {
//		if (str == null || str.isEmpty()) {
//			return "";
//		}
//		// カンマで分割し、最初に見つかった「空文字でない値」を返します
//		String[] parts = str.split(",");
//		for (String part : parts) {
//			String trimmed = part.trim();
//			if (!trimmed.isEmpty()) {
//				return trimmed;
//			}
//		}
//		return "";
	}

}