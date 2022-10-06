//tools
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Base64;
public class rp269 {
	static int n=269;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//mode descriptions//
	//0:Diffie-Hellman private/public key pair generation
	//1:Diffie-Hellman shared key computation
	//2:Schnorr Signature of a hash
	//3:Verify Signature
	//4:Hash computation
	//5:Encrypt a file
	//6:Decrypt a file
	static int mode=0;
	
	//choice of CPRNG for key generation (Java Secure Random) 
	static String cprng="Windows-PRNG";
	
	//path to files
	static String path="C:\\test\\"; //dat files in this folder along file to encrypt/decrypt/compute hash
	static String file="test.png"; //file to encrypt/decrypt/compute hash ---- mode 4: do not include the ".dpe"
	
	//Mode 1 & 2 & 3
	static String privatekey = "PPX8oXHsMefurpFLDm2D3WxGWO6OyuZqMVbYm2UAhxm2Aw=="; //your private key (mode 1 & 2)
	static String publickey = "3sh7hbeW36ULKm1dfRPhw1c54+GWq5aJ5gxx047dTn4YBQ=="; //their public key (mode 1 & 3)
	
	static String hash = "79d7779e3f27c57b24ff20118b42f75d6c78f486b65285595050ec6a47d503f7"; //hash to sign or verify (256 bits, mode 2 & 3)
	static String signature = "e5a/98tt8BUQ50gzGbtBpXXwOufGbCsSie41V8KWIa0a2r3X6lb9O4CtJzwIU5ujn+WiLKfX+mCtUjVZ3U17SRh6fwE="; //signature to verify (mode 3)
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	static int[][] matA;
	static boolean[][] matAbool;
	static int[][] matB;
	static boolean[][] matBbool;
	static int[][] matC;
	static int[][][] matApow;
	static boolean[][][] DHpow;
	static boolean[][] DHinv;
	
	static Random rand;

