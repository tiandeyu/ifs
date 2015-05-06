
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
 * File        : DatabaseRepository.java
 * Description : Handles file operations when a database table is a repository.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P 2005-03-14 - Created(bakalk: sample code provided by Jacek.)
 *    Bakalk  2005-04-14 - Made modification to meet Docmaw Requirements.
 *    Bakalk  2005-05-13 - changed the key from fileName to doc_class,doc_no,doc_sheet,doc_Rev and doc_type.
 *    MDAHSE  2005-12-20 - Commented all code to avoid breaking the build that the first solution-guys do.
 *    MDAHSE  2005-12-22 - Uncommented the code.
 *    MDAHSE  2006-01-03 - Changed all occurances of EdmBlobStream to EdmFileStorage.
 *    MDAHSE  2006-01-10 - Cleaned up code. First try with new build scripts. Hope it holds...
 *    MDAHSE  2006-01-11 - Changed ejb/docman to ejb/fndweb in lookup.
 *    MDAHSE  2006-01-13 - Added srv.setRunAs() to run the activity as end user through JAP too.
 *                         Moved setUseXA() call to Acivity Handler DatabaseStorageHelper.
 *                         Supress exceptions from JAP, for now. Must be changed later.
 *    MDAHSE  2006-01-17 - Changed exception-handling slightly. Now catches and supresses NullPointerException
 *                         explicitly so that I can catch and throw all other exceptions. Until the bug in the JAP is fixed.
 *                         Must be changed later on. I don't like supressing exceptions...
 *    MDAHSE  2006-02-22 - Removed workaround that catched the NullPointerException from JAP.
 *    BAKALK  2007-04-10 - Added new key file_no and overloaded basic methods.
 *    BAKALK  2007-09-11 - Removing deprecated Methods: Removed ASPManagers getConnectionServer(), instead make Server instance here and set credentials.
 *                         Framework made a new method available for us regarding this: ASPManager::setServerCredentials(Server).
 *    BAKALK  2007-09-12 - Removed calling setServerCredentials(), instesd called Server::setCredentials() with ASPConfig::getConfigUser() and ASPConfigand::getConfigPassword(),
 *                         after framework made those methods in ASPConfig public.
 *    BAKALK  2007-09-14 - Fixed a compilation error.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.docmaw;

import java.util.*;
import java.io.*;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.ap.*;
import ifs.fnd.base.*;
import ifs.fnd.remote.*;
import ifs.fnd.remote.j2ee.*;
import ifs.fnd.record.*;
import ifs.application.documentstorage.*;

import ifs.application.documentfiledata.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import javax.naming.NamingException;
import javax.ejb.CreateException;

import java.security.*;
import javax.security.auth.Subject;

import ifs.fnd.websecurity.SecurityHandler;

public class DatabaseRepository
{

   //==========================================================================
   // Static variables
   //==========================================================================

   public static boolean DEBUG  = Util.isDebugEnabled("ifs.docmaw.DatabaseRepository");

   private static final String RMI_METHOD = "RMI";
   private static final String JAP_METHOD = "JAP";
   
   private String activity_user    = "";

   private String tm_method = null;
   private String url              = null;  // JAP only

   private DocumentStorageRemote ejb;         // RMI


   private Server                     srv;         // JAP
   private ASPManager                 mgr;

   private String                     confUser;
   private String                     confPassword;


   //==========================================================================
   //  Construction and initialization
   //==========================================================================

   /**
    * Private constructor - not to be called from outside.
    */
   private DatabaseRepository()
   {
   }

