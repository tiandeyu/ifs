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
*  File        : FileTypeLov.java 
*  Created     :
*  2004-04-30  inoslk   Created for BUG ID 43788.
*  2004-05-24  bakalk   added this file to Edge harvest.  
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class FileTypeLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.FileTypeLov");


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
   public FileTypeLov(ASPManager mgr, String page_path)
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
      FileTypeLov page = (FileTypeLov)(super.clone(obj));

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

   
      blk = mgr.newASPBlock("EDMAPPTYPES");
      blk.setView("EDM_APPLICATION"); 
   
      f = blk.addField( "FILE_TYPE" );
      f.setLabel("DOCMAWFILETYPELOVFILETYPE: File Type");
      f.setSize(10);
      f.setMaxLength(30);
   
      f = blk.addField( "FILE_EXTENTION" );
      f.setLabel("DOCMAWFILETYPELOVFILEEXT: File Extension");
      f.setSize(10);
      f.setMaxLength(254);
   
      f = blk.addField( "DESCRIPTION" );
      f.setLabel("DOCMAWFILETYPELOVFILETYPEDESC: Description");
      f.setSize(30);
      f.setMaxLength(250);
   
      f = blk.addField( "DATA_OUT" );
      f.setFunction(":FILE_TYPE ||' ('|| :DESCRIPTION ||')'");
      f.setSize(200);
      f.setHidden();
   
      tbl = mgr.newASPTable(blk);
      tbl.setTitle("DOCMAWFILETYPELOVTITLE: File Types");
      tbl.setKey("DATA_OUT"); 
      tbl.disableQuickEdit();    
   
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      defineLOV();

   }


}
