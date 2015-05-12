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
 * File        : LocalAddressField.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H    2002-Jan-07  created.
 *    Ramila H    2002-Jan-30  Made changes to support LOVs and thier in parameters.
 *                             Also reduced the size of the query string.
 *    Ramila H    2002-Feb-18  modified getAddressFieldList method not to pass country desc.
 *    Ramila H    2002-Mar-05  implemented clone() method.
 *    Rifki R     2002-Nov-20  modified getLabels(), used getJSLabel() instaed of getLabel(). 
 *    Ramila H    2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    Chandana d  2004-May-12 - Updated for the use of new style sheets.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2007/04/20 rahelk Bug id 63951. passed label with translation key to address dlg.
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

import java.io.*;
import java.util.*;

public class LocalizedAddressField
{
   ASPField[] field_list;
   String[] field_list_names;
   private String address_label ="";
   //display_address_layout = "&ADDRESS1\n&ADDRESS2\n&ZIP_CODE\n&CITY\n&STATE\n&COUNTY\n&COUNTRY_CODE - &COUNTRY";
   private String display_address_layout = "&ADDRESS1\n&ADDRESS2\n&ZIP_CODE\n&CITY\n&STATE\n&COUNTY\n&COUNTRY_CODE";
   private int address_box_row = 8;
   private int address_box_col = 50;
   private String message = "";
   private String line_break = "\r\n";

   private String edit_address_layout = "R1=1,C1=1,W1=4^R2=2,C2=1,W2=4^R3=3,C3=1,W3=4^R4=4,C4=1,W4=4^R5=5,C5=1,W5=4^R6=6,C6=1,W6=4^R7=7,C7=1,W7=4^R8=8,C8=1,W8=4";
   private transient AutoString html = new AutoString();
   private String tool_tip;

   private String dynamic_def_key;
   private Vector non_address_fields_list = new Vector();


   public LocalizedAddressField()
   {
   }

   /**
    * Initialize this class. Calls setLayouts() to initialize display and edit layouts
    * @param add1 ASPField corresponding to ADDRESS1
    * @param add2 ASPField corresponding to ADDRESS2
    * @param zip_code ASPField corresponding to ZIP_CODE
    * @param city ASPField corresponding to CITY
    * @param state ASPField corresponding to STATE
    * @param county ASPField corresponding to COUNTY
    * @param country_code ASPField corresponding to COUNTRY_CODE
    * @param country ASPField corresponding to COUNTRY
    * @param address_label Translatable label shown in HTML
    * @param mgr ASPManager
    *
    * @see #setLayouts
    * @see ASPField
    * @see ASPBlockLayout
    * @see ASPManager
    */
   public void construct(ASPField add1,
                         ASPField add2,
                         ASPField zip_code, 
                         ASPField city, 
                         ASPField state, 
                         ASPField county, 
                         ASPField country_code,
                         ASPField country,
                         String address_label,
                         ASPManager mgr)

   {
      field_list = new ASPField[8];
      field_list[0] = add1;
      field_list[1] = add2;
      field_list[2] = zip_code;
      field_list[3] = city;
      field_list[4] = state;
      field_list[5] = county;
      field_list[6] = country_code;
      field_list[7] = country;
      

      field_list_names = new String[8];
      field_list_names[0] = add1.getName();
      field_list_names[1] = add2.getName();
      field_list_names[2] = zip_code.getName();
      field_list_names[3] = city.getName();
      field_list_names[4] = state.getName();
      field_list_names[5] = county.getName();
      field_list_names[6] = country_code.getName();
      field_list_names[7] = country.getName();

      if (address_label != null)
         this.address_label = mgr.translate(address_label);
      else
         this.address_label = mgr.translate(add1.getLabel());

      message = mgr.translate("FNDASPLOCALADDFLDMSG: To ENTER/EDIT these values press the address icon");
      tool_tip = mgr.translate("FNDASPLOCALADDFLDTOOLTIP: Edit address");
      

      setDynamicDefKey(mgr.getASPPage().getPoolKey());

      //setLayouts(mgr);

   }


