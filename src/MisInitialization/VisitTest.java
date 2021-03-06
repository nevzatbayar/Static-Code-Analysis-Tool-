package MisInitialization;

import java.io.FileInputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import ScaHelper.ExpressionHelper;
import SymbolTable.Symbol;

public class VisitTest {

	public static void main(String[] args) throws Exception {
		// Parse file
		FileInputStream in = new FileInputStream("C:\\Users\\�mer\\workspace\\Copy of static-analiz\\src\\sca\\fxsd.java");

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
			Visitor visitor = new Visitor();
			visitor.visitDepthFirst(cu);
			Table<String, Node, Symbol> st = visitor.st;
			// System.out.print("De�i�ken ismi" + "\t");
			// System.out.print("Tan�mland��� sat�r" + "\t");
			// System.out.print("De�itirildi�i sat�r" + "\t");
			// System.out.print("Kullan�ld�� sat�r" + "\t");
			// System.out.print("De�i�ken tipi" + "\t");
			// System.out.print("ilk de�er" + "\t");
			// System.out.println("De�i�ken t�r�");
			for (Cell<String, Node, Symbol> cell : st.cellSet()) {
				// System.out.print(cell.getRowKey()+"\t"+
				// cell.getValue().getName()+"\t");

				if ((cell.getValue().getReferencedline() == -1
						&& !cell.getValue().getKind().equals("Method parameter"))) {

					System.out.print(cell.getValue().getName() + "\t");
					System.out.print(cell.getValue().getDeclaretedline() + "\t");
					System.out.print(cell.getValue().getChangedline() + "\t");
					System.out.print(cell.getValue().getReferencedline() + "\t");
					System.out.print(cell.getValue().getType() + "\t");
					System.out.print(cell.getValue().getInitialize() + "\t");
					System.out.print(cell.getValue().getKind());
					System.out.println(cell.getValue().getVartype());

				}
			}

			for (String str : ExpressionHelper.getMessages()) {
				System.out.println(str);
			}
		}
	}

}
