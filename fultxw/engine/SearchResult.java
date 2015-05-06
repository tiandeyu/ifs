package ifs.fultxw.engine;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SearchResult implements Serializable {

   private static final long serialVersionUID = -2290568427870357918L;
	public int pageCount ;	//总页数
	public long castTime ;	//花费的时间
	public int resultCount ;	//结果总数
	public Map<String, Map<String, String>> results ;

	public String toString() {
		StringBuffer buff = new StringBuffer() ;
		buff.append("pageCount is ").append(this.pageCount).append("\t\n") ;
		buff.append("resultCount is ").append(this.resultCount).append("\t\n") ;
		buff.append("castTime is ").append(this.castTime).append("\t\n") ;
//		buff.append("result	 is ").append(this.getResults()).append("\t\n") ;
		return buff.toString() ;
	}

	public int getPageCount() {
   	return pageCount;
   }

	public void setPageCount(int pageCount) {
   	this.pageCount = pageCount;
   }

	public long getCastTime() {
   	return castTime;
   }

	public void setCastTime(long castTime) {
   	this.castTime = castTime;
   }

	public int getResultCount() {
   	return resultCount;
   }

	public void setResultCount(int resultCount) {
   	this.resultCount = resultCount;
   }

	public Map<String, Map<String, String>> getResults() {
   	return results;
   }

	public void setResults(Map<String, Map<String, String>> results) {
   	this.results = results;
   }
}
