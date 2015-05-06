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
 * File        : TiffViewer.java
 * Description : Generates HTML which reprsents a Tiff image viewer to be embedded 
 *               in web pages. This viewer supports multi-page Tiff files.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Udayani/  2002-Oct-09 - Created.
 *    Thwaragan               
 *    Rifki R   2004-Oct-04 - Fixed bugs and minor enhancements.
 *    Rifki R   2004-Oct-15 - Added Zoom and Print functionality.         
 *    Rifki R   2004-Oct-19 - Removed timestamps from splitted file names.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2008/08/01 rahelk Bug 74809, Changed to new structure using PlanarImage array stored in TiffViewerCahce
 *               2007/01/08           sadhlk
 *Bug Id 62477, Changed to split Tiff files into separate PNG files.
 *               2006/03/06           prralk
 * B134177 added constructor that takes a cookie name. Otherwise generate the cookie with
 * default name. 
 *               2006/02/24           prralk
 * B134177 when reloaded the zoom sets back to original. Fixed by keeping the width in a cookie 
 *
 *               2006/01/25           mapelk
 * Removed errorneous translation keys
 *
 * Revision 1.3  2005/11/07 08:16:35  mapelk
 * Introduced "persistant" att to Dynamic Objects and remove non persistent objects from the DynamicObjectCache in the first get.
 *
 * Revision 1.2  2005/11/06 08:12:09  mapelk
 * Viewer Bar resizes with the image when zoomIn/Out
 *
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/08 09:44:05  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.4  2005/04/07 13:44:56  riralk
 * Removed sesion id from dynamic cache key for business graphics since the cache is now in the session itself.
 *
 * Revision 1.3  2005/04/01 13:59:57  riralk
 * Moved Dynamic object cache to session - support for clustering
 *
 * Revision 1.2  2005/02/18 09:40:21  riralk
 * Support for clustered environments by caching generated images in memory
 *
 * ----------------------------------------------------------------------------
 */



package ifs.fnd.image;

import java.io.*;
import javax.media.jai.*;
import java.awt.image.*;
import java.util.*;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFDirectory;

import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.PlanarImage;
import java.awt.RenderingHints;
import javax.imageio.ImageIO;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.*;

import com.sun.media.jai.codec.FileSeekableStream;

public class TiffViewer{

    private com.sun.media.jai.codec.ImageEncoder encoder = null;
    private PNGEncodeParam  encodeParam = null;
    public static boolean DEBUG     = Util.isDebugEnabled("ifs.fnd.image.TiffViewer");

    //private Date dt = new Date();
    //private long timeStamp;         //value given to the splitted image's name

    private int noOfImages;         //number of images in the tiff file
    private String dest_path;       //absolute path to put the splitted images

    //for the page
    private String barColor;        //controlbar color
    private String img_path;        //images for controlbar
    private String first;           //alt for command bar images
    private String previous;        //alt for command bar images
    private String next;            //alt for command bar images
    private String last;            //alt for command bar images
    private String zoomIn;            //alt for command bar images
    private String zoomOut;            //alt for command bar images
    private String printImage;
    private String printMsg;    
    private int img_height = 200;   //height of the image in pixels
    private int img_width = 200;    //width of the image in pixels    
    private String tiff_img_path;   //virtual path of aplitted images for the client
    private String caption;		//Caption of the tiff viewer
    //private static int counter = 0; //keep track on number of tiff objects
    private int stamp;  		//to differentiate the viewers in the page
    private String fontSize;
    private String app_path;
    private String cookie_name;
    
    private String fileKey;
    private double zoom_factor = 0.3;

    // Create the image encoder.   
    private void encodeImage(RenderedImage img, OutputStream out) throws IOException
    {
       encoder = ImageCodec.createImageEncoder("PNG", out,encodeParam);
       encoder.encode(img);
       out.close();	     
    }
   
    /** Read a TIFF image file and split into separate PNG files*/
    public TiffViewer(String tiffFile, ASPManager mgr)
    {
       this(tiffFile, mgr, null);
    }
    
