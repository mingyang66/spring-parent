### Spring-boot2.0学习笔记：代码中读取配置文件（properties）中的中文乱码bug

解决方案有两种：

1. 使用yml配置文件代替properties文件
2. 重构org.springframework.boot.env.OriginTrackedPropertiesLoader类

第一种方案就不用多说了，很简单，主要讲解第二种方案；

### properties配置文件中的中文读取出来是乱码

> 原因是org.springframework.boot.env.OriginTrackedPropertiesLoader文件加载配置文件时编码格式硬编码为了ISO-8859-1

```java
        CharacterReader(Resource resource) throws IOException {
            this.reader = new LineNumberReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.ISO_8859_1));
        }
```

接下来要做的就是重构org.springframework.boot.env.OriginTrackedPropertiesLoader类、org.springframework.boot.env.PropertiesPropertySourceLoader类，PropertiesPropertySourceLoader类中调用OriginTrackedPropertiesLoader类，并将编码格式更改为UTF-8;

代码如下：

```
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yaomy.control.conf;

import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.boot.origin.TextResourceOrigin;
import org.springframework.boot.origin.TextResourceOrigin.Location;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

class OriginTrackedPropertiesLoader {
    private final Resource resource;

    OriginTrackedPropertiesLoader(Resource resource) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
    }

    public Map<String, OriginTrackedValue> load() throws IOException {
        return this.load(true);
    }

    public Map<String, OriginTrackedValue> load(boolean expandLists) throws IOException {
        OriginTrackedPropertiesLoader.CharacterReader reader = new OriginTrackedPropertiesLoader.CharacterReader(this.resource);
        Throwable var3 = null;

        try {
            Map<String, OriginTrackedValue> result = new LinkedHashMap();
            StringBuilder buffer = new StringBuilder();

            while(reader.read()) {
                String key = this.loadKey(buffer, reader).trim();
                if (expandLists && key.endsWith("[]")) {
                    key = key.substring(0, key.length() - 2);
                    int var19 = 0;

                    while(true) {
                        OriginTrackedValue value = this.loadValue(buffer, reader, true);
                        this.put(result, key + "[" + var19++ + "]", value);
                        if (!reader.isEndOfLine()) {
                            reader.read();
                        }

                        if (reader.isEndOfLine()) {
                            break;
                        }
                    }
                } else {
                    OriginTrackedValue value = this.loadValue(buffer, reader, false);
                    this.put(result, key, value);
                }
            }

            Map var18 = result;
            return var18;
        } catch (Throwable var16) {
            var3 = var16;
            throw var16;
        } finally {
            if (reader != null) {
                if (var3 != null) {
                    try {
                        reader.close();
                    } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                    }
                } else {
                    reader.close();
                }
            }

        }
    }

    private void put(Map<String, OriginTrackedValue> result, String key, OriginTrackedValue value) {
        if (!key.isEmpty()) {
            result.put(key, value);
        }

    }

    private String loadKey(StringBuilder buffer, OriginTrackedPropertiesLoader.CharacterReader reader) throws IOException {
        buffer.setLength(0);
        boolean previousWhitespace = false;

        while(!reader.isEndOfLine()) {
            if (reader.isPropertyDelimiter()) {
                reader.read();
                return buffer.toString();
            }

            if (!reader.isWhiteSpace() && previousWhitespace) {
                return buffer.toString();
            }

            previousWhitespace = reader.isWhiteSpace();
            buffer.append(reader.getCharacter());
            reader.read();
        }

        return buffer.toString();
    }

    private OriginTrackedValue loadValue(StringBuilder buffer, OriginTrackedPropertiesLoader.CharacterReader reader, boolean splitLists) throws IOException {
        buffer.setLength(0);

        while(reader.isWhiteSpace() && !reader.isEndOfLine()) {
            reader.read();
        }

        Location location = reader.getLocation();

        while(!reader.isEndOfLine() && (!splitLists || !reader.isListDelimiter())) {
            buffer.append(reader.getCharacter());
            reader.read();
        }

        Origin origin = new TextResourceOrigin(this.resource, location);
        return OriginTrackedValue.of(buffer.toString(), origin);
    }

    private static class CharacterReader implements Closeable {
        private static final String[] ESCAPES = new String[]{"trnf", "\t\r\n\f"};
        private final LineNumberReader reader;
        private int columnNumber = -1;
        private boolean escaped;
        private int character;

        CharacterReader(Resource resource) throws IOException {
            this.reader = new LineNumberReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        }
        @Override
        public void close() throws IOException {
            this.reader.close();
        }

        public boolean read() throws IOException {
            return this.read(false);
        }

        public boolean read(boolean wrappedLine) throws IOException {
            this.escaped = false;
            this.character = this.reader.read();
            ++this.columnNumber;
            if (this.columnNumber == 0) {
                this.skipLeadingWhitespace();
                if (!wrappedLine) {
                    this.skipComment();
                }
            }

            if (this.character == 92) {
                this.escaped = true;
                this.readEscaped();
            } else if (this.character == 10) {
                this.columnNumber = -1;
            }

            return !this.isEndOfFile();
        }

        private void skipLeadingWhitespace() throws IOException {
            while(this.isWhiteSpace()) {
                this.character = this.reader.read();
                ++this.columnNumber;
            }

        }

        private void skipComment() throws IOException {
            if (this.character == 35 || this.character == 33) {
                while(true) {
                    if (this.character == 10 || this.character == -1) {
                        this.columnNumber = -1;
                        this.read();
                        break;
                    }

                    this.character = this.reader.read();
                }
            }

        }

        private void readEscaped() throws IOException {
            this.character = this.reader.read();
            int escapeIndex = ESCAPES[0].indexOf(this.character);
            if (escapeIndex != -1) {
                this.character = ESCAPES[1].charAt(escapeIndex);
            } else if (this.character == 10) {
                this.columnNumber = -1;
                this.read(true);
            } else if (this.character == 117) {
                this.readUnicode();
            }

        }

        private void readUnicode() throws IOException {
            this.character = 0;

            for(int i = 0; i < 4; ++i) {
                int digit = this.reader.read();
                if (digit >= 48 && digit <= 57) {
                    this.character = (this.character << 4) + digit - 48;
                } else if (digit >= 97 && digit <= 102) {
                    this.character = (this.character << 4) + digit - 97 + 10;
                } else {
                    if (digit < 65 || digit > 70) {
                        throw new IllegalStateException("Malformed \\uxxxx encoding.");
                    }

                    this.character = (this.character << 4) + digit - 65 + 10;
                }
            }

        }

        public boolean isWhiteSpace() {
            return !this.escaped && (this.character == 32 || this.character == 9 || this.character == 12);
        }

        public boolean isEndOfFile() {
            return this.character == -1;
        }

        public boolean isEndOfLine() {
            return this.character == -1 || !this.escaped && this.character == 10;
        }

        public boolean isListDelimiter() {
            return !this.escaped && this.character == 44;
        }

        public boolean isPropertyDelimiter() {
            return !this.escaped && (this.character == 61 || this.character == 58);
        }

        public char getCharacter() {
            return (char)this.character;
        }

        public Location getLocation() {
            return new Location(this.reader.getLineNumber(), this.columnNumber);
        }
    }
}

```

