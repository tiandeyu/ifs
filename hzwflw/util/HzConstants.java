package ifs.hzwflw.util;

public interface HzConstants {

	String CONTEXT_PATH = "/b2e";

	String CURRENT_FND_USER_ID = "_CURRENT_FND_USER_ID_";
	
	String FROM_FLAG = "_WF_PAGE_FROM_FLAG_";
	String FROM_NAVIGATOR = "_NAVIGATOR_PAGE_";
	String FROM_TODO = "_TODO_PAGE_";
	String FROM_DONE = "_DONE_PAGE_";
	String FROM_TRACK = "_TRACK_PAGE_";
	String FROM_SEND_VIEW = "_SEND_VIEW_PAGE_";
	String FROM_SEND_VIEW_FIN = "_SEND_VIEW_FIN_PAGE_";
	String FROM_DELEGATED_WORKBENCH = "_DELEGATED_WORKBENCH_PAGE_";
	String FROM_RETRIEVE_WORKBENCH = "_RETRIEVE_WORKBENCH_PAGE_";
	
	String FROM_PROCESS_DESIGN_PAGE = "_FROM_PROCESS_DESIGN_PAGE_";//

	/**
	 * workbench  -> IntermediatePage  -> biz page
	 */
//	String INTERMEDIATE_PAGE_URL = "/b2e/horizon/workflow/xmlwork.index.jsp";
	String INTERMEDIATE_PAGE_URL = "/b2e/secured/hzwflw/HzIntermediatePage.page";
	String TODOUrl = INTERMEDIATE_PAGE_URL + "?" + FROM_FLAG + "=" + FROM_TODO + "&dbIdentifier=system";
	String DoneUrl = INTERMEDIATE_PAGE_URL + "?" + FROM_FLAG + "=" + FROM_DONE + "&dbIdentifier=system";
	String TrackUrl = INTERMEDIATE_PAGE_URL + "?" + FROM_FLAG + "=" + FROM_TRACK + "&dbIdentifier=system";
	String ToReadUrl = INTERMEDIATE_PAGE_URL + "?" + FROM_FLAG + "=" + FROM_SEND_VIEW + "&dbIdentifier=system";
	String ReadUrl = INTERMEDIATE_PAGE_URL + "?" + FROM_FLAG + "=" + FROM_SEND_VIEW_FIN + "&dbIdentifier=system";
	String DelegatedUrl = INTERMEDIATE_PAGE_URL + "?" + FROM_FLAG + "=" + FROM_DELEGATED_WORKBENCH + "&dbIdentifier=system";
	String RetrieveUrl = INTERMEDIATE_PAGE_URL + "?" + FROM_FLAG + "=" + FROM_RETRIEVE_WORKBENCH + "&dbIdentifier=system";
	
	String PAGE_URL_PROCESS_DESIGN =  INTERMEDIATE_PAGE_URL + "?" + FROM_FLAG + "=" + FROM_PROCESS_DESIGN_PAGE + "";

	String PAGE_URL_PROCESS_VIEW = "/b2e/horizon/workflow/ShowFlowMap.jsp";

	/**
	 * start page
	 */
	String PAGE_URL_START_PROCESS = "/b2e/secured/hzwflw/StartProcess.page";

	/**
	 * inter-page parameters.
	 */
	String PARA_PROCESS_ID = "_PARA_PROCESS_ID_";
	String PARA_OBJID = "OBJID";
	String PARA_FROM_FLAG = "_PARA_FROM_FLAG_";
	String PARA_CURRENT_ITEM_ID_ = "_PARA_CURRENT_ITEM_ID_";
	String PARA_BIZ_LU_NAME = "_PARA_BIZ_LU_NAME_";
	String PARA_BIZ_VIEW_NAME = "_PARA_BIZ_VIEW_NAME";
	String PARA_BIZ_KEY_REF_PAIR = "_PARA_BIZ_KEY_REFPAIR_";
	String PARA_BIZ_OBJID = "_PARA_BIZ_OBJID_";
	String PARA_BIZ_URL = "_PARA_BIZ_URL_";
	String PARA_PROCESS_TOPIC = "_PARA_PROCESS_TOPIC_";
	String PARA_PROCESS_KEY_STR = "_PARA_PROCESS_KEY_STR_";
	String PARA_PROCESS_NAME = "_PARA_PROCESS_NAME_";
	String PARA_SEND_OBJID = "_PARA_SEND_OBJID_";
	String PARA_SEND_OBJVERSION = "_PARA_SEND_OBJVERSION_";

	/**
	 * track status
	 */
	String TRACK_STATUS_TRACKING = "TRACKING";
	String TRACK_STATUS_TRACKED = "TRACKED";

	String FIELD_CONTROL_READ = "READ";
	String FIELD_CONTROL_EDIT = "EDIT";
	String FIELD_CONTROL_CHECK = "MUSTINPUT";
	String FIELD_CONTROL_HIDDEN = "HIDDEN";
	String FIELD_CONTROL_OTHER = "OTHER";
	//
	String ORG_TYPE_ORG = "ORG";
	String ORG_TYPE_DEPT= "DEP";
	
	String PARA_REAL_USER_ID = "_WF_REAL_USER_ID_";
}
