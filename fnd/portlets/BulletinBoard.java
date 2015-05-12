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
 * File        : BulletinBoard.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H  2000-Apr-03 - Created.
 *    Ramila H  2000-Apr-07 - Changed to suit webkit 3 beta 2
 *    Ramila H  2000-Apr-11 - Added getId() to javascript functions and Code Review.
 *    Ramila H  2000-Apr-20 - Changed headling, added message entry box and limited 
 *                             number of messages.
 *    Ramila H  2000-Apr-26 - Changed getMessages() from function to procedure to 
 *                            use a ROWSET rather than a string which was limited
 *                            to 2000 chars for all messages.
 *    Ramila H  2000-Apr-27 - Added topic description to title when minimized.
 *                            moved fnduser = getFndUser() to init(); to suit
 *                            webkit 3 beta 3.
 *    Jacek P   2000-Aug-25 - Updated according to the latest standard. Still to many 'performs'.
 *    Ramila H  2001-Nov-21 - corrected PROJ call id 69661.
 *    Rifki R   2002-Apr-08 - Fixed translation problems caused by trailing spaces.
 *    Chanaka A 2002-Dec-17 - Changers for log id 934.
 *    Chanaka A 2002-Dec-26 - Changers to identify URLs typed in the add message messagebox
 *                            log id 935.
 *    Chandana D2003-Jan-22 - Log id 942. Modified getMessages() method.
 *    Suneth M  2004-Aug-04 - Changed duplicate localization tags.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.1  2005/09/15 12:38:02  japase
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/19 14:59:06  buhilk
 * Bug id: 63433, Added csv support
 *
 * Revision 1.2  2005/08/15 09:24:08  mapelk
 * JAAS related bug fix. Error when use includeJavaScriptFile() in old fashion.
 * 
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;


