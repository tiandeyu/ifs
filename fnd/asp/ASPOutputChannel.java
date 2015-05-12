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
 * File        : ASPOutputChannel.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Reine A   1999-Dec-06 - Created
 *    Mangala P 2001-Jun-19 - Check the OS when calling the "PDF" Output Channel.
 *    Suneth M  2001-Sep-12 - Changed localization tags according to standard.
 *    Mangala P 2002-Aug-16 - Bug ID: 31021. Error of exporting data with special char's
 *                            to excel 
 *    Udayani/  2002-Oct-09 - "PDF" Output channels implementation is purely java based.
 *    Thwaragan               webpdf.dll is now obsolete. 
 *    Rifki R   2002-Oct-09 - Fixed minor bugs in new implementation of "PDF" Output Channel.
 *    Rifki R   2002-Oct-11 - Fixed bug where the temperory PDF output file is not deleted.
 *    Sampath   2003-Jul-15 - provide support for double byte characters for "PDF" Output Channels.
 *    Suneth M  2004-Apr-26 - Bug 43633, Changed general_Report(). 
 *    Suneth M  2008-Mar-11 - Bug 72284, Changed pdfReportDetail() to avoid breaking words in the middle.
 *    Suneth M  2008-Apr-22 - Bug 72664, Changed pdfReportInit() & general_Report to send the channel as
 *                            a parameter for ASPRowSet().visibleString_().
 *    Suneth M  2009-May-13 - Bug 82658, Changed pdfReportInit() to set a default field size when field size
 *                            is not defined in relevant page.
 *    Amil C    2009-Sep-02 - Bug 84497, Changed generateReport() to export excel data as xml
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import java.math.*;
import java.text.*;

//import ifs.fnd.pdflib.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.os.OSInfo;

import java.util.*;
import java.io.*;

//import java.io.FileOutputStream;
//import java.io.IOException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Toolkit;
import java.awt.Color;


/**
 * This class is cerated to support output channels. Neither public\protected constructor nor
 * public\protected methods available.The existing constructor is friendly (the default). So 
 * no class which is outside the package "ifs.fnd.asp" can use this.
 */

public class ASPOutputChannel
{  
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPOutputChannel");

   private int pageNo = 0;
   private boolean bPortrait;
   private String[] colTitleArr;
   private int[] colWidth;
   private int[] x_pos;
   private boolean width_flag;
   private int totColNo;
   private int lowestY;
   //private int columnFont;
   //private int detailFont;
   //private int titleFont;

   private Document document; 
   private BaseFont columnFont;
   private BaseFont detailFont;
   private BaseFont titleFont;
   private PdfWriter writer;


   private int columnFontSize;
   private int detailFontSize;
   private int titleFontSize;
   private float logoWidth;
   private float logoHeight;
   private int reportWidth;
   private int reportHeight;
   private int rightMargin;
   private int leftMargin;
   private int topMargin;
   private int bottomMargin;
   
   private int pos;
   private int pos2;
   private int pos3;
   private int pos4;
   private String hit;
   private String hit2;
   private String font_file;
   private String default_field_size;

   
   ASPOutputChannel(ASPBlock blk, ASPTable tbl, String channel)throws Exception
   {
      ASPPage current_page = blk.getASPPage();
      ASPManager aspmgr  = tbl.getASPManager();
      String responsType = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/CONTENT_TYPE","application/vnd.ms-excel");
      font_file = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/FONT_FILE","Helvetica");
      default_field_size = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/DEFAULT_FIELD_SIZE","20");
      
      if (responsType.equals("application/pdf"))        
         pdfReportInit(blk, tbl, aspmgr, current_page);         
      else
         general_Report(aspmgr, blk, channel);
   }
   
   private void zero()
   {
      pos  = 0;
      pos2 = 0;
      pos3 = 0;
      pos4 = 0;
      
      hit  = "";
      hit2 = "";
   }
   
   private void pageBreak(ASPBlock blk, ASPTable tbl, String data)throws Exception
   {      
      pageNo++;      
      document.newPage();
      logoHeight = 0;
      pdfReportHeader(blk, tbl, data);
      pdfPageFooter();
   }
   
