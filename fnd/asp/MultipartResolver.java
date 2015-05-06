package ifs.fnd.asp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

public class MultipartResolver {
   
   public String filePath;
   public String fileName;
   public byte contentBuffer[];
   public int contentLength;
   public byte partBuffer[];
   public int partlength;
   
   
   public MultipartResolver(HttpServletRequest request)
   {
       int totalRead = 0;
       int readBytes = 0;
       int mSingCur = 0;
       try
       {
           contentLength = request.getContentLength();
           contentBuffer = new byte[contentLength];
           for(; totalRead < contentLength; totalRead += readBytes)
           {
               request.getInputStream();
               readBytes = request.getInputStream().read(contentBuffer, totalRead, contentLength - totalRead);
           }

       }
       catch(Exception e)
       {
           System.out.println(e.toString());
       }
       for(partlength = 0; contentBuffer[partlength] != 13; partlength = partlength + 1);
       partBuffer = new byte[partlength];
       for(mSingCur = 0; mSingCur < partlength; mSingCur++)
           partBuffer[mSingCur] = contentBuffer[mSingCur];
       filePath = "";
       fileName = "";
   }

   public int FindSing(int Start)
   {
       int result = -1;
       int s = Start;
       do
       {
           if(s >= contentLength - partlength)
           {
               result = -1;
               break;
           }
           int DjfOk = 1;
           int i = 0;
           do
           {
               if(i >= partlength)
                   break;
               if(partBuffer[i] != contentBuffer[s + i])
               {
                   DjfOk = 0;
                   break;
               }
               i++;
           } while(true);
           if(DjfOk == 1)
           {
               result = s;
               break;
           }
           s++;
       } while(true);
       return result;
   }

   public int FindItem(String TagName)
   {
       int result;
 label0:
       {
           result = -1;
           int s = 0;
           do
           {
               s = FindSing(s);
               if(s == -1)
                   break;
               s = s + partlength + 40;
               int e = s;
               if(e + 1 >= contentLength)
               {
                   result = -1;
                   break label0;
               }
               for(; contentBuffer[e + 1] != 34; e++);
               String strName = new String(contentBuffer, s, (e - s) + 1);
               if(TagName.equalsIgnoreCase(strName))
               {
                   result = e + 1;
                   break label0;
               }
           } while(true);
           result = -1;
       }
       return result;
   }

   public String ItemValue(String TagName)
   {
       String strTmp = "";
       String result = "";
       int s = FindItem(TagName);
       if(s == -1)
           return result;
       int e;
       if(contentBuffer[s + 1] == 59)
       {
           s += 13;
           for(e = s; contentBuffer[e + 1] != 34; e++);
       } else
       {
           s += 5;
           e = FindSing(s) - 3;
       }
       if(e - s < 0)
       {
           return result;
       } else
       {
           try {
             strTmp = new String(contentBuffer, s, (e - s) + 1,"UTF-8");
          } catch (UnsupportedEncodingException e1) {
             e1.printStackTrace();
          }
           result = strTmp;
           return result;
       }
   }

   public String ExtName(String TagName)
   {
       String mFileName = "";
       String mExtName = "";
       mExtName = "";
       mFileName = ItemValue(TagName);
       if(mFileName != "")
           mExtName = mFileName.substring(mFileName.lastIndexOf("."));
       return mExtName;
   }

   public byte[] FileBody(String TagName)
   {
       byte mBody[] = null;
       int s = FindItem(TagName);
       if(s == -1)
           return mBody;
       s += 13;
       int i;
       for(i = s; contentBuffer[i] != 34; i++);
       int e = i - 1;
       if(e - s < 0)
           return mBody;
       i = e;
       do
       {
           if(contentBuffer[i - 4] == 13 && contentBuffer[i - 3] == 10 && contentBuffer[i - 2] == 13 && contentBuffer[i - 1] == 10)
           {
               s = i;
               break;
           }
           i++;
       } while(true);
       e = FindSing(s) - 3;
       if(e - s >= 0)
       {
           mBody = new byte[(e - s) + 1];
           int j = 0;
           for(i = s; i <= e; i++)
           {
               mBody[j] = contentBuffer[i];
               j++;
           }

       }
       return mBody;
   }

