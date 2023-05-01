package gamecore.algorithms;

import java.lang.reflect.Array;
import java.util.Comparator;

import gamecore.LINQ.LINQ;
import gamecore.datastructures.queues.PriorityQueue;
import gamecore.datastructures.trees.RedBlackTree;


/**
 * Pure comparison-based sorting algorithms.
 * @author Dawn Nye
 */
public final class SortingAlgorithms
{
	/**
	 * No one will make this forbidden class.
	 * If only static classes were a thing in Java (the inner static class doesn't count).
	 */
	private SortingAlgorithms()
	{return;}
	
	/**
	 * Performs a bubble sort of {@code arr}.
	 * When finished, this algorithm leaves {@code arr} in ascending order according to {@code cmp}.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 * @implNote This sort must run in O(n) best-case time and O(n^2) average and worst-case time.
	 */
	public static <T> void BubbleSort(T[] arr, Comparator<? super T> cmp)
	{
		boolean swapped;
		
		do
		{
			swapped = false;
			
			for(int i = 0;i < arr.length - 1;i++)
				if(cmp.compare(arr[i],arr[i + 1]) > 0)
				{
					Swap(arr,i,i + 1);
					swapped = true;
				}
		}
		while(swapped);
		
		return;
	}
	
	/**
	 * Performs an heap sort of {@code arr}.
	 * When finished, this algorithm leaves {@code arr} in ascending order according to {@code cmp}.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 * @implNote This sort must run in O(n log n) worst-case time.
	 */
	public static <T> void HeapSort(T[] arr, Comparator<? super T> cmp)
	{
		PriorityQueue<T> Q = new PriorityQueue(cmp,LINQ.ToIterable(arr));
		
		for(int i = 0;i < arr.length;i++)
			arr[i] = Q.Dequeue();
		
		return;
	}
	
	/**
	 * Performs an insertion sort of {@code arr}.
	 * When finished, this algorithm leaves {@code arr} in ascending order according to {@code cmp}.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 * @implNote This sort must run in O(n) best-case time and O(n^2) average and worst-case time.
	 */
	public static <T> void InsertionSort(T[] arr, Comparator<? super T> cmp)
	{
		for(int i = 0;i < arr.length - 1;i++)
			for(int j = i + 1;j > 0;j--)
				if(cmp.compare(arr[j - 1],arr[j]) > 0)
					Swap(arr,j - 1,j);
				else
					break;
		
		return;
	}
	
	/**
	 * Performs an merge sort of {@code arr}.
	 * When finished, this algorithm leaves {@code arr} in ascending order according to {@code cmp}.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 * @implNote This sort must run in O(n log n) worst-case time.
	 */
	public static <T> void MergeSort(T[] arr, Comparator<? super T> cmp)
	{
		if(arr.length == 0)
			return;
		
		T[] work = (T[])Array.newInstance(arr.getClass().getComponentType(),arr.length);
		RMergeSort(arr,cmp,work,0,arr.length - 1);
		
		return;
	}
	
	/**
	 * Performs a recursive merge sort of {@code arr}.
	 * When finished, {@code arr} from {@code l} to {@code r} will be sorted.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @param work The work array.
	 * @param l The (inclusive) left index of the subarray to merge sort.
	 * @param r The (inclusive) left index of the subarray to merge sort.
	 * @throws NullPointerException Thrown if {@code arr}, {@code cmp}, or {@code work} is null.
	 */
	protected static <T> void RMergeSort(T[] arr, Comparator<? super T> cmp, T[] work, int l, int r)
	{
		if(r <= l)
			return;
		
		int mid = (l + r) >> 1;
		RMergeSort(arr,cmp,work,l,mid);
		RMergeSort(arr,cmp,work,mid + 1,r);
		
		for(int i = l,L = l,R = mid + 1;i <= r;i++)
			if(L > mid)
				work[i] = arr[R++];
			else if(R > r)
				work[i] = arr[L++];
			else
				work[i] = cmp.compare(arr[L],arr[R]) < 0 ? arr[L++] : arr[R++];
		
		for(int i = l;i <= r;i++)
			arr[i] = work[i];
		
		return;
	}
	
