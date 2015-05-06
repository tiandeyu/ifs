package ifs.docmaw;

import java.util.StringTokenizer;

import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPLog;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;

public class DocIssueObsoleteQuery extends DocIssueStructureQuery
{
   public DocIssueObsoleteQuery(ASPManager mgr, String pagePath) {
      super(mgr, pagePath);
   }
   
   public void runQuery()
   {
      ASPManager mgr = getASPManager();
      ASPLog log = mgr.getASPLog();
      searchURL = mgr.createSearchURL(headblk);

      trans.clear();
      q = trans.addQuery(headblk);
      
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());

      if (!Str.isEmpty(doc_class) && !Str.isEmpty(doc_no) && !Str.isEmpty(doc_sheet) && !Str.isEmpty(doc_rev))
      {
         q.addWhereCondition("DOC_CLASS = '" + doc_class + "'");
         q.addWhereCondition("DOC_NO = '" + doc_no + "'");
         q.addWhereCondition("DOC_SHEET = '" + doc_sheet + "'");
         q.addWhereCondition("DOC_REV = '" + doc_rev + "'");
      }
      
      // 1. Document scope
      q.addWhereCondition("DOC_CLASS IN " + DocmawConstants.getLibarayDocClasses() + " ");
      
      q.addWhereCondition("OBJSTATE = 'Obsolete'");
      
      // 2. Tree conditions
      if (!Str.isEmpty(nodeAddress))
         q.addWhereCondition(nodeAddress);

      // 3. Data isolation
      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
      StringBuffer sb = new StringBuffer("(");
      if (!"()".equals(tempPersonZones)) {
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      } else {
         sb.append("(1=1)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());

      // Order by
      q.setOrderByClause("DT_CRE DESC");
      q.includeMeta("ALL");

      mgr.querySubmit(trans, headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENODATA: No data found."));
         eval(headset.syncItemSets());
         return;
      }
      else
         okFindITEM2();

      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_KEY")))
      {
         String doc_key = mgr.getQueryStringValue("DOC_KEY");
         String[] keys = split(doc_key, '^');
         int k = 0;
         headset.first();
         while (!(headset.getValue("DOC_CLASS").equals(keys[0])
               && headset.getValue("DOC_NO").equals(keys[1])
               && headset.getValue("DOC_SHEET").equals(keys[2]) && headset.getValue("DOC_REV").equals(keys[3]))) {
            headset.next();
         }
      }
   }
   
   public void countFind()
   {
      ASPManager mgr = getASPManager();
      boolean bValueExists = true;
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      
      if (!Str.isEmpty(doc_class) && !Str.isEmpty(doc_no) && !Str.isEmpty(doc_sheet) && !Str.isEmpty(doc_rev))
      {
         q.addWhereCondition("DOC_CLASS = '" + doc_class + "'");
         q.addWhereCondition("DOC_NO = '" + doc_no + "'");
         q.addWhereCondition("DOC_SHEET = '" + doc_sheet + "'");
         q.addWhereCondition("DOC_REV = '" + doc_rev + "'");
      }
      
      // 1. Document scope
      q.addWhereCondition("DOC_CLASS IN " + DocmawConstants.getLibarayDocClasses() + " ");
      
      q.addWhereCondition("OBJSTATE = 'Obsolete'");
      
      // 2. Tree conditions
      if (!Str.isEmpty(nodeAddress))
         q.addWhereCondition(nodeAddress);

      // 3. Data isolation
      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
      StringBuffer sb = new StringBuffer("(");
      if (!"()".equals(tempPersonZones)) {
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      } else {
         sb.append("(1=1)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());
      
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }
   
   protected String getTitle()
   {
      return "DOCISSUEOBSOLETEQUERYTITLE: Doc Issue Obsolete Query";
   }
   
}
