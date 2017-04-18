import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;
import javax.swing.*;

public class ByteMergeAndSplit{
	public ArrayList<String> readAndFragment(String SourceFileName , String destination,int CHUNK_SIZE) throws IOException {
		File willBeRead = new File(SourceFileName);
		int FILE_SIZE = (int)willBeRead.length();
		ArrayList<String> nameList = new ArrayList<String>();

		int NUMBER_OF_CHUNKS = 0 ;
		byte[] temporary = null ;

		try{
			InputStream inStream = null;
			int totalBytesRead = 0 ;
			try{
				inStream = new BufferedInputStream ( new FileInputStream(willBeRead));
				while(totalBytesRead < FILE_SIZE)
				{
					String PART_NAME=NUMBER_OF_CHUNKS +".chunk";
					int bytesRemaining = FILE_SIZE - totalBytesRead;
					if(bytesRemaining < CHUNK_SIZE)
					{
						CHUNK_SIZE = bytesRemaining ;
					}
					temporary = new byte[CHUNK_SIZE];
					int bytesRead = inStream.read(temporary,0,CHUNK_SIZE);
					if(bytesRead > 0 )
					{
						totalBytesRead += bytesRead ;
						NUMBER_OF_CHUNKS++ ;
					}
					write(temporary,destination +"/" + PART_NAME);
					nameList.add(destination+"/"+PART_NAME);
					//System.out.println("Total Chunks : " + NUMBER_OF_CHUNKS);

				}
				System.out.println(""+NUMBER_OF_CHUNKS);
			}
			finally{
				inStream.close();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return nameList;
	}

	void write(byte[] DataByteArray , String DestinationFileName){
		try{
			OutputStream output = null ;
			try{
				output = new BufferedOutputStream(new FileOutputStream(DestinationFileName));
				output.write(DataByteArray);
			}
			finally{
				output.close();
			}
		}catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	public void mergeParts(ArrayList<String> nameList , String source , String DESTIONATION_PATH ){
		File[] file = new File[nameList.size()];
		byte AllFilesContent[] = null ;
		int TOTAL_SIZE = 0 ;
		int FILE_NUMBER = nameList.size();
		int FILE_LENGTH = 0 ;
		int CURRENT_LENGTH = 0 ;
		for(int i = 0 ; i < FILE_NUMBER ; i++){
			file[i] = new File(source+nameList.get(i));
			TOTAL_SIZE += file[i].length();
		}
		try{
			AllFilesContent = new byte[TOTAL_SIZE];
			InputStream inStream = null ;
			for(int j = 0 ; j < FILE_NUMBER ; j++){
				inStream = new BufferedInputStream(new FileInputStream(file[j]));
				FILE_LENGTH = (int)file[j].length();
				inStream.read(AllFilesContent,CURRENT_LENGTH,FILE_LENGTH);
				CURRENT_LENGTH += FILE_LENGTH ;
				inStream.close();
			}
		}catch(FileNotFoundException e){
			System.out.println("File Not Found "  +e );
		}catch(IOException e){
			System.out.println("Exception while reading the file " + e);
		}finally{
			write(AllFilesContent,DESTIONATION_PATH);
		}
		//System.out.println("Merge was executed successfully");
	}

	public static void main(String args[]){

		/*
		Command for execution
		1) java ByteMergeAndSplit split [source-file] [destination-folder(without / at end)] [chunk-size]

		2) java ByteMergeAndSplit merge [source-folder]/ [path+newfilename] [list of pieces in order]
		*/

		ByteMergeAndSplit obj = new ByteMergeAndSplit();
		ArrayList<String> nameList = new ArrayList<String>();
		try{
			if(args[0].equals("split"))
			{
				nameList = obj.readAndFragment(args[1],args[2],Integer.parseInt(args[3]));

			}else{

				// System.out.println("Arguments received : " + args.length ) ;

				ArrayList<String> nameList2 = new ArrayList<String>();
				for(int i = 3 ; i < args.length ; i++)
					nameList2.add(args[i]);
				obj.mergeParts(nameList2,args[1],args[2]);
			}
		}catch(IOException ioe){
			System.out.println("Exception while reading the file " + ioe);
		}

	}
}
