package test;

import common.Constants;
import common.DFileID;
import dfs.DFS;



public class ReaderThread implements Runnable {
    private DFS dfs;
    private byte[] data;
    private int id;
    private DFileID file;
    
    public ReaderThread(DFS dfs, int id) {
        this.dfs = dfs;
        this.id = id;
    }



    @Override
    public void run() {
    	System.out.println("\n new thread was created");

        file = new DFileID(this.id);
        
        System.out.println(printFile());

    }

    public String printFile() {
        byte[] out = new byte[Constants.BLOCK_SIZE -1]; //this just prints a block size
        dfs.read(file, out, 0, out.length);
        return new String(out);
    }
}