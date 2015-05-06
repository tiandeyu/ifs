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
 * File        : Html2Rtf.java
 * Description : Designed to convert html to REF
 * Notes       : The convertion is not a 100% compatible.
 * ----------------------------------------------------------------------------
 * Modified    :
 *    buhilk   2006-11-07   Created
 *
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/09/03 buhilk bug 92381 added debug variable to avoid unnessary system outs
 *         2008/09/10 sadhlk
 * Bug 76949, F1PR461 - Notes feature in Web client.
 * 
*/

package ifs.fnd.pages;

import ifs.fnd.util.Str;

import java.awt.Color;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;

class Html2Rtf
{
   public static boolean DEBUG = ifs.fnd.service.Util.isDebugEnabled("ifs.fnd.pages.Html2Rtf");

   StringBuffer headbuffer;
   StringBuffer colorbuffer;
   StringBuffer bodybuffer;
   HashMap color_map;
   HashMap font_type_map;
   StringBuffer hyperlinkBuffer;
   int par_count;
   boolean hasContent;
   StringBuffer htmlbuf;
   boolean images_available = false;

 /**
   * Inner class which keeps track of all the html elements that were translated
   */
   private class RTFStateMachine
   {
      private String fontname;
      private int fontsize;
      private Hashtable fontTab;
      private int font_count;
      private boolean bold;
      private boolean italic;
      private boolean underline;
      private boolean strike;
      private int color_count;
      private boolean forecolor;
      private boolean backcolor;
      private String url;
      
      /**
       * Inner class constructor
       */
      RTFStateMachine() 
      {
         fontname = "";
         bold = false;
         italic = false;
         underline = false;
         strike = false;
         color_count = 0;
         forecolor = false;
         backcolor = false;
         fontTab = new Hashtable();
         font_count = 2;
         url = null;
      }

      /**
       * Translates html to rtf elements
       * @ param AttributeSet
       * @ param Element
       */
      public void updateState(AttributeSet attributeset, Element element) 
      {

         String s = element.getName().toLowerCase();
         if( s.equals("br"))
            return;
         
         url = setHyperLink(attributeset, url);

         if(color_count == 0)
            setInitialColorValues();
         
         if(Str.isEmpty(fontname))
            fontname = setFontTable(attributeset, fontname); 
         
         fontname = setFontName(attributeset,fontname,"\\f",StyleConstants.FontFamily);         
         fontsize = setFontSize(attributeset,fontsize,"\\fs",StyleConstants.FontSize);

         if(!bold)
            bold = setBooleanTag(attributeset, bold, "\\b ", StyleConstants.Bold);

         if(!italic)
            italic = setBooleanTag(attributeset, italic, "\\i ", StyleConstants.Italic);

         if(!underline)
            underline = setBooleanTag(attributeset, underline, "\\ul ", StyleConstants.Underline);

         if(!strike)
            strike = setBooleanTag(attributeset, strike, "\\strike ", StyleConstants.StrikeThrough);

         if(!forecolor)
          forecolor = setColor(StyleConstants.getForeground(attributeset), forecolor, "\\cf"); 

         if(!backcolor)
          backcolor = setColor(StyleConstants.getBackground(attributeset), backcolor, "\\highlight");

      }
      
      /**
       * This method looks for a given style within an AttributeSet and if that style is available
       * the html style is then translated to the given rtf style.
       * @ param AttributeSet
       * @ param tag
       * @ param value
       * @ param style
       * @ return boolean
       */
      private boolean setBooleanTag(AttributeSet attributeset, boolean tag, String value,  Object style)
      {
         Object obj = attributeset.getAttribute(style);
         boolean s = tag;
         if(obj==null)
            return s;
         String s1 = obj.toString();
         if(!Str.isEmpty(s1) && "TRUE".equals(s1.toUpperCase()))
         {
            bodybuffer.append(value);
            s = true;
         }
         return s;
      }
      
      private String setFontName(AttributeSet attributeset, String tag, String value, Object style)
      {
         Object obj = attributeset.getAttribute(style);
         String s = tag;
         String font_type_val = "\\fnil\\fcharset0";
         if(obj==null)
            return s;
         String tbl_num =  obj.toString();
         if(!Str.isEmpty(tbl_num) /*&& "TRUE".equals(s1.toUpperCase())*/)
         {  
            if(!fontTab.containsKey(obj)){
               tbl_num = String.valueOf(++font_count);
               fontTab.put(obj,(Object) tbl_num);
               if(font_type_map.get(obj) !=null)
                  font_type_val = font_type_map.get(obj).toString();
                  
               headbuffer.append("{\\f"+tbl_num+font_type_val+" "+ obj.toString()+";}");
            }
            tbl_num = fontTab.get(obj).toString();
            if(!Str.isEmpty(url))
               hyperlinkBuffer.append(value+tbl_num);
            else
               bodybuffer.append(value+tbl_num);
            
            s = obj.toString();
         }
         return s;
      }
      
