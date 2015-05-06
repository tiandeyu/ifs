package ifs.hzwflw;

public class HzBizWfException extends Exception {
	  public HzBizWfException(){
	      super();
	   }
	   
	   public HzBizWfException(String msg){
	      super(msg); 
	   }
	   
	   public HzBizWfException(String msg, Throwable th){
	      super(msg,th);
	   }
	   
	   public HzBizWfException( Throwable th){
	      super( th);
	   }
}
