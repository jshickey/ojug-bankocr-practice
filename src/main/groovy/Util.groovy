class Util {
    File stringToClassPathFile(String fileName) {
        new File(this.getClass().getResource(fileName).file as String)
    }

    String replaceChar(int index, String c, String illegibleNumber) {
        def s = illegibleNumber.getChars()
        s[index] = c[0]
        return s as String
    }
}