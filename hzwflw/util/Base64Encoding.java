package ifs.hzwflw.util;

import org.apache.commons.codec.binary.Base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/** 
 * ��url���ܵļ��ܽ����㷨�������ļ��ܽ��ֻ�����ֺ���ĸ 
 * @author Administrator 
 * 
 */ 
public class Base64Encoding { 
    private static final BASE64Decoder decoder = new BASE64Decoder(); 
    private static final BASE64Encoder encoder = new BASE64Encoder(); 
    private static final Base64 base64 = new Base64(); 
 
    /** 
     * BASE64���� 
     *  
     * @param key 
     * @return 
     * @throws Exception 
     */ 
    public static String encryptBASE64(String key) throws Exception { 
        if (key == null || key.length() < 1) { 
            return ""; 
        } 
        //return new String(encoder.encode(key.getBytes())); 
        return new String(base64.encodeBase64URLSafe((new String(encoder.encode(key.getBytes()))).getBytes())); 
    } 
     
     
    public void changeCodeGBK(){ 
         
    } 
    /** 
     * BASE64���� 
     *  
     * @param key 
     * @return 
     * @throws Exception 
     */ 
    public static String decryptBASE64(String key) throws Exception { 
        if (key == null || key.length() < 1) { 
            return ""; 
        } 
        return new String(decoder.decodeBuffer(new String(base64.decodeBase64(key.getBytes())))); 
        //return new String(base64.decodeBase64(key.getBytes())); 
         
         
    } 
     
    public static void main(String[] args) throws Exception { 
//       String s=Base64Encoding.encryptBASE64("$%&^*%^:/=&?*  ^"); 
        String s=Base64Encoding.encryptBASE64("http://lqw:58080/b2e/secured/hzwflw/HzDelegateSetNav.page?__LS__=ZHANGBH"); 
        System.out.println(s); 
        System.out.println(Base64Encoding.decryptBASE64(s)); 
    } 
}