import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pqpham90 on 2/7/15.
 */
public class CachingHTTPClient {
	static final long ONE_SECOND_IN_MILLIS=1000;

	public static void main(String args[]) {
		// take in input
		if (args.length < 1) {
			System.out.println("Usage:");
			System.out.println("java TestUrlConnection <url>");
			System.exit(0);
		}
		URL url = null;
		try {
			url = new URL(args[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		assert url != null;

		// location of cache file
		String cacheDir = File.separator + "tmp" + File.separator + "pnp248" + File.separator + "assignment1" + File.separator;
		String cacheFile =  url.toString().replace("/", "&#92;") + ".cache";

		try {
			// attempt to connect to url
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

			// flag cache status
			boolean newCache = false;
			boolean oldCache = false;
			boolean hasMaxAge = false;

			// parse information from header
			SimpleDateFormat dateParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
			String expires = connection.getHeaderField("Expires");
			String cacheControl = connection.getHeaderField("Cache-Control");
			String ifModifiedSince = connection.getHeaderField("If-Modified-Since");
			String lastModified = connection.getHeaderField("Last-Modified");

			if(cacheControl.contains("max-age")) {
				hasMaxAge = true;
			}

			// produce date object from dates
			Date currentDate = new Date();
			Date expiresDate = null;
			Date ifModifiedDate = null;
			Date lastModifiedDate = null;
			try {
				if(expires != null) {
					expiresDate = dateParser.parse(expires);
				}

				if (ifModifiedSince != null) {
					ifModifiedDate = dateParser.parse(ifModifiedSince);
				}

				if (lastModified != null) {
					lastModifiedDate = dateParser.parse(expires);
				}
			}
			catch (ParseException e) {
				System.out.println("Unexpected Date Format");
			}

			File file = new File(cacheDir, cacheFile);
			CacheMap cacheM = null;
			int maxAge = -1;
			if(hasMaxAge) {
				maxAge = Integer.parseInt(cacheControl.replaceAll("\\D+",""));
			}

			// cache exists
			if (file.exists() && !file.isDirectory()) {
				FileInputStream fileIn = new FileInputStream(cacheDir + cacheFile + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);

				try {
					cacheM = (CacheMap) in.readObject();
				}
				catch (ClassNotFoundException e) {
					System.out.println("Can't read the map");
				}

				in.close();
				fileIn.close();

				long t = expiresDate.getTime();
				Date newDate = new Date (t + (maxAge * ONE_SECOND_IN_MILLIS));

				// cache logic
				if(hasMaxAge) {
					if (newDate.compareTo(currentDate) < 0) {
						newCache = true;
					}
					else {
						oldCache = true;
					}
				}
				else {
					if(expiresDate.compareTo(currentDate) < 0) {
						newCache = true;
					}
					else if (expiresDate.compareTo(currentDate) > 0) {
						if(ifModifiedDate.compareTo(currentDate) < 1) {
							newCache = true;
						}
					}
					else {
						oldCache = true;
					}
				}

				// cache is no longer valid, remove
				if (!oldCache) {
					file.delete();
					File mapFile = new File(cacheDir, cacheFile + ".ser");
					mapFile.delete();
				}
			}
			// no cache found
			else {
				if (maxAge > 0) {
					newCache = true;
				}
				else if (expiresDate.compareTo(currentDate) > 0) {
					newCache = true;
				}
			}

			if (newCache) {
				cacheM = new CacheMap(expiresDate, cacheControl, ifModifiedDate, lastModifiedDate);

				InputStream input = connection.getInputStream();
				writeCache(file, input);

				cacheM.setCacheFile(cacheDir + cacheFile);

				File mapFile = new File(cacheDir, cacheFile + ".ser");
				writeMap(mapFile, cacheM);

			}
			else if (oldCache) {
				System.out.println("***** Serving from the cache – start *****");
				printCache(cacheDir + cacheFile);
				System.out.println("***** Serving from the cache – end *****");
			}
			else {
				System.out.println("***** Serving from the source – start *****");
				printResponse(connection);
				System.out.println("***** Serving from the source – end *****");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// write out the file, obtain from devdatta
	public static void writeCache(File file, InputStream input) throws IOException {
		byte[] buffer = new byte[4096];
		int n = - 1;

		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);

		System.out.println("***** Serving from the source – start *****");
		while ( (n = input.read(buffer)) != -1)
		{
			if (n > 0)
			{
				out.write(buffer, 0, n);
				System.out.write(buffer, 0, n);
			}
		}
		System.out.println("***** Serving from the source – end *****");
		out.close();
	}

	// maps the header information to a cache
	public static void writeMap (File mapFile, CacheMap cacheM) throws IOException {
		mapFile.getParentFile().mkdirs();

		FileOutputStream fileOut = new FileOutputStream(mapFile);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(cacheM);
		out.close();
		fileOut.close();
	}

	// writes to console from URL
	public static void printResponse(URLConnection connection) throws IOException {
		InputStream input = connection.getInputStream();
		byte[] buffer = new byte[4096];
		int n = - 1;

		while ( (n = input.read(buffer)) != -1)
		{
			if (n > 0)
			{
				System.out.write(buffer, 0, n);
			}
		}
	}

	// writes to console from cache
	public static void printCache(String file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		int n = - 1;

		while ( (n = input.read(buffer)) != -1)
		{
			if (n > 0)
			{
				System.out.write(buffer, 0, n);
			}
		}

		System.out.println();
	}


}

