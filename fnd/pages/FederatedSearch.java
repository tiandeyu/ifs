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
 * File        : FederatedSearch.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * buhilk  2010-Oct-13 - Bug Id: 93561, Implemented IFS EE links to be open from Federated search results.
 * buhilk  2010-Feb-18 - Created
 */
package ifs.fnd.pages;

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import ifs.fnd.asp.*;
import ifs.fnd.util.Str;
import ifs.fnd.service.*;
import ifs.fnd.buffer.Buffer;
import ifs.fnd.buffer.ItemNotFoundException;
import ifs.fnd.asp.ApplicationSearchManager.SearchDomain;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Vector;
import java.util.Locale;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;

/**
 * Implements federated search support for windows7 os integrated search with external data sources.
 * @author buhilk
 */
public class FederatedSearch extends ASPPageProvider {

    private Vector results;
    private boolean basic_login = false;

    /**
     * Constructor methos
     * @param mgr ASPManager instance
     * @param page_path request page path
     */
    public FederatedSearch(ASPManager mgr, String page_path) {
        super(mgr, page_path);
    }

    /**
     * Handle all requests from clients.
     * @throws ifs.fnd.service.FndException Exception
     */
    public void run() throws FndException {

        ASPManager mgr = getASPManager();

        basic_login = "Y".equals(mgr.getConfigParameter("ADMIN/FEDERATED_SEARCH/BASIC_LOGIN", "Y"));
        String sResponce = "";

        boolean osdx = !Str.isEmpty(mgr.getQueryStringValue("ADDFEDSEARCH"));
        boolean preview = !Str.isEmpty(mgr.getQueryStringValue("__APP_SEARCH"));
        boolean summery = (!Str.isEmpty(mgr.getQueryStringValue("SUMMARY")) && mgr.getQueryStringValue("SUMMARY").equalsIgnoreCase("Y"));

        if (osdx) {
            mgr.setAspResponsContentType("text/xml");
            mgr.setResponseContentFileName("IFS_Federated_Search_Descriptor.osdx", true);
            sResponce = addFedSearch(mgr);
        } else if (preview) {
            mgr.responseClear();
            mgr.setAspResponsContentType("text/html");
            if (summery) {
                sResponce = renderSummary(mgr, null);
            } else {
                sResponce = renderPreview(mgr);
            }
        } else {
            mgr.setAspResponsContentType("text/xml");
            sResponce = fedSearchRss(mgr);
        }

        mgr.responseWrite(sResponce);
        mgr.endResponse();
    }

    private String addFedSearch(ASPManager mgr) {
        try {
            ASPConfig config = mgr.getASPConfig();
            String xml = "";
            String baseurl = config.getProtocol() + "://" + config.getApplicationDomain() + config.getApplicationPath();

            xml += "<?xml version='1.0' encoding='UTF-8'?>\n";
            xml += "<OpenSearchDescription xmlns='http://a9.com/-/spec/opensearch/1.1/' xmlns:ms-ose='http://schemas.microsoft.com/opensearchext/2009/'>\n";
            xml += "   <ShortName>" + config.getParameter("ADMIN/INSTANCE_DESC", mgr.translate("FEDSEARCHDEFAULTNAME: IFS Applications Search - &1", mgr.getApplicationDomain())) + "</ShortName>\n";
            xml += "   <Description>" + mgr.translate("FEDSEARCHDEFAULTDESC: Search IFS Applications quickly and easily from Windows.") + "</Description>\n";
            xml += "   <Url type='application/rss+xml' template='" + baseurl + "/common/scripts/FederatedSearch.page?opensearch={searchTerms}&amp;num={count}&amp;fedsearch=Y'/>";
            xml += "</OpenSearchDescription>\n";
            return xml;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error";
        }
    }

