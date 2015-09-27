/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

/**
 *
 * @author pabloid
 */
public abstract class Statement
  {

    public static final int IF = 0;
    public static final int FOR = 1;
    public static final int WHILE = 2;
    public static final int DO = 3;
    public static final int TRY = 4;
    public static final int CALL = 5;
    public static final int ASSIGN = 6;
    public static final int BREAK = 7;
    public static final int CONTINUE = 8;
    public static final int SWITCH = 9;

    protected int type;

    public int getType()
      {
        return type;
      }

   public abstract String toShortString();
  }
