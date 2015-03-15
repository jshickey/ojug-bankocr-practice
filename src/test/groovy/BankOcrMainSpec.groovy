import java.util.List;

import spock.lang.*

// Your first task is to write a program that can take this file and parse it into actual account numbers.

class BankOcrMainSpec extends Specification {

    BankOcrConverter converter = new BankOcrConverter()
	Util util = new Util()
	
    def asLinesFromFile = {String s -> s.readLines().collect{ it.padRight(27) }}

	void "test read account numbers from file"() {
		given:
		String f = 'use-case-three.txt'
		
		when:
		def report = converter.readAcctNumbersFromFile(f)
		
		then:
		assert '000000051' == report[0]
		assert '49006771? ILL' == report[1]
	    assert '1234?678? ILL' == report[2]		
	}
	
    @Unroll
    void "test added status column to account number for #acctNumber : #acctWithStatus"() {
        expect:
        String result = converter.composeAccountWithStatus(acctNumber) 
        assert acctWithStatus == result
        
        where:
        acctNumber  | acctWithStatus
        '457508000' | '457508000' 
        '664371495' | '664371495 ERR'
        '86110??36' | '86110??36 ILL'
    }

    @Unroll
    void "test check sum #accountNumber is #valid "() {
        expect:
        def isValid = converter.performChecksum(accountNumber)
        assert valid == isValid

        where:
        accountNumber | valid
        '711111111'   | true
        '123456789'   | true
        '490867715'   | true
        '888888888'   | false
        '490067715'   | false
        '012345678'   | false
    }

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

    void "test ocr number zero to decimal number conversion"() {
        given:
        def ocrNumber = ' _ | ||_|'

        when:
        def decNumber = converter.ocrToDec(ocrNumber)

        then:
        assert decNumber == '0'

    }


    void "test reading an ocr file with one account file"() {
        given:
        def lines = asLinesFromFile ONE_TO_NINE

        when:
        //def lines = testFile.readLines()
        def acctNumber = converter.ocrToDec(converter.convertLinesToOcrDigits(lines))
        
        then:
        assert lines.size() == 4
        assert '123456789'== acctNumber
    }

    void "test reading an ocr account with triple quoted string"() {
        given:
        List<String> lines = asLinesFromFile ONES 

        when:
        String acctNumber = converter.readAcctNumberFromLines(lines)
        
        then:
        assert lines.size() == 4
        assert lines.every{ it.size() == 27}
        assert '111111111'== acctNumber
    }


    @Unroll
    void "Convert OCR Digit version of #expectedAccountNumber"() {
        expect: "#expectedAccountNumber"
        List<String> lines = asLinesFromFile(filename)
        def acctNum = converter.readAcctNumberFromLines(lines)
        assert expectedAccountNumber == acctNum

        where: 'The test file:#filename contains the OCR Digit version of #expectedAccountNumber'
        filename | expectedAccountNumber
        ZEROES   | '000000000'
        ONES     | '111111111'
        TWOS     | '222222222'
        THREES   | '333333333'
        FOURS    | '444444444'
        FIVES[0] | FIVES[1]
        SIXES[0] | SIXES[1]
        /*
        'ocr-777777777.txt' | '777777777'
        'ocr-888888888.txt' | '888888888'
        'ocr-999999999.txt' | '999999999'
        'ocr-123456789.txt' | '123456789'
        */
    }

    void "test converting lines of ocr digits into a list of decimal numbers"() {
        given:
        // a list call [1..9] on OCR Digit to get back list of strings
        
        def testFile = asLinesFromFile ONE_TO_NINE
        when:
        def ocrStrings = converter.convertLinesToOcrDigits(testFile)

        then:
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
        def testFile = asLinesFromFile ONE_TO_NINE
        when:
        def ocrStrings = converter.convertLinesToOcrDigits(testFile)

        then:
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
        assert ocrParts.size() == 9
        assert ocrParts.every { it.size() == 3}
        assert ocrParts[0] == '111'
        assert ocrParts[8] == '999'
    }

static ONE_TO_NINE = """\
    _  _     _  _  _  _  _
  | _| _||_||_ |_   ||_||_|
  ||_  _|  | _||_|  ||_| _| 
                           
"""

    static ZEROES = """\
 _  _  _  _  _  _  _  _  _ 
| || || || || || || || || |
|_||_||_||_||_||_||_||_||_|

"""                           
    static ONES = """\

  |  |  |  |  |  |  |  |  |
  |  |  |  |  |  |  |  |  |

"""

    static TWOS = """\
 _  _  _  _  _  _  _  _  _ 
 _| _| _| _| _| _| _| _| _|
|_ |_ |_ |_ |_ |_ |_ |_ |_ 

    """
    static THREES = """\
 _  _  _  _  _  _  _  _  _ 
 _| _| _| _| _| _| _| _| _|
 _| _| _| _| _| _| _| _| _|

"""

static FOURS = """\

|_||_||_||_||_||_||_||_||_|
  |  |  |  |  |  |  |  |  |

"""

static FIVES = ["""\
 _  _  _  _  _  _  _  _  _ 
|_ |_ |_ |_ |_ |_ |_ |_ |_ 
 _| _| _| _| _| _| _| _| _|

""", '555555555']

    static SIXES = ["""\
 _  _  _  _  _  _  _  _  _ 
|_ |_ |_ |_ |_ |_ |_ |_ |_ 
|_||_||_||_||_||_||_||_||_|

""", '666666666']
/*
 _  _  _  _  _  _  _  _  _ 
  |  |  |  |  |  |  |  |  |
  |  |  |  |  |  |  |  |  |
                           
=> 777777777
 _  _  _  _  _  _  _  _  _ 
|_||_||_||_||_||_||_||_||_|
|_||_||_||_||_||_||_||_||_|
                           
=> 888888888
 _  _  _  _  _  _  _  _  _ 
|_||_||_||_||_||_||_||_||_|
 _| _| _| _| _| _| _| _| _|
                           
=> 999999999
    _  _     _  _  _  _  _
  | _| _||_||_ |_   ||_||_|
  ||_  _|  | _||_|  ||_| _| 
                           
=> 123456789
*/
}