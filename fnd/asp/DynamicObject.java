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
 * File        : DynamicObject.java
 * Description : Represents a DynamicObject that is stored in the  DynamicObject
 *               cache. Dynamic objects such as js files and business graphics are 
 *               generated by the framework. The object is stored as a byte array
 *               along with it's mime type. 
 * Notes       :
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.2  2005/11/07 08:16:33  mapelk
 * Introduced "persistant" att to Dynamic Objects and remove non persistent objects from the DynamicObjectCache in the first get.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/04/07 13:53:23  riralk
 * Changes for cluster support in web components and other improvements.
 *
 * Revision 1.2  2005/04/01 13:59:57  riralk
 * Moved Dynamic object cache to session - support for clustering
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.1  2004/12/15 11:13:00  riralk
 * Support for clustered environments by caching business graphics and generated javascript files in memory
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;


public class DynamicObject implements java.io.Serializable {    
    
    private Object data;   
    private String mime;    
    private long timestamp;   
    private boolean use_streamer; //indicates that data contains call back class name and key to stream back data
    private boolean persistent;
   
    /*
     * Constructor for a dynamic object which is stored in the DynamicObjectCache. The
     * field 'data' contains a String for text data or byte[] for binary data which will 
     * be streamed to the client by the RequestHandler servlet.
     * When use_streamer is true 'data' will contain the String "class_name^key". This will 
     * be used to find the class which implements ObjectStreamer which will then stream the 
     * actual data to the client.
     */    
    DynamicObject(Object data, String mime, boolean use_streamer){
       this(data,mime,use_streamer,false);
    }   
    
    DynamicObject(Object data, String mime, boolean use_streamer, boolean persistent){
       this.data = data;  
       this.timestamp = System.currentTimeMillis();
       this.mime = mime;
       this.use_streamer = use_streamer;
       this.persistent = persistent;
    }
    
    public Object getData() //called by RequestHandler
    {
        timestamp = System.currentTimeMillis();
        return data;     
    }
    
    public String getMime()  //called by RequestHandler
    {        
       return mime; 
    }    
    
    public boolean useStreamer()  //called by RequestHandler
    {        
       return use_streamer; 
    }  
    
    long getLastAccessTime()
    {
        return timestamp;
    }
    
    boolean isPersistent()
    {
       return persistent;
    }
    
    public String toString()
    {
       return "[" + (persistent?"Persistent":"") + "] - " +  mime;
    }
}