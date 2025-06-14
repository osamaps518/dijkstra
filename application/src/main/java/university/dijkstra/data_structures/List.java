package university.dijkstra.data_structures;

public class List<T> {
  private int size;
  private Node<T> head;
  private Node<T> tail;

  public Node<T> getHead() {
    return head;
  }

  public void setHead(Node<T> head) {
    this.head = head;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public Node<T> getTail() {
    return tail;
  }

  public List() {
    this.size = 0;
    this.head = null;
  }

  public int size() {
    return size;
  }

  public boolean contains(T data) {
    Node<T> current = head;
    while (current != null) {
      if (current.data.equals(data)) {
        return true;
      }
      current = current.next;
    }
    return false;
  }

  public void add(T data) {
    Node<T> newNode = new Node<>(data);
    if (head == null) {
      head = newNode;
      tail = newNode;

    } else {
      tail.setNext(newNode);
      newNode.setPrevious(tail);
      tail = newNode;
    }
    size++;
  }

  public void remove(T data) {
    if (head == null)
      return;

    if (head.data.equals(data)) {
      head = head.next;
      size--;
      return;
    }

    Node<T> current = head;
    while (current.next != null) {
      if (current.next.data.equals(data)) {
        current.next = current.next.next;
        size--;
        return;
      }
      current = current.next;
    }
  }

  public static class Node<E> {
    E data;
    Node<E> next;
    Node<E> previous;

    Node(E data) {
      this.data = data;
      this.next = null;
    }

    public E getData() {
      return data;
    }

    public void setData(E data) {
      this.data = data;
    }

    public Node<E> getNext() {
      return next;
    }

    public void setNext(Node<E> next) {
      this.next = next;
    }

    public Node<E> getPrevious() {
      return previous;
    }

    public void setPrevious(Node<E> previous) {
      this.previous = previous;
    }
  }

  public void clear() {
    head = null;
    tail = null;
    size = 0;
  }
}