package TypeCast;

import java.util.ArrayList;

public class Casting {

	private String name;
	private String longName;
	private ArrayList<Casting> parentNodes = null;
	private ArrayList<Casting> childrenNodes = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongname() {
		return longName;
	}

	public void setLongname(String longname) {
		this.longName = longname;
	}

	public ArrayList<Casting> getParentNodes() {
		return parentNodes;
	}

	public void setParentNodes(ArrayList<Casting> parentNodes) {
		if (parentNodes != null) {
			// this.parentNodes = (ArrayList<Casting>) parentNodes.clone();
			this.parentNodes = new ArrayList<>(parentNodes);
		}

	}

	public ArrayList<Casting> getChildrenNodes() {
		return childrenNodes;
	}

	public void setChildrenNodes(ArrayList<Casting> childrenNodes) {
		if (childrenNodes != null) {
			this.childrenNodes = new ArrayList<>(childrenNodes);
		}
	}
}
