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
*  File        : XmlManager.java  
*  History     :
*  LAFOLK  2007-06-26  Created. Added EntitySet and Entity creation methods. 
*  ARWILK  2007-07-11  Modified methods by introducing dirty_flag_name, entity_id and entity_version.
*                      Also added setXMLToOutputStream, getGanttData, setXMLFromInputStream, setGanttData. 
*  ARWILK  2007-07-20  Modified XmlManager to initialize XML Elements. 
*                      Added addDataToOutput, addErrorToOutput, addInfoToOutput, addWarningToOutput, addTranslationToOutput, getOutputXML.
*  LAFOLK  2007-07-21  Added additional fields in createActivityEntitySet and createActivityEntity to support JGannt Scheduling.
*  ARWILK  2007-07-22  Added sendLicenseKey to read the license key from <component>Config.xml file.
*  ARWILK  2007-08-20  Deployment error on websphere and oracle application servers when JDOM is used.
*                      Replaced the use of JDOM with JAXP.
*  ARWILK  2007-08-21  Changed functionality to support DOM Level 2(DOM Level 3 specific functions were removed).
*                      This is because Java 1.4.2 supports DOM Level 2 Specification.
*  LAFOLK  2007-08-22  Added addPropertyTranslationToOutput.
*  ARWILK  2007-08-23  Modified createActivityEntitySet, createActivityEntity, readActivityRowSet to supprot Critical Activities.
*  ARWILK  2007-08-30  Call 147939: Added escapeForXML and decodeForXML and used then when creating appData XML.
*  LAFOLK  2007-08-30  Call 147899: Modified appendActivityModifications.
*  ARWILK  2007-09-13  Call 146779: Modified createActivityEntitySet, createActivityEntity.
*  ARWILK  2007-09-19  Moved specific functions to ProjGanttXMLManager. Made XMLManager abstract.  
*                      (createSubProjectEntitySet, createActivityEntitySet, createLinkEntitySet, 
*                       createSubProjectEntity, createActivityEntity, createLinkEntity, 
*                       readSubProjectRowSet, readActivityRowSet, readLinkRowSet, addActivityRowSet, 
*                       appendActivityModifications, appendLinkModifications)
*  ARWILK  2007-09-21   Added method 'replace' to support Java 1.4.2 string replacement.
* -----------------------APP7.5 SP1-------------------------------------------
*  ARWILK  2007-12-31  Bug 69760(Re-write for Oracle App SRV), Modified to support rowset based data structure.
*                      Added overloaded escapeForXML methods. Removed obsolete methods.
* ----------------------------------------------------------------------------
*/  

package ifs.projw.lib;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.buffer.*;

import ifs.fnd.base.FndEncryption;

import java.io.*;
import java.util.Date;
import java.util.List;
//(+) Bug 69760, Start
import java.util.Arrays;
import java.text.DateFormat;
import java.text.NumberFormat;

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

public abstract class XmlManager
{
   private DocumentBuilderFactory docFactory;
   private DocumentBuilder docBuilder;
   //(+) Bug 69760, Start
   protected XPath xpath;
   //(+) Bug 69760, End

   private TransformerFactory transformFactory;
   private Transformer transformer;
   private StreamResult streamDestination;
   private Source domsrc;

   private Document doc;
   //(-/+) Bug 69760, Start
   //private Document xmlOutputDoc;
   protected Document xmlOutputDoc;
   //(-/+) Bug 69760, End
   protected Document removedRecordSetDoc;

   //(-) Bug 69760, Start
   //private JGantt jGantt;
   //protected NeIAppData appData;    
   //protected NeIEntitySet esSubProj;
   //protected NeIEntitySet esActivity;
   //protected NeIEntitySet esLinks;

   //protected ASPRowSet rsSubProject;
   //protected ASPRowSet rsActivity;
   //protected ASPRowSet rsLinks; 
   //(-) Bug 69760, End

   protected ASPManager mgr;
   protected ASPTransactionBuffer trans;
   protected ASPCommand cmd;

   private Element dataSetElement;
   private Element licenseKeyElement;
   private Element userProfileElement;
   //(-/+) Bug 69760, Start
   //private Element dataElement;
   protected Element entitySetsElement;
   protected Element rowSetsElement;
   //(-/+) Bug 69760, End
   private Element errorElement;
   private Element infoSetElement;
   private Element warningSetElement;
   private Element translationSetElement;
   private Element translator;
   private Element term;

