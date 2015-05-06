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
*  File        : DynamicIIDLov.java
*  Modified    :
*    Suneth M  2002-12-13 - Created.
*    Suneth M  2003-03-20 - Fixed minor bugs.
*    Ramila H  2003-06-04 - disabled IIDOK button when coming from ReportOrder fields.
*    Suneth M  2003-09-03 - Changed getContents() to add a help message.
* ----------------------------------------------------------------------------
* New Comments:
* 2009-08-14   sumelk   Bug 84822, Changed populate() to avoid the system error when enumerate
*                       method is not returning a value.
* 2008-11-28   sumelk   Bug 78124, Changed run() to fix the sorting errors.
*
* Revision 1.2  2005/11/16 10:04:34  rahelk
* Call id 128794: fixed bug with selectAll, deseletAll and invertseletion for IIDLOVs
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.2  2005/02/11 09:12:11  mapelk
* Remove ClientUtil applet and it's usage from the framework
*
* Revision 1.1  2005/01/28 18:07:26  marese
* Initial checkin
*
* Revision 1.2  2004/12/10 10:24:52  riralk
* Disabled Properties button from command bar
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.Str;
import java.util.*;

public class DynamicIIDLov extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.DynamicIIDLov");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock blk;
   private ASPTable tbl;
   private ASPLov lov;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPBlockLayout lay;
   private ASPField fld;
   private ASPContext ctx;   

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String enumerate_method;
   private String field_label;
   
   //===============================================================
   // Construction
   //===============================================================
   public DynamicIIDLov(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ctx    = mgr.getASPContext();
      
      enumerate_method = ctx.readValue("ENUM_METHOD","");
      field_label      = ctx.readValue("FIELD_LABEL","");
      
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else 
      {
         if (!mgr.isEmpty(mgr.getQueryStringValue("IID_PACKAGE")))
            enumerate_method = mgr.getQueryStringValue("IID_PACKAGE");
      
         if (!mgr.isEmpty(mgr.getQueryStringValue("LABEL")))
            field_label = mgr.getQueryStringValue("LABEL");
         populate(); 
      }
      fld.setLabel(field_label);
      
      ctx.writeValue("ENUM_METHOD", enumerate_method);
      ctx.writeValue("FIELD_LABEL", field_label);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      mgr.setPageExpiring();
      disableHelp();
      disableHomeIcon();
      disableNavigate();
      disableFooter();
      disableHeader();
      disableValidation();

      String width            = mgr.getQueryStringValue("WIDTH");
      String height           = mgr.getQueryStringValue("HEIGHT");

      blk = mgr.newASPBlock("LOV");
      blk.addField("LIST").setFunction("NULL").setHidden();
      fld = blk.addField("IID_FIELD");
      
      tbl = mgr.newASPTable(blk);
      tbl.enableRowSelect();
      tbl.disableEditProperties();
      tbl.disableOutputChannels();
      tbl.disableRowCounter();
      tbl.disableQuickEdit();
      
      rowset = blk.getASPRowSet();
      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.disableMinimize();
      cmdbar.disableCommand(cmdbar.FIND);
      cmdbar.disableCommand(cmdbar.PROPERTIES);
      cmdbar.enableCommand(cmdbar.OKIID);
      
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);

      int specifiedWidth  = (int)Double.parseDouble(width);
      int specifiedHeight = (int)Double.parseDouble(height);

      ASPForm frm = getASPForm();
      frm.setFormWidth(specifiedWidth  - 45);
      frm.setFormHeight(specifiedHeight - 30);
   }

   private String[] listToArray( String list, String delimiters )
   {
      if( Str.isEmpty(list) ) return null;
      Vector v = new Vector();
      StringTokenizer st = new StringTokenizer(list,delimiters);
      while( st.hasMoreTokens() )
         v.addElement(st.nextToken());

      String[] arr = new String[v.size()];
      v.copyInto(arr);
      return arr;
   } 

   public void populate()
   {
      String[] iid_client_values = null;
      
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String sep = ASPHTMLFormatter.field_separator;

      ASPCommand cmd = trans.addCustomCommand("ENUM",enumerate_method);
      cmd.addParameter("LIST",null);
      trans = mgr.perform(trans);
      
      String list = trans.getValue("ENUM/DATA/LIST");

      if (!mgr.isEmpty(list))
      {
         iid_client_values = listToArray(list,sep);
      
         rowset.clear();
         for( int i=0; i<iid_client_values.length; i++ )
         {
            ASPBuffer buf = mgr.newASPBuffer();
            buf.setValue("IID_FIELD",iid_client_values[i]);
            rowset.addRow(buf);
         }
      }
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "List of Values";    
   }

   protected String getTitle()
   {
      return "List of Values";
   }

   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = this.getOutputStream();
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append( mgr.generateHeadTag("FNDCFFWINTIT: IFS/Applications - List of values") );
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append( mgr.startPresentation(mgr.translate("FNDCFFTITLE: List of")+"&nbsp;"+field_label), "\n" );
      
      if (("SINGLE".equals(mgr.getQueryStringValue("__TYPE"))) || (rowset.countRows() == 1))
      {
         cmdbar.disableCommand(cmdbar.OKIID);
         tbl.disableRowSelect();
      }
      out.append(cmdbar.showBar());
      if (rowset.countRows() != 1)
      {    
         out.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\"><tr><td>&nbsp;&nbsp;</td><td width=\"100%\">\n" );  
         out.append( "<SPAN class=normalTextValue>" );
         out.append( "<UL><Li>");
         out.append(mgr.translate("FNDLOVSELECTPICKONEVALUE: Use the arrow to pick up a single value. "));
         out.append( "</Li><Li>");
         out.append(mgr.translate("FNDLOVSELECTPICKMULTIVALUE: Select check boxes and press 'OK' to choose multiple values. "));
         out.append( "</Li></UL>");
         out.append( "</SPAN>" );
         out.append( "</td><td>&nbsp;&nbsp;</td></table>\n" );   
      }
      out.append(tbl.populateLov());
      out.append(mgr.endPresentation());
      out.append( "</form>\n</body>\n</html>\n" );
      return out;
   }
}
