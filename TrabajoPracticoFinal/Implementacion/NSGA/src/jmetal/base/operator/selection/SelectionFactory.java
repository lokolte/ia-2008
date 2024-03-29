/**
 * SelectionFactory.java
 *
 * @author Juanjo Durillo
 * @version 1.1
 */

package jmetal.base.operator.selection;

import jmetal.base.Configuration;
import jmetal.base.Operator;
import jmetal.util.JMException;

/**
 * Class implementing a selection operator factory.
 */
public class SelectionFactory {
    
  /**
   * Gets a selection operator through its name.
   * @param name of the operator
   * @return the operator
   * @throws JMException 
   */
  public static Operator getSelectionOperator(String name) throws JMException {
    if (name.equalsIgnoreCase("BinaryTournament"))
      return new BinaryTournament();
    else if (name.equalsIgnoreCase("BinaryTournament2"))
      return new BinaryTournament2();
    else {
      Configuration.logger_.severe("Operator '" + name + "' not found ");
      throw new JMException("Exception in " + name + ".getSelectionOperator()") ;
    } // else    
  } // getSelectionOperator
    
} // SelectionFactory
