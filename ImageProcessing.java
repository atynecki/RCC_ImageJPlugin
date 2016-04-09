
/**
 * @class ImageProcessing
 * @brief class representing image processing to analyze image 
 */

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageProcessing 
{
	/** default constructors */
	public ImageProcessing() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
		 
	/**
	 * @fn Process()
	 * @brief process to analyze image
	 * @param image object
	 * @return image object after process
	 */
	public Mat process(Mat img){ 
		Mat dst = new Mat();
		Imgproc.threshold(img, dst, 12, 1, Imgproc.THRESH_OTSU);
		int dilate_size = 2;
		Mat element  = Imgproc.getStructuringElement(1, new Size(3 * dilate_size + 1, 3 * dilate_size + 1));
		Imgproc.Canny(img, dst, 10, 60);
		Imgproc.dilate(dst, dst, element);
   return dst;
	}
	
	/**
	 * @fn countEryth()
	 * @brief process to count erythrocytes
	 * @param image object
	 * @return number of erythrocytes
	 */
	public int countEryth(Mat dst) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); 
		Imgproc.findContours(dst, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		int eryth = contours.size();
		return eryth;
	}
	
	/**
	 * @fn matToBufferedImage()
	 * @brief conversion mat object to buffered image
	 * @param image object
	 * @return buffered image object
	 */
	public static BufferedImage matToBufferedImage(Mat mat) {
		BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_BYTE_GRAY);
	    WritableRaster raster = image.getRaster();
	    DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
	    byte[] data = dataBuffer.getData();
	    mat.get(0, 0, data);
	    return image;
	}
	
	/**
	 * @fn resizedImage()
	 * @brief resized image
	 * @param image object
	 * @param width of new image
	 * @param height of new image
	 * @return buffered image object after resize
	 */
	public static BufferedImage resizedImage(BufferedImage img, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) resizedImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(img, 0, 0, width, height, null);
		g2.dispose();
		return resizedImage;
	}
}
