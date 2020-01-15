import remoteStore from './RemoteStore'
import {
  appendGroupListener,
  appendRoomListener
} from './RemoteListener'
import {
  log
} from 'utils/log'


export default {
  install(Vue, {
      Store
  }) {
    if (!Store) {
      throw new Error('Can not find vuex store');
    } else {
      Store.registerModule('remote', remoteStore);
    }

    const remote = window.Virnect && window.Virnect.Remote;
    const remoteState = Store.state.remote;

    if (false === !!remote) {
      console.error("Cannot find VIRNECTRemote SDK");
      return;
    }

    Vue.prototype.$remoteSDK = {
      /**
       * Join to signaling server
       * @param {Function} callback
       */
      init: function ({
        token,
        sid,
        server
      }) {
        if (token && sid) {
          return new Promise((resolve, reject) => {
            remote.create(token, server, sid)
              .then((receive) => {
                Store.commit('remoteReady', true)
                resolve(receive);
              })
              .catch(reject)
          })
        } else {
          throw new Error('Not defined sid or token')
        }
      },
      // disconnect: function () {
      //     let counter = 0;

      //     if (remote.controller) {
      //         const group = remote.controller.getCurrentGroup();

      //         if (group) {
      //             group.disconnect();
      //         } else {
      //             if (counter < 5) {
      //                 setTimeout(() => {
      //                     counter++;
      //                     this.disconnect();
      //                 }, 0);
      //             }
      //         }
      //     }
      // },
      /**
       * Join to signaling group
       */
      joinGroup: function (groupId, userNickname, groupNickname) {
        if (remote.controller) {
          if (groupId) {
            return new Promise((resolve, reject) => {
              //그룹 연결
              remote.controller.CONNECT_GROUP(groupId, groupNickname)
                .then(group => {
                  //그룹 이벤트리스너 바인딩.
                  appendGroupListener(group);

                  //그룹 접속
                  remote.controller.JOIN_GROUP(userNickname)
                    .then((group) => {
                      const users = JSON.parse(JSON.stringify(group.onlineUsers))
                      const updates = {
                        users
                      }
                      Store.commit('remoteReady', true)
                      Store.commit('remoteUpdate', updates)

                      resolve(group);
                    })
                    .catch(reject)
                })
                .catch(e => {
                  Store.commit('remoteReady', false)
                  reject(e)
                })
            })
          } else {
              throw new Error('groupId is not defined.')
          }
        } else {
          throw new Error('Call init first.')
        }
      },
      /**
       * Get joined group users
       * @returns {Promise}
       */
      updateGroupUsers() {
        remote.controller.GET_GROUP_USERS()
          .then(result => {
            if ('list' in result.data) {
              const users = JSON.parse(JSON.stringify(result.data.list))
              Store.commit('remoteUpdate', {
                users
              })
            }
          })
      },
      /**
       * Send call to opponent
       * @param {String} opponent
       * @param {Function} callback
       */
      call: function (opponentId) {
        return new Promise((resolve, reject) => {

          remote.controller.INVITE_USER(opponentId)
            .then((room) => {
              //룸 이벤트리스너 바인딩.
              appendRoomListener(room);

              const mediaInfo = {
                audio: true,
                video: true,
              }

              room.getUserMedia(mediaInfo, (stream, e) => {
                if (e) {
                  Store.commit('remoteDeviceReady', {
                    readyState: false,
                    reason: e
                  });
                }
              })

              //데이터채널 생성
              room.createDataChannel({
                label: 'normalDataChannel'
              })
              room.createDataChannel({
                label: 'mediaDataChannel'
              })

              Store.commit('remoteUpdate', {
                opponentId: opponentId
              })

              Store.dispatch('changeStatus', 'sendCall');

              resolve(room);
            })
            .catch(reject)
        })
      },
      /**
       * Send reject
       * @param {Function} callback
       */
      reject: function (callback) {
        remote.controller.REJECT_INVITE()
          .then(() => {
            Store.dispatch('changeStatus', 'sendReject')
              .then((callback) ? callback : () => {});
          })
      },
      /**
       * Send accept
       * @param {Function} callback
       */
      accept: function (callback) {

        //getUserMedia 선작업 필수.
        const room = remote.controller.getCurrentRoom();
        const mediaInfo = {
          audio: true,
          video: true,
        }

        room.getUserMedia(mediaInfo, (stream, e) => {
          if (e) {
            Store.commit('remoteDeviceReady', {
              readyState: false,
              reason: e
            });
          }

          remote.controller.JOIN_ROOM()
            .then((room) => {

              //룸 이벤트리스너 바인딩.
              appendRoomListener(room);

              Store.dispatch('changeStatus', 'sendAccept')
                  .then((callback) ? callback : () => {});
            });
        })

      },
      /**
       * Cancel call
       * @param {*} callback
       */
      cancel: function (callback) {
        remote.controller.CANCEL_INVITE()
          .then(() => {
            Store.commit('remoteClear');
            Store.dispatch('changeStatus', 'sendStop')
                .then((callback) ? callback : () => {});
          })
          .catch(error => {
            console.log(error);
          })
      },
      /**
       * Send stop
       * @param {Function} callback
       */
      stop: function (callback) {
        remote.controller.LEAVE_ROOM()
          .then(() => {
            Store.commit('remoteClear');
            Store.dispatch('changeStatus', 'sendStop')
                 .then((callback) ? callback : () => {})
          })
          .catch((error) => {
            switch (error + "") {
              case "8100":
              case "8101":
              case "8102":
              case "8103":
              case "8104":
                  location.reload();
                  break;
            }
          });
      },
      /**
       * Send notify
       * @param {String} opponentId
       * @param {JSON} type
       * @return {Promise}
       */
      notify: function (opponentId, type) {
          log(opponentId, type);

          return remote.controller.NOTIFY(opponentId, JSON.stringify(type))
      },
      getDevices: function () {
        if (remote.room) {
          return remote.room.getUserMedia
        } else {
          throw new Error('Cannot find room.')
        }
      },
      mute: async function () {
        const room = remote.controller && remote.controller.getCurrentRoom();
        if (!room) {
          throw new Error('Cannot find room.')
        }

        try {
          await room.mute();
        } catch (e) {
          Store.commit('remoteDeviceReady', {
            readyState: false,
            reason: e
          });
        }
      },
      unmute: async function () {
        const room = remote.controller && remote.controller.getCurrentRoom();
        if (!room) {
          throw new Error('Cannot find room.')
        }

        try {
          await room.unmute();
        } catch (e) {
          Store.commit('remoteDeviceReady', {
            readyState: false,
            reason: e
          });
        }
      },
      setState: function (key, value) {
        let obj = {}
        if (typeof key === 'object') {
          obj = key;
        } else if (typeof key === 'string') {
          const obj = {}
          obj[key] = value;
        }
        // console.log(obj);
        Store.commit('remoteUpdate', obj);
      },
      /**
       * Send message
       * @param {String} name
       * @param {String} message
       * @param {Function} callback
       */
      message: function (name, message) {
        const type = {
              resolution: ['normalDataChannel', 'resolution'],
              addImage: ['mediaDataChannel', 'addImage'],
              startImage: ['mediaDataChannel', 'startImage'],
              showImage: ['mediaDataChannel', 'showImage'],
              removeImage: ['mediaDataChannel', 'removeImage'],
              endImage: ['mediaDataChannel', 'endImage'],
              lineStart: ['normalDataChannel', "drawLineDown"],
              lineMove: ['normalDataChannel', "drawLineMove"],
              lineEnd: ['normalDataChannel', "drawLineUp"],
              drawText: ['normalDataChannel', "drawText"],
              updateText: ['normalDataChannel', "updateText"],
              drawMove: ['normalDataChannel', "drawMove"],
              drawRotate: ['normalDataChannel', "drawRotate"],
              drawScale: ['normalDataChannel', "drawScale"],
              clearAll: ['normalDataChannel', "drawClearAll"],
              pointing: ['normalDataChannel', "pointing"],
              redo: ['normalDataChannel', "drawRedo"],
              undo: ['normalDataChannel', "drawUndo"],
              chat: ['normalDataChannel', "chat"],
              clear: ['normalDataChannel', "drawClear"],
              bitrate: ['normalDataChannel', "bitrate"]
        }
        const channel = type[name][0];
        const param = {
          type: type[name][1],
          ...message
        }

        log(channel, param);
        const room = remote.controller.getCurrentRoom();
        if (Store.getters.canUseChannel) {
          room.sendMessage(channel, JSON.stringify(param))
          if ('showImage' === name && param.fileName) {
            Store.commit('remoteUpdate', {
              channelOnUse: true
            })
            // TODO:: 답변 왔는지 체크 (timeout?)
          }
          return true;
        } else {
          return false;
        }
      },
      /**
       * append message channel listener
       * @param {Function} customFunc
       */
      addMessageListener(customFunc) {
        const room = remote.controller.getCurrentRoom();
        room.addEventListener('MESSAGE', (receive, channel) => {
          try {
            // json이 나눠져서 오는 문제때문에 json 그대로 전달
            customFunc(receive, channel)
            // const message = JSON.parse(receive);
            // customFunc(message, channel)
          } catch (error) {
            console.log(error)
            customFunc(receive)
          }
        })
      },
      /**
       * remove message channel listener
       * @param {Function} customFunc
       */
      removeMessageListener(customFunc) {}
    }
  }
}