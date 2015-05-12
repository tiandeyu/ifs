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
 * File        : ASPImageGenerator.java
 * Description : Generates the images in common/images.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Stefan M  2000-Oct-09 - Created.
 *    Stefan M  2000-Nov-27 - Rewritten.
 *    Daniel S  2001-May-28 - Fixed a bug that made it impossible to change background color in ASPConfig.
 *    Daniel S  2001-jan-06 - Phypath removed from ASPConfig.
 *    ChandanaD 2003-May-22 - Addded a new item "Home"
 *    ChandanaD 2003-May-29 - Added two new items for "Home" and "Navigate" to show when popups are there.
 *    Ramila H  2004-05-26  - Generated images for all multi-languages
 * ----------------------------------------------------------------------------
 *
 * New Comments:
 * 2006/09/13 gegulk Bug id 60473, Modified the method generateImage() to support RTL languages
 */

package ifs.fnd.image;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
//import ifs.fnd.buffer.*;

import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;


//import com.ms.awt.FontX;


public class ImageGenerator implements ImageObserver
{

   public static boolean DEBUG    = Util.isDebugEnabled("ifs.fnd.image.ImageGenerator");

   ASPManager mgr;

   Image dummy_image;
   Graphics dummy_g;
   MediaTracker trk;
   Frame frame = new Frame();

   Vector items = new Vector();

   int IMG_HEIGHT;      // height of new images
   int IMG_LEFTPAD;     // between left edge and icon
   int IMG_RIGHTPAD;    // between text and right edge
   int IMG_MIDDLEPAD;   // between icon and text

   static final Color WHITE            = new Color(255,255,255);
   static final Color GREY             = new Color(214,214,214);
   static final Color DARKGREY         = new Color(111,111,111);
   static final Color BLACK            = new Color(0,0,0);

   static final int COMMANDBAR         = 0;
   static final int COMMANDBAR_HOVER   = 1;
   static final int TOOLBAR            = 2;
   static final int TOOLBAR_HOVER      = 3;

   // Types                      Default values
   Color commandbar_bg           = GREY;
   Color commandbar_text         = BLACK;
   Color commandbar_hover_bg     = GREY;
   Color commandbar_hover_text   = BLACK;
   Color toolbar_bg              = WHITE;
   Color toolbar_text            = DARKGREY;
   Color toolbar_hover_bg        = WHITE;
   Color toolbar_hover_text      = BLACK;

   class item
   {
      String label;
      String file;
      boolean hover;
      boolean bold;
      int extra_pixel;
      Color background;
      Color textcolor;

      item (String l, String f, int type)
      {
         label = l;
         file = f;
         extra_pixel = 1;
         background = GREY;
         textcolor = BLACK;

         if(type==TOOLBAR)
         {
            textcolor=toolbar_text;
            background=toolbar_bg;
            bold = true;
         }
         else if(type==TOOLBAR_HOVER)
         {
            textcolor=toolbar_hover_text;
            background=toolbar_hover_bg;
            hover = true;
            bold = true;
         }
         else if(type==COMMANDBAR)
         {
            textcolor=commandbar_text;
            background=commandbar_bg;
         }
         else if(type==COMMANDBAR_HOVER)
         {
            textcolor=commandbar_hover_text;
            background=commandbar_hover_bg;
            hover = true;
            bold = true;
         }
      }

   }

   //==========================================================================
   //  Images
   //==========================================================================
/*
   public static void main (String[] argv)
   {
      ImageGenerator gen = new ImageGenerator();
      gen.generate();
      System.exit(0);
   }
*/
   Color getColor(String type)
   {
      Color new_color = new Color(Integer.parseInt(mgr.getConfigParameter("IMAGE_GENERATOR/TYPES/" + type + "/RED","0")),Integer.parseInt(mgr.getConfigParameter("IMAGE_GENERATOR/TYPES/" + type + "/GREEN","0")),Integer.parseInt(mgr.getConfigParameter("IMAGE_GENERATOR/TYPES/" + type + "/BLUE","0")));
      return new_color;
   }

