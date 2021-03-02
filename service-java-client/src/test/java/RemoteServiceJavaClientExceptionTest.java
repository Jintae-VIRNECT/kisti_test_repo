import com.virnect.java.client.RemoteServiceException;
import com.virnect.java.client.RemoteServiceJavaClientException;
import org.junit.Test;

public class RemoteServiceJavaClientExceptionTest {
    @Test(expected = RemoteServiceException.class)
    public void shouldThrowGenericOpenViduException() throws RemoteServiceJavaClientException {
        throw new RemoteServiceJavaClientException("message");
    }
}
