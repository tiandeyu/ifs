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

package ifs.hsemaw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class HsePersonnelMan extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hsemaw.HsePersonnelMan");

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

   public  HsePersonnelMan (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("NO")) )
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
      q.addWhereCondition("PERSONNELTYPE_DB = ?");
      q.addParameter("PERSONNELTYPE_DB", "HSE");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("HSEPERSONNELMANNODATA: No data found.");
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
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;
      String personneltype;

      cmd = trans.addEmptyCommand("HEAD","HSE_PERSONNEL_MAN_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      
      cmd = trans1.addCustomFunction("PERSONNELTYPE", 
            "PERSONNELTYPE_API.Decode", "PERSONNELTYPE"); 
      cmd.addParameter("PERSONNELTYPE_DB","HSE");

      trans1 = mgr.validate(trans1); 
      personneltype = trans1.getValue("PERSONNELTYPE/DATA/PERSONNELTYPE");
       
//      cmd.setParameter("PERSONNELTYPE", personneltype);
//      cmd.setParameter("PERSONNELTYPE_DB", "HSE");
      
      
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      data.setFieldItem("PERSONNELTYPE", personneltype);
      data.setFieldItem("PERSONNELTYPE_DB", "HSE");
      headset.addRow(data);
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
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("CONDESIGNTECHNOTIFYPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("HSECHECKGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();

      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("NO").
              setHidden().
              setInsertable().
              setLabel("HSEPERSONNELMANNO: No").
              setSize(30);
      headblk.addField("NAME").
              setInsertable().
              setLabel("HSEPERSONNELMANNAME: Name").
              setSize(30);
      headblk.addField("POLTICS_STATUS").
              setInsertable().
              setLabel("HSEPERSONNELMANPOLTICSSTATUS: Poltics Status").
              setSize(30);
      headblk.addField("NATION").
              setInsertable().
              setLabel("HSEPERSONNELMANNATION: Nation").
              setSize(30);
      headblk.addField("NATIVE_PLACE").
              setInsertable().
              setLabel("HSEPERSONNELMANNATIVEPLACE: Native Place").
              setSize(30);
      headblk.addField("ID_NO").
              setInsertable().
              setLabel("HSEPERSONNELMANIDNO: Id No").
              setSize(30);
      headblk.addField("EDUCATION").
              setInsertable().
              setLabel("HSEPERSONNELMANEDUCATION: Education").
              setSize(30);
      headblk.addField("GRADUATE_SCHOOL").
              setInsertable().
              setLabel("HSEPERSONNELMANGRADUATESCHOOL: Graduate School").
              setSize(30);
      headblk.addField("GRADUATE_SPECIALTY").
              setInsertable().
              setLabel("HSEPERSONNELMANGRADUATESPECIALTY: Graduate Specialty").
              setSize(30);
      headblk.addField("PHONE_NO").
              setInsertable().
              setLabel("HSEPERSONNELMANPHONENO: Phone No").
              setSize(30);
      headblk.addField("STATION").
              setInsertable().
              setLabel("HSEPERSONNELMANSTATION: Station").
              setSize(30);
      headblk.addField("JOB").
              setInsertable().
              setLabel("HSEPERSONNELMANJOB: Job").
              setSize(30);
      headblk.addField("CERTIFICATE_SORT").
              setInsertable().
              setLabel("HSEPERSONNELMANCERTIFICATESORT: Certificate Sort").
              setSize(30);
      headblk.addField("CERTIFICATE_NAME").
              setInsertable().
              setLabel("HSEPERSONNELMANCERTIFICATENAME: Certificate Name").
              setSize(30);
      headblk.addField("CERTIFICATE_NO").
              setInsertable().
              setLabel("HSEPERSONNELMANCERTIFICATENO: Certificate No").
              setSize(30);
      headblk.addField("CERTIFICATION_UNIT").
              setInsertable().
              setLabel("HSEPERSONNELMANCERTIFICATIONUNIT: Certification Unit").
              setSize(30);
      headblk.addField("CERTIFICATION_DATE","Date").
              setInsertable().
              setLabel("HSEPERSONNELMANCERTIFICATIONDATE: Certification Date").
              setSize(30);
      headblk.addField("CERTIFICATION_VALID").
              setInsertable().
              setLabel("HSEPERSONNELMANCERTIFICATIONVALID: Certification Valid").
              setSize(30);
      headblk.addField("VALIDITY").
              setInsertable().
              setLabel("HSEPERSONNELMANVALIDITY: Validity").
              setSize(30);
      headblk.addField("ENABLE_PROJ").
              setInsertable().
              setLabel("HSEPERSONNELMANENABLEPROJ: Enable Proj").
              setSize(30);
      headblk.addField("UNIT").
              setInsertable().
              setInsertable().
              setDynamicLOV("GENERAL_ZONE").
//              setLOVProperty("WHERE", "PERSON_ID = '"+mgr.getFndUser()+"'").
              setLabel("HSEPERSONNELMANUNIT: Unit").
              setSize(30);
      headblk.addField("UNIT_DESC").
              setReadOnly().
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc_ (:UNIT)").
              setLabel("HSEPERSONNELMANUNITDESC: Unit Desc").
              setSize(30);  
      mgr.getASPField("UNIT").setValidation("UNIT_DESC");
      headblk.addField("WORK_YEARS").
              setInsertable().
              setLabel("HSEPERSONNELMANWORKYEARS: Work Years").
              setSize(30);
      headblk.addField("SEX").
              enumerateValues("Sex_API").
              setSelectBox().
              setMandatory().
              setInsertable().
              setLabel("HSEPERSONNELMANSEX: Sex").
              setSize(30);
      headblk.addField("PERSONNELTYPE").
              setHidden().
              setLabel("HSEPERSONNELMANPERSONNELTYPE: Personneltype").
              setSize(30);
      
      headblk.addField("PERSONNELTYPE_DB").
              setHidden();
      
      headblk.addField("STATUS").
              enumerateValues("Status_API").
              setSelectBox().
              setMandatory().
              setInsertable().
              setLabel("HSEPERSONNELMANSTATUS: Status").
              setSize(30);
      headblk.addField("NOTE").
              setLabel("HSEPERSONNELMANNOTE: Note").
              setInsertable().
              setHeight(5).
              setSize(120);
      
      headblk.setView("HSE_PERSONNEL_MAN");
      headblk.defineCommand("HSE_PERSONNEL_MAN_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HSEPERSONNELMANTBLHEAD: Hse Personnel Mans");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("UNIT_DESC");
      headlay.setDataSpan("NOTE", 5);

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
      return "HSEPERSONNELMANDESC: Hse Personnel Man";
   }


   protected String getTitle()
   {
      return "HSEPERSONNELMANTITLE: Hse Personnel Man";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());

   }
}