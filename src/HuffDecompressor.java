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

public class HuffDecompressor implements IHuffConstants {

    /**
     * rebuilds freq storage by scanning headStarterSf
     * @param in the input stream
     * @param freqs storage being rebuilt
     */
    public void readSCF(BitInputStream in, int[] freqs)
            throws IOException {
        // reading the header
        for (int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
            int bits = in.readBits(IHuffConstants.BITS_PER_INT);
            // if bits == -1, header was formed incorrectly
            if (bits == -1) {
                throw new IOException("error reading header");
            }
            // store bits frequency into storage
            freqs[i] = bits;
        }
        // incrementing PSEUDO_EOF to build tree correctly and
        // decompression purposes
        freqs[PSEUDO_EOF]++;
    }

    /**
     * pre: none
     * reads STF header in order to determine root
     * @param in the input stream
     * @return root of tree
     */
    public TreeNode readSTF(BitInputStream in) throws IOException {
        int bit = in.readBits(1);
        if (bit == 0) {
            // continue building tree by reading in header
            // and storing the left and right childdren
            return new TreeNode(readSTF(in), -1, readSTF(in));
        } 
		else if (bit == 1) {
            // if bit = 1, then at a leaf node, so scan in next BITS_PER_WORD + 1
            return new TreeNode(in.readBits(IHuffConstants.BITS_PER_WORD + 1),
                    1);
        }
        // if bit is not 0 or 1 then there is an error in the formatting
        else {
            throw new IOException("erorr in reading bits");
        }
    }

    /**
     * pre: none
     * Writes the data from the uncompressed file into the
     * output stream and returns an int to show how many
     * bitsr were written
     * @param in input stream
     * @param out output stream
     * @param root used for deciphering purposes
     * @return how many bits were written
     */
    public int decipher(BitInputStream in, BitOutputStream out, TreeNode root)
            throws IOException {
        TreeNode n = root;
        int numBits = 0;
        boolean end = false;
        // while haven't reached PSEUDO_EOF
        while (!end) {
            int bit = in.readBits(1);
            // if reached end of file without reaching the PSEUDO_EOF value
            // then there is a problem
            if (bit == -1) {
                in.close();
                out.close();
                throw new IOException("Error reading compressed file.");
            }
            // go to left child
            else if (bit == 0) {
                n = n.getLeft();
            }
            // go to right child
            else if (bit == 1) {
                n = n.getRight();
            }
            if (n.isLeaf()) {
                // once PSEUDO_EOF reached end decompression
                if (n.getValue() == IHuffConstants.PSEUDO_EOF) {
                    end = true;
                    out.close();
                    in.close();
                }
                else {
                    // write into uncrompressed file
                    out.writeBits(IHuffConstants.BITS_PER_WORD,
                            n.getValue());
                    numBits += IHuffConstants.BITS_PER_WORD;
                    // reset 
                    n = root;
                }
            }
        }
        return numBits;
    }
}