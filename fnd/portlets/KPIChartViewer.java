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
 * File        : KPIChartViewer.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :   
 * MAWELK      20-11-2001   Created.
 * MAWELK      25-03-2002   Change the name of the portlet,the name of the portlet file, item in portlet configuration.
 * MAWELK      26-08-2002   Made modification for Salsa project.
 * UMDOLK      05-11-2002   Modifications.(Changed the display of dimension headers and Legends)
 * UMDOLK      06-11-2002   Modifications.(Changed the display of legends)
 * SHWILK      21-11-2002   Call Id: 91553 - Changed tags
 * RAHELK      28-01-2003   Code review to add portlet to fnd component.
 * RAHELK      25-02-2003   Changed condition that shows msg
 *                          'Selected Slice Dimension do not have Measure values'.
 * MAWELK      23-01-2004   Bug Id 42172 - Display options for users(color, line, bar)
 * MAWELK      12-02-2004   Bug Id 42440 - Added Global functionality. 
 * Chandana D  13-05-2004   Updated for the use of new style sheets.
 * Mangala     26-05-2004   Merged bug id 44198
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2008/07/01 sumelk Bug 74086, Changed printGraphContents() to sort the values of x-axis by DIM_TIME_KEY1. 
 * 2006/08/10 buhilk Bug 59442, Corrected Translatins in Javascript
 *
 * 2006/06/30 buhilk Bug 58216, Fixed SQL Injection threats
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;
import java.awt.*;

public class KPIChartViewer extends ASPPortletProvider
{
   
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.KPIChartViewer");

   private ASPContext ctx;
   private transient ASPTransactionBuffer trans;
   
   private ASPBlock   headblk;
   
   private String portlet_name;
   private String portlet_product_;  
   private String selected_ial;
   private String actual_combo;
   private String actionIndex2;
   private String colourIndex2;
   
   private String actonCombo; 
   private String colourCombo; 
   private ASPBuffer prof_buffer;
   private ASPBuffer chart_view_buffer;
   private ASPBuffer colour_view_buffer;
   private Vector vCustomeColors = new Vector();
   
   
   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doReset() throws FndException
   {  
      if(DEBUG) debug(this+": KPIChartViewer.doReset()");
      super.doReset(); 
      trans            = null;
      portlet_product_ = "";
      prof_buffer      = null;
      chart_view_buffer = null;
      colour_view_buffer = null;
      selected_ial     = null;
      actionIndex2     = null;
      colourIndex2     = null;
      actonCombo       = null;
      colourCombo      = null;
   }

   public ASPPoolElement clone( Object mgr ) throws FndException
   { 
      KPIChartViewer  page = (KPIChartViewer)(super.clone(mgr));      

      //page.ctx       = page.getASPContext();
      page.headblk          = page.getASPBlock(headblk.getName());
      page.trans            = null;
      page.portlet_product_ = "";
      page.prof_buffer      = null;
      page.chart_view_buffer = null;
      page.colour_view_buffer = null;

      page.actionIndex2     = null;
      page.colourIndex2     = null;
      page.actonCombo       = null; 
      page.colourCombo      = null;
      
      return page;
   }


   public KPIChartViewer( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }

   
   protected void run()
   {    
      ASPManager mgr = getASPManager();

   }


   protected void preDefine() throws FndException
   {	  
      if(DEBUG) debug(this+": KPIChartViewer.preDefine()");
   
      headblk = newASPBlock("HEAD");
      
      appendJavaScript(
                       "function customizeIWAdminPortlet(obj,id)"+
                       "{"+
                       "   customizeBox(id);"+
                       "}\n"); 
      init();
   }


