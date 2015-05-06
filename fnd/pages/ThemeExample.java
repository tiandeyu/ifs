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
*  File        : ThemeExample.java
*  Modified    :
*   Mangala 2007-01-05 Created
*   Buddika 2007-03-06 Bug 63950, disabled application support
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ThemeExample extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ThemeExample");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay;



   //===============================================================
   // Construction
   //===============================================================
   public ThemeExample(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();

      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else
         addData();
   }
   
   private void addData()
   {
       ASPBuffer buf = getASPManager().newASPBuffer();
       buf.addItem("HEAD1", "GLB100000");
       buf.addItem("HEAD2", "Copy machine stops with error code.");
       buf.addItem("HEAD3", "Global");
       buf.addItem("HEAD4", "CUST2");
       buf.addItem("HEAD5", "Customer 2");
       buf.addItem("HEAD6", "Ref 10000");
       buf.addItem("HEAD7", "6010");
       headset.addRow(buf);
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("BLOCK1");

      headblk.addField("HEAD1").
              setSize(10).
              setLabel("Case ID");

      headblk.addField("HEAD2").
              setSize(10).
              setLabel("Title");
      
      headblk.addField("HEAD3").
              setSize(10).
              setLabel("Case Type");
      
      headblk.addField("HEAD4").
              setSize(10).
              setLabel("Customer No");

      headblk.addField("HEAD5").
              setSize(10).
              setLabel("Customer Name");
      
      headblk.addField("HEAD6").
              setSize(10).
              setLabel("Customer Reference");      
      
      headblk.addField("HEAD7").
              setSize(10).
              setLabel("Organization ID");

      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      

      disableHomeIcon();
      disableNavigate();
      disableConfiguration();
      disableOptions();
      disableHelp();
      disableSignoutLink();
      disableSettingsLink();

      headtbl = mgr.newASPTable(headblk);

      headlay = headblk.getASPBlockLayout();
      headlay.defineGroup("Group 1", "HEAD1,HEAD2,HEAD3",false, true);
      headlay.defineGroup("Customer Reference ", "HEAD4,HEAD5,HEAD6,HEAD7",true, true);
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      
      this.disableApplicationSearch();
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDTHEMEEXAMPLE: Theme Preview";
   }

   protected String getTitle()
   {
      return "FNDTHEMEEXAMPLE: Theme Preview";
   }

   protected void printContents() throws FndException
   {
      beginDataPresentation();
      drawSimpleCommandBar(" Cases");
      endDataPresentation(false);
      appendToHTML(headlay.generateDataPresentation());
   }

}
