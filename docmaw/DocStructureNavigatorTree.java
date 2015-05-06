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
 *  File         : DocStructureNavigatorTree.java
 *  Description  : Handles easy-to-use navigation of structures
 *
 *
 *
 *  2005-07-04    Dikalk    Created.
 *
 *  2005-10-10    MDAHSE    Call 126767. Added Close-button. Cleaned up table generation to make
 *                          code easier to read and debug for client code errors.
 *
 *  2005-10-11    MDAHSE    Call 127794. Okey, second attempt. Removed the radio button
 *                          group and added transfer of whole structure to DocIssue when user
 *                          closes navigator. Also changed the structure query to use bind
 *                          variables instead of hard-coded values concatenated into the
 *                          SELECT.
 *  2005-11-30     SUKMLK   Fixed call 127511. Now there is a navigator customizer. Added
 *                          the package docmaw.HTML for this call.
 *  2005-12-07     DIKALK   Fixed call 129099. Changed logic behind addDocumentStructure
 *  2005-12-13     SUKMLK   Modified the way the cus-nav button worked on the request of Mathias
 *  2006-05-24     DULOLK   Bug Id 57007, Modified addDocumentStructure() so that the stack used in document structure is used properly.
 *  2007-08-09     AMNILK   Eliminated SQL Injection Security Vulnerability.
 *
 *  ------------------------------------------------------------------------------
 */



package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import java.util.Stack;
import ifs.docmaw.html.*;



public class DocStructureNavigatorTree extends ASPPageProvider
{
   
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocStructureNavigatorTree");
   
   private static String IMG_CHECKED_IN = "images/document_normal.gif";
   private static String IMG_CHECKED_OUT = "images/document_co_to_other.gif";
   private static String IMG_CHECKED_OUT_BY_ME = "images/document_co_to_me.gif";
   private static String IMG_MINIMIZE = "images/collapse_left.gif";
   private static String IMG_CONFIG_NAV = "images/configure_navigator.gif";
   private static String IMG_CLOSE = "images/close.gif";

   
   private ASPContext           ctx;
   private ASPHTMLFormatter     fmt;
   private ASPBlock             structureblk;
   private ASPBlock             dummyblk;
   private ASPRowSet            structureset;
   private ASPCommandBar        structurebar;
   private ASPBlockLayout       structurelay;
   private ASPBuffer            documents;
   private TreeList             navigator;
   private String               transferURL;
   
   public DocStructureNavigatorTree(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
   
   public void run() throws FndException
   {
      ASPManager mgr   = getASPManager();
      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();
      
      ctx.writeValue("REGISTRY_QUERIED", mgr.readValue("REGISTRY_QUERIED", "FALSE"));
      ctx.writeValue("USE_DOC_KEYS", mgr.readValue("USE_DOC_KEYS", "TRUE")); // Default use keys to true
      ctx.writeValue("USE_DOC_TITLE", mgr.readValue("USE_DOC_TITLE", "FALSE")); // Default use title to false
                              
      if ("TRUE".equals(mgr.readValue("RETURN_TO_DOC_ISSUE")))
      {
         returnToDocIssue();
      }
      else
      {
         initDocStructureNavigatorTree();
      }      
   }
   
   private void returnToDocIssue()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer documents;
      
      documents = ctx.readBuffer("DOCUMENTS").getBufferAt(0);
      
      okFindStructure(documents.getValueAt(0), documents.getValueAt(1), documents.getValueAt(2), documents.getValueAt(3));
      
      documents = structureset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      
      transferURL = "DocIssue.page?__TRANSFER=" + mgr.pack(documents);
      
   }
   
   
   private void initDocStructureNavigatorTree()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer documents;
      
      if (mgr.dataTransfered())
      {
         documents = mgr.getTransferedData();
      }
      else
      {
         documents = ctx.readBuffer("DOCUMENTS");
      }
      
