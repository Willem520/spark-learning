package simulator;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @Author weiyu
 * @Description
 * @Date 2018/10/31 11:44
 */
public class KafkaProducerSimulator {
    public static final String TOPIC = "test";
    public static final String BOOTSTRAP_SERVER = "10.26.27.81:9092";
    public static final String[] OS_TYPE = new String[]{"Android", "IOS", "Other", "None"};

    public static void main(String[] args) throws InterruptedException {
        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", BOOTSTRAP_SERVER);

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer kafkaProducer = new KafkaProducer<String, String>(props);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaProducer::close));

        while (true){
            Random rand = new Random();
            JSONObject event = new JSONObject();
            event.put("uid", UUID.randomUUID());
            event.put("event_time", System.currentTimeMillis());
            event.put("os_type",OS_TYPE[rand.nextInt(4)]);
            event.put("click_count",rand.nextInt(10));
            String message = event.toString();
            System.out.println("******产生消息:"+message);
            kafkaProducer.send(new ProducerRecord<String,String>(TOPIC,message));
            Thread.sleep(2000);
        }
    }
}
