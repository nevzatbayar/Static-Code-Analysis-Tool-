package Recursion;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.Type;

public class Helper1 {
	public void getHelper(Node node, String name, Type type, boolean b) {
		boolean bol = true;
		boolean bol1 = true;
		boolean bol2 = true;
		boolean bol3 = true;
		int cnt = 0;
		int cnt6 = 0;
		for (Node bnr : node.getChildrenNodes()) {
			if (bnr instanceof BinaryExpr) {
				if (((BinaryExpr) bnr).getOperator().toString().equals("equals")) {
					String s = "This method" + " (" + name + ") "
							+ "condition is 'equals' so uncontrolled recursion at line: " + node.getBeginLine();
					SolveRecursion.getMessages().add(s);
				}
				if (b) {
					for (Node stmt : ((IfStmt) node).getThenStmt().getChildrenNodes()) {
						for (Node st : stmt.getChildrenNodes()) {
							if ((st instanceof AssignExpr)) {
								if (((BinaryExpr) bnr).getLeft().equals(((AssignExpr) st).getTarget())
										|| ((BinaryExpr) bnr).getRight().equals((((AssignExpr) st).getTarget()))) {
									bol2 = false;
								}
							} else if ((st instanceof UnaryExpr)) {
								for (Node s : st.getChildrenNodes()) {
									if (((BinaryExpr) bnr).getLeft().toString().equals(((NameExpr) s).getName())
											|| ((BinaryExpr) bnr).getRight().toString()
													.equals((((NameExpr) s).getName()))) {
										bol2 = false;
									}
								}
							}
						}
					}
					if (cnt == 0) {
						cnt++;
						if (bol2) {
							String s = "This method" + " (" + name + ") "
									+ "unchecked the conditions so Uncontrolled Recursion at line: "
									+ node.getBeginLine();
							SolveRecursion.getMessages().add(s);
						}
					}
				} else {
					for (Node vo : node.getChildrenNodes()) {
						for (Node bl : vo.getChildrenNodes()) {
							for (Node n : bl.getChildrenNodes()) {
								if ((n instanceof MethodCallExpr)) {
									for (Node m : n.getChildrenNodes()) {
										if ((m instanceof BinaryExpr)) {
											for (Node a : m.getChildrenNodes()) {
												if ((a instanceof NameExpr)) {
													bol3 = false;
													if (((BinaryExpr) bnr).getLeft().toString()
															.equals(((NameExpr) a).getName())
															|| ((BinaryExpr) bnr).getRight().toString()
																	.equals(((NameExpr) a).getName())) {
														bol = false;
													}
													if (bol) {
														String s = "This method" + " (" + name + ") "
																+ "unchecked the conditions so Uncontrolled Recursion at line: "
																+ node.getBeginLine();
														SolveRecursion.getMessages().add(s);
													}
												}
											}
										}
									}
									if (bol3) {
										for (Node m : n.getChildrenNodes()) {
											if ((m instanceof NameExpr)) {
												if (((BinaryExpr) bnr).getLeft().toString()
														.equals(((NameExpr) m).getName())
														|| ((BinaryExpr) bnr).getRight().toString()
																.equals(((NameExpr) m).getName())) {
													bol1 = false;
												}
												if (!bol1) {
													for (Node stmt : ((IfStmt) node).getThenStmt().getChildrenNodes()) {
														for (Node st : stmt.getChildrenNodes()) {
															if ((st instanceof AssignExpr)) {
																if (((BinaryExpr) bnr).getLeft()
																		.equals(((AssignExpr) st).getTarget())
																		|| ((BinaryExpr) bnr).getRight().equals(
																				(((AssignExpr) st).getTarget()))) {
																	bol2 = false;
																}
															} else if ((st instanceof UnaryExpr)) {
																for (Node s : st.getChildrenNodes()) {
																	if (((BinaryExpr) bnr).getLeft().toString()
																			.equals(((NameExpr) s).getName())
																			|| ((BinaryExpr) bnr).getRight().toString()
																					.equals((((NameExpr) s)
																							.getName()))) {
																		bol2 = false;
																	}
																}
															}
														}
													}
													if (cnt == 0) {
														cnt++;
														if (bol2) {
															String s = "This method" + " (" + name + ") "
																	+ "unchecked the conditions so Uncontrolled Recursion at line: "
																	+ node.getBeginLine();
															SolveRecursion.getMessages().add(s);
														}
													}
												} else {
													if (cnt6 == 0) {
														cnt6++;
														String s = "This method" + " (" + name + ") "
																+ "unchecked the conditions so Uncontrolled Recursion at line: "
																+ node.getBeginLine();
														SolveRecursion.getMessages().add(s);
													}
												}
											}
										}
									}
								}
							}
						}
					}

				}

			}
		}
	}

}
