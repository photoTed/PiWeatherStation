package com.d2g.mead.hourly;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.d2g.mead.weather.DataManager;
import com.d2g.mead.weather.FrameManager;
import com.d2g.mead.weather.Weather;
import com.d2g.mead.weather.WeatherFrame;


public class HourlyFrame extends WeatherFrame {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(HourlyFrame.class);

	private int rowCount;
	private int textWidth;
	private int textHeight;
	private JLabel title = new JLabel("Hourly Forecasts");
	private JLabel wuLogo = new JLabel();
	private List<JLabel> dayList = new LinkedList<JLabel>();
	private List<JLabel> timeList = new LinkedList<JLabel>();
	private List<JLabel> tempList = new LinkedList<JLabel>();
	private List<JLabel> iconList = new LinkedList<JLabel>();
	private List<JLabel> conditionsList = new LinkedList<JLabel>();
	private List<JTextArea> textList = new LinkedList<JTextArea>();
	private FontMetrics metrics;

	
	public HourlyFrame() {
		super();
		name="hourly";
		setCursor(getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null"));
		setLocation(980,0);
		setPreferredSize(new Dimension(700,900));
	}

	public void display() throws MalformedURLException {
		displayX();
//		while ( dayList.get(0).getLocation().x==0 ) {
//			this.remove(title);
//			this.remove(wuLogo);
//			@SuppressWarnings("unchecked")
//			List<List<JLabel>> lists = Arrays.asList(dayList,timeList,tempList,iconList,conditionsList);
//			for ( List<JLabel>list:lists ) {
//				for ( JLabel jlabel:list ) {
//					this.remove(jlabel);
//				}
//			}
//			for ( JTextArea temp:textList ) {
//				this.remove(temp);
//			}
//			rowCount--;
//			dayList = new LinkedList<JLabel>();
//			timeList = new LinkedList<JLabel>();
//			tempList = new LinkedList<JLabel>();
//			iconList = new LinkedList<JLabel>();
//			conditionsList = new LinkedList<JLabel>();
//			textList = new LinkedList<JTextArea>();
//			createComponents();
//			updateComponents(this.hourlyForecasts);
//			displayX();
//		}
	}
	
	private void displayX() throws MalformedURLException {
		getContentPane().setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		Font textFont = title.getFont().deriveFont(18.0f);
		c.gridwidth = 4;
		c.insets = new Insets(0,0,5,0);
		title.setFont(textFont.deriveFont(Font.ITALIC).deriveFont(28.0f));
		title.setForeground(Color.gray);
		add(title,c);
		
		wuLogo.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("resources/wundergroundLogo.jpg")));
		c.anchor = GridBagConstraints.NORTHEAST;
		add(wuLogo,c);
		c.gridx = 0;

		c.gridwidth = 1;

		for ( int i=0;i<rowCount;i++ )  {
			c.insets = new Insets(16,0,0,10);
			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridy++;
			c.gridx=0;
			c.gridheight=1;
			add(dayList.get(i),c);
			c.gridx++;
			add(timeList.get(i),c);
			c.gridy++;
			c.insets = new Insets(0,0,0,10);
			add(tempList.get(i),c);
			c.gridy--;
			c.gridx++;
			c.anchor = GridBagConstraints.WEST;
			add(iconList.get(i),c);
			c.gridy++;
			add(conditionsList.get(i),c);
			c.gridy--;
			c.gridheight=2;
			c.gridx++;
			add(textList.get(i),c);
			c.gridy++;
		}

