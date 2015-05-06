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
*  File        : ObjectConnectionLov.java 
*  Converted   : Bakalk on 2001-03-23 using  ASP file ObjectConnectionLov.asp
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ObjectConnectionLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.ObjectConnectionLov");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPField f;
   private ASPTable tbl;
   private ASPCommandBar bar;
   private ASPBlockLayout lay;

   //===============================================================
   // Construction 
   //===============================================================
   public ObjectConnectionLov(ASPManager mgr, String page_path)
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
      ObjectConnectionLov page = (ObjectConnectionLov)(super.clone(obj));

      // Initializing mutable attributes
      
      // Cloning immutable attributes
      page.headblk = page.getASPBlock(headblk.getName());
      page.f = page.getASPField(f.getName());
      page.tbl = page.getASPTable(tbl.getName());
      page.bar = page.headblk.getASPCommandBar();
      page.lay = page.headblk.getASPBlockLayout();

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

   
   
   
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      setVersion(3);
   
      headblk = mgr.newASPBlock("LU_NAME1");
	  
      
   
      f = headblk.addField("LU_DESC");
      f.setSize(30);
      f.setLabel("DOCMAWOBJECTCONNECTIONLOVLU_DESC: Object");
      f.setReadOnly();
   
      f = headblk.addField("LU_NAME");
      f.setSize(15);
      f.setLabel("DOCMAWOBJECTCONNECTIONLOVLUNAME: Description");
      f.setHidden();
   
      f = headblk.addField("VIEW_NAME");
      f.setSize(15);
      f.setLabel("DOCMAWOBJECTCONNECTIONLOVVIEWNAM: View Name");
      f.setHidden();
   
      f = headblk.addField("SERVICE_LIST");
      f.setSize(18);
      f.setLabel("DOCMAWOBJECTCONNECTIONLOVSERVICLIST: Service List");
      f.setHidden();
      
      
      headblk.setView("OBJECT_CONNECTION");
      tbl = mgr.newASPTable(headblk);
      tbl.setTitle("DOCMAWOBJECTCONNECTIONLOVTITLE: Objects");
      tbl.disableQuickEdit();    
   
   
      lay = headblk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      defineLOV();
   }


}
