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
 * File        : RSSReader.java
 * Description : A portlet for reading and displaying RSS Feeds.
 * Notes       : This portlet uses Informa -- RSS Library for Java  (Under Lesser GPL license) 
 *               http://sourceforge.net/projects/informa 
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Rifki R  2006-Jul-20 - Created.
 *    Buddika  2006-Nov-24 - Modified printContents() to throw a custom error message when a MalformedURLException occurs.
 *    Buddika  2006-Dec-28 - Bug id: 62722, Added imports for new RSS parse library and Modified printContents() to use new RSS parse library.
 *    Sasanka  2007-Jan-30 - Bug id: 63240, Changed printContents() to avoid causing NullPointerException.
 *    buhilk   2007-Feb-09 - Bug id: 63433, Added csv support
 *    buhilk   2007-Feb-14 - Bug id: 63526, Modified printContents() so that it handles changes in the rss feed.
 *    sumelk   2007-Aug-01 - Merged the corrections for Bug 66964, Corrected localization errors.
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.portlets;

import com.sun.cnpi.rss.elements.*;
import com.sun.cnpi.rss.parser.*;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;

public class RSSReader extends ASPPortletProvider
{
    
   private transient String rss_url;
   private transient String title;   
   private transient String no_of_items;   
   private transient boolean show_item_description;   
   private transient int int_no_of_items;
   private transient boolean show_channel_title;
   private transient boolean show_channel_image;
   private transient boolean show_shaded_background;
   private transient boolean update_when_minimized;
   private static final String DEFAULT_NO_OF_ITEMS = "5";
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.RSSReader");   
   private static Pattern header_start_tag = Pattern.compile("<(h|H)\\d>");
   private static Pattern header_end_tag = Pattern.compile("</(h|H)\\d>");
   
   public RSSReader( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }

   
    public ASPPoolElement clone( Object obj ) throws FndException
   {
      if(DEBUG) debug(this+": RSSReader.clone()");

      RSSReader page = (RSSReader)(super.clone(obj));

      page.title   = null;
      page.show_channel_title = false;
      page.rss_url   = null;
      page.no_of_items = null;
      page.update_when_minimized = false;            
      page.show_channel_image = false;   
      page.show_item_description = false; 
      page.show_shaded_background = true; 
      
      return page;
   }


   protected void doReset() throws FndException
   {
      title   = null;      
      rss_url   = null;
      no_of_items = null;
      update_when_minimized = false;      
      show_channel_title = false;      
      show_channel_image = false;   
      show_item_description = false;      
      show_shaded_background = true;

      super.doReset();
   }
   
 protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": RSSReader.preDefine()");

      init();
   }


   protected void init()
   {
      if(DEBUG) debug(this+": RSSReader.init()");

      title = readProfileValue("TITLE", translate(getDescription()) );
      rss_url   = readProfileValue("RSS_URL");
      no_of_items = readProfileValue("NO_OF_ITEMS", DEFAULT_NO_OF_ITEMS);                  
      show_channel_image = readProfileFlag("SHOW_CHANNEL_IMAGE", false);
      show_channel_title = readProfileFlag("SHOW_CHANNEL_TITLE", false);
      show_item_description = readProfileFlag("SHOW_ITEM_DESC", false);      
      show_shaded_background = readProfileFlag("SHOW_SHADED_BG", true);      
      update_when_minimized = readProfileFlag("UPDATE_WHEN_MINIMIZED", false);      
      try{
        int_no_of_items = Integer.parseInt(no_of_items);
      }
      catch(NumberFormatException e){
        int_no_of_items = Integer.parseInt(DEFAULT_NO_OF_ITEMS);
      }
   }
   
  public String getTitle(int mode)
   {
      if(DEBUG) debug(this+": RSSReader.getTitle()");
      
      if( mode==MINIMIZED )
         return title;
      else if( mode==MAXIMIZED )
         return title;
      else
         return translate("FNDRSSREADERCUSTTIT: Customize &1 Portlet",title);
   }


   public static String getDescription()
   {
      return "FNDRSSREADERDESC: RSS Reader";
   }

   public boolean canCustomize()
   {
      return true; 
   }

   public void printCustomBody() throws FndException
   {
      if(DEBUG) debug(this+": RSSReader.printCustomBody()");

      printNewLine();

      beginTransparentTable();       
        beginTableBody();
          beginTableCell();            
            printText("FNDRSSREADERCUSTOMTITLE: Portlet title:");
          endTableCell();
          beginTableCell(2);
            printField("TITLE",title,50);
          endTableCell();              
        nextTableRow();
          printTableCell("FNDRSSREADERURL: RSS Feed URL:");
          beginTableCell();
            printField("RSS_URL",readAbsoluteProfileValue("RSS_URL"),100);            
          endTableCell();          
        nextTableRow();
          printTableCell("FNDRSSNOOFITEMS: Number of items to display:");
          beginTableCell(2);
            printField("NO_OF_ITEMS",no_of_items,5);
          endTableCell();
        nextTableRow();
          printTableCell("FNDRSSSHOWCHANNELTITCHKBOX: Show channel title");      
          beginTableCell();
            printCheckBox("SHOW_CHANNEL_TITLE", show_channel_title);
          endTableCell();          
        nextTableRow();         
          printTableCell("FNDRSSSHOWHEADIMGCHKBOX: Show channel header image");         
          beginTableCell();
            printCheckBox("SHOW_CHANNEL_IMAGE", show_channel_image);
          endTableCell();          
        nextTableRow();         
          printTableCell("FNDRSSSHOWDETAILCHKBOX: Show details of each item");   
          beginTableCell();
            printCheckBox("SHOW_ITEM_DESC", show_item_description);
          endTableCell();                 
        nextTableRow();         
          printTableCell("FNDRSSSHOWSHADEDBACKGROUND: Show shaded background");   
          beginTableCell();
            printCheckBox("SHOW_SHADED_BG", show_shaded_background);
          endTableCell();            
         nextTableRow();         
          printTableCell("FNDURLBOXMINCHKBOX: Update when minimized:");
          beginTableCell();
            printCheckBox("UPDATE_WHEN_MINIMIZED", update_when_minimized);
          endTableCell();      

        endTableBody();
      endTable();      

      
   }
   
   public void submitCustomization()
   {
      if(DEBUG) debug(this+": RSSReader.submitCustomization()");
            
      title = readValue("TITLE");      
      writeProfileValue("TITLE", title );      
      
      rss_url = readValue("RSS_URL");
      writeProfileValue("RSS_URL", readAbsoluteValue("RSS_URL"));

      no_of_items = readValue("NO_OF_ITEMS");
      try
      {
         int_no_of_items=Integer.parseInt(no_of_items);
      }
      catch(NumberFormatException e)
      {
         no_of_items = DEFAULT_NO_OF_ITEMS;
      }
      writeProfileValue("NO_OF_ITEMS",no_of_items);

      show_channel_title = "TRUE".equals(readValue("SHOW_CHANNEL_TITLE"));      
      writeProfileFlag("SHOW_CHANNEL_TITLE", show_channel_title);
      
      show_channel_image = "TRUE".equals(readValue("SHOW_CHANNEL_IMAGE"));      
      writeProfileFlag("SHOW_CHANNEL_IMAGE", show_channel_image);
      
      show_item_description = "TRUE".equals(readValue("SHOW_ITEM_DESC"));      
      writeProfileFlag("SHOW_ITEM_DESC", show_item_description);           
      
      show_shaded_background = "TRUE".equals(readValue("SHOW_SHADED_BG"));      
      writeProfileFlag("SHOW_SHADED_BG", show_shaded_background);           
      
      update_when_minimized = "TRUE".equals(readValue("UPDATE_WHEN_MINIMIZED"));      
      writeProfileFlag("UPDATE_WHEN_MINIMIZED", update_when_minimized);
      

   }
   
   public void printContents() throws FndException
   {
         
         if(!isMaximized() && !update_when_minimized)
         {
                printNewLine();
                printLink(translate("FNDRSSPORTLETURLNOTLOAD1: Press &&update portlet&& to connect to '&1'.",rss_url),"javascript:submitPortlet('" + getId() + "')");
                printNewLine();
                printLink(translate("FNDRSSPORTLETURLNOTLOAD2: You can also &&customize&& the portlet to update even when minimized."),"javascript:customizeBox('" + getId() + "')");                     
         }
         else if (Str.isEmpty(rss_url))
             printLink(translate("FNDRSSPORTLETURLNOTLOAD3: Go to &&customize&& and provide a valid URL for the RSS Feed."),"javascript:customizeBox('" + getId() + "')");                     
         else
         {
             try 
             { 
                 if(DEBUG) debug(this+": RSSReader.printContents() connecting to "+rss_url);
                 SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
                 DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.MEDIUM,new Locale(getASPManager().getLanguageCode()));
                 long start_load = System.currentTimeMillis(); //only for debuggin
                 URL feed = new URL(rss_url);                   
                 RssParser parser = RssParserFactory.createDefault();
                 Rss rss = parser.parse(feed);                 
                 Channel channel = rss.getChannel();
                
                 if (show_channel_title || show_channel_image)
                 {                    
                     beginTransparentTable();
                     beginTableTitleRow();                                            
                     //check if channel has an image
                     if (show_channel_image)
                     {
                       Image cimg = channel.getImage();             
                       if (cimg!=null)
                       {                                                                            
                          beginTableCell(LEFT);
                           printImage(cimg.getUrl().toString());
                          endTableCell();   
                       }
                     }                     
                     if (show_channel_title)
                     {
                       title = channel.getTitle().toString();                       
                       printTableCell(title);
                     }                     
                     endTableTitleRow();  
                     endTable();
                 }

                 printNewLine();
                                  
                 if (show_shaded_background)
                   beginTable();
                 else
                   beginTransparentTable();
                 
                 beginTableBody();
                 Collection items = channel.getItems();
                 if(items==null)
                    items = rss.getChildren();
                 int i=1;
                 int size = items.size(); //total no of items available in the feed
                 for (Iterator iter = items.iterator(); iter.hasNext();) 
                 {
                   if (i>int_no_of_items)  break; //show only specified no of items
                   Item item = null;
                   try
                   {
                      item = (Item)iter.next();
                   }
                   catch (ClassCastException cce)
                   {
                      continue;
                   }
                        
                   //if (DEBUG) debug(item.getTitle());
                   beginTableCell();
                   if(item.getLink() != null)
                     printLink(item.getTitle().toString(),"javascript:showNewBrowser('"+item.getLink().toString()+"')");
                   else
                     printText(item.getTitle().toString());
                   printNewLine();
                   PubDate date_posted = item.getPubDate();
                   if (date_posted!=null)
                      try
                      {
                         printText("["+df.format(sdf.parse(date_posted.toString()))+"]");
                      }
                      catch(Exception e){}
                   printNewLine();
                   if (show_item_description)  //show item's desc
                   {                      
                     String item_desc = item.getDescription().toString();
                     if (!Str.isEmpty(item_desc)) //some feeds don't provide item descriptions
                     {
                         Matcher matcher = header_start_tag.matcher(item_desc);
                         //try to make the RSS portlet L&F consistent with other IFS portlets
                         item_desc = matcher.replaceAll("<h5>");
                         matcher = header_end_tag.matcher(item_desc);
                         item_desc = matcher.replaceAll("</h5>");
                         item_desc = item_desc.replaceAll("<td>","<td class=\"normalTextValue\">");
                         appendToHTML("<font class=\"normalTextValue\">");                         
                         appendToHTML(item_desc);   
                         //if (DEBUG) debug(item_desc);
                         appendToHTML("</font>");                                                                                              
                     }
                   }
                   endTableCell();                   
                   nextTableRow();
                   i++;
                 }
                 endTableBody();
                 endTable();  
                 printNewLine();
                 String p1 = Integer.toString(i-1);
                 String p2 = Integer.toString(size);
                 printText(translate("FNDRSSPORTLETITEMSOF: &1 of &2 items displayed.",p1,p2));
                 long end_load = System.currentTimeMillis(); //only for debuggin
                 if(DEBUG) debug(this+": RSSReader.printContents() portlet done in "+(end_load-start_load)/1000+" seconds");
             } 
            catch (MalformedURLException mue) {
                getASPLog().disableAdditionalErrorMsg();
                throw new FndException(translate("FNDRSSPORTLETURLNOTALLOW: The RSS feed URL entered is invalid."));
            }
            catch (IOException ioe) {
                getASPLog().disableAdditionalErrorMsg();
                throw new FndException(translate("FNDRSSPORTLETURLNOTALLOW: The RSS feed URL entered is invalid."));
            } 
            catch (RssParserException rpe) {
                getASPLog().disableAdditionalErrorMsg();
                throw new FndException(translate("FNDRSSPORTLETURLNOTALLOW: The RSS feed URL entered is invalid."));
            } 
        }

   }
}