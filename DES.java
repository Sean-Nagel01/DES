/*
    DES.java
    By Kelsey Baker 3302479
    and Sean Nagel 3356603
    created 27/05/2021
    for comp3260 assignment 2
*/
public class DES
{
    //initial permutation
    private static int[] ip =
    {
        58, 50, 42, 34, 26, 18, 10, 2,
        60, 52, 44, 36, 28, 20, 12, 4,
        62, 54, 46, 38, 30, 22, 14, 6,
        64, 56, 48, 40, 32, 24, 16, 8,
        57, 49, 41, 33, 25, 17, 9, 1,
        59, 51, 43, 35, 27, 19, 11, 3,
        61, 53, 45, 37, 29, 21, 13, 5,
        63, 55, 47, 39, 31, 23, 15, 7
    };

    //48 bit
    private static int[] expansionBox =
    {
        32, 1, 2, 3, 4, 5, 4, 5,
        6, 7, 8, 9, 8, 9, 10, 11,
        12, 13, 12, 13, 14, 15, 16, 17,
        16, 17, 18, 19, 20, 21, 20, 21,
        22, 23, 24, 25, 24, 25, 26, 27,
        28, 29, 28, 29, 30, 31, 32, 1
    };

    //the inverse of the expansion box
    private static int[] inverseExpansionBox = 
    {
        1, 32, 31, 30, 29, 28, 29, 28,
        27, 26, 25, 24, 25, 24, 23, 22,
        21, 20, 21, 20, 19, 18, 17, 16,
        17, 16, 15, 14, 13, 12, 13, 12,
        11, 10, 9, 8, 9, 8, 7, 6,
        5, 4, 5, 4, 3, 2, 1, 32
    };

