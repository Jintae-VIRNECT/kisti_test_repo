import com.virnect.java.client.RemoteServiceException;
import com.virnect.java.client.RemoteServiceHttpException;
import org.junit.Test;

public class RemoteServiceHttpExceptionTest {
    @Test(expected = RemoteServiceException.class)
    public void shouldThrowGenericOpenViduException() throws RemoteServiceHttpException {
        throw new RemoteServiceHttpException(401);
    }
}
