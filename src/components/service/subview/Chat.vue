<template>
  <div class="chat">
    <div class="chat-header">
      <div class="chat-header__title">
        <div class="title-text">{{ roomTitle + ' 원격협업' }}</div>
        <!-- <popover trigger="hover" placement="bottom">
          <button slot="reference" class="show-list">목록보기</button>
          <div>사용자 목록</div>
        </popover> -->
      </div>

      <div class="chat-header__menu">
        <div
          class="selector"
          :class="{ active: showChat }"
          @click="toggleChatMenu"
        >
          <div
            class="selector__icon--chat"
            :class="{ chat__active: showChat }"
          ></div>
          <span class="selector__text" :class="{ active: showChat }">채팅</span>
        </div>
        <div
          class="selector"
          :class="{ active: showFile }"
          @click="toggleFileMenu"
        >
          <div
            class="selector__icon--file"
            :class="{ file__active: showFile }"
          ></div>
          <span class="selector__text" :class="{ active: showFile }">파일</span>
        </div>
      </div>
      <!-- <p class="chat-header__description">6명의 작업자</p> -->
      <!-- <tooltip
      class="chat-header__fold"
      content="펼치기">
    </tooltip> -->
      <!-- <button slot="body" class="chat-header__fold">펼치기</button> -->
    </div>

    <vue2-scrollbar ref="chatListScrollbar">
      <ol class="chat-list">
        <li class="chat-item date">
          <p>{{ $dayjs().format('LL') }}</p>
        </li>
        <chat-item
          v-for="(chat, idx) of chatList"
          :key="idx"
          :beforeChat="idx === 0 ? null : chatList[idx - 1]"
          :afterChat="idx === chatList.length - 1 ? null : chatList[idx + 1]"
          :chat="textProcessor(chat)"
        ></chat-item>
      </ol>
    </vue2-scrollbar>

    <chat-input></chat-input>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { mapState } from 'vuex'

import ChatItem from './partials/ChatItem'
import ChatInput from './partials/ChatInput'
// import Popover from 'Popover'
import linkifyHtml from 'linkifyjs/html'
// import Tooltip from 'Tooltip'

