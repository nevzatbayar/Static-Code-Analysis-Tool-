package MissingResource;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TreeVisitor;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class MissingResourceVisitor extends TreeVisitor {

	public Table<String, Node, Symbol> st = HashBasedTable.create();

	@Override
	public void process(Node node) {
		if (node instanceof ExpressionStmt) {
			if (((ExpressionStmt) node).getExpression() instanceof AssignExpr) {
				AssignExpr asexpr = (AssignExpr) ((ExpressionStmt) node).getExpression();
				if (!(asexpr.getValue() instanceof NullLiteralExpr)) {
					MissingResourceHelper.setClosed(false);
					if (asexpr.getTarget() instanceof NameExpr) {
						if (ScaHelper.lookup(st, ((NameExpr) asexpr.getTarget()).getName(), node, false)) {
							Symbol sembol = ScaHelper.getSymbol(st, ((NameExpr) asexpr.getTarget()).getName(), node,
									false);
							sembol.setInitialize(true);// will be change
							Node parentNode = ScaHelper.getParentBlock(node, false);
							MissingResourceHelper.solveParentNode(parentNode, ((NameExpr) asexpr.getTarget()).getName(),
									asexpr.getTarget().getBeginLine(), asexpr.getTarget().getBeginColumn());
							if (!MissingResourceHelper.getClosed()) {
								String message = "Missing release resource after effective life time when using variable "
										+"'"+ ((NameExpr) asexpr.getTarget()).getName()+"'" + " at line: "
										+ asexpr.getBeginLine();
								MissingResourceHelper.addMessage(message);
							}
						}
					}
				}
			} else if (((ExpressionStmt) node).getExpression() instanceof VariableDeclarationExpr) {
				MissingResourceHelper.setClosed(false);
				VariableDeclarationExpr varexpr = (VariableDeclarationExpr) ((ExpressionStmt) node).getExpression();

				if (varexpr.getType() instanceof ReferenceType) {
					if (((ReferenceType) varexpr.getType()).getType() instanceof ClassOrInterfaceType) {
						ClassOrInterfaceType classtype = (ClassOrInterfaceType) (((ReferenceType) varexpr.getType())
								.getType());
						switch (classtype.getName()) {
						case "BufferedReader":
						case "BufferedWriter":
						case "OutputStream":
						case "InputStream":
						case "FileOutputStream":
						case "FileInputStream":
						case "FileReader":
						case "FileWriter":
						case "ObjectInputStream":
						case "ObjectOutputStream":
						case "PrintWriter":
							MissingResourceHelper.addSymbol(varexpr, st);
							break;

						default:
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void visitDepthFirst(Node node) {
		// TODO Auto-generated method stub
		super.visitDepthFirst(node);
	}

}
