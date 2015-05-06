
/* ----------------------------------------------------------------------------
 * Comments:
 * 2010/09/03 buhilk bug 92381 added debug variable to avoid unnessary system outs
 * 2009/03/05 rahelk Bug 81146 added method to convert RTF to plain text for use in comparison
 *         2008/09/10 sadhlk
 * Bug 76949, F1PR461 - Notes feature in Web client.
 *         2006/11/13 gegulk 
 * Modified updateState() to handle strike through text and modifiled class to specially handle TRF notes.
 *         2006/02/13 gegulk 
 * Added method insertTableTags() and used it to convert rtf table formatting to html.
 *         2006/01/09 riralk 
 * Used by ifs.fnd.pages.Help, made some changes to the original source to make it useful for "Help on Fields" functionality. Added style for <td> tags.
 * ----------------------------------------------------------------------------
 */



package ifs.fnd.pages;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import ifs.fnd.util.Str;
import java.util.Vector;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.rtf.RTFEditorKit;

/*  This class is used to convert RTF format text into HTML. The source is based on 
 *  on converter RTF2HTML, used for < href="http://webcat.sf.net">webcat</a >.
 *  WebCAT was written by < href="mailto:bmartins@xldb.di.fc.ul.pt">Bruno Martins</a>.<br/> 
 *  The Rtf2Html Class, is permitted under the terms of 
 *  < the href="http://www.opensource.org/licenses/bsd-license.php">BSD License</a >.  
 *
 *  @author < href="mailto:bmartins@xldb.di.fc.ul.pt">Bruno Martins</a >
 */

