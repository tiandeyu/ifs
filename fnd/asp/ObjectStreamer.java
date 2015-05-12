
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
 * File        : ObjectStreamer.java
 * Description :
 * Notes       : This interface should be implemented by components that wish to
 *               stream arbitrary files back to the used in combination with 
 *               ASPPage.setUrlToDynamicFile()
 * ---------------------------------------------------------------------------- 
 * New Comments:
 *
 */

package ifs.fnd.asp;


import java.io.InputStream;

/* 
 * This interface should be implemented when using ASPPage.setUrlToDynamicFile() to 
 * add links to arbitrary files that maybe be located outside the web application. 
 * Typically the getFile() method will locate the file and return an InputStream which 
 * will then be used by the RequestHandler servlet to stream the file when requested by 
 * the client. Values for url and key will be the same as sent to ASPPage.setUrlToDynamicFile()
 * The parameter key can be used to indicate the actual location of the file that can be used
 * when opening the InputStream from within the implemented getFile() method.
 *
 * Example:
 *
 * public class DemoObjectStreamer implements ObjectStreamer{          
 *  public InputStream getFile(String url, String key) {
 *    try{
 *          return new FileInputStream(key);             
 *     }
 *     catch(FileNotFoundException foe)
 *     {
 *          return null;
 *     }
 *   }    
 *} 
 *
 * @see     ifs.fnd.asp.ASPPage#setUrlToDynamicFile()
 */
public interface ObjectStreamer {
    
    public InputStream getFile(String url, String key);        
    
}