      private int setFontSize(AttributeSet attributeset, int tag, String value, Object style)
      {
         Object obj = attributeset.getAttribute(style);
         int s = tag;
         if(obj==null)
            return s;
         int s1 =  Integer.parseInt(obj.toString());
         if(s1 >0 /*&& "TRUE".equals(s1.toUpperCase())*/)
         { 
            if(!Str.isEmpty(url))
               hyperlinkBuffer.append(value+s1*2+" ");
            else
               bodybuffer.append(value+s1*2+" ");
            s = s1;
         }
         else
            bodybuffer.append(value+s*2+" ");
         return s;
      }
      
      /**
       * This method sets the font table for the RTF format. If a font does not exist the font will be search from the 
       * html code.
       * @ param AttributeSet
       * @ param String
       * @ return String
       */
      private String setFontTable(AttributeSet attributeset, String fontname)
      {
         String s = fontname;
         String font_type_val = "\\fnil\\fcharset0";
         Object obj = attributeset.getAttribute(StyleConstants.FontFamily);
         if(obj==null)
            return s;
         String s1 = (String) obj;
         if(!Str.isEmpty(s1))
         {  
            if(font_type_map.get(obj) !=null)
               font_type_val = font_type_map.get(obj).toString();
            headbuffer.append("{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\froman\\fcharset0 Times New Roman;}{\\f1\\fnil\\fcharset0 Symbol;}{\\f2"+ font_type_val+" "+s1+";}");
            s = s1;
         }
         return s;
      }

      /**
       * Sets the color table for the RTF format.
       * @ param Object
       * @ param boolean
       * @ param String
       * @ return boolean
       */
      private boolean setColor(Object obj, boolean setcolor, String value)
      {
         boolean s = setcolor;
         
         if(obj!=null)
         {
            Color c = (Color) obj;
            int red = c.getRed();
            int green = c.getGreen();
            int blue = c.getBlue();
            if(red!=0 || green!=0 || blue!=0)
            {
               String color_key = "\\red"+red+"\\green"+green+"\\blue"+blue;
               String tbl_num = (String) color_map.get(color_key);
               if(Str.isEmpty(tbl_num))
               {
                  tbl_num = String.valueOf(++color_count);
                  color_map.put((Object) color_key, (Object) tbl_num);
                  colorbuffer.append(color_key+";");
               }
               if(!Str.isEmpty(url))
                  hyperlinkBuffer.append(value+tbl_num+" ");
               else
                  bodybuffer.append(value+tbl_num+" ");
               
               s = true;
            }
         }
         
         return s;
      }
            
      private void setInitialColorValues()
      {
         color_map.put((Object) "\\red0\\green0\\blue0", (Object) "1");
         color_map.put((Object) "\\red0\\green0\\blue255", (Object) "2");
         colorbuffer.append("\\red0\\green0\\blue0;\\red0\\green0\\blue255;");
         color_count = 2;
         
      }
      private String setHyperLink(AttributeSet attributeset, String url)
      {
         Object obj = attributeset.getAttribute(HTML.getTag("a"));
         if(obj == null)
            return url;
         
         SimpleAttributeSet simpleAttrSet = (SimpleAttributeSet)obj;
         
         Object url_val = simpleAttrSet.getAttribute(HTML.getAttributeKey("href"));
         if(url_val == null)
            return url;
         url = url_val.toString();
         return url;
      }
      
      private String appendHyperlink(String url_text)
      {
         String hyperlink_val = "{\\field{\\*\\fldinst{" + hyperlinkBuffer.toString() + "HYPERLINK \"" +url+ "\"}}{\\fldrslt {\\ul\\f0\\fs24\\cf2" +hyperlinkBuffer.toString() + url_text+"}}}";
         return hyperlink_val;
      }

      /**
       * Closes any open rtf tags during the HTML ==> RTF convertion
       */
      public void closeTags(StringBuffer stringbuffer)
      {
         if(bold)
         {
            stringbuffer.append("\\b0 ");
            bold = false;
         }
         
         if(italic)
         {
            stringbuffer.append("\\i0 ");
            italic = false;
         }

         if(underline)
         {
            stringbuffer.append("\\ulnone ");
            underline = false;
         }

         if(strike)
         {
            stringbuffer.append("\\strike0 ");
            strike = false;
         }

         if(forecolor)
         {
            stringbuffer.append("\\cf0 ");
            forecolor = false;
         }

         if(backcolor)
         {
            stringbuffer.append("\\highlight0 ");
            backcolor = false;
         }
         
         if(url != null)
            url = null;
         
         clearHyperlinkBuffer();
      }
      
