/*                 IFS Research & Development
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
 * File                  : DocmawFtp.java
 * Description           : Ftp class used in IFS/Document Management WEB solution
 * Notes                 :
 * Other Programs Called :
 * ----------------------------------------------------------------------------
 * Modified    :
 *   Joro     1998-10-24   created
 *   ErGr     1998-10-27   removed underscore from all error messages identifiers
 *   ErGr     1998-10-27   removed some comments that interfered with localization
 *   MDAHSE   2001-03-15   Changed a couple of System.out.println that before printed errors
 *                         to throw exceptions instead.
 *                         Also changed the way sendFile works a little.
 *                         Changed constant names of localized strings.
 *                         Changed this history to be year 2000-compliant :)
 *   DiKalk   2003-02-03   Added new constructor: DocmawFtp(ASPManager mgr)
 *                         Deprecated method initASP()
 *   DiKalk   2003-04-03   Added the ftp host name to one of the error messages in connect()
 *   DiKalk   2003-07-27   Merged Bud Id 43071.
 *   SHTHLK   2005-07-29   Merged bug ID 51341, Control Connection setKeepAlive in connect() method.
 *   Dikalk   2005-10-10   Removed calls to mgr.getASPLog().error()
 *   Dikalk   2005-10-11   Got rid of ASPManager all together..
 *   NIJALK   2006-07-18   Bug ID 56631, Modified deleteFile(). Added ASPManager and set all exceptions to be translated.  
 *   JANSLK   2007-08-08   Changed all non-translatable constants.
 *   VIRALK   2007-10-28   Bug Id 77080, Added new method checkExist().
 *   SHTHLK   2008-01-08   Bug Id 78864, Made modifications to check the file size instead of the transfererd byte size. 
 *   SHTHLK                uploaded the file to FTP with a temporary file name and renamed it if the transfer is sucessfull.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.docmaw.edm;

import java.io.*;
import java.net.*;
import java.util.*;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;


   final public class DocmawFtp
   {
      private static long restartpoint   = 0L;
      private static final int PRELIM    = 1;
      private static final int COMPLETE  = 2;
      private static final int CONTINUE  = 3;
      private static final int TRANSIENT = 4;
      private static final int ERROR     = 5;

      private Socket          ftpSocket  = null;
      private BufferedReader  is         = null;
      private PrintStream     os         = null;
      private PrintWriter     log        = null;
      //Bug 56631, Start
      private ASPManager      mgr        = null;
      //Bug 56631, End
      private int             returnCode;
      private String          lastSockOut;
      public  String          resultString;


      public DocmawFtp()
      {
      }

      //Bug 56631, Start
      public DocmawFtp(ASPManager mgr)
      {
         this.mgr = mgr;
      }       

      /**
       *
       * @param newManager
       *
       * @deprecated Use the constructor DocmawFtp(ASPManager mgr) to initialise
       *             an instance of this class
       */
      public void initASP(ASPManager newManager)
      {
         mgr = newManager;
      }    
      //Bug 56631, End

      /**
       * Login will pass the user and password you pass in as a param to the FTP server you are connected to.
       *
       * @param user      The user name to use to connect to FTP server.
       * @param password  The password to use to connect to FTP server.
       */
      private void login(String user, String password) throws FndException
      {
         int servRep;
         //Bug 56631, Start
         mgr = new ASPManager();
         //Bug 56631, End

         send("user " + user);
         servRep = getServerReply(is);

         if (servRep == 2)
            return;

         //Bug 56631, Start, modified code to translate the exception
         if (servRep == 5)
            throw new FndException(translateIfAspMgrExisits("DOCMAWFTPUNKNOWNUSER: Ftp User or Password is incorrect"));
         //Bug 56631, End

         send("pass " + password);
         //Bug 56631, Start, modified code to translate the exception
         if (getServerReply(is) != 2)
            throw new FndException(translateIfAspMgrExisits("DOCMAWFTPUNKNOWNUSER: Ftp User or Password is incorrect"));
         //Bug 56631, End
      }


      /**
       * Login will pass the user and password you pass in as a param to the FTP server you are connected to.
       * 
       * @param user      The user name to use to connect to FTP server.
       * @param password  The password to use to connect to FTP server.
       */
      public boolean login(String ftpHost,String user, String password,int port) throws FndException
      {         
         connect(ftpHost,port);
         login(user,password);
         return true;
      }


      /**
       * Logoff will will disconnect you from the FTP Server.
       *
       */
      public boolean logoff() throws FndException
      {
         int returnCode;
         //Bug 56631, Start
         mgr = new ASPManager();
         //Bug 56631, End
         send("quit");

         returnCode = getServerReply(is);

         //Bug 56631, Start, modified code to translate the exception
         if (returnCode != 2)
            throw new FndException(translateIfAspMgrExisits("DOCMAWDOCMAWFTPLOGOFF: Error loging off: &1"), lastSockOut);
         //Bug 56631, End

         try
         {
            // Close input and output streams..
            os.close();
            is.close();            
         }
         catch (IOException ioe)
         {
            // do nothing..
         }
         
         return true;
      }

            //Bug id 77080 Start
	/**
       * This method can be used to check whether a file exist or not
	*/
       public boolean checkExist(String fileName) throws FndException
      {
	 
         send("RNFR " + fileName);
         this.returnCode = getServerReply(is);
	 if(returnCode == 3)
	 {
	     return true;
	 }
	 else
	 {
	     return false;
	 }
      }
        //Bug id 77080 End


      /**
       * putASCIIFile will send a file to the host in ASCII mode.
       *
       * @param fileName  The name of the file to send to the host in ASCII mode. fileName will
       * also be used as remote file name.
       */
      public void putASCIIFile(String fileName) throws FndException
      {
         sendFile("stor ", fileName, fileName, true, true);
      }


      /**
       * putASCIIFile will send a file to the host in ASCII mode.
       *
       * @param filename  The name of the file to be send to the host in ASCII mode.
       * @param toFileName  The name to give the file on the host.
       */
      public void putASCIIFile(String fileName, String toFileName) throws FndException
      {
         sendFile("stor ", fileName, toFileName, true, false);
      }


      /**
       * putBinaryFile will send a file to the host in Binary mode.
       *
       * @param fileName  The name of the file to be send to the host in Binary mode.
       */
      public void putBinaryFile(String fileName) throws FndException
      {
         sendFile("stor ",fileName, fileName, false, true);
      }


      /**
       * putBinaryFile will send a file to the host in Binary mode.
       *
       * @param fileName  The name of the file to be send to the host in Binary mode.
       * @param toFileName  the name to give the file on the host.
       */
      public void putBinaryFile(String fileName, String toFileName) throws FndException
      {
         sendFile("stor ",fileName, toFileName, false, false);
      }


      /**
       * get Binary file from host.
       *
       * @param remoteFilename  The name of the file to get from the host.
       *        localFilename   The name given to the file locally.
       */
      public boolean get(String remoteFilename,String localFilename) throws FndException
      {
         byte             b[];
         int              amount;
         int              result;
         InputStream      cis;
         RandomAccessFile outfile;
         Socket           clientSocket;
         String           command;
         ServerSocket     serverSocket;
         StringTokenizer  stringtokens;
         //Bug 56631,Start
         mgr = new ASPManager();
         //Bug 56631,End

         clientSocket = null;
         serverSocket = null;
         command      = "retr " + remoteFilename;
         long remotefilesize = getremotefilesize(remoteFilename); // Bug Id 78864
         try
         {
            serverSocket = new ServerSocket(0);

            port(serverSocket);
            send("type i");

            //Bug 56631, Start, modified code to translate the exception
            if (getServerReply(is) != 2)
               throw new FndException(translateIfAspMgrExisits("DOCMAWFTPWRRESPONSE: Wrong response from ftp server: &1"), lastSockOut);
            //Bug 56631, End

            send(command);

            if(getServerReply(is) == PRELIM)
            {
               clientSocket = serverSocket.accept();
               cis          = clientSocket.getInputStream();
               b            = new byte[1024];  // 1K blocks I guess
               stringtokens = new StringTokenizer(command);
               stringtokens.nextToken();
               outfile      = new RandomAccessFile(localFilename, "rw");

               while((amount = cis.read(b)) != -1)
               {
                  outfile.write(b, 0, amount);
               }
               //Bug Id 78864, Start - Check the local disk's file size 
	       long writtenlength = outfile.length();
//	       if (writtenlength != remotefilesize ) {
//                   throw new FndException(translateIfAspMgrExisits("DOCMAWFTPFILETRANSFERERROR: Error occurred while transferring the file. Retry the operation again."));
//               } 
	       //Bug Id 78864, End
               outfile.close();

               cis.close();
               clientSocket.close();

               //Bug 56631, Start, modified code to translate the exception
               if (getServerReply(is) != 2)
                  throw new FndException(translateIfAspMgrExisits("DOCMAWFTPWRRESPONSE: Wrong response from ftp server: &1"), lastSockOut);
               //Bug 56631, End
            }
            else
            {
               serverSocket.close();
               //Bug 56631, Start, modified code to translate the exception
               throw new FndException(translateIfAspMgrExisits("DOCMAWFTPFILENOTEXIST: File does not exist on server. FTP error: &1"), lastSockOut);
               //Bug 56631, End
            }
         }
         catch(Exception any)
         {
            throw new FndException(any.getMessage());
         }

         return true;
      }


      /**
       * getASCIIFile will get a file from the host in ASCII mode.
       *
       * @param filename  The name of the file to get from the host in ASCII mode.
       */
      public void getASCIIFile(String fileName) throws FndException
      {
         getFile("retr " + fileName,true, is, os, true);
      }


      /**
       * getBinaryFile will get a file from the host in Binary mode.
       *
       * @param filename  The name of the file to get from the host in Binary mode.
       */
      public void getBinaryFile(String fileName) throws FndException
      {
         getFile("retr " + fileName,true, is, os, false);
      }


      /**
       * changeDirectory will let you change the directory on the server.
       *
       * @param directory  The new directory to go to.
       */
      public void changeDirectory(String directory) throws FndException
      {
         send("cwd " + directory);
         this.returnCode = getServerReply(is);
      }


      /**
       * deleteFile will delete a file from the site
       *
       * @param fileName  The name of the file you would like to be deletednew directory to go to.
       */
      public void deleteFile(String fileName) throws FndException
      {
         //Bug Id 56631 start, if there is no file in the harvest exception should not be thrown. we have to continue.
         // ftp command RNFR is used to check the existance of the file.
         mgr = new ASPManager();
         send("RNFR " + fileName);
         this.returnCode = getServerReply(is);

         if (this.returnCode == 3)
         {
             send("dele " + fileName);
             this.returnCode = getServerReply(is);

             if (this.returnCode != 2)
             {
                 throw new FndException(translateIfAspMgrExisits("DOCMAWFTPCANOTDELETE: Failed to delete the file '&1' from the ftp.") , fileName);
             }
         }
         //Bug Id 56631, End
      }


      /**
       * getReturnCode will return the code last return from the FTP Server.
       *
       * @return return the last code returned from the FTP Server
       */
      public int getReturnCode()
      {
         return this.returnCode;
      }


      public void setLog(OutputStream out)
      {
         log = new PrintWriter(out);
      }


      private void send(String command)
      {
         os.print(command + "\r\n");
         if (log != null)
         {
            log.print("SND>" + command + "\r\n");
            log.flush();
         }
      }


      private void connect(String ftpServer,int portNum) throws FndException
      {
          //Bug 56631, Start
          mgr = new ASPManager();
          //Bug 56631, End
         try
         {
            ftpSocket = new Socket(ftpServer, portNum);
	    ftpSocket.setKeepAlive(true);
            os        = new PrintStream(ftpSocket.getOutputStream());
            is        = new BufferedReader(new InputStreamReader(ftpSocket.getInputStream()));
         }
         catch (UnknownHostException e)
         {
             //Bug 56631, Start, modified code to translate the exception
            throw new FndException(translateIfAspMgrExisits("DOCMAWFTPHOSTUNKNOWN: Ftp host &1 not found"), ftpServer);
            //Bug 56631, End
         }
         catch (IOException e)
         {
             //Bug 56631, Start, modified code to translate the exception
            throw new FndException(translateIfAspMgrExisits("DOCMAWFTPPORTERROR: Couldn't get I/O, port number may be wrong"));
            //Bug 56631, End
         }

         //Bug 56631, Start, modified code to translate the exception
         if (getServerReply(is) != 2)
            throw new FndException(translateIfAspMgrExisits("DOCMAWFTPCONNECTIONREFUSED: Connection refused by server. Error: &1"), lastSockOut);
         //Bug 56631, End
      }


      private int getServerReply(BufferedReader is) throws FndException
      {
         String sockoutput;
         //Bug 56631, Start
         mgr = new ASPManager();
         //Bug 56631, End

         try
         {
            do
            {
               sockoutput = is.readLine();

               // MDAHSE, Save this if we want to print this error later on
               lastSockOut = sockoutput;

               if (sockoutput.length() > 3)

                  if (log != null)
                  {
                     log.print("RCV>" + sockoutput + "\r\n");
                     log.flush();
                  }
            }
            while(!(sockoutput.length() > 3 && Character.isDigit(sockoutput.charAt(0)) && Character.isDigit(sockoutput.charAt(1)) && Character.isDigit(sockoutput.charAt(2)) && sockoutput.charAt(3) == ' '));
         }
         catch (IOException e)
         {
            if (log != null)
            {
               log.print("RCV> ERROR" + "\r\n");
               log.flush();
            }

            //Bug 56631, Start, modified code to translate the exception
            throw new FndException(translateIfAspMgrExisits("DOCMAWFTPCONNECTIONLOST: Connection lost"));
            //Bug 56631, End
         }

         return(Integer.parseInt(sockoutput.substring(0, 1)));
      }


      private boolean sendFile(String command,
                String inFileName,
                String inToFileName,
                boolean ASCII,
                boolean stripRemotePath) throws FndException
      {
         ServerSocket serverSocket = null;
         //Bug 56631, Start
         mgr = new ASPManager();
         //Bug 56631, End
	 //Bug Id 78864. Start
	 String tempInFileName = null;	
	 boolean showError = false;
	 //Bug Id 78864, End
         try
         {
            serverSocket = new ServerSocket(0);
         }
         catch (IOException e)
         {
             //Bug 56631, Start, modified code to translate the exception
            throw new FndException(translateIfAspMgrExisits("DOCMAWDOCMAWFTPNOPORTFORLISTENING: Could not get port &1 for listening:  &2"), Integer.toString(serverSocket.getLocalPort()), e.toString());
            //Bug 56631, End
         }

         port(serverSocket);

         if(ASCII)
         {
            send("type a");
         }
         else
         {
            send("type i");
         }

         this.returnCode = getServerReply(is);

         if(restartpoint != 0)
         {
            send("rest " + restartpoint);
            this.returnCode = getServerReply(is);
         }

         // MDAHSE, 2001-03-15
         // Changed this a little.

         if (stripRemotePath == true)
         {
            tempInFileName = new File(inToFileName).getName() + ".tmp"; //Bug Id 78864, rename the file to make a temp file
         }
         else
         {
            tempInFileName = inToFileName + ".tmp"; //Bug Id 78864, rename the file to make a temp file
         }
	 //Bug Id 78864, Start
         deleteFile(tempInFileName);

	 send(command + tempInFileName);
	 //Bug Id 78864, End
         int result = getServerReply(is);

         if(result == PRELIM)
         {
            Socket clientSocket = null;

            try
            {
               clientSocket = serverSocket.accept();
            }
            catch (IOException e)
            {
                //Bug 56631, Start, modified code to translate the exception
               throw new FndException(translateIfAspMgrExisits("DOCMAWDOCMAWFTPACCEPTFAILED: Accept failed on port &1: &2"), Integer.toString(serverSocket.getLocalPort()), e.toString());
               //Bug 56631, End
            }

            try
            {
               OutputStream outdataport = clientSocket.getOutputStream();
               byte b[] = new byte[1024];  // 1K blocks I guess

               RandomAccessFile infile = new RandomAccessFile(inFileName, "r");

               // do restart if desired
               if(restartpoint != 0)
               {
                  infile.seek(restartpoint);
                  restartpoint = 0;
               }

               int amount;
               long filelength = infile.length(); //Bug Id 78864
               while ((amount = infile.read(b)) > 0)
               {
                  outdataport.write(b, 0, amount);
               }

               infile.close();

               outdataport.close();
               clientSocket.close();
               serverSocket.close();

               result = getServerReply(is);
	       //Bug Id 78864, Start
	       long remotefilesize = getremotefilesize(tempInFileName);
	       if (remotefilesize == filelength ) 
	       {
                   deleteFile(inToFileName);

		   send("RNFR " + tempInFileName);
		   result = getServerReply(is);
		   send("RNTO " + inToFileName);
		   result = getServerReply(is);

		   if (getremotefilesize(inToFileName) != filelength ) 
		   {
		       showError = true;
		   }
	       }
	       else
	       {
		   deleteFile(tempInFileName);
		   showError = true;
	       }
//	       if (showError) 
//	       {
//		   throw new FndException(translateIfAspMgrExisits("DOCMAWFTPFILETRANSFERERROR: Error occurred while transferring the file. Retry the operation again."));
//	       }    
	       //Bug Id 78864, End
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }

            return(result == COMPLETE);
         }
         else
         {
            try
            {
               serverSocket.close();
               //Bug 56631, Start, modified code to translate the exception
               throw new FndException(translateIfAspMgrExisits("DOCMAWFTPPUTERROR: There was an unexpected response while trying to store the file on the FTP-server: &1"),lastSockOut);
               //Bug 56631, End
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
            return(false);
         }
      }


      private void port(ServerSocket serverSocket) throws FndException
      {
         int localport = serverSocket.getLocalPort();
         //Bug 56631, Start
         mgr = new ASPManager();
         //Bug 56631, End

         // Changed a block of old bad code here. This is much smaller
         // and works like a charm. What it does is to fetch the
         // address of the machine we are running on. The old code
         // tried to do that too, but in a way that was wrong and that
         // just accidentally worked. Yes, that was the case.

         byte[] addrbytes;

         addrbytes = ftpSocket.getLocalAddress().getAddress();

         short addrshorts[] = new short[4];

         for(int i = 0; i <= 3; i++)
         {
            addrshorts[i] = addrbytes[i];
            if(addrshorts[i] < 0)
               addrshorts[i] += 256;
         }

         send("port " + addrshorts[0] + "," +  addrshorts[1] + "," + addrshorts[2] + "," + addrshorts[3] + "," + ((localport & 0xff00) >> 8) + "," + (localport & 0x00ff));

         //Bug 56631, Start, modified code to translate the exception
         if (getServerReply(is) != COMPLETE)
            throw new FndException(translateIfAspMgrExisits("DOCMAWDOCMAWFTPRECEIVE: Error can't receive from server: &1"), lastSockOut);
         //Bug 56631, End
      }


      private void getFile(String command, boolean  saveToFile, BufferedReader incontrolport, PrintStream outcontrolport, boolean ASCII) throws FndException
      {
         String filename;
         //Bug 56631, Start
         mgr = new ASPManager();
         //Bug 56631, End

         ServerSocket serverSocket = null;

         try
         {
            serverSocket = new ServerSocket(0);
         }
         catch (IOException e)
         {
            //Bug 56631, Start, modified code to translate the exception
            throw new FndException(translateIfAspMgrExisits("DOCMAWDOCMAWFTPNOPORTFORLISTENING: Could not get port &1 for listening:  &2"), Integer.toString(serverSocket.getLocalPort()), e.toString());
            //Bug 56631, End
         }

         try
         {
            port(serverSocket);
         }
         catch(Exception e)
         {
            return;
         }

         if(saveToFile)
         {
            if(ASCII)
            {
               outcontrolport.print("TYPE A" + "\r\n");
            }
            else
            {
               outcontrolport.print("TYPE I" + "\r\n");
            }
            this.returnCode = getServerReply(incontrolport);
         }

         if(restartpoint != 0)
         {
            outcontrolport.print("REST " + restartpoint + "\r\n");
            this.returnCode = getServerReply(incontrolport);
         }

         outcontrolport.print(command + "\r\n");
         int result = getServerReply(incontrolport);

         if(result == PRELIM)
         {
            Socket clientSocket = null;
            try
            {
               clientSocket = serverSocket.accept();
            }
            catch (IOException e)
            {
                //Bug 56631, Start, modified code to translate the exception
               throw new FndException(translateIfAspMgrExisits("DOCMAWDOCMAWFTPACCEPTFAILED: Accept failed on port &1: &2"), Integer.toString(serverSocket.getLocalPort()), e.toString());
               //Bug 56631, End
            }

            try
            {
               InputStream is = clientSocket.getInputStream();
               byte b[] = new byte[1024];  // 1K blocks I guess
               int amount;

               if(saveToFile)
               {
                  StringTokenizer stringtokens = new
                  StringTokenizer(command);
                  stringtokens.nextToken();

                  filename = stringtokens.nextToken();

                  resultString = new String(filename);

                  RandomAccessFile outfile = new RandomAccessFile(filename, "rw");

                  if(restartpoint != 0)
                  {
                     outfile.seek(restartpoint);
                     restartpoint = 0;
                  }

                  while((amount = is.read(b)) != -1)
                  {
                     outfile.write(b, 0, amount);
                  }
                  outfile.close();
               }
               else
                  while((amount = is.read(b)) != -1)
                     System.out.write(b, 0, amount);

               this.returnCode = getServerReply(incontrolport);
               is.close();
               clientSocket.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
         else
         {
            try
            {
               serverSocket.close();
            }
            catch (IOException e)
            {
            }
         }
      }

		// Bug 57279, Start
		private String translateIfAspMgrExisits(String text)
		{
			if (this.mgr != null)
			{
				return mgr.translate(text);
			}
			else
            return text.substring(text.indexOf(":")+1); // return without translating if the asp manager is null.
		}
		// Bug 57279, End 
      //Bug Id 78864, Start
      private long getremotefilesize(String remotefilename) throws FndException
      {
         String firstline = "";
         
         os.print("SIZE " + remotefilename + "\r\n");
         if (log != null)
         {
             log.print("SND>" + "SIZE " + remotefilename + "\r\n");
             log.flush();
         }
         
         try
         {
             do
             {
                 firstline = is.readLine();
                 if (firstline.length() > 3)
                    if (log != null)
                    {
                        log.print("RCV>" + firstline + "\r\n");
                        log.flush();
                    }
             }
             while(!(firstline.length() > 3 && Character.isDigit(firstline.charAt(0)) && Character.isDigit(firstline.charAt(1)) && Character.isDigit(firstline.charAt(2)) && firstline.charAt(3) == ' '));
         }
         catch(IOException e)
         {
             if (log != null)
             {
                 log.print("RCV> ERROR" + "\r\n");
                 log.flush();
             }
             throw new FndException(translateIfAspMgrExisits("DOCMAWFTPCONNECTIONLOST: Connection lost"));
         }         
         return Long.parseLong(firstline.substring(4));
      } 
      //Bug Id 78864, End

   }
