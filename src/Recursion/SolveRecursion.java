package Recursion;

import java.util.ArrayList;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;

public class SolveRecursion {
	// Messages of bugs
	private static ArrayList<String> messages = new ArrayList<>();
	// Check if controlled
	private static boolean chk1 = true;
	private static boolean cnt = true;
	private static boolean bol = true;

	// Get messages bugs
	public static ArrayList<String> getMessages() {
		return messages;
	}

	// check is contain if
	public static void solveUncheckedRecursion(Node node, MethodDeclaration node1, String name, Type type, boolean b,
			Node mtd) {

		if (node != null) {
			switch (node.getClass().getSimpleName()) {
			// if is not contain or check set messages
			case "IfStmt":
				chk1 = true;
				bol = true;
				cnt = true;
				if (type.toString().equals("void")) {
					if (((IfStmt) node).getElseStmt() != null) {// Else is
						for (Node els : ((IfStmt) node).getElseStmt().getChildrenNodes()) {
							for (Node blk : els.getChildrenNodes()) {
								if ((blk instanceof MethodCallExpr)) {
									if (((MethodCallExpr) blk).getName().toString().equals(name)) {
										for (Node el : ((IfStmt) node).getThenStmt().getChildrenNodes()) {
											for (Node bl : el.getChildrenNodes()) {
												if ((bl instanceof MethodCallExpr)) {
													if (((MethodCallExpr) bl).getName().toString().equals(name)) {
														if (cnt) {
															cnt = false;
															String s = "This method" + " (" + name + ") "
																	+ "called both else and then blocks so Uncontrolled recursion at line: "
																	+ node.getBeginLine();
															messages.add(s);

														}
													}
												}

											}
										}
									}
								} else if (chk1) {
									Helper1 help = new Helper1();
									help.getHelper(node, name, type, b);
									chk1 = false;
								}
							}
						}
					} else {
						if (((MethodCallExpr) mtd).getName().toString().equals(name)) {
							Helper1 help = new Helper1();
							help.getHelper(node, name, type, b);
						}
					}
				} else {/// void olmayan metodlar
					if (((IfStmt) node).getElseStmt() != null) {
						for (Node then : ((IfStmt) node).getElseStmt().getChildrenNodes()) {
							if (then instanceof ReturnStmt) {
								for (Node blk : then.getChildrenNodes()) {
									if ((blk instanceof MethodCallExpr)) {
										if (((MethodCallExpr) blk).getName().toString().equals(name)) {
											for (Node el : ((IfStmt) node).getThenStmt().getChildrenNodes()) {
												if (el instanceof ReturnStmt) {

													for (Node bl : el.getChildrenNodes()) {

														if ((bl instanceof MethodCallExpr)) {

															if (((MethodCallExpr) bl).getName().toString()
																	.equals(name)) {
																if (cnt) {
																	cnt = false;
																	String s = "This method" + " (" + name + ") "
																			+ "called both else and then blocks so Uncontrolled Recursion at line: "
																			+ node.getBeginLine();
																	messages.add(s);
																}
															}
														}
													}
												} else if (chk1) {
													Helper1 help = new Helper1();
													help.getHelper(node, name, type, b);
													chk1 = false;
												}

											}

										}
									}
								}
							}
						}

					} else {
						for (Node st : node.getParentNode().getChildrenNodes()) {
							if (st instanceof ReturnStmt) {
								bol = false;
								for (Node s : st.getChildrenNodes()) {
									if (s instanceof MethodCallExpr) {
										if (((MethodCallExpr) s).getName().toString().equals(name)) {
											String s1 = "This method" + " (" + name + ") "
													+ "used both inside if and outside if so Uncontrolled Recursion at line: "
													+ node.getBeginLine();
											messages.add(s1);
										}
									}
								}
							}
						}
						if (bol) {
							String s = "This method" + " (" + name + ") "
									+ "not used Return Stmt so Uncontrolled at line: " + node.getBeginLine();
							messages.add(s);
						}
						Helper1 help = new Helper1();
						help.getHelper(node, name, type, b);
					}
				}
				break;

			default:

				break;

			}
		}
	}
}
