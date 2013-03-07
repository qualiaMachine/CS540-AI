import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * CS540 HW0: Hand-in Practice and Parsing ML Datasets
 * 
 * @author Shishir Kumar Prasad (skprasad@cs.wisc.edu)
 */
public class HW0 {

	private static final String DASH = "-";
	private static final String SPACE = " ";
	private static final String COMMENT = "//";

	public static void main(String[] args) {
		// Input file is a mandatory argument
		if (args.length != 1) {
			System.err.println("Please supply a filename on the command line: java HW0 <filename>");
			System.exit(1);
		}

		// Create a scanner object to read the file
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File(args[0]));
		} catch (Exception e) {
			System.err.println("Unable to read the file " + args[0] + ". Exception : " + e.getMessage());
			System.exit(1);
		}

		int exampleCount = 0; // Total examples in the dataset
		int tempExampleCount = 0; // Temporary variable to ensure only
									// exampleCount entries are read.
		int featureCount = 0; // Total features in the dataset

		// Map of feature to its list of values
		Map<String, List<String>> featureValuesMap = new HashMap<String, List<String>>();
		// Map of output label to its total count in the dataset
		Map<String, Integer> outputLabelCountMap = new HashMap<String, Integer>();
		// Map of feature value to a map of output label and count of data
		// entries containing this features and having this output label
		Map<String, Map<String, Integer>> featureValueCountMap = new HashMap<String, Map<String, Integer>>();

		// Read the file line-by-line
		while (fileScanner.hasNext()) {
			String currLine = fileScanner.nextLine().trim();

			// Skip blank lines and comment lines
			if (currLine.isEmpty() || currLine.contains(COMMENT)) {
				continue;
			}
			// Check for a line containing integer count
			else if (currLine.matches("[0-9]*")) {
				// If feature count not set, set it
				if (featureCount == 0) {
					featureCount = Integer.parseInt(currLine);
				}
				// If total example count not set, set it
				else {
					exampleCount = Integer.parseInt(currLine);
					tempExampleCount = exampleCount;
				}
			}
			// Search for line containing features
			else if (currLine.contains(DASH)) {
				String[] words = currLine.split(DASH);

				String feature = words[0].trim();
				List<String> featureValues = new ArrayList<String>();

				String[] currFeatureValues = words[1].trim().split(SPACE);
				for (int i = 0; i < currFeatureValues.length; i++) {
					featureValues.add(currFeatureValues[i].trim());
				}
				featureValuesMap.put(feature, featureValues);
			} else {
				String[] words = currLine.split("[\\s\\t]+");

				// Extract the output labels
				if (words.length == 1) {
					String outputLabel = words[0].trim();
					outputLabelCountMap.put(outputLabel, 0);
				}
				// Parse the dataset line
				else {
					/*
					 * Even though the file may have many example entries, we
					 * just have to read exampleCount number of entries.
					 */
					if (tempExampleCount == 0) {
						break;
					}
					// Decrement the number of example lines to parse
					--tempExampleCount;
					if (featureValueCountMap.isEmpty()) {
						for (Map.Entry<String, List<String>> entry1 : featureValuesMap.entrySet()) {
							List<String> featureValues = entry1.getValue();
							for (String featureValue : featureValues) {
								Map<String, Integer> labelMap = new HashMap<String, Integer>();
								for (Map.Entry<String, Integer> entry2 : outputLabelCountMap
										.entrySet()) {
									String outputLabel = entry2.getKey();
									labelMap.put(outputLabel, 0);
								}

								featureValueCountMap.put(featureValue, labelMap);
							}
						}
					}

					String ouptutLabel = words[1].trim();
					int labelCount = outputLabelCountMap.get(ouptutLabel);
					outputLabelCountMap.put(ouptutLabel, ++labelCount);

					for (int i = 2; i < words.length; i++) {
						String feature = words[i];

						Map<String, Integer> featureLabelMap = featureValueCountMap.get(feature);
						int featureLabelCount = featureLabelMap.get(ouptutLabel);
						featureLabelMap.put(ouptutLabel, ++featureLabelCount);
						featureValueCountMap.put(feature, featureLabelMap);
					}
				}
			}
		}

		// Print the dataset summary here
		printDatasetSummary(featureCount, exampleCount, featureValuesMap,
				outputLabelCountMap, featureValueCountMap);
	}

	/**
	 * Prints the summary of the dataset that was parsed
	 */
	private static void printDatasetSummary(int featureCount, int exampleCount,
			Map<String, List<String>> featureValuesMap,
			Map<String, Integer> outputLabelCountMap,
			Map<String, Map<String, Integer>> featureValueCountMap) {
		// Summary of the parsed dataset
		System.out.println("There are " + featureCount + " features in the dataset.");
		System.out.println("There are " + exampleCount + " examples.");

		// Count of output labels
		StringBuilder opLabelString = new StringBuilder();
		for (Map.Entry<String, Integer> entry : outputLabelCountMap.entrySet()) {
			opLabelString.append(entry.getValue()).append(" have output label ");
			opLabelString.append("'").append(entry.getKey()).append("'").append(" , ");
		}
		String opLabelStr = opLabelString.toString();
		opLabelStr = opLabelStr.substring(0, opLabelStr.lastIndexOf(",")).trim();
		System.out.println(opLabelStr + ".\n");

		/*
		 * Count of each feature value as a percentage of the output label
		 * featureValue% = count(featureValue &&
		 * outputLabel)*100/count(outputLabel)
		 */
		for (Map.Entry<String, List<String>> entry : featureValuesMap.entrySet()) {
			System.out.println("Feature '" + entry.getKey() + "':");
			List<String> featureValues = entry.getValue();
			for (String featureValue : featureValues) {
				Map<String, Integer> featureLabelMap = featureValueCountMap.get(featureValue);
				for (Map.Entry<String, Integer> labelMap : featureLabelMap.entrySet()) {
					String opLabel = labelMap.getKey();
					int featureLabelCnt = labelMap.getValue();
					int labelCnt = outputLabelCountMap.get(opLabel);

					double percentage = (featureLabelCnt * 100.0d / labelCnt);
					DecimalFormat df = new DecimalFormat("##.##");

					System.out.println("\tIn the examples with label '"
							+ opLabel + "', " + df.format(percentage)
							+ "% have value '" + featureValue + "'.");
				}
			}
		}
	}
}
