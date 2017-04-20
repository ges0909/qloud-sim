package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.Tag;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.TagSensor;

public class TagSensorModel extends Model<TagSensor, Integer> {
	private static TagSensorModel instance = null;
	private static TagModel tagModel = TagModel.instance();
	private static SensorModel sensorModel = SensorModel.instance();
	private PreparedQuery<Tag> tagsForSensorQuery = null;
	private PreparedQuery<Sensor> sensorsForTagQuery = null;

	public TagSensorModel() {
		super(TagSensor.class);
	}

	public static TagSensorModel instance() {
		if (instance == null) {
			instance = new TagSensorModel();
		}
		return instance;
	}

	private PreparedQuery<Tag> makeTagsForSensorQuery() throws SQLException {
		// build our inner query for TagSensor objects
		QueryBuilder<TagSensor, Integer> tagSensorQb = dao.queryBuilder();
		tagSensorQb.selectColumns("tag_id");
		SelectArg sensorSelectArg = new SelectArg();
		tagSensorQb.where().eq("sensor_id", sensorSelectArg);
		// build our outer query for Tag objects
		QueryBuilder<Tag, UUID> tagQb = tagModel.dao.queryBuilder();
		tagQb.where().in("uuid", tagSensorQb);
		return tagQb.prepare();
	}

	private PreparedQuery<Sensor> makeSensorsForTagQuery() throws SQLException {
		// build our inner query for TagSensor objects
		QueryBuilder<TagSensor, Integer> tagSensorQb = dao.queryBuilder();
		tagSensorQb.selectColumns("sensor_id");
		SelectArg sensorSelectArg = new SelectArg();
		tagSensorQb.where().eq("tag_id", sensorSelectArg);
		// build our outer query for Tag objects
		QueryBuilder<Sensor, UUID> sensorQb = sensorModel.dao.queryBuilder();
		sensorQb.where().in("uuid", tagSensorQb);
		return sensorQb.prepare();
	}

	public List<Tag> findTagsBySensor(Sensor sensor) throws SQLException {
		if (tagsForSensorQuery == null) {
			tagsForSensorQuery = makeTagsForSensorQuery();
		}
		tagsForSensorQuery.setArgumentHolderValue(0, sensor);
		return tagModel.dao.query(tagsForSensorQuery);
	}

	public List<Sensor> findSensorsByTag(Tag tag) throws SQLException {
		if (sensorsForTagQuery == null) {
			sensorsForTagQuery = makeSensorsForTagQuery();
		}
		sensorsForTagQuery.setArgumentHolderValue(0, tag);
		return sensorModel.dao.query(sensorsForTagQuery);
	}
}
