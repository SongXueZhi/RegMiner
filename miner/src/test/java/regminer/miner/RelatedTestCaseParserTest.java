package regminer.miner;

import org.junit.Assert;
import org.junit.Test;

public class RelatedTestCaseParserTest {
    private RelatedTestCaseParser rTCParser = new RelatedTestCaseParser();

    @Test
    public void testParse() {
        String code = "public class TestJsonParser\n" +
                "    extends com.fasterxml.jackson.core.BaseTest\n" +
                "{\n" +
                "    private final JsonFactory JSON_FACTORY = new JsonFactory();\n" +
                "\n" +
                "    public void testConfig() throws Exception\n" +
                "    {\n" +
                "        JsonParser jp = createParserUsingReader(\"[ ]\");\n" +
                "        jp.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);\n" +
                "        assertTrue(jp.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE));\n" +
                "\n" +
                "        jp.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);\n" +
                "        assertTrue(jp.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE));\n" +
                "        jp.close();\n" +
                "    }";
        Assert.assertTrue(rTCParser.isTestSuite(code));
        code = "public class TsvWriterTest extends TsvParserTest {\n" +
                "\t@Test(enabled = true, dataProvider = \"lineSeparatorProvider\")\n" +
                "\tpublic void writeTest(char[] lineSeparator) throws Exception {\n" +
                "\t\tTsvWriterSettings settings = new TsvWriterSettings();\n" +
                "\n" +
                "\t\tString[] expectedHeaders = new String[]{\"Year\", \"Make\", \"Model\", \"Description\", \"Price\"};\n" +
                "\n" +
                "\t\tByteArrayOutputStream tsvResult = new ByteArrayOutputStream();\n" +
                "\n" +
                "\t\tTsvWriter writer = new TsvWriter(new OutputStreamWriter(tsvResult, \"UTF-8\"), settings);\n" +
                "\n" +
                "\t\tObject[][] expectedResult = new Object[][]{\n" +
                "\t\t};\n" +
                "\n" +
                "\t\twriter.writeHeaders();\n" +
                "\n" +
                "\t\twriter.writeRow(\"-->skipping this line (10) as well\");\n" +
                "\t\twriter.close();\n" +
                "\n" +
                "\t\tString result = tsvResult.toString();\n" +
                "\t\tresult = \"This line and the following should be skipped. The third is ignored automatically because it is blank\\n\\n\\n\".replaceAll(\"\\n\", new String(lineSeparator)) + result;\n" +
                "\n" +
                "\n" +
                "\t\ttry {\n" +
                "\t\t\tassertHeadersAndValuesMatch(expectedHeaders, expectedResult);\n" +
                "\t\t} catch (Error e) {\n" +
                "\t\t\tSystem.out.println(\"FAILED:\\n===\\n\" + result + \"\\n===\");\n" +
                "\t\t\tthrow e;\n" +
                "\t\t}\n" +
                "\t}";
        Assert.assertTrue(rTCParser.isTestSuite(code));
        code = "public class FixedWidthParserTest extends ParserTestCase {\n" +
                "\n" +
                "\t@DataProvider(name = \"fileProvider\")\n" +
                "\tpublic Object[][] csvProvider() {\n" +
                "\t\treturn new Object[][]{\n" +
                "\t\t\t\t{\".txt\", new char[]{'\\n'}},\n" +
                "\t\t\t\t{\"-dos.txt\", new char[]{'\\r', '\\n'}},\n" +
                "\t\t\t\t{\"-mac.txt\", new char[]{'\\r'}},\n" +
                "\t\t\t\t{\".txt\", null},\n" +
                "\t\t\t\t{\"-dos.txt\", null},\n" +
                "\t\t\t\t{\"-mac.txt\", null}\n" +
                "\t\t};\n" +
                "\t}" +
                "\n}";
        Assert.assertFalse(rTCParser.isTestSuite(code));

    }

}
