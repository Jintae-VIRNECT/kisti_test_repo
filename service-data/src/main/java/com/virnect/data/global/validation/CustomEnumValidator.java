package com.virnect.data.global.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomEnumValidator implements ConstraintValidator<CustomEnumValid, Object> {

   private CustomEnumValid annotation;

   @Override
   public void initialize(CustomEnumValid constraintAnnotation) {
      annotation = constraintAnnotation;
   }

   @Override
   public boolean isValid(Object value, ConstraintValidatorContext context) {
      boolean result = false;
      Object[] enumValues = annotation.enumClass().getEnumConstants();

      if (enumValues != null) {
         for (Object enumValue : enumValues) {
            if (value == enumValue) {
               result = true;
               break;
            }
         }
      }
      return result;
   }
}