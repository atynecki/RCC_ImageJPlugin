import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import ij.*;
import ij.process.*;
import ij.gui.*;
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
	private static int mean_cell_size = 0;
	
	private Frame resultFrame;
	
	public static boolean WBC = false;
	public static boolean RBC = false;
	public static boolean PLT = false;
	public static boolean ALL = false;
	
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
		else if (ALL)
		{
			IJ.run(image, "Analyze Particles...", "size=0-infinity circularity=0.00-1.00 show=[Overlay Outlines] display record slice");
		}
		
		image.updateAndDraw();
		resultsTable = ResultsTable.getResultsTable();
		cell_number = resultsTable.getCounter();
		mean_cell_size = calculteMeanCellSize(resultsTable);
		
		GenericDialog gd = new GenericDialog("Result");
		gd.addMessage("Cell number:");
		gd.addMessage(String.valueOf(cell_number));
		gd.addMessage("Mean cells size:");
		gd.addMessage(String.valueOf(mean_cell_size));
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
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
		
		String[] labels = new String[4];
		labels[0]="White Blood Cells (WBC)";
		labels[1]="Red Blood Cells (RBC)";
		labels[2]="Platelets (PLT)";
		labels[3]="All";
		gd.addRadioButtonGroup("Cells", labels, 4, 1, null);
		
		Panel box = (Panel) gd.getComponent(7);
		Checkbox wbc = (Checkbox) box.getComponent(0);
		Checkbox rbc = (Checkbox) box.getComponent(1);
		Checkbox plt = (Checkbox) box.getComponent(2);
		Checkbox all = (Checkbox) box.getComponent(3);
		
		wbc.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==1)
				{
					size_text.setText("0-1000");
					circularity_text.setText("0.02-0.03");
				}
			}
		});
		
		rbc.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==1)
				{
					size_text.setText("1000-2000");
					circularity_text.setText("0.04-0.5");
				}
			}
		});
	
		plt.addItemListener(new ItemListener() {
		
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==1)
				{
					size_text.setText("2000-3000");
					circularity_text.setText("0.5-0.9");
				}
			}
		});
		
		all.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==1)
				{
					size_text.setText(size);
					circularity_text.setText(circularity);
				}
			}
		});
		
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
			WBC = true; else WBC = false;
		if(option.equals(labels[1]))
			RBC = true; else RBC = false;
		if(option.equals(labels[2]))
			PLT = true; else PLT = false;
		if(option.equals(labels[3]))
			ALL= true; else ALL = false;
		
		return true;
	}
	
	private int calculteMeanCellSize(ResultsTable tab)
	{
		float area_tab [] = tab.getColumn(0);
		double sum=0;
		
		for (float f : area_tab) {
			sum+=f;
		}
		return (int)sum/area_tab.length;
	}
}
