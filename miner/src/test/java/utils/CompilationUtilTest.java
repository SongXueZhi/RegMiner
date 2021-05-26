package utils;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.Test;

import ast.JdtFieldRetriever;

public class CompilationUtilTest {

	@Test
	public void testParseCompliationUnit() {
		String classContent = "\n" + "package com.alibaba.json.bvt.issue_3600;\n" + "\n"
				+ "import com.alibaba.fastjson.JSON;\n" + "import com.alibaba.fastjson.annotation.JSONField;\n"
				+ "import junit.framework.TestCase;\n" + "import lombok.Data;\n" + "\n"
				+ "public class Issue3682 extends TestCase {\n"
				+ "    public void test_for_issue() throws Exception {\n"
				+ "        Cid cid = JSON.parseObject(SOURCE, Cid.class);\n" + "        System.out.println(cid);\n"
				+ "    }\n" + "\n" + "    @Data\n" + "    static public class Cid {\n" + "\n"
				+ "        @JSONField(name = \"/\")\n" + "        private String hash;\n" + "\n" + "    }\n" + "\n"
				+ "    static final String SOURCE = \"{\\n\" +\n"
				+ "            \"    \\\"jsonrpc\\\": \\\"2.0\\\",\\n\" +\n"
				+ "            \"    \\\"result\\\": {\\n\" +\n" + "            \"        \\\"Version\\\": 0,\\n\" +\n"
				+ "            \"        \\\"To\\\": \\\"t1iceld4fv44xgjqfcx5lwz45pubheu3c7c2nmlua\\\",\\n\" +\n"
				+ "            \"        \\\"From\\\": \\\"t152xual7ze57jnnioucuv4lmtxarewtzhkqojboy\\\",\\n\" +\n"
				+ "            \"        \\\"Nonce\\\": 4,\\n\" +\n"
				+ "            \"        \\\"Value\\\": \\\"9999999938462317355\\\",\\n\" +\n"
				+ "            \"        \\\"GasLimit\\\": 609960,\\n\" +\n"
				+ "            \"        \\\"GasFeeCap\\\": \\\"101083\\\",\\n\" +\n"
				+ "            \"        \\\"GasPremium\\\": \\\"100029\\\",\\n\" +\n"
				+ "            \"        \\\"Method\\\": 0,\\n\" +\n"
				+ "            \"        \\\"Params\\\": null,\\n\" +\n"
				+ "            \"        \\\"CID\\\": {\\n\" +\n"
				+ "            \"            \\\"/\\\": \\\"bafy2bzacedgpr5pmkvu4rkq26uv4hidpfrn3gdvtgkp3hpxss3bgmodrgqtk6\\\"\\n\" +\n"
				+ "            \"        }\\n\" +\n" + "            \"    },\\n\" +\n"
				+ "            \"    \\\"id\\\": 1\\n\" +\n" + "            \"}\";\n" + "}";

		CompilationUnit unit = CompilationUtil.parseCompliationUnit(classContent);
		JdtFieldRetriever mr = new JdtFieldRetriever();
		unit.accept(mr);
		for (Entry<FieldDeclaration, List<VariableDeclarationFragment>> entry : mr.fieldMap.entrySet()) {
			FieldDeclaration fd = entry.getKey();
			List<VariableDeclarationFragment> vflist = entry.getValue();
			int a = 0;
			for (VariableDeclarationFragment filed : vflist) {
				System.out.println(filed);
			}
		}
	}

}
