import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.virnect.client.RemoteServiceClient;
import com.virnect.client.ServerJsonRpcHandler;
import org.junit.Before;
import org.junit.Test;
import org.kurento.jsonrpc.client.JsonRpcClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.virnect.client.internal.ProtocolElements.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RemoteServiceClientTest {
    private RemoteServiceClient client;
    private ServerJsonRpcHandler serverHandler;
    private JsonRpcClient jsonRpcClient;

    @Before
    public void setup() {
        jsonRpcClient = mock(JsonRpcClient.class);
        serverHandler = new ServerJsonRpcHandler();
        client = new RemoteServiceClient(jsonRpcClient, serverHandler);
    }

    @Test
    public void testRoomJoin() throws IOException {
        JsonObject params = new JsonObject();
        params.addProperty(JOINROOM_ROOM_PARAM, "room");
        params.addProperty(JOINROOM_USER_PARAM, "user");

        JsonObject result = new JsonObject();
        JsonArray value = new JsonArray();
        result.add("value", value);

        Map<String, List<String>> joinResult = new HashMap<String, List<String>>();

        when(jsonRpcClient.sendRequest(JOINROOM_METHOD, params)).thenReturn(result);
        assertThat(client.joinRoom("room", "user"), is(joinResult));

    }
}
