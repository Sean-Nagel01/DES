/*
    A2Decrypt.java
    By Kelsey Baker 3302479
    and Sean Nagel 3356603
    created 27/05/2021
    for comp3260 assignment 2
*/
import java.io.*;
import java.util.Scanner;

public class A2Decrypt 
{
    public static void main(String args[])
    {
        //the plaintext
        char[] c = null;
        char[] k64 = null;

        try
        {
            //input: the input file passed into this program
            File input = new File(args[0]);

            //s: a scanner for reading input
            Scanner s = new Scanner(input);

            c = s.nextLine().toCharArray();
            k64 = s.nextLine().toCharArray();
            
            s.close();

            //validate the file
            for(int i = 0; i < 64; i++)
            {
                //if ciphertext or key contain characters that arent 0 or 1
                //or if ciphertext or key i too long
                if((c[i] != '0' && c[i] != '1') || 
                (k64[i] != '0' && k64[i] != '1') ||
                c.length > 64 || k64.length > 64)
                {
                    System.exit(1);
                }
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Input file is invalid");
        }
        
        //the key after being converted to 56 bit
        char[] k = KeyGen.to56(k64);

        //sub keys of k
        char[][] kSubKeys = KeyGen.genKeys(k);

        //decrypts c using kSubKeys
        char[] p = DES.decrypt0(c, kSubKeys);

        //printing to file
        try
        {
            File output = new File("output.txt");
            BufferedWriter w = new BufferedWriter(new FileWriter(output, true));

            w.newLine();
            w.append("DECRYPTION");
            w.newLine();
            w.append("CipherText C: " + String.valueOf(c));
            w.newLine();
            w.append("Key K: " + String.valueOf(k64));
            w.newLine();
            w.append("Plaintext P: " + String.valueOf(p));
            
            w.close();
        }
        catch(Exception e)
        {
            System.out.println("it brokey");
        }
    }
}
