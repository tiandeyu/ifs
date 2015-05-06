package ifs.contrw.routerevent;

import ifs.fnd.base.EncryptionException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;

import com.goldgrid.word.utils.DBConnUtils;

public class ConnectionFactory{
   public static String classString ="oracle.jdbc.driver.OracleDriver";
   public static String connectionString="jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.82.1.119)(PORT = 1521))(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = cdsdev) ))";
   //public static String connectionString="jdbc:oracle:thin:@naticlaptop:1521:cosmis";
   public static String userName="ifsapp";
   public static String password="ifsapp";
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
   }
   
   private  Properties loadProperties() {
      String base_path = "C:/ifs/jboss-eap-4.3/jboss-as/server/CDSDEV/deploy/fndweb.ear/b2e.war/WEB-INF/config/server.properties";
//         getServletContext().getRealPath("/") 
//                         + ifs.fnd.os.OSInfo.OS_SEPARATOR + "WEB-INF"
//                         + ifs.fnd.os.OSInfo.OS_SEPARATOR +"config"
//                         + ifs.fnd.os.OSInfo.OS_SEPARATOR + "server.properties";
      Properties props = new Properties();
      try {
         InputStream in = new BufferedInputStream(new FileInputStream(base_path));
         props.load(in);
      } catch (Exception e) {
         throw new RuntimeException("Error loading fulltext Db Configuration File, Please Check if file [" + base_path + "] exists or not.");
      }
      return props;
    }

   public static Connection getConnection(){
   Connection conn = null;
      try {
         Class.forName(classString);
         conn = DriverManager.getConnection( connectionString, userName, password);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return conn;
   }

   public static void main(String[] args) {
      System.out.println(getConnection());
   }
}
