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
*  File        : TechnicalObjectReference.java 
*  Modified    : NUPELK : 27-04-2001
*    ASP2JAVA Tool  2001-04-26  - Created Using the ASP file TechnicalObjectReference.asp
*    NUPELK  14-05-2001 Made adjustments because of Security Scanning warnings 
*    CHCRLK  16-05-2001 Modified adjust() and run() methods to enable edit of characteristics.   
*    VAGULK  22-05-2001 Code Review
*    SHCH    23-08-01   Modified the newRow() function.
*    NUPELK  08-09-2001  Removed Cancel button and corrected the problem with "No Data" msg.
*    BAKALK  30-09-2002  Bug Id:31642, Layout mode of itemlay1 and itemlay2 are set to Single_layout only when 
*                        a button is pressed. 
*    SUMELK  29-09-2004 Call ID 115891, Added new menu item Change Status.
*    NEKOLK  27-09-2005 AMUT 115: Added group name list box and made necessary cahnges..
*    NEKOLK  18-02-2006 Call 134451-Modified JavaScript.
*    NEKOLK  16-03-2006 Call 137419.Modified PreDefine() for Info and value text.
* ----------------------------------------------------------------------------
* New Comments:
* 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
* 2006/09/13 sumelk Bug 59368, Corrected erroneous translation constants.   
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;         
import ifs.fnd.*;
import java.util.Date;