   protected void init()
   {
      ASPManager mgr = getASPManager();
      ctx            = getASPContext();

      portlet_name     = readProfileValue("PORTLET_NAME",translate(getDescription())); 
      portlet_product_ = readProfileValue("PORTLET_PRODUCT_"); 
      actual_combo     = readProfileValue("ACTUAL_COMBO", "");      
      selected_ial     = readProfileValue("SELECTED_IAL");
      prof_buffer      = readProfileBuffer("DATA_BUF");
      chart_view_buffer= readProfileBuffer("DATA_BUF1");
      colour_view_buffer = readProfileBuffer("DATA_BUF2");

      actionIndex2     =  readProfileValue("ACTIONINDEX2");
      colourIndex2     =  readProfileValue("COLOURINDEX2");

       ////////////////// BEGIN  to create Diamention buffer for Global variable 
       if (prof_buffer != null )
       {
       
           trans          = mgr.newASPTransactionBuffer();
           trans.clear();
           ASPQuery  ial_owner_qry1 =  trans.addQuery("TEMPTEMP", "fnd_setting","VALUE,VALUE", "PARAMETER = 'IAL_USER' " , "");  
           ial_owner_qry1.setBufferSize(5);		
           trans = mgr.perform(trans);     
           String ial_owner1_ = trans.getValue("TEMPTEMP/DATA/VALUE");  
    
           trans.clear();
           ASPQuery  DimQry =  trans.addQuery("DIMENQ", "SYS.ALL_TAB_COLUMNS","distinct substr(column_name,5,(instr(column_name,'_',-1,1)-5)) ,substr(column_name,5,(instr(column_name,'_',-1,1)-5)) ", "table_name =? AND column_name like 'DIM/_%' escape '/' AND column_name NOT like 'DIM_TIME/_%' escape '/' AND OWNER =?", "");
           DimQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
           DimQry.addParameter("OWNER", "S", "IN", ial_owner1_);
           DimQry.setBufferSize(10000);		
           trans = mgr.perform(trans); 
           ASPBuffer dim_buffer= trans.getBuffer("DIMENQ"); 
    
           double dimbufsize = dim_buffer.countItems();
    
           String lable_temp;
    
           ASPBuffer buf1 = mgr.newASPBuffer();
           for (int j=0; j<dimbufsize-1; j++)
           {   
              lable_temp ="";
    
              ASPBuffer tempBuff = dim_buffer.getBufferAt(j);
              lable_temp = tempBuff.getValueAt(0) ;
              
              String dim_value = prof_buffer.getBufferAt(j).getValueAt(0);
              String global_diamention = null;
              if (mgr.getPortalPage().getASPPortal().isGlobalVariableDefined(lable_temp))
              {
                  StringTokenizer st4 = new StringTokenizer(mgr.getPortalPage().getASPPortal().getGlobalVariable(lable_temp), ",");
                  global_diamention = st4.nextToken();
              }
    
              if (!(mgr.isEmpty(global_diamention) ))
              {  
                  ASPBuffer row = buf1.addBuffer(String.valueOf(j));        
                  row.addItem("INDEXOF",global_diamention);
              }
              else
              {
                  ASPBuffer row = buf1.addBuffer(String.valueOf(j));        
                  row.addItem("INDEXOF",dim_value);
    
              }
                  
           }
    
           prof_buffer = buf1; 
       }

       ////////////////// END  to create Diamention buffer for Global variable 

   }
   
   public String getTitle( int mode )
   { 
      return portlet_name;
   }

   public static String getDescription()
   {
	   return "FNDKPICHARTVIEWERDESCRIPTION: KPI Chart Viewer";
   }
   
   public void setCustomecolors(String colourIndex2)
   {
       ASPManager mgr   = getASPManager();
         
       if (colourIndex2.equals("BLUE")) //blue
          vCustomeColors.add(Color.blue);  
       else if (colourIndex2.equals("RED"))
          vCustomeColors.add(Color.red);//red
       else if (colourIndex2.equals("GREEN"))
          vCustomeColors.add(Color.green);//green  
       else if (colourIndex2.equals("MAGENTA"))
          vCustomeColors.add(Color.magenta);//magenta 
       else if (colourIndex2.equals("LIGHTGRAY"))
          vCustomeColors.add(Color.lightGray);//lightGray 
       else if (colourIndex2.equals("CYAN"))
          vCustomeColors.add(Color.cyan);//cyan 
       else if (colourIndex2.equals("BLACK"))
          vCustomeColors.add(Color.black);//black
       else if (colourIndex2.equals("LAVENDER"))
          vCustomeColors.add(new Color(183, 153 ,238));// lavander
       else if (colourIndex2.equals("ORANGE"))
          vCustomeColors.add(new Color(243,143,52));//orange
       else if (colourIndex2.equals("PALM"))
          vCustomeColors.add(new Color(130,38,81)); //palm

   }