   protected LocalizedAddressField clone(ASPBlock block) throws FndException
   {
      try
      {
         LocalizedAddressField l = (LocalizedAddressField)Class.forName(this.getClass().getName()).newInstance();
         l.address_label = address_label;
         l.dynamic_def_key = dynamic_def_key;
         l.field_list_names = field_list_names;
         l.non_address_fields_list = non_address_fields_list;
         l.message = message;
         l.tool_tip = tool_tip;
         
         ASPField[] fld_arr = block.getFields();
         l.field_list = new ASPField[8];
   
         for( int i=0; i<fld_arr.length ; i++ )
         {
            ASPField field = fld_arr[i];
            if( field.getName().equals(field_list[0].getName()) )
               l.field_list[0] = field;
            else if( field.getName().equals(field_list[1].getName()) )
               l.field_list[1] = field;
            else if( field.getName().equals(field_list[2].getName()) )
               l.field_list[2] = field;
            else if( field.getName().equals(field_list[3].getName()) )
               l.field_list[3] = field;
            else if( field.getName().equals(field_list[4].getName()) )
               l.field_list[4] = field;
            else if( field.getName().equals(field_list[5].getName()) )
               l.field_list[5] = field;
            else if( field.getName().equals(field_list[6].getName()) )
               l.field_list[6] = field;
            else if( field.getName().equals(field_list[7].getName()) )
               l.field_list[7] = field;
         }

         return l;
      }
      catch (ClassNotFoundException e)
      {
         return null;
      }
      catch (Exception e)
      {
         return null;
      }

   }

   /**
    * Sets the DISPLAY and EDIT layouts by calling the methods 
    * setAddressDisplayLayout and setAddressEditLayout respectively.
    *
    * @param mgr ASPManager.
    * @see #setAddressDisplayLayout
    * @see #setAddressEditLayout
    * @see #getAddressDisplayLayout
    * @see #getAddressEditLayout
    */
   protected void setLayouts(ASPManager mgr)
   {
      String temp_display;
      String temp_edit;

      temp_display = mgr.getASPConfig().getAddressDisplayLayout();
      temp_edit = mgr.getASPConfig().getAddressEditLayout();

      if (! mgr.isEmpty(temp_display))
         setAddressDisplayLayout(temp_display);
      
      if (! mgr.isEmpty(temp_edit))
         setAddressEditLayout(temp_edit);
   }



   protected void setLayouts(ASPManager mgr, String country_code)
   {
   }

   /**
    * Return the localized address fields label used by ASPBlockLayout in SINGLE, NEW and EDIT layouts
    * @see #isFirstAddressField
    * @see ASPBlockLayout
    */
   public String getAddressLabel()
   {
      return address_label;
   }

   /**
    * Return the list of fields making up the localized address field
    */
   protected String[] getFieldListNames()
   {
      return field_list_names;
   }

   /**
    * Return the list of fields making up the localized address field
    */
   protected ASPField[] getFieldList()
   {
      return field_list;
   }

   /**
    * Set the localized address display layout.
    * @param display_layout display layout string.
    * Line breaks denoted either by "^" or "\r\n".
    * <pre>
    * Display layout example:
    *    &ADDRESS1
    *    &ADDRESS2
    *    &ZIP_CODE - &CITY
    *    &COUNTY
    *    &COUNTRY_CODE - &COUNTRY
    * </pre>    
    * @see #getAddressDisplayLayout
    * @see #setAddressEditLayout
    * @see #getAddressEditLayout
    */
   protected void setAddressDisplayLayout(String display_layout)
   {
      StringTokenizer st; 

      if (display_layout.indexOf("^") > -1)
         st = new StringTokenizer(display_layout,"^");  //ASPConfig
      else
         st = new StringTokenizer(display_layout,"\r\n");  //Enterprise
      
      String temp = "";
      
      while (st.hasMoreTokens())
      {
         temp += st.nextToken() + "\n";
      }

      if (!"".equals(temp))
      {
         display_layout = temp.substring(0,temp.length()-1);
      }

      display_address_layout = display_layout;
   }

   /**
    * Returns the localized address display layout.
    * @see #setAddressDisplayLayout
    * @see #getAddressEditLayout
    * @see #setAddressEditLayout
    */
   protected String getAddressDisplayLayout()
   {
      return display_address_layout;
   }

