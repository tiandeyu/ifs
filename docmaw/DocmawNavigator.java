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
*  File        : DocmawNavigator.java
*  Created     :
*
*  2002-11-21   DIKALK   Changed item 'Overview - Document Revisions' to point to DocIssue.page
*  2003-04-22   BAKALK   Added a new page :Overview - Document Briefcase Detail.
*  2003-04-23   BAKALK   Changed the page name from "Overview - Document Briefcase Detail" to "Document Briefcase".
*  2003-06-20   DHPELK   Fixed Call 95767
*  2003-09-03   BAKALK   Fixed Call 102202.
*  2003-09-16   THWILK   Fixed Call 103379.
*  2004-01-09   BAKALK   Added a new page :Overview - Document Package Lines.
*  2004-03-23   DIKALK   SP1-Merge. Bug Id 42046. Modified "Classes,Groups and Approval Templates" to "Classes,Groups,Templates".
*  2004-06-09   DIKALK   Removed navigator item "Invoice Scan"
*  2006-01-19   AMNALK   AMPR125 - 6 Events in Docman: Added new page "Logs".
*  2006-01-30   AMNALK   AMPR125 - 7 Events in Docman: Change the view of new page "Logs".
*  2006-01-31   DIKALK   Changed the lable Logs to File Operation Log
*  2006-12-28   BAKALK   Added new subfolder in Basic data: Document Transmittals.
*  2007-01-08   BAKALK   Added new subfolder in Document Management: Document Transmittals and new 3 overivew  pages under it.
*  2007-01-24   BAKALK   Added Transmittal Info.
*  2007-02-08   BAKALK   Added Transmittal Wizard.
*  2007-02-19   BAKALK   Removed Transmittal Overview.
*  2007-03-28   CHSELK   Added Status for Transmittal Lines.
*  2007-07-19   UPDELK   Call ID 146312 Added Transmittal Comment File Overview.
*  2007-07-24   UPDELK   Call Id 146870: DOCMAW/DOCMAN - Transmittal and dynamic dependencies to PROJ.
*  2007-09-25   DINHLK   Changed navigator entries of "Document Transmittal Info" and "Document Transmittal Info".
*  2007-12-12   AMNALK   Bug Id 68742, Changed the navigator names of some pages.
*----------------------------------------------------------------------------
*/



package ifs.docmaw;

import ifs.fnd.asp.*;

