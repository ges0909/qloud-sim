package de.infinit.emp.service;

import java.util.Set;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public class Service {
	static final Logger LOG = Logger.getLogger(SensorService.class.getName());
	static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public <T> boolean isValid(T bean) {
		Set<ConstraintViolation<T>> violations = validator.validate(bean);
		if (violations.isEmpty())
			return true;
		for (ConstraintViolation<T> v : violations) {
			LOG.warning("constraint violation: " + v.getPropertyPath().toString() + "=" + v.getInvalidValue() + ": "
					+ v.getMessage());
		}
		return false;
	}
}
