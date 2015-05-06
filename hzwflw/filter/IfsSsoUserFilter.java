package ifs.hzwflw.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class IfsSsoUserFilter implements javax.servlet.Filter{

	   public void destroy() {
	   }

	   public void doFilter(ServletRequest request, ServletResponse response,
	         FilterChain chain) throws IOException, ServletException {
	      HttpServletRequest hrequest = (HttpServletRequest)request;
	      String ivUserFromSession = (String) (( javax.servlet.http.HttpServletRequest )request).getSession().getAttribute("iv-user");
	      System.out.println("+++++++++++++++++++++++++++++++++++++++++++set ivUser: ivUser = " + ivUserFromSession);
	      String ivUser = hrequest.getHeader("iv-user");
	      if(ivUser != null){
	    	  (( javax.servlet.http.HttpServletRequest )request).getSession().setAttribute("iv-user", ivUser);
	    	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++set ivUser: ivUser = " + ivUser);
	      }
	      chain.doFilter(request, response);
	   }

	   public void init(FilterConfig filterConfig) throws ServletException {
	   }

	}
