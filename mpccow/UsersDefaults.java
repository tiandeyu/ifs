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
 *  File         : UsersDefaults.java 
 *  Description  : UsersDefaults
 *  Notes        : 
 * ----------------------------------------------------------------------------
 *  Modified     :
 *     NaLrlk 02-Aug-2007 - Added validate,okFindAll, duplicateRowITEM0 functions.
 * ---------------------- Wings Merge End--------------------------------------
 *     RaKalk 09-May-2007 - Modified added Connect All Sites in company functionality
 *     ChJalk 30-Jan-2007 - Merged Wings Code.
 *     ChJalk 01-Nov-2006 - Created.
 * ---------------------- Wings Merge Start------------------------------------
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class UsersDefaults extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.UsersDefaults");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPBlock       headblk;
    private ASPRowSet      headset;
    private ASPCommandBar  headbar;
    private ASPTable       headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock       itemblk0;
    private ASPRowSet      itemset0;
    private ASPCommandBar  itembar0;
    private ASPTable       itemtbl0;
    private ASPBlockLayout itemlay0;

    private ASPTransactionBuffer trans;

    //===============================================================
    // Construction 
    //===============================================================
    public UsersDefaults(ASPManager mgr, String page_path)
    { 
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        
        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
            okFindAll();
        else if ( mgr.commandBarActivated() )
            eval(mgr.commandBarFunction());
        else if ( mgr.dataTransfered() )
            okFindAll();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
           validate();
        adjust();
    }

    //=============================================================================
    //  Command Bar Edit Group functions
    //=============================================================================

    public void  validate()
    {
       ASPManager           mgr   = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand           cmd   = null;
       String               sVal  = mgr.readValue("VALIDATE");
       String               txt   = null;

       if ("CONTRACT".equals(sVal))
       {
          cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","COMPANY");
          cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));

          cmd=trans.addCustomFunction("SDESC","Site_API.Get_Description","ITEM0_DESCRIPTION");
          cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));

          trans = mgr.validate(trans);

          String sCompany         = trans.getValue("COMP/DATA/COMPANY");
          String sSiteDescription = trans.getValue("SDESC/DATA/ITEM0_DESCRIPTION");

          txt = (mgr.isEmpty(sCompany) ? "" : sCompany) + "^" +
                (mgr.isEmpty(sSiteDescription) ? "" : sSiteDescription) + "^";

       }
       mgr.responseWrite( txt );
       mgr.endResponse();
    }

    public void  newRow()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPCommand cmd;
        ASPBuffer data;

        cmd = trans.addEmptyCommand("HEAD","USER_DEFAULT_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);
    }

    public void  newRowITEM0()
    {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("ITEM0","USER_ALLOWED_SITE_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_USERID",headset.getRow().getFieldValue("USERID"));
      itemset0.addRow(data);
    }

    public void duplicateRowITEM0()
    {
       ASPManager mgr = getASPManager();
       ASPBuffer  data;

       itemset0.store();
       data = itemset0.getRow();
       itemset0.addRow(data);
       itemlay0.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
    }


    //=============================================================================
    //  Command Bar Search Group functions
    //=============================================================================

    public void  countFind()
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

    public void  okFindAll()
    {
       okFind();
       okFindITEM0();
    }

    public void  okFind()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);

        if ( headset.countRows() == 0 )
        {
            mgr.showAlert("MPCCOWUSERSDEFAULTSNODATA: No data found.");
            headset.clear();
        }
        mgr.createSearchURL(headblk);
        eval(headset.syncItemSets());
    }

    public void  okFindITEM0()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;
        int currow = headset.getCurrentRowNo();

        itemset0.clear();
        q = trans.addEmptyQuery(itemblk0);   
        q.addWhereCondition("USERID = ? ");
        q.addParameter("USERID",headset.getValue("USERID"));
        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk0);

        headset.goTo(currow);
    }

    public void  deleteRowITEM0()
    {
      ASPManager  mgr     = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      itemset0.store();
      itemset0.setRemoved();
      mgr.submit(trans);
      itemset0.refreshAllRows();

    }

    public void  setDefaultSite()
    {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd;
       ASPBuffer data;

       int currentRow = headset.getCurrentRowNo();
       
       itemset0.store();          // Get what user has
       itemset0.unselectRows();   // To remove [invisible] multiple selection
       itemset0.selectRow();
  
       String obj    = itemset0.getRow().getFieldValue("ITEM0_OBJID");
       String objver = itemset0.getRow().getFieldValue("ITEM0_OBJVERSION");
       String attr   = "USER_SITE_TYPE_DB" + (char)31 + "DEFAULT SITE" + (char)30;

       cmd = trans.addCustomCommand("MODI","User_Allowed_Site_API.Modify__");
       cmd.addParameter("INFO");
       cmd.addParameter("OBJID",obj);
       cmd.addParameter("OBJVERSION",objver);
       cmd.addParameter("ATTR",attr);
       cmd.addParameter("ACTION","DO");
       trans = mgr.perform(trans);

       headset.goTo(currentRow);
       itemset0.refreshAllRows();
    }

    public void connectToAllSitesInCompany(){
       ASPManager mgr = getASPManager();
       headset.store();
       mgr.redirectTo("ConnectToAllSites.page?USER_ID=" + mgr.URLEncode(headset.getValue("USERID")));
    }
    
    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        headblk.addField( "OBJID" ).
        setHidden();

        headblk.addField( "OBJVERSION" ).
        setHidden();

        headblk.addField("USERID").
        setMandatory().
        setLabel("MPCCOWUSERSDEFAULTSUSERID: User Id").
        setDynamicLOV("FND_USER").
        setLOVProperty("TITLE","MPCCOWUSERSDEFAULTSUSERID: User Id").
        setSize(15).
        setMaxLength(30).
        setReadOnly().
        setInsertable().
        setUpperCase();

        headblk.addField("AUTHORIZE_CODE").
        setUpperCase().
        setLabel("MPCCOWUSERSDEFAULTSCOORDINATORID: Default Coordinator Id").
        setMaxLength(20).
        setDynamicLOV("ORDER_COORDINATOR_LOV").
        setLOVProperty("TITLE",mgr.translate("MPCCOWUSERSDEFAULTSCOORDINATORIDLOV: Default Coordinator Id")).
        setSize(15);

        headblk.addField("COORDINATOR_NAME").
        setFunction("ORDER_COORDINATOR_API.Get_Name(:AUTHORIZE_CODE)").
        setReadOnly().
        setSize(30);

        mgr.getASPField("AUTHORIZE_CODE").setValidation("COORDINATOR_NAME");

        headblk.addField("BUYER_CODE").
        setUpperCase().
        setLabel("MPCCOWUSERSDEFAULTSBUYERCODE: Default Buyer Id").
        setMaxLength(20).
        setDynamicLOV("PURCHASE_BUYER_LOV2").
        setLOVProperty("TITLE",mgr.translate("MPCCOWUSERSDEFAULTSBUYERCODELOV: Default Buyer Id")).
        setSize(15);

        headblk.addField("BUYER_NAME").
        setFunction("PURCHASE_BUYER_API.Get_Name(:BUYER_CODE)").
        setReadOnly().
        setSize(30);

        mgr.getASPField("BUYER_CODE").setValidation("BUYER_NAME");

        headblk.setView("USER_DEFAULT");
        headblk.defineCommand("USER_DEFAULT_API","New__,Modify__,Remove__");
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
        headlay.unsetAutoLayoutSelect();
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable( headblk );
        headtbl.setTitle(mgr.translate("MPCCOWUSERSDEFAULTSTBLTITLE: Users"));

        headbar = mgr.newASPCommandBar(headblk);
        headlay.setDialogColumns(1);
        headlay.setSimple("COORDINATOR_NAME");
        headlay.setSimple("BUYER_NAME");

        headbar.addCustomCommand("connectToAllSitesInCompany",mgr.translate("MPCCOWUSERDEFAULTS: Connect all Sites in Company..."));
        headbar.removeFromMultirowAction("connectToAllSitesInCompany");

        //===============================================================
        //   Block for Site 
        //===============================================================

        itemblk0 = mgr.newASPBlock("ITEM0");

        itemblk0.addField( "ITEM0_OBJID" ).
        setDbName("OBJID").
        setHidden();

        itemblk0.addField( "ITEM0_OBJVERSION" ).
        setDbName("OBJVERSION").
        setHidden();

        itemblk0.addField( "INFO" ).
        setFunction("''").
        setHidden();

        itemblk0.addField( "ATTR" ).
        setFunction("''").
        setHidden();

        itemblk0.addField( "ACTION" ).
        setFunction("''").
        setHidden();
        
        itemblk0.addField("ITEM0_USERID").
        setDbName("USERID").
        setHidden();

        itemblk0.addField("CONTRACT").
        setCustomValidation("CONTRACT","COMPANY,ITEM0_DESCRIPTION").
        setMandatory().
        setReadOnly().
        setInsertable().
        setLabel("MPCCOWUSERSDEFAULTSSITE: Site").
        setMaxLength(5).
        setDynamicLOV("SITE").
        setLOVProperty("TITLE",mgr.translate("MPCCOWUSERSDEFAULTSITELOV: Site")).
        setSize(10);
        
        itemblk0.addField("ITEM0_DESCRIPTION").
        setLabel("MPCCOWUSERSDEFAULTSSITEDESC: Site Description").
        setFunction("SITE_API.Get_Description(:CONTRACT)").
        setReadOnly().
        setSize(30);

        itemblk0.addField("COMPANY").
        setLabel("MPCCOWUSERDEFAULTSCOMPANY: Company").
        setFunction("SITE_API.Get_Company(:CONTRACT)").
        setReadOnly().
        setSize(20);

        itemblk0.addField("USER_SITE_TYPE_DB").
        setLabel("MPCCOWUSERSDEFAULTSUSERSITETYPEDB: Default Site").
        setReadOnly().
        setInsertable().
        setCheckBox("NOT DEFAULT SITE,DEFAULT SITE");

        itemblk0.setView("USER_ALLOWED_SITE");
        itemblk0.defineCommand("USER_ALLOWED_SITE_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);   
        
        itemset0 = itemblk0.getASPRowSet();
        itembar0 = mgr.newASPCommandBar(itemblk0);

        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
        itembar0.defineCommand(itembar0.DUPLICATEROW,"duplicateRowITEM0");
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.DELETE,"deleteRowITEM0");
        itembar0.defineCommand(ASPCommandBar.SAVERETURN,null,"checkItem0Fields()");

        itemtbl0 = mgr.newASPTable( itemblk0 );
        
        itembar0.addCustomCommand("setDefaultSite",mgr.translate("MPCCOWUSERSDEFAULTSDEFAULTSITE: Set as Default Site"));
        itembar0.addCommandValidConditions("setDefaultSite","USER_SITE_TYPE_DB","Disable","DEFAULT SITE");
        itembar0.disableCommand(itembar0.EDITROW);
    }


    public void  adjust()
    {
        if ( headset.countRows() == 0 )
        {
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.BACK);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand("connectToAllSitesInCompany");
        }
    }


    //===============================================================
    //  HTML
    //===============================================================
    protected String getDescription()
    {
        return "MPCCOWUSERSDEFAULTSDESC: Users";
    }

    protected String getTitle()
    {
        return "MPCCOWUSERSDEFAULTSTITLE: Users";
    }

    protected void printContents() throws FndException
    {
      if(headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }
      if(itemlay0.isVisible() && headset.countRows() > 0)
      {
         appendToHTML(itemlay0.show());
      }
    }
}
