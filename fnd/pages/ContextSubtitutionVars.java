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
* File                          : ContextSubtitutionVars.java
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Modified    :
* 2006/09/24      mapelk      Improved CSV code 
* 2006/09/21      buhilk      Improved code and added translation constants
* 2006/09/19      buhilk      Added functionality to reload CSV registry cache by modifying the run()
* 2006/09/12      buhilk      Created.
* ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ContextSubtitutionVars extends ASPPageProvider
{

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ContextSubtitutionVars");

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   public  ContextSubtitutionVars (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      
      if(mgr.buttonPressed("RELOADCSV"))
      {
         mgr.resetCSVCache();
      }
      
      adjust();
   }

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("FNDPAGESCSVNODATA: No data found.");
         headset.clear();
      }
   }

   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }

   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","CONTEXT_SUBSTITUTION_VAR_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("NAME").
              setMandatory().
              setInsertable().
              setLabel("FNDPAGESCSVNAME: Name").
              setSize(80);
      headblk.addField("SERVER_METHOD").
              setLabel("FNDPAGESCSVMETHOD: Server Method").
              setSize(80);
      headblk.addField("IMPLEMENTATION_TYPE").
              setMandatory().
              setLabel("FNDPAGESCSVIMPLTYPE: Implementation Type").
              enumerateValues("IMPLEMENTATION_TYPE_API").
              setSelectBox();
      headblk.addField("TRANSIENT").
              setMandatory().
              setLabel("FNDPAGESCSVTRANSIENT: Transient").
              enumerateValues("FND_BOOLEAN_API").
              setSelectBox();
      headblk.addField("MODULE").
              setMandatory().
              setLabel("FNDPAGESCSVMODULE: Module").
              enumerateValues("MODULE_API.Enumerate","").
              setSelectBox();
      headblk.addField("FND_DATA_TYPE").
              setMandatory().
              setLabel("FNDPAGESCSVDATATYPE: Data Type").
              enumerateValues("FND_DATA_TYPE_API").
              setSelectBox();
      headblk.setView("CONTEXT_SUBSTITUTION_VAR");
      //headblk.defineCommand("CONTEXT_SUBSTITUTION_VAR_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("FNDPAGESCSVTBLHEAD: Context Substitution Variables");
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

   }


   public void  adjust()
   {
   }


   protected String getDescription()
   {
      return "FNDPAGESCSVDESC: Context Substitution Variables";
   }


   protected String getTitle()
   {
      return "FNDPAGESCSVTITLE: Context Substitution Variables";
   }


   protected void printContents() throws FndException
   {
     /* ASPManager mgr = getASPManager();
      printSpaces(2);
      printSubmitButton("RELOADCSV",mgr.translate("FNDPAGESCSVRLDCACHE: Reload CSV Registry Cache"),"");
      printNewLine();
      printNewLine();*/
      //if (headlay.isVisible())
      appendToHTML(headlay.show());   
   }
}
