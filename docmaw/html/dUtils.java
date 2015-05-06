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
 *  File         : dUtils.java
 *  Description  : This file contains a static class that handles some utilities
 *                 that help in writing HTML objects.
 *
 * DATE           USER          COMMENTS
 * 2005-11-30     SUKMLK        Created. Added methods to generate HTML attributes,
 *                              read static html files.
 * 
 */

package ifs.docmaw.html;

import java.io.*;
import java.util.StringTokenizer;

/**
 *
 * @author sukmlk
 */
public class dUtils
{
      
   /*
    * genStyleAttribute
    * This method takes in a name, value pair and returns a html style type
    * "name: value; " (with a space at the end). Note that if the value
    * is empty this method returns and empty string.
    */
   
   public static String genStyleAttribute(String styleProperty, String styleValue)
   {
      if (!styleValue.equals(""))
         return styleProperty + ": " + styleValue + "; ";
      else
         return "";
   }
   
   public static String genStyleAttribute(String styleProperty, int styleValue)
   {
      if (styleValue != 0)
         return styleProperty + ": " + styleValue + "; ";
      else
         return "";
   }
   
   
   /*
    * genHtmlAttribute
    * This method takes in a name, value pair and returns a html type
    * "name='value' " (with a space at the end). Note that if the value
    * is empty this method returns and empty string.
    */
    
   public static String genHtmlAttribute(String styleProperty, String htmlValue)
   {
      if (!htmlValue.equals(""))
         return styleProperty + "='" + htmlValue + "' ";
      else
         return "";
   }
   
   public static String genHtmlAttribute(String styleProperty, int htmlValue)
   {
      if (htmlValue != 0)
         return styleProperty + "='" + htmlValue + "' ";
      else
         return "";
   }
   
   /*
    * addLineBreak
    * Add's a '/n' at the end of the line.
    */
   
   public static String addLineBreak(String line)
   {
      return line + "\n";   
   }
   
   public static String getHtmlContentFromFile (String fileName)
   {    
      String fileContents = "";
      String line;
      try
      {  
         File myFile = new File(".");
         
         //System.out.println("-----[" + myFile.getAbsolutePath());
         //System.out.println(System.getProperty("user.dir"));
         
         FileReader fileReader = new FileReader (fileName);
         BufferedReader bufReader = new BufferedReader (fileReader);
         
         while (true)
         {
            line  = bufReader.readLine();
            if (line == null) 
               break;
            else
               fileContents += line + "\n";            
         }                                    
      }
      catch (Exception e)
      {
         System.out.println("-----------------------[Docmaw.HTML.dUtils] Could not open file.");
         e.printStackTrace();
      }
      return fileContents;
   }      
   
   private void debug(String value)
   {
      
   }
   
}
