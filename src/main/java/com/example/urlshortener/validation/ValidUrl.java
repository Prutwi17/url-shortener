package com.example.urlshortener.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUrl {
    String message() default "Invalid URL format. Must be a valid HTTP or HTTPS URL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
