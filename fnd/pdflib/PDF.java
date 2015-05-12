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
*  File        : PDF.java
*  Description : Interface to JNI methods for webpdf.dll.
*  Notes       : Warning: Never use any of the methods without checking the OS.
*                This class only works in Windows environment.
*  Modified    :
*    Mangala   - 2001-Jun-15  - Created. The old rapper class of the COM which had the
*                               same name as "PDF.java", no longer exists.
* ----------------------------------------------------------------------------
*/

package ifs.fnd.pdflib;

public class PDF
{

   private int i_hdl; //Handler for the PDF structure created by the webpdf.dll.

   //Load the libarary runtime
   static {
      System.loadLibrary("webpdf");
   }

   /**
    * Contructor. Here we create a new instance of the PDF structure and get 
    * the PDF handler.
    **/
   
   public PDF()
   {
      
      i_hdl = create_Handler();
   }

   //------------------------------------------------------------------------
   // Following public methods call the corresponding native method with the 
   // handler of the PDF.
   //------------------------------------------------------------------------

   public int open(String filename)
   {
      return open(filename, i_hdl);         
   }

   public void close() 
   {
      close(i_hdl);         
   }
   
   public void set_info(String key, String value)
   {
      set_info(key, value, i_hdl);         
   }

   public int findfont(String fontname, String encoding, int embed)
   {
      return findfont(fontname, encoding, embed, i_hdl);         
   }

   public void begin_page(float width, float height)
   {
      begin_page(width, height, i_hdl);         
   }


   public int open_GIF(String filename)
   {
      return open_GIF(filename, i_hdl);         
   }

   public int open_JPEG(String filename)
   {
      return open_JPEG(filename, i_hdl);         
   }

   public int get_image_width(int image)
   {
      return get_image_width(image, i_hdl);         
   }

   public int get_image_height(int image)
   {
      return get_image_height(image, i_hdl);         
   }

   public void place_image(int image, float x, float y, float scale)
   {
      place_image(image, x, y, scale, i_hdl);         
   }

   public void setfont(int font, float fontsize)
   {
      setfont(font, fontsize, i_hdl);         
   }

   public void show_xy(String text, float x, float y)
   {
      show_xy( text, x, y, i_hdl);         
   }

   public void moveto(float x, float y)
   {
      moveto( x, y, i_hdl);         
   }

   public void lineto(float x, float y)
   {
      lineto( x, y, i_hdl);         
   }

   public void stroke()
   {
      stroke(i_hdl);         
   }

   public void end_page()
   {
      end_page(i_hdl);         
   }

   public String get_fontname()
   {
      return get_fontname(i_hdl);

   }

   public float get_fontsize()
   {
      return get_fontsize(i_hdl);
   }
   
   public int get_font()
   {
      return get_font(i_hdl);
   }

   public float stringwidth(String text, int font, float size)
   {
      return stringwidth(text, font, i_hdl);
   }

   public int open_image(String type, String source, String data, int len, int width, int height, int components, int bpc, String params)
   {
      return open_image( type, source, data, len, width, height, components, bpc, params, i_hdl);
   }

   public int open_TIFF(String filename)
   {
      return open_TIFF(filename, i_hdl);
   }

   public int open_CCITT(String filename, int width, int height, int BitReverse, int K, int BlackIs1)
   {
      return open_CCITT(filename, width, height, BitReverse, K, BlackIs1, i_hdl);
   }

   public int add_bookmark(String text, int parent, int open)
   {
      return add_bookmark(text, parent, open, i_hdl);
   }

   public void set_transition(String type)
   {
      set_transition(type, i_hdl);
   }

   public void set_duration(float t)
   {
      set_duration(t, i_hdl);
   }

   public void set_parameter(String key, String value)
   {
      set_parameter(key, value, i_hdl);
   }

   public void show(String text)
   {
      show(text, i_hdl);
   }

   public void continue_text(String text)
   {
      continue_text(text, i_hdl);
   }

   public void set_leading(float l)
   {
      set_leading(l, i_hdl);
   }

   public void set_text_rise(float rise)
   {
      set_text_rise(rise, i_hdl);
   }

   public void set_horiz_scaling(float scale)
   {
      set_horiz_scaling(scale, i_hdl);
   }

   public void set_text_rendering(int mode)
   {
      set_text_rendering(mode, i_hdl);
   }

   public void set_text_matrix(float a, float b, float c, float d, float e, float f)
   {
      set_text_matrix(a, b, c, d, e, f, i_hdl);
   }

   public void set_text_pos(float x, float y)
   {
      set_text_pos(x, y, i_hdl);
   }

   public void set_char_spacing(float spacing)
   {
      set_char_spacing(spacing, i_hdl);
   }

