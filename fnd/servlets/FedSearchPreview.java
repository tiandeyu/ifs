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
 * File        : FedSearchPreview.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * buhilk  2010-Oct-13 - Bug Id: 93561, Implemented IFS EE links to be open from Federated search results.
 * buhilk  2010-Feb-18 - Created
 */
package ifs.fnd.servlets;

import ifs.fnd.asp.ASPManager;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.IfsNames;
import ifs.fnd.util.Str;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author buhilk
 */
public class FedSearchPreview extends HttpServlet {

    /**
     * Constructor
     */
    public FedSearchPreview() {
        System.out.println("Starting servlet ifs.fnd.servlets.FedSearchPreview");
    }

    /**
     *
     * @param req HttpServletRequest by post
     * @param resp HttpServletResponse for post request
     * @throws ServletException
     * @throws IOException
     */
    //@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     *
     * @param req HttpServletRequest by get
     * @param resp HttpServletResponse for get request
     * @throws ServletException
     * @throws IOException
     */
    //@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        printContents(req, resp);
    }

    private void printContents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean summery = "Y".equals(req.getParameter("SUMMARY"));

        ASPManager mgr = new ASPManager();

        String domains = java.net.URLDecoder.decode(req.getParameter("DOMAINS"), "UTF-8");
        String title = java.net.URLDecoder.decode(req.getParameter("TITLE"), "UTF-8");
        String css = java.net.URLDecoder.decode(req.getParameter("CSS"), "UTF-8");
        String desc = java.net.URLDecoder.decode(req.getParameter("DESC"), "UTF-8");
        String link = (!summery) ? java.net.URLDecoder.decode(req.getParameter("LINK"), "UTF-8") : "";
        String clients = (!summery) ? java.net.URLDecoder.decode(req.getParameter("CLIENTS"), "UTF-8") : "";
        String urlWeb = (!summery) ? java.net.URLDecoder.decode(req.getParameter("WEBURL"), "UTF-8") : "";
        String urlEE = (!summery) ? java.net.URLDecoder.decode(req.getParameter("EEURL"), "UTF-8") : "";
        String view = req.getParameter("VIEW");
        String keys = mgr.URLDecode(req.getParameter("KEYS"));

        StringTokenizer dlist = new StringTokenizer(domains, ",");

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();


        AutoString preview = new AutoString();
        preview.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.1//EN' 'http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd'>\n");
        preview.append("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en'>\n");
        preview.append("<head>\n");
        preview.append("<title>" + title + "</title>\n");
        preview.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />\n");
        preview.append("<link rel='STYLESHEET' href='" + css + "' type='text/css'/>\n");
        preview.append("</head>\n");
        preview.append("<body>\n");
        preview.append("<div id='header'>\n");
        preview.append("<h1>" + title + "</h1>\n");
        preview.append("</div>\n");
        if (!Str.isEmpty(clients.trim())) {
            boolean draw_seperator = false;
            preview.append("<p>\n");
            if (clients.contains("WEB")) {
                String params = "?__APP_SEARCH=Y&SEARCH_DOMAIN=" + domains + "&VIEW=" + view + "&KEYS=" + mgr.URLEncode(keys);
                preview.append("<a href='" + urlWeb.substring(urlWeb.indexOf("^") + 1) + params + "'>" + urlWeb.substring(0, urlWeb.indexOf("^")) + "</a>");
                draw_seperator = true;
            }
            if (clients.contains("WIN")) {
                int keyCount = 0;
                String WinKeyList = "";
                StringTokenizer st = new StringTokenizer(keys, "" + IfsNames.recordSeparator);
                while (st.hasMoreElements()) {
                    StringTokenizer nameValue = new StringTokenizer((String) st.nextElement(), "" + IfsNames.fieldSeparator);
                    String name = (String) nameValue.nextElement();
                    String value = (String) nameValue.nextElement();

                    value = value.replaceAll("&", "\\&");
                    value = value.replaceAll("=", "\\=");
                    WinKeyList += (keyCount++ > 1) ? "&" : "";
                    WinKeyList += name + "=" + value;
                }
                try {
                    WinKeyList = java.net.URLEncoder.encode(WinKeyList, "UTF-8");
                    WinKeyList = java.net.URLEncoder.encode("ifshome:view." + view + "?external_search=" + WinKeyList, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                if(draw_seperator) preview.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
                preview.append("<a href='" + urlEE.substring(urlEE.indexOf("^") + 1) + "?url=" + WinKeyList + "'>" + urlEE.substring(0, urlEE.indexOf("^")) + "</a>\n");
            }
            preview.append("<br></p>\n");
        }
        preview.append("<div id='search_summary'>\n");
        preview.append("<p>" + desc + "</p>\n");
        preview.append("</div>\n");
        preview.append("<div id='search_domains'>\n");
        preview.append("<dl>\n");
        if (summery) {
            while (dlist.hasMoreTokens()) {
                preview.append("<dt>" + dlist.nextToken() + "</dt>\n");
            }
        } else {
            preview.append("<dt><a href=\"" + link.substring(link.indexOf("^") + 1) + "&CLIENTS=" + mgr.URLEncode(clients) + "&VIEW=" + view + "&KEYS=" + mgr.URLEncode(keys) + "\">" + link.substring(0, link.indexOf("^")) + "</a></dt>\n");
        }
        preview.append("</dl>\n");
        preview.append("</div>\n");
        preview.append("</body>\n");
        preview.append("</html>\n");

        out.println(preview);
    }
}
