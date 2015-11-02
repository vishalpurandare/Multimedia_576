import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JPEGDCTQuant {

	public static Map<String, List<double[][]>> getBlocksWithDCTs(
			Map<String, List<int[][]>> blocks, double quantCoefficient) {

		Map<String, List<double[][]>> dctBlocks = new HashMap<String, List<double[][]>>();

		List<int[][]> redBlocks = blocks.get("red");
		List<int[][]> greenBlocks = blocks.get("green");
		List<int[][]> blueBlocks = blocks.get("blue");

		List<double[][]> redBlocksOut = new ArrayList<double[][]>();
		List<double[][]> greenBlocksOut = new ArrayList<double[][]>();
		List<double[][]> blueBlocksOut = new ArrayList<double[][]>();

		for (int i = 0; i < redBlocks.size(); i++) {

			int[][] redArr = redBlocks.get(i);
			int[][] greenArr = greenBlocks.get(i);
			int[][] blueArr = blueBlocks.get(i);

			double[][] redArrOut = new double[8][8];
			double[][] greenArrOut = new double[8][8];
			double[][] blueArrOut = new double[8][8];

			for (int u = 0; u < 8; u++) {
				for (int v = 0; v < 8; v++) {

					double cU = 1.0;
					double cV = 1.0;
					if (u == 0 || v == 0) {
						
						cU = (1 / (Math.sqrt(2.0)));
						cV = (1 / (Math.sqrt(2.0)));
					}

					double finalValRed = 0.0;
					double finalValGreen = 0.0;
					double finalValBlue = 0.0;

					for (int x = 0; x < 8; x++) {
						for (int y = 0; y < 8; y++) {

							double cosXAngle = ((2 * x + 1) * (u * Math.PI)) / 16;
							double cosYAngle = ((2 * y + 1) * (v * Math.PI)) / 16;

							finalValRed += (redArr[x][y] * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));
							finalValGreen += (greenArr[x][y]
									* Math.cos(cosXAngle) * Math.cos(cosYAngle));
							finalValBlue += (blueArr[x][y]
									* Math.cos(cosXAngle) * Math.cos(cosYAngle));

						}
					}

					double multFactor = (cU * cV) / 4;
					redArrOut[u][v] = Math.round((finalValRed * multFactor)
							/ quantCoefficient);
					greenArrOut[u][v] = Math.round((finalValGreen * multFactor)
							/ quantCoefficient);
					blueArrOut[u][v] = Math.round((finalValBlue * multFactor)
							/ quantCoefficient);
				}
			}

			redBlocksOut.add(redArrOut);
			greenBlocksOut.add(greenArrOut);
			blueBlocksOut.add(blueArrOut);

		}
		dctBlocks.put("red", redBlocksOut);
		dctBlocks.put("green", greenBlocksOut);
		dctBlocks.put("blue", blueBlocksOut);

		return dctBlocks;
	}

	public static Map<String, List<int[][]>> getBlocksWithInverseDCTs(
			Map<String, List<double[][]>> dctBlocks, double quantCoefficient) {

		Map<String, List<int[][]>> inverseDctBlocks = new HashMap<String, List<int[][]>>();

		List<double[][]> redBlocks = dctBlocks.get("red");
		List<double[][]> greenBlocks = dctBlocks.get("green");
		List<double[][]> blueBlocks = dctBlocks.get("blue");

		List<int[][]> redBlocksOut = new ArrayList<int[][]>();
		List<int[][]> greenBlocksOut = new ArrayList<int[][]>();
		List<int[][]> blueBlocksOut = new ArrayList<int[][]>();

		for (int i = 0; i < redBlocks.size(); i++) {

			double[][] redArr = redBlocks.get(i);
			double[][] greenArr = greenBlocks.get(i);
			double[][] blueArr = blueBlocks.get(i);

			int[][] redArrOut = new int[8][8];
			int[][] greenArrOut = new int[8][8];
			int[][] blueArrOut = new int[8][8];

			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {

					double finalValRed = 0.0;
					double finalValGreen = 0.0;
					double finalValBlue = 0.0;

					for (int u = 0; u < 8; u++) {
						for (int v = 0; v < 8; v++) {

							double cU = 1.0;
							double cV = 1.0;
							if (u == 0 && v == 0) {
								cU = (1 / (Math.sqrt(2.0)));
								cV = (1 / (Math.sqrt(2.0)));
							}

							double cosXAngle = ((2 * x + 1) * (u * Math.PI)) / 16;
							double cosYAngle = ((2 * y + 1) * (v * Math.PI)) / 16;

							finalValRed += (cU * cV * redArr[u][v]
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));
							finalValGreen += (cU * cV * greenArr[u][v]
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));
							finalValBlue += (cU * cV * blueArr[u][v]
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));

						}
					}

					int rVal = (int) Math.min(Math.max(((finalValRed / 4)), 0),
							255);
					int gVal = (int) Math.min(
							Math.max(((finalValGreen / 4)), 0), 255);
					int bVal = (int) Math.min(
							Math.max(((finalValBlue / 4)), 0), 255);

					redArrOut[x][y] = rVal;
					greenArrOut[x][y] = gVal;
					blueArrOut[x][y] = bVal;

				}
			}

			redBlocksOut.add(redArrOut);
			greenBlocksOut.add(greenArrOut);
			blueBlocksOut.add(blueArrOut);

		}

		inverseDctBlocks.put("red", redBlocksOut);
		inverseDctBlocks.put("green", greenBlocksOut);
		inverseDctBlocks.put("blue", blueBlocksOut);

		return inverseDctBlocks;
	}

	public static int[][] getBlockInverseDCTsForBaseline(double[][] redArr,
			double[][] greenArr, double[][] blueArr, double quantCoefficient) {

		int[][] outPixBlockArr = new int[8][8];

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				double finalValRed = 0.0;
				double finalValGreen = 0.0;
				double finalValBlue = 0.0;

				for (int u = 0; u < 8; u++) {
					for (int v = 0; v < 8; v++) {

						double cU = 1.0;
						double cV = 1.0;
						if (u == 0 && v == 0) {
							cU = (1 / (Math.sqrt(2.0)));
							cV = (1 / (Math.sqrt(2.0)));
						}

						double cosXAngle = ((2 * x + 1) * (u * Math.PI)) / 16;
						double cosYAngle = ((2 * y + 1) * (v * Math.PI)) / 16;

						finalValRed += (cU * cV * redArr[u][v]
								* quantCoefficient * Math.cos(cosXAngle) * Math
								.cos(cosYAngle));
						finalValGreen += (cU * cV * greenArr[u][v]
								* quantCoefficient * Math.cos(cosXAngle) * Math
								.cos(cosYAngle));
						finalValBlue += (cU * cV * blueArr[u][v]
								* quantCoefficient * Math.cos(cosXAngle) * Math
								.cos(cosYAngle));

					}
				}

				int rVal = (int) Math
						.min(Math.max(((finalValRed / 4)), 0), 255);
				int gVal = (int) Math.min(Math.max(((finalValGreen / 4)), 0),
						255);
				int bVal = (int) Math.min(Math.max(((finalValBlue / 4)), 0),
						255);

				int pix = 0xff000000 | ((rVal & 0xff) << 16)
						| ((gVal & 0xff) << 8) | (bVal & 0xff);

				outPixBlockArr[x][y] = pix;

			}
		}

		return outPixBlockArr;

	}

	public static Map<String, List<int[][]>> getBlocksInverseDCTsSpectral(
			Map<String, List<double[][]>> dctBlocks, double quantCoefficient,
			int num) {

		Map<String, List<int[][]>> inverseDctBlocks = new HashMap<String, List<int[][]>>();

		List<double[][]> redBlocks = dctBlocks.get("red");
		List<double[][]> greenBlocks = dctBlocks.get("green");
		List<double[][]> blueBlocks = dctBlocks.get("blue");

		List<int[][]> redBlocksOut = new ArrayList<int[][]>();
		List<int[][]> greenBlocksOut = new ArrayList<int[][]>();
		List<int[][]> blueBlocksOut = new ArrayList<int[][]>();

		for (int i = 0; i < redBlocks.size(); i++) {

			double[][] redArr = redBlocks.get(i);
			double[][] greenArr = greenBlocks.get(i);
			double[][] blueArr = blueBlocks.get(i);

			double[][] redArrTemp = new double[8][8];
			double[][] greenArrTemp = new double[8][8];
			double[][] blueArrTemp = new double[8][8];

			int[][] redArrOut = new int[8][8];
			int[][] greenArrOut = new int[8][8];
			int[][] blueArrOut = new int[8][8];
			
			// Cross, Zig-Zag traversal
			int count = 0;
			int iIdx = 1;
			int jIdx = 1;
			for (int k = 0; k < 64; k++) {
				
				redArrTemp[iIdx - 1][jIdx - 1] = redArr[iIdx - 1][jIdx - 1];
				greenArrTemp[iIdx - 1][jIdx - 1] = greenArr[iIdx - 1][jIdx - 1];
				blueArrTemp[iIdx - 1][jIdx - 1] = blueArr[iIdx - 1][jIdx - 1];
				count++;

				if (count > num) {
					break;
				}

				if ((iIdx + jIdx) % 2 == 0) {
					if (jIdx < 8) {
						jIdx++;
					} else {
						iIdx += 2;
					}
					if (iIdx > 1) {
						iIdx--;
					}
				} else {
					if (iIdx < 8) {
						iIdx++;
					} else {
						jIdx += 2;
					}
					if (jIdx > 1) {
						jIdx--;
					}
				}
			}

			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {

					double finalValRed = 0.0;
					double finalValGreen = 0.0;
					double finalValBlue = 0.0;

					for (int u = 0; u < 8; u++) {
						for (int v = 0; v < 8; v++) {

							double cU = 1.0;
							double cV = 1.0;
							if (u == 0 && v == 0) {
								cU = (1 / (Math.sqrt(2.0)));
								cV = (1 / (Math.sqrt(2.0)));
							}

							double cosXAngle = ((2 * x + 1) * (u * Math.PI)) / 16;
							double cosYAngle = ((2 * y + 1) * (v * Math.PI)) / 16;

							finalValRed += (cU * cV * redArrTemp[u][v]
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));
							finalValGreen += (cU * cV * greenArrTemp[u][v]
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));
							finalValBlue += (cU * cV * blueArrTemp[u][v]
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));

						}
					}

					int rVal = (int) Math.min(Math.max(((finalValRed / 4)), 0),
							255);
					int gVal = (int) Math.min(
							Math.max(((finalValGreen / 4)), 0), 255);
					int bVal = (int) Math.min(
							Math.max(((finalValBlue / 4)), 0), 255);

					redArrOut[x][y] = rVal;
					greenArrOut[x][y] = gVal;
					blueArrOut[x][y] = bVal;

				}
			}

			redBlocksOut.add(redArrOut);
			greenBlocksOut.add(greenArrOut);
			blueBlocksOut.add(blueArrOut);
		}

		inverseDctBlocks.put("red", redBlocksOut);
		inverseDctBlocks.put("green", greenBlocksOut);
		inverseDctBlocks.put("blue", blueBlocksOut);

		return inverseDctBlocks;
	}

	public static Map<String, List<int[][]>> getBlocksInverseDCTsSuccessive(
			Map<String, List<double[][]>> dctBlocks, double quantCoefficient,
			int bitNum) {

		Map<String, List<int[][]>> inverseDctBlocks = new HashMap<String, List<int[][]>>();

		List<double[][]> redBlocks = dctBlocks.get("red");
		List<double[][]> greenBlocks = dctBlocks.get("green");
		List<double[][]> blueBlocks = dctBlocks.get("blue");

		List<int[][]> redBlocksOut = new ArrayList<int[][]>();
		List<int[][]> greenBlocksOut = new ArrayList<int[][]>();
		List<int[][]> blueBlocksOut = new ArrayList<int[][]>();

		for (int i = 0; i < redBlocks.size(); i++) {

			double[][] redArr = redBlocks.get(i);
			double[][] greenArr = greenBlocks.get(i);
			double[][] blueArr = blueBlocks.get(i);

			int[][] redArrOut = new int[8][8];
			int[][] greenArrOut = new int[8][8];
			int[][] blueArrOut = new int[8][8];

			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {

					double finalValRed = 0.0;
					double finalValGreen = 0.0;
					double finalValBlue = 0.0;

					for (int u = 0; u < 8; u++) {
						for (int v = 0; v < 8; v++) {

							double cU = 1.0;
							double cV = 1.0;
							if (u == 0 && v == 0) {
								cU = (1 / (Math.sqrt(2.0)));
								cV = (1 / (Math.sqrt(2.0)));
							}

							double cosXAngle = ((2 * x + 1) * (u * Math.PI)) / 16;
							double cosYAngle = ((2 * y + 1) * (v * Math.PI)) / 16;

							int redComp = (int) (Math.round(redArr[u][v]));
							int greenComp = (int) (Math.round(greenArr[u][v]));
							int blueComp = (int) (Math.round(blueArr[u][v]));

							redComp = getMSBNumberAdjusted(redComp, bitNum);
							greenComp = getMSBNumberAdjusted(greenComp, bitNum);
							blueComp = getMSBNumberAdjusted(blueComp, bitNum);

							finalValRed += (cU * cV * redComp
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));
							finalValGreen += (cU * cV * greenComp
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));
							finalValBlue += (cU * cV * blueComp
									* quantCoefficient * Math.cos(cosXAngle) * Math
									.cos(cosYAngle));

						}
					}

					int rVal = (int) Math.min(Math.max(((finalValRed / 4)), 0),
							255);
					int gVal = (int) Math.min(
							Math.max(((finalValGreen / 4)), 0), 255);
					int bVal = (int) Math.min(
							Math.max(((finalValBlue / 4)), 0), 255);

					redArrOut[x][y] = rVal;
					greenArrOut[x][y] = gVal;
					blueArrOut[x][y] = bVal;

				}
			}

			redBlocksOut.add(redArrOut);
			greenBlocksOut.add(greenArrOut);
			blueBlocksOut.add(blueArrOut);
		}

		inverseDctBlocks.put("red", redBlocksOut);
		inverseDctBlocks.put("green", greenBlocksOut);
		inverseDctBlocks.put("blue", blueBlocksOut);

		return inverseDctBlocks;
	}

	//Utitlity method for Successive Bit Approximation
	private static int getMSBNumberAdjusted(int val, int bitNum) {

		boolean isNegative = false;

		if (val == 0) {
			return val;
		}

		if (val < 0) {
			isNegative = true;
		}

		val = Math.abs(val);
		int msb = getMSB(val);
		int shiftedNum = 1 << msb;

		int currNum = 1 << bitNum;

		if (currNum > shiftedNum) {
			if (isNegative) {
				shiftedNum = (shiftedNum * (-1));
			}
			return shiftedNum;
		}

		int factor = 1;
		for (int i = 0; i < bitNum - 1; i++) {
			factor = factor << 1;
			factor = factor | 1;
		}

		factor = ~factor;

		int n = val & factor;

		if (isNegative) {
			return (n * (-1));
		}
		return n;
	}
	
	//Get Most Significant bit of the number as index in 32 bit
	public static int getMSB(int val) {
		int msb = -1;
		int mask = 1 << 31;
		for (int bitIndex = 31; bitIndex >= 0; bitIndex--) {
			if ((val & mask) != 0) {
				msb = bitIndex;
				break;
			}
			mask >>>= 1;
		}
		return msb;
	}

	//Get Highest bit for greatest number from all the blocks
	public static int getHighestBitFromBlocks(Map<String, List<double[][]>> dctBlocks) {
		
		int highestBit = 0;
		
		List<double[][]> redBlocks = dctBlocks.get("red");
		List<double[][]> greenBlocks = dctBlocks.get("green");
		List<double[][]> blueBlocks = dctBlocks.get("blue");

		for (int i = 0; i < redBlocks.size(); i++) {

			double[][] redArr = redBlocks.get(i);
			double[][] greenArr = greenBlocks.get(i);
			double[][] blueArr = blueBlocks.get(i);
			
			// For all block DC Coeff will be highest to take only first element
		
			highestBit = highestBit | (Math.abs((int)Math.round(redArr[0][0])));
			highestBit = highestBit | (Math.abs((int)Math.round(greenArr[0][0])));
			highestBit = highestBit | (Math.abs((int)Math.round(blueArr[0][0])));
			
		}
		
		highestBit = getMSB(highestBit);
		
		return highestBit;
	}
}
