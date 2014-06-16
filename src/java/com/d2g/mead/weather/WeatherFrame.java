package com.d2g.mead.weather;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Properties;

import javax.swing.JFrame;

/**
 * 
 * @author Ted Mead
 *
 * An abstract class provides structure to other classes in your application.
 * You cannot instantiate an abstract class, it has two purposes:
 * <ul>
 * <li>It provides a single place to store code that is used by
 * all the subclasses.</li>
 * <i>By the use of abstract methods it forces all subclasses to have
 * that method.
 * </ul>
 * When you create subclasses of a super class you may define the variable
 * as being of the superclass type, e.g.
 * 
 * WeatherFrame myFrame = new ConditionsFrame();
 * 
 * or you can create a list to hold objects of the WeatherFrame class that
 * can contain any objects that inherit (are subclasses of) WeatherFrame. If
 * you iterate over that list you may call any method in the superclass or
 * any abstract method of the superclass because you know that all subclasses
 * must have that method.  Each subclass can do different things in the program
 * but the method must exists, must take the same parameters, and must return 
 * the same type of object (or void for none);
 */
public abstract class WeatherFrame  extends JFrame {

	private static final long serialVersionUID = 1L;
	protected String name;
	protected int frameX;
	protected int frameY;
	protected int frameWidth;
	protected int frameHeight;

	public void setProperties( Properties userProperties, FrameManager frameManager) {
		if ( Weather.properties.getProperty(name + ".location.x")!=null ) {
			frameX = Integer.parseInt(Weather.properties.getProperty(name + ".location.x").replaceAll("\\D", ""));
		} else {
			frameX = Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenWidth() + "w." + name + ".location.x","-1"));
		}
		if ( Weather.properties.getProperty(name + ".location.y")!=null ) {
			frameY = Integer.parseInt(Weather.properties.getProperty(name + ".location.y").replaceAll("\\D", ""));
		} else {
			frameY = Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenHeight() + "h." + name + ".location.y","-1"));
		}
		if ( Weather.properties.getProperty(name + ".width")!=null ) {
			frameWidth = Integer.parseInt(Weather.properties.getProperty(name + ".width").replaceAll("\\D", ""));
		} else {
			frameWidth = Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenWidth() + "w." + name + ".width","-1"));
		}
		if ( Weather.properties.getProperty(name + ".height")!=null ) {
			frameHeight = Integer.parseInt(Weather.properties.getProperty(name + ".height").replaceAll("\\D", ""));
		} else {
			frameHeight = Integer.parseInt(Weather.defaultProperties.getProperty(frameManager.getDefaultScreenHeight() + "h." + name + ".height","-1"));
		}
	}
	
	public String getHtmlSettings() {
		Dimension size = getPreferredSize();
		Point location = getLocation();
		StringBuilder sb = new StringBuilder();
		sb.append("      <tr>\n");
		sb.append("        <td class=\"settingCell\">").append(name).append(".location=").append(location.x).append(",").append(location.y).append("</tr>\n");
		sb.append("        <td class=\"settingCell\">").append(name).append(".size=").append(size.width).append(",").append(size.height).append("</tr>\n");
		sb.append("      </tr>\n");
		return sb.toString();
	}
	
	public int getFrameX() {
		return frameX;
	}

	public int getFrameY() {
		return frameY;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

}
