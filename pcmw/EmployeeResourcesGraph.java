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
*  File        : EmployeeResourcesGraph.java 
*  Created     : NUPELK  010410  Created Using the ASP file EmployeeResourcesGraph.asp
*  Modified    : 
*  NUPELK  010612  Changed the prepareGraph and  prepareDayGraph function's date 
*                  value comparisions.
*                  i.e added some conditions to detect 'null' vlaues.  
*  NUPELK  010822  Changed graph generation function according to the new
*                  java graphics classes
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041111  Replaced getContents with printContents.
*  SHAFLK  050929  Bug 52651,Changed prepareGraph() and prepareDayGraph().
*  NIJALK  051014  Merged bug 52651. 
*  NIJALK  060110  Changed DATE format to compatible with data formats in PG19.
*  DIAMLK  060821  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060905  Merged with the Bug Id 58216
*  AMDILK  070507  Call Id 143572: Modified run() to fetch the value of the field "SIGNATURE"
*  AMDILK  070509  call Id 143157: Displayed the absence time in the graph, modified run(), prepareDayGraph(), prepareGraph()
*  AMDILK  070611  Call Id 143157: Modified method prepareGarph()
*  ILSOLK  070713  Eliminated XSS.
*  AMDILK  070723  Call id 146893: Modified run(), prepareDayGraph(), prepareGraph()
* ----------------------------------------------------------------------------
*/
 
package ifs.pcmw; 
 
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.Vector; 
import java.awt.Color;
import java.awt.Font;

