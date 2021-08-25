<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    width="39.4286rem"
    height="30rem"
    :beforeClose="beforeClose"
    class="service-guest-invite"
    :title="$t('service.guest_invite_url')"
  >
    <div class="guest-invite">
      <div class="guest-invite__header">
        <img src="~assets/image/call/ic_link.svg" alt="link" />
        <p>{{ $t('URL 초대하기') }}</p>
      </div>

      <figure class="guest-invite__input-url border-bottom">
        <input
          v-model="inviteUrl"
          class="input"
          ref="inviteUrl"
          aria-label="inviteUrl"
          readonly
        />
        <button class="btn copy-url" @click="copyUrl">
          {{ $t('button.copy_url') }}
        </button>
      </figure>

      <hr class="guest-invite--divider" />

      <div class="guest-invite__header">
        <img src="~assets/image/call/ic_mail.svg" alt="mail" />
        <p>{{ $t('service.guest_invite_send_email') }}</p>
      </div>
      <figure class="guest-invite__input-url">
        <input
          v-model="emailAddress"
          class="input"
          ref="emailAddress"
          aria-label="emailAddress"
          :placeholder="$t('메일 주소를 입력해주세요.')"
        />

        <a
          v-if="isOnpremise"
          class="btn send-email"
          :href="mailToLink('mailto')"
          target="_blank"
        >
          {{ $t('button.send_email') }}</a
        >
        <button v-else class="btn send-email" @click="processMail">
          {{ $t('button.send_email') }}
        </button>
      </figure>
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
</template>

<script>
import Modal from 'Modal'

import toastMixin from 'mixins/toast'

import { getGuestInviteUrl } from 'api/http/guest'
import { sendEmail } from 'api/http/mail'

import { mapGetters } from 'vuex'

export default {
  name: 'GuestInviteModal',
  mixins: [toastMixin],
  components: {
    Modal,
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
        this.getInviteUrl()
      }
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
    async getInviteUrl() {
      try {
        const result = await getGuestInviteUrl({
          sessionId: this.roomInfo.sessionId,
          workspaceId: this.workspace.uuid,
        })
        this.inviteUrl = result.url
      } catch (error) {
        console.error(error)
        this.beforeClose()
      }
    },
    copyUrl() {
      navigator.clipboard.writeText(this.inviteUrl).then(
        () => {
          this.toastDefault(this.$t('service.guest_invite_url_copied'))
        },
        () => {
          console.error('copy clipboard failed')
        },
      )
    },
    async processMail() {
      try {
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

      const msg =
        `[${this.emailSubject}] ${newLine}` +
        `(Access URL) ${newLine} ${this.inviteUrl} ${newLine} ${newLine}` +
        `(QR Code access) ${newLine} ${'qr로그인 주소'} ${newLine}`
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

.guest-invite__input-url {
  display: flex;
  align-items: center;
  width: 35.1429rem;
  height: 3.4286rem;
  padding: 0.9286rem 0rem 0.9286rem 1.1429rem;
  color: #fff;
  font-size: 1em;
  background-color: #1a1a1b;
  border: solid 1px #303030;
  border-radius: 3px;
  &::placeholder {
    color: rgba(#979fb0, 0.7);
  }

  & > input {
    width: 26.2143rem;
    height: 1.4286rem;
    overflow-y: hidden;
    color: #fff;
    font-weight: normal;
    font-size: 1rem;
    letter-spacing: 0px;
    background-color: #1a1a1b;
    border: none;
    outline: none;
    resize: none;
  }
  &::placeholder {
    color: rgba(#979fb0, 0.7);
  }
  &:hover {
    border: solid 1px #585858;
  }
  &:focus-within {
    border: solid 1px $color_primary;
  }

  .btn {
    width: 7rem;
    height: 2.5714rem;
    padding: 0.6429rem 1.6429rem;
    color: #ffffff;
    font-weight: 500;
    font-size: 0.9286rem;
    border-radius: 2px;

    &.send-email {
      background: #616872;
      border-radius: 2px;

      &:hover {
        background: #494c5b;
      }

      &:active {
        background-color: $color_darkgray_400;
      }
    }
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

.guest-invite__header {
  display: flex;
  margin-bottom: 0.7857rem;
  > p {
    margin-left: 0.5714rem;
  }
}
</style>
