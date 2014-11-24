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
		System.out.println("\now in init function");
		
		myDevilCache = new DBufferCache(Constants.NUM_OF_CACHE_BLOCKS);
		myInodes = new LinkedList<Inode>();
		myDFileIDs = new LinkedList<DFileID>();
		dfiles = new ArrayList<DFileID>();
		inodes = new ArrayList<Inode>();
		
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
	

	@Override
	public synchronized DFileID createDFile() {
		// TODO Auto-generated method stub
		
		System.out.println("\n now in createFile function");
		
		DFileID dfid = myDFileIDs.poll();
		int blockid = myBlockIDs.poll();
		Inode inode = myInodes.poll();
		
		DBuffer dbuf = myDevilCache.getBlock(blockid);
		myDevilCache.releaseBlock(dbuf);
		inode.updateBlockMap(blockid);
		inode.updateDFID(dfid.getDFileID());
		dfiles.add(dfid);
		inodes.add(inode);
		
		return dfid;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		
		System.out.println("\n Now in read function");
		
		if (!dfiles.contains(dFID)){
			System.out.println("\n Requested file not found");
			return 0;
		}
		
		Inode i = getMyInode(dFID);
		if(i==null){
			System.out.println("\n Inode not found for requested ");
			return 0;
		}
		
		ArrayList<Integer> blocks = i.getBlockMap();
		
		for (int j=0; j<=count/Constants.BLOCK_SIZE; j++){
			DBuffer dbuf = myDevilCache.getBlock(blocks.get(j));
			int cap;
			if(j==count/Constants.BLOCK_SIZE){
				cap = count - Constants.BLOCK_SIZE*j;
			}
			else{
				cap = Constants.BLOCK_SIZE;
			}
			dbuf.read(buffer, Constants.BLOCK_SIZE*j, cap);
		}
		
	/*	
	    
		file.getLock().readLock().lock();
		List<Integer> blockIDs = getMappedBlockIDs(file);
		System.out.println("Size of block ids is "+blockIDs.size()+"with numbers\n"+blockIDs.toString());
		int size = blockIDs.size();
		int start = startOffset;
		int howMany = count;
		if (file.getSize() < count)
			howMany = file.getSize();

		for (int i = 0; i < size; i++) {
			DBuffer dbuffer = _cache.getBlock(blockIDs.get(i));

			if (!dbuffer.checkValid()) {
				dbuffer.startFetch();
				dbuffer.waitValid();
			}
	
			int read = dbuffer.read(buffer, start, howMany);
			howMany -= read;
			start += read;
		}
		file.getLock().readLock().unlock();
		return count;
		*/
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
		
		return dfiles;
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[]){
		DFS d = new DFS(true);
	}
}
