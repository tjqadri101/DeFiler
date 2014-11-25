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

	private int _totalInodeBlocks;



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
			Inode inode = new Inode(i); 
			myInodes.add(inode);
		}
		
		
		int inodeSpace = Constants.MAX_DFILES * Constants.INODE_SIZE;
		_totalInodeBlocks = inodeSpace/Constants.BLOCK_SIZE;
		if(inodeSpace%Constants.BLOCK_SIZE != 0)
			_totalInodeBlocks++;

		//initiate BlockIDS
		for (int i=_totalInodeBlocks + 1;i<Constants.NUM_OF_BLOCKS;i++){
			myBlockIDs.add(i);
		}
		
		
		//Check block 0 to see if VDF used for the first time or not
		DBuffer buf = myDevilCache.getBlock(0);
		
		byte[] vdfStatusArr = new byte[Constants.INTEGER_SIZE];
		
		buf.read(vdfStatusArr, 0, Constants.INTEGER_SIZE);
		//System.out.println("Reading block 0");
		//System.out.println(vdfStatusArr.length);
		//System.out.println(vdfStatusArr[3]);
		
		//VDF used for first time
		if(Utils.bytesToInt(vdfStatusArr) == 0){
			System.out.println("First time");
			byte[] vdfStatusArr2 = Utils.intToBytes(1);
			buf.write(vdfStatusArr2, 0, Constants.INTEGER_SIZE);
			//buf.read(vdfStatusArr, 0, Constants.INTEGER_SIZE);
			//System.out.println("Reading block 0");
			//System.out.println(vdfStatusArr.length);
			//System.out.println(vdfStatusArr[3]);
		}
		//update inodes from VDF
		else{
			//System.out.println("Debug init in else");
			System.out.println("VDF used again");
			int inodeCount = 0;
			for(int i = 1; i <= _totalInodeBlocks; i++){
				int seek = 0;
				System.out.println("Reading inode Block"+i);
				DBuffer buffer = myDevilCache.getBlock(i);
				byte[] blockByte = new byte[Constants.BLOCK_SIZE];
				buffer.read(blockByte, 0, Constants.BLOCK_SIZE);
				while(seek < Constants.BLOCK_SIZE){
					Inode test = new Inode(-1);
					byte[] inodeData = new byte[Constants.INODE_SIZE];

					for(int j = 0; j < Constants.INODE_SIZE; j++){
						inodeData[j] = blockByte[j+seek];
					}
					
					if(test.initFromDisk(inodeData)){
						System.out.println("true" + test.getDFileID());
						myInodes.remove(test);
						inodes.add(test);
						myDFileIDs.remove(test.getDFileID());
						dfiles.add(test.getDFileID());
						test.removeBIDsFromList(myBlockIDs);
					};
					seek += Constants.INODE_SIZE;
					inodeCount++;
				}
			}

			//System.out.println("debug"+ inodeCount);

			if(inodeCount > Constants.MAX_DFILES){
				System.out.println("Error. More inodes than possible read from VDF in initiallization");
				System.exit(1);
			}
		}
		
		System.out.println("Initializing with dfile list: ");
		System.out.println(myDFileIDs);
		
		
	}

	//helper functions 
	public synchronized Inode getMyInode(DFileID dfid){
		for(Inode i: inodes){
			if(i.getDFID()==dfid.getDFileID()){
				return i;
			}
		}
		return null;
	}

	//print all mapped inodes
	public void printAllInodes(){
		for(Inode i: inodes){
			i.printBlockMap();
		}
	}
	


	@Override
	public synchronized DFileID createDFile() {
		// TODO Auto-generated method stub

		//System.out.println("\n now in createFile function");

		DFileID dfid = myDFileIDs.poll();
		int blockid = myBlockIDs.poll();
		Inode inode = myInodes.poll();

		DBuffer dbuf = myDevilCache.getBlock(blockid);
		System.out.println("we have bid "+ blockid);
		myDevilCache.releaseBlock(dbuf);

		//inode.updateBlockMap(blockid, Constants.BLOCK_SIZE);
		inode.updateDFID(dfid.getDFileID(), blockid);
		writeInode(inode);
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
		writeInode(i);
		myDFileIDs.add(dFID);
		myInodes.add(i);

		inodes.remove(i);
		dfiles.remove(dFID);

	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub

		//System.out.println("\n Now in read function");

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
			System.out.println("\n read iteration - "+j);
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
		//System.out.println("\n Now in write function");

		if (count > Constants.MAX_FILE_BLOCKS*Constants.BLOCK_SIZE){
			System.out.println("\n Write exceeds maximum file size");
			return -1;
		}

		if (!dfiles.contains(dFID)){
			System.out.println("\n Requested file to write to not found");
			return 0;
		}

		Inode i = getMyInode(dFID);
		if(i==null){
			System.out.println("\n Inode not found for requested ");
			return 0;
		}

		for (int j=0; j<=count/Constants.BLOCK_SIZE; j++){
			
			int bId = i.getBlockID(j);
		
			
			DBuffer dbuf = myDevilCache.getBlock(bId);



			int cap;
			if(j==count/Constants.BLOCK_SIZE){
				cap = count - Constants.BLOCK_SIZE*j;
			}
			else{
				cap = Constants.BLOCK_SIZE;
			}

			

			if (dbuf==null){
				//System.out.println("\n Block not found in block map");
				int newBlock = myBlockIDs.poll();
				if(!i.updateBlockMap(newBlock, cap)){
					System.out.println("\n File exceeds block bounds");
				}
				
				dbuf = myDevilCache.getBlock(newBlock);
				System.out.println("\n block IDs: "+newBlock);
				System.out.println("\n inode IDs: "+i.getID());
				//System.out.println("BlockId write "+newBlock );
			}

			//System.out.println(dbuf.getBlockID());
			
			if(dbuf.write(buffer, startOffset+Constants.BLOCK_SIZE*j, cap)==-1){
				myDevilCache.releaseBlock(dbuf);
				return -1;
			}
		}
		i.updateFileSize(count);
		writeInode(i);
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
		synchronized(dfiles){
			return dfiles;
		}
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		System.out.println("sync started");
		myDevilCache.sync();
	}
	
	//Write inode to DBufferCache when an appropriate update is used
	private void writeInode(Inode inode){
		byte[] inodeData = inode.getAllInodeData();
		DBuffer dbuf = myDevilCache.getBlock((inode.getID()/(Constants.BLOCK_SIZE/Constants.INODE_SIZE)) + 1);
		//System.out.println("testing " + (inode.getID()/_totalInodeBlocks + 1));
		
		byte[] blockByte = new byte[Constants.BLOCK_SIZE];
		dbuf.read(blockByte, 0, Constants.BLOCK_SIZE);
		int seek = (inode.getID() % (Constants.BLOCK_SIZE/Constants.INODE_SIZE))*Constants.INODE_SIZE;
		for(int i = 0; i < Constants.INODE_SIZE; i++){
			blockByte[i + seek] = inodeData[i];
		}
		dbuf.write(blockByte, 0, Constants.BLOCK_SIZE);
		
		
	}
	/*
	public static void main(String args[]){
		DFS d = new DFS(true);
	}
	 */
}
