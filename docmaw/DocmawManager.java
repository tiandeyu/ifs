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
 *  File        : DocmawManager.java
 *  Description :
 *
 *  History
 *
 *  Date        Sign    Descripiton
 *  ----        ----    -----------
 *
 *  2003-10-20  DIKALK  Overrode callError in ASPManager to handle Oracle errors differently
 *  2005-10-18  MDAHSE  Two years later a disgruntled programmer had to add yet another
 *                      workaround here... This time to be able to access the response object from
 *                      the new DocFileFetcher page. Call ID 126700.
 *  2006-05-30  BAKALK  bug 58326,Removing 2nd ftp.
 *
 */



package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

import javax.servlet.http.*;

/**
 *
 *
 */
public class DocmawManager  extends ASPManager
{

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocmawManager");

   public boolean format_error_page;
   public boolean from_buffer;
   public boolean throw_exception;
   public Throwable call_mts_exception;

   private HttpServletResponse my_response;

   public DocmawManager()
   {
      debug("Constructing DocmawManager...");
   }


   /**
    *  Prints out debug messages
    */
   private void debug(String debug_message)
   {
      if (DEBUG)
      {
         Util.debug(getClass().getName() + ": " + debug_message);
      }
   }


   /**
    * Invoked by ASPManager.callMTS() when an Oracle exception occurs. This method overrides
    * callError in ASPManager, in order to handle the Oracle exception differently. A runtime
    * exception containing the oracle error is raised.
    *
    */
   protected void callError(Throwable any, boolean format_error_page, boolean from_buffer, boolean throw_exception)
   {
      this.format_error_page = format_error_page;
      this.from_buffer = from_buffer;
      this.throw_exception = throw_exception;
      call_mts_exception = any;

      String error_message = any.getMessage();

      // Ideally, this runtime exception should have been more
      // specific, like ifs.docmaw.OracleException, so that it
      // is eaiser to catch and handle the error more correctly.
      debug("Throwing RuntimeException from callError :" + error_message);
      throw new RuntimeException(error_message);
   }

   // 2005-10-20, MDAHSE: TODO - Change the following two methods when
   // FNDWEB has included a way for us to get hold of the response
   // object. They have already done it for package 18 but it is not
   // in package 16 yet so we cannot use it in B&T.
   
   /*
    * 2006-01-18, SUKMLK: ASPManager has now defined asp_response as a protected field,
    * which means that we can access it from this extended class, hense this over-ridden
    * method is not needed anymore.
    */
   
   /*
   public void OnStartPage( HttpServletRequest  sreq,
                            HttpServletResponse sresp,
                            HttpServlet         servlet,
                            String              cfgpath,
                            String              path,
                            String              portlet_name,
                            int                 portlet_state)
   {
      // Get hold of the preciousssss response object
      my_response = sresp;
      super.OnStartPage( sreq, sresp, servlet, cfgpath, path, portlet_name, portlet_state);
   }*/

   protected HttpServletResponse getPublicAspResponse()
   {
      return asp_response; // Return ASPManagers asp_response object.
   }

   //bug 58326, starts....
   protected HttpServletRequest getPublicAspRequest()
   {
      return asp_request; // Return ASPManagers asp_response object.
   }
   //bug 58326, ends....

}
