package SymbolTable;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.Type;

public class Symbol {

	public final static int arraytype = 1;
	public final static int nonarraytype = 0;
	int varType;
	String name;
	Boolean Initialize = false;
	Boolean InitializetoNull = false;
	int referencedline = -1;
	int changedline = -1;
	int declaretedline = -1;
	int initializedline = -1;
	int initializedtonullline = -1;
	Type type;
	String realType = null;
	String kind;
	Node ParentBlock;
	boolean insideBlock = false;
	Node initialInsideBlock = null;
	boolean insidenullBlock = false;
	Node initialnullInsideBlock = null;




	public Symbol(String name, int declaretedline, Type type, Node ParentBlock, Boolean init) {
		this.name = name;
		this.declaretedline = declaretedline;
		this.type = type;
		this.ParentBlock = ParentBlock;
		this.Initialize = init;
	}

	
	public String getRealType() {
		return realType;
	}


	public void setRealType(String realType) {
		this.realType = realType;
	}


	public int getInitializedtonullline() {
		return initializedtonullline;
	}

	public void setInitializedtonullline(int initializedtonullline) {
		this.initializedtonullline = initializedtonullline;
	}

	public int getInitializedline() {
		return initializedline;
	}

	public void setInitializedline(int initializedline) {
		this.initializedline = initializedline;
	}

	public Boolean getInitializetoNull() {
		return InitializetoNull;
	}

	public void setInitializetoNull(Boolean initializetoNull) {
		InitializetoNull = initializetoNull;
	}
	
	public boolean isInsidenullBlock() {
		return insidenullBlock;
	}

	public void setInsidenullBlock(boolean insidenullBlock) {
		this.insidenullBlock = insidenullBlock;
	}

	public Node getInitialnullInsideBlock() {
		return initialnullInsideBlock;
	}

	public void setInitialnullInsideBlock(Node initialnullInsideBlock) {
		this.initialnullInsideBlock = initialnullInsideBlock;
	}
	public Node getInitialInsideBlock() {
		return initialInsideBlock;
	}

	public void setInitialInsideBlock(Node initialInsideBlock) {
		this.initialInsideBlock = initialInsideBlock;
	}

	public boolean isInsideBlock() {
		return insideBlock;
	}

	public void setInsideBlock(boolean insideBlock) {
		this.insideBlock = insideBlock;
	}

	public void setVartype(int varType) {
		this.varType = varType;
	}

	public int getVartype() {
		return varType;
	}

	public Boolean getInitialize() {
		return Initialize;
	}

	public void setInitialize(Boolean initialize) {
		Initialize = initialize;
	}

	public Node getParentBlock() {
		return ParentBlock;
	}

	public int getDeclaretedline() {
		return declaretedline;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setReferencedline(int referencedline) {
		this.referencedline = referencedline;
	}

	public int getReferencedline() {
		return referencedline;
	}

	public int getChangedline() {
		return changedline;
	}

	public void setChangedline(int changedline) {
		this.changedline = changedline;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

}
