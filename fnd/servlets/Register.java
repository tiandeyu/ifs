/*
 *                 IFS Research & Development
 *
 *  This program is protected by copyright law and by international
 *  conventions. All licensing, renting, lending or copying (including
 *  for private use), and all other use of the program, which is not
 *  expressively permitted by IFS Research & Development (IFS), is a
 *  violation of the rights of IFS. Such violations will be reported to the
 *  appropriate authorities.
 *
 *  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
 *  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
 * ----------------------------------------------------------------------------
 * File        : Register.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Johan S  2002-Nov-.. - Created
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class Register extends HttpServlet
{
    char sep = (byte) 31;

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      response.setContentType("text/html");
      PrintWriter resp = response.getWriter();

      String site_path = getServletContext().getRealPath("");
      String file_path = getServletContext().getInitParameter("config_path");
      
      if(!site_path.endsWith(File.separator))
          site_path += File.separator;

      resp.println(site_path+sep+file_path);
   }
}
