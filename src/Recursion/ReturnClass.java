package Recursion;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;

public class ReturnClass {
	public void getReturn(Node node, Node child, String name, Type type,boolean a) {
		boolean b = true;
		for (Node If : child.getChildrenNodes()) {
			if ((If instanceof IfStmt)) {
				boolean cont = true;
				for (Node blk : If.getChildrenNodes()) {
					if ((blk instanceof BlockStmt)) {
						for (Node ex : blk.getChildrenNodes()) {

							if ((ex instanceof ReturnStmt)) {
								b = false;
								for (Node mtd : ex.getChildrenNodes()) {
									if ((mtd instanceof MethodCallExpr)) {
									if (cont) {
										cont = false;
											SolveRecursion.solveUncheckedRecursion(blk.getParentNode(),
													(MethodDeclaration) node, name, type,a,mtd);
									}
									} else {
										for (Node l : If.getChildrenNodes()) {
											if (l instanceof BinaryExpr) {
												for (Node m : l.getChildrenNodes()) {
													if (mtd instanceof FieldAccessExpr) {
														if (m instanceof NameExpr) {
															for (Node I : child.getChildrenNodes()) {
																if (I instanceof ReturnStmt) {
																	for (Node mt : I.getChildrenNodes()) {
																		if ((mt instanceof MethodCallExpr)) {
																			Helper help = new Helper();
																			help.getHelp(node, mt, l, name);
																		}

																	}
																}
															}
														}
													} else if (m instanceof NameExpr) {
														for (Node I : child.getChildrenNodes()) {
															if (I instanceof ReturnStmt) {
																for (Node mt : I.getChildrenNodes()) {
																	if ((mt instanceof MethodCallExpr)) {
																		Helper help = new Helper();
																		help.getHelp(node, mt, l, name);
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
			} else if (b) {// If yoksa direk hatalýdýr.
				if (If instanceof ReturnStmt) {
					for (Node mtd : If.getChildrenNodes()) {
						if ((mtd instanceof MethodCallExpr)) {
							String s = "This method" + " (" + name + ") "
									+ "(not used IfStmt) so Uncontrolled recursion at line: " + If.getBeginLine();
							SolveRecursion.getMessages().add(s);

						}
					}
				}
			}
		}
	}
}
