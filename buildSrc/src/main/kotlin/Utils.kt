import org.gradle.api.Project
import kotlin.reflect.KProperty

open class ProviderDelegate<out T : Any>(
    private val defaultValueProvider: () -> T,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return defaultValueProvider()
    }
}

class PropertyDelegate<T : Any>(
    private val defaultValueProvider: () -> T,
) {
    private var backing: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return backing ?: defaultValueProvider()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        backing = value
    }
}

fun <T : Any> prov(
    defaultValueProvider: () -> T,
): ProviderDelegate<T> = ProviderDelegate(defaultValueProvider)

fun <T : Any> prop(
    defaultValueProvider: () -> T,
): PropertyDelegate<T> = PropertyDelegate(defaultValueProvider)

fun String?.toBooleanLenient(): Boolean? = when (this?.lowercase()) {
    null -> false
    in listOf("", "yes", "true", "on", "y") -> true
    in listOf("no", "false", "off", "n") -> false
    else -> null
}

fun Project.enabled(propertyName: String, envName: String = propertyName) =
    prov {
        System.getenv(envName)?.toString()?.toBooleanLenient()
            ?: (hasProperty(propertyName) && property(propertyName).toString().toBooleanLenient() == true)
    }