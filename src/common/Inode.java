package common;

import java.util.ArrayList;

public class Inode {

	/**
	 * @param args
	 */
	private int myDFID;
	private ArrayList<Integer> myBlockMap;
	private int ID;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Inode(int ID) {
		this.ID = ID;
	}
	
	
	public void updateBlockMap(int BlockId){
		myBlockMap.add(BlockId);
	}
	public void updateDFID(int dfid){
		myDFID = dfid;
	}

}
