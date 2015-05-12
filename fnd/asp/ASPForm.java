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
 * File        : ASPForm.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Micke A  1998-May-19 - Created
 *    Jacek P  1998-Aug-07 - Removed 'throws Exception'
 *    Jacek P  1998-Aug-10 - Added try..catch block to each public function
 *                           which can throw exception
 *    Marek D  1998-Aug-21 - New structure of ASPConfig.ifm
 *    Jacek P  1998-Aug-26 - Added default value to getConfigParameter() call
 *    Jacek P  1999-Feb-17 - Extends ASPPageElement instead of ASPObject.
 *    Jacek P  1999-Feb-19 - Implemented ASPPoolable.
 *    Jacek P  1999-Mar-05 - Interface ASPPoolable replaced with an abstarct
 *                           class ASPPoolElement.
 *    Jacek P  1999-Mar-15 - Method init() renamed to construct().
 *    Marek D  1999-Mar-19 - Removed calls to getConfigParameter()
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Jacek P  1999-Aug-10 - Added tool tip to Help icon.
 *    Jacek P  1999-Sep-09 - Default form height changed to 555 in construct().
 *    Jacek P  1999-Sep-10 - Added new public method getBorderWidth().
 *    Ramila H 2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;

/**
 *
 *
 */
public class ASPForm extends ASPPageElement
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPForm");

   private final static String IMG_HELP        = "help.gif";
   private final static int    IMG_HELP_HEIGHT = 16;
   private final static int    IMG_HELP_WIDTH  = 16;


   //==========================================================================
   // Instance variables
   //==========================================================================

   private int formWidth;                      private int pre_formWidth;
   private int formHeight;                     private int pre_formHeight;

   private int formHeaderHeight;
   private int formFooterHeight;

   private String border_width;


   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Package constructor
    */
   ASPForm( ASPPage page )
   {
      super(page);
      formHeaderHeight = 25;
      formFooterHeight = 30;
   }

   protected ASPForm construct()
   {
      setFormWidth(731);
      setFormHeight(555);
      border_width = getASPPage().getASPConfig().getParameter("PAGE/FORM/BORDER_WIDTH","1");
      return this;
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Called by freeze() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#freeze
    */
   protected void doFreeze()
   {
      pre_formWidth  = formWidth;
      pre_formHeight = formHeight;
   }
   

   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset()
   {  
      formWidth  = pre_formWidth;
      formHeight = pre_formHeight;
   }


   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      ASPForm f = new ASPForm((ASPPage)page);
      f.formWidth  = f.pre_formWidth  = pre_formWidth;
      f.formHeight = f.pre_formHeight = pre_formHeight;

      f.border_width = border_width;

      f.setCloned();
      return f;
   }

   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   //==========================================================================
   //  Public.
   //==========================================================================

   /**
    * Set the width of the ASPForm. 
    */
   public void setFormWidth(int formWidth)
   {
      try
      {
         modifyingMutableAttribute("FORM_WIDTH");
         this.formWidth = formWidth;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Set the height of the ASPForm. 
    */
   public void setFormHeight(int formHeight)
   {
      try
      {
         modifyingMutableAttribute("FORM_HEIGHT");
         this.formHeight = formHeight;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns the width of the ASPForm. 
    */
   public int getFormWidth()
   {
      return formWidth;
   }

   /**
    * Returns the height of the ASPForm. 
    */
   public int getFormHeight()
   {
      return formHeight;
   }

   /**
    * Returns the header height of the ASPForm. 
    */
   public int getFormHeaderHeight()
   {
      return formHeaderHeight;
   }

   /**
    * Returns the footer height of the ASPForm. 
    */
   public int getFormFooterHeight()
   {
      return formFooterHeight;
   }

   /**
    * Returns the content width of the ASPForm. 
    */
   public int getFormContentWidth()
   {
      return getFormWidth() - 2 - 2;
   }

   /**
    * Returns the content height of the ASPForm. 
    */
   public int getFormContentHeight()
   {
      return getFormHeight() - getFormHeaderHeight() - getFormFooterHeight() - 2 - 2;
   }


   /**
    * Generate HTML code that implements the help button.
    * 
    */
   public String showHelpCommand()
   {
      ASPManager mgr = getASPManager();
      return "<a href=\"javascript:showHelp('" +
             mgr.getURL() +
             "')\"><img src=\"" +
             getASPPage().getASPConfig().getImagesLocation() + IMG_HELP +
             "\" align=\"absmiddle\" width=\"" + IMG_HELP_WIDTH + "\" height=\"" + IMG_HELP_HEIGHT +
             "\" alt=\""+mgr.translateJavaText("FNDFRMHELP: Help")+"\" title=\""+mgr.translateJavaText("FNDFRMHELP: Help")+"\" border=0></a>";
   }

   /**
    * Returns the border width of the ASPForm. 
    */
   public String getBorderWidth()
   {
      return border_width;
   }
}

