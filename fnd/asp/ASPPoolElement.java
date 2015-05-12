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
 * File        : ASPPoolElement.java
 * Description : Common super class for objects that can be stored in a pool
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1999-Feb-16 - Created
 *    Jacek P  1999-Feb-17 - Changed interface
 *    Jacek P  1999-Mar-01 - Interface ASPPoolable replaced with an abstract
 *                           class ASPPoolElement.
 *    Jacek P  1999-Mar-05 - Added method activate()
 *    Jacek P  1999-Apr-27 - New way of verifying poolable objects.
 *    Jacek P  2000-Apr-17 - Added debug-printouts in reset() and activate() functions.
 *    Daniel S 2002-Aug-28 - Added support for a new smart Reset/Clone. And added array/constants
 *                           that holds all supported classes. This will help to identify a class
 *                           and give it a number.
 *    Jacek P  2002-Sep-24 - Added method debugSupportedClassNr().
 *    Johan S  2003-Mar-06 - Added __AUTOSTRING
 *    Jacek P  2004-Nov-11 - Added method newProfile() for new profile handling.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.3  2007/12/03 12:36:40  buhilk
 * IID F1PR1472, Added Mini framework functionality for use on PDA.
 *
 * Revision 1.2  2005/09/28 13:02:41  japase
 * Keep track of the current page for better error handling.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/07/11 09:49:24  japase
 * Better error message for FNDPELNDEF
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.2  2004/12/20 08:45:07  japase
 * Changes due to the new profile handling
 *
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.buffer.*;
import java.util.*;

/**
 * Common super class for objects that can be stored in a pool.
 */
public abstract class ASPPoolElement extends ASPObject
{
   public static       boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPoolElement");

   public static final int UNDEFINED    = 0;
   public static final int DEFINED      = 1;
   public static final int DIRTY        = 2;
   public static final int LOADED_DIRTY = 3;

   private transient int state;

   /**
   * Static infomation on classes.
   */

   final static int __UNSUPPORTED = 0;

   // Pool Classes
   final static int __ASPCONTEXT = 1;
   final static int __ASPHTMLFORMATTER = 2;
   final static int __ASPCOMMANDBAR = 3;
   final static int __ASPBLOCK = 4;
   final static int __ASPBLOCKLAYOUT = 5;
   final static int __ASPROWSET = 6;
   final static int __ASPTABLE = 7;
   final static int __ASPFIELD = 8;
   final static int __ASPFORM = 9;
   final static int __ASPLOV = 10;
   final static int __ASPINFOSERVICES = 11;
   final static int __ASPLOG = 12;
   final static int __ASPTABCONTAINER = 13;
   final static int __ASPPOPUP = 14;
   final static int __MOBILEBLOCKLAYOUT  = 15;

   // Supported Types
   final static int __STRING = 30;
   final static int __BUFFER = 31;
   final static int __ASPBUFFER = 32;
   final static int __ASPTRANSACTIONBUFFER = 33;
   final static int __ASPQUERY = 34;
   final static int __ASPCOMMAND = 35;
   final static int __VECTOR = 36;
   final static int __AUTOSTRING = 37;

   final static Class[] supported_classes = generateSupportedClassArray();


   static String debugSupportedClassNr( int no )
   {
      switch(no)
      {
         case __UNSUPPORTED          : return "__UNSUPPORTED";

         // Pool Classes
         case __ASPCONTEXT           : return "__ASPCONTEXT";
         case __ASPHTMLFORMATTER     : return "__ASPHTMLFORMATTER";
         case __ASPCOMMANDBAR        : return "__ASPCOMMANDBAR";
         case __ASPBLOCK             : return "__ASPBLOCK";
         case __ASPBLOCKLAYOUT       : return "__ASPBLOCKLAYOUT";
         case __ASPROWSET            : return "__ASPROWSET";
         case __ASPTABLE             : return "__ASPTABLE";
         case __ASPFIELD             : return "__ASPFIELD";
         case __ASPFORM              : return "__ASPFORM";
         case __ASPLOV               : return "__ASPLOV";
         case __ASPINFOSERVICES      : return "__ASPINFOSERVICES";
         case __ASPLOG               : return "__ASPLOG";
         case __ASPTABCONTAINER      : return "__ASPTABCONTAINER";
         case __ASPPOPUP             : return "__ASPPOPUP";
         case __MOBILEBLOCKLAYOUT    : return "__MOBILEBLOCKLAYOUT";         

         // Supported Types
         case __STRING               : return "__STRING";
         case __BUFFER               : return "__BUFFER";
         case __ASPBUFFER            : return "__ASPBUFFER";
         case __ASPTRANSACTIONBUFFER : return "__ASPTRANSACTIONBUFFER";
         case __ASPQUERY             : return "__ASPQUERY";
         case __ASPCOMMAND           : return "__ASPCOMMAND";
         case __VECTOR               : return "__VECTOR";
         case __AUTOSTRING           : return "__AUTOSTRING";
         default                     : return "NOT_DEFINED";
      }
   }


