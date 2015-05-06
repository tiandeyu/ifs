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
*  File        : PhraseTextLov.java 
*  Modified    :
*                Muneera  -  21-May-2003 -  Created
* ----------------------------------------------------------------------------
*/


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class PhraseTextLov extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.PhraseTextLov");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPTable tbl;
   private ASPCommandBar bar;
   private ASPBlockLayout lay;

   //===============================================================
   // Construction 
   //===============================================================
   public PhraseTextLov(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {

   
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("NOTES");

      headblk.setView("MPCCOM_PHRASE_TEXT"); 
   
      headblk.addField("OBJID").
         setHidden();
      
      headblk.addField("OBJVERSION").
         setHidden();

      headblk.addField("PHRASE_ID").
         setSize(20).
         setLabel("MROMFWPHRASETEXTLOVPHRASEID: Phrase ID");
      
      headblk.addField("LANGUAGE_CODE").
         setSize(20).
         setLabel("MROMFWPHRASETEXTLOVLANGUAGECODE: Language Code");
      
      headblk.addField("PHRASE_TEXT").
         setLabel("MROMFWPHRASETEXTLOVPHRASETEXT: Phrase Text");
      
      tbl = mgr.newASPTable(headblk);
      tbl.setTitle("MROMFWPHRASETEXTLOVNOTE: Notes");
      tbl.setKey("PHRASE_TEXT"); 
      tbl.disableQuickEdit();    
   
      defineLOV();
      bar = headblk.getASPCommandBar();
      lay = headblk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
   }
}
