import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.io.SaveDialog;
import ij.plugin.frame.*;
import ij.plugin.ImageCalculator;
import ij.plugin.Duplicator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RCC extends PlugInFrame implements ActionListener {	
	private Panel panel;
	private static Frame instance;
	private ImagePlus image = new ImagePlus();
	public static ImagePlus image1 = new ImagePlus();
	public static ImagePlus image2 = new ImagePlus();
	
	private static int frame_width = 400;
	private static int frame_height = 400;
	
	public RCC() {
		super("Red cell counter");
		
		if (instance!=null) {
			instance.toFront();
			return;
		}
		instance = this;
		addKeyListener(IJ.getInstance());
		
		/** SET VIEW */
		this.setSize((int) frame_width,(int) frame_height);
		
		setLayout(new FlowLayout());
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new GridLayout(2, 1, 5, 5));
		buttonPanel.add(addButton("Analyze"));
		buttonPanel.add(addButton("Save result"));
		add(buttonPanel);
		
		Panel parametersPanel = new Panel();
		parametersPanel.setLayout(new GridLayout(2, 1, 5, 5));
		parametersPanel.add(addParameterField("parameter1", 200, 15));
		parametersPanel.add(addParameterField("parameter2", 200, 15));
		add(parametersPanel);
		
		/** GET ACTIVE IMAGE */
		image = IJ.getImage();
				
		GUI.center(this);
		setVisible(true);
	}
	
	Button addButton(String label) {
		Button button = new Button(label);
		button.addActionListener(this);
		button.addKeyListener(IJ.getInstance());
		
		return button;
	}
	
	Panel addParameterField(String label, double defaultValue, int digits) {
		Panel parameterFieldPanel = new Panel();
		Label labelName = new Label(label);
		TextField textField = new TextField(digits);
		
		parameterFieldPanel.setLayout(new GridBagLayout());
		GridBagConstraints d = new GridBagConstraints();
		d.insets = new Insets(5, 10, 5, 5);
		d.gridx = 0;
		d.gridy = 0;
		d.ipady = 15;
		parameterFieldPanel.add(labelName, d);
		d.gridx = 1;
		d.gridy = 0;
		d.ipady = 15;
		parameterFieldPanel.add(textField, d);
		textField.setText(String.valueOf(defaultValue));
		
		return parameterFieldPanel;
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		Object actionSource = e.getSource();
		
		if(actionCommand.equals("Analyze")){
			ImageProcessing.ImageProcess(image);
							
		}
		else if(actionCommand.equals("Save result")) {
			SaveDialog saveImage = new SaveDialog("Save image...", "image", ".jpg");
			
			this.close();
		}
	}

	private static class ImageProcessing
	{
		public ImageProcessing() {
			}
		
		public static ImagePlus ImageProcess(ImagePlus img) {
						
			IJ.run(img, "8-bit", "");
									
			int sigma = 3; // do pobrania z aplikacji, wartosc do Gaussian Blur, jak bardzo ma byæ rozmyte
			
			for (int i = 0; i < sigma; i++)
			{ 
			  IJ.run(img, "Gaussian Blur...", "Sigma = 1"); 
			}
				
				IJ.run(img, "Make Binary", "");
				IJ.run(img, "Fill Holes", "");
				IJ.run(img, "Watershed", "");
				IJ.run(img, "Analyze Particles...", "size=0-infinity circularity=0.00-1.00 show=[Overlay Outlines] display record slice");
				img.show();
					
			
			
			return img;
		}
	}//ImageProcessing inner class
}

