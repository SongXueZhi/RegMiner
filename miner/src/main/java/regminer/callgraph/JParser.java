package regminer.callgraph;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import regminer.maven.MavenManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class JParser {

    public void  setSrcRoot(File dir) throws Exception {
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(dir,"src/main/java"));
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(javaParserTypeSolver);
        MavenManager mavenManager = new MavenManager();
        List<String> mavenList = mavenManager.readAllDependency(new File(dir,"pom.xml"));
        for (String depen : mavenList){
            combinedTypeSolver.add(JarTypeSolver.getJarTypeSolver(depen));
        }
        combinedTypeSolver.add(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
    }
    public CompilationUnit getCodeUnit(File file) throws FileNotFoundException {
        return StaticJavaParser.parse(file);
    }
}