   /**
    * Set the localized address edit layout.This will change the fileds in the address dialog.
    * @param edit_layout edit layout string.
    * <pre>
    * Edit layout example:
    *    $R1=1
    *    $C1=1
    *    $W1=4
    *    $R2=2
    *    $C2=1
    *    $W2=4
    *    $R3=3
    *    $C3=1
    *    $W3=4
    *    $R4=4
    *    $C4=1
    *    $W1=4
    * and so on.
    * </pre>    
    * You can omit any of the fields by setting zero (0) to either the row (R) or column (c).
    * @see #getAddressEditLayout
    * @see #setAddressDisplayLayout
    * @see #getAddressDisplayLayout
    */
   protected void setAddressEditLayout(String edit_layout)
   {

      edit_layout = edit_layout.substring(edit_layout.indexOf("$R1")+1);
      
      StringTokenizer st = new StringTokenizer(edit_layout,"\n");
      String temp = "";
      
      //Support both ASPConfig value and Enterprise value
      while (st.hasMoreTokens() && st.countTokens() > 1)
      {
         //convert to this format R1=1,C1=1,W1=3.7^R2=2,C2=1,W2=3.7^...
         temp += Str.replace(st.nextToken(),"$","") + Str.replace(st.nextToken(),"$",",") + Str.replace(st.nextToken(),"$",",") +"^"; 
      }

      if (!"".equals(temp))
      {
         edit_layout = temp.substring(0,temp.length()-1);
      }

      edit_address_layout = edit_layout;
   }


   /**
    * Returns the localized address edit layout.
    * @see #setAddressEditLayout
    * @see #getAddressDisplayLayout
    * @see #setAddressDisplayLayout
    */
   protected String getAddressEditLayout()
   {
      return edit_address_layout;
   }



   private String getLOVViews()
   {
      String lov_views = "";
      String view_name;

      for (int i=0; i<field_list.length; i++)
      {
         view_name = field_list[i].getLOVView();
         lov_views += ((view_name!=null)?view_name:"") + "^";
      }
      
      lov_views = lov_views.substring(0,lov_views.length()-1);

      return lov_views;
   }


   private String getLOVProperties()
   {
      String lov_property = "";
      String where_prop = "";

      for(int i=0; i<field_list.length; i++)
      {
         where_prop = field_list[i].getLOVProperty("WHERE");
         lov_property += ((where_prop!=null)?where_prop:"") + "^";
      }
               
      lov_property = lov_property.substring(0,lov_property.length()-1);

      return lov_property;
   }
   
   
   private String getLOVInParameters()
   {
      String lov_in_parameters = "";
      String in_parameters;

      for(int i=0; i<field_list.length; i++)
      {
         in_parameters = field_list[i].getLOVInParams();
         lov_in_parameters += ((in_parameters!=null)?in_parameters:"") + "^";

         if (in_parameters!=null)
            setNonAddressFields(in_parameters);
      }

      lov_in_parameters = lov_in_parameters.substring(0,lov_in_parameters.length()-1);

      return lov_in_parameters;
   }

   
   private void setNonAddressFields(String parameter_list)
   {
      StringTokenizer st = new StringTokenizer(parameter_list,",");
      String field_name;

      while (st.hasMoreTokens())
      {
         field_name = st.nextToken();

         if (!isAddressField(field_name) && !non_address_fields_list.contains(field_name))
            non_address_fields_list.add(field_name);
      }
   }

   private boolean isAddressField(String field_name)
   {
      for(int i=0; i<field_list.length; i++)
         if(field_name.equals(field_list[i].getName()))
            return true;

      return false;
   }

   
   private String getNonAddressFieldList()
   {
      String field_list = "";

      for (int i=0; i<non_address_fields_list.size(); i++)
         field_list += (String) non_address_fields_list.elementAt(i) + "^";
          
      
      if (field_list.length() > 0)
         return field_list.substring(0,field_list.length()-1);
      else
         return field_list;
   }


   private String getNonAddressFieldValues()
   {
      String field_values = "";
      String field_name = "";

      for (int i=0; i<non_address_fields_list.size(); i++)
      {
         field_name = (String) non_address_fields_list.elementAt(i);
         field_values += "+ getValue_('"+field_name+"',i) +\"^\"";
      }
         
      if (field_values.length() > 0)
         return field_values.substring(0,field_values.length()-4) + ";";
      else
         return field_values;
   }



   private String getAddressFieldList()
   {
      String address_field_list = "";

      // exclude country description field.
      for (int i=0; i<field_list.length-1; i++)
         address_field_list += field_list[i].getName() + "^";
      
      return address_field_list.substring(0,address_field_list.length()-1);
   }

   
   private void setDynamicDefKey(String key)
   {
      key = key.substring(0,key.indexOf("."));
      key += "/" + field_list[0].getName();

      dynamic_def_key = key;
   }


   private String getDynamicDefKey(String country_code_val)
   {
      //return (dynamic_def_key + (country_code_val!=null?country_code_val:""));
      return dynamic_def_key;
   }



