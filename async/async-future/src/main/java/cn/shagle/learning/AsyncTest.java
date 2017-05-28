package cn.shagle.learning;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.CloseableHttpPipeliningClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by lenovo on 2017/5/27.
 */
public class AsyncTest {

    public static CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.createDefault();
    public static CompletableFuture<BasicHttpResponse> getHttpData(String url) {
        CompletableFuture asyncFuture = new CompletableFuture<>();
        HttpAsyncRequestProducer producer = HttpAsyncMethods.create(new HttpPost(url));
        BasicAsyncResponseConsumer consumer = new BasicAsyncResponseConsumer();
        FutureCallback<HttpResponse> callback = new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                asyncFuture.complete(httpResponse);
            }

            @Override
            public void failed(Exception e) {
                asyncFuture.completeExceptionally(e);
            }

            @Override
            public void cancelled() {
                asyncFuture.cancel(true);
            }
        };
        System.out.println("结束");
        httpAsyncClient.start();
        httpAsyncClient.execute(producer,consumer,callback);
        return asyncFuture;
    }

    public static void main(String[] args) throws Exception {
        CompletableFuture<BasicHttpResponse> future = AsyncTest.getHttpData("http://www.jd.com");
        BasicHttpResponse result = future.get();
        HttpEntity httpEntity = result.getEntity();
        InputStream is =httpEntity.getContent();
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] bytes = new byte[1024];
        while (bis.read(bytes) > 0) {
            System.out.println(new String(bytes));
        }
    }
}
