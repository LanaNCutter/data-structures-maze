package mazePD;

import java.util.ArrayList;

/**
 * Uses a linked list like a stack
 * @author Lana Cutter
 * @param <E>
 */
public class LinkedStack<E> {
	SinglyLinkedList<E> stack;
	
	public LinkedStack() {
		stack = new SinglyLinkedList<E>();
	}
	
	public void push(E e) {
		stack.addFirst(e);
	}
	
	public E pop() {
		return stack.removeFirst();
	}
	
	boolean isEmpty() {
		return stack.isEmpty();
	}
	
	E peek() {
		return stack.first();
	}
	
	E peekSecondLast() {
		return stack.second();
	}
	
	int size() {
		return stack.size();
	}
	
	ArrayList<E> toArray() {
		return stack.toArray();
	}
}
