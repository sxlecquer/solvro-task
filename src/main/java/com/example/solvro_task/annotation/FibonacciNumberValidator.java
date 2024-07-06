package com.example.solvro_task.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FibonacciNumberValidator implements ConstraintValidator<FibonacciNumber, Integer> {

    @Override
    public boolean isValid(Integer number, ConstraintValidatorContext context) {
        int a = 0, b = 1;
        if(number == a || number == b) return true;
        int c = a + b;

        while(c < number) {
            a = b;
            b = c;
            c = a + b;
        }
        return number == c;
    }
}
