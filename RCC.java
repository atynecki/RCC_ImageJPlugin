import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import ij.plugin.frame.*;

public class RCC extends PlugInFrame implements ActionListener {	
	private Panel panel;
	private int previousID;
	private static Frame instance;
	
	public RCC() {
		super("Red cell counter");
		
		if (instance!=null) {
			instance.toFront();
			return;
		}
		instance = this;
		addKeyListener(IJ.getInstance());
		
		setLayout(new FlowLayout());
		panel = new Panel();
		panel.setLayout(new GridLayout(3, 1, 5, 5));
		addButton("Image load");
		addButton("Analyze");
		addButton("Save result");
		add(panel);
		
		pack();
		GUI.center(this);
		setVisible(true);
	}
	
	void addButton(String label) {
		Button b = new Button(label);
		b.addActionListener(this);
		b.addKeyListener(IJ.getInstance());
		panel.add(b);
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

