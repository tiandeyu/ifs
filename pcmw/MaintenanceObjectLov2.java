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
*  File        : MaintenanceobjectLov2.java 
*  History
*  2004-04-27  BAKALK   Created.
*  CHANLK      100212  	Bug 88985, increase field size of MCH_CODE, MCH_NAME.
* ----------------------------------------------------------------------------
*/

 
package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class MaintenanceObjectLov2 extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceObjectLov2");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock blk;
   private ASPTable tbl;
   private ASPBlockLayout lay;

   //===============================================================
   // Construction 
   //===============================================================
   public MaintenanceObjectLov2(ASPManager mgr, String page_path)
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
      MaintenanceObjectLov2 page = (MaintenanceObjectLov2)(super.clone(obj));

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
      blk.setView("MAINTENANCE_OBJECT");
   
      blk.addField("OBJID").       
          setHidden();
   
      blk.addField("OBJVERSION").       
          setHidden();

 		//Bug 88985,Start 
      blk.addField("MCH_CODE").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2OBJID: Object ID").
          setSize(100);

      blk.addField("MCH_NAME").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2DESC: Description").
          setSize(150);
 		//Bug 88985,End 

      blk.addField("MCH_LOC").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2ROOM: Room").
          setSize(20);

      blk.addField("GROUP_ID").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2GROUP: Group").
          setSize(20);

      blk.addField("MCH_TYPE").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2OBJTYPE: Object Type").
          setSize(20);

      blk.addField("CATEGORY_ID").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2CATEGORY: Category").
          setSize(20);

      blk.addField("SUP_MCH_CODE").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2SUPOBJECT: Superior Object").
          setSize(20);

      blk.addField("TYPE").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2TYPEDES: Type Designation").
          setSize(20);

      blk.addField("SUP_CONTRACT").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2SUPSITE: Superior Site").
          setSize(20);

      blk.addField("OPERATIONAL_STATUS").       
          setLabel("PCMWMAINTENANCEOBJECTLOV2OPERATIONALSTAT: Operational Status").
          setSize(20);

      tbl = mgr.newASPTable(blk);
      tbl.setTitle("PCMWMAINTENANCEOBJECTLOV2OBJID: Object ID");   
      tbl.setKey("MCH_CODE");
   
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);   
      
   
      defineLOV();

   }


}
