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
 * File        : FndDebug.java
 * Description : Service class for debugging server code.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek  2004-Dec-17 - Copied from ifs.fnd.base.FndDebug.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.1  2004/12/20 07:47:05  japase
 * Temporary solution for debugging of Records to DBMON
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.base.*;
import ifs.fnd.internal.*;
import ifs.fnd.record.*;
import ifs.fnd.record.serialization.FndBufferUtil;
import ifs.fnd.record.serialization.FndUtil;

import java.text.SimpleDateFormat;
import java.io.PrintStream;
import java.util.*;

/**
 * This service class can be used as help for debugging server code.
 *  A temporary solution for debugging of records to DBMON.
 * Should be removed in the furure and replaced with standard methods from
 * ifs.fnd.base.FndDebug for listing of the record contents to StringBuffer.
 */
public final class FndDebugUtil
{
   private static int indentWidth = 3;
   private static int indentValue = 1;
   private static StringBuffer indentStr;

   /**
    * Prints an array to debug output.
    *
    * @param result
    *           Array to print
    */
   public static void debugArrayRecord( FndAbstractArray result )
   {
      FndAbstractRecord arrayView = FndBufferUtil.createArrayView(result.newRecord());
      FndAttributeInternals.load(((FndArray) arrayView.getAttribute("ARR")), FndAttributeInternals.getInternalRecords(result));
      debugRecord(arrayView);
   }

   /**
    * Prints a record to debug output.
    *
    * @param record
    *           Record to print
    */
   public static void debugRecord( FndAbstractRecord record )
   {
      if(record != null)
      {
         resetDebugIndent();
         StringBuffer message = debugRecord(record, -1, "", "");
         message.insert(0, ':');
         message.insert(0, FndContext.getTaskId());
         printDebug(message);
         resetDebugIndent();
      }
   }

   /**
    * Resets the debugging output indentation to none.
    */
   public static void resetDebugIndent()
   {
      indentValue = 1;
   }

   /**
    * Increase the indentation of debug output by one level.
    */
   public static void pushDebugIndent()
   {
      indentValue += indentWidth;
   }

   /**
    * Decrease the debugging output indentation by one level.
    */
   public static void popDebugIndent()
   {
      if(indentValue > indentWidth)
         indentValue -= indentWidth;
   }

   private static StringBuffer debugRecord(FndAbstractRecord record, int itemNo, String parentName, String arrayType)
   {
      FndAttribute element;
      FndAbstractArray elementVector;
      String value;
      StringBuffer debugMessage = new StringBuffer();
      FndAbstractRecord elementRecord;
      FndAbstractRecord conditionRecord;

      String state = record.getState() == null ? "" : record.getState().toString();
      if(record.isDirty())
         state = "(DIRTY) " + state;

      if(itemNo < 0)
      {
         debugMessage.append(formatLine("Fnd_View &1 &2", record.getName(), state, "", "", ""));
      }
      else
         debugMessage.append(formatLine("&1:Fnd_View &2 &3 element of &4.&5", String.valueOf(itemNo), record.getName(), state, parentName, arrayType));
      FndAbstractRecord.Iterator records = record.records();
      while(records.hasNext())
      {
         conditionRecord = records.next();
         if((conditionRecord instanceof FndCondition) || (conditionRecord instanceof FndSimpleCondition))
            debugMessage.append(formatLine("CONDITION:&1", debugAdditionalCondition(conditionRecord).toString(), "", "", "", ""));
      }
      //         for(int i=0 ; i<record.getRecords().size();i++) {
      //            conditionRecord=(FndAbstractRecord)record.getRecords().get(i);
      //            if((conditionRecord instanceof FndCondition)||(conditionRecord
      // instanceof FndSimpleCondition))
      //               debugMessage.append(formatLine("CONDITION:&1",debugAdditionalCondition(conditionRecord).toString(),"","","",""));
      //         }
      pushDebugIndent();
      FndAttribute.Iterator attributes = record.attributes();
      while(attributes.hasNext())
      {
         element = attributes.next();
         //for(int i=0 ; i<record.getAttributes().size();i++) {
         //   element=(FndAttribute)record.getAttributes().get(i);
         if(element.isVector())
         {
            if(element.exist())
            {
               elementVector = (FndAbstractArray) element;
               debugMessage.append(formatLine("&1 &2 (&3 elements):", attributeType(element), attributeName(element), String.valueOf(elementVector.size()), "", ""));
               for(int j = 0; j < elementVector.size(); j++)
               {
                  pushDebugIndent();
                  debugMessage.append(debugRecord(FndAttributeInternals.internalGet(elementVector, j), j + 1, record.getName(), element.getName()).toString());
                  popDebugIndent();
               }
            }
            else
               debugMessage.append(formatLine("Array &1 Non_Existent", attributeName(element), "", "", "", ""));
         }
         else
         {
            if(element instanceof FndSimpleArray)
            {
               debugSimpleArray(debugMessage, (FndSimpleArray) element);
            }
            else if(!element.exist())
            {
               debugMessage.append(formatLine("&1 &2 Non_Existent", attributeType(element), attributeName(element), "", "", ""));
            }
            else if(FndAttributeInternals.internalGetValue(element) == null)
            {
               debugMessage.append(formatLine("&1 &2 Null", attributeType(element), attributeName(element), "", "", ""));
            }
            else if(element.getType().equals(FndAttributeType.AGGREGATE))
            {
               elementRecord = FndAttributeInternals.internalGetRecord((FndAbstractAggregate) element);
               debugMessage.append(formatLine("&1 &2:", attributeType(element), attributeName(element), "", "", ""));
               pushDebugIndent();
               debugMessage.append(debugRecord(elementRecord, -1, elementRecord.getName(), elementRecord.getName()).toString());
               popDebugIndent();
            }
            else if(element.getType().equals(FndAttributeType.ENUMERATION))
            {
               value = element.toString();
               debugMessage.append(formatLine("&1 &2 = &3", attributeType(element), attributeName(element), value, "", ""));
            }
            else if(element.getType().equals(FndAttributeType.BINARY))
            {
               byte[] data = ((FndBinary) element).getValue();
               byte[] d2 = new byte[((data.length <= 16) ? data.length : 16)];
               System.arraycopy(data, 0, d2, 0, d2.length);
               value = FndUtil.toHexText(d2);
               if(data.length > d2.length)
                  value += "...";
               debugMessage.append(formatLine("&1 &2 = 16#&3# (&4 bytes)", attributeType(element), attributeName(element), value, Integer.toString(data.length), ""));
            }
            else
            {
               if(FndAttributeInternals.internalGetValue(element) == null)
                  value = null;
               else
                  value = FndAttributeInternals.internalGetValue(element).toString();
               debugMessage.append(formatLine("&1 &2 = &3", attributeType(element), attributeName(element), value, "", ""));
            }
         }
      }
      debugReferences(record, debugMessage);
      popDebugIndent();
      return debugMessage;
   }

