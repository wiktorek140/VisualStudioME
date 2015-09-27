/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class WhileStatement extends Statement
  {

    private String condition;
    private String body;

    public WhileStatement(String condition, String ifTrue)
      {
        this.condition = condition;
        this.body = ifTrue;
        type = WHILE;
      }

    public String toString()
      {
        return "while(" + condition + ")\n{\n" + body + "\n}";
      }

    public String toShortString()
      {
        return "while(" + condition + ")";
      }

    public String getBody()
      {
        return body;
      }

    public void setBody(String body)
      {
        this.body = body;
      }

    public String getCondition()
      {
        return condition;
      }

    public void setCondition(String condition)
      {
        this.condition = condition;
      }
  }
