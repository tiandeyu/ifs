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
*  File        : Id1Lov.java 
*  Created     : DhPelk 030520 Created to fix call 95271
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Id1Lov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.Id1Lov");


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
   public Id1Lov(ASPManager mgr, String page_path)
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
      Id1Lov page = (Id1Lov)(super.clone(obj));

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
      f.setLabel("DOCMAWID1LOVID1: Id1");
      f.setSize(15);
      
      f = blk.addField( "ID2");
      f.setLabel("DOCMAWID1LOVID2: Id2");
      f.setSize(15);
   
      f = blk.addField( "START_VALUE");
      f.setLabel("DOCMAWID1LOVSTARTVALUE: Start Value");
      f.setSize(15);
      
      f = blk.addField( "PREFIX");
      f.setLabel("DOCMAWID1LOVPREFIX: Prefix");
      f.setSize(15);
      
      f = blk.addField( "SUFFIX");
      f.setLabel("DOCMAWID1LOVSUFFIX: Suffix");
      f.setSize(15);
   
      tbl = mgr.newASPTable(blk);
      tbl.setTitle("DOCMAWID1LOVNUMBERCOUNTERS: Number Counter ID1");
      tbl.setKey("ID1"); 
      tbl.disableQuickEdit();    
   
   
      //bar = blk.getASPCommandBar();
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      defineLOV();

   }


}