	/**
	 * Performs an recursive quick sort of {@code arr}.
	 * When finished, this algorithm leaves {@code arr} in ascending order according to {@code cmp}.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 * @implNote This sort must run in O(n log n) average-case time.
	 */
	public static <T> void QuickSort(T[] arr, Comparator<? super T> cmp)
	{
		RQuickSort(arr,cmp,0,arr.length - 1);
		return;
	}
	
	/**
	 * Performs a recursive quick sort of {@code arr}.
	 * When finished, {@code arr} from {@code l} to {@code r} will be sorted.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @param work The work array.
	 * @param l The (inclusive) left index of the subarray to quick sort.
	 * @param r The (inclusive) left index of the subarray to quick sort.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 */
	protected static <T> void RQuickSort(T[] arr, Comparator<? super T> cmp, int l, int r)
	{
		if(r <= l)
			return;
		
		int p = PickPivot(arr,cmp,l,r);
		Swap(arr,r,p);
		
		int L = l - 1;
		int R = r;
		
		while(true)
		{
			do
			{L++;} // This can never go past the pivot, which is not smaller than itself, so we don't need a bounds check on L
			while(cmp.compare(arr[L],arr[r]) < 0);
				
			do
			{R--;} // This does not guarantee there is an element smaller than the pivot left, so we need to bounds check R
			while(R >= l && cmp.compare(arr[R],arr[r]) > 0);
			
			if(L < R)
				Swap(arr,L,R);
			else
				break;
		}
		
		Swap(arr,r,L);
		
		RQuickSort(arr,cmp,l,L - 1);
		RQuickSort(arr,cmp,L + 1,r);
		
		return;
	}
	
	/**
	 * Picks a pivot of {@code arr}.
	 * @param <T> The type of data in the array.
	 * @param arr The array to pick a pivot out of.
	 * @param cmp The means by which elements are compared.
	 * @param l The (inclusive) left index of the subarray to pick a pivot out of.
	 * @param r The (inclusive) right index of the subarray to pick a pivot out of.
	 * @return Returns the index of the pivot.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 */
	protected static <T> int PickPivot(T[] arr, Comparator<? super T> cmp, int l, int r)
	{return l;}
	
	/**
	 * Performs a selection sort of {@code arr}.
	 * When finished, this algorithm leaves {@code arr} in ascending order according to {@code cmp}.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 * @implNote This sort must run in O(n^2) worst-case time.
	 */
	public static <T> void SelectionSort(T[] arr, Comparator<? super T> cmp)
	{
		for(int i = 0;i < arr.length - 1;i++)
		{
			int min = i;
			
			for(int j = i + 1;j < arr.length;j++)
				if(cmp.compare(arr[min],arr[j]) > 0)
					min = j;
			
			if(min != i)
				Swap(arr,i,min);
		}
		
		return;
	}
	
	/**
	 * Performs a tree sort of {@code arr}.
	 * When finished, this algorithm leaves {@code arr} in ascending order according to {@code cmp}.
	 * @param <T> The type of data being sorted.
	 * @param arr The array to sort.
	 * @param cmp The means by which items are compared.
	 * @throws NullPointerException Thrown if {@code arr} or {@code cmp} is null.
	 * @implNote This sort must run in O(n log n) worst-case time.
	 */
	public static <T> void TreeSort(T[] arr, Comparator<? super T> cmp)
	{
		RedBlackTree<T> T = new RedBlackTree<T>(LINQ.ToIterable(arr),cmp);
		T.InOrderTraversal((e,index,depth) -> arr[index] = e);
		
		return;
	}
	
	/**
	 * Swaps two elements of an array.
	 * @param <T> The type to swap.
	 * @param arr The array to swap items within.
	 * @param i The first index to swap.
	 * @param j The second index to swap.
	 */
	public static <T> void Swap(T[] arr, int i, int j)
	{
		T temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
		
		return;
	}
}