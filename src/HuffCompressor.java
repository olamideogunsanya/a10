import java.io.IOException;

public class HuffCompressor implements IHuffConstants {

    /**
     * pre: none
     * post: counts number of bits in file and increments storage
     * container to add that particular frequency
     * @param in input stream
     * @param count counts number of bits
     * @param freqs frequency storage container
     */
    public void countFreqs(BitInputStream in, int count, int[] freqs)
            throws IOException {
        int bis = in.readBits(BITS_PER_WORD);
        // reads through all the bits in file
        while (bis != -1) {
            count += BITS_PER_WORD;
            freqs[bis]++;
            bis = in.readBits(BITS_PER_WORD);
        }
        // once get to end, set frequency of PSEUDO_EOF to since
        // PSEUDO_EOF is at the end of the file
        freqs[PSEUDO_EOF]++;
    }

    /**
     * pre: none
     * post: writes compressed data to output stream using bit sequence stored
     * in bitMappings
     * @param in input stream
     * @param out output stream
     * @param count keeps track of number of bits
     * @param tree variable for huffman tree
     */
    public void compress(BitInputStream in, BitOutputStream out, int count,
                         HuffmanTree tree) throws IOException {
        int bis = in.readBits(BITS_PER_WORD);
        while (bis != -1) {
            // store bit sequence string into compressed file
            translateBits(out, tree.getMap().get(bis), count);
            bis = in.readBits(BITS_PER_WORD);
        }
        // once all bits from BitInputStream are scanned, add the PSEUDO_EOF to
        // compressed file
        translateBits(out, tree.getMap().get(PSEUDO_EOF), count);
        out.close();
    }

    /**
     * pre: none
     * counts how many bits are added and writes the data into output strem
     * @param out output stream
     * @param data data being translated
     * @param count keeps track of the
     */
    private void translateBits(BitOutputStream out, String data, int count) {
        // goes through each character in code, translates that character into
        // an integer and writes it to out
        for (int i = 0; i < data.length(); i++) {
            out.writeBits(1, Integer.parseInt(data.substring(i, i + 1)));
            count++;
        }
    }

    /**
     * pre: none
     * post: count length of SCF header and writes it to output stream
     * @param out output stream
     * @param count keeps track of bits
     * @param freqs frequency storage
     */
    public void writeSCF(BitOutputStream out, int count,
                         int[] freqs) {
        // goes through each frequency in valueStorage, and writes BITS_PER_INT
        // amount of bits for that frequency
        for (int k = 0; k < ALPH_SIZE; k++) {
            out.writeBits(BITS_PER_INT, freqs[k]);
            count += BITS_PER_INT;
        }
    }

    /**
     * pre: none
     * post: count length of the STF header and write it into
     * compressed file
     * @param out output stream
     * @param node
     * @param count keeps track of number
     */
    public void writeSTF(BitOutputStream out, TreeNode node,
                         int count) {
        // increment count because node is not null
        count++;
        if (node.isLeaf()) {
            if (out != null) {
                // write 1 to out because at a leaf node
                out.writeBits(1, 1);
                // write BITS_PER_WORD + 1 (+1 to account for PSEUDO_EOF which
                // will be that many bits) bits for node's value
                out.writeBits(BITS_PER_WORD + 1,
                        node.getValue());
            }
            // update count to account for node's value being added
            count += BITS_PER_WORD + 1;
        } else {
            // if out isn't null, then write 0 to out
            if (out != null) {
                out.writeBits(1, 0);
            }
            // if left child isn't null, recursive step passing in left child
            if (node.getLeft() != null) {
                writeSTF(out, node.getLeft(), count);
            }
            // if right child isn't null, recursive step passing in right child
            if (node.getRight() != null) {
                writeSTF(out, node.getRight(), count);
            }
        }
    }
}