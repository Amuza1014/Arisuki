package com.Arisuki.log.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.Arisuki.log.entity.CommentEntity;
import com.Arisuki.log.entity.InformationEntity;
import com.Arisuki.log.entity.LikeEntity;
import com.Arisuki.log.entity.UserEntity;
import com.Arisuki.log.repository.CommentRepository;
import com.Arisuki.log.repository.ItemRepository;
import com.Arisuki.log.repository.LikeRepository;
import com.Arisuki.log.repository.UserRepository;
import com.Arisuki.log.service.CloudinaryService;

@Controller
public class ItemController {

	@Autowired
	private ItemRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private CloudinaryService cloudinaryService;

	// --- ルート ---
	@GetMapping("/")
	public String input() {
		return "redirect:/login";
	}

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

	// --- サインアップ ---
	@GetMapping("/signup")
	public String signupForm() {
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@RequestParam String username, @RequestParam String password) {
		UserEntity newUser = new UserEntity();
		newUser.setUsername(username);
		newUser.setPassword(password);
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

	// --- ユーザー名更新処理 ---
	@PostMapping("/update-username")
	public String updateUsername(@RequestParam("newUsername") String newUsername, HttpSession session) {
		UserEntity loginUser = (UserEntity) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		loginUser.setUsername(newUsername);
		loginUser.setDisplayName(newUsername);
		userRepository.save(loginUser);

		session.setAttribute("user", loginUser);
		return "redirect:/mypage";
	}

	// --- 投稿 ---
	@GetMapping("/form")
	public String form(HttpSession session, Model model) {
	    if (session.getAttribute("user") == null)
	        return "redirect:/login";

	    model.addAttribute("item", new InformationEntity());
	    return "form";
	}

	@PostMapping("/complete")
	public String complete(@ModelAttribute InformationEntity item,
			@RequestParam(value = "thumbnail", required = false) MultipartFile file,
			HttpSession session,
			Model model) {

		UserEntity loginUser = (UserEntity) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/login";

		item.setUser(loginUser);

		boolean isEdit = (item.getId() != null);
		InformationEntity dbItem = null;
		if (isEdit) {
			dbItem = repository.findById(item.getId()).orElse(null);
		}

		if (file != null && !file.isEmpty()) {
			try {
				String imageUrl = cloudinaryService.uploadImage(file);
				item.setThumbnailUrl(imageUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (item.getThumbnailUrl() == null || item.getThumbnailUrl().isBlank()) {
			if (dbItem != null) {
				item.setThumbnailUrl(dbItem.getThumbnailUrl());
			}
		}

		item.setCreator(cleanComma(item.getCreator()));
		item.setCategory(cleanComma(item.getCategory()));
		item.setPublisher(cleanComma(item.getPublisher()));
		item.setSubAttribute(cleanComma(item.getSubAttribute()));

		repository.save(item);

		model.addAttribute("isEdit", isEdit);
		model.addAttribute("item", item);

		return "complete";
	}

	@GetMapping("/timeline")
	public String timeline(HttpSession session, Model model) {
	    if (session.getAttribute("user") == null)
	        return "redirect:/login";

	    // 方針2：単純な全件取得（カウントはEntity内のカラムを参照するため計算不要）
	    List<InformationEntity> list = repository.findAllByOrderByIdDesc();
	    model.addAttribute("sukiList", list);
	    return "timeline";
	}

	// --- 詳細表示 ---
	@GetMapping("/view/{id}")
	public String view(@PathVariable Integer id,
	                   HttpSession session,
	                   Model model) {

	    InformationEntity item = repository.findById(id).orElseThrow();
	    model.addAttribute("item", item);
	    
	    // 循環参照を避けるため、Repositoryから直接コメントリストを取得
	    List<CommentEntity> comments = commentRepository.findByInformationOrderByCreatedAtDesc(item);
	    model.addAttribute("comments", comments);

	    UserEntity user = (UserEntity) session.getAttribute("user");

	    if (user != null) {
	        CommentEntity myComment =
	                commentRepository
	                .findByInformationAndUser(item, user)
	                .orElse(null);

	        model.addAttribute("myComment", myComment);
			boolean isLiked = likeRepository.existsByInformationAndUser(item, user);
			model.addAttribute("isLiked", isLiked);
	    }

	    return "view";
	}

	// --- いいね切り替えロジック (カウント更新版) ---
	@Transactional
	@GetMapping("/like/{id}")
	public String toggleLike(@PathVariable Integer id, HttpSession session) {
		UserEntity loginUser = (UserEntity) session.getAttribute("user");
		if (loginUser == null) return "redirect:/login";

		InformationEntity item = repository.findById(id).orElseThrow();

		// 安全策：nullチェックと初期化
		if (item.getLikeCount() == null) item.setLikeCount(0);

		if (likeRepository.existsByInformationAndUser(item, loginUser)) {
			likeRepository.deleteByInformationAndUser(item, loginUser);
			// カウントを減らす
			item.setLikeCount(Math.max(0, item.getLikeCount() - 1));
		} else {
			LikeEntity like = new LikeEntity();
			like.setInformation(item);
			like.setUser(loginUser);
			likeRepository.save(like);
			// カウントを増やす
			item.setLikeCount(item.getLikeCount() + 1);
		}
		repository.save(item); // 数値を保存

		return "redirect:/view/" + id;
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Integer id, HttpSession session, Model model) {
		if (session.getAttribute("user") == null)
			return "redirect:/login";

		InformationEntity item = repository.findById(id).orElse(null);
		if (item == null)
			return "redirect:/mypage";
		
		List<CommentEntity> comments = commentRepository.findByInformationOrderByCreatedAtDesc(item);

		model.addAttribute("item", item);
		model.addAttribute("comments", comments);
		return "detail";
	}

	// コメント機能 (カウント更新版)
	@Transactional
	@PostMapping("/view/{id}")
	public String postComment(@PathVariable Integer id,
	                          @RequestParam String comment,
	                          HttpSession session) {

	    UserEntity user = (UserEntity) session.getAttribute("user");
	    if (user == null) return "redirect:/login";

	    InformationEntity item = repository.findById(id).orElseThrow();
		if (item.getCommentCount() == null) item.setCommentCount(0);

	    CommentEntity existingComment =
	            commentRepository.findByInformationAndUser(item, user)
	                             .orElse(null);

	    if (existingComment == null) {
	        // 新規作成
	        CommentEntity newComment = new CommentEntity();
	        newComment.setContent(comment);
	        newComment.setUser(user);
	        newComment.setInformation(item);
	        commentRepository.save(newComment);
	        
	        // カウントを増やす
	        item.setCommentCount(item.getCommentCount() + 1);
	        repository.save(item);
	    } else {
	        // 編集（更新）
	        existingComment.setContent(comment);
	        commentRepository.save(existingComment);
	    }

	    return "redirect:/view/" + id;
	}

	// コメント削除 (カウント更新版)
	@Transactional
	@PostMapping("/comment/delete/{id}")
	public String deleteComment(@PathVariable Integer id,
	                            HttpSession session) {

	    UserEntity user = (UserEntity) session.getAttribute("user");
	    if (user == null) return "redirect:/login";

	    CommentEntity comment = commentRepository.findById(id).orElse(null);

	    if (comment != null && comment.getUser().getId().equals(user.getId())) {
	        InformationEntity item = comment.getInformation();
	        Integer informationId = item.getId();
	        
	        commentRepository.delete(comment);
	        
	        // カウントを減らす
			if (item.getCommentCount() == null) item.setCommentCount(1);
	        item.setCommentCount(Math.max(0, item.getCommentCount() - 1));
	        repository.save(item);
	        
	        return "redirect:/view/" + informationId;
	    }

	    return "redirect:/timeline";
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
	public String ratesuccess(
			@PathVariable Integer id,
			@RequestParam("score") Integer score,
			HttpSession session) {

		UserEntity loginUser = (UserEntity) session.getAttribute("user");
		if (loginUser == null) return "redirect:/login";

		InformationEntity item = repository.findById(id).orElseThrow();
		repository.save(item);

		return "redirect:/timeline";
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
					sb.append(",");
				sb.append(trimmed);
			}
		}
		return sb.toString();
	}

	// --- マイいいね一覧 ---
	@GetMapping("/mylikes")
	public String showMyLikes(HttpSession session, Model model) {
	    UserEntity user = (UserEntity) session.getAttribute("user");
	    if (user == null) return "redirect:/login";

	    List<InformationEntity> likeList = likeRepository.findAll().stream()
	            .filter(like -> like.getUser().getId().equals(user.getId()))
	            .map(LikeEntity::getInformation)
	            .toList();

	    model.addAttribute("likeList", likeList);
	    return "my_likepage"; 
	}
}