public class TechnicalObjectReference extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.TechnicalObjectReference");


    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;
    private ASPBlock itemblk0;
    private ASPRowSet itemset0;
    private ASPCommandBar itembar0;
    private ASPTable itemtbl0;
    private ASPBlockLayout itemlay0;
    private ASPBlock itemblk1;
    private ASPRowSet itemset1;
    private ASPCommandBar itembar1;
    private ASPTable itemtbl1;
    private ASPBlockLayout itemlay1;
    private ASPBlock itemblk2;
    private ASPRowSet itemset2;
    private ASPCommandBar itembar2;
    private ASPTable itemtbl2;
    private ASPBlockLayout itemlay2;

    private ASPBlock itemblk3;
    private ASPRowSet itemset3;
    private ASPCommandBar itembar3;
    private ASPTable itemtbl3;
    private ASPBlockLayout itemlay3;
    
    private ASPBlock itemblk4;
    private ASPRowSet itemset4;
    private ASPCommandBar itembar4;
    private ASPTable itemtbl4;
    private ASPBlockLayout itemlay4;
    
    private ASPBlock itemblk5;
    private ASPRowSet itemset5;
    private ASPCommandBar itembar5;
    private ASPTable itemtbl5;
    private ASPBlockLayout itemlay5;

    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String lu_name;
    private String key_ref;
    private String form_name;
    private String mode;
    private String group;
    private ASPBuffer buffer;
    private ASPBuffer row;
    private String flag;
    private String val;
    private ASPCommand cmd;
    private String descr;
    private ASPQuery q;
    private String attribute;
    private ASPBuffer data;
    private int currrow;
    private String n;
    private String buttonPressed;

    private String txt; 
    private String lsAttr;
    private int beg_pos; 
    private int end_pos;  
    private String ok_yes_no;
    private String ok_sign; 
    private String dt_ok;
    private int datePos; 
    private String technical_spec_no; 

    //===============================================================
    // Construction 
    //===============================================================
    public TechnicalObjectReference(ASPManager mgr, String page_path)
    { 
        super(mgr,page_path);
    }


    protected void doReset() throws FndException
    {
        //Resetting mutable attributes
        trans   = null;
        lu_name   = null;
        key_ref   = null;
        form_name   = null;
        mode   = null;
        group  = null;
        buffer   = null;
        row   = null;
        flag   = null;
        val   = null;
        cmd   = null;
        descr   = null;
        q   = null;
        attribute   = null;
        data   = null;
        currrow   = 0;
        n   = null;
        buttonPressed = null;

        txt   = null;
        lsAttr   = null;
        beg_pos   = 0;
        end_pos   = 0;
        ok_yes_no   = null;
        ok_sign   = null;
        dt_ok   = null;
        datePos   = 0;
        technical_spec_no   = null;

        super.doReset();
    }

    public ASPPoolElement clone(Object obj) throws FndException
    {
        TechnicalObjectReference page = (TechnicalObjectReference)(super.clone(obj));

        // Initializing mutable attributes
        page.trans   = null;
        page.lu_name   = null;
        page.key_ref   = null;
        page.form_name   = null;
        page.mode   = null;     
        page.group   = null;     
        page.buffer   = null;
        page.row   = null;
        page.flag   = null;
        page.val   = null;
        page.cmd   = null;
        page.descr   = null;
        page.q   = null;
        page.attribute   = null;
        page.data   = null;
        page.currrow   = 0;
        page.n   = null;
        page.buttonPressed = null;

        // Cloning immutable attributes
        page.ctx = page.getASPContext();
        page.fmt = page.getASPHTMLFormatter();
        page.headblk = page.getASPBlock(headblk.getName());
        page.headset = page.headblk.getASPRowSet();
        page.headbar = page.headblk.getASPCommandBar();
        page.headtbl = page.getASPTable(headtbl.getName());
        page.headlay = page.headblk.getASPBlockLayout();
        page.itemblk0 = page.getASPBlock(itemblk0.getName());
        page.itemset0 = page.itemblk0.getASPRowSet();
        page.itembar0 = page.itemblk0.getASPCommandBar();
        page.itemtbl0 = page.getASPTable(itemtbl0.getName());
        page.itemlay0 = page.itemblk0.getASPBlockLayout();
        page.itemblk1 = page.getASPBlock(itemblk1.getName());
        page.itemset1 = page.itemblk1.getASPRowSet();
        page.itembar1 = page.itemblk1.getASPCommandBar();
        page.itemtbl1 = page.getASPTable(itemtbl1.getName());
        page.itemlay1 = page.itemblk1.getASPBlockLayout();
        page.itemblk2 = page.getASPBlock(itemblk2.getName());
        page.itemset2 = page.itemblk2.getASPRowSet();
        page.itembar2 = page.itemblk2.getASPCommandBar();
        page.itemtbl2 = page.getASPTable(itemtbl2.getName());
        page.itemlay2 = page.itemblk2.getASPBlockLayout();
        page.f = page.getASPField(f.getName());


        page.txt   = null;
        page.lsAttr   = null;
        page.beg_pos   = 0;
        page.end_pos   = 0;
        page.ok_yes_no   = null;
        page.ok_sign   = null;
        page.dt_ok   = null;
        page.datePos   = 0;
        page.technical_spec_no   = null;

        return page;
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        mgr.setPageExpiring();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter(); 

        lu_name = ctx.readValue("LUNAME","");
        key_ref = ctx.readValue("KEYREF","");
        form_name = ctx.readValue("FORM_NAME","FunctionalObject");
        mode = ctx.readValue("FORMMODE","3");

        group = ctx.readValue("GROUP","");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
        {
            buffer = mgr.getTransferedData();
            row = buffer.getBufferAt(0);
            lu_name = row.getValue("LU_NAME");
            key_ref = row.getValue("KEY_REF");

            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("MODE")))
        {
            lu_name   = mgr.readValue("LU_NAME");                 
            key_ref   = mgr.readValue("KEY_REF"); 
            form_name = mgr.readValue("FORM_NAME"); 
            mode = mgr.readValue("MODE");
            group = mgr.readValue("GROUPNAME");

            buttonPressed = "TRUE";
            okFind();   
            if (mgr.isEmpty(group))
            {
                if ( "1".equals(mode) )
                    okFindITEM1();
                if ( "2".equals(mode) )
                    okFindITEM2();
                if ( "3".equals(mode) )
                    okFindITEM0();
                   }else
                   {
                if ( "1".equals(mode) )
                    okFindITEM4();
                if ( "2".equals(mode) )
                    okFindITEM5();
                if ( "3".equals(mode) )
                    okFindITEM3();
            }
        } else if ( (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME")) || !mgr.isEmpty(mgr.getQueryStringValue("KEY_REF"))) && (!mgr.isEmpty(mgr.getQueryStringValue("TECHOBJ"))) )
        {

            lu_name   = mgr.readValue("LU_NAME");                 
            key_ref   = mgr.readValue("KEY_REF"); 
            form_name = mgr.readValue("FORM_NAME");
            flag      = mgr.readValue("TECHOBJ"); 
            if ( "FALSE".equals(flag) )
                headbar.disableCommand(headbar.FIND);
            else
                okFind();          
        } else if (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME")) || !mgr.isEmpty(mgr.getQueryStringValue("KEY_REF")))
        {
            lu_name   = mgr.readValue("LU_NAME");                 
            key_ref   = mgr.readValue("KEY_REF"); 
            form_name = mgr.readValue("FORM_NAME"); 
            okFind();             
        }

        adjust();

        ctx.writeValue("LUNAME",lu_name);
        ctx.writeValue("KEYREF",key_ref); 
        ctx.writeValue("FORM_NAME",form_name);
        ctx.writeValue("FORMMODE",mode);
        ctx.writeValue("GROUP",group);
    }

//-----------------------------------------------------------------------------
//------------------------  VAIDATE FUNCTION  ---------------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();   
        val = mgr.readValue("VALIDATE");      

        if ( "TECHNICAL_CLASS".equals(val) )
        {
            cmd = trans.addCustomFunction("TECHDESCR","TECHNICAL_CLASS_API.Get_Description","DESCRIPTION");    
            cmd.addParameter("TECHNICAL_CLASS");                                                                        

            trans = mgr.validate(trans);                                                                          
            descr = trans.getValue("TECHDESCR/DATA/DESCRIPTION");                                                

            txt = (mgr.isEmpty(descr) ? "" : (descr))+ "^";                                                 
            mgr.responseWrite(txt);                                                                                  
        }

        mgr.endResponse();
    }


    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);     
        if (!mgr.isEmpty(lu_name) &&  !mgr.isEmpty(key_ref)){
         q.addWhereCondition("LU_NAME = ? and KEY_REF = ?");
         q.addParameter("LU_NAME", lu_name);
         q.addParameter("KEY_REF", key_ref);
        }
        q.includeMeta("ALL");     
        mgr.submit(trans);

        if (  headset.countRows() == 0 )
        {
            mgr.showAlert(mgr.translate("APPSRWTECHNICALOBJECTREFERENCENODATAFOUND: No data found."));  
        }

        trans.clear(); 
  
    }


    public void  newRow()
    {
        ASPManager mgr = getASPManager();

        lsAttr = "LU_NAME" + (char)31 + lu_name + (char)30 + "KEY_REF" + (char)31 +key_ref + (char)30 + "TECHNICAL_SPEC_NO" + (char)31 +"0"+ (char)30;

        q = trans.addQuery("SYSD","select SYSDATE DT_OK FROM DUAL");
        q.includeMeta("ALL");

        cmd = trans.addCustomCommand("HEAD","TECHNICAL_OBJECT_REFERENCE_API.NEW__");
        cmd.addParameter("INFO");    
        cmd.addParameter("OBJID");
        cmd.addParameter("OBJVERSION");
        cmd.addParameter("ATTR",lsAttr);
        cmd.addParameter("ACTION","PREPARE");   

        trans = mgr.perform(trans);

        attribute = trans.getValue("HEAD/DATA/ATTR");
        String sysd = trans.getBuffer("SYSD/DATA").getFieldValue("DT_OK");

        beg_pos = attribute.indexOf((char)31)+1;
        end_pos = attribute.indexOf((char)30);
        ok_yes_no = attribute.substring(beg_pos,end_pos);

        beg_pos = attribute.indexOf("OK_SIGN")+8;
        end_pos = attribute.indexOf("DT_OK")-1;
        ok_sign = attribute.substring(beg_pos,end_pos);

        beg_pos = attribute.indexOf("DT_OK")+6;
        end_pos = attribute.indexOf("TECHNICAL_SPEC_NO")-1;
        dt_ok = attribute.substring(beg_pos,end_pos);
        datePos = dt_ok.lastIndexOf("-");
        dt_ok = dt_ok.substring(0,datePos);

        beg_pos = attribute.lastIndexOf((char)31)+1;
        end_pos = attribute.lastIndexOf((char)30);
        technical_spec_no = attribute.substring(beg_pos,end_pos);

        data = trans.getBuffer("HEAD/DATA");
        data.setFieldItem("LU_NAME",lu_name);
        data.setFieldItem("KEY_REF",key_ref);
        data.setFieldItem("OK_YES_NO",ok_yes_no);
        data.setFieldItem("OK_SIGN",ok_sign);
        data.setFieldItem("DT_OK",sysd);
        data.setFieldItem("TECHNICAL_SPEC_NO",technical_spec_no);
        headset.addRow(data);
    }