   /**
    * Initiate the configuration and create an instance of EJB connection
    * for the current instance of this class
    */
   private void init( ASPConfig cfg, ASPManager mgr ) throws NamingException, CreateException, RemoteException, FndException
   {

      tm_method       = cfg.getParameter("ADMIN/TRANSACTION_MANAGER", RMI_METHOD);
      url             = cfg.getParameter("ADMIN/FNDEXT/URL",          "");
      confUser        = cfg.getConfigUser();
      confPassword    = cfg.getConfigPassword();

      this.mgr = mgr;

      activity_user    = getActivityUser();

      if (DEBUG) Util.debug("DatabaseRepository.init METHOD = " + tm_method); 

      // RMI
      if (RMI_METHOD.equals(tm_method))
         try
         {
            ejb = createEJBConnection(); // may take some time
         }
         catch (FndException e)
         {
            if (DEBUG) Util.debug("DatabaseRepository.init - RMI exception: " + Str.getStackTrace(e));
            throw new FndException(mgr.translate("DATABASEREPOSITORYINITCRTEJB: DatabaseRepository.init - RMI exception: ") + Str.getStackTrace(e));            
         }
      // JAP
      else
      {
         srv = new Server();
         srv.setCredentials(confUser,confPassword);
         srv.setConnectionString(url);
      }
      
   }

   //==========================================================================
   //  Static methods to be called from the page
   //==========================================================================

   /**
    * Return an instance of the DatabaseRepository class.
    */
   static synchronized DatabaseRepository getInstance( ASPConfig cfg, ASPManager mgr) throws NamingException, CreateException, RemoteException, FndException
   {
      // A simple solution: a new instance is created for each request.
      // For better performance (creation of EJB connection may take some time)
      // an implementation with a pool of already created instances should be used.
      // A pool is a static list of created instances, but one instance can be
      // used by one thread at time only. New instances are created on demand.
      // See EJBConnectionPool.java

      DatabaseRepository ejb_caller = new DatabaseRepository();

      if (DEBUG) Util.debug("DatabaseRepository.getInstance {");
      
      try
      {
         ejb_caller.init(cfg,mgr);
      }
      catch (FndException e)
      {
         if (DEBUG) Util.debug("DatabaseRepository.getInstance - exception: " + Str.getStackTrace(e));
         throw new FndException(mgr.translate("DATABASEREPOSITORYGETINST: DatabaseRepository.getInstance - exception: ") + Str.getStackTrace(e));
      }
      
      if (DEBUG) Util.debug("DatabaseRepository.getInstance }");
      
      return ejb_caller;

   }

   //==========================================================================
   //  Instance methods to be called from the page
   //==========================================================================

   void uploadFile( String docClass,
                    String docNo,
                    String docSheet,
                    String docRev,
                    String docType,
                    double fileNo,
                    String localFileName ) throws NamingException,
                                                  CreateException,
                                                  ApplicationException,
                                                  IfsException,
                                                  RemoteException,
                                                  FndException
   {
      if( RMI_METHOD.equals(tm_method) )
      {
         DocumentFileData aRecord = new DocumentFileData();

         aRecord.docClass.setValue(docClass);
         aRecord.docNo.setValue(docNo);
         aRecord.docSheet.setValue(docSheet);
         aRecord.docRev.setValue(docRev);
         aRecord.docType.setValue(docType);
         aRecord.fileNo.setValue(fileNo);
         aRecord.localFileName.setValue(localFileName);
         
         try
         {
            SecurityHandler.getConfigSubject();
         }
         catch(FndException e)
         {
            if (DEBUG) Util.debug("DatabaseRepository.uploadFile - SecurityHandler exception: " + Str.getStackTrace(e));
            throw new FndException(mgr.translate("DATABASEREPOSITORYULSECEXC: DatabaseRepository.uploadFile - SecurityHandler exception: ") + Str.getStackTrace(e));
         }
         
         FndContext context = FndContext.getCurrentContext();
         
         context.setRunAs(activity_user);

         FndRecordResultWrapper result = ejb.databaseStorageHelper_writeToDatabase(aRecord,context);

      }
      else
      {
         // JAP here
         executeJAP("writeToDatabase",docClass,docNo,docSheet,docRev,docType,fileNo,localFileName);
      }
   }



   //overload uploadFile
   void uploadFile( String docClass,
                    String docNo,
                    String docSheet,
                    String docRev,
                    String docType,
                    String localFileName ) throws NamingException,
                                                  CreateException,
                                                  ApplicationException,
                                                  IfsException,
                                                  RemoteException,
                                                  FndException
   {
      uploadFile( docClass,docNo,docSheet,docRev,docType,1,localFileName );
   }



