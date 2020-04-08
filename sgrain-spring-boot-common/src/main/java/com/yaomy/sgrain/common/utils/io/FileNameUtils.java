package com.yaomy.sgrain.common.utils.io;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

import java.io.IOException;
import java.util.Collection;

/**
 * @Description: 文件名称和文件路径操作工具类，共27个工具方法
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class FileNameUtils {
    /**
     * 使用常规命令行样式规则将文件名连接到基础路径
     * 在unix和windows上，除了分隔符之外，输出都是相同的
     * 示例：
     * /foo/ + bar          --&gt;   /foo/bar
     * /foo + bar           --&gt;   /foo/bar
     * /foo + /bar          --&gt;   /bar
     * /foo + C:/bar        --&gt;   C:/bar
     * /foo + C:bar         --&gt;   C:bar (*)
     * /foo/a/ + ../bar     --&gt;   foo/bar
     * /foo/ + ../../bar    --&gt;   null
     * /foo/ + /bar         --&gt;   /bar
     * /foo/.. + /bar       --&gt;   /bar
     * /foo + bar/c.txt     --&gt;   /foo/bar/c.txt
     * /foo/c.txt + bar     --&gt;   /foo/c.txt/bar (!)
     *
     * @param basePath 要附加的基路径，始终被视为路径
     * @param fullFilenameToAdd 要附加到及路径的文件名或者路径
     * @return 连接的路径，如果无效则为空。字符串中的空字节将被删除
     */
    public static String concat(final String basePath, final String fullFilenameToAdd){
        return FilenameUtils.concat(basePath, fullFilenameToAdd);
    }

    /**
     * 判断parent目录是否包含child元素（文件或目录）
     * 文件名需要规范化
     * parent目录不可以为空，为null将会抛出IllegalArgumentException
     * 目录不可以包含自己，将返回false
     * child为null，不会包含于任何parent，将返回false
     *
     * @param canonicalParent 视为父级的文件
     * @param canonicalChild 作为child考虑的文件
     * @return true parent目录包含child,否则返回false
     */
    public static boolean directoryContains(final String canonicalParent, final String canonicalChild){
        try {
            return FilenameUtils.directoryContains(canonicalParent, canonicalChild);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 使用系统的大小写规则检查两个文件名是否相等
     * @param filename1 要查询的第一个文件名，可能为null
     * @param filename2 要查询的第二个文件名，可能为null
     * @return 如果文件名相等则为true，null等于null
     */
    public static boolean equalsOnSystem(final String filename1, final String filename2){
        return equals(filename1, filename2, false, IOCase.SYSTEM);
    }
    /**
     * 检查两个文件名在规范化后是否相等
     * @param filename1 要查询的第一个文件名，可能为null
     * @param filename2 要查询的第二个文件名，可能为null
     * @return 如果文件名相等则为true，null等于null
     */
    public static boolean equalsNormalized(final String filename1, final String filename2){
        return equals(filename1, filename2, true, IOCase.SENSITIVE);
    }
    /**
     * 检查两个文件名使用规范化并使用系统大小写规则后是否相等
     * 两个文件名都首先传递给{@link #normalize(String)
     * @param filename1 要查询的第一个文件名，可能为null
     * @param filename2 要查询的第二个文件名，可能为null
     * @return 如果文件名相等则为true，null等于null
     */
    public static boolean equalsNormalizedOnSystem(final String filename1, final String filename2){
        return equals(filename1, filename2, true, IOCase.SYSTEM);
    }
    /**
     * 检查两个文件名是否完全相等，除了比较之外，对文件名不执行任何处理
     * @param filename1 要查询的第一个文件名，可能为null
     * @param filename2 要查询的第二个文件名，可能为null
     * @return 如果文件名相等则为true，null等于null
     */
    public static boolean equals(String filename1, String filename2){
        return equals(filename1, filename2, false, IOCase.SENSITIVE);
    }
    /**
     * 检查两个文件名是否相等，可以选择规范化并提供对区分大小写的控制
     * @param filename1 要查询的第一个文件名，可能为null
     * @param filename2 要查询的第二个文件名，可能为null
     * @param normalized 是否规范化文件名
     * @param caseSensitivity 使用什么区分大小写规则，空表示区分大小写
     * @return 如果文件名相等则为true，null等于null
     */
    public static boolean equals(String filename1, String filename2, final boolean normalized, IOCase caseSensitivity){
        return FilenameUtils.equals(filename1, filename2, normalized, caseSensitivity);
    }
    /**
     * 获取文件名称减去完整文件名称中的路径
     * 此方法将处理UNIX和WINDOWS格式的文件，返回最后一个正斜杠或反斜杠后的文本
     * 示例：
     * a/b/c.txt --&gt; c.txt
     * a.txt     --&gt; a.txt
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * 无论代码在那台机器上运行输出结果都是一样的
     * @param filename 查询的文件名，null返回null
     * @return 文件名
     */
    public static String getName(final String filename){
        return FilenameUtils.getName(filename);
    }
    /**
     * 从完整的文件名中获取基本名称，减去完整路径和扩展名
     * 此方法将处理UNIX和WINDOWS文件，返回最后一个正斜杠或反斜杠之后和最后一个点之前的文本。
     * 示例：
     * a/b/c.txt --&gt; c
     * a.txt     --&gt; a
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     *
     * 无论代码在哪里运行，输出都是相同的
     * @param filename 要查询的文件名，null返回null
     * @return 文件名，如果不存在则为空串
     */
    public static String getBaseName(final String filename){
        return FilenameUtils.getBaseName(filename);
    }

    /**
     * 从完整文件名获取路径,不包括前缀
     * 此方法将处理Unix或Windows格式的文件。该方法完全基于文本，返回最后一个正斜杠或反斜杠之前的文本。
     * 示例：
     * C:\a\b\c.txt --&gt; a\b\
     * ~/a/b/c.txt  --&gt; a/b/
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b/
     * a/b/c/       --&gt; a/b/c/
     * @param filename 要查询的文件名，null返回null
     * @return 文件路径不包括前缀
     */
    public static String getPath(final String filename){
        return FilenameUtils.getPath(filename);
    }

    /**
     * 从完整文件名获取路径，该文件名不包括前缀，也不包括最后的目录分隔符
     * 此方法将处理Unix或Windows格式的文件。该方法完全基于文本，返回最后一个正斜杠或反斜杠之前的文本。
     * 示例：
     * C:\a\b\c.txt --&gt; a\b
     * ~/a/b/c.txt  --&gt; a/b
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b
     * a/b/c/       --&gt; a/b/c
     * 无论代码在那台机器上运行，输出的结果都是相同的，此方法从结果中删除前缀
     * @param filename 查询的文件名，null返回null
     * @return 文件路径，不包括前缀
     */
    public static String getPathNoEndSeparator(final String filename){
        return FilenameUtils.getPathNoEndSeparator(filename);
    }
    /**
     * 从完整的文件名获取完整的路径，这是prefix+path
     * 此方法将处理Unix或Windows格式的文件。该方法完全基于文本，并返回包含最后一个正斜杠或反斜杠之前的文本。
     * 示例：
     * C:\a\b\c.txt --&gt; C:\a\b\
     * ~/a/b/c.txt  --&gt; ~/a/b/
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b/
     * a/b/c/       --&gt; a/b/c/
     * C:           --&gt; C:
     * C:\          --&gt; C:\
     * ~            --&gt; ~/
     * ~/           --&gt; ~/
     * ~user        --&gt; ~user/
     * ~user/       --&gt; ~user/
     * 无论代码在那台机器上运行，输出都是相同的
     * @param filename  要查询的文件名，null返回null
     * @return 文件全路径
     */
    public static String getFullPath(final String filename){
        return FilenameUtils.getFullPath(filename);
    }

    /**
     * 从完整的文件名获取完整的路径，就是prefix+path，并且不包含最后的目录分割符
     * 此方法将处理Unix或Windows格式的文件。该方法完全基于文本，并返回最后一个正斜杠或反斜杠之前的文本。
     * 示例：
     * C:\a\b\c.txt --&gt; C:\a\b
     * ~/a/b/c.txt  --&gt; ~/a/b
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b
     * a/b/c/       --&gt; a/b/c
     * C:           --&gt; C:
     * C:\          --&gt; C:\
     * ~            --&gt; ~
     * ~/           --&gt; ~
     * ~user        --&gt; ~user
     * ~user/       --&gt; ~user
     * 无论代码在那台机器上运行，输出都是相同的
     * @param filename 查询的文件名，null返回null
     * @return 文件全路径不包含最后的目录分隔符
     */
    public static String getFullPathNoEndSeparator(final String filename){
        return FilenameUtils.getFullPathNoEndSeparator(filename);
    }

    /**
     * 获取文件名的扩展名
     * 此方法返回文件名最后一个点后的文本部分。点后不能有目录分隔符。
     * 示例：
     * foo.txt      --&gt; "txt"
     * a/b/c.jpg    --&gt; "jpg"
     * a/b.txt/c    --&gt; ""
     * a/b/c        --&gt; ""
     * 无论代码在那台机器上运行，输出都是相同的
     * @param filename 查询的文件名，null返回null
     * @return 文件的扩展名，如果为null返回null,
     */
    public static String getExtension(final String filename){
        return FilenameUtils.getExtension(filename);
    }

    /**
     * 从一个完整的文件名中获取前缀，如：C:/ 或 ~/
     * 此方法将处理Unix或Windows格式的文件。前缀包括完整文件名中的第一个斜杠（如果适用）。
     * 示例：
     * Windows:
     * a\b\c.txt           --&gt; ""          --&gt; relative
     * \a\b\c.txt          --&gt; "\"         --&gt; current drive absolute
     * C:a\b\c.txt         --&gt; "C:"        --&gt; drive relative
     * C:\a\b\c.txt        --&gt; "C:\"       --&gt; absolute
     * \\server\a\b\c.txt  --&gt; "\\server\" --&gt; UNC
     *
     * Unix:
     * a/b/c.txt           --&gt; ""          --&gt; relative
     * /a/b/c.txt          --&gt; "/"         --&gt; absolute
     * ~/a/b/c.txt         --&gt; "~/"        --&gt; current user
     * ~                   --&gt; "~/"        --&gt; current user (slash added)
     * ~user/a/b/c.txt     --&gt; "~user/"    --&gt; named user
     * ~user               --&gt; "~user/"    --&gt; named user (slash added)
     * 无论代码运行在哪台机器上，输出都将相同。即无论Unix和Windows前缀如何匹配。
     * @param filename 查询的文件名，null返回null
     * @return 文件的前缀，如果无效返回null,字符串中的空字节将被删除
     */
    public static String getPrefix(final String filename){
        return FilenameUtils.getPrefix(filename);
    }

    /**
     * 返回文件名前缀的长度
     * 此方法将处理Unix或Windows格式的文件。前缀长度包括完整文件名中的第一个斜杠（如果适用）。因此，返回的长度可能大于输入字符串的长度。
     * 示例：
     * Windows:
     * a\b\c.txt           --&gt; ""          --&gt; relative
     * \a\b\c.txt          --&gt; "\"         --&gt; current drive absolute
     * C:a\b\c.txt         --&gt; "C:"        --&gt; drive relative
     * C:\a\b\c.txt        --&gt; "C:\"       --&gt; absolute
     * \\server\a\b\c.txt  --&gt; "\\server\" --&gt; UNC
     * \\\a\b\c.txt        --&gt;  error, length = -1
     *
     * Unix:
     * a/b/c.txt           --&gt; ""          --&gt; relative
     * /a/b/c.txt          --&gt; "/"         --&gt; absolute
     * ~/a/b/c.txt         --&gt; "~/"        --&gt; current user
     * ~                   --&gt; "~/"        --&gt; current user (slash added)
     * ~user/a/b/c.txt     --&gt; "~user/"    --&gt; named user
     * ~user               --&gt; "~user/"    --&gt; named user (slash added)
     * //server/a/b/c.txt  --&gt; "//server/"
     * ///a/b/c.txt        --&gt; error, length = -1
     * 无论代码在哪台机器上运行，输出都是相同的。也就是说，unix和windows前缀都是匹配的。
     * @param filename 查询的文件名，null返回-1
     * @return 前缀的长度，如果无效或者null返回-1
     */
    public static int getPrefixLength(final String filename){
        return FilenameUtils.getPrefixLength(filename);
    }

    /**
     * 检查文件filename的扩展名是否为指定的extension
     * 此方法获取最后一个点号后面的文本作为文件名的扩展名，点后面不可以有分隔符，扩展检查在所有的平台上都区分大消息
     * @param filename 查询的文件名，null返回false
     * @param extension 要检查的扩展名，null或“”没有扩展名
     * @return true 文件扩展名等于指定扩展，否则：false
     */
    public static boolean isExtension(final String filename, final String extension){
        return FilenameUtils.isExtension(filename, extension);
    }

    /**
     * 检查文件名的扩展名是否是指定的扩展名之一
     * 此方法获取文件名最后一个点号之后的文本部分作为扩展名，最后一个点号之后不可以有分隔符，扩展名检查在所有平台上区分大小写
     * @param filename 查询的文件名，null返回false
     * @param extensions 要检查的扩展名，null检查没有扩展
     * @return true 文件扩展名包含于指定扩展集合，false 不包含
     */
    public static boolean isExtension(final String filename, final String[] extensions){
        return FilenameUtils.isExtension(filename, extensions);
    }

    /**
     * 检查文件名的扩展名是否是指定的扩展名之一
     * 此方法获取文件名最后一个点号之后的文本部分作为扩展名，最后一个点号之后不可以有分隔符，扩展名检查在所有平台上区分大小写
     * @param filename 查询的文件名，null返回false
     * @param extensions 要检查的扩展名，null检查没有扩展
     * @return true 文件扩展名包含于指定扩展集合，false 不包含
     */
    public static boolean isExtension(final String filename, final Collection<String> extensions){
        return FilenameUtils.isExtension(filename, extensions);
    }

    /**
     * 规范化路径，删除双点和单点路径步骤
     * 此方法将路径规范化为标准格式
     * 输入可以包含unix或windows格式的分隔符。
     * 输出将包含系统格式的分隔符。
     * 将保留一个尾部斜杠
     * 双斜杠将合并为单斜杠（但会处理unc名称）。
     * 将删除单个点路径段
     * 双点将导致该路径段和之前的路径段被删除。
     * 如果双点没有要处理的父路径段，则返回{@code null}
     * 在unix和windows上，除了分隔符之外，输出都是相同的。
     * 示例：
     * /foo//               --&gt;   /foo/
     * /foo/./              --&gt;   /foo/
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar/
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo/
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar/
     * ~/../bar             --&gt;   null
     * @param filename 要规范化的文件名，null返回null
     * @return 规范化文件名，如果无效返回null,字符串中的空字节将被删除
     */
    public static String normalize(final String filename){
        return FilenameUtils.normalize(filename);
    }

    /**
     * 规范化路径，删除双点和单点路径步骤
     * 此方法将路径规范化为标准格式
     * 输入可以包含unix或windows格式的分隔符。
     * 输出将包含系统格式的分隔符。
     * 将保留一个尾部斜杠
     * 双斜杠将合并为单斜杠（但会处理unc名称）。
     * 将删除单个点路径段
     * 双点将导致该路径段和之前的路径段被删除。
     * 如果双点没有要处理的父路径段，则返回{@code null}
     * 在unix和windows上，除了分隔符之外，输出都是相同的。
     * 示例：
     * /foo//               --&gt;   /foo/
     * /foo/./              --&gt;   /foo/
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar/
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo/
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar/
     * ~/../bar             --&gt;   null
     * @param filename 要规范化的文件名，null返回null
     * @param unixSeparator true应该使用unix分隔符，false应该使用windows分隔符
     * @return 规范化文件名，如果无效返回null,字符串中的空字节将被删除
     */
    public static String normalize(final String filename, final boolean unixSeparator){
        return FilenameUtils.normalize(filename, unixSeparator);
    }

    /**
     * 规范化路径，删除双点和单点路径步骤,并且删除任何最后的目录分隔符
     * 此方法将路径规范化为标准格式
     * 输入可以包含unix或windows格式的分隔符。
     * 输出将包含系统格式的分隔符。
     * 将保留一个尾部斜杠
     * 双斜杠将合并为单斜杠（但会处理unc名称）。
     * 将删除单个点路径段
     * 双点将导致该路径段和之前的路径段被删除。
     * 如果双点没有要处理的父路径段，则返回{@code null}
     * 在unix和windows上，除了分隔符之外，输出都是相同的。
     * 示例：
     * /foo//               --&gt;   /foo/
     * /foo/./              --&gt;   /foo/
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar/
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo/
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar/
     * ~/../bar             --&gt;   null
     * @param filename 要规范化的文件名，null返回null
     * @return 规范化文件名，如果无效返回null,字符串中的空字节将被删除
     */
    public static String normalizeNoEndSeparator(final String filename){
        return FilenameUtils.normalizeNoEndSeparator(filename);
    }

    /**
     * 规范化路径，删除双点和单点路径步骤,并且删除任何最后的目录分隔符
     * 此方法将路径规范化为标准格式
     * 输入可以包含unix或windows格式的分隔符。
     * 输出将包含系统格式的分隔符。
     * 将保留一个尾部斜杠
     * 双斜杠将合并为单斜杠（但会处理unc名称）。
     * 将删除单个点路径段
     * 双点将导致该路径段和之前的路径段被删除。
     * 如果双点没有要处理的父路径段，则返回{@code null}
     * 在unix和windows上，除了分隔符之外，输出都是相同的。
     * 示例：
     * /foo//               --&gt;   /foo/
     * /foo/./              --&gt;   /foo/
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar/
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo/
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar/
     * ~/../bar             --&gt;   null
     * @param filename 要规范化的文件名，null返回null
     * @param unixSeparator true应该使用unix分隔符，false应该使用windows分隔符
     * @return 规范化文件名，如果无效返回null,字符串中的空字节将被删除
     */
    public static String normalizeNoEndSeparator(final String filename, final boolean unixSeparator){
        return FilenameUtils.normalizeNoEndSeparator(filename, unixSeparator);
    }

    /**
     * 从文件名中删除扩展名
     * 此方法返回最后一个点号之前的文本，点号后不能有目录分隔符
     * 示例：
     * foo.txt    --&gt; foo
     * a\b\c.jpg  --&gt; a\b\c
     * a\b\c      --&gt; a\b\c
     * a.b\c      --&gt; a.b\c
     * 无论代码运行在任何机器上，输出都是相同的
     * @param filename 查询的文件名，null返回null
     * @return 减去扩展名后的文件名
     */
    public static String removeExtension(final String filename){
        return FilenameUtils.removeExtension(filename);
    }

    /**
     * 检查文件名是否与指定的通配符匹配，始终测试区分大小写；
     * 通配符匹配器使用字符“?”和“*”表示单个或多个（零个或多个）通配符
     * 这与dos/unix命令行中经常出现的情况相同，检查总是区分大小写的
     * 示例：
     * wildcardMatch("c.txt", "*.txt")      --&gt; true
     * wildcardMatch("c.txt", "*.jpg")      --&gt; false
     * wildcardMatch("a/b/c.txt", "a/b/*")  --&gt; true
     * wildcardMatch("c.txt", "*.???")      --&gt; true
     * wildcardMatch("c.txt", "*.????")     --&gt; false
     * 注意序列“*？”在匹配字符串中当前无法正常工作
     * @param filename 要匹配的文件名
     * @param wildcardMatcher 要匹配的通配符字符串
     * @return true 匹配成功，false 匹配失败
     */
    public static boolean wildcardMatch(final String filename, final String wildcardMatcher){
        return FilenameUtils.wildcardMatch(filename, wildcardMatcher);
    }

    /**
     * 使用系统的大小写规则检查文件名是否与指定的通配符匹配。
     * 通配符匹配器使用字符“?”和“*”表示单个或多个（零个或多个）通配符
     * 这与dos/unix命令行中经常出现的情况相同，检查总是区分大小写的
     * 该检查在UNIX上区分大小写，在Windows上不区分大小写
     * 示例：
     * wildcardMatch("c.txt", "*.txt")      --&gt; true
     * wildcardMatch("c.txt", "*.jpg")      --&gt; false
     * wildcardMatch("a/b/c.txt", "a/b/*")  --&gt; true
     * wildcardMatch("c.txt", "*.???")      --&gt; true
     * wildcardMatch("c.txt", "*.????")     --&gt; false
     * 注意序列“*？”在匹配字符串中当前无法正常工作
     * @param filename 要匹配的文件名
     * @param wildcardMatcher 要匹配的通配符字符串
     * @return true 匹配成功，false 匹配失败
     */
    public static boolean wildcardMatchOnSystem(final String filename, final String wildcardMatcher) {
        return wildcardMatch(filename, wildcardMatcher, IOCase.SYSTEM);
    }
    /**
     * 检查文件名是否与指定的通配符匹配,并且允许控制大小写的敏感度
     * 通配符匹配器使用字符“?”和“*”表示单个或多个（零个或多个）通配符
     * 这与dos/unix命令行中经常出现的情况相同，检查总是区分大小写的
     * @param filename 要匹配的文件名
     * @param wildcardMatcher 要匹配的通配符字符串
     * @param caseSensitivity 使用什么区分大小写规则，空表示区分大小写
     * @return true 匹配成功，false 匹配失败
     */
    public static boolean wildcardMatch(final String filename, final String wildcardMatcher, IOCase caseSensitivity){
        return FilenameUtils.wildcardMatch(filename, wildcardMatcher, caseSensitivity);
    }
}
