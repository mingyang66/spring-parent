### springboot2.x+quartz+mysql持久化集成

##### 1.pom文件中引入相关依赖

```java
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.21</version>
        </dependency>
```

##### 2.新增yml配置文件如下

```java
#属性配置文档 https://github.com/quartz-scheduler/quartz/blob/master/docs/configuration.adoc
spring:
  #配置数据源
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/emily?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
    username: root
    password: smallgrain
  quartz:
    #持久化到数据库方式
    job-store-type: jdbc
    #quartz调度程序属性
    properties:
      org:
        quartz:
          scheduler:
            #调度任务实例名称，如果是集群则每个实例必须是相同的名字
            instanceName: SmallEmilyScheduler
            #实例ID，对于集群中工作的所有调度器必须是唯一的，如果值是AUTO则会自动生成，如果希望值来自系统属性则设置为SYS_PROP
            instanceId: AUTO
          jobStore:
            #job、traggers、calendars持久化实现类，默认：org.quartz.simpl.RAMJobStore
            class: org.quartz.impl.jdbcjobstore.JobStoreTX
            #调度程序下次触发时间的毫秒数，默认是60000（60 seconds）
            misfireThreshold: 60000
            #驱动程序代理类
            driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
            #表名前缀，默认：QRTZ_
            tablePrefix: QUATRZ_
            #默认：false，设置JDBCJobStore的JobDataMaps中存储的是字符串类型的key-value,否则为 true
            useProperties: false
            #设置为true以启用集群功能，如果Quartz的多个实例使用同一组数据库表，则必须将此属性设置为true,否则将遇到严重的破话，默认：false
            isClustered: true
            #设置此实例与集群的其它实例"checks-in"的频率（毫秒），影响实例的检测失败速率，默认：15000
            clusterCheckinInterval: 10000
          #配置线程池
          threadPool:
            #要使用的线程池实心名称，与Quartz自带的线程池应该可以满足几乎每个用户的需求，它的行为非常简单，而且已经过很好的测试，它提供了一个固定大小的线程池，这些线程在调度程序的生存期内"生存"
            class: org.quartz.simpl.SimpleThreadPool
            #线程数
            threadCount: 10
            #线程优先级，可以是Thread.MIN_PRIORITY（1）和Thread.MAX_PRIORITY（10）之间的数据，默认是：Thread.NORM_PRIORITY (5)
            threadPriority: 5
            #可以设置为true以将线程池中的线程创建为守护程序线程。默认：false
            makeThreadsDaemons: false
            #线程池中线程名的前缀,默认：MyScheduler_Worker
            threadNamePrefix: MyScheduler_Worker
            #默认true
            threadsInheritGroupOfInitializingThread: true
            #默认true
            threadsInheritContextClassLoaderOfInitializingThread: true
    jdbc:
      initialize-schema: always
      #初始化数据库脚本路径，默认使用classpath:org/quartz/impl/jdbcjobstore/tables_@@platform@@.sql路径下的脚本
      schema: classpath:tables_mysql.sql


```

上述quartz集成到springboot的配置注解已经写的很清楚了，现在就对schema配置的sql脚本做一个解释，默认情况下jar包中自带的有对应的各种数据库的脚本，不需要配置根据数据源的不同自动选择脚本进行初始化，如果需要更改表的前缀名称那么可以将其拷贝出来放到classpath下；脚本在jar包中的位置注释中已经写了，可以自己查看；

##### 3.定义一个Job任务

```java
import com.emily.boot.common.enums.DateFormatEnum;
import com.emily.boot.common.utils.date.DateUtils;
import com.emily.boot.common.utils.json.JSONUtils;
import com.emily.boot.context.httpclient.HttpClientService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/28
 */
public class ThreadPoolJob extends QuartzJobBean {
    @Autowired
    private HttpClientService httpClientService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Trigger trigger = context.getTrigger();
        String valeu = context.getTrigger().getJobDataMap().getString("tragger_param");
        String jobValue = context.getJobDetail().getJobDataMap().getString("jobDataKey");
        System.out.println(JSONUtils.toJSONPrettyString(context.getMergedJobDataMap()));
        System.out.println(Thread.currentThread().getName() + "--执行作业调度--" + DateUtils.formatDate(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getPattern())+"--"+valeu+"--"+jobValue);
    }
}

```

