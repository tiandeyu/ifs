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
 * File        : FndDataSetFactory.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * $Log: FndDataSetFactory.java,v $
 * 2008/06/26 mapelk - Bug 74852, Created. Programming Model for Activities.
 * 
 */

package ifs.fnd.asp;

import ifs.fnd.webfeature.FndWebFeature;

/**
 * Factory class which construct the ASPRowSet instance depending on the 
 * programming model. For FndWebFeatures, it returns the FndDataSet instance 
 * and otherwise BufferedDataSet. 
 */
public class FndDataSetFactory {
   
   /** Creates a new instance of FndDataSetFactory */
   FndDataSetFactory() {
   }
   
   ASPRowSet createDataSet(ASPPage page, ASPBlock block)
   {
      if (page instanceof FndWebFeature)
         return new FndDataSet(block);
      else
         return new BufferedDataSet(block);
   }
   
}
