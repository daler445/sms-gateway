package tj.epic.sms.gateway.ws;

import java.util.Arrays;

public class test {
	private static final char[] hexChar = {'0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


	public static void main(String[] args) {
		/*String[] arr = new String[]{
				"12a", "23", "34", "45", "56", "67", "78", "89", "90", "01", "23d",
				"12b", "23", "34", "45", "56", "67", "78", "89", "90", "01", "23d",
				"12c", "23", "34", "45", "56", "67", "78", "89", "90", "01", "23d",
				"12d", "23", "34", "45", "56", "67", "78", "89", "90", "01", "23d",
				"12e", "23", "34", "45", "56", "67", "78", "89", "90", "01", "23d",
				"12f", "23", "34", "45", "56", "67", "78", "89", "90", "01", "23d",
		};

		String[][] arrays = chunkStringArray(arr, 11);
		for (String[] arrDivided : arrays) {
			System.out.println(Arrays.toString(arrDivided));
		}

		System.exit(0);*/
		System.out.println(convertHexStringToString("0000004e000000210000000000000007000501434254000201010139393239313832363830363400010101393932383830303031303234000000030000000000000c54657374206d657373616765"));
	}

	public static String[][] chunkStringArray(String[] array, int chunkSize) {
		int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
		String[][] output = new String[numOfChunks][];

		for (int i = 0; i < numOfChunks; ++i) {
			int start = i * chunkSize;
			int length = Math.min(array.length - start, chunkSize);

			String[] temp = new String[length];
			System.arraycopy(array, start, temp, 0, length);
			output[i] = temp;
		}

		return output;
	}

	public static String convertBytesToHexString(byte[] data, int offset, int length) {
		return convertBytesToHexString(data, offset, length, "");
	}

	public static String convertBytesToHexString(byte[] data, int offset, int length, String byteDelimiter) {
		final StringBuilder stringBuilder = new StringBuilder((length - offset) * (2 + byteDelimiter.length()));
		for (int i = offset; i < length; i++) {
			stringBuilder.append(hexChar[(data[i] >> 4) & 0x0f]);
			stringBuilder.append(hexChar[data[i] & 0x0f]);
			stringBuilder.append(byteDelimiter);
		}
		return stringBuilder.toString();
	}

	public static String convertHexStringToString(String hexString) {
		String uHexString = hexString.toLowerCase();
		StringBuilder sBld = new StringBuilder();
		for (int i = 0; i < uHexString.length(); i = i + 2) {
			char c = (char)Integer.parseInt(uHexString.substring(i, i + 2), 16);
			sBld.append(c);
		}
		return sBld.toString();
	}
}
