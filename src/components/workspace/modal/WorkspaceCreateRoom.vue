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
        <profile-image
          :image="imageURL"
          :deleteBtn="!!imageURL"
          @delete="imageRemove"
        ></profile-image>
        <input
          ref="inputImage"
          type="file"
          name="file"
          accept="image/gif,image/jpeg,image/png"
          style="display: none;"
          @change="uploadImage($event)"
        />
        <button
          class="btn normal createroom-info_regist-image"
          @click="imageUpload"
        >
          협업 프로필 등록
        </button>
        <input-row
          :title="'협업 이름'"
          :placeholder="'그룹이름을 입력해 주세요.'"
          :value.sync="name"
          validate="validName"
          validMessage="특수 문자는 협업 이름에서 제외시켜주세요."
          type="text"
          :count="20"
          required
          showCount
        ></input-row>
        <input-row
          :title="'협업 설명'"
          :placeholder="'그룹 설명을 입력해주세요.'"
          :value.sync="description"
          type="textarea"
          :count="50"
          showCount
        ></input-row>
        <input-row v-if="users.length > 0" :title="'선택한 멤버'" required>
          <profile-list
            v-if="selection.length > 0"
            :users="selection"
          ></profile-list>
          <p class="createroom-info__add-member" v-else>
            멤버를 추가해 주세요.
          </p>
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
          <p class="createroom-user__title">
            선택 가능한 멤버 리스트
            <span class="createroom-user__number">12</span>
          </p>
          <icon-button
            text="새로고침"
            :imgSrc="require('assets/image/back/mdpi_icn_renew.svg')"
          ></icon-button>
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
import IconButton from 'IconButton'

import mixinImage from 'mixins/uploadImage'
export default {
  name: 'WorkspaceCreateRoom',
  mixins: [mixinImage],
  components: {
    Modal,
    ProfileImage,
    InputRow,
    Scroller,
    Profile,
    WideCard,
    ProfileList,
    IconButton,
  },
  data() {
    return {
      selection: [],
      visibleFlag: false,
      name: '',
      description: '',
      image: null,
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
        this.name = '우경아의 원격 협업협업'
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
  src="assets/style/workspace/workspace-createroom.scss"
></style>