    /** 
    * Read a TIFF image file and split into separate PNG files
    * Set a cookie with the specified name to handle the resizing of images.
    */
    public TiffViewer(String tiffFile, ASPManager mgr, String cookieName)
    {
        long startTime = System.currentTimeMillis();
        try
        {
           FileSeekableStream stream = new FileSeekableStream(tiffFile);
           ParameterBlock params = new ParameterBlock();
           params.add(stream);
           
           TIFFDecodeParam decodeParam = new TIFFDecodeParam();
           decodeParam.setDecodePaletteAsShorts(true);
           RenderedOp image1 = JAI.create("tiff", params);
           int dataType = image1.getColorModel().getColorSpace().getType(); // .getSampleModel().getDataType();
           
           /*System.out.println("DataBuffer.TYPE_BYTE:"+ColorSpace.CS_GRAY);
           System.out.println("DataBuffer.type_double:"+DataBuffer.TYPE_DOUBLE);
           System.out.println("DataBuffer.type_float:"+DataBuffer.TYPE_FLOAT);
           System.out.println("DataBuffer.type_int:"+DataBuffer.TYPE_INT);
           System.out.println("DataBuffer.type_short:"+DataBuffer.TYPE_SHORT);
           System.out.println("DataBuffer.type_undefined:"+DataBuffer.TYPE_UNDEFINED);
           System.out.println("DataBuffer.type_ushort:"+DataBuffer.TYPE_USHORT);*/
           
           
           System.out.println("image:"+tiffFile+" type:"+dataType);


           
            TiffViewerCache.TiffSessionCache cache = TiffViewerCache.getInstance(mgr.getSessionId());

            fileKey = tiffFile; //.replaceAll("\\","_");

            ArrayList imagesPng = cache.get(fileKey);

            /*if (imagesPng == null)
            {
               imagesPng = readAndFormat(new FileInputStream(tiffFile));
               cache.put(fileKey, imagesPng);
            }*/

            noOfImages = imagesPng.size();

            dest_path = mgr.getPhyPath("images");   //physical path on server where images are generated                           

            boolean dyna_cache_enabled= "Y".equals(mgr.getASPConfig().getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N"));
            //relative path for javascript 
            if (dyna_cache_enabled) //support for clustering by caching image in memory
               tiff_img_path = mgr.getApplicationPath()+"/"+DynamicObjectCache.URL_INDICATOR+"/";   //relative path for javascript    
            else
               tiff_img_path = mgr.getApplicationPath()+"/images/";   

            app_path = mgr.getApplicationPath();

            barColor = mgr.getConfigParameter("SCHEME/COMMANDBAR/BGCOLOR");
            img_path = mgr.getASPConfig().getImagesLocation();
            first = mgr.translate("FNDIMAGETIFFVIEWERFIRST: First");
            previous = mgr.translate("FNDIMAGETIFFVIEWERPREVIOUS: Previous");
            next = mgr.translate("FNDIMAGETIFFVIEWERNEXT: Next");
            last = mgr.translate("FNDIMAGETIFFVIEWERLAST: Last");
            zoomIn = mgr.translate("FNDIMAGETIFFVIEWERZOOMIN: Zoom In");
            zoomOut = mgr.translate("FNDIMAGETIFFVIEWERZOOMOUT: Zoom Out");
            printImage = mgr.translate("FNDIMAGETIFFVIEWERPRINT: Print");              

            stamp = mgr.getTiffViewerCount(true);

            /*
            // Generate png for first page
            inFile = System.currentTimeMillis()+"";
            OutputStream out=null;              

            int index = 0;

            if (dyna_cache_enabled)
               out = new ByteArrayOutputStream();
            else  
            {
               File outFile = new File(dest_path,inFile + index + ".png");
               out = new FileOutputStream(outFile);                   
            }                 

            PlanarImage pi = Resize(cache.getPlanarImageAt(fileKey,index), img_width, img_height);
            ImageIO.write(pi, "png", out);

            if (dyna_cache_enabled)
            {
               String key = inFile + index +".png"; 
               DynamicObjectCache.put(key, ((ByteArrayOutputStream)out).toByteArray(),"image/png",mgr); 
            }
           */
            
/*            for(int i = 0; i<noOfImages;i++){
                if (dyna_cache_enabled)
                    out = new ByteArrayOutputStream();
                else  
                {
                    File outFile = new File(dest_path,inFile + i + ".png");                 
                    out = new FileOutputStream(outFile);                   
                }                 
               // encodeParam = new JPEGEncodeParam();
                encodeParam = new PNGEncodeParam.Palette(); 
                encodeParam.setBitDepth(8);
                encodeImage(dec.decodeAsRenderedImage(i), out);
                if (dyna_cache_enabled)
                {
                    String key = inFile + i +".png"; 
                    DynamicObjectCache.put(key, ((ByteArrayOutputStream)out).toByteArray(),"image/png",mgr); 
                }
            }*/

       } 
       catch (FndException e) 
       {
          mgr.showError(e.getMessage());
       }
       catch (java.io.IOException ioe) {
          mgr.showError(ioe.getMessage());
       }       
        
       if (mgr.isEmpty(cookieName)) 
          this.cookie_name = "DEFAULT_TIFF_WIDTH";
       else
          this.cookie_name= cookieName+"_TIFF_WIDTH";
                   
       long endTime = System.currentTimeMillis();
       long totalTime = endTime - startTime;
       if(DEBUG) Util.debug("Image conversion time= ("+totalTime+")ms\n");
      
   }
    
   /**
    * Takes an inputstream to a tiff (can be multipage) and converts to
    * Arraylist of PlanarImages formatting
    *
    * @param Input stream of the TIFF image
    * @return ArrayList of PlanarImages
    */
   static synchronized ArrayList readAndFormat(InputStream in)
   {
      ArrayList images = new ArrayList();
      
      SeekableStream s = SeekableStream.wrapInputStream(in, true);
      ParameterBlock pb = new ParameterBlock();
      pb.add(s);
      
      TIFFDecodeParam param = new TIFFDecodeParam();
      pb.add(param);
      
      long nextOffset = 0;
      int count = 1;
      
      do {
         long t1 = System.currentTimeMillis();
         PlanarImage pi = JAI.create("tiff", pb);
         TIFFDirectory dir = (TIFFDirectory)
         pi.getProperty("tiff_directory");
         
         //PlanarImage formattedPi = formatTiffImage(pi, null);//(pi,500,800, null);
         //images.add(formattedPi);
         images.add(pi);
         nextOffset = dir.getNextIFDOffset();
         
         if (nextOffset != 0) {
            param.setIFDOffset(nextOffset);
         }
         long t2 = System.currentTimeMillis();
         System.out.println("Page " + count + " is loaded in " + ((t2 - t1)) + " MilSec");
         count++;
         //break;
      } while (nextOffset != 0);
      
      return images;
   }

   private static synchronized RenderedOp formatTiffImage(PlanarImage source, RenderingHints hints) 
   {
      
      ParameterBlock scale_pb = new ParameterBlock();
      scale_pb.addSource(source);//.add(0.0F).add(0.0F).add(0.0F).add(0.0F);
      return JAI.create("SubsampleBinaryToGray", scale_pb, hints);
      //return JAI.create("scale", scale_pb, hints);
   }   
   
	/*private static PlanarImage Resize(PlanarImage i, int width, int height)
   {
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
		return JAI.create("SubsampleBinaryToGray", pb, null);
	}*/
   
   
   /** Set the height and width of the viewer in the HTML page */
   public void setScale(int height,int width)
   {
      this.img_height = height;
      this.img_width = width;
	}

   /** 
    * The zoom factor will increase/decrease the image width by this amount. 
    * The default value is 0.3
    */
   public void setZoomFactor(double zoom_factor)
   {
      this.zoom_factor = zoom_factor;
	}
   
	/** Set the title of the viewer in the html page */
	public void setCaption(String cap)
   {
      this.caption = cap;
	}

	

   //generate relevant script for the client browaer to view the images
   // a stamp is added to identify multiple images in a single page
   /** Generate and return the html&JavaScript codes to display the images in the html page */
   public String show()
   {
      AutoString out = new AutoString(); //return generateClientJS()+"\n"+getHTML();

      out.clear();
      out.append("<script language=\"JavaScript\">\n");
      out.append("<!--\n");
      out.append(" var TIFF_IMG_PATH = \""+tiff_img_path+"\"; \n");
      out.append(" var TIFF_VIEWER_COOKIE"+stamp+" = \"TIFFVIEW_"+cookie_name+stamp+"\"; \n");
      out.append(" var TIFF_SCALE_FACTOR"+stamp+" = "+img_width+"*"+zoom_factor+";\n"); //scale factor
      out.append(" var picCount"+stamp+" = "+ noOfImages +"; \n");
      out.append("-->\n");
      out.append("</script>\n");
      
      out.append("<table cellspacing=0 cellpadding=0 width="+img_width+">");
      out.append("<tr>");
      out.append("<td>&nbsp;&nbsp;</td>"); //two space emtpy column for left margin
      out.append("<td width=100%>");       //the real column containing all html                      

      out.append("<table id=\"tiff_viewer_bar" + stamp + "\" cellspacing=\"0\" cellpadding=\"0\" width = "+img_width+" class='cmdBarBrowse' height=22>");
      out.append("<A name= TIFFViewer"+stamp+" ></A> ");
      //command bar                                                    
      out.append("<tr>");
      out.append(" <td nowrap align=LEFT><SPAN class=\"cmdBarTitle\">&nbsp;"+caption+"</SPAN></td>"); //title for cmdbar
      out.append(" <td nowrap align=RIGHT>");
      if (noOfImages > 1)
      {
         out.append("<a href=\"javascript:moveTo("+stamp+",0)\"><img name=\"tiffBarFirst"+stamp+"\" src=\""+img_path+"first.gif\" onMouseOver=\"JavaScript:tiffBarFirst"+stamp+".src=\'"+img_path+"first_hov.gif\'\" onMouseOut=\"JavaScript:tiffBarFirst"+stamp+".src=\'"+img_path+"first.gif\'\" alt=\""+first+"\" border=\"0\"></a>");
         out.append("<a href=\"javascript:movePrevious("+stamp+")\"><img name=\"tiffBarPrevious"+stamp+"\" src=\""+img_path+"backward.gif\" onMouseOver=\"JavaScript:tiffBarPrevious"+stamp+".src=\'"+img_path+"backward_hov.gif\'\" onMouseOut=\"JavaScript:tiffBarPrevious"+stamp+".src=\'"+img_path+"backward.gif\'\" alt=\""+previous+"\" border=\"0\"></a>");
         out.append("<a valign=MIDDLE><SPAN ID=\"counter"+stamp+"\" class=cmdBarTitle>1 of "+noOfImages+"</SPAN></a>");
         out.append("<a href=\"javascript:moveNext("+stamp+")\"><img name=\"tiffBarNext"+stamp+"\" src=\""+img_path+"forward.gif\" onMouseOver=\"JavaScript:tiffBarNext"+stamp+".src=\'"+img_path+"forward_hov.gif\'\" onMouseOut=\"JavaScript:tiffBarNext"+stamp+".src=\'"+img_path+"forward.gif\'\" alt=\""+next+"\" border=\"0\"></a>");
         out.append("<a href=\"javascript:moveTo("+stamp+","+(noOfImages-1)+")\"><img name=\"tiffBarLast"+stamp+"\" src=\""+img_path+"last.gif\" onMouseOver=\"JavaScript:tiffBarLast"+stamp+".src=\'"+img_path+"last_hov.gif\'\" onMouseOut=\"JavaScript:tiffBarLast"+stamp+".src=\'"+img_path+"last.gif\'\" alt="+last+" border=\"0\"></a>");
      }
      out.append("<a href=\"javascript:zoomOut("+stamp+")\"><img name=\"tiffBarZoomOut"+stamp+"\" src=\""+img_path+"zoom_out_image.gif\" onMouseOver=\"JavaScript:tiffBarZoomOut"+stamp+".src=\'"+img_path+"zoom_out_image_hov.gif\'\" onMouseOut=\"JavaScript:tiffBarZoomOut"+stamp+".src=\'"+img_path+"zoom_out_image.gif\'\" alt="+zoomOut+" border=\"0\"></a>");
      out.append("<a href=\"javascript:zoomIn("+stamp+")\"><img name=\"tiffBarZoomIn"+stamp+"\" src=\""+img_path+"zoom_in_image.gif\" onMouseOver=\"JavaScript:tiffBarZoomIn"+stamp+".src=\'"+img_path+"zoom_in_image_hov.gif\'\" onMouseOut=\"JavaScript:tiffBarZoomIn"+stamp+".src=\'"+img_path+"zoom_in_image.gif\'\" alt="+zoomIn+" border=\"0\"></a>");
      out.append("<a href=\"javascript:openTiffPrintDlg("+stamp+")\"><img name=\"tiffBarPrint"+stamp+"\" src=\""+img_path+"print.gif\" onMouseOver=\"JavaScript:tiffBarPrint"+stamp+".src=\'"+img_path+"print_hov.gif\'\" onMouseOut=\"JavaScript:tiffBarPrint"+stamp+".src=\'"+img_path+"print.gif\'\" alt="+printImage+" border=\"0\"></a>");
      out.append(" </td>"); 
      out.append(" </tr>");  
      out.append(" </table>");  
      out.append(" <table>"); 
      out.append(" <tr valign=TOP align=LEFT height="+img_height+">");                      
      out.append(" <td>");
      out.append(" <img src=\""+img_path+"empty.gif\" id=\"myTiff"+stamp+"\" border=\"1\" >");
      out.append(" </td>");
      out.append(" </tr>");   
      out.append(" </table>"); 
      out.append("</td>"); //end of real HTML column                
      out.append("</tr>");
      out.append("</table>");

      out.append("\n\n<input type=hidden name=TIFF_FILE value=\""+fileKey+"\">");
      out.append("\n<input type=hidden name=TIFF_PAGENO value=\"0\">");
      out.append("\n<input type=hidden name=TIFF_HEIGHT value=\""+img_height+"\">");
      out.append("\n<input type=hidden name=TIFF_WIDTH value=\""+img_width+"\">");
      
      return out.toString();
   }
}
