package com.sample.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sample.model.Credentials;

public class AuthFilter implements Filter {

	public AuthFilter() {
	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(false);

		if (session == null) {
			Credentials credentials = getCredentials(req);
			if (credentials == null) {
				((HttpServletResponse) response).sendError(403);
				return;
			}
			if ("admin".equals(credentials.getUserName()) && "password".equals(credentials.getPassword())) {
				session = req.getSession();
				session.setAttribute("sessionCreated", true);
			} else {
				((HttpServletResponse) response).sendError(403);
				return;
			}

		}
		chain.doFilter(request, response);
	}

	private static Credentials getCredentials(HttpServletRequest req) {
		String authHeader = req.getHeader("Authorization");

		if (authHeader == null)
			return null;

		StringTokenizer st = new StringTokenizer(authHeader);
		if (st.hasMoreTokens()) {
			String basic = st.nextToken();

			if (basic.equalsIgnoreCase("Basic")) {
				try {
					String credentials = new String(Base64.getDecoder().decode(st.nextToken()), "UTF-8");
					int p = credentials.indexOf(":");
					if (p != -1) {
						String login = credentials.substring(0, p).trim();
						String password = credentials.substring(p + 1).trim();

						return new Credentials(login, password);
					}
				} catch (UnsupportedEncodingException e) {
					return null;
				}
			}
		}

		return null;

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

}
