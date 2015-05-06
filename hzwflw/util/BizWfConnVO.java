package ifs.hzwflw.util;


import java.io.Serializable;

public class BizWfConnVO implements Serializable {
	
   private static final long serialVersionUID = 6342317797176213833L;
   private String bizLuName;
	private String bizViewName;
	private String luKeyValuePairs;
	private String bizPagePath;
	private String processId;
	private String processName;
	private String processKey;
	
   public BizWfConnVO(){}
	
	public BizWfConnVO(String bizLuName,String bizViewName, String bizObjId,String bizPagePath,String processId,String processName){
		this.bizLuName = bizLuName;
		this.bizViewName = bizViewName;
		this.bizPagePath = bizPagePath;
		this.luKeyValuePairs = bizObjId;
		this.processId = processId;
		this.processName = processName;
	}
	
	/**
	 * @return the bizLuName
	 */
	public String getBizLuName() {
		return bizLuName;
	}
	/**
	 * @param bizLuName the bizLuName to set
	 */
	public void setBizLuName(String bizLuName) {
		this.bizLuName = bizLuName;
	}
	/**
	 * @return the bizViewName
	 */
	public String getBizViewName() {
		return bizViewName;
	}
	/**
	 * @param bizViewName the bizViewName to set
	 */
	public void setBizViewName(String bizViewName) {
		this.bizViewName = bizViewName;
	}
	/**
	 * @return the bizObjId
	 */
	public String getLuKeyValuePairs() {
		return luKeyValuePairs;
	}
	/**
	 * @param bizObjId the bizObjId to set
	 */
	public void setBizObjId(String bizObjId) {
		this.luKeyValuePairs = bizObjId;
	}
	/**
	 * @return the bizPagePath
	 */
	public String getBizPagePath() {
		return bizPagePath;
	}
	/**
	 * @param bizPagePath the bizPagePath to set
	 */
	public void setBizPagePath(String bizPagePath) {
		this.bizPagePath = bizPagePath;
	}
	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}
	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}
	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
   public String getProcessKey() {
      return processKey;
   }

   public void setProcessKey(String processKey) {
      this.processKey = processKey;
   }

}
