package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class RemoteSetup {

	public static void main(String[] args) throws Exception {
		if (args.length < 5) {
			throw new IllegalArgumentException(
					"Provide the endpoint IP, username and password that you were given for the remote setup.");
		}

		String ip = args[0]; // endpoint IP
		String username = args[1];
		String password = args[2];
		Boolean startRegistry = Boolean.parseBoolean(args[3]); // whether to start a RMI registry on the server
		int registryPort = Integer.parseInt(args[4]); // on which port to start a RMI registry
		String jar = (args.length > 5) ? args[5] : "rmi1_server.jar"; // name of the JAR file that contains the server
																		// code

		//System.out.println("This prints some output because I need to find where the nullptr exception comes from in here.");

		HttpURLConnection connection = null;
		InputStream inputStream = null;
		try {
			connection = (HttpURLConnection) new URL("http://" + ip + "/rmi").openConnection();
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", java.nio.charset.StandardCharsets.UTF_8.name());
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			String basicAuth = "Basic "
					+ new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
			connection.setRequestProperty("Authorization", basicAuth);

			connection.setRequestProperty("X-Registry-Start", startRegistry.toString());
			connection.setRequestProperty("X-Registry-Port", Integer.toString(registryPort));

			//System.out.println("Properties set");

			
			FileInputStream fin = new FileInputStream(jar);
			//System.out.println("Created inputstream to read in jar");
		

			// getting a connection timed out after 2 minutes
			//connection.getOutputStream();
			//System.out.println("Got output stream");

			copy(fin, connection.getOutputStream());
			//System.out.println("JAR transferred");
			fin.close();

			

			inputStream = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = rd.readLine()) != null) {
				System.out.println("[REMOTE SERVER] " + line);
			}
			rd.close();

			//System.out.println("Response received");
		} finally {
			inputStream.close();
			connection.disconnect();
		}
	}

	// https://stamm-wilbrandt.de/en/blog/Post.java
	// copy method from From E.R. Harold's book "Java I/O"
	public static void copy(FileInputStream in, OutputStream out) throws IOException {
		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place
		synchronized (in) {
			synchronized (out) {
				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1)
						break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}
}
