package gisone.ext.openjump

import kotlinx.dom.appendText
import kotlinx.dom.build.addElement
import kotlinx.dom.children
import kotlinx.dom.createDocument
import kotlinx.dom.elements
import kotlinx.dom.toXmlString
import org.w3c.dom.Document
import javax.xml.transform.OutputKeys

fun main(args: Array<String>) {
    val doc = createDocument()

    doc.addElement("customer") {
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

    println(doc.toPrettyXmlString())
    println(doc.elements("customer")[0].getAttribute("first-name"))
    println(doc.elements("phone-number").map { it.children()[0].textContent })
    println(doc.elements("street")[0].textContent)
}

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
