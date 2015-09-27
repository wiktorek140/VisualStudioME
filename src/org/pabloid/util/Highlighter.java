package org.pabloid.util;

import javax.microedition.lcdui.*;
import java.io.*;
import org.pabloid.io.*;

public class Highlighter
{

    private String[][] vKeywords;
    private int[] vColors;
    private char[] quoteChar;
    private String multiCommentStart;
    private String multiCommentEnd;
    private String singleComment;
    private boolean bNull = true;

    public Highlighter(String resource)
    {
        try
        {
            bNull = false;
            ResourceReader is = new ResourceReader(resource);
            if(!is.exists())
            {
                bNull = true;
                return;
            }
            BufferedReader dis = new BufferedReader(is);
            int nCategories = Integer.parseInt(dis.readLine());
            vColors = new int[nCategories];
            vKeywords = new String[nCategories][];
            for (int i = 0; i < nCategories; i++)
            {
                vColors[i] = Integer.parseInt(dis.readLine(),16);
                int nKeywords = Integer.parseInt(dis.readLine());
                vKeywords[i] = new String[nKeywords];
                for (int j = 0; j < nKeywords; j++)
                    vKeywords[i][j] = dis.readLine();

            }
            int nQuotes = Integer.parseInt(dis.readLine());
            quoteChar = new char[nQuotes];
            for (int i = 0; i < nQuotes; i++)
                quoteChar[i] = dis.readLine().charAt(0);
            multiCommentStart = dis.readLine();
            multiCommentEnd = dis.readLine();
            singleComment = dis.readLine();
        }
        catch (Throwable ioe)
        {
            ioe.printStackTrace();
        }
    }

    public Image highlight(String text, Font font, boolean bError, int errStart, int errEnd)
    {
        int w = font.stringWidth(text);
        if(text.equals(""))
            w = 1;
        Image img = Image.createImage(w, font.getHeight());
        Graphics g = img.getGraphics();
        g.setFont(font);
        if(bError)
        {
            g.setColor(0xff0000);
            g.fillRect(font.stringWidth(text.substring(0, errStart)), 0, font.stringWidth(text.substring(errStart, errEnd)), font.getHeight());
        }
        g.setColor(0xffffff);
        g.fillRect(0, 0, w, font.getHeight());
        g.setColor(0x000000);
        g.drawString(text, 0, 0, 20);
        if(bNull)
            return img;
        int i0 = 0;
        for (int j = 0; j < vKeywords.length; j++)
        {
            g.setColor(vColors[j]);
            i0 = 0;
            for (int i = 0; i < vKeywords[j].length; i++)
                for (; i0 < text.length(); i0++)
                {
                    i0 = text.indexOf(vKeywords[j][i], i0);
                    if(i0 < 0)
                        break;
                    char c = (i0 + vKeywords[j][i].length() < text.length()) ? text.charAt(i0 + vKeywords[j][i].length()) : '.';
                    char c1 = (i0 > 0) ? text.charAt(i0 - 1) : '.';
                    if(isName(c) || isName(c1))
                        continue;
                    int color = g.getColor();
                    g.setColor(0xffffff);
                    g.fillRect(font.stringWidth(text.substring(0, i0)), 0, font.stringWidth(vKeywords[j][i]), w);
                    g.setColor(color);
                    g.drawSubstring(text, i0, vKeywords[j][i].length(), font.stringWidth(text.substring(0, i0)), 0, 20);
                }
        }
        g.setColor(0x008000);
        for (i0 = 0; i0 < text.length(); i0++)
        {
            i0 = text.indexOf(multiCommentStart, i0);
            if(i0 < 0)
                break;
            int i1 = text.indexOf(multiCommentEnd, i0 + 2);
            if(i1 < 0)
                i1 = text.length() - 2;
            int color = g.getColor();
            g.setColor(0xffffff);
            g.fillRect(font.stringWidth(text.substring(0, i0)), 0, font.stringWidth(text.substring(i0, i1 + 2)), w);
            g.setColor(color);
            g.drawSubstring(text, i0, i1 - i0 + 2, font.stringWidth(text.substring(0, i0)), 0, 20);
            i0 = i1;
        }
        i0 = text.indexOf(singleComment);
        if(i0 >= 0)
        {
            int color = g.getColor();
            g.setColor(0xffffff);
            g.fillRect(font.stringWidth(text.substring(0, i0)), 0, font.stringWidth(text.substring(i0)), w);
            g.setColor(color);
            g.drawSubstring(text, i0, text.length() - i0, font.stringWidth(text.substring(0, i0)), 0, 20);
        }
        g.setColor(0xff8000);
        for (int i = 0; i < quoteChar.length; i++)
            for (i0 = 0; i0 < text.length(); i0++)
            {
                i0 = text.indexOf(quoteChar[i], i0);
                if(i0 < 0)
                    break;
                int i1 = text.indexOf(quoteChar[i], i0 + 1);
                if(i1 < 0)
                    i1 = text.length() - 1;
                int color = g.getColor();
                g.setColor(0xffffff);
                g.fillRect(font.stringWidth(text.substring(0, i0)), 0, font.stringWidth(text.substring(i0, i1 + 1)), w);
                g.setColor(color);
                g.drawSubstring(text, i0, i1 - i0 + 1, font.stringWidth(text.substring(0, i0)), 0, 20);
                i0 = i1;
            }
        return img;
    }

    private boolean isName(char c)
    {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '$';
    }
}
