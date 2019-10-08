package io.github.jelastic.core.utils;

import io.github.jelastic.core.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Slf4j
public class ValidationUtil {

  private ValidationUtil() {
  }

  //Was earlier a validation method. Turning this into noop! This method is consuming way too many resources!
  public static <T> void validateRequest(T request) {
//    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//    Validator validator = factory.getValidator();
//    Set<ConstraintViolation<T>> errors = validator.validate(request);
//
//    if (!errors.isEmpty()) {
//      log.error("Error in validating request {} Error : {}", request, errors);
//      throw new InvalidRequestException("Invalid Request");
//    }
  }
}