   public ImageGenerator(ASPManager m)
   {
      mgr = m;

      IMG_HEIGHT = Integer.parseInt(mgr.getConfigParameter("IMAGE_GENERATOR/IMAGE/HEIGHT","19"));
      IMG_LEFTPAD = Integer.parseInt(mgr.getConfigParameter("IMAGE_GENERATOR/IMAGE/LEFTPAD","6"));
      IMG_RIGHTPAD = Integer.parseInt(mgr.getConfigParameter("IMAGE_GENERATOR/IMAGE/RIGHTPAD","6"));
      IMG_MIDDLEPAD = Integer.parseInt(mgr.getConfigParameter("IMAGE_GENERATOR/IMAGE/MIDDLEPAD","8"));

      if(!mgr.getConfigParameter("IMAGE_GENERATOR/TYPES/COMMANDBAR/BACKGROUND/RED","").equals(""))
      {
         commandbar_bg           = getColor("COMMANDBAR/BACKGROUND");
         commandbar_text         = getColor("COMMANDBAR/TEXTCOLOR");
      }
      if(!mgr.getConfigParameter("IMAGE_GENERATOR/TYPES/COMMANDBAR_HOVER/BACKGROUND/RED","").equals(""))
      {
         commandbar_hover_bg     = getColor("COMMANDBAR_HOVER/BACKGROUND");
         commandbar_hover_text   = getColor("COMMANDBAR_HOVER/TEXTCOLOR");
      }
      if(!mgr.getConfigParameter("IMAGE_GENERATOR/TYPES/TOOLBAR/BACKGROUND/RED","").equals(""))
      {
         toolbar_bg              = getColor("TOOLBAR/BACKGROUND");
         toolbar_text            = getColor("TOOLBAR/TEXTCOLOR");
      }
      if(!mgr.getConfigParameter("IMAGE_GENERATOR/TYPES/COMMANDBAR/BACKGROUND/RED","").equals(""))
      {
         toolbar_hover_bg        = getColor("TOOLBAR_HOVER/BACKGROUND");
         toolbar_hover_text      = getColor("TOOLBAR_HOVER/TEXTCOLOR");
      }

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELACTIONS: Actions"),mgr.getConfigParameter("COMMAND_BAR/ICONS/ACTIONS/NORMAL","actions.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELACTIONS: Actions"),mgr.getConfigParameter("COMMAND_BAR/ICONS/ACTIONS/HOVER","actions_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELDELETE: Delete"),mgr.getConfigParameter("COMMAND_BAR/ICONS/DELETE/NORMAL","delete.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELDELETE: Delete"),mgr.getConfigParameter("COMMAND_BAR/ICONS/DELETE/HOVER","delete_hov.png"),COMMANDBAR_HOVER));
      
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCONFIG: Manage Portal"),mgr.getConfigParameter("PAGE/TOOLBAR/CONFIG/NORMAL","config.png"),TOOLBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCONFIG: Manage Portal"),mgr.getConfigParameter("PAGE/TOOLBAR/CONFIG/HOVER","config_hov.png"),TOOLBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCOUNT: Count"),mgr.getConfigParameter("COMMAND_BAR/ICONS/COUNTFIND/NORMAL","count.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCOUNT: Count"),mgr.getConfigParameter("COMMAND_BAR/ICONS/COUNTFIND/HOVER","count_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELEDIT: Edit"),mgr.getConfigParameter("COMMAND_BAR/ICONS/EDITROW/NORMAL","edit.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELEDIT: Edit"),mgr.getConfigParameter("COMMAND_BAR/ICONS/EDITROW/HOVER","edit_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCANCELEDIT: Cancel Edit"),mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELEDIT/NORMAL","edit_cancel.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCANCELEDIT_HOVER: Cancel Edit"),mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELEDIT/HOVER","edit_cancel_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELFIND: Find"),mgr.getConfigParameter("COMMAND_BAR/ICONS/FIND/NORMAL","find.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELFIND: Find"),mgr.getConfigParameter("COMMAND_BAR/ICONS/FIND/HOVER","find_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCANCELFIND: Cancel"),mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELFIND/NORMAL","find_find.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCANCELFIND: Cancel"),mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELFIND/HOVER","find_find_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELHELP: Help"),mgr.getConfigParameter("PAGE/TOOLBAR/HELP/NORMAL","help2.png"),TOOLBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELHELP: Help"),mgr.getConfigParameter("PAGE/TOOLBAR/HELP/HOVER","help2_hov.png"),TOOLBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELNAVIGATE: Navigate"),mgr.getConfigParameter("PAGE/TOOLBAR/NAVIGATE/NORMAL","navigate.png"),TOOLBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELNAVIGATE: Navigate"),mgr.getConfigParameter("PAGE/TOOLBAR/NAVIGATE/HOVER","navigate_hov.png"),TOOLBAR_HOVER));
      
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELNAVIGATE: Navigate"),mgr.getConfigParameter("PAGE/TOOLBAR/NAVIGATEPOP/NORMAL","navigatepop.png"),TOOLBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELNAVIGATE: Navigate"),mgr.getConfigParameter("PAGE/TOOLBAR/NAVIGATEPOP/HOVER","navigatepop_hov.png"),TOOLBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELNEWROW: New"),mgr.getConfigParameter("COMMAND_BAR/ICONS/NEWROW/NORMAL","new.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELNEWROW: New"),mgr.getConfigParameter("COMMAND_BAR/ICONS/NEWROW/HOVER","new_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELBACK: Overview"),mgr.getConfigParameter("COMMAND_BAR/ICONS/BACK/NORMAL","return.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELBACK: Overview"),mgr.getConfigParameter("COMMAND_BAR/ICONS/BACK/HOVER","return_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCANCELNEW: Cancel"),mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELNEW/NORMAL","new_cancel.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELCANCELNEW: Cancel"),mgr.getConfigParameter("COMMAND_BAR/ICONS/CANCELNEW/HOVER","new_cancel_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELOKFIND: Ok"),mgr.getConfigParameter("COMMAND_BAR/ICONS/OKFIND/NORMAL","ok.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELOKFIND: Ok"),mgr.getConfigParameter("COMMAND_BAR/ICONS/OKFIND/HOVER","ok_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELSAVERETURN: Save & Return"),mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVERETURN/NORMAL","save.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELSAVERETURN: Save & Return"),mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVERETURN/HOVER","save_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELSAVENEW: Save & New"),mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVENEW/NORMAL","savennew.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELSAVENEW: Save & New"),mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVENEW/HOVER","savennew_hov.png"),COMMANDBAR_HOVER));

      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELSAVE: Save"),mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVE/NORMAL","save.png"),COMMANDBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELSAVE: Save"),mgr.getConfigParameter("COMMAND_BAR/ICONS/SAVE/HOVER","save_hov.png"),COMMANDBAR_HOVER));
      
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELUPDATEPORTAL: Refresh"),mgr.getConfigParameter("PAGE/TOOLBAR/UPDATE_PORTAL/NORMAL","update.png"),TOOLBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELUPDATEPORTAL: Refresh"),mgr.getConfigParameter("PAGE/TOOLBAR/UPDATE_PORTAL/HOVER","update_hov.png"),TOOLBAR_HOVER));
      
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELPERSONALPORTAL: Home"),mgr.getConfigParameter("PAGE/TOOLBAR/PORTAL/NORMAL","home.png"),TOOLBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELPERSONALPORTAL: Home"),mgr.getConfigParameter("PAGE/TOOLBAR/PORTAL/HOVER","home_hov.png"),TOOLBAR_HOVER));
      
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELPERSONALPORTAL: Home"),mgr.getConfigParameter("PAGE/TOOLBAR/PORTALPOP/NORMAL","homepop.png"),TOOLBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELPERSONALPORTAL: Home"),mgr.getConfigParameter("PAGE/TOOLBAR/PORTALPOP/HOVER","homepop_hov.png"),TOOLBAR_HOVER));
      
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELOPTIONS: Options"),mgr.getConfigParameter("PAGE/TOOLBAR/PORTAL/NORMAL","options.png"),TOOLBAR));
      items.addElement(new item(mgr.translateJavaText("FNDIMAGELABELOPTIONS: Options"),mgr.getConfigParameter("PAGE/TOOLBAR/PORTAL/HOVER","options_hov.png"),TOOLBAR_HOVER));
   }

   public void generate() throws Exception
   {
      generate(mgr.getDefaultLanguage());
   }
   
   public void generate(String lang_code) throws Exception
   {
      frame.addNotify();
      trk = new MediaTracker(frame);

      dummy_image = frame.createImage(200,IMG_HEIGHT);
      dummy_g = dummy_image.getGraphics();

//      Font normal_font = new Font("Verdana",Font.PLAIN,10);
//      Font hov_font = new Font("Verdana",Font.BOLD,10);

      String font_face = mgr.getConfigParameter("LANGUAGE/"+lang_code+"/IMAGE_GENERATION_TEXT/FONT_FACE","Verdana");
      int font_size = Integer.parseInt(mgr.getConfigParameter("LANGUAGE/"+lang_code+"/IMAGE_GENERATION_TEXT/FONT_SIZE","9"));
      Font normal_font = new Font(font_face,Font.PLAIN,font_size);//FontX.getFont(font_face,FontX.PLAIN,font_size,0);
      Font hov_font = new Font(font_face,Font.BOLD,font_size);//FontX.getFont(font_face,FontX.BOLD,font_size,0);

      //FontMetrics normal_metrics = dummy_g.getFontMetrics(normal_font);
      FontMetrics hov_metrics = dummy_g.getFontMetrics(hov_font);

      Toolkit tk = Toolkit.getDefaultToolkit();

//      String[] fonts = FontX.getFontList();
//      for(int i=0;i<Array.getLength(fonts);i++)
//         debug(fonts[i]);

      String icon_loc  = mgr.getASPConfig().getApplicationPhyPath() + mgr.getConfigParameter("APPLICATION/LOCATION/ICONS"     ,"common"+ifs.fnd.os.OSInfo.OS_SEPARATOR+"images"+ifs.fnd.os.OSInfo.OS_SEPARATOR+"icons"     +ifs.fnd.os.OSInfo.OS_SEPARATOR).replace('/',ifs.fnd.os.OSInfo.OS_SEPARATOR);
      String image_loc = mgr.getASPConfig().getApplicationPhyPath() + mgr.getConfigParameter("APPLICATION/LOCATION/TRANSLATED"+lang_code+ifs.fnd.os.OSInfo.OS_SEPARATOR,"common"+ifs.fnd.os.OSInfo.OS_SEPARATOR+"images"+ifs.fnd.os.OSInfo.OS_SEPARATOR+"translated"+ifs.fnd.os.OSInfo.OS_SEPARATOR+lang_code+ifs.fnd.os.OSInfo.OS_SEPARATOR).replace('/',ifs.fnd.os.OSInfo.OS_SEPARATOR);

      File image_lang_dir = new File(image_loc);
      
      if (!(image_lang_dir.exists() && image_lang_dir.isDirectory()))
         image_lang_dir.mkdir();
      
      if(DEBUG) debug("iconloc: " + icon_loc);
      if(DEBUG) debug("imageloc: " + image_loc);

      FileOutputStream out;

      for(int i=0;i<items.size();i++)
      {
            item icon = (item)items.elementAt(i);

            File f = new File(icon_loc + icon.file);
            if(!f.exists())
            {
               if(DEBUG) debug("ImageGenerator: Image file '" + icon.file + "' was not found. Skipping.");
               continue;
            }

            int size = (int)f.length();

            byte[] bb = new byte[size];
            FileInputStream in = new FileInputStream(f);
            in.read(bb);
            in.close();

            if(DEBUG) debug("SIZE (" + icon.file + ") = " + size);

            Image image = tk.createImage(bb);

            trk.addImage(image,i);

            trk.waitForID(i);

            if(trk.isErrorAny())
               if(DEBUG) debug("trk ERROR");

            if(DEBUG) debug("Icon width: " + image.getWidth(this));
            if(DEBUG) debug("Icon height: " + image.getHeight(this));

            File result_file = new File(image_loc + icon.file);
            out = new FileOutputStream(result_file);


            Image new_image = generateImage(image,icon.label,icon.bold ? hov_font : normal_font,hov_metrics,icon.hover,icon.extra_pixel,icon.background,icon.textcolor);

            // Write image to file
            PngEncoder encoder = new PngEncoder(new_image);
            encoder.setCompressionLevel(9);
            out.write(encoder.pngEncode());
            out.close();
      }

   }

   Image generateImage(Image icon,String text,Font font,FontMetrics metrics,boolean border,int extra_pixel,Color background,Color textcolor) throws Exception
   {
      // Calculate the width of the new image (can't change width once created, in Java 1.1)
      int string_width = 0;
      for(int i=0;i<text.length();i++)
      {
         int char_width = metrics.charWidth(text.charAt(i));
         string_width += char_width + extra_pixel;
      }
      int image_width = IMG_LEFTPAD+icon.getWidth(this)+IMG_MIDDLEPAD+string_width+IMG_RIGHTPAD;

      Image new_image = frame.createImage(image_width,IMG_HEIGHT);
      Graphics g = new_image.getGraphics();

      if(new_image == null)
         throw new Exception("ImageGenerator: Background image is null. Aborting.");

      // Background
      g.setColor(background);
      g.fillRect(0,0,new_image.getWidth(this),IMG_HEIGHT);

      // Icon
      int y = 1; //java.lang.Math.round((IMG_HEIGHT - icon.getWidth(this)) / 2);
      if(mgr.isRTL())
      { 
      // Text
         g.setColor(textcolor);
         g.setFont(font);
      
         int char_width = 0;
         for(int i=0;i<text.length();i++)
         {
            char_width += metrics.charWidth(text.charAt(i));
         }
       
         int current_x = image_width -(IMG_RIGHTPAD + icon.getWidth(this) + IMG_MIDDLEPAD + char_width  );

         g.drawString(text,current_x,IMG_HEIGHT-5);
         g.drawImage(icon,image_width-IMG_RIGHTPAD-icon.getWidth(this),y,this);
      }
      else
      {
      g.drawImage(icon,IMG_LEFTPAD,y,this);

      // Text
      g.setColor(textcolor);
      g.setFont(font);

      int current_x = IMG_LEFTPAD + icon.getWidth(this) + IMG_MIDDLEPAD;
      for(int i=0;i<text.length();i++)
      {
         int char_width = metrics.charWidth(text.charAt(i));
         g.drawString(String.valueOf(text.charAt(i)),current_x,IMG_HEIGHT-5);
         current_x += char_width + extra_pixel;
      }
      }
      // Border
      if(border)
      {
         g.setColor(new Color(67,67,67));
         g.drawLine(0,IMG_HEIGHT-1,image_width,IMG_HEIGHT-1);
         g.drawLine(image_width-1,1,image_width-1,IMG_HEIGHT);
         g.setColor(new Color(255,255,255));
         g.drawLine(0,0,0,IMG_HEIGHT-2);
         g.drawLine(0,0,image_width,0);
      }

      return new_image;

   }



   // ImageObserver interface implementation
   public boolean imageUpdate(
                                    Image img,
                                    int infoflags,
                                     int x,
                                     int y,
                                     int width,
                                     int height)
   {
      if(infoflags == ERROR)
         debug("imageupdate ERROR");
      if(infoflags == ABORT)
         debug("imageupdate ABORT");

      if(infoflags != ALLBITS)
         return true;
      else
      {
         debug("imageUpdate, ALLBITS");
         debug("width: " + width);
         return false;
      }
   }


   //==========================================================================
   //  Debugging, error
   //==========================================================================

   private static void debug( String line )
   {
//      System.out.println(line);
      Util.debug(line);
   }

}
