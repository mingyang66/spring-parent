package com.yaomy.control.common.control.utils;

import com.google.common.collect.Lists;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.Checksum;

/**
 * @Description: 文件读写工具类
 * @Date: 2019/10/9 10:37
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class FileUtils {
    /**
     * 写数据到指定的文件中
     * @param filePath 文件路径
     * @param data 数据
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean write(final String filePath, final CharSequence data){
        return write(filePath, data, false, CharsetUtils.UTF8);
    }
    /**
     * 写数据到指定的文件中
     * @param filePath 文件路径
     * @param data 数据
     * @param encoding 编码
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean write(final String filePath, final CharSequence data, final String encoding){
        return write(filePath, data, false, encoding);
    }
    /**
     * 写数据到指定的文件中
     * @param filePath 文件路径
     * @param data 数据
     * @param append 是否累加
     * @param encoding 编码
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean write(final String filePath, final CharSequence data, final boolean append, final String encoding){
      return write(new File(filePath), data, append, encoding);
    }    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param data 数据
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean write(final File file, final CharSequence data){
        return write(file, data, false, CharsetUtils.UTF8);
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param data 数据
     * @param encoding 编码
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean write(final File file, final CharSequence data, final String encoding){
        return write(file, data, false, encoding);
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param data 数据
     * @param append 是否累加
     * @param encoding 编码
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean write(final File file, final CharSequence data, final boolean append, final String encoding){
        try{
            org.apache.commons.io.FileUtils.write(file, data, encoding, append);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "写数据到文件中异常！"+e.toString());
            return false;
        }
        return true;
    }

    /**
     * 写数据到指定的文件中
     * @param filePath 文件路径
     * @param lines 需要写入的数据集合
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLines(final String filePath, final Collection<?> lines){
        return writeLines(filePath, lines, false, CharsetUtils.UTF8);
    }
    /**
     * 写数据到指定的文件中
     * @param filePath 文件路径
     * @param lines 需要写入的数据集合
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLines(final String filePath, final Collection<?> lines, final String encoding){
        return writeLines(filePath, lines, false, encoding);
    }
    /**
     * 写数据到指定的文件中
     * @param filePath 文件路径
     * @param lines 需要写入的数据集合
     * @param append 是否累加
     * @param encoding 编码格式
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLines(final String filePath, final Collection<?> lines, final boolean append, final String encoding){
        return writeLines(new File(filePath), lines, append, encoding);
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param lines 需要写入的数据集合
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLines(final File file, final Collection<?> lines){
        return writeLines(file, lines, false, CharsetUtils.UTF8);
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param lines 需要写入的数据集合
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLines(final File file, final Collection<?> lines, final String encoding){
        return writeLines(file, lines, false, encoding);
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param lines 需要写入的数据集合
     * @param append 是否累加
     * @param encoding 编码格式
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLines(final File file, final Collection<?> lines, final boolean append, final String encoding){
        try{
            org.apache.commons.io.FileUtils.writeLines(file, encoding,lines, append);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "写数据到文件中异常！"+e.toString());
            return false;
        }
        return true;
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param lines 需要写入的数据集合
     * @param lineEnding 行结尾字符串,默认null时会自动换行，否则需要加上'\n'
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLinesEnding(final String filePath, final Collection<?> lines, final String lineEnding){
        return writeLinesEnding(filePath, lines, lineEnding, false, CharsetUtils.UTF8);
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param lines 需要写入的数据集合
     * @param append 是否累加
     * @param lineEnding 行结尾字符串,默认null时会自动换行，否则需要加上'\n'
     * @param encoding 编码格式
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLinesEnding(final String filePath, final Collection<?> lines, final String lineEnding, final boolean append, final String encoding){
       return writeLinesEnding(new File(filePath), lines, lineEnding, append, encoding);
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param lines 需要写入的数据集合
     * @param lineEnding 行结尾字符串,默认null时会自动换行，否则需要加上'\n'
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLinesEnding(final File file, final Collection<?> lines, final String lineEnding){
        return writeLinesEnding(file, lines, lineEnding, false, CharsetUtils.UTF8);
    }
    /**
     * 写数据到指定的文件中
     * @param file 文件对象
     * @param lines 需要写入的数据集合
     * @param append 是否累加
     * @param lineEnding 行结尾字符串,默认null时会自动换行，否则需要加上'\n'
     * @param encoding 编码格式
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLinesEnding(final File file, final Collection<?> lines, final String lineEnding, final boolean append, final String encoding){
        try{
            org.apache.commons.io.FileUtils.writeLines(file, encoding, lines, lineEnding, append);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "写数据到文件中异常！"+e.toString());

            return false;
        }
        return true;
    }
    /**
     * 将字节数组写入到指定的文件中;
     * @param file 文件对象
     * @param data 字节数组
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeLinesEnding(final File file, final byte[] data){
        return writeByteArrayToFile(file, data, false);
    }
    /**
     * 将字节数组写入到指定的文件中
     * @param file 文件对象
     * @param data 字节数组
     * @param append true添加到文件末尾，false覆盖文件
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeByteArrayToFile(final File file, final byte[] data, final boolean append){
        try{
            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data, append);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "写数据到文件中异常！"+e.toString());
            return false;
        }
        return true;
    }
    /**
     * 将字节数组写入到指定的文件中;
     * @param file 文件对象
     * @param data 字节数组
     * @param off 字节数组其实位置
     * @param len 写入的字节长度
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeByteArrayToFile(final File file, final byte[] data, final int off, final int len){
        return writeByteArrayToFile(file, data, off, len, false);
    }
    /**
     * 将字节数组写入到指定的文件中
     * @param file 文件对象
     * @param data 字节数组
     * @param off 字节数组其实位置
     * @param len 写入的字节长度
     * @param append true添加到文件末尾，false覆盖文件
     * @return 返回true,如果写入成功，返回false,如果写入失败
     */
    public static boolean writeByteArrayToFile(final File file, final byte[] data, final int off, final int len, final boolean append){
        try{
            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data, off, len, append);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "写数据到文件中异常！"+e.toString());
            return false;
        }
        return true;
    }
    /**
     * 读取文件，整个文件读出一个字符串；编码方式默认是UTF8
     * @param filePath 文件路径
     * @return List
     */
    public static String readFileToString(final String filePath){
        return readFileToString(filePath, CharsetUtils.UTF8);
    }
    /**
     * 读取文件，整个文件读出一个字符串
     * @param filePath 文件路径
     * @param encoding 编码方式
     * @return List
     */
    public static String readFileToString(final String filePath, final String encoding){
       return readFileToString(new File(filePath), encoding);
    }
    /**
     * 读取文件，整个文件读出一个字符串；编码方式默认是UTF8
     * @param file 文件对象
     * @return List
     */
    public static String readFileToString(final File file){
        return readFileToString(file, CharsetUtils.UTF8);
    }
    /**
     * 读取文件，整个文件读出一个字符串
     * @param file 文件对象
     * @param encoding 编码方式
     * @return List
     */
    public static String readFileToString(final File file, final String encoding){
        try{
            return org.apache.commons.io.FileUtils.readFileToString(file, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "读文件异常！"+e.toString());
            return StringUtils.EMPTY;
        }
    }
    /**
     * 将文件内容读取到字节数组之中
     * @param filePath 文件路径
     * @return byte[]
     */
    public static byte[] readFileToByteArray(final String filePath){
        return readFileToByteArray(new File(filePath));
    }
    /**
     * 将文件内容读取到字节数组之中
     * @param file 文件对象
     * @return byte[]
     */
    public static byte[] readFileToByteArray(final File file){
        try{
            return org.apache.commons.io.FileUtils.readFileToByteArray(file);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "读文件异常！"+e.toString());
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }
    /**
     * 读取文件，按照行读取, 编码方式默认是UTF-8
     * @param filePath 文件路径
     * @return List
     */
    public static List<String> readLines(final String filePath){
        return readLines(filePath, CharsetUtils.UTF8);
    }
    /**
     * 读取文件，按照行读取
     * @param filePath 文件路径
     * @param encoding 编码方式
     * @return List
     */
    public static List<String> readLines(final String filePath, final String encoding){
        return readLines(new File(filePath), encoding);
    }
    /**
     * 读取文件，按照行读取, 编码方式默认是UTF-8
     * @param file 文件对象
     * @return List
     */
    public static List<String> readLines(final File file){
        return readLines(file, CharsetUtils.UTF8);
    }
    /**
     * 读取文件，按照行读取
     * @param file 文件对象
     * @param encoding 编码方式
     * @return List
     */
    public static List<String> readLines(final File file, final String encoding){
        try{
            return org.apache.commons.io.FileUtils.readLines(file, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "读文件异常！"+e.toString());
            return Collections.emptyList();
        }
    }
    /**
     * 计算文件大小
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static long sizeOf(final String filePath){
        return sizeOf(new File(filePath));
    }
    /**
     * 计算文件大小
     * @param file 文件对象
     * @return 文件大小
     */
    public static long sizeOf(final File file){
        try{
            return org.apache.commons.io.FileUtils.sizeOf(file);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "计算文件大小异常！"+e.toString());
            return 0;
        }
    }
    /**
     * 计算文件大小
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static BigInteger sizeOfAsBigInteger(final String filePath){
        return sizeOfAsBigInteger(new File(filePath));
    }
    /**
     * 计算文件大小
     * @param file 文件对象
     * @return 文件大小
     */
    public static BigInteger sizeOfAsBigInteger(final File file){
        try{
            return org.apache.commons.io.FileUtils.sizeOfAsBigInteger(file);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "计算文件大小异常！"+e.toString());
            return BigInteger.ZERO;
        }
    }
    /**
     * 计算目录下文件大小
     * @param directory 文件目录对象
     * @return 文件大小
     */
    public static long sizeOfDirectory(final String directory){
        return sizeOfDirectory(new File(directory));
    }
    /**
     * 计算目录下文件大小
     * @param directory 文件目录对象
     * @return 文件大小
     */
    public static long sizeOfDirectory(final File directory){
        try{
            return org.apache.commons.io.FileUtils.sizeOfDirectory(directory);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "计算目录文件大小异常！"+e.toString());
            return 0;
        }
    }
    /**
     * 计算目录下文件大小
     * @param directory 文件目录对象
     * @return 文件大小
     */
    public static BigInteger sizeOfDirectoryAsBigInteger(final String directory){
        return sizeOfDirectoryAsBigInteger(new File(directory));
    }
    /**
     * 计算目录下文件大小
     * @param directory 文件目录对象
     * @return 文件大小
     */
    public static BigInteger sizeOfDirectoryAsBigInteger(final File directory){
        try{
            return org.apache.commons.io.FileUtils.sizeOfDirectoryAsBigInteger(directory);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "计算目录文件大小异常！"+e.toString());
            return BigInteger.ZERO;
        }
    }

    /**
     * @return 返回系统临时目录路径
     */
    public static String getTempDirectoryPath(){
        return org.apache.commons.io.FileUtils.getTempDirectoryPath();
    }
    /**
     * @return 返回系统临时目录路径
     */
    public static File getTempDirectory(){
        return org.apache.commons.io.FileUtils.getTempDirectory();
    }
    /**
     * @return 返回用户主目录路径
     */
    public static String getUserDirectoryPath(){
        return org.apache.commons.io.FileUtils.getUserDirectoryPath();
    }
    /**
     * @return 返回用户主目录路径
     */
    public static File getUserDirectory(){
        return org.apache.commons.io.FileUtils.getUserDirectory();
    }

    /**
     *
     * @param names 根据路径名称集构造File对象，如 ["D:", "work", "workplacec", "Emm"]
     * @return File文件对象
     */
    public static File getFile(final String... names){
        return org.apache.commons.io.FileUtils.getFile(names);
    }

    /**
     *
     * @param file 父目录对象， 如：new File("D:")
     * @param names 根据路径名称集构造File对象，如 ["work", "workplacec", "Emm"]
     * @return File文件对象
     */
    public static File getFile(final File file, final String... names){
        return org.apache.commons.io.FileUtils.getFile(file, names);
    }

    /**
     *
     * @param url 参数为null或者new URL("file://D:\\d.txt")
     * @return File文件对象
     */
    public static File toFile(final URL url){
        try {
            return org.apache.commons.io.FileUtils.toFile(url);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "指定文件转换为File对象异常："+e.toString());
            return null;
        }
    }
    /**
     *
     * @param url 参数为null或者new URL("file://D:\\d.txt")
     * @return File文件对象
     */
    public static File[] toFiles(final URL[] url){
        try {
            return org.apache.commons.io.FileUtils.toFiles(url);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "指定文件转换为File对象异常："+e.toString());
            return null;
        }
    }

    /**
     *
     * @param files 文件数组
     * @return java.net.URL数组
     */
    public static URL[] toUrls(final File[] files){
        try {
            return org.apache.commons.io.FileUtils.toURLs(files);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "指定文件对象转换为java.net.URL对象异常："+e.toString());
            return null;
        }
    }

    /**
     *
     * @param file 创建文件，如果文件存在则更新时间；如果不存在，创建一个空文件
     * @return true 创建成功，false 创建失败
     */
    public static boolean touch(final File file){
        try {
            org.apache.commons.io.FileUtils.touch(file);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "创建文件对象异常："+e.toString());
            return false;
        }
        return true;
    }

    /**
     *  删除指定的file文件，不会抛出任何异常，如果file是一个目录则会删除当前目录及其子目录
     * @param file 文件对象
     * @return true 删除成功，false 删除失败
     */
    public static boolean deleteQuietly(final File file){
        return org.apache.commons.io.FileUtils.deleteQuietly(file);
    }

    /**
     * 删除指定的目录，当目录不存在或者不是目录的时候会删除失败
     * @param directory
     * @return true 删除成功，false 删除失败
     */
    public static boolean deleteDirectory(final File directory){
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(directory);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "删除指定的目录异常："+e.toString());
            return false;
        }
        return true;
    }

    /**
     * 强制删除指定的文件或者目录
     * @param file 文件对象
     * @return true 删除成功，false 删除失败
     */
    public static boolean forceDelete(final File file){
        try {
            org.apache.commons.io.FileUtils.forceDelete(file);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "强制删除指定的文件|目录异常："+e.toString());
            return false;
        }
        return true;
    }

    /**
     * 在调度JVM退出时删除文件，通常用在临时文件的删除
     * @param file 文件或目录
     * @return true 删除成功，false 删除失败
     */
    public static boolean forceDeleteOnExit(final File file){
        try {
            org.apache.commons.io.FileUtils.forceDeleteOnExit(file);
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "强制删除指定的文件|目录异常："+e.toString());
            return false;
        }
        return true;
    }

    /**
     * 将给定的字节（byte）数据转换为最接近的单位；如：1023->1023 bytes、1024->1KB、 1025->1KB、2047->1KB、2048->2KB、 2049->2kb
     * 单位包括：EB, PB, TB, GB, MB, KB or bytes
     * @param size 字节大小
     * @return string 如：1 bytes
     */
    public static String byteCountToDisplaySize(final long size){
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }
    /**
     * 将给定的字节（byte）数据转换为最接近的单位；如：1023->1023 bytes、1024->1KB、 1025->1KB、2047->1KB、2048->2KB、 2049->2kb
     * 单位包括：EB, PB, TB, GB, MB, KB or bytes
     * @param size 字节大小
     * @return string 如：1 bytes
     */
    public static String byteCountToDisplaySize(final BigInteger size){
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);
    }

    /**
     * 打开指定文件的FileInputStream输出流
     * @param file 文件对象
     * @return FileInputStream，异常时返回null
     */
    public static FileInputStream openInputStream(final File file){
        try{
            return org.apache.commons.io.FileUtils.openInputStream(file);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "打开文件FileInputStream输出流异常："+e.toString());
            return null;
        }
    }

    /**
     * 打开指定文件的FileOutputStream输入流
     * @param file 文件对象
     * @return FileOutputStream， 异常时返回null
     */
    public static FileOutputStream openOutputStream(final File file){
        try{
            return org.apache.commons.io.FileUtils.openOutputStream(file);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "打开文件FileInputStream输出流异常："+e.toString());
            return null;
        }
    }

    /**
     * 打开指定文件的FileOutputStream输入流
     * @param file 文件对象
     * @param append true 添加到文件末尾，false 覆盖文件
     * @return FileOutputStream， 异常时返回null
     */
    public static FileOutputStream openOutputStream(final File file, final boolean append){
        try{
            return org.apache.commons.io.FileUtils.openOutputStream(file, append);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "打开文件FileInputStream输出流异常："+e.toString());
            return null;
        }
    }

    /**
     * 指定的父File对象是否包含子File对象
     * @param directory 父目录
     * @param child 子对象可以是目录也可以是文件
     * @return true 如果包含，false 不包含；child 为null 返回false
     */
    public static boolean directoryContains(final File directory, final File child){
        try {
            return org.apache.commons.io.FileUtils.directoryContains(directory, child);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "打开文件FileInputStream输出流异常："+e.toString());
            return false;
        }
    }

    /**
     * 比较两个文件的内容是否相同
     * @param file1 第一个文件
     * @param file2 第二个文件
     * @return true 文件内容相同，false 文件内容不同
     */
    public static boolean contentEquals(final File file1, final File file2){
        try {
            return org.apache.commons.io.FileUtils.contentEquals(file1, file2);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "比较文件内容是否相同异常："+e.toString());
            return false;
        }
    }

    /**
     * 比较两个文件的内容是否相同，ignoring EOL characters（EOL END OF LINE）忽略换行符
     * @param file1 第一个文件
     * @param file2 第二个文件
     * @param charsetName 字符编码，可以为null
     * @return true 文件内容相同，false 文件内容不同
     */
    public static boolean contentEqualsIgnoreEOL(final File file1, final File file2, final String charsetName){
        try {
            return org.apache.commons.io.FileUtils.contentEqualsIgnoreEOL(file1, file2, charsetName);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "比较文件内容是否相同异常："+e.toString());
            return false;
        }
    }

    /**
     * 将包含java.io.File对象的集合转换为File[]
     * @param files java.io.File集合
     * @return File[]
     */
    public static File[] convertFileCollectionToFileArray(final Collection<File> files){
        return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(files);
    }

    /**
     * 校验文件数据的正确性
     * org.apache.commons.io.FileUtils.checksum(file, new CRC32());
     * @param file 文件对象
     * @param checksum 校验算法
     * @return Checksum
     */
    public static Checksum checksum(final File file, final Checksum checksum){
        try{
            return org.apache.commons.io.FileUtils.checksum(file, checksum);
        }catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "校验文件正确性异常："+e.toString());
            return null;
        }
    }
    /**
     * 校验文件数据的正确性,使用CRC32 循环冗余算法校验数据的正确性
     * @param file 文件对象
     * @param checksum 校验算法
     * @return Checksum
     */
    public static Long checksumCRC32(final File file){
        try{
            return org.apache.commons.io.FileUtils.checksumCRC32(file);
        }catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "校验文件正确性异常："+e.toString());
            return null;
        }
    }

    /**
     * 清空目录但是不删除目录
     * @param directory 目录
     * @return true 清空目录成功，false 清空目录失败
     */
    public static boolean cleanDirectory(final File directory){
        try {
            org.apache.commons.io.FileUtils.cleanDirectory(directory);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "清空目录下面的文件及目录异常："+e.toString());
            return false;
        }
        return true;
    }

    /**
     * 将整个目录复制到保留文件日期的新位置
     * @param srcDir 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null；如果目标目录不存在则创建该目录，否则合并目标源
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyDirectory(final File srcDir, final File destDir){
        return copyDirectory(srcDir, destDir, true);
    }
    /**
     * 将整个目录复制到新位置
     *
     * @param srcDir 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null；如果目标目录不存在则创建该目录，否则合并目标源
     * @param preserveFileDate true 如果副本的文件日期要保持与源文件相同
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyDirectory(final File srcDir, final File destDir, final boolean preserveFileDate){
        return copyDirectory(srcDir, destDir, null, preserveFileDate);
    }
    /**
     * 将筛选后的目录复制到保留文件日期的新位置
     * 示例：复制目录和txt文件
     * 为“.txt”文件创建过滤器
     * OFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
     * IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
     *
     * 为目录或“.txt”文件创建筛选器
     * FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
     *
     * 使用筛选器复制
     * FileUtils.copyDirectory(srcDir, destDir, filter);
     *
     * @param srcDir 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null；如果目标目录不存在则创建该目录，否则合并目标源
     * @param filter 要应用的筛选器，null意味着复制所有的目录和文件
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyDirectory(final File srcDir, final File destDir, final FileFilter filter){
        return copyDirectory(srcDir, destDir, filter, true);
    }
    /**
     * 将经过过滤器筛选的目录复制到指定的目录
     * 示例：复制目录和txt文件
     * 为“.txt”文件创建过滤器
     * OFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
     * IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
     *
     * 为目录或“.txt”文件创建筛选器
     * FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
     *
     * 使用筛选器复制
     * FileUtils.copyDirectory(srcDir, destDir, filter, false);
     *
     * @param srcDir 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null；如果目标目录不存在则创建该目录，否则合并目标源
     * @param filter 要应用的筛选器，null意味着复制所有的目录和文件
     * @param preserveFileDate true 如果副本的文件日期要保持与源文件相同
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyDirectory(final File srcDir, final File destDir, final FileFilter filter, final boolean preserveFileDate){
        try{
            org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir, filter, true);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "复制目录文件异常："+e.toString());
            return false;
        }
        return true;
    }

    /**
     * 将目录复制到保存文件日期的另外一个目录中，此方法将源目录及其所有内容复制到指定目标目录中同名的目录
     * @param srcDir 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null；如果目标目录不存在则创建该目录，否则合并目标源
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyDirectoryToDirectory(final File srcDir, final File destDir){
        try{
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(srcDir, destDir);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "复制目录文件异常："+e.toString());
            return false;
        }
        return true;
    }
    /**
     * 将文件复制到目录（保留文件日期）
     * 此方法复制指定源文件内容到指定目标目录中同名文件；如果目标目录不存在，则创建该目录，否则将覆盖
     * @param srcFile 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyFileToDirectory(final File srcFile, final File destDir){
        return copyFileToDirectory(srcFile, destDir, true);
    }
    /**
     * 将文件复制到目录（可以选择保留文件日期）
     * 此方法复制指定源文件内容到指定目标目录中同名文件；如果目标目录不存在，则创建该目录，否则将覆盖
     * @param srcFile 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null
     * @param preserveFileDate true 保留文件最后修改日期，false 不保留
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyFileToDirectory(final File srcFile, final File destDir, final boolean preserveFileDate){
        try {
            org.apache.commons.io.FileUtils.copyFileToDirectory(srcFile, destDir, preserveFileDate);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "复制目录文件异常："+e.toString());
            return false;
        }
        return true;
    }

    /**
     * 将文件或目录复制到保留文件日期的另外一个目录中；此方法将源文件或目录及其所有内容复制到指定目标目录中同名的目录，如果目标目录不存在，则创建该目录。
     * 如果目标目录确实存在，则此方法将源与目标合并，以源为准
     * @param src 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyToDirectory(final File src, final File destDir){
        try{
            org.apache.commons.io.FileUtils.copyToDirectory(src, destDir);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "复制目录文件异常："+e.toString());
            return false;
        }
        return true;
    }
    /**
     * 将文件复制到保留每个文件日期的目录；此方法复制指定源文件的内容到指定目标目录同名文件夹下，如果目录不存在，则创建该目录，否则覆盖目录
     * @param srcs 要复制的现有目录，不可以为null
     * @param destDir 新目录，不可以为null
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyToDirectory(final Iterable<File> srcs, final File destDir){
        try{
            org.apache.commons.io.FileUtils.copyToDirectory(srcs, destDir);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "复制目录文件异常："+e.toString());
            return false;
        }
        return true;
    }
    /**
     *  复制文件到一个新的位置,这个方法复制指定源文件的内容到指定的目标文件；如果目标文件不存在，则创建保存该文件的目录。如果目标文件存在，则此方法将覆盖它。
     * @param srcFile 要复制现有的文件，不可以为null
     * @param destFile 新的文件，不可以为null
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyFile(final File srcFile, final File destFile){
        return copyFile(srcFile, destFile, true);
    }
    /**
     *  复制文件到一个新的位置,这个方法复制指定源文件的内容到指定的目标文件；如果目标文件不存在，则创建保存该文件的目录。如果目标文件存在，则此方法将覆盖它。
     * @param srcFile 要复制现有的文件，不可以为null
     * @param destFile 新的文件，不可以为null
     * @param preserveFileDate true 副本文件的日期保持一样，false 不保持
     * @return true 复制成功， false 复制失败
     */
    public static boolean copyFile(final File srcFile, final File destFile, final boolean preserveFileDate){
        try {
            org.apache.commons.io.FileUtils.copyFile(srcFile, destFile, preserveFileDate);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "复制文件异常："+e.toString());
            return false;
        }
        return true;
    }

    /**
     * 将字节从文件中复制到OutputStream输出流中，
     * @param input 读取字节流的文件
     * @param output 将要写入的OutputStream字节流
     * @return 复制的字节数
     */
    public static long copyFile(final File input, final OutputStream output){
        try{
            return org.apache.commons.io.FileUtils.copyFile(input, output);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "复制文件异常："+e.toString());
            return 0;
        }
    }

    /**
     * 从InputStream输入流中读取字节到目标文件，如果目标文件不存在，则将会被闯将；
     * @param source 读取字节的InputStream流
     * @param destination 写入字节的目标文件
     * @return true 复制读取成功，false 复制读取失败
     */
    public static boolean copyToFile(final InputStream source, final File destination){
        try {
            org.apache.commons.io.FileUtils.copyToFile(source, destination);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(FileUtils.class, "复制文件异常："+e.toString());
            return false;
        }
        return true;
    }
}
