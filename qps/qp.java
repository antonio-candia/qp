import java.io.*;
import java.util.*;


public class qp{
  //defines needed
  static String algorithms[] = {"crypt", "md5"};
  static int CRYPT=0;
  static int MD5=1;
  
  public qp(){
	System.out.println("Initializing...");
	return;
  }
   
  static String pwdIsInCache(String hash, int type){
    String cacheFilename = "cache.qp";
    try {
      RandomAccessFile cache = new RandomAccessFile(cacheFilename, "rw");
      String line;
      while((line=cache.readLine()) != null)
        if(algorithms[type].equals(new String(line.substring(0,line.indexOf('\t')))))
          if(hash.equals(line.substring(line.indexOf('\t')+1, line.lastIndexOf('\t'))))
	    return line.substring(line.lastIndexOf('\t')+1, line.length());

      cache.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
    return null;
  }

  static void cachePwdFound(String passwd, String hash, int type){
    String cacheFilename = "cache.qp";
    try {
      RandomAccessFile cache = new RandomAccessFile(cacheFilename, "rw");
      cache.seek(cache.length());
      cache.writeBytes(algorithms[type]+'\t'+hash+'\t'+passwd+'\n');
      cache.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
  }

  static void saveState(int type, String hash, String rule){
    String stateFilename = "state.qp";
    try {
	RandomAccessFile state = new RandomAccessFile(stateFilename, "rw");
	if (state.length() != 0){
    		String tempStateFilename = "state.tmp";
		RandomAccessFile statetmp = new RandomAccessFile(tempStateFilename, "rw");
		String line;
		int flag = 0;
		while((line=state.readLine()) != null)
			if(algorithms[type].equals(new String(line.substring(0,line.indexOf('\t')))))
				if(hash.equals(line.substring(line.indexOf('\t')+1, line.lastIndexOf('\t')))){
				//se já houve um "checkpoint" anterior já existe uma linha no arquivo de estado 
				//para esta senha, então somente deve ser atualizada.
				statetmp.writeBytes(algorithms[type]+'\t'+hash+'\t'+rule+"\n");
				flag = 1;
				}
			else
				statetmp.writeBytes(line+"\n");
		if(flag == 0)
	 		statetmp.writeBytes(algorithms[type]+'\t'+hash+'\t'+rule+"\n");
		state.close();
		statetmp.close();
		File stateorg = new File(stateFilename);
		File statenew = new File(tempStateFilename);
		stateorg.delete();
		statenew.renameTo(stateorg);
	}
	else {
		state.writeBytes(algorithms[type]+'\t'+hash+'\t'+rule+"\n");
		state.close();
	}
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
  }

  static String loadState(int type, String hash){
    String stateFilename = "state.qp";
    try {
	RandomAccessFile state = new RandomAccessFile(stateFilename, "rw");
	if (state.length() != 0){
		String line;
		while((line=state.readLine()) != null)
			if(algorithms[type].equals(new String(line.substring(0,line.indexOf('\t')))))
				if(hash.equals(line.substring(line.indexOf('\t')+1, line.lastIndexOf('\t')))){
				//se já houve um "checkpoint" anterior já existe uma linha no arquivo de estado 
				//para esta senha, então a ultima regra usada deve ser retornada
				state.close();
				return line.substring(line.lastIndexOf('\t'), line.length()).trim();
			}
	}
	state.close();
    } catch (IOException e) {
      System.err.println("Problem: " + e);
    } 
    return null;
  }


/*
  void multipleDictFilesRead(void) {
    File myDir = new File("dicts/"); // or any required directory pathname
    File[] files = myDir.listFiles();
    // can also use a FileFilter
    for (int i=0 ; i < files.length ; i++) {
      try {
      FileInputStream in = new FileInputStream(files[i]);
      // Do here your reading and handling
      in.close();
      }
      catch (IOException e)
      {
        System.err.println("Could not read file: " + files[i]);
        e.printStackTrace();
      }
    }
  }
*/

}
