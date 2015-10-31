import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class EncodeDecode {

	public static void encodeDecodeUsingDCT(int width, int height,
			String fileName, int deliveryMode, double quantCoefficient,
			int latency) throws IOException, InterruptedException {

		// Get Pixel values from array
		int[] componentArray = ImageUtilities.getImagePixelCoponents(width,
				height, fileName);

		// Get 8x8 Blocks from the image bytes array: RGB component wise
		Map<String, List<int[][]>> blocks = getBlocksFromImageComponents(width,
				height, componentArray);

		// Calculate DCTs: ENCODE
		System.out.println("***** Calculating DCT ***** START");
		
		Map<String, List<double[][]>> dctBlocks = JPEGDCTQuant.getBlocksWithDCTs(blocks, quantCoefficient);
		
		System.out.println("***** Calculating DCT ***** END");

		// Display input RGB image
		ImageUtilities.getAndDisplayImage(width, height, fileName);
		
		String outImgMsg = "Output Image: ";
		int xLoc = 700;
		int yLoc = 200;
		
		switch (deliveryMode) {
			case 1 : outImgMsg = outImgMsg + " Baseline Mode"; break;
			case 2 : outImgMsg = outImgMsg + " Spectral Selection"; break;
			case 3 : outImgMsg = outImgMsg + " Successive Bit"; break;
		}
	
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		//Display Input Image
		JPanel panel = new JPanel();
		JComponent comp = new JLabel(new ImageIcon(img));
		panel.add(comp);
		JFrame frame = new JFrame(outImgMsg);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocation(xLoc, yLoc);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if (deliveryMode == 1) {
			
			System.out.println("***** Decoding the image: Delivery Mode: BASELINE ***** START");
			
			List<double[][]> redBlocks = dctBlocks.get("red");
			List<double[][]> greenBlocks = dctBlocks.get("green");
			List<double[][]> blueBlocks = dctBlocks.get("blue");
			
			int xStep = 0;
			int yStep = 0;
			
			for (int blockNum = 0; blockNum < redBlocks.size(); blockNum++) {
				
				double[][] redArr = redBlocks.get(blockNum);
				double[][] greenArr = greenBlocks.get(blockNum);
				double[][] blueArr = blueBlocks.get(blockNum);
				
				int[][] pixBlock = JPEGDCTQuant.getBlockInverseDCTsForBaseline(redArr, greenArr, blueArr, quantCoefficient);
				
				yStep = (blockNum % 44) * 8;
				
				if (blockNum != 0 && blockNum % 44 == 0) {
					yStep = 0;
					xStep = (blockNum / 44) * 8;
				} 
				
				for (int x = 0; x < pixBlock.length; x++) {
					for (int y = 0; y < pixBlock.length; y++) {
						int xIdx = x + xStep;
						int yIdx = y + yStep;
						img.setRGB(yIdx, xIdx, pixBlock[x][y]);
					}
				}
				
				SwingUtilities.updateComponentTreeUI(frame);
				Thread.sleep(latency);
				
			}
			
			System.out.println("***** Decoding the image: Delivery Mode: BASELINE ***** END");
			
		} else if (deliveryMode == 2) {
			
			System.out.println("***** Decoding the image: Delivery Mode: Spectral Selection ***** START");
			
			for (int i = 0; i < 64; i++) {
				
				Map<String, List<int[][]>> iDctBlocks = JPEGDCTQuant
						.getBlocksInverseDCTsSpectral(dctBlocks, quantCoefficient, i);

				int[] newComponents = getImageComponentsFromBlocks(
						iDctBlocks.get("red"), iDctBlocks.get("green"),
						iDctBlocks.get("blue"));
				
				int ind = 0;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {

						int r = newComponents[ind];
						int g = newComponents[ind + height * width];
						int b = newComponents[ind + height * width * 2];
						
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8)
								| (b & 0xff);
						
						img.setRGB(x, y, pix);
						ind++;
					}
				}
				
				SwingUtilities.updateComponentTreeUI(frame);
				Thread.sleep(latency);
			}
			
			System.out.println("***** Decoding the image: Delivery Mode: Spectral Selection ***** END");
			
		} else if (deliveryMode == 3) {
			
			System.out.println("***** Decoding the image: Delivery Mode: Successive bit approximation ***** START");
			//Find out the highest bit to start from
		
			int highestBit = JPEGDCTQuant.getHighestBitFromBlocks(dctBlocks);
			
			for (int i = highestBit; i >= 0; i--) {
				
				Map<String, List<int[][]>> iDctBlocks = JPEGDCTQuant
						.getBlocksInverseDCTsSuccessive(dctBlocks, quantCoefficient, i);

				int[] newComponents = getImageComponentsFromBlocks(
						iDctBlocks.get("red"), iDctBlocks.get("green"),
						iDctBlocks.get("blue"));
				
				int ind = 0;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {

						int r = newComponents[ind];
						int g = newComponents[ind + height * width];
						int b = newComponents[ind + height * width * 2];
						
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8)
								| (b & 0xff);
						
						img.setRGB(x, y, pix);
						ind++;
					}
				}
				
				SwingUtilities.updateComponentTreeUI(frame);
				Thread.sleep(latency);
			}
			
			System.out.println("***** Decoding the image: Delivery Mode: Successive bit approximation ***** END");
			
		} else {
			// Calculate IDCTs: DECODE
			Map<String, List<int[][]>> iDctBlocks = JPEGDCTQuant
					.getBlocksWithInverseDCTs(dctBlocks, quantCoefficient);

			// get bytes back from the blocks
			// int[] newComponents = getImageComponentsFromBlocks(blocks.get("red"), blocks.get("green"), blocks.get("blue"));
			
			int[] newComponents = getImageComponentsFromBlocks(
					iDctBlocks.get("red"), iDctBlocks.get("green"),
					iDctBlocks.get("blue"));

			// Display output image
			ImageUtilities.displayInImageInJPanel(width, height, newComponents, outImgMsg, 700, 200);
		}
	}

	private static Map<String, List<int[][]>> getBlocksFromImageComponents(
			int width, int height, int[] bytes) {

		Map<String, List<int[][]>> blocks = new HashMap<String, List<int[][]>>();

		// Create list holding all the blocks for red, green and blue components
		List<int[][]> redBlockList = new ArrayList<int[][]>();
		List<int[][]> greenBlockList = new ArrayList<int[][]>();
		List<int[][]> blueBlockList = new ArrayList<int[][]>();

		int greenStep = height * width;
		int blueStep = height * width * 2;
		int heightStep = 0;

		for (int ind = 0; ind < height * width;) {
			// create RGB component blocks
			int[][] redCompBlock = new int[8][8];
			int[][] greenCompBlock = new int[8][8];
			int[][] blueCompBlock = new int[8][8];
			int step = ind;
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					redCompBlock[i][j] = bytes[step + j];
					greenCompBlock[i][j] = bytes[step + greenStep + j];
					blueCompBlock[i][j] = bytes[step + blueStep + j];
				}
				step = step + width;
			}

			ind = ind + 8;
			if (ind % width == 0) {
				heightStep = heightStep + (width * 8);
				ind = heightStep;
			}

			// Add blocks to list
			redBlockList.add(redCompBlock);
			greenBlockList.add(greenCompBlock);
			blueBlockList.add(blueCompBlock);
		}

		blocks.put("red", redBlockList);
		blocks.put("green", greenBlockList);
		blocks.put("blue", blueBlockList);

		return blocks;
	}

	private static int[] getImageComponentsFromBlocks(
			List<int[][]> redBlockList, List<int[][]> greenBlockList,
			List<int[][]> blueBlockList) {

		int height = 288;
		int width = 352;

		int[] bytes = new int[width * height * 3];
		int idx = 0;
		int greenStep = width * height;
		int blueStep = width * height * 2;

		int step = 0;
		int heightStep = 0;

		for (int i = 0; i < redBlockList.size(); i++) {

			int[][] red = redBlockList.get(i);
			int[][] green = greenBlockList.get(i);
			int[][] blue = blueBlockList.get(i);

			if (i != 0 && i % 44 == 0) {
				heightStep = heightStep + (width * 8);
				idx = heightStep;
				step = 0;
			} else {
				idx = heightStep;
			}

			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					bytes[k + idx + step] = red[j][k];
					bytes[k + idx + greenStep + step] = green[j][k];
					bytes[k + idx + blueStep + step] = blue[j][k];
				}
				idx = idx + width;
			}
			step = step + 8;
		}
		return bytes;
	}
}
