package au.com.xprime.webapp.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.xprime.misc.StringCSVTokenizer;

public class ResponseCacheControlFilter implements Filter {
	static final Log LOG = LogFactory.getLog(ResponseCacheControlFilter.class);
	private static final String CACHE_CONTROL_HEADER = "Cache-Control";
	private static final String EXPIRES_HEADER = "Expires";

	private static class PatternSpec {
		private static final String ZERO_CACHE_CONTROL_HEADER_VALUE = "max-age=0, no-cache, no-store";
		private Pattern pattern;
		private String spec;
		boolean zero = false;

		public PatternSpec(Pattern pattern, String spec) {
			this.pattern = pattern;
			this.spec = spec;
			this.zero = "0".equals(spec);
		}

		public boolean matches(String uri) {
			return pattern.matcher(uri).matches();
		}

		public void setHeaders(HttpServletResponse response) {
			if (zero) {
				if (LOG.isDebugEnabled())
					LOG.debug("Setting " + ZERO_CACHE_CONTROL_HEADER_VALUE);
				response.setHeader(CACHE_CONTROL_HEADER, ZERO_CACHE_CONTROL_HEADER_VALUE);
				response.setHeader(EXPIRES_HEADER, "0");
			} else {
				if (LOG.isDebugEnabled())
					LOG.debug("Setting " + spec);
				response.setHeader(CACHE_CONTROL_HEADER, spec);
			}
		}
	}

	private PatternSpec[] patternSpecs;

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;

		// set the provided HTTP response parameters
		String uri = request.getRequestURI();
		if (LOG.isDebugEnabled())
			LOG.debug("Checking type for " + uri);
		boolean found = false;
		for (int i = 0; i < patternSpecs.length; i++) {
			if (patternSpecs[i].matches(uri)) {
				found = true;
				patternSpecs[i].setHeaders((HttpServletResponse) res);
				break;
			}
		}
		if (!found && LOG.isDebugEnabled())
			LOG.debug("No match for " + uri);

		chain.doFilter(req, response);
	}

	@SuppressWarnings("unchecked")
	public void init(FilterConfig config) throws ServletException {
		Vector<PatternSpec> v = new Vector<PatternSpec>();
		for (Enumeration<String> e = (Enumeration<String>) config.getInitParameterNames(); e.hasMoreElements();) {
			try {
				String key = e.nextElement();
				String spec = config.getInitParameter(key);
				String[] sa = StringCSVTokenizer.getStringsFromCSVString(key);
				for (int i = 0; i < sa.length; i++) {
					if (sa[i].trim().length() == 0)
						continue;
					v.add(new PatternSpec(Pattern.compile(sa[i].trim()), spec));
				}
			} catch (Exception ex) {
			}
		}
		patternSpecs = v.toArray(new PatternSpec[0]);
	}
}