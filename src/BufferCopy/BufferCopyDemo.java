package BufferCopy;

import java.io.FileInputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class BufferCopyDemo {
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
			BufferCopyVisitor visitor = new BufferCopyVisitor();
			visitor.visitDepthFirst(cu);
			for (String str : BufferCopyHelper.getMessages()) {
				System.out.println(str);
			}
		}
	}

}
