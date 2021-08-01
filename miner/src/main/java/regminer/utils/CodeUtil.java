package regminer.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.tools.ant.DirectoryScanner;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class CodeUtil {

	public static JavaClass lookupClassbyFile(String resourceName) {
		byte[] b = new byte[(int) new File(resourceName).length()];
		InputStream in;
		try {
			in = new FileInputStream(resourceName);
			new DataInputStream(in).readFully(b);
			in.close();
			ClassParser parser = new ClassParser(new ByteArrayInputStream(b), resourceName);
			JavaClass javaClass = parser.parse();
			return javaClass;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethodByName(JavaClass clazz, String methodName) {
		Method[] ms = clazz.getMethods();
		for (Method method : ms) {
			String s = method.getSignature();
			String ss = method.toString();
			FileUtilx.log("");
		}
		return null;
	}

	public static String getMethodSig(String name, Type[] types) {
		StringJoiner sb = new StringJoiner(",", name + "(", ")");
		for (int i = 0; i < types.length; i++) {
			sb.add(types[i].toString());
		}
		return sb.toString();
	}

	public static Repository getRepository(File meta) {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(meta);
		scanner.setIncludes(new String[] { "**/*.class" });
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();
		Repository repository = SyntheticRepository
				.getInstance(new ClassPath(ClassPath.SYSTEM_CLASS_PATH, meta.getAbsolutePath()));
		for (int i = 0; i < files.length; i++) {
			File f = new File(meta, files[i]);
			JavaClass oldclazz = getJavaClassFromFile(f, repository);
			repository.storeClass(oldclazz);
		}
		return repository;
	}

	public static String[] getJavaAndClassFiles(File meta) {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(meta);
		scanner.setIncludes(new String[] { "**/*.java", "**/*.class" });
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();
		return files;
	}

	public static String[] getJavaFiles(File meta) {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(meta);
		scanner.setIncludes(new String[] { "**/*.java"});
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();
		return files;
	}

	private static JavaClass getJavaClassFromFile(File f, Repository repository) {
		InputStream is = null;
		try {
			is = new FileInputStream(f);
			ClassParser parser = new ClassParser(is, f.getName());
			JavaClass clazz = parser.parse();
			clazz.setRepository(repository);
			return clazz;
		} catch (IOException ex) {
			return null;
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
	}

	public static MethodDeclaration methodMatch(List<MethodDeclaration> mdList, Method m) {
		List<MethodDeclaration> hitList = new ArrayList<>();
		Type[] typesOfMethod = m.getArgumentTypes();
		String Name = m.getName();
		for (MethodDeclaration md : mdList) {
			if (md.getName().toString().equals(Name)) {
				hitList.add(md);
			}
		}
		if (hitList.size() == 1) {
			return hitList.get(0);
		} else if (hitList.size() > 1) {
			List<MethodDeclaration> hit2List = new ArrayList<>();

			for (MethodDeclaration m1 : hitList) {
				if (m1.parameters().size() == typesOfMethod.length) {
					hit2List.add(m1);
				}
			}

			if (hit2List.size() == 1) {
				return hit2List.get(0);
			} else if (hit2List.size() > 1) {
				int max = 0;
				MethodDeclaration tm = hit2List.get(0);
				for (MethodDeclaration m2 : hit2List) {
					List<ASTNode> params = m2.parameters();
					int leg = 0;
					for (int i = 0; i < params.size(); i++) {
						String s1 = params.get(i).toString();
						if (s1.contains("<")) {
							s1 = s1.split("[<]")[0];
						}
						String s2 = typesOfMethod[i].toString();
						leg += lengthOfLongestCommonSubstring(s1, s2);
					}
					if (leg > max) {
						max = leg;
						tm = m2;
					}
				}
				return tm;
			}
		}
		return null;
	}

	public static Method methodMatch(Method[] mdArray, MethodDeclaration m) {
		List<Method> hitList = new ArrayList<>();
		List<ASTNode> paramsList = m.parameters();
		String Name = m.getName().toString();
		for (int j = 0; j < mdArray.length; j++) {
			Method md = mdArray[j];
			if (md.getName().equals(Name)) {
				hitList.add(md);
			}
		}
		if (hitList.size() == 1) {
			return hitList.get(0);
		} else if (hitList.size() > 1) {
			List<Method> hit2List = new ArrayList<>();

			for (Method m1 : hitList) {
				if (m1.getArgumentTypes().length == paramsList.size()) {
					hit2List.add(m1);
				}
			}

			if (hit2List.size() == 1) {
				return hit2List.get(0);
			} else if (hit2List.size() > 1) {
				int max = 0;
				Method tm = hit2List.get(0);
				for (Method m2 : hit2List) {
					Type[] typeParamArray = m2.getArgumentTypes();
					int leg = 0;
					for (int i = 0; i < typeParamArray.length; i++) {
						String s1 = paramsList.get(i).toString();
						if (s1.contains("<")) {
							s1 = s1.split("[<]")[0];
						}
						String s2 = typeParamArray[i].toString();
						leg += lengthOfLongestCommonSubstring(s1, s2);
					}
					if (leg > max) {
						max = leg;
						tm = m2;
					}
				}
				return tm;
			}
		}
		return null;
	}

	public static int lengthOfLongestCommonSubstring(String s1, String s2) {
		if (s1 == null || s2 == null || s1.length() == 0 || s2.length() == 0) {
			return 0;
		}
		int start = 0;
		int maxLen = 0;
		int[][] table = new int[s1.length()][s2.length()];
		for (int i = 0; i < s1.length(); i++) {
			for (int j = 0; j < s2.length(); j++) {
				if (i == 0 || j == 0) {
					if (s1.charAt(i) == s2.charAt(j)) {
						table[i][j] = 1;
					}
					if (table[i][j] > maxLen) {
						maxLen = table[i][j];
						start = i;
					}
				} else {
					if (s1.charAt(i) == s2.charAt(j)) {
						table[i][j] = table[i - 1][j - 1] + 1;
					}
					if (table[i][j] > maxLen) {
						maxLen = table[i][j];
						start = i + 1 - maxLen;
					}
				}
			}
		}
		return s1.substring(start, start + maxLen).length();
	}
}
