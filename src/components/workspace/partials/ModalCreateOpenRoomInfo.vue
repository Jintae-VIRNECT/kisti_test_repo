<template>
  <section class="openroom-info">
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
      @change="uploadImage($event)"
    />
    <button class="btn normal openroom-info_regist-image" @click="imageUpload">
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
    <button
      class="btn large openroom-info__button"
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

import imageMixin from 'mixins/uploadImage'
import confirmMixin from 'mixins/confirm'
import toastMixin from 'mixins/toast'

export default {
  name: 'ModalCreateRoomInfo',
  mixins: [imageMixin, confirmMixin, toastMixin],
  components: {
    ProfileImage,
    InputRow,
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
      return !!this.titleValid
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
    },
    async startRemote() {
      if (this.btnDisabled) {
        this.confirmDefault(this.titleValidMessage)
        return
      }

      this.$emit('startRemote', {
        title: this.title,
        description: this.description,
        imageFile: this.imageFile,
        open: true,
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
  src="assets/style/workspace/workspace-openroom-info.scss"
></style>
