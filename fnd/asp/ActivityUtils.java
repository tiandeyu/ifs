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
*  File        : ActivityUtils.java
*  Modified    :
* ----------------------------------------------------------------------------
* 2008/08/15 buhilk - Bug 76288, Modified addAttributeCondition() and enumAttributeCondition().
* 2008/07/01 buhilk - Bug 74852, Modified all set attribute methods to cehck unchanged values.
* 2008/06/26 buhilk - Bug 74852, Created. Programming Model for Activities.
*
*/

package ifs.fnd.asp; 

import ifs.fnd.base.ApplicationException;
import ifs.fnd.base.FndContext;
import ifs.fnd.base.ParseException;
import ifs.fnd.buffer.DateFormatter;
import ifs.fnd.record.*;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import java.util.StringTokenizer;

/**
 * Utility class for Activity Programming Model.
 */
public class ActivityUtils {

   /**
    * Create a search condition for attributes and returns it as a FndCondition object.
    * @param field field object.
    * @param value query value for the given attribute.
    * @param case_sensitive either case_sensitivity is true or false.
    * @return FndCondition for the attribute.
    * @throws ifs.fnd.service.FndException During condition creation.
    */
   static FndCondition addAttributeCondition(ASPField field, String value, boolean case_sensitive) throws FndException
   {
      if(field==null || value==null) return null;
      FndAttributeType attr_type = field.getTemplate().getType();
      FndCondition simple_cond = null;
 
      try{
         if(attr_type == FndAttributeType.INTEGER)
            simple_cond = integerAttributeCondition(field, value);
         else if(attr_type == FndAttributeType.DECIMAL || attr_type == FndAttributeType.NUMBER)
            simple_cond = numberAttributeCondition(field, value);
         else if(attr_type == FndAttributeType.BOOLEAN)
            simple_cond = booleanAttributeCondition(field, value);
         else if(attr_type == FndAttributeType.TIMESTAMP)
            simple_cond = timestampAttributeCondition(field, value);
         else if(attr_type == FndAttributeType.DATE)
            simple_cond = dateAttributeCondition(field, value);
         else if(attr_type == FndAttributeType.TIME)
            simple_cond = timeAttributeCondition(field, value);
         else if(attr_type == FndAttributeType.ENUMERATION)
            simple_cond = enumAttributeCondition(field, value, case_sensitive);
         else
            simple_cond = textAttributeCondition(field, value, case_sensitive);
      }
      catch (FndException fe)
      {
         throw new FndException("FNDACTIVITYUTILPARSEERROR1: Query value for &1 is not in a correct format.", field.getLabel());
      }
      
      return simple_cond;
   }
   
   /**
    * Returns a string array containing the condition and value in index 0 and 1 respectivly.
    * @param fValue Value inside the field.
    * @return string array containg the condition and values in index 0 and 1.
    */
   private static String[] splitConditionAndValue(String fValue)
   {
      String cond = "";
      String value= "";
      if(fValue.startsWith("<=") || fValue.startsWith(">=") || fValue.startsWith("<>") || fValue.startsWith("!="))
      {
         cond = fValue.substring(0,2);
         value = fValue.substring(2);
      }
      else if(fValue.startsWith("<") || fValue.startsWith(">") || fValue.startsWith("!") || fValue.startsWith("="))
      {
         cond = fValue.substring(0,1);
         value = fValue.substring(1);         
      }
      else if(fValue.indexOf("%")!=-1)
      {
         cond = "%";
         value = fValue;
      }
      else if(fValue.indexOf("..")!=-1)
      {
         cond = "..";
         value = fValue;
      }
      else
      {
         cond = "=";
         value = fValue;
      }
      return new String[] {cond, value};
   }
   
