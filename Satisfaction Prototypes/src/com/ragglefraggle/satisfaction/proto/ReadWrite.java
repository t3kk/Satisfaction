package com.ragglefraggle.satisfaction.proto;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.swing.JFileChooser;

public class ReadWrite {
	
	private static final int BYTE_BUFFER_SIZE = 1024*1024*4;
	
	
	
	public static void main(String[] args) throws IOException
	{
		JFileChooser fc = new JFileChooser();
		Component parent = new Window(null);
		fc.showOpenDialog(parent);
		File chosenFile= fc.getSelectedFile();
		
		String fileName = chosenFile.getName().substring(0 , chosenFile.getName().lastIndexOf('.'));
		String fileExtension = chosenFile.getName().substring(chosenFile.getName().lastIndexOf('.')+1);
		String dirOfSelectedFile = chosenFile.getParentFile().toString();
		
		System.out.println(fileName);
		System.out.println(fileExtension);
		System.out.println(dirOfSelectedFile);
		System.out.println(chosenFile);
		
		
				//Open our input file as read only
		RandomAccessFile inRandomFile = new RandomAccessFile(chosenFile, "r"); 	
		//Open our target as read/write
		RandomAccessFile outRandomFile = new RandomAccessFile(dirOfSelectedFile + "\\" + fileName
				+ " copy." + fileExtension, "rw");
		
		//Create filechannels so we can access the bytes anywhere
		FileChannel inFileChannel = inRandomFile.getChannel();
		FileChannel outFileChannel = outRandomFile.getChannel();
		ByteBuffer fileByteBuffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
		long position = 0l;
		int bytesRead;
		long inFileSize = inFileChannel.size();
		System.out.println("infilesize "+ inFileSize);
		
		//This will run once for sure and then keep running as long as the while condition is met
		do{
			//Make the buffer smaller for the last bytes
			if (BYTE_BUFFER_SIZE > inRandomFile.length() - position ){
				fileByteBuffer = ByteBuffer.allocate((int)(inRandomFile.length() - position));
				System.out.println("Last allocation "+fileByteBuffer.capacity()+"vs "+BYTE_BUFFER_SIZE);
			}
			bytesRead = inFileChannel.read(fileByteBuffer, position);
			
			//Need to move the pointer back to the head.
			fileByteBuffer.rewind();
			outFileChannel.write(fileByteBuffer, position);
			position = position + bytesRead;
			//Clean up again for the next loop.
			fileByteBuffer.clear();
			
		}while (bytesRead==BYTE_BUFFER_SIZE);
		//Close access to our file when we are done to prevent leaks.
		inRandomFile.close();
		//Flush to filesystem
//		long truncateOffset = fileByteBuffer.capacity()-bytesRead-1;
//		System.out.println(outFileChannel.size());
//		System.out.println(truncateOffset);
//		outFileChannel.force(true);
//		outFileChannel.truncate(outFileChannel.size()-truncateOffset);
		outFileChannel.force(true);
		long outFileSize = outFileChannel.size();
		System.out.println("finalSize "+ outFileSize);
		if (outFileSize==inFileSize){
			System.out.println("SUCCESS!");
		}
		else{
			System.out.println("FILE SIZED DONT MATCH");
		}
		//Then close file
		outRandomFile.close();
		
	}
	
}
