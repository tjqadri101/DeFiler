package test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	public static void main (String[] args) {
		
		testProgram test = new testProgram();



			try {
				try {
					test.run();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		


	}
}