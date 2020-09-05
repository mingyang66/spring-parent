### springboot quartz任务信息持久化storeDurably详解

在将JobDetails信息持久化到数据库的时候有一个属性storeDurably，如果设置为true则无论与其关联的Trigger是否存在其都会一直存在，否则只要相关联的trigger删除掉了其会自动删除掉；

##### 1.看下源码注解

```
   /**
     * Whether or not the <code>Job</code> should remain stored after it is
     * orphaned (no <code>{@link Trigger}s</code> point to it).
     * 
     * <p>
     * If not explicitly set, the default value is <code>false</code>.
     * </p>
     * 
     * @param jobDurability the value to set for the durability property.
     * @return the updated JobBuilder
     * @see JobDetail#isDurable()
     */
    public JobBuilder storeDurably(boolean jobDurability) {
        this.durability = jobDurability;
        return this;
    }
```

解释下大概意思是在没有Trigger指向Job的时候是否还需要继续存储，默认是false;也就是在没有Trigger指向Job的时候会被删除掉；

##### 2.看下新增调度任务及触发器的方法

```
    @Override
    public String scheduleJob(AddQuartzEntity addQuartzEntity) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(CronJob.class)
                    .withDescription(addQuartzEntity.getDescription())
                    //任务名称和任务分组 组合成任务唯一标识
                    .withIdentity(JobKey.jobKey(addQuartzEntity.getTaskName(), addQuartzEntity.getTaskGroup()))
                    //无触发器（Trigger）指向时是否需要持久化哦或删除
                    .storeDurably(false)
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    //作业优先级
                    .withPriority(5)
                    .withDescription(addQuartzEntity.getDescription())
                    //设置触发器名称、触发器分组，组合为触发器唯一标识
                    .withIdentity(TriggerKey.triggerKey(addQuartzEntity.getTaskName(), addQuartzEntity.getTaskGroup()))
                    //设置用于定义触发器的{@link org.quartz.ScheduleBuilder}配置计划 "0/10 * * * * ? "
                    //.withSchedule(CronScheduleBuilder.cronSchedule(addQuartzEntity.getCron()))
                    .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever())
                    //通过从给定的作业中提取出jobKey,设置由生成的触发器触发的作业的标识
                    .forJob(jobDetail)
                    .startNow()
                    .build();
            //返回第一次任务执行时间
            Date date = scheduler.scheduleJob(jobDetail, trigger);
            return DateFormatUtils.format(date, DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat());
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "新增Task任务异常" + e.getMessage());
        }
    }
```

上述代码storeDurably我设置为false,这样无论CronScheduleBuilder或者SimpleScheduleBuilder当删除Trigger的时候都会级联删除JobDetails;如果设置为true，则Trigger删除的时候JobDetails任然存在，在数据库表QUARTZ_JOB_DETAILS中的IS_DURABLE字段存储；

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-quartz](https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-quartz)