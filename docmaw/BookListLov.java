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
*  File        : BookListLov.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-13  - Created Using the ASP file BookListLov.asp
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class BookListLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.BookListLov");


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
   public BookListLov(ASPManager mgr, String page_path)
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
      BookListLov page = (BookListLov)(super.clone(obj));

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

   
      blk = mgr.newASPBlock("BOOKINGLIST");
      blk.setView("DOC_NUM_BOOKING"); 
      
      
      f = blk.addField( "BOOKING_LIST");
      f.setLabel("DOCMAWBOOKLISTLOVBOOKINGLIST: Booking List");
      f.setSize(15);
      
      f = blk.addField( "DESCRIPTION");
      f.setLabel("DOCMAWBOOKLISTLOVDESCRIPTION: Description");
      f.setSize(15);
      
      f = blk.addField( "ID1");
      f.setLabel("DOCMAWBOOKLISTLOVID1: Id1");
      f.setSize(15);
      
      f = blk.addField( "ID2");
      f.setLabel("DOCMAWBOOKLISTLOVID2: Id2");
      f.setSize(15);
   
      
   
      
   
      tbl = mgr.newASPTable(blk);
      tbl.setTitle("DOCMAWBOOKLISTLOVBOOKINGLIST: Booking List");
      tbl.setKey("BOOKING_LIST"); 
      tbl.disableQuickEdit();    
   
   
      //bar = blk.getASPCommandBar();
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      defineLOV();

   }


}
