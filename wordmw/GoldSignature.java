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

package ifs.wordmw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ifs.fnd.asp.*;
import ifs.fnd.base.EncryptionException;
import ifs.fnd.base.FndEncryption;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class GoldSignature extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.wordmw.GoldSignature");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   
   private ASPTransactionBuffer trans;
   private ASPBuffer            data;
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  GoldSignature (ASPManager mgr, String page_path)
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
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
    	 validate();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SIGNATUREID")) )
         okFind();
      else
      	okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void  validate()
   {
	   ASPManager mgr = getASPManager();
	   ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	   String val = mgr.readValue("VALIDATE");
	   if("USERNAME".equals(val)){
		   if (!mgr.isEmpty(mgr.readValue("USERNAME"))){
			   ASPCommand cmd = trans.addCustomFunction("MARKNAME","PERSON_INFO_API.Get_Name_For_User","MARKNAME");
	           cmd.addParameter("USERNAME");        
	           trans = mgr.validate(trans);
	           String result = trans.getValue("MARKNAME/DATA/MARKNAME");
	           mgr.responseWrite(result + "^");
		   }
	   }
	   mgr.endResponse();
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
      if(!mgr.isEmpty(mgr.getQueryStringValue("SIGNATUREID"))){
    	  q.addWhereCondition("SIGNATUREID = "+mgr.getQueryStringValue("SIGNATUREID"));
      }
      q.setOrderByClause("SIGNATUREID DESC");
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("GDSIGNATUREVIEWNODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","GD_SIGNATURE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
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
      headblk.addField("SIGNATUREID","Number").
              //setMandatory().
              setReadOnly().
              setLabel("GDSIGNATUREVIEWSIGNATUREID: Signatureid").
              setSize(10);
      headblk.addField("USERNAME").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setCustomValidation("USERNAME","MARKNAME").
              setLabel("GDSIGNATUREVIEWUSERNAME: Username").
              setSize(20);
      headblk.addField("PASSWORD").
              setPasswordField().
              setInsertable().
              setMandatory().
//              setHidden().
              setLabel("GDSIGNATUREVIEWPASSWORD: Password").
              unsetQueryable().
              setSize(20).setDefaultNotVisible();
      headblk.addField("MARKNAME").
              setReadOnly().
              setLabel("GDSIGNATUREVIEWMARKNAME: Markname").
              setSize(40);
      headblk.addField("MARKTYPE").
              setInsertable().
              setHidden().
              setLabel("GDSIGNATUREVIEWMARKTYPE: Marktype").
              setSize(10);
//      headblk.addField("MARKBODY").
//              setInsertable().
//              setLabel("GDSIGNATUREVIEWMARKBODY: Markbody").
//              setSize(30);
      headblk.addField("MARKPATH").
              setInsertable().
              setHidden().
              setLabel("GDSIGNATUREVIEWMARKPATH: Markpath").
              setSize(50);
      headblk.addField("MARKSIZE","Number").
              setInsertable().
              setHidden().
              setLabel("GDSIGNATUREVIEWMARKSIZE: Marksize").
              setSize(10);
      headblk.addField("MARKDATE","Datetime","yyyy-MM-dd").
              setReadOnly().
              setLabel("GDSIGNATUREVIEWMARKDATE: Markdate").
              setSize(20);
      headblk.setView("GD_SIGNATURE_VIEW");
      headblk.defineCommand("GD_SIGNATURE_API","New__,Modify__,Remove__");
//      headbar.defineCommand(headbar.SAVERETURN,"saveReturn");
//      headbar.defineCommand(headbar.SAVENEW,"saveNew");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("uploadImg", mgr.translate("GDSIGNATUREUPLOADIMG:  Upload Img"));
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("GDSIGNATUREVIEWTBLHEAD: Signatures");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
 



   }
   
   
   public void  saveReturn()
   {
      saveNewRecord();
   }
   
   public void saveNew(){
      saveNewRecord();
      newRow();
   }
   
   public void saveNewRecord(){
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      int currrow = headset.getCurrentRowNo();
      headset.changeRow();
      
      

      data = headset.getRow();
      
      String password = headset.getValue("PASSWORD");
      
      String encrptedPassword = null;
      try {
         encrptedPassword = FndEncryption.encrypt(password);
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      } catch (EncryptionException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      data.setFieldItem("PASSWORD",encrptedPassword);
      headset.setRow(data);
      mgr.submit(trans);
      trans.clear(); 
   }
   
   public void uploadImg()  throws FndException
   {
	   ASPManager mgr = getASPManager();
	   if (headlay.isMultirowLayout())
		   headset.goTo(headset.getRowSelected());
	   String SignatureID=headset.getValue("SIGNATUREID");
	   appendDirtyJavaScript("window.location.href=\"/b2e/secured/wordmw/UploadImageData.page?SignatureID="+SignatureID+"\";\n");
   }


   public void  adjust()  
   {
      // fill function body
	   ASPManager mgr = getASPManager();
	   if(!mgr.isEmpty(mgr.getQueryStringValue("SIGNATUREID"))){
		   headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
	   }
	   
	   if(headlay.isSingleLayout()){
		   mgr.getASPField("PASSWORD").setHidden();
		   mgr.getASPField("PASSWORD").unsetMandatory();
	   }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "GDSIGNATUREVIEWDESC: Gold Signature";
   }


   protected String getTitle()
   {
      return "GDSIGNATUREVIEWTITLE: Gold Signature";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      
      appendDirtyJavaScript("function checkUsername(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_('USERNAME',i);\n");
      //appendDirtyJavaScript("    alert(getRowStatus_('MAIN',i));\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");

   }
}
