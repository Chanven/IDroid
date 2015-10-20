package com.andy.demo.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * ƴ��������
 */
public class PinyinUtils {

	private static PinyinUtils utils = null;

	public static PinyinUtils getInstance() {
		if (utils == null) {
			synchronized (PinyinUtils.class) {
				if (utils == null) {
					utils = new PinyinUtils();
				}
			}
		}
		return utils;
	}

	/**
	 * ��ú���ƴ������ĸ
	 * */
	public String getAlpha(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		if (nameChar != null && defaultFormat != null) {
			for (int i = 0; i < nameChar.length; i++) {
				if (nameChar[i] > 128) {
					try {
						pinyinName += PinyinHelper.toHanyuPinyinStringArray(
								nameChar[i], defaultFormat)[0].charAt(0);
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				} else {
					pinyinName += nameChar[i];
				}
			}
		}
		return pinyinName;
	}

	/**
	 * ���ַ��е�����ת��Ϊƴ��,�����ַ��
	 * 
	 * @param inputString
	 * @return ƴ��
	 */
	public String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);
					output += temp[0];
				} else
					output += java.lang.Character.toString(input[i]);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * ����ת��λ����ƴ������ĸ��Ӣ���ַ��
	 * 
	 * @param chines
	 *            ����
	 * @return ƴ��
	 */
	public String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}
}