##### 4.定义JobDetail和Trigger

```java
package com.emily.boot.quartz.factory;

import com.emily.boot.quartz.job.ThreadPoolJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/28
 */
@Configuration(proxyBeanMethods = false)
public class QuartzConfig {

    @Bean
    public JobDetail jobDetail(){
        JobDetail jobDetail = JobBuilder.newJob(ThreadPoolJob.class)
                                        //是否持久化
                                        .storeDurably(true)
                                        .withDescription("Job任务描述")
                                        //任务名称和任务分组 组合成任务唯一标识
                                        .withIdentity(JobKey.jobKey("JobName", "JobGroup"))
                                        .usingJobData("jobDataKey", "jobDataValue")
                                        .build();
        return jobDetail;
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail){
        Trigger trigger = TriggerBuilder.newTrigger() //创建一个定义或者构建触发器的builder实例
                                .forJob(jobDetail) //通过从给定的作业中提取出jobKey,设置由生成的触发器触发的作业的标识
                                //自定义触发器描述
                                .withDescription("自定义触发器描述")
                                //设置触发器名称、触发器分组，组合为触发器唯一标识
                                .withIdentity(TriggerKey.triggerKey("traggerName", "traggerGroup"))
                                //如果有一个Job任务供多个触发器调度，而每个触发器调度传递不同的参数，此时JobDataMap可以提供不同的数据输入
                                //在任务执行时JobExecutionContext提供不同的JobDataMap参数给Job
                                .usingJobData("tragger_param", "tragger_value")
                                //触发器优先级
                                .withPriority(6)
                                //设置用于定义触发器的{@link org.quartz.ScheduleBuilder}配置计划
                                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ? "))
                                //将触发器的启动时间设置为当前时刻，触发器可能此时触发，也可能不触发，这取决于为触发器配置的计划
                                .startNow()
                                //构造触发器
                                .build();

        return trigger;
    }
}

```

##### 5.JobDataMap

在上述示例中对Trigger和JobDetials都有对JobDataMap属性的设置usingJobData，那么usingJobData的作用是什么呢？其实我们看下其源码的注释就会很清楚了：

```java
/**
 * Holds state information for <code>Job</code> instances.
 * 
 * <p>
 * <code>JobDataMap</code> instances are stored once when the <code>Job</code>
 * is added to a scheduler. They are also re-persisted after every execution of
 * jobs annotated with <code>@PersistJobDataAfterExecution</code>.
 * </p>
 * 
 * <p>
 * <code>JobDataMap</code> instances can also be stored with a 
 * <code>Trigger</code>.  This can be useful in the case where you have a Job
 * that is stored in the scheduler for regular/repeated use by multiple 
 * Triggers, yet with each independent triggering, you want to supply the
 * Job with different data inputs.  
 * </p>
 * 
 * <p>
 * The <code>JobExecutionContext</code> passed to a Job at execution time 
 * also contains a convenience <code>JobDataMap</code> that is the result
 * of merging the contents of the trigger's JobDataMap (if any) over the
 * Job's JobDataMap (if any).  
 * </p>
 *
 * <p>
 * Update since 2.2.4 - We keep an dirty flag for this map so that whenever you modify(add/delete) any of the entries,
 * it will set to "true". However if you create new instance using an exising map with {@link #JobDataMap(Map)}, then
 * the dirty flag will NOT be set to "true" until you modify the instance.
 * </p>
 * 
 * @see Job
 * @see PersistJobDataAfterExecution
 * @see Trigger
 * @see JobExecutionContext
 * 
 * @author James House
 */
```

解释一下首先是存储Job任务实例的信息，当Job任务添加到调度任务时JobDataMap会被持久化存储；在Job类上添加@PersistJobDataAfterExecution注解在任务执行完成后也会重新持久化；

JobDataMap实例也可以通过Trigger一起存储，当有一个Job被多个触发器调用时，每个触发器可以传递不同的参数给Job任务；

Job任务可以通过JobExecutionContext接收到JobDataMap中存储的数据，可以通过getMergedJobDataMap合并的方法获取到存储在JobDetails和Trigger中的数据，也可以分别获取存储在Trigger和JobDetails中的数据。

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-quartz](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-quartz)