   /**
    * Create a search condition for text field and returns it as a FndCondition object.
    * @param field ASPField.
    * @param fValue query value for the given attribute.
    * @param case_sensitive either case_sensitivity is true or false.
    * @return FndCondition for text attribute.
    */
   static FndCondition textAttributeCondition(ASPField field, String fValue, boolean case_sensitive)
   {
      FndText attr = (FndText) field.getTemplate();
      FndCondition simple_cond = null;
      FndCondition final_cond = null;
      
      String[] cond_value = splitConditionAndValue(fValue);
      
      boolean _or = false;
      String condition = cond_value[0];      
      StringTokenizer st = new StringTokenizer(cond_value[1],";");
      
      while(st.hasMoreTokens())
      {
         simple_cond = null;
         String val = st.nextToken();
         String value1 = val;
         String value2 = "";
         
         if(val.indexOf("..")!=-1)
         {
            value1 = val.substring(0,val.indexOf(".."));
            value2 = val.substring(val.indexOf("..")+2);
         }
         
         if(!Str.isEmpty(value1))
         {
            if(condition.equals("<="))
               simple_cond = attr.createLessThanOrEqualCondition(value1);
            else if(condition.equals(">="))
               simple_cond = attr.createGreaterThanOrEqualCondition(value1);
            else if( condition.equals("<>") || condition.equals("!="))
               simple_cond = (case_sensitive)?attr.createNotEqualCondition(value1) :attr.createNotEqualIgnoreCaseCondition(value1);
            else if(condition.equals("!") && condition.trim().length()==1)
               simple_cond = attr.createIsNullCondition();
            else if(condition.equals(".."))
               simple_cond = attr.createBetweenCondition(value1, value2);
            else if(condition.equals("<"))
               simple_cond = attr.createLessThanCondition(value1);
            else if(condition.equals(">"))
               simple_cond = attr.createGreaterThanCondition(value1);
            else if( condition.equals("%"))
               simple_cond = (case_sensitive)?attr.createLikeCondition(value1) :attr.createLikeIgnoreCaseCondition(value1);
            else if(condition.equals("="))
               simple_cond = (case_sensitive)?attr.createEqualCondition(value1) :attr.createEqualIgnoreCaseCondition(value1);
         }
         final_cond = (_or)? final_cond.or(simple_cond) : simple_cond;
         _or = true;
      }
      
      return final_cond;
   }
      
   /**
    * Create a search condition for an enumeration field and returns it as a FndCondition object.
    * @param field ASPField.
    * @param fValue query value for the given attribute.
    * @param case_sensitive either case_sensitivity is true or false.
    * @return FndCondition for the enumeration attribute.
    */
   static FndCondition enumAttributeCondition(ASPField field, String fValue, boolean case_sensitive)
   {
      FndEnumeration attr = (FndEnumeration) field.getTemplate();
      FndCondition simple_cond = null;
      
      String[] cond_value = splitConditionAndValue(fValue);
      
      boolean _or = false;
      String condition = cond_value[0];      
      String st[] = Str.split(cond_value[1],";");
      ArrayList list = new ArrayList();
      
      for(int i=0; i<st.length; i++)
      {
         try {
            attr.parseString(st[i]);
         } catch (ParseException ex) {
            ex.printStackTrace();
         }
         list.add(attr.toString());
      }
      if(condition.equals("!") && condition.trim().length()==1 && list.size()>0)
         simple_cond = attr.createNotInCondition(list);
      else if(condition.equals("!") && condition.trim().length()==1)
         simple_cond = attr.createIsNullCondition();
      else if(list.size()>0)
         simple_cond = attr.createInCondition(list);
         
      return simple_cond;
   }
   
   /**
    * Create a search condition for integer attributes and returns it as a FndCondition object.
    * @param field ASPField.
    * @param fValue query value for the given attribute.
    * @return FndCondition for integer attribute.
    * @throws ifs.fnd.service.FndException During parsing of condition values.
    */
   static FndCondition integerAttributeCondition(ASPField field, String fValue) throws FndException
   {
      FndInteger attr = (FndInteger) field.getTemplate();
      FndCondition simple_cond = null;
      FndCondition final_cond = null;
      
      String[] cond_value = splitConditionAndValue(fValue);
      
      boolean _or = false;
      String condition = cond_value[0];      
      StringTokenizer st = new StringTokenizer(cond_value[1],";");
      
      while(st.hasMoreTokens())
      {
         simple_cond = null;
         String val = st.nextToken();
         Long value1 = null;
         Long value2 = null;
         
         try{
            if(val.indexOf("..")!=-1)
            {
               value1 = Long.valueOf(val.substring(0,val.indexOf("..")));
               value2 = Long.valueOf(val.substring(val.indexOf("..")+2));
            }
            else
               value1 = Long.valueOf(val);
         }
         catch(Exception e)
         {
            throw new FndException();
         }
         
         if(value1!=null)
         {
            if(condition.equals("<="))
               simple_cond = attr.createLessThanOrEqualCondition(value1);
            else if(condition.equals(">="))
               simple_cond = attr.createGreaterThanOrEqualCondition(value1);
            else if( condition.equals("<>") || condition.equals("!="))
               simple_cond = attr.createNotEqualCondition(value1);
            else if(condition.equals(".."))
               simple_cond = attr.createBetweenCondition(value1, value2);
            else if(condition.equals("!") && condition.trim().length()==1)
               simple_cond = attr.createIsNullCondition();
            else if(condition.equals("<"))
               simple_cond = attr.createLessThanCondition(value1);
            else if(condition.equals(">"))
               simple_cond = attr.createGreaterThanCondition(value1);
            else if(condition.equals("="))
               simple_cond = attr.createEqualCondition(value1);
         }
         
         final_cond = (_or)? final_cond.or(simple_cond) : simple_cond;
         _or = true;
      }
      
      return final_cond;
   }

