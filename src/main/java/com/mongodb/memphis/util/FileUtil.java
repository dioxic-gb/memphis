package com.mongodb.memphis.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	private static List<Path> directories = new ArrayList<>();

	static {
		addDirectory(".");
	}

	public static void addDirectory(String directory) {
		Path dir = Paths.get(directory);

		if (Files.isDirectory(dir)) {
			directories.add(dir.toAbsolutePath());
		}
		else {
			throw new IllegalArgumentException("input must be a directory");
		}
	}

	public static Path resolveFile(String filename) {
		for (Path p : directories) {
			// try file relative to stored directories
			Path path = Paths.get(p.toString(), filename);
			if (Files.isRegularFile(path)) {
				return path;
			}
			if (Files.isDirectory(path)) {
				throw new IllegalStateException(path.toString() + " is a directory!");
			}
		}

		throw new IllegalStateException(filename + " cannot be found!");
	}
}
