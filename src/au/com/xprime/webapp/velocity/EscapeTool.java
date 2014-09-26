package au.com.xprime.webapp.velocity;

public class EscapeTool extends org.apache.velocity.tools.generic.EscapeTool {
	public EscapeTool() {
	}

	public String nobr(Object string) {
		return string.toString().replace(" ", "&nbsp;").replace("\t", "&nbsp;");
	}
}