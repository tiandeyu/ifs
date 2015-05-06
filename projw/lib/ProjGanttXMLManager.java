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
*  File        : ProjGanttXMLManager.java 
*  History     :
*  ARWILK  2007-09-19  Created by Moving some functions from XMLManager.
*                      (createSubProjectEntitySet, createActivityEntitySet, createLinkEntitySet, 
*                       createSubProjectEntity, createActivityEntity, createLinkEntity, 
*                       readSubProjectRowSet, readActivityRowSet, readLinkRowSet, addActivityRowSet, 
*                       appendActivityModifications, appendLinkModifications)
* -----------------------APP7.5 SP1-------------------------------------------
*  ARWILK  2007-12-31  Bug 69760(Re-write for Oracle App SRV), Modified to support rowset based data structure (for applet communication).
*                      Added new methdos createSubProjectRowSet, createActivityRowSet and createLinkRowSet. 
* ----------------------------------------------------------------------------
*/  

package ifs.projw.lib;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.buffer.*; 

import java.util.Date;
import java.util.List;
//(+) Bug 69760, Start
import java.text.DateFormat;
import java.text.NumberFormat;
//(+) Bug 69760, End

//(+) Bug 69760, Start
import javax.xml.xpath.*;
//(+) Bug 69760, End
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

//(-) Bug 69760, Start
//import de.netronic.jgantt.JGantt;
//import de.netronic.common.intface.NeIEntity;
//import de.netronic.common.intface.NeIEntitySet;
//import de.netronic.common.intface.NeIAppData;
//(-) Bug 69760, End

public class ProjGanttXMLManager extends XmlManager
{
   //(+) Bug 69760, Start
   private static final String DIRTY_FLAG_NAME = "___entity_status_flag"; // Accepts REMOVED/MODIFIED/NEW.
   private static final String OBJ_VERSION = "objversion"; // Is mapped to the objversion of the framework.
   private static final String OBJ_ID = "objid"; // Is mapped to the objid of the framework.
   private static final String ENTITY_ID = "___entity_id"; // Used to decide on groups.

   private ASPRowSet rsSubProject;
   private ASPRowSet rsActivity;
   private ASPRowSet rsLinks; 

   private Element entitySetElement;
   private Element entityElement;
   private Element rowSetElement;
   private Element rowElement;

   private static final String SUB_PROJECT_SET_NAME = "sub_project_set";
   private static final String ACTIVITY_SET_NAME = "activity_set";
   private static final String LINK_SET_NAME = "link_set";
   //(+) Bug 69760, End

   public ProjGanttXMLManager(ASPManager mgr) throws ParserConfigurationException, TransformerConfigurationException
   {
      super(mgr);
   }

   //(+) Bug 69760, Start
   private void createSubProjectRowSet()
   {
      // Create Rowset
      rowSetElement  = xmlOutputDoc.createElement(ROW_SET_ELEMENT);
      rowSetElement.setAttribute("Name", SUB_PROJECT_SET_NAME);
      rowSetsElement.appendChild(rowSetElement);
   }

   private void createActivityRowSet()
   {
      // Create Rowset
      rowSetElement  = xmlOutputDoc.createElement(ROW_SET_ELEMENT);
      rowSetElement.setAttribute("Name", ACTIVITY_SET_NAME);
      rowSetsElement.appendChild(rowSetElement);
   }

   private void createLinkRowSet()
   {
      // Create Rowset
      rowSetElement  = xmlOutputDoc.createElement(ROW_SET_ELEMENT);
      rowSetElement.setAttribute("Name", LINK_SET_NAME);
      rowSetsElement.appendChild(rowSetElement);
   }
   //(+) Bug 69760, End

