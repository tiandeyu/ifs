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
 * File            : MobilePageProvider.java
 * Description :
 * Notes         :
 * Created    : mapelk
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2007-Dec-03 buhilk IID F1PR1472 - Improved miniFW functionality.
 */

package ifs.fnd.webmobile.web;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.ap.*;
import ifs.fnd.buffer.*;

import java.lang.reflect.*;

/**
 *
 * @author mapelk
 */
public class MobilePageProvider extends ASPPageProvider{
   
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.webmobile.web.MobilePageProvider");
   
   private AutoString mobile_out          = new AutoString();
   private AutoString mobile_script       = new AutoString();
   private String     mobile_scriptfile;      

   /**
    * Package constructor that will be called by ASPManager.
    */
   protected MobilePageProvider( ASPManager mgr, String page_path )
   {
      super(mgr,page_path);
      if ( DEBUG ) debug(this+": ASPPageProvider.<init>: "+mgr+","+page_path);
   }
   
   /**
    * Construction of the page.
    */
   protected ASPPage construct() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.construct()");
      return super.construct();
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Freeze the page. Call freeze() for all enclosed elements.
    * Set the page in state DEFINED. Can only by called if the current
    * state of the page is UNDEFINED.
    */
   protected void doFreeze() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.doFreeze()");
      super.doFreeze();
   }

   /**
    * Just a wrapper for frameworkReset.
    */
   protected void doReset() throws FndException
   {
      mobile_out.clear();
      frameworkReset();
   }

   /**
    * Activate the page after feching from pool. Call activate() for
    * all enclosed elements.
    * Can only be called if the current object is in state DEFINED.
    */
   protected void doActivate() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.doActivate()");
      mobile_out.clear();
      super.doActivate();
   }

   /**
    * Clone the page and all enclosed elements.
    * The new page is always in state DEFINED.
    * Set default values for all mutable attributes.
    */
   public ASPPoolElement clone( Object obj ) throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.clone("+obj+")");
      ASPPoolElement page = super.clone(obj);
      
      return page;
   }

   //==========================================================================
   //  Template functions for overriding in pages
   //==========================================================================

   /**
    * Return the page description that will be shown ...
    * The description text should contain the translatable constant.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   protected String getDescription()
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.getDescription()");
      return "FNDPAGPRVDESC: Description not set";
   }

   /**
    * Return the title of the page ...
    */
   protected String getTitle() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.getTitle()");
      return getClass().getName();
   }

   /**
    * Return the output stream used to generate the html code of the page ...
    */ 
   public AutoString getOutputStream()
   {
      return mobile_out;
   }
   
   /**
    * Append a string to the HTML-output without any conversion.
    */
   protected final void appendToMobileHTML( String str )
   {
      if ( DEBUG ) debug(this+": MobilePageProvider.appendToHTML("+str+")");
      mobile_out.append(str);
   }  
   
   /**
    * Create the HTML contents of the page by calling printContents().
    * ... not for overriding in the first palce
    */
   protected AutoString getContents() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.getContents()");
      
      ASPManager mgr = getASPManager();

      mobile_out.clear();

      mobile_out.append("<html>\n");
      mobile_out.append("<head>\n");
      mobile_out.append(mgr.generateHeadTag(getTitle()));
      mobile_out.append("</head>\n");
      mobile_out.append("<body ");
      mobile_out.append(mgr.generateBodyTag());
      mobile_out.append(" >\n");
      mobile_out.append("<form ");
      mobile_out.append(mgr.generateFormTag());
      mobile_out.append(" >\n");
      mobile_out.append(mgr.startPresentation(getTitle()));

      printContents();

      mobile_out.append(mgr.endPresentation());

      mobile_out.append("\n");
      mobile_out.append("</form>\n");
      mobile_out.append("</body>\n");
      mobile_out.append("</html>\n");

      return mobile_out;
   }

   /**
    * Draw a simple command bar with 100% width and cellpadding 1
    * @param Title to be displayed
    */
   public void drawSimpleCommandBar(String title)
   {
       mobile_out.append("<table cellpadding=\"1\" width=\"100%\" class=\"pageCommandBar\">\n");
       mobile_out.append("   <tr>\n");
       mobile_out.append("      <td>&nbsp;&nbsp;"+getASPManager().translate(title)+"</td>\n");
       mobile_out.append("   </tr>\n");
       mobile_out.append("</table>\n");
   }  
   
   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * If the href represents a relative reference the application root will be added.
    */
   protected final void printAbsoluteLink( String value, String href )
   {
      printLink( value, href, true, false, false, false, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * @param value label to be display
    * @param href hyperlink 
    */
   protected final void printLink( String value, String href )
   {
      printLink( value, href, false, false, false, true );
   }
   
   /**
    * Append a secure link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * @param value label to be display
    * @param href hyperlink 
    * @param sec_objects comma seperated list of security objects
    */
   protected final void printLink( String value, String href, String sec_objects )
   {
      printLink( value, href, false, false, false,isObjectAccessible(sec_objects));
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    */
   protected final void printImageLink( String imgpath, String href )
   {
      printLink( imgpath, href, true, false, false, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes no parameters. 
    */
   protected final void printScriptLink( String value, String func )
   {
      printLink( value, func, false, true, false, true );
   }

   /**
    * Append a secure link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes no parameters.
    * @param value value to be display
    * @param func javascript function with no argument
    * @param sec_objects comma seperated security objects
    */
   
   protected final void printScriptLink( String value, String func, String sec_objects )
   {
      printLink( value, func, false, true, false, isObjectAccessible(sec_objects));
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * Any text, send in the value-parameter, will be translated.
    */
   protected final void printSubmitLink( String value )
   {
      printLink( value, null, false, true, true, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes no parameters. 
    */
   protected final void printSubmitLink( String value, String func )
   {
      printLink( value, func, false, true, true, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes no parameters.
    * @param value value to be display
    * @param func javascript function with param
    * @param sec_objects list of comma seperated security objects
    */   
   protected final void printSubmitLink( String value, String func, String sec_objects )
   {
      printLink( value, func, false, true, true, isObjectAccessible(sec_objects));
   }
   
   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * Specified JavaScript function takes no parameters. 
    */
   protected final void printScriptImageLink( String imgpath, String func )
   {
      printLink( imgpath, func, true, true, false,true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    */
   protected final void printSubmitImageLink( String imgpath )
   {
      printLink( imgpath, null, true, true, true, true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * Specified JavaScript function takes no parameters.
    */
   protected final void printSubmitImageLink( String imgpath, String func )
   {
      printLink( imgpath, func, true, true, true, true );
   }
   
   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page) if the submit-flag is true.
    * The value can be any text, which will be translated, or, if the image-parameter
    * is true, an image specification.
    * Run a JavaScript function given in the link-parameter rather then an URL
    * if the script-parameter is true.
    * Specified JavaScript function takes no parameters.
    */
   private void printLink( String value, String link, boolean image, boolean script, boolean submit, boolean has_access )
   {
      printLink( value, link, false, image, script, submit, has_access );
   }   

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page) if the submit-flag is true.
    * The value can be any text, which will be translated, or, if the image-parameter
    * is true, an image specification. Then the alternative image specification can be
    * send in hovimgpath-parameter to attain the hover effect.
    * Run a JavaScript function given in the link-parameter rather then an URL
    * if the script-parameter is true.
    * Specified JavaScript function takes no parameters. 
    * Try to add absolute reference if abshref is true.
    * Note: if the value contains '&&' only the text within them will be hyperlinked.
    *       eg: "Click on &&this&& link" for value will only hyperlink the word 'this'.
    */   
   private void printLink( String  value, String  link, boolean abshref, boolean image, boolean script, boolean submit, boolean has_access)
   {
      String text1="";
      String text2="";
      value = getASPManager().translate(value);
      if(value.indexOf("&&")!=-1)
      {
         if(value.indexOf("&&")==value.lastIndexOf("&&"))
         {
            text1 = value.substring(0,value.indexOf("&&"));
            value = value.substring(value.indexOf("&&")+2,value.length());
         }
         else
         {
            text1 = value.substring(0,value.indexOf("&&"));
            text2 = value.substring(value.lastIndexOf("&&")+2,value.length());
            value = value.substring(value.indexOf("&&")+2,value.lastIndexOf("&&"));
         }
      }
      
      mobile_out.append("<font class=\"normalTextValue\">");
      mobile_out.append(getASPManager().HTMLEncode(text1,true));
      
      if (has_access)
      {
         mobile_out.append("<a class=\"hyperLink\" href=\"");

         if ( script )
         {
            mobile_out.append("javascript:");

            if ( !Str.isEmpty(link) )
               mobile_out.append(link,"()");

            if ( submit && !Str.isEmpty(link) )
               mobile_out.append(";");

            if ( submit )
               mobile_out.append("submit(",")");
         }
         else
         {
            if ( abshref && link.indexOf("://")<0 && !link.startsWith("/") )
               mobile_out.append(getApplicationPath(),"/");
            mobile_out.append( link );
         }
         mobile_out.append("\"");
         mobile_out.append(">");
      }
      else
         mobile_out.append("<font class=dissabledLinkValue>");

      if ( image )
      {
         mobile_out.append("<img");
         mobile_out.append(" src=\"",value,"\" border=0>");
      }
      else
         mobile_out.append( getASPManager().HTMLEncode(getASPManager().translate(value),true) );

      if (has_access)
         mobile_out.append("</a>");
      else
         mobile_out.append("</font>");
     
      mobile_out.append(getASPManager().HTMLEncode(text2,true));
      mobile_out.append("</font>");
   }
   
   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined styles.
    */   
   protected final void printText( String str )
   {
      ASPManager mgr = getASPManager();
      mobile_out.append("<font class=\"normalTextValue\">");
      if ( Str.isEmpty(str) )
         mobile_out.append("&nbsp;");
      else
         mobile_out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      mobile_out.append("</font>");
   }

   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined bold styles.
    */   
   protected final void printBoldText( String str )
   {
      ASPManager mgr = getASPManager();
      mobile_out.append("<font class=\"boldTextValue\">");
      if ( Str.isEmpty(str) )
         mobile_out.append("&nbsp;");
      else
         mobile_out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      mobile_out.append("</font>");
   }

   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined italic styles.
    */   
   protected final void printItalicText( String str )
   {
      ASPManager mgr = getASPManager();
      mobile_out.append("<font class=\"italicTextValue\">");
      if ( Str.isEmpty(str) )
         mobile_out.append("&nbsp;");
      else
         mobile_out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      mobile_out.append("</font>");
   }

   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined bold and italic styles.
    */   
   protected final void printBoldItalicText( String str )
   {
      ASPManager mgr = getASPManager();
      mobile_out.append("<font class=\"italicTextValue boldTextValue\">");
      if ( Str.isEmpty(str) )
         mobile_out.append("&nbsp;");
      else
         mobile_out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      mobile_out.append("</font>");
   }
   
   /**
    * Append a label after translation to the HTML-output.
    * Apply predefined styles.
    */   
   protected final void printTextLabel( String str )
   {
      ASPManager mgr = getASPManager();
      String usage_id ="";
      int index = 0;
      if(!Str.isEmpty(str))
         index = str.indexOf(":");
      if (index>0)
      {
         String tr_constant = str.substring(0,index);
         usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
      
      mobile_out.append("<font class=\"normalTextLabel\">");
      if ( Str.isEmpty(str) )
         mobile_out.append("&nbsp;");
      else
         mobile_out.append(mgr.HTMLEncode(mgr.translateJavaText(str),true));
      mobile_out.append("</font>");
   }   

   /**
    * Append amount of spaces ('&nbsp;' sequences) to the HTML-output.
    */   
   protected final void printSpaces( int spaces )
   {
      for ( int i=0; i<spaces; i++ )
         mobile_out.append("&nbsp;");
   }

   /**
    * Append a &lt;br&gt; to the HTML-output.
    */   
   protected final void printNewLine()
   {
      mobile_out.append("<br>\n");
   }

   /**
    * Append a named hidden input tag to the HTML-output.
    */   
   protected final void printHiddenField( String name, String value )
   {
      mobile_out.append("<input type=\"hidden\" name=\"",name,"\"");
      mobile_out.append(" value=\"",getASPManager().HTMLEncode(value),"\">");
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */   
   protected final void printField( String name, String value, String tag )
   {
      printField(name, value, tag, 0, 0, false);
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected final void printField( String name, String value, String tag, int size)
   {
      printField(name, value, tag, size, 0, false);      
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */   
   protected final void printField( String name, String value, String tag, int size, int maxlength)
   {
      printField(name, value, tag, size, maxlength, false);      
   }   

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */   
   protected final void printField( String name, String value, String tag, int size, int maxlength, boolean mandatory)
   {
      ASPManager mgr = getASPManager();
      mobile_out.append("<input class=\"editableTextField\" type=\"text\"");
      mobile_out.append(" name=\"",name);
      mobile_out.append("\" value=\"",mgr.HTMLEncode(value),"\" ");

      mobile_out.append(" size=\""+((size>MobileBlockLayout.MAX_FIELD_SIZE)?MobileBlockLayout.MAX_FIELD_SIZE:size)+"\" ");
      
      if ( maxlength>0 )
      {
         mobile_out.append(" maxlength=\"");
         mobile_out.appendInt(maxlength);
         mobile_out.append("\" ");
      }
      if ( tag != null )
         mobile_out.append(tag);

      mobile_out.append(">");

      if ( mandatory )
         mobile_out.append("*");      
   }

   /**
    * Append a named ReadOnly input tag of type 'text' to the HTML-output. Apply predefined styles.
    */   
   protected final void printReadOnlyTextField( String name, String value, String tag )
   {
      printReadOnlyField( name, value, tag, 0, 0, false);
   }

   /**
    * Append a named ReadOnly input tag of type 'text' to the HTML-output. Apply predefined styles.
    */  
   protected final void printReadOnlyTextField( String name, String value, String tag, int size )
   {
      printReadOnlyField( name, value, tag, size, 0, false);      
   }

   /**
    * Append a named ReadOnly input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected final void printReadOnlyTextField( String name, String value, String tag, int size, int maxlength )
   {
      printReadOnlyField( name, value, tag, size, maxlength, false);
   }

   /**
    * Append a named ReadOnly input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected final void printReadOnlyField( String name, String value, String tag, int size, int maxlength, boolean mandatory)
   {
      ASPManager mgr = getASPManager();
      mobile_out.append("<input class=\"readOnlyTextField\" type=\"text\" readonly tabindex=\"-1\" OnChange=\"this.value=this.defaultValue\" ");
      mobile_out.append(" name=\"",name);
      mobile_out.append("\" value=\"",mgr.HTMLEncode(value),"\" ");
      mobile_out.append("size=\""+((size>MobileBlockLayout.MAX_FIELD_SIZE)?MobileBlockLayout.MAX_FIELD_SIZE:size)+"\" ");
      if ( maxlength>0 )
      {
         mobile_out.append("maxlength=\"");
         mobile_out.appendInt(maxlength);
         mobile_out.append("\" ");
      }
      if ( tag != null )
         mobile_out.append(tag);

      mobile_out.append(">");

      if ( mandatory )
         mobile_out.append("*");
   }

   /**
    * Append a named input tag of type 'password' to the HTML-output. Apply predefined styles.
    */   
   protected final void printPasswordField( String name, String value, String tag )
   {
      printPasswordField( name, value, tag, 0, false);            
   }

   /**
    * Append a named input tag of type 'password' to the HTML-output. Apply predefined styles.
    */   
   protected final void printPasswordField( String name, String value, String tag, boolean mandatory )
   {
      printPasswordField( name, value, tag, 0, mandatory);            
   }

   /**
    * Append a named input tag of type 'password' to the HTML-output. Apply predefined styles.
    */
   protected final void printPasswordField( String name, String value, String tag, int size, boolean mandatory)
   {
      ASPManager mgr = getASPManager();
      mobile_out.append("<input class=\"passwordTextField\" type=\"password\" ");
      mobile_out.append("name=\"",name);
      mobile_out.append("\" value=\"",mgr.HTMLEncode(value),"\" ");
      mobile_out.append("size=\""+((size>MobileBlockLayout.MAX_FIELD_SIZE)?MobileBlockLayout.MAX_FIELD_SIZE:size)+"\" ");
      if ( tag != null )
         mobile_out.append(tag);

      mobile_out.append(">");

      if ( mandatory )
         mobile_out.append("*");
   }   

   /**
    * Append a named textarea to the HTML-output. Apply predefined styles.
    */   
   protected final void printTextArea( String name, String value, String tag, int rows, int cols )
   {
      printTextArea( name, value, tag, rows, cols, false);
   }

   /**
    * Append a named textarea to the HTML-output. Apply predefined styles.
    */   
   protected final void printTextArea( String name, String value, String tag, int rows, int cols, boolean mandatory )
   {
      ASPManager mgr = getASPManager();
      mobile_out.append("<textarea class=\"editableTextArea\"");
      mobile_out.append(" name=\"",name);
      mobile_out.append("\" rows=\"");
      mobile_out.appendInt(rows);
      mobile_out.append("\" cols=\""+cols+"\" ");      

      if ( tag != null )
         mobile_out.append(tag);
      
      mobile_out.append(">",mgr.HTMLEncode(value),"</textarea>");

      if ( mandatory )
         mobile_out.append("*");      
   }

   /**
    * Append a named check box input tag to the HTML-output and apply predefined styles.
    */   
   protected final void printCheckBox( String name, String value, boolean checked, String tag)
   {
      printCheckBox( null, name, value, checked, tag);
   }
   
   protected final void printCheckBox( String label, String name, String value, boolean checked, String tag)
   {
      ASPManager mgr = getASPManager();
            
      mobile_out.append("<input class=\"checkbox\" type=\"checkbox\"");
      mobile_out.append(" name=\"",name);
      mobile_out.append("\" value=\"" + value + "\" ");

      if ( tag != null )
         mobile_out.append(tag);
      if ( checked )
         mobile_out.append(" checked");
      mobile_out.append(">");
      if (!Str.isEmpty(label))
      {
         mobile_out.append(mgr.translate(label,this));
      }
   }

   /**
    * Append a named radio button input tag to the HTML-output and apply predefined styles.
    * The specified label will be translated.
    */
   protected final void printRadioButton( String label, String name, String value, boolean checked, String tag )
   {
      printRadioButton( label, name, value, checked, tag, -1 );
   }
   
   protected final void printRadioButton( String label, String name, String value, boolean checked, String tag, int index )
   {
      mobile_out.append("<input class=\"radioButton\" type=\"radio\"");
      mobile_out.append(" name=\"",name);
      mobile_out.append("\" value=\"" + value + "\"");
      if ( checked )
         mobile_out.append(" checked ");
      if ( tag != null )
         mobile_out.append(tag);
      
      mobile_out.append(">",getASPManager().translate(label,this));
   }   

   protected final void printSelectStart( String name, String tag )
   {
      mobile_out.append("<select class=\"selectbox\"");
      mobile_out.append(" name=\"",name,"\" ");
      if ( tag != null )
         mobile_out.append(tag);
      mobile_out.append(">");
   }

   protected final void printSelectOption( String label, String value, boolean selected )
   {
      if ( selected )
         mobile_out.append("<option selected value=\"",value,"\">",label);
      else
         mobile_out.append("<option value=\"",value,"\">",label);
      mobile_out.append("</option>");

   }

   protected final void printSelectEnd( )
   {
      printSelectEnd(false);
   }

   protected final void printSelectEnd( boolean mandatory )
   {
      mobile_out.append("</select>",(mandatory? "*":""));
   }   

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected final void printSelectBox( String name, ASPBuffer aspbuf )
   {
      printSelectBox( name, aspbuf, null, null, false, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected final void printSelectBox( String name, ASPBuffer aspbuf, String key )
   {
      printSelectBox( name, aspbuf, key, null, false, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected final void printSelectBox( String name, ASPBuffer aspbuf, String key, String tag )
   {
      printSelectBox( name, aspbuf, key, tag, false, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected final void printSelectBox( String name, ASPBuffer aspbuf, String key, String tag, int size )
   {
      printSelectBox( name, aspbuf, key, tag, false, size );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected final void printMandatorySelectBox( String name, ASPBuffer aspbuf )
   {
      printSelectBox( name, aspbuf, null, null, true, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected final void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key )
   {
      printSelectBox( name, aspbuf, key, null, true, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected final void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key, String tag )
   {
      printSelectBox( name, aspbuf, key, tag, true, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */   
   protected final void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key, String tag, int size )
   {
      printSelectBox( name, aspbuf, key, tag, true, size );
   }   

   private void printSelectBox( String name, ASPBuffer aspbuf, String key, String tag, boolean mandatory, int size )
   {
      mobile_out.append("<select class=\"selectbox\" size=");
      mobile_out.appendInt(size);
      mobile_out.append(" name=\"",name,"\" ");

      if ( tag != null )
         mobile_out.append(tag);
      mobile_out.append(">");
      if ( mandatory )
         mobile_out.append( getASPHTMLFormatter().populateMandatoryListBox(aspbuf, key) );
      else
         mobile_out.append( getASPHTMLFormatter().populateListBox(aspbuf, key) );
      mobile_out.append("</select>");
   }   

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * with an empty tag at the beginning.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   protected final void printIIDSelectBox( ASPField fld ) throws FndException
   {
      printIIDSelectBox( fld, null, null, false );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * with an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   protected final void printIIDSelectBox( ASPField fld, String key ) throws FndException
   {
      printIIDSelectBox( fld, key, null, false );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * with an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   protected final void printIIDSelectBox( ASPField fld, String key, String tag ) throws FndException
   {
      printIIDSelectBox( fld, key, tag, false );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * without an empty tag at the beginning.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */   
   protected final void printMandatoryIIDSelectBox( ASPField fld ) throws FndException
   {
      printIIDSelectBox( fld, null, null, true );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * without an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */   
   protected final void printMandatoryIIDSelectBox( ASPField fld, String key ) throws FndException
   {
      printIIDSelectBox( fld, key, null, true );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * without an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */   
   protected final void printMandatoryIIDSelectBox( ASPField fld, String key, String tag ) throws FndException
   {
      printIIDSelectBox( fld, key, tag, true );
   }

   private void printIIDSelectBox( ASPField fld, String key, String tag, boolean mandatory ) throws FndException
   {
      if ( !fld.isSelectBox() )
         throw new FndException("FNDPRVNSELBOX: The field &1 is not defined as Select Box.", fld.getName());
      printSelectBox( fld.getName(), fld.getIidClientValuesBuffer(), key, tag, mandatory, 1 );
   }   

   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * @param name name of the button
    *
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    */   
   protected final void printButton(String name, String value, String tag)
   {
      printButton(name, value, tag, 0, true);
   }
   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * @param name name of the button
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    * @param sec_objects comma seperated list of security objects
    */
   protected final void printButton(String name, String value, String tag, String sec_objects)
   {
      printButton(name, value, tag, 0, isObjectAccessible(sec_objects));
   }

   
   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * The button will submit the request (current page).
    */   
   protected final void printSubmitButton(String name, String value, String tag)
   {
      printButton(name, value, tag, 1, true);
   }

   /**
    * Append a named submit button input tag to the HTML-output and apply predefined styles.
    * @param name name of the button
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    * @param sec_objects comma seperated list of security objects
    */   
   protected final void printSubmitButton(String name, String value, String tag, String sec_objects)
   {
      printButton(name, value, tag, 1,isObjectAccessible(sec_objects));
   }

   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * The button will reset the request (current page).
    */   
   protected final void printResetButton(String name, String value, String tag)
   {
      printButton(name, value, tag, 2, true);
   }
   
   /**
    * Append a named reset button input tag to the HTML-output and apply predefined styles.
    * @param name name of the button
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    * @param sec_objects comma seperated list of security objects
    */     
   protected final void printResetButton(String name, String value, String tag, String sec_objects)
   {
      printButton(name, value, tag, 2, isObjectAccessible(sec_objects));
   }

   private void printButton( String name, String value, String tag, int type, boolean has_security )
   {
      switch ( type ){
      case 0:
         mobile_out.append("<input class=\"button\" type=\"button\"");
         break;
      case 1:
         mobile_out.append("<input class=\"button\" type=\"submit\"");
         break;
      default:
         mobile_out.append("<input class=\"button\"' type=\"reset\"");
      }

      mobile_out.append(" name=\"",name);
      mobile_out.append("\" value=\"", getASPManager().HTMLEncode(getASPManager().translate(value),true), "\" ");

      if ( tag != null )
         mobile_out.append(tag);

      mobile_out.append(!has_security?" disabled":"", ">");
   }
}
