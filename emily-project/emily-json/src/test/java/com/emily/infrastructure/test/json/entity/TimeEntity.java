package com.emily.infrastructure.test.json.entity;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author :  Emily
 * @since :  2025/11/28 下午2:27
 */
public class TimeEntity {
    private LocalDateTime localDateTime;
    private Date date;
    private Calendar calendar;

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