   /**
    * Create a search condition for decimal/number field and returns it as a FndCondition object.
    * @param field ASPField.
    * @param fValue query value for the given attribute.
    * @return FndCondition for decimal/number attribute.
    * @throws ifs.fnd.service.FndException During parsing of condition values.
    */
   static FndCondition numberAttributeCondition(ASPField field, String fValue) throws FndException
   {
      FndNumber attr = (FndNumber) field.getTemplate();
      FndCondition simple_cond = null;
      FndCondition final_cond = null;
      
      String[] cond_value = splitConditionAndValue(fValue);
      
      boolean _or = false;
      String condition = cond_value[0];      
      StringTokenizer st = new StringTokenizer(cond_value[1],";");
      
      while(st.hasMoreTokens())
      {
         simple_cond = null;
         String val = st.nextToken();
         Double value1=null;
         Double value2=null;
         
         try{
            if(val.indexOf("..")!=-1)
            {
               value1 = Double.valueOf(val.substring(0,val.indexOf("..")));
               value2 = Double.valueOf(val.substring(val.indexOf("..")+2));
            }
            else
               value1 = Double.valueOf(val);
         }
         catch(Exception e)
         {
            throw new FndException();
         }
         
         if(value1!=null)
         {
            if(condition.equals("<="))
               simple_cond = attr.createLessThanOrEqualCondition(value1);
            else if(condition.equals(">="))
               simple_cond = attr.createGreaterThanOrEqualCondition(value1);
            else if( condition.equals("<>") || condition.equals("!="))
               simple_cond = attr.createNotEqualCondition(value1);
            else if(condition.equals(".."))
               simple_cond = attr.createBetweenCondition(value1, value2);
            else if(condition.equals("!") && condition.trim().length()==1)
               simple_cond = attr.createIsNullCondition();
            else if(condition.equals("<"))
               simple_cond = attr.createLessThanCondition(value1);
            else if(condition.equals(">"))
               simple_cond = attr.createGreaterThanCondition(value1);
            else if(condition.equals("="))
               simple_cond = attr.createEqualCondition(value1);
         }
         
         final_cond = (_or)? final_cond.or(simple_cond) : simple_cond;
         _or = true;
      }
      
      return final_cond;
   }
   
/**
    * Create a search condition for boolean field and returns it as a FndCondition object.
    * @param field ASPField.
    * @param fValue query value for the given attribute.
    * @return FndCondition for boolean attribute.
    * @throws ifs.fnd.service.FndException During parsing of condition values.
    */
   static FndCondition booleanAttributeCondition(ASPField field, String fValue) throws FndException
   {
      FndBoolean attr = (FndBoolean) field.getTemplate();
      FndCondition simple_cond = null;
      FndCondition final_cond = null;
      
      String[] cond_value = splitConditionAndValue(fValue);
      
      boolean _or = false;
      String condition = cond_value[0];      
      StringTokenizer st = new StringTokenizer(cond_value[1],";");
      
      while(st.hasMoreTokens())
      {
         simple_cond = null;
         boolean value = false;
         
         try
         {
            value = Boolean.parseBoolean(st.nextToken());
         }
         catch(Exception e)
         {
            throw new FndException();
         }
         
         if( condition.equals("<>") || condition.equals("!="))
            simple_cond = attr.createNotEqualCondition(value);
         else if(condition.equals("!") && condition.trim().length()==1)
            simple_cond = attr.createIsNotNullCondition();
         else if(condition.equals("="))
            simple_cond = attr.createEqualCondition(value);
         
         final_cond = (_or)? final_cond.or(simple_cond) : simple_cond;
         _or = true;
      }
      
      return final_cond;
   }

