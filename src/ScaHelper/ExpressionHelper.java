package ScaHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.google.common.collect.Table;

import SymbolTable.Symbol;

public class ExpressionHelper {

	private static ArrayList<String> messages = new ArrayList<>();

	private static boolean checked = false;

	public static void setChecked(boolean bool) {
		checked = bool;
	}

	public static ArrayList<String> getMessages() {
		return messages;
	}

	public static void SolveAssignExpr(Expression expr, Table<String, Node, Symbol> st, Boolean isThis) {
		String name;
		if (expr != null) {
			switch (expr.getClass().getSimpleName()) {
			case "ArrayAccessExpr":
				SolveExpr(((ArrayAccessExpr) expr).getIndex(), st, isThis);
				SolveExpr(((ArrayAccessExpr) expr).getName(), st, isThis);
				break;
			case "FieldAccessExpr":
				if (isThisExpr(((FieldAccessExpr) expr))) {

					if (((FieldAccessExpr) expr).getFieldExpr() != null) {
						((FieldAccessExpr) expr).getFieldExpr().setParentNode((FieldAccessExpr) expr);
						;
						SolveAssignExpr(((FieldAccessExpr) expr).getFieldExpr(), st, true);
					}
				} else {
					if (((FieldAccessExpr) expr).getScope() != null) {
						SolveExpr(((FieldAccessExpr) expr).getScope(), st, isThis);
					}
				}
				break;

			case "MethodCallExpr":
				if (((MethodCallExpr) expr).getScope() != null) {
					SolveExpr(((MethodCallExpr) expr).getScope(), st, isThis);
				}

				if (((MethodCallExpr) expr).getArgs() != null) {
					for (Expression a : ((MethodCallExpr) expr).getArgs()) {
						SolveExpr(a, st, isThis);
					}
				}

				break;
			case "NameExpr":
				name = ((NameExpr) expr).getName().toString();
				if (ScaHelper.lookup(st, name, ScaHelper.getParentBlock(expr, isThis), isThis)) {
					Symbol sembol = ScaHelper.getSymbol(st, name, ScaHelper.getParentBlock(expr, isThis), isThis);
					sembol.setChangedline(expr.getBeginLine());
					Node insideBlock = Getparentblock(expr);
					Node parentinsideblock = Getparentblock(insideBlock.getParentNode());
					if (!sembol.getInitialize()) {
						if (insideBlock.equals(sembol.getParentBlock())) {
							sembol.setInitialize(true);
							sembol.setInsideBlock(false);
							sembol.setInitialInsideBlock(null);
							sembol.setInitializedline(-1);
						} else {

							switch (insideBlock.getClass().getSimpleName()) {
							case "ClassOrInterfaceDeclaration":
							case "EnumDeclaration":
							case "ConstructorDeclaration":
							case "MethodDeclaration":
							case "InitializerDeclaration":
								sembol.setInitialize(true);
								sembol.setInsideBlock(false);
								sembol.setInitialInsideBlock(null);
								sembol.setInitializedline(-1);
								break;
							case "BlockStmt":

								switch (parentinsideblock.getClass().getSimpleName()) {
								case "ClassOrInterfaceDeclaration":
								case "EnumDeclaration":
								case "ConstructorDeclaration":
								case "MethodDeclaration":
								case "InitializerDeclaration":
									IfStmt ifstateament = (IfStmt) insideBlock.getParentNode();
									if (ifstateament.getElseStmt() != null) {
										if (ifstateament.getElseStmt().equals(insideBlock)) {

											solveIfStmt((BlockStmt) ifstateament.getThenStmt(), name, sembol);
											if (checked) {
												sembol.setInitialize(true);
												sembol.setInsideBlock(false);
												sembol.setInitialInsideBlock(null);
												sembol.setInitializedline(-1);
											} else {
												sembol.setInsideBlock(true);
												sembol.setInitialInsideBlock(insideBlock);
												sembol.setInitializedline(expr.getBeginLine());
											}
										} else {
											sembol.setInsideBlock(true);
											sembol.setInitialInsideBlock(insideBlock);
											sembol.setInitializedline(expr.getBeginLine());
										}
									} else {
										sembol.setInsideBlock(true);
										sembol.setInitialInsideBlock(insideBlock);
										sembol.setInitializedline(expr.getBeginLine());
									}
									break;

								default:
									if (!ifinitalizeinsideParent(sembol, insideBlock)) {
										IfStmt ifsteament = (IfStmt) insideBlock.getParentNode();
										if (ifsteament.getElseStmt() != null) {
											if (ifsteament.getElseStmt().equals(insideBlock)) {

												solveIfStmt((BlockStmt) ifsteament.getThenStmt(), name, sembol);
												if (checked) {

													sembol.setInsideBlock(true);
													sembol.setInitialInsideBlock(parentinsideblock);
													sembol.setInitializedline(expr.getBeginLine());
												} else {
													sembol.setInsideBlock(true);
													sembol.setInitialInsideBlock(insideBlock);
													sembol.setInitializedline(expr.getBeginLine());
												}
											} else {
												sembol.setInsideBlock(true);
												sembol.setInitialInsideBlock(insideBlock);
												sembol.setInitializedline(expr.getBeginLine());
											}
										} else {
											sembol.setInsideBlock(true);
											sembol.setInitialInsideBlock(insideBlock);
											sembol.setInitializedline(expr.getBeginLine());
										}

									}
									break;

								}

								break;
							case "SwitchEntryStmt":

								switch (parentinsideblock.getClass().getSimpleName()) {
								case "ClassOrInterfaceDeclaration":
								case "EnumDeclaration":
								case "ConstructorDeclaration":
								case "MethodDeclaration":
								case "InitializerDeclaration":
									Expression label = ((SwitchEntryStmt) insideBlock).getLabel();
									if (label == null) {
										if (solveSwitchStmt(
												(SwitchStmt) (((SwitchEntryStmt) insideBlock).getParentNode()), name)) {
											sembol.setInitialize(true);
											sembol.setInsideBlock(false);
											sembol.setInitialInsideBlock(null);
											sembol.setInitializedline(-1);

										} else {
											sembol.setInsideBlock(true);
											sembol.setInitialInsideBlock(insideBlock);
											sembol.setInitializedline(expr.getBeginLine());
										}
									} else {
										sembol.setInsideBlock(true);
										sembol.setInitialInsideBlock(insideBlock);
										sembol.setInitializedline(expr.getBeginLine());
									}
									break;
								default:
									if (!ifinitalizeinsideParent(sembol, insideBlock)) {
										Expression label2 = ((SwitchEntryStmt) insideBlock).getLabel();
										if (label2 == null) {
											if (solveSwitchStmt(
													(SwitchStmt) (((SwitchEntryStmt) insideBlock).getParentNode()),
													name)) {

												sembol.setInsideBlock(true);
												sembol.setInitialInsideBlock(parentinsideblock);
												sembol.setInitializedline(expr.getBeginLine());

											} else {
												sembol.setInsideBlock(true);
												sembol.setInitialInsideBlock(insideBlock);
												sembol.setInitializedline(expr.getBeginLine());
											}
										} else {
											sembol.setInsideBlock(true);
											sembol.setInitialInsideBlock(insideBlock);
											sembol.setInitializedline(expr.getBeginLine());
										}
									}
									break;
								}
								break;
							default:
								sembol.setInsideBlock(true);
								sembol.setInitialInsideBlock(insideBlock);
								sembol.setInitializedline(expr.getBeginLine());
								break;
							}
						}
					}

					ScaHelper.setSymbol(st, name, sembol.getParentBlock(), sembol);
				}

				break;
			case "AssignExpr":
				// if (((AssignExpr) expr).getValue() instanceof
				// NullLiteralExpr) {
				// SolveNullExpr(((AssignExpr) expr).getTarget(), st, isThis);
				// }else {
				SolveExpr(((AssignExpr) expr).getValue(), st, isThis);
				if (((AssignExpr) expr).getOperator() != Operator.assign) {
					SolveExpr(((AssignExpr) expr).getTarget(), st, isThis);
				}
				SolveAssignExpr(((AssignExpr) expr).getTarget(), st, isThis);
				// }
				break;
			case "ThisExpr":
				if (((ThisExpr) expr).getClassExpr() != null) {
					SolveExpr(((ThisExpr) expr).getClassExpr(), st, true);
				}
				break;
			default:
				break;

			}
		}
	}

