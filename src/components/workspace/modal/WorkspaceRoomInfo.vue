<template>
  <modal
    title="원격협업 상세보기"
    :width="900"
    :height="790"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="modal-roominfo"
  >
    <div class="roominfo">
      <section class="roominfo-nav">
        <img class="roominfo-nav__image" :src="image" />
        <button
          class="roominfo-nav__menu"
          :class="{ active: tabview === 'group' }"
          @click="tabChange('group')"
        >
          협업 정보
        </button>
        <button
          class="roominfo-nav__menu"
          :class="{ active: tabview === 'user' }"
          @click="tabChange('user')"
        >
          참가자 정보
        </button>
      </section>
      <section class="roominfo-view" v-if="tabview === 'group'">
        <div>
          <p class="roominfo-view__title">
            협업 정보
          </p>
          <div class="roominfo-view__body">
            <input-row
              type="text"
              title="협업 이름"
              :showCount="true"
              :count="20"
              :value.sync="name"
              validMessage="특수 문자는 협업 이름에서 제외시켜주세요."
            ></input-row>
            <input-row
              type="textarea"
              title="협업 설명"
              :showCount="true"
              :count="50"
              :value.sync="description"
            ></input-row>
            <input-row type="buttons" title="이미지 등록">
              <div>
                <input
                  ref="inputImage"
                  type="file"
                  name="file"
                  accept="image/gif,image/jpeg,image/png"
                  style="display: none;"
                  @change="uploadImage($event)"
                />
                <button class="btn line imageinput regist" @click="imageUpload">
                  이미지 등록
                </button>
                <button class="btn line imageinput delete" @click="imageRemove">
                  이미지 삭제
                </button>
              </div>
            </input-row>
          </div>
          <div class="roominfo-view__footer">
            <div class="roominfo-view__data">
              <span class="data-title">협업 진행일</span>
              <span class="data-value">2020.03.05</span>
            </div>
            <div class="roominfo-view__data">
              <span class="data-title">시작 시간</span>
              <span class="data-value">15:20:25</span>
            </div>
            <div class="roominfo-view__button">
              <button class="btn" :disabled="!canSave">저장하기</button>
            </div>
          </div>
        </div>
      </section>

      <section class="roominfo-view" v-else>
        <p class="roominfo-view__title">
          참가자 정보
        </p>
        <div class="roominfo-view__body">
          <scroller>
            <wide-card v-for="user of users" :key="user.id">
              <div class="roominfo-userinfo">
                <profile
                  :image="user.image"
                  :mainText="user.mainText"
                  :subText="user.subText"
                  :role="user.role"
                ></profile>

                <img
                  class="userinfo__image"
                  :src="require('assets/image/ic_hololens.svg')"
                />
                <button class="btn line userinfo__button">내보내기</button>
              </div>
            </wide-card>
          </scroller>
        </div>
      </section>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import InputRow from 'InputRow'
import WideCard from 'WideCard'
import Scroller from 'Scroller'
import Profile from 'Profile'

import mixinImage from 'mixins/uploadImage'
export default {
  name: 'WorkspaceRoomInfo',
  mixins: [mixinImage],
  components: {
    Modal,
    InputRow,
    WideCard,
    Profile,
    Scroller,
  },
  data() {
    return {
      tabview: 'group',
      groupDefault: require('assets/image/img_default_group.svg'),
      selection: [],
      visibleFlag: false,
      name: '',
      description: '',
      image: null,
      users: [
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
      ],
    }
  },
  computed: {
    canSave() {
      if (this.name !== this.room.name) {
        return true
      }
      if (this.description !== this.room.description) {
        return true
      }
      if (this.image !== this.room.image) {
        return true
      }
      return false
    },
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    room: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },
    imageURL(url) {
      if (url === '') {
        // 기본 이미지
        this.image = this.groupDefault
      } else {
        this.image = url
      }
    },
    room: {
      handler(room) {
        this.name = room.name
        this.description = room.description
        this.image = room.image
      },
      deep: true,
    },
  },
  methods: {
    tabChange(view) {
      this.tabview = view
    },
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
  mounted() {
    this.name = this.room.name
    this.description = this.room.description
    this.image = this.room.image
  },
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-roominfo.scss"
></style>

<style lang="scss">
.modal.modal-roominfo {
  .modal--body {
    padding: 0;
  }

  .modal--inner {
    display: flex;
    flex-direction: column;
  }
  .modal--header {
    flex: 0;
  }

  .modal--body {
    flex: 1;
  }
}
</style>
