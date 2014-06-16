package com.d2g.mead.weather;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.d2g.mead.conditions.ConditionsFrame;
import com.d2g.mead.forecast.ForecastFrame;
import com.d2g.mead.hourly.HourlyFrame;


public class FrameManager {

	private static Logger logger = Logger.getLogger(FrameManager.class);
	
	//private static final List<Integer> defaultScreenWidths = Arrays.asList(new Integer[]{1920,1680,1368,1024,800});
	//private static final List<Integer> defaultScreenHeights = Arrays.asList(new Integer[]{1200,1080,1050,980,768,600});
	private static final SortedSet<Integer> defaultScreenWidths = new TreeSet<Integer>();
	private static final SortedSet<Integer> defaultScreenHeights = new TreeSet<Integer>();
	
	private int screenWidth;
	private int screenHeight;
	private String defaultScreenWidth;
	private String defaultScreenHeight;
	
	private ConditionsFrame conditionsFrame;
	private ForecastFrame forecastFrame;
	private HourlyFrame hourlyFrame;
	
	static {
		Pattern widthPattern = Pattern.compile("^(\\d*)w\\..*");
		Pattern heightPattern = Pattern.compile("^(\\d*)h\\..*");
		for ( Object key:Weather.defaultProperties.keySet() ) {
			Matcher widthMatcher = widthPattern.matcher((String) key);
			if ( widthMatcher.find() ) {
				defaultScreenWidths.add(Integer.parseInt(widthMatcher.group(1)));
			} else {
				Matcher heightMatcher = heightPattern.matcher((String) key);
				if ( heightMatcher.find() ) {
					defaultScreenHeights.add(Integer.parseInt(heightMatcher.group(1)));
				}
			}
		}
	}

	public FrameManager() {}
	
	public void init() throws IOException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = DataManager.getIntegerProperty("screen.width",-1);
		if ( screenWidth==-1) {
			screenWidth = screenSize.width;
		}
		for (int width:defaultScreenWidths) {
			if ( screenWidth>=width ) {
				defaultScreenWidth = String.valueOf(width);
			} else {
				break;
			}
		}
		screenHeight = DataManager.getIntegerProperty("screen.height",-1);
		if ( screenHeight==-1) {
			screenHeight = screenSize.height;
		}
		for (int height:defaultScreenHeights) {
			if ( screenHeight>=height ) {
				defaultScreenHeight = String.valueOf(height);
			} else {
				break;
			}
		}
		conditionsFrame = new ConditionsFrame();
		conditionsFrame.setProperties(Weather.properties, this);
		if ( conditionsFrame.getPreferredSize().width==0 ) {
			conditionsFrame = null;
		} else {
			conditionsFrame.createComponents();
		}
		forecastFrame = new ForecastFrame();
		forecastFrame.setProperties(Weather.properties, this);
		forecastFrame.createComponents();
		if ( forecastFrame.getPreferredSize().width==0 || forecastFrame.getPreferredSize().height==0 ) {
			forecastFrame.setVisible(false);
		}
		hourlyFrame = new HourlyFrame();
		hourlyFrame.setProperties(Weather.properties, this);
		if ( hourlyFrame.getPreferredSize().width==0 || hourlyFrame.getPreferredSize().height==0 ) {
			hourlyFrame = null;
		} else {
			hourlyFrame.createComponents();
		}
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}
	
	public String getDefaultScreenWidth() {
		return defaultScreenWidth;
	}

	public String getDefaultScreenHeight() {
		return defaultScreenHeight;
	}

	public ConditionsFrame getConditionsFrame() {
		return conditionsFrame;
	}

	public ForecastFrame getForecastFrame() {
		return forecastFrame;
	}

	public HourlyFrame getHourlyFrame() {
		return hourlyFrame;
	}
	
}
