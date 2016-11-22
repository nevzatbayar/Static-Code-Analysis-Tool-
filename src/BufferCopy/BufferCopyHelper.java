package BufferCopy;

import java.util.ArrayList;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr.Operator;
import com.github.javaparser.ast.stmt.IfStmt;
import com.google.common.collect.Table;

import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class BufferCopyHelper {

	// messages of bugs
	public static ArrayList<String> messages = new ArrayList<>();

	// if has a checked size of buffer will be (checked) true
	private static boolean checked = false;

	// Set Checked size of input
	public static void setChecked(boolean checked) {
		BufferCopyHelper.checked = checked;
	}

	// return bug's messages
	public static ArrayList<String> getMessages() {
		return messages;
	}

	// if assign contain name Expression of Array
	public static NameExpr containNameExprofArray(Expression expr, Table<String, Node, Symbol> st, Boolean isThis) {

		if (expr instanceof NameExpr) {
			if (ScaHelper.lookup(st, ((NameExpr) expr).getName(), ScaHelper.getParentBlock(expr, isThis), isThis)) {
				Symbol symbol = ScaHelper.getSymbol(st, ((NameExpr) expr).getName(),
						ScaHelper.getParentBlock(expr, isThis), isThis);
				if (symbol.getVartype() == Symbol.arraytype) {
					return (NameExpr) expr;
				}
			}
		}

		return null;
	}

	// Check parent if's of assign expression
	public static void SolveIfStmt(Node node, Expression target, Expression value) {

		if (node != null) {

			if (!checked) {

				switch (node.getClass().getSimpleName()) {

				// Check if parent reach block statment
				case "MethodDeclaration":
				case "EnumDeclaration":
				case "InitializerDeclaration":
				case "ConstructorDeclaration":

					// Set messages bugs
					String s = "Buffer copy Without Checking size of input when  " + value.toString() + " assign to "
							+ target.toString() + " at line: " + target.getBeginLine();
					messages.add(s);
					break;

				// Check parent if of assign Expression
				case "IfStmt":
					SolveExpr(((IfStmt) node).getCondition(), target, value);

					// Check other Parent If's
					SolveIfStmt(node.getParentNode(), target, value);
					break;

				default:
					// Get parent Node to find parent if statement
					SolveIfStmt(node.getParentNode(), target, value);
					break;
				}
			}
		}
	}

	// Check Condition of if
	public static void SolveExpr(Expression expr, Expression target, Expression value) {

		if (expr != null) {

			switch (expr.getClass().getSimpleName()) {

			case "BinaryExpr":
				// Check Binary expression if contains array
				if ((((BinaryExpr) expr).getLeft() instanceof FieldAccessExpr)
						&& (((BinaryExpr) expr).getRight() instanceof FieldAccessExpr)) {

					switch (((BinaryExpr) expr).getOperator()) {

					// Check for equals operator
					case equals:

						if (((FieldAccessExpr) ((BinaryExpr) expr).getRight()).getScope().toString()
								.equals(target.toString())
								&& ((FieldAccessExpr) ((BinaryExpr) expr).getLeft()).getScope().toString()
										.equals(value.toString())) {
							setChecked(true);
						} else if (((FieldAccessExpr) ((BinaryExpr) expr).getLeft()).getScope().toString()
								.equals(target.toString())
								&& ((FieldAccessExpr) ((BinaryExpr) expr).getRight()).getScope().toString()
										.equals(value.toString())) {
							setChecked(true);
						}

						break;

					// Check if output size is greater
					case greater:
					case greaterEquals:
						if (((FieldAccessExpr) ((BinaryExpr) expr).getLeft()).getScope().toString()
								.equals(target.toString())
								&& ((FieldAccessExpr) ((BinaryExpr) expr).getRight()).getScope().toString()
										.equals(value.toString())) {
							setChecked(true);
						}
						break;

					// Check if input size is less
					case less:
					case lessEquals:
						if (((FieldAccessExpr) ((BinaryExpr) expr).getRight()).getScope().toString()
								.equals(target.toString())
								&& ((FieldAccessExpr) ((BinaryExpr) expr).getLeft()).getScope().toString()
										.equals(value.toString())) {
							setChecked(true);
						}
						break;

					default:
						break;
					}
				} else {

					// if is not Field access expression check right and left of
					// binary expression
					switch (((BinaryExpr) expr).getOperator()) {

					// Check for equals operator
					case and:

						SolveExpr(((BinaryExpr) expr).getLeft(), target, value);
						SolveExpr(((BinaryExpr) expr).getRight(), target, value);
						break;
					default:
						break;
					}
				}

				break;

			// if condition contains enclosed
			case "EnclosedExpr":
				SolveExpr(((EnclosedExpr) expr).getInner(), target, value);
				break;
			case "UnaryExpr":
				if (((UnaryExpr)expr).getOperator() == Operator.not) {
					SolveExpr1(((UnaryExpr)expr).getExpr(), target, value); 
				} 
				break;
			// Break for other case
			default:
				break;
			}
		}

	}
    
	public static void SolveExpr1(Expression expr, Expression target, Expression value) {

		if (expr != null) {

			switch (expr.getClass().getSimpleName()) {

			case "BinaryExpr":
				// Check Binary expression if contains array
				if ((((BinaryExpr) expr).getLeft() instanceof FieldAccessExpr)
						&& (((BinaryExpr) expr).getRight() instanceof FieldAccessExpr)) {

					switch (((BinaryExpr) expr).getOperator()) {

					// Check for equals operator
					case equals:

						if (((FieldAccessExpr) ((BinaryExpr) expr).getRight()).getScope().toString()
								.equals(value.toString())
								&& ((FieldAccessExpr) ((BinaryExpr) expr).getLeft()).getScope().toString()
										.equals(target.toString())) {
							setChecked(true);
						} else if (((FieldAccessExpr) ((BinaryExpr) expr).getLeft()).getScope().toString()
								.equals(value.toString())
								&& ((FieldAccessExpr) ((BinaryExpr) expr).getRight()).getScope().toString()
										.equals(target.toString())) {
							setChecked(true);
						}

						break;

					// Check if output size is greater
					case greater:
					case greaterEquals:
						if (((FieldAccessExpr) ((BinaryExpr) expr).getLeft()).getScope().toString()
								.equals(value.toString())
								&& ((FieldAccessExpr) ((BinaryExpr) expr).getRight()).getScope().toString()
										.equals(target.toString())) {
							setChecked(true);
						}
						break;

					// Check if input size is less
					case less:
					case lessEquals:
						if (((FieldAccessExpr) ((BinaryExpr) expr).getRight()).getScope().toString()
								.equals(value.toString())
								&& ((FieldAccessExpr) ((BinaryExpr) expr).getLeft()).getScope().toString()
										.equals(target.toString())) {
							setChecked(true);
						}
						break;

					default:
						break;
					}
				} else {

					// if is not Field access expression check right and left of
					// binary expression
					switch (((BinaryExpr) expr).getOperator()) {

					// Check for equals operator
					case and:

						SolveExpr1(((BinaryExpr) expr).getLeft(), target, value);
						SolveExpr1(((BinaryExpr) expr).getRight(), target, value);
						break;
					default:
						break;
					}
				}

				break;

			// if condition contains enclosed
			case "EnclosedExpr":
				SolveExpr1(((EnclosedExpr) expr).getInner(), target, value);
				break;

			// Break for other case
			default:
				break;
			}
		}

	}
}