   public void printContents()throws FndException    
   {
      if (prof_buffer != null )
         printGraphContents();
      else
      {
         if (!canCustomize())
         {
            printNewLine();
            printText("FNDKPICHARTVIEWERIWADMCREATEIAL: Create an IAL starting with KPIC_ to configure  ");
            printNewLine();
            printNewLine();
         }
         else
         {
            printNewLine();
            printText("FNDKPICHARTVIEWERIWADMIAL123: Choose a IAL Object on the  ");
            printScriptLink("FNDKPICHARTVIEWERCUSTOM15: Customization", "customizeIWAdminPortlet");                  
            printText("FNDKPICHARTVIEWERIWADMPAGE1:  page.");
            printNewLine();
            printNewLine();
	 }
      }
   }

   
   public void printGraphContents()
   {    
      ASPManager mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();    

      ASPGraph graph;
      graph = null;

      int graph_width;
      int graph_height;
      String graph_file;      
      String temp_prod ;
     
// --------------------------------------------------------------
     
      trans.clear();
      ASPQuery  ial_owner_qry =  trans.addQuery("TEMP1243", "fnd_setting","VALUE,VALUE", "PARAMETER = 'IAL_USER' " , "");  
      ial_owner_qry.setBufferSize(5);		
      trans = mgr.perform(trans);     
      String ial_owner_ = trans.getValue("TEMP1243/DATA/VALUE");  
     
     // to get the legents buffer(without the Measure Part)
      trans.clear();
      ASPQuery  LableMeasureQry =  trans.addQuery("MAAMAA1", "SYS.ALL_TAB_COLUMNS","SUBSTR(column_name,9,length(column_name)),SUBSTR(column_name,9,length(column_name))", "table_name =? AND column_name like 'MEASURE/_%' escape '/' AND OWNER =?", "");
      LableMeasureQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
      LableMeasureQry.addParameter("OWNER", "S", "IN", ial_owner_);
      LableMeasureQry.setBufferSize(10000);		

      ASPQuery  MeasureQrySel =  trans.addQuery("MAAM1", "SYS.ALL_TAB_COLUMNS","column_name,column_name", "table_name =? AND column_name like 'MEASURE/_%' escape '/' AND OWNER =?", "");
      MeasureQrySel.addParameter("TABLE_NAME", "S", "IN", selected_ial);
      MeasureQrySel.addParameter("OWNER", "S", "IN", ial_owner_);
      MeasureQrySel.setBufferSize(10000);		

      ASPQuery  WhereDimQry =  trans.addQuery("WHEREDIMENQ", "SYS.ALL_TAB_COLUMNS","distinct substr(column_name,5,(instr(column_name,'_',-1,1)-5)), substr(column_name,5,(instr(column_name,'_',-1,1)-5))", "table_name =? AND column_name like 'DIM/_%' escape '/'AND column_name NOT like 'DIM_TIME/_%' escape '/' AND OWNER =?" , "");  
      WhereDimQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
      WhereDimQry.addParameter("OWNER", "S", "IN", ial_owner_);
      WhereDimQry.setBufferSize(10000);		
      
      trans = mgr.perform(trans);     
      ASPBuffer LabMea_buffer= trans.getBuffer("MAAMAA1"); 

     //end legent buffer

      int Meabufsize ;
      Meabufsize =0;

      ASPBuffer MeaCol_buffer= trans.getBuffer("MAAM1"); 
      Meabufsize = MeaCol_buffer.countItems();

      ASPBuffer where_buffer= trans.getBuffer("WHEREDIMENQ");  
      int wherebufsize = where_buffer.countItems();  
      
      ////////////////////////////////////////////////////////////////////////////
     int line_count = 0;  
     int bar_count = 0;   

     String temp_view_property = null;
     for(int j =0; j<Meabufsize-1 ; j++)
     {
         if(chart_view_buffer != null)
          temp_view_property = chart_view_buffer.getBufferAt(j).getValueAt(0);

         
         if(temp_view_property.equals("LINE")) 
            line_count = line_count +1; 
         else if(temp_view_property.equals("BAR")) 
            bar_count = bar_count +1;
              
     }
     
     boolean  is_overlay = false;
     if (line_count == Meabufsize-1 && bar_count == 0)
     {
         graph = mgr.newASPGraph(ASPGraph.LINECHART); 
         graph.setLineMarkType(4);
         graph.setLineChartType(1);
         graph.setLineMarkSize(4); 

         is_overlay = false;
     }
     else if (line_count == 0 && bar_count == Meabufsize-1)
     {
        graph = mgr.newASPGraph(ASPGraph.COLUMNCHART);

        is_overlay = false;
     } 
     else
     {
         graph = mgr.newASPGraph(ASPGraph.COLUMNCHART);
         graph.setLineMarkType(4);
         graph.setLineChartType(1);
         graph.setLineMarkSize(4);

         is_overlay = true;
     }  


     graph.setLineThickness(2);
     graph.setWidth(getColumnWidth());//288 
     graph.setHeight((int)(getColumnWidth()*0.764));//220 
     graph.setPalette(4);
   
// -------------------This buffer is to creat the SELECT statment for the MeasureQryValues Buffer.  
      String temp_select ;

      temp_select ="";

      for (int ww=0; ww<Meabufsize-1; ww++)
      {        
         ASPBuffer tempMeaColBuff = MeaCol_buffer.getBufferAt(ww);
         if (ww == Meabufsize-2) 
            temp_select += tempMeaColBuff.getValueAt(0);
        else
            temp_select += MeaCol_buffer.getBufferAt(ww).getValueAt(0)+"," ;
      } 

//--------------- These arrays are created to pass the WHERE Conditions to the MeasureQryValues Buffer.

      String temp_Colwhere ;
      temp_Colwhere ="";
      
      String[] colArray = new String[100];
      int count_val =0;  
     
      for (int k=0; k<wherebufsize-1; k++)
      {
         ASPBuffer whereBuff = where_buffer.getBufferAt(k);
         temp_Colwhere = "DIM_" + whereBuff.getValueAt(0) + "_DESC1" ;
         colArray[k] = temp_Colwhere;
      }
      
      String[] valArray = new String[100];
      temp_prod ="";
     
      for(int j =0; j<wherebufsize-1 ; j++)
      {
         temp_prod = prof_buffer.getBufferAt(j).getValueAt(0);
         valArray[j] = temp_prod ;     

         if(mgr.isEmpty(temp_prod))      
            count_val =count_val +1; 
      }
      String temp_table ="";    
      String temp_where_go ="";
      
      temp_table = ial_owner_ + "." +selected_ial ;

      if(wherebufsize >0)
      {
         temp_where_go = colArray[0]+'='+ "'"+valArray[0]+ "'";
         for (int y=1;y<wherebufsize-1;y++)
         {
           temp_where_go=temp_where_go.concat(" AND " +colArray[y]+'='+ "'"+valArray[y]+ "'");
         }
      }

//----------------Arrays end. -----------------------------------------------------------
      ASPBuffer Mea_buffer;
    
      if(count_val <1)
      {
         trans.clear();
         ASPQuery  MeasureQryValues =  trans.addQuery("MANU1", temp_table,temp_select, temp_where_go, "");
         MeasureQryValues.setOrderByClause("DIM_TIME_KEY1");
         MeasureQryValues.setBufferSize(10000);

         //To get the Time Lable of the X axis
         ASPQuery  timeLableQry =  trans.addQuery("MANU234", temp_table,"DIM_TIME_DESC1", temp_where_go, "");
         timeLableQry.setOrderByClause("DIM_TIME_KEY1");
         timeLableQry.setBufferSize(10000);
  
         //To get the Information Updated value
         ASPQuery  UpdatedLableQry =  trans.addQuery("UPDATED23", temp_table,"DISTINCT UPDATED","" , "");
         UpdatedLableQry.setBufferSize(10000);
         
         trans = mgr.perform(trans); 

         Mea_buffer= trans.getBuffer("MANU1");
         ASPBuffer Time_Lable_buffer= trans.getBuffer("MANU234");
         ASPBuffer info_updated_buffer= trans.getBuffer("UPDATED23/DATA");
      
         String measure_temp ; 
         int Meabufsize1 = Mea_buffer.countItems();
         if (Meabufsize1 < 2)
         {
            printNewLine();
            printText("FNDKPICHARTVIEWERIWADM_INFOMES1:  Selected Slice Dimension do not have Measure values. "+"\n");
            printNewLine();
         }
         else
         {
            // Print the name lable of the graph
            String lable_col = "";
            String lable_val ="";
            String lable_head ="";
            for (int rr=0; rr<wherebufsize-1; rr++)
            {
               ASPBuffer whereBuff1 = where_buffer.getBufferAt(rr);
              
               lable_col  =  whereBuff1.getValueAt(0) ;             
               lable_val = prof_buffer.getBufferAt(rr).getValueAt(0);
               
               printNewLine();

               appendToHTML("<font style=\"FONT: bold 10pt Arial;color:black;\"> ");
               lable_col = (lable_col.substring(0,1)).toUpperCase() + (lable_col.substring(1)).toLowerCase();  
               printText(lable_col+" : ");
               appendToHTML("</font>");
                          
               appendToHTML("<font style=\"FONT: 10pt Times New Roman;color:black;\"> ");
               printSpaces(1);
               printText(lable_val );
               appendToHTML("</font>");

               printNewLine();
            } 
            graph.setNumPoints(Meabufsize1-1);
            graph.setNumSets(Meabufsize);
            int sets=0;
     
            for (int x=0; x<Meabufsize-1; x++)
            {
                temp_view_property = chart_view_buffer.getBufferAt(x).getValueAt(0);
                colourIndex2 = colour_view_buffer.getBufferAt(x).getValueAt(0);

                setCustomecolors(colourIndex2);
                if(is_overlay)
                   graph.setOverlayColors(vCustomeColors);
                graph.setColors(vCustomeColors);
                sets++;
                graph.setThisSet(sets);  
                for (int i=0; i<Meabufsize1-1; i++)
                {
                   measure_temp ="";
                     
                   ASPBuffer tempMeaBuff = Mea_buffer.getBufferAt(i);
                   measure_temp = tempMeaBuff.getValueAt(sets-1) ;
                   if(measure_temp != null)
                   {
                      Double temp5 = new Double(measure_temp); 
                      ////////////////////////////////////
                      ///////////////////////////////////
                      if(is_overlay)
                      {
                          if(temp_view_property.equals("LINE")) 
                             graph.setOverlayData(i+1,temp5.doubleValue());
                          else
                             graph.setData(i+1,temp5.doubleValue());
                      }
                      else
                         graph.setData(i+1,temp5.doubleValue());
                     
                      //1111111111111111 Time Lable
                      ASPBuffer time_buff = Time_Lable_buffer.getBufferAt(i);
                      String time_lab = time_buff.getValueAt(0) ;
                      graph.setLabel(i+1,time_lab);
                    }
                }
               
            }
            // To set the legents
            String leg_val ;  
            int indexOfSlash;
            indexOfSlash = 0;
            String beforeSlash;
            String  afterSlash;
            beforeSlash = "";
            afterSlash =  "";

            for (int m=0; m<Meabufsize-1; m++)
            { 
               leg_val ="";
               ASPBuffer LegBuf = LabMea_buffer.getBufferAt(m);             
               leg_val = LegBuf.getValueAt(0);
               leg_val = (leg_val.substring(0,1)).toUpperCase() + (leg_val.substring(1)).toLowerCase();  
               leg_val = Str.replace(leg_val,"__", "/"); 
               indexOfSlash = leg_val.indexOf("/");  
               if (indexOfSlash != -1)   //slash is included in the string.
               {
                   beforeSlash = leg_val.substring(0, indexOfSlash+1);
                   afterSlash  = leg_val.substring(indexOfSlash);
                   afterSlash  = afterSlash.substring(1,2).toUpperCase()+ afterSlash.substring(2); 
                   leg_val = beforeSlash + afterSlash;
               }
               leg_val = Str.replace(leg_val,"_", " ");  
               graph.setLegend(m+1,leg_val);
            }             
            graph_width  = graph.getWidth();
	         graph_height = graph.getHeight();
	         graph_file   = graph.getGraph();	
            
            printImage( graph_file, graph_width, graph_height, 0 );
            
            // To Print the information Updated

            printNewLine();
            printNewLine();
            printText("FNDKPICHARTVIEWERUPDATEDINFO: Information Updated");
            printSpaces(2);
            printText(info_updated_buffer.getValueAt(0));
            printNewLine();
               
         }
      }
      else
      {
         printNewLine();
         printText("FNDKPICHARTVIEWERIWADMINFO1:  All Slice Dimensions must have values to display the graph. "+"\n");
         printNewLine();
      }  
   }
   

    
  public void printCustomBody()throws FndException
  {
     ASPManager           mgr   = getASPManager();
     ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();      
     
     printNewLine();
     appendToHTML("<font style=\"FONT: bold 10pt Arial; color:darkblue;\"><B>\n");
     printText("FNDKPICHARTVIEWERIWADMINPORTLETMAME: Portlet Name"); 
     appendToHTML("</font></B>"); 
     printSpaces(9);
     
     printNewLine();
     printField("IWPORTLETNAME",portlet_name, 30);
     
// $$$$$$$$$$$$$$$$$$$$$$$$$--- KPI IAL ---$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  
     printNewLine();
     printNewLine();
     appendToHTML("<font style=\"FONT: bold 10pt Arial; color:darkblue;\"><B>\n");
     printText("FNDKPICHARTVIEWERIWADMINANA48: KPI IAL"); 
     appendToHTML("</font></B>"); 
     printSpaces(9);
   
     trans.clear();
     ASPQuery  IalQry = trans.addQuery("IALBUFFER1", "IAL_OBJECT", "NAME,NAME", "NAME like 'KPIC/_%' escape '/'", "");
     IalQry.setBufferSize(10000);		
     trans = mgr.perform(trans);	

     if (!mgr.isEmpty(readValue("SCORECARD111")))
        selected_ial = readValue("SCORECARD111");
     else if (mgr.isEmpty(selected_ial))
        selected_ial = trans.getBuffer("IALBUFFER1/DATA").getValueAt(0);

     printNewLine();
     printMandatorySelectBox("SCORECARD111",trans.getBuffer("IALBUFFER1"), selected_ial,"ValidateType");

// $$$$$$$$$$$$$$$$$$$$$$$$$--- Slice Diamention ---$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  
     
     printNewLine();
     printNewLine();
     appendToHTML("<font style=\"FONT: bold 10pt Arial; color:darkblue;\"><B>\n");	
     printText("FNDKPICHARTVIEWERSLICEDIM1: Slice Dimension");
     appendToHTML("</font></B>");	
     printSpaces(22);
     
     trans.clear();
     ASPQuery  ial_owner_qry1 =  trans.addQuery("TEMPTEMP", "fnd_setting","VALUE,VALUE", "PARAMETER = 'IAL_USER' " , "");  
     ial_owner_qry1.setBufferSize(5);		
     trans = mgr.perform(trans);     
     String ial_owner1_ = trans.getValue("TEMPTEMP/DATA/VALUE");  
     
     trans.clear();
     ASPQuery  DimQry =  trans.addQuery("DIMENQ", "SYS.ALL_TAB_COLUMNS","distinct substr(column_name,5,(instr(column_name,'_',-1,1)-5)) ,substr(column_name,5,(instr(column_name,'_',-1,1)-5)) ", "table_name =? AND column_name like 'DIM/_%' escape '/' AND column_name NOT like 'DIM_TIME/_%' escape '/' AND OWNER =?", "");
     DimQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
     DimQry.addParameter("OWNER", "S", "IN", ial_owner1_);
     DimQry.setBufferSize(10000);		
     trans = mgr.perform(trans);
     ASPBuffer dim_buffer= trans.getBuffer("DIMENQ");
     
     double dimbufsize = dim_buffer.countItems();
     ctx.writeNumber("DIMBUFSIZE",dimbufsize);

     beginTable("showDimensions",true,false);
     beginTableBody();

     String lable_temp;
     
     for (int j=0; j<dimbufsize-1; j++)
     {
        lable_temp ="";

        ASPBuffer tempBuff = dim_buffer.getBufferAt(j);
        lable_temp += tempBuff.getValueAt(0) ;
        
        beginTableCell();
            setFontStyle(BOLD);
            printText(lable_temp);
        endTableCell();
        
        beginTableCell();
        endTableCell();
        
        beginTableCell();
        if (!mgr.isEmpty(readValue("SCORECARD111")) || (prof_buffer == null))
           printField("DIPRODUCT"+j,portlet_product_, 30);
        else
           printField("DIPRODUCT"+j,prof_buffer.getBufferAt(j).getValueAt(0), 30);
        
        printHiddenField("DILVERSION","");
        printLOV("DIPRODUCT"+j,"common/scripts/DimensionColumnLov.page?IAL_VAL="+selected_ial + "&DIM_VAL="+lable_temp);
        endTableCell();

        nextTableRow();        

        ctx.writeValue("LABLE_TEMP",lable_temp);

    }   
     endTableBody();
     endTable();
     
//$$$$$$$$$$$$$$$$$$$$$$$$$$--- Measure List Box ---$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
     
     printNewLine();    
     appendToHTML("<table border='0' height='82' width='309'>\n");
     appendToHTML("<tr>\n");	
     appendToHTML("<td width='160' height='1' colspan='4'><div style=\"FONT: bold 10pt Arial; color:darkblue;\">\n");
     printText("FNDKPICHARTVIEWERMEASURETEMP:  Measures");
     appendToHTML("<td width='24' height='1'></td>\n");
     appendToHTML("</tr>\n");
     appendToHTML("<tr>\n");
     appendToHTML("<td width='160' height='1' colspan='4'>\n");

     trans.clear();
     ASPQuery  MeasureQry =  trans.addQuery("ALLIAL1Q1SYS", "SYS.ALL_TAB_COLUMNS","SUBSTR(column_name,9,length(column_name)),SUBSTR(column_name,9,length(column_name))", "table_name =? AND column_name like 'MEASURE/_%' escape '/' AND OWNER =?", "");
     MeasureQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
     MeasureQry.addParameter("OWNER", "S", "IN", ial_owner1_);
     MeasureQry.setBufferSize(10000);		
     trans = mgr.perform(trans);

     int indexOfSlash;
     indexOfSlash = 0;
     String beforeSlash;
     String  afterSlash;
     beforeSlash = "";
     afterSlash =  "";
     ASPBuffer temp_buffer= trans.getBuffer("ALLIAL1Q1SYS"); 
     double tempbufsize = temp_buffer.countItems();
     ctx.writeNumber("DIMMEABUFSIZE",tempbufsize);
    
             
     for (int j=0; j<tempbufsize-1; j++)
     {
        String measure_temp ="";
        ASPBuffer tempBuff = temp_buffer.getBufferAt(j);
        
        measure_temp += tempBuff.getValueAt(0) ;
        measure_temp = measure_temp.substring(0,1).toUpperCase()+ measure_temp.substring(1).toLowerCase(); 
        measure_temp = Str.replace(measure_temp,"__", "/"); 
        indexOfSlash = measure_temp.indexOf("/");  
        if (indexOfSlash != -1)   //slash is included in the string.
        {
            beforeSlash = measure_temp.substring(0, indexOfSlash+1);
            afterSlash  = measure_temp.substring(indexOfSlash);
            afterSlash  = afterSlash.substring(1,2).toUpperCase()+ afterSlash.substring(2); 
            measure_temp = beforeSlash + afterSlash;
        }
        measure_temp = Str.replace(measure_temp,"_", " ");  
        
        tempBuff.setValueAt(1,measure_temp);


     }
     printMandatorySelectBox("CORRESPONDINGMEASURES",temp_buffer,"","",6);
    
     appendToHTML("</td>\n");
     appendToHTML("</table>\n"); 


   /////////////////// To Show the View Property Section ////////////////////////////////////////////
     printNewLine();
     printNewLine();
     appendToHTML("	<div style=\"FONT: bold 10pt Arial; color:darkblue;\">\n"); 
     printText("FNDKPICHARTVIEWERVIEWPROPERTIES:  Chart View Properties"+"\n");
     appendToHTML("</B>");
     printNewLine();
     printNewLine();             
     appendToHTML("	</div>"); 

    actonCombo = populateViewProCombo(actionIndex2);
    colourCombo = populateColourProCombo(colourIndex2); 

     beginTable("showDimensions",true,false);
     beginTableBody();

     for (int j=0; j<tempbufsize-1; j++)
     {
        String measure_temp ="";
        ASPBuffer tempBuff = temp_buffer.getBufferAt(j);
        
        measure_temp += tempBuff.getValueAt(0) ;
        measure_temp = measure_temp.substring(0,1).toUpperCase()+ measure_temp.substring(1).toLowerCase(); 
        measure_temp = Str.replace(measure_temp,"__", "/"); 
        indexOfSlash = measure_temp.indexOf("/");  
        if (indexOfSlash != -1)   //slash is included in the string.
        {
            beforeSlash = measure_temp.substring(0, indexOfSlash+1);
            afterSlash  = measure_temp.substring(indexOfSlash);
            afterSlash  = afterSlash.substring(1,2).toUpperCase()+ afterSlash.substring(2); 
            measure_temp = beforeSlash + afterSlash;
        }
        measure_temp = Str.replace(measure_temp,"_", " ");  

        beginTableCell();
        setFontStyle(BOLD);
        printText(measure_temp);
        endTableCell(); 

        beginTableCell();
        endTableCell();

        beginTableCell();

        if (chart_view_buffer != null) 
        {
             String action_val = chart_view_buffer.getBufferAt(j).getValueAt(0);
             actonCombo = populateViewProCombo(action_val);
        }
            
        appendToHTML("  <input type=\"hidden\" name=\"SEARCH_ACTION\" value>\n");
        appendToHTML(" <td width=\"124\"><select class='selectbox' name="+addProviderPrefix()+"CMD_ACTION"+j+"  onChange=\"javascript:changed("+tempbufsize+",this)\"  size=\"1\">\n");
        appendToHTML(actonCombo);
        appendToHTML(" </select></td>\n");
        endTableCell();

        ////////////// BEGIN To show the colours of the chart

        beginTableCell();
        endTableCell();   

        beginTableCell();

        if (colour_view_buffer != null) 
        {
             String colour_val = colour_view_buffer.getBufferAt(j).getValueAt(0);
             colourCombo = populateColourProCombo(colour_val);
        } 
        appendToHTML(" <td width=\"124\"><select class='selectbox' name="+addProviderPrefix()+"COLOUR_CMD_ACTION"+j+"  size=\"1\">\n");
        appendToHTML(colourCombo);
        appendToHTML(" </select></td>\n");
        endTableCell(); 
        ////////////// END To show the colours of the chart  
      
        nextTableRow();    
     
     }
     endTableBody();
     endTable();
   ///////////////////////////////////////////////////////////////


     appendDirtyJavaScript("function ValidateType"+"(obj,val)\n"+
         "{\n"+
         "     firstobj_=eval('document.form."+addProviderPrefix()+"SCORECARD111');\n"+
         " customizeBox(f.__PORTLET_ID.value);"+                 
         "}\n");
                 
     appendDirtyJavaScript("function changed"+"(tempbufsize,obj)\n"+
      "{       \n"+ 
       "  count_bar = 0;\n"+
       "  var arrver = new Array();\n"+
       "  count_line = 0;\n"+ 
       "  for (i=0; i <tempbufsize-1; i++)\n"+
       "  {\n"+  
       "     act = eval('document.form."+addProviderPrefix()+"CMD_ACTION'+i);\n"+
       "     pro_val = act.options[act.selectedIndex].value;\n"+
       "     arrver[i] = act;\n"+ 
       "     if(pro_val =='BAR')\n"+
       "        count_bar = count_bar +1;\n"+
       "     else if(pro_val =='LINE')\n"+
       "        count_line = count_line +1;\n"+ 
       "   }\n"+ 

       "   if((count_line>1) && (count_bar>=1))\n"+ 
       "   {\n"+ 
       "	alert('"+mgr.translateJavaScript("FNDWEBKPICHARTVIEWERPROERROR: The combination more than one line with bar cannot be displayed in the chart. Select Same View Properties for all Measures.")+"');\n"+                             
       "        for (j=0; j <tempbufsize-1; j++)\n"+
       "        {\n"+  
       "           arrver[j].options[obj.selectedIndex].selected = true;\n"+
       "        }\n"+
       "   }\n"+ 
       "}\n"); 

  }
  