   //(-) Bug 69760, Start
   //protected final String dirty_flag_name = "___entity_status_flag"; // Accepts REMOVED/MODIFIED/NEW.
   //protected final String entity_version = "objversion"; // Is mapped to the objversion of the framework.
   //protected final String entity_id = "objid"; // Is mapped to the objid of the framework.
   //(-) Bug 69760, End

   //(-/+) Bug 69760, Start
   //private final String ROOT_ELEMENT = "JGanttDataSet";
   protected static final String ROOT_ELEMENT = "JGanttDataSet";
   private static final String LICENSE_KEY_ELEMENT = "LicenseKey"; 
   private static final String USER_PROFILE = "User_Profile";
   //private static final String DATA_ELEMENT = "Data";
   private static final String ENTITY_SETS_ELEMENT = "EntitySets";
   protected static final String ENTITY_SET_ELEMENT = "EntitySet";
   protected static final String ENTITY_ELEMENT = "Entity";
   protected static final String ROW_SETS_ELEMENT = "RowSets";
   protected static final String ROW_SET_ELEMENT = "RowSet";
   protected static final String ROW_ELEMENT = "Row";

   private static final String ERROR_ELEMENT = "Error";
   private static final String INFO_SET_ELEMENT = "InfoSet";
   private static final String INFO_ELEMENT = "Info";
   private static final String WARNING_SET_ELEMENT = "WarningSet";
   private static final String WARNING_ELEMENT = "Warning";
   private static final String TRANSLATION_SET_ELEMENT = "TranslationSet";
   private static final String TERM_ELEMENT = "Term";

   private static final String REMOVED_RECORDSET_ELEMENT = "RemovedRecordSet";
   protected static final String REMOVED_RECORD_ELEMENT = "RemovedRecord";

   private static final String BUTTON_TRANSLATION = "Button";
   private static final String POPUPMENU_TRANSLATION = "PopupMenu";
   private static final String PROPERTY_TRANSLATION = "PropertyFields"; 
   //(-/+) Bug 69760, End

   /** Creates a new instance of XmlManager */
   public XmlManager(ASPManager mgr) throws ParserConfigurationException, TransformerConfigurationException
   {
      this.mgr = mgr;
      //(+) Bug 69760, Start
      trans = mgr.newASPTransactionBuffer(); 
      //(+) Bug 69760, End

      docFactory = DocumentBuilderFactory.newInstance();
      //(+) Bug 69760, Start
      xpath = XPathFactory.newInstance().newXPath();
      //(+) Bug 69760, End
      docBuilder = docFactory.newDocumentBuilder();
      xmlOutputDoc = docBuilder.newDocument();

      dataSetElement = xmlOutputDoc.createElement(ROOT_ELEMENT); 
      xmlOutputDoc.appendChild(dataSetElement);

      licenseKeyElement = xmlOutputDoc.createElement(LICENSE_KEY_ELEMENT); 
      dataSetElement.appendChild(licenseKeyElement);

      userProfileElement = xmlOutputDoc.createElement(USER_PROFILE); 
      dataSetElement.appendChild(userProfileElement);

      //(-/+) Bug 69760, Start
      //dataElement = xmlOutputDoc.createElement(DATA_ELEMENT); 
      //dataSetElement.appendChild(dataElement);

      entitySetsElement = xmlOutputDoc.createElement(ENTITY_SETS_ELEMENT); 
      dataSetElement.appendChild(entitySetsElement);

      rowSetsElement = xmlOutputDoc.createElement(ROW_SETS_ELEMENT); 
      dataSetElement.appendChild(rowSetsElement);
      //(-/+) Bug 69760, End

      errorElement = xmlOutputDoc.createElement(ERROR_ELEMENT); 
      dataSetElement.appendChild(errorElement);

      infoSetElement = xmlOutputDoc.createElement(INFO_SET_ELEMENT); 
      dataSetElement.appendChild(infoSetElement);

      warningSetElement = xmlOutputDoc.createElement(WARNING_SET_ELEMENT); 
      dataSetElement.appendChild(warningSetElement);

      translationSetElement = xmlOutputDoc.createElement(TRANSLATION_SET_ELEMENT); 
      dataSetElement.appendChild(translationSetElement);

      transformFactory = TransformerFactory.newInstance( ); 
      transformer = transformFactory.newTransformer(); 

      //(-) Bug 69760, Start
      //jGantt = new JGantt();
      //appData = jGantt.getAppData();
      //(-) Bug 69760, End
   }

