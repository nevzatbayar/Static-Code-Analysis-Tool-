package DeclarationCatches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
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
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class DeclarationHelper {

	private static ArrayList<String> messages = new ArrayList<>();
	private ArrayList<String> exceptionType = new ArrayList<>();
	private Node parentnode;
	private Node compilationUnit = null;
	private HashMap<String, MethodDeclaration> methods = null;
	public Table<String, Node, Symbol> st = HashBasedTable.create();
	private boolean checkedDivideByZero = false;
	// private boolean divideByZero = false; may be will use
	// private boolean FileNotFound = false;
	// private boolean outOfBounds = false;
	public static boolean nullPointer = false;
	public String exctype = "";
	public String parentinf = "";

	// if has a checked size of buffer will be (checked) true
	private boolean checked = false; // use for for statement
	private boolean checked1 = false;// use for if statement i>0
	private boolean checked2 = false;// use for if statement i<a length
	private boolean checked3 = false;// use for if statement a.length !=0
										// or 0 != a.length and if index
										// equals to zero

	public DeclarationHelper(ArrayList<String> exceptionType, Node node, Table<String, Node, Symbol> st) {
		this.exceptionType = exceptionType;
		this.parentnode = node;
		this.st.putAll(st);
	}

	public static ArrayList<String> getMessages() {
		return messages;
	}

	public ArrayList<String> getExceptionTypes() {
		return exceptionType;
	}

	public Node getParentnode() {
		return parentnode;
	}

	public void checkForGenericException(Node node) {
		fiilparentinfo(node.getParentNode());

		if (exceptionType.contains("Throwable")) {
			if (!exceptionType.contains("Exception")) {

				if (!exceptionType.contains("RuntimeException")) {

					if (!exceptionType.contains("ArithmeticException")) {
						exctype = "Throwable";
						visitForAritmetic(node);
					}

					if (!exceptionType.contains("IndexOutOfBoundsException")) {
						exctype = "Throwable";
						visitForOutofBounds(node);
					}

					if (!exceptionType.contains("NullPointerException")) {
						exctype = "Throwable";
						ExpressionHelper.setExctype(exctype);
						ExpressionHelper.setParentinf(parentinf);
						visitForNullPointer(node);
					}

				}

				if (!exceptionType.contains("IOException")) {
					if (!exceptionType.contains("FileNotFoundException")) {
						exctype = "Throwable";
						visitForFileNotFound(node);
					}
				}
			}
		}

		if (exceptionType.contains("Exception")) {
			if (!exceptionType.contains("RuntimeException")) {

				if (!exceptionType.contains("ArithmeticException")) {
					exctype = "Exception";
					visitForAritmetic(node);
				}

				if (!exceptionType.contains("IndexOutOfBoundsException")) {
					exctype = "Exception";
					visitForOutofBounds(node);
				}

				if (!exceptionType.contains("NullPointerException")) {
					exctype = "Exception";
					ExpressionHelper.setExctype(exctype);
					ExpressionHelper.setParentinf(parentinf);
					visitForNullPointer(node);
				}

			}

			if (!exceptionType.contains("IOException")) {
				if (!exceptionType.contains("FileNotFoundException")) {
					exctype = "Exception";
					visitForFileNotFound(node);
				}
			}

		}

		if (exceptionType.contains("RuntimeException")) {
			if (!exceptionType.contains("ArithmeticException")) {
				exctype = "RuntimeException";
				visitForAritmetic(node);
			}

			if (!exceptionType.contains("IndexOutOfBoundsException")) {
				exctype = "RuntimeException";
				visitForOutofBounds(node);
			}

			if (!exceptionType.contains("NullPointerException")) {
				exctype = "RuntimeException";
				visitForNullPointer(node);
			}
		}

		if (exceptionType.contains("IOException")) {
			if (!exceptionType.contains("FileNotFoundException")) {
				exctype = "IOException";
				visitForFileNotFound(node);
			}
		}

	}

	public void processForAritmetic(Node node) {

		checkedDivideByZero = false;
		if (node instanceof BinaryExpr) {

			if (((BinaryExpr) node).getOperator() == Operator.divide) {
				checkForDivide(((BinaryExpr) node).getRight());
			}
		}
		if (node instanceof AssignExpr) {
			if (((AssignExpr) node).getOperator() == AssignExpr.Operator.slash) {
				checkForDivide(((AssignExpr) node).getValue());
			}
		}

		if (node instanceof ThrowStmt) {
			if (((ThrowStmt) node).getExpr() instanceof ObjectCreationExpr) {
				String message = "";
				ObjectCreationExpr objexpr = (ObjectCreationExpr) ((ThrowStmt) node).getExpr();
				if (objexpr.getType().getName().equals("ArithmeticException")) {
					// divideByZero = true;
					message = parentinf + " instead of " + exctype + ", AritmeticException will occurs at line : "
							+ node.getBeginLine();
					DeclarationHelper.getMessages().add(message);
				}
			}
		}

		if (node instanceof MethodCallExpr) {
			MethodCallExpr metcall = (MethodCallExpr) node;
			if (metcall.getScope() == null) {
				if (compilationUnit == null) {
					compilationUnit = getComplinationUnit(parentnode);
					methods = getMethods(compilationUnit);
				}

				if (methods.containsKey(metcall.getName())) {
					MethodDeclaration metdec = methods.get(metcall.getName());
					if (metdec.getThrows() != null) {
						for (ReferenceType throww : metdec.getThrows()) {
							if (throww.toString().equals("AritmeticException")) {
								String message = "";
								message = parentinf + " instead of " + exctype
										+ ", AritmeticException may occurs at line : " + node.getBeginLine();
								DeclarationHelper.getMessages().add(message);
							}
						}
					}
				}

			}
		}

	}

	public void visitForAritmetic(Node node) {
		// if (!divideByZero) {
		processForAritmetic(node);
		for (Node child : node.getChildrenNodes()) {
			if (child instanceof TryStmt) {
				continue;
			}
			visitForAritmetic(child);
			// }
		}
	}

	public void processForFileNotFound(Node node) {
		String message = "";

		if (node instanceof ExpressionStmt) {
			if (((ExpressionStmt) node).getExpression() instanceof AssignExpr) {
				AssignExpr asexpr = (AssignExpr) ((ExpressionStmt) node).getExpression();
				if (!(asexpr.getValue() instanceof NullLiteralExpr)) {

					if (asexpr.getTarget() instanceof NameExpr) {
						if (ScaHelper.lookup(st, ((NameExpr) asexpr.getTarget()).getName(), node, false)) {
							Symbol sembol = ScaHelper.getSymbol(st, ((NameExpr) asexpr.getTarget()).getName(), node,
									false);
							if (sembol.getType() instanceof ReferenceType) {
								if (((ReferenceType) sembol.getType()).getType() instanceof ClassOrInterfaceType) {
									ClassOrInterfaceType classtype = (ClassOrInterfaceType) (((ReferenceType) sembol
											.getType()).getType());
									switch (classtype.getName()) {
									case "BufferedReader":
									case "BufferedWriter":
									case "OutputStream":
									case "InputStream":
									case "FileOutputStream":
									case "FileInputStream":
									case "FileReader":
									case "FileWriter":
									case "ObjectInputStream":
									case "ObjectOutputStream":
									case "PrintWriter":
										// FileNotFound = true;
										message = parentinf + " instead of " + exctype
												+ ", FileNotFoundException may occurs at line : "
												+ node.getBeginLine();
										DeclarationHelper.getMessages().add(message);
										break;

									default:
										break;
									}
								}
							}

						}
					}
				}
			} else if (((ExpressionStmt) node).getExpression() instanceof VariableDeclarationExpr) {

				VariableDeclarationExpr varexpr = (VariableDeclarationExpr) ((ExpressionStmt) node).getExpression();

				if (varexpr.getType() instanceof ReferenceType) {
					if (((ReferenceType) varexpr.getType()).getType() instanceof ClassOrInterfaceType) {
						ClassOrInterfaceType classtype = (ClassOrInterfaceType) (((ReferenceType) varexpr.getType())
								.getType());
						switch (classtype.getName()) {
						case "BufferedReader":
						case "BufferedWriter":
						case "OutputStream":
						case "InputStream":
						case "FileOutputStream":
						case "FileInputStream":
						case "FileReader":
						case "FileWriter":
						case "ObjectInputStream":
						case "ObjectOutputStream":
						case "PrintWriter":
							addSymbol(varexpr, st);
							break;

						default:
							break;
						}
					}
				}
			}
		}

		if (node instanceof ThrowStmt) {
			if (((ThrowStmt) node).getExpr() instanceof ObjectCreationExpr) {
				ObjectCreationExpr objexpr = (ObjectCreationExpr) ((ThrowStmt) node).getExpr();
				if (objexpr.getType().getName().equals("FileNotFoundException")) {
					// FileNotFound = true;
					message = parentinf + " instead of " + exctype + ", FileNotFoundException will occurs at line : "
							+ node.getBeginLine();
					DeclarationHelper.getMessages().add(message);
				}
			}
		}

		if (node instanceof MethodCallExpr) {
			MethodCallExpr metcall = (MethodCallExpr) node;
			if (metcall.getScope() == null) {
				if (compilationUnit == null) {
					compilationUnit = getComplinationUnit(parentnode);
					methods = getMethods(compilationUnit);
				}

				if (methods.containsKey(metcall.getName())) {
					MethodDeclaration metdec = methods.get(metcall.getName());
					if (metdec.getThrows() != null) {
						for (ReferenceType throww : metdec.getThrows()) {
							if (throww.toString().equals("FileNotFoundException")) {
								message = parentinf + " instead of " + exctype
										+ ", FileNotFoundException may occurs at line : " + node.getBeginLine();
								DeclarationHelper.getMessages().add(message);
							}
						}
					}
				}

			}
		}
	}

	public void visitForFileNotFound(Node node) {
		// if (!FileNotFound) {
		processForFileNotFound(node);
		for (Node child : node.getChildrenNodes()) {
			if (child instanceof TryStmt) {
				continue;
			}
			visitForFileNotFound(child);
		}
		// }
	}

	public void processForOutofBounds(Node node) {
		if (node instanceof ArrayAccessExpr) {
			checked = false;
			checked1 = false;
			checked2 = false;
			checked3 = false;
			solveForStmt(node, ((ArrayAccessExpr) node).getName(), ((ArrayAccessExpr) node).getIndex());
		}

		if (node instanceof ThrowStmt) {
			if (((ThrowStmt) node).getExpr() instanceof ObjectCreationExpr) {
				ObjectCreationExpr objexpr = (ObjectCreationExpr) ((ThrowStmt) node).getExpr();
				if (objexpr.getType().getName().equals("IndexOutOfBoundsException")) {
					String s = parentinf + " instead of " + exctype + ", IndexOutOfBounds may occurs at line: "
							+ node.getBeginLine();
					DeclarationHelper.getMessages().add(s);
					// outOfBounds = true;
				}
			}
		}

		if (node instanceof MethodCallExpr) {
			MethodCallExpr metcall = (MethodCallExpr) node;
			if (metcall.getScope() == null) {
				if (compilationUnit == null) {
					compilationUnit = getComplinationUnit(parentnode);
					methods = getMethods(compilationUnit);
				}

				if (methods.containsKey(metcall.getName())) {
					MethodDeclaration metdec = methods.get(metcall.getName());
					if (metdec.getThrows() != null) {
						for (ReferenceType throww : metdec.getThrows()) {
							if (throww.toString().equals("IndexOutOfBoundsException")) {
								String message = parentinf + " instead of " + exctype
										+ ", IndexOutOfBoundsException may occurs at line : " + node.getBeginLine();
								DeclarationHelper.getMessages().add(message);
							}
						}
					}
				}

			}
		}
	}

	public void visitForOutofBounds(Node node) {
		// if (!outOfBounds) {
		processForOutofBounds(node);
		for (Node child : node.getChildrenNodes()) {
			if (child instanceof TryStmt) {
				continue;
			}
			visitForOutofBounds(child);
		}
		// }
	}

	public void processForNullPointer(Node node) {
		if (node instanceof FieldDeclaration) {

			List<VariableDeclarator> varDList;
			varDList = (List<VariableDeclarator>) ((FieldDeclaration) node).getVariables();

			for (VariableDeclarator var : varDList) {
				boolean a = false;
				boolean b = false;

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
					symbol.setChangedline(node.getBeginLine());
				}
				if (b) {
					symbol.setInitializetoNull(true);
				}
				symbol.setKind("Global Variable");
				symbol.setVartype(varType);
				st.put(var.getId().getName(), ParentBlock, symbol);
			}
		}
		if (node instanceof ExpressionStmt) {
			if (((ExpressionStmt) node).getExpression() instanceof AssignExpr) {

				ExpressionHelper.setChecked(false);
				ExpressionHelper.setChecked1(false);
				ExpressionHelper.SolveAssignExpr(((ExpressionStmt) node).getExpression(), st, false);
			} else if (!(((ExpressionStmt) node).getExpression() instanceof VariableDeclarationExpr)) {
				ExpressionHelper.setChecked(false);
				ExpressionHelper.setChecked1(false);
				ExpressionHelper.SolveExpr(((ExpressionStmt) node).getExpression(), st, false);
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
					symbol.setKind("Constructur parameter");
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

		// will be change
		if (node instanceof ThrowStmt) {
			if (((ThrowStmt) node).getExpr() != null) {
				ExpressionHelper.SolveExpr(((ThrowStmt) node).getExpr(), st, false);
			}

			if (((ThrowStmt) node).getExpr() instanceof ObjectCreationExpr) {
				String message = "";
				ObjectCreationExpr objexpr = (ObjectCreationExpr) ((ThrowStmt) node).getExpr();
				if (objexpr.getType().getName().equals("NullPointerException")) {
					message = parentinf + " instead of " + exctype + ", NullPointerException will occurs at line : "
							+ node.getBeginLine();
					DeclarationHelper.getMessages().add(message);
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

		if (node instanceof MethodCallExpr) {
			MethodCallExpr metcall = (MethodCallExpr) node;
			if (metcall.getScope() == null) {
				if (compilationUnit == null) {
					compilationUnit = getComplinationUnit(parentnode);
					methods = getMethods(compilationUnit);
				}

				if (methods.containsKey(metcall.getName())) {
					MethodDeclaration metdec = methods.get(metcall.getName());
					if (metdec.getThrows() != null) {
						for (ReferenceType throww : metdec.getThrows()) {
							if (throww.toString().equals("NullPointerException")) {
								String message = parentinf + " instead of " + exctype
										+ ", NullPointerException may occurs at line : " + node.getBeginLine();
								DeclarationHelper.getMessages().add(message);
							}
						}
					}
				}

			}
		}
	}

	public void processTryNullPointer(Node node) {
		if (node instanceof FieldDeclaration) {

			List<VariableDeclarator> varDList;
			varDList = (List<VariableDeclarator>) ((FieldDeclaration) node).getVariables();

			for (VariableDeclarator var : varDList) {
				boolean a = false;
				boolean b = false;

				if (var.getInit() != null) {// Binary sabit deðilse solve yolla

					if (!(var.getInit() instanceof NullLiteralExpr)) {
						a = true;

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
					symbol.setChangedline(node.getBeginLine());
				}
				if (b) {
					symbol.setInitializetoNull(true);
				}
				symbol.setKind("Global Variable");
				symbol.setVartype(varType);
				st.put(var.getId().getName(), ParentBlock, symbol);
			}
		}
		if (node instanceof ExpressionStmt) {
			if (((ExpressionStmt) node).getExpression() instanceof AssignExpr) {

				ExpressionHelper.setChecked(false);
				ExpressionHelper.setChecked1(false);
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
					symbol.setKind("Constructur parameter");
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
								if (!(var.getInit() instanceof NullLiteralExpr)) {

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
	}

	public void visitForNullPointer(Node node) {
		// if (!nullPointer) {
		ExpressionHelper.setParentNode(parentnode);
		processForNullPointer(node);
		for (Node child : node.getChildrenNodes()) {
			if (child instanceof TryStmt) {
				processTryNullPointer(node);
			}
			visitForNullPointer(child);
			// }
		}
	}

	private void checkForDivide(Expression expr) {
		String message = "";
		switch (expr.getClass().getSimpleName()) {

		case "IntegerLiteralMinValueExpr":
		case "LongLiteralMinValueExpr":
			break;
		case "DoubleLiteralExpr":
			DoubleLiteralExpr dexpr = (DoubleLiteralExpr) expr;
			if (Double.parseDouble(dexpr.getValue()) == 0) {
				// divideByZero = true;
				message = parentinf + " instead of " + exctype + ", Arithmetic Exception will occurs at line : "
						+ expr.getBeginLine();
				DeclarationHelper.getMessages().add(message);
			}
			break;
		case "IntegerLiteralExpr":
			IntegerLiteralExpr intexpr = (IntegerLiteralExpr) expr;
			if (Integer.parseInt(intexpr.getValue()) == 0) {
				// divideByZero = true;
				message = parentinf + " instead of " + exctype + ", Aritmetich Exception will occurs at line : "
						+ expr.getBeginLine();
				DeclarationHelper.getMessages().add(message);
			}
			break;
		case "LongLiteralExpr":
			LongLiteralExpr longexpr = (LongLiteralExpr) expr;
			if (Long.parseLong(longexpr.getValue()) == 0) {
				// divideByZero = true;
				message = parentinf + " instead of " + exctype + ", Arithmetic Exception will occurs at line : "
						+ expr.getBeginLine();
				DeclarationHelper.getMessages().add(message);
			}
			break;

		default:
			solveIfStatement(expr.getParentNode(), expr);
			if (!checkedDivideByZero) {
				// divideByZero = true;
				message = parentinf + " instead of " + exctype + ", Arithmetic Exception may occurs at line : "
						+ expr.getBeginLine();
				DeclarationHelper.getMessages().add(message);
				break;
			}
		}
	}

	// Check parent if of divide
	private void solveIfStatement(Node node, Expression expr) {
		if (node != parentnode) {
			if (!checkedDivideByZero) {
				if (node instanceof IfStmt) {
					checkIfConditon(((IfStmt) node).getCondition(), expr);
				}
				solveIfStatement(node.getParentNode(), expr);
			}
		}

	}

	private void checkIfConditon(Expression conditon, Expression expr) {
		if (conditon != null) {
			switch (conditon.getClass().getSimpleName()) {
			case "BinaryExpr":
				BinaryExpr binaryexpr = (BinaryExpr) conditon;
				switch (binaryexpr.getOperator()) {
				case notEquals:
					if (binaryexpr.getRight() instanceof IntegerLiteralExpr
							&& binaryexpr.getLeft().toString().equals(expr.toString())) {
						String intexpr = ((IntegerLiteralExpr) binaryexpr.getRight()).getValue();
						if (Integer.parseInt(intexpr) == 0) {
							checkedDivideByZero = true;
						}

					} else if (binaryexpr.getLeft() instanceof IntegerLiteralExpr
							&& binaryexpr.getRight().toString().equals(expr.toString())) {
						String intexpr = ((IntegerLiteralExpr) binaryexpr.getLeft()).getValue();
						if (Integer.parseInt(intexpr) == 0) {
							checkedDivideByZero = true;
						}
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

	public void addSymbol(VariableDeclarationExpr varexpr, Table<String, Node, Symbol> st) {
		String message = "";
		// if (!FileNotFound) {
		List<VariableDeclarator> varDList;
		varDList = (List<VariableDeclarator>) varexpr.getVars();
		for (VariableDeclarator var : varDList) {
			// if (!FileNotFound) {
			boolean a = false;
			if (var.getInit() != null) {

				if (!(var.getInit() instanceof NullLiteralExpr)) {
					a = true;
				}
			}
			if (!a) {

				Node ParentBlock = ScaHelper.getParentBlock(varexpr, false);

				Symbol symbol = new Symbol(var.getId().getName(), var.getBeginLine(), varexpr.getType(), ParentBlock,
						a);

				st.put(var.getId().getName(), ParentBlock, symbol);

			} else {
				// FileNotFound = true;
				message = parentinf + " instead of " + exctype + ", FileNotFoundException may occurs at line : "
						+ varexpr.getBeginLine();
				DeclarationHelper.getMessages().add(message);
			}
			// }
		}
		// }
	}

	// Check if is inside for and index between 0 and its length
	public void solveForStmt(Node node, Expression name, Expression index) {

		if (node != null) {

			if (!checked) {

				switch (node.getClass().getSimpleName()) {

				// Check for parent reach block statement
				case "MethodDeclaration":
				case "EnumDeclaration":
				case "InitializerDeclaration":
				case "ConstructorDeclaration":

					solveIStmt(name.getParentNode(), name, index);
					break;

				// Check parent for of array access Expression
				case "ForStmt":
					checkForExpr(((ForStmt) node).getInit(), ((ForStmt) node).getCompare(), name, index);

					// Check other Parent for's
					solveForStmt(node.getParentNode(), name, index);
					break;

				default:
					// Get parent Node to find parent for statement
					solveForStmt(node.getParentNode(), name, index);
					break;
				}
			}
		}
	}

	// Check if is inside if and index between 0 and its length
	public void solveIStmt(Node node, Expression name, Expression index) {

		if (node != null) {

			if (!(checked1 && checked2) && !checked3) {

				switch (node.getClass().getSimpleName()) {

				// Check for parent reach block statement
				case "MethodDeclaration":
				case "EnumDeclaration":
				case "InitializerDeclaration":
				case "ConstructorDeclaration":

					String s = parentinf + " instead of " + exctype + ", IndexOutOfBounds may occurs when use array "
							+ name.toString() + " with index " + index.toString() + " at line: " + name.getBeginLine();
					DeclarationHelper.getMessages().add(s);
					// outOfBounds = true;
					break;

				// Check parent if of array access expression
				case "IfStmt":
					checkIfExpr(((IfStmt) node).getCondition(), name, index);

					// Check other Parent if's
					solveIStmt(node.getParentNode(), name, index);
					break;

				default:
					// Get parent Node to find parent if statement
					solveIStmt(node.getParentNode(), name, index);
					break;
				}
			}
		}
	}

	// Check if statement condition
	public void checkIfExpr(Expression expr, Expression name, Expression index) {
		if (expr != null) {
			switch (expr.getClass().getSimpleName()) {
			case "BinaryExpr":
				BinaryExpr binaryexpr = (BinaryExpr) expr;
				switch (binaryexpr.getOperator()) {

				case less:
					if (binaryexpr.getLeft() instanceof IntegerLiteralExpr
							&& binaryexpr.getRight().toString().equals(index.toString())) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getLeft()).getValue();
						if (Integer.parseInt(inexpr) >= -1) {
							checked1 = true;
						}
					}

					if (binaryexpr.getLeft().toString().equals(index.toString())) {

						if (binaryexpr.getRight() instanceof FieldAccessExpr) {
							FieldAccessExpr fAExpr = (FieldAccessExpr) binaryexpr.getRight();
							if (fAExpr.getField().equals("length")
									&& fAExpr.getScope().toString().equals(name.toString())) {
								checked2 = true;
							}
						}
					}
					break;
				case lessEquals:
					if (binaryexpr.getLeft() instanceof IntegerLiteralExpr
							&& binaryexpr.getRight().toString().equals(index.toString())) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getLeft()).getValue();
						if (Integer.parseInt(inexpr) >= 0) {
							checked1 = true;
						}
					}
					break;
				case greater:
					if (binaryexpr.getRight() instanceof IntegerLiteralExpr
							&& binaryexpr.getLeft().toString().equals(index.toString())) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getRight()).getValue();
						if (Integer.parseInt(inexpr) >= -1) {
							checked1 = true;
						}
					}

					if (binaryexpr.getRight().toString().equals(index.toString())) {

						if (binaryexpr.getLeft() instanceof FieldAccessExpr) {
							FieldAccessExpr fAExpr = (FieldAccessExpr) binaryexpr.getLeft();
							if (fAExpr.getField().equals("length")
									&& fAExpr.getScope().toString().equals(name.toString())) {
								checked2 = true;
							}
						}
					}
					break;
				case greaterEquals:

					if (binaryexpr.getRight() instanceof IntegerLiteralExpr
							&& binaryexpr.getLeft().toString().equals(index.toString())) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getRight()).getValue();
						if (Integer.parseInt(inexpr) >= 0) {
							checked1 = true;
						}
					}
					break;
				case notEquals:
					if (binaryexpr.getRight() instanceof IntegerLiteralExpr
							&& binaryexpr.getLeft().toString().equals(name.toString() + ".length")) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getRight()).getValue();
						if (Integer.parseInt(inexpr) == 0) {
							checked3 = true;
						}
					}

					if (binaryexpr.getLeft() instanceof IntegerLiteralExpr
							&& binaryexpr.getRight().toString().equals(name.toString() + ".length")) {
						String inexpr = ((IntegerLiteralExpr) binaryexpr.getLeft()).getValue();
						if (Integer.parseInt(inexpr) == 0) {
							checked3 = true;
						}
					}

					break;
				case and:
					checkIfExpr(binaryexpr.getRight(), name, index);
					checkIfExpr(binaryexpr.getLeft(), name, index);
					break;
				default:
					break;
				}
				break;

			case "EnclosedExpr":
				checkIfExpr(((EnclosedExpr) expr).getInner(), name, index);
			default:
				break;
			}
		}
	}

	// Check if for condition between 0 and array length
	public void checkForExpr(List<Expression> initialize, Expression condition, Expression name, Expression index) {

		if (condition != null && initialize != null) {

			// Check condition
			if (condition instanceof BinaryExpr) {

				BinaryExpr brexpr = ((BinaryExpr) condition);

				switch (brexpr.getOperator()) {

				case greater:

					if (brexpr.getLeft().toString().equals(index.toString())
							&& (brexpr.getRight() instanceof IntegerLiteralExpr)) {
						String intexpr = ((IntegerLiteralExpr) brexpr.getRight()).getValue();
						checkGreaterCase1(initialize, brexpr, name, index, true, intexpr);
					}
					if (brexpr.getRight().toString().equals(index.toString())) {
						if (brexpr.getLeft() instanceof FieldAccessExpr) {
							FieldAccessExpr fAExpr = (FieldAccessExpr) brexpr.getLeft();
							if (fAExpr.getField().equals("length")
									&& fAExpr.getScope().toString().equals(name.toString())) {
								checkGreaterCase2(initialize, brexpr, name, index);
							}
						}
					}

					break;
				case greaterEquals:
					if (brexpr.getLeft().toString().equals(index.toString())
							&& (brexpr.getRight() instanceof IntegerLiteralExpr)) {
						String intexpr = ((IntegerLiteralExpr) brexpr.getRight()).getValue();
						checkGreaterCase1(initialize, brexpr, name, index, false, intexpr);
					}
					break;

				case less:
					if (brexpr.getLeft().toString().equals(index.toString())) {
						if (brexpr.getRight() instanceof FieldAccessExpr) {
							FieldAccessExpr fAExpr = (FieldAccessExpr) brexpr.getRight();
							if (fAExpr.getField().equals("length")
									&& fAExpr.getScope().toString().equals(name.toString())) {
								checkGreaterCase2(initialize, brexpr, name, index);
							}
						}
					}

					if (brexpr.getRight().toString().equals(index.toString())
							&& (brexpr.getLeft() instanceof IntegerLiteralExpr)) {
						String intexpr = ((IntegerLiteralExpr) brexpr.getLeft()).getValue();
						checkGreaterCase1(initialize, brexpr, name, index, true, intexpr);
					}
					break;

				case lessEquals:
					if (brexpr.getLeft().toString().equals(index.toString())
							&& (brexpr.getRight() instanceof IntegerLiteralExpr)) {
						String intexpr = ((IntegerLiteralExpr) brexpr.getLeft()).getValue();
						checkGreaterCase1(initialize, brexpr, name, index, false, intexpr);
					}
					break;

				default:
					break;
				}

			}

		}

	}

	// Check if condition i>0
	public void checkGreaterCase1(List<Expression> initialize, BinaryExpr brexpr, Expression name, Expression index,
			boolean greater, String intexpr) {

		if ((Integer.parseInt(intexpr) >= -1 && greater) || (Integer.parseInt(intexpr) >= 0 && !greater)) {

			for (Expression expr : initialize) {

				if (expr instanceof VariableDeclarationExpr) {
					for (VariableDeclarator var : ((VariableDeclarationExpr) expr).getVars()) {
						if (var.getId().getName().equals(index.toString()) && var.getInit() != null) {
							if (var.getInit() instanceof FieldAccessExpr) {
								FieldAccessExpr fieldexpr = (FieldAccessExpr) var.getInit();
								if (fieldexpr.getField().equals("length")
										&& fieldexpr.getScope().toString().equals(name.toString())) {
									checked = true;
								}
							}
						}
					}
				} else if (expr instanceof AssignExpr) {
					AssignExpr asexpr = (AssignExpr) expr;
					if (asexpr.getTarget().toString().equals(index.toString())) {
						if (asexpr.getValue() instanceof FieldAccessExpr) {
							FieldAccessExpr fieldexpr = (FieldAccessExpr) asexpr.getValue();
							if (fieldexpr.getField().equals("length")
									&& fieldexpr.getScope().toString().equals(name.toString())) {
								checked = true;
							}
						}
					}
				}
			}
		}
	}

	// Check if condition a.length > i
	public void checkGreaterCase2(List<Expression> initialize, BinaryExpr brexpr, Expression name, Expression index) {
		for (Expression expr : initialize) {
			if (expr instanceof VariableDeclarationExpr) {
				for (VariableDeclarator var : ((VariableDeclarationExpr) expr).getVars()) {
					if (var.getId().getName().equals(index.toString())) {
						if (var.getInit() instanceof IntegerLiteralExpr) {
							IntegerLiteralExpr intexpr = (IntegerLiteralExpr) var.getInit();
							if (Integer.parseInt(intexpr.toString()) >= 0) {
								checked = true;
							}
						}
					}
				}
			} else if (expr instanceof AssignExpr) {
				AssignExpr asExpr = (AssignExpr) expr;
				if (asExpr.getTarget().toString().equals(index.toString())
						&& asExpr.getValue() instanceof IntegerLiteralExpr) {
					IntegerLiteralExpr intexpr = (IntegerLiteralExpr) asExpr.getValue();
					if (Integer.parseInt(intexpr.toString()) >= 0) {
						checked = true;
					}
				}
			}
		}
	}

	public void fiilparentinfo(Node node) {
		switch (node.getClass().getSimpleName()) {
		case "MethodDeclaration":
			parentinf = "In method " + ((MethodDeclaration) node).getName() + " define at line: " + node.getBeginLine();
			break;

		case "ConstructorDeclaration":
			parentinf = "In constructor " + ((ConstructorDeclaration) node).getName() + " define at line: "
					+ node.getBeginLine();

			break;
		case "TryStmt":
			parentinf = "In try statement" + " define at line: " + node.getBeginLine();

			break;
		}
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