public class BulletinBoard extends ASPPortletProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.BulletinBoard");

   //private ASPBlock   blk;
   private ASPBlock  itemblk;
   private ASPRowSet itemset;
   private String    imgloc;
   private String    scriptloc;


   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient String topic_id;
   private transient String topic_desc;
   private transient String iswriter;
   private transient String savemsg;
   private transient String startfrom;
   private transient String pagesize;
   private transient ASPTransactionBuffer trans;
   
   

   public BulletinBoard( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }


   public ASPPoolElement clone( Object obj ) throws FndException
   {
      BulletinBoard page = (BulletinBoard)(super.clone(obj));

      page.topic_id   = null;
      page.topic_desc = null;
      page.iswriter   = null;
      page.savemsg    = null;
      page.startfrom  = null;
      page.pagesize   = null;
      page.trans      = null;
      
      //page.blk    = page.getASPBlock(blk.getName());
      page.itemblk = page.getASPBlock(itemblk.getName());
      page.itemset = page.itemblk.getASPRowSet();
      page.imgloc    = imgloc;
      page.scriptloc = scriptloc;

      return page;
   }


   protected void doReset() throws FndException
   {
      topic_id   = null;
      topic_desc = null;
      iswriter   = null;
      savemsg    = null;
      startfrom  = null;
      pagesize   = null;
      trans      = null;

      super.doReset();
   }

   //==========================================================================
   //  
   //==========================================================================

   public static String getDescription()
   {
       return "FNDBBOARDDESC: Bulletin Board";
   }


   public String getTitle( int mode )
   {
      String title = getASPManager().isEmpty(topic_desc)?translate(getDescription()):topic_desc;

      if ((mode==MINIMIZED)||(mode==MAXIMIZED))
         return title;
      else
         return translate("FNDBBOARDCUSTTIT: Customize") +" "+ translate(getDescription());
   }

   
   protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": bulletinboard.preDefine()"); 
       
      imgloc    = getImagesLocation();
      scriptloc = getScriptsLocation();

      ASPBlock blk = newASPBlock("MAIN");
      addField(blk, "TOPIC_ID"); //.setHidden();
      addField(blk, "DESCRIPTION");
      addField(blk, "FNDUSER").setHidden().setFunction("''");
      
      ASPBlock tmpblk = newASPBlock("TEMP");
      addField(tmpblk, "MESSAGES").setHidden().setFunction("''");
      addField(tmpblk, "ISWRITER");

      itemblk = newASPBlock("ITEMBLK");
      addField(itemblk, "ITEM_TOPIC_ID").setDbName("TOPIC_ID");
      addField(itemblk, "WRITER").setHidden();
      addField(itemblk, "WRITER_NAME").setFunction("fnd_user_api.get_description(:WRITER)");
      addField(itemblk, "CREATED_AT", "Datetime");
      addField(itemblk, "EMAIL").setFunction(" fnd_user_property_api.get_value(:WRITER,'SMTP_MAIL_ADDRESS')"); 
      addField(itemblk, "ITEM_MESSAGE").setDbName("MESSAGE");
      
      itemblk.setView("BULLETIN_BOARD_MESSAGES");
      itemset = itemblk.getASPRowSet();

      includeJavaScriptFile(getScriptsLocation().substring(getASPConfig().getApplicationContext().length()) + "BulletinBoardScript.js");
      init();
   }
   
   
   protected void init()// throws FndException
   {
      topic_id = readProfileValue( "TOPIC_ID", ""  );
      pagesize = readProfileValue( "PAGE_SIZE", "10" );
   }

   
   protected void run()
   {
      savemsg = readValue("SAVEMSG","");
      startfrom = readValue( "START_FROM", "1" );

      topic_desc = getTopicDescription();
      
      if ("TRUE".equals(savemsg))
         saveMessage();

      if( !(Str.isEmpty(topic_id)) )
         getMessages();

      iswriter = getIsWriter();
   }


   private void saveMessage()
   {
      savemsg = readValue("MESSAGEBOX","NULL");
      
      if (!"NULL".equals(savemsg))
      {    
         trans = getASPManager().newASPTransactionBuffer(); 
         ASPCommand cmd = getASPManager().newASPCommand();
      
         cmd = trans.addEmptyCommand("ITEM","Bulletin_Board_Messages_API.New__",itemblk);
         cmd.setOption("ACTION","DO");
         cmd.addParameter(this,"ITEM_MESSAGE",savemsg);
         cmd.addParameter(this,"TOPIC_ID",topic_id);

         perform(trans);
      }
   }
   

   private void getMessages()
   {
      trans = getASPManager().newASPTransactionBuffer(); 
      ASPQuery q = trans.addQuery(itemblk);
      //q.addWhereCondition("TOPIC_ID='"+topic_id+"'");
      q.addWhereCondition("TOPIC_ID= ?");
      q.addParameter(this,"TOPIC_ID",topic_id);

      q.setOrderByClause("CREATED_AT desc");
      q.includeMeta("ALL");
      
      submit(trans);
   }

   
   private String getTopicDescription()
   {
      if(Str.isEmpty(topic_id)) return "";

      trans = getASPManager().newASPTransactionBuffer();  
      ASPCommand cmd = getASPManager().newASPCommand();
      cmd.defineCustomFunction(this, "Bulletin_Board_Topics_API.Get_Description", "DESCRIPTION");
      cmd.addParameter(this, "TOPIC_ID", topic_id);
      trans.addCommand("NAME", cmd);
      
      trans = perform(trans);

      return( trans.getValue("NAME/DATA/DESCRIPTION") );
   }  


   private String getIsWriter()
   {
      trans = getASPManager().newASPTransactionBuffer();  
      ASPCommand cmd = getASPManager().newASPCommand();
      cmd.defineCustomFunction(this, "Bulletin_Board_Topic_Users_API.Is_Writer", "ISWRITER");
      cmd.addParameter(this, "TOPIC_ID", topic_id);
      trans.addCommand("WRITER", cmd);
      
      trans = perform(trans);

      return( trans.getValue("WRITER/DATA/ISWRITER") );
   }  
   

   public void printContents() throws FndException
   {
      printHiddenField("SCRIPT_LOC",scriptloc); 

      if(!Str.isEmpty(topic_id))
      {    
         printHiddenField("TOPIC_ID",topic_id); 
         printNewLine();           

         if(itemset.countRows()== 0)
         {    
            printSpaces(5);
            printText("FNDBBOARDNOMSG: NO MESSAGES");
            printNewLine();
         }    
         else
         {
            String token,user,time,msg,email;

            int temp_pagesize  = Integer.parseInt(pagesize);
            int temp_startfrom = Integer.parseInt(startfrom);
            int cnt_msg        = itemset.countRows();
                
            if( temp_startfrom > 1 )
            {    
               printSpaces(2);
               printSubmitImageLink(imgloc+"previous.gif","previousBulletinBoardMessages");
               printSpaces(2);
               printText("FNDBBOARDPREV:  previous");
            }
            if( cnt_msg - temp_startfrom >= temp_pagesize)
            {    
               printSpaces(3);
               printText("FNDBBOARDNEXT: next");
               printSpaces(2);
               printSubmitImageLink(imgloc+"next.gif","nextBulletinBoardMessages");
            }

            --temp_startfrom;
            itemset.goTo(temp_startfrom);
                
            if(temp_startfrom + temp_pagesize > cnt_msg)
               temp_pagesize -= temp_startfrom + temp_pagesize - cnt_msg;
                
            beginTable(null,true,false);
               beginTableBody();
               while( temp_pagesize > 0 )
               {
                  temp_pagesize--;
                  user  = itemset.getValue("WRITER_NAME");
                  email = itemset.getValue("EMAIL");
                  time  = itemset.getValue("CREATED_AT");
                  msg   = itemset.getValue("MESSAGE");
                  itemset.next();
                       
                     beginTableCell();
                        printImage(imgloc+"red-ball-small.gif");
                     endTableCell();

                     beginTableCell(true);
                        setFontStyle(ITALIC);
                        printLink(user,"mailto:"+email);
                        endFont();
                     endTableCell();

                     beginTableCell(RIGHT,true);
                        setFontStyle(ITALIC);
                        printText("FNDBBOARDTIME: Entered at :");
                        printText(time);
                        endFont();
                     endTableCell();

                     nextTableRow();
                     printTableCell(null);
                     beginTableCell(2);
                        try
                        {
                           bbFormatMessage(msg);
                        }
                        catch (Exception e)
                        {
                           printText(msg); 
                        }
                     endTableCell();
                     nextTableRow();
               }
               endTableBody();
            endTable();    
                
            printHiddenField("START_FROM",startfrom); 
            printHiddenField("PAGE_SIZE" , pagesize);
         }
            
         if("TRUE".equals(iswriter))
         {    
            ASPManager mgr = getASPManager();

            if (!mgr.isNetscape4x()) 
            {
               printNewLine();           
               printCheckBox("SHOWADDMESSAGEBOX","SHOW","SHOW".equals(readValue("SHOWADDMESSAGEBOX")),"showAddMessagebox");
               printSpaces(2);
               printText("FNDBBOARDSHOWMSG: Show Message Box");
               printNewLine();

            }
            else
            {
               printNewLine();
               printSpaces(1);
               printText("FNDBBOARDADDMSG: Add Message :");
            }

            beginTable("MESSAGEBOXTABLE",true,false);
               beginTableBody();

                  beginTableCell();
                     printTextArea("MESSAGEBOX","",3,65);
                  endTableCell();

                  beginTableCell();
                     printSubmitImageLink(imgloc+"toolbar_submit.gif","addBulletinBoardMessage");
                  endTableCell();

               endTableBody();
            endTable(); 

            if (!"SHOW".equals(readValue("SHOWADDMESSAGEBOX")))
               appendDirtyJavaScript("initBulletinBoardTable('",getId(),"');\n");

            printHiddenField("SAVEMSG","FALSE" );
         }

         printNewLine();
         printScriptLink("FNDBBOARDCREATE: Create/Modify Topics","runBulletinBoardTopic");
                
         if ("TRUE".equals(iswriter))
         {    
            printSpaces(2);
            printScriptLink("FNDBBOARDADD: Add/Modify Messages","runBulletinBoardMessages");
         }    
      }      
      else
      {
         printNewLine();
         printText("FNDBBOARDCHCMP1: Choose the topic id from the");
         printSpaces(1);
         printScriptLink("FNDBBOARDCHCMP2: Customization", "customizeBulletinBoard");
         printSpaces(1);
         printText("FNDBBOARDCHCMP3: page or create a");
         printSpaces(1);
         printScriptLink("FNDBBOARDNEW: New","runBulletinBoardTopic");
         printSpaces(1);
         printText("FNDBBOARDCHMP5: one.");
         printNewLine();
         printNewLine();
      }
   }


   public boolean canCustomize()
   {
      return true;
   }

   
   public void runCustom()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = mgr.newASPCommand();
      cmd.defineCustomFunction(this, "FND_SESSION_API.Get_Fnd_User","FNDUSER");
      trans.addCommand("USERDET", cmd);

      cmd = trans.addQuery("TOPICLIST", "BULLETIN_BOARD_TOPIC_USERS",
                                    "TOPIC_ID,BULLETIN_BOARD_TOPICS_API.Get_Description(TOPIC_ID)", "IDENTITY=?", "TOPIC_ID");
      cmd.addReference(this,"FNDUSER","USERDET/DATA","FNDUSER");

      cmd = mgr.newASPCommand();
      cmd.defineCustomFunction(this, "Bulletin_Board_Topics_API.Get_Description", "DESCRIPTION");
      cmd.addParameter(this, "TOPIC_ID", topic_id);
      trans.addCommand("NAME", cmd);
      
      trans = perform(trans);
   }


   public void printCustomBody() throws FndException
   {
      printNewLine();
      beginTable(null,true,false);
         beginTableBody();
            printTableCell("FNDBBOARDCURTOP: Current Topic:");  
            printTableCell( trans.getValue("NAME/DATA/DESCRIPTION") );
         nextTableRow();
            printTableCell("FNDBBOARDCHOTOP: Choose your Topic:");
            beginTableCell();
               printSelectBox("TOPICS", trans.getBuffer("TOPICLIST"), readAbsoluteProfileValue( "TOPIC_ID", ""  ));
            endTableCell();
         nextTableRow();
            printTableCell("FNDBBOARDPAGESIZE: Number of Messages shown in this portlet:");
            beginTableCell();
               printField("PAGE_SIZE", readAbsoluteProfileValue( "PAGE_SIZE", "10" ), 3,3);
            endTableCell();
         endTableBody();
      endTable();
   }


   public void submitCustomization()
   {
      topic_id = readValue("TOPICS");
      writeProfileValue("TOPIC_ID", readAbsoluteValue("TOPICS"));

      pagesize = readValue("PAGE_SIZE");
      try
      {
         Integer.parseInt(pagesize);
      }
      catch(NumberFormatException e)
      {
         pagesize = "10";
      }
      writeProfileValue("PAGE_SIZE",readAbsoluteValue("PAGE_SIZE","10"));
   }

   public void bbFormatMessage(String message)
   {
      int startindex;
      int endindex;
      int index;
      int count;
      int spaceindex;
      int comarindex;
      String substringhtml;
      String substringstart;
      String substringend;
      String tempstr;
      boolean httpcount = true;
      
      index=0;
      startindex=0;
      endindex=0;
      spaceindex=0;
      comarindex=0;

      index = message.indexOf("://");
      spaceindex = message.indexOf(" ",index);
      comarindex = message.indexOf(",",index);
      endindex = spaceindex;

      while (httpcount)
      {
         if (endindex<0)
         {
            endindex = message.length();
         }

         if ((comarindex<endindex)&&(comarindex>=0))
         {
            endindex=comarindex;
         }

         if (index>0)
         {
            count=index;
            while(count!=0)
            {
               if ((message.charAt(count)==' '))
               {
                  startindex = count;
                  break;
               }
               else if ((message.charAt(count)==','))
               {
                  startindex = count;
                  break;
               }
              count--;
            }
              
            if ((startindex==0)&&(message.charAt(startindex)!=' ')
               &&(message.charAt(startindex)!=','))
            {
               substringhtml = message.substring(0,endindex);
               printLink(substringhtml,"javascript:showNewBrowser('"+substringhtml+"')");
            }
            else
            {
               substringstart = message.substring(0,startindex+1);
               substringhtml = message.substring(startindex+1,endindex);
               printText(substringstart);
               printLink(substringhtml,"javascript:showNewBrowser('"+substringhtml+"')");
            }

            tempstr = message.substring(endindex,message.length());
            message = "";
            message = tempstr;
            index = message.indexOf("://");

            if (!(index<0))
            {
               spaceindex = message.indexOf(" ",index);
               comarindex = message.indexOf(",",index);
               endindex = spaceindex;
               startindex=0;
               count=0;
               substringstart="";
               substringhtml="";
               substringend="";
            }
         }
         else
         {
            printText(message);
            httpcount=false;
         }
      }
   }
}