   //public XmlManager(ASPManager mgr, ASPRowSet rowset1, ASPRowSet rowset2, ASPRowSet rowset3) throws ParserConfigurationException, TransformerConfigurationException
   //{
   //   this(mgr);
   //   this.rsSubProject = rowset1;
   //   this.rsActivity = rowset2;
   //   this.rsLinks = rowset3;
   //}

   //(-) Bug 69760, Start
   //private void setXMLToOutputStream(OutputStream out)
   //{
   //   appData.getXMLInterface().saveXML(out, true, true);
   //} 
   //(-) Bug 69760, End

   //(-) Bug 69760, Start
   //public String getGanttData()
   //{
   //   ByteArrayOutputStream bOut = new ByteArrayOutputStream();
   //   setXMLToOutputStream(bOut);
   //   return bOut.toString();
   //} 
   //(-) Bug 69760, End

   //(-) Bug 69760, Start
   //private void setXMLFromInputStream(InputStream in)
   //{
   //   appData.getXMLInterface().loadXML(in);
   //} 
   //(-) Bug 69760, End

   //(-/+) Bug 69760, Start
   //public void setGanttData(String xmlString)
   //{
   //   appData.clear();
   //   ByteArrayInputStream bIn = new ByteArrayInputStream(xmlString.getBytes());
   //   setXMLFromInputStream(bIn);
   //} 

   public void setGanttData(String xmlString) throws Exception
   {
      xmlOutputDoc = createDocument(xmlString);
   } 
   //(-/+) Bug 69760, End

   public void setRemovedRecordSet(String xmlString) throws Exception
   {
      //(-/+) Bug 69760, Start
      //DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      //removedRecordSetDoc = docBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
      removedRecordSetDoc = createDocument(xmlString);
      //(-/+) Bug 69760, End
   }

   //(+) Bug 69760, Start
   protected Document createDocument(String xmlString) throws Exception
   {
      docBuilder = docFactory.newDocumentBuilder();
      Document document = docBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
      return document;
   }
   //(+) Bug 69760, End

   // This should get all the entity modifications (new and modify. Not remove) and call appropriate saving methods through performEx.
   public abstract void commitData() throws Exception;

   public void sendLicenseKey()
   {
      String licenseKey = "";
      String is_license_encrypted = "";

      try
      {
         is_license_encrypted = this.mgr.getASPConfig().getParameter("PROJW/JGANTT_LICENSE/IS_LICENSE_ENCRYPTED", "");
         licenseKey = this.mgr.getASPConfig().getParameter("PROJW/JGANTT_LICENSE/LICENSE_KEY", "");
      }
      catch ( Throwable e )
      {
         is_license_encrypted = "";
         licenseKey = "";
      }

      if (!mgr.isEmpty(licenseKey))
         try
         {
            // Changed to Support for DOM 2
            //licenseKeyElement.setTextContent("Y".equals(is_license_encrypted)?FndEncryption.decrypt(licenseKey):licenseKey);
            licenseKeyElement.setAttribute("Value", "Y".equals(is_license_encrypted)?FndEncryption.decrypt(licenseKey):licenseKey);
         }
         catch (Exception ex)
         {
            addErrorToOutput(ex);
         }
      else
         addErrorToOutput(new Exception(mgr.translate("PROJWJGANTTNOLICENSE: Parameter 'LICENSE_KEY' not found in configuration file.")));

      writeOutputToResponse();
   }

   //(-) Bug 69760, Start
   //public void addDataToOutput(String appData)
   //{
   //   // Changed to Support for DOM 2 
   //   //dataElement.setTextContent(appData);
   //   dataElement.setAttribute("Value", appData);
   //}
   //(-) Bug 69760, End

