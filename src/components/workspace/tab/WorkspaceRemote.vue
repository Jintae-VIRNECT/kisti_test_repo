<template>
  <tab-view
    title="진행중인 원격협업"
    description="진행중인 협업참여 요청 대기 목록을 보여줍니다."
    placeholder="원격 협업 검색"
    emptyTitle="원격 협업 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :listCount="0"
    :empty="roomList.length === 0"
    customClass="remote"
    :showRefreshButton="true"
  >
    <remote-card
      v-for="room of roomList"
      :key="room.id"
      :room="room"
    ></remote-card>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import RemoteCard from 'RemoteCard'
export default {
  name: 'WorkspaceRemote',
  components: { TabView, RemoteCard },
  data() {
    return {
      roomList: [
        {
          id: 1,
          image: require('assets/image/img_default_group.svg'),
          name: '리모트 개발팀',
          description:
            '가나다라마바사아자차카타파하가나다가나다라마바사아자차카타파하가나가가나다라마바사아자차카타파하가나다가나다라마바사아자차카타파하가나가',
          readerId: 11,
          all: 7,
          connectedMembers: [
            {
              ids: 11,
              name: '나는리더',
              image: require('assets/image/img_welcome_user.svg'),
            },
            {
              ids: 12,
              name: '멤버닷',
              image: require('assets/image/img_welcome_user.svg'),
            },
            {
              ids: 13,
              name: '멤버닷',
              image: require('assets/image/img_welcome_user.svg'),
            },
            {
              ids: 14,
              name: '멤버닷',
              image: require('assets/image/img_welcome_user.svg'),
            },
            {
              ids: 13,
              name: '멤버닷',
              image: require('assets/image/img_welcome_user.svg'),
            },
            {
              ids: 14,
              name: '멤버닷',
              image: require('assets/image/img_welcome_user.svg'),
            },
          ],
        },
        {
          id: 2,
          image: require('assets/image/img_default_group.svg'),
          name: '협업1',
          description: '협업 설명 블라블라블라블라',
          readerId: 13,
          all: 9,
          connectedMembers: [
            {
              ids: 11,
              name: '멤버닷',
              image: require('assets/image/img_welcome_user.svg'),
            },
            {
              ids: 12,
              name: '멤버닷',
              image: require('assets/image/img_welcome_user.svg'),
            },
            {
              ids: 13,
              name: '나는쫄보',
              image: require('assets/image/img_welcome_user.svg'),
            },
            {
              ids: 14,
              name: '멤버닷',
              image: require('assets/image/img_welcome_user.svg'),
            },
          ],
        },
      ],
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
