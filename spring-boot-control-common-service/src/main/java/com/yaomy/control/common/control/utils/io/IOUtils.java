package com.yaomy.control.common.control.utils.io;

import com.yaomy.control.logback.utils.LoggerUtil;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * @Description: 通用IO流操作实用程序
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class IOUtils {
    public static void main(String[] args) {
        org.apache.commons.io.IOUtils.c
    }

    /**
     * 返回BufferedReader，如果给定的reader是BufferedReader，则直接返回，否则创建一个新的BufferedReader返回
     * @param reader 要包装或者返回的reader,不可以为null
     * @return BufferedReader
     */
    public static BufferedReader buffer(final Reader reader){
        return org.apache.commons.io.IOUtils.buffer(reader);
    }

    /**
     * 返回BufferedReader，如果给定的reader是BufferedReader，则直接返回，否则创建一个新的BufferedReader返回
     * @param reader 要包装或者返回的reader,不可以为null
     * @param size 如果reader不是BufferedReader，则创建的BufferedReader的大小
     * @return BufferedReader
     */
    public static BufferedReader buffer(final Reader reader, final int size){
        return org.apache.commons.io.IOUtils.buffer(reader, size);
    }

    /**
     * 返回BufferedWriter,如果给定的Writer已经是BufferedWriter，则直接返回，否则创建一个新的BufferedWriter
     * @param writer 要包装或者返回的Writer,不可以为null
     * @return  BufferedWriter
     */
    public static BufferedWriter buffer(final Writer writer){
        return org.apache.commons.io.IOUtils.buffer(writer);
    }
    /**
     * 返回BufferedWriter,如果给定的Writer已经是BufferedWriter，则直接返回，否则创建一个新的BufferedWriter
     * @param writer 要包装或者返回的Writer,不可以为null
     * @param size 如果writer不是BufferedWriter，则创建的BufferedWriter的大小
     * @return  BufferedWriter
     */
    public static BufferedWriter buffer(final Writer writer, final int size){
        return org.apache.commons.io.IOUtils.buffer(writer, size);
    }

    /**
     * 返回BufferedOutputStream，如果给定的outputStream是BufferedOutputStream，则直接返回，否则创建一个BufferedOutputStream返回
     * @param outputStream 给定要包装或者返回的输出流，不可以为null
     * @return BufferedOutputStream
     */
    public static BufferedOutputStream buffer(final OutputStream outputStream){
        try {
            return org.apache.commons.io.IOUtils.buffer(outputStream);
        } catch (NullPointerException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "转换OutputStream时异常："+e.toString());
            return null;
        }
    }

    /**
     * 返回BufferedOutputStream，如果给定的outputStream是BufferedOutputStream，则直接返回，否则创建一个BufferedOutputStream返回
     * @param outputStream 给定要包装或者返回的输出流，不可以为null
     * @param size 如果outputStream不是BufferedOutputStream，则创建的BufferedOutputStream的大小
     * @return BufferedOutputStream
     */
    public static BufferedOutputStream buffer(final OutputStream outputStream, final int size){
        try {
            return org.apache.commons.io.IOUtils.buffer(outputStream, size);
        } catch (NullPointerException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "转换OutputStream时异常："+e.toString());
            return null;
        }
    }

    /**
     * 返回BufferedInputStream，如果给定的inputStream是返回BufferedInputStream，则直接返回，否则创建一个BufferedInputStream返回
     * @param inputStream 给定要返回或者包装的InputStream，不可以为null
     * @return BufferedInputStream
     */
    public static BufferedInputStream buffer(final InputStream inputStream){
        try {
            return org.apache.commons.io.IOUtils.buffer(inputStream);
        } catch (NullPointerException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "转换InputStream时异常："+e.toString());
            return null;
        }
    }
    /**
     * 返回BufferedInputStream，如果给定的inputStream是返回BufferedInputStream，则直接返回，否则创建一个BufferedInputStream返回
     * @param inputStream 给定要返回或者包装的InputStream，不可以为null
     * @param size inputStream不是BufferedInputStream时创建新的BufferedInputStream的初始容量
     * @return BufferedInputStream
     */
    public static BufferedInputStream buffer(final InputStream inputStream, final int size){
        try {
            return org.apache.commons.io.IOUtils.buffer(inputStream, size);
        } catch (NullPointerException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "转换InputStream时异常："+e.toString());
            return null;
        }
    }
    /**
     * 读取给定URI的内容
     * @param uri URI资源
     * @param encoding URI资源编码
     * @return 请求的内容
     */
    public static String toString(final URI uri, final String encoding){
        try {
            return org.apache.commons.io.IOUtils.toString(uri, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }
    /**
     * 获取给定URL的内容
     * 示例：
     * URL url = new URL("http://ifeve.com/commons-io/");
     * String s = org.apache.commons.io.IOUtils.toString(url, CharsetUtils.UTF8);
     * @param url 给定的URL
     * @param encoding URL内容编码名称
     * @return 以字符串形式显示的URL内容
     */
    public static String toString(final URL url, final String encoding){
        try {
            return org.apache.commons.io.IOUtils.toString(url, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 将字节数组byte[]转换为字符串，使用指定的字符编码
     * @param input 要读取的字节数组
     * @param encoding 编码方式
     * @return 请求的字符串
     */
    public static String toString(final byte[] input, final String encoding){
        try {
            return org.apache.commons.io.IOUtils.toString(input, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "字节数组转化为字符串异常："+e.toString());
            return null;
        }
    }

    /**
     * 读取InputStream流转换为字符串，并使用指定的编码方式
     * @param input 将要读取的InputStream流
     * @param encoding 编码方式
     * @return 请求的字符串
     */
    public static String toString(final InputStream input, final String encoding){
        try {
            return org.apache.commons.io.IOUtils.toString(input, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 读取Reader流转换为字符串
     * @param input 将要读取的Reader流
     * @return 请求的字符串
     */
    public static String toString(final Reader input){
        try {
            return org.apache.commons.io.IOUtils.toString(input);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 从资源URI中获取内容转换为byte[]
     * @param uri 要读取的URI
     * @return 请求的byte[]
     */
    public static byte[] toByteArray(final URI uri){
        try {
            return org.apache.commons.io.IOUtils.toByteArray(uri);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 从URL中读取内容转换为byte[]
     * @param url 要读取的URL
     * @return 请求的byte[]
     */
    public static byte[] toByteArray(final URL url){
        try {
            return org.apache.commons.io.IOUtils.toByteArray(url);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 读取InputStream的内容转换为byte[]
     * @param input 要读取的InputStream流
     * @return 请求的byte[]
     */
    public static byte[] toByteArray(final InputStream input){
        try {
            return org.apache.commons.io.IOUtils.toByteArray(input);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 读取URLConnection的内容转换为byte[]
     * @param urlConn 要读取的URLConnection
     * @return 请求的byte[]
     */
    public static byte[] toByteArray(final URLConnection urlConn){
        try {
            return org.apache.commons.io.IOUtils.toByteArray(urlConn);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 读取InputStream流的内容转换为byte[],这个方法用来替换toByteArray(InputStream)在直到流中字节数的情况
     * @param input 要读取的InputStream流
     * @param size InputStream流中的字节数量
     * @return 请求的byte[]
     */
    public static byte[] toByteArray(final InputStream input, final int size){
        try {
            return org.apache.commons.io.IOUtils.toByteArray(input, size);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }
    /**
     * 读取InputStream流的内容转换为byte[],这个方法用来替换toByteArray(InputStream)在直到流中字节数的情况
     * @param input 要读取的InputStream流
     * @param size InputStream流中的字节数量,size不可以大于{@link Integer.MAX_VALUE}
     * @return 请求的byte[]
     */
    public static byte[] toByteArray(final InputStream input, final long size){
        try {
            return org.apache.commons.io.IOUtils.toByteArray(input, size);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 从Reader字符流中读取内容转换为byte[],并且使用指定的编码
     * @param input 要读取的Reader字符流
     * @param encoding 指定的编码
     * @return 请求的byte[]
     */
    public static byte[] toByteArray(final Reader input, final String encoding){
        try {
            return org.apache.commons.io.IOUtils.toByteArray(input, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 获取InputStream相同的全部内容，并转换成相同的InputStream结果数据；
     * 这种方法很有用：
     * 1.源输入流速度慢
     * 2.它的相关的网络资源，我们不可以长时间的保持连接
     * 3.它设置了网络超时
     * 它可以用来替换{@link #toByteArray(InputStream)}，因为它避免了byte[]不必要的分配和复制；
     * 此方法在内部缓冲input,因此无需使用BufferedInputStream
     * @param input 要完全缓存的流
     * @return 完全缓存的流
     */
    public static InputStream toBufferedInputStream(final InputStream input){
        try {
            return org.apache.commons.io.IOUtils.toBufferedInputStream(input);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }
    /**
     * 获取InputStream相同的全部内容，并转换成相同的InputStream结果数据；
     * 这种方法很有用：
     * 1.源输入流速度慢
     * 2.它的相关的网络资源，我们不可以长时间的保持连接
     * 3.它设置了网络超时
     * 它可以用来替换{@link #toByteArray(InputStream)}，因为它避免了byte[]不必要的分配和复制；
     * 此方法在内部缓冲input,因此无需使用BufferedInputStream
     * @param input 要完全缓存的流
     * @param size 初始缓冲区的大小
     * @return 完全缓存的流
     */
    public static InputStream toBufferedInputStream(final InputStream input, final int size){
        try {
            return org.apache.commons.io.IOUtils.toBufferedInputStream(input, size);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 将制定的CharSequence 转换为输入流，并使用指定的字符编码
     * @param input 要转换的CharSequence
     * @param encoding 编码
     * @return 输入流
     */
    public static InputStream toInputStream(final CharSequence input, final String encoding){
        try {
            return org.apache.commons.io.IOUtils.toInputStream(input, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "读取网络资源异常："+e.toString());
            return null;
        }
    }

    /**
     * 如果给定的reader是BufferedReader就返回，否则创建一个BufferedReader
     * @param reader 要包装的reader或者返回的BufferedReader,不可以为null
     * @return  BufferedReader
     */
    public static BufferedReader toBufferedReader(final Reader reader){
        return org.apache.commons.io.IOUtils.toBufferedReader(reader);
    }

    /**
     * 如果给定的reader是BufferedReader就返回，否则创建一个BufferedReader
     * @param reader 要包装的reader或者返回的BufferedReader,不可以为null
     * @param size 如果reader不是BufferedReader创建新的BufferedReader的大小
     * @return BufferedReader
     */
    public static BufferedReader toBufferedReader(final Reader reader, final int size){
        return org.apache.commons.io.IOUtils.toBufferedReader(reader, size);
    }

    /**
     * 读取InputStream流的内容转换为char数组，并使用指定的编码
     * 此方法在内部存在缓存，因此无需使用BufferedInputStream
     * @param is 要读取的InputStream流
     * @param encoding 指定的编码
     * @return char[]
     */
    public static char[] toCharArray(final InputStream is, final Charset encoding){
        try {
            return org.apache.commons.io.IOUtils.toCharArray(is, encoding);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "转换为char数组异常："+e.toString());
            return null;
        }
    }

    /**
     * 读取InputStream流的内容转换为char数组，并使用指定的编码
     * 此方法在内部存在缓存，因此无需使用BufferedInputStream
     * @param input 要读取的Reader流
     * @return char[]
     */
    public static char[] toCharArray(final Reader input){
        try {
            return org.apache.commons.io.IOUtils.toCharArray(input);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(IOUtils.class, "转换为char数组异常："+e.toString());
            return null;
        }
    }
}
