package com.jam_int.jt316_jt318_m8;

public class SubString {
    static class SubstringExtensions
    {
        /// <summary>
        /// Get string value between [first] a and [last] b.
        /// </summary>
        public static String Between(String value, String a, String b)
        {
            int posA = value.indexOf(a);
            int posB = value.lastIndexOf(b);
            if (posA == -1)
            {
                return "";
            }
            if (posB == -1)
            {
                return "";
            }
            int adjustedPosA = posA + a.length();
            if (adjustedPosA >= posB)
            {
                return "";
            }
            return value.substring(adjustedPosA, posB - adjustedPosA);
        }

        /// <summary>
        /// Get string value after [first] a.
        /// </summary>
        public static String Before(String value, String a)
        {
            int posA = value.indexOf(a);
            if (posA == -1)
            {
                return "";
            }
            return value.substring(0, posA);
        }


        public static String BeforeLast(String value, String a)
        {
            int posA = value.lastIndexOf(a);
            if (posA == -1)
            {
                return "";
            }
            return value.substring(0, posA);
        }
        /// <summary>
        /// Get string value after [last] a.
        /// </summary>
        public static String After(String value, String a)
        {
            int posA = value.lastIndexOf(a);
            if (posA == -1)
            {
                return "";
            }
            int adjustedPosA = posA + a.length();
            if (adjustedPosA >= value.length())
            {
                return "";
            }
            return value.substring(adjustedPosA);
        }
    }
}