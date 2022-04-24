/*  Student information for assignment:
 *
 *  On <MY> honor, Olamide Ogunsanya) this programming assignment is <MY> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used: 2
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID: oto272
 *  email address: olamideogunsanya@utexas.edu
 *  Grader name: Trisha Agrawal
 */

import java.util.LinkedList;
import java.util.Iterator;

public class PriorityQueue<E extends Comparable< ? super E>>{
    private LinkedList<E> con;
    // creates empty priority queue
    PriorityQueue(){
        con = new LinkedList<>();
    }

    /**
     * pre: none
     * adds elements at correct spot based on fairness
     * @param element element being added
     */
    public void enqueue(E element) { 
        // will be used to see if elemenet is already in queue
        boolean inQ = false;
        Iterator<E> it = con.iterator();
        int idx = 0;
        while(it.hasNext() && !inQ) {
            E curr = it.next();
            // If  currentElement > elementToAdd, this is correct spot to add elementToAdd 
            if(curr.compareTo(element) > 0) { 
                con.add(idx, element);
                inQ = true;
            }
            idx++;
        }
        if(!inQ) {
            con.addLast(element);
        }
    }

    // remove first element in queue
    public E dequeue(){
        return con.remove(0);
    }

    // return size of queue
    public int size(){
        return con.size();
    }

    // return string representation of queue
    public String toString(){
        return con.toString();
    }
}
