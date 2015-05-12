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
 * File        : ASPGraph.java
 * Description : This is a small graph engine built into the webkit.
 * Notes       : Supports Columns, Pie, Line and Area Charts. Only 2D.
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Stefan M  2001-01-15 - Rewritten from scratch.
 *    Stefan M  2001-03-22 - Exception handling added. Labels fixed.
 *                           setColumnLabelsFont(Font) added.
 *    Stefan M  2001-04-25 - Two new chart types for negative values. graphPainter
 *                           interface for custom charts.
 *    Piotr  Z  2001-05-23 - Changed getGraph().
 *    Daniel S  2001-06-18 - Removed PowerChart(tm) dependency
 *    Daniel S  2001-07-03 - New graph types added. Almost totally rewritten.
 *    Daniel S  2001-07-11 - Added JavaDoc Comments.
 *    Daniel S  2002-10-09 - Added A new Legend position = 4.
 *    Rifki  R  2003-10-06 - Fixed AbstractChart.init() to avoid chance of infinite 
 *                           loops when calculating smart_interval.
 *    Chandana  2004-06-22 - Config parameter /GRAPH/FONTS/DEFAULT_FACE moved as per language.
 * ----------------------------------------------------------------------------
 * New Comments:
 * sumelk   2008-08-19 - Bug 76446, Changed drawIntervalAndGrid().   
 * sadhlk   2008-07-09 - Bug 73745, Added code to check DEBUG condition before calling debug() method.
 * sumelk   2008-06-26 - Bug 74086, Changed drawIntervalAndGrid().  
 * 
 * 2007/05/11 sumelk
 * Merged the corrections for Bug 65296, Changed JavaDoc comments to avoid broken links.
 *
 * Revision 1.2  2005/11/09 03:22:31  sumelk
 * Merged the corrections for Bug 53536, Changed drawIntervalAndGrid().
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.6  2005/08/08 09:44:04  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.5  2005/07/12 10:56:10  mapelk
 * Merged bug #51228
 *
 * Revision 1.4  2005/05/26 13:45:35  kirolk
 * Merged PKG14 changes to HEAD.
 *
 * Revision 1.3.2.1  2005/05/16 11:20:10  mapelk
 * Removed the posibility to having infinite loop
 *
 * Revision 1.3  2005/04/07 13:43:24  riralk
 * Removed sesion id from dyanamic cache key for business graphics since the cache is now in the session itself.
 *
 * Revision 1.2  2005/04/01 13:59:56  riralk
 * Moved Dynamic object cache to session - support for clustering
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.2  2004/12/15 11:04:03  riralk
 * Support for clustered environments by caching business graphics and generated javascript files in memory
 *
 * ----------------------------------------------------------------------------
 */

// TODO: The thrown FndExceptions should be unique.

package ifs.fnd.asp;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.image.*;
import ifs.fnd.os.*;

import com.sun.image.codec.jpeg.*;

/**
 * This class generates business graphics in PNG or JPG format.<br>
 * <br>
 * To create a graph, simply instance a new ASPGraph and specify the chart type.<br>
 * <p>Supported types are:
 * <ul>
 * <li>Column Chart (<a href="ASPGraph.html#COLUMNCHART">ASPGraph.COLUMNCHART</a>) 
 * <li>Stacked Column Chart (<a href="ASPGraph.html#STACKEDCOLUMNCHART">ASPGraph.STACKEDCOLUMNCHART</a>) 
 * <li>Line Chart (<a href="ASPGraph.html#LINECHART">ASPGraph.LINECHART</a>) 
 * <li>Pie Chart (<a href="ASPGraph.html#PIECHART">ASPGraph.PIECHART</a>) 
 * <li>Area Chart (<a href="ASPGraph.html#AREACHART">ASPGraph.AREACHART</a>) 
 * <li>Stacked Area Chart (<a href="ASPGraph.html#STACKEDAREACHART">ASPGraph.STACKEDAREACHART</a>) 
 * </ul></p>
 * <p>Populate data using any of <a href="ASPGraph.html#setData(int, double)">setData</a>, <a href="ASPGraph.html#setDataAt(int, int, double)">setDataAt</a>
 * or <a href="ASPGraph.html#setQuickData(java.lang.String)">setQuickData</a>.<br>
 * Specify any settings using set methods, for example <a href="ASPGraph.html#setGridColor(java.awt.Color)">setGridColor<br>
 * </a>Finally call <a href="ASPGraph.html#drawGraph()">drawGraph</a> or just use <a href="ASPGraph.html#getGraph()">getGraph</a>
 * which in turn will call drawGraph.
 * </p>
 */

public class ASPGraph 
{


   /** a chart type    */
   public static final int COLUMNCHART             =   100;
   /** a chart type    */
   public static final int LINECHART               =   101;
   /** a chart type    */
   public static final int STACKEDCOLUMNCHART      =   102;
   /** a chart type    */
   public static final int PIECHART                =   103;
   /** a chart type    */
   public static final int AREACHART               =   104;
   /** a chart type    */
   public static final int STACKEDAREACHART        =   105;
   /** a chart type. same as COLUMNCHART. */
   public static final int NEGATIVECOLUMNCHART     =   100;      //obsolete
   /** a chart type same as LINECHART.    */
   public static final int NEGATIVELINECHART       =   101;      //obsolete

   /** a constant for column label direction. */
   public static final int BEST_FIT   = 0;
   /** a constant for column label direction. */
   public static final int HORIZONTAL = 1;
   /** a constant for column label direction. */
   public static final int DIAGONAL   = 2;
   /** a constant for column label direction. */
   public static final int VERTICAL   = 3;

   /** a grid option constant. No grid lines at all.*/
   public static final int NO_GRID         = 0;
   /** a grid option constant. Both vertical and horizontal lines. */
   public static final int FULL_GRID       = 1;
   /** a grid option constant. Only horizontal grid lines. */
   public static final int HORIZONTAL_GRID = 2;
   /** a grid option constant. Only vertical grid lines.*/
   public static final int VERTICAL_GRID   = 3;

   /** a constant for line graph style. Better for overlays and short series*/
   public static final int CENTERED   = 0;
   /** a constant for line graph style. Better for long series*/
   public static final int FULL       = 1;


   /* NOT SUPPORTED constant for encoder type. */
//   public static final int GIF   = 0;  // not supported yet
   /** a constant for encoder type. supported. Use only on >256 color machines*/
   public static final int JPG   = 1;  // may look bad on 256 color machines
   /** a constant for encoder type. supported. Good packing. Use this. Default. */
   public static final int PNG   = 2;  // maximum pack rate

   private static int chart_number = 0;
   private final static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPGraph");

   private String tmploc;
   private String tmp_path;
   private String default_font_face;
   private boolean dyna_cache_enabled;
   
   private int encoder_type=2;
   private int this_set = 1;
   private AbstractChart chart;

   private boolean anti_aliasing = true;
   private boolean grid_on_top   = false;
   private Color pic_background = new Color(255,255,255);

   private ASPManager mgr;
   private ASPConfig cfg;
   private ASPLog log;

   private String filename;
   private String extension;

   private Frame frame;



/**
 * Constructs a new instance of ASPGraph.<br>
 * @param manager      the current ASPManager object.<br>
 * @param chart_type   any of the predefined constants.
 */

   public ASPGraph(ASPManager manager,int chart_type)
   {

      mgr = manager;
      cfg = mgr.getASPConfig();
      log = mgr.getASPLog();

      tmploc = ASPConfig.getGraphTempLocation();
      tmp_path = mgr.getPhyPath(tmploc);
      String user_lan = mgr.getUserPreferredLanguage();
      default_font_face = cfg.getParameter("LANGUAGE/"+user_lan+"/GRAPH/FONTS/DEFAULT_FACE","Arial");
      dyna_cache_enabled= "Y".equals(cfg.getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N"));

      if(chart_type==100)
      {
         chart = new ColumnChart();
      }
      else if(chart_type==101)
      {
         chart = new LineChart();
      }
      else if(chart_type==102)
      {
         chart = new StackedColumnChart();
      }
      else if(chart_type==103)
      {
         chart = new PieChart();
      }
      else if(chart_type==104)
      {
         chart = new AreaChart();
      }
      else if(chart_type==105)
      {
         chart = new StackedAreaChart();
      }
      if(DEBUG) debug("Constructing ASPGraph: type:" + chart.chart_type);


   }


/**
 * Create temporary image file located in directory specified
 * in configuration file. Remove other files located in the same
 * directory that are older the specified time out.<br>
 * This function is automatically called by getGraph(), so it
 * is not necessary to call it explicitly.
 * @return The file name without path and extension.
 * @see ifs.fnd.asp.ASPGraph#getGraph
 */
   public String drawGraph()
   {

      try
      {

         filename = "chart" + chart_number + "-" + System.currentTimeMillis();
         if(encoder_type==1)
            extension = ".jpg";
         else
            extension = ".png"; 
         if(chart_number < 9999)
            chart_number++;
         else
            chart_number = 0;

         ASPGraphCache.init();

         if(DEBUG && !dyna_cache_enabled) debug("saving image to " + tmp_path + OSInfo.OS_SEPARATOR + filename + extension);

         createGraph();
         
         //RIRALK:
         if (!dyna_cache_enabled)
           ASPGraphCache.put(filename+extension);                                    

         return filename;
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }


/**
 * Creates a graph.<br>
 * <br>
 * Note: Do not use this method unless you know what you are doing.<br>
 *       Use <code>ASPGraph.drawGraph</code> instead.
 *
 * @see ifs.fnd.asp.ASPGraph#drawGraph
 */

   public void createGraph()
   {
      try
      {
         int width = chart.width;
         int height = chart.height;

         BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
         Graphics2D g = img.createGraphics();

         if(anti_aliasing)
         {
            g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON));
            g.addRenderingHints(new RenderingHints(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE));
            g.addRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
         }

         g.setBackground(pic_background);
         g.clearRect(0,0,width,height);
         g.setColor(chart.foreground);
         if(chart.data.getRowCount()!=0 && chart.data.getColumnCount()!=0)
         {
            chart.init(g);
            chart.drawTitles(g);
            chart.drawIntervalAndGrid(g);
            chart.drawContent(g);
            if(grid_on_top)
            {
               chart.pass2=true;
               chart.drawIntervalAndGrid(g);
            }
         }
         else
         {
            chart.interval = 10;
            chart.max_value= 100;
            chart.min_value= 0;
            chart.start_x = (chart.left_title==null?0:chart.left_title_font.getSize()) + g.getFontMetrics(chart.unit_label_font).stringWidth("100 -");
            chart.slut_x = chart.width - 8;
            chart.column_width = (float)(chart.slut_x - chart.start_x)/5;  // 5 default columns
            chart.bottom = chart.height - (chart.bottom_title==null ? 0: chart.bottom_title_font.getSize()) - 8;
            chart.top = (chart.graph_title==null ? 8 : (chart.graph_title_font.getSize())+ 8);
            chart.middle_y = chart.bottom;

            chart.span = (double) (chart.max_value + Math.abs(chart.min_value));
            chart.data.setColumnCount(5);
            chart.drawTitles(g);
            chart.drawIntervalAndGrid(g);
//            g.drawString("NO DATA",width/2-40,height/2-5);
         }
         g.dispose();         
                  
         saveImage(img,tmp_path + OSInfo.OS_SEPARATOR + filename + extension);        
      }
      catch(Exception e)
      {
         error(e);
      }

   }

/**
 * To make a new chart type, subclass this
 * and add the new class to the types in ASPGraph (and in the constructor).
 *
 * All graph types extends this class. I hold all basic methods and settings.
 * along with the data model to hold both values,labels and colors.
 * Each new chart type can naturally have there own specific settings and methods.
 */
   abstract class AbstractChart
   {

// Data Storage
      public Vector labels                  = new Vector();
      public Vector legends                 = new Vector();
      public DefaultTableModel data         = new DefaultTableModel();
      public DefaultTableModel overlay_data = new DefaultTableModel();
      public DefaultTableModel temp_data    = new DefaultTableModel();
      public Vector row_colors              = new Vector();
      public Vector show_serie              = new Vector();
      public Vector show_overlay_serie      = new Vector();

// Settings
      public double min_value = 0;
      public double max_value = 0;
      public double interval  = 0;

      public int chart_type      = 100;
      public int palette         =   2;
      public int lightness       =   0;

      public int labels_format   = 0;
      public int legend_position = 0;
      public int value_decimals  = -1;
      public int label_count     = -1;
      public int every           = -1;
      public int unit_spacing    = 0;


      public boolean show_vertical_grid  = true;
      public boolean show_horizontal_grid= true;
      public boolean show_labels         = true;
      public boolean show_legends        = true;
      public boolean chart_outline       = true;
      public boolean show_values         = false;
      public boolean show_overlay_values = false;
      public boolean use_websafe_colors  = true;
      public boolean value_colors_set    = false;

      public Color foreground       = new Color(102,102,102);
      public Color grid_color       = new Color(204,204,204);;
      public Color chart_background = new Color(255,255,255);
      public Color value_color      = foreground;

      public int width  = 288;
      public int height = 288;

      public String graph_title;
      public String left_title;
      public String bottom_title;
      public String y_axis_desc;

      public Font y_axis_desc_font  = new Font(default_font_face,0,6);
      public Font label_font        = new Font(default_font_face,1,8);
      public Font legend_font       = new Font(default_font_face,1,8);
      public Font unit_label_font   = new Font(default_font_face,1,8);
      public Font left_title_font   = new Font(default_font_face,1,12);
      public Font graph_title_font  = new Font(default_font_face,1,12);
      public Font bottom_title_font = new Font(default_font_face,1,12);
      public Font value_font        = new Font(default_font_face,0,8);

//lines, overlays
      public float line_thickness = 1.0f;
      public int mark_type        = 0;
      public int mark_size        = 0;

