package DAO;

import java.util.Random;

public class Testing {
	
	public String generateKeys(int length) {
		Random random = new Random();
		Random random2 = new Random();
		String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String smallLetters = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
	
		String[] allCombinations = { capitalLetters, smallLetters, numbers };// symbols };
	
		StringBuilder password = new StringBuilder();
		int j, i = 0;
	
		while (i < length) {
			password.append(allCombinations[j = (random2.nextInt(allCombinations.length))]
					.charAt(random.nextInt(allCombinations[j].length())));
			i++;
		}
	
		return password.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(new Testing().generateKeys(50));
	}
	
}
