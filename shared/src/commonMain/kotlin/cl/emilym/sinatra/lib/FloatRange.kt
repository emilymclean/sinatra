package cl.emilym.sinatra.lib

class FloatRange(
    override val start: Float,
    override val endInclusive: Float
): ClosedRange<Float> {

    companion object {

        val default = FloatRange(-1f, Float.MAX_VALUE)

    }

}