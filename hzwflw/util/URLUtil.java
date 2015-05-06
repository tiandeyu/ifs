package ifs.hzwflw.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLUtil {  
	
	
   public static String encodeURL(String url,String encode)  {
   	try {
	      return encodeURL1(url,encode);
      } catch (UnsupportedEncodingException e) {
      }
      return null;
   }
    public static String encodeURL1(String url,String encode)  
            throws UnsupportedEncodingException {  
        StringBuilder sb = new StringBuilder();  
        StringBuilder noAsciiPart = new StringBuilder();  
        for (int i = 0; i < url.length(); i++) {  
            char c = url.charAt(i);  
            if (c > 255) {  
                noAsciiPart.append(c);  
            } else {  
                sb.append(c);  
            }
            if (noAsciiPart.length() != 0) {  
               sb.append(URLEncoder.encode(noAsciiPart.toString(),encode));  
               noAsciiPart.delete(0, noAsciiPart.length());  
           }  
        }  
        return sb.toString();  
    }  
}  
