package p3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * @author ivy
 * 
 *         2018.8.6
 * 
 * 
 */
public class findmotif {

	public static void main(String[] args) {

		Scanner reader = new Scanner(System.in);
		System.out.println("请输入输出文件名称：");
		String outfilename = reader.next();
		System.out.println("请输入输入文件名称：");
		String infilename = reader.next();
		//System.out.println("请输入碱基序列：");

		PrintStream myout = null;
		try {
			myout = new PrintStream(outfilename + ".txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 创建一个打印输出流
		System.setOut(myout);// 把创建的打印输出流赋给系统。即系统下次向 myout输出

		String s = null;
		try {
			s = LoadContentByPath(infilename+".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//s = reader.next();//手动添加初始序列
		s += "$";

		char[] chars = s.toCharArray();
		int len = s.length();
		String[] suff = new String[len];
		SUF[] suf = new SUF[len];

		// 打印初初始序列
		System.out.println("================initialization：");
		System.out.println(s);
		System.out.println();

		// 获得全部后缀
		int i = 0;
		int j = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();// 暂时使用map,after sorting 的时候赋值
		System.out.println();
		System.out.println("================before sorting：");
		while (i < len) {
			suff[j] = String.copyValueOf(chars, i, len - i);
			System.out.println(suff[j]);
			map.put(suff[j], j);
			i++;
			j++;
		}

		Arrays.sort(suff);
		System.out.println();
		System.out.println("================after sorting：");
		for (int m = 0; m < suff.length; m++) {
			// 对后缀进行排序之后的打印
			suf[m] = new SUF();
			suf[m].setAi(m);
			suf[m].setSa(map.get(suff[m]));
			suf[m].setString(suff[m]);
			System.out.printf("%-6d", map.get(suff[m]));
			System.out.println(suff[m]);
		}

		for (int m = 1; m < suf.length; m++) {
			// 获得SUF类的一系列值并将其存入到map中
			suf[m].setLcpa(suf[m].findLcpa(suf[m - 1].getString()));
			suf[m].setK(suf[m].findK(suf[m - 1].getString()));
			suf[m].setR(suf[m].findR());
			suf[m].setP(suf[m].findP(suf[m - 1].getSa()));
		}

		// 打印一系列中间值
		System.out.println();
		System.out.println("================after finding LCPA K R P：");
		System.out.println();
		System.out.printf("%-6s", "AI");
		System.out.printf("%-6s", "SA");
		System.out.printf("%-6s", "LCPA");
		System.out.printf("%-6s", "K");
		System.out.printf("%-6s", "R");
		System.out.printf("%-6s", "P");
		System.out.printf("%-6s", "STRING");
		System.out.println();
		System.out.println();
		for (int m = 0; m < suf.length; m++) {
			System.out.printf("%-6d", suf[m].getAi());
			System.out.printf("%-6d", suf[m].getSa());
			System.out.printf("%-6d", suf[m].getLcpa());
			System.out.printf("%-6d", suf[m].getK());
			System.out.printf("%-6d", suf[m].getR());
			System.out.printf("%-6d", suf[m].getP());
			System.out.println(suf[m].getString());
		}
		// 打印结果
		System.out.println();
		System.out.println("================result：");
		System.out.println();
		for (int m = 0; m < suf.length; m++) {
			if (suf[m].getR() > 0) {
				// （1）第4个位置存在长度为6的重复序列，重复2次的SSR, GGTGGT
				String prin = String.copyValueOf(chars, suf[m].getP(), (suf[m].getR() + 1) * suf[m].getK());
				System.out.println("第" + suf[m].getP() + "个位置存在长度为" + (suf[m].getR() + 1) * suf[m].getK() + "，重复"
						+ (suf[m].getR() + 1) + "次的SSR，" + prin);
			}
		}

		// 寻找不相邻的重复徐杰
		System.out.println();
		System.out.println("================motif：");
		findmymotif(suf, 2, 4, 3);
	}

	/**
	 * 从文件中读取
	 * 
	 * @param path
	 * @return string
	 */
	public static String LoadContentByPath(String path) throws IOException {
		InputStream is = new FileInputStream(path);
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();

	}

	/**
	 * 寻找Motif，相当于非连续的ssr
	 * 
	 * @param lmin
	 *            区间中的数字都大于等于它
	 * @param lmax
	 *            区间中至少有一个小于等于它
	 * @param r
	 *            区间长度大于等于它(重复次数的下限)
	 * @return
	 * 
	 */
	public static void findmymotif(SUF[] suf, int lmin, int lmax, int r) {
		for (int i = 0; i < suf.length;) {

			if (suf[i].getLcpa() >= lmin) {// 用来验证lmin
				int min = lmax;// 用于获取区间中最小的数字
				boolean fmax = false;// 用来验证lmax
				int count = 1;// 用来验证r
				Map<Integer, SUF> map = new HashMap<Integer, SUF>();// 存放暂时的序列
				map.put(suf[i - 1].getAi(), suf[i - 1]);
				while (i < suf.length) {
					if (suf[i].getLcpa() < lmin) {
						break;
					}
					if (suf[i].getLcpa() < min) {
						min = suf[i].getLcpa();
					}
					if (suf[i].getLcpa() >= lmax) {
						fmax = true;
					}
					map.put(suf[i].getAi(), suf[i]);
					i++;
					count++;
				}
				if (count >= r && fmax) {
					char[] chars = suf[i - 1].getString().toCharArray();
					String re = String.copyValueOf(chars, 0, min);
					// 打印
					Iterator<Entry<Integer, SUF>> iterator = map.entrySet().iterator();
					System.out.print("在");
					while (iterator.hasNext()) {// 用while循环，判断是否有下一个
						Entry<Integer, SUF> entry = iterator.next();// 声明entry，并用它来装载字符串
						System.out.print(entry.getValue().getSa() + " ");
					}
					System.out.print("的位置上，存在重复" + count + "次的SSR：");
					System.out.println(re);
				}

			} else {
				i++;
			}
		}
	}
}
