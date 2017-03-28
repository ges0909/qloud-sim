package de.infinit.emp.service;

import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.j256.ormlite.dao.Dao;

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

	public <T, U> T create(Dao<T, U> dao, T bean) {
		if (!isValid(bean)) {
			return null;
		}
		try {
			if (dao.create(bean) == 1) {
				return bean;
			}
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}

	public <T, U> T update(Dao<T, U> dao, T bean) {
		if (!isValid(bean)) {
			return null;
		}
		try {
			if (dao.update(bean) == 1) {
				return bean;
			}
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}

	public <T, U> T queryForId(Dao<T, U> dao, U id) {
		try {
			return dao.queryForId(id);
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}

	public <T, U> int deleteByUuid(Dao<T, U> dao, U id) {
		try {
			return dao.deleteById(id);
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return 0;
	}
}
