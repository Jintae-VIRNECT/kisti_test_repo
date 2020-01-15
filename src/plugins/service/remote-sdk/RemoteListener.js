import Store from "stores/service/store";
import { log } from "utils/log";

export const appendRoomListener = function(room) {
  room.addEventListener("REMOTE_STREAM", stream => {
    const updates = {
      opponentStream: stream
    };
    Store.commit("remoteUpdate", updates);
  });

  room.addEventListener("MESSAGE", (receive, channel) => {
    // log(receive, channel);
    // JSON 형식 체크
    if (receive.indexOf("}") < 0) {
      Store.commit("messageData", receive);
      return;
    }
    const fullReceive = Store.getters.messageData + receive;
    Store.commit("remoteUpdate", {
      messageData: ""
    });
    log(fullReceive, channel);

    const message = JSON.parse(fullReceive);
    if ("type" in message) {
      switch (message.type) {
        case "showImage":
          if (true == message.result) {
            Store.commit("remoteUpdate", {
              channelOnUse: false
            });
            Store.dispatch("changeCanDrawing", true);
          }
          break;
        case "resolution":
          const params = {};
          params.opponentScreenWidth = message.width;
          params.opponentScreenHeight = message.height;
          params.opponentScreenVerticalPad = message.softKeyHeight;
          Store.commit("remoteUpdate", params);
          break;
        case "bitrate":
          Store.commit("remoteUpdate", {
            callBitrate: message.rate
          })
          break;
      }
    }
  });
};

export const appendGroupListener = function(group) {
  let connectTry = 0;

  group.addEventListener("CONNECT", receive => {
    Store.commit("remoteConnected", group.groupNickname);
    // log(receive)
  });

  group.addEventListener("DISCONNECT", receive => {
    Store.commit("remoteDisconnected", group.groupNickname);

    if ("8105" === String(receive)) {
      return;
    } else {
      Store.dispatch("changeStatus", "sendStop");
    }
    //  else {

    // }
    // Store.commit('remoteDisconnected', group.groupName);
    // alert(receive);
    // if ('8105' !== String(receive)) {
    //     Store.dispatch('changeStatus', 'sendStop');
    // }
  });

  group.addEventListener("CONNECT_ERROR", receive => {
    Store.commit("remoteConnectError", connectTry);
    log(receive);
  });

  group.addEventListener("CONNECT_TIMEOUT", receive => {
    Store.commit("remoteConnectTimeout", connectTry);
    log(receive);
  });

  group.addEventListener("EXCEPTION", receive => {
    log(receive);
    switch (Number(receive.status)) {
      case 9000:
        alert("SchemaValidationError:: check your schema is correct or not.");
        break;
      case 9100:
        alert("RegisterFirstError:: check id is sent or not.");
        break;
      case 9101:
        Store.commit("remoteUpdate", {
          duplicated: true
        });
        break;
        // case 9102: //alert('id already existed.');
        break;
      case 9200:
        alert("SelfError:: check you invite yourself or not.");
        break;
      case 9201:
        alert("UserNotFoundError:: check opponent is exist or not.");
        break;
      case 9202:
        alert("CallingStatusError:: check you is on calling status or not.");
        break;
      case 9203: //alert('CallingStatusError:: check opponent is on calling status or not.')
        Store.dispatch("showToast", {
          text: "service.call_already_online",
          type: "error"
        });
        break;
      case 9205:
      case 9206:
        alert("The signaling server is temporarily unavailable");
        break;
      case 9300:
        alert("SelfNotifyError:: check you notify yourself or not.");
        break;
      case 9999:
        alert("InternalServerError:: check server works well or not.");
        Store.commit("remoteReady", false);
        break;
    }
  });

  group.addEventListener("INVITED", receive => {
    const message = receive.data;
    log(message);
    const updates = {
      roomId: message.roomId,
      opponentId: message.opponentId,
      clientType: message.clientType
    };

    Store.commit("remoteUpdate", updates);
    Store.dispatch("changeStatus", "recvCall");
  });

  group.addEventListener("INVITE_CANCELED", receive => {
    const message = receive.data;
    Store.dispatch("changeStatus", "recvStop");
  });

  group.addEventListener("USER_GROUP_JOINED", receive => {
    /**
     * {
     * opponentId: "4e25737e4f3650acac924f136dcc7803"
     * status: 1
     * }
     */
    // console.log(receive.data)

    const data = receive.data;
    if (data) {
      const users = JSON.parse(JSON.stringify(group.onlineUsers));
      const updates = {
        users: users.map(user => {
          if (data.opponentId === user.sId) {
            return {
              sId: user.sId,
              status: data.status
            };
          } else {
            return user;
          }
        })
      };
      Store.commit("remoteUpdate", updates);
    }
  });

  group.addEventListener("USER_GROUP_LEAVED", receive => {
    /**
     * {
     * opponentId: "4e25737e4f3650acac924f136dcc7803"
     * status: 1
     * }
     */
    // console.log(receive.data)

    const data = receive.data;
    if (data) {
      const users = JSON.parse(JSON.stringify(group.onlineUsers));
      const updates = {
        users: users.map(user => {
          if (data.opponentId === user.sId) {
            return {
              sId: user.sId,
              status: data.status
            };
          } else {
            return user;
          }
        })
      };
      Store.commit("remoteUpdate", updates);
    }
  });

  group.addEventListener("USER_STATUS_CHANGED", receive => {
    /**
     * {
     * opponentId: "4e25737e4f3650acac924f136dcc7803"
     * status: 1
     * }
     */
    const data = receive.data;
    if (data) {
      const users = JSON.parse(JSON.stringify(group.onlineUsers));
      const updates = {
        users: users.map(user => {
          if (data.opponentId === user.sId) {
            return {
              sId: user.sId,
              status: data.status
            };
          } else {
            return user;
          }
        })
      };
      Store.commit("remoteUpdate", updates);
    }
  });

  group.addEventListener("USER_JOINED", receive => {
    const message = receive.data;
    if (message) {
      const updates = {
        roomId: message.roomId,
        opponentId: message.opponentId,
        clientType: message.clientType
      };
      Store.commit("remoteUpdate", updates);
      Store.dispatch("changeStatus", "recvAccept");
    } else {
      console.log("USER_JOINED :: ");
      console.log(message);
    }
  });

  group.addEventListener("USER_REJECTED", message => {
    Store.commit("remoteClear");
    Store.dispatch("changeStatus", "recvReject");
  });

  group.addEventListener("USER_LEAVED", message => {
    Store.commit("remoteClear");
    Store.dispatch("changeStatus", "recvStop");
  });

  group.addEventListener("NOTIFY", receive => {
    const message = receive.data;
    const updates = {
      notify: {
        opponentId: message.opponentId,
        datetime: new Date(),
        type: message.type
      }
    };

    log(updates);
    Store.commit("remoteUpdate", updates);
  });
};
