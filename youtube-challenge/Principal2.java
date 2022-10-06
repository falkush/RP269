//by falkush
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class Principal2 {
	static int n=512;
	static int[][] matA;
	
	public static void main(String args[]) throws IOException
	{	
		//challenge: find answer to return true
		String answer = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001";
		String target = "399d90ed8d7e2f12e556ad818e365e5c9806a3e42c6cbfdaeeb01f4b52bbd87faaec35306c34a0e03e48010dad98b83027fd8361470acf279172d4c5acb4b5fc";
		
		boolean[] f;
		
		matA=matidx(readmat("C:\\test\\A.dat")); //path to A.dat
		
		f=hextobin(answer);
		f=addition(matact2(matA,f),f);
		System.out.println(bintohex(f));
		System.out.println(compare(f,hextobin(target)));
    }
	
	public static boolean compare(boolean[] v1, boolean[] v2)
	{
		for(int i=0;i<512;i++) if(v1[i]!=v2[i]) return false;
		return true;
	}
	
	public static String bintohex(boolean[] hash)
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
	
	static boolean[] hextobin(String s) {
		boolean[] ret = new boolean[n];
		String str = new BigInteger(s, 16).toString(2);
		
		while(str.length()<512) str="0"+str;
		
		for(int i=0;i<512;i++){
			if(str.charAt(i)=='0') ret[i]=false;
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
	
	public static int[][] matidx(boolean[][] mat) //optimization for the matrix action
	{
		int[][] ret = new int[n][n];
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
	
	
	public static int bintodec(boolean[] vec)
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
	
	public static boolean[] matact2(int[][] mat, boolean[] vec)
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
}

	