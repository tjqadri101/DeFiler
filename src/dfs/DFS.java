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
    //allocated dfile array
    private ArrayList<DFileID> dfiles;
    //allocated inode array
    private ArrayList<Inode> inodes;
    
    
    
	
	
	//Constructors
	public DFS(String volName, boolean format, DBufferCache cache) {
		super(volName, format);
		myDevilCache = cache;
		
	}

	public DFS(boolean format, DBufferCache cache) {
		super(format);
		myDevilCache = cache;
	}

	public DFS(DBufferCache cache) {
		super();
		myDevilCache = cache;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("\n now in init function");
		myInodes = new LinkedList<Inode>();
		myDFileIDs = new LinkedList<DFileID>();
		myBlockIDs = new LinkedList<Integer>();
		dfiles = new ArrayList<DFileID>();
		inodes = new ArrayList<Inode>();
		
		//initiate DFileIDs
		for (int i=0;i<Constants.MAX_DFILES;i++){
			DFileID dfid = new DFileID(i);
			myDFileIDs.add(dfid);
		}
		
		//initiate Inodes
		for (int i=0;i<Constants.MAX_DFILES;i++){
			Inode inode = new Inode();
			myInodes.add(inode);
		}
		
		
		int inodeSpace = Constants.MAX_DFILES * Constants.INODE_SIZE;
		int totalInodeBlocks = inodeSpace/Constants.BLOCK_SIZE;
		if(inodeSpace%Constants.BLOCK_SIZE != 0)
			totalInodeBlocks++;
		
		//initiate BlockIDS
		for (int i=totalInodeBlocks;i<Constants.NUM_OF_BLOCKS;i++){
			myBlockIDs.add(i);
		}
		
		
		
		
	}
	
	//helper functions 
	public Inode getMyInode(DFileID dfid){
		for(Inode i: inodes){
			if(i.getDFID()==dfid.getDFileID()){
				return i;
			}
		}
		return null;
	}
	public static void writeInodeToDisk(){
		
	}
	public ArrayList<Inode> listAllInodes(){
		return inodes;
	}
	

	@Override
	public synchronized DFileID createDFile() {
		// TODO Auto-generated method stub
		
		System.out.println("\n now in createFile function");
		
		DFileID dfid = myDFileIDs.poll();
		int blockid = myBlockIDs.poll();
		Inode inode = myInodes.poll();
		
		DBuffer dbuf = myDevilCache.getBlock(blockid);
		myDevilCache.releaseBlock(dbuf);
		inode.updateBlockMap(blockid, Constants.BLOCK_SIZE);
		inode.updateDFID(dfid.getDFileID(), blockid);
		dfiles.add(dfid);
		inodes.add(inode);
		
		return dfid;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		
		Inode i = getMyInode(dFID);
		int[] bMap = i.getBlockMap();
		int iter = 0;
		while (bMap[iter]!=-1){
			myBlockIDs.add(bMap[iter]);
		}
		i.freeInode();
		myDFileIDs.add(dFID);
		myInodes.add(i);
		
		inodes.remove(i);
		dfiles.remove(dFID);
	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		
		System.out.println("\n Now in read function");
		
		if (!dfiles.contains(dFID)){
			System.out.println("\n Requested file not found");
			return -1;
		}
		
		Inode i = getMyInode(dFID);
		if(i==null){
			System.out.println("\n Inode not found for requested ");
			return -1;
		}
		
		
		for (int j=0; j<=count/Constants.BLOCK_SIZE; j++){
			int bId = i.getBlockID(j);
			if (bId ==-1){
				System.out.println("\n Block not found in block map");
				return -1;
			}
			DBuffer dbuf = myDevilCache.getBlock(bId);
			int cap;
			if(j==count/Constants.BLOCK_SIZE){
				cap = count - Constants.BLOCK_SIZE*j;
			}
			else{
				cap = Constants.BLOCK_SIZE;
			}
			dbuf.read(buffer, (startOffset+Constants.BLOCK_SIZE*j), cap);
			myDevilCache.releaseBlock(dbuf);
		}
		
		return 0;
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		System.out.println("\n Now in write function");
		
		if (count > Constants.MAX_FILE_BLOCKS*Constants.BLOCK_SIZE){
			System.out.println("\n Write exceeds maximum file size");
			return -1;
		}
		
		if (!dfiles.contains(dFID)){
			System.out.println("\n Requested file not found");
			return -1;
		}
		
		Inode i = getMyInode(dFID);
		if(i==null){
			System.out.println("\n Inode not found for requested ");
			return -1;
		}
	
		
		for (int j=0; j<=count/Constants.BLOCK_SIZE; j++){
			int bId = i.getBlockID(j);
			if (bId ==-1){
				System.out.println("\n Block not found in block map");
			}
			DBuffer dbuf = myDevilCache.getBlock(bId);
			
			int cap;
			if(j==count/Constants.BLOCK_SIZE){
				cap = count - Constants.BLOCK_SIZE*j;
			}
			else{
				cap = Constants.BLOCK_SIZE;
			}
			
			if (dbuf==null){
				int newBlock = myBlockIDs.poll();
				if(!i.updateBlockMap(newBlock, cap)){
					System.out.println("\n File exceeds block bounds");
				}
				dbuf = myDevilCache.getBlock(newBlock);
			}
			
			if(dbuf.write(buffer, startOffset+Constants.BLOCK_SIZE*j, cap)==-1){
				myDevilCache.releaseBlock(dbuf);
				return -1;
			}
			i.updateFileSize(count);
		}
		
		
		
		
		
		return 0;
	}

	@Override
	public int sizeDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		
		Inode i = getMyInode(dFID);
		return i.getFileSize();
	}

	@Override
	public List<DFileID> listAllDFiles() {
		// TODO Auto-generated method stub
		
		return dfiles;
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}
	/*
	public static void main(String args[]){
		DFS d = new DFS(true);
	}
	*/
}
