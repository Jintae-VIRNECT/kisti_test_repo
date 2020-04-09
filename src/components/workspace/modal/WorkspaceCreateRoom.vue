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
        <profile-image></profile-image>
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
        <div>프로필</div>
        <div>협업 이름</div>
        <div>협업 설명</div>
        <div>선택한 멤버</div>
        <button class="btn large">시작하기</button>
      </section>
      <section class="createroom-user">
        <div class="createroom-user__header">
          <p class="createroom-user__title">선택 가능한 멤버 리스트</p>
          <button class="createroom-user__button">새로고침</button>
        </div>
        <div class="createroom-user__body">
          <scroller>
            <div>
              <wide-card>
                <profile
                  :image="require('assets/image/img_user_profile.svg')"
                  imageAlt="버넥트 리모트01"
                  mainText="버넥트 리모트01"
                  subText="example@example.com"
                  status="online"
                  role="Master"
                ></profile
              ></wide-card>
            </div>
          </scroller>
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
export default {
  name: 'WorkspaceCreateRoom',
  components: {
    Modal,
    ProfileImage,
    InputRow,
    Scroller,
    Profile,
    WideCard,
  },
  data() {
    return {
      users: [
        {
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트 01',
          mainText: '버넥트 리모트01',
          subText: 'example@example.com',
          status: 'online',
          role: 'Master',
        },
      ],
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
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace/workspace-modal.scss"></style>
