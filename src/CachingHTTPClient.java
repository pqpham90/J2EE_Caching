import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		String cacheDir = "/tmp/<uteid>/assignment1/" + url.toString().replace("/", "&#92;");
		cacheDir += ".cache";

		System.out.println(cacheDir);

		try {
			URLConnection connection = url.openConnection();

			System.out.println("Expires:" + connection.getHeaderField("Expires"));
			System.out.println("Cache-Control:" + connection.getHeaderField("Cache-Control"));
			System.out.println("Last-Modified:" + connection.getHeaderField("Last-Modified"));

			System.out.println("******");

			Map<String, List<String>> headerFields = connection.getHeaderFields();

			Set<String> headerFieldsSet = headerFields.keySet();
			Iterator<String> hearerFieldsIter = headerFieldsSet.iterator();

			while (hearerFieldsIter.hasNext()) {

				String headerFieldKey = hearerFieldsIter.next();
				List<String> headerFieldValue = headerFields.get(headerFieldKey);

				StringBuilder sb = new StringBuilder();
				for (String value : headerFieldValue) {
					sb.append(value);
					sb.append("");
				}

				System.out.println(headerFieldKey + "=" + sb.toString());

			}

			System.out.println("******");

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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

