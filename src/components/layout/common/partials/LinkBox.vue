<template>
  <div class="hover-box">
    <p>{{ $t('header.shortcut') }}</p>
    <ul>
      <template v-for="(site, idx) of link">
        <li :key="idx" :class="idx" v-if="isOnpremise(idx)">
          <a target="_blank" :href="site.href">{{ site.title }}</a>
        </li>
      </template>
    </ul>
  </div>
</template>

<script>
export default {
  props: {
    urls: Object,
    env: String,
    regex: RegExp,
  },
  data() {
    return {
      currentUrl: '',
    }
  },
  computed: {
    link() {
      return {
        remote: {
          title: 'Remote',
          href: this.urls.remote,
        },
        workstation: {
          title: 'Workstation',
          href: this.urls.workstation,
        },
        login: {
          title: this.$t('header.loginCenter'),
          href: `${this.urls.console}/qr_login_center?continue=${this.currentUrl}`,
        },
        payment: {
          title: this.$t('header.payment'),
          href: this.urls.pay,
        },
        download: {
          title: this.$t('header.downloadCenter'),
          href: this.urls.download,
        },
        support: {
          title: this.$t('header.supportCenter'),
          href: this.urls.support,
        },
        learning: {
          title: this.$t('header.learningCenter'),
          href: this.urls.learning,
        },
        community: {
          title: this.$t('header.community'),
          href: 'https://inquiry-and-feedback.hellonext.co/',
        },
      }
    },
  },
  methods: {
    isOnpremise(key) {
      if (this.env === 'onpremise') {
        return this.regex.test(key)
      } else return true
    },
  },
  mounted() {
    this.currentUrl = location.href
  },
}
</script>