  public void submitCustomization()
  { 
      ASPManager mgr   = getASPManager();

      portlet_name    = readValue("IWPORTLETNAME");
      writeProfileValue("PORTLET_NAME", portlet_name);
    
      double dimbufsize = ctx.readNumber("DIMBUFSIZE");
      ctx.writeNumber("DIMBUFSIZE", dimbufsize);

      double measurebufsize = ctx.readNumber("DIMMEABUFSIZE");
      ctx.writeNumber("DIMMEABUFSIZE", measurebufsize);

      selected_ial = readValue("SCORECARD111");
      writeProfileValue("SELECTED_IAL", selected_ial);

      
  //*********************************************
     ASPBuffer buf1 = mgr.newASPBuffer();
     ASPBuffer buf2 = mgr.newASPBuffer();
     ASPBuffer buf3 = mgr.newASPBuffer();

     String temp_prod1;
     String chart_properties;
     String colour_properties;
     temp_prod1 ="";
     chart_properties ="";

     for (int j=0; j<dimbufsize-1; j++)
     {
        temp_prod1 = readValue("DIPRODUCT"+j);
        ASPBuffer row = buf1.addBuffer(String.valueOf(j));        
        row.addItem("INDEXOF",temp_prod1);
     }
     
     prof_buffer = buf1;
     writeProfileBuffer("DATA_BUF",prof_buffer);

     for (int i=0; i<measurebufsize-1; i++)
     { 
         // to create Chart Type Property buffer 
         chart_properties = readValue("CMD_ACTION"+i); 
         ASPBuffer row2 = buf2.addBuffer(String.valueOf(i));        
         row2.addItem("INDEXOF1",chart_properties);
         
         // to create Chart Colour buffer 
         colour_properties = readValue("COLOUR_CMD_ACTION"+i); 
         ASPBuffer row3 = buf3.addBuffer(String.valueOf(i));        
         row3.addItem("INDEXOF2",colour_properties);
     }

     chart_view_buffer = buf2;
     writeProfileBuffer("DATA_BUF1",chart_view_buffer);

     colour_view_buffer = buf3;
     writeProfileBuffer("DATA_BUF2",colour_view_buffer);
      
   //*********************************************************** 
           
  }   

