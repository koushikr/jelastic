package io.github.jelastic.core.utils;

import io.github.jelastic.core.exception.InvalidRequestException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationUtil {

  private ValidationUtil() {
  }

  public static <T> void validateRequest(T request) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<T>> errors = validator.validate(request);

    if (!errors.isEmpty()) {
      log.error("Error in validating request {} Error : {}", request, errors);
      throw new InvalidRequestException("Invalid Request");
    }
  }
}