      public Vector overlay_legends         = new Vector();
      public Vector overlay_colors          = new Vector();
      public boolean show_overlay           = true;

// no API, but changeable
      boolean center_labels = true;
      float top_fill_factor = 0.1f; 


// Internal variables 
      protected int start_x;
      protected int slut_x;
      protected int bottom;
      protected int top;
      protected double span;
      protected double half;
      protected double zero;
      protected int current_x;
      protected double middle_y;
      protected int overlay_centering_space = 0;
      protected int max_label_width = 0;
      protected int max_legend_width = 0;
      protected int cumulative_legend_width = 0;
      protected int current_legend_x= 0;
      protected int current_legend_y= 0;
      protected int col_label_space = 0;
      protected double angle        = 0;
      protected double column_width  = 0;
      protected boolean pass2       = false;
      protected boolean fixed_max   = false;
      protected boolean fixed_min   = false;
      protected boolean empty       = false;


// Methods

      // Draws the lines, bars, overlays, values etc.
      public abstract void drawContent(Graphics2D g);

      // Calculates and sets all the nessecery params for each chart.
      // make sure to call super.init() if you overrides it in your new 
      // chart type. This method is very complex!!
      public void init(Graphics2D g)
      {

         int label_width = label_font.getSize();
         int legend_width = 0;
         cumulative_legend_width=0;
         int cumulative_legend_height=0;

         try
         {
            if(label_count==0||labels.size()==0)
               show_labels=false;
            else
            {
               if(label_count<=0||label_count>=labels.size())
                  label_count=labels.size();
               if(every<=-1)
                  every=Math.round(labels.size()/label_count);
            }

            // Find the max label width.
            if(show_labels)
            {
               Enumeration lab = labels.elements();
               while(lab.hasMoreElements())
               {
                  String label = (String)lab.nextElement();
                  if(label != null)
                  {
                     label_width = g.getFontMetrics(label_font).stringWidth(label);
                     if(label_width > max_label_width)
                        max_label_width = label_width;
                  }
               }
            }
            // Find the max legend width.
            if((legends.size()>0)&&show_legends)
            {
               int the_row=1;
               while(the_row<=legends.size())
               {
                  String legend = (String)legends.elementAt(the_row-1);
                  if(legend != null)
                  {
                     legend_width = (int)(g.getFontMetrics(legend_font).stringWidth(legend)+legend_font.getSize()*2);
                     cumulative_legend_width += legend_width;
                     cumulative_legend_height += (int)Math.round(legend_font.getSize()*1.5);
                     if(legend_width > max_legend_width)
                        max_legend_width = legend_width;
                  }
                  the_row++;
               }
            }
            // Find the max overlay legend width.
            if(overlay_legends.size()>0&&show_overlay)
            {
               int the_row=1;
               while(the_row<=overlay_legends.size())
               {
                  String legend = (String)overlay_legends.elementAt(the_row-1);
                  if(legend != null)
                  {
                     legend_width = (int)(g.getFontMetrics(legend_font).stringWidth(legend)+legend_font.getSize()*2);
                     cumulative_legend_width += legend_width;
                     cumulative_legend_height += (int)Math.round(legend_font.getSize()*1.5);
                     if(legend_width > max_legend_width)
                        max_legend_width = legend_width;
                  }
                  the_row++;
               }
            }

            //init palette
            initColors(data.getRowCount(),lightness,row_colors);
            initColors(overlay_data.getRowCount(),lightness-60,overlay_colors); // a bit darker

            // determine coordinates/positions for different components in the chart.

            span = (double) (max_value + Math.abs(min_value));

            start_x = (left_title==null?0:left_title_font.getSize()) + g.getFontMetrics(unit_label_font).stringWidth((span>=6?stringOf((double)span,0)+"-0":stringOf((double)span,(int)Math.ceil(Math.abs((double)(Math.log(span)/Math.log(10))))) +"-00")); //skapa plats till units labels
            if(legend_position==0)
            {
               if(cumulative_legend_width>(width-start_x-label_width))
               {
                  legend_position = 1;
               }
               else
               {
                  legend_position = 2;
               }
            }

            if(legend_position == 1)
            {
               slut_x = width - (legends.size()>0?max_legend_width+(int)(legend_font.getSize()*0.5):label_width);
            }
            else
            {
               slut_x = width - label_width;
            }


            column_width = (float)(slut_x - start_x)/data.getColumnCount();

            overlay_centering_space = (int)(column_width/2);


            // SMART LABELS            

            if(column_width/(max_label_width+5)<=1)
            {
               angle = Math.abs(Math.acos(column_width/(max_label_width+5)));
            }
            else if(labels_format==BEST_FIT)
            {
               labels_format=HORIZONTAL;
            }
            col_label_space = (int)(max_label_width*Math.sin(angle)+label_font.getSize()/2);
            if(Math.toDegrees(angle)>65)
            {
               labels_format=VERTICAL;
            }

            if(labels_format == HORIZONTAL)
               col_label_space = label_font.getSize();
            else if(labels_format == VERTICAL)
            {
               angle = Math.toRadians(90);
               col_label_space = max_label_width;
            }
            else if(labels_format == DIAGONAL)
            {
               angle = Math.toRadians(45);
               col_label_space = (int)(max_label_width*Math.sin(angle)+label_font.getSize()/2);
            }

            // determine interval and scale

            bottom = height - (bottom_title==null ? 0: bottom_title_font.getSize()) - col_label_space - 8 ;
            top = (graph_title==null ? 1 : (graph_title_font.getSize()))+ (legend_position==2?5:legend_font.getSize());

            current_legend_x = slut_x-cumulative_legend_width;
            if(legend_position==4)
            {
                bottom -= (cumulative_legend_height+8);
                if (bottom<0)                                
                {                                         //too many legends at the bottom,
                   bottom+=(cumulative_legend_height+8);  //acquire space taken by legends, and move them to right of chart.
                   legend_position=1;                        
                }
                current_legend_y = height - (cumulative_legend_height+8);
            }
            else
            {
                current_legend_y = top+2;
            }

            if(max_value!=0&&!fixed_max)
               max_value += (value_font.getSize()*(show_values?2:1)+1)*span/(bottom-top);
            if(min_value!=0&&!fixed_min)
               min_value -= (value_font.getSize()*(show_values?2:1)+1)*span/(bottom-top);

            if((max_value==min_value))      // in case all data = zero or null
               max_value=min_value+100;


            span = (double) (max_value + Math.abs(min_value));
            zero = (double) (max_value / span);

            if(legend_position == 2)
            {
               top+=legend_font.getSize();
            }
            if(min_value >= 0)
               middle_y = bottom;
            else
               middle_y    = (int) (top + (zero * (bottom - top)));
            current_x = start_x;
            double smart_interval;
            smart_interval=(double)((max_value-min_value)/((bottom-top)/(unit_label_font.getSize()*2+unit_spacing)));

            double expo=1.0d;
            
            if (smart_interval>0)  //avoid infinite loop
            {
              while(true)
              {
                if((smart_interval*expo)<0.5)
                {
                   expo=expo*10;
                   continue;
                }
                else if((smart_interval*expo)>=5)
                {
                   expo=expo/10;
                   continue;
                }
                else
                {
                   if((smart_interval*expo<=5)&&(smart_interval*expo>=2))
                   {
                     smart_interval=5;
                     break;
                   }
                   else
                   {
                     smart_interval=1;
                     break;
                   }
                }
              }
            }
            else
              smart_interval=10;  //just a guess to avoid unpredictable behaviour
            
            interval=(interval==0?(double)((int)smart_interval/expo):interval);


         }
         catch(Exception e)
         {
            error(e);
         }
      }


      // Draws chart background.
      // Note: If pass2 is true only grid and axis will be drawn. 
      //       use this to place grids on top.

      public void drawIntervalAndGrid(Graphics2D g)
      {
         try
         {
            if(!pass2)
            {
               // Chart boundaries
               g.setColor(chart_background);
               g.fillRect(start_x,top,slut_x-start_x,bottom-top);

            }

            BasicStroke dotted = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 10.0f, new float[]{2.1f}, 0.0f);
            BasicStroke solid  = new BasicStroke(1.0f);

            double my_value;
            int dec = (int)Math.abs(Math.log(interval)/Math.log(10))+1; //calculate number of decimals.

            g.setFont(chart.unit_label_font);
            FontMetrics metrics = g.getFontMetrics(chart.unit_label_font);
            g.setColor(foreground);
            double unit_y, old_unit_y = bottom; //mapelk: old_unit_y is added to avoid infinite loops
            int nr = 0;
            int count = 0;
            while(true)
            {
               Double value = new Double(nr * interval);
               unit_y = middle_y - ((value.doubleValue() / span) * (bottom - top));
               count++;

               if((unit_y < top) || (unit_y>=old_unit_y && count>50))    
                  break;
               old_unit_y = unit_y;

               my_value = (double)(nr*interval);

               if(show_horizontal_grid)
               {
                  g.setStroke(dotted);
                  g.setColor(grid_color);
                  g.drawLine(start_x,(int)unit_y,slut_x,(int)unit_y);
                  g.setStroke(solid);
               }
               g.setColor(foreground);
               if(!pass2)
               {

                  if(interval>=1)
                     g.drawString(stringOf(my_value,0),start_x - metrics.stringWidth(stringOf(my_value,0)) - 3,(int)unit_y+3);   // unit text
                  else
                     g.drawString(stringOf(my_value,dec),start_x - metrics.stringWidth(stringOf(my_value,dec)) - 3,(int)unit_y+3); // unit text
               }
               g.drawLine(start_x-2,(int)unit_y,start_x+2,(int)unit_y);                    // skal streck
               nr++;
            }
            nr = 0;
            old_unit_y = unit_y;
            count = 0;
            while(true)
            {
               Double value = new Double(nr * interval);
               unit_y = middle_y + ((Math.abs(value.doubleValue()) / span) * (bottom - top));
               count++;

               if((unit_y > bottom) || (unit_y>=old_unit_y && count>50))
                  break;

               old_unit_y = unit_y;
               my_value = (double)(nr*interval);


               if(show_horizontal_grid)
               {
                  g.setStroke(dotted);
                  g.setColor(grid_color);
                  g.drawLine(start_x,(int)unit_y,slut_x,(int)unit_y);
                  g.setStroke(solid);
               }
               g.setColor(foreground);
               if(!pass2)
               {
                  if(interval>=1)
                     g.drawString(stringOf(my_value,0),start_x - metrics.stringWidth(stringOf(my_value,0)) - 3,(int)unit_y+3);   // unit text
                  else
                     g.drawString(stringOf(my_value,dec),start_x - metrics.stringWidth(stringOf(my_value,dec)) - 3,(int)unit_y+3); // unit text
               }
               g.drawLine(start_x-2,(int)unit_y,start_x+2,(int)unit_y);                    // skal streck

               nr--;
            }
            for(int i=1;i<data.getColumnCount();i++)
            {
               if((((i-1)%every)==0))
               {
                  if(show_vertical_grid)
                  {
                     g.setStroke(dotted);
                     g.setColor(grid_color);
                     g.drawLine((int)(start_x+i*column_width),top,(int)(start_x+i*column_width),bottom);
                  }
                  g.setStroke(solid);
                  g.setColor(foreground);
                  g.drawLine((int)(start_x+i*column_width),(int)(middle_y)-2,(int)(start_x+i*column_width),(int)(middle_y)+2);
               }
            }
            g.setColor(foreground);
            if(chart_outline)
            {
               g.drawRect(start_x,top,slut_x-start_x,bottom-top);
            }
            g.drawLine(start_x,(int)(middle_y),slut_x,(int)(middle_y));
            g.drawLine(start_x,top,start_x,bottom);
            if(!pass2)
            {
               if(y_axis_desc != null)
               {
                  g.setFont(chart.y_axis_desc_font);
                  int textlen = metrics.stringWidth(y_axis_desc);
                  g.drawString(y_axis_desc,start_x - textlen/2,top-y_axis_desc_font.getSize());  
               }
            }
         }
         catch(Exception e)
         {
            error(e);
         }
      }

      // Basic method, Will draw the titles.

      public void drawTitles(Graphics2D g)
      {
         // Draw arena
         try
         {
            if(graph_title != null)
            {
               g.setColor(foreground);
               g.setFont(graph_title_font);
               int title_width = g.getFontMetrics().stringWidth(graph_title);
               g.drawString(graph_title,start_x + ((slut_x - title_width - start_x) / 2),graph_title_font.getSize());
            }
            if(bottom_title != null)
            {
               g.setColor(foreground);
               g.setFont(bottom_title_font);
               int bottom_title_width = g.getFontMetrics().stringWidth(bottom_title);
               g.drawString(bottom_title,start_x + ((slut_x - bottom_title_width - start_x) / 2),height-3 );
            }
            if(left_title != null)
            {
               int left_title_width = g.getFontMetrics().stringWidth(left_title);
               g.rotate(Math.toRadians(-90));
               g.setFont(left_title_font);
               g.setColor(foreground);
               g.drawString(left_title,(int) ((bottom-top) / 2 + top +left_title_width/2) * -1,left_title_font.getSize());
               g.rotate(Math.toRadians(90));
            }
         }
         catch(Exception e)
         {
            error(e);
         }
      }



