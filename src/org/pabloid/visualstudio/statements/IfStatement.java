/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class IfStatement extends Statement
  {

    private String condition;
    private String ifTrue;
    private String ifFalse = null;


      {
        type = IF;
      }

    public IfStatement(String condition, String ifTrue, String ifFalse)
      {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = "".equals(ifFalse) ? null : ifFalse;
      }

    public IfStatement(String condition, String ifTrue)
      {
        this.condition = condition;
        this.ifTrue = ifTrue;
      }

    public String toString()
      {
        return "if(" + condition + ")\n{\n" + ifTrue + (ifFalse == null ? ("\n}") : ("\n}else{\n" + ifFalse + "\n}"));
      }

    public String toShortString()
      {
        return "if(" + condition + ")";
      }

    public void setCondition(String condition)
      {
        this.condition = condition;
      }

    public void setIfFalse(String ifFalse)
      {
        this.ifFalse = ifFalse == null ? "" : ifFalse;
      }

    public void setIfTrue(String ifTrue)
      {
        this.ifTrue = ifTrue;
      }

    public String getCondition()
      {
        return condition;
      }

    public String getIfFalse()
      {
        return ifFalse;
      }

    public String getIfTrue()
      {
        return ifTrue;
      }
  }