   private String getAddressDisplayString(String add1_val,
                                          String add2_val,
                                          String zip_code_val, 
                                          String city_val, 
                                          String state_val, 
                                          String county_val, 
                                          String country_code_val,
                                          String country_val)
   {
      String display_address_values = "";

      display_address_values = display_address_layout;

      display_address_values = Str.replace(display_address_values,"&ADDRESS1",add1_val);
      display_address_values = Str.replace(display_address_values,"&ADDRESS2",add2_val);
      display_address_values = Str.replace(display_address_values,"&ZIP_CODE",zip_code_val);
      display_address_values = Str.replace(display_address_values,"&CITY",city_val);
      display_address_values = Str.replace(display_address_values,"&STATE",state_val);
      display_address_values = Str.replace(display_address_values,"&COUNTY",county_val);
      display_address_values = Str.replace(display_address_values,"&COUNTRY_CODE",country_code_val);
      display_address_values = Str.replace(display_address_values,"&COUNTRY",country_val);

      return display_address_values;
   }

   /**
    * Check whether this is the first address field. Used as a place holder for ASPBlockLayout
    * @param field_name address field name
    * @see #getAddressLabel
    * @see ASPBlockLayout
    */
   public boolean isFirstAddressField(String field_name)
   {
      return field_name.equals(field_list[0].getName());
   }


   private String getTextAreaName()
   {
      return "__DISPLAY_ADDRESS_"+field_list[0].getName();
   }


   private int getRowCount()
   {
      StringTokenizer st = new StringTokenizer(getAddressDisplayLayout(),"\n");
      address_box_row = st.countTokens();

      return address_box_row;
   }


   /**
    * Return the HTML code for the localized address text area.
    * @param add1_val value of ADDRESS1
    * @param add2_val value of ADDRESS2
    * @param zip_code_val value of ZIP_CODE
    * @param city_val value of CITY
    * @param state_val value of STATE
    * @param county_val value of COUNTY
    * @param country_code_val value of COUNTRY_CODE
    * @param country_val value of COUNTRY
    * @param editabl_flag enable/disable the icon to open the address dialog
    * @param explorer flag to check browser type
    *
    * @see #getAddressLabel
    * @see ASPBlockLayout
    */
   protected String getAddressColumn(String add1_val,
                                     String add2_val,
                                     String zip_code_val, 
                                     String city_val, 
                                     String state_val, 
                                     String county_val, 
                                     String country_code_val,
                                     String country_val, int layout_mode, boolean explorer,ASPManager mgr)
   {
      
      boolean editable = false;

      switch (layout_mode) {
      case ASPBlockLayout.SINGLE_LAYOUT: editable = false;break; 
      case ASPBlockLayout.EDIT_LAYOUT  : editable = !isReadOnly(); break;
      case ASPBlockLayout.NEW_LAYOUT   : editable = (isInsertable() || !isReadOnly()); break;
      }

      return getAddressColumn(add1_val,add2_val,zip_code_val,city_val,state_val,county_val,
                              country_code_val,country_val,editable,explorer,mgr);

   }

   private boolean isReadOnly()
   {
      return (field_list[0].isReadOnly() || field_list[1].isReadOnly() || 
              field_list[2].isReadOnly() || field_list[3].isReadOnly() ||
              field_list[4].isReadOnly() || field_list[5].isReadOnly() ||
              field_list[6].isReadOnly() || field_list[7].isReadOnly() );
   }

   private boolean isInsertable()
   {
      return (field_list[0].isInsertable() && field_list[1].isInsertable() &&
              field_list[2].isInsertable() && field_list[3].isInsertable() &&
              field_list[4].isInsertable() && field_list[5].isInsertable() &&
              field_list[6].isInsertable() && field_list[7].isInsertable() );
   }

