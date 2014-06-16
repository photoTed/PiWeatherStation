package com.d2g.mead.weather;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServer {
	/**
	 * This class uses the Sun httpserver classes to implement a simple web
	 * server to give an outside interface to the Pi Weather Station application.
	 * 
	 * It started as a way to force one or more of the feeds to refresh,
	 * then grew to be the way two instances of the application can 
	 * interact in a master/slave setup.  The master updates from the
	 * weather site, then sends an event update notification to all
	 * slaves that have registered themselves as listeners.  On receiving
	 * the event update, each slave updates its feed by requesting the
	 * file from the master.
	 * 
	 * This emulates a standard listener pattern in Java that allows one
	 * process to update any number of other processes or systems that 
	 * have the method(s) required to receive listener events.  The way
	 * Java guarantees the listeners have the required methods is with 
	 * the "implements" clause on the class which identifies one (or more)
	 * "interfaces" that the class implements.  An interface just identifies
	 * all the methods the implementing class must have in order to implement
	 * the interface.  Then any other class that wants to notify other classes
	 * of an event has an addListener(<Interface-Name> listener) method that
	 * collects any and all classes that want to know of an event.  When the
	 * event occurs, the notifying class iterates over the listener collection
	 * calling one of the methods imposed by the interface.  The notifying 
	 * class has no idea what any one listener will do with the event notification
	 * and does not need to know.
	 * 
	 * Example:
	 * ========
	 * 
	 * public interface MyListerner {
	 *   
	 *   public void eventHappened(String event);
	 * 
	 * 
	 * public class MyClass1 implements MyListener {
	 * 
	 *    public void methodA() {
	 *       blah;
	 *       blah;
	 *    }
	 * 
	 *    public void methodB() {
	 *       blah;
	 *       blah;
	 *    }
	 *   
	 *    public void eventHappened(String event) {
	 *       do something when the event happens
	 *  
	 * }
	 * 
	 * 
	 * public class MyClass2 implements MyListener {
	 * 
	 *    public void methodX() {
	 *       foo;
	 *       foo;
	 *    }
	 * 
	 *    public void methodY() {
	 *       bar;
	 *       bar;
	 *    }
	 *   
	 *    public void eventHappened(String event) {
	 *       do something when the event happens
	 * }
	 *     
	 * public class MyNotifier {
	 * 
	 *    private Set<MyListeners> listeners = new HashSet<MyListeners>(); 
	 * 
	 *    public void addListener(<MyListener> listener) {
	 *       listeners.add(listener);
	 *    }
	 *    
	 *    private void notifyListeners(String event) {
	 *       for (MyListener listener:listeners) {
	 *          listener.eventHappened(event)
	 *       }
	 *       
	 * The any class that implements the interface MyListener MUST have
	 * the method eventHappened that takes a String parameter.  It does
	 * not matter what it does in that method nor does it matter what
	 * other methods are part of that class.
	 * 
	 * When the class that collects the listeners needs to tell them
	 * the event happened, it iterates over them making the call to 
	 * the method agreed to in the interface, in this case eventHappened.
	 * 
	 * Sometimes an interface is called a "contract", that is, a commitment
	 * by any class that implements it to have the agreed to method(s).
	 */
	
	private static Logger logger = Logger.getLogger(WebServer.class);

    public static final String HTML_HEAD = 
    		  "<html>\n  <head>\n"
            + "    <style>\n"
            + "      .tableCell {\n"
            + "        text-align: left;\n"
            + "        text-decoration : none;\n"
            + "        font-size: 100%;\n"
            + "        padding: 5px 15px 5px 15px;\n"
            + "        border-style: inset;\n"
            + "        border-width : 2px 2px 2px 2px;\n"
            + "        vertical-align: top;\n"
            + "        color: black;\n"
            + "      }\n"
            + "      .settingsCell {\n"
            + "        text-align: left;\n"
            + "        text-decoration : none;\n"
            + "        font-size: 100%;\n"
            + "        padding: 6px 0px 6px 0px;\n"
            + "        border-style: none;\n"
            + "        border-width : 0px;\n"
            + "        vertical-align: top;\n"
            + "        color: black;\n"
            + "      }\n"
            + "      .tableCellLeft {\n"
            + "        text-align: left;\n"
            + "        float: left;\n"
            + "      }\n"
            + "      .tableCellRight {\n"
            + "        text-align: right;\n"
            + "        float: right;\n"
            + "      }\n"
            + "      .tabkeTitle {\n"
            + "        font-weight: bold;\n"
            + "      }\n"
            + "      .indent {\n"
            + "        margin-left: 30px;\n"
            + "      }\n" 
            + "    </style>\n" 
            + "  </head>\n\n"
            + "  <body>\n";
    public static final String HTML_FOOT = "  <body>\n</html>";
    public static final String HOME_LINK = "<a class=\"indent\" href=\"\\\">Pi Weather Station Home</a>" ;

	private DataManager dataManager;
	
	public WebServer(DataManager dataManager) {
		this.dataManager = dataManager;
	}

	public void create(int port) throws IOException {
		logger.info("created refresh server on port " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

	/**
	 * This inner class, by implementing HttpHandler, it commits to having the 
	 * method public void handle(HttpExchange t), that receives the http request
	 * and responds to it.
	 * 
	 * @author ted
	 *
	 */
	class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			logger.info(t.getRequestURI());
			String responseString = null;
			byte[] response = null;
			OutputStream os = null;
			Headers responseHeaders = t.getResponseHeaders();
			String rawPath = t.getRequestURI().getRawPath();
			int returnCode = 200;
			try {
				//if the request starts with /get, the system responds by returning
				//the file specified in the get.  This is how a slave gets the 
				//feed files from the master.
				if ( rawPath.startsWith("/get/") ) {
					File file = new File(rawPath.replace("/get/", "/tmp/"));
					if ( file.exists() ) {
						if ( file.getName().endsWith(".json") ) {
							response = FileUtils.readFileToString(file).getBytes();
							responseHeaders.set("Content-Type", "application/json");
							t.sendResponseHeaders(200, response.length);
						} else {
							response = FileUtils.readFileToByteArray(file);
							responseHeaders.set("Content-Type", "image/gif");
							t.sendResponseHeaders(200, response.length);
						}
					} else {
						responseString = HTML_HEAD + "<p class=\"indent\">ERROR.  File " + file.getName() + " was not found</p>" + HOME_LINK + "\n" + HTML_FOOT;
						responseHeaders.set("Content-Type", "text/html");
						t.sendResponseHeaders(404, responseString.length());
						response = responseString.getBytes();
					}
				} else {

					//a slave tells the master to add its ip address to the set of listeners so it can be
					//notified when the master has new feed files.
					if ( rawPath.toLowerCase().equalsIgnoreCase("/registerListener") ) {
						dataManager.addListener(t.getRemoteAddress().getAddress().getHostAddress());
						logger.info("Registered listener at " + t.getRemoteAddress().getAddress().getHostAddress());
						responseString = HTML_HEAD + "<p class=\"indent\">Registered listener at " + t.getRemoteAddress().getAddress().getHostAddress() + "</p>" + HOME_LINK + "\n" + HTML_FOOT;
					//it's good practise to allow a listener to remove itself, but this isn't used in the system.
					} else if ( rawPath.toLowerCase().equalsIgnoreCase("/removeListener") ) {
						dataManager.removeListener(t.getRemoteAddress().getAddress().getHostAddress());
						logger.info("Removed listener at " + t.getRemoteAddress().getAddress().getHostAddress());
						responseString = HTML_HEAD + "<p class=\"indent\">Removed listener at " + t.getRemoteAddress().getAddress().getHostAddress() + "</p>" + HOME_LINK + "\n" + HTML_FOOT;
					//another not really used, but it allows the master ip address of a slave to be set from a web browser.
					} else if ( rawPath.toLowerCase().startsWith("/register/") ) {
						String masterIpAddress = rawPath.toLowerCase().replace("/register/", "");
						dataManager.registerMasterIpAddress(masterIpAddress);
						if ( dataManager.getWeatherMaster()==null ) {
							logger.info("Error registering master IP Address at " + masterIpAddress);
							responseString = HTML_HEAD + "<p class=\"indent\">Error registered Master IP Address at " + masterIpAddress + "</p>" + HOME_LINK + "\n" + HTML_FOOT;
						} else {
							logger.info("Registered master IP Address at " + masterIpAddress);
							responseString = HTML_HEAD + "<p class=\"indent\">Registered Master IP Address at " + masterIpAddress + "</p>" + HOME_LINK + "\n" + HTML_FOOT;
						}
					//refresh all the datafeeds
					} else if ( rawPath.equals("/refresh/all") ) {
						for ( Map.Entry<String, DataFeed>entry:dataManager.getDataFeeds().entrySet() ) {
							entry.getValue().update(false);
						}
						responseString = "    <p class=\"indent\">refreshed all at " + new Date() + "</p>" + HOME_LINK + "\n";
					//refresh a particular data feed, allowing control from a web browser.
					} else if ( rawPath.startsWith("/refresh/") ) {
						String feedName = rawPath.replace("/refresh/", "").toLowerCase();
						if ( dataManager.getDataFeeds().containsKey(feedName)) {
							dataManager.getDataFeeds().get(feedName).update(false);
							responseString = "    <p class=\"indent\">refreshed " + feedName + " at " + new Date() + "</p>" + HOME_LINK + "\n";
						} else {
							responseString = "    <p class=\"indent\">ERROR " +  feedName + " does not exist.</p>" + HOME_LINK + "\n";
							returnCode = 404;
						}
					//essentially the same logic as the refresh, but couched in listener terminology. The subtle difference
					//is checking the sender is in fact the registered master.
					} else if ( rawPath.startsWith("/updateEvent/") ) {
						String feedName = rawPath.replace("/updateEvent/", "").toLowerCase();
						if ( t.getRemoteAddress().getAddress().getHostAddress().equals(dataManager.getWeatherMaster()) ) {
							if ( dataManager.getDataFeeds().containsKey(feedName)) {
								dataManager.getDataFeeds().get(feedName).update(false);
								responseString = "    <p class=\"indent\">refreshed " + feedName + " at " + new Date() + "</p>" + HOME_LINK + "\n";
							} else {
								responseString = "    <p class=\"indent\">ERROR " +  feedName + " does not exist.</p>" + HOME_LINK + "\n";
								returnCode = 404;
							}
						} else {
							logger.info("Removed master IP Address at " + t.getRemoteAddress().getAddress().getHostAddress());
							dataManager.removeMasterIpAddress(t.getRemoteAddress().getAddress().getHostAddress());
						}
						
					//display the settings
					} else if ( rawPath.equals("/settings") ) {
						responseString = "<H3>Pi Weather Station Settings</H3>\n" + dataManager.getHtmlSettings() + "      <p>" + HOME_LINK + "</p>\n";

					//reboot the system (if allowed by the properties -- defaults to no).
					} else if ( rawPath.equals("/reboot") ) {
						if ( Weather.properties.getProperty("reboot","no").toString().toLowerCase().startsWith("y") ) {
							responseString = "    <p class=\"indent\">reboot command being executed</p>\n";
							Runtime.getRuntime().exec("sudo /sbin/shutdown -r now");
						} else {
							responseString = "    <p class=\"indent\">the reboot option has not been set on this computer.</p>" + HOME_LINK + "\n";
						}
					//shuts down the system (if allowed to reboot by the properties -- defaults to no).
					} else if ( rawPath.equals("/shutdown") ) {
						if ( Weather.properties.getProperty("reboot","no").toString().toLowerCase().startsWith("y") ) {
							responseString = "    <p class=\"indent\">shutdown command being executed</p>\n";
							Runtime.getRuntime().exec("sudo /sbin/shutdown -h now");
						} else {
							responseString = "    <p class=\"indent\">the reboot option has not been set on this computer.</p>" + HOME_LINK + "\n";
						}

					//and finally the home page.
					} else if ( rawPath.equals("/") ) {					
						StringBuilder sb = new StringBuilder();
						sb.append("<H3>Frame Updates</H3>\n");
						sb.append(dataManager.getHtmlUpdate());
						sb.append("<H3>Pi Weather Station Refresh Schedule</H3>\n");
						sb.append(dataManager.getScheduleManager().getHtmlSchedule());
						sb.append("<p><a class=\"indent\" href=\"\\settings\">Pi Weather Station Settings</a></p>");
						responseString = sb.toString();
					}
					responseString = HTML_HEAD + responseString + HTML_FOOT;
					responseHeaders.set("Content-Type", "text/html");
					t.sendResponseHeaders(returnCode, responseString.length());
					response = responseString.getBytes();
				}

				os = t.getResponseBody();
				os.write(response);

			} catch (Exception e) {
				responseString = "server error.  Check the log file.";
				t.sendResponseHeaders(500, responseString.length());
				os = t.getResponseBody();
				os.write(responseString.getBytes());
				logger.error("error refreshing with URI: " + t.getRequestURI(), e);
			} finally {
				if (os!=null ) {
					os.flush();
					os.close();
				}
			}
		}
	}

}
