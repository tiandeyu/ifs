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
 * File        : ASPField.java
 * Description : A class that represents a HTML field on an ASP page
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1998-Feb-14 - Created
 *    Marek D  1998-Mar-17 - Added Label, Hyperlink and Boolean Flags
 *    Marek D  1998-Mar-25 - Added LOV, Validation and Computable fields
 *    Marek D  1998-Apr-05 - Added RadioButtons and CheckBox
 *    Marek D  1998-May-01 - Added implementation of Bufferable interface
 *    Marek D  1998-Jun-04 - Added connection to a global variable
 *    Marek D  1998-Jun-15 - Added POST/GET method to setHyperlink()
 *    Jacek P  1998-Jul-16 - Added function set/getAlignment().
 *    Marek D  1998-Jul-24 - Added alias for IN parameters to LOV and Custom
 *                           Validation
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-07 - All public functions of type setXxx() return
 *                           'this' (log id: #100).
 *                           Fixed bug #95: Function setFunction()
 *                           does not consider DB name
 *    Marek D  1998-Aug-17 - Use SQLTokenizer in addWhereCondition()
 *                           Added protected method parseException(value)
 *    Jacek P  1998-Aug-17 - Function setHyperlink() can define the hyperlink
 *                           to be run in a new browser (log id #101)
 *    Marek D  1998-Aug-19 - Add first ">" and skip last ">" in appendLOVTag()
 *    Marek D  1998-Aug-20 - Validation of non-mandatory empty field clears
 *                           OUT parameters (no HTTP request)
 *    Marek D  1998-Aug-21 - New structure of ASPConfig.ifm
 *    Jacek P  1998-Aug-26 - Added default value to getConfigParameter() call
 *    Marek D  1998-Sep-16 - Bug #2692: Call JavaScript function
 *                           resetReadOnlyField() for Read-Only fields.
 *                           New methods unsetMandatory(), unsetReadOnly()
 *    Marek D  1998-Sep-24 - Call assignValue_() from validateXXX()
 *                           Call checkReadOnly_() from checkXXX()
 *    Marek D  1998-Sep-28 - Use '\1' instead of '\0' in implementation of
 *                           setFunction()
 *    Jacek P  1998-Sep-29 - Added queryable flag with corresponding methods
 *                           (ToDo #2718).
 *    Marek D  1998-Sep-30 - New method setLOVProperty() (ToDo #2709)
 *                           Use js_app_name for JS generation (Idea #2720)
 *                           Use global variable f instead of document.form
 *                           Added LOV properties
 *                           Send label to checkMandatory_(), checkStatus_()
 *    Jacek P  1998-Oct-01 - Relative path in default value of parameter
 *                           lov_image_file changed to absolute with protocol
 *                           (Bug: #2673).
 *    Jacek P  1998-Oct-12 - Added new functions for definition of the field as
 *                           Select Box. Changes in LOV and validation functions
 *                           (Bug: #2707).
 *    Jacek P  1998-Oct-21 - Added call to Util.trimLine() in readValueAt()
 *                           (Bug: #2816).
 *    Marek D  1998-Oct-27 - Bug #2692: Skip Read-Only check for Explorer
 *    Marek D  1998-Oct-29 - Declared as "public":
 *                           hasLOV(), appendLOVTag(), appendValidationTag()
 *    Marek D  1998-Nov-24 - Added parse() and format() (ToDo #2859)
 *    Marek D  1998-Dec-02 - Renamed format/parse to formatNumber/parseNumber
 *                           and changed Object to double and null to NaN.
 *    Marek D  1998-Dec-10 - Added function getFunctionDbParameters() (Bug #3005)
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Now extends ASPPageSubElement.
 *    Marek D  1999-Mar-01 - Implementation of ASPPoolElement state diagram
 *    Marek D  1999-Mar-19 - Removed calls to getConfigParameter()
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Marek D  1999-Apr-28 - Altered SIZE and LABEL to be mutable attributes
 *    Marek D  1999-May-10 - Added transient variables 'variable_name' and
 *                           'tag_variable_name'
 *                           New methods: enumerateValues(), unsetSelectBox()
 *                           Made LOV immutable (only lov_active is mutable)
 *                           Added IN/OUT parameter arrays created in doFreeze()
 *                           New persistent mutable attribute 'insertable'
 *    Jacek P  1999-May-25 - Added method prepareClientValue()
 *    Marek D  1999-Jun-03 - Added argumentless versions of setCheckBox()
 *                           and setRadioButtons()
 *                           Made setRadioButtons(iid_pkg,db_values) obsolete
 *                           New persistent immutable attribute 'search_on_db_column'
 *    Jacek P  1999-Jul-09 - Corrected bug in generation of call to assignSelectBoxValue_()
 *                           in appendValidationFunction().
 *    Jacek P  1999-Jul-14 - Added new mutable attribute max_length and the
 *                           corresponding methods set/getMaxLength()
 *    Jacek P  1999-Jul-21 - Added generation of MAXLENGTH attribute in appendValidationTag()
 *    Jacek P  1999-Aug-02 - Added block name to call to getRowStatus_().
 *    Jacek P  1999-Aug-09 - Added control of the new row status 'QueryMode__'
 *                           in appendValidationFunction().
 *    Jacek P  1999-Sep-01 - Call to perform() replaced with performConfig() in
 *                           method enumerateValues().
 *    Jacek P  1999-Sep-06 - Added new overloaded version of appendReadOnlyTag()
 *                           which takes bgcolor as parameter.
 *    Jacek P  1999-Oct-20 - Changed access of appendReadOnlyTag() to public.
 *    Stefan M 1999-Dec-20 - Added dynamic LOV functions, setDynamicLov().
 *    Jacek P  2000-Jan-21 - Implementation of portal:
 *                           call to ASPManager.readValue*() replaced with ASPPage.readValue*()
 *                           in readValueAt().
 *    Jacek P  2000-Jan-25 - Implementation of portal:
 *                           variable js_app_name replaced with getJavaScriptName()
 *                           in appendLOVFunction(), appendValidationFunction(),
 *                           appendCheckFunction(), appendLOVTag(), appendValidationTag(),
 *                           appendCheckTag().
 *    Stefan M 2000-Feb-03 - Added setHeight(), which determines if field is a
 *                           TEXTAREA or INPUT field in generated dialogs.
 *    Stefan M 2000-Apr-20 - Added setSimple(), function called from ASPBlockLayout,
 *                           which in single record will omit the label and HTML TD cell.
 *    Stefan M 2000-May-20 - appendLOVTag() changed to support custom validation function.
 *    Johan S  2000-Jul-24 - isLovActive() changed to detect FIND dialog invalidations.
 *    Johan S  2000-Jul-26 - added formatDate and parseDate
 *    Stefan M 2000-Oct-03 - Added setHilite().
 *    Jacek P  2000-Oct-31 - Added overloaded functions getValue() and getTag() for support
 *                           of the ASP2JAVA tool.
 *    Jacek P  2001-Jan-08 - Upgraded to latest version.
 *    Jacek P  2001-Jan-25 - Changed handling of the in_assignment for Java pages.
 *                           Function getValue() will not throw exception any longer.
 *    Jacek P  2001-Feb-01 - Fixed problems with quotation mark in assignments.
 *    Artur K  2001-Feb-16 - Changed setCustomValidation() function.
 *    Jacek P  2001-Mar-02 - Functions getValue() and getTag() will return null
 *                           if no rows exist in the row set.
 *    Johan S  2001-Mar-02 - Added URLencode for validations
 *    Kingsly P 2001-May-11 - appendValidationFunction(); document.ClientUtil
 *                            -> document.applets[0] in Standard Validation.
 *    Mangala  2001-Jul-04 - Partially improved Javadoc comments.
 *    Suneth M 2001-Sep-12 - Changed duplicated localization tags.
 *    Mangala  2001-Sep-19 - Log id 803. Change set simple method as the pre_variable concept
 *                           and also add a uset-function
 *    Ramila H 2002-Jan-10 - Added property address_field for localized address field concept.
 *    Ramila H 2002-Jan-25 - added address_field and lov_view_name to clone().
 *    Suneth M 2002-Jul-24 - Log id 869. Changed appendValidationFunction().
 *    Chandana 2002-Jul-30 - Added setTooltip(ASPField field), setTooltip(ASPField field, boolean label) and other supportive methods.
 *    Ramila H 2002-Jul-30 - Log id 875. Added isDateTime() and appendCalendarTag() methods.
 *    Ramila H 2002-Jul-31 - Log id 875. call the javascript validate method always from the Calendar.
 *    Suneth M 2002-Aug-01 - Log id 869. Changed setDynamicLOV() to set the key field as a in parameter.
 *    Suneth M 2002-Aug-08 - Log id 869. Changed appendLOVFunction() & appendURL().
 *    Mangala  2002-Aug-23 - Corrected Bug 32278: See the comments at corrected place
 *                           appendValidationFunction().
 *    Mangala  2002-Sep-05 - Make the function convertToClientString() to public.
 *    Mangala  2002-Oct-03 - Add facility to validate check box fields.
 *    Rifki R  2002-Nov-05 - Log id 905. Added getJSLabel(), which is now used instead of getLabel()
 *                           when generating javascript code.
 *    Ramila H 2002-Nov-20 - Removed the validate function call from the Calendar in LOV pages.
 *    Ramila H 2002-Dec-02 - Log id 762. added code to insert checkNumberValue_ for javascript checking of number fields.
 *    Suneth M 2002-Dec-02 - Corrected Project link call 92068: Changed isLOVActive().
 *    Suneth M 2002-Dec-17 - Log id 1002, Changed setDynamicLOV(),setLOV() & appendLOVFunction() to handle multi choice LOV's.
 *    Sampath  2002-Dec-27 - Add the field mask to editable fields; add the method appendTranslatedMask() and translateDateTimeMask();
 *    Suneth M 2002-Dec-30 - Log id 1002, Changed appendPageClientScript() & enumerateValues(). Added new methods
 *                           appendIidLOVtag(),appendIidLOVFunction(),getEnumerateMethod(),setFindModeIidToLov(),
 *                           unsetFindModeIidToLov() & isFindModeIidToLov().
 *    ChandanaD2003-Jan-08 - Added updateLovPopup(int row_nr),getLovQueries() and getLovParams() methods.
 *                           Changed appendLOVTag() to call getLovQueries() & updateLovPopup(int row_nr) methods.
 *    ChandanaD2003-Jan-10 - Changed getLovQueries() method.
 *    Sampath  2003-Jan-10 - called the client script function __connect() instead of connect() in the clientutil applet
 *    Marek D
 *    Jacek P  2003-Jan-13 - Removed double semicolon not allowed in JDK 1.4
 *    Chandana 2003-Jan-20 - Moved the creation of the lov_popup to the setLov() method.
 *    Chandana 2003-Jan-23 - Handled dynamicLOV variable in the clone() and doReset() methods.
 *    Chandana 2003-Jan-28 - Changed "Empty Query" to "New Query".
 *    Chandana 2003-Feb-10 - Changed getLovQueries & getLovParams methods.
 *    Suneth M 2003-Feb-18 - Added new method getIidClientValuesBuffer().
 *    Rifki R  2003-Feb-28 - Log id 1010. Added setSecureHyperLink() and getHyperlinkedPresObjectId()
 *                           for enabling and disabling hyperlink by checking PO security.
 *    ChandanaD2003-Mar-17 - Changes setLOV() method.
 *    Mangala  2003-May-13 - Introduced new member variable 'title' to use <BR> tags for title wrapping.
 *                           Related to Log# 1049.
 *    Mangala  2003-May-21 - reapply mutable design patern to lov variables.
 *    ChandanaD2003-Jun-03 - Called cfg.getLOVTag2Pop(in_assignment) instead of cfg.getLOVTag2(in_assignment).
 *    ChandanaD2003-Jun-03 - Made standard LOVs display saved queries popup.
 *    ChandanaD2003-Jun-03 - lov_popup set to null if unDefined in the doReset method.
 *    Ramila H 2003-Jul-29 - Log id 853, added Password type field.
 *    Sampath  2003-Jul-30 - insert field mask for dynamic-lov.
 *    Sampath  2003-Aug-13 - changed appendLOVFunction() to have new lov features in overloaded pages.
 *    ChandanaD2003-Sep-02 - Made show_lov_popup as an immutable attribue.
 *    ChandanaD2003-Sep-05 - Fixed a bug in Lov Popups.
 *    Ramila H 2003-Sep-18 - Log id 1044, added variable has_setfunction.
 *    ChandanaD2003-Oct-07 - Called javascript function formatNumber_() for number type fields in the appendValidationFunction method.
 *    Suneth M 2003-Dec-17 - Bug 40900,Added new LOV property FORMAT_MASK to handle the format mask of the fields in the dynamic LOV.
 *    Ramila H 2004-02-16  - Bug id 42211, Changed condition to handle NN7 like IE.
 *    ChandanaD2004-May-12 - Updated for the new style sheets.
 *    ChandanaD2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Ramila H 2004-05-28  - Bug id 42390, checked compatibale application b4 adding lov save queries.
 *    Chandana 2004-Jun-10 - Removed all absolute URLs.
 *    Suneth M 2004-Jul-09 - Changed appendCheckFunction() to format the date & time values according to the format mask.
 *    Ramila H 2004-08-02  - checked isActive depending on PO security.
 *    Chandana 2004-Aug-23 - Added new public method convertToJavaDate(String).
 *    Chandana 2004-Aug-23 - Fixed a bug in appendCheckFunction().
 *    Chandana 2004-Sep-10 - Fixed a bug in getLovQueries method (language code suffixed to the url)
 *    Rifki R  2004-Oct-06 - Fixed a bug in isLOVActive(), isReadOnly() is not checked if 'rowstatus' is empty.
 *    Mangala  2004-Oct-22 - Removed the checks for insertable fildes in appendCheckFunction().
 *    Chandana 2004-Oct-22 - Fixed a bug in setCustomValidation().
 *    Ramila H 2004-10-22  - Removed addition of language suffix to profile key.
 *    Rifki R  2004-Oct-26 - Merged Bug id 47417 and 47336, fixed checkbox validation problem in editable tables.
 *    Mangala  2004-Oct-26 - Merged Bug id 46758: Make hasSetFunction public.
 *    Chandana 2004-Nov-10 - Added setAggregateReference() and getAggregateReference() to support Activity API calls.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/04/19 sumelk Bug 89896, Changed appendLOVTag() to generate HTML tag correctly for Ctrl+k functionality. 
 * 2010/03/24 buhilk Bug 89729, Changed hasTemplate() method to check field template only if the page is a web-feature.
 * 2010/01/20 amiklk Bug 88151, Changed NUM_FIELD_MAX_LENGTH to 16. Max accurate IEEE float length.
 * 2009/08/14 sumelk Bug 84822, Changed enumerateValues() to show empty select box when enumerate method not returning a value.
 * 2009/07/15 amiklk Bug 84690, added setCustomValidation(boolean,string,string) and setCustomValidation(boolean,string,string,string), 
 *                              changed appendValidationFunction() to 	pass GET or POST for __connect() client js function.
 * 2009/03/25 amiklk Bug 81679, changed appendValidationTag() & getMaxlength() for number field maxlength limitation
 * 2008/08/20 buhilk Bug 76491, Overloaded setLabel() method to retrieve usage id for pre-translated labels.
 * 2008/08/15 buhilk Bug 76288, Modofied getAggregateReference() to return String instead of null when empty.
 * 2008/06/26 mapelk Bug 74852, Programming Model for Activities. 
 * 2008/04/21 buhilk Bug 72855, Added new APIs to support rich menus/table cells.
 * 2008/04/04 buhilk Bug 72854, Added functionality to add a RWC key name to support RWC links commands.
 *                              Added setRWCKeyName() and getRWCKeyName().
 * 2008/03/27 sadhlk Bug 72361, Added setAsLongYear(), isLongYear() and repalceWithLongYear() to set date format.
 * 2008/03/26 buhilk Bug 72676, Removed append of Ctrl+k function code from within appendLOVTag() and moved it to ASPBlockLayout.writeCloumn()
 * 2008/01/21 sumelk Bug 69852, Changed appendCheckFunction() to send the block name as a parameter for formatDate_(). 
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/11/29 buhilk Bug Id 69678. Fixed cloning error by removing the ASPField array "hyperlink_in_fields".
 * 2007/06/22 sumelk Merged the corrections for Bug 65852, Changed getLOVURL(), getScriptLOVURL(), appendLOVFunction(),  
 *                   appendURL() & isLOVActive() to enable LOV's in find mode which have hidden in-parameters.
 * 2007/04/20 rahelk Bug id 63951. Added method to pass translation key.
 * 2007/04/17 buhilk Bug 64311, Added findValues() method to search values from a given Array
 * 2007/04/11 buhilk Bug 64050, Modified appendCalendarTag() to pass custom validation method if available.
 * 2007/03/28 buhilk IID F1PR1449 added ctrl+k functionality to lov fields
 * 2007/03/01 buhilk Bug 63870, Improves getUsageID()
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/12/21 sumelk Bug 62620, Modified convertToClientString() to handle the date fields with null value.
 * 2006/11/22 rahelk Bug 61532 Added functionality for decimal accurate fields
 * 2006/09/29 gegulk removed the usages of depricated method getUsageID() 
 * 2006/09/26 gegulk Bug 60797, Modified the method prepareClientValue() to avoid returning the
 *                   global value when a value is passed in
 *
 * 2006/08/29 gegulk Bug 60172, Added the methods isDirtyHeightFlag() isDirtySizeFlag()
 *
 * 2006/08/07 buhilk Bug 59442, Corrected Translatins in Javascript
 *
 * 2006/08/01 rahelk Bug id 59663, fixed bug in setHyperLink regarding standard portal mode
 *
 * 2006/06/06 riralk Bug 58551, Fixed setLabel() to avoid Null pointer exception.
 *
 * 2006/05/17 riralk Bug 57749, Terms related improvements in web client. Modified setLabel() to support code part labels.
 *
 * 2006/05/16 mapelk Improved "What's This?" functionality to show help as a tool tip
 *                2006/02/22           prralk
 * B133922 in ST, made getIidDBValues public
 *                2006/02/09           prralk
 * B133326 in ST, added method to dirty span attribute, to fix error in Edit Address dlg
 *                2006/01/09           rahelk
 * Added public method to disable calendar tag
 *
 *                2006/01/09           mapelk
 * Improved regional settings support in the profile
 *
 * Revision 1.14 2005/12/21 mapelk 
 * Generate validate functions even for Hidden fields. Because they can be Visible run time. 
 *
 * Revision 1.13 2005/12/01 mapelk 
 * Bug fix in prfile formats 
 *
 * Revision 1.12  2005/11/23 mapelk
 * Bug fix: Wrong date validation when profile mask taken.
 *
 * Revision 1.11  2005/11/23 riralk
 * Modified getLovQueries() used getApplicationContext() when building the key to fetch profiles
 *
 * Revision 1.10  2005/11/09 05:58:30  sumelk
 * Merged the corrections for Bug 47881, Added javadoc comments.
 *
 * Revision 1.9  2005/11/08 07:50:54  rahelk
 * core changes for using USAGES in help
 *
 * Revision 1.8  2005/10/25 11:06:13  mapelk
 * Introduced different validations for Number & Money. Also replaces ASCII 160 with 32 which returns as group seperator for some languages.
 *
 * Revision 1.7  2005/10/14 09:08:13  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.6  2005/10/11 12:24:29  rahelk
 * changed access controler of getTranslatedMask to public
 *
 * Revision 1.5  2005/10/04 08:25:36  madrse
 *
 * Revision 1.4  2005/09/23 08:40:27  mapelk
 * Merged from package 16
 *
 * Revision 1.3  2005/09/22 12:39:22  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.2  2005/09/20 08:35:23  mapelk
 * check for invalid numbers in each number filed's OnChange() event
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.18.2.1  2005/08/26 06:04:26  mapelk
 * Fixed call 126650: Problems with dynamic lov in-fields. Also fixed generating dirty scripts even the field is not dirty.
 *
 * Revision 1.18  2005/07/27 12:19:54  riralk
 * Modified getLovQueries() to work with new profile framewrok also made it private.
 *
 * Revision 1.17  2005/07/25 12:19:42  mapelk
 * Make setPasswordField() method immutable. Depricated setRadioButtons( String iid_package, String db_values ).
 *
 * Revision 1.16  2005/06/15 11:15:03  rahelk
 * CSL 2: bug fix: default values
 *
 * Revision 1.15  2005/06/07 07:47:02  mapelk
 * Bugfix in removeing lable from CSL page
 *
 * Revision 1.14  2005/06/06 07:29:02  rahelk
 * Restructured BlockProfile to handle both queries and default values
 *
 * Revision 1.13  2005/05/04 05:32:00  rahelk
 * Layout profile support for groups
 *
 * Revision 1.12  2005/04/27 08:51:27  sumelk
 * Merged the corrections for Bug 50830, Replaced getLabel() with getJSLabel() in appendCheckFunction() method.
 *
 * Revision 1.11  2005/04/18 04:51:59  sumelk
 * Merged the corrections for Bug 50526, Changed isLOVActive().
 *
 * Revision 1.10  2005/04/08 06:05:36  riralk
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.9  2005/03/12 15:47:24  marese
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.8.2.1  2005/03/04 10:55:01  mapelk
 * bug fix: call 122296, 1222348 and 122422
 *
 * Revision 1.8  2005/02/24 08:53:51  mapelk
 * Improved automatic security checks
 *
 * Revision 1.7  2005/02/15 08:45:10  mapelk
 * Bug fix: Project link call 121294: Static LOVs with query string disapear. And removed tabale property icon if not necessory.
 *
 * Revision 1.6  2005/02/11 09:12:09  mapelk
 * Remove ClientUtil applet and it's usage from the framework
 *
 * Revision 1.5  2005/02/03 11:12:59  mapelk
 * Remove application path form lov tags
 *
 * Revision 1.4  2005/02/03 04:22:28  mapelk
 * access modifiers of getTypeId() and getMask() changed to public.
 *
 * Revision 1.3  2005/02/02 09:36:26  mapelk
 * bug fix: Lov script error and image generation bug appeared after proxy related changes.
 *
 * Revision 1.2  2005/02/01 10:32:58  mapelk
 * Proxy related bug fix: Removed path dependencies from webclientconfig.xml and changed the framework accordingly.
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.7  2005/01/21 10:41:18  rahelk
 * Bug 48132 merged, Added method appendTooltipTag.
 *
 * Revision 1.6  2005/01/07 10:26:58  marese
 * Merged changes made on the PKG10 branch back to HEAD
 *
 * Revision 1.5.2.1  2004/12/29 12:16:41  riralk
 * Fxied Call id 120932
 *
 * Revision 1.5  2004/12/13 11:36:15  mapelk
 * Added method setClearValidatedFieldsOnEmpty(boolean) to change the validation behavior
 *
 * Revision 1.4  2004/11/25 05:58:01  chdelk
 * Added support for Activity APIs based LOVs.
 *
 * Revision 1.3  2004/11/22 10:35:56  chdelk
 * Clonned String variable reference_field.
 *
 * Revision 1.2  2004/11/22 04:55:58  chdelk
 * Modified/Added APIs to support Activities in Master-Detail pages.
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.record.*;

import java.util.*;
import java.io.*;
import java.text.*;


/**
 * A class that represents a HTML field on an ASP page.
 * Each field has a name, data type, format mask and other properties.
 * The following functionality may be attached to an ASPField:
 *<p>
 * <b>Validation</b> - a HTTP request send with out reloading the
 *                     page to the Web server and copies the
 *                     result into other HTML fields
 *
 *<p>
 * <b>Check</b> - the browser executes a JavaScript code
 *<p>
 * <b>LOV</b>   - the browser performs a HTTP request
 *                to the Web server and presents the result
 *                in a new window. The selected value
 *                is copied into a HTML field
 *<p>
 * The information stored in an ASPField object is used to generate HTML tags
 * and attributes as well as JavaScript code to be executed by a browser.
 *<p>
 * No public constructor available. So that to construct an instance
 * you should call the addField methods in ASPBlock.
 * @see ASPBlock#addField(String)
 * @see ASPBlock#addField(String, String)
 * @see ASPBlock#addField(String, String, String)
 */
public class ASPField extends ASPPageSubElement
{
   public static String FND_ACCURATE = "_FND_NONACC_VALUE";
   //==========================================================================
   //  Transient Attributes
   //
   //  Not preserved by serialization. Must be initialized in every new
   //  instance created by load(), clone() or new.
   //==========================================================================

   private transient DataFormatter formatter, pre_formatter, accurate_client_formatter;
   private transient String        app_name;
   private transient String        js_app_name;
   private transient String        global_value;
   private transient String        variable_name;
   private transient String        tag_variable_name;

   //==========================================================================
   //  Transient Dirty-Flags
   //
   //  They are false when the field is DEFINED.
   //  The are not saved() nor cloned(). They are set to true if the
   //  corresponding JavaScript function should be re-generated.
   //
   //  Validation depends on:
   //     mandatory
   //     validation_*
   //     select_box
   //     label
   //
   //  LOV depends on:
   //     lov_*
   //     select_box
   //
   //  Check depends on:
   //     mandatory
   //     read_only
   //     insertable
   //     label
   //==========================================================================

   private transient boolean dirty_validate;
   private transient boolean dirty_check;
   private transient boolean dirty_lov;

   //==========================================================================
   //  Persistent Attributes
   //
   //  They are preserved by serialization.
   //
   //  Immutable attributes may be modified only during the pre-define phase,
   //  until they are frozen. There is no pre-variables for these attributes.
   //
   //  Mutable attributes may be modified in state DEFINED moving the object
   //  to state DIRTY. Default (pre-defined) values of mutable attributes are
   //  stored in pre-variables. They are used to return to state DEFINED
   //  during the reset() process.
   //
   //  lov_active, hyperlink_in_fields are NOT persistant
   //
   //==========================================================================

   private boolean default_not_visible;        private boolean pre_default_not_visible;
   private String name;
   private String db_name;
   private String label;                       private String pre_label;
   private String usage_id;                    private String pre_usage_id;
   private String translate_key;               private String pre_translate_key;
   private String label_js;                    private String pre_label_js;
   private String title;                       private String pre_title;

   private int type_id;
   private boolean mask_given;
   private String mask;                        private String pre_mask;
   private String translated_mask;             private String pre_translated_mask;

   private int title_wrap;
   // Added by Terry 20120821
   // Control column wrap in ASPTable
   private int field_wrap;
   // Added end
   private boolean simple;                     private boolean pre_simple;

   private boolean hilite;                     private boolean pre_hilite;
   private boolean bold;                       private boolean pre_bold;

   private int size;                           private int     pre_size;
   private int max_length;                     private int     pre_max_length;

   private String  hyperlink_url;              private String  pre_hyperlink_url;
   // Added by Terry 20121218
   // Can use ASPField to set Hyperlink URL.
   private String  hyperlink_field_url;        private String  pre_hyperlink_field_url;
   // Added end
   private String  hyperlink_parameters;       private String  pre_hyperlink_parameters;
   private String  hyperlink_method;           private String  pre_hyperlink_method;
   private boolean hyperlink_new_browser;      private boolean pre_hyperlink_new_browser;
   private boolean hyperlink_as_javascript;    private boolean pre_hyperlink_as_javascript;
   private String hyperlink_pres_obj_id;       private String  pre_hyperlink_pres_obj_id;

   private boolean mandatory;                  private boolean pre_mandatory;
   private boolean read_only;                  private boolean pre_read_only;
   private boolean insertable;                 private boolean pre_insertable;
   // Modified by Terry 20130619
   // Change upper_case to mutable
   // Original:
   // private boolean upper_case;
   private boolean upper_case;                 private boolean pre_upper_case;
   // Modified end
   private boolean hidden;                     private boolean pre_hidden;
   private boolean iid_db_field;
   private boolean radio_buttons;
   // Modified by Terry 20130619
   // Change check_box to mutable
   // Original:
   // private boolean check_box;
   private boolean check_box;                  private boolean pre_check_box;
   // Modified end
   private boolean select_box;                 private boolean pre_select_box;
   private boolean search_on_db_column;

   //rahelk
   private boolean address_field;
   private String  lov_view_name;

   private String mandatory_error_message;

   private String  lov_url;                    private String pre_lov_url;
   private String  lov_in_parameters;          private String pre_lov_in_parameters;
   private int     lov_width;                  private int    pre_lov_width;
   private int     lov_height;                 private int    pre_lov_height;
   private Buffer  lov_property;               private Buffer pre_lov_property;
   private boolean lov_active;                 private boolean pre_lov_active;
   private boolean dynamic_lov;                private boolean pre_dynamic_lov;
   private boolean lov_multichoice;
   
   // Added by Terry 20140911
   // Save conditional lov buffer
   private Buffer  conditional_lov;            private Buffer pre_conditional_lov;
   // Added end
   
   // Added by Jack Zhang,20100930 10:59:41
   private String sv_fields;
   private String sv_values;
   private boolean sv_force = false;
   // Added end
   
   // Added by Terry 20110627
   private String client_func;              private String pre_client_func;
   // Added end
   
   // Added by Terry 20120717
   // Control background color in ASPTable...
   private String bg_color;                    private String pre_bg_color;
   private String font_color;                  private String pre_font_color;
   private String font_content;                private String pre_font_content;
   private Buffer font_property;               private Buffer pre_font_property;
   // Added end
   
   // Added by Terry 20100309
   // enable lov multichoice in new or edit layout
   private boolean lov_multichoice_ne;
   // Added end
   
   // Added by Terry 20120831
   private boolean wf_title = false;
   private int     wf_title_order;
   private boolean wf_title_value_only = false;
   // Added end

   private int     height     = 1;             private int  pre_height = 1;
   // number of rows; used to determine if field should be drawn as TEXTAREA
   // default is one row (drawn as regular input field).

   private int    labelSpan   = 1;             private int  pre_labelSpan = 1;
   private int    dataSpan    = 1;             private int  pre_dataSpan  = 1;
   private int    skip        = 1;             private int  pre_skip      = 1;


   private String validate_function;

   private boolean tooltip;                   private boolean pre_tooltip;
   private String tooltip_label;              private String pre_tooltip_label;
   private ASPField tooltip_field;            private ASPField pre_tooltip_field;

   // Modified by Terry 20130619
   // Modified validation_url, validation_method, validation_in_parameters, validation_out_parameters, validation_clear_fields_on_empty to mutable
   // Original:
   // private String validation_url;
   // private String validation_method;
   // private String validation_in_parameters;
   // private String validation_out_parameters;
   // private boolean validation_clear_fields_on_empty = true;
   private String validation_url;             private String pre_validation_url;
   private String validation_method;          private String pre_validation_method;
   private String validation_in_parameters;   private String pre_validation_in_parameters;
   private String validation_out_parameters;  private String pre_validation_out_parameters;
   private boolean validation_clear_fields_on_empty = true;
   private boolean pre_validation_clear_fields_on_empty = true;
   // Modified end

   // Modified by Terry 20130619
   // Modified has_setfunction, function_where_text, function_call_text, function_parameters to mutable
   // Original:
   // private boolean has_setfunction;
   // private String function_where_text;
   // private String function_call_text;
   // private String function_parameters;
   private boolean has_setfunction;            private boolean pre_has_setfunction;
   private String function_where_text;         private String pre_function_where_text;
   private String function_call_text;          private String pre_function_call_text;
   private String function_parameters;         private String pre_function_parameters;
   // Modified end

   // Modified by Terry 20130619
   // Modified iid_package, iid_db_values, iid_client_values, enumerate_method to mutable
   // Original:
   // private String iid_package;
   // private String[] iid_db_values;
   // private String[] iid_client_values;
   // private String enumerate_method;
   private String iid_package;                 private String pre_iid_package;
   private String[] iid_db_values;             private String[] pre_iid_db_values;
   private String[] iid_client_values;         private String[] pre_iid_client_values;
   private String enumerate_method;            private String pre_enumerate_method;
   // Modified end

   private String global_name;
   private String global_url;
   private String global_debug_value;

   // Modified by Terry 20130619
   // Modified alignment to mutable
   // Original:
   // private String  alignment = ALIGN_LEFT;
   private String  alignment = ALIGN_LEFT;
   private String  pre_alignment = ALIGN_LEFT;
   // Modified end
   
   private boolean queryable = true;           private boolean pre_queryable = true;
   private boolean is_multichoice_lov;
   private boolean enable_iid_to_lov;
   private boolean disable_calendar_tag;

   private ASPPopup lov_popup;
   private boolean show_lov_popup = true;
   private boolean password_type;

   private String reference_field;
   private boolean activity_lov;
   private boolean is_long_year = false;

   private int group_id = -1;
   
   private FndAttribute template;
   private String aggregate;
   private String rwc_name;
   private boolean expandable;                  private boolean pre_expandable;
   private int exp_tabs;
   private boolean is_image_field;
   private boolean has_popup_menu;
   private ASPPopup custom_popup_menu;
   private String custom_popup_label;
   
   // Added by Terry 20140822
   // Conditional Mandatory property of field
   private Buffer validate_field_ofcm;          private Buffer pre_validate_field_ofcm;
   private Buffer condition_mandatory;          private Buffer pre_condition_mandatory;
   // Added end
   
   //==========================================================================
   //  Cache Attributes
   //
   //  Transient, immutable attributes with late initialization
   //  (just before the first use).
   //==========================================================================

   private transient String function_text;
   private transient String function_db_parameters;
   private transient ASPField[] lov_in_fields;
   private transient ASPField[] pre_lov_in_fields;
   private transient String[]   lov_in_aliases;
   private transient ASPField[] validation_in_fields;
   private transient String[]   validation_in_aliases;
   private transient ASPField[] validation_out_fields;

   //==========================================================================
   //  Transient temporary variables
   //==========================================================================

   private AutoString tmpbuf = new AutoString();
   private transient boolean used_as_parameter;

   private boolean is_accuracy_fld;
   private String accuracy_fld_name;
   private String validation_http_method = GET;   

   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPField");
   /**
    * Constant used to specifies the alignment to left.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("DELIVERY_TYPE").
    *        setAlignment(ASPField.ALIGN_LEFT);
    * </pre>
    * @see #ALIGN_CENTER
    * @see #ALIGN_RIGHT
    */
   public static final String ALIGN_LEFT   = "LEFT";
   /**
    * Constant used to specifies alignment to right.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("DELIVERY_TYPE")
    *       .setAlignment(ASPField.ALIGN_RIGHT);
    * </pre>
    * @see #ALIGN_LEFT
    * @see #ALIGN_CENTER
    */
   public static final String ALIGN_RIGHT  = "RIGHT";

   /**
    * Constant used to specifies alignment to center.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("DELIVERY_TYPE").
    *       .setAlignment(ASPField.ALIGN_CENTER);
    * </pre>
    * @see #ALIGN_LEFT
    * @see #ALIGN_RIGHT
    */
   public static final String ALIGN_CENTER = "CENTER";

   /**
    * Constant used to specifies HTTP "GET" method. This can be used when setting a
    * hyperlink to a ASPField. If the HTTP method is set to GET then only the url will be
    * included in the HTTP request.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("CUSTOMER_ID").
    *        setHyperlink("Customer.page","CUSTOMER_ID",ASPField.GET);
    *
    *    Note that here the url will be
    *    "http://server/ifsapp/demorw/Customer.page?CUSTOMER_ID=01"
    * </pre>
    * @see #POST
    * @see #NEWWIN
    * @see #setHyperlink(String, String, String)
    */
   public final static String GET    = "GET";
   /**
    * Constant used to specifies HTTP "POST" method. This can be used when setting a
    * hyperlink to a ASPField. If HTTP method is set to POST then the url and all HTML
    * fields will be included in the HTTP request.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("CUSTOMER_ID").
    *        setHyperlink("Customer.page","CUSTOMER_ID",ASPField.POST);
    *
    * </pre>
    * @see #GET
    * @see #NEWWIN
    * @see #setHyperlink(String, String, String)
    */
   public final static String POST   = "POST";
   /**
    * Constant used to show the requested url in a new browser window
    * when setting a hyperlink to the ASPField.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("CUSTOMER_ID").
    *        setHyperlink("Customer.page","CUSTOMER_ID",ASPField.NEWWIN);
    *
    * </pre>
    * @see #GET
    * @see #POST
    * @see #setHyperlink(String, String, String)
    */
   public final static String NEWWIN = "NEWWIN";
   public final static String JAVASCRIPT = "JAVASCRIPT";

   private final static String CHR1 = "\1";
   private final static int NUM_FIELD_MAX_LENGTH=16;
   
   // Added by Terry 20120821
   // Constant to set WRAP property in ASPTable
   public static final int WITHTABLE       = 0;
   public static final int SETNOWRAP       = 1;
   public static final int SETWRAP         = 2;
   // Added end
   
   //==========================================================================
   //  Construction:
   //
   //  The constructor and construct() function must initialize all transient
   //  non-cache variables.
   //
   //  fetchGlobalValue() must be called too!
   //==========================================================================

   ASPField( ASPBlock block )
   {
      super(block);
   }

   ASPField construct( String name, int type_id, String mask  ) throws Exception
   {
      return construct(name, type_id, mask, null);
   }
   
   ASPField construct( String name, int type_id, String mask, FndAttribute template  ) throws Exception
   {
      ASPPage page = getASPPage();

      this.template = template;
      this.name = name;
      this.rwc_name = name;
      this.app_name = IfsNames.dbToAppName(name);
      this.type_id = type_id;
      this.mask = mask;
      this.formatter = page.getASPConfig().getDataFormatter(type_id,mask);
      pre_formatter = formatter; 
      page.register(this);
      this.js_app_name = app_name;
      this.variable_name = name.toLowerCase();
      this.tag_variable_name = variable_name + "_tag";
      this.title_wrap = 0;
      // Added by Terry 20120821
      // Set field_wrap DEFAULT Value to WITHTABLE
      this.field_wrap = WITHTABLE;
      // Added end
      this.is_multichoice_lov = page.getASPConfig().isMultiChoiceLovEnabled();
      this.enable_iid_to_lov  = page.getASPConfig().isFindModeIidToLovEnabled();

      if(isDateTime() || isTime())
         translateDateTimeMask(mask);
      return this;
   }

   //==========================================================================
   //  Bufferable interface
   //
   //  save() and load() ignore pre-variables (default values for mutable
   //  attributes), because reset() from the state LOADED_DIRTY is not allowed.
   //==========================================================================

   void save( Buffer into )
   {
      Buffers.save( into, "SHOWLOVPOPUP",  show_lov_popup  );
      Buffers.save( into, "NAME",     name     );
      Buffers.save( into, "DB_NAME",  db_name  );
      Buffers.save( into, "LABEL",    label    );
      Buffers.save( into, "LABELJS",    label_js    );
      Buffers.save( into, "TITLE",    title );
      Buffers.save( into, "TYPE_ID",  type_id  );
      Buffers.save( into, "MASK",     mask     );
      Buffers.save(into, "TRANSLATED_MASK", translated_mask );

      Buffers.save( into, "SIZE",       size       );
      Buffers.save( into, "MAX_LENGTH", max_length );

      Buffers.save( into, "SIMPLE",     simple     );

      Buffers.save( into, "TITLE_WRAP",     title_wrap     );

      Buffers.save( into, "HEIGHT", height );

      Buffers.save( into, "LABELSPAN", labelSpan );
      Buffers.save( into, "DATASPAN", dataSpan );
      Buffers.save( into, "SKIP", skip );

      Buffers.save( into, "HYPERLINK_URL"         , hyperlink_url         );
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      Buffers.save( into, "HYPERLINK_FIELD_URL"   , hyperlink_field_url   );
      // Added end
      Buffers.save( into, "HYPERLINK_PARAMETERS"  , hyperlink_parameters  );
      Buffers.save( into, "HYPERLINK_METHOD"      , hyperlink_method      );
      Buffers.save( into, "HYPERLINK_NEW_BROWSER" , hyperlink_new_browser );
      Buffers.save( into, "HYPERLINK_AS_JAVASCRIPT", hyperlink_as_javascript);
      Buffers.save( into, "HYPERLINK_PRES_OBJ_ID" , hyperlink_pres_obj_id );

      Buffers.save( into, "MANDATORY"      , mandatory     );
      Buffers.save( into, "READ_ONLY"      , read_only     );
      Buffers.save( into, "INSERTABLE"     , insertable    );
      Buffers.save( into, "UPPER_CASE"     , upper_case    );
      Buffers.save( into, "HIDDEN"         , hidden        );
      Buffers.save( into, "EXPANDABLE"     , expandable    );
      Buffers.save( into, "IMAGE_FIELD"    , is_image_field);
      Buffers.save( into, "POPUP_FIELD"    , has_popup_menu);
      Buffers.save( into, "IID_DB_FIELD"   , iid_db_field  );
      Buffers.save( into, "RADIO_BUTTONS"  , radio_buttons );
      Buffers.save( into, "CHECK_BOX"      , check_box     );
      Buffers.save( into, "SELECT_BOX"     , select_box    );
      Buffers.save( into, "GROUP_ID"       , group_id      );

      Buffers.save( into, "SEARCH_ON_DB_COLUMN"     , search_on_db_column     );
      Buffers.save( into, "MANDATORY_ERROR_MESSAGE" , mandatory_error_message );

      Buffers.save( into, "LOV_URL"           , lov_url           );
      Buffers.save( into, "PRE_LOV_URL"       , pre_lov_url       );
      Buffers.save( into, "LOV_IN_PARAMETERS" , lov_in_parameters );
      Buffers.save( into, "PRE_LOV_IN_PARAMETERS" , pre_lov_in_parameters );
      Buffers.save( into, "LOV_WIDTH"         , lov_width         );
      Buffers.save( into, "PRE_LOV_WIDTH"         , pre_lov_width         );
      Buffers.save( into, "LOV_HEIGHT"        , lov_height        );
      Buffers.save( into, "PRE_LOV_HEIGHT"        , pre_lov_height        );
      Buffers.save( into, "LOV_PROPERTY"      , lov_property      );
      Buffers.save( into, "PRE_LOV_PROPERTY"      , pre_lov_property      );
      Buffers.save( into, "LOV_MULTICHOICE"   , lov_multichoice   );
      
      Buffers.save( into, "VALIDATE_FUNCTION"            , validate_function            );

      Buffers.save( into, "VALIDATION_URL"            , validation_url            );
      Buffers.save( into, "VALIDATION_METHOD"         , validation_method         );
      Buffers.save( into, "VALIDATION_IN_PARAMETERS"  , validation_in_parameters  );
      Buffers.save( into, "VALIDATION_OUT_PARAMETERS" , validation_out_parameters );
      Buffers.save( into, "VALIDATION_CLEARWHENEMPTY" , validation_clear_fields_on_empty);


      Buffers.save( into, "HAS_SETFUNCTION", has_setfunction     );
      Buffers.save( into, "FUNCTION_WHERE_TEXT"       , function_where_text       );
      Buffers.save( into, "FUNCTION_CALL_TEXT"        , function_call_text        );
      Buffers.save( into, "FUNCTION_PARAMETERS"       , function_parameters       );

      Buffers.save( into, "IID_PACKAGE"       , iid_package       );
      Buffers.save( into, "IID_DB_VALUES"     , iid_db_values     );
      Buffers.save( into, "IID_CLIENT_VALUES" , iid_client_values );
      Buffers.save( into, "ENUMERATE_METHOD"  , enumerate_method  );

      Buffers.save( into, "GLOBAL_NAME"  , global_name   );
      Buffers.save( into, "GLOBAL_URL"   , global_url    );

      Buffers.save( into, "ALIGNMENT"    , alignment     );
      Buffers.save( into, "QUERYABLE"    , queryable     );

      Buffers.save( into, "PASSWORD_TYPE", password_type     );
      Buffers.save(into, "LONG_YEAR",is_long_year);

      Buffers.save( into, "RWC_NAME", rwc_name);
      
      // Added by Terry 20120821
      // Save new properties to Buffers
      Buffers.save( into, "FIELD_WRAP"         , field_wrap          );
      Buffers.save( into, "SV_FIELDS"          , sv_fields           );
      Buffers.save( into, "SV_VALUES"          , sv_values           );
      Buffers.save( into, "SV_FORCE"           , sv_force            );
      Buffers.save( into, "CLIENT_FUNC"        , client_func         );
      Buffers.save( into, "BG_COLOR"           , bg_color            );
      
      Buffers.save( into, "FONT_COLOR"         , font_color          );
      Buffers.save( into, "FONT_CONTENT"       , font_content        );
      Buffers.save( into, "FONT_PROPERTY"      , font_property       );
      Buffers.save( into, "PRE_FONT_PROPERTY"  , pre_font_property   );
      
      Buffers.save( into, "LOV_MULTICHOICE_NE" , lov_multichoice_ne  );
      Buffers.save( into, "WF_TITLE"           , wf_title );
      Buffers.save( into, "WF_TITLE_ORDER"     , wf_title_order );
      Buffers.save( into, "WF_TITLE_VALUE_ONLY", wf_title_value_only );
      
      // Added by Terry 20140822
      // Conditional Mandatory property of field
      Buffers.save( into, "VALIDATE_FIELD_OFCM",     validate_field_ofcm     );
      Buffers.save( into, "PRE_VALIDATE_FIELD_OFCM", pre_validate_field_ofcm );
      Buffers.save( into, "CONDITION_MANDATORY",     condition_mandatory     );
      Buffers.save( into, "PRE_CONDITION_MANDATORY", pre_condition_mandatory );
      // Added end
      
      // Added by Terry 20140911
      // Save conditional lov buffer
      Buffers.save( into, "CONDITIONAL_LOV",     conditional_lov     );
      Buffers.save( into, "PRE_CONDITIONAL_LOV", pre_conditional_lov );
      // Added end
   }

   void load( Buffer from ) throws Exception
   {
      setLoaded();
      construct( Buffers.loadString(from,"NAME"),
                 Buffers.loadInt(from,"TYPE_ID"),
                 Buffers.loadString(from,"MASK") );

      show_lov_popup   = Buffers.loadBoolean( from, "SHOWLOVPOPUP" );
      db_name   = Buffers.loadString( from, "DB_NAME" );
      label     = Buffers.loadString( from, "LABEL"   );
      label_js  = Buffers.loadString( from, "LABELJS"   );
      title     = Buffers.loadString( from, "TITLE"   );

      size       = Buffers.loadInt  ( from, "SIZE"       );
      max_length = Buffers.loadInt  ( from, "MAX_LENGTH" );

      title_wrap = Buffers.loadInt  ( from, "TITLE_WRAP" );

      simple = Buffers.loadBoolean  ( from, "SIMPLE" );

      height = Buffers.loadInt  ( from, "HEIGHT" );

      labelSpan = Buffers.loadInt( from, "LABELSPAN" );
      dataSpan = Buffers.loadInt( from, "DATASPAN" );
      skip = Buffers.loadInt( from, "SKIP" );

      hyperlink_url         = Buffers.loadString( from, "HYPERLINK_URL");
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      hyperlink_field_url   = Buffers.loadString( from, "HYPERLINK_FIELD_URL");
      // Added end
      hyperlink_parameters  = Buffers.loadString( from, "HYPERLINK_PARAMETERS");
      hyperlink_method      = Buffers.loadString( from, "HYPERLINK_METHOD");
      hyperlink_new_browser = Buffers.loadBoolean(from, "HYPERLINK_NEW_BROWSER");
      hyperlink_as_javascript=Buffers.loadBoolean( from, "HYPERLINK_AS_JAVASCRIPT");
      hyperlink_pres_obj_id = Buffers.loadString(from, "HYPERLINK_PRES_OBJ_ID");

      mandatory     = Buffers.loadBoolean( from, "MANDATORY"    );
      read_only     = Buffers.loadBoolean( from, "READ_ONLY"    );
      insertable    = Buffers.loadBoolean( from, "INSERTABLE"   );
      upper_case    = Buffers.loadBoolean( from, "UPPER_CASE"   );
      hidden        = Buffers.loadBoolean( from, "HIDDEN"       );
      expandable    = Buffers.loadBoolean( from, "EXPANDABLE"   );
      is_image_field= Buffers.loadBoolean( from, "IMAGE_FIELD"  );
      has_popup_menu= Buffers.loadBoolean( from, "POPUP_FIELD"  );
      iid_db_field  = Buffers.loadBoolean( from, "IID_DB_FIELD" );
      radio_buttons = Buffers.loadBoolean( from, "RADIO_BUTTONS");
      check_box     = Buffers.loadBoolean( from, "CHECK_BOX"    );
      select_box    = Buffers.loadBoolean( from, "SELECT_BOX"   );
      group_id      = Buffers.loadInt( from, "GROUP_ID"     );

      search_on_db_column     = Buffers.loadBoolean( from, "SEARCH_ON_DB_COLUMN" );
      mandatory_error_message = Buffers.loadString ( from, "MANDATORY_ERROR_MESSAGE" );

      lov_url           = Buffers.loadString( from, "LOV_URL"           );
      pre_lov_url       = Buffers.loadString( from, "PRE_LOV_URL"       );
      lov_in_parameters = Buffers.loadString( from, "LOV_IN_PARAMETERS" );
      pre_lov_in_parameters = Buffers.loadString( from, "PRE_LOV_IN_PARAMETERS" );
      lov_width         = Buffers.loadInt   ( from, "LOV_WIDTH"         );
      pre_lov_width     = Buffers.loadInt   ( from, "PRE_LOV_WIDTH"         );
      lov_height        = Buffers.loadInt   ( from, "LOV_HEIGHT"        );
      pre_lov_height    = Buffers.loadInt   ( from, "PRE_LOV_HEIGHT"        );
      lov_property      = Buffers.loadBuffer( from, "LOV_PROPERTY"      );
      pre_lov_property  = Buffers.loadBuffer( from, "PRE_LOV_PROPERTY"      );
      lov_multichoice   = Buffers.loadBoolean( from, "LOV_MULTICHOICE"  );

      validate_function            = Buffers.loadString( from, "VALIDATE_FUNCTION"            );

      validation_url            = Buffers.loadString( from, "VALIDATION_URL"            );
      validation_method         = Buffers.loadString( from, "VALIDATION_METHOD"         );
      validation_in_parameters  = Buffers.loadString( from, "VALIDATION_IN_PARAMETERS"  );
      validation_out_parameters = Buffers.loadString( from, "VALIDATION_OUT_PARAMETERS" );
      validation_clear_fields_on_empty = Buffers.loadBoolean( from, "VALIDATION_CLEARWHENEMPTY");


      has_setfunction           = Buffers.loadBoolean( from, "HAS_SETFUNCTION"       );
      function_where_text       = Buffers.loadString( from, "FUNCTION_WHERE_TEXT"       );
      function_call_text        = Buffers.loadString( from, "FUNCTION_CALL_TEXT"        );
      function_parameters       = Buffers.loadString( from, "FUNCTION_PARAMETERS"       );
      translated_mask           = Buffers.loadString( from, "TRANSLATED_MASK"           );

      iid_package   = Buffers.loadString(          from, "IID_PACKAGE"       );
      iid_db_values = Buffers.loadStringArray(     from, "IID_DB_VALUES"     );
      iid_client_values = Buffers.loadStringArray( from, "IID_CLIENT_VALUES" );
      enumerate_method  = Buffers.loadString(      from, "ENUMERATE_METHOD"  );

      global_name = Buffers.loadString( from, "GLOBAL_NAME" );
      global_url  = Buffers.loadString( from, "GLOBAL_URL"  );
      fetchGlobalValue();

      alignment   = Buffers.loadString(  from, "ALIGNMENT" );
      queryable   = Buffers.loadBoolean( from, "QUERYABLE" );

      password_type = Buffers.loadBoolean( from, "PASSWORD_TYPE");
      is_long_year  = Buffers.loadBoolean(from, "LONG_YEAR");
      rwc_name = Buffers.loadString( from, "RWC_NAME");
      
      // Added by Terry 20120821
      // Load saved values of new properties from Buffers
      field_wrap          = Buffers.loadInt(     from, "FIELD_WRAP"         );
      sv_fields           = Buffers.loadString(  from, "SV_FIEELDS"         );
      sv_values           = Buffers.loadString(  from, "SV_VALUES"          );
      sv_force            = Buffers.loadBoolean( from, "SV_FORCE"           );
      client_func         = Buffers.loadString(  from, "CLIENT_FUNC"        );
      bg_color            = Buffers.loadString(  from, "BG_COLOR"           );
      
      font_color          = Buffers.loadString(  from, "FONT_COLOR"         );
      font_content        = Buffers.loadString(  from, "FONT_CONTENT"       );
      font_property       = Buffers.loadBuffer(  from, "FONT_PROPERTY"      );
      pre_font_property   = Buffers.loadBuffer(  from, "PRE_FONT_PROPERTY"  );
      
      lov_multichoice_ne  = Buffers.loadBoolean( from, "LOV_MULTICHOICE_NE" );
      wf_title            = Buffers.loadBoolean( from, "WF_TITLE"           );
      wf_title_order      = Buffers.loadInt(     from, "WF_TITLE_ORDER"     );
      wf_title_value_only = Buffers.loadBoolean( from, "WF_TITLE_VALUE_ONLY");
      // Added end
      
      // Added by Terry 20140822
      // Conditional Mandatory property of field
      validate_field_ofcm     = Buffers.loadBuffer( from, "VALIDATE_FIELD_OFCM"     );
      pre_validate_field_ofcm = Buffers.loadBuffer( from, "PRE_VALIDATE_FIELD_OFCM" );
      condition_mandatory     = Buffers.loadBuffer( from, "CONDITION_MANDATORY"     );
      pre_condition_mandatory = Buffers.loadBuffer( from, "PRE_CONDITION_MANDATORY" );
      // Added end
      
      // Added by Terry 20140911
      // Save conditional lov buffer
      conditional_lov     = Buffers.loadBuffer( from, "CONDITIONAL_LOV"     );
      pre_conditional_lov = Buffers.loadBuffer( from, "PRE_CONDITIONAL_LOV" );
      // Added end
   }

   //==========================================================================
   //  ASPPoolElement logic
   //==========================================================================

   /**
    * Copy the default value of every mutable attribute (pre-variable) to
    * its current value. This method is called when unlocking a dirty page
    * from the pool.
    * @see #doFreeze
    */
   protected void doReset()
   {
      
      if (DEBUG)
         debug(" Resetting ASPField " +  name);

      label                 = pre_label;
      usage_id              = pre_usage_id;
      translate_key         = pre_translate_key;
      label_js              = pre_label_js;
      title                 = pre_title;
      size                  = pre_size;
      max_length            = pre_max_length;
      hilite                = pre_hilite;
      bold                  = pre_bold;
      height                = pre_height;

      labelSpan            = pre_labelSpan;
      dataSpan             = pre_dataSpan;
      skip                 = pre_skip;

      default_not_visible   = pre_default_not_visible;
      hyperlink_url         = pre_hyperlink_url;
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      hyperlink_field_url   = pre_hyperlink_field_url;
      // Added end
      hyperlink_parameters  = pre_hyperlink_parameters;
      hyperlink_method      = pre_hyperlink_method;
      hyperlink_new_browser = pre_hyperlink_new_browser;
      hyperlink_as_javascript=pre_hyperlink_as_javascript;
      hyperlink_pres_obj_id = pre_hyperlink_pres_obj_id;

      lov_url           = pre_lov_url;
      lov_in_parameters = pre_lov_in_parameters;
      lov_width         = pre_lov_width;
      lov_height        = pre_lov_height;
      lov_property      = pre_lov_property;
      lov_active        = pre_lov_active;
      lov_in_fields     = pre_lov_in_fields;

      mandatory  = pre_mandatory;
      read_only  = pre_read_only;
      insertable = pre_insertable;
      // Added by Terry 20130619
      // Change upper_case to mutable
      upper_case = pre_upper_case;
      // Added end
      hidden     = pre_hidden;
      expandable = pre_expandable;
      // Added by Terry 20130619
      // Change check_box to mutable
      check_box  = pre_check_box;
      // Added end
      select_box = pre_select_box;
      // Added by Terry 20130619
      // Modified alignment to mutable
      alignment = pre_alignment;
      // Added end
      queryable  = pre_queryable;
      simple     = pre_simple;

      tooltip               = pre_tooltip;
      tooltip_field         = pre_tooltip_field;
      tooltip_label          = pre_tooltip_label;

      dirty_validate = false;
      dirty_check    = false;
      dirty_lov      = false;

      global_value = null;

      tag_values = null;

      dynamic_lov = pre_dynamic_lov;
      //password_type = false;

      show_lov_popup = true;

      if(lov_popup!=null && lov_popup.isUndefined())
         lov_popup = null;

      if(custom_popup_menu!=null && custom_popup_menu.isUndefined())
      {
         custom_popup_menu = null;
         has_popup_menu = false;
         custom_popup_label = null;
      }
      
      if (DEBUG)
         debug("    --- Resetting mask from " + mask + " to "  + pre_mask);
      
      formatter = pre_formatter;
      translated_mask = pre_translated_mask;
      mask = pre_mask;
      
      // Added by Terry 20120821
      // Reset values
      client_func = pre_client_func;
      bg_color = pre_bg_color;
      font_color = pre_font_color;
      font_content = pre_font_content;
      font_property = pre_font_property;
      // Added end
      
      // Added by Terry 20130619
      // Modified iid_package, iid_db_values, iid_client_values, enumerate_method to mutable
      iid_package = pre_iid_package;
      iid_db_values = pre_iid_db_values;
      iid_client_values = pre_iid_client_values;
      enumerate_method = pre_enumerate_method;
      // Added end
      
      // Added by Terry 20130619
      // Modified validation_url, validation_method, validation_in_parameters, validation_out_parameters, validation_clear_fields_on_empty to mutable
      validation_url = pre_validation_url;
      validation_method = pre_validation_method;
      validation_in_parameters = pre_validation_in_parameters;
      validation_out_parameters = pre_validation_out_parameters;
      validation_clear_fields_on_empty = pre_validation_clear_fields_on_empty;
      // Added end
      
      // Added by Terry 20130619
      // Modified has_setfunction, function_where_text, function_call_text, function_parameters to mutable
      has_setfunction = pre_has_setfunction;
      function_where_text = pre_function_where_text;
      function_call_text = pre_function_call_text;
      function_parameters = pre_function_parameters;
      // Added end
      
      // Added by Terry 20140822
      // Conditional Mandatory property of field
      validate_field_ofcm = pre_validate_field_ofcm;
      condition_mandatory = pre_condition_mandatory;
      // Added end
      
      // Added by Terry 20140911
      // Save conditional lov buffer
      conditional_lov = pre_conditional_lov;
      // Added end
   }

   /**
    * Copy the current value of every mutable attribute to its default value (pre-variable).
    * This method is called just before putting the page to the page pool. These pre-variable
    * used to reset the mutable attributes when unlocking a dirty page in the page pool.
    * @see #doReset
    */
   protected void doFreeze() throws FndException
   {
      pre_label                 = label;
      pre_usage_id              = usage_id;
      pre_translate_key         = translate_key;
      pre_label_js              = label_js;
      pre_title                 = title;
      pre_size                  = size;
      pre_max_length            = max_length;
      pre_hilite                = hilite;
      pre_bold                  = bold;
      pre_height                = height;
      pre_labelSpan             = labelSpan;
      pre_dataSpan              = dataSpan;
      pre_skip                  = skip;

      pre_default_not_visible   = default_not_visible;
      pre_hyperlink_url         = hyperlink_url;
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      pre_hyperlink_field_url   = hyperlink_field_url;
      // Added end
      pre_hyperlink_parameters  = hyperlink_parameters;
      pre_hyperlink_method      = hyperlink_method;
      pre_hyperlink_new_browser = hyperlink_new_browser;
      pre_hyperlink_as_javascript=hyperlink_as_javascript;
      pre_hyperlink_pres_obj_id = hyperlink_pres_obj_id;

      pre_lov_url           = lov_url;
      pre_lov_in_parameters = lov_in_parameters;
      pre_lov_width         = lov_width;
      pre_lov_height        = lov_height;
      pre_lov_property      = lov_property;
      pre_lov_active        = lov_active;
      pre_lov_in_fields     = lov_in_fields;

      pre_mandatory  = mandatory;
      pre_read_only  = read_only;
      pre_insertable = insertable;
      // Added by Terry 20130619
      // Change upper_case to mutable
      pre_upper_case = upper_case;
      // Added end
      pre_hidden     = hidden;
      pre_expandable = expandable;
      // Added by Terry 20130619
      // Change check_box to mutable
      pre_check_box  = check_box;
      // Added end
      pre_select_box = select_box;
      // Added by Terry 20130619
      // Modified alignment to mutable
      pre_alignment = alignment;
      // Added end
      pre_queryable  = queryable;
      pre_simple     = simple;

      pre_tooltip    = tooltip;
      pre_tooltip_field   = tooltip_field;
      pre_tooltip_label    = tooltip_label;
      pre_dynamic_lov = dynamic_lov;
      
      pre_translated_mask = translated_mask;
      pre_mask            = mask;

      // Added by Terry 20110421
      pre_client_func = client_func;
      pre_bg_color = bg_color;
      pre_font_color = font_color;
      pre_font_content = font_content;
      pre_font_property = font_property;
      // Added end
      
      // Added by Terry 20130619
      // Modified iid_package, iid_db_values, iid_client_values, enumerate_method to mutable
      pre_iid_package = iid_package;
      pre_iid_db_values = iid_db_values;
      pre_iid_client_values = iid_client_values;
      pre_enumerate_method = enumerate_method;
      // Added end
      
      // Added by Terry 20130619
      // Modified validation_url, validation_method, validation_in_parameters, validation_out_parameters, validation_clear_fields_on_empty to mutable
      pre_validation_url = validation_url;
      pre_validation_method = validation_method;
      pre_validation_in_parameters = validation_in_parameters;
      pre_validation_out_parameters = validation_out_parameters;
      pre_validation_clear_fields_on_empty = validation_clear_fields_on_empty;
      // Added end
      
      // Added by Terry 20130619
      // Modified has_setfunction, function_where_text, function_call_text, function_parameters to mutable
      pre_has_setfunction = has_setfunction;
      pre_function_where_text = function_where_text;
      pre_function_call_text = function_call_text;
      pre_function_parameters = function_parameters;
      // Modified end
      
      // Added by Terry 20140822
      // Conditional Mandatory property of field
      pre_validate_field_ofcm = validate_field_ofcm;
      pre_condition_mandatory = condition_mandatory;
      // Added end
      
      // Added by Terry 20140911
      // Save conditional lov buffer
      pre_conditional_lov = conditional_lov;
      // Added end
      
      dirty_validate = false;
      dirty_check    = false;
      dirty_lov      = false;
      createInOutParamArrays();
   }


   /**
    * Initiate the instance for the current request. Called by activate() in the super
    * class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
      if( hasGlobalConnection() )
         if( global_debug_value!=null )
            global_value = global_debug_value;
         else
            fetchGlobalValue();
   }
   
   void setMaskGivenProperty(boolean mask_given)
   {
      this.mask_given = mask_given;
   }

   void applyProfilesFormatter(String[] number_formats, String[] currency_formats, String[] date_formats)
   {
      try
      {
         if (DEBUG)
         {   
            debug("Appling profile setting for " + name + " of type " + formatter.getTypeId());
            debug("   mask given : " + mask_given);
         }
         int decimal_sizes;

         switch(formatter.getTypeId())
         {
            case DataFormatter.NUMBER:
               if (!mask_given && number_formats[2]!=null)
                     mask = number_formats[2]; 
               if (isAccurateFld())
               {
                  accurate_client_formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),mask);
                  if (number_formats[0]!=null)
                     ((NumberFormatter)accurate_client_formatter).setDecimalSeparator(number_formats[0].charAt(0));
                  if (number_formats[1]!=null)
                     ((NumberFormatter)accurate_client_formatter).setGroupingSeparator(number_formats[1].charAt(0));
                  
                  String acc_mask = mask.substring(0,mask.indexOf(".")+1);
                  int no_of_dec = getNoOfDecimals();
                  for (int i=0; i<no_of_dec; i++)
                     acc_mask +="#";
                  
                  formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),acc_mask);
               }
               else
                  formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),getMask());
               if (number_formats[0]!=null)
                  ((NumberFormatter)formatter).setDecimalSeparator(number_formats[0].charAt(0));
               if (number_formats[1]!=null)
                  ((NumberFormatter)formatter).setGroupingSeparator(number_formats[1].charAt(0));
               modifyingMutableAttribute("FORMATTER");
               break;               
            case DataFormatter.INTEGER:
               if (!mask_given && number_formats[3]!=null)
                     mask = number_formats[3]; 
               formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),getMask());
               if (number_formats[0]!=null)
                  ((NumberFormatter)formatter).setDecimalSeparator(number_formats[0].charAt(0));
               if (number_formats[1]!=null)
                  ((NumberFormatter)formatter).setGroupingSeparator(number_formats[1].charAt(0));
               modifyingMutableAttribute("FORMATTER");
               break;
            case DataFormatter.MONEY:
               if (!mask_given && currency_formats[2] != null)
                  mask = currency_formats[2];
               if (isAccurateFld())
               {
                  accurate_client_formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),mask);
                  if (currency_formats[0]!=null)
                     ((NumberFormatter)accurate_client_formatter).setDecimalSeparator(currency_formats[0].charAt(0));
                  if (currency_formats[1]!=null)
                     ((NumberFormatter)accurate_client_formatter).setGroupingSeparator(currency_formats[1].charAt(0));
                  
                  String acc_mask = mask.substring(0,mask.indexOf(".")+1);
                  int no_of_dec = getNoOfDecimals();
                  for (int i=0; i<no_of_dec; i++)
                     acc_mask +="#";
                  
                  formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),acc_mask);
               }
               else
                  formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),getMask());
               if (currency_formats[0]!=null)
                  ((NumberFormatter)formatter).setDecimalSeparator(currency_formats[0].charAt(0));
               if (currency_formats[1]!=null)
                  ((NumberFormatter)formatter).setGroupingSeparator(currency_formats[1].charAt(0));
               modifyingMutableAttribute("FORMATTER");
               break;
            case DataFormatter.DATE:
               if (date_formats[0] == null || mask_given)
                  return;
               if (DEBUG)
               {
                  debug("Appling Profile formatter for ASPFeild - " + name + " of type " + formatter.getTypeId());
                  debug("   Original mask : " + getMask() + " changed to " + date_formats[0]);
               }
               formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),date_formats[0]);
               mask = date_formats[0];
               translateDateTimeMask(date_formats[0]);
               modifyingMutableAttribute("FORMATTER");
               break;
            case DataFormatter.TIME:
               if (date_formats[1] == null || mask_given)
                  return;
               if (DEBUG)
               {
                  debug("Appling Profile formatter for ASPFeild - " + name + " of type " + formatter.getTypeId());
                  debug("   Original mask : " + getMask() + " changed to " + date_formats[1]);
               }
               formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),date_formats[1]);
               mask = date_formats[1];
               translateDateTimeMask(date_formats[1]);
               modifyingMutableAttribute("FORMATTER");
               break;
            case DataFormatter.DATETIME:
               if (date_formats[2] == null || mask_given)
                  return;
               if (DEBUG)
               {
                  debug("Appling Profile formatter for ASPFeild - " + name + " of type " + formatter.getTypeId());
                  debug("   Original mask : " + getMask() + " changed to " + date_formats[2]);
               }
               formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),date_formats[2]);
               mask = date_formats[2];
               translateDateTimeMask(date_formats[2]);
               modifyingMutableAttribute("FORMATTER");
               break; 
            default:
               if (DEBUG)
                  debug("---- Match not found.");
         }
      }
      catch (Exception e)
      {
         
      }
      
   }
   
   
   /**
    * Clone this field into a new field included in the specified block.
    * The state of the new field is DEFINED.
    */
   protected ASPPoolElement clone( Object block ) throws FndException
   {
      ASPField f = new ASPField((ASPBlock)block);

      f.formatter = f.pre_formatter = (DataFormatter)pre_formatter.clone();
      f.accurate_client_formatter = accurate_client_formatter;

      f.app_name          = app_name;
      f.js_app_name       = js_app_name;
      f.variable_name     = variable_name;
      f.tag_variable_name = tag_variable_name;

      f.name          = name;
      f.db_name       = db_name;

      f.type_id       = type_id;
      f.mask          = f.pre_mask = pre_mask;
      f.translated_mask = f.pre_translated_mask = pre_translated_mask;

      f.reference_field = reference_field;
      f.activity_lov    = activity_lov;

      f.title_wrap    = title_wrap;

      f.hilite                = f.pre_hilite                = pre_hilite;
      f.bold                  = f.pre_bold                  = pre_bold;
      f.label                 = f.pre_label                 = pre_label;
      f.usage_id              = f.pre_usage_id              = pre_usage_id;
      f.translate_key         = f.pre_translate_key         = pre_translate_key;
      f.label_js              = f.pre_label_js              = pre_label_js;
      f.title                 = f.pre_title                 = pre_title;
      f.size                  = f.pre_size                  = pre_size;
      f.max_length            = f.pre_max_length            = pre_max_length;

      f.height                = f.pre_height                = pre_height;

      f.labelSpan             = f.pre_labelSpan             = pre_labelSpan;
      f.dataSpan              = f.pre_dataSpan              = pre_dataSpan;
      f.skip                  = f.pre_skip                  = pre_skip;

      f.default_not_visible   = f.pre_default_not_visible   = pre_default_not_visible;
      f.hyperlink_url         = f.pre_hyperlink_url         = pre_hyperlink_url;
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      f.hyperlink_field_url   = f.pre_hyperlink_field_url   = pre_hyperlink_field_url;
      // Added end
      f.hyperlink_parameters  = f.pre_hyperlink_parameters  = pre_hyperlink_parameters;
      f.hyperlink_method      = f.pre_hyperlink_method      = pre_hyperlink_method;
      f.hyperlink_new_browser = f.pre_hyperlink_new_browser = pre_hyperlink_new_browser;
      f.hyperlink_as_javascript=f.pre_hyperlink_as_javascript=pre_hyperlink_as_javascript;
      f.hyperlink_pres_obj_id = f.pre_hyperlink_pres_obj_id = pre_hyperlink_pres_obj_id;

      f.mandatory      = f.pre_mandatory  = pre_mandatory;
      f.read_only      = f.pre_read_only  = pre_read_only;
      f.insertable     = f.pre_insertable = pre_insertable;
      // Modified by Terry 20130619
      // Change upper_case to mutable
      // Original:
      // f.upper_case     = upper_case;
      f.upper_case     = f.pre_upper_case = pre_upper_case;
      // Modified end
      f.is_image_field = is_image_field;
      f.hidden         = f.pre_hidden     = pre_hidden;
      f.expandable     = f.pre_expandable = pre_expandable;
      f.exp_tabs        = exp_tabs;
      f.iid_db_field   = iid_db_field;
      f.radio_buttons  = radio_buttons;
      // Modified by Terry 20130619
      // Change check_box to mutable
      // Original:
      // f.check_box      = check_box;
      f.check_box      = f.pre_check_box  = pre_check_box;
      // Modified end
      f.select_box     = f.pre_select_box = pre_select_box;

      f.search_on_db_column     = search_on_db_column;
      f.mandatory_error_message = mandatory_error_message;

      f.lov_url           = f.pre_lov_url           = pre_lov_url;
      f.lov_in_parameters = f.pre_lov_in_parameters = pre_lov_in_parameters;
      f.lov_width         = f.pre_lov_width         = pre_lov_width;
      f.lov_height        = f.pre_lov_height        = pre_lov_height;
      f.lov_property      = f.pre_lov_property      = pre_lov_property;
      f.lov_active        = f.pre_lov_active          = pre_lov_active;
      f.lov_multichoice   = lov_multichoice;

      f.tooltip           = f.pre_tooltip             = pre_tooltip;
      f.tooltip_field     = f.pre_tooltip_field       = pre_tooltip_field;
      f.tooltip_label      = f.pre_tooltip_label        = pre_tooltip_label;

      f.validate_function            = validate_function;

      // Modified by Terry 20130619
      // Modified validation_url, validation_method, validation_in_parameters, validation_out_parameters, validation_clear_fields_on_empty to mutable
      // Original:
      // f.validation_url            = validation_url;
      // f.validation_method         = validation_method;
      // f.validation_in_parameters  = validation_in_parameters;
      // f.validation_out_parameters = validation_out_parameters;
      f.validation_url            = f.pre_validation_url            = pre_validation_url;
      f.validation_method         = f.pre_validation_method         = pre_validation_method;
      f.validation_in_parameters  = f.pre_validation_in_parameters  = pre_validation_in_parameters;
      f.validation_out_parameters = f.pre_validation_out_parameters = pre_validation_out_parameters;
      f.validation_clear_fields_on_empty = f.pre_validation_clear_fields_on_empty = pre_validation_clear_fields_on_empty;
      // Modified end

      // Modified by Terry 20130619
      // Modified has_setfunction, function_where_text, function_call_text, function_parameters to mutable
      // Original:
      // f.has_setfunction = has_setfunction;
      // f.function_where_text = function_where_text;
      // f.function_call_text  = function_call_text;
      // f.function_parameters = function_parameters;
      f.has_setfunction     = f.pre_has_setfunction     = pre_has_setfunction;
      f.function_where_text = f.pre_function_where_text = pre_function_where_text;
      f.function_call_text  = f.pre_function_call_text  = pre_function_call_text;
      f.function_parameters = f.pre_function_parameters = pre_function_parameters;
      // Modified end

      // Modified by Terry 20130619
      // Modified iid_package, iid_db_values, iid_client_values, enumerate_method to mutable
      // Original:
      // f.iid_package       = iid_package;
      // f.iid_db_values     = iid_db_values;
      // f.iid_client_values = iid_client_values;
      // f.enumerate_method  = enumerate_method;
      f.iid_package       = f.pre_iid_package         = pre_iid_package;
      f.iid_db_values     = f.pre_iid_db_values       = pre_iid_db_values;
      f.iid_client_values = f.pre_iid_client_values   = pre_iid_client_values;
      f.enumerate_method  = f.pre_enumerate_method    = pre_enumerate_method;
      // Modified end

      f.global_name = global_name;
      f.global_url  = global_url;
      f.global_debug_value = global_debug_value;

      // Modified by Terry 20130619
      // Modified alignment to mutable
      // Original:
      // f.alignment = alignment;
      f.alignment = f.pre_alignment = pre_alignment;
      // Modified end
      
      f.queryable = f.pre_queryable = pre_queryable;
      f.simple    = f.pre_simple    = pre_simple;

      f.function_text          = function_text;
      f.function_db_parameters = function_db_parameters;

      f.address_field = address_field;
      f.lov_view_name = lov_view_name;

      f.is_multichoice_lov = is_multichoice_lov;
      f.enable_iid_to_lov  = enable_iid_to_lov;

      f.group_id = group_id;
      f.template = template;
      f.aggregate = aggregate;

      ASPBlock b = (ASPBlock)block;

      if(hasLOV())
        f.lov_popup = b.getASPPage().getASPPopup("lov_popup_" + getName());

      f.dynamic_lov = f.pre_dynamic_lov = pre_dynamic_lov;
      f.password_type = password_type;

      f.show_lov_popup = show_lov_popup;
      f.mask_given = mask_given;
      f.is_long_year = is_long_year;

      f.rwc_name = rwc_name;

      f.has_popup_menu = has_popup_menu;
      f.custom_popup_menu = custom_popup_menu;
      f.custom_popup_label= custom_popup_label;
      
      // Added by Terry 20120821
      // Clone new properties
      f.field_wrap          = field_wrap;
      f.sv_fields           = sv_fields;
      f.sv_values           = sv_values;
      f.sv_force            = sv_force;
      f.client_func         = f.pre_client_func = pre_client_func;
      f.bg_color            = f.pre_bg_color = pre_bg_color;
      f.font_color          = f.pre_font_color = pre_font_color;
      f.font_content        = f.pre_font_content = pre_font_content;
      f.font_property       = f.pre_font_property = pre_font_property;
      f.lov_multichoice_ne  = lov_multichoice_ne;
      f.wf_title            = wf_title;
      f.wf_title_order      = wf_title_order;
      f.wf_title_value_only = wf_title_value_only;
      // Added end
      
      // Added by Terry 20140822
      // Conditional Mandatory property of field
      f.validate_field_ofcm = f.pre_validate_field_ofcm = pre_validate_field_ofcm;
      f.condition_mandatory = f.pre_condition_mandatory = pre_condition_mandatory;
      // Added end
      
      // Added by Terry 20140911
      // Save conditional lov buffer
      f.conditional_lov     = f.pre_conditional_lov     = pre_conditional_lov;
      // Added end
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
   //  Name
   //==========================================================================

   /**
    * Return the HTML name of this field. It is an upper-case string.
    */

   public String getName()
   {
      return name;
   }

   String getAppName()
   {
      return app_name;
   }

   String getJavaScriptName()
   {
      return js_app_name;
   }

   /**
    * This method is used to get the JScript variable that corresponds
    * to this field in early versions (in ASPs) of the WebKit.
    */
   public String getVariableName()
   {
      return variable_name;
   }

   /**
    * This method is used to get the JScript tag-variable that corresponds
    * to this field in early versions (in ASPs) of the WebKit.
    */
   public String getTagVariableName()
   {
      return tag_variable_name;
   }

   /**
    * Return the client value of this radio button field or null if assignments are
    * not generated or unless this is a radio button.
    *
    * <pre>
    *    Example:
    *      protected void printContents() throws FndException
    *      {
    *         ...
    *         if (rowset.countRows()>0)
    *         {
    *            ASPField f = getASPField("DELIVERY_TYPE"); //this is a radio button
    *            blk.generateAssignments(); //generate assignments for the relevent ASPBlock
    *            printRadioButton(f.getValue(0), f.getName(), f.getValue(0), rowset.getValue("DELIVERY_TYPE") == f.getValue(0), f.getTag(0) );
    *            printRadioButton(f.getValue(1), f.getName(), f.getValue(1), rowset.getValue("DELIVERY_TYPE") == f.getValue(1), f.getTag(1) );
    *            printRadioButton(f.getValue(2), f.getName(), f.getValue(2), rowset.getValue("DELIVERY_TYPE") == f.getValue(2), f.getTag(2) );
    *
    *         }
    *         ...
    *      }
    *
    *</pre>
    * @param pos the index of the radio button
    * @see ifs.fnd.asp.ASPBlock#generateAssignments
    * @see ifs.fnd.asp.ASPBlock#regenerateAssignments
    * @see #getTag(int)
    * @see #setRadioButtons
    * @see ASPPageProvider#printRadioButton(String,String,String,boolean,String )
    */
   public String getValue( int pos )
   {
      if(DEBUG) debug("ASPField.getValue("+pos+")");

      ASPBlock blk   = getBlock();
      if(blk.getASPRowSet().countRows() == 0) return null;
      int assign_gen = blk.assignmentsGenerated();
      if(assign_gen==blk.ASSIGNMENT_NOT_GENERATED || !isRadioButtons()) return null;

      String valarr[] = getIidClientValues();

      if(DEBUG) debug("  getValue(): valarr["+pos+"]="+valarr[pos]);
      return valarr[pos];
   }

   /**
    * Returns the client value of this field from the row set,
    * or from the Request object after the call to regenerateAssignments().
    * If assigments are not generated the null will be returned. If the assignments
    * generated then following methods follows to return values.
    * <pre>
    * If there is a global connection then the global value is returned.
    * If this is a check box or radio button then just the db value is returned.
    * If this is a select box, generates then the option tags for the IID values are returned.
    * If this is just a filed then the HTML encoded client value is returned.
    *
    * Example:
    * Suppose that blk is an instance of ASPBlock and f1 and f2 are instances of ASPFiled
    * and both are connected to blk. The f1 has been defined as a select box and the f2 is
    * just a field. Following example draws a select box for f1 and text box for f2.
    * See how the contens of the form objects varies from one layout mode to another.
    *
    * public void  adjust()
    * {
    *    blk.generateAssignments();
    *    ...
    * }
    *
    * protected void printContents() throws FndException
    * {
    *    ...
    *    printSelectStart(f1.getName(), f1.getTag());
    *    appendToHTML( f1.getValue());
    *    printSelectEnd();
    *    printField(f2.getName(), f2.getValue() , f2.getTag());
    *    ...
    * }
    * </pre>
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#regenerateAssignments
    * @see #getTag
    */

   public String getValue()// throws FndException
   {
      if(DEBUG) debug("ASPField.getValue()");

      ASPManager mgr  = getASPManager();
      ASPPage    page = getASPPage();
      ASPBlock   blk  = getBlock();
      if(blk.getASPRowSet().countRows() == 0) return null;
      int assign_gen  = blk.assignmentsGenerated();

      if(assign_gen==blk.ASSIGNMENT_NOT_GENERATED) return null;

      String value;
      if( hasGlobalConnection() )
         value = getGlobalValue();
      else if( assign_gen==blk.ASSIGNMENT_REGENERATED )
         value = Util.trimLine( page.readValue(getName()) );
      else
      {
         value = blk.getASPRowSet().getValue(getDbName());
         if( !isRadioButtons() && !isCheckBox() && !isSelectBox() )
         try
         {
            value = convertToClientString(value);
         }
         catch( FndException x )
         {
            error(x);
            //logError(x);
            //value = null;
         }
      }

      if( isRadioButtons() || isCheckBox() )
      {
         if(DEBUG) debug("  getValue(): value="+value);
         return value;
      }
      else if( isSelectBox() )
      {
         //AutoString tmpbuf = new AutoString();
         tmpbuf.clear();
         page.getASPHTMLFormatter().populateListBox( tmpbuf,
                                                     getIidClientValues(),
                                                     value,
                                                     !Str.isEmpty(value) && isMandatory(),
                                                     false );
         if(DEBUG) debug("  getValue(): tmpbuf="+tmpbuf);
         return tmpbuf.toString();
      }
      else  // Field
      {
         String retval = mgr.JScriptEncode(mgr.HTMLEncode(value));
         if(DEBUG) debug("  getValue(): retval="+retval);
         return retval;
      }
   }


   private transient Buffer tag_values;

   /**
    * Return a tag-attribute for the current field, if assignments are generated.
    * <pre>
    * Example:
    * Suppose blk is an instance of the ASPBlock and f is an instance of the ASPField.
    * Following example print a HTML text box for the CUSTOMER_ID with the value and
    * the relevent properties declared for the ASPField f.
    *
    *    public void  adjust()
    *    {
    *       blk.generateAssignments();
    *       ...
    *    }
    *
    *    protected void printContents() throws FndException
    *    {
    *       ...
    *       printField(f.getName(), f.getValue(), f.getTag());
    *       ...
    *    }
    * </pre>
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#regenerateAssignments
    * @see #getValue
    */

   public String getTag()
   {
      return getTag(0);
   }

   /**
    * Return a tag-attribute for the given index. For non radio button fields
    * parameter should set to 0. However it is recomended to use the getTag() method
    * which has no parameter for non Radio button field. To get the values
    * assignments should be generated first. Version for using with radio buttons.
    * <pre>
    *    Example:
    *      protected void printContents() throws FndException
    *      {
    *         ...
    *         if (rowset.countRows()>0)
    *         {
    *            ASPField f = getASPField("DELIVERY_TYPE"); //this is a radio button
    *            blk.generateAssignments(); //generate assignments for the relevent ASPBlock
    *            printRadioButton(f.getValue(0), f.getName(), f.getValue(0), rowset.getValue("DELIVERY_TYPE") == f.getValue(0), f.getTag(0) );
    *            printRadioButton(f.getValue(1), f.getName(), f.getValue(1), rowset.getValue("DELIVERY_TYPE") == f.getValue(1), f.getTag(1) );
    *            printRadioButton(f.getValue(2), f.getName(), f.getValue(2), rowset.getValue("DELIVERY_TYPE") == f.getValue(2), f.getTag(2) );
    *
    *         }
    *         ...
    *      }
    *
    *</pre>
    * @param pos The index of the radio button. This should be 0 if this is not a Radio button
    * field.
    * @see ifs.fnd.asp.ASPBlock#generateAssignments
    * @see ifs.fnd.asp.ASPBlock#regenerateAssignments
    * @see #getValue(int pos)
    * @see #getValue
    * @see #getTag
    * @see #setRadioButtons
    * @see ASPPageProvider#printRadioButton(String,String,String,boolean,String )
    *
    */
   public String getTag( int pos )
   {
      ASPBlock blk = getBlock();
      if(tag_values==null || blk.assignmentsGenerated()==ASPBlock.ASSIGNMENT_NOT_GENERATED)
         return null;
      else if(blk.getASPRowSet().countRows() == 0)
         return null;
      else
         return tag_values.getString(pos);
   }

   void setTagAssignment( String tag_value ) throws FndException
   {
      setTagAssignment(0,tag_value);
   }

   void setTagAssignment( int pos, String tag_value ) throws FndException
   {
      if(DEBUG) debug("ASPField.setTagAssignment("+pos+","+tag_value+")");
      modifyingMutableAttribute("FIELD_TAG");
      if(tag_values==null)
         tag_values = getASPManager().getFactory().getBuffer();
      tag_values.insertItem(new Item(null,tag_value),pos);
   }

   //==========================================================================
   //  Size & Max Length
   //==========================================================================

   /**
    * Returns the size (number of characters) of this ASPField.
    * @see #setSize
    */
   public int getSize()
   {
      return size;
   }

   /**
    * Set the size (number of characters) for this ASPField. Size of an ASPField is
    * a mutable attribute. So that this method can be called even after the page is
    * defined.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    ASPField f   = blk.addField("DELIVERY_TYPE");
    *    f.setSize(10);
    * </pre>
    * @param size size of the ASPField.
    * @see #getSize
    */
   public ASPField setSize( int size )
   {
      try
      {
         modifyingMutableAttribute("SIZE");
         this.size = size;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
   * Returns the height of this ASPField. In other words number of rows.
   * @see #setHeight
   */
   public int getHeight( )
   {
      return this.height;
   }

   /**
    * Set the height (number of rows) for this ASPField. The height of an ASPField is
    * a mutable attribute. So that this method can be called even after the page is defined.
    * Only used in automatically generated dialogs.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    ASPField f   = blk.addField("COMMENTS");
    *    f.setHeight(5);
    * </pre>
    * @see #getHeight
    * @param height the height of the ASPField
    */
   public ASPField setHeight( int height )
   {
      try
      {
         modifyingMutableAttribute("HEIGHT");
         this.height = height;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }


   /**
    * Sets this ASPField to be simple, meaning that in single record
    * mode no label is shown, and the field is outside the layout logic;
    * no HTML TD cell is generated. This method causes to change only a mutable attribute.
    * So that this method can be called even after the page is defined.
    * Only used in automatically generated dialogs.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    ASPField f   = blk.addField("NAME");
    *    f.setSimple();
    * </pre>
    * @see #isSimple
    */
   ASPField setSimple( )
   {
      try
      {
         modifyingMutableAttribute("SIMPLE");
         this.simple = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }


   ASPField unsetSimple( )
   {
      try
      {
         modifyingMutableAttribute("SIMPLE");
         this.simple = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Returns if this ASPField is simple or not. Simple means, in single record
    * mode no label is shown, and the field is outside the layout logic;
    * no HTML TD cell is generated.
    */
   public boolean isSimple()
   {
      return simple;
   }


   /**
    * Returns the max length (number of characters) of this ASPField.
    * @see #setMaxLength
    */
   public int getMaxLength()
   {
      try{
         if( DataFormatter.getBaseTypeId(this.getTypeId())==DataFormatter.NUMBER 
                 && ( max_length>NUM_FIELD_MAX_LENGTH || max_length==0 ) )
            return NUM_FIELD_MAX_LENGTH;
      }
      catch (Throwable any)
      {
         error(any);
      }
      return max_length;
   }

   /**
    * Set the maximum length (number of characters) for this ASPField. This method
    * causes to change only a mutable attribute. So that this can be called
    * even after the page is defined.
    * @param max_length the maximum length of the field.
    * @see #getMaxLength
    */
   public ASPField setMaxLength( int max_length )
   {
      try
      {
         modifyingMutableAttribute("MAX_LENGTH");
         this.max_length = max_length;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }


   //==========================================================================
   //  Data Formatter
   //==========================================================================

   DataFormatter getDataFormatter()
   {
      return formatter;
   }

   /**
    * Returns the field type as integer. You can comapre the type with constants defined  in ifs.fnd.buffer.DataFormatter.
    * <PRE>Example:
    * <CODE>if (ifs.fnd.buffer.DataFormatter.DATE == f.getTypeId())</CODE>
    * </PRE>
    */
   public int getTypeId()
   {
      return type_id;
   }

   /**
    * Returns the format MASK
    */
   public String getMask()
   {
      return mask;
   }
   
   //==========================================================================
   //  Database Name
   //==========================================================================

   /**
    * Returns the database column name connected to this field. The database column
    * name will be the name of this field unless you explicitly set the DbName by
    * calling setDbName().
    * @see #setDbName
    */
   public String getDbName()
   {
      String name_ = Str.isEmpty(db_name) ? name : db_name;
      if (hasAggregateReference())
         return getAggregateReference()+"."+name_; 
      return name_;
   }

   /**
    * Set the database column name connected to this field. The specified name must be
    * unique per ASPBlock. When we define the field names, we must avoid name clashes
    * with other fields in the page because block should be unique per ASPPage. So we invent
    * new name for field names that already exists. To set the right database connection we
    * use this method. The db_name is an immutable attribute and should call when
    * the page is in undefine state.
    * <pre>
    * Example:
    *    ASPBlock item_blk = getASPManager().newASPBlock("ITEM");
    *    ASPField f   = item_blk.addField("ITEM_OBJID" );
    *    f.setDbName("OBJID").
    * </pre>
    * @param db_name The column name of the view.
    * @see #getDbName
    */
   public ASPField setDbName( String db_name )
   {
      try
      {
         modifyingImmutableAttribute("DB_NAME");
         getBlock().checkDbName(this,db_name);
         this.db_name = db_name;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   //==========================================================================
   //  Label
   //==========================================================================

   /**
    * Return the label for this field. If the lable is not defined then the field name
    * will be returned.
    * @see #setLabel
    */
   public String getLabel()
   {
      return Str.isEmpty(label) ? name : label;
   }


   /**
    * Return the javascript encoded label for this field. If the lable is not defined then the field name
    * will be returned.
    * @see #setLabel
    */
   public String getJSLabel()
   {
      return Str.isEmpty(label_js) ? getLabel() : label_js;
   }

   /** Return the title for this field. The difference of title and label is title
    * contains &lt;BR&gt; tags you used to wrap the title.
    * @see #setLabel
    * @see #getLabel
    * @see #getJSLabel
    */
   public String getTitle()
   {
       return title;
   }

   /**
    * Set the label for this field. This lable will be used as the label in non-multirow layout
    * mode and column name as in the multirow layout mode. The label is a mutable attribute.
    * So that this can be called even after the page is defined.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("ORDER_DATE","Date").
    *        setLabel("DEMORWORDERITEMSORDERDATE: Order Date");
    * </pre>
    * @param label Translatable label.
    * @see #getLabel
    * @see ASPBlockLayout
    */
   public ASPField setLabel( String label )
   {
      return setLabel(label, null);
   }
   
   /**
    * Set the label for this field. Can be used to set the label after appending extra text
    * to the label. This lable will be used as the label in non-multirow layout mode and column
    * name as in the multirow layout mode. The label is a mutable attribute. So that this can 
    * be called even after the page is defined.<br><br>
    * NOTE: The label will not be translated, hense the "translated_label" parameter
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    ASPField fld = blk.addField("ORDER_DATE","Date");
    *    String label_text = getASPManager().translate("DEMORWORDERITEMSORDERDATE: Order Date");
    *           label_text += " <extra text> " ;
    *    fld.setLabel("DEMORWORDERITEMSORDERDATE", label_text);
    * </pre>
    * @param label_constant Translatable constant.
    * @param translated_label Translated label.
    * @see #getLabel
    * @see ASPBlockLayout
    */
   public ASPField setLabel(String label_constant, String translated_label)
   {
      try
      {
         ASPManager mgr = getASPManager();
         boolean complex = !mgr.isEmpty(translated_label);
         
         modifyingMutableAttribute("LABEL");
         if( !isUndefined() )
            dirty_validate = dirty_check = true;

         int index = Str.isEmpty(label_constant)?-1:label_constant.indexOf(":");
         if (index>0 || complex)
         {
            translate_key = complex? label_constant : label_constant.substring(0,index);
            usage_id = getASPManager().getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),translate_key);
         }
         
         label_constant = complex? translated_label : label_constant;
         
         title = mgr.translate(label_constant, getASPPage());         
         this.label    = removeBR(title);
         label_js      = removeBR(mgr.translateJavaScript(label_constant));

         if (!Str.isEmpty(label_constant) && !Str.isEmpty(pre_label) && !label_constant.equals(pre_label) && pre_label.startsWith("@") && pre_label.length()==2)                      
             mgr.addCodePartLabels(mgr.getLanguageCode().toUpperCase()+"_"+translate_key, label_constant);             
         
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   private String removeBR(String str)
   {
       ASPManager mgr = getASPManager();
       return mgr.replace(mgr.replace(mgr.replace(mgr.replace(str, "<bR>", ""), "<Br>", ""), "<br>", ""), "<BR>", "");
   }

   //==========================================================================
   //  Alignment and queryable
   //==========================================================================

   /**
    * Return the alignment of this field.
    * @see #ALIGN_CENTER
    * @see #ALIGN_LEFT
    * @see #ALIGN_RIGHT
    * @see #setAlignment
    */
   public String getAlignment()
   {
      return alignment;
   }

   /**
    * Set the alignment of this field. The alignment is an immutable attribute. So that
    * this method should call only when the page is in undefine state.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("STATE").
    *        setAlignment(ASPField.ALIGN_CENTER);
    * </pre>
    * @param alignment Must be one of align constant declared in ASPField.
    * @see #ALIGN_CENTER
    * @see #ALIGN_LEFT
    * @see #ALIGN_RIGHT
    * @see #getAlignment
    */
   public ASPField setAlignment( String alignment )
   {
      try
      {
         // Modified by Terry 20130619
         // Modified alignment to mutable
         // Original:
         // modifyingImmutableAttribute("ALIGNMENT");
         modifyingMutableAttribute("ALIGNMENT");
         // Modified end
         if ( ALIGN_LEFT.equals(alignment) || ALIGN_RIGHT.equals(alignment) || ALIGN_CENTER.equals(alignment) )
            this.alignment = alignment;
         else
            throw new FndException("FNDFLDINVVAL: Invalid value of field alignment.");
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Define this field as queryable. This is a mutable attribute. So that this can be called
    * even after the page is defined and by default any  field is queryable. If the field is set
    * to queryable then it will visible in find layout mode.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if( condition)
    *              getASPField("ORDER_DATE").setQueryable();
    *           else
    *              getASPField("ORDER_DATE").unsetQueryable();
    *        }
    *</pre>
    *
    * @see ASPBlockLayout#FIND_LAYOUT
    * @see #unsetQueryable
    * @see #isQueryable
    * @see ASPPageProvider#getASPField
    */
   public ASPField setQueryable()
   {
      if( queryable ) return this;
      try
      {
         modifyingMutableAttribute("QUERYABLE");
         queryable = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Undefine this field as queryable. This is a mutable attribute and by default any
    * field is queryable. If the field is unset to queryable then it will invisible in find
    * layout mode.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if( condition)
    *              getASPField("ORDER_DATE").setQueryable();
    *           else
    *              getASPField("ORDER_DATE").unsetQueryable();
    *        }
    *</pre>
    *
    * @see ASPBlockLayout#FIND_LAYOUT
    * @see #setQueryable
    * @see #isQueryable
    * @see ASPPageProvider#getASPField
    */
   public ASPField unsetQueryable()
   {
      if( !queryable ) return this;
      try
      {
         modifyingMutableAttribute("QUERYABLE");
         this.queryable = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Return true if this field is queryable.
    * @see #setQueryable
    * @see #unsetQueryable
    */
   public boolean isQueryable()
   {
      return this.queryable;
   }

   //==========================================================================
   //  Hyperlink URL and Parameters
   //==========================================================================

   /**
    * Define a hyperlink for this ASPField using "GET" as the HTTP method.
    * This information is used by an ASPHTMLFormatter and ASPTable during
    * generation of HTML tables.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("CUSTOMER_ID", "Number").
    *        setHyperlink("Customer.page","COMPANY_ID,CUSTOMER_ID");
    * </pre>
    * @param url Target URL.
    * @param field_name List of field names as parameters. Each parameter will get the name and value from
    * from the corresponding ASPField.
    * @see #setHyperlink(String,String,String)
    * @see ASPTable#populate
    * @see ASPHTMLFormatter#populateHyperlinkTable
    *
    */
   public ASPField setHyperlink( String url, String field_names )
   {
      return setHyperlink(url,field_names,GET);
   }

   public ASPField setHyperlink( String url, String field_names, String http_method )
   {
      return setHyperlink(url,field_names,http_method,false);
   }

   // Added by Terry 20121218
   // Can use ASPField to set Hyperlink URL.
   public ASPField setFieldHyperlink( String url, String field_url, String field_names )
   {
      return setFieldHyperlink(url, field_url, field_names, GET);
   }
   
   public ASPField setFieldHyperlink( String url, String field_url, String field_names, String http_method )
   {
      return setHyperlink(url, field_url, field_names, http_method, false);
   }
   // Added end
   
   /**
    * Similar to setHyperLink() but additionaly checks for presentation object security
    * of the specified page. The hyperlink is disabled if PO security is not granted to
    * the logged in user.
    *
    * @see ASPField#setHyperlink
    */


   public ASPField setSecureHyperlink( String url, String field_names )
   {
      return setHyperlink(url,field_names,GET,true);
   }

   public ASPField setSecureHyperlink( String url, String field_names, String http_method )
   {
      return setHyperlink(url,field_names,http_method,true);
   }
   
   /**
    * Define a hyperlink for this ASPField, by specifying an URL string and
    * a list of parameters. Each parameter will get the name and value from
    * from the corresponding ASPField.
    * The third argument is a list of HTTP methods, which must consist of:
    *<pre>
    *   "GET"    : only the url will be included in the HTTP request
    *   "POST"   : the url and all HTML fields will be included in the HTTP request
    *   "NEWWIN" : show the requested url in a new browser window
    * <p>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *     blk.addField("CUSTOMER_ID", "Number").
    *         setHyperlink("Customer.page","COMPANY_ID,CUSTOMER_ID","NEWWIN");
    * </pre>
    * This information is used by an ASPHTMLFormatter and ASPTable during
    * generation of HTML tables.
    *
    * @see ASPTable#populate
    * @see ASPHTMLFormatter#populateHyperlinkTable
    * @see #GET
    * @see #POST
    * @see #NEWWIN
    */
   public ASPField setHyperlink( String url,
                                 String field_names,
                                 String http_method,
                                 boolean secure )
   {
      return setHyperlink(url, null, field_names, http_method, secure);
   }
   
   // Added by Terry 20121218
   // Can use ASPField to set Hyperlink URL.
   public ASPField setHyperlink( String url,
                                 String field_url,
                                 String field_names,
                                 String http_method,
                                 boolean secure )
   {
      try
      {
         modifyingMutableAttribute("HYPERLINK");
         
         // Added by Terry 20121218
         // Can use ASPField to set Hyperlink URL.
         if (!Str.isEmpty(url))
         {
            hyperlink_url = url;
            
            if (getASPManager().isStdPortlet() && hyperlink_url.indexOf("://") < 0 && !hyperlink_url.startsWith("/"))
               hyperlink_url = getASPManager().getASPConfig().getApplicationPath() + "/" + hyperlink_url;
         }
         else
            throw new FndException("FNDFLDHYURLEMPTY: The url can not be empty!");
         // Added end

         // Added by Terry 20121218
         // Can use ASPField to set Hyperlink URL.
         hyperlink_field_url = field_url;
         // Added end
         
         hyperlink_parameters = field_names;
         hyperlink_method = "";
         hyperlink_new_browser = false;
         hyperlink_as_javascript = false;
         hyperlink_pres_obj_id = "";

         String module = "";
         String page_name = "";
         
         if (secure) // build the presentation_obj_id of the page to be hyperlinked
         {
            if (url.indexOf("/") > 0 && url.indexOf(".page") > 0) // hyperlink is set to a page in another module
            {
               module = url.substring(url.indexOf("/") + 1, url.lastIndexOf("/"));
               page_name = url.substring(url.lastIndexOf("/") + 1, url.length());
            }
            else // hyperlink is set to a page in same module
            {
               module = getASPPage().getComponent();
               page_name = url;
            }
         }
         
         if (!Str.isEmpty(module) && !Str.isEmpty(page_name))
            hyperlink_pres_obj_id = module.toUpperCase() + "/" + page_name;

         StringTokenizer st = new StringTokenizer(http_method, ", \t\n\r");
         while (st.hasMoreTokens())
         {
            String token = st.nextToken();
            if (POST.equals(token) || GET.equals(token))
            {
               if (Str.isEmpty(hyperlink_method))
                  hyperlink_method = token;
               else
                  throw new FndException("FNDFLDHYPM: The HTTP method for Hyperlink must be GET or POST, not both!");
            }
            else if (NEWWIN.equals(token))
            {
               if (!hyperlink_new_browser)
                  hyperlink_new_browser = true;
               else
                  throw new FndException("FNDFLDHYPNB: The HTTP method for Hyperlink is already defined to run in a new browser window!");
            }
            else if (JAVASCRIPT.equals(token))
            {
               if (!hyperlink_as_javascript)
                  hyperlink_as_javascript = true;
            }
            else
               throw new FndException("FNDFLDHYPT: Not recognized token in the definition of HTTP method for Hyperlink: '&1'", token);
         }
         
         if (Str.isEmpty(hyperlink_method))
            hyperlink_method = GET;
         if (POST.equals(hyperlink_method) && hyperlink_new_browser)
            throw new FndException("FNDFLDHYPNBP: Not allowed to use the POST HTTP method for Hyperlink with new browser window!");

      } catch (Throwable any) {
         error(any);
      }
      return this;
   }
   // Added end

   /**
    * Return the URL string of the hyperlink associated with this ASPField.
    * @see #setHyperlink(String, String)
    * @see #setHyperlink(String, String, String)
    */
   public String getHyperlinkURL()
   {
      return hyperlink_url;
   }
   
   // Added by Terry 20121218
   // Can use ASPField to set Hyperlink URL.
   public String getHyperlinkFieldURL()
   {
      return hyperlink_field_url;
   }
   // Added end

   /**
    * Return the HTTP method for the hyperlink associated with this ASPField.
    */
   String getHyperlinkMethod()
   {
      return hyperlink_method;
   }

   /**
    * Return true if the hyperlink is defined to start a new browser window.
    */
   boolean isHyperlinkInNewBrowser()
   {
      return hyperlink_new_browser;
   }

   boolean isHyperlinkAsJavascript()
   {
      return hyperlink_as_javascript;
   }
   
   boolean isHyperLinked()
   {
      ASPManager mgr = getASPManager();
      return !mgr.isEmpty(hyperlink_url) || !mgr.isEmpty(hyperlink_method) || hyperlink_as_javascript;
   }
   
   // Added by Terry 20121218
   // Can use ASPField to set Hyperlink URL.
   boolean isFieldHyperLinked()
   {
      return !Str.isEmpty(hyperlink_field_url) && !Str.isEmpty(hyperlink_method);
   }
   // Added end
   
   /**
    * Returns the presenation object id of the page pointed to by the hyperlink.
    * The PO id is set only when setSecureHyperLink() is used. ASPBlockLayout and ASPTable
    * uses this to check security and enable/disable the hyperlink in the page.
    */

   String getHyperlinkedPresObjectId()
   {
      return hyperlink_pres_obj_id;
   }

   /**
    * Return an array of ASPFields/parameters defined for the hyperlink
    * associated with this ASPField.
    */
   ASPField[] getHyperlinkParameters() throws Exception
   {
      return createHyperlinkParameters();
   }

   ASPField[] createHyperlinkParameters() throws Exception
   {
      if( Str.isEmpty(hyperlink_parameters) ) return null;

      Vector v = new Vector();
      StringTokenizer st = new StringTokenizer(hyperlink_parameters," ,\t\r\n");
      while( st.hasMoreTokens() )
      {
         ASPField field = getASPPage().getASPField(st.nextToken());
         v.addElement(field);
      }
      ASPField[] params = new ASPField[v.size()];
      v.copyInto(params);
      return params;
   }
   
   // Added by Terry 20131028
   // getHyperlinkParameters, can accept field alias
   protected void getHyperlinkParameters(Vector fields, Vector aliases) throws Exception
   {
      if (fields == null)
         fields  = new Vector();
      if (aliases == null)
         aliases = new Vector();
      fields.clear();
      aliases.clear();
      if( Str.isEmpty(hyperlink_parameters) ) return;
      
      StringTokenizer main = new StringTokenizer(hyperlink_parameters, ",");
      ASPPage page = getASPPage();
      while( main.hasMoreTokens() )
      {
         String argname, alias;
         StringTokenizer st = new StringTokenizer(main.nextToken());
         switch(st.countTokens())
         {
            case 1:
               argname = st.nextToken();
               alias = argname;
               break;
            case 2:
               argname = st.nextToken();
               alias = st.nextToken();
               break;
            default:
               throw new FndException("FNDBADINPARAM: Invalid syntax for parameter list: '&1'", hyperlink_parameters);
         }
         ASPField arg = page.getASPField(argname);
         fields.addElement(arg);
         aliases.addElement(alias);
      }
   }
   // Added end
   
   // Added by Terry 20121218
   // Can use ASPField to set Hyperlink URL.
   ASPField getHyperlinkField()
   {
      if( Str.isEmpty(hyperlink_field_url) )
         return null;

      return getASPPage().getASPField(hyperlink_field_url);
   }
   // Added end

   //==========================================================================
   //  ASPBlock
   //==========================================================================

   /**
    * Return the reference to the ASPBlock that owns this field.
    * @see ASPBlock#addField
    */
   public ASPBlock getBlock()
   {
      return (ASPBlock)getContainer();
   }

   //==========================================================================
   //  Boolean Flags
   //==========================================================================

   /**
    * Set the value of this ASPField to be displayed in bold font. The bold attribute is
    * a mutable attribute. So that this can be called even after the page is defined.
    * <pre>
    * Example:
    *     ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *     blk.addField("CUSTOMER_ID", "Number").
    *         setBold();
    * </pre>
    * @see #isBold
    * @see #unsetBold
    */
   public ASPField setBold()
   {
      try
      {
         modifyingMutableAttribute("BOLD");
         this.bold = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Turn off the bold flag. Bold fields are displayed in bold font. The bold flag is a
    * mutable attribute. So that this can be called even after the page is defined.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if( condition)
    *              getASPField("ORDER_DATE").unsetBold();
    *           else
    *              getASPField("ORDER_DATE").setBold();
    *        }
    *</pre>
    * @see #isBold
    * @see #setBold
    */
   public ASPField unsetBold()
   {
      try
      {
         modifyingMutableAttribute("BOLD");
         this.bold = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Returns true if this field has bold flag on.
    * @see #setBold
    * @see #unsetBold
    */

   public boolean isBold()
   {
      return this.bold;
   }


   /**
    * Set the Hilite flag for this ASPField. Hilited (or "highlighted",
    * depending on your grammatical aspirations) fields are shown in bold and blue.
    * The highlight flag is a mutable attribute. So that this can be called even
    * after the page is defined.
    * <pre>
    *   Example:
    *     ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *     blk.addField("ORDER_ID").
    *         setHilite();
    * </pre>
    * @see #unsetHilite
    * @see #isHilite
    */
   public ASPField setHilite( )
   {
      try
      {
         modifyingMutableAttribute("HILITE");
         if( !isUndefined() )
            dirty_validate = dirty_check = true;
         hilite = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Turn off the Hilite flag for this ASPField. The highlight flag is a mutable attribute.
    * So that this can be called even after the page is defined.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if( condition)
    *              getASPField("ORDER_DATE").unsetHilite();
    *           else
    *              getASPField("ORDER_DATE").setHilite();
    *        }
    *</pre>
    * @see #setHilite
    * @see #isHilite
    */
   public ASPField unsetHilite()
   {
      if( !hilite ) return this;
      try
      {
         modifyingMutableAttribute("HILITE");
         if( !isUndefined() )
            dirty_validate = dirty_check = true;
         hilite = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Return true if this ASPField is hilited.
    * @see #setHilite
    * @see #unsetHilite
    */
   public boolean isHilite()
   {
      return hilite;
   }

   /**
    * Set the Mandatory flag on for this ASPField. This is a mutable attribute.The Mandatory
    * functionality is triggered by the generated OnChange event handler.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("CUSTOMER_ID", "Number").
    *        setMandatory();
    * </pre>
    * But it works only if the user changes the field's value.
    * To trigger the Check functionality for all fields
    * in an ASPBlock insert a call to the JavaScript method check<block-name>Fields() generated
    * by ASPManager. It may be, for example, an OnClick event handler for a submit button:
    * <pre>
    *   <I>&lt;input type="submit" ... OnClick="return checkMasterFields()"&gt;</I>
    * </pre>
    * or you can redefine standard Commandbar commands to run this Javascript method as a
    * pre function.
    * <pre>
    *   <I>cmdbar.defineCommand(ASPCommandBar.SAVERETURN, null, "checkMasterFields");</I>
    *   //Note that the cmdbar is an instance of ASPCommandBar.
    * </pre>
    *
    * @see #setMandatory(String)
    * @see #unsetMandatory
    * @see #getMandatoryErrorMessage
    * @see #isMandatory
    * @see ASPManager#generateClientScript
    * @see ASPCommandBar#defineCommand(String, String,String)
    */
   public ASPField setMandatory()
   {
      if (isMandatory()) return this;
      return setMandatory(null);
   }

   /**
    * Set the Mandatory flag and the corresponding error message for this ASPField.
    * This method causes to change only a mutable attribute.So that this can be
    * called even after the page is defined.
    * <pre>
    * Example:
    *    ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *    blk.addField("CUSTOMER_ID", "Number").
    *        setMandatory( getASPManager().translate("IFSFNDDEMORWMANDERR: Customer Id is mandatory");
    * </pre>
    * @param error_message Translated error message to display when mandatory filed is empty.
    * @see #setMandatory(String)
    * @see #unsetMandatory
    * @see #isMandatory
    * @see #getMandatoryErrorMessage
    */
   public ASPField setMandatory( String error_message )
   {
      try
      {
         modifyingMutableAttribute("MANDATORY");
         if( !isUndefined() )
            dirty_validate = dirty_check = true;
         mandatory = true;
         mandatory_error_message = error_message;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Return the error message associated with the Mandatory flag of this ASPField.
    * @see #setMandatory(String)
    * @see #setMandatory
    * @see #unsetMandatory
    * @see #isMandatory
    */
   public String getMandatoryErrorMessage()
   {
      return mandatory_error_message;
   }

   /**
    * Turn off the Mandatory flag for this ASPField. This method causes to change only a
    * mutable attribute.So that this can be called even after the page is defined.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if( condition)
    *              getASPField("CUSTOMER_ID").unsetMandatory();
    *           else
    *              getASPField("CUSTOMER_ID").setMandatory();
    *        }
    *</pre>
    * @see #setMandatory(String)
    * @see #setMandatory
    * @see #getMandatoryErrorMessage
    * @see #isMandatory
    */
   public ASPField unsetMandatory()
   {
      if( !mandatory ) return this;
      try
      {
         modifyingMutableAttribute("MANDATORY");
         if( !isUndefined() )
            dirty_validate = dirty_check = true;
         mandatory = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Return true if this ASPField must be entered by a client, false otherwise.
    * @see #setMandatory(String)
    * @see #setMandatory
    * @see #getMandatoryErrorMessage
    * @see #unsetMandatory
    */
   public boolean isMandatory()
   {
      return mandatory;
   }

   /**
    * Return true if this ASPField can be modified, false otherwise.
    * @see #setReadOnly
    * @see #unsetReadOnly
    * @see #isReadOnly(String)
    * @see #setInsertable
    */
   public boolean isReadOnly()
   {
      return read_only;
   }

   /**
    * Return true if this ASPField can be modified in the given row status, false
    * otherwise. For an example, if the field is readonly and insertable then you need
    * this method to check the readonly flag.
    * <B>Example:</B>
    *    if (f.isReadOnly(f.getBlock().getASPRowSet().getRowStatus()))
    *       ...
    *    <I>Here f is an instance of an ASPField.</I>
    * @param row_status State of the row.
    * @see #setInsertable
    * @see #isReadOnly
    * @see #setReadOnly
    * @see #unsetReadOnly
    */
   boolean isReadOnly( String row_status )
   {
      return read_only && !(insertable && ASPRowSet.NEW.equals(row_status));
   }

   /** Set the ReadOnly flag for this ASPField. This flag is a mutable attribute.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setReadOnly();
    * </pre>
    * @see #setInsertable
    * @see #isReadOnly
    * @see #isReadOnly(String)
    * @see ASPManager.generateClientScript
    * @see ASPBlock.generateAssignments
    *
    */
   public ASPField setReadOnly()
   {
      if( read_only ) return this;
      try
      {
         modifyingMutableAttribute("READ_ONLY");
         read_only = true;
         if( !isUndefined() ) dirty_check = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Turn off the ReadOnly flag for this ASPField. This flag is a mutable attribute.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if( condition)
    *              getASPField("ORDER_DATE").unsetReadOnly();
    *           else
    *              getASPField("ORDER_DATE").setReadOnly();
    *        }
    *</pre>
    * @see #setInsertable
    * @see #isReadOnly
    * @see #isReadOnly(String)
    * @see #setReadOnly
    */
   public ASPField unsetReadOnly()
   {
      if( !read_only ) return this;
      try
      {
         modifyingMutableAttribute("READ_ONLY");
         read_only = false;
         if( !isUndefined() ) dirty_check = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }


   /**
    * Return true if this ASPField can be inserted (modified in a NEW row)
    * by a client, false otherwise.
    * @see #setInsertable
    * @see #unsetInsertable
    */
   public boolean isInsertable()
   {
      return insertable;
   }

   /**
    * Set the Insertable flag for this ASPField. This flag is a mutable attribute.
    * So that this can be called even after the page is defined.
    * Insertable fields can be modified when the row state is NEW.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID").
    *           setInsertable();
    * </pre>
    * @see #isInsertable
    * @see #unsetInsertable
    * @see #setReadOnly
    */
   public ASPField setInsertable()
   {
      if( insertable ) return this;
      try
      {
         modifyingMutableAttribute("INSERTABLE");
         insertable = true;
         if( !isUndefined() ) dirty_check = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Turn off the Insertable flag for this ASPField. This flag is a mutable attribute.
    * So that this can be called even after the page is defined.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if( condition)
    *              getASPField("ORDER_DATE").setInsertable();
    *           else
    *              getASPField("ORDER_DATE").unsetInsertable();
    *        }
    *</pre>
    * @see #isInsertable
    * @see #setInsertable
    */
   public ASPField unsetInsertable()
   {
      if( !insertable ) return this;
      try
      {
         modifyingMutableAttribute("INSERTABLE");
         insertable = false;
         if( !isUndefined() ) dirty_check = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }


   /**
    * Return true if this ASPField should be always converted to upper case,
    * false otherwise.
    * @see #setUpperCase
    */
   public boolean isUpperCase()
   {
      return upper_case;
   }

   /**
    * Set the UpperCase flag for this ASPField. If the UpperCase flag is true, then
    * the corresponding HTML field value will be converted to Upper case on OnChange
    * event of the HTML field. The Uppercase is an Immutable attribute and can not
    * call this function after the page is defined.
    * @see #isUpperCase
    */
   public ASPField setUpperCase()
   {
      try
      {
         // Modified by Terry 20130619
         // Change upper_case to mutable
         // Original:
         // modifyingImmutableAttribute("UPPER_CASE");
         modifyingMutableAttribute("UPPER_CASE");
         // Modified end
         upper_case = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   // Added by Terry 20130619
   // Change upper_case to mutable
   public ASPField unsetUpperCase()
   {
      try
      {
         modifyingMutableAttribute("UPPER_CASE");
         upper_case = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   // Added end


   /**
    * Return true if this ASPField has the Hidden flag turned on. This works only for
    * generated HTML objects and they do not appear in any layout mode.
    * @see #setHidden
    * @see #unsetHidden
    * @see ifs.fnd.asp.ASPBlock#generateHiddenFields
    */
   public boolean isHidden()
   {
      return hidden;
   }


   /**
    * Return true if this ASPField has the expandable flag turned on. This works only for
    * generated HTML objects as they become expandable fileds in multirow layout mode.
    * @return true if expandable, false if otherwise.
    * @see #setAsExpandable
    * @see #unsetExpandable
    * @see #ExpandableTabCount
    */
   public boolean isExpandable()
   {
      return expandable;
   }
   
   /**
    * Return the number of columns to tab before showing this fields contents.
    * @return number of cilumns to skip as an integer.
    * @see #setAsExpandable
    * @see #unsetExpandable
    * @see #ExpandableTabCount
    */
   public int ExpandableTabCount()
   {
      return exp_tabs;
   }
   
   /**
    * Set the Hidden flag for this ASPField. This works only for generated HTML objects
    * and they do not appear in any layout mode.  Hidden is a mutable flag.
    * So that this can be called even after the page is defined.
    *
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("COMPANY_ID").
    *           setHidden();
    * </pre>
    *
    * @see #isHidden
    * @see #unsetHidden
    * @see ifs.fnd.asp.ASPBlock#generateHiddenFields
    */
   public ASPField setHidden()
   {
      if( hidden ) return this;
      try
      {
         modifyingMutableAttribute("HIDDEN");
         getBlock().notifyHiddenFlagDirty();
         hidden = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   /**
    * Set the expandable flag for this ASPField. This works only for
    * generated HTML objects as they become expandable fileds in multirow layout mode.
    * Expandable is a mutable flag. 
    * So that this can be called even after the page is defined.
    *
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("MESSAGE_BODY").
    *           setAsExpandable();
    * </pre>
    *
    * @param tabs number of table cell to skip/tab.
    * @see #isExpandable
    * @see #unsetExpandable
    */
   public ASPField setAsExpandable(int tabs)
   {
      if( expandable ) return this;
      try
      {
         modifyingMutableAttribute("EXPANDABLE");
         expandable = true;
         exp_tabs    = tabs;
         getBlock().setExpandableTabs(exp_tabs);
         getBlock().increaseExpandableFields();
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }   
   
   /**
    * Set the image field flag for this ASPField. If the image field flag is true, then
    * the corresponding HTML field will contain an image in the MULTIROW_LAYOUT mode.
    * The image field is an Immutable attribute and can not call this function after the 
    * page is defined.
    * @see #isUpperCase
    */
   public ASPField setAsImageField()
   {
      try
      {
         modifyingImmutableAttribute("IMAGE_FIELD");
         is_image_field = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   /**
    * Set the popup menu for this ASPField. Thia will add a custom popup menu to the field which
    * will be available when you click on the value itself.
    * The popup menu field is an Immutable attribute and can not call this function after the 
    * page is defined.
    */
   public ASPField setAsPopupMenuField(ASPPopup menu, String label)
   {
       try
      {
         if(menu==null) return this;
         modifyingImmutableAttribute("POPUP_FIELD");
         has_popup_menu = true;
         custom_popup_menu = menu;
         custom_popup_label= getASPManager().translate(label);
         setHyperlink(menu.generateCall(), null, JAVASCRIPT);
         getBlock().setPopupField(this);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   /**
    * Return true if this ASPField is a image field, false otherwise.
    * @see #setUpperCase
    */
   public boolean isImageField()
   {
      return is_image_field;
   }
   
   public boolean hasPopupMenu()
   {
      return this.has_popup_menu;
   }
   
   public ASPPopup getCustomPopupMenu()
   {
      return this.custom_popup_menu;
   }
   
   public String getCustomPopupLabel()
   {
      return custom_popup_label;
   }

   /**
    * Turn OFF the Hidden flag for this ASPField. This works only for generated
    * HTML objects.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if( condition)
    *              getASPField("COMPANY_ID").unsetHidden();
    *           else
    *              getASPField("COMPANY_ID").setHidden();
    *        }
    *</pre>
    * @see #isHidden
    * @see #setHidden
    */
   public ASPField unsetHidden()
   {
      if( !hidden ) return this;
      try
      {
         modifyingMutableAttribute("HIDDEN");
         getBlock().notifyHiddenFlagDirty();
         hidden = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   
   /**
    * Turn OFF the expandable flag for this ASPField. This works only for generated
    * HTML objects.
    *<pre>
    *    Example:
    *        public void  adjust()
    *        {
    *           if(condition)
    *              getASPField("MESSAGE_BODY").unsetExpandable();
    *           else
    *              getASPField("MESSAGE_BODY").setAsExpandable();
    *        }
    *</pre>
    * @see #isExpandable
    * @see #setAsExpandable
    */
   public ASPField unsetExpandable()
   {
      if( !expandable ) return this;
      try
      {
         modifyingMutableAttribute("EXPANDABLE");
         expandable = false;
         getBlock().decreaseExpandableFields();
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   /**
    * Set the DefaultNotVisible flag true for this ASPField. Used in the default table profile.
    * If the DefaultNotVisible flag is true then this field disappear from the default
    * table profile.That means the field is hidden in multirow mode. But the user can get it
    * visible using Table properties. This flag is a mutable attribute. So that this can be
    * called even after the page is defined.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setDefaultNotVisible();
    * </pre>
    * @see #isDefaultNotVisible
    * @see #unsetDefaultNotVisible
    */
   public ASPField setDefaultNotVisible()
   {
      try {
         modifyingMutableAttribute("DEFAULTNOTVSIBLE");
         default_not_visible=true;
      }
         catch(Throwable any){error(any);}
      return this;
   }
   /**
    * Is this field set to be default not visible. If it is, then this field disappear from the default
    * table profile.
    * @see #setDefaultNotVisible
    * @see #unsetDefaultNotVisible
    */

   public boolean isDefaultNotVisible()
    {
        return default_not_visible;
    }

    /**
     * Unset the DefaultNotVisible flag. This method causes to change only a mutable attribute.
     * So that this can be called even after the page is defined.
     *<pre>
     *    Example:
     *        public void  adjust()
     *        {
     *           if( condition)
     *              getASPField("ORDER_DATE").unsetDefaultNotVisible();
     *           else
     *              getASPField("ORDER_DATE").setDefaultNotVisible();
     *        }
     *</pre>
     * @see #isDefaultNotVisible
     * @see #setDefaultNotVisible
     */
   public ASPField unsetDefaultNotVisible()
   {
      try {
      modifyingMutableAttribute("DEFAULTNOTVSIBLE");
      default_not_visible=false;
      }
       catch(Throwable any){error(any);}
      return this;
   }

   /**
    * Set the title wrap for this ASPField in multirow mode. In other words
    * the column title (the label) will be warped in mutlirow mode. Used in ASPTable.
    * This is a immutable attribute. So that this should be called before the
    * page is defined.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("ABC").
    *           setLabel("TRANSLATEKEY: Long lable").
    *           setTitleWrap(4);
    * </pre>
    * @param wrap length to wrap the titile of the field. value 0 means not to
    * wrap the title and it is the default .
    * @see #getTitleWrap
    * @deprecated
    * Use <BR> tags where the title should be wrapped when defining the field in preDefine
    * or set <BR> in translated text as required.
    */
   public ASPField setTitleWrap( int wrap )
   {
      try
      {
         modifyingImmutableAttribute("TITLE_WRAP");
         title_wrap = wrap;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

    /**
     * Fetches the value of Title Wrap. 0 means no wrap and it is the default.
     * @see #setTitleWrap
     */

   public int getTitleWrap()
   {
      return title_wrap;
   }

   //==========================================================================
   //  Span
   //==========================================================================

   /**
    * Set the span for this ASPField. Used in autogenerated single-record dialogs.
    */
   ASPField setSpan(int lSpan, int dSpan, int skipped)
   {
      try
      {
         this.setLabelSpan(lSpan);
         this.setDataSpan(dSpan);
         this.setSkip(skipped);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Set the span for this ASPField. Used in autogenerated single-record dialogs.
    */
   ASPField setLabelSpan(int lSpan)
   {
      try
      {
         modifyingMutableAttribute("LABELSPAN");
         this.labelSpan = lSpan;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Set the span for this ASPField. Used in autogenerated single-record dialogs.
    */
   ASPField setDataSpan(int dSpan)
   {
      try
      {
         modifyingMutableAttribute("DATASPAN");
         this.dataSpan = dSpan;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Set the span for this ASPField. Used in autogenerated single-record dialogs.
    */
   ASPField setSkip(int skipped)
   {
      try
      {
         modifyingMutableAttribute("SKIPPED");
         this.skip = skipped;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Set the span for this ASPField. Used in autogenerated single-record dialogs.
    */
   ASPField setSpan(int lSpan, int dSpan)
   {
      try
      {
         this.setLabelSpan(lSpan);
         this.setDataSpan(dSpan);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   int getLabelSpan()
   {
      return labelSpan;
   }

   int getDataSpan()
   {
      return dataSpan;
   }

   int getSkip()
   {
      return skip;
   }

   //==========================================================================
   //  LOV - List Of Values
   //==========================================================================

   /**
    * Returns true if this ASPField has defined LOV, false otherwise.
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    */
   public boolean hasLOV()
   {
      return !Str.isEmpty(lov_url);
   }

   /**
    * Returns the LOV URL defined for this field.
    * @see #hasLOV
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see #getScriptLOVURL
    */
   public String getLOVURL()
   {
      if(!dynamic_lov) return lov_url;
      else return getASPManager().getASPConfig().getRootLocation(false) + lov_url + "&__FIELD=" + getASPManager().URLEncode(getLabel()) + "&__INIT=1&__LOV=Y";
      //else return lov_url + "+ '&__FIELD=" + getASPManager().URLEncode(getLabel()) + "&__INIT=1'";
   }

   /**
    * Returns the LOV URL defined for this field for static script files. The difference between getLOVURL() and this method is that
    * this return the URL with out the hardcoded path name which may differ in proxy environments.
    * @see #hasLOV
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see #getLOVURL
    */
   public String getScriptLOVURL()
   {
      if(!dynamic_lov) return "'" + lov_url + "'";
      ASPConfig config = getASPManager().getASPConfig();
      return config.getRootLocation(true) + " + '" + lov_url + "&__FIELD=" + getASPManager().URLEncode(getLabel()) + "&__INIT=1&__LOV=Y'";
   }

   /**
    * Returns the width of the LOV window.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    */
   public int getLOVWidth()
   {
      return lov_width;
   }

   /**
    * Returns the height of the LOV window.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see #traceLOV
    */
   public int getLOVHeight()
   {
      return lov_height;
   }

   /**
    * Set the LOV URL for this field. The LOV window will have the default size.
    * The url will be included in an Anchor tag associated with this ASPField.
    * If you have standard LOV and if the information are properly exist in VIEW
    * comments it is recomended to use dynamic LOV. Then no need to code a LOV page.
    * When a non standard LOV page is expected, create the LOV page and use
    * setLOV methods. ASPBlockLayout will take care of printing the LOV image just after
    * the field in editable and find layout modes. However it is posible to get the
    * LOV image printed for manualy drawn fields using the getTag method after generating
    * assignments for the ASPBlock.
    *
    * @param url The target URL, where the LOV page located.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public ASPField setLOV( String url )
   {
      return setLOV(url,null,0,0,is_multichoice_lov);
   }

   /**
    * Set the LOV URL for this field.
    * The url will be included in an Anchor tag associated with this ASPField.
    * If you have standard LOV and if the information are properly exist in VIEW
    * comments it is recomended to use dynamic LOV. Then no need to code a LOV page.
    * When a non standard LOV page is expected, create the LOV page and use
    * setLOV methods. ASPBlockLayout will take care of printing the LOV image just after
    * the field in editable and find layout modes. However it is posible to get the
    * LOV image printed for manualy drawn fields using the getTag method after generating
    * assignments for the ASPBlock.
    * The LOV window will have the default size.
    *
    * @param url The target LOV page and you have to first create a LOV page.
    * @param in_parameters comma-separated list of ASPField names.
    * The names and values of the corresponding HTML fields will be appended
    * to the url, by a JavaScript, when the user clicks on the LOV-icon.
    * Each in parameter can have an alias, which will replace the field name
    * in the url.
    * <pre>
    *   Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setLOV("CustomerLov.page","COMPANY COMPANY_ID,CUSTOMER CUSTOMER_ID");
    * </pre>
    * The above example will use the values from the HTML fields COMPANY and CUSTOMER
    * combined with the names COMPANY_ID and CUSTOMER_ID respectivelly.
    *
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public ASPField setLOV( String url, String in_parameters )
   {
      return setLOV(url,in_parameters,0,0,is_multichoice_lov);
   }

   /**
    * Set the LOV URL for this field.
    * The url will be included in an Anchor tag associated with this ASPField.
    * If you have standard LOV and if the information are properly exist in VIEW
    * comments it is recomended to use dynamic LOV. Then no need to code a LOV page.
    * When a non standard LOV page is expected, create the LOV page and use
    * setLOV methods. ASPBlockLayout will take care of printing the LOV image just after
    * the field in editable and find layout modes. However it is posible to get the
    * LOV image printed for manualy drawn fields using the getTag method after generating
    * assignments for the ASPBlock.
    * <pre>
    *   Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setLOV("CustomerLov.page",650, 750);
    * </pre>
    * @param url The target LOV page and you have to first create a LOV page.
    * @param width The width of the LOV page.
    * @param height The height of the LOV.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public ASPField setLOV( String url, int width, int height )
   {
      return setLOV(url,null,width,height,is_multichoice_lov);
   }

   /**
    * Set the LOV URL for this field.
    * The url will be included in an Anchor tag associated with this ASPField.
    * If you have standard LOV and if the information are properly exist in VIEW
    * comments it is recomended to use dynamic LOV. Then no need to code a LOV page.
    *<p>
    * When a non standard LOV page is expected, create the LOV page and use
    * setLOV methods. ASPBlockLayout will take care of printing the LOV image just after
    * the field in editable and find layout modes. However it is posible to get the
    * LOV image printed for manualy drawn fields using the getTag method after generating
    * assignments for the ASPBlock.
    * @param url The target LOV page and you this page should be cteated by yourself.
    * @param in_parameters comma-separated list of ASPField names.
    * The names and values of the corresponding HTML fields will be appended
    * to the url, by a JavaScript, when the user clicks on the LOV-icon.
    * Each in parameter can have an alias, which will replace the field name
    * in the url, for example:
    * <pre>
    *   Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setLOV("CustomerLov.page","COMPANY COMPANY_ID,CUSTOMER CUSTOMER_ID", 650, 700);
    * </pre>
    * will use the values from the HTML fields COMPANY and CUSTOMER combined
    * with the names COMPANY_ID and CUSTOMER_ID respectivelly.
    * @param width The width of the LOV page.
    * @param height The height of the LOV.
    *
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public ASPField setLOV( String url, String in_parameters, int width, int height )
   {
      return setLOV(url,in_parameters,width,height,is_multichoice_lov);
   }
   
   // Added by Terry 20120821
   // Set multi-select in LOV
   public ASPField setLOV( String url, String in_parameters, int width, int height, boolean enable_multichoice )
   {
	   return setLOV(url, in_parameters, width, height, enable_multichoice, false);
   }
   // Added end

   /**
    * Set the LOV URL for this field.
    * The url will be included in an Anchor tag associated with this ASPField.
    * If you have standard LOV and if the information are properly exist in VIEW
    * comments it is recomended to use dynamic LOV. Then no need to code a LOV page.
    *<p>
    * When a non standard LOV page is expected, create the LOV page and use
    * setLOV methods. ASPBlockLayout will take care of printing the LOV image just after
    * the field in editable and find layout modes. However it is posible to get the
    * LOV image printed for manualy drawn fields using the getTag method after generating
    * assignments for the ASPBlock.
    * @param url The target LOV page and you this page should be cteated by yourself.
    * @param in_parameters comma-separated list of ASPField names.
    * The names and values of the corresponding HTML fields will be appended
    * to the url, by a JavaScript, when the user clicks on the LOV-icon.
    * Each in parameter can have an alias, which will replace the field name
    * in the url, for example:
    * <pre>
    *   Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setLOV("CustomerLov.page","COMPANY COMPANY_ID,CUSTOMER CUSTOMER_ID", 650, 700, true);
    * </pre>
    * will use the values from the HTML fields COMPANY and CUSTOMER combined
    * with the names COMPANY_ID and CUSTOMER_ID respectivelly.
    * @param width The width of the LOV page.
    * @param height The height of the LOV.
    * @param enable_multichoice Enable/disable multi choice LOV in find mode.
    *
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public ASPField setLOV( String url, String in_parameters, int width, int height, boolean enable_multichoice, boolean enable_multichoice_ne )
   {
      try
      {
         modifyingMutableAttribute("LOV");
         if( !isUndefined() )
            dirty_lov = true;
         lov_url            = url;
         lov_in_parameters  = in_parameters;
         lov_width          = width;
         lov_height         = height;
         lov_active         = true;
         lov_multichoice    = enable_multichoice;
         // Added by Terry 20120821
         // Set multi-select in LOV
         lov_multichoice_ne = enable_multichoice_ne;
         // Added end

         if (!getASPPage().isPopupExist("lov_popup_" + getName()))
            lov_popup = getASPPage().newASPPopup("lov_popup_" + getName(),this);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Defines this ASPField to have a dynamic LOV. This can be use for IFS standard
    * LOV pages. No need to design the LOV page.
    * The LOV page is generated dynamically at run time. All visible fields shown in
    * the LOV page and their properties are extracted from the view comments in the database.
    * Several LOV properties can be set using the setLOVProperty() method. This LOV page
    * has default size.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setDynamicLOV("DEMO_CUSTOMER");
    * </pre>
    *
    * @param view The name of the VIEW from which records are fetched.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */

   public ASPField setDynamicLOV( String view )
   {
      return setDynamicLOV(view,null,0,0,is_multichoice_lov);
   }

   /**
    * Defines this ASPField to have a dynamic LOV. This can be use for IFS standard
    * LOV pages. No need to design the LOV page.
    * The LOV page is generated dynamically at run time. All visible fields shown in
    * the LOV page and their properties are extracted from the view comments in the database.
    * Several LOV properties can be set using the setLOVProperty() method.
    *
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setDynamicLOV("DEMO_CUSTOMER",642,453);
    * </pre>
    * @param view The name of the VIEW from which records are fetched.
    * @param width The width of the LOV window.
    * @param height The height of the LOV window.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    * @see #isLOVActive( String row_status )
    */
   public ASPField setDynamicLOV( String view, int width, int height )
   {
      return setDynamicLOV(view,null,width,height,is_multichoice_lov);
   }

   /**
    * Defines this ASPField to have a dynamic LOV. This can be use for IFS standard
    * LOV pages. No need to design the LOV page.
    * The LOV page is generated dynamically at run time. All visible fields shown in
    * the LOV page and their properties are extracted from the view comments in the database.
    * Several LOV properties can be set using the setLOVProperty() method. This LOV page
    * has default size.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setDynamicLOV("DEMO_CUSTOMER","COMPANY_ID,CUSTOMER_ID");
    * </pre>
    *
    * @param view The name of the VIEW from which records are fetched.
    * @param in_parameters List of fields used to restrict the number of records shown in
    * the LOV page.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String row_status )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public ASPField setDynamicLOV( String view, String in_parameters )
   {
      return setDynamicLOV(view,in_parameters,0,0,is_multichoice_lov);
   }

   /**
    * Defines this ASPField to have a dynamic LOV. This can be use for IFS standard
    * LOV pages. No need to design the LOV page.
    * The LOV page is generated dynamically at run time. All visible fields shown in
    * the LOV page and their properties are extracted from the view comments in the database.
    * Several LOV properties can be set using the setLOVProperty() method. This LOV page
    * has default size.
    *
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setDynamicLOV("DEMO_CUSTOMER","COMPANY_ID,CUSTOMER_ID",642,453);
    * </pre>
    * @param view The name of the VIEW from which records are fetched.
    * @param in_parameters List of fields used to restrict the number of records shown in
    * the LOV page.
    * @param width The width of the LOV window.
    * @param height The height of the LOV window.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    * @see #isLOVActive( String)
    */
   public ASPField setDynamicLOV( String view, String in_parameters, int width, int height )
   {
      return setDynamicLOV(view,in_parameters,width,height,is_multichoice_lov);
   }
   
   // Added by Terry 20120821
   // Set multi-select in LOV
   public ASPField setDynamicLOV( String view, String in_parameters, int width, int height, boolean enable_multichoice )
   {
      return setDynamicLOV(view, in_parameters, width, height, enable_multichoice, false);
   }
   // Added end

   /**
    * Defines this ASPField to have a dynamic LOV. This can be use for IFS standard
    * LOV pages. No need to design the LOV page.
    * The LOV page is generated dynamically at run time. All visible fields shown in
    * the LOV page and their properties are extracted from the view comments in the database.
    * Several LOV properties can be set using the setLOVProperty() method. This LOV page
    * has default size.
    *
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setDynamicLOV("DEMO_CUSTOMER","COMPANY_ID,CUSTOMER_ID",642,453,true);
    * </pre>
    * @param view The name of the VIEW from which records are fetched.
    * @param in_parameters List of fields used to restrict the number of records shown in
    * the LOV page.
    * @param width The width of the LOV window.
    * @param height The height of the LOV window.
    * @param enable_multichoice Enable/disable multi choice LOV in find mode.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    * @see #isLOVActive( String)
    */
   public ASPField setDynamicLOV( String view, String in_parameters, int width, int height, boolean enable_multichoice, boolean enable_multichoice_ne )
   {
      dynamic_lov = true;

      if (!getASPManager().isEmpty(in_parameters))
      {
         StringTokenizer params = new StringTokenizer(in_parameters,",");
         String field_name = "";
         boolean key_field_exist = false;

         while(params.hasMoreTokens())
         {
            field_name = params.nextToken();
            if((field_name.trim()).equals(this.getName()))
            {
               key_field_exist = true;
               break;
            }
         }

         if (!key_field_exist)
            in_parameters = this.getName() + "," + in_parameters;
      }
      else
         in_parameters = this.getName();
      // Modified by Terry 20120821
      // Original: return setLOV(getDynamicLovURL(view),in_parameters,width,height,enable_multichoice);
      return setLOV(getDynamicLovURL(view),in_parameters,width,height,enable_multichoice,enable_multichoice_ne);
      // Modified end
   }


   private String getDynamicLovURL(String view)
   {
      lov_view_name = view; // for localized address field.

      String url = getASPPage().getASPConfig().getDynamicLOVURL();
      return url + (url.indexOf("?")>0 ? "&" : "?") + "__DYNAMIC_LOV_VIEW=" + view;
      //String url = getASPPage().getASPConfig().getDynamicLOVURL(true);
      //return url + " + '" + (url.indexOf("?")>0 ? "&" : "?") + "__DYNAMIC_LOV_VIEW=" + view+(isActivityLOV()?"&__ACTIVITY_LOV=TRUE":"") + "'";

   }

   // Bug 40900, start
   /**
    * Define a LOV property for this ASPField. Several LOV properties can be set using
    * this method. Following properties can be set using this method.
    * <pre>
    *    "WHERE"       - Additional restrains can be set if required.
    *    "TITLE"       - Title for the list of values can be set.
    *    "AUTO_SEARCH" - Auto serch "Y" or "N".
    *    "GROUP_BY"    - GROUP_BY clause.
    *    "ORDER_BY"    - ORDER_BY clause.
    *    "FORMAT_MASK" - Format mask of the fields in the dynamic LOV can be change.
    *    "COLUMN_TITLES" - View Titles for the columns of the LOV activated from the field. 
    *                      If the column titles in LOV needs to be different from 
    *                      the database column names, they can be listed in the following format.
    *                         "ColumnName1=NewTitle1^ColumnName2=NewTitle2".
    *                      The value of COLUMN_TITLES defined in preDefine() can be changed at the 
    *                      run() method when preferred!.
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("CUSTOMER_ID", "Number").
    *           setDynamicLOV("DEMO_CUSTOMER","COMPANY_ID,CUSTOMER_ID",642,453).
    *           setLOVProperty("WHERE","VALID='TRUE'").
    *           setLOVProperty("TITLE","DEMORWORDERITEMSLOVTITLE: List of valid Customers").
    *           setLOVProperty("FORMAT_MASK","CREDIT_LIMIT=######,####^DISCOUNT=####.00").
    *           setLOVProperty("COLUMN_TITLES","FIRST_NAME=Customer Name^DOB=Birth Day");
    * </pre>
    * @param name The name of the property.
    * @param value The value of the property.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    * @see #isLOVActive( String )
    */
   // Bug 40900, end
   public ASPField setLOVProperty( String name, String value )
   {
      try
      {
         modifyingMutableAttribute("LOV_PROPERTY");
         if( !isUndefined() )
            dirty_lov = true;
         if( lov_property==null )
            lov_property = getASPManager().getFactory().getBuffer();
         lov_property.setItem(name,value);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }


   /**
    * Return the value of named LOV property, or null if the property has not been
    * defined.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String  )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public String getLOVProperty( String name )
   {
      try
      {
         return lov_property==null ? null : lov_property.getString(name,null);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return null;
   }


   /**
    * Deactivate previously defined LOV on this ASPField.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #activateLOV
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    * @see #isLOVActive
    * @see #isLOVActive( String  )
    */
   public void deactivateLOV()
   {
      try
      {
         modifyingMutableAttribute("LOV_ACTIVE");
         lov_active = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Activate previously deactivated LOV on this ASPField. Activating LOV is
    * cause to change only a mutable attribute. So that it can be called even after the
    * page is defined.
    *
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    * @see #deactivateLOV
    * @see #isLOVActive
    * @see #isLOVActive(String)
    */
   public void activateLOV()
   {
      try
      {
         modifyingMutableAttribute("LOV_ACTIVE");
         lov_active = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Return true if the LOV definition for this ASPField is active.
    * It will be automatically inactivated by the FIND dialog if
    * the inparameters are not Global Variables.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive( String  )
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public boolean isLOVActive()
   {
      return isLOVActive("");
   }


   /**
    * Return true if the LOV definition for this ASPField is active for the
    * given row status. It will be automatically inactivated by the FIND dialog if
    * the inparameters are not Global Variables.
    * @param row_status The state of the row.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public boolean isLOVActive( String row_status )
   {
      ASPBlockLayout lay = getBlock().getASPBlockLayout();
      String lov_view = getLOVView();
      if (!Str.isEmpty(lov_view) && !getASPPage().isObjectAccessible(lov_view)) return false;

      //check accessibility (PO sec) for Static LOV pages.
      if (Str.isEmpty(lov_view))
      {
         String lov_url = getLOVURL();
         int index = lov_url.indexOf("?");
         if (index>0)
            lov_url = lov_url.substring(0,index);
         String module = "";
         String page_name = "";

         if ( lov_url.indexOf("/") > 0 ) //hyperlink is set to a page in another module
         {
           module    = lov_url.substring(lov_url.indexOf("/")+1 ,lov_url.lastIndexOf("/"));
           page_name = lov_url.substring(lov_url.lastIndexOf("/")+1,lov_url.length());
         }
         else //hyperlink is set to a page in same module
         {
           module    = getASPPage().getComponent();
           page_name = lov_url;
         }
         String lov_view_pres_obj_id = module.toUpperCase() + "/" + page_name;
         if (!getASPPage().isObjectAccessible(lov_view_pres_obj_id)) return false;

      }

      boolean show_lov = Str.isEmpty(row_status) || !isReadOnly(row_status);
      if (!lay.isFindLayout() || !lov_active) return lov_active && show_lov;
      try {
          ASPField[] in = getLOVInParameters();
          if(in==null) return lov_active && show_lov;
          for(int i=0;i<in.length;i++)
             if((!in[i].hasGlobalConnection() && !in[i].equals(this) && !in[i].isQueryable())) 
                return false;
      }
      catch (Throwable any) { error(any);}
      return lov_active && show_lov;
   }

   ASPField[] getLOVInParameters() throws FndException
   {
      //if( lov_in_fields==null )
      createInOutParamArrays();
      return lov_in_fields;
   }

   /**
    * Write to the trace output the details of the LOV defined for
    * this ASPField.
    * @see #hasLOV
    * @see #getLOVURL
    * @see #getLOVWidth
    * @see #getLOVHeight
    * @see #setLOV( String )
    * @see #setLOV( String, String )
    * @see #setLOV( String,int,int)
    * @see #setLOV( String ,String ,int ,int )
    * @see #setLOV( String ,String ,int ,int ,boolean )
    * @see #setDynamicLOV( String )
    * @see #setDynamicLOV( String ,int ,int )
    * @see #setDynamicLOV( String ,String )
    * @see #setDynamicLOV( String ,String ,int ,int )
    * @see #setDynamicLOV( String ,String ,int ,int ,boolean )
    * @see #setLOVProperty( String ,String )
    * @see #deactivateLOV
    * @see #activateLOV
    * @see #isLOVActive
    * @see #isLOVActive(String)
    * @see ASPManager#generateClientScript
    * @see ASPBlock#generateAssignments
    * @see ASPBlock#generateEmptyAssignments
    * @see #traceLOV
    */
   public ASPField traceLOV()
   {
      try
      {
         if( !isTraceOn() ) return this;
         trace("LOV for ASPField "+getName()+":");
         trace("   lov_url            = "+lov_url);
         trace("   lov_in_parameters  = "+lov_in_parameters);
         trace("   lov_width          = "+lov_width);
         trace("   lov_height         = "+lov_height);
         trace("   lov_multichoice    = "+lov_multichoice);
         // Added by Terry 20120821
         // enable lov multichoice in new or edit layout
         trace("   lov_multichoice_ne = "+lov_multichoice_ne);
         // Added end
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   //==========================================================================
   //  Validation
   //==========================================================================


   /**
    * Define a custom JavaScript validation function to be used for the "onChange"
    * event in the ASPField.
    */
   public ASPField setValidateFunction( String function )
   {

      try
      {
         modifyingImmutableAttribute("VALIDATE_FUNCTION");
         validate_function = function;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Define the validation method for this ASPField. The method will be
    * called by the Browser (with out refreshing) when a user changes the value
    * of the corresponding HTML field.
    *
    * @see ifs.fnd.asp.ASPManager.generateClientScript
    * @see ifs.fnd.asp.ASPBlock.generateAssignments
    * @see ifs.fnd.asp.ASPBlock.generateEmptyAssignments
    */
   public ASPField setValidation( String url,
                                  String method,
                                  String in_parameters,
                                  String out_parameter )
   {
      try
      {
         // Modified by Terry 20130619
         // Modified validation_url, validation_method, validation_in_parameters, validation_out_parameters, validation_clear_fields_on_empty to mutable
         // Original:
         // modifyingImmutableAttribute("VALIDATION");
         modifyingMutableAttribute("VALIDATION");
         // Modified end
         if( !isUndefined() )
            dirty_validate = true;
         validation_url = url;
         validation_method = method;
         validation_in_parameters = in_parameters;
         validation_out_parameters = out_parameter;
         // setHtmlField()
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Define the validation method for this ASPField. The method will be
    * called by the Browser (with out refreshing) when a user changes the value
    * of the corresponding HTML field.
    *
    * @see ifs.fnd.asp.ASPManager.generateClientScript
    * @see ifs.fnd.asp.ASPBlock.generateAssignments
    * @see ifs.fnd.asp.ASPBlock.generateEmptyAssignments
    */
   public ASPField setValidation( String method,
                                  String in_parameters,
                                  String out_parameter )
   {
      try
      {
         setValidation( getASPPage().getASPConfig().getValidationURL(true),
                        method,
                        in_parameters,
                        out_parameter );
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }


   /**
    * Define the validation for this ASPField to be based on the function
    * defined for the named ASPField via a call to setFunction().
    * This function will be called by the Browser (with out refreshing) when a user
    * changes the value of the HTML field corresponding to this ASPField.
    *<p>
    * Note! The Function used for this Validation:
    *<pre>
    *   o must have been defined before a call to this method
    *   o it cannot refer to SQL constants, just to other ASPFields.
    *</pre>
    *
    * @see #setFunction
    */
   public ASPField setValidation( String field_name )
   {
      try
      {
         ASPField into = getASPPage().getASPField(field_name);
         if( !into.isComputable() )
            throw new FndException("FNDFLDNOFUNC: The filed &1 has no defined Function.", field_name);

         int pos = into.function_where_text.indexOf('(');
         String method = into.function_where_text.substring(0,pos);
         setValidation(method,into.function_parameters,field_name);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   // Added by Terry 20130619
   // Modified validation_url, validation_method, validation_in_parameters, validation_out_parameters, validation_clear_fields_on_empty to mutable
   public ASPField unsetValidation()
   {
      try
      {
         modifyingMutableAttribute("VALIDATION");
         validation_url = "";
         validation_method = "";
         validation_in_parameters = "";
         validation_out_parameters = "";
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   // Added end

   /**
    * Calls setCustomValidation(String, String, String)
    * use_http_post : boolean indicating whether to use HTTP POST method for 
    *                 validation request.
    **/
   public ASPField setCustomValidation( boolean use_http_post, String url, String in_parameters, String out_parameters )
   {
      this.validation_http_method = use_http_post? POST: GET;
      return setCustomValidation( url, in_parameters, out_parameters );
   }

   /**
    * Define the validation URL for this ASPField. The specified url will be
    * called by the browser (with out refreshing) when a user changes the value
    * of the corresponding HTML field.
    * The specified URL may be defined relative to the application base.
    *<p>
    * The called ASP page may perform one or more SQL and PL/SQL
    * statements and then write to the Response a "^"-separated string
    * containing one value per specified OUT-parameter. The JavaScript
    * code in the browser will copy each returned value to the corresponding
    * HTML field. The validation process may be aborted by returning a string
    * that begins with the text "No_Data_Found". The rest of such a string may
    * contain a message that will be presented to the user in an Alert Box.
    *
    * @see ifs.fnd.asp.ASPManager.generateClientScript
    * @see ifs.fnd.asp.ASPBlock.generateAssignments
    * @see ifs.fnd.asp.ASPBlock.generateEmptyAssignments
    */
   public ASPField setCustomValidation( String url, String in_parameters, String out_parameters )
   {
      try
      {
         if( !url.startsWith(getASPManager().getProtocol()+":") && !url.startsWith(getASPPage().getASPConfig().getRootLocation(false)) && url.indexOf(ASPConfigFile.APP_PATH)==-1 && url.indexOf(ASPConfigFile.APP_ROOT)==-1 )
             url = getASPPage().getASPConfig().getRootLocation(true)+ "+'" + url + "'";

         setValidation(url,null,in_parameters,out_parameters);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Call setCustomValidation(String,String) passing the URL
    * of this ASP page as the first argument. 
    * use_http_post : boolean indicating whether to use HTTP POST method for 
    *                 validation request.
    **/
   public ASPField setCustomValidation( boolean use_http_post, String in_parameters, String out_parameters )
   {
      this.validation_http_method = use_http_post? POST: GET;
      return setCustomValidation( in_parameters, out_parameters );
   }

   /**
    * Call setCustomValidation(String,String,String) passing the URL
    * of this ASP page as the first argument.
    */
   public ASPField setCustomValidation( String in_parameters, String out_parameters )
   {
      try
      {
         String url = getASPManager().getURL();
         String root = getASPManager().getASPConfig().getRootLocation(false);
         int index = url.indexOf(root);
         if ( index > -1)
            url = ASPConfigFile.APP_ROOT +"+ '" + url.substring(index + root.length()) + "'";
         setCustomValidation( url , in_parameters, out_parameters );
         //setCustomValidation( getASPManager().getURL(), in_parameters, out_parameters );
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Return true if this ASPField has defined Validation, false otherwise.
    */
   boolean hasValidation()
   {
      return !Str.isEmpty(validation_url);
   }

   /**
    * Write to the trace output the details of the Validation defined for
    * this ASPField.
    */
   public ASPField traceValidation()
   {
      try
      {
         if( !isTraceOn() ) return this;
         trace("Validation for ASPField "+getName()+":");
         trace("   validation_url            = "+validation_url           );
         trace("   validation_method         = "+validation_method        );
         trace("   validation_in_parameters  = "+validation_in_parameters );
         trace("   validation_out_parameters = "+validation_out_parameters);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   ASPField[] getValidationInParameters() throws FndException
   {
      if( validation_in_fields==null ) createInOutParamArrays();
      return validation_in_fields;
   }

   ASPField[] getValidationOutParameters() throws FndException
   {
      if( validation_out_fields==null ) createInOutParamArrays();
      return validation_out_fields;
   }



   /**
    */
   private void createInOutParamArrays() throws FndException
   {
      Vector fields  = new Vector();
      Vector aliases = new Vector();

      parseParameterList(lov_in_parameters,fields,aliases,true);
      lov_in_fields  = toASPFieldArray(fields);
      lov_in_aliases = toStringArray(aliases);

      parseParameterList(validation_in_parameters,fields,aliases);
      validation_in_fields  = toASPFieldArray(fields);
      validation_in_aliases = toStringArray(aliases);

      parseParameterList(validation_out_parameters,fields,aliases);
      validation_out_fields = toASPFieldArray(fields);
   }


   //==========================================================================
   //  Computable Fields - Functions
   //==========================================================================

   /**
    * Define a function for this ASPField. The function will be included in
    * the standard Query and it will be executed after any standard command
    * (like New__ or Modify__), if the input arguments have been changed.
    * The returned value will be placed in the
    * result buffer (as an item with the name of this ASPField).
    * The specified text should have valid SQL syntax. It may
    * include SQL contstants and references to other ASPFields from this
    * ASPBlock but NOT to database columns. For example:
    * "Demo_Customer_API.Get_Name(:COMPANY_ID,:CUSTOMER_ID,'TRUE')",
    * where COMPANY_ID and CUSTOMER_ID are names of ASPFields and 'TRUE'
    * is a string literal.
    */
   public ASPField setFunction( String sql_text )
   {
      try
      {
         // Modified by Terry 20130619
         // Modified has_setfunction, function_where_text, function_call_text, function_parameters to mutable
         // Original:
         // modifyingImmutableAttribute("FUNCTION");
         modifyingMutableAttribute("FUNCTION");
         // Modified end
         if ( Str.isEmpty(sql_text) )
            throw new FndException("FNDFLDEFUNC: Not allowed to define an empty function");

         has_setfunction = true;

         AutoString select = new AutoString();
         AutoString call   = new AutoString();
         AutoString params = new AutoString();
         int index         = 0;

         call.append("BEGIN ? := ");

         SQLTokenizer st = new SQLTokenizer(sql_text);

         while(true)
         {
            int type = st.nextToken();
            String value = st.getValue();

            switch(type)
            {
               case StreamTokenizer.TT_EOF:
                  function_where_text  = select.toString();
                  function_call_text   = call.toString() + "; END;";
                  function_parameters  = params.toString();
                  return this;

               case ':':
                  st.mark();
                  st.nextNonSpaceToken();
                  String name = st.getValue();
                  if( IfsNames.isId(name) )
                  {
                     select.append(CHR1+index+CHR1);
                     index++;

                     call.append("?");
                     if( params.length()>0 ) params.append(',');
                     params.append(name);
                  }
                  else
                  {
                     st.reset();
                     select.append(':');
                     call.append(':');
                  }
                  break;

               case '\'':
                  select.append(value);
                  call.append(value);
                  break;

               default:
                  if( type>0 )
                  {
                     select.append((char)type);
                     call.append((char)type);
                  }
                  else
                  {
                     select.append(value);
                     call.append(value);
                  }
            }
         }
      }
      catch(Throwable e)
      {
         error(e);
      }
      return this;
   }
   
   // Added by Terry 20130619
   // Modified has_setfunction, function_where_text, function_call_text, function_parameters to mutable
   public ASPField unsetFunction()
   {
      try
      {
         modifyingMutableAttribute("FUNCTION");
         has_setfunction     = true;
         function_where_text = "";
         function_call_text  = "";
         function_parameters = "";
      }
      catch(Throwable e)
      {
         error(e);
      }
      return this;
   }
   // Added end

   /**
    * Return the SELECT expression defined for this field in a call
    * to setFunction() method. The returned string contains an alias,
    * so that the item name in the result buffer will match the name
    * of this ASPField.
    * @see #setFunction
    */
   public String getSelectExpression()
   {
      tmpbuf.clear();
      // Added by Terry 20131023
      // Check blk non select mark
      if (!getBlock().getFuncFieldsNonSelect())
         tmpbuf.append(getWhereExpression());
      else
         tmpbuf.append("''");
      // Added end
      tmpbuf.append(' ');
      tmpbuf.append(getDbName());
      return tmpbuf.toString();
   }


   /**
    * Return the SELECT expression defined for this field in a call
    * to setFunction() method.
    * @see #setFunction
    */
   public String getWhereExpression()
   {
      if( function_text==null )
         try
         {
            AutoString nr = new AutoString();
            ASPField[] fields = getFunctionASPParameters();
            function_text = function_where_text;

            for (int i=0; i<fields.length; i++)
            {
               nr.clear();
               nr.append(CHR1);
               nr.appendInt(i);
               nr.append(CHR1);
               function_text = Str.replace(function_text, nr.toString(), fields[i].getDbName() );
            }
         }
         catch( Throwable any )
         {
            error(any);
         }
      return function_text;
   }

   // Modified by Terry 20130619
   // Original:
   // String getCallExpression()
   public String getCallExpression()
   {
      return function_call_text;
   }

   ASPField[] getFunctionASPParameters() throws Exception
   {
      Vector v = new Vector();
      StringTokenizer st = new StringTokenizer(function_parameters," ,\n\r\t");
      while( st.hasMoreTokens() )
         v.addElement(getASPPage().getASPField(st.nextToken()));

      ASPField[] fields = new ASPField[v.size()];
      v.copyInto(fields);
      return fields;
   }

   // Modified by Terry 20130619
   // Original:
   // String getFunctionParameters()
   public String getFunctionParameters()
   {
      return function_parameters;
   }


   /**
    * Convert each parameter name to its Db-Name
    */
   String getFunctionDbParameters()
   {
      if( function_db_parameters==null )
      {
         tmpbuf.clear();
         StringTokenizer st = new StringTokenizer(function_parameters," ,\n\r\t");
         while( st.hasMoreTokens() )
         {
            if( tmpbuf.length()>0 ) tmpbuf.append(',');
            tmpbuf.append( getASPPage().getASPField(st.nextToken()).getDbName() );
         }
         function_db_parameters = tmpbuf.toString();
      }
      return function_db_parameters;
   }

   /**
    * Return true if this field has been assigned a Function that will compute
    * the field's value.
    * @see #setFunction
    */
   public boolean isComputable()
   {
      return !Str.isEmpty(function_where_text);
   }

   /**
    * Write to the trace output the details of the function defined for
    * this ASPField.
    */
   public ASPField traceFunction()
   {
      try
      {
         if( !isTraceOn() ) return this;
         trace("Function for ASPField "+getName()+":");
         trace("   select: "+getSelectExpression());
         trace("   call:   "+function_call_text);
         trace("   params: "+function_parameters);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   //==========================================================================
   //  RadioButtons, CheckBox and SelectBox
   //==========================================================================

   /**
    * Define that this ASPField will correspond to a set of HTML Radio Buttons.
    * @see #enumerateValues
    */
   public ASPField setRadioButtons()
   {
      try
      {
         modifyingImmutableAttribute("RADIO_BUTTONS");
         radio_buttons = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    *
    * @deprecated use enumerateValues() and setRadioButtons() instead.
    * @see #setRadioButtons
    * @see #enumerateValues
    * @see #enumerateValues(String,String)
    */
   public ASPField setRadioButtons( String iid_package, String db_values )
   {
      return this;
   }

   /**
    * Define that this ASPField will correspond to a HTML Check Box.
    * <pre>
    * Example:
    *    ASPField f = blk.addField("COMMENTS_EXISTS");
    *    f.setCheckBox("F,T").
    * </pre>    
    * @param db_values Two valid database values for the check box field.  
    *                  The first value represents false (unchecked) while 
    *                  second value represents true (checked). 
    * @see #enumerateValues    
    */
   public ASPField setCheckBox( String db_values )
   {
      try
      {
         // Modified by Terry 20130619
         // Change check_box to mutable
         // Original:
         // modifyingImmutableAttribute("CHECK_BOX");
         modifyingMutableAttribute("CHECK_BOX");
         // Modified end
         check_box = true;
         iid_db_values = listToArray(db_values);
         if( iid_db_values.length!=2 )
            throw new FndException("FNDFLDCBXVAL: CheckBox must contain 2 values: &1", db_values);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   // Added by Terry 20130619
   // Change check_box to mutable
   public ASPField unsetCheckBox()
   {
      try
      {
         modifyingMutableAttribute("CHECK_BOX");
         check_box = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   // Added end

   /**
    * Define that this ASPField will correspond to a HTML Check Box.
    * @see #enumerateValues
    */
   public ASPField setCheckBox()
   {
      try
      {
         modifyingImmutableAttribute("CHECK_BOX");
         check_box = true;
         if( iid_db_values!=null && iid_db_values.length!=2 )
            throw new FndException("FNDFLDCBXVAL2: CheckBox must contain 2 values.");
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Return the DB values 
    */
   public String[] getIidDbValues()
   {
      return iid_db_values;
   }

   /**
    * Return the client values 
    */
   public String[] getIidClientValues()
   {
      return iid_client_values;
   }

   String getIidPackage_() // obsolete
   {
      return iid_package;
   }

   String getEnumerateMethod()
   {
      return enumerate_method;
   }

   public boolean isRadioButtons()
   {
      return radio_buttons;
   }

   public boolean isCheckBox()
   {
      return check_box;
   }

   public String getCheckedValue()
   {
      return iid_db_values[1];
   }

   public String getUncheckedValue()
   {
      return iid_db_values[0];
   }

   /**
    * Return true if this ASPField is a Select Box.
    */
   public boolean isSelectBox()
   {
      return select_box;
   }

   /**
    * Set this ASPField to be a Select Box.
    */
   public ASPField setSelectBox()
   {
      try
      {
         modifyingMutableAttribute("SELECT_BOX");
         select_box = true;
         if( !isUndefined() )
            dirty_validate = dirty_lov = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Remove the SelectBox flag on this ASPField
    */
   public ASPField unsetSelectBox()
   {
      try
      {
         modifyingMutableAttribute("SELECT_BOX");
         select_box = false;
         if( !isUndefined() )
            dirty_validate = dirty_lov = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Defines this ASPField to have a multi choice LOV instead of a select box
    * in find layout mode. No need to design the LOV page. The LOV page is
    * generated dynamically at run time. This ASPField should be a select box to
    * apply this functionality. This is an Immutable attribute and can not call
    * this function after the page is defined.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("DELIVERY_TYPE").
    *           setFindModeIidToLov();
    * </pre>
    *
    * @see ASPBlockLayout#FIND_LAYOUT
    * @see #unsetFindModeIidToLov
    * @see #isFindModeIidToLov
    * @see #setSelectBox
    */
   public ASPField setFindModeIidToLov()
   {
      try
      {
         modifyingImmutableAttribute("IID_TO_LOV");
         enable_iid_to_lov = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Turn off the multi choice LOV in find layout mode for this ASPField.
    * Then this ASPField is a select box in all layout modes. This flag is a
    * Immutable attribute. So that this can not call after the page is defined.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("DELIVERY_TYPE").
    *           unsetFindModeIidToLov();
    * </pre>
    *
    * @see ASPBlockLayout#FIND_LAYOUT
    * @see #setFindModeIidToLov
    * @see #isFindModeIidToLov
    * @see #setSelectBox
    */
   public ASPField unsetFindModeIidToLov()
   {
      try
      {
         modifyingImmutableAttribute("IID_TO_LOV");
         enable_iid_to_lov = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   public boolean isFindModeIidToLov()
   {
      return enable_iid_to_lov;
   }

   /**
    * Turn off the calendar icon for this ASPField date type.
    * This flag is a Immutable attribute. Therefore it can not be called after the page has been defined.
    * <pre>
    *    Example:
    *       ASPBlock blk = getASPManager().newASPBlock("MAIN");
    *       blk.addField("DELIVERY_DATE","Datetime").
    *           disableCalendarTag();
    * </pre>
    *
    */
   public ASPField disableCalendarTag()
   {
      try
      {
         modifyingImmutableAttribute("CALENDAR");
         disable_calendar_tag = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   

   //==========================================================================
   //  IID Values
   //==========================================================================

   /**
    * Fetch enumeration values from the given view. 
    * @param view FndEnumerationView object from witch IID values to be taken.
    * @return this object
    */
   public ASPField enumerateValues( FndEnumerationView view )
   {
      int count = view.values.getLength();
      this.iid_client_values = new String[count];
      this.iid_db_values     = new String[count];
      for(int a=0; a<count; a++)
      {
         this.iid_client_values[a] = view.values.get(a).id.getValue();
         this.iid_db_values[a]     = view.values.get(a).description.getValue();
      }
      
      return this;
   }   
   
   /**
    * Fetch from the database and attach to this ASPField a list of IID values,
    * using the specified PL/SQL package, which must be a standard IID package.
    */
   public ASPField enumerateValues( String iid_packege )
   {
      return enumerateValues(iid_packege+".Enumerate",iid_packege+".Encode");
   }

   /**
    * Fetch from the database and attach to this ASPField a list of IID values.
    * Thr first argument is a name of a PL/SQL procedure that enumerates client
    * (localized) values, for example: "Demo_Delivery_Type_API.Enumerate".
    * The second argument is a name of the PL/SQL procedure that encodes a
    * client value to the corresponding database value for example:
    * "Demo_Delivery_Type_API.Encode".
    * <p>
    * Standard queries on this field will use the corresponding DB-column
    * (the column created by appending the suffix "_DB" to the column name
    * defined for this ASPField).
    * @see #setDbName
    * @see #unsetSearchOnDbColumn
    */
   public ASPField enumerateValues( String enumerate_method, String encode_method )
   {
      try
      {
         // Modified by Terry 20130619
         // Modified iid_package, iid_db_values, iid_client_values, enumerate_method to mutable
         // Original:
         // modifyingImmutableAttribute("IID_VALUES");
         modifyingMutableAttribute("IID_VALUES");
         // Modified end
         String sep = ASPHTMLFormatter.field_separator;
         ASPManager mgr = getASPManager();

         this.enumerate_method = enumerate_method;

         //
         //  Execute Enumerate method into iid_client_values[]
         //
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPCommand cmd = trans.addCustomCommand("ENUM",enumerate_method);
         cmd.addParameter("LIST",null,null,null);
//         trans.addConfigRequestHeader();
//         trans = mgr.perform(trans);
         // Added by Terry 20130619
         // Modified iid_package, iid_db_values, iid_client_values, enumerate_method to mutable
         // Original:
         // trans = mgr.performConfig(trans);
         if( isUndefined() )
            trans = mgr.performConfig(trans);
         else
            trans = mgr.perform(trans);
         // Added end
         String list = trans.getValue("ENUM/DATA/LIST");
         if (!mgr.isEmpty(list))
         {
            iid_client_values = listToArray(list,sep);
            if(DEBUG) debugStringArray(iid_client_values,name+"'s iid_client_values");
            //
            //  Execute Encode method once per client value
            //
            trans = mgr.newASPTransactionBuffer();
            ASPCommand main = trans.addCustomCommand("MAIN","PerRow");
            main.getBuffer().removeItem("DATA");
            cmd = mgr.newASPCommand();
            cmd.defineCustomFunction(encode_method);
            cmd.setOption("FORCE","Y");
            cmd.addParameter(null,"R","OUT","DB");
            cmd.addParameter(null,"R","IN","CL");
            Buffer mainbuf = main.getBuffer();
            Buffer def = mainbuf.newInstance();
            def.addItem(new Item("Encode",null,null,cmd.getBuffer()));
            mainbuf.addItem("DEFINE",def);
            for( int i=0; i<iid_client_values.length; i++ )
            {
               Buffer row = mainbuf.newInstance();
               row.addItem("CL",iid_client_values[i]);
               row.addItem(new Item("DB",null));
               mainbuf.addItem(new Item("DATA",null,"Encode",row));
            }
            // Added by Terry 20130619
            // Modified iid_package, iid_db_values, iid_client_values, enumerate_method to mutable
            // Original:
            // trans = mgr.performConfig(trans);
            if( isUndefined() )
               trans = mgr.performConfig(trans);
            else
               trans = mgr.perform(trans);
            // Added end
            
            //
            //  Copy result into iid_db_values[]
            //
            Buffer data = trans.getBuffer().getBuffer("MAIN");
            iid_db_values = new String[iid_client_values.length];
            for( int i=0; i<iid_client_values.length; i++ )
               iid_db_values[i] = data.getBuffer(i).getString(1);
            if(DEBUG) debugStringArray(iid_db_values,    name+"'s iid_db_values");

            if( check_box && iid_db_values.length!=2 )
               throw new FndException("FNDFLDCBXVAL2: CheckBox must contain 2 values.");
            search_on_db_column = true;
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Change the order of IID values attached to this ASPField.
    * @see #enumerateValues
    */
   public void reverseCheckBoxValues()
   {
      try
      {
         if( !check_box || iid_db_values.length!=2 )
            throw new FndException("FNDFLDCBXVAL2: CheckBox must contain 2 values.");

         reverse2Values(iid_db_values);
         reverse2Values(iid_client_values);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Encode the specified client value into the corresponding database value.
    */
   public String encode( String client_value )
   {
      try
      {
         int i = findValue(iid_client_values,client_value);
         if( i<0 )
            throw new FndException("FNDFLDIIDENC: Cannot encode Client value '&1' for field &2.",
                                    client_value,name);
         return iid_db_values[i];
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Decode the specified database value into the corresponding client value.
    */
   public String decode( String db_value )
   {
      try
      {
         int i = findValue(iid_db_values,db_value);
         if( i<0 )
            throw new FndException("FNDFLDIIDDEC: Cannot decode DB value '&1' for field &2.",
                                    db_value,name);
         return iid_client_values[i];
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return the IID client value at the specified position (0,1,...)
    */
   public String getClientValueAt( int position )
   {
      try
      {
         if( iid_client_values==null || position<0 || position>=iid_client_values.length )
            throw new FndException("FNDFLDIIDGETC: There is no Client value at position &1 in field &2.",
                                   ""+position,name);
         return iid_client_values[position];
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return the IID database value at the specified position (0,1,...)
    */
   public String getDbValueAt( int position )
   {
      try
      {
         if( iid_db_values==null || position<0 || position>=iid_db_values.length )
            throw new FndException("FNDFLDIIDGETD: There is no DB value at position &1 in field &2.",
                                   ""+position,name);
         return iid_db_values[position];
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return the number of IID values attached to this ASPField.
    */
   public int countValues()
   {
      return iid_client_values==null ? 0 : iid_client_values.length;
   }


   /**
    * Return an ASPBuffer with all IID values attached to this ASPField.
    * The buffer has the format accepted by ASPHTMLFormatter.populateListBox().
    * @see #removeClientValue
    * @see ifs.fnd.asp.ASPHTMLFormatter.populateListBox
    */
   public ASPBuffer getClientValues()
   {
      try
      {
         if( iid_client_values==null )
            throw new FndException("FNDFLDIIDGETB: There are no defined IID values for field &1", name);

         ASPManager mgr = getASPManager();

         ASPBuffer aspbuf = mgr.newASPBuffer();
         Buffer buf = aspbuf.getBuffer();
         for( int i=0; i<iid_client_values.length; i++ )
         {
            Buffer row = buf.newInstance();
            row.addItem(String.valueOf(i),iid_client_values[i]);
            buf.addItem("DATA",row);
         }
         return aspbuf;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   public ASPBuffer getIidClientValuesBuffer()
      {
         try
         {
            if( iid_client_values==null )
               throw new FndException("FNDFLDIIDGETB: There are no defined IID values for field &1", name);

            ASPManager mgr = getASPManager();

            ASPBuffer aspbuf = mgr.newASPBuffer();
            Buffer buf = aspbuf.getBuffer();
            for( int i=0; i<iid_client_values.length; i++ )
            {
               Buffer row = buf.newInstance();
               row.addItem("VALUE",iid_client_values[i]);
               row.addItem("NAME",iid_client_values[i]);
               buf.addItem("DATA",row);
            }
            return aspbuf;
         }
         catch( Throwable any )
         {
            error(any);
            return null;
         }
      }

   /**
    * Remove the specified item from an ASPBuffer containing IID values.
    * @see #getClientValues
    */
   public void removeClientValue( ASPBuffer client_values, String db_value )
   {
      try
      {
         int i = findValue(iid_db_values,db_value);
         if( i<0 ) return;

         client_values.removeItemAt(i);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Define that the standard queries should NOT use the DB-column corresponding
    * to this ASPField.
    * @see #enumerateValues
    * @see ifs.fnd.asp.ASPTransactionBuffer.addQuery
    */
   public ASPField unsetSearchOnDbColumn()
   {
      try
      {
         modifyingImmutableAttribute("SEARCH_ON_DB_COLUMN");
         search_on_db_column = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }


   boolean searchOnDbColumn()
   {
      return search_on_db_column;
   }

   //==========================================================================
   //  Global Connection
   //==========================================================================

   /**
    * Connect this ASPField to the specified global variable. The value of the
    * global variable will be used as the value of this field.
    * The specified url will be used to create the global variable,
    * if it is undefined.
    */
   public ASPField setGlobalConnection( String global_name, String global_url )
   {
      try
      {
         modifyingImmutableAttribute("GLOBAL_CONNECTION");
         this.global_name = global_name;
         this.global_url  = global_url;
         fetchGlobalValue();
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Connect this ASPField to the specified global variable. The value of the
    * global variable will be used as the value of this field.
    */
   public ASPField setGlobalConnection( String global_name )
   {
      return setGlobalConnection(global_name,null);
   }

   /**
    * Return true if this ASPField is connected to a global variable
    */
   public boolean hasGlobalConnection()
   {
      return global_name != null;
   }

   /**
    * Return the value of the global variable connected to this ASPField,
    * or null if this field is not connected to any global varible.
    */
   public String getGlobalValue()
   {
      return global_value;
   }

   /**
    * Fetch the value of the global variable connected to this ASPField
    * and store it in a transient variable. The value may be fetched
    * by calling getGlobalValue().
    */
   private void fetchGlobalValue()
   {
      if( hasGlobalConnection() )
      {
         ASPContext ctx = getASPPage().getASPContext();
         global_value = global_url==null ?
                        ctx.getGlobal(global_name) :
                        ctx.getGlobal(global_name,global_url);
      }
   }

   public ASPField setDebugGlobalConnection( String global_name, String value )
   {
      try
      {
         modifyingImmutableAttribute("GLOBAL_CONNECTION");
         this.global_name = global_name;
         this.global_url  = null;
         this.global_value = value;
         this.global_debug_value = value;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   //==========================================================================
   //  Value in Request
   //==========================================================================

   String readValue() throws FndException
   {
      return readValueAt(-1);
   }

   String readValueAt( int rownr ) throws FndException
   {
      //JAPA
      ASPPage page = getASPPage();

      String value = Util.trimLine( rownr<0 ?
                                    page.readValue(name) :
                                    page.readValueAt(name,rownr) );

      if( isCheckBox() )
         value = Str.isEmpty(value) || iid_db_values[0].equals(value) ?
                 iid_db_values[0] :
                 iid_db_values[1];

      return value;
   }

   //==========================================================================
   //  Conversion Client-String <-> Server-Item
   //==========================================================================

   /**
    * Append to the dest-buffer a new Item corresponding to this ASPField.
    * Retrieve the value from the source-buffer without any conversion.
    * Mark the new Item as changed (status="*"),
    * if the new value differs from the old one.
    * Return true if the value has been changed.
    */
   boolean copyToBuffer( Buffer dest,
                         Buffer source,
                         Buffer old_dest ) throws Exception
   {
      String dbname = getDbName();

      int type_id = formatter.getTypeId();
      char type_marker = DataFormatter.getBaseTypeMarker(type_id);

      String value;

      if( hasGlobalConnection() )
         value = getGlobalValue();
      else
      {
         value = source==null ? null : source.getString(dbname,null);
         if( upper_case && value!=null )
            value = value.toUpperCase();
      }

      Item item = ASPBuffer.copyToServerItem( dbname,
                                              String.valueOf(type_marker),
                                              value,
                                              old_dest );
      return appendItemToBuffer(item,dest,old_dest);
   }


   /**
    * Append, to the specified buffer, a new Item based on this ASPField.
    * Fetch the value from the ASP Request object.
    * Mark the new item as changed (status="*"),
    * if the old_dest-buffer has been specified and
    * if the current value differs from the old one.
    * Convert the value to the server format using this
    * field's type and format mask.
    * Return true if the value has been changed.
    */
   boolean readToBuffer( Buffer dest,
                         Buffer old_dest ) throws Exception
   {
      String value = readValue();
      return convertToBuffer(dest,value,old_dest);
   }


   /**
    * Append, to the specified buffer, a new Item based on this ASPField.
    * Convert the specified value to the server format using this
    * field's type and format mask.
    * Mark the new item as changed (status="*"),
    * if the old_dest-buffer has been specified and
    * if the current value differs from the old one.
    * Return true if the value has been changed.
    */
   boolean convertToBuffer( Buffer dest,
                            String value,
                            Buffer old_dest ) throws FndException
   {
      Item item;
      item = convertToServerItem( value, old_dest ); //radio_buttons ? null : old_dest );
      return appendItemToBuffer(item,dest,old_dest);
   }


   protected FndException parseException( String value, Throwable any ) throws FndException
   {
      try
      {
         switch( formatter.getTypeId() )
         {
            case DataFormatter.STRING:
               return (new FndException(
                  "FNDFMTFLDSTR: Invalid string '&1' in field '&2'.",
                  value,getLabel())).addCaughtException(any);

            case DataFormatter.INTEGER:
               return (new FndException(
                  "FNDFMTFLDINT: Invalid integer value '&1' in field '&2'.",
                  value,getLabel())).addCaughtException(any);

            case DataFormatter.NUMBER:
            case DataFormatter.MONEY:
               return (new FndException(
                  "FNDFMTFLDNUM: Invalid number '&1' in field '&2'.",
                  value,getLabel())).addCaughtException(any);

            case DataFormatter.BOOLEAN:
               return (new FndException(
                  "FNDFMTFLDBOL: Invalid boolean '&1' in field '&2'.",
                  value,getLabel())).addCaughtException(any);

            case DataFormatter.DATETIME:
            case DataFormatter.DATE:
            case DataFormatter.TIME:
               return (new FndException(
                  "FNDFMTFLDDAT: Invalid date '&1' in field '&2'. It should be for example '&3'.",
                  value,getLabel(),formatter.format(new Date()))).addCaughtException(any);

            default:
               throw (new FndException("FNDFMTBADID: Invalid value '&1' for data type ID.",
                                      ""+type_id)).addCaughtException(any);
         }
      }
      catch( Throwable panic )
      {
         throw (new FndException(panic)).addCaughtException(any);
      }
   }

   /**
    * Modify an existing Item in the specified buffer.
    * Convert the specified value to the server format using this
    * ASPField's type and format mask.
    */
   void convertInBuffer( Buffer dest,
                         String value ) throws Exception
   {
      Item item = convertToServerItem(value, null);
      Item olditem = dest.getItem(getDbName(), null);
      if( olditem==null )
         dest.addItem(item);
      else
      {
         olditem.setValue (item.getValue() );
         olditem.setType  (item.getType()  );
         olditem.setStatus(item.getStatus());
      }
      if( isComputable() ) item.setStatus("-");
   }


   String prepareClientValue( String value )
   {
      if( hasGlobalConnection() && value==null)
         return getGlobalValue();
      else if( upper_case && value!=null )
         return value.toUpperCase();
      return value;
   }


   Item convertToServerItem( String value,
                             Buffer old_dest ) throws FndException
   {
      value = prepareClientValue(value);
      try
      {
         ServerFormatter srvfmt = getASPPage().getASPConfig().getServerFormatter();
         return ASPBuffer.convertToServerItem(getDbName(),value,srvfmt,formatter,old_dest);
      }
      catch( Throwable any )
      {
         throw parseException(value,any);
      }
   }


   private boolean appendItemToBuffer( Item item, Buffer dest, Buffer old_dest )
   {
      dest.addItem(item);
      if( isComputable() )
      {
         item.setStatus("-");
         //if( old_dest==null ) item.setValue(null);
      }
      return "*".equals(item.getStatus());
   }


   /**
    * Convert the specified String from the server format to the client
    * format using this ASPField's datatype and format mask.
    * @server_value server format value
    * @see ifs.fnd.asp.ASPRowSet.getClientValue
    * @see ifs.fnd.asp.ASPRowSet.getClientValueAt
    * @see ifs.fnd.asp.ASPRowSet.getValue
    * @see ifs.fnd.asp.ASPRowSet.getValueAt
    */

   public String convertToClientString( String server_value ) throws FndException
   {
      if(DEBUG) debug("ASPField.convertToClientString("+server_value+")");

      if( server_value==null ) return null;
      
      // Modified by Terry 20120821
      // Catch Exception in convert
      // Original:
      // if( getASPManager().isEmpty(server_value.trim())) return "";
      // int server_type_id = formatter.getBaseTypeId(formatter.getTypeId());
      // ServerFormatter srvfmt = getASPPage().getASPConfig().getServerFormatter();
      // Object obj = srvfmt.parse(server_value, server_type_id);
      // 
      // String client_value = formatter.format(obj);
      
      try
      {
         if( Str.isEmpty(server_value.trim()))
            return "";
      }
      catch(Exception e)
      {
         return "";
      }
      
      String client_value = "";
      
      try
      {
         int server_type_id = formatter.getBaseTypeId(formatter.getTypeId());
         ServerFormatter srvfmt = getASPPage().getASPConfig().getServerFormatter();
         Object obj = srvfmt.parse(server_value, server_type_id);
         client_value = formatter.format(obj);
      }
      catch (Exception e)
      {
         client_value = server_value;
      }
      // Modified end
      
      if(DEBUG) debug("  convertToClientString(): client_value="+client_value);
      return client_value;
   }

   /**
    * Converts the specified date value in server format to a java Date object.
    * @params server_value Date value in server format.
    */
   public Date convertToJavaDate(String server_value) throws FndException
   {
       return getASPManager().parseDate(getName(),convertToClientString(server_value));
   }

   /**
    * Convert a numeric value (or JScript variable) to the client format
    * using this ASPField's datatype and format mask.
    * Note! This function returns an empty String ("") if the specified
    * value is so called NaN (Not-a-Number) value.
    */
   public String formatNumber( double value )// throws FndException
   {
      try
      {
         if( formatter.getBaseTypeId(type_id) != formatter.NUMBER )
            throw new FndException("FNDFRMNOTNUM: The field &1 cannot be formatted as a Number");

         if( Double.isNaN(value) )
            return "";

         if( Double.isInfinite(value) )
            throw new FndException("FNDFRMINFNUM: Infinite Number in field &1", name);

         return formatter.format(new Double(value));
      }
      catch( Throwable e)
      {
         error(e);
         return null;
      }
   }


   /**
    * Convert given String into a numeric value (or JScript variable)
    * using this ASPField's datatype and format mask.
    * Return NaN (Not-a-Number) value, if the specified text is empty.
    */
   public double parseNumber( String text ) throws FndException
   {
      try
      {
         if( formatter.getBaseTypeId(type_id) != formatter.NUMBER )
            throw new FndException("FNDPRSNOTNUM: The field &1 cannot be parsed as a Number");

         if( Str.isEmpty(text) )
            return getASPManager().NOT_A_NUMBER;

         Number num = (Number)formatter.parse(text);

         return num.doubleValue();
      }
      catch( Throwable e)
      {
         error(e);
         return getASPManager().NOT_A_NUMBER;
      }
   }

   /**
    * Convert a Date value  to the client format
    * using this ASPField's datatype and format mask.
    */
   public String formatDate( Date value ) throws FndException
   {
       if(value==null) return null;
      try
      {
         if( formatter.getBaseTypeId(type_id) != formatter.DATETIME
             && formatter.getBaseTypeId(type_id) != formatter.DATE
             && formatter.getBaseTypeId(type_id) != formatter.TIME)
            throw new FndException("FNDFRMNOTDATE: The field &1 cannot be formatted as a Date");

         SimpleDateFormat fmt = new SimpleDateFormat(((DateFormatter) getDataFormatter()).getFormatMask());
         return fmt.format(value);
      }
      catch( Throwable e)
      {
         error(e);
         return null;
      }
   }


   /**
    * Convert given String into a Date value
    * using this ASPField's datatype and format mask.
    */

   public Date parseDate( String text ) throws FndException
   {
       if(text==null) return null;
      try
      {
         if( formatter.getBaseTypeId(type_id) != formatter.DATETIME
             && formatter.getBaseTypeId(type_id) != formatter.DATE
             && formatter.getBaseTypeId(type_id) != formatter.TIME)
            throw new FndException("FNDPRSNOTDATE: The field &1 cannot be parsed as a Date");

         SimpleDateFormat fmt = new SimpleDateFormat(((DateFormatter) getDataFormatter()).getFormatMask());
         return fmt.parse(text);

      }
      catch( Throwable e)
      {
         error(e);
         return null;
      }
   }

   //==========================================================================
   //  JavaScript code generation
   //==========================================================================

   void appendDirtyClientScript( AutoString code ) throws FndException
   {
      if( isImageField() && !hasTemplate()) return;
      createInOutParamArrays();
      if( hidden ) return;
      if( dirty_lov && hasLOV() ) appendLOVFunction(code);
      if( dirty_validate ) appendValidationFunction(code);
      if( dirty_check ) appendCheckFunction(code);
   }

   void appendPageClientScript( AutoString code ) throws FndException
   {
      if( isImageField() && !hasTemplate()) return;
      createInOutParamArrays();
      if( hasLOV() ) appendLOVFunction(code);
      if( enable_iid_to_lov && isSelectBox()) appendIidLOVFunction(code);
      appendValidationFunction(code);
      appendCheckFunction(code);
   }

   private void appendLOVFunction( AutoString code ) throws FndException
   {
      ASPConfig cfg = getASPPage().getASPConfig();
      int dx = lov_width==0  ? cfg.getLOVWindowWidth()  : lov_width;
      int dy = lov_height==0 ? cfg.getLOVWindowHeight() : lov_height;

      code.append("\n function preLov");
      code.append(getJavaScriptName());
      code.append("(i,params)\n{\n");
      code.append("\n\t if(params) \n\t\t PARAM = params;\n\t else \n\t\t PARAM = '';\n");
      // Modified by Terry 20120821
      // Set multi-select in LOV
      // Original: code.append("\t var enable_multichoice =("+this.lov_multichoice," && ",getBlock().getName()+"_IN_FIND_MODE" ,");\n");
      if (this.lov_multichoice_ne)
         code.append("\t var enable_multichoice = true;\n");
      else
         code.append("\t var enable_multichoice =("+this.lov_multichoice," && ",getBlock().getName()+"_IN_FIND_MODE" ,");\n");
      // Modified end
      code.append("\t MULTICH=\"\"+enable_multichoice;\n");
      if(formatter.getBaseTypeId(type_id)==formatter.NUMBER)
         code.append("\t MASK =\""+getASPManager().URLEncode(getMask())+"\";\n");
      else
         code.append("\t MASK =\"\";");
      code.append("\t KEY_VALUE = (getValue_('",getName(),"',i).indexOf('%') !=-1)? getValue_('",getName(),"',i):'';\n");
      if(dynamic_lov)
         code.append("\t FIELDKEYVALUE ='&__KEY_VALUE=' + ","URLClientEncode(getValue_('",getName(),"',i))\n");
      code.append("\t lov");
      code.append(getJavaScriptName());
      code.append("(i,params);\n");
      code.append("}\n\n");

      code.append("\nfunction lov");
      code.append(getJavaScriptName()); //JAPA
      code.append("(i,params)\n{\n");
      code.append("\tif(params) param = params;\n\telse param = '';\n");
      // Modified by Terry 20120821
      // Set multi-select in LOV
      // Original: code.append("\tvar enable_multichoice =("+this.lov_multichoice," && ",getBlock().getName()+"_IN_FIND_MODE" ,");\n");
      if (this.lov_multichoice_ne)
         code.append("\tvar enable_multichoice = true;\n");
      else
         code.append("\tvar enable_multichoice =("+this.lov_multichoice," && ",getBlock().getName()+"_IN_FIND_MODE" ,");\n");
      // Modified end
      code.append("\tvar key_value = (getValue_('",getName(),"',i).indexOf('%') !=-1)? getValue_('",getName(),"',i):'';\n");
      
      // Added by Terry 20140911
      // Conditional LOV
      if (hasConditionalLOV() && dynamic_lov)
      {
         // code.append("\tvar dynamic_view;\n");
         String if_clause = "\tif(";
         String lovurl_ = getScriptLOVURL() + " + '";
         String from_lov = "";
         String to_lov = "";
         boolean append_succ = false;
         int from_index = lovurl_.indexOf("__DYNAMIC_LOV_VIEW");
         if (from_index >= 0)
         {
            int to_index = lovurl_.indexOf("&", from_index);
            if (to_index > from_index)
            {
               from_lov = lovurl_.substring(0, from_index + "__DYNAMIC_LOV_VIEW".length() + 1);
               to_lov = lovurl_.substring(to_index);
            }
         }
         
         if (!Str.isEmpty(from_lov) && !Str.isEmpty(to_lov))
         {
            for( int i=0; i<conditional_lov.countItems(); i++ )
            {
               String conditional_view = conditional_lov.getItem(i).getName();
               Buffer conditional_nv   = conditional_lov.getItem(i).getBuffer();
               String conditional_field_name = conditional_nv.getString("FIELD_NAME");
               String conditional_field_value = conditional_nv.getString("FIELD_VALUES");
               String conditional_view_params = conditional_nv.getString("VIEW_PARAMS");
               if (Str.isEmpty(conditional_field_value))
                  conditional_field_value = "null";
               // Check view and field name empty
               if (!Str.isEmpty(conditional_view) && !Str.isEmpty(conditional_field_name))
               {
                  append_succ = true;
                  code.append(if_clause + "checkFieldValueEqu_(i,'" + conditional_field_name + "','" + conditional_field_value + "')" + ")\n");
                  // code.append("\t\tdynamic_view = '" + conditional_view + "';\n");
                  if ("\tif(".equals(if_clause))
                     if_clause = "\telse if(";
                  
                  code.append("\topenLOVWindow('");
                  code.append(name);
                  code.append("',i,\n");
                  
                  tmpbuf.clear();
                  lovurl_ = from_lov + conditional_view + to_lov;
                  tmpbuf.append(lovurl_);
                  
                  if(tmpbuf.toString().indexOf('?')<0 )
                     tmpbuf.append("?MULTICHOICE='+enable_multichoice+'");
                  else
                     tmpbuf.append("&MULTICHOICE='+enable_multichoice+'");
                  
                  if(formatter.getBaseTypeId(type_id)==formatter.NUMBER)
                     tmpbuf.append("&__MASK="+getASPManager().URLEncode(getMask())+"");
                  tmpbuf.append("'");
                  
                  // Adding View Params
                  Vector fields  = new Vector();
                  Vector aliases = new Vector();
                  parseParameterList(conditional_view_params, fields, aliases, true);
                  ASPField[] lov_in_fields_  = toASPFieldArray(fields);
                  String[] lov_in_aliases_ = toStringArray(aliases);
                  appendURL(code, tmpbuf.toString(), lov_in_fields_, lov_in_aliases_);

                  code.append("\t\t,");
                  code.appendInt(dx);
                  code.append(',');
                  code.appendInt(dy);

                  if(!(validate_function == null) && !(validate_function.equals("")))
                     code.append(",'",validate_function,"');\n");
                  else
                     code.append(",'validate",getJavaScriptName(),"');\n"); //JAPA
               }
            }
         }
         
         if (append_succ)
            code.append("\telse\n");
      }
      // Added end
      
      code.append("\topenLOVWindow('");
      code.append(name);
      code.append("',i,\n");

      tmpbuf.clear();

      String lovurl = getScriptLOVURL() + " + '";
      boolean isdynamic = lovurl.indexOf("__DYNAMIC_LOV_VIEW")!=-1;
      //lovurl += "+'";

      if(!isdynamic)
      {
         if(lovurl.indexOf('?')<0)
            lovurl += "?__VIEW=DUMMY&__INIT=1&__LOV=Y";
         else
            lovurl += "&__VIEW=DUMMY&__INIT=1&__LOV=Y";
      }
      // tmpbuf.append(lovurl+"'+param+'");
      //tmpbuf.append(lovurl);
      
      // Added by Terry 20140911
      // Conditional LOV
      /*
      if (hasConditionalLOV() && dynamic_lov)
      {
         int from_index = lovurl.indexOf("__DYNAMIC_LOV_VIEW");
         if (from_index >= 0)
         {
            int to_index = lovurl.indexOf("&", from_index);
            if (to_index > from_index)
            {
               String from_lov = lovurl.substring(0, from_index + "__DYNAMIC_LOV_VIEW".length() + 1);
               String to_lov = lovurl.substring(to_index);
               lovurl = from_lov + "' + dynamic_view + '" + to_lov;
            }
         }
      }*/
      // Added end
      tmpbuf.append(lovurl);

      if( lov_property!=null )
      {
         for( int i=0; i<lov_property.countItems(); i++ )
         {
            if( i==0 && lov_url.indexOf('?')<0 && lovurl.indexOf('?')<0)
               tmpbuf.append('?');
            else
               tmpbuf.append('&');
            Item item = lov_property.getItem(i);
            tmpbuf.append("__",getASPManager().URLEncode(item.getName()));
            tmpbuf.append('=');
            tmpbuf.append(getASPManager().URLEncode(item.getString()));
         }
      }

      if(tmpbuf.toString().indexOf('?')<0 )
         tmpbuf.append("?MULTICHOICE='+enable_multichoice+'");
      else
         tmpbuf.append("&MULTICHOICE='+enable_multichoice+'");
      
      if(formatter.getBaseTypeId(type_id)==formatter.NUMBER)
         tmpbuf.append("&__MASK="+getASPManager().URLEncode(getMask())+"");
      tmpbuf.append("'");
      appendURL(code,tmpbuf.toString(),lov_in_fields,lov_in_aliases);

      code.append("\t\t,");
      code.appendInt(dx);
      code.append(',');
      code.appendInt(dy);

      if(!(validate_function == null) && !(validate_function.equals("")))
         code.append(",'",validate_function,"');\n");

      else
         code.append(",'validate",getJavaScriptName(),"');\n"); //JAPA

      code.append("}\n");
   }

   private void appendIidLOVFunction( AutoString code ) throws FndException
   {
      ASPConfig cfg = getASPPage().getASPConfig();
      int dx = lov_width==0  ? cfg.getLOVWindowWidth()  : lov_width;
      int dy = lov_height==0 ? cfg.getLOVWindowHeight() : lov_height;

      code.append("\nfunction lov");
      code.append(getJavaScriptName());
      code.append("(i)\n{\n");
      code.append("\topenLOVWindow('");
      code.append(name);
      code.append("',i,\n");

      String url = getASPPage().getASPConfig().getDynamicIidLOVURL(true);
      url = url + "+ '?IID_PACKAGE=" + getASPManager().URLEncode(this.getEnumerateMethod()) + "&LABEL=" + getASPManager().URLEncode(this.getLabel()) + "'";

      appendURL(code,url,lov_in_fields,lov_in_aliases);

      code.append("\t\t,");
      code.appendInt(dx);
      code.append(',');
      code.appendInt(dy);

      if(!(validate_function == null) && !(validate_function.equals("")))
         code.append(",'",validate_function,"');\n");

      else
         code.append(",'validate",getJavaScriptName(),"');\n");

      code.append("}\n");
   }

   // Added by Terry 20140822
   // Conditional Mandatory property of field
   // Clear addtional readonly text(*) field for conditional mandatory field
   private void appendConditionalClearFunc( AutoString code ) throws FndException
   {
      Buffer validate_buff = getValidateFieldOfcm();
      if (validate_buff != null && validate_buff.countItems() > 0)
      {
         for (int i = 0; i < validate_buff.countItems(); i++)
         {
            Item validate_field = validate_buff.getItem(i);
            String fields_name = validate_field.getString();
            StringTokenizer st = new StringTokenizer(fields_name, ";, \t\n\r");
            while (st.hasMoreTokens())
            {
               String field_name = st.nextToken();
               if (!Str.isEmpty(field_name))
               {
                  code.append("\t\tgetField_('","__CONDITIONAL_MANDATORY_" + field_name,"',i).value = '';\n");
               }
            }
         }
      }
   }
   
   // When validate field value is changed, set addtional readonly text(*) field for conditional mandatory field
   private void appendConditionalCheckFunc( AutoString code ) throws FndException
   {
      Buffer validate_buff = getValidateFieldOfcm();
      if (validate_buff != null && validate_buff.countItems() > 0)
      {
         for (int i = 0; i < validate_buff.countItems(); i++)
         {
            Item validate_field = validate_buff.getItem(i);
            String fields_name = validate_field.getString();
            StringTokenizer st = new StringTokenizer(fields_name, ";, \t\n\r");
            while (st.hasMoreTokens())
            {
               String field_name = st.nextToken();
               if (!Str.isEmpty(field_name))
               {
                  // Get conditional mandatory field
                  ASPField conditional_mandatory_field = getASPPage().getASPField(field_name);
                  // Get condition
                  Buffer condition_buff = conditional_mandatory_field.getConditionalMandatory();
                  // Get if clause of conditional mandatory
                  String condition_man_js = getConditionalMandatoryIf(condition_buff);
                  if (!Str.isEmpty(condition_man_js))
                  {
                     code.append(condition_man_js);
                     code.append("\t\tgetField_('","__CONDITIONAL_MANDATORY_" + field_name,"',i).value = '*';\n");
                     code.append("\telse\n");
                     code.append("\t\tgetField_('","__CONDITIONAL_MANDATORY_" + field_name,"',i).value = '';\n");
                  }
               }
            }
         }
      }
   }
   
   // Get conditional mandatory if clause of field
   private String getConditionalMandatoryIf( Buffer condition_buff )
   {
      String condition_man_js = "";
      String and_js = " && ";
      if (condition_buff != null && condition_buff.countItems() > 0)
      {
         condition_man_js = "\tif (";
         for (int j = 0; j < condition_buff.countItems(); j++)
         {
            Item validate_fld = condition_buff.getItem(j);
            String validate_fld_name = validate_fld.getName();
            String validate_fld_value = validate_fld.getString();
            if (!Str.isEmpty(validate_fld_name) && !Str.isEmpty(validate_fld_value))
               condition_man_js = condition_man_js + "checkFieldValueEqu_(i,'" + validate_fld_name + "','" + validate_fld_value + "')" + and_js;
         }
         condition_man_js = condition_man_js.substring(0, condition_man_js.length() - and_js.length()) + ")\n";         
      }
      return condition_man_js;
   }
   // Added end

   private void appendValidationFunction( AutoString code ) throws FndException
   {

      if(!(validate_function == null) && !(validate_function.equals("")))
      {
         code.append("\nfunction ",validate_function);
         code.append("(i)\n{");
         code.append("\n\tvalidate",getJavaScriptName(),"(i);");
         code.append("\n}");
      }

      code.append("\nfunction validate");
      code.append(getJavaScriptName()); //JAPA
      code.append("(i)\n{");
      code.append("\n\tif( getRowStatus_('",getBlock().getName(),"',i)=='QueryMode__' ) return;");
      code.append("\n\tsetDirty();\n");
      code.append("\tif( !check");
      code.append(getJavaScriptName()); //JAPA
      code.append("(i) ) return;\n");

      String fld_name = "";
      for( int i=0; i<validation_in_fields.length; i++ )
      {
         if (!validation_in_fields[i].isAccurateFld())
            code.append("\tif( getValue_('",validation_in_fields[i].getName(),"',i).indexOf('%') != -1) return;\n");
         else
            code.append("\tif( getValue_('",validation_in_fields[i].getAccurateDisplayFldName(),"',i).indexOf('%') != -1) return;\n");
      }

      if( hasValidation() )
      {
         if (isAccurateFld())
            code.append("\n\tgetField_('"+name+"',i).value = getField_('"+getAccurateDisplayFldName()+"',i).value;\n\n");
            
         if( !isMandatory() && !isCheckBox() && validation_clear_fields_on_empty)
         {
            //
            //  Clear OUT parameters if value is empty
            //
            code.append("\tif( getValue_('",name,"',i)=='' )\n");
            code.append("\t{\n");

            for( int i=0; i<validation_out_fields.length; i++ )
               if (!validation_out_fields[i].isCheckBox())
               {
                  code.append("\t\tgetField_('",validation_out_fields[i].getName(),"',i).value = '';\n");

                  if (validation_out_fields[i].isAccurateFld()) //clear HTML accuracy field
                     code.append("\t\tgetField_('",validation_out_fields[i].getAccurateDisplayFldName(),"',i).value = '';\n");
                  
                  // Added by Terry 20140823
                  // Conditional Mandatory property of field
                  // When validate field has validate conditional mandatory, add validate js
                  if (validation_out_fields[i].hasValidateFieldOfcm())
                     code.append("\t\tvalidate" + validation_out_fields[i].getJavaScriptName() + "(i);\n");
                  // Added end
               }

            // Added by Terry 20140822
            // Conditional Mandatory property of field
            // Clear addtional readonly text(*) field for conditional mandatory field
            if (hasValidateFieldOfcm())
               appendConditionalClearFunc(code);
            // Added end
            
            code.append("\t\treturn;\n");
            code.append("\t}\n");
         }

         if( Str.isEmpty(validation_method) ) // Custom Validation
         {
            //
            //  Custom Validation
            //
            if(!getASPManager().isMobileVersion())
               code.append("\twindow.status='Please wait for validation';\n");
            code.append("\t r = __connect(\n");
            String url = validation_url + "+'" +
                         (validation_url.indexOf('?')>0 ? '&' : '?') +
                         "VALIDATE="+name + "'";
            appendURL(code,url,validation_in_fields,validation_in_aliases);
            code.append("\t\t, '" + validation_http_method + "');\n");            
            if(!getASPManager().isMobileVersion())
               code.append("\twindow.status='';\n");
         }
         else
         {
            //
            //  Standard Validation
            //
            if(!getASPManager().isMobileVersion())
               code.append("\twindow.status='",getASPManager().translateJavaScript("FNDASPFIELDVALIDATIONWAIT: Please wait for validation"),"';\n");
            code.append("\tr = __lookup(\n");
            code.append("\t\t",validation_url,",\n");
            code.append("\t\t'DUAL',\n");

            // Bug 32278, start

            // Encode entire function and the in params all together instead of encoding each
            // param values. New Decode function expects you to encode a query string value
            // together.
            code.append("\t\t URLClientEncode('",validation_method,"('\n");
            for( int i=0; i<validation_in_fields.length; i++ )
            {
               ASPField param = validation_in_fields[i];
               DataFormatter fmt = param.getDataFormatter();
               boolean str = fmt.getBaseTypeMarker(fmt.getTypeId()) != 'N';
               code.append("\t\t+");
               if( i>0 ) code.append(" \",\" +");
               if( str ) code.append(" \"'\" +");
               if(str)
                  code.append("getValue_('",param.getName(),"',i)");
               else
                  code.append("formatNumber_(getValue_('",param.getName(),"',i))");
               if( str ) code.append(" + \"'\"");
               code.append("\n");
            }
            code.append("\t\t+ ') ",validation_out_parameters,"'), '', '');\n");
            // Bug 32278,end
            if(!getASPManager().isMobileVersion())
               code.append("\twindow.status='';\n");
         }
         code.append("\n");
         code.append("\tif( checkStatus_(r,'",name,"',i,'");
         code.append(getJSLabel(),"') )\n\t{\n");

         for( int i=0; i<validation_out_fields.length; i++ )
         {
            ASPField outparam = validation_out_fields[i];
            if( outparam.isSelectBox() )
            {
               code.append("\t\tassignSelectBoxValue_('",outparam.getName(),"',i,");
               code.appendInt(i);
               code.append(");\n");
            }
            else if (outparam.isCheckBox())
            {
               code.append("\t\tif (i>0) //Editable tables\n\t\t{\n");
               code.append("\t\t\tassignCheckBoxValue_('_",outparam.getName(),"',i-1,");
               code.appendInt(i);
               code.append(",'", outparam.getCheckedValue() ,"');\n");
               code.append("\t\t\tsetCheckBox_('", outparam.getName(),"',getField_('_", outparam.getName() ,"',i-1).checked,i,'");
               code.append(outparam.getCheckedValue(),"');\n");
               code.append("\t\t}\n\t\telse\n");
               code.append("\t\t\tassignCheckBoxValue_('",outparam.getName(),"',i,");
               code.appendInt(i);
               code.append(",'", outparam.getCheckedValue() ,"');\n");
            }
            else
            {
               if (!outparam.isAccurateFld()) 
               {
                  code.append("\t\tassignValue_('",outparam.getName(),"',i,");
                  code.appendInt(i);
                  code.append(");\n");
               }
               else //accurate values have char 29 separating the 2 values
               {
                  code.append("\t\tassignAccurateValue_('",outparam.getName(),"','",ASPField.FND_ACCURATE,"',i,");
                  code.appendInt(i);
                  code.append(");\n");
               }
            }
            
            // Added by Terry 20140823
            // Conditional Mandatory property of field
            // When validate field has validate conditional mandatory, add validate js
            if (outparam.hasValidateFieldOfcm())
               code.append("\t\tvalidate" + outparam.getJavaScriptName() + "(i);\n");
            // Added end
         }

         code.append("\t}\n");
         
         // Added by Terry 20140822
         // Conditional Mandatory property of field
         // When validate field value is changed, set addtional readonly text(*) field for conditional mandatory field
         if (hasValidateFieldOfcm())
            appendConditionalCheckFunc(code);
         // Added end
      }
      // Added by Terry 20140823
      // Conditional Mandatory property of field
      else
      {
         if (hasValidateFieldOfcm())
         {
            code.append("\tif( getValue_('",name,"',i)=='' )\n");
            code.append("\t{\n");

            // Added by Terry 20140822
            // Conditional Mandatory property of field
            // Clear addtional readonly text(*) field for conditional mandatory field
            appendConditionalClearFunc(code);
            // Added end
            
            code.append("\t\treturn;\n");
            code.append("\t}\n");
            
            // When validate field value is changed, set addtional readonly text(*) field for conditional mandatory field
            appendConditionalCheckFunc(code);
         }
      }
      // Added end
      code.append("}\n");
   }


   private void appendCheckFunction( AutoString code ) throws FndException
   {
      code.append("\nfunction check");
      code.append(getJavaScriptName()); //JAPA
      code.append("(i)\n{\n");
      
      if (!isAccurateFld())
         code.append("\tfld = getField_('",name,"',i);\n");
      else
         code.append("\tfld = getField_('",getAccurateDisplayFldName(),"',i);\n");

      if( isDateTime() || isTime() )
      {
         if (mask_given)         
            code.append("\tformatDate_(fld,'",getMask(),"','",getBlock().getName(),"');\n");
         else
         {
            String mask_var="";
            switch(type_id)
            {
               case DataFormatter.DATETIME:
                  mask_var = "MASK_DATETIME";
                  break;
               case DataFormatter.DATE:
                  mask_var = "MASK_DATE";
                  break;
               case DataFormatter.TIME:
                  mask_var = "MASK_TIME";
                  break;
            }
            if (isLongYear())
               mask_var = "__addLongYear(" + mask_var + ")";
            code.append("\tformatDate_(fld,",mask_var,",'",getBlock().getName(),"');\n");
         }
      }

     /* if( read_only )
      {
         if( insertable )
         {
             code.append("\tif( ");
             code.append("getRowStatus_('",getBlock().getName(),"',i)!='New__'");
             code.append(") return false;\n");
         }
      }*/

      if( upper_case )
         code.append("\tfld.value = fld.value.toUpperCase();\n");

      if( mandatory && !isCheckBox() ) //no need to check mandatory for checkboxes
      {
         if (formatter.getBaseTypeId(type_id) == formatter.NUMBER)// && hasValidation())
         {
            code.append("\tif (!checkMandatory_(fld,'");
            code.append(getJSLabel(),"','",Str.nvl(mandatory_error_message,""),"')) return;\n");
            // Modified by Terry 20140410
            // Check and Calculate numberic field value
            // Original:
            /*
            if (type_id == formatter.MONEY)
               code.append("\treturn checkMoneyValue_(fld,'");
            else
               code.append("\treturn checkNumberValue_(fld,'");
            code.append(getJSLabel(),"','');\n");
            */
            if (type_id == formatter.MONEY)
               code.append("\tif (checkMoneyValue_(fld,'");
            else
               code.append("\tif (checkNumberValue_(fld,'");
            code.append(getJSLabel(),"',''))\n");
            code.append("\t{\n");
            code.append("\t\tif(fld.value.indexOf('=') == 0)\n");
            code.append("\t\t\tfld.value = eval(fld.value.substring(1));\n");
            code.append("\t\treturn true;\n");
            code.append("\t}\n");
            code.append("\telse\n");
            code.append("\t\treturn false;\n");
            // Modified end
         }
         else
         {
            code.append("\treturn checkMandatory_(fld,'");
            code.append(getJSLabel(),"','",Str.nvl(mandatory_error_message,""),"');\n");
         }
      }
      // Added by Terry 20140822
      // Conditional Mandatory property of field
      else if (!mandatory && !isCheckBox() && hasConditionalMandatory())
      {
         Buffer condition_buff = getConditionalMandatory();
         if (condition_buff != null && condition_buff.countItems() > 0)
         {
            // Get conditional mandatory if clause
            String condition_man_js = getConditionalMandatoryIf(condition_buff);
            if (formatter.getBaseTypeId(type_id) == formatter.NUMBER)
            {
               if (!Str.isEmpty(condition_man_js))
               {
                  code.append(condition_man_js);
                  code.append("\t\tif (!checkMandatory_(fld,'");
                  code.append(getJSLabel(),"','",Str.nvl(mandatory_error_message,""),"')) return;\n");
               }
               else
               {
                  code.append("\tif (!checkMandatory_(fld,'");
                  code.append(getJSLabel(),"','",Str.nvl(mandatory_error_message,""),"')) return;\n");
               }
               // Modified by Terry 20140410
               // Check and Calculate numberic field value
               // Original:
               /*
               if (type_id == formatter.MONEY)
                  code.append("\treturn checkMoneyValue_(fld,'");
               else
                  code.append("\treturn checkNumberValue_(fld,'");
               code.append(getJSLabel(),"','');\n");
               */
               if (type_id == formatter.MONEY)
                  code.append("\tif (checkMoneyValue_(fld,'");
               else
                  code.append("\tif (checkNumberValue_(fld,'");
               code.append(getJSLabel(),"',''))\n");
               code.append("\t{\n");
               code.append("\t\tif(fld.value.indexOf('=') == 0)\n");
               code.append("\t\t\tfld.value = eval(fld.value.substring(1));\n");
               code.append("\t\treturn true;\n");
               code.append("\t}\n");
               code.append("\telse\n");
               code.append("\t\treturn false;\n");
               // Modified end
            }
            else
            {
               if (!Str.isEmpty(condition_man_js))
               {
                  code.append(condition_man_js);
                  code.append("\t\treturn checkMandatory_(fld,'");
                  code.append(getJSLabel(),"','",Str.nvl(mandatory_error_message,""),"');\n");
                  code.append("\treturn true;\n");
               }
               else
               {
                  code.append("\treturn checkMandatory_(fld,'");
                  code.append(getJSLabel(),"','",Str.nvl(mandatory_error_message,""),"');\n");
               }
            }
         }
      }
      // Added end
      else if (formatter.getBaseTypeId(type_id) == formatter.NUMBER)// && hasValidation())
      {
         // Modified by Terry 20140410
         // Check and Calculate numberic field value
         // Original:
         /*
         if (type_id == formatter.MONEY)
            code.append("\treturn checkMoneyValue_(fld,'");
         else
            code.append("\treturn checkNumberValue_(fld,'");
         code.append(getJSLabel(),"','');\n");
         */
         if (type_id == formatter.MONEY)
            code.append("\tif (checkMoneyValue_(fld,'");
         else
            code.append("\tif (checkNumberValue_(fld,'");
         code.append(getJSLabel(),"',''))\n");
         code.append("\t{\n");
         code.append("\t\tif(fld.value.indexOf('=') == 0)\n");
         code.append("\t\t\tfld.value = eval(fld.value.substring(1));\n");
         code.append("\t\treturn true;\n");
         code.append("\t}\n");
         code.append("\telse\n");
         code.append("\t\treturn false;\n");
         // Modified end
      }
      else
         code.append("\treturn true;\n");
      code.append("}\n");
   }


   private void appendURL( AutoString code,
                           String url,
                           ASPField[] param,
                           String[] alias ) throws FndException
   {
      code.append("\t\t",url,"\n");

      if (url.indexOf("__DYNAMIC_LOV_VIEW") != -1)
         code.append("\t\t+ '&__KEY_VALUE=' + ","URLClientEncode(getValue_('",getName(),"',i))\n");

      for( int i=0; i<param.length; i++ )
      {
         code.append("\t\t+ '");
         if( i==0 && url.indexOf('?')<0 )
            code.append('?');
         else
            code.append('&');

         code.append(alias[i],"=' + ");
         //if( param.isHidden() )
         //   code.append("f.",argname,".value\n");
         String argname = param[i].getName();
         if( param[i].isSelectBox() )
         {
            code.append("SelectURLClientEncode('",argname,"',i)");
            /* code.append("getField_('",argname,"',i).options[");
               code.append("getField_('",argname,"',i).selectedIndex].value\n"); */
         }
         else
         {
            if (argname.equals(getName()) && url.indexOf("__DYNAMIC_LOV_VIEW") != -1)
               code.append("URLClientEncode(key_value)\n");
            else
            {
              if (url.indexOf("__LOV=Y") != -1 && param[i].isHidden() && !param[i].hasGlobalConnection())  
                code.append("((getValue_('",argname,"',i) == null)? '':URLClientEncode(getValue_('",argname,"',i)))\n");
              else
                code.append("URLClientEncode(getValue_('",argname,"',i))\n");
            }   
         }
      }
   }

   //==========================================================================
   //  HTML generation: LOV + Validation Tags
   //==========================================================================

   /**
    * Generate, into the specified AutoString, HTML code that implements
    * LOV functionality for this ASPField.
    *<p>
    * Note! This method is not supposed to be called from JScript code.
    */
   public void appendLOVTag( AutoString html,
                             int row_nr,
                             boolean in_assignment ) throws FndException
   {
      if(DEBUG) debug("ASPField.appendLOVTag("+html+","+row_nr+","+in_assignment+")");

      if( hidden ) return;
      if( !hasLOV() ) return;

      if(getASPPage() instanceof ASPPageProvider)
         in_assignment = false;

      ASPConfig cfg = getASPPage().getASPConfig();

      String keyShortcut = " onkeydown=\"if(catchKeyCombination(event)){ " +
                           "lov" + getJavaScriptName() + "(" + row_nr + ")" +
                           "; return false;}\" ";
      html.append(keyShortcut);

      //Bug id 42390, start
      Buffer lov_queries = null;
      if (!"2003".equals(cfg.getApplicationCompatibility()))
         lov_queries = getLovQueries();
      //Bug id 42390, end
      
      if(show_lov_popup && lov_queries!=null && lov_queries.countItems()>1)
      {
        updateLovPopup(row_nr,lov_queries);        
        html.append("> <a href=\"javascript:__lov_row_no=",row_nr+";",lov_popup.generateCall()+"\"");
        html.append(cfg.getLOVTag2Pop(in_assignment));
      }
      else
      {
        html.append(cfg.getLOVTag1(in_assignment));
        html.append(getJavaScriptName(),"("); //JAPA
        html.appendInt(row_nr);
        // Modified by Terry 20120821
        // Set multi-select LOV
        // Original: html.append(")",cfg.getLOVTag2(in_assignment));
        html.append(")\" id=\"lovhref" + row_nr + "_" + getJavaScriptName() + "\"", " name=\"lovhref_" + getJavaScriptName(),cfg.getLOVTag2(in_assignment));
        // Modified end
      }

      if(DEBUG) debug("  appendLOVTag(): html="+html);
   }

   void appendIidLOVTag( AutoString html,
                         int row_nr,
                         boolean in_assignment ) throws FndException
   {
      if(DEBUG) debug("ASPField.appendIidLOVTag("+html+","+row_nr+","+in_assignment+")");

      if( hidden ) return;
      ASPConfig cfg = getASPPage().getASPConfig();
      
      String keyShortcut = " onkeydown=\"if(catchKeyCombination(event)){ " +
                           "lov" + getJavaScriptName() + "(" + row_nr + ")" +
                           "; return false;}\" ";
      html.append(keyShortcut);
      
      html.append(cfg.getLOVTag1Iid(in_assignment));
      html.append(getJavaScriptName(),"(");
      html.appendInt(row_nr);
      html.append(")",cfg.getLOVTag2(in_assignment),">");

      if(DEBUG) debug("  appendIidLOVTag(): html="+html);
   }

   /**
    * Generate, into the specified AutoString, HTML code that implements
    * Validate functionality for this ASPField.
    *<p>
    * Note! This method is not supposed to be called from JScript code.
    */
   public void appendValidationTag( AutoString html,
                                    int row_nr,
                                    boolean in_assignment ) throws FndException
   {
      if(DEBUG) debug("ASPField.appendValidationTag("+html+","+row_nr+","+in_assignment+")");

      if( hidden ) return;
      String quot = (in_assignment && !(getASPPage() instanceof ASPPageProvider)) ? "\\\"" : "\"";

      if(max_length > 0 )
      {
         html.append("maxlength=");
         html.appendInt(max_length);
         html.append(" ");
      }

      //bind client script for number fields maxlength handling
      if( DataFormatter.getBaseTypeId(this.getTypeId())==DataFormatter.NUMBER)
      {
         html.append("onkeypress='return validateNumberFieldMaxLength(event, "+ getMaxLength() +", "+ (getTypeId()==DataFormatter.MONEY) +");'");
         html.append(" ");
      }
      
      if(isCheckBox())
      {
        //to work for checkboxes in editable tables, first call setCheckBox_() then call validate funtion
        html.append("OnClick=",quot,"setCheckBox_('", getName(),"',checked,");
        html.appendInt(row_nr);
        html.append(",'",getCheckedValue(),"');");
      }
      else
         html.append("OnChange=");

      if(!(validate_function == null) && !(validate_function.equals("")))
      {
         if (isCheckBox())
           html.append(validate_function);
         else
           html.append(quot,validate_function);
         html.append("(",Integer.toString(row_nr),")",quot);
      }
      else
      {
         if (isCheckBox())
           html.append("validate");
         else
           html.append(quot,"validate");
         html.append(getJavaScriptName(),"("); //JAPA
         html.appendInt(row_nr);
         html.append(")",quot);
      }

      if(DEBUG) debug("  appendValidationTag: html="+html);
   }

   void appendCheckTag( AutoString html,
                        int row_nr,
                        boolean in_assignment ) throws FndException
   {
      if(DEBUG) debug("ASPField.appendCheckTag("+html+","+row_nr+","+in_assignment+")");

      String quot = (in_assignment && !(getASPPage() instanceof ASPPageProvider)) ? "\\\"" : "\"";
      html.append("OnBlur=",quot,"check");
      html.append(getJavaScriptName(),"("); //JAPA
      html.appendInt(row_nr);
      html.append(")",quot);

      if(DEBUG) debug("  appendCheckTag: html="+html);
   }


   /**
    * Generate, into the specified AutoString, HTML code that implements
    * read only functionality for this ASPField.
    *<p>
    * Note! This method is not supposed to be called from JScript code.
    */
   public void appendReadOnlyTag( AutoString html,
                           boolean in_assignment,
                           String row_status) throws FndException
   {
      if(DEBUG) debug("ASPField.appendReadOnlyTag("+html+","+in_assignment+","+row_status+")");

      if( isReadOnly(row_status) )
      {
         //if(bgcolor==null)
         //   bgcolor = getASPPage().getASPConfig().getFormBgColor();
         String quot = (in_assignment && !(getASPPage() instanceof ASPPageProvider)) ? "\\\"" : "\"";
         //bug id 42211 start.
         if( !getASPManager().isNetscape4x() )
        //bug id 42211 end.
            html.append(" READONLY ");
         else
            html.append(" READONLY style=italicTextValue ");
      }

      if(DEBUG) debug("  appendReadOnlyTag: html="+html);
   }

   //==========================================================================
   //  Temporary flag used by ASPTable.populate
   //==========================================================================

   void setUsedAsParameter( boolean flag ) throws FndException
   {
      used_as_parameter = flag;
   }

   boolean isUsedAsParameter()
   {
      return used_as_parameter;
   }

   //==========================================================================
   //  Other
   //==========================================================================


   private String[] listToArray( String list )
   {
      return listToArray(list,",");
   }

   private String[] nameListToArray( String list )
   {
      return listToArray(list,", \t\r\n");
   }

   private String[] listToArray( String list, String delimiters )
   {
      if( Str.isEmpty(list) ) return null;
      Vector v = new Vector();
      StringTokenizer st = new StringTokenizer(list,delimiters);
      while( st.hasMoreTokens() )
         v.addElement(st.nextToken());

      String[] arr = new String[v.size()];
      v.copyInto(arr);
      return arr;
   }

   private void parseParameterList( String list, Vector fields, Vector aliases ) throws FndException
   {
      parseParameterList( list, fields, aliases,false );
   }

   // Note: Quick 'n moderately dirty fix for LOV parameter fields with name != dbname
   private void parseParameterList( String list, Vector fields, Vector aliases, boolean lov ) throws FndException
   {
      fields.setSize(0);
      aliases.setSize(0);
      if( Str.isEmpty(list) ) return;
      StringTokenizer main = new StringTokenizer(list,",");
      ASPPage page = getASPPage();
      while( main.hasMoreTokens() )
      {
         String argname, alias;
         StringTokenizer st = new StringTokenizer(main.nextToken());
         switch(st.countTokens())
         {
            case 1:
               argname = st.nextToken();
               alias = argname;
               break;

            case 2:
               argname = st.nextToken();
               alias = st.nextToken();
               break;

            default:
               throw new FndException("FNDBADINPARAM: Invalid syntax for parameter list: '&1'",
                                      list);
         }
         ASPField arg = page.getASPField(argname);
         fields.addElement(arg);
         /*if(lov)
            alias = arg.getDbName();*/
         aliases.addElement(alias);
      }
   }

   private ASPField[] toASPFieldArray( Vector v )
   {
      ASPField[] arr = new ASPField[v.size()];
      v.copyInto(arr);
      return arr;
   }

   private String[] toStringArray( Vector v )
   {
      String[] arr = new String[v.size()];
      v.copyInto(arr);
      return arr;
   }


   private int countParameters( String name_list )
   {
      if( Str.isEmpty(name_list) ) return 0;
      StringTokenizer st = new StringTokenizer(name_list,", \t\r\n");
      return st.countTokens();
   }

   private String arrayToList_( String[] array )
   {
      if( array==null ) return null;
      AutoString buf = new AutoString();
      for( int i=0; i<array.length; i++ )
      {
         if( i>0 ) buf.append(',');
         buf.append(array[i]);
      }
      return buf.toString();
   }

   public String findValues(String[] clientValues, String expression)
   {
      String valueList = "";

      if(clientValues!=null && expression.indexOf("%")>=0)
      {
         String temp = expression.substring(0,expression.indexOf("%"));
         for(int i=0; i<clientValues.length; i++)
         {
            if(clientValues[i].regionMatches(true,0,temp,0,temp.length()))
            {
               valueList = valueList + clientValues[i]+";";
            }
         }
         if(Str.isEmpty(valueList)) 
            getASPManager().showError("FNDLOVNODATA: No data found.");
      }
      if(clientValues==null) valueList = expression;
      return valueList;
   }

   private int findValue( String[] arr, String value )
   {
      if( value!=null && arr!=null )
         for( int i=0; i<arr.length; i++ )
            if( value.equals(arr[i]) )
               return i;
      return -1;
   }

   private void debugStringArray( String[] arr, String name )
   {
      debug("   "+name+":");
      if( arr==null ) return;
      for( int i=0; i<arr.length; i++ )
         debug("      "+i+" : "+arr[i]);
   }


   private void reverse2Values( String[] arr )
   {
      String tmp = arr[0];
      arr[0] = arr[1];
      arr[1] = tmp;
   }

   void setFirstAddressField()
   {
      address_field = true;
   }

   boolean isFirstAddressField()
   {
      return address_field;
   }

   String getLOVInParams()
   {
      return lov_in_parameters;
   }

   String getLOVView()
   {
      return lov_view_name;
   }

   /**
    *Sets a Tooltip-like description to a given field
    *@param field The field to be shown in the tooltip
    */
   public ASPField setTooltip(ASPField field)
   {
       return setTooltip(field,false);
   }

   /**
    *Sets a Tooltip-like description to a given field
    *@param field The field to be shown in the tooltip
    *@param label Value 'true' will include a label to the tooltip
    */
   public ASPField setTooltip(ASPField field, boolean label)
   {
      try
      {
         modifyingMutableAttribute("TOOLTIP");
         getBlock().notifyHiddenFlagDirty();
         if(!getASPManager().isNetscape4x())
            field.setHidden();
         this.tooltip = true;
         this.tooltip_field = field;
         if(label)
            this.tooltip_label = "<B>"+field.getLabel()+":</B> ";
         else
            this.tooltip_label = "";
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Returns the tooltip label of this field.
    */
   public String getTooltipLabel(){
       return  this.tooltip_label;
   }

   /**
    * Returns the field name which has a tooltip.
    */
   public ASPField getTooltipField(){
       return  this.tooltip_field;
   }

   /**
    * Unsets the Tooltip-like description of a field.
    *
    * @see #setTooltip
    */
   public ASPField unsetTooltip()
   {
      if(!this.tooltip) return this;
      try
      {
         modifyingMutableAttribute("TOOLTIP");
         getBlock().notifyHiddenFlagDirty();
         this.tooltip = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   /**
    * Returns true if a tooltip has been set for this field.
    */
   public boolean hasTooltip()
   {
      return this.tooltip;
   }

   /**
    * Returns true if the type of this field is DATE or DATETIME.
    */
   public boolean isDateTime() throws FndException
   {
      //formatter.getBaseTypeId returns formatter.DATETIME
      //for Datetime, Date, and Time
      return ( type_id == formatter.DATETIME || type_id == formatter.DATE);
   }


   
   /**
    * Returns true if the type of this field is TIME.
    */
   public boolean isTime() throws FndException
   {
      return ( type_id ==formatter.TIME);
   }


  /**
    * Generate, into the specified AutoString, HTML code that implements
    * Calendar functionality for this ASPField.
    */
   public void appendCalendarTag( AutoString html,int row_nr) throws FndException
   {
      if(DEBUG) debug("ASPField.appendCalendarTag("+html+","+row_nr+")");

      if( hidden ) return;
      if( !isDateTime() ) return;
      if (disable_calendar_tag) return;

      ASPManager mgr = getASPManager();
      if (!(mgr.isExplorer() || mgr.isMozilla())) return;

      boolean time = (type_id == formatter.DATETIME || type_id == formatter.TIME);

      String _mask = getMask();
      String _label = getJSLabel();
      _mask = mgr.replace(_mask,"'","\\\'");

      ASPConfig cfg = getASPPage().getASPConfig();
      html.append(cfg.getCalendarTag1());
      html.append(name);
      html.append(cfg.getCalendarTag2());
      if (mgr.getASPPage().getASPLov() == null)
      {
         if(!(validate_function == null) && !(validate_function.equals("")))
            html.append("'",validate_function,"'");
         else
            html.append("'validate",getJavaScriptName(),"'");
      }
      else
         html.append("''");
      html.append(",'",_mask,"',");
      html.appendInt(row_nr);
      html.append(",'",_label,"'");
      html.append(",'",""+time,"'");
      html.append(cfg.getCalendarTag4());
      //html.append(")",cfg.getLOVTag2(in_assignment));
      html.append(cfg.getCalendarTag3());

      if(DEBUG) debug("  appendCalendarTag(): html="+html);
   }

   void appendTooltipTag( AutoString html,int row_nr, boolean editable) throws FndException
   {
      if(DEBUG) debug("ASPField.appendTooltipTag("+html+","+row_nr+","+editable+")");

      ASPManager mgr = getASPManager();

      if(!mgr.isNetscape4x() && hasTooltip())
      {
         if (editable)
         {
            String tooltip_fld_name = getTooltipField().getName();
            String fld_script = "";

            if (row_nr == -1)
               fld_script = "f."+tooltip_fld_name;
            else
               fld_script = "getField_('"+tooltip_fld_name+"',"+row_nr+")";

            html.append(" onMouseover=\"javascript:if(");
            html.append(fld_script+" && "+fld_script,".value!='')");
            html.append("showtip(this,event,'"+getTooltipLabel()+"'+"+fld_script);
            html.append(".value)\" onMouseOut=\"javascript:hidetip()\" ");
         }
         else
         {
            ASPField tip_field = getTooltipField ();
            //String tip = toSingleLine(tip_field.convertToClientString(getBlock().getASPRowSet().getValue(tip_field)));
            String tip = "";
            if (row_nr == -1) //single layout modes (i.e. not a table)
               tip = tip_field.convertToClientString(getBlock().getASPRowSet().getValue(tip_field.getName()));
            else
               tip = tip_field.convertToClientString(getBlock().getASPRowSet().getValueAt(row_nr-1,tip_field.getName()));

            tip = toSingleLine(tip);
            tip = mgr.replace(mgr.HTMLEncode(tip),"\'","\\\'");

            html.append(" onMouseover=\"showtip(this,event,'"+getTooltipLabel()+tip+"')\" onMouseOut=\"hidetip()\" ");
         }
     }
     else
        return;
   }

   private String toSingleLine(String tip)
   {
      ASPManager mgr = getASPManager();
      String tip_line = mgr.translate("ASPTOOLTIPNOTAVAILABLE: Not Available");
      if(!mgr.isEmpty(tip))
      {
         StringReader temp_String_Reader = new StringReader(tip);
         tip_line = "";
         int temp_String = 0;
         try
         {
            while((temp_String = temp_String_Reader.read())!=-1)
            {
               if(temp_String==13)
               {
                  tip_line += " <BR>";
                  temp_String_Reader.skip(1);
               }
               else
                  tip_line += (char)temp_String;
            }
         }
         catch(Exception e){}
       }
       return tip_line;
   }


  /**
   * Generate, into the specified AutoString, HTML code which appends the
    * translated Date/DateTime mask for this ASPField.
   */
   public void appendTranslatedMask(AutoString html) throws FndException
   {
      if(DEBUG) debug("ASPField.appendTranslatedMask("+html+")");
      if(!isDateTime())
         if(!isTime())
            return;

      html.append("<font class=\"maskText\"> &nbsp;(");
      html.append(translated_mask,") </font>");
   }

  /**
   * returns the mask of the field
   */
   public String getTranslatedMask()
   {
      if(DEBUG) debug("ASPField.getTranslatedMask");
      try
      {
         if(!isDateTime())
            if(!isTime())
               return null;

         return translated_mask;
      }
      catch (Exception any)
      {
         return null;
      }
   }

   private void translateDateTimeMask(String mask)
   {
      ASPPage page = getASPPage();
      translated_mask = mask;
      String[] maskarr = page.getASPConfig().getTranslatedDateTimeMask();
      ASPManager mgr = getASPManager();
     if(maskarr!=null)
     {
       char[] tempChars = translated_mask.toCharArray();
       StringBuffer tempStr = new StringBuffer();
       for (int i=0;i<tempChars.length;i++)
       {
          switch (tempChars[i])

          {
             case 'G': tempStr.append(maskarr[ASPConfigFile.ERA]);
                        break;
             case 'y': tempStr.append(maskarr[ASPConfigFile.YEAR]);
                        break;
             case 'M': tempStr.append(maskarr[ASPConfigFile.MONTH_IN_YEAR]);
                        break;
             case 'd': tempStr.append(maskarr[ASPConfigFile.DAY_IN_MONTH]);
                        break;
             case 'h': tempStr.append(maskarr[ASPConfigFile.HOUR_IN_AMPM_112]);
                        break;
             case 'H': tempStr.append(maskarr[ASPConfigFile.HOUR_IN_DAY_023]);
                        break;
             case 'm': tempStr.append(maskarr[ASPConfigFile.MINUTE_IN_HOUR]);
                        break;
             case 's': tempStr.append(maskarr[ASPConfigFile.SECOND_IN_MINUTE]);
                        break;
             case 'S': tempStr.append(maskarr[ASPConfigFile.MILLISECOND]);
                        break;
             case 'E': tempStr.append(maskarr[ASPConfigFile.DAY_IN_WEEK]);
                        break;
             case 'D': tempStr.append(maskarr[ASPConfigFile.DAY_IN_YEAR]);
                        break;
             case 'F': tempStr.append(maskarr[ASPConfigFile.DAY_OF_WEEK_IN_MONTH]);
                        break;
             case 'w': tempStr.append(maskarr[ASPConfigFile.WEEK_IN_YEAR]);
                        break;
             case 'W': tempStr.append(maskarr[ASPConfigFile.WEEK_IN_MONTH]);
                        break;
             case 'a': tempStr.append(maskarr[ASPConfigFile.AMPM_MARKER]);
                        break;
             case 'k': tempStr.append(maskarr[ASPConfigFile.HOUR_IN_DAY_124]);
                        break;
             case 'K': tempStr.append(maskarr[ASPConfigFile.HOUR_IN_AMPM_011]);
                        break;
             case 'z': tempStr.append(maskarr[ASPConfigFile.TIME_ZONE]);
                        break;
             default:tempStr.append(tempChars[i]);
                        break;

          }
       }
       translated_mask = tempStr.toString();

      }
   }


   /**
    * This method is used for updating the popup menu of LOV's with saved queries.
    * Not to be used by developers.
    */
   public void updateLovPopup(int row_nr)throws FndException
   {
      Buffer buff = getLovQueries();
      updateLovPopup(row_nr, buff);
   }

   private void updateLovPopup(int row_nr, Buffer buff) throws FndException
   {
       lov_popup.removeAllItems();
       lov_popup.addItem(getASPManager().translate("FNDLOVPOPUPNEWQUERY: New Query"),"preLov"+getJavaScriptName()+"(__lov_row_no,"+"''"+")");
       if(buff!=null)
       {
          for(int c=0;c<buff.countItems();c++)
          {
             String item_name = buff.getItem(c).getName();
             item_name = item_name.substring(item_name.indexOf(ProfileUtils.ENTRY_SEP)+1);
             lov_popup.addItem(item_name,"preLov"+getJavaScriptName()+"(__lov_row_no,'"+getLovParams(buff.getItem(c).getString())+"')");
          }
       }
   }

   //public Buffer getLovQueries() throws FndException  //RIRALK: why is this public ?????
   private Buffer getLovQueries() throws FndException
   {
      String url="";
      ASPConfig cfg = getASPPage().getASPConfig();
      String lov_url =  getLOVURL();
      try{

          if(!Str.isEmpty(getLOVView()))   //dynamic LOV
          {
             String app_path = cfg.getApplicationContext();
             if (app_path.startsWith("/"))
                app_path = app_path.substring(1);
             url = app_path + "/Pages/fndweb.dynamiclov#" + (getLOVView()==null?null:getLOVView().toLowerCase());
            
             ASPProfile p = ASPProfileCache.get(getASPPage(),url);
             ProfileBuffer qry_buf = (ProfileBuffer)p.get("Block/LOV/QUERY_SECTION");
             if (qry_buf!=null)
               qry_buf.sort(new PositionComparator());
             return qry_buf;
          }
          else  //static LOV
          {

             int pos = lov_url.indexOf(".page"); //remove the .page part
             if (pos>0)
              lov_url = lov_url.substring(0,pos);

             if ( lov_url.indexOf("/") < 0 ) //check if module is included in LOV url
               lov_url = getASPPage().getComponent() + "." + lov_url;  //add current module if not included in url

             lov_url = lov_url.toLowerCase();

             url = cfg.getApplicationContext() + "/Pages/" + lov_url;
             if (url.startsWith("/"))
                url = url.substring(1);

             ASPProfile p = ASPProfileCache.get(getASPPage(),url);
             Buffer b = p.get("Block");
             if (b!=null)
             {
               Buffer blkbuf = b.getBuffer(0);  //use the first buffer since we don't know the name of the block
               if (blkbuf!=null)
               {
                 Item q_item =  blkbuf.findItem("QUERY_SECTION");
                 if (q_item!=null)
                 {
                   ProfileBuffer qry_buf = (ProfileBuffer)q_item.getBuffer();
                   if (qry_buf!=null)
                     qry_buf.sort(new PositionComparator());
                   return qry_buf;
                 }
               }
             }

             return null;
          }
      }
      catch(Exception e){
          if(DEBUG)debug("Exception in getLovQueries() "+e.getMessage());
          return null;
      }
   }

   private String getLovParams(String str)
   {
      StringTokenizer params = new StringTokenizer(str,(char)31+"");
      String lov_qry_str="";
      String lov_token = "";
      String lov_key = "";
      String lov_value = "";
      try{
          while(params.hasMoreTokens())
          {
             lov_token = params.nextToken();
             if(lov_token.indexOf("^")>0)
             {
                lov_key = lov_token.substring(0,lov_token.indexOf("^"));
                lov_value = getASPManager().URLEncode(lov_token.substring(lov_token.indexOf("^")+1,lov_token.length()));
                if(lov_key.equals("__CASESS_VALUE"))
                   lov_key = "__CASESS";
                if(!Str.isEmpty(lov_value))
                   lov_qry_str += "&"+lov_key+"="+lov_value;
             }
          }
      }catch(Exception e){if(DEBUG)debug("Exception in getLovParams(String str)");}
      return  lov_qry_str;
   }


   /**
    * Draw this field as a password field. This is an immutable method and can not called after the
    * page is defined.
    */
   public ASPField setPasswordField()
   {

      try
      {
         modifyingImmutableAttribute("PASSWORDTYPE");
         password_type = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   
   /**
    * Returns true if this field is defined as a password field.
    */
   public boolean isPasswordField()
   {

      return password_type;
   }

   /** Disables popup menu shown when there are saved queries in lov icon
    * @return a reference to this ASPfield
    */
   public ASPField disableLOVPopup()
   {
      try
      {
         modifyingImmutableAttribute("SHOWLOVPOPUP");
         show_lov_popup = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }

   
   /**
    * Returns true if this field has defined with a set function.
    */
   public boolean hasSetFunction()
   {
      return has_setfunction;
   }


//========================================================================================
// Depricated Methods: These methods are introduced to use Activities in FNDWEB and 
// never used. Due to New Programing model, all these methods are now obsolete. Should 
// use new Programing model instead
//========================================================================================
   /**
    * @deprecated - use new programing model instead.
    */
   public ASPField setActivityDynamicLOV(String entity, String handler, String operation){return this;}

   /**
    * @deprecated - use new programing model instead.
    */
   public ASPField setActivityDynamicLOV(String entity, String handler, String operation, String in_parameters){return this;}

   /**
    * @deprecated - use new programing model instead.
    */
   public ASPField setActivityDynamicLOV(String entity, String handler, String operation, String in_parameters, int width, int height, boolean enable_multichoice){return this;}

   /**
    * @deprecated - use new programing model instead.
    */
   public ASPField setActivityLOV(String handler, String operation, String url, String in_parameters, int width, int height, boolean enable_multichoice ){return this;}

//============================================================================================


   /** If the field is mandory and not a check box, the default behavior is remove all
    * the validated fileds when the field value is set to empty. You can swith off/on
    * useing this method before the page is defined. This method is immutable.
    * @param clear true (switch on) or false (swithc off)
    */
   public ASPField setClearValidatedFieldsOnEmpty(boolean clear)
   {
      try
      {
         // Modified by Terry 20130619
         // Modified validation_url, validation_method, validation_in_parameters, validation_out_parameters, validation_clear_fields_on_empty to mutable
         // Original:
         // modifyingImmutableAttribute("CLEARWHENEMPTY");
         modifyingMutableAttribute("CLEARWHENEMPTY");
         // Modified end
         validation_clear_fields_on_empty = clear;
      }
      catch ( Throwable any)
      {
         error(any);
      }
      return this;
   }


   /**
    * Set the ASPField as an accurate field with number of decimal places.
    * NOTE: A number can hold a total of 17 digits.
    * @return a reference to this ASPfield
    * @see getAccurateDisplayFldName()
    * @see getAccurateFldDisplayValue(double value)
    */
   public ASPField setAsAccurate()
   {
      try
      {
         modifyingImmutableAttribute("ACCURATEFIELD");
         
         if (type_id != DataFormatter.MONEY)
            throw new FndException("FNDNANACCFLD: Field '&1' is not a Money field.", name);

         String mask = "";
         int no_of_dec = getNoOfDecimals();
         for (int i=0; i<17-no_of_dec; i++)
            mask += "#";

         mask += ".";

         for (int i=0; i<no_of_dec; i++)
            mask += "#";

         this.accurate_client_formatter = formatter;
         this.pre_formatter = getASPPage().getASPConfig().getDataFormatter(type_id,mask);
         this.formatter = pre_formatter;
         this.mask = mask;

         is_accuracy_fld = true;
         accuracy_fld_name = name + FND_ACCURATE;
      }
      catch ( Throwable any)
      {
         error(any);
      }
      
      return this;
   }
   
   public boolean isAccurateFld()
   {
      return is_accuracy_fld;
   }

   int getNoOfDecimals()
   {
      return getASPManager().getASPConfig().getNoOfDecimals();
   }
   
   public DataFormatter getAccurateClientFormatter()
   {
      return accurate_client_formatter;
   }
   
   DataFormatter getAccurateFormatter()
   {
      return formatter;
   }
   
   /**
    * Called from Application pages to return both precision and rounded off value to client
    */
   public String formatAccurateNumber( double value )
   {
      try
      {
         if (type_id != DataFormatter.MONEY)
            throw new FndException("FNDFRMNOTACCNUM: The field &1 cannot be formatted as Money", name);

         if( Double.isNaN(value) )
            return "";

         if( Double.isInfinite(value) )
            throw new FndException("FNDFRMINFNUM: Infinite Number in field &1", name);

         Double numVal = new Double(value);
         String strVal = formatter.format(numVal)+(char)29+accurate_client_formatter.format(numVal);
         
         return strVal;
      }
      catch( Throwable e)
      {
         error(e);
         return null;
      }
   }
   
   
   /**
    * Returns the dummy HTML form element's name holding the non-accurate value
    * @see setAccuracy()
    * @see getAccurateFldDisplayValue(double value)
    */
   public String getAccurateDisplayFldName()
   {
      return accuracy_fld_name;
   }
   
   /**
    * Convert a numeric value to the nonaccurate display value using this
    * field's non_accurate format mask. 
    * This function returns an empty String ("") if the specified value is Not-a-Number
    * @return nonaccurate display string value.
    * @see setAccuracy()
    * @see getAccurateDisplayFldName()
    */
   public String getAccurateFldDisplayValue(double value) throws FndException
   {
      return accurateFormat(accurate_client_formatter, value);
   }
   
   private String getAccurateFldValue(double value) throws FndException
   {
      return accurateFormat(formatter, value);
   }
   
   private String accurateFormat(DataFormatter dataFormatter, double value) throws FndException
   {
      if (!isAccurateFld()) 
         throw new FndException("FNDACCFORMATNOACC: The field &1 has no accuracy set.", name);

      try
      {
         if( formatter.getBaseTypeId(type_id) != formatter.NUMBER )
            throw new FndException("FNDFRMNOTNUM: The field &1 cannot be formatted as a Number", name);

         if( Double.isNaN(value) )
         {
               return "";
         }

         if( Double.isInfinite(value) )
            throw new FndException("FNDFRMINFNUM: Infinite Number in field &1", name);

         return dataFormatter.format(new Double(value));
      }
      catch( Throwable e)
      {
         error(e);
         return null;
      }
   }
   
   /**
    * Sets the ASPField to format date as 'yyyy'. This method should only be called
    * for the fields that are defined as Date or DateTime.
    */
   public ASPField setAsLongYear()
   {  
      ASPPage page = getASPPage();
      
      try
      {
         modifyingImmutableAttribute("LONG_YEAR");
         is_long_year = true;
         page.registerLongYearField(this);
      }
      catch ( Throwable any)
      {
         error(any);
      }
      return this;
   }
   
   public boolean isLongYear()
   {
      return this.is_long_year;
   }
   
   void repalceWithLongYear()
   { 
      try{
			if((type_id == DataFormatter.DATE || type_id == DataFormatter.DATETIME) && !mask.contains("yyyy") && mask.contains("yy")){
            mask = mask.replace("yy","yyyy");
            formatter = getASPPage().getASPConfig().getDataFormatter(formatter.getTypeId(),mask);
            translateDateTimeMask(mask);
         }
            
      }
      catch( Throwable e)
      {
         error(e);
      }
   }
   
   boolean isDirtyReadOnlyFlag()
   {
      return pre_read_only != read_only;
   }

   boolean isDirtyMandatoryFlag()
   {
      return mandatory != pre_mandatory;
   }

   boolean isDirtyQueryableFlag()
   {
      return queryable != pre_queryable;
   }

   boolean isDirtyHiddenFlag()
   {
      return hidden != pre_hidden;
   }

   boolean isDirtySizeFlag()
   {
      return size != pre_size ;
   }
   
   boolean isDirtyHeightFlag()
   {
      return height != pre_height;
   }
   
   boolean isDirtySimpleFlag()
   {
      return simple != pre_simple;
   }

   boolean isDirtyDataSpanFlag()
   {
      return dataSpan != pre_dataSpan;
   }
   
   void setGroupId(int group_id)
   {
      this.group_id = group_id;
   }

   int getGroupId()
   {
      return group_id;
   }

   String getValidateFunction()
   {
      if (!Str.isEmpty(validate_function))
         return validate_function;
      else
         return "validate"+getJavaScriptName();
   }
   
   String getUsageID()
   {
      return Str.isEmpty(usage_id)?"":usage_id;
   }
   
   String getTranslationKey()
   {
      return Str.isEmpty(translate_key)?"":translate_key;
   }
      
   /**
    * Sets the Rich Web Client (RWC) compatible name for this ASPField.
    * If this method is not used the ASPField name will be used as the RWC name.
    * @param rwc_name
    */
   public ASPField setRWCKeyName( String rwc_name )
   {
      try
      {
         modifyingMutableAttribute("RWC_NAME");
         if( !isUndefined() )
            dirty_validate = dirty_check = true;

         this.rwc_name = rwc_name;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   /**
    * Returnds the Rich Web Client compatible name for this ASPField.
    * @return String
    */
   public String getRWCKeyName()
   {
      return this.rwc_name;
   }   
   
   /**
    * Returns the templates set to the ASPField. This method should only be used in 
    * Activity Programing Model.
    */   
   FndAttribute getTemplate()
   {
      return template; 
   }
      
   /**
    * Returns the availability of a template.
    */   
   boolean hasTemplate()
   {
      return (getASPPage() instanceof ifs.fnd.webfeature.FndWebFeature)? template!=null: true;
   }
   
   /**
    * Fields refering attributes in agrregate reference should be set the reference useing this method.
    * @param aggregate reference aggregate name
    * @see getAggregateReference
    */
   public ASPField setAggregateReference(String aggregate)
   {
      this.aggregate = aggregate;
      return this;
   }
   
   /**
    * Returns the aggregate reference.
    * @see setAggregateReference(String)
    */
   public String getAggregateReference()
   {
      return aggregate==null?"":aggregate;
   }   
   
   /**
    * Returns true if the field has aggregate reference.
    * @see setAggregateReference(String)
    * @see getAggregateReference
    */
   public boolean hasAggregateReference()
   {
      return (aggregate != null);
   }
      
   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================

   public void appendMobileValidationTag( AutoString mobilehtml, int row_nr, boolean in_assignment ) throws FndException
   {
      if( hidden ) return;
      
      String quot = (in_assignment && !(getASPPage() instanceof ASPPageProvider)) ? "\\\"" : "\"";

      mobilehtml.append("onBlur=");
      
      if(!(validate_function == null) && !(validate_function.equals("")))
      {
         if (isCheckBox())
           mobilehtml.append(validate_function);
         else
           mobilehtml.append(quot,validate_function);
         
         mobilehtml.append("(",Integer.toString(row_nr),")",quot);
      }
      else
      {
         if (isCheckBox())
           mobilehtml.append("validate");
         else
           mobilehtml.append(quot,"validate");
         
         mobilehtml.append(getJavaScriptName(),"("); //JAPA
         mobilehtml.appendInt(row_nr);
         mobilehtml.append(")",quot);
      }

   }
   
   // ===================================================================================================
   // Added by Terry 20120821
   // Functions for new properties
   // ===================================================================================================
   
   public int getFieldWrap()
   {
      return field_wrap;
   }
   
   public ASPField setFieldWrap(int mode)
   {
      try
      {
         modifyingImmutableAttribute("FIELD_WRAP");
         field_wrap = mode;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public boolean hasShortcutValue()
   {
      return !Str.isEmpty(sv_fields) && !read_only;
   }
   
   public boolean isForceSV()
   {
      return hasShortcutValue() && sv_force;
   }
   
   public ASPField setShortcutValue(String value,boolean force)
   {
      try
      {
         modifyingImmutableAttribute("SHORTCUT_VALUE");
         sv_fields = name;
         sv_values = value;
         sv_force = force;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public ASPField setShortcutValue(String fields,String values,boolean force)
   {
      try
      {
         modifyingImmutableAttribute("SHORTCUT_VALUE");
         sv_fields = fields;
         sv_values = values;
         sv_force = force;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public void appendSVTag( AutoString html ) throws FndException
   {
      ASPManager mgr  = getASPManager();
      if(DEBUG) debug("ASPField.appendSVTag("+html+")");

      if( hidden ) return;
      if( !hasShortcutValue() ) return;

      String sv_title = mgr.translate("ASPFIELDSVTITLE: Sign");
      html.append("> <a href=\"javascript:setSignValue('",sv_fields + "','" + sv_values + "',",sv_force?"true":"false",");\">");
      html.append("<img src=\"/b2e/secured/common/images/edit.gif\" ");
      html.append("width=22 height=16 border=0 alt=\""+ sv_title +"\" title=\"" + sv_title + "\"></a");

      if(DEBUG) debug("  appendSVTag(): html="+html);
   }
   
   public ASPField setClientFunc(String func)
   {
	   try
	   {
		   modifyingMutableAttribute("CLIENT_FUNC");
		   client_func = func;
	   }
	   catch( Throwable any )
	   {
		   error(any);
	   }
	   return this;
   }
   
   public boolean hasClientFunc()
   {
	   return (!Str.isEmpty(client_func));
   }
   
   public void appendClientFuncTag( AutoString html ) throws FndException
   {
	   if(DEBUG) debug("ASPField.appendClientFuncTag("+html+")");
	   
	   if(hidden || read_only) return;
	   if(!hasClientFunc()) return;
	   
	   html.append(" " + client_func + " ");
	   
	   if(DEBUG) debug("  appendClientFuncTag(): html="+html);
   }
   
   public String getClientFunc()
   {
      return client_func;
   }
   
   public ASPField setBgColor(String bgColor)
   {
	   try
	   {
		   modifyingMutableAttribute("BG_COLOR");
		   bg_color = bgColor;
	   }
	   catch( Throwable any )
	   {
		   error(any);
	   }
	   return this;
   }
   
   public String getBgColor()
   {
	   return bg_color;
   }
   
   public String getBgColorTag()
   {
	   if(hidden) return "";
	   if(!hasBgColor()) return "";
	   
	   return " bgcolor=\"" + bg_color + "\" ";
   }
   
   public boolean hasBgColor()
   {
	   return (!Str.isEmpty(bg_color));
   }
   
   public void appendBgColorTag( AutoString html ) throws FndException
   {
	   if(DEBUG) debug("ASPField.appendBgColorTag("+html+")");
	   
	   if(hidden) return;
	   if(!hasBgColor()) return;
	   
	   html.append(" bgcolor=\"" + bg_color + "\" ");
	   
	   if(DEBUG) debug("  appendBgColorTag(): html="+html);
   }
   
   public ASPField setWfProperties()
   {
      return setWfProperties(0);
   }
   
   public ASPField setWfProperties(int wf_title_order)
   {
      return setWfProperties(wf_title_order, false);
   }
   
   public ASPField setWfProperties(int wf_title_order, boolean wf_title_value_only)
   {
      return setWfProperties(true, wf_title_order, wf_title_value_only);
   }
   
   public ASPField setWfProperties(boolean wf_title, int wf_title_order, boolean wf_title_value_only)
   {
      try
      {
         modifyingImmutableAttribute("WF_PROPERTIES");
         this.wf_title = wf_title;
         this.wf_title_order = wf_title_order;
         this.wf_title_value_only = wf_title_value_only;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public boolean getWfTitle()
   {
      return wf_title;
   }
   
   public int getWfTitleOrder()
   {
      return wf_title_order;
   }
   
   public boolean getWfTitleValueOnly()
   {
      return wf_title_value_only;
   }
   
   public ASPField setFontColor(String fontColor)
   {
      return setFontColor(fontColor, null);
   }
   
   public ASPField setFontColor(String fontColor, String fontContent)
   {
      try
      {
         modifyingMutableAttribute("FONT_COLOR");
         font_color = fontColor;
         font_content = fontContent;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public String getFontColor()
   {
      return font_color;
   }
   
   public String getFontContent()
   {
      return font_content;
   }
   
   public boolean hasFontColor()
   {
      return (!Str.isEmpty(font_color));
   }
   
   public String getFontColorTag(String value)
   {
      if(hidden) return "";
      if(!hasFontColor()) return "";
      
      if (!Str.isEmpty(value))
      {
         if (!Str.isEmpty(font_content))
         {
            if (parseParameter(value, font_content))
               value = "<font color=\"" + font_color + "\">" + value + "</font>";
            else
               value = value.replace(font_content, "<font color=\"" + font_color + "\">" + font_content + "</font>");
         }
         else
         {
            value = "<font color=\"" + font_color + "\">" + value + "</font>";
         }
      }
      return value;
   }
   
   public ASPField setFontProperty( String fontContent, String fontColor )
   {
      try
      {
         modifyingMutableAttribute("FONT_PROPERTY");
         if( font_property==null )
            font_property = getASPManager().getFactory().getBuffer();
         font_property.setItem(fontContent, fontColor);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public String getFontProperty( String fontContent )
   {
      try
      {
         return font_property==null ? null : font_property.getString(fontContent, null);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return null;
   }
   
   public boolean hasFontProperty()
   {
      return (!(font_property==null));
   }
   
   public String getFontPropertyTag(String value)
   {
      if(hidden) return "";
      if(!hasFontProperty()) return "";
      
      if (!Str.isEmpty(value))
      {
         String store_unparse = "";
         for( int i = 0; i < font_property.countItems(); i++ )
         {
            Item item = font_property.getItem(i);
            if (parseParameter(value, item.getName()))
            {
               value = "<font color=\"" + item.getString() + "\">" + value + "</font>";
            }
            else
               store_unparse = store_unparse + i + ";";
         }
         
         if (!Str.isEmpty(store_unparse))
         {
            StringTokenizer st = new StringTokenizer(store_unparse, ";");
            int no_of_unparse = st.countTokens();
            for (int i = 0; i < no_of_unparse; i++)
            {
               int index = Integer.parseInt(st.nextToken());
               Item item = font_property.getItem(index);
               value = value.replace(item.getName(), "<font color=\"" + item.getString() + "\">" + item.getName() + "</font>");
            }
         }
      }
      return value;
   }
   
   private boolean match(String input, String regExp)
   {
      String tempRegExp = regExp.replaceAll("\\%", ".\\*");
      String finalRegExp = tempRegExp.replaceAll("_", ".");
      return input.matches(finalRegExp);
   }
   
   private boolean parseParameter(String value, String parameter)
   {
      int from_, to_, pos_;
      String value_, from_value_, to_value_, parm_list_;
      
      if (Str.isEmpty(parameter) || Str.isEmpty(value))
         return true;
      else
         parm_list_ = parameter + ";";
      
      try
      {
         from_ = 0;
         to_ = parm_list_.indexOf(";", from_);
         while (to_ >= 0)
         {
            value_ = parm_list_.substring(from_, to_ - from_).trim();
            pos_ = value_.indexOf("..");
            if (pos_ >= 0)
            {
               if (value_.indexOf("%") >= 0 || value_.indexOf("_") >= 0)
                  return false;
               else
               {
                  from_value_ = value_.substring(0, pos_ - 1).trim();
                  to_value_ = value_.substring(pos_ + 2).trim();
                  if (from_value_.compareTo(to_value_) <= 0)
                     if (value.compareTo(from_value_) >= 0 && value.compareTo(to_value_) <= 0)
                        return true;
                  else
                     if (value.compareTo(from_value_) <= 0 && value.compareTo(to_value_) >= 0)
                        return true;
               }
            }
            else if (value_.indexOf("%") >= 0 || value_.indexOf("_") >= 0)
            {
               if (value_.indexOf("..") >= 0)
                  return false;
               else
                  return match(value, value_);
            }
            else if ("<=".equals(value_.substring(0, 2)))
            {
               if (value.compareTo(value_.substring(3).trim()) <= 0)
                  return true;
            }
            else if (">=".equals(value_.substring(0, 2)))
            {
               if (value.compareTo(value_.substring(3).trim()) >= 0)
                  return true;
            }
            else if ("!=".equals(value_.substring(0, 2)))
            {
               if (!value.equals(value_.substring(3).trim()))
                  return true;
            }
            else if ("<".equals(value_.substring(0, 1)))
            {
               if (value.compareTo(value_.substring(2).trim()) < 0)
                  return true;
            }
            else if (">".equals(value_.substring(0, 1)))
            {
               if (value.compareTo(value_.substring(2).trim()) > 0)
                  return true;
            }
            else
            {
               if (value.equals(value_))
                  return true;
            }
            
            from_ = to_ + 1;
            to_ = parm_list_.indexOf(";", from_);
         }
         return false;
      }
      catch (Exception e)
      {
         return false;
      }
   }
   
   // Conditional Mandatory property of field
   public ASPField setConditionalMandatory( String values, String fields )
   {
      try
      {
         modifyingMutableAttribute("CONDITION_MANDATORY");
         if( validate_field_ofcm == null )
            validate_field_ofcm = getASPManager().getFactory().getBuffer();
         validate_field_ofcm.setItem(values, fields);
         if (!Str.isEmpty(fields))
         {
            StringTokenizer st = new StringTokenizer(fields, ";, \t\n\r");
            while( st.hasMoreTokens() )
            {
               ASPField field = getASPPage().getASPField(st.nextToken());
               if( field.condition_mandatory == null )
                  field.condition_mandatory = getASPManager().getFactory().getBuffer();
               field.condition_mandatory.setItem(this.getName(), values);
            }
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public String getConditionalMandatory( String field_name )
   {
      try
      {
         return condition_mandatory==null ? null : condition_mandatory.getString(field_name, null);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return null;
   }
   
   public Buffer getConditionalMandatory()
   {
      try
      {
         return condition_mandatory==null ? null : condition_mandatory;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return null;
   }
   
   public Buffer getValidateFieldOfcm()
   {
      try
      {
         return validate_field_ofcm==null ? null : validate_field_ofcm;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return null;
   }
   
   public boolean hasValidateFieldOfcm()
   {
      if (validate_field_ofcm != null && validate_field_ofcm.countItems() > 0)
         return true;
      return false;
   }
   
   public boolean hasConditionalMandatory()
   {
      if (condition_mandatory != null && condition_mandatory.countItems() > 0)
         return true;
      return false;
   }
   
   // Set Conditional LOV
   public ASPField setConditionalLOV( String view_name, String field_name, String field_values )
   {
      return setConditionalLOV(view_name, null, field_name, field_values);
   }
   
   public ASPField setConditionalLOV( String view_name, String view_params, String field_name, String field_values )
   {
      try
      {
         modifyingMutableAttribute("CONDITIONAL_LOV");
         if( conditional_lov==null )
            conditional_lov = getASPManager().getFactory().getBuffer();
         
         Buffer conditional_lov_values = getASPManager().getFactory().getBuffer();
         
         String in_parameters = view_params;
         if (!Str.isEmpty(view_params))
         {
            StringTokenizer params = new StringTokenizer(view_params, ",");
            String fld_name = "";
            boolean key_field_exist = false;

            while(params.hasMoreTokens())
            {
               fld_name = params.nextToken();
               if((fld_name.trim()).equals(this.getName()))
               {
                  key_field_exist = true;
                  break;
               }
            }

            if (!key_field_exist)
               in_parameters = this.getName() + "," + in_parameters;
         }
         else
            in_parameters = this.getName();
         
         conditional_lov_values.setItem("VIEW_PARAMS", in_parameters);
         conditional_lov_values.setItem("FIELD_NAME", field_name);
         conditional_lov_values.setItem("FIELD_VALUES", field_values);
         
         int i = conditional_lov.getItemPosition(view_name);
         if (i < 0)
            conditional_lov.addItem(view_name, conditional_lov_values);
         else
            conditional_lov.getItem(i).setValue(conditional_lov_values);
      }
      catch( Throwable any )
      {
         error(any);
      }
      return this;
   }
   
   public Buffer getConditionalLOV()
   {
      try
      {
         return conditional_lov==null ? null : conditional_lov;
      }
      catch( Throwable any )
      {
         error(any);
      }
      return null;
   }
   
   public boolean hasConditionalLOV()
   {
      if (conditional_lov != null && conditional_lov.countItems() > 0)
         return true;
      return false;
   }
   
   // ===================================================================================================
   // Added end
   // ===================================================================================================
}