   /**
    * Create a search condition for timestamp field and returns it as a FndCondition object.
    * @param field ASPField.
    * @param fValue query value for the given attribute.
    * @return FndCondition for timestamp attribute.
    * @throws ifs.fnd.service.FndException During parsing of condition values.
    */
   static FndCondition timestampAttributeCondition(ASPField field, String fValue) throws FndException
   {
      FndTimestamp attr = (FndTimestamp) field.getTemplate();
      FndCondition simple_cond = null;
      FndCondition final_cond = null;
      
      String[] cond_value = splitConditionAndValue(fValue);
      
      boolean _or = false;
      String condition = cond_value[0];      
      StringTokenizer st = new StringTokenizer(cond_value[1],";");
      
      while(st.hasMoreTokens())
      {
         simple_cond = null;
         String val = st.nextToken();
         Timestamp value1 = null;
         Timestamp value2 = null;
         try{
            if(val.indexOf("..")!=-1)
            {
               value1 =  new Timestamp(field.parseDate(val.substring(0,val.indexOf(".."))).getTime());
               value2 =  new Timestamp(field.parseDate(val.substring(val.indexOf("..")+2)).getTime());
            }
            else
               value1 =  new Timestamp(field.parseDate(val).getTime());
         }
         catch(Exception e)
         {
            throw new FndException();
         }
         
         if(value1!=null)
         {
            if(condition.equals("<="))
               simple_cond = attr.createLessThanOrEqualCondition(value1);
            else if(condition.equals(">="))
               simple_cond = attr.createGreaterThanOrEqualCondition(value1);
            else if( condition.equals("<>") || condition.equals("!="))
               simple_cond = attr.createNotEqualCondition(value1);
            else if(condition.equals(".."))
               simple_cond = attr.createBetweenCondition(value1, value2);
            else if(condition.equals("!") && condition.trim().length()==1)
               simple_cond = attr.createIsNullCondition();
            else if(condition.equals("<"))
               simple_cond = attr.createLessThanCondition(value1);
            else if(condition.equals(">"))
               simple_cond = attr.createGreaterThanCondition(value1);
            else if(condition.equals("="))
               simple_cond = attr.createEqualCondition(value1);
         }
         final_cond = (_or)? final_cond.or(simple_cond) : simple_cond;
         _or = true;
      }
      
      return final_cond;
   }

   /**
    * Create a search condition for date attributes and returns it as a FndCondition object.
    * @param field ASPField.
    * @param fValue query value for the given attribute.
    * @return FndCondition for date attribute.
    * @throws ifs.fnd.service.FndException During parsing of condition values.
    */
   static FndCondition dateAttributeCondition(ASPField field, String fValue) throws FndException
   {
      FndDate attr = (FndDate) field.getTemplate();
      FndCondition simple_cond = null;
      FndCondition final_cond = null;
      
      String[] cond_value = splitConditionAndValue(fValue);
      
      boolean _or = false;
      String condition = cond_value[0];      
      StringTokenizer st = new StringTokenizer(cond_value[1],";");
      
      while(st.hasMoreTokens())
      {
         simple_cond = null;
         String val = st.nextToken();
         Date value1 = null;
         Date value2 = null;
         try{
            if(val.indexOf("..")!=-1)
            {
               value1 =  field.parseDate(val.substring(0,val.indexOf("..")));
               value2 =  field.parseDate(val.substring(val.indexOf("..")+2));
            }
            else
               value1 =  field.parseDate(val);
         }
         catch(Exception e)
         {
            throw new FndException();
         }
         
         if(value1!=null)
         {
            if(condition.equals("<="))
               simple_cond = attr.createLessThanOrEqualCondition(value1);
            else if(condition.equals(">="))
               simple_cond = attr.createGreaterThanOrEqualCondition(value1);
            else if( condition.equals("<>") || condition.equals("!="))
               simple_cond = attr.createNotEqualCondition(value1);
            else if(condition.equals(".."))
               simple_cond = attr.createBetweenCondition(value1, value2);
            else if(condition.equals("!") && condition.trim().length()==1)
               simple_cond = attr.createIsNullCondition();
            else if(condition.equals("<"))
               simple_cond = attr.createLessThanCondition(value1);
            else if(condition.equals(">"))
               simple_cond = attr.createGreaterThanCondition(value1);
            else if(condition.equals("="))
               simple_cond = attr.createEqualCondition(value1);
         }
         final_cond = (_or)? final_cond.or(simple_cond) : simple_cond;
         _or = true;
      }
      
      return final_cond;
   }