	public static boolean ifinitalizeinsideParent(Symbol symbol, Node parentbolock) {

		if (symbol.isInsideBlock()) {
			if (symbol.getInitialInsideBlock().equals(parentbolock)) {
				return false;
			}
			if (symbol.getInitialInsideBlock().contains(parentbolock)) {
				return true;
			}
		}
		return false;
	}

	public static void SolveExpr(Expression expr, Table<String, Node, Symbol> st, Boolean isThis) {
		String name;
		if (expr != null) {
			switch (expr.getClass().getSimpleName()) {
			case "BinaryExpr":
				SolveExpr(((BinaryExpr) expr).getLeft(), st, isThis);
				SolveExpr(((BinaryExpr) expr).getRight(), st, isThis);
				break;
			case "AssignExpr":
				// new added
				SolveExpr(((AssignExpr) expr).getValue(), st, isThis);
				if (((AssignExpr) expr).getOperator() != Operator.assign) {
					SolveExpr(((AssignExpr) expr).getTarget(), st, isThis);
				}
				SolveAssignExpr(((AssignExpr) expr).getTarget(), st, isThis);
				break;
				
			case "ArrayAccessExpr":
				SolveExpr(((ArrayAccessExpr) expr).getIndex(), st, isThis);
				SolveExpr(((ArrayAccessExpr) expr).getName(), st, isThis);
				break;

			case "EnclosedExpr":

				SolveExpr(((EnclosedExpr) expr).getInner(), st, isThis);

				break;
			case "FieldAccessExpr":
				if (isThisExpr(((FieldAccessExpr) expr))) {
					((FieldAccessExpr) expr).getFieldExpr().setParentNode((FieldAccessExpr) expr);
					;
					SolveExpr(((FieldAccessExpr) expr).getFieldExpr(), st, true);
				} else {
					SolveExpr(((FieldAccessExpr) expr).getScope(), st, isThis);
				}
				break;
			case "InstanceOfExpr":
				SolveExpr(((InstanceOfExpr) expr).getExpr(), st, isThis);
				break;
			case "MethodCallExpr":
				if (((MethodCallExpr) expr).getScope() != null) {
					SolveExpr(((MethodCallExpr) expr).getScope(), st, isThis);
				}

				if (((MethodCallExpr) expr).getArgs() != null) {
					for (Expression a : ((MethodCallExpr) expr).getArgs()) {
						SolveExpr(a, st, isThis);
					}
				}
				break;
			case "NameExpr":
				name = ((NameExpr) expr).getName().toString();
				if (ScaHelper.lookup(st, name, ScaHelper.getParentBlock(expr, isThis), isThis)) {
					Symbol sembol = ScaHelper.getSymbol(st, name, ScaHelper.getParentBlock(expr, isThis), isThis);
					sembol.setReferencedline(expr.getBeginLine());
					Node insideBlock = Getparentblock(expr);
					if (!sembol.getInitialize()) {
						if (sembol.isInsideBlock()) {
							if (!sembol.getInitialInsideBlock().equals(insideBlock)) { // else
																						// dikkat

								if (!(sembol.getInitialInsideBlock().contains(insideBlock)
										&& expr.getBeginLine() > sembol.getInitializedline())) {
									String messagess = "Using variable " + "'" + sembol.getName() + "'"
											+ " may not initialize before using at line: " + expr.getBeginLine();
									ExpressionHelper.messages.add(messagess);
								}
							}

						} else {
							String messagess = "Using variable " + "'" + sembol.getName() + "'"
									+ " before initialization at line: " + expr.getBeginLine();
							ExpressionHelper.messages.add(messagess);
						}
					}

					ScaHelper.setSymbol(st, name, sembol.getParentBlock(), sembol);
				}
				break;

			case "UnaryExpr":
				if (((UnaryExpr) expr).getOperator() == com.github.javaparser.ast.expr.UnaryExpr.Operator.not) {
					SolveExpr(((UnaryExpr) expr).getExpr(), st, isThis);
				}
				SolveExpr(((UnaryExpr) expr).getExpr(), st, isThis);
				SolveAssignExpr(((UnaryExpr) expr).getExpr(), st, isThis);
				break;

			case "ThisExpr":
				if (((ThisExpr) expr).getClassExpr() != null) {
					SolveExpr(((ThisExpr) expr).getClassExpr(), st, true);
				}
				break;

			case "SuperExpr":
				if (((SuperExpr) expr).getClassExpr() != null) {
					SolveExpr(((SuperExpr) expr).getClassExpr(), st, isThis);
				}
				break;

			case "CastExpr":
				SolveExpr(((CastExpr) expr).getExpr(), st, isThis);

				break;
			case "MethodReferenceExpr":
				if (((MethodReferenceExpr) expr).getScope() != null) {
					SolveExpr(((MethodReferenceExpr) expr).getScope(), st, isThis);
				}

				break;
			case "ObjectCreationExpr":
				if (((ObjectCreationExpr) expr).getScope() != null) {
					SolveExpr(((ObjectCreationExpr) expr).getScope(), st, isThis);
				}

				if (((ObjectCreationExpr) expr).getArgs() != null) {
					for (Expression a : ((ObjectCreationExpr) expr).getArgs()) {
						SolveExpr(a, st, isThis);
					}
				}
				break;
			case "ArrayCreationExpr":
				if (((ArrayCreationExpr) expr).getInitializer() != null) {
					SolveExpr(((ArrayCreationExpr) expr).getInitializer(), st, isThis);
				}

				if (((ArrayCreationExpr) expr).getDimensions() != null
						|| ((ArrayCreationExpr) expr).getDimensions().isEmpty()) {
					for (Expression a : ((ArrayCreationExpr) expr).getDimensions()) {
						SolveExpr(a, st, isThis);
					}
				}
				break;
			case "ArrayInitializerExpr":
				if (((ArrayInitializerExpr) expr).getValues() != null) {
					for (Expression a : ((ArrayInitializerExpr) expr).getValues()) {
						SolveExpr(a, st, isThis);
					}
				}
				break;
			case "ConditionalExpr":
				SolveExpr(((ConditionalExpr) expr).getCondition(), st, isThis);
				SolveExpr(((ConditionalExpr) expr).getElseExpr(), st, isThis);
				SolveExpr(((ConditionalExpr) expr).getThenExpr(), st, isThis);
				break;
			default:
				break;
			}
		}
	}

