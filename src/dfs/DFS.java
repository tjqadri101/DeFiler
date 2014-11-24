package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import virtualdisk.VirtualDisk;

import common.*;
import dblockcache.*;

public class DFS extends AbstractDFS{
	
	//Objects needed
	//Buffer Cache
	private DBufferCache myDevilCache;
	//Disk
    private VirtualDisk  myDevilDisk;
    //Inode Array
    private Queue<Inode> myInodes;
    //DFID Array
    private Queue<DFileID> myDFileIDs;
    //BlockID Array
    private Queue<Integer> myBlockIDs;
    //dfile array
    private Queue<Integer> dfiles;
    
    
    
	
	
	//Constructors
	DFS(String volName, boolean format) {
		super(volName, format);
		//create Disk
		try {
			myDevilDisk = new VirtualDisk(volName, format);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	DFS(boolean format) {
		super(format);
		//create Disk
		try {
			myDevilDisk = new VirtualDisk(format);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	DFS() {
		super();
		//create Disk
		try {
			myDevilDisk = new VirtualDisk();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		myDevilCache = new DBufferCache(Constants.NUM_OF_CACHE_BLOCKS);
		myInodes = new LinkedList<Inode>();
		myDFileIDs = new LinkedList<DFileID>();
		dfiles = new LinkedList<Integer>();
		
		//initiate DFileIDs
		for (int i=0;i<Constants.MAX_DFILES;i++){
			DFileID dfid = new DFileID(i);
			myDFileIDs.add(dfid);
		}
		
		//initiate Inodes
		for (int i=0;i<Constants.MAX_DFILES;i++){
			Inode inode = new Inode(i);
			myInodes.add(inode);
		}
		
		
		int totalInodeBlocks = nearestCeiling(Constants.MAX_DFILES, Constants.INODE_SIZE/Constants.BLOCK_SIZE);
		
		//initiate BlockIDS
		for (int i=totalInodeBlocks;i<Constants.NUM_OF_BLOCKS;i++){
			myBlockIDs.add(i);
		}
		
		
		
		
	}
	
	//helper functions 
	public static int nearestCeiling(int numerator, int denominator){
		return (((numerator + denominator-1) )/denominator);
	}
	public static void writeInodeToDisk(){
		
	}
	

	@Override
	public DFileID createDFile() {
		// TODO Auto-generated method stub
		
		DFileID dfid = myDFileIDs.poll();
		int blockid = myBlockIDs.poll();
		Inode inode = myInodes.poll();
		
		DBuffer dbuf = myDevilCache.getBlock(blockid);
		inode.updateBlockMap(blockid);
		inode.updateDFID(dfid.getDFileID());
		dfiles.add(dfid.getDFileID());
		
		return dfid;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sizeDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DFileID> listAllDFiles() {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[]){
		DFS d = new DFS(true);
	}
}
