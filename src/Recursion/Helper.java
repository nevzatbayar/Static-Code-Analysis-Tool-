package Recursion;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;

public class Helper {
	public void getHelp(Node node, Node mt, Node l, String name) {
		boolean bol2 = true;
		boolean bol3 = true;
		int cnt6 = 0;
		int cnt7 = 0;
		if (l instanceof BinaryExpr) {
			if (((BinaryExpr) l).getOperator().toString().equals("equals")) {
				String s = "This method" + " (" + name + ") "
						+ "condition is 'equals' so uncontrolled recursion at line: " + node.getBeginLine();
				SolveRecursion.getMessages().add(s);
			}
		}
		for (Node x : mt.getChildrenNodes()) {
			if ((x instanceof BinaryExpr)) {
				for (Node a : x.getChildrenNodes()) {
					if ((a instanceof NameExpr)) {
						if (((BinaryExpr) l).getLeft().toString().equals(((NameExpr) a).getName())
								|| ((BinaryExpr) l).getRight().toString().equals(((NameExpr) a).getName())) {
							bol2 = false;
						}
						if (bol2) {
							String s = "This method" + " (" + name + ") "
									+ "unchecked the conditions so Uncontrolled Recursion at line: "
									+ node.getBeginLine();
							SolveRecursion.getMessages().add(s);
						}
					}
				}
			} else if ((x instanceof NameExpr)) {
				if (((BinaryExpr) l).getLeft().toString().equals(((NameExpr) x).getName())
						|| ((BinaryExpr) l).getRight().toString().equals(((NameExpr) x).getName())) {
					bol3 = false;
				}
				if (!bol3) {
					for (Node st : l.getParentNode().getParentNode().getChildrenNodes()) {
						for (Node ch : st.getChildrenNodes()) {
							if ((ch instanceof AssignExpr)) {
								if (((BinaryExpr) l).getLeft().equals(((AssignExpr) ch).getTarget())
										|| ((BinaryExpr) l).getRight().equals((((AssignExpr) ch).getTarget()))) {
									cnt6++;
								}
							} else if ((ch instanceof UnaryExpr)) {
								for (Node s : ch.getChildrenNodes()) {
									if (((BinaryExpr) l).getLeft().toString().equals(((NameExpr) s).getName())
											|| ((BinaryExpr) l).getRight().toString()
													.equals((((NameExpr) s).getName()))) {
										cnt6++;
									}
								}
							}
						}
					}
					if (cnt6 == 0 && cnt7 == 0) {
						cnt7++;
						String s = "This method" + " (" + name + ") "
								+ "unchecked the conditions so Uncontrolled Recursion at line: " + node.getBeginLine();
						SolveRecursion.getMessages().add(s);
					}
				} else {
					String s = "This method" + " (" + name + ") "
							+ "unchecked the conditions so Uncontrolled Recursion at line: " + node.getBeginLine();
					SolveRecursion.getMessages().add(s);
				}
			}

		}
	}
}
