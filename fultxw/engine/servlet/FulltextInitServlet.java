package ifs.fultxw.engine.servlet;

import ifs.fnd.base.EncryptionException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;

import com.goldgrid.word.utils.DBConnUtils;

public class FulltextInitServlet extends HttpServlet {
   public static String classString = "oracle.jdbc.driver.OracleDriver";
   public static String connectionString = "jdbc:oracle:thin:@lqw-pc:1521:gddev";
   public static String userName = "IFSAPP";
   public static String password = "IFSAPP";
   
   public void init(){
      Properties  props=loadProperties();
      classString = props.getProperty("dbpool.driver_class");
      connectionString = props.getProperty("dbpool.url");
      userName = props.getProperty("dbpool.username");
      try {
         password = ifs.fnd.base.FndEncryption.decrypt(props.getProperty("dbpool.password"));
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      } catch (EncryptionException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      } catch (IOException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
      
      this.getServletContext().log("fulltext oracle driver class name:"+DBConnUtils.ClassString);
      this.getServletContext().log("fulltext oracle connection url:"+DBConnUtils.ConnectionString);
      this.getServletContext().log("fulltext oracle connectioin user:"+DBConnUtils.UserName);
      this.getServletContext().log("fulltext oracle password:"+props.getProperty("dbpool.password"));
   }
   
   private  Properties loadProperties() {
      String base_path = getServletContext().getRealPath("/") 
                         + ifs.fnd.os.OSInfo.OS_SEPARATOR + "WEB-INF"
                         + ifs.fnd.os.OSInfo.OS_SEPARATOR +"config"
                         + ifs.fnd.os.OSInfo.OS_SEPARATOR + "server.properties";
      Properties props = new Properties();
      try {
         InputStream in = new BufferedInputStream(new FileInputStream(base_path));
         props.load(in);
      } catch (Exception e) {
         throw new RuntimeException("Error loading fulltext Db Configuration File, Please Check if file [" + base_path + "] exists or not.");
      }
      return props;
    }

}
