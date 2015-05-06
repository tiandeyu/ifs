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
*  File        : Id2Lov.java 
*  Created     : Bakalk 12th Feb 2003
*  2005-03-14  SUKMLK   Merged Bug 48882
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Id2Lov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.Id2Lov");


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
   public Id2Lov(ASPManager mgr, String page_path)
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
      Id2Lov page = (Id2Lov)(super.clone(obj));

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

   
      blk = mgr.newASPBlock("NUMBER_COUNT");
      blk.setView("DOC_NUMBER_COUNTER"); 
   
      f = blk.addField( "ID1" );
      f.setLabel("DOCMAWID2LOVID1: Id1");
      f.setSize(15);
      
      f = blk.addField( "ID2");
      f.setLabel("DOCMAWID2LOVID2: Id2");
      f.setSize(15);
         
      f = blk.addField( "DESCRIPTION");
      f.setLabel("DOCMAWID2LOVDESC: Description");
      f.setSize(35);
   
      tbl = mgr.newASPTable(blk);
      tbl.setTitle("DOCMAWID2LOVNUMBERCOUNTERS: Number Counters");
      tbl.setKey("ID2"); 
      tbl.disableQuickEdit();       
         
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      defineLOV();

   }


}
