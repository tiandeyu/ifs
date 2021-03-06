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
* File                          :
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Modified    : Automatically generated by IFS/Design
* ----------------------------------------------------------------------------
*/

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.matmaw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class MatWitnessInfo extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.matmaw.MatWitnessInfo");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  MatWitnessInfo (ASPManager mgr, String page_path)
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
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();     
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("INFO_NO")) )
         okFind();
      else 
         okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

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
         mgr.showAlert("MATWITNESSINFONODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","MAT_WITNESS_INFO_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }
   
   
   
   public void validate()
   {
      ASPManager mgr=getASPManager();
      ASPTransactionBuffer trans=mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPTransactionBuffer trans1=mgr.newASPTransactionBuffer();
      ASPCommand cmd1;
      String str=mgr.readValue("VALIDATE");
    
      if ("SUP_IMP_NO".equals(str)) {
         cmd = trans.addCustomFunction("SUP_PROJ_NO","MAT_SUP_IMP_API.GET_SUP_PROJ_NO","SUP_PROJ_NO");
         cmd.addParameter("SUP_IMP_NO");
         cmd = trans.addCustomFunction("SUP_EQU_NO","MAT_SUP_IMP_API.GET_LIST_NO","SUP_EQU_NO");
         cmd.addParameter("SUP_IMP_NO");

         trans = mgr.validate(trans);
         String SUP_PROJ_NO     = trans.getValue("SUP_PROJ_NO/DATA/SUP_PROJ_NO");
         String LIST_NO     = trans.getValue("SUP_EQU_NO/DATA/SUP_EQU_NO");

         cmd1 = trans1.addCustomFunction("SUP_PROJ_NAME","MAT_SUP_PROJECT_API.GET_SUP_PROJ_NAME","SUP_PROJ_NAME");
         cmd1.addParameter("SUP_IMP_NO",SUP_PROJ_NO);
         cmd1 = trans1.addCustomFunction("SUP_EQU_NO","MAT_SUP_PROJECT_LIST_API.GET_SUP_EQU_NO","SUP_EQU_NO");
         cmd1.addParameter("SUP_PROJ_NO",SUP_PROJ_NO);
         cmd1.addParameter("SUP_IMP_NO",LIST_NO);
         
         trans1 = mgr.validate(trans1);
         String SUP_PROJ_NAME     = trans1.getValue("SUP_PROJ_NAME/DATA/SUP_PROJ_NAME");
         String SUP_EQU_NO     = trans1.getValue("SUP_EQU_NO/DATA/SUP_EQU_NO");
         
         mgr.responseWrite(SUP_PROJ_NO + "^" + SUP_PROJ_NAME + "^" + SUP_EQU_NO + "^" );
      }
      
      mgr.endResponse();
   }


   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("INFO_NO").
              setMandatory().
              setInsertable().
              setLabel("MATWITNESSINFOINFONO: Info No").
              setSize(20);
      headblk.addField("SUP_IMP_NO").
              setInsertable().
              setDynamicLOV("MAT_SUP_IMP").
              setLabel("MATWITNESSINFOSUPIMPNO: Sup Imp No").
              setCustomValidation("SUP_IMP_NO", "SUP_PROJ_NO,SUP_PROJ_NAME,SUP_EQU_NO").
              setSize(20);

      headblk.addField("SUP_PROJ_NO").
              setReadOnly().
              setFunction("MAT_SUP_IMP_API.GET_SUP_PROJ_NO(:SUP_IMP_NO)").
              setLabel("MATSUPIMPSUPPROJNO: Sup Proj No").
              setSize(20);
      headblk.addField("SUP_PROJ_NAME").
              setReadOnly().
              setFunction("MAT_SUP_PROJECT_API.GET_SUP_PROJ_NAME (MAT_SUP_IMP_API.GET_SUP_PROJ_NO(:SUP_IMP_NO))").
              setLabel("MATSUPIMPSUPPROJNAME: Sup Proj Name").
              setSize(30);
      headblk.addField("SUP_EQU_NO").
              setReadOnly().
              setFunction("MAT_SUP_PROJECT_LIST_API.GET_SUP_EQU_NO (MAT_SUP_IMP_API.GET_SUP_PROJ_NO(:SUP_IMP_NO),MAT_SUP_IMP_API.GET_LIST_NO(:SUP_IMP_NO))").
              setLabel("MATSUPPROJECTLISTSUPEQUNO: Sup Equ No").
//              setHyperlink("MatSupEquipment.page", "SUP_EQU_NO").
              setSize(20);
      
      headblk.addField("PRODUCT_NO").
              setInsertable().
              setLabel("MATWITNESSINFOPRODUCTNO: Product No").
              setSize(20);
      headblk.addField("EQUIP_NAME").
              setInsertable().
              setLabel("MATWITNESSINFOEQUIPNAME: Equip Name").
              setSize(20);
      headblk.addField("EQUIP_MAP_NO").
              setInsertable().
              setLabel("MATWITNESSINFOEQUIPMAPNO: Equip Map No").
              setSize(20);
      
      headblk.addField("SUP_TYPE").
              enumerateValues("Sup_Type_API").
              setSelectBox().
              setInsertable().
              setLabel("MATWITNESSINFOSUPTYPE: Sup Type").
              setSize(20);
      headblk.addField("WITNESS_MSG").
              setInsertable().
              setLabel("MATWITNESSINFOWITNESSMSG: Witness Msg").
              setSize(30);
      headblk.addField("WITNESS_PLACE").
              setInsertable().
              setLabel("MATWITNESSINFOWITNESSPLACE: Witness Place").
              setSize(20);
      headblk.addField("WITNESS_TIME","Date").
              setInsertable().
              setLabel("MATWITNESSINFOWITNESSTIME: Witness Time").
              setSize(30);
      
      headblk.addField("SUP_ENGINEER_NAME").
              setInsertable().
              setLabel("MATWITNESSINFOSUPENGINEERNAME: Sup Engineer Name").
              setSize(20);
      
      headblk.addField("UNIT_MSG").
              setInsertable().
              setLabel("MATWITNESSINFOUNITMSG: Unit Msg").
              setHeight(3).
              setSize(100);
      headblk.addField("CON_PERSON_NAME").
              setInsertable().
              setLabel("MATWITNESSINFOCONPERSONNAME: Con Person Name").
              setSize(20);
      headblk.addField("CON_PERSON_TEL").
              setInsertable().
              setLabel("MATWITNESSINFOCONPERSONTEL: Con Person Tel").
              setSize(20);
      headblk.addField("PERSON_ID").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("MATSUPIMPPERSONID: Person Id").
              setSize(20);
      headblk.addField("PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME (:PERSON_ID)").
              setLabel("MATSUPIMPPERSONNAME: Person Name").
              setReadOnly().
              setSize(30);
      mgr.getASPField("PERSON_ID").setValidation("PERSON_NAME");

      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("MATWITNESSINFOCREATETIME: Create Time").
              setSize(30);
      headblk.setView("MAT_WITNESS_INFO");
      headblk.defineCommand("MAT_WITNESS_INFO_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MATWITNESSINFOTBLHEAD: Mat Witness Infos");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headlay.setDataSpan("UNIT_MSG", 5);

      headlay.setSimple("SUP_PROJ_NAME");
      headlay.setSimple("PERSON_NAME");
 



   }



   public void  adjust()
   {
      // fill function body
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "MATWITNESSINFODESC: Mat Witness Info";
   }


   protected String getTitle()
   {
      return "MATWITNESSINFOTITLE: Mat Witness Info";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());

   }
}