   public void set_word_spacing(float spacing)
   {
      set_word_spacing(spacing, i_hdl);
   }

   public void save()
   {
      save(i_hdl);
   }

   public void restore()
   {
      restore(i_hdl);
   }

   public void translate(float tx, float ty)
   {
      translate(tx, ty, i_hdl);

   }

   public void scale(float sx, float sy)
   {
      scale(sx, sy, i_hdl);
   }

   public void rotate(float phi)
   {
      rotate(phi, i_hdl);
   }

   public void setdash(float d1, float d2)
   {
      setdash(d1, d2, i_hdl);
   }

   public void setflat(float flat)
   {
      setflat(flat, i_hdl);
   }

   public void set_fillrule(String fillrule)
   {
      set_fillrule(fillrule, i_hdl);
   }

   public void setlinejoin(int join)
   {
      setlinejoin(join, i_hdl);
   }

   public void setlinecap(int cap)
   {
      setlinecap(cap, i_hdl);
   }

   public void setmiterlimit(float miter)
   {
      setmiterlimit(miter, i_hdl);
   }

   public void setlinewidth(float width)
   {
      setlinewidth(width, i_hdl);
   }

   public void curveto(float x1, float y1, float x2, float y2, float x3, float y3)
   {
      curveto(x1, y1, x2, y2, x3, y3, i_hdl);
   }

   public void circle(float x, float y, float r)
   {
      circle(x, y, r, i_hdl);
   }

   public void arc(float x, float y, float r, float alpha1, float alpha2)
   {
      arc(x, y, r, alpha1, alpha2, i_hdl);
   }

   public void rect(float x, float y, float width, float height)
   {
      rect(x, y, width, height, i_hdl);
   }

   public void closepath_stroke()
   {
      closepath_stroke(i_hdl);
   }

   public void closepath()
   {
      closepath(i_hdl);
   }

   public void fill()
   {
      fill(i_hdl);
   }

   public void fill_stroke()
   {
      fill_stroke(i_hdl);
   }

   public void closepath_fill_stroke()
   {
      closepath_fill_stroke(i_hdl);
   }

   public void endpath()
   {
      endpath(i_hdl);
   }

   public void clip()
   {
      clip(i_hdl);
   }

   public void setgray_fill(float g)
   {
      setgray_fill(g, i_hdl);
   }

   public void setgray_stroke(float g)
   {
      setgray_stroke(g, i_hdl);
   }

   public void setgray(float g)
   {
      setgray(g, i_hdl);
   }

   public void setrgbcolor_fill(float red, float green, float blue)
   {
      setrgbcolor_fill(red, green, blue, i_hdl);
   }

   public void setrgbcolor_stroke(float red, float green, float blue)
   {
      setrgbcolor_stroke(red, green, blue, i_hdl);
   }

   public void setrgbcolor(float red, float green, float blue)
   {
      setrgbcolor(red, green, blue, i_hdl);
   }

   public void attach_file(float llx, float lly, float urx, float ury, String filename, String description, String author, String mimetype, String icon)
   {
      attach_file(llx, lly, urx, ury, filename, description, author, mimetype, icon, i_hdl);
   }

   public void add_note(float llx, float lly, float urx, float ury, String contents, String title, String icon, int open)
   {
      add_note(llx, lly, urx, ury, contents, title, icon, open, i_hdl);
   }

   public void add_pdflink(float llx, float lly, float urx, float ury, String filename, int page, String dest)
   {
      add_pdflink(llx, lly, urx, ury, filename, page, dest, i_hdl);
   }

   public void add_launchlink(float llx, float lly, float urx, float ury, String filename)
   {
      add_launchlink(llx, lly, urx, ury, filename, i_hdl);
   }

   public void add_locallink(float llx, float lly, float urx, float ury, int page, String dest)
   {
      add_locallink(llx, lly, urx, ury, page, dest, i_hdl);
   }

   public void add_weblink(float llx, float lly, float urx, float ury, String url)
   {
      add_weblink(llx, lly, urx, ury, url, i_hdl);
   }

   public void set_border_style(String style, float width)
   {
      set_border_style(style, width, i_hdl);
   }

   public void set_border_color(float red, float green, float blue)
   {
      set_border_color(red, green, blue, i_hdl);
   }

   public void set_border_dash(float d1, float d2)
   {
      set_border_dash(d1, d2, i_hdl);
   }



   //-----------------------------------------------------------------------
   // Following native methods uses JNI to comunicate with webpdf.dll
   //-----------------------------------------------------------------------

   public native int get_majorversion(); 
   public native int get_minorversion(); 
   
