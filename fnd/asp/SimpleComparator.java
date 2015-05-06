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
 * File        : SimpleComparator.java
 * Description : Comparator of item names in a Buffer
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Rifki R  2005-Jul-04 - Created
 * ----------------------------------------------------------------------------
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.1  2005/07/05 13:27:28  riralk
 * Simple implementation of ItemComparator to sort a buffer using the names of it's items
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.util.Str;
import ifs.fnd.service.*;
import ifs.fnd.buffer.*;
import java.util.*;
import java.text.*;

public class SimpleComparator implements ItemComparator {   
   
   private Collator collator; // for String comparsion
   private boolean ascending;
   
   /**
    * Create a SimpleComparator.
    */
  
   public SimpleComparator(boolean ascending) throws Exception {    
     this.collator = Collator.getInstance(Locale.US);
     this.ascending = ascending;
   }

   /**
    * Compare two items using their item name    
    */
   public int compare(Item i1, Item i2) throws Exception {     
      String s1 = i1.getName();
      String s2 = i2.getName();     

      int r = collator.compare(s1, s2);     
      return ascending ? r : -r;
   }   
}
