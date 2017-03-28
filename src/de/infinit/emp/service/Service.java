package de.infinit.emp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class Service<T, U> {
	static final Logger LOG = Logger.getLogger(SensorService.class.getName());
	static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	final ConnectionSource connectionSource;
	Dao<T, U> dao;

	public Service(Class<T> bean) throws IOException, SQLException {
		connectionSource = Database.getConnectionSource();
		dao = DaoManager.createDao(connectionSource, bean);
		TableUtils.createTableIfNotExists(connectionSource, bean);
	}

	public boolean isBeanValid(T bean) {
		Set<ConstraintViolation<T>> violations = validator.validate(bean);
		if (violations.isEmpty())
			return true;
		for (ConstraintViolation<T> v : violations) {
			LOG.warning("constraint violation: " + v.getPropertyPath().toString() + "=" + v.getInvalidValue() + ": "
					+ v.getMessage());
		}
		return false;
	}

	public T create(Dao<T, U> dao, T bean) {
		if (!isBeanValid(bean)) {
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

	public T update(Dao<T, U> dao, T bean) {
		if (!isBeanValid(bean)) {
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

	public T queryForId(Dao<T, U> dao, U id) {
		try {
			return dao.queryForId(id);
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}

	public List<T> queryForAll(Dao<T, U> dao) {
		try {
			return dao.queryForAll();
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return new ArrayList<>();
	}

	public int deleteByUuid(Dao<T, U> dao, U id) {
		try {
			return dao.deleteById(id);
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return 0;
	}
}
