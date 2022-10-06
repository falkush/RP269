import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

//Script to find primitive matrices

public class find-primitive {
	//2, 3, 5, 7, 13, 17, 19, 31, 61, 89, 107, 127, 521, 607, 1279, 2203, 2281, 3217
	 //mersenne
	static int[][] matA;
	static int[][] matB;
	static int[][] matC;
	
	static int n=269;
	
	//static double[] div = {};
	//static double[] div = {9,21}; //n=6
	//static double[] div = {15,51,85}; //n=8
	//static double[] div = {7,73}; //n=9
	//static double[] div = {33,93,341}; //n=10
	//static double[] div = {23,89}; //n=11
	//static double[] div = {1365,819,585,315}; //n=12
	
	static String[] div= {"13822297","68625988504811774259364670661552948915363901845035416371912463477873783063"};
	
	//static String[] div= {"3","5","17","257","641","65537","274177","6700417","67280421310721","59649589127497217","5704689200685129054721"};
	//static String[] div= {};
	
	
	static int nbdiv=div.length;
	
	static BigInteger[] divbig = new BigInteger[nbdiv];
	static BigInteger[] divbigtrue = new BigInteger[nbdiv];
	static Random rand = new SecureRandom();
	
	
	
	public static void main(String args[]) throws IOException
	{
		
		int[][] mat = new int[n][n];
		int[][] mat3 = new int[n][n];
		int[][] mat2 = new int[n][n];
		int[][] mat4= new int[n][n];
		boolean[] test = new boolean[n];
		boolean[] h = new boolean[n];
		int[] test2;
		int[] test3;
		boolean[] compare = new boolean[n];
		//boolean[] stats = new boolean[(int)Math.pow(2, n)+1];
		
		boolean[] v1= new boolean[n];
		boolean[] v2= new boolean[n];
		boolean[] v3= new boolean[n];
		boolean[] v4= new boolean[n];
		boolean[] v5= new boolean[n];
		boolean[] v6= new boolean[n];
		boolean[] t= new boolean[n];
		boolean[] tx= new boolean[n];
		boolean[] one = new boolean[n];
		boolean[][] matbool = new boolean[n][n];
		boolean[][] matbool1 = new boolean[n][n];
		one[n-1]=true;
		

		int trackcount=0;
		int nb;
		

		int trackcountc=0;
		long countc=0;
		long totcountc=0;
		
		int[] histo = new int[5000];
		int[] histoc = new int[5000];
		
		String hash;

		compare[0]=true;
		test[0]=true;
		int i,j,tmp;
		long count=0;
		long sum=0;
		long count2=0;
		long totcount=0;
		int ord;
		
		double mu;
		double var=0;
		
		double muc;
		double varc=0;
		
		nb=1000000;
		ord=0;
		
		try {
			rand=SecureRandom.getInstance("Windows-PRNG");
		} catch (NoSuchAlgorithmException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		for(i=0;i<nbdiv;i++)
		{
			divbig[i]=new BigInteger(div[i]);
			divbigtrue[i]=new BigInteger("1");
		}
		
		for(i=0;i<nbdiv;i++)
		{
			for(j=0;j<nbdiv;j++) if(i!=j) divbigtrue[i]=divbigtrue[i].multiply(divbig[j]);
		}
		
		
		matbool1=randmat();
		while(!isPrim(matbool1)) {System.out.println(ord);matbool1=randmat();ord++;}
		writemat(matbool1,"C:\\test\\falk.dat");
		System.exit(0);

	
		//matbool=readmat("C:\\test\\A.dat");
		//matA=matidx(matbool);

		for(tmp=1;tmp<=nb;tmp++)
		{
			matbool1=randmat();
			while(!isPrim(matbool1)) matbool1=randmat();
			matA=matidx(matbool1);
			
			matbool=randmat();
			while(!isPrim(matbool) || compmat(matbool1,matbool)) matbool=randmat();
			matB=matidx(matbool);
			
			
			count=0;
			countc=0;
			v1 = new boolean[n];
		
			for(i=1;i<=Math.pow(2, n-ord);i++)
			{
			
				v2=derivative(v1,ord);
				
				
				
				v2=randvecfast();
				
	
				
				for(j=1;j<=Math.pow(2, ord);j++) increase(v1);
			}

			/*
			for(i=1;i<=Math.pow(2, n);i++)
			{
			
				v2=derivative2(v1,ord);
				stats[bintodec2(v2)]++;
				increase(v1);
			}
			*/
		
			totcount+=count;
			totcountc+=countc;
			
			histo[(int)Math.floor((double)count/(double)10)]++;
			histoc[(int)Math.floor((double)countc/(double)10)]++;
		
			System.out.println(tmp);
		}
		
		mu=(double)totcount/(double)nb;
		
		muc=(double)totcountc/(double)nb;
	
		
		System.out.println("observed:"+mu);
		System.out.println("expected:"+expected(ord));
		System.out.println("ratio:"+((double)totcount/(double)nb)/expected(ord));
		System.out.println("std:"+Math.sqrt(var));
		
		
		System.out.println(n+" & "+ mu + " & "+ expected(ord) +" & " + ((double)totcount/(double)nb)/expected(ord) + " & " + Math.sqrt(var)+ " & " + Math.sqrt(varc) +" \\\\" );

		for(i=0;i<400;i++)
		{
			System.out.print("(" + 10*i + ", " + histo[i] + ") ");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("");
		
		for(i=0;i<400;i++)
		{
			System.out.print("(" + 10*i + ", " + histoc[i] + ") ");
		}
		//finding a primitive matrix
		/*
		for(i=1;i<10000;i++)
		{
			mat=randmat();
				
			if(isPrim(mat))
			{
				writemat(mat,"C:\\test\\test.dat");
				System.out.println("found");
				System.exit(0);
			}
			System.out.println(i);
		}	
		*/
		
		//computing hash
		//hash=hashtohex(compHash2("C:\\test\\RMCrypto2021.zip"));
		//System.out.println("hash2:"+hash);
    }
	
	public static double expected(int m)
	{
		double n1=Math.pow(2,n);
		double m1=Math.pow(2, n-m);
		int i;
		double calc=1;
		
		for(i=1;i<=m1;i++)
		{
			calc*=(double)1-(double)1/n1;
		}
		
		calc*=n1;
		
		return n1-calc;
	}
	
	
	public static boolean[] derivative(boolean[] vec, int m)
	{
		if(m==0) return oneway2(vec);
		else 
		{
			boolean[] delta = new boolean[n];
			delta[m-1]=true;
			return xor(derivative(xor(vec,delta),m-1),derivative(vec,m-1));
		}
	}
	
	public static boolean[] derivative2(boolean[] vec, int m)
	{
		if(m==0) return oneway2(vec);
		else 
		{
			boolean[] delta = new boolean[n];
			delta[m-1]=true;
			return addmod(derivative(xor(vec,delta),m-1),derivative(vec,m-1));
		}
	}
	
	public static int[] diff(boolean[] vec1, boolean[] vec2)
	{
		int ind=0;
		int i;
		int[] ret = new int[n+1];
		
		for(i=0;i<n;i++)
		{
			if(vec1[i]!=vec2[i])
			{
				ret[ind]=i;
				ind++;
			}
		}
		ret[ind]=-1;
		return ret;
	}
	
	
	public static int diffcount(boolean[] vec1, boolean[] vec2)
	{
		int ind=0;
		int i;
		
		for(i=0;i<n;i++)
		{
			if(vec1[i]!=vec2[i])
			{
				ind++;
			}
		}
		return ind;
	}
	
	public static boolean[][] vectomat(boolean[] vec)
	{
		boolean[][] ret = new boolean[n][n];
		int i,j;
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				ret[j][i]=vec[(j+i)%n];
			}
		}
		return ret;
	}
	
