class Util {
	File stringToClassPathFile(String fileName) {
		new File(this.getClass().getResource(fileName).file as String)
	}
}