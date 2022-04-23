/*  Student information for assignment:
 *
 *  On my honor, Olamide Ogunsanya, this programming assignment is my own work
 *  and I have not provided this code to any other student.
 *
 *  Number of slip days used: 1
 *
 *  Student 1 (Student whose turning account is being used)
 *  UTEID: oto272
 *  email address: olamideogunsanya@utexas.edu
 *  Grader name: Trisha Agrawal
 *
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private HuffCompressor comp;
    private int HF;
    private int[] freq;
    private HuffmanTree tree;
    
    /**
     * Preprocess data so that compression is possible --- count
     * characters/create tree/store state so that a subsequent call to compress
     * will work. The InputStream is <em>not</em> a BitInputStream, so wrap it
     * int one as needed.
     *
     * @param in           is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what
     *                     kind of header to use, standard count format,
     *                     standard tree format, or possibly some format added
     *                     in the future.
     * @return number of bits saved by compression or some other measure Note,
     *         to determine the number of bits saved, the number of bits written
     *         includes ALL bits that will be written including the magic
     *         number, the header format number, the header to reproduce the
     *         tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat)
            throws IOException {
        HF = headerFormat;
        // initialize frequency to ALPH_SIZE + 1 in order to
        freq = new int[ALPH_SIZE + 1];
        comp = new HuffCompressor();
        int oldBits = 0;
        // newbits initialized to 2 * BITS_PER_INT because thats bits for
        // MAGIC_NUMBER and headerFormat
        int newBits = 2 * BITS_PER_INT;
        // counts number of bits in original file and stores in count
        comp.countFreqs(new BitInputStream(new BufferedInputStream(in)),
                oldBits, freq);
        tree = new HuffmanTree(freq);
        if (headerFormat == STORE_COUNTS) {
            // if SCF, then adds to newCount the SCF header size
            newBits += BITS_PER_INT * ALPH_SIZE;
        } else if (headerFormat == STORE_TREE) {
             // if STF, then add another BITS_PER_INT representing size of tree
            newBits += BITS_PER_INT + tree.getSize(comp);
        } else {
            // throw error if not SCF Or STF
            in.close();
            throw new IOException("this type of compresion is not implmented");
        }
        // stores the huffman code of each value into a map (called BitMappings
        // in HuffmanTree class), value added to newCount[0] is # of bits for
        // actual compressed data
        tree.gitBits("", newBits);
        // return the bits saved
        myViewer.update("total uncompressed bits " + (oldBits - newBits));
        return oldBits - newBits;
    
    }

    /**
     * Compresses input to output, where the same InputStream has previously
     * been pre-processed via <code>preprocessCompress</code> storing state used
     * by this call. <br>
     * pre: <code>preprocessCompress</code> must be called before this method
     *
     * @param in    is the stream being compressed (NOT a BitInputStream)
     * @param out   is bound to a file/stream to which bits are written for the
     *              compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger
     *              than the input file. If this is false do not create the
     *              output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file
     *                     or writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force)
            throws IOException {
        if (force) {
            BitInputStream bis = new BitInputStream(new BufferedInputStream(in));
            BitOutputStream bos = new BitOutputStream(new BufferedOutputStream(out));
            // every compression implemented writes the MAGIC_NUMBER and
            // headerFormat
            bos.writeBits(BITS_PER_INT, MAGIC_NUMBER);
            bos.writeBits(BITS_PER_INT, HF);
            // this array will count number of bits in compressed file
            // initialized to following value because of wrote MAGIC_NUMBER and
            int compressedBits = BITS_PER_INT + BITS_PER_INT;
            // if doing SCF, then write SCF header
            if (HF == STORE_COUNTS) {
                comp.writeSCF(bos, compressedBits, freq);
            }
            else if (HF == STORE_TREE) {
                // write the size of the tree
                bos.writeBits(BITS_PER_INT, tree.getSize(comp));
                // add to count appropriately (adding BITS_PER_INT) because
                // added BITS_PER_INT bits for tree size
                compressedBits += BITS_PER_INT;
                // write the STF header
                tree.writeHeaderSTF(bos, compressedBits, comp);
            }
            // else, this compression is not implemented, so close streams and
            // throw error
            else {
                bis.close();
                bos.close();
                throw new IOException("error");
            }
            comp.compress(bis, bos, compressedBits, tree);
            myViewer.update("Total compressed bits" + compressedBits);
            myViewer.showMessage("Compressed bits" + compressedBits);
            return compressedBits;
        }
        // force is false so show an error
        myViewer.showError(
                "Compressed file has more bits than uncompressed file");
        return 0;
    }

    /**
     * Uncompress a previously compressed stream in, writing the uncompressed
     * bits/data to out.
     *
     * @param in  is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file
     *                     or writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        BitInputStream bis = new BitInputStream(new BufferedInputStream(in));
        BitOutputStream bos = new BitOutputStream(new BufferedOutputStream(out));
        HuffDecompressor decomp = new HuffDecompressor();
        // throw error because this means that file has not been compressed, thus
        // no reason to uncompress
        if (bis.readBits(BITS_PER_INT) != MAGIC_NUMBER) {
            bis.close();
            bos.close();
            myViewer.update("Magic number does not match. Halting decompression");
            throw new IOException("File has not been compressed");
        }
        freq = new int[ALPH_SIZE + 1];
        // type will determine whether it is STF or SCF
        int type = bis.readBits(BITS_PER_INT);
        // if SCF
        if (type == STORE_COUNTS) {
            // read SCF header, rebuild the tree, and store the root
            decomp.readSCF(bis, freq);
            tree = new HuffmanTree(freq);
        }
        else if (type == STORE_TREE) {
            // read size of tree
            bis.readBits(BITS_PER_INT);
            // read STF header and store root
            tree = new HuffmanTree(decomp, bis);
        }
        // when it is not STF OR SCF
        else {
            bos.close();
            bis.close();
            throw new IOException("this type of compression has not gone through");
        }
        // write uncompressed file
        myViewer.showMessage("Decompression complete");
        return tree.decode(bis, bos, decomp);
    }

    /**
     * set the viewer
     * @param viewer is the view for communicating.
     */
    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    /**
     * displays the viewer
     * @param s string representation of viewer
     */
    private void showString(String s) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}