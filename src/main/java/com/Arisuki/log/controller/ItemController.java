package com.Arisuki.log.controller;

import java.io.File;
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

@Controller
public class ItemController {

	@Autowired
	private ItemRepository repository;

	@Autowired
	private UserRepository userRepository;

	// --- ログイン関連 ---
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
					if (user.getPassword().equals(password)) {
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

	@GetMapping("/")
	public String root() {
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

	// --- 投稿関連 ---
	@GetMapping("/form")
	public String form(HttpSession session) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";
		return "form";
	}

	@PostMapping("/complete")
	public String complete(@ModelAttribute InformationEntity item,
			@RequestParam("thumbnail") MultipartFile file,
			HttpSession session,
			Model model) {

		UserEntity loginUser = (UserEntity) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/login";

		item.setUser(loginUser);

		// 編集時の既存データ
		InformationEntity dbItem = null;
		if (item.getId() != null) {
			dbItem = repository.findById(item.getId()).orElse(null);
		}

		// 画像アップロード処理
		if (!file.isEmpty()) {
			String uploadDir = new File("uploads/images").getAbsolutePath();
			new File(uploadDir).mkdirs();

			File dest = new File(uploadDir, file.getOriginalFilename());
			try {
				file.transferTo(dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
			item.setThumbnailUrl("/uploads/images/" + file.getOriginalFilename());
		} else if ((item.getThumbnailUrl() == null || item.getThumbnailUrl().isBlank()) && dbItem != null) {
			item.setThumbnailUrl(dbItem.getThumbnailUrl());
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

	@GetMapping("/timeline")
	public String timeline(HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		List<InformationEntity> list = repository.findAll();
		model.addAttribute("sukiList", list);
		return "timeline";
	}

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

	@PostMapping("/delete/{id}")
	public String deleteItem(@PathVariable Integer id, HttpSession session) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		repository.deleteById(id);
		return "redirect:/mypage";
	}

	@GetMapping("/edit/{id}")
	public String editItem(@PathVariable Integer id, HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		InformationEntity item = repository.findById(id).orElseThrow();
		model.addAttribute("item", item);
		return "edit";
	}

	// --- 評価関連 ---
	@PostMapping("/rate/{id}")
	public String rate(@PathVariable Integer id, @RequestParam("score") Integer score) {
		InformationEntity item = repository.findById(id).orElseThrow();
		item.setScore(score);
		repository.save(item);
		return "redirect:/view/" + id;
	}

	@PostMapping("/ratesuccess/{id}")
	public String ratesuccess(@PathVariable Integer id, @RequestParam("score") Integer score) {
		InformationEntity item = repository.findById(id).orElseThrow();
		item.setScore(score);
		repository.save(item);
		return "ratesuccess";
	}

	@PostMapping("/ratesuccess")
	public String ratesuccessfull() {
		return "ratesuccess";
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

	// --- 共通補助メソッド ---
	private String cleanComma(String str) {
		if (str == null || str.isEmpty())
			return "";

		String[] parts = str.split(",");
		for (String part : parts) {
			String trimmed = part.trim();
			if (!trimmed.isEmpty())
				return trimmed;
		}
		return "";
	}
}