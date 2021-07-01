<template>
  <section class="createroom-info">
    <profile-image
      :image.sync="imageURL"
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
      @change="uploadImage($event, (isProfile = true))"
    />
    <button
      class="btn normal createroom-info_regist-image"
      @click="imageUpload"
    >
      {{ $t('workspace.create_remote_profile_regist') }}
    </button>
    <input-row
      :title="$t('workspace.remote_name')"
      :placeholder="$t('workspace.create_remote_name_input')"
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
      :title="$t('workspace.remote_description')"
      :placeholder="$t('workspace.create_remote_description_input')"
      :value.sync="description"
      type="textarea"
      :count="50"
      showCount
    ></input-row>
    <input-row :title="$t('workspace.create_remote_selected')" required>
      <profile-list
        v-if="selection.length > 0"
        :users="selection"
      ></profile-list>
      <p class="createroom-info__add-member" v-else>
        {{ $t('workspace.create_remote_add_member') }}
      </p>
    </input-row>
    <button
      class="btn large createroom-info__button"
      :class="{ disabled: btnDisabled, 'btn-loading': btnLoading }"
      @click="startRemote"
    >
      {{ $t('button.start') }}
    </button>
  </section>
</template>

<script>
import ProfileImage from 'ProfileImage'
import InputRow from 'InputRow'
import ProfileList from 'ProfileList'

import imageMixin from 'mixins/uploadImage'
import confirmMixin from 'mixins/confirm'
import toastMixin from 'mixins/toast'

export default {
  name: 'ModalCreateRoomInfo',
  mixins: [imageMixin, confirmMixin, toastMixin],
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
    roomInfo: {
      type: Object,
      default: () => {
        return {}
      },
    },
    btnLoading: {
      type: Boolean,
      defalut: false,
    },
  },
  watch: {
    roomInfo: {
      deep: true,
      handler: function(info) {
        this.title = info.title
        this.description = info.description
        this.imageURL = info.profile
      },
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
      return this.selection.length < 1 || this.titleValid
    },
    titleValidMessage() {
      if (this.title.length < 2) {
        return this.$t('workspace.remote_name_valid1')
      } else {
        return this.$t('workspace.remote_name_valid2')
      }
    },
  },
  methods: {
    reset() {
      this.title = `${this.shortName}'s Room`
      this.description = ''
      if (this.roomInfo && this.roomInfo.title) {
        this.title = this.roomInfo.title
        this.description = this.roomInfo.description
        this.imageURL = this.roomInfo.profile
      }
    },
    async startRemote() {
      if (this.btnDisabled) {
        if (this.selection.length < 1) {
          this.confirmDefault(this.$t('workspace.create_remote_selected_empty'))
        } else if (this.title.length < 2) {
          this.confirmDefault(this.$t('workspace.remote_name_valid1'))
        } else {
          this.confirmDefault(this.$t('workspace.remote_name_valid2'))
        }
        return
      }

      this.$emit('startRemote', {
        title: this.title,
        description: this.description,
        imageFile: this.imageFile,
        imageUrl: this.imageURL,
        open: false,
      })
    },
    checkEmpty() {
      if (this.title.trim() === '') {
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
