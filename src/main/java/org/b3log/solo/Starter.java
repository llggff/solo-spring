/*
 * Copyright (c) 2017, cxy7.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.b3log.solo.util.PropsUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Solo with embedded Jetty,
 * <a href="https://github.com/b3log/solo/issues/12037">standalone mode</a>.
 *
 * <ul>
 * <li>Windows: java -cp WEB-INF/lib/*;WEB-INF/classes
 * org.b3log.solo.Starter</li>
 * <li>Unix-like: java -cp WEB-INF/lib/*:WEB-INF/classes
 * org.b3log.solo.Starter</li>
 * </ul>
 *
 * @author <a href="http://cxy7.com">XyCai</a>
 * @version 1.1.0.7, Dec 23, 2015
 * @since 1.2.0
 */
public final class Starter {
	private static Logger logger = LoggerFactory.getLogger(Starter.class);
	static {
		try {
			Log.setLog(new Slf4jLog());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main.
	 *
	 * @param args
	 *            the specified arguments
	 * @throws java.lang.Exception
	 *             if start failed
	 */
	public static void main(final String[] args) throws Exception {

		final Options options = new Options();
		final Option listenPortOpt = Option.builder("lp").longOpt("listen_port").argName("LISTEN_PORT").hasArg()
				.desc("listen port, default is 8080").build();
		options.addOption(listenPortOpt);

		final Option serverSchemeOpt = Option.builder("ss").longOpt("server_scheme").argName("SERVER_SCHEME").hasArg()
				.desc("browser visit protocol, default is http").build();
		options.addOption(serverSchemeOpt);

		final Option serverHostOpt = Option.builder("sh").longOpt("server_host").argName("SERVER_HOST").hasArg()
				.desc("browser visit domain name, default is localhost").build();
		options.addOption(serverHostOpt);

		final Option serverPortOpt = Option.builder("sp").longOpt("server_port").argName("SERVER_PORT").hasArg()
				.desc("browser visit port, default is 8080").build();
		options.addOption(serverPortOpt);

		final Option staticServerSchemeOpt = Option.builder("sss").longOpt("static_server_scheme")
				.argName("STATIC_SERVER_SCHEME").hasArg()
				.desc("browser visit static resource protocol, default is http").build();
		options.addOption(staticServerSchemeOpt);

		final Option staticServerHostOpt = Option.builder("ssh").longOpt("static_server_host")
				.argName("STATIC_SERVER_HOST").hasArg()
				.desc("browser visit static resource domain name, default is localhost").build();
		options.addOption(staticServerHostOpt);

		final Option staticServerPortOpt = Option.builder("ssp").longOpt("static_server_port")
				.argName("STATIC_SERVER_PORT").hasArg().desc("browser visit static resource port, default is 8080")
				.build();
		options.addOption(staticServerPortOpt);

		final Option runtimeModeOpt = Option.builder("rm").longOpt("runtime_mode").argName("RUNTIME_MODE").hasArg()
				.desc("runtime mode (DEVELOPMENT/PRODUCTION), default is DEVELOPMENT").build();
		options.addOption(runtimeModeOpt);

		options.addOption("h", "help", false, "print help for the command");

		final HelpFormatter helpFormatter = new HelpFormatter();
		final CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine;

		final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
		final String cmdSyntax = isWindows ? "java -cp WEB-INF/lib/*;WEB-INF/classes org.b3log.solo.Starter"
				: "java -cp WEB-INF/lib/*;WEB-INF/classes org.b3log.solo.Starter";
		final String header = "\nSolo is a blogging system written in Java, feel free to create your or your team own blog.\nSolo 是一个用 Java 实现的博客系统，为你或你的团队创建个博客吧。\n\n";
		final String footer = "\nReport bugs or request features please visit our project website: https://github.com/b3log/solo\n\n";
		try {
			commandLine = commandLineParser.parse(options, args);
		} catch (final ParseException e) {
			helpFormatter.printHelp(cmdSyntax, header, options, footer, true);

			return;
		}

		if (commandLine.hasOption("h")) {
			helpFormatter.printHelp(cmdSyntax, header, options, footer, true);

			return;
		}

//		String portArg = commandLine.getOptionValue("listen_port");
//		if (!NumberUtils.isDigits(portArg)) {
//			portArg = "80";
//		}

		String serverScheme = commandLine.getOptionValue("server_scheme");
		Latkes.setServerScheme(serverScheme);
		String serverHost = commandLine.getOptionValue("server_host");
		Latkes.setServerHost(serverHost);
		String serverPort = commandLine.getOptionValue("server_port");
		Latkes.setServerPort(serverPort);
		String staticServerScheme = commandLine.getOptionValue("static_server_scheme");
		Latkes.setStaticServerScheme(staticServerScheme);
		String staticServerHost = commandLine.getOptionValue("static_server_host");
		Latkes.setStaticServerHost(staticServerHost);
		String staticServerPort = commandLine.getOptionValue("static_server_port");
		Latkes.setStaticServerPort(staticServerPort);
		String runtimeMode = commandLine.getOptionValue("runtime_mode");
		if (null != runtimeMode) {
			Latkes.setRuntimeMode(RuntimeMode.valueOf(runtimeMode));
		}

		String webappDirLocation = "src/main/webapp/"; // POM structure in dev
														// env
		final File file = new File(webappDirLocation);
		if (!file.exists()) {
			webappDirLocation = "."; // production environment
		}

//		final int port = Integer.valueOf(portArg);
		int port = PropsUtil.getInteger("listen_port");

		final Server server = new Server(port);
		final WebAppContext root = new WebAppContext();
		root.setParentLoaderPriority(true); // Use parent class loader
		root.setContextPath("/");
		root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
		root.setResourceBase(webappDirLocation);
		server.setHandler(root);

		try {
			server.start();
		} catch (final Exception e) {
			logger.error("Server start failed", e);

			System.exit(-1);
		}

		serverScheme = Latkes.getServerScheme();
		serverHost = Latkes.getServerHost();
		serverPort = Latkes.getServerPort();

		final String contextPath = Latkes.getContextPath();

//		try {
//			Desktop.getDesktop()
//					.browse(new URI(serverScheme + "://" + serverHost + ":" + serverPort + contextPath + "/admin-index.do#article/article"));
//		} catch (final Throwable e) {
//			// Ignored
//		}

		server.join();
	}

	/**
	 * Private constructor.
	 */
	private Starter() {
	}
}
