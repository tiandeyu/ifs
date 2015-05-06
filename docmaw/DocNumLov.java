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
*  File        : DocNumLov.java
*  Modified    :
*  2003-08-27  INOSLK  Call ID 101731: Modified method clone().
*  2004-03-23  DIKALK  SP1-Merge. Bug Id 42916, Removed the setSize function
*                      from the field DOC_NO.
*  2006-02-12  ThWilk  Call ID 132041,Changed the view from DOC_TITLE to DOC_ISSUE and 
*                      added/removed some fields to method predefine.
*  2007-08-01  Janslk  Call ID 144662, Concatenated DOC_CLASS to "RET_FIELD" so that it will be also transffered back to the parent window.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocNumLov extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocNumLov");


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
   public DocNumLov(ASPManager mgr, String page_path)
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
      DocNumLov page = (DocNumLov)(super.clone(obj));

      //Initializing mutable attributes

      //Cloning immutable attributes
      page.blk = page.getASPBlock(blk.getName());
      page.f = page.getASPField(f.getName());
      page.tbl = page.getASPTable(tbl.getName());
      page.lay = page.blk.getASPBlockLayout();
      page.bar = page.blk.getASPCommandBar();

      return page;
   }

   public void run()
   {

   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("DOCISSUE");

      f = blk.addField( "DOC_CLASS" );
      f.setLabel("DOCMAWDOCNUMLOVDOCCLASS: Document Class");
      f.setSize(12);

      f = blk.addField( "DOC_NO" );
      f.setLabel("DOCMAWDOCNUMLOVDOCNO: Document No");

      f = blk.addField( "DOC_SHEET" );
      f.setLabel("DOCMAWDOCNUMLOVDOCSHEET: Document Sheet");

      f = blk.addField( "DOC_REV" );
      f.setLabel("DOCMAWDOCNUMLOVDOCREV: Document Revision");

      f = blk.addField( "REV_NO" );
      f.setLabel("DOCMAWDOCNUMLOVREVNO: Revision No");

      f = blk.addField( "DOC_REV_TEXT" );
      f.setLabel("DOCMAWDOCNUMLOVREVTEST: Document Revision Note");

      f = blk.addField( "INFO" );
      f.setLabel("DOCMAWDOCNUMLOVNOTE: Note");

      f = blk.addField( "ACCESS_CONTROL" );
      f.setLabel("DOCMAWDOCNUMLOVACCESSCTRL: Access Control");

      f= blk.addField("RET_FIELD");
      f.setFunction("DOC_CLASS"+"||"+"'^'"+"||"+"DOC_NO"+"||"+"'^'"+"||"+"DOC_SHEET"+"||"+"'^'"+"||"+"DOC_REV"+"||"+"'^'");
      f.setHidden();


      /*f = blk.addField( "TITLE" );
      f.setLabel("DOCMAWDOCNUMLOVDOCTITLE: Title");
      f.setSize(47);

      f = blk.addField( "OBJ_CONN_REQ" );
      f.setLabel("DOCMAWDOCNUMLOVOBJCONN: Object Connection");
      f.setSize(10);
   	
      f = blk.addField( "SAFETY_COPY_REQ" );
      f.setLabel("DOCMAWDOCNUMLOVSAFECOP: Safety Copy Req");
      f.setSize(10);
      
      f = blk.addField( "VIEW_FILE_REQ" );
      f.setLabel("DOCMAWDOCNUMLOVVIEWFILE: View Copy Required");
      f.setSize(10);

      f = blk.addField( "MAKE_WASTE_REQ" );
      f.setLabel("DOCMAWDOCNUMLOVDESTROY: Destroy");
      f.setSize(4); */

      blk.setView("DOC_ISSUE");

      tbl = mgr.newASPTable(blk);
      tbl.setTitle("DOCMAWDOCNUMLOVDOCNO1: List of Document No");
      tbl.disableQuickEdit();
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      tbl.setKey("RET_FIELD");
      defineLOV();
   }


}
