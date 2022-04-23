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

    public static void main(String[] args){
    }
    // creates empty priority queue
    PriorityQueue(){
        con = new LinkedList<>();
    }

    /**
     * adds element at the appropriate spot
     * @param element the element being added
     * @return T if element was added, F otherwise
     */
    // Enqueue object to correct spot based on priority of object
    // Higher priority is placed towards the front while low priority is
    // placed towards the end
    // In the case of a tie, put object being added behind objects already
    // present with the same priority 
    // Pre: value != null
    // Post: Enqueue value into correct spot based on priority
    public void enqueue(E elementToAdd) { 
        Iterator<E> iterator = con.iterator();
        boolean isAdded = false;
        
        int index = 0;
        while(iterator.hasNext() && !isAdded) {
            E currentElement = iterator.next();
            // If  currentElement > elementToAdd, this is correct spot to add elementToAdd 
            if(currentElement.compareTo(elementToAdd) > 0) { 
                con.add(index, elementToAdd);
                isAdded = true;
            }
            index++;
        }
        
        if(!isAdded) {
            con.addLast(elementToAdd);
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
