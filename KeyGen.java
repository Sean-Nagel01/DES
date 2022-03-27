/*
    KeyGen.java
    By Kelsey Baker 3302479
    and Sean Nagel 3356603
    created 27/05/2021
    for comp3260 assignment 2
*/
public class KeyGen
{
    //56 bit
    static int[] permChoice1 =
    {
        57,49,41,33,25,17,9,   
        1,58,50,42,34,26,18,   
        10,2,59,51,43,35,27,   
        19,11,3,60,52,44,36,             
        63,55,47,39,31,23,15,   
        7,62,54,46,38,30,22,   
        14,6,61,53,45,37,29,   
        21,13,5,28,20,12,4
    };

    //48 bit
    static int[] permChoice2 = 
    {
        14,17,11,24,1,5,   
        3,28,15,6,21,10,   
        23,19,12,4,26,8,   
        16,7,27,20,13,2,   
        41,52,31,37,47,55,   
        30,40,51,45,33,48,   
        44,49,39,56,34,53,   
        46,42,50,36,29,32
    };

    //the shifting scheduel 
    static int[] lShiftSchedule = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};

    //shrinks a 64 bit key to 56 bits
    public static char[] to56(char[] key)
    {
        char[] result = new char[56];
        for(int i = 0; i < 56; i++)
        {
            result[i] = key[permChoice1[i] - 1];
        }
        return result;
    }

    //shrinks a 56 bit key to 48 bits
    public static char[] to48(char[] key)
    {
        char[] result = new char[48];
        for(int i = 0; i < 48; i++)
        {
            result[i] = key[permChoice2[i] - 1];
        }
        return result;
    }

    //shrinks an array of 16 56 bit keys to 48 bits
    public static char[][] to48(char[][] keys)
    {
        for(int i = 0; i < 16; i++)
        {
            keys[i] = to48(keys[i]);
        }
        return keys;
    }

    //splits an array into two arrays
    //half1 is at result[0], half2 at result[1]
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

    //shifts returns an array equal to arr shifted to the left by shift positions
    public static char[] leftShift(char[] arr, int shift)
    {
        char[] result = new char[arr.length];
        for(int i = 0; i < arr.length; i++)
        {
            result[i] = arr[(i + shift) % arr.length];
        }
        return result;
    }

    //used the generate all of the Cs and Ds
    public static char[][] genSubKeys(char[] arr)
    {
        char[][] subKeys = new char[16][28];

        subKeys[0] = leftShift(arr, lShiftSchedule[0]);
        for(int i = 1; i < 16; i++)
        {
            subKeys[i] = leftShift(subKeys[i - 1], lShiftSchedule[i]);
        }
        return subKeys;
    }

    //combines each key in C with the key in the corresponding position within D and returns these cobination in their own array
    public static char[][] combineKeys(char[][] C, char[][] D)
    {
        char[][] keys = new char[16][56];
        for(int z = 0; z < 16; z++)
        {
            for(int i = 0; i < 28; i++)
            {
                keys[z][i] = C[z][i];
                keys[z][i + 28] = D[z][i];
            }
        }
        return keys;
    }

    //generates all of the subkeys of the passed key
    public static char[][] genKeys(char[] key)
    {
        //split key
        char[][] halves = split(key);
        char[] c = halves[0];
        char[] d = halves[1];

        //generate sub keys (shifting)
        char[][] C = genSubKeys(c);
        char[][] D = genSubKeys(d);

        //combine keys and permute to 48 bits
        char[][] subKeys = to48(combineKeys(C, D));

        return subKeys;
    }
}