   void downloadFile( String docClass,
                      String docNo,
                      String docSheet,
                      String docRev,
                      String docType,
                      double fileNo,
                      String localFileName) throws NamingException,
                                                   CreateException,
                                                   ApplicationException,
                                                   IfsException,
                                                   RemoteException,
                                                   FndException
   {

      if( RMI_METHOD.equals(tm_method) )
      {
         DocumentFileData aRecord = new DocumentFileData();
         
         aRecord.docClass.setValue(docClass);
         aRecord.docNo.setValue(docNo);
         aRecord.docSheet.setValue(docSheet);
         aRecord.docRev.setValue(docRev);
         aRecord.docType.setValue(docType);
         aRecord.fileNo.setValue(fileNo);
         aRecord.localFileName.setValue(localFileName);
         
         if (DEBUG) Util.debug("DatabaseRepository.downloadFile - localFileName = " + localFileName);

         try
         {
            SecurityHandler.getConfigSubject();
         }
         catch(FndException e)
         {
            if (DEBUG) Util.debug("DatabaseRepository.downloadFile - SecurityHandler exception: " + Str.getStackTrace(e));
            throw new FndException(mgr.translate("DATABASEREPOSITORYDLSECEXC: DatabaseRepository.downloadFile - SecurityHandler exception: ") + Str.getStackTrace(e));
         }
         
         FndContext context = FndContext.getCurrentContext();

         if (DEBUG) Util.debug("DatabaseRepository.downloadFile - setRunAs, user = " + activity_user);
         
         context.setRunAs(activity_user);

         if (DEBUG) Util.debug("DatabaseRepository.downloadFile - after setRunAs");
         
         if (DEBUG) Util.debug("DatabaseRepository.downloadFile - before readFromDatabase");

         long start = getTime();
                  
         try
         {
            FndRecordResultWrapper result = ejb.databaseStorageHelper_readFromDatabase(aRecord,context);
         }
         catch(Exception e)
         {
            if (DEBUG) Util.debug("DatabaseRepository.downloadFile - ejb.fileOperation exception: " + Str.getStackTrace(e));
            throw new FndException(e.getMessage());
         }
         
         if (DEBUG) Util.debug("DatabaseRepository.downloadFile - after readFromDatabase. Copy took " + (getTime() - start) + " milliseconds.");

      }
      else
      {
         // JAP here
         executeJAP("readFromDatabase",docClass,docNo,docSheet,docRev,docType,fileNo,localFileName);
      }
   }


   //overload downloadFile
   void downloadFile( String docClass,
                      String docNo,
                      String docSheet,
                      String docRev,
                      String docType,
                      String localFileName) throws NamingException,
                                                   CreateException,
                                                   ApplicationException,
                                                   IfsException,
                                                   RemoteException,
                                                   FndException
   {
      downloadFile( docClass,docNo,docSheet,docRev,docType,1,localFileName);
   }

   void deleteRemoteFile(String docClass,
                         String docNo,
                         String docSheet,
                         String docRev,
                         String docType,
                         double fileNo) throws NamingException,
                                                CreateException,
                                                ApplicationException,
                                                IfsException,
                                                RemoteException,
                                                FndException
   {
      if( RMI_METHOD.equals(tm_method) )
      {
         DocumentFileData aRecord = new DocumentFileData();

         aRecord.docClass.setValue(docClass);
         aRecord.docNo.setValue(docNo);
         aRecord.docSheet.setValue(docSheet);
         aRecord.docRev.setValue(docRev);
         aRecord.docType.setValue(docType);
         aRecord.fileNo.setValue(fileNo);

         try
         {
            SecurityHandler.getConfigSubject();
         }
         catch(FndException e)
         {
            if (DEBUG) Util.debug("DatabaseRepository.deleteRemoteFile - SecurityHandler exception: " + Str.getStackTrace(e));
            throw new FndException(mgr.translate("DATABASEREPOSITORYDELSECEXC: DatabaseRepository.deleteRemoteFile - SecurityHandler exception: ") + Str.getStackTrace(e));
         }
         
         FndContext context = FndContext.getCurrentContext();
         
         context.setRunAs(activity_user);

         FndRecordResultWrapper result = ejb.databaseStorageHelper_removeFromDatabase(aRecord,context);

      }
      else
      {
         // JAP here
         executeJAP("removeFromDatabase",docClass,docNo,docSheet,docRev,docType,fileNo,"");
      }
   }

