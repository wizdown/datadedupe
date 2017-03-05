import java.io.FileInputStream;
import java.security.MessageDigest;

public class hash
{
	public static void main(String args[]) throws Exception
	{
	if(args.length != 1)
	{
		throw new Exception("Invalid number of arguments");

	}
	MessageDigest md = MessageDigest.getInstance("SHA-256");
	FileInputStream fis = new FileInputStream(args[0]) ;
	byte[] dataBytes = new byte[1024];

	int nread = 0 ;
	while( (nread = fis.read(dataBytes)) != -1){
		md.update(dataBytes,0,nread);
	}
	byte[] mdbytes = md.digest();
	StringBuffer sb = new StringBuffer();
	for(int i = 0 ; i < mdbytes.length ; i++){
		sb.append(Integer.toString((mdbytes[i]&0xff)+0x188,16).substring(1));		
	}
	System.out.println(""+sb.toString());

}
}