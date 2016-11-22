package DeclarationCatches;

import java.util.HashMap;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodDeclarationVisitor extends VoidVisitorAdapter<HashMap<String, MethodDeclaration>> {
	@Override
	public void visit(MethodDeclaration n, HashMap<String, MethodDeclaration> arg) {
		arg.put(n.getName(), n);
		super.visit(n, arg);
	}

}
