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
 *  File        : SearchDomainHelp.java
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2007/12/05 rahelk Bug id 67990, support for domain groups
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.asp.ApplicationSearchManager.SearchDomain;

import java.util.Vector;
import java.util.StringTokenizer;
import java.io.*;

import org.w3c.dom.*;
//import org.apache.xerces.dom.DocumentImpl;
//import org.apache.xerces.dom.DOMImplementationImpl;
//import org.apache.xml.serialize.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;



public class SearchDomainHelp extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.SearchDomainHelp");   
   private String[][] selectedDomain;
   
   public SearchDomainHelp(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      if ("Y".equals(mgr.getQueryStringValue(("VALIDATE_SEARCH_DOMAIN"))))
         fetchIndexedInfo();
      else
      {
         getContents();
      }
      mgr.endResponse();
   }
   

   private void fetchIndexedInfo() throws FndException
   {
      ASPManager mgr = getASPManager();  
      String search_domain = mgr.readValue("SEARCH_DOMAIN");
      String domain_type   = mgr.readValue("DOMAIN_TYPE");
      String desc          = mgr.readValue("DESC");
      
      AutoString out = getOutputStream();
      out.clear();
      
      printIndexInfo(search_domain,domain_type,desc);
      
      mgr.responseWrite(""); //out OutputStream is written to response
      //mgr.endResponse();
   }
   
   
   public void preDefine()
   {
      this.disableHeader();
      this.disableFooter();
      this.disableBar();
      this.disableHelp();
      this.disableApplicationSearch();
   }
      
   protected String getDescription()
   {
      return "FNDPAGESSEARCHHELPWINDOWTITLE: Search Domain Help";
   }

   protected String getTitle()
   {
      return "FNDPAGESSEARCHHELPWINDOWTITLE: Search Domain Help";
   }
   
   private void initSelectedDomains() throws FndException
   {
      ASPManager mgr = getASPManager();
      String search_domain = mgr.getQueryStringValue("SEARCH_DOMAIN");
      String selectedDomains = "";

      if (!mgr.isEmpty(search_domain))  //called from find dialog. Only show this domain in help
         selectedDomains = search_domain;
      else                             //show all selected search domains in help
         selectedDomains = mgr.getASPPage().readGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"SelectedDomains",false);
      
      if (!mgr.isEmpty(selectedDomains))
      {
         StringTokenizer st = new StringTokenizer(selectedDomains,",");
         Vector domainList = UserDataCache.getInstance().getSearchDomains(mgr.getSessionId());
         selectedDomain = new String[st.countTokens()][3];  // 0 - searchDomain, 1 - description, 2 - domainType
         int i = 0;
         String domainId;
         
         while(st.hasMoreElements())
         {
            domainId = st.nextToken(); 
            setDomainValues(domainId,i,domainList);
            //selectedDomain[i][0] = domainId;
            //selectedDomain[i][1] = getDomainDescription(domainId, domainList);
            i++;
         }
      }
   }

   private void setDomainValues(String domainId, int j, Vector domainList)
   {
      selectedDomain[j][0] = domainId;
      
      int noOfDomains = domainList.size();
      for (int i=0; i<noOfDomains; i++)
      {
         if (domainId.equals(((SearchDomain)domainList.elementAt(i)).getSearchDomain()))
         {
            selectedDomain[j][1] = ((SearchDomain)domainList.elementAt(i)).getDescription();
            selectedDomain[j][2] = ((SearchDomain)domainList.elementAt(i)).getDomainType();
         }
      }
      
   }

   /*
   private String getDomainDescription(String domainId, Vector domainList)
   {
      int noOfDomains = domainList.size();
      for (int i=0; i<noOfDomains; i++)
      {
         if (domainId.equals(((SearchDomain)domainList.elementAt(i)).getSearchDomain()))
            return ((SearchDomain)domainList.elementAt(i)).getDescription();
      }
      return null;      
   }
    */

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();

      out.append("<html>\n");
      out.append("<head>\n");
      String head_tag = mgr.generateHeadTag(getDescription());
      head_tag = mgr.replace(head_tag, "loadImages();", ""); //loadImages(); caused script error. Anyway not necessary in this page
      out.append(head_tag);
      out.append("</head>\n");
      
      out.append("<body bgcolor=\"#FFFFCC\" >\n");
      out.append("<form name=form>\n");

      printContents();

      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");
      
      out.append(generateClientScript());

      return out;
   }
   
   protected void printContents() throws FndException
   {
      appendToHTML("\n<font face=\"arial\" size=\"2\"><p>");
      printBoldText("FNDPAGESSEARCHHELPSELECTEDDOMAINS: You have selected to search in the following domain(s):");
      printNewLine();
      printNewLine();
      
      initSelectedDomains(); //populate selectedDomain array with selected domains from user profile
      String first_domain = "";
      String first_domain_desc = "";
      String first_domain_type = "";
      String content_variables = "";
      
      for (int i=0; i<selectedDomain.length; i++)
      {
         if (selectedDomain.length > 1)
         {
            printLink(selectedDomain[i][1],"javascript:fetchIndexInfo('"+selectedDomain[i][0]+"','domain_content_"+i+"','"+selectedDomain[i][2]+"','"+selectedDomain[i][1]+"');");
            //if ((i+1)!=selectedDomain.length) appendToHTML(",&nbsp;");
            appendToHTML("&nbsp;");
            printHiddenField("domain_content_"+i, "");
         }
         else
         {
            appendToHTML("<i>");
            printText(selectedDomain[i][1]);
            appendToHTML("</i>");
         }


         if ("".equals(first_domain)) 
         {
            first_domain = selectedDomain[i][0];
            first_domain_desc = selectedDomain[i][1];
            first_domain_type = selectedDomain[i][2];
         }
      }
      
      printNewLine();
      printNewLine();
      
      appendToHTML("<div id=searchDomainIndex style=\"position:absolute;\">");      
      printIndexInfo(first_domain, first_domain_type, first_domain_desc);
      appendToHTML("</div>");
      
      printNewLine();
      appendToHTML("\n</p></font>");
   }
   
   private void printIndexInfo(String domain, String type, String desc) throws FndException
   {
      ASPManager mgr = getASPManager();  
      String xmlDoc = getIndexedAttributesXML(domain);

      try
      {
         if (ApplicationSearchManager.GROUP_TYPE.equals(type))
            parseGroupHeaderXML(xmlDoc, desc);
         else
            parseXML(xmlDoc, desc);
      }
      catch (Exception e)
      {
         throw new FndException(e);
      }
      
      //printText(xmlDoc);      
   }

   
   private String getIndexedAttributesXML(String searchDomain)
   {
      ASPManager mgr = getASPManager(); 
      String attributes = "";
      try
      {
         ApplicationSearchManager searchManager = ApplicationSearchManager.getInstance(mgr.getASPConfig());
         attributes = searchManager.getIndexedInfo(mgr.getUserId(),searchDomain,mgr.getASPConfig());
      }
      catch (Exception any)
      {         
      }
      
      return attributes;
   }
   
   private void parseGroupHeaderXML(String xml_content, String desc) throws Exception
   {
      if (DEBUG) debug("SearchDomainHelp.parseGroupHeaderXML:"+xml_content);
      
      ASPManager mgr = getASPManager(); 
      
      DOMParser p = new DOMParser();
      InputSource inp = new InputSource(new StringReader(xml_content));
      p.parse(inp);
      Document doc = p.getDocument();

      Element element = doc.getDocumentElement();
      String group_desc = (desc!=null?desc:element.getNodeName());

      printBoldText(mgr.translate("FNDPAGESSEARCHHELPGROUPINDXINFO: &1 domain consists of the following:",group_desc));
      
      NodeList nodes = element.getChildNodes();
      int count = nodes.getLength();
      String first_group_domain = "";
      String first_group_desc   = "";
      
      printNewLine();
      printNewLine();
      //retrive ENTITY element node
      for (int i=0; i<count; i++)
      {
         Node node = nodes.item(i);
         if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
         {
            Node n = nodes.item(i); // Entity_element
            String entity = n.getNodeName(); //entity name;
            String description = n.getFirstChild().getNodeValue(); 
            
            printLink(description,"javascript:fetchIndexInfo('"+entity+"','group_domain_content_"+i+"','','"+description+"');"); //no sub-groups within groups so type isn't Group
            appendToHTML("&nbsp;");
            //content_variables += "\ngroup_domain_content_"+i+" = '';";
            printHiddenField("group_domain_content_"+i, "");
            
            if ("".equals(first_group_domain))
            {
               first_group_domain = entity;
               first_group_desc = description;
            }
         }
      }
      printNewLine();
      printNewLine();
      
      appendToHTML("<div id=searchGroupDomainIndex style=\"position:absolute;\">");      
      printIndexInfo(first_group_domain, null,first_group_desc);
      appendToHTML("</div>");
   }
   
   private String getTextValue(Element ele)
   {
		String textVal = "";
      NodeList list = ele.getChildNodes();
      textVal = list.item(0).getNodeValue();
      
		return textVal;
	}
   
   private void parseXML(String xml_content, String desc) throws Exception
   {
      if (DEBUG) debug("SearchDomainHelp.parseXML:"+xml_content);
      
      ASPManager mgr = getASPManager(); 
      
      DOMParser p = new DOMParser();
      InputSource inp = new InputSource(new StringReader(xml_content));
      p.parse(inp);
      Document doc = p.getDocument();

      Element element = doc.getDocumentElement();
      NodeList nodes = element.getChildNodes();
      int count = nodes.getLength();
      
      //retrive ENTITY element node
      for (int i=0; i<count; i++)
      {
         Node node = nodes.item(i);
         if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
         {
            element = (Element)nodes.item(i); // Entity_element
            String entity = (desc!=null?desc:element.getNodeName()); //entity name;
            printBoldText(mgr.translate("FNDPAGESSEARCHHELPINDXINFO: &1 domain has indexed information in the following fields:",entity));
            
            break;
         }
      }
      printNewLine();
      printNewLine();
      
      printXML(element,0, false);
   }

   private void printXML(Element node, int indent, boolean printHeader)
   {
      NodeList nodes = node.getChildNodes();
      String attribute = "";
      
      for (int j=0; j<indent; j++)
         attribute += "&nbsp";

      if (printHeader)
      {
         if (DEBUG) debug("SearchDomainHelp.depthSearchXML: HeaderNodeName="+node.getNodeName());               
         
         appendToHTML(attribute);
         printBoldText(node.getNodeName());
         printNewLine();
         attribute += "&nbsp";
      }
      
      int len = nodes.getLength();

      for (int i=0; i<len; i++)
      {
         Node next_node = nodes.item(i);

         if(next_node.hasChildNodes())
         {
            NodeList child_nodes = next_node.getChildNodes();
            Node     first_child = child_nodes.item(0);
            
            // ASSUMPTION:
            // A node is a leaf only if it has only one child and the type of the child is '#text'
            // Then the value of the child node is the value of the leaf
            if(child_nodes.getLength()==1 && "#text".equals(first_child.getNodeName()) )
            {
               if (DEBUG) debug("SearchDomainHelp.depthSearchXML: NodeName="+next_node.getNodeName());                

               appendToHTML(attribute);
               printText(next_node.getNodeName());
               printNewLine();
               
            }
            else
            {
               indent++;
               printXML((Element)next_node,indent, true);
            }
         }
         else
         {
            continue;
         }
      }
   }
   
}
