/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class ForStatement extends Statement
  {

    private String init;
    private String condition;
    private String incr;
    private String body;

    public ForStatement(String init, String condition, String incr, String body)
      {
        type = FOR;
        this.init = init;
        this.condition = condition;
        this.incr = incr;
        this.body = body;
      }

    public String toString()
      {
        return "for(" + init + ";" + condition + ";" + incr + ")\n{" + body + "\n}";
      }

    public String toShortString()
      {
        return "for(" + init + ")";
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

    public String getIncr()
      {
        return incr;
      }

    public void setIncr(String incr)
      {
        this.incr = incr;
      }

    public String getInit()
      {
        return init;
      }

    public void setInit(String init)
      {
        this.init = init;
      }
  }
