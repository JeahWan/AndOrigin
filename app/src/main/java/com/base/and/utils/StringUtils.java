package com.base.and.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String Utils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class StringUtils {

    public static String touzi_ed_values22 = "";

    /**
     * is null or its length is 0 or it is made by space
     * <p>
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return true, else return false.
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * is null or its length is 0
     * <p>
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0, return true, else return false.
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    public static String hideMidString(String str) {
        if (!isEmpty(str)) {
            if (str.length() >= 3) {
                String pre_string = str.substring(0, 3);
                String end_string = str.substring(str.length() - 3, str.length());
                return pre_string + "***" + end_string;
            } else {
                return str + "***" + str;
            }
        } else {
            return "";
        }
    }

    /**
     * 银行卡 卡号 format
     *
     * @param str    卡号
     * @param format 隐藏用的**之类的样式,中间几个*也要写清楚
     * @return 返回format之后的卡号
     */
    public static String hidecardMidString(String str, String format) {
        if (!isEmpty(str)) {
            String pre_string = str.substring(0, 4);
            String end_string = str.substring(str.length() - 4, str.length());
            return pre_string + format + end_string;
        } else {
            return "";
        }
    }

    /**
     * 字符串中间位置变为*，指定前后显示字符长度
     *
     * @param str
     * @param startShowNum
     * @param endShowNum
     * @return
     */
    public static String hideMidString(String str, int startShowNum, int endShowNum) {
        if (isEmpty(str)) {
            return "";
        }
        try {
            char[] charArray = str.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                if (i < startShowNum) {
                    charArray[i] = charArray[i];
                } else if (i >= startShowNum && i < charArray.length - endShowNum) {
                    charArray[i] = '*';
                } else {
                    charArray[i] = charArray[i];
                }
            }
            return String.valueOf(charArray);
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * null string to empty string
     * <p>
     * <pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     *
     * @param str
     * @return
     */
    public static String nullStrToEmpty(String str) {
        return (str == null ? "" : str);
    }

    /**
     * capitalize first letter
     * <p>
     * <pre>
     * capitalizeFirstLetter(null)     =   null;
     * capitalizeFirstLetter("")       =   "";
     * capitalizeFirstLetter("2ab")    =   "2ab"
     * capitalizeFirstLetter("a")      =   "A"
     * capitalizeFirstLetter("ab")     =   "Ab"
     * capitalizeFirstLetter("Abc")    =   "Abc"
     * </pre>
     *
     * @param str
     * @return
     */
    public static String capitalizeFirstLetter(String str) {
        if (isEmpty(str)) {
            return str;
        }

        char c = str.charAt(0);
        return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str : new StringBuilder(
                str.length()).append(Character.toUpperCase(c)).append(str.substring(1)).toString();
    }

    /**
     * encoded in utf-8
     * <p>
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException if an error occurs
     */
    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }

    /**
     * encoded in utf-8, if exception, return defultReturn
     *
     * @param str
     * @param defultReturn
     * @return
     */
    public static String utf8Encode(String str, String defultReturn) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }

    /**
     * get innerHtml from href
     * <p>
     * <pre>
     * getHrefInnerHtml(null)                                  = ""
     * getHrefInnerHtml("")                                    = ""
     * getHrefInnerHtml("mp3")                                 = "mp3";
     * getHrefInnerHtml("&lt;a innerHtml&lt;/a&gt;")                    = "&lt;a innerHtml&lt;/a&gt;";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com"&gt;innerHtml&lt;/a&gt;")               = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com" title="baidu"&gt;innerHtml&lt;/a&gt;") = "innerHtml";
     * getHrefInnerHtml("   &lt;a&gt;innerHtml&lt;/a&gt;  ")                           = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                      = "innerHtml";
     * getHrefInnerHtml("jack&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                  = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml1&lt;/a&gt;&lt;a&gt;innerHtml2&lt;/a&gt;")        = "innerHtml2";
     * </pre>
     *
     * @param href
     * @return <ul>
     * <li>if href is null, return ""</li>
     * <li>if not match regx, return source</li>
     * <li>return the last string that match regx</li>
     * </ul>
     */
    public static String getHrefInnerHtml(String href) {
        if (isEmpty(href)) {
            return "";
        }

        String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
        Pattern hrefPattern = Pattern.compile(hrefReg, Pattern.CASE_INSENSITIVE);
        Matcher hrefMatcher = hrefPattern.matcher(href);
        if (hrefMatcher.matches()) {
            return hrefMatcher.group(1);
        }
        return href;
    }

    /**
     * process special char in html
     * <p>
     * <pre>
     * htmlEscapeCharsToString(null) = null;
     * htmlEscapeCharsToString("") = "";
     * htmlEscapeCharsToString("mp3") = "mp3";
     * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
     * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
     * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
     * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
     * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
     * </pre>
     *
     * @param source
     * @return
     */
    public static String htmlEscapeCharsToString(String source) {
        return StringUtils.isEmpty(source) ? source : source.replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
    }

    /**
     * transform half width char to full width char
     * <p>
     * <pre>
     * fullWidthToHalfWidth(null) = null;
     * fullWidthToHalfWidth("") = "";
     * fullWidthToHalfWidth(new String(new char[] {12288})) = " ";
     * fullWidthToHalfWidth("！＂＃＄％＆) = "!\"#$%&";
     * </pre>
     *
     * @param s
     * @return
     */
    public static String fullWidthToHalfWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == 12288) {
                source[i] = ' ';
                // } else if (source[i] == 12290) {
                // source[i] = '.';
            } else if (source[i] >= 65281 && source[i] <= 65374) {
                source[i] = (char) (source[i] - 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * transform full width char to half width char
     * <p>
     * <pre>
     * halfWidthToFullWidth(null) = null;
     * halfWidthToFullWidth("") = "";
     * halfWidthToFullWidth(" ") = new String(new char[] {12288});
     * halfWidthToFullWidth("!\"#$%&) = "！＂＃＄％＆";
     * </pre>
     *
     * @param s
     * @return
     */
    public static String halfWidthToFullWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == ' ') {
                source[i] = (char) 12288;
                // } else if (source[i] == '.') {
                // source[i] = (char)12290;
            } else if (source[i] >= 33 && source[i] <= 126) {
                source[i] = (char) (source[i] + 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * 在傳進來的字符串中间加 "—"
     *
     * @param s
     * @return
     */
    public static String formatString(String s) {
        if (isEmpty(s)) {
            return s;
        }
        StringBuffer str = new StringBuffer(s);
        if (s.length() == 6) {
            String strInsert = "-";
            str.insert(4, strInsert);
        } else if (s.length() == 8) {
            String strInsert = "-";
            str.insert(4, strInsert);
            str.insert(7, strInsert);
        }
        return str.toString();
    }

    /**
     * 将字符串大于len长度的字符截掉
     *
     * @param s             str
     * @param len           长度
     * @param isAddEllipsis 是否添加省略号
     * @return 返回截取的字符串
     */
    public static String subString(String s, int len, boolean isAddEllipsis) {
        if (isEmpty(s) || s.length() < len) {
            return s;
        }
        String str = s.substring(0, len - 1);
        if (isAddEllipsis) {
            return str + "...";
        } else {
            return str;
        }
    }

    /**
     * 格式化手机号码，344  （150 3390 0001） 并显示到edittext中
     *
     * @param s
     * @param et
     */
    public static void formatPhoneStr(CharSequence s, EditText et) {
        if (s == null || s.length() == 0) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        //设置文本到et
        if (!sb.toString().equals(s.toString())) {
            et.setText(sb.toString());
        }
    }

    /**
     * 格式化银行卡号 四位一隔，444N  （XXXX XXXX XXXX ） 并显示到edittext中
     *
     * @param s
     * @param et
     */
    public static void formatCardNo(CharSequence s, EditText et) {
        if (s == null || s.length() == 0) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 4 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() % 5 == 0) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            et.setText(sb.toString());
        }
    }

    /**
     * 格式化身份证
     *
     * @param s        传入的字符串
     * @param et       editext
     * @param isLongId true为18位 false15位
     */
    public static void formatIdCard(CharSequence s, EditText et, boolean isLongId) {
        if (s == null || s.length() == 0) return;
        //空格分割
        StringBuilder sb = new StringBuilder();
        if (isLongId) {
            //684格式
            for (int i = 0; i < s.length(); i++) {
                if (i != 6 && i != 15 && s.charAt(i) == ' ') {
                    continue;
                } else {
                    sb.append(s.charAt(i));
                    if ((sb.length() == 7 || sb.length() == 16) && sb.charAt(sb.length() - 1) != ' ') {
                        sb.insert(sb.length() - 1, ' ');
                    }
                }
            }
        } else {
            //663格式
            for (int i = 0; i < s.length(); i++) {
                if (i != 6 && i != 13 && s.charAt(i) == ' ') {
                    continue;
                } else {
                    sb.append(s.charAt(i));
                    if ((sb.length() == 7 || sb.length() == 14) && sb.charAt(sb.length() - 1) != ' ') {
                        sb.insert(sb.length() - 1, ' ');
                    }
                }
            }
        }
        //设置文本到et
        if (!sb.toString().equals(s.toString())) {
            et.setText(sb.toString());
        }
    }

    /**
     * 格式化手机号码，将带带空格的字符串空格去掉
     *
     * @param str
     */
    public static String cleanStrSpace(String str) {
        if (!TextUtils.isEmpty(str))
            return str.replace(" ", "");
        LogUtil.info("传入的字符串为空");
        return null;
    }

    /**
     * desc: 金额字体大小不一方法，整数位32dip,小数位22dip
     *
     * @param str    金额
     * @param format 格式化样式，比如"#,###,###,##0.0000000"
     * @return 返回一个格式化好了spannableString
     */
    public static SpannableString formatTextSize(BigDecimal str, String format, int textBigSize, int textSmallSize) {
        DecimalFormat df = new DecimalFormat(format);
        String assets = df.format(str);
        SpannableString msp = new SpannableString(assets);
        int dotsPos = assets.indexOf(".");//小数点的位置
        //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素。
        msp.setSpan(new AbsoluteSizeSpan(textBigSize, true), 0, dotsPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(textSmallSize, true), dotsPos + 1, assets.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
    }

    /**
     * desc: 返回一个字符串指定字符位置的字体大小
     *
     * @param string 需要formate的字符串
     * @param start  较小字符开始的位置
     * @return 返回一个spannableString
     */
    public static SpannableString formateTextSize(String string, int start, int textBigSize, int textSmallSize) {
        SpannableString msp = new SpannableString(string);
        //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素。
        msp.setSpan(new AbsoluteSizeSpan(textBigSize, true), 0, start - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(textSmallSize, true), start, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
    }

    /**
     * 在数字型字符串千分位加逗号
     *
     * @paramstr
     * @paramedtext
     * @returnsb.toString()
     */
    public static String addComma(String str, EditText edtext) {

        touzi_ed_values22 = edtext.getText().toString().trim().replaceAll(",", "");

        boolean neg = false;
        if (str.startsWith("-")) {//处理负数
            str = str.substring(1);
            neg = true;
        }
        String tail = null;
        if (str.indexOf('.') != -1) {//处理小数点
            tail = str.substring(str.indexOf('.'));
            str = str.substring(0, str.indexOf('.'));
        }
        StringBuilder sb = new StringBuilder(str);
        sb.reverse();
        for (int i = 3; i < sb.length(); i += 4) {
            sb.insert(i, ',');
        }
        sb.reverse();
        if (neg) {
            sb.insert(0, '-');
        }
        if (tail != null) {
            sb.append(tail);
        }
        return sb.toString();
    }
}
