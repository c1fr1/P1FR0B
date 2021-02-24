package bot.modules

import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.CLASS)
annotation class ModuleID(val name : String) {
	companion object {

		fun getID(name : String) : String = name
				.replace("'", "")
				.replace(Regex("([^a-zA-Z])"), "-")
				.toLowerCase()

		fun getID(mAnn : ModuleID) : String = getID(mAnn.name)

		fun <T : IModule> getID(m : KClass<T>) : String? {
			if (m.isAbstract) return null
			val ret = m.findAnnotation<ModuleID>()
			if (ret != null) return getID(ret.name)
			for (constructor in m.constructors) {
				if (constructor.parameters.isEmpty()) {
					return constructor.call().id
				}
			}
			return null
		}

		fun <T : IModule> getID(m : Class<T>) : String? = getID(m.kotlin)

		fun <T : IModule> getName(m : KClass<T>) : String? {
			if (m.isAbstract) return null
			val ret = m.findAnnotation<ModuleID>()
			if (ret != null) return ret.name
			for (constructor in m.constructors) {
				if (constructor.parameters.isEmpty()) {
					return constructor.call().name
				}
			}
			return null
		}

		fun <T : IModule> getName(m : Class<T>) : String? = getName(m.kotlin)
	}
}
