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
 * File        : Video.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 *    Ramila H  2000-Apr-27 - Created.
 *    Ramila H  2000-May-03 - Changed dynsrc with activeX and plugin controls.
 *    Jacek P   2000-Aug-24 - Updated to the latest release of Web Kit (3.0.0.A).
 *    Mangala   2001-May-31 - Log id #582: Replaced <img> tag with <embed>
 *    Suneth M  2001-Sep-13 - Changed CLSID to lowercase in printContents() to
 *                            avoid localization problems.
 *    Rifki R  2002-Apr-08 - Fixed translation problems caused by trailing spaces.
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

public class Video extends ASPPortletProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.URLBox");
   

   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================

   private transient String  mintitle;
   private transient String  maxtitle;
   private transient String  video_path;
   private transient String  player_ver;
   private transient boolean show_controls;
   private transient boolean auto_start;
   private transient String  player_width;
   private transient String  player_height;

   private transient boolean is_explorer;
   

   public Video( ASPPortal portal, String clspath )
   {
      super(portal,clspath);
   }


   public ASPPoolElement clone( Object obj ) throws FndException
   {
      Video page = (Video)(super.clone(obj));

      page.mintitle      = null;
      page.maxtitle      = null;
      page.video_path    = null;
      page.player_ver    = null;
      page.show_controls = false;
      page.auto_start    = true;
      page.player_width  = null;
      page.player_height = null;

      return page;
   }


   protected void doReset() throws FndException
   {
      mintitle      = null;
      maxtitle      = null;
      video_path    = null;
      player_ver    = null;
      show_controls = false;
      auto_start    = true;
      player_width  = null;
      player_height = null;

      super.doReset();
   }


   public static String getDescription()
   {
      return "FNDVIDDESC: Video Box";
   }


   public String getTitle( int mode )
   {
      if( mode==MINIMIZED )
         return mintitle;
      else if( mode==MAXIMIZED )
         return maxtitle;
      else
         return translate("FNDVIDCUSTTIT: Customize Video Box");
   }


   protected void preDefine() throws FndException
   {
      appendJavaScript("function customizeVideoBox(obj,id)",
                       "{",
                          "customizeBox(id);",
                       "}\n");
      appendJavaScript("function maximizeActiveXChooser(obj,id)\n",
                       "{\n",
                          "var cb  = getPortletField(id,'USEACTIVEX');\n",
                          "var row = eval(getPortletFieldName(id,'ActiveXRow'));\n",
                          "if(cb.checked)\n");
      appendJavaScript(      "row.style.display='block';\n",
                          "else\n",
                             "row.style.display='none';\n",
                       "}\n");
      init();
   }


   protected void init()
   {
      mintitle = readProfileValue("MINTITLE", translate(getDescription()) );
      maxtitle = readProfileValue("MAXTITLE", translate(getDescription()) );

      video_path    = readProfileValue("VIDEO_PATH",   null);
      player_ver    = readProfileValue("PLAYER_VER",   null);
      show_controls = readProfileFlag( "SHOW_CONTROLS",false);
      auto_start    = readProfileFlag( "AUTO_START",   true);
      player_width  = readProfileValue("PLAYER_WIDTH", null);
      player_height = readProfileValue("PLAYER_HEIGHT",null);

      is_explorer = getASPManager().isExplorer();
   }


   public void printContents() throws FndException
   {
      if (!Str.isEmpty(video_path))
      {    
         appendToHTML("<center>");
         if(is_explorer && player_ver==null)
         {
            // this solution doesn't require ActiveX, but works only in IE and doesn't show controls
            //appendToHTML("<img dynsrc=\"",video_path,"\" controls>");

            appendToHTML("<embed src=\"",video_path,"\" controller=false autoplay=true>");
            appendToHTML("</center>\n");
         }
         else
         {
            // ActiveX based solution that works in both IE and Netscape
            boolean newver = "5.1".equals(player_ver);
            appendToHTML("<object id=nsplayer",getId()," classid=\"");
            if(newver)
            {
               // MS Media Player Ver 5.1
               appendToHTML("clsid:22D6F312-B0F6-11D0-94AB-0080C74C7E95\" ");
               appendToHTML("codebase=\"http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701\"");
            }
            else
            {
               // NetShow 2.0
               appendToHTML("clsid:2179C5D3-EBFF-11CF-B6FD-00AA00B4E220\" ");
               appendToHTML("codebase=\"http://www.microsoft.com/netshow/download/en/nsasfinf.cab#Version=2,0,0,912\"");
            }
            appendToHTML("\n standby=\"");
            printText("FNDVIDLOAD: Loading Windows Media Player components...");
            appendToHTML("\" type=\"application/x-oleobject\">\n");

            // IE code
            appendToHTML("<param name=Filename value=\"",video_path,"\">\n");
            if(!auto_start)
               appendToHTML("<param name=AutoStart value=false>\n");
            if(newver && !show_controls)
               appendToHTML("<param name=ShowControls value=false>\n"); // default for 5.1 = true
            else if(!newver && show_controls)
               appendToHTML("<param name=ControlType value=2>\n");      // default for 2.0 = 0

            // Netscape code
            appendToHTML("<embed type=\"video/x-ms-asf-plugin\" \n");
            appendToHTML(" pluginspage=\"http://www.microsoft.com/windows/windowsmedia/en/download/default.asp\"\n");
            appendToHTML(" filename=\"",video_path,"\" controltype=2");
            appendToHTML(" width=",player_width==null?"290":player_width," height=",player_height==null?"250":player_height,">\n");
            appendToHTML("</embed>\n");
            appendToHTML("</object>\n");
            appendToHTML("</center>\n");
            printNewLine();
            printLink("FNDVIDNEWWIN: Start the presentation in the stand-alone Media Player.",video_path);
         }
      } 
      else
      {
         printNewLine();
         printText("FNDVIDINFO1: Enter path to the video clip on the");
	 printSpaces(1);
         printScriptLink("FNDVIDINFO2: Customization", "customizeVideoBox");
	 printSpaces(1);
         printText("FNDVIDINFO3: page.");
      }
   }


   public boolean canCustomize()
   {
      return true;
   }


   public void printCustomBody() throws FndException
   {
      printNewLine();

      beginTransparentTable();
        beginTableTitleRow();
          printTableCell("FNDVIDTITLEMSG: You can choose your own title for this portlet",2,0,LEFT);
        endTableTitleRow();

        beginTableBody();
          beginTableCell();
            printSpaces(5);
            printText("FNDVIDMAXTITLE: when maximized:");
          endTableCell();
          beginTableCell();
            printField("MAXTITLE",maxtitle,50);
          endTableCell();
        nextTableRow();
          beginTableCell();
            printSpaces(5);
            printText("FNDVIDMINTITLE: and when minimized:");
          endTableCell();
          beginTableCell();
            printField("MINTITLE",mintitle,50);
          endTableCell();
        nextTableRow();
          printTableCell(null,2);
        nextTableRow();
          printTableCell("FNDVIDURL: Enter URL to video clip:");
          beginTableCell();
            printField("VIDEO_PATH",video_path,is_explorer?100:50);
          endTableCell();
        nextTableRow();
          printTableCell(null,2);
        nextTableRow();
          if(is_explorer)
          {
               beginTableCell(2);
                 printCheckBox("USEACTIVEX",player_ver!=null,"maximizeActiveXChooser");
                 printText("FNDVIDCHOOSEVER: I want to use Microsoft Media Player ActiveX control instead of the built-in one.");
               endTableCell();
             nextTableRow("ActiveXRow");
               beginTableCell(2);
                 printCheckBox("NETSHOW2","2.0".equals(player_ver));
                 printText("FNDVIDCHOOSENS2: I prefer to use the older Player version NetShow 2.0.");
                 printNewLine();
                 printCheckBox("SHOW_CONTROLS",show_controls);
                 printText("FNDVIDSHOWCTRL: I want the Player to show its controls.");
                 printNewLine();
                 printCheckBox("AUTO_START",auto_start);
                 printText("FNDVIDAUTOSTART: I want the Player to start the movie automatically as soon as it is loaded.");
               endTableCell();
          }
          else
          {
               printTableCell("FNDVIDWIDTH: Video Player Width:");
               beginTableCell();
                 printField("PLAYER_WIDTH",player_width,5);
               endTableCell();
             nextTableRow();
               printTableCell("FNDVIDHEIGHT: Video Player Height:");
               beginTableCell();
                 printField("PLAYER_HEIGHT",player_height,5);
               endTableCell();
          }
        endTableBody();
      endTable();

      appendDirtyJavaScript("maximizeActiveXChooser('','",getId(),"');\n");
   }


   public void submitCustomization()
   {
      mintitle = readValue("MINTITLE");
      maxtitle = readValue("MAXTITLE");
      writeProfileValue("MINTITLE", mintitle );
      writeProfileValue("MAXTITLE", maxtitle );

      video_path = readValue("VIDEO_PATH"); 
      writeProfileValue("VIDEO_PATH", video_path );


      if(is_explorer)
      {
         if("TRUE".equals(readValue("USEACTIVEX")))
         {
            if("TRUE".equals(readValue("NETSHOW2")))
               player_ver = "2.0";
            else
               player_ver = "5.1";

            show_controls = "TRUE".equals(readValue("SHOW_CONTROLS"));
            writeProfileFlag("SHOW_CONTROLS",show_controls);

            auto_start    = "TRUE".equals(readValue("AUTO_START"));
            writeProfileFlag("AUTO_START",auto_start);
         }
         else
         {
            player_ver = null;
         }
         writeProfileValue("PLAYER_VER",player_ver);
      }
      else
      {
         player_width = readValue("PLAYER_WIDTH");
         try
         {
            Integer.parseInt(player_width);
         }
         catch(NumberFormatException e)
         {
            player_width = null;
         }
         writeProfileValue("PLAYER_WIDTH",player_width);

         player_height = readValue("PLAYER_HEIGHT");
         try
         {
            Integer.parseInt(player_height);
         }
         catch(NumberFormatException e)
         {
            player_height = null;
         }
         writeProfileValue("PLAYER_HEIGHT",player_height);
      }
   }
}
