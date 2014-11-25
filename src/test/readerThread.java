package test;

import common.DFileID;
import dfs.DFS;



public class readerThread implements Runnable {
    private DFS dfs;
    private byte[] data;
    private int id;
    private DFileID file;
    
    public readerThread(DFS dfs, int id) {
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
        byte[] out = new byte[1024];
        dfs.read(file, out, 0, out.length);
        return new String(out);
    }
}