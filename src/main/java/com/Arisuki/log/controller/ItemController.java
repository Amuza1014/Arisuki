package com.Arisuki.log.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import com.Arisuki.log.repository.ItemRepository;

@Controller
public class ItemController {

	@Autowired
	private ItemRepository repository;

	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}

	@PostMapping("login")
	public String loginSuccess() {
		return "form";
	}

	@GetMapping("/")
	public String input() {
		return "login";
	}

	@GetMapping("/timeline")
	public String timeline(Model model) {
		List<InformationEntity> list = repository.findAll();
		model.addAttribute("sukiList", list);
		return "timeline";
	}

	// ------------------- 画像アップロード対応 -------------------
	@PostMapping("/complete")
	public String result(@ModelAttribute InformationEntity item,
			@RequestParam(value = "thumbnail", required = false) MultipartFile file,
			Model model) {

		if (file != null && !file.isEmpty()) {
			String uploadDir = new File("src/main/resources/static/uploads/images").getAbsolutePath();
			new File(uploadDir).mkdirs();
			File dest = new File(uploadDir, file.getOriginalFilename());
			try {
				file.transferTo(dest);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			item.setThumbnailUrl("/uploads/images/" + file.getOriginalFilename());
		}

		// カンマ処理
		item.setCreator(cleanComma(item.getCreator()));
		item.setCategory(cleanComma(item.getCategory()));
		item.setPublisher(cleanComma(item.getPublisher()));
		item.setSubAttribute(cleanComma(item.getSubAttribute()));

		repository.save(item);
		model.addAttribute("item", item);

		return "complete";
	}
	// -----------------------------------------------------------

	@GetMapping("/mypage")
	public String mypage(Model model) {
		List<InformationEntity> itemList = repository.findAll();
		model.addAttribute("itemList", itemList);
		return "mypage";
	}

	@GetMapping("/view/{id}")
	public String view(@PathVariable Integer id, Model model) {
		InformationEntity item = repository.findById(id).orElseThrow();
		model.addAttribute("item", item);
		return "view";
	}

	@GetMapping("/form")
	public String form() {
		return "form";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Integer id, Model model) {
		InformationEntity item = repository.findById(id).orElse(null);
		if (item == null) {
			return "redirect:/mypage";
		}
		model.addAttribute("item", item);
		return "detail";
	}

	@PostMapping("/delete/{id}")
	public String deleteItem(@PathVariable("id") Integer id) {
		repository.deleteById(id);
		return "redirect:/mypage";
	}

	@GetMapping("/edit/{id}")
	public String editItem(@PathVariable("id") Integer id, Model model) {
		InformationEntity item = repository.findById(id).orElseThrow();
		model.addAttribute("item", item);
		return "edit";
	}

	@PostMapping("/rate/{id}")
	public String rate(@PathVariable Integer id,
			@RequestParam("score") Integer score) {
		InformationEntity item = repository.findById(id).orElseThrow();
		item.setScore(score);
		repository.save(item);
		return "redirect:/view/" + id;
	}

	// ------------------- ヘルパーメソッド -------------------
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