class BankOcrConverter {
    Util util = new Util()

    static ZERO =  ' _ ' +
                   '| |' +
                   '|_|'
        
    static ONE = '   ' +
                 '  |' +
                 '  |'
        
    static TWO = ' _ ' +
                 ' _|' +
                 '|_ '
         
    static THREE = ' _ ' +
                   ' _|' +
                   ' _|'
          
    static FOUR = '   ' +
                  '|_|' +
                  '  |'
         
    static FIVE = ' _ ' +
                  '|_ ' +
                  ' _|'
         
    static SIX = ' _ ' +
                 '|_ ' +
                 '|_|'
    
    static SEVEN =  ' _ ' +
                    '  |' +
                    '  |'
           
    static EIGHT = ' _ ' +
                   '|_|' + 
                   '|_|'
        
    static NINE = ' _ ' +
                  '|_|' +
                  ' _|'     

    static numberMap = [ (ONE) :'1', (TWO) :'2', (THREE):'3',
                         (FOUR):'4', (FIVE):'5', (SIX):'6',
                         (SEVEN):'7', (EIGHT):'8',(NINE):'9', (ZERO):'0']

	
    String composeAccountWithStatus(String acctNumber) {
        if (acctNumber.contains('?')) {
            "$acctNumber ILL"
        } else if (performChecksum(acctNumber)) {
            acctNumber
        } else {
            "$acctNumber ERR"
        }
    }

    Boolean performChecksum(String acctNumber) {
        computeSum(acctNumber) % 11 == 0
    }

    def computeSum(acctNumber, acc = 0) {
        acctNumber ? computeSum(acctNumber.drop(1), acc + (acctNumber.size() * acctNumber.take(1).toInteger())) : acc
    }

	List<String> readAcctNumbersFromFile(String fileName, acc = []) {
		File testFile = util.stringToClassPathFile(fileName)
		List<String> acctNumbers =  readAcctNumbersFromLines(testFile.readLines())
		acctNumbers.collect { composeAccountWithStatus(it) }
	}

	List<String> readAcctNumbersFromLines(List<String> lines, acc = []) {
		lines ? readAcctNumbersFromLines(lines.drop(4), acc << 	ocrToDec(convertLinesToOcrDigits(lines.take(4))))
			  : acc
	}
	
	String readAcctNumberFromFile(String fileName) {
        File testFile = util.stringToClassPathFile(fileName)
        ocrToDec(convertLinesToOcrDigits(testFile.readLines()))
    }


    String readAcctNumberFromLines(List<String> lines) {
        ocrToDec(convertLinesToOcrDigits(lines))
    }

    String ocrToDec(List<String> ocrAcctNumber) {
        ocrAcctNumber.collect{ ocrToDec(it) }.join()
    }

    String ocrToDec(String ocrNumber) {
        return numberMap[ocrNumber] ?: '?'
    }

    /**
    *  convert ocr lines into 9, 9 character strings representing each OCR Digit
    */
    List<String> convertLinesToOcrDigits(List<String> lines) {
        lines
        .take(3)
        .collect { line ->
            splitLineIntoOcrParts(line)
        }.transpose().collect{ it.join()}
    }

    /**
      * Split a single line of ocr into 9, 3-character strings
      **/
    List<String> splitLineIntoOcrParts(line, accumulator = []) {
        line ? splitLineIntoOcrParts(line.drop(3), accumulator << line.take(3)) : accumulator 
    }
}