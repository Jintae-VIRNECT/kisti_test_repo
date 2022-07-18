import { COMMAND } from 'configs/push.config'
import { URLS } from 'configs/env.config'
import auth from 'utils/auth'

//멤버 상태 소켓의 응답에 따라 실행할 callback methods 모음
//WorkspaceLayout의 watch : workspace에서 auth.initAuthConnection의 파라미터로 해당 메서드들을 넘긴다.

//esc key로 팝업 탈출 방지
const option = {
  allowEscapeKey: false,
}

export default {
  methods: {
    //worksspace 변경 실패 시 - 워크스페이스 롤백 & 재시도/취소 팝업
    //재시도 : 워크스페이스 변경 재요청
    //취소 : 기존 워크스페이스에서 머뭄
    onWorkspaceUpdateFail({ oldWorkspaceId, workspaceId, userId }, socket) {
      //기존 워크스페이스로 롤백
      this.$eventBus.$emit('workspaceChange', oldWorkspaceId)

      //재시도 팝업
      const text = this.$t('workspace.workspace_update_failed')
      const confirm = {
        text: this.$t('button.retry'),
        action: () => {
          //워스스페이스 변경 재요청
          socket.send(
            JSON.stringify({
              command: COMMAND.WORKSPACE_UPDATE,
              data: {
                userId,
                workspaceId,
              },
            }),
          )
        },
      }
      const cancel = {
        text: this.$t('button.leave_exit'),
        action: () => {},
      }
      this.confirmCancel(text, confirm, cancel, option)
    },

    //유저 정보 등록 실패시 - 재시도/종료 팝업
    //재시도 : 유저 정보 등록 재요청
    //종료 : 로그아웃 및 로그인 페이지로 리디렉트
    onRegistrationFail({ userId, nickname, email, workspaceId }, socket) {
      const text = this.$t('workspace.auth_status_failed')

      const confirm = {
        text: this.$t('button.retry'),
        action: () => {
          //인증 등록 재요청
          socket.send(
            JSON.stringify({
              command: COMMAND.REGISTER,
              data: {
                userId,
                nickname,
                email,
                workspaceId,
              },
            }),
          )
        },
      }

      //로그아웃 및 로그인 페이지로 리디렉트
      const cancel = {
        text: this.$t('button.leave_exit'),
        action: async () => {
          await auth.logout()
        },
      }
      this.confirmCancel(text, confirm, cancel, option)
    },

    //중복된 기 접속자가 있는 경우
    //기접속자 로그인인 경우 (+예외 상황 발생시키지 않기 위해 로그아웃인 경우도 포함)- 원격종료/종료
    //원격 종료 : 기접속자 원격종료 요청
    //종료 : 신규 로그인 시도자 로그아웃 및 로그인페이지로 리디렉트
    //기접속자 협업 중인 경우 - 팝업 알림 후 로그아웃 및 로그인 페이지로 리디렉트
    async onDuplicatedRegistration({ currentStatus, userId }, socket) {
      //로그인 된 기 접속자가 있는 경우 : 팝업으로 강제 로그아웃 실행 여부 확인
      //로그아웃인 경우는 발생하지 않아야 하지만 예외경우 포함시킴
      if (currentStatus === 'LOGIN' || currentStatus === 'LOGOUT') {
        const text = this.$t('workspace.confirm_duplicated_session_logout')

        //원격종료
        const confirmAction = () => {
          //기 접속자 원격 종료 요청
          socket.send(
            JSON.stringify({
              command: COMMAND.REMOTE_EXIT,
              data: {
                service: 'remote',
                workspaceId: this.workspace.uuid,
                userId,
                targetUserId: userId,
                event: 'remoteExit',
              },
            }),
          )
        }
        //취소 : 로그인 시도했던 사용자 로그아웃 처리 및 로그인 페이지로 리디렉트
        const cancelAction = async () => {
          //(REGISTER)
          //A) 로그인 => 기존 워크스페이스
          //B) 워크스페이스 목록 => 워크스페이스 선택 하는 경우
          await auth.logout() //1) 일관성 있게 로그아웃 후 로그인 페이지로 리디렉트
        }

        const confirm = {
          text: this.$t('button.force_logout'),
          action: confirmAction,
        }

        const cancel = {
          text: this.$t('button.leave_exit'),
          action: cancelAction,
        }

        this.confirmCancel(text, confirm, cancel, option)
      }
      //협업 중인 경우 : 팝업 띄운 후 로그인 페이지로 리디렉트
      else if (currentStatus === 'JOIN') {
        await auth.logout(false) //바로 로그아웃 처리하고, 리디렉트는 팝업 엑션에서 실행한다

        const text = this.$t('workspace.confirm_duplicated_session_joined')
        const action = () =>
          (location.href = `${URLS['console']}/?continue=${URLS['remote']}`) //리디렉트
        this.confirmDefault(text, { action }, option)
      }
    },

    //중복 로그인으로 인해 신규접속자로 부터 원격종료 이벤트 수신한 경우 - 로그아웃 처리 후 팝업 클릭 시 로그인 페이지 이동
    async onRemoteExitReceived() {
      await auth.logout(false) //바로 로그아웃 처리
      //팝업 표시 후 리디렉트 실행
      this.confirmDefault(
        this.$t('workspace.confirm_duplicated_session_logout_received'),
        {
          action: () =>
            (location.href = `${URLS['console']}/?continue=${URLS['remote']}`),
        },
        option,
      )
    },

    //구축형 - 마스터로 부터 강제로그아웃 이벤트 수신한 경우 - 로그아웃 처리후 팝업 클릭 시 로그인 페이지로 이동
    async onForceLogoutReceived() {
      if (!this.isOnpremise) return
      this.debug('force logout received')

      //no redirect
      await auth.logout(false)

      //로그아웃 처리
      const action = () =>
        (location.href = `${URLS['console']}/?continue=${URLS['remote']}`)

      //강제 로그아웃 알림 팝업
      this.confirmDefault(
        this.$t('workspace.confirm_force_logout_received'),
        {
          action,
        },
        option,
      )
    },

    //워크스페이스 변경 시 해당 워스크페이스에 중복 로그인인 경우
    //기접속자 로그인인 경우 (+예외 상황 발생시키지 않기 위해 로그아웃인 경우도 포함)- 원격종료/취소
    //원격 종료 : 기접속자 원격종료 요청
    //취소 : 워크스페이스 변경 취소 & 기존 워스크페이스 머뭄
    //기접속자 협업 중인 경우 - 팝업 알림 후 로그아웃 및 로그인 페이지로 리디렉트
    onWorkspaceDuplicated(
      { currentStatus, workspaceId, oldWorkspaceId, userId },
      socket,
    ) {
      //로그인 된 기 접속자가 있는 경우 : 팝업으로 강제 로그아웃 실행 여부 확인
      //로그아웃인 경우는 발생하지 않아야 하지만 예외경우 포함시킴
      if (currentStatus === 'LOGIN' || currentStatus === 'LOGOUT') {
        //기존 워크스페이스에서 변경 시 발생한 중복 로그인 상황인 경우 취소시 그대로 머문다 (CHANGE)
        const text = this.$t('workspace.confirm_duplicated_session_logout')

        //원격종료
        const confirmAction = () => {
          //해당 워크스페이스 기 접속자 원격 종료 요청
          socket.send(
            JSON.stringify({
              command: COMMAND.REMOTE_EXIT,
              data: {
                service: 'remote',
                workspaceId: workspaceId,
                userId,
                targetUserId: userId,
                event: 'remoteExit',
              },
            }),
          )
        }

        const confirm = {
          text: this.$t('button.force_logout'),
          action: confirmAction,
          option,
        }

        const cancel = {
          text: this.$t('button.cancel'),
          action: () => {
            //기존 워크스페이스로 롤백
            this.$eventBus.$emit('workspaceChange', oldWorkspaceId)
          },
        }

        this.confirmCancel(text, confirm, cancel, option)
      }
      //협업 중인 경우 : 팝업 띄운 후 로그인 페이지로 리디렉트
      else {
        const text = this.$t('workspace.confirm_duplicated_session_joined')
        this.confirmDefault(text, option)
      }
    },
  },
}
