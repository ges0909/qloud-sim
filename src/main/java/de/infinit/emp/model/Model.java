package de.infinit.emp.model;

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

public class Model<T, U> {
	static final Logger log = Logger.getLogger(Model.class.getName());
	static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	ConnectionSource cs = null;
	Dao<T, U> dao = null;

	public Model(Class<T> bean) {
		try {
			cs = Database.getConnectionSource();
			dao = DaoManager.createDao(cs, bean);
			TableUtils.createTableIfNotExists(cs, bean);
		} catch (SQLException e) {
			log.severe(log.toString());
		}
	}

	public boolean isValid(T bean) {
		Set<ConstraintViolation<T>> violations = validator.validate(bean);
		if (violations.isEmpty())
			return true;
		for (ConstraintViolation<T> v : violations) {
			log.warning("constraint violation: " 
					+ v.getPropertyPath().toString() + "=" 
					+ v.getInvalidValue() + ": "
					+ v.getMessage());
		}
		return false;
	}

	public T create(Dao<T, U> dao, T bean) {
		if (!isValid(bean)) {
			return null;
		}
		try {
			if (dao.create(bean) == 1) {
				return bean;
			}
		} catch (SQLException e) {
			log.severe(e.toString());
		}
		return null;
	}

	public T update(Dao<T, U> dao, T bean) {
		if (!isValid(bean)) {
			return null;
		}
		try {
			if (dao.update(bean) == 1) {
				return bean;
			}
		} catch (SQLException e) {
			log.severe(e.toString());
		}
		return null;
	}
	
	public int delete(Dao<T, U> dao, U id) {
		try {
			return dao.deleteById(id);
		} catch (SQLException e) {
			log.severe(e.toString());
		}
		return 0;
	}
	
	public T queryForId(Dao<T, U> dao, U id) {
		try {
			return dao.queryForId(id);
		} catch (SQLException e) {
			log.severe(e.toString());
		}
		return null;
	}

	public List<T> queryForAll(Dao<T, U> dao) {
		try {
			return dao.queryForAll();
		} catch (SQLException e) {
			log.severe(e.toString());
		}
		return new ArrayList<>();
	}
	
	public <V> T findByColumn(Dao<T, U> dao, String column, V value) {
		try {
			return dao.queryBuilder()
					.where()
					.eq(column, value)
					.queryForFirst();
		} catch (SQLException e) {
			log.severe(e.toString());
		}
		return null;
	}
}
