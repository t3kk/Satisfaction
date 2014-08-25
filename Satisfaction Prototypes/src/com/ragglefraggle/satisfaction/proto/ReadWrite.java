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
		long position = 0L;
		int bytesRead;
		long inFileSize = inFileChannel.size();
		System.out.println("infilesize "+ inFileSize);
		
		long startTime = System.currentTimeMillis();
		
		System.out.print("Percent complete:  ");
		int percentComplete;
		//This will run once for sure and then keep running as long as the while condition is met
		do{
			//TODO: check for what would happen if we have 0 bytes left
			//Make the buffer smaller for the last bytes
			if (BYTE_BUFFER_SIZE > inRandomFile.length() - position ){
				//TODO: switch to limit
				fileByteBuffer.limit((int)(inRandomFile.length() - position));
				//System.out.println("Last allocation "+fileByteBuffer.limit()+"vs "+BYTE_BUFFER_SIZE);
			}
			bytesRead = inFileChannel.read(fileByteBuffer, position);
			
			//Need to move the pointer back to the head.
			fileByteBuffer.rewind();
			outFileChannel.write(fileByteBuffer, position);
			
			position = position + bytesRead;
			//Clean up again for the next loop.
			fileByteBuffer.clear();
			
			
			
			percentComplete = (int) ((100*position)/inRandomFile.length());
			if (percentComplete<10){
				System.out.print("\b" + percentComplete );
			}
			else{
				System.out.print("\b\b" + percentComplete );
			}
			
			
			
		}while (bytesRead==BYTE_BUFFER_SIZE);
		//Close access to our file when we are done to prevent leaks.
		inRandomFile.close();
		//Flush to filesystem
		outFileChannel.force(true);
		long outFileSize = outFileChannel.size();
		System.out.println("finalSize "+ outFileSize);
		if (outFileSize==inFileSize){
			System.out.println("SUCCESS!");
		}
		else{
			System.out.println("FILE SIZED DONT MATCH");
		}
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		
		System.out.println("Elapsed time in seconds " + elapsedTime/1000.0);
		
		//Then close file
		outRandomFile.close();
		
	}
	
}
