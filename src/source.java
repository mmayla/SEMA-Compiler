import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Scanner;


public class source
{
	public static String inst,oper1="r1",oper2="r1";
	public static int oper1am,oper2am; //0->direct , 1->indirect, 2->absolute
	
	public static String[] two_oper_N = {"add","adc","sub","mulb","mulw","muld","div","and","xor","or","cmp","sup","mov"};
	public static String[] two_oper_C = {"0000","0001","0010","0011","0100","0101","0110","0111","1000","1001","1010","1011","1110"};
	public static String[] one_oper_N = {"inc","dec","not","clr","shl","shr","ror","rorc","rol","rolc","push","pop"};
	public static String[] one_oper_C = {"0000","0001","0010","0011","0100","0101","0110","0111","1000","1001","1010","1011"};
	public static String[] no_oper_N = {"clc","stc","clz","hlt","nop"};
	public static String[] no_oper_C = {"000","001","010","011","100"};
	public static String[] jmp_oper_N = {"jmp","jz","jnz"};
	public static String[] jmp_oper_C = {"00","01","10"};
	
	
	public static void decode(String line)
	{
		line = line.toLowerCase();
		line = line.trim();
		
		if(line.length()>3)
			inst = line.substring(0, line.indexOf(' '));
		else inst = line;
		
		if(line.contains(","))
		{
			oper1 = line.substring(line.indexOf(' ')+1,line.indexOf(','));
			oper2 = line.substring(line.indexOf(',')+1, line.length());
			
			oper1 = oper1.trim();
			oper2 = oper2.trim();
		}
		else if(line.length()>3)
		{
			oper1 = line.substring(line.indexOf(' ')+1,line.length());
			oper1 = oper1.trim();
		}
		
		inst = inst.trim();
		
		
		operandsmod();
	}
	
	public static void operandsmod()
	{
		//oper1
		if(oper1.length()==2)
			oper1am = 0;
		else if(oper1.charAt(1)=='r')
		{
			oper1am = 1;
			oper1 = oper1.substring(1, oper1.length()-1);
		}
		else
		{
			oper1am = 2;
			oper1 = oper1.substring(1, oper1.length()-1);
		}
		
		//oper2
		if(oper2.length()==2)
			oper2am = 0;
		else if(oper2.charAt(1)=='r')
		{
			oper2am = 1;
			oper2 = oper2.substring(1, oper2.length()-1);
		}
		else
		{
			oper2am = 2;
			oper2 = oper2.substring(1, oper2.length()-1);
		}
	}
	
	public static String convert()
	{
		String ir = "0000000000000000";
		String type,opcode,dstmode,regdst,srcmode,regsrc;
		if(Arrays.asList(two_oper_N).contains(inst))
		{
			type = "01";
			opcode = two_oper_C[Arrays.asList(two_oper_N).indexOf(inst)];
			
			//addressing mode
			if(oper1am==0) dstmode = "00";
			else if(oper1am==1) dstmode ="01";
			else dstmode = "10";
			
			if(oper2am==0) srcmode = "00";
			else if(oper2am==1) srcmode ="01";
			else srcmode = "10";
			
			//reg binary
			if(oper1am==0 || oper1am==1)
			{
				if(oper1.equals("r0")) regdst="00";
				else if(oper1.equals("r1")) regdst="01";
				else if(oper1.equals("r2")) regdst="10";
				else regdst="11";
				
				regdst += "000000000";
			}
			else regdst = oper1;
			
			if(oper2am==0 || oper2am==1)
			{
				if(oper2.equals("r0")) regsrc="00";
				else if(oper2.equals("r1")) regsrc="01";
				else if(oper2.equals("r2")) regsrc="10";
				else regsrc="11";
				
				regsrc += "000000000";
			}
			else regsrc = oper2;
			
			ir = type+opcode+dstmode+regdst+srcmode+regsrc;
		}
		else if(Arrays.asList(one_oper_N).contains(inst))
		{
			type="00";
			opcode = one_oper_C[Arrays.asList(one_oper_N).indexOf(inst)];
			//reg binary
			if(oper1.equals("r0")) regdst="00";
			else if(oper1.equals("r1")) regdst="01";
			else if(oper1.equals("r2")) regdst="10";
			else regdst="11";
			
			ir = type+opcode+"11"+regdst+"0000000000000000000000";
		}
		else if(Arrays.asList(no_oper_N).contains(inst))
		{
			type="10";
			opcode = no_oper_C[Arrays.asList(no_oper_N).indexOf(inst)];
			ir = type+opcode+"000000000000000000000000000";
		}
		else
		{
			type="11";
			opcode = jmp_oper_C[Arrays.asList(jmp_oper_N).indexOf(inst)];
			
			//addressing mode
			if(oper1am==0) dstmode = "00";
			else if(oper1am==1) dstmode ="01";
			else dstmode = "10";
			
			//reg binary
			if(oper1am==0 || oper1am==1)
			{
				if(oper1.equals("r0")) regdst="00";
				else if(oper1.equals("r1")) regdst="01";
				else if(oper1.equals("r2")) regdst="10";
				else regdst="11";
				
				regdst += "000000000";
			}
			else regdst = oper1;
			
			ir = type+opcode+"00"+dstmode+regdst+"0000000000000";
		}
		
		return ir;
	}
	
	public static void printfile(String filecontent)
	{
		Writer writer = null;

		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("sema_assembly.mem"), "utf-8"));
			writer.write(filecontent);
		} catch (IOException ex)
		{
		} finally
		{
			try
			{
				writer.close();
			} catch (Exception ex)
			{
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner in = new Scanner(new File("sema_assembly.asm"));
		int tcase=0;
		String fcontent="";
		String convline;
		
		
		while(in.hasNextLine())
		{
			decode(in.nextLine());
			convline=convert();
			fcontent +=""+tcase+": "+convline.substring(0,16)+" "+convline.substring(16,32)+"\n";
			tcase += 2;
		}
		
		printfile(fcontent);
		
		System.out.println("finish compiling");
	}

}