//public class Rtf2Html {
 public class Rtf2Html { //RIRALK: made the class friendly since its only used by our Help.java

    public static boolean DEBUG = ifs.fnd.service.Util.isDebugEnabled("ifs.fnd.pages.Rtf2Html");

    private boolean note_formatting = true;
    private String image_location = "note_image.png";
    private boolean deleteImages = false;
    private StringBuffer htmlbuf;
    private Vector colorTable;
    private NoteBook noteBook;
    private boolean images_available = false;
    
      private class HTMLStateMachine {

        private String alignNames[] = { "left", "center", "right" };
        public boolean acceptFonts;
        private String fontName;
        private Color color;
        private Color backColor;
        private int size;
        private int alignment;
        private boolean bold;
        private boolean italic;
        private boolean underline;
        private boolean strike;
        private double firstLineIndent;
        private double oldLeftIndent;
        private double oldRightIndent;
        private double leftIndent;
        private double rightIndent;
        private boolean firstLine;
        private boolean isIndentTableOpen;
        private String indentString;
        private int fontTabCount;
        private StringBuffer fontBuff;

        /**
         * Construtor Padr?
         */
        HTMLStateMachine() {
            acceptFonts = true;
            fontName = "";
            alignment = -1;
            fontTabCount = 0;
            fontBuff = new StringBuffer();
            isIndentTableOpen = false;
            indentString="";
            bold = false;
            italic = false;
            underline = false;
            strike = false;
            color = null;
            backColor = null;
            size = -1;
            firstLineIndent = 0.0D;
            oldLeftIndent = 0.0D;
            oldRightIndent = 0.0D;
            leftIndent = 0.0D;
            rightIndent = 0.0D;
            firstLine = false;
        }
        
       public void clearFontBuffer()
       {
          fontBuff.setLength(0);
       }
       
       public StringBuffer getFontBuffer()
       {
          return fontBuff;
       }

        /**
         * Atualiza estados
         * @param attributeset
         * @param stringbuffer
         * @param element
         */
        public void updateState(
            AttributeSet attributeset,
            StringBuffer stringbuffer,
            Element element) {
            String s = element.getName();
 
            if (s.equalsIgnoreCase("paragraph")) {
                firstLine = true;
            }       
            leftIndent =
                updateDouble(
                    attributeset,
                    leftIndent,
                    StyleConstants.LeftIndent);
            rightIndent =
                updateDouble(
                    attributeset,
                    rightIndent,
                    StyleConstants.RightIndent);
            if (leftIndent != oldLeftIndent || rightIndent != oldRightIndent) {
                closeIndentTable(stringbuffer, oldLeftIndent, oldRightIndent);
            }
            
            if (s.equalsIgnoreCase("paragraph")) {
                if(indentString.length() >0 && isIndentTableOpen)
               {  stringbuffer.append("</td></tr>");
                  stringbuffer.append("<td class=\"help\">" + indentString + "</td>");
                  stringbuffer.append("<td class=\"help\">");
               }
            }

            if (s.equalsIgnoreCase("content")) {
               size = updateFontSize(attributeset, size, stringbuffer, fontBuff);
               color = updateFontColor(attributeset, color, stringbuffer, fontBuff);
               backColor = updateFontBackColor(attributeset, backColor, stringbuffer, fontBuff);
               if (acceptFonts) {
                  fontName = updateFontName(attributeset, fontName, stringbuffer, fontBuff);
               }
            }
            
            bold =
                updateBoolean(
                    attributeset,
                    StyleConstants.Bold,
                    "b",
                    bold,
                    stringbuffer,
                    fontBuff);
            italic =
                updateBoolean(
                    attributeset,
                    StyleConstants.Italic,
                    "i",
                    italic,
                    stringbuffer,
                    fontBuff);
            underline =
                updateBoolean(
                    attributeset,
                    StyleConstants.Underline,
                    "u",
                    underline,
                    stringbuffer,
                    fontBuff);
            strike =
                updateBoolean(
                    attributeset,
                    "strike",
                    "strike",
                    strike,
                    stringbuffer,
                    fontBuff);

            alignment = updateAlignment(attributeset, alignment, stringbuffer);
            firstLineIndent =
                updateDouble(
                    attributeset,
                    firstLineIndent,
                    StyleConstants.FirstLineIndent);        
            if (leftIndent != oldLeftIndent || rightIndent != oldRightIndent) {
                openIndentTable(stringbuffer, leftIndent, rightIndent);
                oldLeftIndent = leftIndent;
                oldRightIndent = rightIndent;
            }
            
        }

        /**
         * Abre tabela identada
         * @param stringbuffer
         * @param d
         * @param d1
         */
        private void openIndentTable(
            StringBuffer stringbuffer,
            double d,
            double d1) {
            if (d != 0.0D || d1 != 0.0D) {
                closeSubsetTags(stringbuffer);
                stringbuffer.append("<table><tr>");
                String s = getSpaceTab((int) (d / 4D));
                if (s.length() > 0) {
                   isIndentTableOpen = true;
                   indentString = s;
                   stringbuffer.append("<td class=\"help\">" + s + "</td>"); //RIRALK: modified original line above
                }
                stringbuffer.append("<td class=\"help\">"); //RIRALK: modified original line above
            }
        }

        /**
         * Fecha tabela identada
         * @param stringbuffer
         * @param d
         * @param d1
         */
        private void closeIndentTable(
            StringBuffer stringbuffer,
            double d,
            double d1) {
            if (d != 0.0D || d1 != 0.0D) {
                closeSubsetTags(stringbuffer);
                stringbuffer.append("</td>");
                String s = getSpaceTab((int) (d1 / 4D));
                isIndentTableOpen = false;
                indentString = "";

                if (s.length() > 0) {
                    stringbuffer.append("<td class=\"help\">" + s + "</td>"); //RIRALK: modified original line above
                }
                stringbuffer.append("</tr></table>");
            }
        }

        /**
         * Fecha tags
         * @param stringbuffer
         */
        public void closeTags(StringBuffer stringbuffer) {           
            closeSubsetTags(stringbuffer);
            closeTag(alignment, -1, "div", stringbuffer);
            alignment = -1;
            closeIndentTable(stringbuffer, oldLeftIndent, oldRightIndent);
        }

        /**
         * Fecha conjunto de tags
         * @param stringbuffer
         */
        private void closeSubsetTags(StringBuffer stringbuffer) {

            bold = false;
            italic = false;
            underline = false;
            strike = false;
            color = null;
            fontName = "";
            size = -1;
        }

        /**
         * Fecha Tag
         * @param flag
         * @param s
         * @param stringbuffer
         */
        private void closeTag(
            boolean flag,
            String s,
            StringBuffer stringbuffer) {
            if (flag) {
                stringbuffer.append("</" + s + ">");
            }
        }

        /**
         * Fecha Tag
         * @param color1
         * @param s
         * @param stringbuffer
         */
        private void closeTag(
            Color color1,
            String s,
            StringBuffer stringbuffer) {
            if (color1 != null) {
                stringbuffer.append("</" + s + ">");
            }
        }

        /**
         * Fecha Tag
         * @param s
         * @param s1
         * @param stringbuffer
         */
        private void closeTag(String s, String s1, StringBuffer stringbuffer) {
            if (s.length() > 0) {
                stringbuffer.append("</" + s1 + ">");
            }
        }

        /**
         * Fecha Tag
         * @param i
         * @param j
         * @param s
         * @param stringbuffer
         */
        private void closeTag(
            int i,
            int j,
            String s,
            StringBuffer stringbuffer) {
            if (i > j) {
                stringbuffer.append("</" + s + ">");
            }
        }

        /**
         * Atualiza alinhamentos
         * @param attributeset
         * @param k
         * @param stringbuffer
         * @return
         */
        private int updateAlignment(
            AttributeSet attributeset,
            int k,
            StringBuffer stringbuffer) {
            int i = k;
            Object obj = attributeset.getAttribute(StyleConstants.Alignment);
            if (obj == null)
                return i;
            int j = ((Integer) obj).intValue();
            if (j == 3) {
                j = 0;
            }
            if (j != i && j >= 0 && j <= 2) {
                if (i > -1) {
                    stringbuffer.append("</div>");
                }
                if(note_formatting) stringbuffer.append("<div align=\"" + alignNames[j] + "\">");
                i = j;
            }
            return i;
        }

        /**
         * Atualiza cores das fontes
         * @param attributeset
         * @param color3
         * @param stringbuffer
         * @return
         */
        private Color updateFontColor(
            AttributeSet attributeset,
            Color color3,
            StringBuffer stringbuffer,
            StringBuffer fontbuffer) {
            Color color1 = color3;
            Object obj = attributeset.getAttribute(StyleConstants.Foreground);
            Color color2 = color1;
            if(obj != null)
               color2 = (Color) obj;

                if (color2 != null) {
                    if(note_formatting) {
                       stringbuffer.append("<font color=\"#" + makeColorString(color2) + "\">");
                       fontbuffer.append("</font>");
                    }
                }
            color1 = color2;
            return color1;
        }
        
          private Color updateFontBackColor(
            AttributeSet attributeset,
            Color color3,
            StringBuffer stringbuffer,
            StringBuffer fontbuffer) {
            Color color1 = color3;
            Object obj = attributeset.getAttribute(StyleConstants.Background);
            if (obj == null)
                return null;
            Color color2 = (Color) obj;
                if (color2 != null) {
                    if(note_formatting) {
                       stringbuffer.append("<font style=\"background-color:" + makeColorString(color2) + "\">");
                       fontbuffer.append("</font>");
                    }
                }
            color1 = color2;
            return color1;
        }

        /**
         * Atualiza o nome das fontes
         * @param attributeset
         * @param s2
         * @param stringbuffer
         * @return
         */
        private String updateFontName(
            AttributeSet attributeset,
            String s2,
            StringBuffer stringbuffer,
            StringBuffer fontbuffer    ) {
            String s = s2;
            Object obj = attributeset.getAttribute(StyleConstants.FontFamily);
            String s1 = s;
            if(obj != null)
               s1 = (String)obj;
            if(!Str.isEmpty(s1)){

                if(note_formatting) {
                   stringbuffer.append("<font face=\"" + s1 + "\">");
                   fontbuffer.append("</font>");
                }
            }
            s = s1;
            return s;
        }

        /**
         * Atualiza atributos do tipo Double
         * @param attributeset
         * @param d2
         * @param obj
         * @return
         */
        private double updateDouble(
            AttributeSet attributeset,
            double d2,
            Object obj) {
            double d = d2;
            Object obj1 = attributeset.getAttribute(obj);
            if (obj1 != null) {
                d = ((Float) obj1).floatValue();
            }
            return d;
        }

        /**
         * Atualiza o tamanho das fontes
         * @param attributeset
         * @param k
         * @param stringbuffer
         * @return
         */
        private int updateFontSize(
            AttributeSet attributeset,
            int k,
            StringBuffer stringbuffer,
            StringBuffer fontbuffer) {
            int i = k;
            Object obj = attributeset.getAttribute(StyleConstants.FontSize);
            int j = i;
            if(obj != null)
               j = ((Integer) obj).intValue();
            if(j != -1){
                if(note_formatting){ 
                   stringbuffer.append("<font style=\"font-size:" + j +"pt\">");
                   fontbuffer.append("</font>");
                }
            }
            i = j;
            return i;
        }

        /**
         * Atualiza atributos do tipo Boolean
         * @param attributeset
         * @param obj
         * @param s
         * @param flag2
         * @param stringbuffer
         * @return
         */
        private boolean updateBoolean(
            AttributeSet attributeset,
            Object obj,
            String s,
            boolean flag2,
            StringBuffer stringbuffer,
            StringBuffer fontbuffer) {
            boolean flag = flag2;
            Object obj1 = attributeset.getAttribute(obj);
            if (obj1 != null) {
                boolean flag1 = ((Boolean) obj1).booleanValue();
                if (flag1 != flag) {
                    if (flag1) {
                        stringbuffer.append("<" + s + ">");
                        fontbuffer.insert(0,"</" + s + ">");
                    }
                }
                flag = flag1;
            }
            return flag;
        }

        /**
         * Cria String com nome de cores
         * @param color1
         * @return
         */
        private String makeColorString(Color color1) {
            String s = Long.toString(color1.getRGB() & 0xffffff, 16);
            if (s.length() < 6) {
                StringBuffer stringbuffer = new StringBuffer();
                for (int i = s.length(); i < 6; i++) {
                    stringbuffer.append("0");
                }
                stringbuffer.append(s);
                s = stringbuffer.toString();
            }
            return s;
        }

        /**
         * Cria identa? para primeira linha
         * @param s2
         * @return
         */
        public String performFirstLineIndent(String s2) {
            String s = s2;
            if (firstLine) {
                if (firstLineIndent != 0.0D) {
                    int i = (int) (firstLineIndent / 4D);
                    s = getSpaceTab(i) + s;
                }
                firstLine = false;
            }
            return s;
        }

        /**
         * Coleta o atributo spaceTab do objeto HTMLStateMachine
         * @param i
         * @return
         */
        public String getSpaceTab(int i) {
            StringBuffer stringbuffer = new StringBuffer();
            for (int j = 0; j < i; j++) {
                stringbuffer.append("&nbsp;");
            }
            return stringbuffer.toString();
        }

    }

    /**
     * Construtor padr?
     */
    public Rtf2Html() {
            System.setProperty( "java.awt.headless", "true" );
            htmlbuf = new StringBuffer();
            colorTable = new Vector();
            this.deleteImages = true;

    }

    Rtf2Html(boolean note_formatting, String image_location, boolean deleteImages/*, Hashtable imageTab*/, NoteBook noteBook) {
            this.note_formatting = note_formatting;
            this.image_location = image_location + this.image_location;
            System.setProperty( "java.awt.headless", "true" );
            htmlbuf = new StringBuffer();
            colorTable = new Vector();
            this.deleteImages = deleteImages;
            this.noteBook = noteBook;
            this.images_available = true;
    }

    private int sizeCount = 0;

    private int originalSize() {
        return sizeCount;
    }

    /**
     * Converte uma String RTF em uma String HTML
     * @param s4
     * @return
     */
    protected String convertRTFToHTML(String s4) {// changed the access modified private to 
        String s2 = s4;
        sizeCount = s2.length();
        HTMLStateMachine htmlstatemachine = new HTMLStateMachine();
        s2 = convertRTFStringToHTML(s2, htmlstatemachine);
        String s3 = new String("<html><body>");
        StringTokenizer st = new StringTokenizer(s2);
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (s.startsWith("http://")) {
                s = "<a href='" + s + "'>" + s + "</a>";
            }
            s3 += s + " ";
        }  
        return s3 + "</body></html>";
    }
    
    private String includeImageData()
    {
       String imageTag = "<!-- imagedata ";
       Enumeration e =  NoteBook.getImageTable().keys(); 
      
       
       while(e.hasMoreElements())
       {
           Integer key = (Integer)e.nextElement();
           String imageData = (String)NoteBook.getImageTable().get((Object)key);
          imageTag = imageTag + key + "=" + imageData + ";";
       }
       imageTag += "-->";
       return imageTag;
    }

    /**
     * Converte RTF de um Reader para uma String HTML
     * @param input
     * @return
     * @throws IOException
     */
    private String convertRTFToHTML(Reader input) throws IOException {
        BufferedReader strm = new BufferedReader(input);
        StringBuffer sb = new StringBuffer();
        int s;
        while ((s = strm.read()) != -1) {
            sb.append((char) s);
        }
        return convertRTFToHTML(sb.toString());
    }

    /**
     * Converte RTF de um InputStream para uma String HTML
     * @param input
     * @return
     * @throws IOException
     */
    protected String convertRTFToHTML(InputStream input) throws IOException {
        BufferedReader strm = new BufferedReader(new InputStreamReader(input));
        StringBuffer sb = new StringBuffer();
        int s;
        while ((s = strm.read()) != -1) {
            sb.append((char) s);
        }
        return convertRTFToHTML(sb.toString());
    }

    /**
     * Converte arquivo RTF em uma String HTML
     * @param input
     * @return
     * @throws IOException
     */
    private String convertRTFToHTML(File input) throws IOException {
        BufferedReader strm = new BufferedReader(new FileReader(input));
        StringBuffer sb = new StringBuffer();
        int s;
        while ((s = strm.read()) != -1) {
            sb.append((char) s);
        }
        return convertRTFToHTML(sb.toString());
    }

    /**
     * Converte RTF de uma URL para uma String HTML
     * @param input
     * @return
     * @throws IOException
     */
    private String convertRTFToHTML(URL input) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) input.openConnection();
        conn.setAllowUserInteraction(false);
        conn.setRequestProperty(
            "User-agent",
            "Mozilla/5.0 (X11; U; Linux i686; pt-BR; rv:1.7.5) Gecko/20041118 Firefox/1.0" );
        conn.setInstanceFollowRedirects(true);
        conn.connect();
        BufferedReader strm =
            new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuffer sb = new StringBuffer();
        int s;
        while ((s = strm.read()) != -1) {
            sb.append((char) s);
        }
        return convertRTFToHTML(sb.toString());
    }

    /**
     * Converte String RTF em String HTML
     * @param s2
     * @param htmlstatemachine
     * @return
     */
    private String convertRTFStringToHTML(
        String s2,
        HTMLStateMachine htmlstatemachine) {
        String s = s2;
        RTFEditorKit rtfeditorkit = new RTFEditorKit();
        DefaultStyledDocument defaultstyleddocument =
            new DefaultStyledDocument();      
        readString(s, defaultstyleddocument, rtfeditorkit);
        s = scanDocument(defaultstyleddocument, htmlstatemachine);
        return s;
    }

    /**
     * Leitor de String
     * @param s
     * @param document
     * @param rtfeditorkit
     */
    private void readString(
        String s,
        Document document,
        RTFEditorKit rtfeditorkit) {
        
        s = encodeHTML(s,false);
        if(images_available)
         s = findImages(s);
        s = findHighlights(s);
        s = insertTableTags(s);  //GEGULK: Fix for table problem
        s = findHyperlinks(s).toString();
        s = setSectionEnds(s);
        s = setBulletsTag(s);
        try {
            ByteArrayInputStream bytearrayinputstream =
                new ByteArrayInputStream(s.getBytes());
            rtfeditorkit.read(bytearrayinputstream, document, 0);

        } catch (Exception exception) {
            exception.printStackTrace( System.out );
            return;
        }
    }
    
    private String setSectionEnds(String s)
    {
       int index = s.indexOf("\\uc1\\pard");
       String firstPart = "";
       String secondPart = "";
       if(index != -1)
       {
          firstPart = s.substring(0, index+9);
          secondPart = s.substring(index+9);
          secondPart = secondPart.replace("\\pard","<!--SectionEnd-->");
          s = firstPart + secondPart;
       }
       else
          s = s.replace("\\pard","<!--SectionEnd-->");
       
       return s;
    }
    
    private String setBulletsTag(String s)
    {
       int index = -1;
       int startIndex = 0; 
       int txtIndex = 0;
       StringBuffer sb = new StringBuffer(s);
       index = sb.indexOf( "{\\*\\pn", startIndex);
       while(index >-1)
       {
         
         StringBuffer part = new StringBuffer(sb.substring(0,index));
         String rest = sb.substring(index,sb.length());
         txtIndex = part.lastIndexOf("{\\pntext");
         part.insert(txtIndex,"<!--BulletStart-->");//20 characters
         startIndex = index + 25;
         sb = part.append(rest);     
         index = sb.indexOf( "{\\*\\pn", startIndex);
       }
       
       return sb.toString();
    }
    
    private String findHighlights(String s)
    {
        if(getColorTable(s))
         return setHighlightTags(s);
        else
           return s;
    }
    
    private String setHighlightTags(String s)
    {
       s = s.replace("\\par\r\n\\highlight0 ","</font>\\par\r\n").replace("\\par\\r\\n\\highlight0 ","</font>\\par\\r\\n").replace("\\highlight0", "</font>");
       return setHighlightColor(s);
    }
    
    private String setHighlightColor(String s)
    {
       String colorNo = "";
       StringBuffer sb = new StringBuffer(s);
       int startIndex = sb.indexOf("\\highlight");
       int counter = startIndex+ 10;
       int colorValue = -1;
       
       if( startIndex == -1)
          return sb.toString();
       
       while(true)
       {
          char value = sb.charAt(counter);
          if(value == ' ' || value == '\\' || value == '{')
             break;
          colorNo += value;
          counter++;
       }
       
       try{
          colorValue = Integer.parseInt(colorNo);          
          Color hiColor = (Color)colorTable.get(colorValue-1);
          String highlightString = "<font style=\"background-color:" +makeColorString(hiColor)+"\">";
          int length = highlightString.length();
          sb.insert(startIndex+11,highlightString);
          StringBuffer newBuff = new StringBuffer(sb.substring(0,startIndex+length+12));
          newBuff = newBuff.append(setHighlightColor(sb.substring(startIndex+length+12)));
          sb = newBuff;
                    
       }
       catch(NumberFormatException e)
       {
          return sb.toString();
       }     
       
       return sb.toString();
    }
    
    private String makeColorString(Color color) {
       String s = Long.toString(color.getRGB() & 0xffffff, 16);
       if (s.length() < 6) {
          StringBuffer stringbuffer = new StringBuffer();
          for (int i = s.length(); i < 6; i++) {
             stringbuffer.append("0");
          }
          stringbuffer.append(s);
          s = stringbuffer.toString();
       }
       return s;
    }
    
    private boolean getColorTable(String s)
    {
       int startIndex = s.indexOf("{\\colortbl");
       int endIndex = -1;
       String colorString = "";
       if(startIndex == -1)
          return false;
         endIndex   =  countBrackets(s.substring(startIndex));
       
       colorString = s.substring(startIndex, startIndex + endIndex);
       startIndex = colorString.indexOf(';');
       endIndex = colorString.lastIndexOf(';');
       
       if(startIndex != -1){
          colorString = colorString.substring(startIndex+1,endIndex);
         populateColorTable(colorString);
       }
       return true;       
    }
    
    private void populateColorTable(String s)
    {
       StringTokenizer st = new StringTokenizer(s,";");
       while(st.hasMoreTokens())
       {
          String colorValue = st.nextToken();
          insertColor(colorValue);
       }
    }
    
    private void insertColor(String colorValue) {
       int red = -1;
       int green = -1;
       int blue = -1;
       
       StringTokenizer st = new StringTokenizer(colorValue,"\\");
       
       while(st.hasMoreTokens()) {
          String color = st.nextToken();
          if(color.contains("red")) {
             red = Integer.parseInt(color.substring(3,color.length()));
          }
          if(color.contains("green")) {
             green = Integer.parseInt(color.substring(5,color.length()));
          }
          if(color.contains("blue")) {
             blue = Integer.parseInt(color.substring(4,color.length()));
          }
       }
       if( red !=-1 && green !=-1 && blue != -1){
          Color newColor = new Color(red,green,blue);
          colorTable.addElement(newColor);
       }
    }
    
    private String encodeHTML(String s, boolean encode_additional_spaces)
    {
       try
      {
         if ( Str.isEmpty(s) ) return s;

         htmlbuf.setLength(0);

         for ( int i=0; i<s.length(); i++ )
         {
            char ch = s.charAt(i);
            switch ( ch )
            {
               case '<': htmlbuf.append("&lt;");
                  break;
               case '>': htmlbuf.append("&gt;");
                  break;
               //case '"': htmlbuf.append("&quot;");
               //   break;
               case '&': htmlbuf.append("&amp;");
                  break;
               case 32:  //Space
               case 160: //No-Break Space
                  if (encode_additional_spaces)
                  {
                     while(i<s.length()-2 && (s.charAt(i+1) == 32 || s.charAt(i+1) == 160))
                     {
                        htmlbuf.append("&nbsp;");
                        i++;
                     }
                     htmlbuf.append(s.charAt(i));
                  }
                  else
                  {
                     htmlbuf.append(ch);
                  }
                  break;
               default : htmlbuf.append(ch);
            }
         }
         return htmlbuf.toString();
      }
      catch ( Exception e )
      {
        e.printStackTrace( System.out );
         return null;
      }
    }
    private String findImages(String s)
    {
       StringBuffer sb = new StringBuffer(s);
       int index = -1;
       int i = sb.indexOf("{\\pict");
       if(i != -1)
       {
          String s1 = sb.substring(0,i);
          index = i;
          int end_index = index + countBrackets(sb.substring(index)) + 1;
          int imageKey = getUniqueKey();
          String image_data = "<img src=\""+ image_location + "\"><!-- image value=" + imageKey + " -->";
          String image_string = sb.substring(index,end_index);
          image_string = image_string.replace("\r\n", "\\newline");
          if(!deleteImages)
          {
             NoteBook.NoteImage noteImage = noteBook.getNoteImageInstance(image_string,System.currentTimeMillis());
             NoteBook.getImageTable().put((Object)new Integer(imageKey),(Object)noteImage);
          }
          StringBuffer newBuff = new StringBuffer(s1);
          newBuff = newBuff.append(image_data);
          newBuff = newBuff.append(findImages(sb.substring(end_index)));
          sb = newBuff;
       }
       return sb.toString();
    }
    
    private int getUniqueKey()
    {       
       int key = NoteBook.generateImageKey();
       while(NoteBook.getImageTable().containsKey((Object)new Integer(key)))
       {
          key = NoteBook.generateImageKey();
       }
       return key;
    }
        
    private int getStartImageTag(String s)
    {
       StringBuffer sb = new StringBuffer(s);
       return sb.lastIndexOf("{\\rtlch");       
       
    }
    public int countBrackets(String s)
    {
       int bracket_count = 0;
       boolean is_bracket_found = false;
       int last_index = -1;

       
       for(int i=0; i<s.length();i++)
       {
          char value = s.charAt(i);
          if(value == '{'){
             is_bracket_found = true;
             bracket_count++;             
          }
             
          if(value == '}')
          {
             is_bracket_found = true;
             bracket_count--;
          }
          
          if(is_bracket_found && bracket_count ==0){
             last_index = i;
             break;
          }       
       }
       
       return last_index;
    }
    
    private StringBuffer findHyperlinks(String s)
    {
       StringBuffer sb = new StringBuffer(s);
       int index = -1;
       String url="";
       String hype_text = "";
       int i = sb.indexOf("{\\field{\\*\\fldinst");
       if(i != -1){
          String s1 = sb.substring(i);
          index = countBrackets(s1);
          if(index == -1)
             return sb;
          else
             index = index + i;
          
          url  = getHyperlinkUrl(sb.substring(i,index));
          hype_text = getHyperlinkText(sb.substring(i,index));

          String hyperlink_val = " {\\rtlch \\f0\\fs24 <a href=\""+ url+"\">"+ hype_text + "</a> }";
          int length = hyperlink_val.length();
          sb.insert(index+1,hyperlink_val);
          StringBuffer newBuff = new StringBuffer(sb.substring(0,index+length+1));
          newBuff = newBuff.append(findHyperlinks(sb.substring(index+length+1)));
          sb = newBuff;
       }

       return sb;
    }
    
    private String getHyperlinkUrl(String s)
    {
       StringBuffer sb = new StringBuffer(s);
       int index = sb.indexOf("HYPERLINK");
       if(index == -1)
          return "";
       int start_index = sb.indexOf("\"");
       int end_index = sb.lastIndexOf("\"");
       if(start_index != -1 && end_index != -1)
         return sb.substring(start_index+1,end_index);
       else
          return "";
    }
    
    private String getHyperlinkText(String s)
    {
       StringBuffer sb = new StringBuffer(s);
       int i = sb.indexOf("{\\fldrslt");
       if (i == -1)
          return "";
       String s1 = sb.substring(i+10);
       int bracket_index   =  countBrackets(s1);
       int last = s1.lastIndexOf(' ');
       if(last == -1 || bracket_index == -1)
          return "";
       String s2 = s1.substring(last+1,bracket_index);
       
       return s2;
    }
    
    private String insertTableTags(String s)
    {
       if (s.indexOf("\\trowd") != -1 )
       { 
         int lastCell =0;
         int startTab;
         int endTab;
         
         boolean junkTags = true;
         String s1,s2;
         s = s.replaceFirst("\\Q\\trowd\\E","\\\\trowd<table border=\"1\" cellspacing=\"0\"><tr><td>");
         s = s.replaceAll("\\Q\\cell\\E","</td><td>\\\\cell");
         s = s.replaceAll("\\Q\\row\\E","</tr><tr><td>\\\\row");
         s = s.replaceAll("\\Q</td><td>\\cellx\\E","\\\\cellx");
         
         s = correctTable(s);
         
         int endIndex = s.length()-1;
         int startIndex = 0;
         int lastrow = -1;
         int lastcell = -1;
         String lastcellString = "";
         
         if (s.indexOf("\\lastrow") != -1) //if multiple tables exist
         {
            do{
               int lastrowIndex = s.indexOf("\\lastrow", startIndex);
               lastrow = s.indexOf("</tr><tr><td>\\row",lastrowIndex);

               endTab = lastrow + 5;
               s1 = s.substring(0,endTab)+"</table>";
               s2 = s.substring(endTab+8,s.length()-1);
               s= s1+s2;
               int nextTabStartIndex = s.indexOf("\\trowd",lastrow);
               if( nextTabStartIndex!= -1){
                  s1 = s.substring(0,nextTabStartIndex)+ "<table border=\"1\" cellspacing=\"0\"><tr><td>";
                  s2 = s.substring(nextTabStartIndex,s.length()-1);
                  s = s1 + s2;
                  startIndex = lastrow;
               }
               else 
                  startIndex = -1;
               
            }while( startIndex!= -1);
         }
       }
       return s;
    }
    
    private String correctTable(String s)
    {
       int lastCellIndex = -1;
       int lastRowIndex = -1;
       int startIndex = 0;
       String s1,s2,s3,s4;
       do{
          
        lastRowIndex = s.indexOf("\\row",startIndex);
        if(lastRowIndex != -1)
        {
           s1 = s.substring(0,lastRowIndex);
           s2 = s.substring(lastRowIndex, s.length()-1);
           lastCellIndex = s1.lastIndexOf("</td><td>\\cell");
           if(lastCellIndex != -1)
           {
               s3 = s1.substring(0,lastCellIndex+5);
               s4 = s1.substring(lastCellIndex+9,s1.length());
               s1 = s3 + s4;
           }
           s = s1 + s2;
           startIndex = lastRowIndex + 4;
        }
       }while(lastRowIndex !=-1);
       return s;
    }

    /**
     * Leitor de Documento
     * @param document
     * @param htmlstatemachine
     * @return
     */
    private String scanDocument(
        Document document,
        HTMLStateMachine htmlstatemachine) {
        String s = "";
        try {
            StringBuffer stringbuffer = new StringBuffer();
            Element element = document.getDefaultRootElement();          
            recurseElements(element, document, stringbuffer, htmlstatemachine);
            htmlstatemachine.closeTags(stringbuffer);
            s = stringbuffer.toString();            
        } catch (Exception exception) {
            exception.printStackTrace( System.out );
            return s;
        }
        return s;
    }

    /**
     * Leitor de Elementos
     * @param element
     * @param document
     * @param stringbuffer
     * @param htmlstatemachine
     */
    private void recurseElements(
        Element element,
        Document document,
        StringBuffer stringbuffer,
        HTMLStateMachine htmlstatemachine) {      
        for (int i = 0; i < element.getElementCount(); i++) {
            Element element1 = element.getElement(i);
            scanAttributes(element1, document, stringbuffer, htmlstatemachine);
            recurseElements(element1, document, stringbuffer, htmlstatemachine);            
        }        
    }

    /**
     * Leitor de Atributos
     * @param element
     * @param document
     * @param stringbuffer
     * @param htmlstatemachine
     */
    private void scanAttributes(
        Element element,
        Document document,
        StringBuffer stringbuffer,
        HTMLStateMachine htmlstatemachine) {       
        try {
            int i = element.getStartOffset();
            int j = element.getEndOffset();
            String s = document.getText(i, j - i);
            String s1 = element.getName(); 

            if(!s.contains("<!--BulletStart-->")){
               if(s.contains("<font style=\"background-color:") && s1.equalsIgnoreCase("content"))
               {
                  int k = s.indexOf("<font style=\"background-color:");
                  String fontPart = s.substring(k,k+38);
                  stringbuffer.append(fontPart);
                  s = s.replace(fontPart,"");
               }

            javax.swing.text.AttributeSet attributeset = element.getAttributes();
            htmlstatemachine.updateState(attributeset, stringbuffer, element);
            }        
            if (s1.equalsIgnoreCase("content")) {
                s = s.replaceAll("\\t", htmlstatemachine.getSpaceTab(4));
                s = s.replaceAll("\\n", "<br>");
                s = htmlstatemachine.performFirstLineIndent(s);
                stringbuffer.append(s);

               stringbuffer.append(htmlstatemachine.getFontBuffer().toString());
            }

            htmlstatemachine.clearFontBuffer();

        } catch (BadLocationException badlocationexception) {
            badlocationexception.printStackTrace( System.out );
            return;        }
    }

    /**
     * Transforma um File em um InputStream
     * @param in
     * @return
     * @throws Exception
     */
    private InputStream parse(File in) throws Exception {
        return parse(new FileInputStream(in));
    }

    /**
     * Transforma um URL em um InputStream
     * @param in
     * @return
     * @throws Exception
     */
    private InputStream parse(URL in) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) in.openConnection();
        conn.setAllowUserInteraction(false);
        conn.setRequestProperty("User-agent", "Firefox-1.0");
        conn.setInstanceFollowRedirects(true);
        conn.connect();
        return parse(conn.getInputStream());
    }

    /**
     * L?m InputStream
     * @param in
     * @return
     * @throws Exception
     */
    private InputStream parse(InputStream in) throws Exception {
        BufferedReader strm = new BufferedReader(new InputStreamReader(in));
        StringBuffer sb = new StringBuffer();
        int s;
        while ((s = strm.read()) != -1) {
            sb.append((char) s);
        }
        String s2 = convertRTFToHTML(sb.toString());
        return new ByteArrayInputStream(s2.getBytes());
    }
    
    /**
     * Converts the RTF String to HTML
     * @param s
     * @return
     */
    //public String convertRTFStringToHTML(final String s) {
    public String convertRTFStringToHTML(final String s) { //RIRALK: made the method friendly
        HTMLStateMachine htmlstatemachine = new HTMLStateMachine();
        RTFEditorKit rtfeditorkit = new RTFEditorKit();
        DefaultStyledDocument defaultstyleddocument = new DefaultStyledDocument();
        readString(s, defaultstyleddocument, rtfeditorkit);
        return scanDocument(defaultstyleddocument, htmlstatemachine);
    }
    
    /**
     * Converte uma String RTF em uma String HTML, convertendo barras, se necess?o.
     * @param rtf String RTF a converter
     * @return String HTML convertida.
     */
    String rtf2html( final String rtf ) {
        if(DEBUG) System.out.println( "String original: " + rtf);
        String s = rtf.replaceAll("\\\\\\\\", "" + (char)92 + (char)92 );
        s = convertRTFStringToHTML( s );
        return s;
    }

    /**
     * Convert RTF to plain text for comparision. Used to save modofications
     * @param rtf String RTF format
     * @return String PLAIN text.
     */
    String convertRTFStringToPlainText(String rtf)
    {
       String s = rtf.replaceAll("\\\\\\\\", "" + (char)92 + (char)92 );
       RTFEditorKit rtfeditorkit = new RTFEditorKit();
       DefaultStyledDocument defaultstyleddocument = new DefaultStyledDocument();
       
       String plain_text = "";        
       try {
          ByteArrayInputStream bytearrayinputstream =  new ByteArrayInputStream(s.getBytes());
          rtfeditorkit.read(bytearrayinputstream, defaultstyleddocument, 0);
          
          plain_text = defaultstyleddocument.getText(0,defaultstyleddocument.getLength());

        } catch (Exception exception) {
            exception.printStackTrace( System.out );
        }
       
       return plain_text;
    }
}
