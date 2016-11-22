package UnCheckedReturnParameter;

import java.util.ArrayList;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;

public class SolveMethodCallExpr {

	// Messages of bugs
	private static ArrayList<String> messages = new ArrayList<>();
	// Check if controlled
	private static boolean checked = false;

	public static void setChecked(boolean checked) {
		SolveMethodCallExpr.checked = checked;
	}

	// Get messages bugs
	public static ArrayList<String> getMessages() {
		return messages;
	}

	// check is contain if
	public static void solveIfStatement(Node node, MethodCallExpr methodCallExpr) {

		if (node != null) {

			if (!checked) {

				switch (node.getClass().getSimpleName()) {

				// if is not contain or check set messages
				case "MethodDeclaration":
				case "EnumDeclaration":
				case "InitializerDeclaration":
				case "ConstructorDeclaration":
					String s = "Unchecked return value when using " + methodCallExpr + " at line: "
							+ methodCallExpr.getBeginLine();
					messages.add(s);
					break;

				// Check parent if for controller
				case "IfStmt":
					SolveExpr(((IfStmt) node).getCondition(), methodCallExpr);
					solveIfStatement(node.getParentNode(), methodCallExpr);
					break;

				// Get parent of statement
				default:
					solveIfStatement(node.getParentNode(), methodCallExpr);
					break;
				}
			}
		}

	}

	// Check if if conditions contain method call
	public static void SolveExpr(Expression expr, MethodCallExpr methodCallExpr) {

		if (expr != null) {
			switch (expr.getClass().getSimpleName()) {
			case "MethodCallExpr":
				if (methodCallExpr.getName().equals(((MethodCallExpr) expr).getName())) {
					if (methodCallExpr.getScope() != null && ((MethodCallExpr) expr).getScope() != null) {
						if (methodCallExpr.getScope().toString()
								.equals(((MethodCallExpr) expr).getScope().toString())) {
							checked = true;
						}
					} else if (methodCallExpr.getScope() == null && ((MethodCallExpr) expr).getScope() == null) {
						checked = true;
						 // This else added after
					}
				}
				break;

			case "BinaryExpr":
				SolveExpr(((BinaryExpr) expr).getLeft(), methodCallExpr);
				SolveExpr(((BinaryExpr) expr).getRight(), methodCallExpr);
				break;
			case "InstanceOfExpr":
				SolveExpr(((InstanceOfExpr) expr).getExpr(), methodCallExpr);
				break;

			case "EnclosedExpr":
				SolveExpr(((EnclosedExpr) expr).getInner(), methodCallExpr);
				break;
			case "CastExpr":
				SolveExpr(((CastExpr) expr).getExpr(), methodCallExpr);
				break;
			case "FieldAccessExpr":
				SolveExpr(((FieldAccessExpr) expr).getScope(), methodCallExpr);
				break;
			case "UnaryExpr":
				SolveExpr(((UnaryExpr) expr).getExpr(), methodCallExpr);
				break;
			default:
				break;
			}
		}

	}
}
