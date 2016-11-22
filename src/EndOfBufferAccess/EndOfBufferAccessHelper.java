package EndOfBufferAccess;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;

public class EndOfBufferAccessHelper {

	// Messages of bugs
	private static ArrayList<String> messages = new ArrayList<>();

	// if has a checked size of buffer will be (checked) true
	private static boolean checked = false; // use for for statement
	private static boolean checked1 = false;// use for if statement i>0
	private static boolean checked2 = false;// use for if statement i<a length
	private static boolean checked3 = false;// use for if statement a.length !=
											// 0 or 0 != a.length and if index
											// equals to zero

	// Set Checked size of input
	public static void setChecked(boolean checked) {
		EndOfBufferAccessHelper.checked = checked;
	}

	public static void setAllChecked(boolean checked) {
		EndOfBufferAccessHelper.checked = checked;
		EndOfBufferAccessHelper.checked1 = checked;
		EndOfBufferAccessHelper.checked2 = checked;
		EndOfBufferAccessHelper.checked3 = checked;
	}

	// Get message of bug
	public static ArrayList<String> getMessages() {
		return messages;
	}

	// Check if is inside for and index between 0 and its length
	public static void solveForStmt(Node node, Expression name, Expression index) {

		if (node != null) {

			if (!checked) {

				switch (node.getClass().getSimpleName()) {

				// Check for parent reach block statement
				case "MethodDeclaration":
				case "EnumDeclaration":
				case "InitializerDeclaration":
				case "ConstructorDeclaration":

					solveIStmt(name.getParentNode(), name, index);
					break;

				// Check parent for of array access Expression
				case "ForStmt":
					checkForExpr(((ForStmt) node).getInit(), ((ForStmt) node).getCompare(), name, index);

					// Check other Parent for's
					solveForStmt(node.getParentNode(), name, index);
					break;

				default:
					// Get parent Node to find parent for statement
					solveForStmt(node.getParentNode(), name, index);
					break;
				}
			}
		}
	}

	// Check if is inside if and index between 0 and its length
	public static void solveIStmt(Node node, Expression name, Expression index) {

		if (node != null) {

			if (!(checked1 && checked2) && !checked3) {

				switch (node.getClass().getSimpleName()) {

				// Check for parent reach block statement
				case "MethodDeclaration":
				case "EnumDeclaration":
				case "InitializerDeclaration":
				case "ConstructorDeclaration":

					// Set messages bugs
					String s = "End of buffer access may occurs when use array " + name.toString() + " with index "
							+ index.toString() + " at line: " + name.getBeginLine();
					messages.add(s);
					break;

				// Check parent if of array access expression
				case "IfStmt":
					checkIfExpr(((IfStmt) node).getCondition(), name, index);

					// Check other Parent if's
					solveIStmt(node.getParentNode(), name, index);
					break;

				default:
					// Get parent Node to find parent if statement
					solveIStmt(node.getParentNode(), name, index);
					break;
				}
			}
		}
	}

