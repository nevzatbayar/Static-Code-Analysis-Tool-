package EndOfBufferAccess;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TreeVisitor;
import com.github.javaparser.ast.expr.ArrayAccessExpr;

public class EndOfBufferAccessVisitor extends TreeVisitor {

	@Override
	public void process(Node node) {

		if (node instanceof ArrayAccessExpr) {
			EndOfBufferAccessHelper.setAllChecked(false);
			EndOfBufferAccessHelper.solveForStmt(node, ((ArrayAccessExpr) node).getName(),
					((ArrayAccessExpr) node).getIndex());
		}
	}

	@Override
	public void visitDepthFirst(Node node) {
		super.visitDepthFirst(node); 
	}

}