	public static boolean[] vecmult(boolean[] v1, boolean[] v2)
	{
		boolean[] ret = new boolean[n];
		boolean[] tmp = new boolean[n];
		
		int i,j;
		
		for(i=0;i<n;i++)
		{
			if(v1[i])
			{
				for(j=0;j<n;j++) tmp[j]=v2[(j-i-1+n)%n];
				ret=addmod(ret,tmp);
			}
		}
		
		return ret;
	}
	
	public static boolean[] oneway(boolean[] vec)
	{
		return addition(matact(matA,vec),vec);
		//return vecmult(matact(matA,vec),vec); //OG
		//return vecmult(matact(matA,vecmult(matact(matA,vec),vec)),vec); //two-round
		//return vecmult(vecmult(matact(matA,vec),matact(matB,vec)),vec); //two-mat
	}
	
	public static boolean[] oneway2(boolean[] vec)
	{
		boolean[] ret = new boolean[n];
		
		ret=Arrays.copyOf(vec, vec.length);
		int i;
		
		for(i=0;i<n;i++) if(vec[i]) ret=matact(matA,ret); else ret=matact(matB,ret);
		
		return ret;
	}
	
	
	public static boolean[][] getInv(boolean[][] mat)
	{
		int i,j,k;
		boolean copy[][]=new boolean[n][n];
		boolean inv[][] = new boolean[n][n];
		
		
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++) copy[i][j]=mat[i][j];
			inv[i][i]=true;
		}
	
		for(i=0;i<n;i++)
		{
			j=i;
			while(j<n && !copy[i][j]) j++;
			
			if(j==n) {System.out.println(i);
			for(k=0;k<n;k++) inv[k][0]=false;
			return inv;} //if not invertible, fill first row with zeros
			
			if(j!=i)
			{
				for(k=i;k<n;k++) { copy[k][i]^=copy[k][j];
				inv[k][i]^=inv[k][j];}
			}
			
			for(j=i+1;j<n;j++) if(copy[i][j]) for(k=i;k<n;k++) {copy[k][j]^=copy[k][i]; inv[k][j]^=inv[k][i];}
		}
		
		
		for(i=1;i<n;i++)
		{
			for(j=0;j<i;j++)
			{
				if(copy[i][j]) for(k=0;k<n;k++) {copy[k][j]^=copy[k][i]; inv[k][j]^=inv[k][i];}
			}
		}
	
		return inv;
	}
	
	
	public static boolean[] compHash(String path) throws IOException
	{
		boolean[] ret = new boolean[n];
		boolean[] fin = new boolean[n];
		boolean[] mi = new boolean[n];
		boolean[] byt = new boolean[8];
		InputStream os = new FileInputStream(path);
		int size=(int) new File(path).length();
		int tmp2,i,tmp,j,tmp3;
		tmp2=size;
		
		ret[0]=true;
		
		fin=matact(matB,ret);
		ret=matact(matA,ret);
		
		tmp3=0;
		for(i=1;i<=size;i++)
		{
			byt=dectobin(os.read());
			for(j=0;j<8;j++)
			{
				mi[tmp3]=byt[j];
				tmp3++;
				if(tmp3==n)
				{
					tmp3=0;
					ret=addplus(addplus(matact(matA,addplus(ret,mi)),matact(matB,addplus(not(ret),mi))),not(mi));
				}
			}
		}
		
		if(tmp3!=0)
		{
			mi[tmp3]=true;
			tmp3++;
			for(i=tmp3;i<n;i++) mi[i]=false;
			ret=addplus(addplus(matact(matA,addplus(ret,mi)),matact(matB,addplus(not(ret),mi))),not(mi));
		}
		
		ret=addplus(addplus(matact(matA,addplus(ret,fin)),matact(matB,addplus(not(ret),fin))),not(fin));
		
		return ret;
	}
	
	public static String hashtohex(boolean[] hash)
	{
		int i,j;
		boolean[] tmp = new boolean[8];
		String ret="";
		String tmp2;
		
		for(i=0;i<64;i++)
		{
			for(j=0;j<8;j++)
			{
				tmp[j]=hash[8*i+j];
			}
			tmp2=Integer.toHexString(bintodec(tmp));
			if(tmp2.length()==1) tmp2="0"+tmp2;
			ret+=tmp2;
		}
		
		return ret;
	}
	
	public static boolean[] addition(boolean[] a, boolean[] b)
	{
		boolean[] ret = new boolean[n];
		int i;
		boolean tmp=false;
		
		for(i=n-1;i>=0;i--)
		{
			if(tmp)
			{
				if(a[i]^b[i])
				{
					ret[i]=false;
				}
				else if(a[i] && b[i])
				{
					ret[i]=true;
				}
				else
				{
					ret[i]=true;
					tmp=false;
				}
			}
			else
			{
				
				if(a[i]^b[i])
				{
					ret[i]=true;
				}
				else if(a[i] && b[i])
				{
					ret[i]=false;
					tmp=true;
				}
				else
				{
					ret[i]=false;
				}
			}
		}
		
		return ret;
	}
	
	public static boolean[][] randmat()
	{
		int i,j,k;
		boolean[][] ret = new boolean[n][n];
		
		for(i=0;i<n;i++) ret[i][i]=true;
		
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				if(i!=j && rand.nextInt(2)==0)
				{
					for(k=0;k<n;k++) ret[k][j]^=ret[k][i];
				}
			}
		}
		
		return ret;
	}
	
	public static boolean[] randvec()
	{
		boolean[] ret = new boolean[n];
		int i;
	
		for(i=0;i<n;i++)
		{
			if(rand.nextInt(2)==0) ret[i]=false;
			else ret[i]=true;
		}
		
		return ret;
	}
	
	public static boolean[] randvecfast()
	{
		Random rand = new Random();
		boolean[] ret = new boolean[n];
		int i;
		
		for(i=0;i<n;i++)
		{
			if(rand.nextInt(2)==0) ret[i]=false;
			else ret[i]=true;
		}
		
		return ret;
	}
	
	public static void writemat(boolean[][] mat, String path) throws IOException
	{
		OutputStream os = new FileOutputStream(path);
		int i,j,tmp=0;
		boolean[] vec=new boolean[8];
		
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				vec[tmp]=mat[j][i];
				tmp++;
				if(tmp==8)
				{
					tmp=0;
					os.write(bintodec(vec));
				}
			}
		}
		
		if(tmp!=0)
		{
			for(i=tmp;i<=7;i++) vec[i]=false;
			os.write(bintodec(vec));
		}
		os.close();
	}
	
	public static int[][] matidx(boolean[][] mat) //(optimization, stores the positions of the 1 of the matrix for each lines, ret[0][i] is number of 1 in line i)
	{
		int[][] ret = new int[n+1][n+1];
		int i,j,tmp;
		
		for(i=0;i<n;i++)
		{
			tmp=1;
			for(j=0;j<n;j++)
			{
				if(mat[j][i])
				{
					ret[tmp][i]=j;
					tmp++;
				}
			}
			ret[0][i]=tmp-1;
		}
		
		return ret;
	}
	
	public static boolean[] addplus(boolean[] a, boolean[] b)
	{
		boolean[] ret = new boolean[n];
		int i;
		boolean tmp=false;
		boolean tmp2=false;
		
		for(i=n-1;i>=0;i--)
		{
			if(tmp)
			{
				if(tmp2)
				{
					if(a[i])
					{
						if(!b[i])
						{
							ret[i]=true;
							tmp2=false;
						}
					}
					else
					{
						if(b[i])
						{
							ret[i]=true;
						}
						else
						{
							tmp=false;
						}
					}
				}
				else
				{
					if(a[i])
					{
						if(b[i])
						{
							ret[i]=true;
						}
					}
					else
					{
						if(b[i])
						{
							tmp2=true;
						}
						else
						{
							ret[i]=true;
							tmp=false;
						}
					}
				}
			}
			else
			{
				if(tmp2)
				{
					if(a[i])
					{
						if(b[i])
						{
							ret[i]=true;
							tmp=true;
						}
						else
						{
							tmp2=false;
						}
					}
					else
					{
						if(!b[i])
						{
							ret[i]=true;
						}
					}
				}
				else
				{
					if(a[i])
					{
						if(b[i])
						{
							tmp=true;
						}
						else
						{
							ret[i]=true;
						}
					}
					else
					{
						if(b[i])
						{
							ret[i]=true;
							tmp2=true;
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	public static boolean[] addmod(boolean[] a, boolean[] b)
	{
		boolean[] ret = new boolean[n];
		int i;
		boolean tmp=false;
		
		for(i=n-1;i>=0;i--)
		{
			if(tmp)
			{
				if(a[i]^b[i])
				{
					ret[i]=false;
				}
				else if(a[i] && b[i])
				{
					ret[i]=true;
				}
				else
				{
					ret[i]=true;
					tmp=false;
				}
			}
			else
			{
				
				if(a[i]^b[i])
				{
					ret[i]=true;
				}
				else if(a[i] && b[i])
				{
					ret[i]=false;
					tmp=true;
				}
				else
				{
					ret[i]=false;
				}
			}
		}
		
		if(tmp)
		{
			boolean[] one = new boolean[n];
			one[n-1]=true;
			
			return addmod(ret,one);
		}
		return ret;
	}
	
	public static boolean[] xor(boolean[] a, boolean b[])
	{
		boolean[] ret = new boolean[n];
		int i;
		
		for(i=0;i<n;i++) ret[i]=a[i]^b[i];
		
		return ret;
	}
	
	public static boolean[] not(boolean[] a)
	{
		boolean[] ret = new boolean[n];
		int i;
		
		for(i=0;i<n;i++) ret[i]=!a[i];
		
		return ret;
	}
	
	public static boolean[][] readmat(String path) throws IOException
	{
		InputStream os = new FileInputStream(path);
		boolean[] vec = new boolean[8];
		int i=0,j=0,k;
		boolean[][] ret = new boolean[n][n];
		
		while(true)
		{
			vec=dectobin(os.read());
			for(k=0;k<8;k++)
			{
				ret[i][j]=vec[k];
				j++;
				if(j==n)
				{
					j=0;
					i++;
					if(i==n)
					{
						os.close();
						return ret;
					}
				}
			}
		}
	}
	
	public static void increase(boolean[] vec)
	{
		int i=n-1;
		
		while(i>=0 && vec[i])
		{
			vec[i]=false;
			i--;
		}
		if(i>=0) vec[i]=true;
	}
	
	
	public static int bintodec(boolean[] vec) //for hex output
	{
		int ret=0;
		int mult=1;
		int i;
		
		for(i=7;i>=0;i--)
		{
			if(vec[i]) ret+=mult;
			mult*=2;
		}
		return ret;
	}
	
	public static int bintodec2(boolean[] vec)
	{
		int ret=0;
		int mult=1;
		int i;
		
		for(i=n-1;i>=0;i--)
		{
			if(vec[i]) ret+=mult;
			mult*=2;
		}
		return ret;
	}
	
	public static boolean[] dectobin(int byt)
	{
		boolean[] ret = new boolean[8];
		int i;
		int tmp=byt;
		
		for(i=7;i>=0;i--)
		{
			tmp=byt%2;
			if(tmp==1) ret[i]=true;
			byt-=tmp;
			byt/=2;
		}
		
		return ret;
	}
	
	public static boolean[] dectobin2(int byt)
	{
		boolean[] ret = new boolean[n];
		int i;
		int tmp=byt;
		
		for(i=n-1;i>=0;i--)
		{
			tmp=byt%2;
			if(tmp==1) ret[i]=true;
			byt-=tmp;
			byt/=2;
		}
		
		return ret;
	}
	
	public static boolean[][] matmultbool(boolean[][] mat1, boolean[][] mat2)
	{
		boolean[][] ret = new boolean[n][n];
		int i,j,k;
		
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				for(k=0;k<n;k++) if(mat1[k][j] && mat2[i][k]) ret[i][j]=!ret[i][j];
			}
		}
		
		return ret;
	}
	
	public static boolean isPrim(boolean[][] mat)
	{
		boolean[][] tmp;
		boolean[][][] mats = new boolean[n+1][n][n];
		boolean[][] id = new boolean[n][n];
		int i,j,k;
		BigInteger tmp2;
		
		for(i=0;i<n;i++) id[i][i]=true;
		
		mats[0]=mat;
		mats[1]=matmultbool(mat,mat);
		
		for(i=1;i<n;i++)
		{
			mats[i+1]=matmultbool(mats[i],mats[i]);
		}

		if(compmat(mats[n],mat))
		{
			System.out.print("hype:");
			for(i=0;i<nbdiv;i++)
			{
				
				System.out.print(i+",");
				tmp2=divbigtrue[i];

				tmp=new boolean[n][n];
				for(j=0;j<n;j++) tmp[j][j]=true;
				k=0;

				while(!tmp2.equals(BigInteger.valueOf(0)))
				{
					if(tmp2.mod(BigInteger.valueOf(2)).equals(BigInteger.valueOf(0)))
					{
						tmp2=tmp2.divide(BigInteger.valueOf(2));
					}
					else
					{
						tmp2=tmp2.subtract(BigInteger.valueOf(1));
						tmp2=tmp2.divide(BigInteger.valueOf(2));
						tmp=matmultbool(tmp,mats[k]);
					}
					k++;
				}
				
				if(compmat(tmp,id)) return false;
				
			}
			
			return true;
		}
		
		return false;
	}
	
	public static boolean compmat(boolean[][] mat1, boolean[][] mat2)
	{
		int i,j;
		
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++) if(mat1[i][j]!=mat2[i][j]) return false;
		}
		return true;
	}
	
	public static int compvec(boolean[] vec1, boolean[] vec2)
	{
		int i;
		int count=0;
		for(i=0;i<n;i++)
		{
			if(vec1[i]!=vec2[i]) count++;
		}
		return count;
	}
	
	public static void display(boolean[][] mat)
	{
		int i,j;
		
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				if(mat[j][i]) System.out.print("1 ");
				else System.out.print("0 ");
			}
			System.out.println("");
		}
		System.out.println("");
	}
	
	
	public static boolean[] matact(int[][] mat, boolean[] vec)
	{
		int i,j;
		boolean[] ret = new boolean[n];
		
		for(i=0;i<n;i++)
		{
			for(j=1;j<=mat[0][i];j++)
			{
				if(vec[mat[j][i]]) ret[i]=!ret[i];
			}
		}
		
		return ret;
	}
	
	
	public static void displayvec(boolean[] vec)
	{
		int i;
		
		for(i=0;i<vec.length;i++)
		{
			if(vec[i]) System.out.print("1");
			else System.out.print("0");
		}
		System.out.println("");
	}
	
	public static int[][] matv(boolean[] vec)
	{
		boolean[][] ret0=new boolean[n][n];
		int i,j;
		
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				ret0[j][i]=vec[(j+i)%n];
			}
		}
		
		return matidx(ret0);
	}
	
}

	