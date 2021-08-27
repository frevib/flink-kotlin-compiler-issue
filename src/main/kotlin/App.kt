
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.walkthrough.common.entity.Alert
import org.apache.flink.walkthrough.common.entity.Transaction
import org.apache.flink.walkthrough.common.sink.AlertSink
import org.apache.flink.walkthrough.common.source.TransactionSource



fun main(args: Array<String>) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment()

    val transactions: DataStream<Transaction> = env
        .addSource(TransactionSource())
        .name("transactions")

    val alerts: DataStream<Alert> = transactions
        .keyBy { it.accountId }
        .process(FraudDetector())
        .name("fraud-detector")

    alerts
        .addSink(AlertSink())
        .name("send-alerts")

    println("now starting...")
    env.execute("Fraud Detection");
}