export default {
  name: 'Chat',
  components: {
    ChatItem,
    ChatInput,
    // Popover,
    // Tooltip,
  },
  data() {
    return {
      showChat: true,
      showFile: false,

      roomTitle: '',
      participantsCount: 1,
    }
  },
  computed: {
    ...mapGetters(['chatList']),
    ...mapState({
      room: state => state.room,
      viewMode: state => state.oncall.view,
    }),
  },
  watch: {
    chatList: {
      handler() {
        this.$nextTick(() => {
          if (this.$refs['chatListScrollbar']) {
            this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
          }
        })
      },
      deep: true,
    },
    viewMode: {
      handler(newVal) {
        switch (newVal) {
          case 'stream':
            console.log('실시간 공유')
            break
          case 'drawing':
            console.log('협업 보드')
            this.chatList.push({
              text: '협업 보드를 사용합니다.',
              name: 'board',
              date: new Date(),
              uuid: null,
              type: 'system',
            })
            break
          case 'ar':
            this.chatList.push({
              text: 'AR 기능을 사용합니다.',
              name: 'ar',
              date: new Date(),
              uuid: null,
              type: 'system',
            })
            break
        }
      },
    },
  },
  methods: {
    toggleChatMenu() {
      console.log('채팅 clicked')
      if (!this.showChat) {
        this.showChat = true
        this.showFile = false
      }
    },
    toggleFileMenu() {
      console.log('파일 clicked')
      // if (!this.showFile) {
      //   this.showChat = false
      //   this.showFile = true
      // }
    },

    /**
     * process chat text
     */
    textProcessor(chat) {
      if (chat.text === undefined || chat.text === null) {
        return chat
      }

      if (chat.type === undefined || chat.type === null) {
        return chat
      }

      if (chat.type === 'system') {
        chat.text = this.sysHyliter(chat.text)
      } else {
        chat.text = this.urlHyliter(chat.text)
      }
      return chat
    },

    /**
     *
     * Change span tag to p tag for hylite text
     * if <span></span> tag changed for other tag. please modifiy this method
     */
    sysHyliter(chatText) {
      let replaced = chatText

      try {
        //check system type, and text length over 10
        if (chatText.indexOf('</span>') > 34) {
          replaced = chatText.replace('<span', '<p').replace('</span>', '</p>')
        }
      } catch (e) {
        console.log(e)
      }

      return replaced
    },

    urlHyliter(chatText) {
      let replaced = chatText

      try {
        replaced = linkifyHtml(chatText, {
          defaultProtocol: 'https',
          className: 'chat-url',
        })
      } catch (e) {
        console.log(e)
      }
      return replaced
    },
  },

  /* Lifecycles */
  mounted() {
    this.roomTitle = this.room.title ? this.room.title : ''

    //test message. delete if you need
    // this.chatList.push(
    //   {
    //     type: 'opponent',
    //     name: '참여자2',
    //     text:
    //       '안녕하세요 고객님. VIRNECT 고객센터입니다. 다음 링크에 접속 부탁드립니다. https://remote.virnect.com/ 감사합니다.',
    //     date: new Date(),
    //   },
    //   {
    //     type: 'opponent',
    //     name: '참여자2',
    //     text:
    //       '안녕하세요 고객님. VIRNECT 고객센터입니다. 다음 링크에 접속 부탁드립니다.https://remote.virnect.com/감사합니다.',
    //     date: new Date(),
    //   },
    //   {
    //     type: 'me',
    //     name: '고리3발 ENG 팀',
    //     text: '펭하',
    //     date: new Date(),
    //   },
    //   {
    //     type: 'opponent',
    //     name: '참여자2',
    //     text: '안녕하세요.',
    //     date: new Date(),
    //   },
    //   {
    //     type: 'opponent',
    //     name: '신규사업부 팀장',
    //     file: [
    //       {
    //         filename: 'Webex.png',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'opponent',
    //     name: '한전 제1 발전처장',
    //     file: [
    //       {
    //         filename: '3분기 전력수요량 자료.txt',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'opponent',
    //     name: '한전 제2 발전처장',
    //     file: [
    //       {
    //         filename: '명상에 좋은 음악.mp3',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'opponent',
    //     name: '고리3발 시설관리팀장',
    //     file: [
    //       {
    //         filename: '흡연실 사용규칙 준수.jpg',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'opponent',
    //     name: '고리3발 ENG 팀',
    //     file: [
    //       {
    //         filename: '가스터빈 도면.pdf',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'opponent',
    //     name: '신규사업부 팀장',
    //     file: [
    //       {
    //         filename: '도면2.jpg',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'me',
    //     name: '신규사업부 팀장',
    //     file: [
    //       {
    //         filename: 'Webex.png',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'me',
    //     name: '한전 제1 발전처장',
    //     file: [
    //       {
    //         filename: '3분기 전력수요량 자료.txt',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'me',
    //     name: '한전 제2 발전처장',
    //     file: [
    //       {
    //         filename: '명상에 좋은 음악.mp3',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'me',
    //     name: '고리3발 시설관리팀장',
    //     file: [
    //       {
    //         filename: '흡연실 사용규칙 준수.jpg',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'me',
    //     name: '고리3발 ENG 팀',
    //     file: [
    //       {
    //         filename: '가스터빈 도면.pdf',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'me',
    //     name: '신규사업부 팀장',
    //     file: [
    //       {
    //         filename: '도면2.jpg',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     type: 'me',
    //     name: '참여자2',
    //     text: 'ㅎㅇ',
    //     date: new Date(),
    //   },
    //   {
    //     text: '<span class="emphasize">테스트</span>님이 입장하셨습니다.',
    //     name: 'people',
    //     date: new Date(),
    //     uuid: null,
    //     type: 'system',
    //   },

    //   {
    //     type: 'me',
    //     name: '고리3발 ENG 팀',
    //     file: [
    //       {
    //         filename: '가스터빈 도면(최종).pdf',
    //         filesize: '10MB',
    //       },
    //     ],
    //     date: new Date(),
    //   },
    //   {
    //     text:
    //       '<span class="emphasize">이름이 엄청긴분이 오셨습니다.으어어어어어어어어</span>' +
    //       '님이 입장하셨습니다.',
    //     name: 'people',
    //     date: new Date(),
    //     uuid: null,
    //     type: 'system',
    //   },
    //   {
    //     text:
    //       '<span class="emphasize">테스트 팀장</span>' +
    //       '님이 전송을 취소했습니다.',
    //     name: 'cancel',
    //     date: new Date(),
    //     uuid: null,
    //     type: 'system',
    //   },
    // )
  },
}
</script>
