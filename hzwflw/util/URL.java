package ifs.hzwflw.util;

public class URL {
	
	public URL(String originalUrl){
		this.originalUrl = originalUrl;
		url = new StringBuilder(originalUrl);
	}
	
	public void addParameters(String paraName,String value){
		if(url.toString().indexOf('?') < 0){
			url.append('?');
		}else{
			url.append('&');
		}
		url.append(paraName).append("=").append(value);
	}
	
	public static void main(String[] args) {
		URL url = new URL("/b2e/secured/hzwflw/StartProcess.page");
		url.addParameters("name", "¹ÜÀíÔ±");
		System.out.println(url);
	}
	
	public String toString(){
	   String tempUrl = URLUtil.encodeURL(url.toString(), "UTF-8");
	   System.out.println(tempUrl);
		return tempUrl;
	}
	
	private String originalUrl = null;
	private StringBuilder url = null;
}