   private native int create_Handler(); 
   private native int open(String filename, int i_hdl); 
   private native void set_info(String key, String value, int i_hdl);   
   private native void close(int i_hdl);
   private native int findfont(String fontname, String encoding, int embed, int i_hdl);
   private native void begin_page(float width, float height, int i_hdl);
   private native int open_GIF(String filename, int i_hdl); 
   private native int open_JPEG(String filename, int i_hdl);
   private native int get_image_width(int image, int i_hdl);
   private native int get_image_height(int image, int i_hdl);
   private native void place_image(int image, float x, float y, float scale, int i_hdl);
   private native void setfont(int font, float fontsize, int i_hdl);
   private native void show_xy(String text, float x, float y, int i_hdl);
   private native void moveto(float x, float y, int i_hdl); 
   private native void lineto(float x, float y, int i_hdl); 
   private native void stroke(int i_hdl); 
   private native void end_page(int i_hdl); 
   private native String get_fontname( int i_hdl);
   private native float get_fontsize( int i_hdl);
   private native int get_font( int i_hdl);
   private native float stringwidth(String text, int font, float size, int i_hdl);
   private native int open_image(String type, String source, String data, int len, int width, int height, int components, int bpc, String params, int i_hdl);
   private native int open_TIFF(String filename, int i_hdl);
   private native int open_CCITT(String filename, int width, int height, int BitReverse, int K, int BlackIs1, int i_hdl);
   private native int add_bookmark(String text, int parent, int open, int i_hdl);
   private native void set_transition(String type, int i_hdl);
   private native void set_duration(float t, int i_hdl);
   private native void set_parameter(String key, String value, int i_hdl);
   private native void show(String text, int i_hdl);
   private native void continue_text(String text, int i_hdl);
   private native void set_leading(float l, int i_hdl);
   private native void set_text_rise(float rise, int i_hdl);
   private native void set_horiz_scaling(float scale, int i_hdl);
   private native void set_text_rendering(int mode, int i_hdl);
   private native void set_text_matrix(float a, float b, float c, float d, float e, float f, int i_hdl);
   private native void set_text_pos(float x, float y, int i_hdl);
   private native void set_char_spacing(float spacing, int i_hdl);
   private native void set_word_spacing(float spacing, int i_hdl);
   private native void save( int i_hdl);
   private native void restore( int i_hdl);
   private native void translate(float tx, float ty, int i_hdl);
   private native void scale(float sx, float sy, int i_hdl);
   private native void rotate(float phi, int i_hdl);
   private native void setdash(float d1, float d2, int i_hdl);
   //private native void setpolydash(float[] darray, int length, int i_hdl);
   private native void setflat(float flat, int i_hdl);
   private native void set_fillrule(String fillrule, int i_hdl);
   private native void setlinejoin(int join, int i_hdl);
   private native void setlinecap(int cap, int i_hdl);
   private native void setmiterlimit(float miter, int i_hdl);
   private native void setlinewidth(float width, int i_hdl);
   private native void curveto(float x1, float y1, float x2, float y2, float x3, float y3, int i_hdl);
   private native void circle(float x, float y, float r, int i_hdl);
   private native void arc(float x, float y, float r, float alpha1, float alpha2, int i_hdl);
   private native void rect(float x, float y, float width, float height, int i_hdl);
   private native void closepath_stroke( int i_hdl);
   private native void closepath( int i_hdl);
   private native void fill( int i_hdl);
   private native void fill_stroke( int i_hdl);
   private native void closepath_fill_stroke( int i_hdl);
   private native void endpath( int i_hdl);
   private native void clip( int i_hdl);
   private native void setgray_fill(float g, int i_hdl);
   private native void setgray_stroke(float g, int i_hdl);
   private native void setgray(float g, int i_hdl);
   private native void setrgbcolor_fill(float red, float green, float blue, int i_hdl);
   private native void setrgbcolor_stroke(float red, float green, float blue, int i_hdl);
   private native void setrgbcolor(float red, float green, float blue, int i_hdl);
   private native void attach_file(float llx, float lly, float urx, float ury, String filename, String description, String author, String mimetype, String icon, int i_hdl);
   private native void add_note(float llx, float lly, float urx, float ury, String contents, String title, String icon, int open, int i_hdl);
   private native void add_pdflink(float llx, float lly, float urx, float ury, String filename, int page, String dest, int i_hdl);
   private native void add_launchlink(float llx, float lly, float urx, float ury, String filename, int i_hdl);
   private native void add_locallink(float llx, float lly, float urx, float ury, int page, String dest, int i_hdl);
   private native void add_weblink(float llx, float lly, float urx, float ury, String url, int i_hdl);
   private native void set_border_style(String style, float width, int i_hdl);
   private native void set_border_color(float red, float green, float blue, int i_hdl);
   private native void set_border_dash(float d1, float d2, int i_hdl);
}