public class DocmawNavigator implements Navigator
{
   public ASPNavigatorNode add(ASPManager mgr)
   {
      ASPNavigatorNode nod = mgr.newASPNavigatorNode("DOCMAWDOCMAWNAVIGATORTITLE: Document Management");
            
      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLE: Overview - Document Titles","docmaw/DocTitleOvw.page", "DOC_TITLE" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLERECEIVE: Document Titles Receive","docmaw/DocTitleReceive.page", "DOC_TITLE" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESEND: Document Titles Send","docmaw/DocTitleSend.page", "DOC_TITLE" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLEGRAPH: Document Titles Graph","docmaw/DocTitleGraph.page", "DOC_TITLE" );
      
      
      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCINFO: Document Info","docmaw/DocIssue.page", "DOC_ISSUE_REFERENCE" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORFOLDER: Overview - Document Folders", "docmaw/DocSubjectOvw.page", "DOC_SUBJECT" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCREF: Overview - Object Connections",  "docmaw/DocReferenceObjectOvw.page", "DOC_REFERENCE_OBJECT" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCAPP: Overview - Documents to Approve","docmaw/ApprovalCurrentStepDocApproveOvw.page", "APPROVAL_CURRENT_STEP" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCPKG: Document Package", "docmaw/DocPackageId.page", "DOC_PACKAGE_ID" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCPKGLINE: Overview - Document Package Lines", "docmaw/DocPackageLines.page", "DOC_PACKAGE_TEMPLATE_EXTRA" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCQUERY: Search Document Contents", "docmaw/SearchDocumentContents.page", "DOC_ISSUE" );
      nod.addItem("DOCMAWDOCMAWNAVIGATORFILEIMPORT: File Import", "docmaw/FileImport.page", "DOC_FILE_IMPORT" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORCREATENEWDOCUMENT: Create New Document", "docmaw/CreateNewDocument.page", "DOC_TITLE" );
//      nod.addItem("DOCMAWDOCMAWNAVIGATORDOCBRIEFCASE: Document Briefcase", "docmaw/DocBriefcase.page", "DOC_BRIEFCASE" );

   
//      ASPNavigatorNode a = nod.addNode("DOCMAWDOCMAWNAVIGATORDOCDIST: Document Distribution" );
//      a.addItem("DOCMAWDOCMAWNAVIGATORDOCDISOVW: Overview - Document Distribution", "docmaw/DocDistributionListOvw.page", "DOC_DIST_LIST" );
//      a.addItem("DOCMAWDOCMAWNAVIGATORDOCDISHIST: Overview - Document Distribution History", "docmaw/DocumentDistributionHistory.page", "DOC_CLASS" );
//
//      ASPNavigatorNode ab = nod.addNode("DOCMAWDOCMAWNAVIGATORTRANSMITTAL: Document Transmittals");
//      ab.addItem("DOCMAWDOCMAWNAVIGATORTRANSMITISSUES: Overview - Document Transmittal Document Revisions","docmaw/DocumentTransmittalIssues.page","DOC_TRANSMITTAL_ISSUE"); //Bug Id 68742
//      ab.addItem("DOCMAWDOCMAWNAVIGATORTRANSMITHISTORY: Overview - Document Transmittal History","docmaw/DocumentTransmittalHistory.page","DOC_TRANSMITTAL_HISTORY");//
//      ab.addItem("DOCMAWDOCMAWNAVIGATORTRANINFO: Overview - Document Transmittal Info ", "docmaw/DocTransmittalInfo.page", "DOCUMENT_TRANSMITTAL" ); //Bug Id 68742
//      ab.addItem("DOCMAWDOCMAWNAVIGATORTRANWIZ: Document Transmittal Wizard ", "docmaw/DocTransmittalWizard.page?FROM_NAV=TRUE", "DOCUMENT_TRANSMITTAL" );
//      ab.addItem("DOCMAWDOCMAWNAVIGATOROVWTRANSCMNTFILE: Overview - Document Transmittal Comment Files", "docmaw/TransmittalCommentFile.page", "TRANSMITTAL_COMMENT_FILE" ); //Bug Id 68742
//   

      
      
//      ASPNavigatorNode docLib = nod.addNode("DOCMAWDOCMAWNAVIGATORDOCDOCUMENTLIBRARY: Document Library" ); 
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUERECIEVE: Doc Issue Receive", "docmaw/DocIssueReceive.page", "DOC_TITLE");      
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESEND: Doc Issue Send", "docmaw/DocIssueSend.page", "DOC_TITLE");      
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUEGRAPH: Doc Issue Graph", "docmaw/DocIssueGraph.page", "DOC_TITLE");      
      
      
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLERECEIVEFAX: Doc Title Receive Fax","docmaw/DocTitleReceiveFax.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUERECEIVEFAX: Doc Issue Receive Fax","docmaw/DocIssueReceiveFax.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLERECEIVEMEMO: Doc Title Receive Memo","docmaw/DocTitleReceiveMemo.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUERECEIVEMEMO: Doc Issue Receive Memo","docmaw/DocIssueReceiveMemo.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLERECEIVEMEETING: Doc Title Receive Meeting","docmaw/DocTitleReceiveMeeting.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUERECEIVEMEETING: Doc Issue Receive Meeting","docmaw/DocIssueReceiveMeeting.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESENDFAX: Doc Title Send Fax","docmaw/DocTitleSendFax.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESENDFAX: Doc Issue Send Fax","docmaw/DocIssueSendFax.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESENDMEMO: Doc Title Send Memo","docmaw/DocTitleSendMemo.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESENDMEMO: Doc Issue Send Memo","docmaw/DocIssueSendMemo.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESENDMEETING: Doc Title Send Meeting","docmaw/DocTitleSendMeeting.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESENDMEETING: Doc Issue Send Meeting","docmaw/DocIssueSendMeeting.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLEPROPHASE: Doc Title Prophase","docmaw/DocTitleProphase.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUEPROPHASE: Doc Issue Prophase","docmaw/DocIssueProphase.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLECONSTRUCT: Doc Title Construct","docmaw/DocTitleConstruct.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUECONSTRUCT: Doc Issue Construct","docmaw/DocIssueConstruct.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLEDESIGN: Doc Title Design","docmaw/DocTitleDesign.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUEDESIGN: Doc Issue Design","docmaw/DocIssueDesign.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLEEQUIPMENT: Doc Title Equipment","docmaw/DocTitleEquipment.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUEEQUIPMENT: Doc Issue Equipment","docmaw/DocIssueEquipment.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESTANDARD: Doc Title Standard","docmaw/DocTitleStandard.page", "DOC_ISSUE" );
//      docLib.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESTANDARD: Doc Issue Standard","docmaw/DocIssueStandard.page", "DOC_ISSUE" );
      
      
      
//      ASPNavigatorNode docTrans = nod.addNode("DOCMAWDOCMAWNAVIGATORDOCBIZMANAGEMENT: Document Business" ); 
////      docTrans.addItem("DOCMAWDOCMAWNAVIGATORDOCRECEIVETRANS: Doc Receive Trans Old", "doctrw/DocReceiveTransOld.page", "DOC_TITLE");   
////      docTrans.addItem("DOCMAWDOCMAWNAVIGATORDOCSENDTRANS: Doc Send Trans", "doctrw/DocSendTrans.page", "DOC_TITLE");   
//      docTrans.addItem("DOCMAWDOCMAWNAVIGATORDOCRECEIVETRANS: Doc Receive Trans", "doctrw/DocReceiveTrans.page", "DOC_RECEIVE_TRANS_REFERENCE");   
//      docTrans.addItem("DOCMAWDOCMAWNAVIGATORDOCSENDTRANSFAX: Doc Send Trans Fax", "doctrw/DocSendTransFax.page", "DOC_SEND_TRANS");   
//      docTrans.addItem("DOCMAWDOCMAWNAVIGATORDOCSENDTRANSMEMO: Doc Send Trans Memo", "doctrw/DocSendTransMemo.page", "DOC_SEND_TRANS");   
//      docTrans.addItem("DOCMAWDOCMAWNAVIGATORDOCSENDTRANSMEETING: Doc Send Trans Meeting", "doctrw/DocSendTransMeeting.page", "DOC_SEND_TRANS");
//      docTrans.addItem("DOCMAWDOCMAWNAVIGATORDOCDISTRIBUTION: Document Distribution", "docctw/DocDistributionCtl.page", "DOC_DISTRIBUTION_CTL");
//      docTrans.addItem("DOCMAWDOCMAWNAVIGATORFILEIMPORT: File Import", "docmaw/FileImport.page", "DOC_FILE_IMPORT" );
      
      
//      ASPNavigatorNode contractorLibNode = nod.addNode("DOCMAWDOCMAWNAVIGATORDOCDOCUMENTLIBRARYFORCONTRACTOR: Document Library For Contractor" ); 
//      contractorLibNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLERECEIVEFORCONTRACTOR: Doc Titles Receive For Contractor", "docmaw/DocTitleReceiveForContractor.page", "DOC_TITLE");     
//      contractorLibNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESENDFORCONTRACTOR: Doc Issue Send For Contractor", "docmaw/DocTitleSendForContractor.page", "DOC_TITLE");      
//      contractorLibNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLEGRAPHFORCONTRACTOR: Doc Titles Graph For Contractor", "docmaw/DocTitleGraphForContractor.page", "DOC_TITLE");     
//      contractorLibNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUERECIEVEFORCONTRACTOR: Doc Issue Receive For Contractor", "docmaw/DocIssueReceiveForContractor.page", "DOC_TITLE");      
//      contractorLibNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESENDFORCONTRACTOR: Doc Issue Send For Contractor", "docmaw/DocIssueSendForContractor.page", "DOC_TITLE");      
//      contractorLibNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUEGRAPHFORCONTRACTOR: Doc Issue Graph For Contractor", "docmaw/DocIssueGraphForContractor.page", "DOC_TITLE");      
   
//      ASPNavigatorNode switchCenterNode = nod.addNode("DOCMAWDOCMAWNAVIGATORDOCEXCHANGE: Document Exchange" ); 
////      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCCONTRACTORSEND: Doc Title Contactor Send", "docmaw/DocTitleContractorSend.page", "DOC_TITLE");   
////      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCCONTRACTORSEND: Doc Contactor Send", "docmaw/DocIssueContractorSend.page", "DOC_TITLE");   
////      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCCONTRACTORRECEIVE: Contractor Receive", "docmaw/DocIssueContractorReceive.page", "DOC_TITLE");   
////      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCCONTRACTORRECEIVENAV: Contractor Receive Nav" , "docmaw/DocIssueTobeSignedNav.page", "DOC_TITLE");   
//      
////      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLERECEIVEFAXEXCH: Doc Title Receive Fax Exch","docmaw/DocTitleReceiveFaxExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUERECEIVEFAXEXCH: Exchange Receive Fax","docmaw/DocIssueReceiveFaxExch.page", "DOC_TITLE" );
////      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLERECEIVEMEMOEXCH: Doc Title Receive Memo Exch","docmaw/DocTitleReceiveMemoExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUERECEIVEMEMOEXCH: Exchange Receive Memo","docmaw/DocIssueReceiveMemoExch.page", "DOC_TITLE" );
////      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLERECEIVEMEETINGEXCH: Doc Title Receive Meeting Exch","docmaw/DocTitleReceiveMeetingExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUERECEIVEMEETINGEXCH: Exchange Receive Meeting","docmaw/DocIssueReceiveMeetingExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESENDFAXEXCH: Doc Title Send Fax Exch","docmaw/DocTitleSendFaxExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESENDFAXEXCH: Exchange Send Fax","docmaw/DocIssueSendFaxExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESENDMEMOEXCH: Doc Title Send Memo Exch","docmaw/DocTitleSendMemoExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESENDMEMOEXCH: Exchange Send Memo","docmaw/DocIssueSendMemoExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESENDMEETINGEXCH: Doc Title Send Meeting Exch","docmaw/DocTitleSendMeetingExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESENDMEETINGEXCH: Exchange Send Meeting","docmaw/DocIssueSendMeetingExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLEPROPHASEEXCH: Doc Title Prophase Exch","docmaw/DocTitleProphaseExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUEPROPHASEEXCH: Exchange Prophase","docmaw/DocIssueProphaseExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLECONSTRUCTEXCH: Doc Title Construct Exch","docmaw/DocTitleConstructExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUECONSTRUCTEXCH: Exchange Construct","docmaw/DocIssueConstructExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLEDESIGNEXCH: Doc Title Design Exch","docmaw/DocTitleDesignExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUEDESIGNEXCH: Exchange Design","docmaw/DocIssueDesignExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLEEQUIPMENTEXCH: Doc Title Equipment Exch","docmaw/DocTitleEquipmentExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUEEQUIPMENTEXCH: Exchange Equipment","docmaw/DocIssueEquipmentExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCTITLESTANDARDEXCH: Doc Title Standard Exch","docmaw/DocTitleStandardExch.page", "DOC_TITLE" );
//      switchCenterNode.addItem("DOCMAWDOCMAWNAVIGATORDOCISSUESTANDARDEXCH: Exchange Standard","docmaw/DocIssueStandardExch.page", "DOC_TITLE" );
      
      
      
//      ASPNavigatorNode queryCenter = nod.addNode("DOCMAWDOCMAWNAVIGATORQUERYCENTER: Query Center" ); 
//      queryCenter.addItem("DOCMAWDOCMAWNAVIGATORFULLTEXTSEARCH: Fulltext Search", "fultxw/IfsFulltextSearchPage.page");
//      queryCenter.addItem("DOCMAWDOCMAWNAVIGATORDOCSTRUCTURETREEFRAME: Document Tree Query", "docmaw/DocStructureTreeFrame.page");
//      
//      ASPNavigatorNode fulltextNode = queryCenter.addNode("DOCMAWDOCMAWNAVIGATORFULLTEXT: Fulltext" ); 
//      fulltextNode.addItem("DOCMAWDOCMAWNAVIGATORIFSFULLTEXTCONFIG: Fulltext Config", "fultxw/IfsFulltextConfig.page","IFS_FULLTEXT_CONFIG");
//      fulltextNode.addItem("DOCMAWDOCMAWNAVIGATORIFSFTXCOLUMNTEMPLATE: Fulltext Column Template ", "fultxw/IfsFtxColumnTemplate.page","IFS_FTX_COLUMN_TEMPLATE");
//      fulltextNode.addItem("DOCMAWDOCMAWNAVIGATORFULLTEXTDOCCLASS: Fulltext Column Config", "fultxw/IfsFulltextDocClass.page","IFS_FULLTEXT_DOC_CLASS");
//      fulltextNode.addItem("DOCMAWDOCMAWNAVIGATORFULLTEXTINDEXINGPAGE: Fulltext Indexing", "fultxw/IfsFulltextIndexingPage.page","IFS_FULLTEXT_CONFIG");
      
//      ASPNavigatorNode goldgridNode = nod.addNode("DOCMAWDOCMAWNAVIGATORWORDTEMPLATEMANAGEMENT: Word Template Management");
//        
//      goldgridNode.addItem("DOCMAWDOCMAWNAVIGATORGOLDSIGNATURE: Signatue Management", "wordmw/GoldSignature.page","GD_SIGNATURE_VIEW");
//      goldgridNode.addItem("DOCMAWDOCMAWNAVIGATORBOOKMARKS: Bookmarks Management", "wordmw/Bookmarks.page","BOOKMARKS_VIEW");
//      goldgridNode.addItem("DOCMAWDOCMAWNAVIGATORBUSINESSTEMPLATE: Business Template", "wordmw/BusinessTemplate.page","BUSINESS_TEMPLATE");
//      goldgridNode.addItem("DOCMAWDOCMAWNAVIGATORTEMPLATEFILE: Template File", "wordmw/TemplateFile.page","TEMPLATE_FILE_VIEW");
//      goldgridNode.addItem("DOCMAWDOCMAWNAVIGATORUSBKEYPASSWORD: Usbkey Password", "wordmw/UsbkeyPassword.page","USBKEY_PASSWORD");
      
      ASPNavigatorNode b = nod.addNode("DOCMAWDOCMAWNAVIGATORDOCBASIC: Basic Data" );
      if (mgr.isExplorer())
      {
         b.addItem("DOCMAWDOCMAWNAVIGATORUSER_SETTING: User Settings", "docmaw/dlgUserSettings.page", "DOC_CLASS");
      }     
   
//      b.addItem("DOCMAWDOCMAWNAVIGATORDOCUMENTLOG: File Operation Log", "docmaw/DocLog.page", "EDM_FILE_OP_ANNOUNCE" );
      b.addItem("DOCMAWDOCMAWNAVIGATORDOCCLASS: Classes, Groups, Templates", "docmaw/DocumentBasicTempl.page", "DOC_CLASS" );
      b.addItem("DOCMAWDOCMAWNAVIGATORDOCMILESTONEPROFILE: Various Settings", "docmaw/DocumentBasicVarSet.page", "DOC_MILESTONE_PROFILE" );
//      b.addItem("DOCMAWDOCMAWNAVIGATORDOCEXVARIOUSSETTING: Extended Various Settings", "docmaw/DocumentBasicVarSet2.page", "DOC_SCALE" );
      b.addItem("DOCMAWDOCMAWNAVIGATOREDM_BASIC: EDM Basic", "docmaw/EDMBasic.page", "DOC_CLASS");
//      b.addItem("DOCMAWDOCMAWNAVIGATORMACRO_BASIC: Macro Basic", "docmaw/MacroBasic.page", "EDM_MACRO");
      b.addItem("DOCMAWDOCMAWNAVIGATORCLASSMNG: Document Class Management", "docmaw/DocumentClassBasic.page", "DOC_CLASS" );
      b.addItem("DOCMAWDOCMAWNAVIGATORDOCCLASSPROCESSACTION: Document Class Process Action", "docmaw/DocClassProcessAction.page", "DOC_CLASS_PROC_ACTION_HEAD");
//      b.addItem("DOCMAWDOCMAWNAVIGATORDOCCONTACTUNIT: Document Contact Unit", "docmaw/DocContactUnit.page", "DOC_CONTACT_UNIT");
//      b.addItem("DOCMAWDOCMAWNAVIGATORGENERALZONE: General Zone", "genbaw/GeneralZone.page", "GENERAL_ZONE");
//      b.addItem("DOCMAWDOCMAWNAVIGATORDOCCOMMUNICATIONSEQ: Document Communication Seqence", "docmaw/DocCommunicationSeq.page", "DOC_COMMUNICATION_SEQ");
            
//      b.addItem("DOCMAWDOCMAWNAVIGATORDOCSTRUCTURETREE: Doc Structure Tree","docmaw/DocStructureTree.page","DOC_STRUCTURE_TREE");//-
//      b.addItem("DOCMAWDOCMAWNAVIGATORDOCASSOCIATIONTYPE: Doc Association Type","docmaw/DocAssociationType.page","DOC_ASSOCIATION_TYPE");//-
//      b.addItem("DOCMAWDOCMAWNAVIGATORDOCPROJDNSEG: Doc_Proj_Dnseg","docmaw/DocProjDnseg.page","DOC_PROJ_DNSEG");//-
      b.addItem("DOCMAWDOCMAWNAVIGATORDOCASSOCIATEBUSINESS: Doc Associate Business","docmaw/DocAssociateBusiness.page","DOC_ASSOCIATE_BUSINESS");//-
      b.addItem("DOCMAWDOCMAWNAVIGATORUSINGLIBARARYORG: Using Libaray Org","docmaw/UsingLibOrg.page","USING_LIB_ORG");//-
      b.addItem("DOCMAWDOCMAWNAVIGATORUSINGDOCBASICDATA: Doc basic Data","docmaw/DocBasicData.page","DOC_BOOKLET,DOC_ASSOCIATION_TYPE,DOC_COMMUNICATION_SEQ,DOC_MEETING_TYPE,DOC_PAGE_SIZE,DOC_SEC_LEVEL,DOC_PROFESSION");//-
      
      /*ASPNavigatorNode t = b.addNode("DOCMAWDOCMAWNAVIGATORTRANSMITTAL: Document Transmittals" );
      
      boolean isProjInstalled = DocmawUtil.isProjInstalled(mgr);
      if (isProjInstalled)
      {
     //Bug Id 68742: Changed the titles   
          t.addItem("DOCMAWDOCMAWNAVIGATORCOUNTERS: Document Transmittal Counters", "docmaw/ProjectTransmittalCounter.page", "TRANSMITTAL_COUNTER" );
          t.addItem("DOCMAWDOCMAWNAVIGATORTRANSCLASS: Document Classes per Project for Document Transmittals", "docmaw/ProjectTransmittalDocClass.page", "PROJECT_DOCUMENT_CLASS" );
          t.addItem("DOCMAWDOCMAWNAVIGATORTRANSRECEIVERS: Document Transmittal Receivers", "docmaw/ProjectTransmittalReceivers.page", "PROJ_TRANSMITTAL_RECEIVER" );
          t.addItem("DOCMAWDOCMAWNAVIGATORLINESTATES: Statuses for Document Transmittal Lines ", "docmaw/TransmittalLineState.page", "TRANSMITTAL_LINE_STATE" );
      }*/
      
      
      return nod;
   }
}