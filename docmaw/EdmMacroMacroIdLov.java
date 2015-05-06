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
*  File        : EdmMacroMacroIdLov.java
*  Nisilk      :2003-01-30  - Created. Lov to retrieve the Macro Id
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class EdmMacroMacroIdLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.EdmMacroMacroIdLov");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock blk;
   private ASPTable tbl;
   private ASPBlockLayout lay;        

   //===============================================================
   // Construction 
   //===============================================================
   public EdmMacroMacroIdLov(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      super.doReset();               
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      EdmMacroMacroIdLov page = (EdmMacroMacroIdLov)(super.clone(obj));

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


      blk = mgr.newASPBlock("HEAD");
      blk.setView("EDM_MACRO");

      blk.addField("MACRO_ID").
      setSize(20).
      setLabel("MACROID: Macro Id");              

      blk.addField("PROCESS").
      setSize(15).
      setLabel("PROCESS: Process");              

      blk.addField("FILE_TYPE").
      setLabel("FILETYPE: File Type");             

      blk.addField("ACTION").
      setSize(40).
      setLabel("ACTION: Action");                               


      tbl = mgr.newASPTable(blk);
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      tbl.setTitle(mgr.translate("EDMMACROMACROID: List of MAcro Id's"));
      tbl.setKey("MACRO_ID");              
      defineLOV(); 



   }

}
