import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.TextField;
import java.util.Vector;

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
	
	private TextField size_text;
	private TextField circularity_text;
	
	private static ResultsTable resultsTable;
	private static int cell_number = 0;
	private TextField resultTextField;
	
	private Frame resultFrame;
	
	public static boolean WBC = false;
	public static boolean RBC = false;
	public static boolean PLT = false;
	
	/** Display image containing outlines of measured particles. */
	public static final int SHOW_OUTLINES = 4;
	
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
		if(WBC)
		{
			IJ.run(image, "Analyze Particles...", "size=0-1000 circularity=0.00-1.00 show=[Overlay Outlines] display record slice");
		}
		else if (RBC)
		{
			IJ.run(image, "Analyze Particles...", "size=1000-2000 circularity=0.00-1.00 show=[Overlay Outlines] display record slice");
	
		}
		else if (PLT)
		{
			IJ.run(image, "Analyze Particles...", "size=2000-3000 circularity=0.00-1.00 show=[Overlay Outlines] display record slice");
	
		}
		
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
		
		size_text = (TextField)gd.getComponent(3);
		size_text.setEditable(false);
		
		circularity_text = (TextField)gd.getComponent(5);
		circularity_text.setEditable(false);
		
		String[] labels = new String[3];
		labels[0]="White Blood Cells (WBC)";
		labels[1]="Red Blood Cells (RBC)";
		labels[2]="Platelets (PLT)";
		gd.addRadioButtonGroup("Cells", labels, 3, 1, null);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		gd.setSmartRecording(true);
		sigma = (int)(gd.getNextNumber());
		
		if (gd.invalidNumber()) {
			IJ.error("Bins invalid.");
			return false;
		}
		
		String option = (String)gd.getNextRadioButton();
		if(option.equals(labels[0]))
		{
			size_text.setText("0-1000");
			circularity_text.setText("0.10-1.00");
			WBC = true;
		}
		else WBC = false;
		if(option.equals(labels[1]))
			RBC = true; else RBC = false;
		if(option.equals(labels[2]))
			PLT = true; else PLT = false;
		
		return true;
	}
}
