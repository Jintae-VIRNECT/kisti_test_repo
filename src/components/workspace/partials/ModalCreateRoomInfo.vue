<template>
  <section class="createroom-info">
    <profile-image
      :image="imageURL"
      :deleteBtn="!!imageURL"
      @delete="imageRemove"
      size="6.143em"
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
      :class="{ disabled: btnDisabled }"
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

import { createRoom } from 'api/workspace/room'
import imageMixin from 'mixins/uploadImage'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'ModalCreateRoomInfo',
  mixins: [imageMixin, confirmMixin],
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
    shortName() {
      if (this.account.nickname.length > 10) {
        return this.account.nickname.substr(0, 10)
      } else {
        return this.account.nickname
      }
    },
    btnDisabled() {
      if (this.selection.length < 1) {
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
      this.title = `${this.shortName}'s Room`
      this.description = ''
    },
    async start() {
      if (this.btnDisabled) {
        this.confirmDefault(
          '선택한 멤버가 없습니다.\n1명 이상의 협업 멤버를 선택해 주세요',
        )
        return
      }
      const selectedUser = []
      for (let select of this.selection) {
        selectedUser.push(select.uuid)
      }
      selectedUser.push(this.account.uuid)
      const roomId = await createRoom({
        file: this.imageFile,
        title: this.title,
        description: this.description,
        leaderId: this.account.uuid,
        participants: selectedUser,
        workspaceId: this.workspace.uuid,
      })
      console.log(roomId)
      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        // this.$router.push({ name: 'service' })
      })
    },
    checkEmpty() {
      if (this.title === '') {
        this.title = `${this.shortName}'s Room`
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
