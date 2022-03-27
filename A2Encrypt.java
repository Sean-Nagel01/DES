/*
    A2Encrypt.java
    By Kelsey Baker 3302479
    and Sean Nagel 3356603
    created 27/05/2021
    for comp3260 assignment 2
*/
import java.io.*;
import java.util.Scanner;

public class A2Encrypt
{
    public static void main(String args[])
    {
        //the plaintext
        char[] p = null;
        char[] k64 = null;

        try
        {
            //input: the input file passed into this program
            File input = new File(args[0]);

            //s: a scanner for reading input
            Scanner s = new Scanner(input);

            p = s.nextLine().toCharArray();
            k64 = s.nextLine().toCharArray();
            
            s.close();

            for(int i = 0; i < 64; i++)
            {
                //if plaintext or key contain characters that arent 0 or 1
                //or if plaintext or key i too long
                if((p[i] != '0' && p[i] != '1') || 
                (k64[i] != '0' && k64[i] != '1') ||
                p.length > 64 || k64.length > 64)
                {
                    System.exit(1);
                }
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Input file is invalid");
        }      
        
        //64 plaintexts differing from the original by 1 bit each
        char[][] plainTexts = genPlainTexts(p);

        //the key after being reduced to 56 bits
        char[] k = KeyGen.to56(k64);
        //56 keys differing from the original by 1 bit each
        char[][] keys = genKeys(k); 

        //sub keys of k
        char[][] kSubKeys = KeyGen.genKeys(k);

        char[][] pRounds = DES.encrypt0(p, kSubKeys);

        //[des][altered plainText][roundNum][bit]
        char[][][][] desRoundsPi = new char[4][64][18][64];

        //[des][altered key][roundNum][bit]
        char[][][][] desRoundsKi = new char[4][56][18][64];

        //fills desRoundsPi
        for(int i = 0; i < 64; i++)
        {
            desRoundsPi[0][i] = DES.encrypt0(plainTexts[i], kSubKeys);
            desRoundsPi[1][i] = DES.encrypt1(plainTexts[i], kSubKeys);
            desRoundsPi[2][i] = DES.encrypt2(plainTexts[i], kSubKeys);
            desRoundsPi[3][i] = DES.encrypt3(plainTexts[i], kSubKeys);
        }

        //fills desRoundsKi
        for(int i = 0; i < 56; i++)
        {
            desRoundsKi[0][i] = DES.encrypt0(p, KeyGen.genKeys(keys[i]));
            desRoundsKi[1][i] = DES.encrypt1(p, KeyGen.genKeys(keys[i]));
            desRoundsKi[2][i] = DES.encrypt2(p, KeyGen.genKeys(keys[i]));
            desRoundsKi[3][i] = DES.encrypt3(p, KeyGen.genKeys(keys[i]));
        }

        int[][] averageDifs = new int[8][17];

        //for each des
        for(int o = 0; o < 4; o++)
        {
            //for each round
            for(int i = 0; i < 17; i++)
            {
                int difs = 0;
                //for each bit
                for(int z = 0; z < 64; z++)
                {
                    difs += DES.compare(desRoundsPi[o][z][i], pRounds[i]);
                }
                averageDifs[o][i] = difs / 64;
            }
        }

        //for each des
        for(int o = 0; o < 4; o++)
        {
            //for each round
            for(int i = 0; i < 17; i++)
            {
                int difs = 0;
                //for each bit
                for(int z = 0; z < 56; z++)
                {
                    difs += DES.compare(desRoundsKi[o][z][i], pRounds[i]);
                }
                averageDifs[o + 4][i] = difs / 56;
            }
        }

        //printing to file
        try
        {
            File output = new File("output.txt");
            BufferedWriter w = new BufferedWriter(new FileWriter(output, true));

            w.newLine();
            w.append("ENCRYPTION");
            w.newLine();
            w.append("Plaintext P: " + String.valueOf(p));
            w.newLine();
            w.append("Key K: " + String.valueOf(k64));
            w.newLine();
            w.append("Ciphertext C: " + String.valueOf(pRounds[17]));
            w.newLine();
            w.newLine();
            w.append("Avalanche:");
            w.newLine();
            w.newLine();
            w.append("P and Pi under K");
            w.newLine();
            w.append("Round     DES0      DES1      DES2      DES3");

            for(int i = 0; i < 17; i++)
            {
                w.newLine();
                w.append("  " + String.format("%2d", i) + "       " + 
                String.format("%2d", averageDifs[0][i]) + "        " + 
                String.format("%2d", averageDifs[1][i]) + "        " + 
                String.format("%2d", averageDifs[2][i]) + "        " + 
                String.format("%2d", averageDifs[3][i]));
            }  

            w.newLine();
            w.newLine();
            w.append("P under K and Ki");
            w.newLine();
            w.append("Round     DES0      DES1      DES2      DES3");

            for(int i = 0; i < 17; i++)
            {
                w.newLine();
                w.append("  " + String.format("%2d", i) + "       " + 
                String.format("%2d", averageDifs[4][i]) + "        " + 
                String.format("%2d", averageDifs[5][i]) + "        " + 
                String.format("%2d", averageDifs[6][i]) + "        " + 
                String.format("%2d", averageDifs[7][i]));
            }
            
            w.close();
        }
        catch(Exception e)
        {
            System.out.println("it brokey");
        }
    }

    //generates the plaintexts that differ from p by 1 bit
    private static char[][] genPlainTexts(char[] p)
    {
        char[][] plainTexts = new char[64][64];
        for(int i = 0; i < 64; i++)
        {
            plainTexts[i] = String.valueOf(p).toCharArray();
            if(plainTexts[i][i] == '0')
            {
                plainTexts[i][i] = '1';
            }
            else
            {
                plainTexts[i][i] = '0';
            }
        }
        return plainTexts;
    }

    //generates the keys that differ from k by 1 bit
    private static char[][] genKeys(char[] k)
    {
        char[][] keys = new char[56][56];
        for(int i = 0; i < 56; i++)
        {
            keys[i] = String.valueOf(k).toCharArray();
            if(keys[i][i] == '0')
            {
                keys[i][i] = '1';
            }
            else
            {
                keys[i][i] = '0';
            }
        }
        return keys;
    }
}

