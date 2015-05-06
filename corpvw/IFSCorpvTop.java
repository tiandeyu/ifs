package ifs.corpvw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.ap.*;
import ifs.fnd.*;

public class IFSCorpvTop extends ASPPageProvider {

   // ===============================================================
   // Static constants
   // ===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.corpvw.IFSCorpvTop");

   // ===============================================================
   // Instances created on page creation (immutable attributes)
   // ===============================================================
   protected ASPBlock treetypeblk;

   protected ASPCommandBar treetypebar;

   private ASPBuffer buf;

   // ===============================================================
   // Transient temporary variables (never cloned)
   // ===============================================================
   String userID;
   String userType;
   String treeType;
   String menuID;
   String parentID;
   String menuName;
   String linkedPage;
   String orderNum;
   String num1;
   String str;
   String overviewState;
   String refreshID;
   String desc;

   // ===============================================================
   // Construction
   // ===============================================================
   public IFSCorpvTop(ASPManager mgr, String page_path) {
      super(mgr, page_path);

   }

   public void run() {
      // getUserMenuitems();
   }

   // =============================================================================
   // Command Bar functions
   // =============================================================================

   public void preDefine() {
      this.disableFooter();
   }

/*   public String modifiedClientScript(ASPManager mgr, String sGenerateClientScript) {
      return mgr.replace(sGenerateClientScript, "document.location=", "window.parent.location=");
   }

   public void getUserMenuitems() {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

   }*/

   protected AutoString getContents() throws FndException {
      ASPManager mgr = getASPManager();

      ASPContext ctx = mgr.getASPContext();

      AutoString out = getOutputStream();
      out.clear();

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("IFSCORPVTOPTITLE: IFS Project Management"));

      out.append("<SCRIPT LANGUAGE=\"javascript\" type=\"text/JavaScript\" >\n");
      out.append("<!--\n");
      out.append(" function MM_swapImgRestore() {\n");
      out.append("      var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;\n");
      out.append(" }\n");

      out.append(" function MM_preloadImages() {\n");
      out.append("      var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();\n");
      out.append("      var i,j=d.MM_p.length,a=MM_preloadImages.arguments;\n");
      out.append("      for(i=0; i<a.length; i++)\n");
      out.append("      if (a[i].indexOf('#')!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];\n");
      out.append(" }}\n");
      out.append(" }\n");

      out.append(" function MM_findObj(n, d) {\n");
      out.append("        var p,i,x;  if(!d) d=document; if((p=n.indexOf('?'))>0&&parent.frames.length) {\n");
      out.append("      d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}\n");
      out.append("      if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];\n");
      out.append(" for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);\n");
      out.append(" if(!x && d.getElementById) x=d.getElementById(n); return x;\n");
      out.append(" }\n");

      out.append(" function MM_swapImage() {\n");
      out.append("    var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)\n");
      out.append("      if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}\n");
      out.append(" }\n");
      
      out.append("var displayBar=true;\n");
      out.append("var nav_cols;\n");
      out.append("function switchBar() {\n");
      out.append("   obj = document.getElementById(\"NAV_CTL\");\n");
      out.append("   if (displayBar){\n");
      out.append("      nav_cols = parent.frameSet2.cols;\n");
      out.append("      if (nav_cols == \"0,*\")\n");
      out.append("         nav_cols = \"5,*\";\n");
      out.append("      parent.frameSet2.cols=\"0,*\";\n");
      out.append("      displayBar=false;\n");
      out.append("      obj.src=\"/b2e/secured/common/images/Open_Navigator.gif\";\n");
      out.append("      obj.title=\"" + mgr.translate("IFSCORPVTOPOPENNAV: Open Navigator") + "\";\n");
      out.append("   }else{\n");
      out.append("      parent.frameSet2.cols = nav_cols;\n");
      out.append("      displayBar=true;\n");
      out.append("      obj.src=\"/b2e/secured/common/images/Close_Navigator.gif\";\n");
      out.append("      obj.title=\"" + mgr.translate("IFSCORPVTOPCLOSENAV: Close Navigator") + "\";\n");
      out.append("   }\n");
      out.append("}\n");

      out.append("//-->\n");
      out.append("</SCRIPT>\n");
      out.append("<style>\n");
      out.append("a:link{text-decoration:none;}\n");
      out.append("a:visited{text-decoration:none;}\n");
      out.append("a:active{text-decoration:none;}\n");
      out.append("</style>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append("onLoad=\"MM_preloadImages('/b2e/common/images/exitgray.gif ')\">\n");

      out.append("<table border=0 width=100% cellpadding=\"0\" cellspacing=\"0\" height=\"60\" background =\"/b2e/secured/common/images/back.jpg\">\n");

      out.append("<tr height=\"1\" ><td width=\"100%\" colspan=\"3\"></td></tr>");

      out.append("<tr valign=\"middle\"><td width=\"3%\">&nbsp;</td><td align=\"left\" width=\"50%\"><img src=/b2e/secured/common/images/logo.jpg ></img></td><td width=\"20%\">&nbsp;</td>");
      out.append("<td width=\"5%\" align=\"left\" valign=\"middle\">\n");

      out.append("<a href=\"javascript:switchBar()\">\n");
      out.append("<img id=\"NAV_CTL\" title=\"" + mgr.translate("IFSCORPVTOPNAVCONTROL: Navigator Control") + "\" src=/b2e/secured/common/images/Close_Navigator.gif name=\"navctl\"  border=0>\n");
      out.append("</a>\n");
      
      out.append("<a href=\"#\" onclick=javascript:window.top.location.href=\"/b2e/secured/Default.page\" >\n");
      out.append("<img title=\"" + mgr.translate("IFSCORPVTOPRETURNDEF: Return Main Page") + "\" src=/b2e/secured/common/images/homePop24x24.gif name=\"home\"  border=0></a>\n");

      out.append("</td></tr></table></td></tr></table>");

      out.append("</body>\n");
      out.append("</html>\n");

      return out;

   }

   public void adjust() {

   }

}