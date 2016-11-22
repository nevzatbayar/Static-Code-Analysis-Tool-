package UnCheckedReturnParameter;

import java.io.FileInputStream;
//import java.util.ArrayList;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class UnCheckedReturn {
   
	public static void main(String[] args) throws Exception {
		FileInputStream in = new FileInputStream("C:\\Users\\ömer\\workspace\\Copy of static-analiz\\src\\sca\\fxsd.java");

		CompilationUnit cu = null;
		
		try {
			// parse the file
			cu = JavaParser.parse(in);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		} finally {
			in.close();
		}
		if (cu != null) {
			UnCheckedVisitor visitor = new UnCheckedVisitor();
			visitor.visitDepthFirst(cu);
//			ArrayList<String> aa = new ArrayList<>();
//			aa = SolveMethodCallExpr.getMessages();
			for (String str : SolveMethodCallExpr.getMessages()) {
				 System.out.println(str);	
				}    
		}
	}
}
