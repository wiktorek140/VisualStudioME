/**
 *
 * @author note173 mailto:note173@mail.ru
 */
package org.pabloid.editor;

import org.pabloid.util.Highlighter;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.*;
import org.pabloid.util.*;

public class Editor extends GameCanvas implements Runnable
{

    public static final int MODE_LOWER_CASE = 1;
    public static final int MODE_UPPER_CASE = 2;
    public static final int MODE_NUMBERS = 3;
    public static final int MODE_ULOWER_CASE = 4;
    static final String sMenuButton = "Меню";
    static final String sClearButton = "<C";
    static final String sReturnButton = "LF";
    static final char[] KEY_NUM1_CHARS = new char[]
    {
        '.', ',', '-', '?', '!', '\'', '"', '+', '/', ':', ';', '(', ')', '1'
    };
    static final char[] KEY_NUM2_CHARS = new char[]
    {
        'a', 'b', 'c', '2',
    };
    static final char[] KEY_NUM3_CHARS = new char[]
    {
        'd', 'e', 'f', '3',
    };
    static final char[] KEY_NUM4_CHARS = new char[]
    {
        'g', 'h', 'i', '4',
    };
    static final char[] KEY_NUM5_CHARS = new char[]
    {
        'j', 'k', 'l', '5',
    };
    static final char[] KEY_NUM6_CHARS = new char[]
    {
        'm', 'n', 'o', '6',
    };
    static final char[] KEY_NUM7_CHARS = new char[]
    {
        'p', 'q', 'r', 's', '7'
    };
    static final char[] KEY_NUM8_CHARS = new char[]
    {
        't', 'u', 'v', '8',
    };
    static final char[] KEY_NUM9_CHARS = new char[]
    {
        'w', 'x', 'y', 'z', '9'
    };
    static final char[] KEY_NUM0_CHARS = new char[]
    {
        ' ', '0'
    };
    static final char[] SPECIAL_CHARS =
    {
        ';', '(', ')', '{', '}', '[', ']', '!',
        '-', '_', '+', '=', '|', '\\', '/', '"', '\'',
        '@', '#', '$', '%', '^', '&', '*', ':',
        '<', '>', '?', '.', ','
    };
    EditorSettings settings;
    public static final int STATE_SPLASH = 1;
    public static final int STATE_NORMAL = 2;
    public static final int STATE_SELECT_CHAR = 3;
    public static final int STATE_MENU = 4;
    public int currentMode;
    long splashStartTime;
    static final int splashTimeout = 1500;
    int clearKeyCode = Integer.MIN_VALUE;
    Vector vCurText;
    int iCurLine;
    StringBuffer currentText = new StringBuffer();
    //String currentString = new String();
    int lastPressedKey = Integer.MIN_VALUE;
    int currentKeyStep = 0;
    Font inputFont = null, linesFont = null, titleFont = null;
    int inputWidth = 0;
    int inputHeight = 0;
    int inputTranslationY = 10;
    int inputTranslationX = 10;
    int titleHeight;
    int mode = MODE_LOWER_CASE;
    String sMode = "abc";
    String sTitle = "";
    //boolean bSelectingChar;
    long selectingCharKeyTime;
    static final int selectingCharKeyTimeout = 500;
    int selectingCharFocus;
    Image imgSelectingCharset;
    int selectingTableWidth;
    int selectingCharW, selectingCharH;
    long lastKeyTimestamp = 0;
    long maxKeyDelay = 1000L;
    int caretIndex = 0;
    int caretLeft = 0;
    boolean caretBlinkOn = true;
    long caretBlinkDelay = 500L;
    long lastCaretBlink = 0;
    boolean goToNextChar = true;
    //#ifdef DEBUG
    String sMsg = "";
    //#endif
    int iNumbersWidth;
    int nLinesMax;
    int selStartLine, selStartX, selStart, selEndLine, selEnd, selEndX;
    boolean bSelecting;
    int offsetLine;
    boolean bScrollMode;
    int scrollStartY;
    int errorLine, errorStart, errorEnd;
    boolean bShowLines;
    Display disp;
    TextBox tbText;
    Vector vCommands;
    //boolean bShowMenu;
    CommandListener commandListener;
    int menuFocus;
    int menuX, menuY, menuW, menuH;
    int menuBarH;
    int menuBarCenterStrPosition;
    Highlighter hlHighlighter = new Highlighter("/j2me.stx");
    Image imError;
    boolean bShowChars = false;
    char[] vChars = null;
    boolean bCanChangeSyntax = true;
    boolean bSyntax;
    boolean bCheck;

    public Editor(Display d, String title)
    {
        super(false);
        currentMode = STATE_SPLASH;
        disp = d;
        //d.setCurrent(this);
        sTitle = title;
        settings = new EditorSettings();

        inputFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        linesFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        titleFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);

        iCurLine = 0;
        offsetLine = 0;
        vCurText = new Vector();
        vCurText.addElement(new EditorItem("", false));
        try
        {
            imError = Image.createImage("/error.png");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        vCommands = new Vector();

        if (!settings.hasSettings())
        {
            settings.bSyntax = true;
            settings.bFullscreen = true;
            settings.bLineNumbers = true;
        } else
            settings.loadSettings();
        reinit();

        new Thread(this).start();
    }

    public Editor(Display disp)
    {
        this(disp, "");
    }

    public EditorSettings getSettings()
    {
        return settings;
    }

    public void setTitle(String title)
    {
        sTitle = title;
    }

    public String getTitle()
    {
        return sTitle;
    }

    public void showSettings()
    {
        settings.show(disp, this);
    }

    public void showNotify()
    {
//        settings.saveSettings();
        //reinit ();
    }

    public void setCommandListener(CommandListener listener)
    {
        commandListener = listener;
    }

    public void addCommand(Command c)
    {
        if (!vCommands.contains(c))
        {
            vCommands.addElement(c);
            if (menuW - 4 < linesFont.stringWidth(c.getLabel()))
            {
                menuW = 4 + linesFont.stringWidth(c.getLabel());
                menuX = getWidth() - menuW;
            }
            menuH = 4 + vCommands.size() * (linesFont.getHeight() + 1);
            menuY = getHeight() - menuBarH - menuH;
        }
    }

    public void showMenu(boolean bShow)
    {
        if (bShow)
        {
            currentMode = STATE_MENU;
            bSyntax = settings.bSyntax;
            bCheck = settings.bCheck;
            settings.bCheck = false;
            settings.bSyntax = false;
        } else
        {
            currentMode = STATE_NORMAL;
            settings.bSyntax = bSyntax;
            settings.bCheck = bCheck;
        }
        menuFocus = 0;
    }