   private static void debugSimpleArray(StringBuffer debugMessage, FndSimpleArray arr)
   {
      FndSimpleArray.ElementType elemType = arr.getElementType();
      String attrType = "SimpleArray of " + elemType.getJavaType();
      if(arr.isDirty())
         attrType += " (DIRTY)";

      String attrName = arr.getName();

      if(!arr.exist())
      {
         debugMessage.append(formatLine("&1 &2 Non_Existent", attrType, attrName, "", "", ""));
      }
      else if(arr.isNull())
      {
         debugMessage.append(formatLine("&1 &2 Null", attrType, attrName, "", "", ""));
      }
      else
      {
         String value;
         try
         {
            if(elemType == FndSimpleArray.STRING)
               value = formatStringArray(arr.getStringArray());
            else if(elemType == FndSimpleArray.DATE)
               value = formatDateArray(arr.getDateArray());
            else if(elemType == FndSimpleArray.DOUBLE)
               value = formatDoubleArray(arr.getDoubleArray());
            else if(elemType == FndSimpleArray.BOOLEAN)
               value = formatBooleanArray(arr.getBooleanArray());
            else if(elemType == FndSimpleArray.LONG)
               value = formatLongArray(arr.getLongArray());
            else if(elemType == FndSimpleArray.BINARY)
               value = formatBinaryArray(arr.getBinaryArray());
            else
               value = "Invalid FndSimpleArray.ElementType: " + elemType;
         }
         catch(ParseException e)
         {
            throw new IfsRuntimeException(e, "Error during conversion of SimpleArray: &1", e.toString());
         }

         debugMessage.append(formatLine("&1 &2 = &3", attrType, attrName, value, "", ""));
      }
   }

   private static String formatLine(String line, String p1, String p2, String p3, String p4, String p5)
   {
      // HEDJSE: Changed to return String for JDK 1.3 compatability.
      StringBuffer message = new StringBuffer(line);
      message = formatMessage(message, p1, p2, p3, p4, p5);
      message.insert(0, indent());
      return message.append('\n').toString();
   }

   private static String attributeType(FndAttribute attr)
   {
      String name = attr.getType().getName();
      if(attr.isDirty())
         name = name + " (DIRTY)";
      String info = attr.getInvalidValueInfo();
      if(info != null)
         name = name + " (INVALID: " + info + ")";
      return name;
   }

   private static String attributeName(FndAttribute attr)
   {
      String name = attr.getName();
      String state = attr.getState();
      return name + ("".equals(state) ? "" : "/"+state);
   }

