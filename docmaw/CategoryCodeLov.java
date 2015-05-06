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
*  File        : CategoryCodeLov.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-13  - Created Using the ASP file CategoryCodeLov.asp
* ----------------------------------------------------------------------------
*/

 
package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CategoryCodeLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.CategoryCodeLov");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock blk;
   private ASPTable tbl;
   private ASPBlockLayout lay;

   //===============================================================
   // Construction 
   //===============================================================
   public CategoryCodeLov(ASPManager mgr, String page_path)
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
      CategoryCodeLov page = (CategoryCodeLov)(super.clone(obj));

      // Initializing mutable attributes
      
      // Cloning immutable attributes
      page.blk = page.getASPBlock(blk.getName());
      page.tbl = page.getASPTable(tbl.getName());
      page.lay = page.blk.getASPBlockLayout();

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

   
   
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

   
      blk = mgr.newASPBlock("ITEM");
      blk.setView("DOC_REFERENCE_CATEGORY");
   
      blk.addField("OBJID").       
          setHidden();
   
      blk.addField("OBJVERSION").       
          setHidden();
   
   
      blk.addField("CATEGORY").       
          setLabel("DOCMAWCATEGORYCODELOVCATEGORY: Category").
          setSize(20);
   
      blk.addField("CATEGORY_NAME").       
          setLabel("DOCMAWCATEGORYCODELOVCATEGORYNAME: Category Name ").
          setSize(30);
   
      tbl = mgr.newASPTable(blk);
      tbl.setTitle("DOCMAWCATEGORYCODELOVTBLCATEGORY: Category");   
      tbl.setKey("CATEGORY");
   
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);     
   
      defineLOV();

   }


}
