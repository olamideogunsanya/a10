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
     * @param freqStorage storage being rebuilt
     * @throws IOException
     */
    public void readSCF(BitInputStream in, int[] freqStorage)
            throws IOException {
        // reading the header
        for (int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
            int bits = in.readBits(IHuffConstants.BITS_PER_INT);
            // if bits == -1, header was formed incorrectly
            if (bits == -1) {
                throw new IOException("error reading header");
            }
            // store bits (frequency) into freqStorage at index k
            freqStorage[i] = bits;
        }
        // incrementing PSEUDO_EOF to build tree correctly and
        // decompression purposes
        freqStorage[PSEUDO_EOF]++;
    }

    /**
     * reads
     * @param in the input stream
     * @return TreeNode root of the tree
     */
    public TreeNode readSTF(BitInputStream in) throws IOException {
        int bit = in.readBits(1);
        if (bit == 0) {
            // continue building tree by reading in header
            // and storing the left and right childdren
            return new TreeNode(readSTF(in), -1, readSTF(in));
        } else if (bit == 1) {
            // if bit=1, then at a leaf node, so scan in next BITS_PER_WORD + 1
            // bits which is value, and frequency as 1
            return new TreeNode(in.readBits(IHuffConstants.BITS_PER_WORD + 1),
                    1);
        }
        // if bit is not 0 or 1 then there is incorrect formatting
        else {
            throw new IOException("erorr in reading bits");
        }
    }

    /**
     * pre: none
     * Writes the data from the uncompressed file into
     * @param in
     * @param out
     * @param root
     * @return
     * @throws IOException
     */
    public int writeData(BitInputStream in, BitOutputStream out, TreeNode root)
            throws IOException {
        TreeNode node = root;
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
                throw new IOException("Error reading compressed file. \n"
                        + "unexpected end of input. No PSEUDO_EOF value.");
            }
            // go to left child
            else if (bit == 0) {
                node = node.getLeft();
            }
            // go to right child
            else if (bit == 1) {
                node = node.getRight();
            }
            if (node.isLeaf()) {
                // if at PSEUDO_EOF, end decompression and close streams
                if (node.getValue() == IHuffConstants.PSEUDO_EOF) {
                    end = true;
                    out.close();
                    in.close();
                }
                else {
                    // write those that value into uncompressed file
                    out.writeBits(IHuffConstants.BITS_PER_WORD,
                            node.getValue());
                    numBits += IHuffConstants.BITS_PER_WORD;
                    // reset to beginning of tree
                    node = root;
                }
            }
        }
        return numBits;
    }
}