   static Class[] generateSupportedClassArray()
   {
      try
      {
         Class[] tmparray = new  Class[38];

         tmparray[__ASPCONTEXT] = Class.forName("ifs.fnd.asp.ASPContext");
         tmparray[__ASPHTMLFORMATTER] = Class.forName("ifs.fnd.asp.ASPHTMLFormatter");
         tmparray[__ASPCOMMANDBAR] = Class.forName("ifs.fnd.asp.ASPCommandBar");
         tmparray[__ASPBLOCK] = Class.forName("ifs.fnd.asp.ASPBlock");
         tmparray[__ASPBLOCKLAYOUT] = Class.forName("ifs.fnd.asp.ASPBlockLayout");
         tmparray[__ASPROWSET] = Class.forName("ifs.fnd.asp.ASPRowSet");
         tmparray[__ASPTABLE] = Class.forName("ifs.fnd.asp.ASPTable");
         tmparray[__ASPFIELD] = Class.forName("ifs.fnd.asp.ASPField");
         tmparray[__ASPFORM] = Class.forName("ifs.fnd.asp.ASPForm");
         tmparray[__ASPLOV] = Class.forName("ifs.fnd.asp.ASPLov");
         tmparray[__ASPINFOSERVICES] = Class.forName("ifs.fnd.asp.ASPInfoServices");
         tmparray[__ASPLOG] = Class.forName("ifs.fnd.asp.ASPLog");
         tmparray[__ASPTABCONTAINER] = Class.forName("ifs.fnd.asp.ASPTabContainer");
         tmparray[__ASPPOPUP] = Class.forName("ifs.fnd.asp.ASPPopup");
         tmparray[__MOBILEBLOCKLAYOUT] = Class.forName("ifs.fnd.webmobile.web.MobileBlockLayout");         

         tmparray[__STRING]               = Class.forName("java.lang.String");
         tmparray[__BUFFER]               = Class.forName("ifs.fnd.buffer.Buffer");
         tmparray[__ASPBUFFER]            = Class.forName("ifs.fnd.asp.ASPBuffer");
         tmparray[__ASPTRANSACTIONBUFFER] = Class.forName("ifs.fnd.asp.ASPTransactionBuffer");
         tmparray[__ASPQUERY]             = Class.forName("ifs.fnd.asp.ASPQuery");
         tmparray[__ASPCOMMAND]           = Class.forName("ifs.fnd.asp.ASPCommand");
         tmparray[__VECTOR]               = Class.forName("java.util.Vector");
         tmparray[__AUTOSTRING]           = Class.forName("ifs.fnd.buffer.AutoString");
         return tmparray;
      }
      catch (ClassNotFoundException x)
      {
         return null;
      }
   }

   static int getClassNr(Class cls)
   {
      for (int i = 1;i<supported_classes.length;i++)
      {
         if (supported_classes[i]!=null && supported_classes[i].equals(cls))
            return i;
      }
      return __UNSUPPORTED;
   }

   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Protected constructor. Initiate the instance to state UNDEFINED.
    */
   protected ASPPoolElement( ASPManager mgr )
   {
      super(mgr);
      if(DEBUG) debug("ASPPoolElement: constructing "+this);
      this.state = UNDEFINED;
   }

   //==========================================================================
   //  Public state methods
   //==========================================================================

   /**
    * Check if the object is in state UNDEFINED or LOADED_DIRTY
    */
   public boolean isNotDefined()
   {
      return state==UNDEFINED || state==LOADED_DIRTY;
   }


   /**
    * Check if the object is in state UNDEFINED.
    */
   public boolean isUndefined()
   {
      return state==UNDEFINED;
   }


   /**
    * Check if the object is in state DEFINED.
    */
   public boolean isDefined()
   {
      return state==DEFINED;
   }


   /**
    * Check if the object is in state DIRTY.
    */
   public boolean isDirty()
   {
      return state==DIRTY;
   }


