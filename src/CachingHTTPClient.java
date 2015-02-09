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
		String cacheDir = File.separator + "tmp" + File.separator + "pnp24" + File.separator + "assignment1" + File.separator;
		String cacheFile =  url.toString().replace("/", "&#92;") + ".cache";

		try {
			// attempt to connect to url
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

			// flag cache status
			boolean isExpired = false;
			boolean isPastMaxAge = false;
			boolean hasBeenModified = false;

			// parse information from header
			SimpleDateFormat dateParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
			String expires = connection.getHeaderField("Expires");
			String maxAge = connection.getHeaderField("Cache-Control");
			String ifModifiedSince = connection.getHeaderField("If-Modified-Since");
			String lastModified = connection.getHeaderField("Last-Modified");

			// produce date object from dates
			Date expiresDate = null;
			Date ifModifiedDate = null;
			Date lastModifiedDate = null;
			try {
				if(expires != null) {
					expiresDate = dateParser.parse(expires);
				}
				else {
					isExpired = true;
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

			if (expiresDate != null)
				System.out.println(expiresDate.toString());
			else
				System.out.println("null");
			System.out.println(maxAge);
			if (ifModifiedDate != null)
				System.out.println(ifModifiedDate.toString());
			else
				System.out.println("null");
			if(lastModifiedDate != null)
				System.out.println(lastModifiedDate.toString());
			else
				System.out.println("null");


			System.out.println("******");

			File file = new File(cacheDir, cacheFile);

			if (file.exists() && !file.isDirectory()) {
				System.out.println("Found Cache!");
				FileInputStream fileIn = new FileInputStream(cacheDir + cacheFile + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);

				CacheMap cacheM = null;

				try {
					cacheM = (CacheMap) in.readObject();
				}
				catch (ClassNotFoundException e) {
					System.out.println("Can't read the map");
				}

				in.close();
				fileIn.close();

				System.out.println(cacheM.getMaxAge());

			}
			else {
				CacheMap cacheM = new CacheMap(expiresDate, maxAge, ifModifiedDate,lastModifiedDate);

				InputStream input = connection.getInputStream();
				writeCache(file, input);

				File mapFile = new File(cacheDir, cacheFile + ".ser");
				writeMap(mapFile, cacheM);
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

		while ( (n = input.read(buffer)) != -1)
		{
			if (n > 0)
			{
				out.write(buffer, 0, n);
			}
		}
		out.close();
	}

	// maps the header information to a cache
	public static void writeMap (File mapFile, CacheMap cacheM) throws IOException {
		mapFile.getParentFile().mkdirs();

		System.out.println("Here");
		FileOutputStream fileOut = new FileOutputStream(mapFile);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(cacheM);
		out.close();
		fileOut.close();
	}
}

