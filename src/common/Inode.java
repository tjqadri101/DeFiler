package common;

import java.util.ArrayList;

public class Inode {

	/**
	 * @param args
	 */
	private int myDFID;
	private ArrayList<Integer> myBlockMap;
	private int ID;
	boolean mapped;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Inode(int ID) {
		this.ID = ID;
	}
	
	public void updateBlockMap(int BlockId){
		myBlockMap.add(BlockId);
	}
	public void detach(int BlockId){
		myBlockMap.remove(BlockId);
	}
	public void updateDFID(int dfid){
		myDFID = dfid;
		mapped = true;
	}
	public int getDFID(){
		return myDFID;
	}
	
	public ArrayList<Integer> getBlockMap(){
		return myBlockMap;
	}

}