		pack();
		setVisible(true);
	}

	public void createComponents() {
		JLabel tempLabel = new JLabel();
		Font textFont = tempLabel.getFont().deriveFont(18.0f);
		metrics = tempLabel.getFontMetrics(textFont);
		for ( int i=0;i<rowCount;i++ )  {
			JLabel day = new JLabel();
			day.setFont(textFont.deriveFont(24.0f).deriveFont(Font.ITALIC));
			day.setForeground(Color.gray);
			dayList.add(day);

			JLabel time = new JLabel();
			time.setFont(textFont.deriveFont(24.0f));
			time.setForeground(Color.gray);
			timeList.add(time);

			JLabel temp = new JLabel();
			temp.setFont(textFont.deriveFont(24.0f));
			temp.setForeground(Color.gray);
			tempList.add(temp);

			JLabel icon = new JLabel();
			iconList.add(icon);
			
			JLabel conditions = new JLabel();
			conditions.setFont(textFont.deriveFont(Font.ITALIC).deriveFont(16.0f));
			conditions.setForeground(Color.gray);
			conditionsList.add(conditions);
			
			JTextArea text = new JTextArea();
			
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			text.setEditable(false);
			text.setBackground(Color.black);
			text.setBorder(null);
			text.setForeground(Color.gray);
			text.setFont(textFont);
			text.setPreferredSize(new Dimension(textWidth,textHeight * 2));

			textList.add(text);
		}
	}

	public void updateComponents(final List<HourlyForecast> hourlyForecasts) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					int hourlyIndex = 0;
					int lastDay = 0;
					for ( int i=0;i<rowCount;i++ )  {
						HourlyForecast hourlyForecast = hourlyForecasts.get(hourlyIndex);
						hourlyIndex++;
						if ( i>2 ) {
							hourlyIndex++;
						}
						if ( i>5 ) {
							hourlyIndex++;
						}
						if ( lastDay != (hourlyForecast.getFCTTIME().getMday())) {
							dayList.get(i).setText(hourlyForecast.getFCTTIME().getWeekday_name_abbrev());
							lastDay = hourlyForecast.getFCTTIME().getMday();
						} else {
							dayList.get(i).setText("");
						}
						timeList.get(i).setText(hourlyForecast.getFCTTIME().getCivil());
						String windchillString = "[" + hourlyForecast.getWindchill().getEnglishString() + "]";
						if ( hourlyForecast.getWindchill().getEnglishString().equals("") ) {
							windchillString = "";
						}
						tempList.get(i).setText(hourlyForecast.getTemp().getEnglishString() + " " + windchillString);
						URL url = new URL(hourlyForecast.getIcon_url());
						iconList.get(i).setIcon(new ImageIcon(url));
						conditionsList.get(i).setText(hourlyForecast.getCondition());
						textList.get(i).setText(hourlyForecast.getWxPlus());
						if ( metrics.stringWidth(hourlyForecast.getWxPlus())>textWidth ) {
							textList.get(i).setPreferredSize(new Dimension(textWidth,textHeight * 2));
						} else {
							textList.get(i).setPreferredSize(new Dimension(textWidth,textHeight));
						}
					}
				} catch (MalformedURLException e) {
					logger.error(e);
				}
			}
		});
	}

	@Override
	public void setProperties(Properties properties, final FrameManager frameManager) {
		super.setProperties(properties, frameManager);
		if ( frameX<0 ) {
			frameX = frameManager.getConditionsFrame().getFrameX() + frameManager.getConditionsFrame().getFrameWidth();
		}
		if ( frameY<0 ) {
			frameY = 0;
		}
		if ( frameWidth<0 ) {
			frameWidth = Math.max(frameManager.getScreenWidth()-frameX,0);
		}
		if ( frameHeight<0 ) {
			frameHeight = frameManager.getForecastFrame().getFrameY();
		}
		setLocation(frameX,frameY);
		setPreferredSize(new Dimension(frameWidth,frameHeight));
		if ( getPreferredSize().width>0 ) {
			textWidth = DataManager.getIntegerProperty("hourly.text.width",
					Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenWidth() + "w.hourly.text.width")));
			rowCount = DataManager.getIntegerProperty("hourly.row.count",
					Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenHeight() + "h.hourly.row.count")));
			textHeight = DataManager.getIntegerProperty("hourly.text.height",
					Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenHeight() + "h.hourly.text.height")));
		}
	}

}
