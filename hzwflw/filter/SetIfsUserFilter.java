package ifs.hzwflw.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
/**
 * 
 * @author LQW 2013Äê9ÔÂ5ÈÕ 
 *
 */
public class SetIfsUserFilter implements javax.servlet.Filter{

   public void destroy() {
      // TODO Auto-generated method stub
   }

   public void doFilter(ServletRequest request, ServletResponse response,
         FilterChain chain) throws IOException, ServletException {
      HttpServletRequest hrequest = (HttpServletRequest)request;
      System.out.println("hrequest.getQueryString() :" + hrequest.getQueryString());
      System.out.println("hrequest.getServletPath() :" + hrequest.getServletPath());
      String ivUser = hrequest.getHeader("iv-user");
      (( javax.servlet.http.HttpServletRequest )request).getSession().setAttribute("iv-user", ivUser);
      String currentUserId = (String) (( javax.servlet.http.HttpServletRequest )request).getSession().getAttribute("HZ_SESSION_USER_ID");
      if(null != currentUserId && !"".equals(currentUserId) ){
         com.horizon.organization.orgimpl.OrgSelectImpl.currentUser.set(currentUserId);
         System.out.println("+++++set THREADLOCALVARIABLE: HZ_SESSION_USER_ID = " + currentUserId);
      }
      chain.doFilter(request, response);
      com.horizon.organization.orgimpl.OrgSelectImpl.currentUser.remove();
      System.out.println("-----remove THREADLOCALVARIABLE: HZ_SESSION_USER_ID");
   }

   public void init(FilterConfig filterConfig) throws ServletException {
      // TODO Auto-generated method stub
   }

}