      public void clearHyperlinkBuffer()
      {
          hyperlinkBuffer.setLength(0);
      }
   }

   public Html2Rtf()
   {
      System.setProperty( "java.awt.headless", "true" );
      headbuffer = new StringBuffer();
      colorbuffer = new StringBuffer();
      bodybuffer = new StringBuffer();
      hyperlinkBuffer = new StringBuffer();
      color_map = new HashMap();
      font_type_map = new HashMap();
      par_count = 0;
      hasContent = false;
      this.initializeFontTypeMap();
      htmlbuf = new StringBuffer();
   }
   
    Html2Rtf(boolean images_available)
   {
      System.setProperty( "java.awt.headless", "true" );
      headbuffer = new StringBuffer();
      colorbuffer = new StringBuffer();
      bodybuffer = new StringBuffer();
      hyperlinkBuffer = new StringBuffer();
      color_map = new HashMap();
      font_type_map = new HashMap();
      par_count = 0;
      hasContent = false;
      this.initializeFontTypeMap();
      htmlbuf = new StringBuffer();
      this.images_available = images_available;
   }
   
   private void initializeFontTypeMap()
   {
      font_type_map.put((Object)"Arial",(Object)"\\fswiss\\fcharset0");
      font_type_map.put((Object)"Times New Roman",(Object)"\\froman\\fcharset0");
      font_type_map.put((Object)"Courier New",(Object)"\\fmodern\\fcharset0");
      font_type_map.put((Object)"Garamond",(Object)"\\froman\\fcharset0");
      font_type_map.put((Object)"Helvetica",(Object)"\\fswiss\\fcharset0");
      font_type_map.put((Object)"MS Serif",(Object)"\\froman\\fcharset0");
      font_type_map.put((Object)"Comic Sans MS",(Object)"\\fnil\\fcharset0");
      
   }
   
   /**
    * Parses a html string into an rtf based string.
    * @ param String
    * @ return String
    */
   String html2rtf( final String html ) 
   {
      if(DEBUG) System.out.println( "String original: " + html);
      String body = convertHTMLStringToRTF( html );
      if(!hasContent)
         body = "";
      return body;
   }
   
   /**
    * Creates an HTMLDocument using the html string passed in as a parameter with the help of a 
    * HTMLEditorKit and scans the document for possible convertions. Returna a converted converted string in 
    * RTF format.
    * @ param String
    * @ retun String
    */
   String convertHTMLStringToRTF(final String s) 
   {
      RTFStateMachine rtfstatemachine = new RTFStateMachine();
      HTMLEditorKit htmleditorkit = new HTMLEditorKit();
      HTMLDocument htmldocument = new HTMLDocument();
      readString(s, htmldocument, htmleditorkit);
      return scanDocument(htmldocument, rtfstatemachine);
   }
   
       /**
     * Converte RTF de um InputStream para uma String HTML
     * @param input
     * @return
     * @throws IOException
     */
    protected String convertHTMLStringToRTF(InputStream input) throws IOException {
        BufferedReader strm = new BufferedReader(new InputStreamReader(input));
        StringBuffer sb = new StringBuffer();
        int s;
        while ((s = strm.read()) != -1) {
            sb.append((char) s);
        }
        return convertHTMLStringToRTF(sb.toString());
    }

   /**
    * Read the HTMLDocument
    */
   private void readString(String s, HTMLDocument document, HTMLEditorKit htmleditorkit) 
   {

      s = encodeBraces(s);
      if(images_available)
         s = findImages(s);
      s = setSectionEnds(s);
      s = setBulletsTag(s);
      
      try {
         ByteArrayInputStream bytearrayinputstream =
         new ByteArrayInputStream(s.getBytes());
         htmleditorkit.read(bytearrayinputstream, document, 0);
      } 
      catch (Exception exception)
      {
         exception.printStackTrace( System.out );
         return;
      }
   }
   
   private String setSectionEnds(String s)
   {
      return s.replace("<!--SectionEnd-->","\\pard").replace("<br>","\\par\\r\\n").replace("<BR>","\\par\\r\\n");
   }
   