   private static void debugReferences(FndAbstractRecord rec, StringBuffer buf)
   {
      int count = rec.getCompoundReferenceCount();
      if(count == 0)
         return;
      buf.append(formatLine("References (&1):", String.valueOf(count), "", "", "", ""));
      pushDebugIndent();
      boolean hasOwnParentKey = false;
      for(int i = 0; i <= count; i++)
      {
         //
         // the loop for i==count handles not-own parentKey reference in a
         // detail
         // record that iherits part of its primary key from parent
         //
         if(i == count && hasOwnParentKey)
            break;
         FndCompoundReference ref = i < count ? rec.getCompoundReference(i) : rec.getParentKey();
         if(ref == null)
            break;
         FndAttribute.Iterator refIterator = ref.iterator();
         StringBuffer attrList = new StringBuffer();
         while(refIterator.hasNext())
         {
            FndAttribute att = refIterator.next();
            if(attrList.length() > 0)
               attrList.append(", ");
            FndAbstractRecord parent = att.getParentRecord();
            if(parent != null && parent != rec)
               attrList.append(parent.getType() + ".");
            attrList.append(att.getName());
         }
         String isA = "";
         if(ref == rec.getPrimaryKey() && ref == rec.getParentKey())
         {
            isA = " Primary_Key, Parent_Key";
            hasOwnParentKey = true;
         }
         else if(ref == rec.getPrimaryKey())
         {
            isA = " Primary_Key";
         }
         else if(ref == rec.getParentKey())
         {
            isA = " Parent_Key";
            hasOwnParentKey = true;
         }

         buf.append(formatLine("&1 (&2)&3", ref.getName(), attrList.toString(), isA, "", ""));
      }
      popDebugIndent();
   }

   private static String formatStringArray(String[] arr)
   {
      if(arr == null)
         return "null";

      StringBuffer buf = new StringBuffer();
      buf.append('{');
      for(int i = 0; i < arr.length; i++)
      {
         if(!appendNextElement(buf, i))
            break;
         String value = arr[i];

         if(value == null)
         {
            buf.append("null");
            continue;
         }

         if(value.length() > MAX_ELEM_SIZE)
            value = value.substring(0, MAX_ELEM_SIZE) + "...";
         buf.append(value);
      }
      buf.append("} (" + arr.length + " elements)");
      return buf.toString();
   }

   private static String formatDateArray(Date[] arr)
   {
      if(arr == null)
         return "null";

      SimpleDateFormat dateFormat = FndContext.getCurrentTimestampFormat();

      StringBuffer buf = new StringBuffer();
      buf.append('{');
      for(int i = 0; i < arr.length; i++)
      {
         if(!appendNextElement(buf, i))
            break;
         buf.append(arr[i] == null ? "null" : dateFormat.format(arr[i]));
      }
      buf.append("} (" + arr.length + " elements)");
      return buf.toString();
   }

   private static String formatDoubleArray(double[] arr)
   {
      if(arr == null)
         return "null";

      StringBuffer buf = new StringBuffer();
      buf.append('{');
      for(int i = 0; i < arr.length; i++)
      {
         if(!appendNextElement(buf, i))
            break;
         buf.append("" + arr[i]);
      }
      buf.append("} (" + arr.length + " elements)");
      return buf.toString();
   }

   private static String formatBooleanArray(boolean[] arr)
   {
      if(arr == null)
         return "null";

      StringBuffer buf = new StringBuffer();
      buf.append('{');
      for(int i = 0; i < arr.length; i++)
      {
         if(!appendNextElement(buf, i))
            break;
         buf.append("" + arr[i]);
      }
      buf.append("} (" + arr.length + " elements)");
      return buf.toString();
   }

   private static String formatLongArray(long[] arr)
   {
      if(arr == null)
         return "null";

      StringBuffer buf = new StringBuffer();
      buf.append('{');
      for(int i = 0; i < arr.length; i++)
      {
         if(!appendNextElement(buf, i))
            break;
         buf.append("" + arr[i]);
      }
      buf.append("} (" + arr.length + " elements)");
      return buf.toString();
   }

   private static String formatBinaryArray(byte[][] arr)
   {
      if(arr == null)
         return "null";

      StringBuffer buf = new StringBuffer();
      buf.append('{');
      for(int i = 0; i < arr.length; i++)
      {
         if(!appendNextElement(buf, i))
            break;

         byte[] data = arr[i];

         if(data == null)
         {
            buf.append("null");
            continue;
         }

         byte[] d2 = new byte[((data.length <= 16) ? data.length : 16)];
         System.arraycopy(data, 0, d2, 0, d2.length);
         String value = FndUtil.toHexText(d2);
         if(data.length > d2.length)
            value += "...";
         buf.append("16#" + value + "# (" + data.length + " bytes)");
      }
      buf.append("} (" + arr.length + " elements)");
      return buf.toString();
   }

