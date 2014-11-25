package test;

import common.DFileID;
import dfs.DFS;



public class UserThread implements Runnable {
    private DFS dfs;
    private byte[] data;
    private DFileID file;
    
    public UserThread(DFS dfs, byte[] data) {
        this.dfs = dfs;
        this.data = data;
    }



    @Override
    public void run() {

        DFileID myFileID = dfs.createDFile();
        file = myFileID;
        dfs.write(myFileID, data, 0, data.length);
        
       // System.out.println(printFile()); uncomment this if you want to print a whole file
       

    }

    public String printFile() {
        byte[] out = new byte[data.length];
        dfs.read(file, out, 0, out.length);
        return new String(out);
    }
}