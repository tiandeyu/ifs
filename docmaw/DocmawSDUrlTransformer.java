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
*  File        : DocmawSDUrlTransformer.java    
*  Modified    :
*      2009-08-13   SHTHLK   Bug Id 84834, Created the page to redirect the search domain information to Document Info
*      2009-08-18   SHTHLK   Bug Id 84834, Modified transferToDocIssue() to modify the view and keys and sent the url
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DocmawSDUrlTransformer extends ASPPageProvider {
    
    //===============================================================
    // Static constants
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocmawSDUrlTransformer");

    //===============================================================
    // Instances created on page creation (immutable attributes)
    //===============================================================

   //===============================================================
   // Construction 
   //===============================================================
   public DocmawSDUrlTransformer(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


    public void run() 
    {
        ASPManager mgr = getASPManager();
	transferToDocIssue();
    }
   

   public void  transferToDocIssue()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("__APP_SEARCH");
      
      if("Y".equals(val) )
      {
         String keys = mgr.readValue("KEYS");
	 String key_trans="";
            
	 String[] main_keys = keys.split(String.valueOf(IfsNames.record_separator));
         for (int k=0;k<main_keys.length;k++)
	 {
            String[] key = main_keys[k].split(String.valueOf(IfsNames.field_separator));
	    if (("doc_class".equals(key[0])) || ("doc_no".equals(key[0])) || ("doc_sheet".equals(key[0])) || ("doc_rev".equals(key[0]))) 
	    {
	       key_trans = key_trans + key[0] + IfsNames.field_separator + key[1] +IfsNames.record_separator;
	    }
	 }
         keys = "&KEYS=" +mgr.URLEncode(key_trans);
	 mgr.redirectTo("DocIssue.page?&__APP_SEARCH=Y&VIEW=DOC_ISSUE_REFERENCE" + keys);
      }
	   
  }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "DOCMAWSDURLTITLE: Search Domain Url Transformer";
   }

   protected String getTitle()
   {
      return "DOCMAWSDURLTITLE: Search Domain Url Transformer";
   }

  
}
