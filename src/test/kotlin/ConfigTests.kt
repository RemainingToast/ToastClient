
import dev.toastmc.client.util.ConfigUtil.getBoolean
import dev.toastmc.client.util.ConfigUtil.getBranch
import dev.toastmc.client.util.ConfigUtil.getNumber
import dev.toastmc.client.util.ConfigUtil.setBoolean
import dev.toastmc.client.util.ConfigUtil.setNumber
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicReference


class ConfigTests {
    @Test
    fun getTest() {
        val a = AtomicReference<ConfigLeaf<*>>()
        val b = AtomicReference<ConfigLeaf<*>>()
        val tree: ConfigTree = ConfigTree.builder()
            .fork("child")
            .fork("stuff")
            .beginValue("A", ConfigTypes.INTEGER, 10)
            .finishValue(a::set)
            .finishBranch()
            .beginValue("B", ConfigTypes.BOOLEAN, true)
            .finishValue(b::set)
            .finishBranch()
            .build()
        assertEquals(a.get().value, tree.getBranch("child")?.getBranch("stuff")?.getNumber(ConfigTypes.INTEGER, "A"))
        assertEquals(b.get().value, tree.getBranch("child")?.getBoolean("B"))
    }

    @Test
    fun setTest() {
        val a = AtomicReference<ConfigLeaf<*>>()
        val b = AtomicReference<ConfigLeaf<*>>()
        val tree: ConfigTree = ConfigTree.builder()
            .fork("child")
            .fork("stuff")
            .beginValue("A", ConfigTypes.INTEGER, 10)
            .finishValue(a::set)
            .finishBranch()
            .beginValue("B", ConfigTypes.BOOLEAN, true)
            .finishValue(b::set)
            .finishBranch()
            .build()
        tree.getBranch("child")?.getBranch("stuff")?.setNumber(ConfigTypes.INTEGER, "A", BigDecimal(20))
        assertEquals(BigDecimal(20), a.get().value)
        tree.getBranch("child")?.setBoolean("B", false)
        assertEquals(false, b.get().value)
    }
}