package com.emily.infrastructure.test.plugin.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author :  Emily
 * @since :  2023/12/28 7:43 PM
 */
public class EndDateAfterStartDateValidator implements ConstraintValidator<EndDateAfterStartDate, TaskForm> {
    @Override
    public boolean isValid(TaskForm taskForm, ConstraintValidatorContext context) {
        if (taskForm.getStartDate() == null || taskForm.getEndDate() == null) {
            return true;
        }

        return taskForm.getEndDate().isAfter(taskForm.getStartDate());
    }
}
