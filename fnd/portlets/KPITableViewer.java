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
 * File        : KPITableViewer.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :   
 * MAWELK      20-11-2001   Created.
 * MAWELK      25-03-2002   Change the name of the portlet,the name of the portlet file, item in portlet configuration.
 * MAWELK      11-09-2002   Made the table dynamic.
 * UMDOLK      05-11-2002   Modifications.(Changed the display of column headers. Added thousand seperators to values.)
 * UMDOLK      06-11-2002   Modifications.(added thousand seperator only for the string before decimal seperator)
 * UMDOLK      07-11-2002   Modifications.(added Row Diamension in configuration page)
 * SHWILK      21-11-2002   Call Id: 91553 - Changed tags
 * MAWELk      22-11-2002   Call Id: 91569 - When tranlating Information Updated was empty, it was fixed.
 * RAHELK      28-01-2003   Code review to add portlet to fnd component.
 * RAHELK      2003-03-19   Removed rowspan and colspan int values. 
 *                          Problems with netscape 4.7
 * MAWELK      12-02-2004   Bug Id 42440 - Added Global functionality. 
 * ----------------------------------------------------------------------------
 *
 * New Comments:
 * 2009/04/10 amiklk Bug 82003, Changed printTableContents() to support gird layout
 * 2008/07/01 sumelk Bug 74086, Changed printTableContents(), preDefine() & removed setThousandSeperator() &
 *                              added toDouble() to avoid the wraping of table values and to get the
 *                              format mask from the profile.
 * 2006/06/30 buhilk Bug 58216, Fixed SQL Injection threats
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;

