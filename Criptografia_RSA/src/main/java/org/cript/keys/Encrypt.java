package org.cript.keys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Encrypt {

    private static String TEXT_FILE = "src/main/resources/textFile/textFile.txt";
    public static String PUBLIC_KEY_FILE = "src/main/resources/publicKeyFile/publicKeyFile.txt";
    public static String DEST_FILE_ENCRYPT = "src/main/resources/destTextFileEncrypt/destTextFileEncrypt.txt";
    private static String PRIME_LIST_FILE = "src/main/resources/primeList/primeList.txt";

    public static void main(String[] args) {
        encodeFile();
    }

    public static String readTextFile(String path) {
        StringBuilder text = new StringBuilder();

        try {
            InputStream file = new FileInputStream(new File(path));

            Reader isr = new InputStreamReader(file);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while( (line = br.readLine()) != null) {
                text.append(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return text.toString();
    }

    private static void writeTextFile(List<BigInteger> bigIntegerList) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(DEST_FILE_ENCRYPT)));
            for (BigInteger B : bigIntegerList) {
                bw.write(B + "\n");
            }
            bw.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> readTwoPrimeNumbers() {
        List<String> primeNumberList = new ArrayList<>();

        try {
            InputStream file = new FileInputStream(new File(PRIME_LIST_FILE));

            Reader isr = new InputStreamReader(file);
            BufferedReader br = new BufferedReader(isr);

            String line;
            int count = 0;
            while( (line = br.readLine()) != null && count < 2) {
                primeNumberList.add(line);
                count++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return primeNumberList;
    }

    public static BigInteger getPrivatekey() {

        BigInteger publicExponent = new BigInteger(readTextFile(PUBLIC_KEY_FILE));
        BigInteger big1 = new BigInteger("1");

        List<String> numberPrimesList = readTwoPrimeNumbers();

        return GetKeys.getPrivateExp((new BigInteger(numberPrimesList.get(0)).subtract(big1).multiply(new BigInteger(numberPrimesList.get(1)).subtract(big1))), publicExponent);
    }

    public static BigInteger getModulus() {

        List<String> numberPrimesList = readTwoPrimeNumbers();

        BigInteger P = new BigInteger(numberPrimesList.get(0));
        BigInteger Q = new BigInteger(numberPrimesList.get(1));

        return P.multiply(Q);
    }

    private static void encodeFile() {

        String textFileEncoded = Base64.getEncoder().encodeToString(readTextFile(TEXT_FILE).getBytes());
        int blockSize = TextChunk.blockSize(getModulus());

        List<BigInteger> bigIntegerList = new ArrayList<>();
        for (String chunk : TextChunk.splitByWidth(textFileEncoded, blockSize)) {

            BigInteger originalChunk =  new BigInteger(chunk.getBytes());
            BigInteger encodedChunk = originalChunk.modPow( new BigInteger(readTextFile(PUBLIC_KEY_FILE)), getModulus());

            bigIntegerList.add(encodedChunk);
        }

        writeTextFile(bigIntegerList);
    }

}
