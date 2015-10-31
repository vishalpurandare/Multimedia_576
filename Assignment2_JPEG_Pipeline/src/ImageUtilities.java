import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageUtilities {

	public static void getAndDisplayImage(int width, int height, String fileName)
			throws IOException {

		int[] bytes = getImageBytes(width, height, fileName);
		displayInImageInJPanel(width, height, bytes, "Input Image", 200, 200);

	}

	public static int[] getImageBytes(int width, int height, String fileName)
			throws IOException {

		InputStream is = null;
		
		File file = new File(fileName);
		is = new FileInputStream(file);

		long len = file.length();
		byte[] bytes = new byte[(int) len];

		int offset = 0;
		int numRead = 0;

		// Reading the number of bytes into the array bytes
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		is.close();
		
		int[] retArr = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			int b = (int) bytes[i];
			retArr[i] = b;
		}
		
		return retArr;
	}
	
	public static int[] getImagePixelCoponents(int width, int height,
			String fileName) throws IOException {

		InputStream is = null;

		File file = new File(fileName);
		is = new FileInputStream(file);

		long len = file.length();
		byte[] bytes = new byte[(int) len];
		
		int[] componentArray = new int[(int) len];

		int offset = 0;
		int numRead = 0;

		// Reading the number of bytes into the array bytes
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
	
		int ind = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// byte a = 0;
				byte r = bytes[ind];
				byte g = bytes[ind + height * width];
				byte b = bytes[ind + height * width * 2];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8)
						| (b & 0xff);
				
				int rCmp = ((pix >> 16) & 0xff);
				int gCmp = ((pix >> 8) & 0xff);
				int bCmp = ((pix) & 0xff);
				
				componentArray[ind] = rCmp;
				componentArray[ind + height * width] = gCmp;
				componentArray[ind + height * width * 2] = bCmp;
				
				ind++;
			}
		}
		is.close();
		return componentArray;
	}
	
	public static void displayInImageInJPanel(int width, int height,
			int[] bytes, String msg, int xLoc, int yLoc) {

		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		int ind = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int r = bytes[ind];
				int g = bytes[ind + height * width];
				int b = bytes[ind + height * width * 2];
				
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8)
						| (b & 0xff);
				
				img.setRGB(x, y, pix);
				ind++;
			}
		}

		// Use a panel and label to display the image
		JPanel panel = new JPanel();
		panel.add(new JLabel(new ImageIcon(img)));
		JFrame frame = new JFrame(msg);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocation(xLoc, yLoc);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
}