    //the S-Boxes. indices in the below for,
    //[boxNum][y][x]
    private static int[][][] sboxes = 
    {
        //box 0
        {
            { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
            { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
            { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
            { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } 
        },

        //box 1
        { 
            { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
            { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
            { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
            { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } 
        },

        //box 2
        { 
            { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
            { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
            { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
            { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 } 
        },

        //box 3
        { 
            { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
            { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
            { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
            { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 } 
        },

        //box 4
        { 
            { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
            { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
            { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
            { 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 } 
        },

        //box 5
        { 
            { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
            { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
            { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
            { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 } 
        },

        //box 6
        { 
            { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
            { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
            { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
            { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 } 
        },

        //box 7
        { 
            { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
            { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
            { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
            { 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } 
        }
    };

    //invers of the initial permututation
    private static int[] ipInverse =
    { 
        40,8,48,16,56,24,64,32, 
        39,7,47,15,55,23,63,31, 
        38,6,46,14,54,22,62,30, 
        37,5,45,13,53,21,61,29, 
        36,4,44,12,52,20,60,28, 
        35,3,43,11,51,19,59,27, 
        34,2,42,10,50,18,58,26, 
        33,1,41,9,49,17,57,25 
    };

    //binary values of the numbers 0 - 15
    private static String[] biValues = 
    {
        "0000",
        "0001",
        "0010",
        "0011",
        "0100",
        "0101",
        "0110",
        "0111",
        "1000",
        "1001",
        "1010",
        "1011",
        "1100",
        "1101",
        "1110",
        "1111"
    };

    //permutation box
    private static int pBox[] = 
    { 
        16,7,20,21,29,12,28,17, 
        1,15,23,26,5,18,31,10, 
        2,8,24,14,32,27,3,9,
        19,13,30,6,22,11,4,25
    };

    //run plaintext through ip
    public static char[] ip(char[] plainText)
    {
        char[] result = new char[64];
        for(int i = 0; i < 64; i++)
        {
            result[i] = plainText[ip[i] - 1];
        }
        return result;
    }

    //run plaintext through expansionBox
    public static char[] expand(char[] arr)
    {
        char[] result = new char[48];
        for(int i = 0; i < 48; i++)
        {
            result[i] = arr[expansionBox[i] - 1];
        }
        return result;
    }

    //run plaintext through inverseExpansionBox
    public static char[] inverseExpand(char[] arr)
    {
        char[] result = new char[48];
        for(int i = 0; i < 48; i++)
        {
            result[i] = arr[inverseExpansionBox[i] - 1];
        }
        return result;
    }

    //S-Box tomfoolery found here
    private static char[] subChoice(char[] r)
    {
        //chunks for sbox gymnastics
        //we split r into 6 bit chunks for sBox processing
        char[][] chunks = new char[8][6];

        //index of r
        int idx = 0;

        //this loop fills chunks with data from r
        //for each chunk
        for(int i = 0; i < 8; i++)
        {
            //for each bit in a chunk
            for(int z = 0; z < 6; z++)
            {
                chunks[i][z] = r[idx];
                idx++;
            }
        }

        //run each chunk through their respective sbox
        //for each chunk
        for(int i = 0; i < 8; i++)
        {
            String rowBinary = String.valueOf(chunks[i][0]) + String.valueOf(chunks[i][5]);
            int row = Integer.parseInt(rowBinary, 2);

            String columnBinary = String.valueOf(chunks[i][1]) + String.valueOf(chunks[i][2]) + String.valueOf(chunks[i][3]) + String.valueOf(chunks[i][4]);
            int column = Integer.parseInt(columnBinary, 2);

            chunks[i] = biValues[sboxes[i][row][column]].toCharArray();
        }

        //the array to be returned
        char[] result = new char[32]; 
        idx = 0;

        //reasseble processed chunks into a single array
        //for each chunk
        for(int i = 0; i < 8; i++)
        {
            //for each bit in a chunk
            for(int z = 0; z < 4; z++)
            {
                result[idx] = chunks[i][z];
                idx++;
            }
        }

        return result;
    }

    //runs r through pBox
    public static char[] permute(char[] r)
    {
        char[] result = new char[32];
        for(int i = 0; i < 32; i++)
        {
            result[i] = r[pBox[i] - 1];
        }
        return result;
    }

    //run plaintext through ipInverse
    public static char[] ipInverse(char[] cipherText)
    {
        char[] result = new char[64];
        for(int i = 0; i < 64; i++)
        {
            result[i] = cipherText[ipInverse[i] - 1];
        }
        return result;
    }

    //splits a char array into 2 halves.
    //half1 at result[0] half2 at result[1]
    public static char[][] split(char[] arr)
    {
        char[][] result = new char[2][arr.length / 2];

        for(int i = 0; i < result[0].length; i++)
        {
            result[0][i] = arr[i];
            result[1][i] = arr[i + result[0].length];
        }
        return result;
    }

    //combines two char arrays into one 
    public static char[] combine(char[] l, char[] r)
    {
        char[] result = new char[64];
        for(int i = 0; i < 32; i++)
        {
            result[i] = r[i];
            result[i + 32] = l[i];
        }
        return result;
    }

    //XORs two char arrays
    public static char[] XOR(char[] text, char[] key)
    {
        char[] result = new char[text.length];
        for(int i = 0; i < result.length; i++)
        {
            //if they match
            if(text[i] == key[i])
            {
                result[i] = '0';
            }
            else
            {
                result[i] = '1';
            }
        }
        return result;
    }

    //compares two bit arrays and returns how many bits they differ by 
    public static int compare(char[] a, char[] b)
    {
        int dif = 0;
        for(int i = 0; i < a.length; i++)
        {
            if(a[i] != b[i])
            {
                dif++;
            }
        }
        return dif;
    }

    //encrypts with des0
    public static char[][] encrypt0(char[] plainText, char[][] keys)
    {
        char[][] results = new char[18][64];

        char[] text = ip(plainText);
        char[][] halves = split(text);

        //initial l
        char[] l = halves[0];
        //initial r
        char[] r = halves[1];

        //round 0 text
        results[0] = combine(l, r);

        for(int i = 0; i < 16; i++)
        {
            char[] oldR = r;
            r = des0(r, keys[i]);
            r = XOR(l, r);
            l = oldR;

            //round i+1 text
            results[i + 1] = combine(l, r);
        }

        //the ciphertext
        results[17] = ipInverse(combine(l, r));

        return results;
    }

    //encrypts with des2
    public static char[][] encrypt1(char[] plainText, char[][] keys)
    {
        char[][] results = new char[18][64];

        char[] text = ip(plainText);
        char[][] halves = split(text);

        //initial l
        char[] l = halves[0];
        //initial r
        char[] r = halves[1];

        //round 0 text
        results[0] = combine(l, r);

        for(int i = 0; i < 16; i++)
        {
            char[] oldR = r;
            r = des1(r, keys[i]);
            r = XOR(l, r);
            l = oldR;

            //round i+1 text
            results[i + 1] = combine(l, r);
        }

        //the ciphertext
        results[17] = ipInverse(combine(l, r));

        return results;
    }

    //encrypts with des2
    public static char[][] encrypt2(char[] plainText, char[][] keys)
    {
        char[][] results = new char[18][64];

        char[] text = ip(plainText);
        char[][] halves = split(text);

        //initial l
        char[] l = halves[0];
        //initial r
        char[] r = halves[1];

        //round 0 text
        results[0] = combine(l, r);

        for(int i = 0; i < 16; i++)
        {
            char[] oldR = r;
            r = des2(r, keys[i]);
            r = XOR(l, r);
            l = oldR;

            //round i+1 text
            results[i + 1] = combine(l, r);
        }

        //the ciphertext
        results[17] = ipInverse(combine(l, r));

        return results;
    }

    //encrypts with des3
    public static char[][] encrypt3(char[] plainText, char[][] keys)
    {
        char[][] results = new char[18][64];

        char[] text = ip(plainText);
        char[][] halves = split(text);

        //initial l
        char[] l = halves[0];
        //initial r
        char[] r = halves[1];

        //round 0 text
        results[0] = combine(l, r);

        for(int i = 0; i < 16; i++)
        {
            char[] oldR = r;
            r = des3(r, keys[i]);
            r = XOR(l, r);
            l = oldR;

            //round i+1 text
            results[i + 1] = combine(l, r);
        }

        //the ciphertext
        results[17] = ipInverse(combine(l, r));

        return results;
    }

    //decrypts with des0
    public static char[] decrypt0(char[] cipherText, char[][] keys)
    {
        char[] result = new char[64];

        char[] text = ip(cipherText);
        char[][] halves = split(text);

        //initial l
        char[] l = halves[0];
        //initial r
        char[] r = halves[1];

        for(int i = 0; i < 16; i++)
        {
            char[] oldR = r;
            r = des0(r, keys[15 - i]);
            r = XOR(l, r);
            l = oldR;
        }

        //the ciphertext
        result = ipInverse(combine(l, r));

        return result;
    }

    //the f function for des0
    public static char[] des0(char[] r, char[] key)
    {
        //expand r to 48 bits
        char[] result = expand(r);

        //XOR result with key
        result = XOR(result, key);

        //run result through s boxes
        result = subChoice(result);

        //permute result with pBox
        result = permute(result);

        return result;
    }

    //the f function for des1
    public static char[] des1(char[] r, char[] key)
    {
        //expand r to 48 bits
        char[] result = expand(r);

        //run result through s boxes
        result = subChoice(result);

        //permute result with pBox
        result = permute(result);

        return result;
    }

    //the f function for des2
    public static char[] des2(char[] r, char[] key)
    {
        //expand r to 48 bits
        char[] result = expand(r);

        //XOR result with key
        result = XOR(result, key);

        //run result through inverseExpansionBox
        result = inverseExpand(result);

        return result;
    }

    //the f function for des3
    public static char[] des3(char[] r, char[] key)
    {
        //expand r to 48 bits
        char[] result = expand(r);

        //XOR result with key
        result = XOR(result, key);

        //run result through s boxes
        result = subChoice(result);

        return result;
    }
}
