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
 * File        : TiffImageGenerator.java
 * Description : Called by TiffViewer through AJAX to resize and generate image
 * Notes       :
 * ----------------------------------------------------------------------------
 * New Comments   :
 * 2008/08/01 rahelk Bug id 74809, created.
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.image.*;

import java.io.*;
import java.util.*;

import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.PlanarImage;
import javax.imageio.ImageIO;
import javax.media.jai.*;
import java.awt.color.*;
import java.awt.image.*;


public class TiffImageGenerator extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.TiffImageGenerator");
   
   private int image_width;
   private int image_height;
   private ArrayList print_image;
   //===============================================================
   // Construction
   //===============================================================
   public TiffImageGenerator(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   public void run()
   {
      ASPManager mgr = getASPManager();
      
      String action = mgr.getQueryStringValue("ACTION");
      String fileKey = mgr.getQueryStringValue("FILENAME");
      int height = Integer.parseInt(mgr.getQueryStringValue("HEIGHT"));
      int width  = Integer.parseInt(mgr.getQueryStringValue("WIDTH"));
      String stamp = mgr.getQueryStringValue("STAMP");

      if ("LOAD".equals(action))
      {
         if (DEBUG) debug("TiffImageGenerator.run: LOAD");
         
         int index  = Integer.parseInt(mgr.getQueryStringValue("PAGENO"));
         
         if (DEBUG) debug("TiffImageGenerator.run: index="+index);

         mgr.responseWrite(generateImage(fileKey,width,height,index,stamp)+"^"+image_width);
         mgr.endResponse();
      }
      else  //print
      {
         if (DEBUG) debug("TiffImageGenerator.run: PRINT");
         
         int fromPage = Integer.parseInt(mgr.getQueryStringValue("FROM"));
         int toPage = Integer.parseInt(mgr.getQueryStringValue("TO"));
         print_image = new ArrayList();
         
         if (DEBUG) debug("TiffImageGenerator.run: fromPage="+fromPage+" toPage="+toPage);

         for (int i=fromPage-1; i<toPage; i++)
         {
            String key = generateImage(fileKey,width,height,i,stamp);
            print_image.add(key);
         }

      }
         
   }
   
   private String generateImage(String file, int w, int h, int index, String stamp)
   {
      if (DEBUG) debug("TiffImageGenerator.generateImage: file="+file+" w="+w+" h="+h+" index="+index+" stamp="+stamp);
      
      ASPManager mgr = getASPManager();
      String key = null;
      
      try
      {
         String dest_path = mgr.getPhyPath("images");   //physical path on server where images are generated
         boolean dyna_cache_enabled= "Y".equals(mgr.getASPConfig().getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N"));

         String filename = file.substring(file.lastIndexOf("\\")+1,file.lastIndexOf("."));
         String inFile = filename+"_"+w+"x"+h+"_"+stamp+"_";

         OutputStream out=null;              

         if (dyna_cache_enabled)
            out = new ByteArrayOutputStream();
         else  
         {
            File outFile = new File(dest_path,inFile + index + ".png");
            out = new FileOutputStream(outFile);                   
         }                 

         TiffViewerCache.TiffSessionCache cache = TiffViewerCache.getInstance(mgr.getSessionId());
         
         PlanarImage pi = Resize(cache.getPlanarImageAt(file,index), w, h);
         ImageIO.write(pi, "png", out);

         image_width = pi.getWidth();
         image_height = pi.getHeight();

         key = inFile + index +".png"; 
         
         if (dyna_cache_enabled)
         {
            DynamicObjectCache.put(key, ((ByteArrayOutputStream)out).toByteArray(),"image/png",mgr); 
         }
      }
      catch (FndException e) 
      {
         mgr.showError(e.getMessage());
      }
      catch (IOException ioe) {
         mgr.showError(ioe.getMessage());
      }
      
      return key;
   }
   
   private PlanarImage Resize(PlanarImage i, int width, int height)
   {
      if (DEBUG) debug("TiffImageGenerator.Resize: w="+width+" h="+height);
      
		ParameterBlock pb = new ParameterBlock();
      float w_scale = width*1F/i.getWidth();
      float h_scale  = height*1F/i.getHeight();
      float scale = Math.min(w_scale,h_scale);

		pb.addSource(i);
		pb.add(scale);
		pb.add(scale);
		pb.add(0.0F);
		pb.add(0.0F);
      pb.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
      
      try
      {
         return JAI.create("SubsampleBinaryToGray", pb, null);
      }
      catch (Exception any)
      {
         return JAI.create("scale", pb, null);
      }
   }
   
   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      String tiff_img_path = "";
      boolean dyna_cache_enabled= "Y".equals(mgr.getASPConfig().getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N"));
      
      if (dyna_cache_enabled) //support for clustering by caching image in memory
         tiff_img_path = mgr.getApplicationPath()+"/"+DynamicObjectCache.URL_INDICATOR+"/";   //relative path for javascript    
      else
         tiff_img_path = mgr.getApplicationPath()+"/images/";   
      
      out.append("<HTML><BODY ONLOAD=\"window.print(); self.blur(); setTimeout('window.close()', 6000);\">\n");
      for (int i=0; i<print_image.size(); i++)
      {
         out.append("<IMG width=\""+image_width+"\" height=\""+image_height+"\" SRC=\""+tiff_img_path+print_image.get(i)+"\">\n");
         out.append("<BR><BR>");
      }
      out.append("</BODY></HTML>\n");                          
      
      return out;
   }

   
}
