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
*  File        : DocReleaseWizard.java
*  Modified    :
*    ShDilk  2001-03-20  Created Using the ASP file DocReleaseWizard.asp
*    Bakalk  2001-04-20  Call Id:62842. Added some methods in client in order to sort select boxes in last step.
*    Shdilk  2001-04-30  Call Id:64613. Modified okFind function
*    Shdilk  2001-05-23  Call Id:65172. Rewrite sortSelectBox function
*    Bakalk  2002-12-04  Added doc_sheet to track 2002 2.
*    Bakalk  2003-01-28  Modified functionalities for multiple rows.
*    Nisilk  2003-04-01  Fixed call 93165
*    DhPelk  2003-05-02  Fixed call 93165
*    InoSlk  2003-08-15  Call ID:100767. Modified methods okFind(), okFindDocReferance(), okFindOldRevisions()
*                                        finish(), cancel(), printContents().
*    InoSlk  2003-08-28  Call ID:101731. Modified doReset() and clone().
*    NiSilk  2003-08-29  Call ID 102055. Modified method okFindOldRevisions
*    Bakalk  2003-10-10  Call ID 105833. Happened to do many modifications.
*    NiSilk  2003-10-11  Call ID 107073. Modified method adjust and initiate. And fixed the problem of getting undefined for users and persons.
*    NiSilk  2003-10-15  Call ID 107073. Changed the seperator "|" to (char)28 in method initiate where needed.
*    Bakalk  2003-10-15  Call ID 104833. Modified okFindOldRevisions. Now all previous revisions can be set to Obsolete.
*    Bakalk  2003-10-20  Call ID 104833. Did many modifications. Now Released revisions are checked by default and revisions in Approval progess not shown in second step.
*                                        Removed following methods: unselectRevisionObsolete(),selectRevisionObsolete(),setDummyCheckObs(),refreshWithPreRelease().
*    Shtolk  2003-10-21  Call ID 108697. Modified the initiate() to display data in the "Has access to the revision" select dialog.
*    NiSilk  2003-10-29  Call ID 109321. Changes made in finish(), getAdjustedAccess, getViewAccesItems.
*                                        Added javascript method getEntryList. Modified javascript methods moveSide and getViewAccess.
*    InoSlk  2003-11-04  Call ID 110048. Removed code which sets Single Layout when there's only one object connection, in adjust().
*    Bakalk  2003-11-04  Call ID 110303. Modication done in onBackButton() and onNextButton(): fixed problem of selecting records.
*    Bakalk  2004-01-06  Web Alignment. Field order arranged.
*    Dikalk  2004-03-23  SP1 Merge.  Bug ID 40918.
*    Bakalk  2004-06-21  LCS patch <Bug ID 44016> merged.
*    Dikalk  2004-06-23  Merged Bug Id 44562
*    Dikalk  2004-06-23  Merged Bug Id 44171
*    Bakalk  2004-06-25  LCS patch <440189> merged.
*    Bakalk  2004-11-17  LCS patch <46739> merged.
*    SukMlk  2005-03-22  Merged Bug 49914
*    ----------------------------------------------------------------------------
*
*    DiKalk  2005-06-09 Refactored the entire page (got rid of about 2000 lines of crap code)
*    DiKalk  2005-06-09 Implemented functionality for 'Quick Release' and special checks on structure documents
*    SukMlk  2005-06-15 Bug Id 124884 Fixed dupe constant name for accept defaults.
*    DiKalk  2005-08-08 Fixed problem with granting View Access to users and groups
*    Amnalk  2005-10-14 Fixed Call 126674.
*    Amnalk  2005-10-31 Merged Bug Id 53112.
*    Amnalk  2005-11-14 Fixed Call Id 125567.
*    Dikalk  2006-02-16 Fixed call 125567.
*    Bakalk  2006-07-14 Fixed Bug 58214, fixing security issues regarding HTML/Script errors.
*    Bakalk  2006-10-09 Fixed Bug 58541.
*    NIJALK  2007-03-01 Bug 58526, Modified printContents().
*    SHTHLK  2007-05-24 Bug 65167, Modified printContents()/Finish() to disable move of lock objects.
*    SHTHLK  2007-05-30 Bug Id 65687,  Modified Finish() to move the object before releasing the document.
*    AMNALK  2008-06-09 Bug Id 69329, Added new functions GetNumberOfObjConnections() & storeRadioButtons(). Modified many places
*				      to add a new step to document release wizard when number of object connections exeeds 100.				      
*    SHTHLK  2008-07-21 Bug Id 75677, Changed the Step5 of the wizard to load the characters first and then to load the users/groups
*            2008-07-21 based on the selected character. For each character database will be access once to get the data.
*    SHTHLK  2008-07-30 Bug Id 75677, Modified insertOption() to stop selection of options unless specified.  
*    SHTHLK  2008-07-29 Bug Id 75677, Modified okFindPersonsAndGroups() and populateUserGroups to set the buffersize to 10,000.
*    DULOLK  2008-09-17 Bug Id 70808, Added option for Background Job in first step. Modified finish().
*    AMCHLK  2008-11-24 Bug Id 78446, Changed the lable of the field "INSTANCE_DESC"
*    AMCHLK  2008-12-31 Bug Id 78853, Replaced the function call "Move_Document_Reference" from "History_Log_For_Move_Obj" 
*    AMNALK  2009-01-12 Bug Id 78998, Modified printContents() and drawButtonPanel().
*    AMCHLK  2009-01-15 Bug Id 78853, increased the buffer size to dispaly up to 100 connected objects at Release Wizard Step 03 
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;
import java.util.Arrays; //Bug Id 75677

public class DocReleaseWizard extends ASPPageProvider
{

   //
   // Static constants
   //
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocReleaseWizard");


   //
   // Instances created on page creation (immutable attributes)
   //
   private ASPHTMLFormatter fmt;
   private ASPForm frm;
   private ASPContext ctx;

   private ASPRowSet docset;
   private ASPRowSet objectsset;
   private ASPRowSet objectsset2;//Bug Id 69329
   private ASPRowSet obsoleteset;
   private ASPRowSet accessset;
   private ASPRowSet personset;
   private ASPRowSet groupset;

   private ASPCommandBar docbar;
   private ASPCommandBar objectsbar;
   private ASPCommandBar objectsbar2;//Bug Id 69329
   private ASPCommandBar obsoletebar;
   private ASPCommandBar accessbar;

   private ASPTable objectstbl;
   private ASPTable objectstbl2;//Bug Id 69329
   private ASPTable obsoletetbl;
   private ASPTable doctbl;

   private ASPBlockLayout doclay;
   private ASPBlockLayout objectslay;
   private ASPBlockLayout objectslay2;//Bug Id 69329
   private ASPBlockLayout obsoletelay;
   private ASPBlockLayout accesslay;

   private ASPBlock docblk;
   private ASPBlock objectsblk;
   private ASPBlock objectsblk2;//Bug Id 69329
   private ASPBlock accessblk;
   private ASPBlock obsoleteblk;
   private ASPBlock personblk;
   private ASPBlock groupblk;

   private ASPTransactionBuffer trans;
   private boolean close_wizard;
   private boolean unreleased_sub_documents;
   private String message;
   private String client_action;
   private String server_command;
   private String client_command;
   private String sRadioButton; //Bug Id 69329
   private String strObj; //Bug id 75677

   //Bug Id 70808, Start
   private boolean bPerformBackgroundJob;
   private boolean bBackgroundJobAllowed;
   //Bug Id 70808, End

   public DocReleaseWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      fmt = mgr.newASPHTMLFormatter();
      frm = mgr.getASPForm();
      ctx = mgr.getASPContext();

      // Fetch data transfered to wizard..
      if (mgr.dataTransfered())
         okFind();


      // Initialise wizard..
      initWizard();

      //Bug Id 70808, Start
      String test = mgr.readValue("CHECK_BGJ");      
      if ("TRUE".equals(mgr.readValue("CHECK_BGJ")))
         bBackgroundJobAllowed = true;
      else
         bBackgroundJobAllowed = false;
      //Bug Id 70808, End