代码PropertiesPropertySourceLoader如下：

```
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yaomy.control.conf;

import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Description 重写PropertiesPropertySourceLoader类，系统内部的PropertiesPropertySourceLoader类的优先级是2147483647，
 * 设置@Order注解，值是2147483646，优先级比系统自带的高
 * @Date 2019/11/28 19:03
 * @Version  1.0
 */
@Order(2147483646)
public class PropertiesPropertySourceLoader implements PropertySourceLoader {
    private static final String XML_FILE_EXTENSION = ".xml";

    public PropertiesPropertySourceLoader() {
    }
    @Override
    public String[] getFileExtensions() {
        return new String[]{"properties", "xml"};
    }
    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        Map<String, ?> properties = this.loadProperties(resource);
        return properties.isEmpty() ? Collections.emptyList() : Collections.singletonList(new OriginTrackedMapPropertySource(name, properties));
    }

    private Map<String, ?> loadProperties(Resource resource) throws IOException {
        String filename = resource.getFilename();
        return (Map)(filename != null && filename.endsWith(".xml") ? PropertiesLoaderUtils.loadProperties(resource) : (new OriginTrackedPropertiesLoader(resource)).load());
    }
}

```

在resources目录下新建META-INF/spring.factories文件，并添加如下配置：

```java
org.springframework.boot.env.PropertySourceLoader=PropertiesPropertySourceLoader
```

>
两个类在同一个包下，并且PropertiesPropertySourceLoader类设置了@Order注解；原因是默认情况下系统自带的org.springframework.boot.env.PropertiesPropertySourceLoader类的优先级是2147483647，如果重构了类和项目在同一个项目的情况下，系统会加载的顺序是先加载自定义重构类再加载系统自带，这样在读取properties配置文件时优先使用自定义类；而如果是将自定义的类作为一个公用的jar包引入到系统中，加载顺序就会相反，所以需要使用@Order注解将优先级设置为高于系统自带的类；


GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/doc/profiles](https://github.com/mingyang66/spring-parent/tree/master/doc/profiles)
