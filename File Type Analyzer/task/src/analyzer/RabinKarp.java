package analyzer;

public class RabinKarp {
    private static long charToLong(char ch) {
        return (ch - '!' + 1);
    }

    public static boolean search(String text, String pattern) {
        int a = 101;
        long m = 1_000_000_000 + 9;
        long patternHash = 0;
        long currSubstrHash = 0;
        long pow = 1;

        for (int i = 0; i < pattern.length(); i++) {
            patternHash += charToLong(pattern.charAt(i)) * pow;
            patternHash %= m;

            currSubstrHash += charToLong(text.charAt(text.length() - pattern.length() + i)) * pow;
            currSubstrHash %= m;

            if (i != pattern.length() - 1) {
                pow = pow * a % m;
            }
        }

        for (int i = text.length(); i >= pattern.length(); i--) {
            if (patternHash == currSubstrHash) {
                boolean patternIsFound = true;
                for (int j = 0; j < pattern.length(); j++) {
                    if (text.charAt(i - pattern.length() + j) != pattern.charAt(j)) {
                        patternIsFound = false;
                        break;
                    }
                }

                if (patternIsFound) {
                    return true;
                }
            }
            if (i > pattern.length()) {
                currSubstrHash = ((currSubstrHash - charToLong(text.charAt(i - 1)) * pow % m + m) * a % m + m) % m;
                currSubstrHash = (currSubstrHash + charToLong(text.charAt(i - pattern.length() - 1))) % m;
            }
        }

        return false;
    }
}
