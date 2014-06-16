package com.d2g.mead.test;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class JFrameTest {

	public void createAndShowGUI() {
		JFrame frame = new JFrame("Pi Weather Station Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(400,300));
		frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.insets = new Insets(20,0,20,0);
        c.anchor = GridBagConstraints.CENTER;
		frame.getContentPane().add(new JLabel("Testing swing components"),c);
		ActionListener quitListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                    System.exit(0);;
            }
		};

		JButton quitButton = new JButton("Quit");
		frame.getRootPane().setDefaultButton(quitButton);
		c.gridy++;
        c.anchor = GridBagConstraints.SOUTH;
		quitButton.addActionListener(quitListener);
		frame.add(quitButton,c);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void runTest() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	UIManager.put("swing.boldMetal", Boolean.FALSE);
            	createAndShowGUI();
            }
        });
	}
	
	public static void main(String[] args) {
    	JFrameTest jFrameTest = new JFrameTest();
    	jFrameTest.runTest();
	}
}
