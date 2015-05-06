package ifs.docmaw.edm;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;


public class UploadWorker implements Runnable {
   private UploadWorker instance = null;
   
   private UploadWorker(){}
   
   public UploadWorker newInstance(){
      if(null == instance){
         instance = new UploadWorker();
      }
      return instance;
   }
   
   private static List<Command> commandList = new Vector<Command>();
   
   public static void addCommand(Command command){
      commandList.add(command);
   }

   public void run() {
      while(true){
         try {
            Thread.sleep(10000);
            for (Iterator<Command> iterator = commandList.iterator(); iterator.hasNext();) {
               try{
                  Command command = iterator.next();
                  if(command.execute()){
                     commandList.remove(command);
                  }else{
                     //expired or not?
                     //TODO 
                  }
                  
               }catch(Throwable th){
                  //
                  th.printStackTrace();
               }
            }
           
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      
   }

}