	// check Field access as this
	public static boolean isThisExpr(FieldAccessExpr expr) {
		if (expr.getScope() instanceof ThisExpr) {
			return true;
		} else {
			return false;
		}
	}

	// get block
	public static Node Getparentblock(Node node) {
		Node blocknode;
		blocknode = node;

		if (blocknode != null) {

			switch (blocknode.getClass().getSimpleName()) {
			case "ClassOrInterfaceDeclaration":
			case "EnumDeclaration":
			case "ConstructorDeclaration":
			case "MethodDeclaration":
			case "InitializerDeclaration":
			case "ForStmt":
			case "TryStmt":
			case "ForeachStmt":
			case "WhileStmt":
			case "DoStmt":
			case "SynchronizedStmt":
			case "SwitchEntryStmt":
			case "CatchClause":
				return blocknode;
			case "FieldDeclaration":
				return Getparentblock(blocknode.getParentNode());

			default:
				while (!(blocknode instanceof BlockStmt || blocknode instanceof SwitchEntryStmt
						|| blocknode instanceof FieldDeclaration || blocknode instanceof ForStmt)) {
					blocknode = blocknode.getParentNode();
				}

				if (blocknode.getParentNode() instanceof IfStmt) {
					return blocknode;
				}

				if (blocknode instanceof SwitchEntryStmt) {
					return blocknode;
				}
				if (blocknode instanceof ForStmt) {
					return blocknode;
				}
				return Getparentblock(blocknode.getParentNode());
			}

		}
		return null;
	}

