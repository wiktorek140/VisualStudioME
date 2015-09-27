/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio.statements;

import javax.microedition.lcdui.*;

/**
 *
 * @author pabloid
 */
public class StatementForm extends Form implements ItemStateListener
  {

    private Statement statement;
    private TextField tf1 = new TextField("", "", 1024, TextField.ANY);
    private TextField tf2 = new TextField("", "", 1024, TextField.ANY);
    private TextField tf3 = new TextField("", "", 1024, TextField.ANY);
    private TextField tf4 = new TextField("", "", 1024, TextField.ANY);

    public StatementForm()
      {
        super(null);
      }

    public Statement getStatement()
      {
        return statement;
      }

    public void setStatement(Statement st)
      {
        deleteAll();
        this.statement = st;
        switch (st.type)
          {
            case Statement.IF:
                setTitle("If");
                tf1.setLabel("Условие");
                tf1.setString(((IfStatement) st).getCondition());
                append(tf1);
                tf2.setLabel("Если верно");
                tf2.setString(((IfStatement) st).getIfTrue());
                append(tf2);
                tf3.setLabel("Если неверно");
                tf3.setString(((IfStatement) st).getIfFalse());
                append(tf3);
                break;
            case Statement.FOR:
                setTitle("For");
                tf1.setLabel("Инициализация");
                tf1.setString(((ForStatement) st).getInit());
                append(tf1);
                tf2.setLabel("Условие продолжения");
                tf2.setString(((ForStatement) st).getCondition());
                append(tf2);
                tf3.setLabel("Приращение");
                tf3.setString(((ForStatement) st).getIncr());
                append(tf3);
                tf4.setLabel("Действия");
                tf4.setString(((ForStatement) st).getBody());
                append(tf4);
                break;
            case Statement.WHILE:
                setTitle("While");
                tf1.setLabel("Условие");
                tf1.setString(((WhileStatement) st).getCondition());
                append(tf1);
                tf2.setLabel("Действия");
                tf2.setString(((WhileStatement) st).getBody());
                append(tf2);
                break;
            case Statement.DO:
                setTitle("Do");
                tf1.setLabel("Условие");
                tf1.setString(((DoStatement) st).getCondition());
                append(tf1);
                tf2.setLabel("Действия");
                tf2.setString(((DoStatement) st).getBody());
                append(tf2);
                break;
            case Statement.TRY:
                setTitle("Try");
                tf1.setLabel("Действия");
                tf1.setString(((TryStatement) st).getBody());
                append(tf1);
                tf2.setLabel("Действия при ошибке");
                tf2.setString(((TryStatement) st).getCatching());
                append(tf2);
                break;
            case Statement.CALL:
                setTitle("Вызов функции");
                tf1.setLabel("Название");
                tf1.setString(((CallStatement) st).getMethodName());
                append(tf1);
                tf2.setLabel("Параметры через запятую");
                tf2.setString(((CallStatement) st).getParams());
                append(tf2);
                break;
            case Statement.ASSIGN:
                setTitle("Присваивание");
                tf1.setLabel("Переменная");
                tf1.setString(((AssignStatement) st).getVariable());
                append(tf1);
                tf2.setLabel("Значение");
                tf2.setString(((AssignStatement) st).getValue());
                append(tf2);
                break;
            case Statement.BREAK:
                setTitle("Break");
                break;
            case Statement.CONTINUE:
                setTitle("Continue");
                break;
            case Statement.SWITCH:
                setTitle("Switch");
                tf1.setLabel("Выражение");
                tf1.setString(((SwitchStatement) st).getExpression());
                append(tf1);
                tf2.setLabel("Действия");
                tf2.setString(((SwitchStatement) st).getBody());
                append(tf2);
                break;
          }
      }

    public void itemStateChanged(Item item)
      {
        switch (statement.type)
          {
            case Statement.IF:
                IfStatement ifst = (IfStatement) statement;
                if (item == tf1)
                    ifst.setCondition(tf1.getString());
                if (item == tf2)
                    ifst.setIfTrue(tf2.getString());
                if (item == tf3)
                    ifst.setIfFalse(tf3.getString());
                break;
            case Statement.FOR:
                ForStatement forst = (ForStatement) statement;
                if (item == tf1)
                    forst.setInit(tf1.getString());
                if (item == tf2)
                    forst.setCondition(tf2.getString());
                if (item == tf3)
                    forst.setIncr(tf3.getString());
                if (item == tf4)
                    forst.setBody(tf4.getString());
                break;
            case Statement.WHILE:
                WhileStatement whilest = (WhileStatement) statement;
                if (item == tf1)
                    whilest.setCondition(tf1.getString());
                if (item == tf2)
                    whilest.setBody(tf2.getString());
                break;
            case Statement.DO:
                DoStatement dost = (DoStatement) statement;
                if (item == tf1)
                    dost.setCondition(tf1.getString());
                if (item == tf2)
                    dost.setBody(tf2.getString());
                break;
            case Statement.TRY:
                TryStatement tryst = (TryStatement) statement;
                if (item == tf1)
                    tryst.setBody(tf1.getString());
                if (item == tf2)
                    tryst.setCatching(tf2.getString());
                break;
            case Statement.CALL:
                CallStatement callst = (CallStatement) statement;
                if (item == tf1)
                    callst.setMethodName(tf1.getString());
                if (item == tf2)
                    callst.setParams(tf2.getString());
                break;
            case Statement.ASSIGN:
                AssignStatement assignst = (AssignStatement) statement;
                if (item == tf1)
                    assignst.setVariable(tf1.getString());
                if (item == tf2)
                    assignst.setValue(tf2.getString());
                break;
            case Statement.SWITCH:
                SwitchStatement switchst = (SwitchStatement) statement;
                if (item == tf1)
                    switchst.setExpression(tf1.getString());
                if (item == tf2)
                    switchst.setBody(tf2.getString());
                break;
          }
      }

    public void setStatement(int type)
      {
        switch (type)
          {
            case Statement.IF:
                setStatement(new IfStatement("", "", ""));
                break;
            case Statement.FOR:
                setStatement(new ForStatement("", "", "", ""));
                break;
            case Statement.WHILE:
                setStatement(new WhileStatement("", ""));
                break;
            case Statement.DO:
                setStatement(new DoStatement("", ""));
                break;
            case Statement.ASSIGN:
                setStatement(new AssignStatement("", ""));
                break;
            case Statement.CALL:
                setStatement(new CallStatement("", ""));
                break;
            case Statement.SWITCH:
                setStatement(new SwitchStatement("", ""));
                break;
            case Statement.TRY:
                setStatement(new TryStatement("", ""));
                break;
            case Statement.BREAK:
                setStatement(new BreakStatement());
                break;
            case Statement.CONTINUE:
                setStatement(new ContinueStatement());
                break;
          }
      }
  }
