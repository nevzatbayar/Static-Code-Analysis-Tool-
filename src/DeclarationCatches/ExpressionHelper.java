package DeclarationCatches;

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
import com.github.javaparser.ast.expr.NullLiteralExpr;
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
import com.github.javaparser.ast.type.PrimitiveType;
import com.google.common.collect.Table;

import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class ExpressionHelper {

	private static ArrayList<String> messages = new ArrayList<>();

	private static boolean checked = false;
	private static boolean checked1 = false;
	private static String exctype = "";
	private static String parentinf = "";
	private static Node parentNode = null;
	private static boolean checkNull = false;

	public static void setExctype(String exctype) {
		ExpressionHelper.exctype = exctype;
	}

	public static void setParentNode(Node parentNode) {
		ExpressionHelper.parentNode = parentNode;
	}

	public static void setParentinf(String parentinf) {
		ExpressionHelper.parentinf = parentinf;
	}

	public static void setChecked(boolean bool) {
		checked = bool;
	}

	public static void setChecked1(boolean bool) {
		checked1 = bool;
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
					// if (!sembol.getInitialize()) {
					if (insideBlock.equals(sembol.getParentBlock())) {
						setiniitalize(sembol, true);
						setinsideBlock(sembol, null, -1, false);
						setiniitalizetoNull(sembol, false);
						setinsidenullBlock(sembol, null, -1, false);
					} else {

						switch (insideBlock.getClass().getSimpleName()) {
						case "ClassOrInterfaceDeclaration":
						case "EnumDeclaration":
						case "ConstructorDeclaration":
						case "MethodDeclaration":
						case "InitializerDeclaration":
						case "TryStmt":
							setiniitalize(sembol, true);
							setinsideBlock(sembol, null, -1, false);
							setiniitalizetoNull(sembol, false);
							setinsidenullBlock(sembol, null, -1, false);
							break;
						case "BlockStmt":

							switch (parentinsideblock.getClass().getSimpleName()) {
							case "ClassOrInterfaceDeclaration":
							case "EnumDeclaration":
							case "ConstructorDeclaration":
							case "MethodDeclaration":
							case "InitializerDeclaration":
							case "TryStmt":
								IfStmt ifstateament = (IfStmt) insideBlock.getParentNode();
								if (ifstateament.getElseStmt() != null) {
									if (ifstateament.getElseStmt().equals(insideBlock)) {

										solveIfStmt((BlockStmt) ifstateament.getThenStmt(), name, sembol);
										if (checked) {
											setiniitalize(sembol, true);
											setinsideBlock(sembol, null, -1, false);
											setiniitalizetoNull(sembol, false);
											setinsidenullBlock(sembol, null, -1, false);
										} else {
											setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
											setinsidenullBlock(sembol, null, -1, false);
										}
									} else {
										setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
										setinsidenullBlock(sembol, null, -1, false);
									}
								} else {
									setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
									setinsidenullBlock(sembol, null, -1, false);
								}
								break;

							default:
								if (!ifinitalizeinsideParent(sembol, insideBlock)) {
									IfStmt ifsteament = (IfStmt) insideBlock.getParentNode();
									if (ifsteament.getElseStmt() != null) {
										if (ifsteament.getElseStmt().equals(insideBlock)) {

											solveIfStmt((BlockStmt) ifsteament.getThenStmt(), name, sembol);
											if (checked) {
												setinsideBlock(sembol, parentinsideblock, expr.getBeginLine(), true);
												setinsidenullBlock(sembol, null, -1, false);
											} else {
												setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
												setinsidenullBlock(sembol, null, -1, false);
											}
										} else {
											setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
											setinsidenullBlock(sembol, null, -1, false);
										}
									} else {
										setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
										setinsidenullBlock(sembol, null, -1, false);
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
							case "TryStmt":
								Expression label = ((SwitchEntryStmt) insideBlock).getLabel();
								if (label == null) {
									if (solveSwitchStmt((SwitchStmt) (((SwitchEntryStmt) insideBlock).getParentNode()),
											name)) {
										setiniitalize(sembol, true);
										setinsideBlock(sembol, null, -1, false);
										setiniitalizetoNull(sembol, false);
										setinsidenullBlock(sembol, null, -1, false);
									} else {
										setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
										setinsidenullBlock(sembol, null, -1, false);
									}
								} else {
									setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
									setinsidenullBlock(sembol, null, -1, false);

								}
								break;
							default:
								if (!ifinitalizeinsideParent(sembol, insideBlock)) {
									Expression label2 = ((SwitchEntryStmt) insideBlock).getLabel();
									if (label2 == null) {
										if (solveSwitchStmt(
												(SwitchStmt) (((SwitchEntryStmt) insideBlock).getParentNode()), name)) {
											setinsideBlock(sembol, parentinsideblock, expr.getBeginLine(), true);
											setinsidenullBlock(sembol, null, -1, false);

										} else {
											setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
											setinsidenullBlock(sembol, null, -1, false);
										}
									} else {
										setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
										setinsidenullBlock(sembol, null, -1, false);
									}
								}
								break;
							}
							break;

						default:
							setinsideBlock(sembol, insideBlock, expr.getBeginLine(), true);
							setinsidenullBlock(sembol, null, -1, false);
							break;
						}
					}

					ScaHelper.setSymbol(st, name, sembol.getParentBlock(), sembol);
				}

				break;
			case "AssignExpr":
				if (((AssignExpr) expr).getValue() instanceof NullLiteralExpr) {
					SolveNullExpr(((AssignExpr) expr).getTarget(), st, isThis);
				} else {
					SolveExpr(((AssignExpr) expr).getValue(), st, isThis);
					if (((AssignExpr) expr).getOperator() != Operator.assign) {
						SolveExpr(((AssignExpr) expr).getTarget(), st, isThis);
					}
					SolveAssignExpr(((AssignExpr) expr).getTarget(), st, isThis);
				}
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

	public static boolean ifinitalizetonullinsideParent(Symbol symbol, Node parentbolock) {

		if (symbol.isInsidenullBlock()) {
			if (symbol.getInitialnullInsideBlock().equals(parentbolock)) {
				return false;
			}
			if (symbol.getInitialnullInsideBlock().contains(parentbolock)) {
				return true;
			}
		}
		return false;
	}

	public static void SolveNullExpr(Expression expr, Table<String, Node, Symbol> st, Boolean isThis) {
		String name;
		if (expr != null) {
			switch (expr.getClass().getSimpleName()) {
			case "ArrayAccessExpr":
				SolveExpr(((ArrayAccessExpr) expr).getIndex(), st, isThis);
				SolveExpr(((ArrayAccessExpr) expr).getName(), st, isThis);
				break;
			case "FieldAccessExpr":
				if (isThisExpr(((FieldAccessExpr) expr))) {
					((FieldAccessExpr) expr).getFieldExpr().setParentNode((FieldAccessExpr) expr);
					;

					SolveAssignExpr(((FieldAccessExpr) expr).getFieldExpr(), st, true);
				} else {
					SolveExpr(((FieldAccessExpr) expr).getScope(), st, isThis);
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
					// if (!sembol.getInitializetoNull()) {
					if (insideBlock.equals(sembol.getParentBlock())) {
						setiniitalize(sembol, false);
						setinsideBlock(sembol, null, -1, false);
						setiniitalizetoNull(sembol, true);
						setinsidenullBlock(sembol, null, -1, false);
					} else {

						switch (insideBlock.getClass().getSimpleName()) {
						case "ClassOrInterfaceDeclaration":
						case "EnumDeclaration":
						case "ConstructorDeclaration":
						case "MethodDeclaration":
						case "InitializerDeclaration":
						case "TryStmt":
							setiniitalize(sembol, false);
							setinsideBlock(sembol, null, -1, false);
							setiniitalizetoNull(sembol, true);
							setinsidenullBlock(sembol, null, -1, false);
							break;
						case "BlockStmt":
							switch (parentinsideblock.getClass().getSimpleName()) {
							case "ClassOrInterfaceDeclaration":
							case "EnumDeclaration":
							case "ConstructorDeclaration":
							case "MethodDeclaration":
							case "InitializerDeclaration":
							case "TryStmt":
								IfStmt ifstateament = (IfStmt) insideBlock.getParentNode();
								if (ifstateament.getElseStmt() != null) {
									if (ifstateament.getElseStmt().equals(insideBlock)) {

										solveIfStmtNull((BlockStmt) ifstateament.getThenStmt(), name, sembol);
										if (checked1) {
											setiniitalize(sembol, false);
											setinsideBlock(sembol, null, -1, false);
											setiniitalizetoNull(sembol, true);
											setinsidenullBlock(sembol, null, -1, false);
										} else {
											setinsideBlock(sembol, null, -1, false);
											setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
										}
									} else {
										setinsideBlock(sembol, null, -1, false);
										setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
									}
								} else {
									setinsideBlock(sembol, null, -1, false);
									setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
								}
								break;
							default:
								if (!ifinitalizetonullinsideParent(sembol, insideBlock)) {
									IfStmt ifstament = (IfStmt) insideBlock.getParentNode();
									if (ifstament.getElseStmt() != null) {
										if (ifstament.getElseStmt().equals(insideBlock)) {

											solveIfStmtNull((BlockStmt) ifstament.getThenStmt(), name, sembol);
											if (checked1) {
												setinsideBlock(sembol, null, -1, false);
												setinsidenullBlock(sembol, parentinsideblock, expr.getBeginLine(),
														true);
											} else {
												setinsideBlock(sembol, null, -1, false);
												setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
											}
										} else {
											setinsideBlock(sembol, null, -1, false);
											setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
										}
									} else {
										setinsideBlock(sembol, null, -1, false);
										setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
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
							case "TryStmt":
								Expression label = ((SwitchEntryStmt) insideBlock).getLabel();
								if (label == null) {
									if (solveNullSwitchStmt(
											(SwitchStmt) (((SwitchEntryStmt) insideBlock).getParentNode()), name)) {
										setiniitalize(sembol, false);
										setinsideBlock(sembol, null, -1, false);
										setiniitalizetoNull(sembol, true);
										setinsidenullBlock(sembol, null, -1, false);
									} else {
										setinsideBlock(sembol, null, -1, false);
										setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
									}
								} else {
									setinsideBlock(sembol, null, -1, false);
									setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);

								}
								break;
							default:
								if (!ifinitalizeinsideParent(sembol, insideBlock)) {
									Expression label2 = ((SwitchEntryStmt) insideBlock).getLabel();
									if (label2 == null) {
										if (solveNullSwitchStmt(
												(SwitchStmt) (((SwitchEntryStmt) insideBlock).getParentNode()), name)) {
											setinsideBlock(sembol, null, -1, false);
											setinsidenullBlock(sembol, parentinsideblock, expr.getBeginLine(), true);

										} else {
											setinsideBlock(sembol, null, -1, false);
											setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
										}
									} else {
										setinsideBlock(sembol, null, -1, false);
										setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
									}
								}
								break;
							}
							break;
						default:
							setinsideBlock(sembol, null, -1, false);
							setinsidenullBlock(sembol, insideBlock, expr.getBeginLine(), true);
							break;
						}
					}

					ScaHelper.setSymbol(st, name, sembol.getParentBlock(), sembol);
				}

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

	public static void SolveExpr(Expression expr, Table<String, Node, Symbol> st, Boolean isThis) {
		String name;
		if (expr != null) {
			switch (expr.getClass().getSimpleName()) {
			case "BinaryExpr":
				SolveExpr(((BinaryExpr) expr).getLeft(), st, isThis);
				SolveExpr(((BinaryExpr) expr).getRight(), st, isThis);
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
				checkNull = false;
				solveIfStatement(((NameExpr) expr).getParentNode(), (NameExpr) expr);
				if (!checkNull) {
					name = ((NameExpr) expr).getName().toString();
					if (ScaHelper.lookup(st, name, ScaHelper.getParentBlock(expr, isThis), isThis)) {
						Symbol sembol = ScaHelper.getSymbol(st, name, ScaHelper.getParentBlock(expr, isThis), isThis);
						sembol.setReferencedline(expr.getBeginLine());
						if (!(sembol.getType() instanceof PrimitiveType)) {
							Node insideBlock = Getparentblock(expr);
							if (!(sembol.getInitialize() || sembol.getInitializetoNull())) {
								if (sembol.isInsideBlock()) {
									if (!sembol.getInitialInsideBlock().equals(insideBlock)) {
										if (!(sembol.getInitialInsideBlock().contains(insideBlock)
												&& expr.getBeginLine() > sembol.getInitializedline())) {
											String messagess = parentinf + " instead of " + exctype
													+ ", Null Pointer Access may occurs when using variable " + "'"
													+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
											DeclarationHelper.getMessages().add(messagess);
										}
									}
								} else if (sembol.isInsidenullBlock()) {
									if (sembol.getInitialnullInsideBlock().equals(insideBlock)) {
										if ((sembol.getInitialnullInsideBlock().contains(insideBlock)
												&& expr.getBeginLine() > sembol.getInitializedtonullline())) {
											String messagess = parentinf + " instead of " + exctype
													+ ", Null Pointer Access will occurs when using variable " + "'"
													+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
											DeclarationHelper.getMessages().add(messagess);
										}
									} else {
										String messagess = parentinf + " instead of " + exctype
												+ ", Null Pointer Access will occurs when using variable " + "'"
												+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
										DeclarationHelper.getMessages().add(messagess);
									}
								} else {
									String messagess = parentinf + " instead of " + exctype
											+ ", Null Pointer Access will occurs when using variable " + "'"
											+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
									DeclarationHelper.getMessages().add(messagess);
								}
							} else if (sembol.getInitializetoNull() && !sembol.getInitialize()) {
								if (sembol.isInsideBlock()) {
									if (!sembol.getInitialInsideBlock().equals(insideBlock)) {
										if (!(sembol.getInitialInsideBlock().contains(insideBlock)
												&& expr.getBeginLine() > sembol.getInitializedline())) {
											String messagess = parentinf + " instead of " + exctype
													+ ", Null Pointer Access will occurs when using variable " + "'"
													+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
											DeclarationHelper.getMessages().add(messagess);
										}
									}
								} else {
									String messagess = parentinf + " instead of " + exctype
											+ ", Null Pointer Access will occurs when using variable " + "'"
											+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
									DeclarationHelper.getMessages().add(messagess);
								}
							} else if (!sembol.getInitializetoNull() && sembol.getInitialize()) {
								if (sembol.isInsidenullBlock()) {
									if (sembol.getInitialnullInsideBlock().equals(insideBlock)) {
										if ((sembol.getInitialnullInsideBlock().contains(insideBlock)
												&& expr.getBeginLine() > sembol.getInitializedtonullline())) {
											String messagess = parentinf + " instead of " + exctype
													+ ", Null Pointer Access will occurs when using variable " + "'"
													+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
											DeclarationHelper.getMessages().add(messagess);
										} else {// May do not visit here
											String messagess = parentinf + " instead of " + exctype
													+ ", Null Pointer Access will occurs when using variable " + "'"
													+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
											DeclarationHelper.getMessages().add(messagess);
										}
									} else {
										String messagess = parentinf + " instead of " + exctype
												+ ", Null Pointer Access may occurs when using variable " + "'"
												+ sembol.getName() + "'" + " at line: " + expr.getBeginLine();
										DeclarationHelper.getMessages().add(messagess);
									}
								}
							}
						}
						ScaHelper.setSymbol(st, name, sembol.getParentBlock(), sembol);
					}
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
							if (((AssignExpr) expr).getValue() instanceof NullLiteralExpr) {
								break;
							} else {
								initialized = true;
								break;
							}
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

	// if inside if statement initialize to null set symbol table
	public static void solveIfStmtNull(BlockStmt thenStmt, String name, Symbol sembol) {
		String thenStr = "";
		boolean initializedtoNull = false;
		boolean init = false;
		BlockStmt statement = (BlockStmt) thenStmt.clone();
		List<Statement> lstmt = statement.getStmts();
		Collections.reverse(lstmt);
		if (sembol.isInsidenullBlock()) {
			if (sembol.getInitialnullInsideBlock().equals(thenStmt)) {
				init = true;
			}
		}

		if (!init) {
			for (Statement stmt : lstmt) {
				if (stmt instanceof ExpressionStmt) {
					Expression expr = ((ExpressionStmt) stmt).getExpression();
					if (expr instanceof AssignExpr) {

						thenStr = checkAssignExpr(((AssignExpr) expr).getTarget());

						if (((AssignExpr) expr).getValue() instanceof NullLiteralExpr) {
							if (thenStr.equals(name)) {
								initializedtoNull = true;
								break;
							}
						} else if (!(((AssignExpr) expr).getValue() instanceof NullLiteralExpr)) {
							if (thenStr.equals(name)) {
								break;
							}
						}

					}
				}
			}

			if (thenStr.equals(name)) {
				if (initializedtoNull) {
					setChecked1(true);
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
		boolean initialize = false;

		SwitchStmt switchStatement = (SwitchStmt) switchStmt.clone();
		List<SwitchEntryStmt> lSwitchEntryStmt = switchStatement.getEntries();
		// Collections.reverse(lSwitchEntryStmt);
		lSwitchEntryStmt.remove(lSwitchEntryStmt.size() - 1);
		String nameexpr = "";
		for (SwitchEntryStmt stmt : lSwitchEntryStmt) {
			initialize = false;
			if (!(stmt.getStmts().get(stmt.getStmts().size() - 1) instanceof BreakStmt)) {
				continue;
			}
			SwitchEntryStmt switcstmt = (SwitchEntryStmt) stmt.clone();
			Collections.reverse(switcstmt.getStmts());
			for (Statement stmts : switcstmt.getStmts()) {

				if (stmts instanceof ExpressionStmt) {
					Expression expr = ((ExpressionStmt) stmts).getExpression();
					if (expr instanceof AssignExpr) {
						nameexpr = checkAssignExpr(((AssignExpr) expr).getTarget());
						if (!(((AssignExpr) expr).getValue() instanceof NullLiteralExpr)) {
							if (nameexpr.equals(name)) {
								initialize = true;
								break;
							}
						} else {
							if (nameexpr.equals(name)) {
								break;
							}
						}
					}
				}
			}

			if (!initialize) {
				break;
			}
		}

		if (!initialize) {
			return false;
		} else {
			return true;
		}
	}

	// Check switch entry for initialization to null
	public static boolean solveNullSwitchStmt(SwitchStmt switchStmt, String name) {
		boolean initialize = false;

		SwitchStmt switchStatement = (SwitchStmt) switchStmt.clone();
		List<SwitchEntryStmt> lSwitchEntryStmt = switchStatement.getEntries();
		// Collections.reverse(lSwitchEntryStmt);
		lSwitchEntryStmt.remove(lSwitchEntryStmt.size() - 1);
		String nameexpr = "";
		for (SwitchEntryStmt stmt : lSwitchEntryStmt) {
			initialize = false;
			if (!(stmt.getStmts().get(stmt.getStmts().size() - 1) instanceof BreakStmt)) {
				continue;
			}
			SwitchEntryStmt switcstmt = (SwitchEntryStmt) stmt.clone();
			Collections.reverse(switcstmt.getStmts());
			for (Statement stmts : switcstmt.getStmts()) {

				if (stmts instanceof ExpressionStmt) {
					Expression expr = ((ExpressionStmt) stmts).getExpression();
					if (expr instanceof AssignExpr) {
						nameexpr = checkAssignExpr(((AssignExpr) expr).getTarget());
						if (!(((AssignExpr) expr).getValue() instanceof NullLiteralExpr)) {
							if (nameexpr.equals(name)) {
								break;
							}
						} else {
							if (nameexpr.equals(name)) {
								initialize = true;
								break;
							}
						}
					}
				}
			}

			if (!initialize) {
				break;
			}
		}

		if (!initialize) {
			return false;
		} else {
			return true;
		}
	}

	public static void setiniitalize(Symbol sembol, boolean b) {
		sembol.setInitialize(b);
	}

	public static void setinsideBlock(Symbol sembol, Node node, int line, boolean b) {
		sembol.setInitialInsideBlock(node);
		sembol.setInitializedline(line);
		sembol.setInsideBlock(b);
	}

	public static void setiniitalizetoNull(Symbol sembol, boolean b) {
		sembol.setInitializetoNull(b);
	}

	public static void setinsidenullBlock(Symbol sembol, Node node, int line, boolean b) {
		sembol.setInsidenullBlock(b);
		sembol.setInitializedtonullline(line);
		sembol.setInitialnullInsideBlock(node);
	}

	// Check parent if of divide
	private static void solveIfStatement(Node node, Expression expr) {
		if (node != parentNode) {
			if (!checkNull) {
				if (node instanceof IfStmt) {
					checkIfConditon(((IfStmt) node).getCondition(), expr);
				}
				solveIfStatement(node.getParentNode(), expr);
			}
		}

	}

	private static void checkIfConditon(Expression conditon, Expression expr) {
		if (conditon != null) {
			switch (conditon.getClass().getSimpleName()) {
			case "BinaryExpr":
				BinaryExpr binaryexpr = (BinaryExpr) conditon;
				switch (binaryexpr.getOperator()) {
				case notEquals:
					if (binaryexpr.getRight() instanceof NullLiteralExpr
							&& binaryexpr.getLeft().toString().equals(expr.toString())) {
						checkNull = true;
					} else if (binaryexpr.getLeft() instanceof NullLiteralExpr
							&& binaryexpr.getRight().toString().equals(expr.toString())) {
						checkNull = true;
					}
					break;
				case and:
					checkIfConditon(((BinaryExpr) conditon).getRight(), expr);
					checkIfConditon(((BinaryExpr) conditon).getLeft(), expr);
					break;

				default:
					break;
				}
				break;
			case "EnclosedExpr":
				checkIfConditon(((EnclosedExpr) conditon).getInner(), expr);
				break;
			default:
				break;
			}
		}
	}

}