      // Handle client actions..
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.buttonPressed("BACK"))
         previousStep();
      else if (mgr.buttonPressed("NEXT"))
         nextStep();
      else if (mgr.buttonPressed("FINISH"))
         finish();
      //Bug Id 75677, Start
      else if( !mgr.isEmpty(mgr.readValue("execute")) )
            execute();
      //Bug Id 75677, End
      adjust();
   }


   //Bug Id 75677, Start
   public void  execute()
   {
       ASPManager mgr = getASPManager();

       String val = mgr.readValue("execute");
       mgr.responseClear();
       //ServerFunction 
       if( "populateUserGroups".equals(val) )
       {
	   mgr.responseWrite(populateUserGroups( mgr.readValue("SELECTED_CHAR")) );
       }    
        mgr.endResponse();
   }
   
   private String populateUserGroups(String selected_character)
   {
      ASPManager mgr = getASPManager();
      ASPBuffer temp_buff;
      int count;
      String person_id_list = "",person_name_list ="",group_id_list="",group_description_list="";

      // Get Person details..
      trans.clear();
      ASPQuery query = trans.addQuery("PERSONS", "SELECT PERSON_ID, NAME FROM PERSON_INFO_PUBLIC WHERE UPPER(SUBSTR(NAME,0,1))=?");
      query.addParameter("DUMMY", selected_character);
      query.setBufferSize(10000);//Bug Id 75677, So that we don't run out of buffer

      // Get Group details..
      query = trans.addQuery("GROUPS", "SELECT GROUP_ID, GROUP_DESCRIPTION FROM DOCUMENT_GROUP WHERE UPPER(SUBSTR(GROUP_DESCRIPTION,0,1))=?");
      query.addParameter("DUMMY", selected_character);
      query.setBufferSize(10000);//Bug Id 75677, So that we don't run out of buffer
      trans = mgr.perform(trans);

      // Buffer containing list of persons in the enterprise..
      ASPBuffer person_buf = trans.getBuffer("PERSONS");
      person_buf.removeItem("INFO");

      if (person_buf.countItems() > 0) {
	  
	  temp_buff = person_buf.getBufferAt(0);
      	  person_id_list= temp_buff.getValueAt(0)+(char)31;
	  person_name_list= temp_buff.getValueAt(1)+(char)31;
	  count = person_buf.countItems();
	  for (int x = 1; x < count; x++)
	  {
	      temp_buff = person_buf.getBufferAt(x);
	      person_id_list += temp_buff.getValueAt(0)+(char)31;
	      person_name_list += temp_buff.getValueAt(1)+(char)31;
	  }
      }
      // Buffer containing list of groups created in Basic Data..
      ASPBuffer group_buf = trans.getBuffer("GROUPS");
      group_buf.removeItem("INFO");

      if (group_buf.countItems() > 0) {
      	  temp_buff = group_buf.getBufferAt(0);
      	  group_id_list= temp_buff.getValueAt(0)+(char)31;
	  group_description_list= temp_buff.getValueAt(1)+(char)31;
	  count = group_buf.countItems();
	  for (int x = 1; x < count; x++)
	  {
	      temp_buff = group_buf.getBufferAt(x);
	      group_id_list += temp_buff.getValueAt(0)+(char)31;
	      group_description_list += temp_buff.getValueAt(1)+(char)31;
	  }
      }
      return person_id_list +(char)30 +person_name_list +(char)29 +group_id_list + (char)30 +group_description_list;
   }
   //Bug Id 75677, End

   public void initWizard()
   {
      ASPManager mgr = getASPManager();

      //Bug 58541
      mgr.responseWrite("mgr.readValue(\"OVERRIDE_ACCESS\")"+ mgr.readValue("OVERRIDE_ACCESS"));

      // Internal operational values used by the Wizard..
      ctx.writeValue("CURRENT_STEP", ctx.readValue("CURRENT_STEP", "STEP_1"));
      ctx.writeFlag("POPULATE_OLD_REVISIONS", ctx.readFlag("POPULATE_OLD_REVISIONS", true));
      ctx.writeFlag("POPULATE_CONNECTED_OBJECTS", ctx.readFlag("POPULATE_CONNECTED_OBJECTS", true));
      ctx.writeFlag("POPULATE_PERSONS_GROUPS", ctx.readFlag("POPULATE_PERSONS_GROUPS", true));


      // Persons and Groups..
      ctx.writeBuffer("DOCUMENT_ACCESS_PERSONS", readBuffer("DOCUMENT_ACCESS_PERSONS"));
      ctx.writeBuffer("DOCUMENT_ACCESS_GROUPS", readBuffer("DOCUMENT_ACCESS_GROUPS"));
      ctx.writeBuffer("PERSONS", readBuffer("PERSONS"));
      ctx.writeBuffer("GROUPS", readBuffer("GROUPS"));
      //Bug Id 75677, Start
      ctx.writeValue("CHARACTER_LIST", ctx.readValue("CHARACTER_LIST", ""));
      ctx.writeValue("SELECTED_CHARACTERS", mgr.readValue("SELECTED_CHARACTERS", ctx.readValue("SELECTED_CHARACTERS", "")));
      ctx.writeValue("SELECTED_CHARACTER", mgr.readValue("SELECTED_CHARACTER", ctx.readValue("SELECTED_CHARACTER", "")));
      ctx.writeValue("PERSON_ID_LIST", mgr.readValue("PERSON_ID_LIST", ctx.readValue("PERSON_ID_LIST", "")));
      ctx.writeValue("PERSON_NAME_LIST", mgr.readValue("PERSON_NAME_LIST", ctx.readValue("PERSON_NAME_LIST", "")));
      ctx.writeValue("GROUP_ID_LIST", mgr.readValue("GROUP_ID_LIST", ctx.readValue("GROUP_ID_LIST", "")));
      ctx.writeValue("GROUP_DESCRIPTION_LIST", mgr.readValue("GROUP_DESCRIPTION_LIST", ctx.readValue("GROUP_DESCRIPTION_LIST", "")));
      //Bug Id 75677, End

      // Client values..
      ctx.writeValue("SET_OBSOLETE", mgr.readValue("SET_OBSOLETE", ctx.readValue("SET_OBSOLETE", "YES")));
      ctx.writeValue("ACCESS_LEVEL", mgr.readValue("ACCESS_LEVEL", ctx.readValue("ACCESS_LEVEL", getDefaultAccessLevel())));
      ctx.writeValue("SHOW_GROUPS", mgr.readValue("SHOW_GROUPS", ctx.readValue("SHOW_GROUPS", "YES")));
      ctx.writeValue("SHOW_PERSONS", mgr.readValue("SHOW_PERSONS", ctx.readValue("SHOW_PERSONS", "YES")));
      ctx.writeValue("UNRELEASED_SUB_DOCUMENTS", mgr.readValue("UNRELEASED_SUB_DOCUMENTS", ctx.readValue("UNRELEASED_SUB_DOCUMENTS", "NO")));
      //Bug 58541
      ctx.writeValue("OVERRIDE_ACCESS", mgr.readValue("OVERRIDE_ACCESS_TEXT", ctx.readValue("OVERRIDE_ACCESS", "NO")));
      //bOverride

      ctx.writeValue("RADIOBUTTON", ctx.readValue("RADIOBUTTON","DEFAULT"));//Bug Id 69329

      // Initialise DOCUMENT_ACCESS_PERSONS and DOCUMENT_ACCESS_GROUPS
      // if changes were made on the client side in step-5..
      String grantees = mgr.readValue("GRANTEES");
      storeGrantees(grantees, mgr.readValue("GRANTEES_VIEW_ACCESS"));
      

   }


   private ASPBuffer readBuffer(String name)
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buf = ctx.readBuffer(name);

      if (buf == null)
         buf = createBuffer(name);

      return buf;
   }


   private ASPBuffer createBuffer(String name)
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf = mgr.newASPBuffer();
      buf.addBuffer(name);
      return buf;
   }


   public String getDefaultAccessLevel()
   {
      String access_level = "GROUP_ACCESS";

      if (docset.countRows() == 1)
      {
         if ("A".equals(docset.getValue("ACCESS_CONTROL_DB")))
            access_level = "ALL_ACCESS";
         else if ("U".equals(docset.getValue("ACCESS_CONTROL_DB")))
            access_level = "USER_ACCESS";
         else if ("G".equals(docset.getValue("ACCESS_CONTROL_DB")))
            access_level = "GROUP_ACCESS";
      }

      return access_level;
   }


   private void storeGrantees(String grantees, String view_access)
   {
      ASPManager mgr = getASPManager();
      ASPBuffer person_access_buf = mgr.newASPBuffer();
      ASPBuffer group_access_buf = mgr.newASPBuffer();
      String current_step = ctx.readValue("CURRENT_STEP");//Bug Id 75677
      if (!mgr.isEmpty(grantees)) //Bug Id 75677
      {
	  StringTokenizer st = new StringTokenizer(grantees, "" + DocmawUtil.RECORD_SEPARATOR);
	  ASPBuffer old_person_access_buf = readBuffer("DOCUMENT_ACCESS_PERSONS");
	  ASPBuffer old_group_access_buf = readBuffer("DOCUMENT_ACCESS_GROUPS");
    
	  int person_prefix_len = "PERSON_".length();
	  int group_prefix_len = "GROUP_".length();
    
	  while (st.hasMoreTokens())
	  {
	     String entity = st.nextToken();
	     int sep = entity.indexOf(DocmawUtil.FIELD_SEPARATOR);
	     String entity_id = entity.substring(0, sep);
	     String entity_name = entity.substring(sep + 1);
    
	     if (entity_id.startsWith("PERSON_"))
	     {
		ASPBuffer data_buf = person_access_buf.addBuffer("DATA");
		entity_id = entity_id.substring(person_prefix_len);
		data_buf.addItem("PERSON_ID", entity_id);
		data_buf.addItem("NAME", entity_name);
		data_buf.addItem("EDIT_ACCESS", getEditAccess(old_person_access_buf, "PERSON_ID", entity_id));
		data_buf.addItem("VIEW_ACCESS", getViewAccess("PERSON_ID", entity_id, view_access));
	     }
	     else if (entity_id.startsWith("GROUP_"))
	     {
		ASPBuffer data_buf = group_access_buf.addBuffer("DATA");
		entity_id = entity_id.substring(group_prefix_len);
		data_buf.addItem("GROUP_ID", entity_id);
		data_buf.addItem("GROUP_DESCRIPTION", entity_name);
		data_buf.addItem("EDIT_ACCESS", getEditAccess(old_group_access_buf, "GROUP_ID", entity_id));
		data_buf.addItem("VIEW_ACCESS", getViewAccess("GROUP_ID", entity_id, view_access));
	     }
	  }
      } 
      //Bug Id 75677, Start - Get the access information from the ctx when its not the access step
      else if (!("STEP_5".equals(current_step)))
      {
	  person_access_buf = ctx.readBuffer("DOCUMENT_ACCESS_PERSONS");
	  group_access_buf = ctx.readBuffer("DOCUMENT_ACCESS_GROUPS");
      }
      //Bug Id 75677, End
      ctx.writeBuffer("DOCUMENT_ACCESS_PERSONS", person_access_buf);
      ctx.writeBuffer("DOCUMENT_ACCESS_GROUPS", group_access_buf);
   }


   private String getGrantedPersons()
   {
      ASPBuffer person_access_buf = readBuffer("DOCUMENT_ACCESS_PERSONS");
      int count = person_access_buf.countItems();

      String person_list = "";
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data_buf = person_access_buf.getBufferAt(x);
         person_list += data_buf.getValue("PERSON_ID");
         person_list += DocmawUtil.FIELD_SEPARATOR;
      }
      return person_list;
   }


   private String getGrantedGroups()
   {
      ASPBuffer group_access_buf = readBuffer("DOCUMENT_ACCESS_GROUPS");
      int count = group_access_buf.countItems();

      String group_list = "";
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data_buf = group_access_buf.getBufferAt(x);
         group_list += data_buf.getValue("GROUP_ID");
         group_list += DocmawUtil.FIELD_SEPARATOR;
      }
      return group_list;
   }


   /**
    * getGrantedViewAccess returns a string of values of the form
    * "1;0;1;1;0", where each '1' or '0' corresponds to VIEW_ACCESS
    * given to the GROUP_ID or PERSON_ID
    */
   private String getGrantedViewAccess()
   {
      ASPBuffer person_access_buf = readBuffer("DOCUMENT_ACCESS_PERSONS");
      ASPBuffer group_access_buf = readBuffer("DOCUMENT_ACCESS_GROUPS");

      String view_list = "";
      int count = group_access_buf.countItems();
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data_buf = group_access_buf.getBufferAt(x);
         view_list += data_buf.getValue("VIEW_ACCESS");
         view_list += DocmawUtil.FIELD_SEPARATOR;
      }

      count = person_access_buf.countItems();
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data_buf = person_access_buf.getBufferAt(x);
         view_list += data_buf.getValue("VIEW_ACCESS");
         view_list += DocmawUtil.FIELD_SEPARATOR;
      }
      return view_list;
   }


   /**
    * getGrantedEditAccess returns a string of values of the form
    * "1;0;1;1;0", where each '1' or '0' corresponds to EDIT_ACCESS
    * given to the GROUP_ID or PERSON_ID
    */
   private String getGrantedEditAccess()
   {
      ASPBuffer person_access_buf = readBuffer("DOCUMENT_ACCESS_PERSONS");
      ASPBuffer group_access_buf = readBuffer("DOCUMENT_ACCESS_GROUPS");

      String edit_list = "";
      int count = group_access_buf.countItems();
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data_buf = group_access_buf.getBufferAt(x);
         edit_list += data_buf.getValue("EDIT_ACCESS");
         edit_list += DocmawUtil.FIELD_SEPARATOR;
      }

      count = person_access_buf.countItems();
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data_buf = person_access_buf.getBufferAt(x);
         edit_list += data_buf.getValue("EDIT_ACCESS");
         edit_list += DocmawUtil.FIELD_SEPARATOR;
      }
      return edit_list;
   }


   /**
    * getViewAccess returns the value of VIEW_ACCESS of the named
    * eniity (PERSON_ID or GROUP_ID) in the given access buffer
    */
   private String getViewAccess(String entity_type, String entity_id, String view_access)
   {
      if ("PERSON_ID".equals(entity_type))
      {
         return DocmawUtil.getItemValue(view_access, "PERSON_" + entity_id);
      }
      else // if "GROUP_ID"..
      {
         return DocmawUtil.getItemValue(view_access, "GROUP_" + entity_id);
      }
   }


   /**
    * getEditAccess returns the value of EDIT_ACCESS of the named
    * eniity (PERSON_ID or GROUP_ID) in the given access buffer
    */
   private String getEditAccess(ASPBuffer entity_buf, String entity_type, String entity_id)
   {
      int count = entity_buf.countItems();
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data_buf = entity_buf.getBufferAt(x);
         if (entity_id.equals(data_buf.getValue(entity_type)))
         {
            if (data_buf.itemExists("EDIT_ACCESS"))
               return data_buf.getValue("EDIT_ACCESS");
            else
               return "0";
         }
      }
      return "0";
   }


   public void nextStep() throws FndException
   {
      String current_step = ctx.readValue("CURRENT_STEP");

      if ("STEP_1".equals(current_step))
      {
         String revision_style = getRevisionStyle();

         if ("FREE".equals(revision_style))
         {
            // With revision style Free, the user is allowed to set previous
            // revisions to obsolete. If the user wants to do so..
            if ("YES".equals(ctx.readValue("SET_OBSOLETE")))
            {
               setNextStep("STEP_2");
            }
            else
            {
	       //Bug Id 69329, start
	       if (GetNumberOfObjConnections() > 100) 
	       {
		   setNextStep("STEP_3_2");
	       }
	       else
	       {
                  setNextStep("STEP_3");
	       }
	       //Bug Id 69329, end
            }
         }
         else if ("RESTRICTED".equals(revision_style))
         {
            //Bug Id 69329, start
            if (GetNumberOfObjConnections() > 100) 
            {
		setNextStep("STEP_3_2");
            }
            else
            {
		setNextStep("STEP_3");
	    }
            //Bug Id 69329, end
         }
      }
      else if ("STEP_2".equals(current_step))
      {
         // Save selections made by the user..
         obsoleteset.storeSelections();

	 //Bug Id 69329, start
	 if (GetNumberOfObjConnections() > 100) 
	 {
             setNextStep("STEP_3_2");
	 }
	 else
	 {
	     setNextStep("STEP_3");
	 }
	 //Bug Id 69329, end
      }
      else if ("STEP_3".equals(current_step))
      {
         // Save selections made by the user..
         objectsset.store();

         setNextStep("STEP_4");
      }

      //Bug Id 69329, start
      else if ("STEP_3_2".equals(current_step))
      {
	 storeRadioButtons();
         setNextStep("STEP_4");
      }
      //Bug Id 69329, end

      else if ("STEP_4".equals(current_step))
      {
         setNextStep("STEP_5");
      }
      else if ("STEP_5".equals(current_step))
      {
         // No more steps..
      }
   }


   private void setNextStep(String step) throws FndException
   {
      if ("STEP_1".equals(step))
      {
      }
      else if ("STEP_2".equals(step))
      {
         okFindRevisions();
      }
      else if ("STEP_3".equals(step))
      {
         okFindObjects();
      }
      //Bug Id 69329, start
      else if ("STEP_3_2".equals(step))
      {
      }
      //Bug Id 69329, end
      else if ("STEP_4".equals(step))
      {
      }
      else if ("STEP_5".equals(step))
      {
         okFindPersonsAndGroups();
      }

      ctx.writeValue("CURRENT_STEP", step);
   }



   public void previousStep() throws FndException
   {
      String current_step = ctx.readValue("CURRENT_STEP");

      if ("STEP_1".equals(current_step))
      {
         // No previous step..
      }
      else if ("STEP_2".equals(current_step))
      {
         // Save selections made by the user..
         obsoleteset.storeSelections();
         setPreviousStep("STEP_1");
      }
      else if ("STEP_3".equals(current_step))
      {
         // Save selections made by the user..
         objectsset.store();

         String revision_style = getRevisionStyle();

         if ("FREE".equals(revision_style))
         {
            // With revision style Free, the user is allowed to set previous
            // revisions to obsolete. If the user wants to do so..
            if ("YES".equals(ctx.readValue("SET_OBSOLETE")))
            {
               setPreviousStep("STEP_2");
            }
            else
            {
               setPreviousStep("STEP_1");
            }
         }
         else if ("RESTRICTED".equals(revision_style))
         {
            // With revision style Restricted, the user is NOT allowed to set
            // previous revisions to obsolete, so skip Step 2.
            setPreviousStep("STEP_1");
         }
      }
      //Bug Id 69329, start
      else if ("STEP_3_2".equals(current_step))
      {
         String revision_style = getRevisionStyle();
	 storeRadioButtons();

         if ("FREE".equals(revision_style))
         {
            // With revision style Free, the user is allowed to set previous
            // revisions to obsolete. If the user wants to do so..
            if ("YES".equals(ctx.readValue("SET_OBSOLETE")))
            {
               setPreviousStep("STEP_2");
            }
            else
            {
               setPreviousStep("STEP_1");
            }
         }
         else if ("RESTRICTED".equals(revision_style))
         {
            // With revision style Restricted, the user is NOT allowed to set
            // previous revisions to obsolete, so skip Step 2.
            setPreviousStep("STEP_1");
         }
      }
      //Bug Id 69329, end
      else if ("STEP_4".equals(current_step))
      {
	  if (GetNumberOfObjConnections() > 100)
	  {
	      setPreviousStep("STEP_3_2");
	  }
	  else
	  {
	      setPreviousStep("STEP_3");         
	  }
      }
      else if ("STEP_5".equals(current_step))
      {
         setPreviousStep("STEP_4");
      }
   }


   private void setPreviousStep(String step) throws FndException
   {
      if ("STEP_1".equals(step))
      {
      }
      else if ("STEP_2".equals(step))
      {
         okFindRevisions();
      }
      else if ("STEP_3".equals(step))
      {
         okFindObjects();
      }
      else if ("STEP_4".equals(step))
      {
      }
      else if ("STEP_5".equals(step))
      {
      }

      ctx.writeValue("CURRENT_STEP", step);
   }


   private String getRevisionStyle()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPCommand cmd = trans.addCustomFunction("REVISION_STYLE", "Doc_Class_Default_API.Get_Default_Value_", "DUMMY");
      cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
      cmd.addParameter("DUMMY1", "DocIssue");
      cmd.addParameter("DUMMY1", "REVISION_STYLE");
      trans = mgr.perform(trans);

      return trans.getValue("REVISION_STYLE/DATA/DUMMY").toUpperCase();
   }


   public void okFind() throws FndException
   {
      ASPManager mgr = getASPManager();

      // Get transferred data..
      ASPBuffer buf = mgr.getTransferedData();

      //## TOCHECK - Check this use of document transfer handler here
      ASPQuery query = trans.addEmptyQuery(docblk);
      query.addOrCondition(buf.getBufferAt(1));
      query.setOrderByClause("DOC_NO");
      query.includeMeta("ALL");
      mgr.submit(trans);



      //
      // Run checks to see if all documents satisfy mandatory conditions
      // before being released..
      //


      if (docset.countRows() > 0)
      {
         // Check all documents to see if there are in state 'Approved'
         String approved = DocmawConstants.getConstantHolder(mgr).doc_issue_approved;
         docset.first();
         do
         {
            if (!approved.equals(docset.getValue("STATE")))
            {
               message = mgr.translate("DOCMAWDOCRELEASEWIZARDINVALIDSTATE: Only Approved documents may be Released. This wizard will now close.");
               client_action = "SHOW_ALERT";
               client_command = "closeWindow()";
            }
         }
         while (docset.next());


         // Check to see of the user is attempting to release more than
         // one structure document with children
         docset.first();
         int count = 0;
         do
         {
            if ("1".equals(docset.getValue("STRUCTURE")) && Integer.parseInt((docset.getValue("NO_CHILDREN"))) > 0)
               count ++;

            if (count > 1)
            {
               message = mgr.translate("DOCMAWDOCRELEASEWIZARDMORETHANONESTRUCTUREWITHCHILDREN: This wizard does not allow you to release more than one structure document having unreleased sub-documents, at a time.");
               //Bug 70808, Start
               client_action = "SHOW_ALERT";
               client_command = "closeWindow()";
               //Bug 70808, End
            }
         }
         while (docset.next());



         // If only one structure document was passed and it has unreleased
         // children, ask the user whether he wants to set the children to
         // released
         docset.first();
         do
         {
            if ("1".equals(docset.getValue("STRUCTURE")) && "FALSE".equals(docset.getValue("CHILDREN_RELEASED")))
            {
               ctx.writeValue("UNRELEASED_SUB_DOCUMENTS", "YES");
               unreleased_sub_documents = true;
            }
         }
         while (docset.next());
      }
   }


   public void releaseChildren()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      docset.first();
      int count = 0;
      do
      {
         if ("1".equals(docset.getValue("STRUCTURE")) && "FALSE".equals(docset.getValue("CHILDREN_RELEASED")))
         {
            ASPCommand cmd = trans.addCustomCommand("SET_RELEASED" + count++, "Doc_Structure_Util_API.Release_Children_");
            cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));
         }
      }
      while (docset.next());
      trans = mgr.perform(trans);
   }


   public void okFindRevisions() throws FndException
   {
      ASPManager mgr = getASPManager();

      if (!ctx.readFlag("POPULATE_OLD_REVISIONS", true))
         return;

      trans.clear();
      ASPQuery query = trans.addQuery(obsoleteblk);

      String sql_where = "";
      docset.first();

      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);
      String obsolete = dm_const.doc_issue_obsolete;
      String approval_in_progress = dm_const.doc_issue_approval_in_progress;

      do
      {
         if (!mgr.isEmpty(sql_where))
            sql_where += " OR ";

         sql_where += " (DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV <> ? ";
         sql_where += " AND STATE != ? AND STATE != ?) ";

         query.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
         query.addParameter("DOC_NO", docset.getValue("DOC_NO"));
         query.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
         query.addParameter("DOC_REV", docset.getValue("DOC_REV"));
         query.addParameter("STATE", obsolete);
         query.addParameter("STATE", approval_in_progress);
      }
      while (docset.next());

      query.addWhereCondition(sql_where);
      query.setOrderByClause("DOC_REV");
      query.includeMeta("ALL");
      mgr.submit(trans);


      // Set all released revisions as selected
      // by default..

      String released = dm_const.doc_issue_released;

      if (obsoleteset.countRows() > 0)
      {
         obsoleteset.first();
         do
         {
            if (obsoleteset.getRow().getValue("STATE").equals(released))
               obsoleteset.selectRow();

         }
         while (obsoleteset.next());
      }

      // Theres no need to populate this block again..
      ctx.writeFlag("POPULATE_OLD_REVISIONS", false);
   }


   public void okFindObjects()
   {
      if (!ctx.readFlag("POPULATE_CONNECTED_OBJECTS", true))
         return;

      ASPManager mgr = getASPManager();
      trans.clear();
      ASPQuery query = trans.addQuery(objectsblk);

      String sSelect = "";
      docset.first();

      do
      {
         if (!mgr.isEmpty(sSelect))
            sSelect += " OR ";

         sSelect += " (DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? )";
         
         query.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
         query.addParameter("DOC_NO", docset.getValue("DOC_NO"));
         query.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
      }
      while (docset.next());

      query.addWhereCondition(sSelect);
      query.setOrderByClause("DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV, REV_NO");
      query.includeMeta("ALL");
      query.setBufferSize(100);  // Bug Id 78853
      mgr.submit(trans);

      
      //
      // Next, select objects that must be moved by
      // to the latest released revision by default..
      //


      // If no objects are available..
      if (objectsset.countRows() == 0)
         return;


      objectsset.first();
      do
      {
         if (("R".equals(objectsset.getValue("KEEP_LAST_DOC_REV_DB"))) ||
            (("L".equals(objectsset.getValue("KEEP_LAST_DOC_REV_DB"))) && (objectsset.getValue("LATEST_REVISION").equals(getReleaseRevision(objectsset.getValue("DOC_NO"), objectsset.getValue("DOC_SHEET"))))) ||
            (("F".equals(objectsset.getValue("KEEP_LAST_DOC_REV_DB"))) && (isReleaseRevision(objectsset.getValue("DOC_NO"), objectsset.getValue("DOC_SHEET"), objectsset.getValue("DOC_REV")))))
         {
            objectsset.setValue("REPLACE", "T");
         }
         else
         {
            objectsset.setValue("REPLACE", "F");
         }
      }
      while(objectsset.next());


      // There's no need to populate the same block again
      // when returning to this step, so..
      ctx.writeFlag("POPULATE_CONNECTED_OBJECTS", false);
   }


   private boolean isReleaseRevision(String doc_no, String doc_sheet, String doc_rev)
   {
      docset.first();
      do
      {
         if (docset.getValue("DOC_NO").equals(doc_no) && 
             docset.getValue("DOC_SHEET").equals(doc_sheet) &&
             docset.getValue("DOC_REV").equals(doc_rev))
         {
            return true;
         }
      }
      while(docset.next());

      return false;
   }


   private String getReleaseRevision(String doc_no, String doc_sheet)
   {
      docset.first();
      do
      {
         if (docset.getValue("DOC_NO").equals(doc_no) && 
             docset.getValue("DOC_SHEET").equals(doc_sheet))
         {
            return docset.getValue("DOC_REV");
         }
      }
      while(docset.next());

      return "";
   }


   public void finish()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;
      int count = 0;

      //Bug 70808, Start
      if ("YES".equals(mgr.readValue("BACKGROUND_JOB"))) {
         bPerformBackgroundJob = true;
         // Close wizard and refresh parent..
         close_wizard = true;
         return;
      }
      //Bug 70808, End

      //
      // If the user choose to accept all defaults..
      //
      if ("YES".equals(mgr.readValue("ACCEPT_DEFAULTS")))
      {
         // Release all child documents first..
         if ("YES".equals(ctx.readValue("UNRELEASED_SUB_DOCUMENTS")))
         {
            releaseChildren();
         }

         trans.clear();
         count = 0;
         do
         {
            cmd = trans.addCustomCommand("SET_RELEASED" + count++, "Doc_Issue_API.Release_Document_");
            cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));
         }
         while (docset.next());
         trans = mgr.perform(trans);

         // Close wizard and refresh parent..
         close_wizard = true;
         return;
      }


      //
      // Set old revisions to obsolete (Step-2)
      //


      // If revision style is "RESTRICTED" set all previous
      // revisions to obsolete
      String revision_style = getRevisionStyle();
      if ("RESTRICTED".equals(revision_style))
      {
         trans.clear();
         docset.first();
         count = 0;
         do
         {
            cmd = trans.addCustomCommand("SET_OBSOLETE" + count++, "Doc_Issue_API.Set_Old_Released_Obsolete__");
            cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));
         }
         while (docset.next());
         trans = mgr.perform(trans);
      }
      else
      {
         // If revision style is "FREE"..
         if (obsoleteset.countSelectedRows() > 0)
         {
            trans.clear();
            obsoleteset.setFilterOn();
            obsoleteset.first();
            count = 0;
            do
            {
               cmd = trans.addCustomCommand("SET_OBSOLETE" + count++, "Doc_Issue_API.Promote_To_Obsolete__");
               cmd.addParameter("DUMMY");
               cmd.addParameter("OBSOLETE_OBJID", obsoleteset.getValue("OBJID"));
               cmd.addParameter("OBSOLETE_OBJVERSION", obsoleteset.getValue("OBJVERSION"));
               cmd.addParameter("DUMMY");
               cmd.addParameter("DUMMY", "DO");
            }
            while (obsoleteset.next());
            obsoleteset.setFilterOff();
            trans = mgr.perform(trans);
         }
      }


      //
      // Move object connections (Step-3)
      //


      //Bug Id 69329, start
      if (GetNumberOfObjConnections() > 100) 
      {
	  if ("DEFAULT".equals(ctx.readValue("RADIOBUTTON")))
          {
              trans.clear();
              cmd = trans.addCustomCommand("MOVEDEFAULT","DOC_REFERENCE_OBJECT_API.Move_All_Objs_To_Rev");
              cmd.addParameter("DOC_CLASS",    docset.getValue("DOC_CLASS"));
              cmd.addParameter("DOC_NO",       docset.getValue("DOC_NO"));
              cmd.addParameter("DOC_SHEET",    docset.getValue("DOC_SHEET"));
              cmd.addParameter("DOC_REV",      getReleaseRevision(docset.getValue("DOC_NO"), docset.getValue("DOC_SHEET")));
	      cmd.addParameter("DUMMY1",       "TRUE");

              trans = mgr.perform(trans);
              trans.clear();
          }
          else if ("MOVEALL".equals(ctx.readValue("RADIOBUTTON")) )
          {
              trans.clear();
              cmd = trans.addCustomCommand("MOVEALL","DOC_REFERENCE_OBJECT_API.Move_All_Objs_To_Rev");
              cmd.addParameter("DOC_CLASS",    docset.getValue("DOC_CLASS"));
              cmd.addParameter("DOC_NO",       docset.getValue("DOC_NO"));
              cmd.addParameter("DOC_SHEET",    docset.getValue("DOC_SHEET"));
              cmd.addParameter("DOC_REV",      getReleaseRevision(docset.getValue("DOC_NO"), docset.getValue("DOC_SHEET")));
              cmd.addParameter("DUMMY1",       "TRUE");
              cmd.addParameter("DUMMY1",       "Y");

              trans = mgr.perform(trans);
              trans.clear();
          }
          else if ("NOMOVE".equals(ctx.readValue("RADIOBUTTON")) )
          {
              debug("DEBUG: button FINISH4 is pressed .... NOMOVE " + ctx.readValue("RADIOBUTTON"));
          }
	  else
	  {
              trans.clear();
              cmd = trans.addCustomCommand("MOVEDEFAULT","DOC_REFERENCE_OBJECT_API.Move_All_Objs_To_Rev");
              cmd.addParameter("DOC_CLASS",    docset.getValue("DOC_CLASS"));
              cmd.addParameter("DOC_NO",       docset.getValue("DOC_NO"));
              cmd.addParameter("DOC_SHEET",    docset.getValue("DOC_SHEET"));
              cmd.addParameter("DOC_REV",      getReleaseRevision(docset.getValue("DOC_NO"), docset.getValue("DOC_SHEET")));
	      cmd.addParameter("DUMMY1",       "TRUE");

              trans = mgr.perform(trans);
              trans.clear();
	  }
      }
      else
      {
	  // The user is given the liberty to choose objects in step-3..
	  if (objectsset.countRows() > 0)
	  {
	      trans.clear();
	      objectsset.first();
	      count = 0;
	      do
	      {
		  debug("debug: objectsset.getValue(REPLACE) == " + objectsset.getValue("REPLACE"));

		  // If the particular object was selected by the user..
		  if ("T".equals(objectsset.getValue("REPLACE")))
		  {
		      cmd = trans.addCustomCommand("MOVE_OBJECT_REFERENCE" + count++, "Doc_Reference_Object_API.History_Log_For_Move_Obj"); // Bug Id 78853
		      cmd.addParameter("DOC_CLASS", objectsset.getValue("DOC_CLASS"));
		      cmd.addParameter("DOC_NO", objectsset.getValue("DOC_NO"));
		      cmd.addParameter("DOC_SHEET", objectsset.getValue("DOC_SHEET"));
		      cmd.addParameter("DUMMY");
		      cmd.addParameter("LU_NAME", objectsset.getValue("LU_NAME"));
		      cmd.addParameter("KEY_REF", objectsset.getValue("KEY_REF"));
		      cmd.addParameter("DOC_REV", getReleaseRevision(objectsset.getValue("DOC_NO"), objectsset.getValue("DOC_SHEET")));
		      cmd.addParameter("DUMMY","'Y'"); //Bug Id 65687
		  }
	      }
	      while(objectsset.next());
	      trans = mgr.perform(trans);
	  }
      }
      //Bug Id 69329, end
      


      //
      // Set document access (Step-4 and Step-5)
      //


      // Access level selected by user in Step-4..
      String access_level = ctx.readValue("ACCESS_LEVEL");


      // Fetch current access level for all documents..
      int rows = docset.countRows();
      docset.first();
      trans.clear();
      for (int x = 0; x < rows; x++)
      {
         cmd = trans.addCustomFunction("CURRENT_ACCESS_LEVEL" + x, "Doc_Issue_API.Get_Access_Control_Db", "DUMMY1");
         cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));
         docset.next();
      }

      // Buffer containing current access levels for documents..
      ASPBuffer current_access_buf = mgr.perform(trans);


      trans.clear();
      docset.first();
      count = 0;
      for (int x = 0; x < rows; x++)
      {
         // If access levels selected in step-4 was "User"..
         if ("USER_ACCESS".equals(access_level) && !"U".equals(current_access_buf.getValue("CURRENT_ACCESS_LEVEL" + x + "/DATA/DUMMY1")))
         {
            cmd = trans.addCustomCommand("SET_USER_ACCESS" + count++, "Doc_Issue_API.Revoke_Access_Control__");
            cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));
         }

         // If access levels selected in step-4 was "All"..
         else if ("ALL_ACCESS".equals(access_level) && !"A".equals(current_access_buf.getValue("CURRENT_ACCESS_LEVEL" + x + "/DATA/DUMMY1")))
         {
            cmd = trans.addCustomCommand("SET_ALL_ACCESS" + count++, "Doc_Issue_API.Set_Access_Control__");
            cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));
         }

         // If access levels selected in step-4 was "Group"..

         //Bug 58541
         else if ("GROUP_ACCESS".equals(access_level) &&(rows==1||(rows>1 && "YES".equals(ctx.readValue("OVERRIDE_ACCESS","")))))
         {
            cmd = trans.addCustomCommand("SET_GROUP_ACCESS" + count++, "Document_Issue_Access_API.Set_Document_Access");
            cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));
            cmd.addParameter("DUMMY", getGrantedGroups()); // Groups
            cmd.addParameter("DUMMY", getGrantedPersons()); // Persons
            cmd.addParameter("DUMMY", getGrantedViewAccess()); // View access
            cmd.addParameter("DUMMY", getGrantedEditAccess()); // Edit access


            if (!"G".equals(current_access_buf.getValue("CURRENT_ACCESS_LEVEL" + x + "/DATA/DUMMY1")))
            {
               cmd = trans.addCustomCommand("SET_USER_ACCESS" + count++, "Doc_Issue_API.Set_Group_Access__");
               cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
               cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
               cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
               cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));
            }
         }
         docset.next();
      }
      trans = mgr.perform(trans);


      //
      // Release all child documents, before releasing the parent
      //

      if ("YES".equals(ctx.readValue("UNRELEASED_SUB_DOCUMENTS")))
      {
         releaseChildren();
      }


      //
      // Finally, promote all documents to 'Released'
      //

      trans.clear();
      docset.first();
      count = 0;
      do
      {
         cmd = trans.addCustomCommand("SET_RELEASED" + count++, "Doc_Issue_API.Release_Doc");
         cmd.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", docset.getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", docset.getValue("DOC_REV"));

         //cmd = trans.addCustomCommand("SET_RELEASED" + count++, "Doc_Issue_API.Promote_To_Released__");
         //cmd.addParameter("DUMMY");
         //cmd.addParameter("OBJID", docset.getValue("OBJID"));
         //cmd.addParameter("OBJVERSION", docset.getValue("OBJVERSION"));
         //cmd.addParameter("DUMMY");
         //cmd.addParameter("DUMMY", "DO");
      }
      while (docset.next());
      trans = mgr.perform(trans);

      // Close wizard and refresh parent..
      close_wizard = true;
   }

   //Bug Id 69329, start
   public int GetNumberOfObjConnections()
   {
       ASPManager mgr = getASPManager();

       trans.clear();

       ASPCommand cmd = trans.addCustomFunction("NOOFOBJECTCON","DOC_REFERENCE_OBJECT_API.Get_Num_Of_All_Connections", "OBJECTS2_NO_OF_OBJ_CON");
       cmd.addParameter("DOC_CLASS",   docset.getValue("DOC_CLASS") );
       cmd.addParameter("DOC_NO",   docset.getValue("DOC_NO") );
       cmd.addParameter("DOC_SHEET",   docset.getValue("DOC_SHEET") );

       trans = mgr.perform(trans);

       String sno_of_obj_con = trans.getValue("NOOFOBJECTCON/DATA/OBJECTS2_NO_OF_OBJ_CON");
       
       trans.clear();

       return Integer.parseInt(sno_of_obj_con);

   }

   public void storeRadioButtons()
   {
       ASPManager mgr = getASPManager();
       String buttonDefault = mgr.readValue("DEFAULT");
       String buttonMoveAll = mgr.readValue("MOVEALL");
       String buttonNoMove  = mgr.readValue("NOMOVE");

       if ("DEFAULT".equals(buttonDefault)) 
       {
	   ctx.writeValue("RADIOBUTTON", buttonDefault);
	   sRadioButton = buttonDefault;
       }
       else if ("MOVEALL".equals(buttonMoveAll))
       {
	   ctx.writeValue("RADIOBUTTON", buttonMoveAll);
	   sRadioButton = buttonMoveAll;
       }
       else if ("NOMOVE".equals(buttonNoMove))
       {
	   ctx.writeValue("RADIOBUTTON", buttonNoMove);
	   sRadioButton = buttonNoMove;
       }
   }
   //Bug Id 69329, end

   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      disableOptions();
      disableHomeIcon();
      disableNavigate();


      //
      // Container for documents to release
      //

      docblk = mgr.newASPBlock("DOCUMENTS");

      docblk.addField("OBJID").
             setHidden();

      docblk.addField("OBJVERSION").
             setHidden();

      docblk.addField("DOC_CLASS").
             setSize(30).
             setLabel("DOCMAWDOCRELEASEWIZARDDOCCLASS: Doc Class");

      docblk.addField("DOC_NO").
             setSize(30).
             setLabel("DOCMAWDOCRELEASEWIZARDDOCNO: Doc No");

      docblk.addField("DOC_SHEET").
             setSize(30).
             setLabel("DOCMAWDOCRELEASEWIZARDSHEETNO: Doc Sheet");

      docblk.addField("DOC_REV").
             setSize(30).
             setLabel("DOCMAWDOCRELEASEWIZARDDOCREV: Doc Revision");

      docblk.addField("OLD_RELEASED_REVISIONS").
             setSize(30).
             setFunction("Doc_Issue_Util_API.Get_Number_Of_Released_Revs_(DOC_CLASS, DOC_NO, DOC_SHEET)").
             setLabel("DOCMAWDOCRELEASEWIZARDOLDRELEASEDREVISION: Old Released Rev");

      docblk.addField("ACCESS_CONTROL").
             setSize(30).
             setLabel("DOCMAWDOCRELEASEWIZARDACCESSTYPE: Access Type");

      docblk.addField("ACCESS_CONTROL_DB").
             setHidden();

      docblk.addField("NO_OBJECT_CONNECTIONS").
             setHidden().
             setFunction("Doc_Reference_Object_API.Get_Number_Of_Connections_(DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV)");

      docblk.addField("STRUCTURE").
             setHidden().
             setFunction("Doc_Title_API.Get_Structure_(DOC_CLASS, DOC_NO)");

      docblk.addField("NO_CHILDREN").
             setHidden().
             setFunction("Doc_Structure_API.Number_Of_Children_(DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV)");

      docblk.addField("CHILDREN_RELEASED").
             setHidden().
             setFunction("Doc_Structure_Util_Api.Check_Children_Released_(DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV)");

      docblk.addField("DOCUMENT_STATE").
             setDbName("STATE").
             setHidden();

      docblk.setView("DOC_ISSUE");
      docblk.setTitle("DOCMAWDOCRELEASEWIZARDBLOCKTITLE: The following documents will be released - Step 1 of 5");

      docset = docblk.getASPRowSet();
      docbar = mgr.newASPCommandBar(docblk);
      docbar.disableCommand(ASPCommandBar.FIND);
      docbar.addCustomCommand("releaseChildren", "Release Children");
      docbar.disableCommand("releaseChildren");

      doctbl = mgr.newASPTable(docblk);
      doctbl.disableQueryRow();
      doctbl.disableRowCounter();

      doclay = docblk.getASPBlockLayout();
      doclay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);


      //
      // Container for old revisions that may need
      // to be set to Obsolete
      //

      obsoleteblk = mgr.newASPBlock("OBSOLETE");

      obsoleteblk.addField("OBSOLETE_OBJID").
                  setHidden().
                  setDbName("OBJID");

      obsoleteblk.addField("OBSOLETE_OBJVERSION").
                  setHidden().
                  setDbName("OBJVERSION");

      obsoleteblk.addField("OBSOLETE_DOC_CLASS").
                  setDbName("DOC_CLASS").
                  setSize(10).
                  setReadOnly().
                  setLabel("DOCMAWDOCRELEASEWIZARDSTEP2DOCUMENTCLASS: Document Class");

      obsoleteblk.addField("OBSOLETE_DOC_NO").
                  setDbName("DOC_NO").
                  setSize(15).
                  setReadOnly().
                  setLabel("DOCMAWDOCRELEASEWIZARDSTEP2DOCUMENTNO: Document No");

      obsoleteblk.addField("OBSOLETE_DOC_SHEET").
                  setDbName("DOC_SHEET").
                  setSize(15).
                  setReadOnly().
                  setLabel("DOCMAWDOCRELEASEWIZARDSTEP2SHEETNO: Document Sheet");


      obsoleteblk.addField("OBSOLETE_DOC_REV").
                  setDbName("DOC_REV").
                  setSize(10).
                  setReadOnly().
                  setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADDOC4REV: Revision");

      obsoleteblk.addField("DOC_TITLE").
                  setSize(10).
                  setReadOnly().
                  setFunction("DOC_TITLE_API.Get_Title(:DOC_CLASS,:DOC_NO)").
                  setLabel("DOCMAWDOCRELEASEWIZARDDDOCTITLE: Title");

      obsoleteblk.addField("STATE").
                  setSize(10).
                  setReadOnly().
                  setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADSTATE: Status");

      obsoleteblk.addField("REV_NO","Number").
                  setSize(15).
                  setReadOnly().
                  setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADREVNO: Revision Number");

      obsoleteblk.setView("DOC_ISSUE");
      obsoleteblk.setTitle(mgr.translate("DOCMAWDOCRELEASEWIZARDOBSOLETETITLE: Select the documents you want to set to obsolete - Step 2 of 5"));

      obsoleteset = obsoleteblk.getASPRowSet();
      obsoletebar = mgr.newASPCommandBar(obsoleteblk);
      obsoletetbl = mgr.newASPTable(obsoleteblk);
      obsoletetbl.enableRowSelect();
      obsoletetbl.disableRowCounter();

      obsoletelay = obsoleteblk.getASPBlockLayout();
      obsoletelay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);



      //
      // Container for objects to be connected
      // to the released revision
      //


      objectsblk = mgr.newASPBlock("OBJECTS");

      objectsblk.addField("REPLACE").
                 setCheckBox("F,T").
                 setFunction("'F'").
                 setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADGETCONNECT: Replace");

      objectsblk.addField("OBJECT_DOC_CLASS").
                 setSize(10).
                 setDbName("DOC_CLASS").
                 setReadOnly().
                 setLabel("DOCMAWDOCRELEASEWIZARDSTEP3DOCUMENTCLASS: Doc Class");

      objectsblk.addField("OBJECT_DOC_NO").
                 setSize(10).
                 setDbName("DOC_NO").
                 setReadOnly().
                 setLabel("DOCMAWDOCRELEASEWIZARDSTEP3DOCUMENTNO: Doc No");

      objectsblk.addField("OBJECT_DOC_SHEET").
                 setSize(10).
                 setDbName("DOC_SHEET").
                 setReadOnly().
                 setLabel("DOCMAWDOCRELEASEWIZARDSTEP3SHEETNO: Doc Sheet");

      objectsblk.addField("OBJECT_DOC_REV").
                 setSize(10).
                 setDbName("DOC_REV").
                 setReadOnly().
                 setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADBLK1DOCREV: Doc Rev");

      objectsblk.addField("LU_NAME").
                 setSize(20).
                 setHidden();

      objectsblk.addField("KEY_REF").
                 setSize(20).
                 setHidden();

      objectsblk.addField("UNIT_DESC").
                 setSize(20).
                 setReadOnly().
                 setFunction("OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(:LU_NAME)").
                 setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADUNITDESC: Object Type");

      //Bug Id 78446 start - change the label of the feild 
      objectsblk.addField("INSTANCE_DESC").
                 setSize(40).
                 setReadOnly().
                 setFunction("OBJECT_CONNECTION_SYS.Get_Instance_Description(:LU_NAME,NULL,:KEY_REF)").
                 setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADOBJKEY: Object Key");
      //Bug Id 78446 end

      objectsblk.addField("DOC_OBJECT_DESC").
                 setSize(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADDOCOBJECTDESC: Object Description");

      objectsblk.addField("SURVEY_LOCKED_FLAG").
                 setSize(10).
                 setReadOnly().
                 setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADSURVEYLOCKEDFLAG: Doc Connection Status");

      objectsblk.addField("KEEP_LAST_DOC_REV").
                 setSize(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCRELEASEWIZARDDOCMAWDOCRELEASEWIZRADKEEPLASTDOCREV: Keep Revision");

      objectsblk.addField("KEEP_LAST_DOC_REV_DB").
                 setHidden();

      objectsblk.addField("LATEST_REVISION").
                 setFunction("Doc_Issue_API.Latest_Revision(DOC_CLASS, DOC_NO, DOC_SHEET)").
                 setHidden();

      //Bug Id 65167, Start
      objectsblk.addField("SURVEY_LOCKED_FLAG_DB").
                 setHidden();
      //Bug Id 65167, End

      objectsblk.setView("DOC_REFERENCE_OBJECT");
      objectsblk.setTitle(mgr.translate("DOCMAWDOCRELEASEWIZARDOBJECTSTITLE: Choose object(s) that should get the released revision - Step 3 of 5"));

      objectsset = objectsblk.getASPRowSet();
      objectsbar = mgr.newASPCommandBar(objectsblk);
      objectstbl = mgr.newASPTable(objectsblk);
      objectstbl.setEditable();
      objectstbl.disableRowCounter();

      objectslay = objectsblk.getASPBlockLayout();
      objectsbar.defineCommand(ASPCommandBar.OKFIND, "okFindObjects");
      objectsbar.disableCommand(ASPCommandBar.NEWROW);
      objectslay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      //Bug Id 69329, start
      //
      // Container for objects to be connected
      // to the released revision when the number of connections > 100
      //


      objectsblk2 = mgr.newASPBlock("OBJECTS2");

      objectsblk2.addField( "OBJECTS2_NO_OF_OBJ_CON" ).
		 setHidden();

      objectsblk2.setTitle(mgr.translate("DOCMAWDOCRELEASEWIZARDOBJECTSTITLE: Choose object(s) that should get the released revision - Step 3 of 5"));

      objectsset2 = objectsblk2.getASPRowSet();
      objectsbar2 = mgr.newASPCommandBar(objectsblk2);
      objectstbl2 = mgr.newASPTable(objectsblk2);
      objectstbl2.setEditable();
      objectstbl2.disableRowCounter();

      objectslay2 = objectsblk2.getASPBlockLayout();
      objectslay2.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      //Bug Id 69329, end

      //
      // Container for document access levels
      //

      accessblk = mgr.newASPBlock("ACCESS");

      //## TOCHECK - Field not required
      accessblk.addField("GROUP_ID");
      accessblk.addField("PERSON_ID");
      accessblk.addField("PERSON_NAME").
                setFunction("Person_Info_API.Get_Name(PERSON_ID)");
      accessblk.addField("EDIT_ACCESS");
      accessblk.addField("VIEW_ACCESS");
      accessblk.addField("ACCESS_OWNER");

      //## TOCHECK - Field not required
      accessblk.addField("DUMMY").
                setFunction("''");

      //## TOCHECK - Field not required
      accessblk.addField("DUMMY1").
                setFunction("''");

      accessblk.setView("DOCUMENT_ISSUE_ACCESS");
      accessset = accessblk.getASPRowSet();
      accessbar = mgr.newASPCommandBar(accessblk);
      accesslay = accessblk.getASPBlockLayout();
      accesslay.setDefaultLayoutMode(ASPBlockLayout.CUSTOM_LAYOUT);


      //
      // Container for Persons
      //

      personblk = mgr.newASPBlock("PERSON");

      personblk.addField("DOCUMENT_PERSON_ID").setDbName("PERSON_ID");;
      personblk.addField("NAME");

      personblk.setView("PERSON_INFO_PUBLIC");
      personset = personblk.getASPRowSet();



      //
      // Container for Groups
      //

      groupblk = mgr.newASPBlock("GROUP");

      groupblk.addField("DOCUMENT_GROUP_ID").setDbName("GROUP_ID");
      groupblk.addField("GROUP_DESCRIPTION");

      groupblk.setView("DOCUMENT_GROUP");
      groupset = groupblk.getASPRowSet();


      //
      // Static Javascript
      //

      appendJavaScript("var arrGroups;\n");
      appendJavaScript("var arrPersons;\n");
      appendJavaScript("var arrObjects;\n");

      appendJavaScript("function addPersonGroup()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var available = document.form.AVAILABLE_PERSONS_GROUPS;\n");
      appendJavaScript("   var grantees = document.form.GRANTED_PERSONS_GROUPS;\n");
      appendJavaScript("   clearSelections(grantees);\n");
      appendJavaScript("   for (var x = 0; x < available.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("       if (available.options[x].selected)\n");
      appendJavaScript("       {\n");
      appendJavaScript("          //Bug Id 75677,Start - No need to add if the person/group already exists\n");
      appendJavaScript("          for (var y = 0; y < grantees.length; y++)\n");
      appendJavaScript("          {\n");
      appendJavaScript("             if (available.options[x].value.toLowerCase() == grantees.options[y].value.toLowerCase())\n");
      appendJavaScript("                return;\n");
      appendJavaScript("          }\n");
      appendJavaScript("          //Bug Id 75677,End\n");
      appendJavaScript("          insertOption(grantees, available.options[x].value, available.options[x].text, true);\n"); //Bug Id 75677
      appendJavaScript("          view_access.push(new Array(available.options[x].value, \"1\"));\n");
      appendJavaScript("          setCurrentViewAccess();\n"); //Bug Id 75677
      appendJavaScript("       }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function removePersonGroup()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var available = document.form.AVAILABLE_PERSONS_GROUPS;\n");
      appendJavaScript("   var grantees = document.form.GRANTED_PERSONS_GROUPS;\n");
      appendJavaScript("   clearSelections(available);\n");
      appendJavaScript("   for (var x = 0; x < grantees.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("       if (grantees.options[x].selected)\n");
      appendJavaScript("       {\n");
      appendJavaScript("          removeViewAccess(grantees.options[x].value);\n");
      appendJavaScript("          grantees.options[x] = null;\n");
      appendJavaScript("          --x;\n");
      appendJavaScript("       }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function insertOption(select_list, option_value, option_text, selected)\n");//Bug Id 75677, Added new parameter selected
      appendJavaScript("{\n");
      appendJavaScript("   var new_option = document.createElement('option');\n");
      appendJavaScript("   new_option.value = option_value;\n");
      appendJavaScript("   new_option.appendChild(document.createTextNode(option_text));\n");
      appendJavaScript("   //Bug Id 75677, Start\n");
      appendJavaScript("   if (selected)\n");
      appendJavaScript("      new_option.selected = true;\n");
      appendJavaScript("   //Bug Id 75677, End\n");
      appendJavaScript("   option_text = option_text.toLowerCase();\n");
      appendJavaScript("   for (var x = 0; x < select_list.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("       if (option_text < select_list.options[x].text.toLowerCase())\n");
      appendJavaScript("       {\n");
      appendJavaScript("          select_list.insertBefore(new_option, select_list.options[x]);\n");
      appendJavaScript("          return;\n");
      appendJavaScript("       }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   select_list.insertBefore(new_option);\n");
      appendJavaScript("}\n");


      appendJavaScript("function clearSelections(select_list)\n");
      appendJavaScript("{\n");
      appendJavaScript("   for (var x = 0; x < select_list.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      select_list.options[x].selected = false;\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function countEntities(entity_type)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var available = document.form.AVAILABLE_PERSONS_GROUPS;\n");
      appendJavaScript("   var count = 0;\n");
      appendJavaScript("   for (var x = 0; x < available.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("       if (available.options[x].text.indexOf(entity_type) == 0)\n");
      appendJavaScript("          count++;\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function showGroups()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var available = document.form.AVAILABLE_PERSONS_GROUPS;\n");
      appendJavaScript("   if (document.form.SHOW_GROUPS.checked)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      for (var x = 0; x < arrGroups.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("          var new_option = document.createElement('option');\n");
      appendJavaScript("          new_option.value = arrGroups[x][0];\n");
      appendJavaScript("          new_option.appendChild(document.createTextNode(arrGroups[x][1]));\n");
      appendJavaScript("          available.insertBefore(new_option);\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (arrGroups == null)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         arrGroups = new Array(countEntities(\"GROUP_\"));\n");
      appendJavaScript("      }\n");
      appendJavaScript("      var count = 0;\n");
      appendJavaScript("      for (var x = 0; x < available.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (available.options[x].value.indexOf(\"GROUP_\") == 0)\n");
      appendJavaScript("         {\n");
      appendJavaScript("            arrGroups[count] = new Array(2);\n");
      appendJavaScript("            arrGroups[count][0] = available.options[x].value;\n");
      appendJavaScript("            arrGroups[count++][1] = available.options[x].text;\n");
      appendJavaScript("            available.options[x--] = null;\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function showPersons()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var available = document.form.AVAILABLE_PERSONS_GROUPS;\n");
      appendJavaScript("   if (document.form.SHOW_PERSONS.checked)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      for (var x = 0; x < arrPersons.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("          var new_option = document.createElement('option');\n");
      appendJavaScript("          new_option.value = arrPersons[x][0];\n");
      appendJavaScript("          new_option.appendChild(document.createTextNode(arrPersons[x][1]));\n");
      appendJavaScript("          available.insertBefore(new_option, available.options[0 + x]);\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (arrPersons == null)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         arrPersons = new Array(countEntities(\"PERSON_\"));\n");
      appendJavaScript("      }\n");
      appendJavaScript("      var count = 0;\n");
      appendJavaScript("      for (var x = 0; x < available.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (available.options[x].value.indexOf(\"PERSON_\") == 0)\n");
      appendJavaScript("         {\n");
      appendJavaScript("            arrPersons[count] = new Array(2);\n");
      appendJavaScript("            arrPersons[count][0] = available.options[x].value;\n");
      appendJavaScript("            arrPersons[count++][1] = available.options[x].text;\n");
      appendJavaScript("            available.options[x--] = null;\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function savePersonsAndGroups()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var grantees = document.form.GRANTED_PERSONS_GROUPS;\n");
      appendJavaScript("   var list = \"\";\n");
      appendJavaScript("   for (var x = 0; x < grantees.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("       list += grantees.options[x].value + String.fromCharCode(31) + grantees.options[x].text + String.fromCharCode(30)\n");
      appendJavaScript("   }\n");
      appendJavaScript("   document.form.GRANTEES.value = list;\n");
      appendJavaScript("   var access = \"\";\n");
      appendJavaScript("   for (var x = 0; x < view_access.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("       access += view_access[x][0] + String.fromCharCode(31) + view_access[x][1] + String.fromCharCode(30)\n");
      appendJavaScript("   }\n");
      appendJavaScript("   document.form.GRANTEES_VIEW_ACCESS.value = access;\n");
      appendJavaScript("}\n");

      //Bug Id 70808, Start
      appendJavaScript("function updateBackgroungJob()\n");
      appendJavaScript("{\n");
      appendJavaScript("   if (document.form.BACKGROUND_JOB.checked)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      document.form.ACCEPT_DEFAULTS.checked = true;\n");
      appendJavaScript("      document.form.UNRELEASED_SUB_DOCUMENTS.checked = false;\n");
      appendJavaScript("      document.form.ACCEPT_DEFAULTS.disabled = true;\n");
      appendJavaScript("      document.form.UNRELEASED_SUB_DOCUMENTS.disabled = true;\n");
      appendJavaScript("      updateControlButtons();\n");      
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      document.form.ACCEPT_DEFAULTS.disabled = false;\n");
      appendJavaScript("      document.form.UNRELEASED_SUB_DOCUMENTS.disabled = false;\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");
      //Bug Id 70808, End

      appendJavaScript("function updateControlButtons()\n");
      appendJavaScript("{\n");
      appendJavaScript("   if (document.form.ACCEPT_DEFAULTS.checked)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (!document.form.UNRELEASED_SUB_DOCUMENTS.disabled)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (!document.form.UNRELEASED_SUB_DOCUMENTS.checked)\n");
      appendJavaScript("         {\n");
      appendJavaScript("             document.form.NEXT.disabled = true;\n");
      appendJavaScript("             document.form.FINISH.disabled = true;\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else\n");
      appendJavaScript("         {\n");
      appendJavaScript("             document.form.NEXT.disabled = true;\n");
      appendJavaScript("             document.form.FINISH.disabled = false;\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("         document.form.NEXT.disabled = true;\n");
      appendJavaScript("         document.form.FINISH.disabled = false;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      document.getElementById(\"RevisionStyle\").style.display = 'none';\n");
      appendJavaScript("      document.getElementById(\"AcceptAllDefaults\").style.display = 'block';\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (!document.form.UNRELEASED_SUB_DOCUMENTS.disabled)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (!document.form.UNRELEASED_SUB_DOCUMENTS.checked)\n");
      appendJavaScript("         {\n");
      appendJavaScript("             document.form.NEXT.disabled = true;\n");
      appendJavaScript("             document.form.FINISH.disabled = true;\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else\n");
      appendJavaScript("         {\n");
      appendJavaScript("             document.form.NEXT.disabled = false;\n");
      appendJavaScript("             document.form.FINISH.disabled = true;\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("         document.form.NEXT.disabled = false;\n");
      appendJavaScript("         document.form.FINISH.disabled = true;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      document.getElementById(\"RevisionStyle\").style.display = 'block';\n");
      appendJavaScript("      document.getElementById(\"AcceptAllDefaults\").style.display = 'none';\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function executeServerCommand(command)\n");
      appendJavaScript("{\n");
      appendJavaScript("   f.__STRUCTURE_Perform.value = command;\n");
      appendJavaScript("   commandSet('STRUCTURE.Perform','');\n");
      appendJavaScript("}\n");


      appendJavaScript("function showConfirmation(message, server_command, client_command, execute_client_command)\n");
      appendJavaScript("{\n");
      appendJavaScript("   if (confirm(message))\n");
      appendJavaScript("   {\n");
      appendJavaScript("      f.__DOCUMENTS_Perform.value = server_command;\n");
      appendJavaScript("      commandSet('DOCUMENTS.Perform','');\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else if (execute_client_command)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      eval(client_command);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function closeWindow()\n");
      appendJavaScript("{\n");
      appendJavaScript("   window.close();\n");
      appendJavaScript("}\n");


      appendJavaScript("function showAlert(message, client_command)\n");
      appendJavaScript("{\n");
      appendJavaScript("   alert(message);\n");
      appendJavaScript("   eval(client_command);\n");
      appendJavaScript("}\n");


      appendJavaScript("function getElements(name)\n");
      appendJavaScript("{\n");
      appendJavaScript("   return eval('document.form.' + name);\n");
      appendJavaScript("}\n");


      appendJavaScript("function setObjectSelection()\n");
      appendJavaScript("{\n");
      appendJavaScript("    var replacebox = getElements('_REPLACE');\n");
      appendJavaScript("    var replace    = getElements('REPLACE');\n");
      appendJavaScript("    if (arrObjects == null)\n");
      appendJavaScript("    {\n");
      appendJavaScript("        arrObjects = new Array(replacebox.length);\n");
      appendJavaScript("        for (var x = 0; x < replacebox.length; x++)\n");
      appendJavaScript("        {\n");
      appendJavaScript("            if (replacebox[x].checked)\n");
      appendJavaScript("               arrObjects[x] = \"1\";\n");
      appendJavaScript("            else\n");
      appendJavaScript("               arrObjects[x] = \"0\";\n");
      appendJavaScript("        }\n");
      appendJavaScript("    }\n");
      appendJavaScript("    for (var x = 0; x < replacebox.length; x++)\n");
      appendJavaScript("    {\n");
      appendJavaScript("         if (document.form.OBJECT_SELECTION[0].checked && replacebox[x].disabled == false)\n");//Bug Id 65167
      appendJavaScript("         {\n");
      appendJavaScript("             replacebox[x].checked = true;\n");
      appendJavaScript("             replace[x + 1].value = \"1\";\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else if (document.form.OBJECT_SELECTION[1].checked)\n");
      appendJavaScript("         {\n");
      appendJavaScript("             if (arrObjects[x] == \"0\")\n");
      appendJavaScript("             {\n");
      appendJavaScript("                 replacebox[x].checked = false;\n");
      appendJavaScript("                 replace[x + 1].value = \"0\";\n");
      appendJavaScript("             }\n");
      appendJavaScript("             else if (replacebox[x].disabled == false)\n");//Bug Id 65167
      appendJavaScript("             {\n");
      appendJavaScript("                 replacebox[x].checked = true;\n");
      appendJavaScript("                 replace[x + 1].value = \"1\";\n");
      appendJavaScript("             }\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else if (document.form.OBJECT_SELECTION[2].checked)\n");
      appendJavaScript("         {\n");
      appendJavaScript("             replacebox[x].checked = false;\n");
      appendJavaScript("             replace[x + 1].value = \"0\";\n");
      appendJavaScript("         }\n");
      appendJavaScript("    }\n");
      appendJavaScript("}\n");

      //Bug 58541 start
      appendJavaScript("function overrideAccessSettings(obj)\n");
      appendJavaScript("{\n");
      appendJavaScript("   if(obj.checked){\n");
      appendJavaScript("      document.form.OVERRIDE_ACCESS_TEXT.value=\"YES\";\n");
      appendJavaScript("      document.form.NEXT.disabled=false;\n");
      appendJavaScript("   }else{\n");
      appendJavaScript("      document.form.OVERRIDE_ACCESS_TEXT.value=\"NO\";\n");
      appendJavaScript("      document.form.NEXT.disabled=true;\n");
      appendJavaScript("   } \n");
      appendJavaScript("   document.form.FINISH.disabled=!document.form.NEXT.disabled;\n");
      appendJavaScript("}\n");
        //Bug 58541 end


      appendJavaScript("function setCurrentViewAccess()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var index = document.form.GRANTED_PERSONS_GROUPS.selectedIndex;\n");
      appendJavaScript("   var access = getViewAccess(document.form.GRANTED_PERSONS_GROUPS.options[index].value);\n");
      appendJavaScript("   document.form.VIEW.checked = (access == \"1\");\n");
      appendJavaScript("}\n");

      //Bug Id 75677, Modified updateViewAccess() to handle multiple view access
      appendJavaScript("function updateViewAccess()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var grantees = document.form.GRANTED_PERSONS_GROUPS;\n");
      appendJavaScript("   for (var x = 0; x < grantees.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (grantees.options[x].selected)\n");
      appendJavaScript("         setViewAccess(document.form.GRANTED_PERSONS_GROUPS.options[x].value, (document.form.VIEW.checked) ? \"1\" : \"0\");\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function getViewAccess(option_)\n");
      appendJavaScript("{\n");
      appendJavaScript("   for (var x = 0; x < view_access.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (option_ == view_access[x][0])\n");
      appendJavaScript("         return view_access[x][1];\n");
      appendJavaScript("   }\n");
      appendJavaScript("   return \"1\";\n");
      appendJavaScript("}\n");


      appendJavaScript("function setViewAccess(option_, value)\n");
      appendJavaScript("{\n");
      appendJavaScript("   for (var x = 0; x < view_access.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (option_ == view_access[x][0])\n");
      appendJavaScript("      {\n");
      appendJavaScript("         view_access[x][1] = value;\n");
      appendJavaScript("         return;\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function removeViewAccess(option_)\n");
      appendJavaScript("{\n");
      appendJavaScript("   for (var x = 0; x < view_access.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (option_ == view_access[x][0])\n");
      appendJavaScript("      {\n");
      appendJavaScript("         view_access.splice(x, 1);\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");

   }


   public void adjust()
   {
      ASPManager mgr = getASPManager();

      obsoletebar.disableCommand(obsoletebar.FIND);
      objectsbar.disableCommand(objectsbar.FIND);
   }


   private void okFindPersonsAndGroups()
   {
      if (!ctx.readFlag("POPULATE_PERSONS_GROUPS", true))
         return;


      ASPManager mgr = getASPManager();


      // Get persons with access to document..
      trans.clear();
      ASPQuery query = trans.addQuery("DOCUMENT_ACCESS_PERSONS", "DOCUMENT_ISSUE_ACCESS", "PERSON_ID, Person_Info_API.Get_Name(PERSON_ID) NAME, EDIT_ACCESS, VIEW_ACCESS", "DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ? AND PERSON_ID IS NOT NULL", "");
      query.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
      query.addParameter("DOC_NO", docset.getValue("DOC_NO"));
      query.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
      query.addParameter("DOC_REV", docset.getValue("DOC_REV"));


      // Get groups with access to document..
      query = trans.addQuery("DOCUMENT_ACCESS_GROUPS", "DOCUMENT_ISSUE_ACCESS", "GROUP_ID, Document_Group_API.Get_Group_Description(GROUP_ID) GROUP_DESCRIPTION, EDIT_ACCESS, VIEW_ACCESS", "DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ? AND GROUP_ID IS NOT NULL", "");
      query.addParameter("DOC_CLASS", docset.getValue("DOC_CLASS"));
      query.addParameter("DOC_NO", docset.getValue("DOC_NO"));
      query.addParameter("DOC_SHEET", docset.getValue("DOC_SHEET"));
      query.addParameter("DOC_REV", docset.getValue("DOC_REV"));


      // Get Person details..
      query = trans.addQuery("PERSONS_CHARACTERS", "SELECT unique SUBSTR(NAME,0,1) FROM PERSON_INFO_PUBLIC"); //Bug Id 75677
      query.setBufferSize(10000);//Bug Id 75677, So that we don't run out of buffer


      // Get Group details..
      query = trans.addQuery("GROUPS_CHARACTERS", "SELECT unique SUBSTR(GROUP_DESCRIPTION,0,1) FROM DOCUMENT_GROUP");//Bug Id 75677
      query.setBufferSize(10000);//Bug Id 75677, So that we don't run out of buffer
      trans = mgr.perform(trans);


      // Buffer containing list of persons with access to the document..
      ASPBuffer person_access_buf = trans.getBuffer("DOCUMENT_ACCESS_PERSONS");
      if (person_access_buf != null)
      {
         person_access_buf.removeItem("INFO");
         ctx.writeBuffer("DOCUMENT_ACCESS_PERSONS", person_access_buf);
      }
      else
      {
         person_access_buf = createBuffer("DOCUMENT_ACCESS_PERSONS");
      }


      // Buffer containing list of groups with access to the document..
      ASPBuffer group_access_buf = trans.getBuffer("DOCUMENT_ACCESS_GROUPS");
      if (group_access_buf != null)
      {
         group_access_buf.removeItem("INFO");
         ctx.writeBuffer("DOCUMENT_ACCESS_GROUPS", group_access_buf);
      }
      else
      {
         group_access_buf = createBuffer("DOCUMENT_ACCESS_GROUPS");
      }
      //Bug Id 75677, Start
      String GROUP_PERSON_LIST = "";
      String start_character; 
      // Buffer containing list of persons in the enterprise..
      ASPBuffer person_character_buf = trans.getBuffer("PERSONS_CHARACTERS");
      person_character_buf.removeItem("INFO");
      
      // Buffer containing list of groups created in Basic Data..
      ASPBuffer group_character_buf = trans.getBuffer("GROUPS_CHARACTERS");
      group_character_buf.removeItem("INFO");
      
      //Getting all to one character list
      int count = person_character_buf.countItems();
      for (int x = 0; x < count; x++)
      {
	  start_character = person_character_buf.getBufferAt(x).getValueAt(0).toUpperCase()+ (char)31;
	  if (GROUP_PERSON_LIST.indexOf(start_character) == -1) {
	     GROUP_PERSON_LIST=GROUP_PERSON_LIST+start_character ;
	  }       
      }
      
      count = group_character_buf.countItems();
      for (int x = 0; x < count; x++)
      {
	  start_character = group_character_buf.getBufferAt(x).getValueAt(0).toUpperCase()+ (char)31;
	  if (GROUP_PERSON_LIST.indexOf(start_character) == -1) {
	     GROUP_PERSON_LIST=GROUP_PERSON_LIST+start_character ;
	  }   
      }

      ctx.writeValue("CHARACTER_LIST", GROUP_PERSON_LIST);
      //Bug Id 75677, End

      ctx.writeFlag("POPULATE_PERSONS_GROUPS", false);
   }


   private void removePerson(String person_id)
   {
      ASPBuffer buf = ctx.readBuffer("PERSONS");

      int count = buf.countItems();
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data = buf.getBufferAt(x);

         if (person_id.equals(data.getValue("PERSON_ID")))
         {
            buf.removeItemAt(x);
            count--;
         }
      }

      // Save changes back to the context..
      ctx.writeBuffer("PERSONS", buf);
   }


   private void removeGroup(String group_id)
   {
      ASPBuffer buf = ctx.readBuffer("GROUPS");

      int count = buf.countItems();
      for (int x = 0; x < count; x++)
      {
         ASPBuffer data = buf.getBufferAt(x);

         if (group_id.equals(data.getValue("GROUP_ID")))
         {
            buf.removeItemAt(x);
            count--;
         }
      }

      // Save changes back to the context..
      ctx.writeBuffer("GROUPS", buf);
   }


   public String drawRadio(String label, String name, String value, boolean checked, String tag)
   {
      return "<input class=radioButton type=radio name=\"" + name + "\" value=\"" + value + "\"" + (checked ? " CHECKED " : "") + " " + (tag == null ? "" : tag) + ">&nbsp;<font class=normalTextValue>" + label + "</font>";
   }



   private String enableButton(boolean enable)
   {
      if (!enable)
         return "disabled";
      else
         return "";
   }


   private String onClickScript(String event)
   {
      if (event == null)
         return "";
      else
         return "onClick=javascript:" + event;
   }


   private void drawButtonPanel(boolean enable_back, String back_event, boolean enable_next, String next_event, boolean endable_finish, String finish_event)
   {
      ASPManager mgr = getASPManager();

      // Draw buttons..
      //Bug Id 78998, Changed the order of the buttons
      appendToHTML("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td align=\"right\">\n");
      appendToHTML(fmt.drawButton("CANCEL", mgr.translate("DOCMAWDOCRELEASEWIZARDSUBMITCANCEL: Cancel"), "OnClick=javascript:window.close();"));
      appendToHTML("&nbsp;");
      appendToHTML(fmt.drawSubmit("BACK", mgr.translate("DOCMAWDOCRELEASEWIZARDSUBMITBACK: < Back"), enableButton(enable_back) + " " + onClickScript(back_event)));
      appendToHTML("&nbsp;");
      appendToHTML(fmt.drawSubmit("NEXT", mgr.translate("DOCMAWDOCRELEASEWIZARDSUBMITNEXT: Next >"), enableButton(enable_next)  + " " + onClickScript(next_event)));
      appendToHTML("&nbsp;");
      appendToHTML(fmt.drawSubmit("FINISH", mgr.translate("DOCMAWDOCRELEASEWIZARDSUBMITFINISH: Finish"), enableButton(endable_finish)  + " " + onClickScript(finish_event)));
      appendToHTML("&nbsp;&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</table>\n");
   }

   protected String getDescription()
   {
      return "DOCMAWDOCRELEASEWIZARDTITLE: Document Release Wizard";
   }


   protected String getTitle()
   {
      return "DOCMAWDOCRELEASEWIZARDTITLE: Document Release Wizard";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      boolean lock_objects = false; //Bug Id 65167

      String current_step = ctx.readValue("CURRENT_STEP");

      if ("STEP_1".equals(current_step))
      {
         appendToHTML(docbar.showBar());
         beginDataPresentation();
         appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         appendToHTML("<tr><td height=\"260\" width=\"130\" rowspan=\"3\"><img src=\"images/ReleaseDocument.jpg\"></td>\n");
         appendToHTML("<td valign=\"top\">\n");
         appendToHTML(doclay.generateDataPresentation());
         appendToHTML("</td></tr>\n");

         // Options depending on when Accpet All Defaults was chosen..
         appendToHTML("<tr><td valign=\"middle\" height=\"120\">\n");

         appendToHTML("<div id=\"AcceptAllDefaults\" style=\"display: none;\">");
         appendToHTML("<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\" width=\"75%\">\n");
         appendToHTML("<tr>");
         appendToHTML("<td colspan=\"2\">&nbsp;&nbsp;");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCEPTDEFAULTSNOTE1: Note - By accepting all defaults the following will happen:")));
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
         appendToHTML("<td nowrap>");
         appendToHTML(" - ");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCEPTDEFAULTSNOTE2: All old Released revisions will be set to Obsolete")));
         appendToHTML("<br>");
         appendToHTML(" - ");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCEPTDEFAULTSNOTE3: Object connections will be copied according to the 'Copy Flag'")));
         appendToHTML("<br>");
         appendToHTML(" - ");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCEPTDEFAULTSNOTE4: The Document Access type will remain the same")));
         appendToHTML("<br>");
         appendToHTML(" - ");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCEPTDEFAULTSNOTE5: The Access Permissions, if Group Access is used, will remain the same")));
         appendToHTML("</td></tr>\n");
         appendToHTML("</table>\n");
         appendToHTML("</div>\n");


         appendToHTML("<div id=\"RevisionStyle\" style=\"display: block;\">");
         appendToHTML("<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\" width=\"75%\">\n");
         String revision_style = getRevisionStyle();
         if ("FREE".equals(revision_style))
         {
            String set_obsolete = ctx.readValue("SET_OBSOLETE");
            appendToHTML("<tr>");
            appendToHTML("<td>&nbsp;&nbsp;&nbsp;</td>");
            appendToHTML("<td nowrap>");
            appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDRELEASENOTE1: If there exists other document revisions that are not obsolete it is recommended that you set them to obsolete now.")));
            appendToHTML("</td></tr>\n");
            appendToHTML("<tr>\n");
            appendToHTML("<td >&nbsp;&nbsp;&nbsp;</td>");
            appendToHTML("<td nowrap>");
            appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDRELEASENOTE2: Do you want to set old document revisions to obsolete?")));
            appendToHTML("</td></tr>\n");
            appendToHTML("<tr><td></td><td>&nbsp;&nbsp;&nbsp;\n");
            appendToHTML(drawRadio(mgr.translate("DOCMAWDOCRELEASEWIZARDSETOBSOLETEYES: Yes"), "SET_OBSOLETE", "YES", "YES".equals(set_obsolete), ""));
            appendToHTML("</td></tr>\n");
            appendToHTML("<tr><td></td><td>&nbsp;&nbsp;&nbsp;\n");
            appendToHTML(drawRadio(mgr.translate("DOCMAWDOCRELEASEWIZARDSETOBSOLETENO: No"), "SET_OBSOLETE", "NO", "NO".equals(set_obsolete), ""));
            appendToHTML("</td></tr>\n");
         }
         else if ("RESTRICTED".equals(revision_style))
         {
            appendToHTML("<tr>");
            appendToHTML("<td >&nbsp;&nbsp;&nbsp;</td>");
            appendToHTML("<td nowrap>");
            appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDRELEASENOTE3: The Revision Style in the system is set to restricted. This means that only one revision per document can have status Released.")));
            appendToHTML("</td></tr>\n");
            appendToHTML("<tr>\n");
            appendToHTML("<td >&nbsp;&nbsp;&nbsp;</td>");
            appendToHTML("<td nowrap>");
            appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDRELEASENOTE4: Any previously document revisions that are released will now be automatically set to status Obsolete.")));
            appendToHTML("</td></tr>\n");
         }
         appendToHTML("</table>");
         appendToHTML("</div>");
         appendToHTML("</td></tr>\n");


         // Option for accepting all defaults and releasing sub documents if any..
         appendToHTML("<tr><td valign=\"bottom\" >\n");
         appendToHTML("<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\" width=\"75%\">\n");
         appendToHTML("<tr>");
         appendToHTML("<td nowrap>&nbsp;&nbsp;");
         appendToHTML(fmt.drawCheckbox("ACCEPT_DEFAULTS", "YES", false, "onClick=javascript:updateControlButtons()"));
         //Bug 58526, Start
         appendToHTML(fmt.drawReadLabel("DOCMAWDOCRELEASEWIZARDACCEPTALLDEFAULTS: Accept All Defaults"));
         //Bug 58526, End
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr>");
         appendToHTML("<td nowrap>&nbsp;&nbsp;");
         appendToHTML(fmt.drawCheckbox("UNRELEASED_SUB_DOCUMENTS", "YES", (!unreleased_sub_documents && "YES".equals(ctx.readValue("UNRELEASED_SUB_DOCUMENTS"))), (("YES".equals(ctx.readValue("UNRELEASED_SUB_DOCUMENTS"))) ? "" : "disabled") + " onClick=javascript:updateControlButtons()"));
         //Bug 58526, Start
         appendToHTML(fmt.drawReadLabel("DOCMAWDOCRELEASEWIZARDRELEASESUBDOCUMENTS: Release Sub Documents"));
         //Bug 58526, End
         appendToHTML("</td></tr>\n");
         //Bug 70808, Start 
         if (bBackgroundJobAllowed) {
            appendToHTML("<tr>");
            appendToHTML("<td nowrap>&nbsp;&nbsp;");
            appendToHTML(fmt.drawCheckbox("BACKGROUND_JOB", "YES", false, " onClick=javascript:updateBackgroungJob()"));
            appendToHTML(fmt.drawReadLabel("DOCMAWDOCRELEASEWIZARDBGJOB: Perform as Background job"));
            appendToHTML("</td></tr>\n");
         }         
         //Bug 70808, End
         appendToHTML("</table>\n");
         appendToHTML("</td></tr></table>\n");
         endDataPresentation(false);

         // Draw control buttons..
         drawButtonPanel(false, null, (!unreleased_sub_documents && "YES".equals(ctx.readValue("UNRELEASED_SUB_DOCUMENTS")) || "NO".equals(ctx.readValue("UNRELEASED_SUB_DOCUMENTS"))), null, false, null);
      }
      else if ("STEP_2".equals(current_step))
      {
         appendToHTML(obsoletebar.showBar());
         beginDataPresentation();
         appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         appendToHTML("<tr><td height=\"260\" width=\"130\" rowspan=\"3\"><img src=\"images/ReleaseDocument.jpg\"></td>\n");
         appendToHTML("<td valign=\"top\">\n");
         appendToHTML(obsoletelay.generateDataPresentation());
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr><td valign=\"bottom\">\n");
         appendToHTML("<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\" width=\"75%\">\n");
         appendToHTML("<tr>");
         appendToHTML("<td nowrap align=\"left\">");
         appendToHTML("&nbsp;&nbsp;");
         appendToHTML(fmt.drawReadValue(obsoleteset.countRows() + ""));
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDOBSOLETENOTE: un-obsolete revision(s) were found.")));
         appendToHTML("</td></tr>\n");
         appendToHTML("</table></td></tr></table>\n");
         endDataPresentation(false);

         // Draw control buttons..
         drawButtonPanel(true, null, true, null, false, null);
      }
      else if ("STEP_3".equals(current_step))
      {
         appendToHTML(objectsbar.showBar());
         beginDataPresentation();
         appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         appendToHTML("<tr><td height=\"060\" width=\"130\" rowspan=\"3\"><img src=\"images/ReleaseDocument.jpg\"></td>\n");
         appendToHTML("<td valign=\"top\">\n");
         appendToHTML(objectslay.generateDataPresentation());

         if (objectsset.countRows() > 1)
         {
            appendToHTML("&nbsp;&nbsp;");
            appendToHTML(drawRadio(mgr.translate("DOCMAWDOCRELEASEWIZARDSELECTALL: All"), "OBJECT_SELECTION", "SELECT_ALL", false, "OnClick=javascript:setObjectSelection();"));
            appendToHTML("&nbsp;&nbsp;");
            appendToHTML(drawRadio(mgr.translate("DOCMAWDOCRELEASEWIZARDSELECTDEFAULT: Default"), "OBJECT_SELECTION", "SELECT_DEFAULT", true, "OnClick=javascript:setObjectSelection();"));
            appendToHTML("&nbsp;&nbsp;");
            appendToHTML(drawRadio(mgr.translate("DOCMAWDOCRELEASEWIZARDSELECTNONE: None"), "OBJECT_SELECTION", "SELECT_NONE", false, "OnClick=javascript:setObjectSelection();"));
         }
         //Bug 65167, Start
	 objectsset.first();
	 if (objectsset.countRows() == 1)
	 {
	     if ("1".equals(objectsset.getValue("SURVEY_LOCKED_FLAG_DB")) && 
		(("L".equals(objectsset.getValue("KEEP_LAST_DOC_REV_DB")) && 
		 (!(objectsset.getValue("LATEST_REVISION").equals(getReleaseRevision(objectsset.getValue("DOC_NO"), objectsset.getValue("DOC_SHEET")))))) || 
		("F".equals(objectsset.getValue("KEEP_LAST_DOC_REV_DB")) && 
		 !(isReleaseRevision(objectsset.getValue("DOC_NO"), objectsset.getValue("DOC_SHEET"), objectsset.getValue("DOC_REV"))))))
	        {
		   appendDirtyJavaScript("document.form._REPLACE.disabled = true;\n");
		   appendDirtyJavaScript("document.form._REPLACE.checked = false;\n");
		   appendDirtyJavaScript("document.form.REPLACE.value = \"\";\n");//Bug Id 65687
		   lock_objects = true;
                 }

	 }
         else
	 {
	    for (int x = 0; x < objectsset.countRows(); x++)
	    {
	        if ("1".equals(objectsset.getValue("SURVEY_LOCKED_FLAG_DB")) && 
		    (("L".equals(objectsset.getValue("KEEP_LAST_DOC_REV_DB")) && 
		      (!(objectsset.getValue("LATEST_REVISION").equals(getReleaseRevision(objectsset.getValue("DOC_NO"), objectsset.getValue("DOC_SHEET")))))) || 
		     ("F".equals(objectsset.getValue("KEEP_LAST_DOC_REV_DB")) && 
		       !(isReleaseRevision(objectsset.getValue("DOC_NO"), objectsset.getValue("DOC_SHEET"), objectsset.getValue("DOC_REV"))))))
	       {
		     appendDirtyJavaScript("document.form._REPLACE["+x+"].disabled = true;\n");
		     appendDirtyJavaScript("document.form._REPLACE["+x+"].checked = false;\n");
		     appendDirtyJavaScript("document.form.REPLACE["+(x+1)+"].value = \"\";\n");//Bug Id 65687
		     lock_objects = true;
               }
	       objectsset.next();
	    }
	 }
	 objectsset.first();
	 //Bug 65167, End
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr><td valign=\"bottom\">\n");
	 //Bug 65167, Start
	 if (lock_objects) {
	    appendToHTML("<div id='objectselectdiv' style='background-color:#FFCC66'>\n");
            appendToHTML("&nbsp;&nbsp;");
	    appendToHTML(fmt.drawReadValue("DOCMAWDOCRELEASEWIZARDLOCKOBJS: Selection of lock documents are disabled since they cannot be moved to other revisions."));
	    appendToHTML("</div>\n");
	 }
	 //Bug 65167, End
         appendToHTML("<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\" width=\"75%\">\n");
         appendToHTML("<tr>");
         appendToHTML("<td nowrap align=\"left\">");
         appendToHTML("&nbsp;&nbsp;");
         appendToHTML(fmt.drawReadValue(objectsset.countRows() + ""));
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDOBJECTSNOTE: object(s) were found.")));
         appendToHTML("</td></tr>\n");
         appendToHTML("</table></td></tr></table>\n");
         endDataPresentation(false);

         // Draw control buttons..
         drawButtonPanel(true, null, true, null, false, null);
      }

      //Bug Id 69329, start
      else if ("STEP_3_2".equals(current_step))
      {
	 String sRadioButton = ctx.readValue("RADIOBUTTON");

         appendToHTML(objectsbar2.showBar());
         beginDataPresentation();
         appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
	 appendToHTML("<tr><td height=\"260\" width=\"130\" rowspan=\"6\"><img src=\"images/ReleaseDocument.jpg\"></td>\n");
	 appendToHTML("<td nowrap colspan=\"2\" >");

	 appendToHTML("&nbsp;&nbsp;" + fmt.drawReadLabel("DOCMAWRELEASEWIZARDOBJCONNOTE1: There are more connected objects than can be displayed in the wizard."));
         
	 appendToHTML("<br>\n");

	 appendToHTML("&nbsp;&nbsp;" +  fmt.drawReadLabel("DOCMAWRELEASEWIZARDOBJCONNOTE2: Use the radio buttons below to select how to treat the connected objects:"));
	 
	 appendToHTML("   </td></tr>\n");

	 appendToHTML("   <tr><td>\n");
	 appendToHTML("   &nbsp;&nbsp;&nbsp;&nbsp;" + fmt.drawRadio(mgr.translate("DOCMAWRELEASEWIZARDOBJDEFAULT: Use Default"),"DEFAULT", "DEFAULT", "DEFAULT".equals(sRadioButton), "OnClick='setRadioButton(\"DEFAULT\");'"));
	 appendToHTML("   </td></tr>\n");

	 appendToHTML("   <tr><td>\n");
	 appendToHTML("   &nbsp;&nbsp;&nbsp;&nbsp;" + fmt.drawRadio(mgr.translate("DOCMAWRELEASEWIZARDOBJMOVEALL: Move All Object Connections"),"MOVEALL", "MOVEALL", "MOVEALL".equals(sRadioButton), "OnClick='setRadioButton(\"MOVEALL\");'"));
	 appendToHTML("   </td></tr>\n");

	 appendToHTML("   <tr><td>\n");
	 appendToHTML("   &nbsp;&nbsp;&nbsp;&nbsp;" + fmt.drawRadio(mgr.translate("DOCMAWRELEASEWIZARDOBJNOMOVE: Don't Move Connections"),"NOMOVE", "NOMOVE", "NOMOVE".equals(sRadioButton), "OnClick='setRadioButton(\"NOMOVE\");'"));
	 appendToHTML("   </td></tr>\n");

         appendToHTML("</td></tr>\n");
         appendToHTML("<tr><td valign=\"bottom\">\n");

	 endDataPresentation(false);

         // Draw control buttons..
         drawButtonPanel(true, null, true, null, false, null);
      }
      //Bug Id 69329, end

      else if ("STEP_4".equals(current_step))
      {
         //Bug 58541
         appendToHTML("<input type=\"hidden\" name=\"OVERRIDE_ACCESS_TEXT\" value=\""+ctx.readValue("OVERRIDE_ACCESS")+"\">\n");//

         accessblk.setTitle(mgr.translate("DOCMAWDOCRELEASEWIZARDACCESSTITLE: Choose an appropriate document access level - Step 4 of 5"));
         appendToHTML(accessbar.showBar());
         beginDataPresentation();

         String access_level = ctx.readValue("ACCESS_LEVEL");
         appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         //Bug 58541 start
         appendToHTML("<tr><td height=\"260\" width=\"130\" rowspan=\"6\"><img src=\"images/ReleaseDocument.jpg\"></td>\n");
         appendToHTML("<td>&nbsp;&nbsp;&nbsp;</td>");
         appendToHTML("<td colspan=\"2\">");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCESSNOTE1: When releasing a document you must remember to update the access to the document revision.")));
         appendToHTML("<br>\n");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCESSNOTE2: Please take the time to set the desired access.")));
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td>&nbsp;&nbsp;&nbsp;</td>");
         appendToHTML("<td rowspan=\"3\"><img src=\"images/PropertySecurity.jpg\"></td>");
         appendToHTML("<td nowrap>");
         //Bug 58526, Start, Removed text translation
         //Bug 58541 start
         if (docset.countRows()>1)
            appendToHTML(fmt.drawRadio("DOCMAWDOCRELEASEWIZARDACCESSUSER: User", "ACCESS_LEVEL", "USER_ACCESS", "USER_ACCESS".equals(access_level), "OnClick=\"document.form.NEXT.disabled=true;document.form.FINISH.disabled=false;document.form.OVERRIDE_ACCESS.disabled=true\""));
         else

            appendToHTML(fmt.drawRadio("DOCMAWDOCRELEASEWIZARDACCESSUSER: User", "ACCESS_LEVEL", "USER_ACCESS", "USER_ACCESS".equals(access_level), "OnClick=javascript:document.form.NEXT.disabled=true;document.form.FINISH.disabled=false;"));
         //Bug 58541 end
         //Bug 58526, End
         appendToHTML(" - ");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCESSNOTEUSER: Only the document creator has access")));
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td>&nbsp;&nbsp;&nbsp;</td>");
         appendToHTML("<td nowrap>");
         //Bug 58526, Start, Removed text translation
         //Bug 58541 start
         if (docset.countRows()>1)
            appendToHTML(fmt.drawRadio("DOCMAWDOCRELEASEWIZARDACCESSGROUP: Group", "ACCESS_LEVEL", "GROUP_ACCESS", "GROUP_ACCESS".equals(access_level), "OnClick=\"javascript: document.form.OVERRIDE_ACCESS.disabled=false; if(document.form.OVERRIDE_ACCESS.checked){document.form.NEXT.disabled=false;document.form.FINISH.disabled=true;} else{document.form.NEXT.disabled=true;document.form.FINISH.disabled=false;}\""));
         else
            appendToHTML(fmt.drawRadio("DOCMAWDOCRELEASEWIZARDACCESSGROUP: Group", "ACCESS_LEVEL", "GROUP_ACCESS", "GROUP_ACCESS".equals(access_level), "OnClick=javascript:document.form.NEXT.disabled=false;document.form.FINISH.disabled=true;"));
         //Bug 58541 end
         //Bug 58526, End
         appendToHTML(" - ");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCESSNOTEGROUP: Only the persons and groups set in document access (recommended)")));
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td>&nbsp;&nbsp;&nbsp;</td>");
         appendToHTML("<td nowrap>");
         //Bug 58526, Start, Removed text translation
         //Bug 58541 start
         if (docset.countRows()>1)
            appendToHTML(fmt.drawRadio("DOCMAWDOCRELEASEWIZARDACCESSALL: All", "ACCESS_LEVEL", "ALL_ACCESS", "ALL_ACCESS".equals(access_level), "OnClick=\"javascript:document.form.NEXT.disabled=true;document.form.FINISH.disabled=false;document.form.OVERRIDE_ACCESS.disabled=true;\""));
         else
            appendToHTML(fmt.drawRadio("DOCMAWDOCRELEASEWIZARDACCESSALL: All", "ACCESS_LEVEL", "ALL_ACCESS", "ALL_ACCESS".equals(access_level), "OnClick=javascript:document.form.NEXT.disabled=true;document.form.FINISH.disabled=false;"));
         //Bug 58541 end
         //Bug 58526, Start, Removed text translation
         appendToHTML(" - ");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCESSNOTEALL: All users have access to the document")));
         appendToHTML("</td></tr>\n");
         appendToHTML("<tr>\n");
         appendToHTML("<td>&nbsp;&nbsp;&nbsp;</td>");
         appendToHTML("<td colspan=\"2\">");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCESSNOTE3: Note: If you select group acess and haven't defined any document access, the access template for the")));
         appendToHTML("<br>\n");
         appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDACCESSNOTE4: document class will be copied to the document.")));
         appendToHTML("</td></tr>\n");
          //Bug 58541 start
         if (docset.countRows()>1) { //this row is for overriding access rights for multiple docs

            appendToHTML("<tr>\n");
            appendToHTML("<td>&nbsp;&nbsp;&nbsp;</td>");
            appendToHTML("<td colspan=\"2\">");

            String overrideTag = (!"GROUP_ACCESS".equals(access_level)?"disabled ":"");
            overrideTag += "onclick=\"javascript:overrideAccessSettings(this);\"";
         
            appendToHTML(fmt.drawCheckbox("OVERRIDE_ACCESS","","YES".equals(ctx.readValue("OVERRIDE_ACCESS","")),overrideTag));
         
            
            appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDOVERACCRIG: override access rights")));

            appendToHTML("<br>\n");
            appendToHTML(fmt.drawReadValue(mgr.translate("DOCMAWDOCRELEASEWIZARDOVERRIDEWARNING: Warning - When 'Override Access Rights' checkbox is selected access rights of all documents will be replaced by the access rights defined in the next step.")));
            
            appendToHTML("</td></tr>\n");
         }
         //Bug 58541 end

         appendToHTML("</table>\n");
         endDataPresentation(false);

         

         //Bug 58541 start
         // Draw control buttons..
         boolean bEnableNext = false;
         if (docset.countRows()>1){
            
            bEnableNext = "GROUP_ACCESS".equals(access_level)&& "YES".equals(ctx.readValue("OVERRIDE_ACCESS",""));
         }
         else
            bEnableNext = "GROUP_ACCESS".equals(access_level);
         drawButtonPanel(true, null, bEnableNext, null, !bEnableNext, null);
         //Bug 58541 end
      }
      else if ("STEP_5".equals(current_step))
      {
         appendToHTML("<input type=\"hidden\" name=\"GRANTEES\" value=\"\">\n");
         appendToHTML("<input type=\"hidden\" name=\"GRANTEES_VIEW_ACCESS\" value=\"\">\n");
	 //Bug Id 75677, Start
	 appendToHTML("<input type=\"hidden\" name=\"SELECTED_CHARACTERS\" value=\""+mgr.HTMLEncode(ctx.readValue("SELECTED_CHARACTERS", ""))+"\">\n");
	 appendToHTML("<input type=\"hidden\" name=\"SELECTED_CHARACTER\" value=\""+mgr.HTMLEncode(ctx.readValue("SELECTED_CHARACTER", ""))+"\">\n");
	 appendToHTML("<input type=\"hidden\" name=\"PERSON_ID_LIST\" value=\""+mgr.HTMLEncode(ctx.readValue("PERSON_ID_LIST", ""))+"\">\n");
	 appendToHTML("<input type=\"hidden\" name=\"PERSON_NAME_LIST\" value=\""+mgr.HTMLEncode(ctx.readValue("PERSON_NAME_LIST", ""))+"\">\n");
	 appendToHTML("<input type=\"hidden\" name=\"GROUP_ID_LIST\" value=\""+mgr.HTMLEncode(ctx.readValue("GROUP_ID_LIST", ""))+"\">\n");
	 appendToHTML("<input type=\"hidden\" name=\"GROUP_DESCRIPTION_LIST\" value=\""+mgr.HTMLEncode(ctx.readValue("GROUP_DESCRIPTION_LIST", ""))+"\">\n");
	 //Bug Id 75677, End

         accessblk.setTitle(mgr.translate("DOCMAWDOCRELEASEWIZARDCONFIGUREACCESSTITLE: Configure access to group - Step 5 of 5"));
         appendToHTML(accessbar.showBar());
         beginDataPresentation();
         appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         appendToHTML("<tr><td height=\"260\" width=\"130\" rowspan=\"3\"><img src=\"images/ReleaseDocument.jpg\"></td>\n");
         appendToHTML("<td rowspan=\"3\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");

         // Place holder..
         appendToHTML("<td>&nbsp;</td>");
         appendToHTML("<td></td>");
         appendToHTML("<td>&nbsp;</td>");
         appendToHTML("<td rowspan=\"3\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");


         // Draw Available Persons and Groups list box..
	 //Bug Id 75677, Start
         appendToHTML("<tr><td>");
	 //Bug 58526, Start, Removed text translation
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCRELEASEWIZARDAVAILABLEPERSONGROUP: Available Groups and Persons"));
         //Bug 58526, End
         appendToHTML("<br>");
	 String[] Person_group_list =  ctx.readValue("CHARACTER_LIST").split(strObj.valueOf((char)31));
	 Arrays.sort(Person_group_list);
	 
         for (int x = 0; x < Person_group_list.length; x++)
	 {
	     printLink(Person_group_list[x],"javascript:populateByCharacter ('"+mgr.encodeStringForJavascript(Person_group_list[x])+"')");
	     appendToHTML("&nbsp;\n");
	 }
	 appendToHTML("<td align=\"center\" width=\"100%\"></td>");
	 appendToHTML("<td valign=\"top\">");
         //Bug 58526, Start, Removed text translation
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCRELEASEWIZARDPERSONGROUPWITHACCESS: Has Access to the Revision"));
         //Bug 58526, End
	 appendToHTML("</td>");
	 appendToHTML("</tr><tr><td>");
	 //Bug Id 75677, End
         appendToHTML(fmt.drawSelectStart("AVAILABLE_PERSONS_GROUPS", "style=\"width:225px;\" size=10 multiple"));
         appendToHTML(fmt.drawSelectEnd());
         appendToHTML("</td>");

         // Draw add and remove buttons..
         appendToHTML("<td align=\"center\" width=\"100%\">");//Bug Id 75677
         appendToHTML("<table border=\"0\">\n");
	 //Bug Id 75677, Start
         appendToHTML("<tr><td align=\"center\" ><p>");
	 appendToHTML( fmt.drawButton("ADD","&nbsp;&nbsp;&nbsp;" + mgr.translate("DOCMAWDOCRELEASEWIZARDADD: Add")+"&nbsp;&nbsp;&nbsp;","onClick='addPersonGroup()'"));
	 appendToHTML("</p></td></tr>\n");
         appendToHTML("<tr><td>&nbsp;</td></tr>\n");
         appendToHTML("<tr><td>&nbsp;</td></tr>\n");
         appendToHTML("<tr><td align=\"center\" ><p>");
	 appendToHTML(fmt.drawButton("REMOVE",mgr.translate("DOCMAWDOCRELEASEWIZARDREMOVE: Remove"),"onClick='removePersonGroup()'"));
	 appendToHTML("</p></td></tr>\n");
	 //Bug Id 75677, End         
	 appendToHTML("</table>\n");
         appendToHTML("</td>\n");

         // Draw list box for Persons and Groups with access..
         appendToHTML("<td nowrap>");
         appendToHTML(fmt.drawSelectStart("GRANTED_PERSONS_GROUPS", "style=\"width:225px;\" size=10 multiple onChange=\"setCurrentViewAccess()\""));

         ASPBuffer person_access_buf = ctx.readBuffer("DOCUMENT_ACCESS_PERSONS");
         ASPBuffer group_access_buf = ctx.readBuffer("DOCUMENT_ACCESS_GROUPS");

         appendDirtyJavaScript("var view_access = new Array(" + (person_access_buf.countItems() + group_access_buf.countItems()) + ");\n");
         appendDirtyJavaScript("var entity_count = 0;\n");

         // list of persons who currently have access to the document revision..
         int count = person_access_buf.countItems();
         for (int x = 0; x < count; x++)
         {
            ASPBuffer data = person_access_buf.getBufferAt(x);
            appendToHTML(fmt.drawSelectOption(data.getValue("NAME") + " <" + data.getValue("PERSON_ID")+ ">", "PERSON_" + data.getValue("PERSON_ID"), (x == 0))); //Bug Id 78998
            appendDirtyJavaScript("view_access[entity_count] = new Array(2);\n");
            appendDirtyJavaScript("view_access[entity_count][0] = \"PERSON_" + data.getValue("PERSON_ID") + "\";\n");
            appendDirtyJavaScript("view_access[entity_count++][1] = \"" + data.getValue("VIEW_ACCESS") + "\";\n");
         }

         // list of groups who currently have access to the document revision..
         count = group_access_buf.countItems();
         for (int x = 0; x < count; x++)
         {
            ASPBuffer data = group_access_buf.getBufferAt(x);
            appendToHTML(fmt.drawSelectOption(data.getValue("GROUP_DESCRIPTION"), "GROUP_" + data.getValue("GROUP_ID"), (x == 0)));
            appendDirtyJavaScript("view_access[entity_count] = new Array(2);\n");
            appendDirtyJavaScript("view_access[entity_count][0] = \"GROUP_" + data.getValue("GROUP_ID") + "\";\n");
            appendDirtyJavaScript("view_access[entity_count++][1] = \"" + data.getValue("VIEW_ACCESS") + "\";\n");
         }

          
         appendToHTML(fmt.drawSelectEnd());
	 appendDirtyJavaScript("clearSelections(document.form.GRANTED_PERSONS_GROUPS);\n");//Bug Id 75677
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td></td><td></td><td>");//Bug Id 75677
         appendToHTML(fmt.drawCheckbox("SHOW_GROUPS", "YES", "YES".equals(ctx.readValue("SHOW_GROUPS")), "onClick=javascript:populateSelectBox()"));//Bug Id 75677
         //Bug 58526, Start
         appendToHTML(fmt.drawReadLabel("DOCMAWDOCRELEASEWIZARDSHOWGROUPS: Show Groups"));
         //Bug 58526, End
         appendToHTML("<br>\n");
         appendToHTML(fmt.drawCheckbox("SHOW_PERSONS", "YES", "YES".equals(ctx.readValue("SHOW_PERSONS")), "onClick=javascript:populateSelectBox()"));//Bug Id 75677
         //Bug 58526, Start
         appendToHTML(fmt.drawReadLabel("DOCMAWDOCRELEASEWIZARDSHOWUSERS: Show Users"));
         //Bug 58526, End
         appendToHTML("</td>\n");
         appendToHTML("<td></td>\n");
         appendToHTML("<td>\n");
         appendToHTML(fmt.drawCheckbox("VIEW", "", (mgr.HTMLEncode(mgr.readValue("CHECKVIEW")) == "false" ? false : true), "onClick='updateViewAccess()'"));
         //Bug 58526, Start
         appendToHTML(fmt.drawReadLabel("DOCMAWDOCRELEASEWIZARDVIEWACCESS: View Access"));
         //Bug 58526, End
         appendToHTML("<br>\n");
         appendToHTML("&nbsp;");
         appendToHTML("</td>\n");

         appendToHTML("</tr>\n");
         appendToHTML("</table>\n");
         endDataPresentation(false);

         // Draw control buttons..
         drawButtonPanel(true, "savePersonsAndGroups()", false, null, true, "savePersonsAndGroups()");//Bug Id 75677

	 //Bug Id 75677, Start
	 appendDirtyJavaScript("populateSelectBox();\n");
	 appendDirtyJavaScript("function populateByCharacter (selected_character) {\n");
	 appendDirtyJavaScript("   if (document.form.SELECTED_CHARACTERS.value.indexOf(selected_character+ String.fromCharCode(31))==-1) {\n");
	 appendDirtyJavaScript("      var ret = __connect(\n");
	 appendDirtyJavaScript("        '");
	 appendDirtyJavaScript(mgr.getURL());
	 appendDirtyJavaScript("?execute=populateUserGroups'\n");
	 appendDirtyJavaScript("        + '&SELECTED_CHAR=' + URLClientEncode(selected_character)\n");
	 appendDirtyJavaScript("        );\n");
	 appendDirtyJavaScript("      document.form.SELECTED_CHARACTERS.value += selected_character+ String.fromCharCode(31);\n");
	 appendDirtyJavaScript("    var personGroupArray;\n");
	 appendDirtyJavaScript("    var personArray;\n");
	 appendDirtyJavaScript("    var groupArray;\n");
	 appendDirtyJavaScript("    if (ret.length>0){ \n");
	 appendDirtyJavaScript("       ret = ret.substr(1,ret.length) \n");
	 appendDirtyJavaScript("       personGroupArray = ret.split(String.fromCharCode(29));\n");
	 appendDirtyJavaScript("       personArray = personGroupArray[0].split(String.fromCharCode(30));\n");
	 appendDirtyJavaScript("       groupArray = personGroupArray[1].split(String.fromCharCode(30));\n");
	 appendDirtyJavaScript("    } \n");
	 appendDirtyJavaScript("    if (personArray[0] != 'null'){ \n");
	 appendDirtyJavaScript("      document.form.PERSON_ID_LIST.value +=  personArray[0];\n");
	 appendDirtyJavaScript("    }\n");
	 appendDirtyJavaScript("    if (personArray[1] != 'null') \n");
	 appendDirtyJavaScript("      document.form.PERSON_NAME_LIST.value += personArray[1];\n");
	 appendDirtyJavaScript("    if (groupArray[0] != 'null') \n");
	 appendDirtyJavaScript("      document.form.GROUP_ID_LIST.value +=  groupArray[0];\n");
	 appendDirtyJavaScript("    if (groupArray[1] != 'null')\n");
	 appendDirtyJavaScript("      document.form.GROUP_DESCRIPTION_LIST.value += groupArray[1];\n");
	 appendDirtyJavaScript("   }\n");
	 appendDirtyJavaScript("   document.form.SELECTED_CHARACTER.value = selected_character;\n");
	 appendDirtyJavaScript("   populateSelectBox();\n");
	 appendDirtyJavaScript("}\n");

	 appendDirtyJavaScript("function populateSelectBox() {\n");
	 appendDirtyJavaScript("  var available = document.form.AVAILABLE_PERSONS_GROUPS;\n");
	 appendDirtyJavaScript("  for (; 0<available.length;) {\n");
	 appendDirtyJavaScript("       available.options[available.length-1] = null;\n");
	 appendDirtyJavaScript("  }\n");
	 appendDirtyJavaScript("  var selected_character = document.form.SELECTED_CHARACTER.value;\n");
	 appendDirtyJavaScript("  //Insert persons\n");
	 appendDirtyJavaScript("  if (document.form.SHOW_PERSONS.checked == true && document.form.PERSON_ID_LIST.value != \"\") {\n");
	 appendDirtyJavaScript("     var personIdList = document.form.PERSON_ID_LIST.value;\n");
	 appendDirtyJavaScript("     var personNameList = document.form.PERSON_NAME_LIST.value;\n");
	 appendDirtyJavaScript("     var personIdArray = personIdList.split(String.fromCharCode(31));\n");
	 appendDirtyJavaScript("     var personNameArray = personNameList.split(String.fromCharCode(31));\n");
	 appendDirtyJavaScript("     for (i=0; i<personIdArray.length; i++) {\n");
	 appendDirtyJavaScript("        personId = personIdArray[i];\n");
	 appendDirtyJavaScript("        personName = personNameArray[i];\n");
	 appendDirtyJavaScript("        if (personName.substr(0,1).toUpperCase()== selected_character) {\n");
	 appendDirtyJavaScript("           insertOption(available, 'PERSON_' +personId, personName + ' <'+ personId + '>', false);\n");//Bug Id 75677 //Bug Id 78998
	 appendDirtyJavaScript("       }\n");
	 appendDirtyJavaScript("     }\n");
	 appendDirtyJavaScript("  }\n");
	 appendDirtyJavaScript("  //Insert groups\n");
	 appendDirtyJavaScript("  if (document.form.SHOW_GROUPS.checked == true && document.form.GROUP_ID_LIST.value != \"\") {\n");
	 appendDirtyJavaScript("     var groupIdList = document.form.GROUP_ID_LIST.value;\n");
	 appendDirtyJavaScript("     var groupDesList = document.form.GROUP_DESCRIPTION_LIST.value;\n");
	 appendDirtyJavaScript("     var groupIdArray = groupIdList.split(String.fromCharCode(31));\n");
	 appendDirtyJavaScript("     var groupDesArray = groupDesList.split(String.fromCharCode(31));\n");
	 appendDirtyJavaScript("     groupIdArray.sort();\n");
	 appendDirtyJavaScript("     groupDesArray.sort();\n");
	 appendDirtyJavaScript("     for (i=0; i<groupIdArray.length; i++) {\n");
	 appendDirtyJavaScript("        groupId = groupIdArray[i];\n");
	 appendDirtyJavaScript("        groupDesc = groupDesArray[i];\n");
	 appendDirtyJavaScript("        if (groupDesc.substr(0,1).toUpperCase()== selected_character) {\n");
	 appendDirtyJavaScript("           insertOption(available, 'GROUP_' + groupId, groupDesc,false);\n");//Bug Id 75677
	 appendDirtyJavaScript("        }\n");
	 appendDirtyJavaScript("     }\n");
	 appendDirtyJavaScript("  }\n");
	 appendDirtyJavaScript("}\n");
	 //Bug Id 75677, End
      }

      //Bug Id 70808, Start
      if (close_wizard)
      {
         if (bPerformBackgroundJob) {
            appendDirtyJavaScript("  window.opener.document.form.CONFIRM.value='OK';\n");
            appendDirtyJavaScript("  window.opener.document.form.BGJ_CONFIRMED.value='OK';\n");
            appendDirtyJavaScript("  eval(\"opener.submitParent()\");\n");
            appendDirtyJavaScript("  window.close();\n");
         }
         else
         {
         appendDirtyJavaScript("  eval(\"opener.refreshParentRowsSelected()\");\n");
         appendDirtyJavaScript("  window.close();\n");
      }
      }
      //Bug Id 70808, End

      appendDirtyJavaScript("var message = \"" + (mgr.isEmpty(message) ? "" : message) + "\";\n");
      appendDirtyJavaScript("var client_action = \"" + (mgr.isEmpty(client_action) ? "" : client_action) + "\";\n");
      appendDirtyJavaScript("var server_command = \"" + (mgr.isEmpty(server_command) ? "" : server_command) + "\";\n");
      appendDirtyJavaScript("var client_command = \"" + (mgr.isEmpty(client_command) ? "" : client_command) + "\";\n");


      appendDirtyJavaScript("initWizard();\n");
      appendDirtyJavaScript("function initWizard()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (client_action == \"SHOW_ALERT\")\n");
      appendDirtyJavaScript("       showAlert(message, client_command);\n");
      appendDirtyJavaScript("   if (client_action == \"CLOSE_WINDOW\")\n");
      appendDirtyJavaScript("       window.close();\n");
      appendDirtyJavaScript("   if (client_action == \"GET_CONFIRMATION\")\n");
      appendDirtyJavaScript("       showConfirmation(message, server_command, client_command, true);\n");
      appendDirtyJavaScript("}\n");

      //Bug Id 69329, start
      if ("STEP_3_2".equals(current_step) )
      {
         appendDirtyJavaScript("function setRadioButton(condition)\n");
         appendDirtyJavaScript("{ // selects or unselects radio buttons depending on condition\n");
         appendDirtyJavaScript("   if(condition == \"DEFAULT\")\n");
         appendDirtyJavaScript("   {\n");
	 appendDirtyJavaScript("      document.form.DEFAULT.checked=true;\n");
         appendDirtyJavaScript("      document.form.MOVEALL.checked=false;\n");
         appendDirtyJavaScript("      document.form.NOMOVE.checked=false;\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else if(condition == \"MOVEALL\")\n");
         appendDirtyJavaScript("   {\n");
	 appendDirtyJavaScript("      document.form.MOVEALL.checked=true;\n");
         appendDirtyJavaScript("      document.form.DEFAULT.checked=false;\n");
         appendDirtyJavaScript("      document.form.NOMOVE.checked=false;\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else if(condition == \"NOMOVE\")\n");
         appendDirtyJavaScript("   {\n");
	 appendDirtyJavaScript("      document.form.NOMOVE.checked=true;\n");
         appendDirtyJavaScript("      document.form.DEFAULT.checked=false;\n");
         appendDirtyJavaScript("      document.form.MOVEALL.checked=false;\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript(" }\n");
      }
      //Bug Id 69329, end

   }
}
