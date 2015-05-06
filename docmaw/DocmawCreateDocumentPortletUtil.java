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
*  File        : DocmawCreateDocumentPortletUtil.java
*  Created     : SHTHLK 2009-04-23 
*  Note        : This page is called from Create Document portal
*                Used to send the reponse to server and back to clinet.
*
*  2009-06-09 SHTHLK Bug Id 78770, Created.
*  2009-06-13 SHTHLK Bug Id 78770, Corrected the comments
* ----------------------------------------------------------------------------
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class DocmawCreateDocumentPortletUtil extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocmawCreateDocumentPortletUtil");

   private ASPHTMLFormatter fmt;
   private ASPForm frm;
   private ASPBlock blk;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPRowSet rowset;
   private ASPField f;  
   private ASPTransactionBuffer trans;


   public DocmawCreateDocumentPortletUtil(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      trans   = null;
      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocmawCreateDocumentPortletUtil page = (DocmawCreateDocumentPortletUtil)(super.clone(obj));

      page.fmt = page.getASPHTMLFormatter();
      page.frm = page.getASPForm();
      page.blk = page.getASPBlock(blk.getName());
      page.cmdbar = page.blk.getASPCommandBar();
      page.tbl = page.getASPTable(tbl.getName());
      page.rowset = page.blk.getASPRowSet();
      page.f = page.getASPField(f.getName());  
      page.trans   = null;
      

      return page;
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      fmt   = mgr.newASPHTMLFormatter();   
      
      if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();
   }


   public void validate()
   {
      ASPManager           mgr    = getASPManager();
      ASPTransactionBuffer trans  = mgr.newASPTransactionBuffer();
      ASPCommand           cmd    = mgr.newASPCommand();
      String val = mgr.readValue("VALIDATE");
      
      if ("DOCCLASS".equals(val)) 
      {
         String doc_class = mgr.readValue("DOC_CLASS");    
	 trans.clear();
    
	 // Get number generator setting, Advanced or Standard
	 cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
	 cmd.addParameter("DOC_CLASS",doc_class);
	 cmd.addParameter("STR_IN","DocTitle");
	 cmd.addParameter("STR_IN","NUMBER_GENERATOR");
    
	 // Default value for ID1
	 cmd = trans.addCustomFunction("GETDEFAULTID1","Doc_Class_Default_API.Get_Default_Value_","ID1");
	 cmd.addParameter("DOC_CLASS",doc_class);
	 cmd.addParameter("STR_IN","DocTitle");
	 cmd.addParameter("STR_IN","NUMBER_COUNTER");
    
	 // Get translated number generator
	 cmd = trans.addCustomFunction("GETCLIENTVAL", "Doc_Number_Generator_Type_API.Decode", "NUM_GEN_TRANSLATED");
	 cmd.addReference("NUMBER_GENERATOR", "NUMBERGENERATOR/DATA");
    
	 trans = mgr.validate(trans);
    
	 String number_generator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
	 String id1 = trans.getValue("GETDEFAULTID1/DATA/ID1");
	 String num_gen_translated = trans.getValue("GETCLIENTVAL/DATA/NUM_GEN_TRANSLATED");
    
	 String id2;
	 if ("ADVANCED".equals(number_generator))
	 {
	    trans.clear();
	    cmd = trans.addCustomFunction("GETDEFAULTID2", "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
	    cmd.addParameter("STR_IN", id1);
	    trans = mgr.perform(trans);
	    id2 = trans.getValue("GETDEFAULTID2/DATA/ID2");
    
	    if (mgr.isEmpty (id2) || id2.equals("0"))
	       id2 = "";
	 }
	 else
	 {
	    id1 = " ";
	    id2 = " ";
	 }
	 
	 StringBuffer response = new StringBuffer(mgr.isEmpty(number_generator) ? "": number_generator) ;
	 response.append("^");
	 response.append(mgr.isEmpty(num_gen_translated) ? "": num_gen_translated);
	 response.append("^");
	 response.append(mgr.isEmpty(id1) ? "": id1);
	 response.append("^");
	 response.append(mgr.isEmpty(id2) ? "": id2);
	 response.append("^");
	 mgr.responseWrite(response.toString());
	 mgr.endResponse();
     }
     else if ("ID1".equals(val)) 
     {
         
	 String id1 = mgr.readValue("ID1");    
	 trans.clear();

         cmd = trans.addCustomFunction("GETDEFAULTID2", "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
	 cmd.addParameter("STR_IN", id1);
	 trans = mgr.perform(trans);
	 String id2 = trans.getValue("GETDEFAULTID2/DATA/ID2");
    
	 if (mgr.isEmpty (id2) || id2.equals("0"))
	    id2 = "";
	 
	 
	 StringBuffer response = new StringBuffer(mgr.isEmpty(id2) ? "": id2) ;
	 response.append("^");
	 mgr.responseWrite(response.toString());
	 mgr.endResponse();
     }

   }

   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");    

      f = blk.addField ("ID1");
      f.setHidden();

      f = blk.addField("ID2");
      f.setHidden();
      
      f = blk.addField("NUMBER_GENERATOR");
      f.setHidden();
      
      f = blk.addField("NUM_GEN_TRANSLATED");
      f.setHidden();

      f = blk.addField("DOC_CLASS");
      f.setHidden();

      f = blk.addField("STR_IN");
      f.setHidden();

      rowset = blk.getASPRowSet();
      cmdbar = mgr.newASPCommandBar(blk);
      tbl = mgr.newASPTable(blk);

      cmdbar.disableCommand(cmdbar.BACK);
      cmdbar.disableCommand(cmdbar.SAVERETURN);   
      cmdbar.disableCommand(cmdbar.SAVENEW);   
      cmdbar.disableCommand(cmdbar.EDITROW);   
      cmdbar.disableCommand(cmdbar.FORWARD);  
      cmdbar.disableCommand(cmdbar.BACKWARD);            
      cmdbar.disableCommand(cmdbar.NEWROW);     
      cmdbar.disableCommand(cmdbar.FIND);     
      cmdbar.disableCommand(cmdbar.COUNTFIND);        
      cmdbar.disableCommand(cmdbar.OKFIND);        
      cmdbar.disableCommand(cmdbar.CANCELFIND);        
      cmdbar.disableCommand(cmdbar.CANCELNEW);   
      cmdbar.disableCommand(cmdbar.CANCELEDIT);        
   }   



//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "Docmaw Create Document Portlet Util";
   }

   protected String getTitle()
   {
      return "Docmaw Create Document Portlet Util";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML("<html>\n");
      appendToHTML("<head>\n");
      appendToHTML("<title>Docmaw Create Document Portlet Util</title>\n");
      appendToHTML("</head>\n");
      appendToHTML("<body>\n");
      appendToHTML("<form>\n");
      appendToHTML("</form>\n");
      appendToHTML("</body>\n");
      appendToHTML("</html>\n");
   }
}
