/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class AssignStatement extends Statement
  {

    private String variable;
    private String value;

    public AssignStatement(String var, String val)
      {
        type = ASSIGN;
        this.variable = var;
        this.value = val;
      }

    public String toString()
      {
        return variable + "=" + value + ";";
      }

    public String toShortString()
      {
        return variable + "=...";
      }

    public String getValue()
      {
        return value;
      }

    public void setValue(String value)
      {
        this.value = value;
      }

    public String getVariable()
      {
        return variable;
      }

    public void setVariable(String variable)
      {
        this.variable = variable;
      }
  }