   private String setBulletsTag(String s)
   {
      int endIndex = s.indexOf("<!--BulletStart-->");
      int startIndex = 0;
      String txt = "";
      boolean is_upper_case = true;
      if(s.contains("<font"))
         is_upper_case = false;
      if(endIndex == -1)
         return s;
      
      String editedTxt = "";
      do{
         endIndex = endIndex + startIndex;
         txt = s.substring(startIndex, endIndex);//st.nextToken();
         editedTxt += replaceBulletTags(txt,is_upper_case);
         startIndex = endIndex + 18;
         if(s.length()<= startIndex)
            break;
         txt = s.substring(startIndex );
         endIndex = txt.indexOf("<!--BulletStart-->");
         if(endIndex == -1)
            endIndex = txt.length();
      }while(true);
      return editedTxt;      
   }
   
   private String replaceBulletTags(String txt, boolean is_upper_case)
   {
      int index = txt.indexOf("·&nbsp;&nbsp;&nbsp;&nbsp;");
      if(index == -1)
         return txt;
      int bulletEndIndex = (is_upper_case)? txt.indexOf("<FONT",index):txt.indexOf("<font",index);
      if(bulletEndIndex != -1){
         String replacingString = txt.substring(0,bulletEndIndex);
      txt = txt.replaceFirst(replacingString,"{\\\\pntext\\\\f1\\\\'B7\\\\tab}{\\\\*\\\\pn\\\\pnlvlblt\\\\pnf1\\\\pnindent0{\\\\pntxtb\\\\'B7}}");
      }
      else
         return txt;
      return replaceRemainingBullets(txt, is_upper_case);
   }
   
   private String replaceRemainingBullets(String txt, boolean is_upper_case)
   {
      int index = txt.indexOf("·&nbsp;&nbsp;&nbsp;&nbsp;");
      String replacingString = "";
      
      if(index ==-1)
         return txt;
      int bulletEndIndex = (is_upper_case)? txt.indexOf("<FONT",index):txt.indexOf("<font",index);
      if(bulletEndIndex != -1){
         int bulletStartIndex = (is_upper_case)?txt.substring(0,index).lastIndexOf("</FONT>"):txt.substring(0,index).lastIndexOf("</font>");
         if(bulletEndIndex != -1 && bulletStartIndex != -1)
         {  bulletStartIndex = bulletStartIndex + 7;
            replacingString = txt.substring(bulletStartIndex,bulletEndIndex);
            txt = txt.replace(replacingString,"{\\pntext\\f1\\'B7\\tab}");
         }
      }
      else
         return txt;
      
     return replaceRemainingBullets(txt, is_upper_case);
   }

      public String decodeHTML( String s )
   {
      try
      {
         if ( Str.isEmpty(s) ) return s;

         htmlbuf.setLength(0);
         int len = s.length();

         for ( int i=0; i<len; i++ )
         {
            char ch = s.charAt(i);

            if ( ch=='&' )
            {
               if ( i<len-3 )
               {
                  char ch1 = s.charAt(i+1);
                  char ch2 = s.charAt(i+2);
                  char ch3 = s.charAt(i+3);

                  if ( ch3==';' )
                  {
                     if ( ch1=='l' && ch2=='t' )
                     {
                        htmlbuf.append('<');
                        i = i+3;
                        continue;
                     }
                     else if ( ch1=='g' && ch2=='t' )
                     {
                        htmlbuf.append('>');
                        i = i+3;
                        continue;
                     }
                  }
                  else if ( i<len-4 )
                  {
                     char ch4 = s.charAt(i+4);

                     if ( ch1=='a' && ch2=='m' && ch3=='p' && ch4==';' )
                     {
                        htmlbuf.append('&');
                        i = i+4;
                        continue;
                     }
                  }
               }
            }
            htmlbuf.append(ch);
         }
         return htmlbuf.toString();
      }
      catch ( Exception e )
      {
         e.printStackTrace(System.out);
         return null;
      }
   }
      
   private String encodeBraces(String s)
   {
       int beginIndex = s.indexOf("<!-- imagedata");
       String htmlString = "";
       String imageData = "";
       if(beginIndex != -1)
       {
          htmlString = s.substring(0,beginIndex);
          imageData = s.substring(beginIndex, s.length());
          
          htmlString = htmlString.replace("{","\\{").replace("}","\\}");
          
          return htmlString + imageData;
          
       }
       else       
         return s.replace("{","\\{").replace("}","\\}");
   }
   