public class KPITableViewer extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.KPITableViewer");

   private ASPContext ctx;
   private transient ASPTransactionBuffer trans;

   private ASPBlock   headblk;
   private String portlet_name;
   private String portlet_product;  
   private String selected_ial;
   private ASPBuffer prof_buffer;

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================


   protected void doReset() throws FndException
   {  
      if(DEBUG) debug(this+": KPITableViewer.doReset()");
      super.doReset(); 
      trans            = null;
      portlet_product  = "";
      prof_buffer      = null;
      selected_ial     = null;
   }


   public ASPPoolElement clone( Object mgr ) throws FndException
   { 
      if(DEBUG) debug(this+": KPITableViewer.clone()");
      KPITableViewer  page = (KPITableViewer)(super.clone(mgr));      

      page.headblk          = page.getASPBlock(headblk.getName());
      page.trans            = null;
      page.portlet_product  = "";
      page.prof_buffer      = null;

      return page;
   }


   public KPITableViewer( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }


   protected void run()
   {    
      ASPManager mgr = getASPManager();
   }


   protected void preDefine() throws FndException
   {    

      headblk = newASPBlock("HEAD");
      addField(headblk, "DUMMY_NUMBER_FIELD","Number").setHidden();

      appendJavaScript(
                      "function customizeIWAdminPortlet(obj,id)"+
                      "{"+
                      "   customizeBox(id);"+
                      "}\n");     

      init();
   }


   protected void init()
   {
      ASPManager mgr   = getASPManager();
      ctx              = getASPContext();
      portlet_name     = readProfileValue("PORTLET_NAME",translate(getDescription())); 
      portlet_product  = readProfileValue("PORTLET_PRODUCT");
      selected_ial     = readProfileValue("SELECTED_IAL");    
      prof_buffer      = readProfileBuffer("DATA_BUF");


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
          ASPQuery  DimQry =  trans.addQuery("DIMENQ", "SYS.ALL_TAB_COLUMNS","distinct substr(column_name,5,(instr(column_name,'_',-1,1)-5)) ,substr(column_name,5,(instr(column_name,'_',-1,1)-5)) ", "table_name =? AND column_name like 'DIM/_%_KEY1%' escape '/' AND column_name NOT like 'DIM_ROW/_%' escape '/'  AND column_name NOT like 'DIM_TIME/_%' escape '/' AND OWNER =?", "");
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
      return "FNDKPITABLEVIEWERDESCRIPTION: KPI Table Viewer";
   }

   public void printContents()throws FndException    
   {
      if (prof_buffer != null )
      {
         printTableContents();
      }
      else 
      {
         if (!canCustomize()) 
         {
            printNewLine();
            printText("FNDKPITABLEVIEWERIWADM_CREATEIAL: Create an IAL starting with KPIT_ and create a Product Dimention to configure  ");
            printNewLine();
            printNewLine(); 
         }
         else
         {
            printNewLine();
            printText("FNDKPITABLEVIEWERIWADM_IAL123: Choose a IAL Object on the  ");
            printScriptLink("FNDKPITABLEVIEWERCUSTOM15: Customization", "customizeIWAdminPortlet");                  
            printText("FNDKPITABLEVIEWERIWADM_PAGE1:  page.");
            printNewLine();
            printNewLine();
         }
      }

   }


   public void CheckTableContents() throws FndException
   {
      ASPManager mgr   = getASPManager();

      trans.clear();
      ASPQuery  ial_owner_qry2 =  trans.addQuery("TEMPIALTEMP", "fnd_setting","VALUE,VALUE", "PARAMETER = 'IAL_USER' " , "");  
      ial_owner_qry2.setBufferSize(5);    
      trans = mgr.perform(trans);     
      String ial_owner2 = trans.getValue("TEMPIALTEMP/DATA/VALUE");  

      trans.clear();
      ASPQuery  ExistIalQry = trans.addQuery("EXISTIAL", "SYS.ALL_TAB_COLUMNS", "distinct table_name, table_name", "table_name like 'KPIT/_%' escape '/' AND table_name NOT like '%/_TAB' escape '/' AND column_name like 'DIM_PRODUCT/_%' escape '/' AND table_name IN (select distinct table_name from SYS.ALL_TAB_COLUMNS where column_name like 'DIM_CUSTOMER/_%' escape '/') AND OWNER =?", "");
      ExistIalQry.addParameter("OWNER", "S", "IN", ial_owner2);
      ExistIalQry.setBufferSize(10000);      
      trans = mgr.perform(trans);   
      ASPBuffer ExistIalBuffer = trans.getBuffer("EXISTIAL");  


      int ExistIalsize = ExistIalBuffer.countItems();

      if (ExistIalsize>0)
      {
         printTableContents();
      }
      else
      {
         printNewLine();
         printText("FNDKPITABLEVIEWERIWADM_CHECKI: Selected IAL does not have Product and Customer Slice Dimensions to Diplay the Table  ");
         printNewLine();
         printNewLine();
      }
   }



   public void printTableContents() throws FndException
   {
      ASPManager mgr   = getASPManager();
      ASPTransactionBuffer trans  = mgr.newASPTransactionBuffer();    

      // --------------------------------------------------------------

      trans.clear();
      ASPQuery  ial_owner_qry =  trans.addQuery("TEMP1243", "fnd_setting","VALUE,VALUE", "PARAMETER = 'IAL_USER' " , "");  
      ial_owner_qry.setBufferSize(5);      
      trans = mgr.perform(trans);     
      String ial_owner_ = trans.getValue("TEMP1243/DATA/VALUE");  

      int Meabufsize ;
      Meabufsize = 0;

      trans.clear();
      
      ASPQuery  MeasureQrySel =  trans.addQuery("MAAM1", "SYS.ALL_TAB_COLUMNS","column_name,column_name", "table_name =? AND column_name like 'MEASURE/_%' escape '/' AND OWNER =?", "");
      MeasureQrySel.addParameter("TABLE_NAME", "S", "IN", selected_ial);
      MeasureQrySel.addParameter("OWNER", "S", "IN", ial_owner_);
      MeasureQrySel.setBufferSize(10000);     

      ASPQuery  WhereDimQry =  trans.addQuery("WHEREDIMENQ", "SYS.ALL_TAB_COLUMNS","distinct substr(column_name,5,(instr(column_name,'_',-1,1)-5)), substr(column_name,5,(instr(column_name,'_',-1,1)-5))", "table_name =? AND column_name like 'DIM_%_KEY1%' escape '/' AND column_name NOT like 'DIM_TIME/_%' escape '/' AND column_name NOT like 'DIM_ROW/_%' escape '/' AND OWNER =?" , "");  
      WhereDimQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
      WhereDimQry.addParameter("OWNER", "S", "IN", ial_owner_);
      WhereDimQry.setBufferSize(10000);    
      
      trans = mgr.perform(trans);     
      
      ASPBuffer MeaCol_buffer= trans.getBuffer("MAAM1"); 
      Meabufsize = MeaCol_buffer.countItems();

      ASPBuffer where_buffer= trans.getBuffer("WHEREDIMENQ");  
      int wherebufsize = where_buffer.countItems();   

      trans.clear();

      // -------------------This buffer is to creat the SELECT statment for the MeasureQryValues Buffer.  
      String temp_select ;

      temp_select ="";
      for (int ww=0; ww<Meabufsize-1; ww++) {
         ASPBuffer tempMeaColBuff = MeaCol_buffer.getBufferAt(ww);

         if (ww == Meabufsize-2)
         {
            temp_select += "SUM(" + tempMeaColBuff.getValueAt(0) +")";
         }
         else
         {
            temp_select += "SUM(" + MeaCol_buffer.getBufferAt(ww).getValueAt(0)+")," ;
         }

      } 

      //--------------- These arrays are created to pass the WHERE Conditions to the MeasureQryValues Buffer.
      String temp_prod ; 
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

      for (int j =0; j<wherebufsize-1 ; j++)
      {
         ASPBuffer whereBuff2 = prof_buffer.getBufferAt(j);
         temp_prod =  whereBuff2.getValueAt(0) ;
         valArray[j] = temp_prod ;     
         if (mgr.isEmpty(temp_prod))
         {
            count_val =count_val +1;
         }
      }
      String temp_table ="";    
      String temp_where_go ="";

      temp_table = ial_owner_ + "." +selected_ial ;

      if (wherebufsize >0)
      {
         temp_where_go = colArray[0]+'='+ "?"; //temp_where_go = colArray[0]+'='+ "'"+valArray[0]+ "'";
         for (int y=1;y<wherebufsize-1;y++)
         {
            temp_where_go=temp_where_go.concat(" AND " +colArray[y]+'='+ "?"); //temp_where_go=temp_where_go.concat(" AND " +colArray[y]+'='+ "'"+valArray[y]+ "'");
         }
      }


      //----------------Arrays end. -----------------------------------------------------------
      if (count_val <1)
      {
         trans.clear();

         ASPQuery  DimSelectQry =  trans.addQuery("MAAM1QQ", "SYS.ALL_TAB_COLUMNS","substr(column_name,9,instr(column_name,'_',-1,1)-9),substr(column_name,9,instr(column_name,'_',-1,1)-9)", "table_name =? AND column_name like 'DIM_ROW_%_DESC1%' escape '/' AND OWNER =?", "");
         DimSelectQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
         DimSelectQry.addParameter("OWNER", "S", "IN", ial_owner_);
         DimSelectQry.setBufferSize(10000);    
         trans = mgr.perform(trans);     
         ASPBuffer DimSel_buffer= trans.getBuffer("MAAM1QQ");                                                                                                            

         String dim_select ;
         String dim_order;
         String dim_Lable ;
         dim_select ="";
         dim_Lable ="";
         dim_order = "";   
         ASPBuffer tempDimBuff = DimSel_buffer.getBufferAt(0);
         dim_Lable += tempDimBuff.getValueAt(0);
         dim_select += "DIM_ROW_"+ tempDimBuff.getValueAt(0)+"_DESC1";
         dim_order += "DIM_ROW_"+ tempDimBuff.getValueAt(0)+"_KEY1";

         String  final_where_go = "";

         final_where_go = temp_where_go+" GROUP BY "+ dim_select + "," + dim_order;

         trans.clear();
         ASPQuery  MeasureQryValues =  trans.addQuery("MANU1", temp_table,temp_select, final_where_go, "");
         if (wherebufsize >0)
         {
            MeasureQryValues.addParameter(colArray[0].toUpperCase(), "S", "IN", valArray[0]);
            for (int y=1;y<wherebufsize-1;y++)
            {
               MeasureQryValues.addParameter(colArray[y].toUpperCase(), "S", "IN", valArray[y]);
            }
         }
         MeasureQryValues.setOrderByClause(dim_order);
         MeasureQryValues.setBufferSize(10000);
         trans = mgr.perform(trans); 
         ASPBuffer Mea_buffer= trans.getBuffer("MANU1");

         String  dim_select_go = "";
         dim_select_go = "DISTINCT " + dim_select;

         int dimSize ;
         dimSize =0;

         trans.clear();
         String temp_sql_query = dim_select_go+","+dim_order;
         ASPQuery  CustomerQry =  trans.addQuery("ROWCUST1", temp_table, temp_sql_query, temp_where_go, "");
         if (wherebufsize >0)
         {
            CustomerQry.addParameter(colArray[0].toUpperCase(), "S", "IN", valArray[0]);
            for (int y=1;y<wherebufsize-1;y++)
            {
               CustomerQry.addParameter(colArray[y].toUpperCase(), "S", "IN", valArray[y]);
            }
         }
         CustomerQry.setOrderByClause(dim_order);
         CustomerQry.setBufferSize(10000);

         trans = mgr.perform(trans); 
         ASPBuffer Customer_buffer = trans.getBuffer("ROWCUST1"); 

         dimSize = Customer_buffer.countItems();

         // *********************************************************************  
         //To get the Information Updated value
         trans.clear();
         ASPQuery  UpdatedLableQry =  trans.addQuery("UPDATED23", temp_table,"DISTINCT UPDATED","" , "");
         UpdatedLableQry.setBufferSize(10000);
         trans = mgr.perform(trans); 
         ASPBuffer info_updated_buffer= trans.getBuffer("UPDATED23/DATA");

         String measure_temp ; 
         int Meabufsize1 = Mea_buffer.countItems();
         if (Meabufsize1 == 1)
         {
            printNewLine();
            printText("FNDKPITABLEVIEWERIWADM_INFOMES1:  Selected Slice Dimension do not have Measure values. "+"\n");
            printNewLine();
         }
         else
         {
            // Print the name lable of the table
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

               printSpaces(5);
               printNewLine();

            }
      //************************************************************

            int CorrectMeaCount ;
            CorrectMeaCount =0;
            
            trans.clear();
            ASPQuery  Measure1QrySel =  trans.addQuery("MAAM1", "SYS.ALL_TAB_COLUMNS","SUBSTR(column_name,9,length(column_name)),SUBSTR(column_name,9,length(column_name))", "table_name =? AND column_name like 'MEASURE/_%' escape '/' AND OWNER =?", "");
            Measure1QrySel.addParameter("TABLE_NAME", "S", "IN", selected_ial);
            Measure1QrySel.addParameter("OWNER", "S", "IN", ial_owner_);
            Measure1QrySel.setBufferSize(10000);      
            trans = mgr.perform(trans);     
            ASPBuffer Mea1Col_buffer= trans.getBuffer("MAAM1"); 

            CorrectMeaCount = Mea1Col_buffer.countItems();

            String temp_select1 ;
            temp_select1 ="";

            printNewLine();
            beginTable();            
            appendToHTML("<TABLE class='multirowTable' cellspacing=0 cellpadding=2 id='KPITABLECont' width=100%>");
            beginTableTitleRow();
            dim_Lable = (dim_Lable.substring(0,1)).toUpperCase() + (dim_Lable.substring(1)).toLowerCase();  

            appendToHTML("<TH align='LEFT' class='tableColumnHeadingText multirowTableTH'>" + dim_Lable + "</TH>");

            int indexOfSlash;
            indexOfSlash = 0;
            String beforeSlash;
            String  afterSlash;
            beforeSlash = "";
            afterSlash =  "";

            for (int ww=0; ww<CorrectMeaCount-1; ww++) 
            {
               ASPBuffer tempMeaColBuff1 = Mea1Col_buffer.getBufferAt(ww);

               temp_select1 = tempMeaColBuff1.getValueAt(0); 
               if (temp_select1 == null)
               {
                  temp_select1 = "";
               }
               else
               {
                  temp_select1 = temp_select1.substring(0,1).toUpperCase()+ temp_select1.substring(1).toLowerCase(); 
                  temp_select1 = Str.replace(temp_select1,"__", "/"); 
                  indexOfSlash = temp_select1.indexOf("/");  
                  if (indexOfSlash != -1)
                  {   //slash is included in the string.
                     beforeSlash = temp_select1.substring(0, indexOfSlash+1);
                     afterSlash  = temp_select1.substring(indexOfSlash);
                     afterSlash  = afterSlash.substring(1,2).toUpperCase()+ afterSlash.substring(2); 
                     temp_select1 = beforeSlash + afterSlash;
                  }
                  temp_select1 = Str.replace(temp_select1,"_", " ");  
               }
               appendToHTML("<TH align='CENTER' class='tableColumnHeadingText multirowTableTH'>" + temp_select1 + "</TH>");
            }
            endTableTitleRow();

            String Cust_val;
            appendToHTML("<TBODY>");
            for (int i=0; i<dimSize-1; i++)
            {
               measure_temp ="";
               
               appendToHTML("<TR class=tableRowColor" + ( (i%2==1)? "2":"1" ) +" onclick=\"rowClicked("+ i +",'KPITABLECont',this);\">");

               ASPBuffer CustBuff1 = Customer_buffer.getBufferAt(i);
               Cust_val = CustBuff1.getValueAt(0) ;

               appendToHTML("<TD align='LEFT' class='multirowTableTD'>" + Cust_val + "</TD>");
               int indexOfDecimalSep;
               String sBeforeDecimal;
               String sAfterDecimal;

               ASPBuffer tempMeaBuff = Mea_buffer.getBufferAt(i);

               for (int zz=0; zz<CorrectMeaCount-1;zz++)
               {

                  measure_temp = tempMeaBuff.getValueAt(zz) ; 

                  if (measure_temp == null)
                  {
                     measure_temp = "";
                  }
                  else
                  {
                     measure_temp = getASPField("DUMMY_NUMBER_FIELD").formatNumber(toDouble(measure_temp));
                  }
                  appendToHTML("<TD align='RIGHT' class='multirowTableTD'>" + measure_temp + "</TD>");
               }
               appendToHTML("</TR>");
            }
            appendToHTML("</BODY>");
            //**************************************************************

            endTable(); 


            // To Print the information Updated
            printNewLine();
            printText("FNDKPITABLEVIEWERIWADM_CHECK2: Information Updated");
            printSpaces(2);
            printText(info_updated_buffer.getValueAt(0));
            printNewLine();
         }
      } 
      else
      {
         printNewLine();
         printText("FNDKPITABLEVIEWERIWADM_INFO1:  All Slice Dimensions must have values to display the table. "+"\n");
         printNewLine();
      }  



   }

   public void printCustomBody()throws FndException
   {
      ASPManager           mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();      

      printNewLine();
      appendToHTML("<font style=\"FONT: bold 10pt Arial; color:darkblue;\"><B>\n");
      printText("FNDKPITABLEVIEWERIWADMINPORTLETNAME: Portlet Name"); 
      appendToHTML("</font></B>"); 
      printSpaces(9);

      printNewLine();
      printField("IWPORTLETNAME",portlet_name, 30);

      // $$$$$$$$$$$$$$$$$$$$$$$$$--- KPI IAL ---$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  
      trans.clear();
      ASPQuery  ial_owner_qry1 =  trans.addQuery("TEMPTEMP", "fnd_setting","VALUE,VALUE", "PARAMETER = 'IAL_USER' " , "");  
      ial_owner_qry1.setBufferSize(5);     
      trans = mgr.perform(trans);     
      String ial_owner1_ = trans.getValue("TEMPTEMP/DATA/VALUE");

      printNewLine();
      printNewLine();
      appendToHTML("<font style=\"FONT: bold 10pt Arial; color:darkblue;\"><B>\n");
      printText("FNDKPITABLEVIEWERIWADMINANA48: KPI IAL"); 
      appendToHTML("</font></B>"); 
      printSpaces(9);

      printNewLine();

      trans.clear();

      ASPQuery  IalQry = trans.addQuery("IALBUFFER1", "IAL_OBJECT", "NAME,NAME", "NAME like 'KPIT/_%' escape '/'", "");
      IalQry.setBufferSize(10000);      
      trans = mgr.perform(trans); 

      if (!mgr.isEmpty(readValue("SCORECARD111")))
      {
         selected_ial = readValue("SCORECARD111");
      }
      else if (mgr.isEmpty(selected_ial))
      {
         selected_ial = trans.getBuffer("IALBUFFER1/DATA").getValueAt(0);
      }

      printMandatorySelectBox("SCORECARD111",trans.getBuffer("IALBUFFER1"), selected_ial,"ValidateType");

      // $$$$$$$$$$$$$$$$$$$$$$$$$--- Row Diamention ---$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  
      printNewLine();

      trans.clear();
      ASPQuery  DimSelectQry =  trans.addQuery("MAAM1QQ", "SYS.ALL_TAB_COLUMNS","substr(column_name,9,instr(column_name,'_',-1,1)-9),substr(column_name,9,instr(column_name,'_',-1,1)-9)", "table_name =? AND column_name like 'DIM_ROW_%_DESC1%' escape '/' AND OWNER =?", "");
      DimSelectQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
      DimSelectQry.addParameter("OWNER", "S", "IN", ial_owner1_);
      DimSelectQry.setBufferSize(10000);      
      trans = mgr.perform(trans);     
      ASPBuffer DimSel_buffer= trans.getBuffer("MAAM1QQ");                                                                                                            

      String dim_Lable ;
      dim_Lable ="";
      ASPBuffer tempDimBuff = DimSel_buffer.getBufferAt(0);
      dim_Lable += tempDimBuff.getValueAt(0);
      printNewLine();          
      appendToHTML("<font style=\"FONT: bold 10pt Arial; color:darkblue;\"><B>\n");  
      printText("FNDKPITABLEVIEWERROWDIM1: Row Dimension");
      appendToHTML("</font></B>");   
      printNewLine();
      appendToHTML("<font style=\"FONT: bold 10pt Arial; color:black;\"><B>\n");  
      printText(dim_Lable);
      appendToHTML("</font></B>");   
      printSpaces(22);   

      // $$$$$$$$$$$$$$$$$$$$$$$$$--- Slice Diamention ---$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  

      printNewLine();
      printNewLine();
      appendToHTML("<font style=\"FONT: bold 10pt Arial; color:darkblue;\"><B>\n");  
      printText("FNDKPITABLEVIEWERSLICEDIM1: Slice Dimension");
      appendToHTML("</font></B>");   
      printSpaces(22);   

      trans.clear();
      ASPQuery  DimQry =  trans.addQuery("DIMENQ", "SYS.ALL_TAB_COLUMNS","distinct substr(column_name,5,(instr(column_name,'_',-1,1)-5)) ,substr(column_name,5,(instr(column_name,'_',-1,1)-5)) ", "table_name =? AND column_name like 'DIM/_%_KEY1%' escape '/' AND column_name NOT like 'DIM_ROW/_%' escape '/'  AND column_name NOT like 'DIM_TIME/_%' escape '/' AND OWNER =?", "");
      DimQry.addParameter("TABLE_NAME", "S", "IN", selected_ial);
      DimQry.addParameter("OWNER", "S", "IN", ial_owner1_);
      DimQry.setBufferSize(10000);      
      trans = mgr.perform(trans); 

      ASPBuffer dim_buffer = trans.getBuffer("DIMENQ"); 
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
            printField("DIPRODUCT"+j,portlet_product, 30);
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
      printNewLine();    
      appendToHTML("<table border='0' height='82' width='309'>\n");
      appendToHTML("<tr>\n");  
      appendToHTML("<td width='160' height='1' colspan='4'><div style=\"FONT: bold 10pt Arial; color:darkblue;\">\n");
      printText("FNDKPITABLEVIEWERMEASURETEMP:  Measures");
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
      int tempbufsize = temp_buffer.countItems();

      for (int j=0; j<tempbufsize-1; j++)
      {
         String measure_temp ="";

         ASPBuffer tempBuff = temp_buffer.getBufferAt(j);

         measure_temp += tempBuff.getValueAt(0) ;
         measure_temp = measure_temp.substring(0,1).toUpperCase()+ measure_temp.substring(1).toLowerCase(); 
         measure_temp = Str.replace(measure_temp,"__", "/"); 
         indexOfSlash = measure_temp.indexOf("/");  
         if (indexOfSlash != -1) 
         {   //slash is included in the string.
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
      printNewLine();


      appendDirtyJavaScript("function ValidateType"+"(obj,val)\n"+
                            "{\n"+
                            "     firstobj_=eval('document.form."+addProviderPrefix()+"SCORECARD111');\n"+
                            " customizeBox(f.__PORTLET_ID.value);"+                 
                            "}\n");


   }

   public void submitCustomization()
   { 
      ASPManager mgr   = getASPManager();

      portlet_name    = readValue("IWPORTLETNAME");
      writeProfileValue("PORTLET_NAME", portlet_name);

      double dimbufsize = ctx.readNumber("DIMBUFSIZE");
      ctx.writeNumber("DIMBUFSIZE", dimbufsize);

      selected_ial = readValue("SCORECARD111");
      writeProfileValue("SELECTED_IAL", selected_ial); 
      
      ASPBuffer buf1 = mgr.newASPBuffer();   
      String temp_prod1;
      temp_prod1 ="";
      for (int j=0; j<dimbufsize-1; j++) 
      {
         temp_prod1 = readValue("DIPRODUCT"+j);
         ASPBuffer row = buf1.addBuffer(String.valueOf(j));        
         row.addItem("INDEXOF",temp_prod1);
      }

      prof_buffer = buf1;
      writeProfileBuffer("DATA_BUF",prof_buffer);
      
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
      trans.addQuery("COUNTITEMS", "IAL_OBJECT", "COUNT(*)", "NAME like 'KPIT/_%' escape '/'", "");
      trans = mgr.perform(trans);
      return(trans.getBuffer("COUNTITEMS/DATA").getValueAt(0).equals("0")? false : true);   
   }

   public String getBoxStyle( int mode )
   {
      return "style='FONT: 8pt Verdana'";
   }

   protected final double toDouble( String value )
   {
      try
      {
         return Double.valueOf(value).doubleValue();
      }
      catch ( Throwable e )
      {
         error(e);
      }
      return -1;
   }


}