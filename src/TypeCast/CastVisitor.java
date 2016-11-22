package TypeCast;

import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TreeVisitor;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
 
import DeclarationCatches.MethodDeclarationVisitor;
import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class CastVisitor extends TreeVisitor {

	public Table<String, Node, Symbol> st = HashBasedTable.create();
	private Node compilationUnit = null;
	private HashMap<String, MethodDeclaration> methods = null;

	@Override
	public void process(Node node) {

		if (node instanceof FieldDeclaration) {

			List<VariableDeclarator> varDList;
			varDList = (List<VariableDeclarator>) ((FieldDeclaration) node).getVariables();

			for (VariableDeclarator var : varDList) {
				boolean a = false;
				if (var.getInit() != null) {// Binary sabit deðilse solve yolla
					a = true;
				}
				Node ParentBlock = ScaHelper.getParentBlock(node, false);

				// dizi olup olmadýðýna karar ver
				int varType = Symbol.nonarraytype;
				Type type = null;
				if (((FieldDeclaration) node).getType() instanceof ReferenceType) {

					varType = ((ReferenceType) (((FieldDeclaration) node).getType())).getArrayCount();
					type = ((ReferenceType) (((FieldDeclaration) node).getType())).getType();

				} else {
					type = ((FieldDeclaration) node).getType();
				}

				if (varType == Symbol.nonarraytype) {
					if (var.getId().toString().contains("[")) {
						varType = Symbol.arraytype;
					}
				}

				Symbol symbol = new Symbol(var.getId().getName(), var.getBeginLine(), type, ParentBlock, a);

				if (var.getInit() != null) {
					if (var.getInit() instanceof ObjectCreationExpr) {
						// important
						if (((ObjectCreationExpr) var.getInit()).getType().toString().contains("<")) {
							String s = type.toString();
							String s2 = ((ObjectCreationExpr) var.getInit()).getType().toString();
							if (s.contains("<")) {
								s = s.substring(s.indexOf("<"), s.length());
								s2 = s2.substring(0, s2.indexOf("<"));
								s2 = s2 + s;
							}
							symbol.setRealType(s2);
						} else {
							symbol.setRealType(((ObjectCreationExpr) var.getInit()).getType().toString());
						}
					}
				}
				symbol.setKind("Global Variable");
				symbol.setVartype(varType);
				st.put(var.getId().getName(), ParentBlock, symbol);
			}
		}
		if (node instanceof ExpressionStmt) {
			if (((ExpressionStmt) node).getExpression() instanceof VariableDeclarationExpr) {
				VariableDeclarationExpr varexpr = (VariableDeclarationExpr) ((ExpressionStmt) node).getExpression();
				List<VariableDeclarator> varDList;
				varDList = (List<VariableDeclarator>) varexpr.getVars();
				for (VariableDeclarator var : varDList) {
					boolean a = false;
					if (var.getInit() != null) {
						a = true;
					}

					Node ParentBlock = ScaHelper.getParentBlock(node, false);

					// dizi olup olmadýðýna karar ver
					int varType = Symbol.nonarraytype;
					Type type = null;
					if (varexpr.getType() instanceof ReferenceType) {

						varType = ((ReferenceType) (varexpr.getType())).getArrayCount();
						type = ((ReferenceType) (varexpr.getType())).getType();

					} else {
						type = varexpr.getType();
					}

					if (varType == Symbol.nonarraytype) {
						if (var.getId().toString().contains("[")) {
							varType = Symbol.arraytype;
						}
					}

					Symbol symbol = new Symbol(var.getId().getName(), var.getBeginLine(), type, ParentBlock, a);
					if (a) {
						symbol.setChangedline(var.getBeginLine());
					}
					if (var.getInit() != null) {
						if (var.getInit() instanceof ObjectCreationExpr) {
							// important
							if (((ObjectCreationExpr) var.getInit()).getType().toString().contains("<")) {
								String s = type.toString();
								String s2 = ((ObjectCreationExpr) var.getInit()).getType().toString();
								if (s.contains("<")) {
									s = s.substring(s.indexOf("<"), s.length());
									s2 = s2.substring(0, s2.indexOf("<"));
									s2 = s2 + s;
								}
								symbol.setRealType(s2);
							} else {
								symbol.setRealType(((ObjectCreationExpr) var.getInit()).getType().toString());
							}

						}
					}
					symbol.setVartype(varType);
					symbol.setKind("Local Variable");
					st.put(var.getId().getName(), ParentBlock, symbol);
				}
			} else if (((ExpressionStmt) node).getExpression() instanceof AssignExpr) {
				// set real type of name expression
				AssignExpr asex = (AssignExpr) ((ExpressionStmt) node).getExpression();
				if (asex.getTarget() instanceof NameExpr && asex.getValue() instanceof ObjectCreationExpr) {
					// important
					if (ScaHelper.lookup(st, asex.getTarget().toString(), node, false)) {
						Symbol sembol = ScaHelper.getSymbol(st, asex.getTarget().toString(), node, false);
						if (((ObjectCreationExpr) asex.getValue()).getType().toString().contains("<")) {
							String s = sembol.getType().toString();
							String s2 = ((ObjectCreationExpr) asex.getValue()).getType().toString();
							if (s.contains("<")) {
								s = s.substring(s.indexOf("<"), s.length());
								s2 = s2.substring(0, s2.indexOf("<"));
								s2 = s2 + s;
							}
							sembol.setRealType(s2);
						} else {
							sembol.setRealType(((ObjectCreationExpr) asex.getValue()).getType().toString());
						}
					}
				} else if (asex.getTarget() instanceof NameExpr && asex.getValue() instanceof NameExpr) {

					if (ScaHelper.lookup(st, asex.getTarget().toString(), node, false)) {
						if (ScaHelper.lookup(st, asex.getValue().toString(), node, false)) {
							Symbol sembol1 = ScaHelper.getSymbol(st, asex.getTarget().toString(), node, false);
							Symbol sembol2 = ScaHelper.getSymbol(st, asex.getValue().toString(), node, false);
							if (sembol2.getRealType() != null) {
								sembol1.setRealType(sembol2.getRealType());
							} else {
								sembol1.setRealType(sembol2.getType().toString());
							}

						}

					}
				}
			}
		}

		if (node instanceof MethodDeclaration) {
			List<Parameter> parameterList;
			if (((MethodDeclaration) node).getParameters() != null) {
				parameterList = ((MethodDeclaration) node).getParameters();
				for (Parameter p : parameterList) {

					int varType = Symbol.nonarraytype;
					Type type = null;
					if (p.getType() instanceof ReferenceType) {

						varType = ((ReferenceType) (p.getType())).getArrayCount();
						type = ((ReferenceType) (p.getType())).getType();

					} else {
						type = p.getType();
					}

					if (varType == Symbol.nonarraytype) {
						if (p.getId().toString().contains("[")) {
							varType = Symbol.arraytype;
						}
					}

					Node ParentBlock = ((MethodDeclaration) node);
					Symbol symbol = new Symbol(p.getId().getName(), p.getBeginLine(), type, ParentBlock, true);
					symbol.setKind("Method parameter");
					symbol.setVartype(varType);
					st.put(p.getId().getName(), ParentBlock, symbol);
				}
			}
		}

		if (node instanceof ConstructorDeclaration) {

			List<Parameter> parameterList;
			if (((ConstructorDeclaration) node).getParameters() != null) {
				parameterList = ((ConstructorDeclaration) node).getParameters();

				for (Parameter p : parameterList) {

					int varType = Symbol.nonarraytype;
					Type type = null;
					if (p.getType() instanceof ReferenceType) {

						varType = ((ReferenceType) (p.getType())).getArrayCount();
						type = ((ReferenceType) (p.getType())).getType();

					} else {
						type = p.getType();
					}

					if (varType == Symbol.nonarraytype) {
						if (p.getId().toString().contains("[")) {
							varType = Symbol.arraytype;
						}
					}

					Node ParentBlock = ((ConstructorDeclaration) node);
					Symbol symbol = new Symbol(p.getId().getName(), p.getBeginLine(), type, ParentBlock, true);
					symbol.setKind("Constructor parameter");
					symbol.setVartype(varType);
					st.put(p.getId().getName(), ParentBlock, symbol);
				}
			}
		}

		if (node instanceof CatchClause) {
			Parameter parameter;
			if (((CatchClause) node).getParam() != null) {
				parameter = ((CatchClause) node).getParam();

				int varType = Symbol.nonarraytype;
				Type type = null;
				if (parameter.getType() instanceof ReferenceType) {

					varType = ((ReferenceType) (parameter.getType())).getArrayCount();
					type = ((ReferenceType) (parameter.getType())).getType();

				} else {
					type = parameter.getType();
				}

				if (varType == Symbol.nonarraytype) {
					if (parameter.getId().toString().contains("[")) {
						varType = Symbol.arraytype;
					}
				}

				Node ParentBlock = ((CatchClause) node);
				Symbol symbol = new Symbol(parameter.getId().getName(), parameter.getBeginLine(), type, ParentBlock,
						true);
				symbol.setKind("Catch parameter");
				symbol.setVartype(varType);
				st.put(parameter.getId().getName(), ParentBlock, symbol);
			}
		}

		if (node instanceof ForeachStmt) {
			VariableDeclarationExpr varexpr = (VariableDeclarationExpr) ((ForeachStmt) node).getVariable();
			List<VariableDeclarator> varDList;
			varDList = (List<VariableDeclarator>) varexpr.getVars();
			for (VariableDeclarator var : varDList) {

				Node ParentBlock = ScaHelper.getParentBlock(node, false);

				// dizi olup olmadýðýna karar ver
				int varType = Symbol.nonarraytype;
				Type type = null;
				if (varexpr.getType() instanceof ReferenceType) {

					varType = ((ReferenceType) (varexpr.getType())).getArrayCount();
					type = ((ReferenceType) (varexpr.getType())).getType();

				} else {
					type = varexpr.getType();
				}

				if (varType == Symbol.nonarraytype) {
					if (var.getId().toString().contains("[")) {
						varType = Symbol.arraytype;
					}
				}

				Symbol symbol = new Symbol(var.getId().getName(), var.getBeginLine(), type, ParentBlock, true);
				symbol.setVartype(varType);
				symbol.setKind("Local Variable");
				st.put(var.getId().getName(), ParentBlock, symbol);
			}

		}

		if (node instanceof ForStmt) {
			if (((ForStmt) node).getInit() != null) {
				for (Expression ex : ((ForStmt) node).getInit()) {
					if (ex instanceof VariableDeclarationExpr) {
						VariableDeclarationExpr varexpr = (VariableDeclarationExpr) ex;
						List<VariableDeclarator> varDList;
						varDList = (List<VariableDeclarator>) varexpr.getVars();
						for (VariableDeclarator var : varDList) {
							boolean a = false;
							Node ParentBlock = ScaHelper.getParentBlock(node, false);
							if (var.getInit() != null) {
								a = true;
							}
							// dizi olup olmadýðýna karar ver
							int varType = Symbol.nonarraytype;
							Type type = null;
							if (varexpr.getType() instanceof ReferenceType) {

								varType = ((ReferenceType) (varexpr.getType())).getArrayCount();
								type = ((ReferenceType) (varexpr.getType())).getType();

							} else {
								type = varexpr.getType();
							}

							if (varType == Symbol.nonarraytype) {
								if (var.getId().toString().contains("[")) {
									varType = Symbol.arraytype;
								}
							}

							Symbol symbol = new Symbol(var.getId().getName(), var.getBeginLine(), type, ParentBlock,
									true);
							if (a) {
								symbol.setChangedline(var.getBeginLine());
							}
							symbol.setVartype(varType);
							symbol.setKind("Local Variable");
							st.put(var.getId().getName(), ParentBlock, symbol);
						}
					}
				}
			}

		}

		if (node instanceof TryStmt) {
			if (((TryStmt) node).getResources() != null) {
				for (VariableDeclarationExpr ex : ((TryStmt) node).getResources()) {
					VariableDeclarationExpr varexpr = ex;
					List<VariableDeclarator> varDList;
					varDList = (List<VariableDeclarator>) varexpr.getVars();

					for (VariableDeclarator var : varDList) {

						Node ParentBlock = ScaHelper.getParentBlock(node, false);

						// dizi olup olmadýðýna karar ver
						int varType = Symbol.nonarraytype;
						Type type = null;
						if (varexpr.getType() instanceof ReferenceType) {

							varType = ((ReferenceType) (varexpr.getType())).getArrayCount();
							type = ((ReferenceType) (varexpr.getType())).getType();

						} else {
							type = varexpr.getType();
						}

						if (varType == Symbol.nonarraytype) {
							if (var.getId().toString().contains("[")) {
								varType = Symbol.arraytype;
							}
						}

						Symbol symbol = new Symbol(var.getId().getName(), var.getBeginLine(), type, ParentBlock, true);
						symbol.setVartype(varType);
						symbol.setKind("Local Variable");
						st.put(var.getId().getName(), ParentBlock, symbol);
					}
				}
			}
		}

		// solve cast expression
		if (node instanceof CastExpr) {
			if (((CastExpr) node).getExpr() instanceof NameExpr) {
				if (CastingHelper.GetTree() == null) {
					CastingHelper.creatTypeTree();
				}
				CastingHelper.setContain(false);
				CastHelper.solveCastExpr((CastExpr) node, st);
			} else if (((CastExpr) node).getExpr() instanceof MethodCallExpr) {
				MethodCallExpr metcall = (MethodCallExpr) (((CastExpr) node).getExpr());
				if (metcall.getScope() == null) {
					if (compilationUnit == null) {
						compilationUnit = getComplinationUnit(node);
						methods = getMethods(compilationUnit);
					}

					if (methods.containsKey(metcall.getName())) {
						MethodDeclaration metdec = methods.get(metcall.getName());
						if (metdec.getType() != null && !(metdec.getType() instanceof VoidType)) {
							CastingHelper.setContain(false);
							CastHelper.solveCastExpr1((CastExpr) node, metdec.getType().toString(), st);
						}
					}
				}
			}
		}
	}

	@Override
	public void visitDepthFirst(Node node) {
		super.visitDepthFirst(node);
	}

	private CompilationUnit getComplinationUnit(Node node) {
		if (node instanceof CompilationUnit) {
			return (CompilationUnit) node;
		}
		return getComplinationUnit(node.getParentNode());
	}

	private HashMap<String, MethodDeclaration> getMethods(Node node) {
		HashMap<String, MethodDeclaration> hm = new HashMap<>();
		node.accept(new MethodDeclarationVisitor(), hm);
		return hm;
	}
}
