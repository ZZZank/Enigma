package cuchaz.enigma.mcp.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import joptsimple.ValueConverter;

/**
 * @author ZZZank
 */
public class PathConverter implements ValueConverter<Path> {
	public static final ValueConverter<Path> INSTANCE = new PathConverter();

	PathConverter() {
	}

	@Override
	public Path convert(String path) {
		if (path.startsWith("~")) {
			Path dirHome = Paths.get(System.getProperty("user.home"));

			if (path.startsWith("~/")) {
				return dirHome.resolve(path.substring(2));
			} else {
				return dirHome.getParent().resolve(path.substring(1));
			}
		}

		return Paths.get(path);
	}

	@Override
	public Class<? extends Path> valueType() {
		return Path.class;
	}

	@Override
	public String valuePattern() {
		return "path";
	}
}
