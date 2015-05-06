package ifs.docmaw.reportupload;
import ifs.docmaw.reportupload.Command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * @author luqingwei 2014-04-14
 *
 */
public class UploadWorker implements Runnable {
   private UploadWorker(){}
   
   private static UploadWorker instance = null;
   private static boolean started = false;
   
   
   public static UploadWorker newInstance(){
      if(null == instance){
         instance = new UploadWorker();
      }
      return instance;
   }
   
   private static List<Command> commandList = new ArrayList<Command>();
   private static List<Command> toDoCommandList = new ArrayList<Command>();
   
   public static void addCommand(Command command){
      if(!started){
         new Thread(newInstance(), "UploadWorker").start();
         started = true;
      }
      synchronized (commandList) {
         commandList.add(command);
      }
   }

   public void run() {
      while(true){
         try {
            Thread.sleep(10000);
            toDoCommandList.clear();
            toDoCommandList.addAll(commandList);
            for (Iterator<Command> iterator = toDoCommandList.iterator(); iterator.hasNext();) {
               try{
                  Command command = iterator.next();
                  if(command.execute()){
                     synchronized (commandList) {
                        commandList.remove(command);
                     }
                  }else{
                     //expired or not?
                     //TODO 
                  }
               }catch(Throwable th){
                  th.printStackTrace();
               }
            }
           
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

}
