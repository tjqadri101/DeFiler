package test;

import common.DFileID;
import dfs.DFS;



public class readerThread implements Runnable {
    private DFS dfs;
    private byte[] data;
    private DFileID file;
    
    public readerThread(DFS dfs, byte[] data) {
        this.dfs = dfs;
        this.data = data;
    }



    @Override
    public void run() {
    	System.out.println("\n new thread was created");

        file = new DFileID(1);
        
        System.out.println(printFile());

    }

    public String printFile() {
        byte[] out = new byte[data.length];
        dfs.read(file, out, 0, out.length);
        return new String(out);
    }
}