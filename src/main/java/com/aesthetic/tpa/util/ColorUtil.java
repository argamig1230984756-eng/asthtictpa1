/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 */
package com.aesthetic.tpa.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public final class ColorUtil {
    private static final Pattern HEX_PATTERN_1 = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_PATTERN_2 = Pattern.compile("<#([A-Fa-f0-9]{6})>");

    private ColorUtil() {
    }

    public static String colorize(String string) {
        String string2;
        if (string == null || string.isEmpty()) {
            return "";
        }
        Matcher matcher = HEX_PATTERN_1.matcher(string);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            string2 = matcher.group(1);
            matcher.appendReplacement(stringBuffer, ChatColor.of((String)("#" + string2)).toString());
        }
        matcher.appendTail(stringBuffer);
        string = stringBuffer.toString();
        matcher = HEX_PATTERN_2.matcher(string);
        stringBuffer = new StringBuffer();
        while (matcher.find()) {
            string2 = matcher.group(1);
            matcher.appendReplacement(stringBuffer, ChatColor.of((String)("#" + string2)).toString());
        }
        matcher.appendTail(stringBuffer);
        string = stringBuffer.toString();
        return ChatColor.translateAlternateColorCodes((char)'&', (String)string);
    }

    public static List<String> colorize(List<String> list) {
        if (list == null) {
            return new ArrayList<String>();
        }
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : list) {
            arrayList.add(ColorUtil.colorize(string));
        }
        return arrayList;
    }

    public static String stripColor(String string) {
        if (string == null) {
            return "";
        }
        return ChatColor.stripColor((String)ColorUtil.colorize(string));
    }
}

