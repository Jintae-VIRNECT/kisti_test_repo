<template>
  <div>
    <modal
      :visible.sync="visiblePcFlag"
      :showClose="true"
      width="39.4286rem"
      height="30rem"
      :beforeClose="beforeClose"
      class="service-guest-invite"
      :title="$t('service.guest_invite_url')"
    >
      <div class="guest-invite">
        <label-input-button
          label="inviteUrl"
          :inputText.sync="inviteUrl"
          :iconImgPath="require('assets/image/call/ic_link.svg')"
          :title="$t('service.guest_invite_with_url')"
          :buttonText="$t('button.copy_url')"
          @buttonClick="copyUrl"
          isButtonPrimaryColor
          isInputReadOnly
          useButton
        >
        </label-input-button>

        <hr class="guest-invite--divider" />

        <label-input-button
          label="emailAddress"
          :inputText.sync="emailAddress"
          :iconImgPath="require('assets/image/call/ic_mail.svg')"
          :title="$t('service.guest_invite_send_email')"
          :buttonText="$t('button.send_email')"
          :linkHref="mailToLink('mailto')"
          @buttonClick="send"
          :placeholder="
            $t('service.guest_invite_email_please_input_email_address')
          "
          :useButton="!isOnpremise"
        >
        </label-input-button>
      </div>
      <template v-slot:footer>
        <footer class="guest-invite__footer">
          <img
            slot="body"
            guide
            class="setting__tooltip--icon"
            src="~assets/image/ic_tool_tip_grey.svg"
          />
          <p>{{ $t('service.guest_invite_description') }}</p>
        </footer>
      </template>
    </modal>
    <mobile-guest-invite-modal
      :visible.sync="visibleMobileFlag"
      :beforeClose="beforeClose"
      :inviteUrl.sync="inviteUrl"
      :emailAddress.sync="emailAddress"
      :linkHref="mailToLink('mailto')"
      @copyUrl="copyUrl"
      @send="send"
    >
    </mobile-guest-invite-modal>
  </div>
</template>

<script>
import Modal from 'Modal'

import toastMixin from 'mixins/toast'
import responsiveModalVisibleMixin from 'mixins/responsiveModalVisible'

import { sendEmail } from 'api/http/mail'

import { mapGetters } from 'vuex'
import MobileGuestInviteModal from './MobileGuestInviteModal'
import LabelInputButton from './partials/LabelInputButton'

export default {
  name: 'GuestInviteModal',
  mixins: [toastMixin, responsiveModalVisibleMixin],
  components: {
    Modal,
    MobileGuestInviteModal,
    LabelInputButton,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      visibleFlag: false,

      inviteUrl: '',
      emailAddress: '',
      emailSender: 'no-reply@virnect.com',
      emailSubject: 'VIRNECT Remote_Access information',
    }
  },

  computed: {
    ...mapGetters(['roomInfo']),
  },

  watch: {
    visible(flag) {
      this.visibleFlag = flag
      if (flag) {
        this.inviteUrl = this.getInviteUrl('web')
      }
      this.setVisiblePcOrMobileFlag(flag)
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
    getInviteUrl(device) {
      const route = device === 'qr' ? '/qr?' : '/connectioninfo?'

      return (
        `${window.location.origin}` +
        route +
        'workspaceId=' +
        `${this.workspace.uuid}` +
        '&sessionId=' +
        `${this.roomInfo.sessionId}`
      )
    },
    copyUrl() {
      navigator.clipboard.writeText(this.getInviteUrl('web')).then(
        () => {
          this.toastDefault(this.$t('service.guest_invite_url_copied'))
        },
        () => {
          console.error('copy clipboard failed')
        },
      )
    },
    async send() {
      try {
        if (this.emailAddress.length === 0) return

        await sendEmail({
          receivers: [this.emailAddress],
          sender: this.emailSender,
          subject: this.emailSubject,
          html: this.mailToLink('html'),
        })

        this.toastDefault(
          this.$t('service.guest_invite_email_transport_succeed'),
        )
      } catch (error) {
        console.error(error)
        this.toastDefault(
          this.$t('service.guest_invite_email_transport_failed'),
        )
      }
    },
    mailToLink(target) {
      if (target === 'mailto') {
        return `mailto:${this.emailAddress}?subject=${
          this.emailSubject
        }&body=${this.getMailBody(target)}`
      } else {
        return this.getMailBody(target)
      }
    },
    getMailBody(target) {
      const newLine = target === 'mailto' ? '%0D%0A' : '<br>'

      const webUrl = this.getInviteUrl('web')
      const qrUrl = this.getInviteUrl('qr')

      const msg =
        `[${this.emailSubject}] ${newLine}` +
        `(Access URL) ${newLine} ${webUrl} ${newLine} ${newLine}` +
        `(QR Code access) ${newLine} ${qrUrl} ${newLine}`
      return msg
    },
  },

  async mounted() {},
}
</script>
<style lang="scss">
@import '~assets/style/vars';
.modal.service-guest-invite .modal--inner {
  > .modal--footer {
    border: none;
  }
}
.guest-invite__footer {
  display: flex;
  align-items: center;
  height: 1.2143rem;
  & > p {
    margin-left: 0.3571rem;
    color: #808991;
    font-weight: 500;
    font-size: 0.9286rem;
  }
}

.guest-invite--divider {
  width: 100%;
  margin-top: 2.0714rem;
  margin-bottom: 1.9286rem;
  border-top: solid #272729 0.1429rem;
  border-right: none;
  border-bottom: none;
  border-left: none;
}
</style>
