package com.example.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.servlet.MySecurityFilter.UserAuthProfile;

@WebServlet(name = "HelloServlet", urlPatterns = { "/hello", "/oauth2/code/oidc" })
public class HelloServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		UserAuthProfile up = (UserAuthProfile) request.getSession().getAttribute("userAuthProfile");

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Hello Servlet</title></head><body>");
		out.println("<h1>Hello, Servlet!</h1>");
		out.println("<h2>Hello, " + up.toString() + "</h2>");
		out.println("</body></html>");
	}
}
