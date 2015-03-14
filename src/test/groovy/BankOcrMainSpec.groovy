import spock.lang.*

// Your first task is to write a program that can take this file and parse it into actual account numbers.

class BankOcrMainSpec extends Specification {

	BankOcrConverter converter = new BankOcrConverter()


	void "test reading a real testFile ocr file with one account file"() {
		given:
		def fileName = '123456789.ocr'
	
		when:
		def acctNumber = converter.readAcctNumberFromFile(fileName)
		
		then:
		assert '123456789'== acctNumber
	}

	void "test converting a collection of ocr digits to list decimal numbers"() {
		given: 
		List<String> ocrAcctNumber = [converter.ONE, converter.TWO, converter.THREE]

		when:
		def decAcctNumber = converter.ocrToDec(ocrAcctNumber)

		then:
		assert '123' == decAcctNumber
	}

	void "test ocr number one to decimal number conversion"() {
		given:
		def ocrNumber = '     |  |'

		when:
		def decNumber = converter.ocrToDec(ocrNumber)

		then:
		assert decNumber == '1'

	}

	void "test ocr number two to decimal number conversion"() {
		given:
		def ocrNumber = ' _  _||_ '

		when:
		def decNumber = converter.ocrToDec(ocrNumber)

		then:
		assert decNumber == '2'

	}

	void "test reading an ocr file with one account file"() {
		given:
		def testFile = 	"    _  _     _  _  _  _  _ \n" +
						"  | _| _||_||_ |_   ||_||_|\n" +
						"  ||_  _|  | _||_|  ||_| _|\n" +
						"                           \n"

		when:
		def lines = testFile.readLines()
		def acctNumber = converter.ocrToDec(converter.convertLinesToOcrDigits(lines))
		
		then:
		assert lines.size() == 4
		assert '123456789'== acctNumber
	}

	void "test converting lines of ocr digits into a list of decimal numbers"() {
		given:
		// a list call [1..9] on OCR Digit to get back list of strings
		
		def testFile = 	"    _  _     _  _  _  _  _ \n" +
						"  | _| _||_||_ |_   ||_||_|\n" +
						"  ||_  _|  | _||_|  ||_| _|\n" +
						"                           \n"
		when:
		def ocrStrings = converter.convertLinesToOcrDigits(testFile.readLines())

		then:
		println ocrStrings
		assert ocrStrings.size() == 9
		assert ocrStrings.every { it.size() == 9 }
		assert ocrStrings[0] ==['   ',
		                        '  |',
		                        '  |'].join()
		assert ocrStrings[8] == [' _ ',
		                         '|_|',
		                         ' _|'].join()

	}

	void "test converting lines of text into a collection of 9 OCR strings"() {
		given:
		def testFile = 	"    _  _     _  _  _  _  _ \n" +
						"  | _| _||_||_ |_   ||_||_|\n" +
						"  ||_  _|  | _||_|  ||_| _|\n" +
						"                           \n"
		when:
		def ocrStrings = converter.convertLinesToOcrDigits(testFile.readLines())

		then:
		println ocrStrings
		assert ocrStrings.size() == 9
		assert ocrStrings.every { it.size() == 9 }
		assert ocrStrings[0] ==['   ',
		                        '  |',
		                        '  |'].join()
		assert ocrStrings[8] == [' _ ',
		                         '|_|',
		                         ' _|'].join()

	}

	void "test breaking an ocr line into 9, 3-character parts"() {
		given:
		def line = "111222333444555666777888999"

		when:
		def ocrParts = converter.splitLineIntoOcrParts(line) 

		then:
		println ocrParts
		assert ocrParts.size() == 9
		assert ocrParts.every { it.size() == 3}
		assert ocrParts[0] == '111'
		assert ocrParts[8] == '999'
	}
}