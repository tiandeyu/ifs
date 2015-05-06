package ifs.hzwflw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.horizon.db.Access;
import com.horizon.organization.orgimpl.OrgSelectImpl;
import com.horizon.todo.grdb.GrdbUtil;
import com.horizon.util.DateUtil;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPHTMLFormatter;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.FndRuntimeException;
import ifs.fnd.service.Util;
import ifs.genbaw.GenbawConstants;

public class HzAdministratorSet extends ASPPageProvider {

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.HzAdministratorSet");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   boolean editLayout = false;
   String processKey = null;
   String processName = null;
   
   
   
   final String deleteSql = "delete from to_horizon_user t where t.id= ?";
   final String querySql = "select id,name,login_name from to_horizon_user t where t.id !='admin'";
   final String insertSql = "insert into TO_HORIZON_USER t(id,name,login_name,active,user_style,firstname,givenname,password,certificate) values(?,?,?,'1','default',?,'','ED94C2C049B041FD','password')";
 
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------
   public HzAdministratorSet(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }

   public void run() throws FndException
   {
      super.run();

      ASPManager mgr = getASPManager();
      
      if( mgr.commandBarActivated() ){
         eval(mgr.commandBarFunction());
      } else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE"))){
         validate();
      } else{
         okFind();
      }
      
      
      
   
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      initHeadSet();
   }



   public void countFind()
   {
   }



   public void newRow()
   {
     headset.clear();
     headset.addRow(null);
   }
   
   
   public void initHeadSet(){
      ASPManager mgr = getASPManager();
      headset.clear();
      List userList = Access.getMultiList(querySql, null,OrgSelectImpl.DEFAULT_DBIDENTIFIER);
      if(null != userList && userList.size() > 0){
         for(int i=0;i<userList.size();i++){
            
            
            List tempUserList = (List) userList.get(i);
            headset.addRow(null);
            headset.setValue("PERSON_ID", (String) tempUserList.get(0));
            headset.setValue("PERSON_NAME",  (String) tempUserList.get(1));
         }
      }
      
     
     
   }
   
   public void deleteDelegate(){
      ASPManager mgr = getASPManager();
      LinkedHashMap paraLmp = new LinkedHashMap(1);
      headset.store();
      List conditionList = new ArrayList();
      conditionList.add(headset.getValue("PERSON_ID"));
      paraLmp.put(deleteSql, conditionList);
      Access.executeUpdate(paraLmp, OrgSelectImpl.DEFAULT_DBIDENTIFIER);
   }
   
   public void setAdministor() {
      ASPManager mgr = getASPManager();
      LinkedHashMap paraLmp = new LinkedHashMap(1);
      headset.changeRow();
      
      List conditionList = new ArrayList();
      conditionList.add(headset.getValue("PERSON_ID"));
      conditionList.add(headset.getValue("PERSON_NAME"));
      conditionList.add(headset.getValue("PERSON_ID"));
      conditionList.add(headset.getValue("PERSON_NAME"));
      paraLmp.put(insertSql, conditionList);
      Access.executeUpdate(paraLmp, OrgSelectImpl.DEFAULT_DBIDENTIFIER);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("PERSON_ID").
      setFunction("''").
      setLabel("HZADMINISTRATORSETPRERSONID: Person Id").
      setDynamicLOV("PERSON_INFO_LOV").
      setLOVProperty("WHERE", "USER_ID IS NOT NULL").
      setCustomValidation("PERSON_ID", "PERSON_NAME").
      setMandatory().
      setSize(20);
      headblk.addField("PERSON_NAME").
      setFunction("''").
      setReadOnly().
      setLabel("HZADMINISTRATORSETPERSONNAME: Person Name").
      setSize(20);

      headblk.setView("DUAL");
      headblk.defineCommand("HZ_BIZ_WF_CONFIG_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
//      headbar.defineCommand(headbar.EDITROW,"editRow");
      headbar.defineCommand(headbar.DELETE,"deleteRow");
      headbar.defineCommand(headbar.SAVERETURN,"saveRow");
      headbar.disableCommand(ASPCommandBar.EDIT);
      headbar.disableCommand(ASPCommandBar.EDITROW);
//      headbar.disableCommand(ASPCommandBar.DELETE);
//      headbar.disableCommand(ASPCommandBar.CANCELEDIT);
      headbar.disableCommand(ASPCommandBar.SAVENEW);
//      headbar.disableCommand(ASPCommandBar.NEWROW);
      headbar.disableCommand(ASPCommandBar.FIND);
      headbar.disableCommand(ASPCommandBar.PROPERTIES);
      headbar.disableCommand(ASPCommandBar.DUPLICATEROW);
      headbar.disableCommand(ASPCommandBar.DUPLICATE);
      headbar.disableCommand(ASPCommandBar.OVERVIEWEDIT);
//      headbar.disableCommand(ASPCommandBar.BACK);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HZBIZWFCONFIGTBLHEAD: Hz Biz Wf Configs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setSimple("PERSON_NAME");
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
//      headlay.setDialogColumns(1);
   }
   
   public void editRow(){
      headlay.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
      initHeadSet();
   }
   
   public void deleteRow(){
      headlay.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      deleteDelegate();
      initHeadSet();
   }
   public void addRow(){
      headlay.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
//      setAdministor();
//      initHeadSet();
   }
   
   
   
   public void addDelegate(){
      
   }
   
   public void saveRow(){
      headlay.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      setAdministor();
      initHeadSet();
   }


   public void adjust()
   {
      
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HZADMISTRATORSETDESC: Hz Administrator Set";
   }


   protected String getTitle()
   {
      return getDescription();
   }


   protected void printContents() throws FndException
   {
      super.printContents();

      ASPManager mgr = getASPManager();
      ASPHTMLFormatter fmt=mgr.newASPHTMLFormatter();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      
      appendToHTML("");
      printHiddenField("PROCESS_KEY", processKey);
      printHiddenField("PROCESS_NAME", processName);
      printSpaces(2);
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
   
   
   public void validate(){
      ASPManager mgr = getASPManager();
      
      
     ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
   
      ASPCommand cmd;
      ASPQuery q;
      
      
      String val = mgr.readValue("VALIDATE");
      
      if ("PERSON_ID".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction( "GETNAME", "PERSON_INFO_API.Get_Name", "PERSON_NAME" );
         cmd.addParameter("PERSON_ID");
         trans = mgr.validate(trans);
         String name = trans.getValue("GETNAME/DATA/PERSON_NAME");
         if (mgr.isEmpty(name))
            name = "";
         mgr.responseWrite( name + "^");
      }
      mgr.endResponse();
   }
}
