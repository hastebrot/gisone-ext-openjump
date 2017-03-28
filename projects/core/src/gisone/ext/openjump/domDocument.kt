package gisone.ext.openjump

import kotlinx.dom.appendText
import kotlinx.dom.build.addElement
import kotlinx.dom.children
import kotlinx.dom.createDocument
import kotlinx.dom.elements
import kotlinx.dom.toXmlString
import org.w3c.dom.Document
import javax.xml.bind.JAXBContext
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlValue
import javax.xml.transform.OutputKeys

fun main(args: Array<String>) {
    val doc = javaXmlDocument()
    javaXmlProcessing(doc)
    javaXmlBinding(doc)
}

fun javaXmlDocument(): Document {
    val doc = createDocument().apply {
        addElement("customer") {
            setAttribute("first-name", "Jane")
            setAttribute("last-name", "Doe")
            addElement("address") {
                addElement("street") {
                    appendText("123 A Street")
                }
                addElement("phone-number") {
                    setAttribute("type", "work")
                    appendText("555-1111")
                }
                addElement("phone-number") {
                    setAttribute("type", "cell")
                    appendText("555-2222")
                }
            }
        }
    }

    val xml = doc.toPrettyXmlString()
    println(xml)

    return doc
}

fun javaXmlProcessing(doc: Document) {
    println(doc.elements("customer")[0].getAttribute("first-name"))
    println(doc.elements("phone-number").map { it.children()[0].textContent })
    println(doc.elements("street")[0].textContent)
//    println(doc.elements("phone-number")[0].toXmlString())
}

fun javaXmlBinding(doc: Document) {
    val unmarshaller = JAXBContext.newInstance(Customer::class.java)
        .createUnmarshaller()
    val customer = unmarshaller.unmarshal(doc.toXmlString().reader()) as Customer

    println(customer)
    println(customer.address)
    println(customer.address?.phoneNumbers)
}

@XmlRootElement
data class Customer(
    @XmlAttribute(name = "first-name") val firstName: String? = null,
    @XmlAttribute(name = "last-name") val lastName: String? = null
) {
    @XmlElement val address: Address? = null
}

@XmlAccessorType(XmlAccessType.FIELD)
data class Address(
    @XmlElement val street: String? = null
) {
    @XmlElement(name = "phone-number") val phoneNumbers = mutableListOf<PhoneNumber>()
}

@XmlAccessorType(XmlAccessType.FIELD)
data class PhoneNumber(
    @XmlAttribute val type: String? = null,
    @XmlValue val value: String? = null
)

private object OutputKeysExt {
    val INDENT_AMOUNT = "{http://xml.apache.org/xslt}indent-amount"
}

private fun Document.toPrettyXmlString(): String {
    return toXmlString(mapOf(
        OutputKeys.OMIT_XML_DECLARATION to "yes",
        OutputKeys.INDENT to "yes",
        OutputKeysExt.INDENT_AMOUNT to "2"
    ))
}
