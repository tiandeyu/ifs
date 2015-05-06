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
 * File        : FndDataAdapter.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * $Log: FndDataAdapter.java,v $
 * 2008/06/26 mapelk - Bug 74852, Created. Programming Model for Activities.
 *  
 */


package ifs.fnd.webfeature;

import ifs.fnd.record.FndAbstractRecord;

/**
 * Supper interface for any data adapter
 */

public interface FndDataAdapter {
  
   public FndAbstractRecord getTemplate();
}