      // Draws a single label; can be re-used by subclasses in their drawContent().
      // the_col = the column number
      protected void drawColumnLabel(int the_col,Graphics2D g)
      {
         if((labels.size() > the_col)&&show_labels&&((((the_col-1)%every)==0)))
         {
            String label = (String)labels.elementAt(the_col);
            if(label==null)
               label="";
            g.setFont(label_font);
            int label_width = g.getFontMetrics().stringWidth(label);
            int label_height= label_font.getSize();
            g.setColor(foreground);



            // Draw label
            if(labels_format == HORIZONTAL )
            {
               int centering_space = (center_labels ? (int)(column_width/2 - label_width / 2):0);
               Point2D.Double d = new Point2D.Double(start_x+(the_col-1)*column_width+centering_space,bottom+label_height);
               g.drawString(label,(int)d.x,(int)d.y);
            }
//             else if(labels_format == VERTICAL )
//             {
//                int centering_space = (center_labels ? (int)(column_width/2 - label_height / 3):0);
//                Point2D.Double p = new Point2D.Double(start_x+(the_col-1)*column_width+centering_space,bottom+label_width);
//                g.rotate(-angle,p.x,p.y);
//                g.drawString(label,(int)p.x,(int)p.y);
//                g.rotate(angle,p.x,p.y);
//             }
            else
            {
               int centering_space = (center_labels ? (int)(column_width/2 - label_height / 3):0);
               Point2D.Double p = new Point2D.Double(start_x+(the_col-1)*column_width+centering_space,bottom+label_height);
               g.rotate(angle,p.x,p.y);
               g.drawString(label,(int)p.x,(int)p.y);
               g.rotate(-angle,p.x,p.y);
            }

         }
      }

      // Draw a legend

      protected void drawLegend(int the_row ,Graphics2D g)
      {
         if((legends.size() > the_row)&&show_legends)
         {
            int l_x;
            int l_y;
            String legend = (String)legends.elementAt(the_row);
            if(legend==null)
               return;
            g.setFont(legend_font);
            int legend_width = g.getFontMetrics().stringWidth(legend);
            int legend_height= legend_font.getSize();
            if(legend_position==2)
            {
               l_x = current_legend_x;
               l_y = top-legend_height-2;
               current_legend_x += (legend_width+(int)Math.round(legend_height*2));
            }
            else if(legend_position==3)
            {
               l_x = slut_x-max_legend_width;
               l_y = current_legend_y;
               current_legend_y +=(int)Math.round(legend_height*1.5);
            }
            else if(legend_position==4)
            {
               l_x = start_x;
               l_y = current_legend_y;
               current_legend_y +=(int)Math.round(legend_height*1.5);
            }
            else
            {
               l_x = slut_x+(int)Math.round(legend_height*0.5);
               l_y = current_legend_y;
               current_legend_y +=(int)Math.round(legend_height*1.5);
            }
            g.setColor((Color)row_colors.elementAt(the_row-1));
            g.fillRect(l_x,l_y,legend_height,legend_height);
            g.setColor(foreground);

            g.drawString(legend,l_x+(int)Math.round(legend_height*1.5),l_y+legend_height);

         }
      }

      // Draw a legend for overlay series

      protected void drawOverlayLegend(int the_row ,Graphics2D g)
      {

         if((overlay_legends.size() > the_row)&&show_legends)
         {
            int l_x;
            int l_y;
            String legend = (String)overlay_legends.elementAt(the_row);
            if(legend==null)
               return;
            g.setFont(legend_font);
            int legend_width = g.getFontMetrics().stringWidth(legend);
            int legend_height= legend_font.getSize();
            if(legend_position==2)
            {
               l_x = current_legend_x;
               l_y = top-legend_height-2;
               current_legend_x += (legend_width+(int)Math.round(legend_height*2));
            }
            else if(legend_position==3)
            {
               l_x = slut_x-max_legend_width;
               l_y = current_legend_y;
               current_legend_y +=(int)Math.round(legend_height*1.5);
            }
            else if(legend_position==4)
            {
               l_x = start_x;
               l_y = current_legend_y;
               current_legend_y +=(int)Math.round(legend_height*1.5);
            }
            else
            {
               l_x = slut_x+(int)Math.round(legend_height*0.5);
               l_y = current_legend_y;
               current_legend_y +=(int)Math.round(legend_height*1.5);
            }
            g.setColor((Color)overlay_colors.elementAt(the_row-1));
            BasicStroke stroke = new BasicStroke(2);
            g.setStroke(stroke);
            g.drawLine(l_x,l_y,l_x+legend_height,l_y+legend_height);
            stroke = new BasicStroke(line_thickness);
            g.setStroke(stroke);
            g.setColor(foreground);

            g.drawString(legend,(int)Math.round(l_x+legend_height*1.5),l_y+legend_height);

         }
      }

      //Draw a line using overlay data serie. Use Line settings.

      public void drawOverlay(Graphics2D g)
      {
         BasicStroke stroke = new BasicStroke(line_thickness);
         g.setStroke(stroke);

         double last_x = 0;
         double last_y = 0;
         double last_v = 0;

         int the_row = 1;
         int the_col = 1;
         while(the_row<=overlay_data.getRowCount())
         {
//            drawOverlayLegend(the_row,g);
            the_col = 1;
            while(the_col<=overlay_data.getColumnCount())
            {
               current_x = (int)(start_x+(the_col-1)*column_width+overlay_centering_space);

               // No Labels on overlay
               //	drawColumnLabel(the_col,(int)(start_x+(column_width*(the_col-1))),g);


               Number nr = (Number)overlay_data.getValueAt(the_row-1,the_col-1);
               if(nr == null)
               {
                  the_col++;
                  last_x=0;
                  last_y=0;
                  last_v=0;
                  continue;
               }
               double value = nr.doubleValue();
               double col_y;
               if(value < 0)
                  col_y = middle_y + ((Math.abs(value) / span) * (bottom - top));
               else
                  col_y   = middle_y - ((value / span) * (bottom - top));

               Color c = (Color)(overlay_colors.elementAt(the_row-1));
               g.setColor(c);
               if(last_x > 0)
               {
                  if(line_thickness>0)
                     g.drawLine((int)last_x,(int)last_y,current_x,(int)col_y);

               }
               int plupp_size = (mark_size==0?(int)(column_width*0.1):mark_size);
               switch(mark_type)
               {
               case 0:
                  plupp_size=1;
                  break;
               case 1:
                  g.drawRoundRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size,plupp_size,plupp_size);
                  break;
               case 2:
                  g.fillRoundRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size,plupp_size,plupp_size);
                  break;
               case 3:
                  g.drawRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size);
                  break;
               case 4:
                  g.fillRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size);
                  break;
               default:
                  ;
               }
               if(show_overlay_values||showOverlaySerie(the_row))
               {
                  g.setColor(value_color);
                  g.setFont(value_font);
                  String string_value = stringOf(value,value_decimals);
                  int string_value_length =  g.getFontMetrics().stringWidth(string_value);
                  if(last_v<value)
                  {
                     g.drawString(string_value,(int)(current_x-string_value_length/2),(int)(col_y-1-plupp_size/2));   // value text
                  }
                  else
                  {
                     g.drawString(string_value,(int)(current_x-string_value_length/2),(int)(col_y+value_font.getSize()+1+plupp_size/2));   // value text
                  }
               }


               last_x = current_x;
               last_y = col_y;
               last_v = value;

               the_col++;
            }
            current_x = start_x;
            last_x = 0;
            last_y = 0;
            the_row++;


         }

      }

      // Use this to make a string from a double.
      // value    = the value to be expressed
      // decimals = the number of digits shown after the dot.

      protected String stringOf(double value,int decimals)
      {
         String returstr;
         if(decimals>0)
         {
            int factor = (int)(Math.pow(10.0,(double)decimals));
            returstr = (double)(Math.round(factor*value))/factor+"";
         }
         else if(decimals<0)
         {
            if(((value==0.0) || Math.round(value)/value==1))
               returstr = (int)value+"";
            else
               returstr =  value+"";
         }
         else
         {
            returstr = (int)Math.round(value)+"";
         }
         // Java fix, values from 0.001 to 0.01 has an extra 0 at the end. Remove this.
         if((Math.abs(value)<1) && (value!=0.0) && (returstr.charAt(returstr.length()-1)=='0'))
         {
            returstr = returstr.substring(0,returstr.length()-1);
         }
         return returstr;
      }


      protected boolean showSerie(int serie)
      {
         if(chart.show_serie.size() <= serie)
            return false;
         else if(chart.show_serie.elementAt(serie) == null)
            return false;
         else if(((Boolean)chart.show_serie.elementAt(serie)).booleanValue() == false)
            return false;
         return true;
      }

      protected boolean showOverlaySerie(int serie)
      {
         if(chart.show_overlay_serie.size() <= serie)
            return false;
         else if(chart.show_overlay_serie.elementAt(serie) == null)
            return false;
         else if(((Boolean)chart.show_overlay_serie.elementAt(serie)).booleanValue() == false)
            return false;
         return true;
      }
      // bound color values and round them to websafe palette.

      protected int inb(int color)
      {
         if(color>255)
            return 255;
         if(color<0)
            return 0;
         if(use_websafe_colors)
            for(int i=0;i<=255;i+=51)
               if(Math.abs(i-color)<26)
                  return i;
         return color;
      }

      // Invert the color. This one is never used.

      protected Color colorInv(Color color)
      {
         return new Color(255-color.getRed(),color.getGreen(),255-color.getBlue());
      }

      // Set the palette

      protected void initColors(int nr,int rel,Vector colors)
      {

         if(colors.size()==0)
         {
            if(palette==2)
            {
               colors.add(new Color(inb(5+rel),inb(68+rel),inb(119+rel)));
               colors.add(new Color(inb(206+rel),inb(54+rel),inb(59+rel)));
               colors.add(new Color(inb(0+rel),inb(151+rel),inb(93+rel)));
               colors.add(new Color(inb(186+rel),inb(141+rel),inb(57+rel)));
               colors.add(new Color(inb(174+rel),inb(13+rel),inb(75+rel)));
               colors.add(new Color(inb(0+rel),inb(94+rel),inb(88+rel)));
               colors.add(new Color(inb(216+rel),inb(124+rel),inb(13+rel)));
               colors.add(new Color(inb(54+rel),inb(35+rel),inb(121+rel)));
            }
            else if(palette==3)
            {
               colors.add(new Color(inb(150+rel),inb(150+rel),inb(150+rel)));
               colors.add(new Color(inb(50+rel),inb(50+rel),inb(50+rel)));
               colors.add(new Color(inb(100+rel),inb(100+rel),inb(100+rel)));
               colors.add(new Color(inb(200+rel),inb(200+rel),inb(200+rel)));
               colors.add(new Color(inb(25+rel),inb(25+rel),inb(25+rel)));
               colors.add(new Color(inb(125+rel),inb(125+rel),inb(125+rel)));
               colors.add(new Color(inb(175+rel),inb(175+rel),inb(175+rel)));
               colors.add(new Color(inb(75+rel),inb(75+rel),inb(75+rel)));
            }
            else if(palette==4)
            {
               colors.add(new Color(inb(0+rel),inb(0+rel),inb(255+rel)));
               colors.add(new Color(inb(0+rel),inb(255+rel),inb(0+rel)));
               colors.add(new Color(inb(0+rel),inb(255+rel),inb(255+rel)));
               colors.add(new Color(inb(255+rel),inb(0+rel),inb(0+rel)));
               colors.add(new Color(inb(255+rel),inb(0+rel),inb(255+rel)));
               colors.add(new Color(inb(255+rel),inb(255+rel),inb(0+rel)));
            }
            else
            {
               colors.add(new Color(inb(50+rel),inb(50+rel),inb(200+rel)));
               colors.add(new Color(inb(200+rel),inb(50+rel),inb(200+rel)));
               colors.add(new Color(inb(50+rel),inb(200+rel),inb(50+rel)));
               colors.add(new Color(inb(200+rel),inb(200+rel),inb(0+rel)));
               colors.add(new Color(inb(0+rel),inb(200+rel),inb(200+rel)));
               colors.add(new Color(inb(200+rel),inb(100+rel),inb(0+rel)));
               colors.add(new Color(inb(150+rel),inb(0+rel),inb(200+rel)));
               colors.add(new Color(inb(200+rel),inb(200+rel),inb(200+rel)));
            }
         }

         int declared = colors.size();

         for(int i=0;i<declared;i++)
         {
            Color c = (Color)(colors.elementAt(i));
            colors.add(new Color(inb(c.getRed()+51),inb(c.getGreen()+51),inb(c.getBlue()+51)));
         }
         for(int i=0;i<declared;i++)
         {
            Color c = (Color)(colors.elementAt(i));
            colors.add(new Color(inb(c.getRed()-51),inb(c.getGreen()-51),inb(c.getBlue()-51)));
         }
         if(nr>(declared*3))
         {
            for(int i=declared*3;i<nr;i++)
            {
               colors.add(new Color(inb((int) (Math.random()*250)),inb((int) (Math.random()*250)),inb((int) (Math.random()*250))));
            }
         }
      }
   }


