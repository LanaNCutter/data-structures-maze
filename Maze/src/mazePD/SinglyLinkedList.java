package mazePD;

import java.util.ArrayList;

/** 
 * Code from the Data Structures and Algorithms book
 * @param <E>
 */
public class SinglyLinkedList<E> {
	private static class Node<E> {
		private E element;
		private Node<E> next;
		public Node(E e, Node<E> n) {
			element = e;
			next = n;
		}
		public E getElement() { return element; }
		public Node<E> getNext() { return next; }
		public void setNext(Node<E> n) { next = n; }
	}
	
	private Node<E> head = null;
	private Node<E> tail = null;
	private int size = 0;
	
	public SinglyLinkedList() {}
	
	public int size() { return size; }
	
	public boolean isEmpty() { return size==0; }
	
	public E first() {
		if(isEmpty()) return null;
		return head.getElement();
	}
	
	public E second() {
		if(isEmpty() || head.getNext()==null) return null;
		return head.getNext().getElement();
	}
	
	public E last() {
		if(isEmpty()) return null;
		return tail.getElement();
	}
	
	public void addFirst(E e) {
		head = new Node<E>(e, head);
		if (size == 0) tail = head;
		size++;
	}
	
	public void addLast(E e) {
		Node<E> newest = new Node<E>(e, null);
		if(isEmpty()) head = newest;
		else tail.setNext(newest);
		tail = newest;
		size++;
	}
	
	public E removeFirst() {
		if(isEmpty()) return null;
		E answer = head.getElement();
		head = head.getNext();
		size--;
		if(size==0) tail = null;
		return answer;
	}
	
	ArrayList<E> toArray() {
		ArrayList<E> array = new ArrayList<E>();
		Node<E> current = head;
		
		for(int i = 0; i < this.size(); i++) {
			array.add(current.getElement());
			current = current.getNext();
		}
		
		return array;
	}
	
}
