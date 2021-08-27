
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.Types
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.apache.flink.walkthrough.common.entity.Alert
import org.apache.flink.walkthrough.common.entity.Transaction


class FraudDetector :
    KeyedProcessFunction<Long, Transaction, Alert>() {

    @Transient
    lateinit var flagState: ValueState<Boolean>

    @Transient
    lateinit var timerState: ValueState<Long>

    override fun open(parameters: Configuration) {
        val flagDescriptor = ValueStateDescriptor(
            "flag",
            Types.BOOLEAN
        )
        flagState = runtimeContext.getState(flagDescriptor)

        val timerDescriptor = ValueStateDescriptor(
            "timer-state",
            Types.LONG
        )
        timerState = runtimeContext.getState(timerDescriptor)
    }

    override fun processElement(
        transaction: Transaction,
        context: Context,
        collector: Collector<Alert>
    ) {
        if (flagState.value() != null) {
            if (transaction.amount > LARGE_AMOUNT) {
                Alert(1337L).also { alert ->
                    collector.collect(alert)
                }
            }
            cleanUp(context);
        }

        if (transaction.amount < SMALL_AMOUNT) {
            flagState.update(true)

            val timer = context.timerService().currentProcessingTime() + ONE_MINUTE
            context.timerService().registerProcessingTimeTimer(timer)
            timerState.update(timer)
        }
    }

    override fun onTimer(timestamp: Long, ctx: OnTimerContext?, out: Collector<Alert>?) {
        timerState.clear()
        flagState.clear()
    }


    private fun cleanUp(context: Context) {
        // delete timer
        val timer = timerState.value()
        context.timerService().deleteProcessingTimeTimer(timer)

        // clean up all state
        timerState.clear()
        flagState.clear()
    }



    companion object {
        private const val serialVersionUID = 1L
        private const val SMALL_AMOUNT = 1.00
        private const val LARGE_AMOUNT = 500.00
        private const val ONE_MINUTE = (60 * 1000).toLong()
    }
}

fun Alert(idSet: Long) = Alert().apply { id = idSet }