   /**
    * Create a search condition for time attributes and returns it as a FndCondition object.
    * @param field ASPField.
    * @param fValue query value for the given attribute.
    * @return FndCondition for time attribute.
    * @throws ifs.fnd.service.FndException During parsing of condition values.
    */
   static FndCondition timeAttributeCondition(ASPField field, String fValue) throws FndException
   {
      FndTime attr = (FndTime) field.getTemplate();
      FndCondition simple_cond = null;
      FndCondition final_cond = null;
      
      String[] cond_value = splitConditionAndValue(fValue);
      
      boolean _or = false;
      String condition = cond_value[0];      
      StringTokenizer st = new StringTokenizer(cond_value[1],";");
      
      while(st.hasMoreTokens())
      {
         simple_cond = null;
         String val = st.nextToken();
         Time value1 = null;
         Time value2 = null;
         try{
            if(val.indexOf("..")!=-1)
            {
               value1 =  new Time(field.parseDate(val.substring(0,val.indexOf(".."))).getTime());
               value2 =  new Time(field.parseDate(val.substring(val.indexOf("..")+2)).getTime());
            }
            else
               value1 =  new Time(field.parseDate(val).getTime());
         }
         catch(FndException e)
         {
            throw new FndException();
         }
         
         if(value1!=null)
         {
            if(condition.equals("<="))
               simple_cond = attr.createLessThanOrEqualCondition(value1);
            else if(condition.equals(">="))
               simple_cond = attr.createGreaterThanOrEqualCondition(value1);
            else if( condition.equals("<>") || condition.equals("!="))
               simple_cond = attr.createNotEqualCondition(value1);
            else if(condition.equals(".."))
               simple_cond = attr.createBetweenCondition(value1, value2);
            else if(condition.equals("!") && condition.trim().length()==1)
               simple_cond = attr.createIsNullCondition();
            else if(condition.equals("<"))
               simple_cond = attr.createLessThanCondition(value1);
            else if(condition.equals(">"))
               simple_cond = attr.createGreaterThanCondition(value1);
            else if(condition.equals("="))
               simple_cond = attr.createEqualCondition(value1);
         }
         final_cond = (_or)? final_cond.or(simple_cond) : simple_cond;
         _or = true;
      }
      
      return final_cond;
   }

   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndAttribute that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @throws ifs.fnd.service.FndException During parsing of values.
    * @return FndAttribute with the value set.
    */
   static FndAttribute setAttributeValue(FndAttribute attr, ASPField field, String value) throws FndException
   {
      FndAttributeType attr_type = attr.getType();
 
      try{
         if(attr_type == FndAttributeType.INTEGER)
            attr = setIntegerAttribute(attr, field, value);
         else if(attr_type == FndAttributeType.DECIMAL || attr_type == FndAttributeType.NUMBER)
            attr = setNumberAttribute(attr, field, value);
         else if(attr_type == FndAttributeType.BOOLEAN)
            attr = setBooleanAttribute(attr, field, value);
         else if(attr_type == FndAttributeType.TIMESTAMP)
            attr = setTimestampAttribute(attr, field, value);
         else if(attr_type == FndAttributeType.DATE)
            attr = setDateAttribute(attr, field, value);
         else if(attr_type == FndAttributeType.TIME)
            attr = setTimeAttribute(attr, field, value);
         else if(attr_type == FndAttributeType.ENUMERATION)
            attr = setEnumerationAttribute(attr, field, value);
         else if(attr_type == FndAttributeType.ALPHA)
            attr = setAlphaAttribute(attr, field, value);
         else
            attr = setTextAttribute(attr, field, value);
      }
      catch (Exception fe)
      {
         throw new FndException("FNDACTIVITYUTILPARSEERROR2: Value for &1 is not in a correct format.", field.getLabel());
      }
      return attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndInteger that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @return FndInteger with the value set.
    */
   static FndAttribute setIntegerAttribute(FndAttribute attr, ASPField field, String value)
   {
      FndInteger _attr = (FndInteger) attr;
      Long val = Long.valueOf(value);
      if(val!=null)
      {
         if(_attr.getValue()!=null && _attr.toString().equals(value)) return _attr;
         _attr.include();
         _attr.setValue(val);
      }
      else
         _attr.exclude();
      return _attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndNumber that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @return FndNumber with the value set.
    */
   static FndAttribute setNumberAttribute(FndAttribute attr, ASPField field, String value)
   {
      FndNumber _attr = (FndNumber) attr;
      Double val = Double.valueOf(value);
      if(val!=null)
      {
         if(_attr.getValue()!=null && val==_attr.getValue()) return _attr;
         _attr.include();
         _attr.setValue(val);
      }
      else
         _attr.exclude();
      return _attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndBoolean that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @return FndBoolean with the value set.
    */
   static FndAttribute setBooleanAttribute(FndAttribute attr, ASPField field, String value)
   {
      FndBoolean _attr = (FndBoolean) attr;
      Boolean val = Boolean.valueOf(value);
      if(val!=null)
      {
         if(_attr.getValue()!=null && val==_attr.getValue()) return _attr;
         _attr.include();
         _attr.setValue(val);
      }
      else
         _attr.exclude();
      return _attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndTimestamp that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @throws ifs.fnd.service.FndException During parsing of values.
    * @return FndTimestamp with the value set.
    */
   static FndAttribute setTimestampAttribute(FndAttribute attr, ASPField field, String value) throws FndException
   {
      FndTimestamp _attr = (FndTimestamp) attr;
      Timestamp val = new Timestamp(field.parseDate(value).getTime());
      if(val!=null)
      {
         if(_attr.getValue()!=null && val.getTime()==_attr.getValue().getTime()) return _attr;
         _attr.include();
         _attr.setValue(val);
      }
      else
         _attr.exclude();
      return _attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndDate that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @throws ifs.fnd.service.FndException During parsing of values.
    * @return FndDate with the value set.
    */
   static FndAttribute setDateAttribute(FndAttribute attr, ASPField field, String value) throws FndException
   {
      FndDate _attr = (FndDate) attr;
      Date val = field.parseDate(value);
      if(val!=null)
      {
         if(_attr.getValue()!=null && val.getTime()==_attr.getValue().getTime()) return _attr;
         _attr.include();
         _attr.setValue(val);
      }
      else
         _attr.exclude();
      return _attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndTime that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @throws ifs.fnd.service.FndException During parsing of values.
    * @return FndTime with the value set.
    */
   static FndAttribute setTimeAttribute(FndAttribute attr, ASPField field, String value) throws FndException
   {
      FndTime _attr = (FndTime) attr;
      Time val = new Time(field.parseDate(value).getTime());
      if(val!=null)
      {
         if(_attr.getValue()!=null && val.getTime()==_attr.getValue().getTime()) return _attr;
         _attr.include();
         _attr.setValue(val);
      }
      else
         _attr.exclude();
      return _attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndEnumeration that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @return FndEnumeration with the value set.
    */
   static FndAttribute setEnumerationAttribute(FndAttribute attr, ASPField field, String value)
   {
      FndEnumeration _attr = (FndEnumeration) attr;
      if(value!=null)
      {
         if(value.equals(_attr.toString())) return _attr;
         _attr.include();
         try {
            _attr.parseString(value);
         } catch (ParseException ex) {
            ex.printStackTrace();
         }
      }
      else
         _attr.exclude();
      return _attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndText that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @return FndText with the value set.
    */
   static FndAttribute setTextAttribute(FndAttribute attr, ASPField field, String value)
   {
      FndText _attr = (FndText) attr;
      String val = value;
      if(val!=null)
      {
         if(_attr.getValue()!=null && val.equals(_attr.getValue())) return _attr;
         _attr.include();
         try {
            _attr.setValue(val);
         } catch (ApplicationException ex) {
            ex.printStackTrace();
         }
      }
      else
         _attr.exclude();
      return _attr;
   }
   
   /**
    * Set a value to the attribute and return the attribute.
    * @param attr FndAlpha that is used to set the value.
    * @param field ASPField object used to format the value.
    * @param value Actual value to set in the attribute.
    * @return FndAlpha with the value set.
    */
   static FndAttribute setAlphaAttribute(FndAttribute attr, ASPField field, String value)
   {
      FndAlpha _attr = (FndAlpha) attr;
      String val = value;
      if(val!=null)
      {
         if(_attr.getValue()!=null && val.equals(_attr.getValue())) return _attr;
         _attr.include();
         try {
            _attr.setValue(val);
         } catch (ApplicationException ex) {
            ex.printStackTrace();
         }
      }
      else
         _attr.exclude();
      return _attr;
   }
}
