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
		System.out.println("����������ļ����ƣ�");
		String outfilename = reader.next();
		System.out.println("�����������ļ����ƣ�");
		String infilename = reader.next();
		//System.out.println("�����������У�");

		PrintStream myout = null;
		try {
			myout = new PrintStream(outfilename + ".txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // ����һ����ӡ�����
		System.setOut(myout);// �Ѵ����Ĵ�ӡ���������ϵͳ����ϵͳ�´��� myout���

		String s = null;
		try {
			s = LoadContentByPath(infilename+".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//s = reader.next();//�ֶ���ӳ�ʼ����
		s += "$";

		char[] chars = s.toCharArray();
		int len = s.length();
		String[] suff = new String[len];
		SUF[] suf = new SUF[len];

		// ��ӡ����ʼ����
		System.out.println("================initialization��");
		System.out.println(s);
		System.out.println();

		// ���ȫ����׺
		int i = 0;
		int j = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();// ��ʱʹ��map,after sorting ��ʱ��ֵ
		System.out.println();
		System.out.println("================before sorting��");
		while (i < len) {
			suff[j] = String.copyValueOf(chars, i, len - i);
			System.out.println(suff[j]);
			map.put(suff[j], j);
			i++;
			j++;
		}

		Arrays.sort(suff);
		System.out.println();
		System.out.println("================after sorting��");
		for (int m = 0; m < suff.length; m++) {
			// �Ժ�׺��������֮��Ĵ�ӡ
			suf[m] = new SUF();
			suf[m].setAi(m);
			suf[m].setSa(map.get(suff[m]));
			suf[m].setString(suff[m]);
			System.out.printf("%-6d", map.get(suff[m]));
			System.out.println(suff[m]);
		}

		for (int m = 1; m < suf.length; m++) {
			// ���SUF���һϵ��ֵ��������뵽map��
			suf[m].setLcpa(suf[m].findLcpa(suf[m - 1].getString()));
			suf[m].setK(suf[m].findK(suf[m - 1].getString()));
			suf[m].setR(suf[m].findR());
			suf[m].setP(suf[m].findP(suf[m - 1].getSa()));
		}

		// ��ӡһϵ���м�ֵ
		System.out.println();
		System.out.println("================after finding LCPA K R P��");
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
		// ��ӡ���
		System.out.println();
		System.out.println("================result��");
		System.out.println();
		for (int m = 0; m < suf.length; m++) {
			if (suf[m].getR() > 0) {
				// ��1����4��λ�ô��ڳ���Ϊ6���ظ����У��ظ�2�ε�SSR, GGTGGT
				String prin = String.copyValueOf(chars, suf[m].getP(), (suf[m].getR() + 1) * suf[m].getK());
				System.out.println("��" + suf[m].getP() + "��λ�ô��ڳ���Ϊ" + (suf[m].getR() + 1) * suf[m].getK() + "���ظ�"
						+ (suf[m].getR() + 1) + "�ε�SSR��" + prin);
			}
		}

		// Ѱ�Ҳ����ڵ��ظ����
		System.out.println();
		System.out.println("================motif��");
		findmymotif(suf, 2, 4, 3);
	}

	/**
	 * ���ļ��ж�ȡ
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
	 * Ѱ��Motif���൱�ڷ�������ssr
	 * 
	 * @param lmin
	 *            �����е����ֶ����ڵ�����
	 * @param lmax
	 *            ������������һ��С�ڵ�����
	 * @param r
	 *            ���䳤�ȴ��ڵ�����(�ظ�����������)
	 * @return
	 * 
	 */
	public static void findmymotif(SUF[] suf, int lmin, int lmax, int r) {
		for (int i = 0; i < suf.length;) {

			if (suf[i].getLcpa() >= lmin) {// ������֤lmin
				int min = lmax;// ���ڻ�ȡ��������С������
				boolean fmax = false;// ������֤lmax
				int count = 1;// ������֤r
				Map<Integer, SUF> map = new HashMap<Integer, SUF>();// �����ʱ������
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
					// ��ӡ
					Iterator<Entry<Integer, SUF>> iterator = map.entrySet().iterator();
					System.out.print("��");
					while (iterator.hasNext()) {// ��whileѭ�����ж��Ƿ�����һ��
						Entry<Integer, SUF> entry = iterator.next();// ����entry����������װ���ַ���
						System.out.print(entry.getValue().getSa() + " ");
					}
					System.out.print("��λ���ϣ������ظ�" + count + "�ε�SSR��");
					System.out.println(re);
				}

			} else {
				i++;
			}
		}
	}
}