   private void pdfReportDetail(ASPBlock blk, ASPTable tbl,String data)throws Exception
   {
	   zero();
	   String colData = "";
	   int columnNo   = 0;
	   int rowNo      = 1;
	   
	   PdfContentByte cb = writer.getDirectContent(); 
           cb.beginText();
           cb.setFontAndSize(detailFont, detailFontSize);
           
	   pos4 = data.indexOf("\n", pos3);
	   colData = data.substring(pos4);
	   pos3 = 0;
	   pos4 = 0;
	   String temp = "";
	   String rest = "";
	   int y_pos = 0;
           int new_y_pos = 0;
           String trimmed_temp = "";
	   
	   pos4 = colData.indexOf("\n", pos3);
	   while (pos4 != -1)
	   {
                  
		   hit2 = colData.substring(pos3, pos4);
		   pos2 = hit2.indexOf("\t", pos);
		   int lowest = 0;
		   lowestY = 0;
		   while (pos2 != -1)
		   {
			   hit = hit2.substring(pos, pos2);
			   y_pos = colWidth[columnNo];
			   if(hit.length()>y_pos)
			   {
				   temp = hit.substring(0, y_pos);  

                                   new_y_pos = temp.lastIndexOf(" ");
                                   if (new_y_pos >0)
                                      new_y_pos = new_y_pos + 1;
                                   else
                                      new_y_pos = y_pos; 
                                   trimmed_temp = temp.substring(0, new_y_pos);

				   cb.setTextMatrix(x_pos[columnNo], reportHeight-2*topMargin-logoHeight-titleFontSize - titleFontSize - columnFontSize-5-detailFontSize*rowNo);
                                   cb.showText(trimmed_temp);
                                   rest = hit.substring(new_y_pos);

				   if(rest.length()>y_pos)
				   {
					   lowest = rowNo+1;
					   while(true)
					   {
						   temp = rest.substring(0, y_pos); 

                                                   new_y_pos = temp.lastIndexOf(" ");
                                                   if (new_y_pos >0)
                                                      new_y_pos = new_y_pos + 1;
                                                   else
                                                      new_y_pos = y_pos; 
                                                   trimmed_temp = temp.substring(0, new_y_pos);

						   cb.setTextMatrix(x_pos[columnNo], reportHeight-2*topMargin-logoHeight-titleFontSize - titleFontSize - columnFontSize-5-detailFontSize*lowest);
                                                   cb.showText(trimmed_temp);
                                                   rest = rest.substring(new_y_pos);
						   lowest++;
						   if(rest.length()<y_pos)
							   break;
					   }
					   
					   cb.setTextMatrix(x_pos[columnNo], reportHeight-2*topMargin-logoHeight-titleFontSize - titleFontSize - columnFontSize-5-detailFontSize*lowest);
                                           cb.showText(rest);
					   if(lowest > lowestY)
						   lowestY = lowest;
				   }
				   else
				   {
					   lowest = rowNo+1;					   
					   cb.setTextMatrix(x_pos[columnNo], reportHeight-2*topMargin-logoHeight-titleFontSize - titleFontSize - columnFontSize-5-detailFontSize*lowest);
					   cb.showText(rest);
					   if(lowest > lowestY)
					       lowestY = lowest;
				   }
			   }
			   else
                           {			   
                              cb.setTextMatrix(x_pos[columnNo], reportHeight-2*topMargin-logoHeight-titleFontSize - titleFontSize - columnFontSize-5-detailFontSize*rowNo);  
			      cb.showText(hit);
                           }
                           
			   pos = pos2+1;
			   pos2 = hit2.indexOf("\t", pos);
			   columnNo++;
			   if(columnNo>=totColNo)
				   break;
		   }
		   columnNo = 0;
		   pos = 0;
		   pos3 = pos4+1;
		   pos4 = colData.indexOf("\n", pos3);
		   if(lowestY != 0)
			   rowNo = lowestY;
		   rowNo++;
		   if (reportHeight-2*topMargin-logoHeight-2*titleFontSize-columnFontSize-5-detailFontSize*rowNo < bottomMargin+2*detailFontSize)
		   {
			   rowNo = 2;
                           cb.endText();
			   pageBreak(blk, tbl, data);
                           cb.beginText();
		   }              
	   }
           
	cb.endText();
   }
   
