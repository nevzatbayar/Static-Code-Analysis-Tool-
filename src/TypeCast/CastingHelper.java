package TypeCast;

import java.util.ArrayList;

public class CastingHelper {

	private static Casting tree = null;
	private static ArrayList<Casting> parentNodes;
	private static ArrayList<Casting> childrenNodes;
	private static boolean contain = false;

	public static void setContain(boolean contains) {
		contain = contains;
	}

	public static void creatTypeTree() {

		// Create Root
		tree = new Casting();
		tree.setName("Root");
		tree.setLongname("Root");

		// Create Objects
		Casting object = createLangObject("Object");
		Casting comparable = createLangObject("Comparable");
		Casting number = createLangObject("Number");
		Casting integerr = createLangObject("Integer");
		Casting floatt = createLangObject("Float");
		Casting doublee = createLangObject("Double");
		Casting shortt = createLangObject("Short");
		Casting longg = createLangObject("Long");
		Casting bytee = createLangObject("Byte");
		Casting booleann = createLangObject("Boolean");
		Casting charr = createLangObject("Character");
		Casting string = createLangObject("String");
		Casting charSequence = createLangObject("CharSequence");
		Casting arrayList = createUtilObject("ArrayList");
		Casting abstractList = createUtilObject("AbstractList");
		Casting list = createUtilObject("List");
		Casting abstractCollection = createUtilObject("AbstractCollection");
		Casting collection = createUtilObject("Collection");
		Casting iterable = createUtilObject("Iterable");
		Casting linkedList = createUtilObject("LinkedList");
		Casting deque = createUtilObject("Deque");
		Casting qeuque = createUtilObject("Qeuque");
		Casting abstractSequentialList = createUtilObject("AbstractSequentialList");
		Casting vector = createUtilObject("Vector");
		Casting hashSet = createUtilObject("HashSet");
		Casting abstractSet = createUtilObject("AbstractSet");
		Casting set = createUtilObject("Set");
		Casting linkedHashSet = createUtilObject("LinkedHashSet");
		Casting treeSet = createUtilObject("TreeSet");
		Casting navigableSet = createUtilObject("NavigableSet");
		Casting sortedSet = createUtilObject("SortedSet");
		Casting hashMap = createUtilObject("HashMap");
		Casting abstractMap = createUtilObject("AbstractMap");
		Casting map = createUtilObject("Map");
		Casting hashtable = createUtilObject("Hashtable");
		Casting dictionary = createUtilObject("Dictionary");
		Casting linkedHashMap = createUtilObject("LinkedHashMap");
		Casting identityHashMap = createUtilObject("IdentityHashMap");
		Casting treeMap = createUtilObject("TreeMap");
		Casting navigableMap = createUtilObject("NavigableMap");
		Casting sortedMap = createUtilObject("SortedMap");
		Casting weakHashMap = createUtilObject("WeakHashMap");

		// set children and parent nodes
		parentNodes = new ArrayList<>();
		childrenNodes = new ArrayList<>();

		// Root
		childrenNodes.add(object);
		childrenNodes.add(comparable);
		childrenNodes.add(charSequence);
		childrenNodes.add(iterable);
		childrenNodes.add(map);
		tree.setParentNodes(null);
		tree.setChildrenNodes(childrenNodes);

		// object
		clear();
		parentNodes.add(tree);
		childrenNodes.add(booleann);
		childrenNodes.add(charr);
		childrenNodes.add(number);
		childrenNodes.add(string);
		childrenNodes.add(abstractCollection);
		childrenNodes.add(abstractMap);
		childrenNodes.add(dictionary);
		object.setParentNodes(parentNodes);
		object.setChildrenNodes(childrenNodes);

		// Comparable
		clear();
		parentNodes.add(tree);
		childrenNodes.add(booleann);
		childrenNodes.add(charr);
		childrenNodes.add(integerr);
		childrenNodes.add(floatt);
		childrenNodes.add(doublee);
		childrenNodes.add(shortt);
		childrenNodes.add(longg);
		childrenNodes.add(bytee);
		childrenNodes.add(string);
		comparable.setParentNodes(parentNodes);
		comparable.setChildrenNodes(childrenNodes);

		clear();
		parentNodes.add(number);
		parentNodes.add(comparable);
		integerr.setParentNodes(parentNodes);
		integerr.setChildrenNodes(null);
		floatt.setParentNodes(parentNodes);
		floatt.setChildrenNodes(null);
		doublee.setParentNodes(parentNodes);
		doublee.setChildrenNodes(null);
		shortt.setParentNodes(parentNodes);
		shortt.setChildrenNodes(null);
		longg.setParentNodes(parentNodes);
		longg.setChildrenNodes(null);
		bytee.setParentNodes(parentNodes);
		bytee.setChildrenNodes(null);

		clear();
		parentNodes.add(object);
		parentNodes.add(comparable);
		booleann.setParentNodes(parentNodes);
		booleann.setChildrenNodes(null);
		charr.setParentNodes(parentNodes);
		charr.setChildrenNodes(null);

		clear();
		parentNodes.add(object);
		childrenNodes.add(integerr);
		childrenNodes.add(floatt);
		childrenNodes.add(doublee);
		childrenNodes.add(shortt);
		childrenNodes.add(longg);
		childrenNodes.add(bytee);
		number.setChildrenNodes(childrenNodes);
		number.setParentNodes(parentNodes);

		clear();
		parentNodes.add(object);
		parentNodes.add(comparable);
		parentNodes.add(charSequence);
		string.setChildrenNodes(null);
		string.setParentNodes(parentNodes);

		clear();
		parentNodes.add(tree);
		childrenNodes.add(string);
		charSequence.setChildrenNodes(childrenNodes);
		charSequence.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractList);
		parentNodes.add(list);
		arrayList.setChildrenNodes(null);
		arrayList.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractCollection);
		parentNodes.add(list);
		childrenNodes.add(arrayList);
		childrenNodes.add(abstractSequentialList);
		childrenNodes.add(vector);
		abstractList.setChildrenNodes(childrenNodes);
		abstractList.setParentNodes(parentNodes);

		clear();
		parentNodes.add(collection);
		childrenNodes.add(abstractList);
		childrenNodes.add(abstractSequentialList);
		childrenNodes.add(arrayList);
		childrenNodes.add(linkedList);
		childrenNodes.add(vector);
		list.setChildrenNodes(childrenNodes);
		list.setParentNodes(parentNodes);

		clear();
		parentNodes.add(object);
		parentNodes.add(collection);
		childrenNodes.add(abstractList);
		childrenNodes.add(abstractSet);
		abstractCollection.setChildrenNodes(childrenNodes);
		abstractCollection.setParentNodes(parentNodes);

		clear();
		parentNodes.add(iterable);
		childrenNodes.add(abstractCollection);
		childrenNodes.add(list);
		childrenNodes.add(set);
		childrenNodes.add(qeuque);
		collection.setChildrenNodes(childrenNodes);
		collection.setParentNodes(parentNodes);

		clear();
		parentNodes.add(tree);
		childrenNodes.add(collection);
		iterable.setChildrenNodes(childrenNodes);
		iterable.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractSequentialList);
		parentNodes.add(list);
		parentNodes.add(deque);
		linkedList.setChildrenNodes(null);
		linkedList.setParentNodes(parentNodes);

		clear();
		parentNodes.add(qeuque);
		childrenNodes.add(linkedList);
		deque.setChildrenNodes(childrenNodes);
		deque.setParentNodes(parentNodes);

		clear();
		parentNodes.add(collection);
		childrenNodes.add(deque);
		qeuque.setChildrenNodes(childrenNodes);
		qeuque.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractList);
		childrenNodes.add(linkedList);
		abstractSequentialList.setChildrenNodes(childrenNodes);
		abstractSequentialList.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractList);
		parentNodes.add(list);
		vector.setChildrenNodes(null);
		vector.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractSet);
		parentNodes.add(set);
		childrenNodes.add(linkedHashSet);
		hashSet.setChildrenNodes(childrenNodes);
		hashSet.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractCollection);
		parentNodes.add(set);
		childrenNodes.add(hashSet);
		childrenNodes.add(treeSet);
		abstractSet.setChildrenNodes(childrenNodes);
		abstractSet.setParentNodes(parentNodes);

		clear();
		parentNodes.add(collection);
		childrenNodes.add(hashSet);
		childrenNodes.add(linkedHashSet);
		childrenNodes.add(abstractSet);
		childrenNodes.add(sortedSet);
		set.setChildrenNodes(childrenNodes);
		set.setParentNodes(parentNodes);

		clear();
		parentNodes.add(hashSet);
		parentNodes.add(set);
		linkedHashSet.setChildrenNodes(null);
		linkedHashSet.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractSet);
		parentNodes.add(navigableSet);
		treeSet.setChildrenNodes(null);
		treeSet.setParentNodes(parentNodes);

		clear();
		parentNodes.add(sortedSet);
		childrenNodes.add(treeSet);
		navigableSet.setChildrenNodes(childrenNodes);
		navigableSet.setParentNodes(parentNodes);

		clear();
		parentNodes.add(set);
		childrenNodes.add(navigableSet);
		sortedSet.setChildrenNodes(childrenNodes);
		sortedSet.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractMap);
		parentNodes.add(map);
		childrenNodes.add(linkedHashMap);
		hashMap.setChildrenNodes(childrenNodes);
		hashMap.setParentNodes(parentNodes);

		clear();
		parentNodes.add(object);
		parentNodes.add(map);
		childrenNodes.add(hashMap);
		childrenNodes.add(identityHashMap);
		childrenNodes.add(treeMap);
		childrenNodes.add(weakHashMap);
		abstractMap.setChildrenNodes(childrenNodes);
		abstractMap.setParentNodes(parentNodes);

		clear();
		parentNodes.add(tree);
		childrenNodes.add(hashMap);
		childrenNodes.add(identityHashMap);
		childrenNodes.add(treeMap);
		childrenNodes.add(weakHashMap);
		childrenNodes.add(linkedHashMap);
		childrenNodes.add(sortedMap);
		childrenNodes.add(abstractMap);
		childrenNodes.add(hashtable);
		map.setChildrenNodes(childrenNodes);
		map.setParentNodes(parentNodes);

		clear();
		parentNodes.add(map);
		parentNodes.add(dictionary);
		hashtable.setChildrenNodes(null);
		hashtable.setParentNodes(parentNodes);

		clear();
		parentNodes.add(object);
		childrenNodes.add(hashtable);
		dictionary.setChildrenNodes(childrenNodes);
		dictionary.setParentNodes(parentNodes);

		clear();
		parentNodes.add(map);
		parentNodes.add(hashMap);
		linkedHashMap.setChildrenNodes(null);
		linkedHashMap.setParentNodes(parentNodes);

		clear();
		parentNodes.add(map);
		parentNodes.add(abstractMap);
		identityHashMap.setChildrenNodes(null);
		identityHashMap.setParentNodes(parentNodes);

		clear();
		parentNodes.add(abstractMap);
		parentNodes.add(navigableMap);
		treeMap.setChildrenNodes(null);
		treeMap.setParentNodes(parentNodes);

		clear();
		parentNodes.add(sortedMap);
		childrenNodes.add(treeMap);
		navigableMap.setChildrenNodes(childrenNodes);
		navigableMap.setParentNodes(parentNodes);

		clear();
		parentNodes.add(map);
		childrenNodes.add(navigableMap);
		sortedMap.setChildrenNodes(childrenNodes);
		sortedMap.setParentNodes(parentNodes);

		clear();
		parentNodes.add(map);
		parentNodes.add(abstractMap);
		weakHashMap.setChildrenNodes(null);
		weakHashMap.setParentNodes(parentNodes);

	}

	private static Casting createLangObject(String name) {
		Casting node = new Casting();
		node.setName(name);
		node.setLongname("java.lang." + name);
		return node;
	}

	private static Casting createUtilObject(String name) {
		Casting node = new Casting();
		node.setName(name);
		node.setLongname("java.util." + name);
		return node;
	}

	private static void clear() {
		childrenNodes.clear();
		parentNodes.clear();
	}

	public static Casting GetTree() {
		return tree;
	}

	public static Casting lookup(Casting node, String name) {
		ArrayList<Casting> element = new ArrayList<>();
		if (node == null) {
			return null;
		}

		visittoChild(node, name, element);

		if (contain) {
			return element.get(0);
		}

		return null;
	}

	private static void visittoChild(Casting node, String name, ArrayList<Casting> element) {
		process(node, name, element);
		if (!contain) {
			if (node.getChildrenNodes() != null) {
				for (Casting child : node.getChildrenNodes()) {
					if (!contain) {
						visittoChild(child, name, element);
					}
				}
			}
		}
	}
	
	private static void visittoParent(Casting node, String name, ArrayList<Casting> element) {
		process(node, name, element);
		if (!contain) {
			if (node.getParentNodes() != null) {
				for (Casting parent : node.getParentNodes()) {
					if (!contain) {
						visittoParent(parent, name, element);
					}
				}
			}
		}
	}
	
	private static void process(Casting node, String name, ArrayList<Casting> element) {
		if (node != null) {
			if (node.getName().equals(name)) {
				setContain(true);
				element.add(node);
			}
			
			if (node.getLongname().equals(name)) {
				setContain(true);
				element.add(node);
			}
		}
	}

	private static boolean castabletoChildren(Casting node, String name) {
		
		ArrayList<Casting> element = new ArrayList<>();
		if (node == null) {
			return false;
		}

		visittoChild(node, name, element);

		if (contain) {
			return true;
		}

		return false;
	}

	private static boolean castabletoParent(Casting node, String name) {
		ArrayList<Casting> element = new ArrayList<>();
		if (node == null) {
			return false;
		}

		visittoParent(node, name, element);

		if (contain) {
			return true;
		}

		return false;
	}

	public static boolean castable(Casting node, String name) {
		if (node != null) {
			if (castabletoParent(node, name)) {
				return true;
			} else if (castabletoChildren(node, name)) {
				return true;
			}
		}
		return false;
	}

}
