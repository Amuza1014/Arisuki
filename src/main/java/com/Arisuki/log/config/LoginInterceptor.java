package com.Arisuki.log.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();

		// セッションに "user" という名前でユーザーオブジェクトが保存されているかチェック
		if (session.getAttribute("user") == null) {
			// ログインしていない場合はログイン画面へリダイレクト
			response.sendRedirect("/login");
			return false; // コントローラーの処理を実行させない
		}

		return true; // ログイン済みなら通す
	}
}