   /**
    * Check if the object is in state LOADED_DIRTY.
    * Not relevant for ASPPage - always false.
    */
   public boolean isLoadedDirty()
   {
      return state==LOADED_DIRTY;
   }


   /**
    * Return the current state of the page.
    */
   public int getState()
   {
      return state;
   }


   //==========================================================================
   //  Other state methods
   //==========================================================================

   /**
    * Change the state of the object according to the state diagram below:
    *
    *      ---                ---                ---                ---
    *     |   |              |   |              |   |              |   |
    *     |   V              |   V              |   V              |   V
    * -------------      -------------      -------------      -------------
    * |  LOADED   | <--- | UNDEFINED | ---> |  DEFINED  | ---> |   DIRTY   |
    * |   DIRTY   |      |           |      |           | <--- |           |
    * -------------      -------------      -------------      -------------
    *
    * The state LOADED_DIRTY is allowed only for objects that implements
    * ASPBufferable (and ASPField) and ASPPage
    */
   private void setState( int state ) throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.setState("+getStateName(state)+"): "+this);

      ASPPoolElement container = getContainer();

      switch(state)
      {
         case UNDEFINED:
            if (this.state!=UNDEFINED)
               throw new FndException("FNDPELFORBTR1: Forbidden state transition!");
            break;

         case DEFINED:
            if (this.state==LOADED_DIRTY)
               throw new FndException("FNDPELFORBTR2: Forbidden state transition!");
            break;

         case DIRTY:
            if (this.state==UNDEFINED || this.state==LOADED_DIRTY)
               throw new FndException("FNDPELFORBTR3: Forbidden state transition!");
            if (container!=null)
               container.notifyDirty();
            break;

         case LOADED_DIRTY:
            //if ( this instanceof ASPPage ) return;
            if ( !(this instanceof ASPBufferable) &&
                 !(this instanceof ASPField)      &&
                 !(this instanceof ASPPage)       )
               throw new FndException("FNDPELINVSTAT: State allowed only for ASPBufferable!");
            if (this.state==DEFINED || this.state==DIRTY)
               throw new FndException("FNDPELFORBTR4: Forbidden state transition!");
            if (container!=null)
               container.notifyLoaded();
            break;
         default:
            throw new FndException("FNDPELUNDEFST: Undefined state!");
      }
      this.state = state;
   }

   static String getStateName( int state )
   {
      switch(state)
      {
         case UNDEFINED:    return "UNDEFINED";
         case DEFINED:      return "DEFINED";
         case DIRTY:        return "DIRTY";
         case LOADED_DIRTY: return "LOADED_DIRTY";
         default:           return "Invalid state: "+state;
      }
   }

   public String getStateName()
   {
      return getStateName(getState());
   }

   /**
    * Return a reference to the container object.
    */
   protected ASPPoolElement getContainer()
   {
      return null;
   }


   /**
    * Set state to DEFINED after cloning.
    */
   final void setCloned() throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.setCloned(): "+this);
      raiseNotUndefined();
      setState(DEFINED);
   }


   /**
    * Set state to LOADED_DIRTY after loading the serialized object.
    */
   final void setLoaded() throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.setLoaded(): "+this);
      raiseNotUndefined();
      setState(LOADED_DIRTY);
   }

   //==========================================================================
   //  Other common methods for implementation of pool
   //==========================================================================

   /**
    * Freeze the object. Call freeze() for all enclosed elements.
    * Set the object in state DEFINED. Can only by called if the current
    * state of the object is UNDEFINED.
    */
   final void freeze() throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.freeze(): "+this);
      raiseNotUndefined();

      doFreeze();
      setState(DEFINED);
   }


   /**
    * Reset the object. Call reset() for all enclosed elements.
    * Set the object in state DEFINED. Can only be called if the current
    * object is in state DIRTY or DEFINED. Prepare for releasing in the pool.
    */
   final void reset() throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.reset(): "+this);
      if ( isNotDefined() )
      {
         if(DEBUG) debug(this+" is in UNDEFINED or LOADED_DIRTY state!");
         ASPManager mgr = getASPManager();
         String page_name = mgr!=null ? mgr.getCurrentPage() : null;
         throw new FndException("FNDPELUNDEF: Object '&1' (page '&2') is in &3 state during resetting! Should be DEFINED or DIRTY",getClass().getName(), page_name, getStateName(state));
      }

      if (isDirty())
         if (forceReset())
         {
            doReset();
         }
         else
         {
            frameworkReset();
            smartReset();
         }

      setState(DEFINED);
   }


   /**
    * Activate the object after feching from pool. Call activate() for
    * all enclosed elements.
    * Can only be called if the current object is in state DEFINED.
    */
   final void activate() throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.activate(): "+this);
      if( !isDefined() )
      {
         if(DEBUG) debug(this+" is not in DEFINED state!");
         ASPManager mgr = getASPManager();
         String page_name = mgr!=null ? mgr.getCurrentPage() : null;
         throw new FndException("FNDPELNDEF: Object '&1' (page '&2')is not in DEFINED state!", getClass().getName(), page_name);
      }
      doActivate();
   }

   /**
    * Called by freeze() after state check but before state change.
    */
   protected void doFreeze() throws FndException
   {
   }


   /**
    * Called by reset() after state check but before state change.
    */
   protected void doReset() throws FndException
   {
   }

   /**
   * Override these functions if you want to handle Reset and/or Clone manually.
   * Every data tape is not supported in automatic handling. This may be the only
   * way to get certain pages to run.
   */

   protected boolean forceReset()
   {
      return true;
   }

   protected boolean forceClone()
   {
      return true;
   }

  /**
   * Top level funtions for handling of Resetting and Cloning
   * They are overridden in ASPPage or ASPPageProvider.
   */



   protected void smartReset() throws FndException
   {
   }

   protected void frameworkReset() throws FndException
   {
   }

   public ASPPoolElement frameworkClone( Object obj ) throws FndException
   {
      return null;
   }




   /**
    * Called by activate() after state check.
    */
   protected void doActivate() throws FndException
   {
   }


   /**
    * Create a new instance of the same class and set in state DEFINED.
    */
   protected abstract ASPPoolElement clone( Object container ) throws FndException;


   /**
    * Notify the container object about transition to the DIRTY state.
    */
   void notifyDirty() throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.notifyDirty(): "+this);
      if( state!=DIRTY )
         setState(DIRTY);
   }


   /**
    * Notify the container object about transition to the LOADED_DIRTY state.
    */
   void notifyLoaded() throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.notifyLoaded(): "+this);
      if( state!=LOADED_DIRTY )
         setState(LOADED_DIRTY);
   }


   /**
    *
    */
   void modifyingMutableAttribute( String attr_name ) throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.modifyingMutableAttribute("+attr_name+")");
      if( state==DEFINED )
         setState(DIRTY);
   }


   /**
    *
    */
   void modifyingImmutableAttribute( String attr_name ) throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.modifyingImmutableAttribute("+attr_name+")");
      switch(state)
      {
         case DEFINED:
         case DIRTY:
            throw new FndException("FNDPELFROZEN: Cannot modify immutable attribute '&1' in state &2.",
                                    attr_name,getStateName(state));
      }
   }


   /**
    * Raise an exception if the page isn't in state UNDEFINED.
    */
   final void raiseNotUndefined() throws FndException
   {
      if(DEBUG) debug("ASPPoolElement.raiseNotUndefined(): "+this);
      if (!isUndefined())
         throw new FndException("FNDPELNUNDEF: The object is not in UNDEFINED state!");
   }

   public String toString()
   {
      return getStateName()+" "+super.toString()+" "+getName();
   }

   public String getName()
   {
      return "";
   }

   /**
    * Create and return new instance of corresponding profile class.
    * Return null if the class doesn't support profile handling.
    */
   protected ASPPoolElementProfile newProfile()
   {
      return null;
   }

   //==========================================================================
   //  Scanning and verifying
   //==========================================================================

   protected void verifyPage( ASPPage page ) throws FndException
   {
/*
      if(DEBUG) debug("ASPPoolElement.verifyPage(): "+this);

      if( this instanceof ASPPageElement )
      {
         ASPPageElement obj = (ASPPageElement)this;
         if( obj.getASPPage() != page )
            throw new FndException("FNDPELERRPAG: The object &1 refers to the wrong instance of ASPPage!",""+obj);
      }
      ASPManager mgr = this.getASPManager();
      if( mgr!=null && mgr!=page.getASPManager() )
         throw new FndException("FNDPELERRMAN: The object &1 refers to the wrong instance of ASPManager!",""+this);
*/
   }

   protected abstract void verify( ASPPage page ) throws FndException;

   protected abstract void scan( ASPPage page, int level ) throws FndException;

   protected void scanAction( ASPPage page, int level ) throws FndException
   {
      if( page.isVerifying() )
         verify(page);
      else
         page.appendContents(this,level);
   }


}

