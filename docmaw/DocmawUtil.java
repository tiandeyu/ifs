 
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
 *  File        : DocmawUtil.java
 *  Description :
 *
 *  History
 *
 *  Date          Sign      Descripiton
 *  ----          ----      -----------
 *
 *  2003-04-02    DIKALK    Added method getClientMgrObjectStr()
 *  2003-05-27    DIKALK    Added methods getFileName(), getBaseFileName(), getFileExtention()
 *  2003-05-27    DIKALK    Moved method getRandomFilename() from DocSrv to this class
 *  2003-08-04    DIKALK    Incremented IFSCliMgrOCX cab version
 *  2003-08-13    DIKALK    Call Id 95648: Modified CLSID.
 *  2004-03-26    BAKALK    Merged SP1.
 *  2004-05-10    BAKALK    Updated Client Manager Ocx version.
 *  2004-05-11    BAKALK    Updated Client Manager Ocx version from 2,1,0,5 to 2,1,0,6.
 *  2004-05-17    BAKALK    Updated Client Manager Ocx version from 2,1,0,6 to 2,1,0,7.
 *  2004-05-17    BAKALK    Updated Client Manager Ocx version from 2,1,0,8 to 2,1,0,9.
 *  2004-10-01    DIKALK    Updated Client Manager Ocx version from 2,1,0,9 to 2,1,0,10.
 *  2004-10-21    BAKALK    Updated Client Manager Ocx version from 2,1,0,9 to 2,1,0,11.
 *  2004-11-08    DIKALK    Updated Client Manager Ocx version from 2,1,0,11 to 2,1,0,12.
 *  2004-11-11    BAKALK    Updated Client Manager Ocx version from 2,1,0,12 to 2,1,0,13.
 *  2004-12-17    Bakalk    Merged the bug 47864. Updated Ocx version from 2,1,0,16 to 2,1,0,17.
 *  2004-12-22    Bakalk    Merged the bug 47864. Updated Ocx version from 2,1,0,17 to 2,1,0,18.
 *  2005-04-07    Karalk    Updated Client Manager Ocx version from 3,0,0,4 to 3,0,0,5.
 *  2005-04-19    Karalk    Updated Client Manager Ocx version from 3,0,0,8 to 3,0,0,9.
 *  2005-04-28    Karalk    Updated Client Manager Ocx version from 3,0,0,11 to 3,0,0,12.
 *  2005-05-16    Karalk    Updated Client Manager Ocx version from 3,0,0,12 to 3,0,0,13.
 *  2005-05-16    SUKMLK    Updated Client Manager Ocx version from 3,0,0,13 to 3,0,0,14.
 *  2005-05-31    SHTHLK    Updated Client Manager Ocx version from 3,0,0,14 to 3,0,0,15.
 *  2005-06-01    KARALK    Updated Client Manager Ocx version from 3,0,0,15 to 3,0,0,16.
 *  2005-06-01    KARALK    Updated Client Manager Ocx version from 3,0,0,16 to 3,0,0,17.
 *  2005-07-12    SOMBDY    Updated Client Manager Ocx version from 3,0,0,17 to 3,0,0,18.
 *  2005-07-12    SUKMLK    Updated Client Manager Ocx version from 3,0,0,18 to 3,0,0,19.
 *  2005-07-14    SUKMLK    Updated Client Manager Ocx version from 3,0,0,19 to 3,0,0,20.
 *  2005-08-05    SHTHLK    Call Id 126148 Updated Client Manager Ocx version to 3,0,0,23.
 *  2005-08-24    SHTHLK    Call Id 121320 Updated Client Manager Ocx version to 3,0,0,24.
 *  2005-08-29    BAKALK    Merged Statnett in Edge.
 *  2005-09-01    SUKMLK    Call Id 125669 Updated Client Manager Ocx version to 3,0,0,25.
 *  2005-10-11    MDAHSE    Call 127801. Added private method drawStatusFieldInternal
 *                          which is called from the two overloaded public methods
 *                          drawStatusField().
 *  2005-10-13    MDAHSE    Changed to use new drop control. Removed writeOleDragOverStatusFunction.
 *  2005-10-14    MDAHSE    Removed the three static message variables and instead send them in as
 *                          arguments to writeOleDragOverFunction(). I got problems with messgaes
 *                          from one page also showing up in other pages.
 *  2005-10-03    DIKALK    Modified convertRandomBytesToFileName() to generate file names that is
 *                          more secure. (Call 128390)
 *  2005-12-01    SUKMLK    Fixed call 129507. Synchronizing the call to getRandomFileName().
 *  2005-12-02    MDAHSE    Changed version of the OCX string (.32) to match new Unicode-fixed OCX.
 *  2005-12-06    MDAHSE    Fixed above fix and had to increase version...
 *  2006-02-12    DIKALK    Removed with and height parameters to DrawDnDArea()
 *  2006-03-08    MDAHSE    Updated IfsCliMgrOCX version to 3,0,0,36.
 *  2006-03-15    BAKALK    Updated IfsCliMgrOCX version to 3,0,0,38.
 *  2006-03-17    THWILK    Bug 56016,Incremented IFSCliMgrOCX.cab version to 2,1,0,40 in getClientMgrObjectStr.
 *  2006-03-21    THWILK    Bug 56321,Incremented IFSCliMgrOCX.cab version to 2,1,0,41 in getClientMgrObjectStr.
 *  2006-05-30    BAKALK    bug 58326,Incremented IFSCliMgrOCX.cab version to 3,0,0,42 in getClientMgrObjectStr.
 *  2006-06-02    KARALK    bug 57041,Incremented IFSCliMgrOCX.cab version to 3,0,0,43 in getClientMgrObjectStr.
 *  2006-06-04    BAKALK    bug 58326,Incremented IFSCliMgrOCX.cab version to 3,0,0,44 in getClientMgrObjectStr.
 *  2006-06-07    KARALK    bug 57041,Incremented IFSCliMgrOCX.cab version to 3,0,0,45 in getClientMgrObjectStr.
 *  2006-06-23    KARALK    bug 56684,Incremented IFSCliMgrOCX.cab version to 3,0,0,46 in getClientMgrObjectStr.
 *  2006-07-12    BAKALK    bug 59331,Incremented IFSCliMgrOCX.cab version to 3,0,0,47 in getClientMgrObjectStr.
 *  2006-07-20    NIJALK    Bug 54793,Incremented IFSCliMgrOCX.cab version to 3,0,0,48 in getClientMgrObjectStr.
 *  2006-07-21    NIJALK    Bug 56685,Incremented IFSCliMgrOCX.cab version to 3,0,0,49 in getClientMgrObjectStr.
 *  2006-07-25    NIJALK    Bug 55611,Incremented IFSCliMgrOCX.cab version to 3,0,0,50 in getClientMgrObjectStr.
 *  2006-07-27    NIJALK    Bug 54793,Incremented IFSCliMgrOCX.cab version to 3,0,0,51 in getClientMgrObjectStr.
 *  2006-08-02    DULOLK    Bug 57010,Incremented IFSCliMgrOCX.cab version to 3,0,0,52 in getClientMgrObjectStr.
 *  2006-08-08    CHODLK    Bug 59432,Incremented IFSCliMgrOCX.cab version to 3,0,0,53 in getClientMgrObjectStr.
 *  2006-08-08    BAKALK    Bug 59667,Incremented IFSCliMgrOCX.cab version to 3,0,0,54 in getClientMgrObjectStr.
 *  2006-09-28    DULOLK    Bug 59787,Incremented IFSCliMgrOCX.cab version to 3,0,0,55 in getClientMgrObjectStr.
 *  2006-11-09    NIJALK    Bug 58421,Incremented IFSCliMgrOCX.cab version to 3,0,0,56 in getClientMgrObjectStr.
 *  2007-03-30    JANSLK    Merged Bug 62341,Incremented IFSCliMgrOCX.cab version to 2,1,0,57 in getClientMgrObjectStr. 
 *  2007-04-08    BAKALK    Incremented IFSCliMgrOCX.cab version to 3,0,0,58 in getClientMgrObjectStr.
 *  2007-05-03    BAKALK    Merged bug 58113,Incremented IFSDropArea.cab version to 1,0,0,2. 
 *  2007-07-24    UPDELK    Call Id: 146870: DOCMAW/DOCMAN - Transmittal and dynamic dependencies to PROJ
 *  2007-08-01    UPDELK    Call Id: 146514: Incremented IFSCliMgrOCX.cab version to 3,0,0,59 in getClientMgrObjectStr.
 *  2007-08-21    BAKALK    Call Id: 143180: Incremented IFSCliMgrOCX.cab version to 3,0,0,60 in getClientMgrObjectStr.
 *  2007-08-24    BAKALK    Merged Bug Id: 64138: Incremented IFSCliMgrOCX.cab version to 3,0,0,61 in getClientMgrObjectStr.
 *  2007-08-24    BAKALK    Call Id: 14664138: Incremented IFSCliMgrOCX.cab version to 3,0,0,62 in getClientMgrObjectStr.
 *  2007-09-24    BAKALK    Changed the classId and cab version for 7.5: CLSID:25988E0B-4429-4A73-A2B5-7153A68155C7 version=4,0,0,1.
 *  2007-11-14    BAKALK    Call Id: 68849: Incremented IFSCliMgrOCX.cab version to 4,0,0,2 in getClientMgrObjectStr.
 *  2007-11-30    AMNALK    Bug Id: 65997: Incremented IFSCliMgrOCX.cab version to 4,0,0,3 in getClientMgrObjectStr.
 *  2008-03-27    VIRALK    Bug Id: 71463: Incremented IFSCliMgrOCX.cab version to 4,0,0,4 in getClientMgrObjectStr.
 *  2008-05-14    DULOLK    Bug Id: 70314: Incremented IFSCliMgrOCX.cab version to 4,0,0,5 in getClientMgrObjectStr.
 *  2008-05-28    VIRALK    Bug Id: 71463: Incremented IFSCliMgrOCX.cab version to 4,0,0,6 in getClientMgrObjectStr.
 *  2008-06-24    SHTHLK    Bug Id: 72366: Incremented IFSCliMgrOCX.cab version to 4,0,0,7 in getClientMgrObjectStr.
 *  2008-06-27    SHTHLK    Bug Id: 74676: Incremented IFSCliMgrOCX.cab version to 4,0,0,8 in getClientMgrObjectStr.
 *  2008-07-07    AMNALK    Bug Id: 72460: Incremented IFSCliMgrOCX.cab version to 4,0,0,9 in getClientMgrObjectStr.
 *  2008-07-08    SHTHLK    Bug Id: 69562: Incremented IFSCliMgrOCX.cab version to 4,0,0,10 in getClientMgrObjectStr.
 *  2008-04-10    DULOLK    Bug Id: 69735, Incremented IFSCliMgrOCX.cab version to 4,0,0,11 in getClientMgrObjectStr.
 *                                         Incremented IFSDropArea.cab version to 1,0,0,3 in drawDnDArea().
 *  2008-07-22    VIRALK    Bug Id: 74523: Incremented IFSCliMgrOCX.cab version to 4,0,0,12 in getClientMgrObjectStr.
 *  2008-08-20    AMNALK    Bug Id: 71842: Incremented IFSCliMgrOCX.cab version to 4,0,0,13 in getClientMgrObjectStr.
 *  2008-08-25    AMNALK    Bug Id: 73736: Incremented IFSCliMgrOCX.cab version to 4,0,0,14 in getClientMgrObjectStr.
 *  2008-09-15    AMNALK    Bug Id: 73736: Incremented IFSDropArea.cab version to 2,0,0,0 in drawDnDArea() and changed the class id of drawDnDArea()
 *  2008-10-15    DULOLK    Bug Id: 71831: Incremented IFSCliMgrOCX.cab version to 4,0,0,15 in getClientMgrObjectStr.
 *  2008-11-24    DULOLK    Bug Id: 78101: Incremented IFSCliMgrOCX.cab version to 4,0,0,16 in getClientMgrObjectStr.
 *  2008-12-9     VIRALK    Bug Id: 79107: Incremented IFSCliMgrOCX.cab version to 4,0,0,17 in getClientMgrObjectStr.
 *  2008-12-19    VIRALK    Bug Id: 79070: Incremented IFSCliMgrOCX.cab version to 4,0,0,18 in getClientMgrObjectStr.
 *  2009-02-18    AMNALK    Bug Id: 78749: Incremented IFSCliMgrOCX.cab version to 4,0,0,19 in getClientMgrObjectStr.
 *  2009-03-30    SHTHLK    Bug Id: 81741: Modified getPath() and getFileName() to be compatible with unix Os's.
 *  2009-08-03    AMNALK    Bug Id: 83912: Incremented IFSCliMgrOCX.cab version to 4,0,0,20 in getClientMgrObjectStr.
 *  2009-08-04    SHTHLK    Bug Id; 83898; Added a new drawDnDArea() and writeOleDragOverFunction() to include the portal id.
 *  2009-08-27    AMNALK    Bug Id: 85187: Incremented IFSCliMgrOCX.cab version to 4,0,0,21 in getClientMgrObjectStr. 
 *  2009-09-21    SHTHLK    Bug Id: 85997: Incremented IFSCliMgrOCX.cab version to 4,0,0,22 in getClientMgrObjectStr.
 *  2009-11-12    AMNALK    Bug Id: 85070: Incremented IFSCliMgrOCX.cab version to 4,0,0,23 in getClientMgrObjectStr.
 *  2010-04-27    RUMELK    Bug Id: 89383: Incremented IFSCliMgrOCX.cab version to 4,0,0,24 in getClientMgrObjectStr.
 *  2010-05-05    RUMELK    Bug Id: 89383: Incremented IFSCliMgrOCX.cab version to 4,0,0,25 in getClientMgrObjectStr.
 *  2010-09-16    DULOLK    Bug Id: 92125: Incremented IFSCliMgrOCX.cab version to 4,0,0,26 in getClientMgrObjectStr.
 *  2010-09-21    ARWILK    Bug Id: 92928: Incremented IFSCliMgrOCX.cab version to 4,0,0,27 in getClientMgrObjectStr.
 *  2010-09-22    ARWILK    Bug Id: 89622: Incremented IFSCliMgrOCX.cab version to 4,0,0,28 in getClientMgrObjectStr.
 *  -------------------------------------------------------------------------------------------------
 */
 
 
