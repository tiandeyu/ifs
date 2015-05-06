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
 *  File        : DocHTTPXMLReceive.java
 *  Description : Used for sending temporary XML from server to IFSCliMgrOCX
 *  Modified    :
 *  2001-10-18  MDAHSE  Converted from servlet to "webkit page" due to problems with HTTPS
 *                      Looked a great deal at PladewHttpServerGateway for tips.
 *  2006-09-19  NIJALK  Bug 58221, Modified run().
 * ----------------------------------------------------------------------------
 */

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.xml.XMLUtil;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DocHTTPXMLReceive extends ASPPageProvider
{
    //===============================================================
    // Static constants
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocHTTPXMLReceive");

    //===============================================================
    // Instances created on page creation (immutable attributes)
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;
    private ASPCommandBar cmdbar;
    private ASPBlock blk;
    private ASPBlockLayout lay;

    //===============================================================
    // Transient temporary variables (never cloned)
    //===============================================================

    //===============================================================
    // Construction
    //===============================================================
    public DocHTTPXMLReceive(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    protected void doReset() throws FndException
    {
        super.doReset();
    }

    public ASPPoolElement clone(Object obj) throws FndException
    {
        DocHTTPXMLReceive page = (DocHTTPXMLReceive)(super.clone(obj));

        // Cloning immutable attributes
        page.ctx    = page.getASPContext();
        page.fmt    = page.getASPHTMLFormatter();
        page.blk    = page.getASPBlock(blk.getName());
        page.cmdbar = page.blk.getASPCommandBar();
        page.lay    = page.blk.getASPBlockLayout();

        return page;
    }

    public void run()
    {
        ASPManager      mgr                    = getASPManager();
        ASPContext      ctx                    = mgr.getASPContext();
        String          xmlFileName;
        String          myXMLString            = "";
        Document        myDocument;
        Element         rootElement;
        File            xmlFile;

        if (DEBUG) debug(this+": run() {");

        try
        {
            if ( "POST".equals( mgr.getRequestMethod() )  &&  ( mgr.getRequestLength() > 0) )
            {

                if (DEBUG) debug(this+":   POST {");

                myDocument = XMLUtil.loadFromInputStream( mgr.getRequestBodyAsInputStream() );

                // Get first root element
                rootElement = myDocument.getDocumentElement();

                //Bug 58221, Start
                xmlFileName = new String(Util.fromBase64Text((String)ctx.getGlobal("DOCMAW_FILE_INFO_XML")));
                //Bug 58221, End

                if ("".equals(xmlFileName))
                {
                    throw new Exception(mgr.translate("DOCMAWDOCHTTPXMLRECEIVEXMLFILENAMEEMPTY: XML file name is empty"));
                }

                if (DEBUG) debug(this+":     XML File name = " + xmlFileName);

                xmlFile = new File(xmlFileName);

                if (!xmlFile.exists())
                {
                    throw new Exception(mgr.translate("DOCMAWDOCHTTPXMLRECEIVECANNOTFINDXMLFILE: Cannot find temporary XML file ") + xmlFileName);
                }

                if (DEBUG) debug(this+":     XML file found");

                myDocument = XMLUtil.loadFromFile(xmlFileName);
                myXMLString = XMLUtil.saveToString(myDocument);

                // Replace CR and LF that make msxml go crazy
                myXMLString = Str.replace(myXMLString,"\r","");
                myXMLString = Str.replace(myXMLString,"\n","");

                if (DEBUG) debug(this+":   myXMLString = " + myXMLString);

                // Remove temporary XML file
                if (!xmlFile.delete())
                {
                    throw new Exception(mgr.translate("DOCMAWDOCHTTPXMLRECEIVECOULDNOTDELETEFILE: Could not delete temporary XML file ") + xmlFileName);
                }
                else
                {
                    if (DEBUG) debug(this+":     Deleted temporary xml file.");
                }

                if (DEBUG) debug(this+":   POST }");

            }
            // Sorry, we only support POSTed test/xml HTTP requests
            else
            {
		// Do not bother translate this. If this error ever reaches the client someone has done something baaad...
                throw new Exception("The class [" + this.getClass().getName() + "] only support a POSTed text/xml request");
            }

        }
        catch( Exception e )
        {
            sendError(Str.getStackTrace(e));
        }

        // Phew! Hopefully everything is ok now. If no exception until now, we can continue.

        // See if myXMLString contains something. If it doesnt, something has gone baaaad...

        if (!mgr.isEmpty(myXMLString))
        {
            mgr.responseWrite(myXMLString);
            mgr.setAspResponsContentType("text/xml");
            mgr.endResponse();
        }
        else
        {
            sendError(mgr.translate("DOCMAWDOCHTTPXMLRECEIVEXMLFILEISEMPTY: XML file found on disk is empty"));
        }
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();
    }

    private void sendError(String errorText)
    {
        ASPManager mgr = getASPManager();

        String errorMessage;
        errorMessage = "Error: " + errorText;

        if (DEBUG) debug(this+": Sending the following error to client:\n" + errorMessage);

        mgr.responseWrite(errorMessage);
        mgr.setAspResponsContentType("text/html");
        mgr.endResponse();
    }

}
