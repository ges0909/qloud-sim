package de.infinit.emp.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import org.aeonbits.owner.ConfigCache;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import de.infinit.emp.ApplicationConfig;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UploadTest {
	static String initialURL;
	static File fileToUpload;

	private static String generatorHardwareCode() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String part1 = RandomStringUtils.random(5, chars);
		String part2 = RandomStringUtils.random(5, chars);
		String part3 = RandomStringUtils.random(5, chars);
		String part4 = RandomStringUtils.random(5, chars);
		return part1 + "-" + part2 + "-" + part3 + "-" + part4;
	}

	private static File createTempFile() throws IOException {
		File csvFile = File.createTempFile("Sensors", ".csv");
		csvFile.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
		for (int i = 0; i < 1000; i++) {
			bw.write(generatorHardwareCode() + "," + "Messtelle " + i + "\n");
		}
		bw.close();
		return csvFile;
	}

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);
		initialURL = "http://localhost:" + config.port();
		Application.main(null);
		Spark.awaitInitialization();
		fileToUpload = createTempFile();
	}

	@AfterClass
	public static void tearDown() throws SQLException {
		Spark.stop();
	}

	@Test
	public void dummyTest() {

	}
}
