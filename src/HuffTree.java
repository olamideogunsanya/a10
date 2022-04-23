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


import java.io.IOException;
import java.util.HashMap;

public class HuffTree {

    private TreeNode root;
    private HashMap<Integer, String> mappings;
    private PriorityQueue<TreeNode> q;
    private int size;

    /**
     * Huffman tree for SCF
     * @param frequencies storage for the frequencies
     */
    HuffTree(int[] frequencies) {
        mappings = new HashMap<>();
        root = makeTree();
        q = buildQueue(frequencies);
    }

    // builds huffman tree for STF
    HuffTree(HuffDecompressor decompressor, BitInputStream in) throws IOException {
        mappings = new HashMap<>();
        root = decompressor.readSTF(in);
    }

    /**
     * builds and fills the priority queue
     * @param freqs frequency storage
     * @return priority queue sorted with treeNodes from lowest to highest
     * priority
     */
    private PriorityQueue<TreeNode> buildQueue(int[] freqs){
        // create queue to be filled and returned
        PriorityQueue<TreeNode> pQueue = new PriorityQueue<>();
        for (int i = 0; i < freqs.length; i++) {
            // if value is not 0 then we need to add it
            if (freqs[i] != 0) {
                // enqueue a new TreeNode with index stored as value, and
                // freqStorage[i] for frequency
                pQueue.enqueue(new TreeNode(i, freqs[i]));
            }
        }
        return pQueue;
    }

    /**
     * pre: none
     * builds the tree needed for SCF
     * @return TreeNode root
     */
    private TreeNode makeTree() {
        while (q.size() > 1) {
            // dequeue 2 nodes, and set first dequeued node to left child,
            // second to right child and set value to whatever (doesn't matter)
            TreeNode n = q.dequeue();
            TreeNode n1 = q.dequeue();
            TreeNode temp = new TreeNode(n, -1, n1);
            // enqueue that new node into priorty queue
            q.enqueue(temp);
        }
        return q.dequeue();
    }

    // return the bit mappings
    public HashMap<Integer, String> getMap(){
        return mappings;
    }

    /**
     * pre: none
     * gets the bitStrings
     * @param bitString
     * @param count
     */
    public void gitBits(String bitString, int count) {
        gitBits(bitString, root, count);
    }

    /**
     * pre: none
     * stores bit sequences for nodes into map and adjusts
     * count accordingly for bits of compressed data
     * @param bitString
     * @param node the node that is being
     * @param count keeps track of the number of bitStrings
     */
    private void gitBits(String bitString, TreeNode node, int count) {
        final String LEFT = "0";
        final String RIGHT = "1";
        if (node.isLeaf()) {
            // put value as key and bitString for value
            mappings.put(node.getValue(), bitString);
            // add the number of bits to be added and multiply it by frequency 
            // pf the node to get the total number of bits added for a value
            count += bitString.length() * node.getFrequency();
        } else {
            // add 0 to bitString and go to left child
            if (node.getLeft() != null) {
                gitBits(bitString + LEFT, node.getLeft(), count);
            }
            // add 1 to bitString and go to right child
            if (node.getRight() != null) {
                gitBits(bitString + RIGHT, node.getRight(), count);
            }
        }
    }

    /**
     * pre: none
     * writes the header and updates count
     * @param out output stream
     * @param count keeps track of number of bits
     * @param compressor object for Huffman Compressor
     */
    public void writeHeaderSTF(BitOutputStream out, int count,
                               HuffCompressor compressor) {
        getSize(compressor);
        compressor.writeSTF(out, root, count);
    }

    /**
     * pre: none
     * @param in input stream
     * @param out output stream
     * @param decompressor object for huffDecompressor
     * @return a decompressed version of the file
     */
    public int decode(BitInputStream in, BitOutputStream out,
                      HuffDecompressor decompressor) throws IOException {
        return decompressor.decipher(in, out, root);
    }



    /**
     * pre: none
     * Return the size of the tree
     * @param compressor compressor object
     * @return the size of tree, calculate it if not set
     */
    public int getSize(HuffCompressor compressor) {
        // if headerSizeSTF is 0, then calculate size (this is not in
        // constructor because SCF doesn't use, so waste of time to calculate)
        if (size == 0) {
            // stores header size of STF so don't have to recalculate when
            // called in precompress and compress
            int count = 0;
            compressor.writeSTF(null, root, count);
            size = count;
        }
        return size;
    }
}
    