   public boolean repeatBackgroundImage( int mode )
   {
      return false;
   }
   
   public boolean canCustomize()
   {
      ASPManager mgr = getASPManager();
      trans          = mgr.newASPTransactionBuffer();
      
      trans.clear();
      trans.addQuery("COUNTITEMS", "IAL_OBJECT", "COUNT(*)", "NAME like 'KPIC/_%' escape '/'", "");
      trans = mgr.perform(trans);
      return (trans.getBuffer("COUNTITEMS/DATA").getValueAt(0).equals("0")? false : true);	
   }

   public String getBoxStyle( int mode )
   {
      return "style='FONT: 8pt Verdana'";
   }     


   public String populateViewProCombo(String actionIndex2)//---populates Chart View Properties comboxes 
   {
      ASPManager mgr = getASPManager();
      if (mgr.isEmpty(actionIndex2))    //select "LINE" as the default combo value
      {
         actonCombo = "<OPTION  VALUE='LINE' SELECTED>" + mgr.translate("FNDKPICHARTLINEPRO: Line") +"</OPTION> <OPTION  VALUE='BAR'>" + mgr.translate("FNDKPICHARTBARPRO: Bar") +"</OPTION> ";
         actionIndex2 = "LINE";
      }
      else
      {
         if (actionIndex2.equals("LINE"))   //Select LINE option
         {
            actonCombo = "<OPTION  VALUE='LINE' SELECTED>" + mgr.translate("FNDKPICHARTLINEPRO: Line") +"</OPTION> <OPTION  VALUE='BAR'>" + mgr.translate("FNDKPICHARTBARPRO: Bar") +"</OPTION>";
         }
         else if (actionIndex2.equals("BAR"))   //Select BAR option
         {
            actonCombo = "<OPTION  VALUE='LINE'>" + mgr.translate("FNDKPICHARTLINEPRO: Line") +"</OPTION> <OPTION  VALUE='BAR' SELECTED>" + mgr.translate("FNDKPICHARTBARPRO: Bar") +"</OPTION> ";
         }
      }
      return actonCombo;
   }