    private void search(ASPManager mgr) throws Exception {
        String open_search_text = mgr.getQueryStringValue("opensearch");
        String output_format = mgr.getQueryStringValue("output");
        String output_specified_max_rows = mgr.getQueryStringValue("num");

        int max_rows = 20;
        boolean domains_available;
        boolean show_snippet;
        String criteria;

        String domains = "";

        try {
            ApplicationSearchManager searchManager = ApplicationSearchManager.getInstance(mgr.getASPConfig());

            domains = mgr.getASPPage().readGlobalProfileValue("Defaults/ApplicationSearch" + ProfileUtils.ENTRY_SEP + "SelectedDomains", false);
            if (mgr.isEmpty(domains)) {
                domains_available = false;
                return;
            }
            domains_available = true;
            show_snippet = mgr.getASPPage().readGlobalProfileFlag("Defaults/ApplicationSearch" + ProfileUtils.ENTRY_SEP + "ShowSnippet", false);
            criteria = open_search_text;
            max_rows = 0;

            if (!mgr.isEmpty(output_specified_max_rows)) {
                max_rows = Integer.parseInt(output_specified_max_rows);
            }

            if (max_rows == 0) {
                max_rows = Integer.parseInt(mgr.getASPPage().readGlobalProfileValue("Defaults/ApplicationSearch" + ProfileUtils.ENTRY_SEP + "MaxRows", "" + max_rows, false));
            }

            int skip_rows = 0;

            StringTokenizer st = new StringTokenizer(domains, ",");
            String[] domainArr = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreElements()) {
                domainArr[i] = (String) st.nextElement();
                i++;
            }

            results = searchManager.search(mgr.getUserId(), domainArr, criteria, show_snippet, max_rows, skip_rows, mgr.getASPConfig(), mgr.getSessionId(), true);
        } catch (Exception any) {
            throw new Exception(any);
        }
    }

    private String fedSearchRss(ASPManager mgr) {
        ASPConfig cfg = mgr.getASPConfig();
        int size = 0;
        boolean error = false;
        String errmsg = "";

        try {
            search(mgr);
            if (results != null) {
                size = results.size();
            } else {
                size = 0;
            }
        } catch (Exception ex) {
            error = true;
            errmsg = ex.getLocalizedMessage();
            ex.printStackTrace();
        }

        String thumbnail1 = "<media:thumbnail url=\"" + cfg.getProtocol() + "://" + cfg.getApplicationDomain() + cfg.getUnsecuredImageLocation() + "SearchSummery.gif\" width=\"40\" height=\"40\"/>\n";
        String thumbnail2 = "<media:thumbnail url=\"" + cfg.getProtocol() + "://" + cfg.getApplicationDomain() + cfg.getUnsecuredImageLocation() + "FedSearchDoc.gif\" width=\"33\" height=\"42\"/>\n";
        String base_url = cfg.getProtocol() + "://" + cfg.getApplicationDomain() + cfg.getApplicationPath() + "/common/scripts/FederatedSearch.page";
        String base_url_ = cfg.getProtocol() + "://" + cfg.getApplicationDomain() + cfg.getUnsecuredApplicationPath() + "/search";
        String summary_url = (basic_login) ? base_url_ : base_url;
        if (basic_login) {
            summary_url += "?SUMMARY=Y&amp;" + renderSummary(mgr, Integer.toString(size));
        } else {
            summary_url += "?fedseach=Y&amp;__APP_SEARCH=Y&amp;SUMMARY=Y&amp;NUM_RESULTS=" + Integer.toString(size);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", new Locale(mgr.getLanguageCode()));
        String pubdate = sdf.format(Calendar.getInstance().getTime());

        String rss_feed = "";
        rss_feed += "<rss version=\"2.0\" xmlns:media=\"http://search.yahoo.com/mrss/\">\n";
        rss_feed += "<channel>\n";
        rss_feed += "<title>" + mgr.translate("FEDSEARCHSUMMARY1: Search Results") + "</title>\n";
        rss_feed += "<item>\n";
        rss_feed += thumbnail1;
        rss_feed += "<category>" + mgr.translate("FEDSEARCHSUMMARY2: Summary") + "</category>\n";
        rss_feed += "<pubDate>" + pubdate + "</pubDate>\n";
        rss_feed += "<title>" + mgr.translate("FEDSEARCHSUMMARY3: Search Result Summary") + "</title>\n";
        rss_feed += "<link>" + summary_url + "</link>\n";
        if (error) {
            rss_feed += "<description>" + mgr.translate("FEDSEARCHSUMMARYERR: The search resulted in an Error. [&1]", errmsg) + "</description>\n";
        } else {
            rss_feed += "<description>" + mgr.translate("FEDSEARCHSUMMARYDES: The search rendered &1 results", Integer.toString(size).toString()) + "</description>\n";
        }
        rss_feed += "</item>\n";

        ApplicationSearchManager searchMgr = null;

        try {
            searchMgr = ApplicationSearchManager.getInstance(cfg);
        } catch (FndException ex) {
            ex.printStackTrace();
        }

        Vector domainList = new Vector();

        try {
            domainList = UserDataCache.getInstance().getSearchDomains(mgr.getSessionId());
        } catch (FndException ex) {
            ex.printStackTrace();
        }

        String weblabel = mgr.translate("FEDSEARCHVIEWWEB: View in IFS Web Cient");
        String eelabel = mgr.translate("FEDSEARCHVIEWWIN: View in Enterprise Explorer");
        String moreinfolabel = mgr.translate("FEDSEARCHVIEWMOREINFO: More Details...");

        for (int i = 0; i < size; i++) {
            rss_feed += "<item>\n";

            ApplicationSearchManager.Result row = (ApplicationSearchManager.Result) results.elementAt(i);
            String searchDomainID = row.getSearchDomainId();
            String url = base_url + "?fedseach=Y&amp;__APP_SEARCH=Y&amp;SEARCH_DOMAIN=" + searchDomainID;
            if (!basic_login) {
                url += "&amp;CLIENTS=" + mgr.URLEncode(row.getClients())
                        + "&amp;VIEW=" + row.getView()
                        + "&amp;KEYS=" + mgr.URLEncode(row.getPrimaryKeys());
            }

            String appdomain = cfg.getProtocol() + "://" + cfg.getApplicationDomain();
            String urlWeb = appdomain + cfg.getApplicationPath() + "/";
            String urlEE = appdomain + cfg.getIFSEEPath();

            if (basic_login) {
                SearchDomain sd = searchMgr.findSearchDomain(searchDomainID, domainList);
                try {
                    url = base_url_ + "?TITLE=" + java.net.URLEncoder.encode(row.getTitle(), "UTF-8") + "&amp;"
                            + "DESC=" + java.net.URLEncoder.encode(row.getUnformattedSnippet(), "UTF-8") + "&amp;"
                            + "LINK=" + java.net.URLEncoder.encode(moreinfolabel + "^" + url, "UTF-8") + "&amp;"
                            + "CLIENTS=" + java.net.URLEncoder.encode(row.getClients(), "UTF-8") + "&amp;"
                            + "WEBURL=" + java.net.URLEncoder.encode(weblabel + "^" + urlWeb + sd.getURL(), "UTF-8") + "&amp;"
                            + "EEURL=" + java.net.URLEncoder.encode(eelabel + "^" + urlEE, "UTF-8") + "&amp;"
                            + "DOMAINS=" + java.net.URLEncoder.encode(searchDomainID, "UTF-8") + "&amp;"
                            + "VIEW=" + row.getView() + "&amp;"
                            + "KEYS=" + mgr.URLEncode(row.getPrimaryKeys()) + "&amp;"
                            + "CSS=" + java.net.URLEncoder.encode(mgr.getASPConfig().getParameter("APPLICATION/LOCATION/STYLESHEETS", "../common/stylesheets/") + "search_preview.css", "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            }

            rss_feed += thumbnail2;
            rss_feed += "<title>" + mgr.HTMLEncode(row.getTitle()) + "</title>\n";
            rss_feed += "<category>" + mgr.HTMLEncode(row.getSearchDomainId()) + "</category>\n";
            rss_feed += "<pubDate>" + pubdate + "</pubDate>\n";
            rss_feed += "<link>" + url + "</link>\n";
            rss_feed += "<description>" + mgr.HTMLEncode(row.getUnformattedSnippet()) + "</description>\n";

            rss_feed += "</item>\n";
        }

        rss_feed += "</channel>\n";
        rss_feed += "</rss>\n";
        return rss_feed;
    }

    private String renderSummary(ASPManager mgr, String results) {
        ASPConfig cfg = mgr.getASPConfig();
        String preview = "";
        String NumResults = mgr.readValue("NUM_RESULTS", results);
        String homeUrl = cfg.getProtocol() + "://" + cfg.getApplicationDomain() + cfg.getApplicationPath() + "/Default.page";
        String title = mgr.translate("FEDSEARCHPREVIEWSUMMARY: Search Summary", mgr.getASPPage());
        String cssUrl = cfg.getParameter("APPLICATION/LOCATION/STYLESHEETS", "../common/stylesheets/") + "search_preview.css";
        String domains = mgr.getASPPage().readGlobalProfileValue("Defaults/ApplicationSearch" + ProfileUtils.ENTRY_SEP + "SelectedDomains", false);
        int domCount = 0;

        if (NumResults == null) {
            NumResults = "0";
        }
        if (domains == null) {
            domains = "";
        }

        String body = mgr.translate("FEDSEARCHSUMMARYDES: The search rendered &1 results from the following domains. You can change the domains searched from your &2Personal Portal&3.", NumResults, "<a href='" + homeUrl + "'>", ".</a>");
        if (basic_login) {
            try {
                return "DESC=" + java.net.URLEncoder.encode(body, "UTF-8") + "&amp;"
                        + "TITLE=" + java.net.URLEncoder.encode(title, "UTF-8") + "&amp;"
                        + "DOMAINS=" + java.net.URLEncoder.encode(domains, "UTF-8") + "&amp;"
                        + "CSS=" + java.net.URLEncoder.encode(cssUrl, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        StringTokenizer st = new StringTokenizer(domains, ",");
        String[] domainArr = new String[st.countTokens()];

        while (st.hasMoreElements()) {
            domainArr[domCount] = (String) st.nextElement();
            domCount++;
        }

        preview += "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.1//EN' 'http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd'>\n";
        preview += "<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en'>\n";
        preview += "<head>\n";
        preview += "<title>" + title + "</title>\n";
        preview += "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />\n";
        preview += "<link rel='STYLESHEET' href='" + cssUrl + "' type='text/css'/>\n";
        preview += "</head>\n";
        preview += "<body>\n";
        preview += "<div id='header'>\n";
        preview += "<h1>" + title + "</h1>\n";
        preview += "</div>\n";
        preview += "<div id='search_summary'>\n";
        preview += "<p>" + body + "</p>\n";
        preview += "</div>\n";
        preview += "<div id='search_domains'>\n";
        preview += "<dl>\n";

        for (int i = 0; i < domCount; i++) {
            preview += "<dt>" + domainArr[i] + "</dt>\n";
        }

        preview += "</dl>\n";
        preview += "</div>\n";
        preview += "</body>\n";
        preview += "</html>\n";

        return preview;
    }

    private ASPTransactionBuffer perform(ASPTransactionBuffer trans, ASPManager mgr) throws Exception {
        ASPTransactionBuffer trans_;

        if (mgr.getASPPage().isUndefined()) {
            trans_ = mgr.performConfig(trans);
        } else {
            trans_ = mgr.performEx(trans);
        }

        return trans_;
    }

    private String getDomainData(String searchDomain, String keys, ASPManager mgr) throws Exception {
        String rowID, masterEntity;
        String indexedContent = "";

        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

        ASPCommand cmdMasterEntity = mgr.newASPCommand();
        cmdMasterEntity.defineCustomFunction("SEARCH_DOMAIN_ENTITY_API.Get_Master");
        cmdMasterEntity.addParameter("MASTER_ENTITY", "S", null, null);
        cmdMasterEntity.addParameter("SEARCH_DOMAIN", "S", "IN", searchDomain);
        trans.addCommand("SEARCH_DOMAIN_ENTITY", cmdMasterEntity);
        trans = perform(trans, mgr);

        masterEntity = trans.getBuffer("SEARCH_DOMAIN_ENTITY/DATA").getValue("MASTER_ENTITY");
        if (Str.isEmpty(masterEntity)) {
            throw new Exception(mgr.translate("FEDSEARCHGETDOMAINMASTERNULL: Search domain returned an empty entity."));
        }
        trans.clear();

        ASPCommand cmdRowID = mgr.newASPCommand();
        cmdRowID.defineCustomFunction("OBJECT_CONNECTION_SYS.Get_Rowid_From_Keyref");
        cmdRowID.addParameter("ROWID", "S", null, null);
        cmdRowID.addParameter("MASTER_ENTITY", "S", "IN", masterEntity);
        cmdRowID.addParameter("KEY_REF", "S", "IN", keys);
        trans.addCommand("SEARCH_DOMAIN_ROWID", cmdRowID);
        trans = perform(trans, mgr);

        rowID = trans.getBuffer("SEARCH_DOMAIN_ROWID/DATA").getValue("ROWID");
        if (Str.isEmpty(rowID)) {
            throw new Exception(mgr.translate("FEDSEARCHGETDOROWNULL: Search keys returned an empty row."));
        }
        trans.clear();

        ASPCommand cmdDocData = mgr.newASPCommand();
        cmdDocData.defineCustomFunction("APPLICATION_SEARCH_SYS.Get_Document_Data");
        cmdDocData.addParameter("DOC_DATA", "S", null, null);
        cmdDocData.addParameter("MASTER_ENTITY", "S", "IN", masterEntity);
        cmdDocData.addParameter("ROWID", "S", "IN", rowID);
        trans.addCommand("DOMAIN_INDEXED_DATA", cmdDocData);
        trans = perform(trans, mgr);

        indexedContent = trans.getBuffer("DOMAIN_INDEXED_DATA/DATA").getValue("DOC_DATA");

        return indexedContent;
    }

    private boolean isWhitespaceNode(Node node) {
        if (node == null) {
            return false;
        }

        if (node.getNodeType() == Node.TEXT_NODE) {
            if (node.getNodeValue().matches("\\s+")) {
                return true;
            }
        }

        return false;
    }

    private Node getNextNonWhitespaceSibling(Node node) {
        if (node == null) {
            return null;
        }

        // Go through siblings at this level skipping text nodes that are white spaces
        Node nextSibling = node.getNextSibling();
        boolean spin = true;

        while (spin) {
            if (isWhitespaceNode(nextSibling)) {
                nextSibling = nextSibling.getNextSibling();
            } else {
                spin = false;
            }
        }

        return nextSibling;
    }

    private String spacify(String text) {
        String spacifiedText = "";
        String lowcaseText = text.toLowerCase();
        // Turns "StringsLikeThis" into "Strings Like This"
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != lowcaseText.charAt(i)) {
                spacifiedText = spacifiedText + " " + text.charAt(i);
            } else {
                spacifiedText = spacifiedText + text.charAt(i);
            }
        }
        return spacifiedText.trim();
    }

    private String addHTMLHeading(String text, int level) {
        text = spacify(text);
        String tag = "H" + level;
        String header = "<" + tag + ">" + text + "</" + tag + ">\n";
        return header;
    }

    private String addHTMLField(String name, String text) {
        name = spacify(name);

        if (Str.isEmpty(text)) {
            return "";
        }

        String label = "<dt>" + name + "</dt>\n";
        String value = "<dd>" + text + "</dd>\n";
        return "<dl>\n" + label + value + "</dl>\n";
    }

    private String generateHTML(Node node, int level) {
        String htmlText = "";
        Node currentNode = node.getFirstChild();

        // Skip carriage returns and other empty spaces
        if (isWhitespaceNode(currentNode)) {
            currentNode = getNextNonWhitespaceSibling(currentNode);
        }

        while (currentNode != null) {
            String currentName = currentNode.getNodeName();
            Node childNode = currentNode.getFirstChild();

            if (isWhitespaceNode(childNode)) {
                childNode = getNextNonWhitespaceSibling(childNode);
            }

            if (childNode != null) {
                // The child node might be a text node or a sub parent element node.
                if ((childNode.getNodeType() == Node.TEXT_NODE)) {
                    String content = childNode.getTextContent();
                    htmlText += addHTMLField(currentName, childNode.getNodeValue());
                } else {
                    // This is sub parent node. Recurse.
                    htmlText += "<div class='object-child'>\n" + addHTMLHeading(currentName, level) + generateHTML(currentNode, level + 1) + "</div>\n";
                }
            }
            currentNode = getNextNonWhitespaceSibling(currentNode);
        }
        return htmlText;
    }

    private String renderPreview(ASPManager mgr) {
        String preview = "";
        ASPConfig cfg = mgr.getASPConfig();
        ApplicationSearchManager searchMgr = null;

        try {
            searchMgr = ApplicationSearchManager.getInstance(cfg);
        } catch (FndException ex) {
            ex.printStackTrace();
        }

        String SearchDomain = mgr.getQueryStringValue("SEARCH_DOMAIN");
        String IsFromAppSearch = mgr.getQueryStringValue("__APP_SEARCH");
        String View = mgr.getQueryStringValue("VIEW");
        String Keys = mgr.URLDecode(mgr.getQueryStringValue("KEYS"));
        String KeyRef = "";
        String WinKeyList = "";
        String Clients = mgr.URLDecode(mgr.getQueryStringValue("CLIENTS"));
        int keyCount = 0;

        StringTokenizer st = new StringTokenizer(Keys, "" + IfsNames.recordSeparator);
        while (st.hasMoreElements()) {
            keyCount++;
            StringTokenizer nameValue = new StringTokenizer((String) st.nextElement(), "" + IfsNames.fieldSeparator);
            String name = (String) nameValue.nextElement();
            String value = (String) nameValue.nextElement();

            KeyRef += (keyCount > 1) ? "^" : "";
            KeyRef += name + "=" + value;

            value = value.replaceAll("&", "\\&");
            value = value.replaceAll("=", "\\=");
            WinKeyList += (keyCount > 1) ? "&" : "";
            WinKeyList += name + "=" + value;
        }

        String RenderedDomainData = "";
        boolean error = false;

        try {
            String DomainData = getDomainData(SearchDomain, KeyRef, mgr);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder parser = factory.newDocumentBuilder();
            StringReader inputReader = new StringReader(DomainData);
            InputSource inputSource = new InputSource(inputReader);
            Document doc = parser.parse(inputSource);
            Node currentNode = doc.getFirstChild();

            RenderedDomainData = generateHTML(currentNode, 2);

        } catch (Exception ex) {
            error = true;
            RenderedDomainData = errorCode(ex, mgr);
        }

        preview += "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.1//EN' 'http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd'>";
        preview += "<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en'>";
        preview += "<head>";
        preview += "<title>" + mgr.translate("FEDSEARCHPREVIEW: Preview of ") + spacify(SearchDomain) + "</title>";
        preview += "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />";

        String cssUrl = cfg.getParameter("APPLICATION/LOCATION/STYLESHEETS", "../common/stylesheets/") + "search_preview.css";

        preview += "<link rel='STYLESHEET' href='" + cssUrl + "' type='text/css'/>";
        preview += "</head>";

        preview += "<body>";

        if (!error) {
            // Header
            preview += "<div id='header'>";
            preview += "<h1>" + spacify(SearchDomain) + "</h1>";
            preview += "</div>";

            // Links
            String urlWeb = "/";
            String urlWin = "/";
            String app_domain = cfg.getProtocol() + "://" + cfg.getApplicationDomain();
            Vector domainList = new Vector();

            try {
                domainList = UserDataCache.getInstance().getSearchDomains(mgr.getSessionId());
            } catch (FndException ex) {
                ex.printStackTrace();
            }

            SearchDomain sd = searchMgr.findSearchDomain(SearchDomain, domainList);

            urlWeb = app_domain + cfg.getApplicationPath() + "/" + sd.getURL();
            urlWeb += "?__APP_SEARCH=Y&amp;SEARCH_DOMAIN=" + SearchDomain
                    + "&amp;VIEW=" + View
                    + "&amp;KEYS=" + mgr.URLEncode(Keys);

            urlWin = app_domain + cfg.getIFSEEPath();
            try {
                WinKeyList = java.net.URLEncoder.encode(WinKeyList, "UTF-8");
                WinKeyList = java.net.URLEncoder.encode("ifshome:view." + View + "?external_search=" + WinKeyList, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            urlWin += "?url=" + WinKeyList;


            if (!Str.isEmpty(Clients.trim())) {
                preview += "<p>\n";
                boolean draw_seperator = false;
                if (Clients.contains("WEB")) {
                    preview += "<a href='" + urlWeb + "'>" + mgr.translate("FEDSEARCHVIEWWEB: View in IFS Web Cient") + "</a>";
                    draw_seperator = true;
                }
                if (Clients.contains("WIN")) {
                    if (draw_seperator) {
                        preview += "&nbsp;&nbsp;|&nbsp;&nbsp;";
                    }
                    preview += "<a href='" + urlWin + "'>" + mgr.translate("FEDSEARCHVIEWWIN: View in Enterprise Explorer") + "</a>\n";
                }
                preview += "</p>\n";
            }
        }

        // The data
        preview += "<div class='object'>";
        preview += RenderedDomainData;
        preview += "</div>";

        preview += "</body>";
        preview += "</html>";
        return preview;
    }

    private String errorCode(Throwable throwable, ASPManager mgr) {
        String message = "";
        String stack = "";
        if (throwable instanceof ifs.fnd.asp.ASPLog.ExtendedAbortException) {
            ifs.fnd.asp.ASPLog.ExtendedAbortException eae = (ifs.fnd.asp.ASPLog.ExtendedAbortException) throwable;
            Buffer errbuf = eae.getExtendedInfo();
            try {
                message = errbuf.getItem("ERROR_MESSAGE").getString();
                stack = errbuf.getItem("ERROR_STACK").toString();
            } catch (ItemNotFoundException ex) {
                message = throwable.getLocalizedMessage();
                StringWriter sw = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sw));
                stack = sw.toString();
            }
        } else {
            message = throwable.getLocalizedMessage();
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            stack = sw.toString();
        }

        String code = "";
        code += "<h1 class=\"error\">" + mgr.translate("FEDSEARCHERROR1: Failed to Preview Search Result.") + "</h1>\n";
        code += "<p>" + message + "</p>\n";
        code += "<h2>" + mgr.translate("FEDSEARCHERROR2: Error stack") + "</h2>\n";
        code += "<p><pre>\n";
        code += stack;
        code += "</pre></p>\n";
        return code;
    }
}
