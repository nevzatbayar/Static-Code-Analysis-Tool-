package MissingResource;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.google.common.collect.Table;

import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class MissingResourceHelper {

	// messages of bugs
	private static ArrayList<String> messages = new ArrayList<>();

	private static boolean closed = false;

	public static void setClosed(boolean closed) {
		MissingResourceHelper.closed = closed;
	}

	public static boolean getClosed() {
		return MissingResourceHelper.closed;
	}

	// return bug's messages
	public static ArrayList<String> getMessages() {
		return messages;
	}

	public static void addMessage(String message) {
		messages.add(message);
	}

	public static void solveStatements(List<Statement> stmts, String name, int beginLine, int beginColumn) {
		for (Statement stmt : stmts) {
			if (closed) {
				break;
			}
			if (beginLine > stmt.getBeginLine()) {
				continue;
			}

			if (beginLine == stmt.getBeginLine() && beginColumn > stmt.getBeginColumn()) {
				continue;
			}

			if (stmt instanceof ExpressionStmt) {
				Expression expr = ((ExpressionStmt) stmt).getExpression();
				if (expr instanceof MethodCallExpr) {
					MethodCallExpr mcexpr = (MethodCallExpr) expr;
					if (mcexpr.getName().equals("close") && mcexpr.getScope().toString().equals(name)) {
						setClosed(true);
					}
				} else {
					continue;
				}
			}

			if (stmt instanceof IfStmt) {
				if (((IfStmt) stmt).getCondition() instanceof BinaryExpr) {
					BinaryExpr bexpr = (BinaryExpr) ((IfStmt) stmt).getCondition();
					switch (bexpr.getOperator()) {
					case notEquals:
						boolean a = false;
						if (bexpr.getRight().toString().equals(name) && bexpr.getLeft() instanceof NullLiteralExpr) {
							a = true;
						} else if (bexpr.getLeft().toString().equals(name)
								&& bexpr.getRight() instanceof NullLiteralExpr) {
							a = true;
						}

						if (a) {
							solveStatements(((BlockStmt) ((IfStmt) stmt).getThenStmt()).getStmts(), name, beginLine,
									beginColumn);
						}

						break;

					default:
						break;
					}
					
				
				}
			}
			// add Try statement			
			if(stmt instanceof TryStmt){
				solveStatements(((BlockStmt) ((TryStmt) stmt).getTryBlock()).getStmts(), name, beginLine,
						beginColumn);
			}
		}
	}

	public static void solveParentNode(Node node, String name, int beginLine, int beginColumn) {
		if (node != null) {
			switch (node.getClass().getSimpleName()) {

			case "ConstructorDeclaration":
				solveStatements(((ConstructorDeclaration) node).getBlock().getStmts(), name, beginLine, beginColumn);
				break;
			case "InitializerDeclaration":
				solveStatements(((InitializerDeclaration) node).getBlock().getStmts(), name, beginLine, beginColumn);
				break;
			case "SynchronizedStmt":
				solveStatements(((SynchronizedStmt) node).getBlock().getStmts(), name, beginLine, beginColumn);
				break;
			case "MethodDeclaration":
				solveStatements(((MethodDeclaration) node).getBody().getStmts(), name, beginLine, beginColumn);
				break;
			case "ForStmt":
				solveStatements(((BlockStmt) ((ForStmt) node).getBody()).getStmts(), name, beginLine, beginColumn);
				break;
			case "ForeachStmt":
				solveStatements(((BlockStmt) ((ForeachStmt) node).getBody()).getStmts(), name, beginLine, beginColumn);
				break;
			case "WhileStmt":
				solveStatements(((BlockStmt) ((WhileStmt) node).getBody()).getStmts(), name, beginLine, beginColumn);
				break;
			case "DoStmt":
				solveStatements(((BlockStmt) ((WhileStmt) node).getBody()).getStmts(), name, beginLine, beginColumn);
				break;
			case "TryStmt":
				if (((TryStmt) node).getFinallyBlock() != null) {
					solveStatements(((TryStmt) node).getFinallyBlock().getStmts(), name, beginLine, beginColumn);
				}
				break;
			case "SwitchEntryStmt":
				solveStatements(((SwitchEntryStmt) node).getStmts(), name, beginLine, beginColumn);
				break;
			case "BlockStmt":
				solveStatements(((BlockStmt) node).getStmts(), name, beginLine, beginColumn);
				break;
			default:
				break;
			}
		}
	}

	public static void addSymbol(VariableDeclarationExpr varexpr, Table<String, Node, Symbol> st) {
		List<VariableDeclarator> varDList;
		varDList = (List<VariableDeclarator>) varexpr.getVars();
		for (VariableDeclarator var : varDList) {
			boolean a = false;
			if (var.getInit() != null) {

				if (!(var.getInit() instanceof NullLiteralExpr)) {
					a = true;
				}
			}

			Node ParentBlock = ScaHelper.getParentBlock(varexpr, false);

			Symbol symbol = new Symbol(var.getId().getName(), var.getBeginLine(), varexpr.getType(), ParentBlock, a);
			if (a) {
				symbol.setChangedline(var.getBeginLine());
			}

			st.put(var.getId().getName(), ParentBlock, symbol);
			if (a) {
				MissingResourceHelper.solveParentNode(ParentBlock, var.getId().getName(), var.getId().getBeginLine(),
						var.getId().getBeginColumn());
				if (!closed) {
					String message = "Missing release resource after effective life time when using variable " + "'"
							+ var.getId().getName() + "'" + " at line: " + var.getId().getBeginLine();
					MissingResourceHelper.addMessage(message);
				}
			}

		}

	}
}