      buildNavigator(documents);
      ctx.writeBuffer("DOCUMENTS", documents);
   }
   
   
   private void buildNavigator(ASPBuffer documents)
   {
      ASPManager mgr = getASPManager();
      
      // Create root of structure navigator..
      navigator = new TreeList(mgr, "  " + mgr.translate("DOCMAWDOCSTRUCTURENAVIGATORTREEROOTLABEL: Documents"));
      navigator.setImage("images/folder.gif");
      
      // Add each of the documents passed to the navigator..
      int count = documents.countItems();
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data = documents.getBufferAt(x);
         
         // Populate and load each of the document
         // structures to the navigator..
         okFindStructure(data.getValueAt(0), data.getValueAt(1), data.getValueAt(2), data.getValueAt(3));
         
         // Add current set of documents to navigator..
         addDocumentStructure(navigator);
         
         // Clear rowset before fetching next structure..
         structureset.clear();
      }
   }
   
   
   private void okFindStructure(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      // build query to fetch all document in structure..
      StringBuffer query = new StringBuffer();
      
      query.append("SELECT (LEVEL + 1) doc_level, sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev, ");
      query.append("       Edm_File_API.Get_Edm_Information(sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev, 'ORIGINAL'), ");
      query.append("       Doc_Title_API.Get_Title(sub_doc_class, sub_doc_no)");
      query.append( "FROM doc_structure ");
      query.append( "CONNECT BY doc_class = PRIOR sub_doc_class ");
      query.append(      "AND doc_no = PRIOR sub_doc_no ");
      query.append(      "AND doc_sheet = PRIOR sub_doc_sheet ");
      query.append(      "AND doc_rev = PRIOR sub_doc_rev ");
      query.append( "START WITH doc_class = ? AND doc_no = ? AND doc_sheet = ? AND doc_rev = ?");
      
      q = trans.addQuery("STRUCTURE", query.toString()); 	//SQLInjections_Safe  AMNILK 20070810          
      
      q.addParameter("DOC_CLASS", doc_class);
      q.addParameter("DOC_NO", doc_no);
      q.addParameter("DOC_SHEET", doc_sheet);
      q.addParameter("DOC_REV", doc_rev);
      
      ASPCommand cmd = trans.addCustomFunction("GET_EDM_INFO", "Edm_File_API.Get_Edm_Information", "EDM_INFO");
      
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV", doc_rev);
      cmd.addParameter("DOC_TYPE", "ORIGINAL");
      
      ASPCommand cmdParentTitle = trans.addCustomFunction("GET_PARENT_DOC_TITLE", "Doc_Title_API.Get_Title", "DOC_TITLE");
      cmdParentTitle.addParameter("DOC_CLASS", doc_class);
      cmdParentTitle.addParameter("DOC_NO", doc_no);
      
      // Send it all away!
      trans = mgr.perform(trans);
      
      // add items in the response buffer to the rowset..
      ASPBuffer buf = trans.getBuffer("STRUCTURE");
      if (buf.itemExists("INFO"))
         buf.removeItem("INFO");
      
      // Add parent doc on top.
      ASPBuffer parent_doc;
      parent_doc = buf.addBufferAt("DATA", 0);
      parent_doc.addItem("LEVEL", "1");
      parent_doc.addItem("DOC_CLASS", doc_class);
      parent_doc.addItem("DOC_NO", doc_no);
      parent_doc.addItem("DOC_SHEET", doc_sheet);
      parent_doc.addItem("DOC_REV", doc_rev);
      parent_doc.addItem("EDM_INFO", trans.getValue("GET_EDM_INFO/DATA/EDM_INFO"));
      parent_doc.addItem("DOC_TITLE", trans.getValue("GET_PARENT_DOC_TITLE/DATA/DOC_TITLE"));
      
      
      for (int x = 0; x < buf.countItems(); x++)
      {
         addDocument(buf.getBufferAt(x));
      }
   }
   
   
   private void addDocument(ASPBuffer document)
   {
      ASPManager mgr = getASPManager();
      
      // add an empty row..
      structureset.addRow(null);
      
      // set corressponding values for the newly added row..
      structureset.setValue("LEVEL", document.getValueAt(0));
      structureset.setValue("DOC_CLASS", document.getValueAt(1));
      structureset.setValue("DOC_NO", document.getValueAt(2));
      structureset.setValue("DOC_SHEET", document.getValueAt(3));
      structureset.setValue("DOC_REV", document.getValueAt(4));
      structureset.setValue("EDM_INFO", document.getValueAt(5));
      structureset.setValue("DOC_TITLE", document.getValueAt(6));
   }
   
   
   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      structureblk = mgr.newASPBlock("STRUCTURE");
      
      structureblk.addField("LEVEL");
      structureblk.addField("DOC_CLASS");
      structureblk.addField("DOC_NO");
      structureblk.addField("DOC_SHEET");
      structureblk.addField("DOC_REV");
      structureblk.addField("EDM_INFO");
      structureblk.addField("DOC_TITLE");
      
      dummyblk = mgr.newASPBlock("DUMMY");
      dummyblk.addField("DOC_TYPE");
      
      structureset = structureblk.getASPRowSet();
      structurebar = structureblk.newASPCommandBar();
      structurelay = structureblk.getASPBlockLayout();
      structurelay.setLayoutMode(ASPBlockLayout.CUSTOM_LAYOUT);
      structureblk.setTitle(mgr.translate("DOCMAWDOCSTRUCTURENAVIGATORTREETITLE: Navigate Document Structure"));
      
      
      appendJavaScript("function refreshNavigator()\n");
      appendJavaScript("{\n");
      appendJavaScript("   submit();\n");
      appendJavaScript("}\n");
      
      
      appendJavaScript("function selectDocument(parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev, doc_class, doc_no, doc_sheet, doc_rev)\n");
      appendJavaScript("{\n");
      appendJavaScript("   window.parent.DocIssue.selectDocument(parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev, doc_class, doc_no, doc_sheet, doc_rev, true);\n");
      appendJavaScript("}\n");
      
      
      appendJavaScript("function returnToDocIssue()\n");
      appendJavaScript("{\n");
      appendJavaScript("   document.form.RETURN_TO_DOC_ISSUE.value=\"TRUE\";\n");
      appendJavaScript("   submit();\n");
      appendJavaScript("}\n");
      
      
      appendJavaScript("var current_nav_width;\n");
      appendJavaScript("var nav_minimized = false;\n");
      appendJavaScript("function minimizeRestoreNavigator()\n");
      appendJavaScript("{\n");
      appendJavaScript("   if (nav_minimized)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      nav_minimized = false;\n");
      appendJavaScript("      this.minimize_restore_image.title = \"" + mgr.translate("DOCMAWDOCSTRUCTURENAVIGATORTREEMINIMIZETITLE: Minimize navigator") + "\";\n");
      appendJavaScript("      this.minimize_restore_image.src=\"" + IMG_MINIMIZE + "\";\n");
      //appendJavaScript("      parent.FrameBody.cols='25%,*';\n");
      appendJavaScript("      parent.FrameBody.cols = current_nav_width;\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      nav_minimized = true;\n");
      appendJavaScript("      current_nav_width = parent.FrameBody.cols;\n");
      appendJavaScript("      this.minimize_restore_image.src=\"images/expand_right.gif\";\n");
      appendJavaScript("      this.minimize_restore_image.title = \"" + mgr.translate("DOCMAWDOCSTRUCTURENAVIGATORTREERESTORETITLE: Restore navigator") + "\";\n");
      appendJavaScript("      parent.FrameBody.cols='2.3%,*';\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");
      
      
      appendJavaScript("var dialogOpened = 'FALSE';\n");
            
      appendJavaScript("function customizeNavIconClick()\n");
      appendJavaScript("{\n");
      appendJavaScript("if (dialogOpened == 'FALSE')\n");
      appendJavaScript("    makeLayerVisible();\n");
      appendJavaScript("else\n");
      appendJavaScript("    alrightDoIt();\n");
      appendJavaScript("}\n");                  
      
      // Functions to show and hide the navigator customizer.
      appendJavaScript("function makeLayerVisible()\n");
      appendJavaScript("{\n");      
      appendJavaScript("   document.getElementById('dialogBox').style.left = -300;\n");
      appendJavaScript("   document.getElementById('dialogBox').className = 'visible';\n");
      appendJavaScript("   growFrame();\n");
      appendJavaScript("   dialogOpened = 'TRUE';\n");
      appendJavaScript("}\n");
      
      appendJavaScript("function makeLayerInvisible()\n");
      appendJavaScript("{\n");
      appendJavaScript("   shrinkFrame();\n");
      appendJavaScript("   dialogOpened = 'FALSE';\n");
      appendJavaScript("}\n");            
      
      appendJavaScript("function growFrame()\n");
      appendJavaScript("{\n");
      appendJavaScript("   document.getElementById('dialogBox').style.left = parseInt(document.getElementById('dialogBox').style.left) + 10;\n");
      appendJavaScript("   if (parseInt(document.getElementById('dialogBox').style.left) < 10)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      timeout = setTimeout('growFrame();', 10);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");
      
      appendJavaScript("function shrinkFrame()\n");
      appendJavaScript("{\n");
      appendJavaScript("   document.getElementById('dialogBox').style.left = parseInt(document.getElementById('dialogBox').style.left) - 10;\n");
      appendJavaScript("   if (parseInt(document.getElementById('dialogBox').style.left) > -300 )\n");
      appendJavaScript("   {\n");
      appendJavaScript("      timeout = setTimeout('shrinkFrame();', 10);\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      document.getElementById('dialogBox').className = 'hidden';\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");
      
      
      
   }
   
   
   private void addDocumentStructure(TreeList root)
   {
      ASPManager mgr = getASPManager();
      Stack parent_nodes = new Stack();
      
      // Add the top-level document first..
      structureset.first();
      String parent_doc_class = structureset.getValue("DOC_CLASS");
      String parent_doc_no = structureset.getValue("DOC_NO");
      String parent_doc_sheet = structureset.getValue("DOC_SHEET");
      String parent_doc_rev = structureset.getValue("DOC_REV");
      String parent_doc_title = structureset.getValue("DOC_TITLE");
      
      // Node for TLD..
      TreeListNode parent_node = root.addNode(generateLabel(parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev, parent_doc_title));
      parent_node.setTarget(getTargetScript(parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev, parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev));
      setDocumentIcon(parent_node, structureset.getValue("EDM_INFO"));
      
      // Add the rest of the sub documents..
      int parent_level = 1;
      TreeListNode current_node = null;
      while (structureset.next())
      {
         int level = Integer.parseInt(structureset.getValue("LEVEL"));
         boolean leaf = isLeafNode();

         if (level > parent_level)
         {
            if (!leaf)
            {
               parent_nodes.push(new Item(parent_level, parent_node));
               parent_level = level;
            }
         }
         else
         {
            // Keep poping items until the parent
            // node is found..
            Item item;
            do
            {
               item = (Item)parent_nodes.pop();
               parent_level = item.getLevel();
            }
            while (parent_level >= level);
            parent_node = item.getTreeListNode();
            //Bug Id 57007, Start
            parent_nodes.push(new Item(parent_level, parent_node));
	    parent_level=level;
            //Bug Id 57007, End
         }


         // Add sub document..
         String label = generateLabel (structureset.getValue("DOC_CLASS"), structureset.getValue("DOC_NO"), structureset.getValue("DOC_SHEET"), structureset.getValue("DOC_REV"), structureset.getValue("DOC_TITLE"));
         String target = getTargetScript(parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev, structureset.getValue("DOC_CLASS"), structureset.getValue("DOC_NO"), structureset.getValue("DOC_SHEET"), structureset.getValue("DOC_REV"));

         if (leaf)
         {
            TreeListItem tree_item = parent_node.addItem(label);
            tree_item.setTarget(target);
            setDocumentLeafIcon(tree_item, structureset.getValue("EDM_INFO"));
         }
         else
         {
            parent_node = parent_node.addNode(label);
            parent_node.setTarget(target);
            setDocumentIcon(parent_node, structureset.getValue("EDM_INFO"));
         }
      }
   }
   
   /*
    * generateLabel
    * Generates the label depending on the values USE_DOC_KEYS and USE_DOC_TITLE
    * stored in the context.
    */   
   private String generateLabel(String docClass,
                                String docNo,
                                String docSheet,
                                String docRev,
                                String docTitle)
   {      
      String label = "";
      ASPManager mgr = getASPManager();
      if (ctx.readValue("USE_DOC_KEYS").equalsIgnoreCase("TRUE"))
      {
         label = docClass + " - " + docNo + " - " + docSheet + " - " + docRev;
      }
      if (ctx.readValue("USE_DOC_TITLE").equalsIgnoreCase("TRUE"))
      {
         if (!mgr.isEmpty(label))
            label += ": " + docTitle;
         else
            label = docTitle;
      }            
      return label;
   }
   
   
   private boolean isLeafNode()
   {
      int count = structureset.countRows();
      if (count == (structureset.getCurrentRowNo() + 1))
      {
         // the last document is always a leaf node..
         return true;
      }
      else
      {
         // current document's level ..
         int level = Integer.parseInt(structureset.getValue("LEVEL"));
         
         // get next document's level..
         structureset.next();
         int next_level = Integer.parseInt(structureset.getValue("LEVEL"));
         structureset.previous();
         
         // compare levels to determine whether the current
         // document is a leaf node..
         return (next_level <= level);
      }
   }
   
   
   /**
    *  Sets the icon of the document, appearing in the nvaigator
    *  depending on the document's file state
    */
   private void setDocumentIcon(TreeListNode document, String edm_info)
   {
      if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, "CHECKED_OUT_TO_ME")))
         document.setImage(IMG_CHECKED_OUT_BY_ME);
      else if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, "CHECKED_OUT")))
         document.setImage(IMG_CHECKED_OUT);
      else
         document.setImage(IMG_CHECKED_IN);
   }
   
   
   /**
    *  Sets the icon of the document, appearing in the nvaigator
    *  depending on the document's file state
    */
   private void setDocumentLeafIcon(TreeListItem document, String edm_info)
   {
      if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, "CHECKED_OUT_TO_ME")))
         document.setImage(IMG_CHECKED_OUT_BY_ME);
      else if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, "CHECKED_OUT")))
         document.setImage(IMG_CHECKED_OUT);
      else
         document.setImage(IMG_CHECKED_IN);
   }
   
   
   
   private String getTargetScript(String parent_doc_class, String parent_doc_no, String parent_doc_sheet, String parent_doc_rev,
           String doc_class, String doc_no, String do_sheet, String doc_rev)
   {
      String target = "javascript:selectDocument(\\\"" + parent_doc_class + "\\\", \\\"" + parent_doc_no + "\\\", \\\"";
      target += parent_doc_sheet +  "\\\", \\\"" + parent_doc_rev + "\\\", \\\"";
      target += doc_class + "\\\", \\\"" + doc_no + "\\\", \\\"";
      target += do_sheet +  "\\\", \\\"" + doc_rev + "\\\")' target='DocStructureNavigatorTree";
      return target;
   }
   
   
   public String drawRadio(String label, String name, String value, boolean checked, String tag)
   {
      return "<input class=radioButton type=radio name=\"" + name + "\" value=\"" + value + "\"" + (checked ? " CHECKED " : "") + " " + (tag == null ? "" : tag) + ">&nbsp;<font class=normalTextValue>" + label + "</font>";
   }
   
   
   protected String getDescription()
   {
      return getTitle();
   }
   
   
   protected String getTitle()
   {
      return "DOCMAWDOCSTRUCTURENAVIGATORTREETITLE2: Document Structure Navigator";
   }
   
   
   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      
      // generate UI..
      out.clear();
      
      out.append(DocmawUtil.getClientMgrObjectStr());
      
      if (!mgr.isEmpty(transferURL))
      {
         out.append("\n");
         out.append("<p>\n");
         out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCSTRUCTURENAVIGATORTREEPLEASEWAIT: Transferring data, please wait...")));
         out.append("\n</p>\n");
         out.append("<script language=\"javascript\">\n");
         out.append("  parent.window.location = \"" + transferURL + "\"\n");
         out.append("</script>\n");
      }
      else
      {
         navigator.setTreePosition(22, 50);
         
         // Draw minmize custimize and close buttons..
         out.append("<table border=0 width=100%>");
         out.append("  <tr>");
         out.append("    <td align=left>");
         out.append("      <a href=\"javascript:minimizeRestoreNavigator()\"><img id=minimize_restore_image border=0 src=\"" + IMG_MINIMIZE + "\" title=\"" +
                           mgr.translate("DOCMAWDOCSTRUCTURENAVIGATORTREEMINIMIZETITLE: Minimize navigator") + "\"></a>");                 
         out.append("    </td>");
         out.append("    <td align=right nowrap>");
         out.append("      <a href=\"javascript:customizeNavIconClick()\"><img border=0 src=\"" + IMG_CONFIG_NAV + "\" title=\"" +
                           mgr.translate("DOCMAWDOCSTRUCTURENAVIGATORTREECONFIGURE: Configure Navigator") + "\"></a>");                 
         out.append("      <a href=\"javascript:returnToDocIssue()\"><img border=0 src=\"" + IMG_CLOSE + "\" title=\"" +
                           mgr.translate("DOCMAWDOCSTRUCTURENAVIGATORTREECLOSENAVBUTTITLE: Close navigator and return to Document Info") +  "\"></a>");                 
         out.append("    </td>");
         out.append("  </tr>");
         out.append("</table>");
         
         out.append(navigator.generateTreeHeader());
         out.append(navigator.generateTreeBody());
         out.append(navigator.generateTreeScripts());
         out.append(navigator.generateHiddenFields());                  
         
         
         
         // Hidden field to close navigator
         out.append("<input type=\"hidden\" name=\"RETURN_TO_DOC_ISSUE\" value=\"FALSE\">\n");
         
         // Hidden field to store if we should add keys to the nodes or add titles to the nodes.         
         out.append("<INPUT TYPE = 'HIDDEN' NAME = 'REGISTRY_QUERIED' VALUE = '" + ctx.readValue("REGISTRY_QUERIED", "FALSE") + "'>");
         out.append("<INPUT TYPE = 'HIDDEN' NAME = 'USE_DOC_KEYS' VALUE = 'FALSE'>");
         out.append("<INPUT TYPE = 'HIDDEN' NAME = 'USE_DOC_TITLE' VALUE = 'FALSE'>");
         out.append("</FORM>");
                  
      }

      
      /*
       * We create an HTML Layer class and a free form HTML class. The free form HTML
       * class we create will store the contents of the dialog box.
       */
      HTMLLayer layer = new HTMLLayer();
      HTMLFreeForm free = new HTMLFreeForm();                  
      
      // Now we add the dialog box scripts and html into the HTMLFreeForm object.
      free.addLine(getTreeCustomizerDialog());
      
      // We set up some parameters for the layer.
      layer.setId("dialogBox");
      layer.setPositionType("absolute");
      layer.setWidth(250);
      layer.setHeight(250);
      layer.setZIndex(1);
      layer.setTop(30);
      layer.setLeft(-250); // off screen at startup.
      layer.setPadding(2);
      
      /*
       * We add the free form html object as a child of the layer. ie, it will
       * be rendered when rendering the layer object.
       */
      layer.addChildNode(free);
      
      /*
       * We append the HTML got by rendering the HTMLLayer object. This object
       * contains both an HTML Layer and the navigator customization dialog
       * along with it's scripts. We can render any HTMLEntity type object's
       * and its entire child heariachy by calling the top level parents getHtml()
       * method. Behold the power of The Docman Webkit (TM)! :).
       */
      out.append(layer.getHtml());
      
      
      if ("FALSE".equals(ctx.readValue("REGISTRY_QUERIED"))) // Registry has not been queried yet.
      {
         // Call a function that resubmits the page.
         out.append("<script language=\"javascript\">\n");
         out.append("readRegistryAndSubmit();\n");
         out.append("</SCRIPT>");
      }
      
      out.append("</BODY>\n");
      out.append("</HTML>\n");
      return out;
      
   }
   
   private String getTreeCustomizerDialog()
   {
      String navigatorCustomizerStuff = "";
      ASPManager mgr = getASPManager();      
      
      // SUKMLK: Following cute indented code was generated from the source using GNU Code Tools,
      // coded by me! :)
      
      navigatorCustomizerStuff +=
      "<!------------------- HTML AND SCRIPTS FOR THE STRUCTURE NAVIGATOR CUSTOMIZATION DIALOG----------------------->                                                  \n" + 
      "   <TABLE CELLSPACING=1>                                                                                                                                         \n" + 
      "      <TR>                                                                                                                                                       \n" + 
      "         <TD CLASS = 'tableRowHiliteColor' width = '250'>                                                                                                        \n" + 
      "            <P class = 'tableColumnHeadingText'>" + mgr.translate("DOCMAWNAVCUSTOMIZENAV: Customize Navigator") + "</P>                                          \n" + 
      "         </TD>                                                                                                                                                   \n" + 
      "      </TR>                                                                                                                                                      \n" + 
      "      <TR>                                                                                                                                                       \n" + 
      "         <TD CLASS = 'tableRowColor1'>                                                                                                                           \n" + 
      "         <!--Checkboxes------------------->                                                                                                                      \n" + 
      "            <FORM NAME='frmCheckboxes'>                                                                                                                          \n" + 
      "               <TABLE>                                                                                                                                           \n" + 
      "                  <TR>                                                                                                                                           \n" + 
      "                     <TD WIDTH=220>                                                                                                                              \n" + 
      "                        <P CLASS='tableContentText'>" + mgr.translate("DOCMAWNAVUSEKEYS: Use Document Keys") + "</P>                                             \n" + 
      "                     </TD>                                                                                                                                       \n" + 
      "                     <TD WIDTH=20>                                                                                                                               \n" + 
      "                        <INPUT TYPE = 'CHECKBOX' ID='cbUseKeys' ONCLICK='validateCheckBoxes(cbUseKeys)'>                                                         \n" + 
      "                     </TD>                                                                                                                                       \n" + 
      "                  </TR>                                                                                                                                          \n" + 
      "                  <TR>                                                                                                                                           \n" + 
      "                     <TD WIDTH=220>                                                                                                                              \n" + 
      "                        <P CLASS='tableContentText'>" + mgr.translate("DOCMAWNAVUSETITLE: Use Document Title") + "</P>                                           \n" + 
      "                     </TD>                                                                                                                                       \n" + 
      "                     <TD WIDTH=20>                                                                                                                               \n" + 
      "                        <INPUT TYPE = 'CHECKBOX' ID='cbUseTitle' ONCLICK='validateCheckBoxes(cbUseTitle)'>                                                       \n" + 
      "                     </TD>                                                                                                                                       \n" + 
      "                  </TR>                                                                                                                                          \n" + 
      "               </TABLE>                                                                                                                                          \n" + 
      "            </FORM>                                                                                                                                              \n" + 
      "         <!--Checkboxes------------------->                                                                                                                      \n" + 
      "                                                                                                                                                                 \n" + 
      "         <!--Preview table---------------->                                                                                                                      \n" + 
      "            <TABLE>                                                                                                                                              \n" + 
      "               <TR BGCOLOR='WHITE'>                                                                                                                              \n" + 
      "                  <TD WIDTH = '245'>                                                                                                                             \n" + 
      "                     <TABLE CELLPADDING = '1'>                                                                                                                   \n" + 
      "                        <TR>                                                                                                                                     \n" + 
      "                           <TD>                                                                                                                                  \n" + 
      "                              <IMG src='images/document_normal.gif'>                                                                                             \n" + 
      "                           </TD>                                                                                                                                 \n" + 
      "                           <TD>                                                                                                                                  \n" + 
      "                              <P CLASS = 'navigatorPageNode' ID='exNodeText'>  </P>                                                                              \n" + 
      "                           </TD>                                                                                                                                 \n" + 
      "                        </TR>                                                                                                                                    \n" + 
      "                     </TABLE>                                                                                                                                    \n" + 
      "                     <BR>                                                                                                                                        \n" + 
      "                  </TD>                                                                                                                                          \n" + 
      "               </TR>                                                                                                                                             \n" + 
      "            </TABLE>                                                                                                                                             \n" + 
      "         <!--Preview table---------------->                                                                                                                      \n" + 
      "         </TD>                                                                                                                                                   \n" + 
      "      </TR>                                                                                                                                                      \n" + 
      "      <TR>                                                                                                                                                       \n" + 
      "         <TD CLASS = 'tableRowColor2'>                                                                                                                           \n" + 
      "         <!--Buttons---------------------->                                                                                                                      \n" + 
      "            <FORM>                                                                                                                                               \n" + 
      "               <TABLE>                                                                                                                                           \n" + 
      "                  <TR>                                                                                                                                           \n" + 
      "                     <TD WIDTH = 115 ALIGN='CENTER'>                                                                                                             \n" + 
      "                        <INPUT CLASS = 'button' TYPE='BUTTON' NAME = 'doIt' VALUE = '" + mgr.translate("DOCMAWNAVAPPLY: Apply") + "' ONCLICK='alrightDoIt()'>    \n" + 
      "                     </TD>                                                                                                                                       \n" + 
      "                     <TD WIDTH = 120 ALIGN='CENTER'>                                                                                                             \n" + 
      "                        <INPUT CLASS = 'button' TYPE='BUTTON' NAME = 'dropIt' VALUE = '" + mgr.translate("DOCMAWNAVCANCEL: Cancel") + "' ONCLICK='doDropIt()'>   \n" + 
      "                     </TD>                                                                                                                                       \n" + 
      "                  <TR>                                                                                                                                           \n" + 
      "               </TABLE>                                                                                                                                          \n" + 
      "            </FORM>                                                                                                                                              \n" + 
      "         <!--Buttons---------------------->                                                                                                                      \n" + 
      "         </TD>                                                                                                                                                   \n" + 
      "      </TR>                                                                                                                                                      \n" + 
      "   </TABLE>                                                                                                                                                      \n" + 
      "                                                                                                                                                                 \n" + 
      "   <SCRIPT>                                                                                                                                                      \n" + 
      "      function setExampleText()                                                                                                                                  \n" + 
      "      {                                                                                                                                                          \n" + 
      "         var oCbUseKeys  = document.getElementById('cbUseKeys');                                                                                                 \n" + 
      "         var oCbUseTitle = document.getElementById('cbUseTitle');                                                                                                \n" + 
      "         var oNodeText  = document.getElementById('exNodeText');                                                                                                 \n" + 
      "                                                                                                                                                                 \n" + 
      "         if (oCbUseKeys.checked && !oCbUseTitle.checked)                                                                                                         \n" + 
      "         {                                                                                                                                                       \n" + 
      "            oNodeText.innerHTML = 'D - 1023 - 1 - A1';                                                                                                           \n" + 
      "         }                                                                                                                                                       \n" + 
      "         if (!oCbUseKeys.checked && oCbUseTitle.checked)                                                                                                         \n" + 
      "         {                                                                                                                                                       \n" + 
      "            oNodeText.innerHTML = '" + mgr.translate("DOCMAWNAVMYTITLE: My Title") + "';                                                                         \n" + 
      "         }                                                                                                                                                       \n" + 
      "         if (oCbUseKeys.checked && oCbUseTitle.checked)                                                                                                          \n" + 
      "         {                                                                                                                                                       \n" + 
      "            oNodeText.innerHTML = 'D - 1023 - 1 - A1: " + mgr.translate("DOCMAWNAVMYTITLE: My Title") + "';                                                      \n" + 
      "         }                                                                                                                                                       \n" + 
      "      }                                                                                                                                                          \n" + 
      "                                                                                                                                                                 \n" + 
      "      function validateCheckBoxes(callingCheckbox)                                                                                                               \n" + 
      "      {                                                                                                                                                          \n" + 
      "         var oCbUseKeys  = document.getElementById('cbUseKeys');                                                                                                 \n" + 
      "         var oCbUseTitle = document.getElementById('cbUseTitle');                                                                                                \n" + 
      "                                                                                                                                                                 \n" + 
      "         if (!oCbUseKeys.checked && !oCbUseTitle.checked)                                                                                                        \n" + 
      "         {                                                                                                                                                       \n" + 
      "            callingCheckbox.checked = true;                                                                                                                      \n" + 
      "         }                                                                                                                                                       \n" + 
      "                                                                                                                                                                 \n" + 
      "         setExampleText();                                                                                                                                       \n" + 
      "      }                                                                                                                                                          \n" + 
      "                                                                                                                                                                 \n" + 
      "                                                                                                                                                                 \n" + 
      "      function loadValues()                                                                                                                                      \n" + 
      "      {                                                                                                                                                          \n" + 
      "         document.form.USE_DOC_KEYS.value  = oCliMgr.RegGetValue('HKEY_CURRENT_USER', 'Software\\\\IFS\\\\Document Management\\\\Settings', 'StrucNavUseKeys');  \n" + 
      "         document.form.USE_DOC_TITLE.value  = oCliMgr.RegGetValue('HKEY_CURRENT_USER', 'Software\\\\IFS\\\\Document Management\\\\Settings', 'StrucNavUseTitle');\n" + 
      "                                                                                                                                                                 \n" + 
      "         var oCbUseKeys  = document.getElementById('cbUseKeys');                                                                                                 \n" + 
      "         var oCbUseTitle = document.getElementById('cbUseTitle');                                                                                                \n" + 
      "                                                                                                                                                                 \n" + 
      "         oCbUseKeys.checked  = false;                                                                                                                            \n" + 
      "         oCbUseTitle.checked = false;                                                                                                                            \n" + 
      "                                                                                                                                                                 \n" + 
      "                                                                                                                                                                 \n" + 
      "         if (document.form.USE_DOC_KEYS.value == 'TRUE')                                                                                                         \n" + 
      "         {                                                                                                                                                       \n" + 
      "            oCbUseKeys.checked = true;                                                                                                                           \n" + 
      "         }                                                                                                                                                       \n" + 
      "         if (document.form.USE_DOC_TITLE.value == 'TRUE')                                                                                                        \n" + 
      "         {                                                                                                                                                       \n" + 
      "            oCbUseTitle.checked = true;                                                                                                                          \n" + 
      "         }                                                                                                                                                       \n" + 
      "         if (document.form.USE_DOC_KEYS.value != 'TRUE' && document.form.USE_DOC_TITLE.value != 'TRUE')                                                          \n" + 
      "         {                                                                                                                                                       \n" + 
      "            oCbUseKeys.checked = true;                                                                                                                           \n" + 
      "         }                                                                                                                                                       \n" + 
      "         setExampleText();                                                                                                                                       \n" + 
      "      }                                                                                                                                                          \n" + 
      "                                                                                                                                                                 \n" + 
      "      function doDropIt()                                                                                                                                        \n" + 
      "      {                                                                                                                                                          \n" + 
      "         /*                                                                                                                                                      \n" + 
      "          * Now we call the function which hides the layer into which this code is added.                                                                        \n" +       
      "          */                                                                                                                                                     \n" + 
      "         makeLayerInvisible();                                                                                                                                   \n" + 
      "         loadValues();                                                                                                                                           \n" + 
      "      }                                                                                                                                                          \n" + 
      "                                                                                                                                                                 \n" + 
      "      function alrightDoIt()                                                                                                                                     \n" + 
      "      {                                                                                                                                                          \n" + 
      "                                                                                                                                                                 \n" + 
      "         var oCbUseKeys  = document.getElementById('cbUseKeys');                                                                                                 \n" + 
      "         var oCbUseTitle = document.getElementById('cbUseTitle');                                                                                                \n" + 
      "                                                                                                                                                                 \n" + 
      "                                                                                                                                                                 \n" + 
      "                                                                                                                                                                 \n" + 
      "         if (oCbUseKeys.checked == true)                                                                                                                         \n" + 
      "         {                                                                                                                                                       \n" + 
      "            document.form.USE_DOC_KEYS.value = 'TRUE';                                                                                                           \n" + 
      "            oCliMgr.RegSetValue('HKEY_CURRENT_USER', 'Software\\\\IFS\\\\Document Management\\\\Settings', 'StrucNavUseKeys', 'TRUE');                           \n" + 
      "         }                                                                                                                                                       \n" + 
      "         else                                                                                                                                                    \n" + 
      "         {                                                                                                                                                       \n" + 
      "            document.form.USE_DOC_KEYS.value = 'FALSE';                                                                                                          \n" + 
      "            oCliMgr.RegSetValue('HKEY_CURRENT_USER', 'Software\\\\IFS\\\\Document Management\\\\Settings', 'StrucNavUseKeys', 'FALSE');                          \n" + 
      "         }                                                                                                                                                       \n" + 
      "                                                                                                                                                                 \n" + 
      "                                                                                                                                                                 \n" + 
      "         if (oCbUseTitle.checked == true)                                                                                                                        \n" + 
      "         {                                                                                                                                                       \n" + 
      "            document.form.USE_DOC_TITLE.value = 'TRUE';                                                                                                          \n" + 
      "            oCliMgr.RegSetValue('HKEY_CURRENT_USER', 'Software\\\\IFS\\\\Document Management\\\\Settings', 'StrucNavUseTitle', 'TRUE');                          \n" + 
      "         }                                                                                                                                                       \n" + 
      "         else                                                                                                                                                    \n" + 
      "         {                                                                                                                                                       \n" + 
      "            document.form.USE_DOC_TITLE.value = 'FALSE';                                                                                                         \n" + 
      "            oCliMgr.RegSetValue('HKEY_CURRENT_USER', 'Software\\\\IFS\\\\Document Management\\\\Settings', 'StrucNavUseTitle', 'FALSE');                         \n" + 
      "         }                                                                                                                                                       \n" + 
      "                                                                                                                                                                 \n" + 
      "         makeLayerInvisible();                                                                                                                                   \n" + 
      "                                                                                                                                                                 \n" + 
      "                                                                                                                                                                 \n" + 
      "         submit();                                                                                                                                               \n" + 
      "      }                                                                                                                                                          \n" + 
      "                                                                                                                                                                 \n" + 
      "                                                                                                                                                                 \n" + 
      "      function readRegistryAndSubmit()                                                                                                                           \n" + 
      "      {                                                                                                                                                          \n" + 
      "         document.form.REGISTRY_QUERIED.value = 'TRUE';                                                                                                          \n" + 
      "         submit();                                                                                                                                               \n" + 
      "      }                                                                                                                                                          \n" + 
      "                                                                                                                                                                 \n" + 
      "      loadValues();                                                                                                                                              \n" + 
      "                                                                                                                                                                 \n" + 
      "   </SCRIPT>                                                                                                                                                     \n" + 
      "<!------------------- HTML AND SCRIPTS FOR THE STRUCTURE NAVIGATOR CUSTOMIZATION DIALOG----------------------->                                                  \n";
      return navigatorCustomizerStuff;
   }
}


/**
 * This class represents an item to be pushed/popped from the
 * tree stack. An item contains a reference to a tree node as
 * well as the level of the node in the structure
 */
class Item
{
   private int level;
   private TreeListNode node;
   
   public Item(int level, TreeListNode node)
   {
      this.level = level;
      this.node = node;
   }
   
   
   /**
    *  Returns the level of the node in the structure
    */
   public int getLevel()
   {
      return level;
   }
   
   
   /**
    *  Returns the node in the tree
    */
   public TreeListNode getTreeListNode()
   {
      return node;
   }
   

}