/**
 * A column chart type that supports negative values.
 *
 * NOTE: There is only one column chart type. 
 * It supports both positive and negative values.
 */

   class ColumnChart extends AbstractChart
   {

// Settings special for ColumnChart

      int row_width =  0;
      int row_space =  0;

      float graph_fill_factor = (float)0.8;   //default 20% space.
      Color outline_color     = new Color(0,0,0);
      boolean column_outline  = false;
      boolean column_color_mode= false;

// Methods

      public ColumnChart()
      {
         this.chart_type=100;
      }

      public void init(Graphics2D g)
      {
         try
         {
            // inherite all settings from the super class.
            super.init(g);
            // set some special to column chart
            row_width = (row_width == 0 ? (int)(column_width*graph_fill_factor/data.getRowCount()) : row_width);
            row_space = (row_space == 0 ? 0 : row_space); // not needed but easy to change.

         }
         catch(Exception e)
         {
            error(e);
         }
      }

      // This is the method every chart must override. It draws the content of the chart.

      public void drawContent(Graphics2D g)
      {
         try
         {
            int group_width = row_width*data.getRowCount()+row_space*(data.getRowCount()-1);
            int centering_space = (int)(column_width/2 - group_width / 2);
            int the_col = 1;
            int the_row = 1;
            while(the_col<=data.getColumnCount())
            {

               current_x = (int)(start_x+(the_col-1)*column_width+centering_space);

               drawColumnLabel(the_col,g);

               the_row = 1;
               while(the_row<=data.getRowCount())
               {

//                   if(column_color_mode)
//                   {
//                      if(the_row==1)
//                         drawLegend(the_col,g);
//                   }
//                   else if(the_col==1)
//                      drawLegend(the_row,g);

                  double value=0;
                  try
                  {
                     value = ((Double)(chart.data.getValueAt(the_row-1,the_col-1))).doubleValue(); 
                     //value = ((Number)data.getValueAt(the_row,the_col)).intValue();

                  }
                  catch(Exception e)
                  {
                     current_x = current_x + row_width+row_space;
                     the_row++;
                     continue;
                  }

                  double column_top;
                  double column_bottom;
                  if(value < 0)
                  {
                     column_top = middle_y;
                     column_bottom = (double)(middle_y + ((Math.abs(value) / span) * (bottom - top)));
                  }
                  else
                  {
                     column_bottom = middle_y;
                     column_top = (double)(middle_y - ((value / span) * (bottom - top)));
                  }
                  Color c;
                  if(column_color_mode)
                     c = (Color)(row_colors.elementAt(the_col-1));
                  else
                     c = (Color)(row_colors.elementAt(the_row-1));
                  g.setColor(c);
                  if(value!=0)
                  {
                     g.fillRect(current_x,(int)column_top,(int)row_width,(int)(1+column_bottom - column_top));
                     if(column_outline)
                     {
                        g.setColor(outline_color);
                        g.drawRect(current_x,(int)column_top,(int)row_width,(int)(1+column_bottom - column_top));
                     }
                  }
                  if((show_values)||showSerie(the_row))
                  {
                     g.setColor(value_color);
                     g.setFont(value_font);
                     String string_value = stringOf(value,value_decimals);
                     int string_value_length =  g.getFontMetrics().stringWidth(string_value);
                     if(value>=0)
                     {
                        g.drawString(string_value,(int)(current_x+row_width/2-string_value_length/2),(int)column_top-1);   // value text
                     }
                     else
                     {
                        g.drawString(string_value,(int)(current_x+row_width/2-string_value_length/2),(int)column_bottom+value_font.getSize()+1);   // value text
                     }
                  }

                  current_x = current_x + row_width+row_space;
                  the_row++;
               }
               the_col++;
            }
            if(show_overlay)
            {
               drawOverlay(g);
            }

            for(int i=1;i<=legends.size();i++)
               drawLegend(i,g);

            for(int i=1;i<=overlay_legends.size();i++)
               drawOverlayLegend(i,g);




         }
         catch(Exception e)
         {
            error(e);
         }
      }

   }
/**
 *
 * A line chart type that supports negative values.
 */
   class LineChart extends AbstractChart
   {
      // Line chart settings
      public int line_chart_type  = 1;

      // Internal variable
      protected int centering_space = 0;

      public LineChart()
      {
         this.chart_type=101;
      }


      public void init(Graphics2D g)
      {
         // Inherits all the normal settings from AbstractChart.
         super.init(g);
         centering_space = (int)(column_width/2);

         if(line_chart_type==1)
         {
            column_width = (float)(slut_x - start_x)/(data.getColumnCount()-1);
            center_labels = false;
            centering_space=0;
            overlay_centering_space=0;
         }
      }

      public void drawContent(Graphics2D g)
      {
         BasicStroke stroke = new BasicStroke(line_thickness);
         g.setStroke(stroke);

         double last_x = 0;
         double last_y = 0;
         double last_v = 0;

         int the_row = 1;
         int the_col = 1;
         while(the_row<=data.getRowCount())
         {
            the_col = 1;
            while(the_col<=data.getColumnCount())
            {
               current_x = (int)(start_x+(the_col-1)*column_width+centering_space);

               // Label
               if(the_row==1)
                  drawColumnLabel(the_col,g);

               Number nr = (Number)data.getValueAt(the_row-1,the_col-1);
               if(nr == null)
               {
                  the_col++;
                  last_x=0;
                  last_y=0;
                  last_v=0;
                  continue;
               }
               double value = nr.doubleValue();
               double col_y;
               if(value < 0)
                  col_y = middle_y + ((Math.abs(value) / span) * (bottom - top));
               else
                  col_y   = middle_y - ((value / span) * (bottom - top));

               Color c = (Color)(row_colors.elementAt(the_row-1));
               g.setColor(c);
               if(last_x > 0)
               {
                  drawIt((int)last_x,(int)last_y,current_x,(int)col_y,g);   //special function that can be overwritten i children to this class
               }
               int plupp_size = (mark_size==0?(int)(column_width*0.1):mark_size);
               switch(mark_type)
               {
               case 0:
                  plupp_size = 1;
                  break;
               case 1:
                  g.drawRoundRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size,plupp_size,plupp_size);
                  break;
               case 2:
                  g.fillRoundRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size,plupp_size,plupp_size);
                  break;
               case 3:
                  g.drawRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size);
                  break;
               case 4:
                  g.fillRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size);
                  break;
               default:
                  ;
               }
               if((show_values)||showSerie(the_row))
               {
                  g.setColor(value_color);
                  g.setFont(value_font);
                  String string_value = stringOf(value,value_decimals);
                  int string_value_length =  g.getFontMetrics().stringWidth(string_value);
                  int x_c;
                  if(the_col==1)
                     x_c = (int)(current_x+2);
                  else if(the_col==data.getColumnCount())
                     x_c = (int)(current_x-string_value_length-1);
                  else
                     x_c = (int)(current_x-string_value_length/2);

                  if(last_v<value)
                  {
                     g.drawString(string_value,x_c,(int)(col_y-1-plupp_size/2));   // value text
                  }
                  else
                  {
                     g.drawString(string_value,x_c,(int)(col_y+value_font.getSize()+1+plupp_size/2));   // value text
                  }
               }


               last_x = current_x;
               last_y = col_y;
               last_v = value;

               the_col++;
            }
            current_x = start_x;
            last_x = 0;
            last_y = 0;
            the_row++;

         }
         if(show_overlay)
         {
            drawOverlay(g);
         }

         for(int i=1;i<=legends.size();i++)
            drawLegend(i,g);

         for(int i=1;i<=overlay_legends.size();i++)
            drawOverlayLegend(i,g);


      }

      protected void drawIt( int l_x,int l_y,int c_x, int c_y,Graphics2D g)  //special function that can be overwritten i children to this class
      {
         if(line_thickness>0)
            g.drawLine(l_x,l_y,c_x,c_y);
      }

   }           
/**
 * An area chart type. A line chart drawn with polygons instead of lines.
 */
   class AreaChart extends LineChart
   {

      public AreaChart()
      {
         this.chart_type=104;
      }

      protected void drawIt( int l_x,int l_y,int c_x, int c_y,Graphics2D g)  //special function that can be overwritten i children to this class
      {
         int xs[] = {l_x,c_x,c_x,l_x};
         int ys[] = {l_y,c_y,(int)middle_y,(int)middle_y};

         g.fillPolygon(xs,ys,xs.length);
      }

   }
/**
 * A stacked area chart type.
 */
   class StackedAreaChart extends AreaChart
   {
      // Internal variable

      public StackedAreaChart()
      {
         this.chart_type=105;
      }


      public void init(Graphics2D g)
      {
         // Inherits all the normal settings from AbstractChart.

         double local_interval = interval;

         super.init(g);
         interval = local_interval;

         centering_space = (int)(column_width/2);
         double total_max_value = (double)max_value;
         if(!fixed_min)
            min_value = 0f;
         double total_min_value = (double)min_value;
         int the_row = 1;
         int the_col = 1;
         while(the_col<=data.getColumnCount())
         {

            // Find the total (stacked) value of this column
            double local_max_value = 0;
            double local_min_value = 0;
            the_row = 1;
            while(the_row<=data.getRowCount())
            {

               double value = 0;
               try
               {
                  value = ((Double)(chart.data.getValueAt(the_row-1,the_col-1))).doubleValue(); 
                  //value = ((Number)data.getValueAt(the_row,the_col)).intValue();

               }
               catch(Exception e)
               {
                  ;
               }
               local_max_value = local_max_value + Math.abs(value);
               the_row++;
            }
            if(local_max_value>total_max_value&&!fixed_max)
               total_max_value = local_max_value;
            chart.temp_data.setRowCount(1);
            if(the_col>chart.temp_data.getColumnCount())
               chart.temp_data.setColumnCount(the_col);
            chart.temp_data.setValueAt(new Double(local_max_value),0,the_col-1);
            the_col++;
         }
         //store the max values in the temp data object. Invisible!


         if(!fixed_max)
            total_max_value = total_max_value*(double)(1+top_fill_factor);

         span = (double) (total_max_value + Math.abs(total_min_value));
         half = (double) (span - total_max_value);

         zero = (double) (total_max_value / span);
         if(total_min_value >= 0)
            middle_y = bottom;
         else
            middle_y    = (int) (top + (zero * (bottom - top)));
         double smart_interval;
         smart_interval=(double)((total_max_value-total_min_value) /   (   (bottom-top)  /  (unit_label_font.getSize()*2) ) );
         float expo=1.0f;
         while(true)
         {
            if((smart_interval*expo)<0.5)
            {
               expo=expo*10;
               continue;
            }
            else if((smart_interval*expo)>=5)
            {
               expo=expo/10;
               continue;
            }
            else
            {
               if((smart_interval*expo<=5)&&(smart_interval*expo>=2))
               {
                  smart_interval=5;
                  break;
               }
               else
               {
                  smart_interval=1;
                  break;
               }
            }
         }
         interval=(interval==0?smart_interval/expo:interval);

         if(line_chart_type==1)
         {
//             slut_x=(int)(slut_x- column_width);
            column_width = (float)(slut_x - start_x)/(data.getColumnCount()-1);
            center_labels = false;
            centering_space=0;
         }
      }

      public void drawContent(Graphics2D g)
      {
         BasicStroke stroke = new BasicStroke(line_thickness);
         g.setStroke(stroke);

         double last_x = 0;
         double last_y = 0;

         int the_row = 1;
         int the_col = 1;
         while(the_row<=data.getRowCount())
         {
//            drawLegend(the_row,g);

            the_col = 1;
            while(the_col<=data.getColumnCount())
            {
               current_x = (int)(start_x+(the_col-1)*column_width+centering_space);

               // Label
               if(the_row==1)
                  drawColumnLabel(the_col,g);

               Number nr = (Number)data.getValueAt(the_row-1,the_col-1);
               if(nr == null)
               {
                  the_col++;
                  last_x=0;
                  last_y=0;
                  continue;
               }

               double max   = ((Number)temp_data.getValueAt(0,the_col-1)).doubleValue();
               double value = Math.abs(nr.doubleValue());
               chart.temp_data.setValueAt(new Double(max-value),0,the_col-1);
               double col_y;
               col_y = middle_y - ((max / span) * (bottom - top));

               Color c = (Color)(row_colors.elementAt(the_row-1));
               g.setColor(c);
               if(last_x > 0)
               {
                  drawIt((int)last_x,(int)last_y,current_x,(int)col_y,g); //special function that can be overwritten i children to this class
               }
               int plupp_size = (mark_size==0?(int)(column_width*0.2):mark_size);
               switch(mark_type)
               {
               case 0:
                  break;
               case 1:
                  g.drawRoundRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size,plupp_size,plupp_size);
                  break;
               case 2:
                  g.fillRoundRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size,plupp_size,plupp_size);
                  break;
               case 3:
                  g.drawRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size);
                  break;
               case 4:
                  g.fillRect(current_x-plupp_size/2,(int)col_y-plupp_size/2,plupp_size,plupp_size);
                  break;
               default:
                  ;
               }


               last_x = current_x;
               last_y = col_y;

               the_col++;
            }
            current_x = start_x;
            last_x = 0;
            last_y = 0;
            the_row++;
         }
         if(show_overlay)
         {
            drawOverlay(g);
         }
         // show legends
         for(int i=1;i<=legends.size();i++)
            drawLegend(i,g);

         for(int i=1;i<=overlay_legends.size();i++)
            drawOverlayLegend(i,g);


         // show values

         int t_r;
         int t_c = 1;
         int c_x = 1;
         while(t_c<=data.getColumnCount())
         {
            c_x = (int)(start_x+(t_c-1)*column_width+centering_space);
            double l_y = middle_y;
            int old = bottom;
            t_r = data.getRowCount();
            while(t_r>0)
            {
               Number nr = (Number)data.getValueAt(t_r-1,t_c-1);
               if(nr == null)
               {
                  t_r--;
                  continue;
               }

               double value = Math.abs(nr.doubleValue());
               double c_y;
               c_y = l_y -((value / span) * (bottom - top));

               Color c = (Color)(row_colors.elementAt(t_r-1));
               g.setFont(value_font);
               String string_value = stringOf(value,value_decimals);
               int string_value_length =  g.getFontMetrics().stringWidth(string_value);
               if((!show_values)&&(!showSerie(t_r)))
                  string_value = "";

               int value_center = (int)(((l_y-c_y)/2)+(value_font.getSize()/2));
               int x_c;
               if(t_c==1)
                  x_c = (int)(c_x+2);
               else if(t_c==data.getColumnCount())
                  x_c = (int)(c_x-string_value_length-1);
               else
                  x_c = (int)(c_x-string_value_length/2);
               int y_c =  Math.min((int)(c_y+value_center),old);
               if(value_colors_set)
               {
                  g.setColor(value_color);
                  g.drawString(string_value,x_c,y_c); 
               }
               else
               {
                  g.setColor(new Color(inb(c.getRed()-51),inb(c.getGreen()-51),inb(c.getBlue()-51)));
                  g.drawString(string_value,x_c,y_c);   // value text
                  g.setColor(new Color(inb(c.getRed()+102),inb(c.getGreen()+102),inb(c.getBlue()+102)));
                  g.drawString(string_value,x_c-1,y_c-1);   // value text
               }
               l_y = c_y;
               old = y_c-value_font.getSize();
               t_r--;
            }
            t_c++;
         }
      }           
   }

