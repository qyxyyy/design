package com.utils;

import cn.hutool.crypto.digest.DigestUtil;

public class MD5Util {
    
	/**
	 * @param text明文
	 * @param key密钥
	 * @return 密文
	 */
	// 带秘钥加密（text 为 null 或空时返回空字符串，避免 NPE）
	public static String md5(String text) {
		if (text == null) text = "";
		String md5str = DigestUtil.md5Hex(text);
		return md5str;
	}

}