    public void reinit()
    {
        currentMode = STATE_SPLASH;
        bScrollMode = false;
        bShowLines = settings.bLineNumbers;
        bSyntax = settings.bSyntax;
        bCheck = settings.bCheck;
        setFullScreenMode(settings.bFullscreen);

        iNumbersWidth = 1 + linesFont.stringWidth("000") + 1;

        titleHeight = 1 + titleFont.getHeight() + 2;
        inputTranslationY = titleHeight + 1;

        inputHeight = getHeight() - inputTranslationY;

        if (bShowLines)
            inputTranslationX = iNumbersWidth + 1;
        else
            inputTranslationX = 1;
        inputWidth = getWidth() - inputTranslationX;

        nLinesMax = inputHeight / (1 + inputFont.getHeight()) - 1;

        menuX = getWidth();
        menuY = getHeight();
        menuW = 0;
        menuH = 0;
        menuBarH = linesFont.getHeight() + 2;
        menuFocus = 0;

        for (Enumeration en = vCommands.elements(); en.hasMoreElements();)
        {
            Command c = (Command) en.nextElement();
            if (menuW - 4 < linesFont.stringWidth(c.getLabel()))
            {
                menuW = 4 + linesFont.stringWidth(c.getLabel());
                menuX = getWidth() - menuW;
            }
            menuH = 4 + vCommands.size() * (linesFont.getHeight() + 1);
            menuY = getHeight() - menuBarH - menuH;
        }

        menuBarCenterStrPosition = (getWidth() - linesFont.stringWidth(sReturnButton)) / 2;

        //bSelectingChar = false;
        splashStartTime = System.currentTimeMillis();
        selectingCharKeyTime = -1;
        selectingCharFocus = 0;

        imgSelectingCharset = Image.createImage(getWidth() - 20, getHeight() - 20);
        Graphics g = imgSelectingCharset.getGraphics();
        g.setFont(inputFont);
        selectingCharW = inputFont.charWidth(SPECIAL_CHARS[0]);
        for (int i = 1; i < SPECIAL_CHARS.length; i++)
            if (inputFont.charWidth(SPECIAL_CHARS[i]) > selectingCharW)
                selectingCharW = inputFont.charWidth(SPECIAL_CHARS[i]);
        selectingCharW += 4;
        selectingCharH = inputFont.getHeight() + 1;
        selectingTableWidth = imgSelectingCharset.getWidth() / selectingCharW;
        g.setColor(220, 220, 255);
        g.fillRect(0, 0, imgSelectingCharset.getWidth(), imgSelectingCharset.getHeight());
        g.setColor(50, 50, 100);
        g.drawRect(0, 0, imgSelectingCharset.getWidth() - 1, imgSelectingCharset.getHeight() - 1);
        g.setColor(0);
        for (int i = 0; i < SPECIAL_CHARS.length; i++)
        {
            int cy = (i / selectingTableWidth) * selectingCharH;
            int cx = (i - (i / selectingTableWidth) * selectingTableWidth) * selectingCharW;
            int dx = selectingCharW / 2 - inputFont.charWidth(SPECIAL_CHARS[i]);
            g.drawChar(SPECIAL_CHARS[i], cx + 1 + dx, cy + 1, Graphics.LEFT | Graphics.TOP);
        }
        errorLine = -1;
        errorStart = -1;
        errorEnd = -1;
        currentMode = STATE_NORMAL;
    }

    public void setMode(int m)
    {
        if (m == MODE_LOWER_CASE)
        {
            mode = m;
            sMode = "abc";
        } else if (m == MODE_UPPER_CASE)
        {
            mode = m;
            sMode = "ABC";
        } else if (m == MODE_ULOWER_CASE)
        {
            mode = m;
            sMode = "Abc";
        } else if (m == MODE_NUMBERS)
        {
            mode = m;
            sMode = "123";
        }
    }

    public void setError(int line, int start, int end)
    {
        errorLine = line - 1;
        errorStart = start - 1;
        errorEnd = end - 1;
    }

    public String getSelectedString()
    {
        if (selStartLine == selEndLine && selStart == selEnd)
            return "";

        StringBuffer sb = new StringBuffer();
        int line1 = selStartLine, line2 = selEndLine;
        int sel1 = selStart, sel2 = selEnd;
        if (line1 > line2)
        {
            int t = line1;
            line1 = line2;
            line2 = t;
            t = sel1;
            sel1 = sel2;
            sel2 = t;
        } else if (sel1 > sel2)
        {
            int t = sel1;
            sel1 = sel2;
            sel2 = t;
        }
        sb.append(vCurText.elementAt(line1).toString().substring(sel1));
        if (line1 != line2)
            sb.append("\n");
        for (int i = line1 + 1; i < line2; i++)
        {
            sb.append((String) vCurText.elementAt(i));
            sb.append("\n");
        }
        if (line2 != line1)
            sb.append(vCurText.elementAt(line2).toString().substring(0, sel2));
        return sb.toString();
    }

    public void setString(String s)
    {
        vCurText.removeAllElements();
        iCurLine = 0;
        offsetLine = 0;
        caretIndex = 0;
        selStartLine = 0;
        selStartX = 0;
        selStart = 0;
        selEndLine = 0;
        selEnd = 0;
        selEndX = 0;
        bSelecting = false;
        int i = 0;
        String cs = "";
        while (i < s.length())
        {
            if (s.charAt(i) == '\n' || s.charAt(i) == '\r')
            {
                vCurText.addElement(new EditorItem(cs));
                cs = "";
            } else if (i == s.length() - 1)
            {
                cs += s.charAt(i);
                vCurText.addElement(new EditorItem(cs));
            } else
                cs += s.charAt(i);
            i++;
        }
        if (s.length() == 0)
            vCurText.addElement(new EditorItem(""));
        currentText.delete(0, currentText.length());
        currentText.append(vCurText.elementAt(0).toString());
    }

    public String getString()
    {
        StringBuffer sb = new StringBuffer();
        for (Enumeration en = vCurText.elements(); en.hasMoreElements();)
        {
            sb.append(en.nextElement().toString());
            if (en.hasMoreElements())
                sb.append("\n");
        }
        return sb.toString();
    }

    public String getDisplayedString()
    {
        StringBuffer sb = new StringBuffer();
        for (int iLine = offsetLine; iLine < offsetLine + nLinesMax && iLine < vCurText.size(); iLine++)
            sb.append(vCurText.elementAt(iLine).toString() + "\n");
        return sb.toString();
    }

