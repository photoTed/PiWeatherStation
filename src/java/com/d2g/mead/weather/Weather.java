package com.d2g.mead.weather;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.d2g.mead.test.JFrameTest;

/**
 * 
 * @author Ted Mead
 * 
 * The initializing class of the application.
 * 
 * The main method needs either three parameters:
 *    state (or country) the location for the weather report,
 *    city
 *    weatherKey  the id provided by www.weather.com for access to their api data
 *
 * <b>OR</b>
 * 
 * a single parameter that is the path to a property file the contains the
 * above data and other optional values to control the running of the program
 * 
 * <b>OR</b>
 * 
 * No parameters, in which case the program expects to find a property file named
 * <b>weather.properties</b> in the same folder as the Weather.jar file.
 *  
 */

public class Weather {

	private static Logger logger = Logger.getLogger(Weather.class);
	
	/*
	 * A static variable is created once when a class is first instantiated.
	 * Even if many instances of the class are instantiated there is one and
	 * only one static value. Newbies tend to overuse static variables to 
	 * hold a value to span across all instances of a class, such as a static
	 * list to keep a reference to all the instances.  In general that is not
	 * a good idea; create another class that holds that list.
	 * 
	 * Making the two properties values static is a slight cheat so they can
	 * be referenced anywhere in the application -- global variables.
	 * 
	 * As these properties never change it seems like a reasonable compromise.
	 */
	public static Properties properties = new Properties();
	public static Properties defaultProperties = new Properties();
	
	private static String weatherKey = null;
	private static String state = null;
	private static String city = null;
	private static String weatherMaster = null;
	
	static {
		try {
			defaultProperties.load(Weather.class.getClassLoader().getResourceAsStream("resources/default.properties"));
		} catch (IOException e){
			logger.error("Could not load default properties \"resources/default.properties\"");
		}
	}

	public static void main(String[] args) throws Exception {
		if ( args.length==1 && args[0].toLowerCase().equals("test") ) {
			JFrameTest jFrameTest = new JFrameTest();
			jFrameTest.runTest();
		} else {
			launchApplication(args);
		}
	}

