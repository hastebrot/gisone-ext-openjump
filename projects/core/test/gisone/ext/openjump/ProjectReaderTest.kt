package gisone.ext.openjump

import com.winterbe.expekt.expect
import kotlinx.dom.childElements
import kotlinx.dom.elements
import kotlinx.dom.parseXml
import org.junit.jupiter.api.Test
import org.w3c.dom.Document

class ProjectReaderTest {
    val fixtureZeroLayers = resource("fixtures/project-zero-layers.jmp.xml")
    val reader = ProjectReader()

    @Test
    fun `read xml document`() {
        // given:
        val doc = parseXml(fixtureZeroLayers)

        // expect:
        expect(reader.readProject(doc)).to.equal(
            Project()
        )
        expect(reader.readCategories(doc)).to.equal(
            listOf(Category("Working"), Category("System"))
        )
        expect(reader.readProperties(doc)).to.equal(
            mapOf("Project File" to "/home/user/work/project-zero-layers.jmp")
        )
        expect(reader.readLayers(doc)).to.equal(
            emptyList()
        )
    }

    private fun resource(path: String) = javaClass.getResourceAsStream(path)!!
}

data class Project(val name: String? = null)
data class Category(val name: String? = null)
data class Layer(val name: String? = null)

class ProjectReader {
    fun readProject(doc: Document): Project {
        val project = doc.elements("project")
        return when (project.size) {
            1 -> Project()
            else -> TODO()
        }
    }

    fun readCategories(doc: Document): List<Category> {
        val project = doc.elements("project")[0]
        val layers = project.childElements("layers")[0]
        return layers.elements("category")
            .map { Category(name = it.getAttribute("name"))}
    }

    fun readProperties(doc: Document): Map<String, String> {
        val project = doc.elements("project")[0]
        val properties = project.childElements("properties")[0]
        val mappings = properties.childElements("mapping")
        return mappings.associate {
            it.childElements("key")[0].textContent to it.childElements("value")[0].textContent
        }
    }

    fun readLayers(doc: Document): List<Layer> {
        val project = doc.elements("project")[0]
        val layers = project.childElements("layers")[0]
        return layers.elements("layer").map { Layer() }
    }
}