   private String findImages(String s)
   {
      StringBuffer sb = new StringBuffer(s);
      int beginIndex = sb.indexOf("<!-- image value=");
      int endIndex = -1;
      String imageData = "";
      
      if(beginIndex == -1)
         return s;
      
      int key = getImageKey(sb.substring(beginIndex+17));
      
      if(key !=-1){
         NoteBook.NoteImage noteImage = (NoteBook.NoteImage)NoteBook.getImageTable().get((Object)new Integer(key));
         imageData = noteImage.getImageData();
         NoteBook.getImageTable().remove((Object)new Integer(key));
      }
      
      sb.insert(beginIndex,imageData);
      StringBuffer newBuff = new StringBuffer(sb.substring(0,beginIndex+17+imageData.length()));
      newBuff.append(findImages(sb.substring(beginIndex+17+imageData.length())));
      sb = newBuff;
      return sb.toString();
   }
   
   private int getImageKey(String s)
   {
      String numVal = "";
      char letter =' ';
      int number = -1;
      
      try
      {
         for(int i=0; i<s.length();i++)
         {
            letter = s.charAt(i);
            int letterVal = letter;
            if(letterVal >47 && letterVal <58)
               numVal += letter;
            else
               break;         
         }

         if(!Str.isEmpty(numVal))
            number = Integer.parseInt(numVal);
         
         return number;
      }
      catch(NumberFormatException e)
      {
         return -1;
      }      
   }

   /**
    * Scan HTMLDocument and parse rtf string
    * @ param HTMLDocument
    * @ param RTFStateMachine
    * @ return String
    */
   private String scanDocument(HTMLDocument document, RTFStateMachine rtfstatemachine) 
   {
      String s = "";
      try {
         Element element = document.getDefaultRootElement();
         bodybuffer.append("\\viewkind4\\uc1\\pard\\f0\\fs20 ");
         recurseElements(element, document, rtfstatemachine);
         if(Str.isEmpty(headbuffer.toString()))
            headbuffer.append("{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\fnil\\fcharset0 Comic Sans MS;}}\n");
         else
            headbuffer.append("}\n");
         if(!Str.isEmpty(colorbuffer.toString()))
            headbuffer.append("{\\colortbl ;").append(colorbuffer).append("}\n");
         bodybuffer.insert(bodybuffer.lastIndexOf("\\par"),"\\fs20").append("}\n");
         s = headbuffer.append(bodybuffer).toString();
      } 
      catch (Exception exception) 
      {
         exception.printStackTrace( System.out );
         return s;
      }
      return s;
   }

   /**
    * Loops through an element and scans html to convert.
    * @ param Element
    * @ param HTMLDocument
    * @ param RTFStateMachine
    */
   private void recurseElements(Element element, HTMLDocument document, RTFStateMachine rtfstatemachine) 
   {      
      for (int i = 0; i < element.getElementCount(); i++) 
      {
         Element element1 = element.getElement(i);
         scanAttributes(element1, document, rtfstatemachine);
         recurseElements(element1, document, rtfstatemachine);            
      }        
   }

   /**
    * Scans the attributes inside an element and convert the html contents into rtf.
    * @ param Element
    * @ param HTMLDocument
    * @ param RTFStateMachine
    */
   private void scanAttributes(Element element, HTMLDocument document, RTFStateMachine rtfstatemachine) 
   {       
      try {
         int i = element.getStartOffset();
         int j = element.getEndOffset();
         String s = document.getText(i, j - i);
         javax.swing.text.AttributeSet attributeset = element.getAttributes();
         if(!s.equalsIgnoreCase("<!--SectionEnd-->"))
            rtfstatemachine.updateState(attributeset, element); 
         String s1 = element.getName();
         if(s1.equalsIgnoreCase("br") && par_count==0)
         {
            bodybuffer.append("\\par\n");
            par_count++;
            return;
         }
         else if (s1.equalsIgnoreCase("content") && !s.equals("\n"))
         {
            if(!Str.isEmpty(s.trim())) 
            {
               par_count=0;
            }

            String y=null;
            byte [] byteString = s.getBytes();
            for(int k=0;k<byteString.length;k++)
            {
               if(byteString[k]== -96)
                  byteString[k] = 32;
            }
            y = new String(byteString);
            s = y;
            s = s.replaceAll("&nbsp;&nbsp;&nbsp;&nbsp;", "\\t").replaceAll("    ", "\\\\tab ");
            hasContent = (!hasContent)?true :hasContent;
            
            if(!Str.isEmpty(rtfstatemachine.url))
               bodybuffer.append(rtfstatemachine.appendHyperlink(s));
            else   
               bodybuffer.append(s);

         }
         rtfstatemachine.closeTags(bodybuffer);
      } 
      catch (BadLocationException badlocationexception) 
      {
         badlocationexception.printStackTrace( System.out );
         return;
      }
   }   
}