   public String FileName(String TagName)
   {
       String result = "";
       int s = FindItem(TagName);
       if(s == -1)
           return result;
       s += 13;
       int i;
       for(i = s; contentBuffer[i] != 34; i++);
       int e = i - 1;
       if(e - s < 0)
           return result;
       i = e;
       do
       {
           if(i < s)
               break;
           if((char)contentBuffer[i - 1] == '\\')
           {
               s = i;
               break;
           }
           i--;
       } while(true);
       
      result = new String(contentBuffer, s, (e - s) + 1);
       return result;
   }
   public String FileName(String TagName,String encode)
   {
      String result = "";
      int s = FindItem(TagName);
      if(s == -1)
         return result;
      s += 13;
      int i;
      for(i = s; contentBuffer[i] != 34; i++);
      int e = i - 1;
      if(e - s < 0)
         return result;
      i = e;
      do
      {
         if(i < s)
            break;
         if((char)contentBuffer[i - 1] == '\\')
         {
            s = i;
            break;
         }
         i--;
      } while(true);
      try {
         result = new String(contentBuffer, s, (e - s) + 1,encode);
      } catch (UnsupportedEncodingException e1) {
         e1.printStackTrace();
      }
      return result;
   }

   public int FileSize(String TagName)
   {
       int result = 0;
       int s = FindItem(TagName);
       if(s == -1)
           return result;
       s += 13;
       int i;
       for(i = s; contentBuffer[i] != 34; i++);
       int e = i - 1;
       if(e - s < 0)
           return result;
       i = e;
       do
       {
           if(contentBuffer[i - 4] == 13 && contentBuffer[i - 3] == 10 && contentBuffer[i - 2] == 13 && contentBuffer[i - 1] == 10)
           {
               s = i;
               break;
           }
           i++;
       } while(true);
       e = FindSing(s) - 3;
       result = (e - s) + 1;
       return result;
   }

   public String getFilePath()
   {
       return filePath;
   }

   public String getParameterValue(String TagName)
   {
       return ItemValue(TagName);
   }

   public boolean SaveAll(String FileName)
   {
       boolean result = false;
       try
       {
           FileOutputStream mFile = new FileOutputStream(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(filePath)))).append('\\').append(FileName))));
           for(int i = 0; i < contentLength; i++)
               mFile.write(contentBuffer[i]);

           mFile.close();
           result = true;
       }
       catch(Exception ex)
       {
           System.out.println(ex.toString());
           result = false;
       }
       return result;
   }

   public boolean SaveFile(String TagName, String FileName)
   {
       String mFileName = "";
       boolean result = false;
       mFileName = FileName;
       int s = FindItem(TagName);
       if(s == -1)
       {
           result = false;
           return result;
       }
       s += 13;
       int i;
       for(i = s; contentBuffer[i] != 34; i++);
       int e = i - 1;
       if(e - s < 0)
       {
           result = false;
           return result;
       }
       i = e;
       do
       {
           if(i < s)
               break;
           if((char)contentBuffer[i - 1] == '\\')
           {
               s = i;
               break;
           }
           i--;
       } while(true);
       fileName = new String(contentBuffer, s, (e - s) + 1);
       if(mFileName.trim() != "")
           fileName = mFileName;
       i = e;
       do
       {
           if(contentBuffer[i - 4] == 13 && contentBuffer[i - 3] == 10 && contentBuffer[i - 2] == 13 && contentBuffer[i - 1] == 10)
           {
               s = i;
               break;
           }
           i++;
       } while(true);
       e = FindSing(s) - 3;
       try
       {
           FileOutputStream mFile = new FileOutputStream(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(filePath)))).append('\\').append(fileName))));
           if(mFile != null)
           {
               if(e - s >= 0)
               {
                   for(i = s; i <= e; i++)
                       mFile.write(contentBuffer[i]);

                   result = true;
               }
           } else
           {
               result = false;
           }
           mFile.close();
       }
       catch(Exception ex)
       {
           System.out.println(ex.toString());
           result = false;
       }
       return result;
   }

   public boolean setFilePath(String Value)
   {
       filePath = Value;
       File mFile = new File(filePath);
       mFile.mkdirs();
       return mFile.isDirectory();
   }
 }
