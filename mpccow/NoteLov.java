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
*  File        : NoteLov.java 
*  VaGulk    2003-05-10  - Created. Call ID - 97068.
*  JEWILK    2003-07-17  - Scream Merge.
* ----------------------------------------------------------------------------
*/


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class NoteLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.NoteLov");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock blk;
   private ASPTable tbl;
   private ASPBlockLayout lay;

   //===============================================================
   // Construction 
   //===============================================================
   public NoteLov(ASPManager mgr, String page_path)
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
      NoteLov page = (NoteLov)(super.clone(obj));

      // Initializing mutable attributes
      
      // Cloning immutable attributes
      page.blk = page.getASPBlock(blk.getName());
      page.tbl = page.getASPTable(tbl.getName());
      page.lay = page.blk.getASPBlockLayout();

      return page;
   }

   public void run() 
   {

   
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

   
      blk = mgr.newASPBlock("HEAD");
      blk.setView("MPCCOM_PHRASE_TEXT_LOV");
   
      blk.addField("DUMMY").
      setFunction("PHRASE_TEXT").
      setHidden();                  
   
      blk.addField("PHRASE_TEXT").
      setSize(50).
      setLabel("MPCCOWNOTELOVPHRASETEXT: Phrase Text");
   
      blk.addField("PHRASE_ID").
      setLabel("MPCCOWNOTELOVPHRASEID: Phrase ID");
   
      blk.addField("LANGUAGE_CODE").
      setLabel("MPCCOWNOTELOVLANGCODE: Language Code");
      
      tbl = mgr.newASPTable(blk);
      tbl.setKey("DUMMY");
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      tbl.setTitle(mgr.translate("MPCCOWNOTELOVTITLE: Note"));
      defineLOV();

   }


}
