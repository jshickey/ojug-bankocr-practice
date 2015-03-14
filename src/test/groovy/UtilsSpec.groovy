import spock.lang.*

class UtilSpec extends Specification {

	void "test reading a file in the classpath"() {
		given:
		def util = new Util()
		String fileName = '123456789.ocr'

		when:
		File f = util.stringToClassPathFile(fileName) 

		then:
		assert f
		assert f.readLines().size() == 4 
	}
}