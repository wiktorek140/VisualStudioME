/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public class CallStatement extends Statement
  {

    private String methodName;
    private String params;

    public CallStatement(String methodName, String params)
      {
        this.methodName = methodName;
        this.params = params;
        type = CALL;
      }

    public String toString()
      {
        return methodName + "(" + params + ");";
      }

    public String toShortString()
      {
        return methodName + "()";
      }

    public String getMethodName()
      {
        return methodName;
      }

    public void setMethodName(String methodName)
      {
        this.methodName = methodName;
      }

    public String getParams()
      {
        return params;
      }

    public void setParams(String params)
      {
        this.params = params;
      }
  }