   private static StringBuffer formatMessage(StringBuffer message, String p1, String p2, String p3, String p4, String p5)
   {
      StringBuffer val;
      int index = 0;

      for(int i = 1; i < 6; i++)
      {
         val = new StringBuffer("&");
         val.append(i);
         index = message.toString().indexOf(val.toString());
         if(index >= 0)
         {
            message.delete(index, index + 2);
            switch(i)
            {
               case 1:
                  if(p1 != null)
                     message.insert(index, p1);
                  break;
               case 2:
                  if(p2 != null)
                     message.insert(index, p2);
                  break;
               case 3:
                  if(p3 != null)
                     message.insert(index, p3);
                  break;
               case 4:
                  if(p4 != null)
                     message.insert(index, p4);
                  break;
               case 5:
                  if(p5 != null)
                     message.insert(index, p5);
                  break;
               default:
                  break;
            }
         }
      }
      return message;
   }

   private static final int MAX_LINE_SIZE = 2000;

   private static final int MAX_ELEM_SIZE = 500;

   private static boolean appendNextElement(StringBuffer buf, int i)
   {
      if(i > 0)
         buf.append(", ");
      if(buf.length() > MAX_LINE_SIZE)
      {
         buf.append("...");
         return false;
      }
      return true;
   }

   private static StringBuffer debugAdditionalCondition(FndAbstractRecord record)
   {
      if(record instanceof FndSimpleCondition)
      {
         String condName = "";
         List condValue = new ArrayList();
         FndAttribute element;
         FndAlpha condOperator = null;
         FndAttribute.Iterator attributes = record.attributes();
         while(attributes.hasNext())
         {
            element = attributes.next();
            //for(int i=0;record.getAttributes().size()>i;i++) {
            //   element =(FndAttribute)record.getAttributes().get(i);
            if(element.getName().equals("NAME"))
               condName = element.toString();
            else if(element.getName().equals("VALUE"))
            {
               if(element != null)
               {
                  Object attrValue = FndAttributeInternals.internalGetValue(element);
                  if(attrValue == null)
                     condValue.add(null);
                  else
                     condValue.add(attrValue.toString());
               }
            }
            else if(element.getName().equals("OPERATOR"))
               condOperator = (FndAlpha) element;
         }
         if(FndQueryOperator.isBetweenOperator(condOperator))
            return formatMessage(new StringBuffer("(&1 &2 &3 &4)"), condName, condValue.get(0).toString(), condOperator.getValue(), condValue.get(1).toString(), "");
         else if(FndQueryOperator.isInOperator(condOperator))
         {
            StringBuffer value = new StringBuffer(condValue.get(0).toString());
            for(int i = 1; i < condValue.size(); i++)
               value.append(",").append(condValue.get(i));
            return formatMessage(new StringBuffer("(&1 &2 [&3])"), condName, condOperator.getValue(), value.toString(), "", "");
         }
         else if(condValue.size() == 0)
            return formatMessage(new StringBuffer("(&1 &2 &3)"), condName, condOperator.getValue(), "Null", "", "");
         else
            return formatMessage(new StringBuffer("(&1 &2 &3)"), condName, condOperator.getValue(), condValue.get(0).toString(), "", "");
      }
      else if(record instanceof FndDetailCondition)
      {
         StringBuffer debugMessage = new StringBuffer();
         String category = record.getAttribute("CATEGORY").toString();
         FndAbstractRecord detail = (FndAbstractRecord) FndAttributeInternals.internalGetValue(record.getAttribute("DETAIL"));
         debugMessage.append(category + "\n");
         pushDebugIndent();
         debugMessage.append(debugRecord(detail, -1, record.getName(), detail.getName()).toString());
         popDebugIndent();
         debugMessage.append(indent());
         return debugMessage;
      }
      else if(record instanceof FndCondition)
      {
         FndCondition cond = (FndCondition) record;
         FndAbstractRecord left = (FndAbstractRecord) FndAttributeInternals.internalGetValue(cond.left);
         FndAbstractRecord right = (FndAbstractRecord) FndAttributeInternals.internalGetValue(cond.right);
         return formatMessage(new StringBuffer("(&1 &2 &3)"), debugAdditionalCondition(left).toString(), cond.category.getValue(), debugAdditionalCondition(right).toString(), "", "");
      }
      else
         return null;
   }

   /**
    * Used by other debugging functions to create a string with correct
    * indentation.
    *
    * @return A string containing blank spaces for indentation
    */
   public static String indent()
   {
      if(indentStr == null)
      {
         indentStr = new StringBuffer();
         for(int i = 0; i < 200 * indentWidth; i++)
            indentStr.append(" ");
      }
      return indentStr.substring(0, indentValue);
   }

   /**
    * Prints a debug message.
    *
    * @msg message to print
    */
   private static void printDebug(StringBuffer msg)
   {
      Util.debug(msg.toString());
   }
}