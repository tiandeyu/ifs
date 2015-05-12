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
 * File        : ASPManager.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1998-Feb-09 - Created
 *    Marek D  1998-Mar-25 - Access to MTS component, Error/Info messages
 *    Marek D  1998-Apr-05 - Added generation of hidden fields
 *    Jacek P  1998-May-11 - Added creation of ASPContext instance
 *    Jacek P  1998-May-14 - getDb/CurrentState() moved to ASPContext.
 *                           Log id 31: Implement a translate() method
 *                                      for run time translations.
 *    Marek D  1998-May-19 - Introduced submit(), perform() and validate()
 *    Jacek P  1998-May-25 - Added methods readvalues() and registerASPObject()
 *    Marek D  1998-May-26 - submit() generates block commands
 *    Marek D  1998-May-26 - Added global routines for ASPCommandBar
 *    Marek D  1998-May-28 - Added Status Line routines
 *    Marek D  1998-Jun-01 - Added method newASPObject(String)
 *    Jacek P  1998-Jun-04 - Added methods for redirections to other URL:s
 *    Micke A  1998-Jun-05 - Added handling of the ASPForm class
 *    Jacek P  1998-Jun-05 - Added methods for handling of URL.
 *    Marek D  1998-Jun-15 - Added generation of submitForm() function
 *                           Added check for duplicate block names
 *                           readValue() returns: 1. QueryString 2. Form
 *    Marek D  1998-Jun-25 - Added method getASPTable(ASPBlock)
 *    Marek D  1998-Jun-29 - Declared callMTS() as protected
 *                           Added getQueryString(String),
 *                                 redirectTo(url,param_name,ASPObject)
 *    Marek D  1998-Jul-03 - Declared setUserId__() as private
 *                           Call setUserId__() for config parameter WEB_USER
 *                           Added method getProtocol()
 *                           getUserId() returns AUTH_USER (not REMOTE_USER)
 *    Marek D  1998-Jul-07 - Added pack()/unpack() methods
 *                           Introduced commandLink concept
 *                           Added methods for Data Transfer
 *    Marek D  1998-Jul-09 - Added generation of JavaScript function showHelp()
 *    Marek D  1998-Jul-14 - validate() can format application errors
 *                           Create NumberFormatters using Language Code
 *    Jacek P  1998-Jul-15 - Added call to ASPTable.generateJavaScript()
 *    Jacek P  1998-Jul-17 - New way to create an instance of ASPConfig class
 *                           (see Log id: #78). Added method reloadConfig().
 *    Jacek P  1998-Jul-20 - Added verification of application path (Log id: #79)
 *    Jacek P  1998-Jul-22 - New exception handling in callMTS()
 *    Jacek P  1998-Jul-29 - Added try..catch block to each public function
 *    Tomas W/
 *    Jacek P  1998-Jul-31 - Added function getASPNavigator()
 *    Jacek P  1998-Aug-03 - Better navigator functionality
 *    Marek D  1998-Aug-04 - JScript function checkMandatory_() now perform even
 *                           validation of combo boxes
 *    Jacek P  1998-Aug-04 - Added functions for SMS call
 *    Jacek P  1998-Aug-17 - New JavaScript function showNewBrowser() for
 *                           handling of hyperlinks in a new browser (log id #101)
 *    Marek D  1998-Aug-18 - Added method showAlert(msg)
 *                           Show standard info messeges after varje request
 *    Jacek P  1998-Aug-20 - Changes due to redesigned structure of
 *                           the ASPConfig.ifm file (Log id:#2623).
 *                           Implement function getLanguageCode().
 *    Marek D  1998-Aug-26 - Added new function preapareURL() - (Bug #2653)
 *    Jacek P  1998-Aug-28 - Better error message on mismatch of application
 *                           definitions in the config file.
 *                           Added getVersion().
 *    Jacek P  1998-Sep-01 - Modified syntax of DIC files.
 *    Jacek P  1998-Sep-02 - Function checkGlobalURL() ignores case of URL's
 *    Jacek P  1998-Sep-03 - Added call to translate() function in
 *                           generateHTMLFields() for translation of alert
 *                           message.
 *    Jacek P  1998-Sep-04 - Bug #2676: Case sensitive URL:s
 *    Marek D  1998-Sep-16 - Bug #2692: Generate new JavaScript function
 *                                      resetReadOnlyField()
 *    Marek D  1998-Sep-18 - Added replace() method - utility for substitution
 *                           of Strings.
 *    Marek D  1998-Sep-24 - JavaScript function resetReadOnlyField() replaced
 *                           with checkReadOnly_()
 *    Marek D  1998-Sep-28 - Bug #2693: Added method createSearchURL()
 *                               and JavaScript function loadFavoriteUrl()
 *                           Added method newASPInfoServices()
 *    Marek D  1998-Sep-29 - Catch AbortException in validate()
 *                           Added global JavaScript variable f = focument.form
 *                           Added package function getFieldCount()
 *                           Use field label in missing-value error message
 *    Jacek P  1998-Oct-01 - Use parameters from ASPConfig.ifm while creating
 *                           new browser window and help window (ToDo: #2675).
 *                           Applet size changed from 0,0 to 5,5 due to problems
 *                           on Unix.
 *                           Added protocol to default values of some config
 *                           parameters (Bug: #2673).
 *    Marek D  1998-Oct-02 - Call default.asp from loadFavoriteUrl()
 *    Jacek P  1998-Oct-12 - Added new isEmpty() function due to changes in the
 *                           new JavaVM (Todo: #2756).
 *                           New JavaScript function assignSelectBoxValue_() for
 *                           support of Select Boxes (Bug: #2707).
 *    Jacek P  1998-Oct-13 - New functions set/clearLanguageContext() for support
 *                           of translations of include files (Bug: #2772).
 *                           ASP file name converted to upper case before
 *                           searching in the dictionary file.
 *    Jacek P  1998-Oct-14 - No uppercase conversion for file name.
 *    Jacek P  1998-Oct-20 - Added check of application info in getDicItem().
 *    Jacek P  1998-Oct-20 - Function createSearchURL() sets search URL
 *                           directly in the corresponding command bar (Todo: #2811).
 *    Jacek P  1998-Oct-21 - Changed release id to 2.2.0
 *    Marek D  1998-Nov-16 - Included port number in APPLICATION/DOMAIN
 *    Marek D  1998-Nov-18 - Clear temporary fields in onLoad() (Bug #2932)
 *    Jacek P  1998-Nov-18 - Repeated calls of generateClientScript()
 *                           signalized to the log file (ToDo: #2850).
 *                           Changed release id to 2.2.0.A
 *    Marek D  1998-Nov-19 - Added method setInitialFocus() (ToDo #2847)
 *                           Use port nr defined in APPLICATION/DOMAIN (Bug #2927)
 *                           Added new instance variable: server_variables
 *    Jacek P  1998-Nov-20 - Added handling of 'consumed' pages, error messages
 *                           in Alert Box. New unique identifier page_id. Common
 *                           JavaScript functions moved to clientscript.js
 *                           (Todo: #2714,#2943).
 *    Marek D  1998-Nov-27 - New conversion functions format(), parse() and
 *                           readNumberValue() (ToDo #2859)
 *                           Added function nvl()
 *    Marek D  1998-Dec-02 - Renamed format/parse to formatNumber/parseNumber
 *    Jacek P  1998-Dec-07 - Module name as a part of URL and in translation key
 *                           (Bug:#2998)
 *    Jacek P  1998-Dec-08 - Debug parameters in AUDIT/__DEBUG section of
 *                           ASPConfig.ifm file (values Y/N, default N):
 *                           SHOW_COOKIES - buttons that allows showing of
 *                           cookies contents, CONFIRM_ON_RELOAD - confirm dialog
 *                           instead of alert box on reload, SHOW_PAGE_ID - shows
 *                           the hidden field __PAGE_ID.
 *                           Definition of META-tag fetched from ASPConfig.ifm.
 *    Jacek P  1998-Dec-09 - New hidden field __HEAD_TAG_GENERATED.
 *    Marek D  1998-Dec-11 - Added commands for trace statistics (ToDo #3014)
 *                           Changed version to 2.2.1
 *                           Added methods newObject() and responseWrite()
 *    Jacek P  1998-Dec-21 - Trace statistics commands changed to support the
 *                           new concept with multiple threads and CPU times.
 *    Jacek P  1999-Feb-01 - Corrected bug (Bug #3136) in function generateFormTag():
 *                           onSubmit() JavaScript function did not return.
 *    Jacek P  1999-Feb-09 - Support of double bytes characters: added call to
 *                           HTMLEncode() in translate() function (Bug #3135).
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *                           Changed default name for MTSManager in function callMTS().
 *    Jacek P  1999-Feb-11 - Added possibility to disable Trace Statistics by Registry.
 *    Jacek P  1999-Feb-17 - Removed calls to Scr class. Introduced Page Pool concept.
 *    Jacek P  1999-Mar-05 - Added call to activate() on ASPPage. Removed variables
 *                           'form' and 'managed_objects'.
 *    Jacek P  1999-Mar-15 - Added call to construct() for newly created objects.
 *    Jacek P  1999-Mar-24 - Call to HTMLEncode() in translate() function depends on
 *                           configuration parameter ADMIN/ENCODE_TRANSLATIONS
 *                           (default value "N") (Bug #3235).
 *    Jacek P  1999-Mar-31 - Removed ThreadEvent.
 *    Jacek P  1999-Apr-01 - Added methods for handling of user authorization.
 *    Jacek P  1999-Apr-28 - New API for trace statistics.
 *    Jacek P  1999-May-04 - Better error handling in onStartPage().
 *    Jacek P  1999-May-05 - Added functionality for control of HTML encoding of
 *                           translations in translate() (quick&dirty).
 *    Marek D  1999-May-11 - Don't create Request Header if already exist
 *    Jacek P  1999-May-14 - Added method setPageNonExpiring().
 *    Jacek P  1999-May-21 - Added method getASPTableFromPage() for handling of profiles.
 *    Jacek P  1999-Jun-07 - Call to ASPContext.getAuthUserCookie() replaced with
 *                           getAuthUserId() in function checkUserAuthorized().
 *                           auth_user_id moved to ASPContext. Added call to
 *                           ASPPage.forceDirty() from onEndPage().
 *    Johan S
 *    Jacek P  1999-Jun-09 - Special treatment of DUPLICATE button in function
 *                           getCommandBarFunction().
 *    Jacek P  1999-Jun-14 - Fixed compatibility bug with runtime translations of
 *                           Java texts in function 'private translate()'. Added
 *                           private logError() without translation. Better exception
 *                           catch in translation methods.
 *    Jacek P  1999-Jul-13 - Corrected limitation in logOnUser(): the function
 *                           should work with both slash and backslash.
 *    Chaminda &
 *    Mangala  1999-Jun-18 - Changed getCommandBarFunction() to suport SpredSheet functionality.
 *    Jacek P  1999-Aug-09 - Included C&M:s code with some modifications.
 *    Jacek P  1999-Aug-13 - Added new config parameter CONTENT_TYPE in getCommandBarFunction().
 *    Jacek P  1999-Aug-31 - Added new methods performConfig() and performWhileUndefined().
 *                           New check in perform() if the method is called while
 *                           the page is still in undefined state.
 *    Jacek P  1999-Sep-08 - Added JavaScript code generation for hover functionality
 *                           in function generateHeadTag() (temporary solution).
 *    Jacek P  1999-Oct-14 - Added handling of system alert (method showSystemAlert() ).
 *    Jacek P  1999-Nov-02 - Changed version to 2.2.3 Beta3.
 *    Reine A  1999-Dec-20 - Moved "OutputChannel" functionality to the class ASPOutputChannel.
 *    Jacek P  1999-Dec-20 - Portal implementation:
 *                           Variable undefined_perform and function performWhileUndefined()
 *                           moved to ASPPage. Changes in perform().
 *                           Methods newASPObject(), newASPNavigatorNode(), formatNumber(),
 *                           parseNumber(), showErrorMessage(), showInfo(), showWarning()
 *                           call methods within ASPPage.
 *                           Portal check in getASPPage(). New functions getPortalPage()
 *                           and checkPortalPage(). The new function checkPortalPage()
 *                           called in newASPBlock(), newASPTable(), newASPHTMLFormatter(),
 *                           newASPInfoServices(), newASPTabContainer(), getASPContext(),
 *                           getASPForm(), getASPNavigator(), getASPField(), getASPTable().
 *                           New callMTS() with page as input parameter.
 *    Stefan M 1999-Dec-21 - Changed OnStartPage() to support dynamic LOVs.
 *    Johan S  2000-Jan-03 - Added public method clearMTSSecurityCache().
 *    Jacek P  2000-Jan-05 - Changes in logOnUser(), logOffCurrentUser(), checkUserAuthorized()
                             and getUserId().
 *    Johan S  2000-Jan-10 - Added public method removeURLFromPool().
 *    Jacek P  2000-Jan-17 - New protected method callError() called from callMTS().
 *    Stefan M 2000-Feb-14 - Added startPresentation(), endPresentation(), generateToolBar().
 *    Johan S  2000-Feb-Gui  Implemented the funcitonality of the GUImura buttons in the
 *                           getcommandBarFunction function. Aslo added utility functions like
 *                           buildItemPopulateString().
 *    Stefan M 2000-Mar-16 - startPresentation() changed; LOV appearance improved.
 *    Jacek P  2000-Mar-20 - Added handling of dynamic definition (variable dynamic_def_key).
 *    Johan S  2000-Mar-28 - Added querySubmit(), modified commandbarFunction() at BACKWARDS/FORWARD
 *                           to handle tablebrowsing.
 *    Stefan M 2000-Mar-29 - Changed generateBodyTag() to also include page popup definitions.
 *    Jacek P  2000-Apr-01 - Added clearing of profile cache in reloadConfig().
 *                           New function createPortalPage(). Call to getConfigParameter() replaced
 *                           with cfg.getParameter() in functions generateToolbar(), startPresentation()
 *                           and endPresentation(). Added handling of portal in generateToolbar().
 *                           New hidden field for support of portal in generateHTMLFields().
 *                           Public functions (such generateHeadTag() ) must not throw any Exception -
 *                           function genHTag() changed to not throw FndException.
 *    Jacek P  2000-Apr-04 - Added functions readAndTrimFile() and writeFile().
 *    Jacek P  2000-Apr-12 - New hidden fields generated for portal support in generateHTMLFields()
 *    Jacek P  2000-Apr-18 - Added command 'update portal' in function generateToolbar()
 *    Stefan M 2000-Apr-28 - Added support for disabling the standard toolbar buttons, in generateToolbar().
 *    Jacek P  2000-May-04 - Added call to clearMTSSecurityCache() in reloadConfig().
 *                           New function setPageExpiring() and initialization of
 *                           the 'page_never_expires' variable from ASPConfig.ifm
 *    Jacek P  2000-May-12 - Corrected HTML code generation in functions
 *                           startPresentation() and generateToolbar().
 *                           Changed access of generateToolbar() to private.
 *    Stefan M 2000-May-20 - Added "Home" button to toolbar.
 *    Jacek P  2000-Jun-06 - Changed version no to Beta6. Added check of HTTP
 *                           protocol in redirectTo().
 *    Johan S  2000-Jun-14   Added runCentura(string window_name)
 *    Johan S  2000-Jul-26 - Added parseDate and formatDate.
 *    Jacek P  2000-Aug-08 - Added functions setStatusLineFromJava() (Bug id #5).
 *                           Changed version number.
 *    Jacek P  2000-Aug-28 - The getDicPageKey() function returns JAVACODE for the portal page
 *                           instead of the current URL.
 *    Johan S  2000-Sep-07 - Removed all referenced to pages_never_expire.
 *    Jacek P  2000-Oct-09 - Added empty method isPresentationObjectInstalled() (log id #463).
 *                           Changed version.
 *    Stefan M 2000-Nov-01 - Added getTranslatedImageLocation().
 *    Johan  S 2000-Nov-03 - Added getHexPicture().
 *    Piotr  Z 2000-Nov-24 - Removed navigate_pop to have always fetched actual instance
 *    Artur  K               of navigate menu.
 *                           Necessary changes for handling first and last buttons.
 *                           Changed HTML code for onmouseover and onmouseout events
 *                           for toolbar images.
 *    Jacek P  2000-Nov-28 - Generated query string parameter SEARCH completed with value
 *                           in function createSearchURL()
 *    Jacek P  2000-Sep-04 - Added support for Java pages.
 *    Jacek P  2000-Oct-30 - New Exception ResponseEndException generated from
 *                           the endResponse() function.
 *    Mangala  2000-Dec-08 - Changed writeResponse(String) for LOVs in java pages
 *    Jacek P  2001-Jan-15 - Changed parsing priority in getDicPageKey().
 *    Jacek P  2001-Jan-18 - Upgraded to latest version.
 *    Jacek P  2001-Jan-25 - Redirect throws exception for Java pages. New function redirectRequest().
 *    Jacek P  2001-Jan-29 - Changes in functions getCommandBarFunction() and buildItemPopulateString().
 *    Jacek P  2001-Jan-30 - Additional handling of ResponseEndException in OnStartPage()
 *    Jacek P  2001-Feb-05 - Caught exception ASPLog.AbortException in OnStartPage
 *    Piotr Z  2001-Feb-15 - Added handling of DB authentication in LogOnUser() method (Duy H solution)
 *    Arturk K 2001-Feb-15 - Removed GetJSPUrl() function.
 *    Piotr Z  2001-Feb-21 - Improvements in handling ResponseEndException (now public class) exception.
 *    Artur K  2001-Feb-28 - Removed contructor. All necessary parameters are defined
 *                           in OnStartPage() function, which is called from RequestHandler servlet.
 *                           It is necessary for handling run-time control of ASPManager.
 *    Stefan M 2001-Mar-02 - Changed newASPGraph() to newASPGraph(int type). Type must be defined
 *                           at creation time.
 *    Johan S  2001-Mar-02 - Fixed getPhyPath() for JRun. Fixed URLEncode.
 *    Artur K  2001-Mar-05 - Added public methods which return some information about request object.
 *    Kingsly P 2001-Mar-06 - Fixed HTMLEncode().
 *    Piotr Z  2001-Mar-05 - Fixed redirect functionality. Default destination in redirecFrom() is portal.
 *    Piotr Z  2001-Mar-06 - Some changes in cookies handling. Solved problem with loosing cookie path property
 *                           after fetching cookie from the HttpServletRequest object.
 *    Jacek P  2001-Mar-06 - Transaction manager controlled by a new config parameter
 *                           ADMIN/TRANSACTION_MANAGER. Possible values: ORB,NATIVE,SOCKET. Default NATIVE.
 *                           Changes in callMTS().
 *    Piotr Z  2001-Mar-13 - New method URLDecode(). Values of cookies are now encoded/decoded.
 *    Piotr Z  2001-Mar-17 - Real user name stored as global value. Some modification due to removing
 *                           __IFS_Application__ cookie and changing authorization cookie.
 *    Kingsly P 2001-Mar-22  Change URLEncode() and URLDecode to handle null values.
 *    Jacek P  2001-Mar-22 - Log id #660 & #661: Added handling of TM. Changes in callMTS().
 *    Jacek P  2001-Mar-23 - Log id #672: Added functions getRequestCharacterEncoding() and
 *                           getRequestBodyAsInputStream() needed for handling of posted XML documents.
 *                           Log id #643: added new version of submit, submitEx(), which will throw
 *                           a runtime ASPLog.ExtendedAbortException on error.
 *    Jacek P  2001-Mar-28 - Problem with recursive call. Some changes in exepction handling for response
 *                           related methods.
 *    Jacek P  2001-Mar-28 - getQueryString() doesn't fetch parameters only from QS.
 *                           Added getQueryStringValue() and getQueryStringValues().
 *    Jacek P  2001-Mar-30 - Corrected getQueryStringValue()
 *    Jacek P  2001-Apr-11 - Some changes in handling of real user name.
 *    Jacek P  2001-Apr-12 - Debugging of session moved from ASPContext.
 *                           Changes in removeAuthUser() (Log id #689).
 *    Chaminda O 2001-Apr-18 - Fixed bugs in showInfo() and showWarning() (Log id #693)
 *    Mangala  2001-Apr-23   Changed commandBarFunction() (Log id #695)
 *    Piotr Z  2001-Apr-25 - Additional condition for generation portal view popup menu.
 *    Jacek P  2001-Apr-26 - Added language code as load balancing group for ORB TM.
 *    Kingsly P 2001-Apr-27 - Added CASESENCETIVE at appendSearchURL().
 *    Piotr Z  2000-May-09 - Changed isPresentationObjectInstalled() method. New method initAvailableList()
 *                           for initiation the list of available pages.
 *    Jacek P  2001-May-11 - Log id #706: checkUserAuthorized() changed to protected.
 *    Artur K  2001-May-15 - Changed querySubmit() function (log id 702)
 *    Piotr Z  2001-May-16 - Added unicode support (UTF8).
 *    Jacek P  2001-May-23 - Temporary disabled functionality in isPresentationObjectInstalled().
 *    Chaminda O 2001-May-28 - Added method redirectToAbsolute() modified method redirectTo()
 *    Mangala  2001-May-28 - Log id #733: Clear Item blocks when ASPCommandBar.DUPLICATEROW pressed.
 *    Piotr Z  2001-May-30 - Changed ResponseEndException inheritance to InterruptFndException.
 *    Jacek P  2001-May-30 - Log id #709: submitEx() method doesn't throw exception.
 *    Chaminda O 2001-May-30 - Log id #726: Errors in Netscape will often show the HTML source code.
 *    Jacek P  2001-May-31 - Log id #742,749: Added HTMLDecode() and reimplemented HTMLEncode().
 *                           Call to HTMLDecode() in decodeValue(). Skip UTF-8 coding for query
 *                           string values wile reading by readValue().
 *    Piotr Z  2001-Jun-07 - Changed isPresentationObjectInstalled() and initAvailableList() methods.
 *                           New method getServlet().
 *    SlawekF  2001-Jun-13 - Changed handling of exceptions thrown by Server.invokeBuffer call, to display
 *                           sensible error message if TM or ORB could not be contacted ( see callMTS method )
 *    Ramila H 2001-Jun-14 - Log id #754. Added public method getTransferedQueryString().
 *                           returns the __TRANSFER query string.
 *    Jacek P  2001-Jun-20 - Added method getRequestContentType() and changed error handling in callError().
 *    Jacek P  2001-Jul-12 - Disconnecting ORB server in onEndPage().
 *    Jacek P  2001-Jul-24 - Removing the SOCKET TM method, implementing the pool of TM connections.
 *    Ramila H 2001-Aug-31 - Log id 796 correct. Increased DBROWS for SAVENEW and SAVERETURN.
 *    Piotr Z  2001-Aug-31 - Corrected buildit() function.
 *    Jacek P  2001-Sep-07 - Log id #799: changed parsing of URL to the logon page in checkUserAuthorized().
 *    Piotr Z  2001-Sep-12 - Modified createSearchURL() - global connection is always set.
 *    Johan S  2001-Sep-13 - Added GZIP functionality.
 *    Jacek P  2001-Sep-13 - Upgraded to Build 13 of FNDAS
 *    Jacek P  2001-Sep-20 - Added function getConnectionServer()
 *    Jacek P  2001-Oct-12 - Moved displaying of portal global variables below the header.
 *    Daniel S 2002-Jan-07 - Added function getSystemName().
 *    Ramila H 2001-Nov-23 - Added/modified functions for GZIP functionality.
 *    Upul P   2001-Nov-26 - Added ip masked filtering for GZIP functionality
 *    Daniel S 2001-Nov-26 - Made stretching images in banners. They are no longer cell background. This will
 *                           make the layout printable.
 *    Daniel S 2002-Jan-14 - Added support for fndas system. Plsqlgateway can nu run with configuration server.
 *    Jacek P  2002-Jan-22 - Changed name of TMConnectionPool class to ORBConnectionPool.
 *                           Prepared for LDAP logon - new function authorizeUserORB() (temporary?)
 *    Suneth M 2002-Jan-30 - Changed OnStartPage() to handle dynamic def key correctly.
 *    Chandana 2002-Feb-12 - Introduced ifsEncode() and ifsDecode() methods.
 *    Jacek P  2002-Feb-20 - Changes in fetchRealUserName() due to changes of handling of FND_USER if using
 *                           FND_DB method for user authentication.
 *    ChandanaD2002-Mar-13 - Re-implemented ifsEncode() & ifsDecode() methods to dynamically choose Base64 or Hex encodings
 *                           accoring to 'b64_enabled' variable.
 *    ChandanaD2002-Mar-16 - Fixed bugs in ifsEncode() & ifsDecode() methods.
 *    Mangala  2002-Mar-18 - Changes to avoid visiting history of other users in the browser.
 *    Suneth M 2002-May-10 - Changed redirectRequest() to add _CACHE variable to the url.
 *    Suneth M 2002-May-22 - Log Id 801. Added new hidden field __CASESS_VALUE.
 *    ChandanaD2002-Jun-18 - Added new public method 'generatePageMaskTag()'.
 *    ChandanaD2002-Jun-19 - Included generatePageMaskTag() in the generateBodyTag() method.
 *    Mangala  2002-Jun-28 - Cache solution is comented out.
 *    Ramila H 2002-Jul-17 - Added support for Netscape 6
 *    Chandana 2002-Jul-18 - Added new method correctURL(String url).
 *                         - Used correctURL(url) to append an ID to the URL.
 *    Chandana 2002-Jul-22 - Added generateUnauthorizedAccessPage() and  getUnauthorizedAccessPage(String mode) methods.
 *    Chandana 2002-Jul-24 - Hard-Coded unauthaccess.htm file name instead of getting it from ASPConfig.ifm
 *    Chandana 2002-Jul-30 - Added new public method isNetscape4x().
 *    Suneth M 2002-Jul-31 - Changed redirectTo() to support https protocols.
 *    Daniel S 2002-Aug-08 - Reactivated the session object and added flow support.
 *    Johan S  2002-Aug-16 - added request header tests.
 *    Chandana 2002-Aug-28 - Calls isHomeIconDisabled() before adding the Home-Link icon. Log Id 974.
 *    Daniel S 2002-Aug-29 - Reactivated the "page_never_expires" flag. Due to the new Context cache.
 *    Chandana 2002-Aug-30 - Call to requestbuf.setSystemName method.
 *    Daniel S 2002-Sep-12 - Added support for "lightversion" of manager if an jsp page.
 *    Jacek P  2002-Sep-24 - Improved error handling if ASPLog instance not found (the error() method).
 *    Chandana 2002-Oct-08 - Removed NOWRAP from the <TD> which shows the header2.jpg in order to function in NS6.
 *    Mangala  2002-Oct-09 - Minor modifications on generateToolBar().
 *    ChandanaD2002-Oct-21 - Temporary solution for NE7.x.
 *    ChandanaD2002-Oct-21 - Enables/Disables Standard Action Buttons according toASPConfig.ifm parameters in generateToolbar() method.
 *    ChandanaD2002-Oct-28 - Uses a new Style sheet for NE6 and above.
 *    Mangala  2002-Oct-30 - redirectToAbsolute now doesn't correct the URL with __ID.
 *    Ramila H 2002-Oct-30 - Log Id 982. Implemented code.
 *    Rifki R  2002-Nov-05 - Added methods translateJavaScript() and jsEncodeQuotes(). Log Id 905.
 *    ChandanaD2002-Nov-07 - Title Bar border corrected for NE6 and above.
 *    ChandanaD2002-Nov-18 - Corrected a bug existed for NE6
 *    Rifki R  2002-Noc-20 - Minor change in  translateJavaScript() esacped quotes even when there is no dictionary entry.
 *    Ramila H 2002-Nov-29 - Corrected bug in log id 982 when calling rowset store().
 *    Sampath  2002-Dec-02 - Removed spaces at values returned from readValue()
 *    Ramila H 2002-Dec-03 - Log id 1012. added methods.
 *    Rifki R  2002-Dec-05 - Added isInitialFocusSet(). Log id 990.
 *    Ramila H 2002-Dec-13 - Log id 933. Added functionality to handle save and delete queries.
 *    Suneth M 2002-Dec-30 - Log id 1002. Changed startPresentation().
 *    Mangala  2003-Jan-01 - Log id 595. Chages for common message display
 *    Rifki R  2003-Jan-09 - Added new method getFndUser().
 *    Sampath  2003-Jan-10 - made changes to endResponse() to work with iFrame
 *    Marek  D 2002-Nov-01 - RMI calls to Plsql Gateway EJB when TM method = NATIVE
 *    ChandanaD2003-Jan-22 - Log id 942. Modified fetchRealUserName() method.
 *    ChandanaD2003-Jan-22 - Added generateMessagePage() and getMessagePageURL() methods.
 *    Jacek P  2003-Jan-27 - Session invalidation moved from ASPContext. Small changes in session handling.
 *    Jacek P  2003-Feb-04 - Config directory path instead for file name send to OnStartPage()
 *    Mangala  2003-Jan-29 - Improvements for Log Id 595.
 *    ChandanaD2003-Feb-11 - Modified startPresentation() & endPresentation() methods.
 *    Jacek P  2003-Feb-21 - Improved error handling during startup. Added error message if page not found.
 *    Jacek P  2003-Feb-28 - Removed getSystemName().
 *                           Changed name of ORBConnectionPool class to JAPConnectionPool.
 *    Rifki R  2003-Mar-03 - Deprecated getPhyPath().
 *    Johan S  2003-Mar-04 - Completely rewrote the cookie handling.
 *    Ramila H 2003-Mar-06 - Added overloaded method clearMyChilds.
 *    Rifki R  2003-Mar-06 - Fixed method invalidateSession(). Session should be invalidated when 'asp_session' is not null.
 *    ChandanaD2003-Mar-17 - Added support for uploading files("multipart/form-data" type requests)
 *    Ramila H 2003-Mar-19 - Corrected spelling in getInstalledModules doc comments.
 *    ChandanaD2003-Mar-20 - Corrected a bug related to deleting an uploaded file.
 *    ChandanaD2003-Mar-24 - Corrected accessing the file path problem in file upload, when using Netscape.
 *    Ramila H 2003-Mar-24 - Corrected problem in performActionOnSelectedRows method.
 *    ChandanaD2003-Mar-25 - Added an ID to to the Ok button of the IFS_DIALOG page generation code.
 *    Sampath  2003-Apr-17 - changed generateMessagePage() to show more_info on errors
 *    Rifki R  2003-Apr-30 - Log 1042, Memory consumption problem. Changed OnEndPage(), connection is now released only
 *                          after generation of navigator and LOV.
 *    ChandanaD2003-May-22 - Modified coding for the new L&F.
 *    ChandanaD2003-May-29 - "Options" button added and "Refresh" button removed from the tool bar.
 *    Ramila H 2003-May-30 - Added config_views popup to manage portal views.
 *    ChandanaD2003-Jun-02 - Added an onClick event function to the toolbar images to place the popups in a proper place.
 *    ChandanaD2003-Jun-03 - 'Options' button in the toolbar conditionally disabled.
 *    Ramila H 2003-Jun-09 - Added CASESENCETIVE to saved query (for LOV).
 *    ChandanaD2003-Jun-24 - Inserted a missing </p> in the endPresentation().
 *    Sampath  2003-Jun-16 - The method 'jsEncodeQuotes()' is given package access.
 *    Ramila H 2003-Jun-18 - Corrected Bug 36965.
 *    Jacek P  2003-Jul-03 - Added support for level 4 proxies (Bug 38326).
 *    ChandanaD2003-Jul-15 - Encoded/Decoded ' and + characters.
 *    Sampath  2003-Jul-17 - Changed the appearance of globle variables.
 *    Ramila H 2003-Jul-18 - Log id 1119, Authentication by fndext.
 *    Ramila H 2003-Jul-26 - Log id 1080, implemented code to import portal profile from a xml file.
 *    Ramila H 2003-Jul-29 - Added refreshRow to call in performActionSelectedRows.
 *    ChandanaD2003-Aug-04 - Restricted uploadable file size.
 *    Chanaka  2003-Aug-06 - Make request, response and context members friendly for
 *                           Cryptonine changes.
 *    Ramila H 2003-Aug-14 - changed method call to query depending on fnduser or webuser in fetchRealUserName.
 *    Mangala  2003-Aug-20 - Added new public method to check authentication by modules.
 *    Ramila H 2003-Sep-04 - changed = to ^ in saveQuery field value separater.
 *    Ramila H 2003-Sep-08 - added method to return redirectFrom url.
 *    Mangala  2003-Sep-19 - Changed Copyright message.
 *    Mangala  2003-Oct-10 - Used resetBuffer (introduced in servlet 2.3 specification) to reset the response
 *                           in clearResponse().
 *    Ramila H 2003-Oct-15 - Call id 107696 fixed.
 *    Ramila H 2003-Oct-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    Mangala  2003-Dec-24 - Fixed bug 40929. Remove the word PANIC from error messages when log is missing.
 *    Jacek P  2004-Jan-04 - Bug# 40931. Better debugging possibilities of Page Pool
 *    ChandanaD2004-Jan-12 - Bug 41996 fixed.
 *    ChandanaD2004-Feb-11 - Bug 42662 fixed.
 *    ChandanaD2004-Feb-16 - Bug 40353 fixed.
 *    Rifki R  2004-Feb-16 - Bug 40903, new context algorithm. setFlowId(), getFlowId() and newFlowID() are now obsolete.
 *    Mangala  2004-Mar-04 - Bug 43191, Changed version to 3.6.0 CPS 1
 *    ChandanaD2004-Mar-24 - Changed version to 3.7.0 Package 4.
 *    Jacek P  2004-Apr-20 - First synchronization with Montgomery project
 *    Suneth M 2004-Apr-28 - Bug 43591, Changed getCommandBarFunction().
 *    Jacek P  2004-May-07 - Some improvments in debugging. Support for new functionality in WebClientAdmin page.
 *                           Adding usage of the new Alert file.
 *                           Replacing deprecated API in java.net.URLEncoder and java.net.URLDecoder.
 *   Chandana D2004-May-12 - Removed all dependencies with the SCHEME section of the webclientconfig.xml.
 *   Chandana D2004-May-19 - Added public method isMozilla(). Changed calls to isNetscape6() to isMozilla().
 *    Suneth M 2004-May-26 - Merged Bug 44759, Changed logOnUser().
 *    Ramila H 2004-May-26  - Implemented code for multi-language support
 *    Ramila H 2004-May-27  - set default_language_code to en if default_language entry not found in webclientconfig
 *    Ramila H 2004-May-28  - Bug 42390, Removed group separator from mask depending on compatible application
 *    Chandana 2004-Jun-10 - Removed all absolute URLs. Added getSite(), getModule(), getProtocolDomain() and getAbsoluteURL() private methods.
 *    Chandana 2004-Jun-11 - Made getProtocolDomain() friendly as reqiured to access from ASPPage.
 *    Mangala  2004-Jun-17 - Merged with bug 44656.
 *    Rifki R  2004-Jun-17 - Merged Bug 43530, Changed printCustomBody().
 *    Chandana 2004-Jun-23 - Disabled GZIP for validations and when exporting the navigator.
 *    Rifki  R 2004-Jun-29 - modified isPresentationObjectInstalled() to support web client pages
 *    Ramila H 2004-Jun-29  - changed html generation imp of unauthorized access page to support multi-language
 *    Mangala  2004-Jul-12  - changed checkUserAuthorized() to give proper message when doing validation
 *                           while the session has expired. Project link call 115295.
 *    Chandana 2004-Jul-23 - Removed the usage of ALT_DOMAIN in fetchUserPreferredLanguage() and checkUserAuthorized()
 *    Chandana 2004-Jul-27 - Further improvements to work with proxy servers.
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 *    Suneth M 2004-Aug-13 - Merged Bug 45335, Changed readValue() to avoid trimming the returned value.
 *    Ramila H 2004-08-24  - Impl multi language support for stylesheet tag.
 *    Ramila H 2004-09-07  - Added lang_code suffix to poolkey using ASPPagePool.getPoolKeyLangSuffix.
 *    Chandana 2004-Sep-10 - Added support for Block Layout Profiles.
 *    Chandana 2004-Sep-10 - Fixed a bug in getAbsoluteURL and getModule method.
 *    Chandana 2004-Sep-14 - Reversed changes done for Block Layout Profiles.
 *    Mangala  2004-Sep-14 - Added new method performEx.
 *    Ramila H 2004-10-18  - Implemented JSR168 support.
 *    Ramila H 2004-10-21  - Made improvements to changing language code by using query string value.
 *    Chandana 2004-Oct-22 - Fixed a bug in getAbsoluteURL().
 *    Ramila H 2004-11-03  - Added method getAlertMsg to show alerts in standard portal mode.
 *    Chandana 2004-Nov-05 - Added support for Block Layout Profiles. Added new method getASPBlockLayoutFromPage().
 *    Mangala  2004-Nov-09 - Changes done for SSO.
 *    Chandana 2004-Nov-10 - Introduced new APIs to support Activity API calls.
 *    Jacek P  2004-Nov-11 - Introduced single instance of ConnectionPool
 *    Jacek P  2006-Feb-21 - Added methods getProfileCacheSize() and getLocalizationSizes() for statistics purpose.

 * -----------------------------------------------------------------------------------------------------------
 * New Comments:
 * 2010/10/28 sumelk Bug 93787, Changed version to CPS7.      
 * 2010/09/21 buhilk Bug 93074, Changed drawContextSensitiveTaskPane() to count todo items for lu on every request.
 * 2010/09/02 sumelk Bug 92787, Changed getFndUser() to execute the method correctly when page is undefined.
 * 2010/06/01 sumelk Bug 90546, Added isInactivePortalViewsDisabled() & changed appendPortalTabs() for enable the possibility 
 *                   to remove inactive portal views when open the portal page via IFS EE shortcuts.
 * 2010/06/01 sumelk Bug 91058, Changed version to CPS6.     
 * 2010/04/20 amiklk Bug 89905, Added isValidSysdateValue() to check for valid date values with sysdate.
 * 09/04/2010 rahelk bug 89989, added auth cookie to prevent weblogic from view back pages after logging out
 * 2010/03/26 sumelk Bug 89672, Changed getCommandBarFunction() to enable first and last icons in multirow layout. 
 * 2010/03/04 buhilk Bug 88106, support for Federated search through windows-7 UI
 * 2010/03/04 sumelk Bug 89326, Changed appendPortalTabs() to freeze the portal page while the active tab is loading. 
 * 2010/02/03 sumelk Bug 88795, Changed fetchRealUserName(), fetchStdPortalLanguage(), OnStartPage(), fetchLanguageFromCookie() 
 *                   fetchLanguageFromUserProfile() & getDefaultLanguage().
 * 2009/12/17 sumelk Bug 87808, Changed version to CPS5.    
 * 2009/12/16 sumelk Bug 87809, Changed appendMobileLeftHeader() to write absolute logout URL. 
 * 2009/10/07 amiklk Bug 85955, Changed toSqlDateMask() to convert MMMM and MMM to server date format.
 * 2009/09/29 sumelk Bug 85807, Changed processMultipartRequest() to handle the uploaded file type.
 * 2009/08/13 buhilk Bug 85018, Changed fetchRealUserName() to throw error when not real user found.
 * 2009/07/17 amiklk Bug 84844, changed generateHTMLFields() to check mobile version, getMobileScriptFileTag() to include clientscript.js
 * 2009/07/14 buhilk Bug 84808, Changed appendSearchURL() to write SEARCHPARAM values to context.
 * 2009/07/09 sumelk Bug 83794, Changed appendSearchURL() to set the value of SEARCHPARAM to "" instead of null.
 * 2009/05/28 sumelk Bug 83128, Changed version to CPS4.   
 * 2009/05/22 sumelk Bug 82893, Modified getCommandBarFunction() to enable next/previous icons correctly in single row mode.
 * 2009/04/02 buhilk Bug 81782, Modified getCommandBarFunction() DELETE section to browse to next/previous db set if all rows were deleted..
 * 2009/03/13 buhilk Bug 81340, Modified appendSearchURL to save links with transfered data.
 * 2009/03/11 sumelk Bug 80503, Modified appendLeftHeader to enable sign out link for enternally identified authentication. 
 * 2009/03/06 amiklk Bug 80874, Modified processMultipartRequest() to avoid writing extra trialing letters to the file/blob.
 * 2009/03/04 buhilk Bug 81107, Modified readValue() and added hasCSVNames().
 * 2009/02/13 buhilk - Bug id 80265, F1PR454 - Templates IID.
 * 2009/01/15 buhilk Bug 79770, Modified processMultipartRequest() to save filepath when installed on AIX environments.;
 * 2008/12/23 sumelk Bug 79480, Moved some file upload functionality to FileUploadPage.java file.
 * 2008/12/23 dusdlk Bug 78562, Added function getUserLocale().
 * 2008/12/18 sumelk Bug 78730, Changed processMultipartRequest() to avoid the garbled filename when uploading files.
 * 2008/11/25 buhilk Bug 78166, Added checkBrowser() and moddified isMobileVersion();
 * 2008/10/23 sumelk Bug 77634, Changed version to CPS3.  
 * 2008/10/20 sumelk Bug 77802, Modified getCommandBarFunction().
 * 2008/10/08 buhilk Bug id 77648, Modified responseWrite() to update context only if requested.
 * 2008/09/24 dusdlk Bug id 77095, Modified isPresentationObjectInstalled( String pres_obj_id ) to check for the index of ".page" in pres_obj_id.
 * 2008/09/23 rahelk Bug id 76831, Reverted to fndext counting the rows if VIEW and WHERE condition not properly known.
 * 2008/09/11 buhilk Bug id 76970, Modified OnStartPage() to correct FireFox redirection error for MacOS users
 * 2008/09/02 buhilk Bug id 76759, Modified getUserThemeID() read Theme from new common profile location.
 * 2008/08/15 buhilk Bug id 76288, Modified OKFIND & COUNTFIND in getCommandBarFunction() as well as startPresentation()
 *                                 to append page related tasks link. Added drawContextSensitiveTaskPane() and getMasterBlock().
 *                                 Modifed generateHTMLFields() to generate "__BUSSINESS_OBJECT" hidden field.
 *                                 Removed getMasterQueryRecord() and added getConditions() in its place.
 * 2008/08/12 buhilk Bug id 76287, Added isAuraraFeaturesEnabled(): Global switch to enable/disable Aurora features
 *                                 Modified isRWCHost(), showRWCLinksInBrowser() and showRWCNavigatorItems()
 * 2008/08/11 rahelk Bug id 74484, Added callQuery method to add __COUNT buffer to request buffer to find row count in query.
 * 2008/08/05 buhilk Bug id 76154, AEE & web integration changes. Added functionlity to add js code to AEE interface.
 * 2008/08/01 rahelk Bug id 74809, handling of tiff viewer instance count
 * 2008/07/24 dusdlk Bug 74480, Modified the appendPortalTabs(AutoString bar) function, added an IF condition to check if its a new window and to pass __NEWWIN as a URL parameter and also updated the call method for generateClientScript() in responseWrite(String text) 
 * 2008/07/01 buhilk Bug 74852, Modifed getCommandBarFunction() and getSaveRecord()
 * 2008/06/26 mapelk Bug 74852, Programming Model for Activities. 
 * 2008/06/13 buhilk Bug 69812, Modified appendApplicationSearch() to read the opensearch xml from dynacache.
 * 2008/05/26 sadhlk Bug 74327, Added getDummyImage() and modified appendRightHeader() and appendApplicationSearch() to correct
 *                              portal view creation error.
 * 2008/05/13 sumelk Bug 73877, Changed version to CPS2. 
 * 2008/05/07 buhilk Bug 73731, Removed "rwcPageHeaderTable" css style when header is disabled in browsers.
 * 2008/05/02 sadhlk Bug 73595, Modified appendRightHeader() and appendApplicationSearch() to avoid empty src values.
 * 2008/05/02 sadhlk Bug 72853, Modified correctURL() to provide single sign-on
 *                              functionality in Aurora Explorer.
 * 2008/04/21 madrse Bug 69076, Added Server functionality needed by new Aurora client
 * 2008/04/21 buhilk Bug 72855, Added new APIs to support RWC interfacing.
 * 2008/04/04 buhilk Bug 72854, Added showRWCLinksInBrowser() and showRWCNavigatorItems() to enable RWC integration support..
 * 2008/04/04 buhilk Bug 72852, Added isRWCHost() and Made changes to html code to hide header when in RWC.
 * 2008/01/30 sumelk Bug 70915, Changed version to CPS1.
 * 2008/01/29 vohelk Bug id 69330, Added new method writeContentToBrowser(byte[] data, String content_type, String file_name)
 * 2008/01/11 sadhlk Bug id 70489, Added getDummyPage() to get the path+filename of the dummy page.
 * 2007/12/20 buhilk Bug id 70052, Framework mobile pages url pattern was mapped to "secured/webmobile".
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/11/08 rahelk Bug id 68906, Added USER_COMMENT attribute to DECISION buffer
 * 2007/11/01 sadhlk Merged Bug id 68311, Added getCookiePrefix() and isStandardChar() to support non-alaphabetical
 *                                 Characters in usernames.
 * 2007/10/17 sadhlk Merged Bug id 67865, Modified generateHTMLFields() and Added IS_PAGE_EXPIRED constant.
 * 2007/09/11 rahelk Bug id 67734, Merged.Populated item blks when application search
 * 2007/07/26 buhilk bug id 66875, Modified appendPortalTabs() to expand clickable area.
 * 2007/07/16 sadhlk bug id 66470, Modified querySubmit().
 * 2007/07/10 buhilk bug id 66858, Modified onStartPage() and added isRequestbyGET().
 * 2007/07/03 sadhlk Merged Bug id 64669, Modified appendApplicationSearch(), appendLeftHeader() and saveQuery().
 * 2007/07/02 sadhlk Merged Bug 64254, Added setScrollPosition() to set current scroll position.
 * 2007/06/29 buhilk IID F1PR497, Modified appendApplicationSearch()
 * 2007/06/29 buhilk Bug 66378, Modified getQueryString(), scanQueryString(), OnStartPage() and added setQueryStringAtLogon(), getQueryStringAtLogon()
 * 2007/05/30 buhilk Bug 65715, Modified appendRightHeader() to change correctPNGforIE() into setImageType() to fix IE6 crashing error.
 * 2007/05/28 rahelk Merged Bug id 63254.
 * 2007/05/22 sumelk Merged the corrections for Bug 65469, Changed getUserId() to obtain the authentication
 *                   header name from configuration files.
 * 2007/05/16 rahelk Call id 143888, fixed proxy related bugs
 * 2007/05/10 rahelk moved application search script down to avoid IE7 script error
 * 2007/05/04 buhilk Bug id 65098, Broadcast message functionality was improved.
 *            Renamed and modified appendBroadcastMessages().
 * 2007/04/11 sadhlk Merged Bug 64359, Modified appendLeftHeader() to show top left header correctly.
 * 2007/04/06 mapelk Depricated image generation methods
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * 2007/03/29 mapelk altered PNG corrections to compatible with new header images.
 * 2007/03/06 buhilk Bug id 64026, NullPointerException in startPresentation() when null title is found
 * 2007/03/06 rahelk Bug id 64000, added empty image when no common msgs.
 * 2007/03/06 rahelk Bug id 63987, passed directoryId instead of fnduser to application search activity handler
 * 2007/03/06 buhilk Bug id 63950, Modified appendApplicationSearch(), appendRightHeader() and startPresentation() to improve GUI.
 * 2007/03/06 sadhlk Bug id 63942, Modified appendCommonMessages().
 * 2007/02/20 rahelk Bug id 63874, spanned common msgs to use entire width when application search not available
 * 2007/02/27 buhilk Bug id 63806, Fixed error stach print on OAS due to JAAS request
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * 2007/02/19 sumelk Bug 63612, Changed version to CPS3.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2007/01/08 sadhlk Bug id 62477, Added sendFile(), sendTiffFile() and readFile() to write Tiff
 *                                 files to the browser.
 * 2006/12/22 sumelk Bug 62620, Moved methods toSqlDateMask() & correctSingleCharFormat() from ASPQuery.
 * 2006/12/20 buhilk Bug id 62472, Modified getASPTableFromPage() to return null when no table is available.
 * 2006/12/15 sadhlk Bug id 62442, Modified canGZIP().
 * 2006/12/08 buhilk Bug id 61535, Modified the method generateToolbar() as the
 *                                 common messages were moved into the DataBase
 * 2006/12/05 rahelk Bug id 60629, Returned -1 when "*" in allowed_host name
 * 2006/11/27 sadhlk Bug Id 61898, Changed encodeCharForJavascript() to fix XSS issue.
 * 2006/11/21 mapelk Bug Id 61977, Changed Authentication plug-in to send HttpServletRequest.
 * 2006/11/09 sadhlk Bug id 61421, Changed the MAX_TAB_LENGTH value
 * 2006/09/29 gegulk Bug id 58618, Modified the class to enable loading the translations from the Database
 * Added the methods getModuleList() & loadTranslations(),
 * Modified the methods onStartPage(),getDicItem() & translateJavascript()
 *
 * 2006/09/27 sumelk Bug 60831, Changed version to CPS2.
 * 2006/09/26 sumelk Bug 60783, Changed fetchRealUserName() & fetchLanguageFromUserProfile().
 * 2006/09/24 mapelk Bug Id 59842, Improved CSV code
 * 2006/09/21 buhilk Bug Id 59842, Modified createPortalPage() so that the CSV cache is initialized before
 *                   the portal page run() method executes
 * 2006/09/18 buhilk Bug Id 59842, added modified readValue(), getQueryStringValue(), saveQuery(), createSearchURL()
 *                   and added new methods called readAbsoluteValue() as well as
 *                   the Context Substitution Variable Section
 * 2006/09/13 gegulk Bug id 60473, Added the method isRTL() to check the direction of the page and
 *                   Modified endPresentation() to set the alignment of the ifs logo accordingly
 * 2006/08/19 madrse Bug id 60208, In proxy-user mode perform() calls in RMI mode are delegated directly to JSF class PlsqlProxy (not via a bean)
 * 2006/08/18 gegulk Bug id 59985, Removed the usages of the word "enum" as variable names
 *
 * 2006/08/01 rahelk Bug id 59663, Removed unnecessary handling of portal user in standard mode
 *
 * 2006/06/30 buhilk Bug 58216, Fixed by removing SQL Injection threats
 *
 * 2006/05/17 riralk Bug 57749, Terms related improvements in web client. Added methods getCodePartLabels() and addCodePartLabels()
 *                              to show correct code part labels in "Help on Fields".
 * 2006/05/16 rahelk Bug 57013, overloaded generateHeadTag method to accept charset
 * 2006/05/16 mapelk Improved "What's This?" functionality to show help as a tool tip
 * 2006/05/04 mapelk Bug 57016, support currency format in readNumberValue.
 * 2006/05/04 sumelk Bug 56029, Changed getUserId() to provide support for WebSeal.
 * 2006/04/26 prralk Bug 57027 issues with 'help on fields'. Moved translation keys defined out
 *                   side predefine to session. Added support to handle multi frame pages.
 * 2006/04/24 prralk Bug 45230 fix for errors caused by page refresh
 *                2006/04/20 prralk
 * Bug 57417 performance issue with default language extraction
 *                2006/04/20 prralk
 * Bug 57026 'Help On Fields' display issues
 *                2006/04/20           sumelk
 * Bug 55966, Changed reloadProfileCache() as a static method.
 *                2006/03/15           prralk
 * XSS vulnerability, encode javascript string before writing to HTML page
 *                2006/02/20           prralk
 * Removed calls to deprecated functions
 *                2006/02/16           prralk
 * B130888 - Remove row number from showing in error messages when multirow delete.
 *                2006/01/31           mapelk
 * B131046 - remove currency sign from pattern and trim
 *                2006/01/31           prralk
 * B131326 - New window with & sign in querry string does not function correctly.
 *                2006/01/12           mapelk
 * corrected readNumberValue to use regional settings from the profile
 *                2006/01/12           prralk
 * if Logon page exclude hover script.
 *                2006/01/10           mapelk
 * returns null from setNumericFormatMask if the group size is null.
 *                2006/01/09           riralk
 * Replaced 'TreeSet' vars with 'Set' vars for "Help on Fields" functionality. Bug fix in getDicItem() to make sure usage_key always gets a value.
 *
 *                2006/01/09           mapelk
 * Improved regional settings support in the profile
 *
 * Revision       2005/12/19           rahelk
 * Set the changed language to the user's profile in fetchRealUserName method
 *
 * Revision 1.28  2005/12/07           prralk
 * Corrected bug: added decoding for &_COPY
 *
 * Revision 1.27  2005/11/24           mapelk
 * Corrected bug: Reading mask from profile raise an error in the portal page
 *
 * Revision 1.27  2005/11/24 14:10:53  rahelk
 * retrieved FNDUSER in fetchRealUserName to get user's profile language
 *
 * Revision 1.26  2005/11/23 14:49:53  rahelk
 * Changed check asppage.isDefined() to asppage.isUndefined() for calling performConfig
 *
 * Revision 1.25  2005/11/17 14:38:53  japase
 * Added function runGC() to be executed from WebClientAdmin page
 *
 * Revision 1.24  2005/11/17 07:44:30  rahelk
 * Considered user's langauge in profile for user_preferred_language
 *
 * Revision 1.23  2005/11/16 14:17:51  japase
 * Added method getRFCLanguageCode()
 *
 * Revision 1.22  2005/11/11 10:09:10  rahelk
 * move method writeContentToBrowser from ASPInfoServices
 *
 * Revision 1.21  2005/11/11 10:01:37  rahelk
 * fixed bug with writing output channel content to browser
 *
 * Revision 1.20  2005/11/10 10:21:07  rahelk
 * changes related to usages
 *
 * Revision 1.19  2005/11/09 13:59:39  mapelk
 * Added New Doc comments. Also changed the version to Package 19.
 *
 * Revision 1.18  2005/11/08 07:50:54  rahelk
 * core changes for using USAGES in help
 *
 * Revision 1.17  2005/11/07 08:16:27  mapelk
 * Introduced "persistant" att to Dynamic Objects and remove non persistent objects from the DynamicObjectCache in the first get.
 *
 * Revision 1.16  2005/11/06 08:47:06  mapelk
 * Header/Footer Stretch image size no longer taken from Java
 *
 * Revision 1.15  2005/11/06 05:46:56  mapelk
 * Introduced posibility to trace Dynamic Object cache from WebClientAdmin
 *
 * Revision 1.14  2005/11/04 12:30:00  japase
 * Introduced possibility for logging used browser versions.
 *
 * Revision 1.13  2005/11/01 08:10:57  mapelk
 * Removed Unnecessory debugs
 *
 * Revision 1.12  2005/10/25 11:06:14  mapelk
 * Introduced different validations for Number & Money. Also replaces ASCII 160 with 32 which returns as group seperator for some languages.
 *
 * Revision 1.11  2005/10/20 06:00:32  mapelk
 * Removed method ASPPage.existsASPPopup and it's usage
 *
 * Revision 1.10  2005/10/19 10:16:32  mapelk
 * Added functionality to refresh profile cache in certain time. Also posibility to flush the cache on demand.
 *
 * Revision 1.9  2005/10/19 03:17:46  mapelk
 * changed setResponseContentFileName() to support long file names and make HttpServletRequest and HttpServletResponse protected.
 *
 * Revision 1.8  2005/10/14 09:08:13  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.7  2005/10/13 04:27:21  rahelk
 * considered language in request header for defaultLanguage
 *
 * Revision 1.6  2005/09/29 13:55:33  japase
 * Changes in createPortalPage() due to changed handling of ASPPopup
 *
 * Revision 1.5  2005/09/28 12:57:26  japase
 * Keep track of the current page for better error handling. Small justifications in getUserID().
 *
 * Revision 1.4  2005/09/27 08:46:24  sumelk
 * Merged the corrections for Bug 52960, Changed hexEncode() & ifsEncode() to encode percentage sign.
 *
 * Revision 1.3  2005/09/22 12:39:22  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.2  2005/09/22 08:09:13  sumelk
 * Merged the corrections for Bug 51139, Changed saveQuery().
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.32  2005/09/14 08:02:07  rahelk
 * password management
 *
 * Revision 1.31  2005/08/26 12:33:29  riralk
 * Modified getFormatMask() to retrieve format masks from Java Locale when not defined in webclientconfig.xml.
 *
 * Revision 1.30.2.3  2005/09/21 12:40:15  mapelk
 * Fixed bug: RMBs added just after seperator dissapears.
 *
 * Revision 1.30.2.2  2005/09/02 09:36:38  mapelk
 * Dissable formating errors in preformEx
 *
 * Revision 1.30.2.1  2005/08/30 08:35:24  rahelk
 * fixed bug in getAbsoluteURL
 *
 * Revision 1.30  2005/08/17 10:46:24  rahelk
 * JAAS AS specific security plugin
 *
 * Revision 1.29  2005/08/17 07:53:19  mapelk
 * Version changed package 16
 *
 * Revision 1.28  2005/08/11 11:40:00  riralk
 * Overloaded setResponseContentFileName() to specify attachment in content disposition
 *
 * Revision 1.27  2005/08/11 09:39:36  riralk
 * Fixed error in getExceptionlessApplicationPath() used instance 'asppage' to getASPConfig(). Also modified setResponseContentFileName() added 'attachment;' to content disposition value.
 *
 * Revision 1.26  2005/08/08 09:44:04  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.25  2005/07/07 08:09:22  rahelk
 * CSL: Corrected profile_page locking problem when getting profile elements from CSL page
 *
 * Revision 1.24  2005/07/05 13:28:32  riralk
 * Fixed deleteQuery problem.
 *
 * Revision 1.23  2005/06/27 10:03:01  riralk
 * Modified methods pack() and unpack() to support clustering
 *
 * Revision 1.22  2005/06/27 05:01:08  mapelk
 * Bug fixes for std portlets
 *
 * Revision 1.21  2005/06/15 11:15:03  rahelk
 * CSL 2: bug fix: default values
 *
 * Revision 1.20  2005/06/13 10:28:51  mapelk
 * Use two static files instead of generating unautherised msg and ifs-dialog files for each language.
 *
 * Revision 1.19  2005/06/09 11:30:28  rahelk
 * CSL 2: private settings
 *
 * Revision 1.18  2005/06/09 09:40:35  mapelk
 * Added functionality to "Show pages in default layout mode"
 *
 * Revision 1.17  2005/06/08 09:21:50  japase
 * Added extended debugging level
 *
 * Revision 1.16  2005/06/06 07:29:02  rahelk
 * Restructured BlockProfile to handle both queries and default values
 *
 * Revision 1.15  2005/05/26 13:45:36  kirolk
 * Merged PKG14 changes to HEAD.
 *
 * Revision 1.14  2005/05/17 10:44:13  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * Revision 1.13.2.1  2005/05/16 11:21:18  mapelk
 * JSR 168: Error handling while reading parameters in WPS
 *
 * Revision 1.13  2005/05/06 09:56:42  mapelk
 * changes required to make standard portlets (JSR 168) to run on WebSphere
 *
 * Revision 1.12  2005/05/04 05:32:00  rahelk
 * Layout profile support for groups
 *
 * Revision 1.11  2005/04/08 06:05:36  riralk
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.10  2005/03/21 09:19:05  rahelk
 * JAAS implementation
 *
 * Revision 1.9  2005/03/18 11:59:11  madrse
 * Removed classes ifs.fnd.service.CellPhone, ifs.fnd.pages.SendSMSMessage and method ifs.fnd.asp.AspManager.sendSMSMessage
 *
 * Revision 1.8  2005/02/25 07:27:12  mapelk
 * Changed version to package 12
 *
 * Revision 1.7  2005/02/17 09:54:32  rahelk
 * Added public method setResponseContentFileName
 *
 * Revision 1.6  2005/02/11 09:12:10  mapelk
 * Remove ClientUtil applet and it's usage from the framework
 *
 * Revision 1.5  2005/02/03 12:40:36  riralk
 * Adapted BlockProfile (saved queries) functionality to new profile changes.
 *
 * Revision 1.4  2005/02/03 03:39:22  sumelk
 * Added new method reloadProfileCache().
 *
 * Revision 1.3  2005/02/02 08:22:17  riralk
 * Adapted global profile functionality to new profile changes
 *
 * Revision 1.2  2005/02/01 10:32:58  mapelk
 * Proxy related bug fix: Removed path dependencies from webclientconfig.xml and changed the framework accordingly.
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.10  2004/12/17 08:45:36  mapelk
 * version changed to 3.7.0 Package 10
 *
 * Revision 1.9  2004/12/10 08:54:28  mapelk
 * Added electronic signature support
 *
 * Revision 1.8  2004/11/29 07:56:24  japase
 * Introduced common super class for all connection pools.
 *
 * Revision 1.7  2004/11/25 05:58:01  chdelk
 * Added support for Activity APIs based LOVs.
 *
 * Revision 1.6  2004/11/23 03:06:57  chdelk
 * Changed the names of addXXXQueyParameter() methods to addQueryParameter()
 *
 * Revision 1.5  2004/11/22 10:31:53  chdelk
 * Changed getActivityOperation() calls according to the changes done in ASPBlock.java.
 *
 * Revision 1.4  2004/11/22 04:55:59  chdelk
 * Modified/Added APIs to support Activities in Master-Detail pages.
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.webmobile.web.MobilePageProvider;
import ifs.fnd.os.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.image.*;
import ifs.fnd.ap.*;
import ifs.fnd.servlets.*;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.lang.reflect.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.portlet.*;

import ifs.fnd.websecurity.SecurityHandler;

import ifs.fnd.webfeature.*;
import ifs.fnd.record.*;
import ifs.fnd.base.FndContext;
import ifs.fnd.webfeature.FndDataAdapter;
import ifs.fnd.internal.*;

/**
 * The only class that can be instantiated from an ASP script by calling
 * Server.CreateObject()
 */
public class ASPManager
{
   //==========================================================================
   // constants and static variables
   //==========================================================================

   private final static String version             = "IFS Web Client 3.8.0 CPS 7";
   private final static String TRANSFER_PARAM_NAME = "__TRANSFER";

   final static String DECISION_BUF        = "__DECISION_BUF";
   final static String ERROR_DECISION_BUF  = "__ERROR_DECISION_BUF";
   final static String URL_TO              = "__REDIRECT_TO";
   final static String URL_FROM            = "__REDIRECT_FROM";
   final static String NAVIGATOR_CLASS_NAME        = "ifs.fnd.pages.Navigator";
   final static String MOBILE_NAVIGATOR_CLASS_NAME = "ifs.fnd.webmobile.pages.Navigator";
   final static String CAN_GZIP_ENABLED    = "__GZIP_ENABLED";
   final static String CURRENT_FND_USER    = "__FND_USER";
   // Added by Terry 20130902
   // Add document reference bar
   final static String DOC_CONNECTION_AWARE = "__DOC_CONNECTION_AWARE";
   // Added end
   final static String DEFAULT_LAYOUT      = "__DEFAULT_LAYOUT";

   public final static String PAGE_COPY_KEY       = "__PAGE_COPY";
   public final static String PAGE_COPY_VALUE     = "COPY";
   public final static String IS_PAGE_EXPIRED     = "__IS_PAGE_EXPIRED";
   final static String PAGE_COPY_SEPARATOR = "@";   //tried but couldnt use # ^ *

   final static String DEF_KEY_SEPARATOR = "@*";
   final static String HOST_PARAM = "__HOST";
   final static String RWC_HOST = "RWC";

   static final double NOT_A_NUMBER = Double.longBitsToDouble(-2251799813685248L); // JScript NaN

   private static Hashtable dic_table = new Hashtable();

   public static String  FRAME_REF       = "FRAME_REF";
   public static String  DIRTY_LABELS       = "DIRTY_LABELS";
   private static final String CODE_PART_LABELS = "__CODE_PART_LABELS";
   public static boolean DEBUG           = Util.isDebugEnabled("ifs.fnd.asp.ASPManager");
   public static boolean DEBUGEX         = Util.isDebugEnabled("ifs.fnd.asp.ASPManager.extended");
   public static boolean DEBUGTRS        = Util.isDebugEnabled("ifs.fnd.asp.ASPManager.trsheader");
   public static boolean TRACEFILE_DEBUG = Util.isTraceFileDebugEnabled();
   static boolean noreset;

   private final static char[] map = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

   private final static char C29 = (char)29;
   private final static char C30 = (char)30;
   private final static char C31 = (char)31;

   private final String ENC_TYPE = "iso-8859-1"; //used in file upload;

   //==========================================================================
   // Trace statistics
   //==========================================================================

   private static TraceEventType total_event_type      = new TraceEventType("ASPManager.total");
   private static TraceEventType call_mts_event_type   = new TraceEventType("ASPManager.call-DB");
   private static TraceEventType on_start_event_type   = new TraceEventType("ASPManager.onStartPage");
   private static TraceEventType on_end_event_type     = new TraceEventType("ASPManager.onEndPage");
   private static TraceEventType asp_event_type        = new TraceEventType("ASP-script");
   private static TraceEventType new_dic_event_type    = new TraceEventType("ASPManager.new-dic");
   private static TraceEventType open_dic_event_type   = new TraceEventType("ASPManager.open-dic");
   private static TraceEventType lookup_dic_event_type = new TraceEventType("ASPManager.lookup-dic");

   private TraceEvent total_event;
   private TraceEvent call_mts_event;
   private TraceEvent on_start_event;
   private TraceEvent on_end_event;
   private TraceEvent asp_event;


   //==========================================================================
   // AspContext objects created by the manager
   //==========================================================================

   private HttpServlet         servlet;
   private HttpSession         asp_session;
   protected HttpServletRequest          asp_request;
   protected HttpServletResponse         asp_response;
   ServletContext              asp_application;

//   private Server           asp_server;

   /* Some variables for the cookie handling */
   private Vector asp_cookie_dict = new Vector();
   private Vector asp_cookie_throw_list = new Vector();
   private int anonymous_cookies;

   //==========================================================================
   // Others objects created by the manager
   //==========================================================================

   //private ASPPopup help_pop;
   private ASPPage asppage;
   private String  page_id;

   private int    restore_position = 0;
   private int    saved_position = 0;

   private int    flowid = 0;

   private String  status_line;        // Text to be shown by showStatusLine(). Used only by setStatusLine() and showStatusLine()
   private String  alert_text;         // Text to be shown by showAlert(). Used only by showAlert() and generateHTMLFields()
   private String  sys_alert_text;     // Text to be shown by showSystemAlert(). Used only by showSystemAlert() and generateHTMLFields()
   private String  initial_focus;      // HTML object name set by setInitialFocus(). Used only by setInitialFocus() and generateHTMLFields()
   //private String  auth_user_id;       // User id if authorization is done by the WebKit - logOnUser()
   private boolean page_never_expires; // Used by setPageNonExpiring(),isPageNonExpiring() and appendRequestClientScript()
   //private boolean undefined_perform;  // Used by perform() and performConfig()
   private String dynamic_def_key;
   private String page_copy_key;
   private byte[] signs = new byte[256]; //used in decodeValue()

   //==========================================================================
   // instances of objects created by ASPPage once per page
   //==========================================================================

   private ASPLog       asplog;
   private ASPContext   context;
   private ASPConfig    config;

   //==========================================================================
   // other references
   //==========================================================================

   // private RequestDictionary server_variables;
   private boolean is_mozilla;
   private boolean is_explorer;
   private boolean is_netscape6;
   private boolean is_netscape4x;
   private String port_string;

   //Temporary variables
   private String toolbar_title;
   private String real_user_name;
   private boolean binary_output=false; //For clarity set to false.
   private boolean html_gzip;
   private boolean is_lov_or_navigator = false;

   private String profile_file;

   private AutoString decode_out = new AutoString();

   private static Hashtable installed_modules = new Hashtable();
   private String user_preferred_language;
   private final static String USER_LANGUAGE_CODE = "__USER_LANGUAGE_CODE";
   public final static String PREFERRED_LANGUAGE = "__LANG_CODE";
   public final static String LANGUAGE_CHANGED   = "__LANG_CHANGED";

   private RenderResponse render_response;

   //private boolean on_save_table_properties;
   //private boolean on_save_block_properties;
   //private boolean on_save_menu_properties;
   private boolean on_save_profile_properties;

   private boolean is_logon_page;
   private boolean is_multirow_delete = false;
   private ContextSubstitutionVariables contextsubvars = new ContextSubstitutionVariables();
   private AutoString rwc_interface = new AutoString();
   private AutoString js_rwc_interface = new AutoString();

   private boolean is_application_search;
   private String[][] application_search_fields;

   private String query_string_at_logon;
   private boolean get_request;
   ConnectionPool.Slot connection            = null;
   ConnectionPool.Slot common_jap_connection = null;
   private boolean mobile_version = false;
   private boolean browser_checked = false;

   private int tiff_viewer_counter = -1; //To keep track of how many tiff viewer objects are there in a Page

   private boolean upload_successful = false;
   private boolean file_upload_error = false;
   private String upload_blob_id;
   private String upload_file_name;
   private String upload_file_path;
   private String upload_file_size;

   /**
    * Public constructor. Due to initialization from an ASP script the whole initialization
    * occurs inside the OnStartPage event handler.
    */
   public ASPManager()
   {
   }

   /**
    * Return current IFS Web Client version
    */
   public String getVersion()
   {
      return version;
   }

   //==========================================================================
   //  pack/unpack
   //==========================================================================


   /**
    * Call pack(ASPObject,int) passing the default maximum length for packed
    * ASPObjects (~ 1K).
    */
   public String pack( ASPObject obj )
   {
      try
      {
         return pack(obj,getASPConfig().getMaxPackLen());
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Transform an ASPObject into a string that is no longer than the
    * specified length.
    * The string may be appended to an url, as a Query String parameter.
    * Throw an exception, if the specified ASPObject does
    * not implement the ASPBufferable interface.
    * Note! This method may store the serialized object in a temporary file,
    * so it is important to call unpack() on the returned string, which will
    * automatically delete the file.
    */
   public String pack( ASPObject obj, int max_len )
   {
      String str;

      try
      {
         if ( obj instanceof ASPBuffer )
            str = ((ASPBuffer)obj).format();
         else
         {
            obj.verifyASPBufferable();

            ASPBuffer aspbuf = newASPBuffer();
            ((ASPBufferable)obj).save(aspbuf);
            str = aspbuf.format();
         }

         String urlstr = URLEncode(str);
         if ( urlstr.length() <= max_len ) return urlstr;

         String tmpfile = getTempFileName();
         boolean docache = "Y".equals(getASPConfig().getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N"));
         if (docache)
           DynamicObjectCache.put(tmpfile,str,null,this,true);
         else
           Str.writeFile(tmpfile,str);
         return URLEncode(tmpfile);
      }
      catch ( Throwable e )
      {
         error(e);
         return null;
      }
   }

   /**
    * Recreate an ASPObject from the specified string created by a call to pack().
    */
   public ASPObject unpack( String str )
   {
      if ( Str.isEmpty(str) ) return null;

      ASPBuffer aspbuf;

      try
      {
         if ( str.charAt(0) >= ' ' ) // str is a filename
         {
            String tmpfile = str;
            boolean docache = "Y".equals(getASPConfig().getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N"));
            if (docache)
            {
              String tmpstr = (String)DynamicObjectCache.get(tmpfile, getAspSession()).getData();
              aspbuf = newASPBuffer();
              aspbuf.parse(tmpstr);
              DynamicObjectCache.remove(tmpfile,asp_session);
            }
            else
            {
              Buffer buf = Buffers.load(tmpfile);
              aspbuf = newASPBuffer(buf);
              removeTempFile(tmpfile);
            }
         }
         else
         { // str is a formatted buffer
            aspbuf = newASPBuffer();
            aspbuf.parse(str);
         }

         String clsname = aspbuf.getBuffer().getString("CLASS",null);
         if ( clsname==null )
            return aspbuf;
         else
         {
            ASPBufferable obj = (ASPBufferable)newASPObject(clsname);
            obj.load(aspbuf);
            return(ASPObject)obj;
         }
      }
      catch ( Throwable e )
      {
         error(e);
         return null;
      }
   }

   //==========================================================================
   //  Creation of ASPObjects
   //==========================================================================

   /**
    * Returns a reference to the newly created instance of the named subclass
    * of the class ASPObject. The specified class name must contain the package name,
    * for example: "ifs.fnd.asp.ASPTable".
    */
   public ASPObject newASPObject( String class_name )
   {
      try
      {
         String n = class_name;

         if ( Str.isEmpty(n) ||
              "ifs.fnd.asp.ASPBuffer"           .equals(n) ) return newASPBuffer();
         if ( "ifs.fnd.asp.ASPBlock"            .equals(n) ) return asppage.newASPBlock(null);
         if ( "ifs.fnd.asp.ASPTable"            .equals(n) ) return asppage.newASPTable();
         if ( "ifs.fnd.asp.ASPCommand"          .equals(n) ) return newASPCommand();
         if ( "ifs.fnd.asp.ASPQuery"            .equals(n) ) return newASPQuery();
         if ( "ifs.fnd.asp.ASPTransactionBuffer".equals(n) ) return newASPTransactionBuffer();

         throw new FndException("FNDMGRCLSERR: The class &1 cannot be created by newASPObject()", class_name);
      }
      catch ( Throwable e )
      {
         error(e);
         return null;
      }
   }

   /**
    * Returns a reference to the newly created instance of the named class.
    * The specified class name must contain the package name, for example:
    * "ifs.demord.OrderProcessing".
    */
   public Object newObject( String class_name )
   {
      try
      {
         String n = class_name;

         return Class.forName(n).newInstance();
      }
      catch ( Throwable e )
      {
         error(e);
         return null;
      }
   }

   /**
   * Create an ASPGraph object.
   */

   public ASPGraph newASPGraph(int type)
   {
      try
      {
         ASPGraph graph = new ASPGraph(this,type);
         return graph;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Returns a reference to the newly created instance of ASPBuffer class.
    */
   public ASPBuffer newASPBuffer()
   {
      try
      {
         ASPBuffer buf = (new ASPTransactionBuffer(this)).construct();    // FUSK
         return buf;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Returns a reference to the newly created instance of ASPBuffer class
    * based on the specified Buffer.
    */
   public ASPBuffer newASPBuffer( Buffer buffer ) //Q&D RIRALK: made this public
   {
      ASPBuffer buf = (new ASPBuffer(this)).construct(buffer);
      return buf;
   }


   /**
    * Returns a reference to the newly created instance of ASPTransactionBuffer class.
    */
   public ASPTransactionBuffer newASPTransactionBuffer()
   {
      try
      {
         ASPTransactionBuffer buf = (ASPTransactionBuffer)(new ASPTransactionBuffer(this)).construct();
         return buf;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Returns a new anonymous (without name) ASPBlock.
    */
   public ASPBlock newASPBlock()
   {
      checkPortalPage();
      return asppage.newASPBlock(null);
   }

   /**
    * Returns a new ASPBlock having the specified name.
    */
   public ASPBlock newASPBlock( String name )
   {
      checkPortalPage();
      ASPBlock blk = asppage.newASPBlock(name);
      if(isMobileVersion())
      {
         blk.disableDocMan();
         blk.disableHistory();
      }
      else
      {
         // Added by Terry 20131101
         // Disable document reference
         blk.disableDocMan();
         // Added end
      }
      return blk;
   }

   /**
    * Returns a new instance of ASPRowSet based on the specified ASPBlock.
    */
   public ASPRowSet newASPRowSet( ASPBlock block )
   {
      return block.getASPRowSet();
   }

   /**
    * Returns a reference to the newly created instance of ASPTable class.
    */
   public ASPTable newASPTable()
   {
      checkPortalPage();
      return asppage.newASPTable(null, null);
   }

   /**
    * Returns a reference to the newly created instance of ASPTable class
    * based on all fields from the specified ASPBlock.
    */
   public ASPTable newASPTable( ASPBlock block )
   {
      checkPortalPage();
      return asppage.newASPTable(block, null);
   }

   /**
    * Returns a reference to the newly created instance of ASPCommand class.
    */
   public ASPCommand newASPCommand()
   {
      try
      {
         ASPCommand cmd = (new ASPCommand(this)).construct();
         return cmd;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Returns a reference to the newly created instance of ASPQuery class.
    */
   public ASPQuery newASPQuery()
   {
      try
      {
         ASPQuery qry = (ASPQuery)(new ASPQuery(this)).construct();
         return qry;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Returns a new instance of ASPHTMLFormatter.
    */
   public ASPHTMLFormatter newASPHTMLFormatter()
   {
      checkPortalPage();
      return asppage.getASPHTMLFormatter();
   }


   /**
    * Returns a new instance of ASPInfoServices.
    */
   public ASPInfoServices newASPInfoServices()
   {
      checkPortalPage();
      return asppage.getASPInfoServices();
   }


   /**
    * Returns a reference to a new local instance of ASPConfig
    * @deprecated
    */
   public ASPConfig newASPConfig( String file_name )
   {
      error(new FndException("FNDMGRNCFGD: Function newASPConfig(file_name) is deprecated"));
      return null;
   }


   /**
    * Returns a reference to the global instance of ASPConfig class.
    */
   public ASPConfig getASPConfig()
   {
      try
      {
         if ( config==null )
            throw new FndException("FNDMGRCFGNULL: Fatal Error - configuration information not accessible!");
         return config;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Force to reload ASPConfig.ifm file from the disk.
    */
   public void reloadConfig()
   {
      ASPConfigFile.clear();
      ASPPagePool.clear();
      ASPProfileCache.clear();
      clearMTSSecurityCache();
   }

   /**
    * Force to reload the profile cache.
    */
   public static void reloadProfileCache()
   {
      ASPProfileCache.clear();
   }

   /**
    * Returns the reference to a new instance of ASPTabContainer class.
    */
   public ASPTabContainer newASPTabContainer()
   {
      checkPortalPage();
      return asppage.newASPTabContainer(null);
   }


   /**
    * Returns the reference to a new instance of ASPCommandBar class
    * attached to the specified ASPBlock.
    */
   public ASPCommandBar newASPCommandBar(ASPBlock block)
   {
      return block.newASPCommandBar();
   }

   /**
    * Returns a reference to an instance of ASPLog class. The instance
    * (only one per instance of ASPManager) is automatically created in
    * OnStartPage event handler.
    */
   public ASPLog getASPLog()
   {
      return asplog;
   }


   /**
    * Returns a reference to an instance of ASPContext class. The only one
    * instance is automatically created in OnStartPage event handler.
    */
   public ASPContext getASPContext()
   {
      checkPortalPage();
      return context;
   }


   /**
    * Returns a reference to an instance of ASPForm class. The only one
    * instance is automatically created in OnStartPage event handler.
    */
   public ASPForm getASPForm()
   {
      //checkPortalPage();
      return asppage.getASPForm();
   }


   //==========================================================================
   //  Navigator functions
   //==========================================================================

   /**
    * Returns a reference to an instance of ASPNavigator class. The only one
    * instance is automatically created on first call of this function.
    */
   public ASPNavigator getASPNavigator()
   {
      checkPortalPage();
      return asppage.getASPNavigator();
   }

   /**
    * Creates a new instance of ASPNavigatorNode that represents a node in the navigator.
    * Returns a reference to the newly created object.
    */
   public ASPNavigatorNode newASPNavigatorNode( String label )
   {
      return newASPNavigatorNode( label, null, null );
   }

   public ASPNavigatorNode newASPNavigatorNode( String label, String view )
   {
      return newASPNavigatorNode( label, view, null );
   }

   public ASPNavigatorNode newASPNavigatorNode( String label, String view, String image )
   {
      try
      {
         ASPNavigatorNode node = (new ASPNavigatorNode(asppage.getASPNavigator()))
                                 .construct(label, view, image );
         return node;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  User authorization
   //==========================================================================

   /**
    * Log on the user. The authorization way is given by configuration parameters
    * in the ASPConfig.ifm file, section ADMIN/USER_AUTHENTICATION.
    * Return true on successful login, throw exception if IIS authorizes users.
    * Additional information may be presented on the status row.
    *
    * @deprecated
    */
   public boolean logOnUser( String userid, String password )
   {
      if ( DEBUG ) debug("ASPManager.logOnUser("+userid+")");
      return false;

      /*
      try
      {
         // allowed values for authorization method: IIS, FND_NT, FND_DB, FND_LDAP
         ASPConfig  cfg = getASPConfig();

         if ( cfg.isUserExternallyIdentified() )
            throw new FndException("FNDMGRUAUTHIIS: User authorization is done by the Web Server.");

         //Authenticate
         int err = authenticateUser(userid, password);

         switch ( err )
         {
            case 1:
               context.authorizeUser(userid.toUpperCase());
               setStatusLineFromJava("FNDMGRLOGONSUCC: User '&1' successfully logged on.",userid);
               break;
            case -2:
               setStatusLineFromJava("FNDMGRLOGONERR: Invalid username/password.");
               context.unAuthorizeUser();
               return false;
            case 0:
               setStatusLineFromJava("FNDMGRCONNERR: Could not establish a connection. Contact your system administrator.");
               context.unAuthorizeUser();
               return false;
         }

         context.removeGlobal(REAL_USER_NAME);
         real_user_name=null;
         fetchRealUserName();
         setUserPreferredLanguage(getUserLanguage());
         return true;
      }
      catch ( Throwable any )
      {
         context.unAuthorizeUser();
         error(any);
         return false;
      }
       */
   }


   private final static String REAL_USER_NAME = "__REAL_USER_NAME";
   /**
    * Log off the current user. Works only if IIS does not perform
    * user authorization.
    *
    * @see ifs.fnd.asp.ASPManager#logOnUser
    */
   public void logOffCurrentUser()
   {
      if ( DEBUG ) debug("ASPManager.logOffCurrentUser()");
      try
      {
         context.unAuthorizeUser();
         context.removeGlobal(REAL_USER_NAME);
         context.removeGlobal(USER_LANGUAGE_CODE);
         real_user_name=null;
         setUserPreferredLanguage(null);
         setStatusLineFromJava("FNDMGRLOGOFF: User logged off.");
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }


   /*
   protected void checkUserAuthorized() throws FndException
   {
      if ( DEBUG ) debug("ASPManager.checkUserAuthorized()");
      if ( context.isUserLoggedOn() ) return;

      if ( DEBUG ) debug("  [checkUserAuthorized] User not logged on.");

      ASPConfig cfg = getASPConfig();

      if (!cfg.useAuthorization()) return;

      String url = cfg.getLogonURL();

      if ( Str.isEmpty(url) )
         throw new FndException("FNDMGRUDEFLURL: URL for Logon script is undefined.");

      int pos = url.indexOf(".page");
      if(pos<0)
         pos = url.indexOf(".asp");
      if(pos>0)
         url = url.substring(0,pos);

      //String my_url = asppage.getURL().toLowerCase();
      String my_url = asppage.getCurrentPagePath().toLowerCase();

      pos = my_url.indexOf(".page");
      if(pos<0)
         pos = my_url.indexOf(".asp");
      if(pos>0)
         my_url = my_url.substring(0,pos);

      if( !my_url.equalsIgnoreCase(url) )
      {
         if(DEBUG) debug("  [checkUserAuthorized]my_url="+my_url+", url="+url);

         if(DEBUG) debug("  [checkUserAuthorized]redirect to URL: "+url);
         if (readValue("VALIDATE") != null)
         {
            writeResponse("SESSION_TIME_OUT:" + translateJavaScript("FNDMGRSESSIONTIMEOUTVAL: Session has expired. Please logon."));
            endResponse();
            return;
         }
         redirectTo(url+".page");
      }
   }
    */

   private int authenticateUser( String userid, String password ) throws FndException
   {
      ASPConfig config = getASPConfig();
      String realm = config.getSecurityRealm();
      //String config_user = config.getConfigUser();
      //String config_password = config.getApplicationPassword();

      if (SecurityHandler.authenticateUser(realm, userid, password))
         return 1;
      else
         return -2;
   }

   /**
    * Check the log on information (user ID and password) supplied by the user.
    */
   public boolean checkLogOn( String userid, String password )
   {
      if(DEBUG) debug("ASPManager.checkLogOn("+userid+")");
      try
      {
         if( getASPConfig().isUserExternallyIdentified() )
            throw new FndException("FNDMGRUAUTHIIS2: User authorization is done by the Web Server.");

         return authenticateUser(userid, password)==1;
      }
      catch( Throwable any)
      {
         error(any);
         return false;
      }
   }

   //==========================================================================
   //  AspContext objects
   //==========================================================================

   HttpServlet getServlet()
   {
      return servlet;
   }

   public HttpServletRequest getAspRequest()
   {
      return asp_request;
   }

   HttpServletResponse getAspResponse()
   {
      return asp_response;
   }

   ServletContext getAspApplication()
   {
      return asp_application;
   }

   void debugSessionAttributes( HttpSession session, String msg )
   {
      AutoString tmpbuf = new AutoString();

      tmpbuf.append("\n",this+".debugSessionAttributes(): session="+session,", ",msg);

      Enumeration en = session.getAttributeNames();
      while ( en.hasMoreElements() )
      {
         String name  = (String)en.nextElement();
         String value = ""+session.getAttribute(name);
         tmpbuf.append("  ",name,"="+value);
      }
      tmpbuf.append(this+".debugSessionAttributes(): END\n");
      debug(tmpbuf.toString());
   }

   public HttpSession getAspSession()
   { 
      //return asp_session;
      /*
      HttpSession session = asp_request.getSession(true);
      if ( DEBUG ) debugSessionAttributes(session,"");
      return session;
      */
      if(asp_session==null)
      {
         if(DEBUG) debug(this+".getAspSession(): fetching session");
         asp_session = asp_request.getSession(true);
      }
      if(DEBUGEX) debugSessionAttributes(asp_session,"");
      return asp_session;
   }

   void invalidateSession()
   {
      if(asp_session!=null)
      {
         if(DEBUG) debug(this+".invalidateSession()");
         asp_session.invalidate();
         asp_session = null;
      }
   }

//    Server getAspServer()
//    {
//       return asp_server;
//    }

   /**
    * @deprecated This method is obsolete in this track, use the method overloaded with the
    * String parameter instead.
    */
   public String getPhyPath()
   {
      String candidate = asp_application.getRealPath(asp_request.getRequestURI());
      if ( !(candidate.endsWith("\\") || candidate.endsWith("/")) )
      {
         if ( candidate.indexOf("/")>-1 )  //Slightly dirty
            candidate += "/";
         else
            candidate += "\\";
      }
      return candidate;
   }

   public String getPhyPath( String url )
   {
      boolean is_dir=false;
      if ( url.endsWith("\\") || url.endsWith("/") )
         is_dir=true;
      String candidate = asp_application.getRealPath(url);
      if ( is_dir && !(candidate.endsWith("\\") || candidate.endsWith("/")) )
      {
         if ( candidate.indexOf("/")>-1 ) //slightly dirty
            candidate += "/";
         else
            candidate += "\\";
      }
      return candidate;
   }


   void setAspResponseBuffered( boolean buffered )
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.setAspResponseBuffered("+buffered+")");
         //asp_response.setBuffered(buffered);
         asp_response.setBufferSize(buffered?100000:0);
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
      }
   }

   boolean getAspResponseBuffered()
   {
      try
      {
         return isResponseBuffered();
      }
      catch ( AspContextException any )
      {
         error(any);
         return false;
      }
   }

   boolean isResponseBuffered() throws AspContextException
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.getAspResponseBuffered()");
         //return asp_response.getBuffered();
         return asp_response.getBufferSize()>0;
      }
//       catch( ResponseEndException x)
//       {
//          return true;
//       }
      catch ( Throwable any )
      {
         throw new AspContextException(any);
      }
   }

   /**
    * Encapsulation of the cookie dictionary
    * There are three types of cookies:
    * VIC (Very Important Cookie)  - These cookies will be preserved at all times. If there's more than
    *                                20 of these cookies and anonymous cookies, the Web Client will display a
    *                                warning alert box.
    *                                VICs will be prefixed fndweb_.
    * Transient Cookies            - These cookies will be preserved only if there's no more than 20 VICs and
    *                                anonymous cookies.
    *                                These cookies are prefixed with fndwebTran. All cookies that's not entirely
    *                                vital for the application should be transient cookies. In most and normal
    *                                cases these cookies will be perfectable safe.
    * Temporary Cookies            - This is an internal type for the page id and should not be used in the rest
    *                                of the application. All temporary cookies in the request will be deleted, if
    *                                there's room for them in the response. Temporary cookies are prefixed
    *                                fndwebPageID. Page id cookies will be treated as VICs in the response.
    *
    * Due to some changes in the last minute, this solution with three types of cookeis is slightly overkill.
    */

   static class CookieContainer
   {
       final static String TRANSIENT_PREFIX = "fndwebTran_";
       final static String TEMPORARY_PREFIX = "fndwebPageID_";
       final static String VIC_PREFIX = "fndweb_";

       Cookie cookie;
       String name; // The name from a programmers point of view
       String browser_name; // The name the cookies has in the request/response/browser.
       int type;

       final static int VIC = 0;
       final static int TRANSIENT = 1;
       final static int TEMPORARY = 2;
   }

   public String getCookie(String name)
   {
      Cookie[] cookies = asp_request.getCookies();
      if ( cookies!=null )
      {
         for ( int i=0;i<cookies.length;i++ )
         {
            if (name.equals(cookies[i].getName()))
               return URLDecode(cookies[i].getValue());
         }
      }
      return null;
   }

   private void fetchCookies()
   {
       CookieContainer cc;
       Cookie[] cookies = asp_request.getCookies();
       if ( cookies!=null )
       {
           for ( int i=0;i<cookies.length;i++ )
           {
               cc = new CookieContainer();
               cc.cookie = cookies[i];
               cc.cookie.setValue(URLDecode(cc.cookie.getValue()));
               cc.cookie = cc.cookie;
               cc.browser_name = cc.cookie.getName();
               cc.cookie.setPath(getExceptionlessApplicationPath());
               if(cc.browser_name.startsWith(CookieContainer.TRANSIENT_PREFIX))
               {
                   cc.name = cc.browser_name.substring(cc.TRANSIENT_PREFIX.length());
                   cc.type = cc.TRANSIENT;
                   asp_cookie_dict.add(cc); //add last
               }
               else if(cc.browser_name.startsWith(CookieContainer.TEMPORARY_PREFIX))
               {
                   cc.cookie.setMaxAge(0);
                   cc.name = cc.browser_name.substring(cc.TEMPORARY_PREFIX.length());
                   cc.type = cc.TEMPORARY;
                   asp_cookie_throw_list.addElement(cc);
               }
               else if(cc.browser_name.startsWith(CookieContainer.VIC_PREFIX))
               {
                   cc.name = cc.browser_name.substring(cc.VIC_PREFIX.length());
                   cc.type = cc.VIC;
                   asp_cookie_dict.add(0,cc); //add first
               }
               else
                   anonymous_cookies++;
           }
           anonymous_cookies = anonymous_cookies>20?20:anonymous_cookies;
       }
       fitInJar();
   }

   Cookie getAspCookie( String name )
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.getAspCookie("+name+")");

         if ( !Str.isEmpty(name) )
            for ( int i=0; i<asp_cookie_dict.size(); i++ )
            {
               CookieContainer cc = (CookieContainer) asp_cookie_dict.elementAt(i);
               if ( name.equals(cc.name) )
               {
                  if ( cc.cookie.getPath() == null )
                     cc.cookie.setPath(getExceptionlessApplicationPath());
                  return cc.cookie;
               }
            }
         return null;
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }

   Cookie newPageIDCookie( String name )
   {
       return newAspCookie(name, CookieContainer.TEMPORARY_PREFIX+name, CookieContainer.TEMPORARY);
   }

   Cookie newTransientAspCookie( String name )
   {
        return newAspCookie(name, CookieContainer.TRANSIENT_PREFIX+name, CookieContainer.TRANSIENT);
   }

   Cookie newAspCookie(String name )
   {
        return  newAspCookie(name, CookieContainer.VIC_PREFIX+name, CookieContainer.VIC);
   }

   private Cookie newAspCookie( String name, String browser_name, int type )
   {
      try
      {
         CookieContainer cc = new CookieContainer();
         cc.name = name;
         cc.browser_name = browser_name;
         cc.cookie = new Cookie(cc.browser_name,null);
         cc.type = type;
         cc.cookie.setPath(getExceptionlessApplicationPath());
         if ( DEBUG ) debug("ASPManager.newAspCookie("+name+"):"+cc.cookie);

         if(type==cc.TRANSIENT)
             asp_cookie_dict.add(cc);
         else
             asp_cookie_dict.add(0,cc);

         fitInJar();
         return cc.cookie;
   }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }


  private boolean fitInJar()
  {
      CookieContainer cc;
      if(asp_cookie_dict.size()>(19-anonymous_cookies)) // This test has to be executed before adding the cookies!
      {
          cc = (CookieContainer) asp_cookie_dict.elementAt(19-anonymous_cookies);
          if(cc.type==cc.VIC || cc.type==cc.TEMPORARY){ //temporary=VIC in the response.
              showAlert(translate("ASPMANAGERCJARFULLW: The cookie limit for this domain has soon been reached and not every anonymous or important cookie did fit. This is just a warning, your application might or might not work correctly from now on."));

              return false;
          }
      }
      return true;
  }

   private boolean cookies_added = false;

   public void addCookies()
   {
      if ( DEBUG ) debug("ASPManager.addCookies()");
      try
      {
         if ( cookies_added ) return;

         CookieContainer cc;
         int number_of_cookies=anonymous_cookies;

         for (int i=0; i<asp_cookie_dict.size() && number_of_cookies<20;i++ )
         {
             number_of_cookies++;
             cc = (CookieContainer) asp_cookie_dict.elementAt(i);
             cc.cookie.setValue(URLEncode(cc.cookie.getValue()));
             cc.cookie.setPath(getExceptionlessApplicationPath());
             if(number_of_cookies>18 && cc.type==cc.TRANSIENT)
                 cc.cookie.setMaxAge(0);
            asp_response.addCookie(cc.cookie);
         }

         for(int i=0;i<asp_cookie_throw_list.size() && number_of_cookies<20;i++)
         {
             cc = (CookieContainer) asp_cookie_throw_list.elementAt(i);
             cc.cookie.setValue(URLEncode(cc.cookie.getValue()));
             asp_response.addCookie(cc.cookie);
             number_of_cookies++;
         }

         if(DEBUG)
         {
             debug("\n\nVIC and Transient Cookie Jar: \n\n");
             for(int i=0;i<asp_cookie_dict.size();i++)
             {
                 cc = (CookieContainer) asp_cookie_dict.elementAt(i);
                 debug("Cookie: "+cc.browser_name+"  Type: "+cc.type+"  at pos: "+i+" path: "+cc.cookie.getPath());
             }

             debug("\n\n Throw away list: \n\n");
             for(int i=0;i<asp_cookie_throw_list.size();i++)
             {
                 cc = (CookieContainer) asp_cookie_throw_list.elementAt(i);
                 debug("Cookie: "+cc.browser_name+"  Type: "+cc.type+"  at pos: "+i+" path: "+cc.cookie.getPath());
             }

         }

         cookies_added = true;
      }
            catch ( Throwable any )
      {
         error(new AspContextException(any));
         return;
      }
   }

   // used to set the path for cookies
   private String getExceptionlessApplicationPath()
   {
       try
       {
          //return getApplicationPath();
          return asppage.getASPConfig().getCookiePath();

       }
       catch(Throwable e){return null;}
   }

   /**
    * Set the content type of the asp response.
    */
   public void setAspResponsContentType( String content_type )
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.setAspResponsContentType("+content_type+")");
         asp_response.setContentType(content_type);
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
      }
   }

   /**
    * Method to set the "save as" file name using the content-disposition attribute
    * @param file_name String desired file name to be displayed.
    */
   public void setResponseContentFileName(String file_name)
   {
      setResponseContentFileName(file_name,false);
   }

   /**
    * Method to set the "save as" file name using the content-disposition attribute
    * @param file_name String desired file name to be displayed.
    * @param attachment boolean indicates whether the response is sent as an attachment.
    */
   public void setResponseContentFileName(String file_name, boolean attachment) {
        try {
            if(DEBUG)
                debug("ASPManager.setResponseContentFileName(" + file_name + ")");
            if (attachment)
               asp_response.setHeader("Content-Disposition", "attachment; filename=\"" + file_name + "\"");
            else
               asp_response.setHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"");
        }
        catch(Throwable any) {
            error(new AspContextException(any));
        }
    }


   /**
    * write data content to the browser giving the appropriate MIME type
    * @param data byte array containing the data preferably as 'UTF-8'
    * @param content-type string value of the required MIME type
    */
   public void writeContentToBrowser(byte[] data, String content_type)
   {
      writeContentToBrowser(data, content_type, null);
   }


   /**
    * write data content to the browser giving the appropriate MIME type
    * @param data byte array containing the data preferably as 'UTF-8'
    * @param content-type string value of the required MIME type.
    * @param file_name String value desired file name to be displayed in client file
    */
   public void writeContentToBrowser(byte[] data, String content_type, String file_name)
   {
      try
      {
         setAspResponsContentType(content_type);

         if (asp_response.containsHeader("Cache-Control") || asp_response.containsHeader("Pragma"))
         {
            asp_response.setHeader("Cache-Control","dummy");
            asp_response.setHeader("Pragma", "dummy");
         }
         if (file_name != null)
         {
            asp_response.setHeader("Content-disposition",
                 "attachment; filename=" +
                 file_name );
         }

         asp_response.setContentLength(data.length);
         writeResponse(data);
         endResponse();
      }
      catch ( Throwable any )
      {
         error(any);
      }

   }


   /**
   * @see ifs.fnd.asp.ASPManager#getASPConfigFileDir
   * @deprecated
    */
   public String getASPConfigFileName()
   {
      try
      {
         if(DEBUG) debug("ASPManager.getASPConfigFileName()");
         if( !Str.isEmpty(cfg_file_dir) )
            return cfg_file_dir +
                   (cfg_file_dir.charAt(cfg_file_dir.length()-1)==OSInfo.OS_SEPARATOR ? "" : OSInfo.OS_SEPARATOR_STRING) +
                    ASPConfigFile.WEB_CONFIG_FILE;
         return(String)asp_application.getAttribute("asp_config_file_name");//.toString();
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }

   /**
    * Returns the location of the configuration file.
    */
   public String getASPConfigFileDir()
   {
      try
      {
         if(DEBUG) debug("ASPManager.getASPConfigFileDir()");
         return cfg_file_dir;
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }
   //==========================================================================
   //  Request parameters
   //==========================================================================

   /**
    * Returns actual session identity
    */
   public String getSessionId()
   {
      try
      {
         return getAspSession().getId();
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }

   /**
    * Returns system name
    * @deprecated
    */
   public String getSystemName()
   {
       return null;//getASPConfig().getSystemName();
   }

   /**
    * Returns identity for the actually logged on Windows NT user in form: "DOMAIN\USER"
    */
   public String getUserId()
   {
      if(DEBUG) debug(this+": ASPManager.getUserId()");
      ASPConfig cfg = getASPConfig();

      if( cfg.useAuthorization() )
      {
         String user_id = retrieveUserId();
         if( isEmpty(user_id) )
         {
            user_id = asp_request.getRemoteUser();
            if( isEmpty(user_id) )
            {
               user_id = asp_request.getHeader(cfg.getAuthHeaderName());

               if( isEmpty(user_id) )
                  return null;
            }
            return user_id.toUpperCase();
         }
         else
            return user_id;
      }
      else
         return cfg.getTraceWebUser();
   }

   /**
    * Returns the modified currently logged user id to be used
    * as prefix for cookie names.
    */
   protected String getCookiePrefix()
   {
      if(DEBUG) debug(this+": ASPManager.getCookiePrefix()");
      String suffix = getUserId();
      if(suffix !=null){
         StringBuffer sb = new StringBuffer(suffix);

         for(int i=0; i <sb.length();i++)
         {
            String val = Integer.toString((int)sb.charAt(i));
            if(!isStandardChar(sb.charAt(i))){
               sb.replace(i,i+1,val);
            }
         }
         return sb.toString();
      }
      return suffix;

   }

   /**
    * Returns whether the input character is a Standard character used
    * for user ID. This method returns true for alphabetical characters and '\'
    * character and false for other characters.
    */
   private boolean isStandardChar(char charValue)
   {
      int intVal = (int)charValue;
      return ((intVal >= 48 && intVal <= 57) || (intVal >= 65 && intVal <= 90) || (intVal >= 97 && intVal <= 122) || intVal == 92);
   }

   private String retrieveUserId()
   {
      ASPConfig cfg = getASPConfig();
      //if (cfg.identifiedByStandardPortal())
      //   return (String)getAspSession().getAttribute(ifs.fnd.portal.GenericIFSPortlet.USER_ID);
      //else
      if (cfg.identifiedByPlugin())
      {
         try
         {
            return ((AuthenticationPlugin)Class.forName(cfg.getAuthenticatePluginClassName()).newInstance()).getUserId(asp_request);
         }
         catch (Exception e)
         {
            return null;
         }
      }
      else
         return null;
   }



   /**
    * Returns the currently logged in Foundation user. The HTTP session object
    * is first checked to see if 'FND_USER' is available. If not found a db call is
    * made using Fnd_Session_Api.Get_Fnd_User.
    * The result is then stored in the current session for further use.
    */

   public String getFndUser()
   {

      try
      {
         HttpSession session = getAspSession();
         String fnd_user = (String)session.getAttribute(CURRENT_FND_USER);

         if (!isEmpty(fnd_user))
            return fnd_user;
         else
         {
            ASPCommand cmd  = (new ASPCommand(this)).construct();
            cmd.defineCustomFunction("Fnd_Session_Api.Get_Fnd_User");
            cmd.addParameter("RESULT", "S", null, null);
            //cmd.addParameter("FNDUSER","S", null, fnd_user);

            ASPTransactionBuffer trans = newASPTransactionBuffer();
            trans.addCommand("FUSER",cmd);
            if( asppage.isUndefined() )
               trans = performConfig(trans);
            else
               trans = perform(trans);
            fnd_user = trans.getValue("FUSER/DATA/RESULT");
            // Added by Terry 20130401
            // Bug fix
            if (!"IFSWEBCONFIG".equals(fnd_user))
            // Added end
               session.setAttribute(CURRENT_FND_USER,fnd_user);
            return fnd_user;
         }
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }

   }

   /**
    * Fetched the real name of the currently logged on user from the database or cookie.
    */
   private void fetchRealUserName()
   {
      if(DEBUG) debug(this+": ASPManager.fetchRealUserName(): real_user_name="+real_user_name);

      String user_id = getUserId();
      String uid = user_id;
      if(asplog!=null)
         asplog.logUserBrowser(user_id);
      ASPContext ctx = asppage.getASPContext();

      if( isEmpty(user_id) )
      {
         ctx.removeGlobal(REAL_USER_NAME);
         real_user_name=null;
         if(DEBUG) debug("  fetchRealUserName: real_user_name="+real_user_name);
         return;
      }

      real_user_name = ctx.findGlobal(REAL_USER_NAME);
      if(DEBUG) debug("  fetchRealUserName: real_user_name="+real_user_name);

      if( !isEmpty(real_user_name) ) return;

      user_id = Str.replace(user_id,"/","'||chr(47)||'");
      user_id = Str.replace(user_id,"\\","'||chr(92)||'");
      user_id = user_id.toUpperCase();

      ASPTransactionBuffer trans = newASPTransactionBuffer();

      //String colname = getASPConfig().getUserAuthorizationMethod()==Util.DB_USER_AUTH?"ORACLE_USER":"WEB_USER";

      //trans.addQuery("GET_NAME","SELECT DESCRIPTION FROM FND_USER WHERE "+colname+"='"+user_id+"'");
      ASPCommand cmd = newASPCommand();

      String fnd_user = null;

      if (getASPConfig().useAuthorization())
      {
         /*
          if (!getASPConfig().isUserExternallyIdentified())
          {
              cmd.defineCustomFunction("FND_USER_API.Get_Description");
              cmd.addParameter("DESCRIPTION","S",null,null);
              cmd.addParameter("FNDUSER","S", "IN", getFndUser());

              trans.addCommand("GET_NAME",cmd);
          }
          else
          */
         trans.addQuery("GET_NAME","SELECT DESCRIPTION, IDENTITY FROM FND_USER WHERE WEB_USER='"+user_id+"'");
      }
      else
      {
          // no authentication user_id must be a fnd_user
          cmd.defineCustomFunction("FND_USER_API.Get_Description");
          cmd.addParameter("DESCRIPTION","S",null,null);
          cmd.addParameter("FNDUSER","S", "IN", user_id);

          trans.addCommand("GET_NAME",cmd);
          fnd_user = user_id;
      }


      if( asppage.isUndefined() )
         trans = performConfig(trans);
      else
         trans = perform(trans);

      try
      {
         real_user_name = trans.getBuffer("GET_NAME/DATA").getValue("DESCRIPTION");
         fnd_user = trans.getBuffer("GET_NAME/DATA").getValue("IDENTITY");
      }
      catch ( Throwable e )
      {
         real_user_name=null;
         error(new FndException("FNDMGRUIDERR: The directory id &1 is not allowed to run the application",uid));
      }


      if( !isEmpty(real_user_name) )
         ctx.setGlobal(REAL_USER_NAME,real_user_name);
      
      // Added by Terry 20130402
      if ( !isEmpty(fnd_user) && !"IFSWEBCONFIG".equals(fnd_user))
      {
         ctx.setGlobal(CURRENT_FND_USER, fnd_user);
      }
      // Added end

      if(DEBUG) debug("  fetchRealUserName: real_user_name="+real_user_name);

      try
      {
         //initialize all user specific data
         UserDataCache user_data = UserDataCache.getInstance();
         user_data.initialize(getUserId(),this);
      }
      catch (Exception any)
      {
         error(any);
      }

      if (!config.isFormBasedAuth()) return;

      trans.clear();

      //if language changed from logon page set that lang to the user's profile
      if ("Y".equals(getAspSession().getAttribute(LANGUAGE_CHANGED)))
      {
         getAspSession().setAttribute(LANGUAGE_CHANGED,"");

         cmd.defineCustom("fnd_user_api.set_property");
         cmd.addParameter("FNDUSER","S", "IN", fnd_user);
         cmd.addParameter("PROPERTY","S", "IN", "PREFERRED_LANGUAGE");
         cmd.addParameter("LANG_CODE","S","IN",user_preferred_language);
      }
      else
      {
         //fetch language from profile
         cmd.defineCustomFunction("FND_USER_API.Get_Property");
         cmd.addParameter("LANG_CODE","S",null,null);
         cmd.addParameter("FNDUSER","S", "IN", fnd_user);
         cmd.addParameter("PROPERTY","S", "IN", "PREFERRED_LANGUAGE");
      }

      trans.addCommand("GET_LANG_CODE",cmd);

      if( asppage.isUndefined() )
         trans = performConfig(trans);
      else
         trans = perform(trans);

      String profile_lang = trans.getBuffer("GET_LANG_CODE/DATA").getValue("LANG_CODE");
      HashMap lang_table = ASPConfigFile.language_table;

      if(DEBUG) debug("  fetchRealUserName:  profile language="+profile_lang);

      //redirect to currect URL to get the correct poolkey
      if( !Str.isEmpty(profile_lang) )
      {
         if (lang_table.containsKey(profile_lang.toUpperCase()))
         {
            setUserPreferredLanguage(profile_lang);
            if(!isEmpty(readValue("fedsearch"))) return;
            String qrystr = getQueryString();
            String url = asppage.getURL();
            if (!isEmpty(qrystr))
               url = url +"?"+qrystr;

            url = getAbsoluteURL(url);

            redirectRequest(url);
         }
         else
            showAlert("FNDMGRUNSUPPPROFILELANG: The application does not support the lanague in your profile. Go to Change Language page if you wish to change your profile language.");
      }

      return;
   }

   String getRealUserName()
   {
      if ( DEBUG ) debug(this+": ASPManager.getRealUserName(): "+real_user_name);
      return real_user_name;
   }

   /**
    * Returns language code for the current client language fetched from the URL.
    */
   public String getLanguageCode()
   {
      //return getASPConfig().getLanguageCode();
      return getUserLanguage();
   }

   public String getUserLocale()
   {
      String locale = "";
      try 
      {
         locale = getRFCLanguageCode();
      }
      catch ( Throwable any )
      {
         error(any);
      }
      return (!isEmpty(locale)?locale:asp_request.getLocale().getDisplayName());
   }

    
   String getRFCLanguageCode() throws FndException
   {
      ASPCommand cmd  = (new ASPCommand(this)).construct();
      cmd.defineCustomFunction("Language_Code_API.Get_Lang_Code_RFC3066");
      cmd.addParameter("RESULT", "S", null, null);
      cmd.addParameter("LNGCODE","S", "IN", getLanguageCode());

      ASPTransactionBuffer trans = newASPTransactionBuffer();
      trans.addCommand("RFCLANG",cmd);

      if(DEBUG) trans.traceBuffer("RFC-lang request:");

      //trans = perform(trans);
      // Do always performConfig()
      trans.addConfigRequestHeader();
      trans = callMTS(trans, null, false, true);

      if(DEBUG) trans.traceBuffer("RFC-lang response:");

      return trans.getValue("RFCLANG/DATA/RESULT");
   }


   /**
    * Returns reference to the server formatter
    */
   protected ServerFormatter getServerFormatter()
   {
      return getASPConfig().getServerFormatter();
   }


   /**
    * Returns the time zone defined for this ASP server
    */
   public String getTimeZone()
   {
      try
      {
         return getASPConfig().getTimeZone();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Returns the default format mask for the current language and
    * the specified data type
    */
   public String getFormatMask( String datatype )
   {
      return getFormatMask(datatype,true);
   }

   /**
    * Returns the format mask for the current language and the specified data type. Depending
    * on the value for read_profile, it read the mask from profile. If the user hasn't set it in
    * the profile then try to get it from the webclientconfig.xml. If it is not specified in the
    * config it get the mask from the Local for the given language.
    *@param datatype this can be "String","Number","Integer","Money","Datetime","Date","Time"
    *@param read_profile to consider  user profile or not
    */

   public String getFormatMask( String datatype, boolean read_profile)
   {
      try
      {
         if (DEBUG)
            debug(" ASPManager.getFormatMask: Reading MASK for data type " + datatype + " in language '" +getLanguageCode() + "'");

         String mask = "";
         int type_id = DataFormatter.getTypeId(datatype);

         if (read_profile)
         {
            if (isPortalPage())
               mask = getPortalPage().readGlobalProfileMask(type_id);
            else
               mask = getASPPage().readGlobalProfileMask(type_id);

            if (DEBUG)
               debug(" ASPManager.getFormatMask: Read MASK from Profile: " + mask);

            if (!isEmpty(mask))
               return mask;
         }

         mask = getConfigParameter("LANGUAGE/"+getLanguageCode()+"/"+datatype+"/MASK",null);

         if (DEBUG)
            debug(" ASPManager.getFormatMask: --- found MASK: " + mask);

         if (!isEmpty(mask))
            return mask;

         //mask not found in webclientconfig, try java Locale using current language
        if (DEBUG)
            debug(" ASPManager.getFormatMask: No MASK found in profile or in config file.");
        DateFormat df = null;
        switch (type_id){
            case DataFormatter.STRING:
               break;
            case DataFormatter.BOOLEAN:
               break;
            case DataFormatter.NUMBER:
               mask=((DecimalFormat) NumberFormat.getInstance(new Locale(getLanguageCode()))).toPattern();
               break;
            case DataFormatter.INTEGER:
               mask=((DecimalFormat) NumberFormat.getIntegerInstance(new Locale(getLanguageCode()))).toPattern();
               break;
            case DataFormatter.MONEY:
               mask=((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale(getLanguageCode()))).toPattern();
               int i = mask.indexOf(';');
               if (i>1)
                  mask = mask.substring(0,i);
               mask=mask.replaceAll("\u00A4", "").trim(); //remove currency sign from pattern and trim
               break;
            case DataFormatter.DATETIME:
               df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM,new Locale(getLanguageCode()));
               if (df instanceof SimpleDateFormat)
                 mask = ((SimpleDateFormat)df).toPattern();
               break;
            case DataFormatter.DATE:
               df = DateFormat.getDateInstance(DateFormat.SHORT,new Locale(getLanguageCode()));
               if (df instanceof SimpleDateFormat)
                 mask = ((SimpleDateFormat)df).toPattern();
               break;
            case DataFormatter.TIME:
               df = DateFormat.getTimeInstance(DateFormat.SHORT,new Locale(getLanguageCode()));
               if (df instanceof SimpleDateFormat)
                 mask = ((SimpleDateFormat)df).toPattern();
               break;
            default:
               throw new FndException("FNDMGRINVVAL: Invalid value for data type : &1", ""+datatype);
         }
         if (DEBUG)
            debug(" ASPManager.getFormatMask: MASK got from locale : " + mask );
      return mask;
         //Bug id 42390, end
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Generate the format mask for given numeric type.
    * @param group_sizes this can be a digit or semicolon separated list. interface 3;2 -> ##,##,###
    * @param decimal_size number of decimals
    * @param type can be DataFormatter.NUMBER or DataFormatter.MONEY or DataFormatter.INTEGER
    */
   public String getNumericFormatMask(String group_sizes, int decimal_size, int type)
   {
      if (group_sizes==null) return null;
      if (DEBUG)
         debug("Params - groups = " + group_sizes + " decimal_size=" + decimal_size + " type = " + type);
      StringTokenizer tokens = new StringTokenizer(group_sizes, ";");
      int token_count = tokens.countTokens();
      int count = 0;
      String[] groups = new String[token_count];
      String mask="";

      String post_mask="";
      String value_symbol;
      String decimal_symbol;
      switch(type)
      {
         case DataFormatter.NUMBER:
            decimal_symbol = "#";
            value_symbol = "#.";
            break;
         case DataFormatter.MONEY:
            decimal_symbol = "0";
            value_symbol = "0.";
            break;
         case DataFormatter.INTEGER:
         default:
            decimal_symbol = "";
            value_symbol = "#";
            break;
      }
      for (int k=0; k<decimal_size;k++)
         post_mask +=decimal_symbol;
      post_mask = value_symbol + post_mask;
      while (tokens.hasMoreElements())
      {
         int size;
         try
         {
            size = Integer.parseInt(tokens.nextToken());
         }
         catch(NumberFormatException e)
         {
            return null;
         }
         String temp_mask = "";
         for (int i=0;i<size;i++)
            temp_mask+="#";
         if (size==0)
            temp_mask = "################";
         else if (token_count==1)
            temp_mask=temp_mask+","+temp_mask+","+temp_mask;
         if (count==0)
            temp_mask=temp_mask.substring(0,temp_mask.length()-1) + post_mask;
         else
            temp_mask +=",";
         groups[count++]=temp_mask;
      }

      for (int i=0;i<count;i++)
         mask = groups[i] + mask;
      if (DEBUG)
         debug("Mask =" + mask);

      return mask;

   }



   /**
    * Returns the reference to the global Factory object instantiated
    * at startup. It is NOT a public method.
    */
   Factory getFactory()
   {
      return getASPConfig().getFactory();
   }

   /**
    * Returns the default protocol for the site, for example "http"
    */
   public String getProtocol()
   {
      try
      {
         return getASPConfig().getProtocol();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  Public methods for translation of texts in ASP scripts
   //==========================================================================

   /**
    * The translate() functions return the translation of a translatable constant
    * and optionally insert parameters in the translation.
    * <p>
    * A translatable constant is a constant which's value begins with the constant name,
    * a colon, and a space.
    *<p>
    * A constant name must begin with an uppercase english letter or underscore sign (_)
    * and can only consists of uppercase english letters, digits or underscore signs.
    * For example "TEXT_HELLO: Hello world!"
    * <p>
    * This function works by replacing placeholders in the constant text with
    * the values of the replacement parameters. A placeholders is an apersand (&)
    * character followed by a number. The number indicates what parameter value should
    * replace the placeholder. For example &1 is replaced by p1, &2 by p2 and &3 by p3.
    */
   public String translate( String text )
   {
      return translate( JAVACODE, text, null, null, null, 0, null  );
   }


   public String translate( String text, String p1 )
   {
      return translate( JAVACODE, text, p1, null, null, 1, null );
   }


   public String translate( String text, String p1, String p2 )
   {
      return translate( JAVACODE, text, p1, p2, null, 2, null );
   }


   public String translate( String text, String p1, String p2, String p3 )
   {
      return translate( JAVACODE, text, p1, p2, p3, 3, null );
   }

   public String translate( String text, ASPPage page )
   {
      return translate( JAVACODE, text, null, null, null, 0, page  );
   }


   private String asp_file_name = null;

   /**
    * Overrides name of the current ASP script for using with translations.
    * Should be called at the beginning of include files.
    * The input parameter is the file name without the extension.
    * Can be preceded by module name in uppercase and a dot.
    *
    * @see ifs.fnd.asp.ASPManager#clearLanguageContext
    * @deprecated
    */
   public void setLanguageContext( String filename )
   {
      try
      {
         if ( filename.indexOf(".") < 0 )
         {
            String f = getDicPageKey();
            asp_file_name = f.substring(0, f.indexOf(".")+1) + filename;
         }
         else
            asp_file_name = filename;
         if ( DEBUG ) debug("ASPManager: Language context set to : "+asp_file_name);
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }


   /**
    * Sets back the original name of the current ASP script for using with translations.
    * Should be called at the end of include files.
    *
    * @see ifs.fnd.asp.ASPManager#setLanguageContext
    * @deprecated
    */
   public void clearLanguageContext()
   {
      asp_file_name = null;
      if ( DEBUG ) debug("ASPManager: Language context cleared.");
   }

   /**
    * Returns the page mask tag.
    */
   public String generatePageMaskTag(){
        return getASPConfig().getPageMaskTag();
   }

   /**
    * Return name of the current ASP script prefixed with the module name in uppercase.
    * @deprecated
    */
   private String getDicPageKey()
   {
      try
      {
         if ( !Str.isEmpty(asp_file_name) ) return asp_file_name;
         if ( asppage.getASPPortal() != null ) return JAVACODE;
         return asppage.getDicPageKey();
      }
      catch ( Throwable e )
      {
         error(e);
         return null;
      }
   }

   //==========================================================================
   //  Public methods for translation of texts inside Java code
   //==========================================================================

   private static final String JAVACODE = "__Java";

   /**
    * Same as translate() but for using inside the Java source code.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   public String translateJavaText( String text )
   {
      return translate( JAVACODE, text, null, null, null, 0, null );
   }


   public String translateJavaText( String text, String p1 )
   {
      return translate( JAVACODE, text, p1, null, null, 1, null );
   }


   public String translateJavaText( String text, String p1, String p2 )
   {
      return translate( JAVACODE, text, p1, p2, null, 2, null );
   }


   public String translateJavaText( String text, String p1, String p2, String p3 )
   {
      return translate( JAVACODE, text, p1, p2, p3, 3, null );
   }

   //==========================================================================
   //  Private methods for translation
   //==========================================================================

   private static final String KEYSEP1 = ".Messages.";
   private static final String KEYSEP2 = ".Text";

   //private Boolean encode_translations = null;

   /**
    * The main translation routine. Looks up the topic in the dictionary file and returns
    * the translation with replacement of eventually parameters.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   private String translate( String master_key, String text,
                             String p1, String p2, String p3, int param_cnt, ASPPage page )
   {
      String trs_text = null;

      try
      {
         if ( Str.isEmpty(text) ) return text;

         int index = text.indexOf(": ");
         String topic = index<0 ? null : text.substring(0,index);

         if ( index<=0 || !IfsNames.isUpperCaseId(topic) )
            index = 0;
         else
            index += 2;

         if ( index > 0 && !Str.isEmpty(master_key) )
         {
            if ( JAVACODE.equals(master_key) )
            {
               trs_text = getDicItem(topic, page);
               if ( Str.isEmpty(trs_text) )
                  trs_text = getDicItem("JAVA."+topic, page);
            }
            else
            {
               trs_text = getDicItem(master_key+KEYSEP1+topic+KEYSEP2, page);
               int p;
               if ( Str.isEmpty(trs_text) && (p=master_key.indexOf("."))>0 )
               {
                  master_key = master_key.substring( p+1 );
                  trs_text = getDicItem(master_key+KEYSEP1+topic+KEYSEP2, page);
               }
            }
         }

         if ( Str.isEmpty(trs_text) ) trs_text = text.substring(index);
      }
      catch ( Throwable e )
      {
         trs_text = text;
         logError(e,false);
      }

      try
      {
         switch ( param_cnt )
         {
         case 3 : trs_text = Str.replace(trs_text, "&3", ""+p3);
         case 2 : trs_text = Str.replace(trs_text, "&2", ""+p2);
         case 1 : trs_text = Str.replace(trs_text, "&1", ""+p1);
         }
      }
      catch ( Throwable e )
      {
         trs_text = text;
         logError(e,false);
      }

      try
      {
         if ( getASPConfig().encodeTranslations() )
            return HTMLEncode(trs_text);
         else
            return trs_text;
      }
      catch ( Throwable e )
      {
         logError(e,false);
         return trs_text;
      }
   }


    private static final String JS_PREFIX = "JAVASCRIPT_";

   /**
    * Same as translate() but to be used when generating JavaScript code.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */

   public String translateJavaScript( String text )
   {
      return translateJavaScript( text, null, null, null, 0 );
   }


   public String translateJavaScript( String text, String p1 )
   {
      return translateJavaScript( text, p1, null, null, 1 );
   }


   public String translateJavaScript( String text, String p1, String p2 )
   {
      return translateJavaScript( text, p1, p2, null, 2 );
   }


   public String translateJavaScript( String text, String p1, String p2, String p3 )
   {
      return translateJavaScript( text, p1, p2, p3, 3 );
   }


   private String translateJavaScript( String text, String p1, String p2, String p3, int param_cnt )
   {

      String trs_text = null;
      String trs_unescaped =null;

      try
      {
         if ( Str.isEmpty(text) ) return text;


         int index = text.indexOf(": ");
         String topic = index<0 ? null : text.substring(0,index);
         String js_topic = JS_PREFIX + topic;

         if ( index<=0 || !IfsNames.isUpperCaseId(topic) )
            index = 0;
         else
            index += 2;

         if ( index > 0 )
         {
            trs_text = getDicItem(js_topic);

            if ( Str.isEmpty(trs_text) )
            {
                trs_unescaped = getDicItem(topic);

                if (!Str.isEmpty(trs_unescaped))
                {
                    trs_text = JScriptEncode(jsEncodeQuotes(trs_unescaped));
                    ASPConfig cfg = getASPConfig();
                    DictionaryCache.addTranslation(getUserLanguage(), js_topic,trs_text);
                }
            }

         }

         if ( Str.isEmpty(trs_text) ) trs_text = JScriptEncode(jsEncodeQuotes(text.substring(index)));
      }
      catch ( Throwable e )
      {
         trs_text = text;
         logError(e,false);
      }

      try
      {
         switch ( param_cnt )
         {
         case 3 : trs_text = Str.replace(trs_text, "&3", ""+ jsEncodeQuotes(p3));
         case 2 : trs_text = Str.replace(trs_text, "&2", ""+ jsEncodeQuotes(p2));
         case 1 : trs_text = Str.replace(trs_text, "&1", ""+ jsEncodeQuotes(p1));
         }
      }
      catch ( Throwable e )
      {
         trs_text = text;
         logError(e,false);
      }

       return trs_text;

   }

   String jsEncodeQuotes(String text)
   {

       try
       {
         if ( text==null ) return null;
         if (( text.indexOf('\'')<0 ) && ( text.indexOf('\"')<0 )) return text;

         int len = text.length();
         AutoString buf = new AutoString(len);
         char ch;

         for ( int i=0; i<len; i++ )
         {
           switch ( ch=text.charAt(i) )
           {

            case '\'':
              if (i==0)
              {
               buf.append('\\');
               buf.append('\'');
              }
              else if (text.charAt(i-1)!='\\')
              {
               buf.append('\\');
               buf.append('\'');
              }
              else
               buf.append(ch); //already encoded
              break;

            case '\"':
              if (i==0)
              {
               buf.append('\\');
               buf.append('\"');
              }
              else if (text.charAt(i-1)!='\\')
              {
               buf.append('\\');
               buf.append('\"');
              }
              else
               buf.append(ch); //already encoded
              break;

            default:
               buf.append(ch);
            }
         }
         return buf.toString();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }

   }

   private static final int MAXLENGTH = 10000;
   private StringBuffer dic_value = new StringBuffer(MAXLENGTH);

   private String usage_key = null;
   private String usage_session_key = null;
   private static Hashtable frame_pages_key_collection = new Hashtable();

   String getUsageKey()
   {
      return usage_key;
   }

   String getUsageSessionKey()
   {
      return usage_session_key;
   }

   /*
    * Only called when a code part label is set to a value other than the value set in
    * preDefine(). The initial labels such as @A, @B, @C are normally modified by the
    * application at runtime. Called by ASPField.setLabel() when the code part label changes.
    * Only used when generating "Help On Fields".
    */
   void addCodePartLabels(String dic_key, String label )
   {
      if (DEBUG) debug("ASPManager.addCodePartLabels( "+dic_key+", "+label+" )");

      Hashtable code_part_labels = (Hashtable)(getAspSession().getAttribute(CODE_PART_LABELS));
      if (code_part_labels==null)
        code_part_labels = new Hashtable();

      code_part_labels.put(dic_key, label);
      getAspSession().setAttribute(CODE_PART_LABELS,code_part_labels);
   }

   /**
    * Returns a hashtable containing code part label set by the application. The keys of the
    * hashtable contains the translate constant for each label. Only Used when generating
    * "Help On Fields".
    */
   public Hashtable getCodePartLabels()
   {
      Hashtable code_part_labels = (Hashtable)(getAspSession().getAttribute(CODE_PART_LABELS));
      return code_part_labels;
   }
   /**
    * Returns a Set of usage terms defined outside preDefine.
    * @param usagekey usagekey (page poolkey)
    */
   public Set getUsageIdSet(String usagekey)
   {
      if (usagekey == null)
         return null;
      else
      {
         HttpSession sess1 = getAspSession();
         return (Set)sess1.getAttribute(usagekey + "_" + DIRTY_LABELS);
      }
   }

   /**
    * Returns a Set of usage terms defined inside a multi frame page.
    * The key is the page poolkey of the main frame page
    * @param usagekey usagekey (page poolkey)
    */
   public static Set getFrameUsageIdSet(String usagekey)
   {
      if (usagekey == null)
         return null;
      else
         return (Set)frame_pages_key_collection.get(usagekey);
   }


   /**
    * Returns a Set of usage terms defined in preDefine.
    * ex: labels of ASPFields
    * @param url page poolkey
    */
   public Set getPageUsages( String url )
   {
      if ( DEBUG ) debug("ASPManager.getPageUsages("+url+")");
      try
      {
         ASPPageHandle page_handle = ASPPagePool.getASPPageHandle(this,url,asppage);
         if ( page_handle!=null )
         {
            ASPPage page = page_handle.getASPPage();
            if ( DEBUG ) debug("ASPManager: ASPPage Usages \""+url+"\" ("+page.getStateName()+") found in pool.");
            Set usage_set = page.getPageKeys();
            ASPPagePool.unlock(page_handle,this,asppage);

            return usage_set;
         }
         else
            throw new FndException("FNDMGRPNOTF: Page '&1' not found in Page Pool",url);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Return text from a dictionary file for given key.
    */
   private String getDicItem( String key )
   {
      return getDicItem( key, null );
   }

   private String getDicItem( String key, ASPPage page )
   {
      try
      {
         String user_language_code = getUserLanguage();
         ASPConfig cfg = getASPConfig();
         //if ( cfg.useWebKitDic() )
         //{
         //   String dickey = user_language_code.toUpperCase()+"_"+key;
         //   String rval = cfg.getDicValue(dickey);
         //   String usage_id = cfg.getUsageID(dickey);

            String rval = cfg.getTranslation(user_language_code,key);
            String usage_id = cfg.getUsageVersionID(user_language_code,key);

            if (usage_session_key == null)
                usage_session_key = ""+System.currentTimeMillis();

           String portlet_class = null;
           if (isPortalPage())
           {
             portlet_class = getPortalPage().getASPPortal().getPortletUsagekey();
             usage_key = portlet_class;

             if (Str.isEmpty(usage_key))
                usage_key = getPortalPage().getPoolKey(); //default page
           }
           else
              usage_key = getASPPage().getPoolKey();


            if ( (page != null) && (! Str.isEmpty(usage_id))) //Usage_Version_Id
            {
               //If the page is put into a frameset, append the
               //key set to the parent frame only
               if(page.isParentFramePagename())
               {
                   String parent_frame = page.getParentFramePagename();
                   String path = cfg.getApplicationPath();
                   String parent_pool_key = path+parent_frame+"-"+user_language_code+"-"+FRAME_REF;

                   synchronized(frame_pages_key_collection)
                   {
                       Set parent_set = (Set)(frame_pages_key_collection.get(parent_pool_key)) ;
                       if (parent_set == null)
                           parent_set = new HashSet();
                       parent_set.add(key);
                       frame_pages_key_collection.put(parent_pool_key, parent_set);
                   }
               }
               else
               {
                   if (page.isUndefined())
               {
                  //add terms in predefine (ex: ASPField labels)
                  //page.addUsageID(usage_id);
                  page.addPageKeys(key);
               }
               else
               {
                     HttpSession session = getAspSession();
                    Set set = (Set)session.getAttribute(usage_key + "_"+ DIRTY_LABELS);
                     if (set == null)
                        set = new HashSet();

                     set.add(key);
                        session.setAttribute(usage_key + "_"+ DIRTY_LABELS,  set);
                  }
               }
            }
            return rval;
         //}
         //else
         //   return null;

      }
      catch ( Throwable e )
      {
         logError(e,false);
         return null;
      }
   }

   //==========================================================================
   //  Temporary files
   //==========================================================================

   private int temp_file_seq;
   private String temp_path;

   private void initTempPath() throws Exception
   {
      if ( temp_path==null )
         temp_path = getASPConfig().getApplicationTempPath() + File.separator;
   }

   /**
    * Returns a unique filename that can be used for temporary storage.
    * The file will be located in the temporary
    * directory defined by the ASPConfig parameter TEMP_PATH.
    */
   public String getTempFileName()
   {
      try
      {
         initTempPath();
         return temp_path + getSessionId() + (temp_file_seq++) + ".tmp";
      }
      catch ( Throwable e )
      {
         error(e);
         return null;
      }
   }

   /**
    * Deletes the specified file, which must be located in the temporary
    * directory defined by the ASPConfig parameter TEMP_PATH.
    */
   public void removeTempFile( String filename )
   {
      try
      {
         initTempPath();

         if ( !filename.startsWith(temp_path) )
            throw new FndException("FNDMGRPATHER: Only files in TEMP_PATH can be removed: &1", filename);

         File f = new File(filename);

         if ( f.exists() )
         {
            if ( DEBUG ) debug("Deleting file "+filename);
            f.delete();
            if ( f.exists() )
               log("Could NOT delete the file "+filename);
         }
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }

   //==========================================================================
   //  File and URL access
   //==========================================================================

   /**
    * Read the file with the specified name, remove trailing blanks from every
    * line, and return the resulting contents as one String.
    */
   public String readAndTrimFile( String filename )
   {
      try
      {
         return Util.readAndTrimFile(filename);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   private static Hashtable pres_objects = null;

   /*
   private static void searchClassPath(File dir, String pkg, Class prvcls)
   {
      if(DEBUG) Util.debug("ASPManager.searchClassPath()");
      File[] files = dir.listFiles();
      for (int i=0; i < files.length; i++ )
      {
         File file = files[i];

         if (file.isDirectory())
            searchClassPath(file, pkg+file.getName()+".", prvcls);
         else
         {
            String name = file.getName();
            if (file.isFile() && name.endsWith(".class") && name.indexOf("$")<0)
            {
               try
               {
                  name = pkg+name.substring(0,name.length()-6);
                  Class cls = Class.forName(name);
                  if(DEBUG) Util.debug("Found candidate: "+cls);
                  if(cls.isInterface())                        continue;
                  if(Modifier.isAbstract(cls.getModifiers()))  continue;
                  if(!prvcls.isAssignableFrom(cls))            continue;

                  String clsname = cls.getName();
                  available_pages.setItem(clsname, null);
                  if(DEBUG) Util.debug("Found page: "+clsname);
               }
               catch( Throwable any )
               {
                  if(DEBUG) Util.debug(any+" [path="+file.getPath()+" clsname="+name+"]");
               }
            }
         }
      }
   }
   */

   private void initAvailableList()
   {
      if ( DEBUG ) Util.debug("ASPManager.initAvailableList()");

      if ( pres_objects == null )
         pres_objects = ((RequestHandler)servlet).getPresentationObjectsList();

      /*
      if (available_pages != null) return;

      Class prvcls    = Class.forName("ifs.fnd.asp.ASPPageProvider");
      available_pages = config.getFactory().getBuffer();
      String clspath  = System.getProperty("java.class.path");
      clspath = config.getApplicationPhyPath()+"WEB-INF"+ifs.fnd.os.OSInfo.OS_SEPARATOR+
                "classes"+";"+(clspath==null ? "" : clspath);

      if(DEBUG) Util.debug("  classpath="+clspath);

      StringTokenizer pst = new StringTokenizer(clspath, ";");

      while( pst.hasMoreTokens() )
      {
         File dir = new File(pst.nextToken());
         if( dir.exists() && dir.isDirectory() )
            searchClassPath(dir, "", prvcls);
      }
      */
   }


   /**
    * Check if the given presentation object (an ASP-page or a portlet)
    * is installed on the system/site. Return true if so.
    */
   public boolean isPresentationObjectInstalled( String pres_obj_id )
   {
      initAvailableList();

      try
      {
         if ( Str.isEmpty(pres_obj_id) )
            throw new FndException("FNDMGRPAREMP: Parameter is empty");

         if ( pres_obj_id.indexOf("/") > 0 ) //page is given as input parameter
         {
            if ( pres_obj_id.endsWith(".asp") || pres_obj_id.indexOf(".page")!=-1)
               pres_obj_id = pres_obj_id.substring(0, pres_obj_id.lastIndexOf('.'));

            int i = pres_obj_id.lastIndexOf('/');
            if ( i > -1 )
            {
               String module_name = pres_obj_id.substring(0,i).toLowerCase();
               if ("fnd".equals(module_name)) //required for web client pages
                   module_name+=".pages";
               pres_obj_id = ("ifs."+module_name+pres_obj_id.substring(i)).replace('/','.');
            }
         }
         else if ( !pres_obj_id.startsWith("ifs.") )  // portlet is not given as input parameter
            throw new FndException("FNDMGRPARINV: Parameter &1 is not valid name for page or portlet", pres_obj_id);

         String value = (String)pres_objects.get(pres_obj_id);

         if ( value == null )
         {
            return false;
         }
         /*
         else if (value == "Y")
         {
            return true;
         }
         */
         else
         {
            /*
            Class cls = Class.forName(pres_obj_id);
            if(cls.isInterface())                        return false;
            if(Modifier.isAbstract(cls.getModifiers()))  return false;

            try
            {
               Class pagecls    = Class.forName("ifs.fnd.asp.ASPPageProvider");
               if(!pagecls.isAssignableFrom(cls))
               {
                  Class portletcls = Class.forName("ifs.fnd.asp.ASPPortletProvider");
                  if (!portletcls.isAssignableFrom(cls)) return false;
               }
            }
            catch(ClassNotFoundException any)
            {
               System.err.println("Can't find ifs.fnd.asp.ASPPageProvider or ifs.fnd.asp.ASPPortletProvider");
            }

            pres_objects.put(pres_obj_id, "Y");
            */
            return true;
         }
      }
      catch ( Throwable any )
      {
         error(any);
         return false;
      }

   }


   /**
    * Write the specified string to a file with the specified name.
    */
   public void writeFile( String filename, String str )
   {
      try
      {
         Util.writeFile(filename,str);
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }



   //==========================================================================
   //  Access to ASP Reaquest and Response objects
   //==========================================================================

   /**
    * Returns a string that identifies the browser used by the client
    */
   protected String getUserAgent()
   {
      //return server_variables.getString("HTTP_USER_AGENT");
      return asp_request.getHeader("User-Agent");
   }

   /**
    * Returns true, if the current browser is Mozilla5.0 compatible.
    *@return : Win-NE7, Lin-NE7 and Lin-Moz will all return true. Otherwise false.
    */
   public boolean isMozilla()
   {
      return is_mozilla;
   }

   /**
    * Returns true, if the current browser is MS Internet Explorer
    */
   public boolean isExplorer()
   {
      return is_explorer;
   }

  /**
    * Returns true, if the current browser is Netscape 6
    *@deprecate: Use isMozilla() instead.
    */
   public boolean isNetscape6()
   {
      return is_netscape6;
   }

   /**
   * Returns true, if the current browser is Netscape 4x. Else returns false.
   */
   public boolean isNetscape4x()
   {
      return is_netscape4x;
   }

   /**
    * Returns the name of a browser used by the client
    */
   public String getBrowserName()
   {
      String ua = getUserAgent();
      return ua;
   }


   /**
    * Returns the version of a browser used by the client
    */
   protected String getBrowserVersion()
   {
      String ua = getUserAgent();
      return ua;
   }
   
   /**
   * Returns any extra path information after the servlet name but before the query string,
   * and translates it to a real path
   */
   public String getPathTranslated()
   {
      try
      {
         return asp_request.getPathTranslated();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Returns the HTTP method, for example GET, POST.
    */
   public String getRequestMethod()
   {
      try
      {
         return asp_request.getMethod();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Returns true, if the current page is a jsp page.
    */
   public boolean isJSPPage()
   {
      return jsp;
   }

   /**
    * Returns true, if the current session is valid.
    */
   public boolean isSessionValid()
   {
      return  (asp_session.getAttribute("is_valid")==null)?false:true;
   }

   /**
   * Returns the length (in bytes) of the request body, or -1 if the lenght is not known.
   */
   public int getRequestLength()
   {
      try
      {
         return asp_request.getContentLength();
      }
      catch ( Throwable any )
      {
         error(any);
         return 0;
      }
   }

   /**
    * Returns the character encoding of the request body
    */
   public String getRequestCharacterEncoding()
   {
      try
      {
         return asp_request.getCharacterEncoding();
      }
      catch ( Throwable any )
      {
         error( any );
         return null;
      }
   }

   /**
    * Returns an InputStream of the request body
    */
   public InputStream getRequestBodyAsInputStream()
   {
      try
      {
         return asp_request.getInputStream();
      }
      catch ( Throwable any )
      {
         error( any );
         return null;
      }
   }

   /**
   * Returns the content type of the request body
   */
   public String getRequestContentType()
   {
      try
      {
         return asp_request.getContentType();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
   * Returns the name and version of the web server software.
   */
   public String getServerInfo()
   {
      try
      {
         return asp_application.getServerInfo();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Write the specified string to the ASP Response object.
    */
   public void responseWrite( String text )
   {
      try
      {
         String __context_id = getQueryStringValue("__CONTEXT_ID");
         String __update_context = getQueryStringValue("__UPDATE_CONTEXT");
         
         //To put changes back into the context cache if the context_id is sent in an AJAX call.
         if(!isEmpty(__update_context) && __update_context.equals("Y") && !isEmpty(__context_id) && !__context_id.equals("*"))
              asppage.generateClientScript();
         writeResponse(text);
      }
      catch ( AspContextException x )
      {
         error(x);
      }
   }


   private boolean end_response  = false;

   void writeResponse( String text ) throws AspContextException
   {
      addCookies();
      try
      {
         if ( DEBUG ) debug("ASPManager.writeResponse("+text+")");
                  if ( asppage instanceof MobilePageProvider && !is_lov_or_navigator )
         {
            end_response = true;
            ((MobilePageProvider)asppage).getOutputStream().append(text);
         }
         else if ( asppage instanceof ASPPageProvider && !is_lov_or_navigator )
         {
            end_response = true;
            ((ASPPageProvider)asppage).getOutputStream().append(text);
         }
         else
         {
             if ( canGZIP(text.length() ,getAspRequest().getRemoteAddr()) )
             {
                 end_response = false;
                 responseGZIP(text);
             }
             else
                 getAspResponse().getWriter().write( text );
         }
      }
      catch ( Throwable any )
      {
         throw new AspContextException(any);
      }
   }

   public void responseWrite( byte[] bytes )
   {
      try
      {
         writeResponse(bytes);
      }
      catch ( AspContextException x )
      {
         error(x);
      }
   }

   void writeResponse( byte[] bytes ) throws AspContextException
   {
      addCookies();
      try
      {
         binary_output=true; //Prevent endResponse to use getWriter().
         if ( DEBUG ) debug("ASPManager.writeResponse("+bytes+")");
         ServletOutputStream out = getAspResponse().getOutputStream();
         if ( bytes==null || bytes.length==0 ) return;
         out.write(bytes);
      }
      catch ( Throwable any )
      {
         throw new AspContextException(any);
      }
   }


   /**
    * Clear the ASP Response object.
    */
   public void responseClear()
   {
      try
      {
         clearResponse();
      }
      catch ( AspContextException any )
      {
         error(any);
      }
   }

   void clearResponse() throws AspContextException
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.clearResponse()");
         if ( asppage instanceof ASPPageProvider )
         {
            end_response = true;
            ((ASPPageProvider)asppage).getOutputStream().clear();
         }
         getAspResponse().resetBuffer();//clear();
         setAspResponsContentType("text/html;charset=utf-8");
      }
      catch ( Throwable any )
      {
         throw new AspContextException(any);
      }
   }


   /**
    * Finish the execution of the current request and send the response
    * to the client.
    */
   public void endResponse()
   {
      if(asppage instanceof MobilePageProvider)
      {
         endMobileResponse();
         return;
      }

      addCookies();
      if(DEBUG) debug("ASPManager.endResponse()");
      try
      {
         HttpServletResponse resp = getAspResponse();
         end_response = false;
         boolean did_zip=false;
         if(asppage instanceof ASPPageProvider)
         {
             boolean is_validate = !isEmpty(getQueryStringValue("VALIDATE"));

             //getAspRequest().getRemoteAddr() doesn't seem work with JSR 168
             if(!binary_output && canGZIP( (((ASPPageProvider)asppage).getOutputStream().toString()).length() ,getAspRequest().getRemoteAddr()))
             {
                 responseGZIP( ((ASPPageProvider)asppage).getOutputStream().toString() );
                 did_zip=true;
             }
             else if(!binary_output)
             {
                if(( ASPPage.IFRAME_VALIDATION.equals(asppage.getValidationMethod()) ) && (isExplorer()) && (is_validate))
                {
                   //Bug 41996, start
                   String endiftext = "--></textarea>\n</form>\n";
                   String endtags = "</BODY></HTML>";
                   resp.getWriter().write(getIframeScript() + ((ASPPageProvider)asppage).getOutputStream().toString()+endiftext+endtags);
                   //Bug 41996, end
                }
                else
                {
                   if (is_validate) resp.setHeader("Content-Type", "application/octet-stream");
                   resp.getWriter().write(((ASPPageProvider)asppage).getOutputStream().toString());
                }
            }
            if(DEBUG) debug(this+": Aborting execution by throwing ResponseEndException ...");
            if(!did_zip)
                resp.flushBuffer();//end();
            throw new ResponseEndException();
         }
         resp.flushBuffer();//end();
      }
      catch( ResponseEndException x )
      {
         throw x;
      }
      catch( Throwable any )
      {
         error(new AspContextException(any));
      }
   }

  private String getIframeScript()
  {
     StringBuffer str = new StringBuffer();
     str.append("<HTML>");
     str.append("<HEAD>");
     //Bug 41996, start
     str.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
     //Bug 41996, end
     str.append("<script language= javascript >");
     str.append("function onLoad()");
     str.append("{");
     str.append(" parent.setiframeloaded();");
     str.append("}");
     str.append("</script>");
     str.append("</HEAD>");
     str.append("<BODY OnLoad=\"javascript:onLoad()\" >");
     //Bug 41996, start
     str.append("\n<form name=ifform>\n<textarea name=iftxt id=iftxt><!--");
     //bug 41996, end
     return str.toString();
  }


   private void responseGZIP(String str_response) throws IOException
   {
       if(DEBUG) debug("ASPManager.responseGZIP()");

       asp_response.setHeader("Content-Encoding","gzip");

       GZIPOutputStream gzip_stream = new GZIPOutputStream(asp_response.getOutputStream());
       byte[] byte_array = str_response.getBytes("UTF8");
       gzip_stream.write(byte_array,0,byte_array.length);
       gzip_stream.close();
   }

   void setEndResponse()
   {
      end_response = true;
   }


//    private RequestDictionary request_form;
//    private RequestDictionary request_query_string;


   private Hashtable parameters = null;

   private void initRequest()
   {
   }

   /**
    * Reurns decoded string value using UTF8 character encoding.
    */
   private String decodeValue(String value)
   {
      if ( DEBUG ) debug("ASPManager.decodeValue()");

      if ( value==null ) return null;

      int length = value.length();

      if ( signs.length<length )
      {
         int extsize = 2*signs.length;
         int newsize = length>extsize ? length : extsize;

         byte[] newsigns =  new byte[newsize];

         System.arraycopy(signs,0,newsigns,0,signs.length);
         signs = newsigns;
      }

      for ( int i=0; i<length; i++ )
         signs[i] = (byte)(value.charAt(i));

      try
      {
         String ret = new String(signs, 0, length, "UTF8");

         return HTMLDecode(ret);
      }
      catch ( UnsupportedEncodingException e )
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * Reurns decoded table of string values using UTF8 character encoding.
    */
   private String[] decodeValues(String[] values)
   {
      if ( DEBUG ) debug("ASPManager.decodeValues()");

      for ( int i=0; i<values.length;i++ )
      {
         values[i] = decodeValue(values[i]);
      }
      return values;
   }

   public String readAbsoluteValue( String name )
   {
      return readAbsoluteValue(name,null);
   }

   public String readAbsoluteValue( String name, String default_value )
   {
      return readValue(name,default_value,true, null);
   }

   /**
    * Read one value from the ASP Request object. Return the value of:
    *<pre>
    *  1. the named QueryString parameter, or if it does not exist
    *  2. the named HTML field in the current ASP Request
    *</pre>
    * Return null, if no such parameter and no such field has been found.
    */
   public String readValue( String name )
   {
      return readValue(name,null);
   }

   /**
    * Read one value from the ASP Request object. Return the value of:
    *<pre>
    *  1. the named QueryString parameter, or if it does not exist
    *  2. the named HTML field in the current ASP Request
    *</pre>
    * Return the specified default value, if no such parameter and no such
    * field has been found.
    */
   public String readValue( String name, String default_value )
   {
      return readValue(name,default_value,false, null);
   }

   String readValue( String name, String default_value, boolean absolute, ASPPage page )
   {
      if ( DEBUG ) debug(this+": ASPManager.readValue("+name+","+default_value+")");
      try
      {
         initRequest();

         //
         //  1. QueryString (due to different handling of Unicodes)
         //
         String param = getQueryStringValue(name);

         if(!Str.isEmpty(param) && hasCSVNames(param.trim()) && !absolute)
         {
                page = ((page==null)?asppage:page);
             ASPField field = null;
             if (page!=null)
             {
                String f = Str.replace(name, page.addProviderPrefix(), "");
                if(page.isASPField(f))
                   field = page.getASPField(f);
             }
             param = getCSVValue(param, default_value, field);
         }

         if ( !Str.isEmpty(param) ) return param.trim();

         //
         //  2. Form
         //
         try
         {
             param = asp_request.getParameter(name);
         }
         catch(Throwable any )
         {
            param = null;
         }

         if(!Str.isEmpty(param) && hasCSVNames(param.trim()) && !absolute)
         {
             page = ((page==null)?asppage:page);
             ASPField field = null;
             if (page!=null)
             {
                String f = Str.replace(name, page.addProviderPrefix(), "");
                if(page.isASPField(f))
                   field = page.getASPField(f);
             }
             param = getCSVValue(param, default_value, field);
         }

         String validation_method = getQueryStringValue("VALIDATION_METHOD");
         //IF AN AJAX CALL WITH POST METHOD, THEN URLDECODE, ELSE JUST PASS
         if( ("POST").equals(validation_method) )
            return Str.isEmpty(param) ? default_value : URLDecode(decodeValue(param));
         else
            return Str.isEmpty(param) ? default_value : decodeValue(param);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return the value at the specified position (0,1,2,...) in the value list
    * for the the named HTML field in the current ASP Request.
    */
   public String readValueAt( String name, int position )
   {
      try
      {
         if ( position<=0 ) return readValue(name);
         initRequest();

         //
         //  1. QueryString (due to different handling of Unicodes)
         //
         String[] params = getQueryStringValues(name);
         if ( params!=null && params.length>position && !Str.isEmpty(params[position]) ) return params[position];

         //
         //  2. Form
         //
         params = asp_request.getParameterValues(name);
         return params==null || params.length<=position ? null : decodeValue(params[position]);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return an array with all values of the named HTML field/column
    * in the current ASP Request,
    */
   public String[] readValues( String name )
   {
      try
      {
         initRequest();

         //
         //  1. QueryString (due to different handling of Unicodes)
         //
         String[] params = getQueryStringValues(name);
         if ( params!=null ) return params;

         //
         //  2. Form
         //
         params = asp_request.getParameterValues(name);
         return params==null ? null : decodeValues(params);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Create an URL that points to the current ASP page and contains QueryString
    * parameters for every non empty HTML field (ASPField) from the specified
    * ASPBlock.
    * The URL will contain an extra parameter named "SEARCH" without any value.
    */
   public String createSearchURL( ASPBlock block )
   {
      ASPContext ctx = block.getASPPage().getASPContext();
      AutoString buf = new AutoString();
      buf.append("?SEARCH=Y");
      ASPField[] fields = block.getFields();
      for ( int i=0; i<fields.length; i++ )
      {
         ASPField f = fields[i];
         if(f.isImageField() && !f.hasTemplate()) continue;
         String name = f.getName();
         String value;
         if (f.hasGlobalConnection())
            value = f.getGlobalValue();
         else
            value = readAbsoluteValue(name);
         if ( !Str.isEmpty(value) )
         {
            buf.append( "&" + name + "=");
            buf.append(URLEncode(value));
         }
      }
      String param = buf.toString();
      String base = getURL();
      ctx.writeValue("__SEARCHPARAM", param);
      return base+param;
   }

   void appendSearchURL(AutoString code, ASPPage page)
   {
      ASPContext ctx = page.getASPContext();
      String searchparam     = ctx.readValue("__SEARCHPARAM","");
      String currquerystring = isEmpty(getQueryString()) ? "":"?"+HTMLDecode(getQueryString());
      boolean casesensitive  = "TRUE".equals(readValue("CASESENCETIVE","").toUpperCase());
      if(!casesensitive) casesensitive  = "Y".equals(readValue("__CASESS_VALUE", getConfigParameter("ADMIN/CASE_SENSITIVE_SEARCH","Y")));
            
      if(!isEmpty(searchparam))
      {
         if(currquerystring.indexOf(searchparam)==-1)
            currquerystring = currquerystring.replace('?','&');
         else
            searchparam = "";
      }
      else
         searchparam = "";
      
      searchparam += currquerystring;
      ctx.writeValue("__SEARCHPARAM",searchparam);

      code.append(ASPPage.BEGIN_SCRIPT_TAG);
      code.append("SEARCHBASE = \"");
      code.append(getURL());
      code.append("\";\n");
      
      code.append("SEARCHPARAM =\"",searchparam,"\";\n");
      code.append("HEADTITLE = \"", HTMLEncode(toolbar_title), "\";\n");
      code.append("CASESENCETIVE = \"&CASESENCETIVE=", casesensitive?"TRUE":"FALSE" , "\";\n");
      code.append(ASPPage.END_SCRIPT_TAG);
   }

   /**
    * Read one value from the ASP Request/Form object calling the function readValue(String)
    * and then convert it into a number using the default Number/Currency formatter.
    * Return NaN (Not-a-Number) value, if no value has been found.
    * @param name parameter name
    * @see readNumberValue( String,ASPPage)
    */

   public double readNumberValue( String name )
   {
      return readNumberValue(name,null);
   }

   /**
    * Read one value from the ASP Request/Form object calling the function readValue(String)
    * and then convert it into a number using the default Number/Currency formatter. This method
    * is to be used in portlets. Return NaN (Not-a-Number) value, if no value has been found.
    * @param name parameter name
    * @param page ASPPage where the specific filed located. From portlets just send 'this'
    * @see readNumberValue( String)
    */
   public double readNumberValue( String name, ASPPage page)
   {
      try
      {
         String text = readValue(name);
         if ( Str.isEmpty(text) ) return NOT_A_NUMBER;
         int type = DataFormatter.NUMBER;

         ASPField f=null;
         if (page==null && !isPortalPage())
            page = getASPPage();
         if (page!=null && page.hasASPField(name))
            f = getASPField(name,page);
         if (f!=null && (f.getTypeId()==DataFormatter.NUMBER || f.getTypeId()==DataFormatter.MONEY ))
            type = f.getTypeId(); //could be MONEY or NUMBER

         NumberFormatter fmt = (NumberFormatter)getDataFormatter(DataFormatter.NUMBER,null);
         fmt.setDecimalSeparator(getDecimalSeparator(type));
         fmt.setGroupingSeparator(getGroupSeparator(type));
         Number num = (Number)fmt.parse(text);
         return num.doubleValue();
      }
      catch ( Throwable e )
      {
         logError(e);
         return NOT_A_NUMBER;
      }
   }

   /**
    * Convert a numeric value (or JScript variable) to the client format
    * using the datatype and format mask of the named ASPField.
    *
    * Note! This function returns an empty String ("") if the specified
    * value is so called NaN (Not-a-Number) value.
    */
   public String formatNumber( String field_name, double value )
   {
      try
      {
         return asppage.getASPField(field_name).formatNumber(value);
      }
      catch ( Throwable e )
      {
         logError(e);
         return null;
      }
   }

   /**
    * Convert given String into a numeric value (or JScript variable)
    * using the datatype and format mask of the named ASPField.
    * Return NaN (Not-a-Number) value, if the specified text is empty.
    */
   public double parseNumber( String field_name, String text )
   {
      try
      {
         return asppage.getASPField(field_name).parseNumber(text);
      }
      catch ( Throwable e )
      {
         logError(e);
         return NOT_A_NUMBER;
      }
   }

   /**
    * Convert a Date value to the client format
    * using the datatype and format mask of the named ASPField.
    */

   public String formatDate( String field_name, Date value )
   {
      try
      {
         return asppage.getASPField(field_name).formatDate(value);
      }
      catch ( Throwable e )
      {
         logError(e);
         return null;
      }
   }

   /**
    * Convert given String into a date value
    * using the datatype and format mask of the named ASPField.
    */
   public Date parseDate( String field_name, String text )
   {
      return parseDate(field_name, text, null);
   }

   Date parseDate( String field_name, String text, ASPPage page)
   {
      if(page==null)
          page = asppage;
      try
      {
         return page.getASPField(field_name).parseDate(text);
      }
      catch ( Throwable e )
      {
         logError(e);
         return null;
      }
   }


   //==========================================================================
   //  URL
   //==========================================================================

   /**
    * Return the name of the ASP server.
    */
   public String getServerName()
   {
      try
      {
         //return server_variables.get("SERVER_NAME").toString();
         return asp_request.getServerName();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return a complete path for the current ASP page.
    */
   public String getPath()
   {
      return asppage.getPagePath();
   }

   /**
    * Return the whole query string from the last request.
    */
   public String getQueryString()
   {
      try
      {
         //return server_variables.get("QUERY_STRING").toString();
         //Bug 42662, start
         String qstr = asp_request.getQueryString();

         if(qstr!=null && !isEmpty(qstr))
         {
            if(qstr.lastIndexOf("&")==qstr.length()-1)
               qstr = qstr.substring(0,qstr.length()-1);

            while(true)
            {
               int pos = qstr.indexOf("__ID");

               if(pos==-1)
                  break;

               if(pos==0)
               {
                  int para_pos = qstr.indexOf("&");
                  if(para_pos!=-1)
                     qstr = qstr.substring(para_pos+1);
                  else
                     qstr = "";
               }
               else
               {
                  int para_pos = qstr.indexOf("&");
                  String first = "";

                  if(para_pos!=-1)
                     first = qstr.substring(0,pos);

                  qstr = qstr.substring(pos);

                  para_pos = qstr.indexOf("&");

                  if(para_pos!=-1)
                     qstr = qstr.substring(para_pos+1);
                  else
                     qstr = "";

                  qstr = first+qstr;
               }
            }
         }

         if(qstr != null && qstr.indexOf("&__REG") != -1)
             qstr = replace(qstr,"&__REG", "&REG");

         if(qstr != null && qstr.indexOf("&__COPY") != -1)
             qstr = replace(qstr,"&__COPY", "&COPY");

         String qParams = query_string_at_logon;
         query_string_at_logon = "";
         if(qParams!=null && !isEmpty(qParams))
            qstr +="&"+qParams;

         return (qstr!=null)? qstr: "";
         //Bug 42662, end
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return the value of the named QueryString parameter, or null if it does not exist.
    * @see ifs.fnd.asp.ASPManager#getQueryStringValue
    * @see ifs.fnd.asp.ASPManager#getQueryStringValues
    * @deprecated
    */
   public String getQueryString( String name )
   {
      return getQueryStringValue(name);
   }

   /**
    * Return the first value of the named Query String parameter, or null if it does not exist.
    * @see ifs.fnd.asp.ASPManager#getQueryString
    * @see ifs.fnd.asp.ASPManager#getQueryStringValues
    */
   public String getQueryStringValue( String name )
   {
      return getQueryStringValue(name, false);
   }

   public String getAbsoluteQueryStringValue( String name )
   {
      return getQueryStringValue(name, true);
   }

   private String getQueryStringValue( String name, boolean absolute )
   {
      try
      {
         String[] params = getQueryStringValues(name);
         if ( params==null || params.length==0 ) return null;

         if(!Str.isEmpty(params[0]) && isCSVName(params[0].trim()) && !absolute){
            if (asppage!=null)
            {
               String f = Str.replace(name, asppage.addProviderPrefix(), "");
               if(asppage.isASPField(f))
                  params[0] = getCSVValue(params[0], asppage.getASPField(f));
            }
            else
               params[0] = getCSVValue(params[0]);
         }

         return params[0];
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return all values of the named Query String parameter, or null if doesn't exist.
    * @see ifs.fnd.asp.ASPManager#getQueryString
    * @see ifs.fnd.asp.ASPManager#getQueryStringValue
    */
   public String[] getQueryStringValues( String name )
   {
      try
      {
         initRequest();
         scanQueryString();

         return(String[])query_string_params.get(name);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   private Hashtable query_string_params = null;

   private void scanQueryString()
   {
      // Copied from org.apache.tomcat.util.RequestUtil, method processFormData(), modified

      if ( query_string_params != null && isEmpty(query_string_at_logon)) return; // already initiated

      query_string_params = new Hashtable();
      String qrystr = getQueryString();
      if ( qrystr==null ) return;

      StringTokenizer tok = new StringTokenizer(qrystr, "&", false);
      while ( tok.hasMoreTokens() )
      {
         String pair = tok.nextToken();
         int pos = pair.indexOf('=');
         if ( pos != -1 )
         {
            //String key = unUrlDecode(pair.substring(0, pos));
            //String value = unUrlDecode(pair.substring(pos+1, pair.length()));
            String key = URLDecode(pair.substring(0, pos));
            String value = URLDecode(pair.substring(pos+1, pair.length()));

            String values[];
            if ( query_string_params.containsKey(key) )
            {
               String oldValues[] = (String[])query_string_params.get(key);
               values = new String[oldValues.length + 1];
               for ( int i = 0; i < oldValues.length; i++ )
               {
                  values[i] = oldValues[i];
               }
               values[oldValues.length] = value;
            }
            else
            {
               values = new String[1];
               values[0] = value;
            }
            query_string_params.put(key, values);
         }
         else
         {
            // we don't have a valid chunk of form data, ignore
         }
      }
   }

   /*
   private String unUrlDecode( String data )
   {
      // Copied from org.apache.tomcat.util.RequestUtil
      // Should be the same as java.net.URLDecoder.decode() !!

      AutoString buf = new AutoString();
      for(int i = 0; i < data.length(); i++)
      {
         char c = data.charAt(i);
         switch(c)
         {
            case '+':
               buf.append(' ');
               break;
            case '%':
               try
               {
                  buf.append((char) Integer.parseInt(data.substring(i+1,i+3), 16));
                  i += 2;
               }
               catch(NumberFormatException e)
               {
                  String msg = "Decode error ";
                  // XXX no need to add sm just for that
                  // sm.getString("serverRequest.urlDecode.nfe", data);
                  throw new IllegalArgumentException(msg);
               }
               catch(StringIndexOutOfBoundsException e)
               {
                  String rest  = data.substring(i);
                  buf.append(rest);
                  if(rest.length()==2)
                     i++;
               }
               break;
            default:
               buf.append(c);
               break;
         }
      }
      return buf.toString();
   }
   */

   /**
    * Return a complete URL for the current page without query string.
    */
   public String getURL()
   {
      return asppage.getURL();
   }

   private String getVarPortString()
   {
      try
      {
         //return server_variables.get("SERVER_PORT").toString();
         return getAspRequest().getServerPort()+"";
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Returns the server port.
    */
   public String getPortString()
   {
      if ( port_string==null )
         port_string = getASPConfig().getApplicationDomain().indexOf(':')<0 ? "" : ":"+getVarPortString();

      return port_string;
   }

   private void redirectRequest( String url)
   {
      redirectRequest(url,true);
   }

   private void redirectRequest( String url, boolean correct_url )
   {
      if(isStdPortlet())
         error(new FndException("FNDREDIRECTNOTSUPPORTED: Redirect not supported for JSR168 compilant portlets"));

      try
      {
         if ( DEBUG ) debug(this + ".redirectRequest()");
         clearResponse();
         end_response = false;
         addCookies();

         if (correct_url)
            url = correctURL(url);

         if ( DEBUG ) debug("  redirectRequest(): sending redirect to: "+url);
         getAspResponse().sendRedirect(url);
         if ( DEBUG ) debug("  redirectRequest(): redirect send.");
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
      }
      if ( asppage instanceof ASPPageProvider )
         throw new ResponseEndException();
   }

   /**
    * Redirect the request to a given URL.
    * Store the current URL in a global variable for future use.
    *
    * @see ifs.fnd.asp.ASPManager#redirectFrom
    */

   public void redirectTo( String url )
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.redirectTo("+url+")");
         redirectTo(url, true);
      }
      catch ( Throwable any )
      {
         error(any);
      }

   }


   /**
    * Redirect the request to a given URL without modifying the URL.
    * Store the current URL in a global variable for future use.
    *
    * @see ifs.fnd.asp.ASPManager#redirectFrom
    */

   public void redirectToAbsolute( String url )
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.redirectToAbsolute("+url+")");
         redirectTo(url, false);
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   private String getModule()
   {
      ASPConfig  cfg = getASPConfig();
      String module = "";
      String site = cfg.getApplicationPath();
      String uri = this.getAspRequest().getRequestURI();
      //String path = uri.substring(uri.indexOf(site)+site.length(),uri.length());
      String secured_path = cfg.getSecuredPath();
      String path = uri.substring(uri.indexOf(secured_path)+secured_path.length(),uri.length());
      if(path.lastIndexOf("/")>0)
        module = path.substring(0,path.lastIndexOf("/")+1);
      return module;
   }

   private String getAbsoluteURL(String url)
   {
      ASPConfig  cfg = getASPConfig();
      String protocol_domain = cfg.getProtocol()+"://"+cfg.getApplicationDomain()+"/";
      String site = cfg.getSecuredApplicationPath();
      String unsec_site = cfg.getUnsecuredApplicationPath();

      String module  = getModule();
      String temp_url = "/"+url;

      if(url.startsWith("http"))                              // url already absolute...
      {
         return url;
      }
      else if((temp_url.indexOf(site) > -1) || (temp_url.indexOf(unsec_site) > -1))                  // url contains site...
      {
         if(url.startsWith("/"))
            url = protocol_domain + url.substring(1,url.length());
         else
            url = protocol_domain + url;
      }
      else if(url.indexOf(module) > -1 || url.indexOf(module.substring(1)) > -1)   // url contains module...
      {
         if(url.startsWith("/"))
            url = protocol_domain + site.substring(1) + url;
         else
            url = protocol_domain + site.substring(1)+ "/" + url;
      }
      else if(url.startsWith("../"))                          // url contains a different module...
          url = protocol_domain + site.substring(1)+ module + url;
      else                                                    //url has neither site nor module
      {
         if(url.startsWith("/"))
           //url = protocol_domain + site.substring(1) + module + url.substring(1,url.length());
           url = protocol_domain + site.substring(1) + url;
         else
           url = protocol_domain + site.substring(1) + module + url;
      }

      return url;
   }

   private void redirectTo( String url, boolean modify_url )
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.redirectTo("+url+")");

         ASPConfig cfg = getASPConfig();
         //String my_url   = getPath();
         String my_url = asppage.getCurrentPagePath();
         String params   = getQueryString();
         String req_url  = "";

         my_url = getAbsoluteURL(my_url);

         if ( !Str.isEmpty(params) )
            my_url = my_url + "?" + params;

         if ( DEBUG ) debug("  my_url="+my_url);

         if(modify_url)
             req_url = getAbsoluteURL(url);
         else
             req_url = url;

         if ( DEBUG ) debug("  req_url="+req_url);

         // save the own URL (with parameters) and the requested one (without parameters)
         context.setGlobal( URL_FROM, my_url );
         int ix = req_url.indexOf('?');
         context.setGlobal( URL_TO,   ix<0 ? req_url : req_url.substring(0, ix) );

         // redirect
         removePageId();
         redirectRequest(req_url,modify_url);
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Try to redirect back to the stored URL.
    *
    * @see ifs.fnd.asp.ASPManager#redirectTo
    */
   public void redirectFrom()
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.redirectFrom()");
         // get callers URL
         String url = context.findGlobal( URL_FROM, getConfigParameter("APPLICATION/LOCATION/PORTAL") );

         url = getAbsoluteURL(url);

         if ( DEBUG ) debug("  [redirectFrom]url="+url);
         // clean up global variables
         context.removeGlobal( URL_FROM );
         context.removeGlobal( URL_TO );
         // try redirect back to caller
         if ( !Str.isEmpty(url) )
         {
            removePageId();
            redirectRequest(url);
         }
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Clean up any redirect information.
    *
    * @see ifs.fnd.asp.ASPManager#redirectTo
    */
   public void cleanUpRedirectInfo()
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.cleanUpRedirectInfo()");
         // clean up global variables
         context.removeGlobal( URL_FROM );
         context.removeGlobal( URL_TO );
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Check the stored URL if any.
    * Clean up it if request is not equal the current page.
    */
   //private
   void checkGlobalURL()
   {
      if ( DEBUG ) debug("ASPManager.checkGlobalURL()");
      // get the requested URL from global variable
      String req_url = context.findGlobal( URL_TO, "" );
      if ( Str.isEmpty(req_url) ) return;
      if ( DEBUG ) debug("  [checkGlobalURL]req_url="+req_url);
      // get the own URL
      String my_url = asppage.getURL();
      if ( DEBUG ) debug("  [checkGlobalURL]my_url="+my_url);
      // compare URL:s
      my_url = getAbsoluteURL(my_url);
      if ( !my_url.equalsIgnoreCase(req_url) )
      {
         if ( DEBUG ) debug("  [checkGlobalURL] removing URL:s");
         context.removeGlobal( URL_FROM );
         context.removeGlobal( URL_TO );
      }
   }

   /**
    * Redirect the request to an URL, passing the specified ASPObject.
    * The object is serialized to a temporary file and the file name is passed
    * as a QueryString parameter with the specified name.
    * Note, that the called ASP page should delete this file.
    *
    * @see ifs.fnd.asp.ASPManager#removeTempFile
    */
   public void redirectTo( String url,
                           String param_name,
                           ASPObject object )
   {
      try
      {
         String parfile = getTempFileName();
         object.saveToFile(parfile);

         String querystr = getQueryString();
         if ( !Str.isEmpty(querystr) ) querystr = "&"+querystr;

         redirectTo(url+"?"+param_name+"="+parfile+querystr);
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }

   //==========================================================================
   //  Data Transfer
   //==========================================================================

   /**
    * Redirect the request to an URL, passing the specified ASPObject
    * using the Data Transfer mechanism.
    */
   public void transferDataTo( String url,
                               // ASPObject object )
                               ASPBuffer object )
   {
      try
      {
         String pkg = pack(object,1900-url.length());
         char sep = url.indexOf('?')>0 ? '&' : '?';
         redirectTo( url + sep + TRANSFER_PARAM_NAME + "=" + pkg );
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   /**
     * Returns the transfer data to query string.
     */
   public String getTransferedQueryString( ASPBuffer object )
   {
      try
      {
         String pkg = pack(object);
         return TRANSFER_PARAM_NAME + "=" + pkg;
      }
      catch ( Throwable any )
      {
         error(any);
      }
      return null;
   }


   /**
    * Return true if the Data Transfer has been used to call this ASP page.
    */
   public boolean dataTransfered()
   {
      try
      {
         return getQueryString(TRANSFER_PARAM_NAME) != null;
      }
      catch ( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Return the ASPObject that has been passed to this ASP page using
    * the Data Transfer mechanism.
    */
   //public ASPObject getTransferedData()
   public ASPBuffer getTransferedData()
   {
      try
      {
         String pkg = getQueryString(TRANSFER_PARAM_NAME);
         if ( Str.isEmpty(pkg) )
            throw new FndException("FNDMGRNOTRANSF: No Data has been Transfered to the current URL");

         return(ASPBuffer)unpack(pkg);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   //==========================================================================
   //  Page pool routines
   //==========================================================================

   private ASPPageHandle asp_page_handle;

   void setASPPageHandle( ASPPageHandle handle ) throws FndException
   {
      if ( asp_page_handle!=null )
         throw new FndException("FNDMGRHANDLE: Page handle already defined !");
      asp_page_handle = handle;
   }

   //==========================================================================
   //  DataFormatter Pool
   //==========================================================================

   DataFormatter getDataFormatter( int type_id, String mask ) throws Exception
   {
      return getASPConfig().getDataFormatter( type_id, mask, getLanguageCode());
   }

   /**
    * Returns the decimal seperator for the given type. This method first look for profile and the
    *try to read from the webclientconfig. If the value is missing in both places it read it from the
    *Locale.
    *@param type_id data type. This can be DataFormatter.MONEY or DataFormatter.INTEGER
    */
   public char getDecimalSeparator(int type_id)
   {
      String sep;
      sep = asppage.readGlobalProfileDecimalSeperator(type_id);

      if (isEmpty(sep))
         sep = getConfigParameter("LANGUAGE/"+getLanguageCode()+"/DECIMAL_SEPARATOR",null);
      if ("SPACE".equals(sep))
         return ' ';//(char)160 + "";

      if (isEmpty(sep))
         return (new DecimalFormatSymbols(new Locale(getLanguageCode()))).getDecimalSeparator();
      else
         return sep.charAt(0);
   }
   /**
    * Returns the group seperator for the given type. This method first look for profile and the
    *try to read from the webclientconfig. If the value is missing in both places it read it from the
    *Locale.
    *@param type_id data type. This can be DataFormatter.MONEY or DataFormatter.INTEGER
    */
   public char getGroupSeparator(int type_id)
   {
      String sep;
      sep = asppage.readGlobalProfileGroupSeperator(type_id);

      if (isEmpty(sep))
         sep = getConfigParameter("LANGUAGE/"+getLanguageCode()+"/GROUP_SEPARATOR",null);

      if ("SPACE".equals(sep))
         return ' ';//(char)160 + "";

      if (isEmpty(sep))
      {
         char char_sep = (new DecimalFormatSymbols(new Locale(getLanguageCode()))).getGroupingSeparator();
         if (char_sep==160)
            return ' ';
         else
            return char_sep;
      }
      else
         return sep.charAt(0);
   }
   /**
    * Return group sizes for given type. Thsi will first read the sizes from profile. Then from Config file
    * and finally from the locale.
    * @param type_id data type. it can be DataFormatter.NUMBER or DataFormatter.MONEY
    * @return group size. It can be either a number or a semicolon separated list. 3;2;2 -> ##,##,###
    */

   public String getGroupingSizes(int type_id)
   {
      String sizes;
      String mask="";
      sizes = asppage.readGlobalProfileGroupingSize(type_id);

      if (!isEmpty(sizes))
         return sizes;

      //not found in the profile, read from config file

      if (DataFormatter.NUMBER==type_id)
         mask = getConfigParameter("LANGUAGE/"+getLanguageCode()+"/NUMBER/MASK",null);
      else if (DataFormatter.MONEY==type_id)
         mask = getConfigParameter("LANGUAGE/"+getLanguageCode()+"/MONEY/MASK",null);

      if (isEmpty(mask))
      {
         //not found in config file either, read from the locale
         if (DataFormatter.NUMBER==type_id)
            mask=((DecimalFormat) NumberFormat.getInstance(new Locale(getLanguageCode()))).toPattern();
         else if (DataFormatter.MONEY==type_id)
         {
            mask=((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale(getLanguageCode()))).toPattern();
            int i = mask.indexOf(';');
            if (i>1)
               mask = mask.substring(0,i);
            mask=mask.replaceAll("\u00A4", "").trim();//remove currency sign from pattern and trim
         }
      }

      if (DEBUG) debug(mask + " found ");
      int decimal_index = mask.lastIndexOf('.');
      if (decimal_index>1)
         mask = mask.substring(0,decimal_index);
      StringTokenizer tokens = new StringTokenizer(mask,",");

      int len = tokens.nextToken().length(); //specially handle the first one.
      sizes = (len==1)?"":len + "";          // if it is just 1 then it may a pattern like #,###. so ignore it.
      int last_length = len;
      while (tokens.hasMoreTokens())
      {
         len = tokens.nextToken().length();
         if (len != last_length)
            sizes = len + (sizes.length()>0?";":"") + sizes;
         last_length = len;
      }
      if (isEmpty(sizes)) //nothing happend. go for standard grouping -> 3
         sizes = "3";
      return sizes;
   }

   /**
    * Returns decimal digit size. It will be taken from profile or config file or locale. Which ever
    * exists.
    * @param type_id data type. it can be DataFormatter.NUMBER or DataFormatter.MONEY
    */
   public int getDecimalDigitsSize(int type_id)
   {
      String size;
      String mask;
      size = asppage.readGlobalProfileDecimalDigitSize(type_id);

      if (isEmpty(size))
      {
         if (DataFormatter.NUMBER==type_id)
            mask = getConfigParameter("LANGUAGE/"+getLanguageCode()+"/NUMBER/MASK",null);
         else if (DataFormatter.MONEY==type_id)
            mask = getConfigParameter("LANGUAGE/"+getLanguageCode()+"/MONEY/MASK",null);
         else
            return 0;
      }
      else
         return Integer.parseInt(size);
      if (isEmpty(mask))
      {
         if (DataFormatter.NUMBER==type_id)
            mask=((DecimalFormat) NumberFormat.getInstance(new Locale(getLanguageCode()))).toPattern();
         else if (DataFormatter.MONEY==type_id)
            mask=((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale(getLanguageCode()))).toPattern();
      }
      if (DEBUG) debug(mask + " found ");
      int index = mask.lastIndexOf('.');
      if (DEBUG) debug(mask.substring(index) + "  substr");
      if (index>-1)
         return mask.substring(index).replace(')',' ').trim().length()-1;
      return 2;
   }

   //==========================================================================
   //  ASPObjects on the current ASP page
   //==========================================================================

   /**
    * Returns a reference to an instance of ASPPage.
    */
   public ASPPage getASPPage()
   {
      if ( asppage.getASPPortal() != null )
         error(new FndException("FNDMGRPORTPAG: This is a portal. Use getPortalPage() to obtain the portal page."));
      return asppage;
   }


   /**
    * Returns a reference to an instance of the portal page.
    */
   public ASPPage getPortalPage()
   {
      return asppage;
   }

   boolean isPortalPage()
   {
      return ( asppage.getASPPortal() != null );
   }

   private void checkPortalPage()
   {
      if ( asppage.getASPPortal() != null )
         error(new FndException("FNDMGRPORTOBJ: This is a portal. Use corresponding method on your page."));
   }


   /**
    * Creates an instance of the portal page.
    */
   public ASPPortal createPortalPage()
   {
      if ( DEBUG ) debug(this+"ASPManager.createPortalPage()");
      ASPPortal portal = null;
      try
      {
         if( asppage.isUndefined() )
         {
            asppage.setVersion(3);
            portal = asppage.newASPPortal();
            asppage.setDefined();
         }
         else
            portal = asppage.getASPPortal();
         //Initialising CSV cash before the portal page can run
         initCSVCache();
         portal.run();
         return portal;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Prevent the page from expiring.
    */
   public void setPageNonExpiring()
   {
      getASPContext().keepContextInCache();
      //page_never_expires = true;
   }

   /**
    * Enables the page expiring.
    */
   public void setPageExpiring()
   {
      getASPContext().removeContextFromCache();
      //page_never_expires = false;
   }

   /**
    * Return a reference to the named ASPField. Throw an Exception if the
    * specified field has not been defined on the current ASP page.
    */
   public ASPField getASPField( String name )
   {
      checkPortalPage();
      return asppage.getASPField(name);
   }

   /**
    * Returns the referance to the specified ASPField.
    * @param name Name of the ASPField.
    * @param page Used for potlet pages. Otherwise null.
    */
   public ASPField getASPField( String name, ASPPage page)
   {
      if(page == null)
         page = asppage;
      return page.getASPField(name);
   }

   /**
    *Checks whether an ASPField exists.
    */
   public boolean hasASPField( String name)
   {
       return hasASPField(name, null);
   }

   /**
    * Checks whether an ASPField exists.
    * @param name Name of the ASPField.
    * @param page Used for potlet pages. Otherwise null.
    * @return true if exixts, otherwise false.
    */
   public boolean hasASPField( String name, ASPPage page)
   {
      if(page == null)
          page = asppage;
      return page.hasASPField(name);
   }

   /**
    * Return the reference to an instance of ASPTable that is based on
    * the specified ASPBlock.
    * Throw an Exception if there is no such table, or if there are more than one
    * table based on the block.
    */
   public ASPTable getASPTable( ASPBlock block )
   {
      checkPortalPage();
      return asppage.getASPTable(block.getName());
   }

   /**
    * Returns the path+filename of the Unauthorized Access Page.
    * 'mode' can either be "PHYSICAL" or "RELATIVE".
    */
   public String getUnauthorizedAccessPage()
   {
      return getASPConfig().getApplicationPath() + "/common/scripts/unauth_message.htm";
   }

   /**
    * Returns the path+filename of the dummy page.
    */
   public String getDummyPage()
   {
      return getASPConfig().getApplicationPath() + "/common/scripts/dummy.htm";
   }
   
   /**
    * Returns the location of empty image.
    */
   String getDummyImage()
   {
      return getASPConfig().getImagesLocation()+ "/empty.gif";
   }


   //==========================================================================
   //  Keep track of the current page
   //==========================================================================

   private Stack page_stack = new Stack();

   void pushCurrentPage( ASPPage page )
   {
      if(DEBUG) debug("ASPManager.pushCurrentPage("+page+")");
      page_stack.push(page);
   }

   boolean popCurrentPage( ASPPage page )
   {
      if(DEBUG) debug("ASPManager.popCurrentPage("+page+")");
      try
      {
         ASPPage top_page = (ASPPage)page_stack.peek();
         if( top_page == page )
         {
            page_stack.pop();
            return true;
         }
         else
         {
            if(DEBUG) debug("ASPManager.popCurrentPage(): wrong top page!");
            return false;
         }
      }
      catch( EmptyStackException x )
      {
         if(DEBUG) debug("ASPManager.popCurrentPage(): stack is empty!");
         return false;
      }
   }

   String getCurrentPage()
   {
      if(DEBUG) debug("ASPManager.getCurrentPage()");

      try
      {
         ASPPage current_page = (ASPPage)page_stack.peek();
         return current_page.getClass().getName();
      }
      catch( EmptyStackException x )
      {
         if(DEBUG) debug("ASPManager.getCurrentPage(): stack is empty!");
         return null;
      }
   }

   //==========================================================================
   //  Image generation
   //==========================================================================

   /**
    * Generates the translated images.
    * @deprecated
    */
   public void generateTranslatedImages()
   {
       return;
   }


   /**
    * Returns true if translated image folder is empty.
    * @deprecated
    */
   public boolean translatedDirectoryIsEmpty()
   {
      return false;
   }

   /**
    * Returns true if translated image folder is empty.
    * @deprecated
    */
   public boolean translatedDirectoryIsEmpty(String lang_code)
   {
      return false;
   }

   //==========================================================================
   //  Profiles
   //==========================================================================

   private ASPPageHandle profile_page_handle;
   //private ASPPage       profile_page;

   /**
    * This method is not to be used by developers.
    */
   public ASPPage getProfilePage(String url)
   {
      if ( DEBUG ) debug("ASPManager.getProfilePage("+url+")");
      try
      {
         profile_page_handle = ASPPagePool.getASPPageHandle(this,url,asppage);
         if ( profile_page_handle!=null )
         {
            ASPPage page = profile_page_handle.getASPPage();
            if ( DEBUG ) debug("ASPManager: ASPPage (profile) \""+url+"\" ("+page.getStateName()+") found in pool.");
            ASPPage profile_page = (ASPPage)(page.forceClone()?page.clone(this):page.frameworkClone(this,true));
            ASPPagePool.unlock(profile_page_handle,this,asppage);

            return profile_page;
         }
         else
            throw new FndException("FNDMGRPNOTF: Page '&1' not found in Page Pool",url);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
      finally
      {
         profile_page_handle = null;
      }
   }

   public ASPTable getASPTableFromPage( ASPPage profilePage, String name )
   {
      if ( DEBUG ) debug("ASPManager.getASPTableFromPage("+profilePage.getStateName()+","+name+")");
      try
      {
         on_save_profile_properties = true;
         if(profilePage.hasASPTable(name))
            return profilePage.getASPTable(name);
         return null;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   public ASPBlockLayout getASPBlockLayoutFromPage( ASPPage profilePage, String name )
   {
      if ( DEBUG ) debug("ASPManager.getASPBlockLayoutFromPage("+profilePage.getStateName()+","+name+")");
      try
      {
         on_save_profile_properties = true;
         return profilePage.getASPBlock(name).getASPBlockLayout();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   public ASPCommandBar getASPCommandBarFromPage( ASPPage profilePage, String name )
   {
      if ( DEBUG ) debug("ASPManager.getASPCommandBarFromPage("+profilePage.getStateName()+","+name+")");
      try
      {
         on_save_profile_properties = true;
         return profilePage.getASPBlock(name).getASPCommandBar();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   public ASPBlock getASPBlockFromPage( ASPPage profilePage, String name )
   {
      if ( DEBUG ) debug("ASPManager.getASPBlockFromPage("+profilePage.getStateName()+","+name+")");
      try
      {
         on_save_profile_properties = true;
         return profilePage.getASPBlock(name);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }


   public ASPTable getASPTableFromPage( String url, String name )
   {
      if ( DEBUG ) debug("ASPManager.getASPTableFromPage("+url+","+name+")");
      try
      {
         //Bug 40931, start
         //profile_page_handle = ASPPagePool.getASPPageHandle(this,url);
         profile_page_handle = ASPPagePool.getASPPageHandle(this,url,asppage);
         //Bug 40931, end
         if ( profile_page_handle!=null )
         {
            ASPPage profile_page = profile_page_handle.getASPPage();
            if ( DEBUG ) debug("ASPManager: ASPPage (profile) \""+url+"\" ("+profile_page.getStateName()+") found in pool.");
            //profile_page.activate();
            on_save_profile_properties = true;
            return profile_page.getASPTable(name);
         }
         else
            throw new FndException("FNDMGRPNOTF: Page '&1' not found in Page Pool",url);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /*boolean onSaveTableProfile()
   {
      return on_save_table_properties;
   }*/

   public ASPBlockLayout getASPBlockLayoutFromPage( String url, String name )
   {
      if ( DEBUG ) debug("ASPManager.getASPBlockLayoutFromPage("+url+","+name+")");
      try
      {
         //Bug 40931, start
         //profile_page_handle = ASPPagePool.getASPPageHandle(this,url);
         profile_page_handle = ASPPagePool.getASPPageHandle(this,url,asppage);
         //Bug 40931, end
         if ( profile_page_handle!=null )
         {
            ASPPage profile_page = profile_page_handle.getASPPage();
            if ( DEBUG ) debug("ASPManager: ASPPage (profile) \""+url+"\" ("+profile_page.getStateName()+") found in pool.");
            //profile_page.activate();
            on_save_profile_properties = true;
            return profile_page.getASPBlock(name).getASPBlockLayout();
         }
         else
            throw new FndException("FNDMGRPNOTF: Page '&1' not found in Page Pool",url);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /*boolean onSaveBlockLayoutProfile()
   {
      return on_save_blocklayout_properties;
   }*/

   public ASPCommandBar getASPCommandBarFromPage( String url, String name )
   {
      if ( DEBUG ) debug("ASPManager.getASPCommandBarFromPage("+url+","+name+")");
      try
      {
         profile_page_handle = ASPPagePool.getASPPageHandle(this,url,asppage);
         if ( profile_page_handle!=null )
         {
            ASPPage profile_page = profile_page_handle.getASPPage();
            if ( DEBUG ) debug("ASPManager: ASPPage (profile) \""+url+"\" ("+profile_page.getStateName()+") found in pool.");
            on_save_profile_properties = true;
            return profile_page.getASPBlock(name).getASPCommandBar();
         }
         else
            throw new FndException("FNDMGRPNOTF: Page '&1' not found in Page Pool",url);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /*boolean onSaveCommandBarProfile()
   {
      return on_save_menu_properties;
   }*/

   public ASPBlock getASPBlockFromPage( String url, String name )
   {
      if ( DEBUG ) debug("ASPManager.getASPBlockFromPage("+url+","+name+")");
      try
      {
         profile_page_handle = ASPPagePool.getASPPageHandle(this,url,asppage);
         if ( profile_page_handle!=null )
         {
            ASPPage profile_page = profile_page_handle.getASPPage();
            if ( DEBUG ) debug("ASPManager: ASPPage (profile) \""+url+"\" ("+profile_page.getStateName()+") found in pool.");
            on_save_profile_properties = true;
            return profile_page.getASPBlock(name);
         }
         else
            throw new FndException("FNDMGRPNOTF: Page '&1' not found in Page Pool",url);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   boolean onSaveProfileProperties()
   {
      return on_save_profile_properties;
   }

//========================================================================================
// Depricated Methods: These methods are introduced to use Activities in FNDWEB and 
// never used. Due to New Programing model, all these methods are now obsolete. Should 
// use new Programing model instead
//========================================================================================
   /**
    * @deprecated - use new programing model instead.
    */
   public int countRows(ASPBlock blk){return 0;}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record getQueryRecord(ASPBlock blk){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record getQueryRecord(String entity){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record addQueryParameter(String name, String value, Record rec){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record addQueryParameter(String name, long value, Record rec){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record addQueryParameter(String name, double value, Record rec){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record addQueryParameter(String name, Date value, Record rec){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record addQueryParameter(String name, boolean value, Record rec){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public int querySubmit(ASPBlock blk){return 0;}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record queryPerform(Record rec, ASPBlock blk){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public ASPBuffer prepareRow(ASPBlock blk){return null;}
   /**
    * @deprecated - use new programing model instead.
    */
   public void updateRow(ASPBlock blk){}
   /**
    * @deprecated - use new programing model instead.
    */
   public Record invokeOperation(String handler, String operation, Record rec){return null;}
//============================================================================================

   /**
    * Call the submit(ASPTransactionBuffer, String) method passing
    * the default MTS server component as the second argument.
    * The ProgID for the default MTS component is defined in the configuration
    * parameter SERVER.PROGID.
    */
   public ASPTransactionBuffer submit( ASPTransactionBuffer request )
   {
      return callMTS(request,null,true,true);
   }

   /**
    * Pass the request buffer to the specified MTS server component
    * for execution. Return the response buffer, if the request has been
    * successfully performed. Otherwise, format an error page, send it
    * to the client and return null.
    * Append to the TransactionBuffer all commands defined in each modified
    * ASPBlock.
    * Modify the Current State in the ASPContext.
    */
   public ASPTransactionBuffer submit( ASPTransactionBuffer request,
                                       String serverProgID )
   {
      return callMTS(request,serverProgID,true,true);
   }

   /**
    * Pass the request buffer to the specified MTS server component
    * for execution. Return the response buffer, if the request has been
    * successfully performed. Otherwise throw ASPLog.ExtendedAbortException,
    * which is supplied with additional information.
    */
   public ASPTransactionBuffer submitEx( ASPTransactionBuffer request )
   {
      return callMTS(request,null,true,false,true,null);
   }

   /**
    * Similar to submit(ASPTransactionBuffer). This function saves the
    * query buffer for the sent block. This will be used to navigate
    * in the table mode in webkit 3.
    */
   public ASPTransactionBuffer querySubmit( ASPTransactionBuffer request, ASPBlock blk)
   {
      try
      {
         if ( DEBUG ) debug("ASPManager.querySubmit()");
         String blk_name = blk.getName();
         ASPContext ctx = blk.getASPPage().getASPContext();
         ASPBuffer old_query = request.getBuffer(blk_name);
         ctx.writeBuffer("__"+blk.getName()+".QUERYBUFFER",old_query);
         
         ASPTransactionBuffer return_buffer = callQuery(request,blk_name);
         
         String skip_rows = ""+blk.getASPRowSet().countSkippedDbRows();
         String dbrows = ""+blk.getASPRowSet().countDbRows();
         String max_rows = ""+old_query.getBuffer().getInt("MAX_ROWS");
         String hasmrows=""+blk.getASPRowSet().moreRowsExist();

         ctx.writeValue("__"+blk_name+".SKIP_ROWS", skip_rows);
         ctx.writeValue("__"+blk_name+".MAX_ROWS", max_rows);
         ctx.writeValue("__"+blk_name+".DBCOUNT", dbrows);
         ctx.writeValue("__"+blk_name+".DEL_ROWS","0");
         ctx.writeValue("__"+blk_name+".HAS_MORE_ROWS", hasmrows);

         return return_buffer;
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //--------------------------------------------------------------------------

   /**
    * Call the perform(ASPTransactionBuffer, String) method passing
    * the default MTS server component as the second argument.
    * Not allowed to run before the page is defined.
    */
   public ASPTransactionBuffer perform( ASPTransactionBuffer request )
   {
//      return callMTS(request,null,false,true);
      return perform(request, null);
   }

   // Added by Terry 20140702
   // perform db call and throw exception
   public ASPTransactionBuffer perform_( ASPTransactionBuffer request )
   {
      return callMTS_(request,null,false,true,false,null);
   }
   // Added end
   
   /**
    * Pass the request buffer to the specified MTS server component
    * for execution. Return the response buffer, if the request has been
    * successfully performed. Otherwise, format an error page, send it
    * to the client and return null.
    * Do NOT modify the Current State in the ASPContext.
    * Not allowed to run before the page is defined.
    */
   public ASPTransactionBuffer perform( ASPTransactionBuffer request,
                                        String serverProgID )
   {
      if ( DEBUG ) debug("ASPManager.perform()");
      if ( asppage.isUndefined() )
         asppage.setPerformWhileUndefined();

      return callMTS(request,serverProgID,false,true);
   }

   /**
    * Pass the request buffer to database
    * for execution. Return the response buffer, if the request has been
    * successfully performed. Otherwise, throw an exception.
    * Do NOT modify the Current State in the ASPContext.
    * Not allowed to run before the page is defined.
    */
   public ASPTransactionBuffer performEx( ASPTransactionBuffer request)
   {
      if ( DEBUG ) debug("ASPManager.performEx()");
      if ( asppage.isUndefined() )
         asppage.setPerformWhileUndefined();

      return callMTS(request,null,false,false,true,null);
   }

   /**
    * Pass the request buffer to the default MTS server component
    * for execution. Return the response buffer, if the request has been
    * successfully performed. Otherwise, format an error page, send it
    * to the client and return null. Use the config user.
    * Only allowed to run while the page is still in the undefine state.
    */
   public ASPTransactionBuffer performConfig( ASPTransactionBuffer request )
   {
      if ( DEBUG ) debug("ASPManager.performConfig()");
      try
      {
         if ( !asppage.isUndefined() )
            throw new FndException("FNDMGREPERC: Not allowed to call performConfig() if the page is already defined.");

         //if (!SecurityHandler.CONFIG_USER_AUTHENTICATED)
         //   throw new FndException("FNDMGRCONUSERNOTAUTH: Config user not Authenticate. Not allowed to call performConfig().");

         request.addConfigRequestHeader();
      }
      catch ( Throwable any )
      {
         error(any);
      }

      return callMTS(request,null,false,true);
   }


   //--------------------------------------------------------------------------

   /**
    * Call the validate(ASPTransactionBuffer, String) method passing
    * the default MTS server component as the second argument.
    */
   public ASPTransactionBuffer validate( ASPTransactionBuffer request )
   {
      return callMTS(request,null,false,false);
   }

   /**
    * Pass the request buffer to the specified MTS server component
    * for execution. Return the response buffer, if the request has been
    * successfully performed. Otherwise, send an unformatted error message
    * to the client and return null.
    * Do NOT modify the Current State in the ASPContext.
    */
   public ASPTransactionBuffer validate( ASPTransactionBuffer request,
                                         String serverProgID )
   {
      return callMTS(request,serverProgID,false,false);
   }

   //--------------------------------------------------------------------------

   protected ASPTransactionBuffer callMTS( ASPTransactionBuffer requestbuf,
                                           String serverProgID,
                                           boolean modify_current_state,
                                           boolean format_error_page )
   {
      return callMTS(requestbuf,serverProgID,modify_current_state,format_error_page,false,null);
   }

   protected ASPTransactionBuffer callMTS( ASPTransactionBuffer requestbuf,
                                           String serverProgID,
                                           boolean modify_current_state,
                                           boolean format_error_page,
                                           ASPPage page)
   {
      return callMTS(requestbuf,serverProgID,modify_current_state,format_error_page,false,page);
   }
   
   protected ASPTransactionBuffer callQuery( ASPTransactionBuffer requestbuf, String blk_name)
   {
      String count_buff_name = "__"+blk_name+"_COUNT";
      
      ASPBuffer query_buf = requestbuf.getBuffer(blk_name);
      
      String view = query_buf.getValue("VIEW");
      String count_rows = query_buf.getValue("COUNT_ROWS");
      String where_condition = query_buf.getValue("WHERE");
      boolean countBufferEnabled = true;
      
      //bug id 76831, some sql queries (mostly complex ones) define the view (and where) condition as a single string. 
      //In such query statements its not possible to find the view name (and where condition).
      if ("N".equals(count_rows) || isEmpty(view))
      {
         countBufferEnabled = false;
      }
      else
      {
         //check DATA parameter count eqauls '?' placeholder count in where condition
         java.util.regex.Matcher match = java.util.regex.Pattern.compile("\\?").matcher(where_condition);
         int parameter_count = 0;
         while (match.find()) {
           parameter_count++;
         }

         int data_buffer_count = query_buf.getBuffer("DATA").countItems();
         
         if (data_buffer_count != parameter_count) countBufferEnabled = false;
      }
      
      if (countBufferEnabled)
      {
         query_buf.setValue("COUNT_ROWS","N");

         ASPBuffer count_buf = requestbuf.addBuffer(count_buff_name);
         count_buf.addItem("METHOD","Query");
         count_buf.addItem("VIEW",view);
         count_buf.addItem("SELECT","to_char(count(*)) N");
         count_buf.addItem("WHERE",where_condition);
         count_buf.addBuffer("DATA",query_buf.getBuffer("DATA"));
         count_buf.addItem("COUNT_ROWS","N");
         count_buf.addItem("MAX_ROWS","1"); 
      }

      ASPTransactionBuffer return_buffer = callMTS(requestbuf,null,true,true,false,null);
      
      if (countBufferEnabled)
      {
         query_buf.setValue("COUNT_ROWS",count_rows); //set it back. ASPCommandBar.addContents uses this value
         
      
         if (return_buffer != null)
         {
            ASPBuffer info_buffer = return_buffer.getBuffer(blk_name+"/INFO");
            String rows = return_buffer.getValue(count_buff_name+"/DATA/N");

            if (!"0".equals(rows))
               info_buffer.setValue("ROWS",rows);
         }
      }
      
      return return_buffer;
   }
   

   /**
    * Return the Java Access Provider Server instance to be used for communication with
    * a FNDEXT server. Do not disconnect the instance after the work is done - the same
    * instance is also used by the framework. Does only work with the JAP method!
    *
    * @deprecated
    */
   public Server getConnectionServer() throws FndException
   {
      if( asppage.getASPConfig().getTMMethod()!=ASPConfig.TM_JAP )
         throw new RuntimeException("FNDMGNOSUPCON: Only JAP provider connection method supported!.");
      if( connection==null ) connection = JAPConnectionPool.get(ConnectionPool.PLSQLGTW, getASPConfig());
      return (Server)connection.getConnectionProvider();
   }


   protected ASPTransactionBuffer callMTS( ASPTransactionBuffer requestbuf,
                                           String serverProgID,
                                           boolean modify_current_state,
                                           boolean format_error_page,
                                           boolean throw_exception,
                                           ASPPage page)
   {
      ASPTransactionBuffer responsebuf;

      try
      {
         if( page==null )
            page = asppage;

         ASPContext ctx = page.getASPContext();
         ASPConfig  cfg = page.getASPConfig();

         if( modify_current_state )
            page.generateBlockCommands(requestbuf);

         requestbuf.clearStandardInfoMessages();

         boolean has_header = requestbuf.getRequestHeader()!=null;

         if( !has_header )
            requestbuf.addRequestHeader();

         if( isTraceOn() )
            requestbuf.traceBuffer("Request");
         else if( DEBUG )
         {
            debug(this+": asplog="+asplog);
            debug("Request buffer contents:\n"+Buffers.listToString( requestbuf.getBuffer() ));
            debug("\nIdentity hash code: "+System.identityHashCode(requestbuf.getBuffer()) );
         }
         else if(DEBUGTRS)
            requestbuf.debugBufferHeader("Request");

         if(has_header)
         {
             if(requestbuf.hasRequestHeader())
             {
                 if(!requestbuf.hasValidRequestHeader())
                     throw new FndException("FNDMGRVAHMAL: Page error, the request buffer header is malformed.");
             }
             else
                 throw new FndException("FNDMGRNOTREQHED: Page error, the request buffer header is contaminated with response buffer header elements");
         }

         responsebuf = newASPTransactionBuffer();
         String requeststr;
         String respstr;

         switch ( cfg.getTMMethod() )
         {
            case ASPConfig.TM_JAP:

               if ( connection==null ) connection = JAPConnectionPool.get(JAPConnectionPool.PLSQLGTW, cfg);
               Buffer buf;
               try
               {
                  call_mts_event = call_mts_event_type.begin();
                  try
                  {
                     buf = (Buffer)connection.invoke(requestbuf.getBuffer());
                  }
                  catch ( FndException fnde )
                  {
                     error(fnde);
                     Throwable any = fnde.getCaughtException();
                     if( any!=null && any instanceof APException )
                     {
                        Buffer headbuf = ((APException)any).getHeaderBuffer();
                        if ( headbuf == null )
                        { // header buffer in APException will not be null if TM has been bound
                           FndException efnd = new FndException("FNDMGRTMNF: Failed to contact IFS Extended Server. Contact your system administrator.");
                           efnd.addCaughtException(any);
                           throw efnd;
                        }
                        else
                        {
                           FndException efnd = new FndException();
                           efnd.loadAndParse(headbuf);
                           throw efnd;
                        }
                     }
                     else
                        throw fnde;
                  }
               }
               finally
               {
                  call_mts_event.end();
               }
               responsebuf.construct(buf);
               break;

            case ASPConfig.TM_RMI:

               try
               {
                  Buffer rbuf = requestbuf.getBuffer();
                  if(DEBUG)
                  {
                     ClassLoader cl = this.getClass().getClassLoader();
                     if(cl!=null)
                        debug("ASPManager: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
                     else
                        debug("ASPManager: current class loader is null");
                     cl = rbuf.getClass().getClassLoader();
                     if(cl!=null)
                        debug("Request Buffer: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
                     else
                        debug("Request Buffer: current class loader is null");
                  }

                  if ( connection==null ) connection = EJBConnectionPool.get(ConnectionPool.PLSQLGTW, cfg);
                  Buffer ejbbuf = connection.perform(rbuf);

                  responsebuf.construct(ejbbuf);
               }
               catch ( Throwable e )
               {
                  FndException efnd = new FndException("FNDMGREJB: Error in communication with Plsql Gateway.");
                  efnd.addCaughtException(e);
                  error(e);
                  throw efnd;
               }

               break;

            case ASPConfig.TM_SOCKET:

               throw new FndException("FNDMGREXCSOCKET: The SOCKET method is now obsolete!");

            default:
               throw new FndException("FNDMGREXCMIS: Transaction Manager method not recognized!");
         }

         if ( isTraceOn() )
            responsebuf.traceBuffer("Response");
         else if ( DEBUG )
         {
            debug(this+": asplog="+asplog);
            debug("Response buffer contents:\n"+Buffers.listToString( responsebuf.getBuffer() ));
         }

         ASPBuffer header = responsebuf.getResponseHeader();
         String status = header.getValue("STATUS");
         if ( "DONE".equals(status) )
         {
            showAlert(responsebuf.getStandardInfoMessages());
            if ( modify_current_state )
            {
               ctx.putDbState( responsebuf );
               page.refresh();
            }
            return responsebuf;
         }
         else
         {
            FndException e = new FndException();

            Buffer fndbuf = header.getBuffer().getBuffer("FND_EXCEPTION",null);
            if ( fndbuf == null )
               throw new FndException("FNDMGREXC: Item 'FND_EXCEPTION' missing in the response buffer.");
            else
            {
               Buffer decision_buf = header.getBuffer().getBuffer("DECISION",null);
               if (decision_buf != null)
               {
                  Buffer dec_buf    = getFactory().getBuffer();
                  Buffer reauth_buf = getFactory().getBuffer();

                  reauth_buf.addItem("PASSWORD","");
                  reauth_buf.addItem(decision_buf.getBuffer("^REAUTHENTICATION^").getItem("USERNAME"));
                  reauth_buf.addItem("USER_COMMENT","");
                  dec_buf.addItem("^REAUTHENTICATION^",reauth_buf);
                  getAspSession().setAttribute(ERROR_DECISION_BUF, dec_buf);
               }
               e.load( fndbuf );
               callError(e,format_error_page,true,throw_exception);
            }
            return null;
         }
      }
      catch ( Throwable any )
      {
         callError(any,format_error_page,false,throw_exception);
         return null;
      }
   }

   // Added by Terry 20140702
   // perform db call and throw exception
   protected ASPTransactionBuffer callMTS_( ASPTransactionBuffer requestbuf,
         String serverProgID,
         boolean modify_current_state,
         boolean format_error_page,
         boolean throw_exception,
         ASPPage page)
   {
      ASPTransactionBuffer responsebuf;
      
      try
      {
         if( page==null )
            page = asppage;
         
         ASPContext ctx = page.getASPContext();
         ASPConfig  cfg = page.getASPConfig();
         
         if( modify_current_state )
            page.generateBlockCommands(requestbuf);
         
         requestbuf.clearStandardInfoMessages();
         
         boolean has_header = requestbuf.getRequestHeader()!=null;
         
         if( !has_header )
            requestbuf.addRequestHeader();
         
         if( isTraceOn() )
            requestbuf.traceBuffer("Request");
         else if( DEBUG )
         {
            debug(this+": asplog="+asplog);
            debug("Request buffer contents:\n"+Buffers.listToString( requestbuf.getBuffer() ));
            debug("\nIdentity hash code: "+System.identityHashCode(requestbuf.getBuffer()) );
         }
         else if(DEBUGTRS)
            requestbuf.debugBufferHeader("Request");
         
         if(has_header)
         {
            if(requestbuf.hasRequestHeader())
            {
               if(!requestbuf.hasValidRequestHeader())
                  throw new FndException("FNDMGRVAHMAL: Page error, the request buffer header is malformed.");
            }
            else
               throw new FndException("FNDMGRNOTREQHED: Page error, the request buffer header is contaminated with response buffer header elements");
         }
         
         responsebuf = newASPTransactionBuffer();
         String requeststr;
         String respstr;
         
         switch ( cfg.getTMMethod() )
         {
            case ASPConfig.TM_JAP:
            if ( connection==null ) connection = JAPConnectionPool.get(JAPConnectionPool.PLSQLGTW, cfg);
            Buffer buf;
            try
            {
               call_mts_event = call_mts_event_type.begin();
               try
               {
                  buf = (Buffer)connection.invoke(requestbuf.getBuffer());
               }
               catch ( FndException fnde )
               {
                  error(fnde);
                  Throwable any = fnde.getCaughtException();
                  if( any!=null && any instanceof APException )
                  {
                     Buffer headbuf = ((APException)any).getHeaderBuffer();
                     if ( headbuf == null )
                     { // header buffer in APException will not be null if TM has been bound
                        FndException efnd = new FndException("FNDMGRTMNF: Failed to contact IFS Extended Server. Contact your system administrator.");
                        efnd.addCaughtException(any);
                        throw efnd;
                     }
                     else
                     {
                        FndException efnd = new FndException();
                        efnd.loadAndParse(headbuf);
                        throw efnd;
                     }
                  }
                  else
                     throw fnde;
               }
            }
            finally
            {
               call_mts_event.end();
            }
            responsebuf.construct(buf);
            break;
            
            case ASPConfig.TM_RMI:
            try
            {
               Buffer rbuf = requestbuf.getBuffer();
               if(DEBUG)
               {
                  ClassLoader cl = this.getClass().getClassLoader();
                  if(cl!=null)
                     debug("ASPManager: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
                  else
                     debug("ASPManager: current class loader is null");
                  cl = rbuf.getClass().getClassLoader();
                  if(cl!=null)
                     debug("Request Buffer: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
                  else
                     debug("Request Buffer: current class loader is null");
               }
               
               if ( connection==null ) connection = EJBConnectionPool.get(ConnectionPool.PLSQLGTW, cfg);
               Buffer ejbbuf = connection.perform(rbuf);
               
               responsebuf.construct(ejbbuf);
            }
            catch ( Throwable e )
            {
               FndException efnd = new FndException("FNDMGREJB: Error in communication with Plsql Gateway.");
               efnd.addCaughtException(e);
               error(e);
               throw efnd;
            }
            
            break;
            
         case ASPConfig.TM_SOCKET:
            
            throw new FndException("FNDMGREXCSOCKET: The SOCKET method is now obsolete!");
            
         default:
            throw new FndException("FNDMGREXCMIS: Transaction Manager method not recognized!");
         }
         
         if ( isTraceOn() )
            responsebuf.traceBuffer("Response");
         else if ( DEBUG )
         {
            debug(this+": asplog="+asplog);
            debug("Response buffer contents:\n"+Buffers.listToString( responsebuf.getBuffer() ));
         }
         
         ASPBuffer header = responsebuf.getResponseHeader();
         String status = header.getValue("STATUS");
         if ( "DONE".equals(status) )
         {
            showAlert(responsebuf.getStandardInfoMessages());
            if ( modify_current_state )
            {
               ctx.putDbState( responsebuf );
               page.refresh();
            }
            return responsebuf;
         }
         else
         {
            return responsebuf;
         }
      }
      catch ( Throwable any )
      {
         callError(any,format_error_page,false,throw_exception);
         return null;
      }
   }
   // Added end
   
   protected void callError(Throwable any, boolean format_error_page, boolean from_buffer, boolean throw_exception)
   {
      if ( from_buffer )
      {
         if ( format_error_page )
            error(any,true,throw_exception);
         else
            try
            {
               error(any,false,throw_exception);
            }
            catch ( ASPLog.ExtendedAbortException extex )
            {
               throw extex;
            }
            catch ( ASPLog.AbortException aborting )
            {
               endResponse();
            }
      }
      else
         error(any,format_error_page,throw_exception);
   }


   //==========================================================================
   // Config Parameters and Sub-Buffers
   //==========================================================================

   /**
    * Return the value of the named configuration parameter, or
    * throw an Exception if such parameter does not exist.
    */
   public synchronized String getConfigParameter( String name )
   {
      try
      {
         return getASPConfig().getParameter(name);
      }
      catch ( Throwable e )
      {
         error(e);
         return null;
      }
   }

   /**
    * Return the value of the named configuration parameter, or the specified
    * default value, if such parameter does not exist.
    */
   public synchronized String getConfigParameter( String name, String default_value )
   {
      try
      {
         return getASPConfig().getParameter(name,default_value);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return the application domain.
    */
   public String getApplicationDomain()
   {
      return asppage.getApplicationDomain();
   }

   /**
    * Return the application path.
    */
   public String getApplicationPath()
   {
      return asppage.getApplicationPath();
   }

   //==========================================================================
   // Error, Warning, Info, LOV, StatusLine, Focus
   //==========================================================================

   private boolean page_id_set;

   boolean isPageIdSet()
   {
      return page_id_set;
   }

   /**
    * Set cookie that identifies this page as valid.
    */
   private void setPageId()
   {
      if ( DEBUG ) debug(this+".setPageId()");

      if ( getQueryString("VALIDATE")==null )
      {
         context.setTemporaryCookie( page_id, "*");
         page_id_set = true;
         if ( DEBUG ) debug(this+": Page ID: "+page_id);
      }
   }

   /**
    * Return the unique identifier of this page.
    */
   public String getPageId()
   {
      return page_id;
   }

   /**
    * @deprecated This method is obsolete
    */
   public int getFlowId()
   {
      return this.flowid;
   }

   /**
    * @deprecated This method is obsolete
    */
   public void setFlowId(int flowid)
   {
      this.flowid = flowid;
   }

   /**
   * @deprecated This method is obsolete
   */
   public int newFlowId(int maxid)
   {
      Integer last_flow_id = (Integer)getAspSession().getAttribute("last_flow_id");
      if (last_flow_id == null || last_flow_id.intValue()>=maxid){
         this.flowid = 0;
      }else{
         this.flowid = last_flow_id.intValue() + 1;
      }
      getAspSession().setAttribute("last_flow_id",new Integer(this.flowid));
      return this.flowid;
   }

   /**
    * Remove cookie that identifies this page as valid.
    * The cookie will be automatically removed on the 'onLoad' trigger.
    */
   public void removePageId()
   {
      context.removeCookie( page_id );
      if ( DEBUG ) debug(this+": Page ID removed.");
   }

   private void showErrorMessage( String message )
   {
      ASPHTMLFormatter fmt = asppage.getASPHTMLFormatter();
      try
      {
         if ( getAspResponse().getBufferSize()>0 ) clearResponse();
         writeResponse(fmt.formatErrorMsg(message));
         endResponse();
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
      }
   }

   /**
    * Pass the specified message to JavaScript in the browser to be displayed
    * in an alert box.
    */
   public void showAlert( String message )
   {
      if ( message==null ) return;
      alert_text = alert_text==null ? message : alert_text + "\n\n" + message;
   }

   String getAlertMsg()
   {
      return translate(alert_text);
   }

   void showSystemAlert( String message )
   {
      if ( message==null ) return;
      sys_alert_text = sys_alert_text==null ? message : sys_alert_text + "\n\n" + message;
   }

   /**
    * Finish the execution of the current request and send the specified error
    * message to the client. To be used from Java source code.
    */
   public void showJavaError( String message )
   {
      showErrorMessage( translateJavaText(message) );
   }

   /**
    * Finish the execution of the current request and send the specified error
    * message to the client.
    */
   public void showError( String message )
   {
      //showErrorMessage( translate(message) );
      error(new FndException(translate(message)) );
   }

   /**
    * Finish the execution of the current request and send the specified
    * info message to the client.
    */
   public void showInfo( String message )
   {
      message = translate(message);
      ASPHTMLFormatter fmt = asppage.getASPHTMLFormatter();
      try
      {
         if ( getAspResponse().getBufferSize()>0 ) clearResponse();
         writeResponse(fmt.formatInfoMsg(message));
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
      }
      endResponse();
   }

   /**
    * Finish the execution of the current request and send the specified
    * warning message to the client.
    */
   public void showWarning( String message )
   {
      message = translate(message);
      ASPHTMLFormatter fmt = asppage.getASPHTMLFormatter();
      try
      {
         if ( getAspResponse().getBufferSize()>0 ) clearResponse();
         writeResponse(fmt.formatWarningMsg(message));
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
      }
      endResponse();
   }

   private String std_lov_url;

   /**
    * Pass the specified ASPTable to the standard LOV ASP-page.
    * This method will redirect the client request to the url
    * defined in the configuration parameter STD_LOV_URL.
    */
   public void showLOV( ASPTable table )
   {
      if ( DEBUG ) debug("ASPManager.showLOV()");
      try
      {
         HttpServletResponse aspresp = getAspResponse();
         if ( aspresp.getBufferSize()>0 ) clearResponse();//aspresp.clear();
/////
         String tablefile = getTempFileName();
         table.saveToFile(tablefile);

         String querystr = getQueryString();
         if ( !Str.isEmpty(querystr) ) querystr = "&"+querystr;

         if ( std_lov_url==null ) std_lov_url = getASPConfig().getLOVURL(false);
         removePageId();
         aspresp.sendRedirect(std_lov_url+"?DEFFILE="+URLEncode(tablefile)+querystr);
/////

//         if(DEBUG) debug("  creating new instance of ASPLov...");
//         new ASPLov(asppage);//, table);
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }

   /**
    * Set the text line that will be returned from the method showStatusLine()
    * @see ifs.fnd.asp.ASPManager.showStatusLine
    */
   public void setStatusLine( String text )
   {
      setStatusLine( text, null, null, null );
   }

   public void setStatusLine( String text, String p1 )
   {
      setStatusLine( text, p1, null, null );
   }

   public void setStatusLine( String text, String p1, String p2 )
   {
      setStatusLine( text, p1, p2, null );
   }

   public void setStatusLine( String text, String p1, String p2, String p3 )
   {
      status_line = translate( text, p1, p2, p3 );
   }

   public void setStatusLineFromJava( String text )
   {
      setStatusLine( text, null, null, null );
   }

   public void setStatusLineFromJava( String text, String p1 )
   {
      setStatusLine( text, p1, null, null );
   }

   public void setStatusLineFromJava( String text, String p1, String p2 )
   {
      setStatusLine( text, p1, p2, null );
   }

   public void setStatusLineFromJava( String text, String p1, String p2, String p3 )
   {
      status_line = translateJavaText( text, p1, p2, p3 );
   }

   /**
    * Return a HTML code that represents the text set by the last call
    * to setStatusLine().
    * @see ifs.fnd.asp.ASPManager.setStatusLine
    */
   public String showStatusLine()
   {
      return "   "+Str.nvl(status_line,"");
   }


   /**
    * Set the name of a HTML object that should get input focus
    * when the current page is shown in a browser.
    *<p>
    * This method may be called many times.
    * The name passed to the last call will be used to set the input focus.
    */
   public void setInitialFocus( String html_name )
   {
      initial_focus = html_name;
   }

   boolean isInitialFocusSet()
   {
     return (!Str.isEmpty(initial_focus));
   }

   //==========================================================================
   //  Command Bar
   //==========================================================================

   /**
    * Return true if a command on a CommandBar has been selected by the user.
    */
   public boolean commandBarActivated()
   {
      try
      {
         if (isApplicationSearch()) return true;

         String cmd = readValue("__COMMAND");
         boolean yes = !Str.isEmpty(cmd);
         if ( yes && DEBUG ) debug("commandBarActivated(): "+cmd);
         return yes;
      }
      catch ( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Return JScript code containing the server function attached to the
    * ASPCommandBar's command that has been selected by the user.
    * The string returned from this method should be passed to the JScript
    * function eval(), for example:
    *<p>
    * <code>   eval(mgr.commandBarFunction());</code>
    * <p>
    * Note! If the user has selected the GOTO command, then the corresponding
    * JScript function will be called with one argument containing the
    * current value from the GOTO field (decreased by 1) in the activated
    * command bar.
    */
   public String commandBarFunction()
   {
      try
      {
         String blkname = null;
         String id = null;

         //find blkname from application search VIEW
         if (isApplicationSearch())
         {
            blkname = FindBlockFromView(getQueryStringValue("VIEW"));
            id = ASPCommandBar.OKFIND;
            if ( DEBUG ) debug("isApplicationSearch: blkname="+blkname+" id="+id);
         }
         else
         {
            String key = readValue("__COMMAND");
            if ( Str.isEmpty(key) )
               throw new FndException("FNDMGRCMDBARNACT: The command bar has not been activated");
            int pos = key.indexOf('.');
            blkname = pos<=0 ? null : key.substring(0,pos);
            id = key.substring(pos+1);
         }

         ASPCommandBar bar = asppage.getASPBlock(blkname).getASPCommandBar();
         String func = getCommandBarFunction(bar,blkname,id);
         if ( DEBUG ) debug("commandBarFunction(): "+func);
         return func;
      }
      catch ( Throwable e )
      {
         error(e);
         return null;
      }
   }

   private String FindBlockFromView(String view_name) throws FndException
   {
      ASPPage page = getASPPage();
      Vector blocks = page.getASPBlocks();

      for( int i=0; i<blocks.size(); i++ )
      {
         ASPBlock blk = (ASPBlock)(blocks.elementAt(i));

         if (view_name.equals(blk.getView()))
         {
            ASPBlock masterblk = blk.getMasterBlock();
            if (masterblk !=null)
               throw new FndException("FNDMGRBLKHASMASTER: ASPBlock with view '&1' has a master block", view_name);

            fillApplicationSearchFieldArray(blk);

            return blk.getName();
         }
      }

      throw new FndException("FNDMGRNOBLK: There is no ASPBlock for view '&1'", view_name);
   }

   private void fillApplicationSearchFieldArray(ASPBlock blk)
   {
      String keys = getQueryStringValue("KEYS");

      StringTokenizer st = new StringTokenizer(keys,""+IfsNames.recordSeparator);
      application_search_fields = new String[st.countTokens()][2];
      int i = 0;

      while (st.hasMoreElements())
      {
         StringTokenizer nameValue = new StringTokenizer((String)st.nextElement(),""+IfsNames.fieldSeparator);
         String name  = (String)nameValue.nextElement();
         String value = (String)nameValue.nextElement();

         application_search_fields[i][0] = name.toUpperCase();
         application_search_fields[i][1] = value;

         /*
         try
         {
            //CONSIDER: what if the field is not defined in the block ??
            ASPField fld = blk.getASPField(name);
            if (DataFormatter.INTEGER == fld.getTypeId() || DataFormatter.MONEY == fld.getTypeId() || DataFormatter.NUMBER == fld.getTypeId())
               application_search_fields[i][2] = "N";
         }
         catch (Exception any)
         {
            //CONSIDER what to do if the field is not defined in the block and the type cant be found??
            error(any);
         }
          */

         i++;
      }
   }

   // Positioning:
   // If we are not in a dialog, restore the last position saved.
   private void savePos(boolean skipCheck)
   {
      if ( asppage.isRepositioningEnabled() )
      {
         try
         {
            if ( skipCheck || !asppage.isAnyBlockInDialog() )
            {
               restore_position =
               Integer.parseInt(readValue("__SAVED_POSITION"));
            }
         }
         catch ( Exception e ) {
            ;
         }
      }
   }

   private String getCommandBarFunction( ASPCommandBar bar,
                                         String blkname,
                                         String id ) throws Exception
   {
      String GOTO    = ASPCommandBar.GOTO;
      String PERFORM = ASPCommandBar.PERFORM;
      int curr_row  = 0;

      // Positioning:
      // If we are not in a dialog at the request time, save the position.
      try
      {
         if ( asppage.isRepositioningEnabled() &&
              !asppage.isAnyBlockInDialog() )
            saved_position = Integer.parseInt(readValue("__LAST_POSITION"));
      }
      catch ( Exception e ) {
         ;
      }

      if ( id.equals(ASPCommandBar.FIND) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.setLayoutMode(bar.FIND_LAYOUT);

         savePos(true);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.CANCELFIND) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.setLayoutMode(bar.getHistoryMode());
         if ( bar.getASPRowSet().countRows()==0 )
            bar.getASPRowSet().clear();
         bar.getASPPage().getASPBlock(blkname).getASPTable().clearQueryRow();

         savePos(false);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.CANCELNEW) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         if ( bar.getASPRowSet().countRows()>1 )
            bar.getASPRowSet().clearRow();
         else
            bar.getASPRowSet().clear();
         bar.getASPPage().getASPBlock(blkname).getASPTable().clearQueryRow();
         bar.setLayoutMode(bar.getHistoryMode());

         ASPContext ctx = bar.getASPPage().getASPContext();
         curr_row = ctx.readNumber("CURRENTROW",0);
         if (bar.isSingleLayout())
            bar.getASPRowSet().goTo(curr_row);

         savePos(false);
         //return "while(false){}";
         return bar.getASPRowSet().countRows()>0?buildItemPopulateString(bar):"while(false){}"; //log id #695
      }
      else if ( id.equals(ASPCommandBar.CANCELEDIT) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.setLayoutMode(bar.getHistoryMode());
         bar.getASPRowSet().resetRow();
         bar.getASPPage().getASPBlock(blkname).getASPTable().clearQueryRow();

         savePos(false);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.EDITROW) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.getASPRowSet().store();
         bar.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);

         savePos(true);
         return "while(false){}";
      }
      // Added by Terry 20130315
      // Edit command in overview
      else if ( id.equals(ASPCommandBar.OVERVIEWEDIT) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         
         bar.setHistoryMode(bar.getLayoutMode());
         
         // Set Master blk layout to CUSTOMLAYOUT
         ASPBlock master_blk = bar.getBlock().getMasterBlock();
         while (master_blk != null)
         {
            ASPBlockLayout master_lay = master_blk.getASPBlockLayout();
            if (master_lay != null)
            {
               master_lay.setHistoryMode(master_lay.getLayoutMode());
               master_lay.setLayoutMode(master_lay.CUSTOM_LAYOUT);
            }
            master_blk = master_blk.getMasterBlock();
         }
         
         bar.getASPRowSet().store();
         
         if (bar.getBlock().hasASPTable())
            bar.getBlock().getASPTable().setEditable();
         
         savePos(true);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.OVERVIEWNEW) )
      {
         if (bar.getLayoutMode() != ASPBlockLayout.MULTIROW_LAYOUT)
            bar.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
         
         clearMyChilds(bar);
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();setDefaultValues();";

         bar.getASPRowSet().store();
         
         if (bar.getBlock().hasASPTable())
            bar.getBlock().getASPTable().setEditable();
         
         savePos(true);
         return bar.getServerFunction(ASPCommandBar.NEWROW) + "();setDefaultValues();";
      }
      else if ( id.equals(ASPCommandBar.OVERVIEWDELETE) )
      {
         int rows_deleted = 0; 
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.getASPRowSet().store();

         ASPPage container = bar.getASPPage();
         ASPBlock blk =  bar.getBlock();

         if ((container instanceof FndWebFeature))
         {
            FndDataAdapter adapter = bar.getBlock().getDataAdapter();
            if (adapter == null)
            {
               //error(new FndExcpetion("FNDASPMANAGERNOADAPTERISSET: No Data Adapter is set"));
               return "while(false){}";
            }
            else
            {
               if ( adapter instanceof FndSaveDataAdapter)
               {
                  int rowno = blk.getASPRowSet().getCurrentRowNo();
                  FndAbstractRecord rec = getSaveRecord(adapter,container.getASPBlock(blkname), rowno);
                  ((FndSaveDataAdapter)adapter).remove(rec, getASPPage());
                  getASPContext().mergeFndAbstractRecordToArray(null,blkname,rowno);
                  return "while(false){}";
               }
            }
         }

         if ( bar.isMultirowLayout() )
         {
            is_multirow_delete = true;
            rows_deleted = bar.getASPRowSet().countSelectedRows();
            bar.getASPRowSet().setSelectedRowsRemoved();
            bar.getASPRowSet().unselectRows();
         }
         else
         {
            rows_deleted = 1;
            bar.getASPRowSet().setRemoved();
         }

         if(blk.hasFileUpload())
         {
            String fuid = blk.getFileUploadId();
            String blob_id = bar.getASPRowSet().getValue(fuid);
            if(!isEmpty(blob_id))
               new FileUpload(getASPPage()).deleteBlob(blob_id);
         }

         ASPTransactionBuffer trans = newASPTransactionBuffer();
         int[] flags = new int[50];
         saveRowNos(bar, flags);
         submit(trans);

         ASPContext ctx= bar.getASPPage().getASPContext();
         int dbrows = Integer.parseInt(ctx.readValue("__"+bar.getBlock().getName()+".DBCOUNT","0"));
         int del_rows = Integer.parseInt(ctx.readValue("__"+bar.getBlock().getName()+".DEL_ROWS","0"));
         if ( dbrows>0 ) dbrows = dbrows - rows_deleted;
         del_rows+=rows_deleted;
         ctx.writeValue("__"+bar.getBlock().getName()+".DBCOUNT",""+dbrows);
         ctx.writeValue("__"+bar.getBlock().getName()+".DEL_ROWS",""+del_rows);

         boolean is_previous_dbset = false;
         if(bar.getASPRowSet().countRows()==0)
         {
            int skipped = Integer.parseInt(ctx.readValue("__"+blkname+".SKIP_ROWS","0"));
            if(dbrows>skipped)
               bar.getASPRowSet().nextDbSet();
            else
            {
               bar.getASPRowSet().prevDbSet();
               if (bar.isSingleLayout())
                  is_previous_dbset =  true;
            }
         }
         restoreRowNos(bar,flags);
         if (is_previous_dbset)
            bar.getASPRowSet().last();

         if ( bar.getASPRowSet().countRows()>0 && bar.isSingleLayout() )
         {
            AutoString returnstr = new AutoString();
            returnstr.append(buildItemPopulateString(bar));

            savePos(false);
            return returnstr.toString();
         }
         else if ( bar.isSingleLayout() )
         {
            clearMyChilds(bar);
         }
         
         if (bar.getBlock().hasASPTable())
            bar.getBlock().getASPTable().setEditable();
         
         savePos(false);
         return "while(false){};";
      }
      else if ( id.equals(ASPCommandBar.OVERVIEWSAVE) )
      {
         ASPContext ctx= bar.getASPPage().getASPContext(); 
         ctx.writeValue("__"+bar.getBlock().getName()+".LAYOUT",""+bar.getLayoutMode());
         boolean queried = false;
         // bar.setLayoutMode(bar.getHistoryMode());
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         // bar.setLayoutMode(ASPBlockLayout.NONE); //ful Buggfix
         bar.getASPRowSet().store();
         int[] saved = new int[50];
         saveRowNos(bar,saved);

         ASPPage container = bar.getASPPage();
         if ((container instanceof FndWebFeature))
         {
            FndDataAdapter adapter = bar.getBlock().getDataAdapter();
            if (adapter == null)
            {
               //error(new FndExcpetion("FNDASPMANAGERNOADAPTERISSET: No Data Adapter is set"));
               return "while(false){}";
            }
            else
            {
               if ( adapter instanceof FndSaveDataAdapter && adapter instanceof FndQueryDataAdapter)
               {
                  ASPBlock blk = container.getASPBlock(blkname);
                  FndDataRowCollection rows = (FndDataRowCollection) blk.getASPRowSet().getDataRows();
                  int rc = rows.countDataRows();
                  for(int i=0; i<rc; i++)
                  {
                     String s = rows.getRowStatus(i);
                     if(!Str.isEmpty(s) && s.equals(ASPRowSet.MODIFY))
                     {
                        FndAbstractRecord rec = getSaveRecord(adapter,blk,i);
                        rec = ((FndSaveDataAdapter)adapter).save(rec, getASPPage());
                        getASPContext().mergeFndAbstractRecordToArray(rec,blkname,i);
                     }
                  }
               }
            }
         }
         else
         {
            ASPTransactionBuffer trans = newASPTransactionBuffer();
            submit(trans);
         }

         restoreRowNos(bar,saved);
         bar.getASPPage().getASPBlock(blkname).getASPTable().clearQueryRow();
         bar.setLayoutMode(bar.getHistoryMode());

         // Set Master blk layout to History Mode
         ASPBlock master_blk = bar.getBlock().getMasterBlock();
         while (master_blk != null)
         {
            ASPBlockLayout master_lay = master_blk.getASPBlockLayout();
            if (master_lay != null)
            {
               master_lay.setLayoutMode(master_lay.getHistoryMode());
            }
            master_blk = master_blk.getMasterBlock();
         }
         
         if (bar.getBlock().hasASPTable())
            bar.getBlock().getASPTable().unsetEditable();
         
         savePos(false);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.OVERVIEWCANCEL) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.setLayoutMode(bar.getHistoryMode());
         
         // Set Master blk layout to History Mode
         ASPBlock master_blk = bar.getBlock().getMasterBlock();
         while (master_blk != null)
         {
            ASPBlockLayout master_lay = master_blk.getASPBlockLayout();
            if (master_lay != null)
            {
               master_lay.setLayoutMode(master_lay.getHistoryMode());
            }
            master_blk = master_blk.getMasterBlock();
         }
         
         bar.getASPRowSet().resetAllRows();
         bar.getASPPage().getASPBlock(blkname).getASPTable().clearQueryRow();

         savePos(false);
         return "while(false){}";
      }
      // Added end
      else if ( id.equals(ASPCommandBar.VIEWDETAILS) )
      {
         ASPPage container = bar.getASPPage();
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         // Modified by Terry 20130911
         // Original:
         // bar.getASPRowSet().store();
         try
         {  
            bar.getASPRowSet().store();
         }
         catch(Exception e)
         {
            
         }
         // Modified end
         
         bar.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
         String returnstring;
         if ((container instanceof FndWebFeature))
            returnstring = populateItemRecords(bar);
         else
            returnstring = buildItemPopulateString(bar);

         savePos(false);
         return returnstring;
      }
      else if ( id.equals(ASPCommandBar.BACK) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.setLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

         savePos(false);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.FIRST) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         if ( bar.isMultirowLayout() )
         {
            int[] saved = new int[50];
            saveRowNos(bar,saved);
            bar.getBlock().getASPRowSet().firstDbSet();
            restoreRowNos(bar,saved);

            savePos(false);
            return "while(false){}";
         }
         bar.first();
         if ( bar.isEditLayout() && bar.getHistoryMode()==bar.MULTIROW_LAYOUT ) return "while(false){}";
         String returnstring;
         if ((bar.getASPPage() instanceof FndWebFeature))
            returnstring = populateItemRecords(bar);
         else
            returnstring = buildItemPopulateString(bar);

         savePos(false);
         return returnstring;
      }
      else if ( id.equals(ASPCommandBar.BACKWARD) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         if ( bar.isMultirowLayout() )
         {
            int[] saved = new int[50];
            saveRowNos(bar,saved);
            bar.getBlock().getASPRowSet().prevDbSet();
            restoreRowNos(bar,saved);

            savePos(false);
            return "while(false){}";
         }
         bar.backward();
         if ( bar.isEditLayout() && bar.getHistoryMode()==bar.MULTIROW_LAYOUT ) return "while(false){}";
         String returnstring;
         if ((bar.getASPPage() instanceof FndWebFeature))
            returnstring = populateItemRecords(bar);
         else
            returnstring = buildItemPopulateString(bar);

         savePos(false);
         return returnstring;
      }
      else if ( id.equals(ASPCommandBar.FORWARD) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         if ( bar.isMultirowLayout() )
         {
            int[] saved = new int[50];
            saveRowNos(bar,saved);
            bar.getBlock().getASPRowSet().nextDbSet();
            restoreRowNos(bar,saved);

            savePos(false);
            return "while(false){}";
         }
         bar.forward();
         if ( bar.isEditLayout() && bar.getHistoryMode()==bar.MULTIROW_LAYOUT ) return "while(false){}";
         String returnstring;
         if ((bar.getASPPage() instanceof FndWebFeature))
            returnstring = populateItemRecords(bar);
         else
            returnstring = buildItemPopulateString(bar);

         savePos(false);
         return returnstring;
      }
      else if ( id.equals(ASPCommandBar.LAST) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         if ( bar.isMultirowLayout() )
         {
            int[] saved = new int[50];
            saveRowNos(bar,saved);
            bar.getBlock().getASPRowSet().lastDbSet();
            restoreRowNos(bar,saved);

            savePos(false);
            return "while(false){}";
         }
         bar.last();
         if ( bar.isEditLayout() && bar.getHistoryMode()==bar.MULTIROW_LAYOUT ) return "while(false){}";
         String returnstring;
         if ((bar.getASPPage() instanceof FndWebFeature))
            returnstring = populateItemRecords(bar);
         else
            returnstring = buildItemPopulateString(bar);

         savePos(false);
         return returnstring;
      }
      // Added by Terry 20121120
      // Control goto page command
      else if ( id.indexOf("GotoPage") != -1 )
      {
         if ( bar.isMultirowLayout() )
         {
            int[] saved = new int[50];
            saveRowNos(bar,saved);
            int goto_page = Integer.parseInt(id.substring(id.indexOf("(") + 1, id.indexOf(")")));
            bar.getBlock().getASPRowSet().gotoPage(goto_page);
            restoreRowNos(bar,saved);

            savePos(false);
            return "while(false){}";
         }
         return "while(false){}";
      }
      // Added end
      else if ( id.equals(ASPCommandBar.SAVENEW) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();setDefaultValues();";
         bar.getASPRowSet().store();
         ASPTransactionBuffer trans = newASPTransactionBuffer();
         int[] saved = new int[50];
         saveRowNos(bar,saved);
         submit(trans);

         restoreRowNos(bar,saved);

         ASPContext ctx= bar.getASPPage().getASPContext();
         int dbrows = Integer.parseInt(ctx.readValue("__"+bar.getBlock().getName()+".DBCOUNT","0"));
         if ( dbrows>0 ) dbrows++;
         ctx.writeValue("__"+bar.getBlock().getName()+".DBCOUNT",""+dbrows);

         savePos(false);
         return bar.getServerFunction(ASPCommandBar.NEWROW) + "();setDefaultValues();";
      }
      else if ( id.equals(ASPCommandBar.SAVERETURN) )
      {
         ASPContext ctx= bar.getASPPage().getASPContext(); 
         ctx.writeValue("__"+bar.getBlock().getName()+".LAYOUT",""+bar.getLayoutMode());
         boolean queried = false;
         bar.setLayoutMode(bar.getHistoryMode());
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.setLayoutMode(ASPBlockLayout.NONE); //ful Buggfix
         bar.getASPRowSet().store();
         int[] saved = new int[50];
         saveRowNos(bar,saved);

         ASPPage container = bar.getASPPage();
         if ((container instanceof FndWebFeature))
         {
            FndDataAdapter adapter = bar.getBlock().getDataAdapter();
            if (adapter == null)
            {
               //error(new FndExcpetion("FNDASPMANAGERNOADAPTERISSET: No Data Adapter is set"));
               return "while(false){}";
            }
            else
            {
               if ( adapter instanceof FndSaveDataAdapter && adapter instanceof FndQueryDataAdapter)
               {
                  ASPBlock blk = container.getASPBlock(blkname);
                  FndDataRowCollection rows = (FndDataRowCollection) blk.getASPRowSet().getDataRows();
                  int rc = rows.countDataRows();
                  for(int i=0; i<rc; i++)
                  {
                     String s = rows.getRowStatus(i);
                     if(!Str.isEmpty(s) && s.equals(ASPRowSet.MODIFY))
                     {
                        FndAbstractRecord rec = getSaveRecord(adapter,blk,i);
                  		rec = ((FndSaveDataAdapter)adapter).save(rec, getASPPage());
                        getASPContext().mergeFndAbstractRecordToArray(rec,blkname,i);
                     }
                  }
               }
            }
         }
         else
         {
            ASPTransactionBuffer trans = newASPTransactionBuffer();
            submit(trans);
         }

         restoreRowNos(bar,saved);
         bar.getASPPage().getASPBlock(blkname).getASPTable().clearQueryRow();
         bar.setLayoutMode(bar.getHistoryMode());

         //ASPContext ctx= bar.getASPPage().getASPContext();
         //int dbrows = Integer.parseInt(ctx.readValue("__"+bar.getBlock().getName()+".DBCOUNT","0"));
         //if ( dbrows>0 ) dbrows++;
         //ctx.writeValue("__"+bar.getBlock().getName()+".DBCOUNT",""+dbrows);

         savePos(false);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.OKFIND) )
      {
         saveQuery(blkname, true);
         bar.setLayoutMode(bar.getHistoryMode());
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         AutoString returnstring = new AutoString();
         ASPPage container = bar.getASPPage();
         if ((container instanceof FndWebFeature))
         {
            FndDataAdapter adapter = bar.getBlock().getDataAdapter();
            if (adapter == null)
            {
               //error(new FndExcpetion("FNDASPMANAGERNOADAPTERISSET: No Data Adapter is set"));
               return "while(false){}";
            }
            else
            {
               if ( adapter instanceof FndQueryDataAdapter)
               {
                  FndQueryRecord rec = ((FndQueryDataAdapter)adapter).getQueryRecord(bar.getBlock());
                  FndAbstractArray array = ((FndQueryDataAdapter)adapter).query(rec, getASPPage());
                  getASPContext().addFndAbstractArray(array,blkname);
                  container.refresh();
                  if ( array.getLength() == 0 )
                     showAlert("FNDMGRWFQUERYNODATAFOUND: No data found.");
                  return "while(false){}";
               }
            }
         }
         if ( bar.getHistoryMode()==bar.SINGLE_LAYOUT ||
              (bar.getHistoryMode()==bar.MULTIROW_LAYOUT &&
               bar.getBlock().getASPBlockLayout().isAutoLayoutSelectOn()) || isApplicationSearch())
         {
//                     if(asppage instanceof ASPPageProvider)
//                     {

//                        asppage.masterHasRows(true);
//                        returnstring.append(bar.getServerFunction(id) + "();");
//                        if(asppage.doesMasterHaveRows())
//                           returnstring.append(buildItemPopulateString(bar));

//                        asppage.masterHasRows(true);
//                        returnstring.append(bar.getServerFunction(id) + "();");
//                        returnstring.append("if(getASPPage().doesMasterHaveRows()){");
//                        returnstring.append(buildItemPopulateString(bar));
//                        returnstring.append("}");

//                        asppage.masterHasRows(true);
//                        returnstring.append(bar.getServerFunction(id) + "();");
//                        returnstring.append("if(getASPPage().doesMasterHaveRows()){");
//                        returnstring.append(buildItemPopulateString(bar));
//                        returnstring.append("}");
//                     }
//                     else
//                     {
            returnstring.append("__master_has_rows=true;");
            returnstring.append(bar.getServerFunction(id) + "();");
            returnstring.append("if(__master_has_rows){");
            returnstring.append(buildItemPopulateString(bar));
            returnstring.append("}");
//                    }
            savePos(false);
            return returnstring.toString();
         }
         returnstring.append(bar.getServerFunction(id) + "();");

         savePos(false);
         return returnstring.toString();
      }
      else if ( id.equals(ASPCommandBar.NEWROW) )
      {
         ASPContext ctx= bar.getASPPage().getASPContext();
         if (bar.isSingleLayout())
            curr_row = bar.getBlock().getASPRowSet().getCurrentRowNo();
         ctx.writeNumber("CURRENTROW",curr_row);

         bar.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
         clearMyChilds(bar);
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();setDefaultValues();";

         savePos(true);
         return bar.getServerFunction(ASPCommandBar.NEWROW) + "();setDefaultValues();";
      }
      else if ( id.equals(ASPCommandBar.COUNTFIND) )
      {
         ASPPage container = bar.getASPPage();
         if ((container instanceof FndWebFeature))
         {
            FndDataAdapter adapter = bar.getBlock().getDataAdapter();
            if (adapter == null)
            {
               //error(new FndExcpetion("FNDASPMANAGERNOADAPTERISSET: No Data Adapter is set"));
               return "while(false){}";
            }
            else
            {
               if ( adapter instanceof FndQueryDataAdapter)
               {
                  FndQueryRecord rec = ((FndQueryDataAdapter)adapter).getQueryRecord(bar.getBlock());
                  int countVal = ((FndQueryDataAdapter)adapter).count(rec, getASPPage());
                  bar.getBlock().getASPBlockLayout().setCountValue(countVal);
                  return "while(false){}";
               }
            }
         }

         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         //if ( bar.getHistoryMode()==bar.SINGLE_LAYOUT ) clearMyChilds(bar);

         savePos(false);
         return bar.getServerFunction(ASPCommandBar.COUNTFIND) + "();";
      }
      // Added by Terry 20130918
      // Advanced Query
      else if ( id.equals(ASPCommandBar.ADVANCEDFIND) )
      {
         ASPPage container = bar.getASPPage();
         if ((container instanceof FndWebFeature))
         {
            FndDataAdapter adapter = bar.getBlock().getDataAdapter();
            if (adapter == null)
            {
               //error(new FndExcpetion("FNDASPMANAGERNOADAPTERISSET: No Data Adapter is set"));
               return "while(false){}";
            }
            else
            {
               if ( adapter instanceof FndQueryDataAdapter)
               {
                  bar.getBlock().getASPBlockLayout().setAdvancedQuery();
                  return "while(false){}";
               }
            }
         }

         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";

         savePos(false);
         bar.getBlock().getASPBlockLayout().setAdvancedQuery();
         return "while(false){}";
      }
      // Added end
      else if ( id.equals(ASPCommandBar.DELETE) )
      {
         int rows_deleted = 0; 
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.getASPRowSet().store();

         ASPPage container = bar.getASPPage();
         ASPBlock blk =  bar.getBlock();

         if ((container instanceof FndWebFeature))
         {
            FndDataAdapter adapter = bar.getBlock().getDataAdapter();
            if (adapter == null)
            {
               //error(new FndExcpetion("FNDASPMANAGERNOADAPTERISSET: No Data Adapter is set"));
               return "while(false){}";
            }
            else
            {
               if ( adapter instanceof FndSaveDataAdapter)
               {
                  int rowno = blk.getASPRowSet().getCurrentRowNo();
                  FndAbstractRecord rec = getSaveRecord(adapter,container.getASPBlock(blkname), rowno);
                  ((FndSaveDataAdapter)adapter).remove(rec, getASPPage());
                  getASPContext().mergeFndAbstractRecordToArray(null,blkname,rowno);
                  return "while(false){}";
               }
            }
         }

         if ( bar.isMultirowLayout() )
         {
            is_multirow_delete = true;
            rows_deleted = bar.getASPRowSet().countSelectedRows();
            bar.getASPRowSet().setSelectedRowsRemoved();
            bar.getASPRowSet().unselectRows();
         }
         else
         {
            rows_deleted = 1;
            bar.getASPRowSet().setRemoved();
         }

         if(blk.hasFileUpload())
         {
            String fuid = blk.getFileUploadId();
            String blob_id = bar.getASPRowSet().getValue(fuid);
            if(!isEmpty(blob_id))
               new FileUpload(getASPPage()).deleteBlob(blob_id);
         }

         ASPTransactionBuffer trans = newASPTransactionBuffer();
         int[] flags = new int[50];
         saveRowNos(bar, flags);
         submit(trans);

         ASPContext ctx= bar.getASPPage().getASPContext();
         int dbrows = Integer.parseInt(ctx.readValue("__"+bar.getBlock().getName()+".DBCOUNT","0"));
         int del_rows = Integer.parseInt(ctx.readValue("__"+bar.getBlock().getName()+".DEL_ROWS","0"));
         if ( dbrows>0 ) dbrows = dbrows - rows_deleted;
         del_rows+=rows_deleted;
         ctx.writeValue("__"+bar.getBlock().getName()+".DBCOUNT",""+dbrows);
         ctx.writeValue("__"+bar.getBlock().getName()+".DEL_ROWS",""+del_rows);

         boolean is_previous_dbset = false;
         if(bar.getASPRowSet().countRows()==0)
         {
            int skipped = Integer.parseInt(ctx.readValue("__"+blkname+".SKIP_ROWS","0"));
            if(dbrows>skipped)
               bar.getASPRowSet().nextDbSet();
            else
            {
               bar.getASPRowSet().prevDbSet();
               if (bar.isSingleLayout())
                  is_previous_dbset =  true;
            }
         }
         restoreRowNos(bar,flags);
         if (is_previous_dbset)
            bar.getASPRowSet().last();

         if ( bar.getASPRowSet().countRows()>0 && bar.isSingleLayout() )
         {
            AutoString returnstr = new AutoString();
            returnstr.append(buildItemPopulateString(bar));

            savePos(false);
            return returnstr.toString();
         }
         else if ( bar.isSingleLayout() )
         {
            clearMyChilds(bar);
         }
         savePos(false);
         return "while(false){};";
      }
      else if ( id.equals(ASPCommandBar.DUPLICATEROW) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.getBlock().getASPRowSet().store();
         bar.getBlock().getASPRowSet().duplicatePressed3();
         bar.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
         clearMyChilds(bar);

         savePos(true);
         return bar.getServerFunction(ASPCommandBar.NEWROW) + "();";
      }
      else if ( id.equals(ASPCommandBar.DUPLICATE) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.getBlock().getASPRowSet().duplicatePressed();

         savePos(false);
         return bar.getServerFunction(ASPCommandBar.PREPARE) + "();";
      }
      else if ( id.equals(GOTO) )
      {
         //String func = bar.getServerFunction(id);
         String val = readValue("__"+blkname+"_"+GOTO);
         int nr;
         try
         {
            nr = Str.isEmpty(val) ? 0 : Integer.parseInt(val);
         }
         catch ( NumberFormatException f )
         {
            nr = 0;
         }
         nr--;
         //return func +"("+String.valueOf(nr)+");";
         bar.getBlock().getASPRowSet().goTo(nr);

         savePos(false);
         return "while(false){}";
      }
      else if ( id.equals(PERFORM) )
      {
         String customid = readValue("__"+Str.nvl(blkname,"")+"_"+PERFORM);
         
         // Added by Terry 20130911
         // Check command bar item property
         if (bar.isCmdProperty(customid))
         {
            ASPPage container = bar.getASPPage();
            if(container instanceof ASPPageProvider)
            {
               ASPRowSet rowset = bar.getASPRowSet();
               if (rowset.countRows() > 0)
               {
                  String command_bar_string = bar.getCmdPropertyString(customid, rowset);
                  if (!isEmpty(command_bar_string))
                  {
                     AutoString returnstr = new AutoString();
                     returnstr.append("showNewBrowser('", command_bar_string);
                     returnstr.append("');\n");
                     ((ASPPageProvider)container).appendDirtyJavaScript(returnstr.toString());
                     return "while(false){}";
                  }
               }
            }
         }
         // Added end
         
         String func = bar.getServerFunction(customid);

         if (asppage.getASPBlock(blkname).getASPBlockLayout().isMultirowLayout() && asppage.getASPBlock(blkname).getASPTable().isRowSelectionEnabled() && !bar.isPageControlingActionButton())
         {
            performActionOnSelectedRows(bar,asppage.getASPBlock(blkname), func, customid);
            return "while(false){}";
         }
         else
         {
            savePos(false);
            return func+"();";
         }
      }
      else if ( id.startsWith("OutputChannel") )
      {
         ASPBlock blk = asppage.getASPBlock(blkname);
         ASPTable tbl = this.getASPTable(blk);
         String channel = id.substring(id.indexOf("-")+1,id.length());

         new ASPOutputChannel(blk, tbl,channel);
         return " ";   //return blank string because there is nothing to do any more
      }
      else if ( id.equals(ASPCommandBar.SAVE) )
      {
         if ( bar.findItem(id).isUserDefinedServerFunction() )
            return bar.getServerFunction(id) + "();";
         bar.getASPRowSet().store();
         ASPTransactionBuffer trans = newASPTransactionBuffer();
         int[] flags = new int[50];
         saveRowNos(bar, flags);
         submit(trans);
         restoreRowNos(bar,flags);

         savePos(false);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.DELETEQUERY) )
      {
         deleteQuery(blkname);
         return "while(false){}";
      }
      else if ( id.equals(ASPCommandBar.SAVEQUERY) )
      {
         saveQuery(blkname, false);
         return "while(false){}";
      }
      else if ( id.equals("isAuthorizedToUpload()") )
      {
         return "isAuthorizedToUpload()";
      }
      // Added by Terry 20130902
      // Add document reference bar
      else if ( id.equals(ASPCommandBar.DOCREF) )
      {
         ASPPage container = bar.getASPPage();
         if(container instanceof ASPPageProvider)
         {
            ASPRowSet rowset = bar.getASPRowSet();
            ASPConfig  cfg = getASPConfig();
            // rowset.store();
            
            AutoString returnstr = new AutoString();
            String obj_id = rowset.getSafeValueAt(rowset.getCurrentRowNo(), "OBJID");
            returnstr.append("showNewBrowser('", cfg.getApplicationPath());
            returnstr.append("/", cfg.getParameter("DOCMAW/DOC_REF_URL"));
            returnstr.append("?view=", URLEncode(bar.getBlock().getDocManView()));
            returnstr.append("&objid=", URLEncode(obj_id));
            returnstr.append("');\n");
            ((ASPPageProvider)container).appendDirtyJavaScript(returnstr.toString());
         }
         return "while(false){}";
      }
      // Added end
      else
      {
         // Added by Terry 20130911
         // Check command bar item property
         if (bar.isCmdProperty(id))
         {
            ASPPage container = bar.getASPPage();
            if(container instanceof ASPPageProvider)
            {
               ASPRowSet rowset = bar.getASPRowSet();
               if (rowset.countRows() > 0)
               {
                  String command_bar_string = bar.getCmdPropertyString(id, rowset);
                  if (!isEmpty(command_bar_string))
                  {
                     AutoString returnstr = new AutoString();
                     returnstr.append("showNewBrowser('", command_bar_string);
                     returnstr.append("');\n");
                     ((ASPPageProvider)container).appendDirtyJavaScript(returnstr.toString());
                     return "while(false){}";
                  }
               }
            }
         }
         // Added end
         
         String func = bar.getServerFunction(id);
         return func + "();";
      }
   }

   void saveQuery(String blkname, boolean to_previous)
   {
      ASPBlock blk = asppage.getASPBlock(blkname);
      ASPField[] flds = blk.getFields();
      String value = "";
      String name = "";

      value = "__CASESS_VALUE^" + readValue("__CASESS_VALUE")+ (char)31;

      if ("Y".equals(readValue("__CASESS_VALUE")))
         value += "CASESENCETIVE^TRUE"+ (char)31;
      else
         value += "CASESENCETIVE^FALSE"+ (char)31;

      for (int i=0; i<flds.length; i++)
      {
         name = flds[i].getName();
         //if (flds[i].isQueryable()&& !flds[i].isHidden() && !isEmpty(readValue(name)))
         if (!isEmpty(readAbsoluteValue(name)))
         {
            //name = flds[i].getName();
            //value += name + "^" + (isEmpty(readValue(name))?"":readValue(name))+ (char)31;
            value += name + "^" + readAbsoluteValue(name)+ (char)31;
         }
      }

      ASPBuffer profile = blk.getQueryProfile();

      if (to_previous)
         profile.setValueAt(0,value); // set value to previous query.
      else
      {
         String query_name = "Saved Query^"+readValue("__QUERY_NAME");
         int item_postion = profile.getItemPosition(query_name);
         if (item_postion < 0)
            profile.addItem(query_name,value);  // add new
         else
            profile.setValueAt(item_postion,value); // modify existing
      }

      if(!getASPPage().getASPProfile().isUserProfileDisabled())
         blk.saveQueryProfile(profile);
   }

   void deleteQuery(String blkname) throws FndException{
      ASPBlock blk = asppage.getASPBlock(blkname);
      ASPBuffer profile = blk.getQueryProfile();
      int selected_index = Integer.valueOf(readValue("__QUERY_INDEX")).intValue();
      blk.removeSavedQuery(profile.getNameAt(selected_index));
      profile.removeItemAt(selected_index);
      blk.saveQueryProfile(profile);
   }


   void performActionOnSelectedRows(ASPCommandBar bar, ASPBlock blk, String func, String customid) throws FndException
   {
      ASPPageProvider page_provider;
      ASPCommandBarItem item;
      String func_not_performed_on = "";
      Vector perform_action_on = new Vector();
      Vector selected_rows = new Vector();

      ASPRowSet rowset = blk.getASPRowSet();

      item = bar.findItem(customid);

      int current_row_no = rowset.getCurrentRowNo();

      rowset.storeSelections();
      for(int j=0; j<rowset.countRows();j++)
      {
         rowset.goTo(j);
         if (rowset.isRowSelected())
         {
            if (item.conditionsTrue(j))
               perform_action_on.add(new Integer(j));
            else
               func_not_performed_on += ""+(j+1)+",";

            selected_rows.add(new Integer(j));
          }
      }
      rowset.unselectRows();
      rowset.goTo(current_row_no);
      for (int i=0; i<perform_action_on.size(); i++)
      {
         rowset.selectRow(((Integer) perform_action_on.elementAt(i)).intValue());
         //rowset.goTo(((Integer) perform_action_on.elementAt(i)).intValue());
         page_provider = (ASPPageProvider)asppage;
         page_provider.eval(func);
         //rowset.refreshRow();
         rowset.unselectRow(((Integer) perform_action_on.elementAt(i)).intValue());
      }

      for (int i=0; i<selected_rows.size(); i++)
      {
         rowset.selectRow(((Integer) selected_rows.elementAt(i)).intValue());
      }

      if (!isEmpty(func_not_performed_on))
         showAlert(translate("ASPMANAGERNOTPERFOMEDON: Selected action was not performed on following rows: ") +"\n"+ func_not_performed_on.substring(0,func_not_performed_on.length()-1));

   }


   void restoreRowNos(ASPCommandBar bar, int[] saved)
   {
      Vector blocks = bar.getASPPage().getASPBlocks();
      for ( int i=0;i<blocks.size();i++ )
         if ( !((ASPBlock) blocks.elementAt(i)).getASPRowSet().goTo(saved[i]) )
            ((ASPBlock) blocks.elementAt(i)).getASPRowSet().last();
   }

   void saveRowNos(ASPCommandBar bar, int[] saved)
   {
      Vector blocks = bar.getASPPage().getASPBlocks();
      for ( int i=0;i<blocks.size();i++ )
         saved[i]=(((ASPBlock) blocks.elementAt(i)).getASPRowSet().getCurrentRowNo());
   }

   /**
    *Clears the rowset of every child of a Block
    */

   void clearMyChilds(ASPCommandBar bar)
   {
      ASPBlock blk = bar.getBlock();
      Vector blocks = blk.getASPPage().getASPBlocks();
      clearthem(blk,blocks);
   }

   void clearMyChilds(ASPBlock blk)
   {
      Vector blocks = blk.getASPPage().getASPBlocks();
      clearthem(blk,blocks);
   }


   void clearthem(ASPBlock mblk, Vector blocks)
   {
      ASPBlock currblk;
      for ( int i=0;i<blocks.size();i++ )
      {
         currblk = (ASPBlock) blocks.elementAt(i);
         if ( currblk.getMasterBlock()==null ) continue;
         if ( mblk.getName().equals(currblk.getMasterBlock().getName()) )
         {
            currblk.getASPRowSet().clear();
            clearthem(currblk, blocks);
         }
      }
   }


   /**
    * Builds a string of okFind functions for all the
    * children of a block
    */
   private String buildItemPopulateString(ASPCommandBar bar)
   {
      ASPBlock blk = bar.getBlock();
      Vector blocks = blk.getASPPage().getASPBlocks();
      String returnstring;
      returnstring = buildit(blk,blocks).toString();
      if ( !(asppage instanceof ASPPageProvider) && returnstring.equals("") )
         return "while(false){};";
      if ( DEBUG ) debug("ASPManager.buildItemPopulateString returned: "+returnstring);
      return returnstring;
   }

   private AutoString buildit(ASPBlock mblk, Vector blocks)
   {
      ASPBlock currblk;
      AutoString execstring = new AutoString();

      for ( int i=0;i<blocks.size();i++ )
      {
         currblk = (ASPBlock) blocks.elementAt(i);
         if ( currblk.getMasterBlock()==null ) continue;
         if ( mblk.getName().equals(currblk.getMasterBlock().getName()) )
         {
            if ( !currblk.getASPBlockLayout().isMultirowLayout() && !currblk.getASPBlockLayout().isSingleLayout() )
               continue;
            ASPCommandBar bar = currblk.getASPCommandBar();
            try
            {
               if ( bar != null && bar.findItem(bar.OKFIND).isUserDefinedServerFunction() )
               {
                  String serfun = bar.getServerFunction(bar.OKFIND);
                  if ( Str.isEmpty(serfun) )
                     execstring.append("okFind"+currblk.getName()+"();");
                  else
                     execstring.append(serfun+"();");
               }
               else
                  execstring.append("okFind"+currblk.getName()+"();");
            }
            catch ( FndException x )
            {
               execstring.append("okFind"+currblk.getName()+"();");
            }
            execstring.append(buildit(currblk, blocks));
         }
      }
      return execstring;
   }

   /**
    * Return true if a button with the given name has been pressed by the user.
    */
   public boolean buttonPressed( String name )
   {
      try
      {
         boolean yes = !Str.isEmpty(readValue(name));
         if ( yes && DEBUG ) debug("buttonPressed("+name+")");
         return yes;
      }
      catch ( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Return true if a link command has been selected by the user.
    */
   public boolean commandLinkActivated()
   {
      try
      {
         String cmd = readValue("__LINK");
         boolean yes = !Str.isEmpty(cmd);
         if ( yes && DEBUG ) debug("commandLinkActivated(): "+cmd);
         return yes;
      }
      catch ( Throwable any )
      {
         error(any);
         return false;
      }
   }

   /**
    * Return JScript code containing the server function attached to the
    * link that has been selected by the user.
    * The string returned from this method should be passed to the JScript
    * function eval(), for example:
    *<p>
    * <code>   eval(mgr.commandLinkFunction());</code>
    */
   public String commandLinkFunction()
   {
      try
      {
         String key = readValue("__LINK");
         if ( Str.isEmpty(key) )
            throw new FndException("FNDMGRCMDLINKNACT: No command link has been activated");
         String func = key;
         if ( DEBUG ) debug("commandLinkFunction(): "+func);
         return func + "();";
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   // Client Java Script Generation
   //==========================================================================

   private int repeat_finc_call = 0;
   /**
    * Generate and return a JavaScript code that implements LOV, Validation
    * and Check functionality defined for all ASPFields on the current ASP page.
    * The method should be called from an ASP file just before the &lt;/FORM&gt; tag.
    * as follows:
    * <pre>
    *    &lt;%=mgr.generateClientScript()%&gt;
    * &lt;/FORM&gt;
    * </pre>
    * where mgr is a JScript variable containing the reference to an instance
    * of this class.
    *
    * @see ifs.fnd.asp.ASPField.setLOV
    * @see ifs.fnd.asp.ASPField.setValidation
    * @see ifs.fnd.asp.ASPField.setMandatory
    * @see ifs.fnd.asp.ASPField.setUpperCase
    * @see ifs.fnd.asp.ASPField.setReadOnly
    * @see ifs.fnd.asp.ASPBlock.generateAssignments
    * @see ifs.fnd.asp.ASPBlock.generateEmptyAssignments
    */
   public String generateClientScript()
   {
      try
      {
         repeat_finc_call++;
         if ( repeat_finc_call>1 )
            log("ASPManager: Function generateClientScript() called "+repeat_finc_call+" times.");

         return asppage.generateClientScript();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   void appendRequestClientScript( AutoString code ) throws Exception
   {
      boolean new_page = isExplorer() && Str.isEmpty(readValue("__PAGE_ID")) && Str.isEmpty(getQueryString());

      code.append(ASPPage.BEGIN_SCRIPT_TAG);
      code.append("NEW_PAGE = ");
      code.appendBoolean(new_page);
      code.append(";\n");

      //code.append("CURRENT_URL =\"",getURL(),"?",getQueryString(),"\";\n");
      code.append("CURRENT_URL =\"", encodeStringForJavascript( getURL() ),"?", encodeStringForJavascript( getQueryString() ),"\";\n");

      code.append("NEVER_EXPIRE = ");
      code.appendBoolean(context.isCacheUserSaved()  || !context.isCacheUserRemoved());
      //code.appendBoolean(page_never_expires);

      code.append(";\n");

      code.append(ASPPage.END_SCRIPT_TAG);

      generateHTMLFields(code);
   }


   private boolean head_tag_generated = false;

   /**
    * Generate sub tags for the <HEAD> HTML tag. In webkit 3, use generateHeadTag(String).
    */
   public String generateHeadTag()// throws FndException
   {
      return genHTag(false);
   }

   private String genHTag(boolean subcalled)// throws FndException
   {
      if ( asppage.getVersion() >=3 && !subcalled )
         error(new FndException("FELHEAD3TAG: In webkit 3 you have to use generateHeadTag(String Title)"));

      head_tag_generated = true;
      ASPConfig cfg = getASPConfig();
      if(isLogonPage())
          return asppage.getHeadTag() + cfg.getHeadTag();
      else
          return asppage.getHeadTag() + cfg.getNewHoverScript() + cfg.getHeadTag();

   }

   /**
    * Generate attributes for the <BODY> HTML tag
    */
   public String generateBodyTag()
   {
//      return getASPConfig().getBodyTag();
      if(isMobileVersion())
         return generateMobileBodyTag();

      try
      {
         AutoString tag = new AutoString();
         
         // Added by Terry 20131106
         // Add background for portal page
         if (isPortalPage())
         {
            String imgSrc = getASPConfig().getImagesLocation();
            tag.append("style=\"background-image:url(" + imgSrc + "Portal_Background.gif);background-repeat:no-repeat; background-position:center\"");
         }
         // Added end
         
         tag.append(getASPConfig().getBodyTag(),isExplorer()?" onKeyPress=\"keyPressed()\"":"", ">");
         if(!isNetscape4x())
            tag.append("\n"+generatePageMaskTag());
//       asppage.generatePopupDefinitions(tag);
         tag.append("<x");
         return tag.toString();
      }
      catch ( Throwable e ) {
         error(e); return "";
      }
   }


   /**
    * Returns the path to the directory of translates images.
    * This method is obsolete since Application 7.5. Translated images are no longer used.
    * @deprecated
    */
   public String getTranslatedImageLocation()
   {
      return "";//config.getParameter("APPLICATION/LOCATION/TRANSLATED_IMAGES",config.getParameter("APPLICATION/LOCATION/ROOT") + "common/images/translated/")+getUserLanguage()+"/";
   }

   public String getToolBarImageLocation()
   {
       return ((isMobileVersion())? config.getMobileImageLocation(): config.getImagesLocation()) + "toolbar/";

   }

   public String getToolBarImageLocationWithRTL()
   {
       return ((isMobileVersion())? config.getMobileImageLocation(): config.getImagesLocationWithRTL()) + "toolbar/";
   }

   /**
    * Generate attributes for the <FORM> HTML tag
    */
   public String generateFormTag()
   {
      return asppage.generateFormTag();
   }

   boolean getHoverEnabled()
   {
      return("Y".equals(getConfigParameter("PAGE/HOVER_ENABLED","N")));
   }

   String getEmptyImage( int width, int height )
   {
      return "<img src=\"" + getASPConfig().getImagesLocation() + "table_empty_image.gif\" width=\"" + width + "\" height=\"" + height + "\">";
   }

   private void appendBroadcastMessages(AutoString bar)
   {
      //TODO: COMMON MESSAGES should moved to some where!!
      if (context.isUserLoggedOnFND() && IFSCommonMessages.hasValidMessages(this))
      {
         String ifs_messages = IFSCommonMessages.getValidMessages(this);
         if (!isEmpty(ifs_messages) )
         {
            String icon = getASPConfig().getImagesLocation()+"bm_icon.gif";
            int message_length = Integer.parseInt(getConfigParameter("IFS_BROADCAST_MESSAGES/MAX_LENGTH",Integer.toString(IFSCommonMessages.MAX_LENGTH)));
            bar.append("<span class=\"commonMessageText\" id=\"broadcast_msg\" style=\"width:100%; left:0; top:0; border-width:0px; overflow:hidden;\">&nbsp;</span>\n");
            int ms_cnt = 0;
            bar.append("\n<script language=\"javascript\" >\n");
            bar.append("var broadcast_msg_icon = \"",icon,"\";\n");
            bar.append("var texta = new Array();\n");
            bar.append("var textfull = new Array();\n");
            StringTokenizer ms = new StringTokenizer(ifs_messages,"^");
            while(ms.hasMoreTokens())
            {
               String brd_msg = ms.nextToken();
               bar.append("textfull["+(ms_cnt)+"] = \""+brd_msg+"\";\n");
               if(brd_msg.length()>message_length)
                  brd_msg = brd_msg.substring(0,message_length-3)+"...";
               bar.append("texta["+(ms_cnt++)+"] = \"&quot;"+brd_msg+"&quot;\";\n");
            }
            bar.append("var mc="+ms_cnt+";\n");
            bar.append("</script>\n");
            bar.append("</td>\n");
            bar.append("<td nowrap width='25px'>\n");
            bar.append("<a href=\"javascript:showHideBroadcastMessages();\">\n");
            String img_title = translate("ASPMANAGERVIEWALLBRDMSGS: View All Broadcast Messages");
            bar.append("<img style=\"margin-top:2\" alt=\""+img_title+"\" title=\""+img_title+"\" border=0 src=\"",icon,"\">\n");
            bar.append("</a>\n");
         }
         else
            bar.append("<img src='",getASPConfig().getImagesLocation(),"6px_space.gif'>");
      }
      else
         bar.append("<img src='",getASPConfig().getImagesLocation(),"6px_space.gif'>");
   }

   /**
    * Generate toolbar
    */
   private void appendRightHeader( AutoString bar, boolean header_disabled)
   {
      if ( DEBUG ) debug(this+": ASPManager.generateToolbar()");

      ASPConfig cfg = asppage.getASPConfig();
      String[] images = cfg.getToolBarImages();
      AutoString png_correction = new AutoString();

      /**
       * getToolBarImages() returns an array with following image names.
          * 0 - Home
          * 1 - Home With down arrow
          * 2 - Refresh
          * 3 - Options
          * 4 - Navigator
          * 5 - Navigator With down arrow
          * 6 - Configure
          * 7 - Help
          * 8 - What's this
          * 9 - IFS Logo
       */

      //String imgloc = getTranslatedImageLocation();
      String img_loc = (isMobileVersion())? cfg.getMobileImageLocation(): cfg.getImagesLocation();
      ASPPortal portal = asppage.getASPPortal();
      Buffer buf = asppage.getPortalViews();

      if(!cfg.isHomeIconEnabled() || isMobileVersion())
         asppage.disableHomeIcon();
      if(!cfg.isOptionsEnabled() || isMobileVersion())
         asppage.disableOptions();
      if(!cfg.isNavigateEnabled())
         asppage.disableNavigate();
      if(!cfg.isHelpEnabled())
         asppage.disableHelp();
      if(!cfg.isPortalConfigEnabled() || isMobileVersion())
         asppage.disableConfiguration();

      if ( asppage.getASPLov() == null)
      {
          if (context.isUserLoggedOnFND())
          {
             String image;
             String tr_text = translateJavaText("FNDMGRPERSONALPORTALREFRESHNEW: Refresh");
             boolean group1=false, group2=false;

             if (portal!=null)
             {
                bar.append("<a href=\"javascript:setFormReference('');refreshPortal();\"><img ");
                bar.append("name=\"refresh\" id=\"__hd_refresh_png\" src=\""+getDummyImage()+"\"");
                bar.append(" alt=\"",tr_text,"\"");
                bar.append(" title=\"",tr_text,"\"");
                bar.append(" border=\"0\"></a>&nbsp;");
                png_correction.append("setImageType(document.getElementById('__hd_refresh_png'),'"+img_loc+images[2].substring (0,images[2].length ()-4)+"');\n");
                group1 = true;
             }

             //Home Button
             if(portal==null && !asppage.isHomeIconDisabled())
             {
                if(buf!=null && buf.countItems()>1 && (portal==null || !portal.isCustomizeMode()))
                {
                   bar.append("<a href=\"",asppage.getASPPopup("views").generateCall(),"\">");
                   image = images[1];
                   image = image.substring (0,image.length ()-4);
                   png_correction.append("setImageType(document.getElementById('__hd_home_png'),'"+img_loc+image+"');\n");
                }
                else
                {
                   image = images[0];
                   image = image.substring (0,image.length ()-4);
                   bar.append("<a href=\"",correctURL(getConfigParameter("APPLICATION/LOCATION/PORTAL")),"\">");
                   png_correction.append("setImageType(document.getElementById('__hd_home_png'),'"+img_loc+image+"');\n");

                }

                tr_text = translateJavaText("FNDMGRPERSONALPORTAL1: Home");
                bar.append("<img ");
                bar.append("onclick=\"javascript:menuClicked(this);\" ");
                bar.append("name=\"personalportal\" id=\"__hd_home_png\" src=\""+getDummyImage()+"\"");
                bar.append(" alt=\"",tr_text,"\"");
                bar.append(" title=\"",tr_text,"\"");
                bar.append(" border=\"0\"></a>&nbsp;");

                group1 = true;
             }

             if (group1)
             {
                bar.append("<img id=\"separator1\" ");
                bar.append("src=\""+getDummyImage()+"\"");
                bar.append(" border=\"0\">&nbsp;");
                png_correction.append("setImageType(document.getElementById('separator1'),'"+img_loc,"/"+getUserTheme(),"/separator');\n");
             }

             //Options Button
             if ( !asppage.isOptionsDisabled() )
             {
                tr_text = translateJavaText("FNDMGRPORTALOPTIONS: Options");

                bar.append("<a href=\"",asppage.getASPPopup("options").generateCall(),"\"");
                bar.append(">");
                bar.append("<img ");
                bar.append("onclick=\"javascript:menuClicked(this);\" ");
                bar.append("name=\"tooloptions\" id=\"__hd_options_png\" SRC=\""+getDummyImage()+"\"");
                bar.append(" alt=\"",tr_text,"\"");
                bar.append(" title=\"",tr_text,"\"");
                bar.append(" border=\"0\"></a>&nbsp;");
                png_correction.append("setImageType(document.getElementById('__hd_options_png'),'"+img_loc+images[3].substring (0,images[3].length ()-4)+"');\n");

             }


             //Navigate Button
             /* Comment by Terry 20120822
             if ( !asppage.isNavigateDisabled() )
             {

                ASPPopup pop = asppage.getASPPopup("navigate");
                if(pop.countItems()>1)
                {
                   bar.append("<a href=\"",asppage.getASPPopup("navigate").generateCall(),"\"");
                   image = images[5];
                   image = image.substring (0,image.length ()-4);
                   png_correction.append("setImageType(document.getElementById('__hd_navigate_png'),'"+img_loc+image+"');\n");
                }
                else
                {
                   bar.append("<a href=\"",correctURL(getConfigParameter("APPLICATION/LOCATION/NAVIGATOR")),"\"");
                   image = images[4];
                   image = image.substring (0,image.length ()-4);
                   png_correction.append("setImageType(document.getElementById('__hd_navigate_png'),'"+img_loc+image+"');\n");
                }

                tr_text = translateJavaText("FNDMGRPORTALNAVIGATOR: Navigate");

                bar.append(">");
                bar.append("<img ");
                bar.append("onclick=\"javascript:menuClicked(this);\" ");
                bar.append("name=\"toolnav\" id=\"__hd_navigate_png\" ");
                if(isMobileVersion())
                   bar.append(" src=\""+img_loc+image+".gif\" ");
                bar.append(" alt=\"",tr_text,"\"");
                bar.append(" title=\"",tr_text,"\"");
                bar.append(" border=\"0\"></a>&nbsp;");

             }
             */

             //Manage Portal Button
             if ( !asppage.isConfigurationDisabled() && !asppage.getASPProfile().isUserProfileDisabled())
             {
                if ( portal==null || !portal.isCustomizeMode() )
                {

                   tr_text = translateJavaText("FNDMGRPORTALMANAGEVIEW: Manage this view");
                   if ( portal==null )
                      bar.append("<a href=config.html ");
                   else
                       bar.append("<a href=\"",asppage.getASPPopup("config_views").generateCall(),"\" ");
                   bar.append(">");
                   bar.append("<img ");
                   bar.append("onclick=\"javascript:menuClicked(this);\" ");
                   bar.append("name=\"toolconfig\" id=\"__hd_config_views_png\"  SRC=\""+getDummyImage()+"\"");
                   bar.append(" alt=\"",tr_text,"\"");
                   bar.append(" title=\"",tr_text,"\"");
                   bar.append(" border=\"0\"></a>&nbsp;");
                   png_correction.append("setImageType(document.getElementById('__hd_config_views_png'),'",img_loc,images[6].substring (0, images[6].length ()-4),"');\n");
                }
             }

             //Help Button
             if ( !asppage.isHelpDisabled() )
             {
                tr_text = translateJavaText("FNDMGRPORTALHELP2: Help");

                bar.append("<a href=\"",asppage.getASPPopup("help").generateCall(),"\" ");
                bar.append(">");
                bar.append("<img ");
                bar.append("onclick=\"javascript:menuClicked(this);\" ");
                bar.append("onclick=\"javascript:menuCliked(this);\" name=toolhelp id=\"__hd_help_png\" ");
                if(isMobileVersion())
                   bar.append("src=\""+img_loc+images[7].substring (0, images[7].length ()-4)+".gif\"");
                bar.append(" alt=\"",tr_text,"\"");
                bar.append(" title=\"",tr_text,"\"");
                bar.append(" border=\"0\"></a>&nbsp;");
                png_correction.append("setImageType(document.getElementById('__hd_help_png'),'",img_loc,images[7].substring (0, images[7].length ()-4),"');\n");

                if (!isPortalPage() && !isMobileVersion())
                {
                   //Help On Fields
                   // bar.append("<img id=\"separator2\" ");
                   // bar.append("src=\""+getDummyImage()+"\"");
                   // bar.append(" border=\"0\">&nbsp;");

                   bar.append("<a href=\"javascript:initHelpFields()\"");
                   bar.append(">");
                   // png_correction.append("setImageType(document.getElementById('separator2'),'"+img_loc,"/"+getUserTheme(),"/separator');\n");
                   bar.append("<img ");
                   bar.append("name=toolfieldhelp id=\"__whatsthis_png\" SRC=\""+getDummyImage()+"\"");
                   tr_text = translateJavaText("FNDMGRPORTALWHATSTHISHELP: What is This?");
                   bar.append(" alt=\"",tr_text,"\"");
                   bar.append(" title=\"",tr_text,"\"");
                   bar.append(" border=\"0\"></a>&nbsp;");
                   png_correction.append("setImageType(document.getElementById('__whatsthis_png'),'",img_loc,images[8].substring (0, images[8].length ()-4),"');\n");
                }
             }
          }

          if(!isMobileVersion())
          {
            //IFS Logo
            String tr_ifs_home = translateJavaText("FNDSERNAVIIFSONWEB: IFS on the Web");

            // bar.append("<img  id=\"separator3\" ");
            // bar.append("src=\""+getDummyImage()+"\"");
            // bar.append(" border=\"0\">&nbsp;");
            // png_correction.append("setImageType(document.getElementById('separator3'),'"+img_loc,"/"+getUserTheme(),"/separator');\n");

            //application search field here
            if ( !asppage.isApplicationSearchDisabled() && isPortalPage())
            {
               appendApplicationSearch(bar);

               png_correction.append("setImageType(document.getElementById('appsearchbutton'),'"+img_loc+"search1_24x24');\n");
               png_correction.append("setImageType(document.getElementById('separator4'),'"+img_loc,"/"+getUserTheme(),"/separator');\n");
            }

            // Modified by Terry 20120822
            // Modify help link to IFS Nav.
            // Original: bar.append("<a href=\"javascript:showNewStyledBrowser('http://www.ifsworld.com','status,resizable,scrollbars,width=790,height=575')\">");
            if (isPortalPage())
            {
               bar.append("<a href=\"../secured/corpvw/IFSCorpvView.page\">");
               bar.append("<img ");
               bar.append(" id=\"__ifs_log_big_png\" SRC=\""+getDummyImage()+"\"");
               bar.append(" alt=\"",tr_ifs_home,"\"");
               bar.append(" title=\"",tr_ifs_home,"\"");
               bar.append(" border=\"0\"></a>&nbsp;");
               png_correction.append("setImageType(document.getElementById('__ifs_log_big_png'),'",img_loc,images[9].substring (0, images[9].length ()-4),"');\n");
            }
            // Modified end

                bar.append("<script language='javascript'>\n " + getASPConfig().getPNGCorrectionScript() + "\n" +
                    png_correction.toString() + "</script> ");
          }

      }
   }

   private void appendPortalTabs(AutoString bar)
   {

      ASPPortal portal = asppage.getASPPortal();
      Vector names = new Vector();
      Vector descs = new Vector();
      int tab_count = 1;
      String home = getConfigParameter("APPLICATION/LOCATION/PORTAL");
      Buffer views = asppage.getPortalViews();
      String img_loc = getASPConfig().getImagesLocation();
      String theme_img_loc = img_loc + getUserTheme() + "/";

      if(asppage.getASPPortal()!=null && !asppage.getASPPortal().isCustomizeMode() && views.countItems()>1)
      {
           bar.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
           //bar.append("<tr width=\"100%\"><td colsapn=\"3\" height=\"10\"></td></tr>\n");
           bar.append("<tr width=\"100%\">\n");
           bar.append("<td background=\""+theme_img_loc+"tabline.gif\" >&nbsp;&nbsp;</td>");
           bar.append("<td background=\""+theme_img_loc+"tabline.gif\" valign=\"bottom\">");
           bar.append("<TABLE BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"0\"><tr>");

           String current_view_name    = getQueryStringValue("__VIEW_NAME");
           String current_view_desc    = getQueryStringValue("__IMPORT");

           if( Str.isEmpty(current_view_name)          ||
               "_NEW".startsWith(current_view_name)    ||
               "_COPY".startsWith(current_view_name)   ||
               "_REMOVE".startsWith(current_view_name) ||
               "_IMPORT".startsWith(current_view_name)  )
           {
              current_view_name = getPortalPage().readGlobalProfileValue(ASPPortal.CURRENT_VIEW,false);
              current_view_desc = getPortalPage().readGlobalProfileValue(ASPPortal.AVAILABLE_VIEWS+"/"+current_view_name+ASPPortal.PAGE_NODE+"/"+ASPPortal.PORTAL_DESC,false);
           }

           final int  MAX_TAB_LENGTH = Integer.parseInt(getConfigParameter("admin/views_tab_width","700"));
           BufferIterator it = views.iterator();
           String active_name = "";
           String active_desc = "";
           int active_tab_pos = 0;
           int count = 1;

           while( it.hasNext() )
           {
               Item item = it.next();
               if(current_view_name.equals(item.getName()))
               {
                   active_name = URLEncode(item.getName());
                   active_desc = item.getString();
                   active_tab_pos = count;
               }
               else
               {
                  names.addElement(URLEncode(item.getName()));
                  descs.addElement(item.getString());
               }
               count++;
           }
           names.addElement(active_name);
           descs.addElement(active_desc);

           int tab_length = (((String)descs.lastElement()).length()*6)+30;
           tab_count = 1;
           for(int i=0; i < names.size()-1; i++ )
           {
               tab_length += (((String)descs.elementAt(i)).length()*6)+30;
               if(tab_length < MAX_TAB_LENGTH){
                  tab_count++;
               }
           }

           if (tab_count >= active_tab_pos)
           {
               names.insertElementAt(active_name,active_tab_pos-1);
               descs.insertElementAt(active_desc,active_tab_pos-1);
               names.removeElementAt(names.size()-1);
               descs.removeElementAt(descs.size()-1);
           }
           else
           {
               names.insertElementAt(active_name,tab_count-1);
               descs.insertElementAt(active_desc,tab_count-1);
               names.removeElementAt(names.size()-1);
               descs.removeElementAt(descs.size()-1);
           }
           if(views!=null && views.countItems()>1 && !isInactivePortalViewsDisabled())
           {
              boolean active_tab_shown = false;

              String name = "";
              String desc = "";
              String img = "";
              boolean is_rtl = isRTL();
              int prev = 0;
              boolean is_current_view = false;

              //Showing the views tabs
              for(int i=0 ; i < tab_count; i++)
              {
                  name = URLDecode((String)names.elementAt(i));
                  desc = (String)descs.elementAt(i);
                  is_current_view = current_view_name.equals(name);
                  if(is_current_view)
                  {
                     bar.append("<td  align=\"middle\" height=\"20\" class=\"currentPortalTab"+((is_rtl)?"Right":"Left")+"\" vAlign=\"bottom\"><img SRC=\""+img_loc+"15px_spacer.gif"+"\"></td>");
                     bar.append("<td align=\"bottom\" height=\"20\" noWrap vAlign=\"center\" background=\""+theme_img_loc+"TabMidW.gif"+"\">");
                     bar.append("<font class=\"activeTabText\">");
                     bar.append(desc);
                     bar.append("</font>");
                     bar.append("</td>");
                     bar.append("<td align=\"middle\" height=\"20\" class=\"currentPortalTab"+((is_rtl)?"Left":"Right")+"\" vAlign=\"bottom\"><img SRC=\""+img_loc+"15px_spacer.gif"+"\"></td>");
                  }
                  else
                  {
                     String tab_id = "portal_tab_"+i+"_";
                     String mouse_over = "_hoverTabOnMouseOver('"+tab_id+"');this.style.cursor='pointer';";
                     String mouse_out = "_hoverTabOnMouseOut('"+tab_id+"');this.style.cursor='default';";
                    
                     String onclick = "javascript:freezePortalPage();document.location='"+home+"?__VIEW_NAME="+URLEncode(name);
                     
                     if ("Y".equals(readValue("__NEWWIN")))
                     {
                        onclick += "&__NEWWIN="+"Y"+"';"; 
                     }
                     else
                     {
                        onclick += "';"; 
                     }
                     
                     bar.append("<td id=\""+tab_id+"L\" onclick=\""+onclick+"\" onmouseover=\""+mouse_over+"\" onmouseout=\""+mouse_out+"\" align=\"middle\" height=\"20\" class=\"portalTab"+((is_rtl)?"Right":"Left")+"\" vAlign=\"bottom\"><img SRC=\""+img_loc+"15px_spacer.gif"+"\"></td>");
                     bar.append("<td id=\""+tab_id+"M\" onclick=\""+onclick+"\" onmouseover=\""+mouse_over+"\" onmouseout=\""+mouse_out+"\" align=\"bottom\" height=\"20\" noWrap vAlign=\"center\" class=\"portalTabMiddle\">");
                     bar.append("<font class=\"activeTabText\">",desc,"</font>");
                     bar.append("</td>");
                     bar.append("<td id=\""+tab_id+"R\" onclick=\""+onclick+"\" onmouseover=\""+mouse_over+"\" onmouseout=\""+mouse_out+"\" align=\"middle\" height=\"20\" class=\"portalTab"+((is_rtl)?"Left":"Right")+"\" vAlign=\"bottom\"><img SRC=\""+img_loc+"15px_spacer.gif"+"\"></td>");
                  }
              }
           }
           bar.append("<tr></table>");
           bar.append("</td>");
           bar.append("<td background=\""+theme_img_loc+"tabline.gif\"  ALIGN=\"left\" WIDTH=100%>");

           if(names.size() > tab_count)
           {
             ASPPopup more = asppage.getASPPopup("more_views");
             more.removeAllItems();
             for(int i=tab_count;i<names.size();i++)
             {
                more.addItem((String)descs.elementAt(i),correctURL("document.location='"+home+"?__VIEW_NAME="+names.elementAt(i)+"'"));
             }
             bar.append("<a class=inactiveTabText href=\""+more.generateCall()+"\">&nbsp;&nbsp;"+translate("MOREVIEWSTEXT: More Views")+" </a>");
           }
           else
               bar.append("&nbsp;");
            bar.append("</td>");

            bar.append("</tr>\n");
            bar.append("</table>\n");
     }
     else{
         //bar.append("<br>\n");
         //bar.append("<tr width=\"100%\"><td height=\"22\" width=\"100%\">&nbsp;</td></tr>");
         //bar.append("</table>\n");
     }
   }

   void appendGlobalVariables(AutoString bar)
   {
      ASPPortal portal = asppage.getASPPortal();
      Hashtable global_variables = null;
      if ( portal!=null )
      {
         global_variables = portal.getGlobalVariables();
         if ( !global_variables.isEmpty() )
         {
            bar.append("<table ");//class=borders ");
            bar.append(" width=");
            bar.append("100%");
            bar.append(" cellspacing=0 cellpadding=0>\n");
            bar.append("<tr><td>&nbsp;&nbsp;</td><td NOWRAP width=\"100%\">\n");
            bar.append("<font  class=\"globalValriablesText\">");
            Enumeration variables = global_variables.keys();
            while ( variables.hasMoreElements() )
            {
               String variable = (String)variables.nextElement();
               String value    = (String)global_variables.get(variable);
               int i = value.indexOf(",");
               int p = value.lastIndexOf(",");

               if ( i>0 )
               {
                  if ( p>i )
                  {
                     bar.append(value.substring(i+1,p),": ",value.substring(0,i),", ",value.substring(p+1));
                     bar.append("&nbsp;&nbsp;");
                  }
                  else
                     bar.append(value.substring(i+1),": ",value.substring(0,i),"&nbsp;&nbsp;");
               }
               else if ( i<0 )
                  bar.append(variable,": ",value,"&nbsp;&nbsp;");

            }
            bar.append("</font>");
            bar.append("</td>\n");
            bar.append("</tr>\n");
            bar.append("</table>\n");
         }
      }
   }

   private void appendApplicationSearch(AutoString bar)
   {
      ASPConfig cfg = asppage.getASPConfig();
      String img_loc = cfg.getImagesLocation();
      String[][] domains = null;
      //boolean disabled = false;

      try
      {
         UserDataCache user_cache = UserDataCache.getInstance();
         domains = user_cache.getSearchDomainList(getSessionId());

         if (domains.length == 0) return;
      }
      catch(Exception any)
      {
         return;
      }

      String selectedDomains = "";
      Vector domainVector = new Vector();
      String maxRows = "100";
      boolean show_snippet = true;

      selectedDomains = asppage.readGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"SelectedDomains",false);
      if (!isEmpty(selectedDomains))
      {
         StringTokenizer st = new StringTokenizer(selectedDomains,",");
         while(st.hasMoreElements())
         {
            domainVector.addElement(st.nextToken());
         }
      }

      maxRows = asppage.readGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"MaxRows","100",false);
      show_snippet = asppage.readGlobalProfileFlag("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"ShowSnippet",true);

      bar.append("</td><td>");
      bar.append("<input type=text align=\"top\" class='appSearchTextField' size=25 name=FND_APPLICATION_SEARCH id=FND_APPLICATION_SEARCH\n");
      bar.append("onBlur=\"javascript:focusInSearchBox=false;checkDoSearch(this);\" onFocus=\"javascript:{this.className='appSearchTextField appSearchText'; focusInSearchBox=true; if (this.value == FND_APPLICATION_SEARCH_TEXT || this.value == FND_SELECT_SEARCH_DOMAIN) this.value = '';}\" > ");
      bar.append("</td><td nowrap>");
      bar.append("<a href=\"javascript:search()\"><img id=appsearchbutton border=0 align=\"top\" src=\""+getDummyImage()+"\" title=\""+translateJavaScript("FNDMGRAPPSEARCHBUTTON: Application Search") +"\"></a>");
      bar.append("<a href=\"javascript:showHideSearchDomains()\"><img id=appsearcdomainhbutton onmouseover=\"mouseOverSearchSpan=true;\" onmouseout=\"mouseOverSearchSpan=false;\"  onclick=\"SearchCloseByButton = true;\" border=0 align=\"top\" src=\""+img_loc+"search2_24x24.gif\" title=\""+translateJavaScript("FNDMGRAPPDOMAINSEARCHBUTTON: Search Domains") +"\"></a>");
      bar.append("<input type=hidden name=FND_SEARCH_PROFILE id=FND_SEARCH_PROFILE>\n");
      bar.append("<img  id=\"separator4\" ");
      bar.append("src=\"",img_loc,"/",getUserTheme(),"/separator.png\"");
      bar.append(" border=\"0\">\n");

      bar.append("\n<span onmouseover=\"mouseOverSearchSpan=true;\" onmouseout=\"mouseOverSearchSpan=false;\" id='searchDomains' class=\"appSearchSpan\" style=\"position:absolute;display:none;\">\n");
      bar.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" class=\"selectbox\">\n");
      bar.append("<tr><td align=right><a href=\"javascript:showSearchDomainHelp('');\"><img id=appsearchwhatsthis src=\"",img_loc,"/app_search_whatsthis.gif\" alt=\"What is This?\" title=\"What is This?\" border=0></a></td></tr>\n");
      bar.append("<tr><td background=\""+img_loc+"navigator_line.gif\"></td></tr>\n");

      boolean hasSelectedDomains = false;
      String selectedDomainName ="";
      int noOfSelectedDomains = 0;

      if(!asppage.getASPProfile().isUserProfileDisabled())
      {

         for (int i=0; i<domains.length; i++)
         {
            bar.append("<tr><td nowrap><input class='checkbox' type=checkbox name=domains value=\""+domains[i][0]+"\"");
            if (!isEmpty(selectedDomains))
            {
               boolean containsDomain = domainVector.contains(domains[i][0]);

               if (containsDomain)
               {
                  noOfSelectedDomains++;
                  selectedDomainName = domains[i][1]; //to display in search box instead of "Search"

                  if (!hasSelectedDomains) hasSelectedDomains = true;
               }

                bar.append(containsDomain?"checked":"");
            }
            bar.append(" text=\""+domains[i][1]+"\">");

            bar.append("&nbsp;&nbsp;<a class=normalTextLabel href=\"javascript:_selectItem(f.domains"+(domains.length>1?"["+i+"]":"")+");");
            bar.append("function _selectItem(elem){elem.checked=(!elem.checked==true);}\">");
            bar.append(domains[i][1]+"</a>\n&nbsp;&nbsp;</td></tr>\n");
         }

         bar.append("<tr><td align='center'>\n");
         bar.append("<a href=\"javascript:selectAllRows('domains',true)\">select all</a>&nbsp;&nbsp;<a href=\"javascript:deselectAllRows('domains',true)\">deselect all</a>\n");
         bar.append("</td></tr>\n");


         bar.append("<tr><td background=\""+img_loc+"navigator_line.gif\">\n");
         bar.append("</td></tr>\n");

         //Max rows
         bar.append("<tr><td>&nbsp;"+translateJavaScript("FNDMGRAPPSEARCHMAXROW: Max number of hits"));
         bar.append("&nbsp;<input type=text class='editableTextField' size=4 name=FND_SEARCH_MAXROWS value="+maxRows+"></td></tr>\n");
         //show snippet
         bar.append("<tr><td nowrap><input class='checkbox' type=checkbox name=FND_SNIPPET value=TRUE "+(show_snippet?"checked":"")+">&nbsp;");
         bar.append("&nbsp;<a class=normalTextLabel href=\"javascript:_selectItem(f.FND_SNIPPET);");
         bar.append("function _selectItem(elem){elem.checked=(!elem.checked==true);}\">");
         bar.append(translateJavaScript("FNDMGRAPPSEARCHSNIPPET: Include summary by default")+"</a>\n&nbsp;&nbsp;</td></tr>\n");

         bar.append("<tr><td background=\""+img_loc+"navigator_line.gif\">\n");
         bar.append("</td></tr>\n");
      }
      else
      {
         for (int i=0; i<domains.length; i++)
         {
            bar.append("<tr><td nowrap><input disabled class='checkbox' type=checkbox name=domains value=\""+domains[i][0]+"\"");
            if (!isEmpty(selectedDomains))
            {
               boolean containsDomain = domainVector.contains(domains[i][0]);

               if (containsDomain)
               {
                  noOfSelectedDomains++;
                  selectedDomainName = domains[i][1]; //to display in search box instead of "Search"

                  if (!hasSelectedDomains) hasSelectedDomains = true;
               }

                bar.append(containsDomain?"checked":"");
            }
            bar.append(" text=\""+domains[i][1]+"\">");

            bar.append("&nbsp;&nbsp;<font class=normalTextLabel>");
            //bar.append("function _selectItem(elem){elem.checked=(!elem.checked==true);}\">");
            bar.append(domains[i][1]+"</font>\n&nbsp;&nbsp;</td></tr>\n");
         }

         bar.append("<tr><td background=\""+img_loc+"navigator_line.gif\">\n");
         bar.append("</td></tr>\n");

         //Max rows
         bar.append("<tr><td>&nbsp;"+translateJavaScript("FNDMGRAPPSEARCHMAXROW: Max number of hits"));
         bar.append("&nbsp;<input disabled type=text class='editableTextField' size=4 name=FND_SEARCH_MAXROWS value="+maxRows+"></td></tr>\n");
         //show snippet
         bar.append("<tr><td nowrap><input disabled class='checkbox' type=checkbox name=FND_SNIPPET value=TRUE "+(show_snippet?"checked":"")+">&nbsp;");
         bar.append("&nbsp;<font class=normalTextLabel>");
         //bar.append("function _selectItem(elem){elem.checked=(!elem.checked==true);}\">");
         bar.append(translateJavaScript("FNDMGRAPPSEARCHSNIPPET: Include summary by default")+"</font>\n&nbsp;&nbsp;</td></tr>\n");

         bar.append("<tr><td background=\""+img_loc+"navigator_line.gif\">\n");
         bar.append("</td></tr>\n");
      }

      bar.append("<tr height=\"20\"><td>&nbsp;<a class=normalTextLabel href=\"javascript:installSearchEngine()\">"+translateJavaScript("FNDMGRADDSEARCHPROVIDER: Add browser search provider")+"</a> </td></tr>\n");
      // Check for windows 7 to allow federated search
      if ( getUserAgent().contains("Windows NT 6.1") && "Y".equals(getConfigParameter("ADMIN/FEDERATED_SEARCH/ENABLE", "Y")))
         bar.append("<tr height=\"20\"><td>&nbsp;<a class=normalTextLabel href=\"javascript:installFedSearch()\">"+translateJavaScript("FNDMGRADDFEDSEARCH: Add to Windows7 Search")+"</a> </td></tr>\n");

      bar.append("</table>\n");

      bar.append("<input type=hidden name=FND_SEARCH_DOMAINS id=FND_SEARCH_DOMAINS value=\""+selectedDomains+"\">\n");

      String allDomainsText = translateJavaScript("FNDMGRALLDOMAINSMSG: All Domains");
      String multipleDomainsText = translateJavaScript("FNDMGRMULTIDOMAINMSG: Multiple Domains");
      String searchText = translateJavaScript("FNDMGRAPPSEARCHTEXTMSG: Search");

      if (noOfSelectedDomains == 1)
         searchText = selectedDomainName;
      else if (noOfSelectedDomains == domains.length)
         searchText = allDomainsText;
      else if (noOfSelectedDomains > 1)
         searchText = multipleDomainsText;

      bar.append("<script>\nFND_SEARCH_ACTION='"+cfg.getScriptsLocation()+"SearchResult.page';\n");
      bar.append("FND_APPLICATION_SEARCH_TEXT =\""+ searchText +"\";\n");
      bar.append("FND_SELECT_SEARCH_DOMAIN =\""+translateJavaScript("FNDMGRSELECTSEARCHDOMAIN: Select Search Domain") +"\";\n");
      bar.append("FND_MULTIPLE_DOMAINS_TEXT =\""+ multipleDomainsText +"\";\n");
      bar.append("FND_ALL_DOMAINS_TEXT =\""+ allDomainsText +"\";\n");
      bar.append("FND_DO_SEARCH =false;\n");
      bar.append("if (document.form) \n\t document.form.FND_APPLICATION_SEARCH.value=FND_APPLICATION_SEARCH_TEXT;\n");

      //============= Method to add search plugin for IE7 and FF2 =====================
      bar.append("\n");
      bar.append("function installSearchEngine() {\n");
      bar.append("   hideSearchDomains();");
      bar.append("   if (window.external && (\"AddSearchProvider\" in window.external)) {\n");
      bar.append("      // Firefox 2 and IE 7, OpenSearch\n");
      bar.append("      var status = ''+__connect('"+getASPConfig().getApplicationPath()+"/common/scripts/SearchResult.page?ADDPROVIDER=Y');\n");
      bar.append("      if(status.indexOf('Done')!=-1)\n");
      bar.append("         window.external.AddSearchProvider('"+getASPConfig().getProtocol()+"://"+getASPConfig().getApplicationDomain()+getASPConfig().getApplicationPath().trim()+"/dynacache/IFSSearchProvider.xml');\n");      
      bar.append("      else\n");
      bar.append("         alert(\""+translateJavaScript("FNDMGRNOSEARCHENGINEXML: Could not generate opensearch plugin")+"\");\n");
      bar.append("   } else {\n");
      bar.append("      // No search engine support (IE 6, Opera, etc).\n");
      bar.append("      alert(\""+translateJavaScript("FNDMGRNOSEARCHENGINESUPPORT: No search engine support")+"\");\n");
      bar.append("   }\n");
      bar.append("}\n");

      //============= Method to add to Windows 7 federated search +=====================
      bar.append("\n");
      bar.append("function installFedSearch() {\n");
      bar.append("      hideSearchDomains();\n");
      bar.append("      location.href='"+getASPConfig().getApplicationPath()+"/common/scripts/FederatedSearch.page?ADDFEDSEARCH=Y';\n");
      bar.append("}\n");

      //================================================================================

      bar.append("</script>\n");

      if (!hasSelectedDomains)
      {
         bar.append("\n<script>\n");
         bar.append("document.form.FND_APPLICATION_SEARCH.value=FND_SELECT_SEARCH_DOMAIN;\n");
         bar.append("</script>\n");
      }

      bar.append("</span>\n");

   }

   /**
    * Generate HTML start, including toolbar.
    */

   public String startPresentation(String title)
   {
      if(isMobileVersion())
         return startMobilePresentation(title);

      if ( DEBUG ) debug(this+": ASPManager.startPresentation("+title+")");

      AutoString bar = new AutoString();
      ASPConfig cfg = asppage.getASPConfig();
      toolbar_title = translate(title);

      asppage.resetBlockCount();

      toolbar_title = (toolbar_title==null)?"":toolbar_title;

      if ( asppage.getASPLov() == null && !("DynamicIIDLov".equals(asppage.getPageName())))
      // Make sure this is not a Dynamic LOV
      {
         if(!isRWCHost())
         {
            String img_loc = cfg.getImagesLocation();
            bar.append("\n");

            if ( !asppage.isHeaderDisabled() )
            {
               ASPPortal portal = asppage.getASPPortal();
               String rname = getRealUserName();
               if ( Str.isEmpty(rname) ) rname = translateJavaText("FNDMGRNOUSER: No user logged on");
               bar.append("<table class=\"pageHeaderTable\" cellspacing=\"0\" cellpadding=\"2\">",
                              "<tr><td  width=5% class=\"pageHeaderLeft\" nowrap>",
                                     "<font class=\"pageHeaderLeftText\" >");
               appendLeftHeader(bar, asppage);
               bar.append("           </font></td>");
               bar.append("<td width=90% class=\"pageHeaderCentral\">",
                          "<font class=\"pageHeaderCentralText"+((toolbar_title.length()>50)?" pageHeaderCentralLongText":"")+"\">",
                           toolbar_title+(isDefaultLayout()?"&nbsp;["+translate("FNDMANAGERDEFAULTMODEMSG: Default Layout mode")+"]":"")
                          ,"</font></td>");
               bar.append(        "<td width=5% class=\"pageHeaderRight\" align=\"right\" nowrap >");
               if ( !asppage.isBarDisabled() && !isDefaultLayout())
                  appendRightHeader(bar, false);
               bar.append(        "</td>");

               bar.append("</tr></table>");
            }
            else if (isDefaultLayout())
            {
               bar.append("<table  border=0 width=100% cellpadding=\"0\" cellspacing=\"0\">");
               bar.append("<tr><td width=20 height=10>&nbsp;&nbsp;</td><td nowrap valign=\"bottom\">");
               bar.append("<font class=\"pageTitle\">","&nbsp;["+translate("FNDMANAGERDEFAULTMODEMSG: Default Layout mode")+"]","</font>");
               bar.append("</td></tr>");
               bar.append("</table>");
            }
            else //header is disabled
            {
               bar.append("<table border=0 width=100% cellpadding=\"0\" cellspacing=\"0\">");
               bar.append("<tr><td align=\"right\">");
               if (!asppage.isBarDisabled())
                   appendRightHeader(bar, false);
               bar.append("</td></tr>");
               bar.append("</table>");
            }
         }
         else
         {
            /* temp variable is to generate the rightheader. It will nor be displayed. The genaration
             * is done because its useed to generate popup definitions used by portlets.
             */
            AutoString temp = new AutoString();
            temp.clear();
            appendRightHeader(temp, false);

            if(!isPortalPage())
               bar.append(getEmptyImage(8,8));
            else
            {
               bar.append("<table border=\"0\" width=100% cellpadding=\"0\" cellspacing=\"0\">");
               bar.append("<tr><td>",getEmptyImage(8,8),"</tr></td>");
               bar.append("</table>");
            }
         }
      }
      else
      {
         bar.append("\n");
         bar.append("<table ");
         if ( cfg.getParameter("AUDIT/SHOW_BORDER","N").equals("Y") )
            bar.append("border=\"1\"");
         else
            bar.append("border=\"0\"");
         bar.append(" cellpadding=\"0\" cellspacing=\"0\"");
         bar.append(" width=\"100%\">\n");
         bar.append("<tr> <td "+(!isRTL()?"align=\"left\"":"")+" valign=\"middle\" height=\"20\"><font class=lovTitle>&nbsp;&nbsp;",translate(title),"</font></td></tr>\n");
         bar.append("</table>\n");
         return bar.toString();
      }

      if(!isRWCHost())
      {
         boolean show_break = true;
         if (!isDefaultLayout() && !isLogonPage() && !asppage.isSubHeaderDisabled())
         {
            bar.append("<table class='searchBar' cellspacing='0' cellpadding='1' width='100%'><tr ><td nowrap>");
            appendBroadcastMessages(bar);
            bar.append("</td></tr></table>");
            show_break = false;
         }
         
         // Added by Terry 20131027
         // Disable head break when page header is disabled
         if (asppage.isHeaderDisabled())
            show_break = false;
         // Added end
         
//         show_break = !drawContextSensitiveTaskPane(bar) && show_break;
         
         if(show_break)
         {
            bar.append("<br>");
         }
      }

      appendPortalTabs(bar);
      appendGlobalVariables(bar);

      return bar.toString();
   }

   private boolean drawContextSensitiveTaskPane(AutoString output)
   {
      if(!asppage.isContextSensitiveTaskPaneDisabled() && !isPortalPage() && !(asppage instanceof ifs.fnd.pages.Navigator))
      {
         ASPBlock master_blk = getMasterBlock();
         if(master_blk==null) return false;
         String busness_obj = master_blk.getLUName();
         int count = 0;
         if(busness_obj!=null)
         {
            count = (new ifs.fnd.web.features.managemytodo.MyToDoTaskAdapter()).countRelatedMyTodo(busness_obj,asppage);
            if(count>0)
            {
               output.append("<table border='0' cellspacing='0' cellpadding='0' width='100%'><tr style=\"padding-bottom: 2px\"><td nowrap>");            
               output.append("&nbsp;&nbsp;<a class=boldTextValue href=\"javascript:showContextTaskPane();\">"+translate("FNDMGRREMYTODOTASKLINK: Tasks")+" ("+count+")"+"</a>");
               output.append("</td></tr></table>");
               return true;
            }
         }
      }
      return false;
   }
   
   private void appendLeftHeader(AutoString str, ASPPage page)
   {
      if (isLogonPage())
         return;

      String name = getRealUserName();
      String signout = "";
      String left="";
      String right="";

      if(getASPConfig().isFormBasedAuth() && !asppage.isSignoutLinkDisabled())
         signout = "<a href=\"javascript:removeClusterCookie();document.location='"+(correctURL(getASPConfig().getLogonURL()+"?LOGOUT=YES"))+"'\">"+translate("FNDASPMANAGERLOGOFF: Sign out")+"</a>";
      else if(!asppage.isSignoutLinkDisabled() && getASPConfig().isUserExternallyIdentified() && getASPConfig().isLogoffEnabled())               
         signout = "<a href=\"javascript:removeClusterCookie();document.location='"+(correctURL(getASPConfig().getLogonURL()+"?LOGOUT=YES&EXTERNAL=YES"))+"'\">"+translate("FNDASPMANAGERLOGOFF: Sign out")+"</a>";

      if(isRTL())
      {
         if(!Str.isEmpty(signout))
            left = signout+" | ";
         right = name;
      }
      else
      {
         left = name;
         if(!Str.isEmpty(signout))
            right = " | "+signout;
      }

      str.append(left);
      if (!asppage.isSettingLinkDisabled() && !asppage.getASPProfile().isUserProfileDisabled())
      {
         if(isRTL())
         {
            str.append("<a href=\"javascript:document.location='", getApplicationPath(),"/common/scripts/GeneralConfigurations.page'\">");
            str.append(translate("FNDASPMANAGER: Settings"),"</a>");
            str.append(" | ");
         }
         else
         {
            str.append(" | ");
            str.append("<a href=\"javascript:document.location='", getApplicationPath(),"/common/scripts/GeneralConfigurations.page'\">");
            str.append(translate("FNDASPMANAGER: Settings"),"</a>");
         }
      }
      str.append(right);

   }


   /**
    * Generate HTML end, including footer.
    */
   public String endPresentation()
   {
      if(isMobileVersion())
         return endMobilePresentation();

      AutoString bar = new AutoString();
      ASPConfig cfg = asppage.getASPConfig();

      /* No footer in App7 SP3 - new themes
      if ( asppage.getASPLov() == null && !asppage.isFooterDisabled() )
      {
         bar.append("<!-- footer start -->");
         bar.append("<br><table class=\"pageFooterTable\" cellpadding=\"0\" cellspacing=\"0\" ><tr><td  class=\"pageFooterLeft\" nowrap><font class=\"pageFooterLeftText\" >" +
                    "&nbsp;&nbsp;&nbsp;IFS Applications</font></td><td class=\"pageFooterRight\" nowrap align=\"right\"><img  src=\"",cfg.getImagesLocation(),
                    "ifsLogo.gif\" border=\"0\">&nbsp;&nbsp;&nbsp;</td></tr></table>");
      }*/

      bar.append(getRWCInterface());

      bar.append(generateClientScript());

      bar.append(asppage.getActiveCommand());
      asppage.setDefaultCommandDisabled(false);

      if(isNetscape4x())
         bar.append("\n"+generatePageMaskTag());

      return bar.toString();
   }


   /**
    * Generate HTML end, without a footer.
    */

   String endPresentationSimple()
   {
      if(isMobileVersion())
         return endMobilePresentationSimple();

      AutoString bar = new AutoString();

      bar.append(generateClientScript());

      return bar.toString();
   }


   /**
    * Generate HTML <head> tag.
    */
   public String generateHeadTag(String title)// throws FndException
   {
      return generateHeadTag(title, null);
   }

   public String generateHeadTag(String title, String charset)// throws FndException
   {
      AutoString temp = new AutoString();

      if(isMobileVersion())
         return generateMobileHeadTag(title);

      if (isEmpty(charset))
         if ("TRUE".equals(readValue("EXCEL_REPORT")))
            temp.append("<meta name=\"DownloadOptions\" content=\"noopen\" http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
         else
            temp.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
      else
         temp.append("<META http-equiv=Content-Type content=\"text/html; charset="+charset+"\">\n");

      if ( isExplorer() || isMozilla() )
         temp.append(getStyleSheetTag());
      else
      {
         Calendar cal = Calendar.getInstance();
         cal.add(cal.YEAR,1); //add a year to current time;

         SimpleDateFormat sd = new SimpleDateFormat ("EEE, dd MMM yyyy hh:mm:ss 'GMT'", Locale.US);
         String exp_date = sd.format(cal.getTime());

         temp.append("<META HTTP-EQUIV=\"expires\" CONTENT=\"",exp_date,"\">");
         temp.append(getStyleSheetTag());
      }

/*
      temp.append("<link rel=\"STYLESHEET\"");
      temp.append("href=\"");
      temp.append(getConfigParameter("APPLICATION/LOCATION/STYLESHEETS","../common/stylesheets/"),"mystyle.css\"");

      temp.append("type=\"text/css\">");
*/
      temp.append("<title>",translate(title),"</title>\n",genHTag(true));

      return temp.toString();
   }

   //==========================================================================
   // HTML Fields Generation
   //==========================================================================

   private Hashtable fields_to_generate = new Hashtable(); // of (String,Buffer)

   /**
    * This method removes duplicate definitions for one HTML field
    */
   void addFieldToGenerate( String name, String value )
   {
      Buffer input = getFactory().getBuffer();

      if ( ("__PAGE_ID".equals(name) && getASPConfig().getDebugPageId()) ||
           "__MGR_REFERENCE".equals(name) )
         input.addItem("type","text");
      else
         input.addItem("type","hidden");

      input.addItem("name",name);
      input.addItem("value",HTMLEncode(value));

      Buffer tags = input.newInstance();
      tags.addItem("input",input);
      fields_to_generate.put(name,tags);
   }

   ASPBlock getMasterBlock()
   {
      Iterator tblItr = asppage.getASPTables().iterator();
      while(tblItr.hasNext())
      {
         ASPBlock blk = ((ASPTable)tblItr.next()).getBlock();
         if(blk.getMasterBlock()!=null) continue;
         return blk;
      }
      return null;
   }
   
   private void generateHTMLFields( AutoString html )
   {
      try
      {
         addFieldToGenerate("__LAST_POSITION",readValue("__LAST_POSITION"));

         addFieldToGenerate("__SAVED_POSITION",Integer.toString(saved_position));

         addFieldToGenerate("__RESTORE_POSITION",Integer.toString(restore_position) ) ;
      }
      catch ( Exception e ) {
         ;
      }

      ASPBlock mb = getMasterBlock();
      addFieldToGenerate("__TEMPLATE_ID",null);
      addFieldToGenerate("__BUSSINESS_OBJECT",mb==null?"":mb.getLUName());
      addFieldToGenerate("__ALERT",translate(alert_text));
      addFieldToGenerate("__SYSTEM_ALERT",translate(sys_alert_text));
      addFieldToGenerate("__COMMAND",null);
      addFieldToGenerate("__REAUTHENTICATION",null);
      addFieldToGenerate("__LINK",null);
      addFieldToGenerate("__SORT",null);
      addFieldToGenerate("__FOCUS",initial_focus);
      addFieldToGenerate("__POSTED",null);
      addFieldToGenerate("__PAGE_ID",page_id);
      addFieldToGenerate("__DYNAMIC_DEF_KEY",dynamic_def_key);
      addFieldToGenerate("__PORTAL_NAME",null);
      if(! isMobileVersion() )
         addFieldToGenerate("__ISNOTEBOOKMINIMIZED",isEmpty(readValue("__ISNOTEBOOKMINIMIZED"))? "false":readValue("__ISNOTEBOOKMINIMIZED"));
      addFieldToGenerate("__CASESS_VALUE",isEmpty(readValue("__CASESS_VALUE")) ? getConfigParameter("ADMIN/CASE_SENSITIVE_SEARCH","Y") : readValue("__CASESS_VALUE"));
      addFieldToGenerate(DEFAULT_LAYOUT,readValue(DEFAULT_LAYOUT));
      addFieldToGenerate(PAGE_COPY_KEY, page_copy_key);
      addFieldToGenerate("__REQUEST_METHOD_TYPE", getAspRequest().getMethod());
      addFieldToGenerate("__CURRENT_PAGE_URL", getURL()+ (isEmpty(getQueryString()) ? "":"?"+getQueryString()));
      Object is_expired = getAspSession().getAttribute(IS_PAGE_EXPIRED);
      if(is_expired !=null){
         getAspSession().removeAttribute(IS_PAGE_EXPIRED);
         addFieldToGenerate(IS_PAGE_EXPIRED, is_expired.toString());
      }
      else
        addFieldToGenerate(IS_PAGE_EXPIRED, null);
      ASPPortal portal = asppage.getASPPortal();
      if ( portal != null )
      {
         addFieldToGenerate("__PORTLET_ID",  portal.getPortletId() );
         addFieldToGenerate("__PORTAL_MODE", null );
         addFieldToGenerate("__PORTAL_COLUMNS",null);
      }

      if ( getASPConfig().getDebugMgrReference() )
      {
         String mgrref = Integer.toHexString(this.hashCode());
         String pageref = Integer.toHexString(asppage.hashCode());
         addFieldToGenerate("__MGR_REFERENCE","page[mgr]="+pageref+"["+mgrref+"]");
      }

      addFieldToGenerate("__AUTH_KEY",(getASPConfig().isFormBasedAuth())?context.getCookie(getASPConfig().getAuthUserCookieName()):"*");
      addFieldToGenerate("__HEAD_TAG_GENERATED", head_tag_generated ? "Y" : "N" );
      html.append('\n');
      Enumeration fields_list = fields_to_generate.elements();
      while ( fields_list.hasMoreElements() )
         generateHTML(html,(Buffer)fields_list.nextElement()," ");
      html.append('\n');
   }

   /**
    * Compound Item = TAG
    * Simple Item   = ATTRIBUTE
    *
    * Note! The simple items on the first level in the specified Buffer are ignored.
    */
   void generateHTML( AutoString html, Buffer tags, String indent )
   {
      int count = tags.countItems();
      for ( int i=0; i<count; i++ )
      {
         Item item = tags.getItem(i);
         String name = item.getName();
         if ( item.isCompound() )
         {
            html.append(indent);
            html.append('<');
            html.append(name);

            Buffer subbuf = item.getBuffer();
            BufferIterator iter = subbuf.iterator();
            boolean attr_exist = false;
            while ( iter.hasNext() )
            {
               Item attr = iter.next();
               if ( !attr.isCompound() )
               {
                  attr_exist = true;
                  String val = attr.getString();
                  html.append(" "+attr.getName()+"=\""+Str.nvl(val,"")+'"');
               }
            }
            html.append(">");

            if ( !attr_exist ) html.append('\n');

            generateHTML(html,subbuf,indent+" ");

            if ( !attr_exist ) html.append(indent);
            if ( isEndTagRequired(name) )
            {
               html.append("</");
               html.append(name);
               html.append('>');
            }
            if ( attr_exist ) html.append('\n');
         }
      }
   }

   private boolean isEndTagRequired( String tag )
   {
      return !( "INPUT".equalsIgnoreCase(tag) ||
                "P"    .equalsIgnoreCase(tag) );
   }




   /**
    * Replace each occurrence of new-line with two characters:
    * '\' and 'n'.
    */
   public String JScriptEncode( String text )
   {
      try
      {
         if ( text==null ) return null;
         if ( text.indexOf('\n')<0 ) return text;
         int len = text.length();
         AutoString buf = new AutoString(len);
         char ch;

         for ( int i=0; i<len; i++ )
         {
            switch ( ch=text.charAt(i) )
            {
            case '\r':
               break;

            case '\n':
               buf.append('\\');
               buf.append('n');
               break;

            default:
               buf.append(ch);
            }
         }
         return buf.toString();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   private AutoString htmlbuf = new AutoString();

   /** Replace all special characters in the specified string with
    * their HTML equivalents.
    * @see #HTMLEncode(String, boolean)
    */
   public String HTMLEncode( String text )
   {
      return HTMLEncode(text,false);
   }

   /** Replace all special characters in the specified string with their HTML
    * equivalents. Additional spaces can be replaced by &amp;nbsp; sending true to the
    * second parameter.
    * @param text String to be encoded
    * @param encode_additional_spaces Encode addtional spaces with &amp;nbsp;
    * @see HTMLEncode(String)
    */
   public String HTMLEncode( String text, boolean encode_additional_spaces)
   {
      if ( DEBUG ) debug(this+".HTMLEncode("+text+")");
      try
      {
         if ( Str.isEmpty(text) ) return text;

         htmlbuf.clear();

         for ( int i=0; i<text.length(); i++ )
         {
            char ch = text.charAt(i);
            switch ( ch )
            {
               case '<': htmlbuf.append("&lt;");
                  break;
               case '>': htmlbuf.append("&gt;");
                  break;
               case '"': htmlbuf.append("&quot;");
                  break;
               case '&': htmlbuf.append("&amp;");
                  break;
               //Bug 40353, start
               case 32:  //Space
               case 160: //No-Break Space
                  if (encode_additional_spaces)
                  {
                     while(i<text.length()-2 && (text.charAt(i+1) == 32 || text.charAt(i+1) == 160))
                     {
                        htmlbuf.append("&nbsp;");
                        i++;
                     }
                     htmlbuf.append(text.charAt(i));
                  }
                  else
                  {
                     htmlbuf.append(ch);
                  }
                  break;
               default : htmlbuf.append(ch);
            }
         }
         return htmlbuf.toString();
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }

   public String HTMLDecode( String text )
   {
      if ( DEBUG ) debug(this+".HTMLDecode("+text+")");
      try
      {
         //return Str.replace(Str.replace(Str.replace(Str.replace(text,"&quot;","\""),"&lt;","<"),"&gt;",">"),"&amp;","&");

         if ( Str.isEmpty(text) ) return text;

         htmlbuf.clear();
         int len = text.length();

         for ( int i=0; i<len; i++ )
         {
            char ch = text.charAt(i);

            if ( ch=='&' )
            {
               if ( i<len-3 )
               {
                  char ch1 = text.charAt(i+1);
                  char ch2 = text.charAt(i+2);
                  char ch3 = text.charAt(i+3);

                  if ( ch3==';' )
                  {
                     if ( ch1=='l' && ch2=='t' )
                     {
                        htmlbuf.append('<');
                        i = i+3;
                        continue;
                     }
                     else if ( ch1=='g' && ch2=='t' )
                     {
                        htmlbuf.append('>');
                        i = i+3;
                        continue;
                     }
                  }
                  else if ( i<len-4 )
                  {
                     char ch4 = text.charAt(i+4);

                     if ( ch1=='a' && ch2=='m' && ch3=='p' && ch4==';' )
                     {
                        htmlbuf.append('&');
                        i = i+4;
                        continue;
                     }
                     else if ( i<len-5 && ch1=='q' && ch2=='u' && ch3=='o' && ch4=='t' && text.charAt(i+5)==';' )
                     {
                        htmlbuf.append('"');
                        i = i+5;
                        continue;
                     }
                     //Bug 40353, start
                     else if ( i<len-5 && ch1=='n' && ch2=='b' && ch3=='s' && ch4=='p' && text.charAt(i+5)==';' )
                     {
                        htmlbuf.append((char)32); //Space
                        i = i+5;
                        continue;
                     }
                     //Bug 40353, end
                  }
               }
            }
            htmlbuf.append(ch);
         }
         return htmlbuf.toString();
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }

   /**
    * Replace all special characters in the specified string with
    * their URL equivalents (e.g. replace "%" with "%25").
    */
   public String URLEncode( String text )
   {
      try
      {
         //return java.net.URLEncoder.encode(text);//getAspServer().URLEncode(text);
         if (!isEmpty(text))
            text = java.net.URLEncoder.encode(ifsEncode(text),"UTF-8");
         return text;
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }


   public String URLDecode( String text )
   {
      try
      {
         //return java.net.URLDecoder.decode(text);
         if (!isEmpty(text))
            text = ifsDecode(java.net.URLDecoder.decode(text,"UTF-8"));
         return text;
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }

   /**
    * Encodes a string to Hex. Hex encoded string will be marked with char27
    **/
   private String ifsEncode(String text) throws Exception
   {
      int char_count = 0;

      for(int i = 0; i < text.length(); i++)
          if(text.charAt(i) > 127 ||
             text.charAt(i) == C29||
             text.charAt(i) == C30||
             text.charAt(i) == C31||
             text.charAt(i) == 39 || // ' sign
             text.charAt(i) == 43 || // + sign
             text.charAt(i) == 37 || // % sign
             text.charAt(i) == 38)   // & sign
                char_count++;

          //if(char_count*100/text.length() > 60 && text.length() > 5 && b64_enabled)
          //  return b64Encode(text);
          //else
             if(char_count != 0)
                return hexEncode(text,char_count);
             else
                return text;
   }

   private String b64Encode(String text) throws Exception
   {
        return C31+ifs.fnd.service.Util.toBase64Text(text.getBytes("UTF8"));
   }

   private String hexEncode(String text, int char_count)
   {
      char[] work = text.toCharArray();
      String hex = "";
      String num = "";
      String pad = "0000";

      if(char_count == 0)
         return text;
      else
         if(char_count*100/work.length < 60)
         {
            for (int i = 0; i < work.length; i++)
            {
               if(work[i] >  127 ||
                  work[i] == 39  || // ' sign
                  work[i] == 43  || // + sign
                  work[i] == 37  || // % sign
                  work[i] == 38  || // & sign
                  work[i] == C29 ||
                  work[i] == C30 ||
                  work[i] == C31)
               {
                  num = Integer.toHexString((int)work[i]).toUpperCase();
                  num = C29 + pad.substring( 0, 4-num.length() ) + num;
               }
               else
                  num = work[i]+"";
               hex += num;
            }
            return (C29 + hex);
         }
         else
         {
            for (int i = 0; i < work.length; i++)
            {
               num = Integer.toHexString((int)work[i]).toUpperCase();
               hex += pad.substring( 0, 4-num.length() ) + num;
            }
            return (C30 + hex);
         }
   }

   /**
    * Decodes Strings encoded by ifsEncode(s) back to the original String
    **/
   private String ifsDecode(String text) throws Exception
   {
      char[] hexarr = text.toCharArray();
      char temp;
      String work = "";

      if(isEmpty(text))
          return "";
      else
         if(hexarr[0] == C29)
            for(int r=1; r < hexarr.length; r++)
            {
               if(hexarr[r] == C29)
               {
                  r++ ;
                  temp = (char)Integer.parseInt(text.substring(r, r+4),16);
                  r += 3;
               }
               else
                  temp = hexarr[r];
               work += temp;
            }
         else
            if(hexarr[0] == C30)
               for(int r=1; r < hexarr.length; r++)
               {
                  temp = (char)Integer.parseInt(text.substring(r, r+4),16);
                  r += 3;
                  work += temp;
               }
             else
                if(hexarr[0] == C31)
                   work = new String(ifs.fnd.service.Util.fromBase64Text(text.substring(1,text.length())),"UTF8");
                else
                   work = text;
      return work;
   }

   /**
    * This is an obsolete method.
    **/
   public String URLPathEncode( String text )
   {
      try
      {
         return text;//getAspServer().URLPathEncode(text);
      }
      catch ( Throwable any )
      {
         error(new AspContextException(any));
         return null;
      }
   }

   /**
    * Replace all special characters in the specified string with
    * their URL equivalents (e.g. replace "%" with "%25"),
    * but skip the prefix (access method) like "mailto:".
    */
   public String prepareURL( String url )
   {
      try
      {
         if ( url!=null && url.startsWith("mailto:") )
            return "mailto:" + URLEncode(url.substring(7));
         else
            return URLEncode(url);
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return true, if the specified variable is Undefined, Null or equals "",
    * false otherwise.
    */
   public boolean isEmpty( String variable )
   {
      return variable==null || variable.equals("");
   }

   /*
   public boolean isEmptyVariant(Variant variant)
   {
      switch(variant.getvt())
      {
         case Variant.VariantEmpty:    // 0
         case Variant.VariantNull:     // 1
            return true;

         case Variant.VariantString:   // 8
            if (Str.isEmpty( (new VariantConverter(null)).toString(variant) ) )
               return true;
            return false;

         default:
            return false;
      }
   }
   */

   /**
    * Return the specified String 'str' with all occurrences of 'substr'
    * replaced with 'with'.
    */
   public String replace( String str, String substr, String with )
   {
      return Str.replace(str,substr,with);
   }


   /**
    * Return the given String if it is not null, otherwise
    * return the specified default value.
    */
   public String nvl( String str, String default_value )
   {
      return Str.nvl(str,default_value);
   }


   /**
    * Set the state of the thread to sleep.
    */
   public void sleep__( int sec ) throws InterruptedException
   {
      Thread.sleep(1000*sec,0);
   }

   public void showPagePoolContents()
   {
      ASPPagePool.showContentsWithMessage(null);
   }


   public void clearPagePoolContents()
   {
      ASPPagePool.clearContents();
   }


   public void clearPagePool()
   {
      ASPPagePool.clear();
   }


   public boolean isPagePoolAutolockOn()
   {
      return ASPPagePool.AUTOLOCK;
   }


   public void setPagePoolAutolock( boolean lock )
   {
      ASPPagePool.AUTOLOCK = lock;
   }


   public boolean isPagePoolDisabled()
   {
      return ASPPagePool.NO_POOL;
   }


   public void disablePagePool( boolean disable )
   {
      ASPPagePool.NO_POOL = disable;
   }


   /**
    * Spool Trace Event statistics in both ASP and MTS.
    */
   public void spoolTraceStatistics()
   {
      try
      {
         TraceEventType.spoolStatistics("ASPManager.total");
         ASPTransactionBuffer trans = newASPTransactionBuffer();
         trans.addSpoolTraceStatisticsCommand();
         perform(trans);
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }

   /**
    * Clear Trace Event statistics in both ASP and MTS.
    */
   public void clearTraceStatistics()
   {
      TraceEventType.clearStatistics();
      try
      {
         ASPTransactionBuffer trans = newASPTransactionBuffer();
         trans.addClearTraceStatisticsCommand();
         perform(trans);
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }


   void setTraceStatisticsPrecision( int prec )
   {
      TraceEventType.precision = prec;
      ASPTransactionBuffer trans = newASPTransactionBuffer();
      trans.addSetTraceStatisticsPrecision(prec);
      perform(trans);
   }


   /**
    * Begin the pre-defined ASP Trace Event
    */
   public void beginASPEvent()
   {
      asp_event = asp_event_type.begin();
   }

   /**
    * End the pre-defined ASP Trace Event
    */
   public void endASPEvent()
   {
      asp_event.end();
   }

   /**
    * Clears the MTS Security Cache.
    */
   public void clearMTSSecurityCache()
   {
      try
      {
         ASPTransactionBuffer trans = newASPTransactionBuffer();
         trans.addClearMTSSecurityCache();
         perform(trans);
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }

   /**
    * Removes a specific URLs stored pages from the page pool.
    */


   public void removeURLFromPool(String url)
   {
      try
      {
         ASPPagePool.removeURL(url);
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }


   /**
    * Returns the dynamic def key.
    */
   public String getDynamicDefKey()
   {
      return dynamic_def_key;
   }

   String getPageCopyKey()
   {
      return page_copy_key;
   }

   public String runGC()
   {
      Runtime r  = Runtime.getRuntime();
      long free  = r.freeMemory();
      long total = r.totalMemory();
      String out = "Memory before: \ttotal:"+Util.lpad(""+total,12)+ " bytes, \tfree:"+Util.lpad(""+free,12)+" bytes\n";
      r.gc();
      free  = r.freeMemory();
      total = r.totalMemory();
      out += "Memory after: \ttotal:"+Util.lpad(""+total,12)+ " bytes, \tfree:"+Util.lpad(""+free,12)+" bytes";
      debug(out);
      return out;
   }

   public static long getProfileCacheSize()
   {
      return ASPProfileCache.getTotalSize();
   }

   public int[] getLocalizationSizes()
   {
      return config.getLocalizationSizes();
   }

   //==========================================================================
   //  Debugging, error
   //==========================================================================

   private boolean isTraceOn()
   {
      return asplog!=null && asplog.isTraceOn();
   }

   private void debug( String line )
   {
//       if ( TRACEFILE_DEBUG && asplog!=null )
//          asplog.trace(line);
//       else
      Util.debug(line);
   }

   private void trace( String line )
   {
      if ( asplog!=null )
         asplog.trace(line);
   }

   private void log( String line )
   {
      if ( asplog!=null )
         asplog.put(line);
   }

   private void error( Throwable any )
   {
      error(any, true, false);
   }

   private void error( Throwable any, boolean format_error_page )
   {
      error(any, format_error_page, false);
   }

   private void error( Throwable any, boolean format_error_page, boolean throw_exception )
   {
      if ( asplog!=null )
      {
         if (isMultiRowDelete())
            asplog.error(any, format_error_page, throw_exception,true);
         else
                asplog.error(any, format_error_page, throw_exception);
      }
      else
      {
         Throwable e = null;
         if(any instanceof FndException)
            e = ((FndException)any).getCaughtException();

         String msg = "\tThe instance of ASPLog is missing in class ASPManager\n"+
                   "\twhile throwing error:\n\t"+Str.getStackTrace(any)+"\n"+
                   (e==null ? "" : "\tException caught by this FndException:\r\n\t"+Str.getStackTrace(e)+"\n") +
                   "\tASPManager: Throws RuntimeException ...\n";
         //Bug 40929, start
         //Just remove the PANIC word from the error message.
         /*
         Util.debug("\tThe instance of ASPLog is missing in class ASPManager\n"+
                   "\twhile throwing error:\n\t"+Str.getStackTrace(any)+"\n"+
                   (e==null ? "" : "\tException caught by this FndException:\r\n\t"+Str.getStackTrace(e)+"\n") +
                   "\tASPManager: Throws RuntimeException ...\n");
         */
         Util.debug(msg);
         Alert.add(msg);
         //Bug 40929, end

         throw new RuntimeException("Aborting execution...\n");
      }
   }

   private void logError( Throwable any )
   {
      logError(any, true);
   }

   private void logError( Throwable any, boolean translate )
   {
      if ( asplog!=null )
         asplog.logError(any, true, translate);
      else
      {
         String msg = "\tThe instance of ASPLog is missing in class ASPManager\n"+
                    "\twhile logging error:\n\t"+Str.getStackTrace(any)+"\n\n";
         //Bug 40929, start
         //Just remove the PANIC word from the error message.
         /*
         Util.debug("\tThe instance of ASPLog is missing in class ASPManager\n"+
                    "\twhile logging error:\n\t"+Str.getStackTrace(any)+"\n\n");
         */
         Util.debug(msg);
         Alert.add(msg);
         //Bug 40929, end
      }
   }

   //==========================================================================
   //  ASP event handlers
   //==========================================================================

   private boolean in_on_start_page = true;

   boolean inOnStartPage()
   {
      if ( DEBUG ) debug(this+".inOnStartPage(): "+in_on_start_page);
      return in_on_start_page;
   }


   private String cfg_file_dir = null;
   private boolean jsp = false;

   /**
    * Initializes the instance
    */
   public void OnStartPage( HttpServletRequest  sreq,
                            HttpServletResponse sresp,
                            HttpServlet         servlet,
                            String              cfgpath,
                            String              path )
   {
      OnStartPage( sreq, sresp, servlet, cfgpath, path, null, 0);
   }

   private boolean std_portlet;

   /*
    * Returns ture is the portlets are running under standard (JSR 168 compliant)
    * portal. ie. Pluto.
    */
   public boolean isStdPortlet()
   {
      return std_portlet;
   }

   public void OnStartPage( HttpServletRequest  sreq,
                            HttpServletResponse sresp,
                            HttpServlet         servlet,
                            String              cfgpath,
                            String              path,
                            String              portlet_name,
                            int                 portlet_state)
   {
      this.servlet  = servlet;
      asp_request   = sreq;
      asp_response  = sresp;
      cfg_file_dir  = cfgpath;

      if( path.endsWith(".jsp") )
          jsp = true;

      if( !Str.isEmpty(portlet_name) )
      {
         if(DEBUG) debug("ASPManager: standard (JSR168) portlet.");
         std_portlet = true;

         //MAPELK: Commented due to a class cast exception in WebSphere 5.1
         //render_response = (RenderResponse) sresp;
      }

      if(DEBUG)
      {
         debug("\n\nCreating an instance of "+getClass().getName()+" for ["+path+"] using '"+cfg_file_dir+"'");
         ClassLoader cl = this.getClass().getClassLoader();
         if(cl!=null)
            debug("ASPManager: current class loader: "+cl.getClass().getName()+"@"+System.identityHashCode(cl) );
         else
            debug("ASPManager: current class loader is null");

         debug("ASPManager.OnStartPage: std_portlet="+std_portlet);
         debug("ASPManager.OnStartPage: portlet_name="+portlet_name);
      }

      Throwable start_exception = null;
      try
      {
         total_event    = total_event_type.begin();
         on_start_event = on_start_event_type.begin();
         if ( DEBUG ) debug("\n"+Util.rpad("-",40,'-')+"\n"+this+": Executing OnStartPage event handler ...");

         try
         {
            asp_application  = servlet.getServletConfig().getServletContext();
            asp_session      = asp_request.getSession(true);
//             asp_server       = AspContext.getServer();

            fetchCookies();

            //change language from logon page. Can b used by other pages too.
            //NOTE: fnd_user property wont get updated. (no user).
            String language_code = getQueryStringValue(PREFERRED_LANGUAGE);
            if (!isEmpty(language_code))
            {
               HashMap lang_table = ASPConfigFile.language_table;
               String lang_code = language_code.toUpperCase();
               if (lang_table.containsKey(lang_code))
               {
                  setUserPreferredLanguage(language_code);
                  getAspSession().setAttribute(LANGUAGE_CHANGED, "Y");
               }
            }

            //if (std_portlet) fetchStdPortalLanguage();

            //user_preferred_language = (String)getAspSession().getAttribute(USER_LANGUAGE_CODE);

            //if (isEmpty(user_preferred_language)) fetchLanguageFromCookie();

//            server_variables = (RequestDictionary)asp_request.getServerVariables();

            if ( DEBUG )
               debug(this+": Fetched AspContext objects:"  +
                     "\n\t  asp_request    ="+asp_request     +
                     "\n\t  asp_response   ="+asp_response    +
                     "\n\t  asp_application="+asp_application +
                     "\n\t  asp_session    ="+asp_session     +
//                  "\n\t  asp_server     ="+asp_server      +
                     "\n\t  asp_cookie_dict="+asp_cookie_dict +
                     "\n\t  user_preferred_language="+user_preferred_language);
         }
         catch ( Throwable any )
         {
            //throw new AspContextException(any.fillInStackTrace());
            start_exception = new AspContextException(any.fillInStackTrace());
         }
         //Moved out of try catch to show the exception in the user's language when
         //an exception occurs in the query string
         user_preferred_language = (String)getAspSession().getAttribute(USER_LANGUAGE_CODE);

         if (isEmpty(user_preferred_language)) fetchLanguageFromCookie();

         page_id = newPageId();

         String user_agent = getUserAgent();
         is_mozilla    = user_agent.indexOf("Mozilla/5.0") > -1;
         is_explorer   = user_agent.indexOf("MSIE") > 0;       //temporary solution for NE7.X
         is_netscape6  = user_agent.indexOf("Netscape6") > 0 || user_agent.indexOf("Netscape/7.") > 0;
         is_netscape4x = user_agent.indexOf("Mozilla/4.") != -1 && user_agent.indexOf("MSIE") == -1 ;

         is_application_search = "Y".equals(getQueryStringValue("__APP_SEARCH"));

         String dynamic_lov_view = readValue("__DYNAMIC_LOV_VIEW");
         dynamic_def_key = readValue("__DYNAMIC_DEF_KEY");

         String req_method = readValue("__REQUEST_METHOD_TYPE");
         if(req_method!=null)
            get_request = req_method.equals("GET");
         else
            get_request = false;

         page_copy_key   = readValue(PAGE_COPY_KEY);
         String pool_key = path;

         if ( !Str.isEmpty(dynamic_lov_view) )
            pool_key = path + dynamic_lov_view;
         if ( !Str.isEmpty(dynamic_def_key) )
            pool_key = path + DEF_KEY_SEPARATOR+dynamic_def_key; //used in ASPPage to make profile pool key

         if ( !Str.isEmpty(page_copy_key) )
            pool_key += PAGE_COPY_SEPARATOR+page_copy_key;


         pool_key += ASPPagePool.getPoolKeyLangSuffix(this);
         //Bug 40931, start
         //asp_page_handle = ASPPagePool.getASPPageHandle( this, pool_key );
         asp_page_handle = ASPPagePool.getASPPageHandle( this, pool_key, null );
         //Bug 40931, end
         if ( asp_page_handle==null )
         {
            if ( DEBUG ) debug(this+": ASPPage \""+path+"\" not found in pool. Constructing new ASPPage.");
            try
            {
               if (jsp)
               {
                  if ( DEBUG ) debug(this+": Not a javapage. Trying to instantiate a JSP page ...");
                  asppage = new ASPPage(this,path);
               }
               else
               {
                  if ( DEBUG ) debug(this+": Trying to instantiate a new Java page: "+path);
                  asppage = ASPPageProvider.newPage(this,path);
               }
            }
            catch ( FndException x )
            {

               asppage = new ASPPage(this,path);
               if ( x.isJavaError() && (x.getCaughtException() instanceof ClassNotFoundException) )
               {
                  if ( DEBUG ) debug(this+": Class not found. Throwing exception ...");
                  //throw new FndException("FNDMGRCLSNFND: Page '&1' could not be found.",path);
                  start_exception = new FndException("FNDMGRCLSNFND: Page '&1' could not be found.",path);
               }
               else
               {
                  //throw x;
                  start_exception = x;
               }
            }

            asppage.setPoolKey(pool_key);
            asplog = asppage.getASPLog();
            config = asppage.getASPConfig();
            context = asppage.getASPContext();

            if (!Str.isEmpty(dynamic_def_key))
               asppage.construct(dynamic_def_key);
            else
               asppage.construct();

            setPageId();
            context.doActivate();
            ASPAdmin.runAdmin();
         }
         else
         {
            asppage = asp_page_handle.getASPPage();
            if ( DEBUG ) debug(this+": ASPPage \""+path+"\" ("+asppage.getStateName()+") found in pool.");
            asplog = asppage.getASPLog();
            config = asppage.getASPConfig();
            context = asppage.getASPContext();
            setPageId();
            asppage.activate();
         }

         //avoid possibility to go back in weblogic after loggin out.
         if (!isEmpty(getUserId()))
         {
             context.setAuthorizedCookie(getUserId());
         }

         if (!isEmpty(readValue("fedsearch"))){
             fetchRealUserName();
             loadTranslations();
             ((ASPPageProvider)asppage).run();
         }

         asppage.setStdPortlet(portlet_name,portlet_state);

         if ( DEBUG )
            debug(this+": Fetched AspContextASPPage objects:"  +
                  "\n\t  asppage  ="+asppage  +
                  "\n\t  pool_key ="+pool_key +
                  "\n\t  asplog   ="+asplog   +
                  "\n\t  config   ="+config   +
                  "\n\t  context  ="+context  );

         page_never_expires = "N".equals(config.getParameter("ADMIN/PAGES_CAN_EXPIRE","N") );

         // Navigate popup
         if ( !asppage.isPopupExist("navigate") )
            asppage.newASPPopup("navigate",(ASPPoolElement)asppage);

         if ( !asppage.isPopupExist("views") )
            asppage.newASPPopup("views",(ASPPoolElement)asppage);

         //More Views popup
         if ( !asppage.isPopupExist("more_views") )
            asppage.newASPPopup("more_views",(ASPPoolElement)asppage);

         //Options popup
         if ( !asppage.isPopupExist("options") )
            asppage.newASPPopup("options",(ASPPoolElement)asppage);

         //Config Views popup
         if ( !asppage.isPopupExist("config_views") )
            asppage.newASPPopup("config_views",(ASPPoolElement)asppage);

         // Help popup
         if ( !asppage.isPopupExist("help") )
            asppage.newASPPopup("help",(ASPPoolElement)asppage);
         else
            asppage.getASPPopup("help");

         checkGlobalURL();

         if ( isTraceOn() )
         {
            trace("Current URL: "+getURL());
            String qstr = getQueryString();
            if ( !Str.isEmpty(qstr) ) trace("Current QueryString: "+qstr);
         }

         //setPageId();
         if (!jsp)
         {
             fetchUserPreferredLanguage();
             //checkUserAuthorized();
             fetchRealUserName();
         }
         if (isEmpty(readValue("REFRESHLANGUAGE")))
            loadTranslations();

         on_start_event.end();
         if(start_exception!=null)
            throw start_exception;
         if ( DEBUG ) debug(this+": End of OnStartPage event handler ...\n"+Util.rpad("-",40,'-')+"\n");

         String contentType = sreq.getContentType();
         int contentLength = sreq.getContentLength();
         String upload_from = readValue("UPLOAD_FROM");
         upload_from = isEmpty(upload_from)?"":upload_from;

         if("__WEB_CLIENT".equals(upload_from) && contentType!=null && contentType.indexOf("multipart/form-data")!=-1 && contentLength > 0)
         {
           try{
              processMultipartRequest(sreq, contentType);
           }catch(Exception e)
           {
              writeResponse("<html>Error: "+e+"</html>");
           }
         }
      }
      catch ( ResponseEndException x )
      {
         return;
      }
      catch ( Throwable e )
      {
         try
         {
            if(path.indexOf("j_security_check")==-1)
            {
               debug(this+": Error occured while executing OnStartPage event handler ...");
               error(e);
            }
         }
         catch ( Throwable x )
         {
            endResponse();
         }
      }
      finally
      {
         in_on_start_page = false;
      }

      try
      {
         if ( asppage instanceof ASPPageProvider )
         {
            boolean re_direct = false;
            if (isMobileVersion())
            {
               if(!(asppage instanceof MobilePageProvider) && !(asppage instanceof ifs.fnd.pages.Logon))
                  re_direct = true;
            }
            if ( DEBUG ) debug(this+": Trying to run main() in Java page ...");
            reserveJAPConnection();
            if(re_direct)
            {
               getAspResponse().sendRedirect(getAbsoluteURL("/webmobile/"+correctURL(getConfigParameter("APPLICATION/LOCATION/MOBILE_NAVIGATOR","Navigator.page?MAINMENU=MOBILE"))));
            }
            else
            {
                //Get the query string values set inside the session because of loosing them to jaas
                getQueryStringAtLogon();
                ((ASPPageProvider)asppage).main(this);
                is_lov_or_navigator = (asppage.getASPLov()!= null || NAVIGATOR_CLASS_NAME.equalsIgnoreCase(asppage.getClass().getName()) || MOBILE_NAVIGATOR_CLASS_NAME.equalsIgnoreCase(asppage.getClass().getName()));
            }
         }
      }
      catch ( ResponseEndException x )
      {
      }
      catch ( ASPLog.AbortException x )
      {
      }
      catch ( Throwable e )
      {
         //logError(e);
         error(e);
      }
      finally
      {
         if ( asppage instanceof ASPPageProvider && end_response )
            try
            {
                if (config.isGZIPEnabled())
                {
                    if (!is_lov_or_navigator)
                        endResponse();
                }
                else
                   endResponse();
            }
            catch ( ResponseEndException x )
            {
            }
      }
   }

   /**
    * Calls reset() method on ASPPage, close the log.
    */
   public void OnEndPage()
   {

      try
      {
         on_end_event = on_end_event_type.begin();
         if ( DEBUG ) debug("\n"+Util.rpad("-",40,'-')+"\n"+this+": Executing OnEndPage event handler ...");
         asppage.forceDirty();
         asppage.generate(); // generation of navigator and LOV
      }
      catch ( ResponseEndException x )
      {
      }
      catch ( Throwable e )
      {
         logError(e);
      }
      finally
      {
         try
         {
            if ( end_response )
               endResponse();
         }
         catch ( ResponseEndException x )
         {
         }
         catch ( Throwable x )
         {
            logError(x);
         }
      }

      try
      {
         if( connection!=null ) connection.release();
         connection = null;
      }
      catch ( Throwable e )
      {
         logError(e);
      }

      try
      {
         if ( asp_page_handle!=null && !noreset )
            //Bug 40931, start
            //ASPPagePool.unlock(asp_page_handle,this); // calls page.reset()
            ASPPagePool.unlock(asp_page_handle,this,asppage); // calls page.reset()
            //Bug 40931, end
      }
      catch ( Throwable e )
      {
         logError(e);
      }
      asp_page_handle = null;

      try
      {
         if ( profile_page_handle!=null && !noreset )
            //Bug 40931, start
            //ASPPagePool.unlock(profile_page_handle,this); // calls page.reset()
            ASPPagePool.unlock(profile_page_handle,this,asppage); // calls page.reset()
            //Bug 40931, end
      }
      catch ( Throwable e )
      {
         logError(e);
      }

      if (common_jap_connection!=null)
         common_jap_connection.release();

      profile_page_handle = null;

      try
      {
         if ( asplog!=null ) asplog.close();
         asplog = null;
      }
      catch ( Throwable e )
      {
         asplog = null;
         debug(this+": ASPManager.onEndPage(): Cannot close ASPLog:\n\n\t"+
               Str.getStackTrace(e));
      }

      if( DEBUG )
      {
         debug(this+": End of OnEndPage event handler ...\n"+
               "\n======================================================================\n\n\n");
      }
      //Bug 40931, start
      if( ASPPagePool.DEBUG )
      {
         ASPPagePool.showContentsWithMessage("End of OnEndPage event handler");
      }
      //Bug 40931, end
      on_end_event.end();
      total_event.end();
   }


   private void traceRequest() throws IOException
   {
//       int size = asp_request.getTotalBytes();
//       byte[] data = new byte[size];
//       asp_request.read(data);
//       Util.writeFile("C:\\Request.dat",data);
   }

   private void loadTranslations()
   {
      String lng = getLanguageCode();
      String default_language = getASPConfig().getDefaultLanguage();

      //TODO: this should be improved after config file read funtionality is moved up
      if((!lng.equals(default_language)) && (!DictionaryCache.isLanguageInitialized(default_language)))
         DictionaryCache.initLanguage(this,default_language);

      String module = DictionaryCache.getModule(getURL());

      if (!DictionaryCache.isLanguageInitialized(lng))
         DictionaryCache.initLanguage(this,lng);

      if (!isEmpty(module))
         if (!DictionaryCache.isModuleInitialized(lng,module))
            DictionaryCache.initModule(this,lng,module);
   }


   private static String constant_page_id;
   private static int    request_count;

   private String newPageId()
   {
      synchronized(getClass())
      {
         if ( constant_page_id==null )
         {
            constant_page_id = //Kernel32.GetCurrentProcessId() +
                               "x" +
                               String.valueOf( (System.currentTimeMillis()-900000000000L)/1000 ) +
                               "x";
         }
         request_count++;
         return constant_page_id + request_count;
      }
   }

   /**
    * Convert from byte array to string hex representation.
    */
   public String convertToHexText(byte[] in)
   {
      try
      {
         return Util.toHexText(in);
      }
      catch ( Throwable e )
      {
         error(e);
      }
      return null;
   }

   /**
    * Convert from string hex representation to byte array.
    */
   public byte[] convertFromHexText(String in)
   {
      try
      {
         return Util.fromHexText(in);
      }
      catch ( Throwable e )
      {
         error(e);
      }
      return null;
   }

   /**
    * This function will convert a String hex representation of an image to a real binary image.
    * It will also end the request and generate the picture.
    * String format is the picture type used.
    */
   public void getHexPicture(String hexpicture, String format)
   {
      try
      {
         setAspResponsContentType("image/"+format);
         setAspResponseBuffered(true);

         writeResponse(convertFromHexText(hexpicture));
         endResponse();
      }
      catch ( Throwable e )
      {
         error(e);
      }
   }


   /**
    * This function will check how much memory Java has allocated. If it's over a given
    * value a Java garbage collection will be forced.
    * This function should only be used via ASPConfig.ifm
    */

   //static ASPManager eg_mgr;
   static String bytes;
   static long gc_counter=0;

   public static void emptyGarbage()
   {
      try
      {
         int bytes = ASPConfig.getGarbageLimit()*1000000;
         if ( bytes<0 ) throw new FndException("ASDFJR: Invalid Memory Limit for variable EMPTY_GARBAGE/LIMIT in ASPStaticConfig");
         Runtime r  = Runtime.getRuntime();
         long free  = r.freeMemory();
         long total = r.totalMemory();
         if ( DEBUG ) Util.debug("\n\n\tTotal memory:"+Util.lpad(""+total,10)+
                                 " bytes\n\tFree  memory:"+Util.lpad(""+free,10)+" bytes\n\n");
         if ( total>=bytes )
         {
            gc_counter++;
            r.gc();
            if ( DEBUG ) Util.debug("\n\tRunning Garbage Collector ..............");
            if ( gc_counter>9 ) Util.debug("ASWEG: Garbage Collection is executed everytime, check you settings in ASPStaticConfig: STATIC/ADMIN_THREAD/EMPTY_GARBAGE/LIMIT");
         }
         else
            gc_counter=0;
         free  = r.freeMemory();
         total = r.totalMemory();
         if ( DEBUG ) Util.debug("\n\n\tTotal memory:"+Util.lpad(""+total,10)+
                                 " bytes\n\tFree  memory:"+Util.lpad(""+free,10)+" bytes\n\n");
      }
      catch ( Throwable any )
      {
         if ( DEBUG ) Util.debug("ASPManager.emptyGarbage failed");
      }
   }


   //==========================================================================
   // Centura
   //==========================================================================

   /**
    * Returns an ifm-header. If the client machine is set up correctly, it will run
    * the Centura form "windowname" with. The Database name will be fetched from
    * ASPConfig.ifm APPLICATION/ID.
    */

   public void runCentura(String windowname, String query)
   {
      setAspResponsContentType("application/ifm");

      responseWrite("!FNDCLI.STARTWINDOW\r\n");
      responseWrite("$WINDOW_NAME="+windowname+"\r\n");
      responseWrite("$QUERY="+query+"\r\n");

      responseWrite("$MDI.MODE=SDI\r\n");

      ASPTransactionBuffer trans = newASPTransactionBuffer();
      ASPCommand cmd = newASPCommand();
      cmd.defineCustomFunction("Fnd_Session_API.Get_Fnd_User");
      cmd.addParameter("USERNAME", "S", null, null);
      trans.addCommand("SESS", cmd);

      trans = perform(trans);

      responseWrite("$DB.USER="+ trans.getValue("SESS/DATA/USERNAME") +"\r\n");
      responseWrite("$DB.NAME="+ getConfigParameter("APPLICATION/ID") +"\r\n");

      endResponse();
   }

   private int getMatchLevel(int masks[], int ip)
   {
      if (DEBUG) debug("ASPManager.getMatchLevel");

      int last_level = 0; // no match
      if (masks != null)
      {
         int level = 0;
         for (int i = 0; i < masks.length; i++)
         {
            if (masks[i] == 0xFFFFFFFF)
               level = 1; // lowest level any match
            else if (ip == masks[i])
               return 9; // highest level or exact match, no other masks are checked
            else
            {
               level = 5;
               for (int j = 0, m = 0x000000FF; j < 4; j++)
               {
                  if ((masks[i] & m) == m) // this position of ip is a wildcard
                     level--;
                  else
                  {
                     if ((masks[i] & m) != (ip & m)) // non wildcard compare the same pos
                     {
                        level = 0;  // this ip does not match the mask
                        break;
                     }
                  }
                  m = m << 8;
               }
            }
            if (level > last_level)
               last_level = level;
         }
      }
      return last_level;
   }

   private boolean canGZIP(int buffer_size, String ip)
   {
      if (DEBUG) debug("ASPManager.canGZIP IP:" + ip + " size:" + buffer_size);

      //getAspRequest().getRemoteAddr() not supported in JSR 168 returns null for ip
      if (isEmpty(ip)) return false;

      if(readValue("VALIDATE") != null || "BOOKMARK".equals(readValue("MAINMENU")) || "TOXML".equals(readValue("MAINMENU")))
         return false;
      else if (config.isGZIPEnabled() && buffer_size > config.getMinZipSize())
      {

         String accept_header = getAspRequest().getHeader("Accept-Encoding");

         if(accept_header!=null && accept_header.indexOf("gzip")>=0 && (accept_header.indexOf("gzip;q=0")<0) )
         {
            ASPContext ctx = asppage.getASPContext();

            if (ctx != null)
            {
               String can_gzip = ctx.findGlobal(CAN_GZIP_ENABLED, "");

               if ("Y".equals(can_gzip))
                 return true;
               else if ("N".equals(can_gzip))
                 return false;
            }

            if (DEBUG) debug("ASPManager.canGZIP: global value not found");

            int current_ip = config.convertIpToInt(ip);
            boolean enable_gzip = getMatchLevel(config.getEnabledMask(), current_ip) >= getMatchLevel(config.getDisabledMask(), current_ip);

            if (ctx != null)
               ctx.setGlobal(CAN_GZIP_ENABLED, enable_gzip ? "Y" : "N");

            if (DEBUG) debug("ASPManager.canGZIP: returning " + enable_gzip + " for IP " + ip);
            return (enable_gzip);
         }
      }
      return false;
   }

   /**
    * Appends an ID to a given URL. If the URL is already having a querystring
    * the ID will be placed inbetween the URL and the query string.
    * eg: http://chdelk/b2e/demorw/Order.page?__ID=325463
    */
   public String correctURL(String url)
   {
      String RWC_param_value = "";
      if(isRWCHost() && url.indexOf("__HOST=RWC")<0)
            RWC_param_value = "&__HOST=RWC";
      if(config.isBrowserCacheDisabled())
      {
         int page_index = url.indexOf(".page");
         if (page_index < 1)
             return url;
         int page_last_index = page_index + 5;
         if (url.indexOf("?")==-1)
            return url.substring(0,page_last_index) +"?__ID="+Math.round(Math.random()*1000000)+ RWC_param_value+ (page_last_index<url.length() ? url.substring(page_last_index):"");
         else if (url.indexOf("&__ID")!=-1) {
            String link = url.substring(0,url.indexOf("?"));
            String qrystr = "";
            if (url.indexOf("&")!=-1 &&
                url.indexOf("?") == url.lastIndexOf("?") &&
                url.indexOf("&")<=url.indexOf("&__ID"))
               qrystr = "&"+url.substring(url.indexOf("?")+1,url.indexOf("&__ID"));
            return link+"?__ID="+Math.round(Math.random()*1000000)+ RWC_param_value+qrystr;
         } else if (url.indexOf("?")!=-1 &&
                    url.lastIndexOf("?")+1!=url.length() &&
                    url.indexOf("?") == url.lastIndexOf("?") &&
                    url.indexOf("?__ID")==-1) {
            String link = url.substring(0,url.indexOf("?"));
            String qrystr = "&"+url.substring(url.indexOf("?")+1,url.length());
            return link+"?__ID="+Math.round(Math.random()*1000000)+ RWC_param_value+qrystr;
         }
         else {
            String link = url.substring(0,url.indexOf("?"));
            String qrystr = "";
            if (url.indexOf("&")!=-1)
               qrystr = "&"+url.substring(url.indexOf("&")+1,url.length());
            return url.substring(0,url.indexOf("?"))+"?__ID="+Math.round(Math.random()*1000000)+ RWC_param_value+qrystr;
         }
      }
      else
         return url;
   }


   //==========================================================================
   //==========================================================================
   // Inner class for error handling
   //==========================================================================
   //==========================================================================

   class AspContextException extends FndException
   {

      //==========================================================================
      //  Construction
      //==========================================================================

      private AspContextException( Throwable e )
      {
         super(e);
      }
   }


   public class ResponseEndException extends InterruptFndException
   {
   }

   /**
    * This method will return a comma separated list of all installed modules.
    * @see ifs.fnd.asp.ASPManager#getModuleVersion
    * @see ifs.fnd.asp.ASPManager#isModuleInstalled
    */
   public String getInstalledModules()
   {
      String modules = "";
      String module_name = "";

      initInstalledModules();

      String module_list = installed_modules.toString();
      module_list = module_list.substring(1,module_list.length()-1);
      StringTokenizer st = new StringTokenizer(module_list,", ");

      StringTokenizer nv;
      while (st.hasMoreTokens()) {
         nv = new StringTokenizer(st.nextToken(),"=");
         module_name = nv.nextToken();
         modules += module_name + ",";
      }

      return modules.substring(0,modules.length()-1);
   }

  /**
   * This method will return the version of the module. null if the module is not installed.
   * @param module - module name
   *
   * @see ifs.fnd.asp.ASPManager#getInstalledModules
   * @see ifs.fnd.asp.ASPManager#isModuleInstalled
   */
   public String getModuleVersion(String module)
   {
      initInstalledModules();
      return (String) installed_modules.get(module);
   }

  /**
   * This method will return true if the module module is installed, false otherwise
   * @param module - module name
   *
   * @see ifs.fnd.asp.ASPManager#getInstalledModules
   * @see ifs.fnd.asp.ASPManager#getModuleVersion
   */
   public boolean isModuleInstalled(String module)
   {
      initInstalledModules();
      return installed_modules.containsKey(module);
   }

   private void initInstalledModules()
   {
      if (!installed_modules.isEmpty()) return;

      ASPTransactionBuffer trans = newASPTransactionBuffer();
      trans.addQuery("INSTALLED_MODULES","select module,version version from module where version is not null").setBufferSize(500);

      if( asppage.isUndefined() )
         trans = performConfig(trans);
      else
         trans = perform(trans);

      ASPBuffer buff = trans.getBuffer("INSTALLED_MODULES");
      ASPBuffer data_buffer = newASPBuffer();

      for (int i=0; i<buff.countItems()-1; i++)
      {
         data_buffer = buff.getBufferAt(i);
         installed_modules.put(data_buffer.getValueAt(0),data_buffer.getValueAt(1));
      }

   }

   void processMultipartRequest(ServletRequest sreq, String contentType)throws Exception
   {
      int max_file_size =  Integer.parseInt(getASPConfig().getParameter("ADMIN/FILE_UPLOAD/MAX_SIZE_MB","5"));
      int upload_mode = Integer.parseInt(readValue("UPLOAD_MODE"));
      String upload_path = readValue("UPLOAD_DESTINATION");
      String blob = readValue("BLOB_ID");
      String filetype = readValue("UPLOAD_TYPE");
      String blob_id = "";
      FileUpload upload =  new FileUpload(getASPPage());

      long start = System.currentTimeMillis();
      int ind = contentType.indexOf("boundary=");
      String boundary = contentType.substring(ind+9);
      String end_boundary = boundary + "--";

      String line="";
      String filename = "";
      String filepath = "";
      String content_type="";
      String lower_line="";
      byte[] buf = new byte[1024*8];
      int line_len=0;
      int len = 0;
      boolean file_part=false;
      BufferedOutputStream outfile = null;

      ServletInputStream in = sreq.getInputStream();

      StringBuffer sbuf = new StringBuffer();

      while ( (len = in.readLine(buf, 0, buf.length)) != -1 )
      {
         line = new String( buf,0,len ,"UTF-8");
         lower_line = line.toLowerCase();

         if ( line.indexOf(boundary) != -1 || lower_line.startsWith("content-disposition:") )
            file_part = false;
         if ( line.indexOf("\"; filename=\"") != -1 && lower_line.startsWith("content-disposition:") )
            file_part = true;

         if ( file_part && lower_line.startsWith("content-disposition:") )
         {

            if ( line.indexOf("\"; filename=\"") != -1 )
            {
               String lowerLine = line.toLowerCase();
               int idx = 0;
               int filenameStart = lowerLine.indexOf("filename=\"", idx) + 10;
               int filenameEnd = lowerLine.indexOf("\"", filenameStart);
               if ( filenameStart != -1 && filenameEnd != -1 )
                  filename = line.substring(filenameStart, filenameEnd);
               File fp = new File(filename);
               filepath = fp.getPath();
               StringTokenizer st = new StringTokenizer(filename, "\\/");
               while (st.hasMoreElements())
                  filename = st.nextToken();
               int pos = filepath.lastIndexOf(filename)-1;
               if(pos>0)
                  filepath = filepath.substring(0,pos);
               else
                  filepath = "";
            }

            len = in.readLine(buf, 0, buf.length);
            line = new String(buf, 0, len);
            lower_line = line.toLowerCase();

            if ( lower_line.startsWith("content-type:") )
            {
               content_type = line.substring(13).trim();
               len = in.readLine(buf, 0, buf.length);
               line = new String(buf, 0, len);
            }

            if(upload_mode == ASPBlock.UPLOAD_TO_DISK)
            {
               String f = "";
               File file = new File(upload_path);
               file.mkdirs();
               if (upload_path.endsWith(File.separator))
                  f = upload_path+filename;
               else
                  f =  upload_path+File.separatorChar+filename;
               file = new File(f);
               if (file.exists())
                  file.renameTo(new File(upload_path.endsWith(File.separator)?
                                         upload_path+"TMP_"+timeStamp()+"_"+filename:
                                         upload_path+File.separatorChar+"TMP_"+timeStamp()+"_"+filename));
               outfile = new BufferedOutputStream(new FileOutputStream(file), 2048);
            }

            byte[] buf2 = new byte[1024*8];
            int len2 = 0;
            len = in.readLine(buf, 0, buf.length);
            line = new String(buf, 0, len, ENC_TYPE);
            boolean bLastLine=false;

            while ( line.indexOf(boundary) == -1 )
            {
               System.arraycopy(buf, 0, buf2, 0, len);
               len2 = len;
               
               len = in.readLine(buf, 0, buf.length);
               line = new String(buf, 0, len, ENC_TYPE);

               bLastLine = ( line.indexOf(boundary) >= 0 );
               if(upload_mode == ASPBlock.UPLOAD_TO_DISK)
                  outfile.write(buf2, 0, (bLastLine)? len2-2 : len2);
               else
               {
                  sbuf.append(new String(buf2, 0, (bLastLine)? len2-2 : len2, ENC_TYPE));
                  if(sbuf.length() > max_file_size*1024*1024)
                  {
                      file_upload_error = true;
                      upload_file_name = filename;
                  }
               }
            }

            if(outfile!=null)
               outfile.close();
         }
      }

      if (upload_mode == ASPBlock.UPLOAD_TO_DB)
      {
         if ("NULL".equalsIgnoreCase(blob))
            blob_id = upload.writeBlobData(sbuf.toString().getBytes(ENC_TYPE), filename, filepath, filetype);
         else
            upload.updateBlobData(blob, sbuf.toString().getBytes(ENC_TYPE), filename, filepath, filetype);
      }
      else if(upload_mode == ASPBlock.UPLOAD_TEMP)
         profile_file = upload.writeXMLToDisk(sbuf.toString().getBytes(ENC_TYPE), upload_path);

      long end = System.currentTimeMillis();
      String post_function = readValue("POST_FUNCTION");

      if(upload_mode == ASPBlock.UPLOAD_TO_DISK || upload_mode == ASPBlock.UPLOAD_TO_DB )
      {
         upload_successful = true;
         upload_blob_id = blob_id;
         upload_file_name = filename;
         upload_file_path = filepath;
         upload_file_size = sbuf.length()+"";
      }
   }

   /**
    * Returns true if file upload is successful.
    */
   public boolean isFileUploadSuccessful()
   {
       return upload_successful;
   }

   /**
    * Returns true if error occurs when uploading a file.
    */
   public boolean isFileUploadError()
   {
       return file_upload_error;
   }
   
   /**
    * Returns the attributes of uploaded file.
    */
   public String getFileUploadAttribute(String attribute)
   {
       String attr = "";
       
       if ("blob_id".equals(attribute))
           attr = upload_blob_id;
       else if ("filename".equals(attribute))
           attr = upload_file_name;
       else if ("filepath".equals(attribute))
           attr = upload_file_path;
       else if ("filesize".equals(attribute))
           attr = upload_file_size;
       return attr;
   }

   private String timeStamp()
   {
     SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
     Date currentTime = new Date();
     String timeStamp = formatter.format(currentTime);
     return timeStamp;
   }

   /**
    * Returns the temporary profile file name.
    * Used to import profiles saved as xml files.
    */
   public String getTempProfileFileName()
   {
       return profile_file;
   }

   void deleteTempProfileFile(String filename)
   {
      new File(filename).delete();
   }


   /**
    * Returns the redirect from url in the context.
    */
   public String getRedirectFromURL()
   {
      return context.findGlobal(URL_FROM,"");
   }


   public String getDefaultLanguage()
   {
      String default_language_code = "";

      if (ASPConfigFile.multi_language_enabled)
      {
         default_language_code = asp_request.getLocale().getLanguage();

         HashMap lang_table = ASPConfigFile.language_table;
         int size = lang_table.size();
         String lang_code = default_language_code.toUpperCase();
         if (size > 0 && lang_table.containsKey(lang_code)) return default_language_code;
      }

      if (!isEmpty(ASPConfigFile.language_code)){
          return (ASPConfigFile.language_code);
      }

      try
      {
         String file_name = cfg_file_dir + ASPConfigFile.WEB_CONFIG_FILE;
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         File f = new File(file_name);
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document doc = builder.parse(f);

         NodeList nodes = doc.getElementsByTagName("default_language");
         Element node_ele = (Element)nodes.item(0);
         default_language_code = ((Text)node_ele.getFirstChild()).getData();
      }
      catch (Exception e)
      {
         default_language_code = "en";
         //error(e);
      }

      return default_language_code;
   }

   public String getUserLanguage()
   {
      if (!ASPConfigFile.multi_language_enabled) return getDefaultLanguage();

      String language_code = getUserPreferredLanguage();

      //cotext is lost when logged out so read from cookie
      if ("YES".equals(getQueryStringValue("LOGOUT")))
      {
         Cookie cookie = getAspCookie("__USER_LANGUAGE");
         if (cookie != null)
            language_code = cookie.getValue();
      }

      if (isEmpty(language_code))
         language_code = getDefaultLanguage();

      return language_code;
   }


   /**
    * Returns the user defined language.
    * @see setUserPreferredLanguage
    */
   public String getUserPreferredLanguage()
   {
      return (String)getAspSession().getAttribute(USER_LANGUAGE_CODE);
   }

  /**
   * Set the User selected language. Use with caution
   * @param language_code as defined in webclientconfig.xml
   * @see getUserPreferredLanguage
   */
   public void setUserPreferredLanguage(String language_code)
   {
      getAspSession().setAttribute(USER_LANGUAGE_CODE, language_code);
   }

   private void fetchStdPortalLanguage() throws FndException
   {
      if (!std_portlet) return;

      user_preferred_language = asp_request.getLocale().getLanguage();

      if (isEmpty(user_preferred_language))
         user_preferred_language = getDefaultLanguage();
      else
      {
         HashMap lang_table = ASPConfigFile.language_table;
         int size = lang_table.size();
         String lang_code = user_preferred_language.toUpperCase();
         if (size > 0 && !lang_table.containsKey(lang_code))
            throw new FndException("FNDMGRUDEFLANGCODE: Language code &1 is undefined.",user_preferred_language);
      }

      setUserPreferredLanguage(user_preferred_language);
   }

   private void fetchUserPreferredLanguage() throws FndException
   {
      if(DEBUG) debug(this+": ASPManager.fetchUserPreferredLanguage()");

      if (!ASPConfigFile.multi_language_enabled) return;

      if (std_portlet)
      {
         fetchStdPortalLanguage();
         return;
      }

      if( !isEmpty(user_preferred_language) ) return;

      if (config.isFormBasedAuth())
         fetchLanguageFromCookie();
      else
         fetchLanguageFromUserProfile();

      //redirect to Language selection page if no preferred language
      //if (isEmpty(user_preferred_language) && config.isUserExternallyIdentified())
      if (isEmpty(user_preferred_language) && !config.isFormBasedAuth())
      {
         String url = config.getLanguageURL(false);

         if ( Str.isEmpty(url) )
            throw new FndException("FNDMGRUDEFLANGURL: URL for Language selection is undefined.");

         int pos = url.indexOf(".page");
         if(pos<0)
            pos = url.indexOf(".asp");
         if(pos>0)
            url = url.substring(0,pos);

         String my_url = asppage.getURL().toLowerCase();

         pos = my_url.indexOf(".page");
         if(pos<0)
            pos = my_url.indexOf(".asp");
         if(pos>0)
            my_url = my_url.substring(0,pos);

         if( !my_url.equalsIgnoreCase(url) )
         {
            if(DEBUG) debug("  [fetchUserPreferredLanguage]my_url="+my_url+", url="+url);

            if(DEBUG) debug("  [fetchUserPreferredLanguage]redirect to URL: "+url);
            redirectTo(url+".page");
         }
      }
   }

   private void fetchLanguageFromCookie()
   {
      if(DEBUG) debug(this+": ASPManager.fetchLanguageFromCookie()");

      Cookie cookie = getAspCookie("__USER_LANGUAGE");

      if (cookie != null)
         user_preferred_language = cookie.getValue();

      HashMap lang_table = ASPConfigFile.language_table;

      String lang_code = "";
      if (!isEmpty(user_preferred_language))
         lang_code = user_preferred_language.toUpperCase();
      if (!lang_table.containsKey(lang_code)) user_preferred_language = null;

      setUserPreferredLanguage(user_preferred_language);

      if(DEBUG) debug("  fetchLanguageFromCookie: user_preferred_language="+user_preferred_language);
   }

   private void fetchLanguageFromUserProfile()
   {
      if(DEBUG) debug(this+": ASPManager.fetchLanguageFromUserProfile()");

      String user_id = getUserId();

      if( isEmpty(user_id) )
      {
         context.removeGlobal(USER_LANGUAGE_CODE);
         user_preferred_language=null;
         return;
      }

      user_id = user_id.toUpperCase();

      if(DEBUG) debug("  fetchLanguageFromUserProfile: language_code="+user_preferred_language);

      if( !isEmpty(user_preferred_language) ) return;

      user_id = Str.replace(user_id,"/","'||chr(47)||'");
      user_id = Str.replace(user_id,"\\","'||chr(92)||'");
      user_id = user_id.toUpperCase();

      ASPTransactionBuffer trans = newASPTransactionBuffer();

      ASPCommand cmd = newASPCommand();

      String fnd_user = null;

      if (getASPConfig().useAuthorization() && asppage.isUndefined())
      {
         trans.addQuery("GET_NAME","SELECT IDENTITY FROM FND_USER WHERE WEB_USER='"+user_id+"'");
         trans = performConfig(trans);

         fnd_user = trans.getBuffer("GET_NAME/DATA").getValue("IDENTITY");
      }
      else
         fnd_user = getFndUser();

      cmd.defineCustomFunction("FND_USER_API.Get_Property");
      cmd.addParameter("LANG_CODE","S",null,null);
      cmd.addParameter("FNDUSER","S", "IN", fnd_user);
      cmd.addParameter("PROPERTY","S", "IN", "PREFERRED_LANGUAGE");

      trans.addCommand("GET_LANG_CODE",cmd);

      if( asppage.isUndefined() )
         trans = performConfig(trans);
      else
         trans = perform(trans);

      //set preferred language
      try
      {
         user_preferred_language = trans.getBuffer("GET_LANG_CODE/DATA").getValue("LANG_CODE");
         HashMap lang_table = ASPConfigFile.language_table;
                        String lang_code = user_preferred_language.toUpperCase();
         if (!lang_table.containsKey(lang_code)) user_preferred_language = null;
      }
      catch ( Throwable e )
      {
         user_preferred_language = null;
      }

      setUserPreferredLanguage(user_preferred_language);

      //redirect to currect URL to get the correct poolkey
      /* redirection now in fetchRealUserName
      if( !isEmpty(user_preferred_language) )
      {
         String qrystr = getQueryString();
         if (isEmpty(qrystr))
            redirectRequest(asppage.getURL());
         else
            redirectRequest(asppage.getURL()+"?"+qrystr);
      }
      */

      if(DEBUG) debug("  fetchLanguageFromUserProfile: user_preferred_language="+user_preferred_language);

      return;
   }

   String getCurrentHost()
   {
       return  asp_request.getServerName()+(asp_request.getServerPort()==0?"":":"+asp_request.getServerPort());
   }

   int getCurrentHostIndex()
   {
      HashMap req_hosts = getASPConfig().getRequestingHosts();
      String host = (String)req_hosts.get(getCurrentHost());

      if (host == null && req_hosts.containsKey("*"))
         return -1;

      return Integer.parseInt(host)-1;
   }

   boolean isDifferentApplicationPath()
   {
      String curr_host = getCurrentHost();
      HashMap req_hosts = getASPConfig().getRequestingHosts();
      return req_hosts.containsKey(curr_host) && !"0".equals((String)req_hosts.get(curr_host));
   }

   /**
    * Returns the style sheets tag. This is called from the framework. Developers should call this when
    * design custom pages. This will return the stylesheet belongs the current theme ene user has selected
    * and the language specific styles for the current language.
    **/

   public String getStyleSheetTag()
   {
      String tag = "<link rel='STYLESHEET' href='"+
            getASPConfig().getParameter("APPLICATION/LOCATION/STYLESHEETS","../common/stylesheets/+")+
            getCSSFileName()+"' type='text/css'><link rel='STYLESHEET' href='"+
            getASPConfig().getParameter("APPLICATION/LOCATION/STYLESHEETS","../common/stylesheets/+")+
            getLanguageCSSFileName()+"' type='text/css'>"  ;
      if(isRTL())
         tag += "<link rel='STYLESHEET' href='"+
               getASPConfig().getParameter("APPLICATION/LOCATION/STYLESHEETS","../common/stylesheets/+")+
               getASPConfig().getParameter("APPLICATION/LOCATION/RTL_STYLESHEET","rtl.css")+
               "' type='text/css'>";
      return tag;
   }

   /**
    * Returns the css file name according to the end-users theme.
    **/

   public String getCSSFileName()
   {
      try
      {
         String cssfile = "";
         if(isMobileVersion())
            cssfile = config.getMobileCSSFile();
         else
            cssfile = config.getThemeData(getUserThemeID())[1];
         return cssfile;
      }
      catch (FndException e)
      {
         Util.debug(e.getMessage());
         return "dummy.css";
      }
   }

   /**
    * Retuns the language specific stylesheet name.
    **/

   public String getLanguageCSSFileName()
   {
         return "Styles-"+getLanguageCode()+".css";
   }


   public String getScriptFileTag()
   {
      if(isMobileVersion())
         return getMobileScriptFileTag();

      String tag = getASPConfig().getGenericClientScript() +
                   getASPConfig().getConstantClientScript();

      return tag;
   }

   /**
    * Return the current user theme index. It first looks for temporary theme which can be send in the Query String
    * parameter __TEMP_THEME. Then look in the profile. Finaly returns the default frofile index if no theme index found.
    */

   public int getUserThemeID()
   {

      if (DEBUG)
         debug("~~~~~ Collecting Theme Index ~~~~~~~~");
      //if (!getASPContext().isLogOn()) return 1;
      String theme="1";
      if (!isEmpty(readValue("__TEMP_THEME")))
      {
         theme = readValue("__TEMP_THEME");
         if (DEBUG)
            debug("        Temporary theme index found: " + theme);
      }

      else
      {
         if (isPortalPage())
         {
            theme = getPortalPage().readGlobalProfileValue(ProfileUtils.SELECTED_THEME,false);
            if(isEmpty(theme))
            theme = getPortalPage().readGlobalProfileValue("Defaults/Theme"+ProfileUtils.ENTRY_SEP+"Default",false);
         }
         else
         {
            theme = getASPPage().readGlobalProfileValue(ProfileUtils.SELECTED_THEME,false);
            if(isEmpty(theme))
            theme = getASPPage().readGlobalProfileValue("Defaults/Theme"+ProfileUtils.ENTRY_SEP+"Default",false);
         }
         
         if (DEBUG)
            debug("        theme index found from profile : " + theme);
      }

      if (isEmpty(theme))
      {
         if (DEBUG)
            debug("        No theme index found");
         return Integer.parseInt(getASPConfig().getParameter("THEMES/DEFAULT","4"));
      }
      else
      {
         String[][] themes = getASPConfig().getThemes();
         for(int i=0; i< themes.length; i++)
            if(!isEmpty(themes[i][0]) && themes[i][0].toUpperCase().equals(theme.toUpperCase())) return i+1;
      }
      try
      {
         return Integer.parseInt(theme);
      }catch(NumberFormatException nfe){
         return 4;
      }
   }

   String  getUserTheme()
   {
      return (isMobileVersion())? "": "theme"+getUserThemeID();
   }


   /* MAPELK: Commented due to a problem in WebSphere. setTitle dose not work now.
   RenderResponse getRenderResponse()
   {
      return render_response;
   }
   */

   /**
    * Returns the absolute url of the current application.
    * @return Absolute url of the current application. Eg: http://cmbrnd34:8080/b2e
    */
   public String getApplicationAbsolutePath()
   {
       return getProtocol() + "://" + getApplicationDomain() +  getApplicationPath() ;
   }


   /** Returns the stored DECISION buffer from the session. This will be used in
    * re-authentication process. Useless for non-framework-developers.
    */
   public Buffer getDecisionBuffer()
   {
      return (Buffer)getAspSession().getAttribute(DECISION_BUF);
   }


   /** Removes the DECISION buffer stored in the session. This method will be called
    * from the doRest of the page where the decision buffer added to the all ASPTransactionBuffers.
    */
   void removeDecisionBuffer()
   {
      getAspSession().removeAttribute(DECISION_BUF);
   }

   boolean isDefaultLayout()
   {
      return "Y".equals(readValue(DEFAULT_LAYOUT));
   }

   boolean isLogonPage()
   {
      return asppage.isLogonPage();
   }

   public void displayProfileCache()
   {
      ASPProfileCache.showContents();
   }

   /**
    * Displays the Dynamic Object Cache.
    */

   public void dispalyDynamicCache()
   {
      DynamicObjectCache.showContents(this);
   }

   private boolean isMultiRowDelete()
   {
      return is_multirow_delete;
   }

   public static String encodeStringForJavascript( String input ) {
       String encoded = "";
       if((input != null) || (!Str.isEmpty(input))) {
          for ( int i = 0; i < input.length(); i++ )
             encoded += encodeCharForJavascript( input.charAt(i) );
       }
       return encoded;
    }

    private static String encodeCharForJavascript( char input ) {
       int unicode = (int) input;
       boolean encode = false;

       if ( (unicode <  32) || (unicode >  127) )
          encode = true;

       switch( input ) {
          case '\\':
          case '\'':
          case '\"':
          case '<':
          case '>':
             encode = true;
       }

       if ( ! encode ) return ""+input;

       return encodeCharForJavascriptU( unicode );
    }

    private static String encodeCharForJavascriptU( int unicode ) {
       final char HEX[] = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };

       if (unicode < 0) unicode = 0;
       if (unicode > 65535) unicode = 0;

       String sU = "\\u";
       sU += HEX[ (unicode >> 12) & 0xF ];
       sU += HEX[ (unicode >>  8) & 0xF ];
       sU += HEX[ (unicode >>  4) & 0xF ];
       sU += HEX[ unicode & 0xF ];
       return sU;
    }

    public void showUsageKeys(String usage_key){

     String out = "\n\n\n Translation constants outside PREDEFINE:\n   =========================\n";

     Set s = (Set)getAspSession().getAttribute(usage_key+"_"+DIRTY_LABELS);
     if (s!= null)
        out = out + "\n   "+usage_key+"_"+DIRTY_LABELS + " ["+s.toString()+"]";

     debug(out+"\n\n\n");
     synchronized(frame_pages_key_collection)
     {
         out = "\n\n\n Translation constants from Frame components:\n   =========================\n";
         Enumeration frame_page_key_keys = frame_pages_key_collection.keys();
         while( frame_page_key_keys.hasMoreElements() )
         {
            String key = (String)frame_page_key_keys.nextElement();
            Set s2 = (Set)frame_pages_key_collection.get(key);
            out = out + "\n   "+key + " ["+s2.toString()+"]";
         }
         debug(out+"\n\n\n");
      }
   }

   public boolean isRWCHost()
   {
      if(!isAuroraFeaturesEnabled()) return false;
      try
      {
         if (RWC_HOST.equals(readValue(HOST_PARAM)))
         {
            getAspSession().setAttribute(HOST_PARAM,RWC_HOST);
            return true;
         }
      }
      catch ( Exception e ) {
          ;
      }

      if (RWC_HOST.equals(getAspSession().getAttribute(HOST_PARAM)))
         return true;
      else
         return false;
   }

   /**
    * Returns a boolean indicating whether the current language is
    * a "Right to Left" language
    */
   public boolean isRTL()
   {
    return getASPConfig().isRTL();
   }

    public HashSet getModuleList()
    {
       HashSet modules = ((RequestHandler)servlet).getModuleList();
       if (DEBUG)
       {
           Iterator module_enum = modules.iterator();
           while (module_enum.hasNext())
               debug((String)module_enum.next());
       }
       return modules;
    }

    private boolean isInactivePortalViewsDisabled()
    {
        boolean tab_header_disabled = "Y".equals(getQueryStringValue("DISABLE_PORTAL_TAB_HEADER")) ? true : false;
        
        if (isRWCHost() && tab_header_disabled)
           return true;
        else
           return false; 
    }
   //===================================================================================
   // Context Substitution Variable Section
   //===================================================================================

   /**
    * initialise Context Substitution Variables
    */
   void initCSVCache(){
      contextsubvars.init(this);
   }

   /**
    * Reset Context Substitution Variables
    */
   public void resetCSVCache(){
      contextsubvars.reset(this);
   }

   /**
    * Looks up for a User context variable value
    * @param name Name of the CSV
    * @return String value for a user context variable, null if not found
   */
   public String getCSVValue(String name) {
      return getCSVValue(name, null, null);
   }

   /**
    * Looks up for a User context variable value
    * @param name Name of the CSV
    * @param field ASPField of the CSV
    * @return String value for a user context variable in the client format or null if not found
   */
   public String getCSVValue(String name, ASPField field){
      return getCSVValue(name, null, field);
   }

   /**
    * Looks up for a User context variable value
    * @param name Name of the CSV
    * @param default_val Default value of the CSV
    * @param field ASPField of the CSV
    * @return Returns the value for a user context variable in the client format or if not found the default value is returned
   */
   public String getCSVValue(String name, String default_val, ASPField field) {
      String returnStr = null;
      returnStr = (String) contextsubvars.getCSVValue(name, default_val, field,  this);
      return (Str.isEmpty(returnStr))? name: returnStr;
   }

   /**
    * Encodes a raw URL strng with CSVs
    * @param url URL to encode
    * @return String encoded url
    */
   public String encodedURLWithCSV(String url) {
      String baseUrl = toCSVName("BASE_URL");
      String app_path = getASPConfig().getApplicationPath();

      if (url.startsWith(app_path))
        url = Str.replace(url,app_path, baseUrl);
      else
        url = Str.replace(url, getCSVValue(baseUrl),baseUrl);
      return url;
   }

   /**
    * Dencodes a URL strng with CSV values
    * @param url URL to dencode
    * @return String dencoded url
    */
   public String decodedURLWithCSV(String url) {
      String baseUrl = toCSVName("BASE_URL");
      url = Str.replace(url, baseUrl, getCSVValue(baseUrl));
      return url;
   }

   /**
    * Return all CSV names
    * @return String[] CSV names
    */
   public String[] getCSVNames()
   {
      return contextsubvars.getCSVNames(contextsubvars.All_CSV);
   }

   /**
    * Return Selected CSV names
    * @return String[] CSV names
    */
   public String[] getCSVNames(int csv_type)
   {
      return contextsubvars.getCSVNames(csv_type);
   }

   /**
    * Replace all CSV names within a query string with the coresponding CSV value
    * Query only CSV's are NOT replaced
    * @param query Query string with the CSV names
    * @return String CSV replaced query string
    */
   public String parseCSVQueryString(String query)
   {
      return contextsubvars.parseCSVQueryString(query, false, this);
   }

   /**
    * Replace all CSV names within a query string with the coresponding CSV value
    * @param query Query string with the CSV names
    * @return String CSV replaced query string
    */
   public String parseCSVQueryString(String query, boolean include_query_only)
   {
      return contextsubvars.parseCSVQueryString(query, include_query_only, this);
   }

   /**
    * Check to see if CSV or not
    * @param name Name to check
    * @return boolean true if CSV or false if not
    */
   public boolean isCSVName(String name){
      return contextsubvars.isCSVName(name);
   }
   
   /**
    * Check to see if string contains CSV values or not
    * @param value String containg csv names
    * @return boolean true if CSV names exist or false if not
    */
   public boolean hasCSVNames(String value)
   {
      return contextsubvars.hasCSVNames(value);
   }

   /**
    * Convert a given name to a CSV
    * @param name Name to be converted
    * @return String converted name
    */
   public String toCSVName(String name){
      return contextsubvars.toCSVName(name);
   }

   /**
    * Converts the date format mask to SQL format mask.
    */
   public String toSqlDateMask( String mask )
   {
      int size = mask.length();
      String new_mask = "";
      String temp = "";
      boolean is24H = mask.indexOf("H")>-1;

      for (int i=0; i<size; i++) {
         char ch = mask.charAt(i);
         if (Character.isLetterOrDigit(ch) )
            temp +=ch+"";
         else
         {
            int len = temp.length();
            if (len >0)
            {
               if (len == 1)
                  temp = correctSingleCharFormat(temp.charAt(0),is24H);
               new_mask += temp + ch;
               temp = "";
            }
         }
      }
      if (temp.length()==1)
         temp = correctSingleCharFormat(temp.charAt(0),is24H);
      new_mask += temp;
      new_mask = new_mask.replaceAll("HH","hh24");
      new_mask = new_mask.replaceAll("mm","mi");
      new_mask = new_mask.replaceAll("MMMM", "MONTH");
      new_mask = new_mask.replaceAll("MMM", "MON");
      return new_mask;
   }

   private String correctSingleCharFormat(char ch, boolean is24H)
   {
      switch (ch)
      {
         case 'y':
            return "yy";
         case 'm':
            return "mi";
         case 'M':
            return "MM";
         case 'd':
            return "dd";
         case 'D':
            return "DD";
         case 'h':
            return "hh";
         case 'H':
            return "HH";
         case 's':
            return "ss";
         case 'S':
            return "SS";
         case 'a':
            return is24H?"":"AM";
      }
      return ch+"";
   }

   /**
    * Sends the given file to the client browser by changing the MIME type
    * into a given MIME type.
    * @param filename physical path and the file name of the existing file.
    * @param content_type sending file's MIME type.
    * @param delete_after_send specifies whether to delete file after sending
    *
    */

   public void sendFile( String filename, String content_type, boolean delete_after_send )
   {
      try
      {
         if ( this.getAspResponseBuffered() ) this.clearResponse();

         byte[] data = readFile(filename);
         if (delete_after_send)
           this.removeTempFile(filename);

         writeContentToBrowser(data, content_type);

      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   public void sendTiffFile(String filename)
   {
      sendFile(filename,"image/tiff",false);
   }

    private byte[] readFile( String filename ) throws IOException
   {
      File file = new File(filename);
      int size = (int)file.length();
      byte[] bb = new byte[size];
      FileInputStream in = new FileInputStream(filename);
      in.read(bb);
      in.close();
      return bb;
   }

   boolean isApplicationSearch()
   {
      if ( DEBUG ) debug("isApplicationSearch(): "+is_application_search);
      return is_application_search;
   }

   String[][] getApplicationSearchFields()
   {
      return application_search_fields;
   }

   public void setQueryStringAtLogon()
   {
      if(!isLogonPage())
      {
         error(new FndException("FNDMGRSETLOGONQUERYNOTALLOWED: Method not allowed from &1 page.", getASPPage().getPageName()));
      }
      String qString = getQueryString().trim();
      if(!isEmpty(qString.trim()))
      {
         context.setGlobal("QueryStringAtLogon",qString.trim());
      }
   }

   private void getQueryStringAtLogon()
   {
      if(DEBUG) debug(this+" Saving query string to ASPManager variable");
      query_string_at_logon = context.findGlobal("QueryStringAtLogon");
      if(!isEmpty(query_string_at_logon))
      {
         if(query_string_at_logon.startsWith("&"))
            query_string_at_logon = query_string_at_logon.substring(1);
         scanQueryString();
      }
      context.removeGlobal("QueryStringAtLogon");

   }

   void setScrollPosition()
   {
      try
      {
         if (!asppage.isAnyBlockInDialog() ){
            String position = readValue("__LAST_POSITION");
            if(position !=null && position !=""){
               saved_position = Integer.parseInt(position);
               restore_position = Integer.parseInt(position);
            }
         }
      }
      catch ( Throwable any ) {
         error(any);
      }
   }

   public boolean isRequestbyGET()
   {
      return get_request && isExplorer();
   }

   //===================================================================================
   // Rich Web Client Interface Section
   //===================================================================================

   /**
    * Creates and returns a string containing a given number of tab spaces.
    * @param count
    * @return String
    */
   private String insertTabs(int count)
   {
      AutoString temp = new AutoString();
      for(int i=1; i<count; i++)
      {
         temp.append("\t");
      }
      return temp.toString();
   }

   /**
    * Starts a Rich Web Client Interface item. This method always creates a level 0 item.
    * @param name
    */
   public void startRWCInterfaceItem(String name) throws FndException
   {
      startRWCInterfaceItem(name,0);
   }

   /**
    * Starts a Rich Web Client Interface item at the specified level.
    * @param name
    * @param level
    */
   public void startRWCInterfaceItem(String name, int level) throws FndException
   {
      if(!Str.isEmpty(name))
      {
         rwc_interface.append(insertTabs(level));
         rwc_interface.append("\t<"+name+">\n");
      }
      else
         throw new FndException();
   }

   /**
    * Ends the Rich Web Client Interface item at level 0.
    * @param name
    */
   public void endRWCInterfaceItem(String name) throws FndException
   {
      endRWCInterfaceItem(name,0);
   }

   /**
    * Ends the Rich Web Client Interface item at the specified level.
    * @param name
    * @param level
    */
   public void endRWCInterfaceItem(String name, int level) throws FndException
   {
      if(!Str.isEmpty(name))
      {
         rwc_interface.append(insertTabs(level));
         if(name.startsWith("/"))
            rwc_interface.append("\t<"+name+">\n");
         else
            rwc_interface.append("\t</"+name+">\n");
      }
      else
         throw new FndException();
   }

   /**
    * Adds a Rich Web Client Interface value. Default level is 0
    * @param name
    * @param value
    */
   public void addRWCInterfaceValue(String name, String value)
   {
      addRWCInterfaceValue(name, value, 0);
   }

   /**
    * Adds a Rich Web Client Interface value at a given level.
    * @param name
    * @param value
    * @param level
    */
   public void addRWCInterfaceValue(String name, String value, int level)
   {
      if(!Str.isEmpty(name))
      {
         rwc_interface.append(insertTabs(level));
         rwc_interface.append("\t\t<"+name+">", value, "</"+name+">\n");
      }
   }

   /**
    * Adds a user defined string value to the Rich Web Client Interface.
    * @param str
    */
   public void addToRWCInterface(String str)
   {
      rwc_interface.append(str);
   }

   /**
    * Adds a user defined JavaScript code to the Rich Web Client Interface.
    * @param js Javascript code to be appended.
    */
   public void addJSToRWCInterface(String js)
   {
      js_rwc_interface.append(js);
   }

   /**
    * Returns the Rich Web Client Interface as a String.
    * @return String
    */
   private String getRWCInterface()
   {
      AutoString api = new AutoString();
      api.append("\n\n<!-- This section describes the Rich web client and webclient shared interface values -->\n\n");
      api.append("<span id=\"__RWC_Interface\" name=\"__RWC_Interface\" style=\"visibility:hidden\">\n");
      api.append("<!-- Do not remove this comment\n");
      api.append(rwc_interface);
      api.append("-->\n");
      api.append("<script language=\"JavaScript\">\n");
      api.append(js_rwc_interface);
      api.append("</script>\n");
      api.append("</span>\n\n");
      return api.toString();
   }

   // ========================================================================
   // Methods introduced for Activity Programin Model
   // ========================================================================
   public static String printAbstractArray(FndAbstractArray array, int indent)
   {
      AutoString out = new AutoString();
      if (array!= null)
      {
         int size = array.size();
         for (int i=0; i<size; i++)
         {
            FndAbstractRecord rec = FndAttributeInternals.internalGet(array, i);
            out.append(rec.getName(), " , "+ rec.getClass().getName() ,", state="+rec.getState()+"\n");
            out.append(printAbstractRecord(rec,indent+1));
         }
      }
      return out.toString();
   }

   public static String printAbstractRecord(FndAbstractRecord rec, int indent)
   {
      AutoString out = new AutoString();
      for (int j = 0; j < rec.getAttributeCount(); j++)
      {
         FndAttribute attr = rec.getAttribute(j);
         //out.append("Attribute " + attr.getName() + " type : " + attr.getType().toString());
         if (attr instanceof FndCompoundItem)
         {
         for (int k = 0; k < indent; k++) out.append("   ");
            out.append("FndCompoundItem:: " + attr.getName()+", " + attr.getClass().getName()+"\n");
            out.append(printAbstractRecord(((FndCompoundItem)attr).getRecord(), indent + 1));
         }
         if (attr instanceof FndAggregate)
         {
            for (int k = 0; k < indent; k++) out.append("   ");
            FndAbstractRecord aggRec = ((FndAggregate)attr).getRecord();
            out.append("FndAggregate:: " + attr.getName(), null, attr.getClass().getName()+"\n");
            out.append(printAbstractRecord(aggRec, indent + 1));
         }
         else if (!attr.isVector())
         {
            String spaces = "";
            for (int k = 0; k < indent; k++) spaces+="   ";
            out.append(spaces + "  " + attr.getName()+ " = " + (attr.isNull() ? "<null>" : attr.toString())
                  + (attr.isDirty() ? " dirty-" : " -") + " type (" + attr.getType().getName() +")\n" );
         }
      }

      /* aggregate arrays */
      for (int j = 0; j < rec.getAttributeCount(); j++)
      {
         FndAttribute attr = rec.getAttribute(j);
         if (attr.isVector())
         {
            FndAbstractArray array = (FndAbstractArray)attr;
            for (int k = 0; k < indent; k++) out.append("   ");

            out.append("  Vector::" + array.getName(), null, array.getClass().getName() + (array.size() == 0 ? "<null>" : "")+"\n");

            for (int l = 0; l < array.size(); l++)
               out.append(printAbstractRecord(FndAttributeInternals.internalGet(array, l), indent + 1));
         }
      }
      return out.toString();
   }


   private String populateItemRecords(ASPCommandBar bar)
   {
      ASPBlock master = bar.getBlock();
      ((FndDataSet)master.getASPRowSet()).writeInfo();
      ASPPage container = bar.getASPPage();
      FndDataAdapter adapter = master.getDataAdapter();
      if (adapter == null)
      {
         error(new FndException("FNDASPMANAGERNOADAPTERISSET: No Data Adapter is set"));
         return "while(false){}";
      }
      else
      {
         if ( adapter instanceof FndPopulateDataAdapter)
         {
            FndAbstractRecord query_rec = getQueryRecord(adapter,master);
            FndAbstractRecord rec_result = ((FndPopulateDataAdapter)adapter).populate(query_rec);
            Vector blocks = container.getASPBlocks();
            for (int i=0; i<blocks.size(); i++)
            {
               ASPBlock detail = (ASPBlock)blocks.get(i);
               if (detail.getMasterBlock()==null) continue;
               if (master.getName().equals(detail.getMasterBlock().getName()))
               {
                   Object array = rec_result.getAttribute(detail.getMasterReference());
                   if (array != null)
                     getASPContext().addFndAbstractArray((FndAbstractArray)array,detail.getName());
               }
            }
            try {
               container.refresh();
            } catch (FndException ex) {
               ex.printStackTrace();
            }
            bar.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
            savePos(false);
         }
      }
      return "while(false){}";
   }

   public void getConditions(FndAbstractRecord aggregate, String aggregate_name, ASPBlock block)
   {
      if(!(block.getASPPage() instanceof FndWebFeature))
         error(new FndException("Can not be called from FndAbstractFetures."));
      boolean addthis = false;
      try {
         ASPField[] field = block.getFields();
         for(int i=0; i<field.length; i++){
            if(isEmpty(aggregate_name))
               if(isEmpty(field[i].getAggregateReference()))
                  addthis = true;
               else
                  addthis = false;
            else if(field[i].getAggregateReference().equals(aggregate_name))
               addthis = true;

            if(addthis)
            {
               FndCondition cond = ActivityUtils.addAttributeCondition(field[i], readValue(field[i].getName()), readValue("__CASESS_VALUE","N").equalsIgnoreCase("Y")?true:false);
               if(cond!=null)
                  aggregate.addCondition(cond);
            }
            addthis = false;
         }
      } catch (FndException ex) {
         error(ex);
      }
   }

   private FndAbstractRecord getSaveRecord(FndDataAdapter adapter, ASPBlock block, int rowno)
   {
      ASPField fields[] = block.getFields();
      FndAbstractRecord record = adapter.getTemplate();
      try
      {
         // Add search condition according to the primary key fields.
         FndAttribute.Iterator itr = record.getPrimaryKey().iterator();
         while(itr.hasNext()){
            FndAttribute key = itr.next();
            for(int f=0; f<fields.length; f++){
               if(fields[f].getDbName().equals(key.getName()))
               {
                  record.addCondition(ActivityUtils.addAttributeCondition(fields[f],block.getASPRowSet().getClientValueAt(rowno, fields[f].getName()),true));
                  break;
               }
            }
         }

         if(adapter instanceof FndQueryDataAdapter){
            FndAbstractArray array = ((FndQueryDataAdapter)adapter).query(new FndQueryRecord(record), getASPPage());;
            if(array!=null && array.getLength()>0)
            {
               record = FndAttributeInternals.internalGet(array,0);
            }
         }
         else
            throw new FndException("FNDDATASETNOQUERYADAPTERERR: No FndQueryDataAdapter implemented.");
      }
      catch( Throwable any )
      {
         error(any);
      }

      //===============================

      HashMap details = new HashMap();
      FndAttribute.Iterator itr = record.details();

      while(itr.hasNext())
      {
         FndCompoundAttribute detail = (FndCompoundAttribute)itr.next();
         if (!detail.isVector())
         {
            FndAbstractAggregate detail_rec = (FndAbstractAggregate)detail;
            FndAbstractRecord aggr_rec = FndAttributeInternals.internalGetRecord(detail_rec);
            aggr_rec.excludeQueryResults();
            details.put(detail.getName(), aggr_rec);
         }
      }

      for(int f=0; f<fields.length; f++){
         String value = block.getASPRowSet().getClientValueAt(rowno, fields[f].getName());
         FndAttribute attr = null;
         if(fields[f].hasAggregateReference())
            attr = ((FndAbstractRecord)details.get(fields[f].getAggregateReference())).getAttribute(fields[f].getTemplate());
         else
            attr = record.getAttribute(fields[f].getTemplate());
         if(!isEmpty(value))
         {
            attr.include();
            try {
               ActivityUtils.setAttributeValue(attr,fields[f],value);
            } catch (FndException ex) {
               ex.printStackTrace();
               error(ex);
            }
         }
      }
      return record;
   }

   private FndAbstractRecord getQueryRecord(FndDataAdapter adapter, ASPBlock block)
   {
      FndAbstractRecord curr_rec = ((FndDataSet)block.getASPRowSet()).getCurrentRecord();
      FndAbstractRecord record = adapter.getTemplate().newInstance();
      FndCompoundReference pkeys = record.getPrimaryKey();
      record.excludeQueryResults();

      int count = pkeys.getOwnAttributeCount();
      for (int i = 0; i < count; i++)
      {
         FndAttribute attr = pkeys.getOwnAttribute(i);
         Object value = FndAttributeInternals.internalGetValue(curr_rec.getAttribute(attr.getName()));
         FndAttributeInternals.internalSetValue(attr,value);
         attr.include();
      }

      Vector blocks = block.getASPPage().getASPBlocks();
      for (int i=0; i<blocks.size(); i++)
      {
         ASPBlock detail = (ASPBlock)blocks.get(i);
         if (detail.getMasterBlock()==null) continue;
         if (block.getName().equals(detail.getMasterBlock().getName()))
         {
            record.getAttribute(detail.getMasterReference()).include();
         }
      }

      return record;
   }

   /**
    * Reserve JAP connection to the current thread. All general activity calls will go
    * through this connection.
    */
   private void reserveJAPConnection()
   {
      if (isLogonPage()) return;
      try {
         common_jap_connection = ConnectionPool.get(ConnectionPool.COMMON_JAP_POOL,getASPConfig(),null);
         ifs.fnd.ap.Server jap_srv = ((ifs.fnd.ap.Server)common_jap_connection.getConnectionProvider());
         jap_srv.saveToThread();
         jap_srv.setRunAs(getUserId().toUpperCase());
      } catch (FndException ex) {
         ex.printStackTrace();
      }

   }

   //=================================================

   /**
    * @return true if RWC links are shown inside standard browsers, false otherwise.
    */
   boolean showRWCLinksInBrowser()
   {
      if(!isAuroraFeaturesEnabled()) return false;
      boolean showLinks=false;
      if(isPortalPage())
         showLinks = Boolean.parseBoolean(getPortalPage().readGlobalProfileValue("Defaults/RWCIntegration"+ProfileUtils.ENTRY_SEP+"ShowLinks","FALSE",false));
      else
         showLinks = Boolean.parseBoolean(getASPPage().readGlobalProfileValue("Defaults/RWCIntegration"+ProfileUtils.ENTRY_SEP+"ShowLinks","FALSE",false));
      return showLinks;
   }

   /**
    * @return true if RWC navigator items are shown inside standard browsers, false otherwise.
    */
   boolean showRWCNavigatorItems()
   {
      if(!isAuroraFeaturesEnabled()) return false;
      boolean showNavItems=false;
      if(isPortalPage())
         showNavItems = Boolean.parseBoolean(getPortalPage().readGlobalProfileValue("Defaults/RWCIntegration"+ProfileUtils.ENTRY_SEP+"ShowNavigatorItems","FALSE"));
      else
         showNavItems = Boolean.parseBoolean(getASPPage().readGlobalProfileValue("Defaults/RWCIntegration"+ProfileUtils.ENTRY_SEP+"ShowNavigatorItems","FALSE"));
      return showNavItems;
   }

   void autoPopulate(ASPBlock block)
   {
      if(getASPPage() instanceof FndWebFeature)
      {
         try {
            getCommandBarFunction(block.getASPCommandBar(), block.getName(), ASPCommandBar.OKFIND);
         } catch (Exception ex) {
            error(ex);
         }
      }
      else
         error(new FndException("Can not be called from ASPPageProvider."));
   }

   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================

   private boolean checkBrowser()
   {
      if(browser_checked) return mobile_version;
      
      String uAgent = getBrowserName().toUpperCase();
      String ids = getConfigParameter("ADMIN/MOBILE_BROWSER_SETTINGS/USER_AGENTS/ACCEPTED","PPC");
      String non_ids = getConfigParameter("ADMIN/MOBILE_BROWSER_SETTINGS/USER_AGENTS/NOT_ACCEPTED","MAC OS");
      StringTokenizer id_tokens = new StringTokenizer(ids.toUpperCase(),"^");
      StringTokenizer non_id_tokens = new StringTokenizer(non_ids.toUpperCase(),"^");
      
      while(id_tokens.hasMoreTokens()){
         if(uAgent.indexOf(id_tokens.nextToken())!=-1 || asppage instanceof MobilePageProvider)
         {
            mobile_version = true;
            break;
         }
      }
      
      while(non_id_tokens.hasMoreTokens()){
         if(uAgent.indexOf(non_id_tokens.nextToken())!=-1)
         {
            mobile_version = false;
            break;
         }
      }
      
      browser_checked = true;
      return mobile_version;
   }
   
   /**
    * Returns a boolean value depending on the type of browser in use.
    * @return boolean Mobile browser or not.
    */
   public boolean isMobileVersion()
   {
      return checkBrowser();
   }

   private String generateMobileHeadTag(String title)// throws FndException
   {
      AutoString temp = new AutoString();

      temp.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
      temp.append(getStyleSheetTag());
      temp.append("\n<title>",translate(title),"</title>\n",genMobileHTag(true));
      return temp.toString();
   }

   private String genMobileHTag(boolean subcalled)// throws FndException
   {
      if ( asppage.getVersion() >=3 && !subcalled )
         error(new FndException("FELHEAD3TAG: In webkit 3 you have to use generateHeadTag(String Title)"));

      head_tag_generated = true;
      ASPConfig cfg = getASPConfig();

     return asppage.getMobileHeadTag();

   }

   private String generateMobileBodyTag()
   {
      try
      {
         AutoString tag = new AutoString();
         tag.append(getASPConfig().getMobileBodyTag());
         return tag.toString();
      }
      catch ( Throwable e ) {
         error(e); return "";
      }
   }

   private String startMobilePresentation(String title)
   {
      if ( DEBUG ) debug(this+": ASPManager.startMobilePresentation("+title+")");

      AutoString bar = new AutoString();
      ASPConfig cfg = asppage.getASPConfig();
      toolbar_title = translate(title);

      asppage.resetBlockCount();
      toolbar_title = (toolbar_title==null)?"":toolbar_title;
      String img_loc = cfg.getMobileImageLocation();
      bar.append("\n");

      if ( !asppage.isHeaderDisabled() )
      {
         ASPPortal portal = asppage.getASPPortal();
         String rname = getRealUserName();
         bar.append("<table class=\"pageHeaderTable\" cellspacing=\"0\" cellpadding=\"0\">\n");
         bar.append("   <tr>\n");
         bar.append("      <td class=\"pageHeaderLeft\" nowrap valign=\"middle\">");
         bar.append("<font class=\"pageHeaderLeftText\" >");
         appendMobileLeftHeader(bar, asppage);
         bar.append("</font></td>\n");

         bar.append("      <td class=\"pageHeaderCentral\" valign=\"center\">");
         bar.append("<font class=\"pageHeaderCentralText"+((toolbar_title.length()>50)?" pageHeaderCentralLongText":"")+"\">");
         bar.append(toolbar_title+(isDefaultLayout()?"&nbsp;["+translate("FNDMANAGERDEFAULTMODEMSG: Default Layout mode")+"]":""));
         bar.append("</font></td>\n");

         bar.append("      <td class=\"pageHeaderRight\" align=\"right\" nowrap  valign=\"middle\">");
         if ( !asppage.isBarDisabled() && !isDefaultLayout())
            appendMobileRightHeader(bar, false);
         bar.append("</td>\n");

         bar.append("   </tr>\n");
         bar.append("</table>\n");
      }
      else //header is disabled
      {
         bar.append("<table  border=0 width=100% cellpadding=\"0\" cellspacing=\"0\">\n");
         bar.append("   <tr>\n");
         bar.append("      <td align=\"right\">");
         if (!asppage.isBarDisabled())
             appendMobileRightHeader(bar, false);
         bar.append("</td>\n");
         bar.append("   </tr>\n");
         bar.append("</table>\n");
      }
      return bar.toString();
   }

   private void appendMobileLeftHeader(AutoString str, ASPPage page)
   {
      String signout = "";
      
      if(getASPConfig().isFormBasedAuth() && !asppage.isSignoutLinkDisabled())
         signout = "&nbsp;<a href=\"javascript:removeClusterCookie();document.location='"+( getASPConfig().getProtocol()+"://"+getASPConfig().getApplicationDomain() + correctURL(getASPConfig().getLogonURL()+"?LOGOUT=YES") )+"'\">"
                   + "<img border=\"0\" src=\""+asppage.getASPConfig().getMobileImageLocation() + "signout.png\">"
                   + "</a>";
      str.append(signout);

   }

   private void appendMobileRightHeader( AutoString bar, boolean header_disabled)
   {
      if ( DEBUG ) debug(this+": ASPManager.appendMobileRightHeader()");

      ASPConfig cfg = asppage.getASPConfig();
      String[] images = cfg.getMobileToolBarImages();
      AutoString png_correction = new AutoString();

      /**
       * getMobileToolBarImages() returns an array with following image names.
          * 0 - Navigator
          * 1 - Help
       */

      //String imgloc = getTranslatedImageLocation();
      String img_loc = cfg.getMobileImageLocation();
      ASPPortal portal = asppage.getASPPortal();
      Buffer buf = asppage.getPortalViews();

      asppage.disableHomeIcon();
      asppage.disableOptions();

      if(!cfg.isNavigateEnabled())
         asppage.disableNavigate();
      if(!cfg.isHelpEnabled())
         asppage.disableHelp();

      asppage.disableConfiguration();

      if ( asppage.getASPLov() == null)
      {
          if (context.isUserLoggedOnFND())
          {
             String image;
             String tr_text = "";
             boolean group1=false, group2=false;

             //Navigate Button
             if ( !asppage.isNavigateDisabled() )
             {

                bar.append("<a href=\"",getApplicationAbsolutePath(),"/webmobile/",correctURL(getConfigParameter("APPLICATION/LOCATION/MOBILE_NAVIGATOR","Navigator.page?MAINMENU=MOBILE")),"\"");
                image = images[0];
                tr_text = translateJavaText("FNDMGRPORTALNAVIGATOR: Navigate");

                bar.append(">");
                bar.append("<img");
                bar.append(" name=\"toolnav\" id=\"__hd_navigate_png\"");
                bar.append(" src=\""+img_loc+image+"\"");
                bar.append(" alt=\"",tr_text,"\"");
                bar.append(" title=\"",tr_text,"\"");
                bar.append(" border=\"0\"></a>&nbsp;");

             }

             //Help Button
             if ( !asppage.isHelpDisabled() )
             {
                tr_text = translateJavaText("FNDMGRPORTALHELP2: Help");

//                bar.append("<a href=\"",asppage.getASPPopup("help").generateCall(),"\" ");
                bar.append("<a href=\"javascript:showFieldHelp('" + getURL() + "','"+getUsageKey()+"')\" ");
                bar.append(">");
                bar.append("<img");
                bar.append(" name=toolhelp id=\"__hd_help_png\"");
                bar.append(" src=\""+img_loc+images[1]+"\"");
                bar.append(" alt=\"",tr_text,"\"");
                bar.append(" title=\"",tr_text,"\"");
                bar.append(" border=\"0\"></a>&nbsp;");
             }
          }
      }
   }

   public String endMobilePresentation()
   {
      AutoString bar = new AutoString();
      ASPConfig cfg = asppage.getASPConfig();

      bar.append(generateMobileClientScript());

      return bar.toString();
   }

   private String getMobileScriptFileTag()
   {
      String tag = getASPConfig().getGenericClientScript();
      if(!isLogonPage())
         tag = ASPPage.SRC_SCRIPT_TAG_LEFT + getASPConfig().getScriptsLocation()+"mobile/mobileclientscript.js" + ASPPage.SRC_SCRIPT_TAG_RIGHT;      
      return tag;
   }

   public String generateMobileClientScript()
   {
      try
      {
         repeat_finc_call++;
         if ( repeat_finc_call>1 )
            log("ASPManager: Function generateMobileClientScript() called "+repeat_finc_call+" times.");

         return asppage.generateMobileClientScript();
      }
      catch ( Throwable any )
      {
         error(any);
         return null;
      }
   }

   String endMobilePresentationSimple()
   {
      AutoString bar = new AutoString();

      bar.append(generateMobileClientScript());

      return bar.toString();
   }

   /**
    * Finish the execution of the current request and send the response
    * to the mobile client.
    */
   public void endMobileResponse()
   {
      addCookies();
      if(DEBUG) debug("ASPManager.endMobileResponse()");
      try
      {
         HttpServletResponse resp = getAspResponse();
         end_response = false;
         boolean did_zip=false;
         if(asppage instanceof MobilePageProvider)
         {
             boolean is_validate = !isEmpty(getQueryStringValue("VALIDATE"));

             //getAspRequest().getRemoteAddr() doesn't seem work with JSR 168
             if(!binary_output && canGZIP( (((MobilePageProvider)asppage).getOutputStream().toString()).length() ,getAspRequest().getRemoteAddr()))
             {
                 responseGZIP( ((MobilePageProvider)asppage).getOutputStream().toString() );
                 did_zip=true;
             }
             else if(!binary_output)
             {
                if(( ASPPage.IFRAME_VALIDATION.equals(asppage.getValidationMethod()) ) && (isExplorer()) && (is_validate))
                {
                   //Bug 41996, start
                   String endiftext = "--></textarea>\n</form>\n";
                   String endtags = "</BODY></HTML>";
                   resp.getWriter().write(getIframeScript() + ((MobilePageProvider)asppage).getOutputStream().toString()+endiftext+endtags);
                   //Bug 41996, end
                }
                else
                {
                   if (is_validate) resp.setHeader("Content-Type", "application/octet-stream");
                   resp.getWriter().write(((MobilePageProvider)asppage).getOutputStream().toString());
                }

            }
            if(DEBUG) debug(this+": Aborting execution by throwing ResponseEndException ...");
            if(!did_zip)
                resp.flushBuffer();//end();
            throw new ResponseEndException();
         }
         resp.flushBuffer();//end();
      }
      catch( ResponseEndException x )
      {
         throw x;
      }
      catch( Throwable any )
      {
         error(new AspContextException(any));
      }
   }

   /**
    * Return whether Aurora features are enabled or not
    */   
   public boolean isAuroraFeaturesEnabled()
   {
      return getASPConfig().getConfigFile().aurora_features;
   }

   /**
    * Return number of tiff viewer instances in page
    */
   public int getTiffViewerCount(boolean increment)
   {
      if (increment) 
         return ++tiff_viewer_counter;
      else
         return tiff_viewer_counter;
   }

   /**
   * Returns whether a date value with SYSDATE is valid
   **/
   public static boolean isValidSysdateValue(String value)
   {
      String date_exp = "^\\s*SYSDATE\\s*([+|-]\\s*[0-9]+){0,1}\\s*$";
      Pattern pattern = Pattern.compile(date_exp, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(value);
      return matcher.find();
   }
   
   // Added by Terry 20130902
   // Check view, can connect with doc
   public boolean isDocConnectionAware(String view_name)
   {
      try
      {
         if (isEmpty(view_name))
            return false;

         HttpSession session = getAspSession();
         Hashtable conn_aware_table = (Hashtable)session.getAttribute(DOC_CONNECTION_AWARE);
         
         if(conn_aware_table == null)
            conn_aware_table = new Hashtable();

         if (conn_aware_table.containsKey(view_name))
         {
            if (!"TRUE".equals(conn_aware_table.get(view_name)))
               return false;
            else
               return true;
         }
         else //new view check
         {
            ASPCommand cmd  = (new ASPCommand(this)).construct();
            cmd.defineCustomFunction("Object_Connection_SYS.Is_Connection_Aware_View");
            cmd.addParameter("IS_AWARE",  "S", null, null);
            cmd.addParameter("VIEW_NAME", "S", "IN", view_name);
            cmd.addParameter("SERVICE",   "S", "IN", "DocReferenceObject");

            ASPTransactionBuffer trans = newASPTransactionBuffer();
            trans.addCommand("IS_CONN_AWARE", cmd);
            
            if(asppage.isUndefined())
               trans = performConfig(trans);
            else
               trans = perform(trans);
            
            String is_conn_aware = trans.getValue("IS_CONN_AWARE/DATA/IS_AWARE");
            conn_aware_table.put(view_name, is_conn_aware);
            session.setAttribute(DOC_CONNECTION_AWARE, conn_aware_table);
            
            if ("TRUE".equals(is_conn_aware))
               return true;
            else
               return false;
         }
      }
      catch(Throwable any)
      {
         error(any);
         return false;
      }
   }
   // Added end
   
   // Added by Terry 20130923
   // Get logical unit with view
   public String getLUName(String view)
   {
      ASPTransactionBuffer trans = newASPTransactionBuffer();
      
      ASPCommand cmd  = (new ASPCommand(this)).construct();
      cmd.defineCustomFunction("Dictionary_SYS.Get_Logical_Unit");
      cmd.addParameter("LU_NAME", "S", null, null);
      cmd.addParameter("VIEW",    "S", "IN", view);
      cmd.addParameter("TYPE",    "S", "IN", "VIEW");

      trans.addCommand("GETLUNAME", cmd);
      
      if(asppage.isUndefined())
         trans = performConfig(trans);
      else
         trans = perform(trans);
      
      String lu_name = trans.getValue("GETLUNAME/DATA/LU_NAME");
      
      return Str.isEmpty(trans.getValue("GETLUNAME/DATA/LU_NAME")) ? "" : trans.getValue("GETLUNAME/DATA/LU_NAME");
   }
   // Added end
}
