package DeclarationCatches;

import java.util.ArrayList;
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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import ScaHelper.ExpressionHelper;
import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class DeclarationCatchesVisitor extends TreeVisitor {

	public Table<String, Node, Symbol> st = HashBasedTable.create();

	@Override
	public void process(Node node) {
		if (node instanceof FieldDeclaration) {

			List<VariableDeclarator> varDList;
			varDList = (List<VariableDeclarator>) ((FieldDeclaration) node).getVariables();

			for (VariableDeclarator var : varDList) {
				boolean b = false;
				boolean a = false;
				if (var.getInit() != null) {// Binary sabit deðilse solve yolla
					
					if (!(var.getInit() instanceof NullLiteralExpr)) {
						a = true;
						ExpressionHelper.SolveExpr(var.getInit(), st, true);
					} else {
						b = true;
					}
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
				
				if (a) {
					symbol.setChangedline(var.getBeginLine());

				}
				
				if (b) {
					symbol.setInitializetoNull(true);
				}
				symbol.setKind("Global Variable");
				symbol.setVartype(varType);
				st.put(var.getId().getName(), ParentBlock, symbol);
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
					symbol.setKind("Constructur parameter");
					symbol.setVartype(varType);
					st.put(p.getId().getName(), ParentBlock, symbol);
				}
			}

			// get throws pass block to declaration helper
			if (((ConstructorDeclaration) node).getThrows() != null) {

				ArrayList<String> throwes = new ArrayList<>();
				for (NameExpr catchces : ((ConstructorDeclaration) node).getThrows()) {
					throwes.add(catchces.getName());
				}

				DeclarationHelper.nullPointer = false;
				DeclarationHelper dec = new DeclarationHelper(throwes, ((ConstructorDeclaration) node).getBlock(), st);
				dec.checkForGenericException(dec.getParentnode());
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

			// get throws pass body to declaration helper
			if (((MethodDeclaration) node).getThrows() != null) {
				ArrayList<String> throwes = new ArrayList<>();
				for (ReferenceType catchces : ((MethodDeclaration) node).getThrows()) {
					throwes.add(((ClassOrInterfaceType) catchces.getType()).getName());
				}
				DeclarationHelper.nullPointer = false;
				DeclarationHelper dec = new DeclarationHelper(throwes, ((MethodDeclaration) node).getBody(), st);
				dec.checkForGenericException(dec.getParentnode());
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
			ArrayList<String> catchnames = new ArrayList<>();
			for (CatchClause catchces : ((TryStmt) node).getCatchs()) {
				ReferenceType type = (ReferenceType) catchces.getParam().getType();
				catchnames.add(((ClassOrInterfaceType) type.getType()).getName());
			}

			// get catches pass try block to declaration helper
			DeclarationHelper.nullPointer = false;
			DeclarationHelper dec = new DeclarationHelper(catchnames, ((TryStmt) node).getTryBlock(), st);
			dec.checkForGenericException(dec.getParentnode());

		}

		if (node instanceof ExpressionStmt) {
			if (((ExpressionStmt) node).getExpression() instanceof AssignExpr) {

				ExpressionHelper.setChecked(false);
				ExpressionHelper.SolveAssignExpr(((ExpressionStmt) node).getExpression(), st, false);
			} else if (((ExpressionStmt) node).getExpression() instanceof VariableDeclarationExpr) {

				VariableDeclarationExpr varexpr = (VariableDeclarationExpr) ((ExpressionStmt) node).getExpression();
				List<VariableDeclarator> varDList;
				varDList = (List<VariableDeclarator>) varexpr.getVars();
				for (VariableDeclarator var : varDList) {
					boolean a = false;
					boolean b = false;
					if (var.getInit() != null) {
						
						if (!(var.getInit() instanceof NullLiteralExpr)) {
							a = true;
							ExpressionHelper.SolveExpr(var.getInit(), st, false);
						} else {
							b = true;
						}
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

					if (b) {
						symbol.setInitializetoNull(true);
					}

					symbol.setVartype(varType);
					symbol.setKind("Local Variable");
					st.put(var.getId().getName(), ParentBlock, symbol);
				}
			}
		}
	}

	@Override
	public void visitDepthFirst(Node node) {
		super.visitDepthFirst(node);
	}

}
