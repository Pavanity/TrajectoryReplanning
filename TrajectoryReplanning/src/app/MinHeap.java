package app;

public class MinHeap<T extends Comparable<T>> {
	Object[] a;
	private int n;
	
	public MinHeap() {
		a = new Object[10000];
		n = 0;
	}
	
	private int left(int k) {
		return 2*k;
	}
	
	private int right(int k) {
		return 2*k+1;
	}
	
	private int parent(int k) {
		return (int) Math.floor(k/2);
	}
	
	private void siftDown(int i) {
		if (2*i<=n) {
			@SuppressWarnings("unchecked")
			T ai=(T)a[i];
			@SuppressWarnings("unchecked")
			T al=(T)a[left(i)];
			@SuppressWarnings("unchecked")
			T ar=(T)a[right(i)];
			int j=left(i);
			T minv=al;
			if (ai.compareTo(minv)<0) {
				j=i;
				minv=ai;
			}
			if (2*i+1<=n && ar.compareTo(minv)<0) {
				j=right(i);
				minv=ar;
			}
			if (i!=j) {
				@SuppressWarnings("unchecked")
				T tmp=(T)a[i];
				a[i]=a[j];
				a[j]=tmp;
				siftDown(j);
			}
		}
	}
	
	public T extractMin() {
		if (n>0) {
			@SuppressWarnings("unchecked")
			T min = (T) a[1];
			a[1]=a[n];
			n--;
			siftDown(1);
			return min;
		}
		return null;
	}
	
	private void removeAt(int index) {
		if (n > 0 && index < n) {
			if (index == n-1) {
				n--;
				return;
			}
			a[index] = a[n-1];
			n--;
			@SuppressWarnings("unchecked")
			T ai = (T)a[index];
			@SuppressWarnings("unchecked")
			T parent = (T) a[parent(index)];
			if (index == 1 || ai.compareTo(parent) > 0) {
				siftDown(index);
			} else {
				bubbleUp(index);
			}
		}
	}
	
	public void remove(T x) {
		for (int i = 1; i < n; i++) {
			if (a[i].equals(x)) {
				removeAt(i);
				break;
			}
		}
	}
	
	private void bubbleUp(int i) {
		if (i>1) {
			@SuppressWarnings("unchecked")
			T ai=(T)a[i];
			@SuppressWarnings("unchecked")
			T ap=(T)a[parent(i)];
			int j=i;
			if (ap.compareTo(ai)<0) 
				j=parent(i);
			if (j==i) {
				@SuppressWarnings("unchecked")
				T tmp=(T)a[i];
				a[i]=a[parent(i)];
				a[parent(i)]=tmp;
				bubbleUp(parent(i));
			}
		}
	}
	
	public void insert(T x) {
		this.n++;
		a[n]=(Object) x;
		bubbleUp(n);
	}
	
	public void buildHeap(T[] a, int n) {
		for (int i=1;i<=a.length;i++) {
			this.a[i]=(Object)a[i-1];
		}
		this.n=n;
		for (int i=(int) Math.floor(n/2);i>=1;i--) {
			siftDown(i);
		}
	}
	
	public T peek() {
		if (n > 0) {
			@SuppressWarnings("unchecked")
			T min = (T) a[1];
			return min;
		}
		return null;
	}
	
	public String toString() {
		String str="";
		for (int i=1;i<=n;i++) {
			@SuppressWarnings("unchecked")
			T elem=(T) a[i];
			str=str+","+elem.toString();
		}
		return str;
	}
	
	public boolean isEmpty() {
		return (n==0);
	}
	
}