   //overload deleteRemoteFile

   void deleteRemoteFile(String docClass,
                         String docNo,
                         String docSheet,
                         String docRev,
                         String docType) throws NamingException,
                                                CreateException,
                                                ApplicationException,
                                                IfsException,
                                                RemoteException,
                                                FndException
   {
      deleteRemoteFile(docClass,docNo,docSheet,docRev,docType,1);
   }

   
   private DocumentStorageRemote createEJBConnection() throws NamingException,
                                                                  CreateException,
                                                                  RemoteException,
                                                                  FndException
   {

      if (DEBUG) Util.debug("DatabaseRepository.createEJBConnection {");
         
      Context initial = new InitialContext();

      if (DEBUG) Util.debug("DatabaseRepository.createEJBConnection initial created, doing lookup...");

      // Note: The "path" below matches the one in jboss.xml. This
      // path is renamed using our special install_docmaw.xml that is
      // run during build of all IFS Applications.
      
      Object objref = initial.lookup("ejb/ifs/fndweb/DocumentStorage");
      
      if (DEBUG) Util.debug("DatabaseRepository.createEJBConnection lookup done. Class name = " + objref.getClass().getName());

      try
      {
         SecurityHandler.getConfigSubject();
      }
      catch(FndException e)
      {
         if (DEBUG) Util.debug("DatabaseRepository.createEJBConnection - SecurityHandler exception: " + Str.getStackTrace(e));
         throw new FndException(mgr.translate("DATABASEREPOSITORYCRTEJBCONNSECEXC: DatabaseRepository.createEJBConnection - SecurityHandler exception: ") + Str.getStackTrace(e));
      }

      if (DEBUG) Util.debug("DatabaseRepository.createEJBConnection SecurityHandler called");

      if (DEBUG) Util.debug("DatabaseRepository.createEJBConnection creating home...");
            
      DocumentStorageHome home = (DocumentStorageHome) PortableRemoteObject.narrow(objref, DocumentStorageHome.class);
      
      DocumentStorageRemote storageManager = home.create();

      if (DEBUG) Util.debug("DatabaseRepository.createEJBConnection }");
      
      return storageManager;
   }



   private void executeJAP(String operation,
                           String docClass,
                           String docNo,
                           String docSheet,
                           String docRev,
                           String docType,
                           double fileNo,
                           String localFileName) throws FndException
   {
      byte [] byteValue = null;

      Record requestRecord = new Record("FILE_DETAILS");
      
      requestRecord.add("DOC_CLASS",      docClass,     DataType.ALPHA);
      requestRecord.add("DOC_NO",         docNo,        DataType.ALPHA);
      requestRecord.add("DOC_SHEET",      docSheet,     DataType.ALPHA);
      requestRecord.add("DOC_REV",        docRev,       DataType.ALPHA);
      requestRecord.add("DOC_TYPE",       docType,      DataType.ALPHA);
      requestRecord.add("FILE_NO",        fileNo,       DataType.DECIMAL);
      requestRecord.add("FILE_DATA",      byteValue,    DataType.BINARY);
      requestRecord.add("LOCAL_FILE_NAME",localFileName,DataType.ALPHA);

      try
      {
         srv.setRunAs(activity_user);
         srv.invoke("DatabaseStorageHelper",operation,requestRecord);
      }
      catch(Exception e)
      {
         if (DEBUG) Util.debug("DatabaseRepository.executeJAP - Exception: " + Str.getStackTrace(e));
         throw new FndException(mgr.translate("DATABASEREPOSITORYEXECJAP: DatabaseRepository.executeJAP - exception: ") + Str.getStackTrace(e));
      }
   }

    

   private static long getTime()
   {
      return System.currentTimeMillis();
   }


   private String getActivityUser()
   {
      // Run activity as end user. Document Issue Access checks are
      // done inside activity, so this should be safe. All users
      // (IFSAPP_NORMAL role, probably) needs to be granted the
      // activity.
      
      return mgr.getUserId();
   }
}
