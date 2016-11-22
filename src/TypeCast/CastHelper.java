package TypeCast;

import java.util.ArrayList;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.google.common.collect.Table;

import ScaHelper.ScaHelper;
import SymbolTable.Symbol;

public class CastHelper {

	private static ArrayList<String> messages = new ArrayList<>();

	public static ArrayList<String> getMessages() {
		return messages;
	}

	public static void solveCastExpr(CastExpr node, Table<String, Node, Symbol> st) {
		String type = node.getType().toString();
		String name = ((NameExpr) node.getExpr()).getName();
		String type2;
		if (ScaHelper.lookup(st, name, ScaHelper.getParentBlock(node, false), false)) {
			Symbol sembol = ScaHelper.getSymbol(st, name, ScaHelper.getParentBlock(node, false), false);

			if (sembol.getRealType() != null) {

				// if symbol has type
				if (type.equals(sembol.getRealType())) {
					return;
				}

				if (isPrimitive(type) && isPrimitive(sembol.getRealType())) {
					String type1 = primitiveToClass(type);
					type2 = primitiveToClass(sembol.getRealType());
					if (type1.equals(type2)) {
						return;
					}
 
					if (!castablePrimitive(type1, type2)) {
						messages.add("Type mismatch: Can not cast from " + sembol.getRealType() + " to " + type
								+ " at line : " + node.getBeginLine());
					} else {
						// set real type
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, sembol, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
						}
					}
				} else if (isPrimitive(type) || isPrimitive(sembol.getRealType())) {
					if (isPrimitive(type)) {
						String s = sembol.getRealType();
						if (s.contains("Comparable")) {

							if (!s.equals("Comparable<" + primitiveToClass(type) + ">")) {
								// substring get
								messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
										+ " at line : " + node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						} else {
							String s1 = new String(primitiveToClass(type));
							String s2 = new String(sembol.getRealType());

							if (sembol.getRealType().contains("<")) {
								s2 = sembol.getRealType().substring(0, sembol.getRealType().indexOf("<"));
							}

							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								// messages.add("Type mismatch: can not cast
								// from " + sembol.getRealType() + " to " + type
								// + " at line : " + node.getBeginLine());
								messages.add("Type mismatch: can not cast from " + s2 + " to " + s1 + " at line : "
										+ node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					}

					if (isPrimitive(sembol.getRealType())) {
						String s = type;
						if (s.contains("Comparable")) {
							if (!s.equals("Comparable<" + primitiveToClass(sembol.getRealType()) + ">")) {
								messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
										+ " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						} else {
							String s1 = new String(type);
							String s2 = new String(primitiveToClass(sembol.getRealType()));
							if (type.contains("<")) {
								s1 = type.substring(0, type.indexOf("<"));
							}

							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
										+ " at line : " + node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					}
				} else if (type.equals("String") || sembol.getRealType().equals("String")) {
					if (type.equals("String")) {
						String s = sembol.getRealType();
						if (s.contains("Comparable")) {
							if (!s.equals("Comparable<" + type + ">")) {
								messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
										+ " at line : " + node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						} else {
							String s1 = new String(primitiveToClass(type));
							String s2 = new String(sembol.getRealType());
							if (sembol.getRealType().contains("<")) {
								s2 = sembol.getRealType().substring(0, sembol.getRealType().indexOf("<"));
							}

							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
										+ " at line : " + node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					}

					if (sembol.getRealType().equals("String")) {
						String s = type;
						if (s.contains("Comparable")) {
							if (!s.equals("Comparable<" + sembol.getRealType().equals("String") + ">")) {
								messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
										+ " at line : " + node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						} else {
							String s1 = new String(type);
							String s2 = new String(primitiveToClass(sembol.getRealType()));
							if (type.contains("<")) {
								s1 = type.substring(0, type.indexOf("<"));
							}

							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
										+ " at line : " + node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					}
				} else {
					if (type.contains("<") && sembol.getRealType().contains("<")) {
						String s1 = type.substring(type.indexOf("<"), type.length());
						String s2 = sembol.getRealType().substring(sembol.getRealType().indexOf("<"),
								sembol.getRealType().length());
						if (!s1.equals(s2)) {
							messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
									+ " at line : " + node.getBeginLine());
						} else {
							s1 = type.substring(0, type.indexOf("<"));
							s2 = sembol.getRealType().substring(0, sembol.getRealType().indexOf("<"));
							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
										+ " at line : " + node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					} else {
						String s1 = new String(type);
						String s2 = new String(sembol.getRealType());

						if (isPrimitive(sembol.getRealType())) {
							s2 = new String(primitiveToClass(sembol.getRealType()));
						} else {
							s2 = new String(sembol.getRealType());
						}

						if (type.contains("<")) {
							s1 = type.substring(0, type.indexOf("<"));
						}

						if (sembol.getRealType().contains("<")) {
							s2 = sembol.getRealType().substring(0, sembol.getRealType().indexOf("<"));
						}

						CastingHelper.setContain(false);
						Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
						CastingHelper.setContain(false);
						Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
						CastingHelper.setContain(false);
						if (cast1 == null || cast2 == null) {
							return;
						}

						if (!CastingHelper.castable(cast1, s2)) {
							messages.add("Type mismatch: can not cast from " + sembol.getRealType() + " to " + type
									+ " at line : " + node.getBeginLine());
						} else {
							// set real type
							if (node.getParentNode() instanceof AssignExpr) {
								if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
									NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
									setRealType(nameex, sembol, st);
								}

							} else if (node.getParentNode() instanceof VariableDeclarator) {
								setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
							}
						}
					}
				}
			} else {
				// if symbol does not have real type
				if (type.equals(sembol.getType().toString())) {
					return;
				}

				if (isPrimitive(type) && isPrimitive(sembol.getType().toString())) {
					String type1 = primitiveToClass(type);
					type2 = primitiveToClass(sembol.getType().toString());
					if (type1.equals(type2)) {
						return;
					}

					if (!castablePrimitive(type1, type2)) {
						messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to " + type
								+ " at line : " + node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, sembol, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
						}
					}
				} else if (isPrimitive(type) || isPrimitive(sembol.getType().toString())) {
					if (isPrimitive(type)) {
						String s = sembol.getType().toString();
						if (s.contains("Comparable")) {

							if (!s.equals("Comparable<" + primitiveToClass(type) + ">")) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						} else {
							String s1 = new String(primitiveToClass(type));
							String s2 = new String(sembol.getType().toString());

							if (sembol.getType().toString().contains("<")) {
								s2 = sembol.getType().toString().substring(0, sembol.getType().toString().indexOf("<"));
							}

							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					}

					if (isPrimitive(sembol.getType().toString())) {
						String s = type;
						if (s.contains("Comparable")) {
							if (!s.equals("Comparable<" + primitiveToClass(sembol.getType().toString()) + ">")) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						} else {
							String s1 = new String(type);
							String s2 = new String(primitiveToClass(sembol.getType().toString()));
							if (type.contains("<")) {
								s1 = type.substring(0, type.indexOf("<"));
							}

							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					}
				} else if (type.equals("String") || sembol.getType().toString().equals("String")) {
					if (type.equals("String")) {
						String s = sembol.getType().toString();
						if (s.contains("Comparable")) {
							if (!s.equals("Comparable<" + type + ">")) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						} else {
							String s1 = new String(primitiveToClass(type));
							String s2 = new String(sembol.getType().toString());
							if (sembol.getType().toString().contains("<")) {
								s2 = sembol.getType().toString().substring(0, sembol.getType().toString().indexOf("<"));
							}

							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					}

					if (sembol.getType().toString().equals("String")) {
						String s = type;
						if (s.contains("Comparable")) {
							if (!s.equals("Comparable<" + sembol.getType().toString().equals("String") + ">")) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						} else {
							String s1 = new String(type);
							String s2 = new String(primitiveToClass(sembol.getType().toString()));
							if (type.contains("<")) {
								s1 = type.substring(0, type.indexOf("<"));
							}

							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					}
				} else {
					if (type.contains("<") && sembol.getType().toString().contains("<")) {
						String s1 = type.substring(type.indexOf("<"), type.length());
						String s2 = sembol.getType().toString().substring(sembol.getType().toString().indexOf("<"),
								sembol.getType().toString().length());
						if (!s1.equals(s2)) {
							messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
									+ type + " at line : " + node.getBeginLine());
						} else {
							s1 = type.substring(0, type.indexOf("<"));
							s2 = sembol.getType().toString().substring(0, sembol.getType().toString().indexOf("<"));
							CastingHelper.setContain(false);
							Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
							CastingHelper.setContain(false);
							Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
							CastingHelper.setContain(false);
							if (cast1 == null || cast2 == null) {
								return;
							}

							if (!CastingHelper.castable(cast1, s2)) {
								messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
										+ type + " at line : " + node.getBeginLine());
							} else {
								// set real type
								if (node.getParentNode() instanceof AssignExpr) {
									if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
										NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
										setRealType(nameex, sembol, st);
									}

								} else if (node.getParentNode() instanceof VariableDeclarator) {
									setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
								}
							}
						}
					} else {
						String s1 = new String(type);
						String s2 = new String(sembol.getType().toString());
						if (type.contains("<")) {
							s1 = type.substring(0, type.indexOf("<"));
						}

						if (sembol.getType().toString().contains("<")) {
							s2 = sembol.getType().toString().substring(0, sembol.getType().toString().indexOf("<"));
						}

						CastingHelper.setContain(false);
						Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
						CastingHelper.setContain(false);
						Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
						CastingHelper.setContain(false);
						if (cast1 == null || cast2 == null) {
							return;
						}

						if (!CastingHelper.castable(cast1, s2)) {
							messages.add("Type mismatch: can not cast from " + sembol.getType().toString() + " to "
									+ type + " at line : " + node.getBeginLine());
						} else {
							// set real type
							if (node.getParentNode() instanceof AssignExpr) {
								if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
									NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
									setRealType(nameex, sembol, st);
								}

							} else if (node.getParentNode() instanceof VariableDeclarator) {
								setRealType(((VariableDeclarator) node.getParentNode()).getId(), sembol, st);
							}
						}
					}

				}
			}
		}

	}

	private static boolean castablePrimitive(String type1, String type2) {
		if (type1.equals("Boolean") && type2.equals("Boolean")) {
			return true;
		} else if (!type1.equals("Boolean") && !type2.equals("Boolean")) {
			return true;
		} else {
			return false;
		}

	}

	private static String primitiveToClass(String primitive) {

		switch (primitive) {
		case "double":
			return "Double";

		case "int":
			return "Integer";

		case "float":
			return "Float";

		case "long":
			return "Long";

		case "short":
			return "Short";

		case "byte":
			return "Byte";

		case "boolean":
			return "Boolean";

		case "char":
			return "Character";

		default:
			return new String(primitive);
		}
	}

	private static boolean isPrimitive(String primitive) {
		switch (primitive) {
		case "Double":
			return true;

		case "Integer":
			return true;

		case "Float":
			return true;

		case "Long":
			return true;

		case "Short":
			return true;

		case "Byte":
			return true;

		case "Boolean":
			return true;

		case "Character":
			return true;

		case "double":
			return true;

		case "int":
			return true;

		case "float":
			return true;

		case "long":
			return true;

		case "short":
			return true;

		case "byte":
			return true;

		case "boolean":
			return true;

		case "char":
			return true;

		default:
			return false;
		}
	}

	private static void setRealType(NameExpr nameex, Symbol sembol, Table<String, Node, Symbol> st) {
		if (ScaHelper.lookup(st, nameex.getName(), ScaHelper.getParentBlock(nameex, false), false)) {
			Symbol sembol2 = ScaHelper.getSymbol(st, nameex.getName(), ScaHelper.getParentBlock(nameex, false), false);
			if (sembol.getRealType() != null) {
				if (isPrimitive(sembol.getRealType())) {
					sembol2.setRealType(primitiveToClass(sembol.getRealType()));
				} else {
					sembol2.setRealType(sembol.getRealType());
				}

			} else {
				if (isPrimitive(sembol.getType().toString())) {
					sembol2.setRealType(primitiveToClass(sembol.getType().toString()));
				} else {
					sembol2.setRealType(sembol.getType().toString());
				}

			}
			st.put(sembol2.getName(), sembol2.getParentBlock(), sembol2);
		}
	}

	private static void setRealType(VariableDeclaratorId nameex, Symbol sembol, Table<String, Node, Symbol> st) {
		if (ScaHelper.lookup(st, nameex.getName(), ScaHelper.getParentBlock(nameex, false), false)) {
			Symbol sembol2 = ScaHelper.getSymbol(st, nameex.getName(), ScaHelper.getParentBlock(nameex, false), false);
			if (sembol.getRealType() != null) {
				if (isPrimitive(sembol.getRealType())) {
					sembol2.setRealType(primitiveToClass(sembol.getRealType()));
				} else {
					sembol2.setRealType(sembol.getRealType());
				}

			} else {
				if (isPrimitive(sembol.getType().toString())) {
					sembol2.setRealType(primitiveToClass(sembol.getType().toString()));
				} else {
					sembol2.setRealType(sembol.getType().toString());
				}

			}
			st.put(sembol2.getName(), sembol2.getParentBlock(), sembol2);
		}
	}

	public static void solveCastExpr1(CastExpr node, String methodtype, Table<String, Node, Symbol> st) {
		String type = node.getType().toString();
		String type2;

		// if symbol does not have real type
		if (type.equals(methodtype)) {
			return;
		}

		if (isPrimitive(type) && isPrimitive(methodtype)) {
			String type1 = primitiveToClass(type);
			type2 = primitiveToClass(methodtype);
			if (type1.equals(type2)) {
				return;
			}

			if (!castablePrimitive(type1, type2)) {
				messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
						+ node.getBeginLine());
			} else {
				if (node.getParentNode() instanceof AssignExpr) {
					if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
						NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
						setRealType(nameex, methodtype, st);
					}

				} else if (node.getParentNode() instanceof VariableDeclarator) {
					setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
				}
			}
		} else if (isPrimitive(type) || isPrimitive(methodtype)) {
			if (isPrimitive(type)) {
				String s = methodtype;
				if (s.contains("Comparable")) {

					if (!s.equals("Comparable<" + primitiveToClass(type) + ">")) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				} else {
					String s1 = new String(primitiveToClass(type));
					String s2 = new String(methodtype);

					if (methodtype.contains("<")) {
						s2 = methodtype.substring(0, methodtype.indexOf("<"));
					}

					CastingHelper.setContain(false);
					Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
					CastingHelper.setContain(false);
					Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
					CastingHelper.setContain(false);
					if (cast1 == null || cast2 == null) {
						return;
					}

					if (!CastingHelper.castable(cast1, s2)) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				}
			}

			if (isPrimitive(methodtype)) {
				String s = type;
				if (s.contains("Comparable")) {
					if (!s.equals("Comparable<" + primitiveToClass(methodtype) + ">")) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				} else {
					String s1 = new String(type);
					String s2 = new String(primitiveToClass(methodtype));
					if (type.contains("<")) {
						s1 = type.substring(0, type.indexOf("<"));
					}

					CastingHelper.setContain(false);
					Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
					CastingHelper.setContain(false);
					Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
					CastingHelper.setContain(false);
					if (cast1 == null || cast2 == null) {
						return;
					}

					if (!CastingHelper.castable(cast1, s2)) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				}
			}
		} else if (type.equals("String") || methodtype.equals("String")) {
			if (type.equals("String")) {
				String s = methodtype.toString();
				if (s.contains("Comparable")) {
					if (!s.equals("Comparable<" + type + ">")) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				} else {
					String s1 = new String(primitiveToClass(type));
					String s2 = new String(methodtype);
					if (methodtype.contains("<")) {
						s2 = methodtype.substring(0, methodtype.indexOf("<"));
					}

					CastingHelper.setContain(false);
					Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
					CastingHelper.setContain(false);
					Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
					CastingHelper.setContain(false);
					if (cast1 == null || cast2 == null) {
						return;
					}

					if (!CastingHelper.castable(cast1, s2)) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				}
			}

			if (methodtype.equals("String")) {
				String s = type;
				if (s.contains("Comparable")) {
					if (!s.equals("Comparable<" + methodtype.equals("String") + ">")) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				} else {
					String s1 = new String(type);
					String s2 = new String(primitiveToClass(methodtype));
					if (type.contains("<")) {
						s1 = type.substring(0, type.indexOf("<"));
					}

					CastingHelper.setContain(false);
					Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
					CastingHelper.setContain(false);
					Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
					CastingHelper.setContain(false);
					if (cast1 == null || cast2 == null) {
						return;
					}

					if (!CastingHelper.castable(cast1, s2)) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				}
			}
		} else {
			if (type.contains("<") && methodtype.contains("<")) {
				String s1 = type.substring(type.indexOf("<"), type.length());
				String s2 = methodtype.substring(methodtype.indexOf("<"), methodtype.length());
				if (!s1.equals(s2)) {
					messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
							+ node.getBeginLine());
				} else {
					s1 = type.substring(0, type.indexOf("<"));
					s2 = methodtype.substring(0, methodtype.indexOf("<"));
					CastingHelper.setContain(false);
					Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
					CastingHelper.setContain(false);
					Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
					CastingHelper.setContain(false);
					if (cast1 == null || cast2 == null) {
						return;
					}

					if (!CastingHelper.castable(cast1, s2)) {
						messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
								+ node.getBeginLine());
					} else {
						// set real type
						if (node.getParentNode() instanceof AssignExpr) {
							if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
								NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
								setRealType(nameex, methodtype, st);
							}

						} else if (node.getParentNode() instanceof VariableDeclarator) {
							setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
						}
					}
				}
			} else {
				String s1 = new String(type);
				String s2 = new String(methodtype);
				if (type.contains("<")) {
					s1 = type.substring(0, type.indexOf("<"));
				}

				if (methodtype.contains("<")) {
					s2 = methodtype.substring(0, methodtype.indexOf("<"));
				}

				CastingHelper.setContain(false);
				Casting cast1 = CastingHelper.lookup(CastingHelper.GetTree(), s1);
				CastingHelper.setContain(false);
				Casting cast2 = CastingHelper.lookup(CastingHelper.GetTree(), s2);
				CastingHelper.setContain(false);
				if (cast1 == null || cast2 == null) {
					return;
				}

				if (!CastingHelper.castable(cast1, s2)) {
					messages.add("Type mismatch: can not cast from " + methodtype + " to " + type + " at line : "
							+ node.getBeginLine());
				} else {
					// set real type
					if (node.getParentNode() instanceof AssignExpr) {
						if (((AssignExpr) node.getParentNode()).getTarget() instanceof NameExpr) {
							NameExpr nameex = (NameExpr) (((AssignExpr) node.getParentNode()).getTarget());
							setRealType(nameex, methodtype, st);
						}

					} else if (node.getParentNode() instanceof VariableDeclarator) {
						setRealType(((VariableDeclarator) node.getParentNode()).getId(), methodtype, st);
					}
				}
			}

		}
	}

	private static void setRealType(NameExpr nameex, String methodType, Table<String, Node, Symbol> st) {
		if (ScaHelper.lookup(st, nameex.getName(), ScaHelper.getParentBlock(nameex, false), false)) {
			Symbol sembol2 = ScaHelper.getSymbol(st, nameex.getName(), ScaHelper.getParentBlock(nameex, false), false);
			if (isPrimitive(methodType)) {
				sembol2.setRealType(primitiveToClass(methodType));
			} else {
				sembol2.setRealType(methodType);
			}
			st.put(sembol2.getName(), sembol2.getParentBlock(), sembol2);
		}
	}

	private static void setRealType(VariableDeclaratorId nameex, String methodType, Table<String, Node, Symbol> st) {
		if (ScaHelper.lookup(st, nameex.getName(), ScaHelper.getParentBlock(nameex, false), false)) {
			Symbol sembol2 = ScaHelper.getSymbol(st, nameex.getName(), ScaHelper.getParentBlock(nameex, false), false);
			if (isPrimitive(methodType)) {
				sembol2.setRealType(primitiveToClass(methodType));
			} else {
				sembol2.setRealType(methodType);
			}
			st.put(sembol2.getName(), sembol2.getParentBlock(), sembol2);
		}
	}
}
