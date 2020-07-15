<template>
  <main class="appmenu">
    <section class="appmenu--section">
      <h1 class="appmenu--title">{{ $t('mobile.policy') }}</h1>
      <ul class="appmenu--list">
        <li class="appmenu--list__item">
          <a
            href="/policy/terms"
            data-wml-style=".overlay"
            :data-wml-speech-command="$t('mobile.use_terms')"
            class="appmenu--list__link"
            >{{ $t('mobile.use_terms') }}</a
          >
          <!-- <a href="/policy/terms" class="appmenu--list__link">이용약관</a> -->
        </li>
        <li class="appmenu--list__item">
          <a
            href="/policy/privacy"
            data-wml-style=".overlay"
            :data-wml-speech-command="$t('mobile.privacy_policy')"
            class="appmenu--list__link"
            >{{ $t('mobile.privacy_policy') }}</a
          >
        </li>
        <li class="appmenu--list__item">
          <a
            href="/OSS"
            data-wml-style=".overlay"
            :data-wml-speech-command="$t('mobile.opensource_license')"
            class="appmenu--list__link"
            >{{ $t('mobile.opensource_license') }}</a
          >
        </li>
      </ul>
    </section>

    <section class="appmenu--section intro">
      <h1 class="appmenu--title">{{ $t('mobile.company') }}</h1>
      <dl class="intro--list">
        <dt class="intro--list__label">{{ $t('mobile.company_name') }}</dt>
        <dd class="intro--list__text">{{ $t('mobile.virnect') }}</dd>
        <dt class="intro--list__label">{{ $t('mobile.ceo') }}</dt>
        <dd class="intro--list__text">{{ $t('mobile.ceo_name') }}</dd>
        <dt class="intro--list__label">{{ $t('mobile.address') }}</dt>
        <dd class="intro--list__text">
          <p>{{ $t('mobile.seoul_branch') }}</p>
          {{ $t('mobile.seoul_address') }}<br />
          <p>{{ $t('mobile.research_center') }}</p>
          {{ $t('mobile.research_address') }}
        </dd>
        <dt class="intro--list__label">{{ $t('mobile.homepage') }}</dt>
        <dd class="intro--list__text">https://virnect.com/</dd>
        <dt class="intro--list__label">
          {{ $t('mobile.business_license_number') }}
        </dt>
        <dd class="intro--list__text">194-81-00551​</dd>
      </dl>
    </section>
  </main>
</template>

<script>
import WearML from 'plugins/wearML/index'
import langMixin from 'mixins/language'
/**
 * https://developer.realwear.com/HTML/api
 */

export default {
  mixins: [langMixin],
  created() {
    if (this.$route.query.lang) {
      this.$i18n.locale = this.$route.query.lang
    } else {
      this.mx_changeLang(this.$route.query.lang)
    }
  },
  mounted() {
    WearML.getCommands()
  },
}
</script>

<style lang="scss" src="assets/style/mobileApp/mobileApp-help.scss"></style>
