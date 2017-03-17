import com.hejun.demo.test.HelloWorld;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by hejun-FDC on 2017/3/1.
 */
public class HelloworldTest {
    @Test
    public void testSayHello() {
        HelloWorld helloWorld = new HelloWorld();
        String result = helloWorld.sayHello();
        Assert.assertEquals("Hello Maven", result);
    }
}
