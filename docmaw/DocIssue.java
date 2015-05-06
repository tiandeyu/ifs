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
*  File        : DocIssue.java
*  Converted   : Bakalk using   ASP2JAVA Tool  2001-03-08
*  Created     : Using the ASP file DocIssue.asp
*
*  09/04/2001  : DIKALK - Call ID 63961 Fixed errors when selecting menu options
*  09/04/2001  : DIKALK - Call ID 63959 Fixed errors when saving new objects in Objects tab
*  26/04/2001  : Bakalk -               Change server function set to the field TECHOBJ.
*  27/04/2001  : Bakalk - Call ID 64606 Made "Delete Document" available only when "state" in Obselete.
*  30/04/2001  : Bakalk - Call Id 64641 Adjusted "objectDetails".
*  02/05/2001  : Shdilk - Call Id 64642 added a hyper link to Previous Level when the number of records are zero.
*  04/05/2001  : Bakalk - Call Id 64687 Directory name turned into lower case.
*  17/05/2001  : Bakalk - Changed tranferToEdmMacro to transferToEdmMacro (spelling mistake) and EdmMacro.asp to EdmMacro.page.
*  22/05/2001  : Shdilk - Call Id 64971 : Added new RMB, View Document with External Viewer
*  23/05/2001  : Dikalk - Call Id 64971 : Checked up(down)load RMBs + added new RMB options
*  25/05/2001  : Bakalk - Call Id 65323 : Made possible "check in document" when multi_row layout.
*  29/05/2001  : Shdilk - Call Id 65480 : Added new context variable 'savetype'
*  29/05/2001  : Shdilk - Call Id 65530 : Changed some labels
*  29/05/2001  : ThAblk - Added Command Groups
*  29/05/2001  : BAKALK - Call Id 65174  Adjust method "releseDocument" and
*                                        checked whether given document has any objects connected.
*  30/05/2001  : Shdilk - Call Id 65659
*  30/05/2001  : Bakalk - Call Id 65677  Checked whether file is checked out in undoCheckOut.
*  31/05/2001  : Shdilk - Call Id 65783
*  04/06/2001  : Shdilk - Call Id 65742
*  06/06/2001  : Bakalk - Call Id 65827  : Adjusted "refreshCurrentRow".
*  11/06/2001  : Bakalk - Call Id 66223  : modified setToCopyFile.
*  27/06/2001  : ThAblk                  Changed process_db parameter in tranferToEdmMacro in deleteDocumentFile to ORIGINAL.
*  28/06/2001  : Shdilk - Call Id 66637
*  02/07/2001  : Bakalk -                Removed "Action" command from NONE lay out.
*  03/07/2001  : Bakalk - Call Id 66764  Adjustment in "adjust" so that query items come in multy_layout if there are
*                                        more than one row,otherwise in single_layout.
*  04/07/2001  : Bakalk - Call Id 66764  Adjustment in okFind.
*  01/08/2001  : Bakalk - Call Id 67226  Added Lovs for 'Created by', 'Modified by', 'Last Checked Out by' and 'Last Checked in by'
*  02/08/2001  : Dikalk - Call Id 67176  Added select boxes menus for 'Status', 'Access' amd 'Comment File Status'
*  10/08/2001  : Bakalk                  Added select boxes menus for 'File Status'
*  16/08/2001  : Shtolk                  Removed select boxes menus for 'File Status'and'Comment File Status'.
*  24/08/2001  : Bakalk                  Added a new method "makeQuery" to make  queries on headlay.
*                                        Added select boxes menus for 'Status', 'Access' amd 'Comment File Status' again.
*                                        Changed the data field lable "Medium" to "Media";
*  17/09/2001  : Bakalk - Call Id 69150  Adjusted "setAsDocFileTemp" (action allow only for those have edit access).
*  18/09/2001  : Bakalk - Call Id 69151  commandbar action "edit" made visible conditionally
*                                       (it is visible only for those have edit acces,except in object tab, where it is visble for users with view acces.
*  21/09/2001  : Bakalk - Call Id 66764  Remove the functionality "multirow layout for more than one row always".
*  18/03/2002  : Shtolk - Bug Id# 27765  Modified releseDocument for release document work properly when view copy is required.
*  03/05/2002  : Larelk - Bug Id  28923  Modified OkFindItem5().
*  03/05/2002  : Larelk - Bug Id  28932  Remove some cordings in runQuery().
*  09/05/2002  : Larelk - Bug Id 29042   Add validation to Doc_status to generate % automatically.
*  20/05/2002  : Shthlk - Bug Id 30018   Modified editComment(), added function editCommentFile() and
*                                        added an error message when the redline Viewer application is not set.
*  13/06/2002  : Larelk - Bug Id 29779   modified editComment(), and editDocument()
*  25/06/2002  : Shthlk - Bug Id 30742   Since the field "DOCTITLE" cannot be updated because it has a setfunction, modified the field to be read only.
*  26/06/2002  : PrSalk - Bug Id 31163   Modified okFind()
*  29/06/2002  : Shthlk - Bug Id 30923   Modified okFindITEM2()
*  29/07/2002  : Larelk - Bug Id 31535   Modified editDocument()
*  12/11/2002  : Dikalk - Added new field 'Doc_Sheet' to all relevant ASP blocks and methods
*  14/11/2002  : Dikalk - Added a new group box called 'Sheets' and new 'Description' fields
*  18/11/2002  : Dikalk - Modified field properties in headblk to synchronize multirow layout with that of DocIssueOvw.page
*  19/11/2002  : Dikalk - Added the multirow "custom" menu bar found in DocIssueOvw page
*  21/11/2002  : Dikalk - Started coding for the popup menus to perform multirow actions
*  28/11/2002  : Prsalk - Added RMB "Create New Sheet" and newSheet method.
*  28/11/2002  : Dikalk - Added some code to implement multi-row file operations
*  18/12/2002  : Nisilk - Call Id 92205 fixed
*  20/12/2002  : Nisilk - Call Id 92263 Corrected the menu label "Create New Sheet"
*                         in overview Document Issues General tab
*  24/12/2002  : inoslk - Call ID 92288
*  26/12/2002  : inoslk - Call ID 92202. Modified method okFind to return if rowcount is zero.
*  30/12/2002  : inoslk - Call ID 92286 - Set client_confirmation to fasle after db submits.
*  02/01/2003  : Dikalk - 2002-2 SP3 Merge: Bug Id 32626 - Made a check on whether the Comment file is checked out before launching Edm Macro window.
*                                           Change the message that is displayed when trying to edit the comment file with the original checked out.
*  02/01/2003  : Dikalk - 2002-2 SP3 Merge: Bug Id 31585 - Changed the Field Description from "Decription" to "Description" in tempblk.
*  02/01/2003  : Dikalk - 2002-2 SP3 Merge: Bug Id 30724 - Redirected to EdmMacro withot launching file when trying "Edti document" on a
                                            document which is already checked out.
*  02/01/2003  : Dikalk - 2002-2 SP3 Merge: Bug Id 33420   Replace the function substr(EQUIPMENT_OBJECT_API.Has_Technical_Spec_No(LU_NAME,KEY_REF), 1, 5)
*                                           with SIGN(TECHNICAL_OBJECT_REFERENCE_API.Get_Technical_Spec_No(LU_NAME,KEY_REF)+1) in makeQuery().
*  02/01/2003  : Dikalk - 2002-2 SP3 Merge: Bug Id 33650 - Modified the method setApproved().
*  02/01/2003  : Dikalk - 2002-2 SP3 Merge: Bug Id 30697 - Incresed the size to 40 in object key
*  02/01/2003  : Dikalk - 2002-2 SP3 Merge: Bug Id 32613 - Corrected in releasedocument.
*  10/01/2003  : inoslk - Call ID 92254 - Modifications in Object Connection Tab.
*  10/01/2003  : inoslk - Call ID 92260 - Alert when no rows are selected in MultiRow Operations.
*  10/01/2003  : inoslk - Call ID 92252 - Changed the title of PROFILE_ID
*  13/01/2003  : Nisilk - Call ID 92204 - Highlight rows for All, None and Invert
*  14/01/2003  : inoslk - Call ID 92260 - Modified condition to return if no rows selected.
*  20/01/2003  : inoslk - Call ID 92882 - Removed Action 'Create New Sheet' from MultiRow Menu 'General'.
*  20/01/2003  : MDAHSE - Call ID 92205 - Call reopened - fixed q.setOrderByClause() on headblk.
*  21/01/2003  : Dikalk - Removed unused instance variables and methods (existsMember(), getValidations())
*  27/02/2003  : MDAHSE - Call ID 92202 - Cannot search in ALTDOCNO
*  27/02/2003  : MDAHSE - Call ID 92770 - Create New Sheet menu choice not translated.
*  28/01/2003  : BAKALK - Added client side function for release wizard over multiple rows.
*  28/01/2003  : inoslk - Added new Tab 'File References'.
*  01/01/2003  : DiKalk - Modified method transferToEdmMacro() to handle transfer of multiple rows
*  01/01/2003  : DiKalk - Changed the page title and page header to be "Overview - Document Revisions" when in multirow mode
*  05/02/2003  : Nisilk - Fixed call 92884
*  06/02/2003  : Dikalk - Modified method transferToEdmMacro()
*  14/02/2003  : Nisilk - Replaced DOC_CLASS_SCALE_LOV2 with DOC_CLASS_SCALE_LOV1 in Scale
*  26/02/2003  : Dikalk - Added method okFindITEM9()
*  27/02/2003  : Thwilk - Bug Id 35980 - Modified 'releseDocument' and 'setApproved' functions in a way to prevent
*                         the user proceeding to the'Approved' state  if no objects are beign connected
*                         to the document when the Object Connection flag is set to "Required" .
*  28/02/2003  : Thwilk - Bug Id 35985 - Fixed the problem of performing a case sensitive search, even after unchecking
*                         the "Case Sensitive" check box. Prevented the displaying of message "Row number
*                         '0' does not exist in the row set" and instead "No Data Found" was displayed.
*  02/03/2003  : Dikalk - Removed method CreateNewDocument. The functionality is now merged with RMB Edit Document
*  03/03/2003  : Prsalk - Fixed, Call ID 92279
*  03/03/2003  : Prsalk - Fixed, Call ID 93339. Changed makeQuery().
*  11/03/2003  : Dikalk - Removed loginUser() and renamed getLoginUser() to getFndUser()
*  12/03/2003  : Dikalk - Modified methods deleteDocument() and deleteDocumentFile() to handle multiple rows
*  13/03/2003  : Thwilk - Bug Id 36034 - Fixed the problem of not getting a message when an Approval Template is added from Document Info/Development Tab.
*                         Also refreshed the Routing and Access Tabs in order to reflect the changes to the user.
*  26/03/2003  : Dikalk - Modifed view copy and comment file methods
*  27/03/2003  : DhPelk - Changed scale view to Doc_Scale_Lov1
*  28/03/2003  : BaKalk - Implemented Copy File to.. and Send Mail.. for XEDM.
*  01/04/2003  : BaKalk - Added clone() and doReset() in order to support 3.5.1 web client.
*  01/04/2003  : Nisilk - Added trans.clear() statement in adjust()
*  02/04/2003  : Dikalk - Replaced calls to getOCXString() with DocmawUtil.getClientMgrObjectStr()
*  17/04/2003  : Dikalk - Removed call to super.define() in method run()
*  21/04/2003  : InoSlk - Added fileds CREATED_BY_IN_BC and REVISION_SIGN_IN_BC.
*  22/04/2003  : InoSlk - Added field BRIEFCASE_NO, Actions 'Go to Briefcase' & 'Add to Briefcase'.
*  23/04/2003  : InoSlk - Added functionality for action 'Add to Briefcase'.
*  24/04/2003  : InoSlk - Added multi row action 'Add to Briefcase'.
*  30/04/2003  : Prsalk - Call ID 95604 fixed. Changed adjust.
*  06/05/2003  : InoSlk - Corrections in Add to Briefcase. (Validate against Active States).
*  07/05/2003  : Bakalk - Implemented Add to Briefcase for Add Documents in DocBriefcase.page.
*  08/05/2003  : InoSlk - Set the translated Db Values to be retrieved from DocmanConstants.
*  22/05/2003  : DhPelk - Fixed call 95565, 97637
*  29/05/2003  : Dikalk - Fixed a bug while trying to edit documents
*  30/05/2003  : InoSlk - Added overridden JavaScript method 'closeLOVWindow' as a workaround
*                         for the Web Client Version conflict.
*  04/03/2003  : Thwilk - Bug Id 35992 - Moved the menu choice "Copy Approval Template" from Actions/Access menu and
*                         placed it directly under the Actions menu.
*  04/03/2003  : Shthlk - Bug Id 35991 - Added menu choice "Copy Access Template". Added two new methods copyAccessTem() and copyAccessMsg().
*  04/06/2003  : DhPelk - Fixed Call 97724
*  16/06/2003  : NiSilk - Added code to handle the CCA error which was generated in Web Client 3.5.1.
*                         modified javascript methods selectAll,deSelectAll and invertSelection.
*  18/06/2003  : Thwilk - Bug Id 38077   Fixed the problem of taking time to make a search for a particular revision
*                         in document info.(Excluded a part of Fix in Bug ID:31163)
*  18/06/2003  : Bakalk - Modified completeAddToBc().
*  18/06/2003  : InoSlk - Set Dynamic LOV for BRIEFCASE_NO as DOC_BC_LOV(Workaround for Scan Script problem).
*  20/06/2003  : DhPelk - Fixed Call 96357, 95767
*  23/06/2003  : DhPelk - Removed Created by in BC and Revision sign in BC fields
*  27/06/2003  : Thwilk - Bug Id 38077   Fixed the problem of taking time to make a search for a particular revision
*                         when the user switches from Overview mode to Detailed Mode.
*  11/07/2003  : NiSilk - Fixed the focus problem for object connections due to the web kit version conflict.
*                         added closeLOVWindow.
*  16/07/2003  : NiSilk - Added new field ENABLE_ADDTO_BC in predefine to take care of enabling/disabling of rmb Add To Briefcase.
*  17/07/2003  : Bakalk - Modified client method:validateKeepLastDocRev
*                         1.Corrected url for validation.
*                         2.Added try-chatch block in order to make it compatible with previous and current web client.
*                         3.Used db value for checking keepcode instead of client value.
*  24/07/2003  : NiSilk   added statement trans.clear() in validate method, to get rid of the page error,
*                         "the request buffer header is contaminated with response buffer header elements".
*  28/07/2003  : NiSilk   Removed command for Multi row create new revision.
*  28/07/2003  : InoSlk   Call ID 95635 - Modified method performRefreshParent().
*  29/07/2003  : InoSlk   Added method isInBriefcase() and modified method editDocument();
*  29/07/2003  : DhPelk   Fixed call 96357
*  29/07/2003  : InoSlk   Call ID 92934 - Removed unnecessary space from URL in objectDetails().
*  30/07/2003  : Bakalk   Call ID 95569 - Modified isValidColumnValue(): changed the compaison logic there.
*  30/07/2003  : Shtolk   Call ID 99835 - Removed the field INFO__ from the dummyblk because it conflicts with  INFO field when webclient validates.
*  31/07/2003  : NiSilk   Implemented multi row copy file to. Added methods transferToEdmMacro, setToCopyFile
*  01/08/2003  : NiSilk   Fixed call 95769, Modified method adjust().
*  04/08/2003  : NiSilk   Fixed call 95771, ReOrdered the field order in DocIssue.
*  06/08/2003  : Nisilk   2003-2 SP4 Merge: Modified method setApproved in order to merge Bug Id 35980
                          2003-2 SP4 Merge: Merged bug Id - 35985
*                         2003-2 SP4 Merge: Merged Bug Id - 35992
*                         2003-2 SP4 Merge: Merged Bug Id - 35991. Also modified method getContents() to display a hyper link
*                                          when no records are found, thereby allowing the user to copy the Access Template using the hyperlink.
*                         2003-2 SP4 Merge: Merged bug Id - 36034
*  12/08/2003  : NiSilk   Fixed call 100804, Modified methods adjust() and run() to avoid the user not allowing to go single layout after rmb Add Documents from BC page.
*  13/08/20003 : InoSlk   Call ID 100939 - Modified method dlgOk to refresh the Access Tab
*                         when an Approval Template is copied.
*  15/08/2003  : InoSlk   Call ID 100767: Modified methods getContents(), distributeDocuments(),
*                         releaseDocument(), userSettings() and createNewRevision().
*  15/08/2003  : InoSlk   Fixed a multi row selection error in setApproved();
*  15/08/2003  : Bakalk   Merged patched bug : 38257.
*  15/08/2003  : NiSilk   Merged Patched bug : 38077, Applied the fix to runQuery() method.
*  18/08/2003  : InoSlk   Call ID 100767: Added method addNewRev() and JavaScript method 'addNewRow' in getContents().
*  18/08/2003  : Shtolk   Call ID 92264, Modified method viewOriginal,editDocument, printDocument, checkInDocument, deleteDocumentFile and deleteDocument
*  20/08/2003  : Shtolk   to enable use of the radio buttons in the overview mode. And to enable 'Edit' , 'View' & 'Print' of multiple documents.
*  20/08/2003  : Dikalk   Modified method deleteDocument()
*  21/08/2003  : Bakalk   Set Lovs for Doc_No,DOC_SHEET, DOC_REV, FORMAT_SIZE AND ORIGINAL_FILE_TYPE
*                         available in Find layout too. Clinet methods thereof overriden.
*  22/08/2003  : Bakalk   Fixed the problem of showing wrong record after approving.
*  25/08/2003  : Dikalk   Fixed call 100960: Deleting documents and document files
*  27/08/2003  : Bakalk   Fixed call 101729: Modifiations in completeAddtoBc,Removed storeSelection in Single layout.
*  27/08/2003  : InoSlk   Call ID 101731: Modified doReset() and clone().
*  29/08/2003  : Bakalk   Call Id 101725: Added an error msg in CheckInDocument and EditDocument under the
*                         conditioin that document is not released from any BC.
*  01/09/2003  : NiSilk   Call Id 95532. Removed FILE_TYPE being passed to EdmMacro. Modified several methods.
*                         Removed method transferToEdmMacro/method calls, which was using file_type as a parameter.
*  03/09/2003  : NiSilk   Fixed call 102304.
*  04/09/2003  : Dikalk   Call 102318. Fixed bug in javascript methods selectAll() DeselectAll() and invert()
*  04/09/2003  : InoSlk   Call ID 102439: Modified saveReturnITEM() so that it saves the changes in the header.
*  05/09/2003  : Thwilk   Call ID 102524: Modified completeAddToBc() and addToBriefcase() methods to check whether the perosn has Edit Access to the doc.
*  08/09/2003  : NiSilk   Call ID 102830: Removed method makeQuery(). Modified methods countFind() and okFind().
*  08/09/2003  : Shtolk   Call ID 101380: Fixed the incompatibility with webclient 3.5.1 for the variable HEAD_IN_FIND_MODE.
*  09/09/2003  : NiSilk   Call ID 102637.
*  09/09/2003  : Shtolk   call Id 102913. Langauge translation.
*  09/09/2003  : Dikalk   Verified access rights to all operations in this page.
*  11/09/2003  : ShThlk   Call Id 102913. Modified and add new translation constant.
*  19/09/2003  : ThWilk   Call Id 103379 - Modified the method userSettings() to pass a QueryString.
*  26/09/2003  : NiSilk   Call Id 104019. Modified methods okFindITEM9 and activateFileReferences.
*  08/10/2003  : Bakalk   Call Id 106527: Now we preserve previous record set even after calling NewSheetWizard.
*  10/10/2003  : NiSilk   Call Id 106866: Removed mandatory option for the description field in Approval Routing.
*  10/10/2003  : NiSilk   Call Id 106557: Modified method releaseDocument.
*  13/10/2003  : NiSilk   Call Id 107284: Modified DeleteDocument. And fixed the problem of getting a system error when doing DeleteDocument.
*  14/10/2003  : NiSilk   Call Id 107457: Fixed. Added method hasEditAccess.
*  15/10/2003  : NiSilk   Call Id 106866: Set the Description field of itemblk5 mandatory.
*  18/10/2003  : InoSlk   Call ID 106768: Added new field CHECKED_OUT_USR & changed the function in SEDMSTATUS.
*  18/10/2003  : BaKalk   Call ID 108651: Modified predefine(),completeAddToBc() and addToBriefcase().
*  20/10/2003  : NiSilk   Call ID 106621: Fixed
*  21/10/2003  : ShTolk   Call Id 108702: Mdae fileds 'REDLINE_STATUS_DES' & 'FILE_STATUS_DES' hidden even in the findlayout mode.
*  21/10/2003  : DIKALK   Call Id 102207: Need Edit access to create new sheets.
*  21/10/2003  : INOSLK   Call ID 106615: Modified in preDefine().
*  23/10/2003  : INOSLK   Call ID 107385: Renamed RMB 'Delete Document' to 'Delete Document Revision'.
*  23/10/2003  : INOSLK   Call ID 106768: Modified ASPField SEDMSTATUS, so that EDM States are used to enumerate
*                                         values shown in the LOV/Select box.
*  27/10/2003  : DIKALK   Call Id 109375: Refresh child blocks after creating a new revision
*  29/10/2003  : SHTOLK   Call Id 109603: Removed the nextLevel() and 'Next Level' menu option from itembar3.
*  29/10/2003  : NISILK   Call Id 109321: Modified performRefreshParent.
*  04/11/203   : BAKALK   Call Id 110042: Fixed some repetitions of translatoin tags. Added storeSelections() in setApproved
*                         in order to support web client 3.5.1.
*  06/11/2003  : DIKALK   Call Id 110303 Changed selectRows() to storeSelections()
*  06/11/2003  : NISILK   Call Id 110312. Fixed.
*  08/11/2003  : MDAHSE   Call Id 110486. Removed itemtbl8 as it caused problems in the clone method.
*                         itemtbl8 was clones but never used anywhere so we got null pointer exception
*                         when trying to do it.
*  19/12/2003  : BAKALK   Did some modifications regarding multirow actions.
*  24/12/2003  : BAKALK   Set field orders as per window client.
*  10/02/2004  : BAKALK   Call Id: 112361."same action" option made available only for multirow layout. Modified copyApprovalTemplate(),dlgOk() and dlgCancel().
*  12/02/2004  : BAKALK   Replced javascript methods lookup,connect and getvalue with new methods supporting for validation.
*  19/02/2004  : DIKALK   Fixed Call 112667. Message to show 'No Rows Selected' of release wiz. is invoked with no rows selected.
*  19/02/2004  : BAKALK   Fixed Call 112742. Overrode the client method showMultiMenu(). ** This overriding must be removed once the web client fix their bug
*                         about showing multirow actions when no records selected.
*  01/03/2004  : DIKALK   Fixed call: 113055. Modified method previousLevel()
*  29/03/2004  : BAKALK   Merged SP1.
*  14/05/2004  : BAKALK   Merged Bug id 44185.
*  24/05/2004  : BAKALK   Merged Bug id 43788.
*  04/06/2004  : DHPELK   Fixed call 115137.
*  17/06/2004  : DIKALK   Most of Send Mail is now implemented in EdmMacro
*  22/06/2004  : BAKALK   Merged LCS patch <44509>.
*  22/06/2004  : BAKALK   Merged LCS patch <45090>.
*  22/06/2004  : DIKALK   Merged Bug Id 38605.
*  22/06/2004  : DIKALK   Merged Bug Id 44128.
*  23/06/2004  : DIKALK   Merged Bud Id 44629.
*  23/06/2004  : DIKALK   Merged Bud Id 44202.
*  30/06/2004  : BAKALK   Fixed the call 115582. Modified setApproved().
*  22/07/2004  : DIKALK   Fixed call 115582. Modified setApproved().
*  15/09/2004  : SUKMLK   Added check box for structure. Changed 'No of sub-documents' to 'No of Children'
*  11/10/2004  : DIKALK   Merged Bud 45688.
*  13/10/2004  : DIKALK   Merged Bud 46231.
*  13/10/2004  : DIKALK   Merged Bud 46819.
*  15/10/2004  : DIKALK   Merged Bud 46024.
*  15/10/2004  : DIKALK   Merged Bud 46625.
*  18/10/2004  : DIKALK   Merged Bud 46341.
*  18/10/2004  : DIKALK   Merged Bud 46865.
*  22/10/2004  : DIKALK   Modified get function for getting the name of the approver in block 5
*  26/10/2004  : DIKALK   Fixed call 119001.
*  02/12/2004  : DIKALK   Merged bug 47383.
*  03/12/2004  : DIKALK   Merged bug 47640.
*  06/12/2004  : BAKALK   Merged bug 47372.
*  06/12/2004  : DIKALK   Merged bug 47706
*  30/12/2004  : SukMlk   Fixed call 120849
*  30/12/2004  : SukMlk   Fixed call 120921
*  07/01/2005  : BaKalk   Fixed call 121035
*  20/01/2005  : BaKalk   Fixed call 121218.
*  16/03/2005  : SukMlk   Merged Bug 49235
*  01/04/2005  : KaRalk   call id 122532 corrected in performRefreshParent().  current tab would be refreshed.
*  06/04/2005  : DIKALK   Made it possible to make connections in the Where Used tab
*  07/04/2005  : DIKALK   Implemented new RMB Replace Revision
*  29/06/2005  : SUKMLK   Fixed call 125384.
*  04/07/2005  : BAKALK   Fixed call 125498.
*  18/07/2005  : DIKALK   Added code to handle links from DocTitleOvw and the tabs Consits Of and Where Used
*  27/07/2005  : SHTHLK   Merged Bug Id 50837, Modified Adjust() to Enable Access Tab, for all the three access types.
*                         modified deleteDocumentFile() to enable file delete to all users when access control is All Access.
*  29/08/2005  : DIKALK   Added functionality for seting structure documents to obsolete.
*  28/09/2005  : AMNALK   Merged bug 52998.
*  2005-10-11    MDAHSE   Call 127801. Made drawing of DnD-area conditional. Now check if there is a file before
*                         drawing it. Also made it smaller and placed it so that not to waste too much space.
*  2005-10-13     MDAHSE   Changed to use new drop control.
*  2005-10-21  :  NISILK   AMPR414-Electronic Signature, Modified nextAppStep and setApproved. Added ITEM5_SECURITY_CHECKPOINT_REQ in block 5 and method isSecurityCheckpointEnabled.
*  2005-10-25  :  NISILK   Modified setApproved and predefine.
*  2005-10-27  :  NISILK   Modified setApproved.  
*  2005-10-27  :  AMNALK   Merged Bug Id 53424.
*  2005-10-31  :  NISILK   Modified setApproved.  
*  2005-10-31  :  AMNALK   Merged Bud Id 53112.
*  2005-11-09  :  AMNALK   Fixed Call 128624.
*  2005-12-09  :  DIKALK   Added checks to ensure files with Unicode characters are excluded when DnDing files
*  2005-12-13  :  DIKALK   Fixed problem with approving documents that have Sec. Checkpt set to 'N'
*  2005-12-22  :  SUKMLK   Fixed call 129814, added a new structure field to the main blocks main section.
*  2006-01-03  :  RUCSLK   Fixed Call 130630 and Merged Bug Id 54528.
*  2006-02-02  :  THWILK   Fixed Call 132404 Modified method distributeDocuments.
*  2006-02-03  :  THWILK   Fixed Call 131944 Modified method setAsDocFileTemp() and Predefine().
*  2006-02-16  :  THWILK   Fixed Call 133949 Modified method setObsolete().
*  2006-03-10  :  AMNALK   Fixed Call 135725 Modified methods printDocument(), sendToMailRecipient() and setToCopyFile().
*  2006-03-14  :  RUCSLK   Fixed Call 135233 Electronic Signature for Reject Step.
*  2006-03-19  :  CHODLK   Merged Patch No: 56381 - The wrong error message in localized SV version.
*  2006-03-09  :  SHTHLK   Bug Id 56535, Modified SINSTANCEDESC fields getFunction to improve performance.
*  2006-03-20  :  THWILK   Merged Bug Id 56535.
*  2006-05-26  :  NIJALK   Made checks for All Access in methods copyAccessMsg(), releaseDocument(), deleteDocument(), copyApprovalTemplate(),isAccessOwner(), adjust(). 
*  2006-05-31  :  DIKALK   Bug 57779, Replaced use of app_owner with Docman Administrator
*  2006-06-01  :  DULOLK   Bug Id 57664, Modified deleteDocumentFile().  
*  2006-06-06  :  DULOLK   Bug Id 57275, Modified javascript function validateSetApproval(). 
*  2006-06-07  :  DULOLK   Bug Id 57275, Modified run().
*  2006-07-18  :  BAKALK   Bug ID 58216, Fixed Sql Injection.
*  2006-07-21  :  NIJALK   Bug Id 56685, Created a new menu option Print View Copy, Created new method printViewCopy(), Modified printDocument() to accomodate printing the view copy.
*  2006-08-04  :  CHODLK   Bug 58049, Modified methods onConfirmNextAppStep(), onConfirmRejectStep().
*  2006-08-04  :  CHODLK   Bug 58035 , Modified method nextAppStep().
*  2006-09-06  :  NIJALK   Bug 57781, Modified transferToDocGroup().
*  2006-11-13  :  NIJALK   Bug 61462, Modified methods completeAddToBc(), addToBriefcase().
*  2006-12-26  :  KARALK   DMPR303 NEW COLUMN ADMIN_ACCESS, NEW RMB Create Document Link.
*  2006-01-17  :  KARALK   DMPR303 save multiple objects.
*  2007-01-22  :  KARALK   call 140540 remove COLUMN ADMIN_ACCESS
*  2007-02-07  :  CHODLK   DMPR303, completed the connection of multiple objs to multiple docs. renamed method postNew() to insertNewObject().
*  2007-02-14  :  BAKALK   DMIT304, Added new tab of Transmittals. 
*  2007-04-10  :  BAKALK   DMIT304, Added new command "view comment file" in File Reference tab.
*  2007-05-21  :  BAKALK   Call Id: 140769, Added new command "Connect to Transmittal" and implemented it.
*  2007-05-23  :  BAKALK   Call ID 144114, Increased the height of Sub window for New revision Wizard.
*  2007-06-01  :  BAKALK   Call ID 145676, Increased the height of Sub window for 'Create Document Link'.
*  2007-06-08  :  BAKALK   Call ID 142538, ChangeRow() is called for every rowset if it has new or edit row when changing Tab.
*  2007-06-08  :  BAKALK   Call ID 143688, Modified run(), saveReturnITEM3() and getContents(). Added replaceRevisionFinish() and isStructure()
*  2007-07-17  :  UPDELK   Call ID 146314, Added Transmittal Project Tab.
*  2007-07-25  :  UPDELK   Call ID 146314, Removed Transmittal Project Tab and moved fields to Transmittal Tab
*  2007-07-25  :  UPDELK   Call Id: 146870: DOCMAW/DOCMAN - Transmittal and dynamic dependencies to PROJ.
*  2007-08-08  :  JANSLK   Changed translatable constants to correct localization errors.
*  2007-08-09  :  NaLrlk   XSS Correction.
*  2007-08-09  :  ASSALK   Merged Bug Id 65645, Added two new methods setStructAttribAll() and unsetStructAttribAll().
*  2007-08-09  :  ASSALK   Merged Bug Id 66028, Modified objectDetails() to include call center pages.
*  2007-08-09  :  AMNILK   Eliminated SQL Injection Security Vulnerability.
*  2007-08-10  :  ASSALK   Merged Bug Id 65339, Modifed adjust() to make the field security checkpoint disable accordingly.
*  2007-08-15  :  ASSALK   Merged Bug 58526, Modified getContents().
*  2007-08-15  :  ASSALK   Merged Bug Id 66804, Modified okFindITEM6() to display all the records.
*  2007-09-19  :  BAKALK   Call Id 148592, Modified getContents() to orverride javascipt::validateItem6PersonId().
*  2007-09-20  :  BAKALK   Call Id 148584, Modified getContents() to orverride javascipt::checkViewAccess(), added new aspfields too.
*  2007-09-24  :  DINHLK   Call Id 147706, Modified findDocumentsInStructure() to correct errors in select statement.
*  2007-11-14  :  AMNALK   Bug Id 66781, Modified adjust() to set the LOV views of the format and reason for issue according to the layout mode. Removed client function lovFormatSize().
*  2007-11-15  :  AMNALK   Bug Id 67336, Added new function checkFileOperationEnable() and disabled the file operations on mixed or multiple structure documents.
*  2007-11-23  :  DULOLK   Bug Id 65675, Modified insertNewObject().
*  2007-12-01  :  SHTHLK   Bug Id 67441, Modified DeleteDocumentFile() to check the edit access for the logged user when deleting the file.
*  2008-01-07  :  DINHLK   Bug Id 65462, Modified printContents().
*  2008-01-09  :  AMNALK   Bug Id 68528, Modified the validation of command viewCommentFromTransmittal in Predefine().
*  2008-01-28  :  BAKALK   Bug Id 68450, Modified insertNewObject().
*  2008-03-03  : VIRALK   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*  2008-03-08  :  SHTHLK   Bug Id 71698 Modified adjust() to call checkEnableHistoryNewRow() only when headset isn't empty
*  2008-03-18  :  SHTHLK   Bug Id 72420, Modified objectDetails() to to include lu "ProjectMiscProcurement"
*  2008-03-24  :  SHTHLK   Bug Id 67105, Modified okFind() and countFind() to get the mandatory search fields from document defaults. Added new menu "Mandatory Fields Settings..."
*  2008-04-16  :  SHTHLK   Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*  2008-04-23  :  SHTHLK   Bug Id 72471, Enable adding of document revisions to other document revisions
*  2008-05-05  :  SHTHLK   Bug Id 70286, Disabled createLink() based on user's grants   
*  2008-05-12  :  DULOLK   Bug Id 70553, Modified preDefine() and getContents(). Changed the Format and Reason For Issue lovs 
*                                        opened depending on the input of doc_class. Removed fix done by Bug 66781.
*  2008-07-24  :  DULOLK   Bug Id 73162, Made PersonId and GroupId Editable.
*  2008-08-04  :  SHTHLK   Bug Id 75677, Open the Distribution Wizard in a larger window.
*  2008-08-04  :  SHTHLK   Bug Id 74985, Added RMB "Edit" for the approval routing block and enable edit of the block accroding to the rights
*  2008-08-05  :  NIJALK   Bug Id 75490, Modified completeAddToBc(). Added overloaded method goToBriefcase().
*  2008-08-13  :  SHTHLK   Bug Id 75490, Modified createNewRevision() to enable creation of a revision when there's no file in document
*  2008-09-01  :  VIRALK   Bug Id 70512, Check Redline file status in all file state changes and wise versa.
*  2008-09-05  :  SHTHLK   Bug Id 76849, Modified error message in createNewRevision()
*  2008-09-09  :  AMNALK   Bug Id 73718, Modified viewCopy(), printDocument() and printViewCopy() to handle the structure documents.
*  2008-09-17  :  DULOLK   Bug Id 70808, Called custom dialog box to allow option for Background Job. Modified code to post BG Jobs.
*  2008-10-10  :  AMNALK   Bug Id 77556, Modified printDocument(), printViewCopy() and viewCopy() to handle non-structure documents.
*  2008-10-13  :  SHTHLK   Bug Id 77651, Modified okFind() and countFind() to refine the check for mandatory fields
*  2008-10-16  :  AMNALK   Bug Id 77774, Modified editCommentFile() to check the redline document type configured value.
*  2008-10-27  :  VIRALK   Bug Id 78032, Specify tab id when adding tabs to the page.
*  2008-10-28  :  VIRALK   Bug Id 77080, Added new methods resetStatus(), checkFileExistInRep() and addHistoryRecord()
*  2008-10-31  :  DULOLK   Bug Id 77727, Modified setApproved() to call different server method when Security Checkpoint enabled.
*  2009-01-15  :  AMCHLK   Bug Id 78853, Modified the okFindITEM7() method to display 100 records
*  2009-01-16  :  AMNALK   Bug Id 78800, Modified preDefine() and validate(). Added new functions saveReturnITEM10(), newRowITEM10() 
*					 and added new field DOC_TRANSMITTAL_LINE_STATUS.
*  2009-02-16  :  AMNALK   Bug Id 79174, Modified preDefine() and getContents(). Added new functions getSelectedTransmittalIds() and executeReport().
*  2009-03-11  :  AMNALK   Bug Id 80759, Modified adjust() and preDefine() to make the field DOC_STATUS readonly accordingly.
*  2009-06-02  :  AMNALK   Bug Id 81806, Added new function transmittalWizard() and new javascript function load_Transmittal_Info() to support RMB Transmittal Wizard.
*  2009-07-03  :  SHTHLK   Bug Id: 84461,Set the length of TRANSMITTAL_ID to 120
*  2009-08-14  :  SHTHLK   Bug Id 85223, Directed the link from consists of/where used to the correct document revision 
*  2009-09-15  :  AMNALK   Bug Id 84270, Added two new menu items to the transmittal tab to view and send by email the comment file.
*  2009-09-18  :  SHTHLK   Bug Id 85876, Added warnings when coping approval templates without steps
*  2009-09-25  :  SHTHLK   Bug Id 85876, Used Get_Max_Line_No__() to check the exisitence of steps in the template
*  2009-09-29  :  SHTHLK   Bug Id 85876, Used Steps_Exists() instead of Get_Max_Line_No__()
*  2009-12-15  :  AMNALK   Bug Id 87331, Changed the LOV view of the BRIEFCASE_NO from DOC_BC_LOV to DOC_BC_LOV1.
*  2010-03-25  :  AMNALK   Bug Id 89200, Modified objectDetails() to include the lu "InventoryPart".
*  2010-04-02  :  VIRALK   Bug Id 88317, Modified createNewRevision() and createNewSheet(). Restrict creation of new files for * users.
*  2010-05-06  :  RUMELK   Bug Id 90011, Made the REV_NO field visible in preDefine(). 
*  2010-09-18  :  DULOLK   Bug Id 92125, Checked for files without extension in drag drop. Multiple files are alowed to proceed to file import where it is checked.
*------------------------------------------------------------------------------------------------------------------------------------------------------------------
*/
   

package ifs.docmaw; 

import ifs.docmaw.edm.*;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class DocIssue extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocIssue");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   protected ASPContext       ctx;
   protected ASPHTMLFormatter fmt;

   protected ASPBlock       headblk;
   protected ASPRowSet      headset;
   protected ASPCommandBar  headbar;
   protected ASPTable       headtbl;
   protected ASPBlockLayout headlay;

   protected ASPBlock       itemblk0;
   protected ASPRowSet      itemset0;
   protected ASPCommandBar  itembar0;
   protected ASPTable       itemtbl0;
   protected ASPBlockLayout itemlay0;

   protected ASPBlock       itemblk1;
   protected ASPRowSet      itemset1;
   protected ASPCommandBar  itembar1;
   protected ASPTable       itemtbl1;
   protected ASPBlockLayout itemlay1;

   protected ASPBlock       itemblk2;
   protected ASPRowSet      itemset2;
   protected ASPCommandBar  itembar2;
   protected ASPTable       itemtbl2;
   protected ASPBlockLayout itemlay2;

   protected ASPBlock       itemblk3;
   protected ASPRowSet      itemset3;
   protected ASPCommandBar  itembar3;
   protected ASPTable       itemtbl3;
   protected ASPBlockLayout itemlay3;

   protected ASPBlock       itemblk4;
   protected ASPRowSet      itemset4;
   protected ASPCommandBar  itembar4;
   protected ASPTable       itemtbl4;
   protected ASPBlockLayout itemlay4;

   protected ASPBlock       itemblk5;
   protected ASPRowSet      itemset5;
   protected ASPCommandBar  itembar5;
   protected ASPTable       itemtbl5;
   protected ASPBlockLayout itemlay5;

   protected ASPBlock       itemblk6;
   protected ASPRowSet      itemset6;
   protected ASPCommandBar  itembar6;
   protected ASPTable       itemtbl6;
   protected ASPBlockLayout itemlay6;

   protected ASPBlock       itemblk7;
   protected ASPRowSet      itemset7;
   protected ASPCommandBar  itembar7;
   protected ASPTable       itemtbl7;
   protected ASPBlockLayout itemlay7;

   protected ASPBlock       itemblk8;
   protected ASPRowSet      itemset8;
   protected ASPCommandBar  itembar8;
   protected ASPBlockLayout itemlay8;

   protected ASPBlock       itemblk9;
   protected ASPRowSet      itemset9;
   protected ASPCommandBar  itembar9;
   protected ASPTable       itemtbl9;
   protected ASPBlockLayout itemlay9;

   protected ASPBlock       itemblk10;
   protected ASPRowSet      itemset10;
   protected ASPCommandBar  itembar10;
   protected ASPTable       itemtbl10;
   protected ASPBlockLayout itemlay10;

   protected ASPBlock  dummyblk;

   protected ASPTabContainer tabs;
   protected ASPField f;

   protected ASPPopup popup_status;
   protected ASPPopup popup_file_operations;
   protected ASPPopup popup_access;
   protected ASPPopup popup_general;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   protected ASPTransactionBuffer trans;
   protected ASPBuffer keys;
   protected ASPBuffer data;
   protected ASPBuffer bc_Buffer;
   protected ASPBuffer trans_Buffer;
   protected ASPCommand cmd;
   protected ASPQuery q;

   protected boolean bConfirm;
   protected boolean bConfirmEx;
   protected boolean bCopyProfile;
   protected boolean bObjectConnection;
   protected boolean bItem3Duplicate;
   protected boolean bItem5Duplicate;
   protected boolean bItem6Duplicate;
   protected boolean bTranferToEDM;
   protected boolean bTranferToCreateLink;//DMPR303
   protected boolean bCommentFileCheckedOut;
   protected boolean launchFile;
   protected boolean RedlineApp;
   protected boolean RedlineThere;
   protected boolean client_confirmation;
   protected boolean addingToBc;
   protected boolean startFromTransmittal;
   protected boolean bShowTransLov;
   protected boolean showInMulti;
   protected boolean bOpenWizardWindow = false;
   protected boolean modifySubWindow4NewRev;
   protected boolean bOpenReleaseWizardWindow = false;
   protected boolean bShowBCLov = false;
   protected boolean bShowStructure = false;
   protected boolean bConfiramtion4SettingStructureType;
   protected boolean bSetStructure;
   protected boolean isProjInstalled = false;

   protected String root_path;
   protected String strIFSCliMgrOCX;
   protected String sMessage;
   protected String sPersonError;
   protected String sHistoryMode;
   protected String confirm_func;
   protected String unconfirm_func;
   protected String searchURL;
   protected String savetype;
   protected String sFilePath;
   protected String sClientFunction;
   protected String bc_BcNo;
   protected String sendingUrl;
   protected String transfered_CurrRow;
   protected String sUrl;
   protected String sBcNoFromBriefcase;
   protected String enableHistoryNewButton;
   //Bug 53039, Start
   protected boolean bDoCheckForAllAccess = false; 
   //Bug 53039, End
   //Bug 56685, Start
   protected boolean bPrintViewCopy = false; 
   //Bug 56685, End

   // Docman constants
   protected String sCheckedIn;
   protected String sCheckedOut;
   protected String sInBriefcase;
   protected String sCheckedOutToBc;
   protected String sPrelimin;
   protected String sAppInProg;
   protected String sApproved;
   protected String sObsolete;
   protected String sReleased;
   protected String dGroup;
   protected String dAll;
   protected String dUser;
   protected String sBcCreated;
   protected String trans_id_sent;
   protected String trans_CurrRow;

   //Bug Id 67105, Start
   protected String sMandatoryFieldsList;
   protected String sMandatoryFields;
   protected boolean bMandatoryFieldsEmpty;
   //Bug Id 67105, End
   protected boolean bOpenDistributionWizardWindow = false; //Bug Id 75677   

   //Bug 70808, Start
   protected static final int BACKGROUNDJOB_LIMIT = 1000;
   protected String objArray[];
   protected boolean bPerformBackgroundJob;
   protected boolean bDialogBoxPopped;
   protected boolean bBGJobAlertShown;
   protected boolean bConfirmBackgroundJob;
   protected boolean bReleaseBGJPossible;
   //Bug 70808, End

   //Bug Id 77080 Start
   protected String sFinishCheckIn;
   protected String sOpeInProg;
   //Bug Id 77080 End
   
   //Bug Id 79174, start
   protected boolean bShowTransmittalReport = false;
   //Bug Id 79174, end

   //Bug Id 81806, start
   protected DocumentTransferHandler     doc_hdlr;
   protected boolean bOpenTransmittalWizardWindow = false;
   //Bug Id 81806, end

   //===============================================================
   // Construction
   //===============================================================
   public DocIssue(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }



   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();

      ctx     = mgr.getASPContext();
      trans   = mgr.newASPTransactionBuffer();
      fmt     = mgr.newASPHTMLFormatter();

      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      bConfirm               = false;
      bCopyProfile           = false;
      bObjectConnection      = false;
      bItem3Duplicate        = false;
      bItem5Duplicate        = false;
      bItem6Duplicate        = false;
      bTranferToEDM          = false;
      bTranferToCreateLink   = false;//DMPR303
      bCommentFileCheckedOut = false;

      launchFile             = false;
      strIFSCliMgrOCX        = "";
      bShowBCLov             = false;
      bOpenWizardWindow      = false;
      bMandatoryFieldsEmpty  = false;//Bug Id 67105

      sFinishCheckIn	     = "FinishCheckIn";// Bug Id 77080
      sOpeInProg	     = "Operation In Progress";// Bug Id 77080


      sMessage = "";
      sPersonError =  mgr.translate("DOCMAWDOCISSUEPERSON_ERROR: Both group id and person id can not have a value.");

      savetype           = ctx.readValue("SAVETYPE", "");
      sHistoryMode       = ctx.readValue("HISTMODE", "7");
      sBcNoFromBriefcase = ctx.readValue("FROM_BRIEFCASE","");
      bc_BcNo            = ctx.readValue("BC_BCNO", "");
      sendingUrl         = ctx.readValue("SEND_URL", "");
      transfered_CurrRow = ctx.readValue("TRANSFERED_CURRROW", "");
      trans_id_sent      = ctx.readValue("TRANS_ID_SENT", "");

      addingToBc           = ctx.readFlag("ADDTOBC",false);
      startFromTransmittal = ctx.readFlag("FROM_TRANS",false);
      showInMulti          = ctx.readFlag("SHOWINMULTI",false);

      bc_Buffer    = ctx.readBuffer("BC_BUFF");
      trans_Buffer = ctx.readBuffer("TRANS_BUFFER");

      initializeSession();
      getMandatoryFieldsForQuery();//Bug Id 67105
      trans.clear();
      if (mgr.commandBarActivated())
      {
         String comnd = mgr.readValue("__COMMAND");

         if ("HEAD.activateTitle".equals(comnd)         || //if changing tab
             "HEAD.activateOriginals".equals(comnd)     ||
             "HEAD.activateObjects".equals(comnd)       ||
             "HEAD.activateConsistsOf".equals(comnd)    ||
             "HEAD.activateWhereUsed".equals(comnd)     ||
             "HEAD.activateRouting".equals(comnd)       ||
             "HEAD.activateAccess".equals(comnd)        ||
             "HEAD.activateHistory".equals(comnd)       ||
             "HEAD.activateFileReferences".equals(comnd)||
             "HEAD.activateTransmittals".equals(comnd))
         {
            if (itemset0.countRows()>0 && "New__".equals(itemset0.getRowStatus())) {
               itemset0.changeRow();
               itemlay0.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset1.countRows()>0 && "New__".equals(itemset1.getRowStatus())) {
               itemset1.changeRow();
               itemlay1.setLayoutMode(itemlay1.MULTIROW_LAYOUT);

            }   
            if (itemset2.countRows()>0 && "New__".equals(itemset2.getRowStatus())) {
               itemset2.changeRow();
               itemlay2.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset3.countRows()>0 && "New__".equals(itemset3.getRowStatus())) {
               itemset3.changeRow();
               itemlay3.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset4.countRows()>0 && "New__".equals(itemset4.getRowStatus())) {
               itemset4.changeRow();
               itemlay4.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset5.countRows()>0 && "New__".equals(itemset5.getRowStatus())) {
               itemset5.changeRow();
               itemlay5.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset6.countRows()>0 && "New__".equals(itemset6.getRowStatus())) {
               itemset6.changeRow();
               itemlay6.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset7.countRows()>0 && "New__".equals(itemset7.getRowStatus())) {
               itemset7.changeRow();
               itemlay7.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset8.countRows()>0 && "New__".equals(itemset8.getRowStatus())) {
               itemset8.changeRow();
               itemlay8.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset9.countRows()>0 && "New__".equals(itemset9.getRowStatus())) {
               itemset9.changeRow();
               itemlay9.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            if (itemset10.countRows()>0 && "New__".equals(itemset10.getRowStatus())) {
               itemset10.changeRow();
               itemlay10.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
            }
            
         }
         else if ("ITEM3.DuplicateRow".equals(comnd))
            bItem3Duplicate = true;
         if ("ITEM5.DuplicateRow".equals(comnd))
            bItem5Duplicate = true;
         if ("ITEM6.DuplicateRow".equals(comnd))
            bItem6Duplicate = true;
         eval(mgr.commandBarFunction());
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();// pls do not insert any condition  here since validate method migh not be called under some condition: bakalk
      else if (mgr.commandLinkActivated())
         eval(mgr.commandLinkFunction());	//EVALInjections_Safe AMNILK 20070810
      else if (!mgr.isEmpty(mgr.getQueryStringValue("ADDTOBC")))
         startAddToBc();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CONNECT_TO_TRANS")))// trans
         startConnectToTrans();
      else if (mgr.dataTransfered())
         populateTransferredData();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) || (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")) && !mgr.isEmpty(mgr.getQueryStringValue("DOC_NO"))))
         runQuery();

      else if (!mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_CLASS")) && !mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_SHEET")) && !mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_REV"))) //Bug Id 85223
         findSubDocuments();

      else if (!mgr.isEmpty(mgr.getQueryStringValue("FINDLAY")))
         openInFindLayout();
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV"))))
         runQuery();
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV"))))
         runQuery();
      else if (!mgr.isEmpty(mgr.readValue("BC_NO")))
         setBcNoFromBriefcase();
      else if (!mgr.isEmpty(mgr.readValue("MULTIROWACTION")))
         eval(mgr.readValue("MULTIROWACTION"));	 		//EVALInjections_Safe AMNILK 20070810
      //Bug Id 57275, Start
      else if ("TRUE".equals(mgr.readValue("CHANGE_CURRENT_DOCUMENT")))
         setCurrentDocument();
      //Bug Id 57275, End
      else if ("OK".equals(mgr.readValue("CONFIRM")))
      {
         client_confirmation = true;
         confirm_func = ctx.readValue("CONFIRMFUNC","");

                        //Bug 70808, Start
                        String bgj_confirm = ctx.readValue("BGJ2BDONE","");
                        String bgj_diag_popped = ctx.readValue("BGJ_DIALOG_POPPED","");

                        if ("OK".equals(mgr.readValue("BGJ_CONFIRMED")) || "TRUE".equals(bgj_confirm))
                            bPerformBackgroundJob = true;                            
                        else
                            bPerformBackgroundJob = false;

                        if ("TRUE".equals(bgj_diag_popped))
                           bDialogBoxPopped = true;
                        else
                           bDialogBoxPopped = false;
                        
         if ("startApproval();".equals(confirm_func))
            startApproval();
         else if ("cancelApproval();".equals(confirm_func))
            cancelApproval();
         else if ("setApproved();".equals(confirm_func))
            setApproved();
         else if ("setObsolete();".equals(confirm_func))
            setObsolete();
         else if ("onConfirmNextAppStep()".equals(confirm_func))
            onConfirmNextAppStep();
         else if ("onConfirmRejectStep()".equals(confirm_func))
            onConfirmRejectStep();
         else if ("saveLast()".equals(confirm_func))
            saveLast();
         else if ("deleteITEM6Last()".equals(confirm_func))
            deleteITEM6Last();
         else if ("copyAccessTem()".equals(confirm_func))
            copyAccessTem();
         else if ("replaceRevInStructure()".equals(confirm_func))
         {
            bSetStructure = true;
            replaceRevisionFinish();
         }
         else if ("releaseDocument()".equals(confirm_func)) 
            releaseDocument();
         //Bug 70808, End 

	 //Bug Id 77080 Start
	 else if ("resetStatus()".equals(confirm_func)) 
	    resetStatus();      	
	 //Bug Id 77080 End
      }
      else if ("CANCEL".equals(mgr.readValue("CONFIRM")))
      {
         unconfirm_func = ctx.readValue("UNCONFIRMFUNC","");
         eval(unconfirm_func+";");		//EVALInjections_Safe AMNILK 20070810
      }
      else if ("TRUE".equals(mgr.readValue("OBJECT_INSERTED")))
         insertNewObject();
      else if ("TRUE".equals(mgr.readValue("MODE_CHANGED")))
         historyModeChanged();
      else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
         performRefreshParent();
      else if ("TRUE".equals(mgr.readValue("BRIEFCASE_SELECTED")))
         addToBriefcase();
      else if ("TRUE".equals(mgr.readValue("TRANSMITTAL_SELECTED")))
         this.connectToTrans();
      else if (!mgr.isEmpty(mgr.readValue("DROPED_FILES_PATH")) && !mgr.isEmpty(mgr.readValue("DROPED_FILES_LIST")))
         lookIntoDroppedFiles();


      if ("TRUE".equals(mgr.readValue("REDLINE_EDIT")))
      {
         if (!mgr.isEmpty(mgr.readValue("REDLINE_APP")))
            editCommentFile();
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUEREDLINEPATHNOTEXSISTS: Application path for External Redline Application does not exists"));
      }
      if (!mgr.isEmpty(mgr.readValue("DOC_CLASS_FROM_WIZ")))
         addNewRev();


      adjust();
      tabs.saveActiveTab();

      ctx.writeValue("HISTMODE", sHistoryMode);
      ctx.writeValue("SAVETYPE", savetype);
      ctx.writeValue("FROM_BRIEFCASE", sBcNoFromBriefcase);
      ctx.writeValue("BC_BCNO", bc_BcNo);
      ctx.writeValue("SEND_URL", sendingUrl);
      ctx.writeValue("TRANS_ID_SENT", trans_id_sent);
      ctx.writeValue("TRANSFERED_CURRROW", transfered_CurrRow);
      ctx.writeFlag("ADDTOBC",addingToBc);
      ctx.writeFlag("FROM_TRANS",startFromTransmittal);
      ctx.writeFlag("SHOWINMULTI",showInMulti);

      if (addingToBc)
      {
         ctx.writeBuffer("BC_BUFF",bc_Buffer);
      }
      else if (startFromTransmittal) {
         ctx.writeBuffer("TRANS_BUFFER",trans_Buffer);
      }
   }

   public void validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

      if ("DAYS_EXPIRED".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction( "DUDAYS", "DOC_ISSUE_API.GET_EXPIRATION_DUE_DAYS", "NEXPIRATIONDUEDAYS" );
         cmd.addParameter("DT_OBSOLETE,DAYS_EXPIRED");
         trans = mgr.validate(trans);
         String days = trans.getValue("DUDAYS/DATA/NEXPIRATIONDUEDAYS");

         if (mgr.isEmpty(days))
            days = "";

         mgr.responseWrite( days + "^" );
      }
      else if ("ITEM6_PERSON_ID".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction( "GETNAME", "PERSON_INFO_API.Get_Name", "PERSONNAME" );
         cmd.addParameter("ITEM6_PERSON_ID");
         cmd = trans.addCustomFunction( "GETUSER", "PERSON_INFO_API.Get_User_Id", "PERSONUSERID" );
         cmd.addParameter("ITEM6_PERSON_ID");
         trans = mgr.validate(trans);

         String name = trans.getValue("GETNAME/DATA/PERSONNAME");
         String user = trans.getValue("GETUSER/DATA/PERSONUSERID");

         if (mgr.isEmpty(name))
            name = "";
         if (mgr.isEmpty(user))
            user = "";

         mgr.responseWrite( name + "^" + user + "^" );
      }
      else if ("TEMP_PROFILE_ID".equals(val))
      {

         trans.clear();
         cmd = trans.addCustomFunction( "PROFILEDESC","APPROVAL_PROFILE_API.Get_Description","DESCRIPTION");
         cmd.addParameter("PROFILE_ID",mgr.readValue("TEMP_PROFILE_ID"));
         trans = mgr.validate(trans);

         String profiledesc = trans.getValue("PROFILEDESC/DATA/DESCRIPTION");
         String txt = (mgr.isEmpty(profiledesc) ? "" : profiledesc + "^") ;
         mgr.responseWrite(txt);
      }
      else if ("REDLINE_STATUS".equals(val))
      {
         mgr.responseWrite(mgr.readValue("REDLINE_STATUS")+"%^");
      }
      else if ("DOC_STATUS".equals(val))
      {
         String curr = mgr.readValue("DOC_STATUS");
         mgr.responseWrite(curr + "^");
      }
      else if ("SUB_DOC_NO".equals(val) || "ITEM4_DOC_NO".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETTITLE1","DOC_TITLE_API.Get_Title","OUT_1");
         cmd.addParameter("SUB_DOC_CLASS");
         cmd.addParameter("SUB_DOC_NO");
         trans = mgr.validate(trans);

         String title = trans.getValue("GETTITLE1/DATA/OUT_1");
         String txt = (mgr.isEmpty(title) ? "" : title + "^") ;
         mgr.responseWrite(txt);
      }
      else if ("SUB_DOC_REV".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETNOCHLDRN","DOC_STRUCTURE_API.Number_Of_Children_","OUT_1");
         cmd.addParameter("SUB_DOC_CLASS");
         cmd.addParameter("SUB_DOC_NO");
         cmd.addParameter("SUB_DOC_SHEET");
         cmd.addParameter("SUB_DOC_REV");
         trans = mgr.validate(trans);

         String noofchildren = trans.getValue("GETNOCHLDRN/DATA/OUT_1");
         String txt = (mgr.isEmpty(noofchildren) ? "" : noofchildren + "^") ;
         mgr.responseWrite(txt);
      }
      //Bug Id 78800, start
      else if ("TRANSMITTAL_ID".equals(val))
      {
	 trans.clear();
         cmd = trans.addCustomFunction("GETTRANSDESC","DOCUMENT_TRANSMITTAL_API.Get_Transmittal_Description","OUT_1");
         cmd.addParameter("TRANSMITTAL_ID");
         trans = mgr.validate(trans);
	 
	 String trans_description = trans.getValue("GETTRANSDESC/DATA/OUT_1");

         trans.clear();
         cmd = trans.addCustomFunction("GETPROJECT","TRANSMITTAL_OBJ_CON_API.Get_Project_Id","OUT_1");
         cmd.addParameter("TRANSMITTAL_ID");
         trans = mgr.validate(trans);

         String project_id = trans.getValue("GETPROJECT/DATA/OUT_1");
         String txt = (mgr.isEmpty(trans_description) ? "" : trans_description + "^") + (mgr.isEmpty(project_id) ? "" : project_id + "^");
	 
         mgr.responseWrite(txt);
      }
      //Bug Id 78800, end
      mgr.endResponse();
   }

   public void copyAccessMsg()
   {
      ASPManager mgr = getASPManager();

      //Bug 53039, Start
      if (dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
      {
         bDoCheckForAllAccess = true;
      }
      else
         bDoCheckForAllAccess = false;
      //Bug 53039, End

      if (!"TRUE".equals(isAccessOwner()))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTOCOPYACCESSTMPL: You must have administrative rights to be able to copy an access template."));
         return;
      }

      if (!isValidColumnValue("STATE", sApproved, sReleased, false))
      {
         mgr.showAlert("DOCMAWDOCISSUECANNOTCOPYACCESSTEMPLINAPPORREL: You cannot copy an access template when document(s) are in state Approved or Released.");
         return;
      }

      bConfirm = true;
      sMessage = mgr.translate("DOCMAWDOCISSUEQACCTEM: Do you want to copy the access template from the document class? NOTE: This will not affect the current access rows.");
      ctx.writeValue("CONFIRMFUNC", "copyAccessTem()");
      ctx.writeValue("UNCONFIRMFUNC", "onUnconfirm()");
   }

   public void copyAccessTem()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      if (headlay.isMultirowLayout())
         headset.selectRows();
      else
         headset.selectRow();

      headset.setFilterOn();
      headset.first();
      int count = 0;

      do
      {
         cmd = trans.addCustomCommand("COPYACCESSTEMPLATE" + count++, "Document_Issue_Access_API.Copy_Access_Template__");
         cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV"));
      }
      while (headset.next());

      trans = mgr.perform(trans);

      headset.setFilterOff();

      if (headlay.isSingleLayout())
         okFindITEM6();
   }

   public void startAddToBc()
   {
      ASPManager mgr = getASPManager();

      bc_Buffer  = mgr.getTransferedData();
      bc_BcNo    = mgr.getQueryStringValue("BC_NO");
      sendingUrl     = mgr.getQueryStringValue("SEND_URL");
      transfered_CurrRow = mgr.getQueryStringValue("CUR_ROW");
      addingToBc = true;
      showInMulti = true;

      headlay.setLayoutMode(headlay.FIND_LAYOUT);
   }

   public void startConnectToTrans()// when the operation start from TransmittalInfo
   {
      ASPManager mgr = getASPManager();
      

      trans_Buffer          = mgr.getTransferedData();
      trans_id_sent         = mgr.getQueryStringValue("TRANSMITTAL_ID");
      sendingUrl            = mgr.getQueryStringValue("SEND_URL");
      startFromTransmittal  = true;
      showInMulti           = true;

      headlay.setLayoutMode(headlay.FIND_LAYOUT);
   }

   public void completeAddToBc() // this method is called when the page is called from DocBriefcase
   {
      String docNo;
      String docClass;
      String docSheet;
      String docRev;
      String attr;
      int no_rows_execute_on = 0;
      ASPCommand cmd;
      boolean bReturn = false;

      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.selectRows();
         if (headset.countSelectedRows() == 0)
         {
            mgr.showAlert("DOCMAWDOCISSUENOROWSSELECTED: No documents selected to Add to Briefcase!");
         }
         headset.storeSelections();
         headset.setFilterOn();

         no_rows_execute_on = headset.countRows();
      }
      else
      {
         no_rows_execute_on = 1;
      }
      trans.clear();
      boolean bInvalidState = false;

      for (int k=0;k< no_rows_execute_on;k++)
      {
         if (!((sPrelimin.equals(headset.getRow().getValue("STATE"))) || (sReleased.equals(headset.getValue("STATE"))) || (sApproved.equals(headset.getRow().getValue("STATE")))))
            bInvalidState = true;
         else
         {
            if ("TRUE".equals(headset.getValue("CAN_ADD_TO_BC")))
            {
               docClass =headset.getRow().getValue("DOC_CLASS");
               docNo    =headset.getRow().getValue("DOC_NO");
               docSheet =headset.getRow().getValue("DOC_SHEET");
               docRev   =headset.getRow().getValue("DOC_REV");

               attr = "BRIEFCASE_NO" + (char)31 + bc_BcNo  + (char)30;
               attr += "DOC_CLASS"   + (char)31 + docClass + (char)30;
               attr += "DOC_NO"      + (char)31 + docNo    + (char)30;
               attr += "DOC_SHEET"   + (char)31 + docSheet + (char)30;
               attr += "DOC_REV"     + (char)31 + docRev   + (char)30;

               //Bug 61462, Start
               cmd = trans.addCustomCommand("FILEREFEXIST"+k,"DOC_BRIEFCASE_ISSUE_API.File_Ref_Exist");
               cmd.addParameter("DUMMY1", docClass);
               cmd.addParameter("DUMMY1", docNo);
               cmd.addParameter("DUMMY1", docSheet);
               cmd.addParameter("DUMMY1", docRev);
               cmd.addParameter("DUMMY1", "ORIGINAL");
               //Bug 61462, End

               cmd = trans.addCustomCommand("ADDTOBC"+k, "DOC_BRIEFCASE_ISSUE_API.New__");
               cmd.addParameter("DUMMY1");
               cmd.addParameter("OBJID");
               cmd.addParameter("OBJVERSION");
               cmd.addParameter("DUMMY1", attr);
               cmd.addParameter("DUMMY1", "DO");

               if (headlay.isMultirowLayout())
               {
                  headset.next();
               }
            }
            else
            {
               bReturn=true;
            }
         }
      }

      if (bInvalidState)
      {
         mgr.showAlert ("DOCMAWDOCISSUENOVALIDSTATE: Only Documents in state Preliminary, Approved or Released can be added to a briefcase");
         headset.setFilterOff();
      }
      else if (bReturn)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOEDITACCESSADDTOBRIEF: You must have Edit access to the Document(s) that you want to add to your Briefcase!"));
         headset.setFilterOff();
      }
      else
      {
         trans = mgr.perform(trans);
         if (headlay.isMultirowLayout())
         {
            headset.setFilterOff();
            headset.unselectRows();
         }
         addingToBc = false;
         mgr.transferDataTo(sendingUrl+"?AFTER_ADDING_DOCS=YES&CURR_BC_NO="+mgr.URLEncode(bc_BcNo),bc_Buffer);
      }
   }

   public void lookIntoDroppedFiles()
   {
      ASPManager mgr = getASPManager();
      String sFileList = mgr.readValue("DROPED_FILES_LIST");
      String sDroppedFilePath = mgr.readValue("DROPED_FILES_PATH");
      String[] sFiles = split(sFileList,"|");

      String fullFileName= "";
      if (sDroppedFilePath.charAt(sDroppedFilePath.length()-1)=='\\')
         fullFileName = sDroppedFilePath+sFiles[0];
      else
      {
         fullFileName = sDroppedFilePath+"\\"+sFiles[0];
         sDroppedFilePath += "\\";
      }

      if (sFiles.length==1)
         checkInTheDroppedFile(fullFileName);
      else if (sFiles.length>1)
      {
         sFileList = "";
         for (int k=0;k<sFiles.length;k++)
         {
            sFileList += sDroppedFilePath + sFiles[k]+ "|";
         }
         moveDroppedFilesToFileImport(sFileList);
      }
   }

   public void checkInTheDroppedFile(String sFileName)
   {
      ASPManager mgr = getASPManager();
      headset.goTo(headset.getCurrentRowNo());
      ASPBuffer currentRow= headset.getRow();
      ASPBuffer buff = mgr.newASPBuffer();

      ASPBuffer actionData = mgr.newASPBuffer();

      String action      = "CHECKIN";
      String doc_type    = "ORIGINAL";
      String same_action = "NO";

      actionData.addItem("DOC_TYPE",doc_type);
      actionData.addItem("FILE_ACTION",action);
      actionData.addItem("SAME_ACTION_TO_ALL",same_action);

      ASPBuffer row = buff.addBuffer("1");

      row.addItem("DOC_CLASS", currentRow.getValue("DOC_CLASS"));
      row.addItem("DOC_NO",    currentRow.getValue("DOC_NO"));
      row.addItem("DOC_SHEET", currentRow.getValue("DOC_SHEET"));
      row.addItem("DOC_REV",   currentRow.getValue("DOC_REV"));
      row.addItem("FILE_NAME",   sFileName);

      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page",actionData,buff);
      bTranferToEDM = true;


   }

   public void moveDroppedFilesToFileImport(String fileNames)
   {

      ASPManager mgr = getASPManager();
      ASPBuffer fileBuffer = mgr.newASPBuffer();
      fileBuffer.addItem("FILE_NAMES",fileNames.replaceAll("\\\\","\\\\\\\\"));
      mgr.transferDataTo("FileImport.page?FROM_OTHER=YES&DOC_CLASS="+mgr.URLEncode(headset.getRow().getValue("DOC_CLASS")),fileBuffer);

   }

   public void openInFindLayout()
   {
      headlay.setLayoutMode(headlay.FIND_LAYOUT);
   }

   protected void populateTransferredData()
   {
      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE")))
      {
         ASPManager mgr = getASPManager();
         ASPBuffer buf = mgr.getTransferedData();

         String doc_class = buf.getValue("DATA/DOC_CLASS");
         String doc_no = buf.getValue("DATA/DOC_NO");
         String doc_sheet = buf.getValue("DATA/DOC_SHEET");
         String doc_rev = buf.getValue("DATA/DOC_REV");

         findDocumentsInStructure(doc_class, doc_no, doc_sheet, doc_rev);
      }
      else
      {
         runQuery();
      }
   }

   public void runQuery()
   {
      ASPManager mgr = getASPManager();
      ASPLog log  = mgr.getASPLog();
      searchURL = mgr.createSearchURL(headblk);

      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      q.setOrderByClause("DOC_CLASS, DOC_NO, SHEET_ORDER, REV_NO DESC");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);


      if (headset.countRows() > 0)
      {
         trans.clear();

         q = trans.addQuery(itemblk0);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.includeMeta("ALL");

         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }

      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_KEY")))
      {
         String doc_key = mgr.getQueryStringValue("DOC_KEY");
         String[] keys  = split(doc_key,'^');
         int k=0;
         headset.first();
         while (!(headset.getValue("DOC_CLASS").equals(keys[0]) &&
                  headset.getValue("DOC_NO").equals(keys[1]) &&
                  headset.getValue("DOC_SHEET").equals(keys[2]) &&
                  headset.getValue("DOC_REV").equals(keys[3]) ))
         {
            headset.next();
         }

      }
   }

   //Bug Id 77080, Start
	public void resetStatus()
	{
	    ASPManager mgr = getASPManager();
	    boolean bMultiRow;

	    if ("OK".equals(mgr.readValue("CONFIRM")))
		{
			client_confirmation = true;
			confirm_func = ctx.readValue("CONFIRMFUNC","");
		}
	    
	    if ("TRUE".equals(mgr.readValue("MULTIROW_EDIT")))
			bMultiRow = true;

	    if (headlay.isMultirowLayout() && headset.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
			return;
		}
		
	    if(isUserDocmanAdministrator())
	    {

		boolean error = false;
		int curr_row = 0;
		String res;

		if (!client_confirmation && !isValidColumnValue("EDM_DB_STATE", sOpeInProg, true))
		{

		    mgr.showAlert(mgr.translate("DOCMAWDOCISSUEOPINPRO: The file status can be reset only for documents in status Operation In Progress.")); 
		}
		else
		{
		
		     if (!client_confirmation)
		    {
			getClientConformation(mgr.translate("DOCMAWDOCISSUECONFIRMRESET: You are trying to reset the file status of this document(s). If a file exists in the repository for this document, the file status will be changed to Checked In. If not, the file reference will be removed. Make sure that no documents are in transit before performing this operation because there may be large files which are currently being checked in or checked out."), "resetStatus()");
			
		    }
		    else
		    {
			
			if (headlay.isMultirowLayout())
			{	
				
				headset.storeSelections();
				curr_row = headset.getRowSelected();
			}
			else
			{
				headset.selectRow();
				curr_row = headset.getCurrentRowNo();
			}
    
			headset.setFilterOn();
			headset.first();

			do
			{
			     String doc_class = headset.getRow().getValue("DOC_CLASS");
			     String doc_no = headset.getRow().getValue("DOC_NO");
			     String doc_sheet = headset.getRow().getValue("DOC_SHEET");
			     String doc_rev = headset.getRow().getValue("DOC_REV");
			     String doc_type = "ORIGINAL";
			     String file_no = "1";
			    
			    

			    res = checkFileExistInRep(doc_class,doc_no,doc_sheet,doc_rev,doc_type);
			    if(res == "exist")
			    {
				 //String checked_in = "FinishCheckIn";
				 trans.clear();
				 ASPCommand cmd = trans.addCustomCommand("STATECHECKIN", "Edm_File_API.Set_File_State_No_History");
				 cmd.addParameter("IN_1", doc_class);
				 cmd.addParameter("IN_1", doc_no);
				 cmd.addParameter("IN_1", doc_sheet);
				 cmd.addParameter("IN_1", doc_rev);
				 cmd.addParameter("IN_1", doc_type);
				 cmd.addParameter("IN_1", sFinishCheckIn);
				 trans = mgr.perform(trans);
    
				 addHistoryRecord(doc_class,doc_no,doc_sheet,doc_rev,"RESETSTATUS",mgr.translate("DOCMAWDOCISSUEFILECHECKEDIN: File status is manually reset to Checked In."));
				
			    }
			    else if(res == "notexist")
			    {
				 trans.clear();
				 ASPCommand cmd = trans.addCustomCommand("STATECHECKIN", "Edm_File_API.Delete_File_Ref_No_History");
				 cmd.addParameter("IN_1", doc_class);
				 cmd.addParameter("IN_1", doc_no);
				 cmd.addParameter("IN_1", doc_sheet);
				 cmd.addParameter("IN_1", doc_rev);
				 cmd.addParameter("IN_1", doc_type);
				 cmd.addParameter("IN_1", file_no);
				 trans = mgr.perform(trans);
    
				 addHistoryRecord(doc_class,doc_no,doc_sheet,doc_rev,"RESETSTATUS",mgr.translate("DOCMAWDOCISSUEFILEDELETED: File status is manually reset by deleting the file reference."));
			    }       		   

			}while (headset.next());

			headset.refreshAllRows();
			client_confirmation = false;
			//headset.goTo(curr_row);
			
		    }//else
		    headset.setFilterOff();
		}
		// Refresh the child blocks if requred..
		if (headlay.isSingleLayout())
		{
			if (tabs.getActiveTab()== 9)
			{
				okFindITEM9();
			}
			else if (tabs.getActiveTab()== 8)
			{
				okFindITEM7();
			}
		}
		
	    }//if not admin
	    else
	    {
		mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTADMIN: You need DOCMAN ADMINISTRATOR privileges to Reset File Status."));  

	    }

	}

	public void addHistoryRecord(String doc_class, String doc_no, String doc_sheet, String doc_rev, String info_cat, String note)
	{
	    ASPManager mgr = getASPManager();
	     trans.clear();
	     ASPCommand cmd = trans.addCustomCommand("STATECHECKIN", "Document_Issue_History_Api.Insert_New_Line_");
	     cmd.addParameter("IN_1", doc_class);
	     cmd.addParameter("IN_1", doc_no);
	     cmd.addParameter("IN_1", doc_sheet);
	     cmd.addParameter("IN_1", doc_rev);
	     cmd.addParameter("IN_1", info_cat);
	     cmd.addParameter("IN_1", note);
	     trans = mgr.perform(trans);

	}

	public String checkFileExistInRep(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type)
	{
	     ASPManager mgr = getASPManager();
    
	     trans = mgr.newASPTransactionBuffer();
    
	     ASPCommand cmd = trans.addCustomCommand("CHECKFILEEXIST", "Edm_File_Api.Check_Exist");
	     cmd.addParameter("OUT_1");
	     cmd.addParameter("IN_1", doc_class);
	     cmd.addParameter("IN_1", doc_no);
	     cmd.addParameter("IN_1", doc_sheet);
	     cmd.addParameter("IN_1", doc_rev);
	     cmd.addParameter("IN_1", doc_type);
    
    
	     trans = mgr.perform(trans);
	     String res = "";
	     String exist;
	     exist = trans.getValue("CHECKFILEEXIST/DATA/OUT_1");
    
	     trans.clear();
	     cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "DUMMY6");
	     cmd.addParameter("DOC_CLASS", doc_class);
	     cmd.addParameter("DOC_NO",    doc_no);
	     cmd.addParameter("DOC_SHEET", doc_sheet);
	     cmd.addParameter("DOC_REV",   doc_rev);
	     cmd.addParameter("DOC_TYPE", doc_type);
	     trans = mgr.perform(trans);
    
	     String edmRepInfo = trans.getValue("EDMREPINFO/DATA/DUMMY6");
    
	     String ftp_port = getStringAttribute(edmRepInfo, "LOCATION_PORT");
	     String ftp_address = getStringAttribute(edmRepInfo, "LOCATION_ADDRESS");
	     String ftp_user = getStringAttribute(edmRepInfo, "LOCATION_USER");
	     String ftp_password = getStringAttribute(edmRepInfo, "LOCATION_PASSWORD");
	     String local_file_name = getStringAttribute(edmRepInfo, "LOCAL_FILE_NAME");
	     String temp_path = getStringAttribute(edmRepInfo, "LOCAL_PATH");
	     String ftp_file_name = getStringAttribute(edmRepInfo, "REMOTE_FILE_NAME");
	     String location_type = getStringAttribute(edmRepInfo, "LOCATION_TYPE");
	     boolean result = false;
	    
    
	     if("TRUE".equals(exist))
	     {
       
		 if ("2".equals(location_type))
		 {
		    DocmawFtp ftp_server = new DocmawFtp();
    
		    int port_no = mgr.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);
			try
			{
			debug("ftp_address" + ftp_address);
			debug("ftp_user" + ftp_user);
			debug("ftp_password" + ftp_password);
			debug("port_no" + port_no);
		    
			result = ftp_server.login(ftp_address, ftp_user, ftp_password, port_no);

    
			}
			catch (Exception e)
			{
			    mgr.showAlert(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED1: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
		    
			}
			if(result)
			{
			
			try
			    {
	     
				if (!ftp_server.checkExist(ftp_file_name))
				{
				    ftp_server.logoff();
				    res = "notexist";

				}
				else
				{
				    res = "exist";
				    ftp_server.logoff();
				}
    
			    }
			    catch(Exception e)
			    {
				res = "notexist";
				
			    }
			}
			   
		     
		    
		 }
		 else if("1".equals(location_type))
		 {
		    mgr.showAlert(mgr.translate("DOCMAWDOCISSUESHAREDNOSUP: Shared repository is not supported by Docmaw. Please use Docman to reset this file."));
		 }
		 else
		 {
		     trans.clear();
		     cmd = trans.addCustomFunction("EXIST", "Edm_File_Storage_API.File_Exist", "OUT_1");
		     cmd.addParameter("IN_1", doc_class);
		     cmd.addParameter("IN_1", doc_no);
		     cmd.addParameter("IN_1", doc_sheet);
		     cmd.addParameter("IN_1", doc_rev);
		     cmd.addParameter("IN_1", doc_type);
		     trans= mgr.perform(trans);
    
		     if("TRUE".equals(trans.getValue("EXIST/DATA/OUT_1")))
		     {
			 res = "exist";
		     }
		     else
		     {
			 res = "notexist";
		     }
		 }
	    }
	    else
	    {
		 res = "notexist";
	    }
	     return res;
	}

	//Bug Id 77080, End


   protected void findSubDocuments()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      //Bug Id 85223, Start, Added Doc_sheet and doc_rev
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", mgr.readValue("SUB_DOC_CLASS"));
      q.addParameter("DOC_NO", mgr.readValue("SUB_DOC_NO"));
      q.addParameter("DOC_SHEET", mgr.readValue("SUB_DOC_SHEET"));
      q.addParameter("DOC_REV", mgr.readValue("SUB_DOC_REV"));
      //Bug Id 85223, End
      q.setOrderByClause("DOC_CLASS, DOC_NO, SHEET_ORDER, REV_NO DESC");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
      okFindITEM0();
   }

   //Bug Id 67105, Start
   protected void getMandatoryFieldsForQuery()
   {
	ASPManager mgr = getASPManager();

	trans.clear();
	cmd = trans.addCustomFunction("MANDATORYFIELDS", "DOCMAN_DEFAULT_API.Get_Default_Value_", "DUMMY3");
	cmd.addParameter("DUMMY1","DocIssue");
	cmd.addParameter("DUMMY2","DOC_ISSUE_MANDATORY_FIELDS");
	trans = mgr.perform(trans);
	sMandatoryFieldsList = trans.getValue("MANDATORYFIELDS/DATA/DUMMY3");
	    
	String sMandatoryField;
	String sField;

	if ((! mgr.isEmpty(sMandatoryFieldsList)) && stringIndex(sMandatoryFieldsList, "^") > 0) 
	{
	    
	   StringTokenizer st = new StringTokenizer(sMandatoryFieldsList, "^");
	   while (st.hasMoreTokens())
	   {
		sMandatoryField = st.nextToken();
		StringTokenizer stfields = new StringTokenizer(headblk.getFieldList(), ",");
		    
		while (stfields.hasMoreTokens())
		{
		   sField = stfields.nextToken();
		   if (sField.equals(sMandatoryField))
		   {
			if (mgr.isEmpty(sMandatoryFields)) 
			    sMandatoryFields = mgr.getASPField(sField).getLabel() + ",";
			else
			    sMandatoryFields = sMandatoryFields + " " + mgr.getASPField(sField).getLabel() + ",";
		    }
		 }
	     }
	     if (! mgr.isEmpty(sMandatoryFields)) 
		sMandatoryFields=sMandatoryFields.substring(0,sMandatoryFields.length()-1);
	 }
    }
    //Bug Id 67105, End

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      searchURL = mgr.createSearchURL(headblk);
      boolean bValueExists = true; //Bug Id 77651
      //Bug Id 67105, Start
      if (! mgr.isEmpty(sMandatoryFields)) 
      {
	    String sMandatoryField;
	    String sField;
            bValueExists = false; //Bug Id 77651

	    StringTokenizer st = new StringTokenizer(sMandatoryFieldsList, "^");
	    while (st.hasMoreTokens())
	    {
		sMandatoryField = st.nextToken();
		StringTokenizer stfields = new StringTokenizer(headblk.getFieldList(), ",");
		
		while (stfields.hasMoreTokens())
		{
		    sField = stfields.nextToken();
		    //Bug Id 77651, Start
		    if (sField.equals(sMandatoryField) && (! mgr.isEmpty(mgr.readValue(sField))) && (! "%".equals(mgr.readValue(sField))))
		    {
			bValueExists = true; 
			break;
		    }
		}
		if (bValueExists) 
		   break;
		//Bug Id 77651, End
	    }
	}
	//Bug Id 77651, Start
        if (bValueExists) 
	   bMandatoryFieldsEmpty = false;
	else
	   bMandatoryFieldsEmpty = true;
	//Bug Id 77651, End
	if (bMandatoryFieldsEmpty) 
	{
	    trans   = mgr.newASPTransactionBuffer();
	    headset.clear();
	    eval(headset.syncItemSets());
	    return;
	}
	//Bug Id 67105, End
        else
	{
	    trans.clear();
	    q = trans.addQuery(headblk);
	    q.setOrderByClause("DOC_CLASS, DOC_NO, SHEET_ORDER, REV_NO DESC");
	    q.includeMeta("ALL");
	
	    if (mgr.dataTransfered())
	    {
		q.addOrCondition(mgr.getTransferedData());
	    }
	
	    mgr.querySubmit(trans,headblk);
	
	    if (headset.countRows() == 0)
	    {
		mgr.showAlert(mgr.translate("DOCMAWDOCISSUENODATA: No data found."));
		eval(headset.syncItemSets());
		return;
	    }
	
	    eval(headset.syncItemSets());
	   okFindITEM0();
	}
   }


   public void findDocumentsInStructure(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();

      // build query to fetch all documents in structure..
      StringBuffer select_structure = new StringBuffer();

      // SQLInjections_Safe AMNILK 20070810
      select_structure.append("SELECT '");
      select_structure.append(     doc_class);
      select_structure.append(     "' DOC_CLASS, '");
      select_structure.append(     doc_no);
      select_structure.append(     "' DOC_NO, '");
      select_structure.append(     doc_sheet);
      select_structure.append(     "' DOC_SHEET, '");
      select_structure.append(     doc_rev);
      select_structure.append(     "' DOC_REV ");
      select_structure.append("FROM DUAL ");
      select_structure.append("UNION ALL ");
      select_structure.append(     "SELECT sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev ");
      select_structure.append(      "FROM doc_structure ");
      select_structure.append(      "CONNECT BY doc_class = PRIOR sub_doc_class ");
      select_structure.append(           "AND doc_no = PRIOR sub_doc_no ");
      select_structure.append(           "AND doc_sheet = PRIOR sub_doc_sheet ");
      select_structure.append(           "AND doc_rev = PRIOR sub_doc_rev ");
      select_structure.append(      "START WITH doc_class = ?");
      select_structure.append(         " AND doc_no = ?");
      select_structure.append(         " AND doc_sheet = ?");
      select_structure.append(         " AND doc_rev = ?");

      // retrieve structure..
      trans.clear();
      q = trans.addQuery("GET_STRUCTURE", select_structure.toString());
      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);

      trans = mgr.perform(trans);

      ASPBuffer doc_buf = trans.getBuffer("GET_STRUCTURE");

      // remove the last INFO item from this buffer..
      doc_buf.removeItemAt(doc_buf.countItems() - 1);

      // populate the master with the all documents in the structure..
      trans.clear();
      ASPQuery query = trans.addEmptyQuery(headblk);
      query.addOrCondition(doc_buf);
      query.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);

      // initialise the current row..
      headset.first();

      // activate title tab..
      refreshActiveTab();
   }


   public void showStructureInNavigator()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.selectRows();
      else
         headset.selectRow();

      ASPBuffer keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      sUrl = DocumentTransferHandler.getDataTransferUrl(mgr, "DocStructureNavigator.page", keys);
      bShowStructure = true;
   }


   public void setStructAttribAll()
   {
      ASPManager mgr = getASPManager();
   
      if (headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();
   
   
   
      headset.setFilterOn();
      trans.clear();
      for (int k = 0;k < headset.countSelectedRows();k++) {
         ASPCommand cmd = trans.addCustomCommand ("SETSTRUCTURE"+k, "DOC_TITLE_API.Set_Structure_All_");
         cmd.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getValue("DOC_NO"));
         headset.next();
      }
      trans = mgr.perform(trans);
      headset.setFilterOff();
      headset.unselectRows();
   
      refreshHeadset();
   
   }
   
   public void unsetStructAttribAll()
   {
      ASPManager mgr = getASPManager();
   
      if (headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();
   
      headset.setFilterOn();
      trans.clear();
      for (int k = 0;k < headset.countSelectedRows();k++) {
         ASPCommand cmd = trans.addCustomCommand ("SETSTRUCTURE"+k, "DOC_TITLE_API.Unset_Structure_All_");
         cmd.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getValue("DOC_NO"));
         headset.next();
      }
      trans = mgr.perform(trans);
      headset.setFilterOff();
      headset.unselectRows();
   
      refreshHeadset();
   }


   private void refreshHeadset()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      ASPQuery q = trans.addEmptyQuery(headblk);
      q.addOrCondition(headset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV"));
      q.setOrderByClause("DOC_CLASS, DOC_NO, SHEET_ORDER, REV_NO DESC");
      q.includeMeta("ALL");
      int row_no = headset.getCurrentRowNo();
      mgr.querySubmit(trans, headblk);
      headset.goTo(row_no);
      eval(headset.syncItemSets());
      okFindITEM0();
   }


   public void countFind()
   {
      ASPManager mgr = getASPManager();
      boolean bValueExists = true; //Bug Id 77651
      //Bug Id 67105, Start
      if (! mgr.isEmpty(sMandatoryFields)) 
      {
		    
	    String sMandatoryField;
	    String sField;

	    StringTokenizer st = new StringTokenizer(sMandatoryFieldsList, "^");
	    while (st.hasMoreTokens())
	    {
		sMandatoryField = st.nextToken();
		StringTokenizer stfields = new StringTokenizer(headblk.getFieldList(), ",");
		bValueExists = false; //Bug Id 77651		

		while (stfields.hasMoreTokens())
		{
		    sField = stfields.nextToken();
		    //Bug Id 77651, Start
		    if (sField.equals(sMandatoryField) && (! mgr.isEmpty(mgr.readValue(sField))) && (! "%".equals(mgr.readValue(sField))))
		    {
			bValueExists = true; 
			break;
		    }
		}
		if (bValueExists) 
		    break;
		//Bug Id 77651, End
	    }
	}
       	//Bug Id 77651, Start
        if (bValueExists) 
	   bMandatoryFieldsEmpty = false;
	else
	   bMandatoryFieldsEmpty = true;
	//Bug Id 77651, End
	if (bMandatoryFieldsEmpty) 
	{
	    trans   = mgr.newASPTransactionBuffer();
	    headset.clear();
	    eval(headset.syncItemSets());
	    return;
	}
        //Bug Id 67105, End
	else
	{
	    q = trans.addQuery(headblk);
	    q.setSelectList("to_char(count(*)) N");
	    mgr.submit(trans);
	    headlay.setCountValue(toInt(headset.getValue("N")));
	    headset.clear();
	}
   }


   public void okFindITEM0()
   {
      ASPManager mgr = getASPManager();
      int k = tabs.getActiveTab();

      if (k == 1)
      {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addEmptyQuery(itemblk0);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
   }


   public void countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ?");
      q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO",headset.getValue("DOC_NO"));
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }


   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      if (tabs.getActiveTab()== 2)
      {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addQuery(itemblk1);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO",headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV",headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getValue("N")));
      itemset1.clear();
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","DOC_ISSUE_ORIGINAL_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }


   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      if (tabs.getActiveTab()== 3)
      {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addQuery(itemblk2);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans,itemblk2);
         headset.goTo(headrowno);
      }
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      //Bug ID 45944, inoslk, start
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO",headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV",headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getValue("N")));
      itemset2.clear();
   }


   public void  newRowITEM2()
   {
      bObjectConnection = true;
      itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
   }


   public void  insertNewObject()
   {
      ASPManager mgr = getASPManager();
      String[] sKeyRefArray;
      String sLuName = mgr.readValue("TEMP_LU_NAME");
      String sKeyRefStr = mgr.readValue("TEMP_KEY_REF");  
      String sKeyRef = null;
      String sDecodedLastDocRev;
      StringTokenizer st = null;
      int nTmpCounter = 0; //used to create unique buffer names.
      int nSelectedRows = 0;
      int headrowno = 0;//Bug Id:68450

      /*the stored selections (in method connectObject()) will be retrieved here in detail mode.*/
      if (headlay.isMultirowLayout())
      {
         headset.setFilterOn();
         nSelectedRows = headset.countSelectedRows();
         headset.first();
       }
      else{//Bug Id:68450, start
         nSelectedRows = 1;
         headrowno = headset.getCurrentRowNo();
      }//Bug Id:68450, end
         
            
      for (int a=0; a<nSelectedRows ; a++)
      {      
         //split() method cannot be used for pipe charactor.therefore a tokenizer is used.
         st = new StringTokenizer(sKeyRefStr, "||"); 
         while (st.hasMoreTokens())
         {
            sKeyRef = st.nextToken();
            trans.clear();

            //gets the default values for the mandatory items

            cmd = trans.addCustomFunction("LASTDOCREV", "Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_", "KEEP_LAST_DOC_REV");
            cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));            
            //used to get the default value using the same db call in case retrived value is  null.
            cmd = trans.addCustomFunction("DEFAULTLASTDOCREVDECODE", "ALWAYS_LAST_DOC_REV_API.Decode", "KEEP_LAST_DOC_REV");
            cmd.addParameter("DUMMY1","F");
            cmd = trans.addCustomFunction("LASTDOCREVDECODE", "ALWAYS_LAST_DOC_REV_API.Decode", "KEEP_LAST_DOC_REV");
            cmd.addInReference("DUMMY1","LASTDOCREV/DATA","KEEP_LAST_DOC_REV");

            cmd = trans.addEmptyCommand("ITEM2" + nTmpCounter, "DOC_REFERENCE_OBJECT_API.New__", itemblk2);
            cmd.setOption("ACTION", "PREPARE");
            cmd.setParameter("LU_NAME", sLuName);
            cmd.setParameter("KEY_REF", sKeyRef);
            cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
            cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
            cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
            cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));         
            trans = mgr.perform(trans); 
            //adds the mandatory items to the rowset to be submitted to the server when 'DO' action is invoked.
            itemset2.addRow(trans.getBuffer("ITEM2" + nTmpCounter + "/DATA"));

            sDecodedLastDocRev = trans.getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");
            if (mgr.isEmpty(sDecodedLastDocRev))
               sDecodedLastDocRev = trans.getValue("DEFAULTLASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");

            //Bug Id 65675, Start
            String[] keyRefArray;
            keyRefArray=split(sKeyRef.substring(0,sKeyRef.length()-1),'^');//

            String keyValues = "";
            for (int i = 0; i < keyRefArray.length; i++)
            {
                    keyValues = keyValues + (keyRefArray[i].substring(keyRefArray[i].indexOf("=")+1, keyRefArray[i].length()));
                    keyValues = keyValues + "^";
            }
            //Bug Id 65675, End

            data = itemset2.getRow();
            data.setFieldItem("KEEP_LAST_DOC_REV", sDecodedLastDocRev);
            data.setFieldItem("KEY_VALUE",keyValues);    //Bug Id 65675
            itemset2.setRow(data);


            trans.clear();
            nTmpCounter ++;
         }      
         headset.next();
      }
      headset.setFilterOff();      
      
      //submit the changes if at least one object is inserted.
      if (nTmpCounter > 0) 
      {
         mgr.submit(trans);
         //Bug Id:68450, start
         if (headlay.isSingleLayout())
         {
            headset.goTo(headrowno);
         }
         //Bug Id:68450, end
      }
      itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
   }

   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();
      if (tabs.getActiveTab()== 4)
      {
         if (headset.countRows() == 0)
            return;

         trans.clear();
         q = trans.addEmptyQuery(itemblk3);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans, itemblk3);
         headset.goTo(headrowno);
      }
   }


   public void countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      //Bug ID 45944, inoslk, start
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO",headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV",headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getValue("N")));
      itemset3.clear();
   }


   public void  newRowITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM3","DOC_STRUCTURE_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
      cmd.setParameter("DOC_NO",headset.getValue("DOC_NO"));
      cmd.setParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
      cmd.setParameter("DOC_REV",headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      itemset3.addRow(data);

      if (bItem3Duplicate)
      {
         data = itemset3.getRow();
         trans.clear();
         cmd = trans.addCustomFunction("GETTITLE", "DOC_TITLE_API.Get_Title", "SSUBDOCTITLE");
         cmd.addParameter("SUB_DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
         cmd.addParameter("SUB_DOC_NO", itemset3.getValue("SUB_DOC_NO"));
         cmd = trans.addCustomFunction("GETNOSUBDOCS", "DOC_STRUCTURE_API.Number_Of_Children_", "NNOOFCHILDREN");
         cmd.addParameter("SUB_DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
         cmd.addParameter("SUB_DOC_NO", itemset3.getValue("SUB_DOC_NO"));
         cmd.addParameter("SUB_DOC_SHEET", itemset3.getValue("SUB_DOC_SHEET"));
         cmd.addParameter("SUB_DOC_REV", itemset3.getValue("SUB_DOC_REV"));
         trans = mgr.perform(trans);
         data.setFieldItem("SSUBDOCTITLE",trans.getValue("GETTITLE/DATA/SSUBDOCTITLE"));
         data.setFieldItem("NNOOFCHILDREN",trans.getValue("GETNOSUBDOCS/DATA/NNOOFCHILDREN"));
         itemset3.setRow(data);
         trans.clear();
      }
   }


   public void newRowITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM4","DOC_STRUCTURE_API.New__",itemblk4);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("SUB_DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("SUB_DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("SUB_DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("SUB_DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM4/DATA");
      itemset4.addRow(data);
   }


   public void duplicateRowITEM4()
   {
      if (itemlay4.isMultirowLayout())
         itemset4.goTo(itemset4.getRowSelected());

      ASPBuffer data = itemset4.getRow();
      itemset4.addRow(data);
      itemlay4.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
   }


   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      if (tabs.getActiveTab()== 5)
      {
         if (headset.countRows() == 0)
            return;

         trans.clear();
         q = trans.addQuery(itemblk4);
         q.addWhereCondition("SUB_DOC_CLASS = ? AND SUB_DOC_NO = ? AND SUB_DOC_SHEET = ? AND SUB_DOC_REV = ?");
         q.addParameter("SUB_DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("SUB_DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("SUB_DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("SUB_DOC_REV", headset.getValue("DOC_REV") );
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans, itemblk4);
         headset.goTo(headrowno);
      }
   }


   public void  countFindITEM4()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk4);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("SUB_DOC_CLASS = ? AND SUB_DOC_NO = ? AND SUB_DOC_SHEET = ? AND SUB_DOC_REV = ?");
      q.addParameter("SUB_DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("SUB_DOC_NO",headset.getValue("DOC_NO"));
      q.addParameter("SUB_DOC_SHEET",headset.getValue("DOC_SHEET"));
      q.addParameter("SUB_DOC_REV",headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay4.setCountValue(toInt(itemset4.getValue("N")));
      itemset4.clear();
   }


   public void  okFindITEM5()
   {
      ASPManager mgr = getASPManager();
      if (tabs.getActiveTab()== 6)
      {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addEmptyQuery(itemblk5);
         q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
         q.addParameter("LU_NAME", headset.getValue("LU_NAME"));
         q.addParameter("KEY_REF", headset.getValue("KEY_REF"));
         q.setOrderByClause("STEP_NO, LINE_NO");
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans,itemblk5);
         headset.goTo(headrowno);
      }
   }


   public void  countFindITEM5()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk5);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
      q.addParameter("LU_NAME", headset.getValue("LU_NAME"));
      q.addParameter("KEY_REF", headset.getValue("KEY_REF"));
      mgr.submit(trans);
      itemlay5.setCountValue(toInt(itemset5.getValue("N")));
      itemset5.clear();
   }


   public void  newRowITEM5()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM5","APPROVAL_ROUTING_API.New__",itemblk5);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("LU_NAME",headset.getValue("LU_NAME"));
      cmd.setParameter("KEY_REF",headset.getValue("KEY_REF"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM5/DATA");
      itemset5.addRow(data);

      if (bItem5Duplicate)
      {
         data = itemset5.getRow();
         trans.clear();
         cmd = trans.addCustomFunction("GETPERNAME","PERSON_INFO_API.Get_Name","ROUTE_SIGN_NAME");
         cmd.addParameter("PERSON_ID",itemset5.getValue("PERSON_ID"));
         trans = mgr.perform(trans);
         data.setFieldItem("ROUTE_SIGN_NAME",trans.getValue("GETPERNAME/DATA/ROUTE_SIGN_NAME"));
         itemset5.setRow(data);
         trans.clear();
      }
   }

   //Bug Id 74985, Start
   public void editITEM5()
   {
      itemset5.goTo(itemset5.getRowSelected());
      itemlay5.setLayoutMode(itemlay5.EDIT_LAYOUT);
   }
   //Bug Id 74985, End

   public void cancelFind()
   {
      ASPManager mgr = getASPManager();
      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }


   public boolean isStructure(String docClass,
                               String docNo)
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("TITLESTRUCTURE", "Doc_Title_Api.Get_Structure_","STRUCTURE_TYPE");
      cmd.addParameter("DOC_CLASS", docClass);
      cmd.addParameter("DOC_NO", docNo);

      trans = mgr.perform(trans);
      double  structure_type  = trans.getNumberValue("TITLESTRUCTURE/DATA/STRUCTURE_TYPE");
      trans.clear();
      
      return ((int)structure_type==1);

   }


   public void saveReturnITEM3() throws FndException
   {
      ASPManager mgr = getASPManager();

      if (ctx.readFlag("REPLACE_REVISION", false))
      {
         // Get keys of revision being replaced..

         

         ctx.writeValue("REPLACE_DOC_CLASS",itemset3.getValue("SUB_DOC_CLASS"));
         ctx.writeValue("REPLACE_DOC_NO",   itemset3.getValue("SUB_DOC_NO"));
         ctx.writeValue("REPLACE_DOC_SHEET",itemset3.getValue("SUB_DOC_SHEET"));
         ctx.writeValue("REPLACE_DOC_REV",  itemset3.getValue("SUB_DOC_REV"));

         itemset3.changeRow();

         // Save replacing revision's data from the current to the current row..
         //check if the replace doc is of structure type
         if (this.isStructure(itemset3.getValue("SUB_DOC_CLASS"),itemset3.getValue("SUB_DOC_NO"))) {
            
            replaceRevisionFinish();
         }
         else{
            bConfiramtion4SettingStructureType = true;
            ctx.writeValue("CONFIRMFUNC","replaceRevInStructure()");
            ctx.writeValue("UNCONFIRMFUNC","okFindITEM3()");
            //bConfirmEx = true;
            sMessage =  mgr.translate("MADESTRUCTURETYPE: The Document Revision you have selected to replace the source document will be made Structure Type.\\nPress Ok to continue.\\nPress Cancel to abort this operation.");
         }
      }
      else
      {
         int head_row = headset.getCurrentRowNo();
         int row = itemset3.getCurrentRowNo();
         itemset3.changeRow();
         mgr.submit(trans);
         headset.goTo(head_row);
         itemset3.goTo(row);
      }

      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE")))
      {
         // Repopulate the structure..
         int row = headset.getCurrentRowNo();
         headset.first();
         findDocumentsInStructure(headset.getValue("DOC_CLASS"), headset.getValue("DOC_NO"), headset.getValue("DOC_SHEET"), headset.getValue("DOC_REV"));
         headset.goTo(row);

         // Refresh the navigator to reflect the new/changed revision..
         appendDirtyJavaScript("window.parent.DocStructureNavigatorTree.refreshNavigator();\n");
      }
   }


   public void saveReturnITEM5()
   {
      savetype="saveReturn";
      saveITEM5();
   }


   public void saveNewITEM5()
   {
      ASPManager mgr = getASPManager();
      savetype="saveNew";
      saveITEM5();
   }


   public void  saveReturnITEM()
   {
      ASPManager mgr = getASPManager();

      String head_lu_name = headset.getValue("LU_NAME");
      String head_key_ref = headset.getValue("KEY_REF");

      trans.clear();
      cmd = trans.addCustomCommand("CHECKAPP", "APPROVAL_ROUTING_API.Check_App_Profile");
      cmd.addParameter("DUMMY1");
      cmd.addParameter("DUMMY2");
      cmd.addParameter("LU_NAME",head_lu_name);
      cmd.addParameter("KEY_REF",head_key_ref);
      cmd.addParameter("PROFILE_ID",mgr.readValue("PROFILE_ID"));
      trans = mgr.perform(trans);

      String persons=trans.getValue("CHECKAPP/DATA/DUMMY1");
      String groups=trans.getValue("CHECKAPP/DATA/DUMMY2");

      if (!mgr.isEmpty(persons) || !mgr.isEmpty(groups))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEAPPTEMPLATEACCESS: The user(s) and/or Group(s) in the Approval Template who didn't have View Access to the document(s) have been given that right. Please verify that this is correct under the Access tab."));
      }
      
      int row = headset.getCurrentRowNo();
      trans.clear();

      headset.changeRow();
      mgr.submit(trans);

      headset.goTo(row);
      performRefreshParent();
   }


   public void  saveITEM5()
   {
      ASPManager mgr = getASPManager();

      itemset5.changeRow();

      if ("New__".equals(itemset5.getRowStatus())) 
      {
         trans.clear();
         cmd = trans.addEmptyCommand("ITEM5","APPROVAL_ROUTING_API.New__",itemblk5);
         cmd.setOption("ACTION","CHECK");
         cmd.setParameter("LU_NAME",itemset5.getValue("LU_NAME"));
         cmd.setParameter("KEY_REF",itemset5.getValue("KEY_REF"));
         cmd.setParameter("STEP_NO",mgr.readValue("STEP_NO"));
         cmd.setParameter("DESCRIPTION",mgr.readValue("DESCRIPTION"));
         cmd.setParameter("PERSON_ID",mgr.readValue("PERSON_ID"));
         cmd.setParameter("GROUP_ID",mgr.readValue("GROUP_ID"));
         cmd.setParameter("LINE_NO",itemset5.getValue("LINE_NO"));

         trans = mgr.perform(trans);

         String sInfo = trans.getValue("ITEM5/DATA/INFO__");
         if (!mgr.isEmpty(sInfo))
         {
            bConfirm = true;
            sMessage = sInfo.substring(8,sInfo.length()-9);
            ctx.writeValue("CONFIRMFUNC","saveLast()");
            ctx.writeValue("UNCONFIRMFUNC","unConfirmITEM5Save()");
         }
         else
            saveLast();
      }
      //Bug Id 73162 Start
      else if ("Modify__".equals(itemset5.getRowStatus()))
      {
         debug("debug inside modify__");

         int currrow = itemset5.getCurrentRowNo();
         
        
         cmd = trans.addCustomCommand("ITEM55","APPROVAL_ROUTING_API.Modify__");
         cmd.addParameter("DUMMY1");
         cmd.addParameter("OBJID", itemset5.getValue("OBJID"));
         cmd.addParameter("OBJVERSION", itemset5.getValue("OBJVERSION"));			       		
         String fieldList = "DESCRIPTION,PERSON_ID,GROUP_ID,NOTE";
         
         String attr = dataRecordFetchEdited(fieldList);
         
         cmd.addParameter("ATTR", attr);
         cmd.addParameter("ACTION", "CHECK");

         trans = mgr.perform(trans);

         String sInfo = trans.getValue("ITEM55/DATA/DUMMY1");           
         if (!mgr.isEmpty(sInfo))                                        
         {                                                               
                                                                                                                              
                 bConfirm = true;                                        
                 sMessage = sInfo.substring(8,sInfo.length()-2);         
                                                                          
                 ctx.writeValue("CONFIRMFUNC","saveLast()");             
                 ctx.writeValue("UNCONFIRMFUNC","unConfirmITEM5Save()"); 
         }                                                             
         else                                                           
         {                                                              
               saveLast();                                              
         }                                                              
      }
      //Bug Id 73162 End
   }

   //Bug Id 73162 Start
   private String dataRecordFetchEdited(String fieldList)
   {

     String attrString = "";
     String attrValue  = "";
     ASPField field = null;
     ASPManager mgr = getASPManager();

     String [] editableAttributes = split(fieldList,""+',');

     for (int i=0; i<editableAttributes.length; i++)
     {

         field= mgr.getASPField(editableAttributes[i]);

        // Note: Add all the attributes not having SetFunction
       
       
        if (!field.hasSetFunction() && isFieldEdited(editableAttributes[i]))
        {
           attrValue = itemset5.getValue(editableAttributes[i]);
           attrValue = (attrValue == null)?"":attrValue;
           attrString = attrString + editableAttributes[i] + DocmawUtil.FIELD_SEPARATOR + attrValue + DocmawUtil.RECORD_SEPARATOR;
           
        }
       
     }
     return attrString;
   }

   private boolean isFieldEdited(String fieldName)
   {
      return !isEqual(itemset5.getDbValue(fieldName), itemset5.getValue(fieldName));
   }
   boolean isEqual( String s1, String s2) 
   {
      ASPManager mgr = getASPManager();
      if (mgr.isEmpty(s1))
      {
         if (mgr.isEmpty(s2))
         {
    	return true;
         }
         return false;
      }
      return(s1.equals(s2)); 
   }
   //Bug Id 73162 End

   public void  saveLast()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      int currrow = headset.getCurrentRowNo();
      mgr.submit(trans);
      trans.clear();
      headset.goTo(currrow);
      okFindITEM6();

      if ("saveNew".equals(savetype))
      {
         newRowITEM5();
      }
   }

   public void saveReturnITEM0()
   {
      ASPManager mgr = getASPManager();
      int row = headset.getCurrentRowNo();

      trans.clear();
      itemset0.changeRow();
      mgr.submit(trans);

      headset.goTo(row);
      performRefreshParent();
   }

   //Bug Id 78800, start
   public void saveReturnITEM10()
   {
      ASPManager mgr = getASPManager();
      int row = headset.getCurrentRowNo();

      trans.clear();
      itemset10.changeRow();
      mgr.submit(trans);

      headset.goTo(row);
      itemset10.refreshAllRows();
   }
   //Bug Id 78800, end

   public void  unConfirmITEM5Save()
   {
      ASPManager mgr = getASPManager();

      onUnconfirm();
      itemset5.clear();
      trans.clear();
      q = trans.addEmptyQuery(itemblk5);
      q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
      q.addParameter("LU_NAME", headset.getValue("LU_NAME"));
      q.addParameter("KEY_REF", headset.getValue("KEY_REF"));
      q.setOrderByClause("STEP_NO, LINE_NO");
      q.includeMeta("ALL");
      int headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
      itemlay5.setLayoutMode(4);

   }


   public void  okFindITEM6()
   {
      ASPManager mgr = getASPManager();
      if (tabs.getActiveTab()== 7)
      {
         if (headset.countRows() == 0)
            return;

         trans.clear();
         q = trans.addQuery(itemblk6);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
         q.setOrderByClause("ACCESS_OWNER DESC, GROUP_ID, PERSON_ID");
         q.setBufferSize(1000);
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
   }


   public void  countFindITEM6()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk6);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO",headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV",headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay6.setCountValue(toInt(itemset6.getValue("N")));
      itemset6.clear();
   }


   public void  newRowITEM6()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM6", "DOCUMENT_ISSUE_ACCESS_API.New__", itemblk6);
      cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM6/DATA");
      itemset6.addRow(data);

      if (bItem6Duplicate)
      {
         data = itemset6.getRow();
         trans.clear();
         cmd = trans.addCustomFunction( "GETNAME", "PERSON_INFO_API.Get_Name", "PERSONNAME" );
         cmd.addParameter("ITEM6_PERSON_ID");
         cmd = trans.addCustomFunction( "GETUSER", "PERSON_INFO_API.Get_User_Id", "PERSONUSERID" );
         cmd.addParameter("ITEM6_PERSON_ID");
         cmd = trans.addCustomFunction( "GETGROUPDES", "DOCUMENT_GROUP_API.Get_Group_Description", "GROUPDESCRIPTION" );
         cmd.addParameter("ITEM6_GROUP_ID");
         trans = mgr.perform(trans);
         data.setFieldItem("PERSONNAME",trans.getValue("GETNAME/DATA/PERSONNAME"));
         data.setFieldItem("PERSONUSERID",trans.getValue("GETUSER/DATA/PERSONUSERID"));
         data.setFieldItem("GROUPDESCRIPTION",trans.getValue("GETGROUPDES/DATA/GROUPDESCRIPTION"));
         itemset6.setRow(data);
         trans.clear();
      }
   }


   public void  deleteRowITEM6()
   {
      ASPManager mgr = getASPManager();

      if (itemlay6.isMultirowLayout())
      {
         itemset6.storeSelections();
         itemset6.setFilterOn();
      }

      trans.clear();
      cmd = trans.addCustomCommand("CHECKDEL","DOCUMENT_ISSUE_ACCESS_API.REMOVE__");
      cmd.addParameter("INFO", null);
      cmd.addParameter("OBJID", itemset6.getValue("OBJID"));
      cmd.addParameter("OBJVERSION", itemset6.getValue("OBJVERSION"));
      cmd.addParameter("ACTION","CHECK");
      trans = mgr.perform(trans);

      String sInfo = trans.getValue("CHECKDEL/DATA/INFO");
      trans.clear();
      itemset6.setFilterOff();
      if (!mgr.isEmpty(sInfo))
      {
         bConfirm = true;
         sMessage = DocmawUtil.getItemValue(sInfo, "WARNING");
         ctx.writeValue("CONFIRMFUNC","deleteITEM6Last()");
         ctx.writeValue("UNCONFIRMFUNC","onUnconfirm()");
      }
      else
         deleteITEM6Last();
   }


   public void  deleteITEM6Last()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      itemset6.setSelectedRowsRemoved();
      int currrow = headset.getCurrentRowNo();
      mgr.submit(trans);
      trans.clear();
      headset.goTo(currrow);
   }


   public String  generateHistoryWhereCond()
   {

      switch (sHistoryMode.charAt(0))
      {
      case '1' :
         return " info_category_db IN ('CHECKIN', 'CHECKOUT', 'UNRESERVED','DELETEFILE')";
      case '2' :
         return " info_category_db = 'ACCESS'";
      case '3' :
         return " info_category_db NOT IN ('ACCESS', 'CHECKIN', 'CHECKOUT', 'UNRESERVED', 'INFO', 'APPROVED', 'REJECTED', 'APPROVALSTEPREMOVED','DELETEFILE')";
      case '4' :
         return " info_category_db IN ('APPROVED', 'REJECTED','APPROVALSTEPREMOVED')";
      case '5' :
         return " info_category_db = 'INFO' ";
      case '6' :
         return " info_category_db IN ('CONNECTED', 'DISCONNECTED')";
      default :
         return "";
      }
   }


   public void  historyModeChanged()
   {
      ASPManager mgr = getASPManager();

      sHistoryMode = mgr.readValue("HISTORY_MODE");
      okFindITEM7();
   }


   public void  okFindITEM7()
   {
      ASPManager mgr = getASPManager();

      if (tabs.getActiveTab()== 8)
      {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addQuery(itemblk7);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
         q.addWhereCondition(generateHistoryWhereCond());	//SQLInjections_Safe AMNILK 20070810
         q.setOrderByClause("LINE_NO DESC");
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans,itemblk7); // Bug Id 78853
         headset.goTo(headrowno);
      }
   }


   public void countFindITEM7()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk7);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO",headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV",headset.getValue("DOC_REV"));
      q.addWhereCondition(generateHistoryWhereCond());		//SQLInjections_Safe AMNILK 20070810
      mgr.submit(trans);
      itemlay7.setCountValue(toInt(itemset7.getValue("N")));
      itemset7.clear();
   }


   public void  newRowITEM7()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM7", "DOCUMENT_ISSUE_HISTORY_API.New__", itemblk7);
      cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM7/DATA");
      itemset7.addRow(data);
   }

   //Bug Id 78800, start
   public void  newRowITEM10()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM10", "DOC_TRANSMITTAL_ISSUE_API.New__", itemblk10);
      cmd.setOption("ACTION", "PREPARE");
      cmd.setParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM10/DATA");
      itemset10.addRow(data);
   }
   //Bug Id 78800, end

   public void  countFindITEM8()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk9);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
      mgr.submit(trans);
      itemlay9.setCountValue(toInt(itemset9.getValue("N")));
      itemset9.clear();
   }


   public void  okFindITEM9()
   {
      ASPManager mgr = getASPManager();

      if (tabs.getActiveTab()== 9)
      {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addQuery(itemblk9);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
   }


   
   public void  okFindITEM10()
   {
      ASPManager mgr = getASPManager();

      if (tabs.getActiveTab()== 10)
      {
         if (headset.countRows() == 0)
            return;
         trans.clear();
         q = trans.addQuery(itemblk10);
         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
   }


   protected void initializeSession() throws FndException
   {
      ASPManager mgr = getASPManager();
      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

      sCheckedIn      = dm_const.edm_file_check_in;
      sCheckedOut     = dm_const.edm_file_check_out;
      sInBriefcase    = dm_const.edm_file_in_briefcase;
      sCheckedOutToBc = dm_const.edm_file_checked_out_to_bc;
      sPrelimin       = dm_const.doc_issue_preliminary;
      sAppInProg      = dm_const.doc_issue_approval_in_progress;
      sApproved       = dm_const.doc_issue_approved;
      sObsolete       = dm_const.doc_issue_obsolete;
      sReleased       = dm_const.doc_issue_released;
      dGroup          = dm_const.doc_user_access_group;
      dAll            = dm_const.doc_user_access_all;
      dUser           = dm_const.doc_user_access_user;
      sBcCreated      = dm_const.doc_briefcase_created;

      String initialised = ctx.readValue("INITIALISED", null);
      String fnd_user  = ctx.readValue("FNDUSER", null);
      // Bug 57779, Start
      String person_id = ctx.readValue("PERSONID",null);
      boolean docman_admin = ctx.readFlag("DOCMAN_ADMIN", false); 
      // Bug 57779, End
      boolean show_user_settings = ctx.readFlag("SHOW_USER_SETTINGS", false);
      boolean show_mandatory_settings = ctx.readFlag("SHOW_MANDATORY_SETTINGS", false); //Bug Id 67105

      if (mgr.isEmpty(initialised))
      {
         trans.clear();
         cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_Api.Get_Fnd_User", "DUMMY1");
         // Bug 57779, Start
         cmd = trans.addCustomFunction("DOCMAN_ADMIN", "Docman_Security_Util_API.Check_Docman_Administrator", "DUMMY1");
         // Bug 57779, End
         cmd = trans.addCustomFunction("PERSONID","Person_Info_API.Get_Id_For_User","DUMMY1");
         cmd.addParameter("USER_ID", fnd_user);

         trans.addPresentationObjectQuery("DOCMAW/dlgUserSettings.page,DOCMAW/MandatorySearchFieldsConfigDlg.page");//Bug Id 67105, Added MandatorySearchFieldsConfigDlg.page
         trans = mgr.perform(trans);

         fnd_user  = trans.getValue("FNDUSER/DATA/DUMMY1");
         // Bug 57779, Start
         person_id = trans.getValue("PERSONID/DATA/DUMMY1");
         docman_admin = "TRUE".equals(trans.getValue("DOCMAN_ADMIN/DATA/DUMMY1"));
         // Bug 57779, End

         if (trans.getSecurityInfo().namedItemExists("DOCMAW/dlgUserSettings.page"))
            show_user_settings = true;
         else
            show_user_settings =  false;
	
	 //Bug Id 67105, Start
	 if (trans.getSecurityInfo().namedItemExists("DOCMAW/MandatorySearchFieldsConfigDlg.page"))
		show_mandatory_settings = true;
	 else
		show_mandatory_settings =  false;
	 //Bug Id 67105, End
      }

      ctx.writeValue("FNDUSER", fnd_user);
      // Bug 57779, Start
      ctx.writeFlag("DOCMAN_ADMIN", docman_admin);
      // Bug 57779, End
      ctx.writeValue("PERSONID", person_id);
      ctx.writeFlag("SHOW_USER_SETTINGS", show_user_settings);
      ctx.writeFlag("SHOW_MANDATORY_SETTINGS", show_mandatory_settings);//Bug Id 67105
      ctx.writeValue("MODE", mgr.readValue("MODE", ctx.readValue("MODE", "")));
   }



   /**
    * isValidColumnValue() validates the specified column. The validation depends on whether
    * match is true or false:
    *
    *    - match is true  : the method returns true if the value of the parameters, column and
    *                       validity_check, always match.
    *    - match is false : the method returns true if the value of the parameters, column and
    *                       validity_check, always mismatch
    *  Works for single and multiple rows
    */
   public boolean isValidColumnValue(String column, String validity_check, boolean match)
   {

      boolean invalid = true;
      int noOFSelectedRows = 1;

      if (headlay.isSingleLayout())
      {
         headset.selectRow();
      }
      else
      {
         headset.storeSelections();
         headset.setFilterOn();
         noOFSelectedRows =  headset.countRows();
      }

      for (int k=0;k<noOFSelectedRows;k++)
      {
         if ((match && !validity_check.equals(headset.getRow().getValue(column))) ||
             (!match && validity_check.equals(headset.getRow().getValue(column))))
         {
            invalid = false;
            break;
         }
         if (headlay.isMultirowLayout())
         {
            headset.next();
         }

      }

      if (headlay.isMultirowLayout())
      {
         headset.setFilterOff();
      }
      return invalid;
   }



   /**
    * isValidColumnValue() validates the specified column. The validation depends on whether
    * match is true or false:
    *
    *    - match is true  : the method returns true if the value of the parameters, column and
    *                       validity_check1 *or* validity_check2, always match.
    *    - match is false : the method returns true if the value of the parameters, column and
    *                       validity_check1 *or* validity_check2, always mismatch
    *  Works for single and multiple rows
    */
   public boolean isValidColumnValue(String column, String validity_check1, String validity_check2, boolean match)
   {

      boolean invalid = true;
      int noOFSelectedRows = 1;

      if (headlay.isSingleLayout())
      {
         headset.selectRow();
      }
      else
      {
         headset.storeSelections();
         headset.setFilterOn();
         noOFSelectedRows = headset.countRows();
      }
      for (int k=0;k<noOFSelectedRows;k++)
      {
         if ((match &&(!validity_check1.equals(headset.getRow().getValue(column)) && !validity_check2.equals(headset.getRow().getValue(column)) ))
             || (!match &&(validity_check1.equals(headset.getRow().getValue(column)) || validity_check2.equals(headset.getRow().getValue(column)) )))
         {
            invalid = false;
            break;
         }
         if (headlay.isMultirowLayout())
         {
            headset.next();
         }

      }
      if (headlay.isMultirowLayout())
      {
         headset.setFilterOff();
      }
      return invalid;
   }


   /**
    * isEmptyColumnValue() checks to see if the specified column is empty.
    *
    * @param column
    *
    * @return Returns true if the value of the specified column in empty for
    * any of the rows, false otherwise
    */
   public boolean isEmptyColumnValue(String column)
   {

      ASPManager mgr = getASPManager();
      boolean empty = false;
      int noOfRowsSelected = 1;

      if (headlay.isSingleLayout())
      {
         headset.selectRow();
      }
      else
      {
         headset.selectRows();
         headset.setFilterOn();
         noOfRowsSelected = headset.countRows();
      }


      for (int k=0;k<noOfRowsSelected;k++)
      {
         if (mgr.isEmpty(headset.getRow().getValue(column)))
         {
            empty = true;
            break;
         }
         if (headlay.isMultirowLayout())
         {
            headset.next();
         }
      }

      if (headlay.isMultirowLayout())
      {
         headset.setFilterOff();
      }
      return empty;
   }

   public void getClientConformation(String conf_msg, String calling_method)
   {
      bConfirm = true;
      sMessage = conf_msg;
      ctx.writeValue("CONFIRMFUNC", calling_method);
   }


   public void getClientConformationEx(String conf_msg, String calling_method)
   {
      bConfirmEx = true;
      sMessage = conf_msg;
      ctx.writeValue("CONFIRMFUNC", calling_method);
   }


   /**
    * Approves selected document(s). The document state is set to
    * 'Approval In Progress'
    *
    */
   public void  startApproval()
   {
      ASPManager mgr = getASPManager();
      boolean bAccessUser = false;
      int curr_row=0;

      

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         if (headset.countSelectedRows() == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
            return;
         }
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
         curr_row = headset.getCurrentRowNo();
      }


      if (!client_confirmation && !isValidColumnValue("STATE", sPrelimin, true))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEPRIMCANSTARTAPP: Only documents in state Preliminary can be started for approval."));
      else
      {
	  //Bug id 70512 Start
	  if ((!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",false)) || (isValidColumnValue("REDLINE_FILE_STATUS","TRUE",true))) && (!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",true))))
	  {
	  mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTSTARTAPPCOMM: Cannot start the approval process when comment file is checked out."));
	  return;
	  }
	  //Bug id 70512 End
	 

         boolean bDocInBriefcase = false;

         headset.setFilterOn();
         headset.first();
         do
         {
            if ( dUser.equals(headset.getRow().getValue("ACCESS_CONTROL")) && !bAccessUser )
               bAccessUser = true;

            if (!mgr.isEmpty(headset.getRow().getValue("BRIEFCASE_NO")))
            {
               if (mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")))
               {
                  bDocInBriefcase = true;
                  break;
               }
               else if ((headset.getRow().getValue("SEDMSTATUS").equals(sInBriefcase)) || (headset.getRow().getValue("SEDMSTATUS").equals(sCheckedOutToBc)))
               {
                  bDocInBriefcase = true;
                  break;
               }
            }
         }
         while (headset.next());

         if (bDocInBriefcase)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTSTARTAPPPROCFORDOCINBC: At least one of the documents you have selected is added to a briefcase. You must unlock the document(s) first in order to start the Approval Process."));
            headset.setFilterOff();
            return;
         }
         if (!client_confirmation)
         {
            if (bAccessUser)
            {
               getClientConformation(mgr.translate("DOCMAWDOCISSUEUSERACCESSAPPROVAL: The access type is set to user, and person(s)/group(s) on the approval routing will not be able to view this document. Do you wish to begin the approval process?"), "startApproval();");
               if (headlay.isMultirowLayout())
                  getClientConformation(mgr.translate("DOCMAWDOCISSUEUSERACCESSAPPROVALMULTIROW: The document(s) you have selected has the access type set to user, and person(s)/group(s) on the approval routing will not be able to view the document(s). Do you wish to begin the approval process?"), "startApproval();");
            }
            else
               getClientConformation(mgr.translate("DOCMAWDOCISSUEWISHTOASTARTAPP: Do you wish to start the approval process?"), "startApproval();");
         }
         else
         {
	   

            if (headlay.isSingleLayout())
               headset.markRow("PROMOTE_TO_APP_IN_PROGRESS__");
            else
               markSelection("PROMOTE_TO_APP_IN_PROGRESS__");

            headset.setFilterOff();

            int row = headset.getCurrentRowNo();
            trans.clear();
            mgr.submit(trans);
            client_confirmation = false;
            headset.goTo(row);
         }
         headset.setFilterOff();
         headset.goTo(curr_row);
      }
   }


   /**
    * Cancels the approval of the selected document(s). The document state
    * is set to 'Preliminary'
    *
    */
   public void  cancelApproval()
   {
      ASPManager mgr = getASPManager();

      

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         if (headset.countSelectedRows() == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
            return;
         }
      }

      if (!client_confirmation && !isValidColumnValue("STATE", sAppInProg, true))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEAPPINPROGRESSCANCANCELL: Only documents in state Approval In Progress can be canceled."));
      else
      {
	  //Bug id 70512 Start
	  if ((!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",false)) || (isValidColumnValue("REDLINE_FILE_STATUS","TRUE",true))) && (!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",true))))
	  {
	  mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTCANCELAPPCOMM: Cannot cancel the approval process when comment file is checked out."));
	  return;
	  }
	  //Bug id 70512 End

         if (!client_confirmation)
         {
            getClientConformation(mgr.translate("DOCMAWDOCISSUEMESCANAPP: Do you really want to cancel the approval process? This will clear all user signs and dates from the approval routing."), "cancelApproval();");
         }
         else
         {
            if (headlay.isSingleLayout())
               headset.markRow("DEMOTE_TO_PRELIMINARY__");
            else
            {
               headset.setFilterOn();
               markSelection("DEMOTE_TO_PRELIMINARY__");
               headset.setFilterOff();
            }

            int row = headset.getCurrentRowNo();
            trans.clear();
            mgr.submit(trans);
            client_confirmation = false;
            headset.goTo(row);
            okFindITEM5();
         }
      }
   }



   /**
    * Sets the selected document(s) to state 'Approved'.
    *
    */
   public void setApproved()
   {
      ASPManager mgr = getASPManager();
      boolean approved_children = false;

      //Bug Id 70808, Start
      bConfirmBackgroundJob = false;
      bBGJobAlertShown = false;
      //Bug Id 70808, End

      if ("OK".equals(mgr.readValue("CONFIRM")))
      {
         client_confirmation = true;
         confirm_func = ctx.readValue("CONFIRMFUNC","");
      }

      int curr_row = 0;

      if (headlay.isMultirowLayout())
      {
         headset.selectRows();
         curr_row = headset.getRowSelected();
      }
      else
      {
         headset.selectRow();
         curr_row = headset.getCurrentRowNo();
      }

      //Bug Id 70808, Start
      if (!client_confirmation && !isValidColumnValue("STATE", sPrelimin, sAppInProg, true))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEPRIMORAPPINPROGRESSCANAPPROVE: Only documents in state Preliminary or Approval In Progress can be approved."));
      else
      {
	  //Bug id 70512 Start
	   if ((!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",false)) || (isValidColumnValue("REDLINE_FILE_STATUS","TRUE",true))) && (!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",true))))
	   {
	       mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTAPPCOMM: Cannot approve documents when comment file is checked out."));
	       return;
	   }
	   //Bug id 70512 End

         if (!client_confirmation)
         {
            int selectedrows = headset.countSelectedRows();
            
            if (headlay.isMultirowLayout() && !bPerformBackgroundJob) {
               int nLimit = getLimitForBGJob();
               if (selectedrows > 1 && selectedrows <= nLimit ) {
                  sUrl = root_path + "docmaw/BackgroundJobConfirm.page";
                  bConfirmBackgroundJob = true;                                 
                  ctx.writeValue("CONFIRMFUNC", "setApproved();");
               }
               else if (selectedrows > nLimit) {
                  double rows = (double)selectedrows;
                  double num = rows/BACKGROUNDJOB_LIMIT;
                  int numOfBGJ = (int)Math.ceil(num);
                  bBGJobAlertShown = true;                                 
                  ctx.writeValue("BGJ2BDONE","TRUE");
                  getClientConformation(mgr.translate("DOCMAWDOCISSUEPOSTBGJ: &1 Background Job(s) will be posted for this operation. Do you want to continue?", numOfBGJ+""), "setApproved();");
               }
            }
         }
         if (!bConfirmBackgroundJob && !bBGJobAlertShown) 
         {
            if (bPerformBackgroundJob) 
            {
               getSelectedDocsObjects();
               String sParameter;
               trans.clear();
               for (int i=0; i<objArray.length;i++) {
                  sParameter = "Action" + (char)31 + "Approve" + (char)30;
                  sParameter += "Documents" + (char)31 + objArray[i] + (char)30;
                  ASPCommand cmd = trans.addCustomCommand("BGJAPPROVE" + i, "Doc_Issue_API.Update_State_Bgj");
                  cmd.addParameter("DUMMY1", sParameter);
               }
               trans = mgr.perform(trans);
               trans.clear(); 
               headset.refreshAllRows();
            }
            else
            {
         boolean bDocInBriefcase = false;
         int count = 0;

         trans.clear();
         headset.setFilterOn();
         headset.first();
         do
         {
            if (!mgr.isEmpty(headset.getRow().getValue("BRIEFCASE_NO")))
            {
               if (mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")))
               {
                  bDocInBriefcase = true;
                  break;
               }
               else if ((headset.getRow().getValue("SEDMSTATUS").equals(sInBriefcase)) || (headset.getRow().getValue("SEDMSTATUS").equals(sCheckedOutToBc)))
               {
                  bDocInBriefcase = true;
                  break;
               }
            }

            if ("1".equals(headset.getRow().getValue("STRUCTURE_TYPE")))
            {
               ASPCommand cmd = trans.addCustomFunction("CHECKCHILDRENAPPROVED" + count++, "Doc_Issue_API.Is_Children_Ok_For_Approval_", "OUT_1");
               cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
               cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
               cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
               cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
            }
         }
         while (headset.next());

         if (bDocInBriefcase)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTAPPROVEDOCINBC: At least one of the documents you have selected is added to a briefcase. You must unlock them first in order to Approve them."));
            headset.setFilterOff(); //Bug Id 75490
	    return;
         }


         // If the user selected more than one document that is of structure type and
         // has children that are not approved yet, then warn the user about it..
         boolean unapproved_children = false;
         if (count > 0)
         {
            trans = mgr.perform(trans);

            do
            {
               if ("FALSE".equals(trans.getValue("CHECKCHILDRENAPPROVED" + (--count) + "/DATA/OUT_1")))
               {
                  unapproved_children = true;
                  break;
               }
            }
            while (count > 0);

            if (unapproved_children && headset.countSelectedRows() > 1)
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTAPPROVETOOMANYSTRUCTURES: Some of the Structure documents selected have unapproved sub-documents. You must approve the parent of these documents individually."));
               headset.setFilterOff();
               return;
            }
         }

         //headset.setFilterOff();

                 if (!client_confirmation && !bDialogBoxPopped)
                 {
            if ("1".equals(headset.getRow().getValue("STRUCTURE_TYPE")) && (headlay.isSingleLayout() || headset.countSelectedRows() == 1) && unapproved_children)
            {
               // If the current document is a structure document..
               getClientConformationEx(mgr.translate("DOCMAWDOCISSUEMESAPPROVESTRUCTURE: This structure document has unapproved sub-documents. Do you want to approve the sub-documents as well?"), "setApproved();");
            }
            else
            {
               if (isSecurityCheckpointEnabled())
               {
                  headset.setFilterOn();
                  markSelection("SET_DOC_REV_TO_APPROVED__");
                  headset.setFilterOff();

                  mgr.submit(trans);
                  client_confirmation = false;
                  headset.goTo(curr_row);
                  headset.refreshAllRows();
               }
                       else if (!bDialogBoxPopped) 
                  getClientConformation(mgr.translate("DOCMAWDOCISSUEMESAPPROVE: Do you wish to approve the Document Revision(s)?"), "setApproved();");
            }
         }
         else
         {
            trans.clear();
            headset.goTo(curr_row);


            if ((headlay.isSingleLayout() || headset.countSelectedRows() == 1) && "1".equals(headset.getRow().getValue("STRUCTURE_TYPE")))
            {
               // If the current document is a structure document then
               // promote all sub-documents to Approved before promoting the parent..
               ASPCommand cmd = trans.addCustomCommand("APPROVECHILDREN", "Doc_Issue_API.Doc_Struc_To_Approved__");
               cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
               cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
               cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
               cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
               approved_children = true;
               trans = mgr.perform(trans);
               trans.clear();
            }
            else
            {
               if (headlay.isSingleLayout())
               {
                  headset.markRow("PROMOTE_TO_APPROVED__");
               }
               else
               {
                  //Bug Id 77727, Start
                  if (isSecurityCheckpointEnabled())
                  {
                       headset.setFilterOn();
                       markSelection("SET_DOC_REV_TO_APPROVED__");
                       headset.setFilterOff();
                  }
                  else
                  {
                       headset.setFilterOn();
                       markSelection("PROMOTE_TO_APPROVED__");
                       headset.setFilterOff();
                  }
                  //Bug Id 77727, End
               }

               mgr.submit(trans);
               client_confirmation = false;  
            }
            headset.goTo(curr_row);

            // We need to refresh all rows in the rowset so that
            // that their status are updated if they happened to be
            // sub document of the current document that was just approved
            headset.refreshAllRows();  
         }
      }
         }
      }

      headset.setFilterOff();
      //Bug Id 70808, End
                
      // Refresh the child blocks if requred..
      if (headlay.isSingleLayout() && approved_children)
      {
         if (tabs.getActiveTab()== 4)
         {
            okFindITEM3();
         }
         else if (tabs.getActiveTab()== 5)
         {
            okFindITEM4();
         }
      }
   }

   //Bug Id 70808, Start
   private int getLimitForBGJob()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPCommand cmdBGJob = trans.addCustomFunction("GETLIMITFORGJOB", "DOCMAN_DEFAULT_API.Get_Default_Value_", "DUMMY3");
      cmdBGJob.addParameter("DUMMY1","DocIssue");
      cmdBGJob.addParameter("DUMMY2","LIMIT_NORMAL_STATE_CHANGES");
      trans = mgr.perform(trans);
      int limit = Integer.parseInt(trans.getValue("GETLIMITFORGJOB/DATA/DUMMY3"));
      return limit;
   }

   private void getSelectedDocsObjects()
   {
      int rowcount = 0;
      int jobcount = 0;
      int nBGJobs = 0;
      double rows = (double)headset.countSelectedRows();
      double num = rows/BACKGROUNDJOB_LIMIT;
      int numOfBGJ = (int)Math.ceil(num);
      objArray = new String[numOfBGJ];

      headset.setFilterOn();           
      headset.first();
      do
      {
         if (rowcount == BACKGROUNDJOB_LIMIT) {
            rowcount = 0;
            jobcount++;
         }
         if (rowcount == 0) {
            objArray[jobcount] = "";
         }
         objArray[jobcount] += headset.getRow().getValue("OBJID") + "^";
         rowcount++;
      }
      while (headset.next());
   }
   //Bug Id 70808, End

   /**
    * Releases the selected document(s). The document is set to state
    * 'Released'.
    *
    */
   public void  releaseDocument()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer act_buff;
      int noOfSelectedRows = 1;

                //Bug Id 70808, Start
                bBGJobAlertShown = false;
                bReleaseBGJPossible = false;
                //Bug Id 70808, End

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      //Bug Id 70808, Start
      if (!client_confirmation && !isValidColumnValue("STATE", sApproved, true))
              mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTINAPPSTATE: Only the documents in state Approved can be released."));
      else
      {
         if (!client_confirmation)
         {
            int selectedrows = headset.countSelectedRows();
            
            if (headlay.isMultirowLayout() && !bPerformBackgroundJob) {
                  int nLimit = getLimitForBGJob();
                  ctx.writeValue("CONFIRMFUNC","releaseDocument()");
                  if (selectedrows > 1 && selectedrows <= nLimit ) {
                     bReleaseBGJPossible = true;
                  }
                  else if (selectedrows > nLimit) 
                  {
                     double rows = (double)selectedrows;
                     double num = rows/BACKGROUNDJOB_LIMIT;
                     int numOfBGJ = (int)Math.ceil(num);
                     bBGJobAlertShown = true;                                 
                     ctx.writeValue("BGJ2BDONE","TRUE");
                     getClientConformation(mgr.translate("DOCMAWDOCISSUEPOSTBGJ: &1 Background Job(s) will be posted for this operation.  Do you want to continue?", numOfBGJ+""), "releaseDocument()");
                  }
            }
         }

         if (!bBGJobAlertShown) 
         {
            if (bPerformBackgroundJob) 
            {
               getSelectedDocsObjects();
               String sParameter;
               trans.clear();
               for (int i=0; i<objArray.length;i++) {
                  sParameter = "Action" + (char)31 + "Release" + (char)30;
                  sParameter += "Documents" + (char)31 + objArray[i] + (char)30;
                  ASPCommand cmd = trans.addCustomCommand("BGJRELEASE" + i, "Doc_Issue_API.Update_State_Bgj");
                  cmd.addParameter("DUMMY1", sParameter);
               }
               trans = mgr.perform(trans);
               trans.clear(); 
               headset.refreshAllRows();
            }
            else
            {
      //Bug 53039, Start
      if (!dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
      {
         bDoCheckForAllAccess = false;
         if (!"TRUE".equals(isAccessOwner()))
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTORELESADOCMENTS: You must have administrative rights to be able to release documents."));
            return;
         }
      }
      //Bug 53039, End

	  //Bug id 70512 Start
	    if ((!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",false)) || (isValidColumnValue("REDLINE_FILE_STATUS","TRUE",true))) && (!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",true))))
	    {
		mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTRELCOMM: Cannot release documents when comment file is checked out."));
		return;
	    }
	    //Bug id 70512 End
         boolean bDocInBriefcase = false;
         if (headlay.isMultirowLayout())
         {
            headset.storeSelections();
            headset.setFilterOn();
            noOfSelectedRows = headset.countRows();
         }
         else
         {
            headset.selectRow();
         }


         // Check if all documents to be released
         // meets the necessary requirements..

                  // Bug Id 70808 - Removed checking release requirements when background job is possible.
                                 
         trans.clear();

         if (headlay.isMultirowLayout())
         {
            headset.first();
         }
         int count = 0;
         for (count=0;count<noOfSelectedRows;count++)
         {
            cmd = trans.addCustomCommand("CHECKRELEASE" + count, "Doc_Issue_Api.Check_Release_Requirements__");
            cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));

            if (!mgr.isEmpty(headset.getRow().getValue("BRIEFCASE_NO")))
            {
               if (mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")))
               {
                  bDocInBriefcase = true;
                  break;
               }
               else if ((headset.getRow().getValue("SEDMSTATUS").equals(sInBriefcase)) || (headset.getRow().getValue("SEDMSTATUS").equals(sCheckedOutToBc)))
               {
                  bDocInBriefcase = true;
                  break;
               }
            }

            if (headlay.isMultirowLayout())
            {
               headset.next();
            }
         }

                  if (bDocInBriefcase && !bReleaseBGJPossible)
                  {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTRELEASEDOCINBC: At least one of the documents you have selected is added to a briefcase. You must unlock them first in order to Release them."));
            headset.setFilterOff();
            return;
         }

         trans = mgr.perform(trans);

         act_buff = mgr.newASPBuffer();
         act_buff.addItem("ACTION", "RELEASE_DOC");
         act_buff.addItem("DOC_TYPE", "ORIGINAL");

         ASPBuffer data_buff = headset.getSelectedRows("DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV");
         ctx.writeValue("OPERATION", "RELEASE");
         sUrl = DocumentTransferHandler.getDataTransferUrl(mgr, "DocReleaseWizard.page", act_buff, data_buff);
                                 
                  if (bReleaseBGJPossible) 
                     sUrl += "&CHECK_BGJ=TRUE";       
                  else
                     sUrl += "&CHECK_BGJ=FALSE";       
         bOpenReleaseWizardWindow = true;
      }
         }
      }
      //Bug Id 70808, End

      if (headlay.isMultirowLayout())
      {
         headset.setFilterOff();
      }

   }


   public void createNewSheet()
   {
      ASPManager mgr = getASPManager();
      String userAccess;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      headset.refreshRow();
      String fnd_user = getFndUser();
      
      // Modified by Terry 20120927
      // Original:
      /*
      // Bug id 88317 start

      trans.clear();

      cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
      cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
      cmd.addReference("DUMMY2", "FNDUSER/DATA");
            
      trans = mgr.perform(trans);
      String person_id = trans.getValue("STARUSER/DATA/DUMMY1");


      if ("*".equals(person_id)){
         mgr.showAlert(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
         return;
      }*/
      // Bug id 88317 end
      // Modified end

      if ("1".equals(headset.getRow().getValue("STRUCTURE_TYPE")))
      {
         mgr.showAlert("DOCMAWDOCISSUECANNOTCREATENEWSHEETFROMSTRUCTURE: You cannot create a new sheet for a structure document.");
         return;
      }

      if (dGroup.equals(headset.getRow().getValue("ACCESS_CONTROL")))
      {
         trans.clear();
         cmd = trans.addCustomFunction("USERACCESS","DOCUMENT_ISSUE_ACCESS_API.User_Get_Access","USERGETACCESS");
         cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV"));
         cmd.addParameter("LOGUSER",fnd_user);
         trans = mgr.perform(trans);

         userAccess = trans.getValue("USERACCESS/DATA/USERGETACCESS");
         trans.clear();
      }
      else if (dAll.equals( headset.getRow().getValue("ACCESS_CONTROL")))
         userAccess="EDIT";
      else
      {
         // Bug 57779, Start
         if (isUserDocmanAdministrator() || (headset.getRow().getValue("USER_CREATED").equals(fnd_user)))
            // Bug 57779, End

            userAccess="EDIT";
         else
            userAccess="";
      }

      if (!("EDIT".equals(userAccess)))
      {
         mgr.showAlert("DOCMAWDOCISSUENORIGHTSTOCREATENEWSHEET: You must have edit access to be able to create a new sheet to this document.");
         headset.setFilterOff();
         headset.unselectRows();
         return;
      }


      // to preserve the record set :bakalk
      data = headset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      headset.refreshRow();

      String doc_no     = headset.getValue("DOC_NO");
      String doc_class  = headset.getValue("DOC_CLASS");
      String doc_sheet =  headset.getValue("DOC_SHEET");
      String doc_rev =  headset.getValue("DOC_REV");
      String url = mgr.getURL();

      mgr.transferDataTo("NewSheetWizard.page?DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&DOC_SHEET="+mgr.URLEncode(doc_sheet)+"&DOC_REV="+mgr.URLEncode(doc_rev)+"&SEND_URL="+mgr.URLEncode(url),data);
      headset.setFilterOff();
      headset.unselectRows();
   }


   /**
    * Sets the selected document(s) to state 'Obsolete'.
    *
    */
   public void setObsolete()
   {
      ASPManager mgr = getASPManager();
      int curr_row=0;


      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         if (headset.countSelectedRows() == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
            return;
         }
      }
      else
      {
         headset.selectRow();
         curr_row = headset.getCurrentRowNo();
      }

      if (!client_confirmation && !isValidColumnValue("STATE", sObsolete, sAppInProg, false))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEOBSOORAPPINPROGRESSCANNOTOBSO: Documents in state Obsolete or Approval In Progress cannot be set to obsolete."));
      else
      {
	 //Bug id 70512 Start
         if ((!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",false)) || (isValidColumnValue("REDLINE_FILE_STATUS","TRUE",true))) && (!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",true))))
	 {
	  mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTOBSOLETECOMM: Cannot set documents to obsolete when comment file is checked out."));
	  return;
	 }
	  //Bug id 70512 End
         boolean bDocInBriefcase = false;

         headset.setFilterOn();

         headset.first();
         do
         {
            if (!mgr.isEmpty(headset.getRow().getValue("BRIEFCASE_NO")))
            {
               if (mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")))
               {
                  bDocInBriefcase = true;
                  break;
               }
               else if ((headset.getRow().getValue("SEDMSTATUS").equals(sInBriefcase)) || (headset.getRow().getValue("SEDMSTATUS").equals(sCheckedOutToBc)))
               {
                  bDocInBriefcase = true;
                  break;
               }
            }
         } while ( headset.next() );


         if (bDocInBriefcase)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTSETOBSOLETEDOCINBC: At least one of the documents you have selected is added to a briefcase. You must unlock them first in order to set them Obsolete."));
            headset.setFilterOff();
            return;
         }
         if (!client_confirmation)
         {
            getClientConformation(mgr.translate("DOCMAWDOCISSUESETTOOBSOLETE: Do you wish set the document to obsolete?"), "setObsolete();");
            headset.setFilterOff();
         }
         else
         {
            headset.refreshAllRows();
            if (headset.countSelectedRows() > 1 && !isValidColumnValue("PARENT_STRUCTURE_TYPE", "FALSE", true) && ctx.readFlag("CONFIRM_OBSOLETING_STRUCTURE", true))
            {
               getClientConformation(mgr.translate("DOCMAWDOCISSUEWARNSETMULTIPLESTRUCTURETOOBSOLETE: Warning: one or more of the documents you are trying to set to obsolete is part of a document structure. Do you want to continue with this operation?"), "setObsolete();");
               ctx.writeFlag("CONFIRM_OBSOLETING_STRUCTURE", false);
            }
            else if ("TRUE".equals(headset.getValue("PARENT_STRUCTURE_TYPE")) && ctx.readFlag("CONFIRM_OBSOLETING_STRUCTURE", true))
            {
               getClientConformation(mgr.translate("DOCMAWDOCISSUEWARNSETSTRUCTURETOOBSOLETE: Warning: the document you are trying to set to obsolete is part of a document structure. Do you want to continue with this operation?"), "setObsolete();");
               ctx.writeFlag("CONFIRM_OBSOLETING_STRUCTURE", false);
            }
            else
            {
               trans.clear();
               if (headlay.isSingleLayout())
                  headset.markRow("PROMOTE_TO_OBSOLETE__");
               else
               {
                  headset.setFilterOn();
                  markSelection("PROMOTE_TO_OBSOLETE__");
                  headset.setFilterOff();
               }

               int row = headset.getCurrentRowNo();
               mgr.submit(trans);
               client_confirmation = false;
               headset.goTo(row);
            }

            headset.setFilterOff();
            headset.goTo(curr_row);
         }
      }
   }


   public void transmittalInfo()
   {
      ASPManager mgr = getASPManager();
      if (itemlay10.isSingleLayout())
		{
			itemset10.unselectRows();
			itemset10.selectRow();
		}
		else
         itemset10.storeSelections();

		ASPBuffer data = itemset10.getSelectedRows("TRANSMITTAL_ID");//
		mgr.transferDataTo("DocTransmittalInfo.page",data);
   }


   public void projectInfo()
   {
      ASPManager mgr = getASPManager();
      if (itemlay10.isSingleLayout())
		{
			itemset10.unselectRows();
			itemset10.selectRow();
		}
		else
         itemset10.storeSelections();

		ASPBuffer data = itemset10.getSelectedRows("PROJECT_ID");
                                 

		mgr.transferDataTo("../projw/ChildTree.page",data);
   }

   //Bug Id 79174, start
   private String getSelectedTransmittalIds()
   {
      String sTransmittalIds = "";
      int no_rows_execute_on = 0;
      if (itemlay10.isMultirowLayout()) 
      {
         itemset10.selectRows();
         itemset10.storeSelections();
         itemset10.setFilterOn();
         no_rows_execute_on = itemset10.countRows();
      }
      else
      {
         no_rows_execute_on = 1;
      }
      for (int k = 0; k < no_rows_execute_on; k++)
      {
         sTransmittalIds += itemset10.getRow().getValue("TRANSMITTAL_ID");
         sTransmittalIds += ";";
         if (itemlay10.isMultirowLayout())
            itemset10.next();
      }
      if (itemlay10.isMultirowLayout())
            itemset10.setFilterOff();

      return sTransmittalIds;
   }


   public void executeReport()
   {
      ASPManager mgr = getASPManager();
      sFilePath = "DocTransmittalInfo.page?EXECUTE_REPORT=TRUE&TRANSMITTAL_ID=" + getSelectedTransmittalIds();
      bShowTransmittalReport = true;
   }
   //Bug Id 79174, end
   
   public void onUnconfirm()
   {
      bConfirm = false;
      sMessage = "";
      ctx.writeValue("CONFIRMFUNC","");
   }



   public void  setAccessRights(int  accessCode,
                                String db_method,
                                String error_msg,
                                String error_msg_with_rowNo)
   {
      ASPManager mgr = getASPManager();
      int count = 0;
      int noOFSelectedRows = 1;
      boolean allCanbeDone = true;
      int problemWithThisRow = 0;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         noOFSelectedRows = headset.countRows();
         if (noOFSelectedRows == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
            return;
         }
      }
      else
      {
         headset.selectRow();
      }

      for (count=0;count<noOFSelectedRows;count++)
      {
         if ((headset.getRow().getValue("SCHANGEACCESSCODE")).charAt(accessCode)== '-')
         {
            allCanbeDone = false;
            break;
         }
         if (headlay.isMultirowLayout())
         {
            headset.next();
         }
      }
      problemWithThisRow = ++count;// if any row selected is not ok
      if (allCanbeDone)
      {
         trans.clear();
         if (headlay.isMultirowLayout())
         {
            headset.first();
         }
         for (count=0;count<noOFSelectedRows;count++)
         {
            cmd = trans.addCustomCommand("SET_ACCESS" + count, db_method);
            cmd.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO",headset.getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV",headset.getValue("DOC_REV"));
            if (headlay.isMultirowLayout())
            {
               headset.next();
            }
         }
         mgr.perform(trans);
      }
      else
      {
         if (noOFSelectedRows == 1)
         {
            mgr.showAlert(mgr.translate(error_msg));
         }
         else
         {
            mgr.showAlert(mgr.translate(error_msg_with_rowNo,(problemWithThisRow+"")));
         }
      }
      // Refresh every selected row in the rowset.
      // NOTE: This is just a temporary solution. The problem is that there is no way
      // to update the database view and the rowset at the same time, and must be done
      // separately. The rowset can be repopulated, but this means that we need to save
      // the search query that gave the current rowset plus the rowno.

      if (headlay.isMultirowLayout())
      {
         headset.first();
      }

      for (count=0;count<noOFSelectedRows;count++)
      {
         headset.refreshRow(); // expensive when doing for many rows
         if (headlay.isMultirowLayout())
         {
            headset.next();
         }
      }

      if (headlay.isMultirowLayout())
      {
         headset.setFilterOff();
      }


   }

   public void setUserAccess()
   {
      int accessCode = 0;
      String db_method = "DOC_ISSUE_API.Revoke_Access_Control__";
      String error_msg = "DOCMAWDOCISSUENOACCESSUSER: You must have administrative rights on this document to change access rights to User Access.";
      String error_msg_with_rowNo="DOCMAWDOCISSUENOACCESSUSERWITHNO: You do not have administrative rights on the document &1 in selected rows to change access rights to User Access.";
      setAccessRights(0,db_method,error_msg,error_msg_with_rowNo);
   }


   public void  setGroupAccess()
   {
      int accessCode = 1;
      String db_method            = "DOC_ISSUE_API.Set_Group_Access__";
      String error_msg            = "DOCMAWDOCISSUENOACCESSGROUP: You must have administrative rights on this document to change access rights to Group Access.";
      String error_msg_with_rowNo = "DOCMAWDOCISSUENOACCESSGROUPWITHNO: You do not have administrative rights on the document &1 in selected rows to change access rights to Group Access.";
      setAccessRights(accessCode,db_method,error_msg,error_msg_with_rowNo);
   }


   public void  setAllAccess()
   {
      int accessCode = 2;
      String db_method            = "DOC_ISSUE_API.Set_Access_Control__";
      String error_msg            = "DOCMAWDOCISSUENOACCESSALL: You must have administrative rights on this document to change access rights to All Access.";
      String error_msg_with_rowNo = "DOCMAWDOCISSUENOACCESSALLWITHNO: You do not have administrative rights on the document &1 in selected rows to change access rights to All Access.";
      setAccessRights(accessCode,db_method,error_msg,error_msg_with_rowNo);
   }





   // Adds the newly created revision and repopulates the record set.
   public void addNewRev()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer rec_set = headset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

      ASPBuffer temp_buff = rec_set.getBufferAt(0).copy();

      int currrow = headset.getCurrentRowNo();
      int newrow = 0;
      boolean created_rev_found = false;

      temp_buff.setValueAt(0,mgr.URLDecode(mgr.readValue("DOC_CLASS_FROM_WIZ")));
      temp_buff.setValueAt(1,mgr.URLDecode(mgr.readValue("DOC_NO_FROM_WIZ")));
      temp_buff.setValueAt(2,mgr.URLDecode(mgr.readValue("DOC_SHEET_FROM_WIZ")));
      temp_buff.setValueAt(3,mgr.URLDecode(mgr.readValue("DOC_REV_FROM_WIZ")));
      rec_set.addBuffer("DATA",temp_buff);

      trans.clear();
      q = trans.addEmptyQuery(headblk);
      q.addOrCondition(rec_set);
      q.setOrderByClause("DOC_CLASS, DOC_NO, SHEET_ORDER, REV_NO DESC");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headlay.isSingleLayout())
      {
         headset.goTo(currrow);

         do
         {
            if ((headset.getRow().getValue("DOC_CLASS").equals(temp_buff.getValueAt(0))) && (headset.getRow().getValue("DOC_NO").equals(temp_buff.getValueAt(1))) && (headset.getRow().getValue("DOC_SHEET").equals(temp_buff.getValueAt(2))) && (headset.getRow().getValue("DOC_REV").equals(temp_buff.getValueAt(3))))
            {
               newrow = headset.getCurrentRowNo();
               created_rev_found = true;
            }
            headset.previous();
         }
         while (!created_rev_found);

         headset.goTo(newrow);
      }

      // Refresh all child blocks
      okFindITEM0();
      okFindITEM1();
      okFindITEM2();
      okFindITEM3();
      okFindITEM4();
      okFindITEM5();
      okFindITEM6();
      okFindITEM7();
      okFindITEM9();
   }


   public void transferToEdmMacro(ASPBuffer buff)
   {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout())
      {
         headset.unselectRows();
         headset.selectRow();
      }
      else
         headset.selectRows();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page",buff,data);
      bTranferToEDM = true;

   }


   /**
    * Builds a url containing all selected rows to EdmMacro
    * for execution. The fields selected for each row are
    * DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV and EDM_FILE_TYPE
    * where EDM_FILE_TYPE is the file type of the original
    * document
    */
   public void transferToEdmMacro(String doc_type, String action)
   {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout())
      {
         headset.unselectRows();
         headset.selectRow();
      }
      else
         headset.selectRows();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", action, doc_type, data);
      bTranferToEDM = true;
   }


   public void goToBriefcase()
   {
      //Bug 75490, Start
       goToBriefcase(null);
       //Bug 75490, End
   }

   //Bug 75490, Start
   public void goToBriefcase(String sBcNo)
   {
      ASPManager mgr = getASPManager();
      ASPBuffer temp = mgr.newASPBuffer();

      if (headlay.isMultirowLayout())
      {
         headset.selectRows();
         headset.setFilterOn();
      }
      else
         headset.selectRow();

      if (!mgr.isEmpty(sBcNo))
      {
          temp.addItem("BRIEFCASE_NO", sBcNo);
      }
      else
      {
          temp = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      }

      mgr.transferDataTo("DocBriefcase.page",temp);
      headset.setFilterOff();
   }
   //Bug 75490, End

   //Bug Id 81806, start
   public void transmittalWizard()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.selectRows();
         headset.setFilterOn();
      }
      else
         headset.selectRow();
      
      ASPBuffer data_buff = headset.getSelectedRows("DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV");
      ASPBuffer act_buff = mgr.newASPBuffer();
      act_buff.addItem("ACTION", "LAUNCH_TRANSMITTAL_WIZARD");

      sUrl = DocumentTransferHandler.getDataTransferUrl(mgr, "DocTransmittalWizard.page", act_buff, data_buff);
      bOpenTransmittalWizardWindow = true; 

      headset.setFilterOff();
   }
   //Bug Id 81806, end

   public void checkEnableHistoryNewRow()
   {

      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("GETCLIENTVAL","DOCUMENT_ISSUE_HISTORY_CAT_API.DECODE","DUMMY1");
      cmd.addParameter("ITEM7_DUMMY1","INFO");
      trans = mgr.perform(trans);
      String infoCategory = trans.getValue("GETCLIENTVAL/DATA/DUMMY1");
      trans.clear();

      cmd = trans.addCustomFunction("GETLOGEVENT","DOC_CLASS_HISTORY_SETTINGS_API.GET_LOGG_EVENT","ITEM7_ENABLE_HISTORY_NEW" );
      cmd.addParameter("INFO_CATEGORY",infoCategory);
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
      trans = mgr.perform(trans);

      enableHistoryNewButton = trans.getValue("GETLOGEVENT/DATA/ITEM7_ENABLE_HISTORY_NEW");
      trans.clear();
   }


   public void addToBriefcase()
   {
      ASPManager mgr = getASPManager();
      int noOfRowsSelected = 1;
      int count = 0;

      if (addingToBc)//to handle case calling from DocBriefcase
      {
         completeAddToBc();
      }
      else
      {
         boolean bReturn = false;

         String briefcase_no = mgr.readValue("BRIEFCASENO");

         if (!mgr.isEmpty(sBcNoFromBriefcase))
         {
            briefcase_no = sBcNoFromBriefcase;
         }

         if (headlay.isMultirowLayout())
         {
            headset.storeSelections();
         }
         else
            headset.selectRow();

         // Return if no rows are selected in MultiRow layout
         if (headlay.isMultirowLayout() && headset.countSelectedRows() ==0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
            return;
         }

         if (headlay.isMultirowLayout())
         {
            headset.setFilterOn();
            noOfRowsSelected = headset.countRows();
            headset.first();
         }

         //Bug 61462, Start
         trans.clear();
         //Bug 61462, End
         for (count=0;count<noOfRowsSelected;count++)
         {
            // Is document in allowed state
            if (!((sPrelimin.equals(headset.getRow().getValue("STATE"))) || (sReleased.equals(headset.getValue("STATE"))) || sApproved.equals(headset.getRow().getValue("STATE"))))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOVALIDSTATE: Only Documents in state Preliminary, Approved or Released can be added to a briefcase"));
               bReturn = true;
               break;
            }

            if ("FALSE".equals(headset.getValue("CAN_ADD_TO_BC")))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOEDITACCESSADDTOBRIEF: You must have Edit access to the Document(s) that you want to add to your Briefcase!"));
               bReturn = true;
               break;
            }
            //Bug 61462, Start
            cmd = trans.addCustomCommand("FILEREFEXIST" + count,"DOC_BRIEFCASE_ISSUE_API.File_Ref_Exist");
            cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV"));
            cmd.addParameter("DUMMY1","ORIGINAL");
            //Bug 61462, End
            if (headlay.isMultirowLayout())
            {
               headset.next();
            }
         }

         //Bug 61462, Start
         trans = mgr.perform(trans);
         //Bug 61462, End

         if (bReturn)
         {
            bShowBCLov = false;
            if (headlay.isMultirowLayout())
            {
               headset.setFilterOff();
            }
            return;
         }

         // Has the briefcase been selected
         if (mgr.readValue("BRIEFCASE_SELECTED").equals("FALSE"))
         {
            bShowBCLov = true;
            if (headlay.isMultirowLayout())
            {
               headset.setFilterOff();
            }
            return;
         }

         // Check if bc is in state created
         trans.clear();

         //bug 58216 starts
         q = trans.addQuery("GETBCSTATE","SELECT STATE FROM DOC_BRIEFCASE WHERE BRIEFCASE_NO = ?");
         q.addParameter("BRIEFCASE_NO",briefcase_no);
         //bug 58216 end

         trans = mgr.perform(trans);

         if (!sBcCreated.equals(trans.getValue("GETBCSTATE/DATA/STATE")))
         {
            mgr.showAlert("DOCMAWDOCISSUEINVALIDBCSTATE: Briefcase must be in state 'Created' in order to add documents.");
            bShowBCLov = false;
            headset.setFilterOff();
            return;
         }

         bShowBCLov = false;
         trans.clear();

         if (headlay.isMultirowLayout())
         {
            headset.first();
         }

         for (count = 0;count<noOfRowsSelected;count++)
         {
            // Add new record to doc_briecase_issue
            cmd = trans.addCustomCommand("ADDDOCTOBC" + count,"DOC_BRIEFCASE_ISSUE_API.Add_To_Briefcase");
            cmd.addParameter("BRIEFCASE_NO",briefcase_no);
            cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV"));
            if (headlay.isMultirowLayout())
            {
               headset.next();
            }

         }

         mgr.perform(trans);

         if (!mgr.isEmpty(sBcNoFromBriefcase))
         {
            headset.last();
            headset.refreshRow();
            //Bug 75490, Start
            goToBriefcase(briefcase_no);
            //Bug 75490, End
         }
         else
         {
            if (headlay.isMultirowLayout())
            {
               headset.first();
            }
            for (count=0;count<noOfRowsSelected;count++)
            {
               headset.refreshRow();
               if (headlay.isMultirowLayout())
               {
                  headset.next();
               }
            }

         }
         if (headlay.isMultirowLayout())
         {
            headset.setFilterOff();
            headset.unselectRows();
         }

      }
   }

   public void connectToTrans()
   {
      ASPManager mgr = getASPManager();
      

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
      }
      else
         headset.selectRow();


      if ( startFromTransmittal) {
         this.executeAddingToTransmittal(trans_id_sent);
         mgr.transferDataTo(sendingUrl+"?AFTER_ADDING_DOCS=YES&CURR_TRANS_NO="+mgr.URLEncode(trans_id_sent),trans_Buffer);
         return;
      }

      // if this has started from Transmittal info, we wont go beyond this.

       
     

      if (!"TRUE".equals(mgr.readValue("TRANSMITTAL_SELECTED"))) {
         bShowTransLov = true;
      }
      else
      {
         String transmittalIdSelected = mgr.readValue("TRANSMITTAL_ID_SELECTED");
        
         executeAddingToTransmittal(transmittalIdSelected);
         
         mgr.redirectTo( "DocTransmittalInfo.page?TRANSMITTAL_ID="+mgr.URLEncode(transmittalIdSelected));

      }
   }


   private void executeAddingToTransmittal(String transmittalId)
   {
      int noOfRowsSelected = 1;
      ASPManager mgr = getASPManager();
      // Return if no rows are selected in MultiRow layout
         if (headlay.isMultirowLayout() && headset.countSelectedRows() ==0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
            return;
         }

         if (headlay.isMultirowLayout())
         {
            headset.setFilterOn();
            noOfRowsSelected = headset.countRows();
            headset.first();
         }

         //Bug 61462, Start
         trans.clear();
         //Bug 61462, End
         for (int count=0;count<noOfRowsSelected;count++)
         {
            String  attr   = "TRANSMITTAL_ID" + String.valueOf((char)31) + transmittalId                 + String.valueOf((char)30);
                    attr  += "DOC_CLASS"      + String.valueOf((char)31) + headset.getRow().getValue("DOC_CLASS")+ String.valueOf((char)30);
                    attr  += "DOC_NO"         + String.valueOf((char)31) + headset.getRow().getValue("DOC_NO")   + String.valueOf((char)30);
                    attr  += "DOC_SHEET"      + String.valueOf((char)31) + headset.getRow().getValue("DOC_SHEET")+ String.valueOf((char)30);
                    attr  += "DOC_REV"        + String.valueOf((char)31) + headset.getRow().getValue("DOC_REV")  + String.valueOf((char)30);

           cmd =  trans.addCustomCommand("ADDDOC"+count,"DOC_TRANSMITTAL_ISSUE_API.New__");
           cmd.addParameter("DUMMY1");
           cmd.addParameter("DUMMY1");
           cmd.addParameter("DUMMY1");
           cmd.addParameter("IN_1",attr);
           cmd.addParameter("IN_2","DO");

           headset.next();
      
      
         }

         trans = mgr.perform(trans);

         if (headlay.isMultirowLayout())
         {
            headset.setFilterOff();
         }

   }



   public void setBcNoFromBriefcase()
   {
      ASPManager mgr = getASPManager();

      // If initilised from BC page
      sBcNoFromBriefcase = mgr.readValue("BC_NO");
      headlay.setLayoutMode(headlay.FIND_LAYOUT);
   }


   protected String  getFndUser()
   {
      return ctx.readValue("FNDUSER", null);
   }

   // Bug 57779, Start
   private boolean isUserDocmanAdministrator()
   {
      return ctx.readFlag("DOCMAN_ADMIN", false);
   }
   // Bug 57779, End

   private String getPersonId()
   {
      return ctx.readValue("PERSONID",null);
   }


   protected boolean isUserSettingsEnable()
   {
      return ctx.readFlag("SHOW_USER_SETTINGS", false);
   }

   //Bug Id 67105, Start
   protected boolean isMandatorySettingsEnable()
   {
	   return ctx.readFlag("SHOW_MANDATORY_SETTINGS", false);
   }
   //Bug Id 67105, End

   public boolean  isEditable()
   {
      int curr_row;
      if (headlay.isMultirowLayout())
         curr_row = headset.getRowSelected();
      else
         curr_row = headset.getCurrentRowNo();

      headset.goTo(curr_row);

      if ("TRUE".equals(headset.getValue("GETEDITACCESS")))
         return true;
      else
         return false;
   }


   public boolean  isViewable()
   {
      if ("TRUE".equals(headset.getValue("GETVIEWACCES")))
         return true;
      else
         return false;
   }


   public boolean isInBriefcase()
   {
      int iRowNo = 0;

      if (headlay.isMultirowLayout())
         iRowNo = headset.getRowSelected();
      else
         iRowNo = headset.getCurrentRowNo();

      headset.goTo(iRowNo);

      if (sInBriefcase.equals(headset.getValue("SEDMSTATUS")) || sCheckedOutToBc.equals(headset.getValue("SEDMSTATUS")))
         return true;
      else
         return false;
   }

   //Bug id 70512, Start
   public boolean isCommentUpdateEnabled()
   {
      ASPManager mgr = getASPManager();
       
      trans.clear();
      cmd = trans.addCustomFunction("COMMENTUPDATEALLOW", "Doc_Class_Default_API.Get_Default_Value_", "DUMMY3");
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DUMMY1","DocIssue");
      cmd.addParameter("DUMMY2","ALLOW_UPD_COMMENT_REL_DOC");

      trans = mgr.perform(trans);
      String comment_updatable = trans.getValue("COMMENTUPDATEALLOW/DATA/DUMMY3");
      trans.clear();

      debug("debug default value:" + comment_updatable);

      if ("Y".equals(comment_updatable))
         return true;
      else
         return false;

   }
//Bug id 70512, End

   public boolean isSecurityCheckpointEnabled()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("SECURITYCHECKPOINTREQ", "Doc_Class_Default_API.Get_Default_Value_", "DUMMY3");
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DUMMY1","DocIssue");
      cmd.addParameter("DUMMY2","SET_APPROVED_SEC_CHKPT");

      ASPCommand cmdCheckActive = trans.addCustomFunction("SECURITYCHECKPOINTACTIVE","Security_SYS.Security_Checkpoint_Activated", "DUMMY3");
      cmdCheckActive.addParameter("DUMMY1", "DOCMAN_DOC_REV_SET_TO_APPROVED");


      trans = mgr.perform(trans);
      String security_checkpoint_req = trans.getValue("SECURITYCHECKPOINTREQ/DATA/DUMMY3");
      String secutiry_checkpoint_active = trans.getValue("SECURITYCHECKPOINTACTIVE/DATA/DUMMY3");
      trans.clear();

      if ("Y".equals(security_checkpoint_req) && "TRUE".equalsIgnoreCase(secutiry_checkpoint_active))
         return true;
      else
         return false;
   }

   public void  viewCopy()
   {
      ASPManager mgr = getASPManager();

      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
          return;
      }
      //Bug Id 67336, end

      //Bug Id 73718, start
      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
	  mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
	  return;
      }

      //Bug Id 77556, start - Structure documents
      if (isStructure(headset.getRow().getValue("DOC_CLASS"),headset.getRow().getValue("DOC_NO")))
      {
	  trans.clear();
	  cmd = trans.addCustomFunction("EDMINFO", "Edm_File_Api.Get_Edm_Information", "DUMMY1");
	  cmd.addParameter("DUMMY2", headset.getRow().getValue("DOC_CLASS"));
	  cmd.addParameter("DUMMY3", headset.getRow().getValue("DOC_NO"));
	  cmd.addParameter("DUMMY3", headset.getRow().getValue("DOC_SHEET"));
	  cmd.addParameter("DUMMY4", headset.getRow().getValue("DOC_REV"));
	  cmd.addParameter("DUMMY5", "ORIGINAL");
	  trans = mgr.perform(trans);

	  String edm_info = trans.getValue("EDMINFO/DATA/DUMMY1");

	  if ("FALSE".equals(DocmawUtil.getAttributeValue(edm_info, "VIEW_REF_EXIST")))
	  {
	      mgr.showAlert("DOCMAWDOCISSUENOVIEWREFERENCE: This document revision does not have a view file reference.");
	      return;
	  }

	  if (isValidColumnValue("GETVIEWACCES", "TRUE", true))
	  {
	      ASPBuffer buff = mgr.newASPBuffer();
	      buff.addItem("FILE_ACTION", "VIEWCOPY");
	      buff.addItem("DOC_TYPE", "VIEW");
	      buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
	      buff.addItem("LAUNCH_FILE", mgr.readValue("LAUNCH_FILE"));
	      transferToEdmMacro(buff);
	  }
	  else
	  {
	      if (headlay.isMultirowLayout())
		  mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSVIEWMULTI: You don't have view access to one or more of the selected documents.");
	      else
		  mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSVIEW: You must have view access to be able to view this document.");
	  }
	  //Bug Id 73718, end
      }
      else //Non-structure documents
      {
	  if (isViewable())
	  {
	      transferToEdmMacro("VIEW", "VIEW");
	  }
	  else
	      mgr.showAlert("DOCMAWDOCISSUECANNOTVIEW: You don't have permission to view this document");

      }
      //Bug Id 77556, end
   }


   public void  viewOriginal()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      /*if (isEmptyColumnValue("CHECKED_IN_SIGN"))
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOVIEWEMPTYFILEMULTI: One or more documents you're trying to view has no file checked in yet."));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOVIEWEMPTYFILE: The document you're trying to view has no file checked in yet."));
         return;
      }*/
      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
	  return;
      }
      //Bug Id 67336, end

      if (isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "VIEW");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
         buff.addItem("LAUNCH_FILE", mgr.readValue("LAUNCH_FILE"));
         transferToEdmMacro(buff);
      }
      else
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSVIEWMULTI: You don't have view access to one or more of the selected documents.");
         else
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSVIEW: You must have view access to be able to view this document.");
      }
   }
   
   // Added by Terry 20121019
   // Download documents function
   public void downloadDocuments()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0) 
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }
      

      if (isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "DOWNLOAD");
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("SAME_ACTION_TO_ALL", "YES");
         buff.addItem("LAUNCH_FILE", "NO");
         transferToEdmMacro(buff);
      }
      else
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENODOWNACCESSDOWNMULTI: You don't have download access to one or more of the selected documents.");
         else
            mgr.showAlert("DOCMAWDOCISSUENODOWNACCESSDOWN: You must have download access of this document.");
      }
   }
   // Added end


   public void  viewOriginalWithExternalViewer()
   {
      ASPManager mgr = getASPManager();

      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
	  return;
      }
      //Bug Id 67336, end

      if (isViewable())
         transferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER");
      else
         mgr.showAlert("DOCMAWDOCISSUECANNOTVIEW: You don't have permission to view this document");
   }


   public void edit()
   {
      headset.goTo(headset.getRowSelected());
      headlay.setLayoutMode(headlay.EDIT_LAYOUT);
   }


   public void  editDocument()
   {
      String status;
      String checkOutUser;
      String fndUser;
      boolean bMultiRow = false;
      fndUser = getFndUser();

      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
	  return;
      }
      //Bug Id 67336, end

      if (!isValidColumnValue("STATE", sApproved, sReleased, false) || !isValidColumnValue("STATE", sObsolete, false))
      {
         mgr.showAlert("DOCMAWDOCISSUECANNOTEDITINAPPORREL: You are not allowed to edit a document in state Approved, Released or Obsolete.");
         return;
      }

      ASPBuffer buff = mgr.newASPBuffer();
      buff.addItem("FILE_ACTION", "CHECKOUT");
      buff.addItem("DOC_TYPE", "ORIGINAL");
      buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
      buff.addItem("LAUNCH_FILE", mgr.readValue("LAUNCH_FILE"));

      if ("TRUE".equals(mgr.readValue("MULTIROW_EDIT")))
         bMultiRow = true;

      if (isValidColumnValue("VALIDATE_FILE_ACTION","FALSE",true))
      {
         mgr.showAlert("DOCMAWDOCISSUENOEDITINBC: This document(s) is reserved for a briefcase and cannot be Edited.");
         return;
      }

      if ((isValidColumnValue("GETEDITACCESS", "TRUE", true)))
      {
         if (!(bMultiRow) && (isEmptyColumnValue("EDM_STATUS")))
         {
            transferToEdmMacro("ORIGINAL", "CREATENEW");
            return;
         }
         else if ((bMultiRow) && (isEmptyColumnValue("EDM_STATUS")))
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUECREATENEWMSG: Cannot create new documents by multirow operations."));
            return;
         }
         else
         {
            transferToEdmMacro(buff);
            return;
         }
         /*
         
         else if ((isValidColumnValue("EDM_STATUS",sCheckedOut,false)) && (((!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",false)) || (isValidColumnValue("REDLINE_FILE_STATUS","TRUE",true))) && (!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",true))))))
         {
            bCommentFileCheckedOut = true;
            transferToEdmMacro(buff);
            return;
         }
         else if ((isValidColumnValue("EDM_STATUS",sCheckedOut,true)) && (isValidColumnValue("CHECKED_OUT_SIGN",fndUser,true)))
         {
            //file is already checked out..., just the launch file
            strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();

            if ((!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",false)) || (isValidColumnValue("REDLINE_FILE_STATUS","TRUE",true))) && (!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",true))))
               bCommentFileCheckedOut = true;

            transferToEdmMacro(buff);
            return;
         }
         else if ((isValidColumnValue("EDM_STATUS",sCheckedOut,true)) && (isValidColumnValue("CHECKED_OUT_SIGN",fndUser,false)))
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUECHECKEDOUTMSG: The document(s) is checked out by another user."));
            return;
         }
         else
         {
            if ((!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",false)) || (isValidColumnValue("REDLINE_FILE_STATUS","TRUE",true))) && (!(isValidColumnValue("REDLINE_FILE_STATUS","FALSE",true))))
               bCommentFileCheckedOut = true;
            transferToEdmMacro(buff);
            return;
         }*/
      }
      else
         mgr.showAlert("DOCMAWDOCISSUECANNOTEDIT: You must have edit access to be able to edit this document.");
   }



   public void  printDocument()
   {
      ASPManager mgr = getASPManager();

      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
         return;
      }
      //Bug Id 67336, end

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      /*if (isEmptyColumnValue("CHECKED_IN_SIGN"))
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPRINTEMPTYFILEMULTI: One or more documents you're trying to print has no file checked in yet."));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPRINTEMPTYFILE: The document you're trying to print has no file checked in yet."));
         return;
      }*/

      if (isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("FILE_ACTION", "PRINT");
         //Bug Id 56685, Start
         //Bug Id 77556, start
         if (bPrintViewCopy)
         {
            buff.addItem("DOC_TYPE", "VIEW");
            bPrintViewCopy = false;
         }
         else
         {
            buff.addItem("DOC_TYPE", "ORIGINAL");
         }
         //Bug Id 77556, end
         //Bug Id 56685, End
         buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
         transferToEdmMacro(buff);
      }
      else
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSPRINTMULTI: You don't have view access to one or more of the selected documents to print.");
         else
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSPRINT: You must have view access to be able to print this document.");

      }
   }

   //Bug Id 56685, Start
   public void  printViewCopy()
   {
      //Bug Id 73718, start
      ASPManager mgr = getASPManager();
      if (checkFileOperationEnable()) 
      {	
	  return;
      }

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      /*if (isEmptyColumnValue("CHECKED_IN_SIGN"))
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPRINTEMPTYFILEMULTI: One or more documents you're trying to print has no file checked in yet."));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPRINTEMPTYFILE: The document you're trying to print has no file checked in yet."));
         return;
      }*/

      //Bug Id 77556, start //Structure documents
      if (isStructure(headset.getRow().getValue("DOC_CLASS"),headset.getRow().getValue("DOC_NO")))
      {
	  trans.clear();
	  cmd = trans.addCustomFunction("EDMINFO", "Edm_File_Api.Get_Edm_Information", "DUMMY1");
	  cmd.addParameter("DUMMY2", headset.getRow().getValue("DOC_CLASS"));
	  cmd.addParameter("DUMMY3", headset.getRow().getValue("DOC_NO"));
	  cmd.addParameter("DUMMY3", headset.getRow().getValue("DOC_SHEET"));
	  cmd.addParameter("DUMMY4", headset.getRow().getValue("DOC_REV"));
	  cmd.addParameter("DUMMY5", "ORIGINAL");
	  trans = mgr.perform(trans);

	  String edm_info = trans.getValue("EDMINFO/DATA/DUMMY1");

	  if ("FALSE".equals(DocmawUtil.getAttributeValue(edm_info, "VIEW_REF_EXIST")))
	  {
	      mgr.showAlert("DOCMAWDOCISSUENOVIEWREFERENCE: This document revision does not have a view file reference.");
	      return;
	  }

	  if (isValidColumnValue("GETVIEWACCES", "TRUE", true))
	  {
	      ASPBuffer buff = mgr.newASPBuffer();
	      buff.addItem("FILE_ACTION", "PRINTCOPY");
	      buff.addItem("DOC_TYPE", "VIEW");
	      buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
	      transferToEdmMacro(buff);
	  }
	  else
	  {
	      if (headlay.isMultirowLayout())
		  mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSPRINTMULTI: You don't have view access to one or more of the selected documents to print.");
	      else
		  mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSPRINT: You must have view access to be able to print this document.");

	  }
	  //Bug Id 73718, end
      }
      else //Non-structure documents
      {
	  bPrintViewCopy = true;
	  printDocument();
      }
      //Bug Id 77556, end
   }
   //Bug Id 56685, End

   public void checkInSelectDocument()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      String action = "CHECKINSEL";
      String doc_type = "ORIGINAL";
      String same_action = "NO";

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }

      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {  
         return;
      }
      //Bug Id 67336, end

      //check if the doc is trapped in a bc

      if (isValidColumnValue("VALIDATE_FILE_ACTION","FALSE",true) )
      {
         mgr.showAlert("DOCMAWDOCISSUENOCHECKININBC: This document(s) is reserved for a briefcase and cannot be Checkede In.");
         return;
      }


      if (isValidColumnValue("GETEDITACCESS", "TRUE", true))
      {
         if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
            same_action = "YES";

         buff.addItem("DOC_TYPE",doc_type);
         buff.addItem("FILE_ACTION",action);
         buff.addItem("SAME_ACTION_TO_ALL",same_action);
         transferToEdmMacro(buff);
      }
      else
         mgr.showAlert("DOCMAWDOCISSUECANNOTCHECKIN: You must have edit access to be able to check documents in.");
   }
   
   public void  checkInDocument()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();
      
      String action = "CHECKIN";
      String doc_type = "ORIGINAL";
      String same_action = "NO";
      
      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOROWS: No Rows Selected."));
         return;
      }
      
      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
         return;
      }
      //Bug Id 67336, end
      
      //check if the doc is trapped in a bc
      
      if (isValidColumnValue("VALIDATE_FILE_ACTION","FALSE",true) )
      {
         mgr.showAlert("DOCMAWDOCISSUENOCHECKININBC: This document(s) is reserved for a briefcase and cannot be Checkede In.");
         return;
      }
      
      
      if (isValidColumnValue("GETEDITACCESS", "TRUE", true))
      {
         if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
            same_action = "YES";
         
         buff.addItem("DOC_TYPE",doc_type);
         buff.addItem("FILE_ACTION",action);
         buff.addItem("SAME_ACTION_TO_ALL",same_action);
         transferToEdmMacro(buff);
      }
      else
         mgr.showAlert("DOCMAWDOCISSUECANNOTCHECKIN: You must have edit access to be able to check documents in.");
   }


   public void  undoCheckOut()
   {
      ASPManager mgr = getASPManager();

      // Modified by Terry 20121024
      // Original:
      
      //if (!isValidColumnValue("EDM_STATUS", sCheckedOut, true))
      // {
      //    if (headlay.isMultirowLayout())
      //       mgr.showAlert("DOCMAWDOCISSUENOTBEENCHECKEDOUTYETMULTI: One or more documents have not been checked out.");
      //    else
      //       mgr.showAlert("DOCMAWDOCISSUENOTBEENCHECKEDOUTYET: This document's files have not been checked out.");
      // }
      // else
      // {
         if (!isValidColumnValue("GETEDITACCESS", "TRUE", true))
            mgr.showAlert("DOCMAWDOCISSUECANNOTUNDOCHECKOUT: You need edit access to undo a previous check out.");
         else
            transferToEdmMacro("ORIGINAL", "UNDOCHECKOUT");
      // }
   }


   public void  userSettings()
   {
      sUrl = "../docmaw/dlgUserSettings.page?PARAM=DOCISSUE";
      bOpenWizardWindow = true;
   }

   //Bug Id 67105, Start
   public void mandatorySettings()
   {
	   sUrl = "../docmaw/MandatorySearchFieldsConfigDlg.page?URL="+getPoolKey();
	   bOpenWizardWindow = true;
   }
   //Bug Id 67105, End


   public String getoriginaldocstate()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomCommand("EDMDOCSTATUS", "EDM_FILE_API.Get_Document_Status");
      cmd.addParameter("DUMMY1");
      cmd.addParameter("DUMMY2");
      cmd.addParameter("DUMMY3");
      cmd.addParameter("DUMMY4");
      cmd.addParameter("DUMMY5");
      cmd.addParameter("DUMMY6");
      cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
      cmd.addParameter("DOC_TYPE", "ORIGINAL");

      trans = mgr.perform(trans);
      String statusdoc  = trans.getValue("EDMDOCSTATUS/DATA/DUMMY1");
      trans.clear();
      return statusdoc;
   }


   public String getCommentdocstate()
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      cmd = trans.addCustomCommand("EDMDOCSTATUS", "EDM_FILE_API.Get_Document_Status");
      cmd.addParameter("DUMMY1");
      cmd.addParameter("DUMMY2");
      cmd.addParameter("DUMMY3");
      cmd.addParameter("DUMMY4");
      cmd.addParameter("DUMMY5");
      cmd.addParameter("DUMMY6");
      cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
      cmd.addParameter("DOC_TYPE", "REDLINE");
      trans = mgr.perform(trans);
      String statuscom  = trans.getValue("EDMDOCSTATUS/DATA/DUMMY1");
      trans.clear();
      return statuscom;
   }


   public void viewComment()
   {
      ASPManager mgr = getASPManager();

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSFORCOMMENT: You must have view access to be able to view the comment file.");
         return;
      }

      /*if (!isValidColumnValue("STATE", sApproved, sReleased, false))
      {
         mgr.showAlert("DOCMAWDOCISSUECANNOTVIEWCOMMENTINAPPORREL: The comment file is not available for viewing when the document is in state Approved or Released.");
         return;
      }*/

      transferToEdmMacro("REDLINE", "VIEW");
   }


   public void viewCommentFromTransmittal()
   {
      ASPManager mgr = getASPManager();

      if (itemlay9.isSingleLayout())
      {
         itemset9.unselectRows();
         itemset9.selectRow();
      }
      else
         itemset9.selectRows();

      ASPBuffer data = itemset9.getSelectedRows("ITEM9_DOC_CLASS,ITEM9_DOC_NO,ITEM9_DOC_SHEET,ITEM9_DOC_REV,ITEM9_FILE_NO");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", "VIEWCOMMENT", "REDLINE", data);
      bTranferToEDM = true;
   }


   public void editComment()
   {
      if (headlay.isMultirowLayout())
         headset.selectRows();

      strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();
      RedlineApp = true;
   }


   public void editCommentFile()
   {
      ASPManager mgr = getASPManager();

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         mgr.showAlert("DOCMAWDOCISSUENOEDITACCESSFORCOMMENT: You must have view access to be able to edit the comment file.");
         return;
      }

      if (!isValidColumnValue("STATE", sApproved, sReleased, false))
      {
	  //Bug Id 70512 Start
	if(!isCommentUpdateEnabled()){
	 mgr.showAlert("DOCMAWDOCISSUECANNOTEDITCOMMENTINAPPORREL: The comment file is not available for editing when the document is in state Approved or Released.");
         return;
	}
	//Bug Id 70512 End
         
      }
      
      //Bug Id 70512 Start
      if (!isValidColumnValue("STATE", sObsolete, false))
      {

	 mgr.showAlert("DOCMAWDOCISSUECANNOTEDITCOMMENTINOBSOLETE: The comment file is not available for editing when the document is in state Obsolete.");
         return;

      }
      //Bug Id 70512 End
      
      String fndUser = getFndUser();
      String docstateorg = headset.getRow().getValue("EDM_STATUS");

      trans.clear();
      cmd = trans.addCustomCommand("EDMDOCSTATUS", "EDM_FILE_API.Get_Document_Status");
      cmd.addParameter("DUMMY1");
      cmd.addParameter("DUMMY2");
      cmd.addParameter("DUMMY3");
      cmd.addParameter("DUMMY4");
      cmd.addParameter("DUMMY5");
      cmd.addParameter("DUMMY6");
      cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO",    headset.getRow().getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV",   headset.getRow().getValue("DOC_REV"));
      cmd.addParameter("DOC_TYPE",  "REDLINE");

      cmd = trans.addCustomFunction("EDMINFO", "Edm_File_Api.Get_Edm_Information", "DUMMY1");
      cmd.addParameter("DUMMY2", headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DUMMY3", headset.getRow().getValue("DOC_NO"));
      cmd.addParameter("DUMMY3", headset.getRow().getValue("DOC_SHEET"));
      cmd.addParameter("DUMMY4", headset.getRow().getValue("DOC_REV"));
      cmd.addParameter("DUMMY5", "ORIGINAL");
      trans = mgr.perform(trans);

      String edmInfo = trans.getValue("EDMINFO/DATA/DUMMY1");
      String status  = trans.getValue("EDMDOCSTATUS/DATA/DUMMY1");
      String checkOutUser = trans.getValue("EDMDOCSTATUS/DATA/DUMMY2");

      //Bug Id 77774, start
      if (status == null) 
      {
         trans.clear();
         cmd = trans.addCustomFunction("REDLINEAPPEXIST","Edm_Application_API.Check_Document_Type_Exist", "OUT_1");
               cmd.addParameter("DUMMY1", "REDLINE");
               
         trans = mgr.perform(trans);
         String doc_type_exists = trans.getValue("REDLINEAPPEXIST/DATA/OUT_1");

         if ( !"1".equals(doc_type_exists)) 
         {
            mgr.showAlert("DOCMAWDOCISSUENOREDLINEAPPLICATION: There is no application defined for redline/comment files. This can be done under Basic Data/EDM Basic/Applications.");
            return;
         }
      }
      //Bug Id 77774, end

      if (sCheckedOut.equals(docstateorg))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANNOTEDITCOI: Original file is Checked Out. Editing of Comment file is not allowed"));
      }
      else if ((sCheckedOut.equals(status)) && (checkOutUser.equals(fndUser)))
      {
         trans.clear();
         cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "DUMMY6");
         cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",    headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",   headset.getRow().getValue("DOC_REV"));
         cmd.addParameter("DOC_TYPE", "REDLINE");
         trans = mgr.perform(trans);

         String edmRepInfo = trans.getValue("EDMREPINFO/DATA/DUMMY6");
         String sLocalFileName = getStringAttribute(edmRepInfo, "REDLINE_FILE_NAME");
         launchFile = true;
         strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();
         sClientFunction = "oCliMgr.LaunchFileWithRedlineViewer(sDocumentFolder +'" +  sLocalFileName + "');";
         return;
      }
      else if ((sCheckedOut.equals(status)) && (!(checkOutUser.equals(fndUser))))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUECHECKEDOUTMSG2: The document is checked out by another user."));
         return;
      }
      else if (mgr.isEmpty(status))
      {
         //
         // If we pass null, in the call to edmMacro, the user will be
         // prompted to select the redline application. We'll have it this
         // way for now.
         //

         transferToEdmMacro("REDLINE", "CREATENEW");
      }
      else
         transferToEdmMacro("REDLINE", "CHECKOUT");
   }


   public void checkInComment()
   {
      ASPManager mgr = getASPManager();

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         mgr.showAlert("DOCMAWDOCISSUENOCHECKINACCESSFORCOMMENT: You must have view access to be able to check the comment file in.");
         return;
      }

      transferToEdmMacro("REDLINE", "CHECKIN");
   }


   public void deleteComment()
   {
      ASPManager mgr = getASPManager();

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         mgr.showAlert("DOCMAWDOCISSUENODELETEACCESSFORCOMMENT: You must have view access to be able to delete the comment file.");
         return;
      }


      if (!isValidColumnValue("STATE", sApproved, sReleased, false))
      {
	   //Bug Id 70512 Start
	  if(!isCommentUpdateEnabled())
	  {
	    mgr.showAlert("DOCMAWDOCISSUECANNOTDELETECOMMENTINAPPORREL: The comment file is not available for deleting when the document is in state Approved or Released.");
	    return;
	  }   
	   //Bug Id 70512 End
      }

      //Bug Id 70512 Start
      if (!isValidColumnValue("STATE",sObsolete, false))
      {

	 mgr.showAlert("DOCMAWDOCISSUECANNOTDELETECOMMENTINOBSOLETE: The comment file is not available for deleting when the document is in state Obsolete.");
	 return;

      }
      //Bug Id 70512 End
      transferToEdmMacro("REDLINE", "DELETE");
   }


   public String isAccessOwner()
   {
      int noOfRowsSelected = 1;
      ASPManager mgr = getASPManager();
      String accessowner = "FALSE";

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         noOfRowsSelected = headset.countRows();
      }
      else
         headset.selectRow();

      String fnd_user = getFndUser();

      trans.clear();
      int count = 0;
      for (count=0;count<noOfRowsSelected;count++)
      {
         //Bug 53039, Start, Added check on bDoCheckForAllAccess
         if (dGroup.equals(headset.getRow().getValue("ACCESS_CONTROL"))||bDoCheckForAllAccess)
         {
            cmd = trans.addCustomFunction("ISACCESSOWNER" + count,"Document_Issue_Access_Api.Is_User_Access_Owner", "ACCESSOWNER");
            cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO",    headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV",   headset.getRow().getValue("DOC_REV"));
            cmd.addParameter("USER_ID",   fnd_user);
         }
         //Bug 53039, End
         if (headlay.isMultirowLayout())
         {
            headset.next();
         }

      }

      trans = mgr.perform(trans);

      if (headlay.isMultirowLayout())
      {
         headset.first();
      }
      count = 0;
      for (count=0;count<noOfRowsSelected;count++)
      {
         //Bug 53039, Start, Added check on bDoCheckForAllAccess
         if (dGroup.equals(headset.getRow().getValue("ACCESS_CONTROL"))||bDoCheckForAllAccess)
         {
            accessowner = trans.getValue("ISACCESSOWNER" + count + "/DATA/ACCESSOWNER");
         }
         //Bug 53039, End
         else
         {
            // Bug 57779, Start
            if (isUserDocmanAdministrator() || (headset.getRow().getValue("USER_CREATED").equals(fnd_user)))
               // Bug 57779, End
               accessowner = "TRUE";
            else
               accessowner = "FALSE";
         }
         if ("FALSE".equals(accessowner))
         {
            break;
         }

         if (headlay.isMultirowLayout())
         {
            headset.next();
         }
      }

      //Bug 53039, Start
      if (bDoCheckForAllAccess)
         bDoCheckForAllAccess = false;
      //Bug 53039, End
      if (headlay.isMultirowLayout())
      {
         headset.setFilterOff();
      }
      return accessowner;
   }


   public String hasEditAccess()
   {

      ASPManager mgr = getASPManager();
      String accessowner= "EDIT";
      int noOfRowsSelected = 1;
      int count;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         noOfRowsSelected = headset.countRows();
      }
      else
      {
         headset.selectRow();
      }

      if (noOfRowsSelected==0)
      {
         return "FALSE";
      }

      String fnd_user = getFndUser();
      String person_id  = getPersonId();

      trans.clear();
      for (count = 0;count<noOfRowsSelected;count++)
      {
         cmd = trans.addCustomFunction("EDITACCESS" + count,"Document_Issue_Access_API.Person_Get_Access","OUT_1");
         cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV"));
         cmd.addParameter("PERSON_ID",person_id);
         if (headlay.isMultirowLayout())
         {
            headset.next();
         }
      }

      trans = mgr.perform(trans);

      for (count=0;count<noOfRowsSelected;count++)
      {
         accessowner = trans.getValue("EDITACCESS" + count+ "/DATA/OUT_1");
         if (!"EDIT".equals(accessowner))
         {
            break;
         }
      }
      if (headlay.isMultirowLayout())
      {
         headset.setFilterOff();
      }

      return accessowner;
   }


   public void deleteDocument()
   {
      ASPManager mgr = getASPManager();
      String fnd_user = getFndUser();
      String person_id = getPersonId();
      int noOFRowsSelected = 1;
      int count= 0;

      //Bug 53039, Start
      if (dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
         bDoCheckForAllAccess = true;
      else
         bDoCheckForAllAccess = false;
      //Bug 53039, End

      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
	  return;
      }
      //Bug Id 67336, end

      // Bug 57779, Start
      if ("TRUE".equals(isAccessOwner()) || isUserDocmanAdministrator())
      {
         // Bug 57779, End
         if (headlay.isMultirowLayout())
         {
            headset.storeSelections();
            headset.setFilterOn();
            noOFRowsSelected = headset.countRows();
         }
         else
         {
            headset.selectRow();
         }
         boolean error = false;
         for (count=0;count<noOFRowsSelected;count++)
         {
            if (!mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")) && !sCheckedIn.equals(headset.getRow().getValue("SEDMSTATUS")))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUEDELETEALLALLFILESMUSTBECHECKEDIN: All document file(s) must be checked in before deleting."));
               error = true;
               break;
            }

            if (!sObsolete.equals(headset.getRow().getValue("STATE")))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUEDELETEALLNOTCORRECTSTATE: Only documents in state Obsolete can be deleted."));
               error = true;
               break;
            }

            if (sCheckedOut.equals(headset.getRow().getValue("REDLINE_FILE_STATUS")))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUEDELETEALLREDLINECHECKOUT: Comment files must be checked in before deleting."));
               error = true;
               break;
            }
            if (headlay.isMultirowLayout())
            {
               headset.next();
            }

         }

         if (error)
         {
            if (headlay.isMultirowLayout())
            {
               headset.setFilterOff();
            }
            return;
         }

         ctx.writeValue("OPERATION", "DELETEALL");

         ASPBuffer buff = mgr.newASPBuffer();
         buff.addItem("DOC_TYPE", "ORIGINAL");
         buff.addItem("FILE_ACTION", "DELETEALL");
         buff.addItem("SAME_ACTION_TO_ALL", (mgr.readValue("SAME_ACTION_TO_ALL") == null) ? "NO" : "YES");
         transferToEdmMacro(buff);
         if (headlay.isMultirowLayout())
         {
         }
         headset.setFilterOff();
      }
      else
      {
         if (noOFRowsSelected > 1)
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTACCESSOWNERMULTI: You don't have administrative rights to delete selected documents and their files "));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTACCESSOWNER: You don't have administrative rights to delete this document and its file(s)"));
      }
   }

   public void deleteDocumentFile()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      String action = "DELETE";
      String doc_type = "ORIGINAL";
      String same_action = "NO";
      String fnd_user = getFndUser();
      String person_id = getPersonId();
      int noOfRowsSelected = 1;

      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
	  return;
      }
      //Bug Id 67336, end

      // Bug 57779, Start
      if ((isValidColumnValue("GETEDITACCESS", "TRUE", true) && "EDIT".equals(hasEditAccess())) || isUserDocmanAdministrator() || dAll.equals(headset.getRow().getValue("ACCESS_CONTROL"))) //Bug 67441
      // Bug 57779, End
      {
         if (headlay.isMultirowLayout())
         {
            headset.storeSelections();
            headset.setFilterOn();
            noOfRowsSelected = headset.countRows();
         }
         else
         {
            headset.selectRow();
         }

         trans.clear();
         boolean error = false;
         int count = 0;
         for (count=0;count<noOfRowsSelected;count++)
         {
            if (mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")))
            {
               if (headlay.isMultirowLayout())
                  mgr.showAlert(mgr.translate("DOCMAWDOCISSUEMULTINOTOBSOLETE: You are attempting to delete file(s) from one or more documents that have no file(s) checked in."));
               else
                  mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTOBSOLETE: You are attempting to delete file(s) from a document that has no file(s) checked in."));

               error = true;
               break;
            }

            if (!(headset.getRow().getValue("SEDMSTATUS").equals(sCheckedIn)))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUEALLFILESMUSTBECHECKEDIN: All document file(s) must be checked in before deleting."));
               error = true;
               break;
            }

            //Bug Id 57664, Start
            if (!sPrelimin.equals(headset.getRow().getValue("STATE")) && !sObsolete.equals(headset.getRow().getValue("STATE")) && !("TRUE".equals(headset.getValue("APPROVAL_UPDATE")) && sAppInProg.equals(headset.getValue("STATE"))))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTCORRECTSTATE: You are not allowed to delete document file(s) when the document is in state ") + headset.getRow().getValue("STATE"));
               error = true;
               break;
            }
            //Bug Id 57664, End

            cmd = trans.addCustomFunction("CHECKEDOUTREDLINE" + count, "Edm_File_API.Get_Check_Out2", "OUT_1");
            cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO",    headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV",   headset.getRow().getValue("DOC_REV"));
            cmd.addParameter("DOC_TYPE",  "REDLINE");
            if (headlay.isMultirowLayout())
            {
               headset.next();
            }

         }

         if (error)
         {
            if (headlay.isMultirowLayout())
            {
               headset.setFilterOff();
            }
            return;
         }

         trans = mgr.perform(trans);
         String redline_state;
         for (int x = 0; x < noOfRowsSelected; x++)
         {
            redline_state = trans.getValue("CHECKEDOUTREDLINE"+x+"/DATA/OUT_1");
            if ("TRUE".equals(redline_state))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUEREDLINECHECKOUT: The Redline file must be checked in before deleting."));
               if (headlay.isMultirowLayout())
               {
                  headset.setFilterOff();
               }
               return;
            }
         }

         if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
            same_action = "YES";

         buff.addItem("DOC_TYPE",doc_type);
         buff.addItem("FILE_ACTION",action);
         buff.addItem("SAME_ACTION_TO_ALL",same_action);
         transferToEdmMacro(buff);
         if (headlay.isMultirowLayout())
         {
            headset.setFilterOff();
         }


      }
      else
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILEMULTI: You don't have the necessary access rights to selected documents in order to delete their files"));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILE: You don't have the necessary access rights to the document in order to delete its file(s)"));

      }

   }
   
   public void deleteSelectDocFile()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      String action = "DELETESEL";
      String doc_type = "ORIGINAL";
      String same_action = "NO";
      String fnd_user = getFndUser();
      String person_id = getPersonId();
      int noOfRowsSelected = 1;

      // Bug Id 67336, start
      if (checkFileOperationEnable())
      {
         return;
      }
      // Bug Id 67336, end

      // Bug 57779, Start
      if ((isValidColumnValue("GETEDITACCESS", "TRUE", true) && "EDIT".equals(hasEditAccess()))
            || isUserDocmanAdministrator()
            || dAll.equals(headset.getRow().getValue("ACCESS_CONTROL"))) // Bug 67441
      // Bug 57779, End
      {
         if (headlay.isMultirowLayout())
         {
            headset.storeSelections();
            headset.setFilterOn();
            noOfRowsSelected = headset.countRows();
         }
         else
         {
            headset.selectRow();
         }

         trans.clear();
         boolean error = false;
         int count = 0;
         for (count = 0; count < noOfRowsSelected; count++)
         {
            if (mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")))
            {
               if (headlay.isMultirowLayout())
                  mgr.showAlert(mgr.translate("DOCMAWDOCISSUEMULTINOTOBSOLETE: You are attempting to delete file(s) from one or more documents that have no file(s) checked in."));
               else
                  mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTOBSOLETE: You are attempting to delete file(s) from a document that has no file(s) checked in."));

               error = true;
               break;
            }

            if (!(headset.getRow().getValue("SEDMSTATUS").equals(sCheckedIn)))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUEALLFILESMUSTBECHECKEDIN: All document file(s) must be checked in before deleting."));
               error = true;
               break;
            }

            // Bug Id 57664, Start
            if (!sPrelimin.equals(headset.getRow().getValue("STATE"))
                  && !sObsolete.equals(headset.getRow().getValue("STATE"))
                  && !("TRUE".equals(headset.getValue("APPROVAL_UPDATE")) && sAppInProg.equals(headset.getValue("STATE"))))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOTCORRECTSTATE: You are not allowed to delete document file(s) when the document is in state ") + headset.getRow().getValue("STATE"));
               error = true;
               break;
            }
            // Bug Id 57664, End

            cmd = trans.addCustomFunction("CHECKEDOUTREDLINE" + count, "Edm_File_API.Get_Check_Out2", "OUT_1");
            cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", headset.getRow().getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", headset.getRow().getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", headset.getRow().getValue("DOC_REV"));
            cmd.addParameter("DOC_TYPE", "REDLINE");
            if (headlay.isMultirowLayout())
            {
               headset.next();
            }
         }

         if (error)
         {
            if (headlay.isMultirowLayout())
            {
               headset.setFilterOff();
            }
            return;
         }

         trans = mgr.perform(trans);
         String redline_state;
         for (int x = 0; x < noOfRowsSelected; x++)
         {
            redline_state = trans.getValue("CHECKEDOUTREDLINE" + x + "/DATA/OUT_1");
            if ("TRUE".equals(redline_state))
            {
               mgr.showAlert(mgr.translate("DOCMAWDOCISSUEREDLINECHECKOUT: The Redline file must be checked in before deleting."));
               if (headlay.isMultirowLayout())
               {
                  headset.setFilterOff();
               }
               return;
            }
         }

         if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
            same_action = "YES";

         buff.addItem("DOC_TYPE", doc_type);
         buff.addItem("FILE_ACTION", action);
         buff.addItem("SAME_ACTION_TO_ALL", same_action);
         transferToEdmMacro(buff);
         if (headlay.isMultirowLayout())
         {
            headset.setFilterOff();
         }

      }
      else
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILEMULTI: You don't have the necessary access rights to selected documents in order to delete their files"));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTODELETEFILE: You don't have the necessary access rights to the document in order to delete its file(s)"));
      }
   }

   public void deleteRow()
   {
      ASPManager mgr = getASPManager();

      headset.store();
      if (headlay.isMultirowLayout())
      {
         headset.setSelectedRowsRemoved();
         headset.unselectRows();
      }
      else
         headset.setRemoved();

      mgr.submit(trans);
      //Refresh the headset only if there are items.
      if (headset.countRows() > 0)
         refreshHeadset();

   }


   public void sendToMailRecipient()
   {

      ASPManager mgr = getASPManager();
      /*if (isEmptyColumnValue("CHECKED_IN_SIGN"))
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOMAILEMPTYFILEMULTI: One or more documents you're trying to mail has no file checked in yet."));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOMAILEMPTYFILE: The document you're trying to mail has no file checked in yet."));
         return;
      }*/

      transferToEdmMacro("ORIGINAL", "SENDMAIL");
   }


   public void setToCopyFile()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSCOPYFILETOMULTI: You don't have view access to one or more of the selected documents.");
         else
            mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSCOPYFILETO: You must have view access to be able to copy this document's files.");

         return;
      }

      /*if (isEmptyColumnValue("CHECKED_IN_SIGN"))
      {
         if (headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOCOPYEMPTYFILEMULTI: One or more documents you're trying to copy has no file checked in yet."));
         else
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOCOPYEMPTYFILE: The document you're trying to copy has no file checked in yet."));
         return;
      }*/

      //Bug Id 67336, start
      if (checkFileOperationEnable()) 
      {	
         return;
      }
      //Bug Id 67336, end

      String action = "GETCOPYTODIR";
      String doc_type = "ORIGINAL";
      // Modified by Terry 20120926
      // Original: String same_action = "NO";
      String same_action = "YES";
      // Modified end
      String cpy_dir = "";
      // Modified by Terry 20120926
      // Original:
      // if ("YES".equals(mgr.readValue("SAME_ACTION_TO_ALL")))
      // {
      //    same_action = "YES";
      // }

      buff.addItem("DOC_TYPE",doc_type);
      buff.addItem("FILE_ACTION",action);
      buff.addItem("SAME_ACTION_TO_ALL",same_action);
      buff.addItem("SELECTED_DIRECTORY",cpy_dir);
      transferToEdmMacro(buff);
   }


   public void createNewRevision()
   {
      ASPManager mgr = getASPManager();
      String userAccess;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      headset.refreshRow();

      // Bug id 88317 start
      // Modified by Terry 20120927
      // Original:
      /*trans.clear();
      
      cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
      cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
      cmd.addReference("DUMMY2", "FNDUSER/DATA");
      
      trans = mgr.perform(trans);
      String person_id = trans.getValue("STARUSER/DATA/DUMMY1");
      
      
      if ("*".equals(person_id)){
         mgr.showAlert(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
         return;
      }*/
      // Bug id 88317 end
      // Modified end

      trans.clear(); //Bug Id 40809
      //Bug Id 75490, Start
      if (! mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")) && ! headset.getRow().getValue("SEDMSTATUS").equals(sCheckedIn))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUECHECKINNOTCREATENEWREV: You can not create a new revision for documents in status") + " '" +headset.getRow().getValue("SEDMSTATUS")+"'"); //Bug Id 76849
         headset.setFilterOff();
         return;
      }
      //Bug Id 75490, End
      cmd = trans.addCustomFunction("FNDUSER","Fnd_Session_API.Get_Fnd_User","LOGUSER");
      trans = mgr.perform(trans);
      String fndUser = trans.getValue("FNDUSER/DATA/LOGUSER");
      trans.clear();
      
      if (dGroup.equals(headset.getRow().getValue("ACCESS_CONTROL")))
      {
         cmd = trans.addCustomFunction("USERACCESS","DOCUMENT_ISSUE_ACCESS_API.User_Get_Access","USERGETACCESS");
         cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV"));
         cmd.addParameter("LOGUSER",fndUser);
         trans = mgr.perform(trans);
         
         userAccess = trans.getValue("USERACCESS/DATA/USERGETACCESS");
         trans.clear();
      }
      else if (dAll.equals( headset.getRow().getValue("ACCESS_CONTROL")))
         userAccess="EDIT";
      else
      {
         // Bug 57778, Start
         if (isUserDocmanAdministrator() || (headset.getRow().getValue("USER_CREATED").equals(fndUser)))
            // Bug 57778, End
            userAccess="EDIT";
         else
            userAccess="";
      }
      
      if ("EDIT".equals(userAccess))
      {
         ASPBuffer act_buf = mgr.newASPBuffer();
         act_buf.addItem("DOC_TYPE","ORIGINAL");
         act_buf.addItem("ACTION","NEW_REVISION");
         
         ASPBuffer data_buff = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
         
         sUrl = DocumentTransferHandler.getDataTransferUrl(mgr,"NewRevisionWizard.page",act_buf,data_buff);
         bOpenWizardWindow = true;
         modifySubWindow4NewRev = true;
      }
      else
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOEDITACCESSTOCREATENEWREV: You must have edit access to be able to create a new revision of this document."));
      
      headset.setFilterOff();
      headset.unselectRows();
   }

   //Bug Id 67336, start
   private boolean checkFileOperationEnable()
   {
       ASPManager mgr = getASPManager();
       if (headlay.isMultirowLayout())
       {
	   headset.storeSelections();
	   headset.setFilterOn();
	   String prestructure = " ";
	   String structure;
	   if (headset.countSelectedRows() > 1)
	   {
	       for (int k = 0;k < headset.countSelectedRows();k++)
	       {
		   structure = headset.getValue("STRUCTURE");
		   if (" ".equals(prestructure)) 
		   {
		       prestructure = structure;
		   }

                   if (!prestructure.equals(structure)) 
		   {
		       mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
		       headset.setFilterOff();
		       return true;
		   }

		   if ("TRUE".equals(prestructure) && "TRUE".equals(structure)) 
		   {
		       mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
		       headset.setFilterOff();
		       return true;
		   }
		   prestructure = structure;
		   headset.next();
	       }
	   }
	   headset.setFilterOff();
       }
       return false;
   }
   //Bug Id 67336, end

   public void previousLevel()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("DOCSTRUC", "DOC_STRUCTURE_API.Number_Of_Parents_", "RETURN");
      cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO",    headset.getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV",   headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);

      // If no parent documents revisions found then..
      if ("0".equals(trans.getValue("DOCSTRUC/DATA/RETURN")))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUETOPSTRUC: You are now at the top of the document structure."));
      }
      else
      {
         // Fetch the list of parent documents..

	 //SQLInjections_Safe AMNILK 20070810

	 StringBuffer sql = new StringBuffer("SELECT DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV FROM DOC_STRUCTURE WHERE SUB_DOC_CLASS = ?");
         sql.append(" AND SUB_DOC_NO = ?");
         sql.append(" AND SUB_DOC_SHEET = ?");
         sql.append(" AND SUB_DOC_REV = ?");

         trans.clear();
         q = trans.addQuery("PARENTS", sql.toString());
	 q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
	 q.addParameter("DOC_NO",headset.getValue("DOC_NO"));
	 q.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
	 q.addParameter("DOC_REV",headset.getValue("DOC_REV"));

         trans = mgr.perform(trans);

         // Redirect to parent document revisions..
         trans.getBuffer("PARENTS").removeItem("INFO");
         mgr.transferDataTo("DocIssue.page", trans.getBuffer("PARENTS"));
      }
   }

   public void  copyApprovalTemplate()
   {
      ASPManager mgr = getASPManager();

      //Bug 53039, Start
      if (!dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
      {
         bDoCheckForAllAccess = false;
         if (!"TRUE".equals(isAccessOwner()))
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOPERMISSIONTOCOPYAPPROVALTMPL: You must have administrative rights to be able to copy an approval template."));
            return;
         }
      }
      //Bug 53039, End

      if (!isValidColumnValue("STATE", sApproved, sReleased, false))
      {
         mgr.showAlert("DOCMAWDOCISSUECANNOTCOPYAPPROVALTEMPLINAPPORREL: You cannot copy an approval template when the document is in state Approved or Released.");
         return;
      }

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();

      }
      else
      {
         headset.selectRow();
      }

      int n = itemset8.addRow(mgr.newASPBuffer());
      itemset8.goTo(n);
      bCopyProfile = true;
   }

   //DMPR303 START
   public void createLink()
   {
      ASPManager mgr = getASPManager();
      transferToCreateLink();
   }

   public void transferToCreateLink()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout())
      {
         headset.unselectRows();
         headset.selectRow();
      }
      else
         headset.selectRows();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,DOCTITLE");

      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "CreateLink.page", data);
      bTranferToCreateLink = true;
   }
   //DMPR303 END.

   public void  dlgOk()
   {
      ASPManager mgr = getASPManager();

      itemset8.changeRow();
      String profile_id = mgr.readValue("TEMP_PROFILE_ID");
      int action_done_on = headlay.isMultirowLayout()? headset.countRows():1;
      //enable for multirow actions

      //Bug Id 85876, Start
      trans.clear();
      cmd = trans.addCustomFunction("CHECKCOPYAPP", "Approval_Template_API.Steps_Exists","DUMMY3");
      cmd.addParameter("TEMP_PROFILE_ID",profile_id);
      trans = mgr.perform(trans);
      if ("FALSE".equals(trans.getValue("CHECKCOPYAPP/DATA/DUMMY3")))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEAPPTEMPLATENOUSERS: The selected Approval Template contains no step(s)."));
      }
      trans.clear();
      //Bug Id 85876, End

      for (int k = 0; k < action_done_on; k++)
      {
         cmd = trans.addCustomCommand("COPYAPP"+k, "APPROVAL_ROUTING_API.Copy_App_Profile");
         cmd.addParameter("DUMMY1");
         cmd.addParameter("DUMMY2");
         cmd.addParameter("LU_NAME", headset.getValue("LU_NAME"));
         cmd.addParameter("KEY_REF", headset.getValue("KEY_REF"));
         cmd.addParameter("TEMP_PROFILE_ID",profile_id);


         if (headlay.isMultirowLayout())
         {
            headset.next();
         }
      }

      trans = mgr.perform(trans);
      String persons = "";
      String groups  = "";

      for (int k=0;k<action_done_on;k++)
      {
         persons = trans.getValue("COPYAPP" + k + "/DATA/DUMMY1");
         groups  = trans.getValue("COPYAPP" + k + "/DATA/DUMMY2");
         if (!mgr.isEmpty(persons) || !mgr.isEmpty(groups))
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUEAPPTEMPLATEACCESS: The user(s) and/or Group(s) in the Approval Template who didn't have View Access to the document(s) have been given that right. Please verify that this is correct under the Access tab."));
            break;
         }
      }

      trans.clear();
      if (headlay.isMultirowLayout())
      {
         headset.setFilterOff();
      }

      itemset8.clear();

      if (headlay.isSingleLayout())
      {
         okFindITEM5();
         okFindITEM6();
         headset.refreshRow();   // Bug 54528, called to refresh parent
      }
   }


   public void  dlgCancel()
   {
      itemset8.clear();
      headset.setFilterOff();
      headset.unselectRows();

   }


   public void  performRefreshParent()
   {
      ASPManager mgr = getASPManager();

      //
      // Perform any necessary actions before
      // refreshing
      //


      if ("DELETEALL".equals(ctx.readValue("OPERATION")))
      {
         refreshHeadset();
         refreshActiveTab();
         return;
      }

      if ("RELEASE".equals(ctx.readValue("OPERATION")))
      {
         refreshHeadset();
         okFindITEM6();
      }

      //
      // Refresh the selected rows
      //
      if (headlay.isSingleLayout())
      {
         headset.refreshRow();
         eval(headset.syncItemSets());
         okFindITEM0();
         okFindITEM1();
         okFindITEM2();
         okFindITEM3();
         okFindITEM4();
         okFindITEM5();
         okFindITEM6();
         okFindITEM7();
         okFindITEM9();
      }
      else
      {
         if (headset.countSelectedRows() == 0)
            headset.selectRows();

         headset.setFilterOn();

         headset.first();
         do
         {
            headset.refreshRow(); // Note: this operation is expensive when doing for many rows
         } while (headset.next());

         headset.setFilterOff();

         if (!"YES".equals(mgr.readValue("LEAVE_ROWS_SELECTED")))
         {
            headset.unselectRows();
         }
      }
   }


   private void refreshActiveTab()
   {
      if (tabs.getActiveTab()== 1)
      {
         okFindITEM0();
      }
      else if (tabs.getActiveTab()== 2)
      {
         okFindITEM1();
      }
      else if (tabs.getActiveTab()== 3)
      {
         okFindITEM2();
      }
      else if (tabs.getActiveTab()== 4)
      {
         okFindITEM3();
      }
      else if (tabs.getActiveTab()== 5)
      {
         okFindITEM4();
      }
      else if (tabs.getActiveTab()== 6)
      {
         okFindITEM5();
      }
      else if (tabs.getActiveTab()== 7)
      {
         okFindITEM6();
      }
      else if (tabs.getActiveTab()== 8)
      {
         okFindITEM7();
      }
      else if (tabs.getActiveTab()== 9)
      {
         okFindITEM9();
      }
   }



   private String getStringAttribute(String attrString, String attrName)
   {
      StringTokenizer st = new StringTokenizer(attrString, "^");
      String str;

      attrName+="=";
      while (st.hasMoreTokens())
      {
         str = st.nextToken();
         if (str.startsWith(attrName))
         {
            return str.substring(attrName.length());
         }
      }
      return "";
   }


   public void nextAppStep()
   {
      ASPManager mgr = getASPManager();

      if (itemlay5.isMultirowLayout())
      {
         itemset5.storeSelections();
         itemset5.setFilterOn();
      }

      // Bug 58035, Start, show warning msg when trying to approve a step that has been alreasdy approved.
      if ("APP".equals(itemset5.getValue("APPROVAL_STATUS_DB")))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEALREADYAPPROVED: This step is already approved."));
         itemset5.unselectRows();
         return;
      }
      // Bug 58035, End

      cmd = trans.addCustomFunction("CHECKRIGHTS", "APPROVAL_ROUTING_API.Check_Ack_Rights", "RETURN");
      cmd.addParameter("LINE_NO",itemset5.getValue("LINE_NO"));
      cmd.addParameter("STEP_NO",itemset5.getValue("STEP_NO"));
      cmd.addParameter("PERSON_ID",itemset5.getValue("PERSON_ID"));
      cmd.addParameter("LU_NAME",itemset5.getValue("LU_NAME"));
      cmd.addParameter("KEY_REF",itemset5.getValue("KEY_REF"));
      cmd.addParameter("GROUP_ID",itemset5.getValue("GROUP_ID"));

      ASPCommand cmdCheckActive = trans.addCustomFunction("SECURITYCHECKPOINTACTIVE","Security_SYS.Security_Checkpoint_Activated", "DUMMY3");
      cmdCheckActive.addParameter("DUMMY1", "DOCMAN_APPROVE_STEP");

      trans = mgr.perform(trans);
      String sReturnVal = trans.getValue("CHECKRIGHTS/DATA/RETURN");
      String secutiry_checkpoint_active = trans.getValue("SECURITYCHECKPOINTACTIVE/DATA/DUMMY3");

      trans.clear();

      if ("TRUE".equals(sReturnVal))
      {

         if ("FALSE".equals(itemset5.getRow().getFieldValue("ITEM5_SECURITY_CHECKPOINT_REQ")) || "FALSE".equalsIgnoreCase(secutiry_checkpoint_active))
         {
            debug("DEBUG : isSecurityCheckpointRequired -FALSE");
            bConfirm = true;
            sMessage = mgr.translate("DOCMAWDOCISSUEQAPPSTEP: Approve Step?");
            ctx.writeValue("CONFIRMFUNC","onConfirmNextAppStep()");
            ctx.writeValue("UNCONFIRMFUNC","onUnconfirm()");
         }
         else
         {
            debug("DEBUG : isSecurityCheckpointRequired -TRUE");
            onConfirmNextAppStep();
         }            
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUENORIGHTSAPPROVE: You do not have rights to approve this step"));
         itemset5.unselectRows();
      }
      itemset5.setFilterOff();
   }


   public void  onConfirmNextAppStep()
   {
      ASPManager mgr = getASPManager();

      if (itemlay5.isMultirowLayout())
         itemset5.setFilterOn();

      trans.clear();
      cmd = trans.addCustomCommand("APPROVESTEPS", "APPROVAL_ROUTING_API.Set_Next_App_Step");
      cmd.addParameter("ITEM5_LU_NAME",itemset5.getValue("LU_NAME"));
      cmd.addParameter("ITEM5_KEY_REF",itemset5.getValue("KEY_REF"));
      cmd.addParameter("LINE_NO",itemset5.getValue("LINE_NO"));
      cmd.addParameter("STEP_NO",itemset5.getValue("STEP_NO"));
      cmd.addParameter("ATTR","APP");
      trans = mgr.perform(trans);

      itemset5.setFilterOff();
      itemset5.unselectRows();
      // Bug 58049, Start, Refresh the current row only
      itemset5.refreshRow();
      // Bug 58049, End
   }


   public void rejectStep()
   {
      ASPManager mgr = getASPManager();

      if (itemlay5.isMultirowLayout())
      {
         itemset5.storeSelections();
         itemset5.setFilterOn();
      }
      else
         itemset5.selectRow();

      if ("REJ".equals(itemset5.getValue("APPROVAL_STATUS_DB")))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUEALREADYREJECTED: This step is already rejected.")); // Bug 58035, modified the msg text
         itemset5.unselectRows();
      }
      else
      {
         trans.clear();                    
         cmd = trans.addCustomFunction("CHECKRIGHTS","APPROVAL_ROUTING_API.Check_Ack_Rights","RETURN");
         cmd.addParameter("LINE_NO",itemset5.getValue("LINE_NO"));
         cmd.addParameter("STEP_NO",itemset5.getValue("STEP_NO"));
         cmd.addParameter("PERSON_ID",itemset5.getValue("PERSON_ID"));
         cmd.addParameter("LU_NAME",itemset5.getValue("LU_NAME"));
         cmd.addParameter("KEY_REF",itemset5.getValue("KEY_REF"));
         cmd.addParameter("GROUP_ID",itemset5.getValue("GROUP_ID"));

         ASPCommand cmdCheckActive = trans.addCustomFunction("SECURITYCHECKPOINTACTIVE","Security_SYS.Security_Checkpoint_Activated", "DUMMY3");
         cmdCheckActive.addParameter("DUMMY1", "DOCMAN_APPROVE_STEP");

         trans = mgr.perform(trans);
         String sReturnVal = trans.getValue("CHECKRIGHTS/DATA/RETURN");
         String secutiry_checkpoint_active = trans.getValue("SECURITYCHECKPOINTACTIVE/DATA/DUMMY3");
         trans.clear();

         if ("TRUE".equals(sReturnVal))
         {
            if ("FALSE".equals(itemset5.getRow().getFieldValue("ITEM5_SECURITY_CHECKPOINT_REQ"))|| "FALSE".equalsIgnoreCase(secutiry_checkpoint_active))
            {
               bConfirm = true;
               sMessage = mgr.translate("DOCMAWDOCISSUEQREJSTEP: Reject Step?");
               ctx.writeValue("CONFIRMFUNC","onConfirmRejectStep()");
               ctx.writeValue("UNCONFIRMFUNC","onUnconfirm()");
            }
            else
            {
               onConfirmRejectStep();
            }                       
         }
         else
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCISSUENORIGHTSREJECT: You do not have rights to reject this step"));
            itemset5.unselectRows();
         }
      }
      itemset5.setFilterOff();
   }


   public void  onConfirmRejectStep()
   {
      ASPManager mgr = getASPManager();

      if (itemlay5.isMultirowLayout())
         itemset5.setFilterOn();

      trans.clear();
      cmd = trans.addCustomCommand ("CHECK_STEPS", "APPROVAL_ROUTING_API.Set_Next_App_Step");
      cmd.addParameter("ITEM5_LU_NAME",itemset5.getValue("LU_NAME"));
      cmd.addParameter("ITEM5_KEY_REF",itemset5.getValue("KEY_REF"));
      cmd.addParameter("LINE_NO",itemset5.getValue("LINE_NO"));
      cmd.addParameter("STEP_NO",itemset5.getValue("STEP_NO"));
      cmd.addParameter("APPSTAT_DUMMY","REJ");
      trans = mgr.perform(trans);

      itemset5.setFilterOff();
      itemset5.unselectRows();
      // Bug 58049, Start, Refresh the current row only
      itemset5.refreshRow();
      // Bug 58049, End
      itemset5.setFilterOff();
   }


   public void objectDetails()
   {
      ASPManager mgr = getASPManager();

      if (itemlay2.isMultirowLayout())
      {
         itemset2.storeSelections();
         itemset2.setFilterOn();
      }

      String lu_name = itemset2.getValue("LU_NAME");
      String key_ref = itemset2.getValue("KEY_REF");

      String parameters = mgr.replace(key_ref,"^","&");
      parameters = parameters.substring(0,parameters.length()-1);


      if ("EngPartRevision".equals(lu_name))
         mgr.redirectTo("../pdmcow/EngPartRevisionOvw.page?"+parameters);
      else if ("DocSubject".equals(lu_name))
         mgr.redirectTo("DocSubjectOvw.page?"+parameters);
      else if ("Project".equals(lu_name))
         mgr.redirectTo("../projw/ProjectDetails.page?"+parameters);
      else if ("Activity".equals(lu_name))
         mgr.redirectTo("../projw/Activity.page?"+parameters);
      else if ("ProdStructureHead".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("RecipeStructureHead".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("PlanStructureHead".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("ConfigStructureHead".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("ProdStructAlternate".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("RecipeStructAlternate".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("PlanStructAlternate".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("ConfigStructAlternate".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("ProdStructure".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("RecipeStructure".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("PlanStructure".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("ConfigStructure".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("ManufStructureHead".equals(lu_name))
         mgr.redirectTo("../mfgstw/ManufacturingStructureQuery.page?"+parameters);
      else if ("RoutingHead".equals(lu_name))
         mgr.redirectTo("../mfgstw/RoutingHead.page?"+parameters);
      else if ("RoutingAlternate".equals(lu_name))
         mgr.redirectTo("../mfgstw/RoutingHead.page?"+parameters);
      else if ("RoutingOperation".equals(lu_name))
         mgr.redirectTo("../mfgstw/RoutingHead.page?"+parameters);
      else if ("PaymentPlanAuth".equals(lu_name))
         mgr.redirectTo("../invoiw/AuthorizePaymentPlanOvw.page?"+parameters);
      else if ("Delimitation".equals(lu_name))
         mgr.redirectTo("../equipw/Delimitation.page?"+parameters);
      else if ("EquipmentObjType".equals(lu_name))
         mgr.redirectTo("../equipw/EquipmentObjTypeOvw.page?"+parameters);
      else if ("EquipmentObject".equals(lu_name))
         mgr.redirectTo("../equipw/ObjectOvw.page?"+parameters);
      else if ("TypeDesignation".equals(lu_name))
         mgr.redirectTo("../equipw/TypeDesignationOvw.page?"+parameters);
      else if ("DelimitationOrder".equals(lu_name))
         mgr.redirectTo("../pcmw/DelimitationOrderTab.page?"+parameters);
      else if ("Permit".equals(lu_name))
         mgr.redirectTo("../pcmw/Permit.page?"+parameters);
      else if ("PmAction".equals(lu_name))
         mgr.redirectTo("../pcmw/PmAction.page?"+parameters);
      else if ("StandardJob".equals(lu_name))
         mgr.redirectTo("../pcmw/StandardJobOvw.page?"+parameters);
      else if ("ShopOrd".equals(lu_name))
         mgr.redirectTo("../shporw/ShopOrd.page?"+parameters);
      else if ("ShopMaterialAlloc".equals(lu_name))
         mgr.redirectTo("../shporw/ShopMaterialAlloc.page?"+parameters);
      else if ("ShopOrderProp".equals(lu_name))
         mgr.redirectTo("../shporw/ShopOrderProp.page?"+parameters);
      else if ("PlantArticle".equals(lu_name))
         mgr.redirectTo("../pladew/PlantDesignPartDtlStd.page?"+parameters);
      else if ("PlantCable".equals(lu_name))
         mgr.redirectTo("../pladew/PlantObjectDtlStd.page?"+parameters);
      else if ("PlantChannel".equals(lu_name))
         mgr.redirectTo("../pladew/PlantObjectDtlStd.page?"+parameters);
      else if ("PlantCircuit".equals(lu_name))
         mgr.redirectTo("../pladew/PlantObjectDtlStd.page?"+parameters);
      else if ("PlantConnectionPoint".equals(lu_name))
         mgr.redirectTo("../pladew/PlantObjectDtlStd.page?"+parameters);
      else if ("PlantIoCard".equals(lu_name))
         mgr.redirectTo("../pladew/PlantObjectDtlStd.page?"+parameters);
      else if ("PlantObject".equals(lu_name))
         mgr.redirectTo("../pladew/PlantObjectDtlStd.page?"+parameters);
      else if ("PlantSignal".equals(lu_name))
         mgr.redirectTo("../pladew/PlantObjectDtlStd.page?"+parameters);
      else if ("ActiveRound".equals(lu_name))
         mgr.redirectTo("../pcmw/ActiveRound.page?"+parameters);
      else if ("ActiveSeparate".equals(lu_name))
         mgr.redirectTo("../pcmw/ActiveSeparate2.page?"+parameters);
      else if ("HistoricalRound".equals(lu_name))
         mgr.redirectTo("../pcmw/HistoricalRound.page?"+parameters);
      else if ("HistoricalSeparate".equals(lu_name))
         mgr.redirectTo("../pcmw/HistoricalSeparateRMB.page?"+parameters);
      else if ("CcCase".equals(lu_name))
         mgr.redirectTo("../callcw/CcCaseDetail.page?"+parameters);
      else if ("CcCaseTask".equals(lu_name))
         mgr.redirectTo("../callcw/CcTaskDetail.page?"+parameters);
      else if ("CcCaseSolution".equals(lu_name))
         mgr.redirectTo("../callcw/CcCaseSolution.page?"+parameters);
      //Bug Id 72420, Start
      else if ("ProjectMiscProcurement".equals(lu_name))
         mgr.redirectTo("../projw/ProjectMiscProcurementOvw1.page?"+parameters);
      //Bug Id 72420, End
      //Bug Id 72471, Start
      else if ("DocIssue".equals(lu_name))
	 mgr.redirectTo("DocIssue.page?"+parameters);
      //Bug Id 72471, End
      //Bug Id 89200, start
      else if ("InventoryPart".equals(lu_name)) 
         mgr.redirectTo("../invenw/InventoryPart.page?"+parameters);
      //Bug Id 89200, end
      else
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANTPER: This operation cannot perform on the selected record."));

      itemset2.unselectRows();
      itemset2.setFilterOff();
   }


   protected void setCurrentDocument()
   {
      ASPManager mgr = getASPManager();

      String parent_doc_class = mgr.readValue("PARENT_DOC_CLASS");
      String parent_doc_no = mgr.readValue("PARENT_DOC_NO");
      String parent_doc_sheet = mgr.readValue("PARENT_DOC_SHEET");
      String parent_doc_rev = mgr.readValue("PARENT_DOC_REV");
      String doc_class = mgr.readValue("CURRENT_DOC_CLASS");
      String doc_no = mgr.readValue("CURRENT_DOC_NO");
      String doc_sheet = mgr.readValue("CURRENT_DOC_SHEET");
      String doc_rev = mgr.readValue("CURRENT_DOC_REV");

      // Check if the parent document exits in the current set..
      headset.first();
      if (!parent_doc_class.equals(headset.getValue("DOC_CLASS")) || !parent_doc_no.equals(headset.getValue("DOC_NO")) ||
          !parent_doc_sheet.equals(headset.getValue("DOC_SHEET")) || !parent_doc_rev.equals(headset.getValue("DOC_REV")))
      {
         findDocumentsInStructure(parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev);
      }

      // Now find the matching sub document..
      headset.first();
      do
      {
         if (doc_class.equals(headset.getValue("DOC_CLASS")) && doc_no.equals(headset.getValue("DOC_NO")) &&
             doc_sheet.equals(headset.getValue("DOC_SHEET")) && doc_rev.equals(headset.getValue("DOC_REV")))
         {
            headlay.setLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
            refreshActiveTab();

            // return, with this being
            // the current document..
            return;
         }
      }
      while (headset.next());
   }


   public void setAsDocFileTemp()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
         headset.selectRow();

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true))
         mgr.showAlert("DOCMAWDOCISSUEEDITACCESSREQUIRED: You must have view access to be able to set a document as a file template.");
      else
      {
         trans.clear();

         cmd = trans.addCustomCommand ("DOC_FILE", "EDM_FILE_TEMPLATE_API.Create_File_Template");
         cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", headset.getValue("DOC_REV"));
         cmd.addParameter("LANGUAGE_CODE", headset.getValue("LANGUAGE_CODE"));
         cmd.addParameter("FORMAT_SIZE", headset.getValue("FORMAT_SIZE"));
         trans = mgr.perform(trans);
      }
      headset.setFilterOff();
      headset.unselectRows();
      headset.refreshRow();
   }


   public void transferToDocGroup()
   {
      ASPManager mgr = getASPManager();

      if (itemlay6.isMultirowLayout())
      {
         itemset6.storeSelections();
         itemset6.setFilterOn();
      }

      String sGroupId = itemset6.getValue("GROUP_ID");
      itemset6.setFilterOff();

      //Bug 57781, Start, Changed the redirect page to DocumentBasicTempl.page 
      if (mgr.isEmpty(sGroupId))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANTPERSONINFO: This operation cannot perform on the selected record."));
      else
         mgr.redirectTo("DocumentBasicTempl.page?GROUP_ID="+mgr.URLEncode(sGroupId));  
      //Bug 57781, End
   }


   public void transferToPersonInfo()
   {
      ASPManager mgr = getASPManager();

      if (itemlay6.isMultirowLayout())
      {
         itemset6.storeSelections();
         itemset6.setFilterOn();
      }

      String personId = itemset6.getValue("PERSON_ID");
      itemset6.setFilterOff();

      if (mgr.isEmpty(personId))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUECANTPERSONINFO: This operation cannot perform on the selected record."));
      else
         mgr.redirectTo("../enterw/PersonInfo.page?PERSON_ID="+mgr.URLEncode(personId));
   }


   public void replaceRevision()
   {
      ASPManager mgr = getASPManager();

      if (itemlay3.isMultirowLayout())
         itemset3.goTo(itemset3.getRowSelected());

      // make the current row editable..
      itemlay3.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);

      // since a new revision needs to be specified, the
      // following fileds must be editable..
      mgr.getASPField("SUB_DOC_CLASS").unsetReadOnly();
      mgr.getASPField("SUB_DOC_NO").unsetReadOnly();
      mgr.getASPField("SUB_DOC_SHEET").unsetReadOnly();
      mgr.getASPField("SUB_DOC_REV").unsetReadOnly();

      // modify attributes and value of field REPLACE_REVISION_TITLE
      mgr.getASPField("REPLACE_REVISION_TITLE").unsetHidden();

      itemlay3.setLabelSpan("REPLACE_REVISION_TITLE", 6);

      // modify title to let the user know what he is doing..
      itemblk3.setTitle(mgr.translate("DOCMAWDOCISSUEREPLACEREVISIONBLOCKTITLE: Replace Revision..."));

      // diable unncessary command bar actions..
      itembar3.disableCommand(ASPCommandBar.BACKWARD);
      itembar3.disableCommand(ASPCommandBar.FORWARD);

      // set a context variable to indicate that the current
      // revision has to be replced when saving..
      ctx.writeFlag("REPLACE_REVISION", true);
   }

   public void replaceRevisionFinish()
   {
      // Replace revision..
      ASPManager mgr = getASPManager();
        
      String replace_doc_class = ctx.readValue("REPLACE_DOC_CLASS","");
      String replace_doc_no    = ctx.readValue("REPLACE_DOC_NO","");
      String replace_doc_sheet = ctx.readValue("REPLACE_DOC_SHEET","");
      String replace_doc_rev   = ctx.readValue("REPLACE_DOC_REV","");

      ASPCommand cmd = trans.addCustomCommand("REPLACE_REVISION", "Doc_Structure_API.Replace_Issue_");
      cmd.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      cmd.addParameter("SUB_DOC_CLASS", replace_doc_class);
      cmd.addParameter("SUB_DOC_NO", replace_doc_no);
      cmd.addParameter("SUB_DOC_SHEET", replace_doc_sheet);
      cmd.addParameter("SUB_DOC_REV", replace_doc_rev);
      cmd.addParameter("DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
      cmd.addParameter("DOC_NO", itemset3.getValue("SUB_DOC_NO"));
      cmd.addParameter("DOC_SHEET", itemset3.getValue("SUB_DOC_SHEET"));
      cmd.addParameter("DOC_REV", itemset3.getValue("SUB_DOC_REV"));

      if (bSetStructure) {
          cmd = trans.addCustomCommand("SET_STRUCTURE_TYPE", "Doc_Title_API.Set_Structure_");
          cmd.addParameter("DOC_CLASS", itemset3.getValue("SUB_DOC_CLASS"));
          cmd.addParameter("DOC_NO", itemset3.getValue("SUB_DOC_NO"));
          cmd.addParameter("NNOOFCHILDREN", "1");//Number type
          
      }
      trans = mgr.perform(trans);

      // Refresh the tab to reflect the modifications..
      okFindITEM3();
   }


   public void transferToDocInfoFromConsistOf()
   {
      ASPManager mgr = getASPManager();

      if (itemlay3.isMultirowLayout())
      {
         itemset3.storeSelections();
         itemset3.setFilterOn();
      }
      else
         itemset3.selectRow();

      mgr.redirectTo("DocIssue.page?DOC_CLASS=" + mgr.URLEncode(itemset3.getValue("SUB_DOC_CLASS")) +
                     "&DOC_NO=" + mgr.URLEncode(itemset3.getValue("SUB_DOC_NO")) +
                     "&DOC_SHEET=" + mgr.URLEncode(itemset3.getValue("SUB_DOC_SHEET")) +
                     "&DOC_REV=" + mgr.URLEncode(itemset3.getValue("SUB_DOC_REV")));
   }


   public void  transferToDocInfoFromWhereUsed()
   {
      ASPManager mgr = getASPManager();

      if (itemlay4.isMultirowLayout())
      {
         itemset4.storeSelections();
         itemset4.setFilterOn();
      }
      else
         itemset4.selectRow();

      mgr.redirectTo("DocIssue.page?DOC_CLASS=" + mgr.URLEncode(itemset4.getValue("DOC_CLASS")) +
                     "&DOC_NO=" + mgr.URLEncode(itemset4.getValue("DOC_NO")) +
                     "&DOC_SHEET=" + mgr.URLEncode(itemset4.getValue("DOC_SHEET")) +
                     "&DOC_REV=" + mgr.URLEncode(itemset4.getValue("DOC_REV")));
   }


   public void transferToDocumentSheets()
   {
      ASPManager mgr = getASPManager();

      if (itemlay1.isMultirowLayout())
      {
         itemset1.storeSelections();
         itemset1.setFilterOn();
      }
      else
         itemset1.selectRow();

      mgr.redirectTo("DocIssueSheetOvw.page?DOC_CLASS=" + mgr.URLEncode(itemset1.getValue("DOC_CLASS")) +
                     "&DOC_NO=" + mgr.URLEncode(itemset1.getValue("DOC_NO")) +
                     "&DOC_SHEET=" + mgr.URLEncode(itemset1.getValue("DOC_SHEET")) +
                     "&DOC_REV=" + mgr.URLEncode(itemset1.getValue("DOC_REV")));
   }


   public void distributeDocuments()
   {
      ASPManager mgr = getASPManager();

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSFORDISTRIBUTION: You must have view access to be able to distribute documents.");
         return;
      }


      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      ASPBuffer data_buff = headset.getSelectedRows("DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV");
      ASPBuffer act_buff = mgr.newASPBuffer();
      act_buff.addItem("ACTION", "DOC_DISTRIBUTION");
      act_buff.addItem("DOC_TYPE", "ORIGINAL");
      sUrl = DocumentTransferHandler.getDataTransferUrl(mgr, "DocumentDistributionWizard.page", act_buff, data_buff);
      bOpenDistributionWizardWindow = true; //Bug Id 75677
      headset.setFilterOff();
   }


   //
   //  Methods on Handling Strings
   //

   private int stringIndex(String mainString,String subString)
   {
      int a = mainString.length();
      int index = -1;

      for (int i = 0; i < a; i++)
         if (mainString.startsWith(subString,i))
         {
            index=i;
            break;
         }
      return index;
   }


   private String replaceString(String mainString,String subString,String replaceString)
   {
      String retString = "";
      int posi;
      posi = stringIndex(mainString, subString);

      while (posi!=-1)
      {
         retString+=mainString.substring(0,posi)+replaceString;
         mainString=mainString.substring(posi+subString.length(),mainString.length());
         posi = stringIndex(mainString, subString);
      }
      return retString+mainString;
   }


   public int howManyOccurance(String str,char c)
   {
      int strLength = str.length();
      int occurance = 0;
      for (int index = 0;index<strLength;index++)
         if (str.charAt(index)==c)
            occurance++;
      return occurance;
   }


   protected String[] split(String str,char c)
   {
      int length_ = howManyOccurance(str,c);
      int strLength = str.length();
      int occurance = 0;
      int index = 0;
      String[] tempString = new String[length_+1];

      while (strLength>0)
      {
         occurance = str.indexOf(c);
         if (occurance == -1)
         {
            tempString[index]=str;
            break;
         }
         else
         {
            tempString[index++]=str.substring(0,occurance);
            str=str.substring(occurance+1,strLength);
            strLength=str.length();
         }
      }
      return tempString;
   }

   //Mark the selected rows with the status
   private void markSelection(String status)
   {
      int selectedrows = headset.countRows();
      headset.first();
      do
      {
         headset.markRow(status);
      }while ( headset.next() );

   }


   public void activateTitle()
   {
      tabs.setActiveTab(1);
      okFindITEM0();
   }


   public void activateOriginals()
   {
      tabs.setActiveTab(2);
      okFindITEM1();
   }


   public void activateObjects()
   {
      tabs.setActiveTab(3);
      okFindITEM2();
   }


   public void activateConsistsOf()
   {
      tabs.setActiveTab(4);
      okFindITEM3();
   }


   public void activateWhereUsed()
   {
      tabs.setActiveTab(5);
      okFindITEM4();
   }


   public void activateRouting()
   {
      tabs.setActiveTab(6);
      okFindITEM5();
   }


   public void activateAccess()
   {
      tabs.setActiveTab(7);
      okFindITEM6();
   }


   public void activateHistory()
   {
      tabs.setActiveTab(8);
      checkEnableHistoryNewRow();
      okFindITEM7();
   }

   public void activateFileReferences()
   {
      tabs.setActiveTab(9);
      okFindITEM9();
   }


   public void activateTransmittals()
   {
      tabs.setActiveTab(10);
      okFindITEM10();
   }


   public void editTitle()
   {
      ASPManager mgr = getASPManager();

      itemset0.goTo(itemset0.getRowSelected());

      if (itemset0.getValue("ISSUEEDITACCESS").equals("N"))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUETITLENOEDIT: You do not have sufficient priviledges to edit this title."));
      else if (itemset0.getValue("ISSUEEDITACCESS").equals("R"))
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUETITLENOEDITRELISSUES: Cannot edit this title because there are Released or Approved issues connected to it."));
      else
         itemlay0.setLayoutMode(itemlay0.EDIT_LAYOUT);
   }
   
   public void connectObject()
   {
      headset.storeSelections();
      //excute the same action as creating a new object in objects tab.
      newRowITEM2();
   }


   public void preDefine() throws FndException
   {

      String sDocDis, sCreateNewRev, sSetToObs;

      ASPManager mgr = getASPManager();
      disableConfiguration();

      headblk = mgr.newASPBlock("HEAD");
      //Bug Id 72471, enabled docman for DocIssue. Removed the disableDocMan() command

      if (mgr.isExplorer())
      {
         sDocDis = mgr.translate("DOCMAWDOCISSUEDOCDIS: Document Distribution...");
         sCreateNewRev = mgr.translate("DOCMAWDOCISSUECNEWREVISION: Create New Revision...");
         sSetToObs = mgr.translate("DOCMAWDOCISSUESETDOCTOOBS: Set Document to Obsolete...");
      }
      else
      {
         sDocDis = mgr.translate("DOCMAWDOCISSUEDIS: Distribute...");
         sCreateNewRev = mgr.translate("DOCMAWDOCISSUECNEWREV: Create new rev...");
         sSetToObs = mgr.translate("DOCMAWDOCISSUESETTOOBS: Set to Obsolete...");
      }


      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("OBJSTATE").
      setHidden();

      headblk.addField("OBJEVENTS").
      setHidden();

      headblk.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setReadOnly().
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUEHEADDOCCLASS: Doc Class");

      headblk.addField("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setReadOnly().
      setUpperCase().
      setDynamicLOV("DOC_TITLE","DOC_CLASS").
      setLabel("DOCMAWDOCISSUEDOCNO: Doc No");

      headblk.addField("DOC_SHEET").
      setSize(20).
      setMaxLength(10).
      setReadOnly().
      setUpperCase().
      setDynamicLOV("DOC_ISSUE_LOV1").
      setLabel("DOCMAWDOCISSUEDOCSHEET: Doc Sheet");

      headblk.addField("DOC_REV").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setUpperCase().
      setDynamicLOV("DOC_ISSUE").
      setLabel("DOCMAWDOCISSUEDOCREV: Doc Rev");

      headblk.addField("DOCTITLE").
      setDbName("TITLE").
      setSize(60).
      setMaxLength(250).
      setReadOnly().
      setBold().
      setLabel("DOCMAWDOCISSUEDOCTITLE: Title");
      
      headblk.addField("PROJ_NO").
      setInsertable().
      setMandatory().
      setLabel("DOCISSUEPROJNO: Proj No").
      setDynamicLOV("GENERAL_PROJECT").
      setSize(20);    
      
      headblk.addField("PROJ_NAME").
      setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC(:PROJ_NO)").
      setLabel("DOCISSUEPROJNAME: Proj Name").
      setReadOnly().
      setSize(20); 
      mgr.getASPField("PROJ_NO").setValidation("PROJ_NAME");
      headblk.addField("MAINBLK_STRUCTURE").
      setCheckBox("FALSE,TRUE").
      setReadOnly().
      setDbName("STRUCTURE").
      setLabel("DOCMAWDOCISSUEMAINBLKSTRUC: Structure");

      headblk.addField("STATE").
      setSize(20).
      setMaxLength(253).
      setSelectBox().
      setReadOnly().
      enumerateValues("DOC_STATE_API").
      unsetSearchOnDbColumn().
      setLabel("DOCMAWDOCISSUESTATE: Status");

      headblk.addField("SEDMSTATUS").
      setSize(20).
      setMaxLength(253).
      setSelectBox().
      enumerateValues("EDM_FILE_API.Enumerate_States__","EDM_FILE_API.Finite_State_Encode__").
      unsetSearchOnDbColumn().setReadOnly().
      setFunction("EDM_FILE_API.GET_DOC_STATE_NO_USER(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV, 'ORIGINAL')").
      setLabel("DOCMAWDOCISSUESEDMSTATUS: File Status");
      mgr.getASPField("DOC_REV").setValidation("SEDMSTATUS");

      //bug id 77080 Start
      headblk.addField("EDM_DB_STATE").
      setHidden();
      //bug id 77080 End

      headblk.addField("CHECKED_OUT_USR").
      setReadOnly().
      setUpperCase().
      setDynamicLOV("PERSON_INFO_USER").
      setLabel("DOCMAWDOCISSUECHECKEDOUTUSER: Checked Out By").
      setFunction("EDM_FILE_API.GET_CHECKED_OUT_USER(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV, 'ORIGINAL')");

      headblk.addField("STRUCTURE_TYPE").
      setCheckBox("0,1").
      setFunction("Doc_Title_API.Get_Structure_(:DOC_CLASS,:DOC_NO)").
      setHidden();  
      //setLabel("DOCISSUESTRUCTURE: Structure");

      headblk.addField("PARENT_STRUCTURE_TYPE").
      setHidden().
      setFunction("Doc_Structure_Util_API.Check_Any_Parent_Struc_(:DOC_CLASS,:DOC_NO, '1')");

      headblk.addField("FILE_TYPE").
      setSize(20).
      setReadOnly().
      setLOV("FileTypeLov.page").
      setLabel("DOCMAWDOCISSUEFILETYPE: Original File Type");

      headblk.addField("ACCESS_CONTROL").
      setSize(20).
      setMaxLength(20).
      setSelectBox().
      setReadOnly().
      enumerateValues("Doc_User_Access_Api").
      setLabel("DOCMAWDOCISSUEACCESSCONTROL: Access");

      headblk.addField("DT_RELEASED", "Date").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEDTRELEASED: Date Released");

      headblk.addField("REV_NO", "Number").
      setSize(20).
      setAlignment("RIGHT").
      // Bug Id 90011, start
      //setHidden().
      setReadOnly().
      // Bug Id 90011, end
      setLabel("DOCMAWDOCISSUEOVWREVNO: Rev No");

      headblk.addField("ALTDOCNO").
      setDbName("ALTERNATE_DOCUMENT_NUMBER").
      setSize(20).
      setMaxLength(120).
      setReadOnly().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEALTERNATEDOCNO: Alt Doc No");

      headblk.addField("DESCRIPTION1").
      setSize(20).
      setMaxLength(100).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDESCRIPTION1: Description 1");

      headblk.addField("DESCRIPTION2").
      setSize(20).
      setMaxLength(100).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDESCRIPTION2: Description 2");

      headblk.addField("DESCRIPTION3").
      setSize(20).
      setMaxLength(100).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDESCRIPTION3: Description 3");

      headblk.addField("DESCRIPTION4").
      setSize(20).
      setMaxLength(100).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDESCRIPTION4: Description 4");

      headblk.addField("DESCRIPTION5").
      setSize(20).
      setMaxLength(100).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDESCRIPTION5: Description 5");

      headblk.addField("DESCRIPTION6").
      setSize(20).
      setMaxLength(100).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDESCRIPTION6: Description 6");

      headblk.addField("NEXT_DOC_SHEET").
      setSize(20).
      setMaxLength(10).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUENEXTSHEET: Next Doc Sheet");

      headblk.addField("NO_OF_SHEETS", "Number").
      setSize(20).
      setMaxLength(5).
      setReadOnly().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUENOOFSHEETS: Total Sheets");

      headblk.addField("SHEET_ORDER").
      setSize(20).
      setMaxLength(10).
      setDefaultNotVisible().
      setLabel("SHEETORDER: Sheet Order");

      headblk.addField("VALIDATE_FILE_ACTION").
      setFunction("DOC_BRIEFCASE_ISSUE_API.IS_DOCUMENT_FREE_FROM_BC(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
      setHidden();

      headblk.addField("LU_NAME").
      setHidden();

      headblk.addField("KEY_REF").
      setHidden();

      headblk.addField("FORM_NAME").
      setHidden().
      setFunction("'Document Management'");

      // Dynamic LOV has been set to DOC_BC_LOV so that it gets scanned in the PO Script.
      headblk.addField("BRIEFCASE_NO"). 
      setReadOnly().
      setDynamicLOV("DOC_BC_LOV"). 
      setFunction("DOC_BRIEFCASE_ISSUE_API.GET_BRIEFCASE_NO(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
      setUpperCase().
      setSecureHyperlink("DocBriefcase.page", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV").//Bug Id 75490
      setLabel("DOCMAWDOCISSUEBCNO: Briefcase No");

      //
      // Fields in the 'General' group box
      //

      headblk.addField("TECHOBJ", "Number").
      setDefaultNotVisible().
      setCheckBox("0,1").
      setReadOnly().
      setFunction("SIGN(TECHNICAL_OBJECT_REFERENCE_API.Get_Technical_Spec_No(:LU_NAME,:KEY_REF)+1)").
      setHyperlink("../appsrw/TechnicalObjectReference.page", "LU_NAME, KEY_REF, TECHOBJ, FORM_NAME", "NEWWIN").
      setLabel("DOCMAWDOCISSUETECHOBJECTS: Characteristics");

      headblk.addField("DOC_REV_TEXT").
      setSize(20).
      setMaxLength(2000).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDOCREVTEXT: Revision Text");

      headblk.addField("DT_DOC_REV", "Date").
      setSize(20).
      setReadOnly().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDTDOCREV: Revision Date");

      headblk.addField("ACCESS_CONTROL_DB").
      setHidden().
      setFunction("DOC_USER_ACCESS_API.Encode(:ACCESS_CONTROL)");

      headblk.addField("FILE_STATUS_DES").
      setHidden().
      setFunction("''");

      headblk.addField("DT_OBSOLETE","Date").
      setSize(20).
      setReadOnly().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDTOBSOLETE: Date Obsolete");

      headblk.addField("INFO").
      setSize(20).
      setMaxLength(2000).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEINFO: Note");

      headblk.addField("LANGUAGE_CODE").
      setSize(20).
      setMaxLength(2).
      setDefaultNotVisible().
      setDynamicLOV("APPLICATION_LANGUAGE").
      setLabel("DOCMAWDOCISSUELANGUAGECODE: Language");

      headblk.addField("SLANGUAGEDESC").
      setSize(20).
      setMaxLength(2000).
      setDefaultNotVisible().
      setReadOnly().
      setFunction("APPLICATION_LANGUAGE_API.GET_DESCRIPTION(:LANGUAGE_CODE)").
      setLabel("DOCMAWDOCISSUESLANGUAGEDESC: Language Desc");
      mgr.getASPField("LANGUAGE_CODE").setValidation("SLANGUAGEDESC");

      //Bug Id 70553, Start
      headblk.addField("FORMAT_SIZE").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOC_FORMAT").
      setLabel("DOCMAWDOCISSUEFORMATSIZE: Format");
      //Bug Id 70553, End

      headblk.addField("SFORMATSIZEDESC").
      setSize(20).
      setMaxLength(2000).
      setDefaultNotVisible().
      setReadOnly().
      setFunction("DOC_FORMAT_API.GET_DESCRIPTION(:FORMAT_SIZE)").
      setLabel("DOCMAWDOCISSUESFORMATSIZEDESC:  Format Desc");
      mgr.getASPField("FORMAT_SIZE").setValidation("SFORMATSIZEDESC");

      headblk.addField("DOC_RESP_DEPT").
      setSize(20).
      setMaxLength(4).
      setUpperCase().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDOCRESPDEPT: Responsible Department");

      headblk.addField("DOC_RESP_SIGN").
      setSize(20).
      setMaxLength(30).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDOCRESPSIGN: Person");

      //Bug Id 70553, Start
      headblk.addField("REASON_FOR_ISSUE").
      setSize(20).
      setUpperCase().
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("DOCUMENT_REASON_FOR_ISSUE").
      setLabel("DOCMAWDOCISSUEREASONFORISSUE: Reason For Issue");
      //Bug Id 70553, End

      headblk.addField("REASON_DESC").
      setSize(20).
      setDefaultNotVisible().
      setFunction("DOCUMENT_REASON_FOR_ISSUE_API.Get_Description(:REASON_FOR_ISSUE)").
      setReadOnly().
      setLabel("DOCMAWDOCISSUEREASONFORISSUEDESC: Reason For Issue Desc");
      mgr.getASPField("REASON_FOR_ISSUE").setValidation("REASON_DESC");

      headblk.addField("APPROVAL_UPDATE").
      setCheckBox("FALSE,TRUE").
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEAPPROVALUPDATE: Update allowed during approval");

      headblk.addField("FILE_TEMPLATE").
      setCheckBox("FALSE,TRUE").
      setFunction("EDM_FILE_TEMPLATE_API.Check_For_Doc_File_Template(:DOC_CLASS,:DOC_NO,:DOC_REV,:DOC_SHEET)").
      setLabel("DOCMAWDOCISSUEFILETEMPLATE: Used as File Template");

      headblk.addField("SCALE").
      setDefaultNotVisible().
      setDynamicLOV("DOC_SCALE_LOV1").
      setLabel("DOCMAWDOCISSUESCALE: Scale");

      headblk.addField("ENABLE_ADDTO_BC").
      setHidden().
      setFunction("Doc_Issue_API.Check_Enable_Add_To_Bc(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");

      //
      // Fields in the 'Development' group box
      //

      headblk.addField("DT_PLANNED_START","Date").
      setSize(20).
      setLabel("DOCMAWDOCISSUEDTPLANNEDSTART: Planned Start");

      headblk.addField("DT_PLANNED_FINISH","Date").
      setSize(20).
      setLabel("DOCMAWDOCISSUEDTPLANNEDFINISH: Planned Finish");

      headblk.addField("DT_ACTUAL_FINISH","Date").
      setSize(20).
      setLabel("DOCMAWDOCISSUEDTACTUALFINISH: Actual Finish");

      headblk.addField("DOC_STATUS","Number","0.00##%").
      setSize(20).
      setMaxLength(5).
      setDefaultNotVisible().
      setCustomValidation("DOC_STATUS","DOC_STATUS").
      setLabel("DOCMAWDOCISSUEDOCSTATUS: Progress");

      headblk.addField("USER_CREATED").
      setSize(20).
      setMaxLength(30).
      setReadOnly().
      setDefaultNotVisible().
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
      setLabel("DOCMAWDOCISSUEUSERCREATED: Created By");

      headblk.addField("DT_CRE","Date").
      setSize(20).
      setReadOnly().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDTCRE: Date Created");

      headblk.addField("USER_SIGN").
      setSize(20).
      setMaxLength(30).
      setReadOnly().
      setDefaultNotVisible().
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
      setLabel("DOCMAWDOCISSUEUSERSIGN: Modified By");

      headblk.addField("DT_CHG","Date").
      setSize(20).
      setDefaultNotVisible().
      setReadOnly().
      setLabel("DOCMAWDOCISSUEDTCHG: Date Modified");

      headblk.addField("DAYS_EXPIRED","Number").
      setSize(20).
      setMaxLength(6).
      setDefaultNotVisible().
      setCustomValidation("DT_OBSOLETE,DAYS_EXPIRED","NEXPIRATIONDUEDAYS").
      setLabel("DOCMAWDOCISSUEDAYSEXPIRED: Expiration Days");

      headblk.addField("NEXPIRATIONDUEDAYS").
      setSize(20).
      setMaxLength(6).
      setDefaultNotVisible().
      setReadOnly().
      setFunction("DOC_ISSUE_API.GET_EXPIRATION_DUE_DAYS(:DT_OBSOLETE,:DAYS_EXPIRED)").
      setLabel("DOCMAWDOCISSUENEXPIRATIONDUEDAYS: Remaining Days");

      /*headblk.addField("CHECKED_IN_DATE","Date").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUECHECKEDINDATE: Last Checked In");

      headblk.addField("CHECKED_IN_SIGN").
      setSize(20).
      setReadOnly().
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
      setLabel("DOCMAWDOCISSUECHECKEDINSIGN: Last Checked In By");

      headblk.addField("CHECKED_OUT_DATE","Date").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUECHECKEDOUTDATE: Last Checked Out");

      headblk.addField("CHECKED_OUT_SIGN").
      setSize(20).
      setReadOnly().
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
      setLabel("DOCMAWDOCISSUECHECKEDOUTSIGN: Last Checked Out By");*/

      headblk.addField("REDLINE_STATUS").
      setSize(20).
      setDefaultNotVisible().
      setReadOnly().
      setSelectBox().
      enumerateValues("Doc_File_State_Api").
      unsetSearchOnDbColumn().
      setFunction("EDM_FILE_API.Get_Document_State(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'REDLINE')").
      setValidation("REDLINE_STATUS").
      setLabel("DOCMAWDOCISSUEREDLINESTATUS: Comment File Status");

      headblk.addField("REDLINE_STATUS_DES").
      setSize(20).
      setDefaultNotVisible().
      setHidden().
      setUpperCase().
      setFunction("''");

      headblk.addField("EDM_FILE_TYPE").
      setHidden().
      setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')");

      headblk.addField("GETEDITACCESS").
      setHidden().
      setFunction("DOC_ISSUE_API.Get_Edit_Access_(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");

      headblk.addField("CAN_ADD_TO_BC").
      setHidden().
      setFunction("DOC_ISSUE_API.can_add_to_bc(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");

      headblk.addField("GETVIEWACCES").
      setHidden().
      setFunction("DOC_ISSUE_API.Get_View_Access_(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");

      headblk.addField("CREDOCFILE").
      setHidden().
      setFunction("EDM_FILE_API.GET_DOCUMENT_STATE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV, 'ORIGINAL') || :STATE");

      headblk.addField("PROFILE_ID").
      setSize(20).
      setMaxLength(10).
      setUpperCase().
      setDefaultNotVisible().
      setDynamicLOV("APPROVAL_PROFILE").
      setLabel("DOCMAWDOCISSUEPROFILEID: Approval Template");

      headblk.addField("SACCESSCODE").
      setHidden().
      setFunction("DOC_USER_ACCESS_API.ENCODE(:ACCESS_CONTROL)");
      mgr.getASPField("ACCESS_CONTROL").setValidation("SACCESSCODE");

      headblk.addField("SCHANGEACCESSCODE").
      setHidden().
      setFunction("DOC_ISSUE_API.CHECK_SET_ACCESS_CONTROL__(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");
      mgr.getASPField("DOC_REV").setValidation("SCHANGEACCESSCODE");

      headblk.addField("EDM_STATUS").
      setHidden();

      headblk.addField("REDLINE_FILE_STATUS").
      setHidden().
      setFunction("EDM_FILE_API.GET_CHECK_OUT2(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'REDLINE')");

      //Bug Id 80759, start
      headblk.addField("USED_IN_PACKAGE").
      setHidden().
      setFunction("DOC_PACKAGE_TEMPLATE_API.Get_Package_No(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");
      //Bug Id 80759, end

      headblk.setView("DOC_ISSUE_REFERENCE");
      headblk.defineCommand("DOC_ISSUE_API", "New__, Modify__, Remove__, PROMOTE_TO_APP_IN_PROGRESS__, PROMOTE_TO_APPROVED__, PROMOTE_TO_RELEASED__, PROMOTE_TO_OBSOLETE__, DEMOTE_TO_PRELIMINARY__,SET_DOC_REV_TO_APPROVED__");

      headblk.enableFuncFieldsNonSelect();
      
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.defineCommand(headbar.CANCELFIND,"cancelFind");
      headbar.defineCommand(headbar.SAVERETURN,"saveReturnITEM");
      headbar.defineCommand(headbar.DELETE,"deleteRow");

      headbar.addSecureCustomCommand("edit", "DOCMAWDOCISSUEMODYFYFOREDIT: Edit","DOC_ISSUE_API.Modify__"); //Bug Id 70286
      headbar.addCommandValidConditions("edit", "GETEDITACCESS", "Enable", "TRUE");


      // State Changing Operations
      headbar.addSecureCustomCommand("startApproval", mgr.translate("DOCMAWDOCISSUESTARTAPPROVE: Start Approval"),"DOC_ISSUE_API.PROMOTE_TO_APP_IN_PROGRESS__"); //Bug Id 70286
      headbar.addSecureCustomCommand("cancelApproval", mgr.translate("DOCMAWDOCISSUECANCELAPP: Cancel Approval"),"DOC_ISSUE_API.DEMOTE_TO_PRELIMINARY__"); //Bug Id 70286

      headbar.addSecureCustomCommand("setApproved", mgr.translate("DOCMAWDOCISSUEAPPDOC: Approve Document"),"DOC_ISSUE_API.PROMOTE_TO_APPROVED__");
      headbar.defineCommand("setApproved", "setApproved", "validateSetApproval");


      headbar.addSecureCustomCommand("releaseDocument", mgr.translate("DOCMAWDOCISSUERELDOC: Release Document"),"DOC_ISSUE_API.PROMOTE_TO_RELEASED__"); //Bug Id 70286
      headbar.addSecureCustomCommand("setObsolete", sSetToObs,"DOC_ISSUE_API.PROMOTE_TO_OBSOLETE__"); //Bug Id 70286
      headbar.addCustomCommandSeparator();

      headbar.addCustomCommandGroup("STATUS", mgr.translate("DOCMAWDOCISSUESTATUS: Status"));
      headbar.setCustomCommandGroup("startApproval", "STATUS");
      headbar.setCustomCommandGroup("cancelApproval", "STATUS");
      headbar.setCustomCommandGroup("setApproved", "STATUS");
      headbar.setCustomCommandGroup("releaseDocument", "STATUS");
      headbar.setCustomCommandGroup("setObsolete", "STATUS");

      // Document Access
      headbar.addSecureCustomCommand("setUserAccess",mgr.translate("DOCMAWDOCISSUEUSERACC: Set User Access"),"DOC_ISSUE_API.Revoke_Access_Control__"); //Bug Id 70286
      headbar.addSecureCustomCommand("setGroupAccess",mgr.translate("DOCMAWDOCISSUEAPPGRACC: Set Group Access"),"DOC_ISSUE_API.Set_Group_Access__"); //Bug Id 70286
      headbar.addSecureCustomCommand("setAllAccess",mgr.translate("DOCMAWDOCISSUEALLACC: Set All Access"),"DOC_ISSUE_API.Set_Access_Control__"); //Bug Id 70286
      headbar.addCustomCommandSeparator();

      headbar.addCustomCommandGroup("ACCESS",mgr.translate("DOCMAWDOCISSUEDOCUMENTACCESS: Document Access"));
      headbar.setCustomCommandGroup("setUserAccess","ACCESS");
      headbar.setCustomCommandGroup("setGroupAccess","ACCESS");
      headbar.setCustomCommandGroup("setAllAccess","ACCESS");

      // File Operations
      headbar.addSecureCustomCommand("checkInDocument",mgr.translate("DOCMAWDOCISSUECHECKINDOC: Check In Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("editDocument",mgr.translate("DOCMAWDOCISSUEEDITDOC: Edit Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("checkInSelectDocument",mgr.translate("DOCMAWDOCISSUECHECKINSELECTEDDOC: Check In Selected Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("undoCheckOut",mgr.translate("DOCMAWDOCISSUEUNDOCHECKOUT: Undo Check Out Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("viewOriginal",mgr.translate("DOCMAWDOCISSUEVIEVOR: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("viewOriginalWithExternalViewer",mgr.translate("DOCMAWDOCISSUEVIEVOREXTVIEWER: View Document with Ext. App."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("viewCopy",mgr.translate("DOCMAWDOCISSUEVIEWCO: View Copy"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("printDocument",mgr.translate("DOCMAWDOCISSUEPRINTDOC: Print Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      // Bug ID 56685, Start
      headbar.addSecureCustomCommand("printViewCopy",mgr.translate("DOCMAWDOCISSUEPRINTVIEWCOPY: Print View Copy"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286 
      // Bug ID 56685, End
      headbar.addSecureCustomCommand("deleteDocument",mgr.translate("DOCMAWDOCISSUEDELETEDOC: Delete Document Revision"),"DOC_ISSUE_API.Remove__"); //Bug Id 70286
      headbar.addSecureCustomCommand("deleteDocumentFile",mgr.translate("DOCMAWDOCISSUEDELETEDOCFILE: Delete Document File"),"EDM_FILE_API.Remove__"); //Bug Id 70286
      
      headbar.addSecureCustomCommand("deleteSelectDocFile",mgr.translate("DOCMAWDOCISSUEDELETESELECTDOCFILE: Delete Selected Document File"), "EDM_FILE_API.Remove__");
      
      headbar.addSecureCustomCommand("downloadDocuments",mgr.translate("DOCMAWDOCISSUEDOWNLOADDOC: Download Documents"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");
      
      headbar.addCustomCommandSeparator();
      headbar.addSecureCustomCommand("setAsDocFileTemp",mgr.translate("DOCMAWDOCISSUESETASDOCFILETEMP: Set as Doc File Template"),"EDM_FILE_TEMPLATE_API.Create_File_Template"); //Bug Id 70286
      headbar.addCustomCommandSeparator();
      headbar.addSecureCustomCommand("setToCopyFile",mgr.translate("DOCMAWDOCISSUECOPYFILETO: Copy File To..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addCustomCommand("userSettings",mgr.translate("DOCMAWDOCISSUEUSERSETTINGS: User Settings..."));
      headbar.addCustomCommandSeparator();
      // Bug ID 77080, Start
      headbar.addCustomCommand("resetStatus",mgr.translate("DOCMAWDOCISSUERESETSTATUS: Reset File Status"));
      // Bug ID 77080, End


      headbar.addCustomCommandGroup("FILE",mgr.translate("DOCMAWDOCISSUEFILE: File Operations"));
      
      headbar.setCustomCommandGroup("viewOriginal","FILE");
      headbar.setCustomCommandGroup("viewOriginalWithExternalViewer","FILE");
      headbar.setCustomCommandGroup("viewCopy","FILE");
      headbar.setCustomCommandGroup("editDocument","FILE");
      headbar.setCustomCommandGroup("checkInSelectDocument", "FILE");
      headbar.setCustomCommandGroup("printDocument","FILE");
      // Bug ID 56685, Start
      headbar.setCustomCommandGroup("printViewCopy","FILE");   
      // Bug ID 56685, End
      headbar.setCustomCommandGroup("checkInDocument","FILE");
      headbar.setCustomCommandGroup("undoCheckOut","FILE");
      headbar.setCustomCommandGroup("userSettings","FILE");
      headbar.setCustomCommandGroup("setAsDocFileTemp","FILE");
      headbar.setCustomCommandGroup("setToCopyFile","FILE");
      headbar.setCustomCommandGroup("deleteDocument","FILE");
      headbar.setCustomCommandGroup("deleteDocumentFile","FILE");
      headbar.setCustomCommandGroup("deleteSelectDocFile", "FILE");
      headbar.setCustomCommandGroup("downloadDocuments", "FILE");
      // Bug ID 77080, Start
      headbar.setCustomCommandGroup("resetStatus","FILE");
      // Bug ID 77080, End


      // Comment File Operations
      headbar.addSecureCustomCommand("viewComment",mgr.translate("DOCMAWDOCISSUECOMMENTVIEW: View"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("editComment",mgr.translate("DOCMAWDOCISSUECOMMENTEDIT: Edit"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286 
      headbar.addSecureCustomCommand("checkInComment",mgr.translate("DOCMAWDOCISSUECOMMENTCHECKIN: Check In"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286 
      headbar.addSecureCustomCommand("deleteComment",mgr.translate("DOCMAWDOCISSUECOMMENTDELETE: Delete"),"EDM_FILE_API.Remove__"); //Bug Id 70286 

      headbar.addCustomCommandGroup("COMMENT",mgr.translate("DOCMAWDOCISSUECOMMENT: Comment"));
      headbar.setCustomCommandGroup("viewComment","COMMENT");
      headbar.setCustomCommandGroup("editComment","COMMENT");
      headbar.setCustomCommandGroup("checkInComment","COMMENT");
      headbar.setCustomCommandGroup("deleteComment","COMMENT");
      headbar.addCustomCommandSeparator();

      // Bug Id 54528, Start , Created new sub menu "Templates" , Added commands.

      // Template Operations   
      headbar.addSecureCustomCommand("copyApprovalTemplate",mgr.translate("DOCMAWDOCISSUECPYAPPPRO: Copy Approval Template"),"APPROVAL_ROUTING_API.Copy_App_Profile"); //Bug Id 70286
      headbar.addSecureCustomCommand("copyAccessMsg",mgr.translate("DOCMAWDOCISSUEACCETEMP: Copy Access Template"),"Document_Issue_Access_API.Copy_Access_Template__"); //Bug Id 70286

      headbar.addCustomCommandGroup("TEMPLATE",mgr.translate("DOCMAWDOCISSUETEMPLATES: Templates"));
      headbar.setCustomCommandGroup("copyApprovalTemplate","TEMPLATE");
      headbar.setCustomCommandGroup("copyAccessMsg","TEMPLATE");
      headbar.addCustomCommandSeparator();

      headbar.addSecureCustomCommand("connectObject","DOCMAWDOCISSUEADDTOBCCONNETOBJECT: Connect Object","DOC_REFERENCE_OBJECT_API.New__"); //Bug Id 70286
      headbar.addCustomCommandSeparator();

      //Bug Id 54528, End

      // Document Structure
      headbar.addCustomCommand("showStructureInNavigator", mgr.translate("DOCMAWDOCISSUESHOWSTRUCTUREINNAVIGATOR: Show In Navigator"));
      headbar.addSecureCustomCommand("setStructAttribAll",mgr.translate("DOCMAWDOCISSUESTRUCATTRSET: Set Structure Attribute"),"DOC_TITLE_API.Set_Structure_All_");//Bug Id 70286
      headbar.addSecureCustomCommand("unsetStructAttribAll",mgr.translate("DOCMAWDOCISSUESTRUCATTRUNSET: Unset Structure Attribute"),"DOC_TITLE_API.Unset_Structure_All_"); //Bug Id 70286
      headbar.addCustomCommandGroup("STRUCTURE",mgr.translate("DOCMAWDOCISSUEDOCUMENTSTRUCTURE: Document Structure"));
      headbar.setCustomCommandGroup("showStructureInNavigator", "STRUCTURE");
      headbar.setCustomCommandGroup("setStructAttribAll", "STRUCTURE");
      headbar.setCustomCommandGroup("unsetStructAttribAll", "STRUCTURE");
      headbar.addCustomCommandSeparator();
      //DMPR303 start
      headbar.addSecureCustomCommand("createLink", mgr.translate("DOCMAWDOCISSUECREATLINK: Create Document Link..."),"DOC_ISSUE_API.LATEST_REVISION"); //Bug Id 70286
      headbar.addCustomCommandSeparator();
      //DMPR303 End.
      headbar.addSecureCustomCommand("sendToMailRecipient",mgr.translate("DOCMAWDOCISSUEOVWSENDMAIL: Send by E-mail..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("createNewRevision", sCreateNewRev,"DOC_ISSUE_API.NEW_REVISION2__"); //Bug Id 70286
      headbar.addSecureCustomCommand("createNewSheet", mgr.translate("DOCMAWDOCISSUECREATENEWSHEET: Create New Sheet..."),"DOC_ISSUE_API.CREATE_NEW_SHEET__"); //Bug Id 70286
      headbar.addSecureCustomCommand("distributeDocuments", sDocDis,"DOC_DIST_ENGINE_API.Execute_Distribution"); //Bug Id 70286
      headbar.addCustomCommand("goToBriefcase", "DOCMAWDOCISSUEGOTOBC: Go to Briefcase...");
      headbar.addCommandValidConditions("goToBriefcase","BRIEFCASE_NO","Disable",null);
      headbar.addSecureCustomCommand("addToBriefcase","DOCMAWDOCISSUEADDTOBC: Add to Briefcase...","DOC_BRIEFCASE_ISSUE_API.Add_To_Briefcase"); //Bug Id 70286
      headbar.addSecureCustomCommand("connectToTrans","DOCMAWDOCISSUECONNTRANS: Connect to Transmittal...","DOC_TRANSMITTAL_ISSUE_API.New__"); //Bug Id 70286
      headbar.addSecureCustomCommand("mandatorySettings","DOCMAWDOCISSUEMADATORY: Configure Mandatory Search Fields...","DOCMAN_DEFAULT_API.Set_Default_Value_");//Bug Id 67105 //Bug Id 70286
      headbar.addSecureCustomCommand("transmittalWizard","DOCMAWDOCISSUETRNWIZARD: Transmittal Wizard...","DOCUMENT_TRANSMITTAL_API.Handle_Transmittal_Wizard_Save"); //Bug Id 81806
      
      headbar.enableMultirowAction();
      //w.a following commands are not supportive for multi row actions
      headbar.removeFromMultirowAction("setAsDocFileTemp");
      headbar.removeFromMultirowAction("viewOriginalWithExternalViewer");
      headbar.removeFromMultirowAction("undoCheckOut");

      headbar.removeFromMultirowAction("viewComment");
      headbar.removeFromMultirowAction("editComment");
      headbar.removeFromMultirowAction("checkInComment");
      headbar.removeFromMultirowAction("deleteComment");

      headbar.removeFromMultirowAction("sendToMailRecipient");
      headbar.removeFromMultirowAction("createNewRevision");
      headbar.removeFromMultirowAction("createNewSheet");
      headbar.removeFromMultirowAction("goToBriefcase");
      headbar.removeFromMultirowAction("edit");
      headbar.removeFromMultirowAction("mandatorySettings");//Bug Id 67105
      
      // Added by Terry 20121022
      headbar.removeFromMultirowAction("checkInSelectDocument");
      headbar.removeFromMultirowAction("deleteSelectDocFile");
      // Added end

      headtbl = mgr.newASPTable(headblk);
      headtbl.enableRowSelect();
      headtbl.setTitle(mgr.translate("DOCMAWDOCISSUEDOCISSUE: Doc Issue"));
      headtbl.enableTitleNoWrap();

      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setSimple("PROJ_NAME");
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headlay.defineGroup("Main", "OBJID,OBJVERSION,OBJSTATE,OBJEVENTS,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,STATE,SEDMSTATUS,REV_NO,DOCTITLE,PROJ_NO,PROJ_NAME,ALTDOCNO,LU_NAME,KEY_REF,MAINBLK_STRUCTURE", false, true);
      headlay.defineGroup(mgr.translate("DOCMAWDOCISSUEGENERALGROUP: General"), ",DOC_REV_TEXT,DT_DOC_REV,CHECKED_OUT_USR,FILE_STATUS_DES,DT_RELEASED,INFO,DT_OBSOLETE,LANGUAGE_CODE,SLANGUAGEDESC,FORMAT_SIZE,SFORMATSIZEDESC,ACCESS_CONTROL,SCALE,REASON_FOR_ISSUE,REASON_DESC,DOC_RESP_DEPT,DOC_RESP_SIGN,APPROVAL_UPDATE,FILE_TEMPLATE,TECHOBJ", true, false);
      //
      headlay.defineGroup(mgr.translate("DOCMAWDOCISSUEDEVELOPGROUP: Development"), "USER_CREATED,USER_SIGN,DT_CRE,DT_CHG,DT_PLANNED_START,DT_PLANNED_FINISH,DT_ACTUAL_FINISH,REDLINE_STATUS_DES,DAYS_EXPIRED,NEXPIRATIONDUEDAYS,DOC_STATUS,PROFILE_ID,REDLINE_STATUS,FILE_TYPE,BRIEFCASE_NO", true, false);
      headlay.defineGroup(mgr.translate("DOCMAWDOCISSUESHEETNO: Sheets/Descriptions"), ",DESCRIPTION1,NO_OF_SHEETS,DESCRIPTION2,NEXT_DOC_SHEET,DESCRIPTION3,SHEET_ORDER,DESCRIPTION4,DESCRIPTION5,DESCRIPTION6", true, false);

      // Tab commands
      headbar.addCustomCommand("activateTitle", "Title");
      headbar.addCustomCommand("activateOriginals", "Originals");
      headbar.addCustomCommand("activateObjects", "Objects");
      headbar.addCustomCommand("activateConsistsOf", "Consists Of");
      headbar.addCustomCommand("activateWhereUsed", "Where Used");
      headbar.addCustomCommand("activateRouting", "Approval");
      headbar.addCustomCommand("activateAccess", "Access");
      headbar.addCustomCommand("activateHistory", "History");
      headbar.addCustomCommand("activateFileReferences", "File Refs.");
      headbar.addCustomCommand("activateTransmittals", "Transmittals");
      

      //
      // Document Title
      //

      itemblk0 = mgr.newASPBlock("ITEM0");

      itemblk0.disableDocMan();

      itemblk0.addField("ITEM0_OBJID").
      setHidden().
      setDbName("OBJID");

      itemblk0.addField("ITEM0_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      itemblk0.addField("ITEM0_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk0.addField("SDOCNAME").
      setSize(20).
      setFunction("DOC_CLASS_API.GET_NAME(:ITEM0_DOC_CLASS)").
      setReadOnly().
      setLabel("DOCMAWDOCISSUESDOCCLASSDESC: Doc Class Desc"); //Bug Id 40265
      mgr.getASPField("DOC_CLASS").setValidation("SDOCNAME");

      itemblk0.addField("ITEM0_DOC_NO").
      setDbName("DOC_NO").
      setHidden();

      itemblk0.addField("ALTERNATE_DOCUMENT_NUMBER").
      setSize(20).
      setMaxLength(20).
      setLabel("DOCMAWDOCISSUEALTDOCNO: Alt Doc No");

      itemblk0.addField("TITLE").
      setSize(20).
      setMaxLength(250).
      setMandatory().
      setLabel("DOCMAWDOCISSUETITLE: Title");

      itemblk0.addField("ITEM0_INFO").
      setDbName("INFO").
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCISSUEINFO: Note");

      itemblk0.addField("ITEM0_TITLE_REV").
      setDbName("TITLE_REV").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setLabel("DOCMAWDOCISSUETITLEREV: Title Rev");

      itemblk0.addField("ITEM0_TITLE_REV_NOTE").
      setDbName("TITLE_REV_NOTE").
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCISSUETITLEREVNOTE: Title Rev Note");

      itemblk0.addField("VIEW_FILE_REQ").
      setSize(20).
      setMaxLength(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_View_Copy_Req_API").
      setLabel("DOCMAWDOCISSUEVIEWFILEREQ: View Copy");

      itemblk0.addField("STRUCTURE").
      setCheckBox("0,1").
      setLabel("DOCISSUESTRUCTURE: Structure");

      itemblk0.addField("OBJ_CONN_REQ").
      setSize(20).
      setMaxLength(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Reference_Object_Req_API").
      setLabel("DOCMAWDOCISSUEOBJCONNREQ: Object");

      itemblk0.addField("MAKE_WASTE_REQ").
      setSize(20).
      setMaxLength(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Make_Waste_Req_API").
      setLabel("DOCMAWDOCISSUEMAKEWASTEREQ: Destroy");

      itemblk0.addField("SAFETY_COPY_REQ").
      setSize(20).
      setMaxLength(20).
      setMandatory().
      setSelectBox().
      enumerateValues("Doc_Safety_Copy_Req_API").
      setLabel("DOCMAWDOCISSUESAFETYCOPYREQ: Safety Copy");

      itemblk0.addField("REPL_BY_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUEREPLBYDOCCLASS: Replaced By Class");

      itemblk0.addField("REPL_BY_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().
      setDynamicLOV("DOC_TITLE","REPL_BY_DOC_CLASS DOC_CLASS").
      setLabel("DOCMAWDOCISSUEREPLBYDOCNO: Replaced By No");

      itemblk0.addField("ORIG_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUEORIGDOCCLASS: Based On Class");

      itemblk0.addField("ORIG_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().
      setDynamicLOV("DOC_TITLE","ORIG_DOC_CLASS DOC_CLASS").
      setLabel("DOCMAWDOCISSUEORIGDOCNO: Based On No");

      itemblk0.addField("ORIG_DOC_SHEET").
      setSize(20).
      setMaxLength(10).
      setDynamicLOV("DOC_ISSUE_LOV1","ORIG_DOC_CLASS DOC_CLASS, ORIG_DOC_NO DOC_NO").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCISSUEORIGDOCSHEET1: List of Based on Sheet")).
      setUpperCase().
      setLabel("DOCMAWDOCISSUEORIGDOCSHEET: Based On Sheet");

      itemblk0.addField("ORIG_DOC_REV").
      setSize(20).
      setMaxLength(6).
      setDynamicLOV("DOC_ISSUE","ORIG_DOC_CLASS DOC_CLASS, ORIG_DOC_NO DOC_NO").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCISSUEORIGDOCREV1: List of Based on Revision")).
      setUpperCase().
      setLabel("DOCMAWDOCISSUEORIGDOCREV: Based On Rev");

      itemblk0.addField("ITEM0_USER_CREATED").
      setDbName("USER_CREATED").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEUSERCREATED: Created By");

      itemblk0.addField("ITEM0_DT_CRE","Date").
      setDbName("DT_CRE").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEDTCRE: Date Created");

      itemblk0.addField("ISO_CLASSIFICATION").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setLabel("DOCMAWDOCISSUEISOCLASSIFICATION: Iso Classification");

      itemblk0.addField("CONFIDENTIAL").
      setSize(20).
      setMaxLength(15).
      setUpperCase().
      setLabel("DOCMAWDOCISSUECONFIDENTIAL: Confidential");

      itemblk0.addField("ISSUEEDITACCESS").
      setHidden().
      setFunction("Doc_Title_API.Check_Title_Editable_(:DOC_CLASS,:DOC_NO)");

      itemblk0.setView("DOC_TITLE");
      itemblk0.defineCommand("DOC_TITLE_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.disableCommand(itembar0.BACK);
      itembar0.disableCommand(itembar0.NEWROW);
      itembar0.disableCommand(itembar0.DUPLICATEROW);
      itembar0.disableCommand(itembar0.DELETE);
      itembar0.disableCommand(itembar0.FORWARD);
      itembar0.disableCommand(itembar0.BACKWARD);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0");
      itembar0.defineCommand(itembar0.EDITROW,"editTitle");

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCISSUEDOCTITLE: Title"));

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(2);
      itemlay0.setDefaultLayoutMode(itemlay0.SINGLE_LAYOUT);


      //
      // Originals
      //

      itemblk1 = mgr.newASPBlock("ITEM1");

      itemblk1.disableDocMan();

      itemblk1.addField("ITEM1_OBJID").
      setHidden().
      setDbName("OBJID");

      itemblk1.addField("ITEM1_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      itemblk1.addField("SHEET_SPEC").
      setSize(20).
      setMaxLength(24).
      setMandatory().
      setReadOnly().
      setInsertable().
      setUpperCase().
      setLabel("DOCMAWDOCISSUESHEETSPEC: ID No");

      itemblk1.addField("MEDIUM").
      setSize(20).
      setMaxLength(2).
      setUpperCase().
      setDynamicLOV("DOC_CLASS_MEDIUM_LOV","DOC_CLASS").
      setLabel("DOCMAWDOCISSUEMEDIA: Media");

      itemblk1.addField("DEPT").
      setSize(20).
      setMaxLength(24).
      setUpperCase().
      setLabel("DOCMAWDOCISSUEDEPT: Department");

      itemblk1.addField("LOCATION").
      setSize(20).
      setMaxLength(24).
      setUpperCase().
      setLabel("DOCMAWDOCISSUELOCATION: Location ID");

      itemblk1.addField("LOAN_SHEET").
      setSize(20).
      setMaxLength(24).
      setUpperCase().
      setLabel("DOCMAWDOCISSUELOANSHEET: Borrowed Sheet");

      itemblk1.addField("LOAN_DEPT").
      setSize(20).
      setMaxLength(4).
      setUpperCase().
      setLabel("DOCMAWDOCISSUELOANDEPT: Borrowing Department");

      itemblk1.addField("LOAN_SIGN").
      setSize(20).
      setMaxLength(30).
      setLabel("DOCMAWDOCISSUELOANSIGN: Borrowed By");

      itemblk1.addField("DT_LOAN","Date").
      setSize(20).
      setLabel("DOCMAWDOCISSUEDTLOAN: Date Borrowed");

      itemblk1.addField("APPL_NODE").
      setSize(20).
      setMaxLength(20).
      setUpperCase().
      setLabel("DOCMAWDOCISSUEAPPLNODE: Application Node");

      itemblk1.addField("APPL_PROGRAM").
      setSize(20).
      setMaxLength(24).
      setUpperCase().
      setLabel("DOCMAWDOCISSUEAPPLPROGRAM: Application Program");

      itemblk1.addField("ITEM1_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk1.addField("ITEM1_DOC_SHEET").
      setDbName("DOC_SHEET").
      setHidden();

      itemblk1.addField("ITEM1_DOC_NO").
      setDbName("DOC_NO").
      setHidden();

      itemblk1.addField("ITEM1_DOC_REV").
      setDbName("DOC_REV").
      setHidden();

      itemblk1.setView("DOC_ISSUE_ORIGINAL");
      itemblk1.defineCommand("DOC_ISSUE_ORIGINAL_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(headblk);

      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.addCustomCommand("transferToDocumentSheets",mgr.translate("DOCMAWDOCISSUEDOCSHEETS: Document Sheets..."));

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWDOCISSUEDOCORIGI: Originals"));

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDialogColumns(2);
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);


      //
      // Object Connections
      //

      itemblk2 = mgr.newASPBlock("ITEM2");

      itemblk2.disableDocMan();

      itemblk2.addField("ITEM2_OBJID").
      setHidden().
      setDbName("OBJID");

      itemblk2.addField("ITEM2_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      itemblk2.addField("ITEM2_LU_NAME").
      setDbName("LU_NAME").
      setHidden();

      itemblk2.addField("ITEM2_KEY_REF").
      setDbName("KEY_REF").
      setHidden();

      itemblk2.addField("KEY_VALUE").
      setHidden();

      itemblk2.addField("ITEM2_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk2.addField("ITEM2_DOC_NO").
      setDbName("DOC_NO").
      setHidden();

      itemblk2.addField("ITEM2_DOC_SHEET").
      setDbName("DOC_SHEET").
      setHidden();

      itemblk2.addField("SLUDESC").
      setSize(20).
      setMaxLength(2000).
      setReadOnly().
      setFunction("OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(:ITEM2_LU_NAME)").
      setLabel("DOCMAWDOCISSUESLUDESC: Object");

      itemblk2.addField("SINSTANCEDESC").
      setSize(40).
      setMaxLength(2000).
      setReadOnly().
      setFunction("OBJECT_CONNECTION_SYS.Get_Instance_Description(:ITEM2_LU_NAME, NULL, :ITEM2_KEY_REF)").
      setLabel("DOCMAWDOCISSUESINSTANCEDESC: Object Key");

      itemblk2.addField("DOC_OBJECT_DESC").
      setSize(20).
      setMaxLength(100).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEDOCOBJECTDESC: Object Desc");

      itemblk2.addField("CATEGORY").
      setSize(20).
      setMaxLength(5).
      setDynamicLOV("DOC_REFERENCE_CATEGORY").
      setUpperCase().
      setLabel("DOCMAWDOCISSUECATEGORY: Association Category");

      itemblk2.addField("KEEP_LAST_DOC_REV").
      setSize(20).
      setMaxLength(200).
      setMandatory().
      setSelectBox().
      enumerateValues("ALWAYS_LAST_DOC_REV_API").
      setLabel("DOCMAWDOCISSUEKEEPLASTDOCREV: Update Revision");

      itemblk2.addField("SKEEPCODE").
      setHidden().
      setFunction("Always_Last_Doc_Rev_API.Encode(:KEEP_LAST_DOC_REV)");
      mgr.getASPField("KEEP_LAST_DOC_REV").setValidation("SKEEPCODE");

      itemblk2.addField("ITEM2_DOC_REV").
      setDbName("DOC_REV").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setMandatory().
      setDynamicLOV("DOC_ISSUE","ITEM2_DOC_CLASS DOC_CLASS,ITEM2_DOC_NO DOC_NO").
      setLabel("DOCMAWDOCISSUEITEM2DOCREV: Document Revision");

      itemblk2.addField("COPY_FLAG").
      setSize(20).
      setMaxLength(200).
      setMandatory().
      setSelectBox().
      enumerateValues("DOC_REFERENCE_COPY_STATUS_API").
      setLabel("DOCMAWDOCISSUECOPYFLAG: Copy Status");

      itemblk2.addField("NNOOFDOCUMENTS","Number").
      setSize(20).
      setMaxLength(4).
      setReadOnly().
      setFunction("DOC_REFERENCE_OBJECT_API.Get_Number_Of_References(:ITEM2_LU_NAME,:ITEM2_KEY_REF)").
      setLabel("DOCMAWDOCISSUENNOOFDOCUMENTS: No of Docs Connected to Object");

      itemblk2.addField("SURVEY_LOCKED_FLAG").
      setSize(20).
      setMaxLength(200).
      setReadOnly().
      setLabel("DOCMAWDOCISSUESURVEYLOCKEDFLAG: Doc Connection Status");

      itemblk2.setView("DOC_REFERENCE_OBJECT");
      itemblk2.defineCommand("DOC_REFERENCE_OBJECT_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(headblk);

      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.addCustomCommand("objectDetails",mgr.translate("DOCMAWDOCISSUEOBJDETAILS: Object Details..."));
      
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DOCMAWDOCISSUEDOCOBJECT: Objects"));
     
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(2);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);


      //
      // Document Structure - Consists of
      //

      itemblk3 = mgr.newASPBlock("ITEM3");

      itemblk3.disableDocMan();

      itemblk3.addField("ITEM3_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk3.addField("ITEM3_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk3.addField("REPLACE_REVISION_TITLE").
      setHidden().
      setReadOnly().
      setBold().
      setFunction("''").
      setLabel("DOCMAWDOCISSUEREPLACEREVISIONTITLE: Enter Replacement Document:");

      itemblk3.addField("SUB_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUESUBDOCCLASS: Doc Class");

      itemblk3.addField("SUB_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_TITLE","SUB_DOC_CLASS DOC_CLASS").
      setCustomValidation("SUB_DOC_CLASS,SUB_DOC_NO","SSUBDOCTITLE").
      //setCustomValidation("DOC_CLASS,DOC_NO","SSUBDOCTITLE").
      setSecureHyperlink("DocIssue.page", "SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV"). //Bug Id 85223
      setLabel("DOCMAWDOCISSUESUBDOCNO: Doc No");

      itemblk3.addField("SUB_DOC_SHEET").
      setSize(20).
      setMaxLength(10).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE_LOV1", "SUB_DOC_CLASS DOC_CLASS, SUB_DOC_NO DOC_NO").
      setLOVProperty("TITLE", mgr.translate("DOCMAWDOCISSUESUBDOCSHEET1: List of Based on Sheets")).
      setLabel("DOCMAWDOCISSUESUBDOCSHEET: Doc Sheet");

      itemblk3.addField("SUB_DOC_REV").
      setSize(20).
      setMaxLength(6).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE", "SUB_DOC_CLASS DOC_CLASS, SUB_DOC_NO DOC_NO, SUB_DOC_SHEET DOC_SHEET").
      setCustomValidation("SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV","NNOOFCHILDREN").
      setLabel("DOCMAWDOCISSUESUBDOCREV: Revision");

      itemblk3.addField("SSUBDOCTITLE").
      setSize(20).
      setMaxLength(80).
      setReadOnly().
      setFunction("DOC_TITLE_API.Get_Title(:SUB_DOC_CLASS, :SUB_DOC_NO)").
      setLabel("DOCMAWDOCISSUESSUBDOCTITLE: Title");

      itemblk3.addField("ITEMBLK3_STATE").
      setSize(20).
      setMaxLength(253).
      setReadOnly().
      setFunction("DOC_ISSUE_API.Get_State (:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)").
      setLabel("DOCMAWDOCISSUECONSISTSOFSTATE: Document Status");

      itemblk3.addField("NNOOFCHILDREN","Number").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setFunction("DOC_STRUCTURE_API.Number_Of_Children_(:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)").
      setLabel("DOCMAWDOCISSUENNOOFCHILDREN: No of Child documents");

      itemblk3.addField("CONSISTS_OF_RELATIVE_PATH").
      setDbName("RELATIVE_PATH").
      setSize(40).
      setMaxLength(256).
      setLabel("DOCMAWDOCISSUERELATIVEPATH: Relative Path");

      itemblk3.addField("ITEM3_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk3.addField("ITEM3_DOC_NO").
      setDbName("DOC_NO").
      setHidden();

      itemblk3.addField("ITEM3_DOC_SHEET").
      setDbName("DOC_SHEET").
      setHidden();

      itemblk3.addField("ITEM3_DOC_REV").
      setDbName("DOC_REV").
      setHidden();

      itemblk3.setView("DOC_STRUCTURE");
      itemblk3.defineCommand("DOC_STRUCTURE_API","New__,Modify__,Remove__");
      itemblk3.setMasterBlock(headblk);

      itemset3 = itemblk3.getASPRowSet();

      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
      itembar3.defineCommand(itembar3.SAVERETURN,"saveReturnITEM3");
      itembar3.addCustomCommand("transferToDocInfoFromConsistOf",mgr.translate("DOCMAWDOCISSUEDOCINFO: Document Info..."));
      itembar3.addSecureCustomCommand("replaceRevision", mgr.translate("DOCMAWDOCISSUEREPLACEREVISION: Replace Revision..."),"Doc_Structure_API.Replace_Issue_"); //Bug Id 70286
      itembar3.addCustomCommand("previousLevel",mgr.translate("DOCMAWDOCISSUEPREVLEVEL: Previous Level"));
      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("DOCMAWDOCISSUEDOCCONSIT: Consists Of"));

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDialogColumns(2);
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);


      //
      // Doucment Structure - Where used
      //

      itemblk4 = mgr.newASPBlock("ITEM4");

      itemblk4.disableDocMan();

      itemblk4.addField("ITEM4_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk4.addField("ITEM4_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk4.addField("ITEM4_DOC_CLASS").
      setDbName("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUEITEM4DOCCLASS: Doc Class");

      itemblk4.addField("ITEM4_DOC_NO").
      setDbName("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_TITLE","ITEM4_DOC_CLASS DOC_CLASS").
      setCustomValidation("DOC_CLASS,DOC_NO","ITEM4_SSUBDOCTITLE").
      setSecureHyperlink("DocIssue.page", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV"). //Bug Id 85223
      setLabel("DOCMAWDOCISSUEITEM4DOCNO: Doc No");

      itemblk4.addField("ITEM4_DOC_SHEET").
      setDbName("DOC_SHEET").
      setSize(20).
      setMaxLength(10).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE_LOV1", "ITEM4_DOC_CLASS DOC_CLASS, ITEM4_DOC_NO DOC_NO").
      setLOVProperty("TITLE", mgr.translate("DOCMAWDOCISSUESUBDOCSHEET1: List of Based on Sheets")).
      setLabel("DOCMAWDOCISSUEITEM4DOCSHEET: Doc Sheet");

      itemblk4.addField("ITEM4_DOC_REV").
      setDbName("DOC_REV").
      setSize(20).
      setMaxLength(6).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE", "ITEM4_DOC_CLASS DOC_CLASS, ITEM4_DOC_NO DOC_NO, ITEM4_DOC_SHEET DOC_SHEET").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV","ITEM4_NNOOFCHILDREN").
      setLabel("DOCMAWDOCISSUEITEM4DOCREV: Revision");

      itemblk4.addField("ITEM4_SSUBDOCTITLE").
      setSize(20).
      setMaxLength(80).
      setReadOnly().
      setFunction("DOC_TITLE_API.Get_Title(:ITEM4_DOC_CLASS,:ITEM4_DOC_NO)").
      setLabel("DOCMAWDOCISSUEITEM4SSUBDOCTITLE: Title");
      mgr.getASPField("ITEM4_DOC_NO").setValidation("ITEM4_SSUBDOCTITLE");

      itemblk4.addField("ITEMBLK4_STATE").
      setSize(20).
      setMaxLength(253).
      setReadOnly().
      setFunction("DOC_ISSUE_API.Get_State (:ITEM4_DOC_CLASS, :ITEM4_DOC_NO, :ITEM4_DOC_SHEET, :ITEM4_DOC_REV)").
      setLabel("DOCMAWDOCISSUECONSISTSOFSTATE: Document Status");

      itemblk4.addField("ITEM4_NNOOFCHILDREN","Number").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setFunction("DOC_STRUCTURE_API.Number_Of_Children_(:ITEM4_DOC_CLASS, :ITEM4_DOC_NO, :ITEM4_DOC_SHEET, :ITEM4_DOC_REV)").
      setLabel("DOCMAWDOCISSUEITEM4NNOOFCHILDREN: No of Child documents");
      mgr.getASPField("ITEM4_DOC_REV").setValidation("ITEM4_NNOOFCHILDREN");

      itemblk4.addField("WHERE_USED_RELATIVE_PATH").
      setDbName("RELATIVE_PATH").
      setSize(40).
      setMaxLength(256).
      setLabel("DOCMAWDOCISSUERELATIVEPATH: Relative Path");

      itemblk4.addField("ITEM4_SUB_DOC_CLASS").
      setDbName("SUB_DOC_CLASS").
      setHidden();

      itemblk4.addField("ITEM4_SUB_DOC_NO").
      setDbName("SUB_DOC_NO").
      setHidden();

      itemblk4.addField("ITEM4_SUB_DOC_SHEET").
      setDbName("SUB_DOC_SHEET").
      setHidden();

      itemblk4.addField("ITEM4_SUB_DOC_REV").
      setDbName("SUB_DOC_REV").
      setHidden();

      itemblk4.setView("DOC_STRUCTURE");
      itemblk4.defineCommand("DOC_STRUCTURE_API","New__,Modify__,Remove__");
      itemblk4.setMasterBlock(headblk);

      itemset4 = itemblk4.getASPRowSet();

      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
      itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
      itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
      itembar4.defineCommand(itembar4.DUPLICATEROW,"duplicateRowITEM4");
      itembar4.addCustomCommand("transferToDocInfoFromWhereUsed",mgr.translate("DOCMAWDOCISSUEDOCINFO: Document Info..."));

      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("DOCMAWDOCISSUEDOCWHEUSED: Where Used"));

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(2);
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);


      //
      // Approval Routing
      //

      itemblk5 = mgr.newASPBlock("ITEM5");

      itemblk5.disableDocMan();

      itemblk5.addField("ITEM5_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk5.addField("ITEM5_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk5.addField("STEP_NO", "Number").
      setSize(20).
      setMaxLength(3).
      setMandatory().
      setUpperCase().
      setInsertable().
      setReadOnly().
      setLabel("DOCMAWDOCISSUESTEP_NO: Approval Step No");

      itemblk5.addField("DESCRIPTION").
      setSize(20).
      setMaxLength(40).
      setMandatory().
      setInsertable().
      setLabel("DOCMAWDOCISSUEDESC: Description");

      itemblk5.addField("PERSON_ID").
      setSize(20).
      setMaxLength(20).
      setUpperCase().
      setInsertable().
      //Bug Id 73162 Start,Unset readonly flag
      //setReadOnly().
      //Bug Id 73162 End
      setDynamicLOV("PERSON_INFO_LOV").
      setLabel("DOCMAWDOCISSUEPERSON_ID: Person ID");

      itemblk5.addField("ROUTE_SIGN_NAME").
      setSize(20).
      setMaxLength(2000).
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_Name(:PERSON_ID)").
      setLabel("DOCMAWDOCISSUEROUTESIGNNAME: Name");
      mgr.getASPField("PERSON_ID").setValidation("ROUTE_SIGN_NAME");

      itemblk5.addField("GROUP_ID").
      setSize(20).
      setMaxLength(20).
      setUpperCase().
      //Bug Id 73162 Start,Unset readonly flag
      //setReadOnly().
      //Bug Id 73162 End
      setInsertable().
      setDynamicLOV("DOCUMENT_GROUP").
      setLabel("DOCMAWDOCISSUEGROUPID: Group ID");

      itemblk5.addField("GROUP_DESCRIPTION").
      setSize(20).
      setReadOnly().
      setFunction("DOCUMENT_GROUP_API.Get_Group_Description(:GROUP_ID)").
      setLabel("DOCMAWDOCISSUEGROUPDESCRIPTION: Group Desc");
      mgr.getASPField("GROUP_ID").setValidation("GROUP_DESCRIPTION");

      itemblk5.addField("APPROVAL_STATUS").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEAPPROVALSTATUS: Approval Status");

      itemblk5.addField("APPROVAL_STATUS_DB").
      setFunction("Approval_Status_API.Encode(:APPROVAL_STATUS)").
      setHidden();

      itemblk5.addField("APP_SIGN").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEAPPSIGN: Approver");

      itemblk5.addField("APP_SIGN_NAME").
      setSize(20).
      setReadOnly().
      setFunction("person_info_api.Get_Name(:APP_SIGN)").
      setLabel("DOCMAWDOCISSUEAPPSIGNNAME: Approver Name");

      itemblk5.addField("APP_DATE","Date").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEAPPDATE: Approved Date");

      itemblk5.addField("NOTE").
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCISSUENOTE: Note");

      itemblk5.addField("LINE_NO").
      setHidden();

      itemblk5.addField("ITEM5_LU_NAME").
      setDbName("LU_NAME").
      setHidden();

      itemblk5.addField("ITEM5_KEY_REF").
      setDbName("KEY_REF").
      setHidden();

      itemblk5.addField("ITEM5_SECURITY_CHECKPOINT_REQ").
      setDbName("SECURITY_CHECKPOINT_REQ").
      setCheckBox("FALSE,TRUE").
      setLabel("DOCMAWDOCISSUECHECKPOINTREQ: Security Checkpoint Required");

      //Bug Id 74985, Start
      itemblk5.addField("ITEM5_EDIT_ENABLE").
      setFunction("APPROVAL_ROUTING_API.Check_Modify_Note__(:LU_NAME,:KEY_REF,:LINE_NO,:STEP_NO)").
      setHidden();
      //Bug Id 74985, End

      itemblk5.setView("APPROVAL_ROUTING");
      itemblk5.defineCommand("APPROVAL_ROUTING_API","New__,Modify__,Remove__,SET_NEXT_APP_STEP");
      itemblk5.setMasterBlock(headblk);

      itemset5 = itemblk5.getASPRowSet();

      itembar5 = mgr.newASPCommandBar(itemblk5);
      itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");
      itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
      itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
      itembar5.defineCommand(itembar5.SAVERETURN,"saveReturnITEM5");
      itembar5.defineCommand(itembar5.SAVENEW,"saveNewITEM5");
      //Bug Id 74985, Start
      itembar5.addSecureCustomCommand("editITEM5", "DOCMAWDOCISSUEAPPSTEPEDIT: Edit","APPROVAL_ROUTING_API.Modify__"); 
      itembar5.addCommandValidConditions("editITEM5", "ITEM5_EDIT_ENABLE", "Enable", "TRUE");
      //Bug Id 74985, End
      itembar5.addSecureCustomCommand("nextAppStep",mgr.translate("DOCMAWDOCISSUEAPPSTEP: Approve Step"),"APPROVAL_ROUTING_API.Set_Next_App_Step"); //Bug Id 70286
      itembar5.addSecureCustomCommand("rejectStep",mgr.translate("DOCMAWDOCISSUEREJSTEP: Reject Step"),"APPROVAL_ROUTING_API.Set_Next_App_Step"); //Bug Id 70286
      
      itemtbl5 = mgr.newASPTable(itemblk5);
      itemtbl5.setTitle(mgr.translate("DOCMAWDOCISSUEDOCROUT: Approval"));

      itemlay5 = itemblk5.getASPBlockLayout();
      itemlay5.setDialogColumns(2);
      itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);


      //
      // Document Access
      //

      itemblk6 = mgr.newASPBlock("ITEM6");

      itemblk6.disableDocMan();

      itemblk6.addField("ITEM6_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk6.addField("ITEM6_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk6.addField("ITEM6_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk6.addField("ITEM6_DOC_NO").
      setDbName("DOC_NO").
      setHidden();

      itemblk6.addField("ITEM6_DOC_SHEET").
      setDbName("DOC_SHEET").
      setHidden();

      itemblk6.addField("ITEM6_DOC_REV").
      setDbName("DOC_REV").
      setHidden();

      itemblk6.addField("ITEM6_LINE_NO","Number").
      setDbName("LINE_NO").
      setHidden();

      itemblk6.addField("ACCESS_OWNER","Number").
      setSize(20).
      setCheckBox("0,1").
      setLabel("DOCMAWDOCISSUEACCESSOWNER: Administrator");

      itemblk6.addField("ITEM6_GROUP_ID").
      setDbName("GROUP_ID").
      setSize(20).
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOCUMENT_GROUP").
      setLabel("DOCMAWDOCISSUEITEM6GROUPID: Group ID");

      itemblk6.addField("GROUPDESCRIPTION").
      setSize(20).
      setFunction("DOCUMENT_GROUP_API.Get_Group_Description(:ITEM6_GROUP_ID)").
      setReadOnly().
      setLabel("DOCMAWDOCISSUEGROUPDESCRIPTION: Group Desc");
      mgr.getASPField("ITEM6_GROUP_ID").setValidation("GROUPDESCRIPTION");

      itemblk6.addField("ITEM6_PERSON_ID").
      setDbName("PERSON_ID").
      setSize(20).
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("PERSON_INFO_LOV").
      setCustomValidation("ITEM6_PERSON_ID,ITEM6_GROUP_ID","PERSONNAME,PERSONUSERID").
      setLabel("DOCMAWDOCISSUEITEM6PERSONID: Person ID");

      itemblk6.addField("PERSONNAME").
      setSize(20).
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_Name(:ITEM6_PERSON_ID)").
      setLabel("DOCMAWDOCISSUEPERSONNAME: Name");

      itemblk6.addField("PERSONUSERID").
      setSize(20).
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_User_Id(:ITEM6_PERSON_ID)").
      setLabel("DOCMAWDOCISSUEPERSONUSERID: User ID");


      itemblk6.addField("EDIT_ACCESS","Number").
      setSize(20).
      setCheckBox("0,1").
      setLabel("DOCMAWDOCISSUEEDITACCESS: Edit Access");

      itemblk6.addField("VIEW_ACCESS","Number").
      setSize(20).
      setCheckBox("0,1").
      setLabel("DOCMAWDOCISSUEVIEWACCESS: View Access");

      itemblk6.addField("ITEM6_NOTE").
      setDbName("NOTE").
      setSize(20).
      setLabel("DOCMAWDOCISSUEITEM6NOTE: Note");

      /*itemblk6.addField("ITEM6_KEY_REF").
      setFunction("Doc_Issue_Api.Get_Key_Ref_(:ITEM6_DOC_CLASS,:ITEM6_DOC_NO,:ITEM6_DOC_SHEET,:ITEM6_DOC_REV)").
      setHidden();*/

      itemblk6.addField("ITEM6_EXIST_IN_APPROVAL").
      setFunction("Approval_Routing_Api.Exist_In_Approval_Routing_('DocIssue',Doc_Issue_Api.Get_Key_Ref_(:ITEM6_DOC_CLASS,:ITEM6_DOC_NO,:ITEM6_DOC_SHEET,:ITEM6_DOC_REV),:ITEM6_PERSON_ID,:ITEM6_GROUP_ID)").
      setHidden();


      itemblk6.setView("DOCUMENT_ISSUE_ACCESS");
      itemblk6.defineCommand("DOCUMENT_ISSUE_ACCESS_API","New__,Modify__,Remove__");
      itemblk6.setMasterBlock(headblk);

      itemset6 = itemblk6.getASPRowSet();
      itembar6 = mgr.newASPCommandBar(itemblk6);
      itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
      itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
      itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
      itembar6.defineCommand(itembar6.DELETE,"deleteRowITEM6");
      itembar6.addCustomCommand("transferToDocGroup", mgr.translate("DOCMAWDOCISSUETRDOCGROUP: Document Group..."));
      itembar6.addCustomCommand("transferToPersonInfo", mgr.translate("DOCMAWDOCISSUEPERSONINFO: Person Info..."));

      itemtbl6 = mgr.newASPTable(itemblk6);
      itemtbl6.setTitle(mgr.translate("DOCMAWDOCISSUEDOCACESS: Access"));

      itemlay6 = itemblk6.getASPBlockLayout();
      itemlay6.setDialogColumns(2);
      itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);



      //
      // Document History
      //

      itemblk7 = mgr.newASPBlock("ITEM7");

      itemblk7.disableDocMan();

      itemblk7.addField("ITEM7_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk7.addField("ITEM7_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk7.addField("ITEM7_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk7.addField("ITEM7_DOC_NO").
      setDbName("DOC_NO").
      setHidden();

      itemblk7.addField("ITEM7_DOC_SHEET").
      setDbName("DOC_SHEET").
      setHidden();

      itemblk7.addField("ITEM7_DOC_REV").
      setDbName("DOC_REV").
      setHidden();

      itemblk7.addField("ITEM7_LINE_NO","Number").
      setDbName("LINE_NO").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEITEM7LINENO: Line No");

      itemblk7.addField("INFO_CATEGORY").
      setSize(20).
      setMandatory().
      setReadOnly().
      setLabel("DOCMAWDOCISSUEINFOCATEGORY: Info Category");

      itemblk7.addField("ITEM7_STATUS").
      setDbName("STATUS").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEITEM7STATUS: Status");

      itemblk7.addField("ITEM7_NOTE").
      setDbName("NOTE").
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCISSUEITEM7NOTE: Note");

      itemblk7.addField("ITEM7_CREATED_BY").
      setDbName("CREATED_BY").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEITEM7CREATEDBY: Created By");

      itemblk7.addField("ITEM7_SPERSONNAME").
      setSize(20).
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_Name_For_User(:ITEM7_CREATED_BY)").
      setLabel("DOCMAWDOCISSUEITEM7SPERSONNAME: Name");

      itemblk7.addField("ITEM7_DATE_CREATED","Date").
      setDbName("DATE_CREATED").
      setSize(20).
      setReadOnly().
      setLabel("DOCMAWDOCISSUEITEM7DATECREATED: Created");

      itemblk7.addField("ITEM7_ENABLE_HISTORY_NEW").
      setHidden().
      setFunction("''");

      itemblk7.addField("ITEM7_DUMMY1").
      setHidden().
      setFunction("''");

      itemblk7.setView("DOCUMENT_ISSUE_HISTORY");
      itemblk7.defineCommand("DOCUMENT_ISSUE_HISTORY_API","New__,Modify__,Remove__");
      itemblk7.setMasterBlock(headblk);

      itemset7 = itemblk7.getASPRowSet();

      itembar7 = mgr.newASPCommandBar(itemblk7);
      itembar7.disableCommand(itembar7.EDITROW);
      itembar7.disableCommand(itembar7.DELETE);

      itembar7.defineCommand(itembar7.OKFIND,"okFindITEM7");
      itembar7.defineCommand(itembar7.COUNTFIND,"countFindITEM7");
      itembar7.defineCommand(itembar7.NEWROW,"newRowITEM7");
      itembar7.enableCommand(itembar7.FIND);
      
      itemtbl7 = mgr.newASPTable(itemblk7);
      itemtbl7.setTitle(mgr.translate("DOCMAWDOCISSUEDOCHIST: History"));

      itemlay7 = itemblk7.getASPBlockLayout();
      itemlay7.setDialogColumns(2);
      itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT);


      //
      // Copy Approval Template (Routing tab)
      //

      itemblk8 = mgr.newASPBlock("ITEM8");

      itemblk8.disableDocMan();

      itemblk8.addField("TEMP_PROFILE_ID").
      setDbName("PROFILE_ID").
      setSize(20).
      setMaxLength(10).
      setUpperCase().
      setMandatory().
      setDynamicLOV("APPROVAL_PROFILE").
      setCustomValidation("TEMP_PROFILE_ID", "TEMP_DESCRIPTION").
      setLabel("DOCMAWDOCISSUEPROFID: Profile ID");

      itemblk8.addField("TEMP_DESCRIPTION").
      setDbName("DESCRIPTION").
      setSize(20).
      setMaxLength(40).
      setReadOnly().
      setFunction("''").
      setLabel("DOCMAWDOCISSUETEMPDESCRIPTION: Description");

      itemblk8.setView("APPROVAL_PROFILE");
      itemset8 = itemblk8.getASPRowSet();

      itembar8 = mgr.newASPCommandBar(itemblk8);
      itemlay8 = itemblk8.getASPBlockLayout();
      itemlay8.setDialogColumns(1);
      itemlay8.setDefaultLayoutMode(itemlay8.CUSTOM_LAYOUT);
      itemlay8.setEditable();

      itembar8.enableCommand(itembar8.OKFIND);
      itembar8.defineCommand(itembar8.OKFIND,"dlgOk");
      itembar8.enableCommand(itembar8.CANCELFIND);
      itembar8.defineCommand(itembar8.CANCELFIND,"dlgCancel");

      //
      // File References
      //

      itemblk9 = mgr.newASPBlock("ITEM9");

      itemblk9.disableDocMan();

      itemblk9.addField("ITEM9_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk9.addField("ITEM9_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk9.addField("ITEM9_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk9.addField("ITEM9_DOC_NO").
      setDbName("DOC_NO").
      setHidden();

      itemblk9.addField("ITEM9_DOC_SHEET").
      setDbName("DOC_SHEET").
      setHidden();

      itemblk9.addField("ITEM9_DOC_REV").
      setDbName("DOC_REV").
      setHidden();

      itemblk9.addField("ITEM9_DOC_TYPE").
      setDbName("DOC_TYPE").
      setSize(12).
      setLabel("DOCMAWDOCISSUEITEM9DOCTYPE: Document Type");

      itemblk9.addField("ITEM9_FILE_NO","Number").
      setDbName("FILE_NO").
      setHidden();


      itemblk9.addField("IS_IN_TRANSMITTAL").
      setFunction("transmittal_comment_file_api.Check_Document_In_Transmittal(:ITEM9_DOC_CLASS,:ITEM9_DOC_NO,:ITEM9_DOC_SHEET,:ITEM9_DOC_REV,:ITEM9_DOC_TYPE,:ITEM9_FILE_NO)").
      setHidden();

      itemblk9.addField("ITEM9_FILE_NAME").
      setDbName("FILE_NAME").
      setSize(254).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEITEM9REPFILENAME: Repository File Name");

      itemblk9.addField("ITEM9_USER_FILE_NAME").
      setDbName("USER_FILE_NAME").
      setSize(254).
      setLabel("DOCMAWDOCISSUEITEM9FILENAME: Original File Name");

      itemblk9.addField("ITEM9_LOCAL_PATH").
      setDbName("LOCAL_PATH").
      setSize(254).
      setLabel("DOCMAWDOCISSUEITEM9LOCALPATH: Local Path");

      itemblk9.addField("ITEM9_LOCAL_FILENAME").
      setDbName("LOCAL_FILENAME").
      setSize(254).
      setLabel("DOCMAWDOCISSUECHECKEDOUTFILENAME: Checked Out File Name");

      itemblk9.addField("ITEM9_FILETYPE").
      setDbName("FILE_TYPE").
      setSize(30).
      setLabel("DOCMAWDOCISSUEITEM9FILETYPE: File Type");

      itemblk9.addField("ITEM9_STATE").
      setDbName("STATE").
      setSize(253).
      setLabel("DOCMAWDOCISSUEITEM9STATE: State");

      itemblk9.setView("EDM_FILE");
      itemblk9.defineCommand("EDM_FILE_API","New__,Modify__,Remove__");
      itemblk9.setMasterBlock(headblk); 

      itemset9 = itemblk9.getASPRowSet();

      itembar9 = mgr.newASPCommandBar(itemblk9);
      itembar9.defineCommand(itembar9.OKFIND, "okFindITEM9");
      itembar9.disableCommand(itembar9.COUNTFIND);
      itembar9.disableCommand(itembar9.EDITROW);
      itembar9.disableCommand(itembar9.NEWROW);
      itembar9.disableCommand(itembar9.DELETE);
      itembar9.disableCommand(itembar9.DUPLICATEROW);

      itembar9.addSecureCustomCommand("viewCommentFromTransmittal",mgr.translate("DOCMAWDOCISSUEVIEWCOMMENTFILE: View Comment File"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      itembar9.addCommandValidConditions("viewCommentFromTransmittal", "IS_IN_TRANSMITTAL", "Disable", null); //Bug Id 68528

      itemtbl9 = mgr.newASPTable(itemblk9);
      itemtbl9.setTitle("DOCMAWDOCISSUEFILEREF: File Refs.");

      itemlay9 = itemblk9.getASPBlockLayout();
      itemlay9.setDefaultLayoutMode(itemlay9.MULTIROW_LAYOUT);


      //-----------------------------transmittal tab
      itemblk10 = mgr.newASPBlock("item10");

      itemblk10.disableDocMan();
      
      itemblk10.addField("TRANSMITTAL_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk10.addField("TRANSMITTAL_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      //Bug Id 78800, start
      isProjInstalled = DocmawUtil.isProjInstalled(getASPManager());
      //Bug Id 84461, Set the length to 120
      if (isProjInstalled)
         itemblk10.addField("TRANSMITTAL_ID").
         setSize(20).
         setMaxLength(120).
         setReadOnly().
         setInsertable().
         setUpperCase().
         setDynamicLOV("DOCUMENT_TRANSMITTAL").
         setCustomValidation("TRANSMITTAL_ID","TRANSMITTAL_DESCRIPTION,PROJECT_ID"). // transmittal validation 
         setLabel("DOCMAWDOCISSUESTRANSMITTALID: Transmittal Id");
      else
	 itemblk10.addField("TRANSMITTAL_ID").
         setSize(20).
         setMaxLength(120).
         setReadOnly().
         setInsertable().
         setUpperCase().
         setDynamicLOV("DOCUMENT_TRANSMITTAL").
         setCustomValidation("TRANSMITTAL_ID","TRANSMITTAL_DESCRIPTION"). // transmittal validation
         setLabel("DOCMAWDOCISSUESTRANSMITTALID: Transmittal Id");
      //Bug Id 78800, end

      itemblk10.addField("TRANSMITTAL_DESCRIPTION").
      setFunction("DOCUMENT_TRANSMITTAL_API.Get_Transmittal_Description(:TRANSMITTAL_ID)").
      setSize(20).
      setReadOnly().
      setMaxLength(800).
      setLabel("DOCMAWDOCISSUESTRANSDESC: Transmittal Description");

      
      itemblk10.addField("TRANSMITTAL_LINE_NO","Number").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setLabel("DOCMAWDOCISSUESTRANSMITTALLINENO: Transmittal Line No");

      //Bug Id 78800, start
      if (isProjInstalled)
         itemblk10.addField("DOC_TRANSMITTAL_LINE_STATUS").
         setSize(20).
         setDynamicLOV("TRANSMITTAL_LINE_STATE","PROJECT_ID").
	 setLOVProperty("WHERE","PROJECT_ID = NVL(:PROJECT_ID, 'null')"). 
         setLabel("DOCMAWDOCISSUESTRANSMITTALLINESTATE: Doc Transmittal Line Status");
      //Bug Id 78800, end
      
      itemblk10.addField("ITEM10_STATE").
                setSize(20).
                setReadOnly().
                setMaxLength(20).
                setDbName("STATE").
                setLabel("DOCMAWDOCTRANSMITTALSTATE: State");


      itemblk10.addField("TRANSMITTAL_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();
      

      itemblk10.addField("TRANSMITTAL_DOC_NO").
      setDbName("DOC_NO").
      setHidden();
		

		itemblk10.addField("TRANSMITTAL_DOC_SHEET").
		setDbName("DOC_SHEET").
      setHidden();

		itemblk10.addField("TRANSMITTAL_DOC_REV").
		setDbName("DOC_REV").
      setHidden();


      itemblk10.addField("CUSTOMER_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUESCUSTOMERDOCCLASS: Customer Doc Class");


      itemblk10.addField("CUSTOMER_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","CUSTOMER_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCMAWDOCISSUESCUSTOMERDOCNO: Customer Document Number");

		itemblk10.addField("CUSTOMER_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","CUSTOMER_DOCUMENT_CLASS DOC_CLASS,CUSTOMER_DOCUMENT_NO DOC_NO").
		setLabel("DOCMAWDOCISSUESCUSTOMERDOCSHEET: Customer Document Sheet");

		itemblk10.addField("CUSTOMER_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","CUSTOMER_DOCUMENT_CLASS DOC_CLASS,CUSTOMER_DOCUMENT_NO DOC_NO,CUSTOMER_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCMAWDOCISSUESCUSTOMERDOCREV: Customer Document Revision");


      itemblk10.addField("SUPPLIER_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUESSUPPLIERDOCCLASS: Supplier Doc Class");


      itemblk10.addField("SUPPLIER_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","SUPPLIER_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCMAWDOCISSUESSUPPLIERDOCNO: Supplier Document Number");

		itemblk10.addField("SUPPLIER_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","SUPPLIER_DOCUMENT_CLASS DOC_CLASS,SUPPLIER_DOCUMENT_NO DOC_NO").
		setLabel("DOCMAWDOCISSUESSUPPLIERDOCSHEET: Supplier Document Sheet");

		itemblk10.addField("SUPPLIER_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","SUPPLIER_DOCUMENT_CLASS DOC_CLASS,SUPPLIER_DOCUMENT_NO DOC_NO,SUPPLIER_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCMAWDOCISSUESSUPPLIERDOCREV: Supplier Document Revision");



      itemblk10.addField("SUB_CONTRACTOR_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUESSUBCONTRACTORDOCCLASS: Sub Contractor Doc Class");


      itemblk10.addField("SUB_CONTRACTOR_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCMAWDOCISSUESSUBCONTRACTORDOCNO: Sub Contractor Document Number");

		itemblk10.addField("SUB_CONTRACTOR_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS,SUB_CONTRACTOR_DOCUMENT_NO DOC_NO").
		setLabel("DOCMAWDOCISSUESSUBCONTRACTORDOCSHEET: Sub Contractor Document Sheet");

		itemblk10.addField("SUB_CONTRACTOR_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS,SUB_CONTRACTOR_DOCUMENT_NO DOC_NO,SUB_CONTRACTOR_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCMAWDOCISSUESSUBCONTRACTORDOCREV: Sub Contractor Document Revision");

       
      itemblk10.addField("ITEM10_NOTE").
      setDbName("NOTE").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCMAWDOCISSUEITEM10NOTE: Note");
      


      itemblk10.addField("COMMENT_RECEIVED_DATE","Date").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCMAWDOCISSUESCOMMENTRECEIVEDDATE: Comment Received Date");

      /*itemblk10.addField("COMMENT_FILE_ID").
		setSize(20).
		setMaxLength(6).
		setReadOnly().
		setUpperCase().
		setLabel("DOCMAWDOCISSUESCOMMENTFILEID: Comment File Id");

      itemblk10.addField("COMMENT_RECEIVED_ID").
		setSize(20).
		setMaxLength(6).
		setReadOnly().
		setUpperCase().
		setLabel("DOCMAWDOCISSUESCOMMENTRECEIVEDID: Comment Received Id");
      */

      itemblk10.addField("COMMENT_RECEIVED").
      setSize(20).
      setReadOnly().
      setCheckBox ("0,1").
      setLabel("DOCMAWDOCISSUECOMMENTRECEIVED: Comment Received");

      ////
      /*itemblk10.addField("DOC_TRANS_SURVEY_STATUS").
		setSize(20).
		setMaxLength(6).
		setReadOnly().
		setLabel("DOCMAWDOCISSUESDOCSURVERYSTATUS: Document Transmittal Survey Status");*/
      
      // call 146314 (start)

      
      if (isProjInstalled)
      {

      itemblk10.addField("PROJECT_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
		 setFunction("TRANSMITTAL_OBJ_CON_API.Get_Project_Id(:TRANSMITTAL_ID)"). //Bug Id 78800
                 setLabel("DOCMAWDOCTRANSMITTALPROJECTID: Project ID");

                 itemblk10.addField("PROJECT_DESCRIPTION").
                 setFunction("PROJECT_API.Get_Description(:PROJECT_ID)").
                 setSize(20).
                 setReadOnly().
                 setMaxLength(20).
                 setLabel("DOCMAWDOCTRANSMITTALPROJECTDESC: Project Description");

                 itemblk10.addField("SUB_PROJECT_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALSUBPROJECTID: Sub Project ID");

                 itemblk10.addField("ACTIVITY_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALACTIVITYID: Activity ID");
      }

       itemblk10.addField("CUSTOMER_PROJECT_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALCUSTOMERPROJECTID: Customer Project ID");


                 itemblk10.addField("CUSTOMER_SUB_PROJECT_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALCUSTOMERSUBPROJECTID: Customer Sub Project ID");

                 itemblk10.addField("CUSTOMER_ACTIVITY_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCISSUECUSTOMERACTIVITYID: Customer Activity ID");


     itemblk10.addField("SUPPLIER_PROJECT_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALSUPPLIERPROJECTID: Supplier Project ID");


                 itemblk10.addField("SUPPLIER_SUB_PROJECT_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALSUPPLIERSUBPROJECTID: Supplier Sub Project ID");

                 itemblk10.addField("SUPPLIER_ACTIVITY_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCISSUESUPPLIERACTIVITYID: Supplier Activity ID");

       itemblk10.addField("SUB_CONTRACTOR_PROJECT_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALSUBCONTRACTORPROJECTID: Sub Contractor Project ID");


                 itemblk10.addField("SUB_CONTRACTOR_SUB_PROJECT_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALSUBCONTRACTORSUBPROJECTID: Sub Contractor Sub Project ID");

                 itemblk10.addField("SUB_CONTRACTOR_ACTIVITY_ID").
                 setSize(20).
                 setMaxLength(20).
                 setReadOnly().
                 setLabel("DOCMAWDOCTRANSMITTALSUBCONTRACTORACTIVITYID: Sub Contractor Activity ID");

       itemblk10.addField("ITEM10_DIRECTION").
                           setSize(20).
                           setReadOnly().
                           setMaxLength(20).
                           setDbName("TRANSMITTAL_DIRECTION").
                           setLabel("DOCMAWDOCTRANSMITTALDIRECTION: Transmittal Direction");

       // call 146314 (finish)

      //Bug Id 84270, start
      itemblk10.addField("VALIDATE_VIEW_COMMENT").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('viewCommentfiles' ,:TRANSMITTAL_ID,:TRANSMITTAL_LINE_NO)").
      setHidden();

      itemblk10.addField("VALIDATE_SEND_COMMENT").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('sendCommentfiles' ,:TRANSMITTAL_ID,:TRANSMITTAL_LINE_NO)").
      setHidden();

      itemblk10.addField("GETCOMMENTVIEWACCES").
      setHidden().
      setFunction("DOC_ISSUE_API.Get_View_Access_(:TRANSMITTAL_DOC_CLASS, :TRANSMITTAL_DOC_NO, :TRANSMITTAL_DOC_SHEET, :TRANSMITTAL_DOC_REV)");

      itemblk10.addField("DOC_FILE_NO").
      setDbName("FILE_NO").
      setHidden().
      setFunction("Transmittal_Comment_File_Api.Get_File_No(:TRANSMITTAL_DOC_CLASS, :TRANSMITTAL_DOC_NO, :TRANSMITTAL_DOC_SHEET, :TRANSMITTAL_DOC_REV, 'REDLINE', :TRANSMITTAL_ID)");
      //Bug Id 84270, end


      itemblk10.setView("DOC_TRANSMITTAL_PROJ_INFO"); 
      itemblk10.defineCommand("DOC_TRANSMITTAL_ISSUE_API","Modify__");
      itemblk10.defineCommand("DOC_TRANSMITTAL_ISSUE_API","New__,Modify__"); //Bug Id 78800
      itemblk10.setMasterBlock(headblk); 

      itemset10 = itemblk10.getASPRowSet();

     

      itembar10 = mgr.newASPCommandBar(itemblk10);
      itembar10.enableMultirowAction();
      itembar10.defineCommand(itembar10.OKFIND, "okFindITEM10");
      //Bug Id 78800, start
      itembar10.defineCommand(itembar10.NEWROW,"newRowITEM10"); 
      itembar10.defineCommand(itembar10.SAVERETURN,"saveReturnITEM10"); 
      //Bug Id 78800, end

      itembar10.addCustomCommand("transmittalInfo", "DOCMAWDOCISSUETRANSMITTALINFO: Transmittal Info");
      
      itembar10.addCustomCommand("projectInfo", "DOCMAWDOCTRANSMITTALPROJINFO: Project Info");

      itembar10.addCustomCommand("executeReport", "DOCMAWDOCTRANSMITTALREPORT: Execute Transmittal Report"); //Bug Id 79174

      //Bug Id 84270, start
      itembar10.addSecureCustomCommand("viewCommentfiles",mgr.translate("DOCMAWDOCISSUEVIEWCOMMENTFILES: View"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  
      itembar10.addSecureCustomCommand("sendCommentfiles",mgr.translate("DOCMAWDOCISSUESENDMAILCOMMENTFILES: Send by E-mail"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  
      
      itembar10.addCustomCommandGroup("COMMENTFILES", mgr.translate("DOCMAWDOCISSUECOMMENTFILES: Comment Files"));
      itembar10.setCustomCommandGroup("viewCommentfiles", "COMMENTFILES");
      itembar10.setCustomCommandGroup("sendCommentfiles", "COMMENTFILES");

      itembar10.addCommandValidConditions("sendCommentfiles", "VALIDATE_SEND_COMMENT", "Enable", "TRUE");
      itembar10.addCommandValidConditions("viewCommentfiles", "VALIDATE_VIEW_COMMENT", "Enable", "TRUE");
      //Bug Id 84270, end

      itemtbl10 = mgr.newASPTable(itemblk10);
      itemtbl10.setTitle(mgr.translate("DocumentTransmittalIssuesTITLE: Overview - Document Classes for Transmittals"));
      itemtbl10.enableRowSelect();
      itemlay10 = itemblk10.getASPBlockLayout();
      itemlay10.setDialogColumns(2);
      itemlay10.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);


      //-----------------------------transmittal tab


      //
      //  DUMMY BLOCK
      //

      dummyblk = mgr.newASPBlock("DUMMY");

      dummyblk.addField("DOC_TYPE");
      dummyblk.addField("RETURN");
      dummyblk.addField("ATTR");
      dummyblk.addField("APPSTAT_DUMMY");
      dummyblk.addField("ACTION");
      dummyblk.addField("DUMMY1");
      dummyblk.addField("DUMMY2");
      dummyblk.addField("DUMMY3");
      dummyblk.addField("DUMMY4");
      dummyblk.addField("DUMMY5");
      dummyblk.addField("DUMMY6");

      dummyblk.addField("ACCESSOWNER");
      dummyblk.addField("LOGUSER");
      dummyblk.addField("USER_ID");
      dummyblk.addField("USERGETACCESS");
      dummyblk.addField("IN_1");
      dummyblk.addField("IN_2");
      dummyblk.addField("OUT_1");



      //
      // Tab definitions
      //

      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEDOCTITLE: Title"), "javascript:commandSet('HEAD.activateTitle','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEORIGINALS: Originals"), "javascript:commandSet('HEAD.activateOriginals','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEOBJECTS: Objects"), "javascript:commandSet('HEAD.activateObjects','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUECONSISTS: Consists Of"), "javascript:commandSet('HEAD.activateConsistsOf','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEWHEREUSED: Where Used"), "javascript:commandSet('HEAD.activateWhereUsed','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEROUTING: Approval"), "javascript:commandSet('HEAD.activateRouting','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEACCESS: Access"), "javascript:commandSet('HEAD.activateAccess','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEHISTORY: History"), "javascript:commandSet('HEAD.activateHistory','')");
      tabs.addTab("FILE_REF",mgr.translate("DOCMAWDOCISSUEFILEREFERENCES: File Refs."), "javascript:commandSet('HEAD.activateFileReferences','')"); //bug 78032 
      tabs.addTab(mgr.translate("DOCMAWDOCISSUETRANSMITTALS: Transmittals"), "javascript:commandSet('HEAD.activateTransmittals','')");

      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);


      //
      // Static JavaScript
      //


      appendJavaScript("function getElements(name)\n");
      appendJavaScript("{\n");
      appendJavaScript("   return eval('document.form.' + name);\n");
      appendJavaScript("}\n");


      appendJavaScript("function selectDocument(parent_doc_class, parent_doc_no, parent_doc_sheet, parent_doc_rev, doc_class, doc_no, doc_sheet, doc_rev, multi_selection)\n");
      appendJavaScript("{\n");
      appendJavaScript("    if (!single_layout && multi_selection)\n");
      appendJavaScript("    {\n");
      appendJavaScript("       var selectbox = getElements(\"__SELECTED1\");\n");
      appendJavaScript("       for (var x = 0; x < arrDocNo.length; x++)\n");
      appendJavaScript("       {\n");
      appendJavaScript("          if (arrDocNo[x] == doc_no)\n");
      appendJavaScript("          {\n");
      appendJavaScript("             selectbox[x].checked = !selectbox[x].checked;\n");
      appendJavaScript("             CCA(selectbox[x], x);\n");
      appendJavaScript("             return;\n");
      appendJavaScript("          }\n");
      appendJavaScript("       }\n");
      appendJavaScript("    }\n");
      appendJavaScript("    else\n");
      appendJavaScript("    {\n");
      appendJavaScript("       document.form.PARENT_DOC_CLASS.value = parent_doc_class\n");
      appendJavaScript("       document.form.PARENT_DOC_NO.value = parent_doc_no\n");
      appendJavaScript("       document.form.PARENT_DOC_SHEET.value = parent_doc_sheet\n");
      appendJavaScript("       document.form.PARENT_DOC_REV.value = parent_doc_rev\n");
      appendJavaScript("       document.form.CURRENT_DOC_CLASS.value = doc_class\n");
      appendJavaScript("       document.form.CURRENT_DOC_NO.value = doc_no\n");
      appendJavaScript("       document.form.CURRENT_DOC_SHEET.value = doc_sheet\n");
      appendJavaScript("       document.form.CURRENT_DOC_REV.value = doc_rev\n");
      appendJavaScript("       document.form.CHANGE_CURRENT_DOCUMENT.value = \"TRUE\"\n");
      appendJavaScript("       submit() \n");
      appendJavaScript("    }\n");
      appendJavaScript("}\n");

      /*appendJavaScript("function validateViewAccess(i)\n");
      appendJavaScript("{\n");
      appendJavaScript("if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
      appendJavaScript("setDirty();\n");
      appendJavaScript("if( !checkViewAccess(i) ) return;\n");
      appendJavaScript("}\n");*/


   }

   //Bug Id 84270, start
   
   public void viewCommentfiles()
   {
      commentFileAction(false,"VIEWCOMMENT");
   }

   public void sendCommentfiles()
   {
      commentFileAction(false,"SENDCOMMENT");
   }

   public void commentFileAction(boolean createNewline, String fileAction)
   {
      ASPManager mgr = getASPManager();

      if (itemlay10.isMultirowLayout() && itemset10.selectRows() > 1) 
      {
         mgr.showAlert("DOCMAWDOCISSUEMULTIPLEROWSSELECTED: This action is not allowed for multiple row selections.");
         return;
      }

      if (!isValidColumnValueItem10("GETCOMMENTVIEWACCES", "TRUE", true))
      {
         mgr.showAlert("DOCMAWDOCISSUENOCHECKINACCESSFORCOMMENT: You must have view access to be able to check the comment file in.");
         return;
      }

      if (itemlay10.isSingleLayout())
      {
         itemset10.unselectRows();
         itemset10.selectRow();
      }
      else
         itemset10.selectRows();

      ASPBuffer data = itemset10.getSelectedRows("TRANSMITTAL_DOC_CLASS,TRANSMITTAL_DOC_NO,TRANSMITTAL_DOC_SHEET,TRANSMITTAL_DOC_REV,DOC_FILE_NO");

      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", fileAction, "REDLINE", data);
      bTranferToEDM = true;
      
   }

   public boolean isValidColumnValueItem10(String column, String validity_check, boolean match)
   {
      boolean invalid = true;
      int noOFSelectedRows = 1;

      if (itemlay10.isSingleLayout())
      {
	  itemset10.selectRow();
      }
      else
      {
	  itemset10.storeSelections();
	  itemset10.setFilterOn();
	  noOFSelectedRows =  itemset10.countRows();
      }

      for (int k=0;k<noOFSelectedRows;k++)
      {
	 if ((match && !validity_check.equals(itemset10.getRow().getValue(column))) ||
	      (!match && validity_check.equals(itemset10.getRow().getValue(column))))
	 {
	     invalid = false;
	     break;
	 }
	 if (itemlay10.isMultirowLayout())
	 {
	     itemset10.next();
	 }

      }

      if (itemlay10.isMultirowLayout())
      {
	  itemset10.setFilterOff();
      }
      return invalid;
   }
   
   //Bug Id 84270, end


   public void adjust() throws FndException
   {
      ASPManager mgr = getASPManager();
      String identity = getFndUser();

      if (showInMulti && (headlay.getLayoutMode()== headlay.SINGLE_LAYOUT))
      {
         headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
         showInMulti = false;
      }


      //
      // Field visibility depending on layout
      //

      if (headlay.isFindLayout())
      {
         headlay.setSimple("REDLINE_STATUS_DES");
         headlay.setSimple("FILE_STATUS_DES");
      }

      // Bug Id 90011, start
      //if (headlay.isMultirowLayout())
      //{
      //   mgr.getASPField("REV_NO").unsetHidden();
      //}
      // Bug Id 90011, end

      //Bug Id 80759, start
      if (headlay.isEditLayout()) 
      {
         if (headset.getValue("USED_IN_PACKAGE") != null) 
         {
             mgr.getASPField("DOC_STATUS").setReadOnly();
         }
      }
      //Bug Id 80759, end

      //
      // When headset has no rows
      //

      if (headset.countRows() <=0)
      {
         headbar.disableCommand("startApproval");
         headbar.disableCommand("cancelApproval");
         headbar.disableCommand("setApproved");
         headbar.disableCommand("releaseDocument");
         headbar.disableCommand("setObsolete");
         headbar.disableCommand("setUserAccess");
         headbar.disableCommand("setGroupAccess");
         headbar.disableCommand("setAllAccess");
         headbar.disableCommand("copyApprovalTemplate");
         headbar.disableCommand("viewOriginal");
         headbar.disableCommand("viewOriginalWithExternalViewer");
         headbar.disableCommand("viewCopy");
         headbar.disableCommand("editDocument");
         headbar.disableCommand("checkInSelectDocument");
         headbar.disableCommand("printDocument");
         headbar.disableCommand("checkInDocument");
         headbar.disableCommand("undoCheckOut");
         headbar.disableCommand("setAsDocFileTemp");
         headbar.disableCommand("setToCopyFile");
         headbar.disableCommand("deleteDocument");
         headbar.disableCommand("deleteDocumentFile");
         headbar.disableCommand("deleteSelectDocFile");
         headbar.disableCommand("downloadDocuments");
         headbar.disableCommand("createNewRevision");
         headbar.disableCommand("distributeDocuments");
         headbar.disableCommand("userSettings");
         headbar.disableCommand("goToBriefcase");
         headbar.disableCommand("addToBriefcase");
         headbar.disableCommand("connectToTrans");
         

         headset.clear();
         if (addingToBc)
         {
            mgr.getASPField("REV_NO").setHidden();
         }
         headlay.setLayoutMode(headlay.FIND_LAYOUT);
      }


      //
      // Things to adjust on every run of the page
      //

      headbar.removeCustomCommand("activateTitle");
      headbar.removeCustomCommand("activateOriginals");
      headbar.removeCustomCommand("activateObjects");
      headbar.removeCustomCommand("activateConsistsOf");
      headbar.removeCustomCommand("activateWhereUsed");
      headbar.removeCustomCommand("activateRouting");
      headbar.removeCustomCommand("activateAccess");
      headbar.removeCustomCommand("activateHistory");
      headbar.removeCustomCommand("activateFileReferences");
      headbar.removeCustomCommand("activateTransmittals");
      


      if (headlay.isMultirowLayout())
      {
         headbar.disableCommand(headbar.EDITROW);
         headbar.enableCustomCommand("edit");
      }
      else if (headlay.isSingleLayout())
      {
         if (headset.countRows()>0)
         {
            if (headblk.getFuncFieldsNonSelect())
               setFuncFieldValue(headblk);
            //Bug 53039, Start
            if (dAll.equals(headset.getRow().getValue("ACCESS_CONTROL")))
               bDoCheckForAllAccess = true;
            //else
            //   bDoCheckForAllAccess = false;
            //Bug 53039, End
            //Bug Id 56725, start

            if ("TRUE".equals(isAccessOwner())||headset.getValue("GETEDITACCESS").equals("TRUE"))
            {
               itembar6.enableCommand(itembar6.EDITROW);
            }
            else
            {
               itembar6.disableCommand(itembar6.EDITROW);
            } 
            //Bug Id 56725, End
	    //Bug Id 74985, Start
	    if (itemlay5.isMultirowLayout()) 
	    {
               itembar5.enableCustomCommand("editITEM5");
	       itembar5.disableCommand(itembar5.EDITROW);
	    }
	    else if (itemlay5.isSingleLayout())
	    {
                itembar5.disableCustomCommand("editITEM5");
		if (itemset5.getValue("ITEM5_EDIT_ENABLE").equals("TRUE")) 
		   itembar5.enableCommand(itembar5.EDITROW);
		else
		    itembar5.disableCommand(itembar5.EDITROW);
	    }
	    //Bug Id 74985, End
            if (headset.getValue("GETEDITACCESS").equals("TRUE"))
            {
               headbar.enableCommand(headbar.EDITROW);
               itembar1.enableCommand(itembar1.EDITROW);
               //itembar6.enableCommand(itembar6.EDITROW);
            }
            else
            {
               headbar.disableCommand(headbar.EDITROW);
               itembar1.disableCommand(itembar1.EDITROW);
               //itembar6.disableCommand(itembar6.EDITROW);
            }
            if (headset.getValue("GETVIEWACCES").equals("TRUE"))
            {
               itembar2.enableCommand(itembar2.EDITROW);
            }
            else
            {
               itembar2.disableCommand(itembar2.EDITROW);
            }
         }
         headbar.disableCustomCommand("edit");
      }

      if ((headlay.isSingleLayout())&&(headset.countRows()>0))
      {
         if ((sPrelimin.equals(headset.getValue("STATE"))) || (sAppInProg.equals(headset.getValue("STATE"))))
         {
            itembar5.enableCommand(itembar5.NEWROW);
            itembar5.enableCommand(itembar5.DUPLICATEROW);
         }
         else
         {
            itembar5.disableCommand(itembar5.NEWROW);
            itembar5.disableCommand(itembar5.DUPLICATEROW);
         }
      }

      if (headlay.isNewLayout() || headlay.isFindLayout())
         mgr.getASPField("TECHOBJ").setReadOnly();

      if (headset.countRows() == 0)
      {
         // If no data in the master then remove unwanted buttons
         if (( headlay.getLayoutMode() == 0 )|| ( headlay.isSingleLayout() ))
         {
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.FORWARD);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.BACK);
         }
      }


      if (headlay.getLayoutMode() == ASPBlockLayout.SINGLE_LAYOUT || headlay.getLayoutMode() == ASPBlockLayout.NONE)
      {
         if (headset.countRows() == 0)
         {
            headlay.setLayoutMode(headlay.NONE);
            headbar.disableCommand(headbar.OKFIND);
            headbar.disableCommand(headbar.COUNTFIND);
            headbar.disableCommand(headbar.SAVERETURN);
            headbar.disableCommand(headbar.SAVENEW);
            headbar.disableCommand(headbar.CANCELNEW);
            headbar.disableCommand(headbar.CANCELEDIT);
            headbar.disableCommand(headbar.BACK);
         }
         else
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }
      if (tabs.getActiveTab()== 8)
      {
         if (headset.countRows() > 0) //Bug Id 71698
	    checkEnableHistoryNewRow();
      }

      if ("FALSE".equals(enableHistoryNewButton))
      {
         itembar7.disableCommand(itembar7.NEWROW);
      }

      //disable RMB for non-IE browsers
      if (!mgr.isExplorer())
      {
         headbar.disableCustomCommand("setToCopyFile");
         headbar.disableCustomCommand("userSettings");
      }

      if (!isUserSettingsEnable())
         headbar.disableCustomCommand("userSettings");

      //Bug Id 67105, Start
      if (!isMandatorySettingsEnable())
	      headbar.disableCustomCommand("mandatorySettings");
      //Bug Id 67105, End

      // Cannot change doc revision text after released or obsolete.
      if (headlay.isEditLayout())
      {
         if ((sReleased.equals( headset.getValue("STATE"))) || (sObsolete.equals(headset.getValue("STATE"))))
            mgr.getASPField("DOC_REV_TEXT").setReadOnly();

         if ((sReleased.equals( headset.getValue("STATE"))) || (sObsolete.equals(headset.getValue("STATE"))) || (sApproved.equals(headset.getValue("STATE"))))
            mgr.getASPField("NEXT_DOC_SHEET").setReadOnly();
         else if ((sAppInProg.equals(headset.getValue("STATE"))) && ("FALSE".equals(headset.getValue("APPROVAL_UPDATE"))))
            mgr.getASPField("NEXT_DOC_SHEET").setReadOnly();

      }
      // Cannot change approval routing notes when the step is approved
      if (itemlay5.isEditLayout())
      {
          //Bug Id 74985, Start
          if (headset.getValue("GETEDITACCESS").equals("FALSE"))	                 
	  {
            mgr.getASPField("ITEM5_SECURITY_CHECKPOINT_REQ").setReadOnly();                            
            mgr.getASPField("DESCRIPTION").setReadOnly();
	    mgr.getASPField("GROUP_ID").setReadOnly();
            mgr.getASPField("PERSON_ID").setReadOnly();
	  }
	  if ("FALSE".equals(itemset5.getValue("ITEM5_EDIT_ENABLE")))
	      mgr.getASPField("NOTE").setReadOnly();
	  //Bug Id 74985, End

         //Bug Id 73162 Start
         if (!mgr.isEmpty(itemset5.getValue("APPROVAL_STATUS")))
         {
            mgr.getASPField("ITEM5_SECURITY_CHECKPOINT_REQ").setReadOnly();                            
            mgr.getASPField("GROUP_ID").setReadOnly();
            mgr.getASPField("PERSON_ID").setReadOnly();
         }
         //Bug Id 73162 End
      }

      if (headlay.isSingleLayout())
         headset.unselectRows();

      // disbale Find button if this is the "Explore Structure" mode
      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE")))
      {
         adjustExploreStructure();
      }
   }



   protected void adjustExploreStructure() throws FndException
   {
      ASPManager mgr = getASPManager();

      // disable find button to prevent frames from
      // being "out-of-synch"
      headbar.disableCommand(ASPCommandBar.FIND);

      int count = headset.countRows();
      int current_row = headset.getCurrentRowNo();

      appendDirtyJavaScript("var single_layout = " + headlay.isSingleLayout() + ";\n");
      appendDirtyJavaScript("var arrDocNo = new Array(" + count + ");\n");
      headset.first();
      for (int x = 0; x < count; x++)
      {
         appendDirtyJavaScript("arrDocNo[" + x + "] = \"" + mgr.encodeStringForJavascript(headset.getValue("DOC_NO")) + "\";\n");
         headset.next();
      }
      headset.goTo(current_row);
   }



   public String  tabsFinish()
   {
      return tabs.showTabsFinish();
   }


   private void drawPageHeader(AutoString out)
   {
      ASPManager mgr = getASPManager();

      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE")))
      {
         // In this mode, the header is not reaquired..
      }
      else
      {
         if (headlay.isMultirowLayout())
            out.append(mgr.startPresentation("DOCMAWDOCISSUEOVERVIEWREVISIONS: Overview - Documents"));
         else
            out.append(mgr.startPresentation("DOCMAWDOCISSUEDOCUMENTINFO: Document Info"));
      }
   }


   private void drawPageFooter(AutoString out)
   {
      ASPManager mgr = getASPManager();

      if ("EXPLORE_STRUCTURE".equals(ctx.readValue("MODE")))
      {
         //disableFooter();
      }

      out.append(mgr.endPresentation());
   }


   protected String getDescription()
   {
      return "DOCMAWDOCISSUEDLGTITLE: Select Approval Template to Copy";
   }


   protected String getTitle()
   {
      if (headlay.isMultirowLayout())
         return "DOCMAWDOCISSUEOVERVIEWREVISIONS: Overview - Documents";
      else
         return "DOCMAWDOCISSUEDOCUMENTINFO: Document Info";

   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");

      if (bCopyProfile)
      {
         out.append(mgr.generateHeadTag("DOCMAWDOCISSUEDLGTITLE: Select Approval Template to Copy"));
      }
      else
      {
         if (headlay.isMultirowLayout())
            out.append(mgr.generateHeadTag("DOCMAWDOCISSUEOVERVIEWREVISIONS: Overview - Documents"));
         else
            out.append(mgr.generateHeadTag("DOCMAWDOCISSUEDOCUMENTINFO: Document Info"));
      }

      out.append("</head>\n");
      out.append("<body " + mgr.generateBodyTag());
      out.append("><form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"RETURN_OBJ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRM\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"TEMP_LU_NAME\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"TEMP_KEY_REF\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"OBJECT_INSERTED\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"MODE_CHANGED\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"REFRESH_PARENT\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"REDLINE_APP\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"REDLINE_EDIT\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"MULTIROWACTION\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"BRIEFCASE_SELECTED\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"TRANSMITTAL_SELECTED\" value=\"FALSE\">\n");
      
      out.append("<input type=\"hidden\" name=\"BRIEFCASENO\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"TRANSMITTAL_ID_SELECTED\" value=\"\">\n");
      
      out.append("<input type=\"hidden\" name=\"SAME_ACTION_TO_ALL\" value=\"NO\">\n");
      out.append("<input type=\"hidden\" name=\"CPY_TO_DIR_PATH\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"LEAVE_ROWS_SELECTED\" value=\"NO\">\n");
      out.append("<input type=\"hidden\" name=\"DOC_CLASS_FROM_WIZ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DOC_NO_FROM_WIZ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DOC_SHEET_FROM_WIZ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DOC_REV_FROM_WIZ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"MULTIROW_EDIT\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"LAUNCH_FILE\" value=\"YES\">\n");
      out.append("<input type=\"hidden\" name=\"CHANGE_CURRENT_DOCUMENT\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"PARENT_DOC_CLASS\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PARENT_DOC_NO\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PARENT_DOC_SHEET\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PARENT_DOC_REV\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CURRENT_DOC_CLASS\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CURRENT_DOC_NO\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CURRENT_DOC_SHEET\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CURRENT_DOC_REV\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DROPED_FILES_LIST\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DROPED_FILES_PATH\" value=\"\">\n");
      //Bug 70808, Start
      out.append("<input type=\"hidden\" name=\"BGJ_CONFIRMED\" value=\"\">\n");  
      out.append("<input type=\"hidden\" name=\"BGJ_DIALOG_POPPED\" value=\"\">\n"); 
      //Bug 70808, End

      if (bCopyProfile)
      {
         out.append(mgr.startPresentation("DOCMAWDOCISSUEDLGTITLE: Select Approval Template to Copy"));
         out.append(itemlay8.show());
      }
      else
      {
         drawPageHeader(out);

         if (headlay.isVisible())
         {
            if (headset.countRows() > 0 && headlay.isMultirowLayout())
            {

               //
               // Generate html for displaying additional objects (menu bar, row selection, radio buttons)
               // that are shown only in the multirow layout
               //

               // Custom menu bar for performing multirow actions

               // Radio buttons for selecting the way file operations should work
               out.append("<table border=0 cellspacing=0 cols=7>\n");
               out.append("<tr><td>&nbsp;&nbsp;</td><td colspan=\"1\" width=\"100%\">\n<table border=0 cellspacing=0 cellpadding=2 cols=2 width=\"65%\">\n");
               out.append("<tr><td colspan=\"1\" nowrap>");
               out.append(fmt.drawRadio("DOCMAWDOCISSUEAPPLYSAMETOALL: Execute same action for all", "sameActionRadio", "APPLY_SAME_TO_ALL", false, "onclick=\"javascript:setSameAction(this)\""));
               out.append("</td><td colspan=\"1\" nowrap>");
               out.append(fmt.drawRadio("DOCMAWDOCISSUELAUNCHTHEFILE: Launch Files    (for View and Check Out)", "launchFileRadio", "LAUNCH_FILE", true, "onclick=\"javascript:setLuanch(this)\""));
               out.append("</td></tr><tr><td colspan=\"1\" nowrap>\n");
               out.append(fmt.drawRadio("DOCMAWDOCISSUEAPPLYDIFFTOEACH: Choose action for each document", "sameActionRadio", "APPLY_DIFF_TO_EACH", true, "onclick=\"javascript:setSameAction(this)\""));
               out.append("</td><td colspan=\"1\" nowrap>");
               out.append(fmt.drawRadio("DOCMAWDOCISSUEDONTLAUNCHTHEFILE: Don't Launch", "launchFileRadio", "DONT_LAUNCH_FILES", false, "onclick=\"javascript:setLuanch(this)\""));
               out.append("</td></tr></table>\n");
               out.append("</td></tr></table>\n");
            }
            else if (headlay.isSingleLayout())
            {
               if (mgr.isEmpty(headset.getRow().getValue("SEDMSTATUS")) && mgr.isExplorer())
               {

                  
                  // ===== Client script =====

                  out.append("<script type=\"text/JavaScript\" for=\"DropArea\" event=\"DocmanDragDrop()\">\n");

                  String unicode_msg = mgr.translate("DOCMAWDOCISSUEUNICODECHARS: The Drag and Drop Area does not support adding files with Unicode characters and any such file will be excluded. Do you want to continue?");
                  
                  // Bug Id 92125, Start
                  out.append("function document.form.DropArea::OLEDragDrop(data, effect, button, shift, x, y){\n");
                  out.append("   var filesDropped = \"\";\n");
                  out.append("   var path = \"\";\n");
                  out.append("   var fullFileName;\n");
                  out.append("   var foundUnicodeFiles = false;\n");
                  out.append("   var count = 0;\n");
                  out.append("   var bShowFileExtError = false;\n");
                  out.append("   if(data.GetFormat(15)){\n");
                  out.append("      var e = new Enumerator(data.Files);\n");
                  out.append("      while(!e.atEnd()){\n");
                  out.append("         count += 1;\n");
                  out.append("         fullFileName =  \"\" + e.item(); \n");
                  out.append("         if (fullFileName.indexOf(\"?\") != -1)\n");
                  out.append("            foundUnicodeFiles = true;\n");
                  out.append("         else\n");
                  out.append("            filesDropped += fullFileName.substr(fullFileName.lastIndexOf(\"\\\\\")+1) + \"|\"; \n");
                  out.append("         if((fullFileName.substr(fullFileName.lastIndexOf(\"\\\\\")+1)).lastIndexOf(\".\") === -1)\n");
                  out.append("            bShowFileExtError = true;\n");
                  out.append("         if (path == \"\")\n");
                  out.append("            path = fullFileName.substr(0,fullFileName.lastIndexOf(\"\\\\\")); \n");
                  out.append("         e.moveNext();\n");
                  out.append("      }\n");
                  out.append("   }\n");
                  out.append("   document.form.DropArea.Backcolor = oldBackColor;\n");
                  out.append("	if (bShowFileExtError==true && count===1)\n");
                  out.append("   {\n");
                  out.append("      alert(\"");
                  out.append(mgr.translate("DOCMAWDOCISSUENOEXTALERT: File(s) without extension(s) are not allowed."));
                  out.append("\");\n");
                  out.append("      return;\n");
                  out.append("   }\n");   
                  out.append("   filesDropped = filesDropped.substr(0,filesDropped.length-1);\n"); //remove last '|'                  
                  out.append("   if (foundUnicodeFiles)\n");
                  out.append("   {\n");
                  out.append("      if (!confirm(\"" + unicode_msg + "\"))\n");
                  out.append("         return;\n");
                  out.append("   }\n");
                  out.append("   if (filesDropped != \"\")\n");
                  out.append("   {\n");
                  out.append("       document.form.DROPED_FILES_LIST.value=filesDropped;\n");
                  out.append("       document.form.DROPED_FILES_PATH.value=path;\n");
                  out.append("       submit();\n");
                  out.append("   }\n");
                  out.append("}\n");
                  // Bug Id 92125, End

                  out.append(DocmawUtil.writeOleDragOverFunction(mgr.translate("DOCMAWDOCISSUEDROPOBJECTDROPHERE: Drop one file here to check in or many files to import"),
                                                                 mgr.translate("DOCMAWDOCISSUEDROPAREAACCEPTEDMSG: Files to drop"),
                                                                 mgr.translate("DOCMAWDOCISSUEDROPAREAILLEGALMSG: Only files are accepted")));                                                                 

                  out.append("</script>  \n");
                  // Add drop area here

						out.append("   <table width=300 height=25 border=\"0\">\n");
						out.append("     <tr>\n");
						out.append("       <td>\n");
						out.append("        &nbsp;&nbsp;");
						out.append(DocmawUtil.drawDnDArea("", ""));
						out.append("       </td>\n");
						out.append("     </tr>");
						out.append("   </table>\n");

						// End of drop area
                  
					} 

            }
            //Bug Id 67105, Start
	    if (headlay.isFindLayout() && !mgr.isEmpty(sMandatoryFields)) 
	    {
		if (bMandatoryFieldsEmpty) 
		{
			out.append("<TABLE cellspacing=0 cellpadding=0 border=0 width=100%>\n");
			out.append("<TR>\n");
                        out.append("<td>&nbsp;&nbsp;</td>\n");
			out.append("<td width=100%>\n");
			out.append("<TABLE id=cntMAIN bgcolor=#FFCC66 height=10 cellSpacing=0 cellPadding=0  width=100% border=0> \n");
			out.append("<TBODY>\n");
			out.append("<TR>\n");
			out.append("<TD width=100% height=30>&nbsp;&nbsp;\n");
			out.append("<FONT class=normalTextLabel>\n");
			out.append(mgr.translate("DOCMAWDOCISSUEQUERYREQ: This page requires one of the following fields to be filled in before searching: "));//Bug Id 77651
			out.append(sMandatoryFields);
			out.append("<BR>&nbsp;&nbsp;\n");
			out.append(mgr.translate("DOCMAWDOCISSUEQUERYINVALIDE: Note: A single % is not enough."));
			out.append("</FONT>\n");
			out.append("</TD>\n");
			out.append("</TR>\n");
			out.append("</table>\n");
			out.append("</TD><td>&nbsp;&nbsp;</td></TR>\n");
			out.append("</TABLE>\n");
		}
		else
		{
			out.append("<TABLE cellspacing=0 cellpadding=0 border=0 width=100%>\n");
			out.append("<TR>\n");
                        out.append("<td>&nbsp;&nbsp;</td>\n");
			out.append("<td width=100%>\n");
			out.append("<TABLE class=pageFormWithoutBottomLine id=cntMAIN height=10 cellSpacing=0 cellPadding=0  width=100% border=0> \n");
			out.append("<TBODY>\n");
			out.append("<TR>\n");
			out.append("<TD width=100% height=30>&nbsp;&nbsp;\n");
			out.append("<FONT class=normalTextLabel>\n");
			out.append(mgr.translate("DOCMAWDOCISSUEQUERYREQ: This page requires one of the following fields to be filled in before searching: "));//Bug Id 77651
			out.append(sMandatoryFields);
			out.append("<BR>&nbsp;&nbsp;\n");
			out.append(mgr.translate("DOCMAWDOCISSUEQUERYINVALIDE: Note: A single % is not enough."));
			out.append("</FONT>\n");
			out.append("</TD>\n");
			out.append("</TR>\n");
			out.append("</table>\n");
			out.append("</TD><td>&nbsp;&nbsp;</td></TR>\n");
			out.append("</TABLE>\n");
		    }
	     }
	     //Bug Id 67105, End
            out.append(headlay.show());
         }

         else
         {
            headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
            out.append(headlay.show());
         }

         if (headset.countRows()>0)
         {
            if (headlay.isSingleLayout()||headlay.isCustomLayout())
            {

               //
               // Generate html for displaying the detailed view of the page
               //

               out.append(tabs.showTabsInit());
               if (tabs.getActiveTab()== 1)
               {
                  out.append(itemlay0.show());
               }
               else if (tabs.getActiveTab()== 2)
               {
                  out.append(itemlay1.show());
               }
               else if (tabs.getActiveTab()== 3)
               {
                  out.append(itemlay2.show());
               }
               else if (tabs.getActiveTab()== 4)
               {
                  out.append(itemlay3.show());
                  if (itemset3.countRows() == 0)
                  {
                     out.append("<ul><li>");
                     out.append(fmt.showCommandLink("previousLevel", mgr.translate("DOCMAWDOCISSUEPREVLEVEL: Previous Level")));
                     out.append("</ul>\n");
                  }
               }
               else if (tabs.getActiveTab()== 5)
               {
                  out.append(itemlay4.show());
               }
               else if (tabs.getActiveTab()== 6)
               {
                  out.append(itemlay5.show());
               }
               else if (tabs.getActiveTab()== 7)
               {
                  out.append(itemlay6.show());
               }
               else if (tabs.getActiveTab()== 8)
               {
                  out.append(itembar7.showBar());
                  out.append("<table cellpadding=\"10\" cellspacing=\"0\" border=\"0\" width=\"");
                  out.append("\">\n");
                  out.append("<tr><td>");
                  out.append(fmt.drawRadio("DOCMAWDOCISSUECHECKEDINOUT: Checked In/Out", "HISTORY_MODE", "1", "1".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
                  out.append("</td><td>");
                  out.append(fmt.drawRadio("DOCMAWDOCISSUEACCESSCB: Access", "HISTORY_MODE" , "2" , "2".equals(sHistoryMode),"OnClick=\"modeChanged()\""));
                  out.append("</td><td>");
                  out.append(fmt.drawRadio("DOCMAWDOCISSUEOTHERSCB: Others", "HISTORY_MODE", "3", "5".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
                  out.append("</td></tr><tr><td>");
                  out.append(fmt.drawRadio("DOCMAWDOCISSUEAPPROUTING: Approval Routing", "HISTORY_MODE", "4", "4".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
                  out.append("</td><td>");
                  out.append(fmt.drawRadio("DOCMAWDOCISSUEINFORMATION: Information", "HISTORY_MODE", "5", "5".equals(sHistoryMode),"OnClick=\"modeChanged()\""));
                  out.append("</td><td>");
                  out.append(fmt.drawRadio("DOCMAWDOCISSUESTRUCTURE: Structure", "HISTORY_MODE", "6", "6".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
                  out.append("</td><td>");
                  out.append(fmt.drawRadio("DOCMAWDOCISSUEALL: All", "HISTORY_MODE", "7", "7".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
                  out.append("</td></tr>");
                  out.append("</table>\n");
                  out.append(itemlay7.generateDataPresentation());
               }
               else if (tabs.getActiveTab()== 9)
                  out.append(itemlay9.show());
               else if (tabs.getActiveTab()== 10)
                  out.append(itemlay10.show());
               out.append(tabsFinish());
            }
         }
      }


      //
      // CLIENT FUNCTIONS
      //

      if (RedlineApp)
      {
         appendDirtyJavaScript("document.form.REDLINE_APP.value = oCliMgr.GetRedlineViewerApplication();\n");
         appendDirtyJavaScript("document.form.REDLINE_EDIT.value=\"TRUE\"\n");
         appendDirtyJavaScript("submit();\n");
      }

      //Bug 70808, Start
      if (bConfirmBackgroundJob) {
         bConfirmBackgroundJob = false;
         ctx.writeValue("BGJ_DIALOG_POPPED","TRUE");
         appendDirtyJavaScript("displayBGJConfirmWindow();");
      }
      //Bug 70808, End

      if (bConfirm)
      {
         appendDirtyJavaScript("displayConfirmBox();");
         bConfirm=false;
      }


      if (bConfirmEx)
      {
         appendDirtyJavaScript("displayConfirmBoxEx('" + mgr.encodeStringForJavascript(sMessage) + "');");
         bConfirmEx=false;
      }

      if (bConfiramtion4SettingStructureType) {
         appendDirtyJavaScript("displayConfirmBox();");

      }


      appendDirtyJavaScript("last_value = '';\n"); // For Association catogory functionality
      appendDirtyJavaScript("popup_window = '';\n");  //Bug Id 70808

      appendDirtyJavaScript("function validateKeepLastDocRev(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if (getRowStatus_('ITEM2',i)=='QueryMode__') return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("if( !checkKeepLastDocRev(i) ) return;\n");
      appendDirtyJavaScript("    r = __lookup(\n"); //tested
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"common/scripts/Lookup.page");
      appendDirtyJavaScript("?',\n");
      appendDirtyJavaScript("        'DUAL',\n");
      appendDirtyJavaScript("URLClientEncode(");
      appendDirtyJavaScript("        'Always_Last_Doc_Rev_API.Encode('\n");
      appendDirtyJavaScript("        + \"'\" + getValue_('KEEP_LAST_DOC_REV',i) + \"'\"\n");
      appendDirtyJavaScript("        + ') SKEEPCODE'), '', '');\n");
      appendDirtyJavaScript("if (checkStatus_(r,'KEEP_LAST_DOC_REV',i,'Update Revision')){\n");
      appendDirtyJavaScript("    assignValue_('SKEEPCODE',i,0);\n");
      appendDirtyJavaScript("        // Overrride normal validate function\n");
      appendDirtyJavaScript("        // According to the selected value changen readonly state of the doc revision\n");
      appendDirtyJavaScript("        keepcode = __getValidateValue(0);\n"); //tested
      appendDirtyJavaScript("        if( keepcode == 'F')\n");   //we check with db value
      appendDirtyJavaScript("           getField_('ITEM2_DOC_REV',i).readOnly = false;\n");
      appendDirtyJavaScript("        else\n");
      appendDirtyJavaScript("           getField_('ITEM2_DOC_REV',i).readOnly = true;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      //Bug 70808, Start
      appendDirtyJavaScript("function displayBGJConfirmWindow()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    var cookie_value = readCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\");\n");
      appendDirtyJavaScript("    removeCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\", COOKIE_PATH);\n");
      appendDirtyJavaScript("    unconfirm = (readCookie(f.__PAGE_ID.value).length>1);\n");
      appendDirtyJavaScript("    onLoad();\n");
      appendDirtyJavaScript("    if (cookie_value ==\"TRUE\" && (!unconfirm)&&(document.form.CONFIRM.value==\"\"))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("      lov_window = window.open(");
      appendDirtyJavaScript("      \"" + sUrl + "\",");
      appendDirtyJavaScript("      \"anotherWindow\",");
      appendDirtyJavaScript("      \"width=400, height=130, left=200, top=200\");\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       document.form.CONFIRM.value=\"\";\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function submitParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 70808, End

      appendDirtyJavaScript("function displayConfirmBox()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   unconfirm = (readCookie(f.__PAGE_ID.value).length>1);\n");
      appendDirtyJavaScript("   onLoad();\n");
      appendDirtyJavaScript("   if ((!unconfirm)&&(document.form.CONFIRM.value==\"\"))\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (confirm('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sMessage));
      appendDirtyJavaScript("'))\n");
      appendDirtyJavaScript("         document.form.CONFIRM.value='OK';\n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("         document.form.CONFIRM.value='CANCEL';\n");
      appendDirtyJavaScript("      submit();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("      document.form.CONFIRM.value=\"\";\n");
      appendDirtyJavaScript("}\n");

      if (bObjectConnection)
      {
         appendDirtyJavaScript("openObjectConnections();\n");
         appendDirtyJavaScript("function openObjectConnections()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("    openLOVWindow('RETURN_OBJ',-1,\n");
         appendDirtyJavaScript("        '");
         appendDirtyJavaScript(root_path);
         appendDirtyJavaScript("docmaw/ObjectConnection.page?SERVICE_LIST=DocReferenceObject&__DYNAMIC_DEF_KEY=&FROM_DOCISSUE=TRUE'\n"); // Bug ID 65462
         appendDirtyJavaScript("        ,500,500,'insertRecord');\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("function insertRecord()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   ret_val = document.form.RETURN_OBJ.value;\n");
         appendDirtyJavaScript("   seperate_pt = ret_val.indexOf('~');\n");
         appendDirtyJavaScript("   lu_name = ret_val.substr(0,seperate_pt);\n");
         appendDirtyJavaScript("   key_ref = ret_val.substr(seperate_pt+1,ret_val.length-seperate_pt-1);\n");
         appendDirtyJavaScript("   document.form.TEMP_LU_NAME.value = lu_name;\n");
         appendDirtyJavaScript("   document.form.TEMP_KEY_REF.value = key_ref;\n");
         appendDirtyJavaScript("   document.form.OBJECT_INSERTED.value = \"TRUE\";\n");
         appendDirtyJavaScript("   submit();\n");
         appendDirtyJavaScript("}\n");

      }

      appendDirtyJavaScript("function checkPersonId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_('PERSON_ID',i);\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM5',i)!='New__' && !IS_EXPLORER && !checkReadOnly_(fld,'Person Id') ) return false;\n");
      appendDirtyJavaScript("    if( getValue_('GROUP_ID',i)!='' && getValue_('PERSON_ID',i)!='')\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       alert('");
      appendDirtyJavaScript(sPersonError);
      appendDirtyJavaScript("');\n");

      appendDirtyJavaScript("       getField_('PERSON_ID',i).value = '';\n");
      appendDirtyJavaScript("       getField_('ROUTE_SIGN_NAME',i).value = '';\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    fld.value = fld.value.toUpperCase();\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function checkGroupId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_('GROUP_ID',i);\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM5',i)!='New__' && !IS_EXPLORER && !checkReadOnly_(fld,'Group Id') ) return false;\n");
      appendDirtyJavaScript("    if( getValue_('PERSON_ID',i)!='' && getValue_('GROUP_ID',i)!='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       alert('");
      appendDirtyJavaScript(sPersonError);
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("       getField_('GROUP_ID',i).value = '';\n");
      appendDirtyJavaScript("       getField_('GROUP_DESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    fld.value = fld.value.toUpperCase();\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");


      //
      // Access tab read only settings
      //

      appendDirtyJavaScript("function checkItem6GroupId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_('ITEM6_GROUP_ID',i);\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM6',i)!='New__' && !IS_EXPLORER && !checkReadOnly_(fld,'Group Id') ) return false;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM6_PERSON_ID',i)!='' && getValue_('ITEM6_GROUP_ID',i)!='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       alert('");
      appendDirtyJavaScript(sPersonError);
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("       getField_('ITEM6_GROUP_ID',i).value = '';\n");
      appendDirtyJavaScript("       getField_('GROUPDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    fld.value = fld.value.toUpperCase();\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function checkItem6PersonId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_('ITEM6_PERSON_ID',i);\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM6',i)!='New__' && !IS_EXPLORER && !checkReadOnly_(fld,'Person Id') ) return false;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM6_GROUP_ID',i)!='' && getValue_('ITEM6_PERSON_ID',i)!='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       alert('");
      appendDirtyJavaScript(sPersonError);
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("       getField_('ITEM6_PERSON_ID',i).value = '';\n");
      appendDirtyJavaScript("       getField_('PERSONNAME',i).value = '';\n");
      appendDirtyJavaScript("       getField_('PERSONUSERID',i).value = '';\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    fld.value = fld.value.toUpperCase();\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem6PersonId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkItem6PersonId(i) ) return;\n");
      appendDirtyJavaScript("   if( getValue_('ITEM6_PERSON_ID',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("   if( getValue_('ITEM6_GROUP_ID',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("   if( getValue_('ITEM6_PERSON_ID',i)=='' )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      getField_('PERSONNAME',i).value = '';\n");
      appendDirtyJavaScript("      getField_('PERSONUSERID',i).value = '';\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    window.status='Please wait for validation';\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("    APP_ROOT+ 'docmaw/DocIssue.page'+'?VALIDATE=ITEM6_PERSON_ID'\n");
      appendDirtyJavaScript("    + '&ITEM6_PERSON_ID=' + URLClientEncode(getValue_('ITEM6_PERSON_ID',i))\n");
      appendDirtyJavaScript("    + '&ITEM6_GROUP_ID=' + URLClientEncode(getValue_('ITEM6_GROUP_ID',i))\n");
      appendDirtyJavaScript("    );\n");
      appendDirtyJavaScript("    window.status='';\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'ITEM6_PERSON_ID',i,'Person ID') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       assignValue_('PERSONNAME',i,0);\n");
      appendDirtyJavaScript("       assignValue_('PERSONUSERID',i,1);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if(getField_('PERSONUSERID',i).value =='')\n");
      appendDirtyJavaScript("       if(!confirm('"+mgr.translate("DOCMAWDOCISSUENOUSERCONNECTEDPERSON: There is no User Id connected to Person Id")+":'+getField_('ITEM6_PERSON_ID',i).value +'\\n"+mgr.translate("DOCMAWDOCISSUESTILLWANTPROCEED: Still want to proceed")+"?'))\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          getField_('PERSONNAME',i).value = '';\n");
      appendDirtyJavaScript("          getField_('PERSONUSERID',i).value = '';\n");
      appendDirtyJavaScript("          getField_('ITEM6_PERSON_ID',i).value = '';\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function checkAccessOwner(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    access_owner = getField_('ACCESS_OWNER',i);\n");
      appendDirtyJavaScript("    edit_access = getField_('EDIT_ACCESS',i);\n");
      appendDirtyJavaScript("    view_access = getField_('VIEW_ACCESS',i);\n");
      appendDirtyJavaScript("    if(access_owner.checked)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       edit_access.checked = true;\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function checkEditAccess(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    access_owner = getField_('ACCESS_OWNER',i);\n");
      appendDirtyJavaScript("    edit_access = getField_('EDIT_ACCESS',i);\n");
      appendDirtyJavaScript("    view_access = getField_('VIEW_ACCESS',i);\n");
      appendDirtyJavaScript("    if((!edit_access.checked)&&(access_owner.checked))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       edit_access.checked = true;\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if(edit_access.checked)\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function checkViewAccess(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    //alert('view access changed........')\n");
      
      appendDirtyJavaScript("    access_owner = getField_('ACCESS_OWNER',i);\n");
      appendDirtyJavaScript("    edit_access = getField_('EDIT_ACCESS',i);\n");
      appendDirtyJavaScript("    view_access = getField_('VIEW_ACCESS',i);\n");
      appendDirtyJavaScript("    if(getField_('ITEM6_EXIST_IN_APPROVAL',i).value=='TRUE' && !view_access.checked)\n");
      appendDirtyJavaScript("       if(!confirm('"+mgr.translate("DOCMAWDOCISSUEREMVOEPERSONORGROUPINAPP: The person or group exists in approval routing. By removing this person/group you will remove their ability to review the document.")+"'))\n");
      appendDirtyJavaScript("          view_access.checked = true;\n");
      appendDirtyJavaScript("    if((!view_access.checked)&&((access_owner.checked)||(edit_access.checked)))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       view_access.checked = true;\n");
      appendDirtyJavaScript("       return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");


      //
      // Association catogoty settings
      //

      appendDirtyJavaScript("function lovCategory(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    last_value =  getValue_('CATEGORY',i);\n");
      appendDirtyJavaScript("    openLOVWindow('CATEGORY',i,\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(root_path);
      appendDirtyJavaScript("common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_REFERENCE_CATEGORY&__FIELD="+mgr.URLEncode (mgr.translate("DOCMAWDOCISSUECATEGORY: Association Category"))+"'\n");
      appendDirtyJavaScript("        ,500,500,'validateCategory');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function validateCategory(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if ( last_value != '' )\n");
      appendDirtyJavaScript("       getField_('CATEGORY',i).value = last_value+getValue_('CATEGORY',i);\n");
      appendDirtyJavaScript("    last_value = '';\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkCategory(i) ) return;\n");
      appendDirtyJavaScript("}\n");


      //
      // History mode changed
      //

      appendDirtyJavaScript("function modeChanged()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.MODE_CHANGED.value = \"TRUE\";\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n");


      // Tranfer to EdmMacro.page file

      if (bTranferToEDM)
      {
         appendDirtyJavaScript("   var openWindow = true; \n");
         appendDirtyJavaScript("   if (");
         appendDirtyJavaScript(bCommentFileCheckedOut);
         appendDirtyJavaScript(" == true){ \n");
         appendDirtyJavaScript("    if (!confirm('");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.translate("DOCMAWDOCISSUECANNOTEDITDF: Comment file is checked out. Do you wish to continue editing the Original Document?")));
         appendDirtyJavaScript("'))\n");
         appendDirtyJavaScript("    openWindow = false; \n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("    if (openWindow)");
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }
      //DMPR303 START
      if (bTranferToCreateLink)
      {
         appendDirtyJavaScript("   var openWindow = true; \n");
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=400,height=625,left=100,top=100\");\n");
      }
      //DMPR303 START

      appendDirtyJavaScript("function lovTempProfileId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        openLOVWindow('TEMP_PROFILE_ID',i,\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(root_path);
      appendDirtyJavaScript("common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=APPROVAL_PROFILE&__FIELD=Profile+ID'\n");
      appendDirtyJavaScript("        ,500,500,'validateTempProfileId');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateTempProfileId(i)\n");
      appendDirtyJavaScript("{   \n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkTempProfileId(i) ) return;\n");
      appendDirtyJavaScript("    r = __connect(\n"); //tested
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=TEMP_PROFILE_ID'\n");
      appendDirtyJavaScript("        + '&TEMP_PROFILE_ID=' + getValue_('TEMP_PROFILE_ID',i)\n");
      appendDirtyJavaScript("        );\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'TEMP_PROFILE_ID',i,'Profile ID') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        assignValue_('TEMP_DESCRIPTION',i,0);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      if (launchFile)
      {
         appendDirtyJavaScript("   var launchFile = true; \n");
         appendDirtyJavaScript("   if (");
         appendDirtyJavaScript(bCommentFileCheckedOut);
         appendDirtyJavaScript(" == true){ \n");
         appendDirtyJavaScript("    if (!confirm('");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.translate("DOCMAWDOCISSUECANNOTEDITDF: Comment file is checked out. Do you wish to continue editing the Original Document?")));
         appendDirtyJavaScript("'))\n");
         appendDirtyJavaScript("    launchFile = false; \n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("if (launchFile){\n");
         appendDirtyJavaScript(" sDocumentFolder = oCliMgr.GetDocumentFolder();\n");
         appendDirtyJavaScript(sClientFunction);
         appendDirtyJavaScript("}\n");
      }

      appendDirtyJavaScript("function assignValue_(name,i,pos)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if (name == 'DOC_STATUS')\n");
      appendDirtyJavaScript("{   \n");
      appendDirtyJavaScript("    curr = getField_('DOC_STATUS',i).value \n");
      appendDirtyJavaScript("    cutt = '\\%' \n");
      appendDirtyJavaScript("    if ( curr.indexOf(cutt) == -1)\n");
      appendDirtyJavaScript("    fld.value = curr+cutt;\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    fld.value = curr;\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    fld = getField_(name,i);\n");
      appendDirtyJavaScript("    fld.value = fld.defaultValue = __getValidateValue(pos); \n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");


      //
      // Row selection
      //

      appendDirtyJavaScript("function selectAll() {\n");
      appendDirtyJavaScript("  for (i=0; i<f.elements.length; i++) {\n");
      appendDirtyJavaScript("     if (f.elements[i].type == \"checkbox\" && f.elements[i].name == \"__SELECTED1\") {\n");
      appendDirtyJavaScript("        f.elements[i].checked = true;\n");
      appendDirtyJavaScript("        try{\n");
      appendDirtyJavaScript("           CCA(f.elements[i]);\n");
      appendDirtyJavaScript("        }catch(err){}\n");
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function deSelectAll() {\n");
      appendDirtyJavaScript("  for (i=0; i<f.elements.length; i++) {\n");
      appendDirtyJavaScript("     if (f.elements[i].type == \"checkbox\" && f.elements[i].name == \"__SELECTED1\") {\n");
      appendDirtyJavaScript("        f.elements[i].checked = false;\n");
      appendDirtyJavaScript("        try{\n");
      appendDirtyJavaScript("           CCA(f.elements[i]);\n");
      appendDirtyJavaScript("        }catch(err){}\n");
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function invertSelection() {\n");
      appendDirtyJavaScript("  for (i=0; i<f.elements.length; i++) {\n");
      appendDirtyJavaScript("     if (f.elements[i].type == \"checkbox\" && f.elements[i].name == \"__SELECTED1\") {\n");
      appendDirtyJavaScript("        f.elements[i].checked = !(f.elements[i].checked);\n");
      appendDirtyJavaScript("        try{\n");
      appendDirtyJavaScript("           CCA(f.elements[i]);\n");
      appendDirtyJavaScript("        }catch(err){}\n");
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");


      if (headlay.isMultirowLayout() && !bCopyProfile)
      {
         appendDirtyJavaScript("function setSameAction(obj){\n");
         appendDirtyJavaScript("    if (obj.value!=\"APPLY_SAME_TO_ALL\") {\n");
         appendDirtyJavaScript("	 document.form.SAME_ACTION_TO_ALL.value=\"NO\";\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("    else{\n");
         appendDirtyJavaScript("	 document.form.SAME_ACTION_TO_ALL.value=\"YES\";\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("}\n");

         /*appendDirtyJavaScript("  if (document.form.sameActionRadio[0].checked==true)\n");
         appendDirtyJavaScript("       document.form.SAME_ACTION_TO_ALL.value=\"YES\"\n");*/

         appendDirtyJavaScript("function setLuanch(obj){\n");
         appendDirtyJavaScript("    if (obj.value!=\"LAUNCH_FILE\") {\n");
         appendDirtyJavaScript("	 document.form.LAUNCH_FILE.value=\"NO\";\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("    else{\n");
         appendDirtyJavaScript("	 document.form.LAUNCH_FILE.value=\"YES\";\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("}\n");
      }

      // Document Briefcase

      if (bShowBCLov)
      {
         if (mgr.isEmpty(sBcNoFromBriefcase))
            appendDirtyJavaScript("lovBriefcase(-1);");	

      }

      
      if (bShowTransLov)
      {
         appendDirtyJavaScript("lovTransmittal(-1);");

      }

      appendDirtyJavaScript("function lovBriefcase(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        openLOVWindow('BRIEFCASENO',i,\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(root_path);
      appendDirtyJavaScript("common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_BC_LOV1&__FIELD="+mgr.URLEncode (mgr.translate("DOCMAWDOCISSUEBCNO: Briefcase No"))+"&__INIT=1&__AUTO%5FSEARCH=N'\n"); //Bug Id 87331
      appendDirtyJavaScript("        ,500,500,'addDocumentToBriefcase');\n");
      appendDirtyJavaScript("}\n");
      

      appendDirtyJavaScript("function lovTransmittal(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        openLOVWindow('TRANSMITTAL_ID_SELECTED',i,\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(root_path);
      appendDirtyJavaScript("common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOCUMENT_TRANSMITTAL&__FIELD="+mgr.URLEncode (mgr.translate("DOCMAWDOCISSUETRANSID: Transmittal Id"))+"&__INIT=1&__AUTO%5FSEARCH=N'\n");
      appendDirtyJavaScript("        ,500,500,'submitForConnectingTrans');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function addDocumentToBriefcase() {\n");
      appendDirtyJavaScript("   document.form.BRIEFCASE_SELECTED.value = 'TRUE';");
      appendDirtyJavaScript("   submit();");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function submitForConnectingTrans() {\n");
      appendDirtyJavaScript("   document.form.TRANSMITTAL_SELECTED.value = 'TRUE';");
      appendDirtyJavaScript("   submit();");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function multiRowCopyFileTo()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if (document.form.sameActionRadio[0].checked==true)\n");
      appendDirtyJavaScript("     document.form.SAME_ACTION_TO_ALL.value=\"YES\"\n");
      appendDirtyJavaScript("   document.form.MULTIROWACTION.value = 'setToCopyFile();';");
      appendDirtyJavaScript("  submit();\n");
      appendDirtyJavaScript("}   \n");


      appendDirtyJavaScript("function refreshParentRowsSelected()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" document.form.LEAVE_ROWS_SELECTED.value=\"YES\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      if (bOpenWizardWindow)
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrl));

         if (modifySubWindow4NewRev) {
            appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=727, height=612, left=100, top=100\");\n");
         }
         else
         {
            appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=727, height=500, left=100, top=100\");\n");
         }

         
      }

      if (bOpenReleaseWizardWindow)
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrl));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=850, height=450, left=100, top=100\");\n");
      }
      //Bug Id 75677, Start
      if (bOpenDistributionWizardWindow)
      {
         appendDirtyJavaScript("   window.open(\"");
	 appendDirtyJavaScript(sUrl);
	 appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=750, height=450, left=100, top=100\");\n");
      }
      //Bug Id 75677, End
      if (bShowStructure)
      {
         appendDirtyJavaScript("window.parent.location = \"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sUrl));
         appendDirtyJavaScript("\";\n");
      }

      //Bug Id 79174, start
      if (bShowTransmittalReport) 
      {
         appendDirtyJavaScript("   report_window = window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }
      //Bug Id 79174, end

      //Bug Id 81806, start
      if (bOpenTransmittalWizardWindow)
      {
         appendDirtyJavaScript("   window.open(\"");
	 appendDirtyJavaScript(sUrl);
	 appendDirtyJavaScript("\",\"anotherWindow\",\"status, resizable, scrollbars, width=1025, height=520, left=100, top=100\");\n");
      }
      //This javascript function should be called by DocTransmittalWizard.page to load the Transmittal Info page on this DocIssue.page
      appendDirtyJavaScript("function load_Transmittal_Info(url)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   window.open(url,\"_self\",\"status, resizable, scrollbars\");\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 81806, end

      appendDirtyJavaScript("function addNewRow(doc_class,doc_no,doc_sheet,doc_rev)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.DOC_CLASS_FROM_WIZ.value=doc_class\n");
      appendDirtyJavaScript(" document.form.DOC_NO_FROM_WIZ.value=doc_no\n");
      appendDirtyJavaScript(" document.form.DOC_SHEET_FROM_WIZ.value=doc_sheet\n");
      appendDirtyJavaScript(" document.form.DOC_REV_FROM_WIZ.value=doc_rev\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      //here we over ride the client method for lovs
      appendDirtyJavaScript("function lovDocNo(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('DOC_NO',i).indexOf('%') !=-1)? getValue_('DOC_NO',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('DOC_NO',i,\n");
      appendDirtyJavaScript("     '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_TITLE&__FIELD="+mgr.URLEncode(mgr.translate("DOCMAWDOCISSUELISTOFVALUEDOCNO: Doc No"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("     + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_NO',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("     ,550,500,'validateDocNo');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovDocSheet(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('DOC_SHEET',i).indexOf('%') !=-1)? getValue_('DOC_SHEET',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('DOC_SHEET',i,\n");
      appendDirtyJavaScript("   '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_ISSUE_LOV1&__FIELD="+mgr.URLEncode(mgr.translate("DOCMAWDOCISSUELISTOFVALUEDOCSHEET: Doc Sheet"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_SHEET',i))\n");
      appendDirtyJavaScript("   + '&DOC_SHEET=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(getValue_('DOC_NO',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovDocRev(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('DOC_REV',i).indexOf('%') !=-1)? getValue_('DOC_REV',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('DOC_REV',i,\n");
      appendDirtyJavaScript("   '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_ISSUE&__FIELD="+mgr.URLEncode(mgr.translate("DOCMAWDOCISSUELISTOFVALUEDOCREV: Doc Rev"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_REV',i))\n");
      appendDirtyJavaScript("   + '&DOC_REV=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("     + '&DOC_NO=' + URLClientEncode(getValue_('DOC_NO',i))\n");
      appendDirtyJavaScript("     + '&DOC_SHEET=' + URLClientEncode(getValue_('DOC_SHEET',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("}\n");

      //Bug 70553, Start
      appendDirtyJavaScript("function lovFormatSize(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('FORMAT_SIZE',i).indexOf('%') !=-1)? getValue_('FORMAT_SIZE',i):'';\n");
      appendDirtyJavaScript("   var doc_class_ = getValue_('DOC_CLASS',i);\n");
      appendDirtyJavaScript("   if (doc_class_ == \"\" || doc_class_ == null) \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('FORMAT_SIZE',i,\n");
      appendDirtyJavaScript("      '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_FORMAT&__FIELD="+mgr.URLEncode(mgr.translate("DOCMAWDOCISSUELISTOFVALUEFORMAT: Format"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('FORMAT_SIZE',i))\n");
      appendDirtyJavaScript("      + '&FORMAT_SIZE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("      ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('FORMAT_SIZE',i,\n");
      appendDirtyJavaScript("      '" + root_path + "docmaw/FormatLov.page?__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('FORMAT_SIZE',i))\n");
      appendDirtyJavaScript("      + '&FORMAT_SIZE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("      + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("      ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovReasonForIssue(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('REASON_FOR_ISSUE',i).indexOf('%') !=-1)? getValue_('REASON_FOR_ISSUE',i):'';\n");
      appendDirtyJavaScript("   var doc_class_ = getValue_('DOC_CLASS',i);\n");
      appendDirtyJavaScript("   if (doc_class_ == \"\" || doc_class_ == null) \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('REASON_FOR_ISSUE',i,\n");
      appendDirtyJavaScript("      '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOCUMENT_REASON_FOR_ISSUE&__FIELD="+mgr.URLEncode(mgr.translate("DOCMAWDOCISSUEREASONFORISSUE: Reason For Issue"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('REASON_FOR_ISSUE',i))\n");
      appendDirtyJavaScript("      + '&REASON_FOR_ISSUE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("      ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('REASON_FOR_ISSUE',i,\n");
      appendDirtyJavaScript("      '" + root_path + "docmaw/ReasonForIssueLov.page?__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('REASON_FOR_ISSUE',i))\n");
      appendDirtyJavaScript("      + '&REASON_FOR_ISSUE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("      + '&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i))\n");
      appendDirtyJavaScript("      ,550,500,'validateFormatSize');\n");
      appendDirtyJavaScript("   }\n");                
      appendDirtyJavaScript("}\n");
      //Bug 70553, End

      appendDirtyJavaScript("function getPageName()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    return \"docissue\"\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function showMultiMenu(call,selboxnam,tblnr,cond)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var t = eval(\"f.\"+selboxnam);\n");
      appendDirtyJavaScript("   var popup_list =  eval(\"popup_list_\"+tblnr);\n");
      appendDirtyJavaScript("   var rows_selected=\"\";\n");
      appendDirtyJavaScript("   var btn_cmds = \"\";                      \n");
      appendDirtyJavaScript("   if(t)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       if(t.length)\n");
      appendDirtyJavaScript("       {   \n");
      appendDirtyJavaScript("           for(i=0;i<t.length;i++)\n");
      appendDirtyJavaScript("           {\n");
      appendDirtyJavaScript("              if (t[i].checked )\n");
      appendDirtyJavaScript("                 rows_selected += \",\"+(i); \n");
      appendDirtyJavaScript("           }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           if (t.checked)\n");
      appendDirtyJavaScript("              rows_selected = \",0\";\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("	   if (rows_selected==\"\") {\n");
      appendDirtyJavaScript("		   //alert(\"no row selected\");\n");
      appendDirtyJavaScript("		   return;\n");
      appendDirtyJavaScript("	   }\n");
      appendDirtyJavaScript("       var call1 = call.substring(0,call.indexOf('[')+1);\n");
      appendDirtyJavaScript("       var call2 = call.substring(call.indexOf('[')+1,call.indexOf(']'));\n");
      appendDirtyJavaScript("       var call3 = call.substring(call.indexOf(']'),call.length); \n");
      appendDirtyJavaScript("       var lst1 = call2.split(',');\n");
      appendDirtyJavaScript("       var lst2 = rows_selected.split(',');\n");
      appendDirtyJavaScript("       var tf_list = \"\"\n");
      appendDirtyJavaScript("       var list_count = 0;\n");
      appendDirtyJavaScript("       if(lst2.length>1)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           for (l=0;l<popup_list.length;l++)\n");
      appendDirtyJavaScript("           {\n");
      appendDirtyJavaScript("               if(popup_list[l].length>0)\n");
      appendDirtyJavaScript("               {\n");
      appendDirtyJavaScript("                   list_count = popup_list[l].length;\n");
      appendDirtyJavaScript("                   break;\n");
      appendDirtyJavaScript("               }\n");
      appendDirtyJavaScript("           }\n");
      appendDirtyJavaScript("          for (j=5;j<list_count;j++)\n");
      appendDirtyJavaScript("           {\n");
      appendDirtyJavaScript("               if(cond)\n");
      appendDirtyJavaScript("                 tmp_tf = \"true\";\n");
      appendDirtyJavaScript("               else\n");
      appendDirtyJavaScript("                 tmp_tf = \"false\";\n");
      appendDirtyJavaScript("              for(k=1;k<lst2.length;k++)\n");
      appendDirtyJavaScript("              {\n");
      appendDirtyJavaScript("                  if(lst2[k] != '')\n");
      appendDirtyJavaScript("                  {\n");
      appendDirtyJavaScript("                     if(cond)\n");
      appendDirtyJavaScript("                     {\n");
      appendDirtyJavaScript("                        if(popup_list[lst2[k]]!= '')\n");
      appendDirtyJavaScript("                        {\n");
      appendDirtyJavaScript("                           if(popup_list[lst2[k]][j] == false) \n");
      appendDirtyJavaScript("                              tmp_tf = 'false';\n");
      appendDirtyJavaScript("                        }\n");
      appendDirtyJavaScript("                        else\n");
      appendDirtyJavaScript("                            tmp_tf = lst1[j-1];\n");
      appendDirtyJavaScript("                     }\n");
      appendDirtyJavaScript("                     else\n");
      appendDirtyJavaScript("                     {\n");
      appendDirtyJavaScript("                         if(popup_list[lst2[k]]!= '')\n");
      appendDirtyJavaScript("                         {\n");
      appendDirtyJavaScript("                            if(popup_list[lst2[k]][j] == true) \n");
      appendDirtyJavaScript("                              tmp_tf = 'true';\n");
      appendDirtyJavaScript("                         }\n");
      appendDirtyJavaScript("                         else\n");
      appendDirtyJavaScript("                            tmp_tf = lst1[j-1];\n");
      appendDirtyJavaScript("                     }\n");
      appendDirtyJavaScript("                  }\n");
      appendDirtyJavaScript("              }\n");
      appendDirtyJavaScript("              if(lst1[j-1]=='false')\n");
      appendDirtyJavaScript("                 tmp_tf = 'false';\n");
      appendDirtyJavaScript("              tf_list +=   \",\" + tmp_tf;\n");
      appendDirtyJavaScript("           }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       var lst = call2.split(',');\n");
      appendDirtyJavaScript("       for(j=0;j<4;j++)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          if (j<3)\n");
      appendDirtyJavaScript("            btn_cmds += lst[j]+\",\";\n");
      appendDirtyJavaScript("          else\n");
      appendDirtyJavaScript("            btn_cmds += lst[j];\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       if(tf_list.length>0)\n");
      appendDirtyJavaScript("          call2 = btn_cmds+tf_list;\n");
      appendDirtyJavaScript("       call = call1+call2+call3;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   while(call.indexOf('FALSE') != -1)\n");
      appendDirtyJavaScript("      call = call.replace('FALSE','false');\n");
      appendDirtyJavaScript("   eval(call);\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function displayConfirmBoxEx(message)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    var cookie_value = readCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\");\n");
      appendDirtyJavaScript("    removeCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\", COOKIE_PATH);\n");
      appendDirtyJavaScript("    unconfirm = (readCookie(f.__PAGE_ID.value).length>1);\n");
      appendDirtyJavaScript("    onLoad();\n");
      appendDirtyJavaScript("    if (cookie_value ==\"TRUE\" && (!unconfirm)&&(document.form.CONFIRM.value==\"\"))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       if (confirm(message))\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           setHEADCommand('setApproved');\n");
      appendDirtyJavaScript("           document.form.CONFIRM.value='OK';\n");
      appendDirtyJavaScript("           commandSet('HEAD.Perform', '');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           document.form.CONFIRM.value='CANCEL';\n");
      appendDirtyJavaScript("           submit();\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       document.form.CONFIRM.value=\"\";\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function writeConfirmationCookie()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    writeCookie(\"DOC_ISSUE_CONFIRM_APPROVAL\", \"TRUE\", \"\", COOKIE_PATH);\n");
      appendDirtyJavaScript("}\n");

      //Bug Id 57275, Start
      appendDirtyJavaScript("function validateSetApproval()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    document.form.CONFIRM.value=\"\";\n");
      appendDirtyJavaScript("    writeConfirmationCookie();\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 57275, End

      drawPageFooter(out);
      out.append("</p>\n");
      out.append("</form>\n");
      if (mgr.isExplorer())
         out.append(strIFSCliMgrOCX);
      out.append("</body></html>");
      return out;
   }
}
