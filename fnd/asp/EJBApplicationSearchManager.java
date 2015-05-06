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
 * File        : EJBApplicationSearchManager.java
 * Description : Handling Application search.
 * Notes       :
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2009/03/25 prralk Bug 81205, Fresh IID 80049 - Application search modifications
 * 2008/04/09 sadhlk Bug id 67895, Corrected View name for Application search.
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.base.*;
import ifs.application.applicationsearch.*;

import java.rmi.RemoteException;

public class EJBApplicationSearchManager extends ApplicationSearchManager
{
   final static boolean DEBUG  = Util.isDebugEnabled("ifs.fnd.asp.EJBApplicationSearchManager");   
   
   EJBApplicationSearchManager()
   {
   }
   
   
   SearchDomainArray listAvailableSearchDomains__(String user_id, ASPConfig cfg) throws FndException,
                                                                           ApplicationException,IfsException 
   {
      if(DEBUG) debug("EJBApplicationSearchManager.listAvailableSearchDomains: user_id:"+user_id);

      try
      {
         SearchDomainArray sdarr = null;      

         ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.APPLICATIONSEARCH, cfg );
         FndContext ctx = FndContext.getCurrentContext();
         ctx.setRunAs(user_id.toUpperCase());

         ApplicationSearchRemote list_ejb = (ApplicationSearchRemote)con.invoke(null);
         sdarr = (SearchDomainArray)list_ejb.applicationSearch_ListAvailableSearchDomains(ctx).getResult();

         //sdarr = (SearchDomainArray)con.invoke(null);
         con.release();
         if(DEBUG) debug("EJBApplicationSearchManager.listAvailableSearchDomains(): result:");      

         return sdarr;
      }
      catch(RemoteException x)
      {
         throw new FndException(x);
      }
   }
   
   String convertSearchCriteria__(String searchString, ASPConfig cfg) throws FndException,
                                                                           ApplicationException,IfsException 
   {
      if(DEBUG) debug("EJBApplicationSearchManager.convertSearchCriteria: searchString:"+searchString);
      
      try
      {
         ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.PASSWORD_CHECK, cfg );
         FndContext ctx = FndContext.getCurrentContext();
         //ctx.setRunAs(user_id.toUpperCase());

         ApplicationSearchViews.V1 param = new ApplicationSearchViews.V1();
         param.criteria.setValue(searchString);

         ApplicationSearchRemote convert_ejb = (ApplicationSearchRemote)con.invoke(null);
         param = (ApplicationSearchViews.V1)convert_ejb.applicationSearch_ConvertSearchCriteria(param, ctx).getResult();
         
         con.release();

         if(DEBUG) debug("EJBApplicationSearchManager.convertSearchCriteria()");      

         return param.result.getValue(); 
      }
      catch(RemoteException x)
      {
         throw new FndException(x);
      }

   }

   String getSnippet__(String user_id, String searchDomain, String primaryKey, String criteria, ASPConfig cfg) throws FndException, ApplicationException, IfsException 
   {

      if(DEBUG) debug("EJBApplicationSearchManager.getSnippet: user_id="+user_id+" searchDomain="+searchDomain+" primaryKey="+primaryKey+" criteria="+criteria);

      try
      {
         ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.APPLICATIONSEARCH, cfg );
         FndContext ctx = FndContext.getCurrentContext();
         ctx.setRunAs(user_id.toUpperCase());

         ApplicationSearchViews.V4 param = new ApplicationSearchViews.V4();

         param.searchDomain.setValue(searchDomain);
         param.primaryKey.setValue(primaryKey);
         param.criteria.setValue(criteria);
         
         ApplicationSearchRemote snippet_ejb = (ApplicationSearchRemote)con.invoke(null);
         param = (ApplicationSearchViews.V4)snippet_ejb.applicationSearch_GetSnippet(param, ctx).getResult();
         
         con.release();

         if(DEBUG) debug("EJBApplicationSearchManager.getSnippet()");      

         return param.result.getValue(); 
      }
      catch(RemoteException x)
      {
         throw new FndException(x);
      }
   }
   

   SearchResultArray search__(String user_id, String[] searchDomain, String criteria, boolean returnSnippet, int maxRows, int skipRows, ASPConfig cfg, String sessionId) throws FndException,
                                                                                                                                                                          ApplicationException,
                                                                                                                                                                          IfsException
   {
      if(DEBUG) debug("EJBApplicationSearchManager.search: user_id="+user_id+" searchDomain="+searchDomain+" criteria="+criteria+" maxrows="+maxRows+" skipRows="+skipRows+" sessionId="+sessionId);

      try
      {
         SearchResultArray srarr = null;

         ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.APPLICATIONSEARCH, cfg );
         FndContext ctx = FndContext.getCurrentContext();
         ctx.setRunAs(user_id.toUpperCase());

         ApplicationSearchViews.V7 param = new ApplicationSearchViews.V7();

         param.searchDomains.setStringArray(searchDomain);
         param.criteria.setValue(criteria);
         param.returnSnippet.setValue(returnSnippet);
         param.maxRows.setValue(maxRows);
         param.skipRows.setValue(skipRows);
         
         ApplicationSearchRemote search_ejb = (ApplicationSearchRemote)con.invoke(null);
         param = (ApplicationSearchViews.V7)search_ejb.applicationSearch_Search(param, ctx).getResult();

         con.release();

         srarr = param.result;

         if(DEBUG)  debug("EJBApplicationSearchManager.search(): result:");      

         return srarr;
      }
      catch(RemoteException x)
      {
         throw new FndException(x);
      }
   }
   
   String getIndexedAttributes__(String user_id, String searchDomain, ASPConfig cfg) throws FndException, ApplicationException, IfsException 
   {
      if(DEBUG) debug("EJBApplicationSearchManager.getIndexedAttributes: user_id="+user_id+" searchDomain="+searchDomain);

      try
      {
         ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.APPLICATIONSEARCH, cfg );
         FndContext ctx = FndContext.getCurrentContext();
         ctx.setRunAs(user_id.toUpperCase());

         ApplicationSearchViews.V3 param = new ApplicationSearchViews.V3();

         param.searchDomain.setValue(searchDomain);
         
         ApplicationSearchRemote attributes_ejb = (ApplicationSearchRemote)con.invoke(null);
         param = (ApplicationSearchViews.V3)attributes_ejb.applicationSearch_GetIndexedAttributes(param, ctx).getResult();

         con.release();
         
         return param.result.getValue();
      }
      catch(RemoteException x)
      {
         throw new FndException(x);
      }
   }
   
   private static void debug( String line )
   {
      Util.debug(line);
   }
   
}