/**
 * A stacked column chart type. Supports negative values.
 */
   class StackedColumnChart extends ColumnChart
   {
      public StackedColumnChart()
      {
         this.chart_type=102;
      }

      public void init(Graphics2D g)
      {
         // Inherits all the normal settings from CustomColumnChart, except the column_width
         // and a few other things.
         int local_row_width = row_width;
         double local_interval = interval;
         super.init(g);
         interval = local_interval;


         double total_max_value = (double)max_value;
         double total_min_value = (double)min_value;
         int the_row = 1;
         int the_col = 1;
         while(the_col<=data.getColumnCount())
         {

            // Find the total (stacked) value of this column
            double local_max_value = 0;
            double local_min_value = 0;
            the_row = 1;
            while(the_row<=data.getRowCount())
            {

               double value = 0;
               try
               {
                  value = ((Double)(chart.data.getValueAt(the_row-1,the_col-1))).doubleValue(); 
                  //value = ((Number)data.getValueAt(the_row,the_col)).intValue();

               }
               catch(Exception e)
               {
                  ;
               }
               if(value>0)
                  local_max_value = local_max_value + value;
               else
                  local_min_value = local_min_value + value;
               the_row++;
            }
            if(local_max_value>total_max_value&&!fixed_max)
               total_max_value = local_max_value;
            if(local_min_value<total_min_value&&!fixed_min)
               total_min_value = local_min_value;
            the_col++;
         }
         if(!fixed_max)
            total_max_value = total_max_value*(double)(1+top_fill_factor);
         if(!fixed_min)
            total_min_value = total_min_value*(double)(1+top_fill_factor);

         span = (double) (total_max_value + Math.abs(total_min_value));
         half = (double) (span - total_max_value);

         zero = (double) (total_max_value / span);
         if(total_min_value >= 0)
            middle_y = bottom;
         else
            middle_y    = (int) (top + (zero * (bottom - top)));
         row_width = (local_row_width == 0 ? (int)(graph_fill_factor*column_width) : local_row_width );  // 10% space på varje sida om stapeln
         double smart_interval;
         smart_interval=(double)((total_max_value-total_min_value)/((bottom-top)/unit_label_font.getSize()/2));
         float expo=1.0f;
         while(true)
         {
            if((smart_interval*expo)<0.5)
            {
               expo=expo*10;
               continue;
            }
            else if((smart_interval*expo)>=5)
            {
               expo=expo/10;
               continue;
            }
            else
            {
               if((smart_interval*expo<=5)&&(smart_interval*expo>=2))
               {
                  smart_interval=5;
                  break;
               }
               else
               {
                  smart_interval=1;
                  break;
               }
            }
         }
         interval=(interval==0?smart_interval/expo:interval);
      }

      public void drawContent(Graphics2D g)
      {
         try
         {
            //int group_width = row_width*rows.size()+row_space*(rows.size()-1);
            int centering_space = (int)(column_width/2 - row_width / 2);
            int the_col = 1;
            while(the_col<=data.getColumnCount())
            {


               current_x = (int)(start_x+(the_col-1)*column_width+centering_space);

               drawColumnLabel(the_col,g);


               double minus_y = 0;
               double plus_y  = 0;

               int the_row = 1;
               while(the_row<=data.getRowCount())
               {
                  double value = 0;
                  try
                  {
                     value = ((Number)data.getValueAt(the_row-1,the_col-1)).doubleValue();
                  }
                  catch(Exception e)
                  {
                     the_row++;
                     continue;
                  }

                  double column_top;
                  double column_bottom;
                  if(value < 0)
                  {
                     column_top = middle_y + minus_y;
                     column_bottom = middle_y + minus_y + ((Math.abs(value) / span) * (bottom - top));
                     minus_y = minus_y+(column_bottom-column_top);

                  }
                  else
                  {
                     column_bottom = middle_y-plus_y;
                     column_top = middle_y - plus_y -((value / span) * (bottom - top));
                     plus_y = plus_y+(column_bottom-column_top);
                  }
                  Color c = (Color)(row_colors.elementAt(the_row-1));
                  g.setColor(c);
                  g.fillRect(current_x,(int)column_top,(int)row_width,(int)(1+column_bottom - column_top));
                  if(column_outline)
                  {
                     g.setColor(outline_color);
                     g.drawRect(current_x,(int)column_top,(int)row_width,(int)(1+column_bottom - column_top));
                  }
                  the_row++;

               }
               the_col++;
            }
            // show legends
            for(int i=1;i<=legends.size();i++)
               drawLegend(i,g);

            for(int i=1;i<=overlay_legends.size();i++)
               drawOverlayLegend(i,g);


            // show values

            int t_r;
            int t_c = 1;
            int c_x = 1;
            while(t_c<=data.getColumnCount())
            {
               double p_y=middle_y;
               double m_y=middle_y;
               double l_y=middle_y;
               int p_o=(int)middle_y;
               int m_o=(int)middle_y;
               c_x = (int)(start_x+(t_c-1)*column_width+centering_space);
               t_r = 1;
               while(t_r<=data.getRowCount())
               {
                  Number nr = (Number)data.getValueAt(t_r-1,t_c-1);
                  if(nr == null)
                  {
                     t_r++;
                     continue;
                  }

                  double value = nr.doubleValue();
                  if(value>=0)
                  {
                     l_y = p_y;
                     p_y -= ((value / span) * (bottom - top));
                  }
                  else
                  {
                     l_y = m_y;
                     m_y -= ((value / span) * (bottom - top));
                  }

                  Color c = (Color)(row_colors.elementAt(t_r-1));
                  g.setFont(value_font);
                  String string_value = stringOf(value,value_decimals);

                  int string_value_length =  g.getFontMetrics().stringWidth(string_value);
                  if((!show_values)&&(!showSerie(t_r)))
                     string_value = "";
                  if(value>=0)
                  {
                     int value_center = (int)(((l_y-p_y)/2)+(value_font.getSize()/2));
                     int x_c = (int)(c_x-string_value_length/2+row_width/2);
                     int y_c = Math.min((int)(p_y+value_center),p_o);
                     if(value_colors_set)
                     {
                        g.setColor(value_color);
                        g.drawString(string_value,x_c,y_c); 
                     }
                     else
                     {

                        g.setColor(new Color(inb(c.getRed()-51),inb(c.getGreen()-51),inb(c.getBlue()-51)));
                        g.drawString(string_value,x_c,y_c);   // value text
                        g.setColor(new Color(inb(c.getRed()+102),inb(c.getGreen()+102),inb(c.getBlue()+102)));
                        g.drawString(string_value,x_c-1,y_c-1);   // value text
                     }   
                     p_o=y_c-value_font.getSize();

                  }
                  else
                  {
                     int value_center = (int)(((m_y-l_y)/2)-(value_font.getSize()/2));
                     int x_c = (int)(c_x-string_value_length/2+row_width/2);
                     int y_c = Math.max((int)(m_y-value_center),m_o);
                     if(value_colors_set)
                     {
                        g.setColor(value_color);
                        g.drawString(string_value,x_c,y_c); 
                     }
                     else
                     {
                        g.setColor(new Color(inb(c.getRed()-51),inb(c.getGreen()-51),inb(c.getBlue()-51)));
                        g.drawString(string_value,x_c,y_c);   // value text
                        g.setColor(new Color(inb(c.getRed()+102),inb(c.getGreen()+102),inb(c.getBlue()+102)));
                        g.drawString(string_value,x_c-1,y_c-1);   // value text
                     }                        
                     m_o=y_c+value_font.getSize();
                  }

                  t_r++;
               }
               t_c++;
            }

            if(show_overlay)
            {
               drawOverlay(g);
            }
         }
         catch(Exception e)
         {
            error(e);
         }

      }

   }


/**
 * A Pie chart type. It converts all data to positive values and draws pies. Eather showing % och real value.
 */
   class PieChart extends AbstractChart
   {
      // Pie chart settings
      public Font slice_label_font  = new Font(default_font_face,1,8);
      public Color pie_label_color  = new Color(255,255,255);
      public int slice_label_style = 1;


      // Internal variable
      protected int centering_space = 0;
      protected float pie_width;
      protected int cent_x;
      protected int cent_y;


      public PieChart()
      {
         this.chart_type=103;
      }
      public void init(Graphics2D g)
      {
         // Inherits all the normal settings.
         // but changes a LOT!

         super.init(g);

         int the_row = 1;
         while(the_row<=data.getRowCount())
         {

            // Find the total (stacked) value of this column
            double local_max_value = 0;
            int the_col = 1;
            while(the_col<=data.getColumnCount())
            {

               double value = 0;
               try
               {
                  value = ((Double)(chart.data.getValueAt(the_row-1,the_col-1))).doubleValue(); 

               }
               catch(Exception e)
               {
                  the_col++;
                  continue;
               }
               local_max_value = local_max_value + Math.abs(value);
               the_col++;
            }

            //store the max values in the temp data object. Invisible!

            chart.temp_data.setColumnCount(1);
            if(the_row>chart.temp_data.getRowCount())
               chart.temp_data.setRowCount(the_row);
            chart.temp_data.setValueAt(new Double(local_max_value),the_row-1,0);
            the_row++;
         }
         // Find the max legend width.
//          if((legends.size()>0)&&show_legends)
//          {
//             cumulative_legend_width = 0;
//             max_legend_width        = 0;
//             int the_col=1;
//             int legend_width;
//             while(the_col<=data.getColumnCount())
//             {
//                String legend = (String)legends.elementAt(the_col);
//                if(legend != null)
//                {
//                   legend_width = (int)(g.getFontMetrics(legend_font).stringWidth(legend)+legend_font.getSize()*2);
//                   cumulative_legend_width += legend_width;
//                   if(legend_width > max_legend_width)
//                      max_legend_width = legend_width;
//                }
//                the_col++;
//             }
//          }

         initColors(chart.data.getColumnCount(),lightness,row_colors);


//          start_x = left_title_font.getSize()+8;
//          if(left_title == null)
//             start_x = 8;
//          if(legend_position==0)
//          {
//             if(cumulative_legend_width>(width-start_x-8))
//             {
//                legend_position = 1;
//             }
//             else
//             {
//                legend_position = 2;
//             }
//          }
//
//          if(legend_position == 1)
//          {
//             slut_x = width - (legends.size()>0?max_legend_width:8);
//          }
//          else
//          {
//             slut_x = width - 8;
//          }
//
//
//          bottom = height - (bottom_title==null ? 0: bottom_title_font.getSize()) - 8;
//          top = (graph_title==null ? 8 : (graph_title_font.getSize()+8));
//          if(legend_position == 2)
//             top+=legend_font.getSize();
         cent_x=(int)(start_x+(slut_x-start_x)/2);
         pie_width = (slut_x-start_x)*0.80f;
         float pie_height = (bottom-top)/data.getRowCount()*0.80f;
         pie_width = Math.min(pie_width,pie_height);
         cent_y=(int)(top+pie_width/2*1.25f);

      }

      // Draws a special label across the pie. Font and text is the same as normal labels.

      protected void drawPieLabel(int the_row,Graphics2D g)
      {
         if((labels.size() > the_row)&&show_labels)
         {
            String label = (String)labels.elementAt(the_row);
            if(label==null)
               label="";
            g.setFont(label_font);
            int label_width = g.getFontMetrics().stringWidth(label);
            int label_height= label_font.getSize();
            g.setColor(pie_label_color);

            g.drawString(label,(int)(cent_x-label_width/2),(int)(cent_y-2));

         }
      }

      // Since pie chart dont have normal grid and axis drawIntervalAndGrid is overridden
      // and simplyfied.

      public void drawIntervalAndGrid(Graphics2D g)
      {
         try
         {
            if(!pass2)
            {
               // Chart boundaries
               g.setColor(chart_background);
               g.fillRect(start_x,top,slut_x-start_x,bottom-top);
               g.setColor(foreground);
               if(chart_outline)
               {
                  g.drawRect(start_x,top,slut_x-start_x,bottom-top);
               }
            }

         }
         catch(Exception e)
         {
            error(e);
         }

      }

      public void drawContent(Graphics2D g)
      {
         try
         {
            //int group_width = row_width*rows.size()+row_space*(rows.size()-1);
            int the_row = 1;
            while(the_row<=data.getRowCount())
            {
               int the_col = 1;
               double start_angel = 0;
               double angel = 0;
               while(the_col<=data.getColumnCount())
               {
                  double value = 0;
                  double max   = ((Number)temp_data.getValueAt(the_row-1,0)).doubleValue();
                  try
                  {
                     value = ((Number)data.getValueAt(the_row-1,the_col-1)).doubleValue();
                  }
                  catch(Exception e)
                  {
                     the_col++;
                     continue;
                  }
                  start_angel = start_angel +angel;
                  angel = 360*(Math.abs(value)/max);

                  try
                  {
                     Color c = (Color)(row_colors.elementAt(the_col-1));
                     g.setColor(c);
                  }
                  catch(Exception e)
                  {
                     ;
                  }
                  if(angel!=0)
                     g.fillArc((int)(cent_x-pie_width/2),(int)(cent_y-pie_width/2),(int)pie_width,(int)pie_width,(int)start_angel,(int)angel+1);
                  g.setColor(value_color);
                  g.setFont(value_font);
                  String slice_label;
                  if(slice_label_style==1)
                  {
                     slice_label= stringOf(100*Math.abs(value)/max,value_decimals)+"%";
                  }
                  else if(slice_label_style==2)
                  {
                     slice_label= stringOf(Math.abs(value),value_decimals);
                  }
                  else
                  {
                     slice_label= "";
                  }
                  if((!show_values)&&(!showSerie(the_row)))
                     slice_label= "";

                  int slice_label_width = g.getFontMetrics().stringWidth(slice_label);

                  if((start_angel+angel/2)<90)
                  {
                     g.drawString(slice_label,(int)(cent_x+2+pie_width/2*Math.cos(Math.toRadians(start_angel+angel/2))),(int)(cent_y-pie_width/2*Math.sin(Math.toRadians(start_angel+angel/2))));
                  }
                  else if((start_angel+angel/2)<180)
                  {
                     g.drawString(slice_label,(int)(cent_x-slice_label_width+pie_width/2*Math.cos(Math.toRadians(start_angel+angel/2))),(int)(cent_y-pie_width/2*Math.sin(Math.toRadians(start_angel+angel/2))));
                  }
                  else if((start_angel+angel/2)<270)
                  {
                     g.drawString(slice_label,(int)(cent_x-slice_label_width+pie_width/2*Math.cos(Math.toRadians(start_angel+angel/2))),(int)(cent_y+slice_label_font.getSize()-pie_width/2*Math.sin(Math.toRadians(start_angel+angel/2))));
                  }
                  else
                  {
                     g.drawString(slice_label,(int)(cent_x+2+pie_width/2*Math.cos(Math.toRadians(start_angel+angel/2))),(int)(cent_y+slice_label_font.getSize()-pie_width/2*Math.sin(Math.toRadians(start_angel+angel/2))));
                  }

//						if(column_outline)
//						{
//                      g.setColor(new Color(0,0,0));
//                      g.rotate(Math.toRadians((int)-start_angel),140,140);
//                      g.drawLine(140,140,240,139);
//                      g.rotate(Math.toRadians(-angel),140,140);
//                      g.drawLine(140,140,240,139);
//                      g.rotate(Math.toRadians((int)(+start_angel+angel)),140,140);
//                      g.drawArc(40,40,200,200,(int)start_angel,(int)angel+1);
//						}
                  the_col++;

               }
               drawPieLabel(the_row,g);
               cent_y=cent_y+(int)(pie_width*1.25f);
               the_row++;
            }
            // show legends
            for(int i=1;i<=legends.size();i++)
               drawLegend(i,g);

            for(int i=1;i<=overlay_legends.size();i++)
               drawOverlayLegend(i,g);
         }
         catch(Exception e)
         {
            error(e);
         }

      }
   }

