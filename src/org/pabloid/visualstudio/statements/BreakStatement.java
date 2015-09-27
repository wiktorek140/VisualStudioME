/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class BreakStatement extends Statement{
    
    {type=BREAK;}
    public String toString()
      {
        return "break;";
      }
    public String toShortString()
    {
        return "break";
    }

}
