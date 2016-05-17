import java.awt.Frame;
import java.awt.TextField;

import ij.*;
import ij.process.*;
import ij.util.Tools;
import ij.gui.*;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;

public class RCC implements PlugInFilter {	
	
	private String arg;
	private ImagePlus image;
	
	private static int sigma;
	private static String size;
	private static String circularity;
	
	private static ResultsTable resultsTable;
	private static int cell_number;
	private TextField resultTextField;
	
	private Frame resultFrame;
	
	public RCC() {
		arg = new String();
		image = new ImagePlus();
		resultFrame = new Frame();
		sigma = 1;
		size = "0-Infinity";
		circularity = "0.00-1.00";
	}
	
	public int setup(String arg, ImagePlus imp) {
		this.arg = arg;
		this.image = imp;
		IJ.register(RCC.class);
		if (imp==null) {
			IJ.noImage();
			return DONE;
		}
		
		if (!showDialog())
			return DONE;
		
		int baseFlags = DOES_ALL+NO_CHANGES+NO_UNDO;
		int flags = IJ.setupDialog(imp, baseFlags);
	
		return flags;
	
	}
	
	public void run(ImageProcessor ip) {
		image.unlock();
		IJ.run(image, "8-bit", "");
		
		for (int i = 0; i < sigma; i++)
		{ 
		  IJ.run(image, "Gaussian Blur...", "Sigma = 1"); 
		}
			
		IJ.run(image, "Make Binary", "");
		IJ.run(image, "Fill Holes", "");
		IJ.run(image, "Watershed", "");
		//String analyze_parameter = "size="+size+" "+"circularity="+circularity+" show=[Overlay Outlines] display record slice";
		//IJ.run(image, "Analyze Particles...", analyze_parameter);
		IJ.run(image, "Analyze Particles...", "size=0-infinity circularity=0.00-1.00 show=[Overlay Outlines] display record slice");
		
		image.updateAndDraw();
		resultsTable = ResultsTable.getResultsTable();
		cell_number = resultsTable.getCounter();
		
		MessageDialog result_msg = new MessageDialog(resultFrame, "Result", "Red cell number: \n Red cell mean value:");
	}

	public boolean showDialog() {
		GenericDialog gd = new GenericDialog("Red cell counter");
	
		gd.addNumericField("Sigma", sigma, 0, 2, null);
		gd.addStringField("Size pixel^2:", size, 12);
		gd.addStringField("Circularity:", circularity, 12);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		gd.setSmartRecording(true);
		sigma = (int)(gd.getNextNumber());
		
		gd.setSmartRecording(true);
		size = gd.getNextString(); // min-max size
		
		gd.setSmartRecording(true);
		circularity = gd.getNextString(); // min-max circularity
		
		if (gd.invalidNumber()) {
			IJ.error("Bins invalid.");
			return false;
		}
		
		return true;
	}
}
