package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;





import virtualdisk.VirtualDisk;
import common.*;
import dblockcache.DBufferCache;
import dfs.DFS;

public class testProgram {
    public testProgram() {
    
    }
    
    public void run()  throws InterruptedException, FileNotFoundException, IOException{
    	
    	System.out.println("#test started");
    	VirtualDisk disk = new VirtualDisk();
    	DBufferCache cache = new DBufferCache(Constants.NUM_OF_CACHE_BLOCKS, disk);
    	
    	
    	//initialize DFS
        DFS dfs = new DFS(cache);
        dfs.init();
        Thread diskThread = new Thread(disk);
        
        
        //create files
        Path path1 = Paths.get("src/test/file1.txt");
        byte[] data1 = null;
        
        Path path2 = Paths.get("src/test/file2.txt");
        byte[] data2 = null;
        
        Path path3 = Paths.get("src/test/file3.txt");
        byte[] data3 = null;
        
        Path path4 = Paths.get("src/test/file4.txt");
        byte[] data4 = null;
        
		try {
			data1 = Files.readAllBytes(path1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			data2 = Files.readAllBytes(path2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			data3 = Files.readAllBytes(path3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		try {
			data4 = Files.readAllBytes(path4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// create multiple threads that will read from a file and write back to it.
        thread thread1 = new thread(dfs, data1);
        Thread t1 = new Thread(thread1);
        
        thread thread2 = new thread(dfs, data2);
        Thread t2 = new Thread(thread2);
        
        thread thread3 = new thread(dfs, data3);
        Thread t3 = new Thread(thread3);
        
        thread thread4 = new thread(dfs, data4);
        Thread t4 = new Thread(thread4);
        
        diskThread.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        
        
        System.out.println("\n List DFileIDs");
        for (DFileID d : dfs.listAllDFiles()) {
            System.out.println(d);
        }
        
        System.out.println("\n List Inodes mapping");
        for (Inode i : dfs.listAllInodes()) {
            System.out.println(i.getBlockMap());
        }


        
        System.out.println("\n Test Successful");

        dfs.sync();
    }
}