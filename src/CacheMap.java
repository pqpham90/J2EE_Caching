import java.util.Date;

/**
 * Created by pqpham90 on 2/8/15.
 */
public class CacheMap implements java.io.Serializable {
	private Date expires;
	private String maxAge;
	private Date ifModifiedSince;
	private Date lastModified;

	private String cacheFile;

	public CacheMap(Date e, String m, Date i, Date l) {
		expires = e;
		maxAge = m;
		lastModified = l;
		ifModifiedSince = i;
	}

	public String getMaxAge() {
		return maxAge;
	}
}
