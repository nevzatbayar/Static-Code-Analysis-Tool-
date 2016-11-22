package ScaHelper;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.google.common.collect.Table;

import SymbolTable.Symbol;

public class ScaHelper {
	public static Node getParentBlock(Node node, Boolean isThis) {
		Node blocknode;
		blocknode = node;
		if (blocknode != null) {
			if (isThis) {
				while (!blocknode.getClass().getSimpleName().equals("ClassOrInterfaceDeclaration")) {
					blocknode = blocknode.getParentNode();
				}

				if (blocknode.getClass().getSimpleName().equals("ClassOrInterfaceDeclaration")) {
					return blocknode;
				}

			} else {
				switch (blocknode.getClass().getSimpleName()) {
				case "CompilationUnit":
					return null;
				case "FieldDeclaration":
					return blocknode.getParentNode();
				case "EnumDeclaration":
				case "ClassOrInterfaceDeclaration":
				case "ConstructorDeclaration":
				case "MethodDeclaration":// will be change
				case "InitializerDeclaration":// if contains binary expression
												// will be change
				case "IfStmt":
				case "ForStmt":
				case "ForeachStmt":
				case "WhileStmt":
				case "DoStmt":
				case "SwitchStmt":
				case "TryStmt":
				case "SynchronizedStmt":
				case "CatchClause":
				case "SwitchEntryStmt":
					return blocknode;

				case "BlockStmt":
					if (blocknode.getParentNode() instanceof IfStmt) {
						return blocknode;
					}
					return getParentBlock(blocknode.getParentNode(), isThis);
				default:
					while (!(blocknode instanceof BlockStmt || blocknode instanceof SwitchEntryStmt
							|| blocknode instanceof FieldDeclaration || blocknode instanceof ForStmt)) {
						blocknode = blocknode.getParentNode();
					}

					if (blocknode.getParentNode() instanceof IfStmt) {
						return blocknode;
					}

					if (blocknode instanceof ForStmt) {
						return blocknode;
					}

					if (blocknode instanceof SwitchEntryStmt) {
						return blocknode;
					}

					return getParentBlock(blocknode.getParentNode(), isThis);
				}
			}
		}
		return null;
	}

	// public static Node getParentBlock(Node node, Boolean isThis) {
	// Node blocknode;
	// blocknode = node;

	// return null;
	// }

	public static boolean lookup(Table<String, Node, Symbol> st, String str, Node node, Boolean isThis) {
		if (node != null && (st.containsRow(str))) {
			if (st.contains(str, node)) {
				return true;
			} else {
				return lookup(st, str, getParentBlock(node.getParentNode(), isThis), isThis);
			}
		} else {
			return false;
		}

	}

	public static Symbol getSymbol(Table<String, Node, Symbol> st, String str, Node node, Boolean isThis) {

		if (st.contains(str, node)) {
			return st.get(str, node);
		} else {
			return getSymbol(st, str, getParentBlock(node.getParentNode(), isThis), isThis);
		}

	}

	public static void insert(Table<String, Node, Symbol> st, String str, Node node, Symbol symbol) {

		st.put(str, node, symbol);

	}

	public static void setSymbol(Table<String, Node, Symbol> st, String str, Node node, Symbol symbol) {
		st.put(str, node, symbol);
	}
}
