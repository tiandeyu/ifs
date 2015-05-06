package ifs.hzwflw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;
import ifs.genbaw.GenbawConstants;
import ifs.hzwflw.util.Base64Encoding;

public class UmIntermediate extends ASPPageProvider {

   private ASPCommandBar cmdbar;
   private ASPBlock blk;
   private ASPBlockLayout lay;   
   private ASPTransactionBuffer trans;
   private ASPBuffer keys;
   private ASPBuffer data;
   private ASPBuffer bc_Buffer;
   private ASPBuffer trans_Buffer;
   private ASPCommand cmd;
   
   public UmIntermediate(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }
   
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      blk = mgr.newASPBlock("MAIN");
      blk.addField("IN_1").
      setFunction("''").
      setReadOnly().setHidden();  
      blk.setTitle(mgr.translate("FNDSCRIPTSLOGONBLKTIT: Log On"));

      blk.addField("OUT_1").
      setFunction("''").
      setHidden();
      
      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.disableMinimize();

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.CUSTOM_LAYOUT);

      disableHeader();
      disableFooter();

      disableHelp();
      disableNavigate();
      disableOptions();
      disableHomeIcon();
      disableValidation();
   }
   
   
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      String username = mgr.readValue("__LS__");
      //init session
      trans = mgr.newASPTransactionBuffer();
      ASPContext ctx =  mgr.getASPContext();
      trans.clear();
      cmd = trans.addCustomFunction( "GETUSERPROJS", "PERSON_PROJECT_API.Get_User_Projs", "IN_1" );
      cmd.addParameter("IN_1",username.toUpperCase());
      cmd = trans.addCustomFunction( "GETUSERDEFPROJ", "PERSON_PROJECT_API.Get_User_Def_Proj", "IN_1" );
      cmd.addParameter("IN_1",username.toUpperCase());
      cmd = trans.addCustomFunction( "GETUSERZONES", "PERSON_ZONE_API.Get_User_Zones", "IN_1" );
      cmd.addParameter("IN_1",username.toUpperCase());
      cmd = trans.addCustomFunction( "GETUSERDEFZONE", "PERSON_ZONE_API.Get_User_Def_Zone", "IN_1" );
      cmd.addParameter("IN_1",username.toUpperCase());
      cmd = trans.addCustomFunction( "GETUSERNAME", "PERSON_INFO_API.Get_Name", "IN_1" );
      cmd.addParameter("IN_1",username.toUpperCase());
      
      cmd = trans.addCustomFunction( "GETACCURATEDEFDEPT", "GENERAL_ORG_POS_PERSON_API.Get_User_Def_Dept", "IN_1");
      cmd.addParameter("IN_1", username.toUpperCase());
      cmd.addParameter("IN_1", "TRUE");
      
      cmd = trans.addCustomFunction( "GETDEFDEPT", "GENERAL_ORG_POS_PERSON_API.Get_User_Def_Dept", "IN_1" );
      cmd.addParameter("IN_1", username.toUpperCase());
      cmd.addParameter("IN_1", "FALSE");
      
      cmd = trans.addCustomFunction( "GETPERSONID", "Person_Info_API.Get_Id_For_User", "IN_1" );
      cmd.addParameter("IN_1", username.toUpperCase());
      
      
      cmd = trans.addCustomFunction( "GETUSERDEFDEPT", "GENERAL_ORG_POS_PERSON_API.GET_USER_DEF_DEPT", "IN_1" );
      cmd.addParameter("IN_1",username.toUpperCase());
      cmd = trans.addCustomFunction( "GETUSERDEFDEPTNAME", "GENERAL_ORG_POS_PERSON_API.GET_PERSON_DEF_DEPT_DESC", "IN_1" );
      cmd.addParameter("IN_1",username.toUpperCase());
      
      trans.addRequestHeader(username.toUpperCase());
      trans = mgr.validate(trans);
      
      final class FilterNullUtil{
        String filterNull(String input){
            return input == null ? "" : input;
         }
      }
      
      FilterNullUtil tempFilter = new FilterNullUtil();
      String userProjs = tempFilter.filterNull(trans.getValue("GETUSERPROJS/DATA/IN_1"));
      String userDefaultProj = tempFilter.filterNull(trans.getValue("GETUSERDEFPROJ/DATA/IN_1"));
      String userZones = tempFilter.filterNull(trans.getValue("GETUSERZONES/DATA/IN_1"));
      String userDefaultZone = tempFilter.filterNull(trans.getValue("GETUSERDEFZONE/DATA/IN_1"));
      String userName = tempFilter.filterNull(trans.getValue("GETUSERNAME/DATA/IN_1"));
      String accurate_def_dept = tempFilter.filterNull(trans.getValue("GETACCURATEDEFDEPT/DATA/IN_1"));
      String def_dept = tempFilter.filterNull(trans.getValue("GETDEFDEPT/DATA/IN_1"));
      String person_id = tempFilter.filterNull(trans.getValue("GETPERSONID/DATA/IN_1"));
      
      String userDefaultDept = tempFilter.filterNull(trans.getValue("GETUSERDEFDEPT/DATA/IN_1"));
      String userDefaultDeptDesc = tempFilter.filterNull(trans.getValue("GETUSERDEFDEPTNAME/DATA/IN_1"));
       
      
      ctx.setGlobal("HZ_SESSION_USER_ID", username.toUpperCase());
      ctx.setGlobal("HZ_SESSION_USER_NAME", userName);
      ctx.setGlobal("HZ_SESSION_LOGIN_NAME", username.toUpperCase());
      ctx.setGlobal("HZ_SESSION_DEPT_ID", "root_node_id");
      ctx.setGlobal("HZ_SESSION_DEPT_NAME", "HUIZHENG");
      ctx.setGlobal("appcode", "system");
      ctx.setGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT, userDefaultProj);
      ctx.setGlobal(GenbawConstants.PERSON_DEFAULT_ZONE, userDefaultZone);
      ctx.setGlobal(GenbawConstants.PERSON_PROJECTS, userProjs);
      ctx.setGlobal(GenbawConstants.PERSON_ZONES, userZones);
      ctx.setGlobal(GenbawConstants.PERSON_DEFAULT_DEPT, def_dept);
      ctx.setGlobal(GenbawConstants.PERSON_ACCURATE_DEFAULT_DEPT, accurate_def_dept);
      ctx.setGlobal(GenbawConstants.PERSON_ID, person_id);
      ctx.setGlobal(GenbawConstants.PERSON_DEFALUT_DEPT, userDefaultDept);
      ctx.setGlobal(GenbawConstants.PERSON_DEFALUT_DEPT_DESC, userDefaultDeptDesc);
      //modify by yming 20150116 for um portal logon on
      ctx.setGlobal("UM_PORTAL", "YES");
      
      System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFAULT_PROJECT + ":" + userDefaultProj);
      System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFAULT_ZONE + ":" + userDefaultZone);
      System.out.println("DataIsolation*****" + GenbawConstants.PERSON_PROJECTS + ":" + userProjs);
      System.out.println("DataIsolation*****" + GenbawConstants.PERSON_ZONES + ":" + userZones);
      System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFALUT_DEPT + ":" + userDefaultDept);
      System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFALUT_DEPT_DESC + ":" + userDefaultDeptDesc);
      
      System.out.println("set hz user info sucess............" + username.toUpperCase());
      
      //
      
      String rt = mgr.readValue("__RT__");
      System.out.println("rt: " + rt);
      if(!Str.isEmpty(rt)){
         try {
            String realTargetUrl = Base64Encoding.decryptBASE64(rt);
            mgr.redirectTo(realTargetUrl);
            return;
         } catch (Exception e) {
            throw new FndException("error in decoding url:" + rt);
         }
      }
   }
   
   

}
