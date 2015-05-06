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
* File                          :
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Modified    : Automatically generated by IFS/Design
* ----------------------------------------------------------------------------
*/

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.conmaw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.genbaw.GenbawConstants;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ConProjConstructionManAllNav extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConProjConstructionManNav");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPQuery q;
   private boolean bEmptyTree;
   private String src_str;
   private String iframe_src;
   private String proj_no;
   private String comnd; 

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ConProjConstructionManAllNav (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else
         okFind();   
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("CONSPECIALPROJSORTNODATA: No data found.");
         headset.clear();
      }
   }



   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }



   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx =  mgr.getASPContext();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","CON_PROJ_CONSTRUCTION_MAN_API.New__",headblk);
      cmd.setParameter("PROJ_NO", ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT));
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("PROJ_NO").
              setMandatory().
              setDynamicLOV("GENERAL_PROJECT").
              setInsertable().        
              setLabel("PROJECTBUDGETPROJECTTYPEID: Project No").
              setSize(30);      
      headblk.addField("PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.Get_Proj_Desc(:PROJ_NO)").
              setReadOnly().   
              setLabel("PROJECTBUDGETPROJDESC: Proj Desc").
              setSize(30);  
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      
      headblk.setView("GENERAL_PROJECT");
      headblk.defineCommand("GENERAL_PROJECT_API","");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONPROJCONSTRUCTIONMANTBLHEAD: Con Proj Construction Man");
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);

   }



   public void  adjust()
   {
   // fill function body
      ASPManager mgr = getASPManager();
      if (( bEmptyTree )|| ( headset.countRows() == 0 ))
      {
//         proj_no = headset.getValue("PROJ_NO");
         src_str    = "ConSpecialProjSortTree.page";
         iframe_src = "ConSpecialProjSortBlank.page";
      }
      else        
      {    
         headbar.disableCommand(headbar.NEWROW);      
         proj_no = headset.getValue("PROJ_NO");       
         String proj_desc = headset.getValue("PROJ_DESC");   
         src_str    = "ConProjConstructionManAllTree.page?PROJ_NO=" +  mgr.URLEncode(proj_no)+ "&PROJ_DESC=" + mgr.URLEncode(proj_desc) ;
         iframe_src = "ConSpecialProjSortBlank.page";
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "CONPROJCONSTRUCTIONMANDESC: Con Proj Construction Man";
   }

   public String  modifiedClientScript(ASPManager mgr,String sGenerateClientScript)
   {
      sGenerateClientScript = mgr.replace(sGenerateClientScript,"document.location=","window.parent.location=");
      return sGenerateClientScript;


   }

   protected String getTitle()
   {
      return "CONPROJCONSTRUCTIONMANTITLE: Con Proj Construction Man";
   }


   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag(""));
      out.append("</head>\n");
      out.append("<body ");      
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRMB\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"ACTIVITYCOM\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PROGRESSCOM\" value=\"\">\n");
      out.append(mgr.startPresentation("CONPROJCONSTRUCTIONMANTITLE: Con Proj Construction Man"));
      out.append(headlay.show());  
      if (headlay.isSingleLayout() && headset.countRows() > 0)    
      { 
          out.append("<iframe name=\"contents\" target=\"ChildMain\" src=");
          out.append(src_str);
          out.append(" width=\"20%\" scrolling=\"auto\" height = \"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");  
          out.append("<iframe name =\"ChildMain\" src=");
          out.append(iframe_src);        
          out.append(" width=\"78%\" scrolling=\"auto\" height=\"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");                            
      }       
      out.append(modifiedClientScript(mgr,mgr.endPresentation()));
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");
      return out;

   }
}