   public String populateColourProCombo(String colourIndex2)//---populates Chart View Properties comboxes 
   {
      ASPManager mgr = getASPManager();
      
      if (mgr.isEmpty(colourIndex2))    //select "Blue" as the default combo value
      {
         colourCombo = "<OPTION  VALUE='BULE' SELECTED>" + mgr.translate("FNDKPICHARTBULE: Blue") +"</OPTION> <OPTION  VALUE='RED'>" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY' >" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION><OPTION  VALUE='LAVENDER' >" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE'>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         colourIndex2 = "BLUE";
         
      }
      else
      {
         if (colourIndex2.equals("BLUE"))   //Select Blue option
         {
           colourCombo = "<OPTION  VALUE='BLUE' SELECTED>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED'>" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY' >" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION> <OPTION  VALUE='LAVENDER' >" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE'>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         }
         else if (colourIndex2.equals("RED"))   //Select Red option
         {
           colourCombo = "<OPTION  VALUE='BLUE'>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED' SELECTED>" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY' >" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION> <OPTION  VALUE='LAVENDER' >" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE'>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         }
         else if (colourIndex2.equals("GREEN"))   //Select Green option
         {
           colourCombo = "<OPTION  VALUE='BLUE'>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED' >" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'SELECTED>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY' >" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION> <OPTION  VALUE='LAVENDER' >" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE'>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         }
         else if (colourIndex2.equals("MAGENTA"))   //Select Magenta option
         {
           colourCombo = "<OPTION  VALUE='BLUE'>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED' >" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' SELECTED>" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY' >" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION> <OPTION  VALUE='LAVENDER' >" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE'>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         }
         else if (colourIndex2.equals("LIGHTGRAY"))   //Select Light Gray option
         {
           colourCombo = "<OPTION  VALUE='BLUE'>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED' >" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY' SELECTED>" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION><OPTION  VALUE='LAVENDER' >" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE'>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         } 
         else if (colourIndex2.equals("CYAN"))   //Select Cyan option
         {
           colourCombo = "<OPTION  VALUE='BLUE'>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED' >" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY'>" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' SELECTED>" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION> <OPTION  VALUE='LAVENDER' >" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION> <OPTION  VALUE='ORANGE'>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         }
         else if (colourIndex2.equals("LAVENDER"))   //Select Lavander option
         {
           colourCombo = "<OPTION  VALUE='BLUE'>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED' >" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY'>" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION> <OPTION  VALUE='LAVENDER' SELECTED>" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE'>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         }
         else if (colourIndex2.equals("ORANGE"))   //Select Orange option
         {
           colourCombo = "<OPTION  VALUE='BLUE'>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED' >" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY'>" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION> <OPTION  VALUE='LAVENDER'>" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE' SELECTED>" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' >" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         }
         else if (colourIndex2.equals("PALM"))   //Select Plam option
         {
            colourCombo = "<OPTION  VALUE='BLUE'>" + mgr.translate("FNDKPICHARTBLUE: Blue") +"</OPTION> <OPTION  VALUE='RED' >" + mgr.translate("FNDKPICHARTRED: Red") +"</OPTION> <OPTION  VALUE='GREEN'>" + mgr.translate("FNDKPICHARTGREEN: Green") +"</OPTION> <OPTION  VALUE='MAGENTA' >" + mgr.translate("FNDKPICHARTMAGENTA: Magenta") +"</OPTION> <OPTION  VALUE='LIGHTGRAY'>" + mgr.translate("FNDKPICHARTLIGHTGRAY: Light Gray ") +"</OPTION> <OPTION  VALUE='CYAN' >" + mgr.translate("FNDKPICHARTCYAN: Cyan") +"</OPTION> <OPTION  VALUE='LAVENDER'>" + mgr.translate("FNDKPICHARTLAVENDER: Lavander") +"</OPTION><OPTION  VALUE='ORANGE' >" + mgr.translate("FNDKPICHARTORANGE: Orange") +"</OPTION><OPTION  VALUE='PALM' SELECTED>" + mgr.translate("FNDKPICHARTPALM: Plam") +"</OPTION>";
         }
         
      }
      return colourCombo;
   }

 

   
}