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
 * File        : MobileBlockLayout.java
 * Description :
 * Notes       :
 * Created     : mapelk
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2009-Aug-11 amiklk Bug id 85128 - changed drawColumns() to remove &nbsp;
 * 2009-Jul-10 amiklk Bug id 84659 - Added appendHyperlinkURL(), createHyperlinkColumn(),
 *                                   HyperlinkColumn inner class. Changed drawField()
 * 2009-Jul-03 amiklk Bug id 83630 - Changed drawField() to draw a disabled checkbox 
 *                                   when the field is a checkbox
 * 2008-Jun-13 buhilk Bug id 74552 - Added support for setHilite() by adding getStyle() and modifying drawField().
 *                                   Added support for setSimple() by modifying drawColumns().
 * 2007-Dec-03 buhilk IID F1PR1472 - Improved miniFW functionality.
 */

package ifs.fnd.webmobile.web;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.buffer.*;
import ifs.fnd.util.Str;


public class MobileBlockLayout extends ASPBlockLayout{
   
   public static final int SINGLE_COLUMN = 1;
   public static final int DOUBLE_COLUMN = 2;
   public static final int MAX_FIELD_SIZE = 15; //characters
   
   private MobileManager mmgr;
   
   /** Creates a new instance of MobileBlockLayout */
   public MobileBlockLayout(ASPBlock obj)
   {
      super(obj);
      try
      {
         mmgr = new MobileManager(this.getASPPage());
      }
      catch (FndException fe)
      {
         getASPManager().showError(fe.getMessage());
      }
   }
   
    protected MobileBlockLayout(ASPBlock obj,boolean clone)
    {
      super(obj,clone);      
      try
      {
         mmgr = new MobileManager(this.getASPPage());
      }
      catch (FndException fe)
      {
         getASPManager().showError(fe.getMessage());
      }
    }
   
