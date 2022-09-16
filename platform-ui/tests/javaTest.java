package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.junit.Assert.*;
/** 
 * Test suite for attribute parser.
 * @author Jonathan Hedley, jonathan@hedley.net 
 */
public class AttributeParseTest {
  @Test public void strictAttributeUnescapes(){
    String html="<a id=1 href='?foo=bar&mid&lt=true'>One</a> <a id=2 href='?foo=bar&lt;qux&lg=1'>Two</a>";
    Elements els=Jsoup.parse(html).select("a");
    assertEquals("?foo=bar&mid&lt=true",els.first().attr("href"));
    assertEquals("?foo=bar<qux&lg=1",els.last().attr("href"));
  }
}

public class Testfunction {
    AttributeParseTest();
    String letter='this is test'
}
