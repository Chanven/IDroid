package com.andy.demo.utils;

import java.util.ArrayList;

import com.andy.demo.utils.HanziToPinyin.Token;


public class PinYin {
	// 汉字返回拼音，字母原样返回，都转换为小写
	public static String getPinYin(String input) {
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
		StringBuilder sb = new StringBuilder();
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					sb.append(token.target);
				} else {
					sb.append(token.source);
				}
			}
		}
		return sb.toString().toLowerCase();
	}

	// 字符串首字母缩写
	public static String getPinYinInitials(String input) {
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
		StringBuilder sb = new StringBuilder();
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					String charAt = String.valueOf(token.target.charAt(0))
							.toLowerCase();
					boolean isPunct = CommonUtils.isPunctuation(token.target
							.charAt(0));
					if (isPunct) {
						continue;
					}
					sb.append(charAt);
					if (token.target.length() > 1 && charAt.matches("[zcs]")) {
						charAt = String.valueOf(token.target.charAt(1));
						if ("h".equalsIgnoreCase(charAt)) {
							sb.append(charAt);
						}
					}
				} else {
					String source = token.source;
					boolean isInt = CommonUtils.isPuleNumber(source);
					if (isInt) {
						sb.append(source);
					} else {
						char chatAt = source.charAt(0);
						boolean isPunct = CommonUtils.isPunctuation(chatAt);
						if (isPunct) {
							continue;
						}
						sb.append(source.charAt(0));
					}
				}
			}
		}
		return sb.toString().toLowerCase();
	}
}