/**
 * Saves the image to a file using the encoder selected.
 * @param image    the image to encode.
 * @param path     the file to create.
 *
 * @see ifs.fnd.asp.ASPGraph#setEncoder
 */

   private void saveImage(BufferedImage image,String path) throws FndException
   {
      try
      {
         if(encoder_type==1)
         {
            if (dyna_cache_enabled) 
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder (out) ;
                JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
                param.setQuality(1,true);
                encoder.setJPEGEncodeParam(param);
                encoder.encode(image);
                String key = filename + extension;
                DynamicObjectCache.put(key, out.toByteArray(), "image/jpeg" ,mgr);
            }
	         else
            {
                FileOutputStream out = new FileOutputStream(path);
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder (out) ;
                JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
                param.setQuality(1,true);
                encoder.setJPEGEncodeParam(param);
                encoder.encode(image);
                out.close() ;            
            }
         }
         else
         {
            if (dyna_cache_enabled) 
            {
                PngEncoder encoder = new PngEncoderB(image);
                encoder.setCompressionLevel(9);                
                String key = filename + extension;
                DynamicObjectCache.put(key, encoder.pngEncode(),"image/png", mgr);
            }
            else
            {
                FileOutputStream out = new FileOutputStream(path);
                PngEncoder encoder = new PngEncoderB(image);
                encoder.setCompressionLevel(9);
                out.write(encoder.pngEncode());
                out.close();
            }
         }
      }
      catch(Exception e)
      {
         throw new FndException(e);
      }
   }
/**
 * Set encoder type.<br>
 * Encoders supported:
 * <ui>
 * <li>ASPGraph.PNG
 * <li>ASPGraph.JPG
 * </ui>
 * @see ifs.fnd.asp.ASPGraph#getEncoder
 * @see ifs.fnd.asp.ASPGraph#getOutput
 */
   public void setEncoder(int type)
   {
      encoder_type=type;
   }