   private void pdfPageFooter()
   {
      DateFormat f1 = DateFormat.getInstance();
      Date now = new Date();

      PdfContentByte cb = writer.getDirectContent(); 
      cb.beginText();
      cb.setFontAndSize(detailFont, detailFontSize);
      cb.setTextMatrix(rightMargin, bottomMargin);
      cb.showText(f1.format(now)); 

      String s_pageno = "Page"+pageNo;
      
      cb.setTextMatrix(reportWidth - s_pageno.length()*6 - leftMargin, bottomMargin);
      cb.showText(s_pageno); 
      cb.endText();

      cb.setLineWidth(0f);
      cb.moveTo(rightMargin, bottomMargin+detailFontSize);
      cb.lineTo(reportWidth-leftMargin, bottomMargin+detailFontSize); 
      cb.stroke();


   }
   
   private void pdfReportHeader(ASPBlock blk, ASPTable tbl, String data)throws Exception
   {
      String tableTitle = tbl.getTitle();
      
            
      PdfContentByte cb = writer.getDirectContent(); 
      cb.beginText();
      cb.setFontAndSize(titleFont, titleFontSize);
      cb.setTextMatrix(rightMargin, reportHeight-2*topMargin-logoHeight-titleFontSize);
      cb.showText(tableTitle);  
      
      //Column Titles      

      cb.setFontAndSize(columnFont, columnFontSize);

      String colTitle = "";
      x_pos = new int[totColNo];
      int colTot = leftMargin;
      
      for (int colNo=0; colNo<totColNo; colNo++)	   
      {
         colTitle = colTitleArr[colNo];      

	 cb.setTextMatrix(colTot, reportHeight-2*topMargin-logoHeight-titleFontSize - titleFontSize - columnFontSize+2);
         cb.showText(colTitle);

         x_pos[colNo] = colTot;
         colTot = colTot+colWidth[colNo]*7;
      }
      cb.endText();

      cb.setLineWidth(0f);
      cb.moveTo(rightMargin, reportHeight-2*topMargin-logoHeight-titleFontSize-5);
      cb.lineTo(reportWidth - leftMargin, reportHeight-2*topMargin-logoHeight-titleFontSize-5);
      cb.moveTo(rightMargin, reportHeight-2*topMargin-logoHeight-titleFontSize - titleFontSize - columnFontSize-5);
      cb.lineTo(reportWidth - leftMargin, reportHeight-2*topMargin-logoHeight-titleFontSize - titleFontSize - columnFontSize-5); 
      cb.stroke();

   }
   
