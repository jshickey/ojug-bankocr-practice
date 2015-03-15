
class ErrorCorrectionUtil {
	Util util = new Util()
	BankOcrConverter converter = new BankOcrConverter()
	
	static decToOcrMap = BankOcrConverter.numberMap.collectEntries {k,v -> [(v):k]}
	
	
	List<String> findAlternates(String testAccount) {
		// dec -> ocr -> replace each char with missing one -> collect if valid digit -> try check sum on new number
		return ['711111111']
	}
	
	
	List<String> findValidAlternateAccounts(acctNum, pos = 0, acc = []) {
		if (pos == acctNum.size()) return acc.flatten()
		def validAltAccts = findAlternateNumbers(decToOcrMap[acctNum[pos]]).collect {n -> util.replaceChar(pos, n , acctNum)}
													   .findAll {altAcct -> converter.performChecksum(altAcct)}
		findValidAlternateAccounts(acctNum, pos + 1, validAltAccts ? acc << validAltAccts : acc  )
	}
	
	List<String> findAlternateNumbers(String ocrDigit, pos = 0, altNums = []) {
		if (pos == ocrDigit.size()) return altNums.flatten()
		def possibleChars = ['|',' ','_'] - ocrDigit[pos]
		def possibleNums = possibleChars.collect { c -> util.replaceChar(pos, c , ocrDigit) }
		             .findAll { it }
					 .collect { BankOcrConverter.numberMap[it] }
					 .findAll {it}
		findAlternateNumbers(ocrDigit, pos + 1, possibleNums ? altNums << possibleNums : altNums)
	}
}
