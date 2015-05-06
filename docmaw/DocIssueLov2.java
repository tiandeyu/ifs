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
*  File        : DocIssueLov2.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-13  - Created Using the ASP file DocIssueLov2.asp
*    NISILK  2003-10-22   Added doc sheet.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocIssueLov2 extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocIssueLov2");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock blk;
   private ASPField f;
   private ASPTable tbl;
   private ASPCommandBar bar;
   private ASPBlockLayout lay;

   //===============================================================
   // Construction 
   //===============================================================
   public DocIssueLov2(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocIssueLov2 page = (DocIssueLov2)(super.clone(obj));

      // Initializing mutable attributes
      
      // Cloning immutable attributes
      page.blk = page.getASPBlock(blk.getName());
      page.f = page.getASPField(f.getName());
      page.tbl = page.getASPTable(tbl.getName());
      page.bar = page.blk.getASPCommandBar();
      page.lay = page.blk.getASPBlockLayout();

      return page;
   }

   public void run() 
   {

   
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

   
      blk = mgr.newASPBlock("DOCISSUE");
      blk.setView("DOC_ISSUE_LOV"); 
   
      f = blk.addField( "DOC_CLASS" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCCLASS: Doc.Class");
      f.setSize(10);
      
      f = blk.addField( "DOC_NAME" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCNAME: Doc.Name");
      f.setSize(10);
      
      f = blk.addField( "SUB_CLASS_NAME" );
      f.setLabel("DOCMAWDOCISSUELOV2SUBCLASSNAME: Sub Class Name");
      f.setSize(10);
      
      f = blk.addField( "INNER_DOC_CODE" );
      f.setLabel("DOCMAWDOCISSUELOV2INNERDOCCODE: Inner Doc Code");
      f.setSize(20);
      
      f = blk.addField( "DOC_CODE" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCCODE: Doc Code");
      f.setSize(20);
      
      f = blk.addField( "DOC_TITLE" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCTITLE: Doc.Title");
      f.setSize(20);
      
      f = blk.addField( "DOC_REV" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCREV: Doc.Rev.");
      f.setSize(8);
      
      f = blk.addField( "DOC_STATE" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCSTATE: Doc State");
      f.setSize(8);
      
      f = blk.addField( "SEND_UNIT_NAME" );
      f.setLabel("DOCMAWDOCISSUELOV2SENDUNITNAME: Send Unit Name");
      f.setSize(20);
      
      f = blk.addField( "SIGN_PERSON" );
      f.setLabel("DOCMAWDOCISSUELOV2SIGNPERSON: Sign Person");
      f.setSize(10);
      
      f = blk.addField( "COMPLETE_DATE", "Date");
      f.setLabel("DOCMAWDOCISSUELOV2COMPLETEDATE: Complete Date");
      f.setSize(10);
      
      f = blk.addField( "DOC_NO" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCNO: Doc.No");
      f.setSize(8);
      f.setHidden();

      f = blk.addField( "DOC_SHEET" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCSHEET: Doc.Sheet");
      f.setSize(8);
      f.setHidden();
   
      f = blk.addField( "DT_CHG","Datetime" );
      f.setLabel("DOCMAWDOCISSUELOV2DATECHG: Date Changed");
      f.setSize(15);
      f.setHidden();
   
      f = blk.addField( "LANGUAGE_CODE" );
      f.setLabel("DOCMAWDOCISSUELOV2LANCODE: Language Code");
      f.setSize(13);
      f.setHidden();
   
      f = blk.addField( "DOC_REV_TEXT" );
      f.setLabel("DOCMAWDOCISSUELOV2DOCREVTEXT: Doc.Rev.Text");
      f.setSize(15);
      f.setHidden();
   
      f = blk.addField( "INFO" );
      f.setLabel("DOCMAWDOCISSUELOV2INFO: Info.");
      f.setSize(6);
      f.setHidden();
   
      f = blk.addField( "USER_CREATED" );
      f.setLabel("DOCMAWDOCISSUELOV2USERCRE: Created By");
      f.setSize(10);
      f.setHidden();
   
      f = blk.addField( "DT_CRE","Datetime" );
      f.setLabel("DOCMAWDOCISSUELOV2DATECRE: Date Created");
      f.setSize(12);
      f.setHidden();
   
      f = blk.addField( "USER_SIGN" );
      f.setLabel("DOCMAWDOCISSUELOV2CHNGBY: Changed By");
      f.setSize(10);
      f.setHidden();
   
      f = blk.addField( "DATA_OUT" );
      f.setFunction("DOC_CLASS||'^'||DOC_NAME||'^'||DOC_NO||'^'||DOC_TITLE||'^'||DOC_SHEET||'^'||DOC_REV||'^'||DOC_ISSUE_API.Get_State(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV)||'^'");
      f.setSize(10);
      f.setHidden();
   
      tbl = mgr.newASPTable(blk);
      tbl.setTitle("DOCMAWDOCISSUELOV2DOCIS: Documents");
      tbl.setKey("DATA_OUT"); 
      tbl.disableQuickEdit();    
   
   
      //bar = blk.getASPCommandBar();
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      defineLOV();

   }


}
