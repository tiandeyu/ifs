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
*  File     : NoteBook.java
*  Modified :
*  amiklk   2010-07-06, Bug 86510, changed run() to use readValue() for encoded parameters.
* 2009-03-09 rahelk Bug 81146, added functionality to show user description and date 
*  sadhlk   2008-09-10  - Bug 76949, F1PR461 - Notes feature in Web client.
*  buhilk   2006-11-20  - Improved code to enhance formatting of notes
*  buhilk   2006-10-26  - Remodeled Retrieving and Saving of client notebook according to access provider (RMI or JAP)
*  buhilk   2006-10-18  - Created
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.application.fnduser.FndUser;
import ifs.application.managenotes.*;
import ifs.application.enumeration.*;
import ifs.application.fndnotebook.*;
import ifs.application.notebook.Note;
import ifs.fnd.ap.*;
import ifs.fnd.base.ApplicationException;
import java.util.regex.Pattern;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.record.*;
import ifs.fnd.*;
import ifs.fnd.util.*;
import java.util.*;

public class NoteBook extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.NoteBook");
   protected static long TIME_OUT = 10*60000; 
   private final static char C30 = (char)30;
   
   //==========================================================================
   // Static variables
   //==========================================================================
   
   private static Hashtable imgtable = new Hashtable();
   private static Random randomGenerator = new Random();
   
   //===============================================================
   // Temporary Variables
   //===============================================================
   private Record cltnb_rec;
   private FndNoteBookArray notebooks;
   private boolean RMI = true;
   
   //===============================================================
   // Notebook Errors
   //===============================================================
   private final String notebook_null_error = "FNDPAGESNOTEBOOKNULLERROR: Could not read specified notebook.";
   private final String notebook_save_error = "FNDPAGESNOTEBOOKSAVEERROR: Could not save specified notebook.";
   
   ASPBlock utilblk; 
   ASPField datefld;
   //===============================================================
   // Construction
   //===============================================================

   public NoteBook(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();
      
      RMI = "RMI".equals(getASPConfig().getParameter("ADMIN/TRANSACTION_MANAGER","RMI"));
      
      mgr.removePageId();

      if( !mgr.isEmpty(mgr.readValue("VALIDATE")) )
         if("Y".equals(mgr.readValue("VALIDATE")) && !mgr.isEmpty(mgr.readValue("__PAGE_NOTES")))
            validate();
   }

   /*
    * Called by the run() method. Depending on the type of request denoted by __PAGE_NOTES
    * this method calls upon the retrieving or saving methods for notes
    */
   public void  validate() 
   {
      ASPManager mgr = getASPManager();
      FndNoteBook cltnb = null;
      String val = mgr.readValue("__PAGE_NOTES");
      
      if (  "GETNOTES".equals(val) )
      {
         try{
            if(RMI){
               cltnb = getClientNoteBook();
               if(cltnb!=null)
                  mgr.responseWrite(splitNotes(cltnb,false));
               else
                  mgr.responseWrite(showError(notebook_null_error));
            }
            else
            {
               cltnb_rec = getClientNoteBookRecord();
               if(cltnb_rec!=null)
                  mgr.responseWrite(splitNotes(cltnb_rec,false));
               else
                  mgr.responseWrite(showError(notebook_null_error));
            }
         }
         catch(Exception e){
            debug(e.getMessage());
            showError(e.getMessage());
         }
      }
      else if (  "COUNTNOTES".equals(val) )
      {
         try{
            if(RMI){
               cltnb = getClientNoteBook();
               if(cltnb!=null)
                  mgr.responseWrite(hasNotes(cltnb));
               else
                  mgr.responseWrite(showError(notebook_null_error));
            }
            else
            {
               cltnb_rec = getClientNoteBookRecord();
               if(cltnb_rec!=null)
                  mgr.responseWrite(hasNotes(cltnb_rec));
               else
                  mgr.responseWrite(showError(notebook_null_error));
            }
         }
         catch(Exception e){
            debug(e.getMessage());
            showError(e.getMessage());
         }
      }
      else if (  "SAVENOTES".equals(val) )
      {
         try
         {
            if(RMI){
               cltnb = saveClientNoteBook();
               if(cltnb!=null)
                  mgr.responseWrite(splitNotes(cltnb,true));
               else
                  mgr.responseWrite(showError(notebook_save_error));                  
            }
            else
            {
               cltnb_rec = saveClientNoteBookRecord();
               if(cltnb_rec!=null)
                  mgr.responseWrite(splitNotes(cltnb_rec,true));
               else
                  mgr.responseWrite(showError(notebook_save_error));                  
            }
            
            clearExpiredImages();
         }
         catch(Exception e)
         {
            debug(e.toString());
            showError(e.getMessage());
         }
      }
      mgr.endResponse();
   }
   
   /*
    * Converts a string in rich text format into html code. Some data may be lost
    * @ param String
    * @ return String
    */
   private String decodeNote(String rtf_note, boolean deleteImages)
   {
      if(rtf_note.indexOf("\\rtf1")==-1)
         return rtf_note;

      Rtf2Html rtf_formatter = new Rtf2Html(true, getASPConfig().getImagesLocation(), deleteImages, this);
      String body = rtf_formatter.rtf2html(rtf_note);
      body = body.trim();
      while(body.indexOf("<br /><br />")!=-1)
      {
         body = Str.replace(body,"<br /><br />","");
      }
      return body;
   }

   /*
    * Converts a string in html code to a rich text format.
    * @ param String
    * @ return String
    */
   private String encodeNote(String note_body)
   {
      note_body = identifyBreakPoints(note_body);
      Html2Rtf html_formatter = new Html2Rtf(true);
      return removeAdditionalLines(html_formatter.html2rtf(note_body).replace("\\newline","\r\n")); 

   }
   
   /*
    * Compare text returned from html agianst existing text in client note book to see if it has been modified
    */
   private boolean isModoified(String body_rft, String page_rtf)
   {
      //convert both to plain text for comparison
      String plain_body = RTFtoPlainText(body_rft);
      String plain_page_text = RTFtoPlainText(page_rtf);
      
      return (!plain_body.equals(plain_page_text));
   }
   
   private String RTFtoPlainText(String rtf_note)
   {
      if(rtf_note.indexOf("\\rtf1")==-1)
         return rtf_note;

      Rtf2Html rtf_formatter = new Rtf2Html(true, getASPConfig().getImagesLocation(), true, this);
      String body = rtf_formatter.convertRTFStringToPlainText(rtf_note);
      body = body.trim();
      while(body.indexOf("<br /><br />")!=-1)
      {
         body = Str.replace(body,"<br /><br />","");
      }
      return body;
   }
   
   
   private String removeAdditionalLines(String note)
   {
      int index = note.lastIndexOf("\\par\\r\\n");
      if(index == -1)
         return note;
     
      return note.substring(0, index)+ note.substring(index+8);
   }
   
