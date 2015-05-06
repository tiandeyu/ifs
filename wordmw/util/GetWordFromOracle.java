package ifs.wordmw.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;
import DBstep.iDBManager2000;

public class GetWordFromOracle {
	
	public GetWordFromOracle() {
		// TODO Auto-generated constructor stub
	}
	
	private String mRecordID;  
	private iDBManager2000  DbaObj;
	private int mFileSize;
	private byte[] mFileBody;
	
	String tempDir;
	private String tempFile;
	StringBuilder sb = new StringBuilder();
	
	
	/**
	 * goldgrid record id
	 * @param mRecordID
	 */
	public GetWordFromOracle(String mRecordID,String tempDir){
		 DbaObj = new DBstep.iDBManager2000();
		 this.mRecordID = mRecordID;
		 this.tempDir = tempDir;
 		 tempFile = getTempDir() + mRecordID + ".doc";
		 
	}
	
	public static void main(String[] args){
//		D:\ifs75sp7\java\bin
		System.err.println(System.getProperty("java.library.path"));
		String wordFilePath = new GetWordFromOracle("1363152263097","D:\\").getFile();
	   String pdfFilePath = Word2Pdf.transfer(wordFilePath);
	   System.out.println(pdfFilePath);
   }
	
	public String getFile(){
		LoadFile();
		DBstep.iMsgServer2000 MsgObj=new DBstep.iMsgServer2000();	
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(new File(tempFile));
	      fout.write(MsgObj.ToDocument(mFileBody));
      } catch (IOException e) {
      }finally{
      	try {
	         fout.close();
         } catch (IOException e) {
         } 
      }
      return tempFile;
	}
	
	public String getFileName(){
		return tempFile;
	}
	
	private String getTempDir(){
		return tempDir;
	}
	
	
	//get file content from db blok.
	private void GetAtBlob(BLOB vField, int vSize) throws IOException {
		try {
			mFileBody = new byte[vSize];
			InputStream instream = vField.getBinaryStream();
			instream.read(mFileBody, 0, vSize);
			instream.close();
		} catch (SQLException e) {
		}
	}
	
	//query file body and store it in mFileBody
	private boolean LoadFile() {
		boolean mResult = false;
		String Sql = "SELECT FileBody,FileSize FROM Document_File WHERE RecordID='"
				+ mRecordID + "'";
		try {
			if (DbaObj.OpenConnection()) {
				try {
					ResultSet result = DbaObj.ExecuteQuery(Sql);
					if (result.next()) {
						try {
							mFileSize = result.getInt("FileSize");
							GetAtBlob(((OracleResultSet) result)
									.getBLOB("FileBody"), mFileSize);
							mResult = true;
						} catch (IOException ex) {
							System.out.println(ex.toString());
						}
					}
					result.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
					mResult = false;
				}
			}
		} finally {
			DbaObj.CloseConnection();
		}
		return (mResult);
	}
	
	

}
