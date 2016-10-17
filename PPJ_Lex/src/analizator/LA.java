package analizator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class LA {
	private static String data;
	private static String state;
	
	public static void main(String[] args) throws IOException{
		BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = bi.readLine()) != null) {
			data += line;
		}
	}
}
