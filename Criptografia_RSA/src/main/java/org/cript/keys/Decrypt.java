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

public class Decrypt {

    public static String DEST_FILE = "src/main/resources/destTextFileDecrypt/destTextFileDecrypt.txt";

    public static void main(String[] args) {

        List<String> destTextList = readTextFile(Encrypt.DEST_FILE_ENCRYPT);

        String base64 = "";
        for (String textLine : destTextList) {

            BigInteger encodedChunk = new BigInteger(textLine);
            BigInteger originalChunk = encodedChunk.modPow(Encrypt.getPrivatekey(), Encrypt.getModulus());

            TextChunk chunk2 = new TextChunk(originalChunk);

            for (int i=chunk2.toString().length(); i > 0; i--) {
                base64 += chunk2.toString().substring(i - 1, i);
            }

        }

        byte[] bytes = Base64.getDecoder().decode(base64.getBytes());

        String textDecrypt = new String(bytes);
        writeTextFile(textDecrypt);

    }

    private static List<String> readTextFile(String path) {
        List<String> textList = new ArrayList<>();

        try {
            InputStream file = new FileInputStream(new File(path));

            Reader isr = new InputStreamReader(file);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while( (line = br.readLine()) != null) {
                textList.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return textList;
    }

    private static void writeTextFile(String text) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(DEST_FILE)));
            bw.write(text);
            bw.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
