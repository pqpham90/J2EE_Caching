import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by pqpham90 on 2/7/15.
 */
public class CachingHTTPClient {
	public static void main(String args[]) {
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

		String cacheDir = File.separator + "tmp" + File.separator + "pnp24" + File.separator + "assignment1" + File.separator;
		String cacheFile =  url.toString().replace("/", "&#92;") + ".html";

		try {
			URLConnection connection = url.openConnection();
			SimpleDateFormat dateParser = new SimpleDateFormat("EEE,d MMM yyyy HH:mm:ss zzz");


			String expires = connection.getHeaderField("Expires");
			String cacheControl = connection.getHeaderField("Cache-Control");
			String ifModifiedSince = connection.getHeaderField("Last-Modified");

			int maxAge = Integer.parseInt(cacheControl.replaceAll("[\\D]", ""));

			Date expiresDate = null;
			Date ifModifiedDate = null;

			try {
				expiresDate = dateParser.parse(expires);
				ifModifiedDate = dateParser.parse(expires);
			}
			catch (ParseException e) {
				System.out.println("Unexpected Date Format");
			}

			CacheMap cacheM = new CacheMap(expiresDate, maxAge, ifModifiedDate);


			System.out.println("Expires:" + connection.getHeaderField("Expires"));
			System.out.println("Cache-Control:" + connection.getHeaderField("Cache-Control"));
			System.out.println("Last-Modified:" + connection.getHeaderField("Last-Modified"));

			System.out.println("******");

			InputStream input = connection.getInputStream();
			byte[] buffer = new byte[4096];
			int n = - 1;

			File file = new File(cacheDir, cacheFile);
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);

			HashMap<String, Object> fileObj = new HashMap<String, Object>();

//			String commentStart = "<!--\n";
//			String commentEnd = "-->\n";
//
//			out.write(commentStart.getBytes());
//
//			out.write((connection.getHeaderField("Expires") + "\n").getBytes());
//			out.write((connection.getHeaderField("Cache-Control") + "\n").getBytes());
//			out.write((connection.getHeaderField("Last-Modified") + "\n").getBytes());
//			out.write(commentEnd.getBytes());

			while ( (n = input.read(buffer)) != -1)
			{
				if (n > 0)
				{
					out.write(buffer, 0, n);
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

