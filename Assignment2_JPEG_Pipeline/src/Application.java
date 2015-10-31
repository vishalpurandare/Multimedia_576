import java.io.IOException;

public class Application {

	/*
	 * Entry point for the program, 5 command line arguments required
	 */
	public static void main(String[] args) {

		// Check if 5 Command line arguments are not supplied, then exit program
		if (args.length != 4) {
			System.out.println("Invalid number of arguments supplied to the program !");
			System.exit(0);
		}

		// Command Line Arguments
		// (1) Input image
		String fileName = args[0];
		// (2) Quantization Level, index ranging from 0 to 7
		int quantizationLevel = Integer.parseInt(args[1]);
		/*
		 * (3) Delivery Mode, index ranging in 1, 2 and 3, 
		 * 1- Baseline Delivery, 2-Progressive Delivery-Spectral, 3- Progressive Delivery- Successive bit approximation
		 */
		int deliveryMode = Integer.parseInt(args[2]);
		
		// (4) Latency, in MS
		int latency = Integer.parseInt(args[3]); // 1 or 2

		int width = 352;
		int height = 288;

		double quantCoefficient = 0;
		
		if (quantizationLevel <= 7 && quantizationLevel >= 0) {
			quantCoefficient = Math.pow(2, quantizationLevel);
		} else {
			System.out.println("Invalid Quantization Level");
			System.exit(0);
		}

		System.out.println("Input image:		" + fileName);
		System.out.println("quatCoefficient:	" + quantCoefficient);
		System.out.println("deliveryMode:		" + deliveryMode);
		System.out.println("Latency:		" + latency);

		try {
			// Perform DCT and deliver the image: Encode and Decode
			EncodeDecode.encodeDecodeUsingDCT(width, height, fileName, deliveryMode,
					quantCoefficient, latency);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}