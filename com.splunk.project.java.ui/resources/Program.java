import java.io.IOException;
import java.io.InputStream;

import com.splunk.Args;
import com.splunk.Event;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;
import com.splunk.ServiceArgs;


public class Program {

	// This is a template for new users of the Splunk SDK for Java.
	// The code below connects to a Splunk instance, runs a search,
	// and prints out the results in a crude form.
	public static void main(String[] args) {
		// Create login parameters. We suggest finding
		// a better way to store these than hard coding
		// them in your program for production code.
		ServiceArgs serviceArgs = new ServiceArgs();
		serviceArgs.setUsername("admin");
		serviceArgs.setPassword("changeme");
		serviceArgs.setHost("localhost");
		serviceArgs.setPort(8089);

		// Create a Service instance and log in with the argument map
		Service service = Service.connect(serviceArgs);
		
		// Set the parameters for the search
		Args oneshotSearchArgs = new Args();
		// For a full list of options, see:
		//
		//     http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTsearch#POST_search.2Fjobs

		// oneshotSearchArgs.put("earliest_time", "now-1w");
		// oneshotSearchArgs.put("latest_time",   "now");

		InputStream resultsStream = service.oneshotSearch("search * | head 5",
				oneshotSearchArgs);

		try {
			ResultsReaderXml resultsReader = new ResultsReaderXml(resultsStream);

			for (Event event : resultsReader) {
				// Process each event
				for (String key: event.keySet()) {
				   System.out.println(key + ": " + event.get(key));
				}
				System.out.println("");
			}

			resultsReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
