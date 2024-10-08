package preevisiontosimulink.util;

import preevisiontosimulink.proxy.system.SimulinkSystem;

public class StringUtils {

	/**
	 * Extracts the first part of a string separated by an underscore.
	 *
	 * @param str the input string
	 * @return the first part of the string before the underscore
	 */
	public static String getFirstPart(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		int underscoreIndex = str.indexOf('_');
		if (underscoreIndex == -1) {
			return str; // No underscore found, return the whole string
		}
		return str.substring(0, underscoreIndex);
	}

	public static String removeEnding(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex != -1) {
			return fileName.substring(0, dotIndex);
		}
		return fileName;
	}

	// Recursive method to generate a unique name
	public static String generateUniqueName(SimulinkSystem system, String name) {
		if (system.getSubsystem(name) != null) {
			// If the name exists, append or increment the suffix and try again
			int suffix = 1;
			String newName;
			do {
				newName = name + "_" + suffix;
				suffix++;
			} while (system.getSubsystem(newName) != null);
			return generateUniqueName(system, newName);
		} else {
			// If the name is unique, return it
			return name;
		}
	}

	public static int convertStringToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			System.out.println("Invalid string format for an integer: " + str);
			return 0; // Return a default value, e.g., 0
		}
	}

	public static int extractNumber(String name, int position) {
		String[] parts = name.split("_");
		return Integer.parseInt(parts[position]);
	}
	
	public static String produceValidModelNameFromWire(String str) {
	    if (str == null) {
	        return null;
	    }
	    return str.replace('-', '_').replace('/', '_').replace(",", "").replace(" ", "");
	}
}