	// if inside if statement initialize set symbol table
	public static void solveIfStmt(BlockStmt thenStmt, String name, Symbol sembol) {
		String thenStr = "";
		boolean initialized = false;
		boolean init = false;
		BlockStmt statement = (BlockStmt) thenStmt.clone();
		List<Statement> lstmt = statement.getStmts();
		Collections.reverse(lstmt);
		if (sembol.isInsideBlock()) {
			if (sembol.getInitialInsideBlock().equals(thenStmt)) {
				init = true;
				setChecked(true);
			}
		}

		if (!init) {

			for (Statement stmt : lstmt) {
				if (stmt instanceof ExpressionStmt) {
					Expression expr = ((ExpressionStmt) stmt).getExpression();
					if (expr instanceof AssignExpr) {
						thenStr = checkAssignExpr(((AssignExpr) expr).getTarget());
						if (thenStr.equals(name)) {
							initialized = true;
							break;
						}
					}
				}
			}

			if (thenStr.equals(name)) {
				if (initialized) {
					setChecked(true);
				}
			}

		}
	}

	// return target of assign expr
	public static String checkAssignExpr(Expression expr) {
		String name;
		if (expr != null) {
			switch (expr.getClass().getSimpleName()) {
			case "FieldAccessExpr":
				if (isThisExpr(((FieldAccessExpr) expr))) {
					if (((FieldAccessExpr) expr).getFieldExpr() != null) {
						((FieldAccessExpr) expr).getFieldExpr().setParentNode((FieldAccessExpr) expr);
						;
						return checkAssignExpr(((FieldAccessExpr) expr).getFieldExpr());
					}
				} 			
			case "NameExpr":
				name = ((NameExpr) expr).getName().toString();
				return name;

			case "ThisExpr":
				if (((ThisExpr) expr).getClassExpr() != null) {
					return checkAssignExpr(((ThisExpr) expr).getClassExpr());
				}

			default:
				return "";

			}

		}
		return "";
	}

	// Check switch entry for initialization
	public static boolean solveSwitchStmt(SwitchStmt switchStmt, String name) {
		boolean frombreak = false;
		SwitchStmt switchStatement = (SwitchStmt) switchStmt.clone();
		List<SwitchEntryStmt> lSwitchEntryStmt = switchStatement.getEntries();
		// Collections.reverse(lSwitchEntryStmt);
		lSwitchEntryStmt.remove(lSwitchEntryStmt.size() - 1);
		String nameexpr = "";
		for (SwitchEntryStmt stmt : lSwitchEntryStmt) {
			frombreak = false;
			for (Statement stmts : stmt.getStmts()) {
				if (stmts instanceof ExpressionStmt) {
					Expression expr = ((ExpressionStmt) stmts).getExpression();
					if (expr instanceof AssignExpr) {
						nameexpr = checkAssignExpr(((AssignExpr) expr).getTarget());
						if (nameexpr.equals(name)) {
							break;
						}
					}
				} else if (stmts instanceof BreakStmt) {
					frombreak = true;
					break;
				}
			}
			if (frombreak) {
				break;
			}
		}
		if (frombreak) {
			return false;
		} else {
			return true;
		}
	}

}
