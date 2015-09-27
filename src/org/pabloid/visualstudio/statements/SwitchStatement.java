/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class SwitchStatement extends Statement
  {

    private String expression;
    private String body;

    public SwitchStatement(String body, String condition)
      {
        type = SWITCH;
        this.expression = body;
        this.body = condition;
      }

    public String toString()
      {
        return "switch(" + expression + ")\n{\n" + body + "}";
      }

    public String toShortString()
      {
        return "switch(" + expression + ")";
      }

    public String getBody()
      {
        return body;
      }

    public void setBody(String body)
      {
        this.body = body;
      }

    public String getExpression()
      {
        return expression;
      }

    public void setExpression(String expression)
      {
        this.expression = expression;
      }
  }
