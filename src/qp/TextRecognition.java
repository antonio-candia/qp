package qp;

import java.lang.*;
import java.io.*;
/**
 * @author candia
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TextRecognition {
    private static byte a,b;
    private static int digraphCount;

    public TextRecognition(){
    }
    
    static float[][] FrequencyCount(String text){
        int i, j, l=text.length();
        float freqTable[][]=new float[256][256];
        byte textBytes[] = new byte[l];
        textBytes=text.getBytes();

        // encontrando frequencias absolutas (contagem)
        for(i=1;i<l;i++)
            freqTable[textBytes[i-1]][textBytes[i]]++;
        return freqTable;
    }

    static float[][] FileFrequencyCount(String filename){
        float freqTable[][]=new float[256][256];
        try{
            File f = new File(filename);
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            String line;
            int i,j,llen;
	    
	    digraphCount = 0;
            while((line = bf.readLine()) != null){
                llen=line.length();
                byte[] lineBytes = new byte[llen];
                lineBytes = line.getBytes();
                for(i=1;i<llen;i++){
                    freqTable[lineBytes[i-1]][lineBytes[i]]++;
		    digraphCount++;
		}
            }
            //calculando probabilidades
//            float flen = f.length();
            float flen = (float)digraphCount;
            for(i=0; i<256; i++)
                for(j=0; j<256; j++)
                    freqTable[i][j] /= flen;
            //mostrando maior frequencia encontrada
            int a=0, b=0;
            float maior=-1000000;
            for(i=0; i<256; i++)
                for(j=0; j<256; j++)
                    if(maior<freqTable[i][j]){
                        maior = freqTable[i][j];
                        a=i;
                        b=j;
                    }
            System.out.println("a: "+a+" b: "+b+" freq: "+maior+" digraphCount: "+digraphCount);
	    // conferindo para ver se a tabela esta correta, se estiver
	    // a soma dos elementos tem que ser 1. (ou quase)
	    double suma=0.0;
	    for (i=0;  i<256; i++)  {
			for (j=0; j<256;j++) {
				suma= suma + freqTable[i][j];
			}
		}
	    System.out.println ("Soma das frequencias da tabela: "+suma);
            return freqTable;
        } catch(IOException e){
            System.err.println("FileFrequencyCount: " + e);
        }
        return null;
    }

// Calculo do logaritmo da verossimilhanca
    double logLikelihood(float[][] freqTabRef, float[][] freqTab, int len){
        double S2=0;
        int i,j;
/*
        // calculo das probabilidades
        float freqTable[][] = new float[256][256];
        for(i=0; i<256; i++)
            for(j=0; j<256; j++)
                if(freqTab[i][j] != 0){
                    freqTable[i][j] = (freqTabRef[i][j] / (len-1));
                    System.out.println(i+":"+j+" : "+freqTable[i][j]);
                }
*/
       // calculo da estatistica de Sinkov (S2) (log-likelihood)
       for(i=0; i<256; i++)
            for(j=0; j<256; j++)
                if(freqTabRef[i][j] != 0){
                    S2 += (freqTab[i][j] * (float) Math.log(freqTabRef[i][j]));
//                    System.out.println(i+":"+j+" : "+freqTable[i][j]);
                }
       return S2;
    }            


// Calculo da entropia
    static double entropy(float[][] freqTab){
	double entropy=0.0;
	int a,b;
	for (a=0;  a<256; a++)  {
		for (b=0; b<256;b++) {
			if (freqTab[a][b]!=0) 
				entropy=entropy-(freqTab[a][b]*(Math.log(freqTab[a][b])));
		}
	}
	return entropy;
}

// Calculo da entropia(log 2)

    static double entropyLog2(float[][] freqTab) {
	double entropy=0.0;
	int a,b;
	for (a=0;  a<256; a++)  {
		for (b=0; b<256;b++) {
			if (freqTab[a][b]!=0) 
				entropy = entropy - (freqTab[a][b] * (Math.log(freqTab[a][b]) / Math.log(2.0)));
		}
	}
	return entropy;
}


// Calculo da inercia
    static double inertia(float[][] freqTab) {
	double inertia=0.0;
	int a,b;
	for (a=0;  a<256; a++)  {
		for (b=0; b<256;b++) {
			if (freqTab[a][b]!=0) 
				inertia=inertia+((a-b)*(a-b)*freqTab[a][b]);
		}
	}
	return inertia;
}

// Calculo da energia
    static double energy(float[][] freqTab) {
	double energy=0.0;
	int a,b;
	for (a=0;  a<256; a++)  {
		for (b=0; b<256;b++) {
			if (freqTab[a][b]!=0) 
				energy=energy+(freqTab[a][b] * freqTab[a][b]);
		}
	}
	return energy;
}