   private void pdfReportInit(ASPBlock blk, ASPTable tbl, ASPManager aspmgr, ASPPage current_page)throws Exception
   {
	   ASPField[] fields     = blk.getFields();
	   ASPRowSet visibleData = blk.getASPRowSet();
      	   
	   String separator = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CONTENT_TYPE","TAB");
	   String sep_sign = "\t";
	   if (separator.equalsIgnoreCase("TAB"))
		   sep_sign = "\t";
	   if (separator.equalsIgnoreCase("COMMA"))
		   sep_sign = ",";
	   if (separator.equalsIgnoreCase("SEMICOLON"))
		   sep_sign = ";";
	   
	   String data = visibleData.visibleString_(fields, sep_sign, "");
	   String s_colWidth = visibleData.visible_ColWidth_(fields);
	   
	   pageNo = 1;
	   bPortrait = true;	   
	   	   
	   reportWidth  = 595;
	   reportHeight = 842;
	   
	   rightMargin  = 42;
	   leftMargin   = 42;
	   topMargin    = 28;
	   bottomMargin = 28;
	    
      //columnFont=  BaseFont.createFont("Times-Bold", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
	   //detailFont=  BaseFont.createFont("Helvetica", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
	   //titleFont=  BaseFont.createFont("Times-Bold", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);	   
	   if(font_file.equals("Helvetica"))
      {
         titleFont =  BaseFont.createFont("Times-Bold", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);	   
         columnFont=  BaseFont.createFont("Times-Bold", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
         detailFont=  BaseFont.createFont(font_file, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
      }
      else
      {
         if (font_file.indexOf(".ttc")>0)
            font_file = font_file+",1";
         titleFont =  BaseFont.createFont(font_file, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
         columnFont=  BaseFont.createFont(font_file, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
         detailFont=  BaseFont.createFont(font_file, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
      }
      
      
      
	   columnFontSize = 10;
	   detailFontSize = 10;
	   titleFontSize  = 12;
	   
	   String logo_position	= aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/LOGO_POSITION","right");
	   char c_logo_pos = logo_position.charAt(0); //l==left c==center r==right
	   
	   //Column titles
	   zero();
	   String colTitle  = "";
	   String colTitles = "";
	   totColNo         = 0;
	   	   
	   pos4 = data.indexOf("\n", pos3);
	   colTitles = data.substring(pos3, pos4);
	   	
	   pos2 = colTitles.indexOf("\t", pos);
	   while (pos2 != -1)
	   {
		   colTitle = colTitles.substring(pos, pos2);
		   pos = pos2+1;
		   pos2 = colTitles.indexOf("\t", pos);
		   totColNo++;
	   }
	   
	   colTitleArr = new String[totColNo];
	   zero();
	   int columnNo	= 0;
	   
	   pos4 = data.indexOf("\n", pos3);
	   colTitles = data.substring(pos3, pos4);
	   
	   pos2 = colTitles.indexOf("\t", pos);
	   while (pos2 != -1)
	   {
		   colTitleArr[columnNo] = colTitles.substring(pos, pos2);
		   pos = pos2+1;
		   pos2 = colTitles.indexOf("\t", pos);
		   columnNo++;
	   }
	   
	   //Width - visible columns 	   
	   int convert;
	   columnNo  = 0;
	   int total = 0;
	   
	   zero();
	   int[] colWidth_temp = new int[totColNo];
	   
	   s_colWidth = s_colWidth.substring(1);
	   pos2 = s_colWidth.indexOf(",", pos);
	   while (pos2 != -1)
	   {
		   hit = s_colWidth.substring(pos, pos2);
                   if ("0".equals(hit)) hit = default_field_size;
		   convert = Integer.parseInt(hit);
		   colWidth_temp[columnNo] = convert;
		   total = total + convert;
		   pos = pos2+1;
		   pos2 = s_colWidth.indexOf(",", pos);
		   columnNo++;
	   }
	   
	   if (total*7>reportWidth-rightMargin-leftMargin)
	   {
		   bPortrait = false;
	   }
	   else
	   {
		   bPortrait = true;
	   }
	   
	   if (bPortrait)
	   {
		   reportWidth   = 595;
		   reportHeight  = 842; 
	   }
	   else
	   {
		   reportWidth   = 842;
		   reportHeight  = 595;
	   }
	   
	   if ((bPortrait == false) && ((total*7)>(reportWidth-rightMargin-leftMargin)))
	   {
		   columnNo = 0;
		   int sum = 0;
		   
		   while(sum <= (reportWidth-leftMargin-rightMargin))
		   {
			   sum = sum+colWidth_temp[columnNo]*7;
			   columnNo++;
		   }
		   totColNo = columnNo-1;
                   
                   if (totColNo==0)  //really long first column but anyway try to show it
                     totColNo=1;
                   
		   colWidth = new int[totColNo];
		   System.arraycopy(colWidth_temp, 0, colWidth, 0, totColNo);
	   }
	   else
	   {
		   colWidth = new int[totColNo];
		   System.arraycopy(colWidth_temp, 0, colWidth, 0, totColNo);
	   }   	   
	   
	   
	   String logotype = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/LOGOTYPE","logotype.gif");
	   ASPConfig cfg = current_page.getASPConfig();	   
      String phypath =  cfg.getApplicationContextPhyPath();
	   String image_location = cfg.getImagesLocation();

	   //If there is a virtual directory
	   zero();
	   int virtual = 0;
	   pos2 = image_location.indexOf("/", pos);
	   virtual++;
	   while (pos2 != -1)
	   {
		   pos = pos2+1;
		   hit = image_location.substring(pos);
		   pos2 = hit.indexOf("/", pos);
		   virtual++;
	   }
	   
       if(virtual>2)
	   {
		   pos4 = image_location.indexOf("/", pos3);
		   pos3 = pos4+1;
		   hit2 = image_location.substring(pos3);
		   pos4 = hit2.indexOf("/", pos3);
		   image_location = hit2.substring(pos4);
	   }
	   //End of the virtual directory check
	   
      if ('/' == image_location.charAt(0))
         image_location = image_location.substring(1);
      
	   logotype = phypath+image_location.replace('/',ifs.fnd.os.OSInfo.OS_SEPARATOR)+logotype;	   

	   //Creates an instance of the com.lowagie.text.Document-object with the required size :
	   Rectangle pageSize = new Rectangle(reportWidth, reportHeight); 
           document = new Document(pageSize);  
	   	  
	   String temp_file = aspmgr.getTempFileName();

	   try 
      {
            
         // we create a writer that listens to the document
         // and directs a PDF-stream to a file

         writer = PdfWriter.getInstance(document, new FileOutputStream(temp_file)); 

         // step 3: we add some metadata and open the document
         document.addCreator("IFS Foundation1");    
         document.addAuthor("IFS Foundation1");     
         document.addTitle("PDF Report");
         document.open(); 
      
         try
         {
            Image  logoPointer = Image.getInstance(Toolkit.getDefaultToolkit().createImage(logotype), null);

            logoWidth  =logoPointer.width();
            logoHeight =logoPointer.height();            

            switch(c_logo_pos)
            {
               case 'r':
               logoPointer.setAbsolutePosition(reportWidth - leftMargin - logoWidth, reportHeight - topMargin - logoHeight);
               break;
               case 'R':
               logoPointer.setAbsolutePosition(reportWidth - leftMargin - logoWidth, reportHeight - topMargin - logoHeight);
               break;
               case 'c':
               logoPointer.setAbsolutePosition(reportWidth/2 - logoWidth/2, reportHeight - topMargin - logoHeight);
               break;
               case 'C':
               logoPointer.setAbsolutePosition(reportWidth/2 - logoWidth/2, reportHeight - topMargin - logoHeight);
               break;
               case 'l':
               logoPointer.setAbsolutePosition(leftMargin, reportHeight - topMargin - logoHeight);
               break;
               case 'L':
               logoPointer.setAbsolutePosition(leftMargin, reportHeight - topMargin - logoHeight);
               break;
               default:
               logoPointer.setAbsolutePosition(reportWidth - leftMargin - logoWidth, reportHeight - topMargin - logoHeight);
            }  
            document.add(logoPointer);                    
         }
         catch (IOException ioe)
         {
         }
            
         pdfReportHeader(blk, tbl, data);
         pdfPageFooter();
         pdfReportDetail(blk, tbl, data);         

	    }
	    catch(DocumentException de) {
            System.err.println(de.getMessage());
	    }
	    catch(IOException ioe) {
            System.err.println(ioe.getMessage());
	    }   
	  
	   document.close();
	   
	   ASPInfoServices infoS = new ASPInfoServices(current_page);	   
      infoS.sendPdfFile(temp_file, true);          
           	   
   }
   
   private void general_Report(ASPManager aspmgr, ASPBlock blk, String channel)throws Exception
   {
      ASPField[] fields = blk.getFields();
      String separator = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/SEPARATOR","TAB");
      String sep_sign = "\t";
      
      if (separator.equalsIgnoreCase("TAB"))
          sep_sign = "\t";
      if (separator.equalsIgnoreCase("COMMA"))
          sep_sign = ",";
      if (separator.equalsIgnoreCase("SEMICOLON"))
          sep_sign = ";";
      
      String export_as_xml = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/EXPORT_AS_XML", "Y");
      String data;
      if( "Y".equals(export_as_xml) && (blk.getASPRowSet() instanceof BufferedDataSet) )
         data = ( (BufferedDataSet) blk.getASPRowSet() ).getSSXMLDataSet_(fields, sep_sign, channel);
      else
         data = blk.getASPRowSet().visibleString_(fields, sep_sign, channel);
      
      
      String charset = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/CHARSET","Unicode");
      String encoding = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/ENCODING","Unicode");
      
      String content_type = aspmgr.getConfigParameter("TABLE/OUTPUT_CHANNELS/CHANNEL"+channel+"/CONTENT_TYPE","application/vnd.ms-excel") + ";charset="+charset;
      
      //aspmgr.setAspResponsContentType(content_type);
      aspmgr.getAspResponse().setHeader("Content-Disposition", "filename="+aspmgr.getASPPage().getPageName()+".xls");
      //aspmgr.responseWrite(data.getBytes("Unicode"));       //write data string to the browser
      //aspmgr.endResponse();
      
      aspmgr.writeContentToBrowser(data.getBytes(encoding),content_type);
   }
}
