package cl.emilym.sinatra.router.data

interface RandomByteReader {
    fun read(position: Int, out: ByteArray)
    fun read(position: Int): Byte
}

class ByteArrayRandomByteReader(
    val data: ByteArray
): RandomByteReader {

    override fun read(position: Int, out: ByteArray) {
        data.copyInto(out, 0, position, position + out.size)
    }

    override fun read(position: Int): Byte {
        return data[position]
    }

}