public class EmployeeResourcesGraph extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.EmployeeResourcesGraph");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String nWeekNumber;
	private ASPTransactionBuffer trans;
	private ASPTransactionBuffer trans1;
	private String sSign;
	private String sSignId;
	private String sys;
	private String nPast;
	private String nFuture;
	private ASPBuffer buff;
	private ASPBuffer row;
	private String option;
	private ASPTransactionBuffer secBuff;
	private String output;
	private String result;
	private String mon;
	private String tue;
	private String wed;
	private String thu;
	private String fri;
	private String sat;
	private String sun;
	private ASPCommand cmd;
	private String week_format;
	private String strtup;
	private String dtFirstDay;
	private String dtLastDay;
	private int items;
	private int i;
	private String nWoNo;
	private String nRowNo;
	private String dtStart;
	private String dtStop;
	private double nDays;
	private double nPlanHrs;
	private int nPlanMen;
	private String result1;
	private String result2;
	private double nRoleDay;
	private int m;
	private String con;
	private String sCompany;
	private String endup;
	private int nWeekCount;
	private String dtWeekDate;
	private String dept;
	private int graphflag;
	private int graphflagWo;
	private ASPGraph deptGraph;
	private int pnum;
	private int count4;
	private String deptGraph_File;
	private String deptGraph_Ext;
	private int deptGraph_Width;
	private int deptGraph_Height;
	private ASPQuery q;
	private String week_len;
	private String weekno; 
	private String dtTemp; 
	private double nRoleEnd;
	private double nHrs;
	private String lsData2;
	private String lsData;
	private String lsLabels;
	private String dtStartHR;
	private String dtStopHR;
	private String _lsData2;
	private String _lsData;
	private String _lsLabels;
	private double nToStartScope;
	private double nToEndScope;
	private int cond;
	private String data2;
	private String data1;
	private String sContract;

	//===============================================================
	// Construction 
	//===============================================================
	public EmployeeResourcesGraph(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		nWeekNumber = "";
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		trans1 = mgr.newASPTransactionBuffer();

		sSign = ctx.readValue("CTXSNG","");
		sys = ctx.readValue("CTXSYS","");
		nPast = ctx.readValue("CTXPST","");
		nFuture = ctx.readValue("CTXFUT","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
		{
			buff = mgr.getTransferedData();
			row = buff.getBufferAt(0); 
			sSign      = row.getValue("SIGNATURE");
			sSignId    = row.getValue("EMPLOYEE_ID");
			sys        = row.getValue("SYS_DATE");
			nPast      = row.getValue("PAST_WEEKS");
			nFuture    = row.getValue("FOLLOW_WEEKS");
			option     = row.getValue("OPTION");
		        sContract  = row.getValue("CONTRACT"); 

			if (!mgr.isEmpty(sSign))
			{

				if ("0".equals(option))
					prepareDayGraph();
				else
					prepareGraph();  
			}
		}

		ctx.writeValue("CTXSNG",sSign);
		ctx.writeValue("CTXSYS",sys);
		ctx.writeValue("CTXPST",nPast);
		ctx.writeValue("CTXFUT",nFuture);
	}

//-----------------------------------------------------------------------------
//------------------------  UTILITY FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

	public boolean  checksec( String method,int ref) 
	{
		ASPManager mgr = getASPManager();


		String splitted[] = split(method,"."); 

		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery(splitted[0],splitted[1]);

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists(method))
		{
			return true; 
		}
		else
			return false;    

	}


	public String  deductNumFromDate( String dt,String num) 
	{
		ASPManager mgr = getASPManager();
                String date_format; 

                if (dt.indexOf("-")>2)
                {
                    date_format = "YYYY-MM-DD";
                    dt = dt.substring(0,10);
                }
                else
                {
                    date_format = "DD-MON-YY";
                    dt = dt.substring(0,9);
                }

		trans.clear();
		q = trans.addQuery("DATEAFTER","select trunc(to_date(?,?)) - ? OUTPUT from DUAL");
                q.addParameter("DUMMY",dt);
                q.addParameter("DUMMY",date_format);
                q.addParameter("DUMMY",num);
		q.includeMeta("ALL");
		trans = mgr.perform(trans);
		output = trans.getValue("DATEAFTER/DATA/OUTPUT");
		trans.clear();

		return output;
	}


	public String  addNumToDate( String dt,String num) 
	{
		ASPManager mgr = getASPManager();
                String date_format; 

                if (dt.indexOf("-")>2)
                {
                    date_format = "YYYY-MM-DD";
                    dt = dt.substring(0,10);
                }
                else
                {
                    date_format = "DD-MON-YY";
                    dt = dt.substring(0,9);
                }

		trans.clear();
		q = trans.addQuery("DATEAFTER","select trunc(to_date(?,?)) + ? OUTPUT from DUAL");
                q.addParameter("DUMMY",dt);
                q.addParameter("DUMMY",date_format);
                q.addParameter("DUMMY",num);
		q.includeMeta("ALL");
		trans = mgr.perform(trans);
		output = trans.getValue("DATEAFTER/DATA/OUTPUT");
		trans.clear();

		return output;
	}


	public int  isGreater( String dt1,String dt2) 
	{
		ASPManager mgr = getASPManager();

		q = trans1.addQuery("ISGRT","select trunc(to_date(?,'YYYY-MM-DD'))-trunc(to_date(?,'YYYY-MM-DD')) RES FROM DUAL");
                q.addParameter("DUMMY",dt1.substring(0,10));
                q.addParameter("DUMMY",dt2.substring(0,10));
		q.includeMeta("ALL");
		trans1 = mgr.perform(trans1);
		result = trans1.getValue("ISGRT/DATA/RES");
		trans1.clear();   

		if (toDouble(result) < 0)
			return 2;  // if dt1 < dt2
		else if (toDouble(result) > 0)
			return 1;  // if dt1 > dt2
		else
			return 0;  // if dt1 = dt2     
	}  

	public String  weekString( String weekno) 
	{

		week_len = String.valueOf(weekno.length());
		if ("1".equals(week_len))
			weekno = "000"+weekno;
		else if ("2".equals(week_len))
			weekno = "00"+weekno;
		else if ("3".equals(week_len))
			weekno = "0"+weekno;

		return weekno ;
	}    

	public void  prepareDayGraph()
	{
		int w;
		String cond1;
		String cond2;
		int i;
		int k;
		int cmdOn = 0;
		int cmd1Done,cmd2Done,cmd3Done;
		cmd1Done = cmd2Done = cmd3Done = 0;
		ASPManager mgr = getASPManager();
                ASPBuffer buf = mgr.newASPBuffer();

		double[] nWeekSum = new double[10000];
		double[] nDaySum = new double[10000];

		// initializing variables..
		for (w=0;w<10000;w++)
		{
			nDaySum[w] = toDouble(0);
			nWeekSum[w] = toDouble(0);
		}
		nWeekNumber = "";


		if (mgr.isEmpty(sys))
			sys = mgr.readValue("SYSDATE");

                buf.addFieldItem("SYSDATE",sys);   

		dtTemp = addNumToDate(buf.getValue("SYSDATE"),String.valueOf(7));    

		trans.clear();
		cmd = trans.addCustomFunction( "WKF","Maintenance_Configuration_API.Get_Week_Format()","WEEK_FORMAT");
		trans = mgr.perform(trans);
		week_format = trans.getValue("WKF/DATA/WEEK_FORMAT");


		trans.clear();  
		q = trans.addQuery("STRT","SELECT to_char(to_number(to_char(to_date(?,'YYYY-MM-DD'),?))) STRTUP  from dual");
                q.addParameter("DUMMY",dtTemp.substring(0,10));
                q.addParameter("DUMMY",week_format);
		trans = mgr.perform(trans);
		strtup = trans.getValue("STRT/DATA/STRTUP");
		trans.clear();


		strtup = weekString(strtup);

		cmd = trans.addCustomFunction( "STDATE","Pm_Calendar_API.Get_Date","TEM_DATE");
		cmd.addParameter("TEM_NUM1",strtup);
		cmd.addParameter("TEM_NUM2",String.valueOf(1));
		trans = mgr.perform(trans);
		dtFirstDay = trans.getValue("STDATE/DATA/TEM_DATE");
		trans.clear();

		dtLastDay  = dtFirstDay;
		dtFirstDay = deductNumFromDate(dtFirstDay,String.valueOf(7));

		q = trans.addQuery("FORMAT","SELECT to_date(to_char(trunc(to_date(?,'YYYY-MM-DD')),'MM/DD/YYYY'),'MM/DD/YYYY') TEM_NUM1 FROM DUAL");
                q.addParameter("DUMMY",dtFirstDay.substring(0,10));

		cmd = trans.addCustomFunction( "NWEEK","Work_Order_Role_API.Get_Week","TEM_NUM2");
		cmd.addReference("TEM_NUM1","FORMAT/DATA");
		
                q = trans.addQuery("STRT","SELECT WO_NO, ROW_NO, trunc(date_from) DATE_FROM1,trunc(date_to) DATE_TO1, trunc(NVL(trunc(date_to),trunc(date_from)) - trunc(date_from) + 1) DIFFER, PLAN_HRS, PLAN_MEN  from WORK_ORDER_ROLE_OPENWO WHERE sign=? AND contract = ?");
                q.addParameter("DUMMY",sSign);
		q.addParameter("DUMMY", sContract);
		q.includeMeta("ALL");

		trans = mgr.perform(trans);

		nWeekNumber = trans.getValue("NWEEK/DATA/TEM_NUM2"); 

		items = trans.getBuffer("STRT").countItems();
		for (i=0;i<items-1;i++)
		{
			nWoNo = trans.getBuffer("STRT").getBufferAt(i).getValue("WO_NO");   
			nRowNo = trans.getBuffer("STRT").getBufferAt(i).getValue("ROW_NO");
			dtStart =  trans.getBuffer("STRT").getBufferAt(i).getValue("DATE_FROM1");
			dtStop = trans.getBuffer("STRT").getBufferAt(i).getValue("DATE_TO1");

			if (!mgr.isEmpty(trans.getBuffer("STRT").getBufferAt(i).getValue("DIFFER")))
				nDays =  toDouble(trans.getBuffer("STRT").getBufferAt(i).getValue("DIFFER"));
			else
				nDays	= 0; 

			if (!mgr.isEmpty(trans.getBuffer("STRT").getBufferAt(i).getValue("PLAN_HRS")))
				nPlanHrs = toDouble(trans.getBuffer("STRT").getBufferAt(i).getValue("PLAN_HRS"));
			else
				nPlanHrs	= 0;

			if (!mgr.isEmpty(trans.getBuffer("STRT").getBufferAt(i).getValue("PLAN_MEN")))
				nPlanMen = Integer.parseInt(trans.getBuffer("STRT").getBufferAt(i).getValue("PLAN_MEN"));
			else
				nPlanMen	= 0;


			if (mgr.isEmpty(dtStop))
				dtStop = dtStart;

			if (!mgr.isEmpty(dtStart) && !mgr.isEmpty(dtFirstDay))
			{

				q = trans1.addQuery("NROLE","select 1 + trunc(to_date(?,'YYYY-MM-DD'))-trunc(to_date(?,'YYYY-MM-DD')) NRL FROM DUAL");
                                q.addParameter("DUMMY",dtStart.substring(0,10));
                                q.addParameter("DUMMY",dtFirstDay.substring(0,10));
				q.includeMeta("ALL");
				cmdOn++;
				cmd1Done = 1;
			}


			if (!mgr.isEmpty(dtStart) && !mgr.isEmpty(dtLastDay))
			{

				q = trans1.addQuery("ISGRT1","select trunc(to_date(?,'YYYY-MM-DD'))-trunc(to_date(?,'DD-MON-YY')) RES FROM DUAL");
                                q.addParameter("DUMMY",dtStart.substring(0,10));
                                q.addParameter("DUMMY",dtLastDay.substring(0,9));
				q.includeMeta("ALL");
				cmdOn++;
				cmd2Done = 1;
			}

			if (!mgr.isEmpty(dtStop) && !mgr.isEmpty(dtFirstDay))
			{

				q = trans1.addQuery("ISGRT2","select trunc(to_date(?,'YYYY-MM-DD'))-trunc(to_date(?,'YYYY-MM-DD')) RES FROM DUAL");
                                q.addParameter("DUMMY",dtStop.substring(0,10));
                                q.addParameter("DUMMY",dtFirstDay.substring(0,10));
				q.includeMeta("ALL");
				cmdOn++;
				cmd3Done = 1;
			}

			if (cmdOn>0)
				trans1 = mgr.perform(trans1);


			if (cmdOn>0)
			{

				if (cmd1Done > 0)
					result1 = trans1.getValue("ISGRT1/DATA/RES");

				if (cmd2Done > 0)
					result2 = trans1.getValue("ISGRT2/DATA/RES");

				if (cmd3Done > 0)
				{

					if (!mgr.isEmpty(trans1.getValue("NROLE/DATA/NRL")))
						nRoleDay = toDouble(trans1.getValue("NROLE/DATA/NRL"));
					else
						nRoleDay	= 0 ;
				}
				else
					nRoleDay	= 0 ;

			}


			trans1.clear(); 


			if (!mgr.isEmpty(result1))
			{
				if (toDouble(result1) < 0)
					cond1 = "OK";
				else if (toDouble(result1) > 0)
					cond1= "NOTOK";
				else
					cond1	= "OK";    
			}
			else
				cond1	= "OK";


			if (!mgr.isEmpty(result2))
			{
				if (toDouble(result2) < 0)
					cond2 = "NOTOK";
				else if (toDouble(result2) > 0)
					cond2 = "OK";
				else
					cond2	= "OK";  
			}
			else
				cond2	= "OK";



			if (( nPlanHrs>0 ) &&  ( nDays > 0 ) &&  ( !("".equals(dtStart)) ) &&  ( "OK".equals(cond1) ) &&  ( "OK".equals(cond2) ))
			{

				nRoleEnd = toDouble(nRoleDay) + nDays -1;


				while (nRoleDay <= nRoleEnd)
				{

					if (nRoleDay > 0)
					{
						m = 0;
						nHrs = (nPlanHrs/nDays);
						if (nPlanMen>0)
							nHrs = toDouble(nHrs) * toDouble(nPlanMen);
						if (m < nDays)
							m = m + 1;
						nDaySum[(int)nRoleDay] = toDouble(nDaySum[(int)nRoleDay]) + toDouble(nHrs);

					}
					nRoleDay = toDouble(nRoleDay) + 1;
				}

			}


		}
		trans.clear();



		i=0;


		k=1;

		cmd = trans.addCustomCommand("DEFCON", "User_Default_API.Get_User_Contract");
		cmd.addParameter("TEM_NUM1");                         
		trans = mgr.perform(trans);                                                                    
		con = trans.getValue("DEFCON/DATA/TEM_NUM1");    
		trans.clear();

		cmd = trans.addCustomFunction("DEFCOM", "Site_API.Get_Company","TEM_NUM1");
		cmd.addParameter("TEM_NUM1", sContract);                         
		trans = mgr.perform(trans);                                                                    
		sCompany = trans.getValue("DEFCOM/DATA/TEM_NUM1"); 



		String[] lsData2 = new String[10000];
		String[] lsData = new String[10000];
		String[] lsLabels  = new String[10000];

		while (i < 7)
		{

			dtStartHR = addNumToDate(dtFirstDay,String.valueOf(i));
			dtStopHR  = dtStartHR;
			trans.clear();

			// checking for method availabilities before calling the function for Available Time

			if (checksec("ACTIVE_WORK_ORDER_UTIL_API.Get_Available_Hours",1))
			{
                                q = trans.addQuery("FORMAT1","SELECT to_date(to_char(trunc(to_date(?,'YYYY-MM-DD')),'MM/DD/YYYY'),'MM/DD/YYYY') TEM_NUM3 FROM DUAL");
                                q.addParameter("DUMMY",dtStartHR.substring(0,10));
                                q = trans.addQuery("FORMAT2","SELECT to_date(to_char(trunc(to_date(?,'YYYY-MM-DD')),'MM/DD/YYYY'),'MM/DD/YYYY') TEM_NUM4 FROM DUAL");
                                q.addParameter("DUMMY",dtStopHR.substring(0,10));

				cmd = trans.addCustomFunction( "STDATE","ACTIVE_WORK_ORDER_UTIL_API.Get_Available_Hours","TEM_DATE");
				cmd.addParameter("TEM_NUM1",sCompany);
				cmd.addParameter("TEM_NUM2",sSignId);
				cmd.addReference("TEM_NUM3","FORMAT1/DATA");
				cmd.addReference("TEM_NUM4","FORMAT2/DATA");

				trans = mgr.perform(trans);
				lsData2[i] = trans.getValue("STDATE/DATA/TEM_DATE");
				trans.clear();
			}



			lsData[i] = String.valueOf(nDaySum[i+1]);

			i = i+1;
		}


		mon = mgr.translate("PCMWEMPLOYEERESOURCESGRAPHMOND: MONDAY");
		tue = mgr.translate("PCMWEMPLOYEERESOURCESGRAPHTUES: TUESDAY"); 
		wed = mgr.translate("PCMWEMPLOYEERESOURCESGRAPHWEDN: WEDNESDAY"); 
		thu = mgr.translate("PCMWEMPLOYEERESOURCESGRAPHTHUR: THURSDAY"); 
		fri = mgr.translate("PCMWEMPLOYEERESOURCESGRAPHFRID: FRIDAY"); 
		sat = mgr.translate("PCMWEMPLOYEERESOURCESGRAPHSATU: SATURDAY"); 
		sun = mgr.translate("PCMWEMPLOYEERESOURCESGRAPHSUND: SUNDAY"); 

		lsLabels[0] = mon.substring(0,1);
		lsLabels[1] = tue.substring(0,1);
		lsLabels[2] = wed.substring(0,1);
		lsLabels[3] = thu.substring(0,1);
		lsLabels[4] = fri.substring(0,1);
		lsLabels[5] = sat.substring(0,1);
		lsLabels[6] = sun.substring(0,1);



		String[] _lsData2 = new String[10000];
		String[] _lsData = new String[10000];
		String[] _lsLabels  = new String[10000];



		i = 0;
		while (i < 7)
		{

			_lsData2[i] = lsData2[i];
			_lsLabels[i]  = lsLabels[i];
			_lsData[i] = lsData[i];

			i = i + 1;
		}

		createGraph(_lsData2,_lsLabels,_lsData);

	}


	public void  prepareGraph()
	{
		int w;
		String cond1;
		String cond2;
		int i;
		int k;
		int cnt;
		String numval;
		int cmdOn = 0;
		int cmd1Done,cmd2Done,cmd3Done;
		cmd1Done = cmd2Done = cmd3Done = 0;
		ASPManager mgr = getASPManager();
                ASPBuffer buf = mgr.newASPBuffer();

		double[] nWeekSum = new double[10000];
		double[] nDaySum = new double[10000];

		// initializing variables..
		for (w=0;w<10000;w++)
		{
			nDaySum[w] = toDouble(0);
			nWeekSum[w] = toDouble(0);
		}


		trans.clear();
		cmd = trans.addCustomFunction( "WKF","Maintenance_Configuration_API.Get_Week_Format()","WEEK_FORMAT");
		trans = mgr.perform(trans);
		week_format = trans.getValue("WKF/DATA/WEEK_FORMAT");

		if (mgr.isEmpty(sys))
			sys = mgr.readValue("SYSDATE");

                buf.addFieldItem("SYSDATE",sys);   

		nToStartScope = (toDouble(nPast) + 1)*7;
		nToEndScope = toDouble(nFuture)  * 7;

		dtTemp = deductNumFromDate(buf.getValue("SYSDATE"),String.valueOf(nToStartScope));
		dtTemp = addNumToDate(dtTemp,String.valueOf(7));

		trans.clear(); 
		q = trans.addQuery("STRT","SELECT to_char(to_number(to_char(to_date(?,'YYYY-MM-DD'),?))) STRTUP  from dual");
                q.addParameter("DUMMY",dtTemp.substring(0,10));
                q.addParameter("DUMMY",week_format);
		trans = mgr.perform(trans);
		strtup = trans.getValue("STRT/DATA/STRTUP");
		trans.clear();


		strtup = weekString(strtup);

		cmd = trans.addCustomFunction( "STDATE","Pm_Calendar_API.Get_Date","TEM_DATE");
		cmd.addParameter("TEM_NUM1",strtup);
		cmd.addParameter("TEM_NUM2",String.valueOf(1));
		trans = mgr.perform(trans);
		dtFirstDay = trans.getValue("STDATE/DATA/TEM_DATE");
		trans.clear();

		dtTemp = addNumToDate(buf.getValue("SYSDATE"),String.valueOf(nToEndScope));

		q = trans.addQuery("END","SELECT to_char(to_number(to_char(to_date(?,'YYYY-MM-DD'),?))+1) ENDUP  from dual");
                q.addParameter("DUMMY",dtTemp.substring(0,10));
                q.addParameter("DUMMY",week_format);
		trans = mgr.perform(trans);
		endup = trans.getValue("END/DATA/ENDUP");
		trans.clear();

		endup = weekString(endup);

		cmd = trans.addCustomFunction( "ENDATE","Pm_Calendar_API.Get_Date","TEM_DATE");
		cmd.addParameter("TEM_NUM1",endup);
		cmd.addParameter("TEM_NUM2",String.valueOf(7));
		trans = mgr.perform(trans);
		dtLastDay = trans.getValue("ENDATE/DATA/TEM_DATE");
		trans.clear();

		q = trans.addQuery("STRT","SELECT WO_NO, ROW_NO, trunc(date_from) DATE_FROM1,trunc(date_to) DATE_TO1, trunc(NVL(trunc(date_to),trunc(date_from)) - trunc(date_from) + 1) DIFFER, PLAN_HRS, PLAN_MEN  from WORK_ORDER_ROLE_OPENWO WHERE sign=?");
                q.addParameter("DUMMY",sSign);
		q.includeMeta("ALL");
		trans = mgr.perform(trans);

		items = trans.getBuffer("STRT").countItems();
		for (i=0;i<items-1;i++)
		{
			nWoNo = trans.getBuffer("STRT").getBufferAt(i).getValue("WO_NO");   
			nRowNo = trans.getBuffer("STRT").getBufferAt(i).getValue("ROW_NO");
			dtStart =  trans.getBuffer("STRT").getBufferAt(i).getValue("DATE_FROM1");
			dtStop = trans.getBuffer("STRT").getBufferAt(i).getValue("DATE_TO1");

			if (!mgr.isEmpty(trans.getBuffer("STRT").getBufferAt(i).getValue("DIFFER")))
				nDays =  toDouble(trans.getBuffer("STRT").getBufferAt(i).getValue("DIFFER"));
			else
				nDays	= 0; 

			if (!mgr.isEmpty(trans.getBuffer("STRT").getBufferAt(i).getValue("PLAN_HRS")))
				nPlanHrs = toDouble(trans.getBuffer("STRT").getBufferAt(i).getValue("PLAN_HRS"));
			else
				nPlanHrs	= 0;

			if (!mgr.isEmpty(trans.getBuffer("STRT").getBufferAt(i).getValue("PLAN_MEN")))
				nPlanMen = Integer.parseInt(trans.getBuffer("STRT").getBufferAt(i).getValue("PLAN_MEN"));
			else
				nPlanMen	= 0;


			if (mgr.isEmpty(dtStop))
				dtStop = dtStart;

			if (!mgr.isEmpty(dtStart) && !mgr.isEmpty(dtFirstDay))
			{

				q = trans1.addQuery("NROLE","select 1 + trunc(to_date(?,'YYYY-MM-DD'))-trunc(to_date(?,'DD-MON-YY')) NRL FROM DUAL");
                                q.addParameter("DUMMY",dtStart.substring(0,10));
                                q.addParameter("DUMMY",dtFirstDay.substring(0,9));
				q.includeMeta("ALL");
				cmdOn++;
				cmd1Done = 1;
			}


			if (!mgr.isEmpty(dtStart) && !mgr.isEmpty(dtLastDay))
			{

				q = trans1.addQuery("ISGRT1","select trunc(to_date(?,'YYYY-MM-DD'))-trunc(to_date(?,'DD-MON-YY')) RES FROM DUAL");
                                q.addParameter("DUMMY",dtStart.substring(0,10));
                                q.addParameter("DUMMY",dtLastDay.substring(0,9));
				q.includeMeta("ALL");
				cmdOn++;
				cmd2Done = 1;
			}

			if (!mgr.isEmpty(dtStop) && !mgr.isEmpty(dtFirstDay))
			{

				q = trans1.addQuery("ISGRT2","select trunc(to_date(?,'YYYY-MM-DD'))-trunc(to_date(?,'DD-MON-YY')) RES FROM DUAL");
                                q.addParameter("DUMMY",dtStop.substring(0,10));
                                q.addParameter("DUMMY",dtFirstDay.substring(0,9));
				q.includeMeta("ALL");
				cmdOn++;
				cmd3Done = 1;
			}

			if (cmdOn>0)
				trans1 = mgr.perform(trans1);


			if (cmdOn>0)
			{

				if (cmd1Done > 0)
					result1 = trans1.getValue("ISGRT1/DATA/RES");

				if (cmd2Done > 0)
					result2 = trans1.getValue("ISGRT2/DATA/RES");

				if (cmd3Done > 0)
				{

					if (!mgr.isEmpty(trans1.getValue("NROLE/DATA/NRL")))
						nRoleDay = toDouble(trans1.getValue("NROLE/DATA/NRL"));
					else
						nRoleDay	= 0 ;
				}
				else
					nRoleDay	= 0 ;

			}

			trans1.clear(); 


			if (!mgr.isEmpty(result1))
			{
				if (toDouble(result1) < 0)
					cond1 = "OK";
				else if (toDouble(result1) > 0)
					cond1= "NOTOK";
				else
					cond1	= "OK";    
			}
			else
				cond1	= "OK";


			if (!mgr.isEmpty(result2))
			{
				if (toDouble(result2) < 0)
					cond2 = "NOTOK";
				else if (toDouble(result2) > 0)
					cond2 = "OK";
				else
					cond2	= "OK";  
			}
			else
				cond2	= "OK";

			if (( nPlanHrs>0 ) &&  ( nDays > 0 ) &&  ( !("".equals(dtStart)) ) &&  ( "OK".equals(cond1) ) &&  ( "OK".equals(cond2) ))
			{

				nRoleEnd = toDouble(nRoleDay) + nDays -1;


				while (nRoleDay <= nRoleEnd)
				{

					if (nRoleDay > 0)
					{
						m = 0;
						nHrs = (nPlanHrs/nDays);
						if (nPlanMen>0)
							nHrs = toDouble(nHrs) * toDouble(nPlanMen);
						if (m < nDays)
							m = m + 1;
						nDaySum[(int)nRoleDay] = nDaySum[(int)nRoleDay] + nHrs;

					}
					nRoleDay = toDouble(nRoleDay) + 1;
				}

			}


		}
		trans.clear();

		q = trans.addQuery("FORMAT","SELECT to_date(to_char(trunc(to_date(?,'DD-MON-YY')),'MM/DD/YYYY'),'MM/DD/YYYY') TEM_NUM1 FROM DUAL");
                q.addParameter("DUMMY",dtFirstDay.substring(0,9));

		cmd = trans.addCustomFunction( "NWEEK","Work_Order_Role_API.Get_Week","TEM_NUM2");
		cmd.addReference("TEM_NUM1","FORMAT/DATA");
		trans = mgr.perform(trans);
		nWeekCount = Integer.parseInt(trans.getValue("NWEEK/DATA/TEM_NUM2"));
		trans.clear();

		i=0;
		k=1;

		dtWeekDate = dtFirstDay;

		cmd = trans.addCustomCommand("DEFCON", "User_Default_API.Get_User_Contract");
		cmd.addParameter("TEM_NUM1");                         
		trans = mgr.perform(trans);                                                                    
		con = trans.getValue("DEFCON/DATA/TEM_NUM1");    
		trans.clear();

		cmd = trans.addCustomFunction("DEFCOM", "Site_API.Get_Company","TEM_NUM1");
		cmd.addParameter("TEM_NUM1", sContract);                         
		trans = mgr.perform(trans);                                                                    
		sCompany = trans.getValue("DEFCOM/DATA/TEM_NUM1");    


		String[] lsData2 = new String[10000];
		String[] lsData = new String[10000];
		String[] lsLabels  = new String[10000];


		while (i <  toDouble(nPast) + toDouble(nFuture) + 1)
		{
			numval = String.valueOf(toDouble(i)*7);


			dtStartHR = addNumToDate(dtFirstDay,numval);
			dtStopHR = addNumToDate(dtStartHR,String.valueOf(6));
			trans.clear();

			// checking for method availabilities before calling the function for Available Time

			if (checksec("ACTIVE_WORK_ORDER_UTIL_API.Get_Available_Hours",1))
			{
                                q = trans.addQuery("FORMAT1","SELECT to_date(to_char(trunc(to_date(?,'YYYY-MM-DD')),'MM/DD/YYYY'),'MM/DD/YYYY') TEM_NUM3 FROM DUAL");
                                q.addParameter("DUMMY",dtStartHR.substring(0,10));
                                q = trans.addQuery("FORMAT2","SELECT to_date(to_char(trunc(to_date(?,'YYYY-MM-DD')),'MM/DD/YYYY'),'MM/DD/YYYY') TEM_NUM4 FROM DUAL");
                                q.addParameter("DUMMY",dtStopHR.substring(0,10));

				cmd = trans.addCustomFunction( "STDATE","ACTIVE_WORK_ORDER_UTIL_API.Get_Available_Hours","TEM_DATE");
				cmd.addParameter("TEM_NUM1",sCompany);
				cmd.addParameter("TEM_NUM2",sSignId);
				cmd.addReference("TEM_NUM3","FORMAT1/DATA");
				cmd.addReference("TEM_NUM4","FORMAT2/DATA");

				trans = mgr.perform(trans);
				lsData2[i] = trans.getValue("STDATE/DATA/TEM_DATE");
				trans.clear();
			}

			for (cnt=1;cnt<=7;cnt++)
			{

				nWeekSum[nWeekCount] = nWeekSum[nWeekCount] + nDaySum[k];

				k=k+1 ;
			}

			lsData[i] = String.valueOf(nWeekSum[nWeekCount]);
			lsLabels[i] = String.valueOf(nWeekCount);
			dtWeekDate = addNumToDate(dtWeekDate,String.valueOf(7));

			trans.clear();

                        q = trans.addQuery("FORMAT","SELECT to_date(to_char(trunc(to_date(?,'YYYY-MM-DD')),'MM/DD/YYYY'),'MM/DD/YYYY') TEM_NUM1 FROM DUAL");
                        q.addParameter("DUMMY",dtWeekDate.substring(0,10));

			cmd = trans.addCustomFunction( "GTWEEK","Work_Order_Role_API.Get_Week","TEM_DATE");
                        cmd.addReference("TEM_NUM1","FORMAT/DATA");
			trans = mgr.perform(trans);
			nWeekCount = Integer.parseInt(trans.getValue("GTWEEK/DATA/TEM_DATE"));
			trans.clear();

			i = i+1;
		}

		String[] _lsData2 = new String[10000];
		String[] _lsData = new String[10000];
		String[] _lsLabels  = new String[10000];

		i = 0;
		while (i <  toDouble(nPast) + toDouble(nFuture) + 1)
		{

			_lsData2[i] = lsData2[i];
			_lsLabels[i]  = lsLabels[i];
			_lsData[i] = lsData[i];


			i = i + 1;
		}

		createGraph(_lsData2,_lsLabels,_lsData);

	}


	public void  createGraph(String[] _lsData2,String[] _lsLabels,String[] _lsData)
	{

		int j;
		int noOfRows;  
		String[] lsData2 = new String[10000];
		String[] lsData = new String[10000];
		String[] lsLabels  = new String[10000];


		ASPManager mgr = getASPManager();

		if ("0".equals(option))
			noOfRows = 7;
		else
			noOfRows	= Integer.parseInt(nPast) + Integer.parseInt(nFuture) + 1;


		dept = sSign;

		for (j = 1 ;j<= noOfRows ; j++)
		{
			lsData2[j] = _lsData2[j-1];
			lsLabels[j]  = _lsLabels[j-1];
			lsData[j] = _lsData[j-1]; 

		}    

		graphflag = 1;


		deptGraph = mgr.newASPGraph(ASPGraph.COLUMNCHART);



		deptGraph.setLeftTitle(mgr.translate("PCMWEMPLOYEERESOURCESGRAPHHRS: Hours"));
		deptGraph.setLeftTitleFont(new Font(deptGraph.getLeftTitleFont().getFontName(),deptGraph.getLeftTitleFont().getStyle(),deptGraph.getLeftTitleFont().getSize()+2));
		//   deptGraph.setRowWidth(20);
		//   deptGraph.setGraphFillFactor(90);

		if (!mgr.isEmpty(nWeekNumber))
		{
			deptGraph.setGraphTitle(mgr.translate("PCMWEMPLOYEERESOURCESGRAPHDEPTPLAN: Planned Time Employee " )+dept+", Week "+nWeekNumber);
			deptGraph.setGraphTitleFont(new Font(deptGraph.getGraphTitleFont().getFontName(),deptGraph.getGraphTitleFont().BOLD,deptGraph.getGraphTitleFont().getSize()+8));
			deptGraph.setMaxValue((double)50);
			deptGraph.setMinValue((double)1);


			deptGraph.setBottomTitle(mgr.translate("PCMWEMPLOYEERESOURCESGRAPHWKDAY: Day"));
			deptGraph.setBottomTitleFont(new Font(deptGraph.getBottomTitleFont().getFontName(),deptGraph.getBottomTitleFont().getStyle(),deptGraph.getBottomTitleFont().getSize()+2));

		}
		else
		{
			deptGraph.setGraphTitle(mgr.translate("PCMWEMPLOYEERESOURCESGRAPHDEPTPLAN: Planned Time Employee " )+dept);
			deptGraph.setGraphTitleFont(new Font(deptGraph.getGraphTitleFont().getFontName(),deptGraph.getGraphTitleFont().BOLD,deptGraph.getGraphTitleFont().getSize()+8));
			deptGraph.setMinValue((double)1);

			deptGraph.setBottomTitle(mgr.translate("PCMWEMPLOYEERESOURCESGRAPHWEEK: Week"));
			deptGraph.setBottomTitleFont(new Font(deptGraph.getBottomTitleFont().getFontName(),deptGraph.getBottomTitleFont().getStyle(),deptGraph.getBottomTitleFont().getSize()+2));

		}   

		Vector colorVector = new Vector(2);
		colorVector.add(Color.blue);
		colorVector.add(Color.green);              
		deptGraph.setColors(colorVector);

		deptGraph.setNumPoints(noOfRows);
		deptGraph.setWidth(700);
		deptGraph.setHeight(350);
		deptGraph.setNumSets(2);

		deptGraph.setLegend(1,mgr.translate("PCMWEMPLOYEERESOURCESGRAPHPLANTIME: Planned Time")); 
		deptGraph.setLegend(2,mgr.translate("PCMWEMPLOYEERESOURCESGRAPHAVAILTIME: Available Time")); 
		deptGraph.setLegendFont(new Font(deptGraph.getLegendFont().getFontName(),deptGraph.getLegendFont().getStyle(),deptGraph.getLegendFont().getSize()+4));


		deptGraph.setLabelFont(new Font(deptGraph.getLabelFont().getFontName(),deptGraph.getLabelFont().getStyle(),deptGraph.getLabelFont().getSize()+2));
		deptGraph.setColumnOutline(true);



		for (i=1;i<3;i++)
		{
			pnum = 1;
			for (count4=1;count4<=noOfRows;count4++)
			{

				data2 = lsData2[count4];
				data1 = lsData[count4];
				
				if (i == 1)
				{
					deptGraph.setDataAt(i,pnum,toDouble(data1));
				}
				else
				{
					deptGraph.setDataAt(i,pnum,toDouble(data2)); 
				}                               
				deptGraph.setLabel(pnum,lsLabels[count4]);                                                  
				pnum++; 

			}

		}


		deptGraph_Width = deptGraph.getWidth();
		deptGraph_Height = deptGraph.getHeight();


	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();



		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("WEEK_FORMAT");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_DATE");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM1");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM2");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM3");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM4");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM5");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("SYSDATE","Date");
		f.setHidden();

                f = headblk.addField("DUMMY", "String");
                f.setHidden();
                f.setFunction("''");

		headblk.setView("DUAL");                  
		headset = headblk.getASPRowSet();                         

		headlay = headblk.getASPBlockLayout();                    
		headlay.setDialogColumns(1);
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();  


		headtbl = mgr.newASPTable(headblk);                       
		headtbl.setTitle(mgr.translate("PCMWEMPLOYEERESOURCESGRAPHMAST: Parameters to Resource Graph"));  
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);                  
		headbar.disableCommand(headbar.REMOVE);

		headbar.disableCommand(headbar.PREVIOUS);                
		headbar.disableCommand(headbar.NEXT);                    
		headbar.disableCommand(headbar.COUNT);                   
		headbar.disableCommand(headbar.DUPLICATE);               
		headbar.disableCommand(headbar.FIND);                    
		headbar.disableCommand(headbar.OKFIND);                  
		headbar.disableCommand(headbar.COUNTFIND);               
		headbar.disableCommand(headbar.EDIT);                    
		headbar.disableCommand(headbar.CANCELFIND);              
		headbar.disableCommand(headbar.CANCELEDIT);              
		headbar.disableCommand(headbar.BACK);  
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");                        
		headbar.defineCommand(headbar.SAVERETURN,"ok");               
		headbar.enableCommand(headbar.CANCELNEW);                
		headbar.defineCommand(headbar.CANCELNEW,"cancel");       

		disableOptions();
		disableNavigate();
		disableHomeIcon();
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWEMPLOYEERESOURCESGRAPHTITLE: Employee Resources";
	}

	protected String getTitle()
	{
		return "PCMWEMPLOYEERESOURCESGRAPHTITLE: Employee Resources";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<center>\n");

		if (graphflag == 1)
			appendToHTML("<table align=\"center\" id=\"grpTBL\" border=\"0\" width=\"100%\" >\n");

		appendToHTML("<tr>\n");

		if (graphflag == 1)
		{
			appendToHTML("   <align = center>\n");
			appendToHTML("     <td width=\"50%\">&nbsp;<p Align=\"center\"><img src=");
			appendToHTML(deptGraph.getGraph()); // XSS_Safe ILSOLK 20070713
			appendToHTML(" Width= ");
			appendToHTML(String.valueOf(deptGraph_Width));
			appendToHTML(" Height= ");
			appendToHTML(String.valueOf(deptGraph_Height));
			appendToHTML(" border = 0 ></td>\n");
		}

		appendToHTML("</tr>\n");
		appendToHTML("</table>\n");
		appendToHTML("</center>\n");
	}
}
