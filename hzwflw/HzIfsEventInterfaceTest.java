package ifs.hzwflw;

import java.util.LinkedHashMap;

import com.horizon.workflow.flowengine.impl.XMLEventInterface;

public class HzIfsEventInterfaceTest  implements XMLEventInterface{

   public boolean doAction(LinkedHashMap arg0, LinkedHashMap arg1) {
      System.out.println(arg0);//{USERNAME=GUOJING, MSGSENDFLAG=Todo, USERID=GUOJING, SELECTPOSITION=, TRACKID=HZ2843eb3ff0c35b013ff0c7d4f10001, SELECTAUTHOR=Line1=U_IFS_ORG_YQ-CNPE-SGGL#IFS_POS_STAFF#IFS_USR_CUISHIZHU~, NEXTNODEID=Line1~Node1, SHOUXIEYIJIAN=详细意见, FLOWREADER=, DBIDENTIFIER=system, ACTIONFLAG=1111101110011000, ACTIONNAME=Submit, SELECTFLOWID=, CURNODEAUTHORSENDFLAG=Todo, SELECTGROUP=, WORKID=HZ2843eb3ff0c35b013ff0c7d4d80000, ALLOWUNITNEXT=, SELECTDEPT=, SELECTAUTHORCN=, DATAIDENTIFIER=system, SECONDAUTHOR=Line1=, SELECTFLAG=1, XWORKXML=, FLOWIDENTIFIER=system, CURNODEREADERSENDFLAG=Read, NODENAME=开始, FORMACTION=/b2e/XMLWorkFrameServlet.hz, URLAPP=/horizon/workflow/xmlwork.index.jsp?workid=|workid|&dbIdentifier=system, COMMENTS=, SXCOMMENTS=, ACTIONCLASS=, NODEID=Start, SUBMITFLAG=0, TMPSAVEINFO=, CANCELFLAG=2, DEPTNAME=HUIZHENG, TMPAUTHOR=, PREFLAG=0}
      System.out.println(arg1);//{}
      
      return true;
   }
}