	// Check if statement condition
	public static void checkIfExpr(Expression expr, Expression name, Expression index) {
		if (expr != null) {
			switch (expr.getClass().getSimpleName()) {
			case "BinaryExpr":
				BinaryExpr binaryexpr = (BinaryExpr) expr;
				switch (binaryexpr.getOperator()) {

				case less:
					if (binaryexpr.getLeft() instanceof IntegerLiteralExpr
							&& binaryexpr.getRight().toString().equals(index.toString())) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getLeft()).getValue();
						if (Integer.parseInt(inexpr) >= -1) {
							checked1 = true;
						}
					}

					if (binaryexpr.getLeft().toString().equals(index.toString())) {

						if (binaryexpr.getRight() instanceof FieldAccessExpr) {
							FieldAccessExpr fAExpr = (FieldAccessExpr) binaryexpr.getRight();
							if (fAExpr.getField().equals("length")
									&& fAExpr.getScope().toString().equals(name.toString())) {
								checked2 = true;
							}
						}
					}
					break;
				case lessEquals:
					if (binaryexpr.getLeft() instanceof IntegerLiteralExpr
							&& binaryexpr.getRight().toString().equals(index.toString())) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getLeft()).getValue();
						if (Integer.parseInt(inexpr) >= 0) {
							checked1 = true;
						}
					}
					break;
				case greater:
					if (binaryexpr.getRight() instanceof IntegerLiteralExpr
							&& binaryexpr.getLeft().toString().equals(index.toString())) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getRight()).getValue();
						if (Integer.parseInt(inexpr) >= -1) {
							checked1 = true;
						}
					}

					if (binaryexpr.getRight().toString().equals(index.toString())) {

						if (binaryexpr.getLeft() instanceof FieldAccessExpr) {
							FieldAccessExpr fAExpr = (FieldAccessExpr) binaryexpr.getLeft();
							if (fAExpr.getField().equals("length")
									&& fAExpr.getScope().toString().equals(name.toString())) {
								checked2 = true;
							}
						}
					}
					break;
				case greaterEquals:

					if (binaryexpr.getRight() instanceof IntegerLiteralExpr
							&& binaryexpr.getLeft().toString().equals(index.toString())) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getRight()).getValue();
						if (Integer.parseInt(inexpr) >= 0) {
							checked1 = true;
						}
					}
					break;

				case notEquals:
					if (binaryexpr.getRight() instanceof IntegerLiteralExpr
							&& binaryexpr.getLeft().toString().equals(name.toString()+".length")) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getRight()).getValue();
						if (Integer.parseInt(inexpr) == 0) {
							checked3 = true; 
						}
					}

					if (binaryexpr.getLeft() instanceof IntegerLiteralExpr
							&& binaryexpr.getRight().toString().equals(name.toString()+".length")) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getLeft()).getValue();
						if (Integer.parseInt(inexpr) == 0) {
							checked3 = true;
						}
					}

					break;
				case and:
					checkIfExpr(binaryexpr.getRight(), name, index);
					checkIfExpr(binaryexpr.getLeft(), name, index);
					break;
				default:
					break;
				}
				break;

			case "EnclosedExpr":
				checkIfExpr(((EnclosedExpr) expr).getInner(), name, index);
			default:
				break;
			}
		}
	}

	// Check if for condition between 0 and array length
	public static void checkForExpr(List<Expression> initialize, Expression condition, Expression name,
			Expression index) {

		if (condition != null && initialize != null) {

			// Check condition
			if (condition instanceof BinaryExpr) {

				BinaryExpr brexpr = ((BinaryExpr) condition);

				switch (brexpr.getOperator()) {

				case greater:

					if (brexpr.getLeft().toString().equals(index.toString())
							&& (brexpr.getRight() instanceof IntegerLiteralExpr)) {
						String intexpr = ((IntegerLiteralExpr) brexpr.getRight()).getValue();
						checkGreaterCase1(initialize, brexpr, name, index, true, intexpr);
					}
					if (brexpr.getRight().toString().equals(index.toString())) {
						if (brexpr.getLeft() instanceof FieldAccessExpr) {
							FieldAccessExpr fAExpr = (FieldAccessExpr) brexpr.getLeft();
							if (fAExpr.getField().equals("length")
									&& fAExpr.getScope().toString().equals(name.toString())) {
								checkGreaterCase2(initialize, brexpr, name, index);
							}
						}
					}

					break;
				case greaterEquals:
					if (brexpr.getLeft().toString().equals(index.toString())
							&& (brexpr.getRight() instanceof IntegerLiteralExpr)) {
						String intexpr = ((IntegerLiteralExpr) brexpr.getRight()).getValue();
						checkGreaterCase1(initialize, brexpr, name, index, false, intexpr);
					}
					break;

				case less:
					if (brexpr.getLeft().toString().equals(index.toString())) {
						if (brexpr.getRight() instanceof FieldAccessExpr) {
							FieldAccessExpr fAExpr = (FieldAccessExpr) brexpr.getRight();
							if (fAExpr.getField().equals("length")
									&& fAExpr.getScope().toString().equals(name.toString())) {
								checkGreaterCase2(initialize, brexpr, name, index);
							}
						}
					}

					if (brexpr.getRight().toString().equals(index.toString())
							&& (brexpr.getLeft() instanceof IntegerLiteralExpr)) {
						String intexpr = ((IntegerLiteralExpr) brexpr.getLeft()).getValue();
						checkGreaterCase1(initialize, brexpr, name, index, true, intexpr);
					}
					break;

				case lessEquals:
					if (brexpr.getLeft().toString().equals(index.toString())
							&& (brexpr.getRight() instanceof IntegerLiteralExpr)) {
						String intexpr = ((IntegerLiteralExpr) brexpr.getLeft()).getValue();
						checkGreaterCase1(initialize, brexpr, name, index, false, intexpr);
					}
					break;

				default:
					break;
				}

			}

		}

	}

	// Check if condition i>0
	public static void checkGreaterCase1(List<Expression> initialize, BinaryExpr brexpr, Expression name,
			Expression index, boolean greater, String intexpr) {

		if ((Integer.parseInt(intexpr) >= -1 && greater) || (Integer.parseInt(intexpr) >= 0 && !greater)) {

			for (Expression expr : initialize) {

				if (expr instanceof VariableDeclarationExpr) {
					for (VariableDeclarator var : ((VariableDeclarationExpr) expr).getVars()) {
						if (var.getId().getName().equals(index.toString()) && var.getInit() != null) {
							if (var.getInit() instanceof FieldAccessExpr) {
								FieldAccessExpr fieldexpr = (FieldAccessExpr) var.getInit();
								if (fieldexpr.getField().equals("length")
										&& fieldexpr.getScope().toString().equals(name.toString())) {
									setChecked(true);
								}
							}
						}
					}
				} else if (expr instanceof AssignExpr) {
					AssignExpr asexpr = (AssignExpr) expr;
					if (asexpr.getTarget().toString().equals(index.toString())) {
						if (asexpr.getValue() instanceof FieldAccessExpr) {
							FieldAccessExpr fieldexpr = (FieldAccessExpr) asexpr.getValue();
							if (fieldexpr.getField().equals("length")
									&& fieldexpr.getScope().toString().equals(name.toString())) {
								setChecked(true);
							}
						}
					}
				}
			}
		}
	}

	// Check if condition a.length > i
	public static void checkGreaterCase2(List<Expression> initialize, BinaryExpr brexpr, Expression name,
			Expression index) {
		for (Expression expr : initialize) {
			if (expr instanceof VariableDeclarationExpr) {
				for (VariableDeclarator var : ((VariableDeclarationExpr) expr).getVars()) {
					if (var.getId().getName().equals(index.toString())) {
						if (var.getInit() instanceof IntegerLiteralExpr) {
							IntegerLiteralExpr intexpr = (IntegerLiteralExpr) var.getInit();
							if (Integer.parseInt(intexpr.toString()) >= 0) {
								setChecked(true);
							}
						}
					}
				}
			} else if (expr instanceof AssignExpr) {
				AssignExpr asExpr = (AssignExpr) expr;
				if (asExpr.getTarget().toString().equals(index.toString())
						&& asExpr.getValue() instanceof IntegerLiteralExpr) {
					IntegerLiteralExpr intexpr = (IntegerLiteralExpr) asExpr.getValue();
					if (Integer.parseInt(intexpr.toString()) >= 0) {
						setChecked(true);
					}
				}
			}
		}
	}
}
