<template>
  <modal
    title="원격 협업 생성하기"
    :width="1098"
    :height="850"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
  >
    <div class="createroom">
      <section class="createroom-info">
        <profile-image :btnText="'등록'"></profile-image>
        <input-row
          :title="'협업 이름'"
          :placeholder="'그룹이름을 입력해 주세요. (최대20자)'"
          :value.sync="name"
          type="text"
          :count="20"
        ></input-row>
        <input-row
          :title="'협업 설명'"
          :placeholder="'그룹 설명을 입력해주세요. (최대50자)'"
          :value.sync="description"
          type="textarea"
          :count="50"
        ></input-row>
        <input-row v-if="users.length > 0" :title="'선택한 멤버'">
          <profile-list :users="selection"></profile-list>
          <!-- <div style="color: #fff;">HAHA</div> -->
        </input-row>
        <button
          class="btn large createroom-info__button"
          :disabled="selection.length < 2"
        >
          시작하기
        </button>
      </section>
      <section class="createroom-user">
        <div class="createroom-user__header">
          <p class="createroom-user__title">선택 가능한 멤버 리스트</p>
          <button class="createroom-user__button">새로고침</button>
        </div>
        <div class="createroom-user__body">
          <scroller v-if="users.length > 0">
            <div>
              <wide-card
                v-for="(user, idx) of users"
                :key="idx"
                :customClass="{
                  choice: true,
                  selected:
                    selection.findIndex(select => select.id === user.id) > -1,
                }"
                @click.native="selectUser(user)"
              >
                <profile
                  :image="user.image"
                  :imageAlt="user.imageAlt"
                  :mainText="user.mainText"
                  :subText="user.subText"
                  :status="user.status"
                  :role="user.role"
                ></profile
              ></wide-card>
            </div>
          </scroller>
          <div v-else class="createroom-user__empty">
            <p class="createroom-user__empty-title">
              협업 가능한 멤버가 없습니다.
            </p>
            <p class="createroom-user__empty-description">
              협업 멤버를 추가해주세요.
            </p>
          </div>
        </div>
      </section>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import ProfileImage from 'ProfileImage'
import InputRow from 'InputRow'
import Scroller from 'Scroller'
import Profile from 'Profile'
import WideCard from 'WideCard'
import ProfileList from 'ProfileList'
export default {
  name: 'WorkspaceCreateRoom',
  components: {
    Modal,
    ProfileImage,
    InputRow,
    Scroller,
    Profile,
    WideCard,
    ProfileList,
  },
  data() {
    return {
      selection: [],
      visibleFlag: false,
      name: '',
      description: '',
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    users: {
      type: Array,
      default: () => {
        return [
          {
            id: 1,
            image: require('assets/image/홍길동.png'),
            imageAlt: '버넥트 리모트 01',
            mainText: '버넥트 리모트01',
            subText: 'example@example.com',
            status: 'online',
            role: 'Master',
          },
          {
            id: 2,
            image: require('assets/image/img_user_profile.svg'),
            imageAlt: '버넥트 리모트 02',
            mainText: '버넥트 리모트03',
            subText: 'example2@example.com',
            status: 'online',
            role: 'Master',
          },
          {
            id: 3,
            image: require('assets/image/img_user_profile.svg'),
            imageAlt: '버넥트 리모트 03',
            mainText: '버넥트 리모트03',
            subText: 'example3@example.com',
            status: 'online',
            role: 'Master',
          },
          {
            id: 4,
            image: require('assets/image/img_user_profile.svg'),
            imageAlt: '버넥트 리모트 01',
            mainText: '버넥트 리모트01',
            subText: 'example@example.com',
            status: 'online',
            role: 'Master',
          },
          {
            id: 5,
            image: require('assets/image/img_user_profile.svg'),
            imageAlt: '버넥트 리모트 03',
            mainText: '버넥트 리모트03',
            subText: 'example3@example.com',
            status: 'online',
            role: 'Master',
          },
          {
            id: 6,
            image: require('assets/image/img_user_profile.svg'),
            imageAlt: '버넥트 리모트 01',
            mainText: '버넥트 리모트01',
            subText: 'example@example.com',
            status: 'online',
            role: 'Master',
          },
          {
            id: 7,
            image: require('assets/image/img_user_profile.svg'),
            imageAlt: '버넥트 리모트 03',
            mainText: '버넥트 리모트03',
            subText: 'example3@example.com',
            status: 'online',
            role: 'Master',
          },
          {
            id: 8,
            image: require('assets/image/img_user_profile.svg'),
            imageAlt: '버넥트 리모트 01',
            mainText: '버넥트 리모트01',
            subText: 'example@example.com',
            status: 'online',
            role: 'Master',
          },
        ]
      },
    },
  },
  watch: {
    visible(flag) {
      if (flag) {
        this.reset()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    reset() {
      this.selection = []
      this.name = ''
      this.description = ''
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    selectUser(user) {
      const idx = this.selection.findIndex(select => user.id === select.id)
      if (idx < 0) {
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-modal.scss"
></style>
