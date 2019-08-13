### Java获取类加载项目根路径

```
package com.yaomy.common.path;

import java.io.File;
import java.io.IOException;


public class PathTest {
    public static void main(String[] args) throws IOException {
        new PathTest().getUrl();

    }
    public void getUrl()  throws IOException {
        //获取项目根路径 D:\work\workplace\test\spring-parent
        File file = new File("");
        System.out.println(file.getCanonicalPath());

        //获取项目根路径 D:\work\workplace\test\spring-parent
        String path = System.getProperty("user.dir");
        System.out.println(path);

        //加载类的根路径：D:\work\workplace\test\spring-parent\spring-common-service\target\classes\com\yaomy\common\path\%5c
        File file2 = new File(this.getClass().getResource(File.separator).getPath());
        System.out.println(file2.getCanonicalPath());

        //获取当前类的加载目录 D:\work\workplace\test\spring-parent\spring-common-service\target\classes\com\yaomy\common\path
        File f2 = new File(this.getClass().getResource("").getPath());
        System.out.println(f2);
    }
}

```

GitHub源码：[https://github.com/mingyang66/spring-parent/blob/master/spring-common-service/path.md](https://github.com/mingyang66/spring-parent/blob/master/spring-common-service/path.md)