package bot.storage

import java.nio.ByteBuffer

abstract class JSONStorage<E> : Storage<E>() {
	override fun serialize(data: E): ByteBuffer {
		TODO("Not yet implemented")
	}

	override fun deserialize(buffer: ByteBuffer): E {
		TODO("Not yet implemented")
	}
}