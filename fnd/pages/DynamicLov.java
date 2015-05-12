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
*  File        : DynamicLov.java
*  Modified    :
*    ASP2JAVA Tool  2000-12-22  - Created Using the ASP file DynamicLov.asp
*    Suneth M       2001-08-31  - Added mgr.setPageExpiring()
* ----------------------------------------------------------------------------
* New Comments:
* 2008/06/26 mapelk Bug 74852, Programming Model for Activities.
* 
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.2  2005/02/11 09:12:11  mapelk
* Remove ClientUtil applet and it's usage from the framework
*
* Revision 1.1  2005/01/28 18:07:26  marese
* Initial checkin
*
* Revision 1.2  2004/11/25 05:58:02  chdelk
* Added support for Activity APIs based LOVs.
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DynamicLov extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.DynamicLov");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock blk;
   private ASPTable tbl;
   private ASPLov lov;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String name;

   //===============================================================
   // Construction
   //===============================================================
   public DynamicLov(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      name   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DynamicLov page = (DynamicLov)(super.clone(obj));

      page.name   = null;

      page.blk = page.getASPBlock(blk.getName());
      page.tbl = page.getASPTable(tbl.getName());
      page.lov = page.getASPLov();

      return page;
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      mgr.setPageExpiring();

      disableValidation();
      blk = mgr.newASPBlock("LOV");
      blk.addField("DUMMY").setFunction("NULL").setHidden();

      tbl = mgr.newASPTable(blk);
      name = mgr.readValue("__FIELD");
      tbl.setTitle(name);

      lov = defineLOV();
      
      // Modified by Jack Zhang,20100710 15:58:50
      // Original: lov.generateDefinition(mgr.readValue("__DYNAMIC_LOV_VIEW"));
      String force_key = mgr.readValue("__FORCE_KEY");
      if (!mgr.isEmpty(force_key))
         lov.generateDefinition(mgr.readValue("__DYNAMIC_LOV_VIEW"), force_key, null, null);
      else
         lov.generateDefinition(mgr.readValue("__DYNAMIC_LOV_VIEW"));
      // Modified end
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "List of Values";
   }

   protected String getTitle()
   {
      return "List of Values";
   }
}
