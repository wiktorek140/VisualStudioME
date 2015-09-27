/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class ContinueStatement extends Statement
  {
      {type=CONTINUE;}

    public String toString()
      {
        return "continue;";
      }

    public String toShortString()
      {
        return "continue";
      }
  }
