package com.Arisuki.log.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.Arisuki.log.entity.InformationEntity;
import com.Arisuki.log.entity.UserEntity;
import com.Arisuki.log.repository.ItemRepository;
import com.Arisuki.log.repository.UserRepository;
import com.Arisuki.log.service.CloudinaryService; // 追加

@Controller
public class ItemController {

	@Autowired
	private ItemRepository repository;

	@Autowired
	private UserRepository userRepository;

	// --- ルート ---
	//	@GetMapping("/")
	//	public String root() {
	//		return "redirect:/login";
	//	}
	// --- ルート ---
	@GetMapping("/")
	public String input() {
		return "redirect:/login";
	}

	@Autowired
	private CloudinaryService cloudinaryService; // 追加

	// --- ログイン関連の処理 ---

	// --- ログイン・ログアウト ---
	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}

	@PostMapping("/login")
	public String loginSuccess(@RequestParam String username,
			@RequestParam String password,
			HttpSession session,
			Model model) {
		return userRepository.findByUsername(username)
				.map(user -> {
					if (user.getPassword().equals(password)) { // 後で暗号化予定
						session.setAttribute("user", user);
						return "redirect:/mypage";
					} else {
						model.addAttribute("error", "パスワードが違います");
						return "login";
					}
				})
				.orElseGet(() -> {
					model.addAttribute("error", "ユーザーが見つかりません");
					return "login";
				});
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	// --- サインアップ ---
	@GetMapping("/signup")
	public String signupForm() {
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@RequestParam String username, @RequestParam String password) {
		UserEntity newUser = new UserEntity();
		newUser.setUsername(username);
		newUser.setPassword(password); // 後で暗号化予定
		newUser.setRole("ROLE_USER");
		newUser.setDisplayName(username);

		userRepository.save(newUser);
		return "redirect:/login";
	}

	// --- マイページ ---
	@GetMapping("/mypage")
	public String mypage(HttpSession session, Model model) {
		UserEntity loginUser = (UserEntity) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/login";

		List<InformationEntity> itemList = repository.findByUserId(loginUser.getId());
		model.addAttribute("itemList", itemList);
		return "mypage";
	}

	// --- 投稿 ---
	@GetMapping("/form")
	public String form(HttpSession session) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";
		return "form";
	}

	@PostMapping("/complete")
	public String complete(@ModelAttribute InformationEntity item,
			@RequestParam(value = "thumbnail",required = false) MultipartFile file,
			HttpSession session,
			Model model) {

		UserEntity loginUser = (UserEntity) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/login";

		item.setUser(loginUser);
		// ===== 既存データ取得（editのときだけ）=====
		InformationEntity dbItem = null;
		if (item.getId() != null) {
			dbItem = repository.findById(item.getId()).orElse(null);
		}
		// ===== 画像アップロード (Cloudinary版へ差し替え) =====
		if (file != null &&!file.isEmpty()) {
			try {
				// CloudinaryServiceを使用してアップロードし、返ってきたURLを保持
				String imageUrl = cloudinaryService.uploadImage(file);
				item.setThumbnailUrl(imageUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// ===== ローカル保存版（旧仕様：一応残す）=====
			// item.setThumbnailUrl("/uploads/images/" + file.getOriginalFilename());

		} else if (item.getThumbnailUrl() == null || item.getThumbnailUrl().isBlank()) {

			// ===== ローカル版の旧分岐（参考用）=====
			// else if ((item.getThumbnailUrl() == null || item.getThumbnailUrl().isBlank()) && dbItem != null) {
			//     item.setThumbnailUrl(dbItem.getThumbnailUrl());
			// }

			// 現在の編集対応処理
			if (dbItem != null) {
				item.setThumbnailUrl(dbItem.getThumbnailUrl());
			}
		}

		// 共通クレンジング
		item.setCreator(cleanComma(item.getCreator()));
		item.setCategory(cleanComma(item.getCategory()));
		item.setPublisher(cleanComma(item.getPublisher()));
		item.setSubAttribute(cleanComma(item.getSubAttribute()));

		repository.save(item);
		model.addAttribute("item", item);

		return "complete";
	}

	// --- タイムライン ---
	@GetMapping("/timeline")
	public String timeline(HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		List<InformationEntity> list = repository.findAll();
		model.addAttribute("sukiList", list);
		return "timeline";
	}

	// --- 詳細表示 ---
	@GetMapping("/view/{id}")
	public String view(@PathVariable Integer id, Model model) {
		InformationEntity item = repository.findById(id).orElseThrow();
		model.addAttribute("item", item);
		return "view";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Integer id, HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		InformationEntity item = repository.findById(id).orElse(null);
		if (item == null)
			return "redirect:/mypage";

		model.addAttribute("item", item);
		return "detail";
	}

	// --- 編集・削除 ---
	@GetMapping("/edit/{id}")
	public String editItem(@PathVariable Integer id, HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		InformationEntity item = repository.findById(id).orElseThrow();
		model.addAttribute("item", item);
		return "edit";
	}

	@PostMapping("/delete/{id}")
	public String deleteItem(@PathVariable Integer id, HttpSession session) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		repository.deleteById(id);
		return "redirect:/mypage";
	}

	// --- 評価（スコア集計） ---
	@PostMapping("/ratesuccess/{id}")
	public String ratesuccess(@PathVariable Integer id, @RequestParam("score") Integer score) {
		InformationEntity item = repository.findById(id).orElseThrow();

		if (item.getScoreSum() == null)
			item.setScoreSum(0);
		if (item.getScoreCount() == null)
			item.setScoreCount(0);

		item.setScoreSum(item.getScoreSum() + score);
		item.setScoreCount(item.getScoreCount() + 1);

		repository.save(item);
		return "ratesuccess";
	}

	@PostMapping("/ratesuccess")
	public String ratesuccessfull() {
		return "ratesuccess";
	}

	// --- 共通メソッド ---
	private String cleanComma(String str) {
		if (str == null || str.isEmpty())
			return "";

		String[] parts = str.split(",");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			String trimmed = part.trim();
			if (!trimmed.isEmpty()) {
				if (sb.length() > 0)
					sb.append(","); // 複数ある場合も維持
				sb.append(trimmed);
			}
		}
		return sb.toString();
	}
}