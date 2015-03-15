import spock.lang.*

class ErrorCorrectionUtilSpec extends Specification {
	
	ErrorCorrectionUtil ecUtil = new ErrorCorrectionUtil()
	BankOcrConverter converter = new BankOcrConverter()
	
	void "test converting all ones to 7 + all ones"() {
		given:
		def testAccount = '111111111'
		
		when:
		def result = ecUtil.findAlternates(testAccount)
		
		then:
		assert '711111111' == result.first()
	}

		
	void "test converting first position"() {
		given:
		def testAccount = '111111111'
		
		when:
		def result = ecUtil.findValidAlternateAccounts(testAccount)
			
		then:
		assert '711111111' == result.first()
	}

	void "test converting alternate position"() {
		given:
		def testAccount = '777777777'
		
		when:
		def result = ecUtil.findValidAlternateAccounts(testAccount)
			
		then:
		assert '777777177' == result.first()
	}
	
	@Unroll
	void "test converting invalid acct #accountNumber to #validAltAccts"() {
		expect:
		def result = ecUtil.findValidAlternateAccounts(accountNumber)
		result.each { assert validAltAccts.contains(it) }
		
		where:
		accountNumber | validAltAccts
		'111111111' | ['711111111']
		'777777777' | ['777777177']
		'888888888' | ['888886888','888888880','888888988']
		'555555555' | ['555655555', '559555555']
		'666666666' | ['666566666', '686666666']
		'999999999' | ['899999999', '993999999', '999959999']
		'490067715' | ['490067115', '490067719', '490867715']
		//'?23456789' | ['123456789']

	}


	void "test finding alternate numbers for an OCR 1 by replacing various characters"() {
		given: 
		def n = '1'
		
		when :
		def ocrString = ecUtil.decToOcrMap[n]
		def altNums = ecUtil.findAlternateNumbers(ocrString)

		then :
		assert altNums.contains("7")
		
		
	}

}