//   private String removeAdditionalLines(String note)
//   {
//      int index = note.lastIndexOf("\\par\r\n");
//      String new_note = "";
//      if(index != -1)
//         new_note = note.substring(0,index) + note.substring(index+6);
//      else
//         new_note = note;
//      index = new_note.lastIndexOf("}");
//      new_note = new_note.substring(0,index+1);
//      return new_note;
//   }
   
   private void clearExpiredImages()
   {
      long timeout = System.currentTimeMillis() - TIME_OUT;
      Enumeration e = imgtable.keys();
      while(e.hasMoreElements())
      {
         Integer key = (Integer)e.nextElement();
         NoteImage noteImage = (NoteImage)imgtable.get((Object)key);
         if(noteImage.getTimeStamp()< timeout)
            imgtable.remove((Object)key);        
      }      
   }
   
   protected static int generateImageKey()
   {
      return randomGenerator.nextInt(10000);
   }

   private String identifyBreakPoints(String note_body)
   {
      note_body = note_body.replace("<br>","<BR> ");
      note_body = note_body.replace("\r\n","<br>");
      return note_body;      
   }
   
   protected static Hashtable getImageTable()
   {
      return imgtable;
   }


   /*
    * Retrieves a client note book. Used when RMI is in use.
    * @ return FndNoteBook
    */
   private FndNoteBook getClientNoteBook ()
   {
      FndNoteBook cltnb = new FndNoteBook();
      FndNotePage page = new FndNotePage();
      FndNotePageArray pages = new FndNotePageArray();
      page.setNonExistent();
      page.includeQueryResults();

      String lu_key_values = getASPManager().readValue("__KEY_REF");
      String lu_name = getASPManager().readValue("__LU_NAME");
      if(!Str.isEmpty(lu_name) && !Str.isEmpty(lu_key_values))
         try
         {      
            cltnb.luName.setValue(lu_name);
            cltnb.keyRef.setValue(lu_key_values);
            cltnb.pages.add(pages);
            cltnb.includeQueryResults();
            FndQueryRecord query = new FndQueryRecord(cltnb);
            notebooks = (FndNoteBookArray)getClientNoteBook(query);
            if(notebooks!=null)
            {
               //Loop through notebooks and get the get notebook we require (There is always one notebook)
               if(notebooks.size()>=1)
                  return notebooks.firstElement();
               else
                  return getNewFndNoteBook(lu_name, lu_key_values);
            }
         }
         catch(Exception e){
            debug(this+"\n"+Str.getStackTrace(e));
         }
      
      return null;
   }
   
   /*
    * Retrieves a client note book. Used when JAP is in use.
    * @ return Record 
    */
   private Record getClientNoteBookRecord ()
   {
      cltnb_rec = new Record("FndNoteBook");
      String lu_key_values = getASPManager().readValue("__KEY_REF");
      String lu_name = getASPManager().readValue("__LU_NAME");
      if(!Str.isEmpty(lu_name) && !Str.isEmpty(lu_key_values))
         try
         {
            cltnb_rec.add("LU_NAME",lu_name);
            cltnb_rec.add("KEY_REF",lu_key_values);
            Record query = new Record("FND_QUERY_RECORD");
            query.addAggregate("CONDITION",cltnb_rec);         
            query = (Record) getClientNoteBook(query);
            if(query!=null)
            {
               //Loop through notebooks and get the get notebook we require (There is always one notebook)
               if(query.find("RESULT").getArray().size()>=1)
                  return query.find("RESULT").getArray().get(0);
               else
                  return getNewFndNoteBookRecord(lu_name,lu_key_values);
            }
         }
         catch(Exception e){
            debug(this+"\n"+Str.getStackTrace(e));
         }
      
      return null;
   }   
   
   /*
    * Splits the notes within a ClientNoteBook into a "^" seperated string'
    * Used when RMI is in use.
    * @ param ClientNoteBook
    * @ return String
    */
   private String splitNotes(FndNoteBook cltnb, boolean deleteImages)
   {
      String notes_str = "";
      int page_no = -1;
      int temp_page_no = -1;
      String description = "";
      String date = ""; 

      //Loop through the notes within the notebook and append to ajax response
      for(int n=0; n<cltnb.pages.size(); n++)
      {
         FndNotePage page = cltnb.pages.get(n);
         FndNotePage._ModifiedByUser modifiedUser = page.modifiedByUser;
         description = modifiedUser.getRecord().description.toString();
         date = page.modifiedDate.toString();
         
         try
         {
            date = datefld.convertToClientString(date);
         }
         catch (FndException e)
         {
         }
         
         page_no = (int)Double.parseDouble(page.pageNo.toString());
         if(page_no < temp_page_no)
         {
            notes_str = "^" + page_no + C30 + decodeNote(page.text.toString(), deleteImages) + C30 + description + C30 + date + notes_str;
         }
         else
         {
            notes_str = notes_str + "^" + page_no + C30 + decodeNote(page.text.toString(), deleteImages) + C30 + description + C30 + date;//del_note
         }

         temp_page_no = page_no;
      }

      if(!Str.isEmpty(notes_str) && notes_str.startsWith("^"))
      {
         notes_str = notes_str.substring(1);
      }
      return notes_str;
   }

   /*
    * Splits the notes within a Record into a "^" seperated string'
    * Used when JAP is in use.
    * @ param Record
    * @ return String
    */
   private String splitNotes(Record nb, boolean deleteImages)
   {
      String notes_str = "";
      int page_no = -1;
      int temp_page_no = -1;
      String description = "";
      String date = "";
      
      if(nb.find("PAGES") != null)
      {
      
         RecordCollection note_col = nb.find("PAGES").getArray();

         //Loop through the notes within the notebook and append to ajax response
         for(int n=0; n<note_col.size(); n++)
         {
            Record note = note_col.get(n);
            page_no = (int)Double.parseDouble(note.findValue("PAGE_NO").toString());//Integer.parseInt(note.findValue("PAGE_NO").toString());
            description = note.find("MODIFIED_BY_USER").getAggregate().findValue("DESCRIPTION").toString();
            try
            {
               date = datefld.formatDate(note.find("MODIFIED_DATE").getDate());
            }
            catch (FndException e)
            {
               e.printStackTrace();
            }

            if(page_no < temp_page_no)
            {
               notes_str = "^" + page_no + C30 + decodeNote(note.findValue("TEXT").toString(), deleteImages) + C30 + description + C30 + date + notes_str;
            }
            else
            {
               notes_str = notes_str + "^" + page_no + C30 + decodeNote(note.findValue("TEXT").toString(), deleteImages) + C30+ description + C30 + date;
            }

            temp_page_no = page_no;

         }

         if(!Str.isEmpty(notes_str) && notes_str.startsWith("^"))
         {
            notes_str = notes_str.substring(1);
         }
         return notes_str;
      }
      
      return "";
    }
   
   /*
    * Saves the client note book. Used with RMI
    * @ return ClientNoteBook
    */
   private FndNoteBook saveClientNoteBook() throws ApplicationException, FndException
   {
      ASPManager mgr = getASPManager();
      String notes_str = mgr.URLDecode(mgr.readValue("__NOTES", ""));
      String del_note_str = mgr.URLDecode(mgr.readValue("__DEL_NOTES", ""));

      String[] note_arr = null;
      String[] del_note_arr = null;
      ArrayList new_notes = null;

      if(!Str.isEmpty(notes_str) || !Str.isEmpty(del_note_str))
      {
         //split notes into an array
         note_arr = Pattern.compile("!note").split(notes_str);

         //split deleted pageNo into an array
         del_note_arr = Pattern.compile(",").split(del_note_str);

         new_notes = getNewNotes(note_arr);
         HashMap map = getNotePages(note_arr) ;

         //get the FndNoteBook to be saved
         FndNoteBook cltnb = getClientNoteBook();

         if(cltnb!=null)
         {
            //loop through the note array
            int total_length = note_arr.length + del_note_arr.length;

            for(int x=0; x<total_length; x++)
            {
               int page_no = -1;
               if(x<cltnb.pages.size())
               {
                  try
                  {
                     page_no = (int)Double.parseDouble(cltnb.pages.get(x).pageNo.toString());
                     String body = (String) map.get(new Integer(page_no));

                     if(body != null)
                     {
                        body = encodeNote(body);

                        if (isModoified(body, cltnb.pages.get(x).text.getValue())) 
                        {
                        cltnb.pages.get(x).text.setValue(body);
                        }

                     }
                     else if(del_note_arr != null && isNoteDeleted(page_no,del_note_arr))
                     {
                        cltnb.pages.get(x).setState(FndRecordState.REMOVED_RECORD);
                     }
                  }
                  catch (Exception any)
                  {
                     any.printStackTrace();
                  }
               }
            }

            Iterator it = new_notes.iterator();
            while(it.hasNext())
            {
               FndNotePage notePage = new FndNotePage();
               String body = encodeNote((String)it.next());
               if(!Str.isEmpty(body))
               {
                  notePage.text.setValue(body);
                  cltnb.pages.add(notePage);
               }
            }

            if(cltnb.pages.size()>0)
            {
               return (FndNoteBook) saveClientNoteBook(cltnb);
            }
         }
      }
      return null;
   }

   /*
    * Saves the client note book. Used with JAP
    * @ return Record
    */
   private Record saveClientNoteBookRecord() throws FndException
   {
      ASPManager mgr = getASPManager();
      String notes_str = mgr.URLDecode(mgr.readValue("__NOTES", ""));
      String del_note_str = mgr.URLDecode(mgr.readValue("__DEL_NOTES", ""));
      
      String[] note_arr = null;
      String[] del_note_arr = null;
      ArrayList new_notes = null;

      if(!Str.isEmpty(notes_str) || !Str.isEmpty(del_note_str))
      {
         //split notes into an array
         note_arr = Pattern.compile("!note").split(notes_str);
         
         //split deleted pageNos' into an array
        del_note_arr = Pattern.compile(",").split(del_note_str);
         
        new_notes = getNewNotes(note_arr);
        HashMap map = getNotePages(note_arr) ;
         
         //get the client notebook to be saved
         cltnb_rec = getClientNoteBookRecord();

         if(cltnb_rec!=null)
         {
            int total_length = note_arr.length + del_note_arr.length;
            
            if(cltnb_rec.find("PAGES")==null)
               cltnb_rec.addArray("PAGES");
            
            //loop through the note array
            for(int x=0; x<total_length; x++)
            {
               int page_no = -1;
               if(x<cltnb_rec.find("PAGES").getArray().size()) //if note already exists in notebook, make changes
               {
                  try
                  {
                     page_no = (int)Double.parseDouble(cltnb_rec.find("PAGES").getArray().get(x).findValue("PAGE_NO").toString());
                     String body = (String) map.get(new Integer(page_no));
                     if(body != null)
                     {
                         body = encodeNote(body);
                         if (isModoified(body, (String)cltnb_rec.find("PAGES").getArray().get(x).find("TEXT").getValue())) 
                         {
                            cltnb_rec.find("PAGES").getArray().get(x).find("TEXT").setValue(body);
                         }

                     }
                     else if(del_note_arr != null && isNoteDeleted(page_no,del_note_arr))
                     {
                          cltnb_rec.find("PAGES").getArray().get(x).remove("CREATED_BY_USER");
                          cltnb_rec.find("PAGES").getArray().get(x).remove("MODIFIED_BY_USER");
                          cltnb_rec.find("PAGES").getArray().get(x).setRemoved();
                     }
                  }
                  catch (Exception any)
                  {
                     any.printStackTrace();
                  }
               }
            }
            Iterator it = new_notes.iterator();
            while(it.hasNext())
            {
               
               Record note = new Record();
               String body = encodeNote((String)it.next());
               if(!Str.isEmpty(body))
               {
                     note.add("TEXT").setValue(body);
                     cltnb_rec.find("PAGES").getArray().add(note);
               }
            }
            if(cltnb_rec.find("PAGES").getArray().size()>0)
            {
                  return (Record) saveClientNoteBook(cltnb_rec);
            }
         }
      }
      return null;
   }
   
   private FndNoteBook getNewFndNoteBook(String lu_name, String lu_key_values) throws ApplicationException, FndException
   {
      FndNoteBook notebook = new FndNoteBook();
      notebook.luName.setValue(lu_name);
      notebook.keyRef.setValue(lu_key_values);
      notebook.posX.setValue(0);
      notebook.posY.setValue(0);
      notebook.height.setValue(0);
      notebook.width.setValue(0);
      notebook.pinned.setValue(FndBooleanEnumeration.FALSE);
      notebook.setState(FndRecordState.NEW_RECORD);
      
      return notebook;
   }
   
   private Record getNewFndNoteBookRecord(String lu_name, String lu_key_values) throws ApplicationException, FndException
   {
      cltnb_rec = new Record("FndNoteBook");
      cltnb_rec.add("LU_NAME",lu_name);
      cltnb_rec.add("KEY_REF",lu_key_values);
      cltnb_rec.add("POS_X",0);
      cltnb_rec.add("POS_Y",0);
      cltnb_rec.add("HEIGHT",0);
      cltnb_rec.add("WIDTH",0);
      cltnb_rec.add("PINNED",false);
      cltnb_rec.setNew();
      
      return cltnb_rec;
   }
   private String hasNotes(FndNoteBook cltnb)
   {
      return "NotesAvailability^"+(cltnb.pages.size()>0?"true":"false");
   }
   
   private String hasNotes(Record nb)
   {
      if(nb.find("PAGES") != null){
         RecordCollection note_col = nb.find("PAGES").getArray();
         return "NotesAvailability^"+(note_col.size()>0?"true":"false");
      }
      else
         return "NoteAvailability^false";
   }
   
   private String showError(String error)
   {
      String err_msg = "__FND_Error^"+getASPManager().translateJavaScript(error);
      return err_msg;
   }
   
   private HashMap getNotePages(String[] array)
   {
      HashMap map = new HashMap();
      String[] str = null;
      int page_no = -1;
      String msg_body = "";
      String delimeter = String.valueOf(C30);
      
      for(int i=0; i<array.length; i++)
      {          
         str = Pattern.compile(delimeter).split(array[i]);
         if(!Str.isEmpty(str[0]) && !str[0].equals("X"))
         {
            page_no = Integer.parseInt(str[0]);
            msg_body = str[1];
            map.put(new Integer(page_no),msg_body);
         }                      
      }      
      return map;
   }
   
   private ArrayList getNewNotes(String[] array)
   {
      String delimeter = String.valueOf(C30);
      String[] str = null;
      ArrayList new_notes = new ArrayList();
      
      for(int i=0;i<array.length;i++)
      {
         str = Pattern.compile(delimeter).split(array[i]);
         if(str[0].equals("X") && str.length == 2)// X indicates a new note
            new_notes.add(str[1]);        
      }
      return new_notes;
   }
   
   private boolean isNoteDeleted(int page_no,String[] array)
   {
      for(int i=0;i<array.length;i++)
      {
         if(page_no == Integer.parseInt(array[i]))
            return true;
      }
      return false;
   }
   
   class NoteImage {
      private String imageData;
      private long timeStamp;
      
      private NoteImage(String imageData, long timeStamp)
      {
         this.imageData = imageData;
         this.timeStamp = timeStamp;
      }
      
      public String getImageData()
      {
         return imageData;
      }
      
      public long getTimeStamp()
      {
         return timeStamp;
      }      
   }
   
   protected NoteImage getNoteImageInstance(String imageData, long timeStamp)
   {
      return new NoteImage(imageData, timeStamp);
   }
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      utilblk = mgr.newASPBlock("HEAD");
      datefld = utilblk.addField("DATE", "Datetime");
   }
   
}

