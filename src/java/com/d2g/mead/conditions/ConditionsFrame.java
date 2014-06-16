package com.d2g.mead.conditions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.d2g.mead.forecast.ForecastDay;
import com.d2g.mead.weather.DataManager;
import com.d2g.mead.weather.FrameManager;
import com.d2g.mead.weather.Weather;
import com.d2g.mead.weather.WeatherFrame;


public class ConditionsFrame extends WeatherFrame {

	private static final long serialVersionUID = 1L;
	
	private int leftIndent = 100;
	private int radarWidth;
	private int radarHeight;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");
	private JLabel displayDate = new JLabel();
	private JLabel location = new JLabel();
	private JLabel temp = new JLabel();
	private JLabel dayLabel = new JLabel("Daytime:");
	private JTextArea dayFctText = new JTextArea(2,10);
	private JLabel nightLabel = new JLabel("Nighttime:");
	private JTextArea nightFctText = new JTextArea(2,10);
	private JLabel sunrise = new JLabel();
	private JLabel sunset = new JLabel();
	private JLabel daylight = new JLabel();
	private JLabel moonPhase = new JLabel();
	private Map<Integer,URL>moonPhaseUrlMap = new HashMap<Integer,URL>();
	private JLabel radarImage = new JLabel();

	public ConditionsFrame() {
		super();
		name="conditions";
		setCursor(getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null"));

		moonPhaseUrlMap.put(1,this.getClass().getClassLoader().getResource("resources/waxingCresent1.jpg"));
		moonPhaseUrlMap.put(2,this.getClass().getClassLoader().getResource("resources/waxingCresent2.jpg"));
		moonPhaseUrlMap.put(3,this.getClass().getClassLoader().getResource("resources/waxingCresent3.jpg"));
		moonPhaseUrlMap.put(4,this.getClass().getClassLoader().getResource("resources/waxingCresent4.jpg"));
		moonPhaseUrlMap.put(5,this.getClass().getClassLoader().getResource("resources/waxingCresent5.jpg"));
		moonPhaseUrlMap.put(6,this.getClass().getClassLoader().getResource("resources/waxingCresent6.jpg"));
		moonPhaseUrlMap.put(7,this.getClass().getClassLoader().getResource("resources/firstQuarter.jpg"));
		moonPhaseUrlMap.put(8,this.getClass().getClassLoader().getResource("resources/waxingGibbous1.jpg"));
		moonPhaseUrlMap.put(9,this.getClass().getClassLoader().getResource("resources/waxingGibbous2.jpg"));
		moonPhaseUrlMap.put(10,this.getClass().getClassLoader().getResource("resources/waxingGibbous2.jpg"));
		moonPhaseUrlMap.put(11,this.getClass().getClassLoader().getResource("resources/waxingGibbous3.jpg"));
		moonPhaseUrlMap.put(12,this.getClass().getClassLoader().getResource("resources/waxingGibbous4.jpg"));
		moonPhaseUrlMap.put(13,this.getClass().getClassLoader().getResource("resources/waxingGibbous4.jpg"));
		moonPhaseUrlMap.put(14,this.getClass().getClassLoader().getResource("resources/waxingGibbous5.jpg"));
		moonPhaseUrlMap.put(15,this.getClass().getClassLoader().getResource("resources/fullMoon.jpg"));
		moonPhaseUrlMap.put(16,this.getClass().getClassLoader().getResource("resources/waningGibbous5.jpg"));
		moonPhaseUrlMap.put(17,this.getClass().getClassLoader().getResource("resources/waningGibbous4.jpg"));
		moonPhaseUrlMap.put(18,this.getClass().getClassLoader().getResource("resources/waningGibbous3.jpg"));
		moonPhaseUrlMap.put(19,this.getClass().getClassLoader().getResource("resources/waningGibbous2.jpg"));
		moonPhaseUrlMap.put(20,this.getClass().getClassLoader().getResource("resources/waningGibbous1.jpg"));
		moonPhaseUrlMap.put(21,this.getClass().getClassLoader().getResource("resources/waningGibbous1.jpg"));
		moonPhaseUrlMap.put(22,this.getClass().getClassLoader().getResource("resources/lastQuarter.jpg"));
		moonPhaseUrlMap.put(23,this.getClass().getClassLoader().getResource("resources/waningCresent4.jpg"));
		moonPhaseUrlMap.put(24,this.getClass().getClassLoader().getResource("resources/waningCresent4.jpg"));
		moonPhaseUrlMap.put(25,this.getClass().getClassLoader().getResource("resources/waningCresent3.jpg"));
		moonPhaseUrlMap.put(26,this.getClass().getClassLoader().getResource("resources/waningCresent3.jpg"));
		moonPhaseUrlMap.put(27,this.getClass().getClassLoader().getResource("resources/waningCresent2.jpg"));
		moonPhaseUrlMap.put(28,this.getClass().getClassLoader().getResource("resources/waningCresent1.jpg"));
		moonPhaseUrlMap.put(29,this.getClass().getClassLoader().getResource("resources/waningCresent1.jpg"));
		moonPhaseUrlMap.put(30,this.getClass().getClassLoader().getResource("resources/newMoon.jpg"));

	}

	public void display() throws MalformedURLException {
		getContentPane().setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(0,leftIndent,0,0);
		if ( getPreferredSize().height>600 ) {
			c.gridy=0;
			add(displayDate, c);
			c.gridy=1;
			add(location,c);
			c.gridy=0;
			c.gridheight = 3;
			c.anchor = GridBagConstraints.EAST;
			add(moonPhase,c);
			c.gridy=1;
			c.gridheight = 1;
			c.anchor = GridBagConstraints.SOUTHWEST;
			add(sunrise,c);
			c.gridy=2;
			c.anchor = GridBagConstraints.NORTHWEST;
			add(sunset,c);
			c.insets = new Insets(0,leftIndent,-2,0);
			c.anchor = GridBagConstraints.SOUTHWEST;
			add(daylight,c);
			c.anchor = GridBagConstraints.CENTER;
			c.insets = new Insets(0,leftIndent,0,0);
		}
		add(temp,c); c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(15,leftIndent,0,5);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		add(dayLabel,c);
		c.gridx++;
		c.insets = new Insets(10,0,0,5);
		add(dayFctText,c);c.gridy++;
		c.insets = new Insets(10,leftIndent,0,5);
		c.gridx = 0;
		add(nightLabel,c);
		c.insets = new Insets(10,0,0,5);
		c.gridx++;
		add(nightFctText,c);c.gridy++;
		
		c.insets = new Insets(15,leftIndent,0,0);
		c.gridx=0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		radarImage.setPreferredSize(new Dimension(radarWidth,radarHeight));
		add(radarImage,c);


		pack();
		setVisible(true);
	}

	public void createComponents() {
		Font textFont = displayDate.getFont().deriveFont(20.0f);
		displayDate.setFont(displayDate.getFont().deriveFont(36.0f));
		displayDate.setForeground(Color.gray);
		displayDate.setHorizontalAlignment(JLabel.CENTER);

		sunrise.setForeground(Color.gray);
		sunrise.setFont(textFont.deriveFont(18.0f));
		sunset.setForeground(Color.gray);
		sunset.setFont(textFont.deriveFont(18.0f));
		daylight.setForeground(Color.gray);
		daylight.setFont(textFont.deriveFont(18.0f));

		location.setForeground(Color.gray);
		location.setFont(textFont.deriveFont(30.0f));

		temp.setForeground(Color.gray);
		temp.setFont(textFont.deriveFont(36.0f));

		dayLabel.setForeground(Color.gray);
		dayLabel.setFont(textFont);

		dayFctText.setLineWrap(true);
		dayFctText.setWrapStyleWord(true);
		dayFctText.setEditable(false);
		dayFctText.setBackground(Color.black);
		dayFctText.setBorder(null);
		dayFctText.setForeground(Color.gray);
		dayFctText.setFont(textFont);
		dayFctText.setPreferredSize(new Dimension(700,12));

		nightLabel.setForeground(Color.gray);
		nightLabel.setFont(textFont);

		nightFctText.setLineWrap(true);
		nightFctText.setWrapStyleWord(true);
		nightFctText.setEditable(false);
		nightFctText.setBackground(Color.black);
		nightFctText.setBorder(null);
		nightFctText.setForeground(Color.gray);
		nightFctText.setFont(textFont);
		nightFctText.setPreferredSize(new Dimension(700,12));
	}


	public void updateConditions(final Conditions conditions) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				displayDate.setText(dateFormat.format(new Date()));
				location.setText(conditions.getDisplay_location().getFull());
				temp.setText(conditions.getTemp_f_string() + "      " + conditions.getWeather());
			}
		});
	}

	public void updateForecast(final List<ForecastDay> forecastDays) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dayFctText.setText(forecastDays.get(0).getDayForecast().getFcttext());
				nightFctText.setText(forecastDays.get(0).getNightForecast().getFcttext());
			}
		});
	}
	
	public void updateRadarImage(final URL url) throws IOException {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ImageIcon icon = null;
				try {
					icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
					if ( icon==null || icon.getIconWidth()<=0 ) {
						icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource("resources/noImage.jpg")));
					}
				} catch (Exception e) {
					icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource("resources/noImage.jpg")));
				}
				radarImage.setIcon(icon);
			}});
	}
	
	public void updateAstronomy(final Astronomy astronomy) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sunrise.setText("sun rise: " + astronomy.getSunrise().getTime());
				sunset.setText("sun set : " + astronomy.getSunset().getTime());
				daylight.setText("daylight : " + astronomy.getDaylight());
				try {
					int urlInt = Integer.valueOf(astronomy.getMoon_phase().getAgeOfMoon());
					if ( moonPhaseUrlMap.containsKey(urlInt) ) {
						moonPhase.setIcon(new ImageIcon(moonPhaseUrlMap.get(urlInt)));
						moonPhase.setVisible(true);
					} else {
						moonPhase.setVisible(false);
					}
				} catch (NumberFormatException e) {
					moonPhase.setVisible(false);
				}
			}
		});
	}

	public int getRadarWidth() {
		return this.radarWidth;
	}

	public int getRadarHeight() {
		return this.radarHeight;
	}

	@Override
	public String getHtmlSettings() {
		Dimension size = getPreferredSize();
		Point location = getLocation();
		StringBuilder sb = new StringBuilder();
		sb.append("      <tr>\n");
		sb.append("        <td class=\"settingCell\">").append(name).append(".location=").append(location.x).append(",").append(location.y).append("</tr>\n");
		sb.append("        <td class=\"settingCell\">").append(name).append(".size=").append(size.width).append(",").append(size.height).append("</tr>\n");
		sb.append("        <td class=\"settingCell\">").append("radar.size=").append(radarWidth).append(",").append(radarHeight).append("</tr>\n");
		sb.append("      </tr>\n");
		return sb.toString();
	}

	@Override
	public void setProperties(final Properties userProperties, final FrameManager frameManager) {
		super.setProperties(userProperties, frameManager);
		if ( frameX<0 ) {
			frameX = 0;
		}
		if ( frameY<0 ) {
			frameY = 0;
		}
		setLocation(frameX,frameY);
		setPreferredSize(new Dimension(frameWidth,frameHeight));
		if ( getPreferredSize().width>0 ) {
			leftIndent = DataManager.getIntegerProperty("conditions.left.indent",
					Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenWidth() + "w.conditions.left.indent")));
			radarWidth = DataManager.getIntegerProperty("conditions.radar.width",
					Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenWidth() + "w.conditions.radar.width")));
			radarHeight = DataManager.getIntegerProperty("conditions.radar.height",
					Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenHeight() + "h.conditions.radar.height")));
		}
	}
}
