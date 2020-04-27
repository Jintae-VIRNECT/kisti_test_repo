<template>
  <section class="createroom-info">
    <profile-image
      :image="imageURL"
      :deleteBtn="!!imageURL"
      @delete="imageRemove"
      size="8.571em"
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
      :value.sync="title"
      :valid.sync="titleValid"
      validate="validName"
      :validMessage="titleValidMessage"
      type="text"
      :count="20"
      @focusOut="checkEmpty"
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
    <input-row v-if="!nouser" :title="'선택한 멤버'" required>
      <profile-list
        v-if="selection.length > 0"
        :users="selection"
      ></profile-list>
      <p class="createroom-info__add-member" v-else>
        멤버를 추가해 주세요.
      </p>
    </input-row>
    <button
      class="btn large createroom-info__button"
      :disabled="btnDisabled"
      @click="start"
    >
      시작하기
    </button>
  </section>
</template>

<script>
import ProfileImage from 'ProfileImage'
import InputRow from 'InputRow'
import ProfileList from 'ProfileList'

import mixinImage from 'mixins/uploadImage'
import { createRoom } from 'api/remote/room'

export default {
  name: 'ModalCreateRoomInfo',
  mixins: [mixinImage],
  components: {
    ProfileImage,
    InputRow,
    ProfileList,
  },
  data() {
    return {
      title: '',
      description: '',
      image: null,
      titleValid: false,
    }
  },
  props: {
    selection: {
      type: Array,
      default: () => {
        return []
      },
    },
    nouser: {
      type: Boolean,
      default: true,
    },
  },
  computed: {
    btnDisabled() {
      if (this.selection.length < 2) {
        return true
      } else if (this.titleValid) {
        return true
      } else {
        return false
      }
    },
    titleValidMessage() {
      if (this.title.length < 2) {
        return '협업 이름은 2자 이상 입력해주세요.'
      } else {
        return '특수 문자는 협업 이름에서 제외시켜주세요.'
      }
    },
  },
  methods: {
    reset() {
      this.title = `${this.account.name}의 원격협업`
      this.description = ''
    },
    async start() {
      console.log('원격 협업 생성')
      // TODO: 이미지는 어떻게 처리??
      const roomId = await createRoom({
        title: this.title,
        description: this.description,
        sessionId: '',
        profile: this.image,
        roomParticipants: this.selection,
      })
      console.log(roomId)
    },
    checkEmpty() {
      if (this.title === '') {
        this.title = `${this.account.name}의 원격협업`
      }
    },
  },

  /* Lifecycles */
  mounted() {
    this.reset()
  },
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-createroom-info.scss"
></style>
<style lang="scss">
// .otheruser-popover {
//   min-width: 150px;
//   background-color: $color_bg_sub;
//   border: solid 1px #3a3a3d;
//   transform: translateX(10px);
//   .popover--body {
//     padding: 15px;
//   }
// }
</style>
