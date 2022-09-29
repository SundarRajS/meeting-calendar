package com.meeting.booking.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public interface FileUtils {

	public static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	public static List<String> readInputFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			return null;
		}
		try (InputStreamReader isr = new InputStreamReader(file.getInputStream());
				BufferedReader br = new BufferedReader(isr)) {
			String line = null;
			List<String> content = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				content.add(line);
			}
			return content;
		} catch (Exception e) {
			logger.error("Exception Occurred in reading the file:" + e.getMessage());
		}
		return null;

	}

}