	public static void main(String args[]) throws IOException
	{
		String hash0;
		String sign;
		boolean[] v0= new boolean[n];
		boolean[] v1= new boolean[n];
		boolean[] v2= new boolean[n];
		boolean[][] mattmp= new boolean[n][n];
		boolean valid;
		
		v0[0]=true;		
		
		try {
			rand=SecureRandom.getInstance(cprng);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("/// Mode " + mode +" ///");
		System.out.print("Loading matrices...");
		
		matApow=new int[n][n][n];
		DHpow=new boolean[n][n][n];
		DHinv=new boolean[n][n];
		matAbool=readmat(path+"A.dat");
		matA=matidx(matAbool);
		matBbool=readmat(path+"B.dat");
		matB=matidx(matBbool);
		
		if(mode==0 || mode==2 || mode==3) readmats(path+"Apow.dat");
		if(mode==1 || mode==3) 
		{
			DHinv=readmat(path+"DHinv.dat");
			readmatsDH(path+"DHpow.dat");
		}
		
		System.out.println("OK");
		System.out.print("Computing...");

		if(mode==0)
		{
			v1=randvec();
			v2=vecpow(v1,v0);
			
			System.out.println("OK");
			System.out.print("private:");
			System.out.println(bintob64(v1));
			System.out.print("public:");
			System.out.println(bintob64(v2));
		}
		else if(mode==1)
		{
			v1=b64tobin(privatekey);
			mattmp=matpowDH(recovermat(b64tobin(publickey)),v1);
			System.out.println("OK");
			System.out.print("shared:");
			System.out.println(bintob64(mattmp[0]));
		}
		else if(mode==2)
		{
			v1=vecpow(b64tobin(privatekey),v0);
			sign=signature();
			
			System.out.println("OK");
			System.out.println("public key:"+bintob64(v1));
			System.out.println("hash:"+hash);
			System.out.println("signature:"+sign);
		}
		else if(mode==3)
		{
			valid=verify();
			System.out.println("OK");
			System.out.println("signature valid:"+valid);
		}
		else if(mode==4)
		{
			hash0=hashtohex(compHash(path+file));
			System.out.println("OK");
			System.out.println("hash:"+hash0);
		}
		else if(mode==5)
		{
			v1=randvec();
			v2=randvec();
			encrypt(v1,v2);
			System.out.println("OK");
		}
		else if(mode==6)
		{
			decrypt();
			System.out.println("OK");
		}
		
		System.out.print("/// END ///");
    }
	
	public static boolean[][] matpow(boolean[] pow)
	{
		boolean[][] ret=new boolean[n][n];
		boolean[][][] mats = new boolean[n][n][n];
		int i;
		
		mats[0]=matAbool;
		if(pow[0]) ret=mats[0];
		else for(i=0;i<n;i++) ret[i][i]=true;

		for(i=1;i<n;i++)
		{
			mats[i]=matmultbool(mats[i-1],mats[i-1]);
			if(pow[i]) ret=matmultbool(ret,mats[i]);
		}

		return ret;
	}
	
	public static boolean[][] matpowDH(boolean[][] mat, boolean[] pow)
	{
		boolean[][] ret=new boolean[n][n];
		boolean[][][] mats = new boolean[n][n][n];
		int i;
		
		mats[0]=mat;
		if(pow[0]) ret=mats[0];
		else for(i=0;i<n;i++) ret[i][i]=true;

		for(i=1;i<n;i++)
		{
			mats[i]=matmultbool(mats[i-1],mats[i-1]);
			if(pow[i]) ret=matmultbool(ret,mats[i]);
		}

		return ret;
	}
	
	public static String bintob64(boolean[] vec)
	{
		byte[] v2 = new byte[34]; //n=269 hard-coded in this function
		int i,j,tmp,tp;
		
		for(j=0;j<34;j++)
		{
			tmp=0;
			tp=1;
			for(i=0;i<8;i++)
			{
				if(8*j+i<n && vec[8*j+i]) tmp+=tp;
				tp*=2;
			}
			v2[j]=(byte) tmp;
		}
		
		return new String(Base64.getEncoder().encode(v2));
	}
	
	public static boolean[] b64tobin(String vec) //n=269 hard-coded in this function
	{
		boolean[] ret = new boolean[n];
		byte[] v2 = Base64.getDecoder().decode(vec.getBytes());
		int i,j,tmp;
		
		for(j=0;j<34;j++)
		{
			tmp=v2[j];
			for(i=0;i<8;i++)
			{
				if(tmp%2==0) tmp/=2;
				else {ret[8*j+i]=true; tmp-=1; tmp/=2;}
			}
		}
		
		
		return ret;
	}
	
	public static String bintob64sign(boolean[] vec)
	{
		byte[] v2 = new byte[68]; //n=269 hard-coded in this function
		int i,j,tmp,tp;
		
		for(j=0;j<68;j++)
		{
			tmp=0;
			tp=1;
			for(i=0;i<8;i++)
			{
				if(8*j+i<2*n && vec[8*j+i]) tmp+=tp;
				tp*=2;
			}
			v2[j]=(byte) tmp;
		}
		
		return new String(Base64.getEncoder().encode(v2));
	}
	
	public static boolean[] b64tobinsign(String vec) //n=269 hard-coded in this function
	{
		boolean[] ret = new boolean[2*n];
		byte[] v2 = Base64.getDecoder().decode(vec.getBytes());
		int i,j,tmp;
		
		for(j=0;j<68;j++)
		{
			tmp=v2[j];
			for(i=0;i<8;i++)
			{
				if(tmp%2==0) tmp/=2;
				else {ret[8*j+i]=true; tmp-=1; tmp/=2;}
			}
		}
		
		
		return ret;
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
				for(j=0;j<n;j++) tmp[j]=v2[(j-i+n)%n];
				ret=addmod(ret,tmp);
			}
		}
		
