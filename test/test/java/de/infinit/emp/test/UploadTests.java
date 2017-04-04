package de.infinit.emp.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UploadTests {
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
		File temp = File.createTempFile("uploadtest", ".temp");
		// temp.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
		for (int i = 0; i < 1000; i++) {
			bw.write(generatorHardwareCode() + "\n");
		}
		bw.close();
		return temp;
	}

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
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
