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
 * File        : FndDataRow.java
 * Description : Specific implementation for FndAbstractArray data.
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2008/06/26 mapelk - Bug 74852, Created. Programming Model for Activities.
 *  
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.DataFormatter;
import ifs.fnd.buffer.ItemNotFoundException;
import ifs.fnd.buffer.ServerFormatter;
import ifs.fnd.internal.FndAttributeInternals;
import ifs.fnd.internal.FndRecordInternals;
import ifs.fnd.record.FndAbstractAggregate;
import ifs.fnd.record.FndAbstractRecord;
import ifs.fnd.record.FndAggregate;
import ifs.fnd.record.FndAttribute;
import ifs.fnd.service.FndException;
import java.util.StringTokenizer;

/**
 * Specific implementation for FndAbstractRecord data.
 */
public class FndDataRow extends AbstractDataRow{
   
   FndAbstractRecord rec;
   boolean is_null;
  
   /** Creates a new instance of FndDataRow */
   FndDataRow(FndAbstractRecord rec) 
   {
      this.rec = rec;
      if (rec==null)
         is_null=true;
   }
   
   public String getString(ASPField field,String default_value)
   {
      String value = rec.getAttribute(field.getName()).toString();
      return value==null?default_value:value;
   }
   
   String convertToClientString(ASPField field) throws Exception
   {
      return convertToClientString(field,field.getDataFormatter());
   }
   
   String convertToClientString(ASPField field, DataFormatter formatter) throws Exception
   {
      String value = getValue(field);
      if(value==null) return null;
      ServerFormatter server_formatter = field.getASPManager().getServerFormatter();

      int server_type_id = DataFormatter.getBaseTypeId(formatter.getTypeId());
      Object obj = server_formatter.parse(value, server_type_id);
      return formatter.format(obj);
   }
   
   public boolean isNULL()
   {
      return is_null;
   }
   
   public int countColumns()
   {
      return rec.getAttributeCount();
   }
   
   public int getAttributePosition(String name)
   {
      return -1;
   }
   
   public String getValue(ASPField f) throws ItemNotFoundException
   {
      return getValue(f.getDbName());
   } 
   
   public String getValue(String name) throws ItemNotFoundException
   {
      FndAttribute attr;
      if (name.indexOf(".")<0)
         attr = rec.getAttribute(name); 
      else
      {
         StringTokenizer token = new StringTokenizer(name,".");
         String reference = token.nextToken();
         FndAttribute temp = rec.getAttribute(reference);
         if(temp==null) return "";
         FndAbstractRecord aggregate = FndAttributeInternals.internalGetRecord((FndAbstractAggregate)rec.getAttribute(reference));
         attr = aggregate.getAttribute(token.nextToken());
      }
            
      return attr==null?null:attr.toString();
   }   
   
   public String getValueAt(int i) throws ItemNotFoundException   
   {
      FndAttribute attr = rec.getAttribute(i);      
      return attr==null?null:attr.toString();
   }
   
   public String getType()
   {
      return (String)FndRecordInternals.getAttributeValue(rec,FndDataSet.__FNDWEB_INTERNAL_TYPE);
   }
   
   public String getStatus()
   {
      return (String)FndRecordInternals.getAttributeValue(rec,FndDataSet.__FNDWEB_INTERNAL_TAG);
   }   
   
   public String getCustomValue(String name)
   {
      return (String)FndRecordInternals.getAttributeValue(rec,name);
   }
   
   void setStatus(String tag)
   {
      setCustomValue(FndDataSet.__FNDWEB_INTERNAL_TAG, tag);
   }
   
   void setType(String type)
   {
      setCustomValue(FndDataSet.__FNDWEB_INTERNAL_TYPE, type);
   }
   
   void setCustomValue(String name, String value)
   {
      FndAttribute tag_attr = rec.getAttribute(name);
      if (tag_attr == null)
      {
         tag_attr = FndAttributeInternals.newAttribute(name);
         FndRecordInternals.add(rec,tag_attr);
      }
      tag_attr.setNonExistent();
      tag_attr.setDirty(false);
      FndAttributeInternals.internalSetValue(tag_attr,value);
   }
}
