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
 *  File        : Branch.java 
 *  Created     : Nimalk 2002-11-27
 *  Modified    : Nimalk 2002-12-30
 * ----------------------------------------------------------------------------
 *  Modified    :
 *  Usheera     : 2003-01-30 - Added RMB option 'Delivery Note Number Series'
 *  MAWELK      : 2005-04-09 - FIPR364 - Corrected of web tags.
 *  Jakalk      : 2005-09-06 - Code Cleanup, Removed doReset and clone methods.
 *  Maselk      : 2006-12-27 - Merged LCS Bug 58216, Fixed SQL Injection threats.
 * ----------------------------------------------------------------------------
 */

package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Branch extends ASPPageProvider
{       

   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.Branch");
   
   private static String APP_ROOT;  // Initialized in predefine

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext     ctx;
   private ASPBlock       headblk;
   private ASPBlock       itemblk;
   private ASPRowSet      headset;
   private ASPRowSet      itemset;
   private ASPCommandBar  headbar;
   private ASPCommandBar  itembar;
   private ASPTable       headtbl;
   private ASPTable       itemtbl;
   private ASPBlockLayout headlay;
   private ASPBlockLayout itemlay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery   q;
   private ASPCommand cmd;
   private ASPBuffer  row;
   private ASPBuffer  data;

   private String     company;
   private String     title;

   //===============================================================
   // Construction 
   //===============================================================

   public Branch(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if(mgr.dataTransfered())
         okFind();
     
     adjust();
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      q = trans.addQuery(headblk);

      if ( mgr.dataTransfered())
         q.addOrCondition( mgr.getTransferedData());

      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
          
      if ( headset.countRows() == 0 )  
      {   
         mgr.showAlert(mgr.translate("ENTERWBRANCHNODATAFOUND: No Data Found.")); 
         headset.clear();
      }
      else
      {
         mgr.createSearchURL(headblk);
      }
      
      headset.syncItemSets();
   }

   public void  okFindITEM()
   {
      if (headset.countRows() > 0) 
      {
         trans.clear();
         ASPManager mgr = getASPManager();

         int currrow;
            
         currrow = headset.getCurrentRowNo();

         q = trans.addQuery(itemblk);  
         q.addWhereCondition("COMPANY = ?");
         q.addParameter("COMPANY", headset.getValue("COMPANY"));
         
         if( mgr.dataTransfered() )
         {
            q.addOrCondition( mgr.getTransferedData() );
         }
         q.includeMeta("ALL");
         mgr.querySubmit(trans,itemblk);
         headset.goTo(currrow);
       }
    }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void  newRowITEM()
   {
      ASPManager mgr = getASPManager();
      
      cmd = trans.addEmptyCommand("ITEM","Branch_api.New__",itemblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      
      data = trans.getBuffer("ITEM/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      itemset.addRow(data);
   }

   public void  saveReturn()
   {
      ASPManager mgr = getASPManager();

      headset.changeRow();
      mgr.submit(trans);         
   }

   /**
    * Transfers data to order module
    */
   public void deliveryNoteNumberSeries()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer  buf = null;
      
      itemset.store();
      itemset.unselectRows();  // Remove if multiple selections exists
      itemset.selectRow();     // Select the current row
      
      buf = itemset.getSelectedRows("COMPANY,BRANCH");  // DeliveryNoteNumberSeries needs these fields like this
      
      mgr.transferDataTo( APP_ROOT + "orderw/DeliveryNoteNumberSeries.page", buf);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPField   f;
      APP_ROOT = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      headblk  = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();
         
      headblk.addField("COMPANY").
         setSize(20).
         setLabel("ENTERWBRANCHCOMPANY: Company").
         setUpperCase().
         setReadOnly();

      headblk.addField("NAME").
         setSize(40).
         setLabel("ENTERWBRANCHNAME: Name").
         setUpperCase().
         unsetQueryable().
         setReadOnly();
         
      headblk.setView("COMPANY");
           
      headbar = mgr.newASPCommandBar(headblk);
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("ENTERWBRANCHCOMPFINANCETBL: Company Finance"));

      headset = headblk.getASPRowSet();

      headbar.disableCommand(ASPCommandBar.NEWROW);

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);

      itemblk = mgr.newASPBlock("ITEM");

      itemblk.addField("OBJID_ITEM"). 
         setDbName("OBJID").
         setHidden();
      
      itemblk.addField("OBJVERSION_ITEM").
         setDbName("OBJVERSION").
         setHidden();

      itemblk.addField("COMPANY_HEAD").
         setDbName("COMPANY").
         setHidden();

      itemblk.addField("BRANCH").
         setSize(23).
         setMandatory().
         setLabel("ENTERWBRANCHBRANCH: Branch").
         setUpperCase();
      
      itemblk.addField("BRANCH_DESC").
         setSize(35).
         setMandatory().
         setLabel("ENTERWBRANCHDESCRIPTION: Description");
      
      itemblk.setView("BRANCH");
      itemblk.defineCommand("BRANCH_API", "New__,Modify__,Remove__");
      itemblk.setMasterBlock(headblk);

      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      itemset = itemblk.getASPRowSet();

      itembar = mgr.newASPCommandBar(itemblk);
      itembar.defineCommand(ASPCommandBar.NEWROW, "newRowITEM");
      itembar.addCustomCommand("deliveryNoteNumberSeries", "ENTERWBRANCHDELNTE: Delivery Note Number Series...");

      // If the files for Delivery Note Number Series is not there no use of this RMB
      if (!mgr.isPresentationObjectInstalled("orderw/DeliveryNoteNumberSeries.page"))
      {
         itembar.disableCustomCommand("deliveryNoteNumberSeries");
      }
      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.setTitle("ENTERWBRANCHTITLE: Branches");
   }

   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      company = ctx.findGlobal("COMPANY", "");

      if (itemlay.isEditLayout() )
      {
         mgr.getASPField("BRANCH").setReadOnly();
      }

      if (headset.countRows() == 0)
      {
         headbar.disableCommand(ASPCommandBar.FORWARD);
         headbar.disableCommand(ASPCommandBar.BACKWARD);
         headbar.disableCommand(ASPCommandBar.BACK);
      }
      else if (itemset.countRows() == 0)
      {
         itembar.disableCommand(ASPCommandBar.BACKWARD);
         itembar.disableCommand(ASPCommandBar.DUPLICATEROW);
         itembar.disableCommand("deliveryNoteNumberSeries");
      }
      title = mgr.translate("ENTERWBRANCHTITLE: Branches");
   }


   protected String getDescription()
   {
      return title;
   }

   protected String getTitle()
   {
      return "ENTERWBRANCHTITLE: Branches";
   }

   protected void printContents() throws FndException
   {
      if (headlay.isVisible())
         appendToHTML(headlay.show());
      if((itemlay.isVisible()) && (headset.countRows() > 0))
         appendToHTML(itemlay.show());
   }
}
