package main;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MergeQuery {

	public static final String FILE_PATH = "E://opt/query.txt";
	public static final String QUERY_SPLIT = "\\[params=";
	private static final List<String> ESCAPE = Arrays.asList("(String)", "(Date)");
	public static final String SELECT_STRING = "SELECT";

	public static void main(String[] args) {
		// System.out.println("Parsing " + FILE_PATH + " ...");

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(FILE_PATH));
			String content = scanner.useDelimiter("\\Z").next();

			if (!content.startsWith(SELECT_STRING)) {
				throw new RuntimeException("File content must start with " + SELECT_STRING);
			}
			
			String[] parts = content.split(QUERY_SPLIT);
			if (parts.length != 2) {
				throw new RuntimeException("Query split not found: " + QUERY_SPLIT);
			}

			String query = parts[0];
			String[] queryParts = query.split("\\?");

			String parameters = parts[1].substring(0, parts[1].length() - 1);
			String[] parameterParts = parameters.split(", ");

			if (queryParts.length - 1 != parameterParts.length) {
				throw new RuntimeException("Parameters not matchable!");
			}

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < queryParts.length; i++) {
				addParameter(builder, i, queryParts, parameterParts);
			}

			System.out.println(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	private static void addParameter(StringBuilder builder, int pos, String[] queryParts, String[] parameterParts) {
		builder.append(queryParts[pos]);
		
		if (pos >= parameterParts.length) {
			return;
		}
		
		String param = parameterParts[pos];
		String[] paramParts = param.split(" ");
		String val = "";
		if (paramParts.length == 2) {
			val = paramParts[1];
		} else if (paramParts.length != 2 && paramParts.length != 1) {
			throw new RuntimeException("Parameter not parseable: " + param);
		}

		String type = paramParts[0];
		if (!ESCAPE.contains(type.trim())) {
			builder.append(val);
		} else {
			builder.append("'");
			builder.append(val);
			builder.append("'");
		}
	}

}
