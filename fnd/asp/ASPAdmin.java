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
 * File        : ASPAdmin.java
 * Description : Administration thread.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Johan S  2000-Sep-11 - Created.
 *    Daniel S 2002-Feb-05 - Synchronized runAdmin() due to thread problems.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import java.lang.reflect.*;
import java.util.*;
import ifs.fnd.buffer.*;

import ifs.fnd.service.*;


class ASPAdmin extends Thread
{

   public static boolean DEBUG    = Util.isDebugEnabled("ifs.fnd.asp.ASPContextCache");
   private Vector tasks = new Vector();
   private static int SEC = 1000;

   private static ASPAdmin admin;

   private ASPAdmin()
   {
   }

   static synchronized void runAdmin()
   {
         if(admin==null)
         {
            admin = new ASPAdmin();
            admin.start();
         }
   }


   public void run()
   {
      initAllTasks();

      if(tasks.size()==0) return;
      while(true)
      {
         long least;
         while(true)
         {
            synchronized(tasks)
            {
               //Execute round
               for(int x=0;x<tasks.size();x++)
               {
                  Date now = new Date();
                  Task current = (Task) tasks.elementAt(x);
                  if(current.getNextExec() <= now.getTime())
                  {
                     try
                     {
//                        (Class.forName(current.class_name).getMethod(current.method_name,new Class[0]).invoke(null,new Object[0]));

                         Class cls = Class.forName(current.class_name);
                         Method met = cls.getMethod(current.method_name,new Class[0]);
                         met.invoke(null,new Object[0]);

                        if(DEBUG) debug("ASPAdmin: Executed task "+current.method_name+" in "+current.class_name);
                     }
                     catch(Throwable e)
                     {
                        if(DEBUG) debug("Couln't access function "+current.method_name+" in "+current.class_name);
                     }
                     current.setNextExec((current.interval)+now.getTime());
                  }
               }
               //Find next Execution
               least=1000*60*60*24*365; //ungefär ett år;
               long now = (new Date()).getTime();
               for(int x=0;x<tasks.size();x++)
               {
                  Task current = (Task) tasks.elementAt(x);
                  if((current.getNextExec()-now<least))
                     least=current.getNextExec()-now;
               }
            }
            if(least>0)
               break;
         }

         try
         {
            sleep(least);
         }
         catch(InterruptedException e)
         {
         }
      }

   }

   private void initAllTasks()
   {
      try
      {
         Buffer buff = ASPConfig.getThreadBuffer();
         if(buff==null) return;

         for(int x=0;x<buff.countItems();x++)
         {
            Buffer current = buff.getBuffer(x);
            String mname = current.getString("METHOD_NAME");
            String cname = current.getString("CLASS_NAME");
            String inter = current.getString("INTERVAL");

            Task newone = new Task(mname,cname,(Integer.parseInt(inter)*1000*60));
            tasks.addElement(newone);
            newone.setNextExec(newone.interval+(new Date()).getTime());
         }
      }
      catch(ItemNotFoundException e)
      {
         if(DEBUG) debug("ASPAdmin.initAllTasks: ASPStaticConfig Buffer error");
      }
   }


   private synchronized int findTask(String fn)
   {
      for(int x=0;x<tasks.size();x++)
      {
         if(((Task) tasks.elementAt(x)).method_name.equals(fn))
            return x;
      }
      return -1;
   }

   class Task
   {
      private String method_name;
      private String class_name;
      private long interval;
      private long next_execution;

      private Task(String name, String clss, long val)
      {
         method_name = name;
         class_name = clss;
         interval = val;
      }

      private long getNextExec()
      {
         return next_execution;
      }

      private void setNextExec(long next)
      {
         next_execution = next;
      }

   }

   //==========================================================================
   //  Debugging, error
   //==========================================================================

   private static void debug( String line )
   {
      Util.debug(line);
   }

}
