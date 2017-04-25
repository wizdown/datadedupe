/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 *
 * @author gurleen
 */
public class FastCDC {

/**
 * @param args the command line arguments
 */
int len;
int count=0;
long [] G;
BigInteger modu;
int max,min,normal;
long maskS,maskA,maskL;
FastCDC(int n,int a,int b,int c) throws FileNotFoundException, IOException
{
        maskS=0x0003590703530000L;
        maskA= 0x0000d90303530000L;
        maskL=0x0000d90003530000L;
        max=a;
        min=b;
        normal=c;
        modu=BigInteger.valueOf(2);
        len=n;
        G=new long [256];
        byte [] data = new  byte [8];
        FileInputStream fis = new FileInputStream(new File("script/block01.txt"));
        for(int i=0; i<256; i++)
        {
                fis.read(data, 0, 8);
                ByteBuffer buffer = ByteBuffer.allocate(8);
                buffer.put(data);
                buffer.flip();
                G[i]=buffer.getLong();
                //  System.out.println(G[i]);
        }
        modu=modu.pow(n);
}
void write(byte[] DataByteArray, String DestinationFileName){
        try{
                OutputStream output = null;
                try{
                        output = new BufferedOutputStream(new FileOutputStream(DestinationFileName));
                        output.write(DataByteArray);
                }
                finally {
                        output.close();
                }
        }catch(FileNotFoundException ex)
        {
                ex.printStackTrace();
        }catch(IOException ex) {
                ex.printStackTrace();
        }
}
public ArrayList<String> readAndFragment(String SourceFileName, String destination) throws IOException {
        File willBeRead = new File(SourceFileName);
        int FILE_SIZE = (int)willBeRead.length();
        ArrayList<String> nameList = new ArrayList<String>();

        int NUMBER_OF_CHUNKS = 0;
        byte[] temporary = null;
        byte[] store = null;
        try{
                InputStream inStream = null;
                int totalBytesRead = 0;
                try{
                        inStream = new BufferedInputStream ( new FileInputStream(willBeRead));
                        while(totalBytesRead < FILE_SIZE)
                        {
                                BigInteger hash=BigInteger.valueOf(0);
                                String PART_NAME=NUMBER_OF_CHUNKS +".chunk";
                                int bytesRemaining = FILE_SIZE - totalBytesRead;
                                if(bytesRemaining <= min)
                                {
                                        store = new byte[bytesRemaining];
                                        totalBytesRead += inStream.read(store,0,bytesRemaining);
                                }
                                else
                                {
                                        int end=max;
                                        if(bytesRemaining > min && bytesRemaining <=max) end=bytesRemaining;
                                        temporary = new byte[end];
                                        int bytesRead = inStream.read(temporary,0,min);
                                        for(int i=0; i<end-min; i++)
                                        {
                                                byte[] b=new byte[1];
                                                bytesRead++;
                                                inStream.read(b,0,1);
                                                temporary[min+i]=b[0];
                                                int a=(int)b[0];
                                                a=a & 0xFF;
                                                // System.out.println(a);
                                                hash=hash.multiply(BigInteger.valueOf(2));
                                                hash=hash.add(BigInteger.valueOf(G[(int)a]));
                                                hash=hash.mod(modu);
                                                BigInteger temp;
                                                if(min+i <= normal)
                                                        temp=hash.and(BigInteger.valueOf(maskS));
                                                else
                                                        temp=hash.and(BigInteger.valueOf(maskL));
                                                // System.out.println(temp);
                                                if(temp.equals(BigInteger.valueOf(0)))
                                                {
                                                        count++;
                                                        store= new byte[min+i+1];
                                                        for(int j=0; j<=min+i; j++)
                                                                store[j]=temporary[j];
                                                        break;
                                                }
                                        }
                                        if(bytesRead==end)
                                        {store=temporary; System.out.println(NUMBER_OF_CHUNKS); }
                                        if(bytesRead > 0 )
                                        {
                                                totalBytesRead += bytesRead;
                                                NUMBER_OF_CHUNKS++;
                                        }
                                }

                                if(store.length==max)
                                {
                                        NUMBER_OF_CHUNKS--;
                                        byte [] temp=new byte[normal];
                                        for(int i=0; i<(max/normal); i++)
                                        {
                                                PART_NAME=NUMBER_OF_CHUNKS +".chunk";
                                                for(int j=0; j<normal; j++)
                                                        temp[j]=store[i*normal + j];
                                                write(temp,destination +"/" + PART_NAME);
                                                nameList.add(destination+"/"+PART_NAME);
                                                NUMBER_OF_CHUNKS++;
                                        }
                                }
                                else
                                {
                                        write(store,destination +"/" + PART_NAME);
                                        nameList.add(destination+"/"+PART_NAME);
                                }
                                //System.out.println("Total Chunks : " + NUMBER_OF_CHUNKS);

                        }
                        System.out.println(""+NUMBER_OF_CHUNKS + " "+ count);
                }
                finally {
                        inStream.close();
                }
        }catch(IOException ex) {
                ex.printStackTrace();
        }
        return nameList;
}


public void mergeParts(ArrayList<String> nameList, String source, String DESTIONATION_PATH ){
        File[] file = new File[nameList.size()];
        byte AllFilesContent[] = null;
        int TOTAL_SIZE = 0;
        int FILE_NUMBER = nameList.size();
        int FILE_LENGTH = 0;
        int CURRENT_LENGTH = 0;
        for(int i = 0; i < FILE_NUMBER; i++) {
                file[i] = new File(source+nameList.get(i));
                TOTAL_SIZE += file[i].length();
        }
        try{
                AllFilesContent = new byte[TOTAL_SIZE];
                InputStream inStream = null;
                for(int j = 0; j < FILE_NUMBER; j++) {
                        inStream = new BufferedInputStream(new FileInputStream(file[j]));
                        FILE_LENGTH = (int)file[j].length();
                        inStream.read(AllFilesContent,CURRENT_LENGTH,FILE_LENGTH);
                        CURRENT_LENGTH += FILE_LENGTH;
                        inStream.close();
                }
        }catch(FileNotFoundException e) {
                System.out.println("File Not Found "  +e );
        }catch(IOException e) {
                System.out.println("Exception while reading the file " + e);
        } finally {
                write(AllFilesContent,DESTIONATION_PATH);
        }
        //System.out.println("Merge was executed successfully");
}

public static void main(String args[]){

        /*
           Command for execution
           1) java ByteMergeAndSplit split [source-file] [destination-folder(without / at end)]
           2) java ByteMergeAndSplit merge [source-folder] [path+newfilename] [list of pieces in order]
         */

        ArrayList<String> nameList = new ArrayList<String>();
        try{
                if(args[0].equals("split"))
                {
                        FastCDC obj = new FastCDC(48,65536,2048,8192);
                        nameList = obj.readAndFragment(args[1],args[2]);
                        //obj.mergeParts(nameList,"","F:\\High\\TestFile4.WEBM");

                }else{
                        ArrayList<String> nameList2 = new ArrayList<String>();
                        FastCDC obj=new FastCDC(0, 0, 0, 0);
                        for(int i = 3; i < args.length; i++)
                                nameList2.add(args[i]);
                        obj.mergeParts(nameList2,args[1],args[2]);
                }
        }catch(IOException ioe) {
                System.out.println("Exception while reading the file " + ioe);
        }

}
}
