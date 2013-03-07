/*
 * This class is used for testing the logic in other classes. 
 */
public class TestClass {

	public static void main(String[] args)
	{
		String line = "handicapped-infants 	       	-      y n";
		extractFeature(line);
	}
	
	// Extracts a feature object from dataset line
	private static void extractFeature(String line)
	{
		int lastDashIndex = line.lastIndexOf("-");
		String beforeFeatureVal = line.substring(0, lastDashIndex);
		String afterFeatureVal = line.substring(lastDashIndex+1);
		
		System.out.println(beforeFeatureVal + ", " + afterFeatureVal);
		
		String featureName = beforeFeatureVal.trim();
		String[] featureValues = afterFeatureVal.trim().split(" ");
		String firstValue = featureValues[0].trim();
		String secondValue = featureValues[1].trim();
		
		System.out.println(featureName + "," + firstValue + "," + secondValue);

	}

	// Returns the information gain at each feature value node
	private static double getInfoGain(double featureValCnt, double totalExamples, double posCnt, double negCnt)
	{
		double infoGain = 
			(featureValCnt/totalExamples)*
			(
				(-1*(posCnt/featureValCnt)*(Math.log(posCnt/featureValCnt)/Math.log(2))) +
				(-1*(negCnt/featureValCnt)*(Math.log(negCnt/featureValCnt)/Math.log(2)))
			);
		
		return infoGain;
	}

}
