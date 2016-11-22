package MisInitialization;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TreeVisitor;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import ScaHelper.ExpressionHelper;
import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class Visitor extends TreeVisitor {

	public Table<String, Node, Symbol> st = HashBasedTable.create();

	@Override
	public void process(Node node) {

		if (node instanceof FieldDeclaration) {

			List<VariableDeclarator> varDList;
			varDList = (List<VariableDeclarator>) ((FieldDeclaration) node).getVariables();

			for (VariableDeclarator var : varDList) {
				boolean a = false;
				if (var.getInit() != null) {// Binary sabit deðilse solve yolla
					a = true;
					if (!(var.getInit() instanceof NullLiteralExpr)) {
						ExpressionHelper.SolveExpr(var.getInit(), st, true);
					} // will be change if null variable using
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

				symbol.setKind("Global Variable");
				symbol.setVartype(varType);
				st.put(var.getId().getName(), ParentBlock, symbol);
			}
		}
		if (node instanceof ExpressionStmt) {
			if (((ExpressionStmt) node).getExpression() instanceof AssignExpr) {

				ExpressionHelper.setChecked(false);
				ExpressionHelper.SolveAssignExpr(((ExpressionStmt) node).getExpression(), st, false);
			} else if (!(((ExpressionStmt) node).getExpression() instanceof VariableDeclarationExpr)) {
				ExpressionHelper.setChecked(false);
				ExpressionHelper.SolveExpr(((ExpressionStmt) node).getExpression(), st, false);
			} else if (((ExpressionStmt) node).getExpression() instanceof VariableDeclarationExpr) {

				VariableDeclarationExpr varexpr = (VariableDeclarationExpr) ((ExpressionStmt) node).getExpression();
				List<VariableDeclarator> varDList;
				varDList = (List<VariableDeclarator>) varexpr.getVars();
				for (VariableDeclarator var : varDList) {
					boolean a = false;
					if (var.getInit() != null) {
						a = true;
						if (!(var.getInit() instanceof NullLiteralExpr)) {
							ExpressionHelper.SolveExpr(var.getInit(), st, false);
						} // will be change if null variable using
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

					symbol.setVartype(varType);
					symbol.setKind("Local Variable");
					st.put(var.getId().getName(), ParentBlock, symbol);
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

		if (node instanceof IfStmt) {
			ExpressionHelper.SolveExpr(((IfStmt) node).getCondition(), st, false);
		}

		if (node instanceof AssertStmt) {
			if (((AssertStmt) node).getCheck() != null) {
				ExpressionHelper.SolveExpr(((AssertStmt) node).getCheck(), st, false);
			}
			if (((AssertStmt) node).getMessage() != null) {
				ExpressionHelper.SolveExpr(((AssertStmt) node).getMessage(), st, false);
			}
		}

		if (node instanceof DoStmt) {
			if (((DoStmt) node).getCondition() != null) {
				ExpressionHelper.SolveExpr(((DoStmt) node).getCondition(), st, false);
			}
		}

		if (node instanceof WhileStmt) {
			if (((WhileStmt) node).getCondition() != null) {
				ExpressionHelper.SolveExpr(((WhileStmt) node).getCondition(), st, false);
			}
		}

		if (node instanceof ExplicitConstructorInvocationStmt) {
			if (!(((ExplicitConstructorInvocationStmt) node).isThis())) {
				if (((ExplicitConstructorInvocationStmt) node).getExpr() != null) {
					ExpressionHelper.SolveExpr(((ExplicitConstructorInvocationStmt) node).getExpr(), st, false);

				}
			}

			if (((ExplicitConstructorInvocationStmt) node).getArgs() != null) {
				for (Expression ex : ((ExplicitConstructorInvocationStmt) node).getArgs()) {
					ExpressionHelper.SolveExpr(ex, st, false);
				}
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

			if (((ForeachStmt) node).getIterable() != null) {
				ExpressionHelper.SolveExpr(((ForeachStmt) node).getIterable(), st, false);
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
								if (!(var.getInit() instanceof NullLiteralExpr)) {
									ExpressionHelper.SolveExpr(var.getInit(), st, false);
								} // will be change if null variable using
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
					} else if (ex instanceof AssignExpr) {

						ExpressionHelper.SolveAssignExpr((AssignExpr) ex, st, false);
					} else {
						ExpressionHelper.SolveExpr(ex, st, false);
					}
				}
			}

			if (((ForStmt) node).getCompare() != null) {
				ExpressionHelper.SolveExpr(((ForStmt) node).getCompare(), st, false);
			}

			if (((ForStmt) node).getUpdate() != null) {
				for (Expression ex : ((ForStmt) node).getUpdate()) {
					ExpressionHelper.SolveExpr(ex, st, false);
				}
			}
		}

		if (node instanceof ReturnStmt) {
			if (((ReturnStmt) node).getExpr() != null) {
				ExpressionHelper.SolveExpr(((ReturnStmt) node).getExpr(), st, false);
			}
		}

		if (node instanceof SwitchEntryStmt) {
			if (((SwitchEntryStmt) node).getLabel() != null) {
				ExpressionHelper.SolveExpr(((SwitchEntryStmt) node).getLabel(), st, false);
			}
		}

		if (node instanceof SwitchStmt) {
			if (((SwitchStmt) node).getSelector() != null) {
				ExpressionHelper.SolveExpr(((SwitchStmt) node).getSelector(), st, false);
			}
		}

		if (node instanceof SynchronizedStmt) {
			if (((SynchronizedStmt) node).getExpr() != null) {
				ExpressionHelper.SolveExpr(((SynchronizedStmt) node).getExpr(), st, false);
			}
		}

		if (node instanceof ThrowStmt) {
			if (((ThrowStmt) node).getExpr() != null) {
				ExpressionHelper.SolveExpr(((ThrowStmt) node).getExpr(), st, false);
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
	}

	@Override
	public void visitDepthFirst(Node node) {
		super.visitDepthFirst(node);
	}

}
