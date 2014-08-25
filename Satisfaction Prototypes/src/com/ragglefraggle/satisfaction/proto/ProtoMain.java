package com.ragglefraggle.satisfaction.proto;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFileChooser;

public class ProtoMain {

	//If you wanna see what was in the byte array set to true
	final static boolean PRINT_CONTENTS_OF_BYTE_ARRAY = false; 
	
	/**
	 * Copies a file into a new
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//Make a window to choose a file
		JFileChooser fc = new JFileChooser();
		Component parent = new Window(null);
		fc.showOpenDialog(parent);
		//Pop the window
		File chosenFile = fc.getSelectedFile();

		//Figure out the file extension
		String fileName = chosenFile.getName();
		String fileExtension = fileName.substring(fileName.lastIndexOf('.')+1);
		//We are going to make the new files in the same directory of the originally selected file
		String dirOfSelectedFile = chosenFile.getPath();
		
		//Read the file into a array of bytes (ones and zeros)
		//TODO: Use Files with SeekableByteChannel in the future
		Path path = FileSystems.getDefault().getPath(chosenFile.getPath());
		byte[] fileData = Files.readAllBytes(path);
		
		//Write it out one way
		FileOutputStream output = new FileOutputStream(new File(dirOfSelectedFile+"\\target-file."+fileExtension));
		output.write(fileData, 0, fileData.length);
		output.close();
		
		//Try a second way
		Path outPath = FileSystems.getDefault().getPath(dirOfSelectedFile+"\\", "target-file2."+fileExtension);
		Files.write(outPath, fileData);
		
		if (PRINT_CONTENTS_OF_BYTE_ARRAY)
		{
			for (byte item : fileData)
			{
				System.out.println("Byte: " + item + " Character: " + (char)item);
			}
		}

	}
}
