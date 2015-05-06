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
 * File        : FndwebSessionListener.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Rifki R  2006-Aug-01 - Created. Bug Id 59776, Better solution for storing context cache
 * ----------------------------------------------------------------------------
 * 2008/08/01 rahelk Bug id 74809, Added Tiff image cache cleaning
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import javax.servlet.*;
import javax.servlet.http.*;
import ifs.fnd.image.TiffViewerCache;

public class FndwebSessionListener implements HttpSessionListener{
    
    
    public void sessionCreated(HttpSessionEvent e) {
        
    }
    
    public void sessionDestroyed(HttpSessionEvent e) {
        String sesion_id = e.getSession().getId();
        ASPContextCache.clearCacheOnSessionId(sesion_id);
        UserDataCache.clearCacheOnSessionId(sesion_id);
        TiffViewerCache.clearSessionCache(sesion_id);
    }
    
}