package ifs.docmaw; 
 
import ifs.fnd.service.*;
import ifs.fnd.asp.*;
import java.io.*;
import java.util.*;
import java.security.*;

public class DocmawUtil
{

   /**
    *  Text Separator: '^'
    */
   public static final char TEXT_SEPARATOR = IfsNames.textSeparator;


   /**
    *  Field Separator: (char)31
    */
   public static final char FIELD_SEPARATOR = IfsNames.fieldSeparator;


   /**
    *  Record Separator: (char)30
    */
   public static final char RECORD_SEPARATOR = IfsNames.recordSeparator;


   /**
    *  Group Separator: (char)29
    */
   public static final char GROUP_SEPARATOR = IfsNames.groupSeparator;


   /**
    *  File Separator (char)28
    */
   public static final char FILE_SEPARATOR = IfsNames.fileSeparator;


   /**
    *  Allowed characters in random filename
    *  hex_chars[] is a character array with the allowed 16 (yes, must be 16) characters:
    */

   private static char hex_chars[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

   /**
    *  Secure random object for generating a cryptographically
    *  strong pseudo-random number
    */
   private static SecureRandom secure_random = new SecureRandom();


   /**
    *  Builds an HTML object string for embedding the client manager ocx.
    *
    *  @return  HTML object string for embedding the client manager ocx
    */
   public static String getClientMgrObjectStr()
   {
      StringBuffer ocx_str = new StringBuffer();
      ocx_str.append("<DIV style=\"DOCMAWEDMMACROVISIBILITY: hidden\">\n"); 
      ocx_str.append("<OBJECT CLASSID=\"CLSID:25988E0B-4429-4A73-A2B5-7153A68155C7\" id=oCliMgr  codebase=\"IFSCliMgrOCX.CAB#version=4,0,0,30\" style=\"DOCMAWEDMMACROLEFT: 0px; TOP: 0px\" VIEWASTEXT>\n");                                              
      ocx_str.append("<PARAM NAME=\"_ExtentX\" VALUE=\"0\">\n");
      ocx_str.append("<PARAM NAME=\"_ExtentY\" VALUE=\"0\"></OBJECT>\n");
      ocx_str.append("</DIV>\n");
      return ocx_str.toString();
   }

   //Bug Id 83898, Start 
   /**
    * Builds an HTML object string for embedding the drop area for document aware pages.
    * @return  HTML object string for embedding the drop Area.
   */
   public static String drawDnDArea(String classstyle, String codebase_path, String id)
   {
     StringBuffer dnd_str = new StringBuffer();

     dnd_str.append("           <script LANGUAGE=\"JavaScript\" SRC=\""+codebase_path+"LoadDocmawActiveX.js\"></script>\n");
     dnd_str.append("           <a id=\"DragDropAnchor_"+id+ "\">\n"); 
     dnd_str.append("               <script LANGUAGE=\"JavaScript\">\n");
     dnd_str.append("                   create_dragdrop_activex(\"DragDropAnchor_"+id+"\",\"DropArea_"+id+"\",\"" + classstyle + "\",\"C2F79C99-53F3-42ED-9DD5-5357214ADDD1\",\"" + codebase_path + "IFSDropArea.CAB#version=2,0,0,0\",\"100%\",\"100%\");\n"); 
     dnd_str.append("               </script>\n");
     dnd_str.append("           </a>");

     return dnd_str.toString();
   }
   //Bug Id 83898, End
   public static String drawDnDArea(String classstyle, String codebase_path)
   {
     StringBuffer dnd_str = new StringBuffer();

     dnd_str.append("           <script LANGUAGE=\"JavaScript\" SRC=\""+codebase_path+"LoadDocmawActiveX.js\"></script>\n");
     dnd_str.append("           <a id=\"DragDropAnchor\">\n");
     dnd_str.append("               <script LANGUAGE=\"JavaScript\">\n");
     dnd_str.append("                   create_dragdrop_activex(\"DragDropAnchor\",\"DropArea\",\"" + classstyle + "\",\"C2F79C99-53F3-42ED-9DD5-5357214ADDD1\",\"" + codebase_path + "IFSDropArea.CAB#version=2,0,0,0\",\"100%\",\"100%\");\n");
     dnd_str.append("               </script>\n");
     dnd_str.append("           </a>");

     return dnd_str.toString();
   }

   public static String writeOleDragOverFunction(String drop_here, String accepted, String illegal)
   {
      StringBuffer dnd_str = new StringBuffer();
      
      dnd_str.append("var bFilesDropped = false;\n\n");
      
      dnd_str.append("var vbCFFiles = 15;\n");
      
      dnd_str.append("\n");
      
      dnd_str.append("var vbDropEffectNone = 0;\n");
      dnd_str.append("var vbDropEffectCopy = 1;\n");
      dnd_str.append("var vbDropEffectMove = 2;\n");
      
      dnd_str.append("\n");
     
      dnd_str.append("  var oldBackColor = document.form.DropArea.Backcolor;\n");
      dnd_str.append("  var oldForeColor = document.form.DropArea.Forecolor;\n");

      dnd_str.append("\n");

      dnd_str.append("document.form.DropArea.Status = \"" + drop_here + "\";\n");

      dnd_str.append("function document.form.DropArea::OLEDragOver(data, effect, button, shift, x, y, state)\n");
      dnd_str.append("{\n");
      dnd_str.append(" var noOfFiles = 0;\n");
      dnd_str.append("   if (state == 0)\n");
      dnd_str.append("   {\n");
      dnd_str.append("      if(data.GetFormat(vbCFFiles)) // File dragged over drop area\n");
      dnd_str.append("      {\n");

      dnd_str.append("         var e = new Enumerator(data.Files);\n");

      dnd_str.append("         while(!e.atEnd()){\n");
      dnd_str.append("            noOfFiles++;\n");
      dnd_str.append("            e.moveNext();\n");
      dnd_str.append("         }\n");

      dnd_str.append("          document.form.DropArea.Backcolor = 0xDF967A;\n");
      dnd_str.append("          bFilesDropped = true;\n\n");
      dnd_str.append("          effect = vbDropEffectCopy;\n\n");
      dnd_str.append("          document.form.DropArea.Status = \"" + accepted + "\" + \": \" + noOfFiles;\n");
     
      dnd_str.append("      } else {\n");
     
      dnd_str.append("          document.form.DropArea.Backcolor = 0x0000ff;\n");
      dnd_str.append("          bFilesDropped = false;\n\n");
      dnd_str.append("          effect = vbDropEffectNone;\n\n");
      dnd_str.append("          document.form.DropArea.Status = \"" + illegal + "\";\n");
      dnd_str.append("      }\n");
     
      dnd_str.append("   }\n");
     
      dnd_str.append("   if (state == 1)\n");
      dnd_str.append("   {\n");
      dnd_str.append("      document.form.DropArea.Backcolor = oldBackColor;\n");
      dnd_str.append("      document.form.DropArea.Status = \"" + drop_here + "\";\n");
      dnd_str.append("   }\n");

      dnd_str.append("}\n");
     
      return dnd_str.toString();
   }

   //Bug Id 83898, Start
   public static String writeOleDragOverFunction(String drop_here, String accepted, String illegal, String id)
   {
      StringBuffer dnd_str = new StringBuffer();
      
      dnd_str.append("var bFilesDropped = false;\n\n");
      
      dnd_str.append("var vbCFFiles = 15;\n");
      
      dnd_str.append("\n");
      
      dnd_str.append("var vbDropEffectNone = 0;\n");
      dnd_str.append("var vbDropEffectCopy = 1;\n");
      dnd_str.append("var vbDropEffectMove = 2;\n");
      
      dnd_str.append("\n");
     
      dnd_str.append("  var oldBackColor = document.form.DropArea_"+id+".Backcolor;\n");
      dnd_str.append("  var oldForeColor = document.form.DropArea_"+id+".Forecolor;\n");

      dnd_str.append("\n");

      dnd_str.append("document.form.DropArea_"+id+".Status = \"" + drop_here + "\";\n");

      dnd_str.append("function document.form.DropArea_"+id+"::OLEDragOver(data, effect, button, shift, x, y, state)\n");
      dnd_str.append("{\n");
      dnd_str.append(" var noOfFiles = 0;\n");
      dnd_str.append("   if (state == 0)\n");
      dnd_str.append("   {\n");
      dnd_str.append("      if(data.GetFormat(vbCFFiles)) // File dragged over drop area\n");
      dnd_str.append("      {\n");

      dnd_str.append("         var e = new Enumerator(data.Files);\n");

      dnd_str.append("         while(!e.atEnd()){\n");
      dnd_str.append("            noOfFiles++;\n");
      dnd_str.append("            e.moveNext();\n");
      dnd_str.append("         }\n");

      dnd_str.append("          document.form.DropArea_"+id+".Backcolor = 0xDF967A;\n");
      dnd_str.append("          bFilesDropped = true;\n\n");
      dnd_str.append("          effect = vbDropEffectCopy;\n\n");
      dnd_str.append("          document.form.DropArea_"+id+".Status = \"" + accepted + "\" + \": \" + noOfFiles;\n");
     
      dnd_str.append("      } else {\n");
     
      dnd_str.append("          document.form.DropArea_"+id+".Backcolor = 0x0000ff;\n");
      dnd_str.append("          bFilesDropped = false;\n\n");
      dnd_str.append("          effect = vbDropEffectNone;\n\n");
      dnd_str.append("          document.form.DropArea_"+id+".Status = \"" + illegal + "\";\n");
      dnd_str.append("      }\n");
     
      dnd_str.append("   }\n");
     
      dnd_str.append("   if (state == 1)\n");
      dnd_str.append("   {\n");
      dnd_str.append("      document.form.DropArea_"+id+".Backcolor = oldBackColor;\n");
      dnd_str.append("      document.form.DropArea_"+id+".Status = \"" + drop_here + "\";\n");
      dnd_str.append("   }\n");

      dnd_str.append("}\n");
     
      return dnd_str.toString();
   }
   //Bug Id 83898, End

   public static String drawStatusField(int width)
   {
      return drawStatusFieldInternal(width, "Center");
   }

   public static String drawStatusField(int width, String text_align)
   {
      return drawStatusFieldInternal(width, text_align);
   }

   private static String drawStatusFieldInternal(int width, String text_align)
   {
  	return "<input type=\"text\" name=\"DROP_STATUS\" size=\"" + width +
           "\" readOnly=\"true\" style=\"text-align: " + text_align +
           "\"  class=\"normalTextValue\">";
   }

    /**
     *  Returns the name of the file or directory denoted by this abstract
     *  pathname. The names (directories and file) in the abstract pathname
     *  may be separated by either "\" or "/". If the pathname's name sequence
     *  is empty, then the empty string is returned.
     *
     *  @return  The name of the file or directory denoted by this abstract
     *           pathname, or the empty string if this pathname's name sequence
     *           is empty
     */
   public static String getFileName(String file_path)
   {
      String file_separator = System.getProperty("file.separator"); //Bug Id 81741
      if (file_path == null)
         return null;
      else if (file_path.substring(file_path.length() - 1).equals(file_separator)) //Bug Id 81741, substitute file_separator to System.getProperty("file.separator")
         return null;
      else
      {
	 //Bug Id 81741, Start 
	 if ("/".equals(file_separator) && (file_path.indexOf(file_separator) == -1))
	 {
	     file_path  = file_path.replaceAll("\\\\", "/");
	 }
	 //Bug Id 81741, End
	 return (new File(file_path)).getName();
      }
         
   }


   /**
    *  Returns the path denoted by this abstract pathname. The names (directories)
    *  in the abstract pathname may be separated by either "\" or "/". If the
    *  pathname's name sequence is empty, then the empty string is returned.
    *
    *  @return  The name of the file or directory denoted by this abstract
    *           pathname, or the empty string if this pathname's name sequence
    *           is empty
    */
   public static String getPath(String file_path)
   {
      String file_separator = System.getProperty("file.separator"); //Bug Id 81741
      if (file_path == null)
         return null;
      else if (file_path.substring(file_path.length() - 1).equals(file_separator))//Bug Id 81741, substitute file_separator to System.getProperty("file.separator")
         return file_path;
      else
      {
      	 //Bug Id 81741, Start 
	 if ("/".equals(file_separator) && (file_path.indexOf(file_separator) == -1))
	 {
	     file_path  = file_path.replaceAll("\\\\", "/");
	     return ((new File(file_path)).getParent()).replaceAll("/","\\\\");
	 }
	 //Bug Id 81741, End
	 else
            return (new File(file_path)).getParent();
      }
   }


   /**
    *  Returns the base name of the file denoted by this abstract
    *  pathname. This is component up to the last "." character in
    *  the file name.
    *
    *  @return  Returns the base name of the file denoted by this
    *           abstract pathname.
    */
   public static String getBaseFileName(String file_path)
   {
      String file_name = getFileName(file_path);
      int index = file_name.lastIndexOf(46);

      if (index >= 0)
         return file_name.substring(0, index);
      else
         return file_name.substring(0);
   }


   /**
    *  Returns the file extention of file denoted by this abstract
    *  pathname. This is component after the last "." character in
    *  the file name.
    *
    *  @return  Returns the file extention of file denoted by this
    *           abstract pathname, or an empty string if the file
    *           does not have an extention
    */
   public static String getFileExtention(String file_path)
   {
      String file_name = getFileName(file_path);
      int index = file_name.lastIndexOf(46);

      if (index >= 0)
         return file_name.substring(index + 1);

      return "";
   }



   /**
    * Returns a unique file name 40 characters long. The file name 
    * is created using a series of randomly generated bytes using 
    * java.security.SecureRandom. This class provides a cryptographically 
    * strong random number generator, that is used in generating the
    * initial byte sequence.
    * 
    */
   public synchronized static String getRandomFilename()
   {
      // Reserve space to fill with random bytes

      byte bytes[] = new byte[20];

      secure_random.nextBytes(bytes);

      return convertRandomBytesToFileName(bytes);
   }


   /**
    * 
    */
   private static String convertRandomBytesToFileName(byte[] bytes)
   {
      char char_array[] = new char[bytes.length * 2];

      for (int i = 0; i < bytes.length; i++)
      {
         int b = bytes[i];
         b = (b < 0) ? 256 + b : b;
         char_array[i * 2] = hex_chars[b & 0xf];
         b = b >> 4;
         char_array[(i * 2) + 1] = hex_chars[b & 0xf];
      }
      return new String(char_array);
   }



   /**
    * creates an object from KeyRef class
    */
   public static KeyRef getKeyRefObject(String keyRefString)
   {
      return new KeyRef(keyRefString);
   }


   public static void addToAttribute(StringBuffer attr, String name, String value)
   {
      attr.append(name);
      attr.append(FIELD_SEPARATOR);
      attr.append(value);
      attr.append(RECORD_SEPARATOR);
   }


   /**
    * Returns the attribute value of the named attribute in the
    * give attribute string
    */
   public static String getAttributeValue(String attr_string, String attr_name)
   {
      StringTokenizer st = new StringTokenizer(attr_string, "^");

      attr_name += "=";
      while (st.hasMoreTokens())
      {
         String str = st.nextToken();
         if (str.startsWith(attr_name))
         {
            return str.substring(attr_name.length());
         }
      }
      return "";
   }


   /**
    * Returns the item value of the named item in the
    * give items string
    */
   public static String getItemValue(String item_string, String item_name)
   {
      StringTokenizer st = new StringTokenizer(item_string, Character.toString(RECORD_SEPARATOR));

      item_name += FIELD_SEPARATOR;
      while (st.hasMoreTokens())
      {
         String str = st.nextToken();
         if (str.startsWith(item_name))
         {
            return str.substring(item_name.length());
         }
      }
      return "";
   }


   /**
    *  Trims the specified character from the left side of
    *  the given string
    */
   public static String leftTrim(String str, char trim)
   {
      if (str == null || str.length() == 0)
         return str;

      char[] chars = str.toCharArray();

      int i = 0;
      while (chars[i] == trim)
         i++;

      int count = str.length() - i;
      char[] new_chars = new char[count];

      System.arraycopy(chars, i, new_chars, 0, count);
      return String.valueOf(new_chars);
   }


   /**
    *  Trims the specified character from the right side of
    *  the given string
    */
   public static String rightTrim(String str, char trim)
   {
      if (str == null || str.length() == 0)
         return str;

      char[] chars = str.toCharArray();

      int i = str.length() - 1;
      while (chars[i] == trim)
         i--;

      int count = i + 1;
      char[] new_chars = new char[count];

      System.arraycopy(chars, 0, new_chars, 0, count);
      return String.valueOf(new_chars);
   }

   public static boolean  isProjInstalled(ASPManager mgr)   {

   
      String dummy1;

      ASPBlock tempblk = mgr.newASPBlock("PROJ_TEMP");
      tempblk.addField("PROJ_LU_NAME");
      tempblk.addField("PROJ_DUMMY1");

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();     

      trans.clear(); 
      ASPCommand cmd = trans.addCustomFunction("PROJQUERY", "TRANSACTION_SYS.Logical_Unit_Is_Installed_Num", "PROJ_DUMMY1"); 
      cmd.addParameter("PROJ_LU_NAME","Project");

      trans = mgr.performConfig(trans);
      
      dummy1 = trans.getValue("PROJQUERY/DATA/PROJ_DUMMY1");
     
      trans.clear();

       if ("1".equals(dummy1))
          return true;
      else
          return false;  
        
   }


}