   private ASPBlock getBlock()
   {
      return blk;
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected ASPPoolElement clone( Object block ) throws FndException
   {
      MobileBlockLayout b = new MobileBlockLayout((ASPBlock)block,true);
      return super.cloneMe(b);
   }

   //==========================================================================
   //  Presentation Section
   //==========================================================================
     
   public String show() throws FndException 
   {
      AutoString presentation = new AutoString();
      ASPManager mgr = getASPManager();
      if(blk.getMasterBlock()==null && (blk.getASPRowSet().countRows() == 1) && (isMultirowLayout() || isSingleLayout()) && isAutoLayoutSelectOn())
      {
         blk.getASPCommandBar().disableCommand("Back");
         setLayoutMode(SINGLE_LAYOUT);
         getASPPage().saveLayout();
      }      
      // Show commandbar (if available)
      presentation.append(blk.getASPCommandBar().showBar());
      String image_location = mgr.getASPConfig().getImagesLocation();
      if(blk.getASPRowSet().countRows() == 0 && !isFindLayout() && !isCustomLayout() && blk.getMasterBlock()==null)
      {
         // if we have no rows, show introductory page.
         presentation.append(introPage());
      }
      else
      {
         // Show table or dialog
         if( getLayoutMode() == MULTIROW_LAYOUT)
            presentation.append(blk.getASPTable().populate());
         else
            presentation.append(generateDialog());
      }
      return presentation.toString();   
   }

   public String generateDialog()
   {
      try
      {
         ASPManager mgr = blk.getASPManager();
         ASPRowSet rowset;
         AutoString out = new AutoString();

         String case_sensitive_flag = mgr.getConfigParameter("ADMIN/CASE_SENSITIVE_SEARCH","Y");
         rowset = blk.getASPRowSet();
                  
         if(isFindLayout())
         {
            if (!mgr.isEmpty(mgr.readValue("__CASESS_VALUE")))
               case_sensitive_flag = mgr.readValue("__CASESS_VALUE");
            
            out.append("\n");
            out.append("<table class=\"pageFormWithoutBottomLine\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n"); 
            
            if(!mgr.getASPPage().getASPProfile().isUserProfileDisabled())
            {
               out.append("   <tr>\n");
               out.append("      <td align=\"left\" height=\"20px\" class=\"normalTextValue\">&nbsp;",mgr.translateJavaText("FNDLAYSAVEDQRY: Saved Queries"),"</td>\n");
               out.append("   </tr>\n");
               out.append("   <tr>\n");
               out.append("      <td align=\"left\" height=\"20px\" class=\"normalTextValue\">&nbsp;",blk.getASPCommandBar().addSaveMobileQuery(),"</td>\n");
               out.append("   </tr>\n");
               out.append("   <tr>\n");
               out.append("      <td align=\"left\" height=\"20px\" class=\"normalTextValue\">&nbsp;"+mgr.translateJavaText("FNDLAYCSENSEAR: Case sensitive search")+"&nbsp;<input class='checkbox' type=checkbox name=__CASESS value=1 ","N".equals(case_sensitive_flag)?"":"checked"," onClick=\"javascript:document.form.__CASESS_VALUE.value = (document.form.__CASESS.checked)?'Y':'N';\")></td>\n");
               out.append("   </tr>\n");
            }

            if (hasCounted())
            {
               out.append("   <tr>\n");
               out.append("      <td align=\"left\" height=\"20px\" class=\"normalTextValue\">&nbsp;", getASPManager().translateJavaText("FNDLAYCOUNT: This query, unedited, will retrieve &1 rows",Integer.toString(getCountedValue())),"</td>\n");
               out.append("   </tr>\n");
            }
               
            out.append("   <tr>\n");
            out.append("      <td><hr width=100% size=1 noshade></td>\n");
            out.append("   </tr>\n");
            out.append("</table>\n");
         }
         
         if(getDefinedGroups().size() == 0)
         {
            // User has not used the defineGroup() function.
            // Draw the default group.
            drawColumns(rowset, out);
         }

         return out.toString();
      }
      catch(Throwable any)
      {
         error(any);
         return "";
      }
   }
   
   private String getStyle(ASPField field)
   {
      if(field.isHilite())
         return "hiliteTextValue";
      else if(field.isBold())
         return "boldTextValue";
      else
         return "normalTextValue";
   }
   
   private void drawColumns(ASPRowSet rowset, AutoString out)
   {
      ASPBlock thisblk = getBlock();
      ASPField[] fields = thisblk.getFields();
      try
      {      
         out.append("\n");
         out.append("<table class=\"pageFormWithBorder\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"2\">\n");
         for(int i=0; i<fields.length; i++)
         {
            if(fields[i].isHidden()) continue;
            if(isNewLayout()  && fields[i].isReadOnly()) continue;
            
            String fieldName = fields[i].getLabel();

            out.append("   <tr>\n");
            out.append("      <td valign=\"center\" class=\"normalTextLabel\">",fieldName);

            if(getDialogColumns() == SINGLE_COLUMN)
            {
               out.append("&nbsp;<br>&nbsp;");
            }
            else
            {
               out.append("</td>\n");
               out.append("      <td>");
            }

            drawField(rowset, fields[i].getName(), out);
            if(isSingleLayout() && (i+1)<fields.length && fields[i+1].isSimple())
            {
               out.append("&nbsp;");
               drawField(rowset, fields[++i].getName(), out);
            }
            
            out.append("</td>\n")   ;
            out.append("   </tr>\n");
         }
         out.append("</table>\n");

         if(isEditLayout() || isNewLayout())
         {
            out.append("<!-- Hidden Fields that belong to the block -->\n");
            out.append(thisblk.generateHiddenFields());
            out.append("\n");
            out.append("<!-- Hidden Fields that belong to the master block -->\n");
            generateMasterHiddens(out);
            out.append("\n");
         }
      }
      catch (Throwable e) {error(e);}
      
   }
      
   private class HyperlinkColumn
   {
      String     colurl;       // URL for this column
      boolean    pres_obj_ok; // whether user has PO security for the page pointed to by the hyperlink
      int[]      parposition;  // position in the DATA buffer for each parameter
      ASPField[] parfield;     // ASPField for each parameter

   }
   
   private HyperlinkColumn createHyperlinkColumn( ASPField field,
                                                  ASPBuffer firstrow ) throws Exception
   {
      HyperlinkColumn hcol = new HyperlinkColumn();
      hcol.colurl = field.getHyperlinkURL();

      String hyperlink_presobj = mmgr.getHyperlinkedPresObjectId(field); 

      if( hcol.colurl!=null )
      {
         ASPField[] params = mmgr.getHyperlinkParameters(field);
         int param_count   = params.length;
         hcol.parposition  = new int[param_count];
         hcol.parfield     = params;

         for( int p=0; p<param_count; p++ )
         {
            ASPField parameter = params[p];            
            hcol.parposition[p] = firstrow.getItemPosition(parameter.getDbName());
         }

         //If 'secure' hyperlink, check security
         //Else security is okay
         if (!Str.isEmpty(hyperlink_presobj))
           hcol.pres_obj_ok = mmgr.isObjectAccessible(getASPPage(), hyperlink_presobj); 
         else
           hcol.pres_obj_ok = true;   
      }

      return hcol;
   }
   
   private void appendHyperlinkURL( AutoString html,
                                    ASPBuffer row,
                                    HyperlinkColumn hypercolumn ) throws FndException
   {
      String url = hypercolumn.colurl;
      html.append(url);

      int param_count = hypercolumn.parposition.length;
      for( int p=0; p<param_count; p++ )
      {
         if( p==0 && url.indexOf('?')<0 )
            html.append('?');
         else
            html.append('&');

         String param_value  = row.getValueAt(hypercolumn.parposition[p]);
         ASPField param = hypercolumn.parfield[p];
         param_value = param.convertToClientString(param_value);

         html.append(hypercolumn.parfield[p].getName());
         html.append('=');
         html.append(getASPManager().prepareURL(param_value));
      }
   }

   private boolean drawField(ASPRowSet rowset, String fieldName, 
                                                AutoString out) throws Exception
   {
      ASPField field = getBlock().getASPField(fieldName);
      ASPManager mgr = getASPManager();
      
      int fildSize = (field.getSize() > MAX_FIELD_SIZE)? MAX_FIELD_SIZE: field.getSize();
      
      if(isSingleLayout() || (isEditLayout() && field.isReadOnly()))
      {         
         String value = getCurrentRowValue(rowset,fieldName,field); 
         //If checkbox, draw one
         if( field.isCheckBox() )
            out.append("<INPUT TYPE=\"checkbox\" ", 
                    field.getCheckedValue().equals(value)? "CHECKED":"", " DISABLED> " );            
         else
         {
            //If Text, manage hyperlinks if any
            ASPBuffer firstrow = rowset.getRow();
            HyperlinkColumn hcol = createHyperlinkColumn(field, firstrow);

            if(hcol.pres_obj_ok)
            {
               out.append("<A HREF=\"");
               appendHyperlinkURL(out, firstrow, hcol);
               out.append("\">", "<font class=\""+getStyle(field)+"\">", value ,"</font>", "</A>");                  
            }  
            else
               out.append("<font class=\""+getStyle(field)+"\">", value ,"</font>");
         }
         field.setHidden();
         return false;
      }
      
      if(field.isSelectBox())
      {
         if(!isSingleLayout() && field.isFindModeIidToLov())
         {
            out.append("<select class=\"selectbox\" size=\"1\" name=\"",field.getDbName(),"\" ");
            if(!isFindLayout())
               field.appendValidationTag(out,-1, false);
            out.append(">");
            getASPPage().getASPHTMLFormatter().populateMobileListBox(out,field.getIidClientValues(),getCurrentRowValue(rowset, fieldName, field), false, false);
            out.append("</select>");
         }
         
         return false;
      }
      else if(field.isCheckBox())
      {
         if(isFindLayout())
         {
            out.append("<select class='selectbox' name="+field.getName()+">\n");
            out.append("   <option value=''></option>");
            out.append("   <option value='"+field.getCheckedValue()+"'>"+mgr.translateJavaText("FNDLAYCHE: Checked")+"</option>");
            out.append("   <option value='"+field.getUncheckedValue()+"'>"+mgr.translateJavaText("FNDLAYUNCHE: Unchecked")+"</option>");
            out.append("</select>");
         }
         else
         {
            out.append("<input class=\"checkbox\" type=\"checkbox\" value=\""+field.getCheckedValue()+"\" ");
            if (getCurrentRowValue(rowset, fieldName, field).equals(field.getCheckedValue()))
               out.append("checked");
            out.append(" ");
            field.appendValidationTag(out,-1, false);
            out.append(" name=\"",fieldName,"\" >");
         }
         return false;
      }      
      else if (field.isRadioButtons())
      {
         out.append("\n");
         for(int i=0;i<field.countValues();i++)
         {
            out.append("<input class=\"radioButton\" type=\"radio\"  name=\""+fieldName+"\" value=\""+field.getIidClientValues()[i]+"\" ");
            if (field.getIidClientValues()[i].equals(getCurrentRowValue(rowset,fieldName,field)))
               out.append(" checked ");
            if(!isFindLayout())
                field.appendValidationTag(out,-1, false);
            out.append("><font class=\"normalTextValue\">"+field.getIidClientValues()[i]+"</font>&nbsp;");
         }
         out.append("\n");
         return false;
      }
      else if (field.isPasswordField())
      {
         out.append("<input class=\"passwordTextField\" type=\"password\" size=\""+fildSize+"\" value=\""+getCurrentRowValue(rowset,fieldName,field)+"\" name=\"",fieldName,"\" >");
         return false;
      }
      
      if (field.getHeight() > 1)
         {
            out.append("<textarea class=\"editableTextArea\" name=\"" + fieldName + "\" cols=\"15\" rows=\"" + field.getHeight() + "\" wrap=\"virtual\">");
            if(isEditLayout())
               out.append(getCurrentRowValue(rowset,fieldName,field));
            out.append("</textarea>");
            return true;
      }
      else
      {

         String eleVal = getCurrentRowValue(rowset,fieldName,field);
         String eleName = fieldName;

         if (field.isAccurateFld() && !isFindLayout())
         {
            out.append("<input type=\"hidden\" name=\""+eleName+"\" value=\""+eleVal+"\" >");

            String dbname = field.getDbName();
            if (rowset.countRows()>0)
               if (rowset.getValue(dbname)!=null && !rowset.getValue(dbname).equals("null"))
               {
                  eleVal = field.getAccurateClientFormatter().format(new Double(rowset.getValue(dbname)));
               }

            eleName = field.getAccurateDisplayFldName();
         }

         out.append("<input class=\"editableTextField\" ");
         if(blk.isAlignNumbersToRight())
         {   
           if(field.getAlignment().length()>0)
           out.append("style=\"text-align=", field.getAlignment(),"\" ");
         }
         out.append("type=\"text\" size=\""+fildSize+"\" value=\""+eleVal+"\" name=\"",eleName,"\" ");

         if (field.isAccurateFld() && !isFindLayout())
         {
            String mask = field.getMask();
            mask = mask.substring(mask.indexOf(".")+1);

            out.append(" onFocus=\"this.value=f."+eleName+".value\" ");
            out.append(" onBlur=\"javascript:roundOff(this,f."+eleName+","+mask.length()+")\" ");
         }
      }      
      return false;
   }
   
   public String introPage()
   {
      AutoString str = new AutoString();
      ASPManager mgr = getASPManager();
           
      String image_location = mgr.getASPConfig().getImagesLocation();

      str.append("<table class=\"pageFormWithBorder\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"2\">\n"); 
      if(blk.getASPCommandBar().IsEnabled("Find"))
      {
         str.append("   <tr>\n");
         str.append("      <td>&nbsp;</td>\n");
         str.append("      <td class=\"introPageText\" width=\"100%\">");
         str.append("<a href=\"javascript:commandSet('"+blk.getName()+".Find','')\">");
         str.append("<img border=\"0\" src=\"",image_location,mgr.getConfigParameter("PAGE/DEFAULT_DIALOG/FIND/NORMAL",     "default_find.gif"),"\">");
         str.append("</a>&nbsp;");
         str.append(mgr.translateJavaText("FNDLAYPRFIND: &1Find&2 existing records","<a href=\"javascript:commandSet('"+blk.getName()+".Find','')\">","</a>"));
         str.append("</td>\n");
         str.append("   </tr>\n");
      }
      
      if(blk.getASPCommandBar().IsEnabled("NewRow") && blk.isCommandDefined("New__"))
      {
         str.append("   <tr>\n");
         str.append("      <td>&nbsp;</td>\n");
         str.append("      <td class=\"introPageText\" width=\"100%\">");
         str.append("<a href=\"javascript:commandSet('"+blk.getName()+".NewRow','')\">");
         str.append("<img border=\"0\" src=\"",image_location,mgr.getConfigParameter("PAGE/DEFAULT_DIALOG/NEWROW/NORMAL",     "default_new.gif"), "\">");
         str.append("</a>&nbsp;");
         str.append(mgr.translateJavaText("FNDLAYPRNEW: Enter a &1New&2 record","<a href=\"javascript:commandSet('"+blk.getName()+".NewRow','')\">","</a>"));
         str.append("</td>\n");
         str.append("   </tr>\n");
      }

      str.append("</table>\n");
      return str.toString();
   }
      
   private void generateMasterHiddens(AutoString out)
   {
       blk = getBlock();
       ASPBlock mblk = blk.getMasterBlock();
       if(mblk==null) return;
       recHiddens(mblk,out);
   }

   private void recHiddens(ASPBlock blk, AutoString out)
   {
      ASPField[] fields = blk.getFields();
      ASPRowSet rowset = blk.getASPRowSet();
      for(int i=0;i<fields.length;i++)
      {
         out.append("<input type=hidden name=\""+fields[i].getName()+"\" value=\""+getCurrentRowValue(rowset,fields[i].getName(),fields[i])+"\">\n");
      }
      out.append("\n");
      ASPBlock mblk = blk.getMasterBlock();
      if(mblk!=null)
         recHiddens(mblk,out);
   }
   
   private String getCurrentRowValue(ASPRowSet rowset, String name, ASPField field)
   {
      return getCurrentRowValue(rowset, name, field, false, false);
   }

   private String getCurrentRowValue(ASPRowSet rowset, String name, ASPField field, boolean encode_additional_spaces, boolean encode_url)
   {
        String dbname = field.getDbName();
        String value = "";
        ASPManager mgr = blk.getASPManager();
        if (getLayoutMode()==FIND_LAYOUT)
        {
            if(getASPPage().getASPLov() != null)
            {
               if(mgr.readValue(name) != null)
                  value = mgr.readValue(name);
            } else
            {
               String key = mgr.readValue("__COMMAND");
               if(key != null)
               {
                  int pos = key.indexOf('.');
                  String id = key.substring(pos+1);

                  if( ! id.equals(ASPCommandBar.FIND))
                  {
                     if(mgr.readValue(name) != null)
                        value = mgr.readValue(name);
                  }
               }
            }
        }
        else
        {
           if (rowset.countRows()>0)
               if (rowset.getValue(dbname)!=null && !rowset.getValue(dbname).equals("null"))
                  value = rowset.getRows().getBufferAt(rowset.getCurrentRowNo()).getFieldValue(name);

        }

         if(Str.isEmpty(value) && isSingleLayout())
            return "&nbsp;";
         else
            if(Str.isEmpty(value))
               return "";
            else if (encode_url)
               return value;
            else
               return mgr.HTMLEncode(value,encode_additional_spaces);
    }

}