    public void setDisplayedString(String s)
    {
        int iLine = 0;
        Vector vText = new Vector();
        for (iLine = 0; iLine < offsetLine; iLine++)
            vText.addElement(vCurText.elementAt(iLine));
        String cs = "";
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == '\n' || s.charAt(i) == '\r')
            {
                vText.addElement(new EditorItem(cs));
                cs = "";
            } else if (i == s.length() - 1)
            {
                cs += s.charAt(i);
                vText.addElement(new EditorItem(cs));
            } else
                cs += s.charAt(i);
        for (iLine = offsetLine + nLinesMax; iLine < vCurText.size(); iLine++)
            vText.addElement(vCurText.elementAt(iLine));
        vCurText.removeAllElements();
        for (Enumeration e = vText.elements(); e.hasMoreElements(); vCurText.addElement(e.nextElement()));
    }

    public char[] getChars(int key, boolean bNum)
    {
        if (bNum)
        {
            char[] chars = new char[1];
            switch (key)
            {
                case Canvas.KEY_NUM1:
                    chars[0] = '1';
                    return chars;
                case Canvas.KEY_NUM2:
                    chars[0] = '2';
                    return chars;
                case Canvas.KEY_NUM3:
                    chars[0] = '3';
                    return chars;
                case Canvas.KEY_NUM4:
                    chars[0] = '4';
                    return chars;
                case Canvas.KEY_NUM5:
                    chars[0] = '5';
                    return chars;
                case Canvas.KEY_NUM6:
                    chars[0] = '6';
                    return chars;
                case Canvas.KEY_NUM7:
                    chars[0] = '7';
                    return chars;
                case Canvas.KEY_NUM8:
                    chars[0] = '8';
                    return chars;
                case Canvas.KEY_NUM9:
                    chars[0] = '9';
                    return chars;
                case Canvas.KEY_NUM0:
                    chars[0] = '0';
                    return chars;
            }
        } else
            switch (key)
            {
                case Canvas.KEY_NUM1:
                    return KEY_NUM1_CHARS;
                case Canvas.KEY_NUM2:
                    return KEY_NUM2_CHARS;
                case Canvas.KEY_NUM3:
                    return KEY_NUM3_CHARS;
                case Canvas.KEY_NUM4:
                    return KEY_NUM4_CHARS;
                case Canvas.KEY_NUM5:
                    return KEY_NUM5_CHARS;
                case Canvas.KEY_NUM6:
                    return KEY_NUM6_CHARS;
                case Canvas.KEY_NUM7:
                    return KEY_NUM7_CHARS;
                case Canvas.KEY_NUM8:
                    return KEY_NUM8_CHARS;
                case Canvas.KEY_NUM9:
                    return KEY_NUM9_CHARS;
                case Canvas.KEY_NUM0:
                    return KEY_NUM0_CHARS;
            }
        return null;
    }

    boolean isClearKey(int key)
    {
        return key == -8;
    }

    void clearChar()
    {
        if (((EditorItem) vCurText.elementAt(iCurLine)).isUneditable())
            return;
        bShowChars = false;
        vChars = null;
        if (selStartLine != selEndLine || (selStart != selEnd))
        {
            int t;
            if (selStartLine > selEndLine)
            {
                t = selStartLine;
                selStartLine = selEndLine;
                selEndLine = t;
                t = selStart;
                selStart = selEnd;
                selEnd = t;
            } else if (selStart > selEnd)
            {
                t = selStart;
                selStart = selEnd;
                selEnd = t;
            }
            caretIndex = selStart;
            iCurLine = selStartLine;

            currentText.delete(0, currentText.length());
            String sc = (vCurText.elementAt(selStartLine)).toString().substring(0, selStart);
            currentText.append(sc);
            sc = (vCurText.elementAt(selEndLine)).toString().substring(selEnd);
            currentText.append(sc);
            vCurText.setElementAt(new EditorItem(currentText.toString()), selStartLine);
            for (int i = selStartLine + 1; i <= selEndLine; i++)
                vCurText.removeElementAt(selStartLine + 1);
            selStart = selEnd = caretIndex;
            selStartLine = selEndLine = iCurLine;
            selStartX = selEndX = 0;
            updateCaretPosition();
            resetBlink();
        } else if (currentText.length() > 0 && caretIndex > 0)
        {
            caretIndex--;
            currentText.deleteCharAt(caretIndex);
            vCurText.setElementAt(new EditorItem(currentText.toString()), iCurLine);
            updateCaretPosition();
            resetBlink();
        } else if (iCurLine > 0 && caretIndex == 0)
        {
            currentText.delete(0, currentText.length());
            String tops = vCurText.elementAt(iCurLine - 1).toString();
            caretIndex = tops.length();
            if (caretIndex < 0)
                caretIndex = 0;
            String newcurl = tops + vCurText.elementAt(iCurLine).toString();
            currentText.append(newcurl);
            vCurText.removeElementAt(iCurLine);
            vCurText.setElementAt(new EditorItem(newcurl), iCurLine - 1);
            iCurLine--;
            if (iCurLine < offsetLine)
                offsetLine--;
            updateCaretPosition();
            resetBlink();
        }
    }

    void deleteChar()
    {
        if (((EditorItem) vCurText.elementAt(iCurLine)).isUneditable())
            return;
        if (selStartLine != selEndLine || (selStart != selEnd))
        {
            int t;
            if (selStartLine > selEndLine)
            {
                t = selStartLine;
                selStartLine = selEndLine;
                selEndLine = t;
            } else if (selStart > selEnd)
            {
                t = selStart;
                selStart = selEnd;
                selEnd = t;
            }
            caretIndex = selStart;
            iCurLine = selStartLine;

            currentText.delete(0, currentText.length());
            String sc = (vCurText.elementAt(selStartLine)).toString().substring(0, selStart);
            currentText.append(sc);
            sc = (vCurText.elementAt(selEndLine)).toString().substring(selEnd);
            currentText.append(sc);
            vCurText.setElementAt(currentText.toString(), selStartLine);
            for (int i = selStartLine + 1; i <= selEndLine; i++)
                vCurText.removeElementAt(selStartLine + 1);
            selStart = selEnd = caretIndex;
            selStartLine = selEndLine = iCurLine;
            selStartX = selEndX = 0;
            updateCaretPosition();
            resetBlink();
        } else if (currentText.length() > 0 && caretIndex < currentText.length() - 1)
        {
            currentText.deleteCharAt(caretIndex);
            caretIndex--;
            vCurText.setElementAt(new EditorItem(currentText.toString()), iCurLine);
            updateCaretPosition();
            resetBlink();
        }
    }

    void updateCaretPosition()
    {
        caretLeft = inputFont.substringWidth(vCurText.elementAt(iCurLine).toString(), 0, caretIndex);

        if (caretLeft + inputTranslationX < iNumbersWidth + 1)
            inputTranslationX = iNumbersWidth + 1 - caretLeft;
        else if (caretLeft + inputTranslationX > inputWidth)
            inputTranslationX = inputWidth - caretLeft;
    }

    void lineFeed()
    {
        if (((EditorItem) vCurText.elementAt(iCurLine)).isUneditable())
            return;
        System.out.println("lineFeed");

        if (selStartLine != selEndLine || selStart != selEnd)
        {
            clearChar();
            bSelecting = false;
        }

        String prevs = (vCurText.elementAt(iCurLine)).toString().substring(0, caretIndex);
        String curs = "";
        int nInd = 0;
        if (true)
        {
            int i = 0;
            while (i < prevs.length() && prevs.charAt(i) == ' ')
            {
                curs += ' ';
                i++;
            }
            nInd = i;
        }
        curs += (vCurText.elementAt(iCurLine).toString()).substring(caretIndex);
        iCurLine++;
        if (iCurLine >= offsetLine + nLinesMax)
            offsetLine++;
        vCurText.setElementAt(new EditorItem(prevs), iCurLine - 1);
        vCurText.insertElementAt(new EditorItem(curs), iCurLine);
        currentText.delete(0, currentText.length());
        currentText.append(curs);
        caretIndex = nInd;
        updateCaretPosition();
        resetBlink();
        goToNextChar = true;
    }

    void moveCaret(int count)
    {
        goToNextChar = true;
        currentKeyStep = 0;

        int dir;
        if (count < 0)
        {
            dir = -1;
            while (count < 0)
            {
                caretIndex--;
                count++;
                if (caretIndex == -1)
                    if (iCurLine > 0)
                    {
                        moveUp();
                        caretIndex = currentText.length();
                    } else
                    {
                        caretIndex = 0;
                        break;
                    }
            }
        } else
        {
            dir = 1;
            while (count > 0)
            {
                caretIndex++;
                count--;
                if (caretIndex == currentText.length() + 1)
                    if (iCurLine < vCurText.size() - 1)
                    {
                        moveDown();
                        caretIndex = 0;
                    } else
                    {
                        caretIndex = currentText.length();
                        break;
                    }
            }
        }
        if (bSelecting)
        {

            selEnd = caretIndex;
            selEndLine = iCurLine;
            selEndX = inputFont.stringWidth(currentText.toString().substring(0, caretIndex));

        } else
        {
            selStart = selEnd = caretIndex;
            selStartX = selEndX = 0;
            selStartLine = selEndLine = iCurLine;
        }
        updateCaretPosition();
        resetBlink();
        goToNextChar = true;
    }

    public void setCaretPosition(int pos)
    {
        goToNextChar = true;
        currentKeyStep = 0;

        if (pos < 0)
            pos = 0;
        if (pos > currentText.length())
            pos = currentText.length();
        caretIndex = pos;
        updateCaretPosition();
        resetBlink();

    }

    public void findString(String str)
    {
        for (int i = 0; i < vCurText.size(); i++)
        {
            String s = vCurText.elementAt(i).toString();
            if (s.indexOf(str) > 0)
            {
                goToLine(i);
                setCaretPosition(s.indexOf(str));
                break;
            }
        }
    }

    void moveUp()
    {
        goToNextChar = true;
        currentKeyStep = 0;

        if (iCurLine > 0)
        {
            iCurLine--;
            if (iCurLine < offsetLine)
                offsetLine--;
            String curs = vCurText.elementAt(iCurLine).toString();
            if (caretIndex > curs.length())
                caretIndex = curs.length();
            if (caretIndex < 0)
                caretIndex = 0;
            if (bSelecting)
            {
                selEnd = caretIndex;
                selEndLine = iCurLine;
                selEndX = inputFont.stringWidth(curs.substring(caretIndex));
            } else
            {
                selStart = selEnd = caretIndex;
                selStartX = selEndX = 0;
                selStartLine = selEndLine = iCurLine;
            }
            currentText.delete(0, currentText.length());
            currentText.append(curs);
            updateCaretPosition();
            resetBlink();
            goToNextChar = true;
        }
    }

    void moveDown()
    {
        goToNextChar = true;
        currentKeyStep = 0;

        if (iCurLine < vCurText.size() - 1)
        {
            iCurLine++;
            if (iCurLine >= offsetLine + nLinesMax)
                offsetLine++;
            String curs = vCurText.elementAt(iCurLine).toString();
            if (caretIndex > curs.length())
                caretIndex = curs.length();
            if (caretIndex < 0)
                caretIndex = 0;
            if (bSelecting)
            {
                selEnd = caretIndex;
                selEndLine = iCurLine;
                selEndX = inputFont.stringWidth(curs.substring(caretIndex));
            } else
            {
                selStart = selEnd = caretIndex;
                selStartX = selEndX = 0;
                selStartLine = selEndLine = iCurLine;
            }
            currentText.delete(0, currentText.length());
            currentText.append(curs);
            updateCaretPosition();
            resetBlink();
            goToNextChar = true;
        }
    }

    public void goToLine(int line)
    {
        goToNextChar = true;
        currentKeyStep = 0;

        if (line < 0)
            line = 0;
        if (line >= vCurText.size())
            line = vCurText.size() - 1;
        iCurLine = line;
        if ((iCurLine >= offsetLine + nLinesMax) || (iCurLine < offsetLine))
            offsetLine = line;
        String curs = vCurText.elementAt(iCurLine).toString();
        if (caretIndex > curs.length())
            caretIndex = curs.length();
        if (caretIndex < 0)
            caretIndex = 0;
        if (bSelecting)
        {
            selEnd = caretIndex;
            selEndLine = iCurLine;
            selEndX = inputFont.stringWidth(curs.substring(caretIndex));
        } else
        {
            selStart = selEnd = caretIndex;
            selStartX = selEndX = 0;
            selStartLine = selEndLine = iCurLine;
        }
        currentText.delete(0, currentText.length());
        currentText.append(curs);
        updateCaretPosition();
        resetBlink();
        goToNextChar = true;

    }

    public void nextMode()
    {
        if (mode == MODE_LOWER_CASE)
            setMode(MODE_ULOWER_CASE);
        else if (mode == MODE_ULOWER_CASE)
            setMode(MODE_UPPER_CASE);
        else if (mode == MODE_UPPER_CASE)
            setMode(MODE_NUMBERS);
        else if (mode == MODE_NUMBERS)
            setMode(MODE_LOWER_CASE);
    }

    public void keyPressed(int key)
    {
        if (currentMode == STATE_SPLASH)
        {
            if (key == KEY_NUM0)
                settings.show(disp, this);
            return;
        }

        int gameAction = getGameAction(key);
//#ifdef DEBUG
        System.out.println("KEY: " + key + ", " + gameAction);
        sMsg = "key: " + key;
//#endif
        if (settings.bQwerty)
            if (currentMode == STATE_MENU)
            {
                if (key == settings.rightSoft)
                    showMenu(false);
                else if (gameAction == Canvas.LEFT)
                    showMenu(false);
                else if (gameAction == Canvas.RIGHT)
                {
                    if (commandListener != null)
                        commandListener.commandAction((Command) vCommands.elementAt(menuFocus), this);
                    showMenu(false);
                } else if (gameAction == Canvas.UP)
                {
                    menuFocus--;
                    if (menuFocus < 0)
                        menuFocus = vCommands.size() - 1;
                } else if (gameAction == Canvas.DOWN)
                {
                    menuFocus++;
                    if (menuFocus >= vCommands.size())
                        menuFocus = 0;
                } else if (gameAction == Canvas.FIRE)
                {
                    if (commandListener != null)
                        commandListener.commandAction((Command) vCommands.elementAt(menuFocus), this);
                    showMenu(false);
                }
            } else
            {
                char c = EditorKeys.getQwertyChar(key);
                int control = EditorKeys.getQwertyControlChar(key);
                if (c != (char) -1)
                    writeChar(c);
                else if (control != -1)
                {
                    if (control == EditorKeys.QKEY_BACKSPACE)
                    {
                        clearChar();
                        updateCaretPosition();
                        goToNextChar = true;
                    } else if (control == EditorKeys.QKEY_DELETE)
                    {
                        deleteChar();
                        updateCaretPosition();
                        goToNextChar = true;
                    } else if (control == EditorKeys.QKEY_ENTER)
                        lineFeed();
                    else if (control == EditorKeys.QKEY_TAB)
                        writeChar('\t');
                } else if (key == settings.leftSoft)
                    clearChar();
                else if (key == settings.rightSoft)
                    showMenu(true);
                else if (gameAction == Canvas.LEFT)
                    moveCaret(-1);
                else if (gameAction == Canvas.RIGHT)
                    moveCaret(1);
                else if (gameAction == Canvas.UP)
                    moveUp();
                else if (gameAction == Canvas.DOWN)
                    moveDown();
            }
        else if (currentMode == STATE_SELECT_CHAR)
        {
            if (key == Canvas.KEY_STAR)
            {
//                    bSelectingChar = false;
                currentMode = STATE_NORMAL;
                selectingCharFocus = 0;
                selectingCharKeyTime = -1;
            } else if (gameAction == Canvas.LEFT)
            {
                selectingCharFocus--;
                if (selectingCharFocus < 0)
                    selectingCharFocus = 0;
            } else if (gameAction == Canvas.RIGHT)
            {
                selectingCharFocus++;
                if (selectingCharFocus >= SPECIAL_CHARS.length)
                    selectingCharFocus = SPECIAL_CHARS.length - 1;
            } else if (gameAction == Canvas.UP)
            {
                selectingCharFocus -= selectingTableWidth;
                if (selectingCharFocus < 0)
                    selectingCharFocus = 0;
            } else if (gameAction == Canvas.DOWN)
            {
                selectingCharFocus += selectingTableWidth;
                if (selectingCharFocus >= SPECIAL_CHARS.length)
                    selectingCharFocus = SPECIAL_CHARS.length - 1;
            } else if (gameAction == Canvas.FIRE)
            {
                writeChar(SPECIAL_CHARS[selectingCharFocus]);
                currentMode = STATE_NORMAL;
                selectingCharFocus = 0;
                selectingCharKeyTime = -1;
            }
        } else if (currentMode == STATE_MENU)
        {
            if (key == settings.rightSoft)
                showMenu(false);
            else if (gameAction == Canvas.LEFT)
                showMenu(false);
            else if (gameAction == Canvas.RIGHT)
            {
                if (commandListener != null)
                    commandListener.commandAction((Command) vCommands.elementAt(menuFocus), this);
                showMenu(false);
            } else if (gameAction == Canvas.UP)
            {
                menuFocus--;
                if (menuFocus < 0)
                    menuFocus = vCommands.size() - 1;
            } else if (gameAction == Canvas.DOWN)
            {
                menuFocus++;
                if (menuFocus >= vCommands.size())
                    menuFocus = 0;
            } else if (gameAction == Canvas.FIRE)
            {
                if (commandListener != null)
                    commandListener.commandAction((Command) vCommands.elementAt(menuFocus), this);
                showMenu(false);
            } else if (key >= KEY_NUM0 && key <= KEY_NUM9)
            {
                int skey = key == KEY_NUM0 ? 10 : key - 48;
                if (commandListener != null)
                    commandListener.commandAction((Command) vCommands.elementAt(skey - 1), this);
                showMenu(false);
            }
        } else if (currentMode == STATE_NORMAL)
            if (key == settings.rightSoft)
                showMenu(true);
            else if (key == settings.leftSoft)
                clearChar();
            else if (isClearKey(key))
            {
                clearChar();

                updateCaretPosition();

                goToNextChar = true;
            } else if (key >= KEY_NUM0 && key <= KEY_NUM9)
                writeKeyPressed(key);
            else if (key == KEY_STAR)
                selectingCharKeyTime = System.currentTimeMillis();
            else if (gameAction == Canvas.LEFT)
                moveCaret(-1);
            else if (gameAction == Canvas.RIGHT)
                moveCaret(1);
            else if (gameAction == Canvas.UP)
                moveUp();
            else if (gameAction == Canvas.DOWN)
                moveDown();
            else if (gameAction == Canvas.FIRE)
                lineFeed();
            else if (key == KEY_POUND)
                bCanChangeSyntax = true;
        repaint();
    }

    public void keyRepeated(int key)
    {
        if (key >= KEY_NUM0 && key <= KEY_NUM9)
        {
            clearChar();
            updateCaretPosition();
            writeChar((char) key);
            bShowChars = false;
            vChars = null;
            return;
        }
        if (key != KEY_STAR && key != KEY_POUND)
            keyPressed(key);
        if (key == KEY_POUND)
            if (bCanChangeSyntax)
            {
                settings.bSyntax = !settings.bSyntax;
                settings.bCheck = settings.bSyntax;
                bCanChangeSyntax = false;
            }
    }

    public void keyReleased(int key)
    {
        //#ifdef DEBUG
        sMsg = "";
        //#endif
        if (key == KEY_POUND)
        {
            bCanChangeSyntax = false;
            settings.saveSettings();
        } else if (key == KEY_STAR)
            if (System.currentTimeMillis() < selectingCharKeyTime +
                    selectingCharKeyTimeout)
            {
                currentMode = STATE_NORMAL;
                nextMode();
                selectingCharKeyTime = -1;
            }
        repaint();
    }

    public void pointerPressed(int x, int y)
    {
        if (currentMode == STATE_SPLASH)
        {
            settings.show(disp, this);
            return;
        }

        if (y <= inputTranslationY)
            return;

        if (y > getHeight() - menuBarH)
            if (x < 1 + linesFont.stringWidth(sClearButton))
            {
                clearChar();
                return;
            } else if (x > getWidth() - 1 - linesFont.stringWidth(sMenuButton))
            {
                if (currentMode == STATE_MENU)
                    showMenu(false);
                else if (currentMode == STATE_NORMAL)
                    showMenu(true);
                return;
            }

        if (currentMode == STATE_MENU)
            if (x < menuX || x > menuX + menuW || y < menuY || y > menuY + menuH)
            {
                showMenu(false);
                return;
            } else
            {
                int localY = y - (menuY + 2);
                int choice = localY / (linesFont.getHeight() + 1);
                menuFocus = choice;
                return;
            }

        if (x > getWidth() - 5)
        {
            bScrollMode = true;
            scrollStartY = y;
        }

        int iLine = (y / (inputFont.getHeight() + 1)) - 1 + offsetLine;
        if (iLine >= vCurText.size())
            iCurLine = vCurText.size() - 1;
        else
            iCurLine = iLine;

        String curs = vCurText.elementAt(iCurLine).toString();

        caretIndex = 0;
        int cx = inputTranslationX + 1;
        while (caretIndex < curs.length())
        {
            if (cx >= x)
                break;
            cx += inputFont.charWidth(curs.charAt(caretIndex));
            selStartX = cx - inputTranslationX;
            caretIndex++;
        }

        if (caretIndex > curs.length())
            caretIndex = curs.length();
        if (caretIndex < 0)
            caretIndex = 0;
        currentText.delete(0, currentText.length());
        currentText.append(curs);
        updateCaretPosition();
        resetBlink();
        goToNextChar = true;

        selStart = caretIndex;
        selStartLine = iCurLine;
        selEnd = selStart;
        selEndLine = selStartLine;
        selEndX = selStartX;
        bSelecting = true;
    }

    public void pointerDragged(int x, int y)
    {
        if (currentMode == STATE_MENU)
            if (x < menuX || x > menuX + menuW || y < menuY || y > menuY + menuH)
                return;
            else
            {
                int localY = y - (menuY + 2);
                int choice = localY / (linesFont.getHeight() + 1);
                menuFocus = choice;
                return;
            }

        if (bScrollMode)
        {
            int hMax = (getHeight() - inputTranslationY - 5);
            int nH = (hMax - hMax / nLinesMax) / (vCurText.size());
            int dY = scrollStartY - y;
            iCurLine = ((y - inputTranslationY - 5) * vCurText.size()) / (hMax - nH);
            if (iCurLine < 0)
                iCurLine = 0;
            if (iCurLine >= vCurText.size())
                iCurLine = vCurText.size() - 1;
            if (iCurLine < offsetLine)
                offsetLine = iCurLine;
            if (iCurLine > nLinesMax)
                offsetLine = iCurLine - nLinesMax + 1;
            return;
        }

        if (y <= inputTranslationY)
        {
            selEndLine = 0;
            return;
        }

        int iLine = (y / (inputFont.getHeight() + 1)) - 1 + offsetLine;
        if (iLine >= vCurText.size())
            iCurLine = vCurText.size() - 1;
        else
            iCurLine = iLine;

        String curs = vCurText.elementAt(iCurLine).toString();

        caretIndex = 0;
        int cx = inputTranslationX + 1;
        while (caretIndex < curs.length())
        {
            if (cx >= x)
                break;
            cx += inputFont.charWidth(curs.charAt(caretIndex));
            caretIndex++;
        }
        selEndX = cx - inputTranslationX;

        if (caretIndex > curs.length())
            caretIndex = curs.length();
        if (caretIndex < 0)
            caretIndex = 0;
        currentText.delete(0, currentText.length());
        currentText.append(curs);
        updateCaretPosition();
        resetBlink();
        goToNextChar = true;

        selEnd = caretIndex;
        selEndLine = iCurLine;
    }

    public void pointerReleased(int x, int y)
    {
        if (bSelecting)
            bSelecting = false;

        if (currentMode == STATE_MENU)
            if (x < menuX || x > menuX + menuW || y < menuY || y > menuY + menuH)
                return;
            else
            {
                int localY = y - (menuY + 2);
                int choice = localY / (linesFont.getHeight() + 1);
                if (commandListener != null)
                    commandListener.commandAction((Command) vCommands.elementAt(choice), this);
                showMenu(false);
                return;
            }
    }

    public void insertString(String s)
    {
//        System.out.println ("insert " + s);
        if (selStartLine != selEndLine || selStart != selEnd)
        {
            clearChar();
            bSelecting = false;
        }

        goToNextChar = true;
        currentKeyStep = 0;

        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == '\n')
            {
                currentText.delete(0, currentText.length());
                vCurText.insertElementAt(new EditorItem(""), iCurLine + 1);
                iCurLine++;
                currentText.append(vCurText.elementAt(iCurLine).toString());
                caretIndex = 0;
            } else
                writeChar(s.charAt(i));

        currentText.delete(0, currentText.length());
        currentText.append(vCurText.elementAt(iCurLine).toString());
        caretIndex = currentText.length();

        lastKeyTimestamp = System.currentTimeMillis();
        resetBlink();
        goToNextChar = true;
    }

    public void writeChar(char c)
    {
        if (((EditorItem) vCurText.elementAt(iCurLine)).isUneditable())
            return;
        if (c == '\n')
            lineFeed();
        else if (selStartLine != selEndLine || selStart != selEnd)
        {
            clearChar();
            bSelecting = false;
        }

        goToNextChar = true;
        currentKeyStep = 0;

        currentText.insert(caretIndex, c);
        caretIndex++;

        vCurText.setElementAt(new EditorItem(currentText.toString()), iCurLine);
        updateCaretPosition();
        lastKeyTimestamp = System.currentTimeMillis();
        resetBlink();
        goToNextChar = true;
    }

    public void writeKeyPressed(int key)
    {
        if (((EditorItem) vCurText.elementAt(iCurLine)).isUneditable())
            return;
        if (!goToNextChar && key != lastPressedKey && mode == MODE_ULOWER_CASE)
            setMode(MODE_LOWER_CASE);
        if (selStartLine != selEndLine || selStart != selEnd)
        {
            clearChar();
            bSelecting = false;
        }
        if (goToNextChar || key != lastPressedKey)
        {
            goToNextChar = true;

            lastPressedKey = key;

            currentKeyStep = 0;
        } else
            currentKeyStep++;

        char[] chars = getChars(key, mode == MODE_NUMBERS);
        vChars = chars;
        bShowChars = true;
        if (chars != null)
        {

            if (currentKeyStep >= chars.length)
                currentKeyStep -= chars.length;
            if (mode == MODE_NUMBERS)
            {
                currentText.insert(caretIndex, chars[0]);
                caretIndex++;
            } else if (goToNextChar)
            {
                String s = "" + chars[currentKeyStep];
                if (mode == MODE_UPPER_CASE || mode == MODE_ULOWER_CASE)
                    s = s.toUpperCase();
                currentText.insert(caretIndex, s);

                caretIndex++;
            } else
            {
                String s = "" + chars[currentKeyStep];
                if (mode == MODE_UPPER_CASE || mode == MODE_ULOWER_CASE)
                    s = s.toUpperCase();
                currentText.setCharAt(caretIndex - 1, s.charAt(0));
            }

            vCurText.setElementAt(new EditorItem(currentText.toString()), iCurLine);

            updateCaretPosition();
            resetBlink();
            lastKeyTimestamp = System.currentTimeMillis();

            goToNextChar = false;
        }
    }

    public void checkTimestamps()
    {
        long currentTime = System.currentTimeMillis();

        if (currentMode == STATE_SPLASH)
            return;

        if (selectingCharKeyTime != -1 && currentTime >= selectingCharKeyTime + selectingCharKeyTimeout)
        {
//            System.out.println("select char");
            currentMode = STATE_SELECT_CHAR;
            selectingCharKeyTime = -1;
            repaint();
        }

        if (lastCaretBlink + caretBlinkDelay < currentTime)
        {
            caretBlinkOn = !caretBlinkOn;

            lastCaretBlink = currentTime;
            repaint();
        }

        if (!goToNextChar && lastKeyTimestamp + maxKeyDelay < currentTime)
        {
            goToNextChar = true;
            if (mode == MODE_ULOWER_CASE)
                setMode(MODE_LOWER_CASE);
            bShowChars = false;
            vChars = null;
            repaint();
        }
    }

    public void resetBlink()
    {
        lastCaretBlink = System.currentTimeMillis();
        caretBlinkOn = true;
        repaint();
    }

    public void run()
    {
        while (true)
        {
            checkTimestamps();

            try
            {

                synchronized (this)
                {
                    wait(50L);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void paint(Graphics g)
    {
        g.setColor(0xffffff);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (currentMode == STATE_SPLASH)
        {
            g.setColor(0);
            g.drawString("Загрузка...", 1, 1, Graphics.LEFT | Graphics.TOP);
            g.drawString("Нажмите 0 для настроек", 1, getHeight() - 1, Graphics.LEFT | Graphics.BOTTOM);
            return;
        }

        g.setFont(titleFont);
        g.setColor(200, 200, 255);
        g.fillRect(0, 0, getWidth(), titleHeight);
        g.setColor(50, 50, 100);
        if (settings.bQwerty)
            sMode = "QWERTY";
        g.drawString(sMode, getWidth() - 1, 1, Graphics.RIGHT | Graphics.TOP);
        g.drawString(sTitle, (getWidth() - titleFont.stringWidth(sMode)) / 2, titleHeight / 2 - titleFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
        g.drawLine(0, titleHeight, getWidth(), titleHeight);
        g.drawLine(getWidth() - titleFont.stringWidth(sMode) - 3, 0, getWidth() - titleFont.stringWidth(sMode) - 3, titleHeight);

        if (bShowChars && vChars != null && vChars.length > 1)
        {
            g.setColor(200, 200, 255);
            g.fillRect(0, 0, getWidth() - titleFont.stringWidth(sMode) - 3, titleHeight);
            g.setColor(0xccaaaa);
            g.fillRoundRect(titleFont.charsWidth(vChars, 0, currentKeyStep), 1, titleFont.charWidth(vChars[currentKeyStep]), titleFont.getHeight(), 5, 5);
            g.setColor(50, 50, 100);
            g.drawRoundRect(titleFont.charsWidth(vChars, 0, currentKeyStep), 1, titleFont.charWidth(vChars[currentKeyStep]), titleFont.getHeight(), 5, 5);
            g.setColor(50, 50, 100);
            g.drawChars(vChars, 0, vChars.length, 1, 1, Graphics.TOP | Graphics.LEFT);
        }


        g.setColor(0x000000);

        g.translate(inputTranslationX, 0);

        g.setColor(0xffaaaa);
        int line1 = selStartLine - offsetLine, line2 = -offsetLine + selEndLine;
        int sel1 = selStartX, sel2 = selEndX;
        if (line1 > line2)
        {
            int t = line1;
            line1 = line2;
            line2 = t;
            t = sel1;
            sel1 = sel2;
            sel2 = t;
        }
        if (line1 == line2)
        {
            if (sel1 > sel2)
            {
                int t = sel1;
                sel1 = sel2;
                sel2 = t;
            }
            int cy = 1 + inputTranslationY + ((iCurLine - offsetLine) * (inputFont.getHeight() + 1));
            g.fillRect(sel1, cy, sel2 - sel1, inputFont.getHeight());
        } else
        {
            int cy = 1 + inputTranslationY + ((line1 - offsetLine) * (inputFont.getHeight() + 1));
            g.fillRect(sel1, cy, inputFont.stringWidth(vCurText.elementAt(line1).toString()) - sel1, inputFont.getHeight());
            for (int i = line1 + 1; i < line2; i++)
            {
                cy = 1 + inputTranslationY + ((i - offsetLine) * (inputFont.getHeight() + 1));
                g.fillRect(0, cy,
                        inputFont.stringWidth(vCurText.elementAt(i).toString()),
                        inputFont.getHeight());
            }
            cy = 1 + inputTranslationY + ((line2 - offsetLine) * (inputFont.getHeight() + 1));
            g.fillRect(0, cy, sel2, inputFont.getHeight());
        }

        int y = 1 + inputTranslationY;
        for (int iLine = offsetLine; iLine < offsetLine + nLinesMax && iLine < vCurText.size(); iLine++)
        {
            g.setColor(0x000000);
            g.setFont(inputFont);

            if (settings.bSyntax)
                g.drawImage(hlHighlighter.highlight(vCurText.elementAt(iLine).toString(), inputFont, iLine == errorLine, errorStart, errorEnd), 0, y, Graphics.LEFT | Graphics.TOP);
            else
                g.drawString(vCurText.elementAt(iLine).toString(), 0, y, Graphics.LEFT | Graphics.TOP);

//            if (bShowLines)
//            {
//                g.setColor (0x0000a0);
//                g.setFont(linesFont);
//                g.drawString(Integer.toString(iLine), -inputTranslationX, y, Graphics.LEFT|Graphics.TOP);
//            }
            y += inputFont.getHeight() + 1;
        }
        if (!(currentMode == STATE_SELECT_CHAR) && caretBlinkOn && goToNextChar)
        {
            y = inputTranslationY + ((iCurLine - offsetLine) * (inputFont.getHeight() + 1));
            g.drawLine(caretLeft, y, caretLeft, y + inputFont.getHeight());
        }
        g.translate(-inputTranslationX, 0);

        if (bShowLines)
        {
            iNumbersWidth = 1 + linesFont.stringWidth(String.valueOf(vCurText.size())) + 1;
            g.setColor(0xaaaaff);

            g.fillRect(0, inputTranslationY, iNumbersWidth, inputHeight);

            y = 1 + inputTranslationY;
            for (int iLine = offsetLine; iLine < offsetLine + nLinesMax && iLine < vCurText.size(); iLine++)
            {
                g.setColor(0x0000a0);
                g.setFont(linesFont);
                if (iLine != errorLine)
                    g.drawString(Integer.toString(iLine + 1), 1, y, Graphics.LEFT | Graphics.TOP);
                else
                    g.drawImage(imError, 1, y, Graphics.TOP | Graphics.LEFT);
                y += inputFont.getHeight() + 1;
            }
        }

        if (currentMode == STATE_SELECT_CHAR)
        {
            //g.setColor (255, 210, 250);
            //g.fillRect (10, 10, getWidth()-10, getHeight()-10);
            g.drawImage(imgSelectingCharset, 10, 10, Graphics.LEFT | Graphics.TOP);
            int cy = (selectingCharFocus / selectingTableWidth) * selectingCharH;
            int cx = (selectingCharFocus - (selectingCharFocus / selectingTableWidth) * selectingTableWidth) * selectingCharW;
            int dx = selectingCharW / 2 - inputFont.charWidth(SPECIAL_CHARS[selectingCharFocus]);
            g.setColor(220, 200, 200);
            g.fillRoundRect(9 + cx, 10 + cy, selectingCharW, selectingCharH + 1, selectingCharW / 2, selectingCharH / 2);
            g.setColor(50, 50, 100);
            g.drawRoundRect(9 + cx, 10 + cy, selectingCharW, selectingCharH + 1, selectingCharW / 2, selectingCharH / 2);
            g.setColor(0);
            g.drawChar(SPECIAL_CHARS[selectingCharFocus], 10 + cx + 1 + dx, 10 + cy + 1, Graphics.LEFT | Graphics.TOP);
        }



        int hMax = (getHeight() - inputTranslationY - 5);
        int nH = 15;//(hMax - hMax / nLinesMax) / (vCurText.size());
        int nAllLines = vCurText.size();
        int nProgress = ((iCurLine) * (hMax - nH)) / (nAllLines);
        g.setColor(0x8888ff);
        g.fillRect(getWidth() - 5, inputTranslationY + nProgress, 4, nH);

        g.setFont(linesFont);
        g.setColor(20, 20, 80);
        g.fillRect(0, getHeight() - menuBarH, getWidth(), menuBarH);
        g.setColor(220, 220, 255);
        g.drawString(sMenuButton, getWidth() - 1, getHeight() - 1, Graphics.RIGHT | Graphics.BOTTOM);
        if (!settings.bQwerty)
            g.drawString(sReturnButton, menuBarCenterStrPosition, getHeight() - 1, Graphics.LEFT | Graphics.BOTTOM);
        g.drawString(sClearButton, 1, getHeight() - 1, Graphics.LEFT | Graphics.BOTTOM);

        if (currentMode == STATE_MENU)
        {
            g.setColor(200, 200, 255);
            g.fillRect(menuX - linesFont.stringWidth(String.valueOf(vCommands.size())), menuY,
                    menuW + linesFont.stringWidth(String.valueOf(vCommands.size())), menuH);
            g.setColor(50, 50, 100);
            g.drawRect(menuX - linesFont.stringWidth(String.valueOf(vCommands.size())), menuY,
                    menuW + linesFont.stringWidth(String.valueOf(vCommands.size())), menuH);
            g.setColor(80, 80, 120);
            g.fillRect(menuX - linesFont.stringWidth(String.valueOf(vCommands.size())) + 1,
                    menuY + 2 + (linesFont.getHeight() + 1) * menuFocus,
                    menuW + linesFont.stringWidth(String.valueOf(vCommands.size())) - 2, linesFont.getHeight());
            y = menuY + 2;
            g.setColor(0);
            int i = 0;
            for (Enumeration en = vCommands.elements(); en.hasMoreElements();)
            {
                Command cmd = (Command) en.nextElement();
                String s = cmd.getLabel();
                g.drawString(String.valueOf(++i), menuX, y, Graphics.RIGHT | Graphics.TOP);
                g.drawString(s, menuX + 2, y, Graphics.LEFT | Graphics.TOP);
                y += linesFont.getHeight() + 1;
            }
        }

//        g.setColor(0x0000ff);
//        g.drawString(sMsg+" "+goToNextChar, 1, 1, Graphics.LEFT|Graphics.TOP);
    }

    public void setCurrentString(String s)
    {
        vCurText.setElementAt(new EditorItem(s), iCurLine);
    }

    public String getCurrentString()
    {
        if (((EditorItem) vCurText.elementAt(iCurLine)).isUneditable())
            return null;
        return vCurText.elementAt(iCurLine).toString();
    }

    public void setUnEditable(int i, boolean e)
    {
        ((EditorItem) vCurText.elementAt(i)).setUneditable(e);
    }
}