   /**
    * Return the HTML code for the localized address text area.
    * @param add1_val value of ADDRESS1
    * @param add2_val value of ADDRESS2
    * @param zip_code_val value of ZIP_CODE
    * @param city_val value of CITY
    * @param state_val value of STATE
    * @param county_val value of COUNTY
    * @param country_code_val value of COUNTRY_CODE
    * @param country_val value of COUNTRY
    * @param editabl_flag enable/disable the icon to open the address dialog
    * @param explorer flag to check browser type
    *
    * @see #getAddressLabel
    * @see ASPBlockLayout
    */
   public String getAddressColumn(String add1_val,
                                  String add2_val,
                                  String zip_code_val, 
                                  String city_val, 
                                  String state_val, 
                                  String county_val, 
                                  String country_code_val,
                                  String country_val, boolean editable_flag, boolean explorer,ASPManager mgr)
   {
      
      setLayouts(mgr,country_code_val);

      html.clear();
      html.append("\n\t\t<td valign=top "," "," colspan=1 >");
      
      if (explorer)
      {
         address_box_col = 50;

         html.append("<textarea tabindex=-1 readonly class='readOnlyTextArea' name=\""+getTextAreaName()+"\" "+
            "  cols="+address_box_col+" rows="+getRowCount()+" wrap=virtual");
      }
      else
      {
         address_box_col = 25;

         html.append("<textarea tabindex=-1 readonly class='readOnlyTextArea' name=\""+getTextAreaName()+"\" "+
            " onChange=\"validateAddressArea"+field_list[0].getJavaScriptName()+"(this)\" cols="+address_box_col+" rows="+getRowCount()+" wrap=virtual");
      }
      
      html.append(">",getAddressDisplayString(add1_val,add2_val,zip_code_val,city_val,state_val,county_val,country_code_val,country_val));
      html.append("</textarea>");

      if (editable_flag)
      {
         html.append("<a href='javascript:addressDialog"+field_list[0].getJavaScriptName()+"()'>",
                     "<img border=\"0\" src=\"../common/images/address_book.gif\" width=\"15\" height=\"13\" alt=\""+tool_tip+"\" title=\""+tool_tip+"\" >",
                     "</a>");

         html.append("\n\n\n<script language=javascript>");
         html.append("\n",getJavaScript(explorer,country_code_val));
         html.append("\n</script>\n\n");
      }
      
      return html.toString();
   }



   private String getJavaScript(boolean explorer, String country_code_val)
   {
      AutoString js = new AutoString();

      js.append("\n\t","edit_template_"+field_list[0].getName()+"='",getAddressEditLayout(),"';");
      js.append("\n");
      js.append("\nfunction ","addressDialog",field_list[0].getJavaScriptName(),"()");
      js.append("\n{");
      js.append("\n\t","labels ='",getLabels(),"';");
      js.append("\n\t","edit_template=","edit_template_"+field_list[0].getName(),";");
      js.append("\n\t","i=0;");
      js.append("\n");
      js.append("\n\t","jsfunc_name = \"","setAddressValues",field_list[0].getJavaScriptName(),"\"");
      js.append("\n");
      js.append("\n\t","lov_views = \"",getLOVViews(),"\"");
      js.append("\n\t","lov_in_parameters = \"",getLOVInParameters(),"\"");
      js.append("\n\t","address_fields = \"",getAddressFieldList(),"\"");
      js.append("\n\t","non_address_fields = \"",getNonAddressFieldList(),"\"");
      js.append("\n\t","lov_property = \"",getLOVProperties(),"\"");
      js.append("\n\t","dynamic_def_key = \"",getDynamicDefKey(country_code_val),"\"");
      js.append("\n");
      js.append("\n\t","query_str =","\t\"LABELS=\"+URLClientEncode(labels)+");
      js.append("\n\t\t\t","\"&EDIT_TEMPLATE=\"+URLClientEncode(edit_template)+");
      js.append("\n\t\t\t","\"&JSFUNC_NAME=\"+URLClientEncode(jsfunc_name)+");
      js.append("\n\t\t\t","\"&LOV_VIEWS=\"+URLClientEncode(lov_views)+");
      js.append("\n\t\t\t","\"&__DYNAMIC_DEF_KEY=\"+URLClientEncode(dynamic_def_key)+");
      js.append("\n\t\t\t","\"&ADDRESS_FIELDS=\"+URLClientEncode(address_fields)+");
      js.append("\n\t\t\t","\"&NON_ADDRESS_FIELDS=\"+URLClientEncode(non_address_fields)+");
      js.append("\n\t\t\t","\"&LOV_IN_PARAMS=\"+URLClientEncode(lov_in_parameters);");
      js.append("\n");
      js.append("\n\t","window.open(\"../common/scripts/LocalizedAddressDlg.page?\"+query_str,");
      js.append("\n\t\t","    \"ADDRESS_DIALOG\",");
      js.append("\n\t\t","    \"status,resizable,scrollbars,width=500,height=300\");");

      js.append("\n}");

      js.append("\n\n");
      js.append("\n\t","display_template_"+field_list[0].getName()+" = \"",Str.replace(getAddressDisplayLayout(),"\n","\\n"),"\";");
      js.append("\n\n");
      js.append("\nfunction ","setAddressValues",field_list[0].getJavaScriptName(),"(add1,add2,zip_code,city,state,county,country_code)");
      js.append("\n{");
      js.append("\n\t","display_template = ","display_template_"+field_list[0].getName(),";");
      js.append("\n");
      js.append("\n\t","f.",field_list[0].getName(),".value = add1;");
      js.append("\n\t","f.",field_list[1].getName(),".value = add2;");
      js.append("\n\t","f.",field_list[2].getName(),".value = zip_code;");
      js.append("\n\t","f.",field_list[3].getName(),".value = city;");
      js.append("\n\t","f.",field_list[4].getName(),".value = state;");
      js.append("\n\t","f.",field_list[5].getName(),".value = county;");
      js.append("\n\t","f.",field_list[6].getName(),".value = country_code;");
      js.append("\n");

      for(int i=0; i<field_list.length; i++)
      {
         if (field_list[i].hasValidation())
            js.append("\n\tvalidate",field_list[i].getJavaScriptName(),"(-1);");
      }
      
      js.append("\n");
      js.append("\n\t","var temp = display_template;");
      js.append("\n\t","temp = temp.replace(\"&ADDRESS1\",","f.",field_list[0].getName(),".value);");
      js.append("\n\t","temp = temp.replace(\"&ADDRESS2\",","f.",field_list[1].getName(),".value);");
      js.append("\n\t","temp = temp.replace(\"&ZIP_CODE\",","f.",field_list[2].getName(),".value);");
      js.append("\n\t","temp = temp.replace(\"&CITY\",","f.",field_list[3].getName(),".value);");
      js.append("\n\t","temp = temp.replace(\"&STATE\",","f.",field_list[4].getName(),".value);");
      js.append("\n\t","temp = temp.replace(\"&COUNTY\",","f.",field_list[5].getName(),".value);");
      js.append("\n\t","temp = temp.replace(\"&COUNTRY_CODE\",","f.",field_list[6].getName(),".value);");
      js.append("\n\t","temp = temp.replace(\"&COUNTRY\",","f.",field_list[7].getName(),".value);");
      js.append("\n");
      js.append("\n\t","f.",getTextAreaName(),".value = temp;");
      js.append("\n\t","f.",getTextAreaName(),".defaultValue = temp;");
  
      js.append("\n}");

      if (! explorer)
      {
         js.append("\n\n");
         js.append("\nfunction ","validateAddressArea",field_list[0].getJavaScriptName(),"(obj)");
         js.append("\n{");
         js.append("\n\t","alert('"+message+"');");
         js.append("\n\t","obj.value=obj.defaultValue;");
         js.append("\n}");
      }

      return js.toString();
   }

