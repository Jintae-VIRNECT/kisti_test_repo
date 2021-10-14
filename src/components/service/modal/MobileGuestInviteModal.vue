<template>
  <full-screen-modal
    :title="$t('service.guest_invite_url')"
    :visible="visible"
    @close="close"
  >
    <header class="mobile-guest-invite__header">
      <img
        slot="body"
        guide
        class="setting__tooltip--icon"
        src="~assets/image/ic_tool_tip_grey.svg"
      />
      <p>{{ $t('service.guest_invite_description') }}</p>
    </header>
    <div class="mobile-guest-invite">
      <label-input-button
        label="inviteUrl"
        :inputText.sync="inviteUrlInput"
        :iconImgPath="require('assets/image/call/ic_link.svg')"
        :title="$t('service.guest_invite_with_url')"
        :buttonText="$t('button.copy_url')"
        @buttonClick="copyUrl"
        isButtonPrimaryColor
        isInputReadOnly
        useButton
      >
      </label-input-button>

      <label-input-button
        label="emailAddress"
        :inputText.sync="emailAddressInput"
        :iconImgPath="require('assets/image/call/ic_mail.svg')"
        :title="$t('service.guest_invite_send_email')"
        :buttonText="$t('button.send_email')"
        :linkHref="linkHref"
        @buttonClick="send"
        :placeholder="
          $t('service.guest_invite_email_please_input_email_address')
        "
        :useButton="!isOnpremise"
      >
      </label-input-button>
    </div>
  </full-screen-modal>
</template>

<script>
import FullScreenModal from 'FullScreenModal'
import LabelInputButton from './partials/LabelInputButton'

export default {
  components: {
    FullScreenModal,
    LabelInputButton,
  },
  props: {
    visible: {
      type: Boolean,
      default: true,
    },
    beforeClose: {
      type: Function,
    },
    inviteUrl: {
      type: String,
    },
    emailAddress: {
      type: String,
    },
    linkHref: {
      type: String,
    },
  },
  data() {
    return {
      inviteUrlInput: this.inviteUrl,
      emailAddressInput: this.emailAddressInput,
    }
  },
  watch: {
    inviteUrl: {
      immediate: true,
      handler(newVal) {
        this.inviteUrlInput = newVal
      },
    },
    emailAddress: {
      immediate: true,
      handler(newVal) {
        this.emailAddressInput = newVal
      },
    },
    inviteUrlInput(newVal) {
      this.$emit('update:inviteUrl', newVal)
    },
    emailAddressInput(newVal) {
      this.$emit('update:emailAddress', newVal)
    },
  },
  methods: {
    close() {
      this.beforeClose()
    },
    copyUrl() {
      this.$emit('copyUrl')
    },
    send() {
      this.$emit('send')
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.mobile-guest-invite {
  padding: 2rem 1.6rem;
  .guest-invite-input__container:first-child {
    margin-bottom: 5.1rem;
  }
}

.mobile-guest-invite__header {
  display: flex;
  align-items: center;
  height: 6.2rem;
  padding: 0 1.6rem;
  background-color: $new_color_bg;
  border-top: solid 1px $new_color_sub_border;

  > img {
    width: 2.4rem;
    height: 2.4rem;
    margin-right: 0.6rem;
  }

  > p {
    @include fontLevel(75);
    color: $new_color_text_sub_description;
  }
}
</style>
