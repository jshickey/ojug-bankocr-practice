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
        def testFile =  "    _  _     _  _  _  _  _ \n" +
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

    void "test reading an ocr account with triple quoted string"() {
        given:
        List<String> lines = ONES.readLines().drop(1).collect{ it.padRight(27) }

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
        List<String> lines = filename.readLines().drop(1).collect{ it.padRight(27) }
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
        
        def testFile =  "    _  _     _  _  _  _  _ \n" +
                        "  | _| _||_||_ |_   ||_||_|\n" +
                        "  ||_  _|  | _||_|  ||_| _|\n" +
                        "                           \n"
        when:
        def ocrStrings = converter.convertLinesToOcrDigits(testFile.readLines())

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
        def testFile =  "    _  _     _  _  _  _  _ \n" +
                        "  | _| _||_||_ |_   ||_||_|\n" +
                        "  ||_  _|  | _||_|  ||_| _|\n" +
                        "                           \n"
        when:
        def ocrStrings = converter.convertLinesToOcrDigits(testFile.readLines())

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

static ZEROES = """
 _  _  _  _  _  _  _  _  _ 
| || || || || || || || || |
|_||_||_||_||_||_||_||_||_|

"""                           
static ONES = """

  |  |  |  |  |  |  |  |  |
  |  |  |  |  |  |  |  |  |
                           
"""

static TWOS = """
 _  _  _  _  _  _  _  _  _ 
 _| _| _| _| _| _| _| _| _|
|_ |_ |_ |_ |_ |_ |_ |_ |_ 
                           
"""
static THREES = """
 _  _  _  _  _  _  _  _  _ 
 _| _| _| _| _| _| _| _| _|
 _| _| _| _| _| _| _| _| _|
                           
"""

static FOURS = """
                           
|_||_||_||_||_||_||_||_||_|
  |  |  |  |  |  |  |  |  |
                           
"""

static FIVES = ["""
 _  _  _  _  _  _  _  _  _ 
|_ |_ |_ |_ |_ |_ |_ |_ |_ 
 _| _| _| _| _| _| _| _| _|
                           
""", '555555555']

static SIXES = ["""
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