   private String getLabels()
   {
      String lbs ="";
      lbs += field_list[0].getTranslationKey()+": "+field_list[0].getLabel()+"^"; 
      lbs += field_list[1].getTranslationKey()+": "+field_list[1].getLabel()+"^"; 
      lbs += field_list[2].getTranslationKey()+": "+field_list[2].getLabel()+"^"; 
      lbs += field_list[3].getTranslationKey()+": "+field_list[3].getLabel()+"^"; 
      lbs += field_list[4].getTranslationKey()+": "+field_list[4].getLabel()+"^"; 
      lbs += field_list[5].getTranslationKey()+": "+field_list[5].getLabel()+"^"; 
      lbs += field_list[6].getTranslationKey()+": "+field_list[6].getLabel()+"^"; 
      lbs += field_list[7].getTranslationKey()+": "+field_list[7].getLabel(); 

      return lbs;
   }

   /**
    * Hide all ASPFields in address field.
    * @see ASPField
    */
   protected void hideAll()
   {
      field_list[0].setHidden();
      field_list[1].setHidden();
      field_list[2].setHidden();
      field_list[3].setHidden();
      field_list[4].setHidden();
      field_list[5].setHidden();
      field_list[6].setHidden();
      field_list[7].setHidden();

   }

   private String getJavaScriptName( String name )
   {
      boolean first = true;
      int len = name.length();
      StringBuffer buf = new StringBuffer(len);
      for( int i=0; i<len; i++ )
      {
         char ch = name.charAt(i);
         
         if( ch=='_' )
            first = true;
         else if( first )
         {
            buf.append(Character.toUpperCase(ch));
            first = false;
         }
         else   
            buf.append(Character.toLowerCase(ch));
      }
      return buf.toString();
   }   

}
