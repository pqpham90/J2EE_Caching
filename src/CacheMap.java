import java.io.File;
import java.util.Date;

/**
 * Created by pqpham90 on 2/8/15.
 */
public class CacheMap implements java.io.Serializable {
	private Date expires;
	private int maxAge;
	private Date ifModifiedSince;

	private File cacheFile;

	public CacheMap(Date e, int m, Date i) {
		expires = e;
		maxAge = m;
		ifModifiedSince = i;
	}
}
