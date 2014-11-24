package common;

import java.util.ArrayList;

public class Inode {

	/**
	 * @param args
	 */
	private int _dFID;
	private int[] _blockMap;
	private int _iter;
	boolean _mapped;
	private int _fileSize;
	
	public Inode() {
		init();
		
	}
	private void init(){
		_mapped = false;
		_blockMap = new int[Constants.BLOCK_MAP_SIZE];
		_iter = 0;
		_fileSize = 0;
	}
	public void freeInode(){
		init();
	}
	public boolean updateBlockMap(int blockID, int numBytes){
		if(_iter < _blockMap.length){
			_blockMap[_iter] = blockID;
			_iter++;
			return true;
		}
		return false;
	}
	
	public int[] getBlockMap(){
		return _blockMap;
	}
	
	public synchronized boolean utitlizeInode(){
		if(!_mapped){
			_mapped = true;
			return true;
		}
		return false;
		
	}
	public void updateDFID(int dfid, int blockID){
		_dFID = dfid;
		updateBlockMap(blockID, 0);
	}
	public int getDFID(){
		return _dFID;
	}
	
	public int getBlockID(int index){
		int bID = _blockMap[index];
		if(bID == 0)
			return -1;
		return bID;
	}
	
	public synchronized void updateFileSize(int byteCount){
		if(_fileSize < byteCount)
			_fileSize = byteCount;	
	}
	
	public int getFileSize(){
		return _fileSize;
	}

}
