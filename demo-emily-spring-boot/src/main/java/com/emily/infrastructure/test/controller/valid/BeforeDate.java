package com.emily.infrastructure.test.controller.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsBeforeEndDate;

import java.time.LocalDate;

/**
 * @author :  Emily
 * @since :  2023/12/28 7:44 PM
 */
@IsBeforeEndDate(startField = "startDate", endField = "endDate", message = "日期大小不符合要求", inclusive = false)
public class BeforeDate {
    private LocalDate startDate;

    private LocalDate endDate;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