   public void addErrorToOutput(Throwable e)
   {
      String errorText ;

      if (e instanceof ASPLog.ExtendedAbortException)
      {
         Buffer b = ((ASPLog.ExtendedAbortException)e).getExtendedInfo();

         try
         {
            errorText = b.getItem("ERROR_MESSAGE").getString();
         }
         catch (ItemNotFoundException itemEx)
         {
            errorText = itemEx.getErrorStack();
         }
      }
      else
      {
         errorText = e.getMessage();
      }

      // Changed to Support for DOM 2
      //errorElement.setTextContent(errorText);

      //(-/+) Bug 69760, Start
      //errorElement.setAttribute("Value", errorText);
      try
      {
         errorElement = (Element) xpath.evaluate("/" + ROOT_ELEMENT + "/" + ERROR_ELEMENT, xmlOutputDoc, XPathConstants.NODE);
         errorElement.setAttribute("Value", errorText);
      }
      catch (Exception ex)
      {
         mgr.responseWrite("ERROR: " + ex.getMessage());
         mgr.endResponse();
      }
      //(-/+) Bug 69760, Start
   }

   public void addInfoToOutput(String infoText)
   {
      Element infoElement  = xmlOutputDoc.createElement(INFO_ELEMENT);
      // Changed to Support for DOM 2
      //infoElement.setTextContent(infoText);
      infoElement.setAttribute("Value", infoText);
      infoSetElement.appendChild(infoElement);
   }

   public void addWarningToOutput(String warningText)
   {
      Element warningElement  = xmlOutputDoc.createElement(WARNING_ELEMENT);
      // Changed to Support for DOM 2
      //warningElement.setTextContent(warningText);
      warningElement.setAttribute("Value", warningText);
      warningSetElement.appendChild(warningElement);
   }

   private void addTranslationToOutput(String id, String translationType, String text, String translatedText)
   {
      Element termElement = xmlOutputDoc.createElement(TERM_ELEMENT);
      termElement.setAttribute("Id", id);
      termElement.setAttribute("Type", translationType);
      termElement.setAttribute("Text", text);
      // Changed to Support for DOM 2
      //termElement.setTextContent(translatedText);
      termElement.setAttribute("Value", translatedText);
      translationSetElement.appendChild(termElement);
   }

   //(-/+) Bug 69760, Start
   //public void addButtonTranslationToOutput(int buttonId, String text, String translatedText)
   public void addButtonTranslationToOutput(String buttonName, String text, String translatedText)
   //(-/+) Bug 69760, End
   {
      //(-/+) Bug 69760, Start
      //addTranslationToOutput((new Integer(buttonId)).toString(), BUTTON_TRANSLATION, text, translatedText);
      addTranslationToOutput(buttonName, BUTTON_TRANSLATION, text, translatedText);
      //(-/+) Bug 69760, End
   }

   public void addPopupMenuTranslationToOutput(String menuId, String text, String translatedText)
   {
      addTranslationToOutput(menuId, POPUPMENU_TRANSLATION, text, translatedText);
   }

   public void addPropertyTranslationToOutput(String propertyField, String text, String translatedText)
   {
      addTranslationToOutput(propertyField, PROPERTY_TRANSLATION, text, translatedText);
   }

   public void addUserProfileToOutput(String profileXML)
   {
      // Changed to Support for DOM 2
      //userProfileElement.setTextContent(mgr.isEmpty(profileXML)?"":profileXML);
      userProfileElement.setAttribute("Value", mgr.isEmpty(profileXML)?"":profileXML);
   }

   //(-/+) Bug 69760, Start
   //private String getOutputXML()
   public String getOutputXML()
   //(-/+) Bug 69760, End
   {
      String xmlString;
      ByteArrayOutputStream bout = new ByteArrayOutputStream();

      try
      {
         domsrc = new DOMSource(xmlOutputDoc); 
         streamDestination = new StreamResult();
         streamDestination.setOutputStream(bout); 
         transformer.transform(domsrc, streamDestination); 
         xmlString = bout.toString();
         return xmlString;
      }
      catch (Exception ex)
      {
         return"";
      }
   }

   public void writeOutputToResponse()
   {
      String theTotalOutput = getOutputXML();
      mgr.responseWrite(theTotalOutput);
      mgr.endResponse();
   } 

