package com.andy.demo.utils;

/**
 * byte相关常用方法工具类
 */
public class ByteFormat {

	/**
	 * 将byte数组转换为十六进制文本
	 * @param buf
	 * @return
	 */
	public static
	String toHex(byte[] buf) {
		if (buf == null || buf.length == 0) {
			return "";
		}
		
		StringBuilder out = new StringBuilder();
		
		for( int i=0; i< buf.length; i++ ){
			out.append( HEX[ (buf[i]>>4) & 0x0f ] ).append(HEX[ buf[i] & 0x0f ] ); 
		}
		
		return out.toString();
	}

	/**
	 * 将十六进制文本转换为byte数组
	 * @param str
	 * @return
	 */
	public static
	byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		}
		
		char[] hex = str.toCharArray();
		
		int length = hex.length / 2;
		byte[] raw = new byte[length];
		for (int i = 0; i < length; i++) {
			int high = Character.digit(hex[i * 2], 16);
			int low = Character.digit(hex[i * 2 + 1], 16);
			int value = (high << 4) | low;
			if (value > 127)
				value -= 256;
			raw[i] = (byte) value;
		}
		return raw;
	}

	/**
	 * 将byte数组转换为格式化的十六进制文本
	 * @param buf
	 * @return
	 */
	public static
	String dumpHex(byte[] buf) {
		if (buf == null) {
			return "";
		}
		
		return dumpHex(buf, 0, buf.length);
	}
	
	/**
	 * 将byte数组转换为格式化的十六进制文本
	 * @param buf
	 * @parm offset
	 * @param numBytes
	 * @return
	 */
	public static
	String dumpHex(byte[] buf, int offset, int numBytes) {
		if (buf == null || buf.length == 0) {
			return "";
		}
		
		if (offset >= buf.length) {
			offset = buf.length - 1;
		}
		
		if (numBytes > buf.length - offset) {
			numBytes = buf.length - offset;
		}
		
		StringBuffer out = new StringBuffer();
		
		int			rows, residue, i, j;
		byte[]		save_buf= new byte[ ROW_BYTES+2 ];
		char[]		hex_buf = new char[ 4 ];
		char[]		idx_buf = new char[ 8 ];
		
		rows = numBytes >> 4;
		residue = numBytes & 0x0000000F;
		for ( i = 0 ; i < rows ; i++ ) {
			int hexVal = (i * ROW_BYTES);
			idx_buf[0] = HEX[ ((hexVal >> 12) & 15) ];
			idx_buf[1] = HEX[ ((hexVal >> 8) & 15) ];
			idx_buf[2] = HEX[ ((hexVal >> 4) & 15) ];
			idx_buf[3] = HEX[ (hexVal & 15) ];

			String idxStr = new String( idx_buf, 0, 4 );
			out.append( idxStr + ": " );
		
			for ( j = 0 ; j < ROW_BYTES ; j++ ) {
				save_buf[j] = buf[ offset + (i * ROW_BYTES) + j ];

				hex_buf[0] = HEX[ (save_buf[j] >> 4) & 0x0F ];
				hex_buf[1] = HEX[ save_buf[j] & 0x0F ];

				out.append( hex_buf[0] );
				out.append( hex_buf[1] );
				out.append( ' ' );

				if ( j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2 )
					out.append( ' ' );

				if ( save_buf[j] < 0x20 || save_buf[j] > 0x7E )
					save_buf[j] = (byte) '.';
				}

			String saveStr = new String( save_buf, 0, j );
			out.append( " ; " + saveStr + "\n" );
		}
		
		if ( residue > 0 ) {
			int hexVal = (i * ROW_BYTES);
			idx_buf[0] = HEX[ ((hexVal >> 12) & 15) ];
			idx_buf[1] = HEX[ ((hexVal >> 8) & 15) ];
			idx_buf[2] = HEX[ ((hexVal >> 4) & 15) ];
			idx_buf[3] = HEX[ (hexVal & 15) ];

			String idxStr = new String( idx_buf, 0, 4 );
			out.append( idxStr + ": " );

			for ( j = 0 ; j < residue ; j++ ) {
				save_buf[j] = buf[ offset + (i * ROW_BYTES) + j ];

				hex_buf[0] = HEX[ (save_buf[j] >> 4) & 0x0F ];
				hex_buf[1] = HEX[ save_buf[j] & 0x0F ];

				out.append( (char)hex_buf[0] );
				out.append( (char)hex_buf[1] );
				out.append( ' ' );

				if ( j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2 )
					out.append( ' ' );

				if ( save_buf[j] < 0x20 || save_buf[j] > 0x7E )
					save_buf[j] = (byte) '.';
			}
				
			for ( /*j INHERITED*/ ; j < ROW_BYTES ; j++ ) {
				save_buf[j] = (byte) ' ';
				out.append( "   " );
				if ( j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2 )
					out.append( " " );
			}
			
			String saveStr = new String( save_buf, 0, j );
			out.append( " ; " + saveStr + "\n" );
		}
		
		return out.toString();
	}

	/**
	 * 测试
	 * @param args
	 */
	public static 
	void main( String[] args ) {

		byte[] data = new byte[1024 * 64];
		for ( int i = 0 ; i < data.length ; ++i ) {
			data[i] = (byte)i;
		}

		//System.out.println(dumpHex( data, 0, data.length ));
	}

	private final static
	char[] HEX = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F' };

	private static final int ROW_BYTES = 16;
	private static final int ROW_QTR1 = 3;
	private static final int ROW_HALF = 7;
	private static final int ROW_QTR2 = 11;

}