/** 
 * Returns the complete URL for the generated image file located in
 * a temporary directory specified in the configuration file.
 * Calls drawGraph() if not called before.
 *
 * @see ifs.fnd.asp.ASPGraph#drawGraph
 */

   public String getGraph()
   {
      try
      {
         if(filename==null)
            drawGraph();
         
         if (dyna_cache_enabled)
            return mgr.getApplicationPath() +  "/" + DynamicObjectCache.URL_INDICATOR + "/" + filename + extension ;
         else
            return mgr.getASPConfig().getApplicationContext() + tmploc + "/" + filename + extension;
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }

/** 
 * Returns the file extension of the generated picture.
 * Default is PNG. JPG is also supported.
 * Please use getEncoder() instead.
 * @see ifs.fnd.asp.ASPGraph#getEncoder
 */

   public String getOutput()
   {
      return extension;
   }

/** 
 * Returns the file extension of the generated picture.
 * Default is ".png". ".jpg" is also supported.
 * Use this instead of getOutput()
 */

   public String getEncoder()
   {
      return extension;
   }



// Labels


/** Sets the label for a specific column.
 */
   public void setLabel(int col,String label)
   {
      try
      {
         if(chart.labels.size() <= col)
            chart.labels.setSize(col+1);
         chart.labels.setElementAt(label,col);
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
         error(e);
      }
   }

/** Gets the label for a specific column.
 */

   public String getLabel(int col)
   {
      return(chart.labels.size() <= col ? (String) chart.labels.elementAt(col) : "");
   }

/**
 * Sets the value in a specific column on the currently active row.
 *
 * @see ifs.fnd.asp.ASPGraph#setDataAt
 * @see ifs.fnd.asp.ASPGraph#setThisSet
 */
   public void setData(int col,double value)
   {
      if(this_set == 0)
         setValueAt(1,col,new Double(value));
      else
         setValueAt(this_set,col,new Double(value));
   }
   public void setData(int col,Double value)
   {
      if(this_set == 0)
         setValueAt(1,col,value);
      else
         setValueAt(this_set,col,value);
   }

/**
 * Sets the value in a specific row and column.
 * Much like data values in MS Excel work sheet.
 * No need to use setThisSet() with this one.<br>
 * <br>
 * This allows grouping of values; all values on a column index are grouped together.
 *
 * @see ifs.fnd.asp.ASPGraph#setThisSet
 * @see ifs.fnd.asp.ASPGraph#setOverlayDataAt
 */
   public void setDataAt(int row,int col,double value)
   {
      setValueAt(row,col,new Double(value));
   }

   public void setDataAt(int row,int col,Double value)
   {
      setValueAt(row,col,value);
   }

/**
 * Sets values for overlay series.
 * Each row equals one serie.<br>
 * <br>
 * No overlay will be displayed if no data is populated.
 *
 * @see ifs.fnd.asp.ASPGraph#setShowOverlay
 * @see ifs.fnd.asp.ASPGraph#setDataAt
 */

   public void setOverlayDataAt(int row,int col,double value)
   {
      setOverlayValueAt(row,col,new Double(value));
   }
   public void setOverlayDataAt(int row,int col,Double value)
   {
      setOverlayValueAt(row,col,value);
   }

   /**
 * Sets the value in a specific overlay column on the currently active row.
 *
 * @see ifs.fnd.asp.ASPGraph#setOverlayDataAt
 * @see ifs.fnd.asp.ASPGraph#setThisSet
 */
   public void setOverlayData(int col,double value)
   {
      if(this_set == 0)
         setOverlayValueAt(1,col,new Double(value));
      else
         setOverlayValueAt(this_set,col,new Double(value));
   }
   public void setOverlayData(int col,Double value)
   {
      if(this_set == 0)
         setOverlayValueAt(1,col,value);
      else
         setOverlayValueAt(this_set,col,value);
   }

/**
 * Gets the value for a specific column on the currently active row.
 *
 * @see ifs.fnd.asp.ASPGraph#setDataAt
 * @see ifs.fnd.asp.ASPGraph#setThisSet
 */
   public double getData(int col)
   {
      if(this_set == 0)
         return((Double)chart.data.getValueAt(1,col)).doubleValue();
      else
         return((Double)chart.data.getValueAt(this_set,col)).doubleValue();
   }

// Private function that sets the actual value in the model.

   private void setValueAt(int row,int column,Object value)
   {
      if(row>chart.data.getRowCount())
         chart.data.setRowCount(row);
      if(column>chart.data.getColumnCount())
         chart.data.setColumnCount(column);
      if(value!=null)
      {
         double double_value = ((Double)value).doubleValue();
         if(!chart.fixed_max)
            if(double_value > chart.max_value)
               chart.max_value = double_value;
         if(!chart.fixed_min)
            if(double_value < chart.min_value)
               chart.min_value = double_value;
      }
      chart.data.setValueAt(value,row-1,column-1);
   }

// Private function that sets the actual value in the overlay model.

   private void setOverlayValueAt(int row,int column,Object value)
   {
      if(row>chart.overlay_data.getRowCount())
         chart.overlay_data.setRowCount(row);
      if(column>chart.overlay_data.getColumnCount())
         chart.overlay_data.setColumnCount(column);
      if(value!=null)
      {
         double double_value = ((Double)value).doubleValue();
         if(!chart.fixed_max)
            if(double_value > chart.max_value)
               chart.max_value = double_value;
         if(!chart.fixed_min)
            if(double_value < chart.min_value)
               chart.min_value = double_value;
      }
      chart.overlay_data.setValueAt(value,row-1,column-1);
   }
/**
 * Sets the number of columns/points to show.
 * No need to use this, unless you don't what to show more that a specific number of points
 * or you don't have enough data but want the last columns to show but be empty.<br> 
 * Note: This will cut off any previously entered data above the column number supplied.
 */
   public void setNumPoints(int cols)
   {
      chart.data.setColumnCount(cols);
      chart.overlay_data.setColumnCount(cols);
   }

/**
 * Returns the number of columns/points that will be shown.
 */
   public int getNumPoints()
   {
      return chart.data.getColumnCount();
   }

/**
 * Sets the number of rows/series. The rows are grouped together in the resulting chart.<br>
 * Note: this will cut off any previously entered data above the row number supplied.
 */
   public void setNumSets(int rows)
   {
      chart.data.setRowCount(rows);
   }

/**
 * Returns the number of rows/series that will be shown.
 */
   public int getNumSets()
   {
      return chart.data.getRowCount();
   }

/**
 * Sets the number of overlay series/rows. The rows are grouped together in the resulting chart.<br>
 * Note: this will cut off any previously entered data above the row number supplied.
 */
   public void setOverlayNumSets(int rows)
   {
      chart.overlay_data.setRowCount(rows);
   }

/**
 * Returns the number of series/rows that will be shown.
 */
   public int getOverlayNumSets()
   {
      return chart.overlay_data.getRowCount();
   }



/**
 * Sets the active set (row).
 * <br>
 * This:<br>
 * <br>
 * setThisSet(1);<br>
 * setData(2,10);<br>
 * <br>
 * ... is functionally equivalent to this: <br>
 * <br>
 * setDataAt(1,2,10).<br>
 */
   public void setThisSet(int row)
   {
      this_set = row;
   }

/**
 * Returns the currently active set (row).
 */
   public int getThisSet()
   {
      return this_set;
   }

/**
 * Sets the data to be used from a string, where each column is separated by a TAB (\t),
 * and each row is separated by a newline (\n).<br>
 * <br>
 * Note: This overrides the values set by setNumSets() and setNumPoints, which are both reset.<br>
 * However, those methods can still be used after setQuickData().
 */
   public void setQuickData(String data)
   {
      StringTokenizer rows = new StringTokenizer(data,"\n");
      int row = 1;
      while(rows.hasMoreTokens())
      {
         StringTokenizer cols = new StringTokenizer(rows.nextToken(),"\t");

         int col = 1;
         while(cols.hasMoreTokens())
         {
            double value = 0;
            try
            {
               value = Double.parseDouble(cols.nextToken().trim());
            }
            catch(NumberFormatException e)
            {
               if(DEBUG) debug(e.toString());
            }
            setDataAt(row,col,value);
            col++;
         }
         row++;
      }
   }

/**
 * Sets the legend for a specific row/serie
 */
   public void setLegend(int row,String legend)
   {
      try
      {
         if(chart.legends.size() <= row)
            chart.legends.setSize(row+1);
         chart.legends.setElementAt(legend,row);
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
         error(e);
      }
   }

/**
 * Gets the legend for a specific row/serie
 */
   public String getLegend(int row)
   {
      return(chart.legends.size() <= row ? (String) chart.legends.elementAt(row) : "");
   }

/**
 * Sets the legend for a specific overlay row/serie
 */
   public void setOverlayLegend(int row,String legend)
   {
      try
      {
         if(chart.overlay_legends.size() <= row)
            chart.overlay_legends.setSize(row+1);
         chart.overlay_legends.setElementAt(legend,row);
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Gets the legend for a specific overlay row/serie
 */
   public String getOverlayLegend(int row)
   {
      return(chart.overlay_legends.size() <= row ? (String) chart.overlay_legends.elementAt(row) : "");
   }

// /**
//  * Adds a custom label anywhere on the chart.
//  */
//    public void addCustomLabel(double x,
//                            double y,
//                            double z,
//                            java.lang.String text,
//                            java.awt.Font font,
//                            java.awt.Color clr)
//    {
//       try {
//          chart.addCustomLabel(x,y,z,text,font,clr);
//       } catch (Throwable e)
//       {
//          error(e);
//       }
//    }
//
/**
 * Sets the interval used in the chart. Intervals are the
 * lines that are drawn across the chart. If this is NOT set, it will be calculated for best look.
 */
   public void setInterval(double interval)
   {
      try
      {
         chart.interval= interval;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }
/**
 * Gets the interval set by setInterval.
 * @see ifs.fnd.asp.ASPGraph#setInterval.
 */

   public double getInterval()
   {
      return chart.interval;
   }

/**
 * Sets the palette to use on series.
 * <ui>
 * <li>1 = Excel inspired palette.
 * <li>2 = IFS module chart colors.
 * <li>3 = Gray scale.
 * </ui>
 *  
 * The palettes consists of 8 base colors and its lighter and darker versions. 
 * 24 colors in total. If more color are needed, they are generated randomly.<br>
 * To set colors explicitly use setColors<br>
 * To change lightness of a palette use setLightness
 * @see ifs.fnd.asp.ASPGraph#setColors.
 * @see ifs.fnd.asp.ASPGraph#setLightness.
 */


   public void setPalette(int pal)
   {
      chart.palette=pal;
   }

/** Gets the palette. 
 * @see ifs.fnd.asp.ASPGraph#setPalette.
 */
   public int getPalette()
   {
      return chart.palette;
   }

/** Sets the relative difference to a palette. 
 * @param light can be positive or negative.
 * @see ifs.fnd.asp.ASPGraph#getLightness.
 */

   public void setLightness(int light)
   {
      chart.lightness=light;
   }


/** Gets the Lightness. 
 * @see ifs.fnd.asp.ASPGraph#setLightness.
 */
   public int getLightness()
   {
      return chart.lightness;
   }


/** Sets the Label format. 
 * <ui>
 * <li>BEST_FIT - The labels will tilt if needed. Default. 
 * <li>HORIZONTAL - No tilt.
 * <li>VERTICAL - 90 degrees tilt.    
 * <li>DIAGONAL - 45 degrees tilt. 
 * </ui> 
 * @see ifs.fnd.asp.ASPGraph#getLabelFormat.
 */
   public void setLabelFormat(int format)
   {
      chart.labels_format=format;
   }


/** Gets the Label format. 
 * <ui>
 * <li>BEST_FIT - The labels will tilt if needed. Default. 
 * <li>HORIZONTAL - No tilt.
 * <li>VERTICAL - 90 degrees tilt.    
 * <li>DIAGONAL - 45 degrees tilt. 
 * </ui> 
 * @see ifs.fnd.asp.ASPGraph#setLabelFormat.
 */
   public int getLabelFormat()
   {
      return chart.labels_format;
   }


/** Sets the Legend position. 
 * <ui>
 * <li>0 - Best place. Default. 
 * <li>1 - On the right side of the chart.
 * <li>2 - On top and to the right.    
 * </ui> 
 * @see ifs.fnd.asp.ASPGraph#getLegendPosition.
 */
   public void setLegendPosition(int position)
   {
      chart.legend_position=position;
   }

/** Gets the Legend position. 
 * <ui>
 * <li>0 - Best place. Default. 
 * <li>1 - On the right side of the chart.
 * <li>2 - On top and to the right.    
 * </ui> 
 * @see ifs.fnd.asp.ASPGraph#setLegendPosition
 */
   public int getLegendPosition()
   {
      return chart.legend_position;
   }

/** Sets the font used for YAxis Decription. 
 * @see ifs.fnd.asp.ASPGraph#setYAxisDesc
 * @see ifs.fnd.asp.ASPGraph#getYAxisDescFont
 */

   public void setYAxisDescFont(Font font)
   {
      chart.y_axis_desc_font=font;
   }

/** Gets the font used for YAxis Decription. 
 * @see ifs.fnd.asp.ASPGraph#setYAxisDesc
 * @see ifs.fnd.asp.ASPGraph#setYAxisDescFont
 */

   public Font getYAxisDescFont()
   {
      return chart.y_axis_desc_font;
   }

/** Sets the font used for Labels. 
 * @see ifs.fnd.asp.ASPGraph#setLabel
 * @see ifs.fnd.asp.ASPGraph#getLabelFont
 */
   public void setLabelFont(Font font)
   {
      chart.label_font=font;
   }

/** Gets the font used for Labels. 
 * @see ifs.fnd.asp.ASPGraph#setLabel
 * @see ifs.fnd.asp.ASPGraph#setLabelFont
 */
   public Font getLabelFont()
   {
      return chart.label_font;
   }

/** Sets the font used for Legends. 
 * @see ifs.fnd.asp.ASPGraph#setLegend
 * @see ifs.fnd.asp.ASPGraph#getLegendFont
 */
   public void setLegendFont(Font font)
   {
      chart.legend_font=font;
   }


/** Gets the font used for Legends. 
 * @see ifs.fnd.asp.ASPGraph#setLabel
 * @see ifs.fnd.asp.ASPGraph#setLabelFont
 */
   public Font getLegendFont()
   {
      return chart.legend_font;
   }
/** Sets the font used for Y-Axis Unit Marks. <br>
 *  <br>
 * NOTE: Unit Labels are the ticks on the axis. Do not mix with Left Title.
 * Which was earlier called Unit Label.
 * @see ifs.fnd.asp.ASPGraph#setLabel
 * @see ifs.fnd.asp.ASPGraph#getLabelFont
 */
   public void setUnitLabelFont(Font font)
   {
      chart.unit_label_font=font;
   }

/** Gets the font used for Y-Axis Unit Marks. 
 * @see ifs.fnd.asp.ASPGraph#setLabel
 * @see ifs.fnd.asp.ASPGraph#setLabelFont
 */
   public Font getUnitLabelFont()
   {
      return chart.unit_label_font;
   }
   /** 
    * Sets the font used for left title. 
   */
   public void setLeftTitleFont(Font font)
   {
      chart.left_title_font=font;
   }
  
   /** 
    * Gets the font used for left title. 
   */
   public Font getLeftTitleFont()
   {
      return chart.left_title_font;
   }

   /** 
    * Sets the font used for graph title. 
   */
   public void setGraphTitleFont(Font font)
   {
      chart.graph_title_font=font;
   }

   /** 
    * Gets the font used for graph title. 
   */
   public Font getGraphTitleFont()
   {
      return chart.graph_title_font;
   }

   /** 
    * Sets the font used for bottom title. 
   */
   public void setBottomTitleFont(Font font)
   {
      chart.bottom_title_font=font;
   }

   /** 
    * Gets the font used for bottom title. 
   */
   public Font getBottomTitleFont()
   {
      return chart.bottom_title_font;
   }

   /** 
   * Sets the height of the chart picture.
   */
   public void setHeight(int height)
   {
      try
      {
         chart.height=height;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   /** 
    * Gets the height of the chart picture.
   */
   public int getHeight()
   {
      return chart.height;
   }

   /** 
    * Sets the width of the chart picture.
   */
   public void setWidth(int width)
   {
      try
      {
         chart.width = width;
      }
      catch(Throwable e)
      {
         if(DEBUG) debug(e.toString());
         error(e);
      }
   }

/** Gets the width of the chart picture.
 */
   public int getWidth()
   {
      return chart.width;
   }


/** 
 * Sets the maximum value on the Y-Axis. 
 * If not set, it will be set to max data value + 10%.
 * @param value must be positive.
 * @see ifs.fnd.asp.ASPGraph#getMaxValue
 * @see ifs.fnd.asp.ASPGraph#setMinValue
 */
   public void setMaxValue(double value)
   {
      if(value>chart.min_value||!chart.fixed_min)
      {
         chart.fixed_max = true;
         chart.max_value = value;
      }
   }

/** 
 * Gets the maximum value on the Y-Axis. 
 * @see ifs.fnd.asp.ASPGraph#setMaxValue
 */
   public double getMaxValue()
   {
      return chart.max_value;
   }


/** 
 * Sets the minimum value on the Y-Axis. 
 * If not set, it will be set to max data value + 10%.
 */
   public void setMinValue(double value)
   {
      if(value<chart.max_value||!chart.fixed_max)
      {
         chart.fixed_min = true;
         chart.min_value = value;
      }
   }

/** 
 * Gets the minimum value on the Y-Axis. 
 * @see ifs.fnd.asp.ASPGraph#setMaxValue
 */
   public double getMinValue()
   {
      return chart.min_value;
   }




/** 
 * Set this to show a border around the chart area. <br>
 * It is always drawn with foreground color. Default is TRUE.
 */
   public void setGraphOutline(boolean value)
   {
      try
      {
         chart.chart_outline = value;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Set this to use web safe palette. <br>
 * Note: This will NOT effect custom palettes. Only the built in.
 * Default is TRUE.
 */
   public void setWebSafeColors(boolean value)
   {
      try
      {
         chart.use_websafe_colors = value;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Gets the show-border property. <br>
 */
   public boolean getGraphOutline()
   {
      return chart.chart_outline;
   }

/** 
 * Set this to show a dotted grid.<br>
 * NOTE: value = true  is equivalent to SetGridOption(ASPGraph.FULL_GRID)<br>
 *       value = false is equivalent to SetGridOption(ASPGraph.NO_GRID)
 * @see ifs.fnd.asp.ASPGraph#getShowGrid
 * @see ifs.fnd.asp.ASPGraph#setGridColor
 * @see ifs.fnd.asp.ASPGraph#setGridOnTop
 * @see ifs.fnd.asp.ASPGraph#setGridOption
 */
   public void setShowGrid(boolean value)
   {
      try
      {
         chart.show_horizontal_grid = value;
         chart.show_vertical_grid   = value;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Gets the property to show a dotted grid.<br>
 * @see ifs.fnd.asp.ASPGraph#setShowGrid
 */
   public boolean getShowGrid()
   {
      return(chart.show_vertical_grid)&&(chart.show_horizontal_grid);
   }

/** 
 * Set this to show labels for points/columns/pies.<br>
 * @see ifs.fnd.asp.ASPGraph#getShowLabels
 */
   public void setShowLabels(boolean value)
   {
      try
      {
         chart.show_labels = value;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Get the property to show labels for points/columns/pies.<br>
 * @see ifs.fnd.asp.ASPGraph#setShowLabels
 */
   public boolean getShowLabels()
   {
      return chart.show_labels;
   }

/** 
 * Set this to show labels for series/rows.<br>
 * @see ifs.fnd.asp.ASPGraph#getShowLegends
 */
   public void setShowLegends(boolean value)
   {
      try
      {
         chart.show_legends = value;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Get the property to show legends for series/rows.<br>
 * @see ifs.fnd.asp.ASPGraph#setShowLegends
 */
   public boolean getShowLegends()
   {
      return chart.show_legends;
   }


/** 
 * Sets the description for the Y-Axis.<br>
 * @see ifs.fnd.asp.ASPGraph#setYAxisDescFont
 */
   public void setYAxisDesc(String desc)
   {
      try
      {
         chart.y_axis_desc = desc;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Sets the Title of the graph.<br>
 * @see ifs.fnd.asp.ASPGraph#getGraphTitle
 */
   public void setGraphTitle(String title)
   {
      try
      {
         chart.graph_title = title;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Gets the Title of the graph.<br>
 * @see ifs.fnd.asp.ASPGraph#setGraphTitle
 */
   public String getGraphTitle()
   {
      return chart.graph_title;
   }

/** 
 * Sets the Left Title of the graph.<br>
 * NOTE: This replaces setUnitLabel.
 * @see ifs.fnd.asp.ASPGraph#getLeftTitle
 */
   public void setLeftTitle(String title)
   {
      try
      {
         chart.left_title = title;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Gets the Left Title of the graph.<br>
 * @see ifs.fnd.asp.ASPGraph#setLeftTitle
 */
   public String getLeftTitle()
   {
      return chart.left_title;
   }

/** 
 * Sets the Bottom Title of the graph.<br>
 * NOTE: This replaces setColumnLabel.
 * @see ifs.fnd.asp.ASPGraph#getBottomTitle
 */
   public void setBottomTitle(String title)
   {
      try
      {
         chart.bottom_title = title;
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

/** 
 * Gets the Bottom Title of the graph.<br>
 * @see ifs.fnd.asp.ASPGraph#setBottomTitle
 */
   public String getBottomTitle()
   {
      return chart.bottom_title;
   }

/** 
 * Set this to use antialiasing. (Smooth edges).<br>
 * @see ifs.fnd.asp.ASPGraph#getAntiAliasing
 */
   public void setAntiAliasing(boolean anti)
   {
      anti_aliasing = anti;
   }

/** 
 * Get the property to use antialiasing. (Smooth edges).<br>
 * @see ifs.fnd.asp.ASPGraph#setAntiAliasing
 */
   public boolean getAntiAliasing()
   {
      return anti_aliasing;
   }

/** 
 * Sets the background color of the graph.<br>
 * NOTE: This will change the color outside the outline. Default is White.
 * @see ifs.fnd.asp.ASPGraph#setChartBackground
 */
   public void setGraphBackground(Color c)
   {
      try
      {
         pic_background = c;
      }
      catch(Exception e)
      {
         ;
      }
   }

/** 
 * Sets the background color of the chart.<br>
 * NOTE: This will change the color inside the outline. Default is White.
 * @see ifs.fnd.asp.ASPGraph#setGraphtBackground
 */
   public void setChartBackground(Color c)
   {
      try
      {
         chart.chart_background = c;
      }
      catch(Exception e)
      {
         ;
      }
   }
/** 
 * Sets the background color of the chart.<br>
 * NOTE: This color should be very different to background colors. Default is dark gray.
 * @see ifs.fnd.asp.ASPGraph#setGraphBackground
 */
   public void setForeground(Color c)
   {
      try
      {
         chart.foreground = c;
      }
      catch(Exception e)
      {
         ;
      }
   }
/** 
 * Sets the grid color of the chart.<br>
 * @see ifs.fnd.asp.ASPGraph#setShowGrid
 */
   public void setGridColor(Color c)
   {
      try
      {
         chart.grid_color = c;
      }
      catch(Exception e)
      {
         ;
      }
   }

/** 
 * Sets the color of the small value  labels shown in the graph.<br>
 * Leave this setting alone and it will try to look good.
 * @see ifs.fnd.asp.ASPGraph#setShowValues
 */
   public void setValueColor(Color c)
   {
      try
      {
         chart.value_color = c;
         chart.value_colors_set = true;
      }
      catch(Exception e)
      {
         ;
      }
   }

/** 
 * Sets the colors of the series.<br>
 * Make sure to create a Vector with your colors.
 * It will be cloned and can therefore be reused.
 * If vector contains 3 color object, 9 color will be generated.
 * First the 3 original colors, then 3 lighter and finally 3 darker versions.
 * If even more colors are needed by the graph, the rest will be random.
 * @see ifs.fnd.asp.ASPGraph#getColors
 */
   public void setColors(Vector V)
   {
      try
      {
         chart.palette=0;
         chart.row_colors = (Vector)V.clone();
      }
      catch(Exception e)
      {
         ;
      }
   }

/** 
 * Returns a reference to the current color vector.
 * @see ifs.fnd.asp.ASPGraph#getColors
 */
   public Vector getColors()
   {
      return chart.row_colors;
   }

/** 
 * Sets the colors of the overlay series.<br>
 * Make sure to create a Vector with your colors.
 * It will be cloned and can therefore be reused.
 * If vector contains 3 color object, 9 color will be generated.
 * First the 3 original colors, then 3 lighter and finally 3 darker versions.
 * If even more colors are needed by the graph, the rest will be random.
 * @see ifs.fnd.asp.ASPGraph#getOverlayColors
 */
   public void setOverlayColors(Vector V)
   {
      try
      {
         chart.overlay_colors =(Vector)V.clone();
      }
      catch(Exception e)
      {
         ;
      }
   }

/** 
 * Returns a reference to the current overlay color vector.
 * @see ifs.fnd.asp.ASPGraph#getColors
 */
   public Vector getOverlayColors()
   {
      return chart.overlay_colors;
   }

/** 
 * Set this to display grids over the content.<br>
 * @see ifs.fnd.asp.ASPGraph#setShowGrid
 */
   public void setGridOnTop(boolean on_top)
   {
      try
      {
         grid_on_top = on_top;
      }
      catch(Exception e)
      {
         ;
      }
   }

/** 
 * Set grid options to either of:<br>
 * - ASPGraph.NO_GRID
 * - ASPGraph.FULL_GRID
 * - ASPGraph.HORIZONTAL_GRID
 * - ASPGraph.VERTICAL_GRID
 * @see ifs.fnd.asp.ASPGraph#setShowGrid
 */
   public void setGridOption(int option)
   {
      switch(option)
      {
      case NO_GRID:
         chart.show_horizontal_grid = false;
         chart.show_vertical_grid   = false;
         break;
      case FULL_GRID:
         chart.show_horizontal_grid = true;
         chart.show_vertical_grid   = true;
         break;
      case HORIZONTAL_GRID:
         chart.show_horizontal_grid = true;
         chart.show_vertical_grid   = false;
         break;
      case VERTICAL_GRID:
         chart.show_horizontal_grid = false;
         chart.show_vertical_grid   = true;
         break;
      default:
         break;
      }
   }


/** 
 * Set this to show overlays. (only if overlay data is available.)<br>
 * @see ifs.fnd.asp.ASPGraph#getShowOverlay
 */
   public void setShowOverlay(boolean show)
   {
      try
      {
         chart.show_overlay= show;
      }
      catch(Exception e)
      {
         ;
      }
   }

/** 
 * Gets the property to show overlay.<br>
 * @see ifs.fnd.asp.ASPGraph#setShowOverlay
 */
   public boolean getShowOverlay()
   {
      return chart.show_overlay;
   }


/**
 * Sets the number of pixels to add between units on Y-axis.<br>
 * Use this if y-axis tend to be overcrowded with unit labels. <br>
 * NOTE: The value is not precise since the interval is calculated. But this will set minimum space between unit labels.
 * @param pixels is the number of extrapixels to add between unit labels.default is 0. it can be both positiv and negative.<br>
 */
   public void setUnitLabelSpace(int pixels)
   {
      try
      {
         chart.unit_spacing= pixels;
      }
      catch(Exception e)
      {
         ;
      }
   }



/**
 * Sets the number of labels that are shown.<br>
 * <br>
 * NOTE: setLabelCount(0) equals setShowLabels(false)
 * @param count number of labels. -1 means all labels and are default.<br>
 */

   public void setLabelCount(int count)
   {
      try
      {
         chart.label_count= count;
      }
      catch(Exception e)
      {
         ;
      }
   }


/**
 * Sets the number of which labels the will be shown.<br>
 * <br>
 * NOTE: setLabelEvery(0) equals setShowLabels(false)
 * @param every number of space minus 1 between labels.<br>
 */

   public void setLabelEvery(int every)
   {
      try
      {
         chart.every= every;
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Sets the color of column bar outlines.<br>
 * <br>
 * NOTE: Can only be used on colomncharts and stackedcolumncharts
 * @see ifs.fnd.asp.ASPGraph#setColumnOutline
 */
   public void setColumnOutlineColor(Color c)
   {
      try
      {
         ((ColumnChart)chart).outline_color= c;
      }
      catch(Exception e)
      {
         ;
      }
   }


/**
 * Sets the thickness of lines/overlays.<br>
 * <br>
 * NOTE: Can be anything from 0.1 to 10.0+
 */
   public void setLineThickness(float value)
   {
      try
      {
         chart.line_thickness= value;
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Sets the line mark type.<br>
 *<ui>
 *<li>0 = no marks at all. (default)
 *<li>1 = Rings.
 *<li>2 = Filled rings.
 *<li>3 = Squares.
 *<li>4 = Filled squares.
 *</ui>
 */
   public void setLineMarkType(int value)
   {
      try
      {
         chart.mark_type= value;
      }
      catch(Exception e)
      {
         ;
      }
   }
/**
 * Sets the line mark size in pixels.<br>
 * Note: Default is 10% of columnwidth.
 */
   public void setLineMarkSize(int value)
   {
      try
      {
         chart.mark_size= value;
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Sets the line chart type.<br>
 *<ui>
 *<li>CENTERED = Each point is centered in the column. (default for overlays)
 *<li>FULL     = Each point is placed on the grid and on the edges. (default for Line Charts)
 *</ui>
 */
   public void setLineChartType(int value)
   {
      try
      {
         ((LineChart)chart).line_chart_type= value;
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Sets the fill factor for columns.<br>
 * Set this in percent. 80 is default.<br>
 * Use this instead of setRowWidth and setRowSpace.
 * A high value (max 100) will render the columnbars as thick as possible, standing togeather, 
 * while a low number will create thin bars with lots of space in between.
 */
   public void setGraphFillFactor(int percent)
   {
      try
      {
         ((ColumnChart)chart).graph_fill_factor= (float)(percent/100.0);
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Sets the columnbar thickness explicitly.<br>
 * @param width Set in pixels.
 * @see ifs.fnd.asp.ASPGraph#setRowSpace
 */
   public void setRowWidth(int width)
   {
      try
      {
         ((ColumnChart)chart).row_width= width;
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Sets the space between columnbars explicitly.<br>
 * @param space Set in pixels.
 * @see ifs.fnd.asp.ASPGraph#setRowWidth
 */
   public void setRowSpace(int space)
   {
      try
      {
         ((ColumnChart)chart).row_space= space;
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Set this to draw outlines on Columnbars and StackedColumnbars.<br>
 * @see ifs.fnd.asp.ASPGraph#setColumnOutlineColor
 */
   public void setColumnOutline(boolean value)
   {
      try
      {
         ((ColumnChart)chart).column_outline = value;
      }
      catch(Exception e)
      {
         ;
      }

   }

/**
 * Set this to draw each column with a different color.<br>
 */
   public void setColumnColorMode(boolean value)
   {
      try
      {
         ((ColumnChart)chart).column_color_mode = value;
      }
      catch(Exception e)
      {
         ;
      }

   }

/**
 * Enables or disables showing pie chart percentages.<br>
 * If set to false the pie will show values instead of percentages.<br><br>
 * NOTE: setShowValues(true) is called from within this function.
 */

   public void setShowPercent(boolean show)
   {
      try
      {
         if(chart.chart_type==103)
         {
            setShowValues(true);
            if(show)
               ((PieChart)chart).slice_label_style=1;
            else
               ((PieChart)chart).slice_label_style=2;
         }
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Enables or disables showing values. (all chart types)
 * @see ifs.fnd.asp.ASPGraph#setShowOverlayValues
 */
   public void setShowValues(boolean show)
   {
      try
      {
         chart.show_values = show;
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Enables or disables showing values for a specific serie. (all chart types)
 * @see ifs.fnd.asp.ASPGraph#setShowOverlayValues
 */
   public void setShowValues(int row,boolean show)
   {
      try
      {
         Boolean Show = new Boolean(show);
         if(chart.show_serie.size() <= row)
            chart.show_serie.setSize(row+1);
         chart.show_serie.setElementAt(Show,row);
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
         error(e);
      }
   }
/**
 * Enables or disables showing values on overlays.
 */
   public void setShowOverlayValues(boolean show)
   {
      try
      {
         chart.show_overlay_values = show;;
      }
      catch(Exception e)
      {
         ;
      }
   }

/**
 * Enables or disables showing values for a specific overlay serie. (all chart types)
 * @see ifs.fnd.asp.ASPGraph#setShowOverlayValues
 */
   public void setShowOverlayValues(int row,boolean show)
   {
      try
      {
         Boolean Show = new Boolean(show);
         if(chart.show_overlay_serie.size() <= row)
            chart.show_overlay_serie.setSize(row+1);
         chart.show_overlay_serie.setElementAt(Show,row);
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
         error(e);
      }
   }
/**
 * Sets the color for the special Pie labels. <br>
 * Use a color in contrast to slice colors. Default is white.
 * @see ifs.fnd.asp.ASPGraph#setShowLabels
 * @see ifs.fnd.asp.ASPGraph#setLabel
 */
   public void setPieLabelColor(Color c)
   {
      try
      {
         ((PieChart)chart).pie_label_color=c;
      }
      catch(Exception e)
      {
         ;
      }
   }


/**
 * Sets the font use for value labels. <br>
 * @see ifs.fnd.asp.ASPGraph#setShowValues
 */
   public void setValueFont(Font font)
   {
      chart.value_font=font;
   }

/**
 * Sets the number of decimals shown for values. <br>
 * Default is one decimal.
 * @see ifs.fnd.asp.ASPGraph#setShowValues
 */
   public void setValueDecimals(int decimals)
   {
      chart.value_decimals=decimals;
   }

//  DEBUGGING
   private void debug( String line )
   {
      System.out.println(line);
      Util.debug(line);
   }


   private void error( Throwable any )
   {
      System.out.println(any.getMessage());

      if(DEBUG) Util.debug(any.getMessage() + "\n" + Str.getStackTrace(any));
      throw new RuntimeException(any.getMessage());
//
//      if(log!=null)
//      {
//         ASPHTMLFormatter fmt = mgr.newASPHTMLFormatter();
//         String msg = fmt.formatErrorMsg("ASPGraph error: " + any);
//         mgr.responseWrite(msg);
//         Util.debug(Str.getStackTrace(any));
//         //         log.error(any,true);
//      }
//      else
//      {
//         Util.debug(
//            "\n\tP A N I C ! ! !\n\t===============\n"+
//            "\tThe instance of ASPLog is missing in class ASPGraph\n"+
//            "\twhile throwing error:\n\t"+Str.getStackTrace(any)+"\n\n\n"+
//            "\tASPGraph: Throws RuntimeException ...\n");
//         throw new RuntimeException("Aborting execution...\n");
//      }


   }
}                                                                                                                                                             





