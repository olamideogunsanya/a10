/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, Olamide Ogunsanya) this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID: oto272
 *  email address: olamideogunsanya@utexas.edu
 *  Grader name: oto272
 */

import java.util.LinkedList;

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
    public boolean enqueue(E element){
        int prevSize = con.size();
        int spot = 0;
        // special cases add to end if empty list and
        if (con.size() == 0 || con.getLast().compareTo(element) <= 0) {
            con.add(element);
        }
        // continue traversing until an element is added
        while (spot < con.size() && prevSize == con.size()) {
            // find right spot to add
            if (element.compareTo(con.get(spot)) < 0) {
                con.add(spot, element);
            }
            spot++;
        }
        // true if element was added
        return prevSize != con.size();
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
