package common;

import java.util.*;

public class Inode {

	/**
	 * @param args
	 */
	private int _inodeID;
	private int _dFID;
	private int[] _blockMap;
	private int _iter;
	private int _mappedBool;
	private int _fileSize;

	public Inode(int inodeID) {
		_inodeID = inodeID;
		init();

	}
	private synchronized void init(){
		_blockMap = new int[Constants.MAX_FILE_BLOCKS];
		_iter = 0;
		_fileSize = 0;
		_mappedBool = 0;
	}
	public synchronized void freeInode(){
		init();
	}
	public synchronized boolean updateBlockMap(int blockID, int numBytes){
		if(_iter < _blockMap.length){
			_blockMap[_iter] = blockID;
			_iter++;
			if(_mappedBool == 0)
				_mappedBool = 1;
			return true;
		}
		return false;
	}

	public int[] getBlockMap(){
		synchronized(_blockMap){
			return _blockMap;
		}
	}
	
	public synchronized void printBlockMap(){
		System.out.println("Printing Inode " + _inodeID + " block map.");
		for(int i = 0; i < _blockMap.length; i++){
			if(_blockMap[i] == 0) break;
			System.out.println("Block ID: " + _blockMap[i]);
		}
		System.out.println("Inode " + _inodeID + " block map print complete.");
	}

	
	public synchronized void updateDFID(int dfid, int blockID){
		_dFID = dfid;
		updateBlockMap(blockID, 0);
	}
	public synchronized int getDFID(){
		return _dFID;
	}

	public synchronized DFileID getDFileID(){
		return new DFileID(_dFID);
	}

	public synchronized int getBlockID(int index){
		int bID = _blockMap[index];
		if(bID == 0)
			return -1;
		return bID;
	}

	public synchronized int getID(){
		return _inodeID;
	}

	public synchronized void updateFileSize(int byteCount){
		if(_fileSize < byteCount)
			_fileSize = byteCount;	
	}

	public synchronized int getFileSize(){
		return _fileSize;
	}

	
	
	//call this method to write an inode in byte[] for to DBuffer
	public synchronized byte[] getAllInodeData(){
		int[] intInodeData = new int[Constants.MAX_FILE_BLOCKS + 5]; 
		intInodeData[0] = _inodeID;
		//System.out.println("ID " + _inodeID);
		intInodeData[1] = _dFID;
		//System.out.println("dFID " + _dFID);
		intInodeData[2] = _mappedBool;
		//System.out.println("mapped " + _mappedBool);
		intInodeData[3] = _fileSize;
		//System.out.println("Size " + _fileSize);
		intInodeData[4] = _iter;
		//System.out.println("Iter" + _iter);
		for(int i = 5; i < intInodeData.length; i++ ){
			intInodeData[i] = _blockMap[i - 5];
			//if(_blockMap[i-5] > 0)
					//System.out.println(_blockMap[i-5]);
		}
		return Utils.intsToBytes(intInodeData);
	}

	//call this method to recreate an inode from disk data
	public synchronized boolean initFromDisk(byte[] inodeData){
		
		int[] intInodeData = Utils.bytesToInts(inodeData);
		_inodeID = intInodeData[0];
		_dFID = intInodeData[1];
		_mappedBool = intInodeData[2];
		_fileSize = intInodeData[3];
		_iter = intInodeData[4];
		for(int i = 5; i < intInodeData.length; i++){
			_blockMap[i-5] = intInodeData[i];
		}
		//System.out.println("testing " + _inodeID);
		return _mappedBool == 1;
	}

	public boolean equals(Object other){
		Inode otherInode =  (Inode) other;
		if(otherInode.getID() == _inodeID){
			return true;
		}
		return false;
	}
	public synchronized void removeBIDsFromList(Queue<Integer> myBlockIDs) {
		// TODO Auto-generated method stub
		for(int i = 0; i < _blockMap.length; i++){

			if(_blockMap[i] == 0)
				break;
			myBlockIDs.remove(_blockMap[i]);
		}


	}

	/*
	public static void main(String args[]){

		Queue<Inode> test = new LinkedList<Inode>();
		test.add(new Inode(2));
		Inode b = new Inode(2);
		System.out.println(test.remove(b));
		System.out.println(test.size());

	}*/
}
