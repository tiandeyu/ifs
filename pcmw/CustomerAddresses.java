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
*  File        : CustomerAddresses.java
*  Modified  
*   2006-09-12    NAMELK     - Created.
*   070710      ILSOLK Eliminated XSS.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CustomerAddresses extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CustomerAddresses");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;
        private ASPTransactionBuffer trans;
        private ASPField f;

	
        private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

        private String addId;
         
   //===============================================================
   // Construction 
   //===============================================================
   public CustomerAddresses(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

        public void run() 
        {
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();  

                addId = ctx.readValue("ASSID", "");
                
                if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTOMER_ID")))
			okFind();
                
                ctx.writeValue("ASSID",addId);      
	}
        
        public void dummyMethod()
        {
            ASPManager mgr = getASPManager();
        }                         
        
        public void  okFind()
	{
		ASPManager mgr = getASPManager();
                
                trans.clear();
                ASPQuery q = trans.addQuery(headblk);
                q.includeMeta("ALL");
                mgr.submit(trans);
        }

        protected String getDescription()
	{
		return "PCMWCUSTOMERADDRESSESTITLE: Customer Addresses";
	}

	protected String getTitle()
	{
		return "PCMWCUSTOMERADDRESSESTITLE: Customer Addresses";
	}

        public void selectAdd()
        {
            ASPManager mgr = getASPManager();

            if (headlay.isMultirowLayout())
             headset.goTo(headset.getRowSelected());
                
            addId = headset.getValue("ADDRESS_ID");
        }


        public void  preDefine()
        {
            ASPManager mgr = getASPManager();
            
            headblk = mgr.newASPBlock("HEAD"); 
              
	    f = headblk.addField("OBJID");
	    f.setHidden();

	    f = headblk.addField("OBJVERSION");
	    f.setHidden();

            f = headblk.addField("ADDRESS_ID");
            f.setSize(50);
            f.setLabel("PCMCUSTOMERADDRESSESADDRESSID: Address Id");

            f = headblk.addField("CUSTOMER_ID");
            f.setSize(20);
	    f.setLabel("PCMCUSTOMERADDRESSESCUSTNO: Customer No");   
            
	    f = headblk.addField("CUSTOMER_NAME");
	    f.setSize(20);
	    f.setLabel("PCMCUSTOMERADDRESSESCUSTNAME: Customer Name");
	    f.setFunction("CUSTOMER_NAME"); 

            f = headblk.addField("ADDRESS1");
	    f.setSize(30);
            f.setLabel("PCMCUSTOMERADDRESSESADDRESS1: Address 1");

            f = headblk.addField("ADDRESS2");
            f.setSize(30);
            f.setLabel("PCMCUSTOMERADDRESSESADDRESS2: Address 2");

            f = headblk.addField("ZIP_CODE");
            f.setSize(30);
            f.setLabel("PCMCUSTOMERADDRESSESZIPCODE: ZIP CODE");

            f = headblk.addField("CITY");
            f.setSize(30);
            f.setLabel("PCMCUSTOMERADDRESSESCITY: CITY");

            f = headblk.addField("STATE");
            f.setSize(30);
            f.setLabel("PCMCUSTOMERADDRESSESSTATE: STATE");

            f = headblk.addField("COUNTY");
            f.setSize(30);
            f.setLabel("PCMCUSTOMERADDRESSESCOUNTY: COUNTY");

            f = headblk.addField("COUNTRY");
            f.setSize(30);
            f.setLabel("PCMCUSTOMERADDRESSESCOUNTRY: COUNTRY");

            f = headblk.addField("COUNTRY_DB");
            f.setSize(30);
            f.setLabel("PCMCUSTOMERADDRESSESCOUNTRYCODE: COUNTRY CODE");

            headblk.setView("CUSTOMER_ADDRESSES");
            headblk.defineCommand("ACTIVE_WORK_ORDER_UTIL_API","");
           
            headset = headblk.getASPRowSet();
            headtbl = mgr.newASPTable(headblk);
            headtbl.setTitle(mgr.translate("PCMCUSTOMERADDRESSESITM1: Customers Addresses"));   
            headtbl.setWrap();

            headbar = mgr.newASPCommandBar(headblk);
            

            headbar.addCustomCommand("dummyMethod",mgr.translate("PCMCUSTOMERADDRESSESITEM1DUMMY1: Dummy Receive Order1...")); 
            headbar.addCustomCommand("selectAdd",mgr.translate("PCMCUSTOMERADDRESSESITEM1SELECT: Select"));
            headbar.enableCommand(headbar.FIND);
            headblk.disableHistory();
		
            headblk.setTitle(mgr.translate("PCMCUSTOMERADDRESSESITEM1TIL: Customer Addresses"));     
	    headlay = headblk.getASPBlockLayout();
	    headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);   
        }
        
   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      out.clear();

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getTitle()));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(getTitle()));
      out.append(headlay.show());
      out.append(mgr.endPresentation());
      
      if (!mgr.isEmpty(addId))
      {
          appendDirtyJavaScript("  jsAddId = '");
          appendDirtyJavaScript(mgr.encodeStringForJavascript(addId)); // XSS_Safe ILSOLK 20070710
          appendDirtyJavaScript("';\n");
          appendDirtyJavaScript("   window.opener.setCustomerAddress(jsAddId);\n");
          appendDirtyJavaScript("   window.close();\n");
      }

      appendDirtyJavaScript("setTimeout('window.focus()',500);");
      return out;
   }        
        
}