// calculo da homogeneidade
    static double homogeneity(float[][] freqTab) {
	double homogeneity=0.0;
	int a,b;
	for (a=0;  a<256; a++)  {
		for (b=0; b<256;b++) {
			homogeneity=homogeneity+ (freqTab[a][b]/(1+Math.abs(a-b)));
		}
	}
	return homogeneity;
}

//Calculo da correlacao
    static double correlation(float[][] freqTab) {
	//First step in the calculations will be to calculate px [] and py []
	double correlation=0.0;
	double stdevx=0.0;
	double stdevy=0.0;
	double [] pxi = new double [256];
	double [] pyj = new double [256];
	int i, j, a, b;
	double mx=0.0, my=0.0;

	// calculo de Px(i) e Py(j)
	for (a=0; a<256;a++){
		pxi[a]=0;  
       		pyj[a]=0;
	}
	for(i=0; i<256; i++)
		for(j=0; j<256; j++){
			pxi[i] += freqTab[i][j];
		}
	for(j=0; j<256; j++)
		for(i=0; i<256; i++){
			pyj[j] += freqTab[i][j];
		}
	// calculo de mx e my
	for(a=0; a<256; a++){
		mx += (a*pxi[a]);
		my += (a*pyj[a]);
	}
	// calculo de stdevx e stdevy
	stdevx=stdevy=0.0;
	for(a=0; a<256; a++){
		stdevx += ((a-mx)*(a-mx)*pxi[a]);
		stdevy += ((a-my)*(a-my)*pyj[a]);
	}
	// calculo final
	double sqstdev = 0.0;
	sqstdev = Math.sqrt(stdevx*stdevy);
	correlation = 0.0;
	for(i=0; i<256; i++)
		for(j=0; j<256; j++){
			correlation += ((((i-mx)*(j-my))/sqstdev)*freqTab[i][j]);
		}
	correlation = -correlation;
	return correlation;
}


// metodos para manipulacao da tabela de frequencia de digrafos
    public void SaveFrequencyTable(float[][] freqTab){
        try{
            FileOutputStream fos = new FileOutputStream("freqtab");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(freqTab);
        } catch(IOException ioe){
            System.err.println("SaveFrequencyTable: cannot open output stream");
            System.exit(-1);
        } 
    }

    public float[][] LoadFrequencyTable(String freqtabname){
        float freqTab[][] = new float[256][256];
        try{
            FileInputStream fis = new FileInputStream(freqtabname);
            ObjectInputStream ois = new ObjectInputStream(fis);
            freqTab = (float[][]) ois.readObject();
        } catch(IOException ioe){
            System.err.println("LoadFrequencyTable: cannot open input stream");
            System.exit(-1);
        } catch(ClassNotFoundException cnfe){
            System.err.println("LoadFrequencyTable: cannot load frequency table");
            System.exit(-1);
        }
        return freqTab;
    }

    static float GetMaior(float[][] ft){
        int i,j, a=0, b=0;
        float maior=-1000000;
        for(i=0; i<256; i++)
            for(j=0; j<256; j++)
                if(maior<ft[i][j]){
                    maior = ft[i][j];
                    a=i;
                    b=j;
                }
        System.out.println("a: "+a+" b: "+b);
        return maior;
    }

    public static void main(String[] args) {
        TextRecognition fc = new TextRecognition();
        float freqTab[][] = new float[256][256];
        if(args[0].equals("-gerafreqtab")){
            freqTab=FileFrequencyCount(args[1]);
            fc.SaveFrequencyTable(freqTab);
        } else{
            if(args[0].equals("-string")){
                freqTab=FrequencyCount(args[1]);
                float freqTab2[][] = new float[256][256];
                freqTab2=fc.LoadFrequencyTable("freqtab");
                System.out.println(GetMaior(freqTab));
                double logl = fc.logLikelihood(freqTab2, freqTab, args[1].length());
                System.out.println("Log-likelihood: "+logl+" normalizado: "+(logl/(args[1].length()-1)));
		System.out.println("Entropia de freqtab: "+entropy(freqTab2));
		System.out.println("Entropia da string: "+entropy(freqTab));
		System.out.println("Entropia(log2) de freqtab: "+entropyLog2(freqTab2));
		System.out.println("Entropia(log2) da string: "+entropyLog2(freqTab));
		System.out.println("Inercia de freqtab: "+inertia(freqTab2));
		System.out.println("Inercia da string: "+inertia(freqTab));
		System.out.println("Energia de freqtab: "+energy(freqTab2));
		System.out.println("Energia da string: "+energy(freqTab));
		System.out.println("Homogeneidade de freqtab: "+homogeneity(freqTab2));
		System.out.println("Homogeneidade da string: "+homogeneity(freqTab));
		System.out.println("Correlacao de freqtab: "+correlation(freqTab2));
		System.out.println("Correlacao da string: "+correlation(freqTab));
            }
        }
        System.exit(0);
    }
}
