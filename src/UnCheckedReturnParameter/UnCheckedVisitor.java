package UnCheckedReturnParameter;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TreeVisitor;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;

public class UnCheckedVisitor extends TreeVisitor {

	@Override
	public void process(Node node) {
		if (node instanceof MethodCallExpr) {
           if(!(node.getParentNode() instanceof ExpressionStmt)){
        	   SolveMethodCallExpr.setChecked(false);
        	   SolveMethodCallExpr.solveIfStatement(node.getParentNode(), (MethodCallExpr)node);
           }
           // if inside if condition just method call don't find
		}
	}

	@Override
	public void visitDepthFirst(Node node) {
		super.visitDepthFirst(node);
	}

}
