package Recursion;

import java.io.FileInputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class RecursionDemo {
	public static void main(String[] args) throws Exception {
		FileInputStream in = new FileInputStream("C:\\Users\\bayar\\workspace\\static-analiz\\src\\sca\\Test.java");
		CompilationUnit cu = null;
		try {
			cu = JavaParser.parse(in);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		} finally {
			in.close();
		}
		if (cu != null) {
			RecursionVisitor visitor = new RecursionVisitor();
			visitor.visitDepthFirst(cu);
			for (String str : SolveRecursion.getMessages()) {
				System.out.println(str);
			}
		}
	}
}