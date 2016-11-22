package Recursion;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TreeVisitor;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;

public class RecursionVisitor extends TreeVisitor {
	boolean x = true;

	@Override
	public void process(Node node) {
		if (node instanceof MethodDeclaration) {
			String name;
			name = ((MethodDeclaration) node).getName();
			Type type;
			type = ((MethodDeclaration) node).getType();

			boolean b = ((MethodDeclaration) node).getParameters().isEmpty();

			for (Node child : node.getChildrenNodes()) {
				if ((child instanceof BlockStmt)) {
					if (type.toString().equals("void")) {
						for (Node If : child.getChildrenNodes()) {
							if ((If instanceof IfStmt)) {
								x = true;
								boolean cont = true;
								for (Node blk : If.getChildrenNodes()) {
									if ((blk instanceof BlockStmt)) {
										for (Node ex : blk.getChildrenNodes()) {
											if ((ex instanceof ReturnStmt)) {
												x = false;
											}
											for (Node mtd : ex.getChildrenNodes()) {
												if ((mtd instanceof MethodCallExpr)) {
													if (cont) {
														cont = false;
														SolveRecursion.solveUncheckedRecursion(blk.getParentNode(),
																(MethodDeclaration) node, name, type, b, mtd);
													}
												}

											}
										}
									}
								}
							} else {
								if (x) {
									for (Node Ifsiz : If.getChildrenNodes()) {
										if (Ifsiz instanceof MethodCallExpr) {
											if (((MethodCallExpr) Ifsiz).getName().toString().equals(name)) {
												String s = "This method" + " (" + name + ") "
														+ "(not used IfStmt) so Uncontrolled recursion at line: "
														+ Ifsiz.getBeginLine();
												SolveRecursion.getMessages().add(s);
											}
										}
									}
								}
							}
						}
					} else {
						ReturnClass help = new ReturnClass();
						help.getReturn(node, child, name, type, b);
					}

				}
			}
		}
	}

	@Override
	public void visitDepthFirst(Node node) {
		super.visitDepthFirst(node);
	}

}
