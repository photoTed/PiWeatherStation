package com.d2g.mead.forecast;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import com.d2g.mead.weather.DataManager;
import com.d2g.mead.weather.FrameManager;
import com.d2g.mead.weather.Weather;
import com.d2g.mead.weather.WeatherFrame;


public class ForecastFrame extends WeatherFrame {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ForecastFrame.class);

	SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");
	private JLabel displayDate = new JLabel();
	private List<JLabel> dayList = new LinkedList<JLabel>();
	private List<JLabel> highTempList = new LinkedList<JLabel>();
	private List<JLabel> lowTempList = new LinkedList<JLabel>();
	private List<JLabel> iconList = new LinkedList<JLabel>();
	private List<JLabel> conditionsL1List = new LinkedList<JLabel>();
	private List<JLabel> conditionsL2List = new LinkedList<JLabel>();
	private boolean visible = true;
	private int columnCount = 7;
	
	public ForecastFrame() {
		super();
		name = "forecast";
		setCursor(getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null"));
	}

	public void display() throws MalformedURLException {
		getContentPane().setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = -1;
		c.gridy = 0;
		Font textFont = displayDate.getFont().deriveFont(18.0f);
		int leftInset = 0;

		for ( int i=0;i<columnCount;i++ ) {
			c.insets = new Insets(0,leftInset,0,5);
			c.anchor = GridBagConstraints.CENTER;
			c.gridx++;
			c.gridy=0;
			add(dayList.get(i),c);
			c.gridy++;
			add(highTempList.get(i),c);
			c.gridy++;
			add(lowTempList.get(i),c);
			c.gridy=0;
			c.gridx++;
			c.insets = new Insets(0,3,0,8);
			add(iconList.get(i),c);
			c.gridy++;
			c.anchor = GridBagConstraints.SOUTH;
			add(conditionsL1List.get(i),c);
			c.gridy++;
			c.anchor = GridBagConstraints.NORTH;
			add(conditionsL2List.get(i),c);
			leftInset = 70;
		}

		pack();
		setVisible(visible);
	}

	public void createComponents() {
		JLabel tempLabel = new JLabel();
		Font textFont = tempLabel.getFont();

		for ( int i=0;i<columnCount;i++ ) {
			JLabel day = new JLabel();
			day.setFont(textFont.deriveFont(24.0f).deriveFont(Font.ITALIC));
			day.setForeground(Color.gray);
			dayList.add(day);
			
			JLabel highTemp = new JLabel();
			highTemp.setFont(textFont.deriveFont(22.0f));
			highTemp.setForeground(Color.gray);
			highTempList.add(highTemp);
			
			JLabel lowTemp = new JLabel();
			lowTemp.setFont(textFont.deriveFont(22.0f));
			lowTemp.setForeground(Color.gray);
			lowTempList.add(lowTemp);
			
			JLabel icon = new JLabel();
			iconList.add(icon);
			
			JLabel conditionsL1 = new JLabel();
			conditionsL1.setFont(textFont.deriveFont(18.0f).deriveFont(Font.ITALIC));
			conditionsL1.setForeground(Color.gray);
			conditionsL1List.add(conditionsL1);

			JLabel conditionsL2 = new JLabel();
			conditionsL2.setFont(textFont.deriveFont(18.0f).deriveFont(Font.ITALIC));
			conditionsL2.setForeground(Color.gray);
			conditionsL2List.add(conditionsL2);
		}

	}

	public void updateComponents(final List<ForecastDay> forecastDays) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					for ( int i=0;i<Math.min(columnCount,forecastDays.size()-1);i++ ) {
						ForecastDay forecastDay = forecastDays.get(i+1);
						dayList.get(i).setText(forecastDay.getDate().getWeekday().substring(0,3));
						highTempList.get(i).setText("H: " + forecastDay.getHigh() + "\u00b0F");
						lowTempList.get(i).setText("L: " + forecastDay.getLow() + "\u00b0F");
						URL url = new URL(forecastDay.getIcon_url());
						iconList.get(i).setIcon(new ImageIcon(url));
						StringBuilder conditions1 = new StringBuilder();
						StringBuilder conditions2 = new StringBuilder();
						String[] conditions = forecastDay.getConditions().split(" ");
						String separator = "";
						for ( int i1=0;i1<conditions.length/2;i1++ ) {
							conditions1.append(separator).append(conditions[i1]);
							separator = " ";
						}
						separator = "";
						for ( int i1=(conditions.length/2);i1<conditions.length;i1++ ) {
							conditions2.append(separator).append(conditions[i1]);
							separator = " ";
						}			
						conditionsL1List.get(i).setText(conditions1.toString());
						conditionsL2List.get(i).setText(conditions2.toString());
					}
				} catch (MalformedURLException e) {
					logger.error(e);
				}
			}
		});

	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.visible = visible;
	}

	@Override
	public void setProperties(Properties properties, final FrameManager frameManager) {
		super.setProperties(properties, frameManager);
		if ( frameX<0 ) {
			frameX = 0;
		}
		if ( frameY<0 ) {
			frameY = frameManager.getConditionsFrame().getFrameY() + frameManager.getConditionsFrame().getFrameHeight();
		}
		if ( frameWidth<0 ) {
			frameWidth = frameManager.getScreenWidth();
		}
		if ( frameHeight<0 ) {
			frameHeight = Math.max(frameManager.getScreenHeight()-frameY,0);
		}
		setLocation(frameX,frameY);
		setPreferredSize(new Dimension(frameWidth,frameHeight));
		if ( getPreferredSize().width>0 && getPreferredSize().height>0 ) {
			columnCount = DataManager.getIntegerProperty("forecast.column.count",
					Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenWidth() + "w.forecast.column.count")));
		}
	}

}
