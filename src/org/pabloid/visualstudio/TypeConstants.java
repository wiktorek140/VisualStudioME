/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pabloid.visualstudio;

/**
 *
 * @author P@bloid
 */
public interface TypeConstants
{

    public static final Type INT = new Type("int");
    public static final Type SHORT = new Type("short");
    public static final Type BYTE = new Type("byte");
    public static final Type CHAR = new Type("char");
    public static final Type FLOAT = new Type("float");
    public static final Type DOUBLE = new Type("double");
    public static final Type LONG = new Type("long");
    public static final Type BOOLEAN = new Type("boolean");
    public static final Type VOID = new Type("void");
    public static final Type FORM = new Type("Form");
    public static final Type LIST = new Type("List");
    public static final Type ALERT = new Type("Alert");
    public static final Type TEXTBOX = new Type("TextBox");
    public static final Type IMAGE = new Type("Image");
    public static final Type COMMAND = new Type("Command");
    public static final Type OBJECT = new Type("Object");
    public static final Type TICKER = new Type("Ticker");
    public static final Type DISPLAY = new Type("Display");
    public static final Type CONSTRUCTOR = new Type("");
    public static final Type GRAPHICS = new Type("Graphics");
    public static final Type STRINGITEM = new Type("StringItem");
    public static final Type STRING = new Type("String");
    public static final Type GAUGE = new Type("Gauge");
    public static final Type CHOICEGROUP = new Type("ChoiceGroup");
    public static final Type DATEFIELD = new Type("DateField");
    public static final Type IMAGEITEM = new Type("ImageItem");
    public static final Type SPACER = new Type("Spacer");
    public static final Type TEXTFIELD = new Type("TextField");
    
    public static final int DEFAULT = 0;
    public static final int PUBLIC = 1;
    public static final int PRIVATE = 2;
    public static final int PROTECTED = 3;
}
