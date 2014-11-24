package dfs;

import java.util.List;

import common.Constants;
import common.DFileID;

public class DFS extends AbstractDFS{

	DFS(String volName, boolean format) {
		super(volName, format);
	}

	DFS(boolean format) {
		super(format);
	}

	DFS() {
		super();
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DFileID createDFile() {
		// TODO Auto-generated method stub
		return null;
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
