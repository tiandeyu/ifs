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
 *  ----------------------------------------------------------------------------
 *  File                        : EdmBriefcaes.java
 *  Description                 : Handles Key_ref values and their names for document Management.
 *  Notes                       :
 *  Other Programs Called       : DocmawUtil.java
 *  ----------------------------------------------------------------------------
 *  History:
 *
 *  03-02-2004  Bakalk  Created.
 *  06-05-2004  Bakalk  Added a new constructor KeyRef() and appendNewAttr(String,String).
 *  07-05-2004  Bakalk  Added a new constructor ValueHolder(String,String,String)() and getKeyRefDBString(String,String).
 *  2005-01-06  SukMlk  Added a new sorting routine to sort the KeyRef. 
 *----------------------------------------------------------------------------
 */

package ifs.docmaw;
import java.util.*;
public class KeyRef{
   private int no_of_elements;
   private ValueHolder[] packs;
   private int current_index;
   private ValueHolder current_pack;
   private boolean anyMore;

   public KeyRef(){ //like default constructor. 
      current_index  = 0;
      no_of_elements = 0;
      packs = new ValueHolder[no_of_elements];
   }


   public KeyRef(String key_string){
      StringTokenizer main_tokenizer = new StringTokenizer(key_string,"^");
      StringTokenizer sub_tokenizer;
      no_of_elements = main_tokenizer.countTokens();
      if (this.no_of_elements>0) {
         anyMore = true;
      }
      packs = new ValueHolder[no_of_elements];
      int index = 0;
      String current_name  = "";
      String current_value = "";
      while (main_tokenizer.hasMoreElements())
      {
         sub_tokenizer = new StringTokenizer(main_tokenizer.nextToken(),"=");

         if(sub_tokenizer.hasMoreTokens()) {
            current_name  = sub_tokenizer.nextToken();//returns the name 
         }
         if(sub_tokenizer.hasMoreTokens()) {
            current_value = sub_tokenizer.nextToken();//returns the value
         }
        
         packs[index++] = new ValueHolder(current_name,current_value);
         current_name  = "";
         current_value = "";

      } 
      current_index = 0;
      current_pack  = packs[current_index];
      sortKeys();

   }


   public void first(){
      current_index = 0;
      current_pack  = packs[current_index];
   }


   public String getCurrentValue(){
      return current_pack.getValue();
   }


   public String getCurrentName(){
      return current_pack.getName();
   }


   public void appendNewAttr(String name, String value)
   {
      appendNewAttr(name,value,"");
   }


   public void appendNewAttr(String name, String value, String db_name)
   {
      ValueHolder[] temp_packs = packs;
      no_of_elements++;
      packs = new ValueHolder[no_of_elements];
      for (int k=0;k<no_of_elements-1;k++) {
         packs[k]= temp_packs[k];
      }
      if ("".equals(db_name)) {
         packs[no_of_elements-1] = new ValueHolder(name,value);
      }
      else{
         packs[no_of_elements-1] = new ValueHolder(name,value,db_name);
      }
      sortKeys();
   }


   public String getKeyRefString()
   {
      String ret_str="";
      for (int k=0;k<this.no_of_elements;k++) {
         ret_str += packs[k].getName()+"="+ packs[k].getValue()+"^";
      }
      return ret_str;
   }


   public String getKeyRefDBString()
   {
      String ret_str="";
      String start_str = "";
      for (int k=0;k<this.no_of_elements;k++) {
         start_str = (k==0)? "":"||";
         ret_str += start_str+"'"+packs[k].getName()+"='||"+packs[k].getDbName()+"||'^'";
      }
      return ret_str;
   }

   public void next(){
      if (!isLast()) {
         current_pack  = packs[++current_index];
      }
      else{
         anyMore = false;
      }
      
   }


   public boolean isLast(){
      return (current_index  == no_of_elements-1);
   }


   public boolean hasMore(){
      return anyMore;
   }




   public String getValueOfName(String keyName){
      String return_string = "";
      for (int k=0;k<no_of_elements;k++) {
         if (packs[k].getName().equals(keyName)) {
            return_string = packs[k].getValue();
            break;
         }
      }
      return return_string;
   }
   
   public void sortKeys()
   {
      int n = no_of_elements - 1;      
      String tName, tValue, tDbName;


      for (int i = 0; i < n; i++) 
      {
         for (int p = 0; p < (n-i); p++) 
         {
            if (packs[p].getName().compareTo(packs[p+1].getName()) > 0)
            {
               tName    = packs[p+1].getName();
               tValue   = packs[p+1].getValue();
               tDbName  = packs[p+1].getDbName();
               
               packs[p+1].setName(packs[p].getName());
               packs[p+1].setValue(packs[p].getValue());
               packs[p+1].setDbName(packs[p].getDbName());

               packs[p].setName(tName);
               packs[p].setValue(tValue);
               packs[p].setDbName(tDbName);
            }
         }
      }  
   }

}


class ValueHolder{
   private String name;
   private String value;
   private String db_name;

   public  ValueHolder( String name,
                        String value){
      this.name  = name;
      this.value = value;
   }

   public  ValueHolder( String name,
                        String value,
                        String db_name){
      this.name    = name;
      this.value   = value;
      this.db_name = db_name;
   }
   public String getValue(){
      return value;
   }
   public String getName(){
      return name;
   }
   public String getDbName(){
      return db_name;
   }

   public void setValue(String tValue)
   {
      value = tValue;
   }
   public void setName(String tName)
   {
      name  = tName;
   }
   public void setDbName(String tDbName)
   {
      db_name = tDbName;
   }
}
