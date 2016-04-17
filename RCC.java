import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.frame.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RCC extends PlugInFrame implements ActionListener {	
	private Panel panel;
	private static Frame instance;
	private ImagePlus image = new ImagePlus();
	
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
			
		}
		else if(actionCommand.equals("Save result")) {
			this.close();
		}
	}

	private class ImageProcessing
	{
		ImageProcessing() {
			
		}
	}//ImageProcessing inner class
}

