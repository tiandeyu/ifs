/*
 *                 IFS Research & Development
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
 * File        : ConfigFileParser.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2005-Oct-27 - Created.
 * ----------------------------------------------------------------------------
 * New Comments:
 *               2006/09/26          riralk
 * Q&D fix in spoolMap() for Bug id 60287. Avoid storing comments in generated change log file. 
 * Since mulitline comments cause problems in installer when applying changes.
 *
 * Revision 1.3  2005/11/16 10:28:17  riralk
 * Fxied bug in applyChanges() made sure that the source and destination files have complete paths. (i.e. parent directories)
 *
 * Revision 1.2  2005/11/15 15:21:09  riralk
 * Fixed bug in createLog() provided parent templ_dir when creating templfile.
 *
 * Revision 1.1  2005/11/04 12:15:45  japase
 * Class ConfigFileParser moved from ifs.fnd.asp to ifs.fnd.xml package
 *
 * Revision 1.2  2005/11/04 12:12:40  japase
 * Added new public methods: createLog() and applyChanges()
 *
 * Revision 1.1  2005/11/02 13:41:33  japase
 * Functionality for handling of XML files; moved from ASPConfigFile and improved.
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.xml;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;


/**
 */
public class ConfigFileParser
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public static final String NL = System.getProperty("line.separator");

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ConfigFileParser");


   /**
    */
   public static void parseXMLFile( File cfg_file, Buffer dest, boolean preserve_empty ) throws FndException
   {
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder        builder = factory.newDocumentBuilder();
         Document               doc     = builder.parse(cfg_file);

         parseXML(doc, dest, preserve_empty);
      }
      catch(SAXException e)
      {
         if(DEBUG) Util.debug("Caught exception:\n" + Str.getStackTrace(e));
         String msg = e.getMessage();
         if(e instanceof SAXParseException)
         {
            SAXParseException spe = (SAXParseException)e;
            int col    = spe.getColumnNumber();
            int line   = spe.getLineNumber();
            String pid = spe.getPublicId();
            String sid = spe.getSystemId();
            if(DEBUG) Util.debug("Line: "+line+", Column: "+col+", Public ID: '"+pid+"', System ID: '"+sid+"'");
            if(col<0)
               throw new FndException("FNDCFFCFGSAX1: Error '&1' in line &2, while parsing file '&3'",
                                      msg, line+"", cfg_file.getName() );
            else
               throw new FndException("FNDCFFCFGSAX2: Error '&1' in line/column &2, while parsing file '&3'",
                                      msg, line+"/"+col, cfg_file.getName() );
         }
         else
            throw new FndException("FNDCFFCFGSAX3: Parsing of configuration file '&1' failed with message '&2'",
                                   cfg_file.getName(), msg );
      }
      catch(IOException e)
      {
         if(DEBUG) Util.debug("Caught exception:\n" + Str.getStackTrace(e));
         throw new FndException("FNDCFFCFGIOERR: Could not read configuration file '&1'",cfg_file.getName());
      }
      catch(ParserConfigurationException e)
      {
         if(DEBUG) Util.debug("Caught exception:\n" + Str.getStackTrace(e));
         throw new FndException("FNDCFFCFGPARSE: Could not obtain an XML parser!");
      }
   }

   private static void parseXML( Document doc, Buffer dest, boolean preserve_empty )
   {
      if(DEBUG) Util.debug("ConfigFileParser.parseXML()");

      NodeList nodes         = doc.getChildNodes();
      int      len           = nodes.getLength();
      boolean  cfgnode_found = false; //parsing only first <webclientconfig> node in a file

      for( int i=0; i<len; i++ )
      {
         Node   node = nodes.item(i);
         String name = node.getNodeName();
         if(DEBUG) Util.debug("Found node:"+name);
         if( node.getNodeType()==Node.COMMENT_NODE && preserve_empty )
         {
            Item item = new Item(name, node.getNodeValue() );
            dest.addItem(item);
         }
         else if( "webclientconfig".equals(name) && !cfgnode_found )
         {
            cfgnode_found = true;
            LevelTracker level_tracker = null; // used only for debugging
            if(DEBUG)
               level_tracker = new LevelTracker();
            depthSearchXML(node, dest, preserve_empty, level_tracker);
            if(DEBUG) Util.debug("\nBuffer contents:\n"+Buffers.listToString(dest)+"\n\n");
         }
      }
      if(DEBUG && !cfgnode_found) Util.debug("ConfigFileParser: File is not a Web Client configuration file.");
   }

   private static class LevelTracker
   {
      private int search_level = 0;
   }

   private static void parseDebug( String line, LevelTracker level_tracker )
   {
      for(int i=0; i<level_tracker.search_level; i++)
         line = "  " + line;
      Util.debug(line);
   }

   private static void depthSearchXML( Node node, Buffer dest, boolean preserve_empty, LevelTracker level_tracker )
   {
      if(DEBUG)
      {
         level_tracker.search_level++;
         parseDebug("ConfigFileParser.depthSearchXML(): search_level="+level_tracker.search_level, level_tracker);
      }

      NodeList nodes = node.getChildNodes();
      if(DEBUG) parseDebug("Nested nodes:\n"+nodes, level_tracker);

      int len = nodes.getLength();
      if(DEBUG) parseDebug("Nodes length: "+len, level_tracker);

      for(int i=0; i<len; i++)
      {
         Item item = null;
         Node next_node = nodes.item(i);
         String node_name = next_node.getNodeName();
         if(DEBUG) parseDebug("Found node("+i+"): '"+ node_name + "', value:\""+
                              Str.replace(Str.replace(next_node.getNodeValue(),"\r","\\r"),"\n","\\n")+"\"", level_tracker);

         if(next_node.getNodeType()==Node.COMMENT_NODE)
         {
            if(DEBUG) parseDebug(" - Comment node. Continuing.", level_tracker);
            if(preserve_empty)
            {
               item = new Item(node_name, next_node.getNodeValue() );
               dest.addItem(item);
            }
            else
               continue;
         }

         if(next_node.hasChildNodes())
         {
            NodeList child_nodes = next_node.getChildNodes();
            Node     first_child = child_nodes.item(0);

            // ASSUMPTION:
            // A node is a leaf only if it has only one child and the type of the child is '#text'
            // Then the value of the child node is the value of the leaf
            if(child_nodes.getLength()==1 && "#text".equals(first_child.getNodeName()) )
            {
               if(DEBUG) parseDebug(" - '"+node_name+"' is a leaf.", level_tracker);
               String value = first_child.getNodeValue();
               if(DEBUG) parseDebug(" - Adding '"+node_name.toUpperCase()+"="+value+"'", level_tracker);
               item = new Item(node_name.toUpperCase(), value);
               dest.addItem(item);
            }
            else
            {
               Buffer buf = dest.newInstance();
               if(DEBUG) parseDebug(" - Adding '"+node_name.toUpperCase()+"=!'", level_tracker);
               item = new Item(node_name.toUpperCase(), buf);
               dest.addItem(item);
               depthSearchXML(next_node, buf, preserve_empty, level_tracker);
            }
         }
         else
         {
            if(DEBUG) parseDebug(" - '"+node_name+"' doesn't have children. Continuing.", level_tracker);
            if(preserve_empty && !"#text".equals(node_name) && !"#comment".equals(node_name) )
            {
               if(DEBUG) parseDebug(" - '"+node_name+"' is an empty leaf. Adding...", level_tracker);
               item = new Item(node_name.toUpperCase(), null);
               dest.addItem(item);
            }
            else
               continue;
         }

         if(preserve_empty && item!=null )
         {
            NamedNodeMap attributes = next_node.getAttributes();
            if( attributes!=null )
            {
               if(DEBUG) parseDebug(" - Found attributes.", level_tracker);
               int attrlen = attributes.getLength();
               AutoString attrs = new AutoString();
               for( int j=0; j<attrlen; j++ )
               {
                 Node attrnode = attributes.item(j);
                 String attrname  = attrnode.getNodeName();
                 String attrvalue = attrnode.getNodeValue();
//                 String attrvalue = Str.replace( Str.replace( attrnode.getNodeValue(), "\n", "&#10;"), "\r", "");
//                 String attrvalue = Str.replace( Str.replace( attrnode.getNodeValue(), "\n", "&#xa;"), "\r", "");
                 if(DEBUG) parseDebug(" - - Found attribute '"+attrname+"' with value="+attrvalue, level_tracker);
                 if(j>0)
                    attrs.append(' ');
                 attrs.append(attrname, "=\"");
                 attrs.append( encode(attrvalue) );
                 attrs.append('"');
               }
               item.setType(attrs.toString());
            }
         }
      }
   }

   /**
    * Scan the configuration buffer and create corresponding entries in the hash table
    * 'params' with all parameters.
    * Do NOT insert parameters with empty ("") values.
    */
   public static void parseConfigBuffer( Buffer cfgbuf, Map params, Map macros ) throws FndException
   {
      parseBuffer( cfgbuf, cfgbuf, null, params, macros, new LevelTracker() );
   }

   private static void parseBuffer( Buffer cfgbuf, Buffer buf, String entry, Map params, Map macros, LevelTracker level_tracker ) throws FndException
   {
      for( int i=0; i<buf.countItems(); i++ )
      {
         Item   item = buf.getItem(i);
         String key;

         if( Str.isEmpty(entry) )
            key = item.getName();
         else
            key = entry+"/"+item.getName();

         if( item.isCompound() )
            parseBuffer( cfgbuf, item.getBuffer(), key, params, macros, level_tracker );
         else
         {
            String value = macros!=null ? replaceMacroReferences( cfgbuf, item.getString(), params, macros, level_tracker ) : item.getString();
            if(DEBUG) Util.debug("ConfigFileParser: Found item ["+key+"] = '"+value+"'");
            if( !Str.isEmpty(value) ) params.put( key, value );
         }
      }
   }

   /**
    * Resolve a macro reference to another parameter.
    */
   private static String replaceMacroReferences( Buffer buf, String text, Map params, Map macros, LevelTracker level_tracker ) throws FndException
   {
      if( Str.isEmpty(text) ) return "";
      try
      {
         String old = text; // only for debug

         int i;
         while( (i = text.indexOf("&(") ) >= 0 )
         {
            int j = text.indexOf(")", i+2);
            if ( j<0 )
               throw new FndException("FNDCFFSYNERRCONF: Syntax error in configuration file for value '&1'.",text);
            String key = text.substring(i+2,j);

            if( macros.get(key)!=null )
               throw new FndException("FNDCFFRCALL: Recursive reference in configuration file for value '&1'.",text);
            macros.put(key,"*");
            String repl = findParameter(buf, key, params, macros, level_tracker);
            text = text.substring(0,i)+repl+text.substring(j+1);
            macros.remove(key);
         }
         if (DEBUG)
            if ( !Str.isEmpty(old) && !old.equals(text) )
               Util.debug("ConfigFileParser: "+sep(level_tracker)+"String with macros '"+old+"' replaced with '"+text+"'.");
         return text;
      }
      catch( FndException any )
      {
         macros.clear();
         throw (FndException)(any.fillInStackTrace());
      }
   }

   private static String sep( LevelTracker level_tracker )
   {
      String sep = "  ";
      for( int i=0; i<level_tracker.search_level; i++)
         sep = sep + ". ";
      return sep;
   }

   /**
    * Find a parameter in the 'params' hash table or, if not found,
    * in the configuration buffer.
    */
   private static String findParameter( Buffer buf, String name, Map params, Map macros, LevelTracker level_tracker )  throws FndException
   {
      level_tracker.search_level++;
      String value = (String)params.get(name);
      if( value!=null )
      {
         level_tracker.search_level--;
         if(DEBUG) Util.debug("ConfigFileParser: "+sep(level_tracker)+"Parameter ["+name+"] found in cache: '"+value+"'.");
         return value;
      }

      Item item = buf.findItem(name);
      value = item==null ? "" : replaceMacroReferences( buf, item.getString(), params, macros, level_tracker );

      if( Str.isEmpty(value) )
         throw new FndException("FNDCFFPARNFOUND: Parameter '&1' not found in the confiugauration file.",name);
      else if (DEBUG)
         Util.debug("ConfigFileParser: "+sep(level_tracker)+"Parameter ["+name+"] found: '"+value+"'.");
      level_tracker.search_level--;
      return value;
   }

   /**
    * Compares two sorted maps containing pairs key-value of type String.
    * The resulting map will contain changed and added (=existing only in 'newmap') pairs.
    */
   public static void compare( SortedMap oldmap, SortedMap newmap, Map result )
   {
      Iterator oldit = oldmap.keySet().iterator();
      Iterator newit = newmap.keySet().iterator();

      String oldkey = null;
      String newkey = null;

      class AbortOuterLoopEexception extends Exception {};
      try
      {
         while( oldit.hasNext() && newit.hasNext() )
         {
            oldkey = (String)oldit.next();
            newkey = (String)newit.next();

            int diff = oldkey.compareTo(newkey);

            while( diff!=0 )
            {
               if( diff<0 && oldit.hasNext() )
                  oldkey = (String)oldit.next();
               else if( diff<0 ) // no more elements in 'oldmap' - write all remaining elements from 'newmap' to 'result'
               {
                  result.put(newkey, (String)newmap.get(newkey) );
                  // abort outer 'while' loop here
                  throw new AbortOuterLoopEexception();
               }
               else if( newit.hasNext() )
               {
                  result.put(newkey, (String)newmap.get(newkey) );
                  newkey = (String)newit.next();
               }
               else  // no more elements in 'nemap' - nothing to compare; exit
               {
                  result.put(newkey, (String)newmap.get(newkey) );
                  return;
               }
               diff = oldkey.compareTo(newkey);
            }

            String oldval = (String)oldmap.get(oldkey);
            String newval = (String)newmap.get(newkey);
            if( !oldval.equals(newval) )
               result.put(newkey, newval);
         }
      }
      catch( AbortOuterLoopEexception x )
      {
      }

      while( newit.hasNext() )
      {
         newkey = (String)newit.next();
         result.put(newkey, (String)newmap.get(newkey) );
      }
   }

   private static void spoolMap( String cfgname, Map map, AutoString log )
   {
      log.append(NL,"[",cfgname,"]",NL);
      Iterator itr = map.keySet().iterator();
      while( itr.hasNext() )
      {
         String key = (String)itr.next();
         String val = (String)map.get(key);
         //log.append(cfgname,".",key,"=",val);
         if (key!=null && key.indexOf("#comment")<0) //Bug#60287
         {
           log.append(key,"=",val);
           log.append(NL);
         }
      }
   }

   private static String readFile( String filename ) throws FndException, IOException//, UnsupportedEncodingException
   {
      File file = new File(filename);
      if( !file.exists() ) return null;
      if( !file.isFile() )
         throw new FndException("FNDCFPNOTAFILE: '&1' is not a file.", filename);

      InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF8");
      BufferedReader r = new BufferedReader(isr);

      AutoString buf = new AutoString();
      String line = null;

      do
      {
         line = r.readLine();
         if(line != null)
            buf.append(line, "\n");
      }
      while(line != null);

      r.close();
      return buf.toString();
      /*
      try
      {
      }
      catch(IOException ioex)
      {
         throw (new FndException("FNDOPENFERR: Cannot open file '&1': [&2]", filename, ioex.getClass().getName())).addCaughtException(ioex);
      }
      */
   }

   /**
    * From installer:
    *  - copy the template config files to a temporary dir
    *  - call applyChanges( temp_dir_with_config_files, log_file_name )
    *  - run XML editor
    *
    * From build.xml:
    *  - copy config files from the temp dir to the proper location
    *  - call createLog( dir_with_config_files, dir_with_template_files, log_file_name )
    *
    * Log files:
    *  - property format: key=value per config file, where key is of type "A/B/C"
    *  - one file per instance and site
    *  - properties collected in paragraphs defined as [CONFIGFILE]
    */
   public static void applyChanges( String confdir, String logfile ) throws FndException, IOException
   {
      String str = readFile(logfile);
      if( Str.isEmpty(str) ) return;

      File conf_dir  = new File(confdir);
      if( !conf_dir.isDirectory() )
         throw new FndException("FNDCFPNOTADIR1: '&1' is not a directory.", confdir);

      String[] configs = conf_dir.list();
      for( int i=0; i<configs.length; i++ )
      {
         if( !configs[i].toLowerCase().endsWith(".xml") ) continue;

         String section = "\n["+configs[i].toUpperCase()+"]\n";
         int start = str.indexOf(section);

         if( start<0 ) continue;

         start += section.length();
         int end = str.indexOf("\n[",start);

         Map map = new HashMap();
         StringTokenizer st = new StringTokenizer( end<0 ? str.substring(start) : str.substring(start,end), "\n");
         while( st.hasMoreTokens() )
         {
            String token = st.nextToken();
            int ix = token.indexOf("=");
            if( ix<0 )
             continue;  //throw new FndException("FNDCFPLOGSYNTAXERR: Syntax error in the log file, section [&1]: '&2'.", configs[i], token);
            map.put( token.substring(0,ix), token.substring(ix+1) );
         }
         applyChanges( new File(conf_dir,configs[i]), confdir + File.separator + configs[i], map );
      }
   }

   public static void createLog( String confdir, String templatedir, String logfile ) throws FndException, IOException
   {
      File templ_dir = new File(templatedir);
      File conf_dir  = new File(confdir);
      if( !templ_dir.isDirectory() || !conf_dir.isDirectory() )
         throw new FndException("FNDCFPNOTADIR: At least one of '&1' or '&2' is not a directory.", templatedir, confdir);

      AutoString log = new AutoString();

      String[] templates = templ_dir.list();
      for( int i=0; i<templates.length; i++ )
      {
         if( !templates[i].toLowerCase().endsWith(".xml") ) continue;

         File cfgfile = new File(conf_dir, templates[i]);
         if( !cfgfile.isFile() ) continue;

         File templfile = new File(templ_dir, templates[i]);
         if( !templfile.isFile() ) continue;

         SortedMap templ = new TreeMap();
         SortedMap conf  = new TreeMap();

         Buffer buf = new StandardBuffer();
         parseXMLFile( templfile, buf, true);
         parseConfigBuffer( buf, templ, null);

         buf = new StandardBuffer();
         parseXMLFile( cfgfile, buf, true);
         parseConfigBuffer( buf, conf, null);

         Map result = new HashMap();
         compare(templ, conf, result);

         spoolMap(templates[i].toUpperCase(), result, log);
      }
      Util.writeFile( logfile, log.toString() );
   }

   /**
    * Read the XML source file 'srcfile', apply the modified values from the Map 'log'
    * and write down the new version of XML to the 'dstfile'.
    * 'log' can be empty, but must not be null.
    */
   public static void applyChanges( File srcfile, String dstfile, Map log ) throws FndException
   {
      Buffer buf = new StandardBuffer();
      parseXMLFile( srcfile, buf, true );
      Iterator itr = log.keySet().iterator();
      while( itr.hasNext() )
      {
         String name = (String)itr.next();
         Item item = buf.findItem(name);
         if( item!=null )
            item.setValue( log.get(name) );
      }
      // save the buffer back to a file
      AutoString out = bufferToXML(buf);
      try
      {
         Util.writeFile( dstfile, out.toString() );
      }
      catch( IOException x )
      {
         throw new FndException(x);
      }
   }

   private static AutoString bufferToXML( Buffer buf )
   {
      AutoString out = new AutoString();
      out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",NL);
      out.append("<webclientconfig>",NL);
      bufferToXML( buf, out, "  " );
      out.append("</webclientconfig>");
      return out;
   }

   private static void bufferToXML( Buffer buf, AutoString out, String indent )
   {
      int size = buf.countItems();
      for( int i=0; i<size; i++ )
      {
         Item   item  = buf.getItem(i);
         String name  = item.getName().toLowerCase();
         String attrs = item.getType();

         if( "#comment".equals(name) )
         {
            out.append(indent,"<!-- ");
            out.append( encodeNL(item.getString()) );
            out.append(" -->", NL);
            continue;
         }

         out.append(indent, "<",name);
         if( !Str.isEmpty(attrs) )
            out.append(" ", attrs);

         if( item.isCompound() )
         {
            AutoString subout = new AutoString();
            bufferToXML( item.getBuffer(), subout, indent+"  " );
            if( subout.length()==0 )
               out.append("/>",NL);
            else
            {
               out.append(">",NL);
               out.append(subout);
               out.append(indent, "</",name,">",NL);
            }
         }
         else
         {
            String value = item.getString();
            if( Str.isEmpty(value) )
               out.append("/>",NL);
            else
            {
               out.append('>');
               out.append( encode(value) );
               out.append("</",name,">",NL);
            }
         }
      }
   }

   /**
    * Encodes necessary special characters in a string. XML requires some
    * characters (such as '&') to be specially encoded when used as attribute
    * values.
    * Copied from ifs.fnd.record.serialization.FndXmlString and modified
    * @param   s  the string to encode.
    * @return a String with the special characters encoded.
    */
   private static AutoString encode( String s )
   {
      int len = s.length();
      AutoString result = new AutoString(len);
      char c;
      for( int i = 0; i < len; i++ )
      {
         c = s.charAt(i);
         switch(c)
         {
            case '&' :
               result.append("&amp;");
               break;
            /*
            case '\'' :
               result.append("&apos;");
               break;
            */
            case '>' :
               result.append("&gt;");
               break;
            case '<' :
               result.append("&lt;");
               break;
            case '"' :
               result.append("&quot;");
               break;
            case '\n' :
               result.append("&#xa;");
               break;
            case '\r' :
               break;
            default :
               result.append(c);
         }
      }
      return result;
   }

   private static AutoString encodeNL( String s )
   {
      int len = s.length();
      AutoString result = new AutoString(len);
      char c;
      for( int i = 0; i < len; i++ )
      {
         c = s.charAt(i);
         switch(c)
         {
            case '\n' :
               result.append(NL);
               break;
            case '\r' :
               break;
            default :
               result.append(c);
         }
      }
      return result;
   }

   private static void parseOneFile( File src, String dest ) throws Exception
   {
      Map map = new HashMap();
      map.put("APPLICATION/LOCATION/IMAGES_REL","jacek/tests");
      map.put("APPLICATION/LOCATION/NOEXISTING","should never be here");
      applyChanges( src, dest, map );
   }

   private static void syntax()
   {
      Util.debug("\n"+
                 "Syntax:\n"+
                 "  java ifs.fnd.asp.ConfigFileParser <source> <destination>\n\n"+
                 "where <source> and <destination> can be either file or directory.\n"+
                 "If <source> points a file, <destination> will be a new file,\n"+
                 "otherwise <source> can point out a directory containing XML files,\n"+
                 "then <destination> has to be a directory where result files will be created\n"+
                 "with the same names as in <source>\n");
      System.exit(1);
   }

   public static void main( String[] args ) throws Exception
   {
      //DEBUG = true;
      if( args.length!=2 ) syntax();

      File source = new File(args[0]);
      File dest   = new File(args[1]);

      if( source.isDirectory() && dest.isDirectory() )
      {
         File[] sources = source.listFiles();
         String destdir = dest.getAbsolutePath();
         if( destdir.charAt(destdir.length()-1)!=File.separatorChar )
            destdir = destdir + File.separatorChar;
         int len = sources.length;
         for( int i=0; i<len; i++ )
         {
            String name = sources[i].getName();
            if( sources[i].isFile() && name.toLowerCase().endsWith(".xml") )
               parseOneFile( sources[i], destdir+name );
         }
      }
      else if( source.isFile() && ( !dest.exists() || ( dest.isFile() && dest.canWrite() ) ) )
         parseOneFile( source, dest.getAbsolutePath() );
      else
         syntax();
   }
}