	public static void launchApplication(String[] args) throws Exception {
		try {
			Logger rootLogger = Logger.getRootLogger();
			if (!rootLogger.getAllAppenders().hasMoreElements()) {
				rootLogger.setLevel(Level.ERROR);
			}
			rootLogger.setLevel(Level.DEBUG);
			//rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d %-5p %m%n")));
			String logFilename = System.getProperty("java.io.tmpdir") + "/PiWeatherStation.log";
			RollingFileAppender rollingFileAppender = new RollingFileAppender(new PatternLayout("%d %-5p %m%n"),logFilename,true);
			rollingFileAppender.setMaxFileSize("5MB");
			rollingFileAppender.setMaxBackupIndex(1);
			rootLogger.addAppender(rollingFileAppender);
			logger.info("**************************************");
			logger.info("Started Pi Weather Station Application");
			logger.info("Log File: " + logFilename);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			logger.info("Screen: " + screenSize.width + " x " + screenSize.height);
		} catch (FileNotFoundException e) {
			System.out.println();
			System.out.println();
			System.out.println("**********************************************************");
			System.out.println("Could not write to the log file /tmp/PiWeatherStation.log.");
			System.out.println("Check ownership of the file and change with command:");
			System.out.println();
			System.out.println("  sudo chown pi:pi /tmp/PiWeatherStation.log");
			System.out.println();
			System.out.println("**********************************************************");
			System.out.println();			
		}
		try {
			File propertyFile = null;
			if ( args.length==0 ) {
				propertyFile = new File("weather.properties");
				if ( propertyFile.exists() ) {
					properties.load(new FileInputStream(propertyFile));
				} else {
					printHelp();
					logger.error("  ERROR");
					logger.error("  =====");
					logger.error("");
					logger.error("Cannot find the property file \"weather.properties\"");
					propertyError();
					System.exit(9);
				}
			} else if ( args.length==1 ) {
				if ( args[0].toLowerCase().endsWith("help") || args[0].equalsIgnoreCase("-h") ) {
					printHelp();
				}
				propertyFile = new File(args[0]);
				if ( propertyFile.exists() ) {
					properties.load(new FileInputStream(propertyFile));
				} else {
					logger.error("The argument " + args[0] + " must be the name of a property file");
					logger.error("");
					propertyError();
					System.exit(9);
				}
			} else {
				processArguments(args);
			}
			if ( propertyFile!=null ) {
				state = properties.getProperty("state");
				city = properties.getProperty("city");
				weatherKey = properties.getProperty("weather.key");
				weatherMaster = properties.getProperty("weather.master");
			}
			if ( (weatherMaster==null && (state==null || city==null || weatherKey==null)) ) {
				logger.error("Error determining critical parameters:");
				logger.error("");
				logger.error("state:         " + state);
				logger.error("city           " + city);
				logger.error("weather.key     " + weatherKey);
				logger.error("weather.master " + weatherMaster);
				logger.error("");
				logger.error("Exiting.");
				System.exit(9);
			} else {
				logger.info("state: " + state + "  city: " +city + "  weather.key: " + weatherKey);
			}
			//new Weather();
			try {
				boolean set = false;
				for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						set = true;
						break;
					}
				}
				if ( !set ) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			final FrameManager frameManager = new FrameManager();
			frameManager.init();
			DataManager dataManager;
			if ( weatherMaster!=null ) {
				dataManager = new DataManager(weatherMaster);
			} else {
				dataManager = new DataManager(state, city, weatherKey);
			}
			dataManager.setFrameManager(frameManager);
			dataManager.init();
			dataManager.getDataFeeds().get("astronomy").update(true);
			dataManager.getDataFeeds().get("conditions").update(true);
			dataManager.getDataFeeds().get("forecast").update(true);
			dataManager.getDataFeeds().get("hourly").update(true);
			dataManager.getDataFeeds().get("radar").update(true);

			WebServer refreshServer = new WebServer(dataManager);
			refreshServer.create(DataManager.getIntegerProperty("server.port", Integer.valueOf(defaultProperties.getProperty("server.port"))));

			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						if ( frameManager.getConditionsFrame()!=null) {
							frameManager.getConditionsFrame().display();
						}
						if ( frameManager.getForecastFrame()!=null) {
							frameManager.getForecastFrame().display();
						}
						if ( frameManager.getHourlyFrame()!=null ) {
							frameManager.getHourlyFrame().display();
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (RuntimeException e) {
			logger.error("Runttime Exception:", e);
		}

	}

	private static void processArguments(String[] args) {
		for ( int i=0;i<args.length-1;i++ ) {
			if ( args[i].equals("-w") ) {
				weatherKey = args[++i];
			} else if ( args[i].equals("-s") ) {
				state = args[++i];
			} else if ( args[i].equals("-c") ) {
				city = args[++i];
			} else if ( args[i].equals("-m") ) {
				weatherMaster = args[++i];
			} else {
				printHelp();
				System.exit(9);
			}
		}
	}

	private static void propertyError() {
		logger.info("It must be located in the same folder as the jar file and ");
		logger.info("java must be run from that location");
		logger.info("   OR");
		logger.info("put the full path to the property file on the command line, e.g.");
		logger.info("   java /home/ted/myWeather.properties");
		logger.info("where you have stored your properties in the file named");
		logger.info("   myWeather.properties");
		logger.info("in the folder named");
		logger.info("   /home/ted");
		logger.info("");
	}

	private static void printHelp() {
		logger.info("Weather Station Application");
		logger.info("");
		logger.info("There are four ways to start the application.");
		logger.info("");
		logger.info("1. Include parameters on the command line:");
		logger.info("    -s [state(or Country)]");
		logger.info("       used by the weather api to query for your location.");
		logger.info("    -c [city]");
		logger.info("       the location you want the reports for.");
		logger.info("    -w [Weather Key]");
		logger.info("       your private ID that you get from www.weather.com");
		logger.info("       needed to be allowed to use their API.");
		logger.info("");
		logger.info("2. Include a master station ip address on the command line.");
		logger.info("    -m [master ip address]");
		logger.info("");
		logger.info("3. Create a file named \"weather.properties\" in the same folder");
		logger.info("   as the Weather.jar file.  That file must have the three properties");
		logger.info("   list above named state, city, and weather.key");
		logger.info("");
		logger.info("4. Create a property file named anything you like stored anywhere");
		logger.info("   you like.  Include the file path as a parameter on the command ");
		logger.info("   to start the application");
		logger.info("");
		System.exit(9);
	}
}
