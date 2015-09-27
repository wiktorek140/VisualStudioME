/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class TryStatement extends Statement
  {

    private String catching;
    private String body;

    public TryStatement(String body, String condition)
      {
        type = TRY;
        this.catching = condition;
        this.body = body;
      }

    public String toString()
      {
        return "try\n{\n" + body + "\n}catch(Throwable _throwable){\n" + catching + "\n}";
      }

    public String toShortString()
      {
        return "try{}catch(Throwable){}";
      }

    public String getBody()
      {
        return body;
      }

    public void setBody(String body)
      {
        this.body = body;
      }

    public String getCatching()
      {
        return catching;
      }

    public void setCatching(String catching)
      {
        this.catching = catching;
      }
  }
