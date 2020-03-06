package com.yaomy.sgrain.common.control.utils.io;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 通用IO流操作实用程序,共87个工具方法
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class IOUtils {
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
            return null;
        }
    }

    /**
     * 关闭URLConnection
     * @param conn 要关闭的URLConnection连接
     */
    public static void close(final URLConnection conn){
        org.apache.commons.io.IOUtils.close(conn);
    }

    /**
     * 比较两个输入流是否相等，如果输入流没内部没有使用BufferedInputStream缓存，则在此方法内部使用BufferedInputStream缓存包装
     * @param input1 第一个输入流
     * @param input2 第二个输入流
     * @return true 如果两个输入流相等或者两个输入流都不存在，否则：false
     */
    public static boolean contentEquals(InputStream input1, InputStream input2){
        try {
            return org.apache.commons.io.IOUtils.contentEquals(input1, input2);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 比较两个Readers 的内容是否相等
     * 如果字符流内部没有使用BufferedReader缓存，则在此方法内部使用BufferedReader缓存包装
     * @param input1 第一个字符流
     * @param input2 第二个字符流
     * @return true 如果两个readers相同或者都不存在，否则：false
     */
    public static boolean contentEquals(Reader input1, Reader input2){
        try {
            return org.apache.commons.io.IOUtils.contentEquals(input1, input2);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 比较两个Readers 的内容是否相等,忽略EOL符
     * 如果字符流内部没有使用BufferedReader缓存，则在此方法内部使用BufferedReader缓存包装
     * @param input1 第一个字符流
     * @param input2 第二个字符流
     * @return true 如果两个readers相同（忽略EOL的不同），否则：false
     */
    public static boolean contentEqualsIgnoreEOL(final Reader input1, final Reader input2){
        try {
            return org.apache.commons.io.IOUtils.contentEqualsIgnoreEOL(input1, input2);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从input复制字符到output；此方法在内部使用缓冲，因此无需使用BufferedReader
     * 大数据流（超过2GB）将会在复制完成后返回-1，因为无法将字符数作为int值返回，对于大数据流可以使用{@link copyLarge(Reader, Writer)}方法
     * @param input 要读取的Reader
     * @param output 要写入的Writer
     * @return 复制的字符数量，或-1，如果返回的字符数&gt {@link Integer.MAX_VALUE}
     */
    public static int copy(final Reader input, final Writer output){
        try {
            return org.apache.commons.io.IOUtils.copy(input, output);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }
    /**
     * 复制字节码从InputStream到OutputStream，此方法在内部使用缓存，因此无需使用BufferedInputStream
     * 大数据流（超过2GB）将在复制完成后返回字节复制值-1，因为无法将字节数作为int值返回，对于大数据流使用copyLarge(InputStream, OutputStream)方法
     * @param input 要读取InputStream流
     * @param output 要写入的OutputStream流
     * @return 复制的字节数或者-1，如果&gt{@link Integer.MAX_VALUE}
     */
    public static int copy(final InputStream input, final OutputStream output){
        try {
            return org.apache.commons.io.IOUtils.copy(input, output);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 复制字节码从InputStream到OutputStream,使用给定大小的内部字节缓冲，此方法在内部使用缓存，因此无需使用BufferedInputStream
     * @param input 要读取InputStream流
     * @param output 要写入的OutputStream流
     * @param bufferSize 字节缓冲大小
     * @return 复制的字节数
     */
    public static long copy(final InputStream input, final OutputStream output, final int bufferSize){
        try {
            return org.apache.commons.io.IOUtils.copy(input, output, bufferSize);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 从InputStream中读取字节流并写入到字符输出流Writer中，使用指定的字符编码
     * 此方法内部使用缓冲，因此无需使用BufferedInputStream
     * @param input 要读取的InputStream流
     * @param output 要写入的Writer字符
     * @param inputEncoding 指定读取InputStream的字符编码，null为默认
     * @return true 复制成功，false复制失败
     */
    public static boolean copy(final InputStream input, final Writer output, final String inputEncoding){
        try {
            org.apache.commons.io.IOUtils.copy(input, output, inputEncoding);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从Reader中复制字符到OutputStream输出字节流，使用指定的输出字节流编码
     * 此方法使用内部缓冲，因此无需使用BufferedReader
     * 此方法由于实现了OutputStreamWriter，执行刷新
     * 此方法使用@link OutputStreamWriter}
     * @param input 要读取的Reader
     * @param output 要写入的OutputStream
     * @param outputEncoding 指定的输出字节流编码
     * @return true 复制成功，false复制失败
     */
    public static boolean copy(final Reader input, final OutputStream output, final String outputEncoding){
        try {
            org.apache.commons.io.IOUtils.copy(input, output, outputEncoding);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从InputStream复制大数据流（超过2GB）到OutputStream
     * 此方法使用内部缓冲，因此无需使用BufferedInputStream
     * 内部缓冲的大小默认是{@link #DEFAULT_BUFFER_SIZE}
     * @param input 要读取的InputStream
     * @param output 要写入的OutputStream
     * @return 复制的字节流数量
     */
    public static long copyLarge(final InputStream input, final OutputStream output){
        try {
            return org.apache.commons.io.IOUtils.copyLarge(input, output);
        } catch (IOException e){
            e.printStackTrace();
            return 0L;
        }
    }
    /**
     * 从InputStream复制大数据流（超过2GB）到OutputStream
     * 此方法使用内部缓冲，因此无需使用BufferedInputStream
     * @param input 要读取的InputStream
     * @param output 要写入的OutputStream
     * @param buffer 内部缓冲的大小
     * @return 复制的字节流数量
     */
    public static long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer){
        try {
            return org.apache.commons.io.IOUtils.copyLarge(input, output, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 从InputStream复制大数据流（超过2GB）到OutputStream
     * 此方法使用内部缓冲，因此无需使用BufferedInputStream
     * @param input 要读取的InputStream
     * @param output 要写入的OutputStream
     * @param inputOffset 读取字节流时跳过的字节数量,-ve意味着忽略
     * @param length 要复制的字节数量，-ve代表全部
     * @return 复制的字节流数量
     */
    public static long copyLarge(final InputStream input, final OutputStream output, final long inputOffset, final long length){
        try {
            return org.apache.commons.io.IOUtils.copyLarge(input, output, inputOffset, length);
        } catch (IOException e){
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 从InputStream复制大数据流（超过2GB）到OutputStream
     * 此方法使用内部缓冲，因此无需使用BufferedInputStream
     * 注意：此方法实现是使用{@link #skip(InputStream, long)}，这意味着该方法可能比使用实际的skip实现效率低很多，这样做是为了保证跳过正确数量的字符
     * @param input 要读取的InputStream
     * @param output 要写入的OutputStream
     * @param inputOffset 读取字节流时跳过的字节数量,-ve意味着忽略
     * @param length 要复制的字节数量，-ve代表全部
     * @param buffer 用于复制的缓冲区
     * @return 复制的字节流数量
     */
    public static long copyLarge(final InputStream input, final OutputStream output, final long inputOffset, final long length, final byte[] buffer){
        try {
            return org.apache.commons.io.IOUtils.copyLarge(input, output, inputOffset, length, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 从Reader复制大数据流（超过2GB）到Writer输出流
     * 此方法使用内部缓冲，因此无需使用BufferedReader
     * 默认的缓冲大小是{@link #DEFAULT_BUFFER_SIZE}
     * @param input 要读取的字符流Reader
     * @param output 要写入的字符流Writer
     * @return 复制的字符流数量
     */
    public static long copyLarge(final Reader input, final Writer output){
        try {
            return org.apache.commons.io.IOUtils.copyLarge(input, output);
        } catch (IOException e){
            e.printStackTrace();
            return 0L;
        }
    }
    /**
     * 从Reader复制大数据流（超过2GB）到Writer输出流
     * 此方法使用内部缓冲，因此无需使用BufferedReader
     * @param input 要读取的字符流Reader
     * @param output 要写入的字符流Writer
     * @param buffer 复制要使用的缓冲区大小
     * @return 复制的字符流数量
     */
    public static long copyLarge(final Reader input, final Writer output, final char[] buffer){
        try {
            return org.apache.commons.io.IOUtils.copyLarge(input, output, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return 0L;
        }
    }
    /**
     * 从Reader复制大数据流（超过2GB）到Writer输出流,可选择跳过输入字符的数量
     * 此方法使用内部缓冲，因此无需使用BufferedReader
     * 默认的缓冲大小是{@link #DEFAULT_BUFFER_SIZE}
     * @param input 要读取的字符流Reader
     * @param output 要写入的字符流Writer
     * @param inputOffset 复制要跳过的字符数量，-ve意味着忽略
     * @param length 复制字符的数量，-ve以为着所有
     * @return 复制的字符流数量
     */
    public static long copyLarge(final Reader input, final Writer output, final long inputOffset, final long length){
        try {
            return org.apache.commons.io.IOUtils.copyLarge(input, output, inputOffset, length);
        } catch (IOException e){
            e.printStackTrace();
            return 0L;
        }
    }
    /**
     * 从Reader复制大数据流（超过2GB）到Writer输出流,可选择跳过输入字符的数量
     * 此方法使用内部缓冲，因此无需使用BufferedReader
     * @param input 要读取的字符流Reader
     * @param output 要写入的字符流Writer
     * @param inputOffset 复制要跳过的字符数量，-ve意味着忽略
     * @param length 复制字符的数量，-ve以为着所有
     * @param buffer 复制要使用的缓冲区大小
     * @return 复制的字符流数量
     */
    public static long copyLarge(final Reader input, final Writer output, final long inputOffset, final long length, final char[] buffer){
        try {
            return org.apache.commons.io.IOUtils.copyLarge(input, output, inputOffset, length, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return 0L;
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
            return null;
        }
    }

    /**
     * 返回Reader的迭代器，LineIterator持有Reader指定的引用，当完成迭代器后应该释放持有的资源
     * 这可以通过关闭reader来直接完成，或者通过调用{@link LineIterator#close()}，或者{@link LineIterator#closeQuietly(LineIterator)}
     * 建议使用的模式是：
     * try {
     *   LineIterator it = IOUtils.lineIterator(reader);
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   IOUtils.closeQuietly(reader);
     * }
     * @param reader 要读取的Reader
     * @return LineIterator，永远不会为null
     */
    public static LineIterator lineIterator(final Reader reader){
        return org.apache.commons.io.IOUtils.lineIterator(reader);
    }

    /**
     * 返回Reader的迭代器，LineIterator持有Reader指定的引用，当完成迭代器后应该释放持有的资源,并使用指定的编码，编码为null使用默认编码
     * 这可以通过关闭reader来直接完成，或者通过调用{@link LineIterator#close()}，或者{@link LineIterator#closeQuietly(LineIterator)}
     * 建议使用的模式是：
     * try {
     *   LineIterator it = IOUtils.lineIterator(reader);
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   IOUtils.closeQuietly(reader);
     * }
     * @param reader 要读取的Reader
     * @param encoding 指定的编码
     * @return LineIterator，永远不会为null
     */
    public static LineIterator lineIterator(final InputStream input, final String encoding){
        try {
            return org.apache.commons.io.IOUtils.lineIterator(input, encoding);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从input中读取字符输入流
     * 此方法的实现保证在流关闭之前尽可能多的读取
     * @param input 要读取的字符流
     * @param buffer 存储读取到字符的字符数组
     * @return 实际的读取长度，如果到达EOF,可能小于请求的长度
     */
    public static int read(final Reader input, final char[] buffer){
        try {
            return org.apache.commons.io.IOUtils.read(input, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 从input中读取字符输入流,此方法的实现保证在流关闭之前尽可能多的读取
     * @param input 要读取的字符流
     * @param buffer 存储读取到字符的字符数组
     * @param offset 缓冲区的初始偏移量
     * @param length 读取的长度，必须>=0
     * @return 实际的读取长度，如果到达EOF,可能小于请求的长度
     */
    public static int read(final Reader input, final char[] buffer, final int offset, final int length){
        try {
            return org.apache.commons.io.IOUtils.read(input, buffer, offset, length);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 从input中读取字符输入流,此方法的实现保证在流关闭之前尽可能多的读取
     * @param input 要读取的字节流
     * @param buffer 存储读取到字节流的字节数组
     * @return 实际的读取长度，如果到达EOF,可能小于请求的长度
     */
    public static int read(final InputStream input, final byte[] buffer){
        try {
            return org.apache.commons.io.IOUtils.read(input, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 从input中读取字符输入流,此方法的实现保证在流关闭之前尽可能多的读取
     * @param input 要读取的字节流
     * @param buffer 存储读取到字节流的字节数组
     * @param offset 缓冲区的初始偏移量
     * @param length 读取的长度，必须>=0
     * @return 实际的读取长度，如果到达EOF,可能小于请求的长度
     */
    public static int read(final InputStream input, final byte[] buffer, final int offset, final int length){
        try {
            return org.apache.commons.io.IOUtils.read(input, buffer, offset, length);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 从input中读取字符输入流,此方法的实现保证在流关闭之前尽可能多的读取
     * @param input 要读取的字节通道
     * @param buffer  存储读取到字节流的字节包装类数组
     * @return 实际的读取长度，如果到达EOF,可能小于请求的长度
     */
    public static int read(final ReadableByteChannel input, final ByteBuffer buffer){
        try {
            return org.apache.commons.io.IOUtils.read(input, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 读取请求的字符数，如果没有足够的剩余字符，则读取失败
     * @param input 要读取的input字符流
     * @param buffer 从字符流中读取的字符数组
     * @param offset buffer偏移量
     * @param length 读取字符数据的长度
     * @return true 读取成功，false读取失败
     */
    public static boolean readFully(final Reader input, final char[] buffer, final int offset, final int length){
        try {
            org.apache.commons.io.IOUtils.readFully(input, buffer, offset, length);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 读取请求的字符数，如果没有足够的剩余字符，则读取失败
     * @param input 要读取的input字符流
     * @param buffer 从字符流中读取的字符数组
     * @return true 读取成功，false读取失败
     */
    public static boolean readFully(final Reader input, final char[] buffer){
        try {
            org.apache.commons.io.IOUtils.readFully(input, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取请求的字节数，如果剩余的字节数不够，则读取失败。
     * @param input 读取的input字节流
     * @param buffer 从input中读取的字节流存储的字节数组
     * @param offset 数组的偏移量
     * @param length 读取字节的长度
     * @return  true 读取成功，false读取失败
     */
    public static boolean readFully(final InputStream input, final byte[] buffer, final int offset, final int length){
        try {
            org.apache.commons.io.IOUtils.readFully(input, buffer, offset, length);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 读取请求的字节数，如果剩余的字节数不够，则读取失败。
     * @param input 读取的input字节流
     * @param buffer 从input中读取的字节流存储的字节数组
     * @return  true 读取成功，false读取失败
     */
    public static boolean readFully(final InputStream input, final byte[] buffer){
        try {
            org.apache.commons.io.IOUtils.readFully(input, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取请求的字节数，如果剩余的字节数不够，则读取失败。
     * @param input 读取的input字节流
     * @param length 读取字节的长度
     * @return 读取到的字节数组
     */
    public static byte[] readFully(final InputStream input, final int length){
        try {
            return org.apache.commons.io.IOUtils.readFully(input, length);
        } catch (IOException e){
            e.printStackTrace();
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }

    /**
     * 读取请求的字节数，如果剩余的字节数不够，则读取失败。
     * @param input 要读取的字节通道
     * @param buffer 读取到的字节存入buffer
     * @return true 读取成功，false读取失败
     */
    public static boolean readFully(final ReadableByteChannel input, final ByteBuffer buffer){
        try {
            org.apache.commons.io.IOUtils.readFully(input, buffer);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取Reader中的字符数据流，将其转换为List<String></>
     * 此方法实现了内部缓冲，因此无需BufferedReader
     * @param input 要读取的InputStream流
     * @param encoding 字符编码
     * @return List<String>不可以为null</>
     */
    public static List<String> readLines(final InputStream input, final String encoding){
        try {
            return org.apache.commons.io.IOUtils.readLines(input, encoding);
        } catch (IOException e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 读取Reader中的字符数据流，将其转换为List<String></>
     * 此方法实现了内部缓冲，因此无需BufferedReader
     * @param input 要读取的reader字符流，不可以为null
     * @return List<String> 不可以为null</>
     */
    public static List<String> readLines(final Reader input){
        try {
            return org.apache.commons.io.IOUtils.readLines(input);
        } catch (IOException e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 获取name指定的系统资源的字节数组
     * @param name 期望的资源
     * @return 资源的字节数组
     */
    public static byte[] resourceToByteArray(final String name){
        try {
            return org.apache.commons.io.IOUtils.resourceToByteArray(name);
        } catch (IOException e){
            e.printStackTrace();
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }

    /**
     * 获取name指定的系统资源的字节数组
     * 希望给定的name是一个绝对路径
     * @param name 期望的资源
     * @param classLoader 解析指定资源的类加载器
     * @return 资源的字节数组
     */
    public static byte[] resourceToByteArray(final String name, final ClassLoader classLoader){
        try {
            return org.apache.commons.io.IOUtils.resourceToByteArray(name, classLoader);
        } catch (IOException e){
            e.printStackTrace();
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }
    /**
     * 获取指定类路径的URL资源
     * @param name 资源地址
     * @return 资源对应的URL
     */
    public static URL resourceToURL(final String name){
        try {
            return org.apache.commons.io.IOUtils.resourceToURL(name);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取指定类路径的URL资源
     * @param name 资源地址
     * @param classLoader 加载资源的类加载器
     * @return 资源对应的URL
     */
    public static URL resourceToURL(final String name, final ClassLoader classLoader){
        try {
            return org.apache.commons.io.IOUtils.resourceToURL(name, classLoader);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 使用指定的字符编码以字符串形式获取类路径资源的内容
     * @param name 资源路径
     * @param encoding 编码
     * @return 请求资源的字符串
     */
    public static String resourceToString(final String name, final Charset encoding){
        try {
            return org.apache.commons.io.IOUtils.resourceToString(name, encoding);
        } catch (IOException e){
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    /**
     * 使用指定的字符编码以字符串形式获取类路径资源的内容
     * @param name 资源路径
     * @param encoding 编码
     * @param classLoader 加载资源的类加载器
     * @return 请求资源的字符串
     */
    public static String resourceToString(final String name, final Charset encoding, final ClassLoader classLoader){
        try {
            return org.apache.commons.io.IOUtils.resourceToString(name, encoding, classLoader);
        } catch (IOException e){
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }
    /**
     * 将字节数组中的字节写入到输出流中
     * @param data 字节数组，输出是不可修改，null忽略
     * @param output 输出流
     * @return true 写入成功，false 写入失败
     */
    public static boolean write(final byte[] data, final OutputStream output){
        try {
            org.apache.commons.io.IOUtils.write(data, output);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将字节数组data中的字节写入到Writer字符输出流中
     * @param data 要写入的字节数组
     * @param output 要输出的字符输出流
     * @param encoding 编码方式
     * @return true 写入成功，false 失败
     */
    public static boolean write(final byte[] data, final Writer output, final String encoding){
        try {
            org.apache.commons.io.IOUtils.write(data, output, encoding);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 将字符数组中的字符写入到输出流中,使用指定的编码
     * @param data 字符数组，输出是不可修改，null忽略
     * @param output 输出流
     * @param encoding 编码
     * @return true 写入成功，false 写入失败
     */
    public static boolean write(final char[] data, final OutputStream output, final String encoding){
        try {
            org.apache.commons.io.IOUtils.write(data, output, encoding);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将字符数组data中的字符写入到output中
     * @param data 字符数组
     * @param output 字符输出流
     * @return  true 写入成功，false 写入失败
     */
    public static boolean write(final char[] data, final Writer output){
        try {
            org.apache.commons.io.IOUtils.write(data, output);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将CharSequence中的字符char写入到Writer输出流
     * @param data 要写入的CharSequence
     * @param output 输出流
     * @return  true 写入成功，false 写入失败
     */
    public static boolean write(final CharSequence data, final Writer output){
        try {
            org.apache.commons.io.IOUtils.write(data, output);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将CharSequence中的字符char写入到Writer输出流
     * @param data 要写入的CharSequence
     * @param output 输出流
     * @param encoding 编码
     * @return  true 写入成功，false 写入失败
     */
    public static boolean write(final CharSequence data, final OutputStream output, final String encoding){
        try {
            org.apache.commons.io.IOUtils.write(data, output, encoding);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从字符串中读取char写入到Writer
     * @param data 要写入的字符串
     * @param output 要写入的Writer
     * @return  true 写入成功，false 写入失败
     */
    public static boolean write(final String data, final Writer output){
        try {
            org.apache.commons.io.IOUtils.write(data, output);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 从字符串中读取char写入到Writer
     * @param data 要写入的字符串
     * @param output 要写入的Writer
     * @param encoding 编码
     * @return  true 写入成功，false 写入失败
     */
    public static boolean write(final String data, final OutputStream output, final String encoding){
        try {
            org.apache.commons.io.IOUtils.write(data, output, encoding);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将字节数组data写入到output输出流，使用分块写入方式
     * @param data 要写入的字节数组
     * @param output 输出流
     * @return  true 写入成功，false 写入失败
     */
    public static boolean writeChunked(final byte[] data, final OutputStream output){
        try {
            org.apache.commons.io.IOUtils.writeChunked(data, output);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将字符数组data写入到Writer输出流，使用分块写入方式
     * @param data 要写入的字符数组
     * @param output 输出字符流
     * @return  true 写入成功，false 写入失败
     */
    public static boolean writeChunked(final char[] data, final Writer output){
        try {
            org.apache.commons.io.IOUtils.writeChunked(data, output);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将lines的每一行转换成字符串写入到输出流中，使用指定的编码、指定的行尾结束符
     * @param lines 要写入的每一行集合，null会产生空白行
     * @param lineEnding 要使用的行分隔符，null使用系统默认
     * @param output 输出字节流
     * @param encoding 编码
     * @return  true 写入成功，false 写入失败
     */
    public static boolean writeLines(final Collection<?> lines, final String lineEnding, final OutputStream output, final String encoding){
        try {
            org.apache.commons.io.IOUtils.writeLines(lines, lineEnding, output, encoding);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *  将lines的每一行转换成字符串写入到输出流中,使用指定的行尾结束符
     * @param lines 要写入的每一行集合，null会产生空白行
     * @param lineEnding 要使用的行分隔符，null使用系统默认
     * @param writer 输出字符流
     * @return  true 写入成功，false 写入失败
     */
    public static boolean writeLines(final Collection<?> lines, String lineEnding, final Writer writer){
        try {
            org.apache.commons.io.IOUtils.writeLines(lines, lineEnding, writer);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
