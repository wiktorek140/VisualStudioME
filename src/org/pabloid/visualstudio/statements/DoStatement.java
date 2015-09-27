/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class DoStatement extends Statement
  {

    private String condition;
    private String body;

    public DoStatement(String condition, String ifTrue)
      {
        type = DO;
        this.condition = condition;
        this.body = ifTrue;
      }

    public String toString()
      {
        return "do\n{\n" + body + "\n}while(" + condition + ");";
      }

    public String toShortString()
      {
        return "do .. while(" + condition + ")";
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