		return ret;
	}
	
	
	public static boolean[] oneway(boolean[] vec)
	{
		boolean[] ret = new boolean[n];
		
		ret=Arrays.copyOf(vec, vec.length);
		int i;
		
		for(i=0;i<n;i++) if(vec[i]) ret=matact(matA,ret); else ret=matact(matB,ret);
		
		return ret;
	}
	

	
	public static boolean[] vecpow(boolean[] m, boolean[] v) //computes A^m acting on v
	{
		boolean[] ret = new boolean[n];
		
		ret=Arrays.copyOf(v, v.length);
		int i;
		
		for(i=0;i<n;i++) if(m[i]) ret=matact(matApow[i],ret);
		
		return ret;
	}
	
	public static boolean isInv(boolean[][] mat)
	{
		int i,j,k;
		boolean copy[][]=new boolean[n][n];
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++) copy[i][j]=mat[i][j];
		}
	
		for(i=0;i<n;i++)
		{
			j=i;
			while(j<n && !copy[i][j]) j++;
			if(j==n) {System.out.println(i); return false;}
			
			if(j!=i) for(k=i;k<n;k++) copy[k][i]^=copy[k][j];
			
			for(j=i+1;j<n;j++) if(copy[i][j]) for(k=i;k<n;k++) copy[k][j]^=copy[k][i];
		}
	
		return true;
	}
	
	
	public static boolean[] compHash(String path) throws IOException
	{
		boolean[] ret = new boolean[n];
		boolean[] mi = new boolean[n];
		boolean[] byt = new boolean[8];
		boolean[] lgtbin = new boolean[64];
		InputStream os = new FileInputStream(path);
		long size= new File(path).length();
		int i,j,tmp3;
		
		ret[0]=true;
		
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
					ret=xor(matact(matA,ret),oneway(mi));
				}
			}
		}
		
		//padding with length
		lgtbin=dectobinlgt(size);
		
		for(i=0;i<64;i++)
		{
			mi[tmp3]=lgtbin[i];
			tmp3++;
			if(tmp3==n)
			{
				tmp3=0;
				ret=xor(matact(matA,ret),oneway(mi));
			}
		}
		
		//padding a single one
		mi[tmp3]=true;
		tmp3++;
		if(tmp3==n)
		{
			tmp3=0;
			ret=xor(matact(matA,ret),oneway(mi));
		}
		
		//padding with zeros
		while(tmp3!=0)
		{
			mi[tmp3]=false;
			tmp3++;
			if(tmp3==n)
			{
				tmp3=0;
				ret=xor(matact(matA,ret),oneway(mi));
			}
		}
		
		ret=oneway(ret);
		
		return ret;
	}
	
	public static String hashtohex(boolean[] hash)
	{
		int i,j;
		boolean[] tmp = new boolean[8];
		String ret="";
		String tmp2;
		
		for(i=0;i<32;i++)
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
		Random rand = new Random();
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
	
	public static String signature()
	{
		int i;
		boolean[] k = randvec();
		boolean[] v0 = new boolean[n];
		boolean[] mone = new boolean[n];
		boolean[] ret = new boolean[2*n];
		
		v0[0]=true;
		for(i=1;i<n;i++) mone[i]=true;
		
		boolean[] r = vecpow(k,v0);
		boolean[] e = oneway(xor(r,hextobin(hash))); //unsafe?
		boolean[] s = addmod(k,vecmult(mone,vecmult(e,b64tobin(privatekey))));
		
		for(i=0;i<n;i++) ret[i]=e[i];
		for(i=n;i<2*n;i++) ret[i]=s[i-n];
		
		return bintob64sign(ret);
	}
	
	public static boolean verify()
	{
		int i;
		boolean[] read = b64tobinsign(signature);
		boolean[] e = new boolean[n];
		boolean[] s = new boolean[n];
		
		
		for(i=0;i<n;i++) e[i]=read[i];
		for(i=n;i<2*n;i++) s[i-n]=read[i];
		
		boolean[][] matpub = matpowDH(recovermat(b64tobin(publickey)),e);
		boolean[] r = vecpow(s,matpub[0]);
		
		if(compvecsign(e,oneway(xor(r,hextobin(hash))))) return true;
		return false;
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
	
	public static void encrypt(boolean[] k1, boolean[] k2) throws IOException
	{
		//creating keys file
		boolean[] stream = k1;
		boolean[] inter = k2;
		OutputStream os = new FileOutputStream(path+file+".rpk");
	
		int i,j,tmp=0;
		boolean[] vec=new boolean[8];
		boolean[] vec2=new boolean[8];

		for(i=0;i<n;i++)
		{
			vec[tmp]=k1[i];
			tmp++;
			if(tmp==8)
			{
				tmp=0;
				os.write(bintodec(vec));
			}
		}
	
		for(i=0;i<n;i++)
		{
			vec[tmp]=k2[i];
			tmp++;
			if(tmp==8)
			{
				tmp=0;
				os.write(bintodec(vec));
			}
		}
		if(tmp!=0)
		{
			for(i=tmp;i<=7;i++) vec[i]=false;
			os.write(bintodec(vec));
		}
		
		os.close();
		
		//encrypting file with keys
		os = new FileOutputStream(path+file+".rpe");
		InputStream rs = new FileInputStream(path+file);
		long size= new File(path+file).length();
		tmp=0;
		
		stream=oneway(xor(stream,inter));
		inter=matact(matA,inter);
		
		for(i=1;i<=size;i++)
		{
			for(j=0;j<8;j++)
			{
				vec2[j]=stream[tmp];
				tmp++;
				if(tmp==n)
				{
					stream=oneway(xor(stream,inter));
					inter=matact(matA,inter);
					tmp=0;
				}
			}
			vec=dectobin(rs.read());
			os.write(bintodec(xor8(vec,vec2)));
		}

		os.close();
		rs.close();
	}
	
	private static boolean[] hextobin(String hex)
	{
		int i;
		boolean[] ret=new boolean[n];
		
        hex = hex.replaceAll("0", "0000");
        hex = hex.replaceAll("1", "0001");
        hex = hex.replaceAll("2", "0010");
        hex = hex.replaceAll("3", "0011");
        hex = hex.replaceAll("4", "0100");
        hex = hex.replaceAll("5", "0101");
        hex = hex.replaceAll("6", "0110");
        hex = hex.replaceAll("7", "0111");
        hex = hex.replaceAll("8", "1000");
        hex = hex.replaceAll("9", "1001");
        hex = hex.replaceAll("a", "1010");
        hex = hex.replaceAll("b", "1011");
        hex = hex.replaceAll("c", "1100");
        hex = hex.replaceAll("d", "1101");
        hex = hex.replaceAll("e", "1110");
        hex = hex.replaceAll("f", "1111");
        
        for(i=0;i<hex.length();i++) if(hex.charAt(i)=='1') ret[i]=true;
        
        return ret;
    }
	
	public static boolean[][] recovermat(boolean[] vec)
	{
		int i;
		boolean[][] ret= new boolean[n][n];
		
		for(i=0;i<n;i++)
		{
			ret[i]=matact(matidx(DHpow[i]),vec);
		}
		
		return matmultbool(ret,DHinv);
	}
	
	
	public static void decrypt() throws IOException
	{
		//getting keys
		int i,j,tmp=0;
		InputStream rs = new FileInputStream(path+file+".rpk");
		boolean[] k1 = new boolean[n];
		boolean[] k2= new boolean[n];
		boolean[] vec= new boolean[8];
		boolean[] vec2= new boolean[8];
		boolean[] stream = new boolean[n];
		boolean[] inter = new boolean[n];
		boolean getout=true;
		boolean other=true;
		
		while(getout)
		{
			vec=dectobin(rs.read());
			for(j=0;j<8;j++) 
			{
				if(other)
				{
					k1[tmp]=vec[j];
					tmp++;
					if(tmp==n)
					{
						other=false;
						tmp=0;
					}
				}
				else
				{
					k2[tmp]=vec[j];
					tmp++;
					if(tmp==n)
					{
						j=8;
						getout=false;
					}
				}
			}
		}
		
		rs.close();
		
		stream=k1;
		inter=k2;
		
		//decrypting
		OutputStream os = new FileOutputStream(path+"decrypted-"+file);
		rs = new FileInputStream(path+file+".rpe");
		long size= new File(path+file+".rpe").length();
		
		tmp=0;
		
		stream=oneway(xor(stream,inter));
		inter=matact(matA,inter);
		
		for(i=1;i<=size;i++)
		{
			for(j=0;j<8;j++)
			{
				vec2[j]=stream[tmp];
				tmp++;
				if(tmp==n)
				{
					stream=oneway(xor(stream,inter));
					inter=matact(matA,inter);
					tmp=0;
				}
			}
			vec=dectobin(rs.read());
			os.write(bintodec(xor8(vec,vec2)));
		}
		os.close();
		rs.close();
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
		
		for(i=0;i<n;i++)
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
			one[0]=true;
			
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
	
	public static boolean[] xor8(boolean[] a, boolean b[])
	{
		boolean[] ret = new boolean[8];
		int i;
		
		for(i=0;i<8;i++) ret[i]=a[i]^b[i];
		
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
				ret[j][i]=vec[k];
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
	
	public static void readmats(String path) throws IOException
	{
		InputStream os = new FileInputStream(path);
		boolean[] vec = new boolean[8];
		int i=0,j=0,k,l=0;
		boolean[][] ret = new boolean[n][n];
		
		while(true)
		{
			vec=dectobin(os.read());
			for(k=0;k<8;k++)
			{
				ret[j][i]=vec[k];
				j++;
				if(j==n)
				{
					j=0;
					i++;
					if(i==n)
					{
						matApow[l]=matidx(ret);
						ret = new boolean[n][n];
						i=0;
						l++;
						if(l==n) {os.close(); return;}
					}
				}
			}
		}
	}
	
	public static void readmatsDH(String path) throws IOException
	{
		InputStream os = new FileInputStream(path);
		boolean[] vec = new boolean[8];
		int i=0,j=0,k,l=0;
		boolean[][] ret = new boolean[n][n];
		
		while(true)
		{
			vec=dectobin(os.read());
			for(k=0;k<8;k++)
			{
				ret[j][i]=vec[k];
				j++;
				if(j==n)
				{
					j=0;
					i++;
					if(i==n)
					{
						DHpow[l]=ret;
						ret = new boolean[n][n];
						i=0;
						l++;
						if(l==n) {os.close(); return;}
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
	
	public static boolean[] dectobin16(int byt)
	{
		boolean[] ret = new boolean[16];
		int i;
		int tmp=byt;
		
		for(i=15;i>=0;i--)
		{
			tmp=byt%2;
			if(tmp==1) ret[i]=true;
			byt-=tmp;
			byt/=2;
		}
		
		return ret;
	}
	
	public static boolean[] dectobinlgt(long lgt)
	{
		boolean[] ret = new boolean[64];
		int i;
		long tmp;
		
		for(i=63;i>=0;i--)
		{
			tmp=lgt%2;
			if(tmp==1) ret[i]=true;
			lgt-=tmp;
			lgt/=2;
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
	
	public static boolean compvecsign(boolean[] vec1, boolean[] vec2)
	{
		int i;
		for(i=0;i<n;i++)
		{
			if(vec1[i]!=vec2[i]) return false;
		}
		return true;
	}
	
	public static void display(boolean[][] mat)
	{
		int i,j;
		
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				if(mat[j][i]) System.out.print("1");
				else System.out.print("0");
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

	
