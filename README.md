# Docker+Websocket+nginx+Springboot+RabbitMQ 构建分布式应用

>对于普通程序员，一般是很难接触到真正的分布式系统。于是只能在自己的电脑上通过Docker搭建虚拟的分布式系统来玩玩，来体验一下集群部署，分布式事务等等高大上的技术。

## Docker 虚拟分布式系统

**Docker** 可以提供一种基于进程的，操作系统层面的虚拟化技术。Docker 在容器的基础上，进行了进一步的封装，从文件系统、网络互联到进程隔离等等，极大的简化了容器的创建和维护。使得 Docker 技术比虚拟机技术更为轻便、快捷。

**Docker Hub** [Docker Hub](https://hub.docker.com "Docker Hub")是一种类似于GitHub的Docker镜像仓库，可以从中取得nginx，rabbitMQ等镜像。

构建两个spring boot镜像，提供web服务和websocket服务。

### WebSocket Code
build.gradle

```
dependencies {
    compile('org.springframework.boot:spring-boot-starter-web')
    compile('org.springframework.boot:spring-boot-starter-thymeleaf')
    compile('org.springframework.boot:spring-boot-starter-websocket')
    compile('org.springframework.boot:spring-boot-starter-data-jpa')
    compile('org.springframework.boot:spring-boot-starter-amqp')
    compile('mysql:mysql-connector-java')
    compile("org.webjars:webjars-locator")
    compile("org.webjars:sockjs-client:1.0.2")
    compile("org.webjars:stomp-websocket:2.3.3")
    compile("org.webjars:bootstrap:3.3.7")
    compile("org.webjars:jquery:3.1.0")
    testCompile('org.springframework.boot:spring-boot-starter-test')
}

```

Controller (websocket 消息接收控制器)

```
@MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public NotificationMessage insertNewData(InsertDataMessage message) throws InterruptedException, IOException, TimeoutException {
        Thread.sleep(1000);

        try {
            if (message.getCounter() == null) {
                System.out.println("count");
            }

            if (message.getCreatedDate() == null) {
                System.out.println("date");
            }
            if (message.getCounter() != null && message.getCreatedDate() != null) {
                Integer count = Integer.valueOf(message.getCounter());
                Date date = Date.valueOf(message.getCreatedDate());

                Computer computer = new Computer();
                computer.setCount(count);
                computer.setCreated(date);
                System.out.println(count);
                System.out.println(date);
                computerRepository.save(computer);
            } else {
                System.out.println("Null!!!");
            }
        } catch (Exception e) {
            System.out.println("Error!!!");
        }

        sender.send();

        return new NotificationMessage("Counter is: " + message.getCounter() + ", created at: " + message.getCreatedDate());
    }
```

WebSocketConfig (websocket 服务端配置)

```
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");

    }
}

```

app.js (websocket client)

```
function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}
```

### Web Code
>使用D3.js构造一个图表显示的web应用

Web服务能够用图表的方式渲染出数据库的中数据，将枯燥的数字以生动的图形表示出来。在这个示例中，这部分将会被websocket的通知消息触发，随后发出ajax请求，动态的刷新图表。

ChartController.java

```
@Controller
public class ChartController {

    @GetMapping(value = "/chart/show")
    public String showChart() {
        return "showchart";
    }
}
```

AjaxController.java

```
@RestController
public class AjaxController {

    @Autowired
    private ComputerRepository computerRepository;

    @RequestMapping("/data")
    public List<Computer> getData(){
        return computerRepository.findAll();
    }
}
```

showchart.js

```

function paint() {
    d3.json("/data",function(data){
......
    
    var x_axis = d3.svg.axis().scale(scale_x),
            y_axis = d3.svg.axis().scale(scale).orient("left");
        chart.append("g")
            .call(x_axis)
            .attr("transform", "translate(0,"+height+")");
        chart.append("g")
            .call(y_axis);

        var bar = chart.selectAll(".bar")
            .data(target)
            .enter()
            .append("g")
            .attr("class","bar")
            .attr("transform", function(d, i) {
                return "translate(" + scale_x(d.created) + ",0)"
            });
......

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            $("#container").empty();

            paint();
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}



$(function () {
    paint();
    connect();
    window.onbeforeunload = function() {
        disconnect();
    }
});
```
现在spring boot应用就构建好了。它有两个页面，一个是使用D3.js显示图表的展示页面，一个是使用websocket推送数据的信息输入页面。

* 当一个用户在信息输入页面中，提交了最新的记录。
* 当前连接的websocket服务器接受到请求信息，保存到mysql数据库
* 该服务器通知所有的已连接的websocket客户端，有新数据需要渲染
* websocket客户端触发图表显示页面重新发起ajax请求更新渲染页面

### Docker化

* 先通过gradle package将应用打包成jar包，并命名为dockerOne.jar
* 编写Dockerfile
* 生成镜像并运行

Dockerfile:

```
FROM registry.cn-hangzhou.aliyuncs.com/alirobot/oraclejdk8-nscd
VOLUME /tmp
add dockerTwo.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
``` 
同样的将One改为Two，再生成一份jar包

构建镜像命令

```
docker build -t dockerdcone .
```

安装以上步骤构建第二个镜像。代码中把“第一个应用”改为“第二个应用”

##构建Nginx负载均衡
> 提供反向代理和负载均衡

下载一个Nginx镜像，最好是alpine版本的，因为这种版本的images文件比较小。

下载命令：

```
docker pull nginx:alpine
```

运行命令：

```
docker run --name nginx_lb -d nginx:alpine

```

##构建一个RabbitMQ镜像
> 提供基本的消息服务。

由于我们构建的是一个分布式系统，因此系统内将不只一个web服务器。那么对于发起数据更新请求的服务器来说，更新相同服务器下其他的图形显示客户端，使用websocket完全够用了。但是对于其他服务器下的图形显示客户端就没法得到更新消息的通知。这个时候就需要消息中间件出马了。

此处我们使用RabbitMQ来作为不同web服务器的消息传递媒介。当其中的一个web服务器下的某个客户端发起数据更新请求，新数据update进mysql。此时websocket会把更新通知消息广播给当前web服务器下的所有websocket客户端广播，同时还通过RabbitMQ发送一个

RabbitMQ 配置文件。 此处我们使用topic的fanout模式广播消息

```
@Configuration
public class FanoutRabbitConfig {

    @Bean
    public Queue AMessage() {
        return new Queue("fanout.A");
    }

    @Bean
    public Queue BMessage() {
        return new Queue("fanout.B");
    }

    @Bean
    public Queue CMessage() {
        return new Queue("fanout.C");
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanoutExchange");
    }

    @Bean
    Binding bindingExchangeA(Queue AMessage,FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(AMessage).to(fanoutExchange);
    }

    @Bean
    Binding bindingExchangeB(Queue BMessage, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(BMessage).to(fanoutExchange);
    }

    @Bean
    Binding bindingExchangeC(Queue CMessage, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(CMessage).to(fanoutExchange);
    }

}

```
RabbitMQ 发送端

```
@Component
public class FanoutSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send() {
        String context = "hi, fanout msg ";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("fanoutExchange","", context);
    }

}
```

RabbitMQ 接收端

> 每个接收端会在各自的queue中等待

```
@Component
@RabbitListener(queues = "fanout.B")
public class FanoutReceiverA {

    @Autowired
    WebSocketService webSocketService;

    @RabbitHandler
    public void process(String message) {
        System.out.println("fanout Receiver B  : " + message);
        webSocketService.notifyWebSocketClient();
    }

}
```

##WebSocket 服务端主动推送信息
当web服务器收到RabbitMQ广播的消息后，需要通过websocket主动推送信息，要求所有的显示客户端更新图表。此时就需要一个service来负责在websocket服务端主动推送消息给所有客户端的工作。

```
@Service
public class WebSocketService {
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    public void notifyWebSocketClient() {

        simpMessagingTemplate.convertAndSend("/topic/greetings", "test");
    }
}
```