//-----------------------------------------------------------------------------
//-----------------------  ITEMBAR FUNCTIONS  ---------------------------------
//-----------------------------------------------------------------------------

    public void countFind()
    {
        ASPManager mgr = getASPManager();
        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("LU_NAME = ? and KEY_REF = ?");
        q.addParameter("LU_NAME", lu_name);
        q.addParameter("KEY_REF", key_ref);
        mgr.submit(trans);

        n = headset.getRow().getValue("N");

        trans.clear();
        headlay.setCountValue(toInt(n));
        headset.goTo(currrow);

        mgr.showAlert(mgr.readValue("TECHNICAL_CLASS"));
    }

    public void  countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk0);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
        q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
        q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
        mgr.submit(trans);

        n = itemset0.getRow().getValue("N");

        trans.clear();
        itemlay0.setCountValue(toInt(n));
        headset.goTo(currrow);
        itemset0.clear();
    }


    public void  countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk1);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
        q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
        q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
        mgr.submit(trans);

        n = itemset1.getRow().getValue("N");

        itemlay1.setCountValue(toInt(n));
        headset.goTo(currrow);
        itemset1.clear();
    }


    public void  countFindITEM2()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk2);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
        q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
        q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
        mgr.submit(trans);

        n = itemset2.getRow().getValue("N");

        itemlay2.setCountValue(toInt(n));
        headset.goTo(currrow);
        itemset2.clear();
    }


    public void  countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk3);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ? and GROUP_NAME = ?");
        q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
        q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
        q.addParameter("GROUP_NAME", group);
        mgr.submit(trans);

        n = itemset3.getRow().getValue("N");

        trans.clear();
        itemlay3.setCountValue(toInt(n));
        headset.goTo(currrow);
        itemset3.clear();
    }


    public void  countFindITEM4()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk4);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ? and GROUP_NAME = ?");
        q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
        q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
        q.addParameter("GROUP_NAME", group);
        mgr.submit(trans);

        n = itemset4.getRow().getValue("N");

        itemlay4.setCountValue(toInt(n));
        headset.goTo(currrow);
        itemset4.clear();
    }


    public void  countFindITEM5()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk5);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ? and GROUP_NAME = ?");
        q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
        q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
        q.addParameter("GROUP_NAME", group);
        mgr.submit(trans);

        n = itemset5.getRow().getValue("N");

        itemlay5.setCountValue(toInt(n));
        headset.goTo(currrow);
        itemset5.clear();
    }


    public void  okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0 && "3".equals(mode))
        {
            trans.clear();
            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk0);
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            trans.clear();

            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk1);
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            trans.clear();

            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk2);
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM3()
    {
        ASPManager mgr = getASPManager();
        if (headset.countRows()>0 )
        {

            trans.clear();

            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk3);
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ? and GROUP_NAME = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.addParameter("GROUP_NAME", group);
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM4()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            trans.clear();

            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk4);
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ? and GROUP_NAME = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.addParameter("GROUP_NAME", group);
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM5()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            trans.clear();
            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk5);            
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ? and GROUP_NAME = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.addParameter("GROUP_NAME", group);
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM00()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0 && "3".equals(mode))
        {
            trans.clear();

            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk0);
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();


            if ( itemset0.countRows() == 0 )
            {
                mgr.showAlert(mgr.translate("APPSRWTECHNICALOBJECTREFERENCENODATAFOUND: No data found."));  
            }

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM11()
    {
        ASPManager mgr = getASPManager();   
        if (headset.countRows()>0  && "1".equals(mode))
        {
            trans.clear();

            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk1);
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);

            trans.clear();

            if ( itemset1.countRows() == 0 )
            {
                mgr.showAlert(mgr.translate("APPSRWTECHNICALOBJECTREFERENCENODATAFOUND: No data found."));  
            }

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM22()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0  && "2".equals(mode))
        {
            trans.clear();   
            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk2);
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();


            if ( itemset2.countRows() == 0 )
            {
                mgr.showAlert(mgr.translate("APPSRWTECHNICALOBJECTREFERENCENODATAFOUND: No data found."));  
            }

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM33()
    {
        ASPManager mgr = getASPManager();
        if (headset.countRows()>0 && "3".equals(mode))
        {
            trans.clear();

            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk3);
           
            q.addWhereCondition("TECHNICAL_CLASS = '" + headset.getRow().getValue("TECHNICAL_CLASS") + "' and TECHNICAL_SPEC_NO = '" + headset.getRow().getValue("TECHNICAL_SPEC_NO") + "' and GROUP_NAME = '" + group + "'");

            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();


            if ( itemset3.countRows() == 0 && (!mgr.isEmpty(group)))
            {
                mgr.showAlert(mgr.translate("APPSRWTECHNICALOBJECTREFERENCENODATAFOUND: No data found."));  
            }

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM44()
    {
        ASPManager mgr = getASPManager();   
        if (headset.countRows()>0  && "1".equals(mode))
        {
            trans.clear();

            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk4);
            
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ? and GROUP_NAME = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.addParameter("GROUP_NAME", group);
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();


            if ( itemset4.countRows() == 0 )
            {
                mgr.showAlert(mgr.translate("APPSRWTECHNICALOBJECTREFERENCENODATAFOUND: No data found."));  
            }

            headset.goTo(currrow);
        }
    }


    public void  okFindITEM55()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0  && "2".equals(mode))
        {
            trans.clear();   
            currrow = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk5);
            
            q.addWhereCondition("TECHNICAL_CLASS = ? and TECHNICAL_SPEC_NO = ? and GROUP_NAME = ?");
            q.addParameter("TECHNICAL_CLASS", headset.getRow().getValue("TECHNICAL_CLASS"));
            q.addParameter("TECHNICAL_SPEC_NO", headset.getRow().getValue("TECHNICAL_SPEC_NO"));
            q.addParameter("GROUP_NAME", group);
            q.setOrderByClause("ATTRIB_NUMBER");
            q.includeMeta("ALL");

            mgr.submit(trans);
            trans.clear();


            if ( itemset5.countRows() == 0 )
            {
                mgr.showAlert(mgr.translate("APPSRWTECHNICALOBJECTREFERENCENODATAFOUND: No data found."));  
            }

            headset.goTo(currrow);
        }
    }


    public void  changeStatus()
    {  
        ASPManager mgr = getASPManager();
        ASPCommand cmd;

        cmd = trans.addCustomCommand("CNGSTATUS", "Technical_Object_Reference_API.Change_Reference_Status_");
        cmd.addParameter("TECHNICAL_SPEC_NO",headset.getValue("TECHNICAL_SPEC_NO"));

        mgr.perform(trans);
        headset.refreshRow();
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();   
        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("ATTR");                                                          
        f.setHidden();  
        f.setFunction("''");  

        f = headblk.addField("ACTION");                                                          
        f.setHidden();    
        f.setFunction("''");

        f = headblk.addField("OBJID");                                                          
        f.setHidden();                                                                     

        f = headblk.addField("OBJVERSION");                                                
        f.setHidden();     

        f = headblk.addField("LU_NAME");                                                          
        f.setHidden();                                                                     

        f = headblk.addField("KEY_REF");                                                
        f.setHidden();   

        f = headblk.addField("TECHNICAL_SPEC_NO");                                                
        f.setHidden();                                                                    

        f = headblk.addField("TECHNICAL_CLASS");                                                  
        f.setSize(15);                                                                     
        f.setMandatory();                                                                  
        f.setDynamicLOV("TECHNICAL_CLASS",600,450);      
        f.setUpperCase();  
        f.setCustomValidation("TECHNICAL_CLASS","DESCRIPTION");                                                                
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCETECHCLS: Technical Class");                                                     
        f.setMaxLength(40);                                                                
        f.setInsertable();                                                                 

        f = headblk.addField("DESCRIPTION");                                                  
        f.setSize(25);   
        f.setReadOnly();                                                                    
        f.setFunction("TECHNICAL_CLASS_API.Get_Description(:TECHNICAL_CLASS)");                                                                  
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCETCHDESC: Description");                                                
        f.setMaxLength(45);                                                                

        f = headblk.addField("OK_YES_NO") ;                                                  
        f.setSize(18);                                                                      
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEOKYESNO: Status");                                                      
        f.setMaxLength(30);                                                                 
        f.setReadOnly();                                                                   

        f = headblk.addField("OK_SIGN");                                                 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEOKSIGN: By");                                                     
        f.setMaxLength(50);                                                                
        f.setReadOnly();                                                                   
        f.setSize(20);                                                                     

        f = headblk.addField("DT_OK","Date");                                                     
        f.setSize(25);                                                                     
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEDTOK: Date");                                                       
        f.setReadOnly();                                                                   
        f.setMaxLength(45);

        f = headblk.addField("DUMMY1","Date");                                                     
        f.setFunction("''");
        f.setHidden();


        f =headblk.addField("GRPSTRING");
        f.setFunction("TECHNICAL_GROUP_API.Get_Grp_Attr(:TECHNICAL_CLASS)");                                                                  
        f.setSize(100);
        f.setHidden();

        headblk.setView("TECHNICAL_OBJECT_REFERENCE");
        headblk.defineCommand("TECHNICAL_OBJECT_REFERENCE_API","New__,Modify__,Remove__");                 
        headset = headblk.getASPRowSet();                        

        headlay = headblk.getASPBlockLayout();                   
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);     
        headlay.setSimple("DESCRIPTION"); 
        headlay.setSimple("DT_OK");                              

        headtbl = mgr.newASPTable(headblk);                      
        headtbl.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEMAST: Demands")); 
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.removeCustomCommand(headbar.COUNTFIND);
        headbar.removeCustomCommand(headbar.EDITROW);
        headbar.defineCommand(headbar.NEWROW,"newRow");                 

        headbar.addCustomCommand("changeStatus",mgr.translate("APPSRWTECHNICALOBJECTREFCNGSTATUSRMB: Change Status"));

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk0.addField("ITEM0_TECHNICAL_SPEC_NO");
        f.setHidden();
        f.setDbName("TECHNICAL_SPEC_NO");

        f = itemblk0.addField("ITEM0_TECHNICAL_CLASS");
        f.setHidden();
        f.setDbName("TECHNICAL_CLASS");

        f = itemblk0.addField("ATTRIB_NUMBER");
        f.setSize(10);
        f.setReadOnly(); 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCENUMB: Order");

        f = itemblk0.addField("ATTRIBUTE");
        f.setSize(25);
        f.setReadOnly(); 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEATTRIBUTE: Attribute");
        f.setMaxLength(20);

        f = itemblk0.addField("ITEM0_DESCRIPTION");
        f.setReadOnly();  
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEDESCR: Description");                                                                  
        f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ATTRIBUTE)");    
        f.setSize(25);    

        f = itemblk0.addField("VALUE_TEXT");
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEVALTXT: Value Text"); 
        f.setSize(20);
        f.setMaxLength(20);

        f = itemblk0.addField("TECHUNIT");
        f.setSize(25);
        f.setReadOnly();
        f.setFunction("TECHNICAL_ATTRIB_NUMERIC_API.Get_Technical_Unit_(:ITEM0_TECHNICAL_CLASS,:ATTRIBUTE)");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCETECHUNIT: Technical Unit");
        f.setMaxLength(60);

        f = itemblk0.addField("VALUE_NO");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEVALNO: Value Number");
        f.setMaxLength(25);

        f = itemblk0.addField("LOWER_LIMIT");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCELOLIM: Lower Limit");
        f.setMaxLength(25);

        f = itemblk0.addField("UPPER_LIMIT");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEUPLIM: Upper Limit");
        f.setMaxLength(25);

        f = itemblk0.addField("INFO");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEINFO: Info");
        f.setMaxLength(2000);

        itemblk0.setView("TECHNICAL_SPECIFICATION_BOTH");
        itemblk0.setMasterBlock(headblk);
        itemblk0.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM0: Characteristics - All"));
        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.setBorderLines(false,true);
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0"); 
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM00"); 
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.disableCommand(itembar0.EDITROW);     
        itembar0.disableCommand(itembar0.NEWROW);      
        itembar0.disableCommand(itembar0.DELETE);     
        itembar0.enableCommand(itembar0.FIND);      

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM0: Characteristics - All"));
        itemtbl0.setWrap();

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setDialogColumns(2);

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk1.addField("ITEM1_TECHNICAL_SPEC_NO");
        f.setHidden();
        f.setDbName("TECHNICAL_SPEC_NO");

        f = itemblk1.addField("ITEM1_TECHNICAL_CLASS");
        f.setHidden();
        f.setDbName("TECHNICAL_CLASS");

        f = itemblk1.addField("ITEM1_ATTRIB_NUMBER");
        f.setSize(10);
        f.setDbName("ATTRIB_NUMBER");
        f.setReadOnly(); 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCENUMB: Order");

        f = itemblk1.addField("ITEM1_ATTRIBUTE");
        f.setSize(25);
        f.setReadOnly(); 
        f.setDbName("ATTRIBUTE");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEATTRIBUTE: Attribute");
        f.setMaxLength(20);

        f = itemblk1.addField("ITEM1_DESCRIPTION");
        f.setReadOnly();  
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEDESCR: Description");                                                                  
        f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ATTRIBUTE)");    
        f.setSize(25);    

        f = itemblk1.addField("ITEM1_TECHUNIT");
        f.setSize(25);
        f.setReadOnly();
        f.setFunction("TECHNICAL_ATTRIB_NUMERIC_API.Get_Technical_Unit_(:ITEM1_TECHNICAL_CLASS,:ITEM1_ATTRIBUTE)");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCETECHUNIT: Technical Unit");
        f.setMaxLength(60);

        f = itemblk1.addField("ITEM1_VALUE_NO");
        f.setSize(25);
        f.setInsertable();
        f.setDbName("VALUE_NO");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEVALNO: Value Number");
        f.setMaxLength(25);

        f = itemblk1.addField("ITEM1_LOWER_LIMIT");
        f.setSize(25);
        f.setDbName("LOWER_LIMIT");
        f.setInsertable();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCELOLIM: Lower Limit");
        f.setMaxLength(25);

        f = itemblk1.addField("ITEM1_UPPER_LIMIT");
        f.setSize(25);
        f.setDbName("UPPER_LIMIT");
        f.setInsertable();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEUPLIM: Upper Limit");
        f.setMaxLength(25);

        f = itemblk1.addField("ITEM1_INFO");
        f.setSize(25);
        f.setInsertable();
        f.setDbName("INFO");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEINFO: Info");
        f.setMaxLength(2000);

        f = itemblk1.addField("ALT_VALUE_NO");
        f.setSize(25);
        f.setReadOnly();
        f.setHidden();

        f = itemblk1.addField("ALT_UNIT");
        f.setSize(25);
        f.setReadOnly();
        f.setHidden();

        itemblk1.setView("TECHNICAL_SPEC_NUMERIC");
        itemblk1.defineCommand("TECHNICAL_SPEC_NUMERIC_API","New__,Modify__,Remove__");
        itemblk1.setMasterBlock(headblk);
        itemblk1.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM1: Characteristics - Numeric"));
        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);
        itembar1.setBorderLines(false,true);
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1"); 
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM11"); 
        itembar1.disableCommand(itembar1.DUPLICATEROW);
        itembar1.disableCommand(itembar1.NEWROW);      
        itembar1.enableCommand(itembar1.FIND);
        itembar1.disableCommand(itembar1.DELETE);     

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM1: Characteristics - Numeric"));
        itemtbl1.setWrap();

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
        itemlay1.setDialogColumns(2);

        itemblk2 = mgr.newASPBlock("ITEM2");

        f = itemblk2.addField("ITEM2_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk2.addField("ITEM2_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk2.addField("ITEM2_TECHNICAL_SPEC_NO");
        f.setHidden();
        f.setDbName("TECHNICAL_SPEC_NO");

        f = itemblk2.addField("ITEM2_TECHNICAL_CLASS");
        f.setHidden();
        f.setDbName("TECHNICAL_CLASS");

        f = itemblk2.addField("ITEM2_ATTRIB_NUMBER");
        f.setSize(10);
        f.setDbName("ATTRIB_NUMBER");
        f.setReadOnly(); 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCENUMB: Order");

        f = itemblk2.addField("ITEM2_ATTRIBUTE");
        f.setSize(25);
        f.setReadOnly(); 
        f.setDbName("ATTRIBUTE");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEATTRIBUTE: Attribute");
        f.setMaxLength(20);

        f = itemblk2.addField("ITEM2_DESCRIPTION");
        f.setReadOnly();  
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEDESCR: Description");                                                                  
        f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ITEM2_ATTRIBUTE)");    
        f.setSize(25);    

        f = itemblk2.addField("ITEM2_VALUE_TEXT");
        f.setInsertable();
        f.setDynamicLOV("TECHNICAL_ATTRIB_TEXT","ITEM2_TECHNICAL_CLASS TECHNICAL_CLASS,ITEM2_ATTRIBUTE ATTRIBUTE",600,450);      
        f.setDbName("VALUE_TEXT");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEVALTXT: Value Text"); 
        f.setSize(20);
        f.setMaxLength(20);

        f = itemblk2.addField("ITEM2_INFO");
        f.setSize(25);
        f.setDbName("INFO");
        f.setInsertable();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEINFO: Info");
        f.setMaxLength(2000);

        itemblk2.setView("TECHNICAL_SPEC_ALPHANUM");
        itemblk2.defineCommand("TECHNICAL_SPEC_ALPHANUM_API","New__,Modify__,Remove__");
        itemblk2.setMasterBlock(headblk);
        itemblk2.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM2: Characteristics - Alpha"));
        itemset2 = itemblk2.getASPRowSet();

        itembar2 = mgr.newASPCommandBar(itemblk2);
        itembar2.setBorderLines(false,true);
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2"); 
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM22"); 
        itembar2.disableCommand(itembar2.DUPLICATEROW);
        itembar2.disableCommand(itembar2.NEWROW);      
        itembar2.enableCommand(itembar2.FIND);
        itembar2.disableCommand(itembar2.DELETE);     

        itemtbl2 = mgr.newASPTable(itemblk2);
        itemtbl2.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM2: Characteristics - Alpha"));
        itemtbl2.setWrap();

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
        itemlay2.setDialogColumns(2);

        //BOTH attributes with GROUP values


        itemblk3 = mgr.newASPBlock("ITEM3");

        f = itemblk3.addField("ITEM3_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk3.addField("ITEM3_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk3.addField("ITEM3_TECHNICAL_SPEC_NO");
        f.setHidden();
        f.setDbName("TECHNICAL_SPEC_NO");

        f = itemblk3.addField("ITEM3_TECHNICAL_CLASS");
        f.setHidden();
        f.setDbName("TECHNICAL_CLASS");

        f = itemblk3.addField("ITEM3_ATTRIB_NUMBER");
        f.setSize(10);
        f.setReadOnly(); 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCENUMB: Order");
        f.setDbName("ATTRIB_NUMBER");

        f = itemblk3.addField("ITEM3_ATTRIBUTE");
        f.setSize(25);
        f.setReadOnly(); 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEATTRIBUTE: Attribute");
        f.setMaxLength(20);
        f.setDbName("ATTRIBUTE");

        f = itemblk3.addField("ITEM3_DESCRIPTION");
        f.setReadOnly();  
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEDESCR: Description");                                                                  
        f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ATTRIBUTE)");    
        f.setSize(25);    

        f = itemblk3.addField("ITEM3_VALUE_TEXT");
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEVALTXT: Value Text"); 
        f.setSize(20);
        f.setDbName("VALUE_TEXT");
        f.setMaxLength(20);

        f = itemblk3.addField("ITEM3_TECHUNIT");
        f.setSize(25);
        f.setReadOnly();
        f.setFunction("TECHNICAL_ATTRIB_NUMERIC_API.Get_Technical_Unit_(:ITEM3_TECHNICAL_CLASS,:ITEM3_ATTRIBUTE)");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCETECHUNIT: Technical Unit");
        f.setMaxLength(60);
        f.setDbName("TECHUNIT");

        f = itemblk3.addField("ITEM3_VALUE_NO");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEVALNO: Value Number");
        f.setMaxLength(25);
        f.setDbName("VALUE_NO");

        f = itemblk3.addField("ITEM3_LOWER_LIMIT");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCELOLIM: Lower Limit");
        f.setMaxLength(25);
        f.setDbName("LOWER_LIMIT");

        f = itemblk3.addField("ITEM3_UPPER_LIMIT");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEUPLIM: Upper Limit");
        f.setMaxLength(25);
        f.setDbName("UPPER_LIMIT");

        f = itemblk3.addField("ITEM3_INFO");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEINFO: Info");
        f.setMaxLength(2000);
        f.setDbName("INFO");

        f = itemblk3.addField("GROUP_NAME");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("YGROUPNAME: Grp name");
        f.setHidden();

        itemblk3.setView("TECHNICAL_SPEC_GRP_BOTH");
        itemblk3.setMasterBlock(headblk);
        itemblk3.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM0: Characteristics - All"));
        itemset3 = itemblk3.getASPRowSet();

        itembar3 = mgr.newASPCommandBar(itemblk3);
        itembar3.setBorderLines(false,true);
        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3"); 
        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM33"); 
        itembar3.disableCommand(itembar3.DUPLICATEROW);
        itembar3.disableCommand(itembar3.EDITROW);     
        itembar3.disableCommand(itembar3.NEWROW);      
        itembar3.disableCommand(itembar3.DELETE);     
        itembar3.enableCommand(itembar3.FIND);      

        itemtbl3 = mgr.newASPTable(itemblk3);
        itemtbl3.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM0: Characteristics - All"));
        itemtbl3.setWrap();

        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
        itemlay3.setDialogColumns(2);
        
        
        //Use for Numeric attributes with GROUP values

        itemblk4 = mgr.newASPBlock("ITEM4");

        f = itemblk4.addField("ITEM4_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk4.addField("ITEM4_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk4.addField("ITEM4_TECHNICAL_SPEC_NO");
        f.setHidden();
        f.setDbName("TECHNICAL_SPEC_NO");

        f = itemblk4.addField("ITEM4_TECHNICAL_CLASS");
        f.setHidden();
        f.setDbName("TECHNICAL_CLASS");

        f = itemblk4.addField("ITEM4_ATTRIB_NUMBER");
        f.setSize(10);
        f.setDbName("ATTRIB_NUMBER");
        f.setReadOnly(); 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCENUMB: Order");

        f = itemblk4.addField("ITEM4_ATTRIBUTE");
        f.setSize(25);
        f.setReadOnly(); 
        f.setDbName("ATTRIBUTE");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEATTRIBUTE: Attribute");
        f.setMaxLength(20);

        f = itemblk4.addField("ITEM4_DESCRIPTION");
        f.setReadOnly();  
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEDESCR: Description");                                                                  
        f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ITEM4_ATTRIBUTE)");    
        f.setSize(25);    

        f = itemblk4.addField("ITEM4_TECHUNIT");
        f.setSize(25);
        f.setReadOnly();
        f.setFunction("TECHNICAL_ATTRIB_NUMERIC_API.Get_Technical_Unit_(:ITEM4_TECHNICAL_CLASS,:ITEM4_ATTRIBUTE)");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCETECHUNIT: Technical Unit");
        f.setMaxLength(60);

        f = itemblk4.addField("ITEM4_VALUE_NO");
        f.setSize(25);
        f.setInsertable();
        f.setDbName("VALUE_NO");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEVALNO: Value Number");
        f.setMaxLength(25);

        f = itemblk4.addField("ITEM4_LOWER_LIMIT");
        f.setSize(25);
        f.setDbName("LOWER_LIMIT");
        f.setInsertable();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCELOLIM: Lower Limit");
        f.setMaxLength(25);

        f = itemblk4.addField("ITEM4_UPPER_LIMIT");
        f.setSize(25);
        f.setDbName("UPPER_LIMIT");
        f.setInsertable();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEUPLIM: Upper Limit");
        f.setMaxLength(25);

        f = itemblk4.addField("ITEM4_INFO");
        f.setSize(25);
        f.setInsertable();
        f.setDbName("INFO");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEINFO: Info");
        f.setMaxLength(2000);

        f = itemblk4.addField("ITEM4_ALT_VALUE_NO");
        f.setSize(25);
        f.setReadOnly();
        f.setHidden();
        f.setDbName("ALT_VALUE_NO");

        f = itemblk4.addField("ITEM4_ALT_UNIT");
        f.setSize(25);
        f.setReadOnly();
        f.setHidden();
        f.setDbName("ALT_UNIT");

        f = itemblk4.addField("ITEM4_GROUP_NAME");
        f.setSize(25);
        f.setReadOnly();
        f.setDbName("GROUP_NAME");
        f.setHidden();

        itemblk4.setView("TECHNICAL_SPEC_GRP_NUM");
        itemblk4.defineCommand("TECHNICAL_SPEC_NUMERIC_API","New__,Modify__,Remove__");
        itemblk4.setMasterBlock(headblk);
        itemblk4.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM1: Characteristics - Numeric"));
        itemset4 = itemblk4.getASPRowSet();

        itembar4 = mgr.newASPCommandBar(itemblk4);
        itembar4.setBorderLines(false,true);
        itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4"); 
        itembar4.defineCommand(itembar4.OKFIND,"okFindITEM44"); 
        itembar4.disableCommand(itembar4.DUPLICATEROW);
        itembar4.disableCommand(itembar4.NEWROW);      
        itembar4.enableCommand(itembar4.FIND);
        itembar4.disableCommand(itembar4.DELETE);     

        itemtbl4 = mgr.newASPTable(itemblk4);
        itemtbl4.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM1: Characteristics - Numeric"));
        itemtbl4.setWrap();

        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
        itemlay4.setDialogColumns(2);
        
        //for Alpha Numeric attributes with GROUP values

        itemblk5 = mgr.newASPBlock("ITEM5");

        f = itemblk5.addField("ITEM5_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk5.addField("ITEM5_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk5.addField("ITEM5_TECHNICAL_SPEC_NO");
        f.setHidden();
        f.setDbName("TECHNICAL_SPEC_NO");

        f = itemblk5.addField("ITEM5_TECHNICAL_CLASS");
        f.setHidden();
        f.setDbName("TECHNICAL_CLASS");

        f = itemblk5.addField("ITEM5_ATTRIB_NUMBER");
        f.setSize(10);
        f.setDbName("ATTRIB_NUMBER");
        f.setReadOnly(); 
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCENUMB: Order");

        f = itemblk5.addField("ITEM5_ATTRIBUTE");
        f.setSize(25);
        f.setReadOnly(); 
        f.setDbName("ATTRIBUTE");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEATTRIBUTE: Attribute");
        f.setMaxLength(20);

        f = itemblk5.addField("ITEM5_DESCRIPTION");
        f.setReadOnly();  
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEDESCR: Description");                                                                  
        f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ITEM5_ATTRIBUTE)");    
        f.setSize(25);    

        f = itemblk5.addField("ITEM5_VALUE_TEXT");
        f.setInsertable();
        f.setDynamicLOV("TECHNICAL_ATTRIB_TEXT","ITEM5_TECHNICAL_CLASS TECHNICAL_CLASS,ITEM5_ATTRIBUTE ATTRIBUTE",600,450);      
        f.setDbName("VALUE_TEXT");
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEVALTXT: Value Text"); 
        f.setMaxLength(20);
        f.setSize(20);

        f = itemblk5.addField("ITEM5_INFO");
        f.setSize(25);
        f.setDbName("INFO");
        f.setInsertable();
        f.setLabel("APPSRWTECHNICALOBJECTREFERENCEINFO: Info");
        f.setMaxLength(2000);


        f = itemblk5.addField("ITEN5_GROUP_NAME");
        f.setSize(25);
        f.setReadOnly();
        f.setDbName("GROUP_NAME");
        f.setHidden();

        itemblk5.setView("TECH_SPEC_GRP_ALPHANUM");
        itemblk5.defineCommand("TECHNICAL_SPEC_ALPHANUM_API","New__,Modify__,Remove__");
        itemblk5.setMasterBlock(headblk);
        itemblk5.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM2: Characteristics - Alpha"));
        itemset5 = itemblk5.getASPRowSet();

        itembar5 = mgr.newASPCommandBar(itemblk5);
        itembar5.setBorderLines(false,true);
        itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5"); 
        itembar5.defineCommand(itembar5.OKFIND,"okFindITEM55"); 
        itembar5.disableCommand(itembar5.DUPLICATEROW);
        itembar5.disableCommand(itembar5.NEWROW);      
        itembar5.enableCommand(itembar5.FIND);
        itembar5.disableCommand(itembar5.DELETE);     

        itemtbl5 = mgr.newASPTable(itemblk5);
        itemtbl5.setTitle(mgr.translate("APPSRWTECHNICALOBJECTREFERENCEITM2: Characteristics - Alpha"));
        itemtbl5.setWrap();

        itemlay5 = itemblk5.getASPBlockLayout();
        itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
        itemlay5.setDialogColumns(2);
        //end GRP ALFA NUM
    }

    public void  adjust()
    {
        ASPManager mgr = getASPManager();
        String tech_class = "";    

        if (headset.countRows() >0)
        {
            lu_name = headset.getRow().getValue("LU_NAME"); 
            key_ref = headset.getRow().getValue("KEY_REF");
            tech_class = headset.getRow().getValue("TECHNICAL_CLASS");
        }
        if (!mgr.isEmpty(group))
        {

            if (!mgr.isEmpty(mode) && headset.countRows() >0 && !mgr.isEmpty(tech_class))
            {

                if ( "1".equals(mode) && "TRUE".equals(buttonPressed) )   //Bug Id:31642
                {

                    if (!itemlay4.isEditLayout())
                        itemlay4.setLayoutMode(itemlay4.SINGLE_LAYOUT);
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);          //Bug Id:31642
                    buttonPressed = "FALSE";
                }

                if ( "2".equals(mode) && "TRUE".equals(buttonPressed))    //Bug Id:31642
                {

                    if (!itemlay5.isEditLayout())
                        itemlay5.setLayoutMode(itemlay5.SINGLE_LAYOUT);
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);          //Bug Id:31642
                    buttonPressed = "FALSE";
                }

                if ( "3".equals(mode) )
                {

                    if (!mgr.isEmpty(group))
                    {

                        if (itemlay3.isEditLayout())
                            itemlay3.setLayoutMode(itemlay3.SINGLE_LAYOUT);
                        if ( "TRUE".equals(buttonPressed))
                            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                        buttonPressed = "FALSE";

                    } else
                    {

                        if (itemlay2.isEditLayout())
                            itemlay2.setLayoutMode(itemlay2.SINGLE_LAYOUT);
                        if ( "TRUE".equals(buttonPressed))
                            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                        buttonPressed = "FALSE";
                    }
                }
            }
        } else
        {

            if (!mgr.isEmpty(mode) && headset.countRows() >0 && !mgr.isEmpty(tech_class))
            {

                if ( "1".equals(mode) && "TRUE".equals(buttonPressed) )   //Bug Id:31642
                {
                    if (!itemlay1.isEditLayout())
                        itemlay1.setLayoutMode(itemlay1.SINGLE_LAYOUT);
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);          //Bug Id:31642
                    buttonPressed = "FALSE";
                }

                if ( "2".equals(mode) && "TRUE".equals(buttonPressed))    //Bug Id:31642
                {
                    if (!itemlay2.isEditLayout())
                        itemlay2.setLayoutMode(itemlay2.SINGLE_LAYOUT);
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);          //Bug Id:31642
                    buttonPressed = "FALSE";
                }

                if ( "3".equals(mode) )
                {
                    if (itemlay0.isEditLayout())
                        itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
                    if ("TRUE".equals(buttonPressed))
                        headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                    buttonPressed = "FALSE";

                }

            }
        }
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "APPSRWTECHNICALOBJECTREFERENCETITLE2: Demands";
    }

    protected String getTitle()
    {
        return "APPSRWTECHNICALOBJECTREFERENCETITLE11: Technical Data";
    }

    protected AutoString getContents() throws FndException
    { 
        AutoString out = getOutputStream();
        out.clear();
        ASPManager mgr = getASPManager();
        out.append("<html>\n");
        out.append("<head>\n");

        if ("FunctionalObject".equals(form_name))
        {
            out.append(mgr.generateHeadTag("APPSRWTECHNICALOBJECTREFERENCETITLEDEMA: Demands "));

        } else if ("DocumentManagement".equals(form_name))
        {
            out.append(mgr.generateHeadTag("APPSRWTECHNICALOBJECTREFERENCETITLE1: Characteristics"));
        } else
        {
            out.append(mgr.generateHeadTag("APPSRWTECHNICALOBJECTREFERENCETITLE11: Technical Data"));
        }
        out.append("<title></title>\n");
        out.append("</head>\n");
        out.append("<body ");
        out.append(mgr.generateBodyTag());
        out.append(">\n");
        out.append("<form ");
        out.append(mgr.generateFormTag());
        out.append(">\n");

        if (  "FunctionalObject".equals(form_name) )
        {
            out.append(mgr.startPresentation("APPSRWTECHNICALOBJECTREFERENCETITLE2: Demands"));

        } else if (  "DocumentManagement".equals(form_name) )
        {
            out.append(mgr.startPresentation("APPSRWTECHNICALOBJECTREFERENCETITLE1: Characteristics"));
        } else
        {
            out.append(mgr.startPresentation("APPSRWTECHNICALOBJECTREFERENCETITLE3: Technical Data"));
        }
        out.append(headlay.show());
        out.append(fmt.drawHidden("BUTTONMODE",mode));
        out.append(fmt.drawHidden("GROUPNAME",""));

        if (headset.countRows()>0 && headlay.isSingleLayout())
        {
            String sListValues = headset.getRow().getValue("GRPSTRING");                                   
            if (!mgr.isEmpty(sListValues) )
            {
                String [] values = split(sListValues ,"^");

                
                out.append("        &nbsp;\n");
                out.append(fmt.drawWriteLabel("APPSRWTECHNICALOBJECTREFERENCEGROUPNAME: Group Names"));
                out.append("        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
                out.append("        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
                out.append(fmt.drawSelectStart("NAME","onChange=\"javascript:setGroup()\""));
                out.append(fmt.drawSelectOption("", "", false));
                for (int i = 0; (i < values.length) && (!mgr.isEmpty(values[i])) ; i++)
                {
                    if (values[i].equals(group) )
                        out.append(fmt.drawSelectOption(group, group, true));
                    else
                        out.append(fmt.drawSelectOption(values[i], values[i], false));
                }

                out.append(fmt.drawSelectEnd(false));
            }
            else
            {
                out.append("        &nbsp;\n");
                out.append(fmt.drawWriteLabel("APPSRWTECHNICALOBJECTREFERENCEGROUPNAME: Group Names"));
                out.append("        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
                out.append("        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
                out.append(fmt.drawSelectStart("NAME","onChange=\"javascript:setGroup()\""));
                out.append(fmt.drawSelectOption("", "", true));
                out.append(fmt.drawSelectEnd(false));
            }


            out.append("        <br><br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
            out.append("        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
            out.append("        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
            out.append("        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n");
            out.append(fmt.drawButton("Num","APPSRWTECHNICALOBJECTREFERENCENUMR: Numeric","onClick='setButtonValue(1)'"));
            out.append("&nbsp;\n");
            out.append(fmt.drawButton("Alpha","APPSRWTECHNICALOBJECTREFERENCEALPHA:  Alpha ","onClick='setButtonValue(2)'"));
            out.append("&nbsp;\n");
            out.append(fmt.drawButton("Both","APPSRWTECHNICALOBJECTREFERENCEBOTH:    All   ","onClick='setButtonValue(3)'"));
            out.append("&nbsp;\n");
            out.append("        <br><br>\n");

            if (!mgr.isEmpty(group))
            {


                if ( itemlay4.isVisible()  &&   "1".equals(mode) )
                {
                    out.append(itemlay4.show());
                }

                if ( itemlay5.isVisible()  &&   "2".equals(mode) )
                {
                    out.append(itemlay5.show());
                }

                if ( itemlay3.isVisible()  &&   "3".equals(mode) )
                {
                    out.append(itemlay3.show());
                }

            } else
            {
                if ( itemlay1.isVisible()  &&   "1".equals(mode) )
                {

                    out.append(itemlay1.show());
                }

                if ( itemlay2.isVisible()  &&   "2".equals(mode) )
                {
                    out.append(itemlay2.show());
                }

                if ( itemlay0.isVisible()  &&   "3".equals(mode) )
                {
                    out.append(itemlay0.show());
                }
            }
        }
        appendDirtyJavaScript("window.name = \"CHARACTERISTICS\"; \n");
        appendDirtyJavaScript("NEVER_EXPIRE = false;\n");

        appendDirtyJavaScript("function setButtonValue(mode)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript(" f.BUTTONMODE.value = mode;\n");
        appendDirtyJavaScript(" refreshData();");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function setGroup()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript(" f.GROUPNAME.value = getValue_('NAME',-1);\n");
        appendDirtyJavaScript(" refreshData();");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function refreshData()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("var jsGroup = getValue_('NAME',-1);\n");
        appendDirtyJavaScript("var jsButtonMode = getValue_('BUTTONMODE',-1);\n");
        appendDirtyJavaScript("window.open(\"TechnicalObjectReference.page?LU_NAME=\"+URLClientEncode('");
 
        appendDirtyJavaScript(lu_name);
        appendDirtyJavaScript("')+\"&KEY_REF=\"+URLClientEncode('");
        appendDirtyJavaScript(key_ref);
        appendDirtyJavaScript("')+\"&FORM_NAME=\"+URLClientEncode('");
        appendDirtyJavaScript(form_name);
        appendDirtyJavaScript("')+\"&MODE=\"+jsButtonMode+\"&GROUPNAME=\"+jsGroup,\"CHARACTERISTICS\",\"\");\n");
        appendDirtyJavaScript("}\n");

        out.append(mgr.endPresentation());
        out.append("</form>\n");
        out.append("</body>\n");
        out.append("</html>");
        return out;
    }

}
