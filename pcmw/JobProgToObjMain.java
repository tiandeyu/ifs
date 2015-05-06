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
*  File        : JobProgToObjMain.java 
*  Created     : NIJALK  050314 (AMEC112 - Job Program)
*  Modified    : 
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  AMNILK  060807  Merged Bug Id: 58214.
*  ILSOLK  070730  Eliminated LocalizationErrors.
*  SHAFLK  080401  Bug 72675, Modified getContents().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class JobProgToObjMain extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.JobProgToObjMain");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPBuffer buff;

	private String mchCode;
        private String mchContract;
        private String jobProgId;
        private String jobProgRev;
        private String jobProgContract;

	private String current_url;
	private int introFlag;
	private String frameErrorValue;
	private String titleValue;
	private String sBackGroundColor;
	private String sTextColor;   

	//===============================================================
	// Construction 
	//===============================================================
	public JobProgToObjMain(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
                mchCode = "";
                mchContract = "";
                jobProgId = "";
                jobProgRev = "";
                jobProgContract = "";

		ctx = mgr.getASPContext();
                buff = mgr.newASPBuffer();
		introFlag = ctx.readNumber("INTROFLAG",0);
		current_url = mgr.getURL();         
		ctx.setGlobal("CALLING_FORM",current_url);

                mchCode = ctx.readValue("MCH_CODE",mchCode);
                mchContract = ctx.readValue("MCH_CONTRACT",mchContract);
                jobProgId = ctx.readValue("JOB_PROGRAM_ID",jobProgId);
                jobProgRev = ctx.readValue("JOB_PROGRAM_REVISION",jobProgRev);
                jobProgContract = ctx.readValue("JOB_PROGRAM_CONTRACT",jobProgContract);

		if ((!mgr.isEmpty(mgr.readValue("MCH_CODE")) && !mgr.isEmpty(mgr.readValue("CONTRACT"))) || (!mgr.isEmpty(mgr.readValue("JOB_PROGRAM_ID")) && !mgr.isEmpty(mgr.readValue("JOB_PROGRAM_REVISION")) && !mgr.isEmpty(mgr.readValue("JOB_PROGRAM_CONTRACT"))))
		{
			mchCode = mgr.readValue("MCH_CODE");        
			mchContract =mgr.readValue("CONTRACT");
                        jobProgId = mgr.readValue("JOB_PROGRAM_ID");
                        jobProgRev = mgr.readValue("JOB_PROGRAM_REVISION");
                        jobProgContract = mgr.readValue("JOB_PROGRAM_CONTRACT");

		        introFlag = 1;
		}
                else if (mgr.dataTransfered())
                {
                    buff = mgr.getTransferedData();

                    mchCode = buff.getBufferAt(0).getValue("MCH_CODE");        
                    mchContract = buff.getBufferAt(0).getValue("CONTRACT");  
                    jobProgId = buff.getBufferAt(0).getValue("JOB_PROGRAM_ID");
                    jobProgRev = buff.getBufferAt(0).getValue("JOB_PROGRAM_REVISION");
                    jobProgContract = buff.getBufferAt(0).getValue("JOB_PROGRAM_CONTRACT");
                    introFlag = 1;
                }

		frameErrorValue = mgr.translate("PCMWJOBPROGTOOBJFRAMERANA: This page uses frames, but your browser doesn't support them.");
		titleValue = mgr.translate("PCMWJOBPROGTOOBJ: Add Job Program to Object");

		sBackGroundColor = mgr.getConfigParameter("SCHEME/FORM/BGCOLOR");
		sTextColor = mgr.getConfigParameter("SCHEME/FORM/FONT/COLOR/NORMAL");  

                ctx.writeValue("MCH_CODE",mchCode);
                ctx.writeValue("MCH_CONTRACT",mchContract);
                ctx.writeValue("JOB_PROGRAM_ID",jobProgId);
                ctx.writeValue("JOB_PROGRAM_REVISION",jobProgRev);
                ctx.writeValue("JOB_PROGRAM_CONTRACT",jobProgContract);
		ctx.writeNumber("INTROFLAG",introFlag);
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
            return "PCMWJOBPROGTOOBJMAINTITLE: Add Job Program to Object";
	}

	protected String getTitle()
	{
            return "PCMWJOBPROGTOOBJMAINTITLE: Add Job Program to Object";
	}

        protected AutoString getContents() throws FndException
	{ 
		AutoString out = getOutputStream();
		out.clear();
		ASPManager mgr = getASPManager();

		out.append("<html>\n");
		out.append("<head>\n");
		out.append("<title>");
		out.append(titleValue);
		out.append("</title>\n");
		out.append("</head>\n");

                //Bug 72675, start
                if (introFlag == 1)
                {
                        out.append("  <iframe name=\"__headerPart\" src=\"JobProgToObject.page?MCH_CODE=");
                        out.append(mgr.URLEncode(mchCode));
                        out.append("&MCH_CONTRACT=");
                        out.append(mgr.URLEncode(mchContract));
                        out.append("&JOB_PROGRAM_ID=");
                        out.append(mgr.URLEncode(jobProgId));
                        out.append("&JOB_PROGRAM_REVISION=");
                        out.append(mgr.URLEncode(jobProgRev));
                        out.append("&JOB_PROGRAM_CONTRACT=");
                        out.append(mgr.URLEncode(jobProgContract));
                        out.append("\" width = \"100%\" height = \"50%\">\n");
                        out.append("</iframe>");
                }
                else if (introFlag == 0){

                    out.append("  <iframe name=\"__headerPart\" src=\"JobProgToObject.page\" width = \"100%\" height = \"50%\">");
                    out.append("</iframe>");

                }
                if (introFlag == 1)
                {
                        out.append("    <iframe name=\"__pmPart\" src=\"JobProgToObjectItems.page?MCH_CODE=");
                        out.append(mgr.URLEncode(mchCode));
                        out.append("&MCH_CODE_CONTRACT=");
                        out.append(mgr.URLEncode(mchContract));
                        out.append("&JOB_PROGRAM_ID=");
                        out.append(mgr.URLEncode(jobProgId));
                        out.append("&JOB_PROGRAM_REVISION=");
                        out.append(mgr.URLEncode(jobProgRev));
                        out.append("&JOB_PROGRAM_CONTRACT=");
                        out.append(mgr.URLEncode(jobProgContract));
                        out.append("&FROMJOBPROGOBJ=1&WINOPENED=1");
                        out.append("\" width = \"100%\" height = \"50%\">");
                        out.append("</iframe>");
                }
                //Bug 58214, end
                else {
                    out.append("<iframe name=\"__pmPart\" src=\"JobProgToObjectItems.page\" width = \"100%\" height = \"50%\">");
                    out.append("</iframe>");

                }
                //Bug 72675, end


		out.append("</frameset>\n");
		out.append("<noframes>\n");
		out.append("  <body bgcolor=\"");
		out.append(sBackGroundColor);
		out.append("\" text=\"");
		out.append(sTextColor);
		out.append("\">\n");
		out.append("  <p>");
		out.append(frameErrorValue);
		out.append("</p>\n");
		out.append("  </body>\n");
		out.append("</noframes>\n");
		out.append("</html>\n");

                appendDirtyJavaScript("window.name = \"JobProgToObjMain\"\n");

		return out;       
	}   
}