   public void createSubProjectEntitySet()
   {
      //(-) Bug 69760, Start
      //esSubProj = appData.createEntitySet("sub_project_set");
      //esSubProj.addEntityAttribute("project_id", String.class, "Project ID");
      //esSubProj.addEntityAttribute("sub_project_id", String.class, "Sub Project ID");
      //esSubProj.addEntityAttribute("sub_project_desc", String.class, "Sub Project Description");
      //esSubProj.addEntityAttribute("sub_project_annotation", String.class, "Sub Project Annotation");
      //esSubProj.addEntityAttribute("early_start", Date.class, "Early Start");
      //esSubProj.addEntityAttribute("early_finish", Date.class, "Early Finish");
      //(-) Bug 69760, End

      //(+) Bug 69760, Start
      entitySetElement  = xmlOutputDoc.createElement(ENTITY_SET_ELEMENT);
      entitySetElement.setAttribute("Name", SUB_PROJECT_SET_NAME);
      entitySetsElement.appendChild(entitySetElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", ENTITY_ID);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", DIRTY_FLAG_NAME);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", OBJ_ID);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", OBJ_VERSION);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement); 

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "project_id");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Project ID");
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "sub_project_id");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Sub Project ID");
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "sub_project_desc");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Sub Project Description");
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "sub_project_annotation");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Sub Project Annotation");
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "early_start");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Early Start");
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "early_finish");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Early Finish");  
      entitySetElement.appendChild(entityElement);
      //(+) Bug 69760, End
   }

   public void createActivityEntitySet()
   {
      //(-) Bug 69760, Start
      //esActivity = appData.createEntitySet("activity_set");
      //esActivity.addEntityAttribute("project_id", String.class, "Project ID");
      //esActivity.addEntityAttribute("sub_project_id", String.class, mgr.translate("XMLMANSUBPROJID: Sub Project ID"));
      //esActivity.addEntityAttribute("sub_project_desc", String.class, mgr.translate("XMLMANSUBPROJDESC: Sub Project Description"));
      //esActivity.addEntityAttribute("activity_no", String.class, mgr.translate("XMLMANACTINFO: Activity No"));
      //esActivity.addEntityAttribute("activity_desc", String.class, mgr.translate("XMLMANACTIDESC: Activity Description"));
      //esActivity.addEntityAttribute("activity_annotation", String.class, "Activity Annotation");
      //esActivity.addEntityAttribute("early_start", Date.class, "Early Start");
      //esActivity.addEntityAttribute("early_finish", Date.class, "Early Finish");
      //esActivity.addEntityAttribute("actual_start", Date.class, "Actual Start");
      //esActivity.addEntityAttribute("actual_finish", Date.class, "Actual Finish");
      //esActivity.addEntityAttribute("late_start", Date.class, "Late Start");
      //esActivity.addEntityAttribute("late_finish", Date.class, "Late Finish");
      //esActivity.addEntityAttribute("need_date", Date.class, "Need Date");
      //esActivity.addEntityAttribute("duration", Double.class, "Duration");      
      //esActivity.addEntityAttribute("total_float", Double.class, "Total Float");
      //esActivity.addEntityAttribute("free_float", Double.class, "Free Float");
      //esActivity.addEntityAttribute("progress_hours_weighted", String.class, "Progress Hours Weighted");
      //esActivity.addEntityAttribute("total_work_days", Double.class, "Total Work Days"); 
      //esActivity.addEntityAttribute("object_identification", String.class, "Object Identification");
      //esActivity.addEntityAttribute("exceptions_exist", Integer.class, "Exceptions Exist");
      //esActivity.addEntityAttribute("has_connected_wo", Integer.class, "Has Connected WO");
      //esActivity.addEntityAttribute("has_connected_so", Integer.class, "Has Connected SO");
      //esActivity.addEntityAttribute("has_connected_po", Integer.class, "Has Connected PO");
      //esActivity.addEntityAttribute("has_connected_dop", Integer.class, "Has Connected DOP");
      //
      //// Attributes for Framework Functionality
      //esActivity.addEntityAttribute(DIRTY_FLAG_NAME, String.class, "");
      //esActivity.addEntityAttribute(OBJ_ID, String.class, "");
      //esActivity.addEntityAttribute(OBJ_VERSION, String.class, "");
      //(-) Bug 69760, End

      //(+) Bug 69760, Start
      entitySetElement  = xmlOutputDoc.createElement(ENTITY_SET_ELEMENT);
      entitySetElement.setAttribute("Name", ACTIVITY_SET_NAME);
      entitySetsElement.appendChild(entitySetElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", ENTITY_ID);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", DIRTY_FLAG_NAME);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", OBJ_ID);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", OBJ_VERSION);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement); 

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "project_id");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Project ID");
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "sub_project_id");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", mgr.translate("XMLMANSUBPROJID: Sub Project ID"));
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "sub_project_desc");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", mgr.translate("XMLMANSUBPROJDESC: Sub Project Description"));
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "activity_no");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", mgr.translate("XMLMANACTINFO: Activity No"));
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "activity_desc");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", mgr.translate("XMLMANACTIDESC: Activity Description"));
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "activity_annotation");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", mgr.translate("XMLMANACTIVANNOT: Activity Annotation"));
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "early_start");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Early Start");
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "early_finish");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Early Finish");
      entitySetElement.appendChild(entityElement);

      entityElement  = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "actual_start");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Actual Start");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "actual_finish");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Actual Finish");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "late_start");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Late Start");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "late_finish");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Late Finish");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "need_date");
      entityElement.setAttribute("DataType", "Date");
      entityElement.setAttribute("Title", "Need Date");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "duration");
      entityElement.setAttribute("DataType", "Double");
      entityElement.setAttribute("Title", "Duration");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "total_float");
      entityElement.setAttribute("DataType", "Double");
      entityElement.setAttribute("Title", "Total Float");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "free_float");
      entityElement.setAttribute("DataType", "Double");
      entityElement.setAttribute("Title", "Free Float");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "progress_hours_weighted");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Progress Hours Weighted");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "total_work_days");
      entityElement.setAttribute("DataType", "Double");
      entityElement.setAttribute("Title", "Total Work Days");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "object_identification");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Object Identification");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "exceptions_exist");
      entityElement.setAttribute("DataType", "Integer");
      entityElement.setAttribute("Title", "Exceptions Exist");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "has_connected_wo");
      entityElement.setAttribute("DataType", "Integer");
      entityElement.setAttribute("Title", "Has Connected WO");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "has_connected_so");
      entityElement.setAttribute("DataType", "Integer");
      entityElement.setAttribute("Title", "Has Connected SO");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "has_connected_po");
      entityElement.setAttribute("DataType", "Integer");
      entityElement.setAttribute("Title", "Has Connected PO");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "has_connected_dop");
      entityElement.setAttribute("DataType", "Integer");
      entityElement.setAttribute("Title", "Has Connected DOP");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "activity_seq");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Activity Seq");
      entitySetElement.appendChild(entityElement);
      //(+) Bug 69760, End
   }

   public void createLinkEntitySet()
   {
      //(-) Bug 69760, Start
      //esLinks = appData.createEntitySet("link_set");
      //
      //// Generating attributes for Source Node, Target Node and Link Type
      //esLinks.addEntityAttribute("activity_seq", String.class, "Source Node");
      //esLinks.addEntityAttribute("activity_relation_seq", String.class, "Target Node");
      //esLinks.addEntityAttribute("dependancy_type_db", String.class, "Type of Link");
      //
      //// Attributes for Framework Functionality
      //esLinks.addEntityAttribute(DIRTY_FLAG_NAME, String.class, "");
      //esLinks.addEntityAttribute(OBJ_ID, String.class, "");
      //esLinks.addEntityAttribute(OBJ_VERSION, String.class, "");
      //(-) Bug 69760, End

      //(+) Bug 69760, Start
      entitySetElement = xmlOutputDoc.createElement(ENTITY_SET_ELEMENT);
      entitySetElement.setAttribute("Name", LINK_SET_NAME);
      entitySetsElement.appendChild(entitySetElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", ENTITY_ID);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", DIRTY_FLAG_NAME);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", OBJ_ID);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement);

      // Entity for Framework Functionality 
      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", OBJ_VERSION);
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "");
      entitySetElement.appendChild(entityElement); 

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "activity_seq");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Source Node");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "activity_relation_seq");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Target Node");
      entitySetElement.appendChild(entityElement);

      entityElement = xmlOutputDoc.createElement(ENTITY_ELEMENT);
      entityElement.setAttribute("Name", "dependancy_type_db");
      entityElement.setAttribute("DataType", "String");
      entityElement.setAttribute("Title", "Type of Link");
      entitySetElement.appendChild(entityElement);
      //(+) Bug 69760, End
   }   

   //(-/+) Bug 69760, Start
   //private void createSubProjectEntity(String project_id, String sub_project_id, String sub_project_desc, String sub_project_annotation, Date early_start, Date early_finish)
   private void createSubProjectEntity(String project_id, String sub_project_id, String sub_project_desc, String sub_project_annotation, Date early_start, Date early_finish) throws XPathExpressionException
   //(-/+) Bug 69760, End
   {
      //(-) Bug 69760, Start
      //if (esSubProj == null)
      //   createSubProjectEntitySet();

      //NeIEntity entity = esSubProj.createEntity(escapeForXML(sub_project_id));
      //entity.setValue("project_id", escapeForXML(project_id));
      //entity.setValue("sub_project_id", escapeForXML(sub_project_id));
      //entity.setValue("sub_project_desc", escapeForXML(sub_project_desc));
      //entity.setValue("sub_project_annotation", escapeForXML(sub_project_annotation));
      //entity.setValue("early_start", early_start);
      //entity.setValue("early_finish", early_finish);
      //(-) Bug 69760, End

      //(+) Bug 69760, Start
      Element rowsetNodeElement;
      Element rowNodeElement;

      rowsetNodeElement = (Element) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ROW_SETS_ELEMENT + "/" + ROW_SET_ELEMENT + "[@Name='" + SUB_PROJECT_SET_NAME + "']", xmlOutputDoc, XPathConstants.NODE);
      if (rowsetNodeElement == null)
      {
         createSubProjectRowSet();
         rowsetNodeElement = (Element) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ROW_SETS_ELEMENT + "/" + ROW_SET_ELEMENT + "[@Name='" + SUB_PROJECT_SET_NAME + "']", xmlOutputDoc, XPathConstants.NODE);
      }

      rowNodeElement = xmlOutputDoc.createElement(ROW_ELEMENT); 
      rowNodeElement.setAttribute(ENTITY_ID, escapeForXML(sub_project_id));
      rowNodeElement.setAttribute(DIRTY_FLAG_NAME, "");
      rowNodeElement.setAttribute(OBJ_ID, "");
      rowNodeElement.setAttribute(OBJ_VERSION, "");
      rowNodeElement.setAttribute("project_id", escapeForXML(project_id));
      rowNodeElement.setAttribute("sub_project_id", escapeForXML(sub_project_id));
      rowNodeElement.setAttribute("sub_project_desc", escapeForXML(sub_project_desc));
      rowNodeElement.setAttribute("sub_project_annotation", escapeForXML(sub_project_annotation));
      rowNodeElement.setAttribute("early_start", escapeForXML(early_start));
      rowNodeElement.setAttribute("early_finish", escapeForXML(early_finish));   
      rowsetNodeElement.appendChild(rowNodeElement);   
      //(+) Bug 69760, End
   }

   //(-/+) Bug 69760, Start
   //private void createActivityEntity(long activity_seq, String project_id, String sub_project_id, String sub_project_desc, String activity_no, String activity_desc, String activity_annotation,
   //                                  Date early_start, Date early_finish, Date actual_start, Date actual_finish, Date late_start, Date late_finish, Date need_date, Double duration, Double total_float, Double free_float, Double progress_hours_weighted, 
   //                                  Double total_work_days, String object_identification, int exceptions_exist, int has_connected_wo, int has_connected_so, int has_connected_po, int has_connected_dop, String objid, String objversion)
   private void createActivityEntity(String obj_id, String obj_version, long activity_seq, String project_id, String sub_project_id, String sub_project_desc, String activity_no, String activity_desc, String activity_annotation,
                                     Date early_start, Date early_finish, Date actual_start, Date actual_finish, Date late_start, Date late_finish, Date need_date, double duration, double total_float, double free_float, double progress_hours_weighted, 
                                     double total_work_days, String object_identification, int exceptions_exist, int has_connected_wo, int has_connected_so, int has_connected_po, int has_connected_dop, String objid, String objversion) throws XPathExpressionException
   //(-/+) Bug 69760, End
   {
      //(-) Bug 69760, Start
      //if (esActivity == null)
      //   createActivityEntitySet();
      //
      //NeIEntity entity = esActivity.createEntity(Long.toString(activity_seq));
      //entity.setValue("project_id", escapeForXML(project_id));
      //entity.setValue("sub_project_id", escapeForXML(sub_project_id));    
      //entity.setValue("sub_project_desc", escapeForXML(sub_project_desc));
      //
      //entity.setValue("activity_no", escapeForXML(activity_no));
      //entity.setValue("activity_desc", escapeForXML(activity_desc));        
      //entity.setValue("activity_annotation", escapeForXML(activity_annotation));
      //
      //entity.setValue("early_start", early_start);  
      //entity.setValue("early_finish", early_finish);    
      //entity.setValue("actual_start", actual_start);  
      //
      //entity.setValue("actual_finish", actual_finish);
      //entity.setValue("late_start", late_start);  
      //entity.setValue("late_finish", late_finish);
      //
      //entity.setValue("need_date", need_date);
      //entity.setValue("duration", duration);  
      //entity.setValue("total_float", total_float);
      //
      //entity.setValue("free_float", free_float);
      //entity.setValue("progress_hours_weighted", progress_hours_weighted.toString());
      //entity.setValue("total_work_days", total_work_days);
      //
      //entity.setValue("object_identification", escapeForXML(object_identification));      
      //entity.setValue("exceptions_exist", exceptions_exist);
      //entity.setValue("has_connected_wo", has_connected_wo);
      //
      //entity.setValue("has_connected_so", has_connected_so);
      //entity.setValue("has_connected_po", has_connected_po);
      //entity.setValue("has_connected_dop", has_connected_dop);  
      //
      //entity.setValue(DIRTY_FLAG_NAME, "");
      //entity.setValue(OBJ_ID, escapeForXML(objid));
      //entity.setValue(OBJ_VERSION, escapeForXML(objversion));
      //(-) Bug 69760, End

      //(+) Bug 69760, Start
      Element rowsetNodeElement;
      Element rowNodeElement;

      rowsetNodeElement = (Element) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ROW_SETS_ELEMENT + "/" + ROW_SET_ELEMENT + "[@Name='" + ACTIVITY_SET_NAME + "']", xmlOutputDoc, XPathConstants.NODE);
      if (rowsetNodeElement == null)
      {
         createActivityRowSet();
         rowsetNodeElement = (Element) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ROW_SETS_ELEMENT + "/" + ROW_SET_ELEMENT + "[@Name='" + ACTIVITY_SET_NAME + "']", xmlOutputDoc, XPathConstants.NODE);
      }

      rowNodeElement = xmlOutputDoc.createElement(ROW_ELEMENT);
      rowNodeElement.setAttribute(ENTITY_ID, escapeForXML(Long.toString(activity_seq)));
      rowNodeElement.setAttribute(DIRTY_FLAG_NAME, "");
      rowNodeElement.setAttribute(OBJ_ID, escapeForXML(obj_id));
      rowNodeElement.setAttribute(OBJ_VERSION, escapeForXML(obj_version));
      rowNodeElement.setAttribute("project_id", escapeForXML(project_id));
      rowNodeElement.setAttribute("sub_project_id", escapeForXML(sub_project_id));
      rowNodeElement.setAttribute("sub_project_desc", escapeForXML(sub_project_desc)); 
      rowNodeElement.setAttribute("activity_no", escapeForXML(activity_no));
      rowNodeElement.setAttribute("activity_desc", escapeForXML(activity_desc));
      rowNodeElement.setAttribute("activity_annotation", escapeForXML(activity_annotation)); 
      rowNodeElement.setAttribute("early_start", escapeForXML(early_start));
      rowNodeElement.setAttribute("early_finish", escapeForXML(early_finish));
      rowNodeElement.setAttribute("actual_start", escapeForXML(actual_start));  
      rowNodeElement.setAttribute("actual_finish", escapeForXML(actual_finish));
      rowNodeElement.setAttribute("late_start", escapeForXML(late_start));
      rowNodeElement.setAttribute("late_finish", escapeForXML(late_finish));  
      rowNodeElement.setAttribute("need_date", escapeForXML(need_date));
      rowNodeElement.setAttribute("duration", escapeForXML(duration));
      rowNodeElement.setAttribute("total_float", escapeForXML(total_float));      
      rowNodeElement.setAttribute("free_float", escapeForXML(free_float));
      rowNodeElement.setAttribute("progress_hours_weighted", escapeForXML(progress_hours_weighted));
      rowNodeElement.setAttribute("total_work_days", escapeForXML(total_work_days)); 
      rowNodeElement.setAttribute("object_identification", escapeForXML(object_identification));
      rowNodeElement.setAttribute("exceptions_exist", escapeForXML(exceptions_exist));
      rowNodeElement.setAttribute("has_connected_wo", escapeForXML(has_connected_wo));
      rowNodeElement.setAttribute("has_connected_so", escapeForXML(has_connected_so));
      rowNodeElement.setAttribute("has_connected_po", escapeForXML(has_connected_po));
      rowNodeElement.setAttribute("has_connected_dop", escapeForXML(has_connected_dop));  
      rowNodeElement.setAttribute("activity_seq", escapeForXML(Long.toString(activity_seq)));  
      rowsetNodeElement.appendChild(rowNodeElement);   
      //(+) Bug 69760, End
   }

   //(-/+) Bug 69760, Start
   //private void createLinkEntity(long activity_seq, long activity_relation_seq, String dependancy_type_db, String objid, String objversion)
   private void createLinkEntity(String obj_id, String obj_version, long activity_seq, long activity_relation_seq, String dependancy_type_db, String objid, String objversion) throws XPathExpressionException
   //(-/+) Bug 69760, End
   {
      //(-) Bug 69760, Start
      //if (esLinks == null)
      //   createLinkEntitySet();

      //NeIEntity entity = esLinks.createEntity("");
      //entity.setValue("activity_seq", Long.toString(activity_seq));
      //entity.setValue("activity_relation_seq", Long.toString(activity_relation_seq));
      //entity.setValue("dependancy_type_db", escapeForXML(dependancy_type_db));

      //entity.setUserID(Long.toString(activity_seq) + escapeForXML(dependancy_type_db) + Long.toString(activity_relation_seq));
      //entity.setValue(DIRTY_FLAG_NAME, ""); 
      //entity.setValue(OBJ_ID, escapeForXML(objid));   
      //entity.setValue(OBJ_VERSION, escapeForXML(objversion));
      //(-) Bug 69760, End

      //(+) Bug 69760, Start
      Element rowsetNodeElement;
      Element rowNodeElement;

      rowsetNodeElement = (Element) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ROW_SETS_ELEMENT + "/" + ROW_SET_ELEMENT + "[@Name='" + LINK_SET_NAME + "']", xmlOutputDoc, XPathConstants.NODE);
      if (rowsetNodeElement == null)
      {
         createLinkRowSet();
         rowsetNodeElement = (Element) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ROW_SETS_ELEMENT + "/" + ROW_SET_ELEMENT + "[@Name='" + LINK_SET_NAME + "']", xmlOutputDoc, XPathConstants.NODE);
      }

      rowNodeElement = xmlOutputDoc.createElement(ROW_ELEMENT); 
      rowNodeElement.setAttribute(ENTITY_ID, escapeForXML(Long.toString(activity_seq) + dependancy_type_db + Long.toString(activity_relation_seq)));
      rowNodeElement.setAttribute(DIRTY_FLAG_NAME, "");
      rowNodeElement.setAttribute(OBJ_ID, escapeForXML(obj_id));
      rowNodeElement.setAttribute(OBJ_VERSION, escapeForXML(obj_version));
      rowNodeElement.setAttribute("activity_seq",escapeForXML(Long.toString(activity_seq))); 
      rowNodeElement.setAttribute("activity_relation_seq", escapeForXML(Long.toString(activity_relation_seq)));
      rowNodeElement.setAttribute("dependancy_type_db", escapeForXML(dependancy_type_db));
      rowsetNodeElement.appendChild(rowNodeElement);         
      //(+) Bug 69760, End
   }

   //(-/+) Bug 69760, Start
   //public void readSubProjectRowSet(ASPRowSet rowset)
   public void readSubProjectRowSet(ASPRowSet rowset) throws XPathExpressionException
   //(-/+) Bug 69760, End
   {
      this.rsSubProject = rowset;    
      rowset.first();
      do
      {
         ASPBuffer row = rowset.getRow();
         //(-/+) Bug 69760, Start
         //createSubProjectEntity(
         //                      row.getValue("PROJECT_ID"),
         //                      row.getValue("SUB_PROJECT_ID"),
         //                      row.getValue("SUBPROJBLK_SUB_PROJECT_DESC"),
         //                      row.getValue("SUBPROJECT_ANNOTATION"),
         //                      row.getFieldDateValue("EARLY_START"),
         //                      row.getFieldDateValue("EARLY_FINISH"));

         createSubProjectEntity(row.getValue("PROJECT_ID"),
                                row.getValue("SUB_PROJECT_ID"),
                                row.getValue("SUBPROJBLK_SUB_PROJECT_DESC"),
                                row.getValue("SUBPROJECT_ANNOTATION"),
                                row.getFieldDateValue("EARLY_START"),
                                row.getFieldDateValue("EARLY_FINISH"));
         //(-/+) Bug 69760, End
      } while (rowset.next());                      
   }

   //(-/+) Bug 69760, Start
   //public void readActivityRowSet(ASPRowSet rowset)
   public void readActivityRowSet(ASPRowSet rowset) throws XPathExpressionException
   //(-/+) Bug 69760, End
   {
      this.rsActivity = rowset;              
      rowset.first();

      int count = 0;
      do
      {
         ASPBuffer row = rowset.getRow();
         //(-/+) Bug 69760, Start
         //createActivityEntity(
         //                    (long) row.getNumberValue("ACTIVITY_SEQ"),
         //                    row.getValue("PROJECT_ID"),
         //                    row.getValue("SUB_PROJECT_ID"),
         //                    row.getValue("ACTIVITYBLK_SUB_PROJECT_DESC"),
         //                    row.getValue("ACTIVITY_NO"),
         //                    row.getValue("DESCRIPTION"),
         //                    row.getValue("ACTIVITY_ANNOTATION"),
         //                    row.getFieldDateValue("EARLY_START"), 
         //                    row.getFieldDateValue("EARLY_FINISH"),
         //                    row.getFieldDateValue("ACTUAL_START"), 
         //                    row.getFieldDateValue("ACTUAL_FINISH"),
         //                    row.getFieldDateValue("LATE_START"), 
         //                    row.getFieldDateValue("LATE_FINISH"),
         //                    row.getFieldDateValue("NEED_DATE"),
         //                    new Double(row.getNumberValue("TOTAL_DURATION_DAYS")),
         //                    new Double(row.getNumberValue("TOTAL_FLOAT")),
         //                    new Double(row.getNumberValue("FREE_FLOAT")),
         //                    new Double(row.getNumberValue("PROGRESS_HOURS_WEIGHTED")),
         //                    new Double(row.getNumberValue("TOTAL_WORK_DAYS")),
         //                    row.getValue("OBJECT_IDENTIFICATION"),
         //                    (int) row.getNumberValue("EXCEPTIONS_EXIST"),
         //                    (int) row.getNumberValue("HAS_CONNECTED_WO"),
         //                    (int) row.getNumberValue("HAS_CONNECTED_SO"),
         //                    (int) row.getNumberValue("HAS_CONNECTED_PO"),
         //                    (int) row.getNumberValue("HAS_CONNECTED_DOP"),
         //                    row.getValue("OBJID"),
         //                    row.getValue("OBJVERSION"));

         createActivityEntity(row.getValue("OBJID"),
                              row.getValue("OBJVERSION"),
                              (long) row.getNumberValue("ACTIVITY_SEQ"),
                              row.getValue("PROJECT_ID"),
                              row.getValue("SUB_PROJECT_ID"),
                              row.getValue("ACTIVITYBLK_SUB_PROJECT_DESC"),
                              row.getValue("ACTIVITY_NO"),
                              row.getValue("DESCRIPTION"),
                              row.getValue("ACTIVITY_ANNOTATION"),
                              row.getFieldDateValue("EARLY_START"), 
                              row.getFieldDateValue("EARLY_FINISH"),
                              row.getFieldDateValue("ACTUAL_START"), 
                              row.getFieldDateValue("ACTUAL_FINISH"),
                              row.getFieldDateValue("LATE_START"), 
                              row.getFieldDateValue("LATE_FINISH"),
                              row.getFieldDateValue("NEED_DATE"),
                              row.getNumberValue("TOTAL_DURATION_DAYS"),
                              row.getNumberValue("TOTAL_FLOAT"),
                              row.getNumberValue("FREE_FLOAT"),
                              row.getNumberValue("PROGRESS_HOURS_WEIGHTED"),
                              row.getNumberValue("TOTAL_WORK_DAYS"),
                              row.getValue("OBJECT_IDENTIFICATION"),
                              (int) row.getNumberValue("EXCEPTIONS_EXIST"),
                              (int) row.getNumberValue("HAS_CONNECTED_WO"),
                              (int) row.getNumberValue("HAS_CONNECTED_SO"),
                              (int) row.getNumberValue("HAS_CONNECTED_PO"),
                              (int) row.getNumberValue("HAS_CONNECTED_DOP"),
                              row.getValue("OBJID"),
                              row.getValue("OBJVERSION"));
         //(-/+) Bug 69760, End
      } while (rowset.next());                      
   }

   //(-/+) Bug 69760, Start
   //public void readLinkRowSet(ASPRowSet rowset)
   public void readLinkRowSet(ASPRowSet rowset) throws XPathExpressionException
   //(-/+) Bug 69760, End
   {
      this.rsLinks = rowset;                   
      rowset.first();  

      do
      {
         ASPBuffer row = rowset.getRow();
         //(-/+) Bug 69760, Start
         //createLinkEntity(
         //                (long) row.getNumberValue("ACTIVITY_SEQ"),
         //                (long) row.getNumberValue("ACTIVITY_RELATION_SEQ"),
         //                row.getValue("DEPENDENCY_TYPE_DB"),
         //                row.getValue("OBJID"),
         //                row.getValue("OBJVERSION")); 

         createLinkEntity(row.getValue("OBJID"),
                          row.getValue("OBJVERSION"),
                          (long) row.getNumberValue("ACTIVITY_SEQ"),
                          (long) row.getNumberValue("ACTIVITY_RELATION_SEQ"),
                          row.getValue("DEPENDENCY_TYPE_DB"),
                          row.getValue("OBJID"),
                          row.getValue("OBJVERSION"));     
         //(-/+) Bug 69760, End
      } while (rowset.next());                    
   }   

   //(-) Bug 69760, Start
   //private void addActivityRowSet(NeIEntity entity)
   //{
   //   ASPBuffer row = mgr.newASPBuffer();

   //   try
   //   {
   //      row.setValue("PROJECT_ID", entity.getValueAsString("project_id"));
   //      row.setValue("SUB_PROJECT_ID", entity.getValueAsString("sub_project_id"));
   //      row.setValue("ACTIVITYBLK_SUB_PROJECT_DESC", entity.getValueAsString("sub_project_desc"));
   //      row.setValue("ACTIVITY_NO", entity.getValueAsString("activity_no"));
   //      row.setValue("DESCRIPTION", entity.getValueAsString("activity_desc"));
   //      row.setFieldDateItem("EARLY_START", entity.getValueAsDate("early_start"));
   //      row.setFieldDateItem("EARLY_FINISH", entity.getValueAsDate("early_finish"));
   //      row.setValue("PROGRESS_HOURS_WEIGHTED", entity.getValueAsString("progress_hours_weighted"));   
   //   }
   //   catch (Exception ex)
   //   {
   //      ex.printStackTrace();
   //   }

   //   this.rsSubProject.addRow(row);
   //}
   //(-) Bug 69760, End

   public void commitData() throws Exception
   {
      boolean commandsExist = false; 

      //(-/+) Bug 69760, Start
      //trans = mgr.newASPTransactionBuffer(); 

      //commandsExist = appendActivityModifications(appData.getEntitySet("activity_set")) || commandsExist;
      //commandsExist = appendLinkModifications(appData.getEntitySet("link_set")) || commandsExist;

      trans.clear();

      commandsExist = appendActivityModifications() || commandsExist;
      commandsExist = appendLinkModifications() || commandsExist;
      //(-/+) Bug 69760, End

      if (commandsExist)
         trans = mgr.performEx(trans);

      //(-) Bug 69760, Start
      //appData.clear();
      //(-) Bug 69760, End 
   }  

   //(-/+) Bug 69760, Start
   //private boolean appendActivityModifications(NeIEntitySet entitySet) throws Exception {
   //   String attr = "";
   //   boolean commandsExist = false;
   //   int count = entitySet.getEntityCount(); 
   //
   //   for (int i = 0; i < count; i++)
   //   {
   //      NeIEntity entity = entitySet.getEntityAtIndex(i);
   //
   //      if (mgr.isEmpty(entity.getValueAsString(entity_version)))
   //      {
   //         addActivityRowSet(entity);
   //     }
   //      else if (entity.getValueAsString(dirty_flag_name).equals("MODIFIED"))
   //      {
   //         ASPBuffer formatBuffer = mgr.newASPBuffer();
   //         formatBuffer.setFieldDateItem("EARLY_START", entity.getValueAsDate("early_start"));
   //         formatBuffer.setFieldDateItem("EARLY_FINISH", entity.getValueAsDate("early_finish"));
   //         formatBuffer.setNumberValue("PROGRESS_HOURS_WEIGHTED", entity.getValueAsDouble("progress_hours_weighted"));
   //
   //         attr = "";
   //         attr += "EARLY_START" + (char)31 + formatBuffer.getValue("EARLY_START") + (char)30;
   //         attr += "EARLY_FINISH" + (char)31 + formatBuffer.getValue("EARLY_FINISH") + (char)30;
   //         attr += "PROGRESS_HOURS_WEIGHTED" + (char)31 + (mgr.isEmpty(formatBuffer.getValue("PROGRESS_HOURS_WEIGHTED"))?"":formatBuffer.getValue("PROGRESS_HOURS_WEIGHTED")) + (char)30;
   //
   //         commandsExist = true;
   //
   //         cmd = trans.addCustomCommand("MODIFYACTIVITY"+i, "ACTIVITY_API.Modify__");
   //         cmd.addParameter("INFO");
   //         cmd.addParameter("OBJID", entity.getValueAsString(entity_id));
   //         cmd.addParameter("OBJVERSION", entity.getValueAsString(entity_version));
   //         cmd.addParameter("ATTR",attr);
   //         cmd.addParameter("ACTION","DO");     
   //      }
   //      else if (entity.getValueAsString(dirty_flag_name).equals("REMOVED"))
   //      {
   //         commandsExist = true;
   //
   //         cmd = trans.addCustomCommand("REMOVEACTIVITY"+i, "ACTIVITY_API.Remove__");
   //         cmd.addParameter("INFO");  
   //         cmd.addParameter("OBJID", entity.getValueAsString(entity_id));
   //         cmd.addParameter("OBJVERSION", entity.getValueAsString(entity_version));
   //         cmd.addParameter("ACTION","DO");
   //      }
   //   }    
   //
   //   return commandsExist;
   //}

   private boolean appendActivityModifications() throws Exception 
   {
      String attr = "";
      boolean commandsExist = false; 
      Element row;
      int noOfRows;

      NodeList rowList = (NodeList) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ROW_SETS_ELEMENT + "/" + ROW_SET_ELEMENT + "[@Name='" + ACTIVITY_SET_NAME + "']" + "/" + ROW_ELEMENT, xmlOutputDoc, XPathConstants.NODESET);
      noOfRows = rowList.getLength();

      for (int i = 0; i < noOfRows; i++)
      {
         if (rowList.item(i) instanceof Element)
         {
            row = (Element) rowList.item(i);

            if (mgr.isEmpty(row.getAttribute(OBJ_VERSION)) || "NEW".equals(row.getAttribute(DIRTY_FLAG_NAME)))
            {
               ASPBuffer formatBuffer = mgr.newASPBuffer();
               formatBuffer.setFieldDateItem("EARLY_START", mgr.isEmpty(row.getAttribute("early_start"))?null:DateFormat.getInstance().parse(row.getAttribute("early_start")));
               formatBuffer.setFieldDateItem("EARLY_FINISH", mgr.isEmpty(row.getAttribute("early_start"))?null:DateFormat.getInstance().parse(row.getAttribute("early_finish")));

               attr = "";
               attr += "PROJECT_ID" + (char)31 + (mgr.isEmpty(row.getAttribute("project_id"))?"":row.getAttribute("project_id")) + (char)30;
               attr += "SUB_PROJECT_ID" + (char)31 + (mgr.isEmpty(row.getAttribute("sub_project_id"))?"":row.getAttribute("sub_project_id")) + (char)30;
               attr += "ACTIVITYBLK_SUB_PROJECT_DESC" + (char)31 + (mgr.isEmpty(row.getAttribute("sub_project_desc"))?"":row.getAttribute("sub_project_desc")) + (char)30;
               attr += "ACTIVITY_NO" + (char)31 + (mgr.isEmpty(row.getAttribute("activity_no"))?"":row.getAttribute("activity_no")) + (char)30;
               attr += "DESCRIPTION" + (char)31 + (mgr.isEmpty(row.getAttribute("activity_desc"))?"":row.getAttribute("activity_desc")) + (char)30;
               attr += "EARLY_START" + (char)31 + formatBuffer.getValue("EARLY_START") + (char)30;
               attr += "EARLY_FINISH" + (char)31 + formatBuffer.getValue("EARLY_FINISH") + (char)30;
               attr += "PROGRESS_HOURS_WEIGHTED" + (char)31 + (mgr.isEmpty(row.getAttribute("progress_hours_weighted"))?"":row.getAttribute("progress_hours_weighted")) + (char)30;

               cmd = trans.addCustomCommand("ADDACTIVITY"+i, "ACTIVITY_API.New__");
               cmd.addParameter("INFO");
               cmd.addParameter("OBJID", "");
               cmd.addParameter("OBJVERSION", "");
               cmd.addParameter("ATTR", attr);
               cmd.addParameter("ACTION","DO");

               commandsExist = true;
            }
            else if ("MODIFIED".equals(row.getAttribute(DIRTY_FLAG_NAME)))
            {
               ASPBuffer formatBuffer = mgr.newASPBuffer();
               formatBuffer.setFieldDateItem("EARLY_START", (mgr.isEmpty(row.getAttribute("early_start"))?null:DateFormat.getInstance().parse(row.getAttribute("early_start"))));
               formatBuffer.setFieldDateItem("EARLY_FINISH", (mgr.isEmpty(row.getAttribute("early_start"))?null:DateFormat.getInstance().parse(row.getAttribute("early_finish"))));
               formatBuffer.setNumberValue("PROGRESS_HOURS_WEIGHTED", mgr.isEmpty(row.getAttribute("progress_hours_weighted"))?0:NumberFormat.getInstance().parse(row.getAttribute("progress_hours_weighted")).doubleValue());

               attr = "";
               attr += "EARLY_START" + (char)31 + formatBuffer.getValue("EARLY_START") + (char)30;
               attr += "EARLY_FINISH" + (char)31 + formatBuffer.getValue("EARLY_FINISH") + (char)30;
               attr += "PROGRESS_HOURS_WEIGHTED" + (char)31 + formatBuffer.getValue("PROGRESS_HOURS_WEIGHTED") + (char)30;

               cmd = trans.addCustomCommand("MODIFYACTIVITY"+i, "ACTIVITY_API.Modify__");
               cmd.addParameter("INFO");
               cmd.addParameter("OBJID", row.getAttribute(OBJ_ID));
               cmd.addParameter("OBJVERSION", row.getAttribute(OBJ_VERSION));
               cmd.addParameter("ATTR",attr);
               cmd.addParameter("ACTION","DO");   

               commandsExist = true;
            }
            else if ("REMOVED".equals(row.getAttribute(DIRTY_FLAG_NAME)))
            {
               cmd = trans.addCustomCommand("REMOVEACTIVITY"+i, "ACTIVITY_API.Remove__");
               cmd.addParameter("INFO");  
               cmd.addParameter("OBJID", row.getAttribute(OBJ_ID));
               cmd.addParameter("OBJVERSION", row.getAttribute(OBJ_VERSION));
               cmd.addParameter("ACTION","DO");

               commandsExist = true;
            }
         }
      }

      return commandsExist;
   }
   //(-/+) Bug 69760, End

   //(-/+) Bug 69760, Start
   //private boolean appendLinkModifications(NeIEntitySet entitySet) throws Exception {
   //   String attr = "";
   //   boolean commandsExist = false;
   //   int count = entitySet.getEntityCount();
   //   Element recordElement;
   //
   //   for (int i = 0; i < count; i++)
   //   {
   //      NeIEntity entity = entitySet.getEntityAtIndex(i);
   //
   //      if (mgr.isEmpty(entity.getValueAsString(entity_version)))
   //      {
   //         attr = "";
   //         attr += "ACTIVITY_SEQ" + (char)31 + entity.getValueAsString("activity_seq") + (char)30;
   //         attr += "ACTIVITY_RELATION_SEQ" + (char)31 + entity.getValueAsString("activity_relation_seq") + (char)30;
   //         attr += "DEPENDENCY_TYPE_DB" + (char)31 + entity.getValueAsString("dependancy_type_db") + (char)30;
   //         attr += "RELATIONSHIP_DIRECTION_DB" + (char)31 + "S" + (char)30;
   //         attr += "LAG" + (char)31 + "0" + (char)30;
   //         commandsExist = true;
   //
   //         cmd = trans.addCustomCommand("ADDLINK"+i, "P3_ACTIVITY_DEPENDENCY_API.New__");
   //         cmd.addParameter("INFO");
   //         cmd.addParameter("OBJID", "");
   //         cmd.addParameter("OBJVERSION", "");
   //         cmd.addParameter("ATTR",attr);
   //         cmd.addParameter("ACTION","DO");
   //      }
   //      else if (entity.getValueAsString(dirty_flag_name).equals("MODIFIED"))
   //      {
   //         attr = "";
   //         attr += "ACTIVITY_SEQ" + (char)31 + entity.getValueAsString("activity_seq") + (char)30;
   //         attr += "ACTIVITY_RELATION_SEQ" + (char)31 + entity.getValueAsString("activity_relation_seq") + (char)30;
   //         attr += "DEPENDENCY_TYPE_DB" + (char)31 + entity.getValueAsString("dependancy_type_db") + (char)30;
   //         attr += "RELATIONSHIP_DIRECTION_DB" + (char)31 + "S" + (char)30;
   //         attr += "LAG" + (char)31 + "0" + (char)30;
   //         commandsExist = true;
   //
   //         cmd = trans.addCustomCommand("MODIFYLINK"+i, "P3_ACTIVITY_DEPENDENCY_API.Modify__");
   //         cmd.addParameter("INFO");
   //         cmd.addParameter("OBJID", entity.getValueAsString(entity_id));
   //         cmd.addParameter("OBJVERSION", entity.getValueAsString(entity_version));
   //         cmd.addParameter("ATTR",attr);
   //         cmd.addParameter("ACTION","DO");
   //      }
   //   }     
   //
   //   NodeList removedRecordList = removedRecordSetDoc.getDocumentElement().getElementsByTagName(REMOVED_RECORD_ELEMENT);
   //
   //   count = 0;
   //
   //   Node nodeObject;
   //
   //   // NodeList is not a collection
   //   for (int i = 0; i < removedRecordList.getLength(); i++)
   //   {
   //      nodeObject = removedRecordList.item(i);
   //
   //      if (nodeObject instanceof Element)
   //      {
   //         recordElement = (Element) nodeObject;
   //         commandsExist = true;
   //
   //         cmd = trans.addCustomCommand("REMOVELINK" + count, "P3_ACTIVITY_DEPENDENCY_API.Remove__");
   //         cmd.addParameter("INFO");  
   //         cmd.addParameter("OBJID", recordElement.getAttribute(entity_id));
   //         cmd.addParameter("OBJVERSION", recordElement.getAttribute(entity_version));
   //         cmd.addParameter("ACTION","DO");      
   //
   //         count++;
   //      }
   //   }
   //
   //   return commandsExist;
   //} 

   private boolean appendLinkModifications() throws Exception 
   {
      String attr = "";
      boolean commandsExist = false;
      Element row;
      int noOfRows;

      NodeList rowList = (NodeList) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ROW_SETS_ELEMENT + "/" + ROW_SET_ELEMENT + "[@Name='" + LINK_SET_NAME + "']" + "/" + ROW_ELEMENT, xmlOutputDoc, XPathConstants.NODESET);
      noOfRows = rowList.getLength();

      for (int i = 0; i < noOfRows; i++)
      {
         if (rowList.item(i) instanceof Element)
         {
            row = (Element) rowList.item(i);

            if (mgr.isEmpty(row.getAttribute(OBJ_VERSION)) || "NEW".equals(row.getAttribute(DIRTY_FLAG_NAME)))
            {
               attr = "";
               attr += "ACTIVITY_SEQ" + (char)31 + (mgr.isEmpty(row.getAttribute("activity_seq"))?"":row.getAttribute("activity_seq")) + (char)30;
               attr += "ACTIVITY_RELATION_SEQ" + (char)31 + (mgr.isEmpty(row.getAttribute("activity_relation_seq"))?"":row.getAttribute("activity_relation_seq")) + (char)30;
               attr += "DEPENDENCY_TYPE_DB" + (char)31 + (mgr.isEmpty(row.getAttribute("dependancy_type_db"))?"":row.getAttribute("dependancy_type_db")) + (char)30;
               attr += "RELATIONSHIP_DIRECTION_DB" + (char)31 + "S" + (char)30;
               attr += "LAG" + (char)31 + "0" + (char)30;

               cmd = trans.addCustomCommand("ADDLINK"+i, "P3_ACTIVITY_DEPENDENCY_API.New__");
               cmd.addParameter("INFO");
               cmd.addParameter("OBJID", "");
               cmd.addParameter("OBJVERSION", "");
               cmd.addParameter("ATTR", attr);
               cmd.addParameter("ACTION","DO");

               commandsExist = true;
            }
            else if ("MODIFIED".equals(row.getAttribute(DIRTY_FLAG_NAME)))
            {
               attr = "";
               attr += "DEPENDENCY_TYPE_DB" + (char)31 + (mgr.isEmpty(row.getAttribute("dependancy_type_db"))?"":row.getAttribute("dependancy_type_db")) + (char)30;
               attr += "RELATIONSHIP_DIRECTION_DB" + (char)31 + "S" + (char)30;
               attr += "LAG" + (char)31 + "0" + (char)30;

               cmd = trans.addCustomCommand("MODIFYLINK"+i, "P3_ACTIVITY_DEPENDENCY_API.Modify__");
               cmd.addParameter("INFO");
               cmd.addParameter("OBJID", row.getAttribute(OBJ_ID));
               cmd.addParameter("OBJVERSION", row.getAttribute(OBJ_VERSION));
               cmd.addParameter("ATTR",attr);
               cmd.addParameter("ACTION","DO");   

               commandsExist = true;
            }
            else if ("REMOVED".equals(row.getAttribute(DIRTY_FLAG_NAME)))
            {
               cmd = trans.addCustomCommand("REMOVELINK"+i, "P3_ACTIVITY_DEPENDENCY_API.Remove__");
               cmd.addParameter("INFO");  
               cmd.addParameter("OBJID", row.getAttribute(OBJ_ID));
               cmd.addParameter("OBJVERSION", row.getAttribute(OBJ_VERSION));
               cmd.addParameter("ACTION","DO");

               commandsExist = true;
            }
         }
      }

      return commandsExist;
   } 
   //(-/+) Bug 69760, End
}
