package net.lutzky.transportdroidil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class BusGetter {
	abstract String getUrl();

	abstract String getQueryJson(String query);

	private String rawResult = null;
	private String htmlResult = null;
	private String filteredResult = null;

	public void runQuery(String query) throws IOException {
		URL url = new URL(getUrl());

		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("POST");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Accept-Charset", "utf-8");
		hurl.setRequestProperty("Content-Type",
				"application/json; charset=utf-8");

		OutputStreamWriter wr = new OutputStreamWriter(hurl.getOutputStream());

		wr.write(getQueryJson(query));

		wr.flush();

		// We're only expecting one line of input anyway.
		rawResult = (new BufferedReader(new InputStreamReader(hurl
				.getInputStream()))).readLine();
	}

	public String getRawResult() {
		return rawResult;
	}

	public String getHtmlResult() {
		if (htmlResult == null) {
			// Drop leading "{\"d\":\""
			htmlResult = rawResult.trim().substring(6);

			// Drop trailing "\"}"
			htmlResult = htmlResult.substring(0, htmlResult.length() - 2);

			htmlResult = htmlResult.replace("\\u003c", "<");
			htmlResult = htmlResult.replace("\\u003e", ">");
		}

		return htmlResult;
	}

	public String getFilteredResult() {
		if (filteredResult == null) {
			filteredResult = getHtmlResult().replace("<br>", "\n").replace(
					"<li>", "\n * ").replace("&nbsp", " ").replace("<BUS>", "")
					.replace("</BUS>", "").replace("<COMPANY>", "").replace(
							"</COMPANY>", "").replaceAll(" *(,? ) *", "$1").replaceAll("(\n ?)+","\n");
		}

		return filteredResult;
	}
}