   /**
    * Returns the specified 'str' string with all occurrences of 'substr'
    * replaced with 'with'. The string is not copied if 'substr' is
    * not found in 'str'. This should be used only when the Java version is < 1.5
    * Else use the standard String.replace(CharSequence target, CharSequence replacement)
    * @param str a String to search and replace in
    * @param substr old substring
    * @param with   new substring
    * @return a string derived from 'str' string by replacing every occurrence of
    *         the 'substr' substring with the 'with' substring
    */
   private static String replace(String str, String substr, String with)
   {
      if (str == null || substr == null)
         return null;
      if (str.length() == 0 || substr.length() == 0)
         return str;

      StringBuffer buf = null;
      int i = 0, j, skip = substr.length();

      while (true)
      {
         j = str.indexOf(substr, i);
         if (j < 0)
         {
            if (buf == null)
               return str;
            else
            {
               buf.append(str.substring(i));
               return buf.toString();
            }
         }
         else
         {
            if (buf == null)
            {
               if (with == null)
                  return null;
               buf = new StringBuffer(10 + str.length());
            }
            buf.append(str.substring(i, j));
            buf.append(with);
            i = j + skip;
         }
      }
   }

   protected String escapeForXML(String inputStr)
   {
      String outputStr; 

      // This code is written for webclient 3.8.0 (using java 1.5)
      // Java 1.4.2 does not support replace(CharSequence target, CharSequence replacement).
      // Thus in webclient 3.7.0 (which uses java 1.4.2) you will have to use the replace method defined in this class. (A copy of ifs.fnd.util.Str.replace(String str, String substr, String with))
      if (!mgr.isEmpty(inputStr))
      {
         // This function replaces all special characters in the specified string with their XML equivalents
         // The excat sequence of replace should take place. The '&' replace should always be the first statement and the '\' should be the second statement.

         // Code for 1.5.0 (Webclient => 3.8.0)
         outputStr = inputStr.replace("&", "&amp;"). 
                     replace("\\", "\\\\").
                     replace("\n", "\\n").
                     replace("\'", "&apos;").
                     replace("\"", "&quot;").
                     replace("<", "&lt;").
                     replace(">", "&gt;");

         // Code for 1.4.2 (3.8.0 > Webclient => 3.7.0)
         //outputStr = replace(replace(replace(replace(replace(replace(replace(inputStr, 
         //                                                        "&", "&amp;"),
         //                                                "\\", "\\\\"),
         //                                        "\n", "\\n"),
         //                                "\'", "&apos;"),
         //                        "\"", "&quot;"),
         //                "<", "&lt;"),
         //        ">", "&gt;");
      }
      else
         //(+) Bug 69760, Start
         //outputStr = inputStr;
         outputStr = "";
      //(+) Bug 69760, End

      return outputStr;
   }

   //(+) Bug 69760, Start
   protected String escapeForXML(Date inputDate)
   {
      if (inputDate != null)
         return escapeForXML(DateFormat.getInstance().format(inputDate));
      else
         return "";
   }

   protected String escapeForXML(double inputDouble)
   {
      if ((new Double(inputDouble)).isNaN())
         return escapeForXML("0");
      else
         return escapeForXML(NumberFormat.getInstance().format(inputDouble));
   }

   protected String escapeForXML(long inputLong)
   {
      return escapeForXML(NumberFormat.getInstance().format(inputLong));
   }
   //(+) Bug 69760, End

   protected String decodeForXML(String inputStr)
   {
      String outputStr;

      // This code is written for webclient 3.8.0 (using java 1.5)
      // Java 1.4.2 does not support replace(CharSequence target, CharSequence replacement).
      // Thus in webclient 3.7.0 (which uses java 1.4.2) you will have to use the replace method defined in this class. (A copy of ifs.fnd.util.Str.replace(String str, String substr, String with))

      if (!mgr.isEmpty(inputStr))
      {
         // This function replaces all XML escape characters with the special characters.
         // The exact sequence of replace should take place. The '&' replace should always be the last statement and the '\' should be placed just before the last.

         // Code for 1.5.0 (Webclient => 3.8.0)
         outputStr = inputStr.replace("&gt;", ">").
                     replace("&lt;", "<").
                     replace("&quot;", "\"").
                     replace("&apos;", "\'").
                     replace("\\n", "\n").
                     replace("\\\\", "\\").
                     replace("&amp;", "&"); 

         // Code for 1.4.2 (3.8.0 > Webclient => 3.7.0)
         //outputStr = replace(replace(replace(replace(replace(replace(replace(inputStr, 
         //                                                                    "&gt;", ">"), 
         //                                                            "&lt;", "<"), 
         //                                                    "&quot;", "\""), 
         //                                            "&apos;", "\'"), 
         //                                    "\\n", "\n"), 
         //                            "\\\\", "\\"), 
         //                    "&amp;", "&");  
      }
      else
         outputStr = inputStr;

      return outputStr;
   }
}

