package common;

import java.util.ArrayList;

public class Inode {

	/**
	 * @param args
	 */
	private DFileID _dFID;
	private int[] _blockMap;
	private int _iter;
	boolean _mapped;
	private int _fileSize;
	
	public Inode(int dFID) {
		_dFID = new DFileID(dFID);
		_mapped = false;
		_blockMap = new int[Constants.BLOCK_MAP_SIZE];
		_iter = 0;
		_fileSize = 0;
		
	}
	
	public boolean updateBlockMap(int blockID, int numBytes){
		if(_iter < _blockMap.length){
			_blockMap[_iter] = blockID;
			_iter++;
			_fileSize += numBytes;
			return true;
		}
		return false;
	}
	
	public synchronized boolean utitlizeInode(){
		if(!_mapped){
			_mapped = true;
			return true;
		}
		return false;
		
	}
	
	public int getDFID(){
		return _dFID.getDFileID();
	}
	
	public int getBlockID(int index){
		int bID = _blockMap[index];
		if(bID == 0)
			return -1;
		return bID;
	}
	
	public int getFileSize(){
		